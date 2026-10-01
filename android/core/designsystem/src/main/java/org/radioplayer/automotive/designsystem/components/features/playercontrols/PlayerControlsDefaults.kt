package org.radioplayer.automotive.designsystem.components.features.playercontrols

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import org.radioplayer.automotive.designsystem.components.composites.iconbutton.standard.IconButtonStandardColors
import org.radioplayer.automotive.designsystem.theme.AutomotiveTheme

object PlayerControlsDefaults {
    const val enabled: Boolean = true
    const val compact: Boolean = false
    val surface = PlayerControlsSurface.Standard
    val iconPosition = PlayerControlsIconPosition.Leading

    @Composable
    fun shape(): Shape = RoundedCornerShape(AutomotiveTheme.measurement.shapes.extraLarge)

    @Composable
    fun size(compact: Boolean): Pair<Dp, Dp> {
        val spaces = AutomotiveTheme.measurement.spaces
        val iconSize = AutomotiveTheme.icon.hero
        val height = iconSize + spaces.medium * 2
        if (compact) {
            val side = iconSize + spaces.medium * 2
            return side to side
        }
        val width = spaces.extraSmall + iconSize + spaces.medium + iconSize + spaces.medium
        return width to height
    }

    @Composable
    fun horizontalContentPadding(compact: Boolean, iconPosition: PlayerControlsIconPosition): Pair<Dp, Dp> {
        val extraSmall = AutomotiveTheme.measurement.spaces.extraSmall
        val medium = AutomotiveTheme.measurement.spaces.medium
        if (compact) return medium to medium
        return when (iconPosition) {
            PlayerControlsIconPosition.Leading -> extraSmall to medium
            PlayerControlsIconPosition.Trailing -> medium to extraSmall
        }
    }

    @Composable
    fun colors(surface: PlayerControlsSurface): IconButtonStandardColors = IconButtonStandardColors(
        containerColor = when (surface) {
            PlayerControlsSurface.Standard -> AutomotiveTheme.colorScheme.surfaceContainer
            PlayerControlsSurface.OnMedia -> AutomotiveTheme.colorScheme.surfaceContainerOverlay
        },
        contentColor = AutomotiveTheme.colorScheme.onSurfaceVariant,
    )

    @Composable
    fun border(): BorderStroke = BorderStroke(AutomotiveTheme.stroke.thin, AutomotiveTheme.colorScheme.outlineVariant)


    @Composable
    fun focusRingColor(): Color = AutomotiveTheme.colorScheme.primary
}
