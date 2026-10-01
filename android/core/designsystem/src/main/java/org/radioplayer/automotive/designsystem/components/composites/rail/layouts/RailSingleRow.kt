package org.radioplayer.automotive.designsystem.components.composites.rail.layouts

import android.util.Log
import androidx.compose.foundation.gestures.ScrollableDefaults
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import org.radioplayer.automotive.designsystem.components.composites.rail.LocalRailDrivingUxRestrictions
import org.radioplayer.automotive.designsystem.components.composites.rail.RailDefaults
import org.radioplayer.automotive.designsystem.components.composites.rail.RailScope
import org.radioplayer.automotive.designsystem.components.composites.rail.RailState
import org.radioplayer.automotive.designsystem.components.composites.rail.gesture.rememberRailSnapFlingBehavior


@Composable
internal fun RailSingleRow(
    modifier: Modifier,
    //state: RailState,
    itemsSpace: Dp,
    userScrollEnabled: Boolean,
    containerWidth: Dp,
    content: RailScope.() -> Unit,
) {
    val uxRestrictions = LocalRailDrivingUxRestrictions.current
    val retrievedUserScrollEnabled = if(uxRestrictions.isRestricted) false else userScrollEnabled

    val (_,calculatedItemWidth) = RailDefaults.calculateLayoutInfo(containerWidth, itemsSpace)

    LazyRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(itemsSpace),
        userScrollEnabled = retrievedUserScrollEnabled
    ) {
        val railScope = RailListScopeImpl(this,calculatedItemWidth)
        railScope.content()
    }
}