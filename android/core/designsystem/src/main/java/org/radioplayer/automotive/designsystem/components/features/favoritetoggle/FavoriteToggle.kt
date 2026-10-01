package org.radioplayer.automotive.designsystem.components.features.favoritetoggle

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.tooling.preview.Preview
import org.radioplayer.automotive.designsystem.components.composites.iconbutton.standard.toggle.IconButtonToggleStandard
import org.radioplayer.automotive.designsystem.components.primitives.icon.Icon
import org.radioplayer.automotive.designsystem.components.primitives.icon.IconSetEnum
import org.radioplayer.automotive.designsystem.theme.AutomotiveTheme

@Composable
fun FavoriteToggle(
    isFavorite: Boolean,
    onFavoriteChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    shape: Shape = RoundedCornerShape(AutomotiveTheme.measurement.shapes.full),
) {
    IconButtonToggleStandard(
        selected = isFavorite,
        onSelectedChange = onFavoriteChange,
        icon = {
            Icon(
                name = if (isFavorite) IconSetEnum.Favorite else IconSetEnum.FavoriteBorder,
                size = AutomotiveTheme.icon.primary
            )
        },
        modifier = modifier,
        enabled = enabled,
        shape = shape
    )
}

@Preview(
    name = "FavoriteToggle - Light Mode",
    showBackground = true,
    widthDp = 1000,
    uiMode = Configuration.UI_MODE_NIGHT_NO or Configuration.UI_MODE_TYPE_CAR,
)
@Preview(
    name = "FavoriteToggle - Dark Mode",
    showBackground = false,
    widthDp = 1000,
    uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_CAR,
)
@Composable
private fun FavoriteTogglePreview() {
    AutomotiveTheme {
        Column(
            modifier = Modifier.padding(AutomotiveTheme.measurement.spaces.large),
            verticalArrangement = Arrangement.spacedBy(AutomotiveTheme.measurement.spaces.large)
        ) {
            FavoriteToggle(isFavorite = true, onFavoriteChange = {})
            FavoriteToggle(isFavorite = false, onFavoriteChange = {})
        }
    }
}