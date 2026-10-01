package org.radioplayer.automotive.designsystem.components.features.country.listitemcountry

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color

/**
 * Colours for [ListItemCountry]. Build through [ListItemCountryDefaults.colors].
 *
 * Disabled is not a separate set of colours: the Figma `Disabled` variants are the enabled ones
 * at `opacity: 0.38`, so every getter here takes `enabled` and applies that alpha to the colour
 * itself instead of the whole item going through an alpha layer (see the README's pitfall).
 */
@Immutable
data class ListItemCountryColors(
    val containerColor: Color,
    val selectedContainerColor: Color,
    val headlineColor: Color,
    val selectedHeadlineColor: Color,
    val flagPlaceholderColor: Color,
    val dividerColor: Color,
    val focusRingColor: Color,
    val disabledAlpha: Float,
) {

    fun containerColor(selected: Boolean, enabled: Boolean): Color =
        (if (selected) selectedContainerColor else containerColor).dimmedUnless(enabled)

    fun headlineColor(selected: Boolean, enabled: Boolean): Color =
        (if (selected) selectedHeadlineColor else headlineColor).dimmedUnless(enabled)

    fun flagPlaceholderColor(enabled: Boolean): Color = flagPlaceholderColor.dimmedUnless(enabled)

    fun dividerColor(enabled: Boolean): Color = dividerColor.dimmedUnless(enabled)

    private fun Color.dimmedUnless(enabled: Boolean): Color =
        if (enabled) this else copy(alpha = alpha * disabledAlpha)
}
