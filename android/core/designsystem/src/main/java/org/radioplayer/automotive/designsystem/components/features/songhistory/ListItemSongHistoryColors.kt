package org.radioplayer.automotive.designsystem.components.features.songhistory

import androidx.compose.ui.graphics.Color
import org.radioplayer.automotive.designsystem.components.composites.listitem.ListItemState

data class ListItemSongHistoryColors(
    val containerColor: Color,
    val contentColor: Color,
    val disabledContainerColor: Color,
    val disableContentColor: Color,
    val dividerColor: Color,
    val activeContainerColor: Color,
    val activeContentColor: Color,
    val headlineColor: Color,
    val supportingColor: Color,
    val extraColor: Color,
) {

    fun containerColor(listItemState: ListItemState): Color = when {
        listItemState == ListItemState.DISABLED -> disabledContainerColor
        listItemState == ListItemState.ACTIVE -> activeContainerColor
        else -> containerColor
    }
}