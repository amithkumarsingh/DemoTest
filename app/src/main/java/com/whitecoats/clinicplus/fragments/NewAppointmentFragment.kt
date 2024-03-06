package com.whitecoats.clinicplus.fragments

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.app.ProgressDialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.PorterDuff
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.telephony.PhoneStateListener
import android.telephony.TelephonyManager
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.view.*
import android.view.View.OnTouchListener
import android.widget.*
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts.RequestPermission
import androidx.core.content.ContextCompat
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout.OnRefreshListener
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.kizitonwose.calendarview.CalendarView
import com.kizitonwose.calendarview.model.CalendarMonth
import com.kizitonwose.calendarview.model.DayOwner
import com.kizitonwose.calendarview.model.InDateStyle
import com.kizitonwose.calendarview.model.OutDateStyle
import com.kizitonwose.calendarview.ui.DayBinder
import com.kizitonwose.calendarview.ui.ViewContainer
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.whitecoats.clinicplus.*
import com.whitecoats.clinicplus.activities.*
import com.whitecoats.clinicplus.adapters.AppointmentCalendarListAdapter
import com.whitecoats.clinicplus.adapters.AppointmentListAdapter
import com.whitecoats.clinicplus.adapters.PatientSearchAdapter
import com.whitecoats.clinicplus.apis.ApiUrls
import com.whitecoats.clinicplus.models.AppointDates
import com.whitecoats.clinicplus.models.AppointmentModel
import com.whitecoats.clinicplus.models.AppointmentServicesModel
import com.whitecoats.clinicplus.utils.AppUtilities
import com.whitecoats.clinicplus.utils.ErrorHandlerClass.errorHandler
import com.whitecoats.clinicplus.utils.SharedPref
import com.whitecoats.clinicplus.utils.ShowAlertDialog
import com.whitecoats.clinicplus.utils.UtilsResource
import com.whitecoats.clinicplus.viewmodels.AppointmentViewModel
import com.whitecoats.clinicplus.viewmodels.DashboardViewModel
import com.whitecoats.clinicplus.viewmodels.SearchPatientViewModel
import com.whitecoats.clinicplus.viewmodels.SettingsViewModel
import com.whitecoats.model.PatientPListModel
import com.zoho.salesiqembed.ZohoSalesIQ
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.ZoneId
import java.util.*

class NewAppointmentFragment : Fragment(), GestureDetector.OnGestureListener, OnTouchListener {
    private var appointmentServiceView: TextView? = null
    private var apptCalendarNoRecordText: TextView? = null
    private var apptClearFilter: TextView? = null
    private var apptCalendarBookAppt: TextView? = null
    private var apptNoPatientText: TextView? = null
    private var apptNoPatientBookAppointment: TextView? = null
    private var noApptText: TextView? = null
    private var filterText: TextView? = null
    private var addAppoinment: FloatingActionButton? = null
    private var appointmentHeaderFilter: ImageView? = null
    private var calendarView: CalendarView? = null
    private lateinit var gestureDetector: GestureDetector
    private var apptCalendarNestedScroll: NestedScrollView? = null
    private var apptHeaderListView: ImageView? = null
    private var apptCalendarSearchClose: ImageView? = null
    private var apptFilter: ImageView? = null
    private var apptCalendar: ImageView? = null
    private var apptCalendarSearchPatient: AutoCompleteTextView? = null
    private var apptSearchPatient: AutoCompleteTextView? = null
    private var apptCalendarRecycler: RecyclerView? = null
    private var apptRecycler: RecyclerView? = null
    private var serviceLayout: LinearLayout? = null
    private var apptCalendarNoRecord: LinearLayout? = null
    private var apptCalendarLayout: LinearLayout? = null
    private var apptNoPatient: LinearLayout? = null
    private var noPatientLayout: LinearLayout? = null
    private var bookAppointmentButton: LinearLayout? = null
    private var openCalendarView: LinearLayout? = null
    private var apptHeaderSearch: LinearLayout? = null
    private var apptCalendarSearchlayout: LinearLayout? = null
    private var apptSearchLayout: LinearLayout? = null
    private var apptOpenTab: RelativeLayout? = null
    private var apptClosedTab: RelativeLayout? = null
    private var apptListView: RelativeLayout? = null
    private var appointmentFilterBottomSheeet: AppointmentFilterBottomSheeet? = null
    private lateinit var appointmentViewModel: AppointmentViewModel
    var durationFilter = "All"
    var modeFilter = "All"
    var consultFilter = "0"
    var selectedPatient = ""
    var checkInStatus = ""
    var payment = "All"
    var productId = ""
    var sort = "asc"
    var sortBy = "scheduled_start_time"
    var perPageString = "20"
    var pageNumber = 1
    private var progressBar: ProgressBar? = null
    private var apptOpenTabView: View? = null
    private var apptClosedTabView: View? = null
    private var openText: TextView? = null
    private var closedText: TextView? = null
    private lateinit var groupData: ArrayList<Int>
    private lateinit var apptSize: ArrayList<Int>
    var c = 0
    lateinit var appointmentModelList: MutableList<AppointmentModel>
    lateinit var appointmentListAdapter: AppointmentListAdapter
    lateinit var appointmentCalendarListAdapter: AppointmentCalendarListAdapter
    private lateinit var apptIdArr: JSONArray
    private var otpLoading: ProgressDialog? = null
    private var loadingDialog: ProgressDialog? = null
    private lateinit var searchPatientViewModel: SearchPatientViewModel
    private lateinit var globalClass: MyClinicGlobalClass
    private lateinit var patientPListModelList: MutableList<PatientPListModel>
    private lateinit var patientSearchAdapter: PatientSearchAdapter
    private var builder: AlertDialog.Builder? = null
    lateinit var data: Array<String>
    lateinit var check: BooleanArray
    private lateinit var serviceData: ArrayList<String>
    private lateinit var appointmentServicesModelArrayList: ArrayList<AppointmentServicesModel>
    private var dialog: AlertDialog? = null
    private lateinit var servicenames: String
    private var currentLayout = "main"
    private lateinit var date: Date
    private lateinit var calendar: Calendar
    private lateinit var calendarDay: CalendarDay
    private var monthText: TextView? = null
    private lateinit var today: LocalDate
    private lateinit var selectedDated: LocalDate
    private var dateChangedInCal: LocalDate? = null
    var mode = "month"
    private lateinit var apptDates: ArrayList<LocalDate>
    private var callCurrentState = 0
    private lateinit var dashboardViewModel: DashboardViewModel
    private var bookApptValue = 1
    private var rightArrow: ImageView? = null
    private var leftArrow: ImageView? = null
    private var searchImage: ImageView? = null
    private var searchFilterText: CharSequence = ""
    var radioGroup: RadioGroup? = null
    var radioGroup_onlineStatus: RadioGroup? = null
    var rbYesNo: RadioButton? = null
    var et_refund_amount: EditText? = null
    var refundMainLayout: RelativeLayout? = null
    var refundMessageLayout: RelativeLayout? = null
    var refundFullLayout: RelativeLayout? = null
    var messageNoteText: TextView? = null
    private lateinit var appointmentApiRequests: AppointmentApiRequests
    private var appointmentID = 0
    private var apptStatusValue = 0
    private lateinit var currentDate: String
    private lateinit var selectedDate: String
    private var amount_paid: TextView? = null
    private var yesButton: RelativeLayout? = null
    private var noButton: RelativeLayout? = null
    private var closeButton: RelativeLayout? = null
    private var refundTextOne: TextView? = null
    private var refundTextTwo: TextView? = null
    private var refundTextThree: TextView? = null
    lateinit var activity: Activity
    var dialogCancel: Dialog? = null
    private var layoutManager: LinearLayoutManager? = null
    private var loading = false
    private var isListExhausted = false
    private lateinit var tabName: String
    private var from = ""
    private var swipeContainer: SwipeRefreshLayout? = null
    lateinit var broadcastReceiver: BroadcastReceiver
    lateinit var intentFilter: IntentFilter
    private var clearImage: ImageView? = null
    private var appointBookFromCal = false
    private var makeSearchViewEmpty = true
    var appointBookRescheduleFromCalDate: LocalDate? = null
    lateinit var onTouchListener: OnTouchListener
    private lateinit var settingsViewModel: SettingsViewModel
    private lateinit var telephonyManager: TelephonyManager
    private lateinit var callStateListener: PhoneStateListener
    private var requestPermissionLauncher: ActivityResultLauncher<String>? = null
    private lateinit var sharedPref: SharedPref

    companion object {
        var activePastFilter = "active"
        var isMoreData = false
        var dummyDate: String = "Dec 12, 2001"
        fun newInstance(): NewAppointmentFragment {
            return NewAppointmentFragment()
        }
    }

    override fun onResume() {
        super.onResume()
        Log.d("CheckingLifeCycle", "New Appoint On onResume")
        activity = requireActivity()
    }

    override fun onPause() {
        super.onPause()
        Log.d("CheckingLifeCycle", "New Appoint On onPause")

        if (dialogCancel != null && dialogCancel!!.isShowing) {
            dialogCancel!!.dismiss()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("CheckingLifeCycle", "New Appoint On onCreate")

    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        Log.d("CheckingLifeCycle", "New Appoint On onAttach")

    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("CheckingLifeCycle", "New Appoint On onDestroy")

        requireActivity().unregisterReceiver(broadcastReceiver)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.d("CheckingLifeCycle", "New Appoint On onDestroyView")

    }

    override fun onStop() {
        super.onStop()
        Log.d("CheckingLifeCycle", "New Appoint On onStop")

    }

    override fun onDetach() {
        super.onDetach()
        Log.d("CheckingLifeCycle", "New Appoint On onDetach")

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_new_appointment, container, false)
        //Sentry.captureMessage("testing SDK setup");
        appointmentServiceView = view.findViewById(R.id.appt_services_view)
        addAppoinment = view.findViewById(R.id.add_appointment)
        appointmentHeaderFilter = view.findViewById(R.id.appt_header_filter)
        calendarView = view.findViewById(R.id.appointmentCalendarView)
        apptCalendarNestedScroll = view.findViewById(R.id.appt_calendar_view_nestedscroll)
        apptHeaderSearch = view.findViewById(R.id.appt_header_search)
        searchImage = view.findViewById(R.id.searchImage)
        clearImage = view.findViewById(R.id.clearImage)
        apptHeaderListView = view.findViewById(R.id.appt_header_listview)
        apptCalendarSearchPatient = view.findViewById(R.id.appt_calendar_search_partient)
        apptCalendarSearchClose = view.findViewById(R.id.appt_calendar_search_partient_close)
        apptCalendarRecycler = view.findViewById(R.id.appt_calendar_list)
        apptCalendarNoRecord = view.findViewById(R.id.appt_calendar_no_record)
        apptCalendarNoRecordText = view.findViewById(R.id.appt_calendar_no_record_text)
        apptClearFilter = view.findViewById(R.id.appt_clear_filter)
        apptCalendarBookAppt = view.findViewById(R.id.appt_calendar_book_appt)
        apptCalendarLayout = view.findViewById(R.id.appt_calendar_view)
        apptOpenTab = view.findViewById(R.id.appointOpenTab)
        apptClosedTab = view.findViewById(R.id.appointClosedTab)
        apptSearchPatient = view.findViewById(R.id.appt_search_partient)
        apptFilter = view.findViewById(R.id.appt_filter)
        apptCalendar = view.findViewById(R.id.appt_calendar)
        apptRecycler = view.findViewById(R.id.appt_list)
        apptNoPatient = view.findViewById(R.id.appt_no_patient)
        apptNoPatientText = view.findViewById(R.id.appt_no_patient_text)
        apptNoPatientBookAppointment = view.findViewById(R.id.appt_no_patient_book_appt)
        noPatientLayout = view.findViewById(R.id.no_appointment_layout)
        noApptText = view.findViewById(R.id.no_appointment_text)
        bookAppointmentButton = view.findViewById(R.id.book_appointment_button)
        openCalendarView = view.findViewById(R.id.open_calendar_view)
        apptListView = view.findViewById(R.id.appt_list_view)
        apptCalendarSearchlayout = view.findViewById(R.id.appt_calendar_search_layout)
        filterText = view.findViewById(R.id.filter_text)
        apptSearchLayout = view.findViewById(R.id.appt_search_header)
        progressBar = view.findViewById(R.id.appt_progress)
        apptOpenTabView = view.findViewById(R.id.appointTabOpenBottom)
        apptClosedTabView = view.findViewById(R.id.appointTabClosedBottom)
        openText = view.findViewById(R.id.apptOpenText)
        closedText = view.findViewById(R.id.apptClosedText)
        monthText = view.findViewById(R.id.month_text)
        rightArrow = view.findViewById(R.id.appt_right_arrow)
        leftArrow = view.findViewById(R.id.appt_back_arrow)
        serviceLayout = view.findViewById(R.id.service_layout)
        swipeContainer = view.findViewById(R.id.appointmentSwipeContainer)
        apptDates = ArrayList()
        appointmentApiRequests = AppointmentApiRequests()
        activePastFilter = "active"
        today = LocalDate.now()
        selectedDated = LocalDate.now()
        calendarView!!.setup(
            YearMonth.now().minusMonths(10),
            YearMonth.now().plusMonths(10),
            DayOfWeek.SUNDAY
        )
        calendarView!!.scrollToMonth(YearMonth.now())
        mode = "week"
        calendarView!!.updateMonthConfiguration(
            InDateStyle.ALL_MONTHS, OutDateStyle.END_OF_ROW,
            1, true
        )
        calendarView!!.scrollToDate(today!!)
        progressBar!!.visibility = View.GONE
        sharedPref = SharedPref(requireActivity())
        globalClass = requireActivity().applicationContext as MyClinicGlobalClass
        patientPListModelList = ArrayList()
        groupData = ArrayList()
        apptSize = ArrayList()
        serviceData = ArrayList()
        Log.d("getActivityCall", "getActivityCall")
        appointmentModelList = ArrayList()
        appointmentServicesModelArrayList = ArrayList()
        calendar = Calendar.getInstance()
        date = Calendar.getInstance().time
        calendarDay = CalendarDay.from(
            calendar!!.get(Calendar.YEAR),
            calendar!!.get(Calendar.MONTH) + 1,
            calendar!!.get(Calendar.DAY_OF_MONTH)
        )
        builder = AlertDialog.Builder(requireActivity())
        registerReceiverAppointment()
        ApiUrls.bottomNaviType = 2
        MyClinicGlobalClass.logUserActionEvent(
            ApiUrls.doctorId,
            getString(R.string.AppointmentsListScreenImpressions),
            null
        )
        patientSearchAdapter =
            PatientSearchAdapter(
                requireActivity(),
                R.layout.patient_search_item,
                patientPListModelList!!
            )
        apptSearchPatient!!.setAdapter(patientSearchAdapter)
        apptCalendarSearchPatient!!.setAdapter(patientSearchAdapter)
        appointmentViewModel = ViewModelProvider(this).get(
            AppointmentViewModel::class.java
        )
        searchPatientViewModel = ViewModelProvider(this).get(
            SearchPatientViewModel::class.java
        )
        dashboardViewModel = ViewModelProvider(this).get(DashboardViewModel::class.java)
        appointmentViewModel!!.init()
        searchPatientViewModel!!.init()
        dashboardViewModel!!.init()
        settingsViewModel = ViewModelProvider(this).get(SettingsViewModel::class.java)
        appointmentFilterBottomSheeet = AppointmentFilterBottomSheeet()
        requestPermissionLauncher = registerForActivityResult(
            RequestPermission()
        ) { result: Boolean ->
            if (result) {
                // PERMISSION GRANTED
                telephonyManager!!.listen(callStateListener, PhoneStateListener.LISTEN_CALL_STATE)
            } else {
                ShowAlertDialog().showPopupToMovePermissionPage(requireActivity())
            }
        }
        apptSearchPatient!!.setOnItemClickListener { parent, _, position, _ ->
            AppUtilities().hideSoftKeyboard(activity)
            pageNumber = 1
            val patientPList = parent.getItemAtPosition(position) as PatientPListModel
            searchFilterText = patientPList.getPatientName()
            selectedPatient = patientPList.getPatientName()
            if (!appointmentModelList.isEmpty()) {
                appointmentModelList.clear()
                dummyDate = "Dec 12, 2001"
                appointmentListAdapter!!.notifyDataSetChanged()
            }
            Log.i("search test", "onclick")
            from = "search"
            durationFilter = "All"
            getAppointmentDetails(
                durationFilter,
                modeFilter,
                searchFilterText.toString(),
                sort,
                sortBy,
                pageNumber.toString() + "",
                perPageString,
                activePastFilter,
                consultFilter,
                productId,
                checkInStatus,
                payment,
                "search",
                selectedDated.toString()
            )
            MyClinicGlobalClass.logUserActionEvent(
                ApiUrls.doctorId,
                getString(R.string.AppointmentsListSearchPatient),
                null
            )
        }
        apptCalendarSearchPatient!!.onItemClickListener =
            AdapterView.OnItemClickListener { parent, view, position, id ->
                AppUtilities().hideSoftKeyboard(activity)
                pageNumber = 1
                val patientPList = parent.getItemAtPosition(position) as PatientPListModel
                searchFilterText = patientPList.getPatientName()
                selectedPatient = patientPList.getPatientName()
                durationFilter = "Specific"
                c = 0
                groupData.clear()
                if (!appointmentModelList.isEmpty()) {
                    appointmentModelList.clear()
                    dummyDate = "Dec 12, 2001"
                    appointmentListAdapter.notifyDataSetChanged()
                }
                from = "search"
                allAppointsDates
                getAppointmentDetails(
                    durationFilter,
                    modeFilter,
                    searchFilterText.toString(),
                    sort,
                    sortBy,
                    pageNumber.toString() + "",
                    perPageString,
                    activePastFilter,
                    consultFilter,
                    productId,
                    checkInStatus,
                    payment,
                    "search",
                    selectedDated.toString()
                )
            }
        apptCalendarSearchPatient!!.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(patientName: CharSequence, i: Int, i1: Int, i2: Int) {
                searchFilterText = patientName
                if (patientName.length > 2) {
                    if (globalClass.isOnline) {
                        searchPatientViewModel.searchPatient(
                            requireActivity(),
                            patientName.toString()
                        ).observe(
                            viewLifecycleOwner, object : Observer<String> {
                                override fun onChanged(s: String) {
                                    try {
                                        val jsonObject = JSONObject(s)
                                        if (jsonObject.getInt("status_code") == 200) {
                                            val patientDetails =
                                                jsonObject.getJSONObject("response")
                                                    .getJSONArray("response")
                                            if (patientDetails.length() == 0) {
                                                apptCalendarNoRecord!!.visibility = View.VISIBLE
                                                bookApptValue = 2
                                                apptCalendarNoRecordText!!.text =
                                                    "No appointment found for the patient '$patientName'"
                                                apptCalendarRecycler!!.visibility = View.GONE
                                                apptDates.clear()
                                                calendarView!!.notifyCalendarChanged()
                                            } else {
                                                patientPListModelList.clear()
                                                for (i in 0 until patientDetails.length()) {
                                                    val patientDetail =
                                                        patientDetails.getJSONObject(i)
                                                    val assignCategory =
                                                        patientDetail.getJSONArray("assignedCategories")
                                                    val patientPListModel = PatientPListModel()
                                                    patientPListModel.setPatientName(
                                                        patientDetail.getString(
                                                            "fullname"
                                                        )
                                                    )
                                                            /*New Registration(Autogenerated ID) changes for Gastro interface*/
                                                    patientPListModel.setGeneralID(
                                                        patientDetail.getString(
                                                            "general_id"
                                                        )
                                                    )
                                                    patientPListModel.patientId =
                                                        patientDetail.getInt("id")
                                                    patientPListModel.setEmailid(
                                                        patientDetail.getString(
                                                            "email"
                                                        )
                                                    )
                                                    patientPListModel.setPhNo(
                                                        patientDetail.optString(
                                                            "phone"
                                                        )
                                                    )
                                                    patientPListModel.patientAge =
                                                        patientDetail.getString("age")
                                                    patientPListModel.patientGender =
                                                        patientDetail.getInt("gender")
                                                    patientPListModel.assignCategory =
                                                        assignCategory
                                                    patientPListModelList.add(patientPListModel)
                                                }
                                                patientSearchAdapter.notifyDataSetChanged()
                                            }
                                        } else {
                                            errorHandler(requireContext(), s)
                                        }
                                    } catch (e: JSONException) {
                                        e.printStackTrace()
                                    }
                                }
                            })
                    } else {
                        globalClass.noInternetConnection.showDialog(requireActivity())
                    }
                }
            }

            override fun afterTextChanged(s: Editable) {
                if (makeSearchViewEmpty) {
                    if (s.toString().length == 0) {
                        apptCalendarNoRecord!!.visibility = View.GONE
                        apptCalendarRecycler!!.visibility = View.VISIBLE
                        searchFilterText = ""
                        pageNumber = 1
                        c = 0
                        groupData.clear()
                        durationFilter = "Specific"
                        if (!appointmentModelList.isEmpty()) {
                            appointmentModelList.clear()
                            dummyDate = "Dec 12, 2001"
                            appointmentListAdapter.notifyDataSetChanged()
                        }
                        from = "search"
                        allAppointsDates
                        getAppointmentDetails(
                            durationFilter,
                            modeFilter,
                            searchFilterText.toString(),
                            sort,
                            sortBy,
                            pageNumber.toString() + "",
                            perPageString,
                            activePastFilter,
                            consultFilter,
                            productId,
                            checkInStatus,
                            payment,
                            "search",
                            selectedDated.toString()
                        )
                    }
                }
            }
        })
        apptSearchPatient!!.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(patientName: CharSequence, i: Int, i1: Int, i2: Int) {
                searchFilterText = patientName
                if (patientName.length > 2) {
                    if (globalClass.isOnline) {
                        searchPatientViewModel.searchPatient(
                            requireActivity(),
                            patientName.toString()
                        ).observe(
                            viewLifecycleOwner, object : Observer<String> {
                                override fun onChanged(s: String) {
                                    try {
                                        val jsonObject = JSONObject(s)
                                        if (jsonObject.getInt("status_code") == 200) {
                                            val patientDetails =
                                                jsonObject.getJSONObject("response")
                                                    .getJSONArray("response")
                                            if (patientDetails.length() == 0) {
                                                apptNoPatient!!.visibility = View.VISIBLE
                                                bookAppointmentButton!!.visibility = View.GONE
                                                openCalendarView!!.visibility = View.GONE
                                                noPatientLayout!!.visibility = View.GONE
                                                bookApptValue = 2
                                                apptNoPatientText!!.text =
                                                    "No appointment found for the patient '$patientName'"
                                                apptRecycler!!.visibility = View.GONE
                                            } else {
                                                patientPListModelList.clear()
                                                for (i in 0 until patientDetails.length()) {
                                                    val patientDetail =
                                                        patientDetails.getJSONObject(i)
                                                    val assignCategory =
                                                        patientDetail.getJSONArray("assignedCategories")
                                                    val patientPListModel = PatientPListModel()
                                                    patientPListModel.setPatientName(
                                                        patientDetail.getString(
                                                            "fullname"
                                                        )
                                                    )
                                                    /*New Registration(Autogenerated ID) changes for Gastro interface*/
                                                    patientPListModel.setGeneralID(
                                                        patientDetail.getString(
                                                            "general_id"
                                                        )
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
                                                    patientPListModelList.add(patientPListModel)
                                                }
                                                patientSearchAdapter.notifyDataSetChanged()
                                            }
                                        } else {
                                            errorHandler(requireContext(), s)
                                        }
                                    } catch (e: JSONException) {
                                        e.printStackTrace()
                                    }
                                }
                            })
                    } else {
                        globalClass.noInternetConnection.showDialog(requireActivity())
                    }
                }
            }

            override fun afterTextChanged(s: Editable) {
                if (makeSearchViewEmpty) {
                    if (s.toString().length == 0) {
                        clearImage!!.visibility = View.GONE
                        searchImage!!.visibility = View.VISIBLE
                        apptNoPatient!!.visibility = View.GONE
                        apptRecycler!!.visibility = View.VISIBLE
                        searchFilterText = ""
                        pageNumber = 1
                        c = 0
                        durationFilter = "All"
                        groupData.clear()
                        if (!appointmentModelList.isEmpty()) {
                            appointmentModelList.clear()
                            dummyDate = "Dec 12, 2001"
                            appointmentListAdapter.notifyDataSetChanged()
                        }
                        from = "search"
                        getAppointmentDetails(
                            durationFilter,
                            modeFilter,
                            searchFilterText.toString(),
                            sort,
                            sortBy,
                            pageNumber.toString() + "",
                            perPageString,
                            activePastFilter,
                            consultFilter,
                            productId,
                            checkInStatus,
                            payment,
                            "search",
                            selectedDated.toString()
                        )
                    } else {
                        clearImage!!.visibility = View.VISIBLE
                        searchImage!!.visibility = View.GONE
                    }
                }
            }
        })
        bookAppointmentButton!!.setOnClickListener {
            registerReceiverAppointment()
            val intent = Intent(requireContext(), BookAppointmentActivity::class.java)
            intent.putExtra("bookAppointment", bookApptValue)
            intent.putExtra("patient name", selectedPatient)
            requireContext().startActivity(intent)
            MyClinicGlobalClass.logUserActionEvent(
                ApiUrls.doctorId,
                getString(R.string.AppointmentsNewAppointment),
                null
            )
        }
        openCalendarView!!.setOnClickListener {
            selectedDated = LocalDate.now()
            dateChangedInCal = null
            calendarView!!.setOnTouchListener(onTouchListener)
            mode = "week"
            calendarView!!.updateMonthConfiguration(
                InDateStyle.ALL_MONTHS, OutDateStyle.END_OF_ROW,
                1, true
            )
            calendarView!!.scrollToDate(today)
            isListExhausted = false
            noPatientLayout!!.visibility = View.GONE
            apptSearchLayout!!.visibility = View.VISIBLE
            apptRecycler!!.visibility = View.GONE
            addAppoinment!!.visibility = View.GONE
            apptListView!!.visibility = View.GONE
            apptCalendarLayout!!.visibility = View.VISIBLE
            apptHeaderSearch!!.visibility = View.VISIBLE
            appointmentHeaderFilter!!.visibility = View.VISIBLE
            apptHeaderListView!!.visibility = View.VISIBLE
            apptCalendarNoRecord!!.visibility = View.GONE
            apptCalendarRecycler!!.visibility = View.GONE
            currentLayout = "calendar"
            c = 0
            groupData.clear()
            pageNumber = 1
            durationFilter = "Specific"
            // modeFilter = "All";
            // consultFilter = "0";
            if (ApiUrls.activePastFilterFlag.equals("active", ignoreCase = true)) {
                activePastFilter = "active"
            } else if (ApiUrls.activePastFilterFlag.equals("past", ignoreCase = true)) {
                activePastFilter = "past"
            } else {
                activePastFilter = "active"
            }
            searchFilterText = ""
            //  checkInStatus = "";
            // payment = "All";
            allAppointsDates
            if (!appointmentModelList.isEmpty()) {
                appointmentModelList.clear()
                dummyDate = "Dec 12, 2001"
                appointmentListAdapter.notifyDataSetChanged()
            }
            if (!TextUtils.isEmpty(apptSearchPatient!!.text.toString().trim { it <= ' ' })) {
                makeSearchViewEmpty = false
                apptSearchPatient!!.setText("")
                selectedPatient = ""
                makeSearchViewEmpty = true
            }
            from = "calendar"
            getAppointmentDetails(
                durationFilter,
                modeFilter,
                searchFilterText.toString(),
                sort,
                sortBy,
                pageNumber.toString() + "",
                perPageString,
                activePastFilter,
                consultFilter,
                productId,
                checkInStatus,
                payment,
                "calendar",
                LocalDate.now().toString()
            )
            MyClinicGlobalClass.logUserActionEvent(
                ApiUrls.doctorId,
                getString(R.string.AppointmentsOpenTabCalenderView),
                null
            )
        }
        apptOpenTab!!.setOnClickListener {
            MyClinicGlobalClass.logUserActionEvent(
                ApiUrls.doctorId,
                getString(R.string.AppointmentsOpenTabImpressions),
                null
            )
            if (progressBar!!.visibility == View.GONE) {
                isListExhausted = false
                activePastFilter = "active"
                sort = "asc"
                apptOpenTabView!!.visibility = View.VISIBLE
                apptClosedTabView!!.visibility = View.GONE
                openText!!.setTextColor(
                    ContextCompat.getColor(
                        requireActivity(),
                        R.color.colorPrimary
                    )
                )
                closedText!!.setTextColor(
                    ContextCompat.getColor(
                        requireActivity(),
                        R.color.colorGreyText
                    )
                )
                noPatientLayout!!.visibility = View.GONE
                apptSearchLayout!!.visibility = View.VISIBLE
                apptRecycler!!.visibility = View.GONE
                addAppoinment!!.visibility = View.GONE
                c = 0
                groupData.clear()
                apptDates.clear()
                activePastFilter = "active"
                ApiUrls.activePastFilterFlag = activePastFilter
                pageNumber = 1
                appointmentModelList.clear()
                dummyDate = "Dec 12, 2001"
                appointmentListAdapter.notifyDataSetChanged()
                from = if (!searchFilterText.toString().equals("", ignoreCase = true)) {
                    "search"
                } else {
                    "main"
                }
                durationFilter = "All"
                getAppointmentDetails(
                    durationFilter,
                    modeFilter,
                    searchFilterText.toString(),
                    sort,
                    sortBy,
                    pageNumber.toString() + "",
                    perPageString,
                    activePastFilter,
                    consultFilter,
                    productId,
                    checkInStatus,
                    payment,
                    from,
                    selectedDated.toString()
                )
            }
        }
        apptClosedTab!!.setOnClickListener(View.OnClickListener {
            MyClinicGlobalClass.logUserActionEvent(
                ApiUrls.doctorId,
                getString(R.string.AppointmentsClosedTabImpressions),
                null
            )
            if (progressBar!!.visibility == View.GONE) {
                isListExhausted = false
                apptOpenTabView!!.visibility = View.GONE
                apptClosedTabView!!.visibility = View.VISIBLE
                activePastFilter = "past"
                sort = "desc"
                openText!!.setTextColor(
                    ContextCompat.getColor(
                        requireActivity(),
                        R.color.colorGreyText
                    )
                )
                closedText!!.setTextColor(
                    ContextCompat.getColor(
                        requireActivity(),
                        R.color.colorPrimary
                    )
                )
                noPatientLayout!!.visibility = View.GONE
                apptSearchLayout!!.visibility = View.VISIBLE
                apptRecycler!!.visibility = View.GONE
                addAppoinment!!.visibility = View.GONE
                c = 0
                groupData.clear()
                apptDates.clear()
                activePastFilter = "past"
                ApiUrls.activePastFilterFlag = activePastFilter
                pageNumber = 1
                appointmentModelList.clear()
                dummyDate = "Dec 12, 2001"
                appointmentListAdapter.notifyDataSetChanged()
                from = if (!searchFilterText.toString().equals("", ignoreCase = true)) {
                    "search"
                } else {
                    "main"
                }
                durationFilter = "All"
                getAppointmentDetails(
                    durationFilter,
                    modeFilter,
                    searchFilterText.toString(),
                    sort,
                    sortBy,
                    pageNumber.toString() + "",
                    perPageString,
                    activePastFilter,
                    consultFilter,
                    productId,
                    checkInStatus,
                    payment,
                    from,
                    selectedDated.toString()
                )
            }
        })
        swipeContainer!!.setOnRefreshListener(OnRefreshListener { // Your code to refresh the list here.
            // Make sure you call swipeContainer.setRefreshing(false)
            // once the network request has completed successfully.
            c = 0
            appointmentFilterBottomSheeet!!.resetFilter()
            services
            groupData.clear()
            if (ApiUrls.activePastFilterFlag.equals("active", ignoreCase = true)) {
                activePastFilter = "active"
            } else if (ApiUrls.activePastFilterFlag.equals("past", ignoreCase = true)) {
                activePastFilter = "past"
            } else {
                activePastFilter = "active"
            }
            pageNumber = 1
            appointmentModelList.clear()
            dummyDate = "Dec 12, 2001"
            appointmentListAdapter.notifyDataSetChanged()
            durationFilter = "All"
            modeFilter = "All"
            consultFilter = "0"
            checkInStatus = ""
            payment = "All"
            from = if (!searchFilterText.toString().equals("", ignoreCase = true)) {
                "search"
            } else {
                "main"
            }
            getAppointmentDetails(
                durationFilter,
                modeFilter,
                searchFilterText.toString(),
                sort,
                sortBy,
                pageNumber.toString() + "",
                perPageString,
                activePastFilter,
                consultFilter,
                "",
                checkInStatus,
                payment,
                from,
                selectedDated.toString()
            )
        })
        services
        durationFilter = "All"
        if (!appointmentModelList.isEmpty()) {
            appointmentModelList.clear()
            dummyDate = "Dec 12, 2001"
            appointmentListAdapter.notifyDataSetChanged()
        }
        from = "main"
        getAppointmentDetails(
            durationFilter,
            modeFilter,
            searchFilterText.toString(),
            sort,
            sortBy,
            pageNumber.toString() + "",
            perPageString,
            activePastFilter,
            consultFilter,
            productId,
            checkInStatus,
            payment,
            "main",
            selectedDated.toString()
        )
        apptCalendarSearchClose!!.setOnClickListener {
            selectedPatient = ""
            apptCalendarSearchPatient!!.setText("")
            apptCalendarSearchlayout!!.visibility = View.GONE
        }
        apptHeaderSearch!!.setOnClickListener {
            apptCalendarSearchlayout!!.visibility = View.VISIBLE
        }
        clearImage!!.setOnClickListener { v: View? ->
            if (searchFilterText != null) {
                selectedPatient = ""
                searchFilterText = ""
                apptSearchPatient!!.setText("")
                pageNumber = 1
            }
        }
        searchImage!!.setOnClickListener {
            MyClinicGlobalClass.logUserActionEvent(
                ApiUrls.doctorId,
                getString(
                    R.string.AppointmentsSearchPatientBookAppointment
                ),
                null
            )
            if (searchFilterText != null) {
                if (searchFilterText.isEmpty()) {
                    searchFilterText = ""
                    pageNumber = 1
                    if (!appointmentModelList.isEmpty()) {
                        appointmentModelList.clear()
                        dummyDate = "Dec 12, 2001"
                        appointmentListAdapter.notifyDataSetChanged()
                    }
                    from = "search"
                    durationFilter = "All"
                    getAppointmentDetails(
                        durationFilter,
                        modeFilter,
                        searchFilterText.toString(),
                        sort,
                        sortBy,
                        pageNumber.toString() + "",
                        perPageString,
                        activePastFilter,
                        consultFilter,
                        productId,
                        checkInStatus,
                        payment,
                        "search",
                        selectedDated.toString()
                    )
                }
                if (searchFilterText.length > 2) {
                    if (globalClass.isOnline) {
                        searchPatientViewModel.searchPatient(
                            requireActivity(),
                            searchFilterText.toString()
                        ).observe(
                            viewLifecycleOwner, object : Observer<String> {
                                override fun onChanged(s: String) {
                                    try {
                                        val jsonObject = JSONObject(s)
                                        if (jsonObject.getInt("status_code") == 200) {
                                            val patientDetails =
                                                jsonObject.getJSONObject("response")
                                                    .getJSONArray("response")
                                            if (patientDetails.length() == 0) {
                                                apptNoPatient!!.visibility = View.VISIBLE
                                                bookAppointmentButton!!.visibility = View.GONE
                                                openCalendarView!!.visibility = View.GONE
                                                noPatientLayout!!.visibility = View.GONE
                                                bookApptValue = 2
                                                apptNoPatientText!!.text =
                                                    "No appointment found for the patient '" + searchFilterText.toString() + "'"
                                                apptRecycler!!.visibility = View.GONE
                                            } else {
                                                patientPListModelList.clear()
                                                for (i in 0 until patientDetails.length()) {
                                                    val patientDetail =
                                                        patientDetails.getJSONObject(i)
                                                    val assignCategory =
                                                        patientDetail.getJSONArray("assignedCategories")
                                                    val patientPListModel = PatientPListModel()
                                                    patientPListModel.setPatientName(
                                                        patientDetail.getString(
                                                            "fullname"
                                                        )
                                                    )
                                                    /*New Registration(Autogenerated ID) changes for Gastro interface*/
                                                    patientPListModel.setGeneralID(
                                                        patientDetail.getString(
                                                            "general_id"
                                                        )
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
                                                    patientPListModelList.add(patientPListModel)
                                                }
                                                patientSearchAdapter.notifyDataSetChanged()
                                            }
                                        } else {
                                            errorHandler(requireContext(), s)
                                        }
                                    } catch (e: JSONException) {
                                        e.printStackTrace()
                                    }
                                }
                            })
                    } else {
                        globalClass.noInternetConnection.showDialog(requireActivity())
                    }
                }
            }
        }
        appointmentFilterBottomSheeet!!.setupBottomSheet(
            durationFilter,
            modeFilter,
            consultFilter,
            activePastFilter,
            checkInStatus,
            payment
        ) { consultMode, consultType, attendence, paymentVal, count ->
            pageNumber = 1
            apptRecycler!!.visibility = View.GONE
            durationFilter = if (currentLayout.contains("calendar")) {
                "Specific"
            } else {
                "All"
            }
            if (count > 0) {
                filterText!!.text = "$count Filters applied"
                filterText!!.visibility = View.VISIBLE
            } else {
                filterText!!.text = ""
                filterText!!.visibility = View.GONE
            }
            if (!appointmentModelList.isEmpty()) {
                appointmentModelList.clear()
                dummyDate = "Dec 12, 2001"
                appointmentListAdapter.notifyDataSetChanged()
            }
            from = "filter"
            consultFilter = consultType
            payment = paymentVal
            modeFilter = consultMode
            checkInStatus = attendence
            allAppointsDates
            getAppointmentDetails(
                durationFilter,
                modeFilter,
                searchFilterText.toString(),
                sort,
                sortBy,
                pageNumber.toString() + "",
                perPageString,
                activePastFilter,
                consultFilter,
                productId,
                checkInStatus,
                payment,
                "filter",
                selectedDated.toString()
            )
        }
        apptCalendar!!.setOnClickListener {
            if (progressBar!!.visibility == View.GONE) {
                dateChangedInCal = null
                selectedDated = LocalDate.now()
                apptListView!!.visibility = View.GONE
                apptCalendarLayout!!.visibility = View.VISIBLE
                apptHeaderSearch!!.visibility = View.VISIBLE
                appointmentHeaderFilter!!.visibility = View.VISIBLE
                apptHeaderListView!!.visibility = View.VISIBLE
                apptCalendarNoRecord!!.visibility = View.GONE
                apptCalendarRecycler!!.visibility = View.GONE
                calendarView!!.setOnTouchListener(onTouchListener)
                mode = "week"
                calendarView!!.updateMonthConfiguration(
                    InDateStyle.ALL_MONTHS,
                    OutDateStyle.END_OF_ROW,
                    1,
                    true
                )
                calendarView!!.scrollToDate(today)
                currentLayout = "calendar"
                c = 0
                groupData.clear()
                pageNumber = 1
                durationFilter = "Specific"
                //  modeFilter = "All";
                //consultFilter = "0";
                searchFilterText = ""
                //  checkInStatus = "";
                //  payment = "All";
                if (ApiUrls.activePastFilterFlag.equals("active", ignoreCase = true)) {
                    activePastFilter = "active"
                } else if (ApiUrls.activePastFilterFlag.equals("past", ignoreCase = true)) {
                    activePastFilter = "past"
                } else {
                    activePastFilter = "active"
                }
                allAppointsDates
                if (!appointmentModelList.isEmpty()) {
                    appointmentModelList.clear()
                    dummyDate = "Dec 12, 2001"
                    appointmentListAdapter.notifyDataSetChanged()
                }
                if (!TextUtils.isEmpty(apptSearchPatient!!.text.toString().trim { it <= ' ' })) {
                    makeSearchViewEmpty = false
                    apptSearchPatient!!.setText("")
                    selectedPatient = ""
                    makeSearchViewEmpty = true
                }
                from = "calendar"
                getAppointmentDetails(
                    durationFilter,
                    modeFilter,
                    searchFilterText.toString(),
                    sort,
                    sortBy,
                    pageNumber.toString() + "",
                    perPageString,
                    activePastFilter,
                    consultFilter,
                    productId,
                    checkInStatus,
                    payment,
                    "calendar",
                    LocalDate.now().toString()
                )
            }
            MyClinicGlobalClass.logUserActionEvent(
                ApiUrls.doctorId,
                getString(
                    R.string.AppointmentsCalenderView
                ),
                null
            )
        }
        apptFilter!!.setOnClickListener {
            appointmentFilterBottomSheeet!!.show(
                requireActivity().supportFragmentManager,
                "filter Sheet Dialog Fragment"
            )
            MyClinicGlobalClass.logUserActionEvent(
                ApiUrls.doctorId,
                getString(R.string.AppointmentsListFilters),
                null
            )
        }
        apptHeaderListView!!.setOnClickListener {
            selectedDated = LocalDate.now()
            apptListView!!.visibility = View.VISIBLE
            apptCalendarLayout!!.visibility = View.GONE
            apptHeaderSearch!!.visibility = View.GONE
            appointmentHeaderFilter!!.visibility = View.GONE
            apptHeaderListView!!.visibility = View.GONE
            apptRecycler!!.visibility = View.GONE
            apptCalendarSearchlayout!!.visibility = View.GONE
            currentLayout = "main"
            c = 0
            groupData.clear()
            pageNumber = 1
            durationFilter = "All"
            // modeFilter = "All";
            // consultFilter = "0";
            searchFilterText = ""
            //  checkInStatus = "";
            //  payment = "All";
            if (!appointmentModelList.isEmpty()) {
                appointmentModelList.clear()
                dummyDate = "Dec 12, 2001"
                appointmentListAdapter.notifyDataSetChanged()
            }
            if (!TextUtils.isEmpty(apptCalendarSearchPatient!!.text.toString()
                    .trim { it <= ' ' })
            ) {
                makeSearchViewEmpty = false
                apptCalendarSearchPatient!!.setText("")
                selectedPatient = ""
                makeSearchViewEmpty = true
            }
            from = "main"
            getAppointmentDetails(
                durationFilter,
                modeFilter,
                searchFilterText.toString(),
                sort,
                sortBy,
                pageNumber.toString() + "",
                perPageString,
                activePastFilter,
                consultFilter,
                productId,
                checkInStatus,
                payment,
                from,
                selectedDated.toString()
            )
        }
        apptCalendarNestedScroll!!.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
            Log.i("scroll data", scrollY.toString() + "")
            if (mode.equals("month", ignoreCase = true)) {
                mode = "week"
                calendarView!!.updateMonthConfiguration(
                    InDateStyle.ALL_MONTHS,
                    OutDateStyle.END_OF_ROW,
                    1,
                    true
                )
                calendarView!!.scrollToDate(today)
                calendarView!!.setOnTouchListener(onTouchListener)
            }

            /* if (scrollY > oldScrollY) {
                        mode = "week";
                        calendarView!!.updateMonthConfiguration(InDateStyle.ALL_MONTHS, OutDateStyle.END_OF_ROW, 1, true);
                        calendarView!!.scrollToDate(today);
                    }*/
        })
        gestureDetector = GestureDetector(this)
        onTouchListener = this
        calendarView!!.setOnTouchListener(onTouchListener)

        /*  calendarView!!.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return gestureDetector.onTouchEvent(motionEvent);
            }
        });*/appointmentHeaderFilter!!.setOnClickListener {
            appointmentFilterBottomSheeet!!.show(
                requireActivity().supportFragmentManager,
                "filter Sheet Dialog Fragment"
            )
        }
        addAppoinment!!.setOnClickListener {
            registerReceiverAppointment()
            val intent = Intent(requireContext(), BookAppointmentActivity::class.java)
            intent.putExtra("bookAppointment", bookApptValue)
            intent.putExtra("patient name", selectedPatient)
            requireContext().startActivity(intent)
            MyClinicGlobalClass.logUserActionEvent(
                ApiUrls.doctorId,
                getString(R.string.AppointmentsFloatingBookAppointment),
                null
            )
        }
        serviceLayout!!.setOnClickListener {
            MyClinicGlobalClass.logUserActionEvent(
                ApiUrls.doctorId,
                requireActivity().getString(R.string.AppointmentsListSelectClinic),
                null
            )
            if (progressBar!!.visibility == View.GONE) {
                if (!noApptText!!.text.toString().trim { it <= ' ' }
                        .equals(
                            "Please set up your practices first",
                            ignoreCase = true
                        )) dialog!!.show()
            }
        }
        appointmentCalendarListAdapter = AppointmentCalendarListAdapter(
            appointmentModelList,
            requireActivity(),
            groupData,
            requireActivity()
        ) { v, loadMore, appointmentModel, appointmentPostion ->
            if (loadMore.contains("calendarloadMore")) {
                appointmentModelList.removeAt(appointmentModelList.size - 1)
                pageNumber += 1
                if (apptOpenTabView!!.visibility == View.VISIBLE) {
                    activePastFilter = "active"
                    sort = "asc"
                } else {
                    activePastFilter = "past"
                    sort = "desc"
                }
                if (!appointmentModelList.isEmpty()) {
                    appointmentListAdapter.notifyDataSetChanged()
                }
                from = "calendar"
                durationFilter = "Specific"
                getAppointmentDetails(
                    durationFilter,
                    modeFilter,
                    searchFilterText.toString(),
                    sort,
                    sortBy,
                    pageNumber.toString() + "",
                    perPageString,
                    activePastFilter,
                    consultFilter,
                    productId,
                    checkInStatus,
                    payment,
                    "calendar",
                    selectedDated.toString()
                )
            } else if (loadMore.contains("canAppt")) {
                singleCancelApptPopup(
                    appointmentModel.appointmentId,
                    appointmentModel.refundAmt,
                    appointmentModel.orderId,
                    appointmentModel.unmapped_status,
                    appointmentModel.netTotalPaid,
                    appointmentModel.apptMode
                )
            } else if (loadMore.contains("apptCalednarDetails")) {
                registerReceiverAppointment()
                val intent = Intent(requireActivity(), AppointmentDetailsActivity::class.java)
                intent.putExtra("apptID", appointmentModel.appointmentId)
                intent.putExtra("orderId", appointmentModel.orderId)
                intent.putExtra("apptMode", appointmentModel.apptMode)
                intent.putExtra("procedureId", appointmentModel.productId)
                intent.putExtra("patientName", appointmentModel.patientName)
                intent.putExtra("appointmentDate", appointmentModel.apptDate)
                intent.putExtra("appointmentTime", appointmentModel.apptTime)
                intent.putExtra("patientId", appointmentModel.patientId)
                intent.putExtra("type", "main")
                intent.putExtra("refundAmt", appointmentModel.refundAmt)
                intent.putExtra("receiptUrl", appointmentModel.receiptUrl)
                intent.putExtra("invoiceData", appointmentModel.invoiceUrl)
                intent.putExtra("scheduletime", appointmentModel.scheduleTime)
                intent.putExtra("appointmentTime", appointmentModel.apptTime)
                intent.putExtra("appointmentPosition", appointmentPostion)
                intent.putExtra("Video_tool", appointmentModel.video_tool)
                intent.putExtra("AppointActivePast", appointmentModel.activePast)
                if (!appointmentModel.doctor_join_external_url.equals(
                        "null",
                        ignoreCase = true
                    ) || !appointmentModel.doctor_join_external_url.isEmpty()
                ) {
                    intent.putExtra(
                        "Doctor_Join_External_Url",
                        appointmentModel.doctor_join_external_url
                    )
                }
                startActivity(intent)
            } else if (loadMore.contains("Take Notes")) {
                val intent = Intent(requireActivity(), EMRActivity::class.java)
                intent.putExtra("ApptId", appointmentID)
                intent.putExtra("PatientId", appointmentModel.patientId)
                intent.putExtra("ApptMode", appointmentModel.apptMode)
                intent.putExtra("ApptDate", appointmentModel.apptDate)
                intent.putExtra("ApptTime", appointmentModel.apptTime)
                intent.putExtra("PatientName", appointmentModel.patientName)
                startActivity(intent)
            } else if (loadMore.contains("calendar join video")) {
                //join video
                joinVideo(appointmentModel)
            } else if (loadMore.contains("setUp doctor video link") || loadMore.contains("setUp patient video link")) {
                setUpExternalVideoLink(appointmentModel, loadMore)
            } else if (loadMore.contains("send video link")) {
                notifyPatientExternalJoinVideo(appointmentModel.appointmentId)
            } else if (loadMore.contains("payment")) {
                registerReceiverAppointment()
                val intent = Intent(requireActivity(), AppointmentDetailsActivity::class.java)
                intent.putExtra("apptID", appointmentModel.appointmentId)
                intent.putExtra("orderId", appointmentModel.orderId)
                intent.putExtra("apptMode", appointmentModel.apptMode)
                intent.putExtra("procedureId", appointmentModel.productId)
                intent.putExtra("patientName", appointmentModel.patientName)
                intent.putExtra("patientId", appointmentModel.patientId)
                intent.putExtra("type", "payment")
                intent.putExtra("refundAmt", appointmentModel.refundAmt)
                intent.putExtra("receiptUrl", appointmentModel.receiptUrl)
                intent.putExtra("invoiceData", appointmentModel.invoiceUrl)
                intent.putExtra("scheduletime", appointmentModel.scheduleTime)
                intent.putExtra("appointmentTime", appointmentModel.apptTime)
                intent.putExtra("appointmentPosition", appointmentPostion)
                intent.putExtra("AppointActivePast", appointmentModel.activePast)
                startActivity(intent)
            } else if (loadMore.contains("CompleteAppt")) {
                registerReceiverAppointment()
                val intent = Intent(requireActivity(), AppointCompletedActivity::class.java)
                intent.putExtra("apptID", appointmentModel.appointmentId)
                intent.putExtra("orderId", appointmentModel.orderId)
                intent.putExtra("apptMode", appointmentModel.apptMode)
                intent.putExtra("procedureId", appointmentModel.productId)
                intent.putExtra("patientName", appointmentModel.patientName)
                intent.putExtra("patientId", appointmentModel.patientId)
                intent.putExtra("type", "payment")
                intent.putExtra("scheduletime", appointmentModel.scheduleTime)
                intent.putExtra("appointmentTime", appointmentModel.apptTime)
                intent.putExtra("appointmentPosition", appointmentPostion)
                intent.putExtra("appointmentDate", appointmentModel.apptDate)
                startActivity(intent)
            } else if (loadMore.equals("settlementPending", ignoreCase = true)) {
                val intent = Intent(requireActivity(), PaymentHistoryTimeLineActivity::class.java)
                intent.putExtra("apptID", appointmentModel.appointmentId)
                intent.putExtra("orderId", appointmentModel.orderId)
                intent.putExtra("refundAmt", appointmentModel.refundAmt)
                intent.putExtra("receiptUrl", appointmentModel.receiptUrl)
                intent.putExtra("invoiceData", appointmentModel.invoiceUrl)
                startActivity(intent)
            } else if (loadMore.equals("canRefund", ignoreCase = true)) {
                cancelRefundPopup(appointmentModel.orderId, appointmentModel.refundAmt)
            } else if (loadMore.equals("Reschedule Appt", ignoreCase = true)) {
                if (globalClass.isOnline) {
                    registerReceiverAppointment()
                    val intent =
                        Intent(requireActivity(), AppointmentRescheduleActivity::class.java)
                    intent.putExtra("InternalSaasId", appointmentModel.internalSaas_id)
                    intent.putExtra("PatientName", appointmentModel.patientName)
                    if (appointmentModel.generalID != null && !appointmentModel.generalID.equals(
                            "",
                            ignoreCase = true
                        )
                    ) {
                        intent.putExtra("PatientGeneralID", appointmentModel.generalID)
                    }
                    intent.putExtra("PatientNumber", appointmentModel.patientPhoneNumber)
                    intent.putExtra("AppointmentMode", appointmentModel.clinicName)
                    intent.putExtra("AppointmentTime", appointmentModel.apptTime)
                    intent.putExtra("AppointmentId", appointmentModel.appointmentId)
                    intent.putExtra("AppointmentDate", appointmentModel.apptDate)
                    startActivity(intent)
                } else {
                    globalClass.noInternetConnection.showDialog(requireActivity())
                }
            }
        }
        val calendarlayoutManager: RecyclerView.LayoutManager = LinearLayoutManager(context)
        apptCalendarRecycler!!.layoutManager = calendarlayoutManager
        apptCalendarRecycler!!.itemAnimator = DefaultItemAnimator()
        apptCalendarRecycler!!.adapter = appointmentCalendarListAdapter
        appointmentListAdapter = AppointmentListAdapter(
            appointmentModelList,
            requireActivity(),
            groupData,
            apptSize,
            requireActivity(),
            activePastFilter
        ) { v, loadMore, appointmentModel, appointmentPostion ->
            if (loadMore.contains("IamLate")) {
                try {
                    apptIdArr = JSONArray()
                    for (i in appointmentModelList.indices) {
                        val model = appointmentModelList.get(i)
                        if (model.apptDate.equals(
                                appointmentModel.apptDate,
                                ignoreCase = true
                            ) && model.apptMode == 3
                        ) {
                            apptIdArr.put(model.appointmentId)
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                if (apptIdArr.length() > 0) {
                    IamLatePopup(appointmentModel.apptDate)
                } else {
                    Toast.makeText(
                        requireActivity(),
                        "You do not have clinic appointments to send delay intimation",
                        Toast.LENGTH_LONG
                    ).show()
                }
            } else if (loadMore.contains("cancel")) {
                val builder = AlertDialog.Builder(requireActivity())
                builder.setTitle("Cancel Confirm")
                builder.setMessage("Do you want cancel appointment?")
                builder.setPositiveButton("YES") { dialog, which ->
                    ZohoSalesIQ.Tracking.setCustomAction("ApptList - Cancel All Appt - Yes")
                    cancelAllAppt(appointmentModel.apptDate)
                    dialog.dismiss()
                }
                builder.setNegativeButton("NO") { dialog, which -> // Do nothing
                    ZohoSalesIQ.Tracking.setCustomAction("ApptList - Cancel All Appt - No")
                    dialog.dismiss()
                }
                val alert = builder.create()
                alert.show()
            } else if (loadMore.contains("canAppt")) {
                singleCancelApptPopup(
                    appointmentModel.appointmentId,
                    appointmentModel.refundAmt,
                    appointmentModel.orderId,
                    appointmentModel.unmapped_status,
                    appointmentModel.netTotalPaid,
                    appointmentModel.apptMode
                )
            } else if (loadMore.contains("loadMore")) {
                appointmentModelList.removeAt(appointmentModelList.size - 1)
                pageNumber = pageNumber + 1
                if (apptOpenTabView!!.visibility == View.VISIBLE) {
                    activePastFilter = "active"
                    sort = "asc"
                } else {
                    activePastFilter = "past"
                    sort = "desc"
                }
                if (!appointmentModelList.isEmpty()) {
                    appointmentListAdapter.notifyDataSetChanged()
                }
                from = "main"
                durationFilter = "All"
                getAppointmentDetails(
                    durationFilter,
                    modeFilter,
                    searchFilterText.toString(),
                    sort,
                    sortBy,
                    pageNumber.toString() + "",
                    perPageString,
                    activePastFilter,
                    consultFilter,
                    productId,
                    checkInStatus,
                    payment,
                    "main",
                    selectedDated.toString()
                )
            } else if (loadMore.contains("apptDetails")) {
                registerReceiverAppointment()
                Log.d("called", "called")
                val intent = Intent(requireActivity(), AppointmentDetailsActivity::class.java)
                intent.putExtra("apptID", appointmentModel.appointmentId)
                intent.putExtra("orderId", appointmentModel.orderId)
                intent.putExtra("apptMode", appointmentModel.apptMode)
                intent.putExtra("procedureId", appointmentModel.productId)
                intent.putExtra("patientName", appointmentModel.patientName)
                intent.putExtra("patientId", appointmentModel.patientId)
                intent.putExtra("type", "main")
                intent.putExtra("refundAmt", appointmentModel.refundAmt)
                intent.putExtra("receiptUrl", appointmentModel.receiptUrl)
                intent.putExtra("invoiceData", appointmentModel.invoiceUrl)
                intent.putExtra("scheduletime", appointmentModel.scheduleTime)
                intent.putExtra("appointmentTime", appointmentModel.apptTime)
                intent.putExtra("appointmentPosition", appointmentPostion)
                intent.putExtra("Video_tool", appointmentModel.video_tool)
                intent.putExtra("AppointActivePast", appointmentModel.activePast)
                if (!appointmentModel.doctor_join_external_url.equals(
                        "null",
                        ignoreCase = true
                    ) || !appointmentModel.doctor_join_external_url.isEmpty()
                ) {
                    intent.putExtra(
                        "Doctor_Join_External_Url",
                        appointmentModel.doctor_join_external_url
                    )
                }
                startActivity(intent)
            } else if (loadMore.contains("Take Notes")) {
                val intent = Intent(requireActivity(), EMRActivity::class.java)
                intent.putExtra("ApptId", appointmentID)
                intent.putExtra("PatientId", appointmentModel.patientId)
                intent.putExtra("ApptMode", appointmentModel.apptMode)
                intent.putExtra("ApptDate", appointmentModel.apptDate)
                intent.putExtra("ApptTime", appointmentModel.apptTime)
                intent.putExtra("PatientName", appointmentModel.patientName)
                requireActivity().startActivity(intent)
            } else if (loadMore.contains("join video")) {
                //join video
                joinVideo(appointmentModel)
            } else if (loadMore.contains("setUp doctor video link") || loadMore.contains("setUp patient video link")) {
                setUpExternalVideoLink(appointmentModel, loadMore)
            } else if (loadMore.contains("send video link")) {
                notifyPatientExternalJoinVideo(appointmentModel.appointmentId)
            } else if (loadMore.contains("payment")) {
                val intent = Intent(requireActivity(), AppointmentDetailsActivity::class.java)
                intent.putExtra("apptID", appointmentModel.appointmentId)
                intent.putExtra("orderId", appointmentModel.orderId)
                intent.putExtra("apptMode", appointmentModel.apptMode)
                intent.putExtra("procedureId", appointmentModel.productId)
                intent.putExtra("patientName", appointmentModel.patientName)
                intent.putExtra("patientId", appointmentModel.patientId)
                intent.putExtra("type", "payment")
                intent.putExtra("refundAmt", appointmentModel.refundAmt)
                intent.putExtra("receiptUrl", appointmentModel.receiptUrl)
                intent.putExtra("invoiceData", appointmentModel.invoiceUrl)
                intent.putExtra("scheduletime", appointmentModel.scheduleTime)
                intent.putExtra("appointmentTime", appointmentModel.apptTime)
                intent.putExtra("appointmentPosition", appointmentPostion)
                intent.putExtra("AppointActivePast", appointmentModel.activePast)
                startActivity(intent)
            } else if (loadMore.contains("CompleteAppt")) {
                registerReceiverAppointment()
                val intent = Intent(requireActivity(), AppointCompletedActivity::class.java)
                intent.putExtra("apptID", appointmentModel.appointmentId)
                intent.putExtra("orderId", appointmentModel.orderId)
                intent.putExtra("apptMode", appointmentModel.apptMode)
                intent.putExtra("procedureId", appointmentModel.productId)
                intent.putExtra("patientName", appointmentModel.patientName)
                intent.putExtra("patientId", appointmentModel.patientId)
                intent.putExtra("type", "payment")
                intent.putExtra("scheduletime", appointmentModel.scheduleTime)
                intent.putExtra("appointmentTime", appointmentModel.apptTime)
                intent.putExtra("appointmentPosition", appointmentPostion)
                intent.putExtra("appointmentDate", appointmentModel.apptDate)
                requireActivity().startActivity(intent)
            } else if (loadMore.equals("settlementPending", ignoreCase = true)) {
                val intent = Intent(requireActivity(), PaymentHistoryTimeLineActivity::class.java)
                intent.putExtra("apptID", appointmentModel.appointmentId)
                intent.putExtra("orderId", appointmentModel.orderId)
                intent.putExtra("refundAmt", appointmentModel.refundAmt)
                intent.putExtra("receiptUrl", appointmentModel.receiptUrl)
                intent.putExtra("invoiceData", appointmentModel.invoiceUrl)
                startActivity(intent)
            } else if (loadMore.equals("canRefund", ignoreCase = true)) {
                cancelRefundPopup(appointmentModel.orderId, appointmentModel.refundAmt)
            } else if (loadMore.equals("Reschedule Appt", ignoreCase = true)) {
                registerReceiverAppointment()
                val intent = Intent(requireActivity(), AppointmentRescheduleActivity::class.java)
                intent.putExtra("InternalSaasId", appointmentModel.internalSaas_id)
                intent.putExtra("PatientName", appointmentModel.patientName)
                if (appointmentModel.generalID != null && !appointmentModel.generalID.equals(
                        "",
                        ignoreCase = true
                    )
                ) {
                    intent.putExtra("PatientGeneralID", appointmentModel.generalID)
                }
                intent.putExtra("PatientNumber", appointmentModel.patientPhoneNumber)
                intent.putExtra("AppointmentMode", appointmentModel.clinicName)
                intent.putExtra("AppointmentTime", appointmentModel.apptTime)
                intent.putExtra("AppointmentId", appointmentModel.appointmentId)
                intent.putExtra("AppointmentDate", appointmentModel.apptDate)
                startActivity(intent)
            }
        }
        layoutManager = LinearLayoutManager(context)
        apptRecycler!!.layoutManager = layoutManager
        apptRecycler!!.itemAnimator = DefaultItemAnimator()
        apptRecycler!!.adapter = appointmentListAdapter
        apptRecycler!!.setHasFixedSize(true)
        apptRecycler!!.addOnScrollListener(object : RecyclerView.OnScrollListener() {

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (isListExhausted) {
                    return
                }
                val toatlcount = layoutManager!!.itemCount
                val lastitem = layoutManager!!.findLastVisibleItemPosition()
                if (!loading) {
                    if (lastitem != RecyclerView.NO_POSITION && lastitem == toatlcount - 1) {
                        pageNumber = pageNumber + 1
                        if (apptOpenTabView!!.visibility == View.VISIBLE) {
                            activePastFilter = "active"
                            sort = "asc"
                        } else {
                            activePastFilter = "past"
                            sort = "desc"
                        }
                        if (!appointmentModelList.isEmpty()) {
                            appointmentListAdapter.notifyDataSetChanged()
                        }
                        from = if (!searchFilterText.toString().equals("", ignoreCase = true)) {
                            "search"
                        } else {
                            "main"
                        }
                        durationFilter = "All"
                        getAppointmentDetails(
                            durationFilter,
                            modeFilter,
                            searchFilterText.toString(),
                            sort,
                            sortBy,
                            pageNumber.toString() + "",
                            perPageString,
                            activePastFilter,
                            consultFilter,
                            productId,
                            checkInStatus,
                            payment,
                            from,
                            selectedDated.toString()
                        )
                        loading = true
                    } else {
                        loading = false
                    }
                }
                super.onScrolled(recyclerView, dx, dy)
            }
        })
        class DayViewContainer(view: View) : ViewContainer(view) {
            var day: com.kizitonwose.calendarview.model.CalendarDay? = null
            val textView: TextView
            val imgDot: ImageView

            init {
                textView = view.findViewById(R.id.exOneDayText)
                imgDot = view.findViewById(R.id.img_dot)
                textView.setOnClickListener {
                    durationFilter = "Specific"
                    if (selectedDated == day!!.date) {
                        selectedDated = today
                        try {
                            val date =
                                Date.from(
                                    selectedDated.atStartOfDay(ZoneId.systemDefault())
                                        .toInstant()
                                )
                            currentDate =
                                SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(
                                    Date()
                                )
                            selectedDate =
                                SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(date)
                            val sdf = SimpleDateFormat("yyyy-MM-dd")
                            if (sdf.parse(currentDate).after(sdf.parse(selectedDate))) {
                                apptCalendarBookAppt!!.visibility = View.GONE
                                addAppoinment!!.visibility = View.GONE
                            } else if (sdf.parse(currentDate) == sdf.parse(selectedDate)) {
                                apptCalendarBookAppt!!.visibility = View.VISIBLE
                                addAppoinment!!.visibility = View.VISIBLE
                            } else {
                                apptCalendarBookAppt!!.visibility = View.VISIBLE
                                addAppoinment!!.visibility = View.VISIBLE
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                        if (!appointmentModelList.isEmpty()) {
                            appointmentModelList.clear()
                            dummyDate = "Dec 12, 2001"
                            appointmentListAdapter.notifyDataSetChanged()
                        }
                        from = "calendar"
                        getAppointmentDetails(
                            durationFilter,
                            modeFilter,
                            searchFilterText.toString(),
                            sort,
                            sortBy,
                            pageNumber.toString() + "",
                            perPageString,
                            activePastFilter,
                            consultFilter,
                            productId,
                            checkInStatus,
                            payment,
                            "calendar",
                            selectedDated.toString()
                        )
                    } else {
                        selectedDated = day!!.date
                        try {
                            val date =
                                Date.from(
                                    selectedDated.atStartOfDay(ZoneId.systemDefault())
                                        .toInstant()
                                )
                            currentDate =
                                SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(
                                    Date()
                                )
                            selectedDate =
                                SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(date)
                            val sdf = SimpleDateFormat("yyyy-MM-dd")
                            if (sdf.parse(currentDate).after(sdf.parse(selectedDate))) {
                                apptCalendarBookAppt!!.visibility = View.GONE
                                addAppoinment!!.visibility = View.GONE
                            } else if (sdf.parse(currentDate) == sdf.parse(selectedDate)) {
                                apptCalendarBookAppt!!.visibility = View.VISIBLE
                                addAppoinment!!.visibility = View.VISIBLE
                            } else {
                                apptCalendarBookAppt!!.visibility = View.VISIBLE
                                addAppoinment!!.visibility = View.VISIBLE
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                        c = 0
                        groupData.clear()
                        if (!appointmentModelList.isEmpty()) {
                            appointmentModelList.clear()
                            dummyDate = "Dec 12, 2001"
                            appointmentListAdapter.notifyDataSetChanged()
                        }
                        from = "calendar"
                        getAppointmentDetails(
                            durationFilter,
                            modeFilter,
                            searchFilterText.toString(),
                            sort,
                            sortBy,
                            pageNumber.toString() + "",
                            perPageString,
                            activePastFilter,
                            consultFilter,
                            productId,
                            checkInStatus,
                            payment,
                            "calendar",
                            selectedDated.toString()
                        )
                    }
                    dateChangedInCal = selectedDated
                    calendarView!!.notifyCalendarChanged()
                }
            }
        }
        calendarView!!.monthScrollListener = { calendarMonth: CalendarMonth ->
            monthText!!.text = calendarMonth.yearMonth.month.toString() + " " + calendarMonth.year
            Unit
        }
        calendarView!!.dayBinder = object : DayBinder<DayViewContainer> {
            override fun create(view: View): DayViewContainer {
                return DayViewContainer(view)
            }

            override fun bind(
                container: DayViewContainer,
                day: com.kizitonwose.calendarview.model.CalendarDay,
            ) {
                container.day = day
                container.imgDot.visibility = View.GONE
                container.textView.text = day.date.dayOfMonth.toString()
                if (day.owner == DayOwner.THIS_MONTH) {
                    if (today == day.date) {
                        container.textView.background = ContextCompat.getDrawable(
                            requireActivity(),
                            R.drawable.no_appointment_image_background
                        )
                        container.textView.setTextColor(
                            ContextCompat.getColor(
                                requireActivity(),
                                R.color.colorWhite
                            )
                        )
                        if (dateChangedInCal != null && dateChangedInCal == today) {
                            container.textView.background = ContextCompat.getDrawable(
                                requireActivity(),
                                R.drawable.calendar_select_date
                            )
                            container.textView.setTextColor(
                                ContextCompat.getColor(
                                    requireActivity(),
                                    R.color.colorWhite
                                )
                            )
                        }
                    } else if (selectedDated == day.date) {
                        container.textView.background = ContextCompat.getDrawable(
                            requireActivity(),
                            R.drawable.calendar_select_date
                        )
                        container.textView.setTextColor(
                            ContextCompat.getColor(
                                requireActivity(),
                                R.color.colorWhite
                            )
                        )
                    } else {
                        container.textView.background = null
                        container.textView.setTextColor(
                            ContextCompat.getColor(
                                requireActivity(),
                                R.color.colorBlack
                            )
                        )
                    }
                } else {
                    container.textView.background = null
                    container.textView.setTextColor(
                        ContextCompat.getColor(
                            requireActivity(),
                            R.color.colorGreyText
                        )
                    )
                }
                if (apptDates.contains(day.date)) {
                    container.imgDot.visibility = View.VISIBLE
                } else {
                    container.imgDot.visibility = View.GONE
                }
            }
        }
        apptNoPatientBookAppointment!!.setOnClickListener(View.OnClickListener {
            MyClinicGlobalClass.logUserActionEvent(
                ApiUrls.doctorId,
                getString(R.string.AppointmentsSearchPatientBookAppointment),
                null
            )
            registerReceiverAppointment()
            val intent = Intent(requireContext(), BookAppointmentActivity::class.java)
            intent.putExtra("bookAppointment", bookApptValue)
            if (bookApptValue == 1) {
                intent.putExtra("patient name", selectedPatient)
            } else {
                intent.putExtra("patient name",
                    apptSearchPatient!!.text.toString().trim { it <= ' ' })
            }
            requireContext().startActivity(intent)
        })
        apptCalendarBookAppt!!.setOnClickListener(View.OnClickListener {
            registerReceiverAppointment()
            val intent = Intent(requireContext(), BookAppointmentActivity::class.java)
            intent.putExtra("bookAppointment", bookApptValue)
            intent.putExtra("patient name", selectedPatient)
            requireContext().startActivity(intent)
            MyClinicGlobalClass.logUserActionEvent(
                ApiUrls.doctorId,
                getString(R.string.AppointmentsCalenderBookAppointment),
                null
            )
        })
        apptClearFilter!!.setOnClickListener(View.OnClickListener {
            durationFilter = if (currentLayout.contains("calendar")) {
                "Specific"
            } else {
                "All"
            }
            modeFilter = "All"
            consultFilter = "0"
            checkInStatus = ""
            payment = "All"
            c = 0
            groupData.clear()
            if (!appointmentModelList.isEmpty()) {
                appointmentListAdapter.notifyDataSetChanged()
            }
            appointmentFilterBottomSheeet!!.setupBottomSheet(
                durationFilter,
                modeFilter,
                consultFilter,
                activePastFilter,
                checkInStatus,
                payment
            )
            from = "calendar"
            allAppointsDates
            getAppointmentDetails(
                durationFilter,
                modeFilter,
                searchFilterText.toString(),
                sort,
                sortBy,
                pageNumber.toString() + "",
                perPageString,
                activePastFilter,
                consultFilter,
                productId,
                checkInStatus,
                payment,
                "calendar",
                selectedDated.toString()
            )
            MyClinicGlobalClass.logUserActionEvent(
                ApiUrls.doctorId,
                getString(R.string.AppointmentsCalenderBookAppointment),
                null
            )
        })
        leftArrow!!.setOnClickListener(View.OnClickListener {
            val calendarMonth = calendarView!!.findFirstVisibleMonth()
            calendarView!!.smoothScrollToMonth(calendarMonth!!.yearMonth.minusMonths(1))
        })
        rightArrow!!.setOnClickListener(View.OnClickListener {
            val calendarMonth = calendarView!!.findFirstVisibleMonth()
            calendarView!!.smoothScrollToMonth(calendarMonth!!.yearMonth.plusMonths(1))
        })
        return view
    }

    private val services: Unit
        get() {
            appointmentViewModel.getServiceDetails(requireActivity())
                .observe(viewLifecycleOwner, object : Observer<String> {
                    override fun onChanged(s: String) {
                        Log.i("service res", s)
                        try {
                            val response = JSONObject(s)
                            if (response.getInt("status_code") == 200) {
                                val services =
                                    response.getJSONObject("response").getJSONArray("response")
                                if (services.length() > 0) {
                                    servicenames = ""
                                    productId = ""
                                    appointmentServicesModelArrayList.clear()
                                    serviceData.clear()
                                    for (i in 0 until services.length()) {
                                        val service = services.getJSONObject(i)
                                        appointmentServicesModelArrayList.add(
                                            AppointmentServicesModel(
                                                service.getInt("user_id"),
                                                service.getString("product_name"),
                                                service.getInt("product_id"),
                                                service.getInt("num_appointments")
                                            )
                                        )
                                        serviceData.add(
                                            service.getString("product_name") + " - " + service.getInt(
                                                "num_appointments"
                                            ) + " Appts"
                                        )
                                        if (TextUtils.isEmpty(productId)) {
                                            productId = service.getInt("product_id").toString()
                                            servicenames = service.getString("product_name")
                                        } else {
                                            productId =
                                                productId + "," + service.getInt("product_id")
                                            servicenames =
                                                servicenames + "," + service.getString("product_name")
                                        }
                                    }
                                    data = serviceData.toTypedArray()
                                    check = BooleanArray(data.size)
                                    for (j in data.indices) {
                                        check[j] = true
                                    }
                                    builder!!.setMultiChoiceItems(
                                        data,
                                        check
                                    ) { dialogInterface, i, b -> check[i] = b }
                                    builder!!.setPositiveButton("Ok") { dialogInterface, i ->
                                        productId = ""
                                        servicenames = ""
                                        for (k in check.indices) {
                                            if (check[k]) {
                                                if (TextUtils.isEmpty(productId)) {
                                                    productId =
                                                        appointmentServicesModelArrayList[k].productId.toString() + ""
                                                    servicenames =
                                                        appointmentServicesModelArrayList[k].productName
                                                } else {
                                                    productId =
                                                        productId + "," + appointmentServicesModelArrayList[k].productId
                                                    servicenames =
                                                        servicenames + "," + appointmentServicesModelArrayList[k].productName
                                                }
                                            }
                                        }
                                        if (TextUtils.isEmpty(servicenames)) {
                                            for (j in data.indices) {
                                                check[j] = true
                                            }
                                            Toast.makeText(
                                                context,
                                                "No Service Selected. Please select at least one service to view appointments.",
                                                Toast.LENGTH_LONG
                                            ).show()
                                        } else {
                                            pageNumber = 1
                                            c = 0
                                            groupData.clear()
                                            appointmentServiceView!!.text = servicenames
                                            durationFilter =
                                                if (currentLayout.contains("calendar")) {
                                                    "Specific"
                                                } else {
                                                    "All"
                                                }
                                            if (!appointmentModelList.isEmpty()) {
                                                appointmentModelList.clear()
                                                dummyDate = "Dec 12, 2001"
                                                appointmentListAdapter.notifyDataSetChanged()
                                            }
                                            from = "serviceFilter"
                                            allAppointsDates
                                            getAppointmentDetails(
                                                durationFilter,
                                                modeFilter,
                                                searchFilterText.toString(),
                                                sort,
                                                sortBy,
                                                pageNumber.toString() + "",
                                                perPageString,
                                                activePastFilter,
                                                consultFilter,
                                                productId,
                                                checkInStatus,
                                                payment,
                                                "serviceFilter",
                                                selectedDated.toString()
                                            )
                                        }
                                    }
                                    dialog = builder!!.create()
                                    val window = dialog!!.window
                                    val layoutParams = window!!.attributes
                                    layoutParams.gravity = Gravity.TOP
                                    layoutParams.y = 100
                                    window.attributes = layoutParams
                                    appointmentServiceView!!.text = servicenames
                                } else {
                                    noApptText!!.text = "Please set up your practices first"
                                    bookAppointmentButton!!.visibility = View.GONE
                                    openCalendarView!!.visibility = View.GONE
                                    apptSearchLayout!!.visibility = View.GONE
                                    apptRecycler!!.visibility = View.GONE
                                    addAppoinment!!.visibility = View.GONE
                                    noPatientLayout!!.visibility = View.VISIBLE
                                }
                            } else {
                                errorHandler(requireContext(), s)
                            }
                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }
                    }
                })
        }

    private fun setUpExternalVideoLink(appointmentModel: AppointmentModel, loadMore: String) {
        val dialog = Dialog(requireActivity())
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.dialog_setup_external_video_link)
        val dialogHeaderText = dialog.findViewById<View>(R.id.dialogHeaderText) as TextView
        val set_up_link_et = dialog.findViewById<View>(R.id.set_up_link_et) as EditText
        val dialogCancel = dialog.findViewById<View>(R.id.dialogCancel) as ImageView
        val save_link = dialog.findViewById<View>(R.id.save_link) as Button
        dialogCancel.setOnClickListener { dialog.dismiss() }
        if (loadMore.contains("setUp patient video link")) {
            dialogHeaderText.text = "SetUp Patient Video Link"
            if (!TextUtils.isEmpty(appointmentModel.patient_join_external_url)) {
                if (!appointmentModel.patient_join_external_url.equals("null", ignoreCase = true)) {
                    set_up_link_et.setText(appointmentModel.patient_join_external_url)
                } else {
                    set_up_link_et.hint = "Joining link for patient"
                }
            } else {
                set_up_link_et.hint = "Joining link for patient"
            }
        } else {
            dialogHeaderText.text = "SetUp Doctor Video Link"
            if (!TextUtils.isEmpty(appointmentModel.doctor_join_external_url)) {
                if (!appointmentModel.doctor_join_external_url.equals("null", ignoreCase = true)) {
                    set_up_link_et.setText(appointmentModel.doctor_join_external_url)
                } else {
                    set_up_link_et.hint = "Joining link for Doctor"
                }
            } else {
                set_up_link_et.hint = "Joining link for Doctor"
            }
        }
        save_link.setOnClickListener { view: View? ->
            if (globalClass.isOnline) {
                if (!set_up_link_et.text.toString().equals("", ignoreCase = true)) {
                    loadingDialog = ProgressDialog(requireActivity())
                    loadingDialog!!.setMessage(requireActivity().resources.getString(R.string.process_request))
                    loadingDialog!!.setCancelable(false)
                    loadingDialog!!.setInverseBackgroundForced(false)
                    loadingDialog!!.show()
                    val reqObj = JSONObject()
                    try {
                        reqObj.put("appt_id", appointmentModel.appointmentId)
                        if (loadMore.contains("setUp doctor video link")) {
                            reqObj.put("doctor_join_external_url", set_up_link_et.text.toString())
                            if (!TextUtils.isEmpty(appointmentModel.patient_join_external_url)) {
                                if (!appointmentModel.doctor_join_external_url.equals(
                                        "null",
                                        ignoreCase = true
                                    )
                                ) {
                                    reqObj.put(
                                        "patient_join_external_url",
                                        appointmentModel.patient_join_external_url
                                    )
                                } else {
                                    reqObj.put("patient_join_external_url", "")
                                }
                            } else {
                                reqObj.put("patient_join_external_url", "")
                            }
                        } else {
                            reqObj.put("patient_join_external_url", set_up_link_et.text.toString())
                            if (!TextUtils.isEmpty(appointmentModel.doctor_join_external_url)) {
                                if (!appointmentModel.doctor_join_external_url.equals(
                                        "null",
                                        ignoreCase = true
                                    )
                                ) {
                                    reqObj.put(
                                        "doctor_join_external_url",
                                        appointmentModel.doctor_join_external_url
                                    )
                                } else {
                                    reqObj.put("doctor_join_external_url", "")
                                }
                            } else {
                                reqObj.put("doctor_join_external_url", "")
                            }
                        }
                        appointmentViewModel.setUpVideoLink(requireActivity(), reqObj).observe(
                            viewLifecycleOwner, object : Observer<String> {
                                override fun onChanged(s: String) {
                                    loadingDialog!!.dismiss()
                                    var response: JSONObject
                                    try {
                                        response = JSONObject(s)
                                        if (response.getInt("status_code") == 200) {
                                            Toast.makeText(
                                                requireActivity(),
                                                "Video Link Updated",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                            if (loadMore.contains("setUp doctor video link")) {
                                                appointmentModel.doctor_join_external_url =
                                                    set_up_link_et.text.toString()
                                                appointmentModel.patient_join_external_url =
                                                    appointmentModel.patient_join_external_url
                                            } else {
                                                appointmentModel.patient_join_external_url =
                                                    set_up_link_et.text.toString()
                                                appointmentModel.doctor_join_external_url =
                                                    appointmentModel.doctor_join_external_url
                                            }
                                            if (from.equals("main", ignoreCase = true)) {
                                                appointmentListAdapter.notifyDataSetChanged()
                                            } else {
                                                appointmentCalendarListAdapter.notifyDataSetChanged()
                                            }
                                            dialog.dismiss()
                                        } else {
                                            errorHandler(requireContext(), s)
                                        }
                                    } catch (e: JSONException) {
                                        e.printStackTrace()
                                    }
                                }
                            })
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                } else {
                    Toast.makeText(requireActivity(), "Please enter video url", Toast.LENGTH_SHORT)
                        .show()
                }
            } else {
                globalClass.noInternetConnection.showDialog(requireActivity())
            }
        }
        dialog.show()
    }

    fun changeMode() {
        /*if (mode.equalsIgnoreCase("month")) {
            mode = "week";
            calendarView!!.updateMonthConfiguration(InDateStyle.FIRST_MONTH, OutDateStyle.END_OF_ROW, 1, true);
            calendarView!!.scrollToDate(today);
        } else {
            mode = "month";
            calendarView!!.updateMonthConfiguration(InDateStyle.ALL_MONTHS, OutDateStyle.END_OF_ROW, 6, true);
            calendarView!!.scrollToDate(today);
        }*/
        if (mode.equals("week", ignoreCase = true)) {
            mode = "month"
            calendarView!!.updateMonthConfiguration(
                InDateStyle.ALL_MONTHS,
                OutDateStyle.END_OF_GRID,
                6,
                true
            )
            calendarView!!.scrollToDate(today)
            calendarView!!.setOnTouchListener(null)
        }
    }

    private fun cancelAllAppt(apptDate: String) {
        loadingDialog = ProgressDialog(requireActivity())
        loadingDialog!!.setMessage(requireActivity().resources.getString(R.string.process_request))
        loadingDialog!!.setCancelable(false)
        loadingDialog!!.setInverseBackgroundForced(false)
        loadingDialog!!.show()
        try {
            val apptIdArr = JSONArray()
            for (i in appointmentModelList.indices) {
                val model = appointmentModelList[i]
                if (model.apptDate.equals(apptDate, ignoreCase = true)) {
                    apptIdArr.put(model.appointmentId)
                }
            }
            val reqObj = JSONObject()
            reqObj.put("appointment_ids", apptIdArr)
            appointmentViewModel.cancelAppt(requireActivity(), reqObj)
                .observe(viewLifecycleOwner, object : Observer<String> {
                    override fun onChanged(s: String) {
                        loadingDialog!!.dismiss()
                        try {
                            val response = JSONObject(s)
                            if (response.getInt("status_code") == 200) {
                                Toast.makeText(
                                    requireActivity(),
                                    apptIdArr.length()
                                        .toString() + " appointments have been cancelled",
                                    Toast.LENGTH_SHORT
                                ).show()
                                val cancelledAppts: MutableList<AppointmentModel> = ArrayList()
                                for (i in appointmentModelList.indices) {
                                    val model = appointmentModelList[i]
                                    if (model.apptDate === apptDate) {
                                        cancelledAppts.add(model)
                                        if (apptIdArr.length() == 1) {
                                            groupData.removeAt(i)
                                        }
                                    }
                                }
                                appointmentModelList.removeAll(cancelledAppts)
                                appointmentListAdapter.notifyDataSetChanged()
                                if (appointmentModelList.size == 0) {
                                    noPatientLayout!!.visibility = View.VISIBLE
                                    apptSearchLayout!!.visibility = View.GONE
                                    apptRecycler!!.visibility = View.GONE
                                    addAppoinment!!.visibility = View.GONE
                                }

                                // getServices();
                            } else {
                                errorHandler(requireContext(), s)
                            }
                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }
                    }
                })
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun IamLatePopup(apptDate: String?) {
        val builder = AlertDialog.Builder(requireActivity())
        val inflater = requireActivity().layoutInflater
        val inflator = inflater.inflate(R.layout.dailog_apointment_delay, null)
        val spinner = inflator.findViewById<View>(R.id.paymentModeSpinner) as Spinner
        val userNameText = inflator.findViewById<View>(R.id.amountPaid) as EditText
        val dailogArticleCancel = inflator.findViewById<View>(R.id.dailogArticleCancel) as ImageView
        val adapter = ArrayAdapter(
            requireActivity(),
            android.R.layout.simple_spinner_item, requireActivity().resources
                .getStringArray(R.array.appointmentDelaylistArray)
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter
        builder.setView(inflator)
            .setPositiveButton(R.string.sends) { dialog, id ->
                var paymentModespin = spinner.selectedItem.toString()
                var time = userNameText.text.toString()
                if (time.equals("0", ignoreCase = true) || time.equals("", ignoreCase = true)) {
                    time = "a"
                    paymentModespin = "little"
                }
                try {
                    val apptIdArr = JSONArray()
                    for (i in appointmentModelList.indices) {
                        val model = appointmentModelList[i]
                        if (model.apptDate.equals(
                                apptDate,
                                ignoreCase = true
                            ) && model.apptMode == 3
                        ) {
                            apptIdArr.put(model.appointmentId)
                        }
                    }
                    appointmentDelay(apptIdArr, paymentModespin, time)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            .setNegativeButton(R.string.cancel) { dialog, id -> dialog.dismiss() }
        val alertDialog = builder.create()
        dailogArticleCancel.setOnClickListener { alertDialog.dismiss() }
        alertDialog.show()
    }

    fun cancelRefundPopup(order_id: Int, refund_amt: Int) {
        val dialog = Dialog(requireActivity())
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
        yesButton!!.setOnClickListener { cancelRefund(order_id, dialog) }
        noButton!!.setOnClickListener { dialog.dismiss() }
        closeButton!!.setOnClickListener { dialog.dismiss() }
        dailogArticleCancel.setOnClickListener { dialog.dismiss() }
        dialog.show()
    }

    fun singleCancelApptPopup(
        apptID: Int,
        refundAmount: Int,
        orderId: Int,
        UnmappedStatus: String,
        netTotalPaid: Int,
        apptMode: Int,
    ) {
        dialogCancel = Dialog(requireActivity())
        dialogCancel!!.setCancelable(false)
        dialogCancel!!.setContentView(R.layout.dailog_single_apointment_cancel)
        val dailogArticleCancel =
            dialogCancel!!.findViewById<View>(R.id.dailogArticleCancel) as ImageView
        val updateStatus = dialogCancel!!.findViewById<View>(R.id.updateStatus) as RelativeLayout
        val refundLayout = dialogCancel!!.findViewById<View>(R.id.refundLayout) as RelativeLayout
        et_refund_amount = dialogCancel!!.findViewById<View>(R.id.et_refund_amount) as EditText
        val radioButton4 = dialogCancel!!.findViewById<View>(R.id.radioButton4) as RadioButton
        val radioButton5 = dialogCancel!!.findViewById<View>(R.id.radioButton5) as RadioButton
        val radioButton_yes = dialogCancel!!.findViewById<View>(R.id.radioButton_yes) as RadioButton
        val radioButton_no = dialogCancel!!.findViewById<View>(R.id.radioButton_no) as RadioButton
        val updateStatusTextView =
            dialogCancel!!.findViewById<View>(R.id.updateStatusTextView) as TextView
        refundMainLayout =
            dialogCancel!!.findViewById<View>(R.id.refundMainLayout) as RelativeLayout
        refundMessageLayout =
            dialogCancel!!.findViewById<View>(R.id.refundMessageLayout) as RelativeLayout
        refundFullLayout =
            dialogCancel!!.findViewById<View>(R.id.refundFullLayout) as RelativeLayout
        messageNoteText = dialogCancel!!.findViewById<View>(R.id.messageNoteText) as TextView
        amount_paid = dialogCancel!!.findViewById<View>(R.id.amount_paid) as TextView
        val messageNoteButton =
            dialogCancel!!.findViewById<View>(R.id.messageNoteButton) as RelativeLayout
        radioGroup = dialogCancel!!.findViewById<View>(R.id.radioGroup) as RadioGroup
        radioGroup!!.clearCheck()
        radioGroup_onlineStatus =
            dialogCancel!!.findViewById<View>(R.id.radioGroup_onlineStatus) as RadioGroup
        radioGroup_onlineStatus!!.clearCheck()
        et_refund_amount!!.setText("" + refundAmount)
        amount_paid!!.text = "Amount Paid:Rs. $netTotalPaid"
        et_refund_amount!!.isEnabled = false
        updateStatus.isEnabled = false
        radioButton_yes.isEnabled = false
        radioButton_no.isEnabled = false
        if (apptMode == 1) {
            radioButton4.visibility = View.GONE
            radioButton5.visibility = View.GONE
        }
        if (UnmappedStatus.isEmpty()) {
            refundFullLayout!!.visibility = View.GONE
        } else {
            refundFullLayout!!.visibility = View.VISIBLE
        }
        radioGroup!!.setOnCheckedChangeListener { group, checkedId ->
            val rb = group.findViewById<View>(checkedId) as RadioButton
            appointmentID = apptID
            if (rb.text.toString().equals("Consulted", ignoreCase = true)) {
                apptStatusValue = 3
                et_refund_amount!!.isEnabled = false
                updateStatus.isEnabled = true
                radioButton_yes.isEnabled = true
                radioButton_no.isEnabled = true
                radioButton_no.isChecked = true
            } else if (rb.text.toString().equals("Cancelled By Patient", ignoreCase = true)) {
                apptStatusValue = 5
                et_refund_amount!!.isEnabled = false
                updateStatus.isEnabled = true
                radioButton_yes.isEnabled = true
                radioButton_no.isEnabled = true
                radioButton_no.isChecked = true
                getRefundAmount(apptStatusValue, orderId)
            } else if (rb.text.toString().equals("Cancelled by Doctor", ignoreCase = true)) {
                apptStatusValue = 8
                et_refund_amount!!.isEnabled = false
                updateStatus.isEnabled = true
                radioButton_yes.isEnabled = true
                radioButton_no.isEnabled = true
                radioButton_no.isChecked = true
                getRefundAmount(apptStatusValue, orderId)
            } else if (rb.text.toString().equals("Specialist No Show", ignoreCase = true)) {
                apptStatusValue = 7
                et_refund_amount!!.isEnabled = false
                updateStatus.isEnabled = true
                radioButton_yes.isEnabled = true
                radioButton_no.isEnabled = true
                radioButton_no.isChecked = true
                getRefundAmount(apptStatusValue, orderId)
            } else if (rb.text.toString().equals("Patient No Show", ignoreCase = true)) {
                apptStatusValue = 6
                et_refund_amount!!.isEnabled = false
                updateStatus.isEnabled = true
                radioButton_yes.isEnabled = true
                radioButton_no.isEnabled = true
                radioButton_no.isChecked = true
                getRefundAmount(apptStatusValue, orderId)
            }
        }
        radioGroup_onlineStatus!!.setOnCheckedChangeListener { group, checkedId ->
            rbYesNo = group.findViewById<View>(checkedId) as RadioButton
            appointmentID = apptID
            if (rbYesNo!!.text.toString().equals("Yes", ignoreCase = true)) {
                et_refund_amount!!.isEnabled = true
                updateStatus.isEnabled = true
                radioButton_yes.isEnabled = true
                radioButton_no.isEnabled = true
                updateStatusTextView.text = "Refund Now"
                updateStatus.background.setColorFilter(
                    Color.parseColor("#EB0000"),
                    PorterDuff.Mode.SRC_ATOP
                )
            } else if (rbYesNo!!.text.toString().equals("No", ignoreCase = true)) {
                et_refund_amount!!.isEnabled = false
                updateStatus.isEnabled = true
                radioButton_yes.isEnabled = true
                radioButton_no.isEnabled = true
                et_refund_amount!!.setText("" + refundAmount)
                updateStatusTextView.text = "Update Status"
                updateStatus.background.setColorFilter(
                    Color.parseColor("#00A65A"),
                    PorterDuff.Mode.SRC_ATOP
                )
            }
        }
        updateStatus.setOnClickListener {
            if (updateStatusTextView.text.toString().equals("Update Status", ignoreCase = true)) {
                cancelApptSingle(apptID, apptStatusValue, dialogCancel!!, activity)
            } else if (updateStatusTextView.text.toString()
                    .equals("Refund Now", ignoreCase = true)
            ) {
                cancelApptSingle(apptID, apptStatusValue, dialogCancel!!, activity)
            }
        }
        messageNoteButton.setOnClickListener { dialogCancel!!.dismiss() }
        dailogArticleCancel.setOnClickListener { dialogCancel!!.dismiss() }
        dialogCancel!!.show()
    }

    fun singleCancelApptPopupCalender(apptID: Int) {
        val dialog = Dialog(requireActivity())
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.dailog_single_apointment_cancel)
        val dailogArticleCancel = dialog.findViewById<View>(R.id.dailogArticleCancel) as ImageView
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

    fun onClear(v: View?) {
        /* Clears all selected radio buttons to default */
        radioGroup!!.clearCheck()
    }

    fun onSubmit(v: View?) {
        val rb = radioGroup!!.findViewById<View>(radioGroup!!.checkedRadioButtonId) as RadioButton
        Toast.makeText(requireActivity(), rb.text, Toast.LENGTH_SHORT).show()
    }

    fun appointmentDelay(pendingIdArray: JSONArray?, delayIn: String?, delayTime: String?) {
        otpLoading = ProgressDialog(requireActivity())
        otpLoading!!.setMessage(resources.getString(R.string.process_request))
        otpLoading!!.setProgressStyle(ProgressDialog.STYLE_SPINNER)
        otpLoading!!.setCancelable(false)
        otpLoading!!.show()
        try {
            val jsonValue = JSONObject()
            jsonValue.put("appointment_ids", pendingIdArray)
            jsonValue.put("delay_amount", delayTime)
            jsonValue.put("delay_unit", delayIn)
            appointmentViewModel.delayAppointment(requireActivity(), jsonValue).observe(
                viewLifecycleOwner, object : Observer<String> {
                    override fun onChanged(s: String) {
                        otpLoading!!.dismiss()
                        try {
                            val jsonObject = JSONObject(s)
                            if (jsonObject.getInt("status_code") == 200) {
                                Toast.makeText(
                                    requireActivity(),
                                    "Patients notified via SMS",
                                    Toast.LENGTH_LONG
                                ).show()
                            } else {
                                errorHandler(requireContext(), s)
                            }
                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }
                    }
                })
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onDown(motionEvent: MotionEvent): Boolean {
        changeMode()
        return true
    }

    override fun onShowPress(motionEvent: MotionEvent) {}
    override fun onSingleTapUp(motionEvent: MotionEvent): Boolean {
        return false
    }

    override fun onScroll(
        p0: MotionEvent?,
        motionEvent: MotionEvent,
        v: Float,
        v1: Float
    ): Boolean {
        return false
    }

    override fun onLongPress(motionEvent: MotionEvent) {}
    override fun onFling(
        p0: MotionEvent?,
        e1: MotionEvent,
        velocityX: Float,
        velocityY: Float
    ): Boolean {
        return false
    }

    fun getAppointmentDetails(
        duration: String?,
        mode: String?,
        search: String?,
        sort: String?,
        sortBy: String?,
        pageNum: String,
        perPage: String?,
        type: String,
        apptType: String?,
        productId: String?,
        checkInStatus: String?,
        payment: String?,
        from: String,
        dateFilter: String?,
    ) {
        Log.i("form", from)
        if (globalClass.isOnline) {
            if (swipeContainer!!.isRefreshing) {
                progressBar!!.visibility = View.GONE
            } else {
                progressBar!!.visibility = View.VISIBLE
            }
            appointmentViewModel.getAppointmentDetails(
                requireActivity(),
                duration!!,
                mode!!,
                search!!,
                sort!!,
                sortBy!!,
                pageNum,
                perPage!!,
                type,
                apptType!!,
                checkInStatus!!,
                payment!!,
                productId!!,
                dateFilter!!
            ).observe(
                viewLifecycleOwner, object : Observer<String> {
                    override fun onChanged(s: String) {
                        loading = false
                        Log.i("appt", s)
                        if (swipeContainer!!.isRefreshing) {
                            swipeContainer!!.isRefreshing = false
                        }
                        if (pageNum.equals("1", ignoreCase = true)) {
                            appointmentModelList.clear()
                            dummyDate = "Dec 12, 2001"
                            c = 0
                            groupData.clear()
                        }
                        try {
                            val response = JSONObject(s)
                            if (response.getInt("status_code") == 200) {
                                val responseObject =
                                    response.getJSONObject("response").getJSONObject("response")
                                val current_page = responseObject.optInt("current_page")
                                val last_page = responseObject.optInt("last_page")
                                isListExhausted = last_page == current_page
                                tabName = type
                                if (responseObject.getInt("total") == 0) {
                                    if (from.equals("search", ignoreCase = true)) {
                                        if (currentLayout.contains("main")) {
                                            if (search != null && !search.equals(
                                                    "",
                                                    ignoreCase = true
                                                )
                                            ) {
                                                apptNoPatientText!!.text =
                                                    "No appointment found for the patient '$search'"
                                                bookAppointmentButton!!.visibility = View.GONE
                                                openCalendarView!!.visibility = View.GONE
                                                noPatientLayout!!.visibility = View.GONE
                                                apptNoPatient!!.visibility = View.VISIBLE
                                            } else {
                                                noApptText!!.text =
                                                    "You have no appointments coming"
                                                bookAppointmentButton!!.visibility = View.VISIBLE
                                                openCalendarView!!.visibility = View.VISIBLE
                                                noPatientLayout!!.visibility = View.VISIBLE
                                                apptNoPatient!!.visibility = View.GONE
                                                apptSearchLayout!!.visibility = View.GONE
                                                apptRecycler!!.visibility = View.GONE
                                                addAppoinment!!.visibility = View.GONE
                                            }
                                            bookApptValue = 1
                                            apptRecycler!!.visibility = View.GONE
                                        } else {
                                            apptCalendarRecycler!!.visibility = View.GONE
                                            apptCalendarNoRecord!!.visibility = View.VISIBLE
                                            apptCalendarNoRecordText!!.text =
                                                "No appointment found for the patient '$search'"
                                            apptClearFilter!!.visibility = View.GONE
                                        }
                                    } else if (from.equals("filter", ignoreCase = true)) {
                                        if (currentLayout.contains("main")) {
                                            bookAppointmentButton!!.visibility = View.GONE
                                            openCalendarView!!.visibility = View.GONE
                                            noPatientLayout!!.visibility = View.GONE
                                            apptNoPatient!!.visibility = View.VISIBLE
                                            apptNoPatientText!!.text =
                                                "No appointment found for the given filter"
                                            apptRecycler!!.visibility = View.GONE
                                        } else {
                                            apptCalendarRecycler!!.visibility = View.GONE
                                            apptCalendarNoRecord!!.visibility = View.VISIBLE
                                            apptCalendarNoRecordText!!.text =
                                                "No appointment found for the selected filter"
                                            apptClearFilter!!.visibility = View.VISIBLE
                                        }
                                    } else if (from.equals("serviceFilter", ignoreCase = true)) {
                                        if (currentLayout.contains("main")) {
                                            noApptText!!.text = "You have no appointments coming"
                                            bookAppointmentButton!!.visibility = View.VISIBLE
                                            openCalendarView!!.visibility = View.VISIBLE
                                            noPatientLayout!!.visibility = View.VISIBLE
                                            apptNoPatient!!.visibility = View.GONE
                                            apptRecycler!!.visibility = View.GONE
                                            addAppoinment!!.visibility = View.GONE
                                        } else {
                                            apptCalendarRecycler!!.visibility = View.GONE
                                            apptCalendarNoRecord!!.visibility = View.VISIBLE
                                            apptCalendarNoRecordText!!.text =
                                                "No appointment found for the selected service"
                                            apptClearFilter!!.visibility = View.GONE
                                        }
                                    } else {
                                        if (currentLayout.contains("main")) {
                                            noApptText!!.text = "You have no appointments coming"
                                            bookAppointmentButton!!.visibility = View.VISIBLE
                                            openCalendarView!!.visibility = View.VISIBLE
                                            noPatientLayout!!.visibility = View.VISIBLE
                                            apptNoPatient!!.visibility = View.GONE
                                            apptSearchLayout!!.visibility = View.GONE
                                            apptRecycler!!.visibility = View.GONE
                                            addAppoinment!!.visibility = View.GONE
                                        } else {
                                            apptCalendarRecycler!!.visibility = View.GONE
                                            apptCalendarNoRecord!!.visibility = View.VISIBLE
                                            apptCalendarNoRecordText!!.text =
                                                "No appointment booked"
                                            apptClearFilter!!.visibility = View.GONE
                                        }
                                    }
                                } else {
                                    if (from.equals("search", ignoreCase = true)) {
                                        appointmentModelList.clear()
                                        dummyDate = "Dec 12, 2001"
                                    }
                                    var responseLenght = 0
                                    val appointmentObject = responseObject.getJSONArray("data1")
                                    for (j in 0 until appointmentObject.length()) {
                                        val appointmentJsonObject =
                                            appointmentObject.getJSONObject(j)
                                        val appointmentObjectTwo =
                                            appointmentJsonObject.getJSONArray("appointments")
                                        if (appointmentObjectTwo.length() > 0) {
                                            for (i in 0 until appointmentObjectTwo.length()) {
                                                responseLenght++
                                                groupData.add(c, i)
                                                apptSize.add(c, appointmentObjectTwo.length())
                                                c++
                                                val tempobj = appointmentObjectTwo.getJSONObject(i)
                                                val orderArray = tempobj.getJSONObject("order")
                                                val productObjectArray =
                                                    orderArray.getJSONObject("products")
                                                var invoiceObject: JSONObject? = null
                                                if (!orderArray.isNull("invoice")) {
                                                    invoiceObject =
                                                        orderArray.getJSONObject("invoice")
                                                }
                                                val productServiceObjectArray =
                                                    productObjectArray.getJSONObject("prod_service")
                                                val model = AppointmentModel()
                                                model.rescheduleJsonArray =
                                                    tempobj.optJSONArray("rescheduledAppointment")
                                                model.booked_by =
                                                    tempobj.optString("booked_by_name")
                                                if (!orderArray.isNull("order_user")) {
                                                    val orderUserArray =
                                                        orderArray.getJSONObject("order_user")
                                                    model.apptMode = tempobj.getInt("mode")
                                                    model.patientName =
                                                        orderUserArray.getString("fname")
                                                    /*New Registration(Autogenerated ID) changes for Gastro interface*/
                                                    model.generalID =
                                                        orderUserArray.getString("general_id")
                                                    model.patientId = orderUserArray.getInt("id")
                                                    model.apptTime =
                                                        tempobj.getString("appt_start_time")
                                                    model.groupNo = j
                                                    model.apptDate =
                                                        appointmentJsonObject.getString("date")
                                                    model.clinicName =
                                                        orderArray.optString("product_metadata")
                                                    model.scheduleTime =
                                                        tempobj.getString("scheduled_start_time")
                                                    model.video_tool = tempobj.optInt("video_tool")
                                                    model.doctor_join_external_url =
                                                        tempobj.optString("doctor_join_external_url")
                                                    model.patient_join_external_url =
                                                        tempobj.optString("patient_join_external_url")
                                                    model.is_reschedule_active =
                                                        tempobj.getInt("is_reschedule_active")
                                                    model.sendPaymentNotification =
                                                        orderArray.getInt("send_payment_notification")
                                                    model.appointmentId = tempobj.getInt("id")
                                                    model.paymentStatus =
                                                        orderArray.getString("payment_status")
                                                    model.apptType = tempobj.getInt("appt_type")
                                                    model.productId =
                                                        orderArray.getInt("product_id")
                                                    model.invoiceData = invoiceObject
                                                    if (invoiceObject != null) {
                                                        model.invoiceUrl =
                                                            invoiceObject.optString("public_url")
                                                    }
                                                    //new parameters
                                                    model.refundStatus =
                                                        orderArray.getInt("refund_status")
                                                    model.isRefundProcessed =
                                                        orderArray.getInt("is_refund_processed")
                                                    model.netTotalPaid =
                                                        orderArray.getInt("net_total_paid")
                                                    model.refundAmt =
                                                        orderArray.getInt("refund_amt")
                                                    model.refundStatus =
                                                        orderArray.getInt("refund_status")
                                                    model.isDoAutoRefund =
                                                        orderArray.getInt("is_do_auto_refund")
                                                    model.isSettlementProcessed =
                                                        orderArray.getInt("is_settlement_processed")
                                                    model.isSettlementTriggered =
                                                        orderArray.getInt("is_settlement_triggered")
                                                    model.refundScheduledOn =
                                                        orderArray.getString("refund_schedule")
                                                    model.unmapped_status =
                                                        orderArray.getString("unmapped_status")
                                                    model.payment_title =
                                                        orderArray.getString("payment_title")
                                                    model.payment_title_color =
                                                        orderArray.getString("payment_title_color")
                                                    model.patientPhoneNumber =
                                                        orderUserArray.optString("phone")
                                                    model.internalSaas_id =
                                                        productServiceObjectArray.optInt("internal_supersaas_id")
                                                    val intervention = orderArray["receipt"]
                                                    if (intervention is JSONArray) {
                                                        // It's an array
                                                        // model.setPhNo(patientObject.getString("phone"));
                                                    } else if (intervention is JSONObject) {
                                                        // It's an object
                                                        val receiptObject =
                                                            orderArray.getJSONObject("receipt")
                                                        model.receiptUrl =
                                                            receiptObject.getString("public_url")
                                                    } else {
                                                        model.receiptUrl = ""
                                                        // It's something else, like a string or number
                                                    }
                                                    model.orderId = orderArray.getInt("id")
                                                    model.orderUserId = orderUserArray.getInt("id")
                                                    model.activePast = 1
                                                    if (type.equals("past", ignoreCase = true)) {
                                                        model.activePast = 2
                                                    }
                                                    model.apptStatus = tempobj.getInt("status")
                                                    appointmentModelList.add(model)
                                                } else {
                                                    //added on 2-7-2020
                                                    model.apptMode = tempobj.getInt("mode")
                                                    model.patientName = ""
                                                    model.patientId = 0
                                                    model.apptTime =
                                                        tempobj.getString("appt_start_time")
                                                    model.groupNo = j
                                                    model.apptDate =
                                                        appointmentJsonObject.getString("date")
                                                    model.clinicName =
                                                        productServiceObjectArray.getString("alias")
                                                    model.scheduleTime =
                                                        tempobj.getString("scheduled_start_time")
                                                    model.video_tool = tempobj.optInt("video_tool")
                                                    model.doctor_join_external_url =
                                                        tempobj.optString("doctor_join_external_url")
                                                    model.patient_join_external_url =
                                                        tempobj.optString("patient_join_external_url")
                                                    model.is_reschedule_active =
                                                        tempobj.getInt("is_reschedule_active")
                                                    model.sendPaymentNotification =
                                                        orderArray.getInt("send_payment_notification")
                                                    model.appointmentId = tempobj.getInt("id")
                                                    model.paymentStatus =
                                                        orderArray.getString("payment_status")
                                                    model.apptType = tempobj.getInt("appt_type")
                                                    model.productId =
                                                        orderArray.getInt("product_id")
                                                    model.invoiceData = invoiceObject

                                                    //new parameters
                                                    model.refundStatus =
                                                        orderArray.getInt("refund_status")
                                                    model.isRefundProcessed =
                                                        orderArray.getInt("is_refund_processed")
                                                    model.netTotalPaid =
                                                        orderArray.getInt("net_total_paid")
                                                    model.refundAmt =
                                                        orderArray.getInt("refund_amt")
                                                    model.refundStatus =
                                                        orderArray.getInt("refund_status")
                                                    model.isDoAutoRefund =
                                                        orderArray.getInt("is_do_auto_refund")
                                                    model.isSettlementProcessed =
                                                        orderArray.getInt("is_settlement_processed")
                                                    model.isSettlementTriggered =
                                                        orderArray.getInt("is_settlement_triggered")
                                                    model.refundScheduledOn =
                                                        orderArray.getString("refund_schedule")
                                                    model.unmapped_status =
                                                        orderArray.getString("unmapped_status")
                                                    model.payment_title =
                                                        orderArray.getString("payment_title")
                                                    model.payment_title_color =
                                                        orderArray.getString("payment_title_color")
                                                    val intervention = orderArray["receipt"]
                                                    if (intervention is JSONArray) {
                                                        // It's an array
                                                        // model.setPhNo(patientObject.getString("phone"));
                                                    } else if (intervention is JSONObject) {
                                                        // It's an object
                                                        val receiptObject =
                                                            orderArray.getJSONObject("receipt")
                                                        model.receiptUrl =
                                                            receiptObject.getString("public_url")
                                                    } else {
                                                        model.receiptUrl = ""
                                                    }
                                                    model.orderId = orderArray.getInt("id")
                                                    model.orderUserId = 0
                                                    model.activePast = 1
                                                    if (type.equals("past", ignoreCase = true)) {
                                                        model.activePast = 2
                                                    }
                                                    model.apptStatus = tempobj.getInt("status")
                                                    appointmentModelList.add(model)
                                                }
                                            }
                                            isMoreData =
                                                appointmentModelList.size < responseObject.getInt(
                                                    "total"
                                                )
                                        }
                                    }
                                    /* if (responseLenght >= Integer.valueOf(perPageString) || appointmentModelList.size >= 20) {
                                         appointmentModelList.add(AppointmentModel())
                                     }*/
                                    if (currentLayout.contains("calendar")) {
                                        appointmentCalendarListAdapter.notifyDataSetChanged()
                                        apptCalendarRecycler!!.visibility = View.VISIBLE
                                        apptCalendarNoRecord!!.visibility = View.GONE
                                        try {
                                            val sdf = SimpleDateFormat("yyyy-MM-dd")
                                            if (sdf.parse(currentDate)
                                                    .after(sdf.parse(selectedDate))
                                            ) {
                                                apptCalendarBookAppt!!.visibility = View.GONE
                                                addAppoinment!!.visibility = View.GONE
                                            } else if (sdf.parse(currentDate) == sdf.parse(
                                                    selectedDate
                                                )
                                            ) {
                                                apptCalendarBookAppt!!.visibility = View.VISIBLE
                                                addAppoinment!!.visibility = View.VISIBLE
                                            } else {
                                                apptCalendarBookAppt!!.visibility = View.VISIBLE
                                                addAppoinment!!.visibility = View.VISIBLE
                                            }
                                        } catch (e: Exception) {
                                            e.printStackTrace()
                                        }
                                    } else {
                                        appointmentListAdapter.notifyDataSetChanged()
                                        noPatientLayout!!.visibility = View.GONE
                                        apptSearchLayout!!.visibility = View.VISIBLE
                                        apptRecycler!!.visibility = View.VISIBLE
                                        addAppoinment!!.visibility = View.VISIBLE
                                        apptNoPatient!!.visibility = View.GONE
                                    }
                                }
                                if (currentLayout.contains("calendar")) {
                                    if (appointBookRescheduleFromCalDate != null) {
                                        if (!apptDates.contains(appointBookRescheduleFromCalDate)) {
                                            apptDates.add(appointBookRescheduleFromCalDate!!)
                                            calendarView!!.notifyCalendarChanged()
                                        }
                                        if (appointmentModelList.size <= 0) {
                                            if (apptDates.contains(selectedDated)) {
                                                apptDates.remove(selectedDated)
                                                calendarView!!.notifyCalendarChanged()
                                            }
                                        }
                                        appointBookRescheduleFromCalDate = null
                                    }
                                    if (appointBookFromCal) {
                                        allAppointsDates
                                        appointBookFromCal = false
                                    }
                                }
                            } else {
                                errorHandler(requireContext(), s)
                            }
                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }
                        progressBar!!.visibility = View.GONE
                    }
                })
        } else {
            globalClass.noInternetConnection.showDialog(requireActivity())
        }
    }

    fun joinVideo(appointmentModel: AppointmentModel) {
        if (appointmentModel.paymentStatus.equals("Pending", ignoreCase = true)) {
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
                if (appointmentModel.video_tool == 1) {
                    telephonyManager =
                        requireActivity().getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
                    callStateListener = object : PhoneStateListener() {
                        override fun onCallStateChanged(state: Int, incomingNumber: String) {
                            if (state == TelephonyManager.CALL_STATE_RINGING) {
                                callCurrentState = 1
                                Toast.makeText(
                                    requireActivity().applicationContext,
                                    "You can't place a video call if you're already on a phone call.",
                                    Toast.LENGTH_LONG
                                ).show()
                            } else if (state == TelephonyManager.CALL_STATE_OFFHOOK) {
                                callCurrentState = 2
                                Toast.makeText(
                                    requireActivity().applicationContext,
                                    "You can't place a video call if you're already on a phone call.",
                                    Toast.LENGTH_LONG
                                ).show()
                            } else {
                                if (callCurrentState == 1 || callCurrentState == 2) {
                                    callCurrentState = 0
                                } else {
                                    if (globalClass.isOnline) {
                                        val intent =
                                            Intent(
                                                requireActivity(),
                                                VideoScreenActivity::class.java
                                            )
                                        intent.putExtra(
                                            "AppointmentId",
                                            appointmentModel.appointmentId
                                        )
                                        requireActivity().startActivity(intent)
                                    } else {
                                        globalClass.noInternetConnection.showDialog(requireActivity())
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
                            telephonyManager.listen(
                                callStateListener,
                                PhoneStateListener.LISTEN_CALL_STATE
                            )
                        }
                    } else {
                        telephonyManager.listen(
                            callStateListener,
                            PhoneStateListener.LISTEN_CALL_STATE
                        )
                    }
                } else {
                    openLinkInBrowser(appointmentModel.doctor_join_external_url, requireActivity())
                }
            }
            sendSMSReminderLayout.setOnClickListener {
                dialog.dismiss()
                dashboardViewModel.sendPaymentReminderDetails(
                    requireActivity(),
                    appointmentModel.appointmentId
                ).observe(
                    (requireActivity() as LifecycleOwner), object : Observer<String> {
                        override fun onChanged(s: String) {
                            try {
                                val jsonObject = JSONObject(s)
                                if (jsonObject.getInt("status_code") == 200) {
                                    val response = JSONObject(s).getJSONObject("response")
                                    val responseValue = response.getInt("response")
                                    if (responseValue == 1) {
                                        Toast.makeText(
                                            requireActivity(),
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
                    })
            }
            dialog.show()
        } else {
            if (appointmentModel.video_tool == 1) {
                telephonyManager =
                    requireActivity().getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
                callStateListener = object : PhoneStateListener() {
                    override fun onCallStateChanged(state: Int, incomingNumber: String) {
                        if (state == TelephonyManager.CALL_STATE_RINGING) {
                            callCurrentState = 1
                            Toast.makeText(
                                requireActivity().applicationContext,
                                "You can't place a video call if you're already on a phone call.",
                                Toast.LENGTH_LONG
                            ).show()
                        } else if (state == TelephonyManager.CALL_STATE_OFFHOOK) {
                            callCurrentState = 2
                            Toast.makeText(
                                requireActivity().applicationContext,
                                "You can't place a video call if you're already on a phone call.",
                                Toast.LENGTH_LONG
                            ).show()
                        } else {
                            if (callCurrentState == 1 || callCurrentState == 2) {
                                callCurrentState = 0
                            } else {
                                if (globalClass.isOnline) {
                                    val intent =
                                        Intent(requireActivity(), VideoScreenActivity::class.java)
                                    intent.putExtra("AppointmentId", appointmentModel.appointmentId)
                                    requireActivity().startActivity(intent)
                                } else {
                                    globalClass.noInternetConnection.showDialog(requireActivity())
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
                        telephonyManager.listen(
                            callStateListener,
                            PhoneStateListener.LISTEN_CALL_STATE
                        )
                    }
                } else {
                    telephonyManager.listen(
                        callStateListener,
                        PhoneStateListener.LISTEN_CALL_STATE
                    )
                }
            } else {
                openLinkInBrowser(appointmentModel.doctor_join_external_url, requireActivity())
            }
        }
    }

    private fun cancelRefund(order_id: Int, dialog: Dialog) {
        val url = ApiUrls.cancelRefund + "?order_id=" + order_id
        appointmentApiRequests.getApptApiData(url, "", requireActivity(), object : VolleyCallback {
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
                errorHandler(requireActivity(), err)
            }
        })
    }

    private fun cancelApptSingle(apptID: Int, status: Int, dialog: Dialog, activity: Activity?) {
        val reqObj = JSONObject()
        try {
            reqObj.put("appId", apptID)
            reqObj.put("cancel", status)
            if (rbYesNo!!.text.toString().equals("No", ignoreCase = true)) {
                reqObj.put("refund_amount", 0)
            } else {
                reqObj.put("refund_amount",
                    et_refund_amount!!.text.toString().trim { it <= ' ' }.toInt()
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        appointmentApiRequests.postApptApiData(ApiUrls.cancelApptCon,
            reqObj,
            activity,
            object : VolleyCallback {
                override fun onSuccess(result: String) {
                    try {
                        val resObj = JSONObject(result)
                        if (resObj.getString("response").equals("success", ignoreCase = true)) {
                            Toast.makeText(
                                context,
                                resources.getString(R.string.appt_cancel),
                                Toast.LENGTH_SHORT
                            ).show()
                            val cancelledAppts: List<AppointmentModel> = ArrayList()
                            for (i in appointmentModelList.indices) {
                                val model = appointmentModelList[i]
                                if (model.appointmentId == apptID) {
                                    appointmentModelList.remove(model)

//                                groupData.remove(i);
                                    if (groupData[i] == 0) {
                                        if (groupData[i + 1] > 0) {
                                            groupData.removeAt(i + 1)
                                        } else {
                                            groupData.removeAt(i)
                                        }
                                    } else {
                                        groupData.removeAt(i)
                                    }
                                    break
                                }
                            }
                            if (appointmentModelList.size <= 0) {
                                if (apptDates.contains(selectedDated)) {
                                    apptDates.remove(selectedDated)
                                    calendarView!!.notifyCalendarChanged()
                                }
                            }
                            if (currentLayout.contains("calendar")) {
                                appointmentCalendarListAdapter.notifyDataSetChanged()
                            } else {
                                appointmentListAdapter.notifyDataSetChanged()
                            }
                            //  getServices();
                            refundMainLayout!!.visibility = View.GONE
                            refundMessageLayout!!.visibility = View.VISIBLE
                            messageNoteText!!.text =
                                "Yor Settlement will be initiated in 2-3 days.\n Please make sure you have completed the KYC Verification."
                            if (appointmentModelList.size == 0) {
                                noPatientLayout!!.visibility = View.VISIBLE
                                apptSearchLayout!!.visibility = View.GONE
                                apptRecycler!!.visibility = View.GONE
                                addAppoinment!!.visibility = View.GONE
                                if (currentLayout.contains("calendar")) {
                                    apptCalendarRecycler!!.visibility = View.GONE
                                    apptCalendarNoRecord!!.visibility = View.VISIBLE
                                    apptCalendarNoRecordText!!.text = "No appointment booked"
                                    apptClearFilter!!.visibility = View.GONE
                                }
                            }

                            //  getServices();
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    } finally {
                        for (i in appointmentModelList.indices) {
                            val model = appointmentModelList[i]
                            if (model.appointmentId == apptID) {
                                appointmentModelList.remove(model)
                                groupData.removeAt(i)
                                break
                            }
                        }
                        if (appointmentModelList.size <= 0) {
                            if (apptDates.contains(selectedDated)) {
                                apptDates.remove(selectedDated)
                                calendarView!!.notifyCalendarChanged()
                            }
                        }
                        if (currentLayout.contains("calendar")) {
                            appointmentCalendarListAdapter.notifyDataSetChanged()
                        } else {
                            appointmentListAdapter.notifyDataSetChanged()
                        }
                        refundMainLayout!!.visibility = View.GONE
                        refundMessageLayout!!.visibility = View.VISIBLE
                        messageNoteText!!.text =
                            "Yor Settlement will be initiated in 2-3 days.\n Please make sure you have completed the KYC Verification."
                        if (appointmentModelList.size == 0) {
                            noPatientLayout!!.visibility = View.VISIBLE
                            apptSearchLayout!!.visibility = View.GONE
                            apptRecycler!!.visibility = View.GONE
                            addAppoinment!!.visibility = View.GONE
                            if (currentLayout.contains("calendar")) {
                                apptCalendarRecycler!!.visibility = View.GONE
                                apptCalendarNoRecord!!.visibility = View.VISIBLE
                                apptCalendarNoRecordText!!.text = "No appointment booked"
                                apptClearFilter!!.visibility = View.GONE
                            }
                        }
                    }
                }

                override fun onError(err: String) {
                    errorHandler(requireActivity(), err)
                }
            })
    }

    private fun getRefundAmount(cancelReason: Int, orderId: Int) {
        otpLoading = ProgressDialog(requireActivity())
        otpLoading!!.setMessage(resources.getString(R.string.process_request))
        otpLoading!!.setProgressStyle(ProgressDialog.STYLE_SPINNER)
        otpLoading!!.setCancelable(false)
        otpLoading!!.show()
        try {
            val jsonValue = JSONObject()
            jsonValue.put("order_id", orderId)
            jsonValue.put("cancel_reason", cancelReason)
            appointmentViewModel.appointmentRefundAmount(
                requireActivity(),
                jsonValue,
                orderId,
                cancelReason
            ).observe(
                viewLifecycleOwner, object : Observer<String> {
                    override fun onChanged(s: String) {
                        otpLoading!!.dismiss()
                        try {
                            val jsonObject = JSONObject(s)
                            if (jsonObject.getInt("status_code") == 200) {
                                val response = JSONObject(s).getJSONObject("response")
                                et_refund_amount!!.setText("" + response.getInt("refundAmt"))
                            } else {
                                if (jsonObject.getString("response")
                                        .equals("Invalid status.", ignoreCase = true)
                                ) {
                                } else {
                                    errorHandler(requireContext(), s)
                                }
                            }
                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }
                    }
                })
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun cancelApptRefund(orderId: Int, status: Int, dialog: Dialog) {
        otpLoading = ProgressDialog(requireActivity())
        otpLoading!!.setMessage(resources.getString(R.string.process_request))
        otpLoading!!.setProgressStyle(ProgressDialog.STYLE_SPINNER)
        otpLoading!!.setCancelable(false)
        otpLoading!!.show()
        try {
            val jsonValue = JSONObject()
            jsonValue.put("order_id", orderId)
            jsonValue.put("cancel", status)
            appointmentViewModel.cancelApptRefund(requireActivity(), jsonValue).observe(
                viewLifecycleOwner, object : Observer<String> {
                    override fun onChanged(s: String) {
                        otpLoading!!.dismiss()
                        try {
                            val jsonObject = JSONObject(s)
                            if (jsonObject.getInt("status_code") == 200) {
                                val response = JSONObject(s).getJSONObject("response")
                                Toast.makeText(
                                    requireActivity(),
                                    response.getString("response"),
                                    Toast.LENGTH_LONG
                                ).show()
                                refundMainLayout!!.visibility = View.GONE
                                refundMessageLayout!!.visibility = View.VISIBLE
                                messageNoteText!!.text =
                                    "The refund request has been initiated. it will usually take 2-3 days to complete the transaction. Patient will notified via SMS, Email or Notification."
                            } else {
                                errorHandler(requireContext(), s)
                            }
                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }
                    }
                })
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun cancelApptSingleCalender(apptID: Int, status: Int, dialog: Dialog) {
        val reqObj = JSONObject()
        try {
            reqObj.put("appId", apptID)
            reqObj.put("cancel", status)
            if (rbYesNo!!.text.toString().equals("No", ignoreCase = true)) {
                reqObj.put("refund_amount", 0)
            } else {
                reqObj.put("refund_amount",
                    et_refund_amount!!.text.toString().trim { it <= ' ' }.toInt()
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        appointmentApiRequests.postApptApiData(ApiUrls.cancelAppt,
            reqObj,
            requireActivity(),
            object : VolleyCallback {
                override fun onSuccess(result: String) {
                    try {
                        val resObj = JSONObject(result)
                        if (resObj.getString("response").equals("success", ignoreCase = true)) {
                            Toast.makeText(
                                requireActivity(),
                                resources.getString(R.string.appt_cancel),
                                Toast.LENGTH_SHORT
                            ).show()
                            val cancelledAppts: List<AppointmentModel> = ArrayList()
                            for (i in appointmentModelList.indices) {
                                val model = appointmentModelList[i]
                                if (model.appointmentId == apptID) {
                                    appointmentModelList.remove(model)
                                    groupData.removeAt(i)
                                    break
                                }
                            }
                            appointmentCalendarListAdapter.notifyDataSetChanged()
                            dialog.dismiss()
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                override fun onError(err: String) {}
            })
    }

    fun openLinkInBrowser(url: String, mContext: Context) {
        var url = url
        try {
            if (url.startsWith("http://") || url.startsWith("https://")) {
            } else {
                url = "http://$url"
            }
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            mContext.startActivity(browserIntent)
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(mContext, "Invalid URL", Toast.LENGTH_SHORT).show()
        }
    }

    private fun notifyPatientExternalJoinVideo(appt_id: Int) {
        otpLoading = ProgressDialog(requireActivity())
        otpLoading!!.setMessage(resources.getString(R.string.please_wait))
        otpLoading!!.setProgressStyle(ProgressDialog.STYLE_SPINNER)
        otpLoading!!.setCancelable(false)
        otpLoading!!.show()
        try {
            appointmentViewModel.sendVideoLink(requireActivity(), appt_id)
                .observe(viewLifecycleOwner, object : Observer<String> {
                    override fun onChanged(s: String) {
                        otpLoading!!.dismiss()
                        try {
                            val jsonObject = JSONObject(s)
                            if (jsonObject.getInt("status_code") == 200) {
                                sendVideoLinkPopup()
                            } else {
                                errorHandler(requireContext(), s)
                            }
                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }
                    }
                })
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun sendVideoLinkPopup() {
        val dialog = Dialog(requireActivity())
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.dailog_send_video_link_popup_message)
        val dailogArticleCancel = dialog.findViewById<View>(R.id.dailogArticleCancel) as ImageView
        closeButton = dialog.findViewById<View>(R.id.closeButton) as RelativeLayout
        closeButton!!.setOnClickListener { dialog.dismiss() }
        dailogArticleCancel.setOnClickListener { dialog.dismiss() }
        dialog.show()
    }

    private fun registerReceiverAppointment() {
        broadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                if (intent != null) {
                    if (intent.action == "UPDATE_FRAG_LIST") {
                        // here you can fire your action which you want also get data from intent
                        try {
                            if (currentLayout.contains("calendar")) {
                                if (intent.hasExtra("appointDateInCal")) {
                                    appointBookRescheduleFromCalDate =
                                        LocalDate.parse(intent.getStringExtra("appointDateInCal"))
                                }
                                if (intent.hasExtra("isAppointmentNew")) {
                                    appointBookFromCal =
                                        intent.getBooleanExtra("isAppointmentNew", false)
                                }
                            }

                            //  getServices();
                            appointmentListRefresh()
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    } else if (intent.action == "APPOINTMENT_COMPLETED") {
                        try {
                            val apptId = intent.getIntExtra("apptID", 0)
                            Log.d("onApptCompt", "onApptCompt")
                            for (i in appointmentModelList.indices) {
                                if (appointmentModelList[i].appointmentId == apptId) {
                                    appointmentModelList.removeAt(i)
                                }
                            }
                            val completeAppointDate =
                                LocalDate.parse(intent.getStringExtra("appointmentDate"))
                            if (appointmentModelList.size <= 0) {
                                if (apptDates.contains(completeAppointDate)) {
                                    apptDates.remove(completeAppointDate)
                                    calendarView!!.notifyCalendarChanged()
                                }
                            }
                            if (appointmentModelList.size == 0) {
                                if (currentLayout.contains("main")) {
                                    noApptText!!.text = "You have no appointments coming"
                                    bookAppointmentButton!!.visibility = View.VISIBLE
                                    openCalendarView!!.visibility = View.VISIBLE
                                    noPatientLayout!!.visibility = View.VISIBLE
                                    apptSearchLayout!!.visibility = View.GONE
                                    apptRecycler!!.visibility = View.GONE
                                    addAppoinment!!.visibility = View.GONE
                                } else {
                                    apptCalendarRecycler!!.visibility = View.GONE
                                    apptCalendarNoRecord!!.visibility = View.VISIBLE
                                    apptCalendarNoRecordText!!.text =
                                        "No appointment found for the selected service"
                                    apptClearFilter!!.visibility = View.GONE
                                }
                            }
                            appointmentListAdapter.notifyDataSetChanged()
                            appointmentCalendarListAdapter.notifyDataSetChanged()
                            //  getServices();
                        } catch (e: Exception) {
                        }
                    } else if (intent.action == "PAYMENT_STATUS_FLAG") {
                        try {
                            val apptId = intent.getIntExtra("apptID", 0)
                            Log.d("onPaymentStatusenter", "onPaymentStatusenter")
                            Log.d("onPaymentId", "onPaymentId" + ApptDetailsFragment.apptId)
                            for (i in appointmentModelList.indices) {
                                if (appointmentModelList[i].appointmentId == apptId) {
                                    appointmentModelList[i].paymentStatus = "success"
                                    appointmentModelList[i].apptType =
                                        intent.getIntExtra("apptType", 0)
                                }
                            }
                            appointmentListAdapter.notifyDataSetChanged()
                            appointmentCalendarListAdapter.notifyDataSetChanged()
                        } catch (e: Exception) {
                        }
                    } else if (intent.action == "APPOINTMENT_TYPE_STATUS") {
                        try {
                            val apptId = intent.getIntExtra("apptID", 0)
                            Log.d("onPaymentId", "onPaymentId" + ApptDetailsFragment.apptId)
                            for (i in appointmentModelList.indices) {
                                if (appointmentModelList[i].appointmentId == apptId) {
                                    appointmentModelList[i].apptType =
                                        intent.getIntExtra("apptType", 0)
                                }
                            }
                            appointmentListAdapter.notifyDataSetChanged()
                            appointmentCalendarListAdapter.notifyDataSetChanged()
                        } catch (e: Exception) {
                        }
                    } else if (intent.action == "CREATE_INVOICE") {
                        try {
                            val apptId = intent.getIntExtra("apptID", 0)
                            Log.d("onCreateInvoice", "onCreateInvoice$apptId")
                            for (i in appointmentModelList.indices) {
                                if (appointmentModelList[i].appointmentId == apptId) {
                                    appointmentModelList[i].invoiceUrl =
                                        intent.getStringExtra("invoiceURL")
                                }
                            }
                            appointmentListAdapter.notifyDataSetChanged()
                            appointmentCalendarListAdapter.notifyDataSetChanged()
                        } catch (e: Exception) {
                        }
                    }
                }
            }
        }
        intentFilter = IntentFilter()
        intentFilter.addAction("UPDATE_FRAG_LIST")
        intentFilter.addAction("APPOINTMENT_COMPLETED")
        intentFilter.addAction("APPOINTMENT_TYPE_STATUS")
        intentFilter.addAction("PAYMENT_STATUS_FLAG")
        intentFilter.addAction("CREATE_INVOICE")
        requireActivity().registerReceiver(broadcastReceiver, intentFilter)
    }


    override fun setMenuVisibility(isVisibleToUser: Boolean) {
        super.setMenuVisibility(isVisibleToUser)
        if (isVisibleToUser) {
            /*if (sharedPref.getPref("is_show_general_id", "").equals("1", ignoreCase = true)) {
                apptSearchPatient!!.hint = getString(R.string.patient_search_text_withID)
                apptCalendarSearchPatient!!.hint = getString(R.string.patient_search_text_withID)
            }*/
            // do something when visible.
            Log.d("TAG", "setUserVisibleAppointment:")
            if (DashboardFullMode.isAppointmentListRefreshReq) {
                DashboardFullMode.isAppointmentListRefreshReq = false
                appointmentListRefresh()
            } else if (DashboardFullMode.isFollowUpListRefreshReq) {
                DashboardFullMode.isFollowUpListRefreshReq = false
                appointmentListRefresh()
            }
        }
    }

    fun appointmentListRefresh() {
        if (ApiUrls.activePastFilterFlag.equals("past", ignoreCase = true)) {
            apptOpenTabView!!.visibility = View.GONE
            apptClosedTabView!!.visibility = View.VISIBLE
            openText!!.setTextColor(Color.parseColor("#6C6C6D"))
            closedText!!.setTextColor(Color.parseColor("#00A65A"))
            activePastFilter = "past"
            sort = "desc"
        } else {
            apptOpenTabView!!.visibility = View.VISIBLE
            apptClosedTabView!!.visibility = View.GONE
            openText!!.setTextColor(Color.parseColor("#00A65A"))
            closedText!!.setTextColor(Color.parseColor("#6C6C6D"))
            activePastFilter = "active"
            sort = "asc"
        }
        isListExhausted = false
        c = 0
        groupData.clear()
        ApiUrls.activePastFilterFlag = activePastFilter
        pageNumber = 1
        appointmentModelList.clear()
        dummyDate = "Dec 12, 2001"
        appointmentListAdapter.notifyDataSetChanged()
        from = "main"
        durationFilter = if (currentLayout.contains("calendar")) {
            "Specific"
        } else {
            "All"
        }
        getAppointmentDetails(
            durationFilter,
            modeFilter,
            searchFilterText.toString(),
            sort,
            sortBy,
            pageNumber.toString() + "",
            perPageString,
            activePastFilter,
            consultFilter,
            productId,
            checkInStatus,
            payment,
            "main",
            selectedDated.toString()
        )
    }

    private val monthFirstLastDates: Unit
        private get() {
            val iYear = 2022
            val iMonth = Calendar.JULY
            val iDay = 1
            val today = LocalDate.now()
            val d = today.withDayOfMonth(1)
            val d1 = today.withDayOfMonth(today.lengthOfMonth())
            Log.i("", "" + d + d1)
            val calendar = Calendar.getInstance()
            calendar[2022, Calendar.JUNE] = 1
            val monthFirstDay = calendar.time
            calendar[2022, Calendar.JUNE] = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
            val monthLastDay = calendar.time
            val df = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val startDateStr = df.format(monthFirstDay)
            val endDateStr = df.format(monthLastDay)
            Log.e("DateFirstLast", "$startDateStr $endDateStr")
            println("First day: " + today.withDayOfMonth(1))
            println("Last day: " + today.withDayOfMonth(today.lengthOfMonth()))
        }

    override fun onTouch(view: View, motionEvent: MotionEvent): Boolean {
        return gestureDetector.onTouchEvent(motionEvent)
    }

    private val allAppointsDates: Unit
        private get() {
            val getAllDateURL =
                ApiUrls.getAllAppointDates + "?duration=" + "All" + "&mode=" + modeFilter + "&search=" + searchFilterText.toString() + "&sort=" + sort + "&sortBy=" + sortBy +
                        "&type=" + activePastFilter + "&appt_type=" + consultFilter + "&checkinStatus=" + checkInStatus + "&payment=" + payment + "&product_ids=" + productId
            settingsViewModel.getAppointmentsDatesList(getAllDateURL)
            settingsViewModel.appointDatesList.observe(viewLifecycleOwner,
                object : Observer<UtilsResource<AppointDates>> {
                    override fun onChanged(dates: UtilsResource<AppointDates>) {
                        if (dates.status === UtilsResource.Status.SUCCESS) {
                            apptDates.clear()
                            if (dates.data != null) {
                                if (dates.data.response != null && dates.data.response.size > 0) {
                                    for (i in dates.data.response.indices) {
                                        try {
                                            apptDates.add(LocalDate.parse(dates.data.response[i]))
                                            calendarView!!.notifyCalendarChanged()
                                        } catch (e: Exception) {
                                            e.printStackTrace()
                                        }
                                    }
                                } else {
                                    apptDates.clear()
                                    calendarView!!.notifyCalendarChanged()
                                }
                            } else {
                                calendarView!!.notifyCalendarChanged()
                            }
                        } else if (dates.status === UtilsResource.Status.ERROR) {
                            if (dates.message != null) {
                                errorHandler(requireContext(), dates.message)
                            }
                        }
                    }
                })
        }
}
