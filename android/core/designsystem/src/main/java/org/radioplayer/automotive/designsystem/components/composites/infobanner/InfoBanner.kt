package org.radioplayer.automotive.designsystem.components.composites.infobanner

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.radioplayer.automotive.designsystem.components.composites.containertappableicon.ContainerTappableIcon
import org.radioplayer.automotive.designsystem.components.composites.iconbutton.standard.IconButtonStandard
import org.radioplayer.automotive.designsystem.components.composites.iconbutton.standard.IconButtonStandardDefaults
import org.radioplayer.automotive.designsystem.components.primitives.Text
import org.radioplayer.automotive.designsystem.components.primitives.icon.Icon
import org.radioplayer.automotive.designsystem.components.primitives.icon.IconSetEnum
import org.radioplayer.automotive.designsystem.theme.AutomotiveTheme

/**
 * A persistent, in-layout status message (Figma: `Info Banner / Standard`). Unlike a
 * [org.radioplayer.automotive.designsystem.components.composites.toast.Toast] it does not float
 * over the screen or auto-dismiss — it occupies real space in the layout and stays until the
 * condition it reports is gone.
 *
 * Not to be confused with
 * [org.radioplayer.automotive.designsystem.components.composites.banner.Banner], which is the
 * promotional artwork tile.
 *
 * The component fills the width it is given; size it from the call site (the Figma frame is 884dp
 * wide inside a 1381dp container, i.e. not a fixed value the component should bake in).
 *
 * @param message the banner's text content. Wraps rather than truncating at one line — the Figma
 *   frame hugs its content vertically, so a longer message makes the banner taller.
 * @param leadingIcon optional icon slot before the message, laid out at
 *   [InfoBannerDefaults.IconSize]. Figma shows this slot as present by default but its content is
 *   an unconfigured placeholder, so callers pick the icon.
 * @param trailing optional content after the message. Pass [InfoBannerTrailing.Icon] for a
 *   passive icon rendered at [InfoBannerDefaults.IconSize], or [InfoBannerTrailing.Action] for an
 *   interactive dismiss/action affordance rendered as an icon button — sized to the theme's
 *   minimum tap area and tinted with [InfoBannerColors.content]. The banner's end padding shrinks
 *   to [InfoBannerDefaults.ContentPaddingEndForAction] for an action, since the button already
 *   carries the min-tap-area inset itself. This component adds no click handling of its own.
 */
@Composable
fun InfoBanner(
    message: String,
    modifier: Modifier = Modifier,
    colors: InfoBannerColors = InfoBannerDefaults.colors(),
    leadingIcon: @Composable (() -> Unit)? = null,
    trailing: InfoBannerTrailing? = null,
) {
    val iconSize = InfoBannerDefaults.IconSize
    val contentPaddingEnd = if (trailing is InfoBannerTrailing.Action) {
        InfoBannerDefaults.ContentPaddingEndForAction
    } else {
        InfoBannerDefaults.ContentPaddingHorizontal
    }

    CompositionLocalProvider(LocalContentColor provides colors.content) {
        Row(
            modifier = modifier
                .semantics(mergeDescendants = true) {}
                .fillMaxWidth()
                .defaultMinSize(minHeight = InfoBannerDefaults.MinHeight)
                .clip(RoundedCornerShape(InfoBannerDefaults.CornerRadius))
                .background(colors.container)
                .padding(
                    PaddingValues(
                        start = InfoBannerDefaults.ContentPaddingHorizontal,
                        end = contentPaddingEnd,
                    )
                ),
            horizontalArrangement = Arrangement.spacedBy(InfoBannerDefaults.ItemSpacing),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            leadingIcon?.invoke()

            Text(
                text = message,
                modifier = Modifier.weight(1f),
                color = colors.content,
                style = InfoBannerDefaults.MessageStyle,
            )

            trailing?.let { RenderTrailing(trailing = it, contentColor = colors.content) }
        }
    }
}

@Composable
private fun RenderTrailing(
    trailing: InfoBannerTrailing,
    contentColor: Color,
) {
    when (trailing) {
        is InfoBannerTrailing.Icon -> Icon(
            name = trailing.name,
            size = InfoBannerDefaults.IconSize,
            contentDescription = trailing.contentDescription,
        )

        is InfoBannerTrailing.Action -> IconButtonStandard(
            onClick = trailing.onClick,
            enabled = trailing.enabled,
            colors = IconButtonStandardDefaults.colors(contentColor = contentColor),
            icon = {
                Icon(
                    name = trailing.name,
                    size = AutomotiveTheme.icon.primary,
                    contentDescription = trailing.contentDescription,
                )
            },
        )

        is InfoBannerTrailing.Custom -> trailing.content()
    }
}

@Preview(
    name = "Info Banner - Light Mode",
    widthDp = 940,
    heightDp = 420,
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_NO or Configuration.UI_MODE_TYPE_CAR,
)
@Preview(
    name = "Info Banner - Dark Mode",
    widthDp = 940,
    heightDp = 420,
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_CAR,
)
@Composable
private fun InfoBannerPreview() {
    AutomotiveTheme {
        Column(
            modifier = Modifier.padding(AutomotiveTheme.measurement.spaces.medium),
            verticalArrangement = Arrangement.spacedBy(AutomotiveTheme.measurement.spaces.medium),
        ) {
            val leading: @Composable () -> Unit = {
                Icon(name = IconSetEnum.Droid, size = InfoBannerDefaults.IconSize)
            }
            val trailingIcon = InfoBannerTrailing.Icon(name = IconSetEnum.Close)
            val trailingAction = InfoBannerTrailing.Action(
                onClick = {},
                name = IconSetEnum.Close,
                contentDescription = "Dismiss",
            )

            InfoBanner(
                message = "Leading and trailing icon",
                modifier = Modifier.width(884.dp),
                leadingIcon = leading,
                trailing = trailingIcon,
            )
            InfoBanner(
                message = "Leading icon and trailing action",
                modifier = Modifier.width(884.dp),
                leadingIcon = leading,
                trailing = trailingAction,
            )
            InfoBanner(
                message = "Leading only",
                modifier = Modifier.width(884.dp),
                leadingIcon = leading,
            )
            InfoBanner(
                message = "Message only",
                modifier = Modifier.width(884.dp),
            )
            InfoBanner(
                message = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do " +
                    "eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad " +
                    "minim veniam, quis nostrud exercitation ullamco laboris.",
                modifier = Modifier.width(884.dp),
                leadingIcon = leading,
                trailing = trailingAction,
            )
        }
    }
}
