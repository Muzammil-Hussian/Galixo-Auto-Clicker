package com.galixo.autoClicker.utils.extensions

import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity

/* ---------------------------------------------- BackPress ---------------------------------------------- */

fun AppCompatActivity.onBackPressedDispatcher(callback: () -> Unit) {
    onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            callback.invoke()
        }
    })
}