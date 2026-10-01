package org.radioplayer.automotive.designsystem.components.composites.iconbutton.standard

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

import org.radioplayer.automotive.designsystem.theme.AutomotiveTheme

object IconButtonStandardDefaults {

    @Composable
    fun colors(
        containerColor: Color = Color.Transparent,
        contentColor: Color = AutomotiveTheme.colorScheme.onSurfaceVariant,
    ): IconButtonStandardColors = IconButtonStandardColors(
        containerColor = containerColor,
        contentColor = contentColor
    )
}