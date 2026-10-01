package org.radioplayer.automotive.designsystem.components.features.savethesong.dialog

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import org.radioplayer.automotive.designsystem.components.composites.button.text.ButtonText
import org.radioplayer.automotive.designsystem.components.primitives.Text
import org.radioplayer.automotive.designsystem.components.primitives.icon.Icon
import org.radioplayer.automotive.designsystem.components.primitives.icon.IconSetEnum
import org.radioplayer.automotive.designsystem.theme.AutomotiveTheme

/**
 * @param onDismissRequest Callback invoked when the dialog is dismissed (scrim tap, back press).
 * @param title The headline rendered at the top of the left column.
 * @param description The supporting text rendered under the title.
 * @param label The text label of the [ButtonText] CTA on the left column.
 * @param logo Caller-supplied slot for the promoted service's logo/wordmark art.
 * @param infoText Text rendered under the logo on the right column (e.g. "Available on Spotify,
 * Apple Music and others").
 * @param modifier Styling and sizing modifier.
 * @param iconButton Optional leading icon rendered inside the [ButtonText] CTA.
 * @param onClick Callback invoked when the [ButtonText] CTA is clicked.
 * @param glowOpacity Opacity of the radial glow overlay.
 * @param colors [SaveTheSongDialogColors] driving the container, glow and text colors.
 */
@Composable
fun SaveTheSongDialog(
    onDismissRequest: () -> Unit,
    title: String,
    description: String,
    label: String,
    logo: @Composable () -> Unit,
    infoText: String,
    modifier: Modifier = Modifier,
    iconButton: @Composable (() -> Unit)? = null,
    onClick: () -> Unit = {},
    glowOpacity: Float = SaveTheSongDialogDefaults.GlowOpacity,
    colors: SaveTheSongDialogColors = SaveTheSongDialogDefaults.colors(),
) {
    val cornerRadius = SaveTheSongDialogDefaults.CornerRadius
    val containerPadding = SaveTheSongDialogDefaults.ContainerPadding

    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        Box(
            modifier = modifier
                .width(SaveTheSongDialogDefaults.ContainerWidth)
                .defaultMinSize(minHeight = SaveTheSongDialogDefaults.ContainerMinHeight)
                .clip(RoundedCornerShape(cornerRadius))
                .background(colors.container)
                .drawWithCache {
                    val bleed = SaveTheSongDialogDefaults.GlowBleed.toPx()
                    val artWidth = SaveTheSongDialogDefaults.RightColumnWidth.toPx()
                    val baseRadius = (artWidth + bleed * 2) / 2f
                    val centerX = size.width - containerPadding.toPx() - artWidth / 2f
                    val center = Offset(centerX, size.height / 2f)
                    val brush = Brush.radialGradient(
                        colors = listOf(
                            colors.glow.copy(alpha = glowOpacity),
                            colors.glow.copy(alpha = 0f)
                        ),
                        center = center,
                        radius = baseRadius + SaveTheSongDialogDefaults.GlowBlurRadius.toPx()
                    )
                    onDrawBehind { drawRect(brush = brush) }
                }
                .padding(containerPadding),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .defaultMinSize(minWidth = SaveTheSongDialogDefaults.RowMinWidth)
                    .height(IntrinsicSize.Min),
                horizontalArrangement = Arrangement.spacedBy(SaveTheSongDialogDefaults.RowGap),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    verticalArrangement = Arrangement.spacedBy(SaveTheSongDialogDefaults.ColumnGap)
                ) {
                    Text(
                        text = title,
                        modifier = Modifier.fillMaxWidth(),
                        color = colors.title,
                        style = SaveTheSongDialogDefaults.TitleStyle,
                        maxLines = 2,
                    )

                    Text(
                        text = description,
                        modifier = Modifier.fillMaxWidth(),
                        color = colors.description,
                        style = SaveTheSongDialogDefaults.DescriptionStyle,
                    )

                    ButtonText(
                        onClick = onClick,
                        label = label,
                        icon = iconButton,
                    )
                }

                Column(
                    modifier = Modifier
                        .width(SaveTheSongDialogDefaults.RightColumnWidth)
                        .fillMaxHeight(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(
                        SaveTheSongDialogDefaults.RightColumnGap,
                        Alignment.CenterVertically,
                    ),
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(
                                horizontal = SaveTheSongDialogDefaults.LogoHorizontalPadding,
                                vertical = SaveTheSongDialogDefaults.LogoVerticalPadding,
                            ),
                        contentAlignment = Alignment.Center,
                    ) {
                        logo()
                    }

                    Text(
                        text = infoText,
                        modifier = Modifier.fillMaxWidth(),
                        color = colors.caption,
                        style = SaveTheSongDialogDefaults.CaptionStyle,
                        textAlign = TextAlign.Center,
                        maxLines = 2,
                    )
                }
            }
        }
    }
}


@Preview(
    name = "Save The Song Dialog - Light Mode",
    widthDp = 800,
    heightDp = 400,
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_NO or Configuration.UI_MODE_TYPE_CAR,
)
@Preview(
    name = "Save The Song Dialog - Dark Mode",
    widthDp = 800,
    heightDp = 400,
    showBackground = false,
    uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_CAR,
)
@Composable
private fun SaveTheSongDialogPreview() {
    AutomotiveTheme {
        SaveTheSongDialog(
            onDismissRequest = {},
            title = "Love it? Save it!",
            description = "Link your music service and save any song playing on the radio straight to your car's library.",
            label = "Got it!",
            iconButton = {
                Icon(
                    name = IconSetEnum.Favorite,
                    contentDescription = null,
                )
            },
            logo = {
                Text(
                    text = "♫ Music",
                    color = AutomotiveTheme.colorScheme.onSurface,
                    style = AutomotiveTheme.typography.body2Medium,
                )
            },
            infoText = "and others",
            onClick = {},
        )
    }
}
