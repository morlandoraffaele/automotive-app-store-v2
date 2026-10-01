package org.radioplayer.automotive.designsystem.components.composites.bottomsheet

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import org.radioplayer.automotive.designsystem.components.composites.iconbutton.standard.IconButtonStandard
import org.radioplayer.automotive.designsystem.components.primitives.Text
import org.radioplayer.automotive.designsystem.components.primitives.icon.Icon
import org.radioplayer.automotive.designsystem.components.primitives.icon.IconSetEnum
import org.radioplayer.automotive.designsystem.theme.AutomotiveTheme

/**
 * Shared chrome for the `Bottom Sheet / *` Figma component family: scrim, slide-up/fade
 * animation, a header slot (title + optional leading collapse/dismiss icon), and a scrollable
 * content slot. [BottomSheetProgrammeSchedule], [BottomSheetEnrichedContent], and
 * [BottomSheetExternalContent] all wrap this.
 *
 * The content slot is deliberately `ColumnScope.() -> Unit` rather than owning scroll itself —
 * callers decide `LazyColumn` (a real list, like Programme Schedule) vs. `Column` +
 * `verticalScroll` (static content, like Enriched/External Content), matching how the Figma
 * export itself splits a fixed `Header Container` from a `Scrollable Container` *slot*.
 *
 * Stays composed through its own exit animation via [MutableTransitionState] — gating visibility
 * on `visible` alone would skip the slide-down/fade-out and just vanish.
 *
 * @param visible Whether the sheet should be shown. Toggling this drives the slide/fade
 * animation; the sheet stays composed until the exit animation finishes.
 * @param onDismissRequest Called when the user taps the scrim, presses back, or taps the leading
 * icon (if [onLeadingIconClick] isn't separately provided).
 * @param title Header title text.
 * @param modifier Modifier applied to the sheet's own [Surface] (not the scrim/overlay).
 * @param onLeadingIconClick Action for the header's leading collapse icon. Defaults to
 * [onDismissRequest]. Pass `null` to hide the leading icon entirely.
 * @param colors See [CarBottomSheetScaffoldDefaults.colors].
 * @param shape See [CarBottomSheetScaffoldDefaults.shape]. Applies to the sheet only, not the scrim.
 * @param maxWidth Caps the sheet's width; it otherwise fills its container. See
 * [CarBottomSheetScaffoldDefaults.MaxWidth].
 * @param heightFraction Fraction of the available height the sheet grows to fill (leaves a gap at
 * the top so it doesn't fully cover the screen). Bottom sheets are usually height-constrained by
 * their content, but this component's `content` slot may host a `LazyColumn`, which needs a
 * bounded height to lay out — a fixed fraction is the simplest way to give it one.
 * @param content The scrollable content area, inserted below the header.
 */
@Composable
fun CarBottomSheetScaffold(
    visible: Boolean,
    onDismissRequest: () -> Unit,
    title: String,
    modifier: Modifier = Modifier,
    onLeadingIconClick: (() -> Unit)? = onDismissRequest,
    colors: CarBottomSheetScaffoldColors = CarBottomSheetScaffoldDefaults.colors(),
    shape: Shape = CarBottomSheetScaffoldDefaults.shape(),
    maxWidth: Dp = CarBottomSheetScaffoldDefaults.MaxWidth,
    heightFraction: Float = 0.85f,
    content: @Composable ColumnScope.() -> Unit,
) {
    val transitionState = remember { MutableTransitionState(false) }
    transitionState.targetState = visible
    val currentOnDismissRequest by rememberUpdatedState(onDismissRequest)

    // Keep the Dialog composed through the exit animation - gating on `visible` alone would
    // remove it the instant `visible` flips to false, skipping the slide-down/fade-out entirely.
    if (transitionState.currentState || transitionState.targetState) {
        Dialog(
            onDismissRequest = onDismissRequest,
            properties = DialogProperties(
                usePlatformDefaultWidth = false,
                dismissOnBackPress = true,
                dismissOnClickOutside = false,
            )
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                AnimatedVisibility(
                    visibleState = transitionState,
                    modifier = Modifier.fillMaxSize(),
                    enter = fadeIn(),
                    exit = fadeOut(),
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(colors.scrimColor)
                            .pointerInput(Unit) {
                                detectTapGestures(onTap = { currentOnDismissRequest() })
                            }
                    )
                }

                // Horizontally centered, not full-width: the source Figma frames are a FIXED
                // 935dp-wide panel (not a responsive edge-to-edge sheet). AnimatedVisibility here
                // is deliberately NOT fillMaxWidth'd - it wraps to the Surface's own
                // (width-capped) size, so the outer Box's BottomCenter alignment has actual slack
                // to center it within. An earlier version chained
                // fillMaxWidth().widthIn(max=...).wrapContentWidth(End) on the Surface itself -
                // by the time wrapContentWidth ran, the preceding modifiers had already pinned an
                // exact width with no slack left to align within, so it was a dead no-op. See
                // docs/bottom-sheet-audit.md.
                AnimatedVisibility(
                    visibleState = transitionState,
                    modifier = Modifier.align(Alignment.BottomCenter),
                    enter = slideInVertically(initialOffsetY = { it }),
                    exit = slideOutVertically(targetOffsetY = { it }),
                ) {
                    Surface(
                        modifier = modifier
                            .widthIn(max = maxWidth)
                            .fillMaxWidth()
                            .fillMaxHeight(heightFraction),
                        shape = shape,
                        color = colors.containerColor,
                        contentColor = colors.contentColor,
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(
                                    start = AutomotiveTheme.measurement.spaces.largeIncreased,
                                    top = AutomotiveTheme.measurement.spaces.largeIncreased,
                                    end = AutomotiveTheme.measurement.spaces.largeIncreased,
                                )
                        ) {
                            BottomSheetHeader(
                                title = title,
                                onLeadingIconClick = onLeadingIconClick,
                                colors = colors,
                            )
                            Column(modifier = Modifier.weight(1f), content = content)
                        }
                    }
                }
            }
        }
    }
}

/**
 * Simplified header: leading collapse icon + title only. The Figma export's `Header Container`
 * wraps a full `Header / Subroute / Default` instance (a 125-variant component family on its
 * own — subtitle, trailing button, icon-swap slots, etc.), which isn't part of the designsystem
 * module moved in Step 0. Porting that whole family is out of scope here; see
 * docs/bottom-sheet-audit.md.
 *
 * Title style is `body1Medium` (32sp Medium), taken from the actual `Header/Subroute/Default`
 * component's own title text node (`components["...mainComponentId"].structure`, resolved via
 * `Bottom Sheet - Extrernal Content.json`) — not the 44sp `display2Medium` used for in-content
 * headlines like External Content's "Scan the QR Code..." text, which is a visually larger,
 * separate style for a different purpose.
 *
 * Spacing also taken from that same component structure's own layout (its "Type=Two-line,
 * Condition=1 line" variant, the one actually instanced here): the root row is
 * `itemSpacing: 32` between the leading icon and the title (`spaces.largeIncreased`), with its
 * own `paddingTop: 8` / `paddingBottom: 16` (`spaces.extraSmall` / `spaces.medium`) — separate
 * from, and in addition to, the 32dp padding the whole sheet applies around all of its content.
 * The leading icon's 76x76dp frame needs no explicit sizing — it already matches
 * `MinimumSizeTokens.MinTapArea` (76dp), which `IconButtonStandard` applies as its own default min size.
 */
@Composable
private fun BottomSheetHeader(
    title: String,
    onLeadingIconClick: (() -> Unit)?,
    colors: CarBottomSheetScaffoldColors,
) {
    Row(
        modifier = Modifier.padding(
            top = AutomotiveTheme.measurement.spaces.extraSmall,
            bottom = AutomotiveTheme.measurement.spaces.medium,
        ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(AutomotiveTheme.measurement.spaces.largeIncreased),
    ) {
        if (onLeadingIconClick != null) {
            IconButtonStandard(
                onClick = onLeadingIconClick,
                icon = {
                    Icon(
                        size = AutomotiveTheme.icon.primary,
                        name = IconSetEnum.ExpandMore,
                        contentDescription = "Collapse",
                        color = colors.leadingIconColor,
                    )
                }
            )
        }
        Text(
            text = title,
            style = AutomotiveTheme.typography.body1Medium,
            color = colors.headlineColor,
        )
    }
}
