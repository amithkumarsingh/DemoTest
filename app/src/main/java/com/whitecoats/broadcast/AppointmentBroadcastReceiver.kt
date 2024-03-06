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

class AppointmentBroadcastReceiver : BroadcastReceiver() {
    private var appPreference: SharedPreferences? = null
    override fun onReceive(context: Context, intent: Intent) {
        /*Checking whether the broadcast receive the intent from current app*/
        if (intent.getPackage().equals("com.whitecoats.clinicplus", ignoreCase = true)) {
            val callingActivity = intent.getStringExtra("Activity")
            appPreference =
                context.getSharedPreferences(ApiUrls.appSharedPref, Context.MODE_PRIVATE)
            if (callingActivity.equals("AppointmentList", ignoreCase = true)) {
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
                        (context as Activity).window.decorView.findViewById<View>(R.id.appointBookAppt)
                    if (!appPreference!!.getBoolean("ApptList", false)) {
                        MaterialIntroView.Builder(context)
                            .enableDotAnimation(true)
                            .enableIcon(false)
                            .dismissOnTouch(true)
                            .setFocusGravity(FocusGravity.CENTER)
                            .setFocusType(Focus.NORMAL)
                            .setDelayMillis(50)
                            .enableFadeAnimation(true)
                            .setInfoText("Click here to book a new appointment")
                            .setShape(ShapeType.CIRCLE)
                            .setTarget(rootView)
                            .setUsageId("intro_appt_filter") //THIS SHOULD BE UNIQUE ID
                            .setListener {
                                showGuide(2, context)
                                val editor = appPreference!!.edit()
                                editor.putBoolean("ApptList", true)
                                editor.commit()
                            }
                            .show()
                    }
                }
                2 -> {
                     rootView =
                        (context as Activity).window.decorView.findViewById<View>(R.id.apptFilterDurationGroup)
                    MaterialIntroView.Builder(context)
                        .enableDotAnimation(true)
                        .enableIcon(false)
                        .dismissOnTouch(true)
                        .setFocusGravity(FocusGravity.CENTER)
                        .setFocusType(Focus.NORMAL)
                        .setDelayMillis(50)
                        .enableFadeAnimation(true)
                        .setInfoText("Filter your appointments with one click")
                        .setShape(ShapeType.RECTANGLE)
                        .setTarget(rootView)
                        .setUsageId("intro_appt_filterDuration") //THIS SHOULD BE UNIQUE ID
                        .setListener { showGuide(3, context) }
                        .show()
                }
                3 -> {
                   rootView =
                        (context as Activity).window.decorView.findViewById<View>(R.id.apptMenuFilter)
                    MaterialIntroView.Builder(context)
                        .enableDotAnimation(true)
                        .enableIcon(false)
                        .dismissOnTouch(true)
                        .setFocusGravity(FocusGravity.CENTER)
                        .setFocusType(Focus.NORMAL)
                        .setDelayMillis(50)
                        .enableFadeAnimation(true)
                        .setInfoText("Find more filter options here")
                        .setShape(ShapeType.CIRCLE)
                        .setTarget(rootView)
                        .setUsageId("intro_appt_moreFilter") //THIS SHOULD BE UNIQUE ID
                        .setListener { showGuide(4, context) }
                        .show()
                }
                4 -> {
                    rootView =
                        (context as Activity).window.decorView.findViewById<View>(R.id.apptMenuSearch)
                    MaterialIntroView.Builder(context)
                        .enableDotAnimation(true)
                        .enableIcon(false)
                        .dismissOnTouch(true)
                        .setFocusGravity(FocusGravity.CENTER)
                        .setFocusType(Focus.NORMAL)
                        .setDelayMillis(50)
                        .enableFadeAnimation(true)
                        .setInfoText("Search for a particular patient's appointment(s)")
                        .setShape(ShapeType.CIRCLE)
                        .setTarget(rootView)
                        .setUsageId("intro_appt_search") //THIS SHOULD BE UNIQUE ID
                        .setListener { }
                        .show()
                }
            }
        } catch (e: Exception) {
        }
    }
}