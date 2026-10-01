package org.radioplayer.automotive.designsystem.components.composites.statusindicator

sealed class StatusIndicatorSlots {
    data class Leading(val content: StatusIndicatorContent) : StatusIndicatorSlots()
    data class Trailing(val content: StatusIndicatorContent) : StatusIndicatorSlots()
    data class Both(val leading: StatusIndicatorContent, val trailing: StatusIndicatorContent) :
        StatusIndicatorSlots()

    internal val leadingContent: StatusIndicatorContent?
        get() = when (this) {
            is Leading -> content
            is Trailing -> null
            is Both -> leading
        }

    internal val trailingContent: StatusIndicatorContent?
        get() = when (this) {
            is Leading -> null
            is Trailing -> content
            is Both -> trailing
        }
}