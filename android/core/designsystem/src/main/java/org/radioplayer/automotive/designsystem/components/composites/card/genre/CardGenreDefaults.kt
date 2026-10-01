package org.radioplayer.automotive.designsystem.components.composites.card.genre

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import org.radioplayer.automotive.designsystem.subsystems.Sizes

object CardGenreDefaults {
    val containerWidth: Dp = 296.dp
    val containerHeight: Dp = 164.dp
    val containerMinWidth: Dp = Sizes().minContentCell

    /**
     * Canonical shape of the genre card as width / height (~1.8:1 wide rectangle).
     *
     * The card sizes itself with [androidx.compose.foundation.layout.aspectRatio] instead of
     * hardcoded dimensions so it scales proportionally with whatever width its container
     * (e.g. a rail slot) gives it. Rails hosting genre cards pass this same value as
     * [org.radioplayer.automotive.designsystem.components.composites.rail.RailLayout.MultiRow.itemAspectRatio]
     * so multi-row grids reserve rows of exactly the right height.
     */
    const val aspectRatio: Float = 296f / 164f
}