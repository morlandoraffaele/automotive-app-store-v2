package com.automotive.appstore.data

/**
 * A one-shot value: [getContentIfNotHandled] returns the payload exactly once, so a
 * recomposition cannot replay a side effect such as starting an install.
 *
 * Ported 1:1 from
 * `radioplayer-automotive-appstore/app/src/main/java/org/radioplayer/automotive/appstore/ui/util/Event.kt`.
 * It lives in `data` here rather than `ui` because, unlike upstream, this app performs the
 * install from the repository instead of from a composable — so no UI file needs the type.
 */
open class Event<out T>(private val content: T) {
    private var hasBeenHandled = false

    fun getContentIfNotHandled(): T? {
        return if (hasBeenHandled) {
            null
        } else {
            hasBeenHandled = true
            content
        }
    }

    fun peekContent(): T = content
}