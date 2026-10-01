package org.radioplayer.automotive.designsystem.components.composites.rail.gesture

import androidx.compose.foundation.gestures.FlingBehavior
import androidx.compose.foundation.gestures.ScrollScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import kotlin.math.sign


class RailSnapFlingBehavior constructor(
    private val scrollProxy: RailScrollProxy,
    // TODO: Use a default object for this value
    private val itemsPerPage: Int = 4
): FlingBehavior {

    override suspend fun ScrollScope.performFling(initialVelocity: Float): Float {
        if (scrollProxy.totalItemsCount == 0) return initialVelocity

        val currentIndex = scrollProxy.firstVisibleItemIndex
        val currentPage = currentIndex / itemsPerPage

        val targetPage = when {
            initialVelocity < -200f -> currentPage + 1
            initialVelocity > 200f -> {
                if (currentIndex % itemsPerPage == 0) currentPage - 1 else currentPage
            }
            else -> {
                currentPage
            }
        }

        val targetIndex = (targetPage * itemsPerPage).coerceIn(0, scrollProxy.totalItemsCount - 1)

        scrollProxy.animateScrollToItem(targetIndex)

        return 0f
    }
}

// Overload per LazyRow (SingleRow)
@Composable
internal fun rememberRailSnapFlingBehavior(listState: LazyListState, itemsPerPage: Int = 4): FlingBehavior {
    return remember(listState, itemsPerPage) {
        RailSnapFlingBehavior(scrollProxy = listState.asProxy(), itemsPerPage = itemsPerPage)
    }
}

// Overload per LazyHorizontalGrid (MultiRow)
@Composable
internal fun rememberRailSnapFlingBehavior(gridState: LazyGridState, itemsPerPage: Int = 4): FlingBehavior {
    return remember(gridState, itemsPerPage) {
        RailSnapFlingBehavior(scrollProxy = gridState.asProxy(), itemsPerPage = itemsPerPage)
    }
}