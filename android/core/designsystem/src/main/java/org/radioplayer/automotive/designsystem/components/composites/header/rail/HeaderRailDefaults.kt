package org.radioplayer.automotive.designsystem.components.composites.header.rail

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import org.radioplayer.automotive.designsystem.theme.AutomotiveTheme

object HeaderRailDefaults {

    @Composable
    fun colors(
        containerColor: Color = Color.Transparent,
        contentColor: Color = AutomotiveTheme.colorScheme.onSurfaceVariant,
    ): HeaderRailColors = HeaderRailColors(containerColor, contentColor)
}