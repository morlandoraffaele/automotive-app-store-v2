package org.radioplayer.automotive.designsystem.components.composites.listitem.boxed

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
import org.radioplayer.automotive.designsystem.components.composites.button.filled.ButtonFilled
import org.radioplayer.automotive.designsystem.components.composites.listitem.ListItemState
import org.radioplayer.automotive.designsystem.components.primitives.Text
import org.radioplayer.automotive.designsystem.components.primitives.icon.Icon
import org.radioplayer.automotive.designsystem.interaction.InteractionState
import org.radioplayer.automotive.designsystem.interaction.rememberFocusRingStroke
import org.radioplayer.automotive.designsystem.interaction.rememberInteractionState
import org.radioplayer.automotive.designsystem.subsystems.ColorSchemes
import org.radioplayer.automotive.designsystem.subsystems.Spaces
import org.radioplayer.automotive.designsystem.theme.AutomotiveTheme

private val HorizontalSpaceContent = Spaces().large
private val HorizontalRowSpace = Spaces().medium
private val VerticalRowSpace = Spaces().medium

private fun resolveListItemState(
    enabled: Boolean,
    interactionState: InteractionState,
): ListItemState = when {
    !enabled -> ListItemState.DISABLED
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
        BorderStroke(2.dp, ColorSchemes.fromPalette().dark.outlineVariant)
    }
}


@Composable
private fun ListItemLayout(
    modifier: Modifier = Modifier,
    state: ListItemState,
    headlineContent: @Composable () -> Unit,
    supportingContent: @Composable (() -> Unit)?,
    leadingContent: @Composable (() -> Unit)?,
    trailingContent: @Composable (() -> Unit)?,
    switchLinePositionContent: Boolean,
    colors: ListItemBoxedColors
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

@Composable
fun ListItemBoxed(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    enabled: Boolean = true,
    active: Boolean = false,
    headlineContent: @Composable () -> Unit,
    supportingContent: @Composable (() -> Unit)? = null,
    leadingContent: @Composable (() -> Unit)? = null,
    trailingContent: @Composable (() -> Unit)? = null,
    colors: ListItemBoxedColors = ListItemBoxedDefaults.colors(),
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
    name = "ListItemBoxed - Light Mode",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_NO or Configuration.UI_MODE_TYPE_CAR,
    widthDp = 1200
)

@Preview(
    name = "ListItemBoxed - Dark Mode",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_CAR,
    widthDp = 1200
)

@Composable
fun ListItemBoxedPreview(
) {
    val backgroundColor = if (isSystemInDarkTheme()) Color.Black else Color.White
    val SupportingText =
        "At vero eos et accusamus et iusto odio dignissimos ducimus qui blanditiis praesentium voluptatum deleniti atque corrupti quos dolores et quas molestias excepturi sint occaecati cupiditate non provident, similique sunt in culpa qui officia deserunt mollitia animi, id est laborum et dolorum fuga. Et harum quidem rerum facilis est et expedita distinctio. Nam libero tempore, cum soluta nobis est eligendi optio cumque nihil impedit quo minus id quod maxime placeat facere possimus, omnis voluptas assumenda est, omnis dolor repellendus. Temporibus autem quibusdam et aut officiis debitis aut rerum necessitatibus saepe eveniet ut et voluptates repudiandae sint et molestiae non recusandae. Itaque earum rerum hic tenetur a sapiente delectus, ut aut reiciendis voluptatibus maiores alias consequatur aut perferendis doloribus asperiores repellat."

    AutomotiveTheme() {
        Column(modifier = Modifier.background(backgroundColor)) {
            ListItemBoxed(
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
                    Icon(size = AutomotiveTheme.icon.primary)
                },
                trailingContent = {
                    ButtonFilled(
                        onClick = {},
                        label = "Label",
                        icon = { Icon(size = AutomotiveTheme.icon.primary) },
                        enabled = true
                    )
                },
            )
        }
    }
}
