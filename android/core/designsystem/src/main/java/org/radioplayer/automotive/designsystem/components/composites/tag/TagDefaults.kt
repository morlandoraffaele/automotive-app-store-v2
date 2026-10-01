package org.radioplayer.automotive.designsystem.components.composites.tag

import android.widget.Space
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import org.radioplayer.automotive.designsystem.subsystems.ColorSchemes
import org.radioplayer.automotive.designsystem.subsystems.Spaces
import org.radioplayer.automotive.designsystem.subsystems.Stroke
import org.radioplayer.automotive.designsystem.theme.AutomotiveTheme

object TagDefaults {
    val colorStyle: TagColorStyle = TagColorStyle.FILLED
    val colors = TagColorSchemes
    val shape = TagShapes

    fun outlinedBorder(isInDarkMode: Boolean): BorderStroke {
        return if (isInDarkMode) {
            BorderStroke(Stroke().thin, ColorSchemes.fromPalette().dark.outline)
        } else {
            BorderStroke(Stroke().thin, ColorSchemes.fromPalette().light.outline)
        }
    }

    @Composable
    fun paddingFor(topology: TagLayoutTopology): PaddingValues {
        return when (topology) {
            TagLayoutTopology.TEXT_ONLY -> PaddingValues(
                horizontal = Spaces().large,
                vertical = Spaces().extraExtraSmall
            )

            TagLayoutTopology.ICON_ONLY -> PaddingValues(
                horizontal = Spaces().medium,
                vertical = Spaces().extraSmall
            )

            TagLayoutTopology.ICON_LABEL -> PaddingValues(
                start = Spaces().extraSmall,
                top = Spaces().extraExtraSmall,
                end = Spaces().medium,
                bottom = Spaces().extraExtraSmall
            )

            TagLayoutTopology.LABEL_ICON -> PaddingValues(
                start = Spaces().medium,
                top = Spaces().extraExtraSmall,
                end = Spaces().extraSmall,
                bottom = Spaces().extraExtraSmall
            )

            TagLayoutTopology.EMPTY -> PaddingValues(0.dp)
        }
    }

}