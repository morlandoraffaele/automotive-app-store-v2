package org.radioplayer.automotive.designsystem.components.composites.search

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import org.radioplayer.automotive.designsystem.theme.AutomotiveTheme

object SearchInputFieldDefaults {
    const val DisabledContentAlpha = 0.38f

    @Composable
    fun colors(
        containerColor: Color = AutomotiveTheme.colorScheme.surfaceContainerHigh,
        contentColor: Color = AutomotiveTheme.colorScheme.onSurface,
        voiceOnlyContentColor: Color = AutomotiveTheme.colorScheme.onSurfaceVariant,
        focusRingColor: Color = AutomotiveTheme.colorScheme.primary,
    ): SearchInputFieldColors = SearchInputFieldColors(
        containerColor = containerColor,
        contentColor = contentColor,
        voiceOnlyContentColor = voiceOnlyContentColor,
        focusRingColor = focusRingColor,
    )
}