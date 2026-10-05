package com.automotive.appstore.data.remote

import com.google.gson.annotations.SerializedName

/**
 * One entry of `store/config.json`, keyed by its package name.
 *
 * Ported 1:1 from
 * `radioplayer-automotive-appstore/app/src/main/java/org/radioplayer/automotive/appstore/data/remote/AppInfo.kt`.
 *
 * The reference project declares these `Parcelable` because it passes them through navigation
 * arguments; this app navigates by app id only, so the `Parcelable`/`@Parcelize` contract (and
 * the `kotlin-parcelize` plugin it requires) is intentionally not carried over.
 */
data class AppDetails(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String,
    @SerializedName("description") val description: String,
    @SerializedName("packageName") val packageName: String,
    @SerializedName("cls") val cls: String?,
    @SerializedName("url") val apkUrl: String,
    @SerializedName("icon") val icon: String?,
    @SerializedName("version") val remoteVersionCode: Int
)

/** A catalogue entry paired with the key it was published under. */
data class App(
    val key: String,
    val details: AppDetails
)