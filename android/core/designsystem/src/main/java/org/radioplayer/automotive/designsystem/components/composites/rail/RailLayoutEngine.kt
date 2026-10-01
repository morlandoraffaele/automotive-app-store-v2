package org.radioplayer.automotive.designsystem.components.composites.rail

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import org.radioplayer.automotive.designsystem.components.composites.rail.layouts.RailMultiRow
import org.radioplayer.automotive.designsystem.components.composites.rail.layouts.RailSingleRow


@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
internal fun RailLayoutEngine(
    modifier: Modifier,
    layout: RailLayout,
    drivingUxRestrictions: RailDrivingUxRestrictions,
    // state: RailState,
    itemsSpace: Dp,
    userScrollEnabled: Boolean,
    contentPadding: PaddingValues,
    content: RailScope.() -> Unit
) {

//    LaunchedEffect(layout) {
//        state.currentLayout = layout
//    }
    CompositionLocalProvider(LocalRailDrivingUxRestrictions provides drivingUxRestrictions) {
        BoxWithConstraints(modifier = modifier) {

            when (layout) {
                is RailLayout.SingleRow -> RailSingleRow(
                    modifier = Modifier.fillMaxWidth(),
                    // state = state,
                    itemsSpace = itemsSpace,
                    userScrollEnabled = userScrollEnabled,
                    containerWidth = maxWidth,
                    content = content
                )

                is RailLayout.MultiRow -> RailMultiRow(
                    modifier = modifier.fillMaxWidth(),
                    // state = state,
                    autoRows = currentRailHeightBucket().rowCount,
                    fixedRows = layout.maxRows,
                    itemCount = layout.itemCount,
                    itemAspectRatio = layout.itemAspectRatio,
                    itemsSpace = itemsSpace,
                    userScrollEnabled = userScrollEnabled,
                    containerWidth = maxWidth,
                    content = content
                )
            }
        }
    }

}