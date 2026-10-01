export type ChannelId = string

export type CategoryId =
  | 'navigation'
  | 'media'
  | 'charging'
  | 'communication'
  | 'utilities'
  | 'parked'

export type AppIconName =
  | 'navigation'
  | 'music'
  | 'podcast'
  | 'charging'
  | 'parking'
  | 'weather'
  | 'audiobook'
  | 'messaging'
  | 'calendar'
  | 'radio'
  | 'games'
  | 'tolls'

export type PermissionId =
  | 'location'
  | 'microphone'
  | 'contacts'
  | 'vehicle-data'
  | 'notifications'
  | 'storage'
  | 'phone'

export interface ChannelDefinition {
  id: ChannelId
  name: string
  description: string
  /** 0 = most stable. Higher numbers are less stable. */
  stabilityRank: number
  locked?: boolean
  lockReason?: string
}

export interface ChannelRelease {
  channelId: ChannelId
  version: string
  releaseDate: string
  changelog: string[]
}

export interface AppListing {
  id: string
  name: string
  developer: string
  tagline: string
  description: string
  category: CategoryId
  icon: AppIconName
  iconColor: string
  sizeMb: number
  permissions: PermissionId[]
  screenshots: string[]
  releases: ChannelRelease[]
}

export interface InstalledRecord {
  version: string
  /** True right after an install or update finished in this session. */
  justCompleted?: boolean
}

export type TaskKind = 'install' | 'update'
export type TaskPhase = 'downloading' | 'installing' | 'failed'

export interface InstallTask {
  kind: TaskKind
  phase: TaskPhase
  progress: number
  targetVersion: string
}

export type CatalogStatus = 'loading' | 'ready' | 'error'
export type CatalogSimulation = 'normal' | 'loading' | 'empty' | 'error'

export type ThemeMode = 'night' | 'day'
export type Locale = 'en' | 'ar'

export interface StoreSettings {
  autoUpdate: boolean
  wifiOnly: boolean
  theme: ThemeMode
  locale: Locale
  simulation: CatalogSimulation
}

export type StoreUpdatePhase = 'up-to-date' | 'checking' | 'downloading' | 'ready'

export interface StoreSelfUpdate {
  currentVersion: string
  availableVersion: string | null
  phase: StoreUpdatePhase
  progress: number
  bannerDismissed: boolean
}

export interface StoreSnapshot {
  catalog: { status: CatalogStatus; apps: AppListing[] }
  channels: ChannelDefinition[]
  installed: Record<string, InstalledRecord>
  selectedChannel: Record<string, ChannelId>
  tasks: Record<string, InstallTask>
  settings: StoreSettings
  storeUpdate: StoreSelfUpdate
}

export type AppStatus =
  | 'not-installed'
  | 'installed'
  | 'up-to-date'
  | 'update-available'
  | 'downloading'
  | 'installing'
  | 'failed'

export interface StoreRepository {
  getSnapshot(): StoreSnapshot
  subscribe(listener: () => void): () => void
  reloadCatalog(): void
  install(appId: string): void
  update(appId: string): void
  updateAll(): void
  cancel(appId: string): void
  retry(appId: string): void
  switchChannel(appId: string, channelId: ChannelId): void
  updateSettings(patch: Partial<StoreSettings>): void
  checkForStoreUpdate(): void
  dismissStoreBanner(): void
}
