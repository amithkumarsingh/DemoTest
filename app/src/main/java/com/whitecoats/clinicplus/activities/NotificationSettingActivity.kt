package com.whitecoats.clinicplus.activities

import android.app.AlertDialog
import android.app.Dialog
import android.graphics.PorterDuff
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.whitecoats.clinicplus.R
import com.whitecoats.clinicplus.utils.ErrorHandlerClass.errorHandler
import com.whitecoats.clinicplus.viewmodels.DashBoardSettingViewModel
import org.json.JSONException
import org.json.JSONObject

class NotificationSettingActivity : AppCompatActivity() {
    private var dashBoardSettingViewModel: DashBoardSettingViewModel? = null
    private lateinit var emailSwitch: SwitchCompat
    private lateinit var smsSwitch: SwitchCompat
    private lateinit var pushSwitch: SwitchCompat
    private var emailSwitchValue = false
    private var smsSwitchValue = false
    private var pushSwitchValue = false
    private lateinit var dialog: Dialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.notification_setting_activity)
        val toolbar = findViewById<Toolbar>(R.id.settingFormToolbar)
        toolbar.title = intent.getStringExtra("Title")
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        val upArrow = ContextCompat.getDrawable(this,R.drawable.ic_arrow_back)
        upArrow!!.setColorFilter(ContextCompat.getColor(this,R.color.colorWhite), PorterDuff.Mode.SRC_ATOP)
        supportActionBar!!.setHomeAsUpIndicator(upArrow)
        dashBoardSettingViewModel = ViewModelProvider(this)[DashBoardSettingViewModel::class.java]
        dashBoardSettingViewModel!!.init()
        emailSwitch = findViewById(R.id.settingEmailSwitch)
        smsSwitch = findViewById(R.id.settingSMSSwitch)
        pushSwitch = findViewById(R.id.settingPushSwitch)
        toolbar.setNavigationOnClickListener { finish() }
        emailSwitch.setOnCheckedChangeListener { compoundButton, isChecked ->
            emailSwitchValue = if (isChecked) {
                // The toggle is enabled
                emailSwitch.isChecked = true
                isChecked
            } else {
                // The toggle is disabled
                emailSwitch.isChecked = false
                isChecked
            }
            saveNotificationPreferences(emailSwitchValue, smsSwitchValue, pushSwitchValue)
        }
        smsSwitch.setOnCheckedChangeListener { compoundButton, isChecked ->
            smsSwitchValue = if (isChecked) {
                // The toggle is enabled
                smsSwitch.isChecked = true
                isChecked
            } else {
                // The toggle is disabled
                smsSwitch.isChecked = false
                isChecked
            }
            saveNotificationPreferences(emailSwitchValue, smsSwitchValue, pushSwitchValue)
        }
        pushSwitch.setOnCheckedChangeListener { compoundButton, isChecked ->
            pushSwitchValue = if (isChecked) {
                // The toggle is enabled
                pushSwitch.isChecked = true
                true
            } else {
                // The toggle is disabled
                pushSwitch.isChecked = false
                isChecked
            }
            saveNotificationPreferences(emailSwitchValue, smsSwitchValue, pushSwitchValue)
        }
    }

    override fun onResume() {
        super.onResume()
        showCustomProgressAlertDialog(resources.getString(R.string.updating),resources.getString(R.string.wait_while_we_updating))
        dashBoardSettingViewModel!!.getDoctorSettingPreferences(this@NotificationSettingActivity)
            .observe(this) { s ->
                Log.i("EMR res", s)
                dialog.dismiss()
                try {
                    val response = JSONObject(s)
                    if (response.getInt("status_code") == 200) {
                        val resObject = response.getJSONObject("response").getJSONObject("user")
                        emailSwitch.isChecked = resObject.getBoolean("email_notification")
                        smsSwitch.isChecked = resObject.getBoolean("sms_notification")
                        pushSwitch.isChecked = resObject.getBoolean("push_notification")
                    } else {
                        errorHandler(this@NotificationSettingActivity, s)
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                    Log.d("parserError:", "parserError:$e")
                }
            }
    }

    private fun saveNotificationPreferences(
        email_notification: Boolean,
        sms_notification: Boolean,
        push_notification: Boolean
    ) {
        var jsonValue = JSONObject()
        try {
            jsonValue = JSONObject()
            jsonValue.put("email_notification", email_notification)
            jsonValue.put("sms_notification", sms_notification)
            jsonValue.put("push_notification", push_notification)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        dashBoardSettingViewModel!!.saveNotificationStatus(
            this@NotificationSettingActivity,
            jsonValue
        ).observe(this@NotificationSettingActivity) { s ->
            try {
                val jsonObject = JSONObject(s)
                if (jsonObject.getInt("status_code") == 200) {
                    Log.d("response", "success")
                } else {
                    errorHandler(this@NotificationSettingActivity, s)
                }
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }
    }

    fun showCustomProgressAlertDialog(
        title: String?,
        progressVal: String?

    ) {
        val builder = AlertDialog.Builder(this@NotificationSettingActivity)
        builder.setTitle(title)
        builder.setCancelable(false)
        val customLayout =
            LayoutInflater.from(this@NotificationSettingActivity)
                .inflate(R.layout.custom_progress_bar, null)
        builder.setView(customLayout)
        dialog = builder.create()
        val tvBody = customLayout.findViewById<TextView>(R.id.tv_value)
        tvBody.text = progressVal
        dialog.show()
    }
}