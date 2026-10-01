package org.radioplayer.automotive.designsystem.components.composites.statusindicator

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.radioplayer.automotive.designsystem.components.primitives.Text
import org.radioplayer.automotive.designsystem.components.primitives.icon.Icon
import org.radioplayer.automotive.designsystem.subsystems.Spaces
import org.radioplayer.automotive.designsystem.theme.AutomotiveTheme

private val HorizontalSpaceContent = Spaces().medium

@Composable
private fun RenderSlot(contentSpec: StatusIndicatorContent) {
    when (contentSpec) {
        is StatusIndicatorContent.Custom -> {
            contentSpec.content.invoke()
        }
    }
}

private fun resolveTopology(
    leadingContent: StatusIndicatorContent?,
    trailingContent: StatusIndicatorContent?
): StatusIndicatorLayoutTopology {
    val leadingRole = (leadingContent as? StatusIndicatorContent.Custom)?.layoutRole
    val trailingRole = (trailingContent as? StatusIndicatorContent.Custom)?.layoutRole

    return when {
        leadingRole == null && trailingRole == null -> StatusIndicatorLayoutTopology.EMPTY

        leadingRole == StatusIndicatorContent.LayoutRole.TEXT && trailingRole == null -> StatusIndicatorLayoutTopology.TEXT_ONLY
        leadingRole == null && trailingRole == StatusIndicatorContent.LayoutRole.TEXT -> StatusIndicatorLayoutTopology.TEXT_ONLY

        leadingRole == StatusIndicatorContent.LayoutRole.ICON && trailingRole == StatusIndicatorContent.LayoutRole.TEXT -> StatusIndicatorLayoutTopology.ICON_LABEL
        else -> StatusIndicatorLayoutTopology.TEXT_ONLY
    }
}

private fun buildSemanticDescription(
    leadingContent: StatusIndicatorContent?,
    trailingContent: StatusIndicatorContent?
): String {
    return listOfNotNull(leadingContent, trailingContent)
        .filterIsInstance<StatusIndicatorContent.Custom>()
        .map { it.accessibilityLabel.trim() }
        .filter { it.isNotEmpty() }
        .joinToString(separator = ", ")
}


@Composable
private fun StatusIndicatorImpl(
    modifier: Modifier = Modifier,
    slots: StatusIndicatorSlots,
    colors: StatusIndicatorColors,
    shape: Shape = StatusIndicatorDefaults.shape.default()
) {
    val leadingSlot = slots.leadingContent
    val trailingSlot = slots.trailingContent

    val topology = resolveTopology(leadingSlot, trailingSlot)
    val padding = StatusIndicatorDefaults.paddingFor(topology)

    val unifiedDescription = remember(leadingSlot, trailingSlot) {
        buildSemanticDescription(leadingSlot, trailingSlot)
    }

    Surface(
        modifier = modifier
            .semantics {
                contentDescription = unifiedDescription
            },
        color = colors.containerColor,
        contentColor = colors.contentColor,
        shape = shape
    ) {
        Row(
            modifier = Modifier
                .padding(padding)
                .clearAndSetSemantics {},
            horizontalArrangement = Arrangement.spacedBy(
                HorizontalSpaceContent,
                Alignment.CenterHorizontally
            ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            leadingSlot?.let { RenderSlot(it) }
            trailingSlot?.let { RenderSlot(it) }
        }
    }
}

/**
 * A composite status indicator component used to convey state (e.g. "Live",
 * "Buffering", error states) via an icon, a text label, or both, inside a
 * pill-shaped [Surface].
 *
 * The internal layout is derived automatically from the [StatusIndicatorContent.LayoutRole]
 * of the content supplied in [slots] — see [resolveTopology] for the resolution rules.
 * Accessibility labels from each slot are merged into a single [contentDescription]
 * exposed on the root container, so screen readers announce one coherent description
 * rather than one per child.
 *
 * @param modifier Modifier to be applied to the root of the indicator.
 * @param slots The leading and/or trailing content to render. See [StatusIndicatorSlots].
 * @param colors The container and content colors applied to the indicator. Defaults
 *   to [StatusIndicatorDefaults.colors.default].
 *
 * @see StatusIndicatorContent
 * @see StatusIndicatorSlots
 * @see StatusIndicatorColors
 * @see StatusIndicatorDefaults
 */
@Composable
fun StatusIndicator(
    modifier: Modifier = Modifier,
    slots: StatusIndicatorSlots,
    colors: StatusIndicatorColors = StatusIndicatorDefaults.colors.default()
) {
    StatusIndicatorImpl(
        modifier = modifier,
        slots = slots,
        colors = colors
    )
}

@Preview(
    name = "StatusIndicator - Light Mode",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_NO or Configuration.UI_MODE_TYPE_CAR
)

@Preview(
    name = "StatusIndicator - Dark Mode",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_CAR
)

@Composable
private fun StatusIndicatorPreview() {
    AutomotiveTheme {
        Column {
            StatusIndicator(
                slots = StatusIndicatorSlots.Both(
                    leading = StatusIndicatorContent.Custom(
                        layoutRole = StatusIndicatorContent.LayoutRole.ICON,
                        accessibilityLabel = "",
                        content = { Icon() }
                    ),
                    trailing = StatusIndicatorContent.Custom(
                        layoutRole = StatusIndicatorContent.LayoutRole.TEXT,
                        accessibilityLabel = "",
                        content = { Text("Label") }
                    ),
                ),
                colors = StatusIndicatorDefaults.colors.default()
            )

            Spacer(modifier = Modifier.padding(0.dp, 10.dp))

            StatusIndicator(
                slots = StatusIndicatorSlots.Both(
                    leading = StatusIndicatorContent.Custom(
                        layoutRole = StatusIndicatorContent.LayoutRole.ICON,
                        accessibilityLabel = "",
                        content = { Icon() }
                    ),
                    trailing = StatusIndicatorContent.Custom(
                        layoutRole = StatusIndicatorContent.LayoutRole.TEXT,
                        accessibilityLabel = "",
                        content = { Text("Label") }
                    ),
                ),
                colors = StatusIndicatorDefaults.colors.live.emphasisHigh()
            )

            Spacer(modifier = Modifier.padding(0.dp, 10.dp))

            StatusIndicator(
                slots = StatusIndicatorSlots.Both(
                    leading = StatusIndicatorContent.Custom(
                        layoutRole = StatusIndicatorContent.LayoutRole.ICON,
                        accessibilityLabel = "",
                        content = { Icon(color = Color(0xFFB3261E)) }
                    ),
                    trailing = StatusIndicatorContent.Custom(
                        layoutRole = StatusIndicatorContent.LayoutRole.TEXT,
                        accessibilityLabel = "",
                        content = { Text("Label") }
                    ),
                ),
                colors = StatusIndicatorDefaults.colors.live.emphasisLow()
            )
        }

    }
}