package org.radioplayer.automotive.designsystem.components.composites.listitem.toggle

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import org.radioplayer.automotive.designsystem.components.composites.listitem.tagged.ListItemTaggedColors
import org.radioplayer.automotive.designsystem.components.composites.listitem.tagged.ListItemTaggedDefaults
import org.radioplayer.automotive.designsystem.theme.AutomotiveTheme

object ListItemToggleDefaults {
    const val DisabledContentAlpha = 0.38f

    @Composable
    fun colors(
        containerColor: Color = Color.Transparent,
        contentColor: Color = AutomotiveTheme.colorScheme.onSurface,
        disabledContainerColor: Color = containerColor,
        disabledContentColor: Color = contentColor,
        dividerColor: Color = AutomotiveTheme.colorScheme.outlineVariant,
        disabledDividerColor: Color = AutomotiveTheme.colorScheme.outlineVariant,
        headlineColor: Color = AutomotiveTheme.colorScheme.onSurfaceVariant,
        selectedContainerColor: Color = AutomotiveTheme.colorScheme.inverseSurface,
        selectedContentColor: Color = AutomotiveTheme.colorScheme.inverseOnSurface
    ): ListItemToggleColors = ListItemToggleColors(
        containerColor = containerColor,
        contentColor = contentColor,
        disabledContainerColor = containerColor.copy(alpha = ListItemTaggedDefaults.DisabledContentAlpha),
        disableContentColor = contentColor.copy(alpha = ListItemTaggedDefaults.DisabledContentAlpha),
        dividerColor = dividerColor,
        headlineColor = headlineColor,
        selectedContainerColor = selectedContainerColor,
        selectedContentColor = selectedContentColor
    )
}