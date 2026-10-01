package org.radioplayer.automotive.designsystem.components.composites.glow

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.radioplayer.automotive.designsystem.theme.AutomotiveTheme
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.exp

/** Gradient stops per blob. 12 already matches the render; 16 leaves margin. */
private const val ProfileStops = 16

/** Midpoint-rule samples per axis when integrating the blurred-disk profile. */
private const val ProfileSamples = 32

/**
 * The ambient glow behind the onboarding views — Figma: `Glow Container` in
 * `View / Onboarding / Page 1 - Dark Mode`, node `37691:40931`.
 *
 * Two blurred circles of [color] in the top-right of whatever it fills, and nothing else: it draws
 * **no background**, so it can sit over any surface. Put it first in a `Box` that owns the
 * background, with the content after it.
 *
 * ### How the blur is drawn
 *
 * Figma's `LAYER_BLUR` is a Gaussian blur of a hard disk. A real blur (`RenderEffect`,
 * `BlurMaskFilter`) is out on this hardware — see the README's AAOS rendering pitfalls — so each
 * blob is a radial gradient whose stops *are* the blurred disk's exact radial profile, integrated
 * once per size change. Measured against the export's preview render this is within 2.7/255 at the
 * worst pixel. The linear centre-to-edge gradient used by `EmptyState` would be 13–15 levels too
 * dark across most of the large blob here, which is why it is not reused.
 *
 * The container's opacity applies to both blobs as a group, and is reproduced with one save layer
 * (see [AmbientGlowDefaults.GroupOpacity]). That layer holds only gradients — no rounded-corner
 * fill and no blur mask — so the alpha-layer pitfall does not apply to it.
 *
 * ### Geometry
 *
 * The blobs keep their drawn size and are anchored the way the export constrains them (both to the
 * right; the large one to the bottom, the small one to the top), so on a wider or taller screen
 * the glow stays in the top-right rather than stretching.
 *
 * @param color the glow colour. The export hardcodes `#25D1DA` with no token
 *   ([AmbientGlowDefaults.ExportColor]); which colour a given screen uses is the caller's call.
 * @param modifier applied to the glow layer, which fills its parent by default.
 */
@Composable
fun AmbientGlow(
    color: Color,
    modifier: Modifier = Modifier,
) {
    val blurRadius = AmbientGlowDefaults.BlurRadius
    Box(
        modifier = modifier
            .fillMaxSize()
            .drawWithCache {
                val sigma = blurRadius.toPx() * AmbientGlowDefaults.BlurSigmaRatio
                val blobColor = color.copy(alpha = color.alpha * AmbientGlowDefaults.BlobOpacity)

                val largeRadius = AmbientGlowDefaults.LargeBlobSize.toPx() / 2f
                val large = blurredDiskBrush(
                    color = blobColor,
                    center = Offset(
                        x = size.width - AmbientGlowDefaults.LargeBlobEndInset.toPx() - largeRadius,
                        y = size.height - largeRadius,
                    ),
                    radius = largeRadius,
                    sigma = sigma,
                )

                val smallRadius = AmbientGlowDefaults.SmallBlobSize.toPx() / 2f
                val small = blurredDiskBrush(
                    color = blobColor,
                    center = Offset(x = size.width - smallRadius, y = smallRadius),
                    radius = smallRadius,
                    sigma = sigma,
                )

                val groupPaint = Paint().apply { alpha = AmbientGlowDefaults.GroupOpacity }
                val bounds = Rect(Offset.Zero, size)

                onDrawBehind {
                    drawIntoCanvas { canvas ->
                        canvas.saveLayer(bounds, groupPaint)
                        // Figma z-order: Blob 2 (large) under Blob 1 (small).
                        drawRect(brush = large)
                        drawRect(brush = small)
                        canvas.restore()
                    }
                }
            },
    )
}

/**
 * A radial gradient reproducing a disk of [radius] blurred by a Gaussian of [sigma], out to
 * `radius + 3σ`, where the coverage is below 0.2% and the last stop is transparent.
 */
private fun blurredDiskBrush(color: Color, center: Offset, radius: Float, sigma: Float): Brush {
    val extent = radius + 3f * sigma
    val stops = Array(ProfileStops + 1) { i ->
        val fraction = i.toFloat() / ProfileStops
        val coverage = if (i == ProfileStops) 0f else blurredDiskCoverage(extent * fraction, radius, sigma)
        fraction to color.copy(alpha = color.alpha * coverage)
    }
    return Brush.radialGradient(colorStops = stops, center = center, radius = extent)
}

/**
 * How much of a unit disk of [radius], blurred by a Gaussian of [sigma], covers a point at
 * [distance] from its centre: the Gaussian integrated over the disk, in polar coordinates around
 * the disk's centre (midpoint rule, half-plane by symmetry). Closed forms need a Bessel function;
 * this is ~1k `exp` calls per stop, once per size change.
 */
private fun blurredDiskCoverage(distance: Float, radius: Float, sigma: Float): Float {
    val dRho = radius / ProfileSamples
    val dTheta = PI.toFloat() / ProfileSamples
    val k = 1f / (2f * sigma * sigma)
    var sum = 0f
    for (i in 0 until ProfileSamples) {
        val rho = (i + 0.5f) * dRho
        for (j in 0 until ProfileSamples) {
            val theta = (j + 0.5f) * dTheta
            val d2 = distance * distance + rho * rho - 2f * distance * rho * cos(theta)
            sum += rho * exp(-d2 * k)
        }
    }
    // × 2 for the other half-plane, ÷ the Gaussian's normalisation 2πσ².
    return (sum * dRho * dTheta * 2f / (2f * PI.toFloat() * sigma * sigma)).coerceIn(0f, 1f)
}

@Preview(
    name = "AmbientGlow - Light Mode",
    widthDp = 999,
    heightDp = 600,
    uiMode = Configuration.UI_MODE_NIGHT_NO or Configuration.UI_MODE_TYPE_CAR,
)
@Preview(
    name = "AmbientGlow - Dark Mode",
    widthDp = 999,
    heightDp = 600,
    uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_CAR,
)
@Composable
private fun AmbientGlowPreview() {
    AutomotiveTheme {
        // The export's frame: 999×600 on sys/color/Surface. The surface belongs to the screen, not
        // to the glow.
        Box(Modifier.size(999.dp, 600.dp).background(AutomotiveTheme.colorScheme.surface)) {
            AmbientGlow(color = AmbientGlowDefaults.ExportColor)
        }
    }
}
