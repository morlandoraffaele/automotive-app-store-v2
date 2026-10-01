package org.radioplayer.automotive.designsystem.components.composites.bottomsheet

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import org.radioplayer.automotive.designsystem.theme.AutomotiveTheme

object CarBottomSheetScaffoldDefaults {

    /**
     * Max content width, taken directly from the `Bottom Sheet / *` Figma components' own frame
     * width (935dp, identical across all 3 variants). The scaffold otherwise fills available
     * width up to this, it isn't a fixed size.
     */
    val MaxWidth: Dp = 935.dp

    /**
     * Height of the trailing spacer at the end of the scrollable content, taken from the `Spacer`
     * frame present in both `Bottom Sheet / Enriched Content` and `Bottom Sheet / Extrernal
     * Content` (120dp in both). Not a token — no matching value exists in [org.radioplayer.automotive.designsystem.tokens.SizeTokens].
     */
    val BottomContentSpacerHeight: Dp = 120.dp

    @Composable
    fun colors(
        scrimColor: Color = AutomotiveTheme.colorScheme.aaosScrimMedium,
        containerColor: Color = AutomotiveTheme.colorScheme.surfaceContainerHigh,
        contentColor: Color = AutomotiveTheme.colorScheme.onSurface,
        headlineColor: Color = AutomotiveTheme.colorScheme.onSurface,
        leadingIconColor: Color = AutomotiveTheme.colorScheme.onSurface,
    ): CarBottomSheetScaffoldColors = CarBottomSheetScaffoldColors(
        scrimColor = scrimColor,
        containerColor = containerColor,
        contentColor = contentColor,
        headlineColor = headlineColor,
        leadingIconColor = leadingIconColor,
    )

    /**
     * Rounded top corners only, flush bottom edge (the sheet is anchored to the bottom of its
     * container). The source `Bottom Sheet / *` Figma components export with no corner radius at
     * all (`cornerRadius: null`, `clipsContent: false`) — that export captures only the sheet's
     * content card, not its presentation chrome, so this default radius is this component's own
     * reasonable choice, not a literal Figma value. See docs/bottom-sheet-audit.md.
     */
    @Composable
    fun shape(cornerRadius: Dp = AutomotiveTheme.measurement.shapes.extraLarge): Shape =
        RoundedCornerShape(
            topStart = cornerRadius,
            topEnd = cornerRadius,
            bottomStart = 0.dp,
            bottomEnd = 0.dp,
        )
}
