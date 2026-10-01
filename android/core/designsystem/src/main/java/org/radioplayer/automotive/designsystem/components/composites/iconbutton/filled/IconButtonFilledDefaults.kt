package org.radioplayer.automotive.designsystem.components.composites.iconbutton.filled

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import org.radioplayer.automotive.designsystem.theme.AutomotiveTheme

object IconButtonFilledDefaults {

    @Composable
    fun colors(
        containerColor: Color = AutomotiveTheme.colorScheme.primary,
        contentColor: Color = AutomotiveTheme.colorScheme.onPrimary,
    ): IconButtonFilledColors = IconButtonFilledColors(
        containerColor = containerColor,
        contentColor = contentColor,
    )
}
