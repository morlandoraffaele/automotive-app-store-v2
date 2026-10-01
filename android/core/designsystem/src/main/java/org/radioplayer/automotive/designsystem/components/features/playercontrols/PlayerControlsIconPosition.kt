package org.radioplayer.automotive.designsystem.components.features.playercontrols

/**
 * `Icon` variant in the source Figma set - whether the caller-supplied [artwork] (when present)
 * sits before or after [icon]. Named to match this project's existing `ButtonFilledIconPosition`/
 * `ButtonOutlineIconPosition`/etc. convention rather than repeat the Figma property's own naming
 * (`Trail`/`Lead`) verbatim.
 */
enum class PlayerControlsIconPosition { Leading, Trailing }