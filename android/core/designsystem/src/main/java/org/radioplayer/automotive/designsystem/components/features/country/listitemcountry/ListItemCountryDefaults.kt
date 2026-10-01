package org.radioplayer.automotive.designsystem.components.features.country.listitemcountry

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import org.radioplayer.automotive.designsystem.theme.AutomotiveTheme

/**
 * Values for [ListItemCountry], read off `List Item / Selection / Country` (component set
 * `36640:45093`, Blueprint export 3.18).
 *
 * Geometry goes through tokens, all bound in the export: `paddingTop/Bottom/Left/Right` →
 * `sys/measurement/space/Medium` (`spaces.medium`), row `itemSpacing` → `Large` (`spaces.large`),
 * the flag-to-text `Container.itemSpacing` → `Medium`, `minHeight` → `tap area` (`sizes.minTapArea`),
 * corner radius → `Large Increased rounding` (`shapes.largeIncreased`), focus ring → 4dp inside
 * stroke (`stroke.medium`). Figma's variable values read 0.75× the drawn px; the token *names*
 * resolve to the drawn px in this module (see the README's density note).
 */
object ListItemCountryDefaults {

    /** Every `State=Disabled` variant has `opacity: 0.38`. */
    const val DisabledAlpha = 0.38f

    /**
     * The `Flags` instance at `Size=Default`: 48 × 32, unbound geometry. Only the placeholder is
     * drawn for now; the real flag assets are pending, and callers can pass one through
     * [ListItemCountry]'s `flag` slot.
     */
    val FlagSize: DpSize = DpSize(48.dp, 32.dp)

    /** Divider height: `Divider` frame, 1px, unbound. */
    val DividerThickness = 1.dp

    /**
     * Colour bindings, identical in every `State`:
     * - `Selected=True` container → `sys/color/Inverse Surface`, `Selected=False` → no fill.
     * - `text-primary` → `On Surface`, or `Inverse On Surface` when selected.
     * - `Flags` placeholder fill → `On Surface` (the `Country=Placeholder` variant).
     * - `Divider` → `Outline Variant`.
     * - `Focused-Rotary` overlay stroke → `sys/color/Primary` (not the lighter `onPrimaryContainer`
     *   the shared `rememberFocusRingStroke` uses — same finding as `SearchInputField`).
     *
     * The `Pressed` state layer is `On Surface` / `Inverse On Surface` at 12%, which is the
     * headline colour — so the Surface ripple, which tints with the content colour, matches it.
     */
    @Composable
    fun colors(
        containerColor: Color = Color.Transparent,
        selectedContainerColor: Color = AutomotiveTheme.colorScheme.inverseSurface,
        headlineColor: Color = AutomotiveTheme.colorScheme.onSurface,
        selectedHeadlineColor: Color = AutomotiveTheme.colorScheme.inverseOnSurface,
        flagPlaceholderColor: Color = AutomotiveTheme.colorScheme.onSurface,
        dividerColor: Color = AutomotiveTheme.colorScheme.outlineVariant,
        focusRingColor: Color = AutomotiveTheme.colorScheme.primary,
    ): ListItemCountryColors = ListItemCountryColors(
        containerColor = containerColor,
        selectedContainerColor = selectedContainerColor,
        headlineColor = headlineColor,
        selectedHeadlineColor = selectedHeadlineColor,
        flagPlaceholderColor = flagPlaceholderColor,
        dividerColor = dividerColor,
        focusRingColor = focusRingColor,
        disabledAlpha = DisabledAlpha,
    )
}
