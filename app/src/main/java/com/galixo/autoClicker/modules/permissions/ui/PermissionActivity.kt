package com.galixo.autoClicker.modules.permissions.ui

import android.content.Intent
import androidx.activity.viewModels
import com.galixo.autoClicker.databinding.ActivityPermissionBinding
import com.galixo.autoClicker.modules.base.activity.BaseActivity
import com.galixo.autoClicker.modules.guide.HowToUseActivity
import com.galixo.autoClicker.utils.FROM_PERMISSION_SCREEN
import com.galixo.autoClicker.utils.PreferenceUtils
import com.galixo.autoClicker.utils.extensions.onBackPressedDispatcher
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PermissionActivity : BaseActivity<ActivityPermissionBinding>(ActivityPermissionBinding::inflate) {

    private val permissionViewModel: PermissionViewModel by viewModels()

    override fun onCreated() {
        binding.apply {
         /*   watchTutorial.setOnClickListener {
                WatchTutorialDialog().show(supportFragmentManager, WatchTutorialDialog.WATCH_TUTORIAL_DIALOG_TAG)
            }*/
            grantPermission.setOnClickListener {
                permissionViewModel.startPermissionFlowIfNeeded((this@PermissionActivity)) {
                    PreferenceUtils.runningAppScreenFirstTime = false
                    val intent = Intent(this@PermissionActivity, HowToUseActivity::class.java)
                    intent.putExtra(FROM_PERMISSION_SCREEN, true)
                    startActivity(intent)
                    finishAffinity()
                }
            }
        }
        onBackPressedDispatcher {
            finishAffinity()
        }
    }
}

