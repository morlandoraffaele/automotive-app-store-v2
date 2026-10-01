package org.radioplayer.automotive.designsystem.subsystems

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp
import org.radioplayer.automotive.designsystem.tokens.MinimumSizeTokens

@Immutable
data class Sizes(
    val minTapArea: Dp = MinimumSizeTokens.MinTapArea,
    val minInteractionWidth: Dp = MinimumSizeTokens.MinInteractionWidth,
    val minContentCell: Dp = MinimumSizeTokens.MinContentCell,
    val minAppCell: Dp = MinimumSizeTokens.MinAppCell,
    val maxInteractionWidth: Dp = MinimumSizeTokens.MaxInteractionWidth,
    val dialogToastWidth: Dp = MinimumSizeTokens.DialogToastWidth,
)

internal val LocalSizes = staticCompositionLocalOf { Sizes() }