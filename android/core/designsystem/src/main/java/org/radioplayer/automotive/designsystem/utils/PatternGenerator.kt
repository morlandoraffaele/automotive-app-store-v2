package org.radioplayer.automotive.designsystem.utils

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.drawscope.CanvasDrawScope
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import android.graphics.Bitmap
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import kotlin.math.ceil

fun generateDotTile(
    density: Density,
    tileSizeDp: Float = 16f,
    dotRadiusDp: Float = 1f,
    dotColor: Color = Color.White
): ImageBitmap {
    val tileSizePx = with(density) { tileSizeDp.dp.toPx() }.let { ceil(it).toInt() }
    val dotRadiusPx = with(density) { dotRadiusDp.dp.toPx() }

    val bitmap = Bitmap.createBitmap(
        tileSizePx.coerceAtLeast(1),
        tileSizePx.coerceAtLeast(1),
        Bitmap.Config.ARGB_8888
    )
    val imageBitmap = bitmap.asImageBitmap()

    CanvasDrawScope().draw(
        density = density,
        layoutDirection = LayoutDirection.Ltr,
        canvas = Canvas(imageBitmap),
        size = Size(tileSizePx.toFloat(), tileSizePx.toFloat())
    ) {
        // Un solo dot centrato nel tile: ripetuto via TileMode.Repeated diventa una griglia regolare
        drawCircle(
            color = dotColor,
            radius = dotRadiusPx,
            center = Offset(tileSizePx / 2f, tileSizePx / 2f)
        )
    }
    return imageBitmap
}

fun generateGridTile(
    density: Density,
    tileSizeDp: Float = 16f,
    lineWidthDp: Float = 0.5f,
    lineColor: Color = Color.White
): ImageBitmap {
    val tileSizePx = with(density) { tileSizeDp.dp.toPx() }.let { ceil(it).toInt() }
    val lineWidthPx = with(density) { lineWidthDp.dp.toPx() }

    val bitmap = Bitmap.createBitmap(
        tileSizePx.coerceAtLeast(1),
        tileSizePx.coerceAtLeast(1),
        Bitmap.Config.ARGB_8888
    )
    val imageBitmap = bitmap.asImageBitmap()

    CanvasDrawScope().draw(
        density = density,
        layoutDirection = LayoutDirection.Ltr,
        canvas = Canvas(imageBitmap),
        size = Size(tileSizePx.toFloat(), tileSizePx.toFloat())
    ) {
        drawLine(
            color = lineColor,
            start = Offset(0f, 0f),
            end = Offset(tileSizePx.toFloat(), 0f),
            strokeWidth = lineWidthPx
        )
        drawLine(
            color = lineColor,
            start = Offset(0f, 0f),
            end = Offset(0f, tileSizePx.toFloat()),
            strokeWidth = lineWidthPx
        )
    }
    return imageBitmap
}


@Composable
fun rememberIconPatternTile(
    icon: ImageVector,
    baseIconSizeDp: Float = 24f,
    scalingFactor: Float = 1f,
    spacingDp: Offset = Offset(4f, 4f),
    tint: Color? = null
): ImageBitmap {
    val density = LocalDensity.current

    val colorFilter = tint?.let { ColorFilter.tint(it) }
    val painter = rememberVectorPainter(icon)

    return remember(icon, baseIconSizeDp, scalingFactor, spacingDp, tint, density) {
        val iconSizeDp = baseIconSizeDp * scalingFactor
        val tileWidthDp = iconSizeDp + spacingDp.x
        val tileHeightDp = iconSizeDp + spacingDp.y

        val tileWidthPx = with(density) { tileWidthDp.dp.toPx() }.let { ceil(it).toInt() }.coerceAtLeast(1)
        val tileHeightPx = with(density) { tileHeightDp.dp.toPx() }.let { ceil(it).toInt() }.coerceAtLeast(1)
        val iconSizePx = with(density) { iconSizeDp.dp.toPx() }

        val bitmap = Bitmap.createBitmap(tileWidthPx, tileHeightPx, Bitmap.Config.ARGB_8888)
        val imageBitmap = bitmap.asImageBitmap()

        CanvasDrawScope().draw(
            density = density,
            layoutDirection = LayoutDirection.Ltr,
            canvas = Canvas(imageBitmap),
            size = Size(tileWidthPx.toFloat(), tileHeightPx.toFloat())
        ) {
            translate (
                left = (tileWidthPx - iconSizePx) / 2f,
                top = (tileHeightPx - iconSizePx) / 2f
            ) {
                with(painter) {
                    draw(
                        size = Size(iconSizePx, iconSizePx),
                        colorFilter = colorFilter
                    )
                }
            }
        }
        imageBitmap
    }
}
