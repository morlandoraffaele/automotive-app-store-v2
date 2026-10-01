package org.radioplayer.automotive.designsystem.components.composites.alphascroller

import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import org.radioplayer.automotive.designsystem.components.primitives.Text
import org.radioplayer.automotive.designsystem.interaction.rememberInteractionState
import org.radioplayer.automotive.designsystem.interaction.rememberSystemFocusRing
import org.radioplayer.automotive.designsystem.theme.AutomotiveTheme

/**
 * `List Item / Alpha Indexer - No Indicator`: a single letter cell, 76×76dp (matching
 * [AutomotiveTheme.measurement.sizes.minTapArea], the same tap-target size every other list
 * item/icon button in this design system already uses). Selected uses `display1Medium`
 * (56sp/64lh, Medium), unselected uses `display3` (36sp/44lh, Regular) — an exact match to the
 * source JSON's two font sizes/weights, confirmed against this project's own typography ladder.
 *
 * The source component's own Selected/Focused/Pressed/Disabled variants all resolve to the
 * *exact same* text color (`sys/color/On Surface` in every one, including Disabled) — treated
 * here as unfinished source data rather than copied literally: [enabled] dims content the same
 * way every other disableable component in this module does (see [AlphaScrollerDefaults]), and
 * focus uses the same shared system focus ring every other focusable primitive here uses
 * ([rememberSystemFocusRing]), rather than shipping a letter index with no visible rotary/D-pad
 * focus indication at all.
 */
@Composable
fun AlphaScrollerLetterCell(
    letter: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    interactionSource: MutableInteractionSource? = null,
) {
    val resolvedInteractionSource = interactionSource ?: remember { MutableInteractionSource() }
    val interactionState by rememberInteractionState(resolvedInteractionSource)
    val focusBorder = rememberSystemFocusRing(interactionState, null)

    Box(
        modifier = modifier
            .size(AutomotiveTheme.measurement.sizes.minTapArea)
            .let { base ->
                focusBorder?.let {
                    base.border(
                        it,
                        RoundedCornerShape(AutomotiveTheme.measurement.shapes.full)
                    )
                } ?: base
            }
            .clip(RoundedCornerShape(AutomotiveTheme.measurement.shapes.full))
            .clickable(
                enabled = enabled,
                interactionSource = resolvedInteractionSource,
                indication = LocalIndication.current,
                onClick = onClick,
            )
            .semantics {
                contentDescription = letter
                role = Role.Button
            },
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = letter.uppercase(),
            style = AlphaScrollerDefaults.textStyle(selected),
            color = AlphaScrollerDefaults.contentColor(selected = selected, enabled = enabled),
        )
    }
}
