package org.radioplayer.automotive.designsystem.subsystems

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp
import org.radioplayer.automotive.designsystem.tokens.SizeTokens

@Immutable
data class Stroke(
    val none: Dp = SizeTokens.Size0,
    val thin: Dp = SizeTokens.Size1,
    val medium: Dp = SizeTokens.Size2,
    val thick: Dp = SizeTokens.Size4,
    val heavy: Dp = SizeTokens.Size5,
)

internal val LocalStroke = staticCompositionLocalOf { Stroke() }