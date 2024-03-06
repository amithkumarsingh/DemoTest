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

class CommunicationBroadcastReceiver : BroadcastReceiver() {
    private var appPreference: SharedPreferences? = null
    override fun onReceive(context: Context, intent: Intent) {
        /*Checking whether the broadcast receive the intent from current app*/
        if (intent.getPackage().equals("com.whitecoats.clinicplus", ignoreCase = true)) {
            val callingActivity = intent.getStringExtra("Activity")
            appPreference =
                context.getSharedPreferences(ApiUrls.appSharedPref, Context.MODE_PRIVATE)
            if (callingActivity.equals("CommunicationList", ignoreCase = true)) {
                val text = intent.getStringExtra("text")
                showGuide(1, context, text)
            } else if (callingActivity.equals("CommunicationMessage", ignoreCase = true)) {
                if (intent.getStringExtra("Param").equals("MsgList", ignoreCase = true)) {
                    Log.d("Comm Messgae", "*********************")
                    showGuide(3, context, "")
                } else if (intent.getStringExtra("Param").equals("DialogOpen", ignoreCase = true)) {
                    showGuide(5, context, "")
                } else {
                    showGuide(6, context, "")
                }
            }
        }
    }

    private fun showGuide(section: Int, context: Context, text: String?) {
        try {
            val rootView: View
            when (section) {
                1 -> if (!appPreference!!.getBoolean("CommList", false)) {
                     rootView =
                        (context as Activity).window.decorView.findViewById<View>(R.id.commListFabCreate)
                     MaterialIntroView.Builder(context)
                        .enableDotAnimation(true)
                        .enableIcon(false)
                        .dismissOnTouch(true)
                        .setFocusGravity(FocusGravity.CENTER)
                        .setFocusType(Focus.NORMAL)
                        .setDelayMillis(50)
                        .enableFadeAnimation(true)
                        .setInfoText(text)
                        .setShape(ShapeType.CIRCLE)
                        .setTarget(rootView)
                        .setUsageId("intro_newArticle") //THIS SHOULD BE UNIQUE ID
                        .setListener {
                            showGuide(2, context, "")
                            val editor = appPreference!!.edit()
                            editor.putBoolean("CommList", true)
                            editor.commit()
                        }
                        .show()
                }
                2 -> {
                     rootView =
                        (context as Activity).window.decorView.findViewById<View>(R.id.commListMenuSearch)
                    MaterialIntroView.Builder(context)
                        .enableDotAnimation(true)
                        .enableIcon(false)
                        .dismissOnTouch(true)
                        .setFocusGravity(FocusGravity.CENTER)
                        .setFocusType(Focus.NORMAL)
                        .setDelayMillis(50)
                        .enableFadeAnimation(true)
                        .setInfoText("Find your article easily")
                        .setShape(ShapeType.CIRCLE)
                        .setTarget(rootView)
                        .setUsageId("intro_article") //THIS SHOULD BE UNIQUE ID
                        .setListener {
                        }
                        .show()
                }
                3 -> {
                    rootView =
                        (context as Activity).window.decorView.findViewById<View>(R.id.commMessageListFabCreate)
                    Log.d(
                        "Comm Messgae",
                        "*********************" + appPreference!!.getBoolean("CommMessage", false)
                    )
                    if (!appPreference!!.getBoolean("CommMessage", false)) {
                        MaterialIntroView.Builder(context)
                            .enableDotAnimation(true)
                            .enableIcon(false)
                            .dismissOnTouch(true)
                            .setFocusGravity(FocusGravity.CENTER)
                            .setFocusType(Focus.NORMAL)
                            .setDelayMillis(50)
                            .enableFadeAnimation(true)
                            .setInfoText("Tap send new message to all your patients at once")
                            .setShape(ShapeType.CIRCLE)
                            .setTarget(rootView)
                            .setUsageId("intro_sendMessage") //THIS SHOULD BE UNIQUE ID
                            .setListener {
                                showGuide(4, context, "")
                                val editor = appPreference!!.edit()
                                editor.putBoolean("CommMessage", true)
                                editor.commit()
                            }
                            .show()
                    }
                }
                4 -> {
                    rootView =
                        (context as Activity).window.decorView.findViewById<View>(R.id.commListMenuSearch)
                    MaterialIntroView.Builder(context)
                        .enableDotAnimation(true)
                        .enableIcon(false)
                        .dismissOnTouch(true)
                        .setFocusGravity(FocusGravity.CENTER)
                        .setFocusType(Focus.NORMAL)
                        .setDelayMillis(50)
                        .enableFadeAnimation(true)
                        .setInfoText("Search for a particular message instantly")
                        .setShape(ShapeType.CIRCLE)
                        .setTarget(rootView)
                        .setUsageId("intro_messageSearch") //THIS SHOULD BE UNIQUE ID
                        .setListener { }
                        .show()
                }
                5 -> if (!appPreference!!.getBoolean("CommMsgToggle", false)) {
                    rootView =
                        (context as Activity).window.decorView.findViewById<View>(R.id.bottomSheetToggleIcon)
                    MaterialIntroView.Builder(context)
                        .enableDotAnimation(true)
                        .enableIcon(false)
                        .dismissOnTouch(true)
                        .setFocusGravity(FocusGravity.CENTER)
                        .setFocusType(Focus.NORMAL)
                        .setDelayMillis(50)
                        .enableFadeAnimation(true)
                        .setInfoText("Do you want to send to all the registered patients or only those who you have consulted till now")
                        .setShape(ShapeType.CIRCLE)
                        .setTarget(rootView)
                        .setUsageId("intro_registerPatient") //THIS SHOULD BE UNIQUE ID
                        .setListener {
                            val editor = appPreference!!.edit()
                            editor.putBoolean("CommMsgToggle", true)
                            editor.commit()
                        }
                        .show()
                }
                6 -> if (!appPreference!!.getBoolean("CommMsgInterfaces", false)) {
                    rootView =
                        (context as Activity).window.decorView.findViewById<View>(R.id.selectInterface)
                    MaterialIntroView.Builder(context)
                        .enableDotAnimation(true)
                        .enableIcon(false)
                        .dismissOnTouch(true)
                        .setFocusGravity(FocusGravity.CENTER)
                        .setFocusType(Focus.NORMAL)
                        .setDelayMillis(50)
                        .enableFadeAnimation(true)
                        .setInfoText("Select the interface clinicplus which all the patients will receive this message")
                        .setShape(ShapeType.CIRCLE)
                        .setTarget(rootView)
                        .setUsageId("intro_interface") //THIS SHOULD BE UNIQUE ID
                        .setListener {
                            val editor = appPreference!!.edit()
                            editor.putBoolean("CommMsgInterfaces", true)
                            editor.commit()
                        }
                        .show()
                }
            }
        } catch (e: Exception) {
        }
    }
}