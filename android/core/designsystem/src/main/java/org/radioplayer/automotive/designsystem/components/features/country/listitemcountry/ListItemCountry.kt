package org.radioplayer.automotive.designsystem.components.features.country.listitemcountry

import android.content.res.Configuration
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import org.radioplayer.automotive.designsystem.components.primitives.Text
import org.radioplayer.automotive.designsystem.interaction.rememberInteractionState
import org.radioplayer.automotive.designsystem.theme.AutomotiveTheme

/**
 * The `Country=Placeholder` variant of Figma `Flags` at `Size=Default`: a rounded `On Surface`
 * fill. Stands in for every flag until the real assets are linked.
 */
@Composable
private fun FlagPlaceholder(color: Color) {
    Box(
        modifier = Modifier
            .size(ListItemCountryDefaults.FlagSize)
            .background(color, RoundedCornerShape(AutomotiveTheme.measurement.shapes.small))
    )
}

/**
 * A selectable country row — Figma `List Item / Selection / Country` (`36640:45093`), used in the
 * country selection screen.
 *
 * A flag and the country name on a transparent row, with a bottom divider. When selected, the row
 * fills with `inverseSurface` and drops its divider, as every `Selected=True` variant does.
 *
 * @param name The country name (Figma `Primary Text`). One line, ellipsized.
 * @param onClick Called when the row is tapped.
 * @param modifier Applied to the clickable surface.
 * @param selected Figma `Selected`. Exposed to accessibility as the row's selected state.
 * @param enabled When `false`, the row is not clickable and is drawn at 38% alpha.
 * @param flag The leading flag. `null` draws the `Placeholder` flag — the real flag assets are
 * not linked yet.
 * @param showFlag Figma `Flag`: whether the leading flag is shown at all.
 * @param trailingContent Figma `Trailing Container`, hidden by default as it is in the Countries
 * Container.
 * @param colors See [ListItemCountryDefaults.colors].
 * @param horizontalDividerEnabled Figma `Divider`. Never drawn while [selected].
 * @param interactionSource Optional source to observe or drive the row's interactions.
 */
@Composable
fun ListItemCountry(
    name: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    selected: Boolean = false,
    enabled: Boolean = true,
    flag: (@Composable () -> Unit)? = null,
    showFlag: Boolean = true,
    trailingContent: (@Composable () -> Unit)? = null,
    colors: ListItemCountryColors = ListItemCountryDefaults.colors(),
    horizontalDividerEnabled: Boolean = true,
    interactionSource: MutableInteractionSource? = null,
) {
    val interactionSource = interactionSource ?: remember { MutableInteractionSource() }
    val interactionState by rememberInteractionState(interactionSource)

    val headlineColor = colors.headlineColor(selected, enabled)
    val border = if (interactionState.isFocused) {
        BorderStroke(AutomotiveTheme.stroke.medium, colors.focusRingColor)
    } else {
        null
    }

    // The Figma divider is an absolutely positioned 1px frame on the row's bottom edge, inside the
    // row's own height, so it overlays the surface rather than stacking under it.
    Box(modifier = Modifier.fillMaxWidth()) {
        Surface(
            selected = selected,
            onClick = onClick,
            modifier = modifier
                .fillMaxWidth()
                .heightIn(min = AutomotiveTheme.measurement.sizes.minTapArea),
            enabled = enabled,
            shape = RoundedCornerShape(AutomotiveTheme.measurement.shapes.largeIncreased),
            color = colors.containerColor(selected, enabled),
            contentColor = headlineColor,
            border = border,
            interactionSource = interactionSource,
        ) {
            Row(
                modifier = Modifier.padding(AutomotiveTheme.measurement.spaces.medium),
                horizontalArrangement = Arrangement.spacedBy(AutomotiveTheme.measurement.spaces.large),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(AutomotiveTheme.measurement.spaces.medium),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    if (showFlag) {
                        flag?.invoke() ?: FlagPlaceholder(colors.flagPlaceholderColor(enabled))
                    }
                    Text(
                        text = name,
                        style = AutomotiveTheme.typography.body1Medium,
                        color = headlineColor,
                        maxLines = 1,
                    )
                }
                trailingContent?.invoke()
            }
        }

        if (horizontalDividerEnabled && !selected) {
            HorizontalDivider(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter),
                thickness = ListItemCountryDefaults.DividerThickness,
                color = colors.dividerColor(enabled),
            )
        }
    }
}

@Preview(
    name = "ListItemCountry - Light Mode",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_NO or Configuration.UI_MODE_TYPE_CAR,
    widthDp = 1200,
)
@Preview(
    name = "ListItemCountry - Dark Mode",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_CAR,
    widthDp = 1200,
)
@Composable
private fun ListItemCountryPreview() {
    AutomotiveTheme {
        Column(
            modifier = Modifier
                .background(AutomotiveTheme.colorScheme.surfaceContainer)
                .padding(AutomotiveTheme.measurement.spaces.medium),
            verticalArrangement = Arrangement.spacedBy(AutomotiveTheme.measurement.spaces.medium),
        ) {
            ListItemCountry(name = "Finland", onClick = {})
            ListItemCountry(name = "France", onClick = {}, selected = true)
            ListItemCountry(name = "Germany", onClick = {}, enabled = false)
            ListItemCountry(name = "Greece", onClick = {}, selected = true, enabled = false)
        }
    }
}
