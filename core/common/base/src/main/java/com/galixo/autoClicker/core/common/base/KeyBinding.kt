package com.galixo.autoClicker.core.common.base

import android.view.KeyEvent

/** Physical key that will stops the running scenario or element try. */
fun KeyEvent.isStopScenarioKey(): Boolean =
    keyCode == KeyEvent.KEYCODE_VOLUME_DOWN