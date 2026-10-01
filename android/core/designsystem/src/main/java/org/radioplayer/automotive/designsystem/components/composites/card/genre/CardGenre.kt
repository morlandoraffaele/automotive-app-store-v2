package org.radioplayer.automotive.designsystem.components.composites.card.genre

import android.content.res.Configuration
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageShader
import androidx.compose.ui.graphics.ShaderBrush
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.radioplayer.automotive.designsystem.components.primitives.Text
import org.radioplayer.automotive.designsystem.components.primitives.icon.Icon
import org.radioplayer.automotive.designsystem.components.primitives.icon.IconSetEnum
import org.radioplayer.automotive.designsystem.interaction.InteractionState
import org.radioplayer.automotive.designsystem.interaction.rememberFocusRingStroke
import org.radioplayer.automotive.designsystem.interaction.rememberInteractionState
import org.radioplayer.automotive.designsystem.theme.AutomotiveTheme
import org.radioplayer.automotive.designsystem.R
import org.radioplayer.automotive.designsystem.utils.rememberIconPatternTile

@Composable
private fun resolveBorderTreatment(
    interactionState: InteractionState,
    default: BorderStroke = BorderStroke(0.dp, Color.Unspecified),
): BorderStroke {

    return if (interactionState.isFocused) {
        rememberFocusRingStroke()
    } else {
        default
    }
}

private fun resolveAlpha(enabled: Boolean) =
    if (enabled) 1F else 0.38F // TODO: Alpha value should come from AutomotiveTheme

private const val PATTERN_ALPHA = 0.12F

@Composable
private fun OverlayWithIcon(
    modifier: Modifier = Modifier,
    name: IconSetEnum,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(AutomotiveTheme.measurement.shapes.largeIncreased)),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            size = AutomotiveTheme.icon.macro,
            name = name,
        )
    }
}

@Composable
private fun PatternBackground(
    modifier: Modifier = Modifier,
    patternIcon: ImageVector,
    tint: Color,
    scalingFactor: Float = 0.5f,
    spacingDp: androidx.compose.ui.geometry.Offset = androidx.compose.ui.geometry.Offset(0f, 0f),
) {
    val density = LocalDensity.current
    val tile = rememberIconPatternTile(
        icon = patternIcon,
        scalingFactor = scalingFactor,
        spacingDp = spacingDp,
        tint = tint,
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .drawWithContent {
                drawContent()
                val shader = ImageShader(tile, TileMode.Repeated, TileMode.Repeated)
                drawRect(brush = ShaderBrush(shader), alpha = PATTERN_ALPHA)
            }
    )
}

@Composable
fun CardGenre(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    text: String,
    enabled: Boolean = true,
    colors: CardGenreColors = CardGenreColors(
        containerColor = AutomotiveTheme.colorScheme.surfaceContainer,
        contentColor = AutomotiveTheme.colorScheme.onSurface
    ),
    iconNameOverlay: IconSetEnum = IconSetEnum.Droid,
    patternIcon: ImageVector = ImageVector.vectorResource(id = R.drawable.micro),
    interactionSource: MutableInteractionSource? = null,
) {
    val actualInteractionSource = interactionSource ?: remember { MutableInteractionSource() }
    val interactionState by rememberInteractionState(actualInteractionSource)

    val resolvedBorder = resolveBorderTreatment(interactionState)
    val alpha = resolveAlpha(enabled)

    Surface(
        modifier = modifier
            .alpha(alpha)
            .widthIn(min = CardGenreDefaults.containerMinWidth, max = CardGenreDefaults.containerWidth)
            .aspectRatio(CardGenreDefaults.aspectRatio),
        onClick = onClick,
        enabled = enabled,
        shape = RoundedCornerShape(AutomotiveTheme.measurement.shapes.extraLarge),
        color = colors.containerColor,
        contentColor = colors.contentColor,
        border = resolvedBorder,
        interactionSource = actualInteractionSource
    ) {
        Box(modifier = Modifier.fillMaxSize()) {

            PatternBackground(
                patternIcon = patternIcon,
                tint = colors.contentColor,
            )


            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(
                        horizontal = AutomotiveTheme.measurement.spaces.largeIncreased,
                        vertical = AutomotiveTheme.measurement.shapes.extraSmall
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = text,
                    color = AutomotiveTheme.colorScheme.onSurface,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    style = AutomotiveTheme.typography.body2,
                    textAlign = TextAlign.Center,
                )

                if (!enabled) {
                    OverlayWithIcon(name = iconNameOverlay)
                }
            }
        }
    }
}

@Preview(
    name = "CardGenre - Light Mode",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_NO or Configuration.UI_MODE_TYPE_CAR,
)

@Preview(
    name = "CardGenre - Dark Mode",
    showBackground = false,
    uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_CAR,
)

@Composable
fun CardGenrePreview() {
    AutomotiveTheme() {
        CardGenre(
            onClick = {},
            patternIcon = ImageVector.vectorResource(id = R.drawable.droid),
            text = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua."
        )
    }
}