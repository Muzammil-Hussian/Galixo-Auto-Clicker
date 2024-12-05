package com.galixo.autoClicker.modules.script.presentation.viewModel

import android.app.NotificationManager
import android.content.ComponentName
import android.content.Context
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.PermissionChecker
import androidx.lifecycle.ViewModel
import com.galixo.autoClicker.AutoClickerService
import com.galixo.autoClicker.AutoClickerService.Companion.getLocalService
import com.galixo.autoClicker.core.common.permissions.PermissionsController
import com.galixo.autoClicker.core.common.permissions.model.PermissionAccessibilityService
import com.galixo.autoClicker.core.common.permissions.model.PermissionIgnoreBatteryOptimization
import com.galixo.autoClicker.core.common.permissions.model.PermissionPostNotification
import com.galixo.autoClicker.core.scenarios.domain.model.Scenario
import com.galixo.autoClicker.localService.ILocalService
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

/** AndroidViewModel for create/delete/list click scenarios from an LifecycleOwner. */
@HiltViewModel
class ScenarioViewModel @Inject constructor(
    @ApplicationContext context: Context,
    private val permissionsController: PermissionsController,
) : ViewModel() {


    /** Callback upon the availability of the [AutoClickerService]. */
    private val serviceConnection: (ILocalService?) -> Unit = { localService ->
        clickerService = localService
    }

    /**
     * Reference on the ILocalService
     * Will be not null only if the Accessibility Service is enabled.
     */
    private var clickerService: ILocalService? = null

    /** The Android notification manager. Initialized only if needed.*/
    private val notificationManager: NotificationManager?

    init {
        getLocalService(serviceConnection)

        notificationManager =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
                context.getSystemService(NotificationManager::class.java)
            else null
    }

    override fun onCleared() {
        getLocalService(null)
        super.onCleared()
    }


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


    fun loadScenario(context: Context, scenario: Scenario): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val foregroundPermission = PermissionChecker.checkSelfPermission(
                context,
                android.Manifest.permission.FOREGROUND_SERVICE
            )
            if (foregroundPermission != PermissionChecker.PERMISSION_GRANTED) return false
        }
        clickerService?.startScenario(scenario)
        return true
    }

    /** Stop the overlay UI and release all associated resources. */
    fun stopScenario() {
        clickerService?.stop()
    }

}