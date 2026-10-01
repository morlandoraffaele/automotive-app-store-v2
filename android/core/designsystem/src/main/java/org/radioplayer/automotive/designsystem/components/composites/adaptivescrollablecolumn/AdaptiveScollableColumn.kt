package org.radioplayer.automotive.designsystem.components.composites.adaptivescrollablecolumn

import android.content.res.Configuration
import androidx.compose.foundation.MutatePriority
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.FlingBehavior
import androidx.compose.foundation.gestures.ScrollableDefaults
import androidx.compose.foundation.gestures.snapping.SnapLayoutInfoProvider
import androidx.compose.foundation.gestures.snapping.SnapPosition
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import org.radioplayer.automotive.designsystem.model.DrivingUxRestrictions
import org.radioplayer.automotive.designsystem.subsystems.Spaces
import org.radioplayer.automotive.designsystem.theme.AutomotiveTheme

/**
 * Remembers and builds a [FlingBehavior] tailored to the provided [AdaptiveScrollMode].
 *
 * This internal helper handles the physics mapping for the list:
 * - [AdaptiveScrollMode.Linear] applies the default platform scroll physics.
 * - [AdaptiveScrollMode.SnapSingle] and [AdaptiveScrollMode.SnapMultiple] employ a custom
 * [SnapLayoutInfoProvider] to calculate specific snap alignment offsets.
 *
 * @param state The [LazyListState] bound to the host scrollable container.
 * @param snapMode The current evaluated scroll strategy to apply.
 * @return A [FlingBehavior] configured according to the evaluated [snapMode].
 */
@Composable
private fun rememberAdaptiveSnapFlingBehavior(
    state: LazyListState,
    snapMode: AdaptiveScrollMode
): FlingBehavior {

    if (snapMode is AdaptiveScrollMode.Linear) {
        return ScrollableDefaults.flingBehavior()
    }

    val baseSnapLayoutInfoProvider = remember(state) {
        SnapLayoutInfoProvider(lazyListState = state, snapPosition = SnapPosition.Start)
    }

    val customSnapLayoutInfoProvider = remember(baseSnapLayoutInfoProvider, state) {
        object : SnapLayoutInfoProvider {
            override fun calculateSnapOffset(velocity: Float): Float {
                return when (snapMode) {
                    is AdaptiveScrollMode.SnapSingle -> baseSnapLayoutInfoProvider.calculateSnapOffset(
                        velocity
                    )

                    is AdaptiveScrollMode.SnapMultiple -> baseSnapLayoutInfoProvider.calculateSnapMultipleItems(
                        state,
                        baseSnapLayoutInfoProvider,
                        velocity
                    )

                    is AdaptiveScrollMode.Linear -> 0F
                }
            }

            override fun calculateApproachOffset(velocity: Float, decayOffset: Float): Float {
                return 0F
            }
        }
    }

    return rememberSnapFlingBehavior(customSnapLayoutInfoProvider)
}

/**
 * Resolves the effective scroll mode based on the user-requested strategy and the current vehicle restriction state.
 *
 * If driving restrictions are active, dangerous continuous scrolling ([AdaptiveScrollMode.Linear]) is intercepted
 * and downshifted to the safe fallback strategy.
 *
 * @param requested The initial [AdaptiveScrollMode] requested by the caller.
 * @param isRestricted Current distraction optimization status from the vehicle system.
 * @param restrictedFallback The safe strategy configuration to enforce when restrictions apply.
 * @return The final safe [AdaptiveScrollMode] to execute.
 */
private fun resolveScrollMode(
    requested: AdaptiveScrollMode,
    isRestricted: Boolean,
    restrictedFallback: RestrictedScrollFallback
): AdaptiveScrollMode = when (requested) {
    is AdaptiveScrollMode.SnapSingle, is AdaptiveScrollMode.SnapMultiple -> requested
    is AdaptiveScrollMode.Linear -> if (isRestricted) restrictedFallback.toScrollMode() else requested
}

/**
 * An automotive-optimized, scrollable vertical container that dynamically adapts its scroll physics
 * and snapping behaviors to comply with active vehicle driving restrictions.
 *
 * ### Driving Safety Behavior
 * To limit driver distraction and comply with regulatory UX mandates, this component safeguards
 * scrolling behaviors when the vehicle transitions into a restricted state (`drivingUxRestrictions.isRestricted == true`):
 * - **Automatic Downshift:** If [AdaptiveScrollMode.Linear] is used during restrictions, it falls back to
 * [restrictedFallbackScrollMode] (typically step-by-step snapping) to prevent runaway infinite scrolling.
 * - **Hard Interruption:** Active continuous scroll momentum is forcefully aborted immediately upon entering a restricted state
 * to guarantee the driver maintains full deterministic control over the list steps.
 * - **Snapping Pass-through:** Configured [AdaptiveScrollMode.SnapSingle] and [AdaptiveScrollMode.SnapMultiple] strategies
 * persist unchanged across states as they are natively safe for automotive environments.
 *
 * @param modifier The [Modifier] to be applied to the outer layout container.
 * @param drivingUxRestrictions The current vehicle restriction payload monitoring driver safety states.
 * @param scrollMode The preferred scroll behavior under unrestricted driving conditions.
 * @param restrictedFallbackScrollMode The defensive configuration applied when [scrollMode] is [AdaptiveScrollMode.Linear] during restricted states. Defaults to [AdaptiveScrollableColumnDefaults.restrictedFallbackScrollMode].
 * @param contentPadding Outer padding values distributed around the whole scroll content boundaries. Defaults to [AdaptiveScrollableColumnDefaults.contentPadding].
 * @param verticalSpace Spacing gap to insert between separate list items. Defaults to [Spaces.extraSmall].
 * @param state The state handle used to observe, control, or manipulate the scroll position. Defaults to a remembered [LazyListState].
 * @param userScrollEnabled Toggles whether interaction gestures or accessibility inputs can manipulate the scroll state. Defaults to [AdaptiveScrollableColumnDefaults.userScrollEnabled].
 * @param content The [LazyListScope] DSL scope building block containing the items to render within the scroll container.
 */
@Composable
fun AdaptiveScrollableColumn(
    modifier: Modifier = Modifier,
    drivingUxRestrictions: DrivingUxRestrictions,
    scrollMode: AdaptiveScrollMode,
    restrictedFallbackScrollMode: RestrictedScrollFallback = AdaptiveScrollableColumnDefaults.restrictedFallbackScrollMode,
    contentPadding: PaddingValues = AdaptiveScrollableColumnDefaults.contentPadding,
    verticalSpace: Dp = AdaptiveScrollableColumnDefaults.verticalSpace,
    state: LazyListState = rememberLazyListState(),
    userScrollEnabled: Boolean = AdaptiveScrollableColumnDefaults.userScrollEnabled,
    content: LazyListScope.() -> Unit
) {
    LaunchedEffect(drivingUxRestrictions.isRestricted) {
        if (drivingUxRestrictions.isRestricted && scrollMode is AdaptiveScrollMode.Linear) {
            state.scroll(MutatePriority.PreventUserInput) {}
        }
    }

    val effectiveScrollMode = remember(scrollMode, drivingUxRestrictions.isRestricted, restrictedFallbackScrollMode) {
        resolveScrollMode(scrollMode, drivingUxRestrictions.isRestricted, restrictedFallbackScrollMode)
    }

    val flingBehavior =
        rememberAdaptiveSnapFlingBehavior(state = state, snapMode = effectiveScrollMode)

    LazyColumn(
        modifier = modifier,
        state = state,
        contentPadding = contentPadding,
        verticalArrangement = Arrangement.spacedBy(verticalSpace),
        flingBehavior = flingBehavior,
        userScrollEnabled = userScrollEnabled,
    ) {
        content()
    }
}

@Preview(
    name = "Column - Light Mode",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_NO or Configuration.UI_MODE_TYPE_CAR
)
@Preview(
    name = "Column - Dark Mode",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_CAR
)
@Composable
private fun AdaptiveScrollableColumnPreview() {
    AutomotiveTheme {
        AdaptiveScrollableColumn(
            modifier = Modifier.background(AutomotiveTheme.colorScheme.primary),
            scrollMode = AdaptiveScrollMode.Linear,
            drivingUxRestrictions = DrivingUxRestrictions()
        ) {
            item {
                Box(
                    modifier = Modifier
                        .width(200.dp)
                        .height(100.dp)
                        .background(Color.Blue)
                )
            }
            item {
                Box(
                    modifier = Modifier
                        .width(200.dp)
                        .height(100.dp)
                        .background(Color.Red)
                )
            }
            item {
                Box(
                    modifier = Modifier
                        .width(200.dp)
                        .height(100.dp)
                        .background(Color.Green)
                )
            }
            item {
                Box(
                    modifier = Modifier
                        .width(200.dp)
                        .height(100.dp)
                        .background(Color.Yellow)
                )
            }
            item {
                Box(
                    modifier = Modifier
                        .width(200.dp)
                        .height(100.dp)
                        .background(Color.Cyan)
                )
            }
        }
    }
}

