package com.automotive.appstore.data.remote

import com.google.gson.annotations.SerializedName

/**
 * One entry of `store/config.json`, keyed by its package name.
 *
 * Ported 1:1 from
 * `radioplayer-automotive-appstore/app/src/main/java/org/radioplayer/automotive/appstore/data/remote/AppInfo.kt`.
 *
 * The reference project declares these `Parcelable` because it passes them through navigation
 * arguments; this app navigates by app id only, so the `Parcelable`/`@Parcelize` contract (and the
 * `kotlin-parcelize` plugin it requires) is intentionally not carried over.
 *
 * ## Contract drift
 *
 * The endpoint moved to a richer document that adds `organization`, `versionName`, `type` and
 * `channel`, renames `version` to `versionCode`, and changes `id` from a number to a UUID string.
 * **The live endpoint has not been switched over yet** — as of writing it still serves the old
 * shape, so both are accepted here:
 *
 *  - `versionCode` has `version` as a Gson [SerializedName.alternate], so one field reads either.
 *    This matters because a silent fallback is not possible otherwise: `versionCode` is the number
 *    every install/update decision is made on, and reading it as 0 would make every app look
 *    permanently up to date.
 *  - Everything genuinely new is nullable with a defined fallback, so a missing `type` degrades to
 *    [org.radioplayer.automotive.appstore.data.AppType.OTHER] rather than dropping the app from the
 *    catalogue.
 *
 * Remove the alternate and the null handling once production serves only the new document.
 */
data class AppDetails(
    @SerializedName("id") val id: String = "",
    @SerializedName("name") val name: String = "",
    @SerializedName("description") val description: String = "",
    /** The publisher. Replaces the package-prefix guess the old document forced. */
    @SerializedName("organization") val organization: String? = null,
    @SerializedName("packageName") val packageName: String = "",
    @SerializedName("cls") val cls: String? = null,
    @SerializedName("url") val apkUrl: String = "",
    @SerializedName("icon") val icon: String? = null,
    /** e.g. `3.0.0-rc.20`. Display only — often empty on media entries. */
    @SerializedName("versionName") val versionName: String? = null,
    /** Android `versionCode`. The legacy document called this `version`. */
    @SerializedName(value = "versionCode", alternate = ["version"])
    val remoteVersionCode: Int = 0,
    /** `media` or `custom`. Unrecognised values map to `AppType.OTHER`. */
    @SerializedName("type") val type: String? = null,
    /** e.g. `stable`, `demo`, `release-candidate`, `alpha`. */
    @SerializedName("channel") val channel: String? = null,
)

/** A catalogue entry paired with the key it was published under. */
data class App(
    val key: String,
    val details: AppDetails
)