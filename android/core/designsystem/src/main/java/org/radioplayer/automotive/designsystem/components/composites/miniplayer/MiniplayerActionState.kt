package org.radioplayer.automotive.designsystem.components.composites.miniplayer

sealed interface MiniplayerActionState {
    data object Play : MiniplayerActionState
    data object Pause : MiniplayerActionState
    data object Stop : MiniplayerActionState
}