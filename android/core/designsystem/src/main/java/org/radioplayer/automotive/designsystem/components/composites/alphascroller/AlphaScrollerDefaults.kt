package org.radioplayer.automotive.designsystem.components.composites.alphascroller



import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import org.radioplayer.automotive.designsystem.theme.AutomotiveTheme

object AlphaScrollerDefaults {
    /**
     * The source `List Item / Alpha Indexer - No Indicator` component's `Selected=*,
     * Status=Disabled` variants are identical to their `Status=Enabled` counterparts in the
     * Figma file (same color, same size) — almost certainly an unfinished state in the source
     * design rather than a deliberate "disabled looks the same as enabled" choice, since every
     * other disableable component in this design system (`SearchInputField`, `ListItem`, ...)
     * dims disabled content. Matching that established convention here instead of the literal
     * (degenerate) source data.
     */
    const val DisabledContentAlpha = 0.38f
    val ContainerWidthFixed = 80.dp


    @Composable
    fun contentColor(selected: Boolean, enabled: Boolean): Color = when {
        !enabled -> AutomotiveTheme.colorScheme.onSurfaceVariant.copy(alpha = DisabledContentAlpha)
        selected -> AutomotiveTheme.colorScheme.onSurface
        else -> AutomotiveTheme.colorScheme.onSurfaceVariant
    }

    @Composable
    fun textStyle(selected: Boolean): TextStyle =
        if (selected) AutomotiveTheme.typography.display1Medium else AutomotiveTheme.typography.display3
}
