package org.radioplayer.automotive.designsystem.components.composites.button.tonal

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import org.radioplayer.automotive.designsystem.theme.AutomotiveTheme

object ButtonTonalDefaults {

    @Composable
    fun colors(
        containerColor: Color = AutomotiveTheme.colorScheme.secondaryContainer,
        contentColor: Color = AutomotiveTheme.colorScheme.onSecondaryContainer,
    ): ButtonTonalColors = ButtonTonalColors(
        containerColor = containerColor,
        contentColor = contentColor
    )
}