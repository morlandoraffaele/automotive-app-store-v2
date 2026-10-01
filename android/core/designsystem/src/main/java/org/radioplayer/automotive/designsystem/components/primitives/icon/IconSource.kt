package org.radioplayer.automotive.designsystem.components.primitives.icon

import androidx.annotation.DrawableRes
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector

sealed class IconSource {
    data class Resource(@param:DrawableRes val resId: Int) : IconSource()
    data class Vector(val imageVector: ImageVector) : IconSource()
    data class Bitmap(val bitmap: ImageBitmap) : IconSource()
}
