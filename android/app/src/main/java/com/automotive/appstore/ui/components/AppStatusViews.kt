package com.automotive.appstore.ui.components

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.automotive.appstore.data.AppStatus
import com.automotive.appstore.ui.theme.StoreType
import com.automotive.appstore.ui.theme.storeColors
import androidx.compose.ui.text.font.FontWeight
import org.radioplayer.automotive.designsystem.components.primitives.Text
import org.radioplayer.automotive.designsystem.components.primitives.icon.Icon
import org.radioplayer.automotive.designsystem.components.primitives.icon.IconSource
import org.radioplayer.automotive.designsystem.theme.AutomotiveTheme

/** Semantic tone of a status chip or badge, matching `Tone` in the web `app-status.tsx`. */
enum class StatusTone { PRIMARY, SUCCESS, DESTRUCTIVE, MUTED }

/** Maps an [AppStatus] to its tone, mirroring `STATUS_VISUALS` on the web. */
fun AppStatus.tone(): StatusTone = when (this) {
    AppStatus.INSTALLED, AppStatus.UP_TO_DATE -> StatusTone.SUCCESS
    AppStatus.UPDATE_AVAILABLE, AppStatus.DOWNLOADING, AppStatus.INSTALLING -> StatusTone.PRIMARY
    AppStatus.FAILED -> StatusTone.DESTRUCTIVE
    AppStatus.NOT_INSTALLED -> StatusTone.MUTED
}

/** The glyph a status shows, or `null` when the status is drawn as a progress ring. */
private fun AppStatus.glyph() = when (this) {
    AppStatus.INSTALLED, AppStatus.UP_TO_DATE -> StoreIcons.Check
    AppStatus.UPDATE_AVAILABLE -> StoreIcons.Update
    AppStatus.INSTALLING -> StoreIcons.Installing
    AppStatus.FAILED -> StoreIcons.Failed
    AppStatus.DOWNLOADING, AppStatus.NOT_INSTALLED -> null
}

@Composable
private fun StatusTone.containerColor(): Color = when (this) {
    StatusTone.PRIMARY -> storeColors.primary
    StatusTone.SUCCESS -> storeColors.success
    StatusTone.DESTRUCTIVE -> storeColors.destructive
    StatusTone.MUTED -> storeColors.muted
}

@Composable
private fun StatusTone.onContainerColor(): Color = when (this) {
    StatusTone.PRIMARY -> storeColors.primaryForeground
    StatusTone.SUCCESS -> storeColors.successForeground
    StatusTone.DESTRUCTIVE -> storeColors.destructiveForeground
    StatusTone.MUTED -> storeColors.mutedForeground
}

@Composable
private fun StatusTone.softContainerColor(): Color = when (this) {
    StatusTone.PRIMARY -> storeColors.primary.copy(alpha = 0.15f)
    StatusTone.SUCCESS -> storeColors.success.copy(alpha = 0.15f)
    StatusTone.DESTRUCTIVE -> storeColors.destructive.copy(alpha = 0.15f)
    StatusTone.MUTED -> storeColors.muted
}

@Composable
private fun StatusTone.softContentColor(): Color = when (this) {
    StatusTone.PRIMARY -> storeColors.primary
    StatusTone.SUCCESS -> storeColors.success
    StatusTone.DESTRUCTIVE -> storeColors.destructive
    StatusTone.MUTED -> storeColors.mutedForeground
}

/** A 0..1 value that breathes between [min] and 1f, mirroring the web `animate-pulse`. */
@Composable
private fun rememberPulseAlpha(min: Float = 0.4f): Float {
    val transition = rememberInfiniteTransition(label = "pulse")
    val value by transition.animateFloat(
        initialValue = min,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(700), RepeatMode.Reverse),
        label = "pulseAlpha",
    )
    return value
}

/** A continuously rotating angle, for the spinning `installing` glyph. */
@Composable
private fun rememberSpinAngle(): Float {
    val transition = rememberInfiniteTransition(label = "spin")
    val value by transition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(tween(900), RepeatMode.Restart),
        label = "spinAngle",
    )
    return value
}

/**
 * Circular progress ring. Port of the web `ProgressRing`.
 *
 * While [status] is [AppStatus.INSTALLING] the web app pulses the whole indicator, so here
 * the arc is drawn at full sweep with a pulsing alpha instead of animating the angle.
 */
@Composable
fun ProgressRing(
    progress: Int,
    modifier: Modifier = Modifier,
    color: Color = storeColors.primary,
    status: AppStatus = AppStatus.DOWNLOADING,
    strokeWidth: Dp = 4.dp,
) {
    val target = (progress.coerceIn(0, 100) / 100f)
    val animated by animateFloatAsState(targetValue = target, label = "ringProgress")
    val installing = status == AppStatus.INSTALLING
    val alpha = if (installing) rememberPulseAlpha() else 1f

    Canvas(modifier = modifier) {
        val stroke = strokeWidth.toPx()
        val inset = stroke / 2f
        val arcSize = androidx.compose.ui.geometry.Size(
            width = size.width - stroke,
            height = size.height - stroke,
        )
        val topLeft = androidx.compose.ui.geometry.Offset(inset, inset)

        drawArc(
            color = color.copy(alpha = 0.3f),
            startAngle = 0f,
            sweepAngle = 360f,
            useCenter = false,
            topLeft = topLeft,
            size = arcSize,
            style = Stroke(width = stroke),
        )
        drawArc(
            color = color.copy(alpha = alpha),
            startAngle = -90f,
            sweepAngle = 360f * if (installing) 1f else animated,
            useCenter = false,
            topLeft = topLeft,
            size = arcSize,
            style = Stroke(width = stroke, cap = StrokeCap.Round),
        )
    }
}

/**
 * The status glyph, or a progress ring while downloading.
 *
 * [tint] is applied explicitly because the design system's `Icon` resolves an unspecified
 * colour to `LocalContentColor`, which is not the tone these badges need.
 */
@Composable
private fun StatusGlyph(
    status: AppStatus,
    progress: Int,
    tint: Color,
    modifier: Modifier,
    glyphSize: Dp,
) {
    if (status == AppStatus.DOWNLOADING) {
        ProgressRing(
            progress = progress,
            modifier = modifier,
            color = tint,
            strokeWidth = 3.dp,
        )
        return
    }
    val vector = status.glyph() ?: return
    if (status != AppStatus.INSTALLING) {
        Icon(
            source = IconSource.Vector(vector),
            size = glyphSize,
            color = tint,
            contentDescription = null,
        )
        return
    }
    // The design system's Icon has no `modifier` parameter, so the spinner is a Box wrapper.
    val spin = rememberSpinAngle()
    Box(modifier = modifier.rotate(spin), contentAlignment = Alignment.Center) {
        Icon(
            source = IconSource.Vector(vector),
            size = glyphSize,
            color = tint,
            contentDescription = null,
        )
    }
}

/**
 * Compact circular badge that sits on the corner of an app icon. Port of the web `StatusBadge`.
 */
@Composable
fun StatusBadge(
    status: AppStatus,
    progress: Int,
    modifier: Modifier = Modifier,
) {
    if (status == AppStatus.NOT_INSTALLED) return
    val tone = status.tone()
    Box(
        modifier = modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(tone.containerColor())
            .border(4.dp, storeColors.card, CircleShape)
            .clearAndSetSemantics { },
        contentAlignment = Alignment.Center,
    ) {
        StatusGlyph(status, progress, tone.onContainerColor(), Modifier.size(24.dp), 22.dp)
    }
}

/** Labeled pill used in list rows and the detail header. Port of the web `StatusChip`. */
@Composable
fun StatusChip(
    status: AppStatus,
    label: String,
    modifier: Modifier = Modifier,
    progress: Int = 0,
) {
    if (status == AppStatus.NOT_INSTALLED) return
    val tone = status.tone()
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(50))
            .background(tone.softContainerColor())
            .padding(horizontal = 16.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        StatusGlyph(status, progress, tone.softContentColor(), Modifier.size(20.dp), 20.dp)
        Text(
            text = label,
            // Web `text-base font-semibold tabular-nums` = 18px; `sub2Medium` is 20sp.
            style = StoreType.base.copy(fontWeight = FontWeight.SemiBold),
            color = tone.softContentColor(),
            maxLines = 1,
        )
    }
}

/** Linear progress bar for rows and the detail header. Port of the web `StatusProgressBar`. */
@Composable
fun StatusProgressBar(status: AppStatus, progress: Int, modifier: Modifier = Modifier) {
    if (status != AppStatus.DOWNLOADING && status != AppStatus.INSTALLING) return
    val installing = status == AppStatus.INSTALLING
    val fraction by animateFloatAsState(
        targetValue = if (installing) 1f else progress.coerceIn(0, 100) / 100f,
        label = "barProgress",
    )
    val alpha = if (installing) rememberPulseAlpha() else 1f

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(8.dp)
            .clip(RoundedCornerShape(50))
            .background(storeColors.muted),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(fraction)
                .height(8.dp)
                .clip(RoundedCornerShape(50))
                .background(storeColors.primary.copy(alpha = alpha)),
        )
    }
}
