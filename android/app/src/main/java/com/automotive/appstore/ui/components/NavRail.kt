package com.automotive.appstore.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.automotive.appstore.data.StringKey
import com.automotive.appstore.data.ThemeMode
import com.automotive.appstore.navigation.StoreDestination
import com.automotive.appstore.ui.theme.StoreType
import com.automotive.appstore.ui.theme.storeColors
import com.automotive.appstore.ui.theme.storeMetrics
import com.automotive.appstore.ui.theme.translator
import org.radioplayer.automotive.designsystem.components.primitives.Text
import org.radioplayer.automotive.designsystem.components.primitives.icon.Icon
import org.radioplayer.automotive.designsystem.components.primitives.icon.IconSource
import org.radioplayer.automotive.designsystem.theme.AutomotiveTheme

/** One entry in the left navigation rail. */
private data class RailItem(
    val destination: StoreDestination,
    val labelKey: StringKey,
    val icon: ImageVector,
)

/**
 * The persistent left navigation rail. Port of the web `NavRail`.
 *
 * On the web the rail is a column of 96px tiles with an optional count badge. Here the tiles
 * are floored at the design system's `minTapArea` (76dp — the same 76px minimum the web
 * `TouchButton` hard-codes) and the badge keeps the same "updates available" semantics.
 */
@Composable
fun NavRail(
    selected: StoreDestination,
    updateCount: Int,
    themeMode: ThemeMode,
    onSelect: (StoreDestination) -> Unit,
    onToggleTheme: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val items = listOf(
        RailItem(StoreDestination.Store, StringKey.NAV_STORE, StoreIcons.Store),
        RailItem(StoreDestination.Installed, StringKey.NAV_INSTALLED, StoreIcons.Installed),
        RailItem(StoreDestination.Settings, StringKey.NAV_SETTINGS, StoreIcons.Settings),
    )
    val railLabel = translator.t(StringKey.NAV_MAIN)
    val themeLabel = if (themeMode == ThemeMode.NIGHT) {
        translator.t(StringKey.THEME_TOGGLE_TO_DAY)
    } else {
        translator.t(StringKey.THEME_TOGGLE_TO_NIGHT)
    }

    val metrics = storeMetrics
    val railPadding = (metrics.railItemSize - 76.dp).coerceAtLeast(0.dp) / 3f

    Column(
        modifier = modifier
            .width(metrics.railWidth)
            .fillMaxHeight()
            .background(storeColors.card)
            .padding(vertical = 12.dp)
            .semantics { contentDescription = railLabel },
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        // Scrollable so that on a short window the tiles below Settings and the theme
        // toggle stay reachable instead of being pushed out of the viewport.
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(vertical = railPadding),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            items.forEach { item ->
                val badge = if (item.destination == StoreDestination.Installed) updateCount else 0
                RailTile(
                    label = translator.t(item.labelKey),
                    contentDescription = if (badge > 0) {
                        translator.t(StringKey.TOPBAR_UPDATES_AVAILABLE, "n" to badge)
                    } else {
                        translator.t(item.labelKey)
                    },
                    icon = item.icon,
                    selected = selected == item.destination,
                    badge = badge,
                    onClick = { onSelect(item.destination) },
                )
            }
        }

        RailTile(
            label = "",
            contentDescription = themeLabel,
            icon = if (themeMode == ThemeMode.NIGHT) StoreIcons.Sun else StoreIcons.Moon,
            selected = false,
            badge = 0,
            onClick = onToggleTheme,
            modifier = Modifier.padding(vertical = railPadding),
        )
    }
}

/**
 * A single rail tile: icon over label, filled with `primary` when active.
 *
 * The web version marks press feedback with `active:border-primary`; the design system's
 * `Surface` supplies the press/ripple behaviour instead, so only resting colours are set here.
 */
@Composable
private fun RailTile(
    label: String,
    contentDescription: String,
    icon: ImageVector,
    selected: Boolean,
    badge: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = storeColors
    val shape = RoundedCornerShape(colors.radiusXl)
    val container = if (selected) colors.primary else Color.Transparent
    val content = if (selected) colors.primaryForeground else colors.mutedForeground

    val metrics = storeMetrics

    Surface(
        onClick = onClick,
        modifier = modifier
            .size(metrics.railItemSize)
            .clip(shape)
            .semantics { this.contentDescription = contentDescription },
        shape = shape,
        color = container,
        contentColor = content,
    ) {
        Column(
            modifier = Modifier.padding(vertical = 6.dp, horizontal = 2.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    source = IconSource.Vector(icon),
                    size = metrics.railItemSize * 0.38f,
                    color = content,
                )
                if (badge > 0) {
                    RailBadge(
                        count = badge,
                        selected = selected,
                        modifier = Modifier.align(Alignment.TopEnd),
                    )
                }
            }
            if (label.isNotEmpty() && metrics.railShowsLabels) {
                Text(
                    text = label,
                    // Web `text-sm font-semibold` = 15.75px; `sub3` is Step1 = 18sp.
                    style = StoreType.smSemibold,
                    color = content,
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}

/** The circular count badge shown on the Installed rail item. */
@Composable
private fun RailBadge(count: Int, selected: Boolean, modifier: Modifier = Modifier) {
    val colors = storeColors
    Box(
        modifier = modifier
            .size(28.dp)
            .clip(CircleShape)
            .background(if (selected) colors.primaryForeground else colors.primary)
            .border(2.dp, colors.card, CircleShape),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = count.toString(),
            // Web `text-sm font-bold tabular-nums`.
            style = StoreType.smBold,
            color = if (selected) colors.primary else colors.primaryForeground,
            maxLines = 1,
        )
    }
}
