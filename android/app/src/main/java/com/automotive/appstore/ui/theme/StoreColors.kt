package com.automotive.appstore.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.automotive.appstore.data.ThemeMode
import com.automotive.appstore.data.Translator

/**
 * The app's colour roles, ported 1:1 from the CSS custom properties in the web app's
 * `app/globals.css`.
 *
 * The design system owns spacing, shapes, typography and interaction, but the store's
 * recognisable blue-on-near-black identity lives in these roles. Rather than duplicating
 * them at every call site, the light/dark palettes below are fed into the design system's
 * [PaletteTokens], so every design-system component picks up the app colours through
 * `AutomotiveTheme.colorScheme`.
 */
@Immutable
data class StoreColors(
    val background: Color,
    val foreground: Color,
    val card: Color,
    val cardForeground: Color,
    val popover: Color,
    val popoverForeground: Color,
    val primary: Color,
    val primaryForeground: Color,
    val secondary: Color,
    val secondaryForeground: Color,
    val muted: Color,
    val mutedForeground: Color,
    val accent: Color,
    val accentForeground: Color,
    val destructive: Color,
    val destructiveForeground: Color,
    val success: Color,
    val successForeground: Color,
    val warning: Color,
    val warningForeground: Color,
    val border: Color,
    val ring: Color,
) {
    /**
     * The CSS `--radius: 1rem` scale from `globals.css`, where the base unit is 18px
     * (`html { font-size: 18px }`). Compose works in dp, so 1rem maps to 18dp and the
     * derived steps mirror the `--radius-*` custom properties.
     */
    val radiusSm: Dp = 10.8.dp
    val radiusMd: Dp = 14.4.dp
    val radiusLg: Dp = 18.dp
    val radiusXl: Dp = 25.2.dp
    val radius2Xl: Dp = 32.4.dp
    val radius3Xl: Dp = 39.6.dp

    companion object {
        /**
         * `.light` block. Values are the sRGB hex equivalents of the `oklch()` declarations.
         */
        val Day = StoreColors(
            background = Color(0xFFEDF0F4),
            foreground = Color(0xFF0B1015),
            card = Color(0xFFFFFFFF),
            cardForeground = Color(0xFF0B1015),
            popover = Color(0xFFFFFFFF),
            popoverForeground = Color(0xFF0B1015),
            primary = Color(0xFF006DA6),
            primaryForeground = Color(0xFFFDFEFF),
            secondary = Color(0xFFDDE2E6),
            secondaryForeground = Color(0xFF0B1015),
            muted = Color(0xFFE3E7EB),
            mutedForeground = Color(0xFF424850),
            accent = Color(0xFFCCE2EF),
            accentForeground = Color(0xFF0B1015),
            destructive = Color(0xFFC21725),
            destructiveForeground = Color(0xFFFDFEFF),
            success = Color(0xFF007840),
            successForeground = Color(0xFFFDFEFF),
            warning = Color(0xFFAA6A00),
            warningForeground = Color(0xFFFDFEFF),
            border = Color(0xFFC9CED4),
            ring = Color(0xFF006DA6),
        )

        /** `.dark` block. */
        val Night = StoreColors(
            background = Color(0xFF080C10),
            foreground = Color(0xFFF3F5F8),
            card = Color(0xFF12181D),
            cardForeground = Color(0xFFF3F5F8),
            popover = Color(0xFF171E24),
            popoverForeground = Color(0xFFF3F5F8),
            primary = Color(0xFF37D2F2),
            primaryForeground = Color(0xFF04212B),
            secondary = Color(0xFF21272E),
            secondaryForeground = Color(0xFFF3F5F8),
            muted = Color(0xFF1D2228),
            mutedForeground = Color(0xFFACB2B9),
            accent = Color(0xFF15323D),
            accentForeground = Color(0xFFF3F5F8),
            destructive = Color(0xFFFF6F69),
            destructiveForeground = Color(0xFF2A0B0C),
            success = Color(0xFF59D38C),
            successForeground = Color(0xFF042615),
            warning = Color(0xFFFAC053),
            warningForeground = Color(0xFF2A1D05),
            border = Color(0xFF2E343A),
            ring = Color(0xFF37D2F2),
        )

        fun of(mode: ThemeMode): StoreColors = when (mode) {
            ThemeMode.DAY -> Day
            ThemeMode.NIGHT -> Night
        }
    }
}

val LocalStoreColors = staticCompositionLocalOf { StoreColors.Night }

/** Shorthand for `LocalStoreColors.current`, available wherever the theme is in scope. */
val storeColors: StoreColors
    @Composable get() = LocalStoreColors.current

/** Translator for the active locale, provided by [StoreTheme]. */
val LocalTranslator = staticCompositionLocalOf { Translator(com.automotive.appstore.data.Locale.EN) }

/** Shorthand for `LocalTranslator.current`. */
val translator: Translator
    @Composable get() = LocalTranslator.current

