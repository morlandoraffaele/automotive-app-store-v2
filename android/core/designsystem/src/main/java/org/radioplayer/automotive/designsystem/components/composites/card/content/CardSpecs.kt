package org.radioplayer.automotive.designsystem.components.composites.card.content

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import org.radioplayer.automotive.designsystem.theme.AutomotiveTheme

data class StackedCardSpec(
    val levelOneOffsetY: Dp,
    val levelOneHeight: Dp,
    val levelTwoOffsetY: Dp,
    val levelTwoHeight: Dp,
)

data class CardSpecs(
    val width: Dp,
    val placeholderHeight: Dp,
    val artworkHeight: Dp,
    val shape: Shape,
    val colors: CardContentColors,
    val border: BorderStroke,
    val stacked: StackedCardSpec? = null,
)

val CardContentType.specs: CardSpecs
    @Composable
    get() = when (this) {
        is CardContentType.Station -> CardSpecs(
            width = 256.dp,
            placeholderHeight = 240.dp,
            artworkHeight = 240.dp,
            shape = RoundedCornerShape(AutomotiveTheme.measurement.shapes.extraLarge),
            colors = CardContentColors(
                containerColor = Color.Unspecified,
                contentColor = AutomotiveTheme.colorScheme.onSurface
            ),
            border = BorderStroke(AutomotiveTheme.stroke.none, Color.Unspecified)
        )

        is CardContentType.Podcast -> CardSpecs(
            width = 232.dp,
            placeholderHeight = 216.dp,
            artworkHeight = 186.dp,
            shape = RoundedCornerShape(AutomotiveTheme.measurement.shapes.extraLarge),
            colors = CardContentColors(
                containerColor = Color.Unspecified,
                contentColor = AutomotiveTheme.colorScheme.onSurface
            ),
            border = BorderStroke(AutomotiveTheme.stroke.none, Color.Unspecified),
            stacked = StackedCardSpec(
                levelOneOffsetY = (-92).dp,
                levelOneHeight = 124.dp,
                levelTwoOffsetY = (-46).dp,
                levelTwoHeight = 155.dp,
            )

        )

        // CardEpisode currently matches CardStation's plain square-artwork layout (no stacked
        // layers), but is spec'd separately - like CardEpisode itself - so it can diverge later
        // without touching Station. See CardContent.kt's `is CardContentType.Episode -> CardEpisode(...)`.
        is CardContentType.Episode -> CardSpecs(
            width = 256.dp,
            placeholderHeight = 240.dp,
            artworkHeight = 240.dp,
            shape = RoundedCornerShape(AutomotiveTheme.measurement.shapes.extraLarge),
            colors = CardContentColors(
                containerColor = Color.Unspecified,
                contentColor = AutomotiveTheme.colorScheme.onSurface
            ),
            border = BorderStroke(AutomotiveTheme.stroke.none, Color.Unspecified)
        )

        is CardContentType.LocalStation -> CardSpecs(
            width = 256.dp,
            placeholderHeight = 224.dp,
            artworkHeight = 224.dp,
            shape = RoundedCornerShape(AutomotiveTheme.measurement.shapes.extraLargeIncreased),
            colors = CardContentColors(
                containerColor = AutomotiveTheme.colorScheme.surfaceContainer,
                contentColor = AutomotiveTheme.colorScheme.onSurface
            ),
            border = BorderStroke(
                AutomotiveTheme.stroke.thin,
                AutomotiveTheme.colorScheme.outlineVariant
            )
        )
    }
