package org.radioplayer.automotive.designsystem.components.composites.miniplayer

import androidx.compose.foundation.BorderStroke
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import org.radioplayer.automotive.designsystem.theme.AutomotiveTheme

object MiniplayerDefaults {
    val containerWidth = 468.dp
    val containerWidthArtwork = 80.dp
    val shapes = MiniplayerShapes

    @Composable
    fun border(): BorderStroke = BorderStroke(AutomotiveTheme.stroke.thin, AutomotiveTheme.colorScheme.outline)
}