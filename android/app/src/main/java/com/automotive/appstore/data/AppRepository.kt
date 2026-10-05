package com.automotive.appstore.data

import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import com.automotive.appstore.BuildConfig
import com.automotive.appstore.data.debug.HardcodedConfig
import com.automotive.appstore.data.local.AppLocalDataStore
import com.automotive.appstore.data.remote.App
import com.automotive.appstore.data.remote.AppApiService

/**
 * The store's single data entry point: fetches the remote catalogue, reads the installed
 * version of a package and persists in-flight download ids.
 *
 * Ported 1:1 from
 * `radioplayer-automotive-appstore/app/src/main/java/org/radioplayer/automotive/appstore/repository/AppRepository.kt`.
 */
class AppRepository(
    private val apiService: AppApiService,
    private val localDataStore: AppLocalDataStore,
    private val context: Context
) {
    companion object {
        private const val TAG = "AppRepository"
    }

    /**
     * Fetches `store/config.json`.
     *
     * When the debug override is on this returns [HardcodedConfig] instead of making a request —
     * see [com.automotive.appstore.data.debug.HardcodedConfig] for why that exists. The override is
     * checked before the network so a developer never has to be online to exercise it, and it feeds
     * both the catalogue and the store self-update check, since both read this one result.
 *
     * @param fresh appends a cache-busting timestamp so an explicit user-triggered re-check cannot
     *   be answered from an intermediary cache. OkHttp has no `Cache` installed, so the request
     *   always reaches the network regardless; this only guards against a CDN in front of it, and
     *   is skipped on the startup path where nothing else has just been fetched.
     */
    suspend fun getAppList(fresh: Boolean = false): Result<List<App>> {
        return withContext(Dispatchers.IO) {
            try {
                if (localDataStore.isHardcodedConfigEnabled()) {
                    return@withContext Result.success(hardcodedApps())
                }
                val url = if (fresh) {
                    "${BuildConfig.APP_LIST_ENDPOINT_URL}?_=${System.currentTimeMillis()}"
                } else {
                    BuildConfig.APP_LIST_ENDPOINT_URL
                }
                val response = apiService.getAppList(url)
                if (response.isSuccessful) {
                    val appMap = response.body()
                    if (appMap != null) {
                        val appList = appMap.map { App(it.key, it.value) }
                        Result.success(appList)
                    } else {
                        Result.failure(Exception("Empty response"))
                    }
                } else {
                    Result.failure(Exception("Network error: ${response.code()} ${response.message()}"))
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error during fetching app list: ", e)
                Result.failure(e)
            }
        }
    }

    /**
     * The debug catalogue, with its store `version` resolved.
     *
     * A pinned version wins; otherwise the published version is the running one plus
     * [HardcodedConfig.DEFAULT_STORE_VERSION_BUMP], so enabling the override reliably produces the
     * "update available" badge. Deriving it from the installed `versionCode` also keeps the
     * override meaningful after a real self-update, where a hardcoded number would start lying.
     */
    private fun hardcodedApps(): List<App> {
        val installed = getInstalledVersionCode(STORE_APP_PACKAGE)
        val storeVersion = localDataStore.getHardcodedStoreVersion()
            ?: ((installed ?: 0) + HardcodedConfig.DEFAULT_STORE_VERSION_BUMP)
        Log.i(TAG, "Using hardcoded catalogue with store version $storeVersion")
        return HardcodedConfig.apps(storeVersion)
    }

    fun getInstalledVersionCode(packageName: String): Int? {
        return try {
            val packageInfo: PackageInfo = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                context.packageManager.getPackageInfo(packageName, PackageManager.PackageInfoFlags.of(0))
            } else {
                context.packageManager.getPackageInfo(packageName, 0)
            }
            packageInfo.versionCode
        } catch (e: PackageManager.NameNotFoundException) {
            null
        }
    }

    fun saveDownloadId(packageName: String, downloadId: Long) {
        localDataStore.saveDownloadId(packageName, downloadId)
    }

    fun getDownloadId(packageName: String): Long? {
        return localDataStore.getDownloadId(packageName)
    }

    fun clearDownloadId(packageName: String) {
        localDataStore.clearDownloadId(packageName)
    }

    // --- debug catalogue override ---------------------------------------------
    //
    // Thin pass-throughs to the data store; the payload itself lives in `HardcodedConfig`.

    fun isHardcodedConfigEnabled(): Boolean = localDataStore.isHardcodedConfigEnabled()

    fun setHardcodedConfigEnabled(enabled: Boolean) =
        localDataStore.setHardcodedConfigEnabled(enabled)

    fun hardcodedStoreVersion(): Int? = localDataStore.getHardcodedStoreVersion()

    fun setHardcodedStoreVersion(versionCode: Int?) =
        localDataStore.setHardcodedStoreVersion(versionCode)
}