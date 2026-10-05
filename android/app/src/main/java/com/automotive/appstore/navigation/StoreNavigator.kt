package com.automotive.appstore.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.snapshots.SnapshotStateList

/**
 * A minimal back stack for [StoreDestination].
 *
 * Selecting a top-level destination resets the stack (matching a `<Link href>` navigation
 * on the web, which pushes a fresh route), while [push] and [pop] handle drill-down and back.
 */
@Stable
class StoreNavigator(initial: StoreDestination = StoreDestination.Store) {

    internal val backStack: SnapshotStateList<StoreDestination> = mutableStateListOf(initial)

    /** The destination currently on screen. */
    val current: StoreDestination get() = backStack.last()

    /** `true` when there is somewhere to go back to. */
    val canGoBack: Boolean get() = backStack.size > 1

    /** Navigates to a top-level destination, replacing the whole stack. */
    fun select(destination: StoreDestination) {
        backStack.clear()
        backStack.add(destination)
    }

    /** Drills into a destination, keeping the current one behind it. */
    fun push(destination: StoreDestination) {
        if (backStack.lastOrNull() == destination) return
        backStack.add(destination)
    }

    /**
     * Pops one level.
     *
     * @return `true` if a destination was popped, `false` if the app should finish instead.
     */
    fun pop(): Boolean {
        if (!canGoBack) return false
        backStack.removeAt(backStack.lastIndex)
        return true
    }
}

/**
 * Remembers a [StoreNavigator] whose back stack survives configuration changes and process
 * death — on the web the URL is the source of truth for the current route, and this is the
 * closest equivalent.
 *
 * It also applies any deep link delivered through [StoreDeepLink], which is how a
 * `com.automotive.appstore://store/app?packageName=…` intent reaches the right screen. The
 * reference app relies on `NavController.handleDeepLink` for this; here the navigator performs
 * the equivalent push.
 */
@Composable
fun rememberStoreNavigator(): StoreNavigator {
    val navigator = rememberSaveable(saver = StoreNavigatorSaver) { StoreNavigator() }
    DisposableEffect(navigator) {
        val remove = StoreDeepLink.addListener { destination -> navigator.push(destination) }
        onDispose { remove() }
    }
    return navigator
}

/** Serialises a navigator's back stack to a flat list of route keys. */
private val StoreNavigatorSaver: Saver<StoreNavigator, List<String>> = Saver(
    save = { navigator -> navigator.backStack.map(::destinationKey) },
    restore = { keys ->
        StoreNavigator(keys.firstOrNull()?.let(::destinationFromKey) ?: StoreDestination.Store)
            .apply { keys.drop(1).forEach { push(destinationFromKey(it)) } }
    },
)

private fun destinationKey(destination: StoreDestination): String = when (destination) {
    StoreDestination.Store -> "store"
    StoreDestination.Installed -> "installed"
    StoreDestination.Settings -> "settings"
    is StoreDestination.AppDetail -> "app/${destination.appId}"
    is StoreDestination.Channels -> "channels/${destination.appId}"
}

private fun destinationFromKey(key: String): StoreDestination = when {
    key == "installed" -> StoreDestination.Installed
    key == "settings" -> StoreDestination.Settings
    key.startsWith("channels/") -> StoreDestination.Channels(key.removePrefix("channels/"))
    key.startsWith("app/") -> StoreDestination.AppDetail(key.removePrefix("app/"))
    else -> StoreDestination.Store
}
