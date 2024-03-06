package com.whitecoats.clinicplus

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.android.volley.Request.Method
import com.whitecoats.clinicplus.apis.ApiGetPostMethodCalls
import com.whitecoats.clinicplus.apis.ApiUrls
import com.whitecoats.clinicplus.fragments.DashboardFullMode
import com.whitecoats.clinicplus.utils.ErrorHandlerClass.errorHandler
import com.whitecoats.clinicplus.viewmodels.CommonViewModel
import com.whitecoats.fragments.AppointmentFragment.Companion.lastHeaderDate
import com.whitecoats.fragments.PatientFragment.Companion.patientTabFlag
import com.zoho.salesiqembed.ZohoSalesIQ
import org.json.JSONException
import org.json.JSONObject
import java.util.*

class ConfirmOrderActivity : AppCompatActivity() {
    private lateinit var extraInfo: TextView
    private var serviceId = 0
    private var prodId = 0
    private var patientID = 0
    private var startTimeSlot: String? = null
    private var endTimeSlot: String? = null
    private lateinit var jsonValue: JSONObject
    private var appUtilities: AppUtilities? = null
    private var quickAppointmentFlag = 0
    private var selectedPaymentDuration = false
    private lateinit var paymentReminderCheckBox: CheckBox
    private lateinit var paymentReminderNote: TextView
    private var endSeprator: View? = null
    private var apiGetPostMethodCalls: ApiGetPostMethodCalls? = null
    private lateinit var dialog: Dialog
    private lateinit var commonViewModel: CommonViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_confirm_order)
        val toolbar = findViewById<Toolbar>(R.id.blockTimeToolbar)
        toolbar.title = resources.getString(R.string.order_summary_header)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        commonViewModel = ViewModelProvider(this@ConfirmOrderActivity)[CommonViewModel::class.java]
        @SuppressLint("UseCompatLoadingForDrawables") val upArrow =
            getDrawable(R.drawable.ic_arrow_back)
        upArrow!!.setColorFilter(
            ContextCompat.getColor(this, R.color.colorWhite),
            PorterDuff.Mode.SRC_ATOP
        )
        supportActionBar!!.setHomeAsUpIndicator(upArrow)
        apiGetPostMethodCalls = ApiGetPostMethodCalls()
        ZohoSalesIQ.Tracking.setPageTitle("On Book Appt Confirm Page")
        val docName = findViewById<TextView>(R.id.confirmDocNameTv)
        val serviceName = findViewById<TextView>(R.id.confirmServiceNameTv)
        val date1 = findViewById<TextView>(R.id.confirmDateTv)
        val time = findViewById<TextView>(R.id.confirmTimeTv)
        val feeAmount = findViewById<TextView>(R.id.confirmFeeRateTv)
        val totalAmount = findViewById<TextView>(R.id.confirmTotalRateTv)
        extraInfo = findViewById(R.id.confirmExtraTv)
        endSeprator = findViewById(R.id.endSeprator)
        val timeIcon = findViewById<ImageView>(R.id.confirmTimeIcon)
        val confirmServiceIcon = findViewById<ImageView>(R.id.confirmServiceIcon)
        appUtilities = AppUtilities()
        val confirmOrderPayBtn = findViewById<Button>(R.id.confirmOrderPayBtn)
        paymentReminderCheckBox = findViewById(R.id.paymentReminderCheckBox)
        paymentReminderNote = findViewById(R.id.paymentReminderNote)
        val paymentDurationSummeryLayoutCheckBox =
            findViewById<LinearLayout>(R.id.paymentDurationSummeryLayoutCheckBox)
        patientID = intent.getIntExtra("patientId", 0)
        checkSendPaymentGatwayStatus(patientID)
        if (intent.getIntExtra("appointment_service_id", 0) == 1) {
            paymentReminderNote.visibility = View.VISIBLE
        } else {
            paymentReminderNote.visibility = View.GONE
        }
        if (intent.getIntExtra("appointment_service_id", 0) == 3) {
            paymentDurationSummeryLayoutCheckBox.visibility = View.GONE
        } else {
            paymentDurationSummeryLayoutCheckBox.visibility = View.VISIBLE
        }
        paymentReminderCheckBox.setOnCheckedChangeListener { _: CompoundButton?, isChecked: Boolean ->
            selectedPaymentDuration = isChecked
        }
        if (intent.getIntExtra("appointment_service_id", 0) == 2) {
            feeAmount.text = intent.getIntExtra("service_price", 0).toString()
            docName.text = intent.getStringExtra("patientName")
            totalAmount.text = intent.getIntExtra("service_price", 0).toString()
            serviceName.text = intent.getStringExtra("service_name")
            date1.text = intent.getStringExtra("date")
            serviceId = intent.getIntExtra("service_id", 0)
            prodId = intent.getIntExtra("prod_id", 0)
            patientID = intent.getIntExtra("patientId", 0)
            startTimeSlot = intent.getStringExtra("service_alias_name")
            quickAppointmentFlag = intent.getIntExtra("quickAppointmentFlag", 0)
        } else if (intent.getIntExtra(
                "appointment_service_id",
                0
            ) == 1 || intent.getIntExtra("appointment_service_id", 0) == 3
        ) {
            feeAmount.text = intent.getIntExtra("price", 0).toString()
            docName.text = intent.getStringExtra("patientName")
            totalAmount.text = intent.getIntExtra("price", 0).toString()
            serviceName.text = intent.getStringExtra("slot_service")
            date1.text = intent.getStringExtra("date")
            serviceId = intent.getIntExtra("slot_service_id", 0)
            prodId = intent.getIntExtra("slot_prod_id", 0)
            patientID = intent.getIntExtra("patientId", 0)
            startTimeSlot = intent.getStringExtra("time_slot")
            endTimeSlot = intent.getStringExtra("end_time_slot")
            quickAppointmentFlag = intent.getIntExtra("quickAppointmentFlag", 0)
        } else {
            feeAmount.text = intent.getIntExtra("service_price", 0).toString()
            docName.text = intent.getStringExtra("patientName")
            totalAmount.text = intent.getIntExtra("service_price", 0).toString()
            serviceName.text = intent.getStringExtra("service_name")
            date1.text = intent.getStringExtra("date")
            serviceId = intent.getIntExtra("service_id", 0)
            prodId = intent.getIntExtra("prod_id", 0)
            patientID = intent.getIntExtra("patientId", 0)
            startTimeSlot = intent.getStringExtra("time_slot")
            endTimeSlot = intent.getStringExtra("end_time_slot")
            quickAppointmentFlag = intent.getIntExtra("quickAppointmentFlag", 0)
        }

        //setting text values
        if (intent.getIntExtra("appointment_service_id", 0) == 2) {
            if (intent.getStringExtra("service_alias_name") != null) {
                time.setText(R.string.chat)
                confirmServiceIcon.setImageResource(R.drawable.ic_chat)
                extraInfo.visibility = View.GONE
            } else {
                time.visibility = View.GONE
                timeIcon.visibility = View.GONE
            }
        } else if (intent.getIntExtra("appointment_service_id", 0) == 3) {
            val temp: String?
            if (intent.getStringExtra("time_slot") != null) {
                temp = intent.getStringExtra("time_slot")
                time.text = appUtilities!!.formatTimeBasedOnPreference(
                    this@ConfirmOrderActivity,
                    "HH:mm",
                    temp
                )
                confirmServiceIcon.setImageResource(R.drawable.ic_hospital)
                extraInfo.visibility = View.GONE
            } else {
                time.visibility = View.GONE
                timeIcon.visibility = View.GONE
            }
        } else if (intent.getIntExtra("appointment_service_id", 0) == 1) {
            val temp: String?
            if (intent.getStringExtra("time_slot") != null) {
                temp = intent.getStringExtra("time_slot")
                time.text = appUtilities!!.formatTimeBasedOnPreference(
                    this@ConfirmOrderActivity,
                    "HH:mm",
                    temp
                )
                confirmServiceIcon.setImageResource(R.drawable.ic_video)
                extraInfo.visibility = View.GONE
            } else {
                time.visibility = View.GONE
                timeIcon.visibility = View.GONE
            }
        } else {
            val temp: String?
            if (intent.getStringExtra("time_slot") != null) {
                temp = intent.getStringExtra("time_slot")
                val separated =
                    temp!!.split(" ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                time.text = appUtilities!!.formatTimeBasedOnPreference(
                    this@ConfirmOrderActivity,
                    "HH:mm",
                    separated[1]
                )
                confirmServiceIcon.setImageResource(R.drawable.ic_video)
                extraInfo.visibility = View.VISIBLE
                val msg =
                    "After booking consult, you will have a maximum of 10 minutes to join the video consult."
                extraInfo.text = msg
            } else {
                time.visibility = View.GONE
                timeIcon.visibility = View.GONE
            }
        }
        confirmOrderPayBtn.setOnClickListener { bookAppointment() }
    }

    fun bookAppointment() {
        showCustomProgressAlertDialog(
            resources.getString(R.string.please_wait),
            resources.getString(R.string.please_wait_loading_message)
        )
        val url = ApiUrls.bookAppointment
        try {
            val invoiceNumber = System.currentTimeMillis().toString()
            val date = appUtilities!!.changeDateFormat(
                "MMMM dd, yyyy",
                "yyyy-MM-dd",
                intent.getStringExtra("date")
            )
            if (intent.getIntExtra("appointment_service_id", 0) == 2) {
                jsonValue = JSONObject()
                jsonValue.put("patientId", patientID)
                jsonValue.put("prodId", prodId)
                jsonValue.put("servId", serviceId)
                jsonValue.put("invoice_no", invoiceNumber)
                jsonValue.put("send_payment_notification", selectedPaymentDuration)
            } else if (intent.getIntExtra(
                    "appointment_service_id",
                    0
                ) == 1 || intent.getIntExtra("appointment_service_id", 0) == 3
            ) {
                jsonValue = JSONObject()
                jsonValue.put("endSlot", "$date $endTimeSlot:00")
                jsonValue.put("patientId", patientID)
                jsonValue.put("prodId", prodId)
                jsonValue.put("servId", serviceId)
                jsonValue.put("startSlot", "$date $startTimeSlot:00")
                jsonValue.put("invoice_no", invoiceNumber)
                jsonValue.put("send_payment_notification", selectedPaymentDuration)
            } else {
                jsonValue = JSONObject()
                jsonValue.put("endSlot", endTimeSlot)
                jsonValue.put("patientId", patientID)
                jsonValue.put("prodId", prodId)
                jsonValue.put("servId", serviceId)
                jsonValue.put("startSlot", startTimeSlot)
                jsonValue.put("invoice_no", invoiceNumber)
                jsonValue.put("send_payment_notification", selectedPaymentDuration)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        commonViewModel.commonViewModelCall(url, jsonValue, Method.POST).observe(
            this@ConfirmOrderActivity
        ) { result ->
            try {
                //Process os success response
                dialog.dismiss()
                val responseObj = JSONObject(result)
                if (responseObj.getInt("status_code") == 200) {
                    val response = responseObj.optJSONObject("response")
                    val createMessageResponse = response!!.getString("response")
                    if (createMessageResponse.equals("success", ignoreCase = true)) {
                        val doctorDetailsIntent = Intent()
                        doctorDetailsIntent.action = "Call_Doctor_Details_API"
                        sendBroadcast(doctorDetailsIntent)
                        MyClinicGlobalClass.logUserActionEvent(
                            ApiUrls.doctorId, getString(
                                R.string.ApptBookNowSuccess
                            ), null
                        )
                        ApiUrls.bottomNaviType = 2
                        lastHeaderDate = ""
                        finish()
                        val intent = Intent()
                        // Here you can also put data on intent
                        intent.action = "UPDATE_FRAG_LIST"
                        intent.putExtra("isAppointmentNew", true)
                        sendBroadcast(intent)
                        DashboardFullMode.isAppointmentListRefreshReq = true
                        if (getIntent().getIntExtra("appointment_service_id", 0) == 2) {
                            confirmOrderFlagChat = 1
                            patientTabFlag = 0
                            ApiUrls.bottomNaviType = 3
                            com.whitecoats.clinicplus.utils.AppUtilities()
                                .selectBottomNavigationScreen("Chats")
                        } else {
                            confirmOrderFlag = 1
                            ApiUrls.bottomNaviType = 2
                            patientTabFlag = 0
                            com.whitecoats.clinicplus.utils.AppUtilities()
                                .selectBottomNavigationScreen("Appointments")
                        }
                        if (quickAppointmentFlag == 1) {
                            BookAppointmentActivity.quickAppointmentFlag = 2
                        }
                    }
                    if (intent.getStringExtra("followUpApptId") != null) {
                        updateFollowUp()
                    }
                } else {
                    dialog.dismiss()
                    errorHandler(this@ConfirmOrderActivity, result)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // handle arrow click here
        if (item.itemId == android.R.id.home) {
            finish()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun updateFollowUp() {
        val url = ApiUrls.updateFollowUpSubmissionRow
        val date1 = appUtilities!!.changeDateFormat(
            "MMMM dd, yyyy",
            "yyyy-MM-dd",
            intent.getStringExtra("date")
        )
        val followUpId = intent.getStringExtra("followUpApptId")
        try {
            jsonValue = JSONObject()
            jsonValue.put("appt_datetime", "$date1 $startTimeSlot:00")
            jsonValue.put("id", followUpId!!.toInt())
        } catch (e: Exception) {
            e.printStackTrace()
        }

        commonViewModel.commonViewModelCall(url, jsonValue, Method.POST).observe(
            this@ConfirmOrderActivity
        ) { result ->
            Log.d("resultValue", "resultValue$result")
            val response = JSONObject(result)
            if (response.getInt("status_code") == 200) {
                val intent = Intent()
                // Here you can also put data on intent
                intent.action = "DASHBOARD_FOLLOWUP_LIST"
                sendBroadcast(intent)
                DashboardFullMode.isFollowUpListRefreshReq = true
            } else {
                errorHandler(this@ConfirmOrderActivity, result)
            }
        }
    }

    fun trimMessage(json: String?, key: String?): String? {
        val trimmedString: String = try {
            val obj = JSONObject(json!!)
            obj.getString(key!!)
        } catch (e: JSONException) {
            e.printStackTrace()
            return null
        }
        return trimmedString
    }

    //Somewhere that has access to a context
    fun displayMessage(toastString: String?) {
        Toast.makeText(applicationContext, toastString, Toast.LENGTH_LONG).show()
    }

    private fun checkSendPaymentGatwayStatus(patient_ID: Int) {
        showCustomProgressAlertDialog("", resources.getString(R.string.please_wait))
        val url = ApiUrls.checkSendPaymentStatus + "?patient_id=" + patient_ID


        commonViewModel.commonViewModelCall(url, JSONObject(), Method.GET).observe(
            this@ConfirmOrderActivity
        ) { result ->
            try {
                //Process os success response
                dialog.dismiss()
                val responseObj = JSONObject(result)
                if (responseObj.getInt("status_code") == 200) {
                    val response = responseObj.optJSONObject("response")
                    val paymentStatusValue = response!!.getInt("response")
                    val paymentStatusMessage = response.optString("message")
                    if (paymentStatusValue == 0) {
                        paymentReminderCheckBox.isEnabled = false
                        paymentReminderCheckBox.setTextColor(Color.parseColor("#ACACAC"))
                        val payRem = "Note: $paymentStatusMessage"
                        paymentReminderNote.text = payRem
                        paymentReminderNote.setTextColor(
                            ContextCompat.getColor(
                                this@ConfirmOrderActivity,
                                R.color.colorRed
                            )
                        )
                        if (intent.getIntExtra("appointment_service_id", 0) == 2) {
                            paymentReminderNote.visibility = View.VISIBLE
                        }
                        if (extraInfo.visibility == View.VISIBLE) {
                            endSeprator!!.visibility = View.VISIBLE
                            paymentReminderNote.visibility = View.VISIBLE
                        }
                    } else {
                        paymentReminderCheckBox.isEnabled = true
                        paymentReminderCheckBox.setTextColor(Color.parseColor("#000000"))
                        val msg =
                            "Note: Your patient needs to complete the payment before consultation time"
                        paymentReminderNote.text = msg
                        paymentReminderNote.setTextColor(Color.parseColor("#000000"))
                    }
                } else {
                    dialog.dismiss()
                    errorHandler(this@ConfirmOrderActivity, result)
                }
            } catch (e: JSONException) {
                e.printStackTrace()
                dialog.dismiss()
            }
        }
    }

    private fun showCustomProgressAlertDialog(title: String, progressVal: String) {
        val builder = AlertDialog.Builder(this@ConfirmOrderActivity)
        builder.setTitle(title)
        builder.setCancelable(false)
        val customLayout: View = layoutInflater.inflate(R.layout.custom_progress_bar, null)
        builder.setView(customLayout)
        dialog = builder.create()
        val tvBody = customLayout.findViewById<TextView>(R.id.tv_value)
        tvBody.text = progressVal
        dialog.show()
    }


    companion object {
        @JvmField
        var confirmOrderFlag = 0

        @JvmField
        var confirmOrderFlagChat = 0
        var followUpDate = ""
    }
}