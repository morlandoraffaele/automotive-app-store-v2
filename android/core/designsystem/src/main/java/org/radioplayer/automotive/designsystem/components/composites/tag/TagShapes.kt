package org.radioplayer.automotive.designsystem.components.composites.tag

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Shape
import org.radioplayer.automotive.designsystem.theme.AutomotiveTheme

object TagShapes {
    @Composable
    fun default(): Shape = RoundedCornerShape(AutomotiveTheme.measurement.shapes.full)

    @Composable
    fun circle(): Shape = RoundedCornerShape(AutomotiveTheme.measurement.shapes.full)
}