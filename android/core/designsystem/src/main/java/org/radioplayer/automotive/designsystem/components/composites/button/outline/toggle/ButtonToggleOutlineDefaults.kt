package org.radioplayer.automotive.designsystem.components.composites.button.outline.toggle

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import org.radioplayer.automotive.designsystem.theme.AutomotiveTheme

object ButtonToggleOutlineDefaults {

    @Composable
    fun colors(
        unselectedContainerColor: Color = Color.Transparent,
        unselectedContentColor: Color = AutomotiveTheme.colorScheme.onSurfaceVariant,
        selectedContainerColor: Color = AutomotiveTheme.colorScheme.inverseSurface,
        selectedContentColor: Color = AutomotiveTheme.colorScheme.inverseOnSurface,
    ): ButtonToggleOutlineColors = ButtonToggleOutlineColors(
        selectedContainerColor = selectedContainerColor,
        selectedContentColor = selectedContentColor,
        unselectedContainerColor = unselectedContainerColor,
        unselectedContentColor = unselectedContentColor,
    )
}
