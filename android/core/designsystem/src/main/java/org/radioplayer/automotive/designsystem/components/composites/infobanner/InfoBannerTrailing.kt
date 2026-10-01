package org.radioplayer.automotive.designsystem.components.composites.infobanner

import androidx.compose.runtime.Composable
import org.radioplayer.automotive.designsystem.components.primitives.icon.IconSetEnum

/**
 * The content [InfoBanner] can show in its trailing slot. Modelling it as a sealed type rather
 * than a bare `@Composable` slot lets the banner render each variant with the right sizing,
 * tinting and semantics: a passive icon is laid out at [InfoBannerDefaults.IconSize] and merged
 * into the banner's announcement, while an action is rendered as an icon button with the theme's
 * minimum tap area and its own accessibility node.
 *
 * Follows the same pattern as
 * [org.radioplayer.automotive.designsystem.components.composites.header.rail.HeaderRailTrailing].
 */
sealed interface InfoBannerTrailing {

    /**
     * A passive icon, rendered at [InfoBannerDefaults.IconSize]. Decorative by default — the
     * banner's merged semantics announce it without a label. Pass a [contentDescription] only if
     * the icon conveys information the banner message does not.
     */
    data class Icon(
        val name: IconSetEnum,
        val contentDescription: String? = null,
    ) : InfoBannerTrailing

    /**
     * An interactive affordance (e.g. a banner dismissal), rendered as an icon button so the
     * theme's minimum tap area, focus ring and disabled-alpha behaviour are enforced by the
     * design system instead of the call site. The button is tinted with the banner's content
     * color so it visually belongs to the banner.
     *
     * @param contentDescription announced by the button's own accessibility node — required
     *   because a bare icon carries no meaning to a screen reader once it becomes clickable.
     */
    data class Action(
        val onClick: () -> Unit,
        val name: IconSetEnum,
        val contentDescription: String,
        val enabled: Boolean = true,
    ) : InfoBannerTrailing

    /**
     * Escape hatch for trailing content the banner does not model, matching the
     * `Custom` variant of
     * [org.radioplayer.automotive.designsystem.components.composites.statusindicator.StatusIndicatorContent].
     * Prefer [Icon] or [Action] where possible — custom content bypasses the banner's sizing and
     * semantics guarantees.
     */
    data class Custom(
        val content: @Composable () -> Unit,
    ) : InfoBannerTrailing
}