package org.radioplayer.automotive.designsystem.components.composites.search

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue

sealed class SearchInputFieldContent {
    data object Empty : SearchInputFieldContent()
    data class Typing(val text: String) : SearchInputFieldContent()
    data object VoiceOnly : SearchInputFieldContent()
}

/**
 * Holds the query text for a [SearchInputField].
 *
 * [SearchInputField] owns and displays this state on its own — typing and clearing update it
 * internally — but a caller can still steer it imperatively when needed. The main use case is
 * injecting a speech-recognition transcript after [SearchInputField]'s `onVoiceSearchClick`
 * launches an external voice recognizer:
 *
 * ```kotlin
 * val searchState = rememberSearchInputFieldState()
 *
 * SearchInputField(
 *     state = searchState,
 *     onVoiceSearchClick = { launchSpeechRecognizer(onResult = searchState::setQuery) },
 *     onQueryChange = { viewModel.onQueryChanged(it) },
 * )
 * ```
 *
 * Create and remember one with [rememberSearchInputFieldState] — don't construct it directly
 * inside a composable, or it won't survive recomposition/configuration changes.
 */
class SearchInputFieldState(initialQuery: String = "") {
    // Underscore-prefixed on purpose: Kotlin compiles a property named `query` with a private
    // setter to a JVM method also named `setQuery(String)`, which clashes with the explicit
    // setQuery(text: String) function below (platform declaration clash). Naming the backing
    // property `_query` makes Kotlin emit `set_query`/`get_query` instead, avoiding the clash.
    private var _query by mutableStateOf(initialQuery)

    val query: String get() = _query

    /** Sets the query text, e.g. from a speech-recognition transcript or a deep link. */
    fun setQuery(text: String) {
        _query = text
    }

    /** Clears the query text, e.g. from a "clear all recent searches" action elsewhere on screen. */
    fun clear() {
        _query = ""
    }

    companion object {
        val Saver: Saver<SearchInputFieldState, String> = Saver(
            save = { it.query },
            restore = { SearchInputFieldState(it) },
        )
    }
}

@Composable
fun rememberSearchInputFieldState(initialQuery: String = ""): SearchInputFieldState =
    rememberSaveable(saver = SearchInputFieldState.Saver) {
        SearchInputFieldState(initialQuery)
    }