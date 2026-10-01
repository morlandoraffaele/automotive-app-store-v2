package org.radioplayer.automotive.designsystem.components.composites.bottomsheet

import androidx.compose.ui.graphics.Color

/**
 * Colors for [CarBottomSheetScaffold]. Unlike most `*Colors` classes in this module, there's no
 * per-interaction-state resolver method here (no `resolveState`) — a bottom sheet's only "state"
 * is open/closed (driven by [CarBottomSheetScaffold]'s own `visible` param, not a color), so a
 * flat set of colors is the honest shape for this component rather than force-fitting the
 * resolveState pattern used by e.g. `ListItem`/`ListItemSchedule`.
 */
data class CarBottomSheetScaffoldColors(
    val scrimColor: Color,
    val containerColor: Color,
    val contentColor: Color,
    val headlineColor: Color,
    val leadingIconColor: Color,
)
