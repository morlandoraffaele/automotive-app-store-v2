package org.radioplayer.automotive.designsystem.components.composites.iconbutton.outline.toggle

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import org.radioplayer.automotive.designsystem.components.composites.iconbutton.filled.toggle.IconButtonToggleFilledColors
import org.radioplayer.automotive.designsystem.theme.AutomotiveTheme

object IconButtonToggleOutlineDefaults {

    @Composable
    fun colors(
        unselectedContainerColor: Color = Color.Transparent,
        unselectedContentColor: Color = AutomotiveTheme.colorScheme.onSurfaceVariant,
        selectedContainerColor: Color = AutomotiveTheme.colorScheme.inverseSurface,
        selectedContentColor: Color = AutomotiveTheme.colorScheme.inverseOnSurface
    ): IconButtonToggleOutlineColors = IconButtonToggleOutlineColors(
        selectedContainerColor = selectedContainerColor,
        selectedContentColor = selectedContentColor,
        unselectedContainerColor = unselectedContainerColor,
        unselectedContentColor = unselectedContentColor,
    )
}