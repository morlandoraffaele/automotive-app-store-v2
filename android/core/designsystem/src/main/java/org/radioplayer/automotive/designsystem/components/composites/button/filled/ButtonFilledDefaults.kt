package org.radioplayer.automotive.designsystem.components.composites.button.filled

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import org.radioplayer.automotive.designsystem.theme.AutomotiveTheme

object ButtonFilledDefaults {

    @Composable
    fun colors(
        containerColor: Color = AutomotiveTheme.colorScheme.primary,
        contentColor: Color = AutomotiveTheme.colorScheme.onPrimary,
    ): ButtonFilledColors = ButtonFilledColors(
        containerColor = containerColor,
        contentColor = contentColor
    )
}