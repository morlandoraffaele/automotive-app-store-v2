package org.radioplayer.automotive.designsystem.components.composites.rail

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import org.radioplayer.automotive.designsystem.subsystems.Spaces
import org.radioplayer.automotive.designsystem.tokens.SpaceTokens
import kotlin.math.ceil

internal enum class RailHeightBucket(val rowCount: Int) {
    COMPACT(2),
    STANDARD(3),
    LARGE(4)
}

internal object RailDefaults {

    val contentPadding = PaddingValues()

    val userScrollEnabled = true

    val itemsSpace = SpaceTokens.P4

    // TODO: Hardcoded value should be get from Design Tokens
    val minTargetWidth = 158.dp

    val minRailHeight = 400.dp

    // Sane ceiling so items don't shrink to near-nothing on ultra-wide displays;
    // the real per-row count is still driven continuously by containerWidth below.
    val maxAllowedItemsPerRow = 10

    val drivingUxRestrictions = RailDrivingUxRestrictions(
        isRestricted = false,
        limitContent = false,
        maxCumulativeContentItems = null,
        limitStringLength = false,
        maxStringLength = null
    )

    fun calculateLayoutInfo(
        containerWidth: Dp,
        itemSpacing: Dp,
        targetItemWidth: Dp = RailDefaults.minTargetWidth,
        maxAllowedItems: Int = maxAllowedItemsPerRow
    ): RailLayoutInfo {
        if (containerWidth <= 0.dp || targetItemWidth <= 0.dp) {
            return RailLayoutInfo(itemsCount = 0, itemWidth = 0.dp)
        }

        val totalAvailable = containerWidth + itemSpacing
        val singleItemSlot = targetItemWidth + itemSpacing
        val physicalFittingItems = (totalAvailable / singleItemSlot).toInt()

        val finalItemsCount = maxOf(1, minOf(physicalFittingItems, maxAllowedItems))

        val totalSpacingSpace = itemSpacing * (finalItemsCount - 1)

        val calculatedItemWidth = (containerWidth - totalSpacingSpace) / finalItemsCount

        return RailLayoutInfo(
            itemsCount = finalItemsCount,
            itemWidth = calculatedItemWidth
        )
    }


    fun calculateMultiRowHeight(rows: Int, itemHeight: Dp, itemsSpace: Dp): Dp =
        (itemHeight * rows) + (itemsSpace * (rows - 1))

    /**
     * Clamps [maxRows] (the row-count bucket for the current screen) down to however many rows
     * are actually needed to hold [itemCount] items at [columnsPerRow] items per row. Without this,
     * a short carousel (e.g. 2 items) would still be laid out into the bucket's full row count,
     * stacking each item into its own near-empty row instead of filling a single row.
     */
    fun calculateEffectiveRows(maxRows: Int, itemCount: Int, columnsPerRow: Int): Int {
        if (itemCount <= 0 || columnsPerRow <= 0) return maxRows
        val neededRows = ceil(itemCount / columnsPerRow.toFloat()).toInt()
        return neededRows.coerceIn(1, maxRows)
    }

    /**
     * Maps a flat, row-major-ordered sequence of [itemCount] items onto the slot order that a
     * `LazyHorizontalGrid` using `GridCells.Fixed(rows)` actually places items in.
     *
     * That grid fills column-major - down a column fully, then the next column to the right - so
     * fed unmodified, item `i` lands at `(row = i % rows, col = i / rows)`, which spreads a short
     * trailing row thinly across every column instead of filling the first few columns fully
     * (e.g. 13 items in 3 rows would render 5/4/4 per row instead of 6/6/1). This instead computes
     * the slot sequence such that feeding it back into that same column-major grid reproduces true
     * row-major reading order: row 0 filled left-to-right first, then row 1, and so on, starting a
     * fresh block of [columnsPerRow] columns every `rows * columnsPerRow` items.
     *
     * @return a list sized to the grid's required physical slot count (trailing all-empty slots
     * trimmed off), where each element is either the original item index to render at that grid
     * slot, or null for an empty filler cell.
     */
    fun buildRowMajorSlots(itemCount: Int, rows: Int, columnsPerRow: Int): List<Int?> {
        if (itemCount <= 0 || rows <= 0 || columnsPerRow <= 0) return emptyList()

        val blockSize = rows * columnsPerRow
        val blockCount = ceil(itemCount / blockSize.toFloat()).toInt()
        val physicalColumns = blockCount * columnsPerRow
        val slots = arrayOfNulls<Int>(physicalColumns * rows)

        for (physicalColumn in 0 until physicalColumns) {
            val block = physicalColumn / columnsPerRow
            val columnInBlock = physicalColumn % columnsPerRow
            for (row in 0 until rows) {
                val originalIndex = block * blockSize + row * columnsPerRow + columnInBlock
                if (originalIndex < itemCount) {
                    slots[physicalColumn * rows + row] = originalIndex
                }
            }
        }

        val lastFilledSlot = slots.indexOfLast { it != null }
        return if (lastFilledSlot < 0) emptyList() else slots.toList().subList(0, lastFilledSlot + 1)
    }
}

/**
 * Classifies the real device screen height into a row-count bucket for [RailLayout.MultiRow].
 *
 * A MultiRow rail is typically hosted inside a vertically scrolling column, where the local
 * layout constraints are unbounded (that's what lets the page scroll) - so the number of rows
 * that fit can't be derived from local BoxWithConstraints measurements. Instead this reads the
 * actual screen size, the same way [currentWidthBucket]-style utilities do for width, so a
 * compact screen shows fewer rows and a taller screen shows more, without any caller declaring
 * a fixed row count.
 */
@Composable
internal fun currentRailHeightBucket(): RailHeightBucket {
    val containerHeightPx = LocalWindowInfo.current.containerSize.height
    val density = LocalDensity.current
    val screenHeightDp = with(density) { containerHeightPx.toDp().value.toInt() }

    return when {
        screenHeightDp < 600 -> RailHeightBucket.COMPACT
        screenHeightDp < 900 -> RailHeightBucket.STANDARD
        else -> RailHeightBucket.LARGE
    }
}