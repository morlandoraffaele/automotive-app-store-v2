package org.radioplayer.automotive.designsystem.components.composites.switch

import androidx.compose.material3.SwitchColors
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import org.radioplayer.automotive.designsystem.subsystems.Sizes

object SwitchDefaults {
    const val checked: Boolean = false
    const val enabled: Boolean = true
    val colors = SwitchColorsSchemes

    /**
     *  TODO: iconSize value should be moved to Sizes (subsystems) and be used here.
     *  e.g val iconSize: Dp = Sizes.iconSize
     */
    val iconSize: Dp = 16.dp
}