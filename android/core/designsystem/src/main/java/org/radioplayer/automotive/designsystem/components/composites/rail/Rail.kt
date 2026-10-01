package org.radioplayer.automotive.designsystem.components.composites.rail

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp

@Composable
fun Rail(
    modifier: Modifier,
    layout: RailLayout,
    drivingUxRestrictions: RailDrivingUxRestrictions,
    // state: RailState = rememberRailState(),
    itemsSpace: Dp =  RailDefaults.itemsSpace,
    userScrollEnabled: Boolean = RailDefaults.userScrollEnabled, // TEMPORARY SET TO TRUE AS DEFAULT
    contentPadding: PaddingValues = RailDefaults.contentPadding,
    content: RailScope.() -> Unit
) {
    RailLayoutEngine(
        modifier = modifier,
        layout = layout,
        drivingUxRestrictions = drivingUxRestrictions,
        // state = state,
        itemsSpace = itemsSpace,
        userScrollEnabled = userScrollEnabled,
        contentPadding = contentPadding,
        content = content
    )
}

