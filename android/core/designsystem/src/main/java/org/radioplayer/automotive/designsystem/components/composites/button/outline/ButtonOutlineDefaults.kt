package org.radioplayer.automotive.designsystem.components.composites.button.outline

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import org.radioplayer.automotive.designsystem.theme.AutomotiveTheme

object ButtonOutlineDefaults {

    @Composable
    fun colors(
        containerColor: Color = Color.Transparent,
        contentColor: Color = AutomotiveTheme.colorScheme.onSurfaceVariant,
    ): ButtonOutlineColors = ButtonOutlineColors(
        containerColor = containerColor,
        contentColor = contentColor
    )
}