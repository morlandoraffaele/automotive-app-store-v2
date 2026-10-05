package com.automotive.appstore.data.debug

import com.automotive.appstore.data.STORE_APP_PACKAGE
import com.automotive.appstore.data.remote.App
import com.automotive.appstore.data.remote.AppDetails
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

/**
 * A frozen copy of `store/config.json`, served in place of the network response so the store's
 * self-update path can be exercised without touching the real endpoint.
 *
 * This exists because the self-update check is **correctly silent** against production data: the
 * APK is built at `versionCode = 2` and `config.json` publishes `"version": 2` for
 * `org.radioplayer.automotive.appstore`, so `2 > 2` is false and the badge never appears. There is
 * no way to see the badge, the Settings action or the failure paths without either publishing a
 * new build to the live endpoint or serving a doctored catalogue — and the latter is what this is.
 *
 * The payload is a trimmed but otherwise verbatim subset of the live document: same keys, same
 * field names, same `url`/`icon` hosts, so it exercises the real Gson mapping and the real
 * `CatalogMapper` rather than a hand-built model. The APK URLs point at the genuine files, so a
 * self-update started from here really does download and install.
 *
 * Only the store's `version` is parameterised, since that is the single field the badge turns on.
 */
object HardcodedConfig {

    /**
     * The store version to publish when the developer has not pinned one.
     *
     * Deliberately *newer* than whatever is installed: the interesting case is the badge being
     * visible, and a default that made it invisible would look like the feature being broken.
     */
    const val DEFAULT_STORE_VERSION_BUMP = 1

    /**
     * The raw document, with [storeVersionCode] substituted into the store's `version`.
     *
     * @param storeVersionCode the `version` to publish for the store's own entry.
     */
    fun payload(storeVersionCode: Int): String = """
        {
          "org.radioplayer.automotive.appstore": {
            "id": 1,
            "name": "App Store (Self Update)",
            "description": "The best App Store in the entire Milky Way",
            "packageName": "$STORE_APP_PACKAGE",
            "cls": "",
            "url": "https://automotive.radioplayer.org/store/apps/$STORE_APP_PACKAGE/source.apk",
            "icon": "https://automotive.radioplayer.org/store/apps/$STORE_APP_PACKAGE/app_icon.png",
            "version": $storeVersionCode
          },
          "org.radioplayer.automotive.radio": {
            "id": 1,
            "name": "Radio",
            "description": "The best Radio in the entire Milky Way",
            "packageName": "org.radioplayer.automotive.radio",
            "cls": "",
            "url": "https://automotive.radioplayer.org/store/apps/org.radioplayer.automotive.radio/source.apk",
            "icon": "https://automotive.radioplayer.org/store/apps/org.radioplayer.automotive.radio/app_icon.png",
            "version": 20
          },
          "com.bbc.sounds": {
            "id": 2,
            "name": "BBC Sounds",
            "description": "Listen to radio, podcasts and audio books.",
            "packageName": "com.bbc.sounds",
            "cls": "com.bbc.sounds.media.MediaBrowserServiceImpl",
            "url": "https://automotive.radioplayer.org/store/apps/com.bbc.sounds/source.apk",
            "icon": "https://automotive.radioplayer.org/store/apps/com.bbc.sounds/app_icon.png",
            "version": 1
          },
          "org.radioplayer.automotive.radio.stellantis": {
            "id": 3,
            "name": "Radio",
            "description": "The best Radio in the entire Milky Way, for Stellantis.",
            "packageName": "org.radioplayer.automotive.radio.stellantis",
            "cls": "",
            "url": "https://automotive.radioplayer.org/store/apps/org.radioplayer.automotive.radio.stellantis/source.apk",
            "icon": "https://automotive.radioplayer.org/store/apps/org.radioplayer.automotive.radio.stellantis/app_icon.png",
            "version": 21
          }
        }
    """.trimIndent()

    /**
     * [payload] mapped through the same `AppDetails` shape the endpoint returns.
     *
     * Goes via Gson rather than hand-constructing `App` values on purpose: the override should fail
     * the same way the network path fails if the contract ever changes.
     */
    fun apps(storeVersionCode: Int): List<App> {
        val type = object : TypeToken<Map<String, AppDetails>>() {}.type
        val map: Map<String, AppDetails> = Gson().fromJson(payload(storeVersionCode), type)
        return map.map { (key, details) -> App(key, details) }
    }
}