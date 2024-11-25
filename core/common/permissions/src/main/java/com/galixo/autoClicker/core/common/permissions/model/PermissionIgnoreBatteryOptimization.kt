package com.galixo.autoClicker.core.common.permissions.model

import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.PowerManager
import android.provider.Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment

@SuppressLint("BatteryLife")
data object PermissionIgnoreBatteryOptimization : Permission.Dangerous(), Permission.Optional {

    private var batteryOptimizationLauncher: ActivityResultLauncher<Intent>? = null

    fun initBatteryOptimizationLauncher(fragment: Fragment, onResult: (isGranted: Boolean) -> Unit) {
        batteryOptimizationLauncher = fragment.registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            onResult(isGranted(fragment.requireContext()))
        }
    }

    override fun isGranted(context: Context): Boolean {
        val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager
        return powerManager.isIgnoringBatteryOptimizations(context.packageName)
    }

    override val permissionString: String
        get() = ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS

    override fun onStartRequestFlow(context: Context): Boolean {
        val intent = Intent(ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS, Uri.parse("package:${context.packageName}"))

        return try {
            batteryOptimizationLauncher?.launch(intent) ?: run { context.startActivity(intent) }
            true
        } catch (ex: ActivityNotFoundException) {
            Log.e(TAG, "Can't open battery optimization settings.")
            false
        }
    }
}
