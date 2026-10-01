package org.radioplayer.automotive.designsystem.components.composites.iconbutton.standard.toggle

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

import org.radioplayer.automotive.designsystem.theme.AutomotiveTheme

object IconButtonToggleStandardDefaults {
    @Composable
    fun colors(
        unselectedContainerColor: Color = Color.Transparent,
        unselectedContentColor: Color = AutomotiveTheme.colorScheme.onSurfaceVariant,
        selectedContainerColor: Color = Color.Transparent,
        selectedContentColor: Color = AutomotiveTheme.colorScheme.primary
    ): IconButtonToggleStandardColors = IconButtonToggleStandardColors(
        selectedContainerColor = selectedContainerColor,
        selectedContentColor = selectedContentColor,
        unselectedContainerColor = unselectedContainerColor,
        unselectedContentColor = unselectedContentColor,
    )
}