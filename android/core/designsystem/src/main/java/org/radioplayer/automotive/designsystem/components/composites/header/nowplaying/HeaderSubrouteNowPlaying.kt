package org.radioplayer.automotive.designsystem.components.composites.header.nowplaying

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.radioplayer.automotive.designsystem.components.composites.header.HeaderRow
import org.radioplayer.automotive.designsystem.components.composites.iconbutton.outline.toggle.IconButtonToggleOutline
import org.radioplayer.automotive.designsystem.components.composites.iconbutton.standard.IconButtonStandard
import org.radioplayer.automotive.designsystem.components.features.continueinapp.ContinueInApp
import org.radioplayer.automotive.designsystem.components.features.favoritetoggle.FavoriteToggle
import org.radioplayer.automotive.designsystem.components.primitives.Text
import org.radioplayer.automotive.designsystem.theme.AutomotiveTheme

@Composable
fun HeaderSubrouteNowPlaying(
    title: String,
    modifier: Modifier = Modifier,
    iconLeading: @Composable () -> Unit,
    onLeadingIconClick: (() -> Unit)? = null,
    trailingSlotContinueInApp: HeaderSubrouteNowPlayingTrailing.ContinueInApp? = null,
    trailingSlotFavorite: HeaderSubrouteNowPlayingTrailing.FavoriteToggle? = null,
    trailingSlotIconButton: HeaderSubrouteNowPlayingTrailing.IconButtonToggleOutline? = null,
) {

    HeaderRow(
        modifier = modifier,
        gap = AutomotiveTheme.measurement.spaces.largeIncreased,
        padding = PaddingValues(0.dp),
        leading = if (onLeadingIconClick != null) {
            {
                IconButtonStandard(
                    onClick = onLeadingIconClick,
                    icon = iconLeading,
                )
            }
        } else null,
        content = {
            Text(
                text = title,
                style = AutomotiveTheme.typography.body1Medium,
                color = AutomotiveTheme.colorScheme.onSurface
            )
        },
        trailing = {
            Row (horizontalArrangement = Arrangement.spacedBy(AutomotiveTheme.measurement.spaces.largeIncreased)) {
                if(trailingSlotContinueInApp != null) {
                    ContinueInApp(
                        label = trailingSlotContinueInApp.label,
                        onClick = trailingSlotContinueInApp.onClick,
                        image = { trailingSlotContinueInApp.image() }
                    )
                }

                if(trailingSlotFavorite != null) {
                    FavoriteToggle(
                        isFavorite = trailingSlotFavorite.isFavorite,
                        onFavoriteChange = trailingSlotFavorite.onFavoriteChange
                    )
                }

                if(trailingSlotIconButton != null) {
                    IconButtonToggleOutline(
                        shape = RoundedCornerShape(AutomotiveTheme.measurement.shapes.largeIncreased),
                        selected = trailingSlotIconButton.selected,
                        icon = { trailingSlotIconButton.icon() },
                        onSelectedChange = trailingSlotIconButton.onSelect
                    )
                }
            }
        }
    )
}

@Preview(
    name = "HeaderSubrouteNowPlaying - Light Mode",
    showBackground = true,
    widthDp = 1000,
    uiMode = Configuration.UI_MODE_NIGHT_NO or Configuration.UI_MODE_TYPE_CAR,
)

@Preview(
    name = "HeaderSubrouteNowPlaying - Dark Mode",
    showBackground = false,
    widthDp = 1000,
    uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_CAR,
)
@Composable
private fun HeaderSubrouteNowPlayingInteractivePreview() {
    AutomotiveTheme {
        Column (
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {

        }
    }
}