package com.galixo.autoClicker.modules.activities

import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.galixo.autoClicker.databinding.ActivityStartingBinding
import com.galixo.autoClicker.modules.base.activity.BaseActivity
import com.galixo.autoClicker.modules.language.presentation.inAppLanguage.ui.LanguageActivity
import com.galixo.autoClicker.utils.PreferenceUtils
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class StartingActivity : BaseActivity<ActivityStartingBinding>(ActivityStartingBinding::inflate) {

    override fun onCreated() {}

    val handler by lazy { Handler(Looper.getMainLooper()) }
    private var progressStatus = 0
    private val updateInterval by lazy { 10L }

    private val runnable: Runnable = object : Runnable {
        override fun run() {
            if (progressStatus < 100) {
                try {
                    progressStatus++
                    handler.postDelayed(this, updateInterval)
                } catch (_: Exception) {
                }
            } else {
                Log.i(TAG, "run: firstTime: ${PreferenceUtils.runningAppScreenFirstTime}")
                if (PreferenceUtils.runningAppScreenFirstTime) navigateToLanguageActivity()
                else navigateToMain()
            }
        }
    }


    private fun navigateToLanguageActivity() {
        val intent = Intent(this, LanguageActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun navigateToMain() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun onStart() {
        super.onStart()
        handler.post(runnable)

    }

    override fun onStop() {
        super.onStop()
        removeRunnableAndResetProgress()
    }

    private fun removeRunnableAndResetProgress() {
        handler.removeCallbacks(runnable)
        progressStatus = 0
    }
}

private const val TAG = "StartingActivity"