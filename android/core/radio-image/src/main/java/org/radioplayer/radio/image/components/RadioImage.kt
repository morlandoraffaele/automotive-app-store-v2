package org.radioplayer.radio.image.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import coil3.compose.AsyncImage
import coil3.request.ImageRequest as CoilImageRequest
import coil3.request.crossfade
import org.radioplayer.radio.image.models.ImageRequest
import org.radioplayer.radio.image.models.ImageSource

/**
 * Displays the image described by [request].
 *
 * Signature-compatible stand-in for radioplayer's `RadioImage`: the same parameter names,
 * order and defaults, so `:core:designsystem` compiles without modification. Upstream
 * resolves the request through a Hilt entry point into a Coil-backed pipeline; here the
 * mapping to a Coil request happens inline.
 *
 * [ImageSource.Asset] has no Coil loader in this build and renders as empty space.
 */
@Composable
fun RadioImage(
    request: ImageRequest,
    modifier: Modifier = Modifier,
    colorFilter: ColorFilter? = null,
    contentDescription: String? = null,
    contentScale: ContentScale = ContentScale.Crop,
) {
    val model = when (val source = request.source) {
        is ImageSource.Resource -> source.id
        is ImageSource.Remote -> source.url
        is ImageSource.Asset -> null
    }

    if (model == null) return

    AsyncImage(
        model = CoilImageRequest.Builder(androidx.compose.ui.platform.LocalContext.current)
            .data(model)
            .crossfade(true)
            .build(),
        contentDescription = contentDescription,
        modifier = modifier,
        colorFilter = colorFilter,
        contentScale = contentScale,
    )
}
