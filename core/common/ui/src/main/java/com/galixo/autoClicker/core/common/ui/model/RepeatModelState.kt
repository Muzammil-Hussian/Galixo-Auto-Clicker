package com.galixo.autoClicker.core.common.ui.model

import com.galixo.autoClicker.core.common.ui.enum.RepeatMode

data class RepeatModeState(
    val selectedMode: RepeatMode,
    val hours: Int = 0,
    val minutes: Int = 0,
    val seconds: Int = 30,
    val repeatCount: Int = 10
)