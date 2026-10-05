package com.automotive.appstore.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.horizontalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.automotive.appstore.StoreViewModel
import androidx.compose.runtime.LaunchedEffect
import com.automotive.appstore.data.AppState
import com.automotive.appstore.data.CatalogStatus
import com.automotive.appstore.data.CategoryId
import com.automotive.appstore.data.STORE_APP_PACKAGE
import com.automotive.appstore.data.StringKey
import com.automotive.appstore.data.getAppState
import com.automotive.appstore.ui.components.AppTile
import com.automotive.appstore.ui.components.CATEGORY_ORDER
import com.automotive.appstore.ui.components.CategoryFilterRow
import com.automotive.appstore.ui.components.categoryLabelKey
import com.automotive.appstore.ui.components.SearchBar
import com.automotive.appstore.ui.components.StateMessage
import com.automotive.appstore.ui.components.StoreIcons
import com.automotive.appstore.ui.components.TileSkeletonGrid
import com.automotive.appstore.ui.components.TypeFilterRow
import com.automotive.appstore.ui.components.typeLabel
import com.automotive.appstore.ui.components.TouchButton
import com.automotive.appstore.ui.components.TouchVariant
import com.automotive.appstore.ui.theme.translator
import com.automotive.appstore.ui.theme.StoreType
import com.automotive.appstore.ui.theme.screenPadding
import com.automotive.appstore.ui.theme.storeColors
import com.automotive.appstore.ui.theme.storeMetrics
import org.radioplayer.automotive.designsystem.components.primitives.Text
import org.radioplayer.automotive.designsystem.theme.AutomotiveTheme

/**
 * Whether the catalog renders the search field.
 *
 * Temporarily off. Opening the soft keyboard re-derived the window size class from the
 * keyboard-shrunk bounds, which dropped the height class to Compact and collapsed the whole
 * shell (rail labels, top-bar clock, search hint, type scale). `rememberStableWindowSizeClass`
 * now corrects the bounds, but the field stays hidden until that is verified on a real head unit.
 *
 * Flipping this back to `true` restores the search with no other change: the `query` state, the
 * text filter, the "no match" state and its "Show all" action are all still wired up below.
 */
private const val SHOW_SEARCH_BAR = false

/**
 * The store catalog. Port of the web `CatalogScreen`.
 *
 * Mirrors the web layout: search, then a scrolling category filter, then either a
 * loading skeleton, one of four state messages, or the app grid with a heading and count.
 */
@Composable
fun CatalogScreen(
    viewModel: StoreViewModel,
    onOpenApp: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val snapshot by viewModel.snapshot.collectAsStateWithLifecycle()

    var category by remember { mutableStateOf<CategoryId?>(null) }
    // The published `type` value, verbatim. Null means "All".
    var type by remember { mutableStateOf<String?>(null) }
    var query by remember { mutableStateOf("") }

    val catalog = snapshot.catalog

    // The remote catalogue publishes the store itself so head units can update it through the
    // normal install flow. It is not browsable: listing it would invite the user to install the
    // app they are already running. Everything below — the grid, the empty state and the category
    // chips — works off this filtered list rather than `catalog.apps` directly.
    val browsable = remember(catalog.apps) {
        catalog.apps.filterNot { it.packageName == STORE_APP_PACKAGE }
    }

    val visible = remember(browsable, category, type, query) {
        val needle = query.trim().lowercase()
        browsable.filter { app ->
            if (category != null && app.category != category) return@filter false
            if (type != null && app.type != type) return@filter false
            if (needle.isEmpty()) return@filter true
            listOf(app.name, app.tagline, app.developer, app.description)
                .any { it.lowercase().contains(needle) }
        }
    }

    val metrics = storeMetrics

    // Only offer categories the catalogue actually has apps in. The endpoint has no category
    // field — CatalogMapper derives one by keyword — so most of the web build's six categories are
    // empty for a media-focused catalogue, and a filter chip that leads to an empty grid is a dead
    // end. `All` is always kept.
    val availableCategories = remember(browsable) {
        val present = browsable.map { it.category }.toSet()
        CATEGORY_ORDER.filter { it in present }
    }

    // `type` chips are derived from the values the catalogue actually contains. Nothing is
    // hardcoded: the vocabulary belongs to `config.json` and grows without an app release, so a
    // new value has to become visible and filterable the moment it is published.
    //
    // Ordered most-common-first so the busiest type leads, then alphabetically so the row is
    // stable between loads rather than reshuffling with catalogue order.
    val availableTypes = remember(browsable) {
        browsable.map { it.type }
            .filter { it.isNotBlank() }
            .groupingBy { it }
            .eachCount()
            .entries
            .sortedWith(compareByDescending<Map.Entry<String, Int>> { it.value }.thenBy { it.key })
            .map { it.key }
    }

    // A category or type can vanish when the catalogue reloads (or a search narrows nothing).
    // Drop a selection that no longer exists rather than leaving the grid filtered to nothing.
    LaunchedEffect(availableCategories, category) {
        if (category != null && category !in availableCategories) category = null
    }
    LaunchedEffect(availableTypes, type) {
        if (type != null && type !in availableTypes) type = null
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(storeColors.background)
            .screenPadding(metrics),
        verticalArrangement = Arrangement.spacedBy(metrics.sectionGap),
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(metrics.itemGap)) {
            if (SHOW_SEARCH_BAR) {
                SearchBar(
                    query = query,
                    onQueryChange = { query = it },
                )
            }
            // Hidden entirely when the catalogue yields a single category: one chip and "All" add
            // nothing, and the row would only take vertical space.
            if (availableCategories.size > 1) {
                Row(modifier = Modifier.horizontalScroll(rememberScrollState())) {
                    CategoryFilterRow(
                        selected = category,
                        onSelect = { category = it },
                        categories = availableCategories,
                    )
                }
            }
            // `type` is published by the catalogue, so unlike `category` it is real data and gets
            // its own filter row. Hidden under the same rule: one chip plus "All" adds nothing.
            if (availableTypes.size > 1) {
                Row(modifier = Modifier.horizontalScroll(rememberScrollState())) {
                    TypeFilterRow(
                        selected = type,
                        onSelect = { type = it },
                        types = availableTypes,
                    )
                }
            }
        }

        when {
            catalog.status == CatalogStatus.LOADING -> TileSkeletonGrid()

            catalog.status == CatalogStatus.ERROR -> StateMessage(
                icon = StoreIcons.Offline,
                title = translator.t(StringKey.CATALOG_ERROR_TITLE),
                body = translator.t(StringKey.CATALOG_ERROR_BODY),
                destructive = true,
                action = {
                    TouchButton(
                        label = translator.t(StringKey.CATALOG_RETRY),
                        onClick = viewModel::reloadCatalog,
                    )
                },
            )

            browsable.isEmpty() -> StateMessage(
                icon = StoreIcons.NoApps,
                title = translator.t(StringKey.CATALOG_EMPTY_TITLE),
                body = translator.t(StringKey.CATALOG_EMPTY_BODY),
            )

            visible.isEmpty() -> StateMessage(
                icon = StoreIcons.NoMatch,
                title = translator.t(StringKey.CATALOG_NO_MATCH_TITLE),
                body = translator.t(StringKey.CATALOG_NO_MATCH_BODY),
                action = {
                    TouchButton(
                        label = translator.t(StringKey.CATALOG_SHOW_ALL),
                        onClick = {
                            category = null
                            type = null
                            query = ""
                        },
                        variant = TouchVariant.SECONDARY,
                    )
                },
            )

            else -> LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = metrics.gridMinTileWidth),
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.spacedBy(metrics.itemGap),
                verticalArrangement = Arrangement.spacedBy(metrics.itemGap),
            ) {
                item(span = { androidx.compose.foundation.lazy.grid.GridItemSpan(maxLineSpan) }) {
                    CatalogHeader(
                        title = when {
                            query.isNotBlank() ->
                                translator.t(StringKey.SEARCH_RESULTS_FOR, "q" to query)
                            // Two filters are active at once, so the heading names the narrower one:
                            // `type` is the published axis, `category` is the keyword-derived one.
                            // Capitalised like its chip, so the heading reads as the label the
                            // user just tapped rather than as a raw wire value.
                            type != null -> typeLabel(type!!)
                            else -> translator.t(category?.let(::categoryLabelKey) ?: StringKey.CATEGORY_ALL)
                        },
                        count = visible.size,
                    )
                }
                items(visible, key = { it.id }) { app ->
                    AppTile(
                        app = app,
                        state = getAppState(snapshot, app),
                        onClick = { onOpenApp(app.id) },
                    )
                }
            }
        }
    }
}

/** The section heading plus "N apps" count, matching the web catalog header row. */
@Composable
private fun CatalogHeader(title: String, count: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 4.dp),
        verticalAlignment = Alignment.Bottom,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text(
            text = title,
            modifier = Modifier.weight(1f),
            // Web `text-2xl font-bold` = 27px at the 700 weight; the nearest design-system
            // role (Step5, 28sp) is only Medium, so use the exact web style.
            style = StoreType.xxlBold,
            color = storeColors.foreground,
            maxLines = 2,
        )
        Text(
            text = translator.t(StringKey.CATALOG_COUNT, "n" to count),
            // Web `text-lg tabular-nums` = 20.25px; `body2` is 28sp.
            style = StoreType.sectionCount,
            color = storeColors.mutedForeground,
            maxLines = 1,
        )
    }
}
