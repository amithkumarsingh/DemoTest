package com.whitecoats.clinicplus

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Build
import android.telephony.PhoneStateListener
import android.telephony.TelephonyManager
import android.util.Log
import android.widget.Toast
import com.onesignal.OSNotificationAction.ActionType
import com.onesignal.OSNotificationOpenedResult
import com.onesignal.OneSignal
import com.whitecoats.clinicplus.constants.AppConstants
import com.whitecoats.clinicplus.patientsharedrecords.PatientSharedRecordsActivity
import com.whitecoats.clinicplus.utils.ShowAlertDialog

class OSNotificationOpenedHandler(activity: Activity) : OneSignal.OSNotificationOpenedHandler {
    private val activity: Context
    private var callCurrentState = 0
    private val sharedpreferences: SharedPreferences
    private var telephonyManager: TelephonyManager? = null
    private var callStateListener: PhoneStateListener? = null

    init {
        this.activity = activity
        sharedpreferences =
            this.activity.getSharedPreferences(AppConstants.appSharedPref, Context.MODE_PRIVATE)
    }

    override fun notificationOpened(result: OSNotificationOpenedResult) {
        val actionType = result.action.type
        val data = result.notification.additionalData
        if (data != null) {
            Log.d("Notification Data", data.toString())
            if (JoinVideoActivity.isVideoStart) {
                isNotificationOpen = true
            }
            if (actionType == ActionType.ActionTaken) {
                Log.i("OneSignalExample", "Button pressed with id: " + result.action.actionId)
                if (result.action.actionId.equals("chat_capture", ignoreCase = true)) {
                    val intent = Intent(activity, ChatRoomActivity::class.java)
                    intent.flags =
                        Intent.FLAG_ACTIVITY_REORDER_TO_FRONT or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NO_HISTORY)
                    intent.putExtra("chatId", data.optString("chatId").toInt())
                    intent.putExtra("patientName", "")
                    intent.putExtra("ChatType", "Active")
                    intent.putExtra("recipientId", data.optString("userId").toInt())
                    activity.startActivity(intent)
                } else if (result.action.actionId.equals("view_capture", ignoreCase = true)) {
                    try {
                        val catid = data.optJSONArray("catIds")
                        val intent = Intent(activity, PatientRecordViewActivity::class.java)
                        intent.flags =
                            Intent.FLAG_ACTIVITY_REORDER_TO_FRONT or Intent.FLAG_ACTIVITY_NEW_TASK
                        intent.putExtra("CategoryId", catid!!.getInt(0))
                        intent.putExtra("CategoryName", "")
                        intent.putExtra("PatientId", data.optString("user_id").toInt())
                        activity.startActivity(intent)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                } else if (result.action.actionId.equals("join_capture", ignoreCase = true)) {
                  /*  telephonyManager =
                        activity.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
                    callStateListener = object : PhoneStateListener() {
                        override fun onCallStateChanged(state: Int, incomingNumber: String) {
                            if (state == TelephonyManager.CALL_STATE_RINGING) {
                                callCurrentState = 1
                                Toast.makeText(
                                    activity.applicationContext,
                                    "You can't place a video call if you're already on a phone call.",
                                    Toast.LENGTH_LONG
                                ).show()
                            } else if (state == TelephonyManager.CALL_STATE_OFFHOOK) {
                                callCurrentState = 2
                                Toast.makeText(
                                    activity.applicationContext,
                                    "You can't place a video call if you're already on a phone call.",
                                    Toast.LENGTH_LONG
                                ).show()
                            } else {
                                if (callCurrentState == 1 || callCurrentState == 2) {
                                    callCurrentState = 0
                                } else {
                                    //checking if the calling activity is open
                                    if (sharedpreferences.getBoolean("InVideoCalling", false)) {
                                        val local = Intent()
                                        local.action = "EndCallingVideo"
                                        activity.sendBroadcast(local)
                                    }
                                    val intent = Intent(activity, JoinVideoActivity::class.java)
                                    intent.flags =
                                        Intent.FLAG_ACTIVITY_REORDER_TO_FRONT or Intent.FLAG_ACTIVITY_NEW_TASK
                                    intent.putExtra(
                                        "AppointmentId",
                                        data.optString("apptId").toInt()
                                    )
                                    intent.putExtra("callingFrom","notificationOpenHandler")
                                    activity.startActivity(intent)
                                }
                            }
                        }
                    }
                    if (Build.VERSION.SDK_INT >= 31) {
                        if (activity.checkSelfPermission(Manifest.permission.READ_PHONE_STATE)
                            != PackageManager.PERMISSION_GRANTED
                        ) {
                            ShowAlertDialog().showPopupToMovePermissionPage(activity)
                        } else {
                            telephonyManager!!.listen(
                                callStateListener,
                                PhoneStateListener.LISTEN_CALL_STATE
                            )
                        }
                    } else {
                        telephonyManager!!.listen(
                            callStateListener,
                            PhoneStateListener.LISTEN_CALL_STATE
                        )
                    }
                }*/
                //checking if the calling activity is open
                if (sharedpreferences.getBoolean("InVideoCalling", false)) {
                    val local = Intent()
                    local.action = "EndCallingVideo"
                    activity.sendBroadcast(local)
                }
                val intent = Intent(activity, JoinVideoActivity::class.java)
                intent.flags =
                    Intent.FLAG_ACTIVITY_REORDER_TO_FRONT or Intent.FLAG_ACTIVITY_NEW_TASK
                intent.putExtra(
                    "AppointmentId",
                    data.optString("apptId").toInt()
                )
                intent.putExtra("callingFrom","notificationOpenHandler")
                activity.startActivity(intent)
            }
            } else {
                if (data.optString("push_type").equals("user_chat", ignoreCase = true)) {
                    val intent = Intent(activity, ChatRoomActivity::class.java)
                    intent.flags =
                        Intent.FLAG_ACTIVITY_REORDER_TO_FRONT or Intent.FLAG_ACTIVITY_NEW_TASK
                    intent.putExtra("chatId", data.optString("chatId").toInt())
                    intent.putExtra("patientName", "")
                    intent.putExtra("ChatType", "Active")
                    intent.putExtra("recipientId", data.optString("userId").toInt())
                    activity.startActivity(intent)
                } else if (data.optString("push_type").equals("user_share", ignoreCase = true)) {
                    try {
                        //val catid = data.optJSONArray("catIds")
                        val intent = Intent(activity, PatientSharedRecordsActivity::class.java)
                        intent.flags =
                            Intent.FLAG_ACTIVITY_REORDER_TO_FRONT or Intent.FLAG_ACTIVITY_NEW_TASK
                        intent.putExtra("RecordsDate", data.optString("date"))
                        intent.putExtra("PatientName", data.optString("user_name"))
                        intent.putExtra("PatientId", data.optString("user_id").toInt())
                        intent.putExtra("FromNotification", true)
                        activity.startActivity(intent)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                } else if (data.optString("push_type").equals("user_push", ignoreCase = true)) {
                 /*   telephonyManager =
                        activity.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
                    callStateListener = object : PhoneStateListener() {
                        override fun onCallStateChanged(state: Int, incomingNumber: String) {
                            if (state == TelephonyManager.CALL_STATE_RINGING) {
                                callCurrentState = 1
                                Toast.makeText(
                                    activity.applicationContext,
                                    "You can't place a video call if you're already on a phone call.",
                                    Toast.LENGTH_LONG
                                ).show()
                            } else if (state == TelephonyManager.CALL_STATE_OFFHOOK) {
                                callCurrentState = 2
                                Toast.makeText(
                                    activity.applicationContext,
                                    "You can't place a video call if you're already on a phone call.",
                                    Toast.LENGTH_LONG
                                ).show()
                            } else {
                                if (callCurrentState == 1 || callCurrentState == 2) {
                                    callCurrentState = 0
                                } else {
                                    val intent = Intent(activity, JoinVideoActivity::class.java)
                                    intent.flags =
                                        Intent.FLAG_ACTIVITY_REORDER_TO_FRONT or Intent.FLAG_ACTIVITY_NEW_TASK
                                    intent.putExtra(
                                        "AppointmentId",
                                        data.optString("apptId").toInt()
                                    )
                                    intent.putExtra("callingFrom","notificationOpenHandler")
                                    activity.startActivity(intent)
                                }
                            }
                        }
                    }
                    if (Build.VERSION.SDK_INT >= 31) {
                        if (activity.checkSelfPermission(Manifest.permission.READ_PHONE_STATE)
                            != PackageManager.PERMISSION_GRANTED
                        ) {
                            ShowAlertDialog().showPopupToMovePermissionPage(activity)
                        } else {
                            telephonyManager!!.listen(
                                callStateListener,
                                PhoneStateListener.LISTEN_CALL_STATE
                            )
                        }
                    } else {
                        telephonyManager!!.listen(
                            callStateListener,
                            PhoneStateListener.LISTEN_CALL_STATE
                        )
                    }*/

                    val intent = Intent(activity, JoinVideoActivity::class.java)
                    intent.flags =
                        Intent.FLAG_ACTIVITY_REORDER_TO_FRONT or Intent.FLAG_ACTIVITY_NEW_TASK
                    intent.putExtra(
                        "AppointmentId",
                        data.optString("apptId").toInt()
                    )
                    intent.putExtra("callingFrom","notificationOpenHandler")
                    activity.startActivity(intent)
                }
            }
        }


        // The following can be used to open an Activity of your choice.
        // Replace - getApplicationContext() - with any Android Context.
        // Intent intent = new Intent(getApplicationContext(), YourActivity.class);
        // intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_NEW_TASK);
        // startActivity(intent);

        // Add the following to your AndroidManifest.xml to prevent the launching of your main Activity
        //   if you are calling startActivity above.
        /*
        <application ...>
          <meta-data android:name="com.onesignal.NotificationOpened.DEFAULT" android:value="DISABLE" />
        </application>
     */
    }

    companion object {
        @JvmField
        var isNotificationOpen = false
    }
}