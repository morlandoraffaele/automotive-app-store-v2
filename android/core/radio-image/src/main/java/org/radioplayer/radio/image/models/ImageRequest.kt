package org.radioplayer.radio.image.models

/**
 * Describes an image to load and how it should be rendered.
 *
 * Mirrors radioplayer's `org.radioplayer.radio.image.models.ImageRequest`; the design system
 * constructs these directly in previews, e.g.
 * `ImageRequest(source = ImageSource.Resource(R.drawable.placeholder_image), format = ImageFormat.Static)`.
 */
data class ImageRequest(
    val source: ImageSource,
    val format: ImageFormat
)
