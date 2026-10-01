package org.radioplayer.automotive.designsystem.components.composites.header.search

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.radioplayer.automotive.designsystem.components.composites.header.HeaderRow
import org.radioplayer.automotive.designsystem.components.composites.iconbutton.standard.IconButtonStandard
import org.radioplayer.automotive.designsystem.components.composites.search.SearchInputField
import org.radioplayer.automotive.designsystem.components.composites.search.SearchInputFieldShape
import org.radioplayer.automotive.designsystem.components.composites.search.SearchInputFieldState
import org.radioplayer.automotive.designsystem.components.composites.search.rememberSearchInputFieldState
import org.radioplayer.automotive.designsystem.components.primitives.icon.Icon
import org.radioplayer.automotive.designsystem.components.primitives.icon.IconSetEnum
import org.radioplayer.automotive.designsystem.model.DrivingUxRestrictions
import org.radioplayer.automotive.designsystem.model.LocalDrivingUxRestrictions
import org.radioplayer.automotive.designsystem.theme.AutomotiveTheme

/**
 * Header variant combining a back navigation icon button with a [SearchInputField] that fills
 * the remaining width.
 *
 * The [state] is owned by the caller — create it with
 * [rememberSearchInputFieldState] — so the header can be driven imperatively, e.g. injecting a
 * speech-recognition transcript after [onVoiceSearchClick] launches an external recognizer.
 *
 * @param state The search field state controlling the current query text.
 * @param hintText Placeholder shown when the query is empty.
 * @param onBackClick Invoked when the leading back icon button is pressed.
 * @param onQueryChange Invoked whenever the query text changes.
 * @param onSearch Invoked when the IME search action is triggered.
 * @param onVoiceSearchClick Invoked when the field is tapped while driving restrictions are
 * active (the whole field becomes a single voice-search press target).
 * @param shapeVariant Visual shape of the search field, defaults to [SearchInputFieldShape.Rounded].
 * @param drivingUxRestrictions Driving restrictions controlling whether the field shows
 * voice-only UI. Defaults to [LocalDrivingUxRestrictions].
 * @param enabled Controls the enabled state of the search field, e.g. to disable text entry
 * while search is unavailable (offline). Defaults to `true`.
 */
@Composable
fun HeaderSearch(
    modifier: Modifier = Modifier,
    state: SearchInputFieldState = rememberSearchInputFieldState(),
    hintText: String = "Search stations",
    onBackClick: () -> Unit = {},
    onQueryChange: (String) -> Unit = {},
    onSearch: (String) -> Unit = {},
    onVoiceSearchClick: () -> Unit = {},
    shapeVariant: SearchInputFieldShape = SearchInputFieldShape.Rounded,
    drivingUxRestrictions: DrivingUxRestrictions = LocalDrivingUxRestrictions.current,
    enabled: Boolean = true,
) {
    HeaderRow(
        modifier = modifier,
        gap = AutomotiveTheme.measurement.spaces.largeIncreased,
        padding = PaddingValues(0.dp),
        leading = {
            IconButtonStandard(
                onClick = onBackClick,
                icon = {
                    Icon(
                        name = IconSetEnum.ArrowBack,
                        contentDescription = "Back",
                        size = AutomotiveTheme.icon.primary
                    )
                },
            )
        },
        content = {
            SearchInputField(
                state = state,
                hintText = hintText,
                onQueryChange = onQueryChange,
                onSearch = onSearch,
                onVoiceSearchClick = onVoiceSearchClick,
                shapeVariant = shapeVariant,
                drivingUxRestrictions = drivingUxRestrictions,
                enabled = enabled,
            )
        },
    )
}

@Preview(
    name = "HeaderSearch - Light Mode",
    showBackground = true,
    widthDp = 1000,
    uiMode = Configuration.UI_MODE_NIGHT_NO or Configuration.UI_MODE_TYPE_CAR,
)
@Preview(
    name = "HeaderSearch - Dark Mode",
    showBackground = false,
    widthDp = 1000,
    uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_CAR,
)
@Composable
private fun HeaderSearchPreview() {
    AutomotiveTheme {
        Column(
            modifier = Modifier.padding(AutomotiveTheme.measurement.spaces.large),
            verticalArrangement = Arrangement.spacedBy(AutomotiveTheme.measurement.spaces.large),
        ) {
            // Empty — internal query starts blank
            HeaderSearch(
                hintText = "Search stations",
            )

            // Pre-filled — seeded once via state, then self-managed from there
            HeaderSearch(
                state = rememberSearchInputFieldState(initialQuery = "Classic Rock"),
                hintText = "Search stations",
            )

            // Voice-only — driven by drivingUxRestrictions; tapping anywhere on the field
            // (not just the mic icon) triggers onVoiceSearchClick.
            HeaderSearch(
                hintText = "Try voice search",
                drivingUxRestrictions = DrivingUxRestrictions(isRestricted = true),
                onVoiceSearchClick = {},
            )

            // Square shape variant
            HeaderSearch(
                state = rememberSearchInputFieldState(initialQuery = "News"),
                hintText = "Search stations",
                shapeVariant = SearchInputFieldShape.Square,
            )
        }
    }
}