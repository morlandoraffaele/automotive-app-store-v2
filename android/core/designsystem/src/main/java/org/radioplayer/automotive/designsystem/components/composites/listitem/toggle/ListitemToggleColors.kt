package org.radioplayer.automotive.designsystem.components.composites.listitem.toggle

import androidx.compose.ui.graphics.Color
import org.radioplayer.automotive.designsystem.components.composites.listitem.ListItemState

data class ListItemToggleColors (
    val containerColor: Color,
    val contentColor: Color,
    val disabledContainerColor: Color,
    val disableContentColor: Color,
    val selectedContainerColor: Color,
    val selectedContentColor: Color,
    val dividerColor: Color,
    val headlineColor: Color,
) {

    fun containerColor(state: ListItemState): Color = when(state) {
        ListItemState.ENABLED, ListItemState.FOCUSED, ListItemState.PRESSED, ListItemState.ACTIVE -> containerColor
        ListItemState.DISABLED -> disabledContainerColor.copy(alpha = 0.38f)
        ListItemState.SELECTED -> selectedContainerColor
    }

    fun contentColor(state: ListItemState): Color = when (state) {
        ListItemState.ENABLED, ListItemState.FOCUSED, ListItemState.PRESSED, ListItemState.ACTIVE -> contentColor
        ListItemState.DISABLED -> disableContentColor.copy(alpha = 0.38f)
        ListItemState.SELECTED ->  selectedContentColor
    }

    fun headlineColor(state: ListItemState): Color = when (state) {
        ListItemState.DISABLED -> headlineColor.copy(alpha = 0.38f)
        ListItemState.ACTIVE -> headlineColor
        ListItemState.SELECTED -> selectedContentColor
        else -> headlineColor
    }
}