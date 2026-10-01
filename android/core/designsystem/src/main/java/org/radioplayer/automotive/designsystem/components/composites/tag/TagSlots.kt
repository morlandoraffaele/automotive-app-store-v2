package org.radioplayer.automotive.designsystem.components.composites.tag

import android.nfc.Tag

sealed class TagSlots {
    data class Leading(val content: TagContent) : TagSlots()
    data class Trailing(val content: TagContent) : TagSlots()
    data class Both(val leading: TagContent, val trailing: TagContent) : TagSlots()

    internal val leadingContent: TagContent?
        get() = when (this) {
            is Leading -> content
            is Trailing -> null
            is Both -> leading
        }

    internal val trailingContent: TagContent?
        get() = when (this) {
            is Leading -> null
            is Trailing -> content
            is Both -> trailing
        }

}