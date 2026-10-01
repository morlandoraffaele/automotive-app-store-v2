package org.radioplayer.automotive.designsystem.components.composites.adaptivescrollablecolumn

sealed interface AdaptiveScrollMode {
    data object SnapSingle: AdaptiveScrollMode
    data object SnapMultiple: AdaptiveScrollMode
    data object Linear: AdaptiveScrollMode
}