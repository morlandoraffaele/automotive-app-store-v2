package com.automotive.appstore.data

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.random.Random

/**
 * Single source of truth for store state, mirroring the web app's `StoreRepository`
 * interface and its `MockStoreRepository` implementation.
 *
 * The web app exposes an external store consumed with `useSyncExternalStore`; here the
 * snapshot is a [StateFlow] and the repository plays the same role, so swapping in a real
 * backend only means reimplementing the same operations against a service.
 */
class MockStoreRepository(
    private val scope: CoroutineScope,
    private val random: Random = Random.Default,
) {
    private val _snapshot = MutableStateFlow(
        StoreSnapshot(
            catalog = Catalog(CatalogStatus.LOADING, emptyList()),
            channels = MockStoreData.MOCK_CHANNELS,
            installed = MockStoreData.MOCK_INSTALLED,
            selectedChannel = MockStoreData.MOCK_SELECTED_CHANNELS,
            tasks = emptyMap(),
            settings = MockStoreData.DEFAULT_SETTINGS,
            storeUpdate = MockStoreData.INITIAL_STORE_UPDATE,
        )
    )
    val snapshot: StateFlow<StoreSnapshot> = _snapshot.asStateFlow()

    private val jobs = mutableMapOf<String, Job>()
    private val failOnce = MockStoreData.FAIL_ONCE_APP_IDS.toMutableSet()

    init {
        reloadCatalog()
    }

    fun reloadCatalog() {
        jobs.remove(CATALOG_JOB)?.cancel()
        set { it.copy(catalog = Catalog(CatalogStatus.LOADING, it.catalog.apps)) }

        val mode = _snapshot.value.settings.simulation
        if (mode == CatalogSimulation.LOADING) return

        jobs[CATALOG_JOB] = scope.launch {
            delay(CATALOG_DELAY_MS)
            set { current ->
                current.copy(
                    catalog = when (mode) {
                        CatalogSimulation.ERROR -> Catalog(CatalogStatus.ERROR, emptyList())
                        CatalogSimulation.EMPTY -> Catalog(CatalogStatus.READY, emptyList())
                        else -> Catalog(CatalogStatus.READY, MockStoreData.MOCK_APPS)
                    }
                )
            }
        }
    }

    fun install(appId: String) = startTask(appId, TaskKind.INSTALL)

    fun update(appId: String) = startTask(appId, TaskKind.UPDATE)

    fun updateAll() {
        getUpdatableAppIds(_snapshot.value).forEach { startTask(it, TaskKind.UPDATE) }
    }

    fun cancel(appId: String) {
        jobs.remove(appId)?.cancel()
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

    fun dismissStoreBanner() {
        set { it.copy(storeUpdate = it.storeUpdate.copy(bannerDismissed = true)) }
    }

    fun checkForStoreUpdate() {
        val phase = _snapshot.value.storeUpdate.phase
        if (phase == StoreUpdatePhase.CHECKING || phase == StoreUpdatePhase.DOWNLOADING) return
        jobs.remove(STORE_JOB)?.cancel()
        set { it.copy(storeUpdate = it.storeUpdate.copy(phase = StoreUpdatePhase.CHECKING)) }

        jobs[STORE_JOB] = scope.launch {
            delay(STORE_CHECK_DELAY_MS)
            val current = _snapshot.value.storeUpdate
            if (current.availableVersion != null && current.progress >= 100) {
                set {
                    it.copy(
                        storeUpdate = it.storeUpdate.copy(
                            phase = StoreUpdatePhase.READY,
                            bannerDismissed = false,
                        )
                    )
                }
                return@launch
            }

            set {
                it.copy(
                    storeUpdate = it.storeUpdate.copy(
                        availableVersion = STORE_LATEST_VERSION,
                        phase = StoreUpdatePhase.DOWNLOADING,
                        progress = 0,
                    )
                )
            }

            while (true) {
                delay(TICK_MS)
                val next = (_snapshot.value.storeUpdate.progress + 12).coerceAtMost(100)
                val done = next >= 100
                set {
                    it.copy(
                        storeUpdate = it.storeUpdate.copy(
                            progress = next,
                            phase = if (done) StoreUpdatePhase.READY else StoreUpdatePhase.DOWNLOADING,
                            bannerDismissed = false,
                        )
                    )
                }
                if (done) return@launch
            }
        }
    }

    private fun startTask(appId: String, kind: TaskKind) {
        val app = _snapshot.value.catalog.apps.firstOrNull { it.id == appId } ?: return
        jobs.remove(appId)?.cancel()
        val release = getReleaseForChannel(app, getSelectedChannelId(_snapshot.value, appId)) ?: return

        set {
            it.copy(
                tasks = it.tasks + (appId to InstallTask(kind, TaskPhase.DOWNLOADING, 0, release.version))
            )
        }

        jobs[appId] = scope.launch {
            while (true) {
                delay(TICK_MS)
                val task = _snapshot.value.tasks[appId] ?: return@launch
                val next = (task.progress + 4 + random.nextInt(0, 7)).coerceAtMost(100)

                if (appId in failOnce && next >= FAIL_AT_PROGRESS) {
                    failOnce -= appId
                    set {
                        it.copy(
                            tasks = it.tasks + (appId to task.copy(phase = TaskPhase.FAILED, progress = next))
                        )
                    }
                    return@launch
                }

                if (next >= 100) {
                    set {
                        it.copy(
                            tasks = it.tasks + (appId to task.copy(phase = TaskPhase.INSTALLING, progress = 100))
                        )
                    }
                    delay(INSTALL_PHASE_MS)
                    set { current ->
                        current.copy(
                            tasks = current.tasks - appId,
                            installed = current.installed +
                                (appId to InstalledRecord(task.targetVersion, justCompleted = true)),
                        )
                    }
                    return@launch
                }

                set { it.copy(tasks = it.tasks + (appId to task.copy(progress = next))) }
            }
        }
    }

    private fun set(updater: (StoreSnapshot) -> StoreSnapshot) {
        _snapshot.value = updater(_snapshot.value)
    }

    private companion object {
        const val CATALOG_JOB = "catalog"
        const val STORE_JOB = "store"
        const val CATALOG_DELAY_MS = 900L
        const val STORE_CHECK_DELAY_MS = 1500L
        const val TICK_MS = 250L
        const val INSTALL_PHASE_MS = 1400L
        const val FAIL_AT_PROGRESS = 55
        const val STORE_LATEST_VERSION = "3.5.0"
    }
}
