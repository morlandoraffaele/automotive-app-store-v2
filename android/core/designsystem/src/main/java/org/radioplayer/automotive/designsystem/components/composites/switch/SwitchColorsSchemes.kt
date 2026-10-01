package org.radioplayer.automotive.designsystem.components.composites.switch

import androidx.compose.material3.SwitchColors
import androidx.compose.runtime.Composable
import org.radioplayer.automotive.designsystem.theme.AutomotiveTheme

object SwitchColorsSchemes {

    @Composable
    fun default(): SwitchColors = SwitchColors(
        checkedThumbColor = AutomotiveTheme.colorScheme.onPrimary,
        checkedTrackColor = AutomotiveTheme.colorScheme.primary,
        checkedBorderColor = AutomotiveTheme.colorScheme.primary,
        checkedIconColor = AutomotiveTheme.colorScheme.onPrimaryContainer,
        uncheckedThumbColor = AutomotiveTheme.colorScheme.outline,
        uncheckedTrackColor = AutomotiveTheme.colorScheme.surfaceContainerHighest,
        uncheckedBorderColor = AutomotiveTheme.colorScheme.outline,
        uncheckedIconColor = AutomotiveTheme.colorScheme.surfaceContainerHighest,
        // TODO: Add disabled colors
        disabledCheckedThumbColor = AutomotiveTheme.colorScheme.primary,
        disabledCheckedTrackColor = AutomotiveTheme.colorScheme.primary,
        disabledCheckedBorderColor = AutomotiveTheme.colorScheme.primary,
        disabledCheckedIconColor = AutomotiveTheme.colorScheme.primary,
        disabledUncheckedThumbColor = AutomotiveTheme.colorScheme.primary,
        disabledUncheckedTrackColor = AutomotiveTheme.colorScheme.primary,
        disabledUncheckedBorderColor = AutomotiveTheme.colorScheme.primary,
        disabledUncheckedIconColor = AutomotiveTheme.colorScheme.primary
    )
}