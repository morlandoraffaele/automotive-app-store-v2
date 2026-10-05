package com.automotive.appstore.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.automotive.appstore.data.StringKey
import com.automotive.appstore.ui.theme.StoreType
import com.automotive.appstore.ui.theme.storeColors
import com.automotive.appstore.ui.theme.storeMetrics
import com.automotive.appstore.ui.theme.translator
import org.radioplayer.automotive.designsystem.components.primitives.Text
import org.radioplayer.automotive.designsystem.components.primitives.icon.Icon
import org.radioplayer.automotive.designsystem.components.primitives.icon.IconSource
import org.radioplayer.automotive.designsystem.theme.AutomotiveTheme

/**
 * The screen header. Port of the web `TopBar`.
 *
 * Layout: an optional back button, a truncating title, then the [StoreUpdateBadge] when the store
 * itself is out of date.
 *
 * Two things the web version had here are gone. The app-update pill and "Update all" button:
 * bulk-updating every app from a header control is not a decision a driver should make by accident,
 * and the pill duplicated the count already on the Installed nav item. The Wi-Fi indicator and wall
 * clock: connectivity is the head unit's business, not the store's, and the clock duplicates what
 * the head unit already shows persistently. What remains is a single badge for the one update that
 * cannot be reached any other way — the store's own — which links to Settings, where the install
 * lives.
 */
@Composable
fun TopBar(
    title: String,
    storeUpdateAvailable: Boolean,
    onBack: (() -> Unit)?,
    onOpenSettings: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = storeColors

    Surface(
        modifier = modifier.fillMaxWidth(),
        color = colors.background,
        contentColor = colors.foreground,
    ) {
        val metrics = storeMetrics
        val rowPadding = (metrics.contentPadding * 0.75f).coerceAtLeast(12.dp)

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(metrics.topBarHeight)
                .padding(horizontal = rowPadding),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(metrics.itemGap),
        ) {
            if (onBack != null) {
                IconTouchTarget(
                    icon = StoreIcons.Back,
                    contentDescription = translator.t(StringKey.NAV_BACK),
                    onClick = onBack,
                )
            }

            Text(
                text = title,
                modifier = Modifier.weight(1f),
                // Web `text-3xl font-bold` = 33.75px; `body1Medium` is Step6 = 32sp.
                style = StoreType.xxxlBold,
                color = colors.foreground,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )

            StoreUpdateBadge(
                visible = storeUpdateAvailable,
                showText = metrics.topBarShowsUpdateText,
                onClick = onOpenSettings,
            )
        }
    }
}

/**
 * The badge announcing that the **store itself** has an update, linking to Settings.
 *
 * It is a link, not the update itself: installing the store replaces the running process, so the
 * action lives in Settings where it can be a deliberate, labelled tap rather than a header control
 * a driver can hit while swiping between screens. Nothing renders when there is no store update —
 * the badge is not a persistent "everything is fine" indicator.
 */
@Composable
private fun StoreUpdateBadge(visible: Boolean, showText: Boolean, onClick: () -> Unit) {
    if (!visible) return

    val colors = storeColors
    val label = translator.t(StringKey.TOPBAR_STORE_UPDATE_AVAILABLE)

    Row(
        modifier = Modifier
            .defaultMinSize(minHeight = AutomotiveTheme.measurement.sizes.minTapArea)
            .clip(RoundedCornerShape(colors.radiusXl))
            .background(colors.warning.copy(alpha = 0.15f))
            .clickable(onClick = onClick)
            .padding(horizontal = 20.dp)
            .semantics { liveRegion = LiveRegionMode.Polite },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Icon(
            source = IconSource.Vector(StoreIcons.UpdateAll),
            size = AutomotiveTheme.icon.primary,
            color = colors.warning,
        )
        if (showText) {
            Text(
                text = label,
                style = StoreType.lgSemibold,
                color = colors.warning,
                maxLines = 1,
            )
        }
    }
}

/** A square, icon-only touch target sized to the design system's minimum tap area. */
@Composable
fun IconTouchTarget(
    icon: ImageVector,
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    tint: Color = storeColors.foreground,
) {
    val minTap = storeMetrics.topBarHeight.coerceAtLeast(64.dp)
    Box(
        modifier = modifier
            .size(minTap)
            .clip(RoundedCornerShape(storeColors.radiusLg))
            .clickable(onClick = onClick)
            .semantics { this.contentDescription = contentDescription },
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            source = IconSource.Vector(icon),
            size = AutomotiveTheme.icon.primary,
            color = tint,
        )
    }
}
