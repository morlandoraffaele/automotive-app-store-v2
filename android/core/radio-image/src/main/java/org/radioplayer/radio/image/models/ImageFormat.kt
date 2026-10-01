package org.radioplayer.radio.image.models

/**
 * How a resolved image should be rendered.
 *
 * Mirrors radioplayer's `org.radioplayer.radio.image.models.ImageFormat`. The app store UI
 * only ever uses [Static]; [Gif] is kept so the sealed hierarchy matches upstream and stays
 * exhaustive for callers.
 */
sealed interface ImageFormat {

    /** Animated GIF. The app store has no animated artwork, so it renders as a static frame. */
    data object Gif : ImageFormat {
        override fun toString(): String = "Animated/Gif"
    }

    data object Static : ImageFormat {
        override fun toString(): String = "Static"
    }
}
