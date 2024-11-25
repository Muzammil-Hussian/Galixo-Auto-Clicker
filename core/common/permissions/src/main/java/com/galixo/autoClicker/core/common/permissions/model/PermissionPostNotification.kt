package com.galixo.autoClicker.core.common.permissions.model

import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build


@SuppressLint("InlinedApi")
data object PermissionPostNotification : Permission.Dangerous(), Permission.Optional, Permission.ForApiRange {

    override val fromApiLvl: Int
        get() = Build.VERSION_CODES.TIRAMISU

    override val permissionString: String
        get() = Manifest.permission.POST_NOTIFICATIONS

    override fun isGranted(context: Context): Boolean =
        context.getSystemService(NotificationManager::class.java).areNotificationsEnabled()
}