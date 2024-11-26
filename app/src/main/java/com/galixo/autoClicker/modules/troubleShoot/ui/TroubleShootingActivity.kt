package com.galixo.autoClicker.modules.troubleShoot.ui

import android.content.res.ColorStateList
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.galixo.autoClicker.R
import com.galixo.autoClicker.core.common.base.extensions.beVisibleIf
import com.galixo.autoClicker.databinding.ActivityTroubleShootingBinding
import com.galixo.autoClicker.databinding.CardTroubleShootingBinding
import com.galixo.autoClicker.modules.base.activity.BaseActivity
import com.galixo.autoClicker.utils.extensions.onBackPressedDispatcher
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class TroubleShootingActivity : BaseActivity<ActivityTroubleShootingBinding>(ActivityTroubleShootingBinding::inflate) {

    private val viewModel by viewModels<TroubleShootingViewModel>()

    override fun onCreated() {
        observePermissionStatus()
        onClickListener()
    }

    private fun observePermissionStatus() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.isAccessibilityGranted.collect { isGranted ->
                        updatePermissionUI(binding.accessibilityCard, isGranted, getString(R.string.accessibility_permission))
                    }
                }

                launch {
                    viewModel.isBatteryOptimizationGranted.collect { isGranted ->
                        updatePermissionUI(binding.batteryOptimizationCard, isGranted, getString(R.string.battery_optimization))
                    }
                }
            }
        }
    }

    private fun onClickListener() {
        with(binding) {
            accessibilityCard.allowPermission.setOnClickListener { viewModel.requestAccessibilityPermission(this@TroubleShootingActivity) }

            batteryOptimizationCard.allowPermission.setOnClickListener { viewModel.requestBatteryOptimizationPermission(this@TroubleShootingActivity) }

            toolbar.setNavigationOnClickListener { finish() }

            onBackPressedDispatcher { finish() }
        }
    }

    private fun updatePermissionUI(card: CardTroubleShootingBinding, isGranted: Boolean, title: String) {
        with(card) {
            this.title.text = title
            permissionStatus.apply {

                text =
                    if (isGranted) context.getString(R.string.permission_granted)
                    else context.getString(R.string.permission_missing)

                chipBackgroundColor =
                    if (isGranted) ColorStateList.valueOf(ContextCompat.getColor(context, R.color.permission_grant_background_color))
                    else ColorStateList.valueOf(ContextCompat.getColor(context, R.color.permission_not_granted_background_color))

                setTextColor(
                    if (isGranted) getColor(R.color.permission_grant_text_color)
                    else getColor(
                        R.color.permission_not_granted_text_color
                    )
                )
            }
            allowPermission.beVisibleIf(!isGranted)
        }
    }
}