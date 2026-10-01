package org.radioplayer.automotive.designsystem.components.features.playbackbar

import android.content.res.Configuration
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.progressBarRangeInfo
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import org.radioplayer.automotive.designsystem.theme.AutomotiveTheme

@Composable
fun LiveRadioPlaybackBar(
    modifier: Modifier = Modifier,
    trackHeight: Dp = PlaybackBarDefaults.TrackHeightDefault,
    colors: PlaybackBarColors = PlaybackBarDefaults.colors()
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(PlaybackBarDefaults.BarHeight),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(
            modifier = Modifier
                .weight(1f)
                .height(PlaybackBarDefaults.HandleSizeStatic)
                .semantics {
                    contentDescription = "Live"
                    progressBarRangeInfo = ProgressBarRangeInfo(
                        current = 1f, range = 0f..1f
                    )
                },
            horizontalArrangement = Arrangement.spacedBy(PlaybackBarDefaults.TrackGap),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                Modifier
                    .weight(1f)
                    .height(trackHeight)
                    .clip(activeTrackShape(startRounded = true))
                    .background(colors.activeTrack)
            )
            Box(
                Modifier
                    .size(PlaybackBarDefaults.HandleSizeStatic)
                    .clip(RoundedCornerShape(AutomotiveTheme.measurement.shapes.full))
                    .background(colors.handle)
            )
        }

        StatusBadge(
            label = "Live",
            containerColor = colors.liveBadgeContainer,
            contentColor = colors.liveBadgeContent
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OnDemandPlaybackBar(
    modifier: Modifier = Modifier,
    positionSeconds: Float,
    durationSeconds: Float,
    onSeekStart: () -> Unit = {},
    onSeek: (Float) -> Unit,
    onSeekEnd: () -> Unit = {},
    handleShape: PlaybackHandleShape = PlaybackHandleShape.Pill,
    colors: PlaybackBarColors = PlaybackBarDefaults.colors(),
    formatTimestamp: (Float) -> String = PlaybackBarDefaults::formatElapsed
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(PlaybackBarDefaults.BarHeight),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Timestamp(text = formatTimestamp(positionSeconds), color = colors.timestampText)

        DraggableTrack(
            positionSeconds = positionSeconds,
            durationSeconds = durationSeconds,
            onSeekStart = onSeekStart,
            onSeek = onSeek,
            onSeekEnd = onSeekEnd,
            trackHeight = PlaybackBarDefaults.TrackHeightDefault,
            handleShape = handleShape,
            cuePoints = emptyList(),
            onCuePointClick = {},
            colors = colors,
            modifier = Modifier
                .weight(1f)
        )

        Timestamp(text = formatTimestamp(durationSeconds), color = colors.timestampText)
    }
}

// -----------------------------------------------------------------------------------------
// 3 & 4. DVR Radio — draggable, freely-positioned cue points, two states
// -----------------------------------------------------------------------------------------

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DvrRadioPlaybackBar(
    modifier: Modifier = Modifier,
    dvrState: DvrState,
    positionSeconds: Float,
    bufferDurationSeconds: Float,
    cuePoints: List<CuePoint> = emptyList(),
    onSeekStart: () -> Unit = {},
    onSeek: (Float) -> Unit,
    onSeekEnd: () -> Unit = {},
    onCuePointClick: (CuePoint) -> Unit = {},
    onGoLive: () -> Unit = {},
    handleShape: PlaybackHandleShape = PlaybackHandleShape.Pill,
    colors: PlaybackBarColors = PlaybackBarDefaults.colors(),
    formatTimeOffset: (Float) -> String = PlaybackBarDefaults::formatOffset
) {
    val settledState = rememberSettled(dvrState)

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(PlaybackBarDefaults.BarHeight),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AnimatedVisibility(
            visible = settledState == DvrState.TimeShifted,
            enter = fadeIn(
                tween(
                    PlaybackBarDefaults.TimestampEnterDurationMillis,
                    easing = PlaybackBarDefaults.EmphasizedDecelerateEasing
                )
            ) + expandHorizontally(
                animationSpec = tween(
                    PlaybackBarDefaults.TimestampEnterDurationMillis,
                    easing = PlaybackBarDefaults.EmphasizedDecelerateEasing
                ),
                expandFrom = Alignment.Start
            ),
            exit = fadeOut(
                tween(
                    PlaybackBarDefaults.TimestampExitDurationMillis,
                    easing = PlaybackBarDefaults.EmphasizedAccelerateEasing
                )
            ) + shrinkHorizontally(
                animationSpec = tween(
                    PlaybackBarDefaults.TimestampExitDurationMillis,
                    easing = PlaybackBarDefaults.EmphasizedAccelerateEasing
                ),
                shrinkTowards = Alignment.Start
            )
        ) {
            val behindSeconds = (bufferDurationSeconds - positionSeconds).coerceAtLeast(0f)
            Timestamp(text = formatTimeOffset(behindSeconds), color = colors.timestampText)
        }

        DraggableTrack(
            positionSeconds = positionSeconds,
            durationSeconds = bufferDurationSeconds,
            onSeekStart = onSeekStart,
            onSeek = onSeek,
            onSeekEnd = onSeekEnd,
            trackHeight = PlaybackBarDefaults.TrackHeightLarge,
            handleShape = handleShape,
            cuePoints = cuePoints,
            onCuePointClick = onCuePointClick,
            colors = colors,
            modifier = Modifier
                .weight(1f)
        )

        when (settledState) {
            DvrState.AtLiveEdge -> StatusBadge(
                label = "Live",
                containerColor = colors.liveBadgeContainer,
                contentColor = colors.liveBadgeContent
            )

            DvrState.TimeShifted -> StatusBadge(
                label = "Live",
                containerColor = colors.goLiveBadgeContainer,
                contentColor = colors.goLiveBadgeContent,
                onClick = onGoLive
            )
        }
    }
}

/**
 * Debounces [value]: a burst of rapid changes (e.g. [DvrState] flip-flopping across its
 * live-edge tolerance boundary on every drag frame) collapses into a single settle once [value]
 * actually stops changing for [delayMillis] — instead of the UI reacting to every intermediate
 * flip. `LaunchedEffect(value)` cancels and restarts its delay on every new value for free,
 * which is exactly debounce semantics: only the value that survives unchanged for the full
 * delay ever gets committed.
 */
@Composable
private fun <T> rememberSettled(
    value: T,
    delayMillis: Long = PlaybackBarDefaults.DvrStateSettleDelayMillis
): T {
    var settled by remember { mutableStateOf(value) }
    LaunchedEffect(value) {
        delay(delayMillis)
        settled = value
    }
    return settled
}

@Preview(
    name = "Live Radio - Light Mode",
    showBackground = true,
    widthDp = 1363,
    heightDp = 96,
    uiMode = Configuration.UI_MODE_NIGHT_NO or Configuration.UI_MODE_TYPE_CAR,
)
@Preview(
    name = "Live Radio - Dark Mode",
    showBackground = false,
    widthDp = 1363,
    heightDp = 96,
    uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_CAR,
)
@Composable
private fun LiveRadioPlaybackBarPreview() {
    AutomotiveTheme { LiveRadioPlaybackBar(modifier = Modifier.padding(16.dp)) }
}

@Preview(
    name = "On Demand - Light Mode",
    showBackground = true,
    widthDp = 1363,
    heightDp = 96,
    uiMode = Configuration.UI_MODE_NIGHT_NO or Configuration.UI_MODE_TYPE_CAR,
)
@Preview(
    name = "On Demand - Dark Mode",
    showBackground = false,
    widthDp = 1363,
    heightDp = 96,
    uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_CAR,
)
@Composable
private fun OnDemandPlaybackBarPreview() {
    AutomotiveTheme {
        OnDemandPlaybackBar(
            positionSeconds = 107f,
            durationSeconds = 214f,
            onSeek = {},
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(
    name = "DVR — At Live Edge - Light Mode",
    showBackground = true,
    widthDp = 1363,
    heightDp = 96,
    uiMode = Configuration.UI_MODE_NIGHT_NO or Configuration.UI_MODE_TYPE_CAR,
)
@Preview(
    name = "DVR — At Live Edge - Dark Mode",
    showBackground = false,
    widthDp = 1363,
    heightDp = 96,
    uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_CAR,
)
@Composable
private fun DvrAtLiveEdgePreview() {
    AutomotiveTheme {
        DvrRadioPlaybackBar(
            dvrState = DvrState.AtLiveEdge,
            positionSeconds = 7200f,
            bufferDurationSeconds = 7200f,
            cuePoints = sampleCuePoints,
            onSeek = {},
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(
    name = "DVR — Time-Shifted - Light Mode",
    showBackground = true,
    widthDp = 1363,
    heightDp = 96,
    uiMode = Configuration.UI_MODE_NIGHT_NO or Configuration.UI_MODE_TYPE_CAR,
)
@Preview(
    name = "DVR — Time-Shifted - Dark Mode",
    showBackground = false,
    widthDp = 1363,
    heightDp = 96,
    uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_CAR,
)
@Composable
private fun DvrTimeShiftedPreview() {
    AutomotiveTheme {
        DvrRadioPlaybackBar(
            dvrState = DvrState.TimeShifted,
            positionSeconds = 3600f,
            bufferDurationSeconds = 7200f,
            cuePoints = sampleCuePoints,
            onSeek = {},
            modifier = Modifier.padding(16.dp)
        )
    }
}

private val sampleCuePoints = listOf(
    CuePoint("Morning News", 0f),
    CuePoint("Traffic & Weather", 640f),
    CuePoint("Song: Midnight City", 1215f),
    CuePoint("Interview: J. Alvarez", 2400f),
    CuePoint("Song: Sundown", 3550f),
    CuePoint("Top of the Hour", 5400f),
    CuePoint("Song: Electric Skies", 6100f)
)
