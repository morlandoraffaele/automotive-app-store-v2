package com.automotive.appstore.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.automotive.appstore.StoreViewModel
import com.automotive.appstore.data.AppListing
import com.automotive.appstore.data.AppState
import com.automotive.appstore.data.AppStatus
import com.automotive.appstore.data.CatalogStatus
import com.automotive.appstore.data.DEFAULT_CHANNEL_ID
import com.automotive.appstore.data.StringKey
import com.automotive.appstore.data.getAppState
import com.automotive.appstore.ui.components.AppActionButton
import com.automotive.appstore.ui.components.AppIconTile
import com.automotive.appstore.ui.components.StatusBadge
import com.automotive.appstore.ui.components.StatusChip
import com.automotive.appstore.ui.components.StatusProgressBar
import com.automotive.appstore.ui.components.StateMessage
import com.automotive.appstore.ui.components.StoreIcons
import com.automotive.appstore.ui.components.TileSkeletonGrid
import com.automotive.appstore.ui.components.TouchButton
import com.automotive.appstore.ui.components.statusLabelFor
import com.automotive.appstore.ui.theme.LocalTranslator
import com.automotive.appstore.ui.theme.StoreType
import com.automotive.appstore.ui.theme.storeColors
import com.automotive.appstore.ui.theme.storeMetrics
import com.automotive.appstore.ui.theme.translator
import org.radioplayer.automotive.designsystem.components.primitives.Text
import org.radioplayer.automotive.designsystem.theme.AutomotiveTheme

/** Statuses that belong in the "Updates" group; everything else is "Up to date". */
private val NEEDS_ATTENTION = setOf(
    AppStatus.UPDATE_AVAILABLE,
    AppStatus.DOWNLOADING,
    AppStatus.INSTALLING,
    AppStatus.FAILED,
)

/**
 * The installed-apps list. Port of the web `InstalledScreen`.
 *
 * Apps are split into an "Updates" section and an "Up to date" section, exactly as on the
 * web, and each row is icon + name/channel + version change + progress + action.
 */
@Composable
fun InstalledScreen(
    viewModel: StoreViewModel,
    onOpenApp: (String) -> Unit,
    onBrowseStore: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val snapshot by viewModel.snapshot.collectAsStateWithLifecycle()

    if (snapshot.catalog.status == CatalogStatus.LOADING) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .background(storeColors.background)
                .padding(storeMetrics.contentPadding)
        ) {
            TileSkeletonGrid(count = 4)
        }
        return
    }

    val installed = snapshot.catalog.apps
        .filter { snapshot.installed.containsKey(it.id) || snapshot.tasks[it.id]?.kind == com.automotive.appstore.data.TaskKind.UPDATE }
        .map { it to getAppState(snapshot, it) }

    if (installed.isEmpty()) {
        StateMessage(
            modifier = modifier
                .fillMaxSize()
                .background(storeColors.background),
            icon = StoreIcons.Installed,
            title = translator.t(StringKey.INSTALLED_EMPTY_TITLE),
            body = translator.t(StringKey.INSTALLED_EMPTY_BODY),
            action = {
                TouchButton(
                    label = translator.t(StringKey.INSTALLED_BROWSE),
                    onClick = onBrowseStore,
                )
            },
        )
        return
    }

    val needsAttention = installed.filter { it.second.status in NEEDS_ATTENTION }
    val upToDate = installed.filter { it.second.status !in NEEDS_ATTENTION }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(storeColors.background)
            .padding(storeMetrics.contentPadding),
        verticalArrangement = Arrangement.spacedBy(storeMetrics.sectionGap * 1.5f),
    ) {
        if (needsAttention.isNotEmpty()) {
            item {
                SectionHeading(
                    title = translator.t(StringKey.INSTALLED_UPDATES_SECTION, "n" to needsAttention.size)
                )
            }
            items(needsAttention, key = { it.first.id }) { (app, state) ->
                InstalledRow(
                    app = app,
                    state = state,
                    viewModel = viewModel,
                    onOpenApp = onOpenApp,
                )
            }
        }
        if (upToDate.isNotEmpty()) {
            item {
                SectionHeading(
                    title = translator.t(StringKey.INSTALLED_UP_TO_DATE_SECTION, "n" to upToDate.size)
                )
            }
            items(upToDate, key = { it.first.id }) { (app, state) ->
                InstalledRow(
                    app = app,
                    state = state,
                    viewModel = viewModel,
                    onOpenApp = onOpenApp,
                )
            }
        }
    }
}

/** A section title, matching the web `h2` above each group. */
@Composable
private fun SectionHeading(title: String) {
    Text(
        text = title,
        modifier = Modifier.padding(bottom = 4.dp),
        // Web `text-2xl font-bold` = 27px; `display3` is Step7 = 36sp.
        style = StoreType.xxlBold,
        color = storeColors.foreground,
        maxLines = 2,
    )
}

/** One installed app row. Port of the web `InstalledRow`. */
@Composable
private fun InstalledRow(
    app: AppListing,
    state: AppState,
    viewModel: StoreViewModel,
    onOpenApp: (String) -> Unit,
) {
    val label = statusLabelFor(state)
    val showTargetVersion = state.status != AppStatus.UP_TO_DATE && state.status != AppStatus.INSTALLED

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(storeColors.radius3Xl))
            .background(storeColors.card)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        Row(
            modifier = Modifier
                .weight(1f)
                .clickable { onOpenApp(app.id) }
                .padding(end = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            Box {
                AppIconTile(icon = app.icon, color = app.iconColor)
                StatusBadge(
                    status = state.status,
                    progress = state.progress,
                    modifier = Modifier.align(Alignment.BottomEnd),
                )
            }
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    Text(
                        text = app.name,
                        // Web `text-xl font-bold` = 22.5px; `huge3Medium` is Step10 = 68sp,
                        // roughly 3x too large.
                        style = StoreType.xlBold,
                        color = storeColors.foreground,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f, fill = false),
                    )
                    // The web app flags any non-stable channel with a warning-outlined pill.
                    if (state.channel != null && state.channel.id != DEFAULT_CHANNEL_ID) {
                        Text(
                            text = state.channel.name,
                            // Web `text-sm font-semibold`.
                            style = StoreType.smSemibold,
                            color = storeColors.warning,
                            maxLines = 1,
                        )
                    }
                }
                VersionChange(
                    from = state.currentVersion,
                    to = if (showTargetVersion) state.targetVersion else null,
                )
                StatusProgressBar(status = state.status, progress = state.progress)
            }
        }

        StatusChip(status = state.status, label = label, progress = state.progress)

        AppActionButton(
            appId = app.id,
            appName = app.name,
            state = state,
            onInstall = viewModel::install,
            onUpdate = viewModel::update,
            onCancel = viewModel::cancel,
            onRetry = viewModel::retry,
        )
    }
}

/**
 * `1.2.3 → 1.2.4`, shown only when the versions actually differ.
 *
 * Port of the web `VersionChange`; the arrow direction follows the layout direction, so it
 * flips automatically in Arabic.
 */
@Composable
fun VersionChange(from: String?, to: String?, modifier: Modifier = Modifier) {
    if (from == null) return
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            text = from,
            // Web `font-mono tabular-nums text-lg`.
            style = StoreType.lg.copy(fontFamily = FontFamily.Monospace),
            color = storeColors.mutedForeground,
            maxLines = 1,
        )
        if (to != null && to != from) {
            Text(
                text = "→",
                // Web `font-mono tabular-nums text-lg`.
                style = StoreType.lg.copy(fontFamily = FontFamily.Monospace),
                color = storeColors.mutedForeground,
                maxLines = 1,
            )
            Text(
                text = to,
                // Web `font-mono tabular-nums text-lg font-semibold text-primary`.
                style = StoreType.lgSemibold.copy(fontFamily = FontFamily.Monospace),
                color = storeColors.primary,
                maxLines = 1,
            )
        }
    }
}
