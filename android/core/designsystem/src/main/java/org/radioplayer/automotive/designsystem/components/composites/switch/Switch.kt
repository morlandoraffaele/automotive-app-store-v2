package org.radioplayer.automotive.designsystem.components.composites.switch

import android.content.res.Configuration
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.material3.SwitchColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import org.radioplayer.automotive.designsystem.components.primitives.icon.Icon
import org.radioplayer.automotive.designsystem.interaction.rememberInteractionState
import org.radioplayer.automotive.designsystem.theme.AutomotiveTheme
import androidx.compose.material3.Switch as MaterialSwitch

@Composable
fun Switch(
    modifier: Modifier = Modifier,
    checked: Boolean = SwitchDefaults.checked,
    onCheckedChange: (Boolean) -> Unit,
    icon: @Composable (() -> Unit)? = null,
    enabled: Boolean = SwitchDefaults.enabled,
    colors: SwitchColors = SwitchDefaults.colors.default(),
    interactionSource: MutableInteractionSource? = null,
) {
    /**
     *  TODO:
     *  1. Color defaults (need to add DISABLED Colors) [ ]
     *  2. Focus style on Switch [ ]
     *  3. Min sizes (?) [ ]
     */

    val interactionSource = interactionSource ?: remember { MutableInteractionSource() }
    val interactionState by rememberInteractionState(interactionSource)

    MaterialSwitch(
        checked = checked,
        onCheckedChange = onCheckedChange,
        modifier = modifier,
        thumbContent = icon,
        enabled = enabled,
        colors = colors,
        interactionSource = interactionSource
    )
}

@Preview(
    name = "Switch - Light Mode",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_NO or Configuration.UI_MODE_TYPE_CAR
)
@Preview(
    name = "Switch - Dark Mode",
    showBackground = false,
    uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_CAR
)

@Composable
fun SwitchPreview() {
    AutomotiveTheme() {
        Column() {

            Switch(
                checked = false,
                onCheckedChange = {},
                icon = { Icon(size = SwitchDefaults.iconSize) }
            )

            Switch(
                checked = true,
                onCheckedChange = {},
                icon = { Icon(size = SwitchDefaults.iconSize) }
            )
        }

    }
}

