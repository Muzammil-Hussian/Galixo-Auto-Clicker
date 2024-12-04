package com.galixo.autoClicker.modules.settings.dialog.repeatMode.model

import com.galixo.autoClicker.modules.settings.dialog.repeatMode.enum.RepeatMode

data class RepeatModeState(
    val selectedMode: RepeatMode = RepeatMode.NEVER_STOP,
    val hours: Int = 0,
    val minutes: Int = 0,
    val seconds: Int = 30,
    val repeats: Int = 10
)