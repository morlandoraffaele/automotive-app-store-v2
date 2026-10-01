package org.radioplayer.automotive.designsystem.components.composites.search

import android.content.res.Configuration
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.clickable
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import org.radioplayer.automotive.designsystem.components.composites.containertappableicon.ContainerTappableIcon
import org.radioplayer.automotive.designsystem.components.composites.iconbutton.standard.IconButtonStandard
import org.radioplayer.automotive.designsystem.components.primitives.Text
import org.radioplayer.automotive.designsystem.components.primitives.icon.Icon
import org.radioplayer.automotive.designsystem.components.primitives.icon.IconSetEnum
import org.radioplayer.automotive.designsystem.interaction.rememberInteractionState
import org.radioplayer.automotive.designsystem.interaction.rememberSystemFocusRing
import org.radioplayer.automotive.designsystem.model.DrivingUxRestrictions
import org.radioplayer.automotive.designsystem.model.LocalDrivingUxRestrictions
import org.radioplayer.automotive.designsystem.theme.AutomotiveTheme

/**
 * A search input field that owns its [SearchInputFieldContent] internally, backed by
 * [SearchInputFieldState].
 *
 * The field decides on its own — based on [state]'s query text and the current
 * [drivingUxRestrictions] — whether it shows empty, typing, or voice-only UI. Callers observe
 * [onQueryChange] / [onSearch] to react to the query, and can still push text into the field
 * imperatively via [state] (see [SearchInputFieldState] for the voice-transcript use case).
 *
 * When [drivingUxRestrictions] is restricted, the **entire field** — not just the mic icon — is
 * tappable and triggers [onVoiceSearchClick], so starting voice control doesn't require hitting
 * a small target while driving.
 */
@Composable
fun SearchInputField(
    modifier: Modifier = Modifier,
    state: SearchInputFieldState = rememberSearchInputFieldState(),
    shapeVariant: SearchInputFieldShape = SearchInputFieldShape.Rounded,
    enabled: Boolean = true,
    drivingUxRestrictions: DrivingUxRestrictions = LocalDrivingUxRestrictions.current,
    onQueryChange: (String) -> Unit = {},
    onSearch: (String) -> Unit = {},
    hintText: String = "Hinted search text",
    onVoiceSearchClick: () -> Unit = {},
    colors: SearchInputFieldColors = SearchInputFieldDefaults.colors(),
) {
    val query = state.query

    val effectiveContent = when {
        drivingUxRestrictions.isRestricted -> SearchInputFieldContent.VoiceOnly
        query.isEmpty() -> SearchInputFieldContent.Empty
        else -> SearchInputFieldContent.Typing(query)
    }

    val interactionSource = remember { MutableInteractionSource() }
    val interactionState by rememberInteractionState(interactionSource)
    val focusBorder = rememberSystemFocusRing(interactionState, null)

    val shape: Shape = when (shapeVariant) {
        SearchInputFieldShape.Rounded -> RoundedCornerShape(AutomotiveTheme.measurement.shapes.full)
        SearchInputFieldShape.Square -> RoundedCornerShape(AutomotiveTheme.measurement.shapes.largeIncreased)
    }

    val fieldAlpha = if (enabled) 1f else SearchInputFieldDefaults.DisabledContentAlpha

    // Only in VoiceOnly does the whole surface act as a single press target for voice control.
    // In Empty/Typing, indication stays tied to the text field's own interactions as before.
    val surfacePressModifier = if (effectiveContent is SearchInputFieldContent.VoiceOnly) {
        Modifier.clickable(
            interactionSource = interactionSource,
            indication = LocalIndication.current,
            enabled = enabled,
            onClickLabel = "Voice search",
            onClick = onVoiceSearchClick,
        )
    } else {
        Modifier.indication(interactionSource, LocalIndication.current)
    }

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .alpha(fieldAlpha)
            .then(surfacePressModifier),
        shape = shape,
        color = colors.containerColor,
        contentColor = colors.contentColor,
        border = focusBorder,
    ) {
        Row(
            modifier = Modifier
                .padding(
                    PaddingValues(
                        horizontal = AutomotiveTheme.measurement.spaces.extraSmall,
                        vertical = AutomotiveTheme.measurement.spaces.extraExtraSmall,
                    )
                )
                .defaultMinSize(minHeight = AutomotiveTheme.measurement.sizes.minTapArea),
            horizontalArrangement = Arrangement.spacedBy(AutomotiveTheme.measurement.spaces.large),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            ContainerTappableIcon {
                Icon(
                    name = IconSetEnum.Search,
                    size = AutomotiveTheme.icon.secondary,
                    contentDescription = "Search",
                    color = colors.contentColor,
                )
            }
            Row(modifier = Modifier.weight(1f)) {
                when (effectiveContent) {
                    is SearchInputFieldContent.Empty,
                    is SearchInputFieldContent.Typing -> SearchInputFieldTextField(
                        text = query,
                        onValueChange = { newText ->
                            state.setQuery(newText)
                            onQueryChange(newText)
                        },
                        onSearch = { onSearch(query) },
                        hintText = hintText,
                        enabled = enabled,
                        contentColor = colors.contentColor,
                        interactionSource = interactionSource,
                    )

                    // No clickable here: the whole Surface already handles the tap (see
                    // surfacePressModifier above). A nested clickable would create a second,
                    // overlapping touch/accessibility target. Shows the last recognized query
                    // (e.g. from a voice transcript pushed into state) when there is one, since
                    // restricted mode still needs to surface what was heard - it just can't offer
                    // an editable BasicTextField/keyboard while driving.
                    SearchInputFieldContent.VoiceOnly -> Text(
                        text = query.ifEmpty { hintText },
                        style = AutomotiveTheme.typography.body3,
                        color = colors.voiceOnlyContentColor,
                    )
                }
            }
            when (effectiveContent) {
                is SearchInputFieldContent.Typing -> SearchInputFieldClearButton(
                    enabled = enabled,
                    contentColor = colors.contentColor,
                    onClick = {
                        state.clear()
                        onQueryChange("")
                    },
                )

                // A recognized transcript can still be shown while restricted (see the VoiceOnly
                // Text branch above), so it needs the same clear affordance as Typing - otherwise
                // there's no way to reset it without waiting for a fresh voice result. The mic
                // icon (purely decorative - the whole surface already handles the tap) only makes
                // sense once there's nothing to clear.
                SearchInputFieldContent.VoiceOnly -> if (query.isNotEmpty()) {
                    SearchInputFieldClearButton(
                        enabled = enabled,
                        contentColor = colors.contentColor,
                        onClick = {
                            state.clear()
                            onQueryChange("")
                        },
                    )
                } else {
                    ContainerTappableIcon {
                        Icon(
                            name = IconSetEnum.Mic,
                            contentDescription = "Voice search",
                            color = colors.contentColor,
                            size = AutomotiveTheme.icon.primary,
                        )
                    }
                }

                is SearchInputFieldContent.Empty -> Unit
            }
        }
    }
}

@Composable
private fun SearchInputFieldClearButton(
    enabled: Boolean,
    contentColor: Color,
    onClick: () -> Unit,
) {
    IconButtonStandard(
        onClick = onClick,
        enabled = enabled,
        icon = {
            Icon(
                name = IconSetEnum.Close,
                contentDescription = "Clear",
                color = contentColor,
                size = AutomotiveTheme.icon.primary,
            )
        }
    )
}

@Composable
private fun SearchInputFieldTextField(
    text: String,
    onValueChange: (String) -> Unit,
    onSearch: () -> Unit,
    hintText: String,
    enabled: Boolean,
    contentColor: Color,
    interactionSource: MutableInteractionSource,
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current

    BasicTextField(
        value = text,
        onValueChange = onValueChange,
        enabled = enabled,
        modifier = Modifier.fillMaxWidth(),
        textStyle = LocalTextStyle.current.merge(
            AutomotiveTheme.typography.body3Medium.copy(color = contentColor)
        ),
        singleLine = true,
        cursorBrush = SolidColor(contentColor),
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Text,
            imeAction = ImeAction.Search
        ),
        keyboardActions = KeyboardActions(
            onSearch = {
                onSearch()
                keyboardController?.hide()
                focusManager.clearFocus()
            }
        ),
        interactionSource = interactionSource,
        decorationBox = { innerTextField ->
            if (text.isEmpty()) {
                Text(
                    text = hintText,
                    style = AutomotiveTheme.typography.body3,
                    color = contentColor,
                )
            }
            innerTextField()
        },
    )
}


@Preview(
    name = "SearchInputField - Light Mode",
    showBackground = true,
    widthDp = 1000,
    uiMode = Configuration.UI_MODE_NIGHT_NO or Configuration.UI_MODE_TYPE_CAR,
)
@Preview(
    name = "SearchInputField - Dark Mode",
    showBackground = true,
    widthDp = 1000,
    uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_CAR,
)
@Composable
private fun SearchInputFieldPreview() {
    AutomotiveTheme {
        Column(
            modifier = Modifier.padding(AutomotiveTheme.measurement.spaces.large),
            verticalArrangement = Arrangement.spacedBy(AutomotiveTheme.measurement.spaces.large),
        ) {
            // Empty — internal query starts blank
            SearchInputField(
                hintText = "Search stations",
            )

            // Pre-filled — seeded once via state, then self-managed from there
            SearchInputField(
                state = rememberSearchInputFieldState(initialQuery = "Classic Rock"),
                hintText = "Search stations",
            )

            // Voice-only — driven by drivingUxRestrictions; tapping anywhere on the field
            // (not just the mic icon) triggers onVoiceSearchClick.
            SearchInputField(
                hintText = "Try voice search",
                drivingUxRestrictions = DrivingUxRestrictions(isRestricted = true),
                onVoiceSearchClick = {},
            )

            // Disabled
            SearchInputField(
                state = rememberSearchInputFieldState(initialQuery = "Classic Rock"),
                hintText = "Search stations",
                enabled = false,
            )
        }
    }
}