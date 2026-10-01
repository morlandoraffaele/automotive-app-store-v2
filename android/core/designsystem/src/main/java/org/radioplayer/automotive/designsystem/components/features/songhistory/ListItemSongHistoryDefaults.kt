package org.radioplayer.automotive.designsystem.components.features.songhistory

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import org.radioplayer.automotive.designsystem.components.features.schedule.listitemschedule.ListItemScheduleColors
import org.radioplayer.automotive.designsystem.theme.AutomotiveTheme

object ListItemSongHistoryDefaults {
    // TODO: use LocalRippleConfiguration to get alpha values applied globally
    const val DisabledContentAlpha = 0.38f

    @Composable
    fun colors(
        containerColor: Color = Color.Transparent,
        contentColor: Color = AutomotiveTheme.colorScheme.onSurface,
        containerIsLiveColor: Color = AutomotiveTheme.colorScheme.surfaceContainerHigh,
        disabledContainerColor: Color = containerColor,
        disabledContentColor: Color = contentColor,
        dividerColor: Color = AutomotiveTheme.colorScheme.outlineVariant,
        disabledDividerColor: Color =  AutomotiveTheme.colorScheme.outlineVariant,
        activeContainerColor: Color = AutomotiveTheme.colorScheme.primaryContainer,
        activeContentColor: Color = AutomotiveTheme.colorScheme.onSurfaceVariant,
        headlineColor: Color = AutomotiveTheme.colorScheme.onSurface,
        supportingColor: Color = AutomotiveTheme.colorScheme.onSurfaceVariant,
        extraColor: Color = AutomotiveTheme.colorScheme.onSurfaceVariant,
    ): ListItemSongHistoryColors = ListItemSongHistoryColors(
        containerColor = containerColor,
        contentColor = contentColor,
        disabledContainerColor = containerColor,
        disableContentColor = contentColor,
        dividerColor = dividerColor,
        activeContainerColor = activeContainerColor,
        activeContentColor = activeContentColor,
        headlineColor = headlineColor,
        supportingColor = supportingColor,
        extraColor = extraColor
    )
}