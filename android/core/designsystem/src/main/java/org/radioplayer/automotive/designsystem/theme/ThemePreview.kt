package org.radioplayer.automotive.designsystem.theme

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.radioplayer.automotive.designsystem.tokens.DefaultPaletteTokens

@Preview(
    name = "Dark Mode Complete Specs",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_CAR
)
@Preview(
    name = "Light Mode Complete Specs",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_NO or Configuration.UI_MODE_TYPE_CAR
)

@Composable
fun AutomotiveThemePreview() {
    AutomotiveTheme {
        // Expanded height canvas to render the complete tokens tree down a single viewport sheet
        Box(
            modifier = Modifier
                .size(width = 1080.dp, height = 2400.dp)
                .background(AutomotiveTheme.colorScheme.surface)
        ) {
            AutomotiveDesignSystemSheet()
        }
    }
}

@Composable
private fun AutomotiveDesignSystemSheet() {
    val sysColors = AutomotiveTheme.colorScheme

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(24.dp),
        verticalArrangement = Arrangement.spacedBy(36.dp)
    ) {
        // --- PAGE HEADER ---
        item {
            Column {
                Text(
                    text = "AUTOMOTIVE SPECIFICATION SANDBOX",
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Black,
                    color = sysColors.primary,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "Single-source interactive view mapping core components alongside all functional & semantic tokens.",
                    fontSize = 13.sp,
                    color = sysColors.onSurface.copy(alpha = 0.6f)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(sysColors.outlineVariant))
            }
        }

        // --- SECTION 1: BUTTONS ---
        item {
            SandboxSectionWrapper(title = "Component Specifications: Buttons") {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(AutomotiveTheme.measurement.spaces.medium)
                ) {
                    AutomotiveButton(
                        text = "Primary Action",
                        modifier = Modifier.weight(1f),
                        backgroundColor = sysColors.primary,
                        contentColor = sysColors.onPrimary
                    )
                    AutomotiveButton(
                        text = "Surface Standard",
                        modifier = Modifier.weight(1f),
                        backgroundColor = sysColors.surfaceVariant,
                        contentColor = sysColors.onSurfaceVariant
                    )
                    AutomotiveButton(
                        text = "Secondary Block",
                        modifier = Modifier.weight(1f),
                        backgroundColor = sysColors.secondaryContainer,
                        contentColor = sysColors.onSecondaryContainer
                    )
                }
            }
        }

        // --- SECTION 2: THE COMPREHENSIVE SEMANTIC SCHEMA ---
        item {
            SandboxSectionWrapper(title = "Semantic UI Architecture System (Functional Token Maps)") {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {

                    // Group A: Core Brand Contexts
                    TokenSubGroupPanel(title = "Core Brand Framework") {
                        val brandTokens = listOf(
                            TokenData("Primary", sysColors.primary, sysColors.onPrimary, "Primary"),
                            TokenData("On Primary", sysColors.onPrimary, sysColors.primary, "OnPrimary"),
                            TokenData("Primary Container", sysColors.primaryContainer, sysColors.onPrimaryContainer, "PrimaryCont."),
                            TokenData("On Primary Container", sysColors.onPrimaryContainer, sysColors.primaryContainer, "OnPrimaryCont."),
                            TokenData("Secondary", sysColors.secondary, sysColors.onSecondary, "Secondary"),
                            TokenData("On Secondary", sysColors.onSecondary, sysColors.secondary, "OnSecondary"),
                            TokenData("Secondary Container", sysColors.secondaryContainer, sysColors.onSecondaryContainer, "Sec.Cont."),
                            TokenData("On Secondary Container", sysColors.onSecondaryContainer, sysColors.secondaryContainer, "OnSec.Cont."),
                            TokenData("Tertiary", sysColors.tertiary, sysColors.onTertiary, "Tertiary"),
                            TokenData("On Tertiary", sysColors.onTertiary, sysColors.tertiary, "OnTertiary"),
                            TokenData("Tertiary Container", sysColors.tertiaryContainer, sysColors.onTertiaryContainer, "TertCont."),
                            TokenData("On Tertiary Container", sysColors.onTertiaryContainer, sysColors.tertiaryContainer, "OnTertCont."),
                            TokenData("Inverse Primary", sysColors.inversePrimary, sysColors.primary, "InversePrimary")
                        )
                        TokenMatrixGrid(tokens = brandTokens)
                    }

                    // Group B: Surfaces & Dynamic Containers
                    TokenSubGroupPanel(title = "Surfaces, Containers & Structural Outlines") {
                        val surfaceTokens = listOf(
                            TokenData("Surface", sysColors.surface, sysColors.onSurface, "Surface"),
                            TokenData("On Surface", sysColors.onSurface, sysColors.surface, "OnSurface"),
                            TokenData("Surface Variant", sysColors.surfaceVariant, sysColors.onSurfaceVariant, "SurfaceVariant"),
                            TokenData("On Surface Variant", sysColors.onSurfaceVariant, sysColors.surfaceVariant, "OnSurfVariant"),
                            TokenData("Inverse Surface", sysColors.inverseSurface, sysColors.inverseOnSurface, "InverseSurface"),
                            TokenData("Inverse On Surface", sysColors.inverseOnSurface, sysColors.inverseSurface, "InverseOnSurf"),
                            TokenData("Surface Tint", sysColors.surfaceTint, sysColors.onPrimary, "SurfaceTint"),
                            TokenData("Surface Dim", sysColors.surfaceDim, sysColors.onSurface, "SurfaceDim"),
                            TokenData("Surface Bright", sysColors.surfaceBright, sysColors.onSurface, "SurfaceBright"),
                            TokenData("Surface Container", sysColors.surfaceContainer, sysColors.onSurface, "SurfContainer"),
                            TokenData("Surf Cont. Low", sysColors.surfaceContainerLow, sysColors.onSurface, "SurfContLow"),
                            TokenData("Surf Cont. Lowest", sysColors.surfaceContainerLowest, sysColors.onSurface, "SurfContLowest"),
                            TokenData("Surf Cont. High", sysColors.surfaceContainerHigh, sysColors.onSurface, "SurfContHigh"),
                            TokenData("Surf Cont. Highest", sysColors.surfaceContainerHighest, sysColors.onSurface, "SurfContHighest"),
                            TokenData("Surf Cont. Selected", sysColors.surfaceContainerSelected, sysColors.onSurfaceContainerSelected, "SurfContSel."),
                            TokenData("On Surf Cont. Selected", sysColors.onSurfaceContainerSelected, sysColors.surfaceContainerSelected, "OnSurfContSel."),
                            TokenData("Surf Cont. Translucent", sysColors.surfaceContainerTranslucent, sysColors.onSurface, "SurfContTrans."),
                            TokenData("Outline", sysColors.outline, sysColors.surface, "Outline"),
                            TokenData("Outline Variant", sysColors.outlineVariant, sysColors.surface, "OutlineVariant")
                        )
                        TokenMatrixGrid(tokens = surfaceTokens)
                    }

                    // Group C: Typography Elements
                    TokenSubGroupPanel(title = "Dedicated Type Content Hierarchies") {
                        val typeTokens = listOf(
                            TokenData("Text Primary", sysColors.textPrimary, sysColors.surface, "TextPrimary"),
                            TokenData("Text Secondary", sysColors.textSecondary, sysColors.surface, "TextSecondary"),
                            TokenData("Text Tertiary", sysColors.textTertiary, sysColors.surface, "TextTertiary")
                        )
                        TokenMatrixGrid(tokens = typeTokens)
                    }

                    // Group D: Feedback, Automotive Extended & Safety Channels
                    TokenSubGroupPanel(title = "System Alerts, Utilities & Extended Brand Tones") {
                        val safetyTokens = listOf(
                            TokenData("Error", sysColors.error, sysColors.onError, "Error"),
                            TokenData("On Error", sysColors.onError, sysColors.error, "OnError"),
                            TokenData("Error Container", sysColors.errorContainer, sysColors.onErrorContainer, "ErrorCont."),
                            TokenData("On Error Container", sysColors.onErrorContainer, sysColors.errorContainer, "OnErrorCont."),
                            TokenData("Blue Base", sysColors.blue, sysColors.onBlue, "Blue"),
                            TokenData("On Blue", sysColors.onBlue, sysColors.blue, "OnBlue"),
                            TokenData("Blue Container", sysColors.blueContainer, sysColors.onBlueContainer, "BlueContainer"),
                            TokenData("On Blue Container", sysColors.onBlueContainer, sysColors.blueContainer, "OnBlueCont."),
                            TokenData("Red Diagnostic", sysColors.red, sysColors.onRed, "Red"),
                            TokenData("On Red", sysColors.onRed, sysColors.red, "OnRed"),
                            TokenData("Red Container", sysColors.redContainer, sysColors.onRedContainer, "RedContainer"),
                            TokenData("On Red Container", sysColors.onRedContainer, sysColors.redContainer, "OnRedCont."),
                            TokenData("Green Confirm.", sysColors.green, sysColors.onGreen, "Green"),
                            TokenData("On Green", sysColors.onGreen, sysColors.green, "OnGreen"),
                            TokenData("Green Container", sysColors.greenContainer, sysColors.onGreenContainer, "GreenCont."),
                            TokenData("On Green Container", sysColors.onGreenContainer, sysColors.greenContainer, "OnGreenCont."),
                            TokenData("Yellow Warning", sysColors.yellow, sysColors.onYellow, "Yellow"),
                            TokenData("On Yellow", sysColors.onYellow, sysColors.yellow, "OnYellow"),
                            TokenData("Yellow Container", sysColors.yellowContainer, sysColors.onYellowContainer, "YellowCont."),
                            TokenData("On Yellow Container", sysColors.onYellowContainer, sysColors.yellowContainer, "OnYellowCont."),
                            TokenData("Accent Auto Mode", sysColors.accentAuto, sysColors.onAccentAuto, "AccentAuto"),
                            TokenData("On Accent Auto", sysColors.onAccentAuto, sysColors.accentAuto, "OnAccentAuto")
                        )
                        TokenMatrixGrid(tokens = safetyTokens)
                    }

                    // Group E: Alpha Utility Scrims & Overlays
                    TokenSubGroupPanel(title = "Atmospheric Proximity: Scrims & Opacities") {
                        val scrimTokens = listOf(
                            TokenData("Standard Scrim", sysColors.scrim, Color.White, "Scrim"),
                            TokenData("AAOS Scrim High", sysColors.aaosScrimHigh, Color.White, "AaosScrimHigh"),
                            TokenData("AAOS Scrim Medium", sysColors.aaosScrimMedium, Color.White, "AaosScrimMedium"),
                            TokenData("AAOS Scrim Low", sysColors.aaosScrimLow, Color.White, "AaosScrimLow")
                        )
                        TokenMatrixGrid(tokens = scrimTokens)
                    }
                }
            }
        }

        // --- SECTION 3: CORE TONAL STRUCTURAL PALETTES ---
        item {
            val pStrips = with(DefaultPaletteTokens.PrimaryColors) { listOf(Primary0, Primary10, Primary20, Primary30, Primary40, Primary50, Primary60, Primary70, Primary80, Primary90, Primary95, Primary99, Primary100) }
            val sStrips = with(DefaultPaletteTokens.SecondaryColors) { listOf(Secondary0, Secondary10, Secondary20, Secondary30, Secondary40, Secondary50, Secondary60, Secondary70, Secondary80, Secondary90, Secondary95, Secondary99, Secondary100) }
            val tStrips = with(DefaultPaletteTokens.TertiaryColors) { listOf(Tertiary0, Tertiary10, Tertiary20, Tertiary30, Tertiary40, Tertiary50, Tertiary60, Tertiary70, Tertiary80, Tertiary90, Tertiary95, Tertiary99, Tertiary100) }
            val nStrips = with(DefaultPaletteTokens.NeutralColors) { listOf(Neutral0, Neutral10, Neutral20, Neutral30, Neutral40, Neutral50, Neutral60, Neutral70, Neutral80, Neutral90, Neutral95, Neutral99, Neutral100) }
            val nvStrips = with(DefaultPaletteTokens.NeutralColors) { listOf(NeutralVariant0, NeutralVariant10, NeutralVariant20, NeutralVariant30, NeutralVariant40, NeutralVariant50, NeutralVariant60, NeutralVariant70, NeutralVariant80, NeutralVariant90, NeutralVariant95, NeutralVariant99, NeutralVariant100) }
            val eStrips = with(DefaultPaletteTokens.ErrorColors) { listOf(Error0, Error10, Error20, Error30, Error40, Error50, Error60, Error70, Error80, Error90, Error95, Error99, Error100) }

            SandboxSectionWrapper(title = "Core Tonal Hardware Palettes (Hardware Spectrum 0 - 100)") {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    ColorTonalStrip(title = "Primary Matrix Key", colors = pStrips)
                    ColorTonalStrip(title = "Secondary Complementary", colors = sStrips)
                    ColorTonalStrip(title = "Tertiary Expressive Accent", colors = tStrips)
                    ColorTonalStrip(title = "Neutral UI Chassis Core", colors = nStrips)
                    ColorTonalStrip(title = "Neutral Variant Framing Elements", colors = nvStrips)
                    ColorTonalStrip(title = "Error Spectrum Block", colors = eStrips)
                }
            }
        }

        // --- SECTION 4: HARDWARE EXTENDED & AAOS NATIVE UTILITIES ---
        item {
            val rStrips = with(DefaultPaletteTokens.RedColors) { listOf(Red0, Red10, Red20, Red30, Red40, Red50, Red60, Red70, Red80, Red90, Red95, Red99, Red100) }
            val gStrips = with(DefaultPaletteTokens.GreenColors) { listOf(Green0, Green10, Green20, Green30, Green40, Green50, Green60, Green70, Green80, Green90, Green95, Green99, Green100) }
            val yStrips = with(DefaultPaletteTokens.YellowColors) { listOf(Yellow0, Yellow10, Yellow20, Yellow30, Yellow40, Yellow50, Yellow60, Yellow70, Yellow80, Yellow90, Yellow95, Yellow99, Yellow100) }
            val bStrips = with(DefaultPaletteTokens.BlueColors) { listOf(Blue0, Blue10, Blue20, Blue30, Blue40, Blue50, Blue60, Blue70, Blue80, Blue90, Blue95, Blue99, Blue100) }
            val aaosNeutrals = with(DefaultPaletteTokens.AaosColors) { listOf(AaosNeutral000, AaosNeutral100, AaosNeutral200, AaosNeutral300, AaosNeutral400, AaosNeutral500, AaosNeutral600, AaosNeutral700, AaosNeutral800, AaosNeutral846, AaosNeutral868, AaosNeutral900, AaosNeutral928, AaosNeutral958, AaosNeutral1000) }

            SandboxSectionWrapper(title = "Extended Native Automotive & Safety Tokens") {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(horizontalArrangement = Arrangement.spacedBy(16.dp), modifier = Modifier.padding(bottom = 2.dp)) {
                        Box(modifier = Modifier.weight(1f).height(36.dp).background(DefaultPaletteTokens.White, RoundedCornerShape(6.dp)).padding(start = 12.dp), contentAlignment = Alignment.CenterStart) {
                            Text("Absolute Pure White", color = Color.Black, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                        Box(modifier = Modifier.weight(1f).height(36.dp).background(DefaultPaletteTokens.Black, RoundedCornerShape(6.dp)).border(1.dp, sysColors.outlineVariant, RoundedCornerShape(6.dp)).padding(start = 12.dp), contentAlignment = Alignment.CenterStart) {
                            Text("Absolute Pure Black", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                    ColorTonalStrip(title = "Red Performance Systems", colors = rStrips)
                    ColorTonalStrip(title = "Green Operations Verified", colors = gStrips)
                    ColorTonalStrip(title = "Yellow Telemetry Cautions", colors = yStrips)
                    ColorTonalStrip(title = "Blue Network Inbound Links", colors = bStrips)
                    ColorTonalStrip(title = "AAOS Infotainment Base Architecture (000 - 1000 Grid)", colors = aaosNeutrals)
                }
            }
        }

        // --- SECTION 5: LIVE MOCKUP GALLERY CONTEXT ---
        item {
            SandboxSectionWrapper(title = "Dynamic Assembly Verification Panel") {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(260.dp)
                ) {
                    ThemeComponentGallery()
                }
            }
        }
    }
}

// --- STRUCTURAL LAYOUT COMPOSABLES ---

@Composable
private fun SandboxSectionWrapper(title: String, content: @Composable () -> Unit) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = title,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = AutomotiveTheme.colorScheme.onSurface,
            modifier = Modifier.padding(bottom = 14.dp)
        )
        content()
    }
}

@Composable
private fun TokenSubGroupPanel(title: String, content: @Composable () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(AutomotiveTheme.colorScheme.surfaceContainerLow, RoundedCornerShape(8.dp))
            .padding(14.dp)
    ) {
        Text(
            text = title.uppercase(),
            fontSize = 11.sp,
            fontWeight = FontWeight.Black,
            color = AutomotiveTheme.colorScheme.primary,
            letterSpacing = 0.5.sp,
            modifier = Modifier.padding(bottom = 10.dp)
        )
        content()
    }
}

@Composable
private fun TokenMatrixGrid(tokens: List<TokenData>) {
    // 3-Column fluid Grid using chunking strategy safely within LazyColumn item bounds
    val columns = 3
    val rows = tokens.chunked(columns)

    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        rows.forEach { rowElements ->
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.fillMaxWidth()) {
                rowElements.forEach { token ->
                    Row(
                        modifier = Modifier
                            .weight(1f)
                            .height(46.dp)
                            .background(AutomotiveTheme.colorScheme.surfaceContainerHigh, RoundedCornerShape(6.dp))
                            .border(0.5.dp, AutomotiveTheme.colorScheme.outlineVariant.copy(alpha = 0.4f), RoundedCornerShape(6.dp))
                            .padding(horizontal = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = token.displayName, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = AutomotiveTheme.colorScheme.onSurface, maxLines = 1)
                            Text(text = token.technicalTokenName, fontSize = 9.sp, color = AutomotiveTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f), maxLines = 1)
                        }
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(token.colorValue)
                                .border(1.dp, AutomotiveTheme.colorScheme.outline.copy(alpha = 0.2f), RoundedCornerShape(4.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            // Check if color is highly transparent to place visibility anchor dot if needed
                            if (token.colorValue.alpha < 0.9f) {
                                Box(modifier = Modifier.size(4.dp).background(AutomotiveTheme.colorScheme.onSurface, RoundedCornerShape(2.dp)))
                            }
                        }
                    }
                }
                // Fill out trailing empty columns to keep matrix alignment intact
                if (rowElements.size < columns) {
                    repeat(columns - rowElements.size) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

@Composable
private fun ColorTonalStrip(title: String, colors: List<Color>) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = title.uppercase(),
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = AutomotiveTheme.colorScheme.onSurfaceVariant,
            letterSpacing = 0.5.sp
        )
        Spacer(modifier = Modifier.height(4.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(26.dp)
                .clip(RoundedCornerShape(6.dp))
        ) {
            colors.forEach { shade ->
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .background(shade)
                )
            }
        }
    }
}

private data class TokenData(
    val displayName: String,
    val colorValue: Color,
    val inverseFallbackColor: Color,
    val technicalTokenName: String
)

// --- BASELINE ENGINE SAMPLES ---

@Composable
private fun ThemeComponentGallery() {
    Row(
        modifier = Modifier.fillMaxSize(),
        horizontalArrangement = Arrangement.spacedBy(AutomotiveTheme.measurement.spaces.medium)
    ) {
        Column(
            modifier = Modifier
                .weight(1.6f)
                .fillMaxHeight()
                .background(AutomotiveTheme.colorScheme.surfaceVariant, RoundedCornerShape(AutomotiveTheme.measurement.shapes.medium))
                .padding(AutomotiveTheme.measurement.spaces.medium),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(text = "NOW PLAYING", fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.5.sp, color = AutomotiveTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f))
                Spacer(modifier = Modifier.height(2.dp))
                Text(text = "Radio Player One", style = AutomotiveTheme.typography.body3, color = AutomotiveTheme.colorScheme.onSurfaceVariant)
                Text(text = "98.5 FM • Live Broadcast", style = AutomotiveTheme.typography.sub1, color = AutomotiveTheme.colorScheme.primary)
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(AutomotiveTheme.measurement.spaces.small)
            ) {
                AutomotiveButton(text = "◀◀", modifier = Modifier.weight(1f), backgroundColor = AutomotiveTheme.colorScheme.surface, contentColor = AutomotiveTheme.colorScheme.onSurface)
                AutomotiveButton(text = "▶", modifier = Modifier.weight(1.4f), backgroundColor = AutomotiveTheme.colorScheme.primary, contentColor = AutomotiveTheme.colorScheme.onPrimary)
                AutomotiveButton(text = "▶▶", modifier = Modifier.weight(1f), backgroundColor = AutomotiveTheme.colorScheme.surface, contentColor = AutomotiveTheme.colorScheme.onSurface)
            }
        }

        Column(
            modifier = Modifier.weight(1f).fillMaxHeight(),
            verticalArrangement = Arrangement.spacedBy(AutomotiveTheme.measurement.spaces.medium)
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .background(AutomotiveTheme.colorScheme.secondaryContainer, RoundedCornerShape(AutomotiveTheme.measurement.shapes.medium))
                    .padding(AutomotiveTheme.measurement.spaces.medium),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "🗺️ Navigation Widget", color = AutomotiveTheme.colorScheme.onSecondaryContainer, fontWeight = FontWeight.SemiBold)
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .background(AutomotiveTheme.colorScheme.surfaceVariant, RoundedCornerShape(AutomotiveTheme.measurement.shapes.small))
                    .padding(horizontal = AutomotiveTheme.measurement.spaces.medium),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "❄️ A/C ON", color = AutomotiveTheme.colorScheme.onSurfaceVariant, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Text(text = "22.0°C", fontSize = 20.sp, fontWeight = FontWeight.Black, color = AutomotiveTheme.colorScheme.onSurfaceVariant)
                Text(text = "♨️ Rear", color = AutomotiveTheme.colorScheme.error, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }
        }
    }
}

@Composable
private fun AutomotiveButton(
    text: String,
    modifier: Modifier = Modifier,
    backgroundColor: Color,
    contentColor: Color
) {
    Box(
        modifier = modifier
            .height(56.dp)
            .clip(RoundedCornerShape(AutomotiveTheme.measurement.shapes.small))
            .background(backgroundColor)
            .clickable { },
        contentAlignment = Alignment.Center
    ) {
        Text(text = text, color = contentColor, fontSize = 16.sp, fontWeight = FontWeight.Bold)
    }
}