package org.radioplayer.automotive.designsystem.components.composites.glow

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import org.radioplayer.automotive.designsystem.theme.AutomotiveTheme

/**
 * Values for [AmbientGlow], read off the `Glow Container` frame of the Figma view
 * `View / Onboarding / Page 1 - Dark Mode` (node `37691:40931`), Blueprint export 3.18.
 */
object AmbientGlowDefaults {

    /**
     * The fill both blobs use in the export: `#25D1DA`, a hardcoded paint with **no** token binding
     * (the export's one unbound colour). It is the reason [AmbientGlow] takes its colour as a
     * parameter; this constant exists for previews and the gallery, not as a default.
     */
    val ExportColor: Color = Color(0xFF25D1DA)

    /** `Glow Blob 2`: 457×457, anchored bottom (gap 0) and right (gap 70). */
    val LargeBlobSize: Dp = 457.dp
    val LargeBlobEndInset: Dp = 70.dp

    /** `Glow Blob 1`: 266×266, anchored top (gap 0) and right (gap 0). */
    val SmallBlobSize: Dp = 266.dp

    /** Each blob's own node opacity in the export. */
    const val BlobOpacity: Float = 0.5f

    /**
     * The `Glow Container` frame's node opacity. It applies to the two blobs *as a group*, which is
     * not the same as drawing each at 0.25: where they overlap, the group renders up to 12/255
     * dimmer than independent draws. [AmbientGlow] reproduces it with one save layer.
     */
    const val GroupOpacity: Float = 0.5f

    /**
     * Figma `LAYER_BLUR` on both blobs, bound to `sys/effect/blur/ambient/medium` — 128dp, as drawn.
     * The export resolves the token to 96, which is the known uniform 0.75x staleness of Figma's
     * variable values (see the README); the name is right, the number is not.
     */
    val BlurRadius: Dp
        @Composable @ReadOnlyComposable
        get() = AutomotiveTheme.effects.blur.ambient.medium

    /**
     * Gaussian sigma as a fraction of Figma's blur radius. **Measured, not documented**: a sweep
     * against the export's 2x preview render fits best at 0.4 (RMSE 0.6/255 over the whole frame);
     * 0.5, the commonly quoted CSS convention, is visibly too soft (RMSE 1.3, worse at the edges).
     */
    const val BlurSigmaRatio: Float = 0.4f
}
