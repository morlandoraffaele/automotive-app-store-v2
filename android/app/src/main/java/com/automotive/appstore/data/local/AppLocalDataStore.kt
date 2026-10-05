package com.automotive.appstore.data.local

import android.content.Context
import android.content.SharedPreferences

/**
 * `SharedPreferences`-backed store of the values that must survive a process death: the
 * version last recorded for a package and the id of its in-flight download.
 *
 * Ported 1:1 from
 * `radioplayer-automotive-appstore/app/src/main/java/org/radioplayer/automotive/appstore/data/local/AppLocalDataStore.kt`.
 */
class AppLocalDataStore(context: Context) {

    companion object {
        private const val PREFS_NAME = "app_store_local_data_prefs"
        private const val KEY_RECORDED_VERSION_PREFIX = "recorded_version_"
        private const val KEY_DOWNLOAD_ID_PREFIX = "download_id_"
        private const val KEY_HARDCODED_CONFIG = "hardcoded_config_enabled"
        private const val KEY_HARDCODED_STORE_VERSION = "hardcoded_config_store_version"
    }

    private val sharedPreferences: SharedPreferences? =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun saveRecordedVersion(packageName: String, versionCode: Int) {
        sharedPreferences?.edit()?.putInt(KEY_RECORDED_VERSION_PREFIX + packageName, versionCode)
            ?.apply()
    }

    fun getRecordedVersion(packageName: String): Int? {
        val version = sharedPreferences?.getInt(KEY_RECORDED_VERSION_PREFIX + packageName, -1)
        return if (version == -1) null else version
    }

    fun clearRecordedVersion(packageName: String) {
        sharedPreferences?.edit()?.remove(KEY_RECORDED_VERSION_PREFIX + packageName)?.apply()
    }

    fun saveDownloadId(packageName: String, downloadId: Long) {
        sharedPreferences?.edit()?.putLong(KEY_DOWNLOAD_ID_PREFIX + packageName, downloadId)?.apply()
    }

    fun getDownloadId(packageName: String): Long? {
        val id = sharedPreferences?.getLong(KEY_DOWNLOAD_ID_PREFIX + packageName, -1L)
        return if (id == -1L) null else id
    }

    fun clearDownloadId(packageName: String) {
        sharedPreferences?.edit()?.remove(KEY_DOWNLOAD_ID_PREFIX + packageName)?.apply()
    }

    // --- debug catalogue override -------------------------------------------------
    //
    // Persisted rather than in-memory because the self-update badge is evaluated at startup: an
    // in-memory flag would be lost on every process restart, which is precisely when the behaviour
    // under test happens.

    /** Whether [com.automotive.appstore.data.debug.HardcodedConfig] replaces the network response. */
    fun isHardcodedConfigEnabled(): Boolean =
        sharedPreferences?.getBoolean(KEY_HARDCODED_CONFIG, false) ?: false

    fun setHardcodedConfigEnabled(enabled: Boolean) {
        sharedPreferences?.edit()?.putBoolean(KEY_HARDCODED_CONFIG, enabled)?.apply()
    }

    /**
     * The store `version` the hardcoded config should publish.
     *
     * `null` means "derive one", which the repository resolves to the installed `versionCode` plus
     * a small bump — so enabling the override shows the badge without anyone having to work out a
     * number first.
     */
    fun getHardcodedStoreVersion(): Int? {
        val version = sharedPreferences?.getInt(KEY_HARDCODED_STORE_VERSION, -1)
        return if (version == -1) null else version
    }

    fun setHardcodedStoreVersion(versionCode: Int?) {
        val editor = sharedPreferences?.edit() ?: return
        if (versionCode == null) {
            editor.remove(KEY_HARDCODED_STORE_VERSION).apply()
        } else {
            editor.putInt(KEY_HARDCODED_STORE_VERSION, versionCode).apply()
        }
    }
}