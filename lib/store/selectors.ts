import type {
  AppListing,
  AppStatus,
  ChannelDefinition,
  ChannelRelease,
  StoreSnapshot,
} from './types'

export interface AppState {
  status: AppStatus
  progress: number
  currentVersion: string | null
  targetVersion: string | null
  channel: ChannelDefinition | undefined
  release: ChannelRelease | undefined
}

export function getSelectedChannelId(snapshot: StoreSnapshot, appId: string) {
  return snapshot.selectedChannel[appId] ?? 'stable'
}

export function getReleaseForChannel(app: AppListing, channelId: string) {
  return app.releases.find((r) => r.channelId === channelId) ?? app.releases[0]
}

export function getAppState(snapshot: StoreSnapshot, app: AppListing): AppState {
  const channelId = getSelectedChannelId(snapshot, app.id)
  const channel = snapshot.channels.find((c) => c.id === channelId)
  const release = getReleaseForChannel(app, channelId)
  const installed = snapshot.installed[app.id]
  const task = snapshot.tasks[app.id]
  const currentVersion = installed?.version ?? null

  if (task) {
    const status: AppStatus =
      task.phase === 'failed' ? 'failed' : task.phase === 'installing' ? 'installing' : 'downloading'
    return { status, progress: task.progress, currentVersion, targetVersion: task.targetVersion, channel, release }
  }

  if (!installed) {
    return { status: 'not-installed', progress: 0, currentVersion, targetVersion: release?.version ?? null, channel, release }
  }

  if (release && release.version !== installed.version) {
    return { status: 'update-available', progress: 0, currentVersion, targetVersion: release.version, channel, release }
  }

  return {
    status: installed.justCompleted ? 'installed' : 'up-to-date',
    progress: 100,
    currentVersion,
    targetVersion: null,
    channel,
    release,
  }
}

export function getUpdatableAppIds(snapshot: StoreSnapshot) {
  return snapshot.catalog.apps
    .filter((app) => {
      const { status } = getAppState(snapshot, app)
      return status === 'update-available' || status === 'failed'
    })
    .map((app) => app.id)
}

export function getActiveTaskCount(snapshot: StoreSnapshot) {
  return Object.values(snapshot.tasks).filter((t) => t.phase !== 'failed').length
}
