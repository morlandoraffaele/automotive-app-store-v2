package org.radioplayer.automotive.designsystem.components.features.schedule.listitemschedule

import androidx.compose.ui.graphics.Color
import org.radioplayer.automotive.designsystem.components.composites.listitem.ListItemState

data class ListItemScheduleColors(
    val containerColor: Color,
    val containerIsLiveColor: Color,
    val contentColor: Color,
    val disabledContainerColor: Color,
    val disableContentColor: Color,
    val dividerColor: Color,
    val activeContainerColor: Color,
    val activeContentColor: Color,
    val headlineColor: Color,
    val supportingColor: Color,
) {

    fun containerColor(isLive: Boolean, listItemState: ListItemState): Color = when {
        listItemState == ListItemState.DISABLED -> disabledContainerColor
        listItemState == ListItemState.ACTIVE -> activeContainerColor
        isLive -> containerIsLiveColor
        else -> containerColor
    }
}