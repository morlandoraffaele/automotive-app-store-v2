package com.automotive.appstore

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.automotive.appstore.data.StoreRepository
import com.automotive.appstore.data.StoreSettings
import com.automotive.appstore.data.StoreSnapshot
import kotlinx.coroutines.flow.StateFlow

/**
 * Holds the store repository for the lifetime of the activity.
 *
 * The web app keeps its repository in a React context (`StoreProvider`); here the
 * ViewModel is the equivalent scope, which also means in-flight download timers survive
 * configuration changes.
 *
 * The signature is unchanged from the mock-backed version — every screen takes a
 * `StoreViewModel` and calls the same methods — but it is now an [AndroidViewModel] because
 * the repository ported from `radioplayer-automotive-appstore` needs the `Application` for
 * `DownloadManager`, `PackageManager` and `PackageInstaller`.
 */
class StoreViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = StoreRepository(application, viewModelScope)

    val snapshot: StateFlow<StoreSnapshot> = repository.snapshot

    fun reloadCatalog() = repository.reloadCatalog()

    fun install(appId: String) = repository.install(appId)

    /**
     * Starts an installed app.
     *
     * @return `true` when it launched, `false` when there was nothing to launch.
     */
    fun launch(appId: String): Boolean = repository.launch(appId)

    fun update(appId: String) = repository.update(appId)

    /**
     * Removes an installed app from the device.
     *
     * @return `true` when the removal was requested, `false` when it could not be started.
     */
    fun uninstall(appId: String): Boolean = repository.uninstall(appId)

    fun updateAll() = repository.updateAll()

    fun cancel(appId: String) = repository.cancel(appId)

    fun retry(appId: String) = repository.retry(appId)

    fun switchChannel(appId: String, channelId: String) = repository.switchChannel(appId, channelId)

    fun checkForStoreUpdate() = repository.checkForStoreUpdate()

    /**
     * Reconciles any in-progress install against real package state.
     *
     * Called when the activity resumes, which is when the installer's confirmation dialog returns
     * to us. The reference app does the same from its detail screen's `repeatOnLifecycle(RESUMED)`
     * block; it lives here so no UI file has to change.
     */
    fun onAppResumed() = repository.onAppResumed()

    fun dismissStoreBanner() = repository.dismissStoreBanner()

    fun updateSettings(settings: StoreSettings) = repository.updateSettings(settings)
}
