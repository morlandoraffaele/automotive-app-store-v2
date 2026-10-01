package org.radioplayer.automotive.designsystem.components.primitives

import android.content.res.Configuration
import androidx.compose.material3.ChipColors
import androidx.compose.material3.SuggestionChip
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import org.radioplayer.automotive.designsystem.components.primitives.icon.Icon
import org.radioplayer.automotive.designsystem.components.primitives.icon.IconSetEnum
import org.radioplayer.automotive.designsystem.theme.AutomotiveTheme

// TODO: Pause implementation until the final design guidelines are provided.
@Composable
fun Chip(
    // variant:
    onClick: () -> Unit,
    label: @Composable (() -> Unit),
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
//    shape: Shape = AssistChipDefaults.shape,
    colors: ChipColors,
//    elevation: ChipElevation? = AssistChipDefaults.assistChipEl…,
//    border: BorderStroke? = AssistChipDefaults.assistChipBo…,
//    interactionSource: MutableInteractionSource? = null
) {

    SuggestionChip(
        onClick = onClick,
        label = label,
        colors = colors
    )

}

@Preview(
    name = "Chip - Dark Mode",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_CAR
)
@Preview(
    name = "Chip - Light Mode",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_NO or Configuration.UI_MODE_TYPE_CAR
)

@Composable
fun ChipPreview() {
    AutomotiveTheme {
        Chip(
            modifier = Modifier,
            onClick = {},
            colors = ChipColors(
                AutomotiveTheme.colorScheme.primary,
                Color.Blue,
                Color.Red,
                Color.Red,
                Color.Red,
                Color.Red,
                Color.Red,
                Color.Red
            ),
            enabled = true, label = { Text(text = "Hello _User_") },
           )
    }
}