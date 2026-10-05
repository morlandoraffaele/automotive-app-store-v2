package com.automotive.appstore.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.automotive.appstore.StoreViewModel
import com.automotive.appstore.data.AppState
import com.automotive.appstore.data.CatalogStatus
import com.automotive.appstore.data.ChannelDefinition
import com.automotive.appstore.data.ChannelRelease
import com.automotive.appstore.data.StringKey
import com.automotive.appstore.data.getAppState
import com.automotive.appstore.ui.components.AppActionButton
import com.automotive.appstore.ui.components.StateMessage
import com.automotive.appstore.ui.components.StoreIcons
import com.automotive.appstore.ui.components.TouchButton
import com.automotive.appstore.ui.components.TouchVariant
import com.automotive.appstore.ui.theme.LocalTranslator
import com.automotive.appstore.ui.theme.StoreType
import com.automotive.appstore.ui.theme.screenPadding
import com.automotive.appstore.ui.theme.storeColors
import com.automotive.appstore.ui.theme.storeMetrics
import com.automotive.appstore.ui.theme.translator
import org.radioplayer.automotive.designsystem.components.primitives.Text
import org.radioplayer.automotive.designsystem.components.primitives.icon.Icon
import org.radioplayer.automotive.designsystem.components.primitives.icon.IconSource
import org.radioplayer.automotive.designsystem.theme.AutomotiveTheme

/** A channel the app actually publishes a build for. */
private data class ChannelOption(val channel: ChannelDefinition, val release: ChannelRelease)

/**
 * The release-channel picker. Port of the web `ChannelsScreen`.
 *
 * Channels are listed most-stable first. Picking a *less* stable channel than the current
 * one first shows a warning strip with confirm/cancel, mirroring the web behaviour where a
 * downgrade in stability is a deliberate, confirmed action.
 */
@Composable
fun ChannelsScreen(
    viewModel: StoreViewModel,
    appId: String,
    onBrowseStore: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val snapshot by viewModel.snapshot.collectAsStateWithLifecycle()
    var pending by remember(appId) { mutableStateOf<ChannelDefinition?>(null) }

    if (snapshot.catalog.status == CatalogStatus.LOADING) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(storeColors.background)
        )
        return
    }

    val app = snapshot.catalog.apps.firstOrNull { it.id == appId }
    val state = app?.let { getAppState(snapshot, it) }

    if (app == null || state == null || state.channel == null) {
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

    val active = state.channel
    val options = snapshot.channels
        .mapNotNull { channel ->
            app.releases.firstOrNull { it.channelId == channel.id }
                ?.let { ChannelOption(channel, it) }
        }
        .sortedBy { it.channel.stabilityRank }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(storeColors.background)
            .screenPadding(),
        verticalArrangement = Arrangement.spacedBy(storeMetrics.sectionGap),
    ) {
        item {
            Column {
                Text(
                    text = translator.t(StringKey.CHANNELS_TITLE, "name" to app.name),
                    // Web `text-2xl font-bold` = 27px; `display3` is Step7 = 36sp.
                    style = StoreType.xxlBold,
                    color = storeColors.foreground,
                    maxLines = 2,
                )
                Text(
                    text = translator.t(StringKey.CHANNELS_SUBTITLE),
                    // Web `text-lg text-muted-foreground`.
                    style = StoreType.lg,
                    color = storeColors.mutedForeground,
                    maxLines = 3,
                )
            }
        }

        pending?.let { target ->
            item {
                ChannelWarning(
                    target = target,
                    current = active,
                    onConfirm = {
                        viewModel.switchChannel(app.id, target.id)
                        pending = null
                    },
                    onDismiss = { pending = null },
                )
            }
        }

        items(options.size) { index ->
            val option = options[index]
            ChannelRow(
                option = option,
                active = option.channel.id == active.id,
                onSelect = { target ->
                    // Selecting a less stable channel needs explicit confirmation.
                    if (target.channel.stabilityRank > active.stabilityRank) {
                        pending = target.channel
                    } else {
                        viewModel.switchChannel(app.id, target.channel.id)
                    }
                },
            )
        }

        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
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
    }
}

/** The amber warning strip shown before switching to a less stable channel. */
@Composable
private fun ChannelWarning(
    target: ChannelDefinition,
    current: ChannelDefinition,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(storeColors.radius3Xl))
            .border(2.dp, storeColors.warning, RoundedCornerShape(storeColors.radius3Xl))
            .background(storeColors.warning.copy(alpha = 0.1f))
            .padding(24.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        Icon(
            source = IconSource.Vector(StoreIcons.WarningAmber),
            size = AutomotiveTheme.icon.hero,
            color = storeColors.warning,
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = translator.t(StringKey.CHANNELS_WARN_TITLE, "name" to target.name),
                // Web `text-2xl font-bold`.
                style = StoreType.xxlBold,
                color = storeColors.foreground,
                maxLines = 2,
            )
            Text(
                text = translator.t(
                    StringKey.CHANNELS_WARN_BODY,
                    "name" to target.name,
                    "current" to current.name,
                ),
                // Web `text-lg text-pretty`.
                style = StoreType.lg,
                color = storeColors.foreground,
                maxLines = 4,
            )
        }
        TouchButton(
            label = translator.t(StringKey.CHANNELS_CANCEL, "name" to target.name),
            onClick = onDismiss,
            variant = TouchVariant.OUTLINE,
        )
        TouchButton(
            label = translator.t(StringKey.CHANNELS_CONFIRM),
            onClick = onConfirm,
        )
    }
}

/** One selectable channel: radio, name, stability pill, changelog preview, latest version. */
@Composable
private fun ChannelRow(
    option: ChannelOption,
    active: Boolean,
    onSelect: (ChannelOption) -> Unit,
) {
    val locked = option.channel.locked
    val shape = RoundedCornerShape(storeColors.radius3Xl)
    val stabilityKey = when (option.channel.stabilityRank.coerceIn(0, 3)) {
        0 -> StringKey.CHANNELS_STABILITY_RANK_0
        1 -> StringKey.CHANNELS_STABILITY_RANK_1
        2 -> StringKey.CHANNELS_STABILITY_RANK_2
        else -> StringKey.CHANNELS_STABILITY_RANK_3
    }
    val stable = option.channel.stabilityRank == 0

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(storeColors.card)
            .clickable(enabled = !locked && !active) { onSelect(option) }
            .padding(24.dp),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        // Radio indicator: a ring holding a filled dot, a lock, or nothing.
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .border(
                    width = 4.dp,
                    color = if (active) storeColors.primary else storeColors.border,
                    shape = CircleShape,
                ),
            contentAlignment = Alignment.Center,
        ) {
            when {
                locked -> Icon(
                    source = IconSource.Vector(StoreIcons.Locked),
                    size = 20.dp,
                    color = storeColors.mutedForeground,
                )

                active -> Box(
                    modifier = Modifier
                        .size(16.dp)
                        .clip(CircleShape)
                        .background(storeColors.primary)
                )
            }
        }

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Text(
                    text = option.channel.name,
                    // Web `text-2xl font-bold`.
                    style = StoreType.xxlBold,
                    color = storeColors.foreground,
                    maxLines = 1,
                )
                Pill(
                    label = translator.t(stabilityKey),
                    background = if (stable) {
                        storeColors.success.copy(alpha = 0.15f)
                    } else {
                        storeColors.warning.copy(alpha = 0.15f)
                    },
                    content = if (stable) storeColors.success else storeColors.warning,
                )
                if (active) {
                    Pill(
                        label = translator.t(StringKey.CHANNELS_ACTIVE),
                        background = storeColors.primary,
                        content = storeColors.primaryForeground,
                    )
                }
                if (locked) {
                    Pill(
                        label = translator.t(StringKey.CHANNELS_LOCKED),
                        background = storeColors.muted,
                        content = storeColors.mutedForeground,
                    )
                }
            }

            Text(
                text = option.channel.description,
                // Web `text-lg text-muted-foreground text-pretty`.
                style = StoreType.lg,
                color = storeColors.mutedForeground,
                maxLines = 3,
            )

            if (locked && option.channel.lockReason != null) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Icon(
                        source = IconSource.Vector(StoreIcons.Locked),
                        size = 20.dp,
                        color = storeColors.foreground,
                    )
                    Text(
                        text = option.channel.lockReason,
                        // Web `text-lg font-medium`.
                        style = StoreType.lgMedium,
                        color = storeColors.foreground,
                        maxLines = 2,
                    )
                }
            }

            option.release.changelog.take(2).forEach { entry ->
                Row(
                    verticalAlignment = Alignment.Top,
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    Box(
                        modifier = Modifier
                            .padding(top = 10.dp)
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(storeColors.mutedForeground)
                    )
                    Text(
                        text = entry,
                        // Web `text-lg` changelog entry.
                        style = StoreType.lg,
                        color = storeColors.foreground,
                        maxLines = 2,
                    )
                }
            }
        }

        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = translator.t(
                    StringKey.CHANNELS_LATEST,
                    "version" to option.release.version,
                ),
                // Web `font-mono text-xl font-semibold tabular-nums`.
                style = StoreType.xlSemibold.copy(fontFamily = FontFamily.Monospace),
                color = storeColors.foreground,
                maxLines = 1,
            )
            Text(
                text = translator.t(
                    StringKey.DETAIL_RELEASED_ON,
                    "date" to LocalTranslator.current.formatDate(option.release.releaseDate),
                ),
                // Web `text-lg text-muted-foreground`.
                style = StoreType.lg,
                color = storeColors.mutedForeground,
                maxLines = 1,
            )
        }
    }
}

/** A small rounded label used for the stability / active / locked indicators. */
@Composable
private fun Pill(label: String, background: androidx.compose.ui.graphics.Color, content: androidx.compose.ui.graphics.Color) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(background)
            .padding(horizontal = 12.dp, vertical = 4.dp)
            .semantics { contentDescription = label },
    ) {
        Text(
            text = label,
            // Web `text-sm font-semibold` / `font-bold` channel pill.
            style = StoreType.smSemibold,
            color = content,
            maxLines = 1,
        )
    }
}
