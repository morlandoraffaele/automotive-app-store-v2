package org.radioplayer.automotive.designsystem.components.composites.toast

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import org.radioplayer.automotive.designsystem.components.primitives.Text
import org.radioplayer.automotive.designsystem.theme.AutomotiveTheme
import org.radioplayer.automotive.designsystem.utils.customShadow

/**
 * A short, informational message shown briefly near the bottom of the screen to confirm an
 * action the app has already taken, without requiring user interaction
 * (docs.partner.android.com/drivingux Toast spec). Purely presentational — callers own
 * placement (near-bottom, in front of other content), auto-dismiss timing
 * ([ToastDefaults.DurationMillis]) and the single-toast-at-a-time rule.
 *
 * @param message the toast's text content. Figma: `maxLines=2`, truncates with an ellipsis.
 */
@Composable
fun Toast(
    message: String,
    modifier: Modifier = Modifier,
    colors: ToastColors = ToastDefaults.colors(),
) {
    val shape = RoundedCornerShape(ToastDefaults.CornerRadius)

    Text(
        text = message,
        modifier = modifier
            .widthIn(max = ToastDefaults.Width)
            .customShadow(color = colors.shadow, shape = shape)
            .clip(shape)
            .background(colors.container)
            .padding(
                horizontal = ToastDefaults.ContainerPaddingHorizontal,
                vertical = ToastDefaults.ContainerPaddingVertical,
            ),
        color = colors.message,
        style = ToastDefaults.MessageStyle,
        maxLines = 2,
    )
}

@Preview(
    name = "Toast - Light Mode",
    widthDp = 800,
    heightDp = 200,
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_NO or Configuration.UI_MODE_TYPE_CAR,
)
@Preview(
    name = "Toast - Dark Mode",
    widthDp = 800,
    heightDp = 200,
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_CAR,
)
@Composable
private fun ToastPreview() {
    AutomotiveTheme {
        Column(
            modifier = Modifier.padding(AutomotiveTheme.measurement.spaces.large),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Toast(
                message = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod " +
                    "tempor incididunt ut labore et dolore magna aliqua.",
            )
        }
    }
}
