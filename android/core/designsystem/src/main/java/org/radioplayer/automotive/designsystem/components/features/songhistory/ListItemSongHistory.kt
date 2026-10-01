package org.radioplayer.automotive.designsystem.components.features.songhistory

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
): BorderStroke {
    return if (interactionState.isFocused) {
        rememberFocusRingStroke()
    } else {
        BorderStroke(2.dp, Color.Transparent)
    }
}

fun resolveAlpha(enabled: Boolean) = if (enabled) 1f else 0.38f

@Composable
private fun ListItemLayout(
    modifier: Modifier = Modifier,
    state: ListItemState,
    headlineContent: @Composable () -> Unit,
    supportingContent: @Composable (() -> Unit)?,
    extraContent: @Composable (() -> Unit)?,
    leadingContent: @Composable (() -> Unit)?,
    trailingContent: @Composable (() -> Unit)?,
    switchLinePositionContent: Boolean,
    colors: ListItemSongHistoryColors
) {

    Row(
        modifier = modifier
            .padding(HorizontalRowSpace, VerticalRowSpace),
        horizontalArrangement = Arrangement.spacedBy(HorizontalSpaceContent),
        verticalAlignment = Alignment.CenterVertically
    ) {

        leadingContent?.invoke()

        Column(
            modifier = Modifier.weight(1f)
        ) {
            val decoratedHeadline: @Composable () -> Unit = headlineContent.let { content ->
                {
                    CompositionLocalProvider(
                        LocalContentColor provides colors.headlineColor
                    ) {
                        ProvideTextStyle(AutomotiveTheme.typography.body1Medium) {
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
                        ProvideTextStyle(AutomotiveTheme.typography.body3) {
                            content()
                        }
                    }
                }
            }

            val decoratedExtra: (@Composable () -> Unit)? = extraContent?.let { content ->
                {
                    CompositionLocalProvider(
                        LocalContentColor provides colors.extraColor
                    ) {
                        ProvideTextStyle(AutomotiveTheme.typography.sub1) {
                            content()
                        }
                    }
                }
            }


            decoratedHeadline()
            decoratedSupporting?.invoke()
            decoratedExtra?.invoke()
        }

        trailingContent?.invoke()
    }
}

/**
 * A specialized list item composable tailored for displaying song history items in an automotive UI context.
 *
 * Provides dedicated slots for displaying song titles, artist/album metadata, playback timestamps, leading artwork,
 * and trailing control icons, while managing automotive-specific focus and interaction states.
 *
 * @param onClick Callback invoked when this list item is clicked/selected.
 * @param modifier The [Modifier] to be applied to the container surface.
 * @param enabled Controls whether the list item is interactive. When `false`, the item cannot be clicked and is rendered with lowered opacity.
 * @param active Represents whether this song entry is currently active or playing.
 * @param headlineContent Primary slot for main text content (e.g., song title).
 * @param supportingContent Optional slot for secondary information below the headline (e.g., artist name).
 * @param extraContent Optional slot for tertiary details below the supporting text (e.g., played time timestamp "hh:mm").
 * @param leadingContent Optional slot positioned at the start of the item (e.g., album cover thumbnail).
 * @param trailingContent Optional slot positioned at the end of the item (e.g., favorite icon, menu trigger).
 * @param colors The set of colors used to style the item states and text elements. Defaults to [ListItemSongHistoryDefaults.colors].
 * @param switchLinePositionContent Optional flag to alter internal text line layout placement.
 * @param horizontalDividerEnabled When `true`, renders a horizontal divider line beneath the list item.
 * @param interactionSource The [MutableInteractionSource] tracking touch, focus, and press events.
 */
@Composable
fun ListItemSongHistory(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    enabled: Boolean = true,
    active: Boolean = false,
    headlineContent: @Composable () -> Unit,
    supportingContent: @Composable (() -> Unit)? = null,
    extraContent: @Composable (() -> Unit)? = null,
    leadingContent: @Composable (() -> Unit)? = null,
    trailingContent: @Composable (() -> Unit)? = null,
    colors: ListItemSongHistoryColors = ListItemSongHistoryDefaults.colors(),
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

    val border = resolveBorderTreatment(interactionState)

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
            color = colors.containerColor(listItemState),
            contentColor = colors.contentColor,
            shape = RoundedCornerShape(AutomotiveTheme.measurement.shapes.largeIncreased),
            border = border,
            interactionSource = interactionSource
        ) {
            ListItemLayout(
                state = listItemState,
                headlineContent = headlineContent,
                supportingContent = supportingContent,
                extraContent = extraContent,
                leadingContent = leadingContent,
                trailingContent = trailingContent,
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
    name = "ListItemSongHistory - Light Mode",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_NO or Configuration.UI_MODE_TYPE_CAR,
    widthDp = 1200
)

@Preview(
    name = "ListItemSongHistory - Dark Mode",
    showBackground = false,
    uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_CAR,
    widthDp = 1200
)

@Composable
fun ListItemSongHistoryPreview(
) {
    AutomotiveTheme() {
        Column(
            modifier = Modifier
                .background(AutomotiveTheme.colorScheme.surface)
                .padding(0.dp, 20.dp)
        ) {
            ListItemSongHistory(
                modifier = Modifier,
                onClick = { },
                headlineContent = { Text("Song Name") },
                supportingContent = { Text("Artist Name") },
                extraContent = { Text("hh:mm") },
                leadingContent = {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(AutomotiveTheme.measurement.shapes.medium))
                            .background(Color.Gray)
                            .size(112.dp, 112.dp)
                    )
                },
                trailingContent = { Icon(size = AutomotiveTheme.icon.primary) },
            )
        }
    }
}
