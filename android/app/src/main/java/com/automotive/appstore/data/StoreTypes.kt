package com.automotive.appstore.data

typealias ChannelId = String

/** A store category. Mirrors the `CategoryId` union in the web app's `lib/store/types.ts`. */
enum class CategoryId {
    NAVIGATION,
    MEDIA,
    CHARGING,
    COMMUNICATION,
    UTILITIES,
    PARKED,
}

/**
 * A store category filter value: either the `All` pseudo-category or a real [CategoryId].
 * Mirrors `CategoryFilterValue` in the web app's `components/store/category-filter.tsx`.
 */
enum class CategoryFilterValue { ALL, CATEGORY }

/** Which glyph an app tile draws. Mirrors the `AppIconName` union. */
enum class AppIconName {
    NAVIGATION,
    MUSIC,
    PODCAST,
    CHARGING,
    PARKING,
    WEATHER,
    AUDIOBOOK,
    MESSAGING,
    CALENDAR,
    RADIO,
    GAMES,
    TOLLS,
}

/** A permission an app requests. Mirrors the `PermissionId` union. */
enum class PermissionId {
    LOCATION,
    MICROPHONE,
    CONTACTS,
    VEHICLE_DATA,
    NOTIFICATIONS,
    STORAGE,
    PHONE,
}

data class ChannelDefinition(
    val id: ChannelId,
    val name: String,
    val description: String,
    /** 0 = most stable. Higher numbers are less stable. */
    val stabilityRank: Int,
    val locked: Boolean = false,
    val lockReason: String? = null,
)

data class ChannelRelease(
    val channelId: ChannelId,
    val version: String,
    /** ISO-8601 calendar date, e.g. `2026-09-22`. */
    val releaseDate: String,
    val changelog: List<String>,
)

/**
 * This app's own `applicationId`.
 *
 * The store publishes itself in the remote catalogue so that head units can update it through the
 * normal install flow (see [StoreSelfUpdate]), which means the listing shows up in the catalogue
 * like any other app. It must be filtered out of the browsable catalogue: a store that lists
 * itself as an installable app is confusing, and "installing" the store you are already running
 * from inside itself is not a supported operation.
 */
const val STORE_APP_PACKAGE = "org.radioplayer.automotive.appstore"

data class AppListing(
    val id: String,
    val name: String,
    val developer: String,
    val tagline: String,
    val description: String,
    val category: CategoryId,
    val icon: AppIconName,
    /** Background colour of the app tile, converted from the web app's `oklch()` value. */
    val iconColor: Int,
    val sizeMb: Int,
    val permissions: List<PermissionId>,
    val screenshots: List<Int>,
    val releases: List<ChannelRelease>,
    // Remote catalogue fields, ported in from the `config.json` contract. Every one of them has
    // a default so the existing `MockStoreData` fixtures and their tests keep compiling, and so
    // the UI — which never reads them — needs no change.
    /** The installed package, which is also the key `config.json` publishes the app under. */
    val packageName: String = id,
    /** Pre-computed APK URL; the installer downloads this URL directly. */
    val apkUrl: String? = null,
    /** Pre-computed icon URL. */
    val iconUrl: String? = null,
    /** The media-browser service class the app publishes, if any. */
    val mediaServiceClass: String? = null,
    /** The `version` field of `config.json`: an Android `versionCode`. */
    val remoteVersionCode: Int = 0,
)

data class InstalledRecord(
    val version: String,
    /** True right after an install or update finished in this session. */
    val justCompleted: Boolean = false,
)

enum class TaskKind { INSTALL, UPDATE }

enum class TaskPhase { DOWNLOADING, INSTALLING, FAILED }

data class InstallTask(
    val kind: TaskKind,
    val phase: TaskPhase,
    val progress: Int,
    val targetVersion: String,
)

enum class CatalogStatus { LOADING, READY, ERROR }

enum class CatalogSimulation { NORMAL, LOADING, EMPTY, ERROR }

enum class ThemeMode { NIGHT, DAY }

enum class Locale { EN, AR }

data class StoreSettings(
    val autoUpdate: Boolean,
    val wifiOnly: Boolean,
    val theme: ThemeMode,
    val locale: Locale,
    val simulation: CatalogSimulation,
)

enum class StoreUpdatePhase { UP_TO_DATE, CHECKING, DOWNLOADING, READY }

data class StoreSelfUpdate(
    val currentVersion: String,
    val availableVersion: String?,
    val phase: StoreUpdatePhase,
    val progress: Int,
    val bannerDismissed: Boolean,
)

data class StoreSnapshot(
    val catalog: Catalog,
    val channels: List<ChannelDefinition>,
    val installed: Map<String, InstalledRecord>,
    val selectedChannel: Map<String, ChannelId>,
    val tasks: Map<String, InstallTask>,
    val settings: StoreSettings,
    val storeUpdate: StoreSelfUpdate,
)

data class Catalog(
    val status: CatalogStatus,
    val apps: List<AppListing>,
)

/** Lifecycle of an app relative to its selected channel. Mirrors the `AppStatus` union. */
enum class AppStatus {
    NOT_INSTALLED,
    INSTALLED,
    UP_TO_DATE,
    UPDATE_AVAILABLE,
    DOWNLOADING,
    INSTALLING,
    FAILED,
}
