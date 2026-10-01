package org.radioplayer.radio.image.models

/**
 * Where the bytes of an [ImageRequest] come from.
 *
 * Mirrors radioplayer's `org.radioplayer.radio.image.models.ImageSource`, including the
 * self-describing [toString] overrides.
 */
sealed interface ImageSource {

    data class Remote(
        val url: String,
        val fallback: ImageRequest? = null
    ) : ImageSource {
        override fun toString(): String = "ImageSource.Remote/$url"
    }

    data class Resource(
        val id: Int
    ) : ImageSource {
        override fun toString(): String = "ImageSource.Resource/$id"
    }

    data class Asset(
        val path: String
    ) : ImageSource {
        override fun toString(): String = "ImageSource.Asset/$path"
    }
}
