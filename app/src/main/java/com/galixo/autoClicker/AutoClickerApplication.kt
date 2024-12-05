package com.galixo.autoClicker

import android.app.Application
import com.galixo.autoClicker.utils.PreferenceUtils
import com.google.android.material.color.DynamicColors
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class AutoClickerApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        DynamicColors.applyToActivitiesIfAvailable(this)
        PreferenceUtils.init(this)
    }
}
