package org.radioplayer.automotive.designsystem.components.composites.bottomsheet

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import org.radioplayer.automotive.designsystem.components.composites.tab.TabContent
import org.radioplayer.automotive.designsystem.components.composites.tab.TabGroup
import org.radioplayer.automotive.designsystem.components.features.schedule.listitemschedule.ListItemSchedule
import org.radioplayer.automotive.designsystem.components.primitives.Text
import org.radioplayer.automotive.designsystem.theme.AutomotiveTheme

/** One row in a [ScheduleDay]'s list, driving a [ListItemSchedule]. */
data class ScheduleRow(
    val time: String,
    val title: String,
    val isLive: Boolean = false,
    val active: Boolean = false,
    val enabled: Boolean = true,
    val leadingContent: (@Composable () -> Unit)? = null,
)

/** One tab's worth of schedule rows. */
data class ScheduleDay(
    val label: String,
    val rows: List<ScheduleRow>,
)

/**
 * `Bottom Sheet / Programme Schedule`: a day-tab row, driven by the design system's own
 * [TabGroup]/[TabItem][org.radioplayer.automotive.designsystem.components.composites.tab.TabItem]
 * (which already resolves `AutomotiveTheme.typography`, the shared rotary/keyboard focus ring,
 * and the 76dp minimum touch target for its icon/flag slots — the same tokens and
 * `rememberInteractionState`/focus-ring pattern every other interactive composite in this module
 * uses) rather than Material3's bare `TabRow`/`Tab`, followed by a scrollable list of
 * [ListItemSchedule] rows for the selected day.
 *
 * Day-tab selection is kept as internal UI state rather than hoisted — it's a pure display
 * concern of this composable, not something a caller needs to drive externally. Uses
 * `rememberSaveable` rather than plain `remember` so the selected day survives process death /
 * config change, not just recomposition — it still resets to day 0 when the sheet is fully
 * dismissed and reopened, since the whole `Dialog` (and this composable with it) leaves
 * composition at that point.
 */
@Composable
fun BottomSheetProgrammeSchedule(
    visible: Boolean,
    onDismissRequest: () -> Unit,
    days: List<ScheduleDay>,
    modifier: Modifier = Modifier,
    title: String = "Programme Schedule",
) {
    var selectedDayIndex by rememberSaveable { mutableIntStateOf(0) }
    val selectedDay = days.getOrNull(selectedDayIndex) ?: days.firstOrNull()

    CarBottomSheetScaffold(
        visible = visible,
        onDismissRequest = onDismissRequest,
        title = title,
        modifier = modifier,
    ) {
        if (days.size > 1) {
            TabGroup(
                tabs = days.map { day -> TabContent(text = { Text(text = day.label) }) },
                selectedIndex = selectedDayIndex,
                onTabSelected = { selectedDayIndex = it },
            )
        }

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(AutomotiveTheme.measurement.spaces.medium),
        ) {
            items(selectedDay?.rows.orEmpty()) { row ->
                ListItemSchedule(
                    onClick = {},
                    isLive = row.isLive,
                    active = row.active,
                    enabled = row.enabled,
                    headlineContent = { Text(row.time) },
                    supportingContent = { Text(row.title) },
                    leadingContent = row.leadingContent,
                )
            }
            item {
                Spacer(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(CarBottomSheetScaffoldDefaults.BottomContentSpacerHeight)
                )
            }
        }
    }
}
