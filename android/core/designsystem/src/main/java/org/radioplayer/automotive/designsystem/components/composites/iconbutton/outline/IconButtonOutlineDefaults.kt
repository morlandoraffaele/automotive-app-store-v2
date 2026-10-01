package org.radioplayer.automotive.designsystem.components.composites.iconbutton.outline

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import org.radioplayer.automotive.designsystem.theme.AutomotiveTheme

object IconButtonOutlineDefaults {

    @Composable
    fun colors(
        containerColor: Color = Color.Transparent,
        contentColor: Color = AutomotiveTheme.colorScheme.onSurfaceVariant,
    ): IconButtonOutlineColors = IconButtonOutlineColors(
        containerColor = containerColor,
        contentColor = contentColor,
    )
}
