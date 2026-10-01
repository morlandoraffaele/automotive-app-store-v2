package org.radioplayer.automotive.designsystem.components.features.playbackbar

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import org.radioplayer.automotive.designsystem.components.composites.statusindicator.StatusIndicator
import org.radioplayer.automotive.designsystem.components.composites.statusindicator.StatusIndicatorColors
import org.radioplayer.automotive.designsystem.components.composites.statusindicator.StatusIndicatorContent
import org.radioplayer.automotive.designsystem.components.composites.statusindicator.StatusIndicatorDefaults
import org.radioplayer.automotive.designsystem.components.composites.statusindicator.StatusIndicatorSlots
import org.radioplayer.automotive.designsystem.components.primitives.Text
import org.radioplayer.automotive.designsystem.interaction.InteractionState
import org.radioplayer.automotive.designsystem.interaction.rememberFocusRingStroke
import org.radioplayer.automotive.designsystem.interaction.rememberInteractionState
import org.radioplayer.automotive.designsystem.theme.AutomotiveTheme

/** Same rule every other interactive composite in the system uses (see e.g. `ButtonFilled`,
 *  `ButtonText`) — only draw a ring while genuinely focused (rotary/keyboard), never on touch. */
@Composable
private fun resolveFocusBorder(interactionState: InteractionState): BorderStroke? {
    return if (interactionState.isFocused) {
        rememberFocusRingStroke()
    } else {
        null
    }
}

/**
 * The "Live" / "Go to Live" badge. Same text ("Live") in every state per the source design —
 * only color and clickability change.
 *
 * The pill itself is delegated to the design system's own [StatusIndicator] composite (so the
 * badge reuses the canonical pill look + the shared "Live"/secondary color schemes). Click
 * handling is layered here on top via a [clickable] modifier — with
 * a Button role and a "Go to live" onClickLabel for accessibility/rotary-select — keeping
 * [StatusIndicator] itself untouched (it has no onClick of its own). Focus is tracked and drawn
 * the same way as the rest of the design system: [rememberInteractionState] over the
 * [MutableInteractionSource] driving `clickable`, then [resolveFocusBorder] turns that into the
 * shared [rememberFocusRingStroke], applied as a border on [StatusIndicatorDefaults]'s own pill
 * shape so the ring hugs the badge exactly.
 *
 * Rendered inside the fixed [SideSlot] so the bar's trailing edge never shifts with content size.
 */
@Composable
internal fun StatusBadge(
    label: String,
    containerColor: Color,
    contentColor: Color,
    onClick: (() -> Unit)? = null
) {
//    val badgeModifier: Modifier = if (onClick != null) {
//        val interactionSource = remember { MutableInteractionSource() }
//        val interactionState by rememberInteractionState(interactionSource)
//        val resolvedBorder = resolveFocusBorder(interactionState)
//
//        Modifier
//            .clickable(
//                interactionSource = interactionSource,
//                indication = LocalIndication.current,
//                role = Role.Button,
//                onClickLabel = "Go to live",
//                onClick = onClick
//            )
//            .let { modifier ->
//                if (resolvedBorder != null) {
//                    modifier.border(resolvedBorder, StatusIndicatorDefaults.shape.default())
//                } else {
//                    modifier
//                }
//            }
//    } else {
//        Modifier
//    }

    var badgeModifier: Modifier = Modifier.fillMaxSize()

    if (onClick != null) {
        val interactionSource = remember { MutableInteractionSource() }
        val interactionState by rememberInteractionState(interactionSource)
        val resolvedBorder = resolveFocusBorder(interactionState)

        badgeModifier = badgeModifier
            .clickable(
                interactionSource = interactionSource,
                indication = LocalIndication.current,
                role = Role.Button,
                onClickLabel = "Go to live",
                onClick = onClick
            )
            .let { modifier ->
                if (resolvedBorder != null) {
                    modifier.border(resolvedBorder, StatusIndicatorDefaults.shape.default())
                } else {
                    modifier
                }
            }
    }

    SideSlot {
        StatusIndicator(
            modifier = badgeModifier,
            slots = StatusIndicatorSlots.Leading(
                StatusIndicatorContent.Custom(
                    layoutRole = StatusIndicatorContent.LayoutRole.TEXT,
                    accessibilityLabel = label,
                    content = {
                        Text(
                            text = label,
                            style = AutomotiveTheme.typography.body3
                        )
                    }
                )
            ),
            colors = StatusIndicatorColors(
                containerColor = containerColor,
                contentColor = contentColor
            )
        )
    }
}
