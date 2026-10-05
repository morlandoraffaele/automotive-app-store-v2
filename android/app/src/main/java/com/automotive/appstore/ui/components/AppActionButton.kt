package com.automotive.appstore.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.automotive.appstore.data.AppState
import com.automotive.appstore.data.AppStatus
import com.automotive.appstore.data.StringKey
import com.automotive.appstore.ui.theme.translator

/**
 * The primary per-app call to action. Port of the web `AppActionButton`.
 *
 * Which button appears is driven entirely by [AppState.status], exactly as on the web:
 * not installed → Install, update available → Update, downloading → Cancel,
 * installing → disabled spinner, failed → Retry.
 *
 * **Nothing is rendered for the installed states.** The web button used to offer "Open" here, which
 * launched the target app from inside the store. Launching is not the store's job on a head unit —
 * the app is controlled through the launcher or the media browser, and most of this catalogue is
 * headless with no UI to start at all — so the installed states render no button rather than a
 * dead or misleading one. The callers lay out their rows around that.
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
) {
    // Installed and up to date: there is nothing for the store to do, and deliberately no button.
    if (state.status == AppStatus.INSTALLED || state.status == AppStatus.UP_TO_DATE) return

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

        AppStatus.INSTALLED, AppStatus.UP_TO_DATE -> return
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
