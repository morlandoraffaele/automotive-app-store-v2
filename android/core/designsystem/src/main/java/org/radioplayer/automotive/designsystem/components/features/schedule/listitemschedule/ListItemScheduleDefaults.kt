package org.radioplayer.automotive.designsystem.components.features.schedule.listitemschedule

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import org.radioplayer.automotive.designsystem.theme.AutomotiveTheme

object ListItemScheduleDefaults {
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
        headlineColor: Color = AutomotiveTheme.colorScheme.onSurfaceVariant,
        supportingColor: Color = AutomotiveTheme.colorScheme.onSurfaceVariant,
    ): ListItemScheduleColors = ListItemScheduleColors(
        containerColor = containerColor,
        containerIsLiveColor = containerIsLiveColor,
        contentColor = contentColor,
        disabledContainerColor = containerColor,
        disableContentColor = contentColor,
        dividerColor = dividerColor,
        activeContainerColor = activeContainerColor,
        activeContentColor = activeContentColor,
        headlineColor = headlineColor,
        supportingColor = supportingColor
    )
}