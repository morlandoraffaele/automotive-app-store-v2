package org.radioplayer.automotive.designsystem.components.composites.rail

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf

@Immutable
data class RailDrivingUxRestrictions(
    val isRestricted: Boolean = false,
    val limitContent: Boolean = false,
    val maxCumulativeContentItems: Int? = null,
    val limitStringLength: Boolean = false,
    val maxStringLength: Int? = null
)

val LocalRailDrivingUxRestrictions = staticCompositionLocalOf { RailDrivingUxRestrictions() }