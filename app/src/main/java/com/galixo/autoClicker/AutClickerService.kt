package com.galixo.autoClicker

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.GestureDescription
import android.view.accessibility.AccessibilityEvent
import com.galixo.autoClicker.core.common.base.AndroidExecutor


class AutClickerService : AccessibilityService(), AndroidExecutor {

    override suspend fun executeGesture(gestureDescription: GestureDescription) {

    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {}
    override fun onInterrupt() {}
}