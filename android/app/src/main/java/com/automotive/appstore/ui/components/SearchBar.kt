package com.automotive.appstore.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.automotive.appstore.data.CategoryId
import com.automotive.appstore.data.StringKey
import com.automotive.appstore.ui.theme.translator
import com.automotive.appstore.ui.theme.StoreType
import com.automotive.appstore.ui.theme.storeColors
import com.automotive.appstore.ui.theme.storeMetrics
import kotlinx.coroutines.delay
import org.radioplayer.automotive.designsystem.components.primitives.Text
import org.radioplayer.automotive.designsystem.components.composites.search.SearchInputField
import org.radioplayer.automotive.designsystem.components.composites.search.rememberSearchInputFieldState

/** Sample transcripts the simulated voice recogniser "hears", cycling on each press. */
private val VOICE_SAMPLES = listOf("charging", "music", "weather", "parking")

/** How long the simulated recogniser listens before returning a transcript. */
private const val VOICE_LISTEN_MS = 1_600L

/**
 * Voice + typed search. Port of the web `SearchBar`.
 *
 * The design system already ships a `SearchInputField` that owns exactly this interaction —
 * an empty state with a voice affordance, a typing state, and a `SearchInputFieldState` that
 * can be driven imperatively. The web app's simulated recogniser (which returns one of
 * [VOICE_SAMPLES] after 1.6s) is preserved by injecting the transcript into that state, which
 * is the documented use case for `SearchInputFieldState.setQuery`.
 */
@Composable
fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val hintLabel = translator.t(StringKey.SEARCH_PLACEHOLDER)
    val hintText = translator.t(StringKey.SEARCH_HINT)
    val state = rememberSearchInputFieldState()
    var listening by remember { mutableStateOf(false) }
    var sampleIndex by remember { mutableStateOf(0) }

    // The design system owns the query text and reports edits through `onQueryChange`, so the
    // caller's `query` is only pushed back in when it differs (e.g. "Show all apps" clearing it).
    LaunchedEffect(query) {
        if (state.query != query) state.setQuery(query)
    }

    // Simulated speech recognition: after a short pause, "hear" the next sample phrase.
    LaunchedEffect(listening) {
        if (!listening) return@LaunchedEffect
        delay(VOICE_LISTEN_MS)
        state.setQuery(VOICE_SAMPLES[sampleIndex % VOICE_SAMPLES.size])
        sampleIndex += 1
        listening = false
    }

    val metrics = storeMetrics

    // The voice hint now lives *inside* the field as its placeholder rather than sitting beside it.
    // As a sibling Text it competed with the field for width, could wrap, and read as a separate,
    // unpressable label. Inside, it is tied to the thing it advertises and disappears as soon as
    // there is a query — which is what the hint always wanted to do.
    SearchInputField(
        modifier = modifier.fillMaxWidth(),
        state = state,
        hintText = if (state.query.isEmpty()) hintText else hintLabel,
        onQueryChange = onQueryChange,
        onVoiceSearchClick = { listening = !listening },
    )
}

/**
 * The horizontally scrolling category chips. Port of the web `CategoryFilter`.
 *
 * The selected chip uses the web app's inverted treatment (`bg-foreground text-background`);
 * unselected chips sit on the card surface.
 */
@Composable
fun CategoryFilterRow(
    selected: CategoryId?,
    onSelect: (CategoryId?) -> Unit,
    modifier: Modifier = Modifier,
    categories: List<CategoryId> = CATEGORY_ORDER,
) {
    val filterLabel = translator.t(StringKey.CATEGORY_FILTER)
    Row(
        modifier = modifier
            .fillMaxWidth()
            .semantics { contentDescription = filterLabel },
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        FilterChip(
            label = translator.t(StringKey.CATEGORY_ALL),
            selected = selected == null,
            onClick = { onSelect(null) },
        )
        categories.forEach { category ->
            FilterChip(
                label = translator.t(categoryLabelKey(category)),
                selected = selected == category,
                onClick = { onSelect(category) },
            )
        }
    }
}

/** Category display order, matching `CATEGORY_ORDER` in the web `CatalogScreen`. */
val CATEGORY_ORDER: List<CategoryId> = listOf(
    CategoryId.NAVIGATION,
    CategoryId.MEDIA,
    CategoryId.CHARGING,
    CategoryId.COMMUNICATION,
    CategoryId.UTILITIES,
    CategoryId.PARKED,
)

/** A single pill in the category filter row. */
@Composable
private fun FilterChip(label: String, selected: Boolean, onClick: () -> Unit) {
    val colors = storeColors
    val container = if (selected) colors.foreground else colors.card
    val content = if (selected) colors.background else colors.foreground
    val shape = RoundedCornerShape(50)

    Surface(
        onClick = onClick,
        modifier = Modifier
            .clip(shape)
            .semantics { contentDescription = label },
        shape = shape,
        color = container,
        contentColor = content,
    ) {
        Box(
            modifier = Modifier.padding(
                horizontal = 20.dp,
                vertical = 12.dp,
            ),
        ) {
            androidx.compose.material3.Text(
                text = label,
                // Web `text-lg font-semibold`.
                style = StoreType.lgSemibold,
                color = content,
                maxLines = 1,
            )
        }
    }
}
