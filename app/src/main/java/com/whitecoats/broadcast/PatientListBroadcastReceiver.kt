package com.whitecoats.broadcast

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.util.Log
import android.view.View
import co.mobiwise.materialintro.shape.Focus
import co.mobiwise.materialintro.shape.FocusGravity
import co.mobiwise.materialintro.shape.ShapeType
import co.mobiwise.materialintro.view.MaterialIntroView
import com.whitecoats.clinicplus.R
import com.whitecoats.clinicplus.apis.ApiUrls

class PatientListBroadcastReceiver : BroadcastReceiver() {
    private var appPreference: SharedPreferences? = null
    override fun onReceive(context: Context, intent: Intent) {
        Log.d("pateintAdd", "pateintAdd")
        /*Checking whether the broadcast receive the intent from current app*/if (intent.getPackage()
                .equals("com.whitecoats.clinicplus", ignoreCase = true)
        ) {
            val callingActivity = intent.getStringExtra("Activity")
            appPreference =
                context.getSharedPreferences(ApiUrls.appSharedPref, Context.MODE_PRIVATE)
            if (callingActivity.equals("PatientList", ignoreCase = true)) {
                if (intent.getStringExtra("Param").equals("NoPatients", ignoreCase = true)) {
                    showGuide(1, context)
                }
            }
        }
    }

    private fun showGuide(section: Int, context: Context) {
        try {
            when (section) {
                1 -> {
                    val rootView =
                        (context as Activity).window.decorView.findViewById<View>(R.id.patientListAddPatient)
                    if (!appPreference!!.getBoolean("PatientList", false)) {

                        MaterialIntroView.Builder(context)
                            .enableDotAnimation(true)
                            .enableIcon(false)
                            .dismissOnTouch(true)
                            .setFocusGravity(FocusGravity.CENTER)
                            .setFocusType(Focus.NORMAL)
                            .setDelayMillis(50)
                            .enableFadeAnimation(true)
                            .setInfoText("If you haven't already added any patient tap the button to add one")
                            .setShape(ShapeType.CIRCLE)
                            .setTarget(rootView)
                            .setUsageId("intro_addPatient") //THIS SHOULD BE UNIQUE ID
                            .setListener {
                                val editor = appPreference!!.edit()
                                editor.putBoolean("PatientList", true)
                                editor.commit()
                            }
                            .show()
                    }
                }
                2 -> {}
            }
        } catch (e: Exception) {
        }
    }
}