package org.radioplayer.automotive.designsystem.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.ripple.RippleAlpha
import androidx.compose.material3.LocalRippleConfiguration
import androidx.compose.material3.RippleDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import org.radioplayer.automotive.designsystem.subsystems.Blur
import org.radioplayer.automotive.designsystem.subsystems.Icon
import org.radioplayer.automotive.designsystem.subsystems.LocalBlur
import org.radioplayer.automotive.designsystem.subsystems.LocalColorSchemeOEM
import org.radioplayer.automotive.designsystem.subsystems.LocalIcon
import org.radioplayer.automotive.designsystem.subsystems.LocalShapes
import org.radioplayer.automotive.designsystem.subsystems.LocalSizes
import org.radioplayer.automotive.designsystem.subsystems.LocalSpaces
import org.radioplayer.automotive.designsystem.subsystems.LocalStroke
import org.radioplayer.automotive.designsystem.subsystems.LocalTypography
import org.radioplayer.automotive.designsystem.subsystems.Shapes
import org.radioplayer.automotive.designsystem.subsystems.Sizes
import org.radioplayer.automotive.designsystem.subsystems.Spaces
import org.radioplayer.automotive.designsystem.subsystems.Stroke
import org.radioplayer.automotive.designsystem.subsystems.Typography
import org.radioplayer.automotive.designsystem.tokens.ColorSchemeOEM

@Composable
fun AutomotiveTheme(
    themeConfig: ThemeConfig = ThemeConfig(),
    isInDarkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorSchemeOEM =
        ThemeConfigResolver.resolveColorSchemeOEM(themeConfig.palette, isInDarkTheme)
    val fontFamily = ThemeConfigResolver.resolveSystemFont(themeConfig.fontName)
        ?: themeConfig.fontFamily
    val typographyOEM = ThemeConfigResolver.resolveTypographyOEM(fontFamily, themeConfig.typography)
    val rippleConfigurationOEM =
        ThemeConfigResolver.resolveRippleConfigurationOEM(themeConfig.rippleAlpha)

    CompositionLocalProvider(
        LocalColorSchemeOEM provides colorSchemeOEM,
        LocalTypography provides typographyOEM,
        LocalSpaces provides themeConfig.spaces,
        LocalShapes provides themeConfig.shapes,
        LocalIcon provides themeConfig.icon,
        LocalBlur provides themeConfig.blur,
        LocalStroke provides themeConfig.stroke,
        LocalSizes provides themeConfig.sizes,
        LocalRippleConfiguration provides rippleConfigurationOEM,
        LocalDisabledContentAlpha provides themeConfig.disabledContentAlpha,
        content = content
    )
}

object AutomotiveTheme {
    val colorScheme: ColorSchemeOEM
        @Composable @ReadOnlyComposable get() = LocalColorSchemeOEM.current

    val typography: Typography
        @Composable @ReadOnlyComposable get() = LocalTypography.current

    val icon: Icon
        @Composable @ReadOnlyComposable get() = LocalIcon.current

    val measurement: Measurement get() = Measurement

    val effects: Effects get() = Effects

    val stroke: Stroke
        @Composable @ReadOnlyComposable get() = LocalStroke.current

    object Measurement {
        val shapes: Shapes
            @Composable @ReadOnlyComposable get() = LocalShapes.current

        val spaces: Spaces
            @Composable @ReadOnlyComposable get() = LocalSpaces.current

        val sizes: Sizes
            @Composable @ReadOnlyComposable get() = LocalSizes.current
    }

    object Effects {
        val blur: Blur
            @Composable @ReadOnlyComposable get() = LocalBlur.current

        val alpha: RippleAlpha
            @Composable @ReadOnlyComposable get() = LocalRippleConfiguration.current?.rippleAlpha
                ?: RippleDefaults.RippleAlpha

        val disabledContentAlpha: Float
            @Composable @ReadOnlyComposable get() = LocalDisabledContentAlpha.current

    }
}
