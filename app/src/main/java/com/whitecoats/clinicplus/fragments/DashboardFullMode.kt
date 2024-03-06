package com.whitecoats.clinicplus.fragments

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.whitecoats.clinicplus.*
import com.whitecoats.clinicplus.activities.AddPatientActivity
import com.whitecoats.clinicplus.activities.PaymentSetupScreen
import com.whitecoats.clinicplus.adapters.*
import com.whitecoats.clinicplus.apis.ApiUrls
import com.whitecoats.clinicplus.autofollowup.AutoFollowUpActivity
import com.whitecoats.clinicplus.constants.AppConstants
import com.whitecoats.clinicplus.models.*
import com.whitecoats.clinicplus.patientsharedrecords.PatientSharedRecordsActivity
import com.whitecoats.clinicplus.trainingschedule.TrainingScheduleActivity
import com.whitecoats.clinicplus.utils.AppUtilities
import com.whitecoats.clinicplus.utils.ErrorHandlerClass.errorHandler
import com.whitecoats.clinicplus.utils.SharedPref
import com.whitecoats.clinicplus.viewmodels.DashboardViewModel
import com.whitecoats.fragments.AppointmentFragment
import com.whitecoats.fragments.PatientFragment
import com.zoho.salesiqembed.ZohoSalesIQ
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

class DashboardFullMode : Fragment() {
    private lateinit var dashboardViewModel: DashboardViewModel

    //DashboardAppointment section
    private lateinit var dashBoardPracticesApptList: RecyclerView

    private var mContext: Context? = null

    //DashboardQuickAction Section
    private lateinit var dashBoardQuickActionRecyclerView: RecyclerView
    private var dashBoardQuickActionDataAdapter: DashBoardQuickActionAdapter? = null
    private lateinit var quickActionItemList: MutableList<DashBoardQuickActionViewItem>

    //DashBoard shared records section
    private lateinit var sharedRecodsViewAll: TextView  //DashBoard shared records section
    private lateinit var sharedRecordsEmptyText: TextView
    private lateinit var dashBoardSharedRecordRv: RecyclerView
    private lateinit var shareRecordsModel: MutableList<DashBoardPatientRecordsModel>
    private lateinit var sharedRecordsListAdapter: DashBoardSharedRecordsListAdapter
    private lateinit var sharedRecordsSection: LinearLayout

    //training schedule section
    private lateinit var trainingScheduleViewAll: TextView
    private lateinit var trainingScheduleRv: RecyclerView
    private lateinit var trainingScheduleModel: MutableList<TrainingScheduleModel>
    private var trainingScheduleListAdapter: DashBoardTrainingScheduleListAdapter? = null
    private var trainingSection: LinearLayout? = null
    var mainActivity = MainActivity()


    //auto follow-up
    private lateinit var autoFollowUpViewAll: TextView //auto follow-up
    private lateinit var autoFollowUpEmptyText: TextView
    private lateinit var dashBoardAutoFollowUpRv: RecyclerView
    private lateinit var dashBoardAutoFollowUpModel: MutableList<DashBoardAutoFollowUpModel>
    private lateinit var autoFollowUpListAdapter: DashBoardAutoFollowUpListAdapter
    private lateinit var autoFollowUpSection: LinearLayout
    private lateinit var trainingScheduleSection: LinearLayout

    private lateinit var quickActionProgressbar: RelativeLayout
    private lateinit var dashboardSharedRecordProgressbar: RelativeLayout
    private lateinit var dashboardFollowUpProgressbar: RelativeLayout
    private lateinit var dashboardTrainingScheduleProgressbar: RelativeLayout
    private lateinit var noDataMessageQuickAction: RelativeLayout
    private lateinit var noDataMessageSharedByPatient: RelativeLayout
    private lateinit var noDataMessageAutoFollowUp: RelativeLayout

    var globalClass: MyClinicGlobalClass? = null

    private lateinit var practicesDurationIcon: ImageView
    private lateinit var practicesDuration: TextView
    private lateinit var practiceProgressbar: RelativeLayout
    private var selectDuration = 0
    private var followUpDefaultSwitchClick = false
    private var invoiceGenerateSwitchClick = false
    private var cancelMessage: String? = null

    private var bulkCancelSpinnerItemPosition = 0
    private var bulkCompleteSpinnerItemPosition: Int = 0

    private var dateFormat: SimpleDateFormat? = null
    private var recordDateFormat: SimpleDateFormat? = null
    private var inputFornat: SimpleDateFormat? = null
    private lateinit var homeScrollView: ScrollView
    private lateinit var create_account_ribbon: TextView
    private lateinit var ribbon_image: ImageView
    private lateinit var ribbon_layout: LinearLayout
    private lateinit var pullToRefresh: SwipeRefreshLayout
    var broadcastReceiver: BroadcastReceiver? = null
    private lateinit var sharedPref: SharedPref
    private lateinit var quickActionMainLayout: LinearLayout
    private lateinit var appUtilities: AppUtilities

    companion object {
        @JvmField
        val TAG = DashboardFullMode::class.java.simpleName

        @SuppressLint("StaticFieldLeak")
        @JvmField
        var dashBoardApptListAdapter: DashBoardApptListAdapter? = null

        @JvmField
        var dashBoardApptListModelList: MutableList<DashBoardApptListModel> = ArrayList()

        @JvmField
        var dashBoardApptListModelListAll: MutableList<DashBoardApptListModel> = ArrayList()


        @JvmField
        var doctorsDetailsRootObj: JSONObject = JSONObject()

        @JvmField
        var doctorServiceArrayList: MutableList<AppointmentSlotListModel> = ArrayList()

        @JvmField
        var selectedDuration = "Today"

        @JvmField
        var isAppointmentServiceId = 0

        @JvmField
        var isServiceId: Int = 0

        @JvmField
        var switchClinicSelectedString = ""

        @JvmField
        var isAppointBookingOnDashBoard = 0

        @JvmField
        var isPatientListRefreshReq = false

        @JvmField
        var isFollowUpListRefreshReq = false

        @JvmField
        var isAppointmentListRefreshReq = false

        @JvmField
        var switchClickSelectedPosition: Int = 0


        fun switchClinic(SelectedClinicId: Int) {
            try {
                AppConstants.selectedClinicClickOnDashBoard = 1
                AppConstants.selectedClinicIdOnDashBoard = SelectedClinicId
                dashBoardApptListModelList.clear()
                if (dashBoardApptListModelListAll.size > 0) {
                    for (i in dashBoardApptListModelListAll.indices) {
                        val dashBoardApptListModel = dashBoardApptListModelListAll[i]
                        if (dashBoardApptListModel.serviceId == 3) {
                            if (i == SelectedClinicId) {
                                val temp = DashBoardApptListModel()
                                temp.clinicName = dashBoardApptListModel.clinicName
                                temp.productId = dashBoardApptListModel.productId
                                temp.doctorServiceId = dashBoardApptListModel.doctorServiceId
                                temp.apptCount = dashBoardApptListModel.apptCount
                                temp.appointmentPendingId =
                                    dashBoardApptListModel.appointmentPendingId
                                temp.patientApptArray = dashBoardApptListModel.patientApptArray
                                temp.serviceId = 3
                                dashBoardApptListModelList.add(temp)
                            }
                        }
                        if (dashBoardApptListModel.serviceId == 1) {
                            val tempVideo = DashBoardApptListModel()
                            tempVideo.clinicName = dashBoardApptListModel.clinicName
                            tempVideo.productId = dashBoardApptListModel.productId
                            tempVideo.apptCount = dashBoardApptListModel.apptCount
                            tempVideo.doctorServiceId = dashBoardApptListModel.doctorServiceId
                            tempVideo.appointmentPendingId =
                                dashBoardApptListModel.appointmentPendingId
                            tempVideo.patientApptArray = dashBoardApptListModel.patientApptArray
                            tempVideo.serviceId = 1
                            dashBoardApptListModelList.add(tempVideo)
                        }
                        if (dashBoardApptListModel.serviceId == 2) {
                            val tempChat = DashBoardApptListModel()
                            tempChat.clinicName = dashBoardApptListModel.clinicName
                            tempChat.productId = dashBoardApptListModel.productId
                            tempChat.doctorServiceId = dashBoardApptListModel.doctorServiceId
                            tempChat.apptCount = dashBoardApptListModel.apptCount
                            tempChat.patientApptArray = dashBoardApptListModel.patientApptArray
                            tempChat.serviceId = 2
                            dashBoardApptListModelList.add(tempChat)
                        }
                    }
                    // Set data adapter.
                    dashBoardApptListAdapter!!.notifyDataSetChanged()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

    }

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater,
        parent: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val dashBoardFullModeTabView =
            inflater.inflate(R.layout.fragment_dashboard_full_mode, parent, false)
        dashboardViewModel = ViewModelProvider(this)[DashboardViewModel::class.java]
        dashboardViewModel.init()
        appUtilities = AppUtilities()
        dashBoardApptListModelList = ArrayList()
        dashBoardApptListModelListAll = ArrayList<DashBoardApptListModel>()
        quickActionItemList = ArrayList<DashBoardQuickActionViewItem>()
        shareRecordsModel = ArrayList<DashBoardPatientRecordsModel>()
        dashBoardAutoFollowUpModel = ArrayList<DashBoardAutoFollowUpModel>()
        trainingScheduleModel = ArrayList<TrainingScheduleModel>()
        this.mContext = activity
        globalClass = requireActivity().applicationContext as MyClinicGlobalClass
        sharedPref = SharedPref(activity)


        registerReceiverAppointmetListPatientListAndAutoFlowUp()
        doctorServiceArrayList = ArrayList()
        dateFormat = SimpleDateFormat("MMM, yyyy", Locale.ENGLISH)
        recordDateFormat = SimpleDateFormat("dd MMM, yyyy mm:HH", Locale.ENGLISH)
        inputFornat = SimpleDateFormat("yyyy-MM-dd mm:HH:ss", Locale.ENGLISH)
        sharedRecodsViewAll = dashBoardFullModeTabView.findViewById(R.id.sharedRecordsViewAll)
        autoFollowUpViewAll = dashBoardFullModeTabView.findViewById(R.id.autoFollowUpViewAll)
        trainingScheduleViewAll =
            dashBoardFullModeTabView.findViewById(R.id.trainingScheduleViewAll)
        autoFollowUpViewAll.paintFlags =
            autoFollowUpViewAll.paintFlags or android.graphics.Paint.UNDERLINE_TEXT_FLAG
        sharedRecodsViewAll.paintFlags =
            sharedRecodsViewAll.paintFlags or android.graphics.Paint.UNDERLINE_TEXT_FLAG
        trainingScheduleViewAll.paintFlags =
            trainingScheduleViewAll.paintFlags or android.graphics.Paint.UNDERLINE_TEXT_FLAG
        quickActionMainLayout =
            dashBoardFullModeTabView.findViewById(R.id.quickActionMainLayout)
        quickActionProgressbar =
            dashBoardFullModeTabView.findViewById(R.id.quickActionProgressbar)
        noDataMessageQuickAction =
            dashBoardFullModeTabView.findViewById(R.id.noDataMessageQuickAction)
        noDataMessageSharedByPatient =
            dashBoardFullModeTabView.findViewById(R.id.noDataMessageSharedByPatient)
        dashboardSharedRecordProgressbar =
            dashBoardFullModeTabView.findViewById(R.id.dashboardSharedRecordProgressbar)
        sharedRecordsEmptyText =
            dashBoardFullModeTabView.findViewById(R.id.sharedRecordsEmptyText)
        dashboardFollowUpProgressbar =
            dashBoardFullModeTabView.findViewById(R.id.dashboardFollowUpProgressbar)
        noDataMessageAutoFollowUp =
            dashBoardFullModeTabView.findViewById(R.id.noDataMessageAutoFollowUp)
        autoFollowUpEmptyText =
            dashBoardFullModeTabView.findViewById(R.id.autoFollowUpEmptyText)
        practicesDurationIcon =
            dashBoardFullModeTabView.findViewById(R.id.practicesDurationIcon)
        practicesDuration = dashBoardFullModeTabView.findViewById(R.id.practicesDuration)
        practiceProgressbar =
            dashBoardFullModeTabView.findViewById(R.id.practiceProgressbar)
        trainingScheduleSection =
            dashBoardFullModeTabView.findViewById(R.id.trainingScheduleSection)
        dashboardTrainingScheduleProgressbar =
            dashBoardFullModeTabView.findViewById(R.id.dashboardTrainingScheduleProgressbar)
        create_account_ribbon =
            dashBoardFullModeTabView.findViewById(R.id.verify_now_ribbon)
        ribbon_image = dashBoardFullModeTabView.findViewById(R.id.image_ribbon)
        ribbon_layout =
            dashBoardFullModeTabView.findViewById(R.id.layout_ribbon_verify)
        homeScrollView = dashBoardFullModeTabView.findViewById(R.id.homeScrollView)
        pullToRefresh =
            dashBoardFullModeTabView.findViewById(R.id.pullToRefresh)
        dashBoardApptListAdapter = DashBoardApptListAdapter(
            requireContext(), dashBoardApptListModelList
        ) { v, position, pendingIdArray, ClickAction, ClinicName, product_id, serviceId, isAppointmentNextBackVideoClick, isAppointmentNextBackChatClick, isAppointmentNextBackClinicClick, appointmentServiceId ->
            if (ClickAction.equals("SWITCH_CLINIC", ignoreCase = true)) {
                val bottomSheetSwitchClinicDialogFragment = DashBoardSwitchClinicBottomSheet()
                bottomSheetSwitchClinicDialogFragment.setupConfig(
                    requireActivity(),
                    this@DashboardFullMode,
                    dashBoardApptListModelListAll
                )
                bottomSheetSwitchClinicDialogFragment.show(
                    requireActivity().supportFragmentManager,
                    "Bottom Sheet Dialog Fragment"
                )
            }
            if (ClickAction.equals("LATE", ignoreCase = true)) {
                iAmLatePopup(pendingIdArray)
            }
            if (ClickAction.equals("BULK_CANCEL", ignoreCase = true)) {
                BulkCancelPopup(pendingIdArray, ClinicName, product_id)
            }
            if (ClickAction.equals("BULK_COMPLETE", ignoreCase = true)) {
                BulkCompletePopup(pendingIdArray, ClinicName, product_id)
            }
            if (ClickAction.equals("PATIENT_APPOINTMENT_CARD", ignoreCase = true)) {
                if (globalClass!!.isOnline) {
                    isAppointBookingOnDashBoard = 1
                    switchClinicSelectedString = ClinicName
                    if (serviceId == 2) {
                        try {
                            var patientName: String? = ""
                            var chatId = 0
                            var recipientId = 0
                            if (pendingIdArray.length() > 0) {
                                for (i in 0 until pendingIdArray.length()) {
                                    if (i == isAppointmentNextBackChatClick) {
                                        patientName =
                                            pendingIdArray.getJSONObject(i)
                                                .getJSONObject("chat_user")
                                                .getString("fname")
                                        chatId = pendingIdArray.getJSONObject(i).getInt("chat_id")
                                        recipientId =
                                            pendingIdArray.getJSONObject(i).getInt("participant_id")
                                        break
                                    }
                                }
                            }
                            val intent = Intent(activity, ChatRoomActivity::class.java)
                            intent.putExtra("chatId", chatId)
                            intent.putExtra("recipientId", recipientId)
                            intent.putExtra("patientName", patientName)
                            intent.putExtra("ChatType", "Active")
                            requireActivity().startActivity(intent)
                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }
                    } else {
                        appUtilities.selectBottomNavigationScreen("Appointments")
                    }
                } else {
                    globalClass!!.noInternetConnection.showDialog(activity)
                }
            }
            if (ClickAction.equals("BOOK_APPOINTMENT", ignoreCase = true)) {
                if (globalClass!!.isOnline) {
                    isAppointBookingOnDashBoard = 1
                    switchClinicSelectedString = ClinicName
                    isAppointmentServiceId = appointmentServiceId
                    isServiceId = serviceId
                    SearchFragment.emptyPatientText.visibility = View.VISIBLE
                    val fm = childFragmentManager
                    val fragm = fm.findFragmentById(R.id.sfragment) as SearchFragment?
                    fragm!!.searchDialog.show()
                } else {
                    globalClass!!.noInternetConnection.showDialog(activity)
                }
            }
            if (ClickAction.equals("VIEW_ALL_CHAT", ignoreCase = true)) {
                if (globalClass!!.isOnline) {
                    appUtilities.selectBottomNavigationScreen("Chats")
                } else {
                    globalClass!!.noInternetConnection.showDialog(activity)
                }
            }
        }
        dashBoardPracticesApptList =
            dashBoardFullModeTabView.findViewById(R.id.dashBoardPracticesApptList)
        dashBoardPracticesApptList.layoutManager =
            LinearLayoutManager(
                requireActivity().applicationContext,
                LinearLayoutManager.HORIZONTAL,
                false
            )
        dashBoardPracticesApptList.itemAnimator = DefaultItemAnimator()
        dashBoardPracticesApptList.isNestedScrollingEnabled = false
        dashBoardPracticesApptList.adapter = dashBoardApptListAdapter

        //Training schedule
        trainingScheduleRv =
            dashBoardFullModeTabView.findViewById(R.id.trainingScheduleRv)
        trainingSection =
            dashBoardFullModeTabView.findViewById(R.id.trainingScheduleBookedSection)
        trainingScheduleListAdapter = activity?.let {
            DashBoardTrainingScheduleListAdapter(
                it, trainingScheduleModel
            ) { v, position, cancel, trainingModel ->
                if (cancel.equals("CANCEL_TRAINING", ignoreCase = true)) {
                    if (globalClass!!.isOnline) {
                        val builder = android.app.AlertDialog.Builder(
                            activity
                        )
                        builder.setTitle("Cancel")
                        builder.setMessage("Do you want to cancel the training?")
                        builder.setPositiveButton(
                            "YES"
                        ) { dialog, which ->
                            dialog.dismiss()
                            cancelBookedTraining(trainingModel.id, trainingModel.traingScheduleId)
                        }
                        builder.setNegativeButton(
                            "NO"
                        ) { dialog, which -> dialog.dismiss() }
                        val alert = builder.create()
                        alert.show()
                    } else {
                        globalClass!!.noInternetConnection.showDialog(activity)
                    }
                } else if (cancel.equals("DESCRIPTION_MORE_INFO", ignoreCase = true)) {
                    val builder = android.app.AlertDialog.Builder(
                        activity
                    )
                    builder.setTitle("Training Description")
                    builder.setMessage(trainingModel.trainingDescription)
                    builder.setPositiveButton(
                        "OK"
                    ) { dialog, which -> dialog.dismiss() }
                    val alert = builder.create()
                    alert.show()
                }
            }
        }
        trainingScheduleRv.layoutManager =
            LinearLayoutManager(
                requireActivity().applicationContext,
                LinearLayoutManager.HORIZONTAL,
                false
            )
        trainingScheduleRv.itemAnimator = DefaultItemAnimator()
        trainingScheduleRv.adapter = trainingScheduleListAdapter


        // Create the recyclerview for quick action.
        dashBoardQuickActionRecyclerView =
            dashBoardFullModeTabView.findViewById(R.id.card_view_recycler_list)
        val gridLayoutManager = GridLayoutManager(activity, 3)
        dashBoardQuickActionRecyclerView.layoutManager = gridLayoutManager
        dashBoardQuickActionDataAdapter = DashBoardQuickActionAdapter(
            quickActionItemList, requireContext()
        ) { v, position, menuId, loadMore ->
            if (globalClass!!.isOnline) {
                when (menuId) {
                    22 -> { //quick note
                        PatientFragment.patientTabFlag = 0
                        AppointmentFragment.appointmentTabFlag = 0
                        com.whitecoats.fragments.ChatFragment.chatTabFlag = 0
                        val intent = Intent(activity, CreateNoteActivity::class.java)
                        requireActivity().startActivity(intent)
                    }
                    27 -> { //quick appointment
                        MyClinicGlobalClass.logUserActionEvent(
                            ApiUrls.doctorId,
                            getString(R.string.DashboardQuickActionBookAppt),
                            null
                        )
                        PatientFragment.patientTabFlag = 0
                        AppointmentFragment.appointmentTabFlag = 0
                        com.whitecoats.fragments.ChatFragment.chatTabFlag = 0
                        val intent = Intent(activity, BookAppointmentActivity::class.java)
                        intent.putExtra("bookAppointment", 1)
                        requireActivity().startActivity(intent)
                    }
                    25 -> { //add patient
                        MyClinicGlobalClass.logUserActionEvent(
                            ApiUrls.doctorId,
                            getString(R.string.DashboardQuickActionAddPatient),
                            null
                        )
                        PatientFragment.patientTabFlag = 0
                        AppointmentFragment.appointmentTabFlag = 0
                        com.whitecoats.fragments.ChatFragment.chatTabFlag = 0
                        val addPatientIntent = Intent(context, AddPatientActivity::class.java)
                        startActivity(addPatientIntent)
                    }
                    26 -> { //block time
                        MyClinicGlobalClass.logUserActionEvent(
                            ApiUrls.doctorId,
                            getString(R.string.DashboardQuickActionBlockTime),
                            null
                        )
                        PatientFragment.patientTabFlag = 0
                        AppointmentFragment.appointmentTabFlag = 0
                        com.whitecoats.fragments.ChatFragment.chatTabFlag = 0
                        val intent = Intent(activity, BlockTimeActivity::class.java)
                        requireActivity().startActivity(intent)
                    }
                    32 -> { //share video
                        MyClinicGlobalClass.logUserActionEvent(
                            ApiUrls.doctorId,
                            getString(R.string.DashboardQuickActionShareVideo),
                            null
                        )
                        PatientFragment.patientTabFlag = 0
                        AppointmentFragment.appointmentTabFlag = 0
                        com.whitecoats.fragments.ChatFragment.chatTabFlag = 0
                        val intent = Intent(activity, CreateVideoArticleActivity::class.java)
                        requireActivity().startActivity(intent)
                    }
                    31 -> { //share articles
                        MyClinicGlobalClass.logUserActionEvent(
                            ApiUrls.doctorId,
                            getString(R.string.DashboardQuickActionShareArticle),
                            null
                        )
                        PatientFragment.patientTabFlag = 0
                        AppointmentFragment.appointmentTabFlag = 0
                        com.whitecoats.fragments.ChatFragment.chatTabFlag = 0
                        val intent = Intent(activity, CreateTextArticleActivity::class.java)
                        requireActivity().startActivity(intent)
                    }
                    29 -> { //new message
                        MyClinicGlobalClass.logUserActionEvent(
                            ApiUrls.doctorId,
                            getString(R.string.DashboardQuickActionNewMessage),
                            null
                        )
                        val bottomSheetDialogFragment = DashBoardCreateMessBottomSheet()
                        bottomSheetDialogFragment.setupConfig(this@DashboardFullMode)
                        bottomSheetDialogFragment.show(
                            requireActivity().supportFragmentManager,
                            "Bottom Sheet Dialog Fragment"
                        )
                    }
                    47 -> { // need to change later
                        PatientFragment.patientTabFlag = 0
                        AppointmentFragment.appointmentTabFlag = 0
                        com.whitecoats.fragments.ChatFragment.chatTabFlag = 0
                        val intent = Intent(activity, CreatePdfArticlesActivity::class.java)
                        requireActivity().startActivity(intent)
                    }
                }
            } else {
                globalClass!!.noInternetConnection.showDialog(activity)
            }
        }
        // Set data adapter.
        dashBoardQuickActionRecyclerView.adapter = dashBoardQuickActionDataAdapter

        //shared records
        sharedRecodsViewAll =
            dashBoardFullModeTabView.findViewById(R.id.sharedRecordsViewAll)
        dashBoardSharedRecordRv =
            dashBoardFullModeTabView.findViewById(R.id.sharedRecordsRv)
        sharedRecordsSection =
            dashBoardFullModeTabView.findViewById(R.id.sharedRecordsSection)
        sharedRecordsListAdapter = activity?.let {
            DashBoardSharedRecordsListAdapter(
                it, shareRecordsModel
            ) { v, parameter, type, recordIdArray ->
                if (globalClass!!.isOnline) {
                    dashboardViewModel.getFileFromUrl(activity, parameter)
                        .observe(requireActivity()) { s ->
                            try {
                                val jsonObject = JSONObject(s)
                                if (jsonObject.getInt("status_code") == 200) {
                                    val response = JSONObject(s).getJSONObject("response")
                                    val browserIntent = Intent(
                                        Intent.ACTION_VIEW,
                                        android.net.Uri.parse(response.getString("response"))
                                    )
                                    startActivity(browserIntent)
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
        }!!
        dashBoardSharedRecordRv.layoutManager =
            LinearLayoutManager(
                requireActivity().applicationContext,
                LinearLayoutManager.HORIZONTAL,
                false
            )
        dashBoardSharedRecordRv.itemAnimator = DefaultItemAnimator()
        dashBoardSharedRecordRv.adapter = sharedRecordsListAdapter


        //auto follow-up
        autoFollowUpViewAll =
            dashBoardFullModeTabView.findViewById(R.id.autoFollowUpViewAll)
        dashBoardAutoFollowUpRv =
            dashBoardFullModeTabView.findViewById(R.id.autoFollowUpRv)
        autoFollowUpSection =
            dashBoardFullModeTabView.findViewById(R.id.autoFollowUpSection)
        autoFollowUpListAdapter = DashBoardAutoFollowUpListAdapter(
            requireActivity(), dashBoardAutoFollowUpModel
        ) { v, type, parameter, videoServiceId, clinicServiceId, instantVideoServiceId, chatServiceId, patientId, patientName, instantVideoObject, instantVideoInfoObject ->
            if (globalClass!!.isOnline) {
                val separated = parameter.split("_".toRegex()).dropLastWhile { it.isEmpty() }
                    .toTypedArray()
                if (type.equals("BOOK_APPT", ignoreCase = true)) {
                    if (clinicServiceId == 1) {
                        BookAppointmentActivity.quickAppointmentFlag = 1
                        val intent = Intent(activity, BookAppointmentTimeSlotActivity::class.java)
                        intent.putExtra(
                            "doctorDetailsRootObjects",
                            doctorsDetailsRootObj.toString()
                        )
                        intent.putExtra("serviceId", videoServiceId)
                        intent.putExtra("patientId", patientId)
                        intent.putExtra("patientName", patientName)
                        intent.putExtra(
                            "quickAppointmentFlag",
                            BookAppointmentActivity.quickAppointmentFlag
                        )
                        intent.putExtra("clinicName", separated[0]) // added by dileep
                        intent.putExtra("followUpApptId", separated[1]) // added by dileep
                        startActivity(intent)
                    } else if (clinicServiceId == 3) {
                        BookAppointmentActivity.quickAppointmentFlag = 1
                        val intent = Intent(activity, BookAppointmentTimeSlotActivity::class.java)
                        intent.putExtra(
                            "doctorDetailsRootObjects",
                            doctorsDetailsRootObj.toString()
                        )
                        intent.putExtra("serviceId", clinicServiceId)
                        intent.putExtra("patientId", patientId)
                        intent.putExtra("patientName", patientName)
                        intent.putExtra(
                            "quickAppointmentFlag",
                            BookAppointmentActivity.quickAppointmentFlag
                        )
                        intent.putExtra("clinicName", separated[0]) // added by dileep
                        intent.putExtra("followUpApptId", separated[1]) // added by dileep
                        startActivity(intent)
                    }
                } else if (type.equals("FILE_URL", ignoreCase = true)) {
                    dashboardViewModel.getFileFromUrl(activity, parameter)
                        .observe(requireActivity()) { s ->
                            try {
                                val jsonObject = JSONObject(s)
                                if (jsonObject.getInt("status_code") == 200) {
                                    val response = JSONObject(s).getJSONObject("response")
                                    val browserIntent = Intent(
                                        Intent.ACTION_VIEW,
                                        android.net.Uri.parse(response.getString("response"))
                                    )
                                    startActivity(browserIntent)
                                } else {
                                    errorHandler(requireContext(), s)
                                }
                            } catch (e: JSONException) {
                                e.printStackTrace()
                            }
                        }
                }
            } else {
                globalClass!!.noInternetConnection.showDialog(activity)
            }
        }
        dashBoardAutoFollowUpRv.layoutManager =
            LinearLayoutManager(
                requireActivity().applicationContext,
                LinearLayoutManager.HORIZONTAL,
                false
            )
        dashBoardAutoFollowUpRv.itemAnimator = DefaultItemAnimator()
        dashBoardAutoFollowUpRv.adapter = autoFollowUpListAdapter
        //        Shared patient records view all
        sharedRecodsViewAll.setOnClickListener {
            if (globalClass!!.isOnline) {
                ZohoSalesIQ.Tracking.setCustomAction("HomeTab - Shared Records - View All")
                val intent = Intent(activity, PatientSharedRecordsActivity::class.java)
                startActivity(intent)
            } else {
                globalClass!!.noInternetConnection.showDialog(activity)
            }
        }
        //        Autofollow up view all
        autoFollowUpViewAll.setOnClickListener {
            if (globalClass!!.isOnline) {
                ZohoSalesIQ.Tracking.setCustomAction("HomeTab - Auto Follow-up - View All")
                val intent = Intent(activity, AutoFollowUpActivity::class.java)
                requireActivity().startActivity(intent)
            } else {
                globalClass!!.noInternetConnection.showDialog(activity)
            }
        }
        practicesDuration.setOnClickListener {
            if (globalClass!!.isOnline) {
                MyClinicGlobalClass.logUserActionEvent(
                    ApiUrls.doctorId,
                    getString(R.string.DashboardBookNewAppt),
                    null
                )
                showDialog(requireActivity())
            } else {
                globalClass!!.noInternetConnection.showDialog(activity)
            }
        }
        trainingScheduleSection.setOnClickListener({
            if (globalClass!!.isOnline) {
                MyClinicGlobalClass.logUserActionEvent(
                    ApiUrls.doctorId,
                    getString(R.string.DashboardRegisterOnlineTraining),
                    null
                )
                val myIntent = Intent(activity, TrainingScheduleActivity::class.java)
                requireActivity().startActivity(myIntent)
            } else {
                globalClass!!.noInternetConnection.showDialog(activity)
            }
        })
        trainingScheduleViewAll.setOnClickListener {
            if (globalClass!!.isOnline) {
                val myIntent = Intent(activity, TrainingScheduleActivity::class.java)
                requireActivity().startActivity(myIntent)
            } else {
                globalClass!!.noInternetConnection.showDialog(activity)
            }
        }
        if (DashBoardSharedRecordsListAdapter.shareRecordClickFlag == 1) {
            homeScrollView.post({
                homeScrollView.fullScroll(ScrollView.FOCUS_DOWN)
                DashBoardSharedRecordsListAdapter.shareRecordClickFlag = 0
            })
        }
        getDashBoardRibbonMessage()
        val sb = SpannableStringBuilder("Create Now")
        // Span to set text color to some RGB value
        val fcs = ForegroundColorSpan(android.graphics.Color.rgb(0, 167, 109))
        // Span to make text bold
        val bss = StyleSpan(android.graphics.Typeface.BOLD)
        // Set the text color for first 4 characters
        sb.setSpan(fcs, 0, sb.toString().length, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
        // make them also bold
        sb.setSpan(bss, 0, sb.toString().length, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
        create_account_ribbon.append(sb)
        create_account_ribbon.setOnClickListener {
            val intent = Intent(activity, PaymentSetupScreen::class.java)
            intent.putExtra("Doc_Name", MainActivity.docName)
            intent.putExtra("Doc_Email", MainActivity.docEmail)
            intent.putExtra("Doc_Phone", MainActivity.docPhone)
            requireActivity().startActivity(intent)
        }
        pullToRefresh.setOnRefreshListener {
            if (globalClass!!.isOnline) {
                when (AppConstants.durationSelectedValue) {
                    1 -> {
                        selectDuration = 1
                        selectedDuration = "All"
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            practicesDuration.text =
                                Html.fromHtml("<u>All</u>", Html.FROM_HTML_MODE_LEGACY)
                        } else {
                            practicesDuration.text = Html.fromHtml("<u>All</u>")
                        }
                        dashBoardAppointmentDetails(selectedDuration)
                    }
                    2 -> {
                        selectDuration = 2
                        selectedDuration = "Today"
                        practicesDuration.text = Html.fromHtml("<u>Today</u>")
                        dashBoardAppointmentDetails(selectedDuration)
                    }
                    3 -> {
                        selectDuration = 3
                        selectedDuration = "This Week"
                        practicesDuration.text = Html.fromHtml("<u>This Week</u>")
                        dashBoardAppointmentDetails(selectedDuration)
                    }
                    4 -> {
                        selectDuration = 4
                        selectedDuration = "This Month"
                        practicesDuration.text = Html.fromHtml("<u>This Month</u>")
                        dashBoardAppointmentDetails(selectedDuration)
                    }
                    5 -> {
                        selectDuration = 5
                        selectedDuration = "This Year"
                        practicesDuration.text = Html.fromHtml("<u>This Year</u>")
                        dashBoardAppointmentDetails(selectedDuration)
                    }
                    else -> {
                        dashBoardAppointmentDetails("Today")
                    }
                }
                //Removed training section due to latency issues
                trainingScheduleModel.clear()
                getBookedTraining()
                getUpComingTraining()
                quickActionItemList.clear()
                dashboardViewModel.getQuickActionDetails(activity)
                    .observe(requireActivity()) { s ->
                        try {
                            val jsonObject = JSONObject(s)
                            if (jsonObject.getInt("status_code") == 200) {
                                quickActionProgressbar.visibility = View.GONE
                                noDataMessageQuickAction.visibility = View.GONE
                                val response = JSONObject(s).getJSONObject("response")
                                val rootObj = response.optJSONObject("response")
                                val menuArr = rootObj!!.getJSONArray("quickLinks")
                                Log.d(
                                    "DashBoardQuickMenuResp",
                                    menuArr.toString()
                                )
                                val menuArrLenth = menuArr.length()
                                if (menuArrLenth > 0) {
                                    quickActionMainLayout.visibility = View.VISIBLE
                                    for (i in 0 until menuArrLenth) {
                                        val tempobj = menuArr.getJSONObject(i)
                                        var iconName = ""
                                        if (tempobj.getString("android_icon")
                                                .equals("ic_calender", ignoreCase = true)
                                        ) {
                                            iconName = "ic_book_calender"
                                        } else if (tempobj.getString("android_icon")
                                                .equals("ic_add_person", ignoreCase = true)
                                        ) {
                                            iconName = "ic_add_patient"
                                        } else if (tempobj.getString("android_icon").equals(
                                                "ic_calender_clock",
                                                ignoreCase = true
                                            )
                                        ) {
                                            iconName = "ic_block_time"
                                        } else if (tempobj.getString("android_icon").equals(
                                                "ic_share_article",
                                                ignoreCase = true
                                            )
                                        ) {
                                            iconName = "ic_share_text"
                                        } else if (tempobj.getString("android_icon")
                                                .equals("ic_share", ignoreCase = true)
                                        ) {
                                            iconName = "ic_share_video"
                                        } else if (tempobj.getString("android_icon")
                                                .equals("ic_send", ignoreCase = true)
                                        ) {
                                            iconName = "ic_send_msg"
                                        }
                                        val temp = DashBoardQuickActionViewItem(
                                            tempobj.getString("page_name"),
                                            iconName,
                                            tempobj.getInt("id"),
                                            tempobj.getInt("is_hidden_for_doctor_only")
                                        )
                                        quickActionItemList.add(temp)
                                    }


                                    // Set data adapter.
                                    dashBoardQuickActionRecyclerView.adapter =
                                        dashBoardQuickActionDataAdapter
                                } else {
                                    quickActionMainLayout.visibility = View.GONE
                                }
                            } else {
                                errorHandler(requireContext(), s)
                                quickActionProgressbar.visibility = View.GONE
                                noDataMessageQuickAction.visibility = View.VISIBLE
                            }
                        } catch (e: JSONException) {
                            Log.d("followUpError2", "followUpError2$e")
                            e.printStackTrace()
                            quickActionProgressbar.visibility = View.GONE
                            noDataMessageQuickAction.visibility = View.VISIBLE
                        }
                    }
                dashboardViewModel.getSharedAndFollowUpDetails(activity)
                    .observe(requireActivity()) { s ->
                        try {
                            shareRecordsModel.clear()
                            dashBoardAutoFollowUpModel.clear()
                            val jsonObject = JSONObject(s)
                            if (jsonObject.getInt("status_code") == 200) {
                                val response = JSONObject(s).getJSONObject("response")

                                //                        share records
                                val sharedRecordsArr = response.getJSONObject("response")
                                    .getJSONArray("shared_records")
                                Log.d(
                                    "Shared Records",
                                    sharedRecordsArr.length().toString() + ""
                                )
                                if (sharedRecordsArr.length() > 0) {
                                    sharedRecordsSection.visibility = View.VISIBLE
                                    if (sharedPref.isPrefExists("EMR")) {
                                        sharedRecordsSection.visibility = View.VISIBLE
                                    } else {
                                        sharedRecordsSection.visibility = View.GONE
                                    }
                                    dashboardSharedRecordProgressbar.visibility = View.GONE
                                    noDataMessageSharedByPatient.visibility = View.GONE
                                    sharedRecordsEmptyText.visibility = View.GONE
                                }
                                if (sharedRecordsArr.length() > 2) {
                                    sharedRecodsViewAll.visibility = View.VISIBLE
                                } else {
                                    sharedRecodsViewAll.visibility = View.GONE
                                }
                                val fieldDicArr = response.getJSONObject("response")
                                    .getJSONObject("field-dictionary")
                                if (sharedRecordsArr.length() > 0) {
                                    for (i in 0 until sharedRecordsArr.length()) {
                                        val sharedObj = sharedRecordsArr.getJSONObject(i)
                                        val model = DashBoardPatientRecordsModel()
                                        val catId = sharedObj.getJSONObject("recordinfo")
                                            .getJSONObject("records")
                                            .getJSONObject("category").getInt("id")
                                            .toString() + ""


                                        //------------by dileep-------------
                                        val catName = sharedObj.getJSONObject("recordinfo")
                                            .getJSONObject("records")
                                            .getJSONObject("category")
                                            .getString("category") + ""
                                        val recordDetailsObjects =
                                            sharedObj.getJSONObject("recordinfo")
                                                .getJSONObject("records")
                                        val sharedOnDateTime =
                                            recordDetailsObjects.getJSONObject("share_details")
                                                .getString("created_at")
                                        val recordDetailsArray = JSONArray()
                                        recordDetailsArray.put(sharedObj)
                                        val recordId =
                                            sharedObj.getInt("record_id") // added by dileep
                                        model.catId = catId //added by dileep
                                        model.categoryId = catId.toInt()
                                        model.fieldDictionary =
                                            fieldDicArr.toString() // added by dileep
                                        model.fieldDictionaryObject = fieldDicArr
                                        model.recordId = recordId // added by dileep
                                        model.catRecordData = recordDetailsArray.toString()
                                        model.recordDetailsObject = recordDetailsObjects
                                        model.sharedOnDateTime = sharedOnDateTime
                                        model.catName = catName
                                        model.created_At = sharedObj.getString("created_at")
                                        //-----------------end------------------
                                        if (fieldDicArr[catId] is JSONArray) {
                                            val fieldArr = fieldDicArr.getJSONArray(
                                                sharedObj.getJSONObject("recordinfo")
                                                    .getJSONObject("records")
                                                    .getJSONObject("category").getInt("id")
                                                    .toString() + ""
                                            )
                                            model.primaryKey =
                                                fieldArr.getJSONObject(0).getString("name")
                                            if (fieldArr.length() > 1) {
                                                model.secKey = fieldArr.getJSONObject(1)
                                                    .getString("name")
                                                if (sharedObj.getJSONObject("recordinfo")
                                                        .getJSONObject("records").has(
                                                            fieldArr.getJSONObject(1)
                                                                .getString("key")
                                                        )
                                                ) {
                                                    model.secData =
                                                        sharedObj.getJSONObject("recordinfo")
                                                            .getJSONObject("records")
                                                            .getString(
                                                                fieldArr.getJSONObject(1)
                                                                    .getString("key")
                                                            )
                                                } else {
                                                    model.secData = "-"
                                                }
                                            }
                                            if (fieldArr.length() > 2) {
                                                model.ternaryKey = fieldArr.getJSONObject(2)
                                                    .getString("name")
                                                if (!sharedObj.getJSONObject("recordinfo")
                                                        .getJSONObject("records").has(
                                                            fieldArr.getJSONObject(2)
                                                                .getString("key")
                                                        )
                                                ) {
                                                    Log.i("Empty", "Empty")
                                                } else {
                                                    model.ternaryData =
                                                        sharedObj.getJSONObject("recordinfo")
                                                            .getJSONObject("records")
                                                            .getString(
                                                                fieldArr.getJSONObject(2)
                                                                    .getString("key")
                                                            )
                                                }
                                            }
                                            if (sharedObj.getJSONObject("recordinfo")
                                                    .getJSONObject("records").has(
                                                        fieldArr.getJSONObject(0)
                                                            .getString("key")
                                                    )
                                            ) {
                                                model.primaryData =
                                                    sharedObj.getJSONObject("recordinfo")
                                                        .getJSONObject("records").getString(
                                                            fieldArr.getJSONObject(0)
                                                                .getString("key")
                                                        )
                                            }
                                            model.fileUrl = ""
                                            if (sharedObj.getJSONObject("recordinfo")
                                                    .getJSONObject("records").has("url")
                                            ) {
                                                model.fileUrl =
                                                    sharedObj.getJSONObject("recordinfo")
                                                        .getJSONObject("records")
                                                        .getString("url")
                                            }

                                            //use to set patient name
                                            model.recordName =
                                                sharedObj.getJSONObject("recordinfo")
                                                    .getJSONObject("records")
                                                    .getString("patient")
                                            shareRecordsModel.add(model)
                                        } else {
                                            val fieldArr = fieldDicArr.getJSONObject(
                                                sharedObj.getJSONObject("recordinfo")
                                                    .getJSONObject("records")
                                                    .getJSONObject("category").getInt("id")
                                                    .toString() + ""
                                            )
                                            model.primaryKey = fieldArr.getJSONObject("0")
                                                .getString("name")
                                            if (fieldArr.length() > 1) {
                                                model.secKey = fieldArr.getJSONObject("1")
                                                    .getString("name")
                                                if (sharedObj.getJSONObject("recordinfo")
                                                        .getJSONObject("records").has(
                                                            fieldArr.getJSONObject("1")
                                                                .getString("key")
                                                        )
                                                ) {
                                                    model.secData =
                                                        sharedObj.getJSONObject("recordinfo")
                                                            .getJSONObject("records")
                                                            .getString(
                                                                fieldArr.getJSONObject("1")
                                                                    .getString("key")
                                                            )
                                                } else {
                                                    model.secData = "-"
                                                }
                                            }
                                            if (fieldArr.length() > 2) {
                                                model.ternaryKey =
                                                    fieldArr.getJSONObject("2")
                                                        .getString("name")

                                                model.ternaryData =
                                                    sharedObj.getJSONObject("recordinfo")
                                                        .getJSONObject("records")
                                                        .getString(
                                                            fieldArr.getJSONObject("2")
                                                                .getString("key")
                                                        )
                                            }
                                            if (sharedObj.getJSONObject("recordinfo")
                                                    .getJSONObject("records").has(
                                                        fieldArr.getJSONObject("0")
                                                            .getString("key")
                                                    )
                                            ) model.primaryData =
                                                sharedObj.getJSONObject("recordinfo")
                                                    .getJSONObject("records").getString(
                                                        fieldArr.getJSONObject("0")
                                                            .getString("key")
                                                    )
                                            model.fileUrl = ""
                                            if (sharedObj.getJSONObject("recordinfo")
                                                    .getJSONObject("records").has("url")
                                            ) {
                                                model.fileUrl =
                                                    sharedObj.getJSONObject("recordinfo")
                                                        .getJSONObject("records")
                                                        .getString("url")
                                            }
                                            shareRecordsModel.add(model)
                                        }
                                    }
                                    sharedRecordsListAdapter.notifyDataSetChanged()
                                } else {
                                    dashboardSharedRecordProgressbar.visibility = View.GONE
                                    noDataMessageSharedByPatient.visibility = View.GONE
                                    sharedRecodsViewAll.visibility = View.GONE
                                    sharedRecordsEmptyText.visibility = View.VISIBLE
                                    sharedRecordsEmptyText.text =
                                        buildString {
                                            append("All records shared by patients will appear here")
                                        }
                                    if (sharedPref.isPrefExists("EMR")) {
                                        sharedRecordsSection.visibility = View.VISIBLE
                                    } else {
                                        sharedRecordsSection.visibility = View.GONE
                                    }
                                }


                                //                      Auto followUp data
                                val autoFollowUpArr = response.getJSONObject("response")
                                    .getJSONArray("followUpSubmissions")
                                if (autoFollowUpArr.length() > 0) {
                                    autoFollowUpSection.visibility = View.VISIBLE
                                    autoFollowUpEmptyText.visibility = View.GONE
                                    dashboardFollowUpProgressbar.visibility = View.GONE
                                    noDataMessageAutoFollowUp.visibility = View.GONE
                                }
                                if (autoFollowUpArr.length() > 2) {
                                    autoFollowUpViewAll.visibility = View.VISIBLE
                                } else {
                                    autoFollowUpViewAll.visibility = View.VISIBLE
                                }
                                if (autoFollowUpArr.length() > 0) {
                                    for (i in 0 until autoFollowUpArr.length()) {
                                        val autoFollowObj = autoFollowUpArr.getJSONObject(i)
                                        val model = DashBoardAutoFollowUpModel()
                                        model.patientMessage =
                                            autoFollowObj.getString("description")
                                        model.followUpId = autoFollowObj.getInt("id")
                                        if (autoFollowObj.isNull("how_are_you_feeling")) {
                                            model.conditionStatus = 0
                                        } else {
                                            model.conditionStatus =
                                                autoFollowObj.getInt("how_are_you_feeling")
                                        }
                                        if (autoFollowObj.isNull("is_following_instructions")) {
                                            model.followInstructionStatus = 0
                                        } else {
                                            model.followInstructionStatus =
                                                autoFollowObj.getInt("is_following_instructions")
                                        }
                                        model.followUpDate =
                                            autoFollowObj.getString("appt_datetime")// old

//                                        model.followUpDate = autoFollowObj.getJSONObject("appointment")
//                                            .getString("follow_up_datetime")
                                        model.submissionDate =
                                            autoFollowObj.getString("created_at")
                                        model.appointmentDate =
                                            autoFollowObj.getJSONObject("appointment")
                                                .getString("scheduled_start_time")
                                        model.clinicName =
                                            autoFollowObj.getJSONObject("appointment")
                                                .getJSONObject("order")
                                                .getJSONObject("products")
                                                .getJSONObject("prod_service")
                                                .getString("alias")
                                        model.clinicAddress =
                                            autoFollowObj.getJSONObject("appointment")
                                                .getJSONObject("order")
                                                .getJSONObject("products")
                                                .getJSONObject("prod_service")
                                                .getString("address")
                                        model.productServiceId =
                                            autoFollowObj.getJSONObject("appointment")
                                                .getJSONObject("order")
                                                .getJSONObject("products")
                                                .getJSONObject("prod_service")
                                                .getInt("service_id")
                                        model.patientName =
                                            autoFollowObj.getJSONObject("appointment")
                                                .getJSONObject("order")
                                                .getJSONObject("order_user")
                                                .getString("fname")
                                        model.patientId =
                                            autoFollowObj.getJSONObject("appointment")
                                                .getJSONObject("order")
                                                .getJSONObject("order_user").getInt("id")
                                        model.patientMessage =
                                            autoFollowObj.getString("description")
                                        model.isApptBooked =
                                            autoFollowObj.getInt("is_appointment_booked")
                                        model.fileUrl = autoFollowObj.getString("file_url")
                                        dashBoardAutoFollowUpModel.add(model)
                                    }
                                    autoFollowUpListAdapter.notifyDataSetChanged()
                                } else {
                                    dashboardFollowUpProgressbar.visibility = View.GONE
                                    autoFollowUpViewAll.visibility = View.GONE
                                    noDataMessageAutoFollowUp.visibility = View.GONE
                                    autoFollowUpEmptyText.visibility = View.VISIBLE
                                    autoFollowUpEmptyText.text =
                                        buildString {
                                            append("Follow-up data from your patients will appear here")
                                        }
                                }
                            } else {
                                errorHandler(requireContext(), s)
                            }
                        } catch (e: JSONException) {
                            Log.d("followUpError", "followUpError$e")
                            e.printStackTrace()
                        }
                    }

                //            get doctor details
                dashboardViewModel.getDoctorDetails(activity, ApiUrls.doctorId)
                    .observe(requireActivity()) { s ->
                        try {
                            doctorServiceArrayList.clear()
                            val jsonObject = JSONObject(s)
                            if (jsonObject.getInt("status_code") == 200) {
                                quickActionProgressbar.visibility = View.GONE
                                noDataMessageQuickAction.visibility = View.GONE
                                val response = JSONObject(s).getJSONObject("response")
                                Log.i("doctor deta full", response.toString())
                                doctorsDetailsRootObj =
                                    response.getJSONObject("response")
                                val userObject =
                                    doctorsDetailsRootObj.getJSONObject("user")
                                val serviceArray = userObject.getJSONArray("services")
                                if (serviceArray.length() > 0) {
                                    for (j in 0 until serviceArray.length()) {
                                        val serviceObject = serviceArray.getJSONObject(j)
                                        val bookAppointmentModel =
                                            AppointmentSlotListModel()
                                        if (serviceObject.getInt("id") == 2) {
                                            val intervention =
                                                doctorsDetailsRootObj["chat_product"]
                                            if (intervention is JSONArray) {
                                                // It's an array
                                                val chatServiceObject =
                                                    doctorsDetailsRootObj.getJSONArray(
                                                        "chat_product"
                                                    )
                                                Log.i("res", chatServiceObject.toString())
                                            } else if (intervention is JSONObject) {
                                                // It's an object
                                                val chatServiceObject =
                                                    doctorsDetailsRootObj.getJSONObject(
                                                        "chat_product"
                                                    )
                                                val chatServiceProdObject =
                                                    chatServiceObject.getJSONObject("prod_service")
                                                val prodId = chatServiceObject.getInt("id")
                                                val ServId =
                                                    chatServiceObject.getInt("dr_service_id")
                                                bookAppointmentModel.prodId = prodId
                                                bookAppointmentModel.servId = ServId
                                                bookAppointmentModel.price =
                                                    chatServiceObject.getInt("price")
                                                bookAppointmentModel.prodAliasName =
                                                    chatServiceProdObject.getString("alias")
                                                bookAppointmentModel.appointmentServiceID =
                                                    serviceObject.getInt("id")
                                                bookAppointmentModel.appointmentServiceName =
                                                    serviceObject.getString("service")
                                                bookAppointmentModel.appointmentServiceAlias =
                                                    serviceObject.getString("alias")
                                            }
                                        } else {
                                            bookAppointmentModel.appointmentServiceID =
                                                serviceObject.getInt("id")
                                            bookAppointmentModel.appointmentServiceName =
                                                serviceObject.getString("service")
                                            bookAppointmentModel.appointmentServiceAlias =
                                                serviceObject.getString("alias")
                                        }
                                        doctorServiceArrayList.add(
                                            bookAppointmentModel
                                        )
                                    }
                                    val intervention =
                                        doctorsDetailsRootObj["inst_video"]
                                    if (intervention is JSONArray) {
                                        // It's an array
                                        val instantVideoObject =
                                            doctorsDetailsRootObj.getJSONArray(
                                                "inst_video"
                                            )
                                        Log.i("res", instantVideoObject.toString())
                                    } else if (intervention is JSONObject) {
                                        // It's an object
                                        val instantVideoObject =
                                            doctorsDetailsRootObj.getJSONObject(
                                                "inst_video"
                                            )
                                        val bookAppointmentModel =
                                            AppointmentSlotListModel()
                                        bookAppointmentModel.instantVideoJsonObject =
                                            instantVideoObject
                                        doctorServiceArrayList.add(
                                            bookAppointmentModel
                                        )
                                    }
                                    if (doctorsDetailsRootObj.has("inst_video_info")) {
                                        //                                            //Checking address Key Present or not
                                        val interventionOne =
                                            doctorsDetailsRootObj["inst_video_info"]
                                        if (interventionOne is JSONArray) {
                                            // It's an array
                                            val instantVideoObject =
                                                doctorsDetailsRootObj.getJSONArray(
                                                    "inst_video_info"
                                                )
                                            Log.i("res", instantVideoObject.toString())
                                        } else if (interventionOne is JSONObject) {
                                            // It's an object
                                            val instantVideoInfoObject =
                                                doctorsDetailsRootObj.getJSONObject(
                                                    "inst_video_info"
                                                )
                                            val bookAppointmentModel =
                                                AppointmentSlotListModel()
                                            //
                                            bookAppointmentModel.instantVideoInfoJsonObject =
                                                instantVideoInfoObject
                                            doctorServiceArrayList.add(
                                                bookAppointmentModel
                                            )
                                        }
                                    } else {
                                        //Do Your Staff
                                    }
                                }
                                pullToRefresh.isRefreshing = false
                            } else {
                                Toast.makeText(
                                    activity,
                                    jsonObject.getString("response"),
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        } catch (e: JSONException) {
                            Log.d("doctorDetails", "doctorDetails$e")
                            e.printStackTrace()
                        }
                    }
            } else {
                globalClass!!.noInternetConnection.showDialog(activity)
            }
        }
        if (globalClass!!.isOnline) {
            when (AppConstants.durationSelectedValue) {
                1 -> {
                    selectDuration = 1
                    selectedDuration = "All"
                    practicesDuration.text = Html.fromHtml("<u>All</u>")
                    dashBoardAppointmentDetails(selectedDuration)
                }
                2 -> {
                    selectDuration = 2
                    selectedDuration = "Today"
                    practicesDuration.text = Html.fromHtml("<u>Today</u>")
                    dashBoardAppointmentDetails(selectedDuration)
                }
                3 -> {
                    selectDuration = 3
                    selectedDuration = "This Week"
                    practicesDuration.text = Html.fromHtml("<u>This Week</u>")
                    dashBoardAppointmentDetails(selectedDuration)
                }
                4 -> {
                    selectDuration = 4
                    selectedDuration = "This Month"
                    practicesDuration.text = Html.fromHtml("<u>This Month</u>")
                    dashBoardAppointmentDetails(selectedDuration)
                }
                5 -> {
                    selectDuration = 5
                    selectedDuration = "This Year"
                    practicesDuration.text = Html.fromHtml("<u>This Year</u>")
                    dashBoardAppointmentDetails(selectedDuration)
                }
                else -> {
                    dashBoardAppointmentDetails("Today")
                }
            }
            //Removed training section due to latency issues
            trainingScheduleModel.clear()
            getBookedTraining()
            getUpComingTraining()
            quickActionItemList.clear()
            dashboardViewModel.getQuickActionDetails(activity)
                .observe(requireActivity()) { s ->
                    try {
                        val jsonObject = JSONObject(s)
                        if (jsonObject.getInt("status_code") == 200) {
                            quickActionProgressbar.visibility = View.GONE
                            noDataMessageQuickAction.visibility = View.GONE
                            val response = JSONObject(s).getJSONObject("response")
                            val rootObj = response.optJSONObject("response")
                            val menuArr = rootObj!!.getJSONArray("quickLinks")
                            Log.d("DashBoardQuickMenuResp", menuArr.toString())
                            val menuArrLenth = menuArr.length()
                            if (menuArrLenth > 0) {
                                quickActionMainLayout.visibility = View.VISIBLE
                                for (i in 0 until menuArrLenth) {
                                    val tempobj = menuArr.getJSONObject(i)
                                    var iconName = ""
                                    if (tempobj.getString("android_icon")
                                            .equals("ic_calender", ignoreCase = true)
                                    ) {
                                        iconName = "ic_book_calender"
                                    } else if (tempobj.getString("android_icon")
                                            .equals("ic_add_person", ignoreCase = true)
                                    ) {
                                        iconName = "ic_add_patient"
                                    } else if (tempobj.getString("android_icon")
                                            .equals("ic_calender_clock", ignoreCase = true)
                                    ) {
                                        iconName = "ic_block_time"
                                    } else if (tempobj.getString("android_icon")
                                            .equals("ic_share_article", ignoreCase = true)
                                    ) {
                                        iconName = "ic_share_text"
                                    } else if (tempobj.getString("android_icon")
                                            .equals("ic_share", ignoreCase = true)
                                    ) {
                                        iconName = "ic_share_video"
                                    } else if (tempobj.getString("android_icon")
                                            .equals("ic_send", ignoreCase = true)
                                    ) {
                                        iconName = "ic_send_msg"
                                    }
                                    val temp = DashBoardQuickActionViewItem(
                                        tempobj.getString("page_name"),
                                        iconName,
                                        tempobj.getInt("id"),
                                        tempobj.getInt("is_hidden_for_doctor_only")
                                    )
                                    quickActionItemList.add(temp)
                                }

                                // Set data adapter.
                                dashBoardQuickActionRecyclerView.adapter =
                                    dashBoardQuickActionDataAdapter
                            } else {
                                quickActionMainLayout.visibility = View.GONE
                            }
                        } else {
                            errorHandler(requireContext(), s)
                            quickActionProgressbar.visibility = View.GONE
                            noDataMessageQuickAction.visibility = View.VISIBLE
                        }
                    } catch (e: JSONException) {
                        Log.d("followUpError2", "followUpError2$e")
                        e.printStackTrace()
                        quickActionProgressbar.visibility = View.GONE
                        noDataMessageQuickAction.visibility = View.VISIBLE
                    }
                }
            dashboardViewModel.getSharedAndFollowUpDetails(activity)
                .observe(requireActivity()) { s ->
                    try {
                        shareRecordsModel.clear()
                        dashBoardAutoFollowUpModel.clear()
                        val jsonObject = JSONObject(s)
                        if (jsonObject.getInt("status_code") == 200) {
                            val response = JSONObject(s).getJSONObject("response")

                            //                        share records
                            val sharedRecordsArr = response.getJSONObject("response")
                                .getJSONArray("shared_records")
                            Log.d(
                                "Shared Records",
                                sharedRecordsArr.length().toString() + ""
                            )
                            if (sharedRecordsArr.length() > 0) {
                                sharedRecordsSection.visibility = View.VISIBLE
                                if (sharedPref.isPrefExists("EMR")) {
                                    sharedRecordsSection.visibility = View.VISIBLE
                                } else {
                                    sharedRecordsSection.visibility = View.GONE
                                }
                                dashboardSharedRecordProgressbar.visibility = View.GONE
                                noDataMessageSharedByPatient.visibility = View.GONE
                                sharedRecordsEmptyText.visibility = View.GONE
                            }
                            if (sharedRecordsArr.length() > 2) {
                                sharedRecodsViewAll.visibility = View.VISIBLE
                            } else {
                                sharedRecodsViewAll.visibility = View.GONE
                            }
                            val fieldDicArr = response.getJSONObject("response")
                                .getJSONObject("field-dictionary")
                            if (sharedRecordsArr.length() > 0) {
                                for (i in 0 until sharedRecordsArr.length()) {
                                    val sharedObj = sharedRecordsArr.getJSONObject(i)
                                    val model = DashBoardPatientRecordsModel()
                                    val catId = sharedObj.getJSONObject("recordinfo")
                                        .getJSONObject("records").getJSONObject("category")
                                        .getInt("id").toString() + ""


                                    //------------by dileep-------------
                                    val catName = sharedObj.getJSONObject("recordinfo")
                                        .getJSONObject("records").getJSONObject("category")
                                        .getString("category") + ""
                                    val recordDetailsObjects =
                                        sharedObj.getJSONObject("recordinfo")
                                            .getJSONObject("records")
                                    val sharedOnDateTime =
                                        recordDetailsObjects.getJSONObject("share_details")
                                            .getString("created_at")
                                    val recordDetailsArray = JSONArray()
                                    recordDetailsArray.put(sharedObj)
                                    val recordId =
                                        sharedObj.getInt("record_id") // added by dileep
                                    model.catId = catId //added by dileep
                                    model.categoryId = catId.toInt()
                                    model.fieldDictionary =
                                        fieldDicArr.toString() // added by dileep
                                    model.fieldDictionaryObject = fieldDicArr
                                    model.recordId = recordId // added by dileep
                                    model.catRecordData = recordDetailsArray.toString()
                                    model.recordDetailsObject = recordDetailsObjects
                                    model.sharedOnDateTime = sharedOnDateTime
                                    model.catName = catName
                                    model.created_At = sharedObj.getString("created_at")
                                    //-----------------end------------------
                                    if (fieldDicArr[catId] is JSONArray) {
                                        val fieldArr = fieldDicArr.getJSONArray(
                                            sharedObj.getJSONObject("recordinfo")
                                                .getJSONObject("records")
                                                .getJSONObject("category").getInt("id")
                                                .toString() + ""
                                        )
                                        model.primaryKey =
                                            fieldArr.getJSONObject(0).getString("name")
                                        if (fieldArr.length() > 1) {
                                            model.secKey =
                                                fieldArr.getJSONObject(1).getString("name")
                                            if (sharedObj.getJSONObject("recordinfo")
                                                    .getJSONObject("records").has(
                                                        fieldArr.getJSONObject(1)
                                                            .getString("key")
                                                    )
                                            ) {
                                                model.secData =
                                                    sharedObj.getJSONObject("recordinfo")
                                                        .getJSONObject("records").getString(
                                                            fieldArr.getJSONObject(1)
                                                                .getString("key")
                                                        )
                                            } else {
                                                model.secData = "-"
                                            }
                                        }
                                        if (fieldArr.length() > 2) {
                                            model.ternaryKey =
                                                fieldArr.getJSONObject(2).getString("name")
                                            if (!sharedObj.getJSONObject("recordinfo")
                                                    .getJSONObject("records").has(
                                                        fieldArr.getJSONObject(2)
                                                            .getString("key")
                                                    )
                                            ) {
                                                Log.i("NullObj", "Null Obj")
                                            } else {
                                                model.ternaryData =
                                                    sharedObj.getJSONObject("recordinfo")
                                                        .getJSONObject("records").getString(
                                                            fieldArr.getJSONObject(2)
                                                                .getString("key")
                                                        )
                                            }
                                        }
                                        if (sharedObj.getJSONObject("recordinfo")
                                                .getJSONObject("records")
                                                .has(fieldArr.getJSONObject(0).getString("key"))
                                        ) {
                                            model.primaryData =
                                                sharedObj.getJSONObject("recordinfo")
                                                    .getJSONObject("records").getString(
                                                        fieldArr.getJSONObject(0)
                                                            .getString("key")
                                                    )
                                        }
                                        model.fileUrl = ""
                                        if (sharedObj.getJSONObject("recordinfo")
                                                .getJSONObject("records").has("url")
                                        ) {
                                            model.fileUrl =
                                                sharedObj.getJSONObject("recordinfo")
                                                    .getJSONObject("records").getString("url")
                                        }

                                        //use to set patient name
                                        model.recordName = sharedObj.getJSONObject("recordinfo")
                                            .getJSONObject("records").getString("patient")
                                        shareRecordsModel.add(model)
                                    } else {
                                        val fieldArr = fieldDicArr.getJSONObject(
                                            sharedObj.getJSONObject("recordinfo")
                                                .getJSONObject("records")
                                                .getJSONObject("category").getInt("id")
                                                .toString() + ""
                                        )
                                        model.primaryKey =
                                            fieldArr.getJSONObject("0").getString("name")
                                        if (fieldArr.length() > 1) {
                                            model.secKey =
                                                fieldArr.getJSONObject("1").getString("name")
                                            if (sharedObj.getJSONObject("recordinfo")
                                                    .getJSONObject("records").has(
                                                        fieldArr.getJSONObject("1")
                                                            .getString("key")
                                                    )
                                            ) {
                                                model.secData =
                                                    sharedObj.getJSONObject("recordinfo")
                                                        .getJSONObject("records").getString(
                                                            fieldArr.getJSONObject("1")
                                                                .getString("key")
                                                        )
                                            } else {
                                                model.secData = "-"
                                            }
                                            //                                            model.setSecData(sharedObj.getJSONObject("recordinfo").getJSONObject("records").getString(fieldArr.getJSONObject("1").getString("key")));
                                        }
                                        if (fieldArr.length() > 2) {
                                            model.ternaryKey =
                                                fieldArr.getJSONObject("2").getString("name")

                                            // String jsonValue = fieldArr.getJSONObject("2").getString("key");
                                            model.ternaryData =
                                                sharedObj.getJSONObject("recordinfo")
                                                    .getJSONObject("records").getString(
                                                        fieldArr.getJSONObject("2")
                                                            .getString("key")
                                                    )
                                        }
                                        if (sharedObj.getJSONObject("recordinfo")
                                                .getJSONObject("records").has(
                                                    fieldArr.getJSONObject("0").getString("key")
                                                )
                                        ) model.primaryData =
                                            sharedObj.getJSONObject("recordinfo")
                                                .getJSONObject("records").getString(
                                                    fieldArr.getJSONObject("0").getString("key")
                                                )
                                        model.fileUrl = ""
                                        if (sharedObj.getJSONObject("recordinfo")
                                                .getJSONObject("records").has("url")
                                        ) {
                                            model.fileUrl =
                                                sharedObj.getJSONObject("recordinfo")
                                                    .getJSONObject("records").getString("url")
                                        }
                                        shareRecordsModel.add(model)
                                    }
                                }
                                sharedRecordsListAdapter.notifyDataSetChanged()
                            } else {
                                dashboardSharedRecordProgressbar.visibility = View.GONE
                                noDataMessageSharedByPatient.visibility = View.GONE
                                sharedRecodsViewAll.visibility = View.GONE
                                sharedRecordsEmptyText.visibility = View.VISIBLE
                                sharedRecordsEmptyText.text =
                                    buildString {
                                        append("All records shared by patients will appear here")
                                    }
                                if (sharedPref.isPrefExists("EMR")) {
                                    sharedRecordsSection.visibility = View.VISIBLE
                                } else {
                                    sharedRecordsSection.visibility = View.GONE
                                }
                            }


                            //                      Auto followUp data
                            val autoFollowUpArr = response.getJSONObject("response")
                                .getJSONArray("followUpSubmissions")
                            if (autoFollowUpArr.length() > 0) {
                                autoFollowUpSection.visibility = View.VISIBLE
                                autoFollowUpEmptyText.visibility = View.GONE
                                dashboardFollowUpProgressbar.visibility = View.GONE
                                noDataMessageAutoFollowUp.visibility = View.GONE
                            }
                            if (autoFollowUpArr.length() > 2) {
                                autoFollowUpViewAll.visibility = View.VISIBLE
                            } else {
                                autoFollowUpViewAll.visibility = View.VISIBLE
                            }
                            if (autoFollowUpArr.length() > 0) {
                                for (i in 0 until autoFollowUpArr.length()) {
                                    val autoFollowObj = autoFollowUpArr.getJSONObject(i)
                                    val model = DashBoardAutoFollowUpModel()
                                    model.patientMessage =
                                        autoFollowObj.getString("description")
                                    model.followUpId = autoFollowObj.getInt("id")
                                    if (autoFollowObj.isNull("how_are_you_feeling")) {
                                        model.conditionStatus = 0
                                    } else {
                                        model.conditionStatus =
                                            autoFollowObj.getInt("how_are_you_feeling")
                                    }
                                    if (autoFollowObj.isNull("is_following_instructions")) {
                                        model.followInstructionStatus = 0
                                    } else {
                                        model.followInstructionStatus =
                                            autoFollowObj.getInt("is_following_instructions")
                                    }
                                    model.followUpDate =
                                        autoFollowObj.getString("appt_datetime")
                                    model.submissionDate = autoFollowObj.getString("created_at")
                                    model.appointmentDate =
                                        autoFollowObj.getJSONObject("appointment")
                                            .getString("scheduled_start_time")
                                    model.clinicName =
                                        autoFollowObj.getJSONObject("appointment")
                                            .getJSONObject("order").getJSONObject("products")
                                            .getJSONObject("prod_service").getString("alias")
                                    model.clinicAddress =
                                        autoFollowObj.getJSONObject("appointment")
                                            .getJSONObject("order").getJSONObject("products")
                                            .getJSONObject("prod_service").getString("address")
                                    model.productServiceId =
                                        autoFollowObj.getJSONObject("appointment")
                                            .getJSONObject("order").getJSONObject("products")
                                            .getJSONObject("prod_service").getInt("service_id")
                                    model.patientName =
                                        autoFollowObj.getJSONObject("appointment")
                                            .getJSONObject("order").getJSONObject("order_user")
                                            .getString("fname")
                                    model.patientId = autoFollowObj.getJSONObject("appointment")
                                        .getJSONObject("order").getJSONObject("order_user")
                                        .getInt("id")
                                    model.patientMessage =
                                        autoFollowObj.getString("description")
                                    model.isApptBooked =
                                        autoFollowObj.getInt("is_appointment_booked")
                                    model.fileUrl = autoFollowObj.getString("file_url")
                                    dashBoardAutoFollowUpModel.add(model)
                                }
                                autoFollowUpListAdapter.notifyDataSetChanged()
                            } else {
                                dashboardFollowUpProgressbar.visibility = View.GONE
                                autoFollowUpViewAll.visibility = View.GONE
                                noDataMessageAutoFollowUp.visibility = View.GONE
                                autoFollowUpEmptyText.visibility = View.VISIBLE
                                autoFollowUpEmptyText.text =
                                    buildString {
                                        append("Follow-up data from your patients will appear here")
                                    }
                            }
                        } else {
                            errorHandler(requireContext(), s)
                        }
                    } catch (e: JSONException) {
                        Log.d("followUpError", "followUpError$e")
                        e.printStackTrace()
                    }
                }

//            get doctor details
            dashboardViewModel.getDoctorDetails(activity, ApiUrls.doctorId)
                .observe(requireActivity()) { s ->
                    try {
                        doctorServiceArrayList.clear()
                        val jsonObject = JSONObject(s)
                        if (jsonObject.getInt("status_code") == 200) {
                            quickActionProgressbar.visibility = View.GONE
                            noDataMessageQuickAction.visibility = View.GONE
                            val response = JSONObject(s).getJSONObject("response")
                            Log.i("doctor deta full", response.toString())
                            doctorsDetailsRootObj =
                                response.getJSONObject("response")
                            val userObject =
                                doctorsDetailsRootObj.getJSONObject("user")
                            val serviceArray = userObject.getJSONArray("services")
                            if (serviceArray.length() > 0) {
                                for (j in 0 until serviceArray.length()) {
                                    val serviceObject = serviceArray.getJSONObject(j)
                                    val bookAppointmentModel = AppointmentSlotListModel()
                                    if (serviceObject.getInt("id") == 2) {
                                        val intervention =
                                            doctorsDetailsRootObj["chat_product"]
                                        when (intervention) {
                                            is JSONArray -> {
                                                // It's an array
                                                val chatServiceObject =
                                                    doctorsDetailsRootObj.getJSONArray(
                                                        "chat_product"
                                                    )
                                            }
                                            is JSONObject -> {
                                                // It's an object
                                                val chatServiceObject =
                                                    doctorsDetailsRootObj.getJSONObject(
                                                        "chat_product"
                                                    )
                                                val chatServiceProdObject =
                                                    chatServiceObject.getJSONObject("prod_service")
                                                val prodId = chatServiceObject.getInt("id")
                                                val ServId =
                                                    chatServiceObject.getInt("dr_service_id")
                                                bookAppointmentModel.prodId = prodId
                                                bookAppointmentModel.servId = ServId
                                                bookAppointmentModel.price =
                                                    chatServiceObject.getInt("price")
                                                bookAppointmentModel.prodAliasName =
                                                    chatServiceProdObject.getString("alias")
                                                bookAppointmentModel.appointmentServiceID =
                                                    serviceObject.getInt("id")
                                                bookAppointmentModel.appointmentServiceName =
                                                    serviceObject.getString("service")
                                                bookAppointmentModel.appointmentServiceAlias =
                                                    serviceObject.getString("alias")
                                            }
                                            else -> {
                                                // It's something else, like a string or number
                                            }
                                        }
                                    } else {
                                        bookAppointmentModel.appointmentServiceID =
                                            serviceObject.getInt("id")
                                        bookAppointmentModel.appointmentServiceName =
                                            serviceObject.getString("service")
                                        bookAppointmentModel.appointmentServiceAlias =
                                            serviceObject.getString("alias")
                                    }
                                    doctorServiceArrayList.add(
                                        bookAppointmentModel
                                    )
                                }
                                val intervention =
                                    doctorsDetailsRootObj["inst_video"]
                                when (intervention) {
                                    is JSONArray -> {
                                        // It's an array
                                        val instantVideoObject =
                                            doctorsDetailsRootObj.getJSONArray("inst_video")
                                    }
                                    is JSONObject -> {
                                        // It's an object
                                        val instantVideoObject =
                                            doctorsDetailsRootObj.getJSONObject("inst_video")
                                        val bookAppointmentModel = AppointmentSlotListModel()
                                        bookAppointmentModel.instantVideoJsonObject =
                                            instantVideoObject
                                        doctorServiceArrayList.add(
                                            bookAppointmentModel
                                        )
                                    }
                                    else -> {
                                        // It's something else, like a string or number
                                    }
                                }
                                val interventionOne =
                                    doctorsDetailsRootObj["inst_video_info"]
                                when (interventionOne) {
                                    is JSONArray -> {
                                        // It's an array
                                        val instantVideoObject =
                                            doctorsDetailsRootObj.getJSONArray("inst_video_info")
                                    }
                                    is JSONObject -> {
                                        // It's an object
                                        val instantVideoInfoObject =
                                            doctorsDetailsRootObj.getJSONObject("inst_video_info")
                                        val bookAppointmentModel = AppointmentSlotListModel()
                                        //
                                        bookAppointmentModel.instantVideoInfoJsonObject =
                                            instantVideoInfoObject
                                        doctorServiceArrayList.add(
                                            bookAppointmentModel
                                        )
                                    }
                                    else -> {
                                        // It's something else, like a string or number
                                    }
                                }
                            }
                        } else {
                            errorHandler(requireContext(), s)
                        }
                    } catch (e: JSONException) {
                        Log.d("doctorDetails", "doctorDetails$e")
                        e.printStackTrace()
                    }
                }
        } else {
            globalClass!!.noInternetConnection.showDialog(activity)
        }
        return dashBoardFullModeTabView
    }

    fun showDialog(activity: Activity) {
        val dialog = Dialog(activity)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.dialog_dashboard_summary_listview)
        val cancelButtonDialog = dialog.findViewById<View>(R.id.dialogDurationCancel) as ImageView
        val all = dialog.findViewById<View>(R.id.all) as RadioButton
        val today = dialog.findViewById<View>(R.id.today) as RadioButton
        val thisWeek = dialog.findViewById<View>(R.id.thisWeek) as RadioButton
        val thisMonth = dialog.findViewById<View>(R.id.thisMonth) as RadioButton
        val thisYear = dialog.findViewById<View>(R.id.thisYear) as RadioButton
        val allLayout = dialog.findViewById<View>(R.id.allLayout) as RelativeLayout
        val todayLayout = dialog.findViewById<View>(R.id.todayLayout) as RelativeLayout
        val thisWeekLayout = dialog.findViewById<View>(R.id.thisWeekLayout) as RelativeLayout
        val thisMonthLayout = dialog.findViewById<View>(R.id.thisMonthLayout) as RelativeLayout
        val thisYearLayout = dialog.findViewById<View>(R.id.thisYearLayout) as RelativeLayout
        when (selectDuration) {
            1 -> {
                all.isChecked = true
                all.setTextColor(ContextCompat.getColor(activity, R.color.colorAccent))
            }
            2 -> {
                today.isChecked = true
                today.setTextColor(ContextCompat.getColor(activity, R.color.colorAccent))
            }
            3 -> {
                thisWeek.isChecked = true
                thisWeek.setTextColor(ContextCompat.getColor(activity, R.color.colorAccent))
            }
            4 -> {
                thisMonth.isChecked = true
                thisMonth.setTextColor(ContextCompat.getColor(activity, R.color.colorAccent))
            }
            5 -> {
                thisYear.isChecked = true
                thisYear.setTextColor(ContextCompat.getColor(activity, R.color.colorAccent))
            }
            else -> {
                today.isChecked = true
                today.setTextColor(ContextCompat.getColor(activity, R.color.colorAccent))
            }
        }
/*
        allLayout.setOnClickListener {
            all.isChecked = true
            today.isChecked = false
            thisWeek.isChecked = false
            thisMonth.isChecked = false
            thisYear.isChecked = false
            selectDuration = 1
            AppConstants.durationSelectedValue = selectDuration
            all.setTextColor(ContextCompat.getColor(activity, R.color.colorAccent))
            dialog.dismiss()
            selectedDuration = "All"
            practicesDuration.text = Html.fromHtml("<u>All</u>")
            filterAppointment(selectedDuration)
        }
*/
        todayLayout.setOnClickListener {
            all.isChecked = false
            today.isChecked = true
            thisWeek.isChecked = false
            thisMonth.isChecked = false
            thisYear.isChecked = false
            selectDuration = 2
            AppConstants.durationSelectedValue = selectDuration
            today.setTextColor(ContextCompat.getColor(activity, R.color.colorAccent))
            dialog.dismiss()
            selectedDuration = "Today"
            practicesDuration.text = Html.fromHtml("<u>Today</u>")
            filterAppointment(selectedDuration)
        }
        thisWeekLayout.setOnClickListener {
            all.isChecked = false
            today.isChecked = false
            thisWeek.isChecked = true
            thisMonth.isChecked = false
            thisYear.isChecked = false
            selectDuration = 3
            AppConstants.durationSelectedValue = selectDuration
            thisWeek.setTextColor(ContextCompat.getColor(activity, R.color.colorAccent))
            dialog.dismiss()
            selectedDuration = "This Week"
            practicesDuration.text = Html.fromHtml("<u>This Week</u>")
            filterAppointment(selectedDuration)
        }
        thisMonthLayout.setOnClickListener {
            all.isChecked = false
            today.isChecked = false
            thisWeek.isChecked = false
            thisMonth.isChecked = true
            thisYear.isChecked = false
            selectDuration = 4
            AppConstants.durationSelectedValue = selectDuration
            thisMonth.setTextColor(ContextCompat.getColor(activity, R.color.colorAccent))
            dialog.dismiss()
            selectedDuration = "This Month"
            practicesDuration.text = Html.fromHtml("<u>This Month</u>")
            filterAppointment(selectedDuration)
        }
        thisYearLayout.setOnClickListener {
            all.isChecked = false
            today.isChecked = false
            thisWeek.isChecked = false
            thisMonth.isChecked = false
            thisYear.isChecked = true
            selectDuration = 5
            AppConstants.durationSelectedValue = selectDuration
            thisYear.setTextColor(ContextCompat.getColor(activity, R.color.colorAccent))
            dialog.dismiss()
            selectedDuration = "This Year"
            practicesDuration.text = Html.fromHtml("<u>This Year</u>")
            filterAppointment(selectedDuration)
        }
        cancelButtonDialog.setOnClickListener { dialog.dismiss() }
        dialog.show()
    }

    fun getBookedTraining() {
        //            get trainng schedule
        Log.d("getBookTrainingSize", "getBookTrainingSize" + trainingScheduleModel.size)
        dashboardViewModel.getBookedTrainingDetails(activity, "50", "", "", "1", "", "").observe(
            requireActivity()
        ) { s ->
            try {
                val jsonObject = JSONObject(s)
                Log.d("getBookTraining", "getBookTraining$jsonObject")
                if (jsonObject.getInt("status_code") == 200) {
                    val response = JSONObject(s).getJSONObject("response")
                    val rootObj = response.getJSONObject("response")
                    val upcomingTrainingArray = rootObj.getJSONArray("data")
                    if (upcomingTrainingArray.length() > 0) {
                        for (j in 0 until upcomingTrainingArray.length()) {
                            val upcomingTrainingObject =
                                upcomingTrainingArray.getJSONObject(j)
                            val appointmentObjectTwo =
                                upcomingTrainingObject.getJSONArray("training_schedules")
                            for (i in 0 until appointmentObjectTwo.length()) {
                                val tempobj = appointmentObjectTwo.getJSONObject(i)
                                if (upcomingTrainingObject.getInt("status") == 1 || upcomingTrainingObject.getInt(
                                        "status"
                                    ) == 2
                                ) {
                                    dashboardTrainingScheduleProgressbar.visibility =
                                        View.GONE
                                    trainingSection!!.visibility = View.VISIBLE
                                    trainingScheduleSection.visibility = View.GONE
                                    val model = TrainingScheduleModel()
                                    model.id = upcomingTrainingObject.getInt("id")
                                    model.traingScheduleId =
                                        upcomingTrainingObject.getInt("training_schedule_id")
                                    model.status = upcomingTrainingObject.getInt("status")
                                    model.topic = tempobj.getString("title")
                                    model.trainingDescription =
                                        tempobj.getString("description")
                                    model.topicDate = tempobj.getString("start_date_time")
                                    trainingScheduleModel.add(model)
                                    break
                                } else {
                                    trainingSection!!.visibility = View.GONE
                                    trainingScheduleSection.visibility = View.VISIBLE
                                    dashboardTrainingScheduleProgressbar.visibility =
                                        View.GONE
                                }
                            }
                            if (upcomingTrainingObject.getInt("status") == 1 || upcomingTrainingObject.getInt(
                                    "status"
                                ) == 2
                            ) {
                                break
                            }
                        }
                        trainingScheduleListAdapter!!.notifyDataSetChanged()
                    } else {
                        trainingSection!!.visibility = View.GONE
                        trainingScheduleSection.visibility = View.VISIBLE
                        dashboardTrainingScheduleProgressbar.visibility = View.GONE
                    }
                } else {
                    dashboardTrainingScheduleProgressbar.visibility = View.GONE
                    errorHandler(requireContext(), s)
                }
            } catch (e: JSONException) {
                dashboardTrainingScheduleProgressbar.visibility = View.GONE
                e.printStackTrace()
            }
        }
    }

    fun getUpComingTraining() {
        //            get trainng schedule
        dashboardViewModel.getUpComingTrainingDetails(activity, "50", "", "", "1", "", "")
            .observe(requireActivity()) { s ->
                try {
                    val jsonObject = JSONObject(s)
                    Log.d("upcomingTraining", "upcomingTraining$jsonObject")
                    if (jsonObject.getInt("status_code") == 200) {
                        val response = JSONObject(s).getJSONObject("response")
                        val rootObj = response.getJSONObject("response")
                        val upcomingTrainingArray = rootObj.getJSONArray("data")
                        if (upcomingTrainingArray.length() > 0) {
                            trainingScheduleViewAll.visibility = View.VISIBLE
                        } else {
                            trainingScheduleViewAll.visibility = View.VISIBLE
                        }
                    } else {
                        errorHandler(requireContext(), s)
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }
    }


    fun dashBoardAppointmentDetails(duration: String?) {
        dashboardViewModel.getDashBoardAppointmentDetails(activity, duration)
            .observe(requireActivity()) { s ->
                try {
                    AppConstants.clinicCount = 0
                    dashBoardApptListModelList.clear()
                    dashBoardApptListModelListAll.clear()
                    val jsonObject = JSONObject(s)
                    if (jsonObject.getInt("status_code") == 200) {
                        practiceProgressbar.visibility = View.GONE
                        val response = JSONObject(s).getJSONObject("response")
                        val rootObj = response.optJSONObject("response")
                        val clinicArr = rootObj!!.getJSONArray("clinic")
                        Log.d("DashBoarAppointmentRes", clinicArr.toString())
                        if (clinicArr.length() > 0) {
                            for (i in 0..0) {
                                val tempobj = clinicArr.getJSONObject(i)
                                val temp = DashBoardApptListModel()
                                temp.clinicName = tempobj.getString("clinic_name")
                                temp.productId = tempobj.getInt("product_id")
                                temp.doctorServiceId = tempobj.getInt("doctor_service_id")
                                temp.apptCount = tempobj.getInt("total_appt")
                                temp.appointmentPendingId =
                                    tempobj.getJSONArray("appt_pending_ids")
                                temp.patientApptArray = tempobj.getJSONArray("appt")
                                temp.serviceId = 3
                                dashBoardApptListModelList.add(temp)
                            }

                            // for all data
                            for (i in 0 until clinicArr.length()) {
                                val tempobj = clinicArr.getJSONObject(i)
                                val tempAll = DashBoardApptListModel()
                                tempAll.clinicName = tempobj.getString("clinic_name")
                                tempAll.productId = tempobj.getInt("product_id")
                                tempAll.doctorServiceId = tempobj.getInt("doctor_service_id")
                                tempAll.apptCount = tempobj.getInt("total_appt")
                                tempAll.appointmentPendingId =
                                    tempobj.getJSONArray("appt_pending_ids")
                                tempAll.patientApptArray = tempobj.getJSONArray("appt")
                                tempAll.serviceId = 3
                                dashBoardApptListModelListAll.add(tempAll)
                                AppConstants.clinicCount++
                            }
                        }
                        if (sharedPref.isPrefExists("Video")) {
                            if (rootObj.has("video")) {
                                val dataObject = rootObj.optJSONObject("video")
                                if (dataObject != null) {
                                    //Do things with object.
                                    val videoObject = rootObj.getJSONObject("video")
                                    val tempVideo = DashBoardApptListModel()
                                    tempVideo.clinicName = videoObject.getString("video_name")
                                    tempVideo.productId = videoObject.getInt("product_id")
                                    tempVideo.apptCount = videoObject.getInt("total_appt")
                                    tempVideo.doctorServiceId =
                                        videoObject.getInt("doctor_service_id")
                                    tempVideo.appointmentPendingId =
                                        videoObject.getJSONArray("appt_pending_ids")
                                    tempVideo.patientApptArray =
                                        videoObject.getJSONArray("appt")
                                    tempVideo.serviceId = 1
                                    dashBoardApptListModelList.add(tempVideo)
                                    val tempVideoAll = DashBoardApptListModel()
                                    tempVideoAll.clinicName =
                                        videoObject.getString("video_name")
                                    tempVideoAll.productId = videoObject.getInt("product_id")
                                    tempVideoAll.apptCount = videoObject.getInt("total_appt")
                                    tempVideoAll.doctorServiceId =
                                        videoObject.getInt("doctor_service_id")
                                    tempVideoAll.appointmentPendingId =
                                        videoObject.getJSONArray("appt_pending_ids")
                                    tempVideoAll.patientApptArray =
                                        videoObject.getJSONArray("appt")
                                    tempVideoAll.serviceId = 1
                                    dashBoardApptListModelListAll.add(
                                        tempVideoAll
                                    )
                                }
                            }
                        }
                        if (sharedPref.isPrefExists("Chat")) {
                            if (rootObj.has("chat")) {
                                val dataObject = rootObj.optJSONObject("chat")
                                if (dataObject != null) {
                                    val chatObject = rootObj.getJSONObject("chat")
                                    val participantArr = chatObject.getJSONArray("participants")
                                    val tempChat = DashBoardApptListModel()
                                    tempChat.clinicName = chatObject.getString("chat_name")
                                    tempChat.productId = chatObject.getInt("product_id")
                                    tempChat.doctorServiceId =
                                        chatObject.getInt("doctor_service_id")
                                    tempChat.patientApptArray =
                                        chatObject.getJSONArray("participants")
                                    tempChat.apptCount = participantArr.length()
                                    tempChat.serviceId = 2
                                    dashBoardApptListModelList.add(tempChat)
                                    val tempChatAll = DashBoardApptListModel()
                                    tempChatAll.clinicName = chatObject.getString("chat_name")
                                    tempChatAll.productId = chatObject.getInt("product_id")
                                    tempChatAll.doctorServiceId =
                                        chatObject.getInt("doctor_service_id")
                                    tempChatAll.patientApptArray =
                                        chatObject.getJSONArray("participants")
                                    tempChatAll.apptCount = participantArr.length()
                                    tempChatAll.serviceId = 2
                                    dashBoardApptListModelListAll.add(
                                        tempChatAll
                                    )
                                }
                            }
                        }
                        // Set data adapter.
                        dashBoardApptListAdapter!!.notifyDataSetChanged()
                        if (AppConstants.selectedClinicClickOnDashBoard == 1) {
                            switchClinic(AppConstants.selectedClinicIdOnDashBoard)
                        }
                    } else {
                        errorHandler(requireContext(), s)
                    }
                } catch (e: JSONException) {
                    practiceProgressbar.visibility = View.GONE
                    e.printStackTrace()
                }
            }
    }

    private fun filterAppointment(selectedDuration: String?) {
        practiceProgressbar.visibility = View.VISIBLE
        dashboardViewModel.getDashBoardAppointmentDetails(activity, selectedDuration)
            .observe(requireActivity()) { s ->
                try {
                    dashBoardApptListModelList.clear()
                    dashBoardApptListModelListAll.clear()

                    val jsonObject = JSONObject(s)
                    if (jsonObject.getInt("status_code") == 200) {
                        practiceProgressbar.visibility = View.GONE
                        val response = JSONObject(s).getJSONObject("response")
                        val rootObj = response.optJSONObject("response")
                        val clinicArr = rootObj!!.getJSONArray("clinic")
                        Log.d("DashBoarAppointmentRes", clinicArr.toString())
                        if (clinicArr.length() > 0) {
                            for (i in 0..0) {
                                val tempobj = clinicArr.getJSONObject(i)
                                val temp = DashBoardApptListModel()
                                temp.clinicName = tempobj.getString("clinic_name")
                                temp.productId = tempobj.getInt("product_id")
                                temp.doctorServiceId = tempobj.getInt("doctor_service_id")
                                temp.apptCount = tempobj.getInt("total_appt")
                                temp.appointmentPendingId =
                                    tempobj.getJSONArray("appt_pending_ids")
                                temp.patientApptArray = tempobj.getJSONArray("appt")
                                temp.serviceId = 3
                                dashBoardApptListModelList.add(temp)
                            }

                            // for all data
                            for (i in 0 until clinicArr.length()) {
                                val tempobj = clinicArr.getJSONObject(i)
                                val tempAll = DashBoardApptListModel()
                                tempAll.clinicName = tempobj.getString("clinic_name")
                                tempAll.productId = tempobj.getInt("product_id")
                                tempAll.doctorServiceId = tempobj.getInt("doctor_service_id")
                                tempAll.apptCount = tempobj.getInt("total_appt")
                                tempAll.appointmentPendingId =
                                    tempobj.getJSONArray("appt_pending_ids")
                                tempAll.patientApptArray = tempobj.getJSONArray("appt")
                                tempAll.serviceId = 3
                                dashBoardApptListModelListAll.add(tempAll)
                            }
                        }
                        if (sharedPref.isPrefExists("Video")) {
                            if (rootObj.has("video")) {
                                // This will give us whatever's at "URL", regardless of its type.
                                val item = rootObj["video"]
                                // `instanceof` tells us whether the object can be cast to a specific type
                                if (item is JSONArray) {
                                    // do all kinds of JSONArray'ish things with urlArray
                                    val videoArray = item
                                    for (i in 0 until videoArray.length()) {
                                        val videoObject = videoArray.getJSONObject(i)
                                        val tempVideo = DashBoardApptListModel()
                                        tempVideo.clinicName =
                                            videoObject.getString("video_name")
                                        tempVideo.productId = videoObject.getInt("product_id")
                                        tempVideo.apptCount = videoObject.getInt("total_appt")
                                        tempVideo.doctorServiceId =
                                            videoObject.getInt("doctor_service_id")
                                        tempVideo.appointmentPendingId =
                                            videoObject.getJSONArray("appt_pending_ids")
                                        tempVideo.patientApptArray =
                                            videoObject.getJSONArray("appt")
                                        tempVideo.serviceId = 1
                                        dashBoardApptListModelList.add(
                                            tempVideo
                                        )
                                        val tempVideoAll = DashBoardApptListModel()
                                        tempVideoAll.clinicName =
                                            videoObject.getString("video_name")
                                        tempVideoAll.productId =
                                            videoObject.getInt("product_id")
                                        tempVideoAll.apptCount =
                                            videoObject.getInt("total_appt")
                                        tempVideoAll.doctorServiceId =
                                            videoObject.getInt("doctor_service_id")
                                        tempVideoAll.appointmentPendingId =
                                            videoObject.getJSONArray("appt_pending_ids")
                                        tempVideoAll.patientApptArray =
                                            videoObject.getJSONArray("appt")
                                        tempVideoAll.serviceId = 1
                                        dashBoardApptListModelListAll.add(
                                            tempVideoAll
                                        )
                                    }
                                } else {
                                    // do objecty stuff with urlObject
                                    val videoObject = item as JSONObject
                                    val tempVideo = DashBoardApptListModel()
                                    tempVideo.clinicName = videoObject.getString("video_name")
                                    tempVideo.productId = videoObject.getInt("product_id")
                                    tempVideo.apptCount = videoObject.getInt("total_appt")
                                    tempVideo.doctorServiceId =
                                        videoObject.getInt("doctor_service_id")
                                    tempVideo.appointmentPendingId =
                                        videoObject.getJSONArray("appt_pending_ids")
                                    tempVideo.patientApptArray =
                                        videoObject.getJSONArray("appt")
                                    tempVideo.serviceId = 1
                                    dashBoardApptListModelList.add(tempVideo)
                                    val tempVideoAll = DashBoardApptListModel()
                                    tempVideoAll.clinicName =
                                        videoObject.getString("video_name")
                                    tempVideoAll.productId = videoObject.getInt("product_id")
                                    tempVideoAll.apptCount = videoObject.getInt("total_appt")
                                    tempVideoAll.doctorServiceId =
                                        videoObject.getInt("doctor_service_id")
                                    tempVideoAll.appointmentPendingId =
                                        videoObject.getJSONArray("appt_pending_ids")
                                    tempVideoAll.patientApptArray =
                                        videoObject.getJSONArray("appt")
                                    tempVideoAll.serviceId = 1
                                    dashBoardApptListModelListAll.add(
                                        tempVideoAll
                                    )
                                }
                            }
                        }
                        if (sharedPref.isPrefExists("Chat")) {
                            if (rootObj.has("chat")) {

                                // This will give us whatever's at "URL", regardless of its type.
                                val item = rootObj["chat"]

                                // `instanceof` tells us whether the object can be cast to a specific type
                                if (item is JSONArray) {
                                    // it's an array
                                    val chatArray = item
                                    // do all kinds of JSONArray'ish things with urlArray
                                    for (i in 0 until chatArray.length()) {
                                        val chatObject = chatArray.getJSONObject(i)
                                        val participantArr =
                                            chatObject.getJSONArray("participants")
                                        val tempChat = DashBoardApptListModel()
                                        tempChat.clinicName = chatObject.getString("chat_name")
                                        tempChat.productId = chatObject.getInt("product_id")
                                        tempChat.doctorServiceId =
                                            chatObject.getInt("doctor_service_id")
                                        tempChat.patientApptArray =
                                            chatObject.getJSONArray("participants")
                                        tempChat.apptCount = participantArr.length()
                                        tempChat.serviceId = 2
                                        dashBoardApptListModelList.add(
                                            tempChat
                                        )
                                        val tempChatAll = DashBoardApptListModel()
                                        tempChatAll.clinicName =
                                            chatObject.getString("chat_name")
                                        tempChatAll.productId = chatObject.getInt("product_id")
                                        tempChatAll.doctorServiceId =
                                            chatObject.getInt("doctor_service_id")
                                        tempChatAll.patientApptArray =
                                            chatObject.getJSONArray("participants")
                                        tempChatAll.apptCount = participantArr.length()
                                        tempChatAll.serviceId = 2
                                        dashBoardApptListModelListAll.add(
                                            tempChatAll
                                        )
                                    }
                                } else {
                                    // if you know it's either an array or an object, then it's an object
                                    val chatObject = item as JSONObject
                                    // do objecty stuff with urlObject
                                    val participantArr = chatObject.getJSONArray("participants")
                                    //                                if (participantArr.length() > 0) {
                                    val tempChat = DashBoardApptListModel()
                                    tempChat.clinicName = chatObject.getString("chat_name")
                                    tempChat.productId = chatObject.getInt("product_id")
                                    tempChat.doctorServiceId =
                                        chatObject.getInt("doctor_service_id")
                                    tempChat.patientApptArray =
                                        chatObject.getJSONArray("participants")
                                    tempChat.apptCount = participantArr.length()
                                    tempChat.serviceId = 2
                                    dashBoardApptListModelList.add(tempChat)
                                    val tempChatAll = DashBoardApptListModel()
                                    tempChatAll.clinicName = chatObject.getString("chat_name")
                                    tempChatAll.productId = chatObject.getInt("product_id")
                                    tempChatAll.doctorServiceId =
                                        chatObject.getInt("doctor_service_id")
                                    tempChatAll.patientApptArray =
                                        chatObject.getJSONArray("participants")
                                    tempChatAll.apptCount = participantArr.length()
                                    tempChatAll.serviceId = 2
                                    dashBoardApptListModelListAll.add(
                                        tempChatAll
                                    )
                                }
                            }
                        }

                        // Set data adapter.
                        dashBoardApptListAdapter!!.notifyDataSetChanged()
                        if (AppConstants.selectedClinicClickOnDashBoard == 1) {
                            switchClinic(AppConstants.selectedClinicIdOnDashBoard)
                        }
                    } else {
                        errorHandler(requireContext(), s)
                    }
                } catch (e: JSONException) {
                    practiceProgressbar.visibility = View.GONE
                    e.printStackTrace()
                }
            }
    }

    private fun iAmLatePopup(pendingIdArray: JSONArray) {
        val dialog = Dialog(requireActivity())
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.dailog_dashboard_apointment_delay)
        val delayInSpinner = dialog.findViewById<View>(R.id.delayInSpinner) as Spinner
        val enterDelayTime = dialog.findViewById<View>(R.id.enterDelayTime) as EditText
        val sendDelayButton = dialog.findViewById<View>(R.id.sendDelayButton) as Button
        val dailogArticleCancel = dialog.findViewById<View>(R.id.dailogArticleCancel) as ImageView
        val adapter = ArrayAdapter(
            requireActivity(),
            android.R.layout.simple_spinner_item, requireActivity().resources
                .getStringArray(R.array.appointmentDelaylistArray)
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        delayInSpinner.adapter = adapter
        dailogArticleCancel.setOnClickListener {
            ZohoSalesIQ.Tracking.setCustomAction("HomeTab - Appt Summary - Delay Intimation - No")
            dialog.dismiss()
        }
        sendDelayButton.setOnClickListener {
            if (globalClass!!.isOnline) {
                if (enterDelayTime.text.toString().isEmpty()) {
                    Toast.makeText(activity, "Please enter delay time", Toast.LENGTH_LONG).show()
                } else {
                    dialog.dismiss()
                    var paymentModespin = delayInSpinner.selectedItem.toString()
                    ZohoSalesIQ.Tracking.setCustomAction("HomeTab - Appt Summary - Delay Intimation - Yes")
                    var time = enterDelayTime.text.toString()
                    if (time.equals("0", ignoreCase = true) || time.equals(
                            "",
                            ignoreCase = true
                        )
                    ) {
                        time = "a"
                        paymentModespin = "little"
                    }
                    appointmentDelay(pendingIdArray, paymentModespin, time)
                }
            } else {
                globalClass!!.noInternetConnection.showDialog(activity)
            }
        }
        dialog.show()
    }

    private fun BulkCancelPopup(pendingIdArray: JSONArray, clinicName: String, product_id: Int) {
        val dialog = Dialog(requireActivity())
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.dailog_dashboard_appointment_bulk_cancel)
        val durationSpinner = dialog.findViewById<View>(R.id.durationSpinner) as Spinner
        val cancelConfirmationText =
            dialog.findViewById<View>(R.id.cancelConfirmationText) as TextView
        val NoButton = dialog.findViewById<View>(R.id.NoButton) as TextView
        val YesButton = dialog.findViewById<View>(R.id.YesButton) as TextView
        val dailogArticleCancel = dialog.findViewById<View>(R.id.dailogArticleCancel) as ImageView
        val bulkCancelConfirmLayout = dialog.findViewById<RelativeLayout>(R.id.bulkCancelConfirm_rl)
        val adapter = ArrayAdapter(
            requireActivity(),
            android.R.layout.simple_spinner_item, requireActivity().resources
                .getStringArray(R.array.appointmentBulkCancelListArray)
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        durationSpinner.adapter = adapter
        durationSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View,
                position: Int,
                id: Long
            ) {
                bulkCancelSpinnerItemPosition = durationSpinner.selectedItemPosition
                val spinnerSelectedItem = durationSpinner.selectedItem.toString()
                if (durationSpinner.selectedItem.toString()
                        .equals("Select Time", ignoreCase = true)
                ) {
                    cancelMessage = "Do you want to cancel appointments for $clinicName?"
                    bulkCancelConfirmLayout.visibility = View.GONE
                } else if (spinnerSelectedItem.equals("Today", ignoreCase = true)) {
                    cancelMessage = "Do you want to cancel Today appointment for $clinicName?"
                    bulkCancelConfirmLayout.visibility = View.VISIBLE
                } else {
                    cancelMessage =
                        "Do you want to cancel  $spinnerSelectedItem appointments for $clinicName?"
                    bulkCancelConfirmLayout.visibility = View.VISIBLE
                }
                cancelConfirmationText.text = Html.fromHtml(cancelMessage)
            } // to close the onItemSelected

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
        dailogArticleCancel.setOnClickListener { dialog.dismiss() }
        NoButton.setOnClickListener { dialog.dismiss() }
        YesButton.setOnClickListener {
            if (globalClass!!.isOnline) {
                if (bulkCancelSpinnerItemPosition == 0) {
                    Toast.makeText(activity, "Please select duration", Toast.LENGTH_LONG).show()
                } else {
                    dialog.dismiss()
                    var selectedDurationBulkCancel = durationSpinner.selectedItem.toString()
                    if (selectedDurationBulkCancel.equals("Today", ignoreCase = true)) {
                        selectedDurationBulkCancel = "today"
                    } else if (selectedDurationBulkCancel.equals("Yesterday", ignoreCase = true)) {
                        selectedDurationBulkCancel = "yesterday"
                    } else if (selectedDurationBulkCancel.equals(
                            "This week so far",
                            ignoreCase = true
                        )
                    ) {
                        selectedDurationBulkCancel = "thisweek"
                    } else if (selectedDurationBulkCancel.equals("Last week", ignoreCase = true)) {
                        selectedDurationBulkCancel = "lastweek"
                    } else if (selectedDurationBulkCancel.equals(
                            "This month so far",
                            ignoreCase = true
                        )
                    ) {
                        selectedDurationBulkCancel = "thismonth"
                    } else if (selectedDurationBulkCancel.equals(
                            "Last month",
                            ignoreCase = true
                        )
                    ) {
                        selectedDurationBulkCancel = "lastmonth"
                    }
                    bulkAppointmentCancel(
                        selectedDurationBulkCancel,
                        "",
                        "",
                        pendingIdArray,
                        8,
                        product_id,
                        clinicName
                    )
                }
            } else {
                globalClass!!.noInternetConnection.showDialog(activity)
            }
        }
        dialog.show()
    }

    private fun BulkCompletePopup(pendingIdArray: JSONArray, clinicName: String, product_id: Int) {
        val dialog = Dialog(requireActivity())
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.dailog_dashboard_appointment_bulk_complete)
        val durationSpinner = dialog.findViewById<View>(R.id.durationSpinner) as Spinner
        val followUpSwitch = dialog.findViewById<View>(R.id.followUpSwitch) as CheckBox
        val invoiceGenerateSwitch =
            dialog.findViewById<View>(R.id.generateInvoiceSwitch) as CheckBox
        val cancelConfirmationText =
            dialog.findViewById<View>(R.id.cancelConfirmationText) as TextView
        val NoButton = dialog.findViewById<View>(R.id.NoButton) as TextView
        val YesButton = dialog.findViewById<View>(R.id.YesButton) as TextView
        val dailogArticleCancel = dialog.findViewById<View>(R.id.dailogArticleCancel) as ImageView
        val bulkCompleteConfirm_rl =
            dialog.findViewById<RelativeLayout>(R.id.bulkCompleteConfirm_rl)
        val adapter = ArrayAdapter(
            requireActivity(),
            android.R.layout.simple_spinner_item, requireActivity().resources
                .getStringArray(R.array.appointmentBulkCancelListArray)
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        durationSpinner.adapter = adapter
        durationSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View,
                position: Int,
                id: Long
            ) {
                bulkCompleteSpinnerItemPosition = durationSpinner.selectedItemPosition
                val spinnerSelectedItem = durationSpinner.selectedItem.toString()
                if (durationSpinner.selectedItem.toString()
                        .equals("Select Time", ignoreCase = true)
                ) {
                    cancelMessage = "Do you want to complete all appointments for $clinicName?"
                    bulkCompleteConfirm_rl.visibility = View.GONE
                } else if (spinnerSelectedItem.equals("Today", ignoreCase = true)) {
                    cancelMessage = "Do you want to complete all Today appointment for $clinicName?"
                    bulkCompleteConfirm_rl.visibility = View.VISIBLE
                } else {
                    cancelMessage =
                        "Do you want to complete all  \"$spinnerSelectedItem\" appointments for $clinicName?"
                    bulkCompleteConfirm_rl.visibility = View.VISIBLE
                }
                cancelConfirmationText.text = Html.fromHtml(cancelMessage)
            } // to close the onItemSelected

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
        followUpSwitch.setOnCheckedChangeListener { buttonView, isChecked -> // do something, the isChecked will be
            // true if the switch is clinicplus the On position
            followUpDefaultSwitchClick = isChecked
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
        dailogArticleCancel.setOnClickListener { dialog.dismiss() }
        NoButton.setOnClickListener { dialog.dismiss() }
        YesButton.setOnClickListener {
            if (globalClass!!.isOnline) {
                if (bulkCompleteSpinnerItemPosition == 0) {
                    Toast.makeText(activity, "Please select duration", Toast.LENGTH_LONG).show()
                } else {
                    dialog.dismiss()
                    var selectedDurationblukComplete = durationSpinner.selectedItem.toString()
                    if (selectedDurationblukComplete.equals("Today", ignoreCase = true)) {
                        selectedDurationblukComplete = "today"
                    } else if (selectedDurationblukComplete.equals(
                            "Yesterday",
                            ignoreCase = true
                        )
                    ) {
                        selectedDurationblukComplete = "yesterday"
                    } else if (selectedDurationblukComplete.equals(
                            "This week so far",
                            ignoreCase = true
                        )
                    ) {
                        selectedDurationblukComplete = "thisweek"
                    } else if (selectedDurationblukComplete.equals(
                            "Last week",
                            ignoreCase = true
                        )
                    ) {
                        selectedDurationblukComplete = "lastweek"
                    } else if (selectedDurationblukComplete.equals(
                            "This month so far",
                            ignoreCase = true
                        )
                    ) {
                        selectedDurationblukComplete = "thismonth"
                    } else if (selectedDurationblukComplete.equals(
                            "Last month",
                            ignoreCase = true
                        )
                    ) {
                        selectedDurationblukComplete = "lastmonth"
                    }
                    bulkCompleteDetails(
                        selectedDurationblukComplete,
                        "",
                        "",
                        pendingIdArray,
                        followUpDefaultSwitchClick,
                        invoiceGenerateSwitchClick,
                        product_id
                    )
                }
            } else {
                globalClass!!.noInternetConnection.showDialog(activity)
            }
        }
        dialog.show()
    }

    fun appointmentDelay(pendingIdArray: JSONArray?, delayIn: String?, delayTime: String?) {
        dashboardViewModel.sendDelayDetails(activity, pendingIdArray, delayIn, delayTime).observe(
            requireActivity()
        ) { s ->
            try {
                val jsonObject = JSONObject(s)
                if (jsonObject.getInt("status_code") == 200) {
                    Toast.makeText(activity, "Patients notified via SMS", Toast.LENGTH_LONG)
                        .show()
                } else {
                    errorHandler(requireContext(), s)
                }
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }
    }

    private fun bulkAppointmentCancel(
        duration: String?,
        toDate: String?,
        fromDate: String?,
        pendingIdArray: JSONArray,
        reason: Int,
        product_id: Int,
        clinicName: String?
    ) {
        dashboardViewModel.bulkCancelDetails(
            activity,
            duration,
            toDate,
            fromDate,
            pendingIdArray,
            reason,
            product_id,
            clinicName
        ).observe(
            requireActivity()
        ) { s ->
            try {
                val jsonObject = JSONObject(s)
                if (jsonObject.getInt("status_code") == 200) {
                    Toast.makeText(
                        activity,
                        pendingIdArray.length()
                            .toString() + " appointments have been cancelled",
                        Toast.LENGTH_SHORT
                    ).show()
                    dashBoardAppointmentDetails(selectedDuration)
                } else {
                    errorHandler(requireContext(), s)
                }
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }
    }

    private fun bulkCompleteDetails(
        duration: String?,
        toDate: String?,
        fromDate: String?,
        pendingIdArray: JSONArray?,
        isFollowUp: Boolean,
        isInVoice: Boolean,
        product_id: Int
    ) {
        dashboardViewModel.bulkCompleteDetails(
            activity,
            duration,
            toDate,
            fromDate,
            pendingIdArray,
            isFollowUp,
            isInVoice,
            product_id
        ).observe(
            requireActivity()
        ) { s ->
            try {
                val jsonObject = JSONObject(s)
                if (jsonObject.getInt("status_code") == 200) {
                    Toast.makeText(
                        activity,
                        "Appointments completed successfully",
                        Toast.LENGTH_LONG
                    ).show()
                    dashBoardAppointmentDetails(selectedDuration)
                } else {
                    errorHandler(requireContext(), s)
                }
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }
    }


    private fun getDefaultFollowUpSetting(defalutFollow: CheckBox) {
        dashboardViewModel.getDefaultFollowUpSettingDetails(activity)
            .observe(requireActivity()) { s ->
                try {
                    val jsonObject = JSONObject(s)
                    if (jsonObject.getInt("status_code") == 200) {
                        val response = JSONObject(s).getJSONObject("response")
                        val defalutFollowUp =
                            response.getJSONObject("response").getBoolean("defaultFollowUp")
                        defalutFollow.isChecked = defalutFollowUp
                    } else {
                        errorHandler(requireContext(), s)
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }
    }


    private fun cancelBookedTraining(id: Int, trainingScheduleId: Int) {
        dashboardViewModel.cancelBookedTrainingDetails(activity, id, trainingScheduleId).observe(
            requireActivity()
        ) { s ->
            try {
                val jsonObject = JSONObject(s)
                if (jsonObject.getInt("status_code") == 200) {
                    val response = JSONObject(s).getJSONObject("response")
                    val responseValue = response.getBoolean("response")
                    if (responseValue) {
                        Toast.makeText(
                            activity,
                            "Your training has been cancelled successfully",
                            Toast.LENGTH_SHORT
                        ).show()
                        trainingScheduleModel.clear()
                        getBookedTraining()
                    }
                } else {
                    errorHandler(requireContext(), s)
                }
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }
    }

    private fun getDashBoardRibbonMessage() {
        dashboardViewModel.getDashboardRibbonData(activity)
            .observe(viewLifecycleOwner) { s ->
                Log.d(TAG, "onChanged: $s")
                try {
                    val jsonObject = JSONObject(s)
                    if (jsonObject.getInt("status_code") == 200) {
                        val response =
                            JSONObject(s).getJSONObject("response").getJSONObject("response")
                        if (response.getInt("is_show_merchant_section") == 1) {
                            val jsonArray = response.getJSONArray("msg")
                            val linkedTxt = response.optString("link_text")
                            val iconCode = response.optString("icon_code")
                            val message = jsonArray[0].toString()
                            ribbon_layout.visibility = View.VISIBLE
                            create_account_ribbon.setText(
                                message + linkedTxt,
                                TextView.BufferType.SPANNABLE
                            )
                            val sp = create_account_ribbon.text as Spannable
                            val start = message.length
                            val end = start + linkedTxt.length
                            //0x00A65A
                            sp.setSpan(
                                ForegroundColorSpan(
                                    ContextCompat.getColor(
                                        requireActivity(),
                                        R.color.colorPrimary
                                    )
                                ),
                                start,
                                end,
                                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                            )
                            if (iconCode.equals("danger", ignoreCase = true)) {
                                ribbon_image.setImageResource(R.drawable.ic_ribbon_cancel)
                                ribbon_image.setColorFilter(
                                    ContextCompat.getColor(
                                        requireActivity(),
                                        R.color.red
                                    )
                                )
                            } else if (iconCode.equals("warning", ignoreCase = true)) {
                                ribbon_image.setImageResource(R.drawable.ic_alert)
                            } else if (iconCode.equals("success", ignoreCase = true)) {
                                ribbon_image.setImageResource(R.drawable.ic_done)
                            } else {
                                if (iconCode.equals("other", ignoreCase = true)) {
                                    ribbon_image.setImageResource(R.drawable.rupee_sign)
                                }
                            }
                            //create_account_ribbon.setText(message + linkedTxt);
                        } else {
                            if (response.getInt("is_show_merchant_section") == 0) {
                                ribbon_layout.visibility = View.GONE
                            }
                        }
                    } else {
                        errorHandler(requireContext(), s)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
    }

    private fun registerReceiverAppointmetListPatientListAndAutoFlowUp() {
        broadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                when (intent.action) {
                    "UPDATE_FRAG_LIST" -> {
                        // here you can fire your action which you want also get data from intent
                        Log.d("onReceviedCalld", "onReceviedCalld")
                        //                    isAppointmentListRefreshReq = true;
                        when (AppConstants.durationSelectedValue) {
                            1 -> {
                                selectDuration = 1
                                selectedDuration = "All"
                                practicesDuration.text = Html.fromHtml("<u>All</u>")
                                dashBoardAppointmentDetails(selectedDuration)
                            }
                            2 -> {
                                selectDuration = 2
                                selectedDuration = "Today"
                                practicesDuration.text = Html.fromHtml("<u>Today</u>")
                                dashBoardAppointmentDetails(selectedDuration)
                            }
                            3 -> {
                                selectDuration = 3
                                selectedDuration = "This Week"
                                practicesDuration.text = Html.fromHtml("<u>This Week</u>")
                                dashBoardAppointmentDetails(selectedDuration)
                            }
                            4 -> {
                                selectDuration = 4
                                selectedDuration = "This Month"
                                practicesDuration.text = Html.fromHtml("<u>This Month</u>")
                                dashBoardAppointmentDetails(selectedDuration)
                            }
                            5 -> {
                                selectDuration = 5
                                selectedDuration = "This Year"
                                practicesDuration.text = Html.fromHtml("<u>This Year</u>")
                                dashBoardAppointmentDetails(selectedDuration)
                            }
                            else -> {
                                dashBoardAppointmentDetails("Today")
                            }
                        }
                    }
                    "PATIENT_LIST_REFRESH" -> {
                        // here you can fire your action which you want also get data from intent
                        Log.d("onReceviedCalldPateint", "onReceviedCalldPateint")
                        isPatientListRefreshReq = true
                    }
                    "GET_ONLINE_TRAINING" -> {
                        Log.d("getOnLineTraining", "getOnLineTraining")
                        trainingScheduleModel.clear()
                        getBookedTraining()
                        getUpComingTraining()
                    }
                }
                if (intent.action == "DASHBOARD_FOLLOWUP_LIST") {
                    // here you can fire your action which you want also get data from intent
                    Log.d("onReceviedCallFollowup", "onReceviedCallFollowup")
                    //                    isFollowUpListRefreshReq = true;
                    dashboardViewModel.getSharedAndFollowUpDetails(activity)
                        .observe(requireActivity()) { s ->
                            try {
                                shareRecordsModel.clear()
                                dashBoardAutoFollowUpModel.clear()
                                val jsonObject = JSONObject(s)
                                if (jsonObject.getInt("status_code") == 200) {
                                    val response = JSONObject(s).getJSONObject("response")

                                    //                        share records
                                    val sharedRecordsArr = response.getJSONObject("response")
                                        .getJSONArray("shared_records")
                                    Log.d(
                                        "Shared Records",
                                        sharedRecordsArr.length().toString() + ""
                                    )
                                    if (sharedRecordsArr.length() > 0) {
                                        sharedRecordsSection.visibility = View.VISIBLE
                                        if (sharedPref.isPrefExists("EMR")) {
                                            sharedRecordsSection.visibility = View.VISIBLE
                                        } else {
                                            sharedRecordsSection.visibility = View.GONE
                                        }
                                        dashboardSharedRecordProgressbar.visibility = View.GONE
                                        noDataMessageSharedByPatient.visibility = View.GONE
                                        sharedRecordsEmptyText.visibility = View.GONE
                                    }
                                    if (sharedRecordsArr.length() > 2) {
                                        sharedRecodsViewAll.visibility = View.VISIBLE
                                    } else {
                                        sharedRecodsViewAll.visibility = View.GONE
                                    }
                                    val fieldDicArr = response.getJSONObject("response")
                                        .getJSONObject("field-dictionary")
                                    if (sharedRecordsArr.length() > 0) {
                                        for (i in 0 until sharedRecordsArr.length()) {
                                            val sharedObj = sharedRecordsArr.getJSONObject(i)
                                            val model = DashBoardPatientRecordsModel()
                                            val catId = sharedObj.getJSONObject("recordinfo")
                                                .getJSONObject("records")
                                                .getJSONObject("category").getInt("id")
                                                .toString() + ""


                                            //------------by dileep-------------
                                            val catName = sharedObj.getJSONObject("recordinfo")
                                                .getJSONObject("records")
                                                .getJSONObject("category")
                                                .getString("category") + ""
                                            val recordDetailsObjects =
                                                sharedObj.getJSONObject("recordinfo")
                                                    .getJSONObject("records")
                                            val sharedOnDateTime =
                                                recordDetailsObjects.getJSONObject("share_details")
                                                    .getString("created_at")
                                            val recordDetailsArray = JSONArray()
                                            recordDetailsArray.put(sharedObj)
                                            val recordId =
                                                sharedObj.getInt("record_id") // added by dileep
                                            model.catId = catId //added by dileep
                                            model.categoryId = catId.toInt()
                                            model.fieldDictionary =
                                                fieldDicArr.toString() // added by dileep
                                            model.fieldDictionaryObject = fieldDicArr
                                            model.recordId = recordId // added by dileep
                                            model.catRecordData = recordDetailsArray.toString()
                                            model.recordDetailsObject = recordDetailsObjects
                                            model.sharedOnDateTime = sharedOnDateTime
                                            model.catName = catName
                                            model.created_At = sharedObj.getString("created_at")
                                            //-----------------end------------------
                                            if (fieldDicArr[catId] is JSONArray) {
                                                val fieldArr = fieldDicArr.getJSONArray(
                                                    sharedObj.getJSONObject("recordinfo")
                                                        .getJSONObject("records")
                                                        .getJSONObject("category").getInt("id")
                                                        .toString() + ""
                                                )
                                                model.primaryKey =
                                                    fieldArr.getJSONObject(0).getString("name")
                                                if (fieldArr.length() > 1) {
                                                    model.secKey = fieldArr.getJSONObject(1)
                                                        .getString("name")
                                                    if (sharedObj.getJSONObject("recordinfo")
                                                            .getJSONObject("records").has(
                                                                fieldArr.getJSONObject(1)
                                                                    .getString("key")
                                                            )
                                                    ) {
                                                        model.secData =
                                                            sharedObj.getJSONObject("recordinfo")
                                                                .getJSONObject("records")
                                                                .getString(
                                                                    fieldArr.getJSONObject(1)
                                                                        .getString("key")
                                                                )
                                                    } else {
                                                        model.secData = "-"
                                                    }
                                                    //                                            model.setSecData(sharedObj.getJSONObject("recordinfo").getJSONObject("records").getString(fieldArr.getJSONObject(1).getString("key")));
                                                }
                                                if (fieldArr.length() > 2) {
                                                    model.ternaryKey = fieldArr.getJSONObject(2)
                                                        .getString("name")
                                                    if (!sharedObj.getJSONObject("recordinfo")
                                                            .getJSONObject("records").has(
                                                                fieldArr.getJSONObject(2)
                                                                    .getString("key")
                                                            )
                                                    ) {
                                                        // if (sharedObj.getJSONObject("recordinfo").getJSONObject("records").getString(fieldArr.getJSONObject(2).getString("key")) == null) {
                                                    } else {
                                                        model.ternaryData =
                                                            sharedObj.getJSONObject("recordinfo")
                                                                .getJSONObject("records")
                                                                .getString(
                                                                    fieldArr.getJSONObject(2)
                                                                        .getString("key")
                                                                )
                                                    }
                                                }
                                                if (sharedObj.getJSONObject("recordinfo")
                                                        .getJSONObject("records").has(
                                                            fieldArr.getJSONObject(0)
                                                                .getString("key")
                                                        )
                                                ) {
                                                    model.primaryData =
                                                        sharedObj.getJSONObject("recordinfo")
                                                            .getJSONObject("records").getString(
                                                                fieldArr.getJSONObject(0)
                                                                    .getString("key")
                                                            )
                                                }
                                                model.fileUrl = ""
                                                if (sharedObj.getJSONObject("recordinfo")
                                                        .getJSONObject("records").has("url")
                                                ) {
                                                    model.fileUrl =
                                                        sharedObj.getJSONObject("recordinfo")
                                                            .getJSONObject("records")
                                                            .getString("url")
                                                }

                                                //use to set patient name
                                                model.recordName =
                                                    sharedObj.getJSONObject("recordinfo")
                                                        .getJSONObject("records")
                                                        .getString("patient")
                                                shareRecordsModel.add(model)
                                            } else {
                                                val fieldArr = fieldDicArr.getJSONObject(
                                                    sharedObj.getJSONObject("recordinfo")
                                                        .getJSONObject("records")
                                                        .getJSONObject("category").getInt("id")
                                                        .toString() + ""
                                                )
                                                model.primaryKey = fieldArr.getJSONObject("0")
                                                    .getString("name")
                                                if (fieldArr.length() > 1) {
                                                    model.secKey = fieldArr.getJSONObject("1")
                                                        .getString("name")
                                                    if (sharedObj.getJSONObject("recordinfo")
                                                            .getJSONObject("records").has(
                                                                fieldArr.getJSONObject("1")
                                                                    .getString("key")
                                                            )
                                                    ) {
                                                        model.secData =
                                                            sharedObj.getJSONObject("recordinfo")
                                                                .getJSONObject("records")
                                                                .getString(
                                                                    fieldArr.getJSONObject("1")
                                                                        .getString("key")
                                                                )
                                                    } else {
                                                        model.secData = "-"
                                                    }
                                                    //                                            model.setSecData(sharedObj.getJSONObject("recordinfo").getJSONObject("records").getString(fieldArr.getJSONObject("1").getString("key")));
                                                }
                                                if (fieldArr.length() > 2) {
                                                    model.ternaryKey =
                                                        fieldArr.getJSONObject("2")
                                                            .getString("name")

                                                    model.ternaryData =
                                                        sharedObj.getJSONObject("recordinfo")
                                                            .getJSONObject("records")
                                                            .getString(
                                                                fieldArr.getJSONObject("2")
                                                                    .getString("key")
                                                            )
                                                }
                                                if (sharedObj.getJSONObject("recordinfo")
                                                        .getJSONObject("records").has(
                                                            fieldArr.getJSONObject("0")
                                                                .getString("key")
                                                        )
                                                ) model.primaryData =
                                                    sharedObj.getJSONObject("recordinfo")
                                                        .getJSONObject("records").getString(
                                                            fieldArr.getJSONObject("0")
                                                                .getString("key")
                                                        )
                                                model.fileUrl = ""
                                                if (sharedObj.getJSONObject("recordinfo")
                                                        .getJSONObject("records").has("url")
                                                ) {
                                                    model.fileUrl =
                                                        sharedObj.getJSONObject("recordinfo")
                                                            .getJSONObject("records")
                                                            .getString("url")
                                                }
                                                shareRecordsModel.add(model)
                                            }
                                        }
                                        sharedRecordsListAdapter.notifyDataSetChanged()
                                    } else {
                                        dashboardSharedRecordProgressbar.visibility = View.GONE
                                        noDataMessageSharedByPatient.visibility = View.GONE
                                        sharedRecodsViewAll.visibility = View.GONE
                                        sharedRecordsEmptyText.visibility = View.VISIBLE
                                        sharedRecordsEmptyText.text =
                                            buildString {
                                                append("All records shared by patients will appear here")
                                            }
                                        if (sharedPref.isPrefExists("EMR")) {
                                            sharedRecordsSection.visibility = View.VISIBLE
                                        } else {
                                            sharedRecordsSection.visibility = View.GONE
                                        }
                                    }


                                    //                      Auto followUp data
                                    val autoFollowUpArr = response.getJSONObject("response")
                                        .getJSONArray("followUpSubmissions")
                                    if (autoFollowUpArr.length() > 0) {
                                        autoFollowUpSection.visibility = View.VISIBLE
                                        autoFollowUpEmptyText.visibility = View.GONE
                                        dashboardFollowUpProgressbar.visibility = View.GONE
                                        noDataMessageAutoFollowUp.visibility = View.GONE
                                    }
                                    if (autoFollowUpArr.length() > 2) {
                                        autoFollowUpViewAll.visibility = View.VISIBLE
                                    } else {
                                        autoFollowUpViewAll.visibility = View.VISIBLE
                                    }
                                    if (autoFollowUpArr.length() > 0) {
                                        for (i in 0 until autoFollowUpArr.length()) {
                                            val autoFollowObj = autoFollowUpArr.getJSONObject(i)
                                            val model = DashBoardAutoFollowUpModel()
                                            model.patientMessage =
                                                autoFollowObj.getString("description")
                                            model.followUpId = autoFollowObj.getInt("id")
                                            if (autoFollowObj.isNull("how_are_you_feeling")) {
                                                model.conditionStatus = 0
                                            } else {
                                                model.conditionStatus =
                                                    autoFollowObj.getInt("how_are_you_feeling")
                                            }
                                            if (autoFollowObj.isNull("is_following_instructions")) {
                                                model.followInstructionStatus = 0
                                            } else {
                                                model.followInstructionStatus =
                                                    autoFollowObj.getInt("is_following_instructions")
                                            }

//                                            if (autoFollowObj.getString("appt_datetime") != null) {
                                                model.followUpDate =
                                                    autoFollowObj.getString("appt_datetime")
//                                            } else {
//                                                model.followUpDate = ConfirmOrderActivity.followUpDate;
//                                            }

//                                            model.followUpDate = autoFollowObj.getJSONObject("appointment")
//                                                .getString("follow_up_datetime")
                                            model.submissionDate =
                                                autoFollowObj.getString("created_at")
                                            model.appointmentDate =
                                                autoFollowObj.getJSONObject("appointment")
                                                    .getString("scheduled_start_time")
                                            model.clinicName =
                                                autoFollowObj.getJSONObject("appointment")
                                                    .getJSONObject("order")
                                                    .getJSONObject("products")
                                                    .getJSONObject("prod_service")
                                                    .getString("alias")
                                            model.clinicAddress =
                                                autoFollowObj.getJSONObject("appointment")
                                                    .getJSONObject("order")
                                                    .getJSONObject("products")
                                                    .getJSONObject("prod_service")
                                                    .getString("address")
                                            model.productServiceId =
                                                autoFollowObj.getJSONObject("appointment")
                                                    .getJSONObject("order")
                                                    .getJSONObject("products")
                                                    .getJSONObject("prod_service")
                                                    .getInt("service_id")
                                            model.patientName =
                                                autoFollowObj.getJSONObject("appointment")
                                                    .getJSONObject("order")
                                                    .getJSONObject("order_user")
                                                    .getString("fname")
                                            model.patientId =
                                                autoFollowObj.getJSONObject("appointment")
                                                    .getJSONObject("order")
                                                    .getJSONObject("order_user").getInt("id")
                                            model.patientMessage =
                                                autoFollowObj.getString("description")
                                            model.isApptBooked =
                                                autoFollowObj.getInt("is_appointment_booked")
                                            model.fileUrl = autoFollowObj.getString("file_url")
                                            dashBoardAutoFollowUpModel.add(model)
                                        }
                                        autoFollowUpListAdapter.notifyDataSetChanged()
                                    } else {
                                        dashboardFollowUpProgressbar.visibility = View.GONE
                                        autoFollowUpViewAll.visibility = View.GONE
                                        noDataMessageAutoFollowUp.visibility = View.GONE
                                        autoFollowUpEmptyText.visibility = View.VISIBLE
                                        autoFollowUpEmptyText.text =
                                            buildString {
                                                append("Follow-up data from your patients will appear here")
                                            }
                                    }
                                } else {
                                    errorHandler(requireContext(), s)
                                }
                            } catch (e: JSONException) {
                                Log.d("followUpError", "followUpError$e")
                                e.printStackTrace()
                            }
                        }
                }
            }
        }
        val intentFilter = IntentFilter()
        intentFilter.addAction("UPDATE_FRAG_LIST")
        intentFilter.addAction("PATIENT_LIST_REFRESH")
        intentFilter.addAction("DASHBOARD_FOLLOWUP_LIST")
        intentFilter.addAction("GET_ONLINE_TRAINING")
        requireActivity().registerReceiver(broadcastReceiver, intentFilter)
    }


    override fun onDestroy() {
        super.onDestroy()
        requireActivity().unregisterReceiver(broadcastReceiver)
    }

}