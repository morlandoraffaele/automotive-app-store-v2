package com.automotive.appstore

import com.automotive.appstore.data.AppIconName
import com.automotive.appstore.data.CatalogMapper
import com.automotive.appstore.data.CategoryId
import com.automotive.appstore.data.DEFAULT_CHANNEL_ID
import com.automotive.appstore.data.getReleaseForChannel
import com.automotive.appstore.data.remote.App
import com.automotive.appstore.data.remote.AppDetails
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

/**
 * Tests for the layer that bridges the real `store/config.json` payload to the model the UI
 * renders.
 *
 * These use the actual shape published at
 * `https://automotive.radioplayer.org/store/config.json`, so a change to that contract fails
 * here rather than as an empty catalogue on a head unit.
 */
class CatalogMapperTest {

    /** A verbatim entry from the live `config.json`. */
    private val radio = App(
        key = "org.radioplayer.automotive.radio",
        details = AppDetails(
            id = 1,
            name = "Radio",
            description = "The best Radio in the entire Milky Way",
            packageName = "org.radioplayer.automotive.radio",
            cls = "",
            apkUrl = "https://automotive.radioplayer.org/store/apps/org.radioplayer.automotive.radio/source.apk",
            icon = "https://automotive.radioplayer.org/store/apps/org.radioplayer.automotive.radio/app_icon.png",
            remoteVersionCode = 19,
        ),
    )

    /** An entry whose `cls` is a real class and whose description runs to several sentences. */
    private val bbc = App(
        key = "com.bbc.sounds",
        details = AppDetails(
            id = 4,
            name = "BBC Sounds: Radio & Podcasts",
            description = "This demonstration showcases an App Link feature. " +
                "The system identifies the tuned FM/DAB channel.",
            packageName = "com.bbc.sounds",
            cls = "com.bbc.sounds.mediabrowser.SoundsMediaBrowserService",
            apkUrl = "https://automotive.radioplayer.org/store/apps/com.bbc.sounds/source.apk",
            icon = "https://automotive.radioplayer.org/store/apps/com.bbc.sounds/app_icon.png",
            remoteVersionCode = 1,
        ),
    )

    @Test
    fun `carries the fields the installer depends on across verbatim`() {
        val listing = CatalogMapper.toListing(radio)
        assertEquals("org.radioplayer.automotive.radio", listing.packageName)
        assertEquals(radio.details.apkUrl, listing.apkUrl)
        assertEquals(radio.details.icon, listing.iconUrl)
        assertEquals(19, listing.remoteVersionCode)
        assertEquals("Radio", listing.name)
        assertEquals("The best Radio in the entire Milky Way", listing.description)
    }

    @Test
    fun `the catalogue key is the app id, so a deep link resolves to the same listing`() {
        // This is the invariant the deep link depends on: AppDetail is keyed by app id, and the
        // deep link carries a package name.
        assertEquals(radio.key, CatalogMapper.toListing(radio).id)
        assertEquals(bbc.key, CatalogMapper.toListing(bbc).id)
    }

    @Test
    fun `publishes exactly one stable release whose version is the remote versionCode`() {
        val listing = CatalogMapper.toListing(bbc)
        assertEquals(1, listing.releases.size)
        val release = listing.releases.single()
        assertEquals(DEFAULT_CHANNEL_ID, release.channelId)
        assertEquals("1", release.version)
        // The channel screen asks for a release on other channels; it must fall back, not crash.
        assertEquals("1", getReleaseForChannel(listing, "beta")?.version)
    }

    @Test
    fun `the version label round-trips through the installed record`() {
        // getAppState compares the installed and published version strings to decide
        // "update available" vs "up to date"; both must come from the same formatter.
        assertEquals("19", CatalogMapper.versionLabel(19))
        assertEquals(
            CatalogMapper.versionLabel(19),
            CatalogMapper.installedVersionLabel(19, 19),
        )
    }

    @Test
    fun `an installed version below the remote one reports an update`() {
        val listing = CatalogMapper.toListing(radio)
        val target = getReleaseForChannel(listing, DEFAULT_CHANNEL_ID)?.version
        assertEquals("19", target)
        assertTrue(
            CatalogMapper.versionLabel(18) != target,
            "18 vs 19 must not read as up to date",
        )
    }

    // --- installed-version labelling -------------------------------------------
    //
    // store/config.json publishes an Android versionCode, but many entries carry a placeholder
    // ("version": 1 for 15 of the 31 published apps) while the APK they point at is far newer.
    // The UI compares the installed version with the published one *as strings*, so recording the
    // real installed number made every freshly installed app read "update available" forever.

    @Test
    fun `a device newer than the published placeholder reads as up to date`() {
        // The live case: de.ard.audiothek publishes version 1, its APK is versionCode 21400.
        assertTrue(CatalogMapper.isUpToDate(installedVersionCode = 21400, remoteVersionCode = 1))
        assertEquals("1", CatalogMapper.installedVersionLabel(21400, 1))
    }

    @Test
    fun `the recorded version then equals the published one so the UI settles`() {
        // This is the invariant the string comparison in getAppState depends on: once installed,
        // release.version == installed.version, so the app stops offering an update.
        val listing = CatalogMapper.toListing(
            radio.copy(details = radio.details.copy(remoteVersionCode = 1))
        )
        val published = getReleaseForChannel(listing, DEFAULT_CHANNEL_ID)?.version
        val recorded = CatalogMapper.installedVersionLabel(21400, listing.remoteVersionCode)
        assertEquals(published, recorded, "installed and published versions must match when up to date")
    }

    @Test
    fun `a genuinely older device still records its own version`() {
        // Published 19, device on 18: keep the real number so the UI shows 18 -> 19.
        assertTrue(!CatalogMapper.isUpToDate(installedVersionCode = 18, remoteVersionCode = 19))
        assertEquals("18", CatalogMapper.installedVersionLabel(18, 19))
        assertTrue(
            CatalogMapper.installedVersionLabel(18, 19) != CatalogMapper.versionLabel(19),
            "an older device must still differ from the target so an update is offered",
        )
    }

    @Test
    fun `an exactly matching device is up to date`() {
        assertTrue(CatalogMapper.isUpToDate(19, 19))
        assertEquals("19", CatalogMapper.installedVersionLabel(19, 19))
    }

    @Test
    fun `an installed app with no published version does not report a bogus update`() {
        // versionCode 0 means the endpoint published nothing usable; recording the device's own
        // number would then differ from "0" and offer a permanent, impossible update.
        assertEquals("0", CatalogMapper.installedVersionLabel(21400, 0))
        assertTrue(CatalogMapper.isUpToDate(21400, 0))
    }

    @Test
    fun `derives a stable tile from the package name`() {
        val first = CatalogMapper.toListing(radio)
        val second = CatalogMapper.toListing(radio)
        assertEquals(first.category, second.category)
        assertEquals(first.icon, second.icon)
        assertEquals(first.iconColor, second.iconColor)
        assertEquals(first.developer, second.developer)
        assertEquals(first.tagline, second.tagline)
    }

    @Test
    fun `classifies by keyword and falls back to utilities`() {
        assertEquals(CategoryId.MEDIA, CatalogMapper.toListing(radio).category)
        assertEquals(AppIconName.RADIO, CatalogMapper.toListing(radio).icon)

        // "podcast" appears in the name, so the podcast rule wins over the radio rule.
        assertEquals(CategoryId.MEDIA, CatalogMapper.toListing(bbc).category)
        assertEquals(AppIconName.PODCAST, CatalogMapper.toListing(bbc).icon)

        // Matching nothing at all is filed under utilities, not dropped from the catalogue.
        val unknown = radio.copy(
            key = "com.example.thing",
            details = radio.details.copy(name = "Thing", description = "A thing."),
        )
        val listing = CatalogMapper.toListing(unknown)
        assertEquals(CategoryId.UTILITIES, listing.category)
        assertEquals(AppIconName.RADIO, listing.icon)
    }

    @Test
    fun `category and glyph always agree`() {
        // They come from one rule set precisely so a tile can never be filed under one category
        // while drawing another category's glyph.
        val samples = listOf(radio, bbc) + listOf(
            "waypoint", "chargegrid", "parkspot", "weather", "audiobook", "messenger",
            "calendar", "parked-arcade", "tollpass", "musicbox", "com.example.thing",
        ).map { key ->
            App(key = key, details = AppDetails(
                id = 1,
                name = key.substringAfterLast('.'),
                description = "",
                packageName = key,
                cls = null,
                apkUrl = "https://example.test/$key.apk",
                icon = null,
                remoteVersionCode = 1,
            ))
        }

        samples.forEach { app ->
            val listing = CatalogMapper.toListing(app)
            assertEquals(
                listing.category,
                CatalogMapper.classificationFor(app.key, app.details).category,
                "${app.key} category must come from the shared rule",
            )
            assertNotNull(listing.icon, "${app.key} must resolve to a glyph")
        }
    }

    @Test
    fun `the tagline is the first sentence, and falls back to the name when there is none`() {
        assertEquals(
            "This demonstration showcases an App Link feature",
            CatalogMapper.toListing(bbc).tagline,
        )

        val noDescription = radio.copy(details = radio.details.copy(description = ""))
        assertEquals("Radio", CatalogMapper.toListing(noDescription).tagline)
    }

    @Test
    fun `the developer is the package prefix`() {
        assertEquals("Org", CatalogMapper.toListing(radio).developer)
        assertEquals("Com", CatalogMapper.toListing(bbc).developer)
    }

    @Test
    fun `fields the endpoint does not publish are left empty rather than invented`() {
        val listing = CatalogMapper.toListing(radio)
        assertEquals(0, listing.sizeMb)
        assertTrue(listing.permissions.isEmpty())
        assertTrue(listing.screenshots.isEmpty())
    }

    @Test
    fun `maps a whole catalogue`() {
        val listings = CatalogMapper.toListings(listOf(radio, bbc))
        assertEquals(
            listOf("org.radioplayer.automotive.radio", "com.bbc.sounds"),
            listings.map { it.id },
        )
    }

    // --- launch-target selection ----------------------------------------------
    //
    // The Open button used `getLaunchIntentForPackage` alone, which only matches an activity
    // declaring MAIN *and* CATEGORY_LAUNCHER. Automotive head-unit apps routinely declare MAIN
    // without LAUNCHER (they are started by a store, never from a home screen), so the lookup
    // returned null and Open silently did nothing. The widened query fixes that; these pin the
    // rule that decides *which* of the resolved activities to start.

    /** A candidate activity, as the pure selection rule sees it. */
    private fun candidate(
        name: String,
        exported: Boolean = true,
        launcher: Boolean = false,
    ) = CatalogMapper.LaunchCandidate(
        packageName = "com.example.app",
        className = name,
        exported = exported,
        isLauncherEntry = launcher,
    )

    @Test
    fun `prefers an exported launcher activity above all others`() {
        val chosen = CatalogMapper.pickLaunchActivity(
            listOf(
                candidate("com.example.app.ShareActivity"),
                candidate("com.example.app.ui.MainActivity", launcher = true),
            )
        )
        assertEquals("com.example.app.ui.MainActivity", chosen?.className)
    }

    @Test
    fun `falls back to an exported non-launcher activity, which is the head-unit case`() {
        // MAIN without CATEGORY_LAUNCHER: exactly the pattern that used to yield no launch target.
        val chosen = CatalogMapper.pickLaunchActivity(
            listOf(
                candidate("com.example.app.ui.MainActivity"),
                candidate("com.example.app.SettingsActivity"),
            )
        )
        assertEquals("com.example.app.ui.MainActivity", chosen?.className)
    }

    @Test
    fun `never picks a non-exported activity while an exported one is available`() {
        // Starting a non-exported activity throws SecurityException, so it must not win.
        val chosen = CatalogMapper.pickLaunchActivity(
            listOf(
                candidate("com.example.app.ui.HiddenActivity", exported = false, launcher = true),
                candidate("com.example.app.ui.MainActivity"),
            )
        )
        assertEquals("com.example.app.ui.MainActivity", chosen?.className)
    }

    @Test
    fun `still attempts the first candidate when nothing is exported`() {
        // Better to try and let startActivity report the failure than to declare the app
        // unlaunchable and show a dead Open button.
        val chosen = CatalogMapper.pickLaunchActivity(
            listOf(
                candidate("com.example.app.ui.MainActivity", exported = false),
                candidate("com.example.app.Other", exported = false),
            )
        )
        assertEquals("com.example.app.ui.MainActivity", chosen?.className)
    }

    @Test
    fun `no resolved activity means no launch target`() {
        assertNull(CatalogMapper.pickLaunchActivity(emptyList()))
    }
}
