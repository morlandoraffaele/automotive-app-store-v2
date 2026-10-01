package org.radioplayer.automotive.designsystem.components.features.continueinapp

import android.content.res.Configuration
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import org.radioplayer.automotive.designsystem.components.primitives.Text
import org.radioplayer.automotive.designsystem.interaction.InteractionState
import org.radioplayer.automotive.designsystem.interaction.rememberFocusRingStroke
import org.radioplayer.automotive.designsystem.interaction.rememberInteractionState
import org.radioplayer.automotive.designsystem.theme.AutomotiveTheme
import org.radioplayer.radio.image.components.RadioImage
import org.radioplayer.radio.image.models.ImageFormat
import org.radioplayer.radio.image.models.ImageRequest
import org.radioplayer.radio.image.models.ImageSize
import org.radioplayer.radio.image.models.ImageSource

@Composable
private fun resolveBorderTreatment(
    interactionState: InteractionState,
): BorderStroke? {
    return if (interactionState.isFocused) {
        rememberFocusRingStroke()
    } else {
        null
    }
}

private fun resolveAlpha(enabled: Boolean) =
    if (enabled) 1F else 0.38F // TODO: Alpha value should come from AutomotiveTheme

@Composable
fun ContinueInApp(
    onClick: () -> Unit,
    image: @Composable (Modifier) -> Unit,
    modifier: Modifier = Modifier,
    label: String? = null,
    enabled: Boolean = true,
    shape: Shape = RoundedCornerShape(AutomotiveTheme.measurement.shapes.largeIncreased),
    colors: ContinueInAppColors = ContinueInAppDefaults.colors(),
    interactionSource: MutableInteractionSource? = null,
) {
    val actualInteractionSource = interactionSource ?: remember { MutableInteractionSource() }
    val interactionState by rememberInteractionState(actualInteractionSource)

    val resolvedBorder = resolveBorderTreatment(interactionState)

    val alpha = resolveAlpha(enabled)

    Surface(
        modifier = modifier
            .alpha(alpha)
            .defaultMinSize(
                minWidth = AutomotiveTheme.measurement.sizes.minInteractionWidth,
                minHeight = AutomotiveTheme.measurement.sizes.minTapArea
            )
            .semantics(mergeDescendants = true) {
                this.role = Role.Button
            },
        onClick = onClick,
        enabled = enabled,
        shape = shape,
        color = colors.containerColor,
        contentColor = colors.contentColor,
        border = resolvedBorder,
        interactionSource = actualInteractionSource
    ) {
        Row(
            modifier = Modifier
                .padding(
                    start = AutomotiveTheme.measurement.spaces.extraSmall,
                    top = AutomotiveTheme.measurement.spaces.extraSmall,
                    end = AutomotiveTheme.measurement.spaces.large,
                    bottom = AutomotiveTheme.measurement.spaces.extraSmall
                ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(AutomotiveTheme.measurement.spaces.medium)
        ) {
            Box(
                modifier = Modifier
                    .width(ContinueInAppDefaults.containerImageWidth)
                    .aspectRatio(1f)
                    .clip(RoundedCornerShape(AutomotiveTheme.measurement.shapes.medium))
                    .clearAndSetSemantics {}
            ) {
                image(Modifier.fillMaxSize())
            }

            if (label != null) {
                Text(
                    text = label,
                    style = AutomotiveTheme.typography.body3Medium,
                    maxLines = 1,
                )
            }
        }
    }
}

@Preview(
    name = "ContinueInApp - Light Mode",
    showBackground = true,
    widthDp = 1000,
    uiMode = Configuration.UI_MODE_NIGHT_NO or Configuration.UI_MODE_TYPE_CAR,
)

@Preview(
    name = "ContinueInApp - Dark Mode",
    showBackground = false,
    widthDp = 1000,
    uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_CAR,
)

@Composable
private fun ContinueInAppPreview() {
    AutomotiveTheme() {
        Column(
            modifier = Modifier.padding(AutomotiveTheme.measurement.spaces.large),
            verticalArrangement = Arrangement.spacedBy(AutomotiveTheme.measurement.spaces.large)
        ) {
            ContinueInApp(
                onClick = {},
                image = { imageModifier ->
                    PreviewAppIconPlaceholder()
                },
                label = "Label",
                enabled = true
            )

            ContinueInApp(
                onClick = {},
                image = { imageModifier ->
                    PreviewAppIconPlaceholder()
                },
                label = "Label",
                enabled = false
            )
        }
    }
}

@Composable
private fun PreviewAppIconPlaceholder() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black),
    )
}
