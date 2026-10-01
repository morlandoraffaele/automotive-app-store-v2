package org.radioplayer.automotive.designsystem.components.composites.listitem.boxed

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import org.radioplayer.automotive.designsystem.components.composites.listitem.ListItemState

@Immutable
data class ListItemBoxedColors (
    val containerColor: Color,
    val contentColor: Color,
    val disabledContainerColor: Color,
    val disableContentColor: Color,
    val dividerColor: Color,
    val headlineColor: Color,
    val supportingColor: Color,
) {

    fun containerColor(state: ListItemState): Color = when(state) {
        ListItemState.ENABLED, ListItemState.FOCUSED, ListItemState.PRESSED, ListItemState.SELECTED, ListItemState.ACTIVE -> containerColor
        ListItemState.DISABLED -> disabledContainerColor.copy(alpha = 0.38f)
    }

    fun contentColor(state: ListItemState): Color = when (state) {
        ListItemState.ENABLED, ListItemState.FOCUSED, ListItemState.PRESSED, ListItemState.SELECTED, ListItemState.ACTIVE  -> contentColor
        ListItemState.DISABLED -> disableContentColor.copy(alpha = 0.38f)
    }

    fun headlineColor(state: ListItemState): Color = when (state) {
        ListItemState.DISABLED -> headlineColor.copy(alpha = 0.38f)
        ListItemState.ACTIVE -> headlineColor
        else -> headlineColor
    }

    fun supportingColor(state: ListItemState): Color = when (state) {
        ListItemState.DISABLED -> supportingColor.copy(alpha = 0.38f)
        ListItemState.ACTIVE -> supportingColor
        else -> supportingColor
    }
}