package org.radioplayer.automotive.designsystem.components.composites.menu

import androidx.compose.runtime.Stable
import androidx.compose.ui.graphics.Color

@Stable
data class MenuItemColors(
    val selectedContainerColor: Color,
    val unselectedContainerColor: Color,
    val selectedContentColor: Color,
    val unselectedContentColor: Color,
    val selectedSupportingContentColor: Color,
    val unselectedSupportingContentColor: Color,
    val selectedLeadingIconColor: Color,
    val unselectedLeadingIconColor: Color,
    val selectedTrailingIconColor: Color,
    val unselectedTrailingIconColor: Color,
) {
    fun containerColor(selected: Boolean) =
        if (selected) selectedContainerColor else unselectedContainerColor

    fun contentColor(selected: Boolean) =
        if (selected) selectedContentColor else unselectedContentColor

    fun supportingContentColor(selected: Boolean) =
        if (selected) selectedSupportingContentColor else unselectedSupportingContentColor

    fun leadingIconColor(selected: Boolean) =
        if (selected) selectedLeadingIconColor else unselectedLeadingIconColor

    fun trailingIconColor(selected: Boolean) =
        if (selected) selectedTrailingIconColor else unselectedTrailingIconColor
}