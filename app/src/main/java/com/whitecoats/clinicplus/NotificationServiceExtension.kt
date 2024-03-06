package com.whitecoats.clinicplus

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.util.Log
import android.widget.Toast
import com.onesignal.OSNotificationReceivedEvent
import com.onesignal.OneSignal.OSRemoteNotificationReceivedHandler
import com.whitecoats.clinicplus.activities.VideoCallActivity
import com.whitecoats.clinicplus.constants.AppConstants
import org.json.JSONObject

@SuppressWarnings("unused")
class NotificationServiceExtension() :
    OSRemoteNotificationReceivedHandler {
    private lateinit var sharedPreferences: SharedPreferences
    override fun remoteNotificationReceived(
        activity: Context,
        notificationReceivedEvent: OSNotificationReceivedEvent
    ) {
        sharedPreferences =
            activity.getSharedPreferences(AppConstants.appSharedPref, Context.MODE_PRIVATE)
        val notification = notificationReceivedEvent.notification
        notificationReceivedEvent.complete(notification)
        val data: JSONObject = notification.additionalData
        Log.d("NotificationData", data.toString())
        try {
            val type = data.getString("push_type")
            if (type == "user_push" && data.optString("apptId") != "") {
                Log.d(
                    "Boolean Value",
                    sharedPreferences.getBoolean("InVideoCalling", false)
                        .toString() + " " + sharedPreferences.getBoolean(
                        "InVideoSession",
                        false
                    )
                )
                if (!sharedPreferences.getBoolean(
                        "InVideoCalling",
                        false
                    ) && !sharedPreferences.getBoolean("InVideoSession", false)
                ) {
                    val intent = Intent(activity, VideoCallActivity::class.java)
                    intent.flags =
                        Intent.FLAG_ACTIVITY_REORDER_TO_FRONT or Intent.FLAG_ACTIVITY_NEW_TASK
                    intent.putExtra("AppointmentId", data.optString("apptId").toInt())
                    intent.putExtra("PatientID", data.getInt("userId"))
                    activity.startActivity(intent)
                } else if (sharedPreferences.getBoolean("InVideoSession", false)) {
                    val local = Intent()
                    local.action = "NewCallIncoming"
                    local.putExtra("IncomingApptId", data.optString("apptId").toInt())
                    activity.sendBroadcast(local)
                }
            } else if (type == "exit_video") {
                val local = Intent()
                local.action = "EndCallingVideo"
                activity.sendBroadcast(local)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }
}