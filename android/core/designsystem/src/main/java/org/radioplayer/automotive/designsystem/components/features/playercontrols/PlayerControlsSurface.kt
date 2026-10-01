package org.radioplayer.automotive.designsystem.components.features.playercontrols

/**
 * `Surface` variant in the source Figma set - `Standard` sits on an ordinary opaque background
 * (`sys/color/Surface Container`); `OnMedia` sits directly on top of art/imagery (e.g. a blurred
 * station artwork backdrop) and needs a translucent container instead of an opaque one so the
 * backdrop shows through.
 */
enum class PlayerControlsSurface { Standard, OnMedia }