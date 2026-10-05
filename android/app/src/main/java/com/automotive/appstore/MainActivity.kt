package com.automotive.appstore

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.safeContent
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.automotive.appstore.data.AppListing
import com.automotive.appstore.data.StringKey
import com.automotive.appstore.data.getUpdatableAppIds
import com.automotive.appstore.navigation.Routes
import com.automotive.appstore.navigation.StoreDeepLink
import com.automotive.appstore.navigation.StoreDestination
import com.automotive.appstore.navigation.StoreNavigator
import com.automotive.appstore.navigation.rememberStoreNavigator
import com.automotive.appstore.ui.components.NavRail
import com.automotive.appstore.ui.components.TopBar
import com.automotive.appstore.ui.screens.AppDetailScreen
import com.automotive.appstore.ui.screens.CatalogScreen
import com.automotive.appstore.ui.screens.ChannelsScreen
import com.automotive.appstore.ui.screens.InstalledScreen
import com.automotive.appstore.ui.screens.SettingsScreen
import com.automotive.appstore.ui.theme.LocalTranslator
import com.automotive.appstore.ui.theme.StoreTheme
import com.automotive.appstore.ui.theme.rememberStableWindowSizeClass
import com.automotive.appstore.ui.theme.storeColors
import com.automotive.appstore.ui.theme.translator

/**
 * Single-activity host for the store.
 *
 * The web app's `app/layout.tsx` wraps every route in a `StoreShell`; the equivalent is this
 * activity, which always renders the nav rail, top bar and update banner around whichever
 * screen the navigator points at.
 */
class MainActivity : ComponentActivity() {

    /**
     * The store ViewModel, resolved through the activity's provider.
     *
     * This is the very same instance the composable below gets from `viewModel()`, because that
     * helper is scoped to the activity's `ViewModelStore`. Holding it here is what lets
     * [onResume] reconcile installs without any UI file having to change.
     */
    private val storeViewModel: StoreViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            // Resolved through our own helper rather than the library's
            // `calculateWindowSizeClass`: that one feeds raw window bounds into the breakpoints,
            // so the keyboard opening re-derived the size class from a shrunken height and
            // collapsed the whole shell into its short-window layout. This one is IME-aware.
            val windowSizeClass = rememberStableWindowSizeClass(this)
            StoreApp(windowSizeClass = windowSizeClass, viewModel = storeViewModel)
        }
        // The navigator is only listening once the composition above has run, so a cold-start
        // deep link is re-delivered from onResume.
        deepLinkHandled = false
    }

    private var deepLinkHandled = false

    override fun onResume() {
        super.onResume()

        // Returning from the installer's confirmation dialog is the moment the install result is
        // known; the reference app re-derives its state here for the same reason.
        storeViewModel.onAppResumed()

        if (deepLinkHandled) return
        deepLinkHandled = true
        StoreDeepLink.handleDeepLink(intent)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        StoreDeepLink.handleDeepLink(intent)
    }
}

@Composable
fun StoreApp(
    windowSizeClass: WindowSizeClass,
    viewModel: StoreViewModel = viewModel(),
) {
    val snapshot by viewModel.snapshot.collectAsStateWithLifecycle()
    val navigator = rememberStoreNavigator()
    val destination = navigator.current
    val settings = snapshot.settings
    val updateCount = getUpdatableAppIds(snapshot).size

    // Back first pops our own stack; only at the root does the system handle it.
    BackHandler(enabled = navigator.canGoBack) { navigator.pop() }

    StoreTheme(
        themeMode = settings.theme,
        locale = settings.locale,
        windowSizeClass = windowSizeClass,
    ) {
        // Arabic is right-to-left; mirrors the web app's `document.dir` handling.
        CompositionLocalProvider(
            LocalLayoutDirection provides
                if (LocalTranslator.current.isRtl) LayoutDirection.Rtl else LayoutDirection.Ltr
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .background(storeColors.background)
                    // enableEdgeToEdge() draws behind the status bar and the system dock, so
                    // the shell has to inset itself or the first row of content is unreachable.
                    .windowInsetsPadding(WindowInsets.safeContent),
            ) {
                NavRail(
                    selected = Routes.selected(destination),
                    updateCount = updateCount,
                    onSelect = navigator::select,
                )

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxSize()
                        .background(storeColors.background),
                ) {
                    TopBar(
                        title = titleFor(destination, snapshot.catalog.apps),
                        // The store's own update is the only one reachable from the header now;
                        // per-app updates live on the Installed screen, behind the nav badge.
                        storeUpdateAvailable = snapshot.storeUpdate.availableVersion != null,
                        onBack = Routes.backTarget(destination)?.let { target ->
                            { navigator.select(target) }
                        },
                        onOpenSettings = { navigator.select(StoreDestination.Settings) },
                    )
                    ScreenHost(
                        destination = destination,
                        navigator = navigator,
                        viewModel = viewModel,
                        modifier = Modifier.weight(1f),
                    )
                }
            }
        }
    }
}

/** Resolves the top-bar title for the current route, mirroring the web `useScreenInfo`. */
@Composable
private fun titleFor(destination: StoreDestination, apps: List<AppListing>): String {
    return when (destination) {
        StoreDestination.Store -> translator.t(StringKey.APP_TITLE)
        StoreDestination.Installed -> translator.t(StringKey.INSTALLED_TITLE)
        StoreDestination.Settings -> translator.t(StringKey.SETTINGS_TITLE)
        is StoreDestination.AppDetail ->
            apps.firstOrNull { it.id == destination.appId }?.name.orEmpty()
        is StoreDestination.Channels ->
            apps.firstOrNull { it.id == destination.appId }?.name.orEmpty()
    }
}

@Composable
private fun ScreenHost(
    destination: StoreDestination,
    navigator: StoreNavigator,
    viewModel: StoreViewModel,
    modifier: Modifier = Modifier,
) {
    when (destination) {
        StoreDestination.Store -> CatalogScreen(
            viewModel = viewModel,
            onOpenApp = { navigator.push(StoreDestination.AppDetail(it)) },
            modifier = modifier,
        )
        StoreDestination.Installed -> InstalledScreen(
            viewModel = viewModel,
            onOpenApp = { navigator.push(StoreDestination.AppDetail(it)) },
            onBrowseStore = { navigator.select(StoreDestination.Store) },
            modifier = modifier,
        )
        StoreDestination.Settings -> SettingsScreen(
            viewModel = viewModel,
            modifier = modifier,
        )
        is StoreDestination.AppDetail -> AppDetailScreen(
            viewModel = viewModel,
            appId = destination.appId,
            onOpenChannels = { navigator.push(StoreDestination.Channels(destination.appId)) },
            onBrowseStore = { navigator.select(StoreDestination.Store) },
            modifier = modifier,
        )
        is StoreDestination.Channels -> ChannelsScreen(
            viewModel = viewModel,
            appId = destination.appId,
            onBrowseStore = { navigator.select(StoreDestination.Store) },
            modifier = modifier,
        )
    }
}
