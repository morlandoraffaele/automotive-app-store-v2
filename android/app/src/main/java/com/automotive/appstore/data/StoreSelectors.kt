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

/**
 * The channel an app is on: the user's explicit choice, else the channel the catalogue published
 * it on. Defaulting straight to `stable` made every config-published `demo`/`alpha` entry report
 * itself as stable.
 */
fun getSelectedChannelId(snapshot: StoreSnapshot, app: AppListing): ChannelId =
    snapshot.selectedChannel[app.id] ?: getPublishedChannelId(app)

/** The channel the catalogue published this entry on; `stable` only if it has no release. */
fun getPublishedChannelId(app: AppListing): ChannelId =
    app.releases.firstOrNull()?.channelId ?: DEFAULT_CHANNEL_ID

fun getReleaseForChannel(app: AppListing, channelId: ChannelId): ChannelRelease? =
    app.releases.firstOrNull { it.channelId == channelId } ?: app.releases.firstOrNull()

fun getAppState(snapshot: StoreSnapshot, app: AppListing): AppState {
    val channelId = getSelectedChannelId(snapshot, app)
    val channel = snapshot.channels.firstOrNull { it.id == channelId }
    val release = getReleaseForChannel(app, channelId)
    val installed = snapshot.installed[app.id]
    val task = snapshot.tasks[app.id]
    val currentVersion = installed?.version

    if (task != null) {
        // A FAILED task records that an install attempt did not land; it is a statement about the
        // attempt, not about the device. It must not override the installed record, which is read
        // from `PackageManager` and is therefore the authoritative signal. Overriding it made a
        // correctly installed app report "failed" and show a Retry button with nothing to retry:
        // the session can be rejected or cancelled after the package has already landed, and
        // `onAppResumed` only reconciles tasks still in the INSTALLING phase, so the stale failure
        // outlived the install and nothing ever cleared it.
        //
        // Downgrading only applies when the installed version already satisfies the release — an
        // app that is genuinely behind a failed update must keep reporting FAILED.
        val satisfiedDespiteFailure = task.phase == TaskPhase.FAILED &&
            installed != null &&
            release != null &&
            installed.version == release.version

        if (!satisfiedDespiteFailure) {
            val status = when (task.phase) {
                TaskPhase.FAILED -> AppStatus.FAILED
                TaskPhase.INSTALLING -> AppStatus.INSTALLING
                TaskPhase.DOWNLOADING -> AppStatus.DOWNLOADING
            }
            return AppState(status, task.progress, currentVersion, task.targetVersion, channel, release)
        }
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
