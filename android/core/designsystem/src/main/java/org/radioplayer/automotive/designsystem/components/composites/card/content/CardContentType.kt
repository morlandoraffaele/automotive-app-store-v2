package org.radioplayer.automotive.designsystem.components.composites.card.content

import org.radioplayer.automotive.designsystem.components.primitives.icon.IconSetEnum

sealed interface CardContentType {
    data class Station(
        val artwork: String,
        val title: String,
        val iconName: IconSetEnum,
    ) : CardContentType

    data class Podcast(
        val artwork: String,
        val title: String,
        val iconName: IconSetEnum,
    ) : CardContentType

    data class Episode(
        val artwork: String,
        val title: String,
        val iconName: IconSetEnum,
    ) : CardContentType

    data class LocalStation(
        val artwork: String,
        val iconName: IconSetEnum,
    ) : CardContentType
}