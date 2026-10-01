package org.radioplayer.automotive.designsystem.components.composites.screenscaffold

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import org.radioplayer.automotive.designsystem.subsystems.LocalSpaces

/**
 * Per-side padding spec for a screen.
 *
 * Every side is optional: `null` means "fall back to the default for that side".
 * This allows a screen to override only the sides it cares about while still
 * inheriting the global default (see [ScreenScaffold] and [ProvideScreenPadding]).
 */
@Immutable
data class ScreenPadding(
    val start: Dp? = null,
    val top: Dp? = null,
    val end: Dp? = null,
    val bottom: Dp? = null
) {
    /**
     * Merges this (possibly partial) spec with a [default] spec:
     * every side that is `null` here falls back to the default's side.
     */
    fun resolve(default: ScreenPadding): ScreenPadding = ScreenPadding(
        start = start ?: default.start,
        top = top ?: default.top,
        end = end ?: default.end,
        bottom = bottom ?: default.bottom
    )

    /** Converts the resolved spec into Compose [PaddingValues]. */
    fun toPaddingValues(): PaddingValues = PaddingValues(
        start = start ?: 0.dp,
        top = top ?: 0.dp,
        end = end ?: 0.dp,
        bottom = bottom ?: 0.dp
    )

    companion object {
        /** Same value on every side. */
        fun all(value: Dp): ScreenPadding =
            ScreenPadding(start = value, top = value, end = value, bottom = value)

        /** Same value on left/right, another on top/bottom. */
        fun symmetric(horizontal: Dp, vertical: Dp): ScreenPadding =
            ScreenPadding(start = horizontal, top = vertical, end = horizontal, bottom = vertical)
    }
}

object ScreenPaddingDefaults {
    /**
     * Theme-driven default screen padding, resolved from the [org.radioplayer.automotive.designsystem.subsystems.Spaces]
     * subsystem so it follows any ThemeConfig spacing overrides.
     */
    val theme: ScreenPadding
        @Composable get() {
            val spaces = LocalSpaces.current
            return ScreenPadding(
                start = spaces.largeIncreased,
                top = 0.dp,
                end = spaces.largeIncreased,
                bottom = 0.dp
            )
        }
}

/**
 * Composition local holding the scoped default screen padding.
 *
 * `null` means "no scoped override" — [ScreenScaffold] then falls back to
 * [ScreenPaddingDefaults.theme]. Set it via [ProvideScreenPadding].
 */
val LocalScreenPadding = staticCompositionLocalOf<ScreenPadding?> { null }

/**
 * Changes the default screen padding for the given [content] subtree.
 *
 * Screens inside can still override any side individually through the
 * `padding` argument of [ScreenScaffold]. Sides not specified here fall back
 * to the enclosing scoped default, or to [ScreenPaddingDefaults.theme] when
 * there is none.
 */
@Composable
fun ProvideScreenPadding(
    padding: ScreenPadding,
    content: @Composable () -> Unit
) {
    val current = LocalScreenPadding.current
    val effective = if (current != null) padding.resolve(current) else padding.resolve(ScreenPaddingDefaults.theme)
    CompositionLocalProvider(LocalScreenPadding provides effective, content = content)
}

/**
 * Standard screen wrapper.
 *
 * Applies padding to the screen content, resolved side-by-side from, in order:
 * 1. the explicit [padding] argument (per-screen override, any side optional),
 * 2. the scoped default from [LocalScreenPadding] (set via [ProvideScreenPadding]),
 * 3. the theme default from [ScreenPaddingDefaults.theme].
 *
 * Example — only override what you need:
 * ```
 * ScreenScaffold(
 *     padding = ScreenPadding(top = AutomotiveTheme.measurement.spaces.large)
 * ) { ... }
 * ```
 *
 * @param padding Per-side padding overrides. Sides left as `null` inherit from
 *   the scoped/theme default.
 * @param contentAlignment Alignment of the content inside the padded area.
 * @param content Screen content, laid out inside the padded [Box].
 */
@Composable
fun ScreenScaffold(
    modifier: Modifier = Modifier,
    padding: ScreenPadding = ScreenPadding(),
    contentAlignment: Alignment = Alignment.TopStart,
    content: @Composable BoxScope.() -> Unit
) {
    val resolved = padding.resolve(LocalScreenPadding.current ?: ScreenPaddingDefaults.theme)

    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(resolved.toPaddingValues()),
        contentAlignment = contentAlignment,
        content = content
    )
}

