package com.whitecoats.clinicplus.utils

import ai.api.BuildConfig
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.widget.Toast
import com.whitecoats.clinicplus.R

class ShowAlertDialog {
    fun showPopupToMovePermissionPage(context: Context) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Allow Permission")
        builder.setMessage("Allow Phone Permission to join the video call")
            .setCancelable(false)
            // positive button text and action
            .setPositiveButton("Ok", DialogInterface.OnClickListener(fun(
                dialog: DialogInterface,
                id: Int
            ) {
                val intent = Intent()
                intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                val uri = Uri.fromParts("package", context.packageName, null)
                intent.data = uri
                intent.addCategory(Intent.CATEGORY_DEFAULT)
                context.startActivity(intent)
            }))
            // negative button text and action
            .setNegativeButton("Cancel", DialogInterface.OnClickListener { dialog, id ->
                dialog.cancel()
            })
        val alert: AlertDialog = builder.create()
        alert.show()
    }
}