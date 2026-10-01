package org.radioplayer.automotive.designsystem.components.composites.alphascroller

/**
 * One letter in an [AlphaScroller]. [enabled] models a letter that has no content behind it
 * (e.g. no station names start with "Q" in the current filtered list) — still shown, for the
 * spatial/muscle-memory consistency real A-Z indexes rely on (iOS/Android Contacts do the same),
 * but not tappable and skipped over by the step buttons. Callers that would rather omit missing
 * letters entirely can just not include them in the list passed to [AlphaScroller] — both are
 * supported, the component doesn't force either convention.
 */
data class AlphaScrollerItem(
    val letter: String,
    val enabled: Boolean = true,
)
