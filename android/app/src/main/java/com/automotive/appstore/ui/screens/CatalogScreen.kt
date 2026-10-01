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
import com.automotive.appstore.data.AppState
import com.automotive.appstore.data.CatalogStatus
import com.automotive.appstore.data.CategoryId
import com.automotive.appstore.data.StringKey
import com.automotive.appstore.data.getAppState
import com.automotive.appstore.ui.components.AppTile
import com.automotive.appstore.ui.components.CategoryFilterRow
import com.automotive.appstore.ui.components.categoryLabelKey
import com.automotive.appstore.ui.components.SearchBar
import com.automotive.appstore.ui.components.StateMessage
import com.automotive.appstore.ui.components.StoreIcons
import com.automotive.appstore.ui.components.TileSkeletonGrid
import com.automotive.appstore.ui.components.TouchButton
import com.automotive.appstore.ui.components.TouchVariant
import com.automotive.appstore.ui.theme.translator
import com.automotive.appstore.ui.theme.StoreType
import com.automotive.appstore.ui.theme.storeColors
import com.automotive.appstore.ui.theme.storeMetrics
import org.radioplayer.automotive.designsystem.components.primitives.Text
import org.radioplayer.automotive.designsystem.theme.AutomotiveTheme

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
    var query by remember { mutableStateOf("") }

    val catalog = snapshot.catalog
    val visible = remember(catalog.apps, category, query) {
        val needle = query.trim().lowercase()
        catalog.apps.filter { app ->
            if (category != null && app.category != category) return@filter false
            if (needle.isEmpty()) return@filter true
            listOf(app.name, app.tagline, app.developer, app.description)
                .any { it.lowercase().contains(needle) }
        }
    }

    val metrics = storeMetrics

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(storeColors.background)
            .padding(metrics.contentPadding),
        verticalArrangement = Arrangement.spacedBy(metrics.sectionGap),
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(metrics.itemGap)) {
            SearchBar(
                query = query,
                onQueryChange = { query = it },
            )
            Row(modifier = Modifier.horizontalScroll(rememberScrollState())) {
                CategoryFilterRow(selected = category, onSelect = { category = it })
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

            catalog.apps.isEmpty() -> StateMessage(
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
                        title = if (query.isNotBlank()) {
                            translator.t(StringKey.SEARCH_RESULTS_FOR, "q" to query)
                        } else {
                            translator.t(category?.let(::categoryLabelKey) ?: StringKey.CATEGORY_ALL)
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
