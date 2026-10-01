package org.radioplayer.automotive.designsystem.components.composites.bottomsheet

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import org.radioplayer.automotive.designsystem.components.primitives.Text
import org.radioplayer.automotive.designsystem.theme.AutomotiveTheme

/**
 * `Bottom Sheet / Extrernal Content`: header + left-aligned title/subtitle copy + a centered QR
 * code.
 *
 * The title/subtitle look centered-as-a-block in the reference screenshot only because they're
 * full-width (871dp, matching the sheet's own content width) — both text nodes' own
 * `textAlignHorizontal` in the JSON is `LEFT`, so the *text* is left-aligned, not centered; the
 * QR code image genuinely is centered (271x271dp within an 871dp-wide row, real horizontal slack
 * exists there, unlike the full-width text).
 *
 * Real QR generation is out of scope (no QR-generation library is a dependency of this module) —
 * [qrCode] is a plain slot the caller fills with a generated image, matching this repo's existing
 * precedent for brand/generated assets (e.g. `SaveTheSongDialog`'s `logo` slot). The slot is
 * sized to the Figma export's `Image / QR Code - Example` instance (272x272dp).
 *
 * @param infoTitle Maps to Figma's `Title` text node (44sp Medium — `AutomotiveTheme.typography.display2Medium`).
 * @param infoSubtitle Maps to Figma's `Subtitle` text node (32sp Regular — `AutomotiveTheme.typography.body1`).
 */
@Composable
fun BottomSheetExternalContent(
    visible: Boolean,
    onDismissRequest: () -> Unit,
    infoTitle: String,
    infoSubtitle: String,
    qrCode: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    title: String = "External Content",
) {
    CarBottomSheetScaffold(
        visible = visible,
        onDismissRequest = onDismissRequest,
        title = title,
        modifier = modifier,
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(AutomotiveTheme.measurement.spaces.extraLarge),
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(AutomotiveTheme.measurement.spaces.medium),
            ) {
                Text(
                    text = infoTitle,
                    modifier = Modifier.fillMaxWidth(),
                    style = AutomotiveTheme.typography.display2Medium,
                    color = AutomotiveTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Start,
                )
                Text(
                    text = infoSubtitle,
                    modifier = Modifier.fillMaxWidth(),
                    style = AutomotiveTheme.typography.body1,
                    color = AutomotiveTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Start,
                    maxLines = Int.MAX_VALUE,
                )
            }
            Box(modifier = Modifier.size(272.dp)) {
                qrCode()
            }
            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(CarBottomSheetScaffoldDefaults.BottomContentSpacerHeight)
            )
        }
    }
}
