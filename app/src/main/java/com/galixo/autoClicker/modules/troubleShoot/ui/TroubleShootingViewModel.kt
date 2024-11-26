package com.galixo.autoClicker.modules.troubleShoot.ui

import android.content.ComponentName
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import com.galixo.autoClicker.AutoClickerService
import com.galixo.autoClicker.core.common.permissions.PermissionsController
import com.galixo.autoClicker.core.common.permissions.model.PermissionAccessibilityService
import com.galixo.autoClicker.core.common.permissions.model.PermissionIgnoreBatteryOptimization
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject


@HiltViewModel
class TroubleShootingViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val permissionsController: PermissionsController
) : ViewModel() {


    private val _isAccessibilityGranted = MutableStateFlow(false)
    val isAccessibilityGranted: StateFlow<Boolean> = _isAccessibilityGranted

    private val _isBatteryOptimizationGranted = MutableStateFlow(false)
    val isBatteryOptimizationGranted: StateFlow<Boolean> = _isBatteryOptimizationGranted

    init {
        checkPermissionsStatus()
    }

    fun requestAccessibilityPermission(activity: AppCompatActivity) {
        permissionsController.startPermissionsUiFlow(activity = activity,
            permissions = listOf(
                PermissionAccessibilityService(componentName = ComponentName(
                    activity,
                    AutoClickerService::class.java
                ),
                    isServiceRunning = { AutoClickerService.isServiceStarted() })
            ),
            onAllGranted = { checkPermissionsStatus() })
    }

    fun requestBatteryOptimizationPermission(activity: AppCompatActivity) {
        permissionsController.startPermissionsUiFlow(activity = activity, permissions = listOf(
            PermissionIgnoreBatteryOptimization
        ), onAllGranted = { checkPermissionsStatus() })
    }


    private fun checkPermissionsStatus() {
        _isAccessibilityGranted.value = isAccessibilityPermissionGranted(context)
        _isBatteryOptimizationGranted.value = isBatteryOptimizationPermissionGranted(context)
    }

    private fun isAccessibilityPermissionGranted(context: Context): Boolean {
        val accessibilityPermission = PermissionAccessibilityService(componentName = ComponentName(
            context,
            AutoClickerService::class.java
        ),
            isServiceRunning = { AutoClickerService.isServiceStarted() })
        return accessibilityPermission.checkIfGranted(context)
    }

    private fun isBatteryOptimizationPermissionGranted(context: Context): Boolean {
        val batteryOptimizationPermission = PermissionIgnoreBatteryOptimization
        return batteryOptimizationPermission.checkIfGranted(context)
    }
}