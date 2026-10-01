package org.radioplayer.automotive.designsystem.components.composites.listitem.toggle

import android.content.res.Configuration
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.radioplayer.automotive.designsystem.components.composites.listitem.ListItemState
import org.radioplayer.automotive.designsystem.components.primitives.Text
import org.radioplayer.automotive.designsystem.components.primitives.icon.Icon
import org.radioplayer.automotive.designsystem.interaction.InteractionState
import org.radioplayer.automotive.designsystem.interaction.rememberFocusRingStroke
import org.radioplayer.automotive.designsystem.interaction.rememberInteractionState
import org.radioplayer.automotive.designsystem.subsystems.Spaces
import org.radioplayer.automotive.designsystem.theme.AutomotiveTheme

private val HorizontalSpaceContent = Spaces().large
private val HorizontalRowSpace = Spaces().medium
private val VerticalRowSpace = Spaces().medium

private fun resolveListItemState(
    enabled: Boolean,
    interactionState: InteractionState,
    selected: Boolean,
): ListItemState = when {
    !enabled -> ListItemState.DISABLED
    selected -> ListItemState.SELECTED
    interactionState.isPressed -> ListItemState.PRESSED
    interactionState.isFocused -> ListItemState.FOCUSED
    else -> ListItemState.ENABLED
}

@Composable
private fun resolveBorderTreatment(
    interactionState: InteractionState
): BorderStroke {
    return if (interactionState.isFocused) {
        rememberFocusRingStroke()
    } else {
        BorderStroke(0.dp, Color.Transparent)
    }
}

@Composable
private fun ListItemLayout(
    modifier: Modifier = Modifier,
    state: ListItemState,
    headlineContent: @Composable () -> Unit,
    leadingContent: @Composable (() -> Unit)?,
    trailingContent: @Composable (() -> Unit)?,
    colors: ListItemToggleColors
) {

    Row(
        modifier = modifier
            .padding(HorizontalRowSpace, VerticalRowSpace),
        horizontalArrangement = Arrangement.spacedBy(HorizontalSpaceContent),
        verticalAlignment = Alignment.CenterVertically
    ) {

        leadingContent?.invoke()

        Column(modifier = Modifier.weight(1f)) {
            val decoratedHeadline: @Composable () -> Unit = headlineContent.let { content ->
                {
                    CompositionLocalProvider(
                        LocalContentColor provides colors.headlineColor(state)
                    ) {
                        ProvideTextStyle(AutomotiveTheme.typography.body1Medium) {
                            content()
                        }
                    }
                }
            }

            decoratedHeadline()
        }

        trailingContent?.invoke()
    }
}

/**
 * An interactive list item composite designed for automotive UI environments.
 * Supports selected, focused, pressed, and disabled states alongside optional icons
 * and bottom dividers.
 *
 * @param onClick Callback to be invoked when this list item is clicked/tapped.
 * @param modifier Optional [Modifier] to be applied to the root layout container.
 * @param selected Whether this list item is currently selected. Defaults to `false`.
 * @param enabled Controls the enabled state of the list item. When `false`, interaction
 * is disabled and the item renders in a disabled state visual treatment.
 * @param headlineContent The main text or custom content composable displayed in the center.
 * @param leadingContent Optional composable slot displayed at the start of the item (e.g., Icon/Image).
 * @param trailingContent Optional composable slot displayed at the end of the item (e.g., Switch/Checkbox/Icon).
 * @param colors The color specs used to resolve container, content, headline, and divider colors
 * across different [ListItemState] values.
 * @param horizontalDividerEnabled Controls whether a bottom [HorizontalDivider] is rendered below the item.
 * @param interactionSource Optional [MutableInteractionSource] to represent and observe the stream of interactions.
 */
@Composable
fun ListItemToggle(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    selected: Boolean = false,
    enabled: Boolean = true,
    headlineContent: @Composable () -> Unit,
    leadingContent: @Composable (() -> Unit)? = null,
    trailingContent: @Composable (() -> Unit)? = null,
    colors: ListItemToggleColors = ListItemToggleDefaults.colors(),
    horizontalDividerEnabled: Boolean = true,
    interactionSource: MutableInteractionSource? = null,
) {
    val interactionSource = interactionSource ?: remember { MutableInteractionSource() }
    val interactionState by rememberInteractionState(interactionSource)

    val listItemState = remember(enabled, interactionState,selected) {
        resolveListItemState(
            enabled = enabled,
            interactionState = interactionState,
            selected = selected
        )
    }

    val border = resolveBorderTreatment(interactionState)

    Column(modifier = Modifier.fillMaxWidth()) {
        Surface(
            modifier = modifier,
            onClick = onClick,
            enabled = enabled,
            color = colors.containerColor(listItemState),
            contentColor = colors.contentColor(listItemState),
            shape = RoundedCornerShape(AutomotiveTheme.measurement.shapes.largeIncreased),
            border = border,
            interactionSource = interactionSource
        ) {
            ListItemLayout(
                state = listItemState,
                headlineContent = headlineContent,
                leadingContent = leadingContent,
                trailingContent = trailingContent,
                colors = colors
            )
        }

        if (horizontalDividerEnabled) {
            HorizontalDivider(
                modifier = Modifier
                    .fillMaxWidth(),
                thickness = 1.dp,
                color = colors.dividerColor
            )
        }
    }
}

@Preview(
    name = "ListItemToggle - Light Mode",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_NO or Configuration.UI_MODE_TYPE_CAR,
    widthDp = 1200
)

@Preview(
    name = "ListItemToggle - Dark Mode",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_CAR,
    widthDp = 1200
)

@Composable
fun ListItemTogglePreview(
) {
    val backgroundColor = if (isSystemInDarkTheme()) Color.Black else Color.White

    AutomotiveTheme() {
        Column(modifier = Modifier.background(backgroundColor)) {
            ListItemToggle(
                modifier = Modifier,
                onClick = {},
                headlineContent = {
                    Text(
                        "Headline Text"
                    )
                },
                leadingContent = { Icon() },
                trailingContent = { Icon(size = AutomotiveTheme.icon.primary) },
                selected = true,
            )
        }
    }
}