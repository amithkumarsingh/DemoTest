package com.whitecoats.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PorterDuff
import android.os.Build
import android.os.Bundle
import android.telephony.PhoneStateListener
import android.telephony.TelephonyManager
import android.text.TextUtils
import android.util.Log
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts.RequestPermission
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.LayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.android.volley.Request
import com.google.android.material.bottomnavigation.BottomNavigationItemView
import com.google.android.material.bottomnavigation.BottomNavigationMenuView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.whitecoats.adapter.AppointmentApptListAdapter
import com.whitecoats.broadcast.AppointmentBroadcastReceiver
import com.whitecoats.clinicplus.*
import com.whitecoats.clinicplus.activities.VideoScreenActivity
import com.whitecoats.clinicplus.apis.ApiGetPostMethodCalls
import com.whitecoats.clinicplus.apis.ApiUrls
import com.whitecoats.clinicplus.casechannels.AddDoctorsOrganisationsActivity
import com.whitecoats.clinicplus.utils.ErrorHandlerClass.errorHandler
import com.whitecoats.clinicplus.utils.ShowAlertDialog
import com.whitecoats.clinicplus.viewmodels.DashboardViewModel
import com.whitecoats.fragments.PatientFragment.Companion.patientTabFlag
import com.whitecoats.model.AppointmentApptListModel
import com.whitecoats.model.PatientRecordsModel
import com.zoho.salesiqembed.ZohoSalesIQ
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.lang.Integer.toString
import java.util.*

class AppointmentFragment : Fragment() {
    private lateinit var apptView: View
    private lateinit var apptRecycleView: RecyclerView
    lateinit var appointmentApptListModelList: List<AppointmentApptListModel>
    private lateinit var appointmentApptListAdapter: AppointmentApptListAdapter
    private lateinit var activeTab: RelativeLayout
    private lateinit var pastTab: RelativeLayout
    private lateinit var activeTabBottom: View
    private lateinit var pastTabBottom: View
    private lateinit var swipeContainer: SwipeRefreshLayout
    private lateinit var loader: ProgressBar
    private val userID = 0
    private lateinit var appDatabaseManager: AppDatabaseManager
    private lateinit var otpLoading: ProgressDialog
    private lateinit var jsonValue: JSONObject

    @JvmField
    var durationFilter = "All"

    @JvmField
    var consultFilter: String = "All"

    @JvmField
    var apptTypeFilter: String = "All"

    @JvmField
    var activePastFilter: String = "active"

    @JvmField
    var isFilterApplied = false

    var searchFilter: String = ""
    var sort = "asc"
    var sortBy: String = "scheduled_start_time"
    private lateinit var emptyText: TextView
    private lateinit var mLayoutManager: LayoutManager
    var assignCategoryData = ""
    var appointmentLength = 0
    private lateinit var searchView: SearchView
    private lateinit var toolbar: Toolbar
    private lateinit var groupData: ArrayList<Int>
    var c = 0
    private lateinit var appointmentApiRequests: AppointmentApiRequests
    private lateinit var loadingDialog: ProgressDialog
    private lateinit var bookAppt: FloatingActionButton
    private lateinit var notificationBadge: View
    private lateinit var AppointmentFilterBottomSheetDialogFragment: AppointmentFilterBottomSheet
    private lateinit var episodeList: List<String>
    private var episodeId = 0
    private lateinit var episodeModels: List<PatientRecordsModel>
    private lateinit var dataAdapter: ArrayAdapter<String>
    private lateinit var apiCalls: PatientRecordsApi
    private lateinit var durationGroup: RadioGroup
    private lateinit var apiCall: PatientRecordsApi
    private var followUpDefaultSwitchClick = false
    private var invoiceGenerateSwitchClick = false
    private lateinit var appointmentBroadcastReceiver: AppointmentBroadcastReceiver
    private lateinit var appUtilities: AppUtilities
    var durationClicked = false
    var perPageString = "50"
    private lateinit var apptIdArr: JSONArray
    private lateinit var transView: RelativeLayout
    private lateinit var getPostMethodCalls: ApiGetPostMethodCalls
    private lateinit var telephonyManager: TelephonyManager
    private lateinit var callStateListener: PhoneStateListener
    private var callCurrentState = 0
    lateinit var globalClass: MyClinicGlobalClass
    private lateinit var dashboardViewModel: DashboardViewModel
    private lateinit var requestPermissionLauncher: ActivityResultLauncher<String>


    companion object {
        var perPage = 10
        var pageNumber = 1
        var isMoreData = false

        @SuppressLint("StaticFieldLeak")
        lateinit var all: RadioButton

        @SuppressLint("StaticFieldLeak")
        lateinit var today: RadioButton

        @SuppressLint("StaticFieldLeak")
        lateinit var week: RadioButton

        @SuppressLint("StaticFieldLeak")
        lateinit var month: RadioButton

        const val CUSTOM_BROADCAST_ACTION = "AppointmentBroadcastReceiver"
        var isStatusClicked = false
        var lastHeaderDate = ""


        var appointmentTabFlag = 0

        fun newInstance(): AppointmentFragment {
            return AppointmentFragment()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {

        apptView = inflater.inflate(R.layout.fragment_appointment, container, false)

        //getting the toolbar
        toolbar = apptView.findViewById(R.id.apptToolbar)
        toolbar.title = "My Appointment"
        toolbar.subtitle = "Total: 0"
        appointmentTabFlag = 1
        ChatFragment.chatTabFlag = 0
        patientTabFlag = 0
        (activity as AppCompatActivity).setSupportActionBar(toolbar)
        BookAppointmentActivity.quickAppointmentFlag = 0
        apptRecycleView = apptView.findViewById(R.id.apptRecycleView)
        appointmentApptListModelList = ArrayList()
        appDatabaseManager = AppDatabaseManager(activity)
        apiCall = PatientRecordsApi()
        appUtilities = AppUtilities()
        getPostMethodCalls = ApiGetPostMethodCalls()
        globalClass = requireActivity().applicationContext as MyClinicGlobalClass
        dashboardViewModel = ViewModelProvider((activity as ViewModelStoreOwner)).get(
            DashboardViewModel::class.java)
        dashboardViewModel.init()
        activeTab = apptView.findViewById(R.id.appointActiveTab)
        pastTab = apptView.findViewById(R.id.appointPastTab)
        activeTabBottom = apptView.findViewById(R.id.appointTabActiveBottom)
        pastTabBottom = apptView.findViewById(R.id.appointTabPastBottom)
        swipeContainer = apptView.findViewById(R.id.appointmentSwipeContainer)
        mLayoutManager = LinearLayoutManager(requireActivity().applicationContext)
        loader = apptView.findViewById(R.id.appointLoader)
        loader.visibility = View.VISIBLE
        emptyText = apptView.findViewById(R.id.appointEmptyText)
        bookAppt = apptView.findViewById(R.id.appointBookAppt)
        transView = apptView.findViewById(R.id.transView)
        groupData = ArrayList()
        appointmentApiRequests = AppointmentApiRequests()
        appointmentBroadcastReceiver = AppointmentBroadcastReceiver()
        episodeList = ArrayList()
        episodeModels = ArrayList()
        apiCalls = PatientRecordsApi()

        dataAdapter =
            ArrayAdapter(requireActivity(), android.R.layout.simple_spinner_item, episodeList)
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        //bottom setting type
        ApiUrls.bottomNaviType = 2
        ZohoSalesIQ.Tracking.setPageTitle("On Appointment List Page")

        //filter
        durationGroup = apptView.findViewById(R.id.apptFilterDurationGroup)
        all = apptView.findViewById(R.id.apptFilterDurationAll)
        today = apptView.findViewById(R.id.apptFilterDurationToday)
        week = apptView.findViewById(R.id.apptFilterDurationWeek)
        month = apptView.findViewById(R.id.apptFilterDurationMonth)

        //register broadcast
        val filter = IntentFilter()
        filter.addAction(CUSTOM_BROADCAST_ACTION)
        filter.addCategory(Intent.CATEGORY_DEFAULT)
        requireActivity().registerReceiver(appointmentBroadcastReceiver, filter)

        requestPermissionLauncher = registerForActivityResult(
            RequestPermission()
        ) { result: Boolean ->
            if (result) {
                // PERMISSION GRANTED
                telephonyManager.listen(callStateListener, PhoneStateListener.LISTEN_CALL_STATE)
            } else {
                ShowAlertDialog().showPopupToMovePermissionPage(requireActivity())
            }
        }

        appointmentApptListAdapter =
            AppointmentApptListAdapter(activity, appointmentApptListModelList,
                activity, groupData
            ) { v, loadMore, appointmentApptListModel ->
                if (loadMore.equals("LOADMORE", ignoreCase = true)) {
                    appointmentApptListModelList.toMutableList()
                        .removeAt(appointmentApptListModelList.size - 1)
                    pageNumber += 1
                    loader.visibility = View.VISIBLE
                    if (activeTabBottom.visibility == View.VISIBLE) {
                        activePastFilter = "active"
                        sort = "asc"
                    } else {
                        activePastFilter = "past"
                        sort = "desc"
                    }
                    getAppointmentList(durationFilter,
                        consultFilter,
                        searchFilter,
                        sort,
                        sortBy,
                        pageNumber.toString() + "",
                        perPageString,
                        activePastFilter,
                        "0",
                        userID.toString(),
                        "No Records Found.")
                } else if (loadMore.equals("ShowAttendDialog", ignoreCase = true)) {
                    getAttendanceStatus(appointmentApptListModel.apptDate + "+" + appointmentApptListModel.apptTime,
                        appointmentApptListModel)
                } else if (loadMore.equals("CancelAppt", ignoreCase = true)) {
                    openAttendDialog(appointmentApptListModel.appointmentId, 0, 1)
                } else if (loadMore.contains("CancelAll")) {
                    val builder = AlertDialog.Builder(activity)
                    builder.setTitle("Cancel Confirm")
                    builder.setMessage("Do you want cancel appointment?")
                    builder.setPositiveButton("YES") { dialog, _ ->
                        ZohoSalesIQ.Tracking.setCustomAction("ApptList - Cancel All Appt - Yes")
                        cancelAllAppt(appointmentApptListModel.apptDate)
                        dialog.dismiss()
                    }
                    builder.setNegativeButton("NO") { dialog, _ -> // Do nothing
                        ZohoSalesIQ.Tracking.setCustomAction("ApptList - Cancel All Appt - No")
                        dialog.dismiss()
                    }
                    val alert = builder.create()
                    alert.show()

                } else if (loadMore.contains("IamLate")) {
                    try {
                        apptIdArr = JSONArray()
                        for (i in appointmentApptListModelList.indices) {
                            val model = appointmentApptListModelList[i]
                            if (model.apptDate.equals(appointmentApptListModel.apptDate,
                                    ignoreCase = true) && model.apptMode == 3
                            ) {
                                apptIdArr.put(model.appointmentId)
                            }
                        }
                    } catch (e: java.lang.Exception) {
                        e.printStackTrace()
                    }
                    if (apptIdArr.length() > 0) {
                        iamLatePopup(appointmentApptListModel.apptDate)
                    } else {
                        Toast.makeText(activity,
                            "You do not have clinic appointments to send delay intimation",
                            Toast.LENGTH_LONG).show()
                    }
                } else if (loadMore.contains("Complete")) {
                    val builder = AlertDialog.Builder(activity)
                    val inflater = requireActivity().layoutInflater
                    val inflator = inflater.inflate(R.layout.bulk_complete_popup, null)
                    val followUpSwitch = inflator.findViewById<View>(R.id.followUpSwitch) as Switch
                    val invoiceGenerateSwitch =
                        inflator.findViewById<View>(R.id.generateInvoiceSwitch) as Switch
                    val dailogArticleCancel =
                        inflator.findViewById<View>(R.id.dailogArticleCancel) as ImageView
                    followUpSwitch.setOnCheckedChangeListener { buttonView, isChecked -> // do something, the isChecked will be
                        // true if the switch is clinicplus the On position
                        followUpDefaultSwitchClick = if (isChecked) {
                            isChecked
                        } else {
                            isChecked
                        }
                    }
                    invoiceGenerateSwitch.setOnCheckedChangeListener { buttonView, isChecked -> // do something, the isChecked will be
                        // true if the switch is clinicplus the On position
                        invoiceGenerateSwitchClick = if (isChecked) {
                            isChecked
                        } else {
                            isChecked
                        }
                    }
                    getDefaultFollowUpSetting(followUpSwitch)

                    builder.setView(inflator)
                        .setPositiveButton(R.string.complete_appointments
                        ) { dialog, id ->
                            ZohoSalesIQ.Tracking.setCustomAction("ApptList - All Appt Complete - Yes")
                            completeBulkAppointment(appointmentApptListModel.apptDate,
                                followUpDefaultSwitchClick,
                                invoiceGenerateSwitchClick)

                        }
                        .setNegativeButton(R.string.dismiss
                        ) { dialog, _ ->
                            ZohoSalesIQ.Tracking.setCustomAction("ApptList - All Appt Complete - No")
                            dialog.dismiss()
                        }
                    val alertDialog = builder.create()
                    dailogArticleCancel.setOnClickListener { alertDialog.dismiss() }
                    alertDialog.show()


                } else if (loadMore.contains("CreateDiscussion")) {
                    episodeList.toMutableList().clear()
                    getEpisodes(appointmentApptListModel.patientId)
                    openCreateDialog(appointmentApptListModel)
                } else if (loadMore.equals("JoinVideoCall", ignoreCase = true)) {
                    if (appointmentApptListModel.paymentStatus.equals("Pending",
                            ignoreCase = true)
                    ) {
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
                                requireActivity().getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
                            callStateListener = object : PhoneStateListener() {
                                override fun onCallStateChanged(
                                    state: Int,
                                    incomingNumber: String,
                                ) {
                                    if (state == TelephonyManager.CALL_STATE_RINGING) {
                                        callCurrentState = 1
                                        Toast.makeText(requireActivity().applicationContext,
                                            "You can't place a video call if you're already on a phone call.",
                                            Toast.LENGTH_LONG).show()
                                    } else if (state == TelephonyManager.CALL_STATE_OFFHOOK) {
                                        callCurrentState = 2
                                        Toast.makeText(requireActivity().applicationContext,
                                            "You can't place a video call if you're already on a phone call.",
                                            Toast.LENGTH_LONG).show()
                                    } else {
                                        if (callCurrentState == 1 || callCurrentState == 2) {
                                            callCurrentState = 0
                                        } else {
                                            if (globalClass.isOnline) {
                                                val intent = Intent(activity,
                                                    VideoScreenActivity::class.java)
                                                intent.putExtra("AppointmentId",
                                                    appointmentApptListModel.appointmentId)
                                                activity!!.startActivity(intent)
                                            } else {
                                                globalClass.noInternetConnection.showDialog(activity)
                                            }
                                        }
                                    }
                                }
                            }
                            if (Build.VERSION.SDK_INT >= 31) {
                                if (requireContext().checkSelfPermission(Manifest.permission.READ_PHONE_STATE)
                                    != PackageManager.PERMISSION_GRANTED
                                ) {
                                    requestPermissionLauncher.launch(Manifest.permission.READ_PHONE_STATE)
                                } else {
                                    telephonyManager.listen(callStateListener,
                                        PhoneStateListener.LISTEN_CALL_STATE)
                                }
                            } else {
                                telephonyManager.listen(callStateListener,
                                    PhoneStateListener.LISTEN_CALL_STATE)
                            }
                        }
                        sendSMSReminderLayout.setOnClickListener {
                            dialog.dismiss()
                            dashboardViewModel.sendPaymentReminderDetails(activity,
                                appointmentApptListModel.appointmentId).observe(
                                (requireActivity() as LifecycleOwner)
                            ) { s ->
                                try {
                                    val jsonObject = JSONObject(s)
                                    if (jsonObject.getInt("status") == 200) {
                                        val response = JSONObject(s).getJSONObject("response")
                                        val responseValue = response.getInt("response")
                                        if (responseValue == 1) {
                                            Toast.makeText(activity,
                                                "Payment reminder send successfully",
                                                Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                } catch (e: JSONException) {
                                    e.printStackTrace()
                                }
                            }
                        }
                        dialog.show()
                    } else {
                        telephonyManager =
                            requireActivity().getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
                        callStateListener = object : PhoneStateListener() {
                            override fun onCallStateChanged(state: Int, incomingNumber: String) {
                                if (state == TelephonyManager.CALL_STATE_RINGING) {
                                    callCurrentState = 1
                                    Toast.makeText(activity!!.applicationContext,
                                        "You can't place a video call if you're already on a phone call.",
                                        Toast.LENGTH_LONG).show()
                                } else if (state == TelephonyManager.CALL_STATE_OFFHOOK) {
                                    callCurrentState = 2
                                    Toast.makeText(activity!!.applicationContext,
                                        "You can't place a video call if you're already on a phone call.",
                                        Toast.LENGTH_LONG).show()
                                } else {
                                    if (callCurrentState == 1 || callCurrentState == 2) {
                                        callCurrentState = 0
                                    } else {
                                        if (globalClass.isOnline) {
                                            val intent =
                                                Intent(activity, VideoScreenActivity::class.java)
                                            intent.putExtra("AppointmentId",
                                                appointmentApptListModel.appointmentId)
                                            activity!!.startActivity(intent)
                                        } else {
                                            globalClass.noInternetConnection.showDialog(activity)
                                        }
                                    }
                                }
                            }
                        }
                        if (Build.VERSION.SDK_INT >= 31) {
                            if (requireContext().checkSelfPermission(Manifest.permission.READ_PHONE_STATE)
                                != PackageManager.PERMISSION_GRANTED
                            ) {
                                requestPermissionLauncher.launch(Manifest.permission.READ_PHONE_STATE)
                            } else {
                                telephonyManager.listen(callStateListener,
                                    PhoneStateListener.LISTEN_CALL_STATE)
                            }
                        } else {
                            telephonyManager.listen(callStateListener,
                                PhoneStateListener.LISTEN_CALL_STATE)
                        }
                    }
                }


            }


        apptRecycleView.layoutManager = mLayoutManager
        apptRecycleView.itemAnimator = DefaultItemAnimator()
        apptRecycleView.adapter = appointmentApptListAdapter
        all.background =
            ContextCompat.getDrawable(requireActivity(), R.drawable.filter_selected_outline)
        all.setTextColor(Color.WHITE)
        durationClicked = true
        pageNumber = 1
        getAppointmentList("All",
            "All",
            searchFilter,
            sort,
            sortBy,
            pageNumber.toString() + "",
            perPageString,
            activePastFilter,
            "0",
            userID.toString(),
            "No Records Found.")

        swipeContainer.setOnRefreshListener { // Your code to refresh the list here.
            // Make sure you call swipeContainer.setRefreshing(false)
            // once the network request has completed successfully.
            appointmentApptListModelList.toMutableList().clear()
            lastHeaderDate = ""
            c = 0
            groupData.clear()
            pageNumber = 1
            activePastFilter = if (activeTabBottom.visibility == View.VISIBLE) {
                "active"
            } else {
                "past"
            }
            if (durationFilter.equals("All", ignoreCase = true) && consultFilter.equals("All",
                    ignoreCase = true) && searchFilter.equals("",
                    ignoreCase = true) && apptTypeFilter.equals("0", ignoreCase = true)
            ) {
                getAppointmentList(durationFilter,
                    consultFilter,
                    searchFilter,
                    sort,
                    sortBy,
                    pageNumber.toString() + "",
                    perPageString,
                    activePastFilter,
                    "0",
                    userID.toString(),
                    "No Records Found.")
            } else {
                getAppointmentList(durationFilter,
                    consultFilter,
                    searchFilter,
                    sort,
                    sortBy,
                    pageNumber.toString() + "",
                    perPageString,
                    activePastFilter,
                    apptTypeFilter,
                    userID.toString(),
                    "No appointments found when given filter apply")
            }
        }

        //on active tab click
        activeTab.setOnClickListener {
            ZohoSalesIQ.Tracking.setCustomAction("ApptList - Active Appts")
            if (loader.visibility == View.VISIBLE) {
            } else {
                activeTabBottom.visibility = View.VISIBLE
                pastTabBottom.visibility = View.INVISIBLE
                appointmentApptListModelList.toMutableList().clear()
                c = 0
                groupData.clear()
                loader.visibility = View.VISIBLE
                activePastFilter = "active"
                pageNumber = 1
                getAppointmentList(durationFilter,
                    consultFilter,
                    searchFilter,
                    sort,
                    sortBy,
                    pageNumber.toString() + "",
                    perPageString,
                    activePastFilter,
                    "0",
                    userID.toString(),
                    "No Records Found.")
            }
        }

        pastTab.setOnClickListener {
            ZohoSalesIQ.Tracking.setCustomAction("ApptList - Past Appts")
            if (loader.visibility == View.VISIBLE) {
            } else {
                activeTabBottom.visibility = View.INVISIBLE
                pastTabBottom.visibility = View.VISIBLE
                appointmentApptListModelList.toMutableList().clear()
                c = 0
                groupData.clear()
                loader.visibility = View.VISIBLE
                activePastFilter = "past"
                pageNumber = 1
                getAppointmentList(durationFilter,
                    consultFilter,
                    searchFilter,
                    "desc",
                    sortBy,
                    pageNumber.toString() + "",
                    perPageString,
                    activePastFilter,
                    "0",
                    userID.toString(),
                    "No Records Found.")
            }
        }

        bookAppt.setOnClickListener {
            val intent = Intent(activity, BookAppointmentActivity::class.java)
            intent.putExtra("bookAppointment", 1)
            requireActivity().startActivity(intent)
        }

        all.setOnClickListener { v ->
            val checked = (v as RadioButton).isChecked
            if (checked) {
                if (durationClicked) {
                } else {
                    durationClicked = true
                    all.isChecked = true
                    today.isChecked = false
                    week.isChecked = false
                    month.isChecked = false
                    durationFilterChanged()
                }
            }
        }
        today.setOnClickListener { v ->
            val checked = (v as RadioButton).isChecked
            if (checked) {
                if (durationClicked) {
                } else {
                    durationClicked = true
                    all.isChecked = false
                    today.isChecked = true
                    week.isChecked = false
                    month.isChecked = false
                    durationFilterChanged()
                }
            }
        }
        week.setOnClickListener { v ->
            val checked = (v as RadioButton).isChecked
            if (checked) {
                if (durationClicked) {
                } else {
                    durationClicked = true
                    all.isChecked = false
                    today.isChecked = false
                    week.isChecked = true
                    month.isChecked = false
                    durationFilterChanged()
                }
            }
        }
        month.setOnClickListener { v ->
            val checked = (v as RadioButton).isChecked
            if (checked) {
                if (durationClicked) {
                } else {
                    durationClicked = true
                    all.isChecked = false
                    today.isChecked = false
                    week.isChecked = false
                    month.isChecked = true
                    durationFilterChanged()
                }
            }
        }


        return apptView
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        // Do something that differs the Activity's menu here
        inflater.inflate(R.menu.appt_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
        var icon = menu.findItem(R.id.apptMenuSearch).icon
        icon?.setColorFilter(ContextCompat.getColor(requireContext(), R.color.colorWhite),
            PorterDuff.Mode.SRC_IN)
        menu.findItem(R.id.apptMenuSearch).icon = icon
        icon = menu.findItem(R.id.apptMenuFilter).icon
        icon?.setColorFilter(ContextCompat.getColor(requireContext(), R.color.colorWhite),
            PorterDuff.Mode.SRC_IN)
        menu.findItem(R.id.apptMenuFilter).icon = icon
        menu.findItem(R.id.apptMenuFilter)
            .setOnMenuItemClickListener {
                ZohoSalesIQ.Tracking.setCustomAction("ApptList - Open Filter")
                AppointmentFilterBottomSheetDialogFragment = AppointmentFilterBottomSheet()
                AppointmentFilterBottomSheetDialogFragment.setupConfig(this@AppointmentFragment,
                    durationFilter,
                    consultFilter,
                    apptTypeFilter,
                    activePastFilter)
                AppointmentFilterBottomSheetDialogFragment.show(requireActivity().supportFragmentManager,
                    "Bottom Sheet Dialog Fragment")
                false
            }

        val myActionMenuItem = menu.findItem(R.id.apptMenuSearch)
        searchView = myActionMenuItem.actionView as SearchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                val view = activity!!.window.currentFocus
                isFilterApplied = true
                if (view != null) {
                    val imm =
                        activity!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(view.windowToken, 0)
                }
                loader.visibility = View.VISIBLE
                //                groupData.clear();
                appointmentApptListModelList.toMutableList().clear()
                if (appointmentApptListAdapter != null) {
                    appointmentApptListAdapter.notifyDataSetChanged()
                }
                searchFilter = query
                pageNumber = 1
                c = 0
                groupData.clear()
                getAppointmentList(durationFilter,
                    consultFilter,
                    searchFilter,
                    sort,
                    sortBy,
                    pageNumber.toString() + "",
                    perPageString,
                    activePastFilter,
                    apptTypeFilter,
                    userID.toString(),
                    "No Records Found.")
                return true
            }

            override fun onQueryTextChange(newText: String): Boolean {
                if (TextUtils.isEmpty(newText)) {
                    isFilterApplied = false
                    appointmentApptListModelList.toMutableList().clear()
                    searchFilter = ""
                    pageNumber = 1
                    c = 0
                    groupData.clear()

                    getAppointmentList(durationFilter,
                        consultFilter,
                        searchFilter,
                        sort,
                        sortBy,
                        pageNumber.toString() + "",
                        perPageString,
                        activePastFilter,
                        apptTypeFilter,
                        userID.toString(),
                        "No Records Found.")
                }
                return true
            }
        })

    }


     fun getAppointmentList(
        duration: String,
        mode: String,
        search: String,
        sort: String,
        sortBy: String,
        pageNum: String,
        perPage: String,
        type: String,
        apptType: String,
        docId: String?,
        emptyMessage: String?,
    ) {
        val url =
            ApiUrls.getAppointmentList + "?duration=" + duration + "&mode=" + mode +
                    "&search=" + search + "&sort=" + sort + "&sortBy=" + sortBy +
                    "&page=" + pageNum + "&per_page=" + perPage + "&type=" + type +
                    "&appt_type=" + apptType
        if (swipeContainer.isRefreshing) {
            loader.visibility = View.GONE
        } else {
            loader.visibility = View.VISIBLE
        }
        transView.visibility = View.VISIBLE
        appointmentApptListAdapter.notifyDataSetChanged()
        emptyText.visibility = View.GONE
        getPostMethodCalls.volleyApiRequestData(url,
            Request.Method.GET,
            null,
            requireContext(),
            object : VolleyCallback {
                override fun onSuccess(result: String) {
                    // display response
                    var responseLenght = 0
                    loader.visibility = View.GONE
                    emptyText.visibility = View.GONE
                    durationClicked = false
                    if (swipeContainer.isRefreshing) {
                        swipeContainer.isRefreshing = false
                    }
                    try {
                        val response = JSONObject(result)
                        val rootObj = response.getJSONObject("response")
                        val currentpage = rootObj.getInt("current_page")
                        val lastpage = rootObj.getInt("last_page")
                        val appointmentObject = rootObj.getJSONArray("data1")
                        for (j in 0 until appointmentObject.length()) {
                            val appointmentJsonObject = appointmentObject.getJSONObject(j)
                            val appointmentObjectTwo =
                                appointmentJsonObject.getJSONArray("appointments")
                            appointmentLength += appointmentObjectTwo.length()
                            for (i in 0 until appointmentObjectTwo.length()) {
                                responseLenght++
                                groupData.add(c, i)
                                c++
                                val tempobj = appointmentObjectTwo.getJSONObject(i)
                                val orderArray = tempobj.getJSONObject("order")
                                val productObjectArray = orderArray.getJSONObject("products")
                                val productServiceObjectArray =
                                    productObjectArray.getJSONObject("prod_service")
                                if (!orderArray.isNull("order_user")) {
                                    val orderUserArray = orderArray.getJSONObject("order_user")
                                    val sb = StringBuilder()
                                    val appointmentAssignCategory =
                                        orderUserArray.getJSONArray("assignedCategories")
                                    assignCategoryData =
                                        if (appointmentAssignCategory.length() > 0) {
                                            for (k in 0 until appointmentAssignCategory.length()) {
                                                val categoryObject =
                                                    appointmentAssignCategory.getJSONObject(k)
                                                val str = categoryObject.getString("category_name")
                                                sb.append(str)
                                                sb.append(",")
                                            }
                                            sb.deleteCharAt(sb.length - 1)
                                            sb.toString()
                                        } else {
                                            "NA"
                                        }
                                    val model = AppointmentApptListModel()
                                    model.apptMode = tempobj.getInt("mode")
                                    model.patientName = orderUserArray.getString("fname")
                                    model.patientId = orderUserArray.getInt("id")
                                    model.apptTime = tempobj.getString("appt_start_time")
                                    model.groupNo = j
                                    model.apptDate = appointmentJsonObject.getString("date")
                                    model.clinicName = productServiceObjectArray.getString("alias")
                                    model.clinicAddress =
                                        productServiceObjectArray.getString("address")
                                    model.apptCatAssign = assignCategoryData
                                    model.appointmentId = tempobj.getInt("id")
                                    model.paymentStatus = orderArray.getString("payment_status")
                                    model.sendPaymentNotification =
                                        orderArray.getInt("send_payment_notification")
                                    model.paymentType = tempobj.getInt("appt_type")
                                    model.apptAttendanceStatus =
                                        tempobj.getInt("overall_presence_status")
                                    val intervention = orderArray["receipt"]
                                    when (intervention) {
                                        is JSONArray -> {
                                            // It's an array
                                        }
                                        is JSONObject -> {
                                            // It's an object
                                            val receiptObject = orderArray.getJSONObject("receipt")
                                            model.receiptUrl = receiptObject.getString("public_url")
                                        }
                                        else -> {
                                            model.receiptUrl = ""
                                            // It's something else, like a string or number
                                        }
                                    }
                                    model.netAmount = orderArray.getInt("net_amount")
                                    model.orderAmount = orderArray.getInt("order_amt")
                                    model.discount = orderArray.getInt("discount")
                                    model.orderId = orderArray.getInt("id")
                                    model.orderUserId = orderUserArray.getInt("id")
                                    model.activePast = 1
                                    if (type.equals("past", ignoreCase = true)) {
                                        model.activePast = 2
                                    }
                                    model.apptStatus = tempobj.getInt("status")
                                    appointmentApptListModelList.toMutableList().add(model)
                                } else {
                                    //added on 2-7-2020
                                    val model = AppointmentApptListModel()
                                    model.apptMode = tempobj.getInt("mode")
                                    model.patientName = ""
                                    model.patientId = 0
                                    model.apptTime = tempobj.getString("appt_start_time")
                                    model.groupNo = j
                                    model.apptDate = appointmentJsonObject.getString("date")
                                    model.clinicName = productServiceObjectArray.getString("alias")
                                    model.clinicAddress =
                                        productServiceObjectArray.getString("address")
                                    model.apptCatAssign = "NA"
                                    model.appointmentId = tempobj.getInt("id")
                                    model.paymentStatus = orderArray.getString("payment_status")
                                    model.sendPaymentNotification =
                                        orderArray.getInt("send_payment_notification")
                                    model.paymentType = tempobj.getInt("appt_type")
                                    model.apptAttendanceStatus =
                                        tempobj.getInt("overall_presence_status")
                                    val intervention = orderArray["receipt"]
                                    when (intervention) {
                                        is JSONArray -> {
                                            // It's an array
                                        }
                                        is JSONObject -> {
                                            // It's an object
                                            val receiptObject = orderArray.getJSONObject("receipt")
                                            model.receiptUrl = receiptObject.getString("public_url")
                                        }
                                        else -> {
                                            model.receiptUrl = ""
                                            // It's something else, like a string or number
                                        }
                                    }
                                    model.netAmount = orderArray.getInt("net_amount")
                                    model.orderAmount = orderArray.getInt("order_amt")
                                    model.discount = orderArray.getInt("discount")
                                    model.orderId = orderArray.getInt("id")
                                    model.orderUserId = 0
                                    model.activePast = 1
                                    if (type.equals("past", ignoreCase = true)) {
                                        model.activePast = 2
                                    }
                                    model.apptStatus = tempobj.getInt("status")
                                    appointmentApptListModelList.toMutableList().add(model)
                                }
                            }
                            Log.d("Appointemt Size",
                                appointmentApptListModelList.size.toString() + "")
                            Log.d("Total Size", rootObj.getInt("total").toString() + "")
                            isMoreData = appointmentApptListModelList.size < rootObj.getInt("total")
                        }
                        if (responseLenght >= Integer.valueOf(perPageString) || appointmentApptListModelList.size >= 50) {
                            appointmentApptListModelList.toMutableList()
                                .add(AppointmentApptListModel())
                        }
                        toolbar.subtitle = "Total: " + rootObj.getInt("total")
                        if (rootObj.getInt("total") == 0) {
                            emptyText.visibility = View.VISIBLE
                            emptyText.text = emptyMessage
                            try {
                                //sending broadcast
                                val intent = Intent(CUSTOM_BROADCAST_ACTION)
                                intent.putExtra("Activity", "AppointmentList")
                                /*set the package name for broadcast and changes the custom_broadcast_action string value to normal string*/
                                if (activity != null) {
                                    intent.setPackage(activity!!.packageName)
                                    activity!!.sendBroadcast(intent)
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                        apptRecycleView.adapter = appointmentApptListAdapter
                        appointmentApptListAdapter.notifyDataSetChanged()
                        transView.visibility = View.GONE
                    } catch (e: Exception) {
                        transView.visibility = View.GONE
                    }
                }

                override fun onError(err: String) {
                    try {
                        transView.visibility = View.GONE
                        loader.visibility = View.GONE
                        emptyText.visibility = View.VISIBLE
                        emptyText.text =
                            resources.getString(R.string.common_opps_somthing_went_wrong_message)
                        if (swipeContainer.isRefreshing) {
                            swipeContainer.isRefreshing = false
                        }
                        errorHandler(requireContext(), err)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            })
    }


    private fun getAttendanceStatus(
        startTime: String,
        appointmentApptListModel: AppointmentApptListModel,
    ) {
        val url = ApiUrls.getApptAttendanceStatus + "?start_time=" + startTime
        appointmentApiRequests.getApptApiData(url, "", activity, object : VolleyCallback {
            override fun onSuccess(result: String) {
                try {
                    val resObj = JSONObject(result)
                    val interventionOne = resObj["response"]
                    when (interventionOne) {
                        is JSONArray -> {
                            // It's an array
                            openAttendDialog(appointmentApptListModel.appointmentId,
                                appointmentApptListModel.apptAttendanceStatus,
                                2)
                        }
                        is JSONObject -> {
                            // It's an object
                        }
                        else -> {
                            // It's something else, like a string or number
                            Toast.makeText(activity,
                                "Attendance can only be updated upto 4 hours after the appointment",
                                Toast.LENGTH_LONG).show()
                        }
                    }
                } catch (e: Exception) {
                    e.message
                }
            }

            override fun onError(err: String) {
                errorHandler(requireActivity(), err)
            }
        })
    }

    fun openAttendDialog(apptID: Int, currentStatus: Int, type: Int) {
        val inflater = layoutInflater
        val alertLayout = inflater.inflate(R.layout.dialog_appt_attendance, null)
        val alert = androidx.appcompat.app.AlertDialog.Builder(
            requireActivity())
        alert.setView(alertLayout)
        alert.setCancelable(true)
        val dialog = alert.create()
        dialog.show()
        val updateStatus = alertLayout.findViewById<LinearLayout>(R.id.apptDialogUpdate)
        val attendStatus = alertLayout.findViewById<LinearLayout>(R.id.apptDialogAttend)
        val none = alertLayout.findViewById<LinearLayout>(R.id.apptAttNoneStatus)
        val checkedIn = alertLayout.findViewById<LinearLayout>(R.id.apptAttCheckedInStatus)
        val consult = alertLayout.findViewById<LinearLayout>(R.id.apptAttConsultStatus)
        val checkedOut = alertLayout.findViewById<LinearLayout>(R.id.apptAttCheckedOutStatus)
        val cbp = alertLayout.findViewById<LinearLayout>(R.id.apptCBPStatus)
        val cbd = alertLayout.findViewById<LinearLayout>(R.id.apptCBDStatus)
        val pns = alertLayout.findViewById<LinearLayout>(R.id.apptPNSStatus)
        val dns = alertLayout.findViewById<LinearLayout>(R.id.apptDNSStatus)
        val consulted = alertLayout.findViewById<LinearLayout>(R.id.apptConsultStatus)
        val cancel = alertLayout.findViewById<TextView>(R.id.apptCancelStatus)
        val closeUpdate = alertLayout.findViewById<TextView>(R.id.apptCloseStatus)
        if (type == 1) {
            updateStatus.visibility = View.VISIBLE
            attendStatus.visibility = View.GONE
        } else {
            updateStatus.visibility = View.GONE
            attendStatus.visibility = View.VISIBLE
        }
        when (currentStatus) {
            0 -> none.setBackgroundColor(ContextCompat.getColor(requireActivity(),
                R.color.colorAccent))
            1 -> checkedIn.setBackgroundColor(ContextCompat.getColor(requireActivity(),
                R.color.colorAccent))
            3 -> consult.setBackgroundColor(ContextCompat.getColor(requireActivity(),
                R.color.colorAccent))
            4 -> checkedOut.setBackgroundColor(ContextCompat.getColor(requireActivity(),
                R.color.colorAccent))
        }
        none.setOnClickListener {
            ZohoSalesIQ.Tracking.setCustomAction("ApptList - Appt Attendance Update")
            updateAttendanceStatus(apptID, 0)
            dialog.dismiss()
        }
        checkedIn.setOnClickListener {
            ZohoSalesIQ.Tracking.setCustomAction("ApptList - Appt Attendance Update")
            updateAttendanceStatus(apptID, 1)
            dialog.dismiss()
        }
        consult.setOnClickListener {
            ZohoSalesIQ.Tracking.setCustomAction("ApptList - Appt Attendance Update")
            updateAttendanceStatus(apptID, 3)
            dialog.dismiss()
        }
        checkedOut.setOnClickListener {
            ZohoSalesIQ.Tracking.setCustomAction("ApptList - Appt Attendance Update")
            updateAttendanceStatus(apptID, 4)
            dialog.dismiss()
        }
        cancel.setOnClickListener { dialog.dismiss() }
        cbd.setOnClickListener {
            ZohoSalesIQ.Tracking.setCustomAction("ApptList - Appt Status Update")
            cancelAppt(apptID, 8)
            dialog.dismiss()
        }
        cbp.setOnClickListener {
            ZohoSalesIQ.Tracking.setCustomAction("ApptList - Appt Status Update")
            cancelAppt(apptID, 5)
            dialog.dismiss()
        }
        dns.setOnClickListener {
            ZohoSalesIQ.Tracking.setCustomAction("ApptList - Appt Status Update")
            cancelAppt(apptID, 7)
            dialog.dismiss()
        }
        pns.setOnClickListener {
            ZohoSalesIQ.Tracking.setCustomAction("ApptList - Appt Status Update")
            cancelAppt(apptID, 6)
            dialog.dismiss()
        }
        consulted.setOnClickListener {
            ZohoSalesIQ.Tracking.setCustomAction("ApptList - Appt Status Update")
            isStatusClicked = true
            completedAppt(apptID, 3)
            dialog.dismiss()
        }
        closeUpdate.setOnClickListener { dialog.dismiss() }
    }

    private fun updateAttendanceStatus(apptId: Int, status: Int) {
        var reqObj: JSONObject? = null
        try {
            reqObj = JSONObject()
            reqObj.put("appt_id", apptId)
            reqObj.put("status_id", status)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        appointmentApiRequests.postApptApiData(ApiUrls.saveApptAttendanceStatus, reqObj,
            activity, object : VolleyCallback {
                override fun onSuccess(result: String) {
                    try {
                        val resObj = JSONObject(result)
                        if (resObj.getInt("response") == 1) {
                            Toast.makeText(activity,
                                resources.getString(R.string.appt_status_update),
                                Toast.LENGTH_SHORT).show()
                            for (i in appointmentApptListModelList.indices) {
                                val model = appointmentApptListModelList[i]
                                if (model.appointmentId == apptId) {
                                    model.apptAttendanceStatus = status
                                    break
                                }
                            }
                            appointmentApptListAdapter.notifyDataSetChanged()
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

    private fun cancelAppt(apptID: Int, status: Int) {
        val reqObj = JSONObject()
        try {
            reqObj.put("appId", apptID)
            reqObj.put("cancel", status)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        appointmentApiRequests.postApptApiData(ApiUrls.cancelAppt,
            reqObj,
            activity,
            object : VolleyCallback {
                override fun onSuccess(result: String) {
                    try {
                        val resObj = JSONObject(result)
                        if (resObj.getString("response").equals("success", ignoreCase = true)) {
                            Toast.makeText(activity,
                                resources.getString(R.string.appt_cancel),
                                Toast.LENGTH_SHORT).show()
                            for (i in appointmentApptListModelList.indices) {
                                val model = appointmentApptListModelList[i]
                                if (model.appointmentId == apptID) {
                                    appointmentApptListModelList.toMutableList().remove(model)
                                    groupData.removeAt(i)
                                    break
                                }
                            }
                            appointmentApptListAdapter.notifyDataSetChanged()
                            if (appointmentApptListModelList.isEmpty()) {
                                emptyText.visibility = View.VISIBLE
                            }
                            /*if (appointmentApptListModelList.toMutableList().contains(1)) {
                                val menuView1 =
                                    MainActivity.bottomNavigationView.getChildAt(0) as BottomNavigationMenuView
                                val itemView1 = menuView1.getChildAt(2) as BottomNavigationItemView
                                notificationBadge = LayoutInflater.from(activity)
                                    .inflate(R.layout.view_notification_badge, menuView1, false)
                                itemView1.addView(notificationBadge)
                            } else {*/
                            val menuView1 =
                                MainActivity.bottomNavigationView!!.getChildAt(0) as BottomNavigationMenuView
                            val itemView1 = menuView1.getChildAt(2) as BottomNavigationItemView
                            notificationBadge = LayoutInflater.from(activity)
                                .inflate(R.layout.view_notification_badge, menuView1, false)
                            itemView1.removeView(notificationBadge)
                            //  }
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

    private fun completedAppt(apptId: Int, status: Int) {

        otpLoading = ProgressDialog(activity)
        otpLoading.setMessage(resources.getString(R.string.process_request))
        otpLoading.setProgressStyle(ProgressDialog.STYLE_SPINNER)
        otpLoading.setCancelable(false)
        otpLoading.show()
        val reqObj = JSONObject()
        try {
            reqObj.put("apptId", apptId)
            reqObj.put("status", status)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        appointmentApiRequests.postApptApiData(ApiUrls.updateClinicAppt,
            reqObj,
            activity,
            object : VolleyCallback {
                override fun onSuccess(result: String) {
                    otpLoading.dismiss()
                    for (i in appointmentApptListModelList.indices) {
                        val model = appointmentApptListModelList[i]
                        if (model.appointmentId == apptId) {
                            appointmentApptListModelList.toMutableList().removeAt(i)
                            break
                        }
                    }
                    appointmentApptListAdapter.notifyDataSetChanged()
                    if (appointmentApptListModelList.isEmpty()) {
                        emptyText.visibility = View.VISIBLE
                    }
                }

                override fun onError(err: String) {
                    otpLoading.dismiss()
                    errorHandler(requireActivity(), err)
                }
            })
    }

    private fun cancelAllAppt(apptDate: String) {
        loadingDialog = ProgressDialog(activity)
        loadingDialog.setMessage(resources.getString(R.string.process_request))
        loadingDialog.setCancelable(false)
        loadingDialog.setInverseBackgroundForced(false)
        loadingDialog.show()
        try {
            val apptIdArr = JSONArray()
            for (i in appointmentApptListModelList.indices) {
                val model = appointmentApptListModelList[i]
                if (model.apptDate.equals(apptDate, ignoreCase = true)) {
                    apptIdArr.put(model.appointmentId)
                }
            }
            val reqObj = JSONObject()
            reqObj.put("appointment_ids", apptIdArr)
            appointmentApiRequests.postApptApiData(ApiUrls.cancelAllAppts,
                reqObj,
                activity,
                object : VolleyCallback {
                    override fun onSuccess(result: String) {
                        loadingDialog.dismiss()
                        Toast.makeText(activity,
                            apptIdArr.length().toString() + " appointments have been cancelled",
                            Toast.LENGTH_SHORT).show()
                        val cancelledAppts: MutableList<AppointmentApptListModel> = ArrayList()
                        for (i in appointmentApptListModelList.indices) {
                            val model = appointmentApptListModelList[i]
                            if (model.apptDate === apptDate) {
                                cancelledAppts.add(model)
                                if (apptIdArr.length() == 1) {
                                    groupData.removeAt(i)
                                }
                            }
                        }
                        appointmentApptListModelList.toMutableList().removeAll(cancelledAppts)
                        appointmentApptListAdapter.notifyDataSetChanged()
                        toolbar.subtitle = "Total: " + appointmentApptListModelList.size
                        if (appointmentApptListModelList.size == 0) {
                            emptyText.visibility = View.VISIBLE
                        }
                    }

                    override fun onError(err: String) {
                        loadingDialog.dismiss()
                        errorHandler(requireActivity(), err)
                    }
                })
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun iamLatePopup(apptDate: String?) {
        val builder = AlertDialog.Builder(activity)
        val inflater = requireActivity().layoutInflater
        val inflator = inflater.inflate(R.layout.dailog_apointment_delay, null)
        val spinner = inflator.findViewById<View>(R.id.paymentModeSpinner) as Spinner
        val userNameText = inflator.findViewById<View>(R.id.amountPaid) as EditText
        val dailogArticleCancel = inflator.findViewById<View>(R.id.dailogArticleCancel) as ImageView
        val adapter = ArrayAdapter(requireActivity(),
            android.R.layout.simple_spinner_item, requireActivity().resources
                .getStringArray(R.array.appointmentDelaylistArray))
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter
        builder.setView(inflator)
            .setPositiveButton(R.string.sends) { _, _ ->
                var paymentModespin = spinner.selectedItem.toString()
                var time = userNameText.text.toString()
                if (time.equals("0", ignoreCase = true) || time.equals("", ignoreCase = true)) {
                    time = "a"
                    paymentModespin = "little"
                }
                try {
                    val apptIdArr = JSONArray()
                    for (i in appointmentApptListModelList.indices) {
                        val model = appointmentApptListModelList[i]
                        if (model.apptDate.equals(apptDate,
                                ignoreCase = true) && model.apptMode == 3
                        ) {
                            apptIdArr.put(model.appointmentId)
                        }
                    }
                    ZohoSalesIQ.Tracking.setCustomAction("ApptList - All Appt Delay Intimation - Yes")
                    appointmentDelay(apptIdArr, paymentModespin, time)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            .setNegativeButton(R.string.cancel
            ) { dialog, _ ->
                ZohoSalesIQ.Tracking.setCustomAction("ApptList - All Appt Delay Intimation - No")
                dialog.dismiss()
            }
        val alertDialog = builder.create()
        dailogArticleCancel.setOnClickListener { alertDialog.dismiss() }
        alertDialog.show()
    }


    fun appointmentDelay(pendingIdArray: JSONArray?, delayIn: String?, delayTime: String?) {
        otpLoading = ProgressDialog(activity)
        otpLoading.setMessage(resources.getString(R.string.process_request))
        otpLoading.setProgressStyle(ProgressDialog.STYLE_SPINNER)
        otpLoading.setCancelable(false)
        otpLoading.show()
        val URL = ApiUrls.delayAppointment
        try {
            jsonValue = JSONObject()
            jsonValue.put("appointment_ids", pendingIdArray)
            jsonValue.put("delay_amount", delayTime)
            jsonValue.put("delay_unit", delayIn)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        getPostMethodCalls.volleyApiRequestData(URL,
            Request.Method.POST,
            jsonValue,
            requireContext(),
            object : VolleyCallback {
                override fun onSuccess(result: String) {
                    otpLoading.dismiss()
                    Toast.makeText(activity, "Patients notified via SMS", Toast.LENGTH_LONG).show()
                }

                override fun onError(err: String) {
                    otpLoading.dismiss()
                    errorHandler(requireContext(), err)
                }
            })
    }


    private fun durationFilterChanged() {
        if (all.isChecked) {
            durationFilter = all.text.toString()
            all.background =
                ContextCompat.getDrawable(requireActivity(), R.drawable.filter_selected_outline)
            today.background =
                ContextCompat.getDrawable(requireActivity(), R.drawable.filter_outline)
            week.background =
                ContextCompat.getDrawable(requireActivity(), R.drawable.filter_outline)
            month.background =
                ContextCompat.getDrawable(requireActivity(), R.drawable.filter_outline)
            all.setTextColor(Color.WHITE)
            today.setTextColor(ContextCompat.getColor(requireActivity(), R.color.colorCardBack))
            week.setTextColor(ContextCompat.getColor(requireActivity(), R.color.colorCardBack))
            month.setTextColor(ContextCompat.getColor(requireActivity(), R.color.colorCardBack))
        } else if (today.isChecked) {
            durationFilter = today.text.toString()
            today.background =
                ContextCompat.getDrawable(requireActivity(), R.drawable.filter_selected_outline)
            all.background = ContextCompat.getDrawable(requireActivity(), R.drawable.filter_outline)
            week.background =
                ContextCompat.getDrawable(requireActivity(), R.drawable.filter_outline)
            month.background =
                ContextCompat.getDrawable(requireActivity(), R.drawable.filter_outline)
            today.setTextColor(Color.WHITE)
            all.setTextColor(ContextCompat.getColor(requireActivity(), R.color.colorCardBack))
            week.setTextColor(ContextCompat.getColor(requireActivity(), R.color.colorCardBack))
            month.setTextColor(ContextCompat.getColor(requireActivity(), R.color.colorCardBack))
        } else if (week.isChecked) {
            durationFilter = "This " + week.text.toString()
            week.background =
                ContextCompat.getDrawable(requireActivity(), R.drawable.filter_selected_outline)
            all.background = ContextCompat.getDrawable(requireActivity(), R.drawable.filter_outline)
            today.background =
                ContextCompat.getDrawable(requireActivity(), R.drawable.filter_outline)
            month.background =
                ContextCompat.getDrawable(requireActivity(), R.drawable.filter_outline)
            week.setTextColor(Color.WHITE)
            all.setTextColor(ContextCompat.getColor(requireActivity(), R.color.colorCardBack))
            today.setTextColor(ContextCompat.getColor(requireActivity(), R.color.colorCardBack))
            month.setTextColor(ContextCompat.getColor(requireActivity(), R.color.colorCardBack))
        } else if (month.isChecked) {
            durationFilter = "This " + month.text.toString()
            month.background =
                ContextCompat.getDrawable(requireActivity(), R.drawable.filter_selected_outline)
            all.background = ContextCompat.getDrawable(requireActivity(), R.drawable.filter_outline)
            today.background =
                ContextCompat.getDrawable(requireActivity(), R.drawable.filter_outline)
            week.background =
                ContextCompat.getDrawable(requireActivity(), R.drawable.filter_outline)
            month.setTextColor(Color.WHITE)
            all.setTextColor(ContextCompat.getColor(requireActivity(), R.color.colorCardBack))
            today.setTextColor(ContextCompat.getColor(requireActivity(), R.color.colorCardBack))
            week.setTextColor(ContextCompat.getColor(requireActivity(), R.color.colorCardBack))
        }
        appointmentApptListModelList.toMutableList().clear()
        pageNumber = 1
        c = 0
        groupData.clear()

        pageNumber = 1
        getAppointmentList(durationFilter,
            consultFilter,
            searchFilter,
            sort,
            sortBy,
            pageNumber.toString() + "",
            perPageString,
            activePastFilter,
            apptTypeFilter,
            toString(userID),
            "No appointments found when given filter apply. ")
    }

    fun setFilter(durationCase: String?) {
        val selected = durationGroup.checkedRadioButtonId
        val btn = requireActivity().findViewById<RadioButton>(selected)
        if (!btn.text.toString().equals(durationFilter, ignoreCase = true)) {
            if (activity != null) {
                when (durationCase) {
                    "All" -> {
                        all.background =
                            ContextCompat.getDrawable(requireActivity(),
                                R.drawable.filter_selected_outline)
                        all.setTextColor(Color.WHITE)
                        all.isChecked = true
                    }
                    "Today" -> {
                        today.background =
                            ContextCompat.getDrawable(requireActivity(),
                                R.drawable.filter_selected_outline)
                        today.setTextColor(Color.WHITE)
                        today.isChecked = true
                    }
                    "This Week" -> {
                        week.background =
                            ContextCompat.getDrawable(requireActivity(),
                                R.drawable.filter_selected_outline)
                        week.setTextColor(Color.WHITE)
                        week.isChecked = true
                    }
                    "This Month" -> {
                        month.background =
                            ContextCompat.getDrawable(requireActivity(),
                                R.drawable.filter_selected_outline)
                        month.setTextColor(Color.WHITE)
                        month.isChecked = true
                    }
                }
            }
        }
    }


    private fun getDefaultFollowUpSetting(defalutFollow: Switch) {
        val url = ApiUrls.getFollowUpDefaultSetting
        apiCall.getRecordPref(url, "", activity, object : VolleyCallback {
            override fun onSuccess(result: String) {
                try {
                    val resObj = JSONObject(result)
                    val defalutFollowUp =
                        resObj.getJSONObject("response").getBoolean("defaultFollowUp")
                    defalutFollow.isChecked = defalutFollowUp
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            override fun onError(err: String) {
                loader.visibility = View.GONE
                errorHandler(requireActivity(), err)
            }
        })
    }


    private fun completeBulkAppointment(apptDate: String, followUp: Boolean, invoice: Boolean) {
        loadingDialog = ProgressDialog(activity)
        loadingDialog.setMessage(requireActivity().resources.getString(R.string.process_request))
        loadingDialog.setCancelable(false)
        loadingDialog.setInverseBackgroundForced(false)
        loadingDialog.show()
        requireActivity().window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        val url = ApiUrls.bulkAppointmentCompleteTwo
        try {
            val apptIdArr = JSONArray()
            for (i in appointmentApptListModelList.indices) {
                val model = appointmentApptListModelList[i]
                if (model.apptDate != null && !model.apptDate.equals("", ignoreCase = true)) {
                    if (model.apptDate.equals(apptDate, ignoreCase = true)) {
                        apptIdArr.put(model.appointmentId)
                    }
                }
            }
            jsonValue = JSONObject()
            jsonValue.put("apptIds", apptIdArr)
            jsonValue.put("isFollowUp", followUp)
            jsonValue.put("isInvoice", invoice)
        } catch (e: Exception) {
            e.message
        }
        apiCall.postRecords(url, jsonValue, activity, object : VolleyCallback {
            override fun onSuccess(result: String) {
                loadingDialog.dismiss()
                try {
                    val resObj = JSONObject(result)
                    Toast.makeText(activity,
                        "Appointments completed successfully",
                        Toast.LENGTH_LONG).show()
                    appointmentApptListModelList.toMutableList().clear()
                    c = 0
                    groupData.clear()
                    pageNumber = 1
                    getAppointmentList("All",
                        "All",
                        searchFilter,
                        sort,
                        sortBy,
                        pageNumber.toString() + "",
                        perPageString,
                        activePastFilter,
                        "0",
                        toString(userID),
                        "No Records Found.")
                    activity!!.window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                } catch (e: Exception) {
                    e.printStackTrace()
                    loadingDialog.dismiss()
                    activity!!.window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                }
            }

            override fun onError(err: String) {
                loadingDialog.dismiss()
                errorHandler(requireActivity(), err)
                activity!!.window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
            }
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            requireActivity().unregisterReceiver(appointmentBroadcastReceiver)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun openCreateDialog(appointmentApptListModel: AppointmentApptListModel) {
        //before inflating the custom alert dialog layout, we will get the current activity viewgroup
        val viewGroup = apptView.findViewById<ViewGroup>(android.R.id.content)

        //then we will inflate the custom alert dialog xml that we created
        val dialogView = LayoutInflater.from(context)
            .inflate(R.layout.dialog_create_case_channel, viewGroup, false)
        val dismiss = dialogView.findViewById<Button>(R.id.newChannelDismissBtn)
        val createNew = dialogView.findViewById<Button>(R.id.newChannelCreateBtn)
        val channelName = dialogView.findViewById<EditText>(R.id.newChannelNameEt)
        val reminder1 = dialogView.findViewById<EditText>(R.id.newChannelReminder1Et)
        val reminder2 = dialogView.findViewById<EditText>(R.id.newChannelReminder2Et)
        val episodes = dialogView.findViewById<Spinner>(R.id.newChannelEpisodes)
        val createCaseLayout = dialogView.findViewById<RelativeLayout>(R.id.createCaseLayout)
        val createNewLayout = dialogView.findViewById<RelativeLayout>(R.id.createNewLayout)
        val createNewTextView = dialogView.findViewById<TextView>(R.id.createNew)
        createNewTextView.paintFlags = createNewTextView.paintFlags or Paint.UNDERLINE_TEXT_FLAG
        val createNewButton = dialogView.findViewById<Button>(R.id.createNewButton)
        val caseEditText = dialogView.findViewById<EditText>(R.id.caseEditText)
        val c = Calendar.getInstance()
        val mYear = c[Calendar.YEAR]
        val mMonth = c[Calendar.MONTH]
        val mDay = c[Calendar.DAY_OF_MONTH]
        var temp = mDay.toString() + " " + (mMonth + 1) + " " + mYear
        temp = appUtilities.changeDateFormat("dd MM yyyy", "dd MMM, yy", temp)
        temp = appUtilities.changeDateFormat("dd MMM, yy", "dd-MM-yyyy", temp)
        temp = "Case-$temp"
        caseEditText.setText(temp)
        episodes.adapter = dataAdapter
        episodes.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>?, view: View, i: Int, l: Long) {
                val item = episodeList[i]
                for (j in episodeModels.indices) {
                    if (item.equals(episodeModels[j].episodeName, ignoreCase = true)) {
                        episodeId = episodeModels[j].episodeId
                        break
                    } else {
                        episodeId = 0
                    }
                }
            }

            override fun onNothingSelected(adapterView: AdapterView<*>?) {}
        }

        //Now we need an AlertDialog.Builder object
        val builder = androidx.appcompat.app.AlertDialog.Builder(requireActivity())

        //setting the view of the builder to our custom view that we already inflated
        builder.setView(dialogView)

        //finally creating the alert dialog and displaying it
        val alertDialog = builder.create()
        alertDialog.show()
        dismiss.setOnClickListener { view: View? -> alertDialog.dismiss() }
        createNew.setOnClickListener { view: View? ->
            if (!channelName.text.toString().equals("", ignoreCase = true)) {
                if (episodeId != 0) {
                    if (!reminder1.text.toString()
                            .equals("", ignoreCase = true) && !reminder2.text.toString()
                            .equals("", ignoreCase = true)
                    ) {
                        alertDialog.dismiss()
                        createNewCase(channelName.text.toString(),
                            appointmentApptListModel.patientId,
                            reminder1.text.toString(),
                            reminder2.text.toString())
                    } else {
                        Toast.makeText(activity,
                            "Please enter both entry for the reminders",
                            Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(context, "Please Select One Episode", Toast.LENGTH_SHORT)
                        .show()
                }
            } else {
                Toast.makeText(activity, "Please enter a channel name", Toast.LENGTH_SHORT)
                    .show()
            }
        }
        createNewLayout.setOnClickListener { view: View? ->
            if (createCaseLayout.visibility == View.VISIBLE) {
                createCaseLayout.visibility = View.GONE
            } else {
                createCaseLayout.visibility = View.VISIBLE
            }
        }
        createNewButton.setOnClickListener { view: View? ->
            if (!caseEditText.text.toString().equals("", ignoreCase = true)) {
                val caseName = caseEditText.text.toString()
                saveNewCase(appointmentApptListModel.patientId, caseName, createCaseLayout)
            } else {
                Toast.makeText(activity, "Please enter a case name", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun getEpisodes(patientId: Int) {
        val url = ApiUrls.getEpisodes + "?patient_id=" + patientId
        apiCalls.getRecordPref(url, "", activity, object : VolleyCallback {
            override fun onSuccess(result: String) {
                try {
                    val resObj = JSONObject(result)
                    val resArr = resObj.getJSONArray("response")
                    episodeList.toMutableList().add("Select Episode")
                    if (resArr.length() > 0) {

//                        episodeList.add("Select Episode");
                        for (i in 0 until resArr.length()) {
                            val episObj = resArr.getJSONObject(i)
                            val model = PatientRecordsModel()
                            model.episodeName = episObj.getString("episode_name")
                            model.episodeId = episObj.getInt("id")
                            episodeModels.toMutableList().add(model)
                            episodeList.toMutableList().add(episObj.getString("episode_name"))
                        }
                        dataAdapter.notifyDataSetChanged()
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

    private fun createNewCase(
        channelName: String,
        patientId: Int,
        reminder1: String,
        reminder2: String,
    ) {
        otpLoading = ProgressDialog(context)
        otpLoading.setMessage(resources.getString(R.string.creating_new_channel))
        otpLoading.setTitle(resources.getString(R.string.please_wait))
        otpLoading.setProgressStyle(ProgressDialog.STYLE_SPINNER)
        otpLoading.setCancelable(false)
        otpLoading.show()
        val params = JSONObject()
        try {
            params.put("patient_id", patientId)
            params.put("name", channelName)
            params.put("episode_id", episodeId)
            params.put("reminder_1", reminder1)
            params.put("reminder_2", reminder2)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        Log.d("NEw Case ", params.toString())
        apiCalls.postRecords(ApiUrls.createNewCaseChannel,
            params,
            activity,
            object : VolleyCallback {
                override fun onSuccess(result: String) {
                    try {
                        otpLoading.dismiss()
                        Log.d("Response", result)
                        val resObj = JSONObject(result)
                        val discussion_id = resObj.getJSONObject("response").getInt("id")
                        val doctorOrgActivity = Intent(activity,
                            AddDoctorsOrganisationsActivity::class.java)
                        doctorOrgActivity.putExtra("discussion_id", discussion_id)
                        startActivity(doctorOrgActivity)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                override fun onError(err: String) {
                    otpLoading.dismiss()
                    errorHandler(requireActivity(), err)
                }
            })
    }

    private fun saveNewCase(patientId: Int, caseName: String, createCaseLayout: RelativeLayout) {
        loadingDialog = ProgressDialog(activity)
        loadingDialog.setMessage(resources.getString(R.string.process_request))
        loadingDialog.setCancelable(false)
        loadingDialog.setInverseBackgroundForced(false)
        loadingDialog.show()
        val caseObj = JSONObject()
        try {
            caseObj.put("created_by", ApiUrls.doctorId)
            caseObj.put("patient_id", patientId)
            caseObj.put("episode_name", caseName)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        apiCall.postRecords(ApiUrls.saveNewEpisode, caseObj, activity, object : VolleyCallback {
            override fun onSuccess(result: String) {
                loadingDialog.dismiss()
                try {
                    val resObj = JSONObject(result)
                    if (resObj.has("response")) {
                        episodeList.toMutableList().clear()
                        getEpisodes(patientId)
                        if (createCaseLayout.visibility == View.VISIBLE) {
                            createCaseLayout.visibility = View.GONE
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            override fun onError(err: String) {
                loadingDialog.dismiss()
                errorHandler(requireActivity(), err)
            }
        })
    }

    fun trimMessage(json: String, key: String): String? {
        val trimmedString: String = try {
            val obj = JSONObject(json)
            obj.getString(key)
        } catch (e: JSONException) {
            e.printStackTrace()
            return null
        }
        return trimmedString
    }

    //Somewhere that has access to a context
    fun displayMessage(toastString: String?) {
        Toast.makeText(activity, toastString, Toast.LENGTH_LONG).show()
    }

}