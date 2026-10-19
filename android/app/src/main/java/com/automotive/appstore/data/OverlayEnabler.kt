package com.automotive.appstore.data

import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Build
import android.os.Process
import android.os.UserHandle
import android.util.Log
import kotlinx.coroutines.delay
import java.lang.reflect.InvocationTargetException

/**
 * Enables the Runtime Resource Overlays (RROs) the store installs.
 *
 * The catalogue can publish an RRO like any other APK (e.g. the overlays that add Radioplayer to
 * car-media-common's `custom_media_packages`), and it goes through the same download, install and
 * update flow. An overlay installed into `/data`, however, always starts **disabled**, so after the
 * install lands the store enables it for its own user, which is what `cmd overlay enable` did
 * by hand. The enabled state is kept by the platform across reboots and updates.
 *
 * `OverlayManager.getOverlayInfo` / `setEnabled` and `OverlayInfo.isEnabled` are `@SystemApi`,
 * hence the reflection; hidden-API enforcement is disabled for the platform-signed build. The
 * caller needs `CHANGE_OVERLAY_PACKAGES` (`signature|privileged`), and the target's
 * `<overlayable>` must either name no `actor` (the platform then falls back to that permission
 * check, as for the `CarMediaCommon` block) or name this app as its actor.
 */
class OverlayEnabler(private val context: Context) {

    private companion object {
        const val TAG = "OverlayEnabler"

        /** Hidden `Context.OVERLAY_SERVICE`. */
        const val OVERLAY_SERVICE = "overlay"

        /**
         * The overlay manager registers a freshly installed overlay asynchronously, so it can be
         * unknown to it for a moment after the install broadcast. Enabling it then fails with
         * "Unable to retrieve overlay information".
         */
        const val REGISTER_ATTEMPTS = 20
        const val REGISTER_RETRY_MS = 500L
    }

    /**
     * The overlay's target package, or `null` when [packageName] is not an overlay or is not
     * installed. Read from the hidden `PackageInfo.overlayTarget` field.
     */
    fun overlayTarget(packageName: String): String? = try {
        val info: PackageInfo = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            context.packageManager.getPackageInfo(packageName, PackageManager.PackageInfoFlags.of(0))
        } else {
            context.packageManager.getPackageInfo(packageName, 0)
        }
        PackageInfo::class.java.getField("overlayTarget").get(info) as String?
    } catch (e: PackageManager.NameNotFoundException) {
        null
    } catch (e: ReflectiveOperationException) {
        Log.e(TAG, "Cannot read the overlay target of $packageName", e)
        null
    }

    /**
     * Enables [packageName] for this process's user if it is an overlay, waiting for the overlay
     * manager to register it first. Does nothing for an ordinary app.
     *
     * @return `true` when the overlay is enabled, `false` when it is not an overlay or could not
     *   be enabled; the reason is logged.
     */
    suspend fun enableIfOverlay(packageName: String): Boolean {
        val target = overlayTarget(packageName) ?: return false
        val user = Process.myUserHandle()
        val manager = context.getSystemService(OVERLAY_SERVICE) ?: run {
            Log.e(TAG, "No overlay service; cannot enable $packageName")
            return false
        }

        repeat(REGISTER_ATTEMPTS) {
            try {
                val info = getOverlayInfo(manager, packageName, user)
                if (info != null) {
                    if (isEnabled(info)) {
                        Log.i(TAG, "Overlay $packageName -> $target already enabled for $user")
                        return true
                    }
                    setEnabled(manager, packageName, user)
                    if (getOverlayInfo(manager, packageName, user)?.let(::isEnabled) == true) {
                        Log.i(TAG, "Enabled overlay $packageName -> $target for $user")
                        return true
                    }
                }
            } catch (e: SecurityException) {
                // Not retried: a missing permission or actor does not fix itself.
                Log.e(TAG, "Not allowed to enable overlay $packageName -> $target", e)
                return false
            } catch (e: Exception) {
                Log.w(TAG, "Enabling overlay $packageName not possible yet: ${e.message}")
            }
            delay(REGISTER_RETRY_MS)
        }
        Log.e(TAG, "Gave up enabling overlay $packageName -> $target for $user")
        return false
    }

    private fun getOverlayInfo(manager: Any, packageName: String, user: UserHandle): Any? =
        invoke(manager, "getOverlayInfo", arrayOf(String::class.java, UserHandle::class.java), packageName, user)

    private fun setEnabled(manager: Any, packageName: String, user: UserHandle) {
        invoke(
            manager,
            "setEnabled",
            arrayOf(String::class.java, Boolean::class.javaPrimitiveType!!, UserHandle::class.java),
            packageName,
            true,
            user,
        )
    }

    private fun isEnabled(info: Any): Boolean =
        invoke(info, "isEnabled", emptyArray()) as Boolean

    /** Calls a hidden method, rethrowing what it threw rather than the reflection wrapper. */
    private fun invoke(target: Any, name: String, types: Array<Class<*>>, vararg args: Any?): Any? = try {
        target.javaClass.getMethod(name, *types).invoke(target, *args)
    } catch (e: InvocationTargetException) {
        throw e.targetException
    }
}
