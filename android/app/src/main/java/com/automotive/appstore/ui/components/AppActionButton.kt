package com.automotive.appstore.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.automotive.appstore.data.AppState
import com.automotive.appstore.data.AppStatus
import com.automotive.appstore.data.StringKey
import com.automotive.appstore.ui.theme.translator
import kotlinx.coroutines.delay

/**
 * The primary per-app call to action. Port of the web `AppActionButton`.
 *
 * Which button appears is driven entirely by [AppState.status], exactly as on the web:
 * not installed → Install, update available → Update, downloading → Cancel,
 * installing → disabled spinner, failed → Retry, otherwise → Open.
 *
 * [onOpen] is invoked for the installed states. It returns whether the app actually started, so the
 * "Opening…" state only shows when there is something to open.
 */
@Composable
fun AppActionButton(
    appId: String,
    appName: String,
    state: AppState,
    onInstall: (String) -> Unit,
    onUpdate: (String) -> Unit,
    onCancel: (String) -> Unit,
    onRetry: (String) -> Unit,
    modifier: Modifier = Modifier,
    onOpen: (String) -> Boolean = { false },
) {

    // "Open" briefly shows a confirming state, as the web button does with `opening`. Only entered
    // when the launch really happened, so a headless app that cannot be opened does not lie.
    var opening by remember(appId) { mutableStateOf(false) }
    LaunchedEffect(opening) {
        if (opening) {
            delay(1_500)
            opening = false
        }
    }

    val (labelKey, icon, variant, enabled, onClick) = when (state.status) {
        AppStatus.NOT_INSTALLED -> Action(
            StringKey.ACTION_INSTALL, StoreIcons.Download, TouchVariant.PRIMARY, true
        ) { onInstall(appId) }

        AppStatus.UPDATE_AVAILABLE -> Action(
            StringKey.ACTION_UPDATE, StoreIcons.Download, TouchVariant.PRIMARY, true
        ) { onUpdate(appId) }

        AppStatus.DOWNLOADING -> Action(
            StringKey.ACTION_CANCEL, StoreIcons.Cancel, TouchVariant.OUTLINE, true
        ) { onCancel(appId) }

        AppStatus.INSTALLING -> Action(
            StringKey.ACTION_INSTALLING, StoreIcons.Installing, TouchVariant.SECONDARY, false
        ) {}

        AppStatus.FAILED -> Action(
            StringKey.ACTION_RETRY, StoreIcons.Retry, TouchVariant.DESTRUCTIVE, true
        ) { onRetry(appId) }

        AppStatus.INSTALLED, AppStatus.UP_TO_DATE -> Action(
            if (opening) StringKey.ACTION_OPENING else StringKey.ACTION_OPEN,
            StoreIcons.Open,
            TouchVariant.SECONDARY,
            true,
        ) { opening = onOpen(appId) }
    }

    val actionLabel = translator.t(labelKey)
    TouchButton(
        label = actionLabel,
        onClick = onClick,
        modifier = modifier
            .semantics { contentDescription = "$actionLabel $appName" },
        icon = icon,
        variant = variant,
        enabled = enabled,
    )
}

/** A resolved action-button configuration. */
private data class Action(
    val labelKey: StringKey,
    val icon: ImageVector,
    val variant: TouchVariant,
    val enabled: Boolean,
    val onClick: () -> Unit,
)
