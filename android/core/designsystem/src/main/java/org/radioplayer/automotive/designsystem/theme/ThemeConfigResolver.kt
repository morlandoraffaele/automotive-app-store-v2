package org.radioplayer.automotive.designsystem.theme

import android.graphics.Typeface
import androidx.compose.material.ripple.RippleAlpha
import androidx.compose.material3.RippleConfiguration
import androidx.compose.ui.text.font.DeviceFontFamilyName
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import org.radioplayer.automotive.designsystem.subsystems.ColorSchemes
import org.radioplayer.automotive.designsystem.subsystems.Typography
import org.radioplayer.automotive.designsystem.tokens.ColorSchemeOEM
import org.radioplayer.automotive.designsystem.tokens.PaletteTokens

internal object ThemeConfigResolver {

    fun resolveColorSchemeOEM(palette: PaletteTokens?, isInDarkTheme: Boolean): ColorSchemeOEM =
        (palette?.let { ColorSchemes.fromPalette(it) }
            ?: ColorSchemes.fromPalette()).run { if (isInDarkTheme) dark else light }

    fun resolveTypographyOEM(fontFamily: FontFamily?, typography: Typography): Typography =
        (fontFamily?.let { Typography.overrideWithFontFamily(it) } ?: typography)

    fun resolveRippleConfigurationOEM(rippleAlpha: RippleAlpha): RippleConfiguration =
        RippleConfiguration(rippleAlpha = rippleAlpha)

    fun resolveSystemFont(familyName: String?): FontFamily? {
        if (familyName.isNullOrBlank()) return null

        val deviceFont = DeviceFontFamilyName(familyName)

        return FontFamily(
            Font(familyName = deviceFont, weight = FontWeight.Thin, style = FontStyle.Normal),
            Font(familyName = deviceFont, weight = FontWeight.Light, style = FontStyle.Normal),
            Font(familyName = deviceFont, weight = FontWeight.Normal, style = FontStyle.Normal),
            Font(familyName = deviceFont, weight = FontWeight.Medium, style = FontStyle.Normal),
            Font(familyName = deviceFont, weight = FontWeight.SemiBold, style = FontStyle.Normal),
            Font(familyName = deviceFont, weight = FontWeight.Bold, style = FontStyle.Normal),
            Font(familyName = deviceFont, weight = FontWeight.Black, style = FontStyle.Normal),
            Font(familyName = deviceFont, weight = FontWeight.Normal, style = FontStyle.Italic),
            Font(familyName = deviceFont, weight = FontWeight.Bold, style = FontStyle.Italic)
        )
    }
}