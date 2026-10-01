package org.radioplayer.automotive.designsystem.interaction

import androidx.compose.foundation.BorderStroke
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import org.radioplayer.automotive.designsystem.theme.AutomotiveTheme

@Composable
fun rememberFocusRingStroke(): BorderStroke {
    val width = AutomotiveTheme.stroke.medium
    val color = AutomotiveTheme.colorScheme.onPrimaryContainer
    return remember(width, color) { BorderStroke(width, color) }
}
@Composable
fun rememberSystemFocusRing(
    interactionState: InteractionState,
    default: BorderStroke?
): BorderStroke? {
    return if (interactionState.isFocused) {
        BorderStroke(
            AutomotiveTheme.stroke.medium,
            AutomotiveTheme.colorScheme.onPrimaryContainer
        )
    } else {
        default
    }
}