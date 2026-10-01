package org.radioplayer.automotive.designsystem.components.features.schedule.listitemschedule

import android.content.res.Configuration
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.radioplayer.automotive.designsystem.components.composites.listitem.ListItemState
import org.radioplayer.automotive.designsystem.components.composites.statusindicator.StatusIndicator
import org.radioplayer.automotive.designsystem.components.composites.statusindicator.StatusIndicatorContent
import org.radioplayer.automotive.designsystem.components.composites.statusindicator.StatusIndicatorDefaults
import org.radioplayer.automotive.designsystem.components.composites.statusindicator.StatusIndicatorSlots
import org.radioplayer.automotive.designsystem.components.primitives.Text
import org.radioplayer.automotive.designsystem.components.primitives.icon.Icon
import org.radioplayer.automotive.designsystem.interaction.InteractionState
import org.radioplayer.automotive.designsystem.interaction.rememberFocusRingStroke
import org.radioplayer.automotive.designsystem.interaction.rememberInteractionState
import org.radioplayer.automotive.designsystem.subsystems.Spaces
import org.radioplayer.automotive.designsystem.theme.AutomotiveTheme
import java.nio.file.WatchEvent

private val HorizontalSpaceContent = Spaces().large
private val HorizontalRowSpace = Spaces().medium
private val VerticalRowSpace = Spaces().medium
private val VerticalColumnContentSpace = Spaces().extraSmall


private fun resolveListItemState(
    enabled: Boolean,
    interactionState: InteractionState,
    active: Boolean,
): ListItemState = when {
    !enabled -> ListItemState.DISABLED
    active -> ListItemState.ACTIVE
    interactionState.isPressed -> ListItemState.PRESSED
    interactionState.isFocused -> ListItemState.FOCUSED
    else -> ListItemState.ENABLED
}

@Composable
private fun resolveBorderTreatment(
    interactionState: InteractionState,
    isLive: Boolean
): BorderStroke {
    return (if (interactionState.isFocused) {
        rememberFocusRingStroke()
    } else if (isLive) {
        BorderStroke(2.dp, AutomotiveTheme.colorScheme.outlineVariant)
    } else {
        BorderStroke(2.dp, Color.Transparent)
    })
}


@Composable
private fun ScheduleTrailingContent(
    isLive: Boolean
) =
    if (isLive) {
        StatusIndicator(
            modifier = Modifier,
            slots = StatusIndicatorSlots.Leading(
                content = StatusIndicatorContent.Custom(
                    layoutRole = StatusIndicatorContent.LayoutRole.TEXT,
                    accessibilityLabel = "",
                    content = { Text("LIVE", style = AutomotiveTheme.typography.body3) }
                )
            ),
            colors = StatusIndicatorDefaults.colors.live.emphasisHigh()
        )
    } else {
        // TODO: add play/pause button
    }


fun resolveAlpha(enabled: Boolean) = if (enabled) 1f else 0.38f

@Composable
private fun ListItemLayout(
    modifier: Modifier = Modifier,
    state: ListItemState,
    headlineContent: @Composable () -> Unit,
    supportingContent: @Composable (() -> Unit)?,
    leadingContent: @Composable (() -> Unit)?,
    trailingContent: @Composable (() -> Unit)?,
    switchLinePositionContent: Boolean,
    colors: ListItemScheduleColors
) {

    Row(
        modifier = modifier
            .padding(HorizontalRowSpace, VerticalRowSpace),
        horizontalArrangement = Arrangement.spacedBy(HorizontalSpaceContent),
        verticalAlignment = Alignment.CenterVertically
    ) {

        leadingContent?.invoke()

        Column(modifier = Modifier.weight(1f).padding(0.dp,VerticalColumnContentSpace)) {
            val decoratedHeadline: @Composable () -> Unit = headlineContent.let { content ->
                {
                    CompositionLocalProvider(
                        LocalContentColor provides colors.headlineColor
                    ) {
                        ProvideTextStyle(AutomotiveTheme.typography.body3) {
                            content()
                        }
                    }
                }
            }
            val decoratedSupporting: (@Composable () -> Unit)? = supportingContent?.let { content ->
                {
                    CompositionLocalProvider(
                        LocalContentColor provides colors.supportingColor
                    ) {
                        ProvideTextStyle(AutomotiveTheme.typography.body1Medium) {
                            content()
                        }
                    }
                }
            }

            if (switchLinePositionContent) {
                decoratedSupporting?.invoke()
                decoratedHeadline()
            } else {
                decoratedHeadline()
                decoratedSupporting?.invoke()
            }
        }

        trailingContent?.invoke()
    }
}

/**
 * A specialized schedule list item component tailored for automotive UI surfaces.
 *
 * Displays broadcast/schedule details such as broadcast time, show titles, artwork,
 * and optional status indicators (e.g., "LIVE"). Handles various interaction states
 * (focused, pressed, disabled, active) seamlessly within the Automotive theme system.
 *
 * @param isLive Indicates whether this schedule item represents a currently broadcasting show.
 * When `true`, displays a "LIVE" status indicator badge in the trailing slot.
 * @param onClick Callback triggered when the user interacts with or selects this list item.
 * @param modifier The [Modifier] applied to the outer layout container.
 * @param enabled Controls user interaction response and visual opacity. When `false`, the component
 * is dimmed and non-interactive.
 * @param active Indicates whether the item is in an active/highlighted state.
 * @param headlineContent Primary composable slot, usually containing time details (e.g., "14:00").
 * @param supportingContent Optional secondary composable slot, usually containing show or station titles.
 * @param leadingContent Optional composable slot positioned at the start, often used for show thumbnails or icons.
 * @param colors Styling configurations for surface, text, and outline colors across different component states.
 * @param switchLinePositionContent When `true`, reverses the vertical ordering of [headlineContent] and [supportingContent].
 * @param horizontalDividerEnabled Displays a horizontal separator line underneath the schedule item when `true`.
 * @param interactionSource Optional custom [MutableInteractionSource] to observe or dispatch interaction states.
 */
@Composable
fun ListItemSchedule(
    modifier: Modifier = Modifier,
    isLive: Boolean,
    onClick: () -> Unit,
    enabled: Boolean = true,
    active: Boolean = false,
    headlineContent: @Composable () -> Unit,
    supportingContent: @Composable (() -> Unit)? = null,
    leadingContent: @Composable (() -> Unit)? = null,
    colors: ListItemScheduleColors = ListItemScheduleDefaults.colors(),
    switchLinePositionContent: Boolean = false,
    horizontalDividerEnabled: Boolean = false,
    interactionSource: MutableInteractionSource? = null,
) {

    val interactionSource = interactionSource ?: remember { MutableInteractionSource() }
    val interactionState by rememberInteractionState(interactionSource)

    val listItemState = remember(enabled, interactionState, active) {
        resolveListItemState(
            enabled = enabled,
            interactionState = interactionState,
            active = active
        )
    }

    val border = resolveBorderTreatment(interactionState, isLive)

    val alpha = resolveAlpha(enabled)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .alpha(alpha)
    ) {
        Surface(
            modifier = modifier,
            onClick = onClick,
            enabled = enabled,
            color = colors.containerColor(isLive,listItemState),
            contentColor = colors.contentColor,
            shape = RoundedCornerShape(AutomotiveTheme.measurement.shapes.largeIncreased),
            border = border,
            interactionSource = interactionSource
        ) {
            ListItemLayout(
                state = listItemState,
                headlineContent = headlineContent,
                supportingContent = supportingContent,
                leadingContent = leadingContent,
                trailingContent = { ScheduleTrailingContent(isLive) },
                switchLinePositionContent = switchLinePositionContent,
                colors = colors
            )
        }

        if (horizontalDividerEnabled) {
            HorizontalDivider(
                modifier = Modifier.fillMaxWidth(),
                thickness = 1.dp,
                color = colors.dividerColor
            )
        }
    }
}

@Preview(
    name = "ListItemSchedule - Light Mode",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_NO or Configuration.UI_MODE_TYPE_CAR,
    widthDp = 1200
)

@Preview(
    name = "ListItemSchedule - Dark Mode",
    showBackground = false,
    uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_CAR,
    widthDp = 1200
)

@Composable
fun ListItemSchedulePreview(
) {
    AutomotiveTheme() {
        Column(
            modifier = Modifier
                .background(AutomotiveTheme.colorScheme.surface)
                .padding(0.dp, 20.dp)
        ) {
            ListItemSchedule(
                modifier = Modifier,
                onClick = {},
                headlineContent = {
                    Text("hh:mm")
                },
                supportingContent = {
                    Text("Programme Show Name")
                },
                leadingContent = {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(AutomotiveTheme.measurement.shapes.medium))
                            .background(Color.Gray)
                            .size(112.dp, 112.dp)
                    )
                },
                isLive = true,
                enabled = true
            )
        }
    }
}