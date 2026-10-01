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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.automotive.appstore.data.Locale
import com.automotive.appstore.data.StringKey
import com.automotive.appstore.ui.theme.StoreType
import com.automotive.appstore.ui.theme.storeColors
import com.automotive.appstore.ui.theme.storeMetrics
import com.automotive.appstore.ui.theme.translator
import org.radioplayer.automotive.designsystem.components.primitives.Text
import org.radioplayer.automotive.designsystem.components.primitives.icon.Icon
import org.radioplayer.automotive.designsystem.components.primitives.icon.IconSource
import org.radioplayer.automotive.designsystem.theme.AutomotiveTheme
import kotlinx.coroutines.delay
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.Locale as JavaLocale

/**
 * The screen header. Port of the web `TopBar`.
 *
 * Layout is unchanged from the web version: an optional back button, a truncating title, then
 * a right-hand cluster of [update summary] → [update-all / in-progress] → [wifi + clock].
 * The web `Clock` re-renders on a 15s interval; the same cadence is used here.
 */
@Composable
fun TopBar(
    title: String,
    updateCount: Int,
    activeTaskCount: Int,
    locale: Locale,
    onBack: (() -> Unit)?,
    onUpdateAll: () -> Unit,
    onOpenInstalled: () -> Unit,
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
                // Web `text-3xl` is 33.75px at the web's 18px root. `huge3Medium` is 68sp, which
                // is twice that and squeezes the title down to an ellipsis next to the
                // update pill and the clock. `body1Medium` (Step6, 32sp) is the matching role.
                // Web `text-3xl font-bold` = 33.75px; `body1Medium` is Step6 = 32sp.
                style = StoreType.xxxlBold,
                color = colors.foreground,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )


            UpdateSummary(
                updateCount = updateCount,
                showText = metrics.topBarShowsUpdateText,
                onClick = onOpenInstalled,
            )

            if (activeTaskCount > 0) {
                TouchButton(
                    label = translator.t(StringKey.TOPBAR_UPDATING, "n" to activeTaskCount),
                    onClick = {},
                    icon = StoreIcons.Updating,
                    variant = TouchVariant.SECONDARY,
                    enabled = false,
                )
            } else {
                TouchButton(
                    label = translator.t(StringKey.TOPBAR_UPDATE_ALL),
                    onClick = onUpdateAll,
                    icon = StoreIcons.UpdateAll,
                    enabled = updateCount > 0,
                )
            }

            if (metrics.topBarShowsStatus) {
                ConnectionCluster(locale = locale)
            }
        }
    }
}

/** The "N updates available" / "all up to date" pill that links to the Installed screen. */
@Composable
private fun UpdateSummary(updateCount: Int, showText: Boolean, onClick: () -> Unit) {
    val colors = storeColors
    val hasUpdates = updateCount > 0
    val label = if (hasUpdates) {
        translator.t(StringKey.TOPBAR_UPDATES_AVAILABLE, "n" to updateCount)
    } else {
        translator.t(StringKey.TOPBAR_NO_UPDATES)
    }
    val content = if (hasUpdates) colors.primary else colors.mutedForeground
    val container = if (hasUpdates) colors.primary.copy(alpha = 0.15f) else Color.Transparent

    Row(
        modifier = Modifier
            .defaultMinSize(minHeight = AutomotiveTheme.measurement.sizes.minTapArea)
            .clip(RoundedCornerShape(colors.radiusXl))
            .background(container)
            .clickable(onClick = onClick)
            .padding(horizontal = 20.dp)
            .semantics { liveRegion = LiveRegionMode.Polite },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Icon(
            source = IconSource.Vector(if (hasUpdates) StoreIcons.UpdatesAvailable else StoreIcons.AllUpToDate),
            size = AutomotiveTheme.icon.primary,
            color = if (hasUpdates) colors.primary else colors.success,
        )
        if (showText) {
            Text(
                text = label,
                // Web `text-lg font-semibold`.
                style = StoreType.lgSemibold,
                color = content,
                maxLines = 1,
            )
        } else {
            // Compact form: the icon alone, with the count exposed to accessibility.
            Text(
                text = if (hasUpdates) updateCount.toString() else "",
                // Web `text-lg font-semibold` (compact form shows the count only).
                style = StoreType.lgSemibold,
                color = content,
                maxLines = 1,
            )
        }
    }
}

/** Wi-Fi indicator plus the wall clock, matching the web top bar's right-hand cluster. */
@Composable
private fun ConnectionCluster(locale: Locale) {
    val colors = storeColors
    val time = rememberClock(locale)

    Row(
        modifier = Modifier.padding(start = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Icon(
            source = IconSource.Vector(StoreIcons.Wifi),
            size = AutomotiveTheme.icon.primary,
            color = colors.mutedForeground,
            contentDescription = translator.t(StringKey.TOPBAR_WIFI),
        )
        Text(
            text = time,
            // Web `text-xl font-semibold` = 22.5px. The previous `huge3` role is
            // Step10 = 68sp, which rendered the clock far larger than the title beside it.
            style = StoreType.clock,
            color = colors.mutedForeground,
            maxLines = 1,
        )
    }
}

/**
 * The current time, refreshed every 15 seconds like the web `Clock`.
 *
 * Ticking once a minute would be enough in practice, but keeping the web cadence means the
 * two implementations stay directly comparable.
 */
@Composable
private fun rememberClock(locale: Locale): String {
    val formatter = remember(locale) {
        DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT).withLocale(
            when (locale) {
                Locale.EN -> JavaLocale.US
                Locale.AR -> JavaLocale.forLanguageTag("ar")
            }
        )
    }
    var text by remember(formatter) { mutableStateOf(LocalTime.now().format(formatter)) }
    LaunchedEffect(formatter) {
        while (true) {
            delay(15_000)
            text = LocalTime.now().format(formatter)
        }
    }
    return text
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
