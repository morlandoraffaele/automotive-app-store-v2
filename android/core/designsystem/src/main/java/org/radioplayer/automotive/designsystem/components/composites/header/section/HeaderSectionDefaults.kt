package org.radioplayer.automotive.designsystem.components.composites.header.section

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import org.radioplayer.automotive.designsystem.theme.AutomotiveTheme

object HeaderSectionDefaults {

    @Composable
    fun colors(
        style: HeaderSectionStyle = HeaderSectionStyle.LabeledDivider,
        containerColor: Color = if(style == HeaderSectionStyle.LabeledDivider) Color.Transparent else AutomotiveTheme.colorScheme.surfaceContainer,
        contentColor: Color = AutomotiveTheme.colorScheme.onSurface,
        dividerColor: Color = AutomotiveTheme.colorScheme.outlineVariant,
    ): HeaderSectionColors = HeaderSectionColors(
        containerColor = containerColor,
        contentColor = contentColor,
        dividerColor = dividerColor
    )
}