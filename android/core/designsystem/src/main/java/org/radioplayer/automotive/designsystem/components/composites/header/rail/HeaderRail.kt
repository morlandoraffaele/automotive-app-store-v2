package org.radioplayer.automotive.designsystem.components.composites.header.rail

import android.content.res.Configuration
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.radioplayer.automotive.designsystem.components.composites.button.outline.ButtonOutline
import org.radioplayer.automotive.designsystem.components.composites.containertappableicon.ContainerTappableIcon
import org.radioplayer.automotive.designsystem.components.composites.header.HeaderRow
import org.radioplayer.automotive.designsystem.components.primitives.Text
import org.radioplayer.automotive.designsystem.components.primitives.icon.Icon
import org.radioplayer.automotive.designsystem.interaction.rememberInteractionState
import org.radioplayer.automotive.designsystem.interaction.rememberSystemFocusRing
import org.radioplayer.automotive.designsystem.theme.AutomotiveTheme

private fun resolveAlpha(enabled: Boolean) =
    if (enabled) 1F else 0.38F // TODO: Alpha value should come from AutomotiveTheme

@Composable
fun HeaderRail(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    label: String,
    enabled: Boolean = true,
    leadingIcon: @Composable (() -> Unit)? = null,
    contentIcon: @Composable (() -> Unit)? = null,
    trailingSlot: HeaderRailTrailing? = null,
    colors: HeaderRailColors = HeaderRailDefaults.colors(),
    shape: Shape = RoundedCornerShape(AutomotiveTheme.measurement.shapes.largeIncreased)
) {
    val interactionSource = remember { MutableInteractionSource() }
    val interactionState by rememberInteractionState(interactionSource)

    val resolvedBorder = rememberSystemFocusRing(interactionState, null)

    val alpha = resolveAlpha(enabled)

    Surface(
        modifier = modifier
            .alpha(alpha),
        onClick = onClick,
        enabled = enabled,
        shape = shape,
        color = colors.containerColor,
        contentColor = colors.contentColor,
        border = resolvedBorder,
        interactionSource = interactionSource,
    ) {
        HeaderRow(
            gap = AutomotiveTheme.measurement.spaces.large,
            padding = PaddingValues(0.dp),
            leading = leadingIcon?.let {
                {
                    ContainerTappableIcon {
                        leadingIcon.invoke()
                    }
                }
            },
            content = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = label,
                        style = AutomotiveTheme.typography.body1Medium,
                    )
                    if (contentIcon != null) {
                        ContainerTappableIcon {
                            contentIcon.invoke()
                        }
                    }
                }
            },
            trailing = {
                if (trailingSlot == null) return@HeaderRow
                when (trailingSlot) {
                    is HeaderRailTrailing.IconSlot -> {
                        ContainerTappableIcon {
                            trailingSlot.icon.invoke()
                        }
                    }

                    is HeaderRailTrailing.ButtonSlot -> {
                        ButtonOutline(
                            shapeVariant = trailingSlot.shapeVariant,
                            onClick = trailingSlot.onClick,
                            icon = trailingSlot.icon,
                            label = trailingSlot.label
                        )
                    }
                }
            }
        )
    }
}

@Preview(
    name = "HeaderRail - Light Mode",
    showBackground = true,
    widthDp = 1000,
    uiMode = Configuration.UI_MODE_NIGHT_NO or Configuration.UI_MODE_TYPE_CAR,
)

@Preview(
    name = "HeaderRail - Dark Mode",
    showBackground = false,
    widthDp = 1000,
    uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_CAR,
)

@Composable
private fun HeaderRailPreview() {
    AutomotiveTheme() {
        Column(
            modifier = Modifier.padding(AutomotiveTheme.measurement.spaces.large),
            verticalArrangement = Arrangement.spacedBy(AutomotiveTheme.measurement.spaces.large)
        ) {
            HeaderRail(
                onClick = {},
                label = "Label",
                leadingIcon = { Icon(size = AutomotiveTheme.icon.primary) },
                contentIcon = { Icon(size = AutomotiveTheme.icon.primary) },
                trailingSlot = HeaderRailTrailing.ButtonSlot(
                    icon = { Icon(size = AutomotiveTheme.icon.primary) },
                    label = "Label",
                    onClick = {}
                ),
                enabled = true
            )

            HeaderRail(
                onClick = {},
                label = "Label",
                leadingIcon = { Icon(size = AutomotiveTheme.icon.primary) },
                contentIcon = { Icon(size = AutomotiveTheme.icon.primary) },
                trailingSlot = HeaderRailTrailing.IconSlot(
                    icon = { Icon(size = AutomotiveTheme.icon.primary) }
                ),
                enabled = true
            )
        }
    }
}
