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
import com.google.android.material.tabs.TabLayout
import com.whitecoats.clinicplus.R
import com.whitecoats.clinicplus.apis.ApiUrls
import smartdevelop.ir.eram.showcaseviewlib.GuideView
import smartdevelop.ir.eram.showcaseviewlib.config.DismissType

class HomeBroadCastReceiver : BroadcastReceiver() {
    private var appPreference: SharedPreferences? = null
    override fun onReceive(context: Context, intent: Intent) {
        /*Checking whether the broadcast receive the intent from current app*/
        if (intent.getPackage().equals("com.whitecoats.clinicplus", ignoreCase = true)) {
            Log.d("Activity", intent.getStringExtra("Activity")!!)
            val callingActivity = intent.getStringExtra("Activity")
            if (callingActivity.equals("HomeTab", ignoreCase = true)) {
                appPreference =
                    context.getSharedPreferences(ApiUrls.appSharedPref, Context.MODE_PRIVATE)
                showGuide(1, context)
            } else if (callingActivity.equals("Assistant", ignoreCase = true)) {
            } else if (callingActivity.equals("Support", ignoreCase = true)) {
            }
        }
    }

    private fun showGuide(section: Int, context: Context) {
        try {
            val rootView: View
            when (section) {
                1 -> {
                     rootView =
                        (context as Activity).window.decorView.findViewById<TabLayout>(R.id.homeTabLayout)
                    if (!appPreference!!.getBoolean("HomeGuide", false)) {

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
    In this dashboard, you will find,
    -Appointment summary
    
    -Quick shortcuts for important tasks
    
    -Records shared by patients
    
    -Follow-up responses from patients
    """.trimIndent()
                            )
                            .setShape(ShapeType.CIRCLE)
                            .setTarget(rootView.getTabAt(0)!!.view)
                            .setUsageId("intro_dashboard") //THIS SHOULD BE UNIQUE ID
                            .setListener {
                                showGuide(2, context)
                                val editor = appPreference!!.edit()
                                editor.putBoolean("HomeGuide", true)
                                editor.commit()
                            }
                            .show()
                    } else {
                        showGuide(5, context)
                    }
                }
                2 -> {
                    rootView =
                        (context as Activity).window.decorView.findViewById<TabLayout>(R.id.homeTabLayout)

                    MaterialIntroView.Builder(context)
                        .enableDotAnimation(true)
                        .enableIcon(false)
                        .dismissOnTouch(true)
                        .setFocusGravity(FocusGravity.CENTER)
                        .setFocusType(Focus.NORMAL)
                        .setDelayMillis(50)
                        .enableFadeAnimation(true)
                        .setInfoText("Use your voice or keyboard to command your virtual assistant to cancel specific appointments or notify patients if you are late") //.setInfoText("Use the virtual assistant to \"get patients data\" or \"cancel appointments\" and more")
                        .setShape(ShapeType.CIRCLE)
                        .setTarget(rootView.getTabAt(1)?.view)
                        .setUsageId("intro_assistant") //THIS SHOULD BE UNIQUE ID
                        .setListener {
                            showGuide(3, context)

                        }
                        .show()
                }
                3 -> {
                    rootView =
                        (context as Activity).window.decorView.findViewById<TabLayout>(R.id.homeTabLayout)

                    MaterialIntroView.Builder(context)
                        .enableDotAnimation(true)
                        .enableIcon(false)
                        .dismissOnTouch(true)
                        .setFocusGravity(FocusGravity.CENTER)
                        .setFocusType(Focus.NORMAL)
                        .setDelayMillis(50)
                        .enableFadeAnimation(true)
                        .setInfoText("For any assistance, use our 9*5 support channels such as clinicplus-app live chat, WhatsApp chat or email") //                        .setInfoText("When clinicplus problem or for any query contact us directly using our support channels")
                        .setShape(ShapeType.CIRCLE)
                        .setTarget(rootView.getTabAt(2)?.view)
                        .setUsageId("intro_support") //THIS SHOULD BE UNIQUE ID
                        .setListener {
                            showGuide(4, context)

                        }
                        .show()
                }
                4 -> {
                    val rootView1 =
                        (context as Activity).window.decorView.findViewById<View>(R.id.homeApptGuidePt)

                    MaterialIntroView.Builder(context)
                        .enableDotAnimation(true)
                        .enableIcon(false)
                        .dismissOnTouch(true)
                        .setFocusGravity(FocusGravity.CENTER)
                        .setFocusType(Focus.NORMAL)
                        .setDelayMillis(50)
                        .enableFadeAnimation(true)
                        .setInfoText("Find your appointments for clinic and video chats")
                        .setShape(ShapeType.RECTANGLE)
                        .setTarget(rootView1)
                        .setUsageId("intro_summary") //THIS SHOULD BE UNIQUE ID
                        .setListener {
                            showGuide(5, context)

                        }
                        .show()
                }
                5 -> {
                   val rootView1 =
                        (context as Activity).window.decorView.findViewById<View>(R.id.summaryDuration)
                    if (rootView1.getVisibility() == View.VISIBLE && !appPreference!!.getBoolean(
                            "ApptGuide",
                            false
                        )
                    ) {

                        MaterialIntroView.Builder(context)
                            .enableDotAnimation(true)
                            .enableIcon(false)
                            .dismissOnTouch(true)
                            .setFocusGravity(FocusGravity.CENTER)
                            .setFocusType(Focus.NORMAL)
                            .setDelayMillis(50)
                            .enableFadeAnimation(true)
                            .setInfoText("Filter your summary as per your preferences") //.setInfoText("Filter your appointments date wise to get more information")
                            .setShape(ShapeType.CIRCLE)
                            .setTarget(rootView1)
                            .setUsageId("intro_home_filter") //THIS SHOULD BE UNIQUE ID
                            .setListener {
                                showGuide(6, context)
                                val editor = appPreference!!.edit()
                                editor.putBoolean("ApptGuide", true)
                                editor.commit()
                            }
                            .show()
                    }
                }
                6 -> {
                   val rootView1 =
                        (context as Activity).window.decorView.findViewById<View>(R.id.cancelAppointment)
                    MaterialIntroView.Builder(context)
                        .enableDotAnimation(true)
                        .enableIcon(false)
                        .dismissOnTouch(true)
                        .setFocusGravity(FocusGravity.CENTER)
                        .setFocusType(Focus.NORMAL)
                        .setDelayMillis(50)
                        .enableFadeAnimation(true)
                        .setInfoText("Cancel appointments and auto notify patients with single click") //.setInfoText("If you are not able to attend any appointment for the day then cancel them at one go and we will inform the patients about it")
                        .setShape(ShapeType.CIRCLE)
                        .setTarget(rootView1)
                        .setUsageId("intro_home_cancel") //THIS SHOULD BE UNIQUE ID
                        .setListener {
                            showGuide(7, context)

                        }
                        .show()
                }
                7 -> {
                  val  rootView1 =
                        (context as Activity).window.decorView.findViewById<View>(R.id.iAmLate)
                    MaterialIntroView.Builder(context)
                        .enableDotAnimation(true)
                        .enableIcon(false)
                        .dismissOnTouch(true)
                        .setFocusGravity(FocusGravity.CENTER)
                        .setFocusType(Focus.NORMAL)
                        .setDelayMillis(50)
                        .enableFadeAnimation(true)
                        .setInfoText("If you are running late, notify patients with a single click") //.setInfoText("Tell all your patients that you are running late for the day")
                        .setShape(ShapeType.CIRCLE)
                        .setTarget(rootView1)
                        .setUsageId("intro_late") //THIS SHOULD BE UNIQUE ID
                        .setListener {
                            showGuide(8, context)

                        }
                        .show()
                }
                8 -> {
                   val rootView1 =
                        (context as Activity).window.decorView.findViewById<View>(R.id.homeQuickGuidePt)
                    MaterialIntroView.Builder(context)
                        .enableDotAnimation(true)
                        .enableIcon(false)
                        .dismissOnTouch(true)
                        .setFocusGravity(FocusGravity.CENTER)
                        .setFocusType(Focus.NORMAL)
                        .setDelayMillis(50)
                        .enableFadeAnimation(true)
                        .setInfoText("Find your quick shortcuts here")
                        .setShape(ShapeType.CIRCLE)
                        .setTarget(rootView1)
                        .setUsageId("intro_quick_shortcut") //THIS SHOULD BE UNIQUE ID
                        .setListener {
                        }
                        .show()
                }
                9 -> {
                  val  rootView1 =
                        (context as Activity).window.decorView.findViewById<View>(R.id.assistantTabChatSendMsg)
                    GuideView.Builder(context)
                        .setTitle("Say Hello")
                        .setContentText("Tap and speak to our Virtual Assistant for any help or perform any task")
                        .setTargetView(rootView1)
                        .setDismissType(DismissType.outside)
                        .setGuideListener {
                        }
                        .build()
                        .show()
                }
            }
        } catch (e: Exception) {
        }
    }
}