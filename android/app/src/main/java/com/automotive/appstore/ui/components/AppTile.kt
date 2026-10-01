package com.automotive.appstore.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.automotive.appstore.data.AppListing
import com.automotive.appstore.data.AppState
import com.automotive.appstore.data.AppStatus
import com.automotive.appstore.data.CategoryId
import com.automotive.appstore.data.StringKey
import com.automotive.appstore.ui.theme.translator
import com.automotive.appstore.ui.theme.StoreType
import com.automotive.appstore.ui.theme.storeColors
import com.automotive.appstore.ui.theme.storeMetrics
import org.radioplayer.automotive.designsystem.components.primitives.Text

/**
 * A catalog grid tile. Port of the web `AppTile`.
 *
 * Structure is unchanged: tinted app icon with a status badge on its corner, then name,
 * tagline, and a row holding the category tag plus the live status text.
 */
@Composable
fun AppTile(
    app: AppListing,
    state: AppState,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = storeColors
    val metrics = storeMetrics
    val statusLabel = statusLabelFor(state)
    val showStatusText = state.status != AppStatus.NOT_INSTALLED && state.status != AppStatus.UP_TO_DATE
    val statusTextColor = when (state.status) {
        AppStatus.FAILED -> colors.destructive
        AppStatus.INSTALLED -> colors.success
        else -> colors.primary
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(colors.radius3Xl))
            .background(colors.card)
            .border(2.dp, Color.Transparent, RoundedCornerShape(colors.radius3Xl))
            .clickable(onClick = onClick)
            .semantics {
                contentDescription = "${app.name}. ${app.tagline}. $statusLabel"
            }
            .padding(metrics.itemGap),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(metrics.itemGap),
    ) {
        Box {
            AppIconTile(icon = app.icon, color = app.iconColor)
            StatusBadge(
                status = state.status,
                progress = state.progress,
                modifier = Modifier.align(Alignment.BottomEnd),
            )
        }

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text(
                text = app.name,
                // Web `text-xl` is 22.5px at the web's 18px root. `huge3Medium` is 68sp — three
                // times too large, and the reason the tile names dominated the catalog grid.
                // Web `text-xl font-bold` = 22.5px at the web's 18px root. The design
                // system offers no bold weight and its nearest size, `body1`, is 32sp, so
                // [StoreType.tileName] carries the web value exactly.
                style = StoreType.tileName,
                color = colors.foreground,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = app.tagline,
                // Web `text-base` is 18px; `body3` is 24sp, so this
                // uses the literal web size.
                style = StoreType.tileTagline,
                color = colors.mutedForeground,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Row(
                modifier = Modifier.padding(top = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                CategoryTag(category = app.category)
                if (showStatusText) {
                    Text(
                        text = statusLabel,
                        // Web `text-sm font-semibold` = 15.75px.
                        style = StoreType.tileMeta,
                        color = statusTextColor,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
            if (state.status == AppStatus.DOWNLOADING || state.status == AppStatus.INSTALLING) {
                StatusProgressBar(
                    status = state.status,
                    progress = state.progress,
                    modifier = Modifier.padding(top = 8.dp),
                )
            }
        }
    }
}

/**
 * The muted pill showing an app's category.
 *
 * This is built directly rather than with the design system's `Tag`, whose `FILLED` colour style
 * resolves `containerColor` to `surfaceVariant` and `contentColor` to `onSurface`. In the store's
 * dark palette both resolve to light greys, so the label rendered invisible on a grey blob. The
 * web's own classes — `rounded-full bg-muted px-3 py-1 text-muted-foreground` — are reproduced
 * here with the store palette, which is also what the reference capture actually shows.
 */
@Composable
fun CategoryTag(category: CategoryId, modifier: Modifier = Modifier) {
    val colors = storeColors
    val label = translator.t(categoryLabelKey(category))

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(percent = 50))
            .background(colors.muted)
            .padding(horizontal = 12.dp, vertical = 4.dp)
            .semantics { contentDescription = label },
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = label,
            style = StoreType.tileMeta,
            color = colors.mutedForeground,
            maxLines = 1,
        )
    }
}

/** Maps a [CategoryId] to its translation key, mirroring the web `category.${id}` lookup. */
fun categoryLabelKey(category: CategoryId): StringKey = when (category) {
    CategoryId.NAVIGATION -> StringKey.CATEGORY_NAVIGATION
    CategoryId.MEDIA -> StringKey.CATEGORY_MEDIA
    CategoryId.CHARGING -> StringKey.CATEGORY_CHARGING
    CategoryId.COMMUNICATION -> StringKey.CATEGORY_COMMUNICATION
    CategoryId.UTILITIES -> StringKey.CATEGORY_UTILITIES
    CategoryId.PARKED -> StringKey.CATEGORY_PARKED
}

/**
 * The localized status string for an app state.
 *
 * Port of `getStatusLabel` from the web `app-status.tsx`, shared by every surface that shows
 * a status so the wording stays identical across tiles, rows and the detail header.
 */
@Composable
fun statusLabelFor(state: AppState): String {
    return when (state.status) {
        AppStatus.UPDATE_AVAILABLE -> state.targetVersion
            ?.let { translator.t(StringKey.STATUS_UPDATE_AVAILABLE, "version" to it) }
            ?: translator.t(StringKey.STATUS_UPDATE_AVAILABLE_SHORT)

        AppStatus.DOWNLOADING ->
            translator.t(StringKey.STATUS_DOWNLOADING, "n" to state.progress.toInt())

        AppStatus.NOT_INSTALLED -> translator.t(StringKey.STATUS_NOT_INSTALLED)
        AppStatus.INSTALLED -> translator.t(StringKey.STATUS_INSTALLED)
        AppStatus.UP_TO_DATE -> translator.t(StringKey.STATUS_UP_TO_DATE)
        AppStatus.INSTALLING -> translator.t(StringKey.STATUS_INSTALLING)
        AppStatus.FAILED -> translator.t(StringKey.STATUS_FAILED)
    }
}
