package org.radioplayer.automotive.designsystem.subsystems

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp
import org.radioplayer.automotive.designsystem.tokens.SizeTokens

@Immutable
data class Icon(
    val brand: Dp = SizeTokens.Size17,
    val hero: Dp = SizeTokens.Size15,
    val macro: Dp = SizeTokens.Size13,
    val primary: Dp = SizeTokens.Size12,
    val secondary: Dp = SizeTokens.Size10,
    val tertiary: Dp = SizeTokens.Size8,
    val micro: Dp = SizeTokens.Size7
)

internal val LocalIcon = staticCompositionLocalOf { Icon() }