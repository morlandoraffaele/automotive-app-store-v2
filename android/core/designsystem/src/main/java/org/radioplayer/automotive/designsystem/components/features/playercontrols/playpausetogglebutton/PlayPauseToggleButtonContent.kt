package org.radioplayer.automotive.designsystem.components.features.playercontrols.playpausetogglebutton

/**
 * `Content` variant in the source Figma set (`"Podcast \ Live DVR"` / `"Live Transmission"`) —
 * determines which icon shows while [PlayPauseToggleButton.checked] is true: on-demand/DVR
 * content can be paused and resumed (`pause` glyph), a live broadcast can only be stopped, not
 * resumed from the same point (`stop` glyph). Both content types share the same `play_arrow`
 * glyph while unchecked. Named to match this project's existing `DvrState`/playback-bar
 * terminology rather than the Figma property's own wording.
 */
enum class PlayPauseToggleButtonContent { OnDemand, Live }