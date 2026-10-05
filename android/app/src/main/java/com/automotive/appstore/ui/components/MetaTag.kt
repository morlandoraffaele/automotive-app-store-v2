package com.automotive.appstore.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.automotive.appstore.ui.theme.StoreType
import com.automotive.appstore.ui.theme.storeColors
import org.radioplayer.automotive.designsystem.components.primitives.Text

/**
 * A labelled metadata pill for the detail header.
 *
 * Each field gets its own [tint] so the row can be scanned without reading it: three differently
 * coloured pills are separable at a glance on a head unit, where three same-coloured ones are not.
 * The tint is applied to both the label and the value at different weights, and to a low-alpha
 * container, so the pill still reads correctly in the day and night palettes without needing a
 * contrasting foreground computed per theme.
 *
 * @param label what the value is, e.g. `Code`.
 * @param value the value itself, e.g. `20`.
 * @param tint this field's colour; distinct per field.
 */
@Composable
fun MetaTag(
    label: String,
    value: String,
    tint: Color,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(percent = 50))
            .background(tint.copy(alpha = 0.15f))
            .padding(horizontal = 16.dp, vertical = 10.dp)
            // Read as one phrase, not as two fragments.
            .semantics { contentDescription = "$label $value" },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Text(
            text = label,
            style = StoreType.smSemibold,
            color = tint.copy(alpha = 0.8f),
            maxLines = 1,
        )
        Text(
            text = value,
            style = StoreType.smSemibold,
            color = tint,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

/**
 * Wraps tags onto multiple lines when they do not fit the header's width.
 *
 * A plain `Row` would clip the third pill on a narrow head unit or a long `versionName`, and the
 * header is the one place a truncated version reads as a wrong version.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun MetaTagRow(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    FlowRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        content()
    }
}