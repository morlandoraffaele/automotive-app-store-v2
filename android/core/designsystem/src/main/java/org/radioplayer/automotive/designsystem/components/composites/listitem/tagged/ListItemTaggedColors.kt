package org.radioplayer.automotive.designsystem.components.composites.listitem.tagged

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import org.radioplayer.automotive.designsystem.components.composites.listitem.ListItemState

@Immutable
data class ListItemTaggedColors(
    val containerColor: Color,
    val contentColor: Color,
    val disabledContainerColor: Color,
    val disableContentColor: Color,
    val dividerColor: Color,
    val activeContainerColor: Color,
    val activeContentColor: Color,
    val headlineColor: Color,
    val tagsColor: Color,
) {

    fun containerColor(state: ListItemState): Color = when(state) {
        ListItemState.ENABLED, ListItemState.FOCUSED, ListItemState.PRESSED, ListItemState.SELECTED -> containerColor
        ListItemState.DISABLED -> disabledContainerColor.copy(alpha = 0.38f)
        ListItemState.ACTIVE -> activeContainerColor
    }

    fun contentColor(state: ListItemState): Color = when (state) {
        ListItemState.ENABLED, ListItemState.FOCUSED, ListItemState.PRESSED, ListItemState.SELECTED -> contentColor
        ListItemState.DISABLED -> disableContentColor.copy(alpha = 0.38f)
        ListItemState.ACTIVE -> activeContentColor
    }

    fun headlineColor(state: ListItemState): Color = when (state) {
        ListItemState.DISABLED -> headlineColor.copy(alpha = 0.38f)
        ListItemState.ACTIVE -> headlineColor
        else -> headlineColor
    }

    fun tagsColor(state: ListItemState): Color = when (state) {
        ListItemState.DISABLED -> tagsColor.copy(alpha = 0.38f)
        ListItemState.ACTIVE -> tagsColor
        else -> tagsColor
    }

}