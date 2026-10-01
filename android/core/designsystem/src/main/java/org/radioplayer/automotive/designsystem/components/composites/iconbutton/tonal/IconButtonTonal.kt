package org.radioplayer.automotive.designsystem.components.composites.iconbutton.tonal

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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.tooling.preview.Preview
import org.radioplayer.automotive.designsystem.components.primitives.icon.Icon
import org.radioplayer.automotive.designsystem.interaction.InteractionState
import org.radioplayer.automotive.designsystem.interaction.rememberFocusRingStroke
import org.radioplayer.automotive.designsystem.interaction.rememberInteractionState
import org.radioplayer.automotive.designsystem.theme.AutomotiveTheme

@Composable
private fun resolveFocusBorder(
    interactionState: InteractionState,
): BorderStroke? = if (interactionState.isFocused) {
    rememberFocusRingStroke()
} else {
    null
}

private fun resolveAlpha(enabled: Boolean) =
    if (enabled) 1F else 0.38F // TODO: Alpha value should come from AutomotiveTheme

@Composable
fun IconButtonTonal(
    onClick: () -> Unit,
    icon: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    shape: Shape = RoundedCornerShape(AutomotiveTheme.measurement.shapes.full),
    colors: IconButtonTonalColors = IconButtonTonalDefaults.colors(),
    interactionSource: MutableInteractionSource? = null,
) {
    val actualInteractionSource = interactionSource ?: remember { MutableInteractionSource() }
    val interactionState by rememberInteractionState(actualInteractionSource)

    Surface(
        modifier = modifier
            .alpha(resolveAlpha(enabled))
            .defaultMinSize(
                minWidth = AutomotiveTheme.measurement.sizes.minTapArea,
                minHeight = AutomotiveTheme.measurement.sizes.minTapArea,
            ),
        onClick = onClick,
        enabled = enabled,
        shape = shape,
        color = colors.containerColor,
        contentColor = colors.contentColor,
        border = resolveFocusBorder(interactionState),
        interactionSource = actualInteractionSource,
    ) {
        Box(contentAlignment = Alignment.Center) {
            icon()
        }
    }
}

@Preview(
    name = "IconButtonTonal - Light Mode",
    showBackground = true,
    widthDp = 1000,
    uiMode = Configuration.UI_MODE_NIGHT_NO or Configuration.UI_MODE_TYPE_CAR,
)
@Preview(
    name = "IconButtonTonal - Dark Mode",
    showBackground = false,
    widthDp = 1000,
    uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_CAR,
)
@Composable
private fun IconButtonTonalPreview() {
    AutomotiveTheme {
        Column(
            modifier = Modifier.padding(AutomotiveTheme.measurement.spaces.large),
            verticalArrangement = Arrangement.spacedBy(AutomotiveTheme.measurement.spaces.large),
        ) {
            IconButtonTonal(
                onClick = {},
                icon = { Icon(size = AutomotiveTheme.icon.primary) },
            )

            IconButtonTonal(
                onClick = {},
                icon = { Icon(size = AutomotiveTheme.icon.primary) },
                enabled = false,
            )
        }
    }
}