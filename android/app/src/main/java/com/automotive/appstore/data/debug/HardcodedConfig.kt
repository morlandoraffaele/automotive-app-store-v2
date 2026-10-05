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
     * The raw document, with [storeVersionCode] substituted into the store's `versionCode`.
     *
     * Mirrors the current `config.json` contract: UUID `id`, `organization`, `versionName`,
     * `versionCode`, `type` and `channel`. It deliberately includes one entry of each `type` so
     * the catalogue's type filter has something to bite on.
     *
     * @param storeVersionCode the `versionCode` to publish for the store's own entry.
     */
    fun payload(storeVersionCode: Int): String = """
        {
          "org.radioplayer.automotive.appstore": {
            "id": "df1f043a-82cc-566f-9262-7bc110d3ef73",
            "name": "App Store (Self Update)",
            "description": "Automotive App Store application used to manage and support self-updating apps.",
            "organization": "Radioplayer",
            "packageName": "$STORE_APP_PACKAGE",
            "cls": "",
            "url": "https://automotive.radioplayer.org/store/apps/$STORE_APP_PACKAGE/source.apk",
            "icon": "https://automotive.radioplayer.org/store/apps/$STORE_APP_PACKAGE/app_icon.png",
            "versionName": "2.0.0",
            "versionCode": $storeVersionCode,
            "type": "custom",
            "channel": "stable"
          },
          "org.radioplayer.automotive.radio": {
            "id": "932dada9-127f-5280-8b31-c586637665f8",
            "name": "Radio",
            "description": "A complete, highly polished Radio application built for AAOS.",
            "organization": "Radioplayer",
            "packageName": "org.radioplayer.automotive.radio",
            "cls": "",
            "url": "https://automotive.radioplayer.org/store/apps/org.radioplayer.automotive.radio/source.apk",
            "icon": "https://automotive.radioplayer.org/store/apps/org.radioplayer.automotive.radio/app_icon.png",
            "versionName": "3.0.0",
            "versionCode": 20,
            "type": "custom",
            "channel": "stable"
          },
          "org.radioplayer.automotive.radio.alpha": {
            "id": "454bf8cd-405a-55b6-9185-85ca2e6db828",
            "name": "Radio (Alpha)",
            "description": "A complete, highly polished Radio application built for AAOS.",
            "organization": "Radioplayer",
            "packageName": "org.radioplayer.automotive.radio.alpha",
            "cls": "",
            "url": "https://automotive.radioplayer.org/store/apps/org.radioplayer.automotive.radio.alpha/source.apk",
            "icon": "https://automotive.radioplayer.org/store/apps/org.radioplayer.automotive.radio.alpha/app_icon.png",
            "versionName": "4.0.0-alpha.5",
            "versionCode": 24,
            "type": "custom",
            "channel": "alpha"
          },
          "com.bbc.sounds": {
            "id": "173527b9-99c7-51ec-af17-c97a4605f20e",
            "name": "BBC Sounds: Radio & Podcasts",
            "description": "Identifies the current FM/DAB station and deep links to the equivalent stream.",
            "organization": "Radioplayer",
            "packageName": "com.bbc.sounds",
            "cls": "com.bbc.sounds.mediabrowser.SoundsMediaBrowserService",
            "url": "https://automotive.radioplayer.org/store/apps/com.bbc.sounds/source.apk",
            "icon": "https://automotive.radioplayer.org/store/apps/com.bbc.sounds/app_icon.png",
            "versionName": "",
            "versionCode": 1,
            "type": "media",
            "channel": "stable"
          },
          "org.radioplayer.radioplayercar.online.headless": {
            "id": "b1f0c9d4-3a77-5f8e-9c21-6d4b0e2a7f11",
            "name": "Radioplayer - Radio & Podcast",
            "description": "Listen to the radio stations you love with the official radio app.",
            "organization": "Radioplayer",
            "packageName": "org.radioplayer.radioplayercar.online.headless",
            "cls": "org.radioplayer.radioplayercar.online.headless.MediaService",
            "url": "https://automotive.radioplayer.org/store/apps/org.radioplayer.radioplayercar.online.headless/source.apk",
            "icon": "https://automotive.radioplayer.org/store/apps/org.radioplayer.radioplayercar.online.headless/app_icon.png",
            "versionName": "",
            "versionCode": 3045,
            "type": "media",
            "channel": "demo"
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