package com.galixo.autoClicker.utils

import android.content.Context
import android.content.SharedPreferences

object PreferenceUtils {
    private lateinit var sharedPreferences: SharedPreferences

    fun init(context: Context) {
        sharedPreferences = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE)
    }

    var runningAppScreenFirstTime: Boolean
        get() = sharedPreferences.getBoolean(RUNNING_APP_FIRST_TIME, true)
        set(value) {
            sharedPreferences.edit().apply {
                putBoolean(RUNNING_APP_FIRST_TIME, value)
                apply()
            }
        }
    var selectedLanguage: String
        get() = sharedPreferences.getString(SELECTED_LANGUAGE, "English").toString()
        set(value) {
            sharedPreferences.edit().apply {
                putString(SELECTED_LANGUAGE, value)
                apply()
            }
        }

}

const val PREFERENCE_NAME = "AutoClickerPreferences"
const val RUNNING_APP_FIRST_TIME = "running_app_first_time"
const val SELECTED_LANGUAGE = "selected_language"



