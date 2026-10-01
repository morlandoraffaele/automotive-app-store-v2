package org.radioplayer.automotive.designsystem.components.composites.infobanner

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.unit.Dp
import org.radioplayer.automotive.designsystem.theme.AutomotiveTheme

object InfoBannerDefaults {

    /**
     * Figma: 32dp icon + 24dp of vertical padding either side. The banner grows past this as soon
     * as the message wraps to a second line, so it is a floor, not a fixed height.
     */
    val MinHeight: Dp
        @Composable @ReadOnlyComposable
        get() = AutomotiveTheme.measurement.sizes.minTapArea

    val CornerRadius: Dp
        @Composable @ReadOnlyComposable
        get() = AutomotiveTheme.measurement.shapes.largeIncreased

    val ContentPaddingHorizontal: Dp
        @Composable @ReadOnlyComposable
        get() = AutomotiveTheme.measurement.spaces.largeIncreased

    /**
     * Padding between the trailing slot and the banner's end edge when the trailing slot is an
     * [InfoBannerTrailing.Action]. The icon button already carries the theme's full min-tap-area
     * inset around its glyph, so only a hairline of banner padding remains on top of it
     * (Figma: `space/extra-small`). A passive [InfoBannerTrailing.Icon] keeps
     * [ContentPaddingHorizontal] on the end edge instead.
     */
    val ContentPaddingEndForAction: Dp
        @Composable @ReadOnlyComposable
        get() = AutomotiveTheme.measurement.spaces.extraSmall

    val ContentPaddingVertical: Dp
        @Composable @ReadOnlyComposable
        get() = AutomotiveTheme.measurement.spaces.large

    /** Gap between the leading slot, the message and the trailing slot. */
    val ItemSpacing: Dp
        @Composable @ReadOnlyComposable
        get() = AutomotiveTheme.measurement.spaces.large

    /** The size both icon slots are laid out at (Figma: `sys/icon/secondary`). */
    val IconSize: Dp
        @Composable @ReadOnlyComposable
        get() = AutomotiveTheme.icon.secondary

    val MessageStyle
        @Composable @ReadOnlyComposable
        get() = AutomotiveTheme.typography.body3

    @Composable
    fun colors(): InfoBannerColors = InfoBannerColors(
        container = AutomotiveTheme.colorScheme.secondaryContainer,
        content = AutomotiveTheme.colorScheme.onSurface,
    )
}
