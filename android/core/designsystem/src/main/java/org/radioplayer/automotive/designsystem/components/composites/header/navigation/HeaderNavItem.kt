package org.radioplayer.automotive.designsystem.components.composites.header.navigation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import org.radioplayer.automotive.designsystem.components.composites.button.filled.toggle.ButtonToggleFilled
import org.radioplayer.automotive.designsystem.components.composites.button.filled.toggle.ButtonToggleFilledShape
import org.radioplayer.automotive.designsystem.components.composites.iconbutton.filled.toggle.IconButtonToggleFilled
import org.radioplayer.automotive.designsystem.components.primitives.icon.Icon
import org.radioplayer.automotive.designsystem.components.primitives.icon.IconSetEnum
import org.radioplayer.automotive.designsystem.theme.AutomotiveTheme

data class HeaderNavItem(
    val icon: IconSetEnum = IconSetEnum.Droid,
    val label: String? = null,
    val contentDescription: String? = null,
)

@Composable
internal fun NavItemRow(
    items: List<HeaderNavItem>,
    range: IntRange,
    selectedIndex: Int,
    onItemSelected: (Int) -> Unit
) {
    Row(horizontalArrangement = Arrangement.spacedBy(AutomotiveTheme.measurement.spaces.large)) {
        range.forEach { index ->
            val item = items[index]
            val checked = index == selectedIndex
            if (item.label != null) {
                ButtonToggleFilled(
                    selected = checked,
                    onSelectedChange = { onItemSelected(index) },
                    icon = { Icon(name = item.icon, size = AutomotiveTheme.icon.primary) },
                    label = item.label,
                    shapeVariant = ButtonToggleFilledShape.Square,
                )
            } else {
                IconButtonToggleFilled(
                    selected = checked,
                    onSelectedChange = { onItemSelected(index) },
                    icon = { Icon(name = item.icon, size = AutomotiveTheme.icon.primary) },
                    shape = RoundedCornerShape(AutomotiveTheme.measurement.shapes.largeIncreased)
                )
            }
        }
    }

}
