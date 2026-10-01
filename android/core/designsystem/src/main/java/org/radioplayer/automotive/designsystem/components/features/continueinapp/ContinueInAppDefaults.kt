package org.radioplayer.automotive.designsystem.components.features.continueinapp

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import org.radioplayer.automotive.designsystem.theme.AutomotiveTheme

object ContinueInAppDefaults {
    val containerImageWidth = 64.dp

    @Composable
    fun colors(
        containerColor: Color = AutomotiveTheme.colorScheme.surfaceContainer,
        contentColor: Color = AutomotiveTheme.colorScheme.onSurface,
    ): ContinueInAppColors = ContinueInAppColors(
        containerColor = containerColor,
        contentColor = contentColor
    )

}