package com.automotive.appstore.data.remote

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Url

/**
 * Store catalogue endpoint.
 *
 * Ported 1:1 from
 * `radioplayer-automotive-appstore/app/src/main/java/org/radioplayer/automotive/appstore/data/remote/AppApiService.kt`.
 */
interface AppApiService {
    @GET
    suspend fun getAppList(@Url url: String): Response<Map<String, AppDetails>>
}