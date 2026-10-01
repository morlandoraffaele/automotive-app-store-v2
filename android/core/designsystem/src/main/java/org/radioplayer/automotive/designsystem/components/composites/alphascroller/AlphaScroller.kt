package org.radioplayer.automotive.designsystem.components.composites.alphascroller

import android.content.res.Configuration
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import org.radioplayer.automotive.designsystem.components.composites.iconbutton.standard.IconButtonStandard
import org.radioplayer.automotive.designsystem.components.primitives.icon.Icon
import org.radioplayer.automotive.designsystem.components.primitives.icon.IconSetEnum
import org.radioplayer.automotive.designsystem.theme.AutomotiveTheme

/**
 * @param items The letters to show, in display order. Only [AlphaScrollerItem.enabled] letters
 * participate in [onLetterSelected] via the step buttons or a direct tap — see [AlphaScrollerItem]
 * for the "show but skip" vs. "omit entirely" tradeoff.
 * @param selectedLetter Which letter is currently highlighted — hoisted, not owned internally,
 * so a caller can drive it from either end (a tap here, or the content list's own scroll
 * position — see the gallery for both wired up together).
 * @param onLetterSelected Fired when the user picks a letter, whether by tapping it directly or
 * by a step button landing on it. Does not fire for a disabled letter.
 * @param listState This rail's own scroll position — kept separate from the caller's content
 * list state on purpose (they scroll independently);
 */
@Composable
fun AlphaScroller(
    items: List<AlphaScrollerItem>,
    selectedLetter: String?,
    onLetterSelected: (String) -> Unit,
    modifier: Modifier = Modifier,
    listState: LazyListState = rememberLazyListState(),
    previousLetterContentDescription: String = "Previous letter",
    nextLetterContentDescription: String = "Next letter",
) {
    val availableLetters = remember(items) { items.filter { it.enabled }.map { it.letter } }
    val currentIndex = availableLetters.indexOf(selectedLetter)

    fun step(delta: Int) {
        if (availableLetters.isEmpty()) return
        val nextIndex = if (currentIndex == -1) {
            if (delta > 0) 0 else availableLetters.lastIndex
        } else {
            (currentIndex + delta).coerceIn(0, availableLetters.lastIndex)
        }
        onLetterSelected(availableLetters[nextIndex])
    }

    Column(
        modifier = modifier.width(AlphaScrollerDefaults.ContainerWidthFixed),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(AutomotiveTheme.measurement.spaces.medium),
    ) {
        IconButtonStandard(
            onClick = { step(-1) },
            enabled = availableLetters.isNotEmpty() && currentIndex != 0,
            icon = {
                Icon(
                    name = IconSetEnum.ExpandLess,
                    contentDescription = previousLetterContentDescription,
                    color = AutomotiveTheme.colorScheme.onSurfaceVariant,
                    size = AutomotiveTheme.icon.primary
                )
            },
        )

        Surface(
            modifier = Modifier.weight(1f),
            color = AutomotiveTheme.colorScheme.surfaceContainerLowest,
            border = BorderStroke(
                AutomotiveTheme.stroke.thin,
                AutomotiveTheme.colorScheme.outlineVariant
            ),
            shape = RoundedCornerShape(AutomotiveTheme.measurement.shapes.largeIncreased),
        ) {
            LazyColumn(
                state = listState,
            ) {
                items(items, key = { it.letter }) { item ->
                    AlphaScrollerLetterCell(
                        letter = item.letter,
                        selected = item.letter == selectedLetter,
                        enabled = item.enabled,
                        onClick = { onLetterSelected(item.letter) },
                    )
                }
            }
        }

        IconButtonStandard(
            onClick = { step(1) },
            enabled = availableLetters.isNotEmpty() && currentIndex != availableLetters.lastIndex,
            icon = {
                Icon(
                    name = IconSetEnum.ExpandMore,
                    contentDescription = nextLetterContentDescription,
                    color = AutomotiveTheme.colorScheme.onSurfaceVariant,
                    size = AutomotiveTheme.icon.primary
                )
            },
        )
    }
}


@Preview(
    name = "AlphaScroller - Light Mode",
    showBackground = true,
    widthDp = 300,
    heightDp = 600,
    uiMode = Configuration.UI_MODE_NIGHT_NO or Configuration.UI_MODE_TYPE_CAR,
)
@Preview(
    name = "AlphaScroller - Dark Mode",
    showBackground = false,
    widthDp = 300,
    heightDp = 600,
    uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_CAR,
)
@Composable
private fun AlphaScrollerPreview() {
    AutomotiveTheme() {
        var selectedLetter by remember { mutableStateOf<String?>("M") }
        AlphaScroller(
            items = PreviewItemsWithGaps,
            selectedLetter = selectedLetter,
            onLetterSelected = { selectedLetter = it },
            modifier = Modifier.fillMaxHeight(),
        )
    }
}

private val PreviewItemsWithGaps: List<AlphaScrollerItem> = run {
    val skippedLetters = setOf('Q', 'X', 'Z')
    ('A'..'Z').map { char ->
        AlphaScrollerItem(letter = char.toString(), enabled = char !in skippedLetters)
    }
}
