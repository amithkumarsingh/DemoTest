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

class BookApptBroadcastReceiver : BroadcastReceiver() {
    private var appPreference: SharedPreferences? = null
    override fun onReceive(context: Context, intent: Intent) {
        /*Checking whether the broadcast receive the intent from current app*/
        if (intent.getPackage().equals("com.whitecoats.clinicplus", ignoreCase = true)) {
            val callingActivity = intent.getStringExtra("Activity")
            appPreference =
                context.getSharedPreferences(ApiUrls.appSharedPref, Context.MODE_PRIVATE)
            if (callingActivity.equals("BookAppt", ignoreCase = true)) {
                showGuide(1, context)
            }
        }
    }

    private fun showGuide(section: Int, context: Context) {
        try {
            val rootView: View
            when (section) {
                1 -> {
                     rootView =
                        (context as Activity).window.decorView.findViewById(R.id.searchPatientEditText)
                    if (!appPreference!!.getBoolean("SearchPatient", false)) {
                        MaterialIntroView.Builder(context)
                            .enableDotAnimation(true)
                            .enableIcon(false)
                            .dismissOnTouch(true)
                            .setFocusGravity(FocusGravity.CENTER)
                            .setFocusType(Focus.NORMAL)
                            .setDelayMillis(50)
                            .enableFadeAnimation(true)
                            .setInfoText("To book appointment for a patient search them by name, contact number or unique id")
                            .setShape(ShapeType.CIRCLE)
                            .setTarget(rootView)
                            .setUsageId("intro_appt_searchByName") //THIS SHOULD BE UNIQUE ID
                            .setListener {
                                showGuide(2, context)
                                val editor = appPreference!!.edit()
                                editor.putBoolean("SearchPatient", true)
                                editor.apply()
                            }
                            .show()
                    }
                }
                2 -> {
                    rootView =
                        (context as Activity).window.decorView.findViewById(R.id.searchPageAddPatient)
                    MaterialIntroView.Builder(context)
                        .enableDotAnimation(true)
                        .enableIcon(false)
                        .dismissOnTouch(true)
                        .setFocusGravity(FocusGravity.CENTER)
                        .setFocusType(Focus.NORMAL)
                        .setDelayMillis(50)
                        .enableFadeAnimation(true)
                        .setInfoText("If you have no patient then add them to book appointment with them")
                        .setShape(ShapeType.CIRCLE)
                        .setTarget(rootView)
                        .setUsageId("intro_noPatient") //THIS SHOULD BE UNIQUE ID
                        .setListener { }
                        .show()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}