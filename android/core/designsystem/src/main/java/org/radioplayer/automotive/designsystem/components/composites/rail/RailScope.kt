package org.radioplayer.automotive.designsystem.components.composites.rail

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.Dp

interface RailScope {
    fun item(key: Any? = null, content: @Composable () -> Unit)

    fun <T> items(
        items: List<T>,
        key: ((item: T) -> Any)? = null,
        itemContent: @Composable (item: T) -> Unit
    )

    val dynamicItemWidth: Dp
}