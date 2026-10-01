package org.radioplayer.automotive.designsystem.components.composites.containertappableicon

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import org.radioplayer.automotive.designsystem.components.primitives.icon.Icon
import org.radioplayer.automotive.designsystem.theme.AutomotiveTheme

@Composable
fun ContainerTappableIcon(
    modifier: Modifier = Modifier,
    icon: @Composable () -> Unit,
) {
    Box(
        modifier = modifier
            .defaultMinSize(
                minWidth = AutomotiveTheme.measurement.sizes.minTapArea,
                minHeight = AutomotiveTheme.measurement.sizes.minTapArea
            )
            .widthIn(max = AutomotiveTheme.measurement.sizes.maxInteractionWidth)
            .padding(AutomotiveTheme.measurement.spaces.medium)
            .clip(RoundedCornerShape(AutomotiveTheme.measurement.shapes.full)),
        contentAlignment = Alignment.Center
    ) {
        icon.invoke()
    }
}

@Preview(
    name = "ContainerTappableIcon - Light Mode",
    showBackground = true,
    widthDp = 1000,
    uiMode = Configuration.UI_MODE_NIGHT_NO or Configuration.UI_MODE_TYPE_CAR,
)

@Preview(
    name = "ContainerTappableIcon - Dark Mode",
    showBackground = false,
    widthDp = 1000,
    uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_CAR,
)

@Composable
private fun ContainerTappableIconPreview() {
    AutomotiveTheme() {
        Column() {
            ContainerTappableIcon {
                Icon()
            }
        }
    }
}

