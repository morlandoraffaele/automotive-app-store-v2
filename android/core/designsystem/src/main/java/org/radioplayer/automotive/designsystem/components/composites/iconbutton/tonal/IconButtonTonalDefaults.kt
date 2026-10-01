package org.radioplayer.automotive.designsystem.components.composites.iconbutton.tonal

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import org.radioplayer.automotive.designsystem.theme.AutomotiveTheme

object IconButtonTonalDefaults {

    @Composable
    fun colors(
        containerColor: Color = AutomotiveTheme.colorScheme.secondaryContainer,
        contentColor: Color = AutomotiveTheme.colorScheme.onSecondaryContainer,
    ): IconButtonTonalColors = IconButtonTonalColors(
        containerColor = containerColor,
        contentColor = contentColor,
    )
}