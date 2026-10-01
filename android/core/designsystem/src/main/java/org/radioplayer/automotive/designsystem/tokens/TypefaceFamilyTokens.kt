package org.radioplayer.automotive.designsystem.tokens

import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.text.font.FontWeight
import org.radioplayer.automotive.designsystem.R

@OptIn(ExperimentalTextApi::class)
internal object TypefaceFamilyTokens {
    private val weights = listOf(
        FontWeight.W100,
        FontWeight.W200,
        FontWeight.W300,
        FontWeight.W400,
        FontWeight.W500,
        FontWeight.W600,
        FontWeight.W700,
        FontWeight.W800,
        FontWeight.W900
    )

    val default: FontFamily = FontFamily(
        weights.flatMap { fontWeight ->
            listOf(
                Font(
                    resId = R.font.roboto_variable,
                    weight = fontWeight,
                    style = FontStyle.Normal,
                    variationSettings = FontVariation.Settings(
                        FontVariation.weight(fontWeight.weight)
                    )
                ),
                Font(
                    resId = R.font.roboto_italic_variable,
                    weight = fontWeight,
                    style = FontStyle.Italic,
                    variationSettings = FontVariation.Settings(
                        FontVariation.weight(fontWeight.weight)
                    )
                )
            )
        }
    )
}