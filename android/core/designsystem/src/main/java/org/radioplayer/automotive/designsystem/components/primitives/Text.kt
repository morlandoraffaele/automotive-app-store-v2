package org.radioplayer.automotive.designsystem.components.primitives

import android.util.Log
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.fromHtml
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import org.radioplayer.automotive.designsystem.model.DrivingUxRestrictions
import org.radioplayer.automotive.designsystem.model.LocalDrivingUxRestrictions

private const val MaxLines = 3
private val UNDERLINE_REGEX = Regex("__(.+?)__")
private val BOLD_REGEX = Regex("\\*(.+?)\\*")
private val ITALIC_REGEX = Regex("_(.+?)_")
private val STRIKETHROUGH_REGEX = Regex("~(.+?)~")


/**
 * A custom automotive text component that formats markdown-like syntax inline
 * and automatically adapts to driving UX restrictions.
 *
 * Supported inline styles:
 * - `*text*` -> **Bold**
 * - `_text_` -> *Italic*
 * - `__text__` -> <u>Underline</u>
 * - `~text~` -> ~Strikethrough~
 *
 * @param text The raw string content containing optional inline formatting syntax.
 * @param modifier Modifier to be applied to the layout node.
 * @param style Typography style configuration. Defaults to [LocalTextStyle].
 * @param color Text color. Defaults to [LocalContentColor].
 * @param boldStyle Custom [SpanStyle] to override default bold styling.
 * @param maxLines Maximum number of visible lines when UX is unrestricted. Defaults to 3.
 * @param overflow How visual overflow should be handled. Defaults to [TextOverflow.Ellipsis].
 * @param drivingUxRestrictions Driving restrictions overrides. Defaults to [LocalDrivingUxRestrictions].
 */
@Composable
fun Text(
    text: String,
    modifier: Modifier = Modifier,
    style: TextStyle = LocalTextStyle.current,
    color: Color = LocalContentColor.current,
    boldStyle: SpanStyle = SpanStyle(fontWeight = FontWeight.Bold),
    maxLines: Int = MaxLines,
    overflow: TextOverflow = TextOverflow.Ellipsis,
    textAlign: TextAlign? = null,
    drivingUxRestrictions: DrivingUxRestrictions? = null,
) {
    val resolvedRestrictions = drivingUxRestrictions ?: LocalDrivingUxRestrictions.current
    val isLineRestricted = resolvedRestrictions.isRestricted || resolvedRestrictions.limitStringLength

    val restrictedText = applyTextRestriction(text, resolvedRestrictions, isLineRestricted)
    val effectiveMaxLines = if (isLineRestricted) 1 else maxLines

    Text(
        text = buildEnrichedText(restrictedText),
        modifier = modifier,
        style = style,
        color = color,
        maxLines = effectiveMaxLines,
        overflow = overflow,
        textAlign = textAlign,
    )
}

private fun applyTextRestriction(
    text: String,
    drivingUxRestrictions: DrivingUxRestrictions?,
    isLineRestricted: Boolean = drivingUxRestrictions?.isRestricted == true ||
            drivingUxRestrictions?.limitStringLength == true,
): String {
    val maxLength = drivingUxRestrictions?.maxStringLength
    return if (isLineRestricted && maxLength != null) {
        text.take(maxLength)
    } else {
        text
    }
}

@Composable
private fun buildEnrichedText(
    text: String,
    boldStyle: SpanStyle = SpanStyle(fontWeight = FontWeight.Bold),
    italicStyle: SpanStyle = SpanStyle(fontStyle = FontStyle.Italic),
    strikethroughStyle: SpanStyle = SpanStyle(textDecoration = TextDecoration.LineThrough),
    underlineStyle: SpanStyle = SpanStyle(textDecoration = TextDecoration.Underline)
): AnnotatedString {

    val processedHtml = text
        .replace(UNDERLINE_REGEX, "<u>$1</u>")
        .replace(BOLD_REGEX, "<b>$1</b>")
        .replace(ITALIC_REGEX, "<i>$1</i>")
        .replace(STRIKETHROUGH_REGEX, "<s>$1</s>")

    val annotated = AnnotatedString.fromHtml(processedHtml)

    return buildAnnotatedString {
        append(annotated)
        annotated.spanStyles.forEach { range ->
            when {
                range.item.fontWeight == FontWeight.Bold ->
                    addStyle(boldStyle, range.start, range.end)

                range.item.fontStyle == FontStyle.Italic ->
                    addStyle(italicStyle, range.start, range.end)

                range.item.textDecoration == TextDecoration.LineThrough ->
                    addStyle(strikethroughStyle, range.start, range.end)

                range.item.textDecoration == TextDecoration.Underline ->
                    addStyle(underlineStyle, range.start, range.end)
            }
        }
    }
}
