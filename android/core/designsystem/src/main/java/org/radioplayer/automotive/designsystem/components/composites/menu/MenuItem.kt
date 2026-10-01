package org.radioplayer.automotive.designsystem.components.composites.menu

import android.content.res.Configuration
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import org.radioplayer.automotive.designsystem.components.composites.divider.Divider
import org.radioplayer.automotive.designsystem.components.composites.divider.DividerVariant
import org.radioplayer.automotive.designsystem.components.primitives.Text
import org.radioplayer.automotive.designsystem.components.primitives.icon.Icon
import org.radioplayer.automotive.designsystem.interaction.InteractionState
import org.radioplayer.automotive.designsystem.interaction.rememberFocusRingStroke
import org.radioplayer.automotive.designsystem.interaction.rememberInteractionState
import org.radioplayer.automotive.designsystem.theme.AutomotiveTheme

@Composable
private fun resolveBorderTreatment(
    interactionState: InteractionState,
): BorderStroke? {
    return if (interactionState.isFocused) {
        rememberFocusRingStroke()
    } else {
        null
    }
}

private fun resolveAlpha(enabled: Boolean) =
    if (enabled) 1F else 0.38F // TODO: Alpha value should come from AutomotiveTheme

/**
 * Automotive menu item, typically used as a child of [Menu].
 *
 * A `MenuItem` displays a headline, with optional supporting text, a leading slot and a
 * trailing slot, and can represent either a stateless clickable action or a selectable
 * option depending on whether [selected] is used.
 *
 * `MenuItem` is stateless with respect to selection: it does not track or persist
 * whether it is selected. The caller is responsible for hoisting the selection state
 * and passing the current value through [selected], updating it in response to
 * [onClick].
 *
 * @param headlineContent the primary content of the menu item, typically a [Text].
 * @param onClick called when the user clicks this menu item.
 * @param modifier the [Modifier] to be applied to this menu item.
 * @param supportingContent the optional secondary content displayed below the headline,
 * typically a [Text].
 * @param leadingContent the optional content displayed before the headline and
 * supporting content, typically an [Icon].
 * @param trailingContent the optional content displayed after the headline and
 * supporting content, typically an [Icon].
 * @param enabled controls the enabled state of this menu item. When `false`, this menu
 * item will not respond to user input, and it will appear visually disabled (reduced
 * opacity).
 * @param selected whether this menu item is currently selected. When `true`, the item
 * is rendered using the selected color set from [colors]. Defaults to `false`.
 * @param showDivider whether a divider is displayed at the bottom of this menu item.
 * Typically set to `false` for the last item in a [Menu] to avoid a trailing divider.
 * @param colors [MenuItemColors] that will be used to resolve the colors for this menu
 * item in its selected and unselected states. See [MenuDefaults.itemColors].
 * @param interactionSource an optional hoisted [MutableInteractionSource] for observing
 * and emitting interactions for this menu item. When `null`, an internal one will be
 * created and remembered.
 */
@Composable
fun MenuItem(
    headlineContent: @Composable () -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    supportingContent: (@Composable () -> Unit)? = null,
    leadingContent: (@Composable () -> Unit)? = null,
    trailingContent: (@Composable () -> Unit)? = null,
    enabled: Boolean = true,
    selected: Boolean = false,
    showDivider: Boolean = true,
    colors: MenuItemColors = MenuDefaults.itemColors(),
    interactionSource: MutableInteractionSource? = null,
) {
    val actualInteractionSource = interactionSource ?: remember { MutableInteractionSource() }
    val interactionState by rememberInteractionState(actualInteractionSource)

    val resolvedBorder = resolveBorderTreatment(interactionState)

    val alpha = resolveAlpha(enabled)

    Surface (
        modifier = modifier.alpha(alpha),
        onClick = onClick,
        enabled = enabled,
        color = colors.containerColor(selected),
        contentColor = colors.contentColor(selected),
        shape = RoundedCornerShape(AutomotiveTheme.measurement.shapes.medium),
        border = resolvedBorder,
        interactionSource = actualInteractionSource,
    ) {
        Column(
            modifier = Modifier.padding(AutomotiveTheme.measurement.spaces.extraExtraSmall),
            verticalArrangement = Arrangement.spacedBy(AutomotiveTheme.measurement.spaces.extraExtraSmall)
        ) {
            Row (
                modifier = Modifier,
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(AutomotiveTheme.measurement.spaces.extraExtraSmall)
            ) {

                if (leadingContent != null) {
                    CompositionLocalProvider(
                        LocalContentColor provides colors.leadingIconColor(selected),
                    ) {
                        Box(
                            modifier = Modifier
                                .defaultMinSize(
                                    minWidth = AutomotiveTheme.measurement.sizes.minTapArea,
                                    minHeight = AutomotiveTheme.measurement.sizes.minTapArea,
                                )
                                .widthIn(max = AutomotiveTheme.measurement.sizes.minTapArea),
                            contentAlignment = Alignment.Center
                        ) {
                            leadingContent()
                        }
                    }
                }

                Column (modifier = Modifier.weight(1f)) {
                    CompositionLocalProvider(
                        LocalContentColor provides colors.contentColor(selected),
                        LocalTextStyle provides AutomotiveTheme.typography.body3Medium
                    ) {
                        headlineContent()
                    }

                    if (supportingContent != null) {
                        CompositionLocalProvider(
                            LocalContentColor provides colors.supportingContentColor(selected),
                            LocalTextStyle provides AutomotiveTheme.typography.sub3
                        ) {
                            supportingContent()
                        }
                    }
                }

                if (trailingContent != null) {
                    CompositionLocalProvider(
                        LocalContentColor provides colors.trailingIconColor(selected),
                    ) {
                        Box(
                            modifier = Modifier
                                .defaultMinSize(
                                    minWidth = AutomotiveTheme.measurement.sizes.minTapArea,
                                    minHeight = AutomotiveTheme.measurement.sizes.minTapArea,
                                )
                                .widthIn(max = AutomotiveTheme.measurement.sizes.minTapArea),
                            contentAlignment = Alignment.Center
                        ) {
                            trailingContent()
                        }
                    }
                }
            }

            if(showDivider) {
                Divider(
                    variant = DividerVariant.MiddleInset
                )
            }
        }
    }
}



@Preview(
    name = "MenuItem - Light Mode",
    showBackground = true,
    widthDp = 1000,
    uiMode = Configuration.UI_MODE_NIGHT_NO or Configuration.UI_MODE_TYPE_CAR,
)

@Preview(
    name = "MenuItem - Dark Mode",
    showBackground = false,
    widthDp = 1000,
    uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_CAR,
)

@Composable
private fun MenuItemPreview() {
    AutomotiveTheme() {
        MenuItem(
            onClick = {},
            headlineContent = { Text(text = "Primary Text") },
            supportingContent = { Text(text = "Supporting Text") },
            leadingContent = { Icon() },
            trailingContent = { Icon() },
            selected = true
        )
    }
}