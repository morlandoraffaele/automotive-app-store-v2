package org.radioplayer.automotive.designsystem.components.composites.dialog

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import org.radioplayer.automotive.designsystem.theme.AutomotiveTheme

data class AutomotiveDialogColors(
    val container: Color,
    val title: Color,
    val content: Color,
    val shadow: Color,
)

object AutomotiveDialogDefaults {

    /** Every role below is a direct 1:1 mapping from the Figma export's `resolvedColor` /
     *  `tokenPath` data — see `AUTOMOTIVE_DIALOG.md` §1 for the full trace. */
    @Composable
    fun colors(
        container: Color = AutomotiveTheme.colorScheme.surfaceContainerHighest,
        title: Color = AutomotiveTheme.colorScheme.onSurface,
        content: Color = AutomotiveTheme.colorScheme.onSurfaceVariant,
        shadow: Color = AutomotiveTheme.colorScheme.aaosScrimLow,
    ): AutomotiveDialogColors = AutomotiveDialogColors(container, title, content, shadow)
}
