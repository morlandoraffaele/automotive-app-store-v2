package com.automotive.appstore.ui.components

import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.automotive.appstore.ui.theme.storeColors
import org.radioplayer.automotive.designsystem.components.composites.button.filled.ButtonFilled
import org.radioplayer.automotive.designsystem.components.composites.button.filled.ButtonFilledColors
import org.radioplayer.automotive.designsystem.components.composites.button.outline.ButtonOutline
import org.radioplayer.automotive.designsystem.components.composites.button.outline.ButtonOutlineColors
import org.radioplayer.automotive.designsystem.components.composites.button.text.ButtonText
import org.radioplayer.automotive.designsystem.components.composites.button.text.ButtonTextColors
import org.radioplayer.automotive.designsystem.components.composites.button.tonal.ButtonTonal
import org.radioplayer.automotive.designsystem.components.primitives.icon.Icon
import org.radioplayer.automotive.designsystem.components.primitives.icon.IconSource
import org.radioplayer.automotive.designsystem.theme.AutomotiveTheme

/** Visual weight of a [TouchButton], matching the `variant` prop of the web `TouchButton`. */
enum class TouchVariant { PRIMARY, SECONDARY, OUTLINE, GHOST, DESTRUCTIVE, WARNING }

/** Sizing intent, matching the `size` prop of the web `TouchButton`. */
enum class TouchSize {
    /** Content-sized, the default. */
    DEFAULT,

    /** Square-ish: equal min width and height, for icon-only buttons. */
    ICON,

    /** At least the design system's standard interaction width. */
    WIDE,
}

/**
 * Every interactive control in the store.
 *
 * The web app has a hand-rolled `TouchButton` whose variants map one-to-one onto the design
 * system's button family: `primary` → `ButtonFilled`, `secondary` → `ButtonTonal`, `outline`
 * → `ButtonOutline`, `ghost` → `ButtonText`. Routing everything through the design system
 * means the automotive minimum tap area (`sizes.minTapArea`, 76dp — the same 76px floor the
 * web app hard-codes) and the focus-ring behaviour come for free.
 */
@Composable
fun TouchButton(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    variant: TouchVariant = TouchVariant.PRIMARY,
    size: TouchSize = TouchSize.DEFAULT,
    enabled: Boolean = true,
) {
    val iconSlot: (@Composable () -> Unit)? = icon?.let { vector ->
        {
            Icon(
                source = IconSource.Vector(vector),
                size = AutomotiveTheme.icon.primary,
                contentDescription = null,
            )
        }
    }
    val sized = modifier.then(touchSizing(size))
    val colors = storeColors

    when (variant) {
        TouchVariant.PRIMARY -> ButtonFilled(
            onClick = onClick,
            modifier = sized,
            label = label,
            icon = iconSlot,
            enabled = enabled,
        )

        TouchVariant.SECONDARY -> ButtonTonal(
            onClick = onClick,
            modifier = sized,
            label = label,
            icon = iconSlot,
            enabled = enabled,
        )

        TouchVariant.OUTLINE -> ButtonOutline(
            onClick = onClick,
            modifier = sized,
            label = label,
            icon = iconSlot,
            enabled = enabled,
            colors = ButtonOutlineColors(
                containerColor = Color.Transparent,
                contentColor = colors.foreground,
            ),
        )

        TouchVariant.GHOST -> ButtonText(
            onClick = onClick,
            modifier = sized,
            label = label,
            icon = iconSlot,
            enabled = enabled,
            colors = ButtonTextColors(
                containerColor = Color.Transparent,
                contentColor = colors.foreground,
            ),
        )

        TouchVariant.DESTRUCTIVE -> ButtonFilled(
            onClick = onClick,
            modifier = sized,
            label = label,
            icon = iconSlot,
            enabled = enabled,
            colors = ButtonFilledColors(
                containerColor = colors.destructive,
                contentColor = colors.destructiveForeground,
            ),
        )

        TouchVariant.WARNING -> ButtonFilled(
            onClick = onClick,
            modifier = sized,
            label = label,
            icon = iconSlot,
            enabled = enabled,
            colors = ButtonFilledColors(
                containerColor = colors.warning,
                contentColor = colors.warningForeground,
            ),
        )
    }
}

/**
 * Applies the design system minimum touch target plus the requested width intent.
 *
 * This mirrors the `min-h-19` (76px) and `min-w-19` classes on the web `TouchButton`; the
 * design system's own buttons already floor themselves at `minTapArea`, so this only adds
 * the extra width constraints the `size` variants ask for.
 */
@Composable
private fun touchSizing(size: TouchSize): Modifier {
    val minTap = AutomotiveTheme.measurement.sizes.minTapArea
    val minWidth = AutomotiveTheme.measurement.sizes.minInteractionWidth
    return when (size) {
        TouchSize.DEFAULT -> Modifier.defaultMinSize(minHeight = minTap)
        TouchSize.ICON -> Modifier.defaultMinSize(minWidth = minTap, minHeight = minTap)
        TouchSize.WIDE -> Modifier.defaultMinSize(minWidth = minWidth, minHeight = minTap)
    }
}
