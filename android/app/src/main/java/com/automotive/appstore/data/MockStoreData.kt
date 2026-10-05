package com.automotive.appstore.data

import com.automotive.appstore.R

/**
 * Catalog fixture. Mirrors `MOCK_APPS` / `MOCK_CHANNELS` / `MOCK_INSTALLED` and friends in
 * the web app's `lib/store/mock-data.ts`.
 *
 * The `iconColor` values are the sRGB hex equivalents of that file's `oklch()` colors, so the
 * tiles keep the same visual identity. Screenshot drawables replace the web app's PNG assets.
 */
object MockStoreData {

    val MOCK_CHANNELS: List<ChannelDefinition> = listOf(
        ChannelDefinition(
            id = "stable",
            name = "Stable",
            description = "Fully tested. Recommended for everyday driving.",
            stabilityRank = 0,
        ),
        // `demo` and `release-candidate` are what `config.json` actually publishes; the rest are
        // kept for the mock fixtures below, which still reference them.
        ChannelDefinition(
            id = "demo",
            name = "Demo",
            description = "Demo builds for evaluation vehicles.",
            stabilityRank = 1,
        ),
        ChannelDefinition(
            id = "release-candidate",
            name = "Release candidate",
            description = "Almost ready. Only a handful of fixes outstanding.",
            stabilityRank = 2,
        ),
        ChannelDefinition(
            id = "beta",
            name = "Beta",
            description = "Upcoming features, mostly stable. Minor issues possible.",
            stabilityRank = 3,
        ),
        ChannelDefinition(
            id = "develop",
            name = "Develop",
            description = "Nightly builds. Features may change or break.",
            stabilityRank = 4,
        ),
        ChannelDefinition(
            id = "alpha",
            name = "Alpha",
            description = "Early experiments for internal testing.",
            stabilityRank = 5,
            locked = true,
            lockReason = "Only available on enrolled developer vehicles.",
        ),
    )

    /** Builds the release list: a mandatory `stable` entry plus any optional extras. */
    private fun releases(
        stable: Triple<String, String, List<String>>,
        extra: Map<String, Triple<String, String, List<String>>> = emptyMap(),
    ): List<ChannelRelease> = buildList {
        add(ChannelRelease("stable", stable.first, stable.second, stable.third))
        listOf("beta", "develop", "alpha").forEach { channelId ->
            extra[channelId]?.let { (version, date, changelog) ->
                add(ChannelRelease(channelId, version, date, changelog))
            }
        }
    }

    val MOCK_APPS: List<AppListing> = listOf(
        AppListing(
            id = "waypoint",
            name = "Waypoint Maps",
            developer = "Northbound Labs",
            tagline = "Lane-level guidance with live traffic",
            description = "Turn-by-turn navigation with lane guidance, live traffic, and offline " +
                "regions. Routes adapt to your battery level and charging stops.",
            category = CategoryId.NAVIGATION,
            icon = AppIconName.NAVIGATION,
            iconColor = 0xFF0094CE.toInt(),
            sizeMb = 184,
            permissions = listOf(
                PermissionId.LOCATION,
                PermissionId.MICROPHONE,
                PermissionId.VEHICLE_DATA,
                PermissionId.STORAGE,
            ),
            screenshots = listOf(R.drawable.screenshot_navigation, R.drawable.screenshot_utility),
            releases = releases(
                Triple(
                    "5.2.0",
                    "2026-09-22",
                    listOf(
                        "Faster rerouting in dense traffic",
                        "Clearer lane arrows at night",
                        "Fixed offline map sync",
                    ),
                ),
                mapOf(
                    "beta" to Triple(
                        "5.3.0-beta.2",
                        "2026-09-26",
                        listOf("3D junction view", "Speed camera alerts", "Battery-aware ETA"),
                    ),
                    "develop" to Triple(
                        "5.4.0-dev.118",
                        "2026-09-29",
                        listOf("Experimental HUD mirroring", "New routing engine"),
                    ),
                    "alpha" to Triple(
                        "6.0.0-alpha.3",
                        "2026-09-28",
                        listOf("Redesigned map renderer"),
                    ),
                ),
            ),
        ),
        AppListing(
            id = "tidewave",
            name = "Tidewave Music",
            developer = "Tidewave Audio",
            tagline = "Lossless streaming, built for the road",
            description = "Stream millions of songs in lossless quality. Downloads play " +
                "offline, and playlists adapt to the length of your drive.",
            category = CategoryId.MEDIA,
            icon = AppIconName.MUSIC,
            iconColor = 0xFF955BE3.toInt(),
            sizeMb = 96,
            permissions = listOf(
                PermissionId.MICROPHONE,
                PermissionId.STORAGE,
                PermissionId.NOTIFICATIONS,
            ),
            screenshots = listOf(R.drawable.screenshot_media),
            releases = releases(
                Triple(
                    "8.4.1",
                    "2026-09-10",
                    listOf("Gapless playback improvements", "Steering wheel skip fix"),
                ),
                mapOf(
                    "beta" to Triple(
                        "8.5.0-beta.1",
                        "2026-09-24",
                        listOf("Drive-length playlists", "New equalizer presets"),
                    ),
                ),
            ),
        ),
        AppListing(
            id = "castline",
            name = "Castline",
            developer = "Castline Media",
            tagline = "Podcasts that pick up where you left off",
            description = "Follow your favorite shows, auto-download new episodes over " +
                "Wi-Fi, and resume exactly where you stopped when you start the car.",
            category = CategoryId.MEDIA,
            icon = AppIconName.PODCAST,
            iconColor = 0xFFE56636.toInt(),
            sizeMb = 58,
            permissions = listOf(PermissionId.STORAGE, PermissionId.NOTIFICATIONS),
            screenshots = listOf(R.drawable.screenshot_media),
            releases = releases(
                Triple(
                    "3.9.0",
                    "2026-09-18",
                    listOf("Smart speed for spoken audio", "Chapter markers", "Smaller downloads"),
                ),
                mapOf(
                    "beta" to Triple(
                        "4.0.0-beta.4",
                        "2026-09-27",
                        listOf("Transcripts while parked", "Queue redesign"),
                    ),
                ),
            ),
        ),
        AppListing(
            id = "chargegrid",
            name = "ChargeGrid",
            developer = "Gridline Energy",
            tagline = "Find, reserve, and pay for chargers",
            description = "See live charger availability, reserve a stall, and pay " +
                "automatically when you plug in. Filters by connector and speed.",
            category = CategoryId.CHARGING,
            icon = AppIconName.CHARGING,
            iconColor = 0xFF24AE56.toInt(),
            sizeMb = 72,
            permissions = listOf(
                PermissionId.LOCATION,
                PermissionId.VEHICLE_DATA,
                PermissionId.NOTIFICATIONS,
            ),
            screenshots = listOf(R.drawable.screenshot_charging, R.drawable.screenshot_navigation),
            releases = releases(
                Triple(
                    "2.7.3",
                    "2026-09-05",
                    listOf("Plug & Charge support for more networks", "Price per kWh shown on map"),
                ),
                mapOf(
                    "beta" to Triple(
                        "2.8.0-beta.3",
                        "2026-09-25",
                        listOf("Charger queue position", "Reservation reminders"),
                    ),
                    "develop" to Triple(
                        "2.9.0-dev.41",
                        "2026-09-29",
                        listOf("Bidirectional charging controls"),
                    ),
                ),
            ),
        ),
        AppListing(
            id = "parkspot",
            name = "ParkSpot",
            developer = "Curbside Inc.",
            tagline = "Pay for parking without a meter",
            description = "Start, extend, and stop parking sessions from the dashboard. " +
                "Get a reminder before your time runs out.",
            category = CategoryId.UTILITIES,
            icon = AppIconName.PARKING,
            iconColor = 0xFF3280DD.toInt(),
            sizeMb = 34,
            permissions = listOf(PermissionId.LOCATION, PermissionId.NOTIFICATIONS),
            screenshots = listOf(R.drawable.screenshot_utility),
            releases = releases(
                Triple(
                    "1.12.0",
                    "2026-08-30",
                    listOf("Garage entry by plate recognition", "Receipt export"),
                ),
            ),
        ),
        AppListing(
            id = "skyfront",
            name = "Skyfront Weather",
            developer = "Skyfront",
            tagline = "Weather along your route",
            description = "Forecasts for every stop on your route, severe weather alerts, " +
                "and road condition warnings for ice and fog.",
            category = CategoryId.UTILITIES,
            icon = AppIconName.WEATHER,
            iconColor = 0xFF00BEC7.toInt(),
            sizeMb = 41,
            permissions = listOf(PermissionId.LOCATION, PermissionId.NOTIFICATIONS),
            screenshots = listOf(R.drawable.screenshot_utility),
            releases = releases(
                Triple("4.1.2", "2026-09-12", listOf("Route timeline view", "Hail alerts")),
                mapOf(
                    "beta" to Triple(
                        "4.2.0-beta.1",
                        "2026-09-23",
                        listOf("Radar overlay on map"),
                    ),
                ),
            ),
        ),
        AppListing(
            id = "chapter",
            name = "Chapter",
            developer = "Longform Audio",
            tagline = "Audiobooks with drive-aware bookmarks",
            description = "Thousands of audiobooks with automatic bookmarks every time " +
                "you park. Sleep timer and speed control included.",
            category = CategoryId.MEDIA,
            icon = AppIconName.AUDIOBOOK,
            iconColor = 0xFFC17A00.toInt(),
            sizeMb = 63,
            permissions = listOf(PermissionId.STORAGE),
            screenshots = listOf(R.drawable.screenshot_media),
            releases = releases(
                Triple(
                    "6.0.4",
                    "2026-09-01",
                    listOf("Faster library loading", "Bookmark sync fixes"),
                ),
            ),
        ),
        AppListing(
            id = "relay",
            name = "Relay",
            developer = "Relay Messaging",
            tagline = "Messages read aloud, replies by voice",
            description = "Hear incoming messages, reply by voice, and share your ETA with " +
                "one tap. Text is never shown while driving.",
            category = CategoryId.COMMUNICATION,
            icon = AppIconName.MESSAGING,
            iconColor = 0xFF00A381.toInt(),
            sizeMb = 49,
            permissions = listOf(
                PermissionId.MICROPHONE,
                PermissionId.CONTACTS,
                PermissionId.NOTIFICATIONS,
                PermissionId.PHONE,
            ),
            screenshots = listOf(R.drawable.screenshot_utility),
            releases = releases(
                Triple(
                    "11.3.0",
                    "2026-09-20",
                    listOf("Share ETA with a contact", "Better voice reply accuracy"),
                ),
                mapOf(
                    "beta" to Triple(
                        "11.4.0-beta.2",
                        "2026-09-27",
                        listOf("Group conversations", "Quick replies"),
                    ),
                ),
            ),
        ),
        AppListing(
            id = "dayplan",
            name = "Dayplan",
            developer = "Dayplan Co.",
            tagline = "Your calendar, with one-tap directions",
            description = "See your next meeting and navigate to it in one tap. Join calls " +
                "by voice and let attendees know if you are running late.",
            category = CategoryId.COMMUNICATION,
            icon = AppIconName.CALENDAR,
            iconColor = 0xFFDA404E.toInt(),
            sizeMb = 28,
            permissions = listOf(
                PermissionId.CONTACTS,
                PermissionId.LOCATION,
                PermissionId.NOTIFICATIONS,
            ),
            screenshots = listOf(R.drawable.screenshot_utility),
            releases = releases(
                Triple(
                    "2.3.1",
                    "2026-09-08",
                    listOf("Running late message", "Meeting location parsing"),
                ),
            ),
        ),
        AppListing(
            id = "airwave",
            name = "AirWave Radio",
            developer = "AirWave",
            tagline = "40,000 live stations worldwide",
            description = "Live radio from around the world with seamless switching between " +
                "FM and internet streams as you drive.",
            category = CategoryId.MEDIA,
            icon = AppIconName.RADIO,
            iconColor = 0xFFB950B2.toInt(),
            sizeMb = 38,
            permissions = listOf(PermissionId.LOCATION, PermissionId.STORAGE),
            screenshots = listOf(R.drawable.screenshot_media),
            releases = releases(
                Triple(
                    "7.8.0",
                    "2026-09-15",
                    listOf("FM to stream handoff", "Station logos refreshed"),
                ),
            ),
        ),
        AppListing(
            id = "parked-arcade",
            name = "Parked Arcade",
            developer = "Idle Motors Games",
            tagline = "Casual games while you charge",
            description = "A collection of casual games playable with the touchscreen or a " +
                "paired controller. Only available while parked.",
            category = CategoryId.PARKED,
            icon = AppIconName.GAMES,
            iconColor = 0xFF7370FA.toInt(),
            sizeMb = 312,
            permissions = listOf(PermissionId.STORAGE, PermissionId.VEHICLE_DATA),
            screenshots = listOf(R.drawable.screenshot_utility),
            releases = releases(
                Triple("1.4.0", "2026-09-03", listOf("Two new puzzle games", "Controller support")),
                mapOf(
                    "beta" to Triple(
                        "1.5.0-beta.1",
                        "2026-09-21",
                        listOf("Multiplayer with passengers"),
                    ),
                ),
            ),
        ),
        AppListing(
            id = "tollpass",
            name = "TollPass",
            developer = "Freeway Systems",
            tagline = "Automatic tolls, one monthly bill",
            description = "Drive through toll plazas without stopping. See upcoming tolls on " +
                "your route and a monthly summary of charges.",
            category = CategoryId.UTILITIES,
            icon = AppIconName.TOLLS,
            iconColor = 0xFF979828.toInt(),
            sizeMb = 22,
            permissions = listOf(
                PermissionId.LOCATION,
                PermissionId.VEHICLE_DATA,
                PermissionId.NOTIFICATIONS,
            ),
            screenshots = listOf(R.drawable.screenshot_navigation),
            releases = releases(
                Triple(
                    "3.0.2",
                    "2026-09-11",
                    listOf("Toll preview on route", "Monthly statement"),
                ),
            ),
        ),
    )

    val MOCK_INSTALLED: Map<String, InstalledRecord> = mapOf(
        "waypoint" to InstalledRecord("5.1.3"),
        "tidewave" to InstalledRecord("8.4.1"),
        "castline" to InstalledRecord("3.8.2"),
        "chargegrid" to InstalledRecord("2.8.0-beta.2"),
        "skyfront" to InstalledRecord("4.1.2"),
        "relay" to InstalledRecord("11.2.4"),
    )

    val MOCK_SELECTED_CHANNELS: Map<String, ChannelId> = mapOf("chargegrid" to "beta")

    val DEFAULT_SETTINGS = StoreSettings(
        autoUpdate = true,
        wifiOnly = true,
        theme = ThemeMode.NIGHT,
        locale = Locale.EN,
        simulation = CatalogSimulation.NORMAL,
    )

    val INITIAL_STORE_UPDATE = StoreSelfUpdate(
        currentVersion = "3.4.1",
        availableVersion = "3.5.0",
        phase = StoreUpdatePhase.READY,
        progress = 100,
        bannerDismissed = false,
    )

    /** Apps whose next download fails once, to showcase the failed/retry state. */
    val FAIL_ONCE_APP_IDS = setOf("castline")
}
