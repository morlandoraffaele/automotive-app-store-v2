package org.radioplayer.automotive.designsystem.components.composites.miniplayer

import android.content.res.Configuration
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.radioplayer.automotive.designsystem.components.primitives.Text
import org.radioplayer.automotive.designsystem.components.primitives.icon.Icon
import org.radioplayer.automotive.designsystem.components.primitives.icon.IconSetEnum
import org.radioplayer.automotive.designsystem.R
import org.radioplayer.automotive.designsystem.interaction.InteractionState
import org.radioplayer.automotive.designsystem.interaction.rememberFocusRingStroke
import org.radioplayer.automotive.designsystem.interaction.rememberInteractionState
import org.radioplayer.automotive.designsystem.theme.AutomotiveTheme
import org.radioplayer.automotive.designsystem.utils.customShadow
import org.radioplayer.radio.image.components.RadioImage
import org.radioplayer.radio.image.models.ImageFormat
import org.radioplayer.radio.image.models.ImageRequest
import org.radioplayer.radio.image.models.ImageSource

@Composable
private fun resolveBorderTreatment(
    interactionState: InteractionState,
    default: BorderStroke,
): BorderStroke {
    return if (interactionState.isFocused) {
        rememberFocusRingStroke()
    } else {
        default
    }
}

@Composable
private fun RowScope.MiniplayerContent(
    modifier: Modifier = Modifier,
    title: String,
    subtitle: String? = null
) {
    Column(
        modifier = modifier.weight(1f),
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = title,
            style = AutomotiveTheme.typography.body2Medium,
            maxLines = 1,
        )

        if (subtitle != null) {
            Text(
                text = subtitle,
                style = AutomotiveTheme.typography.body3,
                maxLines = 1,
            )
        }
    }
}

@Composable
private fun MiniplayerArtwork(
    modifier: Modifier = Modifier,
    imageRequest: ImageRequest
) {
    Box(
        modifier = modifier
            .width(MiniplayerDefaults.containerWidthArtwork)
            .aspectRatio(1f)
            .clip(RoundedCornerShape(AutomotiveTheme.measurement.shapes.small))
    ) {
        RadioImage(
            modifier = Modifier.fillMaxSize(),
            request = imageRequest
        )
    }
}

@Composable
private fun MiniplayerAction(
    actionState: MiniplayerActionState,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .defaultMinSize(
                minWidth = AutomotiveTheme.measurement.sizes.minTapArea,
                minHeight = AutomotiveTheme.measurement.sizes.minTapArea
            ),
        contentAlignment = Alignment.Center
    ) {
        when (actionState) {
            is MiniplayerActionState.Play -> Icon(
                name = IconSetEnum.PlayArrow,
                size = AutomotiveTheme.icon.primary
            )

            is MiniplayerActionState.Pause -> Icon(
                name = IconSetEnum.Pause,
                size = AutomotiveTheme.icon.primary,
                color = AutomotiveTheme.colorScheme.primary
            )

            is MiniplayerActionState.Stop -> Icon(
                name = IconSetEnum.Stop,
                size = AutomotiveTheme.icon.primary,
                color = AutomotiveTheme.colorScheme.primary
            )
        }
    }
}

/**
 * A compact, persistent player control surface that displays the currently playing item's
 * artwork and metadata alongside a primary playback action (play/pause/stop).
 *
 * The component is a clickable [Surface] with an elevated, layered drop shadow, a leading
 * square artwork thumbnail ([MiniplayerArtwork]), a title/subtitle text block
 * ([MiniplayerContent]) that takes up any remaining horizontal space, and a trailing
 * playback action icon ([MiniplayerAction]) sized to meet minimum touch target requirements.
 *
 * When focused (e.g. via a D-pad or rotary controller in automotive contexts), the Surface
 * renders a focus ring border in place of its default border.
 *
 * @param modifier [Modifier] to be applied to the miniplayer's outer [Surface]. Note that a
 * fixed width ([MiniplayerDefaults.containerWidth]) is always applied after this modifier.
 * @param onClick Callback invoked when the miniplayer is clicked or activated, typically used
 * to navigate to a full player screen.
 * @param metadata The [MiniplayerMetadata] (title and optional subtitle) describing the
 * currently playing item.
 * @param imageRequest The [ImageRequest] describing the artwork to load and display for the
 * currently playing item.
 * @param actionState The current [MiniplayerActionState] (e.g. Play, Pause, Stop) that
 * determines which playback icon is rendered.
 * @param colors The [MiniplayerColors] used for the container and content colors of the
 * Surface. Defaults to the current [AutomotiveTheme] surface container and on-surface-variant
 * colors.
 * @param shape The [Shape] used to clip the Surface and to draw its outline shadow. Defaults
 * to [MiniplayerDefaults.shapes.default].
 * @param interactionSource Optional [MutableInteractionSource] used to observe and emit
 * interaction events (e.g. press, focus) for this miniplayer. If `null`, one is created and
 * remembered internally.
 */

@Composable
fun Miniplayer(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    metadata: MiniplayerMetadata,
    imageRequest: ImageRequest,
    actionState: MiniplayerActionState,
    colors: MiniplayerColors = MiniplayerColors(
        containerColor = AutomotiveTheme.colorScheme.surfaceContainerHighest,
        contentColor = AutomotiveTheme.colorScheme.onSurfaceVariant
    ),
    shape: Shape = MiniplayerDefaults.shapes.default(),
    border: BorderStroke = MiniplayerDefaults.border(),
    interactionSource: MutableInteractionSource? = null,
) {
    val actualInteractionSource = interactionSource ?: remember { MutableInteractionSource() }
    val interactionState by rememberInteractionState(actualInteractionSource)

    val resolvedBorder = resolveBorderTreatment(interactionState, border)

    Surface(
        onClick = onClick,
        modifier = modifier
            .customShadow(
                color = Color(0x1F000000),   // #0000001F
                offsetY = 1.dp,
                blur = 8.dp,
                shape = shape
            )
            .customShadow(
                color = Color(0x24000000),   // #00000024
                offsetY = 3.dp,
                blur = 4.dp,
                shape = shape
            )
            .customShadow(
                color = Color(0x33000000),   // #00000033
                offsetY = 3.dp,
                blur = 3.dp,
                spread = (-2).dp,
                shape = shape
            )
            .width(MiniplayerDefaults.containerWidth),
        shape = shape,
        color = colors.containerColor,
        contentColor = colors.contentColor,
        border = resolvedBorder,
        interactionSource = actualInteractionSource
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(AutomotiveTheme.measurement.spaces.extraSmall),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(AutomotiveTheme.measurement.spaces.medium)
        ) {
            MiniplayerArtwork(
                imageRequest = imageRequest
            )
            MiniplayerContent(
                title = metadata.title,
                subtitle = metadata.subtitle
            )
            MiniplayerAction(actionState = actionState)
        }
    }
}

@Preview(
    name = "Miniplayer - Light Mode",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_NO or Configuration.UI_MODE_TYPE_CAR,
)

@Preview(
    name = "Miniplayer - Dark Mode",
    showBackground = false,
    uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_CAR,
)

@Composable
fun MiniplayerPreview() {
    AutomotiveTheme{
        Column(
            modifier = Modifier.padding(AutomotiveTheme.measurement.spaces.medium),
            verticalArrangement = Arrangement.spacedBy(AutomotiveTheme.measurement.spaces.medium)
        ) {
            Miniplayer(
                metadata = MiniplayerMetadata(
                    title = "The Daily News",
                    subtitle = "Episode 42 • Paused"
                ),
                imageRequest = ImageRequest(
                    source = ImageSource.Resource(R.drawable.placeholder_image),
                    format = ImageFormat.Static
                ),
                actionState = MiniplayerActionState.Play,
                onClick = {},
            )

            Miniplayer(
                metadata = MiniplayerMetadata(
                    title = "Tech Talk Weekly",
                    subtitle = "Episode 108 • 12:45"
                ),
                imageRequest = ImageRequest(
                    source = ImageSource.Resource(R.drawable.placeholder_image),
                    format = ImageFormat.Static
                ),
                actionState = MiniplayerActionState.Pause,
                onClick = {},
            )

            Miniplayer(
                metadata = MiniplayerMetadata(
                    title = "Radio One Live",
                    subtitle = "Live • Hits Station"
                ),
                imageRequest = ImageRequest(
                    source = ImageSource.Resource(R.drawable.placeholder_image),
                    format = ImageFormat.Static
                ),
                actionState = MiniplayerActionState.Stop,
                onClick = {},
            )
        }
    }
}