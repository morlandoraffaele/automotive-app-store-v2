package org.radioplayer.automotive.designsystem.components.composites.header.rail

import androidx.compose.runtime.Composable
import org.radioplayer.automotive.designsystem.components.composites.button.outline.ButtonOutlineShape

sealed class HeaderRailTrailing {
    data class IconSlot(val icon: @Composable () -> Unit) : HeaderRailTrailing()
    data class ButtonSlot(
        val icon: @Composable (() -> Unit)? = null,
        val label: String,
        val shapeVariant: ButtonOutlineShape = ButtonOutlineShape.Rounded,
        val onClick: () -> Unit,
    ) : HeaderRailTrailing()
}
