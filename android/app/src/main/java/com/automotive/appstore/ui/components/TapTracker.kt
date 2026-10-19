package com.automotive.appstore.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import kotlinx.coroutines.delay

/**
 * Hidden-gesture counter shared by the advanced settings sections.
 *
 * Display, Language and Demo are developer affordances that clutter the shipping UI, but they
 * still need to be reachable on a debug build and on device. The agreed gesture is 20 taps on the
 * store version row.
 *
 * The count is deliberately **in-memory only**: `remember`-backed state dies with the process, so
 * restarting the app re-hides the sections. [ADVANCED_TAP_RESET_MS] bounds the sequence in time as
 * well, so 20 taps spread over five minutes does not count — otherwise the gesture becomes
 * reachable by accident, which is the opposite of what a hidden gesture is for.
 */
object AdvancedSettingsUnlock {
    /** Taps required on the version row to reveal the advanced sections. */
    const val REQUIRED_TAPS: Int = 20

    /** A gap longer than this restarts the sequence. Recommended value agreed with the requester. */
    const val ADVANCED_TAP_RESET_MS: Long = 3_000L

    /**
     * Remaining taps at which the countdown popup starts, like Android's "You are now N steps away
     * from being a developer". Earlier taps stay silent so the gesture is not advertised.
     */
    const val COUNTDOWN_FROM: Int = 5
}

/** What a tap on the version row should tell the user, mirroring Android's build-number toasts. */
sealed interface TapFeedback {
    /** Too early in the sequence to say anything. */
    data object None : TapFeedback

    /** [remaining] more taps unlock developer mode. */
    data class StepsAway(val remaining: Int) : TapFeedback

    /** This tap unlocked developer mode. */
    data object Unlocked : TapFeedback

    /** Developer mode was already on. */
    data object AlreadyUnlocked : TapFeedback
}

/**
 * Pure state machine for the hidden advanced-settings gesture.
 *
 * Kept separate from the Compose wrapper so it can be unit-tested with an injected clock instead of
 * needing a real 3-second wait.
 *
 * @param required taps needed to unlock.
 * @param resetAfterMs a gap longer than this restarts the sequence.
 */
class TapSequence(
    private val required: Int = AdvancedSettingsUnlock.REQUIRED_TAPS,
    private val resetAfterMs: Long = AdvancedSettingsUnlock.ADVANCED_TAP_RESET_MS,
) {
    /** Taps currently banked. */
    var taps: Int = 0
        private set

    private var lastTapAt: Long = Long.MIN_VALUE

    /**
     * Whether the gesture has been completed.
     *
     * Latched rather than derived from [taps], because the rolling window is only about *earning*
     * the unlock — it stops 20 unhurried taps from counting. It must not undo a completed unlock:
     * the sections have to stay put long enough to be read and used. Deriving this as
     * `taps >= required` meant the idle timer re-locked the developer sections three seconds after
     * every successful unlock, so they vanished while the user was scrolling down to them.
     *
     * Re-hiding on restart is still the default, because the state is in-memory only.
     */
    var unlocked: Boolean = false
        private set

    /**
     * Records a tap at [nowMs].
     *
     * The first tap ever always starts at 1; afterwards a tap is only consecutive if it arrives
     * within [resetAfterMs] of the previous one, so an unhurried sequence of 20 taps spread over
     * minutes never unlocks anything.
     */
    fun tap(nowMs: Long): Int {
        val consecutive = lastTapAt != Long.MIN_VALUE && nowMs - lastTapAt <= resetAfterMs
        taps = if (consecutive) taps + 1 else 1
        lastTapAt = nowMs
        if (taps >= required) unlocked = true
        return taps
    }

    /**
     * Records a tap at [nowMs] and says what the user should be told about it.
     *
     * @param countdownFrom remaining taps at which [TapFeedback.StepsAway] starts being reported.
     */
    fun register(
        nowMs: Long,
        countdownFrom: Int = AdvancedSettingsUnlock.COUNTDOWN_FROM,
    ): TapFeedback {
        if (unlocked) return TapFeedback.AlreadyUnlocked
        tap(nowMs)
        if (unlocked) return TapFeedback.Unlocked
        val remaining = required - taps
        return if (remaining <= countdownFrom) TapFeedback.StepsAway(remaining) else TapFeedback.None
    }

    /** True when [nowMs] is past the reset window, so the banked taps no longer count. */
    fun isExpired(nowMs: Long): Boolean =
        taps > 0 && lastTapAt != Long.MIN_VALUE && nowMs - lastTapAt > resetAfterMs

    /**
     * Drops the banked taps and re-locks.
     *
     * Only called while still counting — once [unlocked] it must not be called, which is what keeps
     * a completed unlock latched. Turning developer mode off on purpose goes through [lock].
     */
    fun reset() {
        taps = 0
        unlocked = false
    }

    /**
     * Turns developer mode off, so the full gesture is needed again to turn it back on.
     *
     * Also forgets the last tap, so taps made while unlocked do not count towards the next unlock.
     */
    fun lock() {
        reset()
        lastTapAt = Long.MIN_VALUE
    }
}

/**
 * Tracks consecutive taps for the advanced-settings gesture.
 *
 * [TapTracker.unlocked] flips true once [required] taps land inside the rolling window. The caller
 * wires [TapTracker.registerTap] to its gesture and reads `unlocked` to decide what to show; no
 * other state has to be threaded through the screen. [TapTracker.lock] turns it back off.
 */
class TapTracker internal constructor(
    val taps: Int,
    val unlocked: Boolean,
    private val register: () -> Unit,
    private val relock: () -> Unit,
) {
    /** Records one tap. Safe to call from any click handler. */
    fun registerTap() = register()

    /** Turns developer mode off; the full gesture turns it back on. */
    fun lock() = relock()
}

/**
 * Remembers a [TapTracker] for the hidden advanced-settings gesture.
 *
 * @param required taps needed to unlock.
 * @param resetAfterMs a gap longer than this restarts the sequence.
 * @param onFeedback told what each tap means, to show Android's build-number style popup.
 */
@Composable
fun rememberTapTracker(
    required: Int = AdvancedSettingsUnlock.REQUIRED_TAPS,
    resetAfterMs: Long = AdvancedSettingsUnlock.ADVANCED_TAP_RESET_MS,
    onFeedback: (TapFeedback) -> Unit = {},
): TapTracker {
    val sequence = remember(required, resetAfterMs) { TapSequence(required, resetAfterMs) }
    var taps by remember { mutableIntStateOf(0) }
    // Mirrors sequence.unlocked as Compose state, so locking recomposes even when `taps` is
    // already 0.
    var unlocked by remember { mutableStateOf(false) }
    val currentOnFeedback by rememberUpdatedState(onFeedback)

    val register: () -> Unit = remember(sequence) {
        {
            val feedback = sequence.register(System.currentTimeMillis())
            taps = sequence.taps
            unlocked = sequence.unlocked
            currentOnFeedback(feedback)
        }
    }
    val relock: () -> Unit = remember(sequence) {
        {
            sequence.lock()
            taps = 0
            unlocked = false
        }
    }

    // Idle fallback: expires the sequence even if no further tap ever arrives. Keying on `taps`
    // restarts the timer on every tap, so the window is measured from the latest one.
    //
    // Guarded on `!sequence.unlocked`: the window exists only to stop unhurried taps from *earning*
    // the unlock. Firing it after a completed unlock re-hid the developer sections seconds later,
    // which is what made them appear to vanish on the way down the screen.
    LaunchedEffect(taps) {
        if (taps > 0 && !sequence.unlocked) {
            delay(resetAfterMs)
            sequence.reset()
            taps = 0
        }
    }

    return TapTracker(
        taps = taps,
        unlocked = unlocked,
        register = register,
        relock = relock,
    )
}