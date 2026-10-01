package org.radioplayer.automotive.designsystem.components.composites.bottomsheet

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.radioplayer.automotive.designsystem.components.primitives.Text
import org.radioplayer.automotive.designsystem.theme.AutomotiveTheme

/**
 * `Bottom Sheet / Enriched Content`: header + an artwork/metadata slot + a long-form description.
 *
 * The Figma export's artwork row is a `List Item / Default` instance (the base `listitem/`
 * family's default variant) — that's a whole other composite this component doesn't own the
 * definition of, so it's exposed as a plain `@Composable` slot rather than hardcoding a
 * dependency on a specific list-item shape. See docs/bottom-sheet-audit.md.
 *
 * @param artwork The artwork/metadata row shown above [description] (e.g. a `ListItemBasic` or
 * similar, supplied by the caller).
 * @param description Long-form body copy. Figma's `Description` text node maps to
 * [org.radioplayer.automotive.designsystem.theme.AutomotiveTheme.typography]'s `body1` (32sp
 * Regular matches exactly).
 */
@Composable
fun BottomSheetEnrichedContent(
    visible: Boolean,
    onDismissRequest: () -> Unit,
    description: String,
    modifier: Modifier = Modifier,
    title: String = "Enriched Content",
    artwork: (@Composable () -> Unit)? = null,
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
            verticalArrangement = Arrangement.spacedBy(AutomotiveTheme.measurement.spaces.extraLarge),
        ) {
            artwork?.invoke()
            Text(
                text = description,
                style = AutomotiveTheme.typography.body1,
                color = AutomotiveTheme.colorScheme.onSurfaceVariant,
                maxLines = Int.MAX_VALUE,
            )
            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(CarBottomSheetScaffoldDefaults.BottomContentSpacerHeight)
            )
        }
    }
}

private const val PreviewWidthDp = 1400
private const val PreviewHeightDp = 900

@Preview(
    name = "Enriched Content",
    widthDp = PreviewWidthDp,
    heightDp = PreviewHeightDp,
    showBackground = true,
)
@Composable
private fun BottomSheetEnrichedContentPreview() {
    AutomotiveTheme {
        BottomSheetEnrichedContent(
            visible = true,
            onDismissRequest = {},
            title = "Now Playing",
            description = "This is a long-form description of the current programme. It wraps " +
                    "across multiple lines and scrolls with the rest of the sheet's content once it " +
                    "runs longer than the available height, so this preview intentionally uses " +
                    "several sentences of filler copy to exercise that scroll behaviour.",
            artwork = { PreviewArtworkPlaceholder() },
        )
    }
}

@Preview(
    name = "Enriched Content - No Artwork",
    widthDp = PreviewWidthDp,
    heightDp = PreviewHeightDp,
    showBackground = true,
)
@Composable
private fun BottomSheetEnrichedContentNoArtworkPreview() {
    AutomotiveTheme {
        BottomSheetEnrichedContent(
            visible = true,
            onDismissRequest = {},
            description = "Enriched content with no artwork slot supplied — `artwork` is " +
                    "nullable, so callers without a list-item-style row can omit it entirely.",
        )
    }
}

/**
 * Stand-in for a caller-supplied `ListItemBasic`-style artwork row (see this file's `artwork`
 * param doc) — this module doesn't own that composite's definition, so the preview here can't
 * construct a real one.
 */
@Composable
private fun PreviewArtworkPlaceholder() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(140.dp)
            .background(Color.DarkGray, RoundedCornerShape(12.dp)),
    )
}
