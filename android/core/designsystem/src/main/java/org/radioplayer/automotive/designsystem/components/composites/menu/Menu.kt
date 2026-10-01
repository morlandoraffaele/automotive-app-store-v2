package org.radioplayer.automotive.designsystem.components.composites.menu

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.radioplayer.automotive.designsystem.components.primitives.Text
import org.radioplayer.automotive.designsystem.components.primitives.icon.Icon
import org.radioplayer.automotive.designsystem.theme.AutomotiveTheme
import org.radioplayer.automotive.designsystem.utils.customShadow

/**
 * Automotive menu container that lays out a vertical list of [MenuItem]s inside a
 * surfaced, shadowed card.
 *
 * `Menu` is a purely structural composable: it owns the container's shape, background,
 * elevation shadow and width constraint, but has no knowledge of what its children are
 * or how they behave. Any content can be placed inside, though it is typically composed
 * of one or more [MenuItem]s.
 *
 * @param modifier the [Modifier] to be applied to the menu's outer surface.
 * @param shape the shape of the menu container. Defaults to [MenuDefaults.shapes]'s
 * default shape.
 * @param colors [MenuColors] that will be used to resolve the container and content
 * colors for this menu. See [MenuDefaults.colors].
 * @param content the menu's content, typically a sequence of [MenuItem]s.
 */
@Composable
fun Menu(
    modifier: Modifier = Modifier,
    shape: Shape = MenuDefaults.shapes.default(),
    colors: MenuColors = MenuDefaults.colors(),
    content: @Composable ColumnScope.() -> Unit,
) {
    Surface(
        modifier = modifier
            .customShadow(
                color = AutomotiveTheme.colorScheme.aaosScrimLow,
                shape = shape
            ),
        shape = shape,
        color = colors.containerColor,
        contentColor = colors.contentColor
    ) {
        Column(
            modifier = Modifier
                .widthIn(max = MenuDefaults.containerMaxWidth),
            verticalArrangement = Arrangement.spacedBy(AutomotiveTheme.measurement.spaces.extraSmall),
        ) {
            content()
        }
    }
}

@Preview(
    name = "Menu - Light Mode",
    showBackground = true,
    widthDp = 1000,
    uiMode = Configuration.UI_MODE_NIGHT_NO or Configuration.UI_MODE_TYPE_CAR,
)

@Preview(
    name = "Menu - Dark Mode",
    showBackground = false,
    widthDp = 1000,
    uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_CAR,
)

@Composable
private fun MenuPreview() {
    AutomotiveTheme() {
        Column(
            modifier = Modifier
                .padding(40.dp)
                .background(AutomotiveTheme.colorScheme.surface)
        ) {
            Menu() {
                MenuItem(
                    onClick = {},
                    headlineContent = { Text(text = "Primary Text") },
                    supportingContent = { Text(text = "Supporting Text") },
                    leadingContent = { Icon() },
                    trailingContent = { Icon() }
                )
                MenuItem(
                    onClick = {},
                    headlineContent = { Text(text = "Primary Text") },
                    supportingContent = { Text(text = "Supporting Text") },
                    leadingContent = { Icon() },
                    trailingContent = { Icon() },
                    selected = true
                )
                MenuItem(
                    onClick = {},
                    headlineContent = { Text(text = "Primary Text") },
                    supportingContent = { Text(text = "Supporting Text") },
                    leadingContent = { Icon() },
                    trailingContent = { Icon() },
                    showDivider = false
                )
            }
        }
    }
}