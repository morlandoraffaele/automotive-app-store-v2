package com.automotive.appstore

import com.automotive.appstore.ui.components.AdvancedSettingsUnlock
import com.automotive.appstore.ui.components.TapSequence
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * Tests for the hidden advanced-settings gesture.
 *
 * The behaviour that matters is the rolling timeout: 20 taps must unlock the developer sections,
 * but 20 taps spread across a slow session must not, or the sections would effectively always be
 * reachable and hiding them would be pointless.
 */
class TapSequenceTest {
    private val resetAfterMs = AdvancedSettingsUnlock.ADVANCED_TAP_RESET_MS

    @Test
    fun `stays locked below the tap threshold`() {
        val sequence = TapSequence()
        repeat(AdvancedSettingsUnlock.REQUIRED_TAPS - 1) { i ->
            sequence.tap(nowMs = i * 100L)
        }
        assertFalse(sequence.unlocked)
    }

    @Test
    fun `unlocks exactly at the tap threshold`() {
        val sequence = TapSequence()
        repeat(AdvancedSettingsUnlock.REQUIRED_TAPS) { i ->
            sequence.tap(nowMs = i * 100L)
        }
        assertTrue(sequence.unlocked)
        assertEquals(AdvancedSettingsUnlock.REQUIRED_TAPS, sequence.taps)
    }

    @Test
    fun `restarting after the timeout restarts the count`() {
        val sequence = TapSequence()
        repeat(AdvancedSettingsUnlock.REQUIRED_TAPS - 1) { i ->
            sequence.tap(nowMs = i * 100L)
        }
        assertFalse(sequence.unlocked)

        // A long pause expires everything banked so far, so the next tap starts from 1.
        sequence.tap(nowMs = 100_000L)
        assertEquals(1, sequence.taps)
        assertFalse(sequence.unlocked)
    }

    @Test
    fun `slow taps never accumulate to the threshold`() {
        val sequence = TapSequence()
        // 20 taps spaced just beyond the reset window: more than enough total taps, but not
        // consecutive, so nothing should unlock.
        repeat(AdvancedSettingsUnlock.REQUIRED_TAPS) { i ->
            sequence.tap(nowMs = i * (resetAfterMs + 1))
        }
        assertFalse(sequence.unlocked)
        assertEquals(1, sequence.taps)
    }

    @Test
    fun `a tap exactly on the window boundary still counts`() {
        val sequence = TapSequence()
        assertEquals(1, sequence.tap(nowMs = 0L))
        assertEquals(2, sequence.tap(nowMs = resetAfterMs))
        assertFalse(sequence.isExpired(nowMs = resetAfterMs))
    }

    @Test
    fun `expiry is reported once the window elapses`() {
        val sequence = TapSequence()
        sequence.tap(nowMs = 0L)
        assertFalse(sequence.isExpired(nowMs = resetAfterMs))
        assertTrue(sequence.isExpired(nowMs = resetAfterMs + 1))
    }

    @Test
    fun `reset drops the banked taps`() {
        val sequence = TapSequence()
        repeat(AdvancedSettingsUnlock.REQUIRED_TAPS) { i ->
            sequence.tap(nowMs = i * 100L)
        }
        assertTrue(sequence.unlocked)

        sequence.reset()
        assertEquals(0, sequence.taps)
        assertFalse(sequence.unlocked)
    }

    @Test
    fun `an unlock survives going past the reset window`() {
        // Regression: the rolling window used to be applied *after* a completed unlock too, so the
        // developer sections re-hid themselves seconds later — the user would unlock them and then
        // find them gone by the time they scrolled down. The window is only about earning it.
        val sequence = TapSequence()
        repeat(AdvancedSettingsUnlock.REQUIRED_TAPS) { i ->
            sequence.tap(nowMs = i * 100L)
        }
        assertTrue(sequence.unlocked)

        // Well past the window.
        assertTrue(sequence.isExpired(nowMs = 100_000L))
        assertTrue(sequence.unlocked, "unlock must latch once earned")
    }

    @Test
    fun `an incomplete sequence still expires`() {
        // The other half: the window must keep working while the count is still climbing, or the
        // gesture becomes reachable by accident.
        val sequence = TapSequence()
        repeat(AdvancedSettingsUnlock.REQUIRED_TAPS - 1) { i ->
            sequence.tap(nowMs = i * 100L)
        }
        assertTrue(sequence.isExpired(nowMs = 100_000L))
        assertFalse(sequence.unlocked)
    }
}