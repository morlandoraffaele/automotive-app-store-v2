package org.radioplayer.automotive.designsystem.components.composites.button.filled.toggle

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import org.radioplayer.automotive.designsystem.theme.AutomotiveTheme

object ButtonToggleFilledDefaults {

    @Composable
    fun colors(
        unselectedContainerColor: Color = AutomotiveTheme.colorScheme.surfaceContainer,
        unselectedContentColor: Color = AutomotiveTheme.colorScheme.onSurfaceVariant,
        selectedContainerColor: Color = AutomotiveTheme.colorScheme.primary,
        selectedContentColor: Color = AutomotiveTheme.colorScheme.onPrimary
    ): ButtonToggleFilledColors = ButtonToggleFilledColors(
        selectedContainerColor = selectedContainerColor,
        selectedContentColor = selectedContentColor,
        unselectedContainerColor = unselectedContainerColor,
        unselectedContentColor = unselectedContentColor,
    )
}