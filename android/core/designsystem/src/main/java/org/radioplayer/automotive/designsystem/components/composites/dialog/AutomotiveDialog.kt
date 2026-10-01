package org.radioplayer.automotive.designsystem.components.composites.dialog

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import org.radioplayer.automotive.designsystem.components.composites.button.text.ButtonText
import org.radioplayer.automotive.designsystem.components.composites.button.text.ButtonTextShape
import org.radioplayer.automotive.designsystem.components.primitives.Text
import org.radioplayer.automotive.designsystem.components.primitives.icon.Icon
import org.radioplayer.automotive.designsystem.components.primitives.icon.IconSetEnum
import org.radioplayer.automotive.designsystem.theme.AutomotiveTheme
import org.radioplayer.automotive.designsystem.utils.customShadow

// -----------------------------------------------------------------------------------------
// Shared shell (private — the 3 public variants below only differ in header/title handling)
// -----------------------------------------------------------------------------------------

@Composable
private fun AutomotiveDialogShell(
    onDismissRequest: () -> Unit,
    modifier: Modifier,
    width: Dp,
    shape: Shape,
    colors: AutomotiveDialogColors,
    paddingTop: Dp,
    paddingBottom: Dp,
    body: @Composable ColumnScope.() -> Unit,
) {
    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = modifier
                .width(width)
                .customShadow(color = colors.shadow, shape = shape),
            shape = shape,
            color = colors.container,
            contentColor = colors.content
        ) {
            Column(
                modifier = Modifier.padding(
                    top = paddingTop,
                    bottom = paddingBottom,
                    start = AutomotiveTheme.measurement.spaces.largeIncreased,
                    end = AutomotiveTheme.measurement.spaces.largeIncreased
                ),
                verticalArrangement = Arrangement.spacedBy(AutomotiveTheme.measurement.spaces.medium),
                content = body
            )
        }
    }
}

// -----------------------------------------------------------------------------------------
// Type=Dialog without title
// -----------------------------------------------------------------------------------------

/**
 * @param content body copy (Figma: `maxLines=3`, truncates with an ellipsis — the Figma
 * placeholder text itself calls out that this obeys the driving-restricted 1-line/120-char cap,
 * which is exactly what [org.radioplayer.automotive.designsystem.components.primitives.Text]
 * already does via [org.radioplayer.automotive.designsystem.model.DrivingUxRestrictions] — no
 * extra wiring needed here, just using that shared primitive instead of a raw Material3 `Text`).
 * @param actions the "Card action area" row — a plain slot rather than a hardcoded pair of
 * buttons: the export's own two button instances are both schema-default placeholders (generic
 * "Label" text, `Show Icon`/`Icon` flagged `isDefaultPlaceholder`), so the real number of
 * actions/icon usage per real dialog isn't confirmed data — pass 1-N [ButtonText] (or other
 * button family) calls.
 */
@Composable
fun AutomotiveDialog(
    onDismissRequest: () -> Unit,
    content: String,
    actions: @Composable RowScope.() -> Unit,
    modifier: Modifier = Modifier,
    width: Dp = AutomotiveTheme.measurement.sizes.dialogToastWidth,
    shape: Shape = RoundedCornerShape(AutomotiveTheme.measurement.shapes.largeIncreased),
    colors: AutomotiveDialogColors = AutomotiveDialogDefaults.colors(),
) {
    AutomotiveDialogShell(
        onDismissRequest = onDismissRequest,
        modifier = modifier,
        width = width,
        shape = shape,
        colors = colors,
        paddingTop = AutomotiveTheme.measurement.spaces.largeIncreased,
        paddingBottom = AutomotiveTheme.measurement.spaces.medium,
    ) {
        Text(
            text = content,
            style = AutomotiveTheme.typography.body3,
            color = colors.content,
            maxLines = 3,
        )
        Row(
            horizontalArrangement = Arrangement.spacedBy(AutomotiveTheme.measurement.spaces.largeIncreased),
            content = actions,
        )
    }
}

// -----------------------------------------------------------------------------------------
// Type=Dialog with title (left-aligned)
// -----------------------------------------------------------------------------------------

/** @see AutomotiveDialog for [content]/[actions] docs — identical here, plus a left-aligned [title]. */
@Composable
fun AutomotiveDialogWithTitle(
    onDismissRequest: () -> Unit,
    title: String,
    content: String,
    actions: @Composable RowScope.() -> Unit,
    modifier: Modifier = Modifier,
    width: Dp = AutomotiveTheme.measurement.sizes.dialogToastWidth,
    shape: Shape = RoundedCornerShape(AutomotiveTheme.measurement.shapes.largeIncreased),
    colors: AutomotiveDialogColors = AutomotiveDialogDefaults.colors(),
) {
    AutomotiveDialogShell(
        onDismissRequest = onDismissRequest,
        modifier = modifier,
        width = width,
        shape = shape,
        colors = colors,
        // Figma: top padding is 16dp here vs 32dp on the other 2 variants — the title's own
        // presence right at the top edge is the intended breathing room, not a rounding error.
        paddingTop = AutomotiveTheme.measurement.spaces.medium,
        paddingBottom = AutomotiveTheme.measurement.spaces.medium,
    ) {
        Text(
            text = title,
            style = AutomotiveTheme.typography.body1Medium,
            color = colors.title,
            maxLines = 2,
        )
        Text(
            text = content,
            style = AutomotiveTheme.typography.body3,
            color = colors.content,
            maxLines = 3,
        )
        Row(
            horizontalArrangement = Arrangement.spacedBy(AutomotiveTheme.measurement.spaces.largeIncreased),
            content = actions,
        )
    }
}

// -----------------------------------------------------------------------------------------
// Type=Dialog with centered title
// -----------------------------------------------------------------------------------------

/**
 * @param icon optional glyph centered above [title] (Figma: `44dp`, `Size=aaos - primary` →
 * [AutomotiveTheme.icon.primary]). The export's own icon resolves to Material Symbols "adb"
 * (the Android robot) flagged `isDefaultPlaceholder: true` — a generic swap-icon default, not a
 * real intended glyph — so this is a nullable caller-supplied slot, same pattern as
 * `SaveTheSongDialog`'s own `icon` param.
 * @see AutomotiveDialogWithTitle for [content]/[actions] docs.
 */
@Composable
fun AutomotiveDialogWithCenteredTitle(
    onDismissRequest: () -> Unit,
    title: String,
    content: String,
    actions: @Composable RowScope.() -> Unit,
    modifier: Modifier = Modifier,
    icon: (@Composable () -> Unit)? = null,
    width: Dp = AutomotiveTheme.measurement.sizes.dialogToastWidth,
    shape: Shape = RoundedCornerShape(AutomotiveTheme.measurement.shapes.largeIncreased),
    colors: AutomotiveDialogColors = AutomotiveDialogDefaults.colors(),
) {
    AutomotiveDialogShell(
        onDismissRequest = onDismissRequest,
        modifier = modifier,
        width = width,
        shape = shape,
        colors = colors,
        paddingTop = AutomotiveTheme.measurement.spaces.largeIncreased,
        paddingBottom = AutomotiveTheme.measurement.spaces.medium,
    ) {
        // Figma: icon+title live in a "Top Container" together with the content text, with a
        // 32dp gap between the title block and the content — wider than the 16dp gap this
        // shell's own `verticalArrangement` uses between top-level siblings (header/content vs.
        // actions), so it's reproduced as its own nested Column rather than reusing the shell's
        // spacing for everything.
        Column(verticalArrangement = Arrangement.spacedBy(AutomotiveTheme.measurement.spaces.largeIncreased)) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(AutomotiveTheme.measurement.spaces.medium),
            ) {
                if (icon != null) {
                    Box(modifier = Modifier.size(AutomotiveTheme.icon.primary)) {
                        icon()
                    }
                }
                Text(
                    text = title,
                    modifier = Modifier.fillMaxWidth(),
                    style = AutomotiveTheme.typography.body1Medium,
                    color = colors.title,
                    maxLines = 2,
                    textAlign = TextAlign.Center,
                )
            }
            Text(
                text = content,
                style = AutomotiveTheme.typography.body3,
                color = colors.content,
                maxLines = 3,
            )
        }
        Row(
            horizontalArrangement = Arrangement.spacedBy(AutomotiveTheme.measurement.spaces.largeIncreased),
            content = actions,
        )
    }
}

// -----------------------------------------------------------------------------------------
// Previews
// -----------------------------------------------------------------------------------------

/** Both action buttons below use `shape = largeIncreased` (20dp corners) to match the export's
 *  real `Type=Square` choice on the Button instances — `isDefaultPlaceholder: false` for that
 *  specific property, i.e. a deliberate pick, not the Button family's own default (`Round`,
 *  fully-pill). See `AUTOMOTIVE_DIALOG.md` §1. */
@Composable
private fun PreviewActions() {
    ButtonText(
        onClick = {},
        label = "Cancel",
        shapeVariant = ButtonTextShape.Square,
    )
    ButtonText(
        onClick = {},
        label = "Confirm",
        shapeVariant = ButtonTextShape.Square,
    )
}

@Preview(name = "Without title", widthDp = 900, heightDp = 400, showBackground = true, backgroundColor = 0xFF1D2022)
@Composable
private fun AutomotiveDialogPreview() {
    AutomotiveTheme {
        AutomotiveDialog(
            onDismissRequest = {},
            content = "Content spans multiple lines when parked, but truncates to 1 line while driving for safety.",
            actions = { PreviewActions() },
        )
    }
}

@Preview(name = "With title", widthDp = 900, heightDp = 400, showBackground = true, backgroundColor = 0xFF1D2022)
@Composable
private fun AutomotiveDialogWithTitlePreview() {
    AutomotiveTheme {
        AutomotiveDialogWithTitle(
            onDismissRequest = {},
            title = "Title",
            content = "Content spans multiple lines when parked, but truncates to 1 line while driving for safety.",
            actions = { PreviewActions() },
        )
    }
}

@Preview(name = "With centered title", widthDp = 900, heightDp = 500, showBackground = true, backgroundColor = 0xFF1D2022)
@Composable
private fun AutomotiveDialogWithCenteredTitlePreview() {
    AutomotiveTheme {
        AutomotiveDialogWithCenteredTitle(
            onDismissRequest = {},
            title = "Title",
            content = "Content spans multiple lines when parked, but truncates to 1 line while driving for safety.",
            icon = { Icon(name = IconSetEnum.Droid, size = AutomotiveTheme.icon.primary) },
            actions = { PreviewActions() },
        )
    }
}
