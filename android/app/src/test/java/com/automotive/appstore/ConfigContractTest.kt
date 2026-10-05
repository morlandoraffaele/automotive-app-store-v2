package com.automotive.appstore

import com.automotive.appstore.data.AppType
import com.automotive.appstore.data.CatalogMapper
import com.automotive.appstore.data.STORE_APP_PACKAGE
import com.automotive.appstore.data.remote.App
import com.automotive.appstore.data.MockStoreData
import com.automotive.appstore.data.remote.AppDetails
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Parses a real captured `store/config.json` through the production mapping.
 *
 * A whole-document test on purpose: the per-entry fixtures in `CatalogMapperTest` prove the rules,
 * but nothing proved the *document* maps end to end. A field renamed or retyped server-side shows
 * up here as a mapped-listing assertion failure rather than as an empty catalogue on a head unit.
 */
class ConfigContractTest {

    /** The new document shape, captured from the endpoint. */
    private val document: Map<String, AppDetails> by lazy {
        val json = checkNotNull(javaClass.getResourceAsStream("/config.json")) {
            "config.json fixture is missing from test resources"
        }.bufferedReader().use { it.readText() }
        Gson().fromJson(json, object : TypeToken<Map<String, AppDetails>>() {}.type)
    }

    private val listings by lazy { document.map { CatalogMapper.toListing(App(it.key, it.value)) } }

    @Test
    fun `every entry maps to a listing`() {
        assertEquals(34, document.size)
        assertEquals(34, listings.size)
        listings.forEach {
            assertTrue(it.name.isNotBlank(), "${it.id} has no name")
            assertTrue(it.packageName.isNotBlank(), "${it.id} has no packageName")
            assertTrue(it.developer.isNotBlank(), "${it.id} has no developer")
            assertTrue(!it.apkUrl.isNullOrBlank(), "${it.id} has no apkUrl")
        }
    }

    @Test
    fun `no entry reads as versionCode zero`() {
        // The failure mode this guards: a renamed field silently parses as 0, which makes every app
        // look permanently up to date and every install a no-op.
        val zero = listings.filter { it.remoteVersionCode <= 0 }
        assertTrue(zero.isEmpty(), "entries parsed with versionCode 0: ${zero.map { it.id }}")
    }

    @Test
    fun `every entry carries a known type`() {
        val unknown = listings.filter { it.type == AppType.OTHER }
        assertTrue(
            unknown.isEmpty(),
            "unrecognised types (new values need adding to AppType): ${unknown.map { it.id }}",
        )
    }

    @Test
    fun `the store publishes itself, and only the store`() {
        val stores = listings.filter { it.packageName == STORE_APP_PACKAGE }
        assertEquals(1, stores.size, "the self-update entry must be present exactly once")
        assertEquals("App Store (Self Update)", stores.single().name)
    }

    @Test
    fun `published version names survive where present`() {
        val named = listings.filter { !it.versionName.isNullOrBlank() }
        assertTrue(named.isNotEmpty())
        assertTrue(named.any { it.versionName!!.contains('-') }, "expected a pre-release label")
    }

    @Test
    fun `every release sits on a channel the store defines`() {
        // Otherwise getAppState resolves no channel definition and the header silently loses it.
        val known = MockStoreData.MOCK_CHANNELS.map { it.id }.toSet()
        val orphan = listings.mapNotNull { listing ->
            listing.releases.single().channelId.takeIf { it !in known }
        }.distinct()
        assertTrue(orphan.isEmpty(), "releases on channels with no definition: $orphan")
    }
}