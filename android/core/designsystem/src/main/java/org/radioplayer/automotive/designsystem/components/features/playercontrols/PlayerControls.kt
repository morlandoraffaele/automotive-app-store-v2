package org.radioplayer.automotive.designsystem.components.features.playercontrols

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import org.radioplayer.automotive.designsystem.components.composites.iconbutton.standard.IconButtonStandard
import org.radioplayer.automotive.designsystem.components.primitives.icon.Icon
import org.radioplayer.automotive.designsystem.components.primitives.icon.IconSetEnum
import org.radioplayer.automotive.designsystem.theme.AutomotiveTheme

/**
 * A bordered, tappable player control used in player surfaces (e.g. now-playing bars,
 * mini players, full-screen player controls) for actions such as skip forward/back,
 * replay, or other transport controls.
 *
 * The control renders a caller-supplied [icon] inside a rounded, bordered container.
 * When [artwork] is provided and [compact] is `false`, the artwork is shown alongside
 * the icon, positioned either before or after it based on [iconPosition]. In compact
 * mode the control collapses to a square and the artwork is hidden.
 *
 * @param onClick callback invoked when the control is activated.
 * @param icon caller-supplied composable that renders the control's icon.
 * @param modifier [Modifier] applied to the outer container.
 * @param enabled whether the control responds to user input. Also affects visual alpha.
 *   Defaults to [PlayerControlsDefaults.enabled].
 * @param compact whether to render the control in its compact (square) form, hiding any
 *   [artwork]. Defaults to [PlayerControlsDefaults.compact].
 * @param surface the visual surface variant — [PlayerControlsSurface.Standard] for opaque
 *   backgrounds, [PlayerControlsSurface.OnMedia] for translucent containers over imagery.
 *   Defaults to [PlayerControlsDefaults.surface].
 * @param iconPosition whether the [artwork] sits before ([PlayerControlsIconPosition.Leading])
 *   or after ([PlayerControlsIconPosition.Trailing]) the [icon]. Defaults to
 *   [PlayerControlsDefaults.iconPosition].
 * @param artwork optional composable rendered alongside the icon. Ignored when [compact]
 *   is `true`.
 * @param interactionSource the [MutableInteractionSource] used to observe and emit
 *   interactions for this control. If `null`, one is created and remembered internally.
 * @param contentDescription accessibility label for the control, announced by screen
 *   readers (e.g. "Previous segment", "Skip forward"). If `null`, no semantic label is
 *   applied.
 */
@Composable
fun PlayerControls(
    onClick: () -> Unit,
    icon: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = PlayerControlsDefaults.enabled,
    compact: Boolean = PlayerControlsDefaults.compact,
    surface: PlayerControlsSurface = PlayerControlsDefaults.surface,
    iconPosition: PlayerControlsIconPosition = PlayerControlsDefaults.iconPosition,
    artwork: (@Composable (Modifier) -> Unit)? = null,
    interactionSource: MutableInteractionSource? = null,
    contentDescription: String? = null,
) {
    val resolvedInteractionSource = interactionSource ?: remember { MutableInteractionSource() }
    val (width, height) = PlayerControlsDefaults.size(compact)
    val (startPadding, endPadding) = PlayerControlsDefaults.horizontalContentPadding(
        compact,
        iconPosition
    )
    val showArtwork = artwork != null && !compact

    IconButtonStandard(
        modifier = modifier
            .width(width)
            .height(height)
            .border(PlayerControlsDefaults.border(), shape = PlayerControlsDefaults.shape())
            .let { base ->
                if (contentDescription != null) {
                    base.semantics { this.contentDescription = contentDescription }
                } else {
                    base
                }
            },
        onClick = onClick,
        enabled = enabled,
        colors = PlayerControlsDefaults.colors(surface),
        interactionSource = resolvedInteractionSource,
        shape = PlayerControlsDefaults.shape(),
        icon = {
            Row(
                modifier = Modifier.padding(
                    start = startPadding,
                    top = AutomotiveTheme.measurement.spaces.medium,
                    end = endPadding,
                    bottom = AutomotiveTheme.measurement.spaces.medium,
                ),
                horizontalArrangement = Arrangement.spacedBy(AutomotiveTheme.measurement.spaces.medium),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                val iconSlot = @Composable { icon() }
                val artworkSlot = @Composable {
                    artwork?.invoke(
                        Modifier
                            .size(AutomotiveTheme.icon.hero)
                            .clip(RoundedCornerShape(AutomotiveTheme.measurement.shapes.medium))
                    )
                }
                if (showArtwork) {
                    when (iconPosition) {
                        PlayerControlsIconPosition.Leading -> {
                            iconSlot()
                            artworkSlot()
                        }

                        PlayerControlsIconPosition.Trailing -> {
                            artworkSlot()
                            iconSlot()
                        }
                    }
                } else {
                    iconSlot()
                }
            }
        }
    )
}

@Preview(
    name = "PlayerControls - Light Mode",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_NO or Configuration.UI_MODE_TYPE_CAR
)
@Preview(
    name = "PlayerControls - Dark Mode",
    showBackground = false,
    uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_CAR
)
@Composable
private fun PlayerControlsPreview() {
    AutomotiveTheme {
        Column(verticalArrangement = Arrangement.spacedBy(AutomotiveTheme.measurement.spaces.large)) {
            PlayerControls(
                onClick = {},
                icon = {
                    Icon(
                        name = IconSetEnum.Replay10,
                        size = AutomotiveTheme.icon.hero
                    )
                },
                contentDescription = "Previous segment",
                artwork = { artworkModifier ->
                    Box(modifier = artworkModifier.background(AutomotiveTheme.colorScheme.surfaceContainerHigh))
                }
            )
            PlayerControls(
                onClick = {},
                icon = {
                    Icon(
                        name = IconSetEnum.Forward10,
                        size = AutomotiveTheme.icon.hero
                    )
                },
                contentDescription = "Skip forward",
            )
            PlayerControls(
                onClick = {},
                icon = {
                    Icon(
                        name = IconSetEnum.Replay10,
                        size = AutomotiveTheme.icon.hero
                    )
                },
                contentDescription = "Disabled",
                enabled = false,
            )
            PlayerControls(
                onClick = {},
                icon = {
                    Icon(
                        name = IconSetEnum.Forward10,
                        size = AutomotiveTheme.icon.hero
                    )
                },
                contentDescription = "Compact",
                compact = true,
            )
        }
    }
}