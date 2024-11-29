package com.galixo.autoClicker.utils.extensions

import android.app.Activity
import android.app.SearchManager
import android.content.ActivityNotFoundException
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.net.Uri
import android.provider.Settings
import android.provider.Settings.SettingNotFoundException
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import com.galixo.autoClicker.AutoClickerService
import com.galixo.autoClicker.R
import com.google.android.material.snackbar.Snackbar


/* ---------------------------------------------- Resources ---------------------------------------------- */

fun Context?.getResString(@StringRes stringId: Int): String {
    return this?.resources?.getString(stringId) ?: ""
}

fun Context?.getDrawableResource(@DrawableRes drawableId: Int): Drawable? {
    this?.let {
        return ContextCompat.getDrawable(it, drawableId)
    } ?: run {
        return null
    }
}

/* ---------------------------------------------- Toast ---------------------------------------------- */

fun Context?.showToast(message: String) {
    (this as Activity).runOnUiThread {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}

fun Context?.showToast(@StringRes stringId: Int) {
    val message = getResString(stringId)
    showToast(message)
}

/* ---------------------------------------------- SnackBar ---------------------------------------------- */

fun Context?.showSnackBar(message: String) {
    (this as Activity).runOnUiThread {
        Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_SHORT).show()
    }
}

/* ---------------------------------------------- Intents ---------------------------------------------- */

fun Context?.openWebUrl(url: String) {
    this?.let {
        try {
            val uri = Uri.parse(url)
            it.startActivity(Intent(Intent.ACTION_VIEW, uri))
        } catch (ex: Exception) {
            Log.e(TAG, "openWebUrl: $ex")
        }
    }
}

fun Context?.openWebUrl(@StringRes stringId: Int) {
    val url = getResString(stringId)
    openWebUrl(url)
}

fun Context?.openPlayStoreApp(packageName: String? = this?.packageName) {
    val playStoreUrl = "https://play.google.com/store/apps/details?id=$packageName"
    openWebUrl(playStoreUrl)
}

fun Context?.openEmailApp(emailAddress: String) {
    this?.let {
        val appName = getResString(R.string.app_name)
        val emailIntent = Intent(Intent.ACTION_SEND)
        emailIntent.type = "message/rfc822"
        emailIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf(emailAddress))
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, appName)
        emailIntent.putExtra(Intent.EXTRA_TEXT, "Feedback...")
        try {
            it.startActivity(Intent.createChooser(emailIntent, "Send mail..."))
        } catch (ex: ActivityNotFoundException) {
            Log.e(TAG, "openEmailApp: $ex")
        }
    }
}

fun Context?.openEmailApp(@StringRes emailAddressId: Int) {
    val emailAddress = getResString(emailAddressId)
    openEmailApp(emailAddress)
}

fun Context?.shareApp() {
    this?.let {
        try {
            val playStoreUrl = "https://play.google.com/store/apps/details?id=$packageName"
            val appName = getResString(R.string.app_name)
            val sendIntent = Intent()
            sendIntent.action = Intent.ACTION_SEND
            sendIntent.putExtra(Intent.EXTRA_SUBJECT, appName)
            sendIntent.putExtra(Intent.EXTRA_TEXT, playStoreUrl)
            sendIntent.type = "text/plain"
            it.startActivity(sendIntent)
        } catch (ex: Exception) {
            Log.e(TAG, "shareApp: $ex")
        }
    }
}

fun Context?.openSubscriptions() {
    this?.let {
        try {
            val playStoreUrl =
                "https://play.google.com/store/account/subscriptions?package=$packageName"
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(playStoreUrl)));
        } catch (ex: ActivityNotFoundException) {
            Log.e(TAG, "openSubscriptions: $ex")
        }
    }
}

fun Context?.copyClipboardData(text: String) {
    this?.let {
        try {
            val clipboard = it.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip: ClipData = ClipData.newPlainText("simple text", text)
            clipboard.setPrimaryClip(clip)
        } catch (ex: Exception) {
            Log.e(TAG, "copyClipboardData: $ex")
        }
    }
}

fun Context?.shareTextData(data: String) {
    this?.let {
        try {
            val intentTextData = Intent(Intent.ACTION_SEND)
            intentTextData.type = "text/plain"
            intentTextData.putExtra(Intent.EXTRA_SUBJECT, "Data")
            intentTextData.putExtra(Intent.EXTRA_TEXT, data)
            it.startActivity(Intent.createChooser(intentTextData, "Choose to share"))
        } catch (ex: Exception) {
            Log.i(TAG, "shareTextData: $ex")
        }
    }
}

fun Context?.searchData(text: String) {
    this?.let {
        try {
            val intentSearch = Intent(Intent.ACTION_WEB_SEARCH)
            intentSearch.putExtra(SearchManager.QUERY, text)
            it.startActivity(intentSearch)
        } catch (ex: Exception) {
            Log.e(TAG, "searchData: $ex")
        }
    }
}

fun Activity?.translateDate(data: String) {
    this?.let {
        try {
            val url =
                "https://translate.google.com/#view=home&op=translate&sl=auto&tl=en&text=$data"
            val intentTranslate = Intent(Intent.ACTION_VIEW)
            intentTranslate.data = Uri.parse(url)
            it.startActivity(intentTranslate)
        } catch (ex: Exception) {
            Log.e(TAG, ex.toString())
        }
    }
}

 fun Context.isAccessibilitySettingsOn(): Boolean {
    var accessibilityEnabled = 0
    val service = packageName + "/" + AutoClickerService::class.java.canonicalName
    try {
        accessibilityEnabled = Settings.Secure.getInt(applicationContext.contentResolver, Settings.Secure.ACCESSIBILITY_ENABLED)
    } catch (e: SettingNotFoundException) {
        e.printStackTrace()
    }
    val mStringColonSplitter = TextUtils.SimpleStringSplitter(':')
    if (accessibilityEnabled == 1) {
        val settingValue = Settings.Secure.getString(
            applicationContext.contentResolver,
            Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
        )
        if (settingValue != null) {
            mStringColonSplitter.setString(settingValue)
            while (mStringColonSplitter.hasNext()) {
                val accessibilityService = mStringColonSplitter.next()
                if (accessibilityService.equals(service, ignoreCase = true)) {
                    return true
                }
            }
        }
    }
    return false
}

private const val TAG = "ContextLogs"