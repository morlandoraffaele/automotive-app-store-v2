package org.radioplayer.automotive.designsystem.components.composites.divider

sealed interface DividerVariant {
    data object FullWidth : DividerVariant
    data object Inset : DividerVariant
    data object MiddleInset : DividerVariant
}

