package org.radioplayer.automotive.designsystem.components.composites.menu

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import org.radioplayer.automotive.designsystem.subsystems.Sizes
import org.radioplayer.automotive.designsystem.subsystems.Spaces
import org.radioplayer.automotive.designsystem.theme.AutomotiveTheme

object MenuDefaults {
    val shapes = MenuShapes
    val containerMaxWidth = 376.dp

    @Composable
    fun colors(
        containerColor: Color = AutomotiveTheme.colorScheme.surfaceContainerHigh,
        contentColor: Color = AutomotiveTheme.colorScheme.onSurface,
        supportingContentColor: Color = AutomotiveTheme.colorScheme.onSurfaceVariant,
        leadingIconColor: Color = AutomotiveTheme.colorScheme.onSurface,
        trailingIconColor: Color = AutomotiveTheme.colorScheme.onSurface,
    ): MenuColors = MenuColors(
        containerColor = containerColor,
        contentColor = contentColor,
        supportingContentColor = supportingContentColor,
        leadingIconColor = leadingIconColor,
        trailingIconColor = trailingIconColor,
    )

    @Composable
    fun itemColors(
        selectedContainerColor: Color = AutomotiveTheme.colorScheme.inverseSurface,
        unselectedContainerColor: Color = Color.Transparent,
        selectedContentColor: Color = AutomotiveTheme.colorScheme.inverseOnSurface,
        unselectedContentColor: Color = AutomotiveTheme.colorScheme.onSurface,
        selectedSupportingContentColor: Color = AutomotiveTheme.colorScheme.inverseOnSurface,
        unselectedSupportingContentColor: Color = AutomotiveTheme.colorScheme.onSurfaceVariant,
        selectedLeadingIconColor: Color = AutomotiveTheme.colorScheme.inverseOnSurface,
        unselectedLeadingIconColor: Color = AutomotiveTheme.colorScheme.onSurface,
        selectedTrailingIconColor: Color = AutomotiveTheme.colorScheme.inverseOnSurface,
        unselectedTrailingIconColor: Color = AutomotiveTheme.colorScheme.onSurface,
    ): MenuItemColors = MenuItemColors(
        selectedContainerColor = selectedContainerColor,
        unselectedContainerColor = unselectedContainerColor,
        selectedContentColor = selectedContentColor,
        unselectedContentColor = unselectedContentColor,
        selectedSupportingContentColor = selectedSupportingContentColor,
        unselectedSupportingContentColor = unselectedSupportingContentColor,
        selectedLeadingIconColor = selectedLeadingIconColor,
        unselectedLeadingIconColor = unselectedLeadingIconColor,
        selectedTrailingIconColor = selectedTrailingIconColor,
        unselectedTrailingIconColor = unselectedTrailingIconColor,
    )
}