package org.radioplayer.automotive.designsystem.components.composites.tag

import androidx.compose.runtime.Composable


sealed interface TagContent {

    data class Custom(
        val layoutRole: LayoutRole,
        val accessibilityLabel: String,
        val content: @Composable () -> Unit
    ) : TagContent

    enum class LayoutRole {
        TEXT,
        ICON
    }
}