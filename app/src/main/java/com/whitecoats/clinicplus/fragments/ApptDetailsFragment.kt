package com.whitecoats.clinicplus.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.PorterDuff
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.telephony.PhoneStateListener
import android.telephony.TelephonyManager
import android.text.SpannableString
import android.text.style.UnderlineSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request.Method
import com.whitecoats.clinicplus.*
import com.whitecoats.clinicplus.activities.AddProcedureActivity
import com.whitecoats.clinicplus.activities.AppointmentDetailsActivity
import com.whitecoats.clinicplus.activities.PaymentHistoryTimeLineActivity
import com.whitecoats.clinicplus.activities.VideoScreenActivity
import com.whitecoats.clinicplus.adapters.AppointmentDetailsAddProcedureAdapter
import com.whitecoats.clinicplus.apis.ApiGetPostMethodCalls
import com.whitecoats.clinicplus.apis.ApiUrls
import com.whitecoats.clinicplus.fragments.NewAppointmentFragment.Companion.activePastFilter
import com.whitecoats.clinicplus.models.AddProcedureListModel
import com.whitecoats.clinicplus.utils.ErrorHandlerClass.errorHandler
import com.whitecoats.clinicplus.utils.ShowAlertDialog
import com.whitecoats.clinicplus.viewmodels.AppointmentDetailsViewModel
import com.whitecoats.clinicplus.viewmodels.CommonViewModel
import com.whitecoats.clinicplus.viewmodels.DashboardViewModel
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.text.DateFormatSymbols
import java.text.SimpleDateFormat
import java.util.*

class ApptDetailsFragment : Fragment() {
    private var addProcedureRecyclerView: RecyclerView? = null
    private var noProcedureText: TextView? = null
    private var appointmentDetailsAddProcedureAdapter: AppointmentDetailsAddProcedureAdapter? = null
    private var addProcedureListModel = ArrayList<AddProcedureListModel>()
    private lateinit var attendanceAt: TextView
    private lateinit var totalPayValue: TextView
    private lateinit var checkInLayout: RelativeLayout
    private lateinit var payModeLayout: RelativeLayout
    private lateinit var appointmentTypeSpinner: Spinner
    private lateinit var paymentModeSpinner: Spinner
    private var appointmentType = arrayOf<String?>(
        "Select Type",
        "First Visit",
        "Routine",
        "Follow-up",
        "Procedure/Vaccination",
        "Dressing/Plaster",
        "Other"
    )
    var paymentMode =
        arrayOf<String?>("Select Mode", "Cash", "Credit Card", "Debit Card", "Net Banking")
    private var selectedApptType: String? = null
    private var jsonValue: JSONObject? = null
    private var appointmentDetailsViewModel: AppointmentDetailsViewModel? = null
    private lateinit var appointmentDetailsProgressbar: RelativeLayout
    private lateinit var totalAmountEditText: EditText
    private lateinit var gstValue: TextView
    private lateinit var netAmount: TextView
    private lateinit var discount: TextView
    private lateinit var consultationAmount: TextView
    private var totalAmountCalculated = 0
    private lateinit var amountPaidLayout: RelativeLayout
    private lateinit var confirmButton: RelativeLayout
    private lateinit var addProcedureAppointmentDetails: TextView
    private var discountArrayList: ArrayList<Int>? = null
    private var clickedAppointmentID = 0
    private lateinit var attendanceLayoutHideShow: RelativeLayout
    private lateinit var addProcedureLayout: RelativeLayout
    private lateinit var totalPayLayout: RelativeLayout
    private lateinit var startConsultationButton: TextView
    private lateinit var appointmentDetailsBottomLayout: RelativeLayout
    private var scheduleStartTime: String? = null
    private var appointmentPostion = 0
    private var appUtilities: AppUtilities? = null
    private lateinit var joinVideoLayout: RelativeLayout
    private var callCurrentState = 0
    private var globalClass: MyClinicGlobalClass? = null
    private var dashboardViewModel: DashboardViewModel? = null
    private lateinit var consultationTime: TextView
    private lateinit var cancelApptLayout: RelativeLayout
    private lateinit var toApptsPage: RelativeLayout
    private lateinit var viewInvoice: RelativeLayout
    private lateinit var apptStatusLayout: RelativeLayout
    private lateinit var appointmentStatusValue: TextView
    private lateinit var attendanceTextLayout: RelativeLayout
    private lateinit var attendanceLayout: RelativeLayout
    var radioGroup: RadioGroup? = null
    private var appointmentApiRequests: AppointmentApiRequests? = null
    private var appointmentID = 0
    private var apptStatusValue = 0
    private lateinit var discountRemoveText: TextView
    private var appointmentOrderAmount = 0
    private var appointmentOrderAmountDiscount = 0
    private var appointmentNetAmount = 0
    private lateinit var decimalValue: TextView
    private var patientRecordsApi: PatientRecordsApi? = null
    private var taxesApplyValueText = 0f
    private lateinit var taxesTextView: TextView
    private var taxesName: String? = null
    private var taxesPercent = 0
    var paymentModeString: String? = null
    private lateinit var invoiceText: TextView
    private var addProcedureClicked = false
    private lateinit var paymentHistoryLayout: RelativeLayout
    private lateinit var receipt_tv: TextView
    private var receiptUrl: String? = null

    /*ENGG-3691 -- Refactoring the code by removing the deprecate functions(StartActivityForResults)*/
    private var launchAddProcedureResults: ActivityResultLauncher<Intent>? = null

    /*ENGG-3785 -- Clinic+ App issues(Android) Added the taxes names with percentage to textview*/
    private var taxesNameBuilder: StringBuilder? = null
    private var taxNameWithPercentage = ""
    private var dateAndTimeFormat: String? = null
    private var globalApiCall: ApiGetPostMethodCalls? = null
    private var telephonyManager: TelephonyManager? = null
    private var callStateListener: PhoneStateListener? = null
    private var requestPermissionLauncher: ActivityResultLauncher<String>? = null
    private lateinit var commonViewModel: CommonViewModel
    private lateinit var dialog: Dialog

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_appt_details, container, false)
        appointmentDetailsViewModel = ViewModelProvider(this)[AppointmentDetailsViewModel::class.java]
        appointmentDetailsViewModel!!.init()
        dashboardViewModel = ViewModelProvider(this)[DashboardViewModel::class.java]
        dashboardViewModel!!.init()
        appointmentApiRequests = AppointmentApiRequests()
        patientRecordsApi = PatientRecordsApi()
        globalApiCall = ApiGetPostMethodCalls()
        commonViewModel = ViewModelProvider(requireActivity())[CommonViewModel::class.java]
        appointmentDetailsActivity = activity as AppointmentDetailsActivity?
        attendanceAt = view.findViewById(R.id.attendanceAt)
        checkInLayout = view.findViewById(R.id.checkInLayout)
        payModeLayout = view.findViewById(R.id.payModeLayout)
        paymentHistoryLayout = view.findViewById(R.id.paymentHistoryLayout)
        appointmentTypeSpinner = view.findViewById(R.id.appointmentTypeSpinner)
        paymentModeSpinner = view.findViewById(R.id.paymentModeSpinner)
        appointmentDetailsProgressbar = view.findViewById(R.id.appointmentDetailsProgressbar)
        totalAmountEditText = view.findViewById(R.id.totalAmountEditText)
        gstValue = view.findViewById(R.id.gstValue)
        netAmount = view.findViewById(R.id.netAmount)
        discount = view.findViewById(R.id.discount)
        taxesTextView = view.findViewById(R.id.taxesTextView)
        discountRemoveText = view.findViewById(R.id.discountRemoveText)
        decimalValue = view.findViewById(R.id.decimalValue)
        consultationAmount = view.findViewById(R.id.consultationAmount)
        totalPayValue = view.findViewById(R.id.totalPayValue)
        amountPaidLayout = view.findViewById(R.id.amountPaidLayout)
        addProcedureAppointmentDetails = view.findViewById(R.id.addProcedure)
        confirmButton = view.findViewById(R.id.confirmButton)
        joinVideoLayout = view.findViewById(R.id.joinVideoLayout)
        consultationTime = view.findViewById(R.id.consultationTime)
        cancelApptLayout = view.findViewById(R.id.cancelApptLayout)
        toApptsPage = view.findViewById(R.id.ApptPageLayout)
        receipt_tv = view.findViewById(R.id.receipt_tv)
        viewInvoice = view.findViewById(R.id.viewInvoiceLayout)
        invoiceText = view.findViewById(R.id.invoiceText)
        apptStatusLayout = view.findViewById(R.id.apptStatusLayout)
        appointmentStatusValue = view.findViewById(R.id.apptStatusValue)
        attendanceTextLayout = view.findViewById(R.id.attendanceTextLayout)
        attendanceLayout = view.findViewById(R.id.attendanceLayout)
        globalClass = requireActivity().applicationContext as MyClinicGlobalClass
        appointmentDetailsBottomLayout = view.findViewById(R.id.appointmentDetailsBottomLayout)
        attendanceLayoutHideShow = view.findViewById(R.id.attendanceLayoutHideShow)
        addProcedureLayout = view.findViewById(R.id.addProcedureLayout)
        totalPayLayout = view.findViewById(R.id.totalPayLayout)
        startConsultationButton = view.findViewById(R.id.startConsultationButton)
        appointmentPostion = appointmentDetailsActivity!!.appointmentPosition
        receiptUrl = appointmentDetailsActivity!!.receiptUrl
        val removeDiscount = SpannableString("(Remove)")
        removeDiscount.setSpan(UnderlineSpan(), 0, removeDiscount.length, 0)
        discountRemoveText.text = removeDiscount
        MyClinicGlobalClass.logUserActionEvent(
            ApiUrls.doctorId,
            getString(R.string.AppointmentDetailsScreenImpressions),
            null
        )
        appUtilities = AppUtilities()
        dateAndTimeFormat = if (appUtilities!!.timeFormatPreferences(requireContext()) == 12) {
            "dd MMM, yy hh:mm aa"
        } else {
            "dd MMM, yy HH:mm"
        }
        requestPermissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { result: Boolean ->
            if (result) {
                // PERMISSION GRANTED
                telephonyManager!!.listen(callStateListener, PhoneStateListener.LISTEN_CALL_STATE)
            } else {
                ShowAlertDialog().showPopupToMovePermissionPage(requireActivity())
            }
        }

        /*ENGG-3691 -- Refactoring the code by removing the deprecate functions(StartActivityForResults)*/
        //Start
        launchAddProcedureResults = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result: ActivityResult ->
            //Request code 2
            val data: Intent? = result.data
            val resultCode: Int = result.resultCode
            if (resultCode == 2) {
                Log.i("to discount", data!!.getIntegerArrayListExtra("discountList").toString())
                discountArrayList = data.getIntegerArrayListExtra("discountList")
                appointmentDetailsAddProcedureAdapter!!.notifyData(
                    addProcedureListModel,
                    discountArrayList!!
                )
            }
        }
        //End
        startConsultationButton.setOnClickListener({
            MyClinicGlobalClass.logUserActionEvent(
                ApiUrls.doctorId, getString(
                    R.string.AppointmentDetailsStartConsult
                ), null
            )
        })
        if (appointmentDetailsActivity!!.appointmentMode == 1) {
            attendanceLayoutHideShow.visibility = View.GONE
            addProcedureLayout.visibility = View.GONE
            totalPayLayout.visibility = View.GONE
            startConsultationButton.visibility = View.GONE
            viewInvoice.visibility = View.GONE
            appointmentDetailsBottomLayout.visibility = View.GONE
            if (activePastFilter.equals("past", ignoreCase = true)) {
                joinVideoLayout.visibility = View.GONE
                consultationTime.visibility = View.GONE
                discount.isEnabled = false
                discount.setTextColor(ContextCompat.getColor(requireActivity(), R.color.colorBlack))
                totalAmountEditText.isEnabled = false
                cancelApptLayout.visibility = View.GONE
            } else {
                joinVideoLayout.visibility = View.VISIBLE
                consultationTime.visibility = View.VISIBLE
                discount.isEnabled = false
                discount.setTextColor(ContextCompat.getColor(requireActivity(), R.color.colorBlack))
                totalAmountEditText.isEnabled = true
                cancelApptLayout.visibility = View.GONE
            }
        } else {
            joinVideoLayout.visibility = View.GONE
            consultationTime.visibility = View.GONE
            attendanceLayoutHideShow.visibility = View.VISIBLE
            addProcedureLayout.visibility = View.VISIBLE
            totalPayLayout.visibility = View.VISIBLE
            startConsultationButton.visibility = View.VISIBLE
            appointmentDetailsBottomLayout.visibility = View.VISIBLE
            cancelApptLayout.visibility = View.GONE
            viewInvoice.visibility = View.VISIBLE
            if (appointmentDetailsActivity!!.flagCreateInvoiceInComplete) {
                if (appointmentDetailsActivity!!.invoiceUrlCompletedId!!.isEmpty()) {
                    invoiceText.text = "Create Invoice"
                    addProcedureAppointmentDetails.visibility = View.VISIBLE
                } else {
                    invoiceText.text = "View Invoice"
                    addProcedureAppointmentDetails.visibility = View.GONE
                    invoiceUrl = appointmentDetailsActivity!!.invoiceUrlCompletedId
                }
            } else {
                if ((appointmentDetailsActivity != null) && (appointmentDetailsActivity!!.invoiceData != null) && !appointmentDetailsActivity!!.invoiceData!!.isEmpty()) {
                    invoiceText.text = "View Invoice"
                    addProcedureAppointmentDetails.visibility = View.GONE
                    invoiceUrl = appointmentDetailsActivity!!.invoiceData
                } else {
                    invoiceText.text = "Create Invoice"
                    addProcedureAppointmentDetails.visibility = View.VISIBLE
                }
            }
            if (activePastFilter.equals("past", ignoreCase = true)) {
                discount.isEnabled = false
                totalAmountEditText.isEnabled = false
                addProcedureLayout.visibility = View.VISIBLE
            } else {
                addProcedureLayout.visibility = View.GONE
                discount.isEnabled = true
                totalAmountEditText.isEnabled = true
            }
        }
        discount.setOnClickListener {
            val dialog = Dialog(requireActivity())
            dialog.setCancelable(false)
            dialog.setContentView(R.layout.procedure_apply_coupon_popup)
            val applyCouponEditText = dialog.findViewById<EditText>(R.id.applyCouponEditText)
            val applyCouponCardView = dialog.findViewById<CardView>(R.id.applyCouponCardView)
            val dailogArticleCancel = dialog.findViewById<ImageView>(R.id.dailogArticleCancel)
            MyClinicGlobalClass.logUserActionEvent(
                ApiUrls.doctorId, getString(
                    R.string.AppointmentDetailApplyDiscount
                ), null
            )
            dailogArticleCancel.setOnClickListener({ dialog.dismiss() })
            applyCouponCardView.setOnClickListener {
                discountRemoveText.visibility = View.VISIBLE
                decimalValue.text = ""
                dialog.dismiss()
                val discountApplyValue =
                    java.lang.Float.valueOf(applyCouponEditText.text.toString())
                val discountAmountAfterCalculate =
                    appointmentNetAmount.toFloat() / 100 * discountApplyValue
                val appointmentOrderAmountAfterDiscountDeduct =
                    (appointmentOrderAmount.toFloat() - discountAmountAfterCalculate)
                val taxesAmountAfterCalculateText =
                    appointmentOrderAmountAfterDiscountDeduct / 100 * taxesApplyValueText
                val consultationAmountValue =
                    appointmentOrderAmountAfterDiscountDeduct - taxesAmountAfterCalculateText
                totalAmountEditText.setText((appointmentNetAmount - discountAmountAfterCalculate).toString() + "")
                gstValue.text = "Rs $taxesAmountAfterCalculateText"
                netAmount.text =
                    "Rs " + (consultationAmountValue - discountAmountAfterCalculate) + ""
                discount.text = "Rs $discountAmountAfterCalculate"
                consultationAmount.text = "Rs $appointmentNetAmount.0"
                totalPayValue.text =
                    "Rs " + (totalAmountCalculated - discountAmountAfterCalculate) + ""
                /*ENGG-3785 -- Clinic+ App issues(Android) Added the taxes names with percentage to textview*/taxesTextView.text =
                "Taxes($taxNameWithPercentage)"
                preTextAmount = appointmentOrderAmount - taxesAmountAfterCalculateText
                preTextDiscount = discountAmountAfterCalculate
                preTextTotal = (consultationAmountValue - discountAmountAfterCalculate)
                totalTax = taxesAmountAfterCalculateText
                invoiceTotalAmount = consultationAmountValue + taxesAmountAfterCalculateText
                postTaxDiscount = discountAmountAfterCalculate
                invoiceGrandAmount = appointmentOrderAmount - taxesAmountAfterCalculateText
                invoice_grand_discount = discountAmountAfterCalculate
                invoice_grand_pre_tax =
                    (consultationAmountValue - discountAmountAfterCalculate)
                invoice_grand_tax = taxesAmountAfterCalculateText
                invoice_grand_total =
                    consultationAmountValue + taxesAmountAfterCalculateText
            }
            dialog.show()
        }
        discountRemoveText.setOnClickListener {
            discountRemoveText.visibility = View.GONE
            decimalValue.text = ""
            addProcedureListModel.clear()
            if (clickedAppointmentID == 0) {
                getServicesForAppointmentData(
                    activity,
                    appointmentDetailsActivity!!.appointmentID
                )
            } else {
                getServicesForAppointmentData(activity, clickedAppointmentID)
            }
        }
        joinVideoLayout.setOnClickListener {
            MyClinicGlobalClass.logUserActionEvent(
                ApiUrls.doctorId, getString(
                    R.string.AppointmentDetailsJoin
                ), null
            )
            if (appointmentDetailsActivity!!.video_tool == 1) {
                joinVideo(appointmentDetailsActivity!!.appointmentID)
            } else {
                /*Added the null check condition to avoid the crash if doctor_join_erl is empty or null*/
                if ((appointmentDetailsActivity!!.doctor_join_url == null) || appointmentDetailsActivity!!.doctor_join_url.equals(
                        "null",
                        ignoreCase = true
                    ) || appointmentDetailsActivity!!.doctor_join_url!!.isEmpty()
                ) {
                    Toast.makeText(activity, "Video Link Not Updated", Toast.LENGTH_SHORT)
                        .show()
                } else {
                    try {
                        if (appointmentDetailsActivity!!.doctor_join_url!!.startsWith(
                                "http://",
                                0
                            ) || appointmentDetailsActivity!!.doctor_join_url!!.startsWith(
                                "https://",
                                0
                            )
                        ) {
                            Log.i("doctorURL","url")
                        } else {
                            appointmentDetailsActivity!!.doctor_join_url =
                                "http://" + appointmentDetailsActivity!!.doctor_join_url
                        }
                        val browserIntent = Intent(
                            Intent.ACTION_VIEW, Uri.parse(
                                appointmentDetailsActivity!!.doctor_join_url
                            )
                        )
                        requireActivity().startActivity(browserIntent)
                    } catch (e: Exception) {
                        e.printStackTrace()
                        Toast.makeText(activity, "Invalid URL", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
        noProcedureText = view.findViewById(R.id.no_procedure_text)
        discountArrayList = ArrayList()
        addProcedureRecyclerView =
            view.findViewById<View>(R.id.addProcedureRecyclerView) as RecyclerView
        appointmentDetailsAddProcedureAdapter = AppointmentDetailsAddProcedureAdapter(
            addProcedureListModel,
            discountArrayList!!
        ) { capturedNotesId, captureNotesText, _, _ ->
            if (captureNotesText.equals("DiscountRemove", ignoreCase = true)) {
                discountArrayList!!.removeAt(capturedNotesId)
                discountArrayList!!.add(capturedNotesId, 0)
                addProcedureListModel.clear()
                getServicesForAppointmentData(
                    activity,
                    appointmentDetailsActivity!!.appointmentID
                )
            } else if (captureNotesText.equals("applyDiscount", ignoreCase = true)) {
                val dialog = Dialog(requireActivity())
                dialog.setCancelable(false)
                dialog.setContentView(R.layout.procedure_apply_coupon_popup)
                val applyCouponEditText =
                    dialog.findViewById<EditText>(R.id.applyCouponEditText)
                val applyCouponCardView =
                    dialog.findViewById<CardView>(R.id.applyCouponCardView)
                val dailogArticleCancel =
                    dialog.findViewById<ImageView>(R.id.dailogArticleCancel)
                dailogArticleCancel.setOnClickListener { dialog.dismiss() }
                applyCouponCardView.setOnClickListener {
                    val applycouponValue = applyCouponEditText.text.toString().toInt()
                    discountArrayList!!.removeAt(capturedNotesId)
                    discountArrayList!!.add(capturedNotesId, applycouponValue)
                    dialog.dismiss()
                    addProcedureListModel.clear()
                    getServicesForAppointmentData(
                        activity,
                        appointmentDetailsActivity!!.appointmentID
                    )
                }
                dialog.show()
            } else if (captureNotesText.equals("removeProcedure", ignoreCase = true)) {
                addProcedureListModel.removeAt(capturedNotesId)
                discountArrayList!!.removeAt(capturedNotesId)
                val params = JSONObject()
                try {
                    params.put("appointment_id", appointmentDetailsActivity!!.appointmentID)
                    val array = JSONArray()
                    for (i in addProcedureListModel.indices) {
                        array.put(addProcedureListModel[i].procedureId)
                    }
                    params.put("services_ids", array)
                    appointmentDetailsProgressbar.visibility = View.VISIBLE
                    updateProcedure(activity, params)
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }
        }
        val layoutManagerMedication = LinearLayoutManager(activity)
        layoutManagerMedication.orientation = LinearLayoutManager.VERTICAL
        addProcedureRecyclerView!!.layoutManager = layoutManagerMedication
        addProcedureRecyclerView!!.adapter = appointmentDetailsAddProcedureAdapter
        val addmedicine = SpannableString("Add Service")
        addmedicine.setSpan(UnderlineSpan(), 0, addmedicine.length, 0)
        addProcedureAppointmentDetails.text = addmedicine
        val timeFormat: String
        timeFormat = if (appUtilities!!.timeFormatPreferences(requireContext()) == 12) {
            "hh:mm a"
        } else {
            "HH:mm"
        }
        consultationTime.text = "Consultation Time: " + appUtilities!!.changeDateFormat(
            "HH:mm:ss",
            timeFormat,
            appointmentDetailsActivity!!.appointmentTime
        )
        addProcedureAppointmentDetails.setOnClickListener {
            addProcedureClicked = true
            MyClinicGlobalClass.logUserActionEvent(
                ApiUrls.doctorId, getString(
                    R.string.AppointmentDetailsAddProcedure
                ), null
            )
            val intent = Intent(activity, AddProcedureActivity::class.java)
            intent.putExtra("apptId", appointmentDetailsActivity!!.appointmentID)
            intent.putExtra("discountList", discountArrayList)
            Log.i("from frag", discountArrayList.toString())
            intent.putExtra("productId", appointmentDetailsActivity!!.productId)
            launchAddProcedureResults!!.launch(intent)
        }
        confirmButton.setOnClickListener {
            if (paymentModeSpinner.selectedItemPosition == 0) {
                Toast.makeText(activity, "Please select payment mode", Toast.LENGTH_LONG).show()
            } else {
                val amountPaid = totalAmountCalculated.toString()
                updatePaymentStatus(
                    amountPaid,
                    appointmentDetailsActivity!!.appointmentOrderId,
                    paymentModeString,
                    false
                )
            }
        }
        checkInLayout.setOnClickListener {
            MyClinicGlobalClass.logUserActionEvent(
                ApiUrls.doctorId,
                getString(R.string.AppointmentDetailsCheckIn),
                null
            )
            getCheckInStatus(appointmentDetailsActivity!!.scheduleTime)
        }
        val aa: ArrayAdapter<*> =
            ArrayAdapter<Any?>(
                requireActivity(),
                android.R.layout.simple_spinner_item,
                appointmentType
            )
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        appointmentTypeSpinner.adapter = aa
        appointmentTypeSpinner.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View,
                position: Int,
                id: Long
            ) {
                if (position == 0) {
                    //                    Toast.makeText(getActivity(), "Please select appointment type", Toast.LENGTH_LONG).show();
                } else {
                    /*ENGG-3785-- Clinic + App issues Checking the condition and changing the values*/
                    if (position == 1) {
                        selectedApptType = "First Visit"
                        apptId = 6
                    } else if (position == 2) {
                        selectedApptType = "Routine"
                        apptId = 1
                    }
                    if (position == 3) {
                        selectedApptType = "Follow-up"
                        apptId = 2
                    }
                    if (position == 4) {
                        selectedApptType = "Procedure/Vaccination"
                        apptId = 3
                    }
                    if (position == 5) {
                        selectedApptType = "Dressing/Plaster"
                        apptId = 4
                    } else if (position == 6) {
                        selectedApptType = "Other"
                        apptId = 5
                    }
                    updateAppointmentType(
                        selectedApptType,
                        apptId,
                        appointmentDetailsActivity!!.appointmentID
                    )
                }
            } // to close the onItemSelected

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
        val paymentModeAd: ArrayAdapter<*> =
            ArrayAdapter<Any?>(requireActivity(), android.R.layout.simple_spinner_item, paymentMode)
        paymentModeAd.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        paymentModeSpinner.adapter = paymentModeAd
        paymentModeSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View,
                position: Int,
                id: Long
            ) {
                MyClinicGlobalClass.logUserActionEvent(
                    ApiUrls.doctorId, getString(
                        R.string.AppointmentsListPaymentDetails
                    ), null
                )
                if (paymentModeSpinner.selectedItemPosition == 0) {
                    //                    Toast.makeText(getActivity(), "Please select payment mode", Toast.LENGTH_LONG).show();
                } else {
                    val paymentModespin = paymentModeSpinner.selectedItem.toString()
                    paymentModeString = ""
                    if (paymentModespin.equals("Cash", ignoreCase = true)) {
                        paymentModeString = "Cash"
                    } else if (paymentModespin.equals("Credit Card", ignoreCase = true)) {
                        paymentModeString = "CC"
                    }
                    if (paymentModespin.equals("Debit Card", ignoreCase = true)) {
                        paymentModeString = "DC"
                    }
                    if (paymentModespin.equals("Net Banking", ignoreCase = true)) {
                        paymentModeString = "Net Banking"
                    }
                }
            } // to close the onItemSelected

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
        cancelApptLayout.setOnClickListener {
            singleCancelApptPopupCalender(
                appointmentDetailsActivity!!.appointmentID
            )
        }
        toApptsPage.setOnClickListener {
            appointPage = true
            if (receipt_tv.text.toString().equals("Create Receipt", ignoreCase = true)) {
                createReceipt(orderIdAppointment)
            } else {
                getReceiptUrl(receiptUrl)
            }
        }
        viewInvoice.setOnClickListener {
            if (invoiceText.text.toString().equals("View Invoice", ignoreCase = true)) {
                getFileFromUrl(invoiceUrl)
            } else {
                val params = JSONObject()
                try {
                    var totalProcedureAmount = 0
                    var totalProcedureTaxesAmount = 0f
                    var finalProcedureAmount = 0f
                    for (i in 0 until invoiceServiceArrayAppended!!.length()) {
                        val invoiceArrayObject = invoiceServiceArrayAppended!!.getJSONObject(i)
                        val getProcedureAmount = invoiceArrayObject.getInt("pre_tax_amount")
                        totalProcedureAmount = totalProcedureAmount + getProcedureAmount
                        var totalPercent = 0
                        for (j in 0 until invoiceArrayObject.getJSONArray("taxes").length()) {
                            val percentObject =
                                invoiceArrayObject.getJSONArray("taxes").getJSONObject(j)
                            val taxPercentage = percentObject.getInt("percentage")
                            totalPercent = totalPercent + taxPercentage
                        }
                        val totalPercentTax = java.lang.Float.valueOf(totalPercent.toFloat())
                        val taxesAmountAfterCalculateText =
                            invoiceArrayObject.getInt("pre_tax_amount")
                                .toFloat() / 100 * totalPercentTax
                        totalProcedureTaxesAmount =
                            totalProcedureTaxesAmount + taxesAmountAfterCalculateText
                        val finalPostTaxAmount =
                            invoiceArrayObject.getInt("pre_tax_amount") + taxesAmountAfterCalculateText
                        finalProcedureAmount = finalProcedureAmount + finalPostTaxAmount
                    }
                    params.put("order_id", orderIdAppointment)
                    params.put("pre_tax_amount", preTextAmount.toDouble())
                    params.put("pre_tax_discount", preTextDiscount.toDouble())
                    params.put("pre_tax_total", preTextTotal.toDouble())
                    params.put("total_tax", totalTax.toDouble())
                    if (texesResponseArray!!.length() > 0) {
                        params.put("taxes", texesResponseArray)
                    } else {
                        val arrayTexes = JSONArray()
                        params.put("taxes", arrayTexes)
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
                    createInvoice(activity, params)
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }
        }
        addProcedureListModel.clear()
        if (clickedAppointmentID == 0) {
            getServicesForAppointmentData(activity, appointmentDetailsActivity!!.appointmentID)
        } else {
            getServicesForAppointmentData(activity, clickedAppointmentID)
        }
        paymentHistoryLayout.setOnClickListener {
            val intent = Intent(activity, PaymentHistoryTimeLineActivity::class.java)
            intent.putExtra("apptID", appointmentDetailsActivity!!.appointmentID)
            intent.putExtra("orderId", appointmentDetailsActivity!!.appointmentOrderId)
            intent.putExtra("refundAmt", appointmentDetailsActivity!!.refundAmount)
            intent.putExtra("receiptUrl", appointmentDetailsActivity!!.receiptUrl)
            intent.putExtra("invoiceData", appointmentDetailsActivity!!.invoiceData)
            startActivity(intent)
        }
        return view
    }

    private fun getReceiptUrl(receiptUrlObj: String?) {
        var receiptUrl = receiptUrlObj
        if (receiptUrl!!.isNotEmpty()) {
            try {
                if (receiptUrl.startsWith("http://", 0) || receiptUrl.startsWith("https://", 0)) {
                Log.i("receiptUrl","url")
                } else {
                    receiptUrl = "http://$receiptUrl"
                }
                val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(receiptUrl))
                requireActivity().startActivity(browserIntent)
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(activity, "Invalid URL", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(activity, "Invalid URL", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showCustomProgressAlertDialog(title: String, progressVal: String) {
        val builder = AlertDialog.Builder(requireActivity())
        builder.setTitle(title)
        builder.setCancelable(false)
        val customLayout: View = layoutInflater.inflate(R.layout.custom_progress_bar, null)
        builder.setView(customLayout)
        dialog = builder.create()
        val tvBody = customLayout.findViewById<TextView>(R.id.tv_value)
        tvBody.text = progressVal
        dialog.show()
    }

    private fun createReceipt(orderIdAppointment: Int) {
        val url = ApiUrls.createReceipt + "?order_id=" + orderIdAppointment

        showCustomProgressAlertDialog(
            "",
            requireActivity().resources.getString(R.string.please_wait)
        )

        commonViewModel.commonViewModelCall(url, JSONObject(), Method.GET).observe(
            requireActivity()
        ) { result ->
            try {
                val responseObj = JSONObject(result)
                if (responseObj.getInt("status_code") == 200) {
                    val response = responseObj.optJSONObject("response")
                    val rootObj = response!!.getJSONObject("response")
                    receiptUrl = rootObj.getString("public_url")
                    appointmentDetailsActivity!!.receiptUrl = receiptUrl
                    val showReceipt = "View Receipt"
                    val content = SpannableString(showReceipt)
                    content.setSpan(UnderlineSpan(), 0, content.length, 0)
                    receipt_tv.text = showReceipt
                    val intent = Intent()
                    // Here you can also put data on intent
                    intent.action = "UPDATE_FRAG_LIST"
                    requireActivity().sendBroadcast(intent)
                    dialog.dismiss()
                } else {
                    dialog.dismiss()
                    errorHandler(requireContext(), result)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                dialog.dismiss()
            }
        }
    }

    fun trimMessage(json: String?, key: String?): String? {
        val trimmedString: String? = try {
            val obj = JSONObject(json!!)
            obj.getString(key!!)
        } catch (e: JSONException) {
            e.printStackTrace()
            return null
        }
        return trimmedString
    }

    fun updateAppointmentType(
        selectedApptType: String?, apptTypeId: Int,
        apppointmentId: Int
    ) {
        val url = ApiUrls.updateAppointmentType
        try {
            jsonValue = JSONObject()
            jsonValue!!.put("id", apppointmentId)
            jsonValue!!.put("appt_type", apptTypeId)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        commonViewModel.commonViewModelCall(url, jsonValue!!, Method.POST).observe(
            requireActivity()
        ) { result ->
            try {
                val resObj = JSONObject(result)
                if (resObj.getInt("status_code") == 200) {
                    val intent = Intent()
                    // Here you can also put data on intent
                    intent.action = "APPOINTMENT_TYPE_STATUS"
                    intent.putExtra("apptID", appointmentDetailsActivity!!.appointmentID)
                    intent.putExtra("apptType", apptId)
                    requireActivity().sendBroadcast(intent)
                } else {
                    errorHandler(requireContext(), result)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun updatePaymentStatus(
        paidAmount: String,
        orderId: Int,
        paymentMode: String?,
        isGeneratedReceipt: Boolean
    ) {
        showCustomProgressAlertDialog(
            requireActivity().resources.getString(R.string.updating),
            requireActivity().resources.getString(R.string.wait_while_we_updating)
        )

        val url = ApiUrls.updatePaymentStatus
        try {
            jsonValue = JSONObject()
            jsonValue!!.put("order_net_amount", paidAmount.toInt())
            jsonValue!!.put("order_id", orderId)
            jsonValue!!.put("order_payment_mode", paymentMode)
            jsonValue!!.put("isGenerateReport", isGeneratedReceipt)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        commonViewModel.commonViewModelCall(url, jsonValue!!, Method.POST).observe(
            requireActivity()
        ) { result ->
            try {
                val responseObj = JSONObject(result)
                dialog.dismiss()
                if (responseObj.getInt("status_code") == 200) {
                    // val response = responseObj.optJSONObject("response")
                    toApptsPage.visibility = View.VISIBLE
                    toApptsPage.isEnabled = true
                    toApptsPage.setBackgroundColor(Color.parseColor("#3C8DBC"))
                    amountPaidLayout.visibility = View.VISIBLE
                    payModeLayout.visibility = View.GONE
                    if (appointmentDetailsActivity != null && appointmentDetailsActivity!!.appointActivePast == 1) {
                        addProcedureLayout.visibility = View.GONE
                    } else {
                        addProcedureLayout.visibility = View.VISIBLE
                    }
                    viewInvoice.visibility = View.VISIBLE
                    viewInvoice.isEnabled = true
                    viewInvoice.background.setColorFilter(
                        Color.parseColor("#3C8DBC"),
                        PorterDuff.Mode.SRC_ATOP
                    )
                    val intent = Intent()
                    // Here you can also put data on intent
                    intent.action = "PAYMENT_STATUS_FLAG"
                    intent.putExtra("apptID", appointmentDetailsActivity!!.appointmentID)
                    intent.putExtra("apptType", apptId)
                    requireActivity().sendBroadcast(intent)
                } else {
                    errorHandler(requireContext(), result)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                dialog.dismiss()
            }
        }
    }

    @SuppressLint("SetTextI18n")
    fun saveCheckedInStatus(appointmentId: Int, checkInStatus: Int) {
        appointmentDetailsViewModel!!.saveCheckedInStatus(
            activity,
            appointmentId,
            checkInStatus
        )
            .observe(requireActivity()) { s ->
                try {
                    val response = JSONObject(s)
                    if (response.getInt("status_code") == 200) {
                        //String date = new SimpleDateFormat("dd MMM, yy").format(new Date());
                        val sdf = SimpleDateFormat(dateAndTimeFormat, Locale.ENGLISH)
                        val symbols = DateFormatSymbols(Locale.US)
                        sdf.dateFormatSymbols = symbols
                        attendanceAt.text =
                            "Checkin @ " + sdf.format(Date(System.currentTimeMillis()))
                        checkInLayout.visibility = View.GONE
                        paymentHistoryLayout.visibility = View.VISIBLE //change
                    } else {
                        errorHandler(requireContext(), s)
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }
    }

    @SuppressLint("SetTextI18n")
    override fun onResume() {
        super.onResume()
        if (addProcedureClicked) {
            addProcedureListModel.clear()
            if (clickedAppointmentID == 0) {
                getServicesForAppointmentData(
                    activity,
                    appointmentDetailsActivity!!.appointmentID
                )
            } else {
                getServicesForAppointmentData(activity, clickedAppointmentID)
            }
        }
        if (appointmentDetailsActivity!!.receiptUrl != null && !appointmentDetailsActivity!!.receiptUrl!!.isEmpty()) {
            receiptUrl = appointmentDetailsActivity!!.receiptUrl
            receipt_tv.text = "View Receipt"
        } else {
            receipt_tv.text = "Create Receipt"
        }
        if (appointmentDetailsActivity!!.invoiceData != null && !appointmentDetailsActivity!!.invoiceData!!.isEmpty()) {
            invoiceUrl = appointmentDetailsActivity!!.invoiceData
            invoiceText.text = "View Invoice"
        } else {
            invoiceText.text = "Create Invoice"
        }
    }

    @SuppressLint("SetTextI18n")
    fun getServicesForAppointmentData(activity: Activity?, appointmentID: Int) {
        appointmentDetailsViewModel!!.getServicesForAppointmentData(activity, appointmentID)
            .observe(
                viewLifecycleOwner
            ) { s ->
                Log.i("capture notes res", s)
                if (appointmentDetailsProgressbar.visibility == View.VISIBLE) {
                    appointmentDetailsProgressbar.visibility = View.GONE
                }
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
                                val percentObject = invoiceArrayObject.getJSONArray("taxes")
                                    .getJSONObject(j)
                                val taxPercentage = percentObject.getInt("percentage")
                                totalPercent = totalPercent + taxPercentage
                            }
                            val totalPercentTax =
                                java.lang.Float.valueOf(totalPercent.toFloat())
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
                        scheduleStartTime =
                            appointmentDataObject.getString("scheduled_start_time")
                        val appointmentType = appointmentDataObject.getInt("appt_type")
                        if (addProcedureClicked) {
                            addProcedureClicked = false
                        } else {
                            when (appointmentType) {
                                0 -> {
                                    appointmentTypeSpinner.setSelection(0)
                                }
                                6 -> {
                                    appointmentTypeSpinner.setSelection(1)
                                }
                                1 -> {
                                    appointmentTypeSpinner.setSelection(2)
                                }
                                2 -> {
                                    appointmentTypeSpinner.setSelection(3)
                                }
                                3 -> {
                                    appointmentTypeSpinner.setSelection(4)
                                }
                                4 -> {
                                    appointmentTypeSpinner.setSelection(5)
                                }
                                5 -> {
                                    appointmentTypeSpinner.setSelection(6)
                                }
                            }
                        }
                        val checkedInStatus = appointmentDataObject.getInt("checked_in")
                        val appointmentOrderObjectPayment =
                            appointmentDataObject.getJSONObject("order")
                        appointmentOrderPaymentStatus =
                            appointmentOrderObjectPayment.getString("payment_status")
                        if (checkedInStatus == 1) {
                            val checkedInAt =
                                appointmentDataObject.getString("checked_in_at")
                            val separatedInteractionDate = checkedInAt.split(" ".toRegex())
                                .dropLastWhile { it.isEmpty() }
                                .toTypedArray()
                            val createInteractionDate = separatedInteractionDate[0]
                            val interactionDate = appUtilities!!.changeDateFormat(
                                "yyyy-MM-dd HH:mm:ss",
                                dateAndTimeFormat,
                                checkedInAt
                            )
                            val createdInteractionTime =
                                separatedInteractionDate[1].substring(
                                    0,
                                    separatedInteractionDate[1].length - 3
                                )
                            if (apptStatus == 1) {
                                apptStatusLayout.visibility = View.GONE
                                attendanceTextLayout.visibility = View.VISIBLE
                                attendanceLayout.visibility = View.VISIBLE
                                cancelApptLayout.visibility = View.GONE
                                addProcedureAppointmentDetails.isEnabled = false
                                if (appointmentOrderPaymentStatus.equals(
                                        "Pending",
                                        ignoreCase = true
                                    )
                                ) {
                                    payModeLayout.visibility = View.VISIBLE
                                } else {
                                    payModeLayout.visibility = View.GONE
                                }
                                paymentHistoryLayout.visibility = View.VISIBLE //change
                                checkInLayout.visibility = View.GONE
                                attendanceAt.text = "Checkin @ $interactionDate"
                            } else {
                                apptStatusLayout.visibility = View.VISIBLE
                                attendanceTextLayout.visibility = View.GONE
                                attendanceLayout.visibility = View.GONE
                                cancelApptLayout.visibility = View.GONE
                                if (appointmentOrderPaymentStatus.equals(
                                        "Pending",
                                        ignoreCase = true
                                    )
                                ) {
                                    payModeLayout.visibility = View.VISIBLE
                                } else {
                                    payModeLayout.visibility = View.GONE
                                }
                                paymentHistoryLayout.visibility = View.VISIBLE //change
                                when (apptStatus) {
                                    3 -> {
                                        appointmentStatusValue.text = "Consulted"
                                        appointmentStatusValue.setTextColor(
                                            ContextCompat.getColor(
                                                requireActivity(),
                                                R.color.colorPrimary
                                            )
                                        )
                                        addProcedureAppointmentDetails.isEnabled = true
                                    }
                                    4 -> {
                                        appointmentStatusValue.text = "Cancelled"
                                        appointmentStatusValue.setTextColor(
                                            ContextCompat.getColor(
                                                requireActivity(),
                                                R.color.colorDanger
                                            )
                                        )
                                    }
                                    5 -> {
                                        appointmentStatusValue.text =
                                            "Cancelled by patient"
                                        appointmentStatusValue.setTextColor(
                                            ContextCompat.getColor(
                                                requireActivity(),
                                                R.color.colorDanger
                                            )
                                        )
                                    }
                                    6 -> {
                                        appointmentStatusValue.text = "Patient no show"
                                        appointmentStatusValue.setTextColor(
                                            ContextCompat.getColor(
                                                requireActivity(),
                                                R.color.colorDanger
                                            )
                                        )
                                    }
                                    7 -> {
                                        appointmentStatusValue.text = "Doctor no-show"
                                        appointmentStatusValue.setTextColor(
                                            ContextCompat.getColor(
                                                requireActivity(),
                                                R.color.colorDanger
                                            )
                                        )
                                    }
                                    8 -> {
                                        appointmentStatusValue.text =
                                            "Cancelled by doctor"
                                        appointmentStatusValue.setTextColor(
                                            ContextCompat.getColor(
                                                requireActivity(),
                                                R.color.colorDanger
                                            )
                                        )
                                    }
                                }
                            }
                        } else {
                            if (appointmentDataObject.getInt("mode") == 1) {
                                attendanceLayoutHideShow.visibility = View.GONE
                                addProcedureLayout.visibility = View.GONE
                                totalPayLayout.visibility = View.GONE
                                startConsultationButton.visibility = View.GONE
                                appointmentDetailsBottomLayout.visibility = View.VISIBLE
                                viewInvoice.visibility = View.GONE
                                if (receipt_tv.text.toString()
                                        .equals("Create Receipt", ignoreCase = true)
                                ) {
                                    if (!appointmentOrderPaymentStatus.equals(
                                            "success",
                                            ignoreCase = true
                                        )
                                    ) {
                                        toApptsPage.visibility = View.VISIBLE
                                        toApptsPage.isEnabled = false
                                        toApptsPage.setBackgroundColor(Color.parseColor("#ACACAC"))
                                    }
                                }
                            } else {
                                if (activePastFilter.equals("past", ignoreCase = true)) {
                                    if (apptStatus == 4) {
                                        viewInvoice.visibility = View.GONE
                                        payModeLayout.visibility = View.GONE
                                        paymentHistoryLayout.visibility = View.GONE
                                    } else {
                                        if (appointmentOrderPaymentStatus.equals(
                                                "Pending",
                                                ignoreCase = true
                                            )
                                        ) {
                                            toApptsPage.visibility = View.GONE
                                            if (apptStatus == 3) {
                                                viewInvoice.visibility = View.VISIBLE
                                                payModeLayout.visibility = View.VISIBLE
                                                paymentHistoryLayout.visibility =
                                                    View.VISIBLE //change
                                            } else {
                                                viewInvoice.visibility = View.GONE
                                                payModeLayout.visibility = View.VISIBLE
                                                paymentHistoryLayout.visibility =
                                                    View.VISIBLE //change
                                            }
                                        } else {
                                            toApptsPage.visibility = View.VISIBLE
                                            if (receiptUrl != null && !receiptUrl.equals(
                                                    "",
                                                    ignoreCase = true
                                                )
                                            ) {
                                                receipt_tv.text = "View Receipt"
                                            } else {
                                                receipt_tv.text = "Create Receipt"
                                            }
                                            viewInvoice.visibility = View.VISIBLE
                                            payModeLayout.visibility = View.GONE
                                            paymentHistoryLayout.visibility = View.VISIBLE
                                        }
                                    }
                                } else {
                                    if (apptStatus == 4) {
                                        viewInvoice.visibility = View.GONE
                                        payModeLayout.visibility = View.GONE
                                        paymentHistoryLayout.visibility = View.GONE
                                    } else {
                                        if (appointmentOrderPaymentStatus.equals(
                                                "Pending",
                                                ignoreCase = true
                                            )
                                        ) {
                                            toApptsPage.visibility = View.GONE
                                            if (apptStatus == 3) {
                                                viewInvoice.visibility = View.VISIBLE
                                                viewInvoice.isEnabled = false
                                                viewInvoice.background.setColorFilter(
                                                    Color.parseColor("#ACACAC"),
                                                    PorterDuff.Mode.SRC_ATOP
                                                )
                                                toApptsPage.visibility = View.VISIBLE
                                                toApptsPage.isEnabled = false
                                                toApptsPage.setBackgroundColor(
                                                    Color.parseColor(
                                                        "#ACACAC"
                                                    )
                                                )
                                                payModeLayout.visibility = View.VISIBLE
                                                paymentHistoryLayout.visibility =
                                                    View.VISIBLE //change
                                            } else {
                                                viewInvoice.visibility = View.VISIBLE
                                                toApptsPage.visibility = View.VISIBLE
                                                toApptsPage.isEnabled = false
                                                toApptsPage.setBackgroundColor(
                                                    Color.parseColor(
                                                        "#ACACAC"
                                                    )
                                                )
                                                viewInvoice.isEnabled = false
                                                viewInvoice.visibility = View.VISIBLE
                                                viewInvoice.isEnabled = false
                                                viewInvoice.background.setColorFilter(
                                                    Color.parseColor("#ACACAC"),
                                                    PorterDuff.Mode.SRC_ATOP
                                                )
                                                payModeLayout.visibility = View.VISIBLE
                                                paymentHistoryLayout.visibility =
                                                    View.VISIBLE //change
                                            }
                                        } else {
                                            toApptsPage.visibility = View.VISIBLE
                                            if (receiptUrl != null && !receiptUrl.equals(
                                                    "",
                                                    ignoreCase = true
                                                )
                                            ) {
                                                receipt_tv.text = "View Receipt"
                                            } else {
                                                receipt_tv.text = "Create Receipt"
                                            }
                                            toApptsPage.isEnabled = true
                                            toApptsPage.setBackgroundColor(
                                                Color.parseColor(
                                                    "#3C8DBC"
                                                )
                                            )
                                            viewInvoice.isEnabled = true
                                            viewInvoice.background.setColorFilter(
                                                Color.parseColor(
                                                    "#3C8DBC"
                                                ), PorterDuff.Mode.SRC_ATOP
                                            )
                                            viewInvoice.visibility = View.VISIBLE
                                            payModeLayout.visibility = View.GONE
                                            paymentHistoryLayout.visibility = View.VISIBLE
                                        }
                                    }
                                }
                                if (apptStatus == 1) {
                                    apptStatusLayout.visibility = View.GONE
                                    attendanceTextLayout.visibility = View.VISIBLE
                                    attendanceLayout.visibility = View.VISIBLE
                                    cancelApptLayout.visibility = View.GONE
                                    checkInLayout.visibility = View.VISIBLE
                                    attendanceAt.text = "Not yet in"
                                } else {
                                    apptStatusLayout.visibility = View.VISIBLE
                                    attendanceTextLayout.visibility = View.GONE
                                    attendanceLayout.visibility = View.GONE
                                    cancelApptLayout.visibility = View.GONE
                                    if (appointmentOrderPaymentStatus.equals(
                                            "Pending",
                                            ignoreCase = true
                                        )
                                    ) {
                                        payModeLayout.visibility = View.VISIBLE
                                    } else {
                                        payModeLayout.visibility = View.GONE
                                    }
                                    //                                    payModeLayout.setVisibility(View.VISIBLE);
                                    paymentHistoryLayout.visibility =
                                        View.VISIBLE //change
                                    when (apptStatus) {
                                        3 -> {
                                            appointmentStatusValue.text = "Consulted"
                                            appointmentStatusValue.setTextColor(
                                                ContextCompat.getColor(
                                                    requireActivity(),
                                                    R.color.colorPrimary
                                                )
                                            )
                                            viewInvoice.visibility = View.VISIBLE
                                        }
                                        4 -> {
                                            appointmentStatusValue.text = "Cancelled"
                                            appointmentStatusValue.setTextColor(
                                                ContextCompat.getColor(
                                                    requireActivity(),
                                                    R.color.colorDanger
                                                )
                                            )
                                            viewInvoice.visibility = View.GONE
                                        }
                                        5 -> {
                                            appointmentStatusValue.text =
                                                "Cancelled by patient"
                                            appointmentStatusValue.setTextColor(
                                                ContextCompat.getColor(
                                                    requireActivity(),
                                                    R.color.colorDanger
                                                )
                                            )
                                            viewInvoice.visibility = View.GONE
                                        }
                                        6 -> {
                                            appointmentStatusValue.text =
                                                "Patient no show"
                                            appointmentStatusValue.setTextColor(
                                                ContextCompat.getColor(
                                                    requireActivity(),
                                                    R.color.colorDanger
                                                )
                                            )
                                            viewInvoice.visibility = View.GONE
                                        }
                                        7 -> {
                                            appointmentStatusValue.text = "Doctor no-show"
                                            appointmentStatusValue.setTextColor(
                                                ContextCompat.getColor(
                                                    requireActivity(),
                                                    R.color.colorDanger
                                                )
                                            )
                                            viewInvoice.visibility = View.GONE
                                        }
                                        8 -> {
                                            appointmentStatusValue.text =
                                                "Cancelled by doctor"
                                            appointmentStatusValue.setTextColor(
                                                ContextCompat.getColor(
                                                    requireActivity(),
                                                    R.color.colorDanger
                                                )
                                            )
                                            viewInvoice.visibility = View.GONE
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
                        if (appointmentOrderPaymentStatus.equals(
                                "success",
                                ignoreCase = true
                            )
                        ) {
                            amountPaidLayout.visibility = View.VISIBLE
                            totalAmountEditText.isEnabled = false
                            val appointmentOrderPaymentMode =
                                appointmentOrderObject.getString("payment_mode")
                            if (appointmentOrderPaymentMode.equals(
                                    "Cash",
                                    ignoreCase = true
                                )
                            ) {
                                paymentModeSpinner.setSelection(1)
                            } else if (appointmentOrderPaymentMode.equals(
                                    "CC",
                                    ignoreCase = true
                                )
                            ) {
                                paymentModeSpinner.setSelection(2)
                            } else if (appointmentOrderPaymentMode.equals(
                                    "DC",
                                    ignoreCase = true
                                )
                            ) {
                                paymentModeSpinner.setSelection(3)
                            } else if (appointmentOrderPaymentMode.equals(
                                    "Net Banking",
                                    ignoreCase = true
                                )
                            ) {
                                paymentModeSpinner.setSelection(4)
                            }
                        } else {
                            totalAmountEditText.isEnabled = true
                            paymentModeSpinner.setSelection(0)
                            amountPaidLayout.visibility = View.GONE
                        }
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
                                procedureListModel.discount = 0
                                procedureListModel.totalAmount = 0
                                addProcedureListModel.add(procedureListModel)
                            }
                            if (discountArrayList!!.size <= 0) {
                                for (i in addProcedureListModel.indices) {
                                    discountArrayList!!.add(0)
                                }
                            }
                            appointmentDetailsAddProcedureAdapter!!.notifyData(
                                addProcedureListModel,
                                discountArrayList!!
                            )
                            for (i in addProcedureListModel.indices) {
                                totalProcedureAmount += if (discountArrayList!![i] == 0) {
                                    addProcedureListModel[i].charges
                                } else {
                                    val discountApplyValue = discountArrayList!![i]
                                    val discountAmountAfterCalculate =
                                        (addProcedureListModel[i].charges / 100 * discountApplyValue)
                                    addProcedureListModel[i].charges - discountAmountAfterCalculate
                                }
                            }
                            noProcedureText!!.visibility = View.GONE
                            addProcedureRecyclerView!!.visibility = View.VISIBLE
                            appointmentDetailsAddProcedureAdapter!!.notifyData(
                                addProcedureListModel,
                                discountArrayList!!
                            )
                        } else {
                            noProcedureText!!.visibility = View.VISIBLE
                            addProcedureRecyclerView!!.visibility = View.GONE
                        }
                        totalAmountCalculated =
                            (appointmentOrderAmount + totalProcedureAmount)
                        totalPayValue.text =
                            "Rs " + (appointmentOrderAmount + totalProcedureAmount) + ".00"
                        val getJsonAppointmentOrderObject =
                            appointmentDataObject.getJSONObject("order")
                        orderIdAppointment = getJsonAppointmentOrderObject.getInt("id")
                        if (!getJsonAppointmentOrderObject.isNull("pre_tax_amount")) {
                            preTextAmount =
                                getJsonAppointmentOrderObject.getInt("pre_tax_amount")
                                    .toFloat()
                        }
                        if (!getJsonAppointmentOrderObject.isNull("pre_tax_discount")) {
                            preTextDiscount =
                                getJsonAppointmentOrderObject.getInt("pre_tax_discount")
                                    .toFloat()
                        }
                        if (!getJsonAppointmentOrderObject.isNull("pre_tax_total")) {
                            preTextTotal =
                                getJsonAppointmentOrderObject.getInt("pre_tax_total")
                                    .toFloat()
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
                                getJsonAppointmentOrderObject.getInt("pre_tax_amount")
                                    .toFloat()
                        }
                        if (!getJsonAppointmentOrderObject.isNull("pre_tax_discount")) {
                            invoice_grand_discount =
                                getJsonAppointmentOrderObject.getInt("pre_tax_discount")
                                    .toFloat()
                        }
                        if (!getJsonAppointmentOrderObject.isNull("pre_tax_total")) {
                            invoice_grand_pre_tax =
                                getJsonAppointmentOrderObject.getInt("pre_tax_total")
                                    .toFloat()
                        }
                        if (!getJsonAppointmentOrderObject.isNull("total_tax")) {
                            invoice_grand_tax =
                                getJsonAppointmentOrderObject.getInt("total_tax").toFloat()
                        }
                        if (!getJsonAppointmentOrderObject.isNull("pre_tax_total")) {
                            invoice_grand_total =
                                getJsonAppointmentOrderObject.getInt("pre_tax_total")
                                    .toFloat()
                        }
                        getProductTaxe(product_id)
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                    Log.d("parserError:", "parserError:$e")
                }
            }
    }

    private fun updateProcedure(activity: Activity?, params: JSONObject?) {
        appointmentDetailsViewModel!!.updateProcedure(activity, params)
            .observe(requireActivity()) { s ->
                try {
                    val response = JSONObject(s)
                    if (response.getInt("status_code") == 200) {
                        if (response.getJSONObject("response").getInt("response") == 1) {
                            Toast.makeText(
                                getActivity(),
                                "Procedure successfully removed",
                                Toast.LENGTH_LONG
                            ).show()
                            appointmentDetailsAddProcedureAdapter!!.notifyData(
                                addProcedureListModel,
                                discountArrayList!!
                            )
                            if (addProcedureListModel.size <= 0) {
                                noProcedureText!!.visibility = View.VISIBLE
                                addProcedureRecyclerView!!.visibility = View.GONE
                            } else {
                                noProcedureText!!.visibility = View.GONE
                                addProcedureRecyclerView!!.visibility = View.VISIBLE
                            }
                            addProcedureListModel.clear()
                            if (clickedAppointmentID == 0) {
                                getServicesForAppointmentData(
                                    getActivity(),
                                    appointmentDetailsActivity!!.appointmentID
                                )
                            } else {
                                getServicesForAppointmentData(
                                    getActivity(),
                                    clickedAppointmentID
                                )
                            }
                        } else {
                            Toast.makeText(
                                getActivity(),
                                "UnExpected error has occured, please try again",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    } else {
                        errorHandler(requireContext(), s)
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
                appointmentDetailsProgressbar.visibility = View.GONE
            }
    }

    private fun getCheckInStatus(startTime: String?) {
        appointmentDetailsViewModel!!.getCheckInStatus(activity, startTime).observe(
            viewLifecycleOwner
        ) { s ->
            try {
                val jsonObject = JSONObject(s)
                if (jsonObject.getInt("status_code") == 200) {
                    val response = jsonObject.getJSONObject("response")
                    val status = response.optString("response")
                    if (status.equals("false", ignoreCase = true)) {
                        Toast.makeText(
                            activity,
                            "Attendance can only be updated upto 4 hours after appt time. This is to prevent misleading data",
                            Toast.LENGTH_LONG
                        ).show()
                    } else {
                        saveCheckedInStatus(appointmentDetailsActivity!!.appointmentID, 1)
                    }
                } else {
                    errorHandler(requireContext(), s)
                }
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }
    }

    fun createInvoice(activity: Activity?, params: JSONObject?) {
        appointmentDetailsViewModel!!.creteInvoice(activity, params)
            .observe(requireActivity()) { s ->
                try {
                    val response = JSONObject(s)
                    if (response.getInt("status_code") == 200) {
                        val invoiceResponse =
                            response.getJSONObject("response").getJSONObject("response")
                        invoiceUrl = invoiceResponse.getString("file_path")
                        Log.d("invoiceUrl", "invoiceUrl$invoiceUrl")
                        appointmentDetailsActivity!!.invoiceData = invoiceUrl
                        Toast.makeText(
                            getActivity(),
                            "Invoice has been generated",
                            Toast.LENGTH_LONG
                        ).show()
                        paymentReceivedDialog()
                        val intent = Intent()
                        // Here you can also put data on intent
                        intent.action = "CREATE_INVOICE"
                        intent.putExtra("apptID", appointmentDetailsActivity!!.appointmentID)
                        intent.putExtra("invoiceURL", invoiceUrl)
                        requireActivity().sendBroadcast(intent)
                    } else {
                        errorHandler(requireContext(), s)
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
                appointmentDetailsProgressbar.visibility = View.GONE
            }
    }

    @SuppressLint("SetTextI18n")
    private fun paymentReceivedDialog() {
        val dialog = Dialog(requireActivity())
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.dialog_payment_received)
        val dialogArticleCancel =
            dialog.findViewById<View>(R.id.PaymentReceivedCancel) as ImageView
        val dialogCloseButton = dialog.findViewById<View>(R.id.close_payment_Button) as Button
        dialogArticleCancel.setOnClickListener {
            dialog.dismiss()
            invoiceText.text = "View Invoice"
            addProcedureAppointmentDetails.visibility = View.GONE
        }
        dialogCloseButton.setOnClickListener {
            dialog.dismiss()
            invoiceText.text = "View Invoice"
            addProcedureAppointmentDetails.visibility = View.GONE
        }
        dialog.show()
    }

/*
    fun sendTheFollowUp(activity: Activity?, params: JSONObject?) {
        appointmentDetailsViewModel!!.sendFollowUp(activity, params)
            .observe(requireActivity()) { s ->
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
                            getActivity(),
                            "Updated Successfully.",
                            Toast.LENGTH_LONG
                        ).show()
                    } else {
                        errorHandler(requireContext(), s)
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
                appointmentDetailsProgressbar.visibility = View.GONE
            }
    }
*/

    private fun joinVideo(appointmentId: Int) {
        if (appointmentOrderPaymentStatus.equals("Pending", ignoreCase = true)) {
            val dialog = Dialog(requireActivity())
            dialog.setCancelable(false)
            dialog.setContentView(R.layout.dailog_video_payment_reminder)
            val dailogArticleCancel =
                dialog.findViewById<View>(R.id.dailogArticleCancel) as ImageView
            val joinVideoButtonLayout =
                dialog.findViewById<View>(R.id.joinVideoButtonLayout) as RelativeLayout
            val sendSMSReminderLayout =
                dialog.findViewById<View>(R.id.sendSMSReminderLayout) as RelativeLayout
            dailogArticleCancel.setOnClickListener { dialog.dismiss() }
            joinVideoButtonLayout.setOnClickListener {
                dialog.dismiss()
                telephonyManager =
                    requireContext().getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
                callStateListener = object : PhoneStateListener() {
                    override fun onCallStateChanged(state: Int, incomingNumber: String) {
                        if (state == TelephonyManager.CALL_STATE_RINGING) {
                            callCurrentState = 1
                            Toast.makeText(
                                context!!.applicationContext,
                                "You can't place a video call if you're already on a phone call.",
                                Toast.LENGTH_LONG
                            ).show()
                        } else if (state == TelephonyManager.CALL_STATE_OFFHOOK) {
                            callCurrentState = 2
                            Toast.makeText(
                                context!!.applicationContext,
                                "You can't place a video call if you're already on a phone call.",
                                Toast.LENGTH_LONG
                            ).show()
                        } else {
                            if (callCurrentState == 1 || callCurrentState == 2) {
                                callCurrentState = 0
                            } else {
                                if (globalClass!!.isOnline) {
                                    val intent =
                                        Intent(activity, VideoScreenActivity::class.java)
                                    intent.putExtra("AppointmentId", appointmentId)
                                    activity!!.startActivity(intent)
                                } else {
                                    globalClass!!.noInternetConnection.showDialog(activity)
                                }
                            }
                        }
                    }
                }
                if (Build.VERSION.SDK_INT >= 31) {
                    if ((requireContext().checkSelfPermission(Manifest.permission.READ_PHONE_STATE)
                                != PackageManager.PERMISSION_GRANTED)
                    ) {
                        requestPermissionLauncher!!.launch(Manifest.permission.READ_PHONE_STATE)
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
            }
            sendSMSReminderLayout.setOnClickListener {
                dialog.dismiss()
                dashboardViewModel!!.sendPaymentReminderDetails(activity, appointmentId)
                    .observe(
                        (activity as LifecycleOwner?)!!
                    ) { s ->
                        try {
                            val jsonObject = JSONObject(s)
                            if (jsonObject.getInt("status_code") == 200) {
                                val response = JSONObject(s).getJSONObject("response")
                                val responseValue = response.getInt("response")
                                if (responseValue == 1) {
                                    Toast.makeText(
                                        activity,
                                        "Payment reminder send successfully",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            } else {
                                errorHandler(requireContext(), s)
                            }
                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }
                    }
            }
            dialog.show()
        } else {
            telephonyManager =
                requireContext().getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
            callStateListener = object : PhoneStateListener() {
                override fun onCallStateChanged(state: Int, incomingNumber: String) {
                    if (state == TelephonyManager.CALL_STATE_RINGING) {
                        callCurrentState = 1
                        Toast.makeText(
                            context!!.applicationContext,
                            "You can't place a video call if you're already on a phone call.",
                            Toast.LENGTH_LONG
                        ).show()
                    } else if (state == TelephonyManager.CALL_STATE_OFFHOOK) {
                        callCurrentState = 2
                        Toast.makeText(
                            context!!.applicationContext,
                            "You can't place a video call if you're already on a phone call.",
                            Toast.LENGTH_LONG
                        ).show()
                    } else {
                        if (callCurrentState == 1 || callCurrentState == 2) {
                            callCurrentState = 0
                        } else {
                            if (globalClass!!.isOnline) {
                                val intent = Intent(activity, VideoScreenActivity::class.java)
                                intent.putExtra("AppointmentId", appointmentId)
                                context!!.startActivity(intent)
                            } else {
                                globalClass!!.noInternetConnection.showDialog(activity)
                            }
                        }
                    }
                }
            }
            if (Build.VERSION.SDK_INT >= 31) {
                if (requireContext().checkSelfPermission(Manifest.permission.READ_PHONE_STATE)
                    != PackageManager.PERMISSION_GRANTED
                ) {
                    requestPermissionLauncher!!.launch(Manifest.permission.READ_PHONE_STATE)
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
        }
    }

    private fun singleCancelApptPopupCalender(apptID: Int) {
        val dialog = Dialog(requireActivity())
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.dailog_single_apointment_cancel)
        val dailogArticleCancel =
            dialog.findViewById<View>(R.id.dailogArticleCancel) as ImageView
        val updateStatus = dialog.findViewById<View>(R.id.updateStatus) as RelativeLayout
        radioGroup = dialog.findViewById<View>(R.id.radioGroup) as RadioGroup
        radioGroup!!.clearCheck()
        radioGroup!!.setOnCheckedChangeListener { group, checkedId ->
            val rb = group.findViewById<View>(checkedId) as RadioButton
            appointmentID = apptID
            if (rb.text.toString().equals("Consulted", ignoreCase = true)) {
                apptStatusValue = 3
            } else if (rb.text.toString().equals("Cancelled By Patient", ignoreCase = true)) {
                apptStatusValue = 5
            } else if (rb.text.toString().equals("Cancelled By You", ignoreCase = true)) {
                apptStatusValue = 8
            } else if (rb.text.toString().equals("Your No Show", ignoreCase = true)) {
                apptStatusValue = 7
            } else if (rb.text.toString().equals("Patient No Show", ignoreCase = true)) {
                apptStatusValue = 6
            }
        }
        updateStatus.setOnClickListener {
            cancelApptSingleCalender(
                apptID,
                apptStatusValue,
                dialog
            )
        }
        dailogArticleCancel.setOnClickListener { dialog.dismiss() }
        dialog.show()
    }

    private fun cancelApptSingleCalender(apptID: Int, status: Int, dialog: Dialog) {
        val reqObj = JSONObject()
        try {
            reqObj.put("appId", apptID)
            reqObj.put("cancel", status)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        commonViewModel.commonViewModelCall(ApiUrls.cancelAppt, reqObj, Method.POST).observe(
            requireActivity()
        ) { result ->
            try {
                val resObjRes = JSONObject(result)
                if (resObjRes.getInt("status_code") == 200) {
                    val resObj = resObjRes.optJSONObject("response")
                    if (resObj!!.getString("response").equals("success", ignoreCase = true)) {
                        Toast.makeText(
                            activity,
                            resources.getString(R.string.appt_cancel),
                            Toast.LENGTH_SHORT
                        ).show()
                        addProcedureListModel.clear()
                        if (clickedAppointmentID == 0) {
                            getServicesForAppointmentData(
                                activity,
                                appointmentDetailsActivity!!.appointmentID
                            )
                        } else {
                            getServicesForAppointmentData(activity, clickedAppointmentID)
                        }
                        dialog.dismiss()
                        requireActivity().finish()
                    }
                } else {
                    errorHandler(requireActivity(), result)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun getProductTaxe(product_id: Int) {
        appointmentDetailsViewModel!!.getProductTaxes(activity, product_id).observe(
            viewLifecycleOwner
        ) { s ->
            try {
                val jsonObject = JSONObject(s)
                if (jsonObject.getInt("status_code") == 200) {
                    val response = jsonObject.getJSONObject("response")
                    texesResponseArray = response.getJSONArray("response")
                    var totalTaxes = 0
                    taxesNameBuilder = StringBuilder()
                    if (texesResponseArray!!.length() > 0) {
                        Log.d("TaxesInformation ", "TaxesInformation $response")
                        for (i in 0 until texesResponseArray!!.length()) {
                            val taxesPercentObject = texesResponseArray!!.getJSONObject(i)
                            taxesPercent = taxesPercentObject.getInt("percentage")
                            taxesName = taxesPercentObject.getString("name")
                            totalTaxes += taxesPercent
                            /*ENGG-3785 -- Clinic+ App issues(Android) Added the taxes names with percentage to textview*/if (i != texesResponseArray!!.length() - 1) {
                                taxesNameBuilder!!.append("$taxesPercent% $taxesName+")
                            } else {
                                taxesNameBuilder!!.append("$taxesPercent% $taxesName")
                            }
                            Log.d("totalTaxes ", "totalTaxes $totalTaxes")
                        }
                        if (taxesNameBuilder != null) {
                            taxNameWithPercentage = taxesNameBuilder.toString()
                        }
                        Log.d("netAmount ", "netAmount $appointmentNetAmount")
                        taxesApplyValueText = java.lang.Float.valueOf(totalTaxes.toFloat())
                        val taxesAmountAfterCalculateText =
                            appointmentNetAmount.toFloat() / 100 * taxesApplyValueText
                        val consultationAmountValue =
                            appointmentOrderAmount - taxesAmountAfterCalculateText
                        val discountApplyValue =
                            java.lang.Float.valueOf(appointmentOrderAmountDiscount.toFloat())
                        val discountAmountAfterCalculate =
                            appointmentNetAmount.toFloat() / 100 * discountApplyValue
                        totalAmountEditText.setText((consultationAmountValue + taxesAmountAfterCalculateText).toString() + "" + "")
                        gstValue.text = "Rs $taxesAmountAfterCalculateText"
                        netAmount.text =
                            "Rs " + (consultationAmountValue - discountAmountAfterCalculate) + ""
                        discount.text = "Rs $discountAmountAfterCalculate"
                        consultationAmount.text = "Rs $appointmentOrderAmount.0"
                        /*ENGG-3785 -- Clinic+ App issues(Android) Added the taxes names with percentage to textview*/taxesTextView.text =
                            "Taxes($taxNameWithPercentage)"
                        preTextAmount =
                            appointmentOrderAmount - taxesAmountAfterCalculateText
                        preTextDiscount = discountAmountAfterCalculate
                        preTextTotal =
                            (consultationAmountValue - discountAmountAfterCalculate)
                        totalTax = taxesAmountAfterCalculateText
                        invoiceTotalAmount =
                            consultationAmountValue + taxesAmountAfterCalculateText
                        postTaxDiscount = discountAmountAfterCalculate
                        invoiceGrandAmount =
                            appointmentOrderAmount - taxesAmountAfterCalculateText
                        invoice_grand_discount = discountAmountAfterCalculate
                        invoice_grand_pre_tax =
                            (consultationAmountValue - discountAmountAfterCalculate)
                        invoice_grand_tax = taxesAmountAfterCalculateText
                        invoice_grand_total =
                            consultationAmountValue + taxesAmountAfterCalculateText
                    } else {
                        taxesApplyValueText = java.lang.Float.valueOf(0f)
                        val taxesAmountAfterCalculateText =
                            appointmentNetAmount.toFloat() / 100 * taxesApplyValueText
                        val consultationAmountValue =
                            appointmentOrderAmount - taxesAmountAfterCalculateText
                        val discountApplyValue =
                            java.lang.Float.valueOf(appointmentOrderAmountDiscount.toFloat())
                        val discountAmountAfterCalculate =
                            appointmentNetAmount.toFloat() / 100 * discountApplyValue
                        totalAmountEditText.setText((consultationAmountValue + taxesAmountAfterCalculateText).toString() + "" + "")
                        gstValue.text = "Rs $taxesAmountAfterCalculateText"
                        netAmount.text =
                            "Rs " + (consultationAmountValue - discountAmountAfterCalculate) + ""
                        discount.text = "Rs $discountAmountAfterCalculate"
                        consultationAmount.text = "Rs $appointmentOrderAmount.0"
                        taxesTextView.text = "Taxes"
                        preTextAmount =
                            appointmentOrderAmount - taxesAmountAfterCalculateText
                        preTextDiscount = discountAmountAfterCalculate
                        preTextTotal =
                            (consultationAmountValue - discountAmountAfterCalculate)
                        totalTax = taxesAmountAfterCalculateText
                        invoiceTotalAmount =
                            consultationAmountValue + taxesAmountAfterCalculateText
                        postTaxDiscount = discountAmountAfterCalculate
                        invoiceGrandAmount =
                            appointmentOrderAmount - taxesAmountAfterCalculateText
                        invoice_grand_discount = discountAmountAfterCalculate
                        invoice_grand_pre_tax =
                            (consultationAmountValue - discountAmountAfterCalculate)
                        invoice_grand_tax = taxesAmountAfterCalculateText
                        invoice_grand_total =
                            consultationAmountValue + taxesAmountAfterCalculateText
                    }
                } else {
                    errorHandler(requireContext(), s)
                }
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }
    }

/*
        private fun completedAppt(apptId: Int, status: Int) {

//        loader = new ProgressDialog(this);
//        loader.setMessage(getResources().getString(R.string.process_request));
//        loader.setProgressStyle(ProgressDialog.STYLE_SPINNER);
//        loader.setCancelable(false);
//        loader.show();
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
                activity,
                object : VolleyCallback {
                    override fun onSuccess(result: String) {
                        getServicesForAppointmentData(
                            activity,
                            appointmentDetailsActivity!!.appointmentID
                        )
                    }

                    override fun onError(err: String) {
                        errorHandler(requireActivity(), err)
                    }
                })
        }
*/

    fun getFileFromUrl(filrUrl: String?) {
        val url = JSONObject()
        try {
            url.put("url", filrUrl)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        showCustomProgressAlertDialog(
            "",
            requireActivity().resources.getString(R.string.process_request)
        )

        commonViewModel.commonViewModelCall(ApiUrls.getFileFromUrl, url, Method.POST).observe(
            requireActivity()
        ) { result ->
            try {
                val resObjRes = JSONObject(result)
                if (resObjRes.getInt("status_code") == 200) {
                    dialog.dismiss()
                    val resObj = resObjRes.optJSONObject("response")
                    val browserIntent =
                        Intent(Intent.ACTION_VIEW, Uri.parse(resObj!!.getString("response")))
                    startActivity(browserIntent)
                } else {
                    dialog.dismiss()
                    errorHandler(requireActivity(), result)
                }
            } catch (e: Exception) {
                dialog.dismiss()
                e.printStackTrace()
            }
        }
    }

    companion object {
        var apptId = 0
        var backToDashBoardClicked = false
        var orderIdAppointment = 0
        var preTextAmount = 0f
        var preTextDiscount = 0f
        var preTextTotal = 0f
        var totalTax = 0f
        var invoiceTotalAmount = 0f
        var postTaxDiscount = 0f
        var invoiceGrandAmount = 0f
        var invoice_grand_discount = 0f
        var invoice_grand_pre_tax = 0f
        var invoice_grand_tax = 0f
        var invoice_grand_total = 0f
        var invoiceServiceArray: JSONArray? = null
        var invoiceServiceArrayAppended: JSONArray? = null
        @SuppressLint("StaticFieldLeak")
        var appointmentDetailsActivity: AppointmentDetailsActivity? = null
        var appointmentOrderPaymentStatus: String? = null
        var appointPage = false
        var texesResponseArray: JSONArray? = null
        var invoiceUrl: String? = ""
        fun newInstance(): ApptDetailsFragment {
            return ApptDetailsFragment()
        }
    }
}