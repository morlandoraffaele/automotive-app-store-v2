import type {
  AppListing,
  ChannelDefinition,
  ChannelRelease,
  InstalledRecord,
  StoreSelfUpdate,
  StoreSettings,
} from './types'

export const MOCK_CHANNELS: ChannelDefinition[] = [
  {
    id: 'stable',
    name: 'Stable',
    description: 'Fully tested. Recommended for everyday driving.',
    stabilityRank: 0,
  },
  {
    id: 'beta',
    name: 'Beta',
    description: 'Upcoming features, mostly stable. Minor issues possible.',
    stabilityRank: 1,
  },
  {
    id: 'develop',
    name: 'Develop',
    description: 'Nightly builds. Features may change or break.',
    stabilityRank: 2,
  },
  {
    id: 'alpha',
    name: 'Alpha',
    description: 'Early experiments for internal testing.',
    stabilityRank: 3,
    locked: true,
    lockReason: 'Only available on enrolled developer vehicles.',
  },
]

function releases(
  stable: [string, string, string[]],
  extra: Partial<Record<'beta' | 'develop' | 'alpha', [string, string, string[]]>> = {},
): ChannelRelease[] {
  const list: ChannelRelease[] = [
    { channelId: 'stable', version: stable[0], releaseDate: stable[1], changelog: stable[2] },
  ]
  for (const channelId of ['beta', 'develop', 'alpha'] as const) {
    const entry = extra[channelId]
    if (entry) list.push({ channelId, version: entry[0], releaseDate: entry[1], changelog: entry[2] })
  }
  return list
}

export const MOCK_APPS: AppListing[] = [
  {
    id: 'waypoint',
    name: 'Waypoint Maps',
    developer: 'Northbound Labs',
    tagline: 'Lane-level guidance with live traffic',
    description:
      'Turn-by-turn navigation with lane guidance, live traffic, and offline regions. Routes adapt to your battery level and charging stops.',
    category: 'navigation',
    icon: 'navigation',
    iconColor: 'oklch(0.62 0.15 230)',
    sizeMb: 184,
    permissions: ['location', 'microphone', 'vehicle-data', 'storage'],
    screenshots: ['/screenshots/navigation.png', '/screenshots/utility.png'],
    releases: releases(
      ['5.2.0', '2026-09-22', ['Faster rerouting in dense traffic', 'Clearer lane arrows at night', 'Fixed offline map sync']],
      {
        beta: ['5.3.0-beta.2', '2026-09-26', ['3D junction view', 'Speed camera alerts', 'Battery-aware ETA']],
        develop: ['5.4.0-dev.118', '2026-09-29', ['Experimental HUD mirroring', 'New routing engine']],
        alpha: ['6.0.0-alpha.3', '2026-09-28', ['Redesigned map renderer']],
      },
    ),
  },
  {
    id: 'tidewave',
    name: 'Tidewave Music',
    developer: 'Tidewave Audio',
    tagline: 'Lossless streaming, built for the road',
    description:
      'Stream millions of songs in lossless quality. Downloads play offline, and playlists adapt to the length of your drive.',
    category: 'media',
    icon: 'music',
    iconColor: 'oklch(0.6 0.2 300)',
    sizeMb: 96,
    permissions: ['microphone', 'storage', 'notifications'],
    screenshots: ['/screenshots/media.png'],
    releases: releases(['8.4.1', '2026-09-10', ['Gapless playback improvements', 'Steering wheel skip fix']], {
      beta: ['8.5.0-beta.1', '2026-09-24', ['Drive-length playlists', 'New equalizer presets']],
    }),
  },
  {
    id: 'castline',
    name: 'Castline',
    developer: 'Castline Media',
    tagline: 'Podcasts that pick up where you left off',
    description:
      'Follow your favorite shows, auto-download new episodes over Wi-Fi, and resume exactly where you stopped when you start the car.',
    category: 'media',
    icon: 'podcast',
    iconColor: 'oklch(0.66 0.17 40)',
    sizeMb: 58,
    permissions: ['storage', 'notifications'],
    screenshots: ['/screenshots/media.png'],
    releases: releases(['3.9.0', '2026-09-18', ['Smart speed for spoken audio', 'Chapter markers', 'Smaller downloads']], {
      beta: ['4.0.0-beta.4', '2026-09-27', ['Transcripts while parked', 'Queue redesign']],
    }),
  },
  {
    id: 'chargegrid',
    name: 'ChargeGrid',
    developer: 'Gridline Energy',
    tagline: 'Find, reserve, and pay for chargers',
    description:
      'See live charger availability, reserve a stall, and pay automatically when you plug in. Filters by connector and speed.',
    category: 'charging',
    icon: 'charging',
    iconColor: 'oklch(0.66 0.17 150)',
    sizeMb: 72,
    permissions: ['location', 'vehicle-data', 'notifications'],
    screenshots: ['/screenshots/charging.png', '/screenshots/navigation.png'],
    releases: releases(['2.7.3', '2026-09-05', ['Plug & Charge support for more networks', 'Price per kWh shown on map']], {
      beta: ['2.8.0-beta.3', '2026-09-25', ['Charger queue position', 'Reservation reminders']],
      develop: ['2.9.0-dev.41', '2026-09-29', ['Bidirectional charging controls']],
    }),
  },
  {
    id: 'parkspot',
    name: 'ParkSpot',
    developer: 'Curbside Inc.',
    tagline: 'Pay for parking without a meter',
    description:
      'Start, extend, and stop parking sessions from the dashboard. Get a reminder before your time runs out.',
    category: 'utilities',
    icon: 'parking',
    iconColor: 'oklch(0.6 0.16 255)',
    sizeMb: 34,
    permissions: ['location', 'notifications'],
    screenshots: ['/screenshots/utility.png'],
    releases: releases(['1.12.0', '2026-08-30', ['Garage entry by plate recognition', 'Receipt export']]),
  },
  {
    id: 'skyfront',
    name: 'Skyfront Weather',
    developer: 'Skyfront',
    tagline: 'Weather along your route',
    description:
      'Forecasts for every stop on your route, severe weather alerts, and road condition warnings for ice and fog.',
    category: 'utilities',
    icon: 'weather',
    iconColor: 'oklch(0.72 0.14 200)',
    sizeMb: 41,
    permissions: ['location', 'notifications'],
    screenshots: ['/screenshots/utility.png'],
    releases: releases(['4.1.2', '2026-09-12', ['Route timeline view', 'Hail alerts']], {
      beta: ['4.2.0-beta.1', '2026-09-23', ['Radar overlay on map']],
    }),
  },
  {
    id: 'chapter',
    name: 'Chapter',
    developer: 'Longform Audio',
    tagline: 'Audiobooks with drive-aware bookmarks',
    description:
      'Thousands of audiobooks with automatic bookmarks every time you park. Sleep timer and speed control included.',
    category: 'media',
    icon: 'audiobook',
    iconColor: 'oklch(0.64 0.14 70)',
    sizeMb: 63,
    permissions: ['storage'],
    screenshots: ['/screenshots/media.png'],
    releases: releases(['6.0.4', '2026-09-01', ['Faster library loading', 'Bookmark sync fixes']]),
  },
  {
    id: 'relay',
    name: 'Relay',
    developer: 'Relay Messaging',
    tagline: 'Messages read aloud, replies by voice',
    description:
      'Hear incoming messages, reply by voice, and share your ETA with one tap. Text is never shown while driving.',
    category: 'communication',
    icon: 'messaging',
    iconColor: 'oklch(0.62 0.16 175)',
    sizeMb: 49,
    permissions: ['microphone', 'contacts', 'notifications', 'phone'],
    screenshots: ['/screenshots/utility.png'],
    releases: releases(['11.3.0', '2026-09-20', ['Share ETA with a contact', 'Better voice reply accuracy']], {
      beta: ['11.4.0-beta.2', '2026-09-27', ['Group conversations', 'Quick replies']],
    }),
  },
  {
    id: 'dayplan',
    name: 'Dayplan',
    developer: 'Dayplan Co.',
    tagline: 'Your calendar, with one-tap directions',
    description:
      'See your next meeting and navigate to it in one tap. Join calls by voice and let attendees know if you are running late.',
    category: 'communication',
    icon: 'calendar',
    iconColor: 'oklch(0.6 0.19 20)',
    sizeMb: 28,
    permissions: ['contacts', 'location', 'notifications'],
    screenshots: ['/screenshots/utility.png'],
    releases: releases(['2.3.1', '2026-09-08', ['Running late message', 'Meeting location parsing']]),
  },
  {
    id: 'airwave',
    name: 'AirWave Radio',
    developer: 'AirWave',
    tagline: '40,000 live stations worldwide',
    description:
      'Live radio from around the world with seamless switching between FM and internet streams as you drive.',
    category: 'media',
    icon: 'radio',
    iconColor: 'oklch(0.6 0.18 330)',
    sizeMb: 38,
    permissions: ['location', 'storage'],
    screenshots: ['/screenshots/media.png'],
    releases: releases(['7.8.0', '2026-09-15', ['FM to stream handoff', 'Station logos refreshed']]),
  },
  {
    id: 'parked-arcade',
    name: 'Parked Arcade',
    developer: 'Idle Motors Games',
    tagline: 'Casual games while you charge',
    description:
      'A collection of casual games playable with the touchscreen or a paired controller. Only available while parked.',
    category: 'parked',
    icon: 'games',
    iconColor: 'oklch(0.62 0.2 280)',
    sizeMb: 312,
    permissions: ['storage', 'vehicle-data'],
    screenshots: ['/screenshots/utility.png'],
    releases: releases(['1.4.0', '2026-09-03', ['Two new puzzle games', 'Controller support']], {
      beta: ['1.5.0-beta.1', '2026-09-21', ['Multiplayer with passengers']],
    }),
  },
  {
    id: 'tollpass',
    name: 'TollPass',
    developer: 'Freeway Systems',
    tagline: 'Automatic tolls, one monthly bill',
    description:
      'Drive through toll plazas without stopping. See upcoming tolls on your route and a monthly summary of charges.',
    category: 'utilities',
    icon: 'tolls',
    iconColor: 'oklch(0.66 0.13 110)',
    sizeMb: 22,
    permissions: ['location', 'vehicle-data', 'notifications'],
    screenshots: ['/screenshots/navigation.png'],
    releases: releases(['3.0.2', '2026-09-11', ['Toll preview on route', 'Monthly statement']]),
  },
]

export const MOCK_INSTALLED: Record<string, InstalledRecord> = {
  waypoint: { version: '5.1.3' },
  tidewave: { version: '8.4.1' },
  castline: { version: '3.8.2' },
  chargegrid: { version: '2.8.0-beta.2' },
  skyfront: { version: '4.1.2' },
  relay: { version: '11.2.4' },
}

export const MOCK_SELECTED_CHANNELS: Record<string, string> = {
  chargegrid: 'beta',
}

export const DEFAULT_SETTINGS: StoreSettings = {
  autoUpdate: true,
  wifiOnly: true,
  theme: 'night',
  locale: 'en',
  simulation: 'normal',
}

export const INITIAL_STORE_UPDATE: StoreSelfUpdate = {
  currentVersion: '3.4.1',
  availableVersion: '3.5.0',
  phase: 'ready',
  progress: 100,
  bannerDismissed: false,
}

/** Apps whose next download fails once, to showcase the failed/retry state. */
export const FAIL_ONCE_APP_IDS = ['castline']
