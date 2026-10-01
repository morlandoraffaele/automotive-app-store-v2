package org.radioplayer.automotive.designsystem.components.features.playbackbar

/** A single jump point in a DVR buffer, positioned by real elapsed-seconds offset. */
data class CuePoint(
    val label: String,
    val offsetSeconds: Float
)

/** Which end of the [PlaybackHandleShape] variants Figma exposes; all three current usages default to [Pill]. */
enum class PlaybackHandleShape { Square, Round, Pill }

/** DVR Radio's two states — see [DvrRadioPlaybackBar]. */
enum class DvrState { AtLiveEdge, TimeShifted }
