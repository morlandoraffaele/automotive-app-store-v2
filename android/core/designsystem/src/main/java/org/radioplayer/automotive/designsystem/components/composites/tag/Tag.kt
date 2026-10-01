package org.radioplayer.automotive.designsystem.components.composites.tag

import android.content.res.Configuration
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
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
private fun RenderSlot(contentSpec: TagContent) {
    when (contentSpec) {
        is TagContent.Custom -> {
            contentSpec.content.invoke()
        }
    }
}

private fun resolveTopology(
    leadingContent: TagContent?,
    trailingContent: TagContent?
): TagLayoutTopology {
    val leadingRole = (leadingContent as? TagContent.Custom)?.layoutRole
    val trailingRole = (trailingContent as? TagContent.Custom)?.layoutRole

    return when {
        leadingRole == null && trailingRole == null -> TagLayoutTopology.EMPTY

        leadingRole == TagContent.LayoutRole.TEXT && trailingRole == null -> TagLayoutTopology.TEXT_ONLY
        leadingRole == null && trailingRole == TagContent.LayoutRole.TEXT -> TagLayoutTopology.TEXT_ONLY
        leadingRole == TagContent.LayoutRole.ICON && trailingRole == null -> TagLayoutTopology.ICON_ONLY
        leadingRole == null && trailingRole == TagContent.LayoutRole.ICON -> TagLayoutTopology.ICON_ONLY

        leadingRole == TagContent.LayoutRole.ICON && trailingRole == TagContent.LayoutRole.TEXT -> TagLayoutTopology.ICON_LABEL
        leadingRole == TagContent.LayoutRole.TEXT && trailingRole == TagContent.LayoutRole.ICON -> TagLayoutTopology.LABEL_ICON

        else -> TagLayoutTopology.TEXT_ONLY
    }
}

private fun buildSemanticDescription(
    leadingContent: TagContent?,
    trailingContent: TagContent?
): String {
    return listOfNotNull(leadingContent, trailingContent)
        .filterIsInstance<TagContent.Custom>()
        .map { it.accessibilityLabel.trim() }
        .filter { it.isNotEmpty() }
        .joinToString(separator = ", ")
}

@Composable
private fun TagImpl(
    modifier: Modifier = Modifier,
    colors: TagColors,
    border: BorderStroke? = null,
    shape: Shape = TagDefaults.shape.circle(),
    leadingContent: TagContent? = null,
    trailingContent: TagContent? = null,
) {
    val topology = resolveTopology(leadingContent, trailingContent)
    val padding = TagDefaults.paddingFor(topology)

    val unifiedContentDescription = remember(leadingContent, trailingContent) {
        buildSemanticDescription(leadingContent = leadingContent, trailingContent = trailingContent)
    }

    val overrideColor = LocalTagContentColorOverride.current


    Surface(
        modifier = modifier.semantics {
            contentDescription = unifiedContentDescription
        },
        shape = shape,
        color = colors.containerColor,
        contentColor = overrideColor?: colors.contentColor,
        border = border
    ) {
        Row(
            modifier = Modifier
                .padding(padding)
                .clearAndSetSemantics {},
            horizontalArrangement = Arrangement.spacedBy(HorizontalSpaceContent),
            verticalAlignment = Alignment.CenterVertically
        ) {
            leadingContent?.let { RenderSlot(it) }
            trailingContent?.let { RenderSlot(it) }
        }
    }
}

@Composable
private fun FilledTag(
    modifier: Modifier = Modifier,
    leadingContent: TagContent?,
    trailingContent: TagContent?,
    colors: TagColors = TagDefaults.colors.filled(),
) {

    TagImpl(
        modifier = modifier,
        colors = colors,
        border = null,
        leadingContent = leadingContent,
        trailingContent = trailingContent,
    )
}

@Composable
private fun OutlinedTag(
    modifier: Modifier = Modifier,
    leadingContent: TagContent?,
    trailingContent: TagContent?,
    colors: TagColors = TagDefaults.colors.outlined(),
    // TODO: Could be improved by hoisting isInDarkMode
    border: BorderStroke? = null,
) {
    val resolvedBorder = border ?: TagDefaults.outlinedBorder(isSystemInDarkTheme())

    TagImpl(
        modifier = modifier,
        colors = colors,
        border = resolvedBorder,
        leadingContent = leadingContent,
        trailingContent = trailingContent,
    )
}

/**
 * A compact, pill-shaped label used to surface short status, category, or
 * metadata information, optionally paired with a leading and/or trailing icon.
 *
 * A `Tag` is built from up to two slots — leading and trailing — described by
 * [slots]. Each slot is a [TagContent], typically holding either a [Text] label
 * or an [Icon]. The combination of populated slots determines the tag's
 * internal layout and padding automatically; callers do not need to configure
 * spacing themselves. Supported layouts include a text-only tag, an icon-only
 * tag, and a tag combining a leading icon with a trailing label (or vice versa).
 *
 * Accessibility is handled for you: the accessibility labels supplied on each
 * [TagContent.Custom] slot are merged into a single content description
 * exposed on the tag as a whole, so assistive technology announces one
 * coherent label instead of reading each slot separately.
 *
 * @param modifier the [Modifier] applied to the tag's root [Surface].
 * @param slots the leading/trailing content configuration for the tag, e.g.
 * [TagSlots.Leading], [TagSlots.Trailing], or [TagSlots.Both].
 * @param colorStyle which visual style to render the tag in — [TagColorStyle.FILLED]
 * for a solid container, or [TagColorStyle.OUTLINED] for a bordered, low-emphasis
 * container. Defaults to [TagDefaults.colorStyle].
 * @param border an optional custom border, only used when [colorStyle] is
 * [TagColorStyle.OUTLINED]. When `null`, an outlined tag resolves a default
 * border based on the current dark/light theme; this parameter is ignored
 * entirely for [TagColorStyle.FILLED].
 */

@Composable
fun Tag(
    modifier: Modifier = Modifier,
    slots: TagSlots,
    colorStyle: TagColorStyle = TagDefaults.colorStyle,
    border: BorderStroke? = null,
) {
    when (colorStyle) {
        TagColorStyle.FILLED -> FilledTag(
            modifier = modifier,
            leadingContent = slots.leadingContent,
            trailingContent = slots.trailingContent,
        )

        TagColorStyle.OUTLINED -> OutlinedTag(
            modifier = modifier,
            leadingContent = slots.leadingContent,
            trailingContent = slots.trailingContent,
            border = border,
        )
    }
}

val LocalTagContentColorOverride = compositionLocalOf<Color?> { null }


@Preview(
    name = "Tag - Light Mode",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_NO or Configuration.UI_MODE_TYPE_CAR
)

@Preview(
    name = "Tag - Dark Mode",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_CAR
)

@Composable
private fun TagPreview() {
    val colorStyle = TagColorStyle.OUTLINED

    AutomotiveTheme {

        Column() {
            Tag(
                colorStyle = colorStyle,
                slots = TagSlots.Both(
                    leading = TagContent.Custom(
                        layoutRole = TagContent.LayoutRole.TEXT,
                        accessibilityLabel = "",
                        content = { Text("Label") }
                    ),
                    trailing = TagContent.Custom(
                        layoutRole = TagContent.LayoutRole.ICON,
                        accessibilityLabel = "",
                        content = { Icon(size = AutomotiveTheme.icon.tertiary) }
                    )
                )
            )

            Spacer(modifier = Modifier.padding(0.dp, 10.dp))

            Tag(
                colorStyle = colorStyle,
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

            Spacer(modifier = Modifier.padding(0.dp, 10.dp))

            Tag(
                colorStyle = colorStyle,
                slots = TagSlots.Leading(
                    content = TagContent.Custom(
                        layoutRole = TagContent.LayoutRole.ICON,
                        accessibilityLabel = "",
                        content = { Icon(size = AutomotiveTheme.icon.tertiary) }
                    )
                )
            )

            Spacer(modifier = Modifier.padding(0.dp, 10.dp))

            Tag(
                colorStyle = colorStyle,
                slots = TagSlots.Leading(
                    content = TagContent.Custom(
                        layoutRole = TagContent.LayoutRole.TEXT,
                        accessibilityLabel = "",
                        content = { Text("Label") }
                    )
                )
            )
        }

    }
}