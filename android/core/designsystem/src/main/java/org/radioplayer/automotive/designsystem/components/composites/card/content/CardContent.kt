package org.radioplayer.automotive.designsystem.components.composites.card.content

import android.content.res.Configuration
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import org.radioplayer.automotive.designsystem.components.primitives.Text
import org.radioplayer.automotive.designsystem.components.primitives.icon.Icon
import org.radioplayer.automotive.designsystem.components.primitives.icon.IconSetEnum
import org.radioplayer.automotive.designsystem.interaction.InteractionState
import org.radioplayer.automotive.designsystem.interaction.rememberFocusRingStroke
import org.radioplayer.automotive.designsystem.interaction.rememberInteractionState
import org.radioplayer.automotive.designsystem.theme.AutomotiveTheme
import org.radioplayer.automotive.designsystem.utils.customShadow

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

private fun resolveAlpha(enabled: Boolean) =
    if (enabled) 1F else 0.38F // TODO: Alpha value should come from AutomotiveTheme

@Composable
private fun BoxScope.Badge(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .align(Alignment.TopEnd)
            .padding(all = AutomotiveTheme.measurement.spaces.extraExtraSmall)
            .customShadow(
                color = AutomotiveTheme.colorScheme.aaosScrimLow,
                shape = RoundedCornerShape(AutomotiveTheme.measurement.shapes.small)
            )
    ) {
        Box(
            modifier = Modifier
                .size(width = BadgeWidth, height = BadgeHeight)
                .clip(RoundedCornerShape(AutomotiveTheme.measurement.shapes.small))
                .background(AutomotiveTheme.colorScheme.onSurface),
        ) {
            content()
        }
    }
}

@Composable
private fun OverlayWithIcon(
    modifier: Modifier = Modifier,
    name: IconSetEnum,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(AutomotiveTheme.measurement.shapes.largeIncreased))
            .background(AutomotiveTheme.colorScheme.scrim.copy(alpha = 0.8f)),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            size = AutomotiveTheme.icon.macro,
            name = name,
        )
    }
}

/**
 * The square artwork tile. [modifier] is expected to fix exactly one dimension - the aspect ratio
 * derives the other. Station/LocalStation size it off the card width (`fillMaxWidth`) so the tile
 * follows a card that is filling its rail slot; the podcast stack sizes it by height, since there
 * it is one layer of a stack that is itself already width-driven.
 */
@Composable
private fun Artwork(
    modifier: Modifier = Modifier,
    iconName: IconSetEnum,
    enabled: Boolean,
    content: @Composable () -> Unit,
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(AutomotiveTheme.measurement.shapes.largeIncreased))
            .aspectRatio(1f)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            content()
        }

        if (!enabled) {
            OverlayWithIcon(name = iconName)
        }
    }
}

@Composable
private fun StackedLayer(
    offsetY: Dp,
    height: Dp,
    color: Color,
) {
    Box(
        modifier = Modifier
            .offset(y = offsetY)
            .clip(RoundedCornerShape(AutomotiveTheme.measurement.shapes.largeIncreased))
            .height(height)
            .aspectRatio(1f)
            .background(color)
    )
}

@Composable
private fun CardScaffold(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    specs: CardSpecs,
    colors: CardContentColors,
    border: BorderStroke,
    shape: Shape,
    enabled: Boolean,
    interactionSource: MutableInteractionSource?,
    contentPadding: Dp,
    verticalArrangement: Arrangement.Vertical = Arrangement.Top,
    horizontalAlignment: Alignment.Horizontal = Alignment.Start,
    badgeContent: @Composable (() -> Unit)? = null,
    imageContent: @Composable ColumnScope.() -> Unit,
) {
    val actualInteractionSource = interactionSource ?: remember { MutableInteractionSource() }
    val interactionState by rememberInteractionState(actualInteractionSource)

    val resolvedBorder = resolveBorderTreatment(interactionState, border)
    val alpha = resolveAlpha(enabled)

    // specs.width is the card's natural width, not a hard one: it is applied here, outside the
    // Surface, so a parent that hands the card a fixed width - a Rail slot, which propagates its
    // minimum constraints - overrides it in both directions. The Surface then fills whatever width
    // won, and the artwork below is measured from that, matching the Figma cards' FILL sizing.
    Box(
        modifier = modifier
            .alpha(alpha)
            .width(specs.width),
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .defaultMinSize(minWidth = AutomotiveTheme.measurement.sizes.minContentCell),
            onClick = onClick,
            enabled = enabled,
            shape = shape,
            color = colors.containerColor,
            contentColor = colors.contentColor,
            border = resolvedBorder,
            interactionSource = actualInteractionSource
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(contentPadding),
                verticalArrangement = verticalArrangement,
                horizontalAlignment = horizontalAlignment,
                content = imageContent,
            )
        }

        if (badgeContent != null) {
            Badge(
                content = badgeContent
            )
        }
    }
}

@Composable
private fun CardStation(
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
    specs: CardSpecs,
    colors: CardContentColors = specs.colors,
    border: BorderStroke = specs.border,
    shape: Shape = specs.shape,
    enabled: Boolean = true,
    content: CardContentType.Station,
    interactionSource: MutableInteractionSource? = null,
    badgeContent: @Composable (() -> Unit)? = null,
    imageContent: @Composable () -> Unit,
) {
    CardScaffold(
        modifier = modifier,
        onClick = onClick,
        specs = specs,
        colors = colors,
        border = border,
        shape = shape,
        enabled = enabled,
        interactionSource = interactionSource,
        contentPadding = AutomotiveTheme.measurement.spaces.extraSmall,
        verticalArrangement = Arrangement.spacedBy(AutomotiveTheme.measurement.spaces.large),
        horizontalAlignment = Alignment.CenterHorizontally,
        badgeContent = badgeContent
    ) {
        Artwork(
            modifier = Modifier.fillMaxWidth(),
            iconName = content.iconName,
            enabled = enabled,
            content = imageContent
        )

        Text(
            text = content.title,
            color = AutomotiveTheme.colorScheme.onSurface,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            style = AutomotiveTheme.typography.body2,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun CardEpisode(
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
    specs: CardSpecs,
    colors: CardContentColors = specs.colors,
    border: BorderStroke = specs.border,
    shape: Shape = specs.shape,
    enabled: Boolean = true,
    content: CardContentType.Episode,
    interactionSource: MutableInteractionSource? = null,
    badgeContent: @Composable (() -> Unit)? = null,
    imageContent: @Composable () -> Unit,
) {
    CardScaffold(
        modifier = modifier,
        onClick = onClick,
        specs = specs,
        colors = colors,
        border = border,
        shape = shape,
        enabled = enabled,
        interactionSource = interactionSource,
        contentPadding = AutomotiveTheme.measurement.spaces.extraSmall,
        verticalArrangement = Arrangement.spacedBy(AutomotiveTheme.measurement.spaces.large),
        horizontalAlignment = Alignment.CenterHorizontally,
        badgeContent = badgeContent
    ) {
        Artwork(
            modifier = Modifier.fillMaxWidth(),
            iconName = content.iconName,
            enabled = enabled,
            content = imageContent
        )

        Text(
            text = content.title,
            color = AutomotiveTheme.colorScheme.onSurface,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            style = AutomotiveTheme.typography.body2,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun CardPodcast(
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
    specs: CardSpecs,
    colors: CardContentColors = specs.colors,
    border: BorderStroke = specs.border,
    shape: Shape = specs.shape,
    enabled: Boolean = true,
    content: CardContentType.Podcast,
    interactionSource: MutableInteractionSource? = null,
    badgeContent: @Composable (() -> Unit)? = null,
    imageContent: @Composable () -> Unit,
) {
    CardScaffold(
        modifier = modifier,
        onClick = onClick,
        specs = specs,
        colors = colors,
        border = border,
        shape = shape,
        enabled = enabled,
        interactionSource = interactionSource,
        contentPadding = AutomotiveTheme.measurement.spaces.extraSmall,
        verticalArrangement = Arrangement.spacedBy(AutomotiveTheme.measurement.spaces.large),
        horizontalAlignment = Alignment.CenterHorizontally,
        badgeContent = badgeContent
    ) {
        // The stack's geometry (both offsets and both layer heights) is specified against
        // specs.placeholderHeight, so when the card fills a wider or narrower slot every value in
        // it has to move together - otherwise the layers detach from the artwork they sit behind.
        // One scale factor off the measured box keeps them locked; it is exactly 1f at the card's
        // natural width, where the box measures placeholderHeight.
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f),
            contentAlignment = Alignment.BottomCenter
        ) {
            val scale = maxWidth / specs.placeholderHeight

            specs.stacked?.let { stacked ->
                StackedLayer(
                    offsetY = stacked.levelOneOffsetY * scale,
                    height = stacked.levelOneHeight * scale,
                    color = AutomotiveTheme.colorScheme.surfaceContainer,
                )
                StackedLayer(
                    offsetY = stacked.levelTwoOffsetY * scale,
                    height = stacked.levelTwoHeight * scale,
                    color = AutomotiveTheme.colorScheme.surfaceContainerHighest,
                )
            }

            Artwork(
                modifier = Modifier.height(specs.artworkHeight * scale),
                iconName = content.iconName,
                enabled = enabled,
                content = imageContent
            )
        }

        Text(
            text = content.title,
            color = AutomotiveTheme.colorScheme.onSurface,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            style = AutomotiveTheme.typography.body2,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun CardLocalStation(
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
    specs: CardSpecs,
    colors: CardContentColors = specs.colors,
    border: BorderStroke = specs.border,
    shape: Shape = specs.shape,
    enabled: Boolean = true,
    content: CardContentType.LocalStation,
    interactionSource: MutableInteractionSource? = null,
    badgeContent: @Composable (() -> Unit)? = null,
    imageContent: @Composable () -> Unit,
) {
    CardScaffold(
        modifier = modifier,
        onClick = onClick,
        specs = specs,
        colors = colors,
        border = border,
        shape = shape,
        enabled = enabled,
        interactionSource = interactionSource,
        contentPadding = AutomotiveTheme.measurement.spaces.medium,
        badgeContent = badgeContent
    ) {
        Artwork(
            modifier = Modifier.fillMaxWidth(),
            iconName = content.iconName,
            enabled = enabled,
            content = imageContent
        )
    }
}

/**
 * Displays a styled card component designed for automotive interfaces, capable of rendering
 * various content types such as radio stations, podcasts, or local stations.
 *
 * The layout, visual hierarchy, and artwork styling dynamically adapt based on the provided [type].
 *
 * @param modifier The [Modifier] to be applied to the card container.
 * @param onClick Callback invoked when the user clicks or selects the card.
 * @param enabled Controls the enabled state of the card. When `false`, user input is disabled
 * and a reduced alpha visual treatment is applied. Defaults to `true`.
 * @param type The [CardContentType] specifying the content variant (e.g., [CardContentType.Station],
 * [CardContentType.Podcast], or [CardContentType.LocalStation]) along with its associated metadata and visual specs.
 * @param interactionSource An optional [MutableInteractionSource] to observe and track user interactions
 * (e.g., focus, press states) on this card.
 */
@Composable
fun CardContent(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    enabled: Boolean = true,
    type: CardContentType,
    interactionSource: MutableInteractionSource? = null,
    badgeContent: @Composable (() -> Unit)? = null,
    imageContent: @Composable () -> Unit,
) {
    val specs = type.specs

    when (type) {
        is CardContentType.Station -> CardStation(
            modifier = modifier,
            onClick = onClick,
            enabled = enabled,
            content = type,
            specs = specs,
            interactionSource = interactionSource,
            badgeContent = badgeContent,
            imageContent = imageContent
        )

        is CardContentType.Podcast -> CardPodcast(
            modifier = modifier,
            onClick = onClick,
            enabled = enabled,
            content = type,
            specs = specs,
            interactionSource = interactionSource,
            badgeContent = badgeContent,
            imageContent = imageContent
        )

        is CardContentType.Episode -> CardEpisode(
            modifier = modifier,
            onClick = onClick,
            enabled = enabled,
            content = type,
            specs = specs,
            interactionSource = interactionSource,
            badgeContent = badgeContent,
            imageContent = imageContent
        )

        is CardContentType.LocalStation -> CardLocalStation(
            modifier = modifier,
            onClick = onClick,
            enabled = enabled,
            content = type,
            specs = specs,
            interactionSource = interactionSource,
            badgeContent = badgeContent,
            imageContent = imageContent
        )
    }
}

@Preview(
    name = "CardContent - Light Mode",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_NO or Configuration.UI_MODE_TYPE_CAR,
)

@Preview(
    name = "CardContent - Dark Mode",
    showBackground = false,
    uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_CAR,
)

@Composable
fun CardContentPreview() {
    val Enabled = true
    AutomotiveTheme() {
        Column(
        ) {
            /**
             *   CardContent/ Station
             */
            CardContent(
                onClick = {},
                type = CardContentType.Station(
                    artwork = "",
                    title = "A long media item for a grid layout falling on 2 rows",
                    iconName = IconSetEnum.Droid
                ),
                enabled = Enabled
            ) {}

            Spacer(modifier = Modifier.height(20.dp))

            /**
             * CardContent/Podcast
             */
            CardContent(
                onClick = {},
                type = CardContentType.Podcast(
                    artwork = "",
                    title = "A long media item for a grid layout falling on 2 rows",
                    iconName = IconSetEnum.Droid
                ),
                enabled = Enabled
            ) {}

            Spacer(modifier = Modifier.height(20.dp))

            /**
             * CardContent/Local Station
             */
            CardContent(
                onClick = {},
                type = CardContentType.LocalStation(
                    artwork = "",
                    iconName = IconSetEnum.Droid
                ),
                enabled = Enabled
            ) {}
        }
    }
}

private val BadgeWidth = 66.dp
private val BadgeHeight = 44.dp