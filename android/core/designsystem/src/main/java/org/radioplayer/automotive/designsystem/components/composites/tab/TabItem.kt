package org.radioplayer.automotive.designsystem.components.composites.tab

import android.content.res.Configuration
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Tab
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.radioplayer.automotive.designsystem.components.primitives.Text
import org.radioplayer.automotive.designsystem.components.primitives.icon.Icon
import org.radioplayer.automotive.designsystem.components.primitives.icon.IconSetEnum
import org.radioplayer.automotive.designsystem.interaction.InteractionState
import org.radioplayer.automotive.designsystem.interaction.rememberFocusRingStroke
import org.radioplayer.automotive.designsystem.interaction.rememberInteractionState
import org.radioplayer.automotive.designsystem.theme.AutomotiveTheme

@Composable
private fun resolveBorderTreatment(
    interactionState: InteractionState,
    default: BorderStroke = BorderStroke(width = 0.dp, color = Color.Unspecified)
): BorderStroke {
    return if (interactionState.isFocused) {
        rememberFocusRingStroke()
    } else {
        default
    }
}

private fun resolveAlpha(enabled: Boolean) =
    if (enabled) 1F else 0.38F // TODO: Alpha value should come from AutomotiveTheme

/**
 * Represents an individual tab item within a tab row or group. Designed for automotive surfaces
 * with built-in support for interaction states (such as rotary knob or D-pad focus indicators).
 *
 * @param tabContent The data container holding the tab's slot contents (text, icon, flag).
 * @param selected Whether this tab is currently in a selected state.
 * @param onClick Callback invoked when this tab is selected or clicked.
 * @param modifier The [Modifier] to be applied to the tab container.
 * @param enabled Controls the enabled state of the tab. When `false`, the tab is non-interactive and dimmed.
 * @param interactionSource An optional [MutableInteractionSource] for tracking interaction and focus state.
 */
@Composable
fun TabItem(
    tabContent: TabContent,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    // selectedContentColor: Color = LocalContentColor.current,
    // unselectedContentColor: Color = selectedContentColor,
    interactionSource: MutableInteractionSource? = null
) {

    val actualInteractionSource = interactionSource ?: remember { MutableInteractionSource() }
    val interactionState by rememberInteractionState(actualInteractionSource)

    val resolvedBorder = resolveBorderTreatment(interactionState)

    val alpha = resolveAlpha(enabled)

    Tab(
        modifier = modifier
            .border(
                border = resolvedBorder,
                shape = RoundedCornerShape(AutomotiveTheme.measurement.shapes.large)
            )
            .alpha(alpha)
            .semantics { role = Role.Tab },
        selected = selected,
        onClick = onClick,
        enabled = enabled,
        // selectedContentColor = selectedContentColor,
        // unselectedContentColor = unselectedContentColor,
        interactionSource = actualInteractionSource,
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(AutomotiveTheme.measurement.spaces.extraExtraSmall),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (tabContent.flag != null) {
                Box(
                    modifier = Modifier.defaultMinSize(minHeight = AutomotiveTheme.measurement.sizes.minTapArea),
                    contentAlignment = Alignment.Center
                ) {
                    tabContent.flag.invoke()
                }
            }

            if (tabContent.icon != null) {
                Box(
                    modifier = Modifier
                        .defaultMinSize(
                            minWidth = AutomotiveTheme.measurement.sizes.minTapArea,
                            minHeight = AutomotiveTheme.measurement.sizes.minTapArea,
                        )
                        .widthIn(max = AutomotiveTheme.measurement.sizes.minTapArea),
                    contentAlignment = Alignment.Center
                ) {
                    tabContent.icon.invoke()
                }

            }

            ProvideTextStyle(value = AutomotiveTheme.typography.body2Medium) {
                tabContent.text()
            }
        }

    }
}

@Preview(
    name = "TabItem - Light Mode",
    showBackground = true,
    widthDp = 1000,
    uiMode = Configuration.UI_MODE_NIGHT_NO or Configuration.UI_MODE_TYPE_CAR,
)

@Preview(
    name = "TabItem - Dark Mode",
    showBackground = false,
    widthDp = 1000,
    uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_CAR,
)

@Composable
private fun TabItemPreview() {
    AutomotiveTheme() {
        TabItem(
            tabContent = TabContent(
                text = { Text(text = "TabContent Item") },
                icon = { Icon(name = IconSetEnum.Droid) },
                flag = null
            ),
            selected = true,
            onClick = {},
            modifier = Modifier,
            enabled = true,
            interactionSource = null,
        )
    }
}