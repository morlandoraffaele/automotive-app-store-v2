package com.automotive.appstore.data

/**
 * How this build is allowed to install other packages.
 *
 * There are two routes, and they are not alternatives to each other so much as a production path
 * and a test path:
 *
 * - [PRIVILEGED] is how the reference app (`radioplayer-automotive-appstore`) ships: the APK is
 *   signed with the AOSP platform key and installed under `/product/priv-app`, so `init` grants
 *   `INSTALL_PACKAGES` at boot with no user involvement. This is what a head unit needs.
 * - [USER_CONSENT] is the fallback for development on a stock emulator, where the app necessarily
 *   lives in `/data/app` and can never be privileged. It relies on `REQUEST_INSTALL_PACKAGES` plus
 *   the user granting "install unknown apps" once in Settings.
 */
enum class InstallRoute {
    /** `INSTALL_PACKAGES` granted by being a platform-signed priv-app. */
    PRIVILEGED,

    /** `canRequestPackageInstalls()` is true, so a session may be committed with user consent. */
    USER_CONSENT,

    /** Neither: the install cannot be attempted until the user grants unknown-sources access. */
    NEEDS_CONSENT,
}

/**
 * Resolves the available [InstallRoute].
 *
 * Pure so the decision can be unit tested without a device — this is the exact logic that decides
 * whether a `PackageInstaller` session may be committed, and getting it wrong is why installs
 * silently failed during development.
 *
 * @param hasInstallPackages whether `INSTALL_PACKAGES` is granted (priv-app route).
 * @param canRequestInstalls the result of `PackageManager.canRequestPackageInstalls()`.
 */
fun resolveInstallRoute(hasInstallPackages: Boolean, canRequestInstalls: Boolean): InstallRoute = when {
    hasInstallPackages -> InstallRoute.PRIVILEGED
    canRequestInstalls -> InstallRoute.USER_CONSENT
    else -> InstallRoute.NEEDS_CONSENT
}
