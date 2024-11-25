package com.galixo.autoClicker.core.common.permissions.model

data object PermissionOverlay : Permission.Optional {
    /*
        override fun isGranted(context: Context): Boolean =
            Settings.canDrawOverlays(context)

        override fun onStartRequestFlow(context: Context): Boolean {
            val intent = Intent(
                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:${context.packageName}")
            )
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_NO_HISTORY)

            return try {
                context.startActivity(intent)
                true
            } catch (ex: ActivityNotFoundException) {
                Log.e(TAG, "Can't find device overlay settings menu.")
                false
            }
        }*/
}