package com.whitecoats.clinicplus.activities

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.PorterDuff
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request.Method
import com.whitecoats.clinicplus.*
import com.whitecoats.clinicplus.adapters.PaymentTimeLineAdapter
import com.whitecoats.clinicplus.apis.ApiGetPostMethodCalls
import com.whitecoats.clinicplus.apis.ApiUrls
import com.whitecoats.clinicplus.fragments.ApptDetailsFragment
import com.whitecoats.clinicplus.fragments.NewAppointmentFragment.Companion.activePastFilter
import com.whitecoats.clinicplus.models.AddProcedureListModel
import com.whitecoats.clinicplus.models.PaymentTimeLineModel
import com.whitecoats.clinicplus.utils.ErrorHandlerClass.errorHandler
import com.whitecoats.clinicplus.viewmodels.AppointmentDetailsViewModel
import com.whitecoats.clinicplus.viewmodels.CommonViewModel
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.text.MessageFormat
import java.util.*

class PaymentHistoryTimeLineActivity : AppCompatActivity() {
    private lateinit var intentObj: Intent
    private var appointmentID = 0
    private var orderId = 0
    private var refundAmount = 0
    private var appointmentDetailsViewModel: AppointmentDetailsViewModel? = null
    private var paymentTimeLineAdapter: PaymentTimeLineAdapter? = null
    private var paymentTimeLineList = ArrayList<PaymentTimeLineModel>()
    private lateinit var apptBookedInfo: TextView
    private lateinit var apptStatusInfo: TextView
    private lateinit var createReceiptText: TextView
    private lateinit var createInvoiceText: TextView
    private lateinit var createInvoiceLayout: RelativeLayout
    var invoiceUrl: String? = ""
    private var patientRecordsApi: PatientRecordsApi? = null
    private var flagCreateInvoiceInComplete = false
    private var invoiceUrlCompletedId: String? = null
    private var invoiceData: String? = null
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
    private var invoiceServiceArrayAppended: JSONArray? = null
    private var appointmentOrderPaymentStatus: String? = null
    private var appointmentOrderAmount = 0
    private var appointmentOrderAmountDiscount = 0
    private var appointmentNetAmount = 0
    private var discountArrayList: ArrayList<Int>? = null
    private var addProcedureListModel = ArrayList<AddProcedureListModel>()
    private var taxesApplyValueText = 0f
    private var texesResponseArray: JSONArray? = null
    var receiptUrl: String? = null
    private var taxesPercent = 0
    private var yesButton: RelativeLayout? = null
    private var noButton: RelativeLayout? = null
    private var closeButton: RelativeLayout? = null
    private var refundTextOne: TextView? = null
    private var refundTextTwo: TextView? = null
    private var refundTextThree: TextView? = null
    private var appointmentApiRequests: AppointmentApiRequests? = null
    private var globalApiCall: ApiGetPostMethodCalls? = null
    private lateinit var dialog: Dialog
    private lateinit var commonViewModel: CommonViewModel

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.payment_history_timeline_activity)
        appointmentDetailsViewModel =
            ViewModelProvider(this)[AppointmentDetailsViewModel::class.java]
        appointmentDetailsViewModel!!.init()
        intentObj = intent
        globalApiCall = ApiGetPostMethodCalls()
        commonViewModel =
            ViewModelProvider(this@PaymentHistoryTimeLineActivity)[CommonViewModel::class.java]
        appointmentID = intentObj.getIntExtra("apptID", 0)
        orderId = intentObj.getIntExtra("orderId", 0)
        refundAmount = intentObj.getIntExtra("refundAmt", 0)
        receiptUrl = intentObj.getStringExtra("receiptUrl")
        invoiceData = intentObj.getStringExtra("invoiceData")
        val toolbar = findViewById<Toolbar>(R.id.addPatientToolbar)
        toolbar.title = "Payment Timeline"
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        val backArrow = ContextCompat.getDrawable(this, R.drawable.ic_arrow_back)
        if (backArrow != null) {
            backArrow.setColorFilter(
                ContextCompat.getColor(this, R.color.colorWhite),
                PorterDuff.Mode.SRC_ATOP
            )
            supportActionBar!!.setHomeAsUpIndicator(backArrow)
        }
        discountArrayList = ArrayList()
        patientRecordsApi = PatientRecordsApi()
        appointmentApiRequests = AppointmentApiRequests()
        getPaymentTimeline(this, appointmentID)
        apptBookedInfo = findViewById(R.id.apptBookedInfo)
        apptStatusInfo = findViewById(R.id.apptStatusInfo)
        createReceiptText = findViewById(R.id.createReceiptText)
        createInvoiceText = findViewById(R.id.createInvoiceText)
        val createReceiptLayout = findViewById<RelativeLayout>(R.id.createReceiptLayout)
        createInvoiceLayout = findViewById(R.id.createInvoiceLayout)
        if (flagCreateInvoiceInComplete) {
            if (invoiceUrlCompletedId!!.isEmpty()) {
                createInvoiceText.text = "Create Invoice"
            } else {
                createInvoiceText.text = "View Invoice"
                invoiceUrl = invoiceUrlCompletedId
            }
        } else {
            if (invoiceData != null && invoiceData!!.isNotEmpty()) {
                createInvoiceText.text = "View Invoice"
                invoiceUrl = invoiceData
            } else {
                createInvoiceText.text = "Create Invoice"
            }
        }
        if (receiptUrl != null && receiptUrl!!.isNotEmpty()) {
            createReceiptText.text = "View Receipt"
        } else {
            createReceiptText.text = "Create Receipt"
        }
        val paymentTimeLineRecyclerView = findViewById<View>(R.id.recycler_view) as RecyclerView
        paymentTimeLineAdapter =
            PaymentTimeLineAdapter(this, paymentTimeLineList) { _, url, actionType ->
                if (actionType.equals("cancelRefund", ignoreCase = true)) {
                    cancelRefundPopup(orderId)
                } else if (actionType.equals("toPayUPortal", ignoreCase = true)) {
                    val uri = Uri.parse(url) // missing 'http://' will cause crashed
                    val intent = Intent(Intent.ACTION_VIEW, uri)
                    startActivity(intent)
                }
            }
        val layoutManager = LinearLayoutManager(applicationContext)
        layoutManager.orientation = LinearLayoutManager.VERTICAL
        paymentTimeLineRecyclerView.layoutManager = layoutManager
        paymentTimeLineRecyclerView.adapter = paymentTimeLineAdapter
        createInvoiceLayout.setOnClickListener {
            if (createInvoiceText.text.toString().equals("View Invoice", ignoreCase = true)) {
                getFileFromUrl(invoiceUrl)
            } else {
                val params = JSONObject()
                try {
                    var totalProcedureAmount = 0
                    var totalProcedureTaxesAmount = 0f
                    var finalProcedureAmount = 0f
                    if(invoiceServiceArrayAppended!=null) {
                        if (invoiceServiceArrayAppended!!.length() > 0) {
                            for (i in 0 until invoiceServiceArrayAppended!!.length()) {
                                val invoiceArrayObject =
                                    invoiceServiceArrayAppended!!.getJSONObject(i)
                                val getProcedureAmount = invoiceArrayObject.getInt("pre_tax_amount")
                                totalProcedureAmount += getProcedureAmount
                                var totalPercent = 0
                                for (j in 0 until invoiceArrayObject.getJSONArray("taxes")
                                    .length()) {
                                    val percentObject =
                                        invoiceArrayObject.getJSONArray("taxes").getJSONObject(j)
                                    val taxPercentage = percentObject.getInt("percentage")
                                    totalPercent = totalPercent + taxPercentage
                                }
                                val totalPercentTax = totalPercent.toFloat()
                                val taxesAmountAfterCalculateText =
                                    invoiceArrayObject.getInt("pre_tax_amount")
                                        .toFloat() / 100 * totalPercentTax
                                totalProcedureTaxesAmount += taxesAmountAfterCalculateText
                                val finalPostTaxAmount =
                                    invoiceArrayObject.getInt("pre_tax_amount") + taxesAmountAfterCalculateText
                                finalProcedureAmount += finalPostTaxAmount
                            }
                        }
                    }
                    params.put("order_id", orderIdAppointment)
                    params.put("pre_tax_amount", preTextAmount.toDouble())
                    params.put("pre_tax_discount", preTextDiscount.toDouble())
                    params.put("pre_tax_total", preTextTotal.toDouble())
                    params.put("total_tax", totalTax.toDouble())
                    if (texesResponseArray != null) {
                        if (texesResponseArray!!.length() > 0) {
                            params.put("taxes", texesResponseArray)
                        } else {
                            val arrayTexes = JSONArray()
                            params.put("taxes", arrayTexes)
                        }
                    }
                    params.put("invoice_total_amount", invoiceTotalAmount.toDouble())
                    params.put("post_tax_discount", postTaxDiscount.toDouble())
                    params.put("isConsultaionForInvoice", true)
                    params.put("invoice_services", invoiceServiceArrayAppended)
                    params.put(
                        "invoice_grand_amount",
                        (invoiceGrandAmount + totalProcedureAmount).toDouble()
                    )
                    params.put("invoice_grand_discount", invoice_grand_discount.toDouble())
                    params.put(
                        "invoice_grand_pre_tax",
                        (invoice_grand_pre_tax + totalProcedureAmount).toDouble()
                    )
                    params.put(
                        "invoice_grand_tax",
                        (invoice_grand_tax + totalProcedureTaxesAmount).toDouble()
                    )
                    params.put(
                        "invoice_grand_total",
                        (invoice_grand_total + finalProcedureAmount).toDouble()
                    )
                    params.put("platform", "app")
                    createInvoice(this@PaymentHistoryTimeLineActivity, params)
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }
        }
        createReceiptLayout.setOnClickListener { view: View? ->
            if (createReceiptText.text.toString().equals("View Receipt", ignoreCase = true)) {
                getReceiptUrl(receiptUrl)
            } else {
                createReceipt(orderId)
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun getPaymentTimeline(activity: Activity?, appointmentID: Int) {
        appointmentDetailsViewModel!!.getPaymentTimeline(activity, appointmentID)
            .observe(this) { s ->
                Log.i("paymentTimeLineRes", s)
                try {
                    val response = JSONObject(s)
                    if (response.getInt("status_code") == 200) {
                        MyClinicGlobalClass.logUserActionEvent(
                            ApiUrls.doctorId,
                            getString(R.string.TransactionPagePaymentHistoryButtonScreenImpression),
                            null
                        )
                        val resObject =
                            response.getJSONObject("response").getJSONObject("response")
                        apptBookedInfo.text =
                            MessageFormat.format("{0}", resObject.getString("appt_booked_on"))
                        apptStatusInfo.text =
                            MessageFormat.format("{0}", resObject.getString("appt_updated_on"))
                        val paymentTimelineArray = resObject.getJSONArray("payment_timeline")
                        for (i in 0 until paymentTimelineArray.length()) {
                            val paymentTimeLineArrayObject =
                                paymentTimelineArray.getJSONObject(i)
                            val pt = PaymentTimeLineModel()
                            pt.text1 = paymentTimeLineArrayObject.getString("text1")
                            pt.text2 = paymentTimeLineArrayObject.getString("text2")
                            pt.text3 = paymentTimeLineArrayObject.getString("text3")
                            pt.color1 = paymentTimeLineArrayObject.getString("color1")
                            pt.color2 = paymentTimeLineArrayObject.getString("color2")
                            pt.color3 = paymentTimeLineArrayObject.getString("color3")
                            pt.isShowCancelRefund =
                                paymentTimeLineArrayObject.getInt("is_show_cancel_refund_schedule")
                            pt.isShowPayULink =
                                paymentTimeLineArrayObject.getInt("is_show_payu_link")
                            pt.payULink = paymentTimeLineArrayObject.getString("payu_link")
                            paymentTimeLineList.add(pt)
                        }
                        paymentTimeLineAdapter!!.notifyDataSetChanged()
                        getServicesForAppointmentData(
                            this@PaymentHistoryTimeLineActivity,
                            appointmentID
                        )
                    } else {
                        errorHandler(this@PaymentHistoryTimeLineActivity, s)
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                    Log.d("parserError:", "parserError:$e")
                }
            }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
        }
        return super.onOptionsItemSelected(item)
    }

    fun createInvoice(activity: Activity?, params: JSONObject?) {
        appointmentDetailsViewModel!!.creteInvoice(activity, params)
            .observe(this) { s ->
                try {
                    val response = JSONObject(s)
                    if (response.getInt("status_code") == 200) {
                        val invoiceResponse =
                            response.getJSONObject("response").getJSONObject("response")
                        Log.d("invoiceOrderId", "invoiceOrderId$invoiceResponse")
                        invoiceUrl = invoiceResponse.getString("file_path")
                        Log.d("invoiceUrl", "invoiceUrl$invoiceUrl")
                        Toast.makeText(
                            applicationContext,
                            "Invoice has been generated",
                            Toast.LENGTH_LONG
                        ).show()
                        paymentReceivedDialog()
                        if (ApptDetailsFragment.appointmentDetailsActivity != null) {
                            ApptDetailsFragment.appointmentDetailsActivity!!.invoiceData =
                                invoiceUrl
                        }
                        val intent = Intent()
                        // Here you can also put data on intent
                        intent.action = "UPDATE_TRANSACTION"
                        intent.putExtra("orderId", invoiceResponse.getInt("order_id"))
                        intent.putExtra("invoiceURL", invoiceUrl)
                        sendBroadcast(intent)
                        val intent1 = Intent()
                        // Here you can also put data on intent
                        intent1.action = "CREATE_INVOICE"
                        intent1.putExtra("apptID", appointmentID)
                        intent1.putExtra("invoiceURL", invoiceUrl)
                        sendBroadcast(intent1)
                    } else {
                        errorHandler(this@PaymentHistoryTimeLineActivity, s)
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }
    }

    @SuppressLint("SetTextI18n")
    fun paymentReceivedDialog() {
        val dialog = Dialog(this)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.dialog_payment_received)
        val dialogArticleCancel = dialog.findViewById<View>(R.id.PaymentReceivedCancel) as ImageView
        val dialogCloseButton = dialog.findViewById<View>(R.id.close_payment_Button) as Button
        dialogArticleCancel.setOnClickListener {
            dialog.dismiss()
            createInvoiceText.text = "View Invoice"
        }
        dialogCloseButton.setOnClickListener {
            dialog.dismiss()
            createInvoiceText.text = "View Invoice"
        }
        dialog.show()
    }

    fun getFileFromUrl(fileUrl: String?) {
        val url = JSONObject()
        try {
            url.put("url", fileUrl)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        patientRecordsApi!!.postRecords(
            ApiUrls.getFileFromUrl,
            url,
            this@PaymentHistoryTimeLineActivity,
            object : VolleyCallback {
                override fun onSuccess(result: String) {
                    try {
                        val resObj = JSONObject(result)
                        val browserIntent =
                            Intent(Intent.ACTION_VIEW, Uri.parse(resObj.getString("response")))
                        startActivity(browserIntent)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                override fun onError(err: String) {
                    errorHandler(this@PaymentHistoryTimeLineActivity, err)
                }
            })
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
                        invoiceServiceArrayAppended = JSONArray()
                        invoiceServiceArray = resObject.getJSONArray("invoice_services")
                        for (i in 0 until invoiceServiceArray!!.length()) {
                            val invoiceArrayObject = invoiceServiceArray!!.getJSONObject(i)
                            val eachValue = JSONObject()
                            eachValue.put("id", invoiceArrayObject.getInt("id"))
                            eachValue.put(
                                "appointment_id",
                                invoiceArrayObject.getInt("appointment_id")
                            )
                            eachValue.put("order_id", invoiceArrayObject.getInt("order_id"))
                            eachValue.put(
                                "inclusion_type",
                                invoiceArrayObject.getInt("inclusion_type")
                            )
                            eachValue.put(
                                "appt_service_id",
                                invoiceArrayObject.getInt("appt_service_id")
                            )
                            eachValue.put(
                                "pre_tax_amount",
                                invoiceArrayObject.getInt("pre_tax_amount")
                            )
                            eachValue.put("discount", invoiceArrayObject.getInt("discount"))
                            eachValue.put(
                                "final_pre_tax_amount",
                                invoiceArrayObject.getInt("final_pre_tax_amount")
                            )
                            eachValue.put("total", invoiceArrayObject.getInt("total"))
                            eachValue.put("tax_amt", invoiceArrayObject.getInt("tax_amt"))
                            eachValue.put(
                                "created_at",
                                invoiceArrayObject.getString("created_at")
                            )
                            eachValue.put(
                                "updated_at",
                                invoiceArrayObject.getString("updated_at")
                            )
                            eachValue.put(
                                "service_details",
                                invoiceArrayObject.getJSONObject("service_details")
                            )
                            eachValue.put("taxes", invoiceArrayObject.getJSONArray("taxes"))
                            eachValue.put("status", invoiceArrayObject.getBoolean("status"))
                            eachValue.put(
                                "percentageApply",
                                invoiceArrayObject.getInt("percentageApply")
                            )
                            eachValue.put(
                                "applyDiscount1Flag",
                                invoiceArrayObject.getInt("applyDiscount1Flag")
                            )
                            var totalPercent = 0
                            for (j in 0 until invoiceArrayObject.getJSONArray("taxes")
                                .length()) {
                                val percentObject =
                                    invoiceArrayObject.getJSONArray("taxes").getJSONObject(j)
                                val taxPercentage = percentObject.getInt("percentage")
                                totalPercent = totalPercent + taxPercentage
                            }
                            val totalPercentTax = totalPercent.toFloat()
                            val taxesAmountAfterCalculateText =
                                invoiceArrayObject.getInt("pre_tax_amount")
                                    .toFloat() / 100 * totalPercentTax
                            val finalPostTaxAmount =
                                invoiceArrayObject.getInt("pre_tax_amount") + taxesAmountAfterCalculateText
                            eachValue.put(
                                "final_post_tax_amount",
                                finalPostTaxAmount.toDouble()
                            )
                            eachValue.put(
                                "tax_amount",
                                taxesAmountAfterCalculateText.toDouble()
                            )
                            invoiceServiceArrayAppended!!.put(eachValue)
                        }
                        val appointmentDataObject = resObject.getJSONObject("appt_data")
                        val apptStatus = appointmentDataObject.getInt("status")
                        val checkedInStatus = appointmentDataObject.getInt("checked_in")
                        if (checkedInStatus == 1) {
                            val checkedInAt = appointmentDataObject.getString("checked_in_at")
                            val separatedInteractionDate =
                                checkedInAt.split(" ".toRegex()).dropLastWhile { it.isEmpty() }
                                    .toTypedArray()
                            Log.i("CheckInAt", separatedInteractionDate.toString())
                        } else {
                            val appointmentOrderObject =
                                appointmentDataObject.getJSONObject("order")
                            appointmentOrderPaymentStatus =
                                appointmentOrderObject.getString("payment_status")
                            if (appointmentDataObject.getInt("mode") == 1) {
                                /* if (apptStatus == 3) {
                                            createInvoiceLayout.setVisibility(View.VISIBLE);
                                        } else {
                                            createInvoiceLayout.setVisibility(View.GONE);
                                        }*/
                                if (appointmentOrderPaymentStatus.equals(
                                        "Pending",
                                        ignoreCase = true
                                    )
                                ) {
                                    if (apptStatus == 3) {
                                        createInvoiceLayout.visibility = View.VISIBLE
                                    } else {
                                        createInvoiceLayout.visibility = View.GONE
                                    }
                                } else {
                                    createInvoiceLayout.visibility = View.VISIBLE
                                }
                            } else {
                                if (activePastFilter.equals("past", ignoreCase = true)) {
                                    if (apptStatus == 4) {
                                        createInvoiceLayout.visibility = View.GONE
                                    } else {
                                        if (appointmentOrderPaymentStatus.equals(
                                                "Pending",
                                                ignoreCase = true
                                            )
                                        ) {
                                            if (apptStatus == 3) {
                                                createInvoiceLayout.visibility = View.VISIBLE
                                            } else {
                                                createInvoiceLayout.visibility = View.GONE
                                            }
                                        } else {
                                            createInvoiceLayout.visibility = View.VISIBLE
                                        }
                                    }
                                } else {
                                    if (apptStatus == 4) {
                                        createInvoiceLayout.visibility = View.GONE
                                    } else {
                                        if (appointmentOrderPaymentStatus.equals(
                                                "Pending",
                                                ignoreCase = true
                                            )
                                        ) {
                                            if (apptStatus == 3) {
                                                createInvoiceLayout.visibility = View.VISIBLE
                                            } else {
                                                createInvoiceLayout.visibility = View.GONE
                                            }
                                        } else {
                                            createInvoiceLayout.isEnabled = true
                                            createInvoiceLayout.background.setColorFilter(
                                                Color.parseColor("#3C8DBC"),
                                                PorterDuff.Mode.SRC_ATOP
                                            )
                                            createInvoiceLayout.visibility = View.VISIBLE
                                        }
                                    }
                                }
                            }
                        }
                        val appointmentOrderObject =
                            appointmentDataObject.getJSONObject("order")
                        val product_id = appointmentOrderObject.getInt("product_id")
                        appointmentOrderPaymentStatus =
                            appointmentOrderObject.getString("payment_status")
                        appointmentOrderAmount = appointmentOrderObject.getInt("order_amt")
                        appointmentOrderAmountDiscount =
                            appointmentOrderObject.getInt("discount")
                        appointmentNetAmount = appointmentOrderObject.getInt("net_amount")
                        val invoiceServiceArray = resObject.getJSONArray("invoice_services")
                        var totalProcedureAmount = 0
                        if (invoiceServiceArray.length() > 0) {
                            for (i in 0 until invoiceServiceArray.length()) {
                                val appointmentInvoiceServiceArrayObject =
                                    invoiceServiceArray.getJSONObject(i)
                                val procedureListModel = AddProcedureListModel()
                                val serviceDetailsObjects =
                                    appointmentInvoiceServiceArrayObject.getJSONObject("service_details")
                                procedureListModel.procedureId =
                                    serviceDetailsObjects.getInt("id")
                                procedureListModel.procedureName =
                                    serviceDetailsObjects.getString("name")
                                procedureListModel.charges =
                                    serviceDetailsObjects.getInt("pre_tax_amount")
                                procedureListModel.tax =
                                    appointmentInvoiceServiceArrayObject.getInt("tax_amt")
                                var discountApplyValue: Int
                                var discountAmountAfterCalculate: Int
                                if (discountArrayList!!.size > 0 && discountArrayList!![i] != 0) {
                                    discountApplyValue = discountArrayList!![i]
                                    discountAmountAfterCalculate =
                                        appointmentInvoiceServiceArrayObject.getInt(
                                            "pre_tax_amount"
                                        ) / 100 * discountApplyValue
                                    Log.i("discountValue", discountAmountAfterCalculate.toString())
                                }
                                procedureListModel.discount = 0
                                procedureListModel.totalAmount = 0
                                addProcedureListModel.add(procedureListModel)
                            }
                            if (discountArrayList!!.size <= 0) {
                                for (i in addProcedureListModel.indices) {
                                    discountArrayList!!.add(0)
                                }
                            }
                            for (i in addProcedureListModel.indices) {
                                totalProcedureAmount += if (discountArrayList!![i] == 0) {
                                    addProcedureListModel[i].charges
                                } else {
                                    val discountApplyValue = discountArrayList!![i]
                                    val discountAmountAfterCalculate =
                                        addProcedureListModel[i].charges / 100 * discountApplyValue
                                    addProcedureListModel[i].charges - discountAmountAfterCalculate
                                }
                            }
                        }
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
                        errorHandler(this@PaymentHistoryTimeLineActivity, s)
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                    Log.d("parserError:", "parserError:$e")
                }
            }
    }

    private fun getProductTaxe(product_id: Int) {
        appointmentDetailsViewModel!!.getProductTaxes(
            this@PaymentHistoryTimeLineActivity,
            product_id
        ).observe(this) { s ->
            try {
                val jsonObject = JSONObject(s)
                if (jsonObject.getInt("status_code") == 200) {
                    val response = jsonObject.getJSONObject("response")
                    texesResponseArray = response.getJSONArray("response")
                    var totalTaxes = 0
                    if (texesResponseArray!=null && texesResponseArray!!.length() > 0) {
                        Log.d("TaxesInformation ", "TaxesInformation $response")
                        for (i in 0 until texesResponseArray!!.length()) {
                            val taxesPercentObject = texesResponseArray!!.getJSONObject(i)
                            taxesPercent = taxesPercentObject.getInt("percentage")
                            totalTaxes += taxesPercent
                            Log.d("totalTaxes ", "totalTaxes $totalTaxes")
                        }
                        Log.d("netAmount ", "netAmount $appointmentNetAmount")
                        taxesApplyValueText = totalTaxes.toFloat()
                        val taxesAmountAfterCalculateText =
                            appointmentNetAmount.toFloat() / 100 * taxesApplyValueText
                        val consultationAmountValue =
                            appointmentOrderAmount - taxesAmountAfterCalculateText
                        val discountApplyValue = appointmentOrderAmountDiscount.toFloat()
                        val discountAmountAfterCalculate =
                            appointmentNetAmount.toFloat() / 100 * discountApplyValue
                        preTextAmount = appointmentOrderAmount - taxesAmountAfterCalculateText
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
                    } else {
                        //                            taxesName = "";
                        taxesApplyValueText = 0f
                        val taxesAmountAfterCalculateText =
                            appointmentNetAmount.toFloat() / 100 * taxesApplyValueText
                        val consultationAmountValue =
                            appointmentOrderAmount - taxesAmountAfterCalculateText
                        val discountApplyValue = appointmentOrderAmountDiscount.toFloat()
                        val discountAmountAfterCalculate =
                            appointmentNetAmount.toFloat() / 100 * discountApplyValue
                        preTextAmount = appointmentOrderAmount - taxesAmountAfterCalculateText
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
                    errorHandler(this@PaymentHistoryTimeLineActivity, s)
                }
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }
    }

    private fun cancelRefundPopup(order_id: Int) {
        val dialog = Dialog(this@PaymentHistoryTimeLineActivity)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.dailog_cancel_refund_popup)
        val dailogArticleCancel = dialog.findViewById<View>(R.id.dailogArticleCancel) as ImageView
        yesButton = dialog.findViewById<View>(R.id.yesButton) as RelativeLayout
        noButton = dialog.findViewById<View>(R.id.noButton) as RelativeLayout
        closeButton = dialog.findViewById<View>(R.id.closeButton) as RelativeLayout
        refundTextOne = dialog.findViewById<View>(R.id.refundTextOne) as TextView
        refundTextTwo = dialog.findViewById<View>(R.id.refundTextTwo) as TextView
        refundTextThree = dialog.findViewById<View>(R.id.refundTextThree) as TextView
        refundTextThree!!.visibility = View.GONE
        closeButton!!.visibility = View.GONE
        yesButton!!.setOnClickListener { cancelRefund(order_id) }
        noButton!!.setOnClickListener { dialog.dismiss() }
        closeButton!!.setOnClickListener { dialog.dismiss() }
        dailogArticleCancel.setOnClickListener { dialog.dismiss() }
        dialog.show()
    }

    private fun cancelRefund(order_id: Int) {
        val url = ApiUrls.cancelRefund + "?order_id=" + order_id
        appointmentApiRequests!!.getApptApiData(
            url,
            "",
            this@PaymentHistoryTimeLineActivity,
            object : VolleyCallback {
                @SuppressLint("SetTextI18n")
                override fun onSuccess(result: String) {
                    try {
                        val resObj = JSONObject(result)
                        if (resObj.getInt("response") == 1) {
                            refundTextOne!!.text = "Refund Cancelled Successfully"
                            refundTextTwo!!.text = "Refund amount will be reflected in your account"
                            yesButton!!.visibility = View.GONE
                            noButton!!.visibility = View.GONE
                            refundTextTwo!!.visibility = View.VISIBLE
                            refundTextThree!!.visibility = View.VISIBLE
                            closeButton!!.visibility = View.VISIBLE
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                override fun onError(err: String) {
                    errorHandler(this@PaymentHistoryTimeLineActivity, err)
                }
            })
    }

    private fun getReceiptUrl(url: String?) {
        val i = Intent(Intent.ACTION_VIEW)
        i.data = Uri.parse(url)
        startActivity(i)
    }

    @SuppressLint("SetTextI18n")
    private fun createReceipt(orderId: Int) {
        val url = ApiUrls.createReceipt + "?order_id=" + orderId
        showCustomProgressAlertDialog("", resources.getString(R.string.please_wait))
        try {

            commonViewModel.commonViewModelCall(url, JSONObject(), Method.GET).observe(
                this@PaymentHistoryTimeLineActivity
            ) { result ->
                try {
                    val responseObj = JSONObject(result)
                    if (responseObj.getInt("status_code") == 200) {
                        val response = responseObj.optJSONObject("response")
                        val rootObj = response!!.getJSONObject("response")
                        receiptUrl = rootObj.getString("public_url")
                        createReceiptText.text = "View Receipt"
                        val intent = Intent()
                        // Here you can also put data on intent
                        intent.action = "UPDATE_FRAG_LIST"
                        sendBroadcast(intent)
                        if (ApptDetailsFragment.appointmentDetailsActivity != null) {
                            ApptDetailsFragment.appointmentDetailsActivity!!.receiptUrl =
                                receiptUrl
                        }
                        dialog.dismiss()
                    } else {
                        dialog.dismiss()
                        errorHandler(this@PaymentHistoryTimeLineActivity, result)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    dialog.dismiss()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        // prepare the Request
    }

    fun showCustomProgressAlertDialog(
        title: String?,
        progressVal: String?

    ) {
        val builder = AlertDialog.Builder(this@PaymentHistoryTimeLineActivity)
        builder.setTitle(title)
        builder.setCancelable(false)
        val customLayout =
            LayoutInflater.from(this@PaymentHistoryTimeLineActivity)
                .inflate(R.layout.custom_progress_bar, null)
        builder.setView(customLayout)
        dialog = builder.create()
        val tvBody = customLayout.findViewById<TextView>(R.id.tv_value)
        tvBody.text = progressVal
        dialog.show()
    }

}