package org.radioplayer.automotive.designsystem.components.composites.toast

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color

@Immutable
data class ToastColors(
    val container: Color,
    val message: Color,
    val shadow: Color,
)
