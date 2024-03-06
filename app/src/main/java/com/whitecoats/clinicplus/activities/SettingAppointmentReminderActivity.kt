package com.whitecoats.clinicplus.activities

import android.app.AlertDialog
import android.content.Intent
import android.graphics.PorterDuff
import android.os.Bundle
import android.text.InputFilter
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.whitecoats.clinicplus.MyClinicGlobalClass
import com.whitecoats.clinicplus.R
import com.whitecoats.clinicplus.apis.ApiGetPostMethodCalls
import com.whitecoats.clinicplus.utils.ErrorHandlerClass.errorHandler
import com.whitecoats.clinicplus.utils.InputFilterMinMax
import com.whitecoats.clinicplus.viewmodels.SettingViewModel
import org.json.JSONObject


class SettingAppointmentReminderActivity : AppCompatActivity() {
    private lateinit var jsonValue: JSONObject
    lateinit var globalClass: MyClinicGlobalClass
    private var rootObjGetAppReminder: JSONObject? = null
    private lateinit var appReminderSmsOne: EditText
    private lateinit var appReminderSmsTwo: EditText
    private lateinit var appReminderEmailOne: EditText
    private lateinit var appReminderEmailTwo: EditText
    private lateinit var rescheduleCB: CheckBox
    private lateinit var appReminderUpdateButton: Button
    private var globalApiCall: ApiGetPostMethodCalls? = null
    private lateinit var dialog: AlertDialog
    private lateinit var settingViewModel: SettingViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.setting_app_reminder_activity)
        val toolbar = findViewById<Toolbar>(R.id.settingFormToolbar)
        toolbar.title = intent.getStringExtra("Title")
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        val upArrow = ContextCompat.getDrawable(this, R.drawable.ic_arrow_back)
        upArrow?.setColorFilter(
            ContextCompat.getColor(this, R.color.colorWhite),
            PorterDuff.Mode.SRC_ATOP
        )
        supportActionBar!!.setHomeAsUpIndicator(upArrow)
        globalClass = applicationContext as MyClinicGlobalClass
        globalApiCall = ApiGetPostMethodCalls()
        settingViewModel = ViewModelProvider(this)[SettingViewModel::class.java]
        settingViewModel.init()
        toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed() // Implemented by activity
        }
        appReminderSmsOne = findViewById(R.id.appReminderSmsOne)
        appReminderSmsTwo = findViewById(R.id.appReminderSmsTwo)
        appReminderEmailOne = findViewById(R.id.appReminderEmailOne)
        appReminderEmailTwo = findViewById(R.id.appReminderEmailTwo)
        appReminderUpdateButton = findViewById(R.id.appReminderUpdateButton)
        appReminderSmsOne.filters = arrayOf<InputFilter>(InputFilterMinMax(0, 72))
        appReminderSmsTwo.filters = arrayOf<InputFilter>(InputFilterMinMax(0, 72))
        appReminderEmailOne.filters = arrayOf<InputFilter>(InputFilterMinMax(0, 72))
        appReminderEmailTwo.filters = arrayOf<InputFilter>(InputFilterMinMax(0, 72))
        rescheduleCB = findViewById(R.id.rescheduleCB)

        rescheduleCB.setOnClickListener { v ->
            if ((v as CheckBox).isChecked) {
                updateRescheduleNotificationPreference(1)
            } else {
                updateRescheduleNotificationPreference(0)
            }
        }

        appReminderUpdateButton.setOnClickListener {
            if (appReminderSmsOne.text.toString()
                    .equals("", ignoreCase = true) || appReminderSmsOne.text.toString()
                    .toInt() > 72 || appReminderSmsOne.text.toString().toInt() < 1
            ) {
                Toast.makeText(
                    this@SettingAppointmentReminderActivity,
                    resources.getString(R.string.app_reminder_message),
                    Toast.LENGTH_SHORT
                ).show()
            } else if (appReminderSmsTwo.text.toString()
                    .equals("", ignoreCase = true) || appReminderSmsTwo.text.toString()
                    .toInt() > 72 || appReminderSmsTwo.text.toString().toInt() < 1
            ) {
                Toast.makeText(
                    this@SettingAppointmentReminderActivity,
                    resources.getString(R.string.app_reminder_message),
                    Toast.LENGTH_SHORT
                ).show()
            } else if (appReminderEmailOne.text.toString()
                    .equals("", ignoreCase = true) || appReminderEmailOne.text.toString()
                    .toInt() > 72 || appReminderEmailOne.text.toString().toInt() < 1
            ) {
                Toast.makeText(
                    this@SettingAppointmentReminderActivity,
                    resources.getString(R.string.app_reminder_message),
                    Toast.LENGTH_SHORT
                ).show()
            } else if (appReminderEmailTwo.text.toString()
                    .equals("", ignoreCase = true) || appReminderEmailTwo.text.toString()
                    .toInt() > 72 || appReminderEmailTwo.text.toString().toInt() < 1
            ) {
                Toast.makeText(
                    this@SettingAppointmentReminderActivity,
                    resources.getString(R.string.app_reminder_message),
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                upDateAppReminder()
            }
        }
        appReminder()
        appReminderRescheduleNotificationPre()
    }

    private fun updateSettingValues() {
        val doctorDetailsIntent = Intent()
        doctorDetailsIntent.action = "Update_Doctor_Details_Settings"
        sendBroadcast(doctorDetailsIntent)
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        super.getOnBackPressedDispatcher().onBackPressed()
        updateSettingValues()
    }

    //Somewhere that has access to a context
    fun displayMessage(toastString: String?) {
        Toast.makeText(applicationContext, toastString, Toast.LENGTH_LONG).show()
    }

    private fun appReminder() {
        showCustomProgressAlertDialog("Fetching")
        settingViewModel.settingRemainderDetails()
            .observe(
                this
            ) { result ->
                dialog.dismiss()
                try {
                    val response = JSONObject(result)
                    if (response.getInt("status_code") == 200) {
                        dialog.dismiss()
                        val jsonRes = response.optJSONObject("response")
                        rootObjGetAppReminder = jsonRes?.optJSONObject("response")
                        if (rootObjGetAppReminder != null) {
                            appReminderSmsOne.setText(rootObjGetAppReminder!!.getString("reminder_1"))
                            appReminderSmsTwo.setText(rootObjGetAppReminder!!.getString("reminder_2"))
                            appReminderEmailOne.setText(rootObjGetAppReminder!!.getString("email_reminder_1"))
                            appReminderEmailTwo.setText(rootObjGetAppReminder!!.getString("email_reminder_2"))
                        }
                    } else {
                        errorHandler(this@SettingAppointmentReminderActivity, result)
                    }
                } catch (e: Exception) {
                    dialog.dismiss()
                    e.printStackTrace()
                }
            }
        /*         val url = ApiUrls.getAppReminder
        globalApiCall!!.volleyApiRequestData(
             url,
             Request.Method.GET,
             null,
             this,
             object : VolleyCallback {
                 override fun onSuccess(result: String) {
                     dialog.dismiss()
                     try {
                         val response = JSONObject(result)
                         dialog.dismiss()
                         rootObjGetAppReminder = response.optJSONObject("response")
                         if (rootObjGetAppReminder != null) {
                             appReminderSmsOne.setText(rootObjGetAppReminder!!.getString("reminder_1"))
                             appReminderSmsTwo.setText(rootObjGetAppReminder!!.getString("reminder_2"))
                             appReminderEmailOne.setText(rootObjGetAppReminder!!.getString("email_reminder_1"))
                             appReminderEmailTwo.setText(rootObjGetAppReminder!!.getString("email_reminder_2"))
                         }
                     } catch (e: Exception) {
                         dialog.dismiss()
                         e.printStackTrace()
                     }
                 }

                 override fun onError(err: String) {
                     dialog.dismiss()
                     errorHandler(this@SettingAppointmentReminderActivity, err)
                 }
             })*/
    }

    private fun appReminderRescheduleNotificationPre() {
        // val url = ApiUrls.getAppReschedulePreferences

        settingViewModel.settingReminderRescheduleNotificationPre().observe(this) { result ->
            try {
                val response = JSONObject(result)
                Log.d("appReminderResponse", response.toString())
                if (response.getInt("status_code") == 200) {
                    val jsonRes = response.optJSONObject("response")
                    val notificationObject = jsonRes?.optJSONObject("response")
                    val rescheduleToDoctor = notificationObject?.getInt("reschedule_to_doctor")
                    rescheduleCB.isChecked = rescheduleToDoctor == 1
                } else {
                    errorHandler(this@SettingAppointmentReminderActivity, result)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        /*globalApiCall!!.volleyApiRequestData(
            url,
            Request.Method.GET,
            null,
            this,
            object : VolleyCallback {
                override fun onSuccess(result: String) {
                    try {
                        val response = JSONObject(result)
                        Log.d("appReminderResponse", response.toString())
                        val notificationObject = response.optJSONObject("response")
                        val rescheduleToDoctor = notificationObject?.getInt("reschedule_to_doctor")
                        rescheduleCB.isChecked = rescheduleToDoctor == 1
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                override fun onError(err: String) {
                    errorHandler(this@SettingAppointmentReminderActivity, err)
                }
            })*/
    }

    //update notification preferences
    private fun updateRescheduleNotificationPreference(rescheduleNotificationValue: Int) {
        showCustomProgressAlertDialog(resources.getString(R.string.updating))
        //  val url = ApiUrls.updateRescheduleNotificationPreferences
        try {
            jsonValue = JSONObject()
            jsonValue.put("reschedule_to_doctor", rescheduleNotificationValue)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        settingViewModel.updateRescheduleNotificationPreference(jsonValue).observe(this) { result ->
            dialog.dismiss()
            try {
                dialog.dismiss()
                val response = JSONObject(result)
                Log.d("saveAppReminderResponse", response.toString())
                if (response.getInt("status_code") == 200) {
                    val jsonRes = response.optJSONObject("response")
                    if (jsonRes?.getInt("reschedule_to_doctor") == 1) {
                        appReminderRescheduleNotificationPre()
                    }
                } else {
                    dialog.dismiss()
                    errorHandler(this@SettingAppointmentReminderActivity, result)
                }
            } catch (e: Exception) {
                dialog.dismiss()
                e.printStackTrace()
            }
        }
        /*globalApiCall!!.volleyApiRequestData(
            url,
            Request.Method.POST,
            jsonValue,
            this,
            object : VolleyCallback {
                override fun onSuccess(result: String) {
                    dialog.dismiss()
                    try {
                        dialog.dismiss()
                        val response = JSONObject(result)
                        Log.d("saveAppReminderResponse", response.toString())
                        if (response.getInt("reschedule_to_doctor") == 1) {
                            appReminderRescheduleNotificationPre()
                        }
                    } catch (e: Exception) {
                        dialog.dismiss()
                        e.printStackTrace()
                    }
                }

                override fun onError(err: String) {
                    dialog.dismiss()
                    errorHandler(this@SettingAppointmentReminderActivity, err)
                }
            })*/
    }

    private fun upDateAppReminder() {
        showCustomProgressAlertDialog(resources.getString(R.string.updating))
        //  val url = ApiUrls.saveAppReminder
        val params = mutableMapOf<Any?, Any?>()
        try {
            params["reminder1"] = appReminderSmsOne.text.toString()
            params["reminder2"] = appReminderSmsTwo.text.toString()
            params["email_reminder_1"] = appReminderEmailOne.text.toString()
            params["email_reminder_2"] = appReminderEmailTwo.text.toString()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        settingViewModel.upDateAppReminder(JSONObject(params)).observe(
            this
        ) { result ->
            dialog.dismiss()
            try {
                val response = JSONObject(result)
                if (response.getInt("status_code") == 200) {
                    Toast.makeText(
                        applicationContext,
                        "Clinic appointment reminders saved successfully",
                        Toast.LENGTH_LONG
                    ).show()
                } else {
                    dialog.dismiss()
                    errorHandler(this@SettingAppointmentReminderActivity, result)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        /*globalApiCall!!.volleyApiRequestData(
            url,
            Request.Method.POST,
            JSONObject(params),
            this,
            object : VolleyCallback {
                override fun onSuccess(result: String) {
                    dialog.dismiss()
                    try {
                        dialog.dismiss()
                        Toast.makeText(
                            applicationContext,
                            "Clinic appointment reminders saved successfully",
                            Toast.LENGTH_LONG
                        ).show()
                    } catch (e: Exception) {
                        dialog.dismiss()
                        e.printStackTrace()
                    }
                }

                override fun onError(err: String) {
                    dialog.dismiss()
                    errorHandler(this@SettingAppointmentReminderActivity, err)
                }
            })*/
    }

    private fun showCustomProgressAlertDialog(title: String) {
        val builder = AlertDialog.Builder(this@SettingAppointmentReminderActivity)
        builder.setTitle(title)
        builder.setCancelable(false)
        val customLayout: View = layoutInflater.inflate(R.layout.custom_progress_bar, null)
        builder.setView(customLayout)
        dialog = builder.create()
        dialog.show()
    }
}