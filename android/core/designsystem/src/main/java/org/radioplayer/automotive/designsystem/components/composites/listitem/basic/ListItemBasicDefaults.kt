package org.radioplayer.automotive.designsystem.components.composites.listitem.basic

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import org.radioplayer.automotive.designsystem.theme.AutomotiveTheme

object ListItemBasicDefaults {
    // TODO: use LocalRippleConfiguration to get alpha values applied globally
    const val DisabledContentAlpha = 0.38f

    @Composable
    fun colors(
        containerColor: Color = Color.Transparent,
        contentColor: Color = AutomotiveTheme.colorScheme.onSurface,
        disabledContainerColor: Color = containerColor,
        disabledContentColor: Color = contentColor,
        dividerColor: Color = AutomotiveTheme.colorScheme.outlineVariant,
        disabledDividerColor: Color =  AutomotiveTheme.colorScheme.outlineVariant,
        activeContainerColor: Color = AutomotiveTheme.colorScheme.primaryContainer,
        activeContentColor: Color = AutomotiveTheme.colorScheme.onSurfaceVariant,
        headlineColor: Color = AutomotiveTheme.colorScheme.onSurfaceVariant,
        supportingColor: Color = AutomotiveTheme.colorScheme.onSurfaceVariant,
    ): ListItemBasicColors = ListItemBasicColors(
        containerColor = containerColor,
        contentColor = contentColor,
        disabledContainerColor = containerColor.copy(alpha = DisabledContentAlpha),
        disableContentColor = contentColor.copy(alpha = DisabledContentAlpha),
        dividerColor = dividerColor,
        activeContainerColor = activeContainerColor,
        activeContentColor = activeContentColor,
        headlineColor = headlineColor,
        supportingColor = supportingColor
    )
}