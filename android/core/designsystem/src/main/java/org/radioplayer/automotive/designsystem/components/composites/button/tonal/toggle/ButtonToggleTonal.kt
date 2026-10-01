package org.radioplayer.automotive.designsystem.components.composites.button.tonal.toggle

import android.content.res.Configuration
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.toggleableState
import androidx.compose.ui.state.ToggleableState
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import org.radioplayer.automotive.designsystem.components.primitives.Text
import org.radioplayer.automotive.designsystem.components.primitives.icon.Icon
import org.radioplayer.automotive.designsystem.interaction.InteractionState
import org.radioplayer.automotive.designsystem.interaction.rememberFocusRingStroke
import org.radioplayer.automotive.designsystem.interaction.rememberInteractionState
import org.radioplayer.automotive.designsystem.theme.AutomotiveTheme

@Composable
private fun resolveFocusBorder(
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

@Composable
private fun resolveContentPadding(
    iconPosition: ButtonToggleTonalIconPosition,
    hasIcon: Boolean,
): Pair<Dp, Dp> {
    val medium = AutomotiveTheme.measurement.spaces.medium
    val large = AutomotiveTheme.measurement.spaces.large

    if (!hasIcon) return large to large

    return when (iconPosition) {
        ButtonToggleTonalIconPosition.Leading -> medium to large
        ButtonToggleTonalIconPosition.Trailing -> large to medium
    }
}

/**
 * A tonal toggle button representing a selected/unselected state, e.g. a standalone
 * filter toggle or a segment within a group of options.
 *
 * State is hoisted: the caller owns [selected] and receives change requests via
 * [onSelectedChange], the same pattern as [androidx.compose.material3.Switch] or
 * [androidx.compose.material3.FilterChip].
 */
@Composable
fun ButtonToggleTonal(
    selected: Boolean,
    onSelectedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    label: String,
    icon: @Composable (() -> Unit)? = null,
    enabled: Boolean = true,
    shapeVariant: ButtonToggleTonalShape = ButtonToggleTonalShape.Rounded,
    colors: ButtonToggleTonalColors = ButtonToggleTonalDefaults.colors(),
    iconPosition: ButtonToggleTonalIconPosition = ButtonToggleTonalIconPosition.Leading,
    interactionSource: MutableInteractionSource? = null,
) {
    val shape: Shape = when (shapeVariant) {
        ButtonToggleTonalShape.Rounded -> RoundedCornerShape(AutomotiveTheme.measurement.shapes.full)
        ButtonToggleTonalShape.Square -> RoundedCornerShape(AutomotiveTheme.measurement.shapes.largeIncreased)
    }

    val actualInteractionSource = interactionSource ?: remember { MutableInteractionSource() }
    val interactionState by rememberInteractionState(actualInteractionSource)

    val resolvedBorder = resolveFocusBorder(interactionState)

    val alpha = resolveAlpha(enabled)
    val (startPadding, endPadding) = resolveContentPadding(iconPosition, icon != null)

    val containerColor = if (selected) colors.selectedContainerColor else colors.unselectedContainerColor
    val contentColor = if (selected) colors.selectedContentColor else colors.unselectedContentColor


    Surface(
        modifier = modifier
            .alpha(alpha)
            .defaultMinSize(
                minWidth = AutomotiveTheme.measurement.sizes.minInteractionWidth,
                minHeight = AutomotiveTheme.measurement.sizes.minTapArea
            )
            .widthIn(max = AutomotiveTheme.measurement.sizes.maxInteractionWidth)
            .semantics(mergeDescendants = true) {
                this.role = Role.Checkbox
                this.toggleableState = if (selected) ToggleableState.On else ToggleableState.Off
            },
        onClick = { onSelectedChange(!selected) },
        enabled = enabled,
        shape = shape,
        color = containerColor,
        contentColor = contentColor,
        border = resolvedBorder,
        interactionSource = actualInteractionSource
    ) {
        Row(
            modifier = Modifier
                .padding(
                    start = startPadding,
                    top = AutomotiveTheme.measurement.spaces.medium,
                    end = endPadding,
                    bottom = AutomotiveTheme.measurement.spaces.medium
                ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(
                AutomotiveTheme.measurement.spaces.medium,
                Alignment.CenterHorizontally
            )
        ) {

            val labelContent = @Composable {
                Text(
                    text = label,
                    style = AutomotiveTheme.typography.body3Medium,
                    maxLines = 1,
                )
            }

            when (iconPosition) {
                ButtonToggleTonalIconPosition.Leading -> {
                    icon?.invoke()
                    labelContent()
                }

                ButtonToggleTonalIconPosition.Trailing -> {
                    labelContent()
                    icon?.invoke()
                }
            }
        }
    }
}


@Preview(
    name = "ButtonToggleTonal - Light Mode",
    showBackground = true,
    widthDp = 1000,
    uiMode = Configuration.UI_MODE_NIGHT_NO or Configuration.UI_MODE_TYPE_CAR,
)

@Preview(
    name = "ButtonToggleTonal - Dark Mode",
    showBackground = false,
    widthDp = 1000,
    uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_CAR,
)

@Composable
private fun ButtonToggleTonalPreview() {
    AutomotiveTheme() {
        Column(
            modifier = Modifier.padding(AutomotiveTheme.measurement.spaces.large),
            verticalArrangement = Arrangement.spacedBy(AutomotiveTheme.measurement.spaces.large)
        ) {
            var isOnSelected by remember { mutableStateOf(true) }
            var isOffSelected by remember { mutableStateOf(false) }

            ButtonToggleTonal(
                selected = isOnSelected,
                onSelectedChange = { isOnSelected = it },
                icon = { Icon(size = AutomotiveTheme.icon.primary) },
                label = "Label",
            )

            ButtonToggleTonal(
                selected = isOffSelected,
                onSelectedChange = { isOffSelected = it },
                icon = { Icon(size = AutomotiveTheme.icon.primary) },
                label = "Label",
            )

            ButtonToggleTonal(
                selected = true,
                onSelectedChange = {},
                icon = { Icon(size = AutomotiveTheme.icon.primary) },
                label = "Label",
                enabled = false,
            )
        }
    }
}