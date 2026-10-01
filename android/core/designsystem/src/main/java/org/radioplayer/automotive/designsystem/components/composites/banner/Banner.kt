package org.radioplayer.automotive.designsystem.components.composites.banner

import android.content.res.Configuration
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.tooling.preview.Preview
import org.radioplayer.automotive.designsystem.components.composites.tag.Tag
import org.radioplayer.automotive.designsystem.components.composites.tag.TagContent
import org.radioplayer.automotive.designsystem.components.composites.tag.TagSlots
import org.radioplayer.automotive.designsystem.components.primitives.Text
import org.radioplayer.automotive.designsystem.components.primitives.icon.Icon
import org.radioplayer.automotive.designsystem.components.primitives.icon.IconSetEnum
import org.radioplayer.automotive.designsystem.interaction.InteractionState
import org.radioplayer.automotive.designsystem.interaction.rememberFocusRingStroke
import org.radioplayer.automotive.designsystem.interaction.rememberInteractionState
import org.radioplayer.automotive.designsystem.theme.AutomotiveTheme
import org.radioplayer.radio.image.components.RadioImage
import org.radioplayer.radio.image.models.ImageFormat
import org.radioplayer.radio.image.models.ImageRequest
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
private fun OverlayWithIcon(
    modifier: Modifier = Modifier,
    name: IconSetEnum,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(AutomotiveTheme.measurement.shapes.extraLargeIncreased)),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            size = AutomotiveTheme.icon.macro,
            name = name,
        )
    }
}

@Composable
private fun BannerLayout(
    imageRequest: ImageRequest,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    textLabel: String? = null,
    enabled: Boolean = true,
    iconNameOverlay: IconSetEnum = IconSetEnum.Droid,
    interactionSource: MutableInteractionSource? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
) {
    val actualInteractionSource = interactionSource ?: remember { MutableInteractionSource() }
    val interactionState by rememberInteractionState(actualInteractionSource)

    val resolvedBorder = resolveBorderTreatment(interactionState)

    val alpha = resolveAlpha(enabled)

    Surface(
        modifier = modifier
            .alpha(alpha)
            .width(BannerDefaults.containerWidth)
            .height(BannerDefaults.containerHeight)
            .defaultMinSize(minWidth = BannerDefaults.containerMinWidth),
        enabled = enabled,
        contentColor = AutomotiveTheme.colorScheme.onSurface,
        onClick = onClick,
        border = resolvedBorder,
        shape = RoundedCornerShape(AutomotiveTheme.measurement.shapes.extraLargeIncreased),
        interactionSource = actualInteractionSource
    ) {

        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            RadioImage(
                modifier = Modifier.fillMaxSize(), request = imageRequest
            )

            if(textLabel != null) {
                Box(
                    modifier = Modifier.padding(
                        AutomotiveTheme.measurement.spaces.small,
                        AutomotiveTheme.measurement.spaces.small
                    )
                ) {
                    Tag(
                        slots = TagSlots.Leading(
                            content = TagContent.Custom(
                                layoutRole = TagContent.LayoutRole.TEXT,
                                accessibilityLabel = "",
                                content = {
                                    Text(
                                        text = textLabel,
                                        style = AutomotiveTheme.typography.body3,
                                    )
                                })
                        )
                    )
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(AutomotiveTheme.measurement.spaces.medium),
                contentAlignment = Alignment.CenterEnd
            ) {
                trailingIcon?.let { icon ->
                    Box(
                        modifier = Modifier.defaultMinSize(
                            minWidth = AutomotiveTheme.measurement.sizes.minTapArea,
                            minHeight = AutomotiveTheme.measurement.sizes.minTapArea
                        ),
                        contentAlignment = Alignment.Center
                    ) {
                        icon()
                    }
                }
            }

            if (!enabled) {
                OverlayWithIcon(
                    name = iconNameOverlay
                )
            }
        }
    }
}

@Composable
fun Banner(
    imageRequest: ImageRequest,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    textLabel: String? = null,
    enabled: Boolean = true,
    iconNameOverlay: IconSetEnum = IconSetEnum.Droid,
    interactionSource: MutableInteractionSource? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
) {
    BannerLayout(
        modifier = modifier,
        onClick = onClick,
        enabled = enabled,
        imageRequest = imageRequest,
        textLabel = textLabel,
        iconNameOverlay = iconNameOverlay,
        trailingIcon = trailingIcon,
        interactionSource = interactionSource
    )
}

@Preview(
    name = "Banner - Light Mode",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_NO or Configuration.UI_MODE_TYPE_CAR,
)

@Preview(
    name = "Banner - Dark Mode",
    showBackground = false,
    uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_CAR,
)

@Composable
fun BannerPreview() {
    AutomotiveTheme() {
        Banner(
            onClick = {},
            enabled = true,
            trailingIcon = { Icon() },
            imageRequest = ImageRequest(
                source = ImageSource.Remote(url = ""),
                format = ImageFormat.Static
            )
        )
    }
}