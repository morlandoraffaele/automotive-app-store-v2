package org.radioplayer.automotive.designsystem.components.composites.rail

import androidx.compose.runtime.Immutable

sealed class RailLayout {

    @Immutable
    object SingleRow : RailLayout()

    @Immutable
    data class MultiRow(
        val itemCount: Int,
        /**
         * Slot shape as width / height (1f = square, >1f = wide rectangle, e.g. the genre card
         * ratio 296f/164f). Drives the uniform slot height
         * (slotWidth / itemAspectRatio) of every grid cell, so items scale proportionally with
         * the available width while preserving their designed shape. Must match the ratio the
         * hosted card declares via its own `aspectRatio` modifier.
         */
        val itemAspectRatio: Float = 1f,
        /**
         * Overrides the automatic screen-height row bucket with an exact row count. Columns per
         * row are still computed at runtime from the available width. Since the row count is no
         * longer derived from available height, items beyond `maxRows * columnsPerRow` are not
         * rendered rather than being reachable by scrolling further.
         */
        val maxRows: Int? = null,
    ) : RailLayout() {
        init {
            require(itemAspectRatio > 0f) {
                "MultiRow.itemAspectRatio must be > 0, got $itemAspectRatio"
            }
            require(maxRows == null || maxRows > 0) {
                "MultiRow.maxRows must be > 0 when provided, got $maxRows"
            }
        }
    }
}

