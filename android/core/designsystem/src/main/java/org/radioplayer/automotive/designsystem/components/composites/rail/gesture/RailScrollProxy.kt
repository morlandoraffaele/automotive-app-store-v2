package org.radioplayer.automotive.designsystem.components.composites.rail.gesture

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.grid.LazyGridState

interface RailScrollProxy {
    val firstVisibleItemIndex: Int
    val totalItemsCount: Int
    suspend fun animateScrollToItem(index: Int)
}

internal fun LazyListState.asProxy() = object : RailScrollProxy {
    override val firstVisibleItemIndex: Int get() = this@asProxy.firstVisibleItemIndex
    override val totalItemsCount: Int get() = this@asProxy.layoutInfo.totalItemsCount
    override suspend fun animateScrollToItem(index: Int) = this@asProxy.animateScrollToItem(index)
}

internal fun LazyGridState.asProxy() = object : RailScrollProxy {
    override val firstVisibleItemIndex: Int get() = this@asProxy.firstVisibleItemIndex
    override val totalItemsCount: Int get() = this@asProxy.layoutInfo.totalItemsCount
    override suspend fun animateScrollToItem(index: Int) = this@asProxy.animateScrollToItem(index)
}