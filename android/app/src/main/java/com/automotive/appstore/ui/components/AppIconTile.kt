package com.automotive.appstore.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import com.automotive.appstore.AppStoreApplication
import com.automotive.appstore.data.AppIconName
import org.radioplayer.automotive.designsystem.components.primitives.icon.Icon
import org.radioplayer.automotive.designsystem.components.primitives.icon.IconSource
import org.radioplayer.automotive.designsystem.theme.AutomotiveTheme

/** Tile icon size scale, matching the `SIZES` map of the web `AppIcon`. */
enum class AppIconSize(val box: Dp, val glyph: Dp, val corner: Dp) {
    /** 80dp box / 40dp glyph — catalog tiles and list rows. */
    MD(80.dp, 40.dp, 18.dp),

    /** 96dp box / 48dp glyph. */
    LG(96.dp, 48.dp, 24.dp),

    /** 128dp box / 64dp glyph — the app detail header. */
    XL(128.dp, 64.dp, 32.dp),
}

/**
 * The rounded, app-tinted square with a white glyph.
 *
 * Port of the web `AppIcon`. The corner radius is expressed in the same proportions
 * (`rounded-2xl` / `rounded-3xl` / `rounded-[2rem]`) and the glyph keeps the white
 * foreground the web version hard-codes.
 */
@Composable
fun AppIconTile(
    icon: AppIconName,
    color: Int,
    modifier: Modifier = Modifier,
    size: AppIconSize = AppIconSize.MD,
    iconUrl: String? = null,
) {
    val shape = RoundedCornerShape(size.corner)
    Box(
        modifier = modifier
            .size(size.box)
            .clip(shape)
            .background(Color(color)),
        contentAlignment = Alignment.Center,
    ) {
        // The catalogue publishes a real publisher icon per app (config.json `icon`). Show it when
        // it resolves, and keep the tinted glyph underneath as the placeholder, so a slow or failed
        // fetch degrades to the coloured tile rather than to nothing.
        if (iconUrl.isNullOrBlank()) {
            Icon(
                source = IconSource.Vector(StoreIcons.forApp(icon)),
                size = size.glyph,
                color = Color.White,
                contentDescription = null,
            )
        } else {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(iconUrl)
                    .build(),
                // Explicit loader: the default Coil singleton has no network fetcher in Coil 3, so
                // it would fail to resolve these URLs.
                imageLoader = AppStoreApplication.imageLoader,
                contentDescription = null,
                modifier = Modifier.size(size.box),
                contentScale = ContentScale.Fit,
            )
        }
    }
}

/**
 * Convenience wrapper that reads the design system's own icon scale instead of a fixed size.
 * Used where the web app sizes icons via a Tailwind class rather than the tile scale.
 */
@Composable
fun AppIconGlyph(icon: AppIconName, color: Int, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.background(Color(color), RoundedCornerShape(AutomotiveTheme.measurement.shapes.medium)),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            source = IconSource.Vector(StoreIcons.forApp(icon)),
            size = AutomotiveTheme.icon.primary,
            color = Color.White,
            contentDescription = null,
        )
    }
}
