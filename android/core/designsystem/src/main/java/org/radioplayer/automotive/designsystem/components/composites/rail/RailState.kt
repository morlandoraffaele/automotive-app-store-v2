package org.radioplayer.automotive.designsystem.components.composites.rail

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.radioplayer.automotive.designsystem.components.composites.rail.gesture.RailScrollProxy
import org.radioplayer.automotive.designsystem.components.composites.rail.gesture.asProxy

class RailState constructor(
    val listState: LazyListState,
    val gridState: LazyGridState,
    private val scope: CoroutineScope
) {

    internal var currentLayout: RailLayout by mutableStateOf(RailLayout.SingleRow)


    private val currentProxy: RailScrollProxy by derivedStateOf {
        when (currentLayout) {
            is RailLayout.SingleRow -> listState.asProxy()
            is RailLayout.MultiRow -> gridState.asProxy()
        }
    }

    val firstVisibleItemIndex: Int by derivedStateOf { currentProxy.firstVisibleItemIndex }
    val totalItemCount: Int by derivedStateOf { currentProxy.totalItemsCount }
    // val canScrollBackward: Boolean by derivedStateOf { currentProxy.canScrollBackward }
    // val canScrollForward: Boolean by derivedStateOf { currentProxy.canScrollForward }

    // ── Metodi di Navigazione Programmatica (utilizzati dalle Frecce) ────────

    fun scrollForward() {
        scope.launch {
            val next = (currentProxy.firstVisibleItemIndex + 4).coerceAtMost(currentProxy.totalItemsCount - 1)
            currentProxy.animateScrollToItem(next)
        }
    }

    fun scrollBackward() {
        scope.launch {
            val prev = (currentProxy.firstVisibleItemIndex - 4).coerceAtLeast(0)
            currentProxy.animateScrollToItem(prev)
        }
    }

}

@Composable
fun rememberRailState(
    initialIndex: Int = 0,
    initialScrollOffset: Int = 0,
): RailState {

    val listState = rememberLazyListState(
        initialFirstVisibleItemIndex = initialIndex,
        initialFirstVisibleItemScrollOffset = initialScrollOffset,
    )
    val gridState = rememberLazyGridState(
        initialFirstVisibleItemIndex = initialIndex,
        initialFirstVisibleItemScrollOffset = initialScrollOffset,
    )
    val scope = rememberCoroutineScope()

    return remember(listState, gridState, scope) {
        RailState(
            listState = listState,
            gridState = gridState,
            scope = scope
        )
    }
}