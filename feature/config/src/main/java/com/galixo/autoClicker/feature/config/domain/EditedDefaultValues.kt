package com.galixo.autoClicker.feature.config.domain

import android.content.Context
import com.galixo.autoClicker.feature.config.R
import com.galixo.autoClicker.feature.config.data.getClickPressDurationConfig
import com.galixo.autoClicker.feature.config.data.getClickRepeatCountConfig
import com.galixo.autoClicker.feature.config.data.getClickRepeatDelayConfig
import com.galixo.autoClicker.feature.config.data.getConfigPreferences
import com.galixo.autoClicker.feature.config.data.getRandomization
import com.galixo.autoClicker.feature.config.data.getSwipeDurationConfig
import com.galixo.autoClicker.feature.config.data.getSwipeRepeatCountConfig
import com.galixo.autoClicker.feature.config.data.getSwipeRepeatDelayConfig

fun Context.getDefaultClickName(): String = getString(R.string.default_click_name)

fun Context.getDefaultClickDurationMs(): Long = getConfigPreferences().getClickPressDurationConfig(resources.getInteger(R.integer.default_click_press_duration).toLong())

fun Context.getDefaultClickRepeatCount(): Int = getConfigPreferences().getClickRepeatCountConfig(1)

fun Context.getDefaultClickRepeatDelay(): Long = getConfigPreferences().getClickRepeatDelayConfig(100)

internal fun Context.getDefaultSwipeName(): String = getString(R.string.default_swipe_name)

fun Context.getDefaultSwipeDurationMs(): Long = getConfigPreferences().getSwipeDurationConfig(resources.getInteger(R.integer.default_swipe_duration).toLong())

internal fun Context.getDefaultSwipeRepeatCount(): Int = getConfigPreferences().getSwipeRepeatCountConfig(1)

fun Context.getDefaultSwipeRepeatDelay(): Long = getConfigPreferences().getSwipeRepeatDelayConfig(100)

fun Context.getDefaultRandomize(): Boolean = getConfigPreferences().getRandomization(false)


