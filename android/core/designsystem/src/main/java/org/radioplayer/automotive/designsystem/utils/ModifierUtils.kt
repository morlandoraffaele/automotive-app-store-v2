package org.radioplayer.automotive.designsystem.utils

import android.graphics.BlurMaskFilter
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawOutline
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

fun Modifier.customShadow(
    color: Color,
    offsetX: Dp = 0.dp,
    offsetY: Dp = 4.dp,
    blur: Dp = 8.dp,
    spread: Dp = 0.dp,
    shape: Shape = RectangleShape
) = this.drawBehind {
    val shadowSize = size.copy(
        width = size.width + spread.toPx() * 2,
        height = size.height + spread.toPx() * 2
    )
    val outline = shape.createOutline(shadowSize, layoutDirection, this)

    drawIntoCanvas { canvas ->
        val paint = Paint().apply {
            this.color = color
        }
        val frameworkPaint = paint.asFrameworkPaint()
        if (blur.toPx() > 0) {
            frameworkPaint.maskFilter = BlurMaskFilter(blur.toPx(), BlurMaskFilter.Blur.NORMAL)
        }

        canvas.save()
        canvas.translate(
            offsetX.toPx() - spread.toPx(),
            offsetY.toPx() - spread.toPx()
        )
        canvas.drawOutline(outline, paint)
        canvas.restore()
    }
}