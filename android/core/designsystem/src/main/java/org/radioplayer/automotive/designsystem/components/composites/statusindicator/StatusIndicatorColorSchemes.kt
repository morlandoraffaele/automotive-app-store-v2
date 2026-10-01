package org.radioplayer.automotive.designsystem.components.composites.statusindicator

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import org.radioplayer.automotive.designsystem.theme.AutomotiveTheme

object StatusIndicatorColorSchemes {
    @Composable
    fun default(): StatusIndicatorColors = StatusIndicatorColors(
        containerColor = AutomotiveTheme.colorScheme.secondaryContainer,
        contentColor = AutomotiveTheme.colorScheme.onSecondaryContainer
    )

    val live = LiveSchemes

    object LiveSchemes {
        @Composable
        fun emphasisHigh(): StatusIndicatorColors = StatusIndicatorColors(
            // TODO: Uncomment when redFixed is added to Palette
            //containerColor = AutomotiveTheme.colorScheme.redFixed,
            containerColor = Color(0xFFB3261E),
            // TODO: Uncomment when onRedFixed is added to Palette
            //contentColor = AutomotiveTheme.colorScheme.onRedFixed
            contentColor = Color(0xFFFFFFFF)
        )

        @Composable
        fun emphasisLow(): StatusIndicatorColors = StatusIndicatorColors(
            containerColor = AutomotiveTheme.colorScheme.surfaceContainer,
            contentColor = AutomotiveTheme.colorScheme.onSurface
        )
    }

}