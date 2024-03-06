package com.whitecoats.broadcast

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.view.View
import co.mobiwise.materialintro.shape.Focus
import co.mobiwise.materialintro.shape.FocusGravity
import co.mobiwise.materialintro.shape.ShapeType
import co.mobiwise.materialintro.view.MaterialIntroView
import com.whitecoats.clinicplus.R
import com.whitecoats.clinicplus.apis.ApiUrls

class SettingsBroadcastReceiver : BroadcastReceiver() {
    private var appPreference: SharedPreferences? = null
    override fun onReceive(context: Context, intent: Intent) {
        /*Checking whether the broadcast receive the intent from current app*/
        if (intent.getPackage().equals("com.whitecoats.clinicplus", ignoreCase = true)) {
            appPreference =
                context.getSharedPreferences(ApiUrls.appSharedPref, Context.MODE_PRIVATE)
            val callingActivity = intent.getStringExtra("Activity")
            if (callingActivity.equals("BlockTime", ignoreCase = true)) {
                showGuid(1, context)
            }
        }
    }

    private fun showGuid(section: Int, context: Context) {
        try {
            when (section) {
                1 -> {
                    val rootView =
                        (context as Activity).window.decorView.findViewById<View>(R.id.blockTimeAddNew2)
                    if (!appPreference!!.getBoolean("BlockTimeList", false)) {
                        MaterialIntroView.Builder(context)
                            .enableDotAnimation(true)
                            .enableIcon(false)
                            .dismissOnTouch(true)
                            .setFocusGravity(FocusGravity.CENTER)
                            .setFocusType(Focus.NORMAL)
                            .setDelayMillis(50)
                            .enableFadeAnimation(true)
                            .setInfoText(
                                """
    Click here to block patients from booking appointments within a certain time.
    
    For e.g. if you are on leave or a vacation.
    """.trimIndent()
                            )
                            .setShape(ShapeType.CIRCLE)
                            .setTarget(rootView)
                            .setUsageId("intro_blockTimeSetting") //THIS SHOULD BE UNIQUE ID
                            .setListener {
                                val editor = appPreference!!.edit()
                                editor.putBoolean("BlockTimeList", true)
                                editor.apply()
                            }
                            .show()
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}