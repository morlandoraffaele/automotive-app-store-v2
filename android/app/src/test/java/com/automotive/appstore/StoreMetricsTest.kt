package com.automotive.appstore

import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowHeightSizeClass
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import com.automotive.appstore.ui.theme.StoreMetrics
import com.automotive.appstore.ui.theme.resolve
import com.automotive.appstore.ui.theme.scaledBy
import org.junit.Test
import org.radioplayer.automotive.designsystem.subsystems.Typography
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * Tests for the adaptive layout.
 *
 * These matter because the breakpoints are invisible at compile time: a mistake shows up as a
 * clipped rail or overflowing text on a real head unit, not as a build error. The sizes that
 * matter here are automotive ones — a wide-but-short cabin screen is exactly the case that
 * broke the previous fixed-dp layout.
 */
@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
class StoreMetricsTest {

    /**
     * Builds a [WindowSizeClass] through the public `calculateFromSize` factory, which is the
     * only way in: the two-argument constructor is private.
     *
     * The sizes chosen here map to the given classes by the library's own breakpoints —
     * width < 600dp is Compact, 600..840dp Medium, above that Expanded; height < 480dp is
     * Compact and above that Expanded.
     */
    private fun sizeClass(width: WindowWidthSizeClass, height: WindowHeightSizeClass): WindowSizeClass {
        val widthDp = when (width) {
            WindowWidthSizeClass.Compact -> 400.dp
            WindowWidthSizeClass.Medium -> 700.dp
            else -> 1128.dp
        }
        val heightDp = when (height) {
            WindowHeightSizeClass.Compact -> 400.dp
            WindowHeightSizeClass.Medium -> 600.dp
            else -> 800.dp
        }
        return WindowSizeClass.calculateFromSize(DpSize(widthDp, heightDp))
    }

    @Test
    fun `a wide short cabin screen collapses the vertical chrome`() {
        val metrics = StoreMetrics.resolve(
            sizeClass(WindowWidthSizeClass.Expanded, WindowHeightSizeClass.Compact)
        )

        assertTrue(metrics.isShortHeight)
        assertTrue(metrics.isExpandedWidth)
        // The purely decorative chrome has to go when height is scarce.
        assertFalse(metrics.topBarShowsStatus, "clock/wifi must drop on a short window")
        assertFalse(metrics.searchShowsHint, "search hint must drop on a short window")
        // And everything that remains has to shrink.
        assertTrue(metrics.topBarHeight < StoreMetrics.Default.topBarHeight)
        assertTrue(metrics.railItemSize < StoreMetrics.Default.railItemSize)
        assertTrue(metrics.contentPadding < StoreMetrics.Default.contentPadding)
        assertTrue(metrics.typeScale < 1f, "type must scale down, not overflow its containers")
    }

    @Test
    fun `a compact width drops rail labels and the update text`() {
        val metrics = StoreMetrics.resolve(
            sizeClass(WindowWidthSizeClass.Compact, WindowHeightSizeClass.Medium)
        )

        assertTrue(metrics.isCompactWidth)
        assertFalse(metrics.railShowsLabels, "rail labels do not fit a narrow rail")
        assertFalse(metrics.topBarShowsUpdateText, "the update pill collapses to a count")
        assertFalse(metrics.topBarShowsStatus)
        assertTrue(metrics.railItemSize >= 76.dp, "rail tiles keep the 76dp touch minimum")
    }

    @Test
    fun `a large window keeps the full experience untouched`() {
        val metrics = StoreMetrics.resolve(
            sizeClass(WindowWidthSizeClass.Expanded, WindowHeightSizeClass.Expanded)
        )

        assertTrue(metrics.railShowsLabels)
        assertTrue(metrics.topBarShowsStatus)
        assertTrue(metrics.topBarShowsUpdateText)
        assertTrue(metrics.searchShowsHint)
        assertEquals(1f, metrics.typeScale, "a large display gets the untouched automotive scale")
        assertEquals(StoreMetrics.Default, metrics)
    }

    @Test
    fun `chrome never shrinks below the driver-distraction touch minimum`() {
        val combinations = listOf(
            WindowWidthSizeClass.Compact to WindowHeightSizeClass.Compact,
            WindowWidthSizeClass.Compact to WindowHeightSizeClass.Medium,
            WindowWidthSizeClass.Compact to WindowHeightSizeClass.Expanded,
            WindowWidthSizeClass.Medium to WindowHeightSizeClass.Compact,
            WindowWidthSizeClass.Medium to WindowHeightSizeClass.Expanded,
            WindowWidthSizeClass.Expanded to WindowHeightSizeClass.Compact,
            WindowWidthSizeClass.Expanded to WindowHeightSizeClass.Medium,
            WindowWidthSizeClass.Expanded to WindowHeightSizeClass.Expanded,
        )

        combinations.forEach { (width, height) ->
            val metrics = StoreMetrics.resolve(sizeClass(width, height))
            assertTrue(
                metrics.railItemSize >= 76.dp,
                "rail tile must stay >= 76dp, was ${metrics.railItemSize} for $width/$height",
            )
            assertTrue(
                metrics.topBarHeight >= 64.dp,
                "top bar must stay hittable, was ${metrics.topBarHeight} for $width/$height",
            )
        }
    }

    @Test
    fun `a phone-sized window still honours the touch minimums`() {
        // The compact branch is the one that shrinks the most; guard it explicitly.
        val compact = StoreMetrics.resolve(
            sizeClass(WindowWidthSizeClass.Compact, WindowHeightSizeClass.Compact)
        )
        assertTrue(compact.railItemSize >= 76.dp, "rail tile ${compact.railItemSize} < 76dp")
        assertTrue(compact.topBarHeight >= 64.dp, "top bar ${compact.topBarHeight} < 64dp")
        assertFalse(compact.railShowsLabels)
    }

    @Test
    fun `scaling type by one is a no-op`() {
        assertEquals(Typography(), Typography().scaledBy(1f))
    }

    @Test
    fun `scaling type scales every role and preserves the hierarchy`() {
        val base = Typography()
        val scaled = base.scaledBy(0.8f)

        // Assert relative to the base: the tokens are absolute sp values, not multipliers.
        // (kotlin.test's tolerance overload takes Double, and these are Float, so compare deltas.)
        assertScaled(base.huge1.fontSize.value, scaled.huge1.fontSize.value)
        assertScaled(base.body1.fontSize.value, scaled.body1.fontSize.value)
        assertScaled(base.sub3.fontSize.value, scaled.sub3.fontSize.value)
        // Line height must scale with the glyphs or multi-line text overlaps.
        assertTrue(scaled.body1.lineHeight.value < base.body1.lineHeight.value)
        // Display roles must still outrank body roles after scaling.
        assertTrue(scaled.display3.fontSize.value > scaled.body1.fontSize.value)
    }

    /** Asserts [actual] is [expected] scaled by 0.8, within a small tolerance. */
    private fun assertScaled(expected: Float, actual: Float) {
        assertTrue(
            kotlin.math.abs(actual - expected * 0.8f) < 0.01f,
            "expected ${expected * 0.8f}, was $actual",
        )
    }

    private companion object {
        val Double.dp: Dp get() = Dp(this.toFloat())
        val Float.dp: Dp get() = Dp(this)
        val Int.dp: Dp get() = Dp(this.toFloat())
    }
}
