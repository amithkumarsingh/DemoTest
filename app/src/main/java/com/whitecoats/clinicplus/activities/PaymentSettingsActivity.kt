package com.whitecoats.clinicplus.activities

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.switchmaterial.SwitchMaterial
import com.whitecoats.clinicplus.MyClinicGlobalClass
import com.whitecoats.clinicplus.R
import com.whitecoats.clinicplus.apis.ApiUrls
import com.whitecoats.clinicplus.utils.ErrorHandlerClass.errorHandler
import com.whitecoats.clinicplus.utils.RestUtils
import com.whitecoats.clinicplus.viewmodels.PaymentNotificationViewModel
import org.json.JSONObject

class PaymentSettingsActivity : AppCompatActivity() {
    lateinit var imgBtn_back: ImageButton
    lateinit var btn_savePPref: Button
    var ch_daily: CheckBox? = null
    var ch_weekly: CheckBox? = null
    var ch_monthly: CheckBox? = null
    var ch_every_trans: CheckBox? = null
    var switchNotify_OnOff: SwitchMaterial? = null
    var layout_enableDisable: RelativeLayout? = null
    var paymentNotificationViewModel: PaymentNotificationViewModel? = null
    private var globalClass: MyClinicGlobalClass? = null
    private lateinit var dialog:Dialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment_settings)
        globalClass = applicationContext as MyClinicGlobalClass
        initialize()
    }

    fun initialize() {
        paymentNotificationViewModel = ViewModelProvider(this).get(
            PaymentNotificationViewModel::class.java
        )
        paymentNotificationViewModel!!.init()
        imgBtn_back = findViewById(R.id.paymentSettings_back)
        btn_savePPref = findViewById(R.id.save_prefButton)
        switchNotify_OnOff = findViewById(R.id.settingSwitch)
        ch_daily = findViewById(R.id.cb_daily)
        ch_weekly = findViewById(R.id.cb_weekly)
        ch_monthly = findViewById(R.id.cb_monthly)
        ch_every_trans = findViewById(R.id.cb_everyTransaction)
        layout_enableDisable = findViewById(R.id.enableOrDisable)
        paymentPreferences
        //save preference button
        btn_savePPref.setOnClickListener { updateNotificationPreference() }
        imgBtn_back.setOnClickListener{finish() }
    }

    private val paymentPreferences: Unit
        get() {
            if (globalClass!!.isOnline) {
                showCustomProgressAlertDialog("Please wait..")
                paymentNotificationViewModel!!.getNotificationSettings(this)
                    .observe(this@PaymentSettingsActivity) { s: String? ->
                        Log.d("get_Payment_Response", s!!)
                        dialog.dismiss()
                        try {
                            var response = JSONObject(s)
                            if (response.getInt("status_code") == 200) {
                                MyClinicGlobalClass.logUserActionEvent(
                                    ApiUrls.doctorId,
                                    getString(R.string.PaymentNotificationPreferenceScreenImpression),
                                    null
                                )
                                response = response.getJSONObject(RestUtils.TAG_RESPONSE)
                                    .getJSONObject(RestUtils.TAG_RESPONSE)
                                val pref_enabled = response.getBoolean("is_preference_enabled")
                                val pref_everyTransaction = response.getBoolean("every_transaction")
                                val prefDaily = response.getBoolean("daily")
                                val prefWeekly = response.getBoolean("weekly")
                                val prefMonthly = response.getBoolean("monthly")
                                switchNotify_OnOff!!.isChecked = pref_enabled
                                ch_daily!!.isChecked = prefDaily
                                ch_weekly!!.isChecked = prefWeekly
                                ch_monthly!!.isChecked = prefMonthly
                                ch_every_trans!!.isChecked = pref_everyTransaction
                            } else {
                                errorHandler(this@PaymentSettingsActivity, s)
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
            } else {
                globalClass!!.noInternetConnection.showDialog(this@PaymentSettingsActivity)
            }
        }

    private fun updateNotificationPreference() {
        if (globalClass!!.isOnline) {
            val jsonValue = JSONObject()
            try {
                jsonValue.put("is_preference_enabled", switchNotify_OnOff!!.isChecked)
                jsonValue.put("daily", ch_daily!!.isChecked)
                jsonValue.put("weekly", ch_weekly!!.isChecked)
                jsonValue.put("monthly", ch_monthly!!.isChecked)
                jsonValue.put("every_transaction", ch_every_trans!!.isChecked)
            } catch (e: Exception) {
                e.printStackTrace()
            }
           showCustomProgressAlertDialog("Please wait..")
            paymentNotificationViewModel!!.postNotificationSettings(
                this@PaymentSettingsActivity,
                jsonValue
            ).observe(this@PaymentSettingsActivity) { s: String? ->
                Log.d("Response", s!!)
                dialog.dismiss()
                try {
                    val resObj = JSONObject(s)
                    if (resObj.getInt("status_code") == 200) {
                        finish()
                    } else {
                        errorHandler(this@PaymentSettingsActivity, s)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        } else {
            globalClass!!.noInternetConnection.showDialog(this@PaymentSettingsActivity)
        }
    }
    fun showCustomProgressAlertDialog(
        progressVal: String?

    ) {
        val builder = AlertDialog.Builder(this@PaymentSettingsActivity)
        builder.setCancelable(false)
        val customLayout = LayoutInflater.from(this@PaymentSettingsActivity).inflate(R.layout.custom_progress_bar, null)
        builder.setView(customLayout)
        dialog = builder.create()
        val tvBody = customLayout.findViewById<TextView>(R.id.tv_value)
        tvBody.text = progressVal
        dialog.show()
    }
}