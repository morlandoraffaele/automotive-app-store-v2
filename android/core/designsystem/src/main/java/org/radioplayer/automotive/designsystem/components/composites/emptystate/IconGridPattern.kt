package org.radioplayer.automotive.designsystem.components.composites.emptystate

import androidx.compose.foundation.Canvas
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.unit.Dp
import org.radioplayer.automotive.designsystem.theme.AutomotiveTheme

/**
 * Tiles [icon] across [modifier]'s bounds — Figma's "Pattern fill" (`sourceNodeId` +
 * `scalingFactor` + `spacing`) reimplemented from scratch, since Compose has no equivalent
 * fill type.
 *
 * The distinction that makes it match: Figma tiles the **source node**, not the glyph. The source
 * node is a [tileSize] box with a [baseIconSize] icon inside it, `scalingFactor` ([zoom]) scales
 * the box, and `spacing` is the gap between boxes — so the grid steps by `tileSize * zoom +
 * spacing` while the glyph itself is only `baseIconSize * zoom`. Scaling the glyph alone and
 * treating `spacing` as the whole gap (as this did first) needs a different, made-up spacing per
 * variant to look right, and still drifts the moment the zoom changes.
 *
 * The tiles are drawn into an offscreen compositing layer ([CompositingStrategy.Offscreen]) so
 * the trailing `BlendMode.DstIn` radial gradient — white (opaque) at the tile grid's center
 * fading to transparent at its edges — only masks *this* layer's own pixels, exactly
 * reproducing Figma's "Mask Overlay" radial-gradient mask over the "Pattern" layer.
 */
@Composable
fun IconGridPattern(
    icon: ImageVector,
    modifier: Modifier = Modifier,
    tint: Color = AutomotiveTheme.colorScheme.onSurface,
    zoom: Float = EmptyStateDefaults.DefaultPatternZoom,
    spacing: Dp = EmptyStateDefaults.DefaultPatternSpacing,
    baseIconSize: Dp = EmptyStateDefaults.PatternBaseIconSize,
    tileSize: Dp = EmptyStateDefaults.PatternTileSize
) {
    val painter = rememberVectorPainter(image = icon)
    val colorFilter = ColorFilter.tint(tint)

    Canvas(
        modifier = modifier.graphicsLayer { compositingStrategy = CompositingStrategy.Offscreen }
    ) {
        val tile = tileSize.toPx() * zoom
        val glyph = baseIconSize.toPx() * zoom
        val glyphInset = (tile - glyph) / 2f
        val step = tile + spacing.toPx()
        if (glyph > 0f && step > 0f) {
            var y = 0f
            while (y < size.height) {
                var x = 0f
                while (x < size.width) {
                    translate(left = x + glyphInset, top = y + glyphInset) {
                        with(painter) { draw(size = Size(glyph, glyph), colorFilter = colorFilter) }
                    }
                    x += step
                }
                y += step
            }
        }

        drawRect(
            brush = Brush.radialGradient(
                colors = listOf(Color.White, Color.Transparent),
                center = Offset(size.width / 2f, size.height / 2f),
                radius = (size.minDimension / 2f).coerceAtLeast(1f)
            ),
            blendMode = BlendMode.DstIn
        )
    }
}
