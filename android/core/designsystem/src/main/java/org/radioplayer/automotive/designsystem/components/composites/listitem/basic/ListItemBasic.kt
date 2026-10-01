package org.radioplayer.automotive.designsystem.components.composites.listitem.basic

import android.content.res.Configuration
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.radioplayer.automotive.designsystem.components.composites.listitem.ListItemState
import org.radioplayer.automotive.designsystem.components.primitives.Text
import org.radioplayer.automotive.designsystem.components.primitives.icon.Icon
import org.radioplayer.automotive.designsystem.components.primitives.icon.IconSetEnum
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
    interactionState: InteractionState
): BorderStroke {
    return if (interactionState.isFocused) {
        rememberFocusRingStroke()
    } else {
        BorderStroke(0.dp, Color.Transparent)
    }
}

/**
 * Dims whatever sibling content sizes this [Box] behind a scrim with a centered icon - the
 * [ListItemBasic] equivalent of `CardContent`'s `OverlayWithIcon`. Uses `matchParentSize` rather
 * than `fillMaxSize` since leading content (an icon, a small thumbnail, ...) is usually
 * wrap-content sized, not driven by an already-fixed parent like a card's artwork tile.
 */
@Composable
private fun BoxScope.LeadingOverlayWithIcon(
    name: IconSetEnum,
) {
    Box(
        modifier = Modifier
            .matchParentSize()
            .clip(RoundedCornerShape(AutomotiveTheme.measurement.shapes.medium))
            .background(AutomotiveTheme.colorScheme.scrim.copy(alpha = 0.8f)),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            size = AutomotiveTheme.icon.macro,
            name = name,
        )
    }
}

@Composable
private fun ListItemLayout(
    modifier: Modifier = Modifier,
    state: ListItemState,
    headlineContent: @Composable () -> Unit,
    supportingContent: @Composable (() -> Unit)?,
    leadingContent: @Composable (() -> Unit)?,
    leadingOverlayIconName: IconSetEnum?,
    trailingContent: @Composable (() -> Unit)?,
    switchLinePositionContent: Boolean,
    colors: ListItemBasicColors
) {

    Row(
        modifier = modifier
            .padding(HorizontalRowSpace, VerticalRowSpace),
        horizontalArrangement = Arrangement.spacedBy(HorizontalSpaceContent),
        verticalAlignment = Alignment.CenterVertically
    ) {

        if (leadingContent != null) {
            Box {
                leadingContent()

                if (state == ListItemState.DISABLED && leadingOverlayIconName != null) {
                    LeadingOverlayWithIcon(name = leadingOverlayIconName)
                }
            }
        }

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
            val decoratedSupporting: (@Composable () -> Unit)? = supportingContent?.let { content ->
                {
                    CompositionLocalProvider(
                        LocalContentColor provides colors.supportingColor(state)
                    ) {
                        ProvideTextStyle(AutomotiveTheme.typography.body3) {
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
 * @param leadingOverlayIconName When set, and [enabled] is `false`, dims the entire
 * [leadingContent] behind a scrim with this icon centered on top - e.g. an offline indicator on
 * a leading thumbnail. Has no effect while [enabled] is `true`, or when `null` (default).
 */
@Composable
fun ListItemBasic(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    enabled: Boolean = true,
    active: Boolean = false,
    headlineContent: @Composable () -> Unit,
    supportingContent: @Composable (() -> Unit)? = null,
    leadingContent: @Composable (() -> Unit)? = null,
    leadingOverlayIconName: IconSetEnum? = null,
    trailingContent: @Composable (() -> Unit)? = null,
    colors: ListItemBasicColors = ListItemBasicDefaults.colors(),
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
                supportingContent = supportingContent,
                leadingContent = leadingContent,
                leadingOverlayIconName = leadingOverlayIconName,
                trailingContent = trailingContent,
                switchLinePositionContent = switchLinePositionContent,
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
    name = "ListItemDefault - Light Mode",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_NO or Configuration.UI_MODE_TYPE_CAR
)

@Preview(
    name = "ListItemDefault - Dark Mode",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_CAR
)

@Composable
fun ListItemDefaultPreview(
) {
    val backgroundColor = if (isSystemInDarkTheme()) Color.Black else Color.White

    val SupportingText =
        "At vero eos et accusamus et iusto odio dignissimos ducimus qui blanditiis praesentium voluptatum deleniti atque corrupti quos dolores et quas molestias excepturi sint occaecati cupiditate non provident, similique sunt in culpa qui officia deserunt mollitia animi, id est laborum et dolorum fuga. Et harum quidem rerum facilis est et expedita distinctio. Nam libero tempore, cum soluta nobis est eligendi optio cumque nihil impedit quo minus id quod maxime placeat facere possimus, omnis voluptas assumenda est, omnis dolor repellendus. Temporibus autem quibusdam et aut officiis debitis aut rerum necessitatibus saepe eveniet ut et voluptates repudiandae sint et molestiae non recusandae. Itaque earum rerum hic tenetur a sapiente delectus, ut aut reiciendis voluptatibus maiores alias consequatur aut perferendis doloribus asperiores repellat."

    AutomotiveTheme() {
        Column(modifier = Modifier.background(backgroundColor)) {
            ListItemBasic(
                onClick = {},
                headlineContent = {
                    Text(
                        "Headling Text"
                    )
                },
                supportingContent = {
                    Text(
                        SupportingText
                    )
                },
                leadingContent = {
                    Box(
                        modifier = Modifier
                            .background(Color.Gray)
                            .size(112.dp, 112.dp)
                            .clip(RoundedCornerShape(AutomotiveTheme.measurement.shapes.medium))
                    )
                },
                trailingContent = { Icon(size = AutomotiveTheme.icon.primary) },
                horizontalDividerEnabled = false,
                enabled = true,
                active = true
            )
        }
    }
}


