package com.galixo.autoClicker.feature.config.data

import android.content.Context
import android.content.SharedPreferences

/** @return the shared preferences for the default configuration. */
fun Context.getConfigPreferences(): SharedPreferences =
    getSharedPreferences(
        CONFIG_PREFERENCES_NAME,
        Context.MODE_PRIVATE,
    )

/** @return the default duration for a click press. */
internal fun SharedPreferences.getClickPressDurationConfig(default: Long): Long =
    getLong(PREF_LAST_CLICK_PRESS_DURATION, default)

/** Save a new default duration for the click press. */
internal fun SharedPreferences.Editor.putClickPressDurationConfig(durationMs: Long): SharedPreferences.Editor =
    putLong(PREF_LAST_CLICK_PRESS_DURATION, durationMs)

/** @return the default repeat count for a click. */
internal fun SharedPreferences.getClickRepeatCountConfig(default: Int): Int =
    getInt(PREF_LAST_CLICK_REPEAT_COUNT, default)

/** Save a new default repeat count for the clicks. */
internal fun SharedPreferences.Editor.putClickRepeatCountConfig(count: Int): SharedPreferences.Editor =
    putInt(PREF_LAST_CLICK_REPEAT_COUNT, count)

/** @return the default repeat delay for a click. */
internal fun SharedPreferences.getClickRepeatDelayConfig(default: Long): Long =
    getLong(PREF_LAST_CLICK_REPEAT_DELAY, default)

/** Save a new default repeat delay for the clicks. */
fun SharedPreferences.Editor.putClickRepeatDelayConfig(durationMs: Long): SharedPreferences.Editor =
    putLong(PREF_LAST_CLICK_REPEAT_DELAY, durationMs)


/** @return the default duration for a swipe. */
internal fun SharedPreferences.getSwipeDurationConfig(default: Long): Long =
    getLong(PREF_LAST_SWIPE_DURATION, default)

/** Save a new default duration for the swipes. */
fun SharedPreferences.Editor.putSwipeDurationConfig(durationMs: Long): SharedPreferences.Editor =
    putLong(PREF_LAST_SWIPE_DURATION, durationMs)

/** @return the default repeat count for a swipe. */
internal fun SharedPreferences.getSwipeRepeatCountConfig(default: Int): Int =
    getInt(PREF_LAST_SWIPE_REPEAT_COUNT, default)

/** Save a new default repeat count for the swipes. */
internal fun SharedPreferences.Editor.putSwipeRepeatCountConfig(count: Int): SharedPreferences.Editor =
    putInt(PREF_LAST_SWIPE_REPEAT_COUNT, count)

/** @return the default repeat delay for a swipe. */
internal fun SharedPreferences.getSwipeRepeatDelayConfig(default: Long): Long =
    getLong(PREF_LAST_SWIPE_REPEAT_DELAY, default)

/** Save a new default repeat delay for the swipes. */
fun SharedPreferences.Editor.putSwipeRepeatDelayConfig(durationMs: Long): SharedPreferences.Editor =
    putLong(PREF_LAST_SWIPE_REPEAT_DELAY, durationMs)


/** @return the default randomize for anti-detection. */
internal fun SharedPreferences.getRandomization(default: Boolean): Boolean =
    getBoolean(PREF_RANDOMIZATION, default)

/** Save a new default randomization for anti detection. */
fun SharedPreferences.Editor.putRandomization(randomize: Boolean): SharedPreferences.Editor =
    putBoolean(PREF_RANDOMIZATION, randomize)


/** Name of the preference file. */
private const val CONFIG_PREFERENCES_NAME = "ConfigPreferences"

/** User last click press duration key in the SharedPreferences. */
private const val PREF_LAST_CLICK_PRESS_DURATION = "Last_Click_Press_Duration"

/** User last click repeat count key in the SharedPreferences. */
private const val PREF_LAST_CLICK_REPEAT_COUNT = "Last_Click_Repeat_Count"

/** User last click repeat delay key in the SharedPreferences. */
private const val PREF_LAST_CLICK_REPEAT_DELAY = "Last_Click_Repeat_Delay"

/** User last swipe press duration key in the SharedPreferences. */
private const val PREF_LAST_SWIPE_DURATION = "Last_Swipe_Duration"

/** User last swipe repeat count key in the SharedPreferences. */
private const val PREF_LAST_SWIPE_REPEAT_COUNT = "Last_Swipe_Repeat_Count"

/** User last swipe repeat delay key in the SharedPreferences. */
private const val PREF_LAST_SWIPE_REPEAT_DELAY = "Last_Swipe_Repeat_Delay"

private const val PREF_RANDOMIZATION = "pref_randomization"