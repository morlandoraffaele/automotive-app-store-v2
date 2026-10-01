package org.radioplayer.automotive.designsystem.components.composites.tag

import androidx.compose.runtime.Composable
import org.radioplayer.automotive.designsystem.theme.AutomotiveTheme

object TagColorSchemes {

    @Composable
    fun filled(): TagColors = TagColors(
        containerColor = AutomotiveTheme.colorScheme.surfaceVariant,
        contentColor = AutomotiveTheme.colorScheme.onSurface
    )

    @Composable
    fun outlined(): TagColors = TagColors(
        containerColor = AutomotiveTheme.colorScheme.surfaceContainer,
        contentColor = AutomotiveTheme.colorScheme.onSurface
    )
}