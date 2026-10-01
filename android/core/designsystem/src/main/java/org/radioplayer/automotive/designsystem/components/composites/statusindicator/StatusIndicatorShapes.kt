package org.radioplayer.automotive.designsystem.components.composites.statusindicator

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Shape
import org.radioplayer.automotive.designsystem.theme.AutomotiveTheme

object StatusIndicatorShapes {
    @Composable
    fun default(): Shape = RoundedCornerShape(AutomotiveTheme.measurement.shapes.small)
}