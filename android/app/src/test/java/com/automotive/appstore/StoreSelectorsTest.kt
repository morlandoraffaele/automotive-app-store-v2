package com.automotive.appstore

import com.automotive.appstore.data.AppStatus
import com.automotive.appstore.data.Catalog
import com.automotive.appstore.data.CatalogStatus
import com.automotive.appstore.data.ChannelId
import com.automotive.appstore.data.DEFAULT_CHANNEL_ID
import com.automotive.appstore.data.InstalledRecord
import com.automotive.appstore.data.InstallTask
import com.automotive.appstore.data.MockStoreData
import com.automotive.appstore.data.StoreSelfUpdate
import com.automotive.appstore.data.StoreSettings
import com.automotive.appstore.data.StoreSnapshot
import com.automotive.appstore.data.StoreUpdatePhase
import com.automotive.appstore.data.TaskKind
import com.automotive.appstore.data.TaskPhase
import com.automotive.appstore.data.ThemeMode
import com.automotive.appstore.data.CatalogSimulation
import com.automotive.appstore.data.Locale
import com.automotive.appstore.data.getActiveTaskCount
import com.automotive.appstore.data.getAppState
import com.automotive.appstore.data.getReleaseForChannel
import com.automotive.appstore.data.getSelectedChannelId
import com.automotive.appstore.data.getUpdatableAppIds
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

/**
 * Unit tests for the store selectors, mirroring the behaviour of the web app's
 * `lib/store/selectors.ts` — these are pure functions of the snapshot, so they are the
 * natural seam to lock down.
 */
class StoreSelectorsTest {

    private val app = MockStoreData.MOCK_APPS.first { it.id == "waypoint" }

    private fun snapshot(
        installed: Map<String, InstalledRecord> = emptyMap(),
        selectedChannel: Map<String, ChannelId> = emptyMap(),
        tasks: Map<String, InstallTask> = emptyMap(),
    ) = StoreSnapshot(
        catalog = Catalog(CatalogStatus.READY, MockStoreData.MOCK_APPS),
        channels = MockStoreData.MOCK_CHANNELS,
        installed = installed,
        selectedChannel = selectedChannel,
        tasks = tasks,
        settings = StoreSettings(true, true, ThemeMode.NIGHT, Locale.EN, CatalogSimulation.NORMAL),
        storeUpdate = StoreSelfUpdate("3.4.1", null, StoreUpdatePhase.UP_TO_DATE, 0, false),
    )

    @Test
    fun `defaults to the stable channel`() {
        assertEquals(DEFAULT_CHANNEL_ID, getSelectedChannelId(snapshot(), "waypoint"))
    }

    @Test
    fun `honours an explicit channel selection`() {
        val s = snapshot(selectedChannel = mapOf("waypoint" to "beta"))
        assertEquals("beta", getSelectedChannelId(s, "waypoint"))
    }

    @Test
    fun `falls back to the first release when a channel has no build`() {
        // waypoint publishes on all four channels, so an unknown channel id has to fall back.
        assertEquals("5.2.0", getReleaseForChannel(app, "stable")?.version)
        assertEquals("5.2.0", getReleaseForChannel(app, "not-a-channel")?.version)
        // parkspot ships stable only, so asking it for beta falls back to stable.
        val stableOnly = MockStoreData.MOCK_APPS.first { it.id == "parkspot" }
        assertEquals("1.12.0", getReleaseForChannel(stableOnly, "beta")?.version)
    }

    @Test
    fun `an app with no record is not installed`() {
        val state = getAppState(snapshot(), app)
        assertEquals(AppStatus.NOT_INSTALLED, state.status)
        assertNull(state.currentVersion)
        assertEquals("5.2.0", state.targetVersion)
    }

    @Test
    fun `a matching version is up to date`() {
        val s = snapshot(installed = mapOf("waypoint" to InstalledRecord("5.2.0")))
        val state = getAppState(s, app)
        assertEquals(AppStatus.UP_TO_DATE, state.status)
        assertEquals(100, state.progress)
        assertNull(state.targetVersion)
    }

    @Test
    fun `a just-completed install reports installed rather than up-to-date`() {
        val s = snapshot(
            installed = mapOf("waypoint" to InstalledRecord("5.2.0", justCompleted = true))
        )
        assertEquals(AppStatus.INSTALLED, getAppState(s, app).status)
    }

    @Test
    fun `an older installed version reports an available update`() {
        val s = snapshot(installed = mapOf("waypoint" to InstalledRecord("5.1.3")))
        val state = getAppState(s, app)
        assertEquals(AppStatus.UPDATE_AVAILABLE, state.status)
        assertEquals("5.1.3", state.currentVersion)
        assertEquals("5.2.0", state.targetVersion)
    }

    @Test
    fun `an in-flight task overrides the installed record`() {
        val s = snapshot(
            installed = mapOf("waypoint" to InstalledRecord("5.1.3")),
            tasks = mapOf(
                "waypoint" to InstallTask(TaskKind.UPDATE, TaskPhase.DOWNLOADING, 42, "5.2.0")
            ),
        )
        val state = getAppState(s, app)
        assertEquals(AppStatus.DOWNLOADING, state.status)
        assertEquals(42, state.progress)
    }

    @Test
    fun `a failed task surfaces the failed status`() {
        val s = snapshot(
            tasks = mapOf(
                "waypoint" to InstallTask(TaskKind.INSTALL, TaskPhase.FAILED, 55, "5.2.0")
            ),
        )
        assertEquals(AppStatus.FAILED, getAppState(s, app).status)
    }

    @Test
    fun `a stale failure does not mask a satisfied install`() {
        // Regression: a task left in FAILED used to win over the installed record, so a correctly
        // installed app showed a Retry button with nothing to retry.
        val s = snapshot(
            installed = mapOf("waypoint" to InstalledRecord("5.2.0")),
            tasks = mapOf(
                "waypoint" to InstallTask(TaskKind.INSTALL, TaskPhase.FAILED, 55, "5.2.0")
            ),
        )
        val state = getAppState(s, app)
        assertEquals(AppStatus.UP_TO_DATE, state.status)
        assertEquals(100, state.progress)
        assertNull(state.targetVersion)
    }

    @Test
    fun `a failure on an app that is genuinely behind still reports failed`() {
        // The other half of the guard: a failed *update* of an older build must stay retryable.
        val s = snapshot(
            installed = mapOf("waypoint" to InstalledRecord("5.1.3")),
            tasks = mapOf(
                "waypoint" to InstallTask(TaskKind.UPDATE, TaskPhase.FAILED, 55, "5.2.0")
            ),
        )
        assertEquals(AppStatus.FAILED, getAppState(s, app).status)
    }

    @Test
    fun `a stale failure on an uninstalled app still reports failed`() {
        val s = snapshot(
            tasks = mapOf(
                "waypoint" to InstallTask(TaskKind.INSTALL, TaskPhase.FAILED, 55, "5.2.0")
            ),
        )
        assertEquals(AppStatus.FAILED, getAppState(s, app).status)
    }

    @Test
    fun `updatable ids include updates and failures but not in-flight work`() {
        val s = snapshot(
            installed = mapOf("waypoint" to InstalledRecord("5.1.3")),
            tasks = mapOf("relay" to InstallTask(TaskKind.UPDATE, TaskPhase.DOWNLOADING, 10, "11.3.0")),
        )
        val updatable = getUpdatableAppIds(s)
        assertEquals(listOf("waypoint"), updatable)
    }

    @Test
    fun `active task count excludes failed tasks`() {
        val s = snapshot(
            tasks = mapOf(
                "waypoint" to InstallTask(TaskKind.UPDATE, TaskPhase.DOWNLOADING, 10, "5.2.0"),
                "relay" to InstallTask(TaskKind.UPDATE, TaskPhase.FAILED, 55, "11.3.0"),
            ),
        )
        assertEquals(1, getActiveTaskCount(s))
    }

    @Test
    fun `switching channel changes the target version`() {
        val s = snapshot(
            installed = mapOf("waypoint" to InstalledRecord("5.1.3")),
            selectedChannel = mapOf("waypoint" to "beta"),
        )
        val state = getAppState(s, app)
        assertEquals("beta", state.channel?.id)
        assertEquals("5.3.0-beta.2", state.targetVersion)
    }

    @Test
    fun `the fixture is internally consistent`() {
        // Every release must reference a channel the store actually defines.
        val channelIds = MockStoreData.MOCK_CHANNELS.map { it.id }.toSet()
        MockStoreData.MOCK_APPS.forEach { listing ->
            listing.releases.forEach { release ->
                assertNotNull(
                    MockStoreData.MOCK_CHANNELS.firstOrNull { it.id == release.channelId },
                    "${listing.id} references unknown channel ${release.channelId}",
                )
            }
            assertEquals("stable", listing.releases.first().channelId, listing.id)
        }
        // Apps referenced by the initial installed/selected maps must exist.
        (MockStoreData.MOCK_INSTALLED.keys + MockStoreData.MOCK_SELECTED_CHANNELS.keys)
            .forEach { id ->
                assertNotNull(MockStoreData.MOCK_APPS.firstOrNull { it.id == id }, "unknown app $id")
            }
        assertTrue(channelIds.containsAll(listOf("stable", "beta", "develop", "alpha")))
    }
}
