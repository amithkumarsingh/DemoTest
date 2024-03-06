package com.whitecoats.clinicplus.activities

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.View.OnFocusChangeListener
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.whitecoats.clinicplus.*
import com.whitecoats.clinicplus.apis.ApiUrls
import com.whitecoats.clinicplus.fragments.ApptDetailsFragment
import com.whitecoats.clinicplus.utils.ErrorHandlerClass.errorHandler
import com.whitecoats.clinicplus.viewmodels.AppointmentDetailsViewModel
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class AppointCompletedActivity : AppCompatActivity() {
    private var orderIdAppointment = 0
    private var preTextAmount = 0f
    private var preTextDiscount = 0f
    private var preTextTotal = 0f
    private var totalTax = 0f
    private var invoiceTotalAmount = 0f
    private var postTaxDiscount = 0f
    private var invoiceGrandAmount = 0f
    private var invoice_grand_discount = 0f
    private var invoice_grand_pre_tax = 0f
    private var invoice_grand_tax = 0f
    private var invoice_grand_total = 0f
    private var invoiceServiceArray: JSONArray? = null
    private var scheduleStartTime: String? = null
    private var appointmentPostion = 0
    private var globalClass: MyClinicGlobalClass? = null
    private var attendanceLayoutHideShow: RelativeLayout? = null
    private var addProcedureLayout: RelativeLayout? = null
    private var totalPayLayout: RelativeLayout? = null
    private var startConsultationButton: TextView? = null
    private var appointmentDetailsBottomLayout: RelativeLayout? = null
    private var apptStatusLayout: RelativeLayout? = null
    private var appointmentStatusValue: TextView? = null
    private var attendanceTextLayout: RelativeLayout? = null
    private var attendanceLayout: RelativeLayout? = null
    private var cancelApptLayout: RelativeLayout? = null
    var radioGroup: RadioGroup? = null
    private var discountRemoveText: TextView? = null
    private var appointmentOrderAmount = 0
    private var appointmentOrderAmountDiscount = 0
    private var appointmentNetAmount = 0
    private var decimalValue: TextView? = null
    private var patientRecordsApi: PatientRecordsApi? = null
    private var taxesApplyValueText = 0f
    private var texesResponseArray: JSONArray? = null
    private var taxesTextView: TextView? = null
    private var taxesName: String? = null
    private var taxesPercent = 0
    private var attendanceAt: TextView? = null
    private var totalPayValue: TextView? = null
    private var checkInLayout: RelativeLayout? = null
    private var payModeLayout: RelativeLayout? = null
    private var appointmentTypeSpinner: Spinner? = null
    private var paymentModeSpinner: Spinner? = null
    var paymentMode = arrayOf("Select Mode", "Cash", "Credit Card", "Debit Card", "Net Banking")
    private var appointmentDetailsViewModel: AppointmentDetailsViewModel? = null
    private var appointmentDetailsProgressbar: RelativeLayout? = null
    private var totalAmountEditText: EditText? = null
    private var gstValue: TextView? = null
    private var netAmount: TextView? = null
    private var discount: TextView? = null
    private var consultationAmount: TextView? = null
    private var amountPaidLayout: RelativeLayout? = null
    private var confirmButton: RelativeLayout? = null
    private var addProcedureAppointmentDetails: TextView? = null
    private lateinit var toolBarText: TextView
    private lateinit var apptDetailsBack: ImageButton
    private var backToHomeLayout: RelativeLayout? = null
    private var ToNextConsultLayout: RelativeLayout? = null
    private var followupButton: RelativeLayout? = null
    private var appointmentOrderPaymentStatus: String? = null
    private lateinit var intentObj: Intent
    var appointmentID = 0
    private var appointmentOrderId = 0
    var appointmentMode = 0
    var productId = 0
    var patientId = 0
    private var appointmentPosition = 0
    var appointmentDate: String? = null
    var appointmentTime: String? = null
    var patientName: String? = null
    var scheduleTime: String? = null
    private var invoiceUrlComplete = ""
    private var flagCreateInvoiceInComplete = false
    @SuppressLint("MissingInflatedId", "SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.appoint_completed_layout)
        appointmentDetailsViewModel = ViewModelProvider(this)[AppointmentDetailsViewModel::class.java]
        appointmentDetailsViewModel!!.init()
        patientRecordsApi = PatientRecordsApi()
        intentObj = intent
        apptDetailsBack = findViewById(R.id.appt_details_back)
        toolBarText = findViewById(R.id.toobar_patient_name)
        appointmentID = intentObj.getIntExtra("apptID", 0)
        appointmentOrderId = intentObj.getIntExtra("orderId", 0)
        appointmentMode = intentObj.getIntExtra("apptMode", 0)
        appointmentDate = intentObj.getStringExtra("appointmentDate")
        appointmentTime = intentObj.getStringExtra("appointmentTime")
        patientId = intentObj.getIntExtra("patientId", 0)
        patientName = intentObj.getStringExtra("patientName")
        productId = intentObj.getIntExtra("procedureId", 0)
        scheduleTime = intentObj.getStringExtra("scheduletime")
        appointmentPosition = intentObj.getIntExtra("appointmentPosition", 0)
        toolBarText.text = intentObj.getStringExtra("patientName")
        confirmButton = findViewById(R.id.confirmButton)
        cancelApptLayout = findViewById(R.id.cancelApptLayout)
        apptStatusLayout = findViewById(R.id.apptStatusLayout)
        appointmentStatusValue = findViewById(R.id.apptStatusValue)
        attendanceTextLayout = findViewById(R.id.attendanceTextLayout)
        attendanceLayout = findViewById(R.id.attendanceLayout)
        attendanceAt = findViewById(R.id.attendanceAt)
        checkInLayout = findViewById(R.id.checkInLayout)
        payModeLayout = findViewById(R.id.payModeLayout)
        appointmentTypeSpinner = findViewById(R.id.appointmentTypeSpinner)
        paymentModeSpinner = findViewById(R.id.paymentModeSpinner)
        appointmentDetailsProgressbar = findViewById(R.id.appointmentDetailsProgressbar)
        totalAmountEditText = findViewById(R.id.totalAmountEditText)
        gstValue = findViewById(R.id.gstValue)
        netAmount = findViewById(R.id.netAmount)
        discount = findViewById(R.id.discount)
        taxesTextView = findViewById(R.id.taxesTextView)
        discountRemoveText = findViewById(R.id.discountRemoveText)
        decimalValue = findViewById(R.id.decimalValue)
        consultationAmount = findViewById(R.id.consultationAmount)
        totalPayValue = findViewById(R.id.totalPayValue)
        amountPaidLayout = findViewById(R.id.amountPaidLayout)
        addProcedureAppointmentDetails = findViewById(R.id.addProcedure)
        globalClass = this.applicationContext as MyClinicGlobalClass
        appointmentDetailsBottomLayout = findViewById(R.id.appointmentDetailsBottomLayout)
        attendanceLayoutHideShow = findViewById(R.id.attendanceLayoutHideShow)
        addProcedureLayout = findViewById(R.id.addProcedureLayout)
        totalPayLayout = findViewById(R.id.totalPayLayout)
        startConsultationButton = findViewById(R.id.startConsultationButton)
        MyClinicGlobalClass.logUserActionEvent(
            ApiUrls.doctorId, getString(
                R.string.AppointmentDetailsPaymentConfirm
            ), null
        )
        MyClinicGlobalClass.logUserActionEvent(
            ApiUrls.doctorId, getString(
                R.string.AppointmentCompletedScreenImpressions
            ), null
        )
        apptDetailsBack.setOnClickListener { finish() }
        val howManyDaysDateText = findViewById<TextView>(R.id.howManyDaysDateText)
        backToHomeLayout = findViewById<View>(R.id.backToHomeLayout) as RelativeLayout
        ToNextConsultLayout = findViewById<View>(R.id.ToCreateInvoiceLayout) as RelativeLayout
        followupButton = findViewById<View>(R.id.followupButton) as RelativeLayout
        val followUpDayEditText = findViewById<View>(R.id.followUpDayEditText) as EditText
        val formattedDate = AppUtilities().changeDateFormat(
            "yyyy-MM-dd HH:mm:ss",
            "dd MMM, yyyy",
            scheduleStartTime
        )
        howManyDaysDateText.text = "After how many days from $formattedDate"
        followUpDayEditText.onFocusChangeListener = OnFocusChangeListener { _, _ ->
            MyClinicGlobalClass.logUserActionEvent(
                ApiUrls.doctorId, getString(
                    R.string.AppointmentCompletedFollowUp
                ), null
            )
        }
        backToHomeLayout!!.setOnClickListener {
            MyClinicGlobalClass.logUserActionEvent(
                ApiUrls.doctorId, getString(
                    R.string.AppointmentCompletedBackToHome
                ), null
            )
            finish()
        }
        ToNextConsultLayout!!.setOnClickListener {
            flagCreateInvoiceInComplete = true
            val intent =
                Intent(this@AppointCompletedActivity, AppointmentDetailsActivity::class.java)
            intent.putExtra("apptID", appointmentID)
            intent.putExtra("orderId", appointmentOrderId)
            intent.putExtra("apptMode", appointmentMode)
            intent.putExtra("procedureId", productId)
            intent.putExtra("patientName", patientName)
            intent.putExtra("patientId", patientId)
            intent.putExtra("type", "payment")
            intent.putExtra("scheduletime", scheduleTime)
            intent.putExtra("appointmentTime", appointmentTime)
            intent.putExtra("appointmentPosition", appointmentPostion)
            intent.putExtra("invoiceURLComplete", invoiceUrlComplete)
            intent.putExtra("flagCreateInvoiceInComplete", flagCreateInvoiceInComplete)
            startActivity(intent)
        }
        followupButton!!.setOnClickListener(View.OnClickListener { view ->
            if (followUpDayEditText.text.toString().isEmpty()) {
                Toast.makeText(view.context, "You did not enter any day", Toast.LENGTH_SHORT).show()
                return@OnClickListener
            }
            val params = JSONObject()
            try {
                params.put("appointment_id", appointmentID)
                params.put("is_do_follow_up", 0)
                params.put("follow_up_days", followUpDayEditText.text.toString().toInt())
                sendTheFollowUp(this@AppointCompletedActivity, params)
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        })
        getServicesForAppointmentData(this@AppointCompletedActivity, appointmentID)
        completedAppt(appointmentID, 3)
    }

    fun createInvoice(activity: Activity?, params: JSONObject?) {
        appointmentDetailsViewModel!!.creteInvoice(activity, params)
            .observe(this@AppointCompletedActivity) { s ->
                try {
                    val response = JSONObject(s)
                    if (response.getInt("status_code") == 200) {
                        val invoiceResponse =
                            response.getJSONObject("response").getJSONObject("response")
                        invoiceUrlComplete = invoiceResponse.getString("file_path")
                        Log.d("invoiceUrl", "invoiceUrl$invoiceUrlComplete")
                        Toast.makeText(
                            applicationContext,
                            "Invoice has been generated",
                            Toast.LENGTH_LONG
                        ).show()
                        flagCreateInvoiceInComplete = true
                        val intent = Intent(
                            this@AppointCompletedActivity,
                            AppointmentDetailsActivity::class.java
                        )
                        intent.putExtra("apptID", appointmentID)
                        intent.putExtra("orderId", appointmentOrderId)
                        intent.putExtra("apptMode", appointmentMode)
                        intent.putExtra("procedureId", productId)
                        intent.putExtra("patientName", patientName)
                        intent.putExtra("patientId", patientId)
                        intent.putExtra("type", "payment")
                        intent.putExtra("scheduletime", scheduleTime)
                        intent.putExtra("appointmentTime", appointmentTime)
                        intent.putExtra("appointmentPosition", appointmentPostion)
                        intent.putExtra("invoiceURLComplete", invoiceUrlComplete)
                        intent.putExtra(
                            "flagCreateInvoiceInComplete",
                            flagCreateInvoiceInComplete
                        )
                        startActivity(intent)
                    } else {
                        errorHandler(this@AppointCompletedActivity, s)
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }
    }

    private fun completedAppt(apptId: Int, status: Int) {
        val reqObj = JSONObject()
        try {
            reqObj.put("apptId", apptId)
            reqObj.put("status", status)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        patientRecordsApi!!.postRecords(
            ApiUrls.updateClinicAppt,
            reqObj,
            this@AppointCompletedActivity,
            object : VolleyCallback {
                override fun onSuccess(result: String) {
                    Log.d("completed", "completed$result")
                    val intent = Intent()
                    // Here you can also put data on intent
                    intent.action = "APPOINTMENT_COMPLETED"
                    intent.putExtra("apptID", apptId)
                    intent.putExtra("appointmentDate", appointmentDate)
                    sendBroadcast(intent)
                }

                override fun onError(err: String) {
                    Log.d("completedError", "completedError$err")
                    //                loader.dismiss();
                  //  val error = err
                    val intent = Intent()
                    // Here you can also put data on intent
                    intent.action = "APPOINTMENT_COMPLETED"
                    intent.putExtra("apptID", apptId)
                    intent.putExtra("appointmentDate", appointmentDate)
                    sendBroadcast(intent)
                    errorHandler(this@AppointCompletedActivity, err)
                }
            })
    }

    private fun sendTheFollowUp(activity: Activity?, params: JSONObject?) {
        appointmentDetailsViewModel!!.sendFollowUp(activity, params)
            .observe(this@AppointCompletedActivity) { s ->
                try {
                    MyClinicGlobalClass.logUserActionEvent(
                        ApiUrls.doctorId, getString(
                            R.string.AppointmentCompletedFollowUp
                        ), null
                    )
                    val response = JSONObject(s)
                    Log.d("FollowUpResponse", "FollowUpResponse$response")
                    if (response.getInt("status_code") == 200) {
                        Toast.makeText(
                            this@AppointCompletedActivity,
                            "Updated Successfully.",
                            Toast.LENGTH_LONG
                        ).show()
                    } else {
                        errorHandler(this@AppointCompletedActivity, s)
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }
    }

    private fun getServicesForAppointmentData(activity: Activity?, appointmentID: Int) {
        appointmentDetailsViewModel!!.getServicesForAppointmentData(activity, appointmentID)
            .observe(this) { s ->
                Log.i("capture notes res", s)
                try {
                    val response = JSONObject(s)
                    if (response.getInt("status_code") == 200) {
                        val resObject =
                            response.getJSONObject("response").getJSONObject("response")
                        invoiceServiceArray = resObject.getJSONArray("invoice_services")
                        val appointmentDataObject = resObject.getJSONObject("appt_data")
                       // val apptStatus = appointmentDataObject.getInt("status")
                        scheduleStartTime =
                            appointmentDataObject.getString("scheduled_start_time")
                       // val appointmentType = appointmentDataObject.getInt("appt_type")
                       // val checkedInStatus = appointmentDataObject.getInt("checked_in")
                       /* if (checkedInStatus == 1) {
                           // val checkedInAt = appointmentDataObject.getString("checked_in_at")
                          *//*  val separatedInteractionDate =
                                checkedInAt.split(" ".toRegex()).dropLastWhile { it.isEmpty() }
                                    .toTypedArray()*//*
                          //  val createInteractionDate = separatedInteractionDate[0]
                          *//*  val interactionDate = AppUtilities().changeDateFormat(
                                "yyyy-MM-dd",
                                "dd MMM, yy",
                                createInteractionDate
                            )*//*
                           *//* val createdInteractionTime = separatedInteractionDate[1].substring(
                                0,
                                separatedInteractionDate[1].length - 3
                            )*//*
                        }*/
                        val appointmentOrderObject =
                            appointmentDataObject.getJSONObject("order")
                        val product_id = appointmentOrderObject.getInt("product_id")
                        appointmentOrderPaymentStatus =
                            appointmentOrderObject.getString("payment_status")
                        appointmentOrderAmount = appointmentOrderObject.getInt("order_amt")
                        appointmentOrderAmountDiscount =
                            appointmentOrderObject.getInt("discount")
                        appointmentNetAmount = appointmentOrderObject.getInt("net_amount")
                        val getJsonAppointmentOrderObject =
                            appointmentDataObject.getJSONObject("order")
                        orderIdAppointment = getJsonAppointmentOrderObject.getInt("id")
                        if (!getJsonAppointmentOrderObject.isNull("pre_tax_amount")) {
                            preTextAmount =
                                getJsonAppointmentOrderObject.getInt("pre_tax_amount").toFloat()
                        }
                        if (!getJsonAppointmentOrderObject.isNull("pre_tax_discount")) {
                            preTextDiscount =
                                getJsonAppointmentOrderObject.getInt("pre_tax_discount")
                                    .toFloat()
                        }
                        if (!getJsonAppointmentOrderObject.isNull("pre_tax_total")) {
                            preTextTotal =
                                getJsonAppointmentOrderObject.getInt("pre_tax_total").toFloat()
                        }
                        if (!getJsonAppointmentOrderObject.isNull("total_tax")) {
                            totalTax =
                                getJsonAppointmentOrderObject.getInt("total_tax").toFloat()
                        }
                        if (!getJsonAppointmentOrderObject.isNull("invoice_total_amount")) {
                            invoiceTotalAmount =
                                getJsonAppointmentOrderObject.getInt("invoice_total_amount")
                                    .toFloat()
                        }
                        if (!getJsonAppointmentOrderObject.isNull("net_total_discount")) {
                            postTaxDiscount =
                                getJsonAppointmentOrderObject.getInt("net_total_discount")
                                    .toFloat()
                        }
                        if (!getJsonAppointmentOrderObject.isNull("pre_tax_amount")) {
                            invoiceGrandAmount =
                                getJsonAppointmentOrderObject.getInt("pre_tax_amount").toFloat()
                        }
                        if (!getJsonAppointmentOrderObject.isNull("pre_tax_discount")) {
                            invoice_grand_discount =
                                getJsonAppointmentOrderObject.getInt("pre_tax_discount")
                                    .toFloat()
                        }
                        if (!getJsonAppointmentOrderObject.isNull("pre_tax_total")) {
                            invoice_grand_pre_tax =
                                getJsonAppointmentOrderObject.getInt("pre_tax_total").toFloat()
                        }
                        if (!getJsonAppointmentOrderObject.isNull("total_tax")) {
                            invoice_grand_tax =
                                getJsonAppointmentOrderObject.getInt("total_tax").toFloat()
                        }
                        if (!getJsonAppointmentOrderObject.isNull("pre_tax_total")) {
                            invoice_grand_total =
                                getJsonAppointmentOrderObject.getInt("pre_tax_total").toFloat()
                        }
                        getProductTaxe(product_id)
                    } else {
                        errorHandler(this@AppointCompletedActivity, s)
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                    Log.d("parserError:", "parserError:$e")
                }
            }
    }

    private fun getProductTaxe(product_id: Int) {
        appointmentDetailsViewModel!!.getProductTaxes(this@AppointCompletedActivity, product_id)
            .observe(this) { s ->
                try {
                    val jsonObject = JSONObject(s)
                    if (jsonObject.getInt("status_code") == 200) {
                        val response = jsonObject.getJSONObject("response")
                        texesResponseArray = response.getJSONArray("response")
                        if (texesResponseArray != null) {
                            Log.d("TaxesInformation ", "TaxesInformation $response")
                            val taxesPercentObject = texesResponseArray!!.getJSONObject(0)
                            taxesPercent = taxesPercentObject.getInt("percentage")
                            //                        Log.d("TaxesInformationP ", "TaxesInformationP " + taxesPercent);
                            taxesName = taxesPercentObject.getString("name")
                            taxesApplyValueText =
                                java.lang.Float.valueOf(taxesPercent.toFloat())
                            val taxesAmountAfterCalculateText =
                                appointmentNetAmount.toFloat() / 100 * taxesApplyValueText
                            val consultationAmountValue =
                                appointmentOrderAmount - taxesAmountAfterCalculateText
                            val discountApplyValue =
                                java.lang.Float.valueOf(appointmentOrderAmountDiscount.toFloat())
                            val discountAmountAfterCalculate =
                                appointmentNetAmount.toFloat() / 100 * discountApplyValue
                            preTextAmount =
                                appointmentOrderAmount - taxesAmountAfterCalculateText
                            preTextDiscount = discountAmountAfterCalculate
                            preTextTotal = consultationAmountValue - discountAmountAfterCalculate
                            totalTax = taxesAmountAfterCalculateText
                            invoiceTotalAmount =
                                consultationAmountValue + taxesAmountAfterCalculateText
                            postTaxDiscount = discountAmountAfterCalculate
                            invoiceGrandAmount =
                                appointmentOrderAmount - taxesAmountAfterCalculateText
                            invoice_grand_discount = discountAmountAfterCalculate
                            invoice_grand_pre_tax =
                                consultationAmountValue - discountAmountAfterCalculate
                            invoice_grand_tax = taxesAmountAfterCalculateText
                            invoice_grand_total =
                                consultationAmountValue + taxesAmountAfterCalculateText
                        }
                    } else {
                        errorHandler(this@AppointCompletedActivity, s)
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }
    }

    override fun onResume() {
        super.onResume()
        if (ApptDetailsFragment.appointPage) {
            ApptDetailsFragment.appointPage = false
            finish()
        }
    }
}