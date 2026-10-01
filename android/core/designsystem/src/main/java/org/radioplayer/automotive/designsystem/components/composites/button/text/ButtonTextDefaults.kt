package org.radioplayer.automotive.designsystem.components.composites.button.text

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import org.radioplayer.automotive.designsystem.theme.AutomotiveTheme

object ButtonTextDefaults {

    @Composable
    fun colors(
        containerColor: Color = Color.Transparent,
        contentColor: Color = AutomotiveTheme.colorScheme.primary,
    ): ButtonTextColors = ButtonTextColors(
        containerColor = containerColor,
        contentColor = contentColor
    )
}