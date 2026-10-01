package org.radioplayer.automotive.designsystem.components.composites.statusindicator

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import org.radioplayer.automotive.designsystem.subsystems.Spaces

object StatusIndicatorDefaults {
    val colors = StatusIndicatorColorSchemes
    val shape = StatusIndicatorShapes

    @Composable
    fun paddingFor(topology: StatusIndicatorLayoutTopology): PaddingValues {
        return when (topology) {
            StatusIndicatorLayoutTopology.TEXT_ONLY -> PaddingValues(
                horizontal = Spaces().large,
                vertical = Spaces().extraExtraSmall
            )

            StatusIndicatorLayoutTopology.ICON_LABEL -> PaddingValues(
                start = Spaces().medium,
                top = Spaces().extraExtraSmall,
                end = Spaces().medium,
                bottom = Spaces().extraExtraSmall
            )

            StatusIndicatorLayoutTopology.EMPTY -> PaddingValues(0.dp)
        }
    }
}