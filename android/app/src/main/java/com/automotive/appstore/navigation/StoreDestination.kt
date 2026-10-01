package com.automotive.appstore.navigation

/**
 * The app's destinations, mirroring the Next.js route tree:
 *
 * | Route                  | Destination                     |
 * |------------------------|---------------------------------|
 * | `/`                    | [Store]                         |
 * | `/installed`           | [Installed]                     |
 * | `/settings`            | [Settings]                      |
 * | `/apps/[id]`           | [AppDetail]                     |
 * | `/apps/[id]/channels`  | [Channels]                      |
 *
 * The web app uses the URL as navigation state and a shared nav rail for the three
 * top-level routes. A back stack gives the same behaviour without pulling in a navigation
 * library, and [StoreNavigator] owns it.
 */
sealed interface StoreDestination {
    data object Store : StoreDestination
    data object Installed : StoreDestination
    data object Settings : StoreDestination
    data class AppDetail(val appId: String) : StoreDestination
    data class Channels(val appId: String) : StoreDestination
}

/**
 * A destination that the left nav rail can select directly.
 *
 * On the web the rail is always visible and highlights the active section; [Routes.selected]
 * decides which item is highlighted for a given destination (an app detail or its channels
 * screen highlights **Store**, matching `pathname.startsWith('/apps')`).
 */
object Routes {
    val topLevel = listOf(StoreDestination.Store, StoreDestination.Installed, StoreDestination.Settings)

    /** The rail item that should read as active on [destination]. */
    fun selected(destination: StoreDestination): StoreDestination = when (destination) {
        is StoreDestination.AppDetail, is StoreDestination.Channels -> StoreDestination.Store
        else -> destination
    }

    /** Whether the top bar shows a back affordance, and where it leads. */
    fun backTarget(destination: StoreDestination): StoreDestination? = when (destination) {
        is StoreDestination.AppDetail -> StoreDestination.Store
        is StoreDestination.Channels -> StoreDestination.AppDetail(destination.appId)
        else -> null
    }
}
