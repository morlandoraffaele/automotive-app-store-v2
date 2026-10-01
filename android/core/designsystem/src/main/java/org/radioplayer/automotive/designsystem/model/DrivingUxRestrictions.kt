package org.radioplayer.automotive.designsystem.model

import androidx.compose.runtime.staticCompositionLocalOf

data class DrivingUxRestrictions (
    val isRestricted: Boolean = false,
    val limitContent: Boolean = false,
    val maxCumulativeContentItems: Int? = null,
    val limitStringLength: Boolean = false,
    val maxStringLength: Int? = null
)

val LocalDrivingUxRestrictions = staticCompositionLocalOf { DrivingUxRestrictions() }