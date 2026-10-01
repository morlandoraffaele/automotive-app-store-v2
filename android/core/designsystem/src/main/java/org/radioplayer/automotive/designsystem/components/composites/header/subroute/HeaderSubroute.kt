package org.radioplayer.automotive.designsystem.components.composites.header.subroute

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.radioplayer.automotive.designsystem.components.composites.containertappableicon.ContainerTappableIcon
import org.radioplayer.automotive.designsystem.components.composites.header.HeaderRow
import org.radioplayer.automotive.designsystem.components.composites.iconbutton.standard.IconButtonStandard
import org.radioplayer.automotive.designsystem.components.primitives.Text
import org.radioplayer.automotive.designsystem.theme.AutomotiveTheme

@Composable
fun HeaderSubroute(
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    contentOrder: HeaderSubrouteContentOrder = HeaderSubrouteContentOrder.TitleFirst,
    iconLeading: @Composable () -> Unit,
    onLeadingIconClick: (() -> Unit)? = null,
    trailingSlotIcon: @Composable (() -> Unit)? = null,
    trailingSlotButton: @Composable (() -> Unit)? = null,
) {
    HeaderRow(
        modifier = modifier,
        gap = AutomotiveTheme.measurement.spaces.largeIncreased,
        padding = PaddingValues(0.dp),
        leading = if (onLeadingIconClick != null) {
            {
                IconButtonStandard(
                    onClick = onLeadingIconClick,
                    icon = iconLeading,
                )
            }
        } else null,
        content = {
            Column(verticalArrangement = Arrangement.spacedBy(AutomotiveTheme.measurement.spaces.extraExtraSmall)) {
                val titleText = @Composable {
                    Text(
                        text = title,
                        style = AutomotiveTheme.typography.body1Medium,
                        color = AutomotiveTheme.colorScheme.onSurface
                    )
                }
                val subtitleText: (@Composable () -> Unit)? = subtitle?.let {
                    {
                        Text(
                            text = it,
                            style = AutomotiveTheme.typography.body3,
                            color = AutomotiveTheme.colorScheme.onSurfaceVariant,
                            maxLines = 2
                        )
                    }
                }
                when (contentOrder) {
                    HeaderSubrouteContentOrder.TitleFirst -> {
                        titleText()
                        subtitleText?.invoke()
                    }

                    HeaderSubrouteContentOrder.SubtitleFirt -> {
                        subtitleText?.invoke()
                        titleText()
                    }
                }
            }
        },
        trailing = {
            Row(horizontalArrangement = Arrangement.spacedBy(AutomotiveTheme.measurement.spaces.largeIncreased)) {
                if (trailingSlotIcon != null) {
                    ContainerTappableIcon {
                        trailingSlotIcon.invoke()
                    }
                }

                if (trailingSlotButton != null) {
                    trailingSlotButton.invoke()
                }
            }
        }
    )
}


@Preview(
    name = "HeaderSubroute - Light Mode",
    showBackground = true,
    widthDp = 1000,
    uiMode = Configuration.UI_MODE_NIGHT_NO or Configuration.UI_MODE_TYPE_CAR,
)

@Preview(
    name = "HeaderSubroute - Dark Mode",
    showBackground = false,
    widthDp = 1000,
    uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_CAR,
)
@Composable
private fun HeaderSubrouteInteractivePreview() {
    AutomotiveTheme {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {

        }
    }
}