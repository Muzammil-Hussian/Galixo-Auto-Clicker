package com.galixo.autoClicker.modules.permissions.ui

import android.content.ComponentName
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import com.galixo.autoClicker.AutoClickerService
import com.galixo.autoClicker.core.common.permissions.PermissionsController
import com.galixo.autoClicker.core.common.permissions.model.PermissionAccessibilityService
import com.galixo.autoClicker.core.common.permissions.model.PermissionIgnoreBatteryOptimization
import com.galixo.autoClicker.core.common.permissions.model.PermissionPostNotification
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@HiltViewModel
class PermissionViewModel @Inject constructor(private val permissionsController: PermissionsController) : ViewModel() {

    fun startPermissionFlowIfNeeded(activity: AppCompatActivity, onAllGranted: () -> Unit) {
        permissionsController.startPermissionsUiFlow(
            activity = activity,
            permissions = listOf(
                PermissionAccessibilityService(
                    componentName = ComponentName(activity, AutoClickerService::class.java),
                    isServiceRunning = { AutoClickerService.isServiceStarted() },
                ),
                PermissionIgnoreBatteryOptimization,
                PermissionPostNotification
            ),
            onAllGranted = onAllGranted,
        )
    }
}