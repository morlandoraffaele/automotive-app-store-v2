package org.radioplayer.automotive.designsystem.subsystems

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp
import org.radioplayer.automotive.designsystem.tokens.SizeTokens

@Immutable
data class BlurBase(
    val none: Dp = SizeTokens.Size0,
    val low: Dp = SizeTokens.Size4,
    val high: Dp = SizeTokens.Size6
)

@Immutable
data class BlurAmbient(
    val low: Dp = SizeTokens.Size17,
    val medium: Dp = SizeTokens.Size19,
    val high: Dp = SizeTokens.Size23
)

@Immutable
data class Blur(
    val base: BlurBase = BlurBase(),
    val ambient: BlurAmbient = BlurAmbient()
)

internal val LocalBlur = staticCompositionLocalOf { Blur() }