package org.radioplayer.automotive.designsystem.components.composites.iconbutton.tonal.toggle

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import org.radioplayer.automotive.designsystem.theme.AutomotiveTheme

object IconButtonToggleTonalDefaults {

    @Composable
    fun colors(
        unselectedContainerColor: Color = AutomotiveTheme.colorScheme.secondaryContainer,
        unselectedContentColor: Color = AutomotiveTheme.colorScheme.onSecondaryContainer,
        selectedContainerColor: Color = AutomotiveTheme.colorScheme.secondary,
        selectedContentColor: Color = AutomotiveTheme.colorScheme.onSecondary
    ): IconButtonToggleTonalColors = IconButtonToggleTonalColors(
        selectedContainerColor = selectedContainerColor,
        selectedContentColor = selectedContentColor,
        unselectedContainerColor = unselectedContainerColor,
        unselectedContentColor = unselectedContentColor,
    )
}