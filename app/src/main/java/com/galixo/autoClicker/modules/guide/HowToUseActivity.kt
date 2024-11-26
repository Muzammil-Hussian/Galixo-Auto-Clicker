package com.galixo.autoClicker.modules.guide

import android.content.Intent
import android.view.MotionEvent
import com.galixo.autoClicker.R
import com.galixo.autoClicker.databinding.ActivityGuideBinding
import com.galixo.autoClicker.modules.activities.MainActivity
import com.galixo.autoClicker.modules.base.activity.BaseActivity
import com.galixo.autoClicker.utils.FROM_PERMISSION_SCREEN
import com.galixo.autoClicker.utils.extensions.onBackPressedDispatcher
import timber.log.Timber

class HowToUseActivity : BaseActivity<ActivityGuideBinding>(ActivityGuideBinding::inflate) {

    private val fromPermissionActivity by lazy {
        intent.getBooleanExtra(
            FROM_PERMISSION_SCREEN,
            false
        )
    }

    override fun onCreated() {
        Timber.d("fromPermissionActivity: $fromPermissionActivity")

        with(binding) {
            intervalHelperText.text = getString(R.string.interval_desc_2, "40ms")
            title.apply {
                setOnTouchListener { v, event ->
                    if (event.action == MotionEvent.ACTION_UP) {
                        val drawableEnd = this.compoundDrawablesRelative[2]
                        if (drawableEnd != null) {
                            val drawableWidth = drawableEnd.bounds.width()
                            val rightBound = this.width - this.paddingEnd

                            if (event.rawX >= rightBound - drawableWidth) {
                                v.performClick()
                                return@setOnTouchListener true
                            }
                        }
                    }
                    false
                }
                this.setOnClickListener {
                    navigateToMainActivity()
                }
            }
        }
        onBackPressedDispatcher { navigateToMainActivity() }
    }

    private fun navigateToMainActivity() {
        if (fromPermissionActivity) {
            val intent = Intent(this@HowToUseActivity, MainActivity::class.java)
            startActivity(intent)
            finish()
            return
        }
        finish()
    }
}