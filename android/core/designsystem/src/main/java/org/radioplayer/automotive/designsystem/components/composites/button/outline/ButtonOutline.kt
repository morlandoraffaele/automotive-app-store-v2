package org.radioplayer.automotive.designsystem.components.composites.button.outline

import android.content.res.Configuration
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import org.radioplayer.automotive.designsystem.components.primitives.Text
import org.radioplayer.automotive.designsystem.components.primitives.icon.Icon
import org.radioplayer.automotive.designsystem.interaction.InteractionState
import org.radioplayer.automotive.designsystem.interaction.rememberFocusRingStroke
import org.radioplayer.automotive.designsystem.interaction.rememberInteractionState
import org.radioplayer.automotive.designsystem.theme.AutomotiveTheme

@Composable
private fun resolveBorderTreatment(
    interactionState: InteractionState,
    default: BorderStroke = BorderStroke(
        AutomotiveTheme.stroke.thin,
        AutomotiveTheme.colorScheme.outlineVariant
    )
): BorderStroke? {
    return if (interactionState.isFocused) {
        rememberFocusRingStroke()
    } else {
        default
    }
}

private fun resolveAlpha(enabled: Boolean) =
    if (enabled) 1F else 0.38F // TODO: Alpha value should come from AutomotiveTheme

@Composable
private fun resolveContentPadding(
    iconPosition: ButtonOutlineIconPosition,
    hasIcon: Boolean,
): Pair<Dp, Dp> {
    val medium = AutomotiveTheme.measurement.spaces.medium
    val large = AutomotiveTheme.measurement.spaces.large

    if (!hasIcon) return large to large

    return when (iconPosition) {
        ButtonOutlineIconPosition.Leading -> medium to large
        ButtonOutlineIconPosition.Trailing -> large to medium
    }
}

@Composable
fun ButtonOutline(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    label: String,
    icon: @Composable (() -> Unit)? = null,
    enabled: Boolean = true,
    shapeVariant: ButtonOutlineShape = ButtonOutlineShape.Rounded,
    colors: ButtonOutlineColors = ButtonOutlineDefaults.colors(),
    iconPosition: ButtonOutlineIconPosition = ButtonOutlineIconPosition.Leading,
    interactionSource: MutableInteractionSource? = null,
) {
    val shape: Shape = when (shapeVariant) {
        ButtonOutlineShape.Rounded -> RoundedCornerShape(AutomotiveTheme.measurement.shapes.full)
        ButtonOutlineShape.Square -> RoundedCornerShape(AutomotiveTheme.measurement.shapes.largeIncreased)
    }

    val actualInteractionSource = interactionSource ?: remember { MutableInteractionSource() }
    val interactionState by rememberInteractionState(actualInteractionSource)

    val resolvedBorder = resolveBorderTreatment(interactionState)

    val alpha = resolveAlpha(enabled)
    val (startPadding, endPadding) = resolveContentPadding(iconPosition, icon != null)

    Surface(
        modifier = modifier
            .alpha(alpha)
            .defaultMinSize(
                minWidth = AutomotiveTheme.measurement.sizes.minInteractionWidth,
                minHeight = AutomotiveTheme.measurement.sizes.minTapArea
            )
            .widthIn(max = AutomotiveTheme.measurement.sizes.maxInteractionWidth)
            .semantics(mergeDescendants = true) {
                this.role = Role.Button
            },
        onClick = onClick,
        enabled = enabled,
        shape = shape,
        color = colors.containerColor,
        contentColor = colors.contentColor,
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
                ButtonOutlineIconPosition.Leading -> {
                    icon?.invoke()
                    labelContent()
                }
                ButtonOutlineIconPosition.Trailing -> {
                    labelContent()
                    icon?.invoke()
                }
            }
        }
    }
}


@Preview(
    name = "ButtonOutline - Light Mode",
    showBackground = true,
    widthDp = 1000,
    uiMode = Configuration.UI_MODE_NIGHT_NO or Configuration.UI_MODE_TYPE_CAR,
)

@Preview(
    name = "ButtonOutline - Dark Mode",
    showBackground = false,
    widthDp = 1000,
    uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_CAR,
)

@Composable
private fun ButtonOutlinePreview() {
    AutomotiveTheme() {
        Column(
            modifier = Modifier.padding(AutomotiveTheme.measurement.spaces.large),
            verticalArrangement = Arrangement.spacedBy(AutomotiveTheme.measurement.spaces.large)
        ) {
            ButtonOutline(
                onClick = {},
                icon = { Icon(size = AutomotiveTheme.icon.primary) },
                label = "Label",
                enabled = false,
            )
        }
    }
}