package org.radioplayer.automotive.designsystem.components.composites.emptystate

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import org.radioplayer.automotive.designsystem.theme.AutomotiveTheme

object EmptyStateDefaults {

    val ContentWidth: Dp = 600.dp

    /**
     * The `Content` frame is 500x232 in export 3.15 (was read as 196 from the earlier export).
     * Applied as a minimum height, so a longer description still grows the component.
     *
     * The Favorites export says 498 rather than 500 for the same frame. Treated as a stray manual
     * resize on that variant rather than a second number to honour - everything else about the two
     * is identical to the pixel.
     */
    val ContentHeight: Dp = 232.dp

    /**
     * The component's own height in the export: [ContentHeight] plus [ContentPadding] top and
     * bottom (232 + 32 + 32 = 296), which is what both variants measure. Applied as a minimum, so
     * content that needs more room still grows it - but a short description no longer collapses
     * the component to 64dp less than the design.
     */
    val MinHeight: Dp = 296.dp

    val CornerRadius: Dp
        @Composable @ReadOnlyComposable
        get() = AutomotiveTheme.measurement.shapes.largeIncreased

    val ContentPadding: Dp
        @Composable @ReadOnlyComposable
        get() = AutomotiveTheme.measurement.spaces.largeIncreased

    /** Gap between the artwork/pattern group and the Content frame - root `itemSpacing` = 48. */
    val RootGap: Dp
        @Composable @ReadOnlyComposable
        get() = AutomotiveTheme.measurement.spaces.extraLarge

    /** Gap between header, description and action inside the Content frame - `itemSpacing` = 24. */
    val ContentGap: Dp
        @Composable @ReadOnlyComposable
        get() = AutomotiveTheme.measurement.spaces.large

    val HeaderGap: Dp
        @Composable @ReadOnlyComposable
        get() = AutomotiveTheme.measurement.spaces.medium

    val HeaderIconSize: Dp
        @Composable @ReadOnlyComposable
        get() = AutomotiveTheme.icon.primary

    val PatternBoxSize: Dp = 296.dp

    val PatternBoxEndInset: Dp
        @Composable @ReadOnlyComposable
        get() = AutomotiveTheme.measurement.spaces.medium

    val GlowSize: Dp = 346.dp

    val GlowBlurRadius: Dp = 256.dp

    /**
     * The Standard variant's glow opacity. Favorites runs its red glow at full opacity - pass
     * `glowOpacity = 1f` for it. Both are read off the `Color` frame in their own export.
     */
    const val GlowOpacity: Float = 0.8f

    /**
     * The icon drawn inside each pattern tile, before [DefaultPatternZoom] scales it.
     *
     * The pattern's *tile* is [PatternTileSize]; this is the glyph inside it. The ratio of the two
     * (20/38) is what gives the pattern its spacing, and it holds across both variants - which is
     * how the source node's own geometry was recovered, since the export references it by id
     * (`sourceNodeId`) without including it.
     */
    val PatternBaseIconSize: Dp
        @Composable @ReadOnlyComposable
        get() = AutomotiveTheme.icon.micro

    /**
     * The pattern source node's own size, which is what Figma's `scalingFactor` scales and what the
     * tiles step by - **not** the glyph size.
     *
     * Derived, since the source node is not in the export: at `scalingFactor` 0.5 the Standard
     * render tiles every 20dp and at 1.2 the Favorites render tiles every ~47dp, and `38 * zoom + 1`
     * is the one tile size that produces both (measured off the exports' own PNG renders, which are
     * 1:1 with dp at 781x296).
     */
    val PatternTileSize: Dp = 38.dp

    /** Figma's `scalingFactor`: 0.5 on Standard, 1.2 on Favorites. */
    const val DefaultPatternZoom: Float = 0.5f

    /** Figma's pattern `spacing`, `{x: 1, y: 1}` in both variants. */
    val DefaultPatternSpacing: Dp = 1.dp

    /**
     * The pattern's colour: **hardcoded `#FFFFFF` at full opacity, on purpose.**
     *
     * Not a token, and not `colorScheme.onSurface` (`#D7DADF` in dark) which it used to be - the
     * design owner asked for flat white. Leave it hardcoded: swapping it back to a token would both
     * change the colour and make it theme-dependent, which is the opposite of what was asked for.
     *
     * This is the colour the tiles are *drawn* with. [IconGridPattern]'s radial mask still fades
     * them toward the edges of the pattern box, exactly as the export's own "Mask Overlay" layer
     * does - that is untouched.
     */
    val PatternColor: Color = Color(0xFFFFFFFF)

    /**
     * The Standard export's pattern glyph: a plain filled square, half its box across.
     *
     * Deliberately not in `IconSetEnum`. The export references its pattern by `sourceNodeId`
     * without including the node, and the render shows a square rather than any icon in the set —
     * it is decoration the designer drew, so it lives here with the component that uses it instead
     * of being promoted to the shared icon set. Favorites uses a real icon for its pattern
     * (`IconSetEnum.Favorite`) and needs nothing from here.
     */
    val PatternSquareIcon: ImageVector by lazy {
        ImageVector.Builder(
            name = "EmptyStatePatternSquare",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(fill = SolidColor(Color.Black)) {
                moveTo(6f, 6f)
                horizontalLineTo(18f)
                verticalLineTo(18f)
                horizontalLineTo(6f)
                close()
            }
        }.build()
    }

    val TitleStyle: TextStyle
        @Composable @ReadOnlyComposable
        get() = AutomotiveTheme.typography.display3Medium

    val DescriptionStyle: TextStyle
        @Composable @ReadOnlyComposable
        get() = AutomotiveTheme.typography.body3

    @Composable
    fun colors(
        container: Color = AutomotiveTheme.colorScheme.surfaceContainer,
        // NOTE: the export's glow is a hardcoded #2558DA with no token binding (one of its three
        // unbound paints), so `primary` here is an inference, not sourced. Confirm with the
        // designer before treating it as correct.
        glow: Color = AutomotiveTheme.colorScheme.primary,
        patternTint: Color = PatternColor,
        iconTint: Color = AutomotiveTheme.colorScheme.onSurface,
        title: Color = AutomotiveTheme.colorScheme.onSurface,
        description: Color = AutomotiveTheme.colorScheme.onSurfaceVariant
    ): EmptyStateColors = EmptyStateColors(
        container, glow, patternTint, iconTint, title, description
    )
}
