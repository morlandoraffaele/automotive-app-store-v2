package org.radioplayer.automotive.designsystem.components.features.playbackbar

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import org.radioplayer.automotive.designsystem.components.primitives.Text
import org.radioplayer.automotive.designsystem.theme.AutomotiveTheme

/**
 * Fixed 156x76 slot ("Leading Items" / "Trailing Items" in the source design) — every
 * timestamp and status badge sits centered inside one of these, regardless of its own
 * natural content size, so the bar's left/right edges never shift as text length changes.
 */
@Composable
internal fun SideSlot(modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    Box(
        modifier = modifier
            .width(PlaybackBarDefaults.SideSlotWidth)
            .defaultMinSize(minHeight = AutomotiveTheme.measurement.sizes.minTapArea)
            .fillMaxHeight(),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .padding(horizontal = AutomotiveTheme.measurement.spaces.medium, vertical = AutomotiveTheme.measurement.spaces.small)
            ,
            content = { content() })
    }
}

@Composable
internal fun Timestamp(text: String, color: Color) {
    SideSlot {
        Text(
            text = text,
            color = color,
            style = AutomotiveTheme.typography.body3Medium,
        )
    }
}
