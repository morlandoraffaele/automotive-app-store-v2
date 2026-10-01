package org.radioplayer.automotive.designsystem.components.composites.header

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import org.radioplayer.automotive.designsystem.theme.AutomotiveTheme

/**
 * Shared internal layout for the `Header / Subroute / Default`, `Header / Subroute / Now
 * Playing`, and `Header / Subheader (Rail Header)` component families: a `Leading Items` slot
 * fixed at [AutomotiveTheme.measurement.sizes.minTapArea] (76dp, matching all three Figma
 * exports exactly), a `Content` slot that fills the remaining width, and a `Trailing Items` slot
 * that hugs its own content.
 *
 * Not public API — each header's own composable is the public surface; this only exists because
 * the same Leading/Content/Trailing shape showed up three real times (the same shape was
 * hand-built once already, inline, for the Bottom Sheet's own header in
 * `CarBottomSheetScaffold.kt` — that one predates this extraction and is left as-is since it's a
 * simpler, header-agnostic scaffold used by a different component family).
 *
 * @param gap Spacing between the three slots. Confirmed different per real usage (32dp for the
 * Subroute headers vs. 24dp for the Rail header, both real `Spaces` tokens) — not defaulted to
 * one hardcoded value, since guessing one would silently be wrong for whichever caller didn't
 * match it.
 */
@Composable
internal fun HeaderRow(
    modifier: Modifier = Modifier,
    gap: Dp,
    padding: PaddingValues = PaddingValues(
        top = AutomotiveTheme.measurement.spaces.extraSmall,
        bottom = AutomotiveTheme.measurement.spaces.medium,
    ),
    leading: (@Composable () -> Unit)? = null,
    content: @Composable () -> Unit,
    trailing: (@Composable () -> Unit)? = null,
) {
    Row(
        modifier = modifier.padding(padding),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(gap),
    ) {
        if (leading != null) {
            Row(
                modifier = Modifier.size(AutomotiveTheme.measurement.sizes.minTapArea),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                leading()
            }
        }
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            content()
        }
        if (trailing != null) {
            trailing()
        }
    }
}
