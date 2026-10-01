package org.radioplayer.automotive.designsystem.components.features.playbackbar

import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import kotlin.math.abs
import org.radioplayer.automotive.designsystem.theme.AutomotiveTheme

@Composable
private fun handleShapeFor(shape: PlaybackHandleShape): Shape = when (shape) {
    PlaybackHandleShape.Square -> RoundedCornerShape(AutomotiveTheme.measurement.shapes.extraSmall)
    PlaybackHandleShape.Round -> CircleShape
    PlaybackHandleShape.Pill -> RoundedCornerShape(AutomotiveTheme.measurement.shapes.full)
}

/** Rounded-pill track shape: fully-rounded on the outer edge, small radius on the inner edge (matches Figma). */
@Composable
internal fun activeTrackShape(startRounded: Boolean): Shape = RoundedCornerShape(
    topStart = if (startRounded) AutomotiveTheme.measurement.shapes.full else AutomotiveTheme.measurement.shapes.small,
    bottomStart = if (startRounded) AutomotiveTheme.measurement.shapes.full else AutomotiveTheme.measurement.shapes.small,
    topEnd = if (startRounded) AutomotiveTheme.measurement.shapes.small else AutomotiveTheme.measurement.shapes.full,
    bottomEnd = if (startRounded) AutomotiveTheme.measurement.shapes.small else AutomotiveTheme.measurement.shapes.full
)

/**
 * The Material3 `Slider` wrapper shared by [OnDemandPlaybackBar] and [DvrRadioPlaybackBar].
 * Owns the Slider's state, its rotary/keyboard focus ring, and cue-point snapping (see below),
 * while the actual track/thumb *drawing* lives in the `track`/`thumb` slots.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun DraggableTrack(
    positionSeconds: Float,
    durationSeconds: Float,
    onSeekStart: () -> Unit,
    onSeek: (Float) -> Unit,
    onSeekEnd: () -> Unit,
    trackHeight: Dp,
    handleShape: PlaybackHandleShape,
    cuePoints: List<CuePoint>,
    onCuePointClick: (CuePoint) -> Unit,
    colors: PlaybackBarColors,
    modifier: Modifier = Modifier
) {
    val safeDuration = durationSeconds.coerceAtLeast(0.001f)
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()
    val isPressed by interactionSource.collectIsPressedAsState()
    val showFocusRing = isFocused && !isPressed

    var pendingCueSnap by remember { mutableStateOf<CuePoint?>(null) }

    val sliderState = remember(safeDuration) {
        SliderState(value = positionSeconds, valueRange = 0f..safeDuration)
    }
    LaunchedEffect(positionSeconds) { sliderState.value = positionSeconds }
    sliderState.onValueChange = { newValue ->
        onSeekStart()
        val tolerance = safeDuration * PlaybackBarDefaults.CuePointSnapTolerance
        val nearestCue = cuePoints
            .minByOrNull { abs(it.offsetSeconds - newValue) }
            ?.takeIf { abs(it.offsetSeconds - newValue) <= tolerance }
        pendingCueSnap = nearestCue
        onSeek((nearestCue?.offsetSeconds ?: newValue).coerceIn(0f, safeDuration))
    }
    LaunchedEffect(sliderState) {
        sliderState.onValueChangeFinished = {
            pendingCueSnap?.let(onCuePointClick)
            pendingCueSnap = null
            onSeekEnd()
        }
    }

    Slider(
        state = sliderState,
        modifier = modifier,
        interactionSource = interactionSource,
        thumb = { Box(Modifier.size(DpSize(1.dp, PlaybackBarDefaults.HandleTouchTargetHeight))) },
        track = {
            val fraction = (positionSeconds / safeDuration).coerceIn(0f, 1f)
            BoxWithConstraints(
                Modifier
                    .fillMaxWidth()
                    .height(PlaybackBarDefaults.HandleTouchTargetHeight)
            ) {
                val handleX = (maxWidth - PlaybackBarDefaults.HandleWidth) * fraction
                val activeWidth = (handleX - PlaybackBarDefaults.TrackGap).coerceAtLeast(0.dp)
                val inactiveStart =
                    handleX + PlaybackBarDefaults.HandleWidth + PlaybackBarDefaults.TrackGap
                val inactiveWidth = (maxWidth - inactiveStart).coerceAtLeast(0.dp)

                if (activeWidth > 0.dp) {
                    Box(
                        Modifier
                            .align(Alignment.CenterStart)
                            .width(activeWidth)
                            .height(trackHeight)
                            .clip(activeTrackShape(startRounded = true))
                            .background(colors.activeTrack)
                    )
                }
                Box(
                    Modifier
                        .offset(x = handleX)
                        .align(Alignment.CenterStart)
                        .size(
                            width = PlaybackBarDefaults.HandleWidth,
                            height = PlaybackBarDefaults.HandleHeightInteractive
                        )
                        .clip(handleShapeFor(handleShape))
                        .background(colors.handle)
                )
                if (inactiveWidth > 0.dp) {
                    Box(
                        Modifier
                            .offset(x = inactiveStart)
                            .align(Alignment.CenterStart)
                            .width(inactiveWidth)
                            .height(trackHeight)
                            .clip(activeTrackShape(startRounded = false))
                            .background(colors.inactiveTrack)
                    )
                }

                if (showFocusRing) {
                    val ringWidth = PlaybackBarDefaults.HandleWidth + 10.dp
                    val ringHeight = PlaybackBarDefaults.HandleHeightInteractive + 10.dp
                    Box(
                        Modifier
                            .offset(x = handleX - 5.dp, y = 0.dp)
                            .align(Alignment.CenterStart)
                            .size(width = ringWidth, height = ringHeight)
                            .clip(handleShapeFor(handleShape))
                            .background(colors.handleFocusRing)
                    )
                }

                if (cuePoints.isNotEmpty()) {
                    CuePointsVisual(
                        cuePoints = cuePoints,
                        bufferDurationSeconds = safeDuration,
                        activeFraction = fraction,
                        colors = colors,
                        trackWidth = maxWidth
                    )
                }
            }
        }
    )
}
