package com.automotive.appstore.ui.theme

import androidx.compose.material3.windowsizeclass.WindowHeightSizeClass
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import org.radioplayer.automotive.designsystem.subsystems.Typography
import android.app.Activity
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.ime
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.DpSize
import androidx.window.layout.WindowMetricsCalculator

/**
 * A [WindowSizeClass] that describes the *display*, not the currently visible slice of it.
 *
 * ## Why this exists
 *
 * The library's own `calculateWindowSizeClass(activity)` reads the window bounds and feeds them
 * straight into the height/width breakpoints, with no notion of the IME. Its only recomposition
 * trigger is `LocalConfiguration.current`, and this activity declares
 * `configChanges="...|keyboardHidden|..."`, so opening the keyboard pushes a configuration change
 * and the size class is re-derived from the keyboard-shrunk height.
 *
 * On a head unit that is the difference between an Expanded and a Compact height class, and
 * `StoreMetrics.resolve` strips the whole shell down when height goes Compact: rail labels, the
 * top-bar clock and the search hint all disappear and the type scale drops to 0.82. Typing in the
 * search box should not dismantle the UI.
 *
 * ## What this does instead
 *
 * Adds the current IME inset back onto the reported bounds, so the breakpoints are computed
 * against the height the window would have with the keyboard closed. The result is stable across
 * keyboard show/hide, while genuine resizes (rotation, multi-display, freeform) still move it.
 *
 * The `keyboardHidden` configuration change is deliberately left in `configChanges`: it is what
 * makes this recompose at all, and the IME inset is re-read on every pass.
 */
@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Composable
fun rememberStableWindowSizeClass(activity: Activity): WindowSizeClass {
    // Subscribes to configuration changes; without this read the size class is computed once.
    LocalConfiguration.current
    val density = LocalDensity.current

    val bounds = WindowMetricsCalculator.getOrCreate()
        .computeCurrentWindowMetrics(activity)
        .bounds

    // Read as state so the inset is observed rather than sampled once.
    val imeBottomPx = WindowInsets.ime.getBottom(density)

    // Width is never affected by the keyboard; only the height needs correcting.
    val size = with(density) {
        DpSize(
            width = bounds.width().toDp(),
            height = bounds.height().toDp() + imeBottomPx.toDp(),
        )
    }

    return remember(size) { WindowSizeClass.calculateFromSize(size) }
}

/**
 * Every layout dimension that has to change with the size of the window.
 *
 * The design system owns its own tokens (spacing, shapes, minimum touch areas), so those stay
 * fixed. What genuinely has to flex is the *density* of the shell: how wide the nav rail is,
 * how tall the top bar is, whether labels and secondary chrome fit at all, and how large the
 * type is. Collecting them in one object keeps every screen reading the same breakpoints
 * instead of each re-deriving them.
 */
@Immutable
data class StoreMetrics(
    val isCompactWidth: Boolean,
    val isExpandedWidth: Boolean,
    /** True when vertical space is scarce, so secondary chrome has to collapse. */
    val isShortHeight: Boolean,
    /** Horizontal/vertical padding around screen content. */
    val contentPadding: Dp,
    /** Gap between stacked sections. */
    val sectionGap: Dp,
    /** Gap between items in a row. */
    val itemGap: Dp,
    val railWidth: Dp,
    val railItemSize: Dp,
    /** Whether rail tiles show a text label under the icon. */
    val railShowsLabels: Boolean,
    val topBarHeight: Dp,
    /** Whether the top bar shows the Wi-Fi indicator and clock. */
    val topBarShowsStatus: Boolean,
    /** Whether the update-summary pill shows its text, or only the count. */
    val topBarShowsUpdateText: Boolean,
    /** Whether the catalog search row shows the "try saying..." hint. */
    val searchShowsHint: Boolean,
    /** Multiplier applied to the design system's type scale. */
    val typeScale: Float,
    /** Minimum tile width for the catalog grid. */
    val gridMinTileWidth: Dp,
    /** Width of one screenshot in the detail carousel. */
    val screenshotWidth: Dp,
    /** Max width for reading-heavy content, centred in wide windows. */
    val maxReadingWidth: Dp,
) {
    companion object {
        /** Sensible values for previews, matching a mid-size cabin display. */
        val Default = StoreMetrics(
            isCompactWidth = false,
            isExpandedWidth = true,
            isShortHeight = false,
            contentPadding = 24.dp,
            sectionGap = 24.dp,
            itemGap = 16.dp,
            railWidth = 96.dp,
            railItemSize = 84.dp,
            railShowsLabels = true,
            topBarHeight = 96.dp,
            topBarShowsStatus = true,
            topBarShowsUpdateText = true,
            searchShowsHint = true,
            typeScale = 1f,
            gridMinTileWidth = 360.dp,
            screenshotWidth = 448.dp,
            maxReadingWidth = 896.dp,
        )
    }
}

/** The metrics for the current window, provided by [StoreTheme]. */
val LocalStoreMetrics = staticCompositionLocalOf { StoreMetrics.Default }

/**
 * Applies [StoreMetrics.contentPadding] to a screen's content.
 *
 * Screens used to each remember to do this, and one of them (Settings) only applied a top
 * spacer, so its cards ran flush against both edges while every other screen had a margin. One
 * named modifier makes the margin impossible to forget and keeps the value sourced from
 * [StoreMetrics] rather than from a per-screen literal.
 *
 * System and navigation insets are *not* handled here: the shell in `MainActivity` already
 * insets its whole row by `WindowInsets.safeContent`, so adding them again would double-count.
 */
@Composable
fun Modifier.screenPadding(metrics: StoreMetrics = storeMetrics): Modifier =
    this.padding(metrics.contentPadding)

/**
 * Floor for rail tiles: the design system's `sizes.minTapArea`, which encodes the
 * driver-distraction guideline that every automotive touch target must reach.
 *
 * Adaptivity may remove chrome (labels, the clock) but must never shrink a target below this.
 */
private val MIN_RAIL_ITEM = 76.dp

/** Shorthand for `LocalStoreMetrics.current`. */
val storeMetrics: StoreMetrics
    @Composable get() = LocalStoreMetrics.current

/**
 * Resolves metrics for a [windowSizeClass].
 *
 * Two independent axes matter on a car head unit:
 *  - **Width** decides how much chrome fits side by side. Narrower than [WindowWidthSizeClass.Expanded]
 *    and the rail drops its labels, the top bar drops the clock, and the update pill collapses
 *    to a count.
 *  - **Height** decides how much chrome fits at all. A short window — a head unit with a status
 *    bar and a dock — tightens padding, shrinks the rail and top bar, and hides the purely
 *    decorative search hint. That is the first thing to go.
 */
fun StoreMetrics.Companion.resolve(windowSizeClass: WindowSizeClass): StoreMetrics {
    val compact = windowSizeClass.widthSizeClass == WindowWidthSizeClass.Compact
    val expanded = windowSizeClass.widthSizeClass == WindowWidthSizeClass.Expanded
    val short = windowSizeClass.heightSizeClass == WindowHeightSizeClass.Compact

    return Default.copy(
        isCompactWidth = compact,
        isExpandedWidth = expanded,
        isShortHeight = short,
        contentPadding = if (short || compact) 16.dp else 24.dp,
        sectionGap = if (short || compact) 16.dp else 24.dp,
        itemGap = if (short || compact) 12.dp else 16.dp,
        railWidth = when {
            // Minimal by default: the rail carries an icon and a label only, so it is sized to the
            // tile rather than to the web's 120px column of icon-over-text blocks.
            short -> 88.dp
            compact -> 80.dp
            else -> 96.dp
        },
        // Rail tiles must never go below the 76dp driver-distraction minimum, so this is floored
        // there rather than shrinking further. The tile is square with the glyph centred in it, so
        // a smaller tile still reads cleanly.
        railItemSize = when {
            short -> MIN_RAIL_ITEM
            compact -> MIN_RAIL_ITEM
            else -> 84.dp
        },
        railShowsLabels = !compact && !short,
        topBarHeight = if (short || compact) 72.dp else 96.dp,
        topBarShowsStatus = expanded && !short,
        topBarShowsUpdateText = !compact,
        searchShowsHint = expanded && !short,
        // Type is what makes a cramped layout look broken, so it scales hardest.
        typeScale = when {
            short -> 0.82f
            compact -> 0.85f
            else -> 1f
        },
        gridMinTileWidth = when {
            // 360dp is what actually produces the three-column catalog on a ~1280dp-wide
            // cabin window once the rail and content padding are subtracted. The previous
            // 420dp forced a two-column grid, so each tile was twice as wide as the web's.
            short -> 300.dp
            compact -> 260.dp
            else -> 360.dp
        },
        screenshotWidth = when {
            short -> 280.dp
            compact -> 240.dp
            else -> 448.dp
        },
        maxReadingWidth = if (compact) Dp.Unspecified else 896.dp,
    )
}

/**
 * Returns a copy of this typography with every size multiplied by [factor].
 *
 * Automotive type tokens are tuned for a wide cabin display; on a short or narrow window the
 * same sizes overflow their containers. Scaling the whole scale together, rather than
 * cherry-picking roles, keeps the type hierarchy intact.
 */
fun Typography.scaledBy(factor: Float): Typography {
    if (factor == 1f) return this
    fun TextStyle.scaled() = copy(
        fontSize = fontSize.scale(factor),
        lineHeight = lineHeight.scale(factor),
    )
    return copy(
        huge1 = huge1.scaled(),
        huge1Medium = huge1Medium.scaled(),
        huge2 = huge2.scaled(),
        huge2Medium = huge2Medium.scaled(),
        huge3 = huge3.scaled(),
        huge3Medium = huge3Medium.scaled(),
        display1 = display1.scaled(),
        display1Medium = display1Medium.scaled(),
        display2 = display2.scaled(),
        display2Medium = display2Medium.scaled(),
        display3 = display3.scaled(),
        body1 = body1.scaled(),
        body1Medium = body1Medium.scaled(),
        body2 = body2.scaled(),
        body2Medium = body2Medium.scaled(),
        body3 = body3.scaled(),
        body3Medium = body3Medium.scaled(),
        sub1 = sub1.scaled(),
        sub1Medium = sub1Medium.scaled(),
        sub2 = sub2.scaled(),
        sub2Medium = sub2Medium.scaled(),
        sub3 = sub3.scaled(),
    )
}

/** Scales an sp-based [TextUnit], leaving already-scaled units untouched. */
private fun TextUnit.scale(factor: Float): TextUnit = if (isSp) (value * factor).sp else this
