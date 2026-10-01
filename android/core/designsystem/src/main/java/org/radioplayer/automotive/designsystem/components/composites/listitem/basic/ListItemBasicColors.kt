package org.radioplayer.automotive.designsystem.components.composites.listitem.basic

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import org.radioplayer.automotive.designsystem.components.composites.listitem.ListItemState

/**
 * Container, content and divider colors used by [ListItemDefault], pre-resolved for the
 * enabled/disabled state and, orthogonally, for the active/inactive (selected) state — mirrors
 * the shape of Material3's own `ListItemColors` / `NavigationDrawerItemColors`.
 *
 * Precedence when resolving a slot's color: `!enabled` always wins (a disabled item can't look
 * active), otherwise `active` picks the "selected" variant, otherwise the default variant.
 */
@Immutable
data class ListItemBasicColors(
    val containerColor: Color,
    val contentColor: Color,
    val disabledContainerColor: Color,
    val disableContentColor: Color,
    val dividerColor: Color,
    val activeContainerColor: Color,
    val activeContentColor: Color,
    val headlineColor: Color,
    val supportingColor: Color,
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

    fun supportingColor(state: ListItemState): Color = when (state) {
        ListItemState.DISABLED -> supportingColor.copy(alpha = 0.38f)
        ListItemState.ACTIVE -> supportingColor
        else -> supportingColor
    }

}