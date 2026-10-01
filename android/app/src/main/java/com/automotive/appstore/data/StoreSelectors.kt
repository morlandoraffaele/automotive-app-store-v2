package com.automotive.appstore.data

/** The derived state of one app, mirroring `AppState` in the web app's selectors. */
data class AppState(
    val status: AppStatus,
    val progress: Int,
    val currentVersion: String?,
    val targetVersion: String?,
    val channel: ChannelDefinition?,
    val release: ChannelRelease?,
)

fun getSelectedChannelId(snapshot: StoreSnapshot, appId: String): ChannelId =
    snapshot.selectedChannel[appId] ?: DEFAULT_CHANNEL_ID

fun getReleaseForChannel(app: AppListing, channelId: ChannelId): ChannelRelease? =
    app.releases.firstOrNull { it.channelId == channelId } ?: app.releases.firstOrNull()

fun getAppState(snapshot: StoreSnapshot, app: AppListing): AppState {
    val channelId = getSelectedChannelId(snapshot, app.id)
    val channel = snapshot.channels.firstOrNull { it.id == channelId }
    val release = getReleaseForChannel(app, channelId)
    val installed = snapshot.installed[app.id]
    val task = snapshot.tasks[app.id]
    val currentVersion = installed?.version

    if (task != null) {
        val status = when (task.phase) {
            TaskPhase.FAILED -> AppStatus.FAILED
            TaskPhase.INSTALLING -> AppStatus.INSTALLING
            TaskPhase.DOWNLOADING -> AppStatus.DOWNLOADING
        }
        return AppState(status, task.progress, currentVersion, task.targetVersion, channel, release)
    }

    if (installed == null) {
        return AppState(
            status = AppStatus.NOT_INSTALLED,
            progress = 0,
            currentVersion = null,
            targetVersion = release?.version,
            channel = channel,
            release = release,
        )
    }

    if (release != null && release.version != installed.version) {
        return AppState(
            status = AppStatus.UPDATE_AVAILABLE,
            progress = 0,
            currentVersion = currentVersion,
            targetVersion = release.version,
            channel = channel,
            release = release,
        )
    }

    return AppState(
        status = if (installed.justCompleted) AppStatus.INSTALLED else AppStatus.UP_TO_DATE,
        progress = 100,
        currentVersion = currentVersion,
        targetVersion = null,
        channel = channel,
        release = release,
    )
}

fun getUpdatableAppIds(snapshot: StoreSnapshot): List<String> =
    snapshot.catalog.apps
        .filter { app ->
            when (getAppState(snapshot, app).status) {
                AppStatus.UPDATE_AVAILABLE, AppStatus.FAILED -> true
                else -> false
            }
        }
        .map { it.id }

fun getActiveTaskCount(snapshot: StoreSnapshot): Int =
    snapshot.tasks.values.count { it.phase != TaskPhase.FAILED }

const val DEFAULT_CHANNEL_ID = "stable"
