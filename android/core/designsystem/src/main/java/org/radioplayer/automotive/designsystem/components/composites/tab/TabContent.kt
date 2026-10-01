package org.radioplayer.automotive.designsystem.components.composites.tab

import androidx.compose.runtime.Composable

data class TabContent(
    val text: @Composable () -> Unit,
    val flag: @Composable (() -> Unit)? = null,
    val icon: @Composable (() -> Unit)? = null,
    val enabled: Boolean = true
)