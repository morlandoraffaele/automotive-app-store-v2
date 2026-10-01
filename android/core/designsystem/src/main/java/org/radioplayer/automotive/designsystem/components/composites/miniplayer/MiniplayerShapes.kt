package org.radioplayer.automotive.designsystem.components.composites.miniplayer

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Shape
import org.radioplayer.automotive.designsystem.theme.AutomotiveTheme

object MiniplayerShapes {
    @Composable
    fun default(): Shape = RoundedCornerShape(AutomotiveTheme.measurement.shapes.large)
}