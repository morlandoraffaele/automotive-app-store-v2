package org.radioplayer.automotive.designsystem.components.composites.listitem.tagged

import android.content.res.Configuration
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
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
import org.radioplayer.automotive.designsystem.components.composites.tag.LocalTagContentColorOverride
import org.radioplayer.automotive.designsystem.components.composites.tag.Tag
import org.radioplayer.automotive.designsystem.components.composites.tag.TagColorStyle
import org.radioplayer.automotive.designsystem.components.composites.tag.TagContent
import org.radioplayer.automotive.designsystem.components.composites.tag.TagSlots
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

private val VerticalContentSpace = Spaces().medium
private val HorizontalTagsSpace = Spaces().medium

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

@Composable
private fun ListItemLayout(
    modifier: Modifier = Modifier,
    state: ListItemState,
    headlineContent: @Composable () -> Unit,
    tagsContent: @Composable (() -> Unit)?,
    leadingContent: @Composable (() -> Unit)?,
    trailingContent: @Composable (() -> Unit)?,
    switchLinePositionContent: Boolean,
    colors: ListItemTaggedColors
) {

    Row(
        modifier = modifier
            .padding(HorizontalRowSpace, VerticalRowSpace),
        horizontalArrangement = Arrangement.spacedBy(HorizontalSpaceContent),
        verticalAlignment = Alignment.CenterVertically
    ) {

        leadingContent?.invoke()

        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(VerticalContentSpace)) {
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

            val decoratedTagsContent: (@Composable () -> Unit)? = tagsContent?.let { content ->
                {
                    CompositionLocalProvider(
                        LocalTagContentColorOverride provides colors.tagsColor(state)
                    ) {
                        ProvideTextStyle(AutomotiveTheme.typography.body3) {
                            content()
                        }
                    }
                }
            }

            if (switchLinePositionContent) {
                decoratedTagsContent?.invoke()
                decoratedHeadline()
            } else {
                decoratedHeadline()
                decoratedTagsContent?.invoke()
            }
        }

        trailingContent?.invoke()
    }
}

@Composable
fun ListItemTagged(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    enabled: Boolean = true,
    active: Boolean = false,
    headlineContent: @Composable () -> Unit,
    tagsContent: @Composable (RowScope.() -> Unit)? = null,
    leadingContent: @Composable (() -> Unit)? = null,
    trailingContent: @Composable (() -> Unit)? = null,
    colors: ListItemTaggedColors = ListItemTaggedDefaults.colors(),
    switchLinePositionContent: Boolean = false,
    horizontalDividerEnabled: Boolean = true,
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

    val wrappedTagsContent: @Composable (() -> Unit)? = tagsContent?.let { content ->
        {
            Row (
                horizontalArrangement = Arrangement.spacedBy(HorizontalTagsSpace),
                verticalAlignment = Alignment.CenterVertically
            ) {
                content()
            }
        }
    }


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
                tagsContent = wrappedTagsContent,
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
    name = "ListItemTagged - Light Mode",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_NO or Configuration.UI_MODE_TYPE_CAR,
    widthDp = 1200
)

@Preview(
    name = "ListItemTagged - Dark Mode",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_CAR,
    widthDp = 1200
)

@Composable
fun ListItemTaggedPreview(
) {
    val backgroundColor = if (isSystemInDarkTheme()) Color.Black else Color.White

    AutomotiveTheme() {
        Column(modifier = Modifier.background(backgroundColor)) {
            ListItemTagged(
                onClick = {},
                headlineContent = {
                    Text(
                        "Headling Text"
                    )
                },
                tagsContent = {
                    Tag(
                        colorStyle = TagColorStyle.FILLED,
                        slots = TagSlots.Both(
                            leading = TagContent.Custom(
                                layoutRole = TagContent.LayoutRole.ICON,
                                accessibilityLabel = "",
                                content = { Icon(size = AutomotiveTheme.icon.tertiary) }

                            ),
                            trailing = TagContent.Custom(
                                layoutRole = TagContent.LayoutRole.TEXT,
                                accessibilityLabel = "",
                                content = { Text("Label") }
                            )
                        )
                    )
                    Tag(
                        colorStyle = TagColorStyle.FILLED,
                        slots = TagSlots.Both(
                            leading = TagContent.Custom(
                                layoutRole = TagContent.LayoutRole.ICON,
                                accessibilityLabel = "",
                                content = { Icon(size = AutomotiveTheme.icon.tertiary) }

                            ),
                            trailing = TagContent.Custom(
                                layoutRole = TagContent.LayoutRole.TEXT,
                                accessibilityLabel = "",
                                content = { Text("Label") }
                            )
                        )
                    )
                },
                leadingContent = {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(AutomotiveTheme.measurement.shapes.medium))
                            .background(Color.Gray)
                            .size(112.dp, 112.dp)
                    )
                },
                trailingContent = { Icon(size = AutomotiveTheme.icon.primary) },
                enabled = true,
            )
        }
    }
}