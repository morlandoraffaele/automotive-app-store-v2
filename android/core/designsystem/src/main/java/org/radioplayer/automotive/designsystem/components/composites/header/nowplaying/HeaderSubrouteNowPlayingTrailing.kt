package org.radioplayer.automotive.designsystem.components.composites.header.nowplaying

import androidx.compose.runtime.Composable

sealed class HeaderSubrouteNowPlayingTrailing {
    data class ContinueInApp(
        val image: @Composable () -> Unit,
        val label: String? = null,
        val onClick: () -> Unit
    ) : HeaderSubrouteNowPlayingTrailing()

    data class FavoriteToggle(
        val isFavorite: Boolean = false,
        val onFavoriteChange: (Boolean) -> Unit
    ) : HeaderSubrouteNowPlayingTrailing()
    data class IconButtonToggleOutline(
        val selected: Boolean = false,
        val icon: @Composable () -> Unit,
        val onSelect: (Boolean) -> Unit,
    ) : HeaderSubrouteNowPlayingTrailing()
}