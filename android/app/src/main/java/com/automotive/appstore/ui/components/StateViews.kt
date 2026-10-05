package com.automotive.appstore.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.automotive.appstore.ui.theme.StoreType
import com.automotive.appstore.ui.theme.storeColors
import com.automotive.appstore.ui.theme.storeMetrics
import org.radioplayer.automotive.designsystem.components.primitives.Text
import org.radioplayer.automotive.designsystem.components.primitives.icon.Icon
import org.radioplayer.automotive.designsystem.components.primitives.icon.IconSource
import org.radioplayer.automotive.designsystem.theme.AutomotiveTheme

/**
 * Centred icon + title + body + optional action. Port of the web `StateMessage`.
 *
 * Used for every non-content state: catalog errors, empty catalogs, no search matches,
 * "no apps installed" and "app not found".
 */
@Composable
fun StateMessage(
    icon: ImageVector,
    title: String,
    modifier: Modifier = Modifier,
    body: String? = null,
    destructive: Boolean = false,
    action: (@Composable () -> Unit)? = null,
) {
    val colors = storeColors
    val iconContainer = if (destructive) {
        colors.destructive.copy(alpha = 0.15f)
    } else {
        colors.muted
    }
    val iconTint = if (destructive) colors.destructive else colors.mutedForeground

    Column(
        // Uses the shared screen padding rather than a hardcoded 24dp/48dp, so an empty state
        // sits on the same margin as the populated layout it replaces.
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = storeMetrics.contentPadding, vertical = storeMetrics.contentPadding * 2f)
            .semantics { liveRegion = LiveRegionMode.Polite },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        Box(
            modifier = Modifier
                .size(96.dp)
                .clip(CircleShape)
                .background(iconContainer),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                source = IconSource.Vector(icon),
                size = AutomotiveTheme.icon.hero,
                color = iconTint,
                contentDescription = null,
            )
        }
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = title,
                // Web `text-2xl font-bold` = 27px; `display3` is Step7 = 36sp.
                style = StoreType.xxlBold,
                color = colors.foreground,
                textAlign = TextAlign.Center,
            )
            if (body != null) {
                Text(
                    text = body,
                    // Web `text-lg`.
                    style = StoreType.lg,
                    color = colors.mutedForeground,
                    textAlign = TextAlign.Center,
                )
            }
        }
        action?.invoke()
    }
}

/**
 * The pulsing placeholder grid shown while the catalog loads. Port of `TileSkeletonGrid`.
 *
 * The web version renders `count` muted placeholder tiles; the same count is used here so
 * the layout does not jump when the real tiles arrive.
 */
@Composable
fun TileSkeletonGrid(
    count: Int = 8,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        repeat(count) { TileSkeleton() }
    }
}

/** A single shimmering placeholder tile. */
@Composable
private fun TileSkeleton() {
    val colors = storeColors
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(colors.radius3Xl))
            .background(colors.card)
            .padding(20.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        SkeletonBlock(width = 80.dp, height = 80.dp, corner = 18.dp)
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            SkeletonBlock(width = 180.dp, height = 24.dp)
            SkeletonBlock(width = 260.dp, height = 20.dp)
            SkeletonBlock(width = 120.dp, height = 20.dp)
        }
    }
}

/** A muted rounded placeholder bar. */
@Composable
fun SkeletonBlock(width: androidx.compose.ui.unit.Dp, height: androidx.compose.ui.unit.Dp, corner: androidx.compose.ui.unit.Dp = 8.dp) {
    Box(
        modifier = Modifier
            .size(width = width, height = height)
            .clip(RoundedCornerShape(corner))
            .background(storeColors.muted),
    )
}
