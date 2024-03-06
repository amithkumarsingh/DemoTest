package com.whitecoats.clinicplus.fragments

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnFocusChangeListener
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.core.util.Pair
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.datepicker.MaterialDatePicker
import com.wang.avi.AVLoadingIndicatorView
import com.whitecoats.clinicplus.MyClinicGlobalClass
import com.whitecoats.clinicplus.R
import com.whitecoats.clinicplus.adapters.PatientSearchAdapter
import com.whitecoats.clinicplus.adapters.TransactionsListAdapter
import com.whitecoats.clinicplus.apis.ApiUrls
import com.whitecoats.clinicplus.interfaces.PaymentTransactionFilterInterface
import com.whitecoats.clinicplus.utils.ErrorHandlerClass.errorHandler
import com.whitecoats.clinicplus.utils.RestUtils
import com.whitecoats.clinicplus.utils.SharedPref
import com.whitecoats.clinicplus.utils.WrapContentLinearLayoutManager
import com.whitecoats.clinicplus.viewmodels.AppointmentDetailsViewModel
import com.whitecoats.clinicplus.viewmodels.PaymentTransactionViewModel
import com.whitecoats.clinicplus.viewmodels.SearchPatientViewModel
import com.whitecoats.model.PatientPListModel
import com.whitecoats.model.PaymentTransactionModel
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

class TransactionsFragment : Fragment() {
    private var transactionsListAdapter: TransactionsListAdapter? = null
    private lateinit var transactionsRecycleView: RecyclerView
    private lateinit var daysTransaction_spinner: Spinner
    var dateFilterSelection = ""
    private lateinit var layout_filter: RelativeLayout
    private lateinit var layout_days: RelativeLayout
    private var transactionsFilterBottomSheet: TransactionsFilterBottomSheet? = null
    lateinit var transaction_noText: TextView
    private lateinit var paymentsTransactionsText: TextView
    private lateinit var patient_autoSearchView: AutoCompleteTextView
    private var onTextChangePatientName: CharSequence? = null
    var paymentMode: String? = null
    var apptStatus: String? = null
    var page = 50
    private lateinit var transaction_NoPatient_layout: LinearLayout
    var c = 0
    private var groupData: ArrayList<Int>? = null
    private var globalClass: MyClinicGlobalClass? = null
    private val param = JSONObject()
    private var patientPListModelList: MutableList<PatientPListModel>? = null
    private var patientSearchAdapter: PatientSearchAdapter? = null
    private var paymentTransactionViewModel: PaymentTransactionViewModel? = null
    private var searchPatientViewModel: SearchPatientViewModel? = null
    private lateinit var orderValue: JSONObject
    private lateinit var productValue: JSONObject
    private lateinit var orderUser: JSONObject
    private var profileName: String? = null
    private var profilePhNumber: String? = null
    private var orderDetails_date: String? = null
    private var patient_payment_status: String? = null
    private var net_total_paid_amount: String? = null
    private var refund_amt: String? = null
    var status = 0
    private var refund_status = 0
    private var is_refund_processed = 0
    private var is_do_auto_refund = 0
    private var is_settlement_processed = 0
    private var is_settlement_triggered = 0
    private var selectedFromDate = ""
    private var selectedToDate = ""
    private val sortby = "order_date"
    private val sortorder = "desc"
    private var daysTransactionAdapter: ArrayAdapter<*>? = null
    private val daysTransaction =
        arrayOf<String?>("All", "Today", "This Week", "This Month", "This Year", "Specific Dates")
    private var appointmentDetailsViewModel: AppointmentDetailsViewModel? = null
    private var sharedPref: SharedPref? = null
    var appointmentId = 0
    var orderId = 0
    private lateinit var dates: Array<String>
    private var dontDisplayCalender = false
    private var check = 0
    private var mLinearLayoutManager: WrapContentLinearLayoutManager? = null
    private var loading = false
    private var isListExhausted = false
    private var aviInTransactionTab: AVLoadingIndicatorView? = null
    private lateinit var transactionsTabFilterText: TextView
    private lateinit var emptyText: TextView
    private var broadcastReceiver: BroadcastReceiver? = null
    var generalID: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        registerTransactionUpdateReceiver()
    }

    @SuppressLint("NotifyDataSetChanged", "SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_transactions, container, false)
        transactionsRecycleView = view.findViewById(R.id.recyclerView_transactions)
        patient_autoSearchView = view.findViewById(R.id.transaction_search_patient)
        layout_filter = view.findViewById(R.id.layout_transaction_filter)
        layout_days = view.findViewById(R.id.layout_transaction_days)
        daysTransaction_spinner = view.findViewById(R.id.days_type_transaction)
        transaction_noText = view.findViewById(R.id.transaction_no_record_text)
        paymentsTransactionsText =
            view.findViewById<View>(R.id.paymentsTransactionsText) as TextView
        transaction_NoPatient_layout = view.findViewById(R.id.layout_transaction_no_record)
        aviInTransactionTab =
            view.findViewById<View>(R.id.aviInTransactionTab) as AVLoadingIndicatorView
        transactionsTabFilterText =
            view.findViewById<View>(R.id.transactionsTabFilterText) as TextView
        emptyText = view.findViewById<View>(R.id.emptyText) as TextView
        globalClass = requireActivity().applicationContext as MyClinicGlobalClass
        paymentTransactionViewModel =
            ViewModelProvider(this)[PaymentTransactionViewModel::class.java]
        paymentTransactionViewModel!!.init()
        searchPatientViewModel = ViewModelProvider(this)[SearchPatientViewModel::class.java]
        searchPatientViewModel!!.init()
        patientPListModelList = ArrayList()
        groupData = ArrayList()
        patientSearchAdapter =
            PatientSearchAdapter(
                requireContext(),
                R.layout.patient_search_item,
                patientPListModelList!!
            )
        patient_autoSearchView.setAdapter(patientSearchAdapter)
        appointmentDetailsViewModel = ViewModelProvider(this)[AppointmentDetailsViewModel::class.java]
        appointmentDetailsViewModel!!.init()
        paymentTransactionModelList = ArrayList()
        paymentTransactionModelList!!.clear()
        if (sharedPref == null) {
            sharedPref = SharedPref(activity)
        }
        /* if (sharedPref.getPref("is_show_general_id", "").equalsIgnoreCase("1")) {
           patient_autoSearchView.setHint(getString(R.string.patient_search_text_withID));
       }*/
        modePaymentAllFilter = requireArguments().getString("ModePaymentAllFilter")
        apptStatusAllFilter = requireArguments().getString("Status")
        pendingSettlementFilter = requireArguments().getInt("PendingSettlementFilter")
        settlementDoneFilter = requireArguments().getInt("SettlementDoneFilter")
        pendingRefundFilter = requireArguments().getInt("PendingRefundFilter")
        refundCompletedFilter = requireArguments().getInt("RefundCompletedFilter")
        partialSettlementDoneFilter = requireArguments().getInt("PartialSettlementDoneFilter")
        partialSettlePendingFilter = requireArguments().getInt("PartialSettlePendingFilter")
        scheduledRefundFilter = requireArguments().getInt("ScheduledRefundFilter")
        receivedPatientFilter = requireArguments().getInt("paymentCompletedFilter")
        if (requireArguments().getInt("filterCount") > 0) {
            count = requireArguments().getInt("filterCount")
        }
        layout_filter.background =
            ContextCompat.getDrawable(requireActivity(),R.drawable.transaction_filters_active)
        transactionsTabFilterText.text = count.toString() + ""
        paymentsTransactionsText.text = "Filters"
        paymentsTransactionsText.setTextColor(
            ContextCompat.getColor(
                requireActivity(),
                R.color.colorAccent
            )
        )
        daysTransactionAdapter = ArrayAdapter<Any?>(
            requireActivity(), android.R.layout.simple_spinner_dropdown_item, daysTransaction
        )
        daysTransactionAdapter!!.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        daysTransaction_spinner.adapter = daysTransactionAdapter
        val filterVale = sharedPref!!.getPref<String>("FilterPaymentOverView")
        if (filterVale != null) {
            if (filterVale.contains("All")) {
                daysTransaction_spinner.setSelection(0)
            } else if (filterVale.contains("Week")) {
                dateFilterSelection = "This Week"
                daysTransaction_spinner.setSelection(2)
            } else if (filterVale.contains("Month")) {
                dateFilterSelection = "This Month"
                daysTransaction_spinner.setSelection(3)
            } else if (filterVale.contains("Year")) {
                dateFilterSelection = "This Year"
                daysTransaction_spinner.setSelection(4)
            } else if (filterVale.contains("Specific_")) {
                dates =
                    filterVale.split("_".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                dateFilterSelection = "Specific"
                selectedFromDate = dates[1]
                selectedToDate = dates[2]
                daysTransaction_spinner.setSelection(5)
                dontDisplayCalender = true
            }
        } else {
            daysTransaction_spinner.setSelection(0)
        }
        if (paymentTransactionModelList!!.isNotEmpty()) {
            paymentTransactionModelList!!.clear()
            transactionsListAdapter!!.notifyDataSetChanged()
        }
        getTransactionsListResponse(

            searchFilter,
        )
        transactionsFilterBottomSheet = TransactionsFilterBottomSheet()
        transactionsListAdapter =
            TransactionsListAdapter(requireContext(), requireActivity(), paymentTransactionModelList!!)
        mLinearLayoutManager = WrapContentLinearLayoutManager(activity)
        mLinearLayoutManager!!.orientation = LinearLayoutManager.VERTICAL
        transactionsRecycleView.layoutManager = mLinearLayoutManager
        transactionsRecycleView.adapter = transactionsListAdapter
        transactionsRecycleView.addOnScrollListener(object : RecyclerView.OnScrollListener() {

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (isListExhausted) {
                    return
                }
                val toatlcount = mLinearLayoutManager!!.itemCount
                val lastitem = mLinearLayoutManager!!.findLastVisibleItemPosition()
                if (!loading) {
                    if (lastitem != RecyclerView.NO_POSITION && lastitem == toatlcount - 1) {
                        pageNumber += 1
                        getTransactionsListResponse(
                            searchFilter
                        )
                        loading = true
                    } else {
                        loading = false
                    }
                }
                super.onScrolled(recyclerView, dx, dy)
            }
        })
        daysTransaction_spinner.onFocusChangeListener = OnFocusChangeListener { v, hasFocus ->
            //spinner item selected
        }
        daysTransaction_spinner.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View,
                position: Int,
                id: Long
            ) {
                if (++check > 1) {
                    val selectedItem = parent.getItemAtPosition(position).toString()
                    //Today selection
                    if (selectedItem.equals("All", ignoreCase = true)) {
                        dateFilterSelection = ""
                    }
                    if (selectedItem.equals("Today", ignoreCase = true)) {
                        dateFilterSelection = "Today"
                    }
                    //This week Selection
                    if (selectedItem.equals("This Week", ignoreCase = true)) {
                        dateFilterSelection = "This Week"
                    }
                    //This Month selection
                    if (selectedItem.equals("This Month", ignoreCase = true)) {
                        dateFilterSelection = "This Month"
                    }
                    //This Year selection
                    if (selectedItem.equals("This Year", ignoreCase = true)) {
                        dateFilterSelection = "This Year"
                    }
                    //specific date selection
                    if (selectedItem.equals("Specific Dates", ignoreCase = true)) {
                        dateFilterSelection = "Specific"
                        if (!dontDisplayCalender) {
                            val materialDateBuilder: MaterialDatePicker.Builder<*> =
                                MaterialDatePicker.Builder.dateRangePicker()
                            // now define the properties of the
                            // materialDateBuilder that is title text as SELECT A DATE
                            materialDateBuilder.setTitleText("SELECT A DATE")
                            // now create the instance of the material date
                            // picker
                            val materialDatePicker = materialDateBuilder.build()
                            materialDatePicker.show(
                                requireActivity().supportFragmentManager,
                                "MATERIAL_DATE_PICKER"
                            )
                            // handle select date button which opens the
                            // material design date picker
                            // now handle the positive button click from the
                            // material design date picker
                            materialDatePicker.addOnPositiveButtonClickListener{
                                val selection = it as Pair<Long, Long>
                                val selectedFrom = selection.first
                                val firstDate = Date(selectedFrom)
                                val selectedTo = selection.second
                                val secondDate = Date(selectedTo)
                                val sdf2 = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                                //format yyyy-MM-dd
                                selectedFromDate = sdf2.format(firstDate)
                                selectedToDate = sdf2.format(secondDate)
                                if (paymentTransactionModelList!!.isNotEmpty()) {
                                    paymentTransactionModelList!!.clear()
                                    transactionsListAdapter!!.notifyDataSetChanged()
                                }
                                getTransactionsListResponse(
                                    searchFilter
                                )
                            }
                        }
                    }
                    if (!selectedItem.equals("Specific Dates", ignoreCase = true)) {
                        if (paymentTransactionModelList!!.isNotEmpty()) {
                            paymentTransactionModelList!!.clear()
                            transactionsListAdapter!!.notifyDataSetChanged()
                        }
                        getTransactionsListResponse(
                            searchFilter
                        )
                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
        patient_autoSearchView.onItemClickListener =
            AdapterView.OnItemClickListener { parent, _, position, _ ->
                val patientPList = parent.getItemAtPosition(position) as PatientPListModel
                searchFilter = patientPList.getPatientName()
                patientPListModelList!!.clear()
                Log.i("search test", "onclick")
                if (paymentTransactionModelList!!.isNotEmpty()) {
                    paymentTransactionModelList!!.clear()
                    transactionsListAdapter!!.notifyDataSetChanged()
                }
                getTransactionsListResponse(
                    searchFilter,
                )
            }
        patient_autoSearchView.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(
                patientName: CharSequence,
                start: Int,
                before: Int,
                count: Int
            ) {
                onTextChangePatientName = patientName
                if (patientName.isEmpty()) {
                    searchFilter = ""
                    pageNumber = 1
                    c = 0
                    groupData!!.clear()
                    patientPListModelList!!.clear()
                    if (paymentTransactionModelList!!.isNotEmpty()) {
                        paymentTransactionModelList!!.clear()
                        transactionsListAdapter!!.notifyDataSetChanged()
                    }
                    getTransactionsListResponse(
                        searchFilter
                    )
                }
                if (patientName.length > 2) {
                    if (globalClass!!.isOnline) {
                        searchPatientViewModel!!.searchPatient(activity, patientName.toString())
                            .observe(
                                viewLifecycleOwner
                            ) { s ->
                                try {
                                    val jsonObject = JSONObject(s)
                                    if (jsonObject.getInt("status_code") == 200) {
                                        val patientDetails =
                                            jsonObject.getJSONObject("response")
                                                .getJSONArray("response")
                                        if (patientDetails.length() == 0) {
                                            transaction_NoPatient_layout.visibility =
                                                View.VISIBLE
                                            transaction_noText.text =
                                                "No Transaction found for the patient '$patientName'"
                                            transactionsRecycleView.visibility = View.GONE
                                        } else {
                                            patientPListModelList!!.clear()
                                            for (i in 0 until patientDetails.length()) {
                                                val patientDetail =
                                                    patientDetails.getJSONObject(i)
                                                val assignCategory =
                                                    patientDetail.getJSONArray("assignedCategories")
                                                val patientPListModel = PatientPListModel()
                                                patientPListModel.setPatientName(
                                                    patientDetail.getString("fullname")
                                                )
                                                /*New Registration(Autogenerated ID) changes for Gastro interface*/
                                                patientPListModel.setGeneralID(
                                                    patientDetail.getString("general_id")
                                                )
                                                patientPListModel.patientId =
                                                    patientDetail.getInt("id")
                                                patientPListModel.setEmailid(
                                                    patientDetail.getString(
                                                        "email"
                                                    )
                                                )
                                                patientPListModel.setPhNo(
                                                    patientDetail.getString(
                                                        "phone"
                                                    )
                                                )
                                                patientPListModel.patientAge =
                                                    patientDetail.getString("age")
                                                patientPListModel.patientGender =
                                                    patientDetail.getInt("gender")
                                                patientPListModel.assignCategory =
                                                    assignCategory
                                                patientPListModelList!!.add(patientPListModel)
                                            }
                                            patientSearchAdapter!!.notifyDataSetChanged()
                                        }
                                    } else {
                                        errorHandler(requireContext(), s)
                                    }
                                } catch (e: JSONException) {
                                    e.printStackTrace()
                                }
                            }
                    } else {
                        globalClass!!.noInternetConnection.showDialog(activity)
                    }
                }
            }

            override fun afterTextChanged(s: Editable) {}
        })

        transactionsFilterBottomSheet!!.setupBottomSheet(
            modeOfConsultFilter,
            modePaymentAllFilter!!,
            paymentStatusFilter,
            pendingPatientFilter,
            receivedPatientFilter,
            pendingSettlementFilter,
            partialSettlePendingFilter,
            settlementDoneFilter,
            partialSettlementDoneFilter,
            scheduledRefundFilter,
            pendingRefundFilter,
            refundCompletedFilter,
            apptStatusAllFilter!!, object : PaymentTransactionFilterInterface{
                override fun applyFilter(
                    modeOfConsultValue: String?,
                    paymentModeFilter: String?,
                    paymentStatusFilter: String?,
                    ApptStatusFilter: String?,
                    modePaymentAll: Int,
                    modePaymentOnline: Int,
                    modePaymentOffline: Int,
                    pendingPatientAll: Int,
                    paymentCompleted: Int,
                    settlementPending: Int,
                    refundSchedule: Int,
                    refundCompleted: Int,
                    partialRefundPending: Int,
                    partialRefundCompleted: Int,
                    paymentPendings: Int,
                    settlementCompleted: Int,
                    refundPending: Int,
                    apptStatusAll: Int,
                    statusActive: Int,
                    statusCompleted: Int,
                    count: Int, clearFilterClicked: Boolean
                ) {
                    pageNumber = 1
                    modeOfConsultFilter = modeOfConsultValue!!
                    modePaymentAllFilter = paymentModeFilter
                    pendingPatientAllFilter = pendingPatientAll
                    pendingPatientFilter = paymentPendings
                    receivedPatientFilter = paymentCompleted
                    pendingSettlementFilter = settlementPending
                    partialSettlePendingFilter = partialRefundPending
                    settlementDoneFilter = settlementCompleted
                    partialSettlementDoneFilter = partialRefundCompleted
                    scheduledRefundFilter = refundSchedule
                    pendingRefundFilter = refundPending
                    refundCompletedFilter = refundCompleted
                    apptStatusAllFilter = ApptStatusFilter
                    if (count > 0) {
                        layout_filter.background = ContextCompat.getDrawable(
                            requireActivity(),
                            R.drawable.transaction_filters_active
                        )
                        transactionsTabFilterText.text = count.toString() + ""
                        paymentsTransactionsText.text = "Filters"
                        paymentsTransactionsText.setTextColor(
                            ContextCompat.getColor(
                                requireActivity(),
                                R.color.colorAccent
                            )
                        )
                    }
                    if (!clearFilterClicked) {
                        if (paymentTransactionModelList!!.isNotEmpty()) {
                            paymentTransactionModelList!!.clear()
                            transactionsListAdapter!!.notifyDataSetChanged()
                        }
                        getTransactionsListResponse(
                            searchFilter
                        )
                    }                }

            })
      /*  transactionsFilterBottomSheet!!.setupBottomSheet(
            modeOfConsultFilter,
            modePaymentAllFilter!!,
            paymentStatusFilter,
            pendingPatientFilter,
            receivedPatientFilter,
            pendingSettlementFilter,
            partialSettlePendingFilter,
            settlementDoneFilter,
            partialSettlementDoneFilter,
            scheduledRefundFilter,
            pendingRefundFilter,
            refundCompletedFilter,
            apptStatusAllFilter!!
        ) { modeOfConsultValue: String?, paymentModeFilter: String?, paymentStatusFilter: String?, ApptStatusFilter: String?, modePaymentAll: Int, modePaymentOnline: Int, modePaymentOffline: Int, pendingPatientAll: Int, paymentCompleted: Int, settlementPending: Int, refundSchedule: Int, refundCompleted: Int, partialRefundPending: Int, partialRefundCompleted: Int, paymentPendings: Int, settlementCompleted: Int, refundPending: Int, apptStatusAll: Int, statusActive: Int, statusCompleted: Int, count: Int, clearFilterClicked: Boolean ->
            pageNumber = 1
            modeOfConsultFilter = modeOfConsultValue!!
            modePaymentAllFilter = paymentModeFilter
            pendingPatientAllFilter = pendingPatientAll
            pendingPatientFilter = paymentPendings
            receivedPatientFilter = paymentCompleted
            pendingSettlementFilter = settlementPending
            partialSettlePendingFilter = partialRefundPending
            settlementDoneFilter = settlementCompleted
            partialSettlementDoneFilter = partialRefundCompleted
            scheduledRefundFilter = refundSchedule
            pendingRefundFilter = refundPending
            refundCompletedFilter = refundCompleted
            apptStatusAllFilter = ApptStatusFilter
            if (count > 0) {
                layout_filter.background = ContextCompat.getDrawable(
                    requireActivity(),
                    R.drawable.transaction_filters_active
                )
                transactionsTabFilterText.text = count.toString() + ""
                paymentsTransactionsText.text = "Filters"
                paymentsTransactionsText.setTextColor(
                    ContextCompat.getColor(
                        requireActivity(),
                        R.color.colorAccent
                    )
                )
            }
            if (!clearFilterClicked) {
                if (paymentTransactionModelList!!.isNotEmpty()) {
                    paymentTransactionModelList!!.clear()
                    transactionsListAdapter!!.notifyDataSetChanged()
                }
                getTransactionsListResponse(
                    searchFilter
                )
            }
        }*/
        layout_filter.setOnClickListener {
            transactionsFilterBottomSheet!!.show(
                requireActivity().supportFragmentManager,
                "Bottom Sheet Dialog Fragment"
            )
            MyClinicGlobalClass.logUserActionEvent(
                ApiUrls.doctorId,
                getString(R.string.TransactionsPageFiltersScreenImpression),
                null
            )
        }
        return view
    }

    @SuppressLint("NotifyDataSetChanged")
    fun getTransactionsListResponse(
        searchText: String?,
    ) {
        if (globalClass!!.isOnline) {
            try {
                param.put("per_page", page)
                param.put("page", pageNumber)
                if (searchFilter.isEmpty()) {
                    param.put("search", searchText)
                } else {
                    param.put("search", searchFilter)
                }
                param.put("mode", modeOfConsultFilter)
                param.put("payment_is_all", pendingPatientAllFilter)
                param.put("payment_is_pending", pendingPatientFilter)
                param.put("payment_is_received", receivedPatientFilter)
                param.put("payment_pendingsettlement", pendingSettlementFilter)
                param.put("payment_partialrefundpending", partialSettlePendingFilter)
                param.put("payment_completesettlement", settlementDoneFilter)
                param.put("payment_partialrefunddone", partialSettlementDoneFilter)
                param.put("payment_schedulerefund", scheduledRefundFilter)
                param.put("payment_pendingrefund", pendingRefundFilter)
                param.put("payment_completerefund", refundCompletedFilter)
                param.put("payment", modePaymentAllFilter)
                param.put("status", apptStatusAllFilter)
                param.put("date_filter", dateFilterSelection)
                //param.put("date_filter", "Specific");
                if (dateFilterSelection.equals("Specific", ignoreCase = true)) {
                    param.put("fromDate", selectedFromDate)
                    param.put("toDate", selectedToDate)
                }
                param.put("sortby", sortby)
                param.put("sortorder", sortorder)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            aviInTransactionTab!!.visibility = View.VISIBLE
            paymentTransactionViewModel!!.getTransactionsDetails(
                activity,
                page,
                pageNumber,
                searchText!!,
                modeOfConsultFilter,
                pendingPatientAllFilter,
                pendingPatientFilter,
                receivedPatientFilter,
                pendingSettlementFilter,
                partialSettlePendingFilter,
                settlementDoneFilter,
                partialSettlementDoneFilter,
                scheduledRefundFilter,
                pendingRefundFilter,
                refundCompletedFilter,
                modePaymentAllFilter!!,
                apptStatusAllFilter!!,
                dateFilterSelection,
                selectedFromDate,
                selectedToDate,
                sortby,
                sortorder
            ).observe(requireActivity()) { s ->
                loading = false
                dontDisplayCalender = false
                Log.d("Response", s)
                aviInTransactionTab!!.visibility = View.GONE
                try {
                    var resObj = JSONObject(s)
                    var responseLength = 0
                    if (resObj.getInt("status_code") == 200) {
                        MyClinicGlobalClass.logUserActionEvent(
                            ApiUrls.doctorId,
                            getString(R.string.TransactionsScreenImpression),
                            null
                        )
                        resObj = resObj.getJSONObject(RestUtils.TAG_RESPONSE)
                            .getJSONObject(RestUtils.TAG_RESPONSE)
                        val current_page = resObj.optInt("current_page")
                        val last_page = resObj.optInt("last_page")
                        if (last_page == current_page) {
                            isListExhausted = true
                        }
                        val jsonArrayData = resObj.getJSONArray(RestUtils.TAG_DATA)
                        if (jsonArrayData.length() > 0) {
                            emptyText.visibility = View.GONE
                            for (i in 0 until jsonArrayData.length()) {
                                responseLength++
                                val tempobj = jsonArrayData.getJSONObject(i)
                                orderValue = tempobj.getJSONObject(RestUtils.TAG_ORDER)
                                productValue = orderValue.getJSONObject(RestUtils.TAG_PRODUCTS)
                                orderUser = orderValue.getJSONObject(RestUtils.TAG_ORDER_USER)
                                status = tempobj.optInt(RestUtils.TAG_STATUS)
                                orderDetails_date = orderValue.optString("created_at")
                                patient_payment_status =
                                    orderValue.optString(RestUtils.TAG_PAYMENTS_PAYMENT_STATUS)
                                refund_status =
                                    orderValue.optInt(RestUtils.TAG_PAYMENTS_REFUND_STATUS)
                                is_refund_processed =
                                    orderValue.optInt(RestUtils.TAG_PAYMENTS_REFUND_PROCESSED)
                                net_total_paid_amount =
                                    orderValue.optString(RestUtils.TAG_PAYMENTS_NET_TOTAL_PAID)
                                refund_amt =
                                    orderValue.optString(RestUtils.TAG_PAYMENTS_REFUND_AMT)
                                is_do_auto_refund =
                                    orderValue.optInt(RestUtils.TAG_PAYMENTS_DO_AUTO_REFUND)
                                is_settlement_processed =
                                    orderValue.optInt(RestUtils.TAG_PAYMENTS_SETTLEMENT_PROCESSED)
                                is_settlement_triggered =
                                    orderValue.optInt(RestUtils.TAG_PAYMENTS_SETTLEMENT_TRIGGERED)
                                refund_amt =
                                    orderValue.optString(RestUtils.TAG_PAYMENTS_REFUND_AMT)
                                profileName = orderUser.optString("fname")
                                /*New Registration(Autogenerated ID) changes for Gastro interface*/
                                generalID =
                                    orderUser.optString("general_id")
                                profilePhNumber = orderUser.optString("phone")
                                val paymentTransactionModel = PaymentTransactionModel()
                                val intervention = orderValue.get("receipt")
                                when (intervention) {
                                    is JSONArray -> {
                                        // It's an array
                                    }
                                    is JSONObject -> {
                                        // It's an object
                                        val receiptObject = orderValue.getJSONObject("receipt")
                                        paymentTransactionModel.receiptURL =
                                            receiptObject.getString("public_url")
                                        paymentTransactionModel.orderReceipt =
                                            receiptObject.getString("public_url")
                                    }
                                    else -> {
                                        paymentTransactionModel.receiptURL = ""
                                        // It's something else, like a string or number
                                    }
                                }
                                val interventionInvoice = orderValue.get("invoice")
                                when (interventionInvoice) {
                                    is JSONArray -> {
                                        // It's an array
                                    }
                                    is JSONObject -> {
                                        // It's an object
                                        val receiptObject = orderValue.getJSONObject("invoice")
                                        paymentTransactionModel.invoiceUrl =
                                            receiptObject.getString("public_url")
                                        paymentTransactionModel.orderInvoice =
                                            receiptObject.getString("public_url")
                                    }
                                    else -> {
                                        paymentTransactionModel.invoiceUrl = ""
                                        // It's something else, like a string or number
                                    }
                                }
                                var invoiceObject: JSONObject? = null
                                if (!orderValue.isNull("invoice")) {
                                    invoiceObject = orderValue.getJSONObject("invoice")
                                }
                                paymentTransactionModel.invoiceData = invoiceObject
                                paymentTransactionModel.order_details_date = orderDetails_date
                                paymentTransactionModel.status = status
                                paymentTransactionModel.patientName = profileName

                                paymentTransactionModel.generalID = generalID


                                paymentTransactionModel.ph_number = profilePhNumber
                                paymentTransactionModel.patientPaymentStatus =
                                    patient_payment_status
                                paymentTransactionModel.amount_paid = net_total_paid_amount
                                paymentTransactionModel.refund_status = refund_status
                                paymentTransactionModel.is_refund_processed =
                                    is_refund_processed
                                paymentTransactionModel.net_total_paid = net_total_paid_amount
                                paymentTransactionModel.refund_amt = refund_amt
                                paymentTransactionModel.is_do_auto_refund = is_do_auto_refund
                                paymentTransactionModel.is_settlement_processed =
                                    is_settlement_processed
                                paymentTransactionModel.is_settlement_triggered =
                                    is_settlement_triggered
                                paymentTransactionModel.unmapped_status =
                                    orderValue.optString("unmapped_status")
                                paymentTransactionModel.appointmentId = tempobj.optInt("id")
                                paymentTransactionModel.order_id = tempobj.optInt("order_id")
                                paymentTransactionModel.productId = productValue.optInt("id")
                                paymentTransactionModel.payment_title =
                                    orderValue.getString("payment_title")
                                paymentTransactionModel.payment_title_color =
                                    orderValue.getString("payment_title_color")
                                paymentTransactionModel.appointmentId = tempobj.getInt("id")
                                paymentTransactionModel.order_id = tempobj.getInt("order_id")
                                paymentTransactionModel.productId = productValue.getInt("id")
                                paymentTransactionModel.netAmountPaid =
                                    orderValue.getInt("net_total_paid")
                                paymentTransactionModel.paymentStatus = status
                                paymentTransactionModel.mode = tempobj.getInt("mode")
                                paymentTransactionModel.orderPaymentStatus =
                                    orderValue.optString("payment_status")
                                paymentTransactionModel.payment_mode =
                                    orderValue.optString("payment_mode")
                                paymentTransactionModel.sendPaymentNotification =
                                    orderValue.optInt("send_payment_notification")
                                paymentTransactionModelList!!.add(paymentTransactionModel)
                            }
                            transactionsListAdapter!!.notifyDataSetChanged()
                        } else {
                            emptyText.visibility = View.VISIBLE
                        }
                    } else {
                        errorHandler(requireContext(), s)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    Log.d("TransactionListExc", "TransactionListExc" + e.message)
                }
            }
        } else {
            aviInTransactionTab!!.visibility = View.GONE
            globalClass!!.noInternetConnection.showDialog(activity)
        }
    }

/*
    fun getServicesForAppointmentData(activity: Activity?, appointmentID: Int) {
        appointmentDetailsViewModel!!.getServicesForAppointmentData(activity, appointmentID)
            .observe(this) { s ->
                Log.i("capture notes res", s)
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
                        val appointmentOrderObject =
                            appointmentDataObject.getJSONObject("order")
                        appointmentOrderAmount = appointmentOrderObject.getInt("order_amt")
                        appointmentOrderAmountDiscount =
                            appointmentOrderObject.getInt("discount")
                        appointmentNetAmount = appointmentOrderObject.getInt("net_amount")
                        changeArray!!.put(appointmentOrderObject.getInt("order_amt"))
                        val invoiceServiceArray = resObject.getJSONArray("invoice_services")
                        val totalProcedureAmount = 0
                        if (invoiceServiceArray.length() > 0) {
                            for (i in 0 until invoiceServiceArray.length()) {
                                val appointmentInvoiceServiceArrayObject =
                                    invoiceServiceArray.getJSONObject(i)
                                changeArray!!.put(appointmentInvoiceServiceArrayObject.getInt("pre_tax_amount"))
                                originalServiceArrayData!!.put(
                                    appointmentInvoiceServiceArrayObject.getInt("pre_tax_amount")
                                )
                            }
                        } else {
                        }
                        Log.d("changeArrayData:", "changeArrayData:$changeArray")
                        getInvoiceDetailsData(getActivity(), orderId, changeArray)
                    } else {
                        errorHandler(requireContext(), s)
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                    Log.d("parserError:", "parserError:$e")
                }
            }
    }
*/

/*
    fun getInvoiceDetailsData(activity: Activity?, orderID: Int, changeArray: JSONArray?) {
        appointmentDetailsViewModel!!.getInvoiceData(activity, orderID, changeArray)
            .observe(this) { s ->
                Log.i("invoice details res", s)
                try {
                    val response = JSONObject(s)
                    if (response.getInt("status_code") == 200) {
                        val resObject =
                            response.getJSONObject("response").getJSONObject("response")
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
                        invoiceServiceArrayData = resObject.getJSONArray("invoice_services")
                    } else {
                        errorHandler(requireContext(), s)
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                    Log.d("parserError:", "parserError:$e")
                }
            }
    }
*/

    override fun onDestroy() {
        pageNumber = 0
        super.onDestroy()
        requireActivity().unregisterReceiver(broadcastReceiver)
    }

    private fun registerTransactionUpdateReceiver() {
        broadcastReceiver = object : BroadcastReceiver() {
            @SuppressLint("NotifyDataSetChanged")
            override fun onReceive(context: Context, intent: Intent) {
                if (intent.action == "UPDATE_TRANSACTION") {
                    // here you can fire your action which you want also get data from intent
                    val bundle = intent.extras
                    if (bundle != null) {
                        val order_id = bundle.getInt("orderId")
                        val transactionModelObj = paymentTransactionModelList!!.stream()
                            .filter { e: PaymentTransactionModel? -> e!!.order_id == order_id }
                            .findAny().orElse(null)
                        if (transactionModelObj != null) {
                            if (bundle.containsKey("invoiceURL")) {
                                val invoiceURL = bundle.getString("invoiceURL")
                                transactionModelObj.invoiceUrl = invoiceURL
                            }
                            if (bundle.containsKey("totalAmountPaidForTransAppoint")) {
                                val amountPaid =
                                    bundle.getString("totalAmountPaidForTransAppoint")
                                transactionModelObj.amount_paid = amountPaid
                            }
                            if (bundle.containsKey("receiptURL")) {
                                val receiptURL = bundle.getString("receiptURL")
                                transactionModelObj.receiptURL = receiptURL
                            }
                            if (bundle.containsKey("paymentStatus")) {
                                val paymentStatus = bundle.getString("paymentStatus")
                                if (paymentStatus.equals("success", ignoreCase = true)) {
                                    transactionModelObj.payment_title = "Payment Completed"
                                    transactionModelObj.payment_title_color = "green"
                                    transactionModelObj.orderPaymentStatus = "success"
                                }
                            }
                        }
                        transactionsListAdapter!!.notifyDataSetChanged()
                    }
                }
            }
        }
        val intentFilter = IntentFilter()
        intentFilter.addAction("UPDATE_TRANSACTION")
        requireActivity().registerReceiver(broadcastReceiver, intentFilter)
    }

    companion object {
        var paymentTransactionModelList: MutableList<PaymentTransactionModel>? = null
        var modePaymentAllFilter: String? = ""
        var apptStatusAllFilter: String? = "ALl"
        var pendingPatientAllFilter = 0
        var pendingPatientFilter = 0
        var receivedPatientFilter = 0
        var pendingSettlementFilter = 0
        var partialSettlePendingFilter = 0
        var settlementDoneFilter = 0
        var partialSettlementDoneFilter = 0
        var scheduledRefundFilter = 0
        var pendingRefundFilter = 0
        var refundCompletedFilter = 0
        var searchFilter = ""
        var modeOfConsultFilter = ""
        var paymentStatusFilter = ""
        var pageNumber = 1
        var count = 5
        fun newInstance(
            mModePaymentAllFilter: String?,
            mStatus: String?,
            mPendingSettlementFilter: Int,
            mSettlementDoneFilter: Int,
            mPendingRefundFilter: Int,
            mRefundCompletedFilter: Int,
            mPartialSettlementDoneFilter: Int,
            mPartialSettlePendingFilter: Int,
            mScheduledRefundFilter: Int,
            mPaymentCompletedFilter: Int,
            filterCount: Int
        ): TransactionsFragment {
            val fragment = TransactionsFragment()
            val args = Bundle()
            args.putString("ModePaymentAllFilter", mModePaymentAllFilter)
            args.putString("Status", mStatus)
            args.putInt("PendingSettlementFilter", mPendingSettlementFilter)
            args.putInt("SettlementDoneFilter", mSettlementDoneFilter)
            args.putInt("PendingRefundFilter", mPendingRefundFilter)
            args.putInt("RefundCompletedFilter", mRefundCompletedFilter)
            args.putInt("PartialSettlementDoneFilter", mPartialSettlementDoneFilter)
            args.putInt("PartialSettlePendingFilter", mPartialSettlePendingFilter)
            args.putInt("ScheduledRefundFilter", mScheduledRefundFilter)
            args.putInt("paymentCompletedFilter", mPaymentCompletedFilter)
            args.putInt("filterCount", filterCount)
            fragment.arguments = args
            return fragment
        }
    }
}