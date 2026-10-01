package org.radioplayer.automotive.designsystem.components.composites.adaptivescrollablecolumn

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

object AdaptiveScrollableColumnDefaults {
    val contentPadding: PaddingValues = PaddingValues(0.dp)
    val verticalSpace: Dp = 0.dp
    val userScrollEnabled: Boolean = true
    val restrictedFallbackScrollMode: RestrictedScrollFallback = RestrictedScrollFallback.SnapSingle
}