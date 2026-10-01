package org.radioplayer.automotive.designsystem.components.composites.tab

import android.content.res.Configuration
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import org.radioplayer.automotive.designsystem.components.primitives.Text
import org.radioplayer.automotive.designsystem.components.primitives.icon.Icon
import org.radioplayer.automotive.designsystem.theme.AutomotiveTheme

/**
 * A composite tab group container component for automotive HMIs, displaying a horizontal sequence of tabs.
 * Supports both controlled (via [selectedIndex]) and uncontrolled selection state modes.
 *
 * @param modifier The [Modifier] to be applied to the tab row container.
 * @param tabs The list of [TabContent] specifications representing the content and state of each tab.
 * @param selectedIndex Optional index of the selected tab for state-controlled usage. When `null`, internal state is maintained.
 * @param onTabSelected Optional callback invoked when a tab is selected, passing the index of the newly selected tab.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TabGroup(
    modifier: Modifier = Modifier,
    tabs: List<TabContent>,
    selectedIndex: Int? = null,
    onTabSelected: ((Int) -> Unit)? = null,
    // TODO: Understand if we need to override colors
    // selectedContentColor: Color = LocalContentColor.current,
    // unselectedContentColor: Color = selectedContentColor,
) {
    if (tabs.isEmpty()) return

    val firstEnabledIndex = tabs.indexOfFirst { it.enabled }.takeIf { it != -1 } ?: 0
    var internalState by rememberSaveable { mutableStateOf(firstEnabledIndex) }
    val selected = (selectedIndex ?: internalState).coerceIn(0, tabs.lastIndex)

    SecondaryTabRow(
        modifier = modifier,
        contentColor = AutomotiveTheme.colorScheme.onSurface,
        containerColor = Color.Transparent,
        selectedTabIndex = selected,
        indicator = {
            TabRowDefaults.SecondaryIndicator(
                modifier = Modifier.tabIndicatorOffset(selectedTabIndex = selected),
                height = TabDefaults.activeIndicatorHeight,
                color = AutomotiveTheme.colorScheme.inverseSurface
            )
        },
        divider = {
            HorizontalDivider(
                thickness = TabDefaults.horizontalDividerThickness,
                color = AutomotiveTheme.colorScheme.outlineVariant
            )
        }
    ) {
        tabs.forEachIndexed { index, tab ->
            Column(
                modifier = Modifier.padding(bottom = AutomotiveTheme.measurement.spaces.extraSmall)
            ) {
                TabItem(
                    tabContent = tab,
                    selected = index == selected,
                    onClick = {
                        internalState = index
                        onTabSelected?.invoke(index)
                    },
                    enabled = tab.enabled,
                    // selectedContentColor = selectedContentColor,
                    // unselectedContentColor = unselectedContentColor
                )
            }
        }
    }
}

@Preview(
    name = "TabGroup - Light Mode",
    showBackground = true,
    widthDp = 1000,
    uiMode = Configuration.UI_MODE_NIGHT_NO or Configuration.UI_MODE_TYPE_CAR,
)

@Preview(
    name = "TabGroup - Dark Mode",
    showBackground = false,
    widthDp = 1000,
    uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_CAR,
)

@Composable
private fun TabGroupPreview() {
    AutomotiveTheme() {
        val sampleTabContents = listOf(
            TabContent(
                text = { Text(text = "Tab 1") },
                icon = { Icon() },
                enabled = true
            ),
            TabContent(
                text = { Text(text = "Tab 2") },
                icon = { Icon() }
            ),
            TabContent(
                text = { Text(text = "Tab 3") },
                icon = { Icon() }
            )
        )

        TabGroup(tabs = sampleTabContents)
    }
}