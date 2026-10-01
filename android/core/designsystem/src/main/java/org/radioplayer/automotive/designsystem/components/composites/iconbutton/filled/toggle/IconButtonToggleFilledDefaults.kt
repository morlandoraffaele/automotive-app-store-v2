package org.radioplayer.automotive.designsystem.components.composites.iconbutton.filled.toggle

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import org.radioplayer.automotive.designsystem.components.composites.button.filled.toggle.ButtonToggleFilledColors
import org.radioplayer.automotive.designsystem.theme.AutomotiveTheme

object IconButtonToggleFilledDefaults {

    @Composable
    fun colors(
        unselectedContainerColor: Color = AutomotiveTheme.colorScheme.surfaceContainer,
        unselectedContentColor: Color = AutomotiveTheme.colorScheme.onSurfaceVariant,
        selectedContainerColor: Color = AutomotiveTheme.colorScheme.primary,
        selectedContentColor: Color = AutomotiveTheme.colorScheme.onPrimary
    ): IconButtonToggleFilledColors = IconButtonToggleFilledColors(
        selectedContainerColor = selectedContainerColor,
        selectedContentColor = selectedContentColor,
        unselectedContainerColor = unselectedContainerColor,
        unselectedContentColor = unselectedContentColor,
    )
}