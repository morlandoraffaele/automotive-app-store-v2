package com.automotive.appstore.data

import android.app.Application
import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageInstaller
import android.support.v4.media.MediaBrowserCompat
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.automotive.appstore.data.local.AppLocalDataStore
import com.automotive.appstore.data.remote.App
import com.automotive.appstore.data.remote.RetrofitClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.io.File


/**
 * Single source of truth for store state, now backed by the real backend.
 *
 * Replaces `MockStoreRepository` with the logic ported 1:1 from
 * `radioplayer-automotive-appstore`: the catalogue comes from `AppRepository` (Retrofit ->
 * `store/config.json`), an app's state comes from `PackageManager`, its APK is fetched with
 * `DownloadManager` and installed through a `PackageInstaller` session.
 *
 * The public surface is deliberately identical to the mock's — same method names, same
 * [StoreSnapshot] shape — so every screen and `StoreViewModel` call site is untouched.
 */
class StoreRepository(
    private val application: Application,
    private val scope: CoroutineScope,
) {
    companion object {
        private const val TAG = "StoreRepository"
        private const val ACTION_INSTALL_COMPLETE = "apk_install_complete"
        private const val ACTION_UNINSTALL_COMPLETE = "apk_uninstall_complete"

        /**
         * The key the platform uses to hand back the install-confirmation Intent when a session
         * comes back as `STATUS_PENDING_USER_ACTION`.
         *
         * Not public API, so the literal is used. Verified against a real
         * `STATUS_PENDING_USER_ACTION` broadcast on Android 14, whose extras are:
         * `android.content.pm.extra.STATUS`, `...PACKAGE_NAME`, `...PRE_APPROVAL`,
         * `...SESSION_ID` and `android.intent.extra.INTENT`.
         */
        private const val EXTRA_INTENT_KEY = "android.intent.extra.INTENT"

        private const val CATALOG_JOB = "catalog"
        private const val STORE_JOB = "store"
        private const val POLL_INTERVAL_MS = 1000L
        private const val DEFAULT_STORE_VERSION = "1.0.0"
    }

    /** The upstream repository, built from the same collaborators as the reference app. */
    private val api = AppRepository(
        RetrofitClient.instance,
        AppLocalDataStore(application.applicationContext),
        application.applicationContext,
    )

    private val _snapshot = MutableStateFlow(
        StoreSnapshot(
            catalog = Catalog(CatalogStatus.LOADING, emptyList()),
            channels = MockStoreData.MOCK_CHANNELS,
            installed = emptyMap(),
            selectedChannel = emptyMap(),
            tasks = emptyMap(),
            settings = MockStoreData.DEFAULT_SETTINGS,
            // The endpoint publishes no self-update feed, so the store starts as up to date
            // rather than advertising the fixture's simulated 3.5.0 build.
            storeUpdate = StoreSelfUpdate(
                currentVersion = currentStoreVersion(),
                availableVersion = null,
                phase = StoreUpdatePhase.UP_TO_DATE,
                progress = 0,
                bannerDismissed = false,
            ),
        )
    )
    val snapshot: StateFlow<StoreSnapshot> = _snapshot.asStateFlow()

    private val downloadManager: DownloadManager =
        application.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager

    private val jobs = mutableMapOf<String, Job>()
    private val pollingJobs = mutableMapOf<String, Job>()

    private val downloadReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val downloadId = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1L) ?: -1L
            if (downloadId != -1L) onDownloadCompletedByReceiver(downloadId)
        }
    }

    private val installReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val packageName = intent?.getStringExtra(PackageInstaller.EXTRA_PACKAGE_NAME) ?: return

            // PackageInstaller status values: STATUS_SUCCESS = 0, STATUS_FAILURE = 1,
            // STATUS_PENDING_USER_ACTION = -1.
            //
            // The reference app only ever runs PRIVILEGED, where the session installs silently and
            // comes back as SUCCESS. On the consent route the system instead asks for confirmation
            // first and hands over the Intent to show it in EXTRA_INTENT, which must be started or
            // the install never happens.
            val status = intent.getIntExtra(
                PackageInstaller.EXTRA_STATUS,
                PackageInstaller.STATUS_FAILURE,
            )
            Log.i(TAG, "APK install broadcast: package=$packageName status=$status")

            when (status) {
                PackageInstaller.STATUS_PENDING_USER_ACTION -> {
                    val confirmation: Intent? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        intent.getParcelableExtra(EXTRA_INTENT_KEY, Intent::class.java)
                    } else {
                        @Suppress("DEPRECATION")
                        intent.getParcelableExtra(EXTRA_INTENT_KEY)
                    }
                    if (confirmation != null) {
                        // NEW_TASK: launched from the repository, which has no task of its own.
                        application.startActivity(confirmation.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
                        Log.i(TAG, "Started the install confirmation for $packageName")
                    } else {
                        Log.w(TAG, "Pending user action for $packageName but no EXTRA_INTENT")
                        onInstallCompletedOrCancelled(packageName, false)
                    }
                }

                PackageInstaller.STATUS_SUCCESS ->
                    onInstallCompletedOrCancelled(packageName, succeeded = true)

                else -> {
                    Log.e(
                        TAG,
                        "Install of $packageName failed with status $status " +
                            "(1 = STATUS_FAILURE). The session was committed but the platform " +
                            "refused it — check the appop/permission and the staged APK.",
                    )
                    onInstallCompletedOrCancelled(packageName, succeeded = false)
                }
            }
        }
    }

    private val uninstallReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val packageName = intent?.getStringExtra(PackageInstaller.EXTRA_PACKAGE_NAME) ?: return
            val status = intent.getIntExtra(
                PackageInstaller.EXTRA_STATUS,
                PackageInstaller.STATUS_FAILURE,
            )
            val message = intent.getStringExtra(PackageInstaller.EXTRA_STATUS_MESSAGE)
            Log.i(TAG, "APK uninstall broadcast: package=$packageName status=$status message=$message")

            when (status) {
                PackageInstaller.STATUS_SUCCESS -> refreshInstalled()

                // The platform wants the user to confirm, e.g. when the store is neither the
                // installer of record nor holding MANAGE_PROFILE_AND_DEVICE_OWNERS. The
                // confirmation result is reconciled by [onAppResumed], as for ACTION_DELETE.
                PackageInstaller.STATUS_PENDING_USER_ACTION -> {
                    val confirmation: Intent? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        intent.getParcelableExtra(EXTRA_INTENT_KEY, Intent::class.java)
                    } else {
                        @Suppress("DEPRECATION")
                        intent.getParcelableExtra(EXTRA_INTENT_KEY)
                    }
                    if (confirmation != null) {
                        application.startActivity(confirmation.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
                        Log.i(TAG, "Started the uninstall confirmation for $packageName")
                    } else {
                        Log.w(TAG, "Pending user action for $packageName but no EXTRA_INTENT")
                        refreshInstalled()
                    }
                }

                // Puts the record dropped by [uninstall] back, since the package is still there.
                else -> {
                    Log.e(TAG, "Uninstall of $packageName failed with status $status: $message")
                    refreshInstalled()
                }
            }
        }
    }

    private var receiversRegistered = false

    /**
     * The app whose install is blocked on the user granting "install unknown apps".
     *
     * Held in memory only: it is a transient UI state, and losing it on process death is harmless
     * because [onAppResumed] also reconciles any still-`INSTALLING` task.
     */
    private var awaitingConsent: String? = null

    init {
        registerReceivers()
        reloadCatalog()
    }

    /**
     * Receiver flags, following the reference app exactly:
     *
     * - `ACTION_DOWNLOAD_COMPLETE` is a protected broadcast, so it must be EXPORTED.
     * - `apk_install_complete` is sent by the **system** package installer through the
     *   `PendingIntent` this app committed with. The broadcast therefore does not originate from
     *   our own UID, so a NOT_EXPORTED receiver would never see it and the install would hang on
     *   "Installing" forever. This is why the reference app registers it EXPORTED on API 33+.
     * - `apk_uninstall_complete` is sent the same way by `PackageInstaller.uninstall`, so it is
     *   EXPORTED for the same reason.
     */
    fun registerReceivers() {
        if (receiversRegistered) return
        val downloadFilter = IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE)
        val installFilter = IntentFilter(ACTION_INSTALL_COMPLETE)
        val uninstallFilter = IntentFilter(ACTION_UNINSTALL_COMPLETE)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            application.registerReceiver(downloadReceiver, downloadFilter, Context.RECEIVER_EXPORTED)
            application.registerReceiver(installReceiver, installFilter, Context.RECEIVER_EXPORTED)
            application.registerReceiver(uninstallReceiver, uninstallFilter, Context.RECEIVER_EXPORTED)
        } else {
            ContextCompat.registerReceiver(
                application, downloadReceiver, downloadFilter, ContextCompat.RECEIVER_EXPORTED
            )
            ContextCompat.registerReceiver(
                application, installReceiver, installFilter, ContextCompat.RECEIVER_EXPORTED
            )
            ContextCompat.registerReceiver(
                application, uninstallReceiver, uninstallFilter, ContextCompat.RECEIVER_EXPORTED
            )
        }
        receiversRegistered = true
    }

    fun unregisterReceivers() {
        if (!receiversRegistered) return
        runCatching { application.unregisterReceiver(downloadReceiver) }
        runCatching { application.unregisterReceiver(installReceiver) }
        runCatching { application.unregisterReceiver(uninstallReceiver) }
        receiversRegistered = false
    }

    /**
     * Refetches the catalogue.
     *
     * The `simulation` setting is honoured exactly as the mock honoured it, so the demo switch in
     * Settings keeps working; `NORMAL` now performs the real network call instead of a timer.
     */
    fun reloadCatalog() {
        jobs.remove(CATALOG_JOB)?.cancel()
        set { it.copy(catalog = Catalog(CatalogStatus.LOADING, it.catalog.apps)) }

        val mode = _snapshot.value.settings.simulation
        // The hardcoded override wins over the simulation switch. Both are developer affordances
        // that replace the catalogue, and the override is the more specific of the two: leaving it
        // subordinate would mean toggling it silently does nothing whenever the simulation happens
        // to sit on EMPTY or ERROR.
        if (!api.isHardcodedConfigEnabled()) {
            if (mode == CatalogSimulation.LOADING) return

            if (mode != CatalogSimulation.NORMAL) {
                set { current ->
                    current.copy(
                        catalog = when (mode) {
                            CatalogSimulation.ERROR -> Catalog(CatalogStatus.ERROR, emptyList())
                            CatalogSimulation.EMPTY -> Catalog(CatalogStatus.READY, emptyList())
                            else -> current.catalog
                        }
                    )
                }
                return
            }
        }

        jobs[CATALOG_JOB] = scope.launch {
            api.getAppList().fold(
                onSuccess = { apps ->
                    set { it.copy(catalog = Catalog(CatalogStatus.READY, CatalogMapper.toListings(apps))) }
                    refreshInstalled()
                    resumePendingDownloads()
                    // The catalogue response already carries the store's own entry, so the
                    // startup self-update check reuses it rather than making a second request.
                    resolveStoreUpdate(apps)?.let(::setStoreUpdate)
                },
                onFailure = { e ->
                    Log.e(TAG, "Error while loading: ${e.message}", e)
                    set { it.copy(catalog = Catalog(CatalogStatus.ERROR, emptyList())) }
                },
            )
        }
    }

    /**
     * Re-reads every catalogue app's installed `versionCode` from `PackageManager`, which is what
     * the reference app compares against the remote `version` field.
     */
    private fun refreshInstalled() {
        val installed = _snapshot.value.catalog.apps.mapNotNull { app ->
            val versionCode = api.getInstalledVersionCode(app.packageName) ?: return@mapNotNull null
            val justCompleted = _snapshot.value.installed[app.id]?.justCompleted ?: false
            // installedVersionLabel, not the raw versionCode: the published versionCode in
            // config.json is often a placeholder (many entries declare 1), so recording the real
            // number would leave the UI permanently reporting "update available".
            app.id to InstalledRecord(
                CatalogMapper.installedVersionLabel(versionCode, app.remoteVersionCode),
                justCompleted,
            )
        }.toMap()
        set { it.copy(installed = installed) }
    }

    fun install(appId: String) = startTask(appId, TaskKind.INSTALL)

    /**
     * Opens an installed app.
     *
     * Two shapes, because most of this catalogue is headless:
     *  - a normal app with a launcher activity: its launch Intent is started;
     *  - a headless media app with no launcher activity (all the Radioplayer/BBC/ARD entries):
     *    the store connects to its `MediaBrowserService` and opens its playback UI, which is how
     *    these apps are meant to be controlled on a head unit.
     *
     * @return `true` when something was actually started or requested.
     */
    fun launch(appId: String): Boolean {
        val app = _snapshot.value.catalog.apps.firstOrNull { it.id == appId }
        if (app == null) {
            Log.w(TAG, "Cannot launch unknown app $appId")
            return false
        }

        if (api.getInstalledVersionCode(app.packageName) == null) {
            Log.w(TAG, "Cannot launch ${app.packageName}: not installed")
            return false
        }

        if (CatalogMapper.isHeadlessMediaApp(application, app)) {
            return launchMediaApp(app)
        }

        // Widened activity lookup: `getLaunchIntentForPackage` misses the head-unit apps that
        // declare ACTION_MAIN without CATEGORY_LAUNCHER, which is why Open used to do nothing.
        val intent = CatalogMapper.launchIntentFor(application, app)
        if (intent == null) {
            Log.w(TAG, "No launchable activity for ${app.packageName}")
            return false
        }

        return try {
            application.startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
            Log.i(TAG, "Launched ${app.packageName} via ${intent.component}")
            true
        } catch (e: Exception) {
            Log.e(TAG, "Failed to launch ${app.packageName}", e)
            false
        }
    }

    /**
     * Opens a headless media app.
     *
     * These apps expose **no launcher activity**, only a `MediaBrowserService`, so there is nothing
     * to `startActivity`. The correct way in is to be a media client: bind a [MediaBrowserCompat] to the
     * app's session token, then `onConnect` to it, which makes the app publish its session and hand
     * playback control to whoever is connected.
     *
     * A previously used `sendBroadcast(ACTION_MEDIA_BUTTON)` looked like it worked — the broadcast
     * is fire-and-forget and no exception is raised — but nothing visible happened, because no
     * media app is listening for it. Binding is what actually opens the app.
     */
    private fun launchMediaApp(app: AppListing): Boolean {
        val component = CatalogMapper.mediaBrowserComponentFor(app)
        if (component == null) {
            Log.w(TAG, "${app.packageName} is headless but publishes no service class")
            return false
        }

        return try {
            val browser = MediaBrowserCompat(application, component, connection, null)
            // Held for the process lifetime: a MediaBrowser must not be collected while connected,
            // and the app stays connected so the user keeps control of the session.
            mediaBrowsers[app.id] = browser
            browser.connect()
            Log.i(TAG, "Connecting to media session of ${app.packageName} via $component")
            true
        } catch (e: Exception) {
            Log.e(TAG, "Failed to connect to media app ${app.packageName}", e)
            false
        }
    }

    /**
     * Receives the app's media session once the browser connects.
     *
     * Also surfaces an active session so the store can report that the app really is open, rather
     * than relying on an Intent that may resolve to nothing.
     */
    private val connection = object : MediaBrowserCompat.ConnectionCallback() {
        override fun onConnected() {
            Log.i(TAG, "Media browser connected")
        }

        override fun onConnectionFailed() {
            Log.w(TAG, "Media browser connection failed")
        }
    }

    private val mediaBrowsers = mutableMapOf<String, MediaBrowserCompat>()

    /** Releases every media-browser connection held for a launched app. */
    fun releaseMediaBrowsers() {
        mediaBrowsers.values.forEach { runCatching { it.disconnect() } }
        mediaBrowsers.clear()
    }

    fun update(appId: String) = startTask(appId, TaskKind.UPDATE)

    /**
     * Removes an installed app from the device.
     *
     * Two routes, mirroring [installRoute]'s precedence:
     *  - `DELETE_PACKAGES` is granted (a priv-app on the system partition, the production route):
     *    `PackageInstaller.uninstall` runs silently, with no system dialog, as long as the store
     *    also holds `MANAGE_PROFILE_AND_DEVICE_OWNERS` or installed the app itself. Otherwise the
     *    platform asks for confirmation, which [uninstallReceiver] starts;
     *  - otherwise the request is handed to the platform uninstaller via `ACTION_DELETE`, which
     *    shows the standard confirmation and works for any app.
     *
     * Either way the caller is left in a truthful state: the installed record is dropped from the
     * snapshot immediately so the tile flips back to "Install" without waiting for the result. If
     * the platform refuses, [uninstallReceiver] re-reads `PackageManager` and puts the record back;
     * if the user cancels a confirmation, [onAppResumed] does.
     *
     * @return `true` when the removal was requested, `false` when it could not be started.
     */
    fun uninstall(appId: String): Boolean {
        val app = _snapshot.value.catalog.apps.firstOrNull { it.id == appId } ?: return false
        if (api.getInstalledVersionCode(app.packageName) == null) {
            Log.w(TAG, "Cannot uninstall ${app.packageName}: not installed")
            return false
        }

        // A pending task or a held media-browser connection would outlive the package and keep a
        // dead app in the UI, so both are torn down first.
        pollingJobs.remove(appId)?.cancel()
        runCatching { mediaBrowsers.remove(appId)?.disconnect() }
        api.getDownloadId(app.packageName)?.let { downloadId ->
            runCatching { downloadManager.remove(downloadId) }
        }
        api.clearDownloadId(app.packageName)

        set { current ->
            current.copy(
                tasks = current.tasks - appId,
                installed = current.installed - appId,
            )
        }

        if (canDeleteSilently()) {
            return try {
                // The result arrives asynchronously in [uninstallReceiver]. The PendingIntent must
                // be MUTABLE: the platform reports EXTRA_STATUS, EXTRA_STATUS_MESSAGE and, when a
                // confirmation is needed, EXTRA_INTENT by filling it in, and an immutable one drops
                // all of them. The Intent is explicit and targets our own package, which is what
                // makes a mutable PendingIntent legal on Android 14+, as on the install path.
                val resultIntent = Intent(ACTION_UNINSTALL_COMPLETE)
                    .putExtra(PackageInstaller.EXTRA_PACKAGE_NAME, app.packageName)
                    .setPackage(application.packageName)
                val sender = android.app.PendingIntent.getBroadcast(
                    application,
                    app.packageName.hashCode(),
                    resultIntent,
                    android.app.PendingIntent.FLAG_UPDATE_CURRENT or
                        android.app.PendingIntent.FLAG_MUTABLE,
                ).intentSender

                application.packageManager.packageInstaller
                    .uninstall(app.packageName, sender)
                Log.i(TAG, "Requested uninstall of ${app.packageName} via PackageInstaller")
                true
            } catch (e: SecurityException) {
                // Not actually privileged after all — fall through to the platform uninstaller.
                Log.w(TAG, "DELETE_PACKAGES refused for ${app.packageName}", e)
                requestPlatformUninstall(app.packageName)
            } catch (e: Exception) {
                Log.e(TAG, "Failed to uninstall ${app.packageName}", e)
                requestPlatformUninstall(app.packageName)
            }
        }

        return requestPlatformUninstall(app.packageName)
    }

    /** Whether this build holds `DELETE_PACKAGES`, the permission a silent uninstall needs. */
    private fun canDeleteSilently(): Boolean =
        application.packageManager.checkPermission(
            android.Manifest.permission.DELETE_PACKAGES,
            application.packageName,
        ) == android.content.pm.PackageManager.PERMISSION_GRANTED

    /**
     * Hands the package to the system uninstaller, which shows the standard confirmation.
     *
     * `NEW_TASK` because the repository has no Activity of its own; the result is picked up by
     * [onAppResumed] when the store comes back to the foreground, the same way the installer's
     * confirmation dialog is.
     */
    private fun requestPlatformUninstall(packageName: String): Boolean {
        val intent = Intent(
            Intent.ACTION_DELETE,
            Uri.parse("package:$packageName"),
        ).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

        return try {
            application.startActivity(intent)
            Log.i(TAG, "Requested platform uninstall of $packageName")
            true
        } catch (e: Exception) {
            Log.e(TAG, "Could not open the uninstaller for $packageName", e)
            false
        }
    }

    /**
     * Updates the store itself, by running its own catalogue entry through the normal install flow.
     *
     * `config.json` publishes the store under [STORE_APP_PACKAGE] so that a head unit can replace
     * it, which means the machinery is already here: the same download, the same `PackageInstaller`
     * session and the same result handling as any other app. Only the entry point differs — this is
     * the one install that replaces the process performing it, so `PackageInstaller` will kill the
     * app once the session commits.
     *
     * This is deliberately not a bulk operation: the store is filtered out of the browsable
     * catalogue and out of `getUpdatableAppIds`, so it can only be updated from here, one
     * deliberate tap at a time.
     */
    fun updateStore() {
        val store = _snapshot.value.catalog.apps
            .firstOrNull { it.packageName == STORE_APP_PACKAGE }
        if (store == null) {
            Log.w(TAG, "Catalogue does not publish $STORE_APP_PACKAGE; cannot self-update")
            return
        }
        startTask(store.id, TaskKind.UPDATE)
    }

    /** Cancels the download the way the reference app does: drop the id and forget the task. */
    fun cancel(appId: String) {
        val app = _snapshot.value.catalog.apps.firstOrNull { it.id == appId } ?: return
        pollingJobs.remove(appId)?.cancel()
        api.getDownloadId(app.packageName)?.let { downloadId ->
            runCatching { downloadManager.remove(downloadId) }
        }
        api.clearDownloadId(app.packageName)
        set { it.copy(tasks = it.tasks - appId) }
    }

    fun retry(appId: String) {
        val kind = _snapshot.value.tasks[appId]?.kind ?: TaskKind.UPDATE
        startTask(appId, kind)
    }

    fun switchChannel(appId: String, channelId: ChannelId) {
        val channel = _snapshot.value.channels.firstOrNull { it.id == channelId } ?: return
        if (channel.locked) return
        set { it.copy(selectedChannel = it.selectedChannel + (appId to channelId)) }
    }

    fun updateSettings(settings: StoreSettings) {
        val simulationChanged = settings.simulation != _snapshot.value.settings.simulation
        set { it.copy(settings = settings) }
        if (simulationChanged) reloadCatalog()
    }

    /**
     * Whether the debug catalogue replaces `config.json`. See [HardcodedConfig].
     *
     * Reloading is what makes the change visible: `reloadCatalog` re-runs the startup self-update
     * check on the response it gets back, so the badge and the Settings row both refresh in step.
     */
    fun isHardcodedConfigEnabled(): Boolean = api.isHardcodedConfigEnabled()

    fun setHardcodedConfigEnabled(enabled: Boolean) {
        api.setHardcodedConfigEnabled(enabled)
        reloadCatalog()
    }

    /** The store `version` the debug catalogue publishes, or `null` when it is derived. */
    fun hardcodedStoreVersion(): Int? = api.hardcodedStoreVersion()

    /**
     * The running store's `versionCode` — the left-hand side of the self-update comparison.
     *
     * Read from `PackageManager` rather than a build constant so the debug stepper is anchored to
     * exactly the number [resolveStoreUpdate] compares against, not a copy of it that could drift.
     */
    fun installedStoreVersionCode(): Int? = currentStoreVersionCode()

    fun setHardcodedStoreVersion(versionCode: Int?) {
        api.setHardcodedStoreVersion(versionCode)
        reloadCatalog()
    }

    /**
     * The store's own `versionName`, read from `PackageManager`. Shown by the About row in
     * Settings, which previously displayed the fixture's hard-coded version.
     */
    private fun currentStoreVersion(): String = try {
        @Suppress("DEPRECATION")
        application.packageManager.getPackageInfo(application.packageName, 0).versionName
            ?: DEFAULT_STORE_VERSION
    } catch (e: Exception) {
        DEFAULT_STORE_VERSION
    }

    /**
     * The store does not publish a self-update feed, so the reference app has no equivalent of
     * this check. It reads the store's own entry out of `config.json` — the same document the
     * catalogue comes from — and compares its published `version` against the running `versionCode`.
     */
    fun checkForStoreUpdate() {
        val phase = _snapshot.value.storeUpdate.phase
        if (phase == StoreUpdatePhase.CHECKING || phase == StoreUpdatePhase.DOWNLOADING) return
        jobs.remove(STORE_JOB)?.cancel()
        set { it.copy(storeUpdate = it.storeUpdate.copy(phase = StoreUpdatePhase.CHECKING)) }

        jobs[STORE_JOB] = scope.launch {
            // A user explicitly asked to re-check, so bypass any intermediary cache.
            val resolved = fetchAndResolveStoreUpdate(fresh = true)
            if (resolved != null) {
                setStoreUpdate(resolved)
            } else {
                // The check could not be made. Leave any already-known update in place rather than
                // clearing a badge we have no new information about, but drop out of CHECKING so
                // the Settings row does not spin forever.
                set { current ->
                    val previous = current.storeUpdate
                    current.copy(
                        storeUpdate = previous.copy(
                            phase = if (previous.availableVersion != null) {
                                StoreUpdatePhase.READY
                            } else {
                                StoreUpdatePhase.UP_TO_DATE
                            },
                        )
                    )
                }
            }
        }
    }

    /**
     * Fetches the catalogue and works out whether the store itself is out of date.
     *
     * @param fresh bypass any intermediary cache; set when the user asked for the re-check.
     * @return the self-update state to publish, or `null` when the check could not be made — in
     *   which case the caller should leave the current state alone rather than clearing a badge it
     *   has no new information about.
     */
    private suspend fun fetchAndResolveStoreUpdate(fresh: Boolean): StoreSelfUpdate? {
        val apps = api.getAppList(fresh = fresh).getOrElse { e ->
            Log.w(TAG, "Store update check failed: ${e.message}")
            return null
        }
        return resolveStoreUpdate(apps)
    }

    /**
     * Works out whether the store is out of date from an already-fetched catalogue.
     *
     * Pure apart from the two `PackageManager` reads, so the startup path can pass the catalogue
     * it already downloaded instead of issuing a second request, while the Settings path re-fetches
     * and calls this with the fresh result. Both paths therefore agree by construction.
     *
     * @return the self-update state, or `null` when the catalogue does not publish the store.
     */
    private fun resolveStoreUpdate(apps: List<App>): StoreSelfUpdate? {
        // config.json is keyed by package name; fall back to the declared field in case a key and
        // a packageName ever disagree.
        val entry = apps.firstOrNull { it.key == STORE_APP_PACKAGE }
            ?: apps.firstOrNull { it.details.packageName == STORE_APP_PACKAGE }
        if (entry == null) {
            Log.i(TAG, "Catalogue does not publish $STORE_APP_PACKAGE; treating store as current")
            return null
        }

        val remoteVersionCode = entry.details.remoteVersionCode
        val installedVersionCode = currentStoreVersionCode()

        // Note this deliberately does not go through `CatalogMapper.installedVersionLabel`, which
        // rewrites the recorded version to the published placeholder whenever the device is newer.
        // That is right for the per-app UI but would silently hide a real self-update.
        val available = CatalogMapper.storeUpdateVersionLabel(installedVersionCode, remoteVersionCode)
        Log.i(
            TAG,
            "Store update check: installed=$installedVersionCode published=$remoteVersionCode " +
                "available=$available",
        )

        val previous = _snapshot.value.storeUpdate
        return previous.copy(
            currentVersion = currentStoreVersion(),
            availableVersion = available,
            phase = if (available != null) StoreUpdatePhase.READY else StoreUpdatePhase.UP_TO_DATE,
            progress = 0,
            // A newly found update always re-arms the badge, even if an earlier one was dismissed.
            bannerDismissed = false,
        )
    }

    private fun setStoreUpdate(update: StoreSelfUpdate) {
        set { it.copy(storeUpdate = update) }
    }

    /** The running store's own `versionCode`, which is what `config.json`'s `version` compares to. */
    private fun currentStoreVersionCode(): Int? = api.getInstalledVersionCode(STORE_APP_PACKAGE)

    /**
     * Begins an install or update: enqueue the APK download, persist its id and start polling.
     *
     * Ported from `AppDetailViewModel.startDownload`; the download id is stored through
     * [AppRepository] so a resumed download is picked back up by [resumePendingDownloads].
     */
    private fun startTask(appId: String, kind: TaskKind) {
        val app = _snapshot.value.catalog.apps.firstOrNull { it.id == appId } ?: return
        val apkUrl = app.apkUrl
        if (apkUrl.isNullOrEmpty()) {
            set {
                it.copy(
                    tasks = it.tasks + (
                        appId to InstallTask(kind, TaskPhase.FAILED, 0, app.remoteVersionCode.toString())
                        )
                )
            }
            return
        }

        pollingJobs.remove(appId)?.cancel()
        val targetVersion = app.remoteVersionCode.toString()

        val externalFilesDir = application.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
        if (externalFilesDir == null) {
            Log.e(TAG, "No external files dir available; cannot stage ${app.packageName}")
            setTask(appId, kind, TaskPhase.FAILED, 0, targetVersion)
            return
        }

        val targetFile = File(externalFilesDir, app.packageName)
        if (targetFile.exists() && !targetFile.delete()) {
            Log.w(TAG, "Could not delete stale APK at ${targetFile.absolutePath}")
        }

        val request = DownloadManager.Request(Uri.parse(apkUrl))
            .setTitle(app.name)
            .setDescription("Download ${app.name}")
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            // setDestinationInExternalFilesDir, not setDestinationUri(Uri.fromFile(..)): a
            // file:// destination is rejected by DownloadManager from Android 10 (API 29), and
            // this module has minSdk 29. Both resolve to the same
            // getExternalFilesDir(DIRECTORY_DOWNLOADS)/<package> path, so the install step still
            // finds the APK where it looks for it.
            .setDestinationInExternalFilesDir(
                application,
                Environment.DIRECTORY_DOWNLOADS,
                app.packageName,
            )
            .setMimeType("application/vnd.android.package-archive")
            .setAllowedOverMetered(!_snapshot.value.settings.wifiOnly)
            .setAllowedOverRoaming(false)

        set {
            it.copy(
                tasks = it.tasks + (appId to InstallTask(kind, TaskPhase.DOWNLOADING, 0, targetVersion))
            )
        }

        try {
            val downloadId = downloadManager.enqueue(request)
            api.saveDownloadId(app.packageName, downloadId)
            pollDownload(appId, app.packageName, downloadId, kind, targetVersion)
        } catch (e: Exception) {
            Log.e(TAG, "Download error: ${e.message}", e)
            set {
                it.copy(
                    tasks = it.tasks + (appId to InstallTask(kind, TaskPhase.FAILED, 0, targetVersion))
                )
            }
        }
    }

    /**
     * Re-attaches to downloads that were still running when the process was last alive, using the
     * ids [AppRepository] persisted. This is what makes an interrupted install survive a restart.
     */
    private fun resumePendingDownloads() {
        _snapshot.value.catalog.apps.forEach { app ->
            val downloadId = api.getDownloadId(app.packageName) ?: return@forEach

            // A persisted id whose APK is already fully downloaded means the previous process
            // got as far as committing an install session. The session either landed (the
            // package is now installed) or it did not; either way the broadcast that would have
            // told us is gone, so resolve it from real state instead of leaving the tile stuck
            // on "Installing" forever.
            val apkFile = stagedApkFile(app.packageName)
            if (apkFile != null && apkFile.exists() && isDownloadFinished(downloadId)) {
                if (!reconcileInstall(app.id, app.packageName)) {
                    initiateInstallProcedure(app.id, app.packageName)
                }
                return@forEach
            }

            val kind =
                if (api.getInstalledVersionCode(app.packageName) == null) TaskKind.INSTALL else TaskKind.UPDATE
            pollDownload(
                app.id,
                app.packageName,
                downloadId,
                kind,
                app.remoteVersionCode.toString(),
            )
        }
    }

    /** The on-disk location of a staged APK, or `null` when external storage is unavailable. */
    private fun stagedApkFile(packageName: String): File? {
        val dir = application.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS) ?: return null
        return File(dir, packageName)
    }

    /** Whether [downloadId] finished successfully according to `DownloadManager`. */
    private fun isDownloadFinished(downloadId: Long): Boolean {
        val cursor = downloadManager.query(DownloadManager.Query().setFilterById(downloadId))
        return cursor?.use {
            if (it.moveToFirst()) {
                it.getInt(it.getColumnIndexOrThrow(DownloadManager.COLUMN_STATUS)) ==
                    DownloadManager.STATUS_SUCCESSFUL
            } else {
                false
            }
        } ?: false
    }

    /**
     * Resolves a staged install that has no pending broadcast by asking `PackageManager` whether
     * it landed.
     *
     * @return `true` when the package is installed at or above the remote version, meaning the
     *   session succeeded and the download id has been cleared.
     */
    private fun reconcileInstall(appId: String, packageName: String): Boolean {
        val installedVersion = api.getInstalledVersionCode(packageName)
        val remoteVersion = appRemoteVersionCode(appId)
        val landed = installedVersion != null &&
            CatalogMapper.isUpToDate(installedVersion, remoteVersion)
        api.clearDownloadId(packageName)
        set { current ->
            val installed = current.installed + (
                appId to InstalledRecord(
                    CatalogMapper.installedVersionLabel(installedVersion ?: 0, remoteVersion),
                    justCompleted = landed,
                )
                )
            current.copy(
                installed = if (landed || installedVersion != null) installed else current.installed,
                tasks = current.tasks - appId,
            )
        }
        return landed
    }

    /**
     * Polls `DownloadManager` once a second and mirrors its status into the snapshot's task,
     * then hands the finished APK to `PackageInstaller`.
     *
     * Ported 1:1 from `AppDetailViewModel.checkDownloadStatusAndPoll`; the progress encoding
     * (`-1` none, `-2` indeterminate) is preserved and normalised to `0` here because the
     * snapshot's [InstallTask.progress] is a plain percentage the UI already renders.
     */
    private fun pollDownload(
        appId: String,
        packageName: String,
        downloadId: Long,
        kind: TaskKind,
        targetVersion: String,
    ) {
        pollingJobs.remove(appId)?.cancel()
        pollingJobs[appId] = scope.launch {
            while (isActive) {
                val query = DownloadManager.Query().setFilterById(downloadId)
                val cursor: Cursor? = downloadManager.query(query)
                var status = DownloadManager.STATUS_FAILED
                var totalBytes = 0
                var downloadedBytes = 0
                var foundInManager = false

                cursor?.use {
                    if (it.moveToFirst()) {
                        foundInManager = true
                        status = it.getInt(it.getColumnIndexOrThrow(DownloadManager.COLUMN_STATUS))
                        totalBytes =
                            it.getInt(it.getColumnIndexOrThrow(DownloadManager.COLUMN_TOTAL_SIZE_BYTES))
                        downloadedBytes =
                            it.getInt(it.getColumnIndexOrThrow(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR))
                    }
                }

                if (!foundInManager) {
                    Log.v(TAG, "Download ID $downloadId not found in manager. Package: $packageName")
                    api.clearDownloadId(packageName)
                    set { it.copy(tasks = it.tasks - appId) }
                    refreshInstalled()
                    break
                }

                when (status) {
                    DownloadManager.STATUS_PENDING -> {
                        setTask(appId, kind, TaskPhase.DOWNLOADING, 0, targetVersion)
                    }

                    DownloadManager.STATUS_RUNNING, DownloadManager.STATUS_PAUSED -> {
                        val progress =
                            if (totalBytes > 0) (downloadedBytes * 100L / totalBytes).toInt() else 0
                        setTask(appId, kind, TaskPhase.DOWNLOADING, progress, targetVersion)
                    }

                    DownloadManager.STATUS_SUCCESSFUL -> {
                        setTask(appId, kind, TaskPhase.INSTALLING, 100, targetVersion)
                        initiateInstallProcedure(appId, packageName)
                        break
                    }

                    DownloadManager.STATUS_FAILED -> {
                        Log.e(TAG, "Download failed for $packageName")
                        api.clearDownloadId(packageName)
                        setTask(appId, kind, TaskPhase.FAILED, 0, targetVersion)
                        break
                    }
                }
                delay(POLL_INTERVAL_MS)
            }
        }
    }

    /**
     * The `ACTION_DOWNLOAD_COMPLETE` path: confirms the status through `DownloadManager` and then
     * installs. Ported from `AppDetailViewModel.onDownloadCompletedByReceiver`.
     */
    private fun onDownloadCompletedByReceiver(downloadId: Long) {
        val entry = _snapshot.value.catalog.apps.firstOrNull { app ->
            api.getDownloadId(app.packageName) == downloadId
        } ?: return

        val kind = _snapshot.value.tasks[entry.id]?.kind ?: TaskKind.INSTALL
        pollingJobs.remove(entry.id)?.cancel()

        val query = DownloadManager.Query().setFilterById(downloadId)
        val cursor = downloadManager.query(query)
        cursor?.use {
            if (it.moveToFirst()) {
                val status = it.getInt(it.getColumnIndexOrThrow(DownloadManager.COLUMN_STATUS))
                if (status == DownloadManager.STATUS_SUCCESSFUL) {
                    setTask(entry.id, kind, TaskPhase.INSTALLING, 100, entry.remoteVersionCode.toString())
                    initiateInstallProcedure(entry.id, entry.packageName)
                    return
                }
            }
        }
        api.clearDownloadId(entry.packageName)
        setTask(entry.id, kind, TaskPhase.FAILED, 0, entry.remoteVersionCode.toString())
    }

    /**
     * Streams the downloaded APK into a `PackageInstaller` session and commits it.
     *
     * Ported 1:1 from the install block of the reference app's `AppDetailScreen`: same
     * `MODE_FULL_INSTALL` session, same `openWrite` copy, same `PendingIntent` carrying
     * `EXTRA_PACKAGE_NAME` back to [ACTION_INSTALL_COMPLETE]. The only change is that it is
     * called from the repository instead of a composable.
     */
    /**
     * Which install route this build has available.
     *
     * `INSTALL_PACKAGES` is granted by `init` at boot, and only to an app that is already a priv-app
     * on the system partition. A platform-signed APK pushed with `adb install` still lands in
     * `/data/app` and is therefore never granted it — signing satisfies the *signature* half of
     * `signature|privileged`, privileged placement satisfies the other half.
     *
     * When privileged is unavailable we fall back to `REQUEST_INSTALL_PACKAGES` plus one-off user
     * consent, which is what makes the store installable on a stock emulator during development.
     */
    private fun installRoute(): InstallRoute {
        val privileged = application.packageManager.checkPermission(
            android.Manifest.permission.INSTALL_PACKAGES,
            application.packageName,
        ) == android.content.pm.PackageManager.PERMISSION_GRANTED

        val route = if (privileged) {
            InstallRoute.PRIVILEGED
        } else {
            resolveInstallRoute(
                hasInstallPackages = false,
                canRequestInstalls = application.packageManager.canRequestPackageInstalls(),
            )
        }

        if (route != InstallRoute.PRIVILEGED) {
            Log.w(
                TAG,
                "Not running as a priv-app (codePath is /data/app), so INSTALL_PACKAGES is not " +
                    "granted. Falling back to REQUEST_INSTALL_PACKAGES user consent: route=$route. " +
                    "This is a development path; a head unit should ship as a priv-app.",
            )
        }
        return route
    }

    /**
     * Sends the user to the "install unknown apps" screen once.
     *
     * Launched from the repository with `NEW_TASK` so no UI file has to change; the install is
     * picked up again by [onAppResumed] when the user comes back having granted it.
     */
    private fun requestInstallPermission(appId: String, packageName: String) {
        val intent = Intent(
            android.provider.Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES,
            Uri.parse("package:${application.packageName}"),
        ).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

        try {
            application.startActivity(intent)
            awaitingConsent = appId
            Log.i(TAG, "Requested unknown-sources consent for ${application.packageName}")
        } catch (e: Exception) {
            Log.e(TAG, "Could not open the unknown-sources settings screen", e)
            failTask(appId, "Could not open the install-permission settings screen.")
        }
    }

    /**
     * Marks [appId] failed, preserving its kind and target version so the UI's Retry action still
     * knows what it is retrying.
     *
     * The staged APK and the persisted download id are deliberately kept: retrying a download that
     * already finished should go straight to the install step rather than fetching the APK again.
     */
    private fun failTask(appId: String, reason: String) {
        Log.w(TAG, "Install task for $appId failed: $reason")
        val task = _snapshot.value.tasks[appId]
        setTask(
            appId,
            task?.kind ?: TaskKind.INSTALL,
            TaskPhase.FAILED,
            0,
            task?.targetVersion ?: "0",
        )
    }

    private fun initiateInstallProcedure(appId: String, packageName: String) {
        api.getDownloadId(packageName) ?: run {
            set { it.copy(tasks = it.tasks - appId) }
            refreshInstalled()
            return
        }

        val apkFile = stagedApkFile(packageName)

        if (apkFile == null || !apkFile.exists()) {
            Log.e(TAG, "APK file not found for $packageName")
            failTask(appId, "Downloaded APK file not found for installation.")
            return
        }

        // PRIVILEGED needs nothing further; USER_CONSENT has already been granted by the user;
        // NEEDS_CONSENT must ask first and resume from onAppResumed().
        when (installRoute()) {
            InstallRoute.NEEDS_CONSENT -> {
                requestInstallPermission(appId, packageName)
                return
            }

            InstallRoute.PRIVILEGED, InstallRoute.USER_CONSENT -> Unit
        }

        try {
            val apkUri = FileProvider.getUriForFile(
                application,
                application.packageName + ".provider",
                apkFile,
            )

            val packageInstaller = application.packageManager.packageInstaller
            val params = PackageInstaller.SessionParams(PackageInstaller.SessionParams.MODE_FULL_INSTALL)

            // Required for a non-privileged installer: without it the platform cannot attribute the
            // session and rejects the commit with STATUS_FAILURE. A priv-app is allowed to omit it,
            // which is why the reference app never sets it — but the reference is always a priv-app.
            params.setAppPackageName(packageName)
            val sessionId = packageInstaller.createSession(params)
            val session = packageInstaller.openSession(sessionId)

            // The stage file is committed, not copied: openWrite streams it in and fsync forces
            // the bytes to disk before commit(). The reference app omits the fsync, which is fine
            // for its small APKs but can hand PackageInstaller a truncated file and fail the
            // install with a parse error on a larger one.
            val bytes = application.contentResolver.openInputStream(apkUri)?.use { input ->
                session.openWrite("app_install", 0, -1).use { out ->
                    val copied = input.copyTo(out)
                    session.fsync(out)
                    out.flush()
                    copied
                }
            }

            if (bytes == null || bytes <= 0L) {
                // Never commit an empty session: it would surface as an opaque failure.
                session.abandon()
                session.close()
                failTask(appId, "Could not read the downloaded APK.")
                return
            }

            val broadcastIntent = Intent(ACTION_INSTALL_COMPLETE).apply {
                putExtra(PackageInstaller.EXTRA_PACKAGE_NAME, packageName)
                // Explicit, so that the MUTABLE PendingIntent used on the consent route is legal on
                // Android 14+ (an implicit one would be hijackable). Targeting our own package keeps
                // the result inside this app without a manifest receiver.
                setPackage(application.packageName)
            }

            // The mutability flag is route-dependent.
            //
            // PRIVILEGED installs silently, so an immutable PendingIntent is correct and safer.
            //
            // USER_CONSENT cannot: on that route the platform hands back a confirmation Intent in
            // EXTRA_INTENT by filling in *this* PendingIntent, and an immutable one cannot be
            // filled in — the session simply never gets confirmed. This is the same Android 14 area
            // the reference app's "fix: Android 14 Intent" commit touched; it went the other way
            // because it is always privileged and never needs the confirmation.
            val consentRoute = installRoute() != InstallRoute.PRIVILEGED
            val pendingIntentFlags = android.app.PendingIntent.FLAG_UPDATE_CURRENT or
                if (consentRoute) {
                    android.app.PendingIntent.FLAG_MUTABLE
                } else {
                    android.app.PendingIntent.FLAG_IMMUTABLE
                }

            val pendingIntent = android.app.PendingIntent.getBroadcast(
                application,
                sessionId,
                broadcastIntent,
                pendingIntentFlags,
            )

            // Only clear the consent latch once the session is genuinely committed, so a failure
            // here does not lose the fact that the user still has to grant access.
            session.commit(pendingIntent.intentSender)
            session.close()
            awaitingConsent = null

            Log.d(TAG, "Install session $sessionId committed for $packageName ($bytes bytes)")
        } catch (e: SecurityException) {
            Log.e(TAG, "Not permitted to install $packageName", e)
            api.clearDownloadId(packageName)
            failTask(appId, "Installation was refused: this build is neither privileged nor allowed to install.")
        } catch (e: Exception) {
            Log.e(TAG, "Install failed for $packageName", e)
            api.clearDownloadId(packageName)
            failTask(appId, "Installation failed: ${e.message}")
        }
    }

    /**
     * The install either landed or was cancelled: drop the download id, then re-read
     * `PackageManager` so the tile flips to installed/up-to-date/failed from real state.
     *
     * Ported 1:1 from `AppDetailViewModel.onInstallCompletedOrCancelled`, with one addition: the
     * `PackageInstaller.EXTRA_STATUS` the reference app reads is passed in as [succeeded], so a
     * user-cancelled or rejected session is reported as a failure rather than being re-derived
     * from a version comparison that could not tell the difference.
     */
    private fun onInstallCompletedOrCancelled(packageName: String, succeeded: Boolean) {
        val appId = _snapshot.value.catalog.apps
            .firstOrNull { it.packageName == packageName }?.id ?: return
        api.clearDownloadId(packageName)
        pollingJobs.remove(appId)?.cancel()
        refreshInstalled()
        set { current ->
            val installedVersion = api.getInstalledVersionCode(packageName)
            val remoteVersion = appRemoteVersionCode(appId)
            val installed = succeeded && installedVersion != null &&
                CatalogMapper.isUpToDate(installedVersion, remoteVersion)
            if (installed) {
                // Flag it so the tile briefly reads "Installed" rather than "Up to date", which
                // is what the UI does for a version that just landed in this session.
                current.copy(
                    tasks = current.tasks - appId,
                    installed = current.installed + (
                        appId to InstalledRecord(
                            CatalogMapper.installedVersionLabel(installedVersion, remoteVersion),
                            justCompleted = true,
                        )
                        ),
                )
            } else {
                current.copy(
                    tasks = current.tasks + (
                        appId to InstallTask(
                            TaskKind.UPDATE,
                            TaskPhase.FAILED,
                            0,
                            appRemoteVersionCode(appId).toString(),
                        )
                        )
                )
            }
        }
    }

    private fun appRemoteVersionCode(appId: String): Int =
        _snapshot.value.catalog.apps.firstOrNull { it.id == appId }?.remoteVersionCode ?: 0

    private fun setTask(
        appId: String,
        kind: TaskKind,
        phase: TaskPhase,
        progress: Int,
        targetVersion: String,
    ) {
        set { it.copy(tasks = it.tasks + (appId to InstallTask(kind, phase, progress, targetVersion))) }
    }

    private fun set(updater: (StoreSnapshot) -> StoreSnapshot) {
        _snapshot.value = updater(_snapshot.value)
    }

    /**
     * Re-reads real install state for any app whose task is still in the INSTALLING phase.
     *
     * The reference app re-runs `determineActionButtonState()` every time its detail screen
     * resumes, because the confirmation dialog lives in another task and the result broadcast is
     * the only other signal. This app cannot hook that from a composable without editing UI, so
     * [Activity.onResume] calls this instead — it is the same reconciliation at the same moment.
     *
     * It is what stops a tile sitting on "Installing" forever when the result broadcast is lost
     * (process death, or the session being aborted while the app was backgrounded).
     */
    fun onAppResumed() {
        refreshInstalled()

        // The user has just come back from the unknown-sources Settings screen. If they granted
        // it, carry on with the install that was waiting; if not, surface a retryable failure
        // instead of leaving the tile spinning.
        awaitingConsent?.let { appId ->
            val app = _snapshot.value.catalog.apps.firstOrNull { it.id == appId }
            if (app == null) {
                awaitingConsent = null
                return@let
            }
            val installedVersion = api.getInstalledVersionCode(app.packageName)
            if (installedVersion != null && installedVersion >= app.remoteVersionCode) {
                awaitingConsent = null
                return@let
            }
            when (installRoute()) {
                InstallRoute.NEEDS_CONSENT -> {
                    Log.i(TAG, "Returned from settings without granting install access")
                    awaitingConsent = null
                    failTask(appId, "Permission to install apps was not granted.")
                }

                InstallRoute.PRIVILEGED, InstallRoute.USER_CONSENT -> {
                    Log.i(TAG, "Install access granted; resuming $appId")
                    awaitingConsent = null
                    setTask(
                        appId,
                        _snapshot.value.tasks[appId]?.kind ?: TaskKind.INSTALL,
                        TaskPhase.INSTALLING,
                        100,
                        app.remoteVersionCode.toString(),
                    )
                    initiateInstallProcedure(appId, app.packageName)
                }
            }
        }

        _snapshot.value.tasks.keys.toList().forEach { appId ->
            val task = _snapshot.value.tasks[appId] ?: return@forEach
            if (task.phase != TaskPhase.INSTALLING) return@forEach
            val app = _snapshot.value.catalog.apps.firstOrNull { it.id == appId } ?: return@forEach

            val installedVersion = api.getInstalledVersionCode(app.packageName)
            if (installedVersion != null &&
                CatalogMapper.isUpToDate(installedVersion, app.remoteVersionCode)
            ) {
                // It landed while we were away.
                api.clearDownloadId(app.packageName)
                set { current ->
                    current.copy(
                        tasks = current.tasks - appId,
                        installed = current.installed + (
                            appId to InstalledRecord(
                                CatalogMapper.installedVersionLabel(
                                    installedVersion,
                                    app.remoteVersionCode,
                                ),
                                justCompleted = true,
                            )
                            ),
                    )
                }
                return@forEach
            }

            // Still not installed. If the APK is gone or the download is no longer pending, the
            // session died without a broadcast; surface a retryable failure rather than a spinner.
            val downloadId = api.getDownloadId(app.packageName)
            val apk = stagedApkFile(app.packageName)
            val stillDownloading = downloadId != null && !isDownloadFinished(downloadId)
            if (!stillDownloading && (apk == null || !apk.exists())) {
                api.clearDownloadId(app.packageName)
                setTask(appId, task.kind, TaskPhase.FAILED, 0, task.targetVersion)
            }
        }
    }

    /** Cancels every polling job and releases the receivers. */
    fun dispose() {
        jobs.values.forEach(Job::cancel)
        pollingJobs.values.forEach(Job::cancel)
        unregisterReceivers()
    }
}
