package org.radioplayer.automotive.designsystem.components.primitives.flag

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import org.radioplayer.automotive.designsystem.theme.AutomotiveTheme

/**
 * Country flag. Unlike [org.radioplayer.automotive.designsystem.components.primitives.icon.Icon]
 * it is never tinted, and [size] selects a dedicated asset instead of scaling one.
 * Rounded corners are part of the assets.
 *
 * @param contentDescription null by default: a flag usually sits next to the country name.
 */
@Composable
fun Flag(
    name: FlagSetEnum,
    modifier: Modifier = Modifier,
    size: FlagSize = FlagSize.Default,
    contentDescription: String? = null,
) {
    Image(
        painter = painterResource(name.resId(size)),
        contentDescription = contentDescription,
        modifier = modifier.size(size.width, size.height),
    )
}


@Preview(
    name = "Flag - Dark Mode",
    showBackground = false,
    uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_CAR
)
@Preview(
    name = "Flag - Light Mode",
    showBackground = false,
    uiMode = Configuration.UI_MODE_NIGHT_NO or Configuration.UI_MODE_TYPE_CAR
)
@Composable
fun FlagPreview() {
    AutomotiveTheme {
        Row(
            horizontalArrangement = Arrangement.spacedBy(AutomotiveTheme.measurement.spaces.medium),
            verticalAlignment = Alignment.CenterVertically
        ) {
            FlagSize.entries.forEach { size ->
                Flag(name = FlagSetEnum.Italy, size = size)
            }
            Flag(name = FlagSetEnum.fromCountryCode("XX"))
        }
    }
}
