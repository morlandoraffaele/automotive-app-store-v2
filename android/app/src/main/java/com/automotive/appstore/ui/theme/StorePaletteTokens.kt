package com.automotive.appstore.ui.theme

import androidx.compose.ui.graphics.Color
import com.automotive.appstore.data.ThemeMode
import org.radioplayer.automotive.designsystem.tokens.BluePalette
import org.radioplayer.automotive.designsystem.tokens.ErrorPalette
import org.radioplayer.automotive.designsystem.tokens.GreenPalette
import org.radioplayer.automotive.designsystem.tokens.NeutralPalette
import org.radioplayer.automotive.designsystem.tokens.PaletteTokens
import org.radioplayer.automotive.designsystem.tokens.PrimaryPalette
import org.radioplayer.automotive.designsystem.tokens.RedPalette
import org.radioplayer.automotive.designsystem.tokens.SecondaryPalette
import org.radioplayer.automotive.designsystem.tokens.TertiaryPalette
import org.radioplayer.automotive.designsystem.tokens.YellowPalette

/**
 * Builds the design system [PaletteTokens] for a [ThemeMode].
 *
 * `AutomotiveTheme` derives its entire `ColorSchemeOEM` from one `PaletteTokens` and picks
 * the light or dark tone ramp from `isInDarkTheme`. Feeding the app's own colours into the
 * tone slots the design system reads means every design-system component picks up the store
 * palette through `AutomotiveTheme.colorScheme` — no per-component overrides, and no risk of
 * a component mixing roles from two different palettes.
 *
 * Tone slots follow the design system's own light/dark mapping: `40` accent, `90` pale
 * container, `10`/`20` deep on-colour, `30` strong container. The `Neutral*` ramp carries
 * surfaces — `98` background, `94`/`96` card, `90`–`92` borders, `30` foreground.
 */
fun storePaletteTokens(mode: ThemeMode): PaletteTokens {
    val c = StoreColors.of(mode)
    return PaletteTokens(
        White = Color.White,
        Black = Color.Black,
        PrimaryColors = PrimaryPalette(
            Primary10 = c.primaryForeground, Primary20 = c.primaryForeground,
            Primary30 = c.primary, Primary40 = c.primary, Primary50 = c.primary,
            Primary60 = c.primary, Primary70 = c.primary, Primary80 = c.primary,
            Primary90 = c.accent, Primary95 = c.accent, Primary99 = c.background,
        ),
        SecondaryColors = SecondaryPalette(
            Secondary10 = c.secondaryForeground, Secondary20 = c.secondaryForeground,
            Secondary30 = c.secondary, Secondary40 = c.secondary, Secondary50 = c.secondary,
            Secondary60 = c.secondary, Secondary70 = c.secondary, Secondary80 = c.secondary,
            Secondary90 = c.secondary, Secondary95 = c.secondary, Secondary99 = c.background,
        ),
        TertiaryColors = TertiaryPalette(
            Tertiary10 = c.accentForeground, Tertiary20 = c.accentForeground,
            Tertiary30 = c.accent, Tertiary40 = c.accent, Tertiary50 = c.accent,
            Tertiary60 = c.accent, Tertiary70 = c.accent, Tertiary80 = c.accent,
            Tertiary90 = c.accent, Tertiary95 = c.accent, Tertiary99 = c.background,
        ),
        ErrorColors = ErrorPalette(
            Error10 = c.destructiveForeground, Error20 = c.destructiveForeground,
            Error30 = c.destructive, Error40 = c.destructive, Error50 = c.destructive,
            Error60 = c.destructive, Error70 = c.destructive, Error80 = c.destructive,
            Error90 = c.destructive, Error95 = c.destructive, Error99 = c.background,
        ),
        RedColors = RedPalette(
            Red10 = c.destructiveForeground, Red20 = c.destructiveForeground,
            Red30 = c.destructive, Red40 = c.destructive, Red50 = c.destructive,
            Red60 = c.destructive, Red70 = c.destructive, Red80 = c.destructive,
            Red90 = c.destructive, Red95 = c.destructive, Red99 = c.background,
        ),
        GreenColors = GreenPalette(
            Green10 = c.successForeground, Green20 = c.successForeground,
            Green30 = c.success, Green40 = c.success, Green50 = c.success,
            Green60 = c.success, Green70 = c.success, Green80 = c.success,
            Green90 = c.success, Green95 = c.success, Green99 = c.background,
        ),
        YellowColors = YellowPalette(
            Yellow10 = c.warningForeground, Yellow20 = c.warningForeground,
            Yellow30 = c.warning, Yellow40 = c.warning, Yellow50 = c.warning,
            Yellow60 = c.warning, Yellow70 = c.warning, Yellow80 = c.warning,
            Yellow90 = c.warning, Yellow95 = c.warning, Yellow99 = c.background,
        ),
        BlueColors = BluePalette(
            Blue10 = c.primaryForeground, Blue20 = c.primaryForeground,
            Blue30 = c.primary, Blue40 = c.primary, Blue50 = c.primary,
            Blue60 = c.primary, Blue70 = c.primary, Blue80 = c.primary,
            Blue90 = c.accent, Blue95 = c.accent, Blue99 = c.background,
        ),
        NeutralColors = NeutralPalette(
            Neutral0 = Color.Black,
            Neutral4 = c.background, Neutral6 = c.background,
            Neutral10 = c.foreground, Neutral12 = c.background,
            Neutral17 = c.background, Neutral20 = c.background, Neutral22 = c.background,
            Neutral30 = c.foreground, Neutral40 = c.foreground, Neutral50 = c.foreground,
            Neutral60 = c.border, Neutral70 = c.mutedForeground, Neutral80 = c.mutedForeground,
            Neutral90 = c.border, Neutral92 = c.border,
            Neutral94 = c.card, Neutral95 = c.card, Neutral96 = c.card,
            Neutral98 = c.background, Neutral99 = c.background, Neutral100 = Color.White,
            NeutralVariant0 = Color.Black,
            NeutralVariant10 = c.foreground, NeutralVariant20 = c.foreground,
            NeutralVariant30 = c.mutedForeground, NeutralVariant40 = c.mutedForeground,
            NeutralVariant50 = c.mutedForeground, NeutralVariant60 = c.border,
            NeutralVariant70 = c.mutedForeground, NeutralVariant80 = c.mutedForeground,
            NeutralVariant90 = c.muted, NeutralVariant95 = c.muted, NeutralVariant99 = c.background,
        ),
    )
}
