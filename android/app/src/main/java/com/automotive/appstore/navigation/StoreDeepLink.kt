package com.automotive.appstore.navigation

import android.content.Intent
import android.net.Uri
import android.util.Log

/**
 * Deep-link handling for `com.automotive.appstore://store/app?packageName=<package>`.
 *
 * The reference app (`radioplayer-automotive-appstore`) hands this intent to
 * `NavController.handleDeepLink`, with a manual `navigate("app_detail/<package>")` fallback for
 * when the URI does not match a registered route. This app has no `NavController` — it has
 * [StoreNavigator] — so the same intent is parsed here and published as a destination that
 * [StoreNavigator] picks up. The scheme/host/path/query contract is unchanged; only the
 * applicationId differs, since that is this app's package.
 */
object StoreDeepLink {

    private const val TAG = "StoreDeepLink"
    private const val EXPECTED_SCHEME = "com.automotive.appstore"
    private const val EXPECTED_HOST = "store"
    private const val EXPECTED_PATH = "/app"
    private const val QUERY_PACKAGE_NAME = "packageName"

    private val listeners = mutableSetOf<(StoreDestination) -> Unit>()

    /**
     * The most recent destination, kept so a cold-start intent — which is delivered in `onResume`
     * before the first composition has subscribed — is not dropped. The reference app sidesteps
     * this because `NavController` reads the intent itself during graph creation.
     */
    private var pending: StoreDestination.AppDetail? = null

    /**
     * Registers [listener] to receive deep-linked destinations, replaying any link that arrived
     * before this subscription. Returns the handle needed to unregister.
     */
    fun addListener(listener: (StoreDestination) -> Unit): () -> Unit {
        listeners += listener
        pending?.let {
            pending = null
            listener(it)
        }
        return { listeners -= listener }
    }

    /**
     * Extracts the destination an intent points at, or `null` when it is not a store deep link.
     *
     * The `config.json` catalogue is keyed by package name, and [StoreDestination.AppDetail] is
     * keyed by app id, which is the same package name — so the query parameter maps straight
     * across.
     */
    fun destinationFor(intent: Intent?): StoreDestination.AppDetail? {
        if (intent?.action != Intent.ACTION_VIEW) return null
        val data: Uri = intent.data ?: return null

        val destination = destinationFor(
            scheme = data.scheme,
            host = data.host,
            path = data.path,
            packageName = data.getQueryParameter(QUERY_PACKAGE_NAME),
        )
        if (destination == null) {
            Log.v(TAG, "Deep link NOT managed automatically. Check URI pattern: $data")
        } else {
            Log.i(TAG, "Handling deep link to ${destination.appId}")
        }
        return destination
    }

    /**
     * The pure part of the match: the scheme/host/path triple plus the `packageName` query.
     *
     * Split out from [destinationFor] so it can be unit tested without an Android runtime, and
     * so the accepted contract is stated in one place. It deliberately does no logging — the
     * `Intent` overload reports the rejection, and `android.util.Log` is not available in a
     * plain JVM test.
     *
     * @param uri only used by the caller for its log line.
     */
    fun destinationFor(
        scheme: String?,
        host: String?,
        path: String?,
        packageName: String?,
    ): StoreDestination.AppDetail? {
        if (!EXPECTED_SCHEME.equals(scheme, ignoreCase = true) ||
            !EXPECTED_HOST.equals(host, ignoreCase = true) ||
            !EXPECTED_PATH.equals(path, ignoreCase = true)
        ) {
            return null
        }

        if (packageName.isNullOrEmpty()) return null
        return StoreDestination.AppDetail(packageName)
    }

    /** Parses [intent] and notifies every listener when it resolves to a destination. */
    fun handleDeepLink(intent: Intent?) {
        val destination = destinationFor(intent) ?: return
        dispatch(destination)
    }

    /**
     * Delivers an already-parsed [destination] to the navigator, holding it for a later
     * subscriber if none exists yet.
     *
     * Internal so the listener bookkeeping can be tested without an Android `Intent`.
     */
    internal fun dispatch(destination: StoreDestination.AppDetail) {
        if (listeners.isEmpty()) {
            // Nobody is listening yet, so hold on to it until the navigator subscribes.
            pending = destination
            return
        }
        listeners.toList().forEach { it(destination) }
    }
}