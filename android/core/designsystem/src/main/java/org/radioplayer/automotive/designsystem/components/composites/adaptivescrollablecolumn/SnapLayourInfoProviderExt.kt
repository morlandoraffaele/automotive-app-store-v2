package org.radioplayer.automotive.designsystem.components.composites.adaptivescrollablecolumn

import androidx.compose.foundation.gestures.snapping.SnapLayoutInfoProvider
import androidx.compose.foundation.lazy.LazyListState

internal fun SnapLayoutInfoProvider.calculateSnapMultipleItems(
    state: LazyListState,
    baseProvider: SnapLayoutInfoProvider,
    velocity: Float
): Float {
    val layoutInfo = state.layoutInfo
    val visibleItems = layoutInfo.visibleItemsInfo

    if (visibleItems.isEmpty()) {
        return baseProvider.calculateSnapOffset(velocity)
    }

    val firstVisibleItem = visibleItems.first()

    val itemHeight = firstVisibleItem.size
    val spacing = layoutInfo.mainAxisItemSpacing
    val totalSlotSize = itemHeight + spacing

    val viewportHeight = layoutInfo.viewportSize.height
    val itemsToScroll = (viewportHeight / totalSlotSize).coerceAtLeast(1)

    val targetIndex = if (velocity > 0) {
        (firstVisibleItem.index + itemsToScroll).coerceAtMost(layoutInfo.totalItemsCount - 1)
    } else {
        (firstVisibleItem.index - itemsToScroll).coerceAtLeast(0)
    }

    val indexDelta = targetIndex - firstVisibleItem.index

    val calculatedOffset = (indexDelta * totalSlotSize) + firstVisibleItem.offset

    return calculatedOffset.toFloat()
}