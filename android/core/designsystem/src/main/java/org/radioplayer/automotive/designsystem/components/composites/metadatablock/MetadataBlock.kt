package org.radioplayer.automotive.designsystem.components.composites.metadatablock

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.radioplayer.automotive.designsystem.components.composites.tag.Tag
import org.radioplayer.automotive.designsystem.components.composites.tag.TagColorStyle
import org.radioplayer.automotive.designsystem.components.composites.tag.TagContent
import org.radioplayer.automotive.designsystem.components.composites.tag.TagSlots
import org.radioplayer.automotive.designsystem.components.primitives.Text
import org.radioplayer.automotive.designsystem.theme.AutomotiveTheme
import org.radioplayer.radio.image.components.RadioImage
import org.radioplayer.radio.image.models.ImageFormat
import org.radioplayer.radio.image.models.ImageRequest
import org.radioplayer.radio.image.models.ImageSource

@Composable
private fun RadioMetadataContent(state: MetadataBlockState.Radio) {
    Column(
        verticalArrangement = Arrangement.spacedBy(AutomotiveTheme.measurement.spaces.largeIncreased)
    ) {
        if (state.badges.isNotEmpty() || !state.headerText.isNullOrEmpty()) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(AutomotiveTheme.measurement.spaces.medium)
            ) {
                state.badges.forEach { badge ->
                    Tag(
                        slots = TagSlots.Leading(
                            content = TagContent.Custom(
                                layoutRole = TagContent.LayoutRole.TEXT,
                                accessibilityLabel = "",
                                content = {
                                    Text(
                                        text = badge,
                                        style = AutomotiveTheme.typography.body3
                                    )
                                }
                            )
                        ),
                        colorStyle = TagColorStyle.OUTLINED
                    )
                }
                state.headerText?.let { header ->
                    Text(
                        text = header,
                        style = AutomotiveTheme.typography.body3,
                        color = AutomotiveTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }

        Row() {
            Column(
                verticalArrangement = Arrangement.spacedBy(AutomotiveTheme.measurement.spaces.medium)
            ) {
                Text(
                    text = state.title,
                    style = AutomotiveTheme.typography.display2,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                state.subtitle?.let { subtitle ->
                    Text(
                        text = subtitle,
                        style = AutomotiveTheme.typography.body1,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

@Composable
private fun PodcastMetadataContent(state: MetadataBlockState.Podcast) {
    Column(
        verticalArrangement = Arrangement.spacedBy(AutomotiveTheme.measurement.spaces.medium)
    ) {
        state.seriesName?.let { series ->
            Text(
                text = series,
                style = AutomotiveTheme.typography.body3,
                color = AutomotiveTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        Text(
            text = state.episodeTitle,
            style = AutomotiveTheme.typography.display2,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        Tag(
            slots = TagSlots.Leading(
                content = TagContent.Custom(
                    layoutRole = TagContent.LayoutRole.TEXT,
                    accessibilityLabel = "",
                    content = {
                        Text(
                            text = state.releaseDate,
                            style = AutomotiveTheme.typography.body3
                        )
                    }
                )
            ),
            colorStyle = TagColorStyle.OUTLINED
        )
    }
}

@Composable
private fun MetadataBlockArtwork(
    modifier: Modifier = Modifier,
    artwork: ImageRequest,
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(AutomotiveTheme.measurement.shapes.largeIncreased))
    ) {
        RadioImage(
            modifier = Modifier.fillMaxSize(),
            request = artwork
        )
    }
}

@Composable
private fun MetadataBlockLandscape(
    state: MetadataBlockState,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(AutomotiveTheme.measurement.spaces.large),
        verticalAlignment = Alignment.CenterVertically
    ) {
        MetadataBlockArtwork(
            modifier = Modifier
                .fillMaxHeight()
                .aspectRatio(1f),
            artwork = state.artwork
        )

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.Center
        ) {
            when (state) {
                is MetadataBlockState.Radio -> RadioMetadataContent(state = state)
                is MetadataBlockState.Podcast -> PodcastMetadataContent(state = state)
            }
        }
    }
}

@Composable
private fun MetadataBlockPortrait(
    state: MetadataBlockState,
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(
            AutomotiveTheme.measurement.spaces.large
        ),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            contentAlignment = Alignment.Center,
        ) {
            MetadataBlockArtwork(
                modifier = Modifier
                    .fillMaxHeight()
                    .aspectRatio(1f),
                artwork = state.artwork,
            )
        }

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.Start,
        ) {
            when (state) {
                is MetadataBlockState.Radio ->
                    RadioMetadataContent(state = state)

                is MetadataBlockState.Podcast ->
                    PodcastMetadataContent(state = state)
            }
        }
    }
}


/**
 * Displays metadata for the currently playing content alongside its artwork.
 *
 * Renders a square artwork image next to a vertically centered metadata column.
 * The content of that column adapts based on the concrete type of [state]:
 * radio metadata (badges, header, title, subtitle) or podcast metadata
 * (series name, episode title, release date).
 *
 * The layout adapts to the provided [orientation]:
 * - [MetadataBlockOrientation.Landscape]: artwork on the left, metadata on the right.
 * - [MetadataBlockOrientation.Portrait]: artwork on top, metadata below, centered.
 *
 * @param modifier [Modifier] applied to the root [Surface].
 * @param state the metadata to display, as a [MetadataBlockState] — either
 * [MetadataBlockState.Radio] or [MetadataBlockState.Podcast].
 * @param orientation the layout orientation. Defaults to the current device orientation.
 */
@Composable
fun MetadataBlock(
    modifier: Modifier = Modifier,
    state: MetadataBlockState,
    orientation: MetadataBlockOrientation =
        if (LocalConfiguration.current.orientation == Configuration.ORIENTATION_PORTRAIT) {
            MetadataBlockOrientation.Portrait
        } else {
            MetadataBlockOrientation.Landscape
        },
) {
    Surface(
        modifier = modifier,
        color = Color.Transparent,
        contentColor = AutomotiveTheme.colorScheme.onSurface
    ) {
        when (orientation) {
            MetadataBlockOrientation.Landscape -> MetadataBlockLandscape(state = state)
            MetadataBlockOrientation.Portrait -> MetadataBlockPortrait(state = state)
        }
    }
}

@Preview(
    name = "MetadataBlock - Landscape Light",
    showBackground = true,
    widthDp = 1000,
    heightDp = 300,
    uiMode = Configuration.UI_MODE_NIGHT_NO or Configuration.UI_MODE_TYPE_CAR,
)

@Preview(
    name = "MetadataBlock - Landscape Dark",
    showBackground = false,
    widthDp = 1000,
    heightDp = 300,
    uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_CAR,
)

@Preview(
    name = "MetadataBlock - Portrait Light",
    showBackground = true,
    widthDp = 500,
    heightDp = 800,
    uiMode = Configuration.UI_MODE_NIGHT_NO or Configuration.UI_MODE_TYPE_CAR,
)

@Preview(
    name = "MetadataBlock - Portrait Dark",
    showBackground = false,
    widthDp = 500,
    heightDp = 800,
    uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_CAR,
)

@Composable
private fun MetadataBlockPreview() {
    AutomotiveTheme() {
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Radio Option 1: Station Info
            MetadataBlock(
                state = MetadataBlockState.Radio(
                    badges = listOf("IP", "DAB"),
                    title = "Station Name",
                    subtitle = "(Optional) Station Description",
                    artwork = ImageRequest(
                        source = ImageSource.Remote(
                            url = ""
                        ),
                        format = ImageFormat.Static
                    )
                )
            )

            // Radio Option 2: Song Info
            MetadataBlock(
                state = MetadataBlockState.Radio(
                    badges = listOf("IP", "DAB"),
                    headerText = "Station Name",
                    title = "Song Title",
                    subtitle = "Artist Name",
                    artwork = ImageRequest(
                        source = ImageSource.Remote(
                            url = "",
                        ),
                        format = ImageFormat.Static
                    )
                )
            )

            // Radio Option 3: Show Info
            MetadataBlock(
                state = MetadataBlockState.Radio(
                    badges = listOf("IP", "DAB"),
                    headerText = "Station Name",
                    title = "Current Aired Show Name",
                    subtitle = "(Optional) Show Description",
                    artwork = ImageRequest(
                        source = ImageSource.Remote(
                            url = "",
                        ),
                        format = ImageFormat.Static
                    )
                )
            )

            // Radio Option 4: FM Frequency
            MetadataBlock(
                state = MetadataBlockState.Radio(
                    badges = listOf("FM"),
                    title = "FM Station Frequency",
                    artwork = ImageRequest(
                        source = ImageSource.Remote(
                            url = "",
                        ),
                        format = ImageFormat.Static
                    )
                )
            )
        }
    }
}
