package org.radioplayer.automotive.designsystem.components.primitives.flag

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Flag sizes. Each size has its own drawable per country, so the size also selects the asset
 * (see [FlagSetEnum.resId]) rather than scaling a single one.
 */
enum class FlagSize(val width: Dp, val height: Dp) {
    Small(36.dp, 24.dp),
    Default(48.dp, 32.dp),
    Large(66.dp, 44.dp),
}
