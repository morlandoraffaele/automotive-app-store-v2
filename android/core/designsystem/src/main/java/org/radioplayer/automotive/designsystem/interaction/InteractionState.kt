package org.radioplayer.automotive.designsystem.interaction

import androidx.compose.foundation.interaction.DragInteraction
import androidx.compose.foundation.interaction.FocusInteraction
import androidx.compose.foundation.interaction.HoverInteraction
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember

/**
 * Aggregated, binary snapshot of the interactions currently active on an [InteractionSource].
 *
 * Generic across the design system — any primitive or composite that wires a
 * [androidx.compose.foundation.interaction.MutableInteractionSource] into `clickable`,
 * `hoverable`, `focusable`, `draggable`, `selectable`, etc. can consume this. A component
 * that never emits one of these interaction types (e.g. a non-draggable Chip) will simply
 * always read `false` for it, at no extra cost.
 */
data class InteractionState(
    val isPressed: Boolean = false,
    val isHovered: Boolean = false,
    val isFocused: Boolean = false,
    val isDragged: Boolean = false,
)

/**
 * Collects raw [androidx.compose.foundation.interaction.Interaction]s from [interactionSource]
 * exactly once and derives a single [InteractionState] from them.
 */
@Composable
fun rememberInteractionState(interactionSource: InteractionSource): State<InteractionState> {
    val state = remember { mutableStateOf(InteractionState()) }

    LaunchedEffect(interactionSource) {
        val pressed = mutableListOf<PressInteraction.Press>()
        val hovered = mutableListOf<HoverInteraction.Enter>()
        val focused = mutableListOf<FocusInteraction.Focus>()
        val dragged = mutableListOf<DragInteraction.Start>()

        interactionSource.interactions.collect { interaction ->
            when (interaction) {
                is PressInteraction.Press -> pressed.add(interaction)
                is PressInteraction.Release -> pressed.remove(interaction.press)
                is PressInteraction.Cancel -> pressed.remove(interaction.press)

                is HoverInteraction.Enter -> hovered.add(interaction)
                is HoverInteraction.Exit -> hovered.remove(interaction.enter)

                is FocusInteraction.Focus -> focused.add(interaction)
                is FocusInteraction.Unfocus -> focused.remove(interaction.focus)

                is DragInteraction.Start -> dragged.add(interaction)
                is DragInteraction.Stop -> dragged.remove(interaction.start)
                is DragInteraction.Cancel -> dragged.remove(interaction.start)

                else -> Unit
            }

            state.value = InteractionState(
                isPressed = pressed.isNotEmpty(),
                isHovered = hovered.isNotEmpty(),
                isFocused = focused.isNotEmpty(),
                isDragged = dragged.isNotEmpty(),
            )
        }
    }

    return state
}