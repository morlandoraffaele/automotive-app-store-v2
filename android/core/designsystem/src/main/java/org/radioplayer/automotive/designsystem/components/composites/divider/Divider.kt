package org.radioplayer.automotive.designsystem.components.composites.divider

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.radioplayer.automotive.designsystem.theme.AutomotiveTheme

/**
 * A thin horizontal line used to visually separate content within a layout.
 *
 * This is a design-system wrapper around Material 3's [HorizontalDivider],
 * themed with [AutomotiveTheme] and offering configurable inset styles via
 * [variant].
 *
 * @param modifier the [Modifier] to be applied to this divider.
 * @param variant the layout style of the divider, controlling its horizontal
 * insets. Defaults to [DividerVariant.FullWidth].
 */

@Composable
fun Divider(
    modifier: Modifier = Modifier,
    variant: DividerVariant = DividerVariant.FullWidth
) {
    // TODO: Add to variant (DIVIDER WITH SUBHEAD)

    val variantModifier = when (variant) {
        DividerVariant.FullWidth -> Modifier
        DividerVariant.Inset -> Modifier.padding(start = DividerDefaults.space)
        DividerVariant.MiddleInset -> Modifier.padding(horizontal = DividerDefaults.space)
    }

    HorizontalDivider(
        modifier = modifier.then(variantModifier),
        thickness = 1.dp,
        color = AutomotiveTheme.colorScheme.outlineVariant
    )
}


@Preview(
    name = "Divider - Light Mode",
    showBackground = true,
    widthDp = 1000,
    uiMode = Configuration.UI_MODE_NIGHT_NO or Configuration.UI_MODE_TYPE_CAR,
)

@Preview(
    name = "Divider - Dark Mode",
    showBackground = false,
    widthDp = 1000,
    uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_CAR,
)

@Composable
private fun DividerPreview() {
    AutomotiveTheme() {
        Column (
            modifier = Modifier.padding(AutomotiveTheme.measurement.spaces.large),
            verticalArrangement = Arrangement.spacedBy(AutomotiveTheme.measurement.spaces.large)
        ) {
            Divider(variant = DividerVariant.FullWidth)
            Divider(variant = DividerVariant.Inset)
            Divider(variant = DividerVariant.MiddleInset)
        }

    }
}