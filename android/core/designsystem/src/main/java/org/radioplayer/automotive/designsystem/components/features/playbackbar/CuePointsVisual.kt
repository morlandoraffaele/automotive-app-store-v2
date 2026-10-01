package org.radioplayer.automotive.designsystem.components.features.playbackbar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import org.radioplayer.automotive.designsystem.theme.AutomotiveTheme

/**
 * Purely visual — cue-point *interaction* (tap-to-jump/snap) happens once, centrally, in
 * [DraggableTrack]'s own `onValueChange`, not via a second gesture layer here.
 */
@Composable
internal fun CuePointsVisual(
    cuePoints: List<CuePoint>,
    bufferDurationSeconds: Float,
    activeFraction: Float,
    colors: PlaybackBarColors,
    trackWidth: Dp
) {
    Box(Modifier.fillMaxSize()) {
        cuePoints.forEach { cue ->
            val cueFraction = (cue.offsetSeconds / bufferDurationSeconds).coerceIn(0f, 1f)
            val onActiveSide = cueFraction <= activeFraction
            val slotX = (trackWidth - PlaybackBarDefaults.HandleWidth) * cueFraction
            val x = slotX + (PlaybackBarDefaults.HandleWidth - PlaybackBarDefaults.CuePointSize) / 2
            Box(
                Modifier
                    .offset(x = x, y = 0.dp)
                    .align(Alignment.CenterStart)
                    .size(PlaybackBarDefaults.CuePointSize)
                    .clip(RoundedCornerShape(AutomotiveTheme.measurement.shapes.full))
                    .background(if (onActiveSide) colors.cuePointOnActiveTrack else colors.cuePointOnInactiveTrack)
            )
        }
    }
}
