package org.radioplayer.automotive.designsystem.components.composites.statusindicator

import androidx.compose.runtime.Composable
sealed interface StatusIndicatorContent {
    data class Custom(
        val layoutRole: LayoutRole,
        val accessibilityLabel: String,
        val content: @Composable () -> Unit
    ) : StatusIndicatorContent

    enum class LayoutRole {
        TEXT,
        ICON
    }
}