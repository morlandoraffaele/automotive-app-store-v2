package org.radioplayer.automotive.designsystem.subsystems

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf

import org.radioplayer.automotive.designsystem.tokens.ColorSchemeOEM
import org.radioplayer.automotive.designsystem.tokens.DefaultPaletteTokens
import org.radioplayer.automotive.designsystem.tokens.PaletteTokens
import org.radioplayer.automotive.designsystem.tokens.darkColorSchemeOEM
import org.radioplayer.automotive.designsystem.tokens.lightColorSchemeOEM
@Immutable
data class ColorSchemes (
    val light: ColorSchemeOEM,
    val dark: ColorSchemeOEM,
) {
    companion object {
        fun fromPalette(
            palette: PaletteTokens = DefaultPaletteTokens
        ): ColorSchemes = ColorSchemes(
            light = lightColorSchemeOEM(palette),
            dark = darkColorSchemeOEM(palette)
        )
    }
}

internal val LocalColorSchemeOEM = staticCompositionLocalOf { darkColorSchemeOEM() }