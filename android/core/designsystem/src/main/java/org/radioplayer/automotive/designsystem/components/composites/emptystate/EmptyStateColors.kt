package org.radioplayer.automotive.designsystem.components.composites.emptystate

import androidx.compose.ui.graphics.Color

/**
 * Colors used by [EmptyState]. Button colors are intentionally not part of this contract: the
 * empty-state CTA delegates to the Design System's tonal button ([org.radioplayer.automotive.designsystem.components.composites.button.tonal.ButtonTonal]),
 * which owns its own [org.radioplayer.automotive.designsystem.components.composites.button.tonal.ButtonTonalDefaults.colors].
 */
data class EmptyStateColors(
    val container: Color,
    val glow: Color,
    val patternTint: Color,
    val iconTint: Color,
    val title: Color,
    val description: Color
)
