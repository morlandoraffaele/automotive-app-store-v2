package org.radioplayer.automotive.designsystem.components.composites.rail.layouts

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import org.radioplayer.automotive.designsystem.components.composites.rail.RailScope

@Composable
internal fun RailItemSlot(
    width: Dp,
    height: Dp? = null,
    content: @Composable () -> Unit
) {
    // When [height] is provided the slot is a fully fixed box: items are clipped to the exact
    // cell the rail computed (width / itemAspectRatio), guaranteeing uniform row heights in
    // multi-row layouts. When it's null the height stays intrinsic, so each card's own
    // aspectRatio modifier derives its height proportionally from the slot width.
    val sizeModifier = if (height != null) {
        Modifier.size(width = width, height = height)
    } else {
        Modifier.width(width)
    }
    Box(modifier = sizeModifier, propagateMinConstraints = true) {
        content()
    }
}

internal class RailListScopeImpl(
    private val lazyListScope: LazyListScope,
    override val dynamicItemWidth: Dp,
) : RailScope {
    override fun item(key: Any?, content: @Composable () -> Unit) {
        lazyListScope.item(key = key) { RailItemSlot(width = dynamicItemWidth, content = content) }
    }

    override fun <T> items(items: List<T>, key: ((item: T) -> Any)?, itemContent: @Composable (item: T) -> Unit) {
        lazyListScope.items(
            count = items.size,
            key = key?.let { k -> { index -> k(items[index]) } }
        ) { index ->
            RailItemSlot(dynamicItemWidth) { itemContent(items[index]) }
        }
    }
}