package com.automotive.appstore

import com.automotive.appstore.navigation.StoreDeepLink
import com.automotive.appstore.navigation.StoreDestination
import org.junit.After
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

/**
 * Tests for the deep link ported from the reference app:
 * `com.automotive.appstore://store/app?packageName=<package>`.
 *
 * The parsing runs through the pure overload and the delivery through the internal `dispatch`, so
 * these stay plain JVM tests — no Robolectric, no shadowed `Uri`/`Intent`.
 */
class StoreDeepLinkTest {

    private fun link(
        scheme: String? = "com.automotive.appstore",
        host: String? = "store",
        path: String? = "/app",
        packageName: String? = "com.bbc.sounds",
    ) = StoreDeepLink.destinationFor(scheme, host, path, packageName)

    @After
    fun tearDown() {
        // Drain any pending link so one test cannot leak its state into the next.
        val remove = StoreDeepLink.addListener { _: StoreDestination -> }
        remove()
    }

    @Test
    fun `a store deep link resolves to that app's detail screen`() {
        assertEquals(StoreDestination.AppDetail("com.bbc.sounds"), link())
    }

    @Test
    fun `the scheme host and path are matched case insensitively`() {
        // The reference app compares with ignoreCase = true; a head unit that lower-cases the
        // scheme must still land on the app.
        assertEquals(
            StoreDestination.AppDetail("com.bbc.sounds"),
            link(scheme = "COM.AUTOMOTIVE.APPSTORE", host = "STORE", path = "/APP"),
        )
    }

    @Test
    fun `rejects anything that is not the store app route`() {
        assertNull(link(scheme = "https"), "an http(s) URL is not a deep link")
        assertNull(link(host = "other"), "wrong host")
        assertNull(link(path = "/list"), "wrong path")
        assertNull(link(scheme = null), "no scheme")
    }

    @Test
    fun `requires a package name`() {
        assertNull(link(packageName = null), "no packageName parameter")
        assertNull(link(packageName = ""), "blank packageName")
    }

    @Test
    fun `a link to an app the catalogue does not carry is still routed`() {
        // The catalogue may not have loaded when the intent arrives; the detail screen renders
        // its own not-found state, so the link must not be swallowed here.
        val destination = link(packageName = "com.not.in.catalogue")
        assertNotNull(destination)
        assertEquals("com.not.in.catalogue", destination.appId)
    }

    @Test
    fun `a cold start link is replayed to the navigator when it subscribes`() {
        // The intent is delivered in onResume, before the first composition subscribes, so the
        // pending destination must not be dropped.
        StoreDeepLink.dispatch(StoreDestination.AppDetail("com.bbc.sounds"))

        val received = mutableListOf<StoreDestination>()
        val remove = StoreDeepLink.addListener { received += it }

        assertEquals(listOf<StoreDestination>(StoreDestination.AppDetail("com.bbc.sounds")), received)

        // A second link is delivered live, exactly once.
        StoreDeepLink.dispatch(StoreDestination.AppDetail("org.radioplayer.automotive.radio"))
        assertEquals(2, received.size)

        remove()
        StoreDeepLink.dispatch(StoreDestination.AppDetail("com.bbc.sounds"))
        assertEquals(2, received.size, "an unsubscribed navigator receives nothing")
    }
}
