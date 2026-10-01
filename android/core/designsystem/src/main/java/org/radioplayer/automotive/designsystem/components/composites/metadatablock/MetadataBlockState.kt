package org.radioplayer.automotive.designsystem.components.composites.metadatablock

import org.radioplayer.radio.image.models.ImageRequest

sealed interface MetadataBlockState {
    val artwork: ImageRequest

    data class Radio(
        val badges: List<String> = emptyList(),
        val headerText: String? = null,
        val title: String,
        val subtitle: String? = null,
        override val artwork: ImageRequest,
    ) : MetadataBlockState

    data class Podcast(
        val seriesName: String? = null,
        val episodeTitle: String,
        val releaseDate: String,
        override val artwork: ImageRequest,
    ) : MetadataBlockState
}

