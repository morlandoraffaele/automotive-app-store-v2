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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.automotive.appstore.StoreViewModel
import com.automotive.appstore.data.AppStatus
import com.automotive.appstore.data.CatalogSimulation
import com.automotive.appstore.data.Locale
import com.automotive.appstore.data.STORE_APP_PACKAGE
import com.automotive.appstore.data.StoreSettings
import com.automotive.appstore.data.StringKey
import com.automotive.appstore.data.ThemeMode
import com.automotive.appstore.data.getAppState
import com.automotive.appstore.ui.components.ProgressRing
import com.automotive.appstore.ui.components.SkeletonBlock
import com.automotive.appstore.ui.components.StoreIcons
import com.automotive.appstore.ui.components.rememberTapTracker
import com.automotive.appstore.ui.theme.LocalTranslator
import com.automotive.appstore.ui.theme.StoreType
import com.automotive.appstore.ui.theme.storeColors
import com.automotive.appstore.ui.theme.screenPadding
import com.automotive.appstore.ui.theme.storeMetrics
import com.automotive.appstore.ui.theme.translator
import org.radioplayer.automotive.designsystem.components.primitives.Text
import org.radioplayer.automotive.designsystem.components.primitives.icon.Icon
import org.radioplayer.automotive.designsystem.components.primitives.icon.IconSource
import org.radioplayer.automotive.designsystem.theme.AutomotiveTheme

/**
 * Store settings. Port of the web `SettingsScreen`.
 *
 * Four sections, each a rounded card of rows: Updates (two switches), About (store version
 * plus a check-for-update action), Display (theme and language segmented controls) and a Demo
 * section that switches the simulated catalog state.
 */
@Composable
fun SettingsScreen(
    viewModel: StoreViewModel,
    modifier: Modifier = Modifier,
) {
    val snapshot by viewModel.snapshot.collectAsStateWithLifecycle()
    val settings = snapshot.settings

    val metrics = storeMetrics

    // Display, Language and Demo are developer affordances. They stay hidden until the user taps
    // the store version row 20 times; the tracker lives in memory only, so a process restart
    // re-hides them.
    val advancedTracker = rememberTapTracker()
    val advancedUnlocked = advancedTracker.unlocked

    LazyColumn(
        // screenPadding, not a bare top spacer: the settings cards previously ran flush against
        // the left and right edges while every other screen had a margin.
        modifier = modifier
            .fillMaxSize()
            .background(storeColors.background)
            .screenPadding(metrics),
        verticalArrangement = Arrangement.spacedBy(metrics.sectionGap * 1.33f),
    ) {
        item {
            SettingsSection(title = translator.t(StringKey.SETTINGS_UPDATES)) {
                ToggleRow(
                    label = translator.t(StringKey.SETTINGS_AUTO_UPDATE),
                    description = translator.t(StringKey.SETTINGS_AUTO_UPDATE_DESC),
                    checked = settings.autoUpdate,
                    onChange = { viewModel.updateSettings(settings.copy(autoUpdate = it)) },
                )
                SettingsDivider()
                ToggleRow(
                    label = translator.t(StringKey.SETTINGS_WIFI_ONLY),
                    description = translator.t(StringKey.SETTINGS_WIFI_ONLY_DESC),
                    checked = settings.wifiOnly,
                    onChange = { viewModel.updateSettings(settings.copy(wifiOnly = it)) },
                )
            }
        }

        item {
            SettingsSection(title = translator.t(StringKey.SETTINGS_ABOUT)) {
                StoreVersionRow(
                    viewModel = viewModel,
                    onCheck = viewModel::checkForStoreUpdate,
                    onTapVersion = advancedTracker::registerTap,
                )
                // The install lives here rather than in the header because it replaces the running
                // process: it needs to be a deliberate, labelled tap, not a header control a driver
                // can hit while swiping between screens. Only shown once an update has been found.
                val storeUpdate = snapshot.storeUpdate
                val storeListing = snapshot.catalog.apps
                    .firstOrNull { it.packageName == STORE_APP_PACKAGE }
                if (storeUpdate.availableVersion != null && storeListing != null) {
                    SettingsDivider()
                    // Derived with the same selector every app uses, so an in-flight download, a
                    // failure and a completed update read exactly as they do in the catalogue.
                    // Self-update is the normal install flow run against the store's own entry.
                    val storeState = getAppState(snapshot, storeListing)
                    StoreUpdateAction(
                        label = translator.t(StringKey.SETTINGS_UPDATE_STORE),
                        status = storeState.status,
                        progress = storeState.progress,
                        onClick = viewModel::updateStore,
                    )
                }
            }
        }

        if (advancedUnlocked) {
            item {
                SettingsSection(title = translator.t(StringKey.SETTINGS_DISPLAY)) {
                    SegmentedRow(
                        label = translator.t(StringKey.SETTINGS_THEME),
                        options = listOf(
                            ThemeMode.DAY to translator.t(StringKey.SETTINGS_DAY),
                            ThemeMode.NIGHT to translator.t(StringKey.SETTINGS_NIGHT),
                        ),
                        selected = settings.theme,
                        onSelect = { viewModel.updateSettings(settings.copy(theme = it)) },
                    )
                    SettingsDivider()
                    SegmentedRow(
                        label = translator.t(StringKey.SETTINGS_LANGUAGE),
                        options = listOf(
                            Locale.EN to LocalTranslator.current.displayNameFor(Locale.EN),
                            Locale.AR to LocalTranslator.current.displayNameFor(Locale.AR),
                        ),
                        selected = settings.locale,
                        onSelect = { viewModel.updateSettings(settings.copy(locale = it)) },
                    )
                }
            }

            item {
                SettingsSection(title = translator.t(StringKey.SETTINGS_DEMO)) {
                SegmentedRow(
                        label = translator.t(StringKey.SETTINGS_SIMULATION),
                        options = listOf(
                            CatalogSimulation.NORMAL to translator.t(StringKey.SETTINGS_SIM_NORMAL),
                            CatalogSimulation.LOADING to translator.t(StringKey.SETTINGS_SIM_LOADING),
                            CatalogSimulation.EMPTY to translator.t(StringKey.SETTINGS_SIM_EMPTY),
                            CatalogSimulation.ERROR to translator.t(StringKey.SETTINGS_SIM_ERROR),
                        ),
                        selected = settings.simulation,
                        onSelect = { viewModel.updateSettings(settings.copy(simulation = it)) },
                    )
                }
            }
        }
    }
}

/** A titled group of rows on a card surface, matching the web `SettingsSection`. */
@Composable
private fun SettingsSection(title: String, content: @Composable () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(
            text = title,
            modifier = Modifier.padding(horizontal = 8.dp),
            // Web `text-lg font-semibold` section heading.
            style = StoreType.lgSemibold,
            color = storeColors.mutedForeground,
            maxLines = 1,
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(storeColors.radius3Xl))
                .background(storeColors.card),
        ) {
            content()
        }
    }
}

/** Hairline separator between rows inside a settings card. */
@Composable
private fun SettingsDivider() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(storeColors.border),
    )
}

/**
 * A label/description row with a switch.
 *
 * The whole row is the touch target, matching the web `ToggleRow`, which uses `role="switch"`
 * on the button itself rather than a separate switch control.
 */
@Composable
private fun ToggleRow(
    label: String,
    description: String,
    checked: Boolean,
    onChange: (Boolean) -> Unit,
) {
    val rowModifier = Modifier
        .fillMaxWidth()
        .height(96.dp)
        .clickable { onChange(!checked) }
        .semantics { role = Role.Switch }
        .padding(horizontal = 24.dp)

    Row(
        modifier = rowModifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(24.dp),
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                // Web `text-xl font-semibold`.
                style = StoreType.xlSemibold,
                color = storeColors.foreground,
                maxLines = 2,
            )
            Text(
                text = description,
                // Web `text-lg text-muted-foreground`.
                style = StoreType.lg,
                color = storeColors.mutedForeground,
                maxLines = 3,
            )
        }
        StoreSwitch(checked = checked)
    }
}

/** The visual switch: a pill track with a round thumb, matching the web toggle. */
@Composable
private fun StoreSwitch(checked: Boolean) {
    val track = if (checked) storeColors.primary else storeColors.mutedForeground.copy(alpha = 0.4f)
    Row(
        modifier = Modifier
            .size(width = 88.dp, height = 48.dp)
            .clip(CircleShape)
            .background(track)
            .padding(6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = if (checked) Arrangement.End else Arrangement.Start,
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(storeColors.background),
        )
    }
}

/** The store version readout plus the "check for update" action. */
@Composable
private fun StoreVersionRow(
    viewModel: StoreViewModel,
    onCheck: () -> Unit,
    onTapVersion: () -> Unit,
) {
    val snapshot by viewModel.snapshot.collectAsStateWithLifecycle()
    val storeUpdate = snapshot.storeUpdate
    val busy = storeUpdate.phase == com.automotive.appstore.data.StoreUpdatePhase.CHECKING ||
        storeUpdate.phase == com.automotive.appstore.data.StoreUpdatePhase.DOWNLOADING

    val statusText = when (storeUpdate.phase) {
        com.automotive.appstore.data.StoreUpdatePhase.CHECKING ->
            translator.t(StringKey.SETTINGS_STORE_CHECKING)

        com.automotive.appstore.data.StoreUpdatePhase.DOWNLOADING ->
            translator.t(
                StringKey.SETTINGS_STORE_DOWNLOADING,
                "version" to (storeUpdate.availableVersion.orEmpty()),
                "n" to storeUpdate.progress,
            )

        com.automotive.appstore.data.StoreUpdatePhase.READY ->
            translator.t(
                StringKey.SETTINGS_STORE_READY,
                "version" to (storeUpdate.availableVersion.orEmpty()),
            )

        com.automotive.appstore.data.StoreUpdatePhase.UP_TO_DATE ->
            translator.t(StringKey.SETTINGS_STORE_UP_TO_DATE)
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(96.dp)
            .padding(horizontal = 24.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(24.dp),
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = translator.t(StringKey.SETTINGS_STORE_VERSION),
                // Web `text-xl font-semibold`.
                style = StoreType.xlSemibold,
                color = storeColors.foreground,
                maxLines = 1,
            )
            Text(
                text = statusText,
                // Web `text-lg`.
                style = StoreType.lg,
                color = if (storeUpdate.phase == com.automotive.appstore.data.StoreUpdatePhase.READY) {
                    storeColors.primary
                } else {
                    storeColors.mutedForeground
                },
                maxLines = 2,
            )
        }
        // The version readout is the advanced-settings gesture target. Clickable but without a
        // ripple or role: it must not look interactive, or the hidden gesture becomes discoverable.
        Text(
            text = storeUpdate.currentVersion,
            // Web `font-mono text-xl tabular-nums`.
            modifier = Modifier.clickable(
                interactionSource = null,
                indication = null,
                onClick = onTapVersion,
            ),
            style = StoreType.xl.copy(fontFamily = FontFamily.Monospace),
            color = storeColors.foreground,
            maxLines = 1,
        )
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(96.dp)
            .clickable(enabled = !busy) { onCheck() }
            .padding(horizontal = 24.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        when (storeUpdate.phase) {
            com.automotive.appstore.data.StoreUpdatePhase.DOWNLOADING -> ProgressRing(
                progress = storeUpdate.progress,
                modifier = Modifier.size(32.dp),
            )

            com.automotive.appstore.data.StoreUpdatePhase.CHECKING ->
                SkeletonBlock(width = 32.dp, height = 32.dp, corner = 16.dp)

            else -> Icon(
                source = IconSource.Vector(StoreIcons.CheckForUpdate),
                size = AutomotiveTheme.icon.primary,
                color = storeColors.primary,
            )
        }
        Text(
            text = translator.t(StringKey.SETTINGS_CHECK_UPDATE),
            // Web `text-xl font-semibold text-primary`.
            style = StoreType.xlSemibold,
            color = storeColors.primary,
            maxLines = 1,
        )
    }
}

/**
 * The "Update store" action row, shown once a store update has been found.
 *
 * Replacing the running app is the most destructive thing this UI can do, so the row mirrors the
 * destructive treatment used elsewhere in the store (see `AppActionButton`'s FAILED branch) and is
 * never rendered speculatively — it appears only when [status] is a state with work left to do.
 */
@Composable
private fun StoreUpdateAction(
    label: String,
    status: AppStatus,
    progress: Int,
    onClick: () -> Unit,
) {
    val busy = status == AppStatus.DOWNLOADING || status == AppStatus.INSTALLING

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(96.dp)
            .clickable(enabled = !busy) { onClick() }
            .padding(horizontal = 24.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        when {
            status == AppStatus.DOWNLOADING -> ProgressRing(
                progress = progress,
                modifier = Modifier.size(32.dp),
            )

            status == AppStatus.INSTALLING ->
                SkeletonBlock(width = 32.dp, height = 32.dp, corner = 16.dp)

            else -> Icon(
                source = IconSource.Vector(StoreIcons.UpdateAll),
                size = AutomotiveTheme.icon.primary,
                color = storeColors.primary,
            )
        }
        Text(
            text = if (status == AppStatus.UP_TO_DATE || status == AppStatus.INSTALLED) {
                // The install already committed — which means this process is about to be replaced.
                translator.t(StringKey.SETTINGS_STORE_RESTARTING)
            } else {
                label
            },
            // Web `text-xl font-semibold text-primary`.
            style = StoreType.xlSemibold,
            color = storeColors.primary,
            maxLines = 2,
        )
    }
}

/**
 * A label with a segmented option group. Port of the web `SegmentedRow`.
 *
 * Used for theme, language and the demo simulation switch.
 */
@Composable
private fun <T> SegmentedRow(
    label: String,
    options: List<Pair<T, String>>,
    selected: T,
    onSelect: (T) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(96.dp)
            .padding(horizontal = 24.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(24.dp),
    ) {
        Text(
            text = label,
            modifier = Modifier.weight(1f),
            // Web `text-xl font-semibold`.
            style = StoreType.xlSemibold,
            color = storeColors.foreground,
            maxLines = 2,
        )
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(storeColors.radiusLg))
                .background(storeColors.muted)
                .padding(6.dp)
                .semantics { contentDescription = label },
            horizontalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            options.forEach { (value, optionLabel) ->
                SegmentedOption(
                    label = optionLabel,
                    selected = value == selected,
                    onClick = { onSelect(value) },
                )
            }
        }
    }
}

/** One option inside a [SegmentedRow]. */
@Composable
private fun SegmentedOption(label: String, selected: Boolean, onClick: () -> Unit) {
    val colors = storeColors
    val shape = RoundedCornerShape(colors.radiusMd)
    Surface(
        onClick = onClick,
        modifier = Modifier
            .height(64.dp)
            .clip(shape)
            .semantics { contentDescription = label },
        shape = shape,
        color = if (selected) colors.card else Color.Transparent,
        contentColor = if (selected) colors.foreground else colors.mutedForeground,
    ) {
        Box(
            modifier = Modifier.padding(horizontal = 20.dp),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = label,
                // Web `text-lg font-semibold`.
                style = StoreType.lgSemibold,
                color = if (selected) colors.foreground else colors.mutedForeground,
                maxLines = 1,
            )
        }
    }
}
