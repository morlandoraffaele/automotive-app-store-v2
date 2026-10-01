package org.radioplayer.automotive.designsystem.components.composites.header.section

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import org.radioplayer.automotive.designsystem.components.composites.divider.Divider
import org.radioplayer.automotive.designsystem.components.primitives.Text
import org.radioplayer.automotive.designsystem.theme.AutomotiveTheme

@Composable
fun HeaderSection(
    label: String,
    modifier: Modifier = Modifier,
    style: HeaderSectionStyle = HeaderSectionStyle.LabeledDivider,
    colors: HeaderSectionColors = HeaderSectionDefaults.colors(style),
) {

    Row(
        modifier = modifier
            .clip(RoundedCornerShape(AutomotiveTheme.measurement.shapes.large))
            .background(colors.containerColor)
            .fillMaxWidth()
            .padding(
                horizontal = AutomotiveTheme.measurement.spaces.large,
                vertical = AutomotiveTheme.measurement.spaces.medium,
            ),
        horizontalArrangement = Arrangement.spacedBy(AutomotiveTheme.measurement.spaces.largeIncreased),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = label,
            style = AutomotiveTheme.typography.body1Medium,
            color = colors.contentColor
        )
        Divider()
    }
}

@Preview(
    name = "HeaderSection - Light Mode",
    showBackground = true,
    widthDp = 1000,
    uiMode = Configuration.UI_MODE_NIGHT_NO or Configuration.UI_MODE_TYPE_CAR,
)

@Preview(
    name = "HeaderSection - Dark Mode",
    showBackground = false,
    widthDp = 1000,
    uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_CAR,
)

@Composable
private fun HeaderSectionPreview() {
    AutomotiveTheme() {
        Column(
            modifier = Modifier.padding(AutomotiveTheme.measurement.spaces.large),
            verticalArrangement = Arrangement.spacedBy(AutomotiveTheme.measurement.spaces.large)
        ) {
            HeaderSection(
                label = "Section Name"
            )

            HeaderSection(
                label = "Section Name",
                style = HeaderSectionStyle.FilledBar
            )
        }
    }
}