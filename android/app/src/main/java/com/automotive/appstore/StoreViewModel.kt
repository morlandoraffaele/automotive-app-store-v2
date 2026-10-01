package com.automotive.appstore

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.automotive.appstore.data.MockStoreRepository
import com.automotive.appstore.data.StoreSettings
import com.automotive.appstore.data.StoreSnapshot
import kotlinx.coroutines.flow.StateFlow

/**
 * Holds the store repository for the lifetime of the activity.
 *
 * The web app keeps its repository in a React context (`StoreProvider`); here the
 * ViewModel is the equivalent scope, which also means in-flight download timers survive
 * configuration changes.
 */
class StoreViewModel : ViewModel() {

    private val repository = MockStoreRepository(viewModelScope)

    val snapshot: StateFlow<StoreSnapshot> = repository.snapshot

    fun reloadCatalog() = repository.reloadCatalog()

    fun install(appId: String) = repository.install(appId)

    fun update(appId: String) = repository.update(appId)

    fun updateAll() = repository.updateAll()

    fun cancel(appId: String) = repository.cancel(appId)

    fun retry(appId: String) = repository.retry(appId)

    fun switchChannel(appId: String, channelId: String) = repository.switchChannel(appId, channelId)

    fun checkForStoreUpdate() = repository.checkForStoreUpdate()

    fun dismissStoreBanner() = repository.dismissStoreBanner()

    fun updateSettings(settings: StoreSettings) = repository.updateSettings(settings)
}
