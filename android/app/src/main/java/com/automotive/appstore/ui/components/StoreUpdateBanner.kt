package com.automotive.appstore.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.automotive.appstore.data.StringKey
import com.automotive.appstore.data.StoreSelfUpdate
import com.automotive.appstore.data.StoreUpdatePhase
import com.automotive.appstore.ui.theme.StoreType
import com.automotive.appstore.ui.theme.storeColors
import com.automotive.appstore.ui.theme.translator
import org.radioplayer.automotive.designsystem.components.primitives.Text
import org.radioplayer.automotive.designsystem.components.primitives.icon.Icon
import org.radioplayer.automotive.designsystem.components.primitives.icon.IconSource
import org.radioplayer.automotive.designsystem.theme.AutomotiveTheme

/**
 * The "store update ready, restarts when parked" strip. Port of the web `StoreUpdateBanner`.
 *
 * Shown only when an update is downloaded and the banner has not been dismissed, exactly as
 * on the web.
 */
@Composable
fun StoreUpdateBanner(
    storeUpdate: StoreSelfUpdate,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val available = storeUpdate.availableVersion ?: return
    if (storeUpdate.phase != StoreUpdatePhase.READY || storeUpdate.bannerDismissed) return

    val colors = storeColors

    Surface(
        modifier = modifier.fillMaxWidth(),
        color = colors.accent,
        contentColor = colors.accentForeground,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 24.dp, end = 8.dp)
                .semantics { liveRegion = LiveRegionMode.Polite },
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Icon(
                source = IconSource.Vector(StoreIcons.Car),
                size = AutomotiveTheme.icon.primary,
                color = colors.primary,
                contentDescription = null,
            )
            Row(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                Text(
                    text = translator.t(StringKey.BANNER_STORE_READY, "version" to available),
                    // Web `text-lg` (plain, no weight).
                    style = StoreType.lg,
                    color = colors.accentForeground,
                    maxLines = 2,
                )
                Text(
                    text = "· " + translator.t(StringKey.BANNER_RESTARTS_WHEN_PARKED),
                    // Web `text-lg`.
                    style = StoreType.lg,
                    color = colors.mutedForeground,
                    maxLines = 2,
                )
            }
            IconTouchTarget(
                icon = StoreIcons.Dismiss,
                contentDescription = translator.t(StringKey.BANNER_DISMISS),
                onClick = onDismiss,
                tint = colors.mutedForeground,
            )
        }
    }
}
