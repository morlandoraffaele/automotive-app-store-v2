package org.radioplayer.automotive.designsystem.components.composites.toast

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import org.radioplayer.automotive.designsystem.theme.AutomotiveTheme

object ToastDefaults {

    /** Toast auto-dismisses after this long (docs.partner.android.com/drivingux Toast spec). */
    const val DurationMillis: Long = 8_000L

    val Width: Dp
        @Composable @ReadOnlyComposable
        get() = AutomotiveTheme.measurement.sizes.dialogToastWidth

    val CornerRadius: Dp
        @Composable @ReadOnlyComposable
        get() = AutomotiveTheme.measurement.shapes.small

    val ContainerPaddingVertical: Dp = 10.dp

    val ContainerPaddingHorizontal: Dp = 23.dp

    val MessageStyle: TextStyle
        @Composable @ReadOnlyComposable
        get() = AutomotiveTheme.typography.body3

    @Composable
    fun colors(
        container: Color = AutomotiveTheme.colorScheme.surfaceContainerHighest,
        message: Color = AutomotiveTheme.colorScheme.onSurface,
        shadow: Color = AutomotiveTheme.colorScheme.aaosScrimLow,
    ): ToastColors = ToastColors(
        container = container,
        message = message,
        shadow = shadow,
    )
}
