package org.radioplayer.automotive.designsystem.components.composites.emptystate

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import org.radioplayer.automotive.designsystem.components.composites.button.tonal.ButtonTonal
import org.radioplayer.automotive.designsystem.components.primitives.Text
import org.radioplayer.automotive.designsystem.components.primitives.icon.Icon
import org.radioplayer.automotive.designsystem.components.primitives.icon.IconSource
import org.radioplayer.automotive.designsystem.R
import org.radioplayer.automotive.designsystem.components.composites.button.tonal.ButtonTonalShape
import org.radioplayer.automotive.designsystem.components.primitives.icon.IconSetEnum
import org.radioplayer.automotive.designsystem.theme.AutomotiveTheme

@Composable
fun EmptyState(
    icon: @Composable () -> Unit,
    patternIcon: ImageVector,
    title: String,
    description: String,
    modifier: Modifier = Modifier,
    buttonIcon: @Composable (() -> Unit)? = null,
    onButtonClick: () -> Unit = {},
    buttonLabel: String? = null,
    patternZoom: Float = EmptyStateDefaults.DefaultPatternZoom,
    patternSpacing: Dp = EmptyStateDefaults.DefaultPatternSpacing,
    glowOpacity: Float = EmptyStateDefaults.GlowOpacity,
    colors: EmptyStateColors = EmptyStateDefaults.colors()
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            // The component's own height in the export - the content frame plus its padding - not
            // the content frame alone, which left it 64dp shorter than the design.
            .defaultMinSize(minHeight = EmptyStateDefaults.MinHeight)
            .clip(RoundedCornerShape(EmptyStateDefaults.CornerRadius))
            .background(colors.container)
            .drawWithCache {
                val baseRadius = EmptyStateDefaults.GlowSize.toPx() / 2f
                val center = Offset(size.width - baseRadius, size.height / 2f)
                val brush = Brush.radialGradient(
                    colors = listOf(
                        colors.glow.copy(alpha = glowOpacity),
                        colors.glow.copy(alpha = 0f)
                    ),
                    center = center,
                    radius = baseRadius + EmptyStateDefaults.GlowBlurRadius.toPx()
                )
                onDrawBehind { drawRect(brush = brush) }
            },
    ) {

        IconGridPattern(
            icon = patternIcon,
            tint = colors.patternTint,
            zoom = patternZoom,
            spacing = patternSpacing,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(end = EmptyStateDefaults.PatternBoxEndInset)
                .size(EmptyStateDefaults.PatternBoxSize)
        )

        Column(
            modifier = Modifier
                // Padding first, then width: the export's Content frame is 500dp wide *inside* the
                // root's 32dp padding, not 500dp including it. The other order narrowed every line
                // of text by 64dp and wrapped the description early.
                .align(Alignment.CenterStart)
                .padding(EmptyStateDefaults.ContentPadding)
                .width(EmptyStateDefaults.ContentWidth),
            verticalArrangement = Arrangement.spacedBy(EmptyStateDefaults.ContentGap)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(EmptyStateDefaults.HeaderGap),
                // The export's Header Container aligns MIN/MIN: a title that wraps to two lines
                // keeps its icon beside the first line rather than floating to the middle.
                verticalAlignment = Alignment.Top
            ) {
                CompositionLocalProvider(LocalContentColor provides colors.iconTint) {
                    icon()
                }
                Text(
                    text = title,
                    modifier = Modifier.weight(1f),
                    color = colors.title,
                    style = EmptyStateDefaults.TitleStyle,
                    maxLines = 2,
                )
            }

            Text(
                text = description,
                modifier = Modifier.fillMaxWidth(),
                color = colors.description,
                style = EmptyStateDefaults.DescriptionStyle,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )

            buttonLabel?.let {
                ButtonTonal(
                    onClick = onButtonClick,
                    label = it,
                    icon = buttonIcon,
                    shapeVariant = ButtonTonalShape.Square
                )
            }
        }
    }
}


@Preview(
    name = "Empty State Standard - Light Mode",
    widthDp = 900,
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_NO or Configuration.UI_MODE_TYPE_CAR,
)
@Preview(
    name = "Empty State Standard - Dark Mode",
    widthDp = 900,
    showBackground = false,
    uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_CAR,
)
@Composable
private fun EmptyStateStandardPreview() {
    AutomotiveTheme {
        // The export's own icon in both slots: it resolves to the `adb` asset, which is this
        // module's `droid`. Still the generic placeholder as of 3.15 - see the README's open items
        // - but it is what the design actually specifies, so it is what the preview shows.
        EmptyState(
            icon = {
                Icon(
                    name = IconSetEnum.Droid,
                    contentDescription = null,
                    size = EmptyStateDefaults.HeaderIconSize
                )
            },
            patternIcon = EmptyStateDefaults.PatternSquareIcon,
            title = "Title",
            // The export's own copy, which doubles as the content rule for this component.
            description = "Content spans multiple lines (max 2 lines and 120 characters)",
            buttonLabel = "Label",
            buttonIcon = {
                Icon(name = IconSetEnum.Droid, contentDescription = null)
            },
            onButtonClick = {},
            // Figma's own scalingFactor for this variant; spacing and glow opacity are the
            // component defaults (1dp, 0.8), which are also this variant's values.
            patternZoom = EmptyStateDefaults.DefaultPatternZoom,
            colors = EmptyStateDefaults.colors(glow = Color(0xFF2558DA)),
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(
    name = "Empty State Favorites - Light Mode",
    widthDp = 900,
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_NO or Configuration.UI_MODE_TYPE_CAR,
)
@Preview(
    name = "Empty State Favorites - Dark Mode",
    widthDp = 900,
    showBackground = false,
    uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_CAR,
)
@Composable
private fun EmptyStateFavoritesPreview() {
    AutomotiveTheme {
        // Same header/button icon as Standard - the export uses the placeholder `adb` in both.
        // Only the *pattern* differs, and there it is a real heart.
        val favorite = ImageVector.vectorResource(IconSetEnum.Favorite.resId)
        Column(
            modifier = Modifier.padding(AutomotiveTheme.measurement.spaces.large)
        ) {
            EmptyState(
                icon = {
                    Icon(
                        name = IconSetEnum.Droid,
                        contentDescription = null,
                        size = EmptyStateDefaults.HeaderIconSize
                    )
                },
                patternIcon = favorite,
                title = "Title",
                description = "Content spans multiple lines (max 2 lines and 120 characters)",
                buttonIcon = { Icon(name = IconSetEnum.Droid) },
                buttonLabel = "Label",
                onButtonClick = {},
                // This variant differs from Standard in exactly two values: a larger scalingFactor
                // and a glow at full opacity. Everything else, spacing included, is shared.
                patternZoom = 1.2f,
                glowOpacity = 1f,
                colors = EmptyStateDefaults.colors(glow = Color(0xFFE90E0E)),
            )
        }
    }
}

