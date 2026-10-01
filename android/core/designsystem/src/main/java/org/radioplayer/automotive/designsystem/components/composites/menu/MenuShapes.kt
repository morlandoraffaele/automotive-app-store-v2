package org.radioplayer.automotive.designsystem.components.composites.menu

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import org.radioplayer.automotive.designsystem.theme.AutomotiveTheme

object MenuShapes {
    @Composable
    fun default() = RoundedCornerShape(AutomotiveTheme.measurement.shapes.large)
}