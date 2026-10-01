package com.automotive.appstore.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.CloudQueue
import androidx.compose.material.icons.filled.ConfirmationNumber
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.DriveEta
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Keyboard
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.LocalParking
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Message
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.NightlightRound
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Podcasts
import androidx.compose.material.icons.filled.Radio
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.RotateRight
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SearchOff
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Smartphone
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.ui.graphics.vector.ImageVector
import com.automotive.appstore.data.AppIconName
import com.automotive.appstore.data.CategoryId
import com.automotive.appstore.data.PermissionId

/**
 * Icon set for the store.
 *
 * The web app uses `lucide-react`; on Android the closest equivalent built-in set is
 * Material Icons, so each Lucide glyph maps to its nearest Material counterpart. All of
 * them render through the design system's `Icon` primitive, which is what applies the
 * automotive icon-size scale.
 */
object StoreIcons {
    val Store: ImageVector = Icons.Filled.GridView
    val Installed: ImageVector = Icons.Filled.Inventory2
    val Settings: ImageVector = Icons.Filled.Settings
    val Sun: ImageVector = Icons.Filled.LightMode
    val Moon: ImageVector = Icons.Filled.NightlightRound
    val DayMode: ImageVector = Icons.Filled.WbSunny
    val Back: ImageVector = Icons.Filled.ArrowBack
    val UpdatesAvailable: ImageVector = Icons.Filled.ArrowDownward
    val AllUpToDate: ImageVector = Icons.Filled.CheckCircle
    val Updating: ImageVector = Icons.Filled.Refresh
    val UpdateAll: ImageVector = Icons.Filled.ArrowDownward
    val Wifi: ImageVector = Icons.Filled.Wifi
    val Car: ImageVector = Icons.Filled.DirectionsCar
    val Dismiss: ImageVector = Icons.Filled.Close
    val VoiceSearch: ImageVector = Icons.Filled.Mic
    val TypeSearch: ImageVector = Icons.Filled.Keyboard
    val SearchIcon: ImageVector = Icons.Filled.Search
    val ClearSearch: ImageVector = Icons.Filled.Close
    val Check: ImageVector = Icons.Filled.Check
    val Update: ImageVector = Icons.Filled.ArrowDownward
    val Installing: ImageVector = Icons.Filled.Refresh
    val Failed: ImageVector = Icons.Filled.Error
    val Download: ImageVector = Icons.Filled.ArrowDownward
    val Open: ImageVector = Icons.Filled.OpenInNew
    val Cancel: ImageVector = Icons.Filled.Close
    val Retry: ImageVector = Icons.Filled.RotateRight
    val Offline: ImageVector = Icons.Filled.CloudOff
    val NoApps: ImageVector = Icons.Filled.Inventory2
    val NoMatch: ImageVector = Icons.Filled.SearchOff
    val NotFound: ImageVector = Icons.Filled.Inventory2
    val Layers: ImageVector = Icons.Filled.Layers
    val Locked: ImageVector = Icons.Filled.Lock
    val WarningAmber: ImageVector = Icons.Filled.Warning
    val CheckForUpdate: ImageVector = Icons.Filled.Refresh
    val Translate: ImageVector = Icons.Filled.Translate
    val Drive: ImageVector = Icons.Filled.DriveEta
    val More: ImageVector = Icons.Filled.MoreVert
    val Sparkle: ImageVector = Icons.Filled.AutoAwesome

    /** Glyph for an app tile, keyed by [AppIconName]. */
    fun forApp(name: AppIconName): ImageVector = when (name) {
        AppIconName.NAVIGATION -> Icons.Filled.DriveEta
        AppIconName.MUSIC -> Icons.Filled.MusicNote
        AppIconName.PODCAST -> Icons.Filled.Podcasts
        AppIconName.CHARGING -> Icons.Filled.Bolt
        AppIconName.PARKING -> Icons.Filled.LocalParking
        AppIconName.WEATHER -> Icons.Filled.CloudQueue
        AppIconName.AUDIOBOOK -> Icons.Filled.Headphones
        AppIconName.MESSAGING -> Icons.Filled.Message
        AppIconName.CALENDAR -> Icons.Filled.Event
        AppIconName.RADIO -> Icons.Filled.Radio
        AppIconName.GAMES -> Icons.Filled.SportsEsports
        AppIconName.TOLLS -> Icons.Filled.ConfirmationNumber
    }

    /** Glyph for a permission row, keyed by [PermissionId]. */
    fun forPermission(permission: PermissionId): ImageVector = when (permission) {
        PermissionId.LOCATION -> Icons.Filled.DriveEta
        PermissionId.MICROPHONE -> Icons.Filled.Mic
        PermissionId.CONTACTS -> Icons.Filled.Message
        PermissionId.VEHICLE_DATA -> Icons.Filled.DirectionsCar
        PermissionId.NOTIFICATIONS -> Icons.Filled.Smartphone
        PermissionId.STORAGE -> Icons.Filled.Storage
        PermissionId.PHONE -> Icons.Filled.PhoneAndroid
    }

    /** Glyph for a category, keyed by [CategoryId]. */
    fun forCategory(category: CategoryId): ImageVector = when (category) {
        CategoryId.NAVIGATION -> Icons.Filled.DriveEta
        CategoryId.MEDIA -> Icons.Filled.MusicNote
        CategoryId.CHARGING -> Icons.Filled.Bolt
        CategoryId.COMMUNICATION -> Icons.Filled.Message
        CategoryId.UTILITIES -> Icons.Filled.Settings
        CategoryId.PARKED -> Icons.Filled.LocalParking
    }
}
