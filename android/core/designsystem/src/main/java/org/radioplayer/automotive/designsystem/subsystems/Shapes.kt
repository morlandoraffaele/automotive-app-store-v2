package org.radioplayer.automotive.designsystem.subsystems

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp
import org.radioplayer.automotive.designsystem.tokens.SizeTokens

@Immutable
data class Shapes(
    val none: Dp = SizeTokens.Size0,
    val extraSmall: Dp = SizeTokens.Size2,
    val small: Dp = SizeTokens.Size4,
    val medium: Dp = SizeTokens.Size5,
    val large: Dp = SizeTokens.Size6,
    val largeIncreased: Dp = SizeTokens.Size7,
    val extraLarge: Dp = SizeTokens.Size9,
    val extraLargeIncreased: Dp = SizeTokens.Size10,
    val extraExtraLarge: Dp = SizeTokens.Size13,
    val full: Dp = SizeTokens.Size25
)

internal val LocalShapes = staticCompositionLocalOf { Shapes() }