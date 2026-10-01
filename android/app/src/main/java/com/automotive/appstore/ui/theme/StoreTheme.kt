package com.automotive.appstore.ui.theme

import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.automotive.appstore.data.Locale
import com.automotive.appstore.data.ThemeMode
import com.automotive.appstore.data.Translator
import org.radioplayer.automotive.designsystem.subsystems.Typography
import org.radioplayer.automotive.designsystem.theme.AutomotiveTheme
import org.radioplayer.automotive.designsystem.theme.ThemeConfig

/**
 * Web-exact type styles, for the places a design-system role cannot express the web value.
 *
 * The web sets `html { font-size: 18px }`, so its Tailwind `rem` steps resolve to:
 *
 * | web class | px    | [StoreType]                       |
 * |-----------|-------|-----------------------------------|
 * | `text-sm` | 15.75 | [sm] / [smMedium] / [smBold]     |
 * | `text-base`| 18    | [base]                            |
 * | `text-lg` | 20.25 | [lg] / [lgMedium] / [lgSemibold]  |
 * | `text-xl` | 22.5  | [xl] / [xlSemibold]               |
 * | `text-2xl`| 27    | [xxl] / [xxlBold]                 |
 * | `text-3xl`| 33.75 | [xxxlBold]                        |
 * | `text-4xl`| 40.5  | [xxxxlBold]                       |
 *
 * Two things make these necessary rather than optional. The design system's own scale starts at
 * `TypefaceSizeTokens.Step1` = 18sp, so it cannot express the 15.75px `text-sm` used by tags,
 * badges and rail labels; and it ships only Regular/Medium weights, so it cannot express the 700
 * weight the web puts on every heading and app name. Nearest-role substitution therefore drifts on
 * both size and weight, which is what made the original port render oversized.
 *
 * Sizes are in `sp` against the 1sp-per-px the reference capture runs at (density 160), so the
 * native layout matches `good-ui.png` 1:1 on this display.
 */
@Immutable
object StoreType {
    // -- text-sm (15.75px) -------------------------------------------------------
    /** `text-sm` — plain supporting text. */
    val sm = TextStyle(fontSize = 15.75.sp, fontWeight = FontWeight.Normal, lineHeight = 21.sp)

    /** `text-sm font-medium`. */
    val smMedium = sm.copy(fontWeight = FontWeight.Medium)

    /** `text-sm font-semibold` — the category pill, live status text and rail labels. */
    val smSemibold = sm.copy(fontWeight = FontWeight.SemiBold)

    /** `text-sm font-bold` — the rail's update-count badge. */
    val smBold = sm.copy(fontWeight = FontWeight.Bold)

    // -- text-base (18px) --------------------------------------------------------
    /** `text-base` — the catalog tile tagline and the app-status chip. */
    val base = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Normal, lineHeight = 24.sp)

    // -- text-lg (20.25px) -------------------------------------------------------
    /** `text-lg` — descriptions, section metadata and button labels. */
    val lg = TextStyle(fontSize = 20.25.sp, fontWeight = FontWeight.Normal, lineHeight = 26.sp)

    /** `text-lg font-medium`. */
    val lgMedium = lg.copy(fontWeight = FontWeight.Medium)

    /** `text-lg font-semibold` — setting labels and primary actions. */
    val lgSemibold = lg.copy(fontWeight = FontWeight.SemiBold)

    // -- text-xl (22.5px) --------------------------------------------------------
    /** `text-xl` — app names on cards and detail body copy. */
    val xl = TextStyle(fontSize = 22.5.sp, fontWeight = FontWeight.Normal, lineHeight = 28.sp)

    /** `text-xl font-semibold` — the top bar clock and setting values. */
    val xlSemibold = xl.copy(fontWeight = FontWeight.SemiBold)

    /** `text-xl font-bold` — the catalog tile's app name. */
    val xlBold = xl.copy(fontWeight = FontWeight.Bold)

    // -- text-2xl (27px) ---------------------------------------------------------
    /** `text-2xl` — section headings such as "All" or "Channels". */
    val xxl = TextStyle(fontSize = 27.sp, fontWeight = FontWeight.Normal, lineHeight = 33.sp)

    /** `text-2xl font-bold` — the weight every web section heading actually uses. */
    val xxlBold = xxl.copy(fontWeight = FontWeight.Bold)

    // -- text-3xl / text-4xl -----------------------------------------------------
    /** `text-3xl font-bold` — the top bar title. */
    val xxxlBold = TextStyle(fontSize = 33.75.sp, fontWeight = FontWeight.Bold, lineHeight = 40.sp)

    /** `text-4xl font-bold` — the app detail hero name. */
    val xxxxlBold = TextStyle(fontSize = 40.5.sp, fontWeight = FontWeight.Bold, lineHeight = 47.sp)

    // -- aliases kept for the screens already ported ------------------------------
    /** Alias of [tileName] — the catalog tile's app name (`text-xl font-bold`). */
    val tileName get() = xlBold

    /** Alias of [tileTagline] — the catalog tile tagline (`text-base`). */
    val tileTagline get() = base

    /** Alias of [tileMeta] — category pill and status text (`text-sm font-semibold`). */
    val tileMeta get() = smSemibold

    /** Alias of [clock] — the top bar clock (`text-xl font-semibold`). */
    val clock get() = xlSemibold

    /** Alias of [sectionCount] — the "12 apps" count (`text-lg`). */
    val sectionCount get() = lg
}

/**
 * Wraps content in the design system [AutomotiveTheme], configured with the store palette.
 *
 * The design system supplies spacing, shapes, typography, icons, strokes, minimum touch
 * targets and focus/ripple behaviour. The store contributes its palette via
 * [storePaletteTokens] and exposes the raw CSS-equivalent roles through [LocalStoreColors]
 * for the few places that need an exact CSS value (e.g. a 15%-tinted chip background).
 *
 * The window size class is resolved here into [StoreMetrics] and the design system's type
 * scale is scaled by [StoreMetrics.typeScale], so every screen automatically gets metrics and
 * typography that match the physical display without any per-screen breakpoint logic.
 */
@Composable
fun StoreTheme(
    themeMode: ThemeMode,
    locale: Locale,
    windowSizeClass: WindowSizeClass,
    content: @Composable () -> Unit,
) {
    val isDark = themeMode == ThemeMode.NIGHT
    val colors = remember(themeMode) { StoreColors.of(themeMode) }
    val translator = remember(locale) { Translator(locale) }
    val metrics = remember(windowSizeClass) { StoreMetrics.resolve(windowSizeClass) }
    val baseTypography = Typography()
    val themeConfig = remember(themeMode, metrics.typeScale) {
        ThemeConfig(
            palette = storePaletteTokens(themeMode),
            typography = baseTypography.scaledBy(metrics.typeScale),
        )
    }

    CompositionLocalProvider(
        LocalStoreColors provides colors,
        LocalStoreMetrics provides metrics,
        LocalTranslator provides translator,
    ) {
        AutomotiveTheme(
            themeConfig = themeConfig,
            isInDarkTheme = isDark,
            content = content,
        )
    }
}

