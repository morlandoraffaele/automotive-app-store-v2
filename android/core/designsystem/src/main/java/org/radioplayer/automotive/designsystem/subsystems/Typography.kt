package org.radioplayer.automotive.designsystem.subsystems

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import org.radioplayer.automotive.designsystem.tokens.TypographyTokens

@Immutable
data class Typography(
    val huge1: TextStyle = TypographyTokens.Huge1,
    val huge1Medium: TextStyle = TypographyTokens.Huge1Medium,
    val huge2: TextStyle = TypographyTokens.Huge2,
    val huge2Medium: TextStyle = TypographyTokens.Huge2Medium,
    val huge3: TextStyle = TypographyTokens.Huge3,
    val huge3Medium: TextStyle = TypographyTokens.Huge3Medium,
    val display1: TextStyle = TypographyTokens.Display1,
    val display1Medium: TextStyle = TypographyTokens.Display1Medium,
    val display2: TextStyle = TypographyTokens.Display2,
    val display2Medium: TextStyle = TypographyTokens.Display2Medium,
    val display3: TextStyle = TypographyTokens.Display3,
    val display3Medium: TextStyle = TypographyTokens.Display3Medium,
    val body1: TextStyle = TypographyTokens.Body1,
    val body1Medium: TextStyle = TypographyTokens.Body1Medium,
    val body2: TextStyle = TypographyTokens.Body2,
    val body2Medium: TextStyle = TypographyTokens.Body2Medium,
    val body3: TextStyle = TypographyTokens.Body3,
    val body3Medium: TextStyle = TypographyTokens.Body3Medium,
    val sub1: TextStyle = TypographyTokens.Sub1,
    val sub1Medium: TextStyle = TypographyTokens.Sub1Medium,
    val sub2: TextStyle = TypographyTokens.Sub2,
    val sub2Medium: TextStyle = TypographyTokens.Sub2Medium,
    val sub3: TextStyle = TypographyTokens.Sub3,
    val sub3Medium: TextStyle = TypographyTokens.Sub3Medium,
) {

    companion object {
        fun overrideWithFontFamily(
            fontFamily: FontFamily?
        ): Typography  {
            val defaultTypography = Typography()
            return Typography().copy(
                huge1 = defaultTypography.huge1.copy(fontFamily = fontFamily),
                huge1Medium = defaultTypography.huge1Medium.copy(fontFamily = fontFamily),
                huge2 = defaultTypography.huge2.copy(fontFamily = fontFamily),
                huge2Medium = defaultTypography.huge2Medium.copy(fontFamily = fontFamily),
                huge3 = defaultTypography.huge3.copy(fontFamily = fontFamily),
                huge3Medium = defaultTypography.huge3Medium.copy(fontFamily = fontFamily),
                display1 = defaultTypography.display1.copy(fontFamily = fontFamily),
                display1Medium = defaultTypography.display1Medium.copy(fontFamily = fontFamily),
                display2 = defaultTypography.display2.copy(fontFamily = fontFamily),
                display2Medium = defaultTypography.display2Medium.copy(fontFamily = fontFamily),
                display3 = defaultTypography.display3.copy(fontFamily = fontFamily),
                display3Medium = defaultTypography.display3Medium.copy(fontFamily = fontFamily),
                body1 = defaultTypography.body1.copy(fontFamily = fontFamily),
                body1Medium = defaultTypography.body1Medium.copy(fontFamily = fontFamily),
                body2 = defaultTypography.body2.copy(fontFamily = fontFamily),
                body2Medium = defaultTypography.body2Medium.copy(fontFamily = fontFamily),
                body3 = defaultTypography.body3.copy(fontFamily = fontFamily),
                body3Medium = defaultTypography.body3Medium.copy(fontFamily = fontFamily),
                sub1 = defaultTypography.sub1.copy(fontFamily = fontFamily),
                sub1Medium = defaultTypography.sub1Medium.copy(fontFamily = fontFamily),
                sub2 = defaultTypography.sub2.copy(fontFamily = fontFamily),
                sub2Medium = defaultTypography.sub2Medium.copy(fontFamily = fontFamily),
                sub3 = defaultTypography.sub3.copy(fontFamily = fontFamily),
                sub3Medium = defaultTypography.sub3Medium.copy(fontFamily = fontFamily)
            )
        }
    }
}

internal val LocalTypography = staticCompositionLocalOf { Typography() }