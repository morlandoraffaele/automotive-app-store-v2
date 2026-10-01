import {
  DEFAULT_SETTINGS,
  FAIL_ONCE_APP_IDS,
  INITIAL_STORE_UPDATE,
  MOCK_APPS,
  MOCK_CHANNELS,
  MOCK_INSTALLED,
  MOCK_SELECTED_CHANNELS,
} from './mock-data'
import { getReleaseForChannel, getSelectedChannelId, getUpdatableAppIds } from './selectors'
import type { ChannelId, StoreRepository, StoreSettings, StoreSnapshot, TaskKind } from './types'

const CATALOG_DELAY_MS = 900
const TICK_MS = 250
const INSTALL_PHASE_MS = 1400

/**
 * In-memory repository that simulates catalog loading, downloads, and
 * installs with timers. Swap for a real implementation of StoreRepository.
 */
export class MockStoreRepository implements StoreRepository {
  private state: StoreSnapshot
  private listeners = new Set<() => void>()
  private timers = new Map<string, ReturnType<typeof setTimeout>>()
  private failOnce = new Set(FAIL_ONCE_APP_IDS)
  private catalogRequested = false

  constructor() {
    this.state = {
      catalog: { status: 'loading', apps: [] },
      channels: MOCK_CHANNELS,
      installed: { ...MOCK_INSTALLED },
      selectedChannel: { ...MOCK_SELECTED_CHANNELS },
      tasks: {},
      settings: DEFAULT_SETTINGS,
      storeUpdate: INITIAL_STORE_UPDATE,
    }
  }

  getSnapshot = () => this.state

  subscribe = (listener: () => void) => {
    this.listeners.add(listener)
    if (!this.catalogRequested) {
      this.catalogRequested = true
      this.reloadCatalog()
    }
    return () => {
      this.listeners.delete(listener)
    }
  }

  private setState(updater: (s: StoreSnapshot) => StoreSnapshot) {
    this.state = updater(this.state)
    this.listeners.forEach((l) => l())
  }

  private clearTimer(key: string) {
    const timer = this.timers.get(key)
    if (timer) {
      clearTimeout(timer)
      clearInterval(timer)
      this.timers.delete(key)
    }
  }

  reloadCatalog = () => {
    this.clearTimer('catalog')
    this.setState((s) => ({ ...s, catalog: { status: 'loading', apps: s.catalog.apps } }))
    const mode = this.state.settings.simulation
    if (mode === 'loading') return
    this.timers.set(
      'catalog',
      setTimeout(() => {
        this.setState((s) => ({
          ...s,
          catalog:
            mode === 'error'
              ? { status: 'error', apps: [] }
              : { status: 'ready', apps: mode === 'empty' ? [] : MOCK_APPS },
        }))
      }, CATALOG_DELAY_MS),
    )
  }

  private startTask(appId: string, kind: TaskKind) {
    const app = this.state.catalog.apps.find((a) => a.id === appId)
    if (!app) return
    this.clearTimer(appId)
    const release = getReleaseForChannel(app, getSelectedChannelId(this.state, appId))

    this.setState((s) => ({
      ...s,
      tasks: { ...s.tasks, [appId]: { kind, phase: 'downloading', progress: 0, targetVersion: release.version } },
    }))

    const interval = setInterval(() => {
      const task = this.state.tasks[appId]
      if (!task) return this.clearTimer(appId)
      const next = Math.min(100, task.progress + 4 + Math.round(Math.random() * 6))

      if (this.failOnce.has(appId) && next >= 55) {
        this.failOnce.delete(appId)
        this.clearTimer(appId)
        this.setState((s) => ({ ...s, tasks: { ...s.tasks, [appId]: { ...task, phase: 'failed', progress: next } } }))
        return
      }

      if (next >= 100) {
        this.clearTimer(appId)
        this.setState((s) => ({ ...s, tasks: { ...s.tasks, [appId]: { ...task, phase: 'installing', progress: 100 } } }))
        this.timers.set(
          appId,
          setTimeout(() => {
            this.timers.delete(appId)
            this.setState((s) => {
              const { [appId]: _done, ...tasks } = s.tasks
              return {
                ...s,
                tasks,
                installed: { ...s.installed, [appId]: { version: task.targetVersion, justCompleted: true } },
              }
            })
          }, INSTALL_PHASE_MS),
        )
        return
      }

      this.setState((s) => ({ ...s, tasks: { ...s.tasks, [appId]: { ...task, progress: next } } }))
    }, TICK_MS)
    this.timers.set(appId, interval)
  }

  install = (appId: string) => this.startTask(appId, 'install')
  update = (appId: string) => this.startTask(appId, 'update')

  updateAll = () => {
    for (const appId of getUpdatableAppIds(this.state)) this.startTask(appId, 'update')
  }

  cancel = (appId: string) => {
    this.clearTimer(appId)
    this.setState((s) => {
      const { [appId]: _cancelled, ...tasks } = s.tasks
      return { ...s, tasks }
    })
  }

  retry = (appId: string) => {
    const task = this.state.tasks[appId]
    this.startTask(appId, task?.kind ?? 'update')
  }

  switchChannel = (appId: string, channelId: ChannelId) => {
    const channel = this.state.channels.find((c) => c.id === channelId)
    if (!channel || channel.locked) return
    this.setState((s) => ({ ...s, selectedChannel: { ...s.selectedChannel, [appId]: channelId } }))
  }

  updateSettings = (patch: Partial<StoreSettings>) => {
    const simulationChanged = patch.simulation && patch.simulation !== this.state.settings.simulation
    this.setState((s) => ({ ...s, settings: { ...s.settings, ...patch } }))
    if (simulationChanged) this.reloadCatalog()
  }

  checkForStoreUpdate = () => {
    if (this.state.storeUpdate.phase === 'checking' || this.state.storeUpdate.phase === 'downloading') return
    this.clearTimer('store')
    this.setState((s) => ({ ...s, storeUpdate: { ...s.storeUpdate, phase: 'checking' } }))

    this.timers.set(
      'store',
      setTimeout(() => {
        if (this.state.storeUpdate.availableVersion && this.state.storeUpdate.progress >= 100) {
          this.setState((s) => ({ ...s, storeUpdate: { ...s.storeUpdate, phase: 'ready', bannerDismissed: false } }))
          return
        }
        this.setState((s) => ({
          ...s,
          storeUpdate: { ...s.storeUpdate, availableVersion: '3.5.0', phase: 'downloading', progress: 0 },
        }))
        const interval = setInterval(() => {
          const next = Math.min(100, this.state.storeUpdate.progress + 12)
          const done = next >= 100
          if (done) this.clearTimer('store')
          this.setState((s) => ({
            ...s,
            storeUpdate: { ...s.storeUpdate, progress: next, phase: done ? 'ready' : 'downloading', bannerDismissed: false },
          }))
        }, TICK_MS)
        this.timers.set('store', interval)
      }, 1500),
    )
  }

  dismissStoreBanner = () => {
    this.setState((s) => ({ ...s, storeUpdate: { ...s.storeUpdate, bannerDismissed: true } }))
  }
}
