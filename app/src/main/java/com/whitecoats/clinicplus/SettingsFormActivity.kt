package com.whitecoats.clinicplus

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.PorterDuff
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.preference.PreferenceManager
import android.text.Html
import android.text.method.LinkMovementMethod
import android.util.Log
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.text.HtmlCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.google.android.material.internal.ViewUtils.hideKeyboard
import com.whitecoats.adapter.SettingScheduleListAdapter
import com.whitecoats.adapter.SettingVideoScheduleListAdapter
import com.whitecoats.clinicplus.activities.PaymentSetupScreen
import com.whitecoats.clinicplus.apis.ApiGetPostMethodCalls
import com.whitecoats.clinicplus.apis.ApiUrls
import com.whitecoats.clinicplus.models.EnableDisableModel
import com.whitecoats.clinicplus.models.EnableOrDisableBody
import com.whitecoats.clinicplus.models.ServiceDetailsModelClass
import com.whitecoats.clinicplus.utils.ErrorHandlerClass.errorHandler
import com.whitecoats.clinicplus.utils.RestUtils
import com.whitecoats.clinicplus.utils.SharedPref
import com.whitecoats.clinicplus.utils.UtilsResource
import com.whitecoats.clinicplus.viewmodels.CommonViewModel
import com.whitecoats.clinicplus.viewmodels.CreateNewAccountViewModel
import com.whitecoats.clinicplus.viewmodels.SettingsViewModel
import com.whitecoats.model.SettingScheduleModel
import com.whitecoats.model.SettingVideoScheduleModel
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

@SuppressLint("UseSwitchCompatOrMaterialCode")
class SettingsFormActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {
    private var personalLayout: RelativeLayout? = null
    private var profesLayout: RelativeLayout? = null
    private var financialLayout: RelativeLayout? = null
    private var timeBlockLayout: RelativeLayout? = null
    private var apptReminderLayout: RelativeLayout? = null
    private var settingServiceSetupLayout: RelativeLayout? = null
    private var settingInstantVideoLayout: RelativeLayout? = null
    private var settingChaLayout: RelativeLayout? = null
    private var settingVideoLayout: RelativeLayout? = null
    private var scheduleEditLayout: RelativeLayout? = null
    private var recordPrefLayout: LinearLayout? = null
    private var apptStatLayout: LinearLayout? = null
    private val param = JSONObject()
    private var videoSettingServiceScrollView: ScrollView? = null
    private var settingServiceVideoForm: RelativeLayout? = null
    private var videoServiceSettingScheduleLayout: RelativeLayout? = null
    private var nextRecyclerView // added on 23rd july 2019
            : RelativeLayout? = null
    private var setupTextView: TextView? = null
    private var drServiceIdClinicOne = 0
    private var drServiceIdClinicTwo = 0
    private var drServiceIdClinicThree = 0
    private var globalApiCall: ApiGetPostMethodCalls? = null
    var mainActivity = MainActivity()
    private var instantVideoFromDay: Spinner? = null
    private var instantVideoFromTime: Spinner? = null
    private var instantVideoToDay: Spinner? = null
    private var instantVideoToTime: Spinner? = null
    private var chatDuration: Spinner? = null

    // Initializing a dayString Array
    private val spinnerDay = arrayOf(
        "Select day",
        "Monday",
        "Tuesday",
        "Wednesday",
        "Thursday",
        "Friday",
        "Saturday",
        "Sunday"
    )

    // Initializing a 24 hours timeString Array
    private val spinnerTime = arrayOf(
        "Select time",
        "00:00",
        "00:30",
        "01:00",
        "01:30",
        "02:00",
        "02:30",
        "03:00",
        "03:30",
        "04:00",
        "04:30",
        "05:00",
        "05:30",
        "06:00",
        "06:30",
        "07:00",
        "07:30",
        "08:00",
        "08:30",
        "09:00",
        "09:30",
        "10:00",
        "10:30",
        "11:00",
        "11:30",
        "12:00",
        "12:30",
        "13:00",
        "13:30",
        "14:00",
        "14:30",
        "15:00",
        "15:30",
        "16:00",
        "16:30",
        "17:00",
        "17:30",
        "18:00",
        "18:30",
        "19:00",
        "19:30",
        "20:00",
        "20:30",
        "21:00",
        "21:30",
        "22:00",
        "22:30",
        "23:00",
        "23:30"
    )

    // Initializing a 12 hours timeString Array
    private val spinner12HoursTime = arrayOf(
        "Select",
        "12:00 AM",
        "12:30 AM",
        "01:00 AM",
        "01:30 AM",
        "02:00 AM",
        "02:30 AM",
        "03:00 AM",
        "03:30 AM",
        "04:00 AM",
        "04:30 AM",
        "05:00 AM",
        "05:30 AM",
        "06:00 AM",
        "06:30 AM",
        "07:00 AM",
        "07:30 AM",
        "08:00 AM",
        "08:30 AM",
        "09:00 AM",
        "09:30 AM",
        "10:00 AM",
        "10:30 AM",
        "11:00 AM",
        "11:30 AM",
        "12:00 PM",
        "12:30 PM",
        "01:00 PM",
        "01:30 PM",
        "02:00 PM",
        "02:30 PM",
        "03:00 PM",
        "03:30 PM",
        "04:00 PM",
        "04:30 PM",
        "05:00 PM",
        "05:30 PM",
        "06:00 PM",
        "06:30 PM",
        "07:00 PM",
        "07:30 PM",
        "08:00 PM",
        "08:30 PM",
        "09:00 PM",
        "09:30 PM",
        "10:00 PM",
        "10:30 PM",
        "11:00 PM",
        "11:30 PM"
    )

    // Initializing a dayString Array
    private val spinnerChatDuration = arrayOf(
        "Select duration",
        "Days",
        "Hours",
        "Minutes"
    )

    // Initializing a dayString Array
    private val spinnerAppointmentDuration = arrayOf(
        "Select appointment duration",
        "5",
        "10",
        "15",
        "20",
        "30",
        "45",
        "60"
    )
    private val spinnerVideoAutoNoShow = arrayOf(
        "Yes",
        "No"
    )
    private val spinnerVideoInstantAutoNoShow = arrayOf(
        "Yes",
        "No"
    )
    private val spinnerVideoSetUpAutoNoShow = arrayOf(
        "Yes",
        "No"
    )
    private lateinit var spinnerDayList: MutableList<String>
    private lateinit var spinnerTimeList: MutableList<String>
    private lateinit var spinner12HoursTimeList: MutableList<String>
    private var spinnerChatDurationList: List<String>? = null
    private lateinit var spinnerVideoDurationList: MutableList<String>
    private var spinner_videoAutoNoShowList: List<String>? = null
    private var spinner_instantvideoAutoNoShowList: List<String>? = null
    private var spinner_videoSetUpAutoNoShowList: List<String>? = null
    private var instantFromDaySpinnerArrayAdapter: ArrayAdapter<String>? = null
    private var instantFromTimeSpinnerArrayAdapter: ArrayAdapter<String>? = null
    private var instantToDaySpinnerArrayAdapter: ArrayAdapter<String>? = null
    private var instantToTimeSpinnerArrayAdapter: ArrayAdapter<String>? = null
    private var chatDurationSpinnerArrayAdapter: ArrayAdapter<String>? = null
    private var videoDurationSpinnerArrayAdapter: ArrayAdapter<String>? = null
    private var videoSettingSpinnerArrayAdapter: ArrayAdapter<String>? = null
    private var videoAutoNoShowSpinnerArrayAdapter: ArrayAdapter<String>? = null
    private var instantVideoAutoNoShowSpinnerArrayAdapter: ArrayAdapter<String>? = null
    private var videoSetUpAutoNoShowSpinnerArrayAdapter: ArrayAdapter<String>? = null

    // private ArrayAdapter<String> chatDurationSpinnerServiceArrayAdapter, videoDurationServicSpinnerArrayAdapter, videoSettingSpinnerArrayAdapter;
    private var serviceRecycleView: RecyclerView? = null
    private var settingServiceServiceRecycleView: RecyclerView? = null
    private var settingScheduleRecycleView: RecyclerView? = null
    private var settingVideoSchedListAdapter: SettingVideoScheduleListAdapter? = null
    private var settingVideoSchedModelList: MutableList<SettingVideoScheduleModel>? = null
    private var settingVideoSchedModelListTemp: MutableList<SettingVideoScheduleModel>? = null
    private var settingVideoSchedModelListDup: MutableList<SettingVideoScheduleModel>? = null
    private var settingSchedListAdapter: SettingScheduleListAdapter? = null
    private lateinit var settingSchedModelList: MutableList<SettingScheduleModel>
    private var scheduleSettingButton: Button? = null
    private val settingFinanceUpdateButton: Button? = null
    private var appReminderUpdateButton: Button? = null
    private var videoAddressInputLayout: RelativeLayout? = null
    private var videoServiceAddressInputLayout: RelativeLayout? = null
    var prefs: SharedPreferences? = null

    //    private RelativeLayout personalDobLayout;
    private var updatePersionalDetails: Button? = null

    private var personalPhoneNumber: EditText? = null
    private val settingProfAccName: EditText? = null
    private val settingProfBankName: EditText? = null
    private val settingProfBranchName: EditText? = null
    private val settingProfAccNo: EditText? = null
    private val settingProfIFSC: EditText? = null
    private var genderValue = 0
    private var rg: RadioGroup? = null
    private var rbMale: RadioButton? = null
    private var rbFemale: RadioButton? = null
    private var appPreference: SharedPreferences? = null
    private var rootObj: JSONObject? = null
    private val rootObjGetBankDetails: JSONObject? = null
    private var rootObjGetAppStatics: JSONObject? = null
    private var rootObjSettingService: JSONObject? = null

    private var avgPatientArrival: Switch? = null
    private var avgTimeInLobby: Switch? = null
    private var avgConsultDelay: Switch? = null
    private var avgConsultTime: Switch? = null
    private var avgPatientArrivalLayout: RelativeLayout? = null
    private var avgTimeInLobbyLayout: RelativeLayout? = null
    private var avgConsultDelayLayout: RelativeLayout? = null
    private var avgConsultTimeLayout: RelativeLayout? = null
    private var jsonValue: JSONObject? = null
    private var jsonValue1: JSONObject? = null
    private var settingVideoService: RelativeLayout? = null
    private var settingInstantVideoService: RelativeLayout? = null
    private var settingChatService: RelativeLayout? = null
    private var settingClinicService: RelativeLayout? = null
    private var settingServiceVideoView: View? = null
    private var settingServiceInstantVideoView: View? = null
    private var settingChatServiceView: View? = null
    private var settingVideoButton: Button? = null
    private var settingInstantVideoButton: Button? = null
    private var settingChatButton: Button? = null
    private var settingClinicOneButton: Button? = null
    private var settingClinicTwoButton: Button? = null
    private var settingVideoCheckIcon: ImageView? = null
    private var settingInstantVideoCheckIcon: ImageView? = null
    private var settingChatCheckIcon: ImageView? = null
    private var settingClinicCheckIcon: ImageView? = null
    private var settingVideoActiveStatus: TextView? = null
    private var settingInstantVideoActiveStatus: TextView? = null
    private var settingChatActiveStatus: TextView? = null
    private var settingClinicActiveStatus: TextView? = null
    private var settingVideoTextMsg: TextView? = null
    private var settingInstantVideoTextMsg: TextView? = null
    private var settingChatTextMsg: TextView? = null
    private var settingClinicTextMsg: TextView? = null
    private var settingServiceSetupForm: RelativeLayout? = null
    private var settingInstantVideoForm: RelativeLayout? = null
    private var settingChatForm: RelativeLayout? = null
    private var settingVideoForm: RelativeLayout? = null
    private var videoSettingScheduleLayout: RelativeLayout? = null
    private var videoSettingScrollView: ScrollView? = null
    private var settingVideoShortText: EditText? = null
    private var settingVideoDescriptionText: EditText? = null
    private var videoPriceInput: EditText? = null
    private var videoAddressText: EditText? = null
    private var advanceBookingText: EditText? = null
    private var settingServiceVideoShortText: EditText? = null
    private var settingServiceVideoDescriptionText: EditText? = null
    private var videoServicePriceInput: EditText? = null
    private var videoServiceAddressText: EditText? = null
    private var advanceServiceBookingText: EditText? = null
    private var appointmentDurationSpinner: Spinner? = null
    private var videoDurationSpinner: Spinner? = null
    private var appointmentServiceDurationSpinner: Spinner? = null
    private var videoServiceDurationSpinner: Spinner? = null
    private var settingVideoUpdateButton: Button? = null
    private var settingServiceVideoUpdateButton: Button? = null
    private var settingVideoEnableDisable: RelativeLayout? = null
    private var settingChatShortText: EditText? = null
    private var chatDescriptionText: EditText? = null
    private var chatPriceText: EditText? = null
    private var chatValidityEditText: EditText? = null
    private var chatValidityDaySpinner: Spinner? = null
    private var chaSettingUpdateButton: Button? = null
    private var settingChatEnableDisable: RelativeLayout? = null
    private var settingChatEnableDisableSwitch: Switch? = null
    private var settingDescText: EditText? = null
    private var settingPriceText: EditText? = null
    private var fromDaySpinner: Spinner? = null
    private var fromTimeSpinner: Spinner? = null
    private var toDaySpinner: Spinner? = null
    private var toTimeSpinner: Spinner? = null
    private var settingInstantVideo: Button? = null
    private var settingInstantVideoEnableDisable: RelativeLayout? = null
    private var settingInstantVideoSwitch: Switch? = null
    private var videoScheduleLayout: RelativeLayout? = null
    private var videoServiceScheduleLayout: RelativeLayout? = null
    private var url = ""
    private var scheduleClickFlag = 0
    private var videoShortNameTxtInput: RelativeLayout? = null
    private var videoDescriptionTxtInput: RelativeLayout? = null
    private var videoPriceTxtInput: RelativeLayout? = null
    private var appointmentDurationLayout: RelativeLayout? = null
    private var videoAdvanceBookingLayout: RelativeLayout? = null
    private var videoAdvanceBookingFromLayout: RelativeLayout? = null
    private var settingServiceRecycleView: RecyclerView? = null
    private var clinicScheduleFlag = 0
    private var item: MenuItem? = null
    private var slotID = 0
    private var scheduleID = 0
    private var instantVideoSetupFlagClick = 0
    private var chatSetupFlagClick = 0
    private var activeTwoLayout: RelativeLayout? = null
    private var editor: SharedPreferences.Editor? = null
    private var prefsClinic: SharedPreferences? = null
    private var currentDate = ""
    var globalClass: MyClinicGlobalClass? = null
    private var isCreateMerchant = 0
    private var errorMessageTextLayout: RelativeLayout? = null
    private var merchantDetailsInfoLayout: RelativeLayout? = null
    private var createMerchantDetailsLayout: RelativeLayout? = null
    var errorMessageText: TextView? = null
    var nameValue: TextView? = null
    var phoneValue: TextView? = null
    var emailValue: TextView? = null
    var statusValue: TextView? = null
    var detailsHeaderText: TextView? = null
    var nameEditText: EditText? = null
    var phoneEditText: EditText? = null
    var emailEditText: EditText? = null
    var sendButton: Button? = null
    var createNewAccountViewModel: CreateNewAccountViewModel? = null
    private var auto_no_show_spinner_video: Spinner? = null
    private var auto_no_show_spinner_instant_video: Spinner? = null
    private var auto_no_show_spinner_video_setup: Spinner? = null
    private var no_show_limit_instant_video_et: EditText? = null
    private var no_show_limit_video_et: EditText? = null
    private var no_show_limit_video_setup_et: EditText? = null
    private var no_show_limt_video: TextView? = null
    private var no_show_limt_instant_video: TextView? = null
    private var no_show_limt_video_setup: TextView? = null
    private var selectedSpinnerNoShow = "Yes"
    private var automatic_no_show_control = 0
    private var automatic_no_show_time_limit = 0
    private var auto_no_show_video: RelativeLayout? = null
    private var help_no_show_video_hyper_text: ImageView? = null
    private var help_no_show_instant_video_hyper_text: ImageView? = null
    private var help_video_setup: ImageView? = null
    private var no_show_lay__video_setup: RelativeLayout? = null
    private var no_show_limit_video_value = 0
    private var no_show_limit_instant_video_value = 0
    private var appUtilities: AppUtilities? = null
    private var instantStartTime: String? = null
    private var instantEndTime: String? = null
    private lateinit var fromTime: String
    private var fromTimeDate: Date? = null
    private lateinit var toTime: String
    private var toTimeDate: Date? = null
    private var selectedserviceProd_ID = 0
    private var selectedDrService_ID = 0
    private var Internal_Supersaas_ID = 0
    private var viewModel: SettingsViewModel? = null
    private var myList: MutableList<Int>? = null
    private var sharedPref: SharedPref? = null
    private lateinit var dialog: Dialog
    private lateinit var commonViewModel: CommonViewModel
    private var slotSetOneCount = 0
    private var slotSetTwoCount = 0
    private var view: View? = null


    @SuppressLint("MissingInflatedId", "CutPasteId", "SimpleDateFormat", "NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting_form)
        val toolbar = findViewById<Toolbar>(R.id.settingFormToolbar)
        toolbar.title = intent.getStringExtra("Title")
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        val upArrow = ResourcesCompat.getDrawable(resources, R.drawable.ic_arrow_back, null)
        upArrow?.setColorFilter(
            ContextCompat.getColor(applicationContext, R.color.colorWhite),
            PorterDuff.Mode.SRC_ATOP
        )
        supportActionBar!!.setHomeAsUpIndicator(upArrow)
        globalClass = applicationContext as MyClinicGlobalClass
        globalApiCall = ApiGetPostMethodCalls()
        if (sharedPref == null) {
            sharedPref = SharedPref(this@SettingsFormActivity)
        }
        personalLayout = findViewById(R.id.settingPersonalForm)
        updatePersionalDetails = findViewById(R.id.updatePersionalDetails)
        profesLayout = findViewById(R.id.settingProfessionalForm)
        financialLayout = findViewById(R.id.settingFinancialForm)
        timeBlockLayout = findViewById(R.id.settingTimeBlockForm)
        recordPrefLayout = findViewById(R.id.settingRecordPrefForm)
        apptReminderLayout = findViewById(R.id.settingApptReminderForm)
        apptStatLayout = findViewById(R.id.settingApptStatsForm)
        settingServiceSetupLayout = findViewById(R.id.settingServiceSetupForm)
        settingInstantVideoLayout = findViewById(R.id.settingInstantVideoForm)
        settingChaLayout = findViewById(R.id.settingChatForm)
        settingVideoLayout = findViewById(R.id.settingVideoForm)
        videoSettingScrollView = findViewById(R.id.videoSettingScrollView)
        videoSettingScheduleLayout = findViewById(R.id.videoSettingScheduleLayout)
        instantVideoFromDay = findViewById(R.id.fromDaySpinner)
        instantVideoFromTime = findViewById(R.id.fromTimeSpinner)
        instantVideoToDay = findViewById(R.id.toDaySpinner)
        instantVideoToTime = findViewById(R.id.toTimeSpinner)
        chatDuration = findViewById(R.id.chatValidityDaySpinner)
        scheduleSettingButton = findViewById(R.id.scheduleSettingButton)
        videoAddressInputLayout = findViewById(R.id.videoAddressInputLayout)
        videoServiceAddressInputLayout = findViewById(R.id.videoServiceAddressInputLayout)
        spinnerDayList = ArrayList(listOf(*spinnerDay))
        spinnerTimeList = ArrayList(listOf(*spinnerTime))
        spinner12HoursTimeList = ArrayList(listOf(*spinner12HoursTime))
        spinnerChatDurationList = ArrayList(listOf(*spinnerChatDuration))
        spinnerVideoDurationList = ArrayList(listOf(*spinnerAppointmentDuration))
        spinner_videoAutoNoShowList = ArrayList(listOf(*spinnerVideoAutoNoShow))
        spinner_instantvideoAutoNoShowList =
            ArrayList(listOf(*spinnerVideoInstantAutoNoShow))
        spinner_videoSetUpAutoNoShowList = ArrayList(listOf(*spinnerVideoSetUpAutoNoShow))
        serviceRecycleView = findViewById(R.id.settingServiceRecycleView)
        settingServiceServiceRecycleView = findViewById(R.id.settingServiceServiceRecycleView)
        settingScheduleRecycleView = findViewById(R.id.SettingScheduleRecycleView)
        scheduleEditLayout = findViewById(R.id.scheduleEdit)
        personalPhoneNumber = findViewById(R.id.personalPhoneNumber)
        errorMessageTextLayout = findViewById(R.id.errorMessageTextLayout)
        merchantDetailsInfoLayout = findViewById(R.id.merchantDetailsInfoLayout)
        createMerchantDetailsLayout = findViewById(R.id.createMerchantDetailsLayout)
        errorMessageText = findViewById(R.id.errorMessageText)
        nameValue = findViewById(R.id.nameValue)
        phoneValue = findViewById(R.id.phoneValue)
        emailValue = findViewById(R.id.emailValue)
        statusValue = findViewById(R.id.statusValue)
        appReminderUpdateButton = findViewById(R.id.appReminderUpdateButton)
        avgPatientArrival = findViewById(R.id.avgPatientArrival)
        avgTimeInLobby = findViewById(R.id.avgTimeInLobby)
        avgConsultDelay = findViewById(R.id.avgConsultDelay)
        avgConsultTime = findViewById(R.id.avgConsultTime)
        avgPatientArrivalLayout = findViewById(R.id.avgPatientArrivalLayout)
        avgTimeInLobbyLayout = findViewById(R.id.avgTimeInLobbyLayout)
        avgConsultDelayLayout = findViewById(R.id.avgConsultDelayLayout)
        avgConsultTimeLayout = findViewById(R.id.avgConsultTimeLayout)
        videoScheduleLayout = findViewById(R.id.videoScheduleLayout)
        videoServiceScheduleLayout = findViewById(R.id.videoServiceScheduleLayout)
        setupTextView = findViewById(R.id.setupTextView)
        profesionalLanguageText = findViewById(R.id.profesionalLanguageText)
        profesionalQualificationText = findViewById(R.id.profesionalQualificationText)
        videoSettingServiceScrollView = findViewById(R.id.videoSettingServiceScrollView)
        settingServiceVideoForm = findViewById(R.id.settingServiceVideoForm)
        no_show_lay__video_setup = findViewById(R.id.no_show_lay__video_setup)
        nextRecyclerView = findViewById(R.id.nextRecyclerView) //added on 23rd july 2019
        videoServiceSettingScheduleLayout = findViewById(R.id.videoServiceSettingScheduleLayout)
        activeTwoLayout = findViewById(R.id.activeTwoLayout)
        editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit()
        prefsClinic = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE)
        appUtilities = AppUtilities()
        commonViewModel = ViewModelProvider(this@SettingsFormActivity)[CommonViewModel::class.java]
        settingServiceSetupForm = findViewById(R.id.settingServiceSetupForm)
        settingInstantVideoForm = findViewById(R.id.settingInstantVideoForm)
        settingChatForm = findViewById(R.id.settingChatForm)
        settingVideoForm = findViewById(R.id.settingVideoForm)
        settingVideoTextMsg = findViewById(R.id.settingVideoTextMsg)
        settingInstantVideoTextMsg = findViewById(R.id.settingInstantVideoTextMsg)
        settingChatTextMsg = findViewById(R.id.settingChatTextMsg)
        settingClinicTextMsg = findViewById(R.id.settingClinicTextMsg)
        settingVideoActiveStatus = findViewById(R.id.settingVideoActiveStatus)
        settingInstantVideoActiveStatus = findViewById(R.id.settingInstantVideoActiveStatus)
        settingChatActiveStatus = findViewById(R.id.settingChatActiveStatus)
        settingClinicActiveStatus = findViewById(R.id.settingClinicActiveStatus)
        settingVideoService = findViewById(R.id.settingVideoService)
        settingInstantVideoService = findViewById(R.id.settingInstantVideoService)
        settingChatService = findViewById(R.id.settingChatService)
        settingClinicService = findViewById(R.id.settingClinicService)
        settingServiceVideoView = findViewById(R.id.settingServiceVideoView)
        settingServiceInstantVideoView = findViewById(R.id.settingServiceInstantVideoView)
        settingChatServiceView = findViewById(R.id.settingChatServiceView)
        settingVideoButton = findViewById(R.id.settingVideoButton)
        settingInstantVideoButton = findViewById(R.id.settingInstantVideoButton)
        settingChatButton = findViewById(R.id.settingChatButton)
        settingClinicOneButton = findViewById(R.id.settingClinicOneButton)
        settingClinicTwoButton = findViewById(R.id.settingClinicTwoButton)
        settingVideoCheckIcon = findViewById(R.id.settingVideoCheckIcon)
        settingInstantVideoCheckIcon = findViewById(R.id.settingInstantVideoCheckIcon)
        settingChatCheckIcon = findViewById(R.id.settingChatCheckIcon)
        settingClinicCheckIcon = findViewById(R.id.settingClinicCheckIcon)
        chatDescriptionText = findViewById(R.id.chatDescriptionText)
        settingChatShortText = findViewById(R.id.settingChatShortText)
        chatPriceText = findViewById(R.id.chatPriceText)
        chatValidityEditText = findViewById(R.id.chatValidityEditText)
        chatValidityDaySpinner = findViewById(R.id.chatValidityDaySpinner)
        chaSettingUpdateButton = findViewById(R.id.chaSettingUpdateButton)
        settingChatEnableDisable = findViewById(R.id.settingChatEnableDisable)
        settingChatEnableDisableSwitch = findViewById(R.id.settingChatEnableDisableSwitch)
        auto_no_show_video = findViewById(R.id.auto_no_show)
        help_no_show_video_hyper_text = findViewById(R.id.help_video)
        help_no_show_instant_video_hyper_text = findViewById(R.id.help_instant_video)
        help_video_setup = findViewById(R.id.help_video_setup)
        if (intent.hasExtra("Service_Product_ID")) {
            selectedserviceProd_ID = intent.getIntExtra("Service_Product_ID", 0)
        }
        if (intent.hasExtra("Dr_Service_ID")) {
            selectedDrService_ID = intent.getIntExtra("Dr_Service_ID", 0)
        }
        if (intent.hasExtra("Internal_Supersaas_ID")) {
            Internal_Supersaas_ID = intent.getIntExtra("Internal_Supersaas_ID", 0)
        }
        createNewAccountViewModel = ViewModelProvider(this).get(
            CreateNewAccountViewModel::class.java
        )
        createNewAccountViewModel!!.init()
        viewModel = ViewModelProvider(this).get(SettingsViewModel::class.java)
        MyClinicGlobalClass.logUserActionEvent(
            ApiUrls.doctorId,
            getString(R.string.ServiceSetupScreenImpression),
            null
        )
        settingChatEnableDisable!!.setOnClickListener {
            if (settingChatEnableDisableSwitch!!.isChecked) {
                upDateServicePermission(2, false)
                settingChatEnableDisableSwitch!!.isChecked = false
                settingChatShortText!!.isEnabled = false
                chatDescriptionText!!.isEnabled = false
                chatPriceText!!.isEnabled = false
                chatValidityEditText!!.isEnabled = false
                chatValidityDaySpinner!!.isEnabled = false
                chaSettingUpdateButton!!.isEnabled = false
                chaSettingUpdateButton!!.setBackgroundColor(
                    ContextCompat.getColor(
                        applicationContext,
                        R.color.colorGrey1
                    )
                )
            } else {
                upDateServicePermission(2, true)
                settingChatEnableDisableSwitch!!.isChecked = true
                settingChatShortText!!.isEnabled = true
                chatDescriptionText!!.isEnabled = true
                chatPriceText!!.isEnabled = true
                chatValidityEditText!!.isEnabled = true
                chatValidityDaySpinner!!.isEnabled = true
                chaSettingUpdateButton!!.isEnabled = true
                chaSettingUpdateButton!!.setBackgroundColor(
                    ContextCompat.getColor(
                        applicationContext,
                        R.color.colorAccent
                    )
                )
                chaSettingUpdateButton!!.setBackgroundResource(R.drawable.drawable_capsule_view)
            }
        }
        help_no_show_instant_video_hyper_text!!.setOnClickListener({ createHyperLinkTextDialog() })
        help_no_show_video_hyper_text!!.setOnClickListener({ createHyperLinkTextDialog() })
        help_video_setup!!.setOnClickListener({ createHyperLinkTextDialog() })
        settingChatEnableDisableSwitch!!.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                upDateServicePermission(2, true)
                settingChatShortText!!.isEnabled = true
                chatDescriptionText!!.isEnabled = true
                chatPriceText!!.isEnabled = true
                chatValidityEditText!!.isEnabled = true
                chatValidityDaySpinner!!.isEnabled = true
                chaSettingUpdateButton!!.isEnabled = true
                chaSettingUpdateButton!!.setBackgroundColor(
                    ContextCompat.getColor(
                        applicationContext,
                        R.color.colorAccent
                    )
                )
                chaSettingUpdateButton!!.setBackgroundResource(R.drawable.drawable_capsule_view)
            } else {
                upDateServicePermission(2, false)
                settingChatShortText!!.isEnabled = false
                chatDescriptionText!!.isEnabled = false
                chatPriceText!!.isEnabled = false
                chatValidityEditText!!.isEnabled = false
                chatValidityDaySpinner!!.isEnabled = false
                chaSettingUpdateButton!!.isEnabled = false
                chaSettingUpdateButton!!.setBackgroundColor(
                    ContextCompat.getColor(
                        applicationContext,
                        R.color.colorGrey1
                    )
                )
            }
        }
        chaSettingUpdateButton!!.setOnClickListener({
            if (chatSetupFlagClick == 1) {
                if (settingChatShortText!!.text.toString().equals("", ignoreCase = true)) {
                    Toast.makeText(
                        this@SettingsFormActivity,
                        "Please enter short name",
                        Toast.LENGTH_SHORT
                    ).show()
                } else if (chatDescriptionText!!.text.toString()
                        .equals("", ignoreCase = true)
                ) {
                    Toast.makeText(
                        this@SettingsFormActivity,
                        "Please enter description",
                        Toast.LENGTH_SHORT
                    ).show()
                } else if (chatPriceText!!.text.toString().equals("", ignoreCase = true)) {
                    Toast.makeText(
                        this@SettingsFormActivity,
                        "Please enter price",
                        Toast.LENGTH_SHORT
                    ).show()
                } else if (chatValidityEditText!!.text.toString()
                        .equals("", ignoreCase = true)
                ) {
                    Toast.makeText(
                        this@SettingsFormActivity,
                        "Please enter duration",
                        Toast.LENGTH_SHORT
                    ).show()
                } else if (chatValidityDaySpinner!!.selectedItemPosition == 0) {
                    Toast.makeText(
                        this@SettingsFormActivity,
                        "Please select duration",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    setupChatInstantVideoSettingDetails("Chat")
                }
            } else {
                if (settingChatShortText!!.text.toString().equals("", ignoreCase = true)) {
                    Toast.makeText(
                        this@SettingsFormActivity,
                        "Please enter short name",
                        Toast.LENGTH_SHORT
                    ).show()
                } else if (chatDescriptionText!!.text.toString()
                        .equals("", ignoreCase = true)
                ) {
                    Toast.makeText(
                        this@SettingsFormActivity,
                        "Please enter description",
                        Toast.LENGTH_SHORT
                    ).show()
                } else if (chatPriceText!!.text.toString().equals("", ignoreCase = true)) {
                    Toast.makeText(
                        this@SettingsFormActivity,
                        "Please enter price",
                        Toast.LENGTH_SHORT
                    ).show()
                } else if (chatValidityEditText!!.text.toString()
                        .equals("", ignoreCase = true)
                ) {
                    Toast.makeText(
                        this@SettingsFormActivity,
                        "Please enter duration",
                        Toast.LENGTH_SHORT
                    ).show()
                } else if (chatValidityDaySpinner!!.selectedItemPosition == 0) {
                    Toast.makeText(
                        this@SettingsFormActivity,
                        "Please select duration",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    upDateChatInstantVideoSettingDetails("Chat")
                }
            }
        })

        settingDescText = findViewById(R.id.settingDescText)
        settingPriceText = findViewById(R.id.settingPriceText)
        fromDaySpinner = findViewById(R.id.fromDaySpinner)
        fromTimeSpinner = findViewById(R.id.fromTimeSpinner)
        toDaySpinner = findViewById(R.id.toDaySpinner)
        toTimeSpinner = findViewById(R.id.toTimeSpinner)
        settingInstantVideo = findViewById(R.id.settingInstantVideo)
        settingInstantVideoEnableDisable = findViewById(R.id.settingInstantVideoEnableDisable)
        settingInstantVideoSwitch = findViewById(R.id.settingInstantVideoSwitch)
        settingInstantVideoEnableDisable!!.setOnClickListener {
            if (settingInstantVideoSwitch!!.isChecked) {
                upDateInstantVideoServicePermission(false)
                settingInstantVideoSwitch!!.isChecked = false
                settingDescText!!.isEnabled = false
                settingPriceText!!.isEnabled = false
                fromDaySpinner!!.isEnabled = false
                fromTimeSpinner!!.isEnabled = false
                toDaySpinner!!.isEnabled = false
                toTimeSpinner!!.isEnabled = false
                settingInstantVideo!!.isEnabled = false
                settingInstantVideo!!.setBackgroundColor(
                    ContextCompat.getColor(
                        applicationContext,
                        R.color.colorGrey1
                    )
                )
            } else {
                upDateInstantVideoServicePermission(true)
                settingInstantVideoSwitch!!.isChecked = true
                settingDescText!!.isEnabled = true
                settingPriceText!!.isEnabled = true
                fromDaySpinner!!.isEnabled = true
                fromTimeSpinner!!.isEnabled = true
                toDaySpinner!!.isEnabled = true
                toTimeSpinner!!.isEnabled = true
                settingInstantVideo!!.isEnabled = true
                settingInstantVideo!!.setBackgroundColor(
                    ContextCompat.getColor(
                        applicationContext,
                        R.color.colorAccent
                    )
                )
                settingInstantVideo!!.setBackgroundResource(R.drawable.drawable_capsule_view)
            }
        }
        settingInstantVideoSwitch!!.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                upDateInstantVideoServicePermission(true)
                settingDescText!!.isEnabled = true
                settingPriceText!!.isEnabled = true
                fromDaySpinner!!.isEnabled = true
                fromTimeSpinner!!.isEnabled = true
                toDaySpinner!!.isEnabled = true
                toTimeSpinner!!.isEnabled = true
                settingInstantVideo!!.isEnabled = true
                settingInstantVideo!!.setBackgroundColor(
                    ContextCompat.getColor(
                        applicationContext,
                        R.color.colorAccent
                    )
                )
                settingInstantVideo!!.setBackgroundResource(R.drawable.drawable_capsule_view)


            } else {
                upDateInstantVideoServicePermission(false)
                settingDescText!!.isEnabled = false
                settingPriceText!!.isEnabled = false
                fromDaySpinner!!.isEnabled = false
                fromTimeSpinner!!.isEnabled = false
                toDaySpinner!!.isEnabled = false
                toTimeSpinner!!.isEnabled = false
                settingInstantVideo!!.isEnabled = false
                settingInstantVideo!!.setBackgroundColor(
                    ContextCompat.getColor(
                        applicationContext,
                        R.color.colorGrey1
                    )
                )
            }
        }

        settingInstantVideo!!.setOnClickListener(View.OnClickListener {
            try {
                if (instantVideoSetupFlagClick == 1) {
                    val timeformatter = SimpleDateFormat("hh:mm")
                    if (appUtilities!!.timeFormatPreferences(applicationContext) == 12) {
                        if (fromTimeSpinner!!.selectedItem.toString()
                                .equals("00:00 AM", ignoreCase = true)
                        ) {
                            fromTime = "00:00"
                            fromTimeDate = timeformatter.parse(fromTime)
                        } else {
                            fromTime = appUtilities!!.changeDateFormat(
                                "hh:mm a",
                                "HH:mm",
                                fromTimeSpinner!!.selectedItem.toString()
                            )
                            fromTimeDate = timeformatter.parse(fromTime)
                        }
                        if (toTimeSpinner!!.selectedItem.toString()
                                .equals("00:00 AM", ignoreCase = true)
                        ) {
                            toTime = "00:00"
                            toTimeDate = timeformatter.parse(toTime)
                        } else {
                            toTime = appUtilities!!.changeDateFormat(
                                "hh:mm a",
                                "HH:mm",
                                toTimeSpinner!!.selectedItem.toString()
                            )
                            toTimeDate = timeformatter.parse(toTime)
                        }
                    } else {
                        fromTime = fromTimeSpinner!!.selectedItem.toString()
                        fromTimeDate = timeformatter.parse(fromTime)
                        toTime = toTimeSpinner!!.selectedItem.toString()
                        toTimeDate = timeformatter.parse(toTime)
                    }
                    if (auto_no_show_spinner_instant_video!!.selectedItem.toString().equals(
                            "yes",
                            ignoreCase = true
                        ) && no_show_limit_instant_video_et!!.text.toString()
                            .equals("", ignoreCase = true)
                    ) {
                        Toast.makeText(
                            this@SettingsFormActivity,
                            "Please Enter No-show Limit",
                            Toast.LENGTH_SHORT
                        ).show()
                        return@OnClickListener
                    }
                    if (settingDescText!!.text.toString().isEmpty()) {
                        Toast.makeText(
                            this@SettingsFormActivity,
                            "Please enter description",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else if (settingPriceText!!.text.toString().isEmpty()) {
                        Toast.makeText(
                            this@SettingsFormActivity,
                            "Please enter price",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else if (auto_no_show_spinner_instant_video!!.selectedItem.toString()
                            .equals("Yes", ignoreCase = true)
                    ) {
                        if (no_show_limit_instant_video_et!!.text.toString()
                                .toInt() >= 5 && no_show_limit_instant_video_et!!.text.toString()
                                .toInt() <= 720
                        ) {
                            if (no_show_limit_instant_video_et!!.text.toString().toInt() % 5 == 0) {
                                setupChatInstantVideoSettingDetails("Instant Video")
                            } else {
                                Toast.makeText(
                                    this@SettingsFormActivity,
                                    "Please set no-show limit in multiples of 5 only (e.g. 5,10,15...)",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        } else {
                            Toast.makeText(
                                this@SettingsFormActivity,
                                "Please enter a value between 5 minutes to 720 minutes (12 hours)",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } else {
                        setupChatInstantVideoSettingDetails("Instant Video")
                    }


                } else {
                    val timeformatter = SimpleDateFormat("hh:mm")
                    if (appUtilities!!.timeFormatPreferences(applicationContext) == 12) {
                        if (fromTimeSpinner!!.selectedItem.toString()
                                .equals("Select", ignoreCase = true)
                        ) {
//                            Toast.makeText(
//                                this@SettingsFormActivity,
//                                "Please select from time",
//                                Toast.LENGTH_SHORT).show()
                        } else if (fromTimeSpinner!!.selectedItem.toString()
                                .equals("00:00 AM", ignoreCase = true)
                        ) {
                            fromTime = "00:00"
                            fromTimeDate = timeformatter.parse(fromTime)
                        } else {
                            fromTime = appUtilities!!.changeDateFormat(
                                "hh:mm a",
                                "HH:mm",
                                fromTimeSpinner!!.selectedItem.toString()
                            )
                            fromTimeDate = timeformatter.parse(fromTime)
                        }

                        if (toTimeSpinner!!.selectedItem.toString()
                                .equals("Select", ignoreCase = true)
                        ) {
//                            Toast.makeText(
//                                this@SettingsFormActivity,
//                                "Please select to time",
//                                Toast.LENGTH_SHORT).show()
                        } else if (toTimeSpinner!!.selectedItem.toString()
                                .equals("00:00 AM", ignoreCase = true)
                        ) {
                            toTime = "00:00"
                            toTimeDate = timeformatter.parse(toTime)
                        } else {
                            toTime = appUtilities!!.changeDateFormat(
                                "hh:mm a",
                                "HH:mm",
                                toTimeSpinner!!.selectedItem.toString()
                            )
                            toTimeDate = timeformatter.parse(toTime)
                        }
                    } else {
                        fromTime = fromTimeSpinner!!.selectedItem.toString()
                        fromTimeDate = timeformatter.parse(fromTime)
                        toTime = toTimeSpinner!!.selectedItem.toString()
                        toTimeDate = timeformatter.parse(toTime)
                    }

                    if (settingDescText!!.text.toString().isEmpty()) {
                        Toast.makeText(
                            this@SettingsFormActivity,
                            "Please enter description",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else if (settingPriceText!!.text.toString().isEmpty()) {
                        Toast.makeText(
                            this@SettingsFormActivity,
                            "Please enter price",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else if (auto_no_show_spinner_instant_video!!.selectedItem.toString().equals(
                            "Yes",
                            ignoreCase = true
                        ) && no_show_limit_instant_video_et!!.text.toString()
                            .equals("", ignoreCase = true)
                    ) {
                        Toast.makeText(
                            this@SettingsFormActivity,
                            "Please Enter No-Show limit",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        if (auto_no_show_spinner_instant_video!!.selectedItem.toString()
                                .equals("Yes", ignoreCase = true)
                        ) {
                            if (no_show_limit_instant_video_et!!.text.toString()
                                    .toInt() >= 5 && no_show_limit_instant_video_et!!.text.toString()
                                    .toInt() <= 720
                            ) {
                                if (no_show_limit_instant_video_et!!.text.toString()
                                        .toInt() % 5 == 0
                                ) {
                                    upDateChatInstantVideoSettingDetails("Instant Video")
                                } else {
                                    Toast.makeText(
                                        this@SettingsFormActivity,
                                        "Please set no-show limit in multiples of 5 only (e.g. 5,10,15...)",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            } else {
                                Toast.makeText(
                                    this@SettingsFormActivity,
                                    "Please enter a value between 5 minutes to 720 minutes (12 hours)",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        } else {
                            upDateChatInstantVideoSettingDetails("Instant Video")
                        }
                    }

                    // }
                }
            } catch (e1: ParseException) {
                e1.printStackTrace()
            }

            // upDateChatInstantVideoSettingDetails("Instant Video");
        })
        rg = findViewById(R.id.radioSex)
        rbMale = findViewById(R.id.personalMale)
        rbFemale = findViewById(R.id.personalFemale)
        settingVideoShortText = findViewById(R.id.settingVideoShortText)
        settingVideoDescriptionText = findViewById(R.id.settingVideoDescriptionText)
        videoPriceInput = findViewById(R.id.videoPriceInput)
        videoAddressText = findViewById(R.id.videoAddressText)
        advanceBookingText = findViewById(R.id.advanceBookingText)
        appointmentDurationSpinner = findViewById(R.id.appointmentDurationSpinner)
        videoDurationSpinner = findViewById(R.id.videoDurationSpinner)
        settingServiceVideoShortText = findViewById(R.id.settingServiceVideoShortText)
        settingServiceVideoDescriptionText = findViewById(R.id.settingServiceVideoDescriptionText)
        videoServicePriceInput = findViewById(R.id.videoServicePriceInput)
        videoServiceAddressText = findViewById(R.id.videoServiceAddressText)
        advanceServiceBookingText = findViewById(R.id.advanceServiceBookingText)
        appointmentServiceDurationSpinner = findViewById(R.id.appointmentServiceDurationSpinner)
        videoServiceDurationSpinner = findViewById(R.id.videoServiceDurationSpinner)
        appointmentServiceDurationSpinner = findViewById(R.id.appointmentServiceDurationSpinner)
        videoServiceDurationSpinner = findViewById(R.id.videoServiceDurationSpinner)
        settingVideoUpdateButton = findViewById(R.id.settingVideoUpdateButton)
        settingServiceVideoUpdateButton = findViewById(R.id.settingServiceVideoUpdateButton)
        settingVideoEnableDisable = findViewById(R.id.settingVideoEnableDisable)
        settingVideoEnableDisableSwitch = findViewById(R.id.settingVideoEnableDisableSwitch)
        videoShortNameTxtInput = findViewById(R.id.videoShortNameTxtInput)
        videoDescriptionTxtInput = findViewById(R.id.videoDescriptionTxtInput)
        videoPriceTxtInput = findViewById(R.id.videoPriceTxtInput)
        appointmentDurationLayout = findViewById(R.id.appointmentDurationLayout)
        videoAdvanceBookingLayout = findViewById(R.id.videoAdvanceBookingLayout)
        videoAdvanceBookingFromLayout = findViewById(R.id.videoAdvanceBookingFromLayout)
        settingServiceRecycleView = findViewById(R.id.settingServiceRecycleView)
        auto_no_show_spinner_video = findViewById(R.id.auto_no_show_spinner)
        auto_no_show_spinner_instant_video = findViewById(R.id.auto_no_show_spinner_instant_video)
        auto_no_show_spinner_video_setup = findViewById(R.id.auto_no_show_spinner_video_setup)
        no_show_limit_instant_video_et = findViewById(R.id.no_show_limit_instant_video_et)
        no_show_limit_video_et = findViewById(R.id.no_show_limit_video_et)
        no_show_limit_video_setup_et = findViewById(R.id.no_show_limit_video_setup_et)
        no_show_limt_video = findViewById(R.id.no_show_limt)
        no_show_limt_instant_video = findViewById(R.id.no_show_limt_instant_video)
        no_show_limt_video_setup = findViewById(R.id.no_show_limt_video_setup)
        settingVideoEnableDisable!!.setOnClickListener {
            if (settingVideoEnableDisableSwitch!!.isChecked) {
                if (intent.getStringExtra("Title").toString()
                        .equals("Clinic 1", ignoreCase = true) || intent.getStringExtra("Title")
                        .toString()
                        .equals("Clinic 2", ignoreCase = true) || intent.getStringExtra("Title")
                        .toString().equals("Clinic 3", ignoreCase = true)
                ) {
                    settingVideoEnableDisableSwitch!!.isChecked = false
                    videoShortNameTxtInput!!.visibility = View.GONE
                    videoDescriptionTxtInput!!.visibility = View.GONE
                    videoPriceTxtInput!!.visibility = View.GONE
                    appointmentDurationLayout!!.visibility = View.GONE
                    videoAdvanceBookingLayout!!.visibility = View.GONE
                    videoAdvanceBookingFromLayout!!.visibility = View.GONE
                    settingServiceRecycleView!!.visibility = View.GONE
                    videoScheduleLayout!!.visibility = View.GONE
                    upDateServicePermission(3, false)
                } else {
                    settingVideoEnableDisableSwitch!!.isChecked = false
                    settingVideoShortText!!.isEnabled = false
                    settingVideoDescriptionText!!.isEnabled = false
                    videoPriceInput!!.isEnabled = false
                    videoAddressText!!.isEnabled = false
                    advanceBookingText!!.isEnabled = false
                    appointmentDurationSpinner!!.isEnabled = false
                    videoDurationSpinner!!.isEnabled = false
                    videoScheduleLayout!!.isEnabled = false
                    videoScheduleLayout!!.setBackgroundColor(
                        ContextCompat.getColor(
                            applicationContext,
                            R.color.colorGrey1
                        )
                    )
                    upDateServicePermission(1, false)
                }
            } else {
                if (intent.getStringExtra("Title").toString()
                        .equals("Clinic 1", ignoreCase = true) || intent.getStringExtra("Title")
                        .toString()
                        .equals("Clinic 2", ignoreCase = true) || intent.getStringExtra("Title")
                        .toString().equals("Clinic 3", ignoreCase = true)
                ) {
                    settingVideoEnableDisableSwitch!!.isChecked = true
                    videoShortNameTxtInput!!.visibility = View.VISIBLE
                    videoDescriptionTxtInput!!.visibility = View.VISIBLE
                    videoPriceTxtInput!!.visibility = View.VISIBLE
                    appointmentDurationLayout!!.visibility = View.VISIBLE
                    videoAdvanceBookingLayout!!.visibility = View.VISIBLE
                    videoAdvanceBookingFromLayout!!.visibility = View.VISIBLE
                    settingServiceRecycleView!!.visibility = View.VISIBLE
                    videoScheduleLayout!!.visibility = View.VISIBLE
                    upDateServicePermission(3, true)
                } else {
                    settingVideoEnableDisableSwitch!!.isChecked = true
                    settingVideoShortText!!.isEnabled = true
                    settingVideoDescriptionText!!.isEnabled = true
                    videoPriceInput!!.isEnabled = true
                    videoAddressText!!.isEnabled = true
                    advanceBookingText!!.isEnabled = true
                    appointmentDurationSpinner!!.isEnabled = true
                    videoDurationSpinner!!.isEnabled = true
                    videoScheduleLayout!!.isEnabled = true
                    videoScheduleLayout!!.setBackgroundColor(
                        ContextCompat.getColor(
                            applicationContext,
                            R.color.colorAccent
                        )
                    )
                    upDateServicePermission(1, true)
                }
            }
        }
        settingVideoEnableDisableSwitch!!.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                if (intent.getStringExtra("Title").toString()
                        .equals("Clinic 1", ignoreCase = true) || intent.getStringExtra("Title")
                        .toString()
                        .equals("Clinic 2", ignoreCase = true) || intent.getStringExtra("Title")
                        .toString().equals("Clinic 3", ignoreCase = true)
                ) {
                    settingVideoEnableDisableSwitch!!.isChecked = true
                    videoShortNameTxtInput!!.visibility = View.VISIBLE
                    videoDescriptionTxtInput!!.visibility = View.VISIBLE
                    videoPriceTxtInput!!.visibility = View.VISIBLE
                    appointmentDurationLayout!!.visibility = View.VISIBLE
                    videoAdvanceBookingLayout!!.visibility = View.VISIBLE
                    videoAdvanceBookingFromLayout!!.visibility = View.VISIBLE
                    settingServiceRecycleView!!.visibility = View.VISIBLE
                    videoScheduleLayout!!.visibility = View.VISIBLE
                    upDateServicePermission(3, true)
                } else {
                    settingVideoShortText!!.isEnabled = true
                    settingVideoDescriptionText!!.isEnabled = true
                    videoPriceInput!!.isEnabled = true
                    videoAddressText!!.isEnabled = true
                    advanceBookingText!!.isEnabled = true
                    appointmentDurationSpinner!!.isEnabled = true
                    videoDurationSpinner!!.isEnabled = true
                    videoScheduleLayout!!.isEnabled = true
                    videoScheduleLayout!!.setBackgroundColor(
                        ContextCompat.getColor(
                            applicationContext,
                            R.color.colorAccent
                        )
                    )
                    upDateServicePermission(1, true)
                }
            } else {
                if (intent.getStringExtra("Title").toString()
                        .equals("Clinic 1", ignoreCase = true) || intent.getStringExtra("Title")
                        .toString()
                        .equals("Clinic 2", ignoreCase = true) || intent.getStringExtra("Title")
                        .toString().equals("Clinic 3", ignoreCase = true)
                ) {
                    settingVideoEnableDisableSwitch!!.isChecked = false
                    videoShortNameTxtInput!!.visibility = View.GONE
                    videoDescriptionTxtInput!!.visibility = View.GONE
                    videoPriceTxtInput!!.visibility = View.GONE
                    appointmentDurationLayout!!.visibility = View.GONE
                    videoAdvanceBookingLayout!!.visibility = View.GONE
                    videoAdvanceBookingFromLayout!!.visibility = View.GONE
                    settingServiceRecycleView!!.visibility = View.GONE
                    videoScheduleLayout!!.visibility = View.GONE
                    // settingVideoUpdateButton.setVisibility(View.GONE);
                    upDateServicePermission(3, false)
                } else {
                    settingVideoShortText!!.isEnabled = false
                    settingVideoDescriptionText!!.isEnabled = false
                    videoPriceInput!!.isEnabled = false
                    videoAddressText!!.isEnabled = false
                    advanceBookingText!!.isEnabled = false
                    appointmentDurationSpinner!!.isEnabled = false
                    videoDurationSpinner!!.isEnabled = false
                    videoScheduleLayout!!.isEnabled = false
                    videoScheduleLayout!!.setBackgroundColor(
                        ContextCompat.getColor(
                            applicationContext,
                            R.color.colorGrey1
                        )
                    )
                    upDateServicePermission(1, false)
                }
            }
        }
        appPreference = applicationContext.getSharedPreferences(ApiUrls.appSharedPref, MODE_PRIVATE)

//        //getting current date and setting it
        val c = Calendar.getInstance()
        currentDate =
            c[Calendar.DAY_OF_MONTH].toString() + "-" + (c[Calendar.MONTH] + 1) + "-" + c[Calendar.YEAR]
        mContext = applicationContext


        spinnerTimeList = if (appUtilities!!.timeFormatPreferences(applicationContext) == 12) {
            spinner12HoursTimeList
        } else {
            spinnerTimeList
        }

        // Initializing an ArrayAdapter
        instantFromDaySpinnerArrayAdapter = ArrayAdapter(
            this, android.R.layout.simple_spinner_item, spinnerDayList as ArrayList<String>
        )
        instantFromDaySpinnerArrayAdapter!!.setDropDownViewResource(android.R.layout.simple_spinner_item)
        instantVideoFromDay!!.adapter = instantFromDaySpinnerArrayAdapter
        instantFromTimeSpinnerArrayAdapter = ArrayAdapter(
            this, android.R.layout.simple_spinner_item, spinnerTimeList
        )
        instantFromTimeSpinnerArrayAdapter!!.setDropDownViewResource(android.R.layout.simple_spinner_item)
        instantVideoFromTime!!.adapter = instantFromTimeSpinnerArrayAdapter
        instantToDaySpinnerArrayAdapter = ArrayAdapter(
            this, android.R.layout.simple_spinner_item, spinnerDayList
        )
        instantToDaySpinnerArrayAdapter!!.setDropDownViewResource(android.R.layout.simple_spinner_item)
        instantVideoToDay!!.adapter = instantToDaySpinnerArrayAdapter
        instantToTimeSpinnerArrayAdapter = ArrayAdapter(
            this, android.R.layout.simple_spinner_item, spinnerTimeList
        )
        instantToTimeSpinnerArrayAdapter!!.setDropDownViewResource(android.R.layout.simple_spinner_item)
        instantVideoToTime!!.adapter = instantToTimeSpinnerArrayAdapter
        chatDurationSpinnerArrayAdapter = ArrayAdapter(
            this, android.R.layout.simple_spinner_item, spinnerChatDurationList as ArrayList<String>
        )
        chatDurationSpinnerArrayAdapter!!.setDropDownViewResource(android.R.layout.simple_spinner_item)
        chatDuration!!.adapter = chatDurationSpinnerArrayAdapter
        videoDurationSpinnerArrayAdapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            spinnerVideoDurationList as ArrayList<String>
        )
        videoDurationSpinnerArrayAdapter!!.setDropDownViewResource(android.R.layout.simple_spinner_item)
        appointmentDurationSpinner!!.adapter = videoDurationSpinnerArrayAdapter
        videoSettingSpinnerArrayAdapter = ArrayAdapter(
            this, android.R.layout.simple_spinner_item, spinnerChatDurationList as ArrayList<String>
        )
        videoSettingSpinnerArrayAdapter!!.setDropDownViewResource(android.R.layout.simple_spinner_item)
        videoDurationSpinner!!.adapter = videoSettingSpinnerArrayAdapter


        //for service page
        videoSettingSpinnerArrayAdapter = ArrayAdapter(
            this, android.R.layout.simple_spinner_item, spinnerVideoDurationList
        )
        videoSettingSpinnerArrayAdapter!!.setDropDownViewResource(android.R.layout.simple_spinner_item)
        appointmentServiceDurationSpinner!!.adapter = videoSettingSpinnerArrayAdapter
        videoSettingSpinnerArrayAdapter = ArrayAdapter(
            this, android.R.layout.simple_spinner_item, spinnerChatDurationList as ArrayList<String>
        )
        videoSettingSpinnerArrayAdapter!!.setDropDownViewResource(android.R.layout.simple_spinner_item)
        videoServiceDurationSpinner!!.adapter = videoSettingSpinnerArrayAdapter
        videoAutoNoShowSpinnerArrayAdapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            spinner_videoAutoNoShowList as ArrayList<String>
        )
        videoAutoNoShowSpinnerArrayAdapter!!.setDropDownViewResource(android.R.layout.simple_spinner_item)
        auto_no_show_spinner_video!!.adapter = videoAutoNoShowSpinnerArrayAdapter
        instantVideoAutoNoShowSpinnerArrayAdapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            spinner_instantvideoAutoNoShowList as ArrayList<String>
        )
        videoAutoNoShowSpinnerArrayAdapter!!.setDropDownViewResource(android.R.layout.simple_spinner_item)
        auto_no_show_spinner_instant_video!!.adapter = instantVideoAutoNoShowSpinnerArrayAdapter
        videoSetUpAutoNoShowSpinnerArrayAdapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            spinner_videoSetUpAutoNoShowList as ArrayList<String>
        )
        videoAutoNoShowSpinnerArrayAdapter!!.setDropDownViewResource(android.R.layout.simple_spinner_item)
        auto_no_show_spinner_video_setup!!.adapter = videoSetUpAutoNoShowSpinnerArrayAdapter
        auto_no_show_spinner_video!!.onItemSelectedListener = this
        auto_no_show_spinner_instant_video!!.onItemSelectedListener = this
        auto_no_show_spinner_video_setup!!.onItemSelectedListener = this
        settingVideoSchedModelList = ArrayList()
        settingVideoSchedModelListTemp = ArrayList()
        settingSchedModelList = ArrayList()
        prefs = PreferenceManager.getDefaultSharedPreferences(mContext)
        settingVideoSchedListAdapter =
            SettingVideoScheduleListAdapter(
                mContext,
                settingVideoSchedModelList as ArrayList<SettingVideoScheduleModel>
            ) { v, position ->
                if (settingVideoEnableDisableSwitch!!.isChecked) {
                    scheduleClickFlag = if (intent.getStringExtra("Title")
                            .equals("Clinic 1", ignoreCase = true) || intent.getStringExtra("Title")
                            .equals("Clinic 2", ignoreCase = true) || intent.getStringExtra("Title")
                            .equals("Clinic 3", ignoreCase = true)
                    ) {
                        getSettingServiceVideoAndClinicSchedule(1, "Clinic")
                        0
                    } else {
                        if (videoSetupFlagClick == 1) {
                            setSettingServiceVideoSchedule(1, "Video")
                            videoSettingServiceScrollView!!.visibility = View.GONE
                            settingServiceVideoForm!!.visibility = View.GONE
                            nextRecyclerView!!.visibility = View.GONE
                        } else if (clinicSetupFlagClick == 1) {
                            setSettingServiceVideoSchedule(1, "Clinic")
                            videoSettingServiceScrollView!!.visibility = View.GONE
                            settingServiceVideoForm!!.visibility = View.GONE
                            nextRecyclerView!!.visibility = View.GONE
                        } else {
                            getSettingServiceVideoAndClinicSchedule(1, "Video")
                        }
                        0
                    }
                    apptStatLayout!!.visibility = View.GONE
                    settingServiceSetupLayout!!.visibility = View.GONE
                    settingInstantVideoLayout!!.visibility = View.GONE
                    settingChaLayout!!.visibility = View.GONE
                    settingVideoLayout!!.visibility = View.GONE
                    videoSettingScheduleLayout!!.visibility = View.VISIBLE
                    timeBlockLayout!!.visibility = View.GONE
                    clinicScheduleFlag = 1
                    item!!.isVisible = true
                } else if (setupTextView!!.text.toString()
                        .equals("Setup", ignoreCase = true)
                ) {
                    scheduleClickFlag = if (intent.getStringExtra("Title")
                            .equals("Clinic 1", ignoreCase = true) || intent.getStringExtra("Title")
                            .equals("Clinic 2", ignoreCase = true) || intent.getStringExtra("Title")
                            .equals("Clinic 3", ignoreCase = true)
                    ) {
                        getSettingServiceVideoAndClinicSchedule(1, "Clinic")
                        0
                    } else {
                        if (videoSetupFlagClick == 1) {
                            setSettingServiceVideoSchedule(1, "Video")
                            videoSettingServiceScrollView!!.visibility = View.GONE
                            settingServiceVideoForm!!.visibility = View.GONE
                            nextRecyclerView!!.visibility = View.GONE
                        } else if (clinicSetupFlagClick == 1) {
                            setSettingServiceVideoSchedule(1, "Clinic")
                            videoSettingServiceScrollView!!.visibility = View.GONE
                            settingServiceVideoForm!!.visibility = View.GONE
                            nextRecyclerView!!.visibility = View.GONE
                        } else {
                            getSettingServiceVideoAndClinicSchedule(1, "Video")
                        }
                        0
                    }
                    apptStatLayout!!.visibility = View.GONE
                    settingServiceSetupLayout!!.visibility = View.GONE
                    settingInstantVideoLayout!!.visibility = View.GONE
                    settingChaLayout!!.visibility = View.GONE
                    settingVideoLayout!!.visibility = View.GONE
                    videoSettingScheduleLayout!!.visibility = View.VISIBLE
                    timeBlockLayout!!.visibility = View.GONE
                    clinicScheduleFlag = 1
                    item!!.isVisible = true
                } else if (position == 1) {

                    //added by dileep 24th july 2019
                    if (intent.getStringExtra("Title")
                            .equals("Clinic 1", ignoreCase = true) || intent.getStringExtra("Title")
                            .equals("Clinic 2", ignoreCase = true) || intent.getStringExtra("Title")
                            .equals("Clinic 3", ignoreCase = true)
                    ) {
                        getSettingServiceVideoAndClinicSchedule(1, "Clinic")
                        scheduleClickFlag = 0
                    } else {
                        if (videoSetupFlagClick == 1) {
                            setSettingServiceVideoSchedule(1, "Video")
                            videoSettingServiceScrollView!!.visibility = View.GONE
                            settingServiceVideoForm!!.visibility = View.GONE
                            nextRecyclerView!!.visibility = View.GONE
                            videoSetupFlagClick = 2
                        } else if (clinicSetupFlagClick == 1) {
                            setSettingServiceVideoSchedule(1, "Clinic")
                            videoSettingServiceScrollView!!.visibility = View.GONE
                            settingServiceVideoForm!!.visibility = View.GONE
                            nextRecyclerView!!.visibility = View.GONE
                            clinicSetupFlagClick = 2
                        } else {
                            getSettingServiceVideoAndClinicSchedule(1, "Video")
                        }
                        scheduleClickFlag = 0
                    }
                    apptStatLayout!!.visibility = View.GONE
                    settingServiceSetupLayout!!.visibility = View.GONE
                    settingInstantVideoLayout!!.visibility = View.GONE
                    settingChaLayout!!.visibility = View.GONE
                    settingVideoLayout!!.visibility = View.GONE
                    videoSettingScheduleLayout!!.visibility = View.VISIBLE
                    timeBlockLayout!!.visibility = View.GONE
                    clinicScheduleFlag = 1
                    item!!.isVisible = true
                } else {
                    Toast.makeText(applicationContext, "Service is disable", Toast.LENGTH_LONG)
                        .show()
                }
            }
        serviceRecycleView!!.layoutManager = LinearLayoutManager(
            this,
            LinearLayoutManager.HORIZONTAL,
            false
        )
        serviceRecycleView!!.itemAnimator = DefaultItemAnimator()
        serviceRecycleView!!.adapter = settingVideoSchedListAdapter
        settingSchedListAdapter = SettingScheduleListAdapter(mContext, settingSchedModelList)
        settingScheduleRecycleView!!.isNestedScrollingEnabled = false
        settingScheduleRecycleView!!.layoutManager = LinearLayoutManager(
            this,
            LinearLayoutManager.VERTICAL,
            false
        )
        settingScheduleRecycleView!!.itemAnimator = DefaultItemAnimator()
        settingScheduleRecycleView!!.adapter = settingSchedListAdapter
        settingServiceServiceRecycleView!!.layoutManager = LinearLayoutManager(
            this,
            LinearLayoutManager.HORIZONTAL,
            false
        )
        settingServiceServiceRecycleView!!.itemAnimator = DefaultItemAnimator()
        settingServiceServiceRecycleView!!.adapter = settingVideoSchedListAdapter
        settingVideoUpdateButton!!.setOnClickListener({
            if (intent.getStringExtra("Title").toString()
                    .equals("Clinic 1", ignoreCase = true) || intent.getStringExtra("Title")
                    .toString()
                    .equals("Clinic 2", ignoreCase = true) || intent.getStringExtra("Title")
                    .toString().equals("Clinic 3", ignoreCase = true)
            ) {
                val clinicType = intent.getStringExtra("Title").toString()
                upDateClinicSettingDetails2(
                    drServiceIdClinicOne,
                    drServiceIdClinicTwo,
                    drServiceIdClinicThree,
                    clinicType
                )
            } else {
                upDateVideoSettingDetails()
            }
        })
        settingServiceVideoUpdateButton!!.setOnClickListener {
            if (auto_no_show_spinner_video_setup!!.selectedItem.toString()
                    .equals("Yes", ignoreCase = true) && no_show_limit_video_et!!.text
                    .toString()
                    .equals("", ignoreCase = true)
            ) {
                Toast.makeText(
                    this@SettingsFormActivity,
                    "Please enter No-Show limit",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                if (auto_no_show_spinner_video_setup!!.selectedItem.toString()
                        .equals("Yes", ignoreCase = true)
                ) {
                    if (no_show_limit_video_et!!.text.toString()
                            .toInt() >= 5 && no_show_limit_video_et!!.text.toString()
                            .toInt() <= 720
                    ) {
                        if (no_show_limit_video_et!!.text.toString().toInt() % 5 == 0) {
                            upDateVideoSettingDetailsTwoService()
                        } else {
                            Toast.makeText(
                                this@SettingsFormActivity,
                                "Please set no-show limit in multiples of 5 only (e.g. 5,10,15...)",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } else {
                        Toast.makeText(
                            this@SettingsFormActivity,
                            "Please enter a value between 5 minutes to 720 minutes (12 hours)",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    upDateVideoSettingDetailsTwoService()
                }
            }
        }
        settingVideoUpdateButton!!.setOnClickListener {
            if (intent.getStringExtra("Title").toString()
                    .equals("Clinic 1", ignoreCase = true) || intent.getStringExtra("Title")
                    .toString()
                    .equals("Clinic 2", ignoreCase = true) || intent.getStringExtra("Title")
                    .toString().equals("Clinic 3", ignoreCase = true)
            ) {
                val clinicType = intent.getStringExtra("Title").toString()
                upDateClinicSettingDetails2(
                    drServiceIdClinicOne,
                    drServiceIdClinicTwo,
                    drServiceIdClinicThree,
                    clinicType
                )
            } else {
                setupUpVideoSettingService(0)
            }
        }
        detailsHeaderText = findViewById(R.id.detailsHeaderText)
        nameEditText = findViewById(R.id.name)
        phoneEditText = findViewById(R.id.phone)
        emailEditText = findViewById(R.id.email)
        sendButton = findViewById(R.id.sendButton)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            val payUTextDetailsHtml =
                "<h5>Create your PayU account by submitting the following details and start collecting payments from patients. Once your account is created, please <a target=\"_blank\" href=\"https://onboarding.payumoney.com/app/account?partner_name=Jainam+Shah&partner_source=Affiliate+Links&partner_uuid=11ea-6eb3-ecd70636-b6d9-02aa98a2d2b0&source=Partner\">login here</a> with your email id and complete your profile (<a target=\"_blank\" href=\"https://www.youtube.com/watch?v=4dTUTH-mHjw\">Complete bank verification and document upload</a>). For any support, email whitecoats.support@payu.in</h5>"
            val result = HtmlCompat.fromHtml(payUTextDetailsHtml, HtmlCompat.FROM_HTML_MODE_LEGACY)
            detailsHeaderText!!.text = result
            detailsHeaderText!!.movementMethod = LinkMovementMethod.getInstance()
        } else {
            val payUTextDetailsHtml =
                "<h5>Create your PayU account by submitting the following details and start collecting payments from patients. Once your account is created, please <a target=\"_blank\" href=\"https://onboarding.payumoney.com/app/account?partner_name=Jainam+Shah&partner_source=Affiliate+Links&partner_uuid=11ea-6eb3-ecd70636-b6d9-02aa98a2d2b0&source=Partner\">login here</a> with your email id and complete your profile (<a target=\"_blank\" href=\"https://www.youtube.com/watch?v=4dTUTH-mHjw\">Complete bank verification and document upload</a>). For any support, email whitecoats.support@payu.in</h5>"
            val result = HtmlCompat.fromHtml(payUTextDetailsHtml, HtmlCompat.FROM_HTML_MODE_LEGACY)
            detailsHeaderText!!.text = result
            detailsHeaderText!!.movementMethod = LinkMovementMethod.getInstance()
        }
        sendButton!!.setOnClickListener {
            val nameString = nameEditText!!.text.toString().trim { it <= ' ' }
            val phoneString = phoneEditText!!.text.toString().trim { it <= ' ' }
            val emailString = emailEditText!!.text.toString().trim { it <= ' ' }
            if (nameString.isEmpty() && phoneString.isEmpty() && emailString.isEmpty()) {
                val Name = "Enter name"
                nameEditText!!.error = Name
                val phone = "Enter 10 digit phone number"
                phoneEditText!!.error = phone
                val email = "Enter email id"
                emailEditText!!.error = email
                Toast.makeText(
                    this@SettingsFormActivity,
                    "Please enter all required details",
                    Toast.LENGTH_SHORT
                ).show()
            } else if (nameString.isEmpty()) {
                val Name = "Enter name"
                nameEditText!!.error = Name
            } else if (phoneString.isEmpty() || phoneString.length < 10) {
                val phone = "Enter 10 digit phone number"
                phoneEditText!!.error = phone
            } else if (emailString.isEmpty()) {
                val email = "Enter email id"
                emailEditText!!.error = email
            } else {
                createMerchant(nameString, phoneString, emailString)
            }
        }



        avgPatientArrivalLayout!!.setOnClickListener {
            if (avgPatientArrival!!.isChecked) {
                avgPatientArrival!!.isChecked = false
                upDateApptStatics(1, false)
            } else {
                avgPatientArrival!!.isChecked = true
                upDateApptStatics(1, true)
            }
        }
        avgTimeInLobbyLayout!!.setOnClickListener {
            if (avgTimeInLobby!!.isChecked) {
                avgTimeInLobby!!.isChecked = false
                upDateApptStatics(2, false)
            } else {
                avgTimeInLobby!!.isChecked = true
                upDateApptStatics(2, true)
            }
        }
        avgConsultDelayLayout!!.setOnClickListener {
            if (avgConsultDelay!!.isChecked) {
                avgConsultDelay!!.isChecked = false
                upDateApptStatics(3, false)
            } else {
                avgConsultDelay!!.isChecked = true
                upDateApptStatics(3, true)
            }
        }
        avgConsultTimeLayout!!.setOnClickListener {
            if (avgConsultTime!!.isChecked) {
                avgConsultTime!!.isChecked = false
                upDateApptStatics(4, false)
            } else {
                avgConsultTime!!.isChecked = true
                upDateApptStatics(4, true)
            }
        }
        videoScheduleLayout!!.setOnClickListener {
            val title = intent.getStringExtra("Title")
            if (title.equals("Clinic 1", ignoreCase = true) || title.equals(
                    "Clinic 2",
                    ignoreCase = true
                ) || title.equals("Clinic 3", ignoreCase = true)
            ) {
                if (settingVideoShortText!!.text.toString().isEmpty()) {
                    Toast.makeText(
                        this@SettingsFormActivity,
                        "Please enter short name",
                        Toast.LENGTH_SHORT
                    ).show()
                } else if (settingVideoDescriptionText!!.text.toString().isEmpty()) {
                    Toast.makeText(
                        this@SettingsFormActivity,
                        "Please enter description",
                        Toast.LENGTH_SHORT
                    ).show()
                } else if (videoPriceInput!!.text.toString().isEmpty()) {
                    Toast.makeText(
                        this@SettingsFormActivity,
                        "Please enter price",
                        Toast.LENGTH_SHORT
                    ).show()
                } else if (videoAddressText!!.text.toString().isEmpty()) {
                    Toast.makeText(
                        this@SettingsFormActivity,
                        "Please enter address",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    upDateClinicSettingDetailsThree(
                        drServiceIdClinicOne,
                        drServiceIdClinicTwo,
                        drServiceIdClinicThree,
                        title
                    )
                }
            } else {
                if (settingVideoShortText!!.text.toString().isEmpty()) {
                    Toast.makeText(
                        this@SettingsFormActivity,
                        "Please enter short name",
                        Toast.LENGTH_SHORT
                    ).show()
                } else if (settingVideoDescriptionText!!.text.toString().isEmpty()) {
                    Toast.makeText(
                        this@SettingsFormActivity,
                        "Please enter description",
                        Toast.LENGTH_SHORT
                    ).show()
                } else if (videoPriceInput!!.text.toString().isEmpty()) {
                    Toast.makeText(
                        this@SettingsFormActivity,
                        "Please enter price",
                        Toast.LENGTH_SHORT
                    ).show()
                } else if (auto_no_show_spinner_video!!.selectedItem.toString()
                        .equals("Yes", ignoreCase = true) && no_show_limit_video_et!!.text
                        .toString().equals("", ignoreCase = true)
                ) {
                    Toast.makeText(
                        this@SettingsFormActivity,
                        "Please enter No-Show limit",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    if (auto_no_show_spinner_video!!.selectedItem.toString()
                            .equals("Yes", ignoreCase = true)
                    ) {
                        if (no_show_limit_video_et!!.text.toString()
                                .toInt() >= 5 && no_show_limit_video_et!!.text.toString()
                                .toInt() <= 720
                        ) {
                            if (no_show_limit_video_et!!.text.toString().toInt() % 5 == 0) {
                                upDateVideoSettingDetailsTwo()
                            } else {
                                Toast.makeText(
                                    this@SettingsFormActivity,
                                    "Please set no-show limit in multiples of 5 only (e.g. 5,10,15...)",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        } else {
                            Toast.makeText(
                                this@SettingsFormActivity,
                                "Please enter a value between 5 minutes to 720 minutes (12 hours)",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } else {
                        upDateVideoSettingDetailsTwo()
                    }
                }
            }
        }
        videoServiceScheduleLayout!!.setOnClickListener {
            val title = intent.getStringExtra("Title")
            if (title.equals("Clinic 1", ignoreCase = true) || title.equals(
                    "Clinic 2",
                    ignoreCase = true
                ) || title.equals("Clinic 3", ignoreCase = true)
            ) {
                upDateClinicSettingDetailsThree(
                    drServiceIdClinicOne,
                    drServiceIdClinicTwo,
                    drServiceIdClinicThree,
                    title
                )
            } else {
                if (videoSetupFlagClick == 1) {
                    if (settingServiceVideoShortText!!.text.toString()
                            .equals("", ignoreCase = true)
                    ) {
                        Toast.makeText(
                            this@SettingsFormActivity,
                            "Please enter short name",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else if (settingServiceVideoDescriptionText!!.text.toString()
                            .equals("", ignoreCase = true)
                    ) {
                        Toast.makeText(
                            this@SettingsFormActivity,
                            "Please enter description",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else if (videoServicePriceInput!!.text.toString()
                            .equals("", ignoreCase = true)
                    ) {
                        Toast.makeText(
                            this@SettingsFormActivity,
                            "Please enter price",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else if (appointmentServiceDurationSpinner!!.selectedItemPosition == 0) {
                        Toast.makeText(
                            this@SettingsFormActivity,
                            "Please select appointment duration",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else if (advanceServiceBookingText!!.text.toString()
                            .equals("", ignoreCase = true)
                    ) {
                        Toast.makeText(
                            this@SettingsFormActivity,
                            "Please enter advance booking duration",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else if (videoServiceDurationSpinner!!.selectedItemPosition == 0) {
                        Toast.makeText(
                            this@SettingsFormActivity,
                            "Please select advance booking duration",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        if (auto_no_show_spinner_video_setup!!.selectedItem.toString()
                                .equals("Yes", ignoreCase = true)
                        ) {
                            if (!no_show_limit_video_setup_et!!.text.toString()
                                    .equals("", ignoreCase = true)
                            ) {
                                if (no_show_limit_video_setup_et!!.text.toString()
                                        .toInt() >= 5 && no_show_limit_video_setup_et!!.text
                                        .toString().toInt() <= 720
                                ) {
                                    if (no_show_limit_video_setup_et!!.text.toString()
                                            .toInt() % 5 == 0
                                    ) {
                                        hideKeyboard()
                                        settingServiceVideoForm!!.visibility = View.GONE
                                        nextRecyclerView!!.visibility = View.VISIBLE
                                        videoSetupFlagClick = 1
                                    } else {
                                        Toast.makeText(
                                            this@SettingsFormActivity,
                                            "Please set no-show limit in multiples of 5 only (e.g. 5,10,15...)",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                } else {
                                    Toast.makeText(
                                        this@SettingsFormActivity,
                                        "Please enter a value between 5 minutes to 720 minutes (12 hours)",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            } else {
                                Toast.makeText(
                                    this@SettingsFormActivity,
                                    "Please Enter No-show Limit",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        } else {
                            hideKeyboard()
                            settingServiceVideoForm!!.visibility = View.GONE
                            nextRecyclerView!!.visibility = View.VISIBLE
                            videoSetupFlagClick = 1
                        }
                    }
                } else {
                    if (settingServiceVideoShortText!!.text.toString()
                            .equals("", ignoreCase = true)
                    ) {
                        Toast.makeText(
                            this@SettingsFormActivity,
                            "Please enter short name",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else if (settingServiceVideoDescriptionText!!.text.toString()
                            .equals("", ignoreCase = true)
                    ) {
                        Toast.makeText(
                            this@SettingsFormActivity,
                            "Please enter description",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else if (videoServicePriceInput!!.text.toString()
                            .equals("", ignoreCase = true)
                    ) {
                        Toast.makeText(
                            this@SettingsFormActivity,
                            "Please enter price",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else if (videoServiceAddressText!!.text.toString()
                            .equals("", ignoreCase = true)
                    ) {
                        Toast.makeText(
                            this@SettingsFormActivity,
                            "Please enter address",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else if (appointmentServiceDurationSpinner!!.selectedItemPosition == 0) {
                        Toast.makeText(
                            this@SettingsFormActivity,
                            "Please select appointment duration",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else if (advanceServiceBookingText!!.text.toString()
                            .equals("", ignoreCase = true)
                    ) {
                        Toast.makeText(
                            this@SettingsFormActivity,
                            "Please enter advance booking duration",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else if (videoServiceDurationSpinner!!.selectedItemPosition == 0) {
                        Toast.makeText(
                            this@SettingsFormActivity,
                            "Please select advance booking duration",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        hideKeyboard()
                        settingServiceVideoForm!!.visibility = View.GONE
                        nextRecyclerView!!.visibility = View.VISIBLE
                        clinicSetupFlagClick = 1
                    }
                }
            }
        }



        scheduleSettingButton!!.setOnClickListener {
            (settingVideoSchedModelList as ArrayList<SettingVideoScheduleModel>).clear()
            var temp = SettingVideoScheduleModel()
            temp.dayName = "EDIT"
            temp.slotOneTime = "Slot 1"
            temp.slotTwoTime = "Slot 2"
            (settingVideoSchedModelList as ArrayList<SettingVideoScheduleModel>).add(temp)
            temp = SettingVideoScheduleModel()
            temp.dayName = "Mon"
            temp.slotOneTime =
                SettingScheduleListAdapter.myArrayMonday[0].toString() + "-" + SettingScheduleListAdapter.myArrayMonday[1].toString()
            temp.slotTwoTime =
                SettingScheduleListAdapter.myArrayMonday[2].toString() + "-" + SettingScheduleListAdapter.myArrayMonday[3].toString()

            /*Adding the IDs for the selected Slots to send to server*/temp.idOne =
            settingVideoSchedModelListDup!![1].idOne
            temp.slotIdOne = settingVideoSchedModelListDup!![1].slotIdOne
            temp.scheduleIdOne = settingVideoSchedModelListDup!![1].scheduleIdOne
            temp.slotFlagIdOne = settingVideoSchedModelListDup!![1].slotFlagIdOne
            temp.idTwo = settingVideoSchedModelListDup!![1].idTwo
            temp.slotIdTwo = settingVideoSchedModelListDup!![1].slotIdTwo
            temp.scheduleIdTwo = settingVideoSchedModelListDup!![1].scheduleIdTwo
            temp.slotFlagIdTwo = settingVideoSchedModelListDup!![1].slotFlagIdTwo
            (settingVideoSchedModelList as ArrayList<SettingVideoScheduleModel>).add(temp)
            temp = SettingVideoScheduleModel()
            temp.dayName = "Tue"
            temp.slotOneTime =
                SettingScheduleListAdapter.myArrayTuesday[0].toString() + "-" + SettingScheduleListAdapter.myArrayTuesday[1].toString()
            temp.slotTwoTime =
                SettingScheduleListAdapter.myArrayTuesday[2].toString() + "-" + SettingScheduleListAdapter.myArrayTuesday[3].toString()
            temp.idOne = settingVideoSchedModelListDup!![2].idOne
            temp.slotIdOne = settingVideoSchedModelListDup!![2].slotIdOne
            temp.scheduleIdOne = settingVideoSchedModelListDup!![2].scheduleIdOne
            temp.slotFlagIdOne = settingVideoSchedModelListDup!![2].slotFlagIdOne
            temp.idTwo = settingVideoSchedModelListDup!![2].idTwo
            temp.slotIdTwo = settingVideoSchedModelListDup!![2].slotIdTwo
            temp.scheduleIdTwo = settingVideoSchedModelListDup!![2].scheduleIdTwo
            temp.slotFlagIdTwo = settingVideoSchedModelListDup!![2].slotFlagIdTwo
            (settingVideoSchedModelList as ArrayList<SettingVideoScheduleModel>).add(temp)
            temp = SettingVideoScheduleModel()
            temp.dayName = "Wed"
            temp.slotOneTime =
                SettingScheduleListAdapter.myArrayWednesday[0].toString() + "-" + SettingScheduleListAdapter.myArrayWednesday[1].toString()
            temp.slotTwoTime =
                SettingScheduleListAdapter.myArrayWednesday[2].toString() + "-" + SettingScheduleListAdapter.myArrayWednesday[3].toString()
            temp.idOne = settingVideoSchedModelListDup!![3].idOne
            temp.slotIdOne = settingVideoSchedModelListDup!![3].slotIdOne
            temp.scheduleIdOne = settingVideoSchedModelListDup!![3].scheduleIdOne
            temp.slotFlagIdOne = settingVideoSchedModelListDup!![3].slotFlagIdOne
            temp.idTwo = settingVideoSchedModelListDup!![3].idTwo
            temp.slotIdTwo = settingVideoSchedModelListDup!![3].slotIdTwo
            temp.scheduleIdTwo = settingVideoSchedModelListDup!![3].scheduleIdTwo
            temp.slotFlagIdTwo = settingVideoSchedModelListDup!![3].slotFlagIdTwo
            (settingVideoSchedModelList as ArrayList<SettingVideoScheduleModel>).add(temp)
            temp = SettingVideoScheduleModel()
            temp.dayName = "Thu"
            temp.slotOneTime =
                SettingScheduleListAdapter.myArrayThursday[0].toString() + "-" + SettingScheduleListAdapter.myArrayThursday[1].toString()
            temp.slotTwoTime =
                SettingScheduleListAdapter.myArrayThursday[2].toString() + "-" + SettingScheduleListAdapter.myArrayThursday[3].toString()
            temp.idOne = settingVideoSchedModelListDup!![4].idOne
            temp.slotIdOne = settingVideoSchedModelListDup!![4].slotIdOne
            temp.scheduleIdOne = settingVideoSchedModelListDup!![4].scheduleIdOne
            temp.slotFlagIdOne = settingVideoSchedModelListDup!![4].slotFlagIdOne
            temp.idTwo = settingVideoSchedModelListDup!![4].idTwo
            temp.slotIdTwo = settingVideoSchedModelListDup!![4].slotIdTwo
            temp.scheduleIdTwo = settingVideoSchedModelListDup!![4].scheduleIdTwo
            temp.slotFlagIdTwo = settingVideoSchedModelListDup!![4].slotFlagIdTwo
            (settingVideoSchedModelList as ArrayList<SettingVideoScheduleModel>).add(temp)
            temp = SettingVideoScheduleModel()
            temp.dayName = "Fri"
            temp.slotOneTime =
                SettingScheduleListAdapter.myArrayFriday[0].toString() + "-" + SettingScheduleListAdapter.myArrayFriday[1].toString()
            temp.slotTwoTime =
                SettingScheduleListAdapter.myArrayFriday[2].toString() + "-" + SettingScheduleListAdapter.myArrayFriday[3].toString()
            temp.idOne = settingVideoSchedModelListDup!![5].idOne
            temp.slotIdOne = settingVideoSchedModelListDup!![5].slotIdOne
            temp.scheduleIdOne = settingVideoSchedModelListDup!![5].scheduleIdOne
            temp.slotFlagIdOne = settingVideoSchedModelListDup!![5].slotFlagIdOne
            temp.idTwo = settingVideoSchedModelListDup!![5].idTwo
            temp.slotIdTwo = settingVideoSchedModelListDup!![5].slotIdTwo
            temp.scheduleIdTwo = settingVideoSchedModelListDup!![5].scheduleIdTwo
            temp.slotFlagIdTwo = settingVideoSchedModelListDup!![5].slotFlagIdTwo
            (settingVideoSchedModelList as ArrayList<SettingVideoScheduleModel>).add(temp)
            temp = SettingVideoScheduleModel()
            temp.dayName = "Sat"
            temp.slotOneTime =
                SettingScheduleListAdapter.myArraySaturday[0].toString() + "-" + SettingScheduleListAdapter.myArraySaturday[1].toString()
            temp.slotTwoTime =
                SettingScheduleListAdapter.myArraySaturday[2].toString() + "-" + SettingScheduleListAdapter.myArraySaturday[3].toString()
            temp.idOne = settingVideoSchedModelListDup!![6].idOne
            temp.slotIdOne = settingVideoSchedModelListDup!![6].slotIdOne
            temp.scheduleIdOne = settingVideoSchedModelListDup!![6].scheduleIdOne
            temp.slotFlagIdOne = settingVideoSchedModelListDup!![6].slotFlagIdOne
            temp.idTwo = settingVideoSchedModelListDup!![6].idTwo
            temp.slotIdTwo = settingVideoSchedModelListDup!![6].slotIdTwo
            temp.scheduleIdTwo = settingVideoSchedModelListDup!![6].scheduleIdTwo
            temp.slotFlagIdTwo = settingVideoSchedModelListDup!![6].slotFlagIdTwo
            (settingVideoSchedModelList as ArrayList<SettingVideoScheduleModel>).add(temp)
            temp = SettingVideoScheduleModel()
            temp.dayName = "Sun"
            temp.slotOneTime =
                SettingScheduleListAdapter.myArraySunday[0].toString() + "-" + SettingScheduleListAdapter.myArraySunday[1].toString()
            temp.slotTwoTime =
                SettingScheduleListAdapter.myArraySunday[2].toString() + "-" + SettingScheduleListAdapter.myArraySunday[3].toString()
            temp.idOne = settingVideoSchedModelListDup!![7].idOne
            temp.slotIdOne = settingVideoSchedModelListDup!![7].slotIdOne
            temp.scheduleIdOne = settingVideoSchedModelListDup!![7].scheduleIdOne
            temp.slotFlagIdOne = settingVideoSchedModelListDup!![7].slotFlagIdOne
            temp.idTwo = settingVideoSchedModelListDup!![7].idTwo
            temp.slotIdTwo = settingVideoSchedModelListDup!![7].slotIdTwo
            temp.scheduleIdTwo = settingVideoSchedModelListDup!![7].scheduleIdTwo
            temp.slotFlagIdTwo = settingVideoSchedModelListDup!![7].slotFlagIdTwo
            (settingVideoSchedModelList as ArrayList<SettingVideoScheduleModel>).add(temp)
            settingVideoSchedListAdapter!!.notifyDataSetChanged()

            apptStatLayout!!.visibility = View.GONE
            settingServiceSetupLayout!!.visibility = View.GONE
            settingInstantVideoLayout!!.visibility = View.GONE
            settingChaLayout!!.visibility = View.GONE
            settingVideoLayout!!.visibility = View.VISIBLE
            videoSettingScheduleLayout!!.visibility = View.GONE
            timeBlockLayout!!.visibility = View.GONE
            if (intent.getStringExtra("Title").equals("Clinic 1", ignoreCase = true)) {
                videoAddressInputLayout!!.visibility = View.VISIBLE
            } else {
                videoAddressInputLayout!!.visibility = View.GONE
            }
        }
        toolbar.setNavigationOnClickListener {
            if (scheduleClickFlag == 1) {
                super.getOnBackPressedDispatcher().onBackPressed() // Implemented by activity
            } else if (intent.getStringExtra("Title")
                    .equals("Clinic 1", ignoreCase = true) || intent.getStringExtra("Title")
                    .equals("Clinic 2", ignoreCase = true) || intent.getStringExtra("Title")
                    .equals("Clinic 3", ignoreCase = true)
            ) {
                if (scheduleClickFlag == 0) {
                    super.getOnBackPressedDispatcher().onBackPressed() // Implemented by activity
                } else {
                    apptStatLayout!!.visibility = View.GONE
                    settingServiceSetupLayout!!.visibility = View.GONE
                    settingInstantVideoLayout!!.visibility = View.GONE
                    settingChaLayout!!.visibility = View.GONE
                    settingVideoLayout!!.visibility = View.VISIBLE
                    videoSettingScheduleLayout!!.visibility = View.GONE
                    videoAddressInputLayout!!.visibility = View.VISIBLE
                    timeBlockLayout!!.visibility = View.GONE
                    scheduleClickFlag = 1
                    item!!.isVisible = false
                }
            } else if (intent.getStringExtra("Title").equals("Video Setting", ignoreCase = true)) {
                if (scheduleClickFlag == 0) {
                    super.getOnBackPressedDispatcher().onBackPressed() // Implemented by activity
                } else {
                    apptStatLayout!!.visibility = View.GONE
                    settingServiceSetupLayout!!.visibility = View.GONE
                    settingInstantVideoLayout!!.visibility = View.GONE
                    settingChaLayout!!.visibility = View.GONE
                    settingVideoLayout!!.visibility = View.VISIBLE
                    videoSettingScheduleLayout!!.visibility = View.GONE
                    videoAddressInputLayout!!.visibility = View.GONE
                    timeBlockLayout!!.visibility = View.GONE
                    scheduleClickFlag = 1
                    item!!.isVisible = false
                }
            } else if (videoSetupFlagClick == 1 && nextRecyclerView!!.visibility == View.VISIBLE) {
                //added by dileep 24th july 2019
                settingServiceVideoForm!!.visibility = View.VISIBLE
                nextRecyclerView!!.visibility = View.GONE
            } else if (videoSetupFlagClick == 1 && nextRecyclerView!!.visibility == View.GONE) {
                videoSetupFlagClick = 0
                super.getOnBackPressedDispatcher().onBackPressed()
            } else if (videoSetupFlagClick == 2) {
                videoSettingScheduleLayout!!.visibility = View.GONE
                nextRecyclerView!!.visibility = View.VISIBLE
                videoSettingServiceScrollView!!.visibility = View.VISIBLE
                videoSetupFlagClick = 1
                item!!.isVisible = false
            } else if (clinicSetupFlagClick == 1 && nextRecyclerView!!.visibility == View.VISIBLE) {
                settingServiceVideoForm!!.visibility = View.VISIBLE
                nextRecyclerView!!.visibility = View.GONE

            } else if (clinicSetupFlagClick == 1 && nextRecyclerView!!.visibility == View.GONE) {
                clinicSetupFlagClick = 0
                super.getOnBackPressedDispatcher().onBackPressed()
            } else if (clinicSetupFlagClick == 2) {
                videoSettingScheduleLayout!!.visibility = View.GONE
                nextRecyclerView!!.visibility = View.VISIBLE // added  new line
                videoSettingServiceScrollView!!.visibility = View.VISIBLE
                clinicSetupFlagClick = 1
                item!!.isVisible = false
            } else {
                super.getOnBackPressedDispatcher().onBackPressed() // Implemented by activity
            }
        }
        settingVideoButton!!.setOnClickListener { //MyClinicGlobalClass.logUserActionEvent(AppConfigClass.doctorId, getString(R.string.ServiceSetupVideo), null);
            MyClinicGlobalClass.logUserActionEvent(
                ApiUrls.doctorId, getString(
                    R.string.SetUpVideoPracticeButton
                ), null
            )
            videoSetupFlagClick = 1
            checkDoctorMerchantStatus()
        }
        settingClinicOneButton!!.setOnClickListener { //MyClinicGlobalClass.logUserActionEvent(AppConfigClass.doctorId, getString(R.string.ServiceSetupClinic), null);
            MyClinicGlobalClass.logUserActionEvent(
                ApiUrls.doctorId, getString(
                    R.string.SetUpClinicPracticeButton
                ), null
            )
            clinicSetupFlagClick = 1
            apptStatLayout!!.visibility = View.GONE
            settingServiceSetupLayout!!.visibility = View.GONE
            settingInstantVideoLayout!!.visibility = View.GONE
            settingChaLayout!!.visibility = View.GONE
            settingVideoLayout!!.visibility = View.GONE
            videoSettingScheduleLayout!!.visibility = View.GONE
            videoAddressInputLayout!!.visibility = View.GONE
            videoSettingScrollView!!.visibility = View.GONE
            timeBlockLayout!!.visibility = View.GONE
            videoSettingServiceScrollView!!.visibility = View.VISIBLE
            settingServiceVideoForm!!.visibility = View.VISIBLE
            videoServiceAddressInputLayout!!.visibility = View.VISIBLE
            no_show_lay__video_setup!!.visibility = View.GONE
            videoServiceSettingScheduleLayout!!.visibility = View.GONE
            setupVideoService(3, "Clinic")
        }
        settingInstantVideoButton!!.setOnClickListener { // MyClinicGlobalClass.logUserActionEvent(AppConfigClass.doctorId, getString(R.string.ServiceSetupInstantVideo), null);
            MyClinicGlobalClass.logUserActionEvent(
                ApiUrls.doctorId,
                getString(R.string.SetUpInstantVideoPracticeButton),
                null
            )
            instantVideoSetupFlagClick = 1
            checkDoctorMerchantStatus()
        }
        //get Setup status
        setUpStatus
        settingChatButton!!.setOnClickListener { //MyClinicGlobalClass.logUserActionEvent(AppConfigClass.doctorId, getString(R.string.ServiceSetupChat), null);
            MyClinicGlobalClass.logUserActionEvent(
                ApiUrls.doctorId, getString(
                    R.string.SetUpChatPracticeButton
                ), null
            )
            chatSetupFlagClick = 1
            checkDoctorMerchantStatus()
        }
        val caseNo = intent.getIntExtra("FormType", 0)
        when (caseNo) {
            1, 2, 5, 4 -> {
                apptStatLayout!!.visibility = View.GONE
                settingServiceSetupLayout!!.visibility = View.GONE
                settingInstantVideoLayout!!.visibility = View.GONE
                settingChaLayout!!.visibility = View.GONE
                settingVideoLayout!!.visibility = View.GONE
                videoSettingScheduleLayout!!.visibility = View.GONE
                videoSettingScrollView!!.visibility = View.GONE
                timeBlockLayout!!.visibility = View.GONE
            }
            3 -> {
                apptStatLayout!!.visibility = View.GONE
                settingServiceSetupLayout!!.visibility = View.GONE
                settingInstantVideoLayout!!.visibility = View.GONE
                settingChaLayout!!.visibility = View.GONE
                settingVideoLayout!!.visibility = View.GONE
                videoSettingScheduleLayout!!.visibility = View.GONE
                videoSettingScrollView!!.visibility = View.GONE
                timeBlockLayout!!.visibility = View.GONE
                bankDetailsMerchant
            }
            6 -> {
                apptStatics
                apptStatLayout!!.visibility = View.VISIBLE
                settingServiceSetupLayout!!.visibility = View.GONE
                settingInstantVideoLayout!!.visibility = View.GONE
                settingChaLayout!!.visibility = View.GONE
                settingVideoLayout!!.visibility = View.GONE
                videoSettingScheduleLayout!!.visibility = View.GONE
                videoSettingScrollView!!.visibility = View.GONE
                timeBlockLayout!!.visibility = View.GONE
                avgPatientArrival!!.setOnCheckedChangeListener { buttonView, isChecked ->
                    upDateApptStatics(
                        1,
                        isChecked
                    )
                }
                avgTimeInLobby!!.setOnCheckedChangeListener { buttonView, isChecked ->
                    upDateApptStatics(
                        2,
                        isChecked
                    )
                }
                avgConsultDelay!!.setOnCheckedChangeListener({ buttonView, isChecked ->
                    upDateApptStatics(
                        3,
                        isChecked
                    )
                })
                avgConsultTime!!.setOnCheckedChangeListener({ buttonView, isChecked ->
                    upDateApptStatics(
                        4,
                        isChecked
                    )
                })
            }
            7 -> {
                doctorService
                apptStatLayout!!.visibility = View.GONE
                settingServiceSetupLayout!!.visibility = View.VISIBLE
                settingInstantVideoLayout!!.visibility = View.GONE
                settingChaLayout!!.visibility = View.GONE
                settingVideoLayout!!.visibility = View.GONE
                videoSettingScheduleLayout!!.visibility = View.GONE
                videoSettingScrollView!!.visibility = View.GONE
                timeBlockLayout!!.visibility = View.GONE
                if (sharedPref!!.isPrefExists("Video")) {
                    settingVideoService!!.visibility = View.VISIBLE
                    settingServiceVideoView!!.visibility = View.VISIBLE
                } else {
                    settingVideoService!!.visibility = View.GONE
                    settingServiceVideoView!!.visibility = View.GONE
                }
                if (sharedPref!!.isPrefExists("Instant Video")) {
                    settingInstantVideoService!!.visibility = View.VISIBLE
                    settingServiceInstantVideoView!!.visibility = View.VISIBLE
                } else {
                    settingInstantVideoService!!.visibility = View.GONE
                    settingServiceInstantVideoView!!.visibility = View.GONE
                }
                if (sharedPref!!.isPrefExists("Chat")) {
                    settingChatService!!.visibility = View.VISIBLE
                    settingChatServiceView!!.visibility = View.VISIBLE
                } else {
                    settingChatService!!.visibility = View.GONE
                    settingChatServiceView!!.visibility = View.GONE
                }
            }
            8 -> {
                updateChatAndInstantVideoSettingServices
                apptStatLayout!!.visibility = View.GONE
                settingServiceSetupLayout!!.visibility = View.GONE
                settingInstantVideoLayout!!.visibility = View.VISIBLE
                settingChaLayout!!.visibility = View.GONE
                settingVideoLayout!!.visibility = View.GONE
                videoSettingScheduleLayout!!.visibility = View.GONE
                videoSettingScrollView!!.visibility = View.GONE
                timeBlockLayout!!.visibility = View.GONE
            }
            9 -> {
                updateChatAndInstantVideoSettingServices
                apptStatLayout!!.visibility = View.GONE
                settingServiceSetupLayout!!.visibility = View.GONE
                settingInstantVideoLayout!!.visibility = View.GONE
                settingChaLayout!!.visibility = View.VISIBLE
                settingVideoLayout!!.visibility = View.GONE
                videoSettingScheduleLayout!!.visibility = View.GONE
                videoSettingScrollView!!.visibility = View.GONE
                timeBlockLayout!!.visibility = View.GONE
            }
            10 -> {
                apptStatLayout!!.visibility = View.GONE
                settingServiceSetupLayout!!.visibility = View.GONE
                settingInstantVideoLayout!!.visibility = View.GONE
                settingChaLayout!!.visibility = View.GONE
                settingVideoLayout!!.visibility = View.VISIBLE
                auto_no_show_video!!.visibility = View.VISIBLE
                videoSettingScheduleLayout!!.visibility = View.GONE
                videoAddressInputLayout!!.visibility = View.GONE
                videoSettingScrollView!!.visibility = View.VISIBLE
                timeBlockLayout!!.visibility = View.GONE
                updateVideoSettingService
            }
            11 -> {
                apptStatLayout!!.visibility = View.GONE
                settingServiceSetupLayout!!.visibility = View.GONE
                settingInstantVideoLayout!!.visibility = View.GONE
                settingChaLayout!!.visibility = View.GONE
                settingVideoLayout!!.visibility = View.VISIBLE
                auto_no_show_video!!.visibility = View.GONE
                videoSettingScheduleLayout!!.visibility = View.GONE
                videoAddressInputLayout!!.visibility = View.VISIBLE
                videoSettingScrollView!!.visibility = View.VISIBLE
                timeBlockLayout!!.visibility = View.GONE
                updateClinicsSettingService
            }
            12 -> {
                apptStatLayout!!.visibility = View.GONE
                settingServiceSetupLayout!!.visibility = View.GONE
                settingInstantVideoLayout!!.visibility = View.GONE
                settingChaLayout!!.visibility = View.GONE
                settingVideoLayout!!.visibility = View.VISIBLE
                videoSettingScheduleLayout!!.visibility = View.GONE
                videoAddressInputLayout!!.visibility = View.GONE
                videoSettingScrollView!!.visibility = View.GONE
                timeBlockLayout!!.visibility = View.VISIBLE
                updateClinicsSettingService
            }
        }
    }

    private fun hideKeyboard() {
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0)
    }

//    fun Context.hideKeyboard() {
//        val inputMethodManager =
//            getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
//        inputMethodManager.hideSoftInputFromWindow(windowToken, 0)
//    }

//    fun View.hideKeyboard() {
//        val inputManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
//        inputManager.hideSoftInputFromWindow(windowToken, 0)
//    }

//    private fun Context.hideKeyboard() {
//        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
//        imm.hideSoftInputFromWindow(windowToken, 0)
//    }

    private fun createHyperLinkTextDialog() {
        val dialog = Dialog(this@SettingsFormActivity)
        dialog.setCancelable(true)
        dialog.setContentView(R.layout.no_show_hyper_link_text)
        val no_show_hyper_text_view = dialog.findViewById<View>(R.id.hyper_text_view) as TextView
        val hyper_text_close = dialog.findViewById<View>(R.id.hyper_text_close) as ImageView
        hyper_text_close.setOnClickListener { view: View? -> dialog.dismiss() }
        val help_text =
            "<i><u>Tip: If your video consults often start late, keep automatic no-show turned off.</u></i><br /><br />Automatic No-Show works with WhiteCoats built-in video to help set a <i><u>time limit to join the video consult</i></u> for you and the patient.<br /><br />Failure to join the video consult within the time limit will mean that the appointment will get auto-cancelled and refunds (if any) will get triggered as per your configured policy on doctor no-show and patient no-show.<br /><br />e.g. if a video consult is scheduled for 2 pm via WhiteCoats built-in video and the automatic no-show limit is set to 15 minutes, the appointment will get auto-cancelled if <i><b>both</b></i> parties do not join by 2:15 pm. Depending on who didn't join, the appointment will get marked as either a doctor no-show or a patient no-show. The refund (if any) would be triggered as per your preferred refund policy in such cases.<br /><br />On the other hand, <b>if automatic no-show is turned off, no such time limit is imposed to join the video consult.</b> However, note that you must then manually mark the consultation as consulted or cancelled (as the case maybe). If you don't manually update the appointment status by end of the next day after appointment, the system will mark it as consulted."
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            no_show_hyper_text_view.text = Html.fromHtml(help_text, Html.FROM_HTML_MODE_COMPACT)
        } else {
            no_show_hyper_text_view.text = Html.fromHtml(help_text)
        }
        dialog.show()
        val window = dialog.window
        window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    }

    fun onRadioButtonClicked(v: View) {

        // Is the current Radio Button checked?
        val checked = (v as RadioButton).isChecked
        when (v.getId()) {
            R.id.personalMale -> {
                if (checked) rbMale!!.setBackgroundColor(
                    ContextCompat.getColor(
                        applicationContext,
                        R.color.colorPrimary
                    )
                )
                rbMale!!.setTextColor(
                    ContextCompat.getColor(
                        applicationContext,
                        R.color.colorWhite
                    )
                )
                rbFemale!!.background =
                    ContextCompat.getDrawable(this, R.drawable.drawable_rectangle_shape)
                rbFemale!!.setTextColor(
                    ContextCompat.getColor(
                        applicationContext,
                        R.color.colorBlack
                    )
                )
                genderValue = 1
            }
            R.id.personalFemale -> {
                if (checked) rbFemale!!.setBackgroundColor(resources.getColor(R.color.colorPrimary))
                rbFemale!!.setTextColor(
                    ContextCompat.getColor(
                        applicationContext,
                        R.color.colorWhite
                    )
                )
                rbMale!!.background =
                    ContextCompat.getDrawable(this, R.drawable.drawable_rectangle_shape)
                rbMale!!.setTextColor(
                    ContextCompat.getColor(
                        applicationContext,
                        R.color.colorBlack
                    )
                )
                genderValue = 2
            }
        }
    }

    val bankDetailsMerchant: Unit
        @SuppressLint("SetTextI18n")
        get() {
            showCustomProgressAlertDialog(
                resources.getString(R.string.fetching),
                resources.getString(R.string.wait_while_we_fetching)
            )

            val url = ApiUrls.getfinancialMerchantDetails

            commonViewModel.commonViewModelCall(url, JSONObject(), Request.Method.GET).observe(
                this@SettingsFormActivity
            ) { result ->
                try {
                    //Process os success response
                    val responseObj = JSONObject(result)
                    dialog.dismiss()
                    if (responseObj.getInt("status_code") == 200) {
                        val response = responseObj.optJSONObject("response")
                        val jsonObject = response.getJSONObject("response")
                        val item = jsonObject["merchantDetails"]
                        Log.d("itemBank", item.toString())
                        if (item is JSONArray) {
                            // it's an array
                            if (jsonObject.getInt("is_show_create_merchant_button") == 0) {
                                errorMessageTextLayout!!.visibility = View.VISIBLE
                                merchantDetailsInfoLayout!!.visibility = View.GONE
                                createMerchantDetailsLayout!!.visibility = View.GONE
                                errorMessageText!!.text = "No data found"
                            } else if (jsonObject.getInt("is_show_create_merchant_button") == 1) {
                                errorMessageTextLayout!!.visibility = View.GONE
                                merchantDetailsInfoLayout!!.visibility = View.GONE
                                createMerchantDetailsLayout!!.visibility = View.VISIBLE
                            }
                            //                                }
                            Log.d("merchantDetailsArray", item.toString())
                        } else {
                            // if you know it's either an array or an object, then it's an object
                            val merchantDetailsObject = item as JSONObject
                            // do objecty stuff with urlObject
                            Log.d("merchantDetailsObject", merchantDetailsObject.toString())
                            if (merchantDetailsObject != null) {
                                errorMessageTextLayout!!.visibility = View.GONE
                                merchantDetailsInfoLayout!!.visibility = View.VISIBLE
                                createMerchantDetailsLayout!!.visibility = View.GONE
                                val name = merchantDetailsObject.getString("name")
                                val phone = merchantDetailsObject.getString("mobile")
                                val email = merchantDetailsObject.getString("email")
                                val status = merchantDetailsObject.getString("status")
                                nameValue!!.text = name
                                phoneValue!!.text = phone
                                emailValue!!.text = email
                                statusValue!!.text = status
                            }
                        }

                    } else {
                        dialog.dismiss()
                        errorHandler(this@SettingsFormActivity, result)
                    }
                } catch (e: Exception) {
                    dialog.dismiss()
                    e.printStackTrace()
                }
            }
        }

    fun upDateFinanceDetails() {
        showCustomProgressAlertDialog(
            resources.getString(R.string.fetching),
            resources.getString(R.string.wait_while_we_fetching)
        )

        val url = ApiUrls.saveFinacialDetails

        val params = JSONObject()
        try {
            if (settingFinanceUpdateButton!!.text.toString().equals("Add", ignoreCase = true)) {
                params.put("acc_holder_name", settingProfAccName!!.text.toString())
                params.put("bank_name", settingProfBankName!!.text.toString())
                params.put("branch_name", settingProfBranchName!!.text.toString())
                params.put("account_number", settingProfAccNo!!.text.toString())
                params.put("ifsc_code", settingProfIFSC!!.text.toString())
                params.put("user_id", ApiUrls.doctorId)
                params.put("full_name", SettingsActivity.settingDocName!!.text.toString())
                params.put("phone", SettingsActivity.settingDocPhone!!.text.toString())
                params.put("email", SettingsActivity.settingDocEmail!!.text.toString())
            } else {
                params.put("id", rootObjGetBankDetails!!.getString("id"))
                params.put("user_id", ApiUrls.doctorId)
                params.put("acc_holder_name", settingProfAccName!!.text.toString())
                params.put("bank_name", settingProfBankName!!.text.toString())
                params.put("branch_name", settingProfBranchName!!.text.toString())
                params.put("account_number", settingProfAccNo!!.text.toString())
                params.put("ifsc_code", settingProfIFSC!!.text.toString())
                params.put("cancelled_cheque", rootObjGetBankDetails.getString("cancelled_cheque"))
                params.put("bene_id", rootObjGetBankDetails.getString("bene_id"))
                params.put("status", rootObjGetBankDetails.getString("status"))
                params.put("created_at", rootObjGetBankDetails.getString("created_at"))
                params.put("updated_at", rootObjGetBankDetails.getString("updated_at"))
                params.put("full_name", SettingsActivity.settingDocName!!.text.toString())
                params.put("phone", SettingsActivity.settingDocPhone!!.text.toString())
                params.put("email", SettingsActivity.settingDocEmail!!.text.toString())
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        commonViewModel.commonViewModelCall(url, params, Request.Method.POST).observe(
            this@SettingsFormActivity
        ) { result ->
            try {
                //Process os success response
                val responseObj = JSONObject(result)
                dialog.dismiss()
                if (responseObj.getInt("status_code") == 200) {
                    val response = responseObj.optJSONObject("response")
                    val rootObj = response!!.optJSONObject("response")
                    if (rootObj != null) {
                        Toast.makeText(
                            applicationContext,
                            "Bank details added successfully",
                            Toast.LENGTH_LONG
                        ).show()
                        finish()
                    }

                } else {
                    dialog.dismiss()
                    errorHandler(this@SettingsFormActivity, result)
                }
            } catch (e: Exception) {
                dialog.dismiss()
                e.printStackTrace()
            }
        }
    }

    val apptStatics: Unit
        get() {
            showCustomProgressAlertDialog(
                resources.getString(R.string.fetching),
                resources.getString(R.string.wait_while_we_fetching)
            )

            val url = ApiUrls.getAppStatics

            commonViewModel.commonViewModelCall(url, JSONObject(), Request.Method.GET).observe(
                this@SettingsFormActivity
            ) { result ->
                try {
                    //Process os success response
                    val responseObj = JSONObject(result)
                    dialog.dismiss()
                    if (responseObj.getInt("status_code") == 200) {
                        val response = responseObj.optJSONObject("response")
                        rootObjGetAppStatics = response.optJSONObject("response")
                        val userArr = response.getJSONArray("response")
                        for (i in 0 until userArr.length()) {
                            val tempobj = userArr.getJSONObject(i)
                            if (tempobj.getInt("card_id") == 1) {
                                val active = tempobj.getInt("active")
                                avgPatientArrival!!.isChecked = active == 1
                            } else if (tempobj.getInt("card_id") == 2) {
                                val active = tempobj.getInt("active")
                                avgTimeInLobby!!.isChecked = active == 1
                            } else if (tempobj.getInt("card_id") == 3) {
                                val active = tempobj.getInt("active")
                                avgConsultDelay!!.isChecked = active == 1
                            } else if (tempobj.getInt("card_id") == 4) {
                                val active = tempobj.getInt("active")
                                avgConsultTime!!.isChecked = active == 1
                            }
                        }

                    } else {
                        dialog.dismiss()
                        errorHandler(this@SettingsFormActivity, result)
                    }
                } catch (e: Exception) {
                    dialog.dismiss()
                    e.printStackTrace()
                }
            }
        }

    fun upDateApptStatics(cardId: Int, active: Boolean) {
        val url = ApiUrls.saveAppStatics
        try {
            jsonValue = JSONObject()
            jsonValue!!.put("card_id", cardId)
            jsonValue!!.put("active", active)
        } catch (e: Exception) {
        }

        jsonValue?.let {
            commonViewModel.commonViewModelCall(url, it, Request.Method.POST).observe(
                this@SettingsFormActivity
            ) { result ->
                try {
                    //Process os success response
                    val responseObj = JSONObject(result)
                    if (responseObj.getInt("status_code") == 200) {
                        val response = responseObj.optJSONObject("response")
                    } else {
                        errorHandler(this@SettingsFormActivity, result)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }

    }

    val settingServiceSetupDetails: Unit
        @SuppressLint("SetTextI18n")
        get() {
            showCustomProgressAlertDialog(
                resources.getString(R.string.fetching),
                resources.getString(R.string.wait_while_we_fetching)
            )
            val url = ApiUrls.getSetting

            commonViewModel.commonViewModelCall(url, JSONObject(), Request.Method.GET).observe(
                this@SettingsFormActivity
            ) { result ->
                try {
                    //Process os success response
                    val responseObj = JSONObject(result)
                    dialog.dismiss()
                    if (responseObj.getInt("status_code") == 200) {
                        val response = responseObj.optJSONObject("response")
                        rootObjSettingService = response!!.optJSONObject("response")
                        val docServiceArr = rootObjSettingService!!.getJSONArray("docSettings")
                        if (docServiceArr.length() == 0) {

                            //setup button
                            settingVideoButton!!.visibility = View.VISIBLE
                            settingInstantVideoButton!!.visibility = View.VISIBLE
                            settingChatButton!!.visibility = View.VISIBLE
                            settingClinicOneButton!!.visibility = View.VISIBLE

                            //Active status image and text
                            settingVideoCheckIcon!!.visibility = View.GONE
                            settingInstantVideoCheckIcon!!.visibility = View.GONE
                            settingChatCheckIcon!!.visibility = View.GONE
                            settingClinicCheckIcon!!.visibility = View.GONE
                            settingVideoActiveStatus!!.visibility = View.GONE
                            settingInstantVideoActiveStatus!!.visibility = View.GONE
                            settingChatActiveStatus!!.visibility = View.GONE
                            settingClinicActiveStatus!!.visibility = View.GONE
                            if (ApiUrls.isDoctorOnly == 1) {
                                settingChatButton!!.visibility = View.GONE
                                settingChatCheckIcon!!.visibility = View.GONE
                                settingChatActiveStatus!!.visibility = View.VISIBLE
                                settingChatActiveStatus!!.text = "Not Eligible"
                                settingChatActiveStatus!!.setTextColor(
                                    ContextCompat.getColor(
                                        this@SettingsFormActivity,
                                        R.color.colorBlack
                                    )
                                )
                            }
                        } else {
                            for (i in 0 until docServiceArr.length()) {
                                val serviceObj = docServiceArr.getJSONObject(i)

                                //JSONObject serviceObjectName = serviceObj.getJSONObject("service");
                                val serviceName = serviceObj["name"] as String
                                if (serviceName.equals("Chat", ignoreCase = true)) {
                                    //setup button
                                    settingChatButton!!.visibility = View.GONE
                                    settingChatCheckIcon!!.visibility = View.VISIBLE
                                    settingChatActiveStatus!!.visibility = View.VISIBLE
                                } else if (serviceName.equals("Video", ignoreCase = true)) {
                                    //setup button
                                    settingVideoButton!!.visibility = View.GONE
                                    settingVideoCheckIcon!!.visibility = View.VISIBLE
                                    settingVideoActiveStatus!!.visibility = View.VISIBLE
                                } else if (serviceName.equals(
                                        "Instant Video",
                                        ignoreCase = true
                                    )
                                ) {
                                    //setup button
                                    settingInstantVideoButton!!.visibility = View.GONE
                                    settingInstantVideoCheckIcon!!.visibility = View.VISIBLE
                                    settingInstantVideoActiveStatus!!.visibility = View.VISIBLE
                                } else if (serviceName.equals("Clinic", ignoreCase = true)) {
                                    //setup button
                                    if (serviceIdClinicCount == 3) {
                                        settingClinicOneButton!!.visibility = View.GONE
                                        activeTwoLayout!!.visibility = View.GONE
                                        settingClinicCheckIcon!!.visibility = View.VISIBLE
                                        settingClinicActiveStatus!!.visibility = View.VISIBLE
                                    } else if (serviceIdClinicCount == 1) {
                                        settingClinicOneButton!!.visibility = View.VISIBLE
                                        activeTwoLayout!!.visibility = View.VISIBLE
                                        settingClinicCheckIcon!!.visibility = View.GONE
                                        settingClinicActiveStatus!!.visibility = View.GONE
                                    } else if (serviceIdClinicCount == 2) {
                                        settingClinicOneButton!!.visibility = View.VISIBLE
                                        activeTwoLayout!!.visibility = View.VISIBLE
                                        settingClinicCheckIcon!!.visibility = View.GONE
                                        settingClinicActiveStatus!!.visibility = View.GONE
                                    }
                                    if (myList != null) {
                                        Log.d(
                                            "serviceListSize",
                                            "serviceListSize" + myList!!.size
                                        )
                                        if (myList!!.size == 1) {
                                            val clinic1 = myList!![0]
                                            if (clinic1 == 0) {
                                                settingClinicTextMsg!!.text =
                                                    "(Clinic 1 service has been disabled in settings)"
                                            }
                                        }
                                        if (myList!!.size == 2) {
                                            val clinic1 = myList!![0]
                                            val clinic2 = myList!![1]
                                            if (clinic1 == 0 && clinic2 == 0) {
                                                settingClinicTextMsg!!.text =
                                                    "(Clinic 1 and Clinic 2 service has been disabled in settings)"
                                            }
                                            if (clinic1 == 1 && clinic2 == 0) {
                                                settingClinicTextMsg!!.text =
                                                    "(Clinic 2 service has been disabled in settings)"
                                            }
                                            if (clinic1 == 0 && clinic2 == 1) {
                                                settingClinicTextMsg!!.text =
                                                    "(Clinic 1 service has been disabled in settings)"
                                            }
                                        }
                                        if (myList!!.size == 3) {
                                            val clinic1 = myList!![0]
                                            val clinic2 = myList!![1]
                                            val clinic3 = myList!![2]
                                            if (clinic1 == 0 && clinic2 == 0 && clinic3 == 0) {
                                                settingClinicTextMsg!!.text =
                                                    "(Clinic service has been disabled in settings)"
                                            }
                                            if (clinic1 == 1 && clinic2 == 0 && clinic3 == 0) {
                                                settingClinicTextMsg!!.text =
                                                    "(Clinic 2 and Clinic 3 service has been disabled in settings)"
                                            }
                                            if (clinic1 == 0 && clinic2 == 1 && clinic3 == 0) {
                                                settingClinicTextMsg!!.text =
                                                    "(Clinic 1 and Clinic 3 service has been disabled in settings)"
                                            }
                                            if (clinic1 == 0 && clinic2 == 0 && clinic3 == 1) {
                                                settingClinicTextMsg!!.text =
                                                    "(Clinic 1 and Clinic 2 service has been disabled in settings)"
                                            }
                                            if (clinic1 == 1 && clinic2 == 0 && clinic3 == 1) {
                                                settingClinicTextMsg!!.text =
                                                    "(Clinic 2 service has been disabled in settings)"
                                            }
                                            if (clinic1 == 1 && clinic2 == 1 && clinic3 == 0) {
                                                settingClinicTextMsg!!.text =
                                                    "(Clinic 3 service has been disabled in settings)"
                                            }
                                            if (clinic1 == 0 && clinic2 == 1 && clinic3 == 1) {
                                                settingClinicTextMsg!!.text =
                                                    "(Clinic 1 service has been disabled in settings)"
                                            }
                                            if (clinic1 == 1 && clinic2 == 1 && clinic3 == 1) {
                                                settingClinicTextMsg!!.visibility = View.GONE
                                            }
                                        }
                                    }
                                }
                            }
                            if (ApiUrls.isDoctorOnly == 1) {
                                settingChatButton!!.visibility = View.GONE
                                settingChatCheckIcon!!.visibility = View.GONE
                                settingChatActiveStatus!!.visibility = View.VISIBLE
                                settingChatActiveStatus!!.text = "Not Eligible"
                                settingChatActiveStatus!!.setTextColor(
                                    ContextCompat.getColor(
                                        this@SettingsFormActivity,
                                        R.color.colorBlack
                                    )
                                )
                            }
                        }

                    } else {
                        dialog.dismiss()
                        errorHandler(this@SettingsFormActivity, result)
                    }
                } catch (e: Exception) {
                    dialog.dismiss()
                    e.printStackTrace()
                }
            }

        }

    private fun setupVideoService(permissionId: Int, serviceName: String?) {
        settingVideoSchedModelList!!.clear()

        var temp = SettingVideoScheduleModel()
        temp.dayName = "EDIT"
        temp.slotOneTime = "Slot 1"
        temp.slotTwoTime = "Slot 2"
        settingVideoSchedModelList!!.add(temp)
        for (k in 0..6) {
            var day = ""
            if (k == 0) {
                day = "Monday"
            } else if (k == 1) {
                day = "Tuesday"
            }
            if (k == 2) {
                day = "Wednesday"
            }
            if (k == 3) {
                day = "Thursday"
            }
            if (k == 4) {
                day = "Friday"
            }
            if (k == 5) {
                day = "Saturday"
            }
            if (k == 6) {
                day = "Sunday"
            }
            val startTime = "00:00:00"
            val endTime = "00:00:00"
            val startTime2 = "00:00:00"
            val endTime2 = "00:00:00"
            temp = SettingVideoScheduleModel()
            temp.dayName = day
            temp.idOne = 0
            temp.slotIdOne = 0
            temp.scheduleIdOne = 0
            temp.slotFlagIdOne = 0
            temp.idTwo = 0
            temp.slotIdTwo = 0
            temp.scheduleIdTwo = 0
            temp.slotFlagIdTwo = 0
            temp.slotOneTime = "$startTime-$endTime"
            temp.slotTwoTime = "$startTime2-$endTime2"
            settingVideoSchedModelList!!.add(temp)
        }
    }

    val updateVideoSettingService: Unit
        get() {
            showCustomProgressAlertDialog(
                resources.getString(R.string.updating),
                resources.getString(R.string.wait_while_we_updating)
            )

            val url = ApiUrls.getIndividualServiceDetails + "?product_id=" + selectedserviceProd_ID
            viewModel!!.getSelectedServicesDetails(url)
            viewModel!!.serviceDetails.observe(
                this,
                object : Observer<UtilsResource<ServiceDetailsModelClass?>?> {
                    @SuppressLint("NotifyDataSetChanged")
                    override fun onChanged(value: UtilsResource<ServiceDetailsModelClass?>?) {
                        dialog.dismiss()
                        if (value?.status === UtilsResource.Status.SUCCESS) {
                            if (value.data != null) {
                                if (value.data.response != null) {
                                    val response = value.data.response
                                    val activeStatus = response!!.prod_service.active
                                    videoPriceInput!!.setText(response.price.toString())
                                    val shortName = response.desc
                                    settingVideoDescriptionText!!.setText(response.desc)
                                    settingVideoShortText!!.setText(response.prod_service.alias)
                                    if (response.automatic_no_show_control == 0) {
                                        auto_no_show_spinner_video!!.setSelection(0)
                                        no_show_limit_video_et!!.visibility = View.VISIBLE
                                        no_show_limt_video!!.visibility = View.VISIBLE
                                        no_show_limit_video_et!!.setText(response.automatic_no_show_time_limit.toString() + "")
                                        no_show_limit_video_value =
                                            response.automatic_no_show_time_limit
                                    } else {
                                        auto_no_show_spinner_video!!.setSelection(1)
                                        no_show_limit_video_et!!.visibility = View.GONE
                                        no_show_limt_video!!.visibility = View.GONE
                                    }
                                    for (j in spinnerAppointmentDuration.indices) {
                                        if (spinnerAppointmentDuration[j].equals(
                                                response.prod_service.consultation_time.toString(),
                                                ignoreCase = true
                                            )
                                        ) {
                                            appointmentDurationSpinner!!.setSelection(j)
                                        }
                                    }
                                    val seconds = response.prod_service.advance_notice_in_mins
                                    if (seconds >= 1440) {
                                        videoDurationSpinner!!.setSelection(1)
                                        advanceBookingText!!.setText((seconds / 1440).toString())
                                    } else if (seconds >= 60 && seconds < 1440) {
                                        videoDurationSpinner!!.setSelection(2)
                                        advanceBookingText!!.setText((seconds / 60).toString())
                                    } else {
                                        videoDurationSpinner!!.setSelection(3)
                                        advanceBookingText!!.setText(seconds.toString())
                                    }
                                    val serviceSlotsList = response.slots
                                    if (serviceSlotsList != null && serviceSlotsList.isNotEmpty()) {
                                        val serviceSlots = serviceSlotsList[0]
                                        var temp = SettingVideoScheduleModel()
                                        temp.dayName = "EDIT"
                                        temp.slotOneTime = "Slot 1"
                                        temp.slotTwoTime = "Slot 2"
                                        settingVideoSchedModelList!!.add(temp)
                                        for (k in 0..0) {
                                            val (_, _, id, schedule_id) = serviceSlots[k]
                                            slotID = id
                                            scheduleID = schedule_id
                                        }
                                        for (k in serviceSlots.indices) {
                                            val (day1, end_time, id, schedule_id, slot_flag, slot_id, start_time) = serviceSlots[k]
                                            val serviceSlots2 = serviceSlotsList[1]
                                            val (_, end_time1, id1, schedule_id1, slot_flag1, slot_id1, start_time1) = serviceSlots2[k]
                                            temp = SettingVideoScheduleModel()
                                            temp.dayName = day1
                                            temp.slotOneTime = "$start_time-$end_time"
                                            temp.idOne = id
                                            temp.slotIdOne = slot_id
                                            temp.scheduleIdOne = schedule_id
                                            temp.slotFlagIdOne = slot_flag
                                            temp.idTwo = id1
                                            temp.slotIdTwo = slot_id1
                                            temp.scheduleIdTwo = schedule_id1
                                            temp.slotFlagIdTwo = slot_flag1
                                            temp.slotTwoTime = "$start_time1-$end_time1"
                                            settingVideoSchedModelList!!.add(temp)
                                            settingVideoSchedModelListTemp!!.add(temp)
                                        }
                                        if (settingVideoSchedModelList!!.size > 1) {
                                            settingVideoSchedListAdapter!!.notifyDataSetChanged()
                                        }
                                        Log.d(
                                            "ScheduleSize ",
                                            "ScheduleSize " + settingVideoSchedModelList!!.size
                                        )
                                        if (settingVideoSchedModelList!!.size >= 8) {
                                            settingVideoSchedModelListDup = ArrayList()
                                            (settingVideoSchedModelListDup as ArrayList<SettingVideoScheduleModel>).addAll(
                                                settingVideoSchedModelList!!
                                            )
                                        }
                                        if (activeStatus == 1) {
                                            settingVideoEnableDisableSwitch!!.isChecked = true
                                            settingVideoShortText!!.isEnabled = true
                                            settingVideoDescriptionText!!.isEnabled = true
                                            videoPriceInput!!.isEnabled = true
                                            videoAddressText!!.isEnabled = true
                                            advanceBookingText!!.isEnabled = true
                                            appointmentDurationSpinner!!.isEnabled = true
                                            videoDurationSpinner!!.isEnabled = true
                                            videoScheduleLayout!!.isEnabled = true
                                            videoScheduleLayout!!.setBackgroundColor(
                                                ContextCompat.getColor(
                                                    applicationContext, R.color.colorAccent
                                                )
                                            )
                                        } else {
                                            settingVideoEnableDisableSwitch!!.isChecked = false
                                            settingVideoShortText!!.isEnabled = false
                                            settingVideoDescriptionText!!.isEnabled = false
                                            videoPriceInput!!.isEnabled = false
                                            videoAddressText!!.isEnabled = false
                                            advanceBookingText!!.isEnabled = false
                                            appointmentDurationSpinner!!.isEnabled = false
                                            videoDurationSpinner!!.isEnabled = false
                                            videoScheduleLayout!!.isEnabled = false
                                            videoScheduleLayout!!.setBackgroundColor(
                                                ContextCompat.getColor(
                                                    this@SettingsFormActivity,
                                                    R.color.colorGrey1
                                                )
                                            )
                                        }
                                    }
                                }
                            }
                        } else if (value?.status === UtilsResource.Status.ERROR) {
                            dialog.dismiss()
                            if (value.message != null) {
                                errorHandler(this@SettingsFormActivity, value.message)
                            }
                        }
                    }
                })
        }
    val updateClinicsSettingService: Unit
        get() {
            showCustomProgressAlertDialog(
                resources.getString(R.string.fetching),
                resources.getString(R.string.wait_while_we_fetching)
            )
            val url = ApiUrls.getIndividualServiceDetails + "?product_id=" + selectedserviceProd_ID
            viewModel!!.getSelectedServicesDetails(url)
            viewModel!!.serviceDetails.observe(
                this,
                object : Observer<UtilsResource<ServiceDetailsModelClass?>?> {
                    override fun onChanged(value: UtilsResource<ServiceDetailsModelClass?>?) {
                        dialog.dismiss()
                        if (value?.status === UtilsResource.Status.SUCCESS) {
                            if (value.data != null) {
                                if (value.data.response != null) {
                                    val response = value.data.response
                                    val activeStatus = response!!.prod_service.active
                                    if (activeStatus == 1) {
                                        settingVideoEnableDisableSwitch!!.isChecked = true
                                        videoShortNameTxtInput!!.visibility = View.VISIBLE
                                        videoDescriptionTxtInput!!.visibility = View.VISIBLE
                                        videoPriceTxtInput!!.visibility = View.VISIBLE
                                        appointmentDurationLayout!!.visibility = View.VISIBLE
                                        videoAdvanceBookingLayout!!.visibility = View.VISIBLE
                                        videoAdvanceBookingFromLayout!!.visibility = View.VISIBLE
                                        settingServiceRecycleView!!.visibility = View.VISIBLE
                                        videoScheduleLayout!!.visibility = View.VISIBLE
                                        if (intent.getStringExtra("Title")
                                                .equals("Clinic 1", ignoreCase = true)
                                        ) {
                                            drServiceIdClinicOne = response.dr_service_id
                                        } else if (intent.getStringExtra("Title")
                                                .equals("Clinic 2", ignoreCase = true)
                                        ) {
                                            drServiceIdClinicTwo = response.dr_service_id
                                        } else if (intent.getStringExtra("Title")
                                                .equals("Clinic 3", ignoreCase = true)
                                        ) {
                                            drServiceIdClinicThree = response.dr_service_id
                                        }
                                        videoPriceInput!!.setText(response.price.toString())
                                        settingVideoDescriptionText!!.setText(response.desc)
                                        settingVideoShortText!!.setText(response.prod_service.alias)
                                        videoAddressText!!.setText(response.prod_service.address)
                                        for (j in spinnerAppointmentDuration.indices) {
                                            if (spinnerAppointmentDuration[j].equals(
                                                    response.prod_service.consultation_time.toString(),
                                                    ignoreCase = true
                                                )
                                            ) {
                                                appointmentDurationSpinner!!.setSelection(j)
                                            }
                                        }
                                        val seconds = response.prod_service.advance_notice_in_mins
                                        if (seconds >= 1440) {
                                            videoDurationSpinner!!.setSelection(1)
                                            advanceBookingText!!.setText((seconds / 1440).toString())
                                        } else if (seconds >= 60 && seconds < 1440) {
                                            videoDurationSpinner!!.setSelection(2)
                                            advanceBookingText!!.setText((seconds / 60).toString())
                                        } else {
                                            videoDurationSpinner!!.setSelection(3)
                                            advanceBookingText!!.setText(seconds.toString())
                                        }
                                        val serviceSlotsList = response.slots
                                        if (serviceSlotsList != null && serviceSlotsList.size > 0) {
                                            val serviceSlots = serviceSlotsList[0]
                                            var temp = SettingVideoScheduleModel()
                                            temp.dayName = "EDIT"
                                            temp.slotOneTime = "Slot 1"
                                            temp.slotTwoTime = "Slot 2"
                                            settingVideoSchedModelList!!.add(temp)
                                            for (k in 0..0) {
                                                val (_, _, id, schedule_id) = serviceSlots[k]
                                                slotID = id
                                                scheduleID = schedule_id
                                            }
                                            for (k in serviceSlots.indices) {
                                                val (day1, end_time, id, schedule_id, slot_flag, slot_id, start_time) = serviceSlots[k]
                                                val serviceSlots2 = serviceSlotsList[1]
                                                val (_, end_time1, id1, schedule_id1, slot_flag1, slot_id1, start_time1) = serviceSlots2[k]
                                                temp = SettingVideoScheduleModel()
                                                temp.dayName = day1
                                                temp.slotOneTime = "$start_time-$end_time"
                                                temp.idOne = id
                                                temp.slotIdOne = slot_id
                                                temp.scheduleIdOne = schedule_id
                                                temp.slotFlagIdOne = slot_flag
                                                temp.idTwo = id1
                                                temp.slotIdTwo = slot_id1
                                                temp.scheduleIdTwo = schedule_id1
                                                temp.slotFlagIdTwo = slot_flag1
                                                temp.slotTwoTime = "$start_time1-$end_time1"
                                                settingVideoSchedModelList!!.add(temp)
                                                settingVideoSchedModelListTemp!!.add(temp)
                                            }
                                            settingVideoSchedListAdapter!!.notifyDataSetChanged()
                                        }
                                    } else {
                                        settingVideoEnableDisableSwitch!!.isChecked = false
                                        videoShortNameTxtInput!!.visibility = View.GONE
                                        videoDescriptionTxtInput!!.visibility = View.GONE
                                        videoPriceTxtInput!!.visibility = View.GONE
                                        appointmentDurationLayout!!.visibility = View.GONE
                                        videoAdvanceBookingLayout!!.visibility = View.GONE
                                        videoAdvanceBookingFromLayout!!.visibility = View.GONE
                                        settingServiceRecycleView!!.visibility = View.GONE
                                        videoScheduleLayout!!.visibility = View.GONE
                                    }
                                }
                            }
                        } else if (value?.status === UtilsResource.Status.ERROR) {
                            dialog.dismiss()
                            if (value.message != null) {
                                errorHandler(this@SettingsFormActivity, value.message)
                            }
                        }
                    }
                })
        }
    val updateChatAndInstantVideoSettingServices: Unit
        get() {
            showCustomProgressAlertDialog(
                resources.getString(R.string.fetching),
                resources.getString(R.string.wait_while_we_fetching)
            )
            val url = ApiUrls.getIndividualServiceDetails + "?product_id=" + selectedserviceProd_ID
            viewModel!!.getSelectedServicesDetails(url)
            viewModel!!.serviceDetails.observe(
                this,
                object : Observer<UtilsResource<ServiceDetailsModelClass?>?> {
                    override fun onChanged(value: UtilsResource<ServiceDetailsModelClass?>?) {
                        dialog.dismiss()
                        if (value?.status === UtilsResource.Status.SUCCESS) {
                            if (value.data != null) {
                                if (value.data.response != null) {
                                    val response = value.data.response
                                    if (response!!.mode.equals("Chat", ignoreCase = true)) {
                                        val activeStatus = response.prod_service.active
                                        chatPriceText!!.setText(response.price.toString())
                                        chatDescriptionText!!.setText(response.desc)
                                        settingChatShortText!!.setText(response.prod_service.alias)
                                        var seconds = 0.0
                                        var secondsS = 0
                                        val validityDays = response.validity_in_days.toString()
                                        if (validityDays.contains(".")) {
                                            seconds = response.validity_in_days
                                            if (seconds >= 1) {
                                                chatValidityDaySpinner!!.setSelection(1)
                                                chatValidityEditText!!.setText(
                                                    seconds.toInt().toString()
                                                )
                                            } else if (seconds >= 0.0416667 && seconds < 1) {
                                                val hoursDouble =
                                                    Math.round((seconds * 24)).toDouble()
                                                chatValidityDaySpinner!!.setSelection(2)
                                                chatValidityEditText!!.setText(
                                                    hoursDouble.toInt().toString()
                                                )
                                            } else {
                                                val minutesDouble =
                                                    Math.round((seconds * 1440)).toDouble()
                                                chatValidityDaySpinner!!.setSelection(3)
                                                chatValidityEditText!!.setText(
                                                    minutesDouble.toInt().toString()
                                                )
                                            }
                                        } else {
                                            secondsS = response.validity_in_days.toInt()
                                            if (secondsS >= 1) {
                                                chatValidityDaySpinner!!.setSelection(1)
                                                chatValidityEditText!!.setText(secondsS.toString())
                                            }
                                        }
                                        if (activeStatus == 1) {
                                            settingChatEnableDisableSwitch!!.isChecked = true
                                            chatDescriptionText!!.isEnabled = true
                                            settingChatShortText!!.isEnabled = true
                                            chatPriceText!!.isEnabled = true
                                            chatValidityEditText!!.isEnabled = true
                                            chatValidityDaySpinner!!.isEnabled = true
                                            chaSettingUpdateButton!!.isEnabled = true
                                            chaSettingUpdateButton!!.setBackgroundColor(
                                                ContextCompat.getColor(
                                                    applicationContext, R.color.colorAccent
                                                )
                                            )
                                            chaSettingUpdateButton!!.setBackgroundResource(R.drawable.drawable_capsule_view)
                                        } else {
                                            settingChatEnableDisableSwitch!!.isChecked = false
                                            chatDescriptionText!!.isEnabled = false
                                            settingChatShortText!!.isEnabled = false
                                            chatPriceText!!.isEnabled = false
                                            chatValidityEditText!!.isEnabled = false
                                            chatValidityDaySpinner!!.isEnabled = false
                                            chaSettingUpdateButton!!.isEnabled = false
                                            chaSettingUpdateButton!!.setBackgroundColor(
                                                ContextCompat.getColor(
                                                    applicationContext, R.color.colorGrey1
                                                )
                                            )
                                        }
                                    } else if (response.mode.equals(
                                            "Instant Video",
                                            ignoreCase = true
                                        )
                                    ) {
                                        settingPriceText!!.setText(response.price.toString())
                                        settingDescText!!.setText(response.desc)
                                        if (response.automatic_no_show_control == 0) {
                                            auto_no_show_spinner_instant_video!!.setSelection(0)
                                            no_show_limit_instant_video_et!!.visibility =
                                                View.VISIBLE
                                            no_show_limt_instant_video!!.visibility = View.VISIBLE
                                            no_show_limit_instant_video_et!!.setText(response.automatic_no_show_time_limit.toString() + "")
                                            no_show_limit_instant_video_value =
                                                response.automatic_no_show_time_limit
                                        } else {
                                            auto_no_show_spinner_instant_video!!.setSelection(1)
                                            no_show_limit_instant_video_et!!.visibility = View.GONE
                                            no_show_limt_instant_video!!.visibility = View.GONE
                                        }
                                        if (response.instant_permission != null) {
                                            fromDaySpinner!!.setSelection(response.instant_permission.instant_days)
                                            toDaySpinner!!.setSelection(response.instant_permission.instant_day_end)
                                            if (appUtilities!!.timeFormatPreferences(
                                                    applicationContext
                                                ) == 12
                                            ) {
                                                instantStartTime =
                                                    if (response.instant_permission.instant_start_time.equals(
                                                            "00:00:00",
                                                            ignoreCase = true
                                                        )
                                                    ) {
                                                        "00:00 AM"
                                                    } else {
                                                        appUtilities!!.changeDateFormat(
                                                            "HH:mm:ss",
                                                            "hh:mm a",
                                                            response.instant_permission.instant_start_time
                                                        )
                                                    }
                                                instantEndTime =
                                                    if (response.instant_permission.instant_end_time.equals(
                                                            "00:00:00",
                                                            ignoreCase = true
                                                        )
                                                    ) {
                                                        "00:00 AM"
                                                    } else {
                                                        appUtilities!!.changeDateFormat(
                                                            "HH:mm:ss",
                                                            "hh:mm a",
                                                            response.instant_permission.instant_end_time
                                                        )
                                                    }
                                            } else {
                                                instantStartTime = appUtilities!!.changeDateFormat(
                                                    "HH:mm:ss",
                                                    "HH:mm",
                                                    response.instant_permission.instant_start_time
                                                )
                                                instantEndTime = appUtilities!!.changeDateFormat(
                                                    "HH:mm:ss",
                                                    "HH:mm",
                                                    response.instant_permission.instant_end_time
                                                )
                                            }
                                            for (i in spinnerTimeList.indices) {
                                                if (instantStartTime.toString().equals(
                                                        spinnerTimeList[i],
                                                        ignoreCase = true
                                                    )
                                                ) {
                                                    fromTimeSpinner!!.setSelection(i)
                                                }
                                                if (instantEndTime.toString().equals(
                                                        spinnerTimeList[i],
                                                        ignoreCase = true
                                                    )
                                                ) {
                                                    toTimeSpinner!!.setSelection(i)
                                                }
                                            }
                                            if (response.instant_permission.instant_permission == 1) {
                                                settingInstantVideoSwitch!!.isChecked = true
                                                settingDescText!!.isEnabled = true
                                                settingPriceText!!.isEnabled = true
                                                fromDaySpinner!!.isEnabled = true
                                                fromTimeSpinner!!.isEnabled = true
                                                toDaySpinner!!.isEnabled = true
                                                toTimeSpinner!!.isEnabled = true
                                                settingInstantVideo!!.isEnabled = true
                                                settingInstantVideo!!.setBackgroundColor(
                                                    ContextCompat.getColor(
                                                        applicationContext, R.color.colorAccent
                                                    )
                                                )
                                                settingInstantVideo!!.setBackgroundResource(R.drawable.drawable_capsule_view)
                                            } else {
                                                settingInstantVideoSwitch!!.isChecked = false
                                                settingDescText!!.isEnabled = false
                                                settingPriceText!!.isEnabled = false
                                                fromDaySpinner!!.isEnabled = false
                                                fromTimeSpinner!!.isEnabled = false
                                                toDaySpinner!!.isEnabled = false
                                                toTimeSpinner!!.isEnabled = false
                                                settingInstantVideo!!.isEnabled = false
                                                settingInstantVideo!!.setBackgroundColor(
                                                    ContextCompat.getColor(
                                                        applicationContext, R.color.colorGrey1
                                                    )
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        } else if (value?.status === UtilsResource.Status.ERROR) {
                            dialog.dismiss()
                            if (value.message != null) {
                                errorHandler(this@SettingsFormActivity, value.message)
                            }
                        }
                    }
                })
        }

    fun getSettingServiceVideoAndClinicSchedule(permissionId: Int, serviceName: String?) {


        showCustomProgressAlertDialog(
            resources.getString(R.string.fetching),
            resources.getString(R.string.wait_while_we_fetching)
        )
        val url = ApiUrls.updateSettingService
        jsonValue = JSONObject()
        try {
            jsonValue!!.put("permission", permissionId)
            jsonValue!!.put("service", serviceName)
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        commonViewModel.commonViewModelCall(url, jsonValue!!, Request.Method.POST).observe(
            this@SettingsFormActivity
        ) { result ->
            try {
                //Process os success response
                val responseObj = JSONObject(result)
//                dialog.dismiss()
                if (responseObj.getInt("status_code") == 200) {
                    val response = responseObj.optJSONObject("response")

                    rootObj = response.optJSONObject("response")
                    if (rootObj?.getString("service").equals("Video", ignoreCase = true)) {
                        val serviceSlot = rootObj?.getJSONArray("slots")
                        settingSchedModelList.clear()
                        var temp2 = SettingScheduleModel()
                        for (i in 0 until serviceSlot!!.length() - 1) {
                            var serviceObj = serviceSlot.getJSONArray(i)
                            for (k in 0 until serviceObj!!.length()) {
                                val serviceObj2 = serviceObj.getJSONObject(k)
                                val day = serviceObj2["day"] as String
                                val startTime = serviceObj2["start_time"] as String
                                val endTime = serviceObj2["end_time"] as String
                                val serviceObjSlot2 = serviceSlot.getJSONArray(1)
                                val serviceObj3 = serviceObjSlot2.getJSONObject(k)
                                // String day = (String) serviceObj2.get("day");
                                val startTime2 = serviceObj3["start_time"] as String
                                val endTime2 = serviceObj3["end_time"] as String
                                temp2 = SettingScheduleModel()
                                temp2.daysName = day
                                temp2.slotOneStart = startTime
                                temp2.slotOneEnd = endTime
                                temp2.slotTwoStart = startTime2
                                temp2.slotTwoEnd = endTime2
                                settingSchedModelList.add(temp2)
                            }
                        }
                        settingSchedListAdapter =
                            SettingScheduleListAdapter(mContext, settingSchedModelList)
                        settingScheduleRecycleView!!.layoutManager = LinearLayoutManager(
                            applicationContext, LinearLayoutManager.VERTICAL, false
                        )
                        settingScheduleRecycleView!!.itemAnimator = DefaultItemAnimator()
                        settingScheduleRecycleView!!.adapter = settingSchedListAdapter
                    } else if (rootObj!!.getString("service").equals("Chat", ignoreCase = true)) {
                        val serviceDetails = rootObj!!.getJSONObject("serviceDetails")
                        val docServiceArr = rootObj!!.getJSONArray("getPrice")
                        for (i in 0 until docServiceArr.length()) {
                            val serviceObj = docServiceArr.getJSONObject(i)
                            val price = serviceObj["price"] as Int
                            chatPriceText!!.setText(price.toString())
                            val description = serviceObj["desc"] as String
                            chatDescriptionText!!.setText(description)
                            settingChatShortText!!.setText(serviceDetails.getString("alias"))
                            var seconds = 0.0
                            var secondsS = 0
                            if (serviceObj["validity_in_days"].toString().contains(".")) {
                                seconds = serviceObj["validity_in_days"] as Double
                                if (seconds >= 1) {
                                    val days = secondsS.toDouble()
                                    chatValidityDaySpinner!!.setSelection(1)
                                    chatValidityEditText!!.setText(days.toInt().toString())
                                } else if (seconds >= 0.0416667 && seconds < 1) {
                                    val hoursDouble = Math.round((seconds * 24)).toDouble()
                                    chatValidityDaySpinner!!.setSelection(2)
                                    chatValidityEditText!!.setText(
                                        hoursDouble.toInt().toString()
                                    )
                                } else {
                                    val minutesDouble = Math.round((seconds * 1440)).toDouble()
                                    chatValidityDaySpinner!!.setSelection(3)
                                    chatValidityEditText!!.setText(
                                        minutesDouble.toInt().toString()
                                    )
                                }
                            } else {
                                secondsS = serviceObj["validity_in_days"] as Int
                                if (secondsS >= 1) {
                                    chatValidityDaySpinner!!.setSelection(1)
                                    chatValidityEditText!!.setText(secondsS.toString())
                                }
                            }


                        }
                        val serviceDetailsArr = rootObj!!.getJSONObject("serviceDetails")
                        val activeStatus = serviceDetailsArr.getInt("active")
                        if (activeStatus == 1) {
                            settingChatEnableDisableSwitch!!.isChecked = true
                            chatDescriptionText!!.isEnabled = true
                            settingChatShortText!!.isEnabled = true
                            chatPriceText!!.isEnabled = true
                            chatValidityEditText!!.isEnabled = true
                            chatValidityDaySpinner!!.isEnabled = true
                            chaSettingUpdateButton!!.isEnabled = true
                            chaSettingUpdateButton!!.setBackgroundColor(
                                ContextCompat.getColor(
                                    this@SettingsFormActivity,
                                    R.color.colorAccent
                                )
                            )
                            chaSettingUpdateButton!!.setBackgroundResource(R.drawable.drawable_capsule_view)
                        } else {
                            settingChatEnableDisableSwitch!!.isChecked = false
                            chatDescriptionText!!.isEnabled = false
                            settingChatShortText!!.isEnabled = false
                            chatPriceText!!.isEnabled = false
                            chatValidityEditText!!.isEnabled = false
                            chatValidityDaySpinner!!.isEnabled = false
                            chaSettingUpdateButton!!.isEnabled = false
                            chaSettingUpdateButton!!.setBackgroundColor(
                                ContextCompat.getColor(
                                    this@SettingsFormActivity,
                                    R.color.colorGrey1
                                )
                            )
                        }
                    } else if (rootObj!!.getString("service")
                            .equals("Instant Video", ignoreCase = true)
                    ) {
                        val docServiceArr = rootObj!!.getJSONArray("getPrice")
                        for (i in 0 until docServiceArr.length()) {
                            val serviceObj = docServiceArr.getJSONObject(i)
                            //JSONObject serviceObjectName = serviceObj.getJSONObject("service");
                            val price = serviceObj["price"] as Int
                            settingPriceText!!.setText(price.toString())
                            val description = serviceObj["desc"] as String
                            settingDescText!!.setText(description)
                            if (serviceObj.optInt("automatic_no_show_control") == 0) {
                                auto_no_show_spinner_instant_video!!.setSelection(0)
                                no_show_limit_instant_video_et!!.visibility = View.VISIBLE
                                no_show_limt_instant_video!!.visibility = View.VISIBLE
                                no_show_limit_instant_video_et!!.setText(
                                    serviceObj.optInt("automatic_no_show_time_limit")
                                        .toString() + ""
                                )
                                no_show_limit_instant_video_value =
                                    serviceObj.optInt("automatic_no_show_time_limit")
                            } else {
                                auto_no_show_spinner_instant_video!!.setSelection(1)
                                no_show_limit_instant_video_et!!.visibility = View.GONE
                                no_show_limt_instant_video!!.visibility = View.GONE
                            }
                        }
                        val getScheduleObject = rootObj!!.getJSONObject("getSchedule")
                        val instantDays = getScheduleObject["instant_days"] as Int
                        val instantDayEnd = getScheduleObject["instant_day_end"] as Int
                        val instant_permission = getScheduleObject["instant_permission"] as Int
                        fromDaySpinner!!.setSelection(instantDays)
                        toDaySpinner!!.setSelection(instantDayEnd)
                        if (appUtilities!!.timeFormatPreferences(applicationContext) == 12) {
                            instantStartTime =
                                if (getScheduleObject["instant_start_time"].toString()
                                        .equals("00:00:00", ignoreCase = true)
                                ) {
                                    "00:00 AM"
                                } else {
                                    appUtilities!!.changeDateFormat(
                                        "HH:mm:ss",
                                        "hh:mm a",
                                        getScheduleObject["instant_start_time"] as String
                                    )
                                }
                            instantEndTime =
                                if (getScheduleObject["instant_end_time"].toString()
                                        .equals("00:00:00", ignoreCase = true)
                                ) {
                                    "00:00 AM"
                                } else {
                                    appUtilities!!.changeDateFormat(
                                        "HH:mm:ss",
                                        "hh:mm a",
                                        getScheduleObject["instant_end_time"] as String
                                    )
                                }
                        } else {
                            instantStartTime = appUtilities!!.changeDateFormat(
                                "HH:mm:ss",
                                "HH:mm",
                                getScheduleObject["instant_start_time"] as String
                            )
                            instantEndTime = appUtilities!!.changeDateFormat(
                                "HH:mm:ss",
                                "HH:mm",
                                getScheduleObject["instant_end_time"] as String
                            )
                        }
                        Log.d(
                            "instantStartTime:",
                            "instantStartTime:" + getScheduleObject["instant_start_time"].toString()
                        )
                        Log.d("instantEndTime:", "instantEndTime:$instantEndTime")
                        for (i in spinnerTimeList.indices) {
                            if (instantStartTime.toString()
                                    .equals(spinnerTimeList[i], ignoreCase = true)
                            ) {
                                fromTimeSpinner!!.setSelection(i)
                            }
                            if (instantEndTime.toString()
                                    .equals(spinnerTimeList[i], ignoreCase = true)
                            ) {
                                toTimeSpinner!!.setSelection(i)
                            }
                        }
                        if (instant_permission == 1) {
                            settingInstantVideoSwitch!!.isChecked = true
                            settingDescText!!.isEnabled = true
                            settingPriceText!!.isEnabled = true
                            fromDaySpinner!!.isEnabled = true
                            fromTimeSpinner!!.isEnabled = true
                            toDaySpinner!!.isEnabled = true
                            toTimeSpinner!!.isEnabled = true
                            settingInstantVideo!!.isEnabled = true
                            settingInstantVideo!!.setBackgroundColor(
                                ContextCompat.getColor(
                                    this@SettingsFormActivity,
                                    R.color.colorAccent
                                )
                            )
                            settingInstantVideo!!.setBackgroundResource(R.drawable.drawable_capsule_view)
                        } else {
                            settingInstantVideoSwitch!!.isChecked = false
                            settingDescText!!.isEnabled = false
                            settingPriceText!!.isEnabled = false
                            fromDaySpinner!!.isEnabled = false
                            fromTimeSpinner!!.isEnabled = false
                            toDaySpinner!!.isEnabled = false
                            toTimeSpinner!!.isEnabled = false
                            settingInstantVideo!!.isEnabled = false
                            settingInstantVideo!!.setBackgroundColor(
                                ContextCompat.getColor(
                                    this@SettingsFormActivity,
                                    R.color.colorGrey1
                                )
                            )
                        }
                    } else if (rootObj!!.getString("service")
                            .equals("Clinic", ignoreCase = true)
                    ) {
                        val serviceSlot = rootObj!!.getJSONArray("slots")
                        val services = rootObj!!.getJSONArray("getService")
                        settingSchedModelList.clear()
                        var temp2 = SettingScheduleModel()
                        var pos = 0
                        outerLoop@ for (i in 0 until serviceSlot.length()) {
                            val serviceObj = serviceSlot.getJSONArray(i)
                            for (k in 0 until serviceObj.length()) {
                                val slotsObj = serviceObj.getJSONObject(k)
                                if (Internal_Supersaas_ID == slotsObj.getInt("schedule_id")) {
                                    pos = i
                                    break@outerLoop
                                }
                            }
                        }
                        val serviceObj = serviceSlot.getJSONArray(pos)
                        val serviceObjSlot2 = serviceSlot.getJSONArray(pos + 1)
                        for (k in 0 until serviceObj.length()) {
                            val serviceObj2 = serviceObj.getJSONObject(k)
                            val day = serviceObj2["day"] as String
                            val startTime = serviceObj2["start_time"] as String
                            val endTime = serviceObj2["end_time"] as String
                            val serviceObj3 = serviceObjSlot2.getJSONObject(k)
                            val startTime2 = serviceObj3["start_time"] as String
                            val endTime2 = serviceObj3["end_time"] as String
                            temp2 = SettingScheduleModel()
                            temp2.daysName = day
                            temp2.slotOneStart = startTime
                            temp2.slotOneEnd = endTime
                            temp2.slotTwoStart = startTime2
                            temp2.slotTwoEnd = endTime2
                            settingSchedModelList.add(temp2)
                        }
                        settingSchedListAdapter =
                            SettingScheduleListAdapter(mContext, settingSchedModelList)
                        settingScheduleRecycleView!!.layoutManager = LinearLayoutManager(
                            applicationContext, LinearLayoutManager.VERTICAL, false
                        )
                        settingScheduleRecycleView!!.itemAnimator = DefaultItemAnimator()
                        settingScheduleRecycleView!!.adapter = settingSchedListAdapter
                    }
                    Handler().postDelayed({ dialog.dismiss() }, 500)

                } else {
                    dialog.dismiss()
                    errorHandler(this@SettingsFormActivity, result)
                }
            } catch (e: Exception) {
                dialog.dismiss()
                e.printStackTrace()
            }
        }


    }

    fun setSettingServiceVideoSchedule(permissionId: Int, serviceName: String?) {
        var temp2 = SettingScheduleModel()
        settingSchedModelList.clear()
        for (k in 0..6) {
            //JSONObject serviceObj2 = serviceObj.getJSONObject(k);
            var day = ""
            if (k == 0) {
                day = "Monday"
            } else if (k == 1) {
                day = "Tuesday"
            }
            if (k == 2) {
                day = "Wednesday"
            }
            if (k == 3) {
                day = "Thursday"
            }
            if (k == 4) {
                day = "Friday"
            }
            if (k == 5) {
                day = "Saturday"
            }
            if (k == 6) {
                day = "Sunday"
            }

            // String day = (String) serviceObj2.get("day");
            val startTime = "00:00:00"
            val endTime = "00:00:00"
            //JSONArray serviceObjSlot2 = serviceSlot.getJSONArray(1);
            // JSONObject serviceObj3 = serviceObjSlot2.getJSONObject(k);
            // String day = (String) serviceObj2.get("day");
            val startTime2 = "00:00:00"
            val endTime2 = "00:00:00"
            temp2 = SettingScheduleModel()
            temp2.daysName = day
            temp2.slotOneStart = startTime
            temp2.slotOneEnd = endTime
            temp2.slotTwoStart = startTime2
            temp2.slotTwoEnd = endTime2
            settingSchedModelList.add(temp2)
        }
        settingSchedListAdapter = SettingScheduleListAdapter(mContext, settingSchedModelList)
        settingScheduleRecycleView!!.layoutManager =
            LinearLayoutManager(applicationContext, LinearLayoutManager.VERTICAL, false)
        settingScheduleRecycleView!!.itemAnimator = DefaultItemAnimator()
        settingScheduleRecycleView!!.adapter = settingSchedListAdapter
    }

    fun upDateVideoSettingDetailsTwo() {

        showCustomProgressAlertDialog(
            resources.getString(R.string.fetching),
            resources.getString(R.string.wait_while_we_fetching)
        )

        val url = ApiUrls.updateDoctorServiceSetting
        val objSlot = JSONArray()
        var slotOneSpinnerOne = ""
        var slotOneSpinnerTwo = ""
        val objSlotOne = JSONArray()
        val objSlotTwo = JSONArray()

        if (settingVideoSchedModelList!!.size > 1) {
//            val objSlotOne = JSONArray()
            try {
                for (i in 1..7) {
                    // 1st object
                    if (i == 1) {
                        val list1 = JSONObject()
                        list1.put(
                            "id",
                            SettingVideoScheduleListAdapter.settingVideoSchedModelList[i].idOne
                        )
                        list1.put("day", "Monday")
                        list1.put(
                            "slot_id",
                            SettingVideoScheduleListAdapter.settingVideoSchedModelList[i].slotIdOne
                        )
                        list1.put(
                            "schedule_id",
                            SettingVideoScheduleListAdapter.settingVideoSchedModelList[i].scheduleIdOne
                        )
                        slotOneSpinnerOne = ""
                        slotOneSpinnerTwo = ""
                        val currentString =
                            SettingVideoScheduleListAdapter.settingVideoSchedModelList[i].slotOneTime.toString()
                        if (!currentString.equals("", ignoreCase = true)) {
                            val separated =
                                currentString.split("-".toRegex()).dropLastWhile { it.isEmpty() }
                                    .toTypedArray()
                            slotOneSpinnerOne = separated[0]
                            slotOneSpinnerTwo = separated[1]
                        }
                        if (slotOneSpinnerOne.equals("00:00:00", ignoreCase = true) &&
                            slotOneSpinnerTwo.equals("00:00:00", ignoreCase = true)
                        ) {
                            list1.put("start_time", "")
                            list1.put("end_time", "")
                        } else {
                            list1.put("start_time", slotOneSpinnerOne)
                            list1.put("end_time", slotOneSpinnerTwo)
                        }
                        list1.put(
                            "slot_flag",
                            SettingVideoScheduleListAdapter.settingVideoSchedModelList[i].slotFlagIdOne
                        )
                        objSlotOne.put(list1)
                    } else if (i == 2) {
                        val list1 = JSONObject()
                        list1.put(
                            "id",
                            SettingVideoScheduleListAdapter.settingVideoSchedModelList[i].idOne
                        )
                        list1.put("day", "Tuesday")
                        list1.put(
                            "slot_id",
                            SettingVideoScheduleListAdapter.settingVideoSchedModelList[i].slotIdOne
                        )
                        list1.put(
                            "schedule_id",
                            SettingVideoScheduleListAdapter.settingVideoSchedModelList[i].scheduleIdOne
                        )
                        slotOneSpinnerOne = ""
                        slotOneSpinnerTwo = ""
                        val currentString =
                            SettingVideoScheduleListAdapter.settingVideoSchedModelList[i].slotOneTime.toString()
                        if (!currentString.equals("", ignoreCase = true)) {
                            val separated =
                                currentString.split("-".toRegex()).dropLastWhile { it.isEmpty() }
                                    .toTypedArray()
                            slotOneSpinnerOne = separated[0]
                            slotOneSpinnerTwo = separated[1]
                        }
                        if (slotOneSpinnerOne.equals("00:00:00", ignoreCase = true) &&
                            slotOneSpinnerTwo.equals("00:00:00", ignoreCase = true)
                        ) {
                            list1.put("start_time", "")
                            list1.put("end_time", "")
                        } else {
                            list1.put("start_time", slotOneSpinnerOne)
                            list1.put("end_time", slotOneSpinnerTwo)
                        }
                        list1.put(
                            "slot_flag",
                            SettingVideoScheduleListAdapter.settingVideoSchedModelList[i].slotFlagIdOne
                        )
                        objSlotOne.put(list1)
                    } else if (i == 3) {
                        val list1 = JSONObject()
                        list1.put(
                            "id",
                            SettingVideoScheduleListAdapter.settingVideoSchedModelList[i].idOne
                        )
                        list1.put("day", "Wednesday")
                        list1.put(
                            "slot_id",
                            SettingVideoScheduleListAdapter.settingVideoSchedModelList[i].slotIdOne
                        )
                        list1.put(
                            "schedule_id",
                            SettingVideoScheduleListAdapter.settingVideoSchedModelList[i].scheduleIdOne
                        )
                        slotOneSpinnerOne = ""
                        slotOneSpinnerTwo = ""
                        val currentString =
                            SettingVideoScheduleListAdapter.settingVideoSchedModelList[i].slotOneTime.toString()
                        if (!currentString.equals("", ignoreCase = true)) {
                            val separated =
                                currentString.split("-".toRegex()).dropLastWhile { it.isEmpty() }
                                    .toTypedArray()
                            slotOneSpinnerOne = separated[0]
                            slotOneSpinnerTwo = separated[1]
                        }
                        if (slotOneSpinnerOne.equals("00:00:00", ignoreCase = true) &&
                            slotOneSpinnerTwo.equals("00:00:00", ignoreCase = true)
                        ) {
                            list1.put("start_time", "")
                            list1.put("end_time", "")
                        } else {
                            list1.put("start_time", slotOneSpinnerOne)
                            list1.put("end_time", slotOneSpinnerTwo)
                        }
                        list1.put(
                            "slot_flag",
                            SettingVideoScheduleListAdapter.settingVideoSchedModelList[i].slotFlagIdOne
                        )
                        objSlotOne.put(list1)
                    } else if (i == 4) {
                        val list1 = JSONObject()
                        list1.put(
                            "id",
                            SettingVideoScheduleListAdapter.settingVideoSchedModelList[i].idOne
                        )
                        list1.put("day", "Thursday")
                        list1.put(
                            "slot_id",
                            SettingVideoScheduleListAdapter.settingVideoSchedModelList[i].slotIdOne
                        )
                        list1.put(
                            "schedule_id",
                            SettingVideoScheduleListAdapter.settingVideoSchedModelList[i].scheduleIdOne
                        )
                        slotOneSpinnerOne = ""
                        slotOneSpinnerTwo = ""
                        val currentString =
                            SettingVideoScheduleListAdapter.settingVideoSchedModelList[i].slotOneTime.toString()
                        if (!currentString.equals("", ignoreCase = true)) {
                            val separated =
                                currentString.split("-".toRegex()).dropLastWhile { it.isEmpty() }
                                    .toTypedArray()
                            slotOneSpinnerOne = separated[0]
                            slotOneSpinnerTwo = separated[1]
                        }
                        if (slotOneSpinnerOne.equals("00:00:00", ignoreCase = true) &&
                            slotOneSpinnerTwo.equals("00:00:00", ignoreCase = true)
                        ) {
                            list1.put("start_time", "")
                            list1.put("end_time", "")
                        } else {
                            list1.put("start_time", slotOneSpinnerOne)
                            list1.put("end_time", slotOneSpinnerTwo)
                        }
                        list1.put(
                            "slot_flag",
                            SettingVideoScheduleListAdapter.settingVideoSchedModelList[i].slotFlagIdOne
                        )
                        objSlotOne.put(list1)
                    } else if (i == 5) {
                        val list1 = JSONObject()
                        list1.put(
                            "id",
                            SettingVideoScheduleListAdapter.settingVideoSchedModelList[i].idOne
                        )
                        list1.put("day", "Friday")
                        list1.put(
                            "slot_id",
                            SettingVideoScheduleListAdapter.settingVideoSchedModelList[i].slotIdOne
                        )
                        list1.put(
                            "schedule_id",
                            SettingVideoScheduleListAdapter.settingVideoSchedModelList[i].scheduleIdOne
                        )
                        slotOneSpinnerOne = ""
                        slotOneSpinnerTwo = ""
                        val currentString =
                            SettingVideoScheduleListAdapter.settingVideoSchedModelList[i].slotOneTime.toString()
                        if (!currentString.equals("", ignoreCase = true)) {
                            val separated =
                                currentString.split("-".toRegex()).dropLastWhile { it.isEmpty() }
                                    .toTypedArray()
                            slotOneSpinnerOne = separated[0]
                            slotOneSpinnerTwo = separated[1]
                        }
                        if (slotOneSpinnerOne.equals("00:00:00", ignoreCase = true) &&
                            slotOneSpinnerTwo.equals("00:00:00", ignoreCase = true)
                        ) {
                            list1.put("start_time", "")
                            list1.put("end_time", "")
                        } else {
                            list1.put("start_time", slotOneSpinnerOne)
                            list1.put("end_time", slotOneSpinnerTwo)
                        }
                        list1.put(
                            "slot_flag",
                            SettingVideoScheduleListAdapter.settingVideoSchedModelList[i].slotFlagIdOne
                        )
                        objSlotOne.put(list1)
                    } else if (i == 6) {
                        val list1 = JSONObject()
                        list1.put(
                            "id",
                            SettingVideoScheduleListAdapter.settingVideoSchedModelList[i].idOne
                        )
                        list1.put("day", "Saturday")
                        list1.put(
                            "slot_id",
                            SettingVideoScheduleListAdapter.settingVideoSchedModelList[i].slotIdOne
                        )
                        list1.put(
                            "schedule_id",
                            SettingVideoScheduleListAdapter.settingVideoSchedModelList[i].scheduleIdOne
                        )
                        slotOneSpinnerOne = ""
                        slotOneSpinnerTwo = ""
                        val currentString =
                            SettingVideoScheduleListAdapter.settingVideoSchedModelList[i].slotOneTime.toString()
                        if (!currentString.equals("", ignoreCase = true)) {
                            val separated =
                                currentString.split("-".toRegex()).dropLastWhile { it.isEmpty() }
                                    .toTypedArray()
                            slotOneSpinnerOne = separated[0]
                            slotOneSpinnerTwo = separated[1]
                        }
                        if (slotOneSpinnerOne.equals("00:00:00", ignoreCase = true) &&
                            slotOneSpinnerTwo.equals("00:00:00", ignoreCase = true)
                        ) {
                            list1.put("start_time", "")
                            list1.put("end_time", "")
                        } else {
                            list1.put("start_time", slotOneSpinnerOne)
                            list1.put("end_time", slotOneSpinnerTwo)
                        }
                        list1.put(
                            "slot_flag",
                            SettingVideoScheduleListAdapter.settingVideoSchedModelList[i].slotFlagIdOne
                        )
                        objSlotOne.put(list1)
                    } else if (i == 7) {
                        val list1 = JSONObject()
                        list1.put(
                            "id",
                            SettingVideoScheduleListAdapter.settingVideoSchedModelList[i].idOne
                        )
                        list1.put("day", "Sunday")
                        list1.put(
                            "slot_id",
                            SettingVideoScheduleListAdapter.settingVideoSchedModelList[i].slotIdOne
                        )
                        list1.put(
                            "schedule_id",
                            SettingVideoScheduleListAdapter.settingVideoSchedModelList[i].scheduleIdOne
                        )
                        slotOneSpinnerOne = ""
                        slotOneSpinnerTwo = ""
                        val currentString =
                            SettingVideoScheduleListAdapter.settingVideoSchedModelList[i].slotOneTime.toString()
                        if (!currentString.equals("", ignoreCase = true)) {
                            val separated =
                                currentString.split("-".toRegex()).dropLastWhile { it.isEmpty() }
                                    .toTypedArray()
                            slotOneSpinnerOne = separated[0]
                            slotOneSpinnerTwo = separated[1]
                        }
                        if (slotOneSpinnerOne.equals("00:00:00", ignoreCase = true) &&
                            slotOneSpinnerTwo.equals("00:00:00", ignoreCase = true)
                        ) {
                            list1.put("start_time", "")
                            list1.put("end_time", "")
                        } else {
                            list1.put("start_time", slotOneSpinnerOne)
                            list1.put("end_time", slotOneSpinnerTwo)
                        }
                        list1.put(
                            "slot_flag",
                            SettingVideoScheduleListAdapter.settingVideoSchedModelList[i].slotFlagIdOne
                        )
                        objSlotOne.put(list1)
                    }
                }
            } catch (e1: JSONException) {
                e1.printStackTrace()
            }
//            val objSlotTwo = JSONArray()
            try {
                for (i in 1..7) {
                    // 1st object
                    if (i == 1) {
                        val list1 = JSONObject()
                        list1.put(
                            "id",
                            SettingVideoScheduleListAdapter.settingVideoSchedModelList[i].idTwo
                        )
                        list1.put("day", "Monday")
                        list1.put(
                            "slot_id",
                            SettingVideoScheduleListAdapter.settingVideoSchedModelList[i].slotIdTwo
                        )
                        list1.put(
                            "schedule_id",
                            SettingVideoScheduleListAdapter.settingVideoSchedModelList[i].scheduleIdTwo
                        )
                        slotOneSpinnerOne = ""
                        slotOneSpinnerTwo = ""
                        val currentString =
                            SettingVideoScheduleListAdapter.settingVideoSchedModelList[i].slotTwoTime.toString()
                        if (!currentString.equals("", ignoreCase = true)) {
                            val separated =
                                currentString.split("-".toRegex()).dropLastWhile { it.isEmpty() }
                                    .toTypedArray()
                            slotOneSpinnerOne = separated[0]
                            slotOneSpinnerTwo = separated[1]
                        }
                        if (slotOneSpinnerOne.equals("00:00:00", ignoreCase = true) &&
                            slotOneSpinnerTwo.equals("00:00:00", ignoreCase = true)
                        ) {
                            list1.put("start_time", "")
                            list1.put("end_time", "")
                        } else {
                            list1.put("start_time", slotOneSpinnerOne)
                            list1.put("end_time", slotOneSpinnerTwo)
                        }
                        list1.put(
                            "slot_flag",
                            SettingVideoScheduleListAdapter.settingVideoSchedModelList[i].slotFlagIdTwo
                        )
                        objSlotTwo.put(list1)
                    } else if (i == 2) {
                        val list1 = JSONObject()
                        list1.put(
                            "id",
                            SettingVideoScheduleListAdapter.settingVideoSchedModelList[i].idTwo
                        )
                        list1.put("day", "Tuesday")
                        list1.put(
                            "slot_id",
                            SettingVideoScheduleListAdapter.settingVideoSchedModelList[i].slotIdTwo
                        )
                        list1.put(
                            "schedule_id",
                            SettingVideoScheduleListAdapter.settingVideoSchedModelList[i].scheduleIdTwo
                        )
                        slotOneSpinnerOne = ""
                        slotOneSpinnerTwo = ""
                        val currentString =
                            SettingVideoScheduleListAdapter.settingVideoSchedModelList[i].slotTwoTime.toString()
                        if (!currentString.equals("", ignoreCase = true)) {
                            val separated =
                                currentString.split("-".toRegex()).dropLastWhile { it.isEmpty() }
                                    .toTypedArray()
                            slotOneSpinnerOne = separated[0]
                            slotOneSpinnerTwo = separated[1]
                        }
                        if (slotOneSpinnerOne.equals("00:00:00", ignoreCase = true) &&
                            slotOneSpinnerTwo.equals("00:00:00", ignoreCase = true)
                        ) {
                            list1.put("start_time", "")
                            list1.put("end_time", "")
                        } else {
                            list1.put("start_time", slotOneSpinnerOne)
                            list1.put("end_time", slotOneSpinnerTwo)
                        }
                        list1.put(
                            "slot_flag",
                            SettingVideoScheduleListAdapter.settingVideoSchedModelList[i].slotFlagIdTwo
                        )
                        objSlotTwo.put(list1)
                    } else if (i == 3) {
                        val list1 = JSONObject()
                        list1.put(
                            "id",
                            SettingVideoScheduleListAdapter.settingVideoSchedModelList[i].idTwo
                        )
                        list1.put("day", "Wednesday")
                        list1.put(
                            "slot_id",
                            SettingVideoScheduleListAdapter.settingVideoSchedModelList[i].slotIdTwo
                        )
                        list1.put(
                            "schedule_id",
                            SettingVideoScheduleListAdapter.settingVideoSchedModelList[i].scheduleIdTwo
                        )
                        slotOneSpinnerOne = ""
                        slotOneSpinnerTwo = ""
                        val currentString =
                            SettingVideoScheduleListAdapter.settingVideoSchedModelList[i].slotTwoTime.toString()
                        if (!currentString.equals("", ignoreCase = true)) {
                            val separated =
                                currentString.split("-".toRegex()).dropLastWhile { it.isEmpty() }
                                    .toTypedArray()
                            slotOneSpinnerOne = separated[0]
                            slotOneSpinnerTwo = separated[1]
                        }
                        if (slotOneSpinnerOne.equals("00:00:00", ignoreCase = true) &&
                            slotOneSpinnerTwo.equals("00:00:00", ignoreCase = true)
                        ) {
                            list1.put("start_time", "")
                            list1.put("end_time", "")
                        } else {
                            list1.put("start_time", slotOneSpinnerOne)
                            list1.put("end_time", slotOneSpinnerTwo)
                        }
                        list1.put(
                            "slot_flag",
                            SettingVideoScheduleListAdapter.settingVideoSchedModelList[i].slotFlagIdTwo
                        )
                        objSlotTwo.put(list1)
                    } else if (i == 4) {
                        val list1 = JSONObject()
                        list1.put(
                            "id",
                            SettingVideoScheduleListAdapter.settingVideoSchedModelList[i].idTwo
                        )
                        list1.put("day", "Thursday")
                        list1.put(
                            "slot_id",
                            SettingVideoScheduleListAdapter.settingVideoSchedModelList[i].slotIdTwo
                        )
                        list1.put(
                            "schedule_id",
                            SettingVideoScheduleListAdapter.settingVideoSchedModelList[i].scheduleIdTwo
                        )
                        slotOneSpinnerOne = ""
                        slotOneSpinnerTwo = ""
                        val currentString =
                            SettingVideoScheduleListAdapter.settingVideoSchedModelList[i].slotTwoTime.toString()
                        if (!currentString.equals("", ignoreCase = true)) {
                            val separated =
                                currentString.split("-".toRegex()).dropLastWhile { it.isEmpty() }
                                    .toTypedArray()
                            slotOneSpinnerOne = separated[0]
                            slotOneSpinnerTwo = separated[1]
                        }
                        if (slotOneSpinnerOne.equals("00:00:00", ignoreCase = true) &&
                            slotOneSpinnerTwo.equals("00:00:00", ignoreCase = true)
                        ) {
                            list1.put("start_time", "")
                            list1.put("end_time", "")
                        } else {
                            list1.put("start_time", slotOneSpinnerOne)
                            list1.put("end_time", slotOneSpinnerTwo)
                        }
                        list1.put(
                            "slot_flag",
                            SettingVideoScheduleListAdapter.settingVideoSchedModelList[i].slotFlagIdTwo
                        )
                        objSlotTwo.put(list1)
                    } else if (i == 5) {
                        val list1 = JSONObject()
                        list1.put(
                            "id",
                            SettingVideoScheduleListAdapter.settingVideoSchedModelList[i].idTwo
                        )
                        list1.put("day", "Friday")
                        list1.put(
                            "slot_id",
                            SettingVideoScheduleListAdapter.settingVideoSchedModelList[i].slotIdTwo
                        )
                        list1.put(
                            "schedule_id",
                            SettingVideoScheduleListAdapter.settingVideoSchedModelList[i].scheduleIdTwo
                        )
                        slotOneSpinnerOne = ""
                        slotOneSpinnerTwo = ""
                        val currentString =
                            SettingVideoScheduleListAdapter.settingVideoSchedModelList[i].slotTwoTime.toString()
                        if (!currentString.equals("", ignoreCase = true)) {
                            val separated =
                                currentString.split("-".toRegex()).dropLastWhile { it.isEmpty() }
                                    .toTypedArray()
                            slotOneSpinnerOne = separated[0]
                            slotOneSpinnerTwo = separated[1]
                        }
                        if (slotOneSpinnerOne.equals("00:00:00", ignoreCase = true) &&
                            slotOneSpinnerTwo.equals("00:00:00", ignoreCase = true)
                        ) {
                            list1.put("start_time", "")
                            list1.put("end_time", "")
                        } else {
                            list1.put("start_time", slotOneSpinnerOne)
                            list1.put("end_time", slotOneSpinnerTwo)
                        }
                        list1.put(
                            "slot_flag",
                            SettingVideoScheduleListAdapter.settingVideoSchedModelList[i].slotFlagIdTwo
                        )
                        objSlotTwo.put(list1)
                    } else if (i == 6) {
                        val list1 = JSONObject()
                        list1.put(
                            "id",
                            SettingVideoScheduleListAdapter.settingVideoSchedModelList[i].idTwo
                        )
                        list1.put("day", "Saturday")
                        list1.put(
                            "slot_id",
                            SettingVideoScheduleListAdapter.settingVideoSchedModelList[i].slotIdTwo
                        )
                        list1.put(
                            "schedule_id",
                            SettingVideoScheduleListAdapter.settingVideoSchedModelList[i].scheduleIdTwo
                        )
                        slotOneSpinnerOne = ""
                        slotOneSpinnerTwo = ""
                        val currentString =
                            SettingVideoScheduleListAdapter.settingVideoSchedModelList[i].slotTwoTime.toString()
                        if (!currentString.equals("", ignoreCase = true)) {
                            val separated =
                                currentString.split("-".toRegex()).dropLastWhile { it.isEmpty() }
                                    .toTypedArray()
                            slotOneSpinnerOne = separated[0]
                            slotOneSpinnerTwo = separated[1]
                        }
                        if (slotOneSpinnerOne.equals("00:00:00", ignoreCase = true) &&
                            slotOneSpinnerTwo.equals("00:00:00", ignoreCase = true)
                        ) {
                            list1.put("start_time", "")
                            list1.put("end_time", "")
                        } else {
                            list1.put("start_time", slotOneSpinnerOne)
                            list1.put("end_time", slotOneSpinnerTwo)
                        }
                        list1.put(
                            "slot_flag",
                            SettingVideoScheduleListAdapter.settingVideoSchedModelList[i].slotFlagIdTwo
                        )
                        objSlotTwo.put(list1)
                    } else if (i == 7) {
                        val list1 = JSONObject()
                        list1.put(
                            "id",
                            SettingVideoScheduleListAdapter.settingVideoSchedModelList[i].idTwo
                        )
                        list1.put("day", "Sunday")
                        list1.put(
                            "slot_id",
                            SettingVideoScheduleListAdapter.settingVideoSchedModelList[i].slotIdTwo
                        )
                        list1.put(
                            "schedule_id",
                            SettingVideoScheduleListAdapter.settingVideoSchedModelList[i].scheduleIdTwo
                        )
                        slotOneSpinnerOne = ""
                        slotOneSpinnerTwo = ""
                        val currentString =
                            SettingVideoScheduleListAdapter.settingVideoSchedModelList[i].slotTwoTime.toString()
                        if (!currentString.equals("", ignoreCase = true)) {
                            val separated =
                                currentString.split("-".toRegex()).dropLastWhile { it.isEmpty() }
                                    .toTypedArray()
                            slotOneSpinnerOne = separated[0]
                            slotOneSpinnerTwo = separated[1]
                        }
                        if (slotOneSpinnerOne.equals("00:00:00", ignoreCase = true) &&
                            slotOneSpinnerTwo.equals("00:00:00", ignoreCase = true)
                        ) {
                            list1.put("start_time", "")
                            list1.put("end_time", "")
                        } else {
                            list1.put("start_time", slotOneSpinnerOne)
                            list1.put("end_time", slotOneSpinnerTwo)
                        }
                        list1.put(
                            "slot_flag",
                            SettingVideoScheduleListAdapter.settingVideoSchedModelList[i].slotFlagIdTwo
                        )
                        objSlotTwo.put(list1)
                    }
                }
                objSlot.put(0, objSlotOne)
                objSlot.put(1, objSlotTwo)
            } catch (e1: JSONException) {
                e1.printStackTrace()
            }
            try {
                jsonValue = JSONObject()
                if (selectedSpinnerNoShow.equals("Yes", ignoreCase = true)) {
                    automatic_no_show_control = 0
                    automatic_no_show_time_limit = no_show_limit_video_et!!.text.toString().toInt()
                } else {
                    automatic_no_show_control = 1
                    automatic_no_show_time_limit = 0
                }
                jsonValue!!.put("alias", settingVideoShortText!!.text.toString())
                jsonValue!!.put("desc", settingVideoDescriptionText!!.text.toString())
                jsonValue!!.put("price", videoPriceInput!!.text.toString().toInt())
                jsonValue!!.put(
                    "appt_duration",
                    appointmentDurationSpinner!!.selectedItem.toString().toInt()
                )
                jsonValue!!.put("adv_booking", advanceBookingText!!.text.toString().toInt())
                jsonValue!!.put("booking_duration", videoDurationSpinner!!.selectedItem.toString())
                jsonValue!!.put("slots", objSlot)
                jsonValue!!.put("service", "Video")
                jsonValue!!.put("automatic_no_show_control", automatic_no_show_control)
                jsonValue!!.put("automatic_no_show_time_limit", automatic_no_show_time_limit)
            } catch (e: Exception) {
            }
        } // added by dileep 31st december 2019
        else {
            try {
                jsonValue = JSONObject()
                if (selectedSpinnerNoShow.equals("Yes", ignoreCase = true)) {
                    automatic_no_show_control = 0
                    automatic_no_show_time_limit = no_show_limit_video_et!!.text.toString().toInt()
                } else {
                    automatic_no_show_control = 1
                    automatic_no_show_time_limit = 0
                }
                jsonValue!!.put("alias", settingVideoShortText!!.text.toString())
                jsonValue!!.put("desc", settingVideoDescriptionText!!.text.toString())
                jsonValue!!.put("price", videoPriceInput!!.text.toString().toInt())
                jsonValue!!.put(
                    "appt_duration",
                    appointmentDurationSpinner!!.selectedItem.toString().toInt()
                )
                jsonValue!!.put("adv_booking", advanceBookingText!!.text.toString().toInt())
                jsonValue!!.put("booking_duration", videoDurationSpinner!!.selectedItem.toString())
                jsonValue!!.put("slots", objSlot)
                jsonValue!!.put("service", "Video")
                jsonValue!!.put("show_previous_slots", false)
                jsonValue!!.put("automatic_no_show_control", automatic_no_show_control)
                jsonValue!!.put("automatic_no_show_time_limit", automatic_no_show_time_limit)
                Log.d("newJsonValue ", jsonValue.toString())
            } catch (e: Exception) {
            }
        }

        for (i in 0 until objSlotOne.length()) {
            val slotOneArray = objSlotOne.getJSONObject(i)
            if (!slotOneArray.get("start_time").toString().equals("", ignoreCase = true)) {


                jsonValue?.let {
                    commonViewModel.commonViewModelCall(url, it, Request.Method.POST).observe(
                        this@SettingsFormActivity
                    ) { result ->
                        try {
                            //Process os success response
                            val responseObj = JSONObject(result)
                            dialog.dismiss()
                            if (responseObj.getInt("status_code") == 200) {
                                val response = responseObj.optJSONObject("response")
                                val successMessageString = response?.getString("response")
                                Toast.makeText(
                                    this@SettingsFormActivity,
                                    successMessageString,
                                    Toast.LENGTH_SHORT
                                ).show()

                            } else {
                                dialog.dismiss()
                                errorHandler(this@SettingsFormActivity, result)
                            }
                        } catch (e: Exception) {
                            dialog.dismiss()
                            e.printStackTrace()
                        }
                    }
                }


                slotSetOneCount = 0;
                slotSetTwoCount = 0;
                break;
            } else {
                slotSetOneCount++;
                if (slotSetOneCount == 7) {
                    for (j in 0 until objSlotTwo.length()) {
                        val slotTwoArray = objSlotTwo.getJSONObject(j)
                        if (!slotTwoArray.get("start_time").toString()
                                .equals("", ignoreCase = true)
                        ) {


                            jsonValue?.let {
                                commonViewModel.commonViewModelCall(url, it, Request.Method.POST)
                                    .observe(
                                        this@SettingsFormActivity
                                    ) { result ->
                                        try {
                                            //Process os success response
                                            val responseObj = JSONObject(result)
                                            dialog.dismiss()
                                            if (responseObj.getInt("status_code") == 200) {
                                                val response = responseObj.optJSONObject("response")
                                                val successMessageString =
                                                    response?.getString("response")
                                                Toast.makeText(
                                                    this@SettingsFormActivity,
                                                    successMessageString,
                                                    Toast.LENGTH_SHORT
                                                ).show()

                                            } else {
                                                dialog.dismiss()
                                                errorHandler(this@SettingsFormActivity, result)
                                            }
                                        } catch (e: Exception) {
                                            dialog.dismiss()
                                            e.printStackTrace()
                                        }
                                    }
                            }




                            slotSetOneCount = 0;
                            slotSetTwoCount = 0;
                            break;
                        } else {
                            slotSetTwoCount++;
                            if (slotSetTwoCount == 7) {
                                Toast.makeText(
                                    this@SettingsFormActivity,
                                    "Please Click on Edit to set atleast one slot",
                                    Toast.LENGTH_SHORT
                                )
                                    .show()
                                slotSetOneCount = 0;
                                slotSetTwoCount = 0;
                                dialog.dismiss()
                            }
                        }
                    }
                }
            }

        }


//        jsonValue?.let {
//            commonViewModel.commonViewModelCall(url, it, Request.Method.POST).observe(
//                this@SettingsFormActivity
//            ) { result ->
//                try {
//                    //Process os success response
//                    val responseObj = JSONObject(result)
//                    dialog.dismiss()
//                    if (responseObj.getInt("status_code") == 200) {
//                        val response = responseObj.optJSONObject("response")
//                        val successMessageString = response?.getString("response")
//                        Toast.makeText(
//                            this@SettingsFormActivity,
//                            successMessageString,
//                            Toast.LENGTH_SHORT
//                        ).show()
//
//                    } else {
//                        dialog.dismiss()
//                        errorHandler(this@SettingsFormActivity, result)
//                    }
//                } catch (e: Exception) {
//                    dialog.dismiss()
//                    e.printStackTrace()
//                }
//            }
//        }


    }

    fun upDateVideoSettingDetailsTwoService() {
        showCustomProgressAlertDialog(
            resources.getString(R.string.fetching),
            resources.getString(R.string.wait_while_we_fetching)
        )

        val url = ApiUrls.setupVideoServiceSetting
        val objSlot = JSONArray()
        val objSlotOne = JSONArray()
        try {
            for (i in 1..7) {
                // 1st object
                if (i == 1) {
                    val list1 = JSONObject()
                    list1.put("day", "Monday")
                    val currentString =
                        SettingVideoScheduleListAdapter.settingVideoSchedModelList[i].slotOneTime.toString()
                    val separated =
                        currentString.split("-".toRegex()).dropLastWhile { it.isEmpty() }
                            .toTypedArray()
                    val slotOneSpinnerOne = separated[0]
                    val slotOneSpinnerTwo = separated[1]
                    if (!slotOneSpinnerOne.equals("00:00:00", ignoreCase = true)) {
                        list1.put("start_time", slotOneSpinnerOne)
                    } else {
                        list1.put("start_time", "")
                    }
                    if (!slotOneSpinnerTwo.equals("00:00:00", ignoreCase = true)) {
                        list1.put("end_time", slotOneSpinnerTwo)
                    } else {
                        list1.put("end_time", "")
                    }
                    objSlotOne.put(list1)
                } else if (i == 2) {
                    val list1 = JSONObject()
                    list1.put("day", "Tuesday")
                    val currentString =
                        SettingVideoScheduleListAdapter.settingVideoSchedModelList[i].slotOneTime.toString()
                    val separated =
                        currentString.split("-".toRegex()).dropLastWhile { it.isEmpty() }
                            .toTypedArray()
                    val slotOneSpinnerOne = separated[0]
                    val slotOneSpinnerTwo = separated[1]
                    if (!slotOneSpinnerOne.equals("00:00:00", ignoreCase = true)) {
                        list1.put("start_time", slotOneSpinnerOne)
                    } else {
                        list1.put("start_time", "")
                    }
                    if (!slotOneSpinnerTwo.equals("00:00:00", ignoreCase = true)) {
                        list1.put("end_time", slotOneSpinnerTwo)
                    } else {
                        list1.put("end_time", "")
                    }
                    objSlotOne.put(list1)
                } else if (i == 3) {
                    val list1 = JSONObject()
                    list1.put("day", "Wednesday")
                    val currentString =
                        SettingVideoScheduleListAdapter.settingVideoSchedModelList[i].slotOneTime.toString()
                    val separated =
                        currentString.split("-".toRegex()).dropLastWhile { it.isEmpty() }
                            .toTypedArray()
                    val slotOneSpinnerOne = separated[0]
                    val slotOneSpinnerTwo = separated[1]
                    if (!slotOneSpinnerOne.equals("00:00:00", ignoreCase = true)) {
                        list1.put("start_time", slotOneSpinnerOne)
                    } else {
                        list1.put("start_time", "")
                    }
                    if (!slotOneSpinnerTwo.equals("00:00:00", ignoreCase = true)) {
                        list1.put("end_time", slotOneSpinnerTwo)
                    } else {
                        list1.put("end_time", "")
                    }
                    objSlotOne.put(list1)
                } else if (i == 4) {
                    val list1 = JSONObject()
                    list1.put("day", "Thursday")
                    val currentString =
                        SettingVideoScheduleListAdapter.settingVideoSchedModelList[i].slotOneTime.toString()
                    val separated =
                        currentString.split("-".toRegex()).dropLastWhile { it.isEmpty() }
                            .toTypedArray()
                    val slotOneSpinnerOne = separated[0]
                    val slotOneSpinnerTwo = separated[1]
                    if (!slotOneSpinnerOne.equals("00:00:00", ignoreCase = true)) {
                        list1.put("start_time", slotOneSpinnerOne)
                    } else {
                        list1.put("start_time", "")
                    }
                    if (!slotOneSpinnerTwo.equals("00:00:00", ignoreCase = true)) {
                        list1.put("end_time", slotOneSpinnerTwo)
                    } else {
                        list1.put("end_time", "")
                    }
                    objSlotOne.put(list1)
                } else if (i == 5) {
                    val list1 = JSONObject()
                    list1.put("day", "Friday")
                    val currentString =
                        SettingVideoScheduleListAdapter.settingVideoSchedModelList[i].slotOneTime.toString()
                    val separated =
                        currentString.split("-".toRegex()).dropLastWhile { it.isEmpty() }
                            .toTypedArray()
                    val slotOneSpinnerOne = separated[0]
                    val slotOneSpinnerTwo = separated[1]
                    if (!slotOneSpinnerOne.equals("00:00:00", ignoreCase = true)) {
                        list1.put("start_time", slotOneSpinnerOne)
                    } else {
                        list1.put("start_time", "")
                    }
                    if (!slotOneSpinnerTwo.equals("00:00:00", ignoreCase = true)) {
                        list1.put("end_time", slotOneSpinnerTwo)
                    } else {
                        list1.put("end_time", "")
                    }
                    objSlotOne.put(list1)
                } else if (i == 6) {
                    val list1 = JSONObject()
                    list1.put("day", "Saturday")
                    val currentString =
                        SettingVideoScheduleListAdapter.settingVideoSchedModelList[i].slotOneTime.toString()
                    val separated =
                        currentString.split("-".toRegex()).dropLastWhile { it.isEmpty() }
                            .toTypedArray()
                    val slotOneSpinnerOne = separated[0]
                    val slotOneSpinnerTwo = separated[1]
                    if (!slotOneSpinnerOne.equals("00:00:00", ignoreCase = true)) {
                        list1.put("start_time", slotOneSpinnerOne)
                    } else {
                        list1.put("start_time", "")
                    }
                    if (!slotOneSpinnerTwo.equals("00:00:00", ignoreCase = true)) {
                        list1.put("end_time", slotOneSpinnerTwo)
                    } else {
                        list1.put("end_time", "")
                    }
                    objSlotOne.put(list1)
                } else if (i == 7) {
                    val list1 = JSONObject()
                    list1.put("day", "Sunday")
                    val currentString =
                        SettingVideoScheduleListAdapter.settingVideoSchedModelList[i].slotOneTime.toString()
                    val separated =
                        currentString.split("-".toRegex()).dropLastWhile { it.isEmpty() }
                            .toTypedArray()
                    val slotOneSpinnerOne = separated[0]
                    val slotOneSpinnerTwo = separated[1]
                    if (!slotOneSpinnerOne.equals("00:00:00", ignoreCase = true)) {
                        list1.put("start_time", slotOneSpinnerOne)
                    } else {
                        list1.put("start_time", "")
                    }
                    if (!slotOneSpinnerTwo.equals("00:00:00", ignoreCase = true)) {
                        list1.put("end_time", slotOneSpinnerTwo)
                    } else {
                        list1.put("end_time", "")
                    }
                    objSlotOne.put(list1)
                }
            }
        } catch (e1: JSONException) {
            // TODO Auto-generated catch block
            e1.printStackTrace()
        }
        val objSlotTwo = JSONArray()
        try {
            for (i in 1..7) {
                // 1st object
                if (i == 1) {
                    val list1 = JSONObject()
                    list1.put("day", "Monday")
                    val currentString =
                        SettingVideoScheduleListAdapter.settingVideoSchedModelList[i].slotTwoTime.toString()
                    val separated =
                        currentString.split("-".toRegex()).dropLastWhile { it.isEmpty() }
                            .toTypedArray()
                    val slotOneSpinnerOne = separated[0]
                    val slotOneSpinnerTwo = separated[1]
                    if (!slotOneSpinnerOne.equals("00:00:00", ignoreCase = true)) {
                        list1.put("start_time", slotOneSpinnerOne)
                    } else {
                        list1.put("start_time", "")
                    }
                    if (!slotOneSpinnerTwo.equals("00:00:00", ignoreCase = true)) {
                        list1.put("end_time", slotOneSpinnerTwo)
                    } else {
                        list1.put("end_time", "")
                    }
                    objSlotTwo.put(list1)
                } else if (i == 2) {
                    val list1 = JSONObject()
                    list1.put("day", "Tuesday")
                    val currentString =
                        SettingVideoScheduleListAdapter.settingVideoSchedModelList[i].slotTwoTime.toString()
                    val separated =
                        currentString.split("-".toRegex()).dropLastWhile { it.isEmpty() }
                            .toTypedArray()
                    val slotOneSpinnerOne = separated[0]
                    val slotOneSpinnerTwo = separated[1]
                    if (!slotOneSpinnerOne.equals("00:00:00", ignoreCase = true)) {
                        list1.put("start_time", slotOneSpinnerOne)
                    } else {
                        list1.put("start_time", "")
                    }
                    if (!slotOneSpinnerTwo.equals("00:00:00", ignoreCase = true)) {
                        list1.put("end_time", slotOneSpinnerTwo)
                    } else {
                        list1.put("end_time", "")
                    }
                    objSlotTwo.put(list1)
                } else if (i == 3) {
                    val list1 = JSONObject()
                    list1.put("day", "Wednesday")
                    val currentString =
                        SettingVideoScheduleListAdapter.settingVideoSchedModelList[i].slotTwoTime.toString()
                    val separated =
                        currentString.split("-".toRegex()).dropLastWhile { it.isEmpty() }
                            .toTypedArray()
                    val slotOneSpinnerOne = separated[0]
                    val slotOneSpinnerTwo = separated[1]
                    if (!slotOneSpinnerOne.equals("00:00:00", ignoreCase = true)) {
                        list1.put("start_time", slotOneSpinnerOne)
                    } else {
                        list1.put("start_time", "")
                    }
                    if (!slotOneSpinnerTwo.equals("00:00:00", ignoreCase = true)) {
                        list1.put("end_time", slotOneSpinnerTwo)
                    } else {
                        list1.put("end_time", "")
                    }
                    objSlotTwo.put(list1)
                } else if (i == 4) {
                    val list1 = JSONObject()
                    list1.put("day", "Thursday")
                    val currentString =
                        SettingVideoScheduleListAdapter.settingVideoSchedModelList[i].slotTwoTime.toString()
                    val separated =
                        currentString.split("-".toRegex()).dropLastWhile { it.isEmpty() }
                            .toTypedArray()
                    val slotOneSpinnerOne = separated[0]
                    val slotOneSpinnerTwo = separated[1]
                    if (!slotOneSpinnerOne.equals("00:00:00", ignoreCase = true)) {
                        list1.put("start_time", slotOneSpinnerOne)
                    } else {
                        list1.put("start_time", "")
                    }
                    if (!slotOneSpinnerTwo.equals("00:00:00", ignoreCase = true)) {
                        list1.put("end_time", slotOneSpinnerTwo)
                    } else {
                        list1.put("end_time", "")
                    }
                    objSlotTwo.put(list1)
                } else if (i == 5) {
                    val list1 = JSONObject()
                    list1.put("day", "Friday")
                    val currentString =
                        SettingVideoScheduleListAdapter.settingVideoSchedModelList[i].slotTwoTime.toString()
                    val separated =
                        currentString.split("-".toRegex()).dropLastWhile { it.isEmpty() }
                            .toTypedArray()
                    val slotOneSpinnerOne = separated[0]
                    val slotOneSpinnerTwo = separated[1]
                    if (!slotOneSpinnerOne.equals("00:00:00", ignoreCase = true)) {
                        list1.put("start_time", slotOneSpinnerOne)
                    } else {
                        list1.put("start_time", "")
                    }
                    if (!slotOneSpinnerTwo.equals("00:00:00", ignoreCase = true)) {
                        list1.put("end_time", slotOneSpinnerTwo)
                    } else {
                        list1.put("end_time", "")
                    }
                    objSlotTwo.put(list1)
                } else if (i == 6) {
                    val list1 = JSONObject()
                    list1.put("day", "Saturday")
                    val currentString =
                        SettingVideoScheduleListAdapter.settingVideoSchedModelList[i].slotTwoTime.toString()
                    val separated =
                        currentString.split("-".toRegex()).dropLastWhile { it.isEmpty() }
                            .toTypedArray()
                    val slotOneSpinnerOne = separated[0]
                    val slotOneSpinnerTwo = separated[1]
                    if (!slotOneSpinnerOne.equals("00:00:00", ignoreCase = true)) {
                        list1.put("start_time", slotOneSpinnerOne)
                    } else {
                        list1.put("start_time", "")
                    }
                    if (!slotOneSpinnerTwo.equals("00:00:00", ignoreCase = true)) {
                        list1.put("end_time", slotOneSpinnerTwo)
                    } else {
                        list1.put("end_time", "")
                    }
                    objSlotTwo.put(list1)
                } else if (i == 7) {
                    val list1 = JSONObject()
                    list1.put("day", "Sunday")
                    val currentString =
                        SettingVideoScheduleListAdapter.settingVideoSchedModelList[i].slotTwoTime.toString()
                    val separated =
                        currentString.split("-".toRegex()).dropLastWhile { it.isEmpty() }
                            .toTypedArray()
                    val slotOneSpinnerOne = separated[0]
                    val slotOneSpinnerTwo = separated[1]
                    if (!slotOneSpinnerOne.equals("00:00:00", ignoreCase = true)) {
                        list1.put("start_time", slotOneSpinnerOne)
                    } else {
                        list1.put("start_time", "")
                    }
                    if (!slotOneSpinnerTwo.equals("00:00:00", ignoreCase = true)) {
                        list1.put("end_time", slotOneSpinnerTwo)
                    } else {
                        list1.put("end_time", "")
                    }
                    objSlotTwo.put(list1)
                }
            }
            objSlot.put(0, objSlotOne)
            objSlot.put(1, objSlotTwo)
        } catch (e1: JSONException) {
            // TODO Auto-generated catch block
            e1.printStackTrace()
        }
        try {
            if (clinicSetupFlagClick == 1) {
                jsonValue = JSONObject()
                val clinicArray = JSONArray()
                val clinicObject = JSONObject()
                clinicObject.put("alias", settingServiceVideoShortText!!.text.toString())
                clinicObject.put("desc", settingServiceVideoDescriptionText!!.text.toString())
                clinicObject.put("price", videoServicePriceInput!!.text.toString().toInt())
                clinicObject.put(
                    "appt_duration",
                    appointmentServiceDurationSpinner!!.selectedItem.toString().toInt()
                )
                clinicObject.put("adv_booking", advanceServiceBookingText!!.text.toString().toInt())
                clinicObject.put(
                    "booking_duration",
                    videoServiceDurationSpinner!!.selectedItem.toString()
                )
                clinicObject.put("slots", objSlot)
                clinicObject.put("address", videoServiceAddressText!!.text.toString())
                clinicArray.put(clinicObject)
                jsonValue!!.put("service", "Clinic")
                jsonValue!!.put("clinic", clinicArray)
            } else if (videoSetupFlagClick == 1) {
                if (selectedSpinnerNoShow.equals("Yes", ignoreCase = true)) {
                    automatic_no_show_control = 0
                    automatic_no_show_time_limit =
                        no_show_limit_video_setup_et!!.text.toString().toInt()
                } else {
                    automatic_no_show_control = 1
                    automatic_no_show_time_limit = 0
                }
                jsonValue = JSONObject()
                jsonValue!!.put("alias", settingServiceVideoShortText!!.text.toString())
                jsonValue!!.put("desc", settingServiceVideoDescriptionText!!.text.toString())
                jsonValue!!.put("price", videoServicePriceInput!!.text.toString().toInt())
                jsonValue!!.put(
                    "appt_duration",
                    appointmentServiceDurationSpinner!!.selectedItem.toString().toInt()
                )
                jsonValue!!.put("adv_booking", advanceServiceBookingText!!.text.toString().toInt())
                jsonValue!!.put(
                    "booking_duration",
                    videoServiceDurationSpinner!!.selectedItem.toString()
                )
                jsonValue!!.put("slots", objSlot)
                jsonValue!!.put("service", "Video")
                jsonValue!!.put("automatic_no_show_control", automatic_no_show_control)
                jsonValue!!.put("automatic_no_show_time_limit", automatic_no_show_time_limit)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        for (i in 0 until objSlotOne.length()) {
            val slotOneArray = objSlotOne.getJSONObject(i)
            if (!slotOneArray.get("start_time").toString().equals("", ignoreCase = true)) {

                commonViewModel.commonViewModelCall(url, jsonValue!!, Request.Method.POST).observe(
                    this@SettingsFormActivity
                ) { result ->
                    try {
                        //Process os success response
                        val responseObj = JSONObject(result)
                        dialog.dismiss()
                        if (responseObj.getInt("status_code") == 200) {
                            val response = responseObj.optJSONObject("response")

                            rootObj = response?.optJSONObject("response")
                            if (clinicSetupFlagClick == 1) {
                                doctorService
                            } else {
                                settingServiceSetupDetails
                            }
                            apptStatLayout!!.visibility = View.GONE
                            settingServiceSetupLayout!!.visibility = View.VISIBLE
                            settingInstantVideoLayout!!.visibility = View.GONE
                            settingChaLayout!!.visibility = View.GONE
                            settingVideoLayout!!.visibility = View.GONE
                            videoSettingScheduleLayout!!.visibility = View.GONE
                            videoSettingScrollView!!.visibility = View.GONE
                            nextRecyclerView!!.visibility = View.GONE
                            timeBlockLayout!!.visibility = View.GONE
                            item!!.isVisible = false
                            videoSettingServiceScrollView!!.visibility = View.GONE
                            settingServiceVideoForm!!.visibility = View.GONE
                            videoServiceSettingScheduleLayout!!.visibility = View.GONE


                            settingServiceVideoShortText!!.text.clear();
                            settingServiceVideoDescriptionText!!.text.clear();
                            videoServicePriceInput!!.text.clear();
                            videoServiceAddressText!!.text.clear();
                            advanceServiceBookingText!!.text.clear();
                            appointmentServiceDurationSpinner!!.setSelection(0);
                            videoServiceDurationSpinner!!.setSelection(0);

                        } else {
                            dialog.dismiss()
                            errorHandler(this@SettingsFormActivity, result)
                        }
                    } catch (e: Exception) {
                        dialog.dismiss()
                        e.printStackTrace()
                    }
                }

                slotSetOneCount = 0;
                slotSetTwoCount = 0;
                break;
            } else {
                slotSetOneCount++;
                if (slotSetOneCount == 7) {
                    for (j in 0 until objSlotTwo.length()) {
                        val slotTwoArray = objSlotTwo.getJSONObject(j)
                        if (!slotTwoArray.get("start_time").toString()
                                .equals("", ignoreCase = true)
                        ) {

                            commonViewModel.commonViewModelCall(
                                url,
                                jsonValue!!,
                                Request.Method.POST
                            ).observe(
                                this@SettingsFormActivity
                            ) { result ->
                                try {
                                    //Process os success response
                                    val responseObj = JSONObject(result)
                                    dialog.dismiss()
                                    if (responseObj.getInt("status_code") == 200) {
                                        val response = responseObj.optJSONObject("response")

                                        rootObj = response?.optJSONObject("response")
                                        if (clinicSetupFlagClick == 1) {
                                            doctorService
                                        } else {
                                            settingServiceSetupDetails
                                        }
                                        apptStatLayout!!.visibility = View.GONE
                                        settingServiceSetupLayout!!.visibility = View.VISIBLE
                                        settingInstantVideoLayout!!.visibility = View.GONE
                                        settingChaLayout!!.visibility = View.GONE
                                        settingVideoLayout!!.visibility = View.GONE
                                        videoSettingScheduleLayout!!.visibility = View.GONE
                                        videoSettingScrollView!!.visibility = View.GONE
                                        nextRecyclerView!!.visibility = View.GONE
                                        timeBlockLayout!!.visibility = View.GONE
                                        item!!.isVisible = false
                                        videoSettingServiceScrollView!!.visibility = View.GONE
                                        settingServiceVideoForm!!.visibility = View.GONE
                                        videoServiceSettingScheduleLayout!!.visibility = View.GONE


                                        settingServiceVideoShortText!!.text.clear();
                                        settingServiceVideoDescriptionText!!.text.clear();
                                        videoServicePriceInput!!.text.clear();
                                        videoServiceAddressText!!.text.clear();
                                        advanceServiceBookingText!!.text.clear();
                                        appointmentServiceDurationSpinner!!.setSelection(0);
                                        videoServiceDurationSpinner!!.setSelection(0);

                                    } else {
                                        dialog.dismiss()
                                        errorHandler(this@SettingsFormActivity, result)
                                    }
                                } catch (e: Exception) {
                                    dialog.dismiss()
                                    e.printStackTrace()
                                }
                            }

                            slotSetOneCount = 0;
                            slotSetTwoCount = 0;
                            break;
                        } else {
                            slotSetTwoCount++;
                            if (slotSetTwoCount == 7) {
                                Toast.makeText(
                                    this@SettingsFormActivity,
                                    "Please Click on Edit to set atleast one slot",
                                    Toast.LENGTH_SHORT
                                )
                                    .show()
                                slotSetOneCount = 0;
                                slotSetTwoCount = 0;
                                dialog.dismiss()
                            }
                        }
                    }
                }
            }

        }


//        commonViewModel.commonViewModelCall(url, jsonValue!!, Request.Method.POST).observe(
//            this@SettingsFormActivity
//        ) { result ->
//            try {
//                //Process os success response
//                val responseObj = JSONObject(result)
//                dialog.dismiss()
//                if (responseObj.getInt("status_code") == 200) {
//                    val response = responseObj.optJSONObject("response")
//
//                    rootObj = response?.optJSONObject("response")
//                    if (clinicSetupFlagClick == 1) {
//                        doctorService
//                    } else {
//                        settingServiceSetupDetails
//                    }
//                    apptStatLayout!!.visibility = View.GONE
//                    settingServiceSetupLayout!!.visibility = View.VISIBLE
//                    settingInstantVideoLayout!!.visibility = View.GONE
//                    settingChaLayout!!.visibility = View.GONE
//                    settingVideoLayout!!.visibility = View.GONE
//                    videoSettingScheduleLayout!!.visibility = View.GONE
//                    videoSettingScrollView!!.visibility = View.GONE
//                    nextRecyclerView!!.visibility = View.GONE
//                    timeBlockLayout!!.visibility = View.GONE
//                    item!!.isVisible = false
//                    videoSettingServiceScrollView!!.visibility = View.GONE
//                    settingServiceVideoForm!!.visibility = View.GONE
//                    videoServiceSettingScheduleLayout!!.visibility = View.GONE
//
//
//                    settingServiceVideoShortText!!.text.clear();
//                    settingServiceVideoDescriptionText!!.text.clear();
//                    videoServicePriceInput!!.text.clear();
//                    videoServiceAddressText!!.text.clear();
//                    advanceServiceBookingText!!.text.clear();
//                    appointmentServiceDurationSpinner!!.setSelection(0);
//                    videoServiceDurationSpinner!!.setSelection(0);
//
//                } else {
//                    dialog.dismiss()
//                    errorHandler(this@SettingsFormActivity, result)
//                }
//            } catch (e: Exception) {
//                dialog.dismiss()
//                e.printStackTrace()
//            }
//        }

    }

    fun upDateVideoSettingDetails() {
        showCustomProgressAlertDialog(
            resources.getString(R.string.fetching),
            resources.getString(R.string.wait_while_we_fetching)
        )

        val url = ApiUrls.updateDoctorServiceSetting
        val objSlot = JSONArray()
        val objSlotOne = JSONArray()
        try {
            for (i in 0..6) {
                // 1st object
                if (i == 0) {
                    val list1 = JSONObject()
                    list1.put("id", slotID)
                    list1.put("day", "Monday")
                    list1.put("slot_id", slotID)
                    list1.put("schedule_id", scheduleID)
                    if (!SettingScheduleListAdapter.myArrayMonday[0].equals(
                            "",
                            ignoreCase = true
                        )
                    ) {
                        list1.put("start_time", SettingScheduleListAdapter.myArrayMonday[0] + ":00")
                    } else {
                        list1.put("start_time", "")
                    }
                    if (!SettingScheduleListAdapter.myArrayMonday[1].equals(
                            "",
                            ignoreCase = true
                        )
                    ) {
                        list1.put("end_time", SettingScheduleListAdapter.myArrayMonday[1] + ":00")
                    } else {
                        list1.put("end_time", "")
                    }
                    if (!SettingScheduleListAdapter.myArrayMonday[0].equals("", ignoreCase = true)
                        && !SettingScheduleListAdapter.myArrayMonday[1].equals(
                            "",
                            ignoreCase = true
                        )
                    ) {
                        list1.put("slot_flag", 1)
                    } else {
                        list1.put("slot_flag", 0)
                    }
                    objSlotOne.put(list1)
                } else if (i == 1) {
                    val list1 = JSONObject()
                    list1.put("id", slotID + 1)
                    list1.put("day", "Tuesday")
                    list1.put("slot_id", slotID + 1)
                    list1.put("schedule_id", scheduleID)
                    if (!SettingScheduleListAdapter.myArrayTuesday[0].equals(
                            "",
                            ignoreCase = true
                        )
                    ) {
                        list1.put(
                            "start_time",
                            SettingScheduleListAdapter.myArrayTuesday[0] + ":00"
                        )
                    } else {
                        list1.put("start_time", "")
                    }
                    if (!SettingScheduleListAdapter.myArrayTuesday[1].equals(
                            "",
                            ignoreCase = true
                        )
                    ) {
                        list1.put("end_time", SettingScheduleListAdapter.myArrayTuesday[1] + ":00")
                    } else {
                        list1.put("end_time", "")
                    }
                    if (!SettingScheduleListAdapter.myArrayTuesday[0].equals("", ignoreCase = true)
                        && !SettingScheduleListAdapter.myArrayTuesday[1].equals(
                            "",
                            ignoreCase = true
                        )
                    ) {
                        list1.put("slot_flag", 1)
                    } else {
                        list1.put("slot_flag", 0)
                    }
                    objSlotOne.put(list1)
                } else if (i == 2) {
                    val list1 = JSONObject()
                    list1.put("id", slotID + 2)
                    list1.put("day", "Wednesday")
                    list1.put("slot_id", slotID + 2)
                    list1.put("schedule_id", scheduleID)
                    if (!SettingScheduleListAdapter.myArrayWednesday[0].equals(
                            "",
                            ignoreCase = true
                        )
                    ) {
                        list1.put(
                            "start_time",
                            SettingScheduleListAdapter.myArrayWednesday[0] + ":00"
                        )
                    } else {
                        list1.put("start_time", "")
                    }
                    if (!SettingScheduleListAdapter.myArrayWednesday[1].equals(
                            "",
                            ignoreCase = true
                        )
                    ) {
                        list1.put(
                            "end_time",
                            SettingScheduleListAdapter.myArrayWednesday[1] + ":00"
                        )
                    } else {
                        list1.put("end_time", "")
                    }
                    if (!SettingScheduleListAdapter.myArrayWednesday[0].equals(
                            "",
                            ignoreCase = true
                        )
                        && !SettingScheduleListAdapter.myArrayWednesday[1].equals(
                            "",
                            ignoreCase = true
                        )
                    ) {
                        list1.put("slot_flag", 1)
                    } else {
                        list1.put("slot_flag", 0)
                    }
                    objSlotOne.put(list1)
                } else if (i == 3) {
                    val list1 = JSONObject()
                    list1.put("id", slotID + 3)
                    list1.put("day", "Thursday")
                    list1.put("slot_id", slotID + 3)
                    list1.put("schedule_id", scheduleID)
                    if (!SettingScheduleListAdapter.myArrayThursday[0].equals(
                            "",
                            ignoreCase = true
                        )
                    ) {
                        list1.put(
                            "start_time",
                            SettingScheduleListAdapter.myArrayThursday[0] + ":00"
                        )
                    } else {
                        list1.put("start_time", "")
                    }
                    if (!SettingScheduleListAdapter.myArrayThursday[1].equals(
                            "",
                            ignoreCase = true
                        )
                    ) {
                        list1.put("end_time", SettingScheduleListAdapter.myArrayThursday[1] + ":00")
                    } else {
                        list1.put("end_time", "")
                    }
                    if (!SettingScheduleListAdapter.myArrayThursday[0].equals("", ignoreCase = true)
                        && !SettingScheduleListAdapter.myArrayThursday[1].equals(
                            "",
                            ignoreCase = true
                        )
                    ) {
                        list1.put("slot_flag", 1)
                    } else {
                        list1.put("slot_flag", 0)
                    }
                    objSlotOne.put(list1)
                } else if (i == 4) {
                    val list1 = JSONObject()
                    list1.put("id", slotID + 4)
                    list1.put("day", "Friday")
                    list1.put("slot_id", slotID + 4)
                    list1.put("schedule_id", scheduleID)
                    if (!SettingScheduleListAdapter.myArrayFriday[0].equals(
                            "",
                            ignoreCase = true
                        )
                    ) {
                        list1.put("start_time", SettingScheduleListAdapter.myArrayFriday[0] + ":00")
                    } else {
                        list1.put("start_time", "")
                    }
                    if (!SettingScheduleListAdapter.myArrayFriday[1].equals(
                            "",
                            ignoreCase = true
                        )
                    ) {
                        list1.put("end_time", SettingScheduleListAdapter.myArrayFriday[1] + ":00")
                    } else {
                        list1.put("end_time", "")
                    }
                    if (!SettingScheduleListAdapter.myArrayFriday[0].equals("", ignoreCase = true)
                        && !SettingScheduleListAdapter.myArrayFriday[1].equals(
                            "",
                            ignoreCase = true
                        )
                    ) {
                        list1.put("slot_flag", 1)
                    } else {
                        list1.put("slot_flag", 0)
                    }
                    objSlotOne.put(list1)
                } else if (i == 5) {
                    val list1 = JSONObject()
                    list1.put("id", slotID + 5)
                    list1.put("day", "Saturday")
                    list1.put("slot_id", slotID + 5)
                    list1.put("schedule_id", scheduleID)
                    if (!SettingScheduleListAdapter.myArraySaturday[0].equals(
                            "",
                            ignoreCase = true
                        )
                    ) {
                        list1.put(
                            "start_time",
                            SettingScheduleListAdapter.myArraySaturday[0] + ":00"
                        )
                    } else {
                        list1.put("start_time", "")
                    }
                    if (!SettingScheduleListAdapter.myArraySaturday[1].equals(
                            "",
                            ignoreCase = true
                        )
                    ) {
                        list1.put("end_time", SettingScheduleListAdapter.myArraySaturday[1] + ":00")
                    } else {
                        list1.put("end_time", "")
                    }
                    if (!SettingScheduleListAdapter.myArraySaturday[0].equals("", ignoreCase = true)
                        && !SettingScheduleListAdapter.myArraySaturday[1].equals(
                            "",
                            ignoreCase = true
                        )
                    ) {
                        list1.put("slot_flag", 1)
                    } else {
                        list1.put("slot_flag", 0)
                    }
                    objSlotOne.put(list1)
                } else if (i == 6) {
                    val list1 = JSONObject()
                    list1.put("id", slotID + 6)
                    list1.put("day", "Sunday")
                    list1.put("slot_id", slotID + 6)
                    list1.put("schedule_id", scheduleID)
                    if (!SettingScheduleListAdapter.myArraySunday[0].equals(
                            "",
                            ignoreCase = true
                        )
                    ) {
                        list1.put("start_time", SettingScheduleListAdapter.myArraySunday[0] + ":00")
                    } else {
                        list1.put("start_time", "")
                    }
                    if (!SettingScheduleListAdapter.myArraySunday[1].equals(
                            "",
                            ignoreCase = true
                        )
                    ) {
                        list1.put("end_time", SettingScheduleListAdapter.myArraySunday[1] + ":00")
                    } else {
                        list1.put("end_time", "")
                    }
                    if (!SettingScheduleListAdapter.myArraySunday[0].equals("", ignoreCase = true)
                        && !SettingScheduleListAdapter.myArraySunday[1].equals(
                            "",
                            ignoreCase = true
                        )
                    ) {
                        list1.put("slot_flag", 1)
                    } else {
                        list1.put("slot_flag", 0)
                    }
                    objSlotOne.put(list1)
                }
            }
        } catch (e1: JSONException) {
            e1.printStackTrace()
        }
        val objSlotTwo = JSONArray()
        try {
            for (i in 0..6) {
                // 1st object
                if (i == 0) {
                    val list1 = JSONObject()
                    list1.put("id", slotID + 7)
                    list1.put("day", "Monday")
                    list1.put("slot_id", slotID + 7)
                    list1.put("schedule_id", scheduleID)
                    if (!SettingScheduleListAdapter.myArrayMonday[2].equals(
                            "",
                            ignoreCase = true
                        )
                    ) {
                        list1.put("start_time", SettingScheduleListAdapter.myArrayMonday[2] + ":00")
                    } else {
                        list1.put("start_time", "")
                    }
                    if (!SettingScheduleListAdapter.myArrayMonday[3].equals(
                            "",
                            ignoreCase = true
                        )
                    ) {
                        list1.put("end_time", SettingScheduleListAdapter.myArrayMonday[3] + ":00")
                    } else {
                        list1.put("end_time", "")
                    }
                    if (!SettingScheduleListAdapter.myArrayMonday[2].equals("", ignoreCase = true)
                        && !SettingScheduleListAdapter.myArrayMonday[3].equals(
                            "",
                            ignoreCase = true
                        )
                    ) {
                        list1.put("slot_flag", 1)
                    } else {
                        list1.put("slot_flag", 0)
                    }
                    objSlotTwo.put(list1)
                } else if (i == 1) {
                    val list1 = JSONObject()
                    list1.put("id", slotID + 8)
                    list1.put("day", "Tuesday")
                    list1.put("slot_id", slotID + 8)
                    list1.put("schedule_id", scheduleID)
                    if (!SettingScheduleListAdapter.myArrayTuesday[2].equals(
                            "",
                            ignoreCase = true
                        )
                    ) {
                        list1.put(
                            "start_time",
                            SettingScheduleListAdapter.myArrayTuesday[2] + ":00"
                        )
                    } else {
                        list1.put("start_time", "")
                    }
                    if (!SettingScheduleListAdapter.myArrayTuesday[3].equals(
                            "",
                            ignoreCase = true
                        )
                    ) {
                        list1.put("end_time", SettingScheduleListAdapter.myArrayTuesday[3] + ":00")
                    } else {
                        list1.put("end_time", "")
                    }
                    if (!SettingScheduleListAdapter.myArrayTuesday[2].equals("", ignoreCase = true)
                        && !SettingScheduleListAdapter.myArrayTuesday[3].equals(
                            "",
                            ignoreCase = true
                        )
                    ) {
                        list1.put("slot_flag", 1)
                    } else {
                        list1.put("slot_flag", 0)
                    }
                    objSlotTwo.put(list1)
                } else if (i == 2) {
                    val list1 = JSONObject()
                    list1.put("id", slotID + 9)
                    list1.put("day", "Wednesday")
                    list1.put("slot_id", slotID + 9)
                    list1.put("schedule_id", scheduleID)
                    if (!SettingScheduleListAdapter.myArrayWednesday[2].equals(
                            "",
                            ignoreCase = true
                        )
                    ) {
                        list1.put(
                            "start_time",
                            SettingScheduleListAdapter.myArrayWednesday[2] + ":00"
                        )
                    } else {
                        list1.put("start_time", "")
                    }
                    if (!SettingScheduleListAdapter.myArrayWednesday[3].equals(
                            "",
                            ignoreCase = true
                        )
                    ) {
                        list1.put(
                            "end_time",
                            SettingScheduleListAdapter.myArrayWednesday[3] + ":00"
                        )
                    } else {
                        list1.put("end_time", "")
                    }
                    if (!SettingScheduleListAdapter.myArrayWednesday[2].equals(
                            "",
                            ignoreCase = true
                        )
                        && !SettingScheduleListAdapter.myArrayWednesday[3].equals(
                            "",
                            ignoreCase = true
                        )
                    ) {
                        list1.put("slot_flag", 1)
                    } else {
                        list1.put("slot_flag", 0)
                    }
                    objSlotTwo.put(list1)
                } else if (i == 3) {
                    val list1 = JSONObject()
                    list1.put("id", slotID + 10)
                    list1.put("day", "Thursday")
                    list1.put("slot_id", slotID + 10)
                    list1.put("schedule_id", scheduleID)
                    if (!SettingScheduleListAdapter.myArrayThursday[2].equals(
                            "",
                            ignoreCase = true
                        )
                    ) {
                        list1.put(
                            "start_time",
                            SettingScheduleListAdapter.myArrayThursday[2] + ":00"
                        )
                    } else {
                        list1.put("start_time", "")
                    }
                    if (!SettingScheduleListAdapter.myArrayThursday[3].equals(
                            "",
                            ignoreCase = true
                        )
                    ) {
                        list1.put("end_time", SettingScheduleListAdapter.myArrayThursday[3] + ":00")
                    } else {
                        list1.put("end_time", "")
                    }
                    if (!SettingScheduleListAdapter.myArrayThursday[2].equals("", ignoreCase = true)
                        && !SettingScheduleListAdapter.myArrayThursday[3].equals(
                            "",
                            ignoreCase = true
                        )
                    ) {
                        list1.put("slot_flag", 1)
                    } else {
                        list1.put("slot_flag", 0)
                    }
                    objSlotTwo.put(list1)
                } else if (i == 4) {
                    val list1 = JSONObject()
                    list1.put("id", slotID + 11)
                    list1.put("day", "Friday")
                    list1.put("slot_id", slotID + 11)
                    list1.put("schedule_id", scheduleID)
                    if (!SettingScheduleListAdapter.myArrayFriday[2].equals(
                            "",
                            ignoreCase = true
                        )
                    ) {
                        list1.put("start_time", SettingScheduleListAdapter.myArrayFriday[2] + ":00")
                    } else {
                        list1.put("start_time", "")
                    }
                    if (!SettingScheduleListAdapter.myArrayFriday[3].equals(
                            "",
                            ignoreCase = true
                        )
                    ) {
                        list1.put("end_time", SettingScheduleListAdapter.myArrayFriday[3] + ":00")
                    } else {
                        list1.put("end_time", "")
                    }
                    if (!SettingScheduleListAdapter.myArrayFriday[2].equals("", ignoreCase = true)
                        && !SettingScheduleListAdapter.myArrayFriday[3].equals(
                            "",
                            ignoreCase = true
                        )
                    ) {
                        list1.put("slot_flag", 1)
                    } else {
                        list1.put("slot_flag", 0)
                    }
                    objSlotTwo.put(list1)
                } else if (i == 5) {
                    val list1 = JSONObject()
                    list1.put("id", slotID + 12)
                    list1.put("day", "Saturday")
                    list1.put("slot_id", slotID + 12)
                    list1.put("schedule_id", scheduleID)
                    if (!SettingScheduleListAdapter.myArraySaturday[2].equals(
                            "",
                            ignoreCase = true
                        )
                    ) {
                        list1.put(
                            "start_time",
                            SettingScheduleListAdapter.myArraySaturday[2] + ":00"
                        )
                    } else {
                        list1.put("start_time", "")
                    }
                    if (!SettingScheduleListAdapter.myArraySaturday[3].equals(
                            "",
                            ignoreCase = true
                        )
                    ) {
                        list1.put("end_time", SettingScheduleListAdapter.myArraySaturday[3] + ":00")
                    } else {
                        list1.put("end_time", "")
                    }
                    if (!SettingScheduleListAdapter.myArraySaturday[2].equals("", ignoreCase = true)
                        && !SettingScheduleListAdapter.myArraySaturday[3].equals(
                            "",
                            ignoreCase = true
                        )
                    ) {
                        list1.put("slot_flag", 1)
                    } else {
                        list1.put("slot_flag", 0)
                    }
                    objSlotTwo.put(list1)
                } else if (i == 6) {
                    val list1 = JSONObject()
                    list1.put("id", slotID + 13)
                    list1.put("day", "Sunday")
                    list1.put("slot_id", slotID + 13)
                    list1.put("schedule_id", scheduleID)
                    if (!SettingScheduleListAdapter.myArraySunday[2].equals(
                            "",
                            ignoreCase = true
                        )
                    ) {
                        list1.put("start_time", SettingScheduleListAdapter.myArraySunday[2] + ":00")
                    } else {
                        list1.put("start_time", "")
                    }
                    if (!SettingScheduleListAdapter.myArraySunday[3].equals(
                            "",
                            ignoreCase = true
                        )
                    ) {
                        list1.put("end_time", SettingScheduleListAdapter.myArraySunday[3] + ":00")
                    } else {
                        list1.put("end_time", "")
                    }
                    if (!SettingScheduleListAdapter.myArraySunday[2].equals("", ignoreCase = true)
                        && !SettingScheduleListAdapter.myArraySunday[3].equals(
                            "",
                            ignoreCase = true
                        )
                    ) {
                        list1.put("slot_flag", 1)
                    } else {
                        list1.put("slot_flag", 0)
                    }
                    objSlotTwo.put(list1)
                }
            }
            objSlot.put(0, objSlotOne)
            objSlot.put(1, objSlotTwo)
        } catch (e1: JSONException) {
            e1.printStackTrace()
        }
        try {
            if (selectedSpinnerNoShow.equals("Yes", ignoreCase = true)) {
                automatic_no_show_control = 0
                automatic_no_show_time_limit = no_show_limit_video_et!!.text.toString().toInt()
            } else {
                automatic_no_show_control = 1
                automatic_no_show_time_limit = 0
            }
            jsonValue = JSONObject()
            jsonValue!!.put("alias", settingVideoShortText!!.text.toString())
            jsonValue!!.put("desc", settingVideoDescriptionText!!.text.toString())
            jsonValue!!.put("price", videoPriceInput!!.text.toString().toInt())
            jsonValue!!.put(
                "appt_duration",
                appointmentDurationSpinner!!.selectedItem.toString().toInt()
            )
            jsonValue!!.put("adv_booking", advanceBookingText!!.text.toString().toInt())
            jsonValue!!.put("booking_duration", videoDurationSpinner!!.selectedItem.toString())
            jsonValue!!.put("slots", objSlot)
            jsonValue!!.put("service", "Video")
            jsonValue!!.put("automatic_no_show_control", automatic_no_show_control)
            jsonValue!!.put("automatic_no_show_time_limit", automatic_no_show_time_limit)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        for (i in 0 until objSlotOne.length()) {
            val slotOneArray = objSlotOne.getJSONObject(i)
            if (!slotOneArray.get("start_time").toString().equals("", ignoreCase = true)) {


                commonViewModel.commonViewModelCall(url, jsonValue!!, Request.Method.POST).observe(
                    this@SettingsFormActivity
                ) { result ->
                    try {
                        //Process os success response
                        val responseObj = JSONObject(result)
                        dialog.dismiss()
                        if (responseObj.getInt("status_code") == 200) {
                            val response = responseObj.optJSONObject("response")
                            val root_Obj = response?.optString("response")
                            Toast.makeText(this@SettingsFormActivity, root_Obj, Toast.LENGTH_SHORT)
                                .show()
                            item!!.isVisible = false
                            settingVideoSchedListAdapter!!.notifyDataSetChanged()

                            settingServiceVideoShortText!!.text.clear();
                            settingServiceVideoDescriptionText!!.text.clear();
                            videoServicePriceInput!!.text.clear();
                            videoServiceAddressText!!.text.clear();
                            advanceServiceBookingText!!.text.clear();
                            appointmentServiceDurationSpinner!!.setSelection(0);
                            videoServiceDurationSpinner!!.setSelection(0);

                        } else {
                            dialog.dismiss()
                            errorHandler(this@SettingsFormActivity, result)
                        }
                    } catch (e: Exception) {
                        dialog.dismiss()
                        e.printStackTrace()
                    }
                }


                slotSetOneCount = 0;
                slotSetTwoCount = 0;
                break;
            } else {
                slotSetOneCount++;
                if (slotSetOneCount == 7) {
                    for (j in 0 until objSlotTwo.length()) {
                        val slotTwoArray = objSlotTwo.getJSONObject(j)
                        if (!slotTwoArray.get("start_time").toString()
                                .equals("", ignoreCase = true)
                        ) {


                            commonViewModel.commonViewModelCall(
                                url,
                                jsonValue!!,
                                Request.Method.POST
                            ).observe(
                                this@SettingsFormActivity
                            ) { result ->
                                try {
                                    //Process os success response
                                    val responseObj = JSONObject(result)
                                    dialog.dismiss()
                                    if (responseObj.getInt("status_code") == 200) {
                                        val response = responseObj.optJSONObject("response")
                                        val root_Obj = response?.optString("response")
                                        Toast.makeText(
                                            this@SettingsFormActivity,
                                            root_Obj,
                                            Toast.LENGTH_SHORT
                                        )
                                            .show()
                                        item!!.isVisible = false
                                        settingVideoSchedListAdapter!!.notifyDataSetChanged()

                                        settingServiceVideoShortText!!.text.clear();
                                        settingServiceVideoDescriptionText!!.text.clear();
                                        videoServicePriceInput!!.text.clear();
                                        videoServiceAddressText!!.text.clear();
                                        advanceServiceBookingText!!.text.clear();
                                        appointmentServiceDurationSpinner!!.setSelection(0);
                                        videoServiceDurationSpinner!!.setSelection(0);

                                    } else {
                                        dialog.dismiss()
                                        errorHandler(this@SettingsFormActivity, result)
                                    }
                                } catch (e: Exception) {
                                    dialog.dismiss()
                                    e.printStackTrace()
                                }
                            }


                            slotSetOneCount = 0;
                            slotSetTwoCount = 0;
                            break;
                        } else {
                            slotSetTwoCount++;
                            if (slotSetTwoCount == 7) {
                                Toast.makeText(
                                    this@SettingsFormActivity,
                                    "Please Click on Edit to set atleast one slot",
                                    Toast.LENGTH_SHORT
                                )
                                    .show()
                                slotSetOneCount = 0;
                                slotSetTwoCount = 0;
                                dialog.dismiss()
                            }
                        }
                    }
                }
            }

        }


//        commonViewModel.commonViewModelCall(url, jsonValue!!, Request.Method.POST).observe(
//            this@SettingsFormActivity
//        ) { result ->
//            try {
//                //Process os success response
//                val responseObj = JSONObject(result)
//                dialog.dismiss()
//                if (responseObj.getInt("status_code") == 200) {
//                    val response = responseObj.optJSONObject("response")
//                    val root_Obj = response?.optString("response")
//                    Toast.makeText(this@SettingsFormActivity, root_Obj, Toast.LENGTH_SHORT)
//                        .show()
//                    item!!.isVisible = false
//                    settingVideoSchedListAdapter!!.notifyDataSetChanged()
//
//                    settingServiceVideoShortText!!.text.clear();
//                    settingServiceVideoDescriptionText!!.text.clear();
//                    videoServicePriceInput!!.text.clear();
//                    videoServiceAddressText!!.text.clear();
//                    advanceServiceBookingText!!.text.clear();
//                    appointmentServiceDurationSpinner!!.setSelection(0);
//                    videoServiceDurationSpinner!!.setSelection(0);
//
//                } else {
//                    dialog.dismiss()
//                    errorHandler(this@SettingsFormActivity, result)
//                }
//            } catch (e: Exception) {
//                dialog.dismiss()
//                e.printStackTrace()
//            }
//        }

    }

    fun setupUpVideoSettingService(flagType: Int) {
        showCustomProgressAlertDialog(
            resources.getString(R.string.fetching),
            resources.getString(R.string.wait_while_we_fetching)
        )


        val url = ApiUrls.setupVideoServiceSetting
        val objSlot = JSONArray()
        val objSlotOne = JSONArray()
        try {
            for (i in 0..6) {
                if (i == 0) {
                    val list1 = JSONObject()
                    list1.put("day", "Monday")

                    if (!SettingScheduleListAdapter.myArrayMonday[0].equals(
                            "",
                            ignoreCase = true
                        )
                    ) {
                        list1.put("start_time", SettingScheduleListAdapter.myArrayMonday[0] + ":00")
                    } else {
                        list1.put("start_time", "")
                    }
                    if (!SettingScheduleListAdapter.myArrayMonday[1].equals(
                            "",
                            ignoreCase = true
                        )
                    ) {
                        list1.put("end_time", SettingScheduleListAdapter.myArrayMonday[1] + ":00")
                    } else {
                        list1.put("end_time", "")
                    }
                    objSlotOne.put(list1)
                } else if (i == 1) {
                    val list1 = JSONObject()
                    list1.put("day", "Tuesday")
                    if (!SettingScheduleListAdapter.myArrayTuesday[0].equals(
                            "",
                            ignoreCase = true
                        )
                    ) {
                        list1.put(
                            "start_time",
                            SettingScheduleListAdapter.myArrayTuesday[0] + ":00"
                        )
                    } else {
                        list1.put("start_time", "")
                    }
                    if (!SettingScheduleListAdapter.myArrayTuesday[1].equals(
                            "",
                            ignoreCase = true
                        )
                    ) {
                        list1.put("end_time", SettingScheduleListAdapter.myArrayTuesday[1] + ":00")
                    } else {
                        list1.put("end_time", "")
                    }
                    objSlotOne.put(list1)
                } else if (i == 2) {
                    val list1 = JSONObject()
                    list1.put("day", "Wednesday")
                    if (!SettingScheduleListAdapter.myArrayWednesday[0].equals(
                            "",
                            ignoreCase = true
                        )
                    ) {
                        list1.put(
                            "start_time",
                            SettingScheduleListAdapter.myArrayWednesday[0] + ":00"
                        )
                    } else {
                        list1.put("start_time", "")
                    }
                    if (!SettingScheduleListAdapter.myArrayWednesday[1].equals(
                            "",
                            ignoreCase = true
                        )
                    ) {
                        list1.put(
                            "end_time",
                            SettingScheduleListAdapter.myArrayWednesday[1] + ":00"
                        )
                    } else {
                        list1.put("end_time", "")
                    }
                    objSlotOne.put(list1)
                } else if (i == 3) {
                    val list1 = JSONObject()
                    list1.put("day", "Thursday")

                    if (!SettingScheduleListAdapter.myArrayThursday[0].equals(
                            "",
                            ignoreCase = true
                        )
                    ) {
                        list1.put(
                            "start_time",
                            SettingScheduleListAdapter.myArrayThursday[0] + ":00"
                        )
                    } else {
                        list1.put("start_time", "")
                    }
                    if (!SettingScheduleListAdapter.myArrayThursday[1].equals(
                            "",
                            ignoreCase = true
                        )
                    ) {
                        list1.put("end_time", SettingScheduleListAdapter.myArrayThursday[1] + ":00")
                    } else {
                        list1.put("end_time", "")
                    }
                    objSlotOne.put(list1)
                } else if (i == 4) {
                    val list1 = JSONObject()
                    list1.put("day", "Friday")

                    if (!SettingScheduleListAdapter.myArrayFriday[0].equals(
                            "",
                            ignoreCase = true
                        )
                    ) {
                        list1.put("start_time", SettingScheduleListAdapter.myArrayFriday[0] + ":00")
                    } else {
                        list1.put("start_time", "")
                    }
                    if (!SettingScheduleListAdapter.myArrayFriday[1].equals(
                            "",
                            ignoreCase = true
                        )
                    ) {
                        list1.put("end_time", SettingScheduleListAdapter.myArrayFriday[1] + ":00")
                    } else {
                        list1.put("end_time", "")
                    }
                    objSlotOne.put(list1)
                } else if (i == 5) {
                    val list1 = JSONObject()
                    list1.put("day", "Saturday")

                    if (!SettingScheduleListAdapter.myArraySaturday[0].equals(
                            "",
                            ignoreCase = true
                        )
                    ) {
                        list1.put(
                            "start_time",
                            SettingScheduleListAdapter.myArraySaturday[0] + ":00"
                        )
                    } else {
                        list1.put("start_time", "")
                    }
                    if (!SettingScheduleListAdapter.myArraySaturday[1].equals(
                            "",
                            ignoreCase = true
                        )
                    ) {
                        list1.put("end_time", SettingScheduleListAdapter.myArraySaturday[1] + ":00")
                    } else {
                        list1.put("end_time", "")
                    }
                    objSlotOne.put(list1)
                } else if (i == 6) {
                    val list1 = JSONObject()
                    list1.put("day", "Sunday")

                    if (!SettingScheduleListAdapter.myArraySunday[0].equals(
                            "",
                            ignoreCase = true
                        )
                    ) {
                        list1.put("start_time", SettingScheduleListAdapter.myArraySunday[0] + ":00")
                    } else {
                        list1.put("start_time", "")
                    }
                    if (!SettingScheduleListAdapter.myArraySunday[1].equals(
                            "",
                            ignoreCase = true
                        )
                    ) {
                        list1.put("end_time", SettingScheduleListAdapter.myArraySunday[1] + ":00")
                    } else {
                        list1.put("end_time", "")
                    }
                    objSlotOne.put(list1)
                }
            }
        } catch (e1: JSONException) {
            e1.printStackTrace()
        }
        val objSlotTwo = JSONArray()
        try {
            for (i in 0..6) {
                if (i == 0) {
                    val list1 = JSONObject()
                    list1.put("day", "Monday")


                    if (!SettingScheduleListAdapter.myArrayMonday[2].equals(
                            "",
                            ignoreCase = true
                        )
                    ) {
                        list1.put("start_time", SettingScheduleListAdapter.myArrayMonday[2] + ":00")
                    } else {
                        list1.put("start_time", "")
                    }
                    if (!SettingScheduleListAdapter.myArrayMonday[3].equals(
                            "",
                            ignoreCase = true
                        )
                    ) {
                        list1.put("end_time", SettingScheduleListAdapter.myArrayMonday[3] + ":00")
                    } else {
                        list1.put("end_time", "")
                    }
                    objSlotTwo.put(list1)
                } else if (i == 1) {
                    val list1 = JSONObject()
                    list1.put("day", "Tuesday")
                    if (!SettingScheduleListAdapter.myArrayTuesday[2].equals(
                            "",
                            ignoreCase = true
                        )
                    ) {
                        list1.put(
                            "start_time",
                            SettingScheduleListAdapter.myArrayTuesday[2] + ":00"
                        )
                    } else {
                        list1.put("start_time", "")
                    }
                    if (!SettingScheduleListAdapter.myArrayTuesday[3].equals(
                            "",
                            ignoreCase = true
                        )
                    ) {
                        list1.put("end_time", SettingScheduleListAdapter.myArrayTuesday[3] + ":00")
                    } else {
                        list1.put("end_time", "")
                    }
                    objSlotTwo.put(list1)
                } else if (i == 2) {
                    val list1 = JSONObject()
                    list1.put("day", "Wednesday")
                    if (!SettingScheduleListAdapter.myArrayWednesday[2].equals(
                            "",
                            ignoreCase = true
                        )
                    ) {
                        list1.put(
                            "start_time",
                            SettingScheduleListAdapter.myArrayWednesday[2] + ":00"
                        )
                    } else {
                        list1.put("start_time", "")
                    }
                    if (!SettingScheduleListAdapter.myArrayWednesday[3].equals(
                            "",
                            ignoreCase = true
                        )
                    ) {
                        list1.put(
                            "end_time",
                            SettingScheduleListAdapter.myArrayWednesday[3] + ":00"
                        )
                    } else {
                        list1.put("end_time", "")
                    }
                    objSlotTwo.put(list1)
                } else if (i == 3) {
                    val list1 = JSONObject()
                    list1.put("day", "Thursday")
                    if (!SettingScheduleListAdapter.myArrayThursday[2].equals(
                            "",
                            ignoreCase = true
                        )
                    ) {
                        list1.put(
                            "start_time",
                            SettingScheduleListAdapter.myArrayThursday[2] + ":00"
                        )
                    } else {
                        list1.put("start_time", "")
                    }
                    if (!SettingScheduleListAdapter.myArrayThursday[3].equals(
                            "",
                            ignoreCase = true
                        )
                    ) {
                        list1.put("end_time", SettingScheduleListAdapter.myArrayThursday[3] + ":00")
                    } else {
                        list1.put("end_time", "")
                    }
                    objSlotTwo.put(list1)
                } else if (i == 4) {
                    val list1 = JSONObject()
                    list1.put("day", "Friday")
                    if (!SettingScheduleListAdapter.myArrayFriday[2].equals(
                            "",
                            ignoreCase = true
                        )
                    ) {
                        list1.put("start_time", SettingScheduleListAdapter.myArrayFriday[2] + ":00")
                    } else {
                        list1.put("start_time", "")
                    }
                    if (!SettingScheduleListAdapter.myArrayFriday[3].equals(
                            "",
                            ignoreCase = true
                        )
                    ) {
                        list1.put("end_time", SettingScheduleListAdapter.myArrayFriday[3] + ":00")
                    } else {
                        list1.put("end_time", "")
                    }
                    objSlotTwo.put(list1)
                } else if (i == 5) {
                    val list1 = JSONObject()
                    list1.put("day", "Saturday")
                    if (!SettingScheduleListAdapter.myArraySaturday[2].equals(
                            "",
                            ignoreCase = true
                        )
                    ) {
                        list1.put(
                            "start_time",
                            SettingScheduleListAdapter.myArraySaturday[2] + ":00"
                        )
                    } else {
                        list1.put("start_time", "")
                    }
                    if (!SettingScheduleListAdapter.myArraySaturday[3].equals(
                            "",
                            ignoreCase = true
                        )
                    ) {
                        list1.put("end_time", SettingScheduleListAdapter.myArraySaturday[3] + ":00")
                    } else {
                        list1.put("end_time", "")
                    }
                    objSlotTwo.put(list1)
                } else if (i == 6) {
                    val list1 = JSONObject()
                    list1.put("day", "Sunday")
                    if (!SettingScheduleListAdapter.myArraySunday[2].equals(
                            "",
                            ignoreCase = true
                        )
                    ) {
                        list1.put("start_time", SettingScheduleListAdapter.myArraySunday[2] + ":00")
                    } else {
                        list1.put("start_time", "")
                    }
                    if (!SettingScheduleListAdapter.myArraySunday[3].equals(
                            "",
                            ignoreCase = true
                        )
                    ) {
                        list1.put("end_time", SettingScheduleListAdapter.myArraySunday[3] + ":00")
                    } else {
                        list1.put("end_time", "")
                    }
                    objSlotTwo.put(list1)
                }
            }
            objSlot.put(0, objSlotOne)
            objSlot.put(1, objSlotTwo)
        } catch (e1: JSONException) {
            e1.printStackTrace()
        }
        try {
            if (clinicSetupFlagClick == 2) {
                MyClinicGlobalClass.logUserActionEvent(
                    ApiUrls.doctorId,
                    getString(R.string.ClinicServiceSetUpComplete),
                    null
                )
                jsonValue = JSONObject()
                val clinicArray = JSONArray()
                val clinicObject = JSONObject()
                clinicObject.put("alias", settingServiceVideoShortText!!.text.toString())
                clinicObject.put("desc", settingServiceVideoDescriptionText!!.text.toString())
                clinicObject.put("price", videoServicePriceInput!!.text.toString().toInt())
                clinicObject.put(
                    "appt_duration",
                    appointmentServiceDurationSpinner!!.selectedItem.toString().toInt()
                )
                clinicObject.put("adv_booking", advanceServiceBookingText!!.text.toString().toInt())
                clinicObject.put(
                    "booking_duration",
                    videoServiceDurationSpinner!!.selectedItem.toString()
                )
                clinicObject.put("slots", objSlot)
                clinicObject.put("address", videoServiceAddressText!!.text.toString())
                clinicArray.put(clinicObject)
                jsonValue!!.put("service", "Clinic")
                jsonValue!!.put("clinic", clinicArray)
            } else if (videoSetupFlagClick == 2) {
                MyClinicGlobalClass.logUserActionEvent(
                    ApiUrls.doctorId,
                    getString(R.string.VideoServiceSetUpComplete),
                    null
                )
                if (selectedSpinnerNoShow.equals("Yes", ignoreCase = true)) {
                    automatic_no_show_control = 0
                    automatic_no_show_time_limit =
                        no_show_limit_video_setup_et!!.text.toString().toInt()
                } else {
                    automatic_no_show_control = 1
                    automatic_no_show_time_limit = 0
                }
                jsonValue = JSONObject()
                jsonValue!!.put("alias", settingServiceVideoShortText!!.text.toString())
                jsonValue!!.put("desc", settingServiceVideoDescriptionText!!.text.toString())
                jsonValue!!.put("price", videoServicePriceInput!!.text.toString().toInt())
                jsonValue!!.put(
                    "appt_duration",
                    appointmentServiceDurationSpinner!!.selectedItem.toString().toInt()
                )
                jsonValue!!.put("adv_booking", advanceServiceBookingText!!.text.toString().toInt())
                jsonValue!!.put(
                    "booking_duration",
                    videoServiceDurationSpinner!!.selectedItem.toString()
                )
                jsonValue!!.put("slots", objSlot)
                jsonValue!!.put("service", "Video")
                jsonValue!!.put("automatic_no_show_control", automatic_no_show_control)
                jsonValue!!.put("automatic_no_show_time_limit", automatic_no_show_time_limit)
                videoSetupFlagClick = 0
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }



        for (i in 0 until objSlotOne.length()) {
            val slotOneArray = objSlotOne.getJSONObject(i)
            if (!slotOneArray.get("start_time").toString().equals("", ignoreCase = true)) {


                commonViewModel.commonViewModelCall(url, jsonValue!!, Request.Method.POST).observe(
                    this@SettingsFormActivity
                ) { result ->
                    try {
                        //Process os success response
                        val responseObj = JSONObject(result)
                        dialog.dismiss()
                        if (responseObj.getInt("status_code") == 200) {
                            val response = responseObj.optJSONObject("response")
                            rootObj = response?.optJSONObject("response")
                            if (clinicSetupFlagClick == 2) {
                                doctorService
                                clinicSetupFlagClick = 0
                            } else {
                                settingServiceSetupDetails
                            }
                            apptStatLayout!!.visibility = View.GONE
                            settingServiceSetupLayout!!.visibility = View.VISIBLE
                            settingInstantVideoLayout!!.visibility = View.GONE
                            settingChaLayout!!.visibility = View.GONE
                            settingVideoLayout!!.visibility = View.GONE
                            videoSettingScheduleLayout!!.visibility = View.GONE
                            videoSettingScrollView!!.visibility = View.GONE
                            timeBlockLayout!!.visibility = View.GONE
                            videoSettingServiceScrollView!!.visibility = View.GONE
                            settingServiceVideoForm!!.visibility = View.GONE
                            videoServiceSettingScheduleLayout!!.visibility = View.GONE
                            item!!.isVisible = false
                            settingVideoSchedListAdapter!!.notifyDataSetChanged()

                            settingServiceVideoShortText!!.text.clear();
                            settingServiceVideoDescriptionText!!.text.clear();
                            videoServicePriceInput!!.text.clear();
                            videoServiceAddressText!!.text.clear();
                            advanceServiceBookingText!!.text.clear();
                            appointmentServiceDurationSpinner!!.setSelection(0);
                            videoServiceDurationSpinner!!.setSelection(0);


                        } else {
                            dialog.dismiss()
                            errorHandler(this@SettingsFormActivity, result)
                        }
                    } catch (e: Exception) {
                        dialog.dismiss()
                        e.printStackTrace()
                    }
                }

                slotSetOneCount = 0;
                slotSetTwoCount = 0;
                break;
            } else {
                slotSetOneCount++;
                if (slotSetOneCount == 7) {
                    for (j in 0 until objSlotTwo.length()) {
                        val slotTwoArray = objSlotTwo.getJSONObject(j)
                        if (!slotTwoArray.get("start_time").toString()
                                .equals("", ignoreCase = true)
                        ) {


                            commonViewModel.commonViewModelCall(
                                url,
                                jsonValue!!,
                                Request.Method.POST
                            ).observe(
                                this@SettingsFormActivity
                            ) { result ->
                                try {
                                    //Process os success response
                                    val responseObj = JSONObject(result)
                                    dialog.dismiss()
                                    if (responseObj.getInt("status_code") == 200) {
                                        val response = responseObj.optJSONObject("response")
                                        rootObj = response?.optJSONObject("response")
                                        if (clinicSetupFlagClick == 2) {
                                            doctorService
                                            clinicSetupFlagClick = 0
                                        } else {
                                            settingServiceSetupDetails
                                        }
                                        apptStatLayout!!.visibility = View.GONE
                                        settingServiceSetupLayout!!.visibility = View.VISIBLE
                                        settingInstantVideoLayout!!.visibility = View.GONE
                                        settingChaLayout!!.visibility = View.GONE
                                        settingVideoLayout!!.visibility = View.GONE
                                        videoSettingScheduleLayout!!.visibility = View.GONE
                                        videoSettingScrollView!!.visibility = View.GONE
                                        timeBlockLayout!!.visibility = View.GONE
                                        videoSettingServiceScrollView!!.visibility = View.GONE
                                        settingServiceVideoForm!!.visibility = View.GONE
                                        videoServiceSettingScheduleLayout!!.visibility = View.GONE
                                        item!!.isVisible = false
                                        settingVideoSchedListAdapter!!.notifyDataSetChanged()

                                        settingServiceVideoShortText!!.text.clear();
                                        settingServiceVideoDescriptionText!!.text.clear();
                                        videoServicePriceInput!!.text.clear();
                                        videoServiceAddressText!!.text.clear();
                                        advanceServiceBookingText!!.text.clear();
                                        appointmentServiceDurationSpinner!!.setSelection(0);
                                        videoServiceDurationSpinner!!.setSelection(0);


                                    } else {
                                        dialog.dismiss()
                                        errorHandler(this@SettingsFormActivity, result)
                                    }
                                } catch (e: Exception) {
                                    dialog.dismiss()
                                    e.printStackTrace()
                                }
                            }


                            slotSetOneCount = 0;
                            slotSetTwoCount = 0;
                            break;
                        } else {
                            slotSetTwoCount++;
                            if (slotSetTwoCount == 7) {
                                Toast.makeText(
                                    this@SettingsFormActivity,
                                    "Please Click on Next button to set atleast one slot",
                                    Toast.LENGTH_SHORT
                                )
                                    .show()
                                slotSetOneCount = 0;
                                slotSetTwoCount = 0;
                                dialog.dismiss()


                                if (flagType == 1) {

                                    videoSetupFlagClick = 1
                                    apptStatLayout!!.visibility = View.GONE
                                    settingServiceSetupLayout!!.visibility = View.GONE
                                    settingInstantVideoLayout!!.visibility = View.GONE
                                    settingChaLayout!!.visibility = View.GONE
                                    settingVideoLayout!!.visibility = View.GONE
                                    videoSettingScheduleLayout!!.visibility = View.GONE
                                    videoAddressInputLayout!!.visibility = View.GONE
                                    videoSettingScrollView!!.visibility = View.GONE
                                    timeBlockLayout!!.visibility = View.GONE
                                    videoSettingServiceScrollView!!.visibility = View.VISIBLE
                                    settingServiceVideoForm!!.visibility = View.VISIBLE
                                    settingVideoButton!!.visibility = View.VISIBLE
                                    no_show_lay__video_setup!!.visibility = View.VISIBLE
                                    videoServiceAddressInputLayout!!.visibility = View.GONE
                                    videoServiceSettingScheduleLayout!!.visibility = View.GONE
                                    setupVideoService(1, "Video")
                                } else {
                                    clinicSetupFlagClick = 1
                                    apptStatLayout!!.visibility = View.GONE
                                    settingServiceSetupLayout!!.visibility = View.GONE
                                    settingInstantVideoLayout!!.visibility = View.GONE
                                    settingChaLayout!!.visibility = View.GONE
                                    settingVideoLayout!!.visibility = View.GONE
                                    videoSettingScheduleLayout!!.visibility = View.GONE
                                    videoAddressInputLayout!!.visibility = View.GONE
                                    videoSettingScrollView!!.visibility = View.GONE
                                    timeBlockLayout!!.visibility = View.GONE
                                    videoSettingServiceScrollView!!.visibility = View.VISIBLE
                                    settingServiceVideoForm!!.visibility = View.VISIBLE
                                    videoServiceAddressInputLayout!!.visibility = View.VISIBLE
                                    no_show_lay__video_setup!!.visibility = View.GONE
                                    videoServiceSettingScheduleLayout!!.visibility = View.GONE
                                    item!!.isVisible = false
                                    setupVideoService(3, "Clinic")
                                }
                            }
                        }
                    }
                }
            }

        }


//        commonViewModel.commonViewModelCall(url, jsonValue!!, Request.Method.POST).observe(
//            this@SettingsFormActivity
//        ) { result ->
//            try {
//                //Process os success response
//                val responseObj = JSONObject(result)
//                dialog.dismiss()
//                if (responseObj.getInt("status_code") == 200) {
//                    val response = responseObj.optJSONObject("response")
//                    rootObj = response?.optJSONObject("response")
//                    if (clinicSetupFlagClick == 2) {
//                        doctorService
//                        clinicSetupFlagClick = 0
//                    } else {
//                        settingServiceSetupDetails
//                    }
//                    apptStatLayout!!.visibility = View.GONE
//                    settingServiceSetupLayout!!.visibility = View.VISIBLE
//                    settingInstantVideoLayout!!.visibility = View.GONE
//                    settingChaLayout!!.visibility = View.GONE
//                    settingVideoLayout!!.visibility = View.GONE
//                    videoSettingScheduleLayout!!.visibility = View.GONE
//                    videoSettingScrollView!!.visibility = View.GONE
//                    timeBlockLayout!!.visibility = View.GONE
//                    videoSettingServiceScrollView!!.visibility = View.GONE
//                    settingServiceVideoForm!!.visibility = View.GONE
//                    videoServiceSettingScheduleLayout!!.visibility = View.GONE
//                    item!!.isVisible = false
//                    settingVideoSchedListAdapter!!.notifyDataSetChanged()
//
//                    settingServiceVideoShortText!!.text.clear();
//                    settingServiceVideoDescriptionText!!.text.clear();
//                    videoServicePriceInput!!.text.clear();
//                    videoServiceAddressText!!.text.clear();
//                    advanceServiceBookingText!!.text.clear();
//                    appointmentServiceDurationSpinner!!.setSelection(0);
//                    videoServiceDurationSpinner!!.setSelection(0);
//
//
//                } else {
//                    dialog.dismiss()
//                    errorHandler(this@SettingsFormActivity, result)
//                }
//            } catch (e: Exception) {
//                dialog.dismiss()
//                e.printStackTrace()
//            }
//        }


    }

    fun upDateChatInstantVideoSettingDetails(serviceName: String) {
        showCustomProgressAlertDialog(
            resources.getString(R.string.fetching),
            resources.getString(R.string.wait_while_we_fetching)
        )

        try {
            if (serviceName.equals("Chat", ignoreCase = true)) {
                url = ApiUrls.updateDoctorServiceSetting
                jsonValue = JSONObject()
                jsonValue!!.put("alias", settingChatShortText!!.text.toString())
                jsonValue!!.put("desc", chatDescriptionText!!.text.toString())
                jsonValue!!.put("price", chatPriceText!!.text.toString().toInt())
                jsonValue!!.put("validity", chatValidityEditText!!.text.toString())
                jsonValue!!.put("validity_in", chatValidityDaySpinner!!.selectedItem.toString())
                jsonValue!!.put("service", serviceName)
            } else if (serviceName.equals("Instant Video", ignoreCase = true)) {
                MyClinicGlobalClass.logUserActionEvent(
                    ApiUrls.doctorId,
                    getString(R.string.InstantVideoSetupSetup),
                    null
                )
                if (selectedSpinnerNoShow.equals("Yes", ignoreCase = true)) {
                    automatic_no_show_control = 0
                    automatic_no_show_time_limit =
                        no_show_limit_instant_video_et!!.text.toString().toInt()
                } else {
                    automatic_no_show_control = 1
                    automatic_no_show_time_limit = 0
                }
                url = ApiUrls.updateDoctorPermission
                jsonValue = JSONObject()
                jsonValue!!.put("availfrom", fromDaySpinner!!.selectedItemPosition)
                jsonValue!!.put("availto", toDaySpinner!!.selectedItemPosition)
                jsonValue!!.put("desc", settingDescText!!.text.toString())
                jsonValue!!.put("permission", 1)
                jsonValue!!.put("price", settingPriceText!!.text.toString().toInt())
                if (appUtilities!!.timeFormatPreferences(applicationContext) == 12) {
                    if (toTimeSpinner!!.selectedItem.toString()
                            .equals("00:00 AM", ignoreCase = true)
                    ) {
                        jsonValue!!.put("timeend", "00:00" + ":00")
                    } else {
                        jsonValue!!.put(
                            "timeend",
                            appUtilities!!.changeDateFormat(
                                "hh:mm a",
                                "HH:mm",
                                toTimeSpinner!!.selectedItem.toString()
                            ) + ":00"
                        )
                    }
                    if (fromTimeSpinner!!.selectedItem.toString()
                            .equals("00:00 AM", ignoreCase = true)
                    ) {
                        jsonValue!!.put("timestart", "00:00" + ":00")
                    } else {
                        jsonValue!!.put(
                            "timestart",
                            appUtilities!!.changeDateFormat(
                                "hh:mm a",
                                "HH:mm",
                                fromTimeSpinner!!.selectedItem.toString()
                            ) + ":00"
                        )
                    }
                } else {
                    jsonValue!!.put("timeend", toTimeSpinner!!.selectedItem.toString() + ":00")
                    jsonValue!!.put("timestart", fromTimeSpinner!!.selectedItem.toString() + ":00")
                }
                jsonValue!!.put("automatic_no_show_control", automatic_no_show_control)
                jsonValue!!.put("automatic_no_show_time_limit", automatic_no_show_time_limit)
            }
        } catch (e: JSONException) {
            e.message
        }

        commonViewModel.commonViewModelCall(url, jsonValue!!, Request.Method.POST).observe(
            this@SettingsFormActivity
        ) { result ->
            try {
                //Process os success response
                val responseObj = JSONObject(result)
                dialog.dismiss()
                if (responseObj.getInt("status_code") == 200) {
                    val response = responseObj.optJSONObject("response")
                    val aObj = response?.get("response")
                    if (aObj is Int) {
                        if (aObj.toInt() == 0) {
                            Toast.makeText(
                                applicationContext,
                                "Start time cannot be greater than end time",
                                Toast.LENGTH_LONG
                            ).show()
                        } else if (aObj.toInt() == 2) {
                            Toast.makeText(
                                applicationContext,
                                "Start day cannot be greater than end day",
                                Toast.LENGTH_LONG
                            ).show()
                        } else if (aObj.toInt() == 1) {
                            Toast.makeText(
                                applicationContext,
                                "Service details updated",
                                Toast.LENGTH_LONG
                            ).show()
                        } else {
                            Toast.makeText(
                                applicationContext,
                                "Could not update service details",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    } else {
                        val successMessageString = response?.getString("response")
                        Toast.makeText(
                            this@SettingsFormActivity,
                            successMessageString,
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                } else {
                    errorHandler(this@SettingsFormActivity, result)
                }
            } catch (e: Exception) {
                dialog.dismiss()
                e.printStackTrace()
            }
        }

    }

    fun setupChatInstantVideoSettingDetails(serviceName: String) {
        showCustomProgressAlertDialog(
            resources.getString(R.string.please_wait),
            resources.getString(R.string.wait_while_we_fetching)
        )
        try {
            if (serviceName.equals("Chat", ignoreCase = true)) {
                MyClinicGlobalClass.logUserActionEvent(
                    ApiUrls.doctorId, getString(
                        R.string.ChatServiceSetUpComplete
                    ), null
                )
                url = ApiUrls.setupVideoServiceSetting
                jsonValue = JSONObject()
                jsonValue!!.put("alias", settingChatShortText!!.text.toString())
                jsonValue!!.put("desc", chatDescriptionText!!.text.toString())
                jsonValue!!.put("price", chatPriceText!!.text.toString().toInt())
                jsonValue!!.put("validity", chatValidityEditText!!.text.toString())
                jsonValue!!.put("validity_in", chatValidityDaySpinner!!.selectedItem.toString())
                jsonValue!!.put("service", serviceName)
            } else if (serviceName.equals("Instant Video", ignoreCase = true)) {
                MyClinicGlobalClass.logUserActionEvent(
                    ApiUrls.doctorId, getString(
                        R.string.InstantVideoServiceSetUpComplete
                    ), null
                )
                url = ApiUrls.setupVideoServiceSetting
                if (selectedSpinnerNoShow.equals("Yes", ignoreCase = true)) {
                    automatic_no_show_control = 0
                    automatic_no_show_time_limit =
                        no_show_limit_instant_video_et!!.text.toString().toInt()
                } else {
                    automatic_no_show_control = 1
                    automatic_no_show_time_limit = 0
                }
                jsonValue = JSONObject()
                jsonValue!!.put("start_day", fromDaySpinner!!.selectedItemPosition)
                jsonValue!!.put("end_day", toDaySpinner!!.selectedItemPosition)
                jsonValue!!.put("desc", settingDescText!!.text.toString())
                jsonValue!!.put("service", "Instant Video")
                jsonValue!!.put("price", settingPriceText!!.text.toString().toInt())
                jsonValue!!.put("end_time", toTimeSpinner!!.selectedItem.toString() + ":00")
                jsonValue!!.put("start_time", fromTimeSpinner!!.selectedItem.toString() + ":00")
                jsonValue!!.put("automatic_no_show_control", automatic_no_show_control)
                jsonValue!!.put("automatic_no_show_time_limit", automatic_no_show_time_limit)
            }
        } catch (e: JSONException) {
            e.message
        }

        commonViewModel.commonViewModelCall(url, jsonValue!!, Request.Method.POST).observe(
            this@SettingsFormActivity
        ) { result ->
            try {
                //Process os success response
                val responseObj = JSONObject(result)
                dialog.dismiss()
                if (responseObj.getInt("status_code") == 200) {
                    val response = responseObj.optJSONObject("response")
                    rootObj = response?.optJSONObject("response")
                    settingServiceSetupDetails
                    apptStatLayout!!.visibility = View.GONE
                    settingServiceSetupLayout!!.visibility = View.VISIBLE
                    settingInstantVideoLayout!!.visibility = View.GONE
                    settingChaLayout!!.visibility = View.GONE
                    settingVideoLayout!!.visibility = View.GONE
                    videoSettingScheduleLayout!!.visibility = View.GONE
                    videoSettingScrollView!!.visibility = View.GONE
                    timeBlockLayout!!.visibility = View.GONE

                } else {
                    errorHandler(this@SettingsFormActivity, result)
                }
            } catch (e: Exception) {
                dialog.dismiss()
                e.printStackTrace()
            }
        }

    }

    fun upDateServicePermission(serviceId: Int, active: Boolean) {
        val body = EnableOrDisableBody(active, selectedDrService_ID)
        viewModel!!.isServiceEnableOrDisable(body)
        viewModel!!.status.observe(this, object : Observer<UtilsResource<EnableDisableModel?>?> {
            override fun onChanged(value: UtilsResource<EnableDisableModel?>?) {
                if (value?.status === UtilsResource.Status.SUCCESS) {
                    if (value.data != null) {
                        if (value.data.response == 1) {
                            if (active && serviceId == 3) {
                                settingVideoSchedModelList!!.clear()
                                updateClinicsSettingService
                            } else if (active && serviceId == 1) {
                                settingVideoSchedModelList!!.clear()
                                updateVideoSettingService
                            }
                        }
                    }
                } else if (value?.status === UtilsResource.Status.ERROR) {
                    if (value.message != null) {
                        errorHandler(this@SettingsFormActivity, value.message)
                    }
                }
            }
        })
    }

    fun upDateInstantVideoServicePermission(active: Boolean) {
        val url = ApiUrls.updateInstantVideoServicePermission
        try {
            jsonValue = JSONObject()
            jsonValue!!.put("value", active)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        commonViewModel.commonViewModelCall(url, jsonValue!!, Request.Method.POST).observe(
            this@SettingsFormActivity
        ) { result ->
            try {
                //Process os success response
                val responseObj = JSONObject(result)
                if (responseObj.getInt("status_code") == 200) {
                    val response = responseObj.optJSONObject("response")
                    val myJson = JSONObject(response?.toString())
                    val responseValue = myJson.optInt("response")

                } else {
                    errorHandler(this@SettingsFormActivity, result)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }


    }

    fun upDateClinicSettingDetails2(
        drServiceIdClinicOne: Int,
        drServiceIdClinicTwo: Int, drServiceIdClinicThree: Int, clinicType: String
    ) {
        showCustomProgressAlertDialog(
            resources.getString(R.string.fetching),
            resources.getString(R.string.wait_while_we_fetching)
        )


        val url = ApiUrls.updateDoctorServiceSetting
        val objSlot = JSONArray()
        val objSlotOne = JSONArray()
        val objClinic = JSONArray()
        try {
            for (i in 0..6) {
                // 1st object
                if (i == 0) {
                    val list1 = JSONObject()
                    list1.put("id", slotID)
                    list1.put("day", "Monday")
                    list1.put("slot_id", slotID)
                    list1.put("schedule_id", scheduleID)
                    if (!SettingScheduleListAdapter.myArrayMonday[0].equals(
                            "",
                            ignoreCase = true
                        )
                    ) {
                        list1.put("start_time", SettingScheduleListAdapter.myArrayMonday[0] + ":00")
                    } else {
                        list1.put("start_time", "")
                    }
                    if (!SettingScheduleListAdapter.myArrayMonday[1].equals(
                            "",
                            ignoreCase = true
                        )
                    ) {
                        list1.put("end_time", SettingScheduleListAdapter.myArrayMonday[1] + ":00")
                    } else {
                        list1.put("end_time", "")
                    }
                    if (!SettingScheduleListAdapter.myArrayMonday[0].equals("", ignoreCase = true)
                        && !SettingScheduleListAdapter.myArrayMonday[1].equals(
                            "",
                            ignoreCase = true
                        )
                    ) {
                        list1.put("slot_flag", 1)
                    } else {
                        list1.put("slot_flag", 0)
                    }
                    objSlotOne.put(list1)
                } else if (i == 1) {
                    val list1 = JSONObject()
                    list1.put("id", slotID + 1)
                    list1.put("day", "Tuesday")
                    list1.put("slot_id", slotID + 1)
                    list1.put("schedule_id", scheduleID)
                    if (!SettingScheduleListAdapter.myArrayTuesday[0].equals(
                            "",
                            ignoreCase = true
                        )
                    ) {
                        list1.put(
                            "start_time",
                            SettingScheduleListAdapter.myArrayTuesday[0] + ":00"
                        )
                    } else {
                        list1.put("start_time", "")
                    }
                    if (!SettingScheduleListAdapter.myArrayTuesday[1].equals(
                            "",
                            ignoreCase = true
                        )
                    ) {
                        list1.put("end_time", SettingScheduleListAdapter.myArrayTuesday[1] + ":00")
                    } else {
                        list1.put("end_time", "")
                    }
                    if (!SettingScheduleListAdapter.myArrayTuesday[0].equals("", ignoreCase = true)
                        && !SettingScheduleListAdapter.myArrayTuesday[1].equals(
                            "",
                            ignoreCase = true
                        )
                    ) {
                        list1.put("slot_flag", 1)
                    } else {
                        list1.put("slot_flag", 0)
                    }
                    objSlotOne.put(list1)
                } else if (i == 2) {
                    val list1 = JSONObject()
                    list1.put("id", slotID + 2)
                    list1.put("day", "Wednesday")
                    list1.put("slot_id", slotID + 2)
                    list1.put("schedule_id", scheduleID)
                    if (!SettingScheduleListAdapter.myArrayWednesday[0].equals(
                            "",
                            ignoreCase = true
                        )
                    ) {
                        list1.put(
                            "start_time",
                            SettingScheduleListAdapter.myArrayWednesday[0] + ":00"
                        )
                    } else {
                        list1.put("start_time", "")
                    }
                    if (!SettingScheduleListAdapter.myArrayWednesday[1].equals(
                            "",
                            ignoreCase = true
                        )
                    ) {
                        list1.put(
                            "end_time",
                            SettingScheduleListAdapter.myArrayWednesday[1] + ":00"
                        )
                    } else {
                        list1.put("end_time", "")
                    }
                    if (!SettingScheduleListAdapter.myArrayWednesday[0].equals(
                            "",
                            ignoreCase = true
                        )
                        && !SettingScheduleListAdapter.myArrayWednesday[1].equals(
                            "",
                            ignoreCase = true
                        )
                    ) {
                        list1.put("slot_flag", 1)
                    } else {
                        list1.put("slot_flag", 0)
                    }
                    objSlotOne.put(list1)
                } else if (i == 3) {
                    val list1 = JSONObject()
                    list1.put("id", slotID + 3)
                    list1.put("day", "Thursday")
                    list1.put("slot_id", slotID + 3)
                    list1.put("schedule_id", scheduleID)
                    if (!SettingScheduleListAdapter.myArrayThursday[0].equals(
                            "",
                            ignoreCase = true
                        )
                    ) {
                        list1.put(
                            "start_time",
                            SettingScheduleListAdapter.myArrayThursday[0] + ":00"
                        )
                    } else {
                        list1.put("start_time", "")
                    }
                    if (!SettingScheduleListAdapter.myArrayThursday[1].equals(
                            "",
                            ignoreCase = true
                        )
                    ) {
                        list1.put("end_time", SettingScheduleListAdapter.myArrayThursday[1] + ":00")
                    } else {
                        list1.put("end_time", "")
                    }
                    if (!SettingScheduleListAdapter.myArrayThursday[0].equals("", ignoreCase = true)
                        && !SettingScheduleListAdapter.myArrayThursday[1].equals(
                            "",
                            ignoreCase = true
                        )
                    ) {
                        list1.put("slot_flag", 1)
                    } else {
                        list1.put("slot_flag", 0)
                    }
                    objSlotOne.put(list1)
                } else if (i == 4) {
                    val list1 = JSONObject()
                    list1.put("id", slotID + 4)
                    list1.put("day", "Friday")
                    list1.put("slot_id", slotID + 4)
                    list1.put("schedule_id", scheduleID)
                    if (!SettingScheduleListAdapter.myArrayFriday[0].equals(
                            "",
                            ignoreCase = true
                        )
                    ) {
                        list1.put("start_time", SettingScheduleListAdapter.myArrayFriday[0] + ":00")
                    } else {
                        list1.put("start_time", "")
                    }
                    if (!SettingScheduleListAdapter.myArrayFriday[1].equals(
                            "",
                            ignoreCase = true
                        )
                    ) {
                        list1.put("end_time", SettingScheduleListAdapter.myArrayFriday[1] + ":00")
                    } else {
                        list1.put("end_time", "")
                    }
                    if (!SettingScheduleListAdapter.myArrayFriday[0].equals("", ignoreCase = true)
                        && !SettingScheduleListAdapter.myArrayFriday[1].equals(
                            "",
                            ignoreCase = true
                        )
                    ) {
                        list1.put("slot_flag", 1)
                    } else {
                        list1.put("slot_flag", 0)
                    }
                    objSlotOne.put(list1)
                } else if (i == 5) {
                    val list1 = JSONObject()
                    list1.put("id", slotID + 5)
                    list1.put("day", "Saturday")
                    list1.put("slot_id", slotID + 5)
                    list1.put("schedule_id", scheduleID)
                    if (!SettingScheduleListAdapter.myArraySaturday[0].equals(
                            "",
                            ignoreCase = true
                        )
                    ) {
                        list1.put(
                            "start_time",
                            SettingScheduleListAdapter.myArraySaturday[0] + ":00"
                        )
                    } else {
                        list1.put("start_time", "")
                    }
                    if (!SettingScheduleListAdapter.myArraySaturday[1].equals(
                            "",
                            ignoreCase = true
                        )
                    ) {
                        list1.put("end_time", SettingScheduleListAdapter.myArraySaturday[1] + ":00")
                    } else {
                        list1.put("end_time", "")
                    }
                    if (!SettingScheduleListAdapter.myArraySaturday[0].equals("", ignoreCase = true)
                        && !SettingScheduleListAdapter.myArraySaturday[1].equals(
                            "",
                            ignoreCase = true
                        )
                    ) {
                        list1.put("slot_flag", 1)
                    } else {
                        list1.put("slot_flag", 0)
                    }
                    objSlotOne.put(list1)
                } else if (i == 6) {
                    val list1 = JSONObject()
                    list1.put("id", slotID + 6)
                    list1.put("day", "Sunday")
                    list1.put("slot_id", slotID + 6)
                    list1.put("schedule_id", scheduleID)
                    if (!SettingScheduleListAdapter.myArraySunday[0].equals(
                            "",
                            ignoreCase = true
                        )
                    ) {
                        list1.put("start_time", SettingScheduleListAdapter.myArraySunday[0] + ":00")
                    } else {
                        list1.put("start_time", "")
                    }
                    if (!SettingScheduleListAdapter.myArraySunday[1].equals(
                            "",
                            ignoreCase = true
                        )
                    ) {
                        list1.put("end_time", SettingScheduleListAdapter.myArraySunday[1] + ":00")
                    } else {
                        list1.put("end_time", "")
                    }
                    if (!SettingScheduleListAdapter.myArraySunday[0].equals("", ignoreCase = true)
                        && !SettingScheduleListAdapter.myArraySunday[1].equals(
                            "",
                            ignoreCase = true
                        )
                    ) {
                        list1.put("slot_flag", 1)
                    } else {
                        list1.put("slot_flag", 0)
                    }
                    objSlotOne.put(list1)
                }
            }
        } catch (e1: JSONException) {
            // TODO Auto-generated catch block
            e1.printStackTrace()
        }
        val objSlotTwo = JSONArray()
        try {
            for (i in 0..6) {
                // 1st object
                if (i == 0) {
                    val list1 = JSONObject()
                    list1.put("id", slotID + 7)
                    list1.put("day", "Monday")
                    list1.put("slot_id", slotID + 7)
                    list1.put("schedule_id", scheduleID)
                    if (!SettingScheduleListAdapter.myArrayMonday[2].equals(
                            "",
                            ignoreCase = true
                        )
                    ) {
                        list1.put("start_time", SettingScheduleListAdapter.myArrayMonday[2] + ":00")
                    } else {
                        list1.put("start_time", "")
                    }
                    if (!SettingScheduleListAdapter.myArrayMonday[3].equals(
                            "",
                            ignoreCase = true
                        )
                    ) {
                        list1.put("end_time", SettingScheduleListAdapter.myArrayMonday[3] + ":00")
                    } else {
                        list1.put("end_time", "")
                    }
                    if (!SettingScheduleListAdapter.myArrayMonday[2].equals("", ignoreCase = true)
                        && !SettingScheduleListAdapter.myArrayMonday[3].equals(
                            "",
                            ignoreCase = true
                        )
                    ) {
                        list1.put("slot_flag", 1)
                    } else {
                        list1.put("slot_flag", 0)
                    }
                    objSlotTwo.put(list1)
                } else if (i == 1) {
                    val list1 = JSONObject()
                    list1.put("id", slotID + 8)
                    list1.put("day", "Tuesday")
                    list1.put("slot_id", slotID + 8)
                    list1.put("schedule_id", scheduleID)
                    if (!SettingScheduleListAdapter.myArrayTuesday[2].equals(
                            "",
                            ignoreCase = true
                        )
                    ) {
                        list1.put(
                            "start_time",
                            SettingScheduleListAdapter.myArrayTuesday[2] + ":00"
                        )
                    } else {
                        list1.put("start_time", "")
                    }
                    if (!SettingScheduleListAdapter.myArrayTuesday[3].equals(
                            "",
                            ignoreCase = true
                        )
                    ) {
                        list1.put("end_time", SettingScheduleListAdapter.myArrayTuesday[3] + ":00")
                    } else {
                        list1.put("end_time", "")
                    }
                    if (!SettingScheduleListAdapter.myArrayTuesday[2].equals("", ignoreCase = true)
                        && !SettingScheduleListAdapter.myArrayTuesday[3].equals(
                            "",
                            ignoreCase = true
                        )
                    ) {
                        list1.put("slot_flag", 1)
                    } else {
                        list1.put("slot_flag", 0)
                    }
                    objSlotTwo.put(list1)
                } else if (i == 2) {
                    val list1 = JSONObject()
                    list1.put("id", slotID + 9)
                    list1.put("day", "Wednesday")
                    list1.put("slot_id", slotID + 9)
                    list1.put("schedule_id", scheduleID)
                    if (!SettingScheduleListAdapter.myArrayWednesday[2].equals(
                            "",
                            ignoreCase = true
                        )
                    ) {
                        list1.put(
                            "start_time",
                            SettingScheduleListAdapter.myArrayWednesday[2] + ":00"
                        )
                    } else {
                        list1.put("start_time", "")
                    }
                    if (!SettingScheduleListAdapter.myArrayWednesday[3].equals(
                            "",
                            ignoreCase = true
                        )
                    ) {
                        list1.put(
                            "end_time",
                            SettingScheduleListAdapter.myArrayWednesday[3] + ":00"
                        )
                    } else {
                        list1.put("end_time", "")
                    }
                    if (!SettingScheduleListAdapter.myArrayWednesday[2].equals(
                            "",
                            ignoreCase = true
                        )
                        && !SettingScheduleListAdapter.myArrayWednesday[3].equals(
                            "",
                            ignoreCase = true
                        )
                    ) {
                        list1.put("slot_flag", 1)
                    } else {
                        list1.put("slot_flag", 0)
                    }
                    objSlotTwo.put(list1)
                } else if (i == 3) {
                    val list1 = JSONObject()
                    list1.put("id", slotID + 10)
                    list1.put("day", "Thursday")
                    list1.put("slot_id", slotID + 10)
                    list1.put("schedule_id", scheduleID)
                    if (!SettingScheduleListAdapter.myArrayThursday[2].equals(
                            "",
                            ignoreCase = true
                        )
                    ) {
                        list1.put(
                            "start_time",
                            SettingScheduleListAdapter.myArrayThursday[2] + ":00"
                        )
                    } else {
                        list1.put("start_time", "")
                    }
                    if (!SettingScheduleListAdapter.myArrayThursday[3].equals(
                            "",
                            ignoreCase = true
                        )
                    ) {
                        list1.put("end_time", SettingScheduleListAdapter.myArrayThursday[3] + ":00")
                    } else {
                        list1.put("end_time", "")
                    }
                    if (!SettingScheduleListAdapter.myArrayThursday[2].equals("", ignoreCase = true)
                        && !SettingScheduleListAdapter.myArrayThursday[3].equals(
                            "",
                            ignoreCase = true
                        )
                    ) {
                        list1.put("slot_flag", 1)
                    } else {
                        list1.put("slot_flag", 0)
                    }
                    objSlotTwo.put(list1)
                } else if (i == 4) {
                    val list1 = JSONObject()
                    list1.put("id", slotID + 11)
                    list1.put("day", "Friday")
                    list1.put("slot_id", slotID + 11)
                    list1.put("schedule_id", scheduleID)
                    if (!SettingScheduleListAdapter.myArrayFriday[2].equals(
                            "",
                            ignoreCase = true
                        )
                    ) {
                        list1.put("start_time", SettingScheduleListAdapter.myArrayFriday[2] + ":00")
                    } else {
                        list1.put("start_time", "")
                    }
                    if (!SettingScheduleListAdapter.myArrayFriday[3].equals(
                            "",
                            ignoreCase = true
                        )
                    ) {
                        list1.put("end_time", SettingScheduleListAdapter.myArrayFriday[3] + ":00")
                    } else {
                        list1.put("end_time", "")
                    }
                    if (!SettingScheduleListAdapter.myArrayFriday[2].equals("", ignoreCase = true)
                        && !SettingScheduleListAdapter.myArrayFriday[3].equals(
                            "",
                            ignoreCase = true
                        )
                    ) {
                        list1.put("slot_flag", 1)
                    } else {
                        list1.put("slot_flag", 0)
                    }
                    objSlotTwo.put(list1)
                } else if (i == 5) {
                    val list1 = JSONObject()
                    list1.put("id", slotID + 12)
                    list1.put("day", "Saturday")
                    list1.put("slot_id", slotID + 12)
                    list1.put("schedule_id", scheduleID)
                    if (!SettingScheduleListAdapter.myArraySaturday[2].equals(
                            "",
                            ignoreCase = true
                        )
                    ) {
                        list1.put(
                            "start_time",
                            SettingScheduleListAdapter.myArraySaturday[2] + ":00"
                        )
                    } else {
                        list1.put("start_time", "")
                    }
                    if (!SettingScheduleListAdapter.myArraySaturday[3].equals(
                            "",
                            ignoreCase = true
                        )
                    ) {
                        list1.put("end_time", SettingScheduleListAdapter.myArraySaturday[3] + ":00")
                    } else {
                        list1.put("end_time", "")
                    }
                    if (!SettingScheduleListAdapter.myArraySaturday[2].equals("", ignoreCase = true)
                        && !SettingScheduleListAdapter.myArraySaturday[3].equals(
                            "",
                            ignoreCase = true
                        )
                    ) {
                        list1.put("slot_flag", 1)
                    } else {
                        list1.put("slot_flag", 0)
                    }
                    objSlotTwo.put(list1)
                } else if (i == 6) {
                    val list1 = JSONObject()
                    list1.put("id", slotID + 13)
                    list1.put("day", "Sunday")
                    list1.put("slot_id", slotID + 13)
                    list1.put("schedule_id", scheduleID)
                    if (!SettingScheduleListAdapter.myArraySunday[2].equals(
                            "",
                            ignoreCase = true
                        )
                    ) {
                        list1.put("start_time", SettingScheduleListAdapter.myArraySunday[2] + ":00")
                    } else {
                        list1.put("start_time", "")
                    }
                    if (!SettingScheduleListAdapter.myArraySunday[3].equals(
                            "",
                            ignoreCase = true
                        )
                    ) {
                        list1.put("end_time", SettingScheduleListAdapter.myArraySunday[3] + ":00")
                    } else {
                        list1.put("end_time", "")
                    }
                    if (!SettingScheduleListAdapter.myArraySunday[2].equals("", ignoreCase = true)
                        && !SettingScheduleListAdapter.myArraySunday[3].equals(
                            "",
                            ignoreCase = true
                        )
                    ) {
                        list1.put("slot_flag", 1)
                    } else {
                        list1.put("slot_flag", 0)
                    }
                    objSlotTwo.put(list1)
                }
            }
            objSlot.put(0, objSlotOne)
            objSlot.put(1, objSlotTwo)
        } catch (e1: JSONException) {
            e1.printStackTrace()
        }
        try {
            jsonValue = JSONObject()
            jsonValue1 = JSONObject()
            val price = 0
            val description = ""
            val address = ""
            val appointmentDuration = 0
            val days = 0
            val hours = 0
            val minutes = 0
            if (clinicType.equals("Clinic 1", ignoreCase = true)) {

                jsonValue = JSONObject()
                jsonValue!!.put("dr_service_id", drServiceIdClinicOne)
                jsonValue!!.put("address", videoAddressText!!.text.toString())
                jsonValue!!.put("alias", settingVideoShortText!!.text.toString())
                jsonValue!!.put("desc", settingVideoDescriptionText!!.text.toString())
                jsonValue!!.put("price", videoPriceInput!!.text.toString().toInt())
                jsonValue!!.put(
                    "appt_duration",
                    appointmentDurationSpinner!!.selectedItem.toString().toInt()
                )
                jsonValue!!.put("adv_booking", advanceBookingText!!.text.toString().toInt())
                jsonValue!!.put("booking_duration", videoDurationSpinner!!.selectedItem.toString())
                jsonValue!!.put("slots", objSlot)
                objClinic.put(jsonValue)
            } else if (clinicType.equals("Clinic 2", ignoreCase = true)) {

                jsonValue = JSONObject()
                jsonValue!!.put("dr_service_id", drServiceIdClinicTwo)
                jsonValue!!.put("address", videoAddressText!!.text.toString())
                jsonValue!!.put("alias", settingVideoShortText!!.text.toString())
                jsonValue!!.put("desc", settingVideoDescriptionText!!.text.toString())
                jsonValue!!.put("price", videoPriceInput!!.text.toString().toInt())
                jsonValue!!.put(
                    "appt_duration",
                    appointmentDurationSpinner!!.selectedItem.toString().toInt()
                )
                jsonValue!!.put("adv_booking", advanceBookingText!!.text.toString().toInt())
                jsonValue!!.put("booking_duration", videoDurationSpinner!!.selectedItem.toString())
                jsonValue!!.put("slots", objSlot)
                objClinic.put(jsonValue)
            } else if (clinicType.equals("Clinic 3", ignoreCase = true)) {
                jsonValue = JSONObject()
                jsonValue!!.put("dr_service_id", drServiceIdClinicThree)
                jsonValue!!.put("alias", settingVideoShortText!!.text.toString())
                jsonValue!!.put("address", videoAddressText!!.text.toString())
                jsonValue!!.put("desc", settingVideoDescriptionText!!.text.toString())
                jsonValue!!.put("price", videoPriceInput!!.text.toString().toInt())
                jsonValue!!.put(
                    "appt_duration",
                    appointmentDurationSpinner!!.selectedItem.toString().toInt()
                )
                jsonValue!!.put("adv_booking", advanceBookingText!!.text.toString().toInt())
                jsonValue!!.put("booking_duration", videoDurationSpinner!!.selectedItem.toString())
                jsonValue!!.put("slots", objSlot)
                objClinic.put(jsonValue)
            }
            jsonValue1!!.put("clinic", objClinic)
            jsonValue1!!.put("service", "Clinic")
        } catch (e: Exception) {
            e.printStackTrace()
        }



        for (i in 0 until objSlotOne.length()) {
            val slotOneArray = objSlotOne.getJSONObject(i)
            if (!slotOneArray.get("start_time").toString().equals("", ignoreCase = true)) {


                commonViewModel.commonViewModelCall(url, jsonValue1!!, Request.Method.POST).observe(
                    this@SettingsFormActivity
                ) { result ->
                    try {
                        //Process os success response
                        val responseObj = JSONObject(result)
                        dialog.dismiss()
                        if (responseObj.getInt("status_code") == 200) {
                            val response = responseObj.optJSONObject("response")
                            val successMessageString = response.getString("response")
                            Toast.makeText(
                                this@SettingsFormActivity,
                                successMessageString,
                                Toast.LENGTH_SHORT
                            ).show()
                            settingVideoSchedListAdapter!!.notifyDataSetChanged()

                        } else {
                            errorHandler(this@SettingsFormActivity, result)
                        }
                    } catch (e: Exception) {
                        dialog.dismiss()
                        e.printStackTrace()
                    }
                }


                slotSetOneCount = 0;
                slotSetTwoCount = 0;
                break;
            } else {
                slotSetOneCount++;
                if (slotSetOneCount == 7) {
                    for (j in 0 until objSlotTwo.length()) {
                        val slotTwoArray = objSlotTwo.getJSONObject(j)
                        if (!slotTwoArray.get("start_time").toString()
                                .equals("", ignoreCase = true)
                        ) {


                            commonViewModel.commonViewModelCall(
                                url,
                                jsonValue1!!,
                                Request.Method.POST
                            ).observe(
                                this@SettingsFormActivity
                            ) { result ->
                                try {
                                    //Process os success response
                                    val responseObj = JSONObject(result)
                                    dialog.dismiss()
                                    if (responseObj.getInt("status_code") == 200) {
                                        val response = responseObj.optJSONObject("response")
                                        val successMessageString = response.getString("response")
                                        Toast.makeText(
                                            this@SettingsFormActivity,
                                            successMessageString,
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        settingVideoSchedListAdapter!!.notifyDataSetChanged()

                                    } else {
                                        errorHandler(this@SettingsFormActivity, result)
                                    }
                                } catch (e: Exception) {
                                    dialog.dismiss()
                                    e.printStackTrace()
                                }
                            }


                            slotSetOneCount = 0;
                            slotSetTwoCount = 0;
                            break;
                        } else {
                            slotSetTwoCount++;
                            if (slotSetTwoCount == 7) {
                                Toast.makeText(
                                    this@SettingsFormActivity,
                                    "Please Click on Edit to set atleast one slot",
                                    Toast.LENGTH_SHORT
                                )
                                    .show()
                                slotSetOneCount = 0;
                                slotSetTwoCount = 0;
                                dialog.dismiss()
                            }
                        }
                    }
                }
            }

        }


//        commonViewModel.commonViewModelCall(url, jsonValue1!!, Request.Method.POST).observe(
//            this@SettingsFormActivity
//        ) { result ->
//            try {
//                //Process os success response
//                val responseObj = JSONObject(result)
//                dialog.dismiss()
//                if (responseObj.getInt("status_code") == 200) {
//                    val response = responseObj.optJSONObject("response")
//                    val successMessageString = response.getString("response")
//                    Toast.makeText(
//                        this@SettingsFormActivity,
//                        successMessageString,
//                        Toast.LENGTH_SHORT
//                    ).show()
//                    settingVideoSchedListAdapter!!.notifyDataSetChanged()
//
//                } else {
//                    errorHandler(this@SettingsFormActivity, result)
//                }
//            } catch (e: Exception) {
//                dialog.dismiss()
//                e.printStackTrace()
//            }
//        }

    }

    fun upDateClinicSettingDetailsThree(
        drServiceIdClinicOne: Int,
        drServiceIdClinicTwo: Int, drServiceIdClinicThree: Int, clinicType: String?
    ) {
        showCustomProgressAlertDialog(
            resources.getString(R.string.fetching),
            resources.getString(R.string.wait_while_we_fetching)
        )

        val url = ApiUrls.updateDoctorServiceSetting
        val objSlot = JSONArray()
        val objSlotOne = JSONArray()
        val objClinic = JSONArray()
        var slotOneSpinnerOne = ""
        var slotOneSpinnerTwo = ""
        try {
            for (i in 1..7) {
                // 1st object
                if (i == 1) {
                    val list1 = JSONObject()
                    list1.put(
                        "id",
                        SettingVideoScheduleListAdapter.settingVideoSchedModelList[i].idOne
                    )
                    list1.put("day", "Monday")
                    list1.put(
                        "slot_id",
                        SettingVideoScheduleListAdapter.settingVideoSchedModelList[i].slotIdOne
                    )
                    list1.put(
                        "schedule_id",
                        SettingVideoScheduleListAdapter.settingVideoSchedModelList[i].scheduleIdOne
                    )
                    slotOneSpinnerOne = ""
                    slotOneSpinnerTwo = ""
                    val currentString =
                        SettingVideoScheduleListAdapter.settingVideoSchedModelList[i].slotOneTime.toString()
                    if (!currentString.equals("", ignoreCase = true)) {
                        val separated =
                            currentString.split("-".toRegex()).dropLastWhile { it.isEmpty() }
                                .toTypedArray()
                        slotOneSpinnerOne = separated[0] // this will contain "Fruit"
                        slotOneSpinnerTwo = separated[1] // this will contain " they taste good"
                    }
                    if (slotOneSpinnerOne.equals("00:00:00", ignoreCase = true) &&
                        slotOneSpinnerTwo.equals("00:00:00", ignoreCase = true)
                    ) {
                        list1.put("start_time", "")
                        list1.put("end_time", "")
                    } else {
                        list1.put("start_time", slotOneSpinnerOne)
                        list1.put("end_time", slotOneSpinnerTwo)
                    }
                    list1.put(
                        "slot_flag",
                        SettingVideoScheduleListAdapter.settingVideoSchedModelList[i].slotFlagIdOne
                    )
                    objSlotOne.put(list1)
                } else if (i == 2) {
                    val list1 = JSONObject()
                    list1.put(
                        "id",
                        SettingVideoScheduleListAdapter.settingVideoSchedModelList[i].idOne
                    )
                    list1.put("day", "Tuesday")
                    list1.put(
                        "slot_id",
                        SettingVideoScheduleListAdapter.settingVideoSchedModelList[i].slotIdOne
                    )
                    list1.put(
                        "schedule_id",
                        SettingVideoScheduleListAdapter.settingVideoSchedModelList[i].scheduleIdOne
                    )
                    slotOneSpinnerOne = ""
                    slotOneSpinnerTwo = ""
                    val currentString =
                        SettingVideoScheduleListAdapter.settingVideoSchedModelList[i].slotOneTime.toString()
                    if (!currentString.equals("", ignoreCase = true)) {
                        val separated =
                            currentString.split("-".toRegex()).dropLastWhile { it.isEmpty() }
                                .toTypedArray()
                        slotOneSpinnerOne = separated[0]
                        slotOneSpinnerTwo = separated[1]
                    }
                    if (slotOneSpinnerOne.equals("00:00:00", ignoreCase = true) &&
                        slotOneSpinnerTwo.equals("00:00:00", ignoreCase = true)
                    ) {
                        list1.put("start_time", "")
                        list1.put("end_time", "")
                    } else {
                        list1.put("start_time", slotOneSpinnerOne)
                        list1.put("end_time", slotOneSpinnerTwo)
                    }
                    list1.put(
                        "slot_flag",
                        SettingVideoScheduleListAdapter.settingVideoSchedModelList[i].slotFlagIdOne
                    )
                    objSlotOne.put(list1)
                } else if (i == 3) {
                    val list1 = JSONObject()
                    list1.put(
                        "id",
                        SettingVideoScheduleListAdapter.settingVideoSchedModelList[i].idOne
                    )
                    list1.put("day", "Wednesday")
                    list1.put(
                        "slot_id",
                        SettingVideoScheduleListAdapter.settingVideoSchedModelList[i].slotIdOne
                    )
                    list1.put(
                        "schedule_id",
                        SettingVideoScheduleListAdapter.settingVideoSchedModelList[i].scheduleIdOne
                    )
                    slotOneSpinnerOne = ""
                    slotOneSpinnerTwo = ""
                    val currentString =
                        SettingVideoScheduleListAdapter.settingVideoSchedModelList[i].slotOneTime.toString()
                    if (!currentString.equals("", ignoreCase = true)) {
                        val separated =
                            currentString.split("-".toRegex()).dropLastWhile { it.isEmpty() }
                                .toTypedArray()
                        slotOneSpinnerOne = separated[0]
                        slotOneSpinnerTwo = separated[1]
                    }
                    if (slotOneSpinnerOne.equals("00:00:00", ignoreCase = true) &&
                        slotOneSpinnerTwo.equals("00:00:00", ignoreCase = true)
                    ) {
                        list1.put("start_time", "")
                        list1.put("end_time", "")
                    } else {
                        list1.put("start_time", slotOneSpinnerOne)
                        list1.put("end_time", slotOneSpinnerTwo)
                    }
                    list1.put(
                        "slot_flag",
                        SettingVideoScheduleListAdapter.settingVideoSchedModelList[i].slotFlagIdOne
                    )
                    objSlotOne.put(list1)
                } else if (i == 4) {
                    val list1 = JSONObject()
                    list1.put(
                        "id",
                        SettingVideoScheduleListAdapter.settingVideoSchedModelList[i].idOne
                    )
                    list1.put("day", "Thursday")
                    list1.put(
                        "slot_id",
                        SettingVideoScheduleListAdapter.settingVideoSchedModelList[i].slotIdOne
                    )
                    list1.put(
                        "schedule_id",
                        SettingVideoScheduleListAdapter.settingVideoSchedModelList[i].scheduleIdOne
                    )
                    slotOneSpinnerOne = ""
                    slotOneSpinnerTwo = ""
                    val currentString =
                        SettingVideoScheduleListAdapter.settingVideoSchedModelList[i].slotOneTime.toString()
                    if (!currentString.equals("", ignoreCase = true)) {
                        val separated =
                            currentString.split("-".toRegex()).dropLastWhile { it.isEmpty() }
                                .toTypedArray()
                        slotOneSpinnerOne = separated[0] // this will contain "Fruit"
                        slotOneSpinnerTwo = separated[1] // this will contain " they taste good"
                    }
                    if (slotOneSpinnerOne.equals("00:00:00", ignoreCase = true) &&
                        slotOneSpinnerTwo.equals("00:00:00", ignoreCase = true)
                    ) {
                        list1.put("start_time", "")
                        list1.put("end_time", "")
                    } else {
                        list1.put("start_time", slotOneSpinnerOne)
                        list1.put("end_time", slotOneSpinnerTwo)
                    }
                    list1.put(
                        "slot_flag",
                        SettingVideoScheduleListAdapter.settingVideoSchedModelList[i].slotFlagIdOne
                    )
                    objSlotOne.put(list1)
                } else if (i == 5) {
                    val list1 = JSONObject()
                    list1.put(
                        "id",
                        SettingVideoScheduleListAdapter.settingVideoSchedModelList[i].idOne
                    )
                    list1.put("day", "Friday")
                    list1.put(
                        "slot_id",
                        SettingVideoScheduleListAdapter.settingVideoSchedModelList[i].slotIdOne
                    )
                    list1.put(
                        "schedule_id",
                        SettingVideoScheduleListAdapter.settingVideoSchedModelList[i].scheduleIdOne
                    )
                    slotOneSpinnerOne = ""
                    slotOneSpinnerTwo = ""
                    val currentString =
                        SettingVideoScheduleListAdapter.settingVideoSchedModelList[i].slotOneTime.toString()
                    if (!currentString.equals("", ignoreCase = true)) {
                        val separated =
                            currentString.split("-".toRegex()).dropLastWhile { it.isEmpty() }
                                .toTypedArray()
                        slotOneSpinnerOne = separated[0] // this will contain "Fruit"
                        slotOneSpinnerTwo = separated[1] // this will contain " they taste good"
                    }
                    if (slotOneSpinnerOne.equals("00:00:00", ignoreCase = true) &&
                        slotOneSpinnerTwo.equals("00:00:00", ignoreCase = true)
                    ) {
                        list1.put("start_time", "")
                        list1.put("end_time", "")
                    } else {
                        list1.put("start_time", slotOneSpinnerOne)
                        list1.put("end_time", slotOneSpinnerTwo)
                    }
                    list1.put(
                        "slot_flag",
                        SettingVideoScheduleListAdapter.settingVideoSchedModelList[i].slotFlagIdOne
                    )
                    objSlotOne.put(list1)
                } else if (i == 6) {
                    val list1 = JSONObject()
                    list1.put(
                        "id",
                        SettingVideoScheduleListAdapter.settingVideoSchedModelList[i].idOne
                    )
                    list1.put("day", "Saturday")
                    list1.put(
                        "slot_id",
                        SettingVideoScheduleListAdapter.settingVideoSchedModelList[i].slotIdOne
                    )
                    list1.put(
                        "schedule_id",
                        SettingVideoScheduleListAdapter.settingVideoSchedModelList[i].scheduleIdOne
                    )
                    slotOneSpinnerOne = ""
                    slotOneSpinnerTwo = ""
                    val currentString =
                        SettingVideoScheduleListAdapter.settingVideoSchedModelList[i].slotOneTime.toString()
                    if (!currentString.equals("", ignoreCase = true)) {
                        val separated =
                            currentString.split("-".toRegex()).dropLastWhile { it.isEmpty() }
                                .toTypedArray()
                        slotOneSpinnerOne = separated[0] // this will contain "Fruit"
                        slotOneSpinnerTwo = separated[1] // this will contain " they taste good"
                    }
                    if (slotOneSpinnerOne.equals("00:00:00", ignoreCase = true) &&
                        slotOneSpinnerTwo.equals("00:00:00", ignoreCase = true)
                    ) {
                        list1.put("start_time", "")
                        list1.put("end_time", "")
                    } else {
                        list1.put("start_time", slotOneSpinnerOne)
                        list1.put("end_time", slotOneSpinnerTwo)
                    }
                    list1.put(
                        "slot_flag",
                        SettingVideoScheduleListAdapter.settingVideoSchedModelList[i].slotFlagIdOne
                    )
                    objSlotOne.put(list1)
                } else if (i == 7) {
                    val list1 = JSONObject()
                    list1.put(
                        "id",
                        SettingVideoScheduleListAdapter.settingVideoSchedModelList[i].idOne
                    )
                    list1.put("day", "Sunday")
                    list1.put(
                        "slot_id",
                        SettingVideoScheduleListAdapter.settingVideoSchedModelList[i].slotIdOne
                    )
                    list1.put(
                        "schedule_id",
                        SettingVideoScheduleListAdapter.settingVideoSchedModelList[i].scheduleIdOne
                    )
                    slotOneSpinnerOne = ""
                    slotOneSpinnerTwo = ""
                    val currentString =
                        SettingVideoScheduleListAdapter.settingVideoSchedModelList[i].slotOneTime.toString()
                    if (!currentString.equals("", ignoreCase = true)) {
                        val separated =
                            currentString.split("-".toRegex()).dropLastWhile { it.isEmpty() }
                                .toTypedArray()
                        slotOneSpinnerOne = separated[0] // this will contain "Fruit"
                        slotOneSpinnerTwo = separated[1] // this will contain " they taste good"
                    }
                    if (slotOneSpinnerOne.equals("00:00:00", ignoreCase = true) &&
                        slotOneSpinnerTwo.equals("00:00:00", ignoreCase = true)
                    ) {
                        list1.put("start_time", "")
                        list1.put("end_time", "")
                    } else {
                        list1.put("start_time", slotOneSpinnerOne)
                        list1.put("end_time", slotOneSpinnerTwo)
                    }
                    list1.put(
                        "slot_flag",
                        SettingVideoScheduleListAdapter.settingVideoSchedModelList[i].slotFlagIdOne
                    )
                    objSlotOne.put(list1)
                }
            }
        } catch (e1: JSONException) {
            e1.printStackTrace()
        }
        val objSlotTwo = JSONArray()
        try {
            for (i in 1..7) {
                // 1st object
                if (i == 1) {
                    val list1 = JSONObject()
                    list1.put(
                        "id",
                        SettingVideoScheduleListAdapter.settingVideoSchedModelList[i].idTwo
                    )
                    list1.put("day", "Monday")
                    list1.put(
                        "slot_id",
                        SettingVideoScheduleListAdapter.settingVideoSchedModelList[i].slotIdTwo
                    )
                    list1.put(
                        "schedule_id",
                        SettingVideoScheduleListAdapter.settingVideoSchedModelList[i].scheduleIdTwo
                    )
                    slotOneSpinnerOne = ""
                    slotOneSpinnerTwo = ""
                    val currentString =
                        SettingVideoScheduleListAdapter.settingVideoSchedModelList[i].slotTwoTime.toString()
                    if (!currentString.equals("", ignoreCase = true)) {
                        val separated =
                            currentString.split("-".toRegex()).dropLastWhile { it.isEmpty() }
                                .toTypedArray()
                        slotOneSpinnerOne = separated[0] // this will contain "Fruit"
                        slotOneSpinnerTwo = separated[1] // this will contain " they taste good"
                    }
                    if (slotOneSpinnerOne.equals("00:00:00", ignoreCase = true) &&
                        slotOneSpinnerTwo.equals("00:00:00", ignoreCase = true)
                    ) {
                        list1.put("start_time", "")
                        list1.put("end_time", "")
                    } else {
                        list1.put("start_time", slotOneSpinnerOne)
                        list1.put("end_time", slotOneSpinnerTwo)
                    }
                    list1.put(
                        "slot_flag",
                        SettingVideoScheduleListAdapter.settingVideoSchedModelList[i].slotFlagIdTwo
                    )
                    objSlotTwo.put(list1)
                } else if (i == 2) {
                    val list1 = JSONObject()
                    list1.put(
                        "id",
                        SettingVideoScheduleListAdapter.settingVideoSchedModelList[i].idTwo
                    )
                    list1.put("day", "Tuesday")
                    list1.put(
                        "slot_id",
                        SettingVideoScheduleListAdapter.settingVideoSchedModelList[i].slotIdTwo
                    )
                    list1.put(
                        "schedule_id",
                        SettingVideoScheduleListAdapter.settingVideoSchedModelList[i].scheduleIdTwo
                    )
                    slotOneSpinnerOne = ""
                    slotOneSpinnerTwo = ""
                    val currentString =
                        SettingVideoScheduleListAdapter.settingVideoSchedModelList[i].slotTwoTime.toString()
                    if (!currentString.equals("", ignoreCase = true)) {
                        val separated =
                            currentString.split("-".toRegex()).dropLastWhile { it.isEmpty() }
                                .toTypedArray()
                        slotOneSpinnerOne = separated[0] // this will contain "Fruit"
                        slotOneSpinnerTwo = separated[1] // this will contain " they taste good"
                    }
                    if (slotOneSpinnerOne.equals("00:00:00", ignoreCase = true) &&
                        slotOneSpinnerTwo.equals("00:00:00", ignoreCase = true)
                    ) {
                        list1.put("start_time", "")
                        list1.put("end_time", "")
                    } else {
                        list1.put("start_time", slotOneSpinnerOne)
                        list1.put("end_time", slotOneSpinnerTwo)
                    }
                    list1.put(
                        "slot_flag",
                        SettingVideoScheduleListAdapter.settingVideoSchedModelList[i].slotFlagIdTwo
                    )
                    objSlotTwo.put(list1)
                } else if (i == 3) {
                    val list1 = JSONObject()
                    list1.put(
                        "id",
                        SettingVideoScheduleListAdapter.settingVideoSchedModelList[i].idTwo
                    )
                    list1.put("day", "Wednesday")
                    list1.put(
                        "slot_id",
                        SettingVideoScheduleListAdapter.settingVideoSchedModelList[i].slotIdTwo
                    )
                    list1.put(
                        "schedule_id",
                        SettingVideoScheduleListAdapter.settingVideoSchedModelList[i].scheduleIdTwo
                    )
                    slotOneSpinnerOne = ""
                    slotOneSpinnerTwo = ""
                    val currentString =
                        SettingVideoScheduleListAdapter.settingVideoSchedModelList[i].slotTwoTime.toString()
                    if (!currentString.equals("", ignoreCase = true)) {
                        val separated =
                            currentString.split("-".toRegex()).dropLastWhile { it.isEmpty() }
                                .toTypedArray()
                        slotOneSpinnerOne = separated[0] // this will contain "Fruit"
                        slotOneSpinnerTwo = separated[1] // this will contain " they taste good"
                    }
                    if (slotOneSpinnerOne.equals("00:00:00", ignoreCase = true) &&
                        slotOneSpinnerTwo.equals("00:00:00", ignoreCase = true)
                    ) {
                        list1.put("start_time", "")
                        list1.put("end_time", "")
                    } else {
                        list1.put("start_time", slotOneSpinnerOne)
                        list1.put("end_time", slotOneSpinnerTwo)
                    }
                    list1.put(
                        "slot_flag",
                        SettingVideoScheduleListAdapter.settingVideoSchedModelList[i].slotFlagIdTwo
                    )
                    objSlotTwo.put(list1)
                } else if (i == 4) {
                    val list1 = JSONObject()
                    list1.put(
                        "id",
                        SettingVideoScheduleListAdapter.settingVideoSchedModelList[i].idTwo
                    )
                    list1.put("day", "Thursday")
                    list1.put(
                        "slot_id",
                        SettingVideoScheduleListAdapter.settingVideoSchedModelList[i].slotIdTwo
                    )
                    list1.put(
                        "schedule_id",
                        SettingVideoScheduleListAdapter.settingVideoSchedModelList[i].scheduleIdTwo
                    )
                    slotOneSpinnerOne = ""
                    slotOneSpinnerTwo = ""
                    val currentString =
                        SettingVideoScheduleListAdapter.settingVideoSchedModelList[i].slotTwoTime.toString()
                    if (!currentString.equals("", ignoreCase = true)) {
                        val separated =
                            currentString.split("-".toRegex()).dropLastWhile { it.isEmpty() }
                                .toTypedArray()
                        slotOneSpinnerOne = separated[0] // this will contain "Fruit"
                        slotOneSpinnerTwo = separated[1] // this will contain " they taste good"
                    }
                    if (slotOneSpinnerOne.equals("00:00:00", ignoreCase = true) &&
                        slotOneSpinnerTwo.equals("00:00:00", ignoreCase = true)
                    ) {
                        list1.put("start_time", "")
                        list1.put("end_time", "")
                    } else {
                        list1.put("start_time", slotOneSpinnerOne)
                        list1.put("end_time", slotOneSpinnerTwo)
                    }
                    list1.put(
                        "slot_flag",
                        SettingVideoScheduleListAdapter.settingVideoSchedModelList[i].slotFlagIdTwo
                    )
                    objSlotTwo.put(list1)
                } else if (i == 5) {
                    val list1 = JSONObject()
                    list1.put(
                        "id",
                        SettingVideoScheduleListAdapter.settingVideoSchedModelList[i].idTwo
                    )
                    list1.put("day", "Friday")
                    list1.put(
                        "slot_id",
                        SettingVideoScheduleListAdapter.settingVideoSchedModelList[i].slotIdTwo
                    )
                    list1.put(
                        "schedule_id",
                        SettingVideoScheduleListAdapter.settingVideoSchedModelList[i].scheduleIdTwo
                    )
                    slotOneSpinnerOne = ""
                    slotOneSpinnerTwo = ""
                    val currentString =
                        SettingVideoScheduleListAdapter.settingVideoSchedModelList[i].slotTwoTime.toString()
                    if (!currentString.equals("", ignoreCase = true)) {
                        val separated =
                            currentString.split("-".toRegex()).dropLastWhile { it.isEmpty() }
                                .toTypedArray()
                        slotOneSpinnerOne = separated[0]
                        slotOneSpinnerTwo = separated[1]
                    }
                    if (slotOneSpinnerOne.equals("00:00:00", ignoreCase = true) &&
                        slotOneSpinnerTwo.equals("00:00:00", ignoreCase = true)
                    ) {
                        list1.put("start_time", "")
                        list1.put("end_time", "")
                    } else {
                        list1.put("start_time", slotOneSpinnerOne)
                        list1.put("end_time", slotOneSpinnerTwo)
                    }
                    list1.put(
                        "slot_flag",
                        SettingVideoScheduleListAdapter.settingVideoSchedModelList[i].slotFlagIdTwo
                    )
                    objSlotTwo.put(list1)
                } else if (i == 6) {
                    val list1 = JSONObject()
                    list1.put(
                        "id",
                        SettingVideoScheduleListAdapter.settingVideoSchedModelList[i].idTwo
                    )
                    list1.put("day", "Saturday")
                    list1.put(
                        "slot_id",
                        SettingVideoScheduleListAdapter.settingVideoSchedModelList[i].slotIdTwo
                    )
                    list1.put(
                        "schedule_id",
                        SettingVideoScheduleListAdapter.settingVideoSchedModelList[i].scheduleIdTwo
                    )
                    slotOneSpinnerOne = ""
                    slotOneSpinnerTwo = ""
                    val currentString =
                        SettingVideoScheduleListAdapter.settingVideoSchedModelList[i].slotTwoTime.toString()
                    if (!currentString.equals("", ignoreCase = true)) {
                        val separated =
                            currentString.split("-".toRegex()).dropLastWhile { it.isEmpty() }
                                .toTypedArray()
                        slotOneSpinnerOne = separated[0]
                        slotOneSpinnerTwo = separated[1]
                    }
                    if (slotOneSpinnerOne.equals("00:00:00", ignoreCase = true) &&
                        slotOneSpinnerTwo.equals("00:00:00", ignoreCase = true)
                    ) {
                        list1.put("start_time", "")
                        list1.put("end_time", "")
                    } else {
                        list1.put("start_time", slotOneSpinnerOne)
                        list1.put("end_time", slotOneSpinnerTwo)
                    }
                    list1.put(
                        "slot_flag",
                        SettingVideoScheduleListAdapter.settingVideoSchedModelList[i].slotFlagIdTwo
                    )
                    objSlotTwo.put(list1)
                } else if (i == 7) {
                    val list1 = JSONObject()
                    list1.put(
                        "id",
                        SettingVideoScheduleListAdapter.settingVideoSchedModelList[i].idTwo
                    )
                    list1.put("day", "Sunday")
                    list1.put(
                        "slot_id",
                        SettingVideoScheduleListAdapter.settingVideoSchedModelList[i].slotIdTwo
                    )
                    list1.put(
                        "schedule_id",
                        SettingVideoScheduleListAdapter.settingVideoSchedModelList[i].scheduleIdTwo
                    )
                    slotOneSpinnerOne = ""
                    slotOneSpinnerTwo = ""
                    val currentString =
                        SettingVideoScheduleListAdapter.settingVideoSchedModelList[i].slotTwoTime.toString()
                    if (!currentString.equals("", ignoreCase = true)) {
                        val separated =
                            currentString.split("-".toRegex()).dropLastWhile { it.isEmpty() }
                                .toTypedArray()
                        slotOneSpinnerOne = separated[0]
                        slotOneSpinnerTwo = separated[1]
                    }
                    if (slotOneSpinnerOne.equals("00:00:00", ignoreCase = true) &&
                        slotOneSpinnerTwo.equals("00:00:00", ignoreCase = true)
                    ) {
                        list1.put("start_time", "")
                        list1.put("end_time", "")
                    } else {
                        list1.put("start_time", slotOneSpinnerOne)
                        list1.put("end_time", slotOneSpinnerTwo)
                    }
                    list1.put(
                        "slot_flag",
                        SettingVideoScheduleListAdapter.settingVideoSchedModelList[i].slotFlagIdTwo
                    )
                    objSlotTwo.put(list1)
                }
            }
            objSlot.put(0, objSlotOne)
            objSlot.put(1, objSlotTwo)
        } catch (e1: JSONException) {
            e1.printStackTrace()
        }
        try {
            jsonValue = JSONObject()
            jsonValue1 = JSONObject()
            val price = 0
            val description = ""
            val address = ""
            val appointmentDuration = 0
            val days = 0
            val hours = 0
            val minutes = 0
            if (clinicType.equals("Clinic 1", ignoreCase = true)) {
                jsonValue = JSONObject()
                jsonValue!!.put("dr_service_id", drServiceIdClinicOne)
                jsonValue!!.put("address", videoAddressText!!.text.toString())
                jsonValue!!.put("alias", settingVideoShortText!!.text.toString())
                jsonValue!!.put("desc", settingVideoDescriptionText!!.text.toString())
                jsonValue!!.put("price", videoPriceInput!!.text.toString().toInt())
                jsonValue!!.put(
                    "appt_duration",
                    appointmentDurationSpinner!!.selectedItem.toString().toInt()
                )
                jsonValue!!.put("adv_booking", advanceBookingText!!.text.toString().toInt())
                jsonValue!!.put("booking_duration", videoDurationSpinner!!.selectedItem.toString())
                jsonValue!!.put("slots", objSlot)
                objClinic.put(jsonValue)
            } else if (clinicType.equals("Clinic 2", ignoreCase = true)) {
                jsonValue = JSONObject()
                jsonValue!!.put("dr_service_id", drServiceIdClinicTwo)
                jsonValue!!.put("address", videoAddressText!!.text.toString())
                jsonValue!!.put("alias", settingVideoShortText!!.text.toString())
                jsonValue!!.put("desc", settingVideoDescriptionText!!.text.toString())
                jsonValue!!.put("price", videoPriceInput!!.text.toString().toInt())
                jsonValue!!.put(
                    "appt_duration",
                    appointmentDurationSpinner!!.selectedItem.toString().toInt()
                )
                jsonValue!!.put("adv_booking", advanceBookingText!!.text.toString().toInt())
                jsonValue!!.put("booking_duration", videoDurationSpinner!!.selectedItem.toString())
                jsonValue!!.put("slots", objSlot)
                objClinic.put(jsonValue)
            } else if (clinicType.equals("Clinic 3", ignoreCase = true)) {
                jsonValue = JSONObject()
                jsonValue!!.put("dr_service_id", drServiceIdClinicThree)
                jsonValue!!.put("address", videoAddressText!!.text.toString())
                jsonValue!!.put("alias", settingVideoShortText!!.text.toString())
                jsonValue!!.put("desc", settingVideoDescriptionText!!.text.toString())
                jsonValue!!.put("price", videoPriceInput!!.text.toString().toInt())
                jsonValue!!.put(
                    "appt_duration",
                    appointmentDurationSpinner!!.selectedItem.toString().toInt()
                )
                jsonValue!!.put("adv_booking", advanceBookingText!!.text.toString().toInt())
                jsonValue!!.put("booking_duration", videoDurationSpinner!!.selectedItem.toString())
                jsonValue!!.put("slots", objSlot)
                objClinic.put(jsonValue)
            }
            jsonValue1!!.put("clinic", objClinic)
            jsonValue1!!.put("service", "Clinic")
        } catch (e: Exception) {
            e.printStackTrace()
        }


        for (i in 0 until objSlotOne.length()) {
            val slotOneArray = objSlotOne.getJSONObject(i)
            if (!slotOneArray.get("start_time").toString().equals("", ignoreCase = true)) {

                commonViewModel.commonViewModelCall(url, jsonValue1!!, Request.Method.POST).observe(
                    this@SettingsFormActivity
                ) { result ->
                    try {
                        //Process os success response
                        val responseObj = JSONObject(result)
                        dialog.dismiss()
                        if (responseObj.getInt("status_code") == 200) {
                            val response = responseObj.optJSONObject("response")
                            val successMessageString = response.getString("response")
                            Toast.makeText(
                                this@SettingsFormActivity,
                                successMessageString,
                                Toast.LENGTH_SHORT
                            ).show()

                        } else {
                            errorHandler(this@SettingsFormActivity, result)
                        }
                    } catch (e: Exception) {
                        dialog.dismiss()
                        e.printStackTrace()
                    }
                }


                slotSetOneCount = 0;
                slotSetTwoCount = 0;
                break;
            } else {
                slotSetOneCount++;
                if (slotSetOneCount == 7) {
                    for (j in 0 until objSlotTwo.length()) {
                        val slotTwoArray = objSlotTwo.getJSONObject(j)
                        if (!slotTwoArray.get("start_time").toString()
                                .equals("", ignoreCase = true)
                        ) {

                            commonViewModel.commonViewModelCall(
                                url,
                                jsonValue1!!,
                                Request.Method.POST
                            ).observe(
                                this@SettingsFormActivity
                            ) { result ->
                                try {
                                    //Process os success response
                                    val responseObj = JSONObject(result)
                                    dialog.dismiss()
                                    if (responseObj.getInt("status_code") == 200) {
                                        val response = responseObj.optJSONObject("response")
                                        val successMessageString = response.getString("response")
                                        Toast.makeText(
                                            this@SettingsFormActivity,
                                            successMessageString,
                                            Toast.LENGTH_SHORT
                                        ).show()

                                    } else {
                                        errorHandler(this@SettingsFormActivity, result)
                                    }
                                } catch (e: Exception) {
                                    dialog.dismiss()
                                    e.printStackTrace()
                                }
                            }

                            slotSetOneCount = 0;
                            slotSetTwoCount = 0;
                            break;
                        } else {
                            slotSetTwoCount++;
                            if (slotSetTwoCount == 7) {
                                Toast.makeText(
                                    this@SettingsFormActivity,
                                    "Please Click on Edit to set atleast one slot",
                                    Toast.LENGTH_SHORT
                                )
                                    .show()
                                slotSetOneCount = 0;
                                slotSetTwoCount = 0;
                                dialog.dismiss()
                            }
                        }
                    }
                }
            }

        }


//        commonViewModel.commonViewModelCall(url, jsonValue1!!, Request.Method.POST).observe(
//            this@SettingsFormActivity
//        ) { result ->
//            try {
//                //Process os success response
//                val responseObj = JSONObject(result)
//                dialog.dismiss()
//                if (responseObj.getInt("status_code") == 200) {
//                    val response = responseObj.optJSONObject("response")
//                    val successMessageString = response.getString("response")
//                    Toast.makeText(
//                        this@SettingsFormActivity,
//                        successMessageString,
//                        Toast.LENGTH_SHORT
//                    ).show()
//
//                } else {
//                    errorHandler(this@SettingsFormActivity, result)
//                }
//            } catch (e: Exception) {
//                dialog.dismiss()
//                e.printStackTrace()
//            }
//        }

    }

    override fun onSupportNavigateUp(): Boolean {
        super.getOnBackPressedDispatcher().onBackPressed()
        return true
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.clinic_menu, menu)
        item = menu.findItem(R.id.menu_tick)
        val newIcon = item!!.icon
        newIcon!!.mutate()
            .setColorFilter(
                ContextCompat.getColor(this@SettingsFormActivity, R.color.colorWhite),
                PorterDuff.Mode.SRC_IN
            )
        item!!.icon = newIcon
        if (clinicScheduleFlag == 0) {
            item!!.isVisible = false
        }
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_tick ->
                //Toast.makeText(this, "tick click", Toast.LENGTH_LONG).show();
                scheduleClinicOptionSave()
            else -> {}
        }
        return true
    }

    fun scheduleClinicOptionSave() {
        settingVideoSchedModelList!!.clear()
        var temp = SettingVideoScheduleModel()
        temp.dayName = "EDIT"
        temp.slotOneTime = "Slot 1"
        temp.slotTwoTime = "Slot 2"
        settingVideoSchedModelList!!.add(temp)
        temp = SettingVideoScheduleModel()
        temp.dayName = "Mon"
        /*Android : video nd Clinic time validation is not happening*/if (checkStartAndEndTime(
                SettingScheduleListAdapter.myArrayMonday[0]!!,
                SettingScheduleListAdapter.myArrayMonday[1]!!,
                SettingScheduleListAdapter.myArrayMonday, 0, 1
            )
        ) {
            if (SettingScheduleListAdapter.myArrayMonday[0].equals("", ignoreCase = true) ||
                SettingScheduleListAdapter.myArrayMonday[0].equals("select", ignoreCase = true)
            ) {
                temp.slotOneTime = ""
            } else {
                temp.slotOneTime = (SettingScheduleListAdapter.myArrayMonday[0]
                        + "-" + SettingScheduleListAdapter.myArrayMonday[1])
            }
        } else {
            Toast.makeText(
                this,
                "START time for Monday Slot 1 should be lesser than END time.",
                Toast.LENGTH_LONG
            ).show()
            return
        }
        if (checkStartAndEndTime(
                SettingScheduleListAdapter.myArrayMonday[2],
                SettingScheduleListAdapter.myArrayMonday[3],
                SettingScheduleListAdapter.myArrayMonday, 2, 3
            )
        ) {
            if (SettingScheduleListAdapter.myArrayMonday[2].equals("", ignoreCase = true) ||
                SettingScheduleListAdapter.myArrayMonday[2].equals("select", ignoreCase = true)
            ) {
                temp.slotTwoTime = ""
            } else {
                temp.slotTwoTime = (SettingScheduleListAdapter.myArrayMonday[2]
                        + "-" + SettingScheduleListAdapter.myArrayMonday[3])
            }
        } else {
            Toast.makeText(
                this,
                "START time for Monday Slot 2 should be lesser than END time.",
                Toast.LENGTH_LONG
            ).show()
            return
        }
        if (settingVideoSchedModelListDup != null) {
            temp.idOne = settingVideoSchedModelListDup!![1].idOne
            temp.slotIdOne = settingVideoSchedModelListDup!![1].slotIdOne
            temp.scheduleIdOne = settingVideoSchedModelListDup!![1].scheduleIdOne
            temp.slotFlagIdOne = settingVideoSchedModelListDup!![1].slotFlagIdOne
            temp.idTwo = settingVideoSchedModelListDup!![1].idTwo
            temp.slotIdTwo = settingVideoSchedModelListDup!![1].slotIdTwo
            temp.scheduleIdTwo = settingVideoSchedModelListDup!![1].scheduleIdTwo
            temp.slotFlagIdTwo = settingVideoSchedModelListDup!![1].slotFlagIdTwo
        }
        settingVideoSchedModelList!!.add(temp)
        temp = SettingVideoScheduleModel()
        temp.dayName = "Tue"
        if (checkStartAndEndTime(
                SettingScheduleListAdapter.myArrayTuesday[0],
                SettingScheduleListAdapter.myArrayTuesday[1],
                SettingScheduleListAdapter.myArrayTuesday, 0, 1
            )
        ) {
            if (SettingScheduleListAdapter.myArrayTuesday[0].equals("", ignoreCase = true) ||
                SettingScheduleListAdapter.myArrayTuesday[0].equals("select", ignoreCase = true)
            ) {
                temp.slotOneTime = ""
            } else {
                temp.slotOneTime = (SettingScheduleListAdapter.myArrayTuesday[0]
                        + "-" + SettingScheduleListAdapter.myArrayTuesday[1])
            }
        } else {
            Toast.makeText(
                this,
                "START time for Tuesday Slot 1 should be lesser than END time.",
                Toast.LENGTH_LONG
            ).show()
            return
        }
        if (checkStartAndEndTime(
                SettingScheduleListAdapter.myArrayTuesday[2],
                SettingScheduleListAdapter.myArrayTuesday[3],
                SettingScheduleListAdapter.myArrayTuesday,
                2, 3
            )
        ) {
            if (SettingScheduleListAdapter.myArrayTuesday[2].equals("", ignoreCase = true) ||
                SettingScheduleListAdapter.myArrayTuesday[2].equals("select", ignoreCase = true)
            ) {
                temp.slotTwoTime = ""
            } else {
                temp.slotTwoTime = (SettingScheduleListAdapter.myArrayTuesday[2]
                        + "-" + SettingScheduleListAdapter.myArrayTuesday[3])
            }
        } else {
            Toast.makeText(
                this,
                "START time for Tuesday Slot 2 should be lesser than END time.",
                Toast.LENGTH_LONG
            ).show()
            return
        }
        if (settingVideoSchedModelListDup != null) {
            temp.idOne = settingVideoSchedModelListDup!![2].idOne
            temp.slotIdOne = settingVideoSchedModelListDup!![2].slotIdOne
            temp.scheduleIdOne = settingVideoSchedModelListDup!![2].scheduleIdOne
            temp.slotFlagIdOne = settingVideoSchedModelListDup!![2].slotFlagIdOne
            temp.idTwo = settingVideoSchedModelListDup!![2].idTwo
            temp.slotIdTwo = settingVideoSchedModelListDup!![2].slotIdTwo
            temp.scheduleIdTwo = settingVideoSchedModelListDup!![2].scheduleIdTwo
            temp.slotFlagIdTwo = settingVideoSchedModelListDup!![2].slotFlagIdTwo
        }
        settingVideoSchedModelList!!.add(temp)
        temp = SettingVideoScheduleModel()
        temp.dayName = "Wed"
        if (checkStartAndEndTime(
                SettingScheduleListAdapter.myArrayWednesday[0],
                SettingScheduleListAdapter.myArrayWednesday[1],
                SettingScheduleListAdapter.myArrayWednesday, 0, 1
            )
        ) {
            if (SettingScheduleListAdapter.myArrayWednesday[0].equals("", ignoreCase = true) ||
                SettingScheduleListAdapter.myArrayWednesday[0].equals("select", ignoreCase = true)
            ) {
                temp.slotOneTime = ""
            } else {
                temp.slotOneTime = (SettingScheduleListAdapter.myArrayWednesday[0]
                        + "-" + SettingScheduleListAdapter.myArrayWednesday[1])
            }
        } else {
            Toast.makeText(
                this,
                "START time for Wednesday Slot 1 should be lesser than END time.",
                Toast.LENGTH_LONG
            ).show()
            return
        }
        if (checkStartAndEndTime(
                SettingScheduleListAdapter.myArrayWednesday[2],
                SettingScheduleListAdapter.myArrayWednesday[3],
                SettingScheduleListAdapter.myArrayWednesday, 2, 3
            )
        ) {
            if (SettingScheduleListAdapter.myArrayWednesday[2].equals("", ignoreCase = true) ||
                SettingScheduleListAdapter.myArrayWednesday[2].equals("select", ignoreCase = true)
            ) {
                temp.slotTwoTime = ""
            } else {
                temp.slotTwoTime = (SettingScheduleListAdapter.myArrayWednesday[2]
                        + "-" + SettingScheduleListAdapter.myArrayWednesday[3])
            }
        } else {
            Toast.makeText(
                this,
                "START time for Wednesday Slot 2 should be lesser than END time.",
                Toast.LENGTH_LONG
            ).show()
            return
        }
        if (settingVideoSchedModelListDup != null) {
            temp.idOne = settingVideoSchedModelListDup!![3].idOne
            temp.slotIdOne = settingVideoSchedModelListDup!![3].slotIdOne
            temp.scheduleIdOne = settingVideoSchedModelListDup!![3].scheduleIdOne
            temp.slotFlagIdOne = settingVideoSchedModelListDup!![3].slotFlagIdOne
            temp.idTwo = settingVideoSchedModelListDup!![3].idTwo
            temp.slotIdTwo = settingVideoSchedModelListDup!![3].slotIdTwo
            temp.scheduleIdTwo = settingVideoSchedModelListDup!![3].scheduleIdTwo
            temp.slotFlagIdTwo = settingVideoSchedModelListDup!![3].slotFlagIdTwo
        }
        settingVideoSchedModelList!!.add(temp)
        temp = SettingVideoScheduleModel()
        temp.dayName = "Thu"
        if (checkStartAndEndTime(
                SettingScheduleListAdapter.myArrayThursday[0],
                SettingScheduleListAdapter.myArrayThursday[1],
                SettingScheduleListAdapter.myArrayThursday, 0, 1
            )
        ) {
            if (SettingScheduleListAdapter.myArrayThursday[0].equals("", ignoreCase = true) ||
                SettingScheduleListAdapter.myArrayThursday[0].equals("select", ignoreCase = true)
            ) {
                temp.slotOneTime = ""
            } else {
                temp.slotOneTime = (SettingScheduleListAdapter.myArrayThursday[0]
                        + "-" + SettingScheduleListAdapter.myArrayThursday[1])
            }
        } else {
            Toast.makeText(
                this,
                "START time for Thursday Slot 1 should be lesser than END time.",
                Toast.LENGTH_LONG
            ).show()
            return
        }
        if (checkStartAndEndTime(
                SettingScheduleListAdapter.myArrayThursday[2],
                SettingScheduleListAdapter.myArrayThursday[3],
                SettingScheduleListAdapter.myArrayThursday, 2, 3
            )
        ) {
            if (SettingScheduleListAdapter.myArrayThursday[2].equals("", ignoreCase = true) ||
                SettingScheduleListAdapter.myArrayThursday[2].equals("select", ignoreCase = true)
            ) {
                temp.slotTwoTime = ""
            } else {
                temp.slotTwoTime = (SettingScheduleListAdapter.myArrayThursday[2]
                        + "-" + SettingScheduleListAdapter.myArrayThursday[3])
            }
        } else {
            Toast.makeText(
                this,
                "START time for Thursday Slot 2 should be lesser than END time.",
                Toast.LENGTH_LONG
            ).show()
            return
        }
        if (settingVideoSchedModelListDup != null) {
            temp.idOne = settingVideoSchedModelListDup!![4].idOne
            temp.slotIdOne = settingVideoSchedModelListDup!![4].slotIdOne
            temp.scheduleIdOne = settingVideoSchedModelListDup!![4].scheduleIdOne
            temp.slotFlagIdOne = settingVideoSchedModelListDup!![4].slotFlagIdOne
            temp.idTwo = settingVideoSchedModelListDup!![4].idTwo
            temp.slotIdTwo = settingVideoSchedModelListDup!![4].slotIdTwo
            temp.scheduleIdTwo = settingVideoSchedModelListDup!![4].scheduleIdTwo
            temp.slotFlagIdTwo = settingVideoSchedModelListDup!![4].slotFlagIdTwo
        }
        settingVideoSchedModelList!!.add(temp)
        temp = SettingVideoScheduleModel()
        temp.dayName = "Fri"
        if (checkStartAndEndTime(
                SettingScheduleListAdapter.myArrayFriday[0],
                SettingScheduleListAdapter.myArrayFriday[1],
                SettingScheduleListAdapter.myArrayFriday, 0, 1
            )
        ) {
            if (SettingScheduleListAdapter.myArrayFriday[0].equals("", ignoreCase = true) ||
                SettingScheduleListAdapter.myArrayFriday[0].equals("select", ignoreCase = true)
            ) {
                temp.slotOneTime = ""
            } else {
                temp.slotOneTime = (SettingScheduleListAdapter.myArrayFriday[0]
                        + "-" + SettingScheduleListAdapter.myArrayFriday[1])
            }
        } else {
            Toast.makeText(
                this,
                "START time for Friday Slot 1 should be lesser than END time.",
                Toast.LENGTH_LONG
            ).show()
            return
        }
        if (checkStartAndEndTime(
                SettingScheduleListAdapter.myArrayFriday[2],
                SettingScheduleListAdapter.myArrayFriday[3],
                SettingScheduleListAdapter.myArrayFriday, 2, 3
            )
        ) {
            if (SettingScheduleListAdapter.myArrayFriday[2].equals("", ignoreCase = true) ||
                SettingScheduleListAdapter.myArrayFriday[2].equals("select", ignoreCase = true)
            ) {
                temp.slotTwoTime = ""
            } else {
                temp.slotTwoTime = (SettingScheduleListAdapter.myArrayFriday[2]
                        + "-" + SettingScheduleListAdapter.myArrayFriday[3])
            }
        } else {
            Toast.makeText(
                this,
                "START time for Friday Slot 2 should be lesser than END time.",
                Toast.LENGTH_LONG
            ).show()
            return
        }
        if (settingVideoSchedModelListDup != null) {
            temp.idOne = settingVideoSchedModelListDup!![5].idOne
            temp.slotIdOne = settingVideoSchedModelListDup!![5].slotIdOne
            temp.scheduleIdOne = settingVideoSchedModelListDup!![5].scheduleIdOne
            temp.slotFlagIdOne = settingVideoSchedModelListDup!![5].slotFlagIdOne
            temp.idTwo = settingVideoSchedModelListDup!![5].idTwo
            temp.slotIdTwo = settingVideoSchedModelListDup!![5].slotIdTwo
            temp.scheduleIdTwo = settingVideoSchedModelListDup!![5].scheduleIdTwo
            temp.slotFlagIdTwo = settingVideoSchedModelListDup!![5].slotFlagIdTwo
        }
        settingVideoSchedModelList!!.add(temp)
        temp = SettingVideoScheduleModel()
        temp.dayName = "Sat"
        if (checkStartAndEndTime(
                SettingScheduleListAdapter.myArraySaturday[0],
                SettingScheduleListAdapter.myArraySaturday[1],
                SettingScheduleListAdapter.myArraySaturday, 0, 1
            )
        ) {
            if (SettingScheduleListAdapter.myArraySaturday[0].equals("", ignoreCase = true) ||
                SettingScheduleListAdapter.myArraySaturday[0].equals("select", ignoreCase = true)
            ) {
                temp.slotOneTime = ""
            } else {
                temp.slotOneTime = (SettingScheduleListAdapter.myArraySaturday[0]
                        + "-" + SettingScheduleListAdapter.myArraySaturday[1])
            }
        } else {
            Toast.makeText(
                this,
                "START time for Saturday Slot 1 should be lesser than END time.",
                Toast.LENGTH_LONG
            ).show()
            return
        }
        if (checkStartAndEndTime(
                SettingScheduleListAdapter.myArraySaturday[2],
                SettingScheduleListAdapter.myArraySaturday[3],
                SettingScheduleListAdapter.myArraySaturday, 2, 3
            )
        ) {
            if (SettingScheduleListAdapter.myArraySaturday[2].equals("", ignoreCase = true) ||
                SettingScheduleListAdapter.myArraySaturday[2].equals("select", ignoreCase = true)
            ) {
                temp.slotTwoTime = ""
            } else {
                temp.slotTwoTime = (SettingScheduleListAdapter.myArraySaturday[2]
                        + "-" + SettingScheduleListAdapter.myArraySaturday[3])
            }
        } else {
            Toast.makeText(
                this,
                "START time for Saturday Slot 2 should be lesser than END time.",
                Toast.LENGTH_LONG
            ).show()
            return
        }
        if (settingVideoSchedModelListDup != null) {
            temp.idOne = settingVideoSchedModelListDup!![6].idOne
            temp.slotIdOne = settingVideoSchedModelListDup!![6].slotIdOne
            temp.scheduleIdOne = settingVideoSchedModelListDup!![6].scheduleIdOne
            temp.slotFlagIdOne = settingVideoSchedModelListDup!![6].slotFlagIdOne
            temp.idTwo = settingVideoSchedModelListDup!![6].idTwo
            temp.slotIdTwo = settingVideoSchedModelListDup!![6].slotIdTwo
            temp.scheduleIdTwo = settingVideoSchedModelListDup!![6].scheduleIdTwo
            temp.slotFlagIdTwo = settingVideoSchedModelListDup!![6].slotFlagIdTwo
        }
        settingVideoSchedModelList!!.add(temp)
        temp = SettingVideoScheduleModel()
        temp.dayName = "Sun"
        if (checkStartAndEndTime(
                SettingScheduleListAdapter.myArraySunday[0],
                SettingScheduleListAdapter.myArraySunday[1],
                SettingScheduleListAdapter.myArraySunday, 0, 1
            )
        ) {
            if (SettingScheduleListAdapter.myArraySunday[0].equals("", ignoreCase = true) ||
                SettingScheduleListAdapter.myArraySunday[0].equals("select", ignoreCase = true)
            ) {
                temp.slotOneTime = ""
            } else {
                temp.slotOneTime = (SettingScheduleListAdapter.myArraySunday[0]
                        + "-" + SettingScheduleListAdapter.myArraySunday[1])
            }
        } else {
            Toast.makeText(
                this,
                "START time for Sunday Slot 1 should be lesser than END time.",
                Toast.LENGTH_LONG
            ).show()
            return
        }
        if (checkStartAndEndTime(
                SettingScheduleListAdapter.myArraySunday[2],
                SettingScheduleListAdapter.myArraySunday[3],
                SettingScheduleListAdapter.myArraySunday, 2, 3
            )
        ) {
            if (SettingScheduleListAdapter.myArraySunday[2].equals("", ignoreCase = true) ||
                SettingScheduleListAdapter.myArraySunday[2].equals("select", ignoreCase = true)
            ) {
                temp.slotTwoTime = ""
            } else {
                temp.slotTwoTime = (SettingScheduleListAdapter.myArraySunday[2]
                        + "-" + SettingScheduleListAdapter.myArraySunday[3])
            }
        } else {
            Toast.makeText(
                this,
                "START time for Sunday Slot 2 should be lesser than END time.",
                Toast.LENGTH_LONG
            ).show()
            return
        }
        if (settingVideoSchedModelListDup != null) {
            temp.idOne = settingVideoSchedModelListDup!![7].idOne
            temp.slotIdOne = settingVideoSchedModelListDup!![7].slotIdOne
            temp.scheduleIdOne = settingVideoSchedModelListDup!![7].scheduleIdOne
            temp.slotFlagIdOne = settingVideoSchedModelListDup!![7].slotFlagIdOne
            temp.idTwo = settingVideoSchedModelListDup!![7].idTwo
            temp.slotIdTwo = settingVideoSchedModelListDup!![7].slotIdTwo
            temp.scheduleIdTwo = settingVideoSchedModelListDup!![7].scheduleIdTwo
            temp.slotFlagIdTwo = settingVideoSchedModelListDup!![7].slotFlagIdTwo
        }
        settingVideoSchedModelList!!.add(temp)
        apptStatLayout!!.visibility = View.GONE
        settingServiceSetupLayout!!.visibility = View.GONE
        settingInstantVideoLayout!!.visibility = View.GONE
        settingChaLayout!!.visibility = View.GONE
        settingVideoLayout!!.visibility = View.VISIBLE
        videoSettingScheduleLayout!!.visibility = View.GONE
        if (intent.getStringExtra("Title")
                .equals("Clinic 1", ignoreCase = true) || intent.getStringExtra("Title")
                .equals("Clinic 2", ignoreCase = true) || intent.getStringExtra("Title")
                .equals("Clinic 3", ignoreCase = true)
        ) {
            videoAddressInputLayout!!.visibility = View.VISIBLE
            item!!.isVisible = false
            val clinicType = intent.getStringExtra("Title").toString()
            MyClinicGlobalClass.logUserActionEvent(
                ApiUrls.doctorId, getString(R.string.ClinicServiceSetUpComplete),
                null
            )
            upDateClinicSettingDetails2(
                drServiceIdClinicOne,
                drServiceIdClinicTwo,
                drServiceIdClinicThree,
                clinicType
            )
        } else {
            videoAddressInputLayout!!.visibility = View.GONE
            if (videoSetupFlagClick == 2) {
                setupUpVideoSettingService(1)
            } else if (clinicSetupFlagClick == 2) {
                setupUpVideoSettingService(3)
            } else {
                upDateVideoSettingDetails()
            }
        }
    }

    val doctorService: Unit
        get() {
            showCustomProgressAlertDialog(
                resources.getString(R.string.fetching),
                resources.getString(R.string.wait_while_we_fetching)
            )
            val url = ApiUrls.getDoctorSettings

            commonViewModel.commonViewModelCall(url, JSONObject(), Request.Method.GET).observe(
                this@SettingsFormActivity
            ) { result ->
                try {
                    //Process os success response
                    val responseObj = JSONObject(result)
                    dialog.dismiss()
                    if (responseObj.getInt("status_code") == 200) {
                        val response = responseObj.optJSONObject("response")
                        serviceIdClinicCount = 0
                        val rootObj = response.optJSONObject("response")
                        var clinicServices: JSONArray? = null
                        myList = ArrayList()
                        if (rootObj != null) {
                            clinicServices = rootObj.getJSONArray("enabledServices")
                            for (i in 0 until clinicServices.length()) {
                                val services =
                                    clinicServices.getJSONObject(i).getInt("service_id")
                                val serviceActive =
                                    clinicServices.getJSONObject(i).getInt("active")
                                if (services == 3) {
                                    serviceIdClinicCount++
                                    (myList as ArrayList<Int>).add(serviceActive)
                                    if (serviceIdClinicCount == 3) {
                                        break
                                    }
                                }
                            }
                        }
                        settingServiceSetupDetails

                    } else {
                        errorHandler(this@SettingsFormActivity, result)
                    }
                } catch (e: Exception) {
                    dialog.dismiss()
                    e.printStackTrace()
                }
            }

        }

    //added on 23rd 2020
    fun checkDoctorMerchantStatus() {
        showCustomProgressAlertDialog(
            resources.getString(R.string.fetching),
            resources.getString(R.string.wait_while_we_fetching)
        )
        val url = ApiUrls.getDoctorMerchantStatus

        commonViewModel.commonViewModelCall(url, JSONObject(), Request.Method.GET).observe(
            this@SettingsFormActivity
        ) { result ->
            try {
                //Process os success response
                val responseObj = JSONObject(result)
                dialog.dismiss()
                if (responseObj.getInt("status_code") == 200) {
                    val response = responseObj.optJSONObject("response")
                    val jsonObject = response.getJSONObject("response")
                    isCreateMerchant = jsonObject.getInt("is_create_merchant")
                    //                            isCreateMerchant = 1;
                    if (isCreateMerchant == 1) {
                        payUMerchantCreaionFormPopup()
                    } else {
                        if (videoSetupFlagClick == 1) {
                            videoSetupFlagClick = 1
                            apptStatLayout!!.visibility = View.GONE
                            settingServiceSetupLayout!!.visibility = View.GONE
                            settingInstantVideoLayout!!.visibility = View.GONE
                            settingChaLayout!!.visibility = View.GONE
                            settingVideoLayout!!.visibility = View.GONE
                            videoSettingScheduleLayout!!.visibility = View.GONE
                            videoAddressInputLayout!!.visibility = View.GONE
                            videoSettingScrollView!!.visibility = View.GONE
                            timeBlockLayout!!.visibility = View.GONE
                            videoSettingServiceScrollView!!.visibility = View.VISIBLE
                            settingServiceVideoForm!!.visibility = View.VISIBLE
                            settingVideoButton!!.visibility = View.VISIBLE
                            no_show_lay__video_setup!!.visibility = View.VISIBLE
                            videoServiceAddressInputLayout!!.visibility = View.GONE
                            videoServiceSettingScheduleLayout!!.visibility = View.GONE
                            setupVideoService(1, "Video")
                        } else if (instantVideoSetupFlagClick == 1) {
                            instantVideoSetupFlagClick = 1
                            apptStatLayout!!.visibility = View.GONE
                            settingServiceSetupLayout!!.visibility = View.GONE
                            settingInstantVideoLayout!!.visibility = View.VISIBLE
                            settingChaLayout!!.visibility = View.GONE
                            settingVideoLayout!!.visibility = View.GONE
                            videoSettingScheduleLayout!!.visibility = View.GONE
                            videoSettingScrollView!!.visibility = View.GONE
                            timeBlockLayout!!.visibility = View.GONE
                            settingInstantVideoEnableDisable!!.visibility = View.GONE
                            settingInstantVideo!!.text = "Setup"
                        } else if (chatSetupFlagClick == 1) {
                            chatSetupFlagClick = 1
                            apptStatLayout!!.visibility = View.GONE
                            settingServiceSetupLayout!!.visibility = View.GONE
                            settingInstantVideoLayout!!.visibility = View.GONE
                            settingChaLayout!!.visibility = View.VISIBLE
                            settingVideoButton!!.visibility = View.GONE
                            settingVideoLayout!!.visibility = View.GONE
                            videoSettingScheduleLayout!!.visibility = View.GONE
                            videoSettingScrollView!!.visibility = View.GONE
                            timeBlockLayout!!.visibility = View.GONE
                            settingChatEnableDisable!!.visibility = View.GONE
                            chaSettingUpdateButton!!.text = "Setup"
                        }
                    }

                } else {
                    errorHandler(this@SettingsFormActivity, result)
                }
            } catch (e: Exception) {
                dialog.dismiss()
                e.printStackTrace()
            }
        }

    }

    fun trimMessage(json: String?, key: String?): String? {
        var trimmedString: String? = null
        trimmedString = try {
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
        Toast.makeText(applicationContext, toastString, Toast.LENGTH_LONG).show()
    }

    fun payUMerchantCreaionFormPopup() {
        val myIntent = Intent(this@SettingsFormActivity, PayUMearchantDetailsActivity::class.java)
        this@SettingsFormActivity.startActivity(myIntent)
    }

    override fun onResume() {
        super.onResume()
        if (isPayUMerchantCreate) {
            checkDoctorMerchantStatus()
            isPayUMerchantCreate = false
        }
    }

    //added on 23rd 2020
    fun createMerchant(nameValue: String?, phoneValue: String?, EmailValue: String?) {

        showCustomProgressAlertDialog(
            resources.getString(R.string.please_wait),
            resources.getString(R.string.wait_while_creating)
        )

        val url = ApiUrls.createMerchant
        try {
            jsonValue = JSONObject()
            jsonValue!!.put("name", nameValue)
            jsonValue!!.put("phone", phoneValue)
            jsonValue!!.put("email", EmailValue)
        } catch (e: Exception) {
        }

        commonViewModel.commonViewModelCall(url, jsonValue!!, Request.Method.POST).observe(
            this@SettingsFormActivity
        ) { result ->
            try {
                //Process os success response
                val responseObj = JSONObject(result)
                dialog.dismiss()
                if (responseObj.getInt("status_code") == 200) {
                    val response = responseObj.optJSONObject("response")
                    val responseValue = response.getInt("response")
                    if (responseValue == 1) {
                        Toast.makeText(
                            this@SettingsFormActivity,
                            "Your PayU merchant account has been created successfully",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                    }

                } else {
                    errorHandler(this@SettingsFormActivity, result)
                }
            } catch (e: Exception) {
                dialog.dismiss()
                e.printStackTrace()
            }
        }

    }

    //setUp status
    val setUpStatus: Unit
        get() {
            createNewAccountViewModel!!.setUpStatusModel(this@SettingsFormActivity, param)
                .observe(this@SettingsFormActivity, object : Observer<String?> {
                    override fun onChanged(value: String?) {
                        if (value != null) {
                            Log.d("Response", value)
                        }
                        try {
                            Log.d(TAG, "onChanged: $value")
                            val jsonObject = JSONObject(value)
                            if (jsonObject.getInt("status_code") == 200) {
                                val response = JSONObject(value).getJSONObject("response")
                                    .getJSONObject("response")
                                val showSectionFlag =
                                    response.getInt(RestUtils.TAG_PAYU_SHOW_SECTION)
                                val Msg = response.getString("message")
                                if (showSectionFlag == 0) {
                                } else {
                                    if (showSectionFlag == 1) {
                                        val builder1 =
                                            AlertDialog.Builder(this@SettingsFormActivity)
                                        builder1.setMessage(Msg)
                                        builder1.setCancelable(true)
                                        builder1.setPositiveButton(
                                            "Create Now"
                                        ) { dialog, id ->
                                            val intent = Intent(
                                                this@SettingsFormActivity,
                                                PaymentSetupScreen::class.java
                                            )
                                            //                                                intent.putExtra("Doc_Name", mainActivity.docName);
//                                                intent.putExtra("Doc_Email", mainActivity.docEmail);
//                                                intent.putExtra("Doc_Phone", mainActivity.docPhone);
                                            startActivity(intent)
                                        }
                                        builder1.setNegativeButton(
                                            "No"
                                        ) { dialog, id -> dialog.cancel() }
                                        val alert11 = builder1.create()
                                        alert11.show()
                                    }
                                }
                            } else {
                                if (value != null) {
                                    errorHandler(this@SettingsFormActivity, value)
                                }
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                })
        }

    override fun onItemSelected(adapterView: AdapterView<*>, view: View, i: Int, l: Long) {
        selectedSpinnerNoShow = adapterView.getItemAtPosition(i).toString()
        if (selectedSpinnerNoShow.equals("No", ignoreCase = true)) {
            no_show_limit_video_et!!.visibility = View.GONE
            no_show_limit_instant_video_et!!.visibility = View.GONE
            no_show_limt_video!!.visibility = View.GONE
            no_show_limt_instant_video!!.visibility = View.GONE
            no_show_limit_instant_video_et!!.setText("")
            no_show_limit_video_et!!.setText("")
            no_show_limit_video_setup_et!!.setText("")
            no_show_limit_video_setup_et!!.visibility = View.GONE
            no_show_limt_video_setup!!.visibility = View.GONE
        } else {
            no_show_limit_video_et!!.visibility = View.VISIBLE
            no_show_limit_instant_video_et!!.visibility = View.VISIBLE
            no_show_limt_video!!.visibility = View.VISIBLE
            no_show_limt_instant_video!!.visibility = View.VISIBLE
            no_show_limit_video_setup_et!!.visibility = View.VISIBLE
            no_show_limt_video_setup!!.visibility = View.VISIBLE
            if (no_show_limit_video_value != 0) {
                no_show_limit_video_et!!.setText(no_show_limit_video_value.toString() + "")
                no_show_limit_video_value = 0
            } else {
                no_show_limit_video_et!!.setText("10")
            }
            if (no_show_limit_instant_video_value != 0) {
                no_show_limit_instant_video_et!!.setText(no_show_limit_instant_video_value.toString() + "")
                no_show_limit_instant_video_value = 0
            } else {
                no_show_limit_instant_video_et!!.setText("10")
            }
            no_show_limit_video_setup_et!!.setText("10")
        }
    }

    override fun onNothingSelected(adapterView: AdapterView<*>?) {}

    /*Android : video nd Clinic time validation is not happening*/
    private fun checkStartAndEndTime(
        firstTime: String?, lastTime: String?, myArray: Array<String?>,
        startPos: Int, endPos: Int
    ): Boolean {
        var timeBool: Boolean
        val formatTime = ""
        var startTimeMills: Long = 0
        var endTimeMills: Long = 0
        /*if (firstTime.equalsIgnoreCase("")) {
            firstTime = "";
        }
        if (lastTime.equalsIgnoreCase("")) {
            lastTime = "";
        }*/if (firstTime.equals("Select", ignoreCase = true) && lastTime.equals(
                "Select",
                ignoreCase = true
            ) || firstTime.equals("", ignoreCase = true) && lastTime.equals("", ignoreCase = true)
        ) {
            myArray[startPos] = ""
            myArray[endPos] = ""
            return true
        } else {
            if (firstTime.equals("00:00", ignoreCase = true) && lastTime.equals(
                    "00:00",
                    ignoreCase = true
                ) ||
                firstTime.equals("Select", ignoreCase = true) ||
                lastTime.equals("Select", ignoreCase = true) ||
                firstTime.equals("", ignoreCase = true) ||
                lastTime.equals("", ignoreCase = true)
            ) {
                return false
            }
        }
        try {
            @SuppressLint("SimpleDateFormat") val date1 = SimpleDateFormat("HH:mm").parse(firstTime)
            @SuppressLint("SimpleDateFormat") val date2 = SimpleDateFormat("HH:mm").parse(lastTime)
            assert(date1 != null)
            startTimeMills = date1!!.time
            assert(date2 != null)
            endTimeMills = date2!!.time
        } catch (e: ParseException) {
            timeBool = false
        }
        timeBool = startTimeMills < endTimeMills
        return timeBool
    }

    private fun updateSettingValues() {
        val doctorDetailsIntent = Intent()
        doctorDetailsIntent.action = "Update_Doctor_Details_Settings"
        sendBroadcast(doctorDetailsIntent)
    }

    override fun onBackPressed() {
        super.getOnBackPressedDispatcher().onBackPressed()
        updateSettingValues()
    }

    companion object {
        val TAG = SettingsFormActivity::class.java.simpleName
        private lateinit var mContext: Context
        var profesionalLanguageText: TextView? = null
        var profesionalQualificationText: TextView? = null
        var settingVideoEnableDisableSwitch: Switch? = null

        @JvmField
        var videoSetupFlagClick = 0

        @JvmField
        var clinicSetupFlagClick = 0
        const val MY_PREFS_NAME = "MyPrefsFile"
        var serviceIdClinicCount = 0

        @JvmField
        var isPayUMerchantCreate = false
    }

    fun showCustomProgressAlertDialog(
        title: String?,
        progressVal: String?

    ) {
        val builder = AlertDialog.Builder(this@SettingsFormActivity)
        builder.setTitle(title)
        builder.setCancelable(false)
        val customLayout = LayoutInflater.from(this@SettingsFormActivity)
            .inflate(R.layout.custom_progress_bar, null)
        builder.setView(customLayout)
        dialog = builder.create()
        val tvBody = customLayout.findViewById<TextView>(R.id.tv_value)
        tvBody.text = progressVal
        dialog.show()
    }

}