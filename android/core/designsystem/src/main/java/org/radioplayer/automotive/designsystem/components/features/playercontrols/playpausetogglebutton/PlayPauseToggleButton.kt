package org.radioplayer.automotive.designsystem.components.features.playercontrols.playpausetogglebutton

import android.content.res.Configuration
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.tooling.preview.Preview
import org.radioplayer.automotive.designsystem.components.composites.iconbutton.standard.toggle.IconButtonToggleStandard
import org.radioplayer.automotive.designsystem.components.primitives.icon.Icon
import org.radioplayer.automotive.designsystem.components.primitives.icon.IconSetEnum
import org.radioplayer.automotive.designsystem.theme.AutomotiveTheme

/**
 * A toggleable play/pause control used in player surfaces (e.g. now-playing bars,
 * mini players, full-screen player controls).
 *
 * The button displays a play icon when unchecked, and either a pause or stop icon
 * when checked, depending on [content]. While [buffering] is `true`, the icon is
 * replaced with an indeterminate [CircularProgressIndicator] and the button's border
 * is suppressed, since buffering state takes visual precedence over the checked state.
 *
 * @param checked whether the button represents the "playing" state. When `false`,
 *   a play icon is shown; when `true`, a pause or stop icon is shown based on [content].
 * @param onCheckedChange callback invoked with the new checked value when the user
 *   toggles the button.
 * @param modifier [Modifier] applied to the outer container.
 * @param enabled whether the button responds to user input. Also affects icon tint.
 * @param buffering when `true`, shows a buffering spinner instead of the play/pause
 *   icon and hides the button border. Defaults to [PlayPauseToggleButtonDefaults.buffering].
 * @param shapeVariant the corner shape of the button, either fully [PlayPauseToggleButtonShape.Rounded]
 *   or [PlayPauseToggleButtonShape.Square].
 * @param content determines whether the checked state renders a pause icon
 *   ([PlayPauseToggleButtonContent.OnDemand]) or a stop icon ([PlayPauseToggleButtonContent.Live]).
 *   Defaults to [PlayPauseToggleButtonDefaults.content].
 * @param interactionSource the [MutableInteractionSource] used to observe and emit
 *   interactions for this button. If `null`, one is created and remembered internally.
 * @param contentDescription accessibility label for the button. Defaults to a value
 *   derived from [checked], [content], and [buffering] (e.g. "Play", "Pause", "Stop",
 *   or "Buffering").
 */
@Composable
fun PlayPauseToggleButton(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = PlayPauseToggleButtonDefaults.enabled,
    buffering: Boolean = PlayPauseToggleButtonDefaults.buffering,
    shapeVariant: PlayPauseToggleButtonShape = PlayPauseToggleButtonShape.Rounded,
    content: PlayPauseToggleButtonContent = PlayPauseToggleButtonDefaults.content,
    interactionSource: MutableInteractionSource? = null,
    contentDescription: String? = defaultContentDescription(checked, content, buffering),
) {
    val resolvedInteractionSource = interactionSource ?: remember { MutableInteractionSource() }
    val iconName = playPauseIcon(checked, content)

    val shape: Shape = when (shapeVariant) {
        PlayPauseToggleButtonShape.Rounded -> RoundedCornerShape(AutomotiveTheme.measurement.shapes.full)
        PlayPauseToggleButtonShape.Square -> RoundedCornerShape(AutomotiveTheme.measurement.shapes.largeIncreased)
    }
    val border = if (buffering) null else PlayPauseToggleButtonDefaults.border(checked)

    Box(
        modifier = modifier.size(PlayPauseToggleButtonDefaults.containerSize()),
        contentAlignment = Alignment.Center,
    ) {
        IconButtonToggleStandard(
            modifier = Modifier
                .matchParentSize()
                .let { if (border != null) it.border(border, shape) else it },
            selected = checked,
            onSelectedChange = onCheckedChange,
            enabled = enabled,
            colors = PlayPauseToggleButtonDefaults.colors(),
            interactionSource = resolvedInteractionSource,
            shape = shape,
            icon = {
                if (!buffering) {
                    Icon(
                        name = iconName,
                        size = AutomotiveTheme.icon.hero,
                        contentDescription = contentDescription,
                        color = PlayPauseToggleButtonDefaults.iconTint(checked = checked, enabled = enabled),
                    )
                }
            }
        )
        if (buffering) {
            CircularProgressIndicator(
                modifier = Modifier.size(PlayPauseToggleButtonDefaults.BufferingRingSize),
                color = PlayPauseToggleButtonDefaults.bufferingRingColor(checked),
                strokeWidth = AutomotiveTheme.stroke.thin,
                trackColor = AutomotiveTheme.colorScheme.secondaryContainer,
            )
        }
    }
}

private fun playPauseIcon(checked: Boolean, content: PlayPauseToggleButtonContent): IconSetEnum {
    if (!checked) return IconSetEnum.PlayArrow
    return when (content) {
        PlayPauseToggleButtonContent.OnDemand -> IconSetEnum.Pause
        PlayPauseToggleButtonContent.Live -> IconSetEnum.Stop
    }
}

private fun defaultContentDescription(
    checked: Boolean,
    content: PlayPauseToggleButtonContent,
    buffering: Boolean = false,
): String {
    if (buffering) return "Buffering"
    if (!checked) return "Play"
    return when (content) {
        PlayPauseToggleButtonContent.OnDemand -> "Pause"
        PlayPauseToggleButtonContent.Live -> "Stop"
    }
}

@Preview(
    name = "PlayPauseToggleButton - Light Mode",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_NO or Configuration.UI_MODE_TYPE_CAR
)
@Preview(
    name = "PlayPauseToggleButton - Dark Mode",
    showBackground = false,
    uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_CAR
)
@Composable
private fun PlayPauseToggleButtonPreview() {
    AutomotiveTheme {
        Column {
            PlayPauseToggleButton(checked = false, onCheckedChange = {}, shapeVariant = PlayPauseToggleButtonShape.Rounded)
            PlayPauseToggleButton(checked = true, onCheckedChange = {}, shapeVariant = PlayPauseToggleButtonShape.Rounded, content = PlayPauseToggleButtonContent.OnDemand)
            PlayPauseToggleButton(checked = true, onCheckedChange = {}, shapeVariant = PlayPauseToggleButtonShape.Rounded, content = PlayPauseToggleButtonContent.Live)
            PlayPauseToggleButton(checked = false, onCheckedChange = {}, shapeVariant = PlayPauseToggleButtonShape.Square)
            PlayPauseToggleButton(checked = true, onCheckedChange = {}, shapeVariant = PlayPauseToggleButtonShape.Square)
            PlayPauseToggleButton(checked = false, onCheckedChange = {}, enabled = false)
            PlayPauseToggleButton(checked = true, onCheckedChange = {}, enabled = false)
            PlayPauseToggleButton(checked = false, onCheckedChange = {}, buffering = true)
            PlayPauseToggleButton(checked = true, onCheckedChange = {}, buffering = true)
        }
    }
}
