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
| `app/…/data/MockStoreRepository.kt` | `lib/store/mock-repository.ts`     |
| `app/…/data/MockStoreData.kt`     | `lib/store/mock-data.ts`             |
| `app/…/data/Translator.kt`        | `lib/i18n.ts`                        |
| `app/…/ui/components/*`           | `components/store/*`                 |
| `app/…/ui/screens/*`              | `app/**/page.tsx`                    |
| `app/…/navigation/*`              | Next.js App Router routes            |

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

**2. `StoreMetrics`.** `calculateWindowSizeClass()` is resolved once in `MainActivity` and
turned into a `StoreMetrics` object that every screen reads. Two independent axes drive it:

- **Width** decides how much chrome fits side by side. Narrower than `Expanded` and the rail
  drops its labels, the top bar drops the clock, and the update pill collapses to a count.
- **Height** decides how much chrome fits at all. A short window — wide cabin display, limited
  vertical room — tightens padding, shrinks the rail and top bar, and hides the decorative
  search hint. That is the first thing to go.

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

- **State.** `MockStoreRepository` exposes a `StateFlow<StoreSnapshot>` and plays the same
  role as the web app's external store + `useSyncExternalStore`. Timings match the web
  version: 900 ms catalog load, 250 ms download ticks, 1.4 s install phase, and the
  `castline` app still fails once to demonstrate the retry path.
- **Navigation.** A small `StoreNavigator` back stack replaces the App Router, mapping 1:1
  onto the routes (`/`, `/installed`, `/settings`, `/apps/[id]`, `/apps/[id]/channels`).
  It is saveable, so the current route survives process death the way the URL does on the web.
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
