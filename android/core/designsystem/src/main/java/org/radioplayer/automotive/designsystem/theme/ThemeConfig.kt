package org.radioplayer.automotive.designsystem.theme

import androidx.compose.material.ripple.RippleAlpha
import androidx.compose.material3.RippleDefaults
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.text.font.FontFamily
import org.radioplayer.automotive.designsystem.subsystems.Blur
import org.radioplayer.automotive.designsystem.subsystems.Icon
import org.radioplayer.automotive.designsystem.subsystems.Shapes
import org.radioplayer.automotive.designsystem.subsystems.Sizes
import org.radioplayer.automotive.designsystem.subsystems.Spaces
import org.radioplayer.automotive.designsystem.subsystems.Stroke
import org.radioplayer.automotive.designsystem.subsystems.Typography
import org.radioplayer.automotive.designsystem.tokens.DefaultPaletteTokens
import org.radioplayer.automotive.designsystem.tokens.PaletteTokens

@Immutable
data class ThemeConfig (
    val palette: PaletteTokens = DefaultPaletteTokens,
    val shapes: Shapes = Shapes(),
    val fontFamily: FontFamily? = FontFamily.Default,
    val fontName: String? = null,
    val typography: Typography = Typography(),
    val spaces: Spaces = Spaces(),
    val icon: Icon = Icon(),
    val blur: Blur = Blur(),
    val stroke: Stroke = Stroke(),
    val sizes: Sizes = Sizes(),
    val rippleAlpha: RippleAlpha = RippleDefaults.RippleAlpha,
    val disabledContentAlpha: Float = DisabledContentAlphaDefault,
) {
    companion object {
        const val DisabledContentAlphaDefault = 0.38f
    }
}

internal val LocalDisabledContentAlpha =
    staticCompositionLocalOf { ThemeConfig.DisabledContentAlphaDefault }
