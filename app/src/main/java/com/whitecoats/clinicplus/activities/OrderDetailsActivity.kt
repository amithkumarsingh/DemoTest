package com.whitecoats.clinicplus.activities

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.SpannableString
import android.text.TextWatcher
import android.text.style.UnderlineSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request.Method
import com.whitecoats.clinicplus.*
import com.whitecoats.clinicplus.adapters.OrderDetailsPaymentTimeLineAdapter
import com.whitecoats.clinicplus.adapters.PaymentServiceAdapter
import com.whitecoats.clinicplus.adapters.ProcedureSearchAdapter
import com.whitecoats.clinicplus.apis.ApiGetPostMethodCalls
import com.whitecoats.clinicplus.apis.ApiUrls
import com.whitecoats.clinicplus.fragments.OrderDetailsRefundNowBottomSheet
import com.whitecoats.clinicplus.fragments.PaymentTransactionBottomSheet
import com.whitecoats.clinicplus.models.AddProcedureListModel
import com.whitecoats.clinicplus.models.OrderDetailsPaymentTimeLineModel
import com.whitecoats.clinicplus.utils.ErrorHandlerClass.errorHandler
import com.whitecoats.clinicplus.utils.InputFilterMinMax
import com.whitecoats.clinicplus.viewmodels.AppointmentDetailsViewModel
import com.whitecoats.clinicplus.viewmodels.CommonViewModel
import com.whitecoats.clinicplus.viewmodels.DashboardViewModel
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.text.MessageFormat
import kotlin.math.roundToInt

class OrderDetailsActivity : AppCompatActivity() {
    private lateinit var appointmentChangeSpinner: Spinner
    private lateinit var spinner_paymentMode: Spinner
    private lateinit var actionbar_back: ImageButton
    private lateinit var imgBtn_expand: ImageButton
    private lateinit var imgBtn_collapse: ImageButton
    private lateinit var tv_expand: TextView
    private lateinit var tv_collapse: TextView
    private lateinit var tv_add_services: TextView
    private lateinit var layout_costExpand: RelativeLayout
    private lateinit var layout_addService: LinearLayout
    private lateinit var layout_procedureSearch: LinearLayout
    private lateinit var procedureSearch: AutoCompleteTextView
    private var searchProcedureListModels: ArrayList<AddProcedureListModel>? = null
    private var dataProcedureListModels: ArrayList<AddProcedureListModel>? = null
    private var procedureSearchAdapter: ProcedureSearchAdapter? = null
    private var globalClass: MyClinicGlobalClass? = null
    lateinit var services_recycleView: RecyclerView
    private var paymentServiceAdapter: PaymentServiceAdapter? = null
    lateinit var payment_invoice_button: Button
    private var appointmentDetailsViewModel: AppointmentDetailsViewModel? = null
    private var appointmentDetailsProgressbar: RelativeLayout? = null
    var invoiceServiceArray: JSONArray? = null
    var invoiceServiceArrayAppended: JSONArray? = null
    var originalServiceArrayData: JSONArray? = null
    private var changeArray: JSONArray? = null
    var totalPercentDiscount = 0f
    var originalOrderDetailsAmount = 0
    var invoicePreTaxAmount = 0.0
    var invoicePreTaxDiscount = 0.0
    var invoicePreTaxTotal = 0.0
    var invoiceTotalTax = 0.0
    var invoiceNetAmount = 0.0
    var invoicePostTaxDiscount = 0.0
    var invoiceGrandAmount = 0.0
    var invoiceGrandDiscount = 0.0
    var invoiceGrandPreTax = 0.0
    var invoiceGrandTax = 0.0
    var invoiceGrandTotal = 0.0
    private lateinit var order_details_status_value: TextView
    private lateinit var patient_application_status: TextView
    private lateinit var appt_date: TextView
    private lateinit var bottom_amount_received: TextView
    private lateinit var bottom_amount: EditText
    private lateinit var apptStatusSpinnerLayout: LinearLayout
    private var appointmentApiRequests: AppointmentApiRequests? = null
    var appointmentOrderAmount = 0
    var appointmentOrderAmountDiscount = 0
    var appointmentNetAmount = 0
    private lateinit var paymentStatusData: TextView
    var arrayProcedureId: JSONArray? = null
    private var isScreenRefresh = true
    private var orderDetailsPaymentTimeLineAdapter: OrderDetailsPaymentTimeLineAdapter? = null
    var orderDetailsPaymentTimeLineList = ArrayList<OrderDetailsPaymentTimeLineModel>()
    var invoiceServiceModelList = ArrayList<AddProcedureListModel>()
    private lateinit var cancelRefund: RelativeLayout
    private lateinit var refundNow: RelativeLayout
    private lateinit var orderDetailsServiceName: TextView
    private lateinit var orderDetails_amount: TextView
    private lateinit var serviceCost_amount: TextView
    private lateinit var gst_Text: TextView
    private lateinit var taxAmt_text: TextView
    private lateinit var discountText: TextView
    private lateinit var totalPay_amount: TextView
    private var serviceMode: String? = null
    private lateinit var discountRemoveText: TextView
    private lateinit var receivedAndCreateReceiptText: TextView
    private lateinit var createInvoiceText: TextView
    private lateinit var jsonValue: JSONObject
    var receiptUrl: String? = null
    var invoiceUrl: String? = null
    private var patientRecordsApi: PatientRecordsApi? = null
    private var invoiceServiceArrayData: JSONArray? = null
    private var status = 0
    private var mode = 0
    private var orderPaymentStatus: String? = null
    private lateinit var viewInvoiceText: TextView
    private lateinit var paymentModeLayout: RelativeLayout
    private var dashboardViewModel: DashboardViewModel? = null
    private lateinit var addProcedureAppointmentDetails: TextView
    var discountArrayList: ArrayList<Int>? = null
    private lateinit var noProcedureText: TextView
    private lateinit var dialog: Dialog
    private lateinit var commonViewModel: CommonViewModel
    private lateinit var mContext: Context

    /*ENGG-3691 -- Refactoring the code by removing the deprecate functions(StartActivityForResults)*/
    private var launchAddProcedureResults: ActivityResultLauncher<Intent>? = null
    private var globalApiCall: ApiGetPostMethodCalls? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_details)
        val intent = intent
        globalApiCall = ApiGetPostMethodCalls()
        commonViewModel = ViewModelProvider(this@OrderDetailsActivity)[CommonViewModel::class.java]
        appointmentID = intent.getIntExtra("apptID", 0)
        orderID = intent.getIntExtra("orderID", 0)
        productId = intent.getIntExtra("productId", 0)
        receiptUrl = intent.getStringExtra("receiptURL")
        invoiceUrl = intent.getStringExtra("invoiceUrl")
        refundAmount = intent.getIntExtra("refundAmount", 0)
        netPaidAmount = intent.getIntExtra("netPaidAmount", 0)
        status = intent.getIntExtra("status", 0)
        mode = intent.getIntExtra("mode", 0)
        orderPaymentStatus = intent.getStringExtra("orderPaymentStatus")
        // val sendPaymentNotification = intent.getIntExtra("sendPaymentNotification", 0)
        // val paymentMode = intent.getStringExtra("paymentMode")
        appointmentDetailsViewModel =
            ViewModelProvider(this)[AppointmentDetailsViewModel::class.java]
        appointmentDetailsViewModel!!.init()
        dashboardViewModel = ViewModelProvider(this).get(DashboardViewModel::class.java)
        dashboardViewModel!!.init()
        appointmentApiRequests = AppointmentApiRequests()
        discountArrayList = ArrayList()
        globalClass = applicationContext as MyClinicGlobalClass
        patientRecordsApi = PatientRecordsApi()
        initialize()
    }

    fun showCustomProgressAlertDialog(
        title: String?,
        progressVal: String?

    ) {
        val builder = AlertDialog.Builder(this@OrderDetailsActivity)
        builder.setTitle(title)
        builder.setCancelable(false)
        val customLayout =
            LayoutInflater.from(this@OrderDetailsActivity)
                .inflate(R.layout.custom_progress_bar, null)
        builder.setView(customLayout)
        dialog = builder.create()
        val tvBody = customLayout.findViewById<TextView>(R.id.tv_value)
        tvBody.text = progressVal
        dialog.show()
    }

    @SuppressLint("NotifyDataSetChanged", "SetTextI18n")
    fun initialize() {
        actionbar_back = findViewById(R.id.toolbarOrderDetails_back)
        appointmentChangeSpinner = findViewById(R.id.spinner_ApptStatus)
        spinner_paymentMode = findViewById(R.id.spinner_paymentMode)
        tv_expand = findViewById(R.id.orderDetails_expand)
        tv_collapse = findViewById(R.id.orderDetails_collapse)
        imgBtn_expand = findViewById(R.id.icon_expand)
        imgBtn_collapse = findViewById(R.id.icon_collapse)
        layout_costExpand = findViewById(R.id.costDetails_Layout)
        tv_add_services = findViewById(R.id.addService_tv)
        layout_addService = findViewById(R.id.layout_add_service)
        layout_procedureSearch = findViewById(R.id.procedure_search_layout)
        procedureSearch = findViewById(R.id.autoText_procedure_search)
        services_recycleView = findViewById(R.id.service_recyclerView)
        payment_invoice_button = findViewById(R.id.btn_payRec_createInv)
        appointmentDetailsProgressbar = findViewById(R.id.appointmentDetailsProgressbar)
        order_details_status_value = findViewById(R.id.order_details_status_value)
        apptStatusSpinnerLayout = findViewById(R.id.apptStatusSpinnerLayout)
        patient_application_status = findViewById(R.id.patient_application_status)
        appt_date = findViewById(R.id.appt_date)
        bottom_amount = findViewById(R.id.bottom_amount)
        bottom_amount_received = findViewById(R.id.bottom_amount_received)
        paymentStatusData = findViewById(R.id.paymentStatusData)
        cancelRefund = findViewById(R.id.cancelRefund)
        refundNow = findViewById(R.id.refundNow)
        val viewReceipt = findViewById<RelativeLayout>(R.id.viewReceipt)
        val createInvoice = findViewById<RelativeLayout>(R.id.createInvoice)
        orderDetailsServiceName = findViewById(R.id.orderDetailsServiceName)
        orderDetails_amount = findViewById(R.id.orderDetails_amount)
        serviceCost_amount = findViewById(R.id.serviceCost_amount)
        gst_Text = findViewById(R.id.gst_Text)
        taxAmt_text = findViewById(R.id.taxAmt_text)
        discountText = findViewById(R.id.discountText)
        totalPay_amount = findViewById(R.id.totalPay_amount)
        discountRemoveText = findViewById(R.id.discountRemoveText)
        receivedAndCreateReceiptText = findViewById(R.id.receivedAndCreateReceiptText)
        createInvoiceText = findViewById(R.id.createInvoiceText)
        val viewInvoiceSmallLayout = findViewById<LinearLayout>(R.id.viewInvoiceSmallLayout)
        viewInvoiceText = findViewById(R.id.viewInvoiceText)
        paymentModeLayout = findViewById(R.id.paymentModeLayout)
        addProcedureAppointmentDetails = findViewById(R.id.addProcedure)
        noProcedureText = findViewById(R.id.no_procedure_text)
        val addmedicine = SpannableString("Add Service")
        addmedicine.setSpan(UnderlineSpan(), 0, addmedicine.length, 0)
        addProcedureAppointmentDetails.text = addmedicine

        /*ENGG-3691 -- Refactoring the code by removing the deprecate functions(StartActivityForResults)*/
        //Start
        launchAddProcedureResults = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result: ActivityResult ->
            //Request code 2
            isScreenRefresh = true
            val data = result.data
            val resultCode = result.resultCode
            if (resultCode == 2) {
                if (data != null) {
                    discountArrayList = data.getIntegerArrayListExtra("discountList")
                    paymentServiceAdapter!!.notifyDataSetChanged()
                }
            } else if (resultCode == 1) {
                isScreenRefresh = false
            }
        }
        //End
        addProcedureAppointmentDetails.setOnClickListener {
            MyClinicGlobalClass.logUserActionEvent(
                ApiUrls.doctorId, getString(
                    R.string.AppointmentDetailsAddProcedure
                ), null
            )
            val intent = Intent(this@OrderDetailsActivity, AddProcedureActivity::class.java)
            intent.putExtra("apptId", appointmentID)
            intent.putExtra("discountList", discountArrayList)
            intent.putExtra("productId", productId)
            launchAddProcedureResults!!.launch(intent)
        }
        apptStatusSpinnerLayout.visibility = View.GONE
        if (orderPaymentStatus.equals("pending", ignoreCase = true) && receiptUrl.equals(
                "",
                ignoreCase = true
            )
        ) {
            viewReceipt.visibility = View.VISIBLE
            receivedAndCreateReceiptText.text = "Received and Create Receipt"
        } else if (orderPaymentStatus.equals("success", ignoreCase = true) && receiptUrl.equals(
                "",
                ignoreCase = true
            )
        ) {
            viewReceipt.visibility = View.VISIBLE
            receivedAndCreateReceiptText.text = "Create Receipt"
        } else if (orderPaymentStatus.equals("success", ignoreCase = true) && !receiptUrl.equals(
                "",
                ignoreCase = true
            )
        ) {
            viewReceipt.visibility = View.VISIBLE
            receivedAndCreateReceiptText.text = "View Receipt"
        }
        if (orderPaymentStatus.equals("cancelled", ignoreCase = true) && status == 4) {
            if (receiptUrl.equals("", ignoreCase = true)) {
                viewReceipt.visibility = View.VISIBLE
                receivedAndCreateReceiptText.text = "Create Receipt"
            } else if (!receiptUrl.equals("", ignoreCase = true)) {
                viewReceipt.visibility = View.VISIBLE
                receivedAndCreateReceiptText.text = "View Receipt"
            }
        }
        if (invoiceUrl.equals(
                "",
                ignoreCase = true
            ) && status < 3 && (mode == 1 || mode == 3 || mode == 2)
        ) {
            createInvoice.visibility = View.VISIBLE
            createInvoiceText.text = "Create Invoice"
            bottom_amount.isEnabled = true
            addProcedureAppointmentDetails.isEnabled = false
            addProcedureAppointmentDetails.setTextColor(
                ContextCompat.getColorStateList(
                    this,
                    R.color.colorGrey2
                )
            )
            createInvoice.isEnabled = false
            createInvoice.backgroundTintList =
                ContextCompat.getColorStateList(this, R.color.colorGrey2)
        } else {
            if (status == 3) {
                if (invoiceUrl.equals("", ignoreCase = true)) {
                    createInvoice.visibility = View.VISIBLE
                    createInvoiceText.text = "Create Invoice"
                    bottom_amount.isEnabled = true
                    addProcedureAppointmentDetails.isEnabled = true
                    createInvoice.isEnabled = true
                } else {
                    bottom_amount.isEnabled = false
                    addProcedureAppointmentDetails.isEnabled = false
                    addProcedureAppointmentDetails.setTextColor(
                        ContextCompat.getColorStateList(
                            this,
                            R.color.colorGrey2
                        )
                    )
                    createInvoice.visibility = View.VISIBLE
                    createInvoiceText.text = "View Invoice"
                    createInvoice.isEnabled = true
                }
            } else if (status == 4) {
                addProcedureAppointmentDetails.isEnabled = false
                addProcedureAppointmentDetails.setTextColor(
                    ContextCompat.getColorStateList(
                        this,
                        R.color.colorGrey2
                    )
                )
                if (orderPaymentStatus.equals("success", ignoreCase = true)) {
                    if (invoiceUrl.equals("", ignoreCase = true)) {
                        createInvoice.visibility = View.VISIBLE
                        createInvoiceText.text = "Create Invoice"
                        bottom_amount.isEnabled = true
                        addProcedureAppointmentDetails.isEnabled = true
                        createInvoice.isEnabled = true
                    } else {
                        bottom_amount.isEnabled = false
                        addProcedureAppointmentDetails.isEnabled = true
                        createInvoice.visibility = View.VISIBLE
                        createInvoiceText.text = "View Invoice"
                        createInvoice.isEnabled = true
                    }
                }
            } else {
                if (status < 3) {
                    bottom_amount.isEnabled = false
                    addProcedureAppointmentDetails.isEnabled = false
                    addProcedureAppointmentDetails.setTextColor(
                        ContextCompat.getColorStateList(
                            this,
                            R.color.colorGrey2
                        )
                    )
                    createInvoice.visibility = View.VISIBLE
                    createInvoiceText.text = "View Invoice"
                    createInvoice.isEnabled = false
                    createInvoice.backgroundTintList =
                        ContextCompat.getColorStateList(this, R.color.colorGrey2)
                    viewInvoiceSmallLayout.visibility = View.GONE
                    viewInvoiceText.text = "View Invoice"
                } else {
                    if (invoiceUrl.equals("", ignoreCase = true)) {
                        createInvoice.visibility = View.GONE
                        createInvoiceText.text = "Create Invoice"
                        bottom_amount.isEnabled = false
                        createInvoice.isEnabled = false
                        addProcedureAppointmentDetails.isEnabled = false
                        addProcedureAppointmentDetails.setTextColor(
                            ContextCompat.getColorStateList(
                                this,
                                R.color.colorGrey2
                            )
                        )
                    } else {
                        bottom_amount.isEnabled = false
                        createInvoice.visibility = View.GONE
                        createInvoiceText.text = "View Invoice"
                        createInvoice.isEnabled = false
                        addProcedureAppointmentDetails.isEnabled = false
                        addProcedureAppointmentDetails.setTextColor(
                            ContextCompat.getColorStateList(
                                this,
                                R.color.colorGrey2
                            )
                        )
                        viewInvoiceSmallLayout.visibility = View.GONE
                        viewInvoiceText.text = "View Invoice"
                    }
                }
            }
        }
        if (status < 3 && orderPaymentStatus.equals("pending", ignoreCase = true)) {
            viewReceipt.visibility = View.GONE
        }
        paymentServiceAdapter = PaymentServiceAdapter(
            this@OrderDetailsActivity,
            invoiceServiceModelList
        ) { capturedNotesId, captureNotesText, _, couponDiscountAmount ->
            if (captureNotesText.equals("applyDiscount", ignoreCase = true) ||
                captureNotesText.equals("Remove", ignoreCase = true)
            ) {
                try {
                    val totalPercent = couponDiscountAmount.toFloat()
                    val totalPercentDiscountInService =
                        originalServiceArrayData!!.getInt(capturedNotesId)
                            .toFloat() / 100 * totalPercent
                    val totalAmountAfterDiscount =
                        originalServiceArrayData!!.getInt(capturedNotesId) - totalPercentDiscountInService
                    //                        originalOrderDetailsAmount = appointmentOrderAmount;
                    changeArray!!.put(capturedNotesId + 1, totalAmountAfterDiscount.toDouble())
                    getInvoiceDetailsData(this@OrderDetailsActivity, orderID, changeArray)
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            } else if (captureNotesText.equals("RemoveProcedure", ignoreCase = true)) {
                if (dataProcedureListModels!!.size > 0) {
                    dataProcedureListModels!!.removeAt(capturedNotesId)
                    val params = JSONObject()
                    try {
                        params.put("appointment_id", appointmentID)
                        arrayProcedureId = JSONArray()
                        if (dataProcedureListModels!!.size > 0) {
                            for (i in dataProcedureListModels!!.indices) {
                                arrayProcedureId!!.put(dataProcedureListModels!![i].appt_service_id)
                            }
                        } else {
                            arrayProcedureId!!.put(0)
                        }
                        originalServiceArrayData!!.remove(capturedNotesId)
                        changeArray!!.remove(capturedNotesId + 1)
                        params.put("services_ids", arrayProcedureId)
                        Log.i("params", params.toString())
                        updateProcedure(this@OrderDetailsActivity, params)
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                }
            }
        }
        services_recycleView.layoutManager = LinearLayoutManager(
            applicationContext,
            LinearLayoutManager.VERTICAL,
            false
        )
        services_recycleView.itemAnimator = DefaultItemAnimator()
        services_recycleView.adapter = paymentServiceAdapter
        payment_invoice_button.setOnClickListener {
            val paymentTransactionBottomSheet = PaymentTransactionBottomSheet()
            paymentTransactionBottomSheet.setupConfig(this@OrderDetailsActivity)
            paymentTransactionBottomSheet.show(
                supportFragmentManager,
                "Bottom Sheet Dialog Fragment"
            )
        }
        layout_addService.setOnClickListener {
            layout_procedureSearch.visibility = View.VISIBLE
        }
        tv_expand.setOnClickListener {
            tv_collapse.visibility = View.VISIBLE
            imgBtn_collapse.visibility = View.VISIBLE
            tv_expand.visibility = View.GONE
            imgBtn_expand.visibility = View.GONE
            layout_costExpand.visibility = View.VISIBLE
        }
        tv_collapse.setOnClickListener {
            tv_collapse.visibility = View.GONE
            imgBtn_collapse.visibility = View.GONE
            tv_expand.visibility = View.VISIBLE
            imgBtn_expand.visibility = View.VISIBLE
            layout_costExpand.visibility = View.GONE
        }
        imgBtn_expand.setOnClickListener {
            tv_collapse.visibility = View.VISIBLE
            imgBtn_collapse.visibility = View.VISIBLE
            tv_expand.visibility = View.GONE
            imgBtn_expand.visibility = View.GONE
            layout_costExpand.visibility = View.VISIBLE
        }
        imgBtn_collapse.setOnClickListener {
            tv_collapse.visibility = View.GONE
            imgBtn_collapse.visibility = View.GONE
            tv_expand.visibility = View.VISIBLE
            imgBtn_expand.visibility = View.VISIBLE
            layout_costExpand.visibility = View.GONE
        }
        actionbar_back.setOnClickListener { finish() }
        val removeDiscount = SpannableString("(Apply Coupon)")
        removeDiscount.setSpan(UnderlineSpan(), 0, removeDiscount.length, 0)
        discountRemoveText.text = removeDiscount
        discountRemoveText.setOnClickListener {
            if (discountRemoveText.text.toString()
                    .equals("(Apply Coupon)", ignoreCase = true)
            ) {
                val dialog = Dialog(this@OrderDetailsActivity)
                dialog.setCancelable(false)
                dialog.setContentView(R.layout.procedure_apply_coupon_popup)
                val applyCouponEditText = dialog.findViewById<EditText>(R.id.applyCouponEditText)
                val applyCouponCardView = dialog.findViewById<CardView>(R.id.applyCouponCardView)
                val dailogArticleCancel = dialog.findViewById<ImageView>(R.id.dailogArticleCancel)
                applyCouponEditText.filters = arrayOf<InputFilter>(InputFilterMinMax(1, 100))
                dailogArticleCancel.setOnClickListener { dialog.dismiss() }
                applyCouponCardView.setOnClickListener {
                    /*Removed the crash issue*/
                    //Start
                    val couponValue = applyCouponEditText.text.toString()
                    if (!couponValue.equals("", ignoreCase = true)) {
                        val removeDiscount1 = SpannableString("(Remove)")
                        removeDiscount1.setSpan(UnderlineSpan(), 0, removeDiscount1.length, 0)
                        discountRemoveText.text = removeDiscount1
                        dialog.dismiss()
                        val applycouponValue = couponValue.toInt()
                        try {
                            val totalPercent = applycouponValue.toFloat()
                            totalPercentDiscount =
                                appointmentOrderAmount.toFloat() / 100 * totalPercent
                            totalPercentDiscount =
                                Math.round(totalPercentDiscount * 100).toFloat() / 100
                            val totalAmountAfterDiscount =
                                appointmentOrderAmount - totalPercentDiscount
                            originalOrderDetailsAmount = appointmentOrderAmount
                            changeArray!!.put(0, totalAmountAfterDiscount.toDouble())
                            getInvoiceDetailsData(this@OrderDetailsActivity, orderID, changeArray)
                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }
                        //End
                    }
                }
                dialog.show()
            } else if (discountRemoveText.text.toString()
                    .equals("(Remove)", ignoreCase = true)
            ) {
                val removeDiscount12 = SpannableString("(Apply Coupon)")
                removeDiscount12.setSpan(UnderlineSpan(), 0, removeDiscount12.length, 0)
                discountRemoveText.text = removeDiscount12
                try {
                    originalOrderDetailsAmount = appointmentOrderAmount
                    changeArray!!.put(0, originalOrderDetailsAmount)
                    totalPercentDiscount = 0f
                    getInvoiceDetailsData(this@OrderDetailsActivity, orderID, changeArray)
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }
        }


        // Spinner Drop down elements
        val categories: MutableList<String> = ArrayList()
        categories.add("Change Appt. Status")
        categories.add("Cancelled By You")
        categories.add("Cancelled By Patient")
        categories.add("Your No Show")
        categories.add("Patient No Show")
        categories.add("Consulted")

        // Creating adapter for spinner
        val dataAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categories)

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        // attaching data adapter to spinner
        appointmentChangeSpinner.adapter = dataAdapter
        appointmentChangeSpinner.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>?, view: View, i: Int, l: Long) {
                val spinnerSelectedValue = appointmentChangeSpinner.selectedItem.toString()
                if (spinnerSelectedValue.equals("Consulted", ignoreCase = true)) {
                    apptStatusValue = 3
                    cancelApptSingle(appointmentID, apptStatusValue)
                } else if (spinnerSelectedValue.equals("Cancelled By Patient", ignoreCase = true)) {
                    apptStatusValue = 5
                    cancelApptSingle(appointmentID, apptStatusValue)
                } else if (spinnerSelectedValue.equals("Cancelled By You", ignoreCase = true)) {
                    apptStatusValue = 8
                    cancelApptSingle(appointmentID, apptStatusValue)
                } else if (spinnerSelectedValue.equals("Your No Show", ignoreCase = true)) {
                    apptStatusValue = 7
                    cancelApptSingle(appointmentID, apptStatusValue)
                } else if (spinnerSelectedValue.equals("Patient No Show", ignoreCase = true)) {
                    apptStatusValue = 6
                    cancelApptSingle(appointmentID, apptStatusValue)
                }
            }

            override fun onNothingSelected(adapterView: AdapterView<*>?) {}
        }


        //for payment mode
        // Spinner Drop down elements
        val paymentMode: MutableList<String> = ArrayList()
        paymentMode.add("Select Payment Mode")
        paymentMode.add("Cash")
        paymentMode.add("Credit Card")
        paymentMode.add("Debit Card")
        paymentMode.add("Net Banking")

        // Creating adapter for spinner
        val dataAdapterPaymentMode =
            ArrayAdapter(this, android.R.layout.simple_spinner_item, paymentMode)

        // Drop down layout style - list view with radio button
        dataAdapterPaymentMode.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        // attaching data adapter to spinner
        spinner_paymentMode.adapter = dataAdapterPaymentMode
        val orderDetailsPaymentTimeLineRecyclerView =
            findViewById<View>(R.id.recycler_view) as RecyclerView
        orderDetailsPaymentTimeLineAdapter = OrderDetailsPaymentTimeLineAdapter(
            orderDetailsPaymentTimeLineList
        )
        val layoutManager = LinearLayoutManager(applicationContext)
        layoutManager.orientation = LinearLayoutManager.VERTICAL
        orderDetailsPaymentTimeLineRecyclerView.layoutManager = layoutManager
        orderDetailsPaymentTimeLineRecyclerView.itemAnimator = DefaultItemAnimator()
        orderDetailsPaymentTimeLineRecyclerView.adapter = orderDetailsPaymentTimeLineAdapter
        cancelRefund.setOnClickListener { cancelRefund(orderID) }
        refundNow.setOnClickListener {
            val orderDetailsRefundNowBottomSheet = OrderDetailsRefundNowBottomSheet()
            orderDetailsRefundNowBottomSheet.setupConfig(this@OrderDetailsActivity, "TotalOverView",mContext)
            orderDetailsRefundNowBottomSheet.show(
                supportFragmentManager,
                "Bottom Sheet Dialog Fragment"
            )
        }
        viewReceipt.setOnClickListener { view: View? ->
            if (receivedAndCreateReceiptText.text.toString()
                    .equals("Received and Create Receipt", ignoreCase = true)
            ) {
                if (globalClass!!.isOnline) {
                    if (spinner_paymentMode.selectedItemPosition == 0) {
                        Toast.makeText(
                            this@OrderDetailsActivity,
                            "Please select payment mode",
                            Toast.LENGTH_LONG
                        ).show()
                    } else {
                        val paymentModespin = spinner_paymentMode.selectedItem.toString()
                        var paymentMode1 = ""
                        if (paymentModespin.equals("Cash", ignoreCase = true)) {
                            paymentMode1 = "Cash"
                        } else if (paymentModespin.equals("Credit Card", ignoreCase = true)) {
                            paymentMode1 = "CC"
                        }
                        if (paymentModespin.equals("Debit Card", ignoreCase = true)) {
                            paymentMode1 = "DC"
                        }
                        if (paymentModespin.equals("Net Banking", ignoreCase = true)) {
                            paymentMode1 = "Net Banking"
                        } else if (paymentModespin.equals(
                                "Offline Collection",
                                ignoreCase = true
                            )
                        ) {
                            paymentMode1 = "Offline Collection"
                        }
                        val amountPaid = bottom_amount.text.toString()
                        updatePaymentStatus(amountPaid, orderID, paymentMode1, true)
                    }
                } else {
                    globalClass!!.noInternetConnection.showDialog(this@OrderDetailsActivity)
                }
            } else if (receivedAndCreateReceiptText.text.toString()
                    .equals("Create Receipt", ignoreCase = true)
            ) {
                createReceipt(orderID)
            } else if (receivedAndCreateReceiptText.text.toString()
                    .equals("View Receipt", ignoreCase = true)
            ) {
                getReceiptUrl(receiptUrl)
            }
        }
        viewInvoiceSmallLayout.setOnClickListener {
            if (viewInvoiceText.text.toString().equals("View Invoice", ignoreCase = true)) {
                getFileFromUrl(invoiceUrl)
            }
        }
        createInvoice.setOnClickListener { view: View? ->
            if (createInvoiceText.text.toString().equals("View Invoice", ignoreCase = true)) {
                getFileFromUrl(invoiceUrl)
            } else {
                val params = JSONObject()
                try {
                    params.put("order_id", orderID)
                    params.put("pre_tax_amount", invoicePreTaxAmount)
                    params.put("pre_tax_discount", invoicePreTaxDiscount)
                    params.put("pre_tax_total", invoicePreTaxTotal)
                    params.put("total_tax", invoiceTotalTax)
                    params.put("invoice_total_amount", invoiceNetAmount)
                    params.put("post_tax_discount", invoicePostTaxDiscount)
                    params.put("isConsultaionForInvoice", true)
                    params.put("invoice_services", invoiceServiceArrayData)
                    params.put("invoice_grand_amount", invoiceGrandAmount)
                    params.put("invoice_grand_discount", invoiceGrandDiscount)
                    params.put("invoice_grand_pre_tax", invoiceGrandPreTax)
                    params.put("invoice_grand_tax", invoiceGrandTax)
                    params.put("invoice_grand_total", invoiceGrandTotal)
                    //                        params.put("platform", "app");
                    createInvoice(this@OrderDetailsActivity, params)
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }
        }
        searchProcedureListModels = ArrayList()
        dataProcedureListModels = ArrayList()
        procedureSearchAdapter = ProcedureSearchAdapter(
            this@OrderDetailsActivity,
            R.layout.procedure_search_item,
            searchProcedureListModels!!
        )
        procedureSearch.setAdapter(procedureSearchAdapter)
        procedureSearch.onItemClickListener =
            AdapterView.OnItemClickListener { parent, view, position, id ->
                val params = JSONObject()
                try {
                    params.put("appointment_id", appointmentID)
                    val array = JSONArray()
                    for (i in dataProcedureListModels!!.indices) {
                        array.put(dataProcedureListModels!![i].appt_service_id)
                    }
                    params.put("services_ids", array)
                    Log.i("params", params.toString())
                    updateProcedure(this@OrderDetailsActivity, params)
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }
        procedureSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(procedureName: CharSequence, i: Int, i1: Int, i2: Int) {
                if (procedureName.length > 2) {
                    if (globalClass!!.isOnline) {
                        appointmentDetailsViewModel!!.getProcedure(
                            this@OrderDetailsActivity,
                            procedureName.toString(),
                            "" + productId,
                            "asc",
                            "name",
                            10,
                            1
                        ).observe(
                            this@OrderDetailsActivity
                        ) { s ->
                            try {
                                val jsonObject = JSONObject(s)
                                Log.d("procedureName", "procedureName$jsonObject")
                                if (jsonObject.getInt("status_code") == 200) {
                                    val data = jsonObject.getJSONObject("response")
                                        .getJSONObject("response").getJSONArray("data")
                                    if (data.length() == 0) {
                                        Log.i("length", "0")
                                    } else {
                                        searchProcedureListModels!!.clear()
                                        for (pos in 0 until data.length()) {
                                            val procedure = data.getJSONObject(pos)
                                            val addProcedureListModel = AddProcedureListModel()
                                            addProcedureListModel.procedureId =
                                                procedure.getInt("id")
                                            addProcedureListModel.appt_service_id =
                                                procedure.getInt("id")
                                            addProcedureListModel.procedureName =
                                                procedure.getString("name")
                                            addProcedureListModel.charges =
                                                procedure.getInt("pre_tax_amount")
                                            addProcedureListModel.discount = 0
                                            addProcedureListModel.tax = 0
                                            addProcedureListModel.totalAmount =
                                                procedure.getInt("pre_tax_amount")
                                            searchProcedureListModels!!.add(
                                                addProcedureListModel
                                            )
                                            dataProcedureListModels!!.add(addProcedureListModel)
                                        }
                                        procedureSearchAdapter!!.notifyDataSetChanged()
                                    }
                                } else {
                                    errorHandler(this@OrderDetailsActivity, s)
                                }
                            } catch (e: JSONException) {
                                e.printStackTrace()
                            }
                        }
                    } else {
                        globalClass!!.noInternetConnection.showDialog(this@OrderDetailsActivity)
                    }
                }
            }

            override fun afterTextChanged(s: Editable) {}
        })
    }

    override fun onResume() {
        super.onResume()
        if (isScreenRefresh) {
            orderDetailsPaymentTimeLineList.clear()
            getOrderDetailsData(this@OrderDetailsActivity, orderID)
            getServicesForAppointmentData(this@OrderDetailsActivity, appointmentID)
        } else {
            isScreenRefresh = true
        }
    }

    private fun getServicesForAppointmentData(activity: Activity?, appointmentID: Int) {
        appointmentDetailsViewModel!!.getServicesForAppointmentData(activity, appointmentID)
            .observe(this) { s ->
                Log.i("capture notes res", s)
                if (appointmentDetailsProgressbar!!.visibility == View.VISIBLE) {
                    appointmentDetailsProgressbar!!.visibility = View.GONE
                }
                try {
                    val response = JSONObject(s)
                    if (response.getInt("status_code") == 200) {
                        val resObject =
                            response.getJSONObject("response").getJSONObject("response")
                        invoiceServiceArrayAppended = JSONArray()
                        changeArray = JSONArray()
                        originalServiceArrayData = JSONArray()
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
                                totalPercent += taxPercentage
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
                        val appointmentOrderObject =
                            appointmentDataObject.getJSONObject("order")
                        appointmentOrderAmount = appointmentOrderObject.getInt("order_amt")
                        appointmentOrderAmountDiscount =
                            appointmentOrderObject.getInt("discount")
                        appointmentNetAmount = appointmentOrderObject.getInt("net_amount")
                        changeArray!!.put(appointmentOrderObject.getInt("order_amt"))
                        val invoiceServiceArray = resObject.getJSONArray("invoice_services")
                        if (invoiceServiceArray.length() > 0) {
                            for (i in 0 until invoiceServiceArray.length()) {
                                val appointmentInvoiceServiceArrayObject =
                                    invoiceServiceArray.getJSONObject(i)
                                changeArray!!.put(appointmentInvoiceServiceArrayObject.getInt("pre_tax_amount"))
                                originalServiceArrayData!!.put(
                                    appointmentInvoiceServiceArrayObject.getInt("pre_tax_amount")
                                )
                            }
                        }
                        getInvoiceDetailsData(this@OrderDetailsActivity, orderID, changeArray)
                    } else {
                        errorHandler(this@OrderDetailsActivity, s)
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                    Log.d("parserError:", "parserError:$e")
                }
            }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun getOrderDetailsData(activity: Activity, orderID: Int) {
        appointmentDetailsViewModel!!.getOrderDetailsData(activity, orderID)
            .observe(this) { s ->
                Log.i("capture notes res", s)
                if (appointmentDetailsProgressbar!!.visibility == View.VISIBLE) {
                    appointmentDetailsProgressbar!!.visibility = View.GONE
                }
                try {
                    val response = JSONObject(s)
                    if (response.getInt("status_code") == 200) {
                        MyClinicGlobalClass.logUserActionEvent(
                            ApiUrls.doctorId,
                            getString(R.string.TransactionsPageOrderDetailsScreenImpression),
                            null
                        )
                        val resObject =
                            response.getJSONObject("response").getJSONObject("response")
                        serviceMode = resObject.getString("mode")
                        appt_date.text =
                            MessageFormat.format("{0}", resObject.getString("appt_date_time"))
                        patient_application_status.text =
                            MessageFormat.format("{0}", resObject.getString("mode"))
                        order_details_status_value.text =
                            MessageFormat.format("{0}", resObject.getString("appt_status"))
                        apptStatus = resObject.getString("appt_status")
                        apptStatusColor = resObject.getString("appt_status_color")
                        if (resObject.getString("appt_status_color")
                                .equals("red", ignoreCase = true)
                        ) {
                            order_details_status_value.setTextColor(
                                ContextCompat.getColor(this, R.color.colorDanger)
                            )
                        } else if (resObject.getString("appt_status_color")
                                .equals("yellow", ignoreCase = true)
                        ) {
                            order_details_status_value.setTextColor(
                                ContextCompat.getColor(
                                    this,
                                    R.color.colorInfo
                                )
                            )
                        } else if (resObject.getString("appt_status_color")
                                .equals("black", ignoreCase = true)
                        ) {
                            order_details_status_value.setTextColor(
                                ContextCompat.getColor(
                                    this,
                                    R.color.colorBlack
                                )
                            )
                        } else if (resObject.getString("appt_status_color")
                                .equals("green", ignoreCase = true)
                        ) {
                            order_details_status_value.setTextColor(
                                ContextCompat.getColor(
                                    this,
                                    R.color.colorPrimary
                                )
                            )
                        } else if (resObject.getString("appt_status_color")
                                .equals("blue", ignoreCase = true)
                        ) {
                            order_details_status_value.setTextColor(
                                ContextCompat.getColor(
                                    this,
                                    R.color.colorAccent
                                )
                            )
                        }
                        bottom_amount.setText(
                            MessageFormat.format(
                                "{0}",
                                resObject.getString("total_amount")
                            )
                        )
                        bottom_amount_received.text =
                            MessageFormat.format("{0}", resObject.getString("payment_status"))
                        paymentStatus = resObject.getString("payment_status")
                        paymentStatusColor = resObject.getString("payment_status_color")
                        val payment_mode =
                            resObject.optJSONObject("appt")!!.optJSONObject("order")!!
                                .optString("payment_mode")
                        setUpPaymentModeSpinnerUI(
                            resObject.optString("payment_status"),
                            payment_mode
                        )
                        if (resObject.getString("payment_status_color")
                                .equals("red", ignoreCase = true)
                        ) {
                            bottom_amount_received.setTextColor(
                                ContextCompat.getColor(
                                    this,
                                    R.color.colorDanger
                                )
                            )
                        } else if (resObject.getString("payment_status_color")
                                .equals("yellow", ignoreCase = true)
                        ) {
                            bottom_amount_received.setTextColor(
                                ContextCompat.getColor(
                                    this,
                                    R.color.colorInfo
                                )
                            )
                        } else if (resObject.getString("payment_status_color")
                                .equals("black", ignoreCase = true)
                        ) {
                            bottom_amount_received.setTextColor(
                                ContextCompat.getColor(
                                    this,
                                    R.color.colorBlack
                                )
                            )
                        } else if (resObject.getString("payment_status_color")
                                .equals("green", ignoreCase = true)
                        ) {
                            bottom_amount_received.setTextColor(
                                ContextCompat.getColor(
                                    this,
                                    R.color.colorPrimary
                                )
                            )
                        } else if (resObject.getString("payment_status_color")
                                .equals("blue", ignoreCase = true)
                        ) {
                            bottom_amount_received.setTextColor(
                                ContextCompat.getColor(
                                    this,
                                    R.color.colorAccent
                                )
                            )
                        }
                        paymentStatusData.text =
                            MessageFormat.format("{0}", resObject.getString("payment_header"))
                        if (resObject.getString("payment_header_color")
                                .equals("red", ignoreCase = true)
                        ) {
                            paymentStatusData.setTextColor(
                                ContextCompat.getColor(
                                    this,
                                    R.color.colorDanger
                                )
                            )
                        } else if (resObject.getString("payment_header_color")
                                .equals("yellow", ignoreCase = true)
                        ) {
                            paymentStatusData.setTextColor(
                                ContextCompat.getColor(
                                    this,
                                    R.color.colorInfo
                                )
                            )
                        } else if (resObject.getString("payment_header_color")
                                .equals("black", ignoreCase = true)
                        ) {
                            paymentStatusData.setTextColor(
                                ContextCompat.getColor(
                                    this,
                                    R.color.colorBlack
                                )
                            )
                        } else if (resObject.getString("payment_header_color")
                                .equals("green", ignoreCase = true)
                        ) {
                            paymentStatusData.setTextColor(
                                ContextCompat.getColor(
                                    this,
                                    R.color.colorPrimary
                                )
                            )
                        } else if (resObject.getString("payment_header_color")
                                .equals("blue", ignoreCase = true)
                        ) {
                            paymentStatusData.setTextColor(
                                ContextCompat.getColor(
                                    this,
                                    R.color.colorAccent
                                )
                            )
                        }

                        //hide and show cancelRefund and refundNow button
                        if (resObject.getInt("is_show_refund_now") == 1) {
                            refundNow.visibility = View.VISIBLE
                        } else {
                            refundNow.visibility = View.GONE
                        }
                        if (resObject.getInt("is_show_cancel_refund_button") == 1) {
                            cancelRefund.visibility = View.VISIBLE
                        } else {
                            cancelRefund.visibility = View.GONE
                        }


                        // details array data
                        val detailsArrayData = resObject.getJSONArray("details")
                        if (detailsArrayData.length() > 0) {
                            paymentModeLayout.visibility = View.GONE
                            for (i in 0 until detailsArrayData.length()) {
                                val detailsInsideArrayData = detailsArrayData.getJSONArray(i)
                                for (j in 0 until detailsInsideArrayData.length()) {
                                    val detailsInsideObjectData =
                                        detailsInsideArrayData.getJSONObject(j)
                                    val orderDetails = OrderDetailsPaymentTimeLineModel()
                                    orderDetails.paymentStatusHeadingData =
                                        detailsInsideObjectData.getString("text1")
                                    orderDetails.paymentStatusData =
                                        detailsInsideObjectData.getString("text2")
                                    orderDetailsPaymentTimeLineList.add(orderDetails)
                                }
                            }
                            orderDetailsPaymentTimeLineAdapter!!.notifyDataSetChanged()
                        } else {
                            paymentModeLayout.visibility = View.VISIBLE
                        }
                    } else {
                        errorHandler(this@OrderDetailsActivity, s)
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                    Log.d("parserError:", "parserError:$e")
                }
            }
    }

    private fun setUpPaymentModeSpinnerUI(payment_status: String, payment_mode: String) {
        if (payment_status.equals("paid", ignoreCase = true)) {
            spinner_paymentMode.isEnabled = false
            var position = 0
            if (payment_mode.equals("cash", ignoreCase = true)) {
                position = 1
            } else if (payment_mode.equals("cc", ignoreCase = true)) {
                position = 2
            } else if (payment_mode.equals("dc", ignoreCase = true)) {
                position = 3
            } else if (payment_mode.equals("net banking", ignoreCase = true)) {
                position = 4
            } else if (payment_mode.equals("Offline Collection", ignoreCase = true)) {
                position = 5
            }
            spinner_paymentMode.setSelection(position)
        } else {
            spinner_paymentMode.isEnabled = true
        }
    }

    @SuppressLint("SetTextI18n", "NotifyDataSetChanged")
    fun getInvoiceDetailsData(activity: Activity?, orderID: Int, changeArray: JSONArray?) {
        showCustomProgressAlertDialog("", resources.getString(R.string.please_wait))
        appointmentDetailsViewModel!!.getInvoiceData(activity, orderID, changeArray)
            .observe(this) { s ->
                Log.i("invoice details res", s)
                try {
                    dialog.dismiss()
                    invoiceServiceModelList.clear()
                    dataProcedureListModels!!.clear()
                    val response = JSONObject(s)
                    if (response.getInt("status_code") == 200) {
                        val resObject =
                            response.getJSONObject("response").getJSONObject("response")
                        orderDetailsServiceName.text =
                            MessageFormat.format("{0}", serviceMode)
                        if (totalPercentDiscount > 0) {
                            orderDetails_amount.text =
                                MessageFormat.format("{0}", originalOrderDetailsAmount)
                        } else {
                            orderDetails_amount.text = MessageFormat.format(
                                "{0}",
                                resObject.getInt("invoiceNetAmount")
                            )
                        }
                        serviceCost_amount.text =
                            "" + (resObject.getDouble("invoicePreTaxTotal") * 100).roundToInt()
                                .toDouble() / 100
                        gst_Text.text =
                            MessageFormat.format("Tax ({0})", resObject.getString("tax_string"))
                        taxAmt_text.text =
                            "" + (resObject.getDouble("invoiceTotalTax") * 100).roundToInt()
                                .toDouble() / 100
                        if (totalPercentDiscount > 0) {
                            discountText.text =
                                MessageFormat.format("{0}", totalPercentDiscount)
                        } else {
                            discountText.text = MessageFormat.format(
                                "{0}",
                                resObject.getInt("invoicePreTaxDiscount")
                            )
                        }
                        totalPay_amount.text =
                            MessageFormat.format("{0}", resObject.getInt("invoiceNetAmount"))
                        bottom_amount.setText(
                            MessageFormat.format(
                                "{0}",
                                resObject.getInt("invoiceGrandTotal")
                            )
                        )
                        invoicePreTaxAmount = resObject.getDouble("invoicePreTaxAmount")
                        invoicePreTaxDiscount = resObject.getDouble("invoicePreTaxDiscount")
                        invoicePreTaxTotal = resObject.getDouble("invoicePreTaxTotal")
                        invoiceTotalTax = resObject.getDouble("invoiceTotalTax")
                        invoiceNetAmount = resObject.getDouble("invoiceNetAmount")
                        invoicePostTaxDiscount = resObject.getDouble("invoicePostTaxDiscount")
                        invoiceGrandAmount = resObject.getDouble("invoiceGrandAmount")
                        invoiceGrandDiscount = resObject.getDouble("invoiceGrandDiscount")
                        invoiceGrandPreTax = resObject.getDouble("invoiceGrandPreTax")
                        invoiceGrandTax = resObject.getDouble("invoiceGrandTax")
                        invoiceGrandTotal = resObject.getDouble("invoiceGrandTotal")

                        //                       invoice services data
                        invoiceServiceArrayData = resObject.getJSONArray("invoice_services")
                        if (invoiceServiceArrayData!!.length() > 0) {
                            for (i in 0 until invoiceServiceArrayData!!.length()) {
                                val invoiceServiceObjectData =
                                    invoiceServiceArrayData!!.getJSONObject(i)
                                val addProcedure = AddProcedureListModel()
                                addProcedure.procedureId = invoiceServiceObjectData.getInt("id")
                                addProcedure.procedureName =
                                    invoiceServiceObjectData.getString("name")
                                addProcedure.serviceAmount =
                                    invoiceServiceObjectData.getInt("pre_tax_amount")
                                addProcedure.serviceCharges =
                                    invoiceServiceObjectData.getDouble("final_pre_tax_amount")
                                addProcedure.serviceTax =
                                    invoiceServiceObjectData.getDouble("tax_amount")
                                addProcedure.serviceDiscount =
                                    invoiceServiceObjectData.getDouble("discount")
                                addProcedure.totalAmount =
                                    invoiceServiceObjectData.getInt("final_post_tax_amount")
                                addProcedure.taxText =
                                    invoiceServiceObjectData.getString("tax_string")
                                addProcedure.applyCoupon = 0
                                val disCountAmount =
                                    invoiceServiceObjectData.getDouble("discount")
                                addProcedure.isDiscountApplied = disCountAmount != 0.0
                                addProcedure.appt_service_id =
                                    invoiceServiceObjectData.getInt("appt_service_id")
                                invoiceServiceModelList.add(addProcedure)
                                dataProcedureListModels!!.add(addProcedure)
                            }
                            noProcedureText.visibility = View.GONE
                        } else {
                            noProcedureText.visibility = View.VISIBLE
                        }
                        if (discountArrayList!!.size <= 0) {
                            for (i in invoiceServiceModelList.indices) {
                                discountArrayList!!.add(0)
                            }
                        }
                        paymentServiceAdapter!!.notifyDataSetChanged()

                    } else {
                        errorHandler(this@OrderDetailsActivity, s)
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                    Log.d("parserError:", "parserError:$e")
                }
            }
    }

    private fun cancelApptSingle(apptID: Int, status: Int) {
        val reqObj = JSONObject()
        try {
            reqObj.put("appId", apptID)
            reqObj.put("cancel", status)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        appointmentApiRequests!!.postApptApiData(
            ApiUrls.cancelAppt,
            reqObj,
            this,
            object : VolleyCallback {
                @SuppressLint("SetTextI18n")
                override fun onSuccess(result: String) {
                    try {
                        val resObj = JSONObject(result)
                        if (resObj.getInt("response") == 1) {
                            Toast.makeText(
                                this@OrderDetailsActivity,
                                resources.getString(R.string.appt_status_update),
                                Toast.LENGTH_SHORT
                            ).show()
                            when (status) {
                                1 -> {
                                    order_details_status_value.text = "Pending"
                                    order_details_status_value.setTextColor(
                                        ContextCompat.getColor(
                                            this@OrderDetailsActivity,
                                            R.color.colorInfo
                                        )
                                    )
                                }
                                3 -> {
                                    order_details_status_value.text = "Consulted"
                                    order_details_status_value.setTextColor(
                                        ContextCompat.getColor(
                                            this@OrderDetailsActivity,
                                            R.color.colorPrimary
                                        )
                                    )
                                }
                                4 -> {
                                    order_details_status_value.text = "Cancelled"
                                    order_details_status_value.setTextColor(
                                        ContextCompat.getColor(
                                            this@OrderDetailsActivity,
                                            R.color.colorDanger
                                        )
                                    )
                                }
                                5 -> {
                                    order_details_status_value.text = "Cancelled by patient"
                                    order_details_status_value.setTextColor(
                                        ContextCompat.getColor(
                                            this@OrderDetailsActivity,
                                            R.color.colorDanger
                                        )
                                    )
                                }
                                6 -> {
                                    order_details_status_value.text = "Patient no show"
                                    order_details_status_value.setTextColor(
                                        ContextCompat.getColor(
                                            this@OrderDetailsActivity,
                                            R.color.colorDanger
                                        )
                                    )
                                }
                                7 -> {
                                    order_details_status_value.text = "Doctor no-show"
                                    order_details_status_value.setTextColor(
                                        ContextCompat.getColor(
                                            this@OrderDetailsActivity,
                                            R.color.colorDanger
                                        )
                                    )
                                }
                                8 -> {
                                    order_details_status_value.text = "Cancelled by doctor"
                                    order_details_status_value.setTextColor(
                                        ContextCompat.getColor(
                                            this@OrderDetailsActivity,
                                            R.color.colorDanger
                                        )
                                    )
                                }
                            }

                            //for hide and show the apptStatusSpinner
                            if (status == 1) {
                                apptStatusSpinnerLayout.visibility = View.VISIBLE
                            } else {
                                apptStatusSpinnerLayout.visibility = View.GONE
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                override fun onError(err: String) {
                    errorHandler(this@OrderDetailsActivity, err)
                }
            })
    }

    fun updateProcedure(activity: Activity?, params: JSONObject?) {
        appointmentDetailsViewModel!!.updateProcedure(activity, params)
            .observe(this@OrderDetailsActivity) { s ->
                try {
                    val response = JSONObject(s)
                    if (response.getInt("status_code") == 200) {
                        procedureSearch.setText("")
                        getInvoiceDetailsData(this@OrderDetailsActivity, orderID, changeArray)
                    } else {
                        errorHandler(this@OrderDetailsActivity, s)
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
                //                addProcedureProgress.setVisibility(View.GONE);
            }
    }

    @SuppressLint("SetTextI18n")
    fun updatePaymentStatus(
        paidAmount: String,
        orderId: Int,
        paymentMode: String,
        isGeneratedReceipt: Boolean
    ) {
        showCustomProgressAlertDialog(
            resources.getString(R.string.updating),
            resources.getString(R.string.wait_while_we_updating)
        )
        val url = ApiUrls.updatePaymentStatus
        try {
            jsonValue = JSONObject()
            jsonValue.put("order_net_amount", paidAmount.toInt())
            jsonValue.put("order_id", orderId)
            jsonValue.put("order_payment_mode", paymentMode)
            jsonValue.put("isGenerateReport", isGeneratedReceipt)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        commonViewModel.commonViewModelCall(url, jsonValue, Method.POST).observe(
            this@OrderDetailsActivity
        ) { result ->
            try {
                val responseObj = JSONObject(result)
                if (responseObj.getInt("status_code") == 200) {
                    dialog.dismiss()
                    val response = responseObj.optJSONObject("response")
                    val rootObj = response!!.getJSONObject("response")
                    if (isGeneratedReceipt) {
                        val receiptObject = rootObj.optJSONObject("receipt")
                        receiptUrl = receiptObject!!.optString("public_url")
                        val showReceipt = "View Receipt"
                        val content = SpannableString(showReceipt)
                        content.setSpan(UnderlineSpan(), 0, showReceipt.length, 0)
                        receivedAndCreateReceiptText.text = content
                        bottom_amount_received.text = "PAID"
                        bottom_amount_received.setTextColor(
                            ContextCompat.getColor(
                                this@OrderDetailsActivity,
                                R.color.colorPrimary
                            )
                        )
                        setUpPaymentModeSpinnerUI("paid", paymentMode)
                        val intent = Intent()
                        intent.action = "UPDATE_TRANSACTION"
                        intent.putExtra("paymentStatus", "success")
                        intent.putExtra("orderId", orderID)
                        if (receiptUrl != null && !receiptUrl!!.isEmpty()) {
                            intent.putExtra("receiptURL", receiptUrl)
                        }
                        sendBroadcast(intent)
                    }
                } else {
                    dialog.dismiss()
                    errorHandler(this@OrderDetailsActivity, result)
                }
            } catch (e: Exception) {
                dialog.dismiss()
                e.printStackTrace()
            }
        }
    }

    private fun getReceiptUrl(url: String?) {
        val i = Intent(Intent.ACTION_VIEW)
        i.data = Uri.parse(url)
        startActivity(i)
    }

    fun getFileFromUrl(filrUrl: String?) {
        val url = JSONObject()
        try {
            url.put("url", filrUrl)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        patientRecordsApi!!.postRecords(
            ApiUrls.getFileFromUrl,
            url,
            this@OrderDetailsActivity,
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
                    errorHandler(this@OrderDetailsActivity, err)
                }
            })
    }

    @SuppressLint("SetTextI18n")
    fun createInvoice(activity: Activity?, params: JSONObject?) {
        appointmentDetailsViewModel!!.creteInvoice(activity, params)
            .observe(this) { s ->
                try {
                    val response = JSONObject(s)
                    if (response.getInt("status_code") == 200) {
                        val invoiceResponse = response.getJSONObject("response")
                        val invoiceObject = invoiceResponse.getJSONObject("invoice")
                        invoiceUrl = invoiceObject.getString("public_url")
                        Log.d("invoiceUrl", "invoiceUrl$invoiceUrl")
                        Toast.makeText(
                            applicationContext,
                            "Invoice has been generated",
                            Toast.LENGTH_LONG
                        ).show()
                        createInvoiceText.text = "View Invoice"
                        addProcedureAppointmentDetails.isEnabled = false
                        addProcedureAppointmentDetails.setTextColor(
                            ContextCompat.getColorStateList(
                                this,
                                R.color.colorGrey2
                            )
                        )
                        val intent = Intent()
                        intent.action = "UPDATE_TRANSACTION"
                        intent.putExtra("orderId", orderID)
                        intent.putExtra("invoiceURL", invoiceUrl)
                        intent.putExtra(
                            "totalAmountPaidForTransAppoint",
                            bottom_amount.text.toString()
                        )
                        sendBroadcast(intent)
                    } else {
                        errorHandler(this@OrderDetailsActivity, s)
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }
    }

/*
    fun sendPaymentLink() {
        dashboardViewModel!!.sendPaymentReminderDetails(this@OrderDetailsActivity, appointmentID)
            .observe(
                (this@OrderDetailsActivity as LifecycleOwner)
            ) { s ->
                try {
                    val jsonObject = JSONObject(s)
                    if (jsonObject.getInt("status_code") == 200) {
                        val response = JSONObject(s).getJSONObject("response")
                        val responseValue = response.getInt("response")
                        if (responseValue == 1) {
                            Toast.makeText(
                                this@OrderDetailsActivity,
                                "Patient has been notified successfully.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } else {
                        errorHandler(this@OrderDetailsActivity, s)
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }
    }
*/

    private fun createReceipt(orderId: Int) {
        val url = ApiUrls.createReceipt + "?order_id=" + orderId
        showCustomProgressAlertDialog("", resources.getString(R.string.please_wait))

        commonViewModel.commonViewModelCall(url, JSONObject(), Method.GET).observe(
            this@OrderDetailsActivity
        ) { result ->
            try {
                val responseObj = JSONObject(result)
                if (responseObj.getInt("status_code") == 200) {
                    val response = responseObj.optJSONObject("response")
                    val rootObj = response!!.getJSONObject("response")
                    receiptUrl = rootObj.getString("public_url")
                    val showReceipt = "View Receipt"
                    val content = SpannableString(showReceipt)
                    content.setSpan(UnderlineSpan(), 0, showReceipt.length, 0)
                    receivedAndCreateReceiptText.text = content
                    val intent = Intent()
                    intent.action = "UPDATE_TRANSACTION"
                    intent.putExtra("orderId", orderID)
                    if (receiptUrl != null && !receiptUrl!!.isEmpty()) {
                        intent.putExtra("receiptURL", receiptUrl)
                    }
                    sendBroadcast(intent)
                    dialog.dismiss()
                } else {
                    dialog.dismiss()
                    errorHandler(this@OrderDetailsActivity, result)
                }
            } catch (e: Exception) {
                dialog.dismiss()
                e.printStackTrace()
            }
        }
    }

    private fun cancelRefund(order_id: Int) {
        val url = ApiUrls.cancelRefund + "?order_id=" + order_id
        appointmentApiRequests!!.getApptApiData(
            url,
            "",
            this@OrderDetailsActivity,
            object : VolleyCallback {
                override fun onSuccess(result: String) {
                    try {
                        val resObj = JSONObject(result)
                        if (resObj.getInt("response") == 1) {
                            val orderDetailsRefundNowBottomSheet =
                                OrderDetailsRefundNowBottomSheet()
                            orderDetailsRefundNowBottomSheet.setupConfig(
                                this@OrderDetailsActivity,
                                "TotalOverView",
                                mContext
                            )
                            orderDetailsRefundNowBottomSheet.show(
                                supportFragmentManager,
                                "Bottom Sheet Dialog Fragment"
                            )
                            OrderDetailsRefundNowBottomSheet.triggerMessageLayout?.visibility =
                                View.VISIBLE
                            OrderDetailsRefundNowBottomSheet.refundNowViewLayout?.visibility =
                                View.GONE
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                override fun onError(err: String) {
                    errorHandler(this@OrderDetailsActivity, err)
                }
            })
    }

    companion object {
        var appointmentID = 0

        @JvmField
        var orderID = 0
        var productId = 0
        var apptStatusValue = 0

        @JvmField
        var apptStatus: String? = null

        @JvmField
        var apptStatusColor: String? = null

        @JvmField
        var paymentStatus: String? = null

        @JvmField
        var paymentStatusColor: String? = null

        @JvmField
        var refundAmount = 0

        @JvmField
        var netPaidAmount = 0
    }
}