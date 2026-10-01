package org.radioplayer.automotive.designsystem.components.composites.iconbutton.outline.toggle

import android.content.res.Configuration
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.tooling.preview.Preview
import org.radioplayer.automotive.designsystem.components.primitives.icon.Icon
import org.radioplayer.automotive.designsystem.interaction.InteractionState
import org.radioplayer.automotive.designsystem.interaction.rememberFocusRingStroke
import org.radioplayer.automotive.designsystem.theme.AutomotiveTheme
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.toggleableState
import androidx.compose.ui.state.ToggleableState
import org.radioplayer.automotive.designsystem.interaction.rememberInteractionState

/**
 * Resolves the border for the outline toggle button.
 *
 * Unlike the filled variant (which only ever shows a border for focus),
 * the outline variant also needs an outline stroke when unselected — the
 * selected state is conveyed by a filled container instead, so no outline
 * is drawn in that case to avoid double-signaling selection.
 */
@Composable
private fun resolveBorder(
    interactionState: InteractionState,
    selected: Boolean,
): BorderStroke? {
    return when {
        interactionState.isFocused -> rememberFocusRingStroke()
        selected -> null
        else -> BorderStroke(
            width = AutomotiveTheme.stroke.thin,
            color = AutomotiveTheme.colorScheme.outlineVariant
        )
    }
}

private fun resolveAlpha(enabled: Boolean) =
    if (enabled) 1F else 0.38F // TODO: Alpha value should come from AutomotiveTheme

@Composable
fun IconButtonToggleOutline(
    selected: Boolean,
    onSelectedChange: (Boolean) -> Unit,
    icon: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    shape: Shape = RoundedCornerShape(AutomotiveTheme.measurement.shapes.full),
    colors: IconButtonToggleOutlineColors = IconButtonToggleOutlineDefaults.colors(),
    interactionSource: MutableInteractionSource? = null,
) {
    val actualInteractionSource = interactionSource ?: remember { MutableInteractionSource() }
    val interactionState by rememberInteractionState(actualInteractionSource)

    val resolvedBorder = resolveBorder(interactionState, selected)
    val alpha = resolveAlpha(enabled)

    val containerColor =
        if (selected) colors.selectedContainerColor else colors.unselectedContainerColor
    val contentColor = if (selected) colors.selectedContentColor else colors.unselectedContentColor

    Surface(
        modifier = modifier
            .alpha(alpha)
            .defaultMinSize(
                minWidth = AutomotiveTheme.measurement.sizes.minTapArea,
                minHeight = AutomotiveTheme.measurement.sizes.minTapArea
            )
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
        Box(
            contentAlignment = Alignment.Center
        ) {
            icon()
        }
    }
}

@Preview(
    name = "IconButtonToggleOutline - Light Mode",
    showBackground = true,
    widthDp = 1000,
    uiMode = Configuration.UI_MODE_NIGHT_NO or Configuration.UI_MODE_TYPE_CAR,
)
@Preview(
    name = "IconButtonToggleOutline - Dark Mode",
    showBackground = false,
    widthDp = 1000,
    uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_CAR,
)
@Composable
private fun IconButtonToggleOutlinePreview() {
    AutomotiveTheme {
        Column(
            modifier = Modifier.padding(AutomotiveTheme.measurement.spaces.large),
            verticalArrangement = Arrangement.spacedBy(AutomotiveTheme.measurement.spaces.large)
        ) {
            var isOnSelected by remember { mutableStateOf(true) }
            var isOffSelected by remember { mutableStateOf(false) }

            IconButtonToggleOutline(
                selected = isOnSelected,
                onSelectedChange = { isOnSelected = it },
                icon = { Icon(size = AutomotiveTheme.icon.primary) },
            )

            IconButtonToggleOutline(
                selected = isOffSelected,
                onSelectedChange = { isOffSelected = it },
                icon = { Icon(size = AutomotiveTheme.icon.primary) },
            )

            IconButtonToggleOutline(
                selected = true,
                onSelectedChange = {},
                icon = { Icon(size = AutomotiveTheme.icon.primary) },
                enabled = false,
            )
        }
    }
}