package org.radioplayer.automotive.designsystem.components.features.savethesong.dialog

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import org.radioplayer.automotive.designsystem.theme.AutomotiveTheme

object SaveTheSongDialogDefaults {

    val ContainerWidth: Dp
        @Composable @ReadOnlyComposable
        get() = AutomotiveTheme.measurement.sizes.dialogToastWidth

    val ContainerMinHeight: Dp = 300.dp

    val RowMinWidth: Dp = 393.dp

    val RightColumnWidth: Dp = 240.dp

    val RightColumnGap: Dp = 30.dp

    val GlowColor: Color = Color(0xFF25D1DA)

    val CornerRadius: Dp
        @Composable @ReadOnlyComposable
        get() = AutomotiveTheme.measurement.shapes.largeIncreased

    val ContainerPadding: Dp
        @Composable @ReadOnlyComposable
        get() = AutomotiveTheme.measurement.spaces.largeIncreased

    val RowGap: Dp
        @Composable @ReadOnlyComposable
        get() = AutomotiveTheme.measurement.spaces.large

    val ColumnGap: Dp
        @Composable @ReadOnlyComposable
        get() = AutomotiveTheme.measurement.spaces.large

    val GlowBleed: Dp = 32.dp

    val GlowBlurRadius: Dp = 256.dp

    const val GlowOpacity: Float = 0.8f

    val LogoHorizontalPadding: Dp = 48.dp

    val LogoVerticalPadding: Dp = 24.dp

    val TitleStyle: TextStyle
        @Composable @ReadOnlyComposable
        get() = AutomotiveTheme.typography.display3Medium

    val DescriptionStyle: TextStyle
        @Composable @ReadOnlyComposable
        get() = AutomotiveTheme.typography.body3

    val CaptionStyle: TextStyle
        @Composable @ReadOnlyComposable
        get() = AutomotiveTheme.typography.body3

    @Composable
    fun colors(
        container: Color = AutomotiveTheme.colorScheme.surfaceContainer,
        glow: Color = GlowColor,
        title: Color = AutomotiveTheme.colorScheme.onSurface,
        description: Color = AutomotiveTheme.colorScheme.onSurfaceVariant,
        caption: Color = AutomotiveTheme.colorScheme.onSurfaceVariant,
    ): SaveTheSongDialogColors = SaveTheSongDialogColors(
        container = container,
        glow = glow,
        title = title,
        description = description,
        caption = caption,
    )
}
