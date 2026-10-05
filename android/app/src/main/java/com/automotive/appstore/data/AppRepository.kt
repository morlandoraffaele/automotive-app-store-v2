package com.automotive.appstore.data

import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
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
        private const val APP_LIST_ENDPOINT_URL =
            "https://automotive.radioplayer.org/store/config.json"
    }

    suspend fun getAppList(): Result<List<App>> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getAppList(APP_LIST_ENDPOINT_URL)
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
}