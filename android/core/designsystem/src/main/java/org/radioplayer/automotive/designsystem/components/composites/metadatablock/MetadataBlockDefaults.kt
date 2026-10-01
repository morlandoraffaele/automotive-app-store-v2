package org.radioplayer.automotive.designsystem.components.composites.metadatablock

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import org.radioplayer.automotive.designsystem.theme.AutomotiveTheme

object MetadataBlockDefaults {

    @Composable
    fun artworkShape(): Shape = RoundedCornerShape(AutomotiveTheme.measurement.shapes.largeIncreased)

    @Composable
    fun titleColor(): Color = AutomotiveTheme.colorScheme.onSurface

    @Composable
    fun subtitleColor(): Color = AutomotiveTheme.colorScheme.onSurfaceVariant

    @Composable
    fun scrimColor(): Color = AutomotiveTheme.colorScheme.scrim.copy(alpha = 0.8f)
}