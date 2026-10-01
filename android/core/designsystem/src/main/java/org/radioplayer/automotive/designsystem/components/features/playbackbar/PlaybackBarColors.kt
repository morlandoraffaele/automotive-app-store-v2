package org.radioplayer.automotive.designsystem.components.features.playbackbar

import androidx.compose.ui.graphics.Color

/**
 * The full set of colors a playback bar can draw. Every value defaults to a standard
 * `AutomotiveTheme.colorScheme` role (see [PlaybackBarDefaults.colors]), so the component
 * re-themes automatically for whatever M3 color scheme the host app defines.
 */
data class PlaybackBarColors(
    val activeTrack: Color,
    val inactiveTrack: Color,
    val handle: Color,
    val handleFocusRing: Color,
    val cuePointOnActiveTrack: Color,
    val cuePointOnInactiveTrack: Color,
    val timestampText: Color,
    val liveBadgeContainer: Color,
    val liveBadgeContent: Color,
    val goLiveBadgeContainer: Color,
    val goLiveBadgeContent: Color
)
