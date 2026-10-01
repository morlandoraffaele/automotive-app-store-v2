package org.radioplayer.automotive.designsystem.components.composites.rail.layouts

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.SubcomposeLayout
import androidx.compose.ui.unit.Dp
import org.radioplayer.automotive.designsystem.components.composites.rail.LocalRailDrivingUxRestrictions
import org.radioplayer.automotive.designsystem.components.composites.rail.RailDefaults
import org.radioplayer.automotive.designsystem.components.composites.rail.RailScope

/**
 * Eagerly collects every item registered on a [RailScope], in registration order, without
 * emitting anything into a layout. RailMultiRow needs the full ordered list upfront to remap
 * the flat list into row-major reading order before handing it to the underlying column-major
 * LazyHorizontalGrid.
 */
private class RailOrderedItemsCollector : RailScope {
    class Entry(val key: Any?, val content: @Composable () -> Unit)

    val entries = mutableListOf<Entry>()

    override fun item(key: Any?, content: @Composable () -> Unit) {
        entries += Entry(key, content)
    }

    override fun <T> items(
        items: List<T>,
        key: ((item: T) -> Any)?,
        itemContent: @Composable (item: T) -> Unit
    ) {
        items.forEach { item ->
            entries += Entry(key?.invoke(item)) { itemContent(item) }
        }
    }

    override val dynamicItemWidth: Dp = Dp.Unspecified
}

private enum class RailMultiRowSlot { Content }

@Composable
internal fun RailMultiRow(
    modifier: Modifier,
//  state: RailState,
    autoRows: Int,
    fixedRows: Int?,
    itemCount: Int,
    /**
     * Slot shape as width / height (1f = square, >1f = wide rectangle). The rail derives a
     * uniform, fully-fixed cell (width = calculatedItemWidth, height = width / ratio) so every
     * row has the same height and every item keeps its proportion at any screen size. Cards
     * placed in this layout must honor the fixed slot (aspectRatio / fillMaxSize based sizing).
     */
    itemAspectRatio: Float,
    itemsSpace: Dp,
    userScrollEnabled: Boolean,
    containerWidth: Dp,
    content: RailScope.() -> Unit,
) {

    val uxRestrictions = LocalRailDrivingUxRestrictions.current
    val retrievedUserScrollEnabled = if (uxRestrictions.isRestricted) false else userScrollEnabled

    val (columnsPerRow, calculatedItemWidth) = RailDefaults.calculateLayoutInfo(containerWidth, itemsSpace)
    val effectiveRows = RailDefaults.calculateEffectiveRows(
        maxRows = fixedRows ?: autoRows,
        itemCount = itemCount,
        columnsPerRow = columnsPerRow
    )
    // A caller-declared fixed row count means "show only what fits" - items beyond capacity
    // are dropped rather than staying reachable by scrolling. The automatic (screen-height
    // bucket) row count keeps today's behavior of not truncating anything.
    val maxVisibleItemCount = fixedRows?.let { effectiveRows * columnsPerRow }

    // Uniform slot height derived from the declared item shape: every cell in the grid gets the
    // same fixed box (width x width/ratio), so items scale proportionally with the container
    // width while preserving their shape. This replaces the previous subcompose "probe" pass,
    // which measured the first item with unbounded constraints and therefore couldn't resolve
    // aspect-ratio-driven heights reliably.
    val slotHeight = calculatedItemWidth / itemAspectRatio

    val collector = RailOrderedItemsCollector()
    collector.content()
    val visibleEntries = maxVisibleItemCount
        ?.let { collector.entries.take(it) }
        ?: collector.entries

    val slots = RailDefaults.buildRowMajorSlots(
        itemCount = visibleEntries.size,
        rows = effectiveRows,
        columnsPerRow = columnsPerRow
    )

    SubcomposeLayout(modifier = modifier) { constraints ->
        // Content height is exact (uniform fixed cells), so no floor is applied: the previous
        // minRailHeight coerce inflated the grid with phantom space that LazyHorizontalGrid
        // then distributed across rows, visually enlarging the vertical gaps between items.
        val gridHeight = RailDefaults
            .calculateMultiRowHeight(effectiveRows, slotHeight, itemsSpace)

        val gridPlaceable = subcompose(RailMultiRowSlot.Content) {
            LazyHorizontalGrid(
                rows = GridCells.Fixed(effectiveRows),
                modifier = Modifier.height(gridHeight),
                horizontalArrangement = Arrangement.spacedBy(itemsSpace),
                verticalArrangement = Arrangement.spacedBy(itemsSpace),
                userScrollEnabled = retrievedUserScrollEnabled
            ) {
                items(
                    count = slots.size,
                    key = { index ->
                        slots[index]?.let { originalIndex -> visibleEntries[originalIndex].key ?: originalIndex }
                            ?: "rail-empty-$index"
                    }
                ) { index ->
                    val originalIndex = slots[index]
                    if (originalIndex != null) {
                        RailItemSlot(
                            width = calculatedItemWidth,
                            height = slotHeight,
                            content = visibleEntries[originalIndex].content
                        )
                    } else {
                        Spacer(modifier = Modifier.size(calculatedItemWidth, slotHeight))
                    }
                }
            }
        }.first().measure(constraints)

        layout(gridPlaceable.width, gridPlaceable.height) {
            gridPlaceable.placeRelative(0, 0)
        }
    }
}
