package org.radioplayer.automotive.designsystem.subsystems

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp
import org.radioplayer.automotive.designsystem.tokens.SpaceTokens

@Immutable
data class Spaces(
    val extraExtraSmall: Dp = SpaceTokens.P0,
    val extraSmall: Dp = SpaceTokens.P1,
    val small: Dp = SpaceTokens.P2,
    val medium: Dp = SpaceTokens.P3,
    val large: Dp = SpaceTokens.P4,
    val largeIncreased: Dp = SpaceTokens.P5,
    val extraLarge: Dp = SpaceTokens.P6,
    val extraLargeIncreased: Dp = SpaceTokens.P7,
    val extraExtraLarge: Dp = SpaceTokens.P8
)

internal val LocalSpaces = staticCompositionLocalOf { Spaces() }
