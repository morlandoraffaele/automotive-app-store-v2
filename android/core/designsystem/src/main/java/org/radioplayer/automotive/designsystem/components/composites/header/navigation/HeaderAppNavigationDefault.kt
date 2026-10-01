package org.radioplayer.automotive.designsystem.components.composites.header.navigation

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.radioplayer.automotive.designsystem.components.composites.statusindicator.StatusIndicator
import org.radioplayer.automotive.designsystem.components.composites.statusindicator.StatusIndicatorContent
import org.radioplayer.automotive.designsystem.components.composites.statusindicator.StatusIndicatorDefaults
import org.radioplayer.automotive.designsystem.components.composites.statusindicator.StatusIndicatorSlots
import org.radioplayer.automotive.designsystem.components.primitives.Text
import org.radioplayer.automotive.designsystem.components.primitives.icon.Icon
import org.radioplayer.automotive.designsystem.components.primitives.icon.IconSetEnum
import org.radioplayer.automotive.designsystem.theme.AutomotiveTheme

@Composable
fun HeaderAppNavigation(
    modifier: Modifier = Modifier,
    items: List<HeaderNavItem>,
    selectedIndex: Int,
    isOffline: Boolean = false,
    onItemSelected: (Int) -> Unit,
    statusLabel: String = "Offline",
    leadingItemCount: Int = items.size,

    ) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(
                top = AutomotiveTheme.measurement.spaces.extraSmall,
                bottom = AutomotiveTheme.measurement.spaces.medium,
            ),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        NavItemRow(
            items = items,
            range = 0 until leadingItemCount,
            selectedIndex = selectedIndex,
            onItemSelected = onItemSelected,
        )

        if(isOffline) {
            StatusIndicator(
                slots = StatusIndicatorSlots.Both(
                    leading = StatusIndicatorContent.Custom(
                        layoutRole = StatusIndicatorContent.LayoutRole.ICON,
                        accessibilityLabel = "",
                        content = {
                            Icon(
                                name = IconSetEnum.CloudOff,
                                size = AutomotiveTheme.icon.tertiary,
                                contentDescription = null
                            )
                        }
                    ),
                    trailing = StatusIndicatorContent.Custom(
                        layoutRole = StatusIndicatorContent.LayoutRole.TEXT,
                        accessibilityLabel = "",
                        content = {
                            Text(
                                text = statusLabel,
                                style = AutomotiveTheme.typography.body3,
                                color = AutomotiveTheme.colorScheme.onSecondaryContainer
                            )
                        }
                    ),
                ),
                colors = StatusIndicatorDefaults.colors.default()
            )
        }

        NavItemRow(
            items = items,
            range = leadingItemCount until items.size,
            selectedIndex = selectedIndex,
            onItemSelected = onItemSelected,
        )

    }
}

// Sample data for preview purposes
private val sampleItems = listOf(
    HeaderNavItem(
        icon = IconSetEnum.Droid,
        label = "Label",
        contentDescription = "First Section"
    ),
    HeaderNavItem(
        icon = IconSetEnum.Droid,
        contentDescription = "Second section"
    ),
    HeaderNavItem(
        icon = IconSetEnum.Droid,
        contentDescription = "Third section"
    ),
    HeaderNavItem(
        icon = IconSetEnum.Droid,
        contentDescription = "Fourth utility"
    ),
    HeaderNavItem(
        icon = IconSetEnum.Droid,
        contentDescription = "Fifth utility"
    ),
    HeaderNavItem(
        icon = IconSetEnum.Droid,
        contentDescription = "Sixth utility"
    ),
    HeaderNavItem(
        icon = IconSetEnum.Droid,
        contentDescription = "Seventh utility"
    )
)

private const val SAMPLE_LEADING_ITEM_COUNT = 4


@Preview(
    name = "HeaderAppNavigation - Light Mode",
    showBackground = true,
    widthDp = 1000,
    uiMode = Configuration.UI_MODE_NIGHT_NO or Configuration.UI_MODE_TYPE_CAR,
)

@Preview(
    name = "HeaderAppNavigation - Dark Mode",
    showBackground = false,
    widthDp = 1000,
    uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_CAR,
)
@Composable
private fun HeaderAppNavigationInteractivePreview() {
    var selectedIndex by remember { mutableIntStateOf(0) }

    AutomotiveTheme {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.Black)
                .padding(horizontal = 16.dp)
        ) {
            HeaderAppNavigation(
                items = sampleItems,
                selectedIndex = selectedIndex,
                onItemSelected = { index -> selectedIndex = index },
                leadingItemCount = SAMPLE_LEADING_ITEM_COUNT,
            )
        }
    }
}
