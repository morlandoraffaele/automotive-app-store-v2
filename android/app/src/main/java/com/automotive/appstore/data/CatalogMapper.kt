package com.automotive.appstore.data

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.util.Log
import com.automotive.appstore.data.remote.App
import com.automotive.appstore.data.remote.AppDetails

/**
 * Bridges the remote `config.json` contract to the [AppListing] the UI renders.
 *
 * The reference implementation (`radioplayer-automotive-appstore`) has no category, channel or
 * screenshot concept at all — its screens show a plain name/description grid — so those fields
 * have no counterpart upstream. Rather than change the UI, they are derived deterministically
 * from the fields the endpoint does publish, so the same package always yields the same tile.
 *
 * Everything the *behaviour* depends on — [AppListing.packageName], [AppListing.apkUrl] and
 * [AppListing.remoteVersionCode] — is carried across verbatim.
 */
object CatalogMapper {

    /** `versionCode` rendered as the version string the UI compares against. */
    fun versionLabel(versionCode: Int): String = versionCode.toString()

    /**
     * The version to record for an app that is already on the device.
     *
     * The UI derives "update available" by comparing the recorded installed version with the
     * published one **as strings** (`release.version != installed.version`), while the endpoint
     * publishes an Android `versionCode`. Those only agree if we record the number the store
     * published whenever the device is at least that new.
     *
     * This matters because `store/config.json` carries placeholder versions for many entries — 15
     * of the 31 published entries declare `"version": 1` — while the APKs they point at are far
     * newer (`de.ard.audiothek` publishes 1 but installs `versionCode=21400`). Comparing those
     * literally reports "update available" forever, and the user can never reach an up-to-date
     * state. The reference app never hit this because it compared the two as *numbers*
     * (`remoteVersion > installedVersion`) instead of as strings.
     *
     * So: record the published version when the device is at least that new (the UI then reads
     * "up to date"), and the real installed number when it is genuinely older (the UI reads
     * "update available", with the correct current/target pair).
     */
    fun installedVersionLabel(installedVersionCode: Int, remoteVersionCode: Int): String =
        versionLabel(if (installedVersionCode >= remoteVersionCode) remoteVersionCode else installedVersionCode)

    /**
     * Whether a device running [installedVersionCode] has a build at least as new as the one the
     * store publishes.
     *
     * Numeric, like the reference app's `remoteVersion > installedVersion`, and deliberately
     * independent of the placeholder values in `config.json`.
     */
    fun isUpToDate(installedVersionCode: Int, remoteVersionCode: Int): Boolean =
        installedVersionCode >= remoteVersionCode

    /**
     * The version a store update would bring, or `null` when the running store is already current.
     *
     * Used for the store's own self-update, where the question is simply "is the build published
     * for this package newer than the one running?". Unlike [installedVersionLabel] this does not
     * rewrite anything towards the published value — a device running something *newer* than the
     * catalogue advertises is current, not out of date — and an unreadable installed `versionCode`
     * is treated as "unknown", which suppresses the badge rather than guessing.
     */
    fun storeUpdateVersionLabel(installedVersionCode: Int?, remoteVersionCode: Int): String? =
        if (installedVersionCode != null && remoteVersionCode > installedVersionCode) {
            versionLabel(remoteVersionCode)
        } else {
            null
        }

    /** Maps one remote entry onto an [AppListing]. */
    fun toListing(app: App): AppListing {
        val details = app.details
        val classification = classificationFor(app.key, details)
        return AppListing(
            id = app.key,
            name = details.name,
            developer = developerOf(app.key),
            tagline = taglineOf(details),
            description = details.description,
            category = classification.category,
            icon = classification.icon,
            iconColor = colorOf(app.key),
            // No size is published by the endpoint, and the APK is only fetched on install, so
            // this stays 0 rather than inventing a number the store would then contradict.
            sizeMb = 0,
            permissions = emptyList(),
            screenshots = emptyList(),
            releases = listOf(
                ChannelRelease(
                    channelId = DEFAULT_CHANNEL_ID,
                    version = versionLabel(details.remoteVersionCode),
                    releaseDate = "",
                    changelog = emptyList(),
                )
            ),
            packageName = details.packageName,
            apkUrl = details.apkUrl,
            iconUrl = details.icon,
            mediaServiceClass = details.cls,
            remoteVersionCode = details.remoteVersionCode,
        )
    }

    fun toListings(apps: List<App>): List<AppListing> = apps.map(::toListing)

    /**
     * Builds the Intent that hands control to an installed app, or `null` when the app exposes
     * nothing this store can open.
     *
     * Two shapes exist in the catalogue:
     *
     * - Apps with a launcher activity (most of them). `getLaunchIntentForPackage` is the answer.
     * - Headless automotive media apps, which have **no launcher activity at all** and instead
     *   publish a `MediaBrowserService` in `config.json`'s `cls` field. `com.bbc.sounds` and
     *   `de.ard.audiothek` are both of this kind — starting them means asking the media browser
     *   for their playback UI, not starting an Intent.
     *
     * `cls` is only ever treated as a *service* here, never as an activity: it names a
     * `MediaBrowserService`, and building an explicit activity Intent for it produces
     * `ActivityNotFoundException` at launch.
     */
    fun isHeadlessMediaApp(context: Context, listing: AppListing): Boolean {
        val hasService = !listing.mediaServiceClass.isNullOrBlank()
        // Uses the widened activity search, so an app whose UI activity omits CATEGORY_LAUNCHER is
        // not misclassified as headless and needlessly routed to the media-browser path.
        return hasService && !hasLauncherActivity(context, listing)
    }

    /**
     * Whether the store can open this app by starting an activity at all.
     *
     * A headless app — one that publishes a `MediaBrowserService` but no launcher entry — is
     * controlled as a media client instead ([launch], in the repository), so this only reports
     * whether a normal `startActivity` route exists.
     */
    fun hasLauncherActivity(context: Context, listing: AppListing): Boolean =
        launchIntentFor(context, listing) != null

    /**
     * Builds the Intent that brings [listing] to the foreground, or `null` when it exposes none.
     *
     * `PackageManager.getLaunchIntentForPackage` alone is **not** sufficient for this catalogue.
     * It only matches an activity declaring `ACTION_MAIN` **and** `CATEGORY_LAUNCHER`, and
     * automotive head-unit apps routinely declare `ACTION_MAIN` on their UI activity while omitting
     * the `LAUNCHER` category — they are launched by a store or a launcher app, never from a home
     * screen. For those, `getLaunchIntentForPackage` returns `null` and the Open button silently
     * did nothing, which is the bug this fallback exists to fix.
     *
     * So the search widens in steps, each one a superset of the last, and stops at the first
     * exported activity it finds:
     *  1. the canonical launcher entry (`MAIN` + `LAUNCHER`);
     *  2. `MAIN` + `LAUNCHER`, resolved by query — covers entries `getLaunchIntentForPackage`
     *     rejects for not being the *preferred* one;
     *  3. bare `MAIN`, the head-unit pattern above;
     *  4. any activity the package exposes at all.
     */
    fun launchIntentFor(context: Context, listing: AppListing): Intent? {
        val pm = context.packageManager
        val packageName = listing.packageName

        pm.getLaunchIntentForPackage(packageName)?.let { return it }

        for ((query, launcherEntry) in launchQueries()) {
            query.setPackage(packageName)
            val resolved = runCatching {
                pm.queryIntentActivities(query, PackageManager.MATCH_DEFAULT_ONLY)
            }.getOrNull().orEmpty()

            // Everything a MAIN + LAUNCHER query returns is a launcher entry by construction, so
            // the flag comes from the query rather than from re-reading each ActivityInfo's filters.
            val info = pickLaunchActivity(
                resolved.mapNotNull { resolved ->
                    resolved.activityInfo?.let { activityInfo ->
                        toLaunchCandidate(
                            packageName = activityInfo.packageName,
                            className = activityInfo.name,
                            exported = activityInfo.exported,
                            isLauncherEntry = launcherEntry,
                        )
                    }
                },
            ) ?: continue

            Log.i(
                "CatalogMapper",
                "Resolved ${info.packageName}/${info.className} for $packageName via " +
                    "${query.action ?: "any"} intent",
            )
            return Intent(Intent.ACTION_MAIN)
                .setComponent(ComponentName(info.packageName, info.className))
        }

        return null
    }

    /**
     * The activity queries [launchIntentFor] tries, widest last, each paired with whether its
     * results are launcher entries.
     *
     * Kept as a function so the widening order is an explicit, documented list rather than an
     * inline chain — and so each entry stays a distinct Intent instance, since `setPackage` mutates
     * the query in place.
     */
    private fun launchQueries(): List<Pair<Intent, Boolean>> = listOf(
        // MAIN + LAUNCHER, resolved by query: covers entries `getLaunchIntentForPackage` rejects
        // because they are not the package's *preferred* launcher activity.
        Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER) to true,
        // Bare MAIN — the head-unit pattern: a UI activity that is never shown on a home screen.
        Intent(Intent.ACTION_MAIN) to false,
        // Anything at all, as a last resort for an app whose UI activity declares no MAIN.
        Intent() to false,
    )

    /**
     * A launchable activity candidate, decoupled from `ActivityInfo`.
     *
     * The picking rule is a pure decision, so it is expressed over this instead of over
     * `ActivityInfo`: `ActivityInfo` is an Android framework class whose members are stubs that
     * cannot be constructed off-device, which would make the rule untestable in a plain JVM test.
     *
     * @param exported whether the activity can be started from another package.
     * @param isLauncherEntry whether the query that produced it asked for `MAIN` + `LAUNCHER`.
     */
    internal data class LaunchCandidate(
        val packageName: String,
        val className: String,
        val exported: Boolean,
        val isLauncherEntry: Boolean,
    )

    /**
     * Chooses which of a query's resolved activities to start.
     *
     * Prefers an exported launcher entry, because that is by definition the activity a user would
     * tap from a home screen; then any exported activity, because starting a non-exported one
     * throws `SecurityException` from another package; and finally the first candidate of all, so
     * a package that declares everything non-exported is still attempted rather than being
     * silently declared unlaunchable.
     *
     * @return the chosen candidate, or `null` when [candidates] is empty.
     */
    internal fun pickLaunchActivity(candidates: List<LaunchCandidate>): LaunchCandidate? =
        candidates.firstOrNull { it.exported && it.isLauncherEntry }
            ?: candidates.firstOrNull { it.exported }
            ?: candidates.firstOrNull()

    /** Projects a `PackageManager` result onto [LaunchCandidate]. */
    private fun toLaunchCandidate(
        packageName: String,
        className: String,
        exported: Boolean,
        isLauncherEntry: Boolean,
    ): LaunchCandidate = LaunchCandidate(packageName, className, exported, isLauncherEntry)

    /**
     * The media-browser session for a headless app, or `null` when it publishes no service.
     *
     * Returned rather than started, because binding is asynchronous and needs a result callback.
     */
    fun mediaBrowserComponentFor(listing: AppListing): ComponentName? {
        val cls = listing.mediaServiceClass?.takeIf { it.isNotBlank() } ?: return null
        return ComponentName(
            listing.packageName,
            resolveClassName(listing.packageName, cls),
        )
    }

    /**
     * Expands a class name from `config.json` into a fully-qualified one.
     *
     * The endpoint publishes some classes in Java's relative form — `service.RadioplayerService` or
     * even `.service.RadioplayerService` — which are only meaningful once joined to their package.
     * [ComponentName] requires the full name, so a leading dot expands to `<package>` and a
     * dotted-but-relative name is prefixed too.
     */
    fun resolveClassName(packageName: String, cls: String): String = when {
        cls.startsWith(".") -> packageName + cls
        !cls.contains(".") -> "$packageName.$cls"
        else -> cls
    }

    /**
     * The publisher, read off the package prefix: `org.radioplayer.automotive.radio` reads as
     * "Radioplayer". Falls back to the whole package when there is no prefix to speak of.
     */
    private fun developerOf(packageName: String): String {
        val prefix = packageName.split('.').firstOrNull { it.isNotBlank() } ?: return packageName
        return prefix.replaceFirstChar { it.uppercase() }
    }

    /** The first sentence of the description, falling back to the name. */
    private fun taglineOf(details: AppDetails): String {
        val description = details.description.trim()
        if (description.isEmpty()) return details.name
        val stop = description.indexOfFirst { it == '.' || it == '\n' }
        val tagline = if (stop > 0) description.substring(0, stop) else description
        return tagline.ifBlank { details.name }
    }

    /** The category and glyph resolved for a remote entry. */
    data class Classification(val category: CategoryId, val icon: AppIconName)

    /**
     * Category and glyph by keyword over the package name and the app name.
     *
     * The endpoint publishes neither, so they are resolved together from one rule set: a tile
     * must never end up filed under `media` with a navigation glyph. An app matching nothing is
     * filed under `utilities` with a radio glyph rather than hidden from the category filter.
     */
    fun classificationFor(packageName: String, details: AppDetails): Classification {
        val haystack = "${packageName} ${details.name}".lowercase()
        val match = CATEGORY_KEYWORDS.firstOrNull { (keywords, _, _) ->
            keywords.any { haystack.contains(it) }
        }
        return Classification(
            category = match?.second ?: CategoryId.UTILITIES,
            icon = match?.third ?: AppIconName.RADIO,
        )
    }

    /**
     * A stable colour per package. The `oklch`-derived sRGB values from the design are reused so
     * the tiles keep the same visual identity the web build has.
     */
    private fun colorOf(packageName: String): Int {
        val hash = packageName.hashCode()
        val index = ((if (hash == Int.MIN_VALUE) 0 else hash) % TILE_COLORS.size + TILE_COLORS.size) %
            TILE_COLORS.size
        return TILE_COLORS[index]
    }

    /** Keywords are matched in order, so the more specific entries come first. */
    private val CATEGORY_KEYWORDS: List<Triple<List<String>, CategoryId, AppIconName>> = listOf(
        Triple(listOf("waypoint", "navigation", "maps"), CategoryId.NAVIGATION, AppIconName.NAVIGATION),
        Triple(listOf("charging", "chargegrid"), CategoryId.CHARGING, AppIconName.CHARGING),
        Triple(listOf("parking", "parkspot", "parked"), CategoryId.PARKED, AppIconName.PARKING),
        Triple(listOf("weather"), CategoryId.UTILITIES, AppIconName.WEATHER),
        Triple(listOf("audiobook", "audible"), CategoryId.MEDIA, AppIconName.AUDIOBOOK),
        Triple(listOf("podcast"), CategoryId.MEDIA, AppIconName.PODCAST),
        Triple(listOf("messenger", "message"), CategoryId.COMMUNICATION, AppIconName.MESSAGING),
        Triple(listOf("calendar"), CategoryId.UTILITIES, AppIconName.CALENDAR),
        Triple(listOf("game", "arcade"), CategoryId.PARKED, AppIconName.GAMES),
        Triple(listOf("toll"), CategoryId.UTILITIES, AppIconName.TOLLS),
        Triple(listOf("radio", "radioplayer", "rayo", "media"), CategoryId.MEDIA, AppIconName.RADIO),
        Triple(listOf("music"), CategoryId.MEDIA, AppIconName.MUSIC),
    )

    private val TILE_COLORS = intArrayOf(
        0xFF0094CE.toInt(),
        0xFF7370FA.toInt(),
        0xFF12A594.toInt(),
        0xFFE5484D.toInt(),
        0xFFF76B15.toInt(),
        0xFF8E4EC6.toInt(),
        0xFF979828.toInt(),
        0xFF46A758.toInt(),
    )
}
