package com.automotive.appstore

import com.automotive.appstore.data.InstallRoute
import com.automotive.appstore.data.resolveInstallRoute
import org.junit.Test
import kotlin.test.assertEquals

/**
 * Tests for the install-route decision.
 *
 * This is the logic that decides whether a `PackageInstaller` session may be committed at all, and
 * it is the part that silently broke installs during development: the app is platform-signed but
 * lives in `/data/app`, so `INSTALL_PACKAGES` is never granted and only the consent fallback can
 * work. The matrix is worth pinning down explicitly.
 */
class InstallRouteTest {

    @Test
    fun `a priv-app takes the privileged route`() {
        assertEquals(
            InstallRoute.PRIVILEGED,
            resolveInstallRoute(hasInstallPackages = true, canRequestInstalls = false),
        )
    }

    @Test
    fun `privileged wins even when consent is also available`() {
        // A priv-app must never be sent to the Settings screen: the reference app installs with no
        // user interaction at all, and that is the behaviour a head unit depends on.
        assertEquals(
            InstallRoute.PRIVILEGED,
            resolveInstallRoute(hasInstallPackages = true, canRequestInstalls = true),
        )
    }

    @Test
    fun `an ordinary app that has been granted consent may install`() {
        assertEquals(
            InstallRoute.USER_CONSENT,
            resolveInstallRoute(hasInstallPackages = false, canRequestInstalls = true),
        )
    }

    @Test
    fun `an ordinary app without consent has to ask first`() {
        assertEquals(
            InstallRoute.NEEDS_CONSENT,
            resolveInstallRoute(hasInstallPackages = false, canRequestInstalls = false),
        )
    }

    @Test
    fun `every combination resolves to a route`() {
        listOf(true, false).forEach { privileged ->
            listOf(true, false).forEach { consent ->
                assertEquals(
                    privileged,
                    resolveInstallRoute(privileged, consent) == InstallRoute.PRIVILEGED,
                    "privileged=$privileged consent=$consent",
                )
            }
        }
    }
}