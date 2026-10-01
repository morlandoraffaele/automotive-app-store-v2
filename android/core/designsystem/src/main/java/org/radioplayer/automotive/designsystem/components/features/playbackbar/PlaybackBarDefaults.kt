package org.radioplayer.automotive.designsystem.components.features.playbackbar

import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt
import org.radioplayer.automotive.designsystem.theme.AutomotiveTheme

object PlaybackBarDefaults {
    val BarHeight: Dp
        @Composable get() = AutomotiveTheme.measurement.sizes.minTapArea

    val SideSlotWidth: Dp
        @Composable get() = AutomotiveTheme.measurement.sizes.minInteractionWidth
    val TrackHeightDefault: Dp
        @Composable get() = AutomotiveTheme.measurement.shapes.small
    val TrackHeightLarge: Dp
        @Composable get() = AutomotiveTheme.measurement.shapes.large
    val HandleWidth: Dp
        @Composable get() = AutomotiveTheme.measurement.shapes.small

    val HandleHeightInteractive: Dp = 38.dp

    val HandleTouchTargetHeight: Dp
        @Composable get() = AutomotiveTheme.measurement.sizes.minTapArea
    val HandleSizeStatic: Dp
        @Composable get() = AutomotiveTheme.measurement.shapes.small
    val TrackGap: Dp
        @Composable get() = AutomotiveTheme.measurement.shapes.small
    val CuePointSize: Dp
        @Composable get() = AutomotiveTheme.measurement.shapes.small

    const val CuePointSnapTolerance: Float = 0.02f

    const val DvrStateSettleDelayMillis: Long = 220L

    val EmphasizedDecelerateEasing = CubicBezierEasing(0.05f, 0.7f, 0.1f, 1f)
    val EmphasizedAccelerateEasing = CubicBezierEasing(0.3f, 0f, 0.8f, 0.15f)
    const val TimestampEnterDurationMillis: Int = 350
    const val TimestampExitDurationMillis: Int = 200

    @Composable
    fun colors(
        activeTrack: Color = AutomotiveTheme.colorScheme.primary,
        inactiveTrack: Color = AutomotiveTheme.colorScheme.surfaceContainer,
        handle: Color = AutomotiveTheme.colorScheme.inverseSurface,
        handleFocusRing: Color = AutomotiveTheme.colorScheme.primary.copy(alpha = 0.12f),
        cuePointOnActiveTrack: Color = AutomotiveTheme.colorScheme.onPrimary,
        cuePointOnInactiveTrack: Color = AutomotiveTheme.colorScheme.onSurface,
        timestampText: Color = AutomotiveTheme.colorScheme.onSurface,
        liveBadgeContainer: Color = AutomotiveTheme.colorScheme.redFixed,
        liveBadgeContent: Color = AutomotiveTheme.colorScheme.onRedFixed,
        goLiveBadgeContainer: Color = AutomotiveTheme.colorScheme.secondaryContainer,
        goLiveBadgeContent: Color = AutomotiveTheme.colorScheme.onSecondaryContainer
    ): PlaybackBarColors = PlaybackBarColors(
        activeTrack, inactiveTrack, handle, handleFocusRing,
        cuePointOnActiveTrack, cuePointOnInactiveTrack, timestampText,
        liveBadgeContainer, liveBadgeContent, goLiveBadgeContainer, goLiveBadgeContent
    )

    /** "H:MM:SS" once past an hour, "MM:SS" otherwise. Override per-call for other locales. */
    fun formatElapsed(totalSeconds: Float): String {
        val s = totalSeconds.roundToInt().coerceAtLeast(0)
        val h = s / 3600; val m = (s % 3600) / 60; val sec = s % 60
        return if (h > 0) "%d:%02d:%02d".format(h, m, sec) else "%02d:%02d".format(m, sec)
    }

    /** "-H:MM:SS" / "-MM:SS" — how far behind live, for DVR's Time-Shifted timestamp. */
    fun formatOffset(behindSeconds: Float): String = "-" + formatElapsed(behindSeconds)
}
