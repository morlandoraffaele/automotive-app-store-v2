# Automotive App Store — Android

Jetpack Compose port of the Next.js app store at the repository root. Same structure, same
screens, same state machine — rebuilt for Android Automotive with Compose and the
`designsystem` module from `radioplayer-automotive-radio`.

## Module layout

The module layout mirrors the web app's folder structure, so the two are easy to compare:

| Android                          | Web (`app/`, `components/`, `lib/`)  |
|----------------------------------|--------------------------------------|
| `app/…/data/StoreTypes.kt`        | `lib/store/types.ts`                 |
| `app/…/data/StoreSelectors.kt`    | `lib/store/selectors.ts`             |
| `app/…/data/StoreRepository.kt`   | `lib/store/mock-repository.ts`       |
| `app/…/data/remote/*`             | — (live `store/config.json`)         |
| `app/…/data/local/*`              | —                                    |
| `app/…/data/MockStoreData.kt`     | `lib/store/mock-data.ts`             |
| `app/…/data/Translator.kt`        | `lib/i18n.ts`                        |
| `app/…/ui/components/*`           | `components/store/*`                 |
| `app/…/ui/screens/*`              | `app/**/page.tsx`                    |
| `app/…/navigation/*`              | Next.js App Router routes            |

## Installing apps requires the platform key

The store's job is to install other apps, which means `PackageInstaller`, which means
`INSTALL_PACKAGES`. That permission is **`signature|privileged`**, so it is granted only when
**both** hold:

1. the APK is signed with the AOSP **platform** key, and
2. it is installed as a privileged app (`app/Android.bp` → `privileged: true`, plus
   `app/privapp-permissions-com.automotive.appstore.xml`).

A debug-signed APK passes the manifest declaration but the platform refuses it at
`session.commit()`, so **every install fails** — the download succeeds and then nothing happens.
This is the single most common reason the install does not work, and it is not something the
code can work around.

To build an install-capable APK, place two git-ignored files at `android/`:

```
android/keystore/platform.jks     # AOSP platform key, from the platform build
android/keystore.properties      # keystore.password / keystore.key.alias / keystore.key.password
```

When both exist, `app/build.gradle.kts` applies the `platform` signing config to the debug **and**
release variants (as the reference app does) and the APK is signed with the platform key. When
they are absent the build still succeeds — it just falls back to the debug key, and installs will
be refused by the platform.

Verify what you actually shipped with:

```bash
$ANDROID_HOME/build-tools/37.0.0/apksigner verify --print-certs \
  app/build/outputs/apk/debug/app-debug.apk | head -2
```

`CN=Android Debug` means installs will not work; the platform key shows the platform subject.

At runtime `StoreRepository.canInstallPackages()` checks the grant before committing a session and
logs exactly why, so `adb logcat -s StoreRepository` reports the cause instead of a silent failure.

### Why a plain `adb install` cannot work — and what to do instead

`INSTALL_PACKAGES` is granted by `init` at **boot**, and only to an app that is *already* a priv-app
on the system partition at scan time. Pushing an APK with `adb install` puts it in `/data/app`,
where it is an ordinary app: it never receives the permission, no matter how it is signed. Signing
fixes the *signature* half of `signature|privileged`; privileged placement fixes the other half.

So there are two deployment routes:

**A. Priv-app (the reference app's route — required for a real head unit).** Add the module to the
platform build so `Android.bp` (`privileged: true`) installs it under `/product/priv-app`, with
`privapp-permissions-com.automotive.appstore.xml` packaged into `/product/etc/permissions/`.
Gradle's `installDebug` cannot do this; it is a platform-image concern.

**B. Unknown-sources consent (the development route — verified working).** The store declares
`REQUEST_INSTALL_PACKAGES` and, the first time an install is attempted, sends the user to
`Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES`. Once granted, `installRoute()` returns
`USER_CONSENT` and sessions commit normally. This is what makes the store testable on a stock
emulator, where privileged placement is impossible (`/system/priv-app` is read-only and
`adb remount` refuses without an unlocked bootloader).

`INSTALL_PACKAGES` still has priority: a priv-app never sees the consent prompt and never shows a
confirmation dialog, exactly like the reference app. `resolveInstallRoute()` (in
`data/InstallRoute.kt`) encodes that precedence and is unit tested.

Three things the consent route needs that the privileged route does not, all of which cost real
debugging time on Android 14:

1. **A mutable `PendingIntent`.** The platform fills in the confirmation Intent, which an immutable
   one cannot accept — the session just fails. Privileged installs need no confirmation and keep
   `FLAG_IMMUTABLE`.
2. **An explicit `PendingIntent`.** Android 14+ rejects a `FLAG_MUTABLE` PendingIntent wrapping an
   implicit Intent, so the callback Intent must target this package with `setPackage(...)`.
3. **`session.fsync()`** before `commit()`, or a larger APK can be handed over truncated.

On `STATUS_PENDING_USER_ACTION` (-1) the confirmation Intent arrives under the
`android.intent.extra.INTENT` key — note that is *not*
`android.content.pm.extra.INSTALL_INTENT`, and neither constant is public API.

### Checking the state of a device

```bash
adb shell dumpsys package com.automotive.appstore | grep -A6 'install permissions'
```

`INSTALL_PACKAGES: granted=true` means route A is in place. Two tells that it is **not**:

- `codePath=/data/app/...` — an ordinary app, not a priv-app.
- `granted=false` for `INSTALL_PACKAGES` *and* `DELETE_PACKAGES` together — both are
  `signature|privileged`, so both fail as a pair. If only `INSTALL_PACKAGES` were missing, the
  signing would still be the problem; both missing means the app is simply not privileged.

Note that a priv-app also shows up per-user; check the line for the user the app is running under
(this image has both `User 0` and `User 10` — the automotive *Driver* user).

## The `designsystem` module

`core/designsystem` is a **verbatim copy** of
`radioplayer-automotive-radio/core/designsystem` — the 260 Kotlin files and resources are
unmodified, so components can be re-synced by re-copying. Two things had to change, both in
`build.gradle.kts` only:

1. **Build configuration.** Upstream uses the `radio.android.library{,.compose}` convention
   plugins from that repo's `build-logic` plus a `vendor` product-flavor axis
   (`radioplayer` / `radioplayerGAS` / `renault`). None of that applies to a standalone app,
   so the convention plugins are inlined as plain AGP config and the flavor axis is dropped.
   Kotlin, AGP and Compose versions come from `gradle/libs.versions.toml`, pinned to the
   same values upstream uses so the copied sources compile against identical APIs.

2. **`:core:radio-image`.** The design system calls `RadioImage` / `ImageRequest` /
   `ImageSource` / `ImageFormat` / `ImageSize` from radioplayer's image module, which resolves
   images through Coil behind a Hilt `@EntryPoint`. `core/radio-image` re-implements exactly
   that surface — same package names, same signatures — on top of Coil 3 directly, so the
   copied sources need no edits and no dependency on the radio app or Hilt.

Everything else — spacing, shapes, typography, icons, strokes, minimum touch targets, focus
and ripple behaviour — comes from the design system at runtime.

## Adaptive layout

The first version used fixed dp values ported from the web's pixel classes. That does not
survive contact with a real head unit: on a 1128x754 dp automotive display the shell was
oversized, the nav rail clipped its labels, the top bar squeezed the title out entirely, and
the search hint wrapped onto a second line. The content also sat under the status bar and
behind the system dock, because nothing consumed the window insets.

Two mechanisms now handle it:

**1. Window insets.** The activity calls `enableEdgeToEdge()`, so the root shell applies
`Modifier.windowInsetsPadding(WindowInsets.safeContent)`. The nav rail, top bar and every
scrolling screen then share one safe area.

**2. `StoreMetrics`.** The window size class is resolved in `MainActivity` and turned into a
`StoreMetrics` object that every screen reads. Two independent axes drive it:

- **Width** decides how much chrome fits side by side. Narrower than `Expanded` and the rail
  drops its labels, the top bar drops the clock, and the update pill collapses to a count.
- **Height** decides how much chrome fits at all. A short window — wide cabin display, limited
  vertical room — tightens padding, shrinks the rail and top bar, and hides the decorative
  search hint. That is the first thing to go.

**The keyboard must not move these breakpoints.** This is why the size class is resolved through
`rememberStableWindowSizeClass` rather than the Material3 library's own
`calculateWindowSizeClass`. That function feeds raw window bounds into the height/width
breakpoints and has no notion of the IME; its only recomposition trigger is
`LocalConfiguration.current`, and the activity declares `configChanges="...|keyboardHidden|..."`.
So opening the keyboard to type in the search box pushed a configuration change, the bounds came
back keyboard-shrunk, the height class dropped from `Expanded` to `Compact`, and the entire shell
above collapsed — rail labels, clock, search hint gone and the type scale down to 0.82.
`rememberStableWindowSizeClass` adds the current IME inset back onto the reported height, so the
breakpoints describe the physical head unit and stay put while the keyboard is up. Real resizes
(rotation, multi-display, freeform) still move them.

Note that `windowSoftInputMode` is deliberately left at `adjustNothing`. Switching it to
`adjustResize` does not address this: it cannot fix a size class that misreads the bounds, and
because `enableEdgeToEdge()` puts the window in full-bleed mode it also risks double-counting the
IME against `safeContent`'s own `ime` component.

The design system's type scale is scaled by `StoreMetrics.typeScale` inside `StoreTheme`, so
the whole type hierarchy shrinks together on small windows instead of overflowing its
containers. The design system's own tokens (spacing, shapes, minimum touch areas) stay fixed.

**The 76dp floor.** Adaptivity may remove chrome, but it must never shrink a touch target
below the design system's `sizes.minTapArea`. `StoreMetricsTest` asserts this across all eight
width/height combinations, which is how a 72dp rail regression was caught during development.

## Design

`AutomotiveTheme` owns layout and interaction tokens; the app contributes only its palette.

`StoreColors` ports the CSS custom properties from the web app's `app/globals.css` (day and
night blocks) into Compose colors, converting `oklch()` to sRGB. `storePaletteTokens()` maps
those roles onto the design system's `PaletteTokens` tone slots, so **every design-system
component picks up the store palette through `AutomotiveTheme.colorScheme`** with no
per-component overrides and no risk of a component mixing roles from two palettes.

`LocalStoreColors` additionally exposes the raw roles for the few places that need an exact
CSS value (e.g. a 15%-tinted chip background).

## Behaviour notes

- **State.** `StoreRepository` exposes a `StateFlow<StoreSnapshot>` and plays the same role as the
  web app's external store + `useSyncExternalStore`. Unlike the web app it is **not** a simulation:
  the catalogue is fetched live from `https://automotive.radioplayer.org/store/config.json` over
  Retrofit, an app's state comes from `PackageManager`, and installs run through `DownloadManager` +
  `PackageInstaller` — all ported from `radioplayer-automotive-appstore`.
- **Downloads.** The APK is staged by `DownloadManager` into
  `getExternalFilesDir(DIRECTORY_DOWNLOADS)/<package>`, polled once a second, and the download id
  is persisted in `SharedPreferences` so an interrupted install resumes after a process restart.
- **Installs.** `PackageInstaller.SessionParams.MODE_FULL_INSTALL`, streamed from the staged APK,
  committed with a `PendingIntent` carrying `EXTRA_PACKAGE_NAME`; the result broadcast is checked
  for `EXTRA_STATUS`. On `onResume` any still-`INSTALLING` task is reconciled against
  `PackageManager`, so a lost broadcast cannot leave a tile spinning forever.
- **Fields the endpoint does not publish.** `config.json` carries only id, name, description,
  packageName, cls, url, icon and version. `category`, `icon`, `developer` and `tagline` are
  derived deterministically by `CatalogMapper`; `sizeMb`, `permissions` and `screenshots` are left
  empty rather than invented. Published versions are unreliable too — 15 of the 31 entries declare
  `"version": 1` while their APKs are far newer (see the installed-version labelling below).

- **Opening an app.** The detail and Installed screens already rendered an "Open" action, but it was
  inert — it flipped a local flag for 1.5s and launched nothing (the web build is a stub in the same
  way). It now calls `StoreRepository.launch()`.

  The catalogue is almost entirely **headless media apps**: `com.bbc.sounds`, `de.ard.audiothek`,
  `com.android.car.media` and the rest publish no launcher activity at all, only a
  `MediaBrowserService` in `config.json`'s `cls`. So launching has two paths — a normal
  `startActivity` for apps that do have a launcher, and a media-session request for the headless
  ones. `cls` must never be built into an *activity* Intent: that is a service, and it fails with
  `ActivityNotFoundException`.

- **Icons.** `config.json` publishes a real 512×512 publisher icon per app and the UI was ignoring
  it, drawing only the tinted category glyph. Two things were needed:

  1. `AppIconTile` now renders the published `icon` URL via Coil, falling back to the glyph when
     absent or slow.
  2. Coil 3 does **not** put a network fetcher on its default singleton — unlike Coil 2, having
     `coil-network-okhttp` on the classpath is not enough and every request failed with "Unable to
     create a network fetcher". `AppStoreApplication` now builds a loader with an explicit
     `OkHttpNetworkFetcherFactory` plus a disk cache (the URLs are fixed and were otherwise
     re-fetched every launch).

- **Navigation.** A small `StoreNavigator` back stack replaces the App Router, mapping 1:1
  onto the routes (`/`, `/installed`, `/settings`, `/apps/[id]`, `/apps/[id]/channels`).
  It is saveable, so the current route survives process death the way the URL does on the web.
  `StoreDeepLink` handles `com.automotive.appstore://store/app?packageName=…`.
- **Icons.** The web app uses `lucide-react`; each glyph maps to its nearest Material
  equivalent in `StoreIcons`.
- **RTL.** Arabic mirrors the web `document.dir` handling by flipping `LocalLayoutDirection`;
  directional glyphs (back arrow, version arrow) follow automatically.
- **Strings.** `StringKey` mirrors the web dictionary keys one-to-one. English is the
  fallback dictionary, matching the `dict[key] ?? en[key]` lookup in `lib/i18n.ts`.

## Build

```bash
cd android
./gradlew :app:assembleDebug     # APK -> app/build/outputs/apk/debug/
./gradlew :app:testDebugUnitTest # store selector tests
./gradlew :app:installDebug      # onto a connected device/emulator
```

Requires JDK 17+ and an Android SDK with platform 37. `local.properties` must point at
your SDK (`sdk.dir=...`).

`minSdk` is 29 and `compileSdk`/`targetSdk` are 37, matching the design system's own
convention (`build-logic/convention/.../AndroidCompose.kt`).
