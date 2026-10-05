package com.automotive.appstore.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.automotive.appstore.StoreViewModel
import com.automotive.appstore.data.AppListing
import com.automotive.appstore.data.AppState
import com.automotive.appstore.data.AppStatus
import com.automotive.appstore.data.CatalogStatus
import com.automotive.appstore.data.DEFAULT_CHANNEL_ID
import com.automotive.appstore.data.PermissionId
import com.automotive.appstore.data.StringKey
import com.automotive.appstore.data.getAppState
import com.automotive.appstore.ui.components.AppActionButton
import com.automotive.appstore.ui.components.AppIconSize
import com.automotive.appstore.ui.components.AppIconTile
import com.automotive.appstore.ui.components.SkeletonBlock
import com.automotive.appstore.ui.components.StateMessage
import com.automotive.appstore.ui.components.StatusChip
import com.automotive.appstore.ui.components.StatusProgressBar
import com.automotive.appstore.ui.components.StoreIcons
import com.automotive.appstore.ui.components.TouchButton
import com.automotive.appstore.ui.components.TouchVariant
import com.automotive.appstore.ui.components.statusLabelFor
import com.automotive.appstore.ui.theme.LocalTranslator
import com.automotive.appstore.ui.theme.StoreType
import com.automotive.appstore.ui.theme.screenPadding
import com.automotive.appstore.ui.theme.storeColors
import com.automotive.appstore.ui.theme.storeMetrics
import com.automotive.appstore.ui.theme.translator
import org.radioplayer.automotive.designsystem.components.composites.dialog.AutomotiveDialogColors
import org.radioplayer.automotive.designsystem.components.composites.dialog.AutomotiveDialogWithTitle
import org.radioplayer.automotive.designsystem.components.primitives.Text
import org.radioplayer.automotive.designsystem.components.primitives.icon.Icon
import org.radioplayer.automotive.designsystem.components.primitives.icon.IconSource
import org.radioplayer.automotive.designsystem.theme.AutomotiveTheme

/** The three tabs of the detail screen, matching the web `Tab` union. */
private enum class DetailTab { OVERVIEW, WHATS_NEW, CHANNELS }

/**
 * A single app's detail page. Port of the web `AppDetailScreen`.
 *
 * A header (icon, name, developer, version/size/channel, status, action button) sits above
 * an Overview / What's new / Channels tab bar. Selecting **Channels** reveals a row that
 * drills into the channel picker.
 */
@Composable
fun AppDetailScreen(
    viewModel: StoreViewModel,
    appId: String,
    onOpenChannels: () -> Unit,
    onBrowseStore: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val snapshot by viewModel.snapshot.collectAsStateWithLifecycle()
    var tab by remember(appId) { mutableStateOf(DetailTab.OVERVIEW) }

    if (snapshot.catalog.status == CatalogStatus.LOADING) {
        DetailSkeleton(modifier)
        return
    }

    val app = snapshot.catalog.apps.firstOrNull { it.id == appId }
    if (app == null) {
        StateMessage(
            modifier = modifier
                .fillMaxSize()
                .background(storeColors.background),
            icon = StoreIcons.NotFound,
            title = translator.t(StringKey.DETAIL_NOT_FOUND),
            body = translator.t(StringKey.DETAIL_NOT_FOUND_BODY),
            action = {
                TouchButton(
                    label = translator.t(StringKey.INSTALLED_BROWSE),
                    onClick = onBrowseStore,
                )
            },
        )
        return
    }

    val state = getAppState(snapshot, app)

    val metrics = storeMetrics

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(storeColors.background)
            .screenPadding(metrics),
        verticalArrangement = Arrangement.spacedBy(metrics.sectionGap),
    ) {
        item {
            DetailHeader(app = app, state = state, viewModel = viewModel)
        }
        item { DetailTabs(selected = tab, onSelect = { tab = it }) }
        item {
            when (tab) {
                DetailTab.OVERVIEW -> OverviewPanel(app = app)
                DetailTab.WHATS_NEW -> WhatsNewPanel(state = state)
                DetailTab.CHANNELS -> ChannelsPanel(app = app, state = state, onClick = onOpenChannels)
            }
        }
    }
}

/** The app summary card with its primary action. */
@Composable
private fun DetailHeader(app: AppListing, state: AppState, viewModel: StoreViewModel) {
    val displayVersion = state.currentVersion ?: state.release?.version.orEmpty()
    val showTarget = state.status == AppStatus.UPDATE_AVAILABLE || state.status == AppStatus.DOWNLOADING

    // Delete only makes sense for an app that is actually on the device, and never while a task is
    // running against it: removing a package mid-install would leave a task polling a dead app.
    val installed = state.status == AppStatus.INSTALLED ||
        state.status == AppStatus.UP_TO_DATE ||
        state.status == AppStatus.UPDATE_AVAILABLE ||
        state.status == AppStatus.FAILED
    var confirmingDelete by remember(app.id) { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(storeColors.radius3Xl))
            .background(storeColors.card)
            .padding(24.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(24.dp),
    ) {
        AppIconTile(icon = app.icon, color = app.iconColor, size = AppIconSize.XL, iconUrl = app.iconUrl)

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Column {
                Text(
                    text = app.name,
                    // Web `text-4xl font-bold` = 40.5px; `huge2` is Step11 = 80sp, roughly 2x.
                    style = StoreType.xxxxlBold,
                    color = storeColors.foreground,
                    maxLines = 2,
                )
                Text(
                    text = translator.t(StringKey.DETAIL_DEVELOPER, "name" to app.developer),
                    // Web `text-xl text-muted-foreground` = 22.5px.
                    style = StoreType.xl,
                    color = storeColors.mutedForeground,
                    maxLines = 1,
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(32.dp)) {
                MetaItem(translator.t(StringKey.DETAIL_VERSION)) {
                    VersionChange(
                        from = displayVersion,
                        to = if (showTarget) state.targetVersion else null,
                    )
                }
                MetaItem(translator.t(StringKey.DETAIL_SIZE)) {
                    Text(
                        text = translator.t(StringKey.DETAIL_SIZE_VALUE, "n" to app.sizeMb),
                        // Web `text-lg` in the header `dl`.
                        style = StoreType.lg,
                        color = storeColors.foreground,
                        maxLines = 1,
                    )
                }
                if (state.channel != null) {
                    MetaItem(translator.t(StringKey.DETAIL_CHANNEL)) {
                        Text(
                            text = state.channel.name,
                            // Web `text-lg` in the header `dl`.
                            style = StoreType.lg,
                            color = if (state.channel.id != DEFAULT_CHANNEL_ID) {
                                storeColors.warning
                            } else {
                                storeColors.foreground
                            },
                            maxLines = 1,
                        )
                    }
                }
            }

            StatusChip(
                status = state.status,
                label = statusLabelFor(state),
                progress = state.progress,
            )

            StatusProgressBar(status = state.status, progress = state.progress)
        }

        Column(
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            AppActionButton(
                appId = app.id,
                appName = app.name,
                state = state,
                onInstall = viewModel::install,
                onUpdate = viewModel::update,
                onCancel = viewModel::cancel,
                onRetry = viewModel::retry,
                onOpen = viewModel::launch,
            )

            // Removal is destructive, so it sits below the primary action as a quiet outline
            // button rather than competing with it, and is confirmed before it runs.
            if (installed) {
                val deleteLabel = translator.t(StringKey.ACTION_DELETE)
                TouchButton(
                    label = deleteLabel,
                    onClick = { confirmingDelete = true },
                    icon = StoreIcons.Delete,
                    variant = TouchVariant.OUTLINE,
                    modifier = Modifier.semantics {
                        contentDescription = "$deleteLabel ${app.name}"
                    },
                )
            }
        }
    }

    if (confirmingDelete) {
        DeleteConfirmDialog(
            appName = app.name,
            onConfirm = {
                confirmingDelete = false
                viewModel.uninstall(app.id)
            },
            onDismiss = { confirmingDelete = false },
        )
    }
}

/**
 * Asks before removing an app.
 *
 * Removing an app is destructive and not undoable from the store's own UI — the app has to be
 * downloaded again — so it is confirmed here, mirroring the channel switch's confirmation strip
 * in `ChannelsScreen`.
 */
@Composable
private fun DeleteConfirmDialog(
    appName: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    AutomotiveDialogWithTitle(
        onDismissRequest = onDismiss,
        title = translator.t(StringKey.DETAIL_DELETE_CONFIRM_TITLE, "name" to appName),
        content = translator.t(StringKey.DETAIL_DELETE_CONFIRM_BODY, "name" to appName),
        // The design system's dialog container/content roles come from the neutral ramp, so they
        // are overridden with the store's own card/foreground roles to keep this legible in both
        // themes — the same reason TouchButton passes its variants explicitly.
        colors = AutomotiveDialogColors(
            container = storeColors.card,
            title = storeColors.foreground,
            content = storeColors.mutedForeground,
            shadow = Color.Black.copy(alpha = 0.4f),
        ),
        // `actions` is declared before `colors`, so it is passed by name: a trailing lambda here
        // would bind to `colors` instead.
        actions = {
            TouchButton(
                label = translator.t(StringKey.DETAIL_DELETE_KEEP, "name" to appName),
                onClick = onDismiss,
                variant = TouchVariant.OUTLINE,
                modifier = Modifier.weight(1f),
            )
            TouchButton(
                label = translator.t(StringKey.DETAIL_DELETE_CONFIRM),
                onClick = onConfirm,
                icon = StoreIcons.Delete,
                variant = TouchVariant.DESTRUCTIVE,
                modifier = Modifier.weight(1f),
            )
        },
    )
}

/** A `label: value` pair from the detail header's definition list. */
@Composable
private fun MetaItem(label: String, value: @Composable () -> Unit) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = label,
            // Web `text-lg` meta label (`dl` inherits text-lg).
            style = StoreType.lg,
            color = storeColors.mutedForeground,
            maxLines = 1,
        )
        value()
    }
}

/** The segmented tab bar, matching the web `role="tablist"` block. */
@Composable
private fun DetailTabs(selected: DetailTab, onSelect: (DetailTab) -> Unit) {
    val options = listOf(
        DetailTab.OVERVIEW to translator.t(StringKey.DETAIL_OVERVIEW),
        DetailTab.WHATS_NEW to translator.t(StringKey.DETAIL_WHATS_NEW),
        DetailTab.CHANNELS to translator.t(StringKey.DETAIL_CHANNELS),
    )
    val tabsLabel = translator.t(StringKey.DETAIL_TABS)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(storeColors.radius3Xl))
            .background(storeColors.card)
            .padding(8.dp)
            .semantics { contentDescription = tabsLabel },
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        options.forEach { (tab, label) ->
            DetailTabButton(
                label = label,
                selected = tab == selected,
                onClick = { onSelect(tab) },
                modifier = Modifier.weight(1f),
            )
        }
    }
}

/** One tab button; selected tabs use the web app's `bg-secondary` treatment. */
@Composable
private fun DetailTabButton(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val shape = RoundedCornerShape(storeColors.radiusLg)
    Surface(
        onClick = onClick,
        modifier = modifier
            .height(76.dp)
            .clip(shape)
            .semantics { contentDescription = label },
        shape = shape,
        color = if (selected) storeColors.secondary else storeColors.card,
        contentColor = if (selected) storeColors.foreground else storeColors.mutedForeground,
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = label,
                // Web `text-lg font-semibold` segmented control.
                style = StoreType.lgSemibold,
                color = if (selected) storeColors.foreground else storeColors.mutedForeground,
                maxLines = 1,
            )
        }
    }
}

/** Description, screenshot carousel and permission chips. Port of the web `OverviewPanel`. */
@Composable
private fun OverviewPanel(app: AppListing) {
    Column(verticalArrangement = Arrangement.spacedBy(32.dp)) {
        Text(
            text = app.description,
            // Web `text-xl leading-relaxed`.
            style = StoreType.xl,
            color = storeColors.foreground,
            maxLines = 6,
        )

        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Text(
                text = translator.t(StringKey.DETAIL_SCREENSHOTS),
                // Web `text-2xl font-bold` = 27px; `display3` is Step7 = 36sp.
                style = StoreType.xxlBold,
                color = storeColors.foreground,
                maxLines = 1,
            )
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                app.screenshots.forEachIndexed { index, resId ->
                    AsyncImage(
                        model = resId,
                        contentDescription = translator.t(
                            StringKey.DETAIL_SCREENSHOT_ALT,
                            "name" to app.name,
                            "n" to (index + 1),
                        ),
                        modifier = Modifier
                            .width(storeMetrics.screenshotWidth)
                            .aspectRatio(16f / 9f)
                            .clip(RoundedCornerShape(storeColors.radiusLg))
                            .background(storeColors.muted),
                        contentScale = ContentScale.Crop,
                    )
                }
            }
        }

        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Text(
                text = translator.t(StringKey.DETAIL_PERMISSIONS),
                // Web `text-2xl font-bold`.
                style = StoreType.xxlBold,
                color = storeColors.foreground,
                maxLines = 1,
            )
            app.permissions.chunked(4).forEach { row ->
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    row.forEach { permission ->
                        PermissionChip(
                            permission = permission,
                            modifier = Modifier.weight(1f),
                        )
                    }
                    // Keep the last row's chips the same width as a full row.
                    repeat(4 - row.size) { Box(modifier = Modifier.weight(1f)) }
                }
            }
        }
    }
}

/** A permission row: muted glyph plus its localized name. */
@Composable
private fun PermissionChip(permission: PermissionId, modifier: Modifier = Modifier) {
    val label = translator.t(permissionLabelKey(permission))

    Row(
        modifier = modifier
            .height(76.dp)
            .clip(RoundedCornerShape(storeColors.radiusLg))
            .background(storeColors.card)
            .padding(horizontal = 20.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Icon(
            source = IconSource.Vector(StoreIcons.forPermission(permission)),
            size = AutomotiveTheme.icon.primary,
            color = storeColors.mutedForeground,
        )
        Text(
            text = label,
            // Web `text-lg font-medium` permission chip.
            style = StoreType.lgMedium,
            color = storeColors.foreground,
            maxLines = 2,
        )
    }
}

/** Maps a [PermissionId] to its translation key. */
private fun permissionLabelKey(permission: PermissionId): StringKey = when (permission) {
    PermissionId.LOCATION -> StringKey.PERMISSION_LOCATION
    PermissionId.MICROPHONE -> StringKey.PERMISSION_MICROPHONE
    PermissionId.CONTACTS -> StringKey.PERMISSION_CONTACTS
    PermissionId.VEHICLE_DATA -> StringKey.PERMISSION_VEHICLE_DATA
    PermissionId.NOTIFICATIONS -> StringKey.PERMISSION_NOTIFICATIONS
    PermissionId.STORAGE -> StringKey.PERMISSION_STORAGE
    PermissionId.PHONE -> StringKey.PERMISSION_PHONE
}

/** The changelog for the currently selected channel. Port of the web `WhatsNewPanel`. */
@Composable
private fun WhatsNewPanel(state: AppState) {
    val release = state.release ?: return
    Column(
        modifier = Modifier
            .width(storeMetrics.maxReadingWidth)
            .clip(RoundedCornerShape(storeColors.radius3Xl))
            .background(storeColors.card)
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Text(
                text = translator.t(StringKey.DETAIL_NEW_IN, "version" to release.version),
                modifier = Modifier.weight(1f),
                // Web `text-2xl font-bold`.
                style = StoreType.xxlBold,
                color = storeColors.foreground,
                maxLines = 2,
            )
            Text(
                text = translator.t(
                    StringKey.DETAIL_RELEASED_ON,
                    "date" to LocalTranslator.current.formatDate(release.releaseDate),
                ),
                // Web `text-lg text-muted-foreground`.
                style = StoreType.lg,
                color = storeColors.mutedForeground,
                maxLines = 1,
            )
        }
        release.changelog.forEach { entry -> ChangelogRow(entry) }
    }
}

/** One bullet in the changelog list. */
@Composable
private fun ChangelogRow(entry: String) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.Top,
    ) {
        Box(
            modifier = Modifier
                .padding(top = 12.dp)
                .size(10.dp)
                .clip(CircleShape)
                .background(storeColors.primary),
        )
        Text(
            text = entry,
            // Web `text-xl` changelog entry.
            style = StoreType.xl,
            color = storeColors.foreground,
            maxLines = 3,
        )
    }
}

/** The row that drills into the channel picker. Port of the web `ChannelsPanel`. */
@Composable
private fun ChannelsPanel(app: AppListing, state: AppState, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .width(storeMetrics.maxReadingWidth)
            .clip(RoundedCornerShape(storeColors.radius3Xl))
            .background(storeColors.card)
            .clickable(onClick = onClick)
            .padding(24.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        Box(
            modifier = Modifier
                .size(76.dp)
                .clip(RoundedCornerShape(storeColors.radiusLg))
                .background(storeColors.primary.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                source = IconSource.Vector(StoreIcons.Layers),
                size = AutomotiveTheme.icon.hero,
                color = storeColors.primary,
            )
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = translator.t(StringKey.DETAIL_ACTIVE_CHANNEL),
                // Web `text-lg text-muted-foreground`.
                style = StoreType.lg,
                color = storeColors.mutedForeground,
                maxLines = 1,
            )
            Text(
                text = state.channel?.name.orEmpty(),
                // Web `text-2xl font-bold` channel name.
                style = StoreType.xxlBold,
                color = storeColors.foreground,
                maxLines = 1,
            )
            Text(
                text = translator.t(StringKey.DETAIL_CHANNELS_AVAILABLE, "n" to app.releases.size),
                // Web `text-lg text-muted-foreground`.
                style = StoreType.lg,
                color = storeColors.mutedForeground,
                maxLines = 1,
            )
        }
        Text(
            text = translator.t(StringKey.DETAIL_CHOOSE_CHANNEL),
            // Web `text-lg font-semibold text-primary`.
            style = StoreType.lgSemibold,
            color = storeColors.primary,
            textAlign = TextAlign.End,
            maxLines = 1,
        )
    }
}

/** The loading placeholder, matching the web `DetailSkeleton`. */
@Composable
private fun DetailSkeleton(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(storeColors.background)
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(storeColors.radius3Xl))
                .background(storeColors.card)
                .padding(24.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(24.dp),
        ) {
            SkeletonBlock(width = 128.dp, height = 128.dp, corner = 32.dp)
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                SkeletonBlock(width = 280.dp, height = 36.dp)
                SkeletonBlock(width = 180.dp, height = 24.dp)
                SkeletonBlock(width = 240.dp, height = 24.dp)
            }
        }
        SkeletonBlock(width = 400.dp, height = 92.dp, corner = 39.dp)
    }
}
