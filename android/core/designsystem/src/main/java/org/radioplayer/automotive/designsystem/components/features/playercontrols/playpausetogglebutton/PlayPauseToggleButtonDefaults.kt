package org.radioplayer.automotive.designsystem.components.features.playercontrols.playpausetogglebutton

import androidx.compose.foundation.BorderStroke
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import org.radioplayer.automotive.designsystem.components.composites.iconbutton.standard.toggle.IconButtonToggleStandardColors
import org.radioplayer.automotive.designsystem.theme.AutomotiveTheme

object PlayPauseToggleButtonDefaults {
    const val checked: Boolean = false
    const val enabled: Boolean = true
    const val buffering: Boolean = false
    val content = PlayPauseToggleButtonContent.OnDemand

    private const val DisabledContentAlpha = 0.38f

    /**
     * The export's own auto-layout is `padding: 16` around a `64×64` (`icon.hero`) icon, HUG-sized
     * to `96×96` — there's no existing shared measurement token that already equals 96dp (this
     * design system's `minTapArea` is 76dp, used everywhere else), so this derives the size from
     * the actual layout math instead of a new magic-number constant: icon size + padding on both
     * sides, matching the source auto-layout exactly rather than a token that only numerically
     * happens to also be 96 (e.g. `spaces.extraExtraLarge`, which is a *spacing* token, not a
     * *size* one, and reusing it here would be a coincidence, not a real relationship).
     */
    @Composable
    fun containerSize(): Dp = AutomotiveTheme.icon.hero + AutomotiveTheme.measurement.spaces.medium * 2

    /**
     * Icon tint, resolved from the export's `instance.overrides` vector-fill diffs (see
     * `PLAY_PAUSE_TOGGLE_BUTTON.md` §3 — these aren't covered by the exporter's usual
     * `resolvedColor` attachment, so this was walked by hand via the bound variable's own
     * `sys/color/...` name):
     * - Unchecked → `sys/color/On Surface Variant` (`onSurfaceVariant`), sitting on the resting
     *   `Surface Container` background.
     * - Checked → `sys/color/On Primary` (`onPrimary`), sitting on the `Primary` fill.
     * - Disabled (either checked state) → collapses to a single neutral `sys/color/On Surface`
     *   (`onSurface`) rather than a dimmed version of the enabled tint - confirmed by both
     *   `Selected=True, State=Disabled` and `Selected=False, State=Disabled` resolving their
     *   icon's bound variable to the exact same id. Not something `ButtonColorsExtended`'s single
     *   shared `disabledContentColor` field could express per-checked-state even if this used it
     *   (see [colors]) - moot here anyway, since it's the same tint for both.
     */
    @Composable
    fun iconTint(checked: Boolean, enabled: Boolean): Color {
        if (!enabled) return AutomotiveTheme.colorScheme.onSurface.copy(alpha = DisabledContentAlpha)
        return if (checked) AutomotiveTheme.colorScheme.onPrimary else AutomotiveTheme.colorScheme.onSurfaceVariant
    }

    /**
     * Container fill: `Surface Container` resting (unchecked), `Primary` when checked - but
     * `Disabled` drops the fill entirely for *both* checked states (confirmed: neither Disabled
     * variant has any `fills` entry on its own root node, unlike Enabled/Focused/Pressed). A
     * disabled `Selected=True` button doesn't keep looking "filled" the way a disabled button
     * elsewhere in this design system might - it goes fully bare except for the dimmed icon. Maps
     * cleanly onto [ButtonColorsExtended]/Material3's own `IconToggleButtonColors` as-is - unlike
     * [iconTint], this doesn't need hand-rolled checked+enabled branching because Material3 only
     * has one `disabledContainerColor` slot to begin with (shared across both checked states),
     * which is exactly what the export needs here anyway.
     */
    @Composable
    fun colors(): IconButtonToggleStandardColors = IconButtonToggleStandardColors(
        unselectedContainerColor = AutomotiveTheme.colorScheme.surfaceContainer,
        unselectedContentColor = AutomotiveTheme.colorScheme.onSurfaceVariant,
        selectedContainerColor = AutomotiveTheme.colorScheme.primary,
        selectedContentColor = AutomotiveTheme.colorScheme.onPrimary,
    )

    /**
     * Border only when unchecked (`sys/color/Outline Variant`, `strokeWeight: 2` = [AutomotiveTheme
     * .stroke.thin]) - present in `Enabled`, `Pressed`, *and* `Disabled` (still visible at reduced
     * opacity), absent whenever `Selected=True`. Not gated on `enabled` - disabled-but-unchecked
     * still shows this border in the export, it's only the fill/icon that respond to disabled.
     * Applied by [PlayPauseToggleButton] itself as a `Modifier.border` around the shared
     * `IconButtonToggleStandard` (which has no built-in permanent-border param of its own) - the
     * `Focused (Rotary)` ring still renders on top via that shared component's own internal
     * `rememberFocusRingStroke` plumbing, matching the export (the ordinary stroke disappears
     * whenever the focus ring is showing).
     */
    @Composable
    fun border(checked: Boolean): BorderStroke? {
        if (checked) return null
        return BorderStroke(AutomotiveTheme.stroke.thin, AutomotiveTheme.colorScheme.outlineVariant)
    }

    /** `.SupportOverlays / State-Layers / Focused-Rotary` resolves its stroke to `sys/color/Primary`. */
    @Composable
    fun focusRingColor(): Color = AutomotiveTheme.colorScheme.primary

    /**
     * `40dp` — Material 3's own `Circular-indeterminate progress indicator` component, `Type=
     * Flat, Thickness=4dp` variant (exported directly from `m3.material.io/components/progress-
     * indicators/overview`'s reference file), not derived from any of this design system's own
     * size tokens — none of them happen to equal 40dp, and this is a literal upstream spec value,
     * not a coincidental match worth reusing a differently-named token for (same reasoning as
     * [containerSize] not reusing `spaces.extraExtraLarge` just because it numerically matches).
     * Centered in the button, replacing the icon's position while [buffering][bufferingRingColor]
     * is active — not stretched to the button's own edge.
     */
    val BufferingRingSize: Dp = 40.dp

    /**
     * Buffering ring color — not a flat [AutomotiveTheme.colorScheme.primary] in both checked
     * states, despite that being the literal design ask: the checked container is *already*
     * `Primary`-filled (see [colors]), so a `Primary` ring on a `Primary` background would be
     * invisible. Mirrors [iconTint]'s own checked-conditional resolution instead — `onPrimary`
     * (contrasts against the checked container's primary fill), `primary` (contrasts against the
     * unchecked container's neutral `surfaceContainer` fill). See `PLAY_PAUSE_TOGGLE_BUTTON.md`
     * §7 for the full reasoning.
     */
    @Composable
    fun bufferingRingColor(checked: Boolean): Color =
        if (checked) AutomotiveTheme.colorScheme.onPrimary else AutomotiveTheme.colorScheme.primary
}
