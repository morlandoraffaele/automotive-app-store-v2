package org.radioplayer.radio.image.models

/**
 * Pixel dimensions requested for an image.
 *
 * Mirrors radioplayer's `org.radioplayer.radio.image.models.ImageSize`. The design system
 * only imports the type (a leftover unused import in `ContinueInApp.kt`), so it exists here
 * purely for signature parity; [RadioImage][org.radioplayer.radio.image.components.RadioImage]
 * sizes itself from the layout instead of from this value.
 */
data class ImageSize(
    val width: Int,
    val height: Int,
) {
    constructor(size: Int) : this(size, size)
}
