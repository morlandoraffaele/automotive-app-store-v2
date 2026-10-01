package org.radioplayer.automotive.designsystem.components.primitives.icon

import android.content.res.Configuration
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.LocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.takeOrElse
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import org.radioplayer.automotive.designsystem.theme.AutomotiveTheme
import androidx.compose.material3.Icon as MaterialIcon

@Composable
fun Icon(
    source: IconSource,
    size: Dp = AutomotiveTheme.icon.primary,
    contentDescription: String? = null,
    color: Color = LocalContentColor.current,
) {
    // TODO: Verify whether to use defaultMinSize or a fixed size, based on the latest design specifications.
    when (source) {
        is IconSource.Resource -> MaterialIcon(
            modifier = Modifier.defaultMinSize(size, size),
            painter = painterResource(source.resId),
            contentDescription = contentDescription,
            tint = color.takeOrElse { LocalContentColor.current }
        )

        is IconSource.Vector -> MaterialIcon(
            modifier = Modifier.defaultMinSize(size, size),
            imageVector = source.imageVector,
            contentDescription = contentDescription,
            tint = color
        )

        is IconSource.Bitmap -> MaterialIcon(
            modifier = Modifier.defaultMinSize(size, size),
            bitmap = source.bitmap,
            contentDescription = contentDescription,
            tint = color
        )
    }
}

@Composable
fun Icon(
    name: IconSetEnum = IconSetEnum.Droid,
    size: Dp = AutomotiveTheme.icon.tertiary,
    contentDescription: String? = null,
    color: Color = LocalContentColor.current,
) {
    // TODO: Verify whether to use defaultMinSize or a fixed size, based on the latest design specifications.
    MaterialIcon(
        modifier = Modifier.size(size, size),
        painter = painterResource(name.resId),
        contentDescription = contentDescription,
        tint = color.takeOrElse { LocalContentColor.current }
    )
}

@Composable
fun Icon(
    resourceId: Int,
    size: Dp = AutomotiveTheme.icon.tertiary,
    contentDescription: String? = null,
    color: Color = LocalContentColor.current,
) {
    // TODO: Verify whether to use defaultMinSize or a fixed size, based on the latest design specifications.
    MaterialIcon(
        modifier = Modifier.size(size, size),
        painter = painterResource(resourceId),
        contentDescription = contentDescription,
        tint = color.takeOrElse { LocalContentColor.current }
    )
}


@Preview(
    name = "Icon - Dark Mode",
    showBackground = false,
    uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_CAR
)
@Preview(
    name = "Icon - Light Mode",
    showBackground = false,
    uiMode = Configuration.UI_MODE_NIGHT_NO or Configuration.UI_MODE_TYPE_CAR
)

@Composable
fun IconPreview() {
    AutomotiveTheme {
        Icon(
            name = IconSetEnum.Droid,
            size = AutomotiveTheme.icon.tertiary
        )
    }
}