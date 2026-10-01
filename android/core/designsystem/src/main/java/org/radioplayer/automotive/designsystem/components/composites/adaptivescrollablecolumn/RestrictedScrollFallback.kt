package org.radioplayer.automotive.designsystem.components.composites.adaptivescrollablecolumn

sealed interface RestrictedScrollFallback {
    data object SnapSingle : RestrictedScrollFallback
    data object SnapMultiple : RestrictedScrollFallback
}

internal fun RestrictedScrollFallback.toScrollMode(): AdaptiveScrollMode = when (this) {
    RestrictedScrollFallback.SnapSingle -> AdaptiveScrollMode.SnapSingle
    RestrictedScrollFallback.SnapMultiple -> AdaptiveScrollMode.SnapMultiple
}