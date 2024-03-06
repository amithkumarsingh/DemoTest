package com.whitecoats.clinicplus

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import co.mobiwise.materialintro.shape.Focus
import co.mobiwise.materialintro.shape.FocusGravity
import co.mobiwise.materialintro.shape.ShapeType
import co.mobiwise.materialintro.view.MaterialIntroView
import com.android.volley.Request
import com.android.volley.toolbox.ImageRequest
import com.android.volley.toolbox.Volley
import com.brandkinesis.BrandKinesis
import com.brandkinesis.activitymanager.BKActivityTypes
import com.brandkinesis.callback.BKActivityCallback
import com.brandkinesis.utils.BKUtilLogger
import com.whitecoats.adapter.SettingServiceListAdapter
import com.whitecoats.clinicplus.activities.*
import com.whitecoats.clinicplus.apis.ApiGetPostMethodCalls
import com.whitecoats.clinicplus.apis.ApiUrls
import com.whitecoats.clinicplus.utils.ErrorHandlerClass.errorHandler
import com.whitecoats.clinicplus.utils.SharedPref
import com.whitecoats.clinicplus.viewmodels.CommonViewModel
import com.whitecoats.clinicplus.viewmodels.MainActivityViewModel
import com.whitecoats.clinicplus.viewmodels.SettingViewModel
import com.whitecoats.model.SettingServiceModel
import com.zoho.salesiqembed.ZohoSalesIQ
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.util.*

class SettingsActivity : AppCompatActivity() {
    private var serviceRecycleView: RecyclerView? = null
    private var settingServiceModelList: MutableList<SettingServiceModel>? = null
    private var settingServiceListAdapter: SettingServiceListAdapter? = null
    private var personalLayout: RelativeLayout? = null
    private var profLayout: RelativeLayout? = null
    private var financeLayout: RelativeLayout? = null
    private var timeBlockLayout: RelativeLayout? = null
    private var recordLayout: RelativeLayout? = null
    private var apptRemindLayout: RelativeLayout? = null
    private var notification_setting_layout: RelativeLayout? = null
    private var appPreference: SharedPreferences? = null
    private var sharedPreferencesTimeFormat: SharedPreferences? = null
    private var rootObjSettingService: JSONObject? = null
    private var appDatabaseManager: AppDatabaseManager? = null
    private var settingDocImage: ImageView? = null
    private var doctorDetailsJsonResponse: String? = null
    private var settingSetupText: TextView? = null
    private var videoToolSettingSetupText: TextView? = null
    private var noExternalVideoSetupText: TextView? = null
    private var noServiceTextMsg: TextView? = null
    private var settingHome: ImageView? = null
    private var serviceIdClinicCount = 0
    private var videoToolSetupLayout: RelativeLayout? = null
    private var videoSetupButtonClicked = false
    private var videoToolOptionLayout: LinearLayout? = null
    private var whiteCoatsBuildInLayout: RelativeLayout? = null
    private var WhatAppLayout: RelativeLayout? = null
    private var zoomLayout: RelativeLayout? = null
    private var googleMeetLayout: RelativeLayout? = null
    private var otherLayout: RelativeLayout? = null
    private var socialProfileLayout: RelativeLayout? = null

    //Initializing timeFormat spinner array
    private val timeFormatSinnerArray = arrayOf<String?>("12 Hrs Format", "24 Hrs Format")

    //guide ppoints
    private var profileGuidePt: LinearLayout? = null
    var mainActivity = MainActivity()
    private var timeFormatSpinner: Spinner? = null
    private var patientAgeTypeAdapter: ArrayAdapter<*>? = null
    private var timeFormat = 0
    private var appUtilities: AppUtilities? = null
    private var settingViewModel: SettingViewModel? = null
    private var flagFirst = 0
    private var mainActivityViewModel: MainActivityViewModel? = null
    private var noAccessMessageLayout: RelativeLayout? = null
    private var noAccessMessageTextView: TextView? = null
    private var deleteAccountTv: TextView? = null
    private var deleteAccountBtn: Button? = null
    private var globalApiCall: ApiGetPostMethodCalls? = null
    private var sharedPref: SharedPref? = null
    private lateinit var dialog: Dialog
    private lateinit var commonViewModel: CommonViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        personalLayout = findViewById(R.id.settingPersonalLayout)
        profLayout = findViewById(R.id.settingProfLayout)
        financeLayout = findViewById(R.id.settingFinanceLayout)
        timeBlockLayout = findViewById(R.id.settingTimeBloclLayout)
        recordLayout = findViewById(R.id.settingRecordLayout)
        notification_setting_layout = findViewById(R.id.notification_setting_layout)
        apptRemindLayout = findViewById(R.id.settingApptReminderLayout)
        serviceRecycleView = findViewById(R.id.settingServiceRecycleView)
        settingDocImage = findViewById(R.id.settingDocImage)
        settingDocName = findViewById(R.id.settingDocName)
        settingDocEmail = findViewById(R.id.settingDocEmail)
        settingDocPhone = findViewById(R.id.settingDocPhone)
        settingDocExp = findViewById(R.id.settingDocExp)
        settingDocQua = findViewById(R.id.settingDocQua)
        settingSetupText = findViewById(R.id.settingSetupText)
        socialProfileLayout = findViewById(R.id.socialProfileLayout)
        videoToolSettingSetupText = findViewById(R.id.videoToolSettingSetupText)
        videoToolSetupLayout = findViewById(R.id.videoToolSetupLayout)
        noExternalVideoSetupText = findViewById(R.id.noExternalVideoSetupText)
        videoToolOptionLayout = findViewById(R.id.videoToolOptionLayout)
        whiteCoatsBuildInLayout = findViewById(R.id.whiteCoatsBuildInLayout)
        WhatAppLayout = findViewById(R.id.WhatAppLayout)
        zoomLayout = findViewById(R.id.zoomLayout)
        googleMeetLayout = findViewById(R.id.googleMeetLayout)
        otherLayout = findViewById(R.id.otherLayout)
        noServiceTextMsg = findViewById(R.id.noServiceTextMsg)
        profileGuidePt = findViewById(R.id.settingsProfileGuidePt)
        settingHome = findViewById(R.id.settingHome)
        noAccessMessageTextView = findViewById(R.id.noAccessMessageTextView)
        noAccessMessageLayout = findViewById(R.id.noAccessMessageLayout)
        deleteAccountTv = findViewById(R.id.delete_account_tv)
        deleteAccountBtn = findViewById(R.id.delete_account_btn)
        mContext = applicationContext
        commonViewModel = ViewModelProvider(this@SettingsActivity)[CommonViewModel::class.java]
        appPreference = applicationContext.getSharedPreferences(ApiUrls.appSharedPref, MODE_PRIVATE)
        sharedPreferencesTimeFormat =
            applicationContext.getSharedPreferences("TimeFormatPreference", MODE_PRIVATE)
        appDatabaseManager = AppDatabaseManager(this)
        settingServiceModelList = ArrayList()
        appUtilities = AppUtilities()
        settingViewModel = ViewModelProvider(this).get(SettingViewModel::class.java)
        settingViewModel!!.init()
        mainActivityViewModel = ViewModelProvider(this).get(
            MainActivityViewModel::class.java
        )
        mainActivityViewModel!!.init()
        globalApiCall = ApiGetPostMethodCalls()
        if (sharedPref == null) {
            sharedPref = SharedPref(this)
        }
        timeFormatSpinner = findViewById(R.id.timeFormatSpinner)
        try {
            ZohoSalesIQ.Tracking.setPageTitle("Settings Page")
        } catch (e: Exception) {
            e.printStackTrace()
        }

        showGuide(1)
        val deleteText =
            "Your profile and all the saved data will be permanently deleted. Read More"
        val spannableString = SpannableString(deleteText)
        val clickableSpan1: ClickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                val `in` = Intent(Intent.ACTION_VIEW)
                `in`.data =
                    Uri.parse("https://www.whitecoats.com/practiceplus-privacypolicyandterms")
                startActivity(`in`)
            }
        }
        val startIndex = deleteText.indexOf("Read More")
        val endIndex = startIndex + "Read More".length
        spannableString.setSpan(
            clickableSpan1,
            startIndex,
            endIndex,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        deleteAccountTv!!.text = spannableString
        deleteAccountTv!!.movementMethod = LinkMovementMethod.getInstance()
        deleteAccountBtn!!.setOnClickListener {
            val `in` = Intent(this@SettingsActivity, AccountDeleteActivity::class.java)
            startActivity(`in`)
        }
        settingSetupText!!.setOnClickListener {
            val intent = Intent(this@SettingsActivity, SettingsFormActivity::class.java)
            intent.putExtra("FormType", 7)
            intent.putExtra("Title", resources.getString(R.string.service_setup))
            startActivity(intent)
        }
        socialProfileLayout!!.setOnClickListener {
            val intent = Intent(this@SettingsActivity, GBPSharedLinkActivity::class.java)
            startActivity(intent)
        }
        videoToolSettingSetupText!!.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View) {
                videoSetupButtonClicked = true
                videoToolSettingDetails
            }
        })

        settingServiceListAdapter =
            SettingServiceListAdapter(
                mContext!!,
                settingServiceModelList!!,
                object : SettingServiceViewItemClickListener {
                    override fun onItemClick(v: View?, position: Int) {
                        if ((settingServiceModelList as ArrayList<SettingServiceModel>)[position].serviceName.equals(
                                "Instant Video",
                                ignoreCase = true
                            )
                        ) {
                            val intent =
                                Intent(this@SettingsActivity, SettingsFormActivity::class.java)
                            intent.putExtra("FormType", 8)
                            intent.putExtra(
                                "Title",
                                resources.getString(R.string.instant_video_setting)
                            )
                            intent.putExtra(
                                "Service_Product_ID",
                                (settingServiceModelList as ArrayList<SettingServiceModel>).get(
                                    position
                                ).service_product_ID
                            )
                            intent.putExtra(
                                "Dr_Service_ID",
                                (settingServiceModelList as ArrayList<SettingServiceModel>).get(
                                    position
                                ).dr_service_ID
                            )
                            intent.putExtra(
                                "Internal_Supersaas_ID",
                                (settingServiceModelList as ArrayList<SettingServiceModel>).get(
                                    position
                                ).clinic_Internal_Supersaas_ID
                            )
                            startActivity(intent)
                        } else if ((settingServiceModelList as ArrayList<SettingServiceModel>).get(
                                position
                            ).serviceName.equals(
                                "Chat",
                                ignoreCase = true
                            )
                        ) {
                            val intent =
                                Intent(this@SettingsActivity, SettingsFormActivity::class.java)
                            intent.putExtra("FormType", 9)
                            intent.putExtra("Title", resources.getString(R.string.chat_setting))
                            intent.putExtra(
                                "Service_Product_ID",
                                (settingServiceModelList as ArrayList<SettingServiceModel>).get(
                                    position
                                ).service_product_ID
                            )
                            intent.putExtra(
                                "Dr_Service_ID",
                                (settingServiceModelList as ArrayList<SettingServiceModel>).get(
                                    position
                                ).dr_service_ID
                            )
                            intent.putExtra(
                                "Internal_Supersaas_ID",
                                (settingServiceModelList as ArrayList<SettingServiceModel>).get(
                                    position
                                ).clinic_Internal_Supersaas_ID
                            )
                            startActivity(intent)
                        } else if ((settingServiceModelList as ArrayList<SettingServiceModel>).get(
                                position
                            ).serviceName.equals(
                                "Video",
                                ignoreCase = true
                            )
                        ) {
                            val intent =
                                Intent(this@SettingsActivity, SettingsFormActivity::class.java)
                            intent.putExtra("FormType", 10)
                            intent.putExtra("Title", resources.getString(R.string.video_setting))
                            intent.putExtra(
                                "Service_Product_ID",
                                (settingServiceModelList as ArrayList<SettingServiceModel>).get(
                                    position
                                ).service_product_ID
                            )
                            intent.putExtra(
                                "Dr_Service_ID",
                                (settingServiceModelList as ArrayList<SettingServiceModel>).get(
                                    position
                                ).dr_service_ID
                            )
                            intent.putExtra(
                                "Internal_Supersaas_ID",
                                (settingServiceModelList as ArrayList<SettingServiceModel>).get(
                                    position
                                ).clinic_Internal_Supersaas_ID
                            )
                            intent.putExtra("DoctorDetails", doctorDetailsJsonResponse)
                            startActivity(intent)
                        } else if ((settingServiceModelList as ArrayList<SettingServiceModel>).get(
                                position
                            ).serviceName.equals(
                                "Clinic 1",
                                ignoreCase = true
                            )
                        ) {
                            val intent =
                                Intent(this@SettingsActivity, SettingsFormActivity::class.java)
                            intent.putExtra("FormType", 11)
                            intent.putExtra(
                                "Title",
                                resources.getString(R.string.clinic_one_setting)
                            )
                            intent.putExtra(
                                "Service_Product_ID",
                                (settingServiceModelList as ArrayList<SettingServiceModel>).get(
                                    position
                                ).service_product_ID
                            )
                            intent.putExtra(
                                "Dr_Service_ID",
                                (settingServiceModelList as ArrayList<SettingServiceModel>).get(
                                    position
                                ).dr_service_ID
                            )
                            intent.putExtra(
                                "Internal_Supersaas_ID",
                                (settingServiceModelList as ArrayList<SettingServiceModel>).get(
                                    position
                                ).clinic_Internal_Supersaas_ID
                            )
                            startActivity(intent)
                        } else if ((settingServiceModelList as ArrayList<SettingServiceModel>).get(
                                position
                            ).serviceName.equals(
                                "Clinic 2",
                                ignoreCase = true
                            )
                        ) {
                            val intent =
                                Intent(this@SettingsActivity, SettingsFormActivity::class.java)
                            intent.putExtra("FormType", 11)
                            intent.putExtra(
                                "Title",
                                resources.getString(R.string.clinic_two_setting)
                            )
                            intent.putExtra(
                                "Service_Product_ID",
                                (settingServiceModelList as ArrayList<SettingServiceModel>).get(
                                    position
                                ).service_product_ID
                            )
                            intent.putExtra(
                                "Dr_Service_ID",
                                (settingServiceModelList as ArrayList<SettingServiceModel>).get(
                                    position
                                ).dr_service_ID
                            )
                            intent.putExtra(
                                "Internal_Supersaas_ID",
                                (settingServiceModelList as ArrayList<SettingServiceModel>).get(
                                    position
                                ).clinic_Internal_Supersaas_ID
                            )
                            startActivity(intent)
                        } else if ((settingServiceModelList as ArrayList<SettingServiceModel>).get(
                                position
                            ).serviceName.equals(
                                "Clinic 3",
                                ignoreCase = true
                            )
                        ) {
                            val intent =
                                Intent(this@SettingsActivity, SettingsFormActivity::class.java)
                            intent.putExtra("FormType", 11)
                            intent.putExtra(
                                "Title",
                                resources.getString(R.string.clinic_three_setting)
                            )
                            intent.putExtra(
                                "Service_Product_ID",
                                (settingServiceModelList as ArrayList<SettingServiceModel>).get(
                                    position
                                ).service_product_ID
                            )
                            intent.putExtra(
                                "Dr_Service_ID",
                                (settingServiceModelList as ArrayList<SettingServiceModel>).get(
                                    position
                                ).dr_service_ID
                            )
                            intent.putExtra(
                                "Internal_Supersaas_ID",
                                (settingServiceModelList as ArrayList<SettingServiceModel>).get(
                                    position
                                ).clinic_Internal_Supersaas_ID
                            )
                            startActivity(intent)
                        }
                    }
                })

        serviceRecycleView!!.layoutManager = LinearLayoutManager(
            this,
            LinearLayoutManager.HORIZONTAL,
            false
        )
        serviceRecycleView!!.itemAnimator = DefaultItemAnimator()
        serviceRecycleView!!.adapter = settingServiceListAdapter
        personalLayout!!.setOnClickListener {
            ZohoSalesIQ.Tracking.setCustomAction("Settings - Personal Details")
            val intent = Intent(this@SettingsActivity, SettingPersonalActivity::class.java)
            intent.putExtra("Title", resources.getString(R.string.personal))
            intent.putExtra("DoctorDetails", doctorDetailsJsonResponse)
            startActivity(intent)
        }
        profLayout!!.setOnClickListener {
            ZohoSalesIQ.Tracking.setCustomAction("Settings - Professional Details")
            val intent = Intent(this@SettingsActivity, SettingProfessionalActivity::class.java)
            intent.putExtra("Title", resources.getString(R.string.professional))
            startActivity(intent)
        }
        financeLayout!!.setOnClickListener {
            ZohoSalesIQ.Tracking.setCustomAction("Settings - Financial Details")
            val intent = Intent(applicationContext, PaymentSetupScreen::class.java)
            startActivity(intent)
        }
        timeBlockLayout!!.setOnClickListener {
            ZohoSalesIQ.Tracking.setCustomAction("Settings - Time Block")
            val intent = Intent(this@SettingsActivity, BlockTimeActivity::class.java)
            startActivity(intent)
        }
        if (sharedPref!!.isPrefExists("EMR")) {
            recordLayout!!.visibility = View.VISIBLE
        } else {
            recordLayout!!.visibility = View.GONE
        }
        recordLayout!!.setOnClickListener {
            ZohoSalesIQ.Tracking.setCustomAction("Settings - Records Preference")
            val intent = Intent(this@SettingsActivity, SettingRecordPreferencesActivity::class.java)
            intent.putExtra("Title", resources.getString(R.string.record_pref))
            startActivity(intent)
        }
        notification_setting_layout!!.setOnClickListener {
            ZohoSalesIQ.Tracking.setCustomAction("Settings - Notification Preference")
            val intent = Intent(this@SettingsActivity, NotificationSettingActivity::class.java)
            startActivity(intent)
        }
        apptRemindLayout!!.setOnClickListener {
            ZohoSalesIQ.Tracking.setCustomAction("Settings - Appt Reminder")
            val intent =
                Intent(this@SettingsActivity, SettingAppointmentReminderActivity::class.java)
            intent.putExtra("Title", resources.getString(R.string.appt_reminder))
            startActivity(intent)
        }
        settingHome!!.setOnClickListener {
            super@SettingsActivity.getOnBackPressedDispatcher().onBackPressed()
        }
        whiteCoatsBuildInLayout!!.setOnClickListener {
            val intent = Intent(this@SettingsActivity, VideoToolSettingActivity::class.java)
            startActivity(intent)
        }
        WhatAppLayout!!.setOnClickListener {
            val intent = Intent(this@SettingsActivity, VideoToolSettingActivity::class.java)
            startActivity(intent)
        }
        zoomLayout!!.setOnClickListener {
            val intent = Intent(this@SettingsActivity, VideoToolSettingActivity::class.java)
            startActivity(intent)
        }
        googleMeetLayout!!.setOnClickListener {
            val intent = Intent(this@SettingsActivity, VideoToolSettingActivity::class.java)
            startActivity(intent)
        }
        otherLayout!!.setOnClickListener {
            val intent = Intent(this@SettingsActivity, VideoToolSettingActivity::class.java)
            startActivity(intent)
        }

        val bkInstance = BrandKinesis.getBKInstance()
        bkInstance.getActivity(
            mContext,
            BKActivityTypes.ACTIVITY_TUTORIAL,
            "onProfileClicked",
            object : BKActivityCallback {
                override fun onActivityError(i: Int) {
                    Log.d("onActivityError", "onActivityError")
                }

                override fun onActivityCreated(bkActivityTypes: BKActivityTypes) {
                    BKUtilLogger.showDebugLog(
                        BKUtilLogger.BK_DEBUG,
                        "BK Tutorial onActivityCreated"
                    )
                    Log.d("onActivityCreated", "onActivityCreated")
                }

                override fun onActivityDestroyed(bkActivityTypes: BKActivityTypes) {
                    Log.d("onActivityDestroyed", "onActivityDestroyed")
                }

                override fun brandKinesisActivityPerformedActionWithParams(
                    bkActivityTypes: BKActivityTypes,
                    map: Map<String, Any>
                ) {
                    Log.d("PerformedAction", "brandKinesisActivityPerformedAction")
                }
            })
        patientAgeTypeAdapter = ArrayAdapter<Any?>(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            timeFormatSinnerArray
        )
        (patientAgeTypeAdapter as ArrayAdapter<*>).setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        timeFormatSpinner!!.adapter = patientAgeTypeAdapter
        flagFirst = if (appUtilities!!.timeFormatPreferences(applicationContext) == 12) {
            timeFormatSpinner!!.setSelection(0)
            0
        } else {
            timeFormatSpinner!!.setSelection(1)
            0
        }
        timeFormatSpinner!!.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?, view: View, position: Int, id: Long
            ) {
                if (flagFirst == 0) {
                    flagFirst = 1
                } else {
                    timeFormat = if (position == 0) {
                        12
                    } else {
                        24
                    }
                    saveTimeFormat(timeFormat)
                }

            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                //                        showToast("Spinner1: unselected");
            }
        }
    }

    fun setTimeFormat(timeFormatValue: Int) {
        // Creating an Editor object to edit(write to the file)
        val myEdit = sharedPreferencesTimeFormat!!.edit()

        // Storing the key and its value as the data fetched from edittext
        myEdit.putInt("timeFormat", timeFormatValue)

        // Once the changes have been made,
        // we need to commit to apply those changes made,
        // otherwise, it will throw an error
        myEdit.apply()
    }
    //                            otpLoading.dismiss();

    //getting language
    val doctorDetails: Unit
        @SuppressLint("SetTextI18n")
        get() {

            val url = ApiUrls.getDoctorDetailsTwo + "?id=" + ApiUrls.doctorId

            commonViewModel.commonViewModelCall(url, JSONObject(), Request.Method.GET).observe(
                this@SettingsActivity
            ) { result ->
                try {
                    //Process os success response
                    val responseObj = JSONObject(result)
                    if (responseObj.getInt("status_code") == 200) {
                        val response = responseObj.optJSONObject("response")
                        doctorDetailsJsonResponse = response?.toString()
                        var rootObj = response?.optJSONObject("response")
                        var userArr = rootObj?.getJSONArray("user")
                        if (userArr != null) {
                            rootObj = userArr.getJSONObject(0)
                        }
                        val specializationsArr = rootObj?.getJSONArray("specializations")
                        var specialStr = ""
                        if (specializationsArr != null) {
                            for (i in 0 until specializationsArr.length()) {
                                val tempobj = specializationsArr.getJSONObject(i)
                                specialStr += tempobj.getString("specialization")
                                if (i != specializationsArr.length() - 1) {
                                    specialStr += " | "
                                }
                            }
                        }
                        val qualificationsArr = rootObj?.getJSONArray("qualifications")
                        var qualificationsStr = ""
                        if (qualificationsArr != null) {
                            for (i in 0 until qualificationsArr.length()) {
                                val tempobj = qualificationsArr.getJSONObject(i)
                                qualificationsStr += tempobj.getString("qualification")
                                if (i != qualificationsArr.length() - 1) {
                                    qualificationsStr += " | "
                                }
                            }
                        }
                        if (rootObj != null) {
                            settingDocName!!.text = rootObj.getString("fname")
                        }
                        if (rootObj != null) {
                            settingDocPhone!!.text = rootObj.getString("phone")
                        }
                        if (rootObj != null) {
                            settingDocEmail!!.text = rootObj.getString("email")
                        }
                        if (rootObj != null) {
                            settingDocExp!!.text = rootObj.getString("exp") + " Exp."
                        }
                        settingDocQua!!.text = qualificationsStr
                        if (rootObj != null) {
                            getImageData(rootObj.getString("image_v2"))
                        }

                        //getting language
                        if (rootObj != null) {
                            userArr = rootObj.getJSONArray("languages")
                        }
                        var temp = ""
                        if (userArr != null) {
                            for (i in 0 until userArr.length()) {
                                val obj = userArr.getJSONObject(i)
                                temp = (temp + obj.getString("language").substring(0, 1).uppercase(
                                    Locale.getDefault()
                                )
                                        + obj.getString("language").substring(1) + ":")
                            }
                        }
                        val appDoctorManagers = appDatabaseManager!!.doctorData
                        if (appDoctorManagers.size == 0) {
                            val appDoctorManager = AppDoctorManager(
                                rootObj!!.getInt("id"),
                                rootObj.getString("fname"),
                                rootObj.getString("phone"),
                                rootObj.getInt("gender"),
                                rootObj.getString("email"),
                                rootObj.getString("exp"),
                                temp
                            )
                            appDatabaseManager!!.addDoctor(appDoctorManager)
                        }

                    } else {
                        errorHandler(this@SettingsActivity, result)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }


        }

    fun getImageData(imgURL: String) {
        val queue = Volley.newRequestQueue(this)

        // Retrieves an image specified by the URL, displays it clinicplus the UI.
        val request = ImageRequest(
            imgURL,
            { bitmap -> settingDocImage!!.setImageBitmap(bitmap) }, 0, 0, null
        ) { settingDocImage!!.setImageResource(R.mipmap.ic_launcher) }
        // Access the RequestQueue through your singleton class.
        queue.add(request)
    }

    val settingServiceSetupDetails: Unit
        @SuppressLint("NotifyDataSetChanged")
        get() {
            val url = ApiUrls.getAllDoctorServices


            commonViewModel.commonViewModelCall(url, JSONObject(), Request.Method.GET).observe(
                this@SettingsActivity
            ) { result ->
                try {
                    //Process os success response
                    dialog.dismiss()
                    val responseObj = JSONObject(result)
                    if (responseObj.getInt("status_code") == 200) {
                        val response = responseObj.optJSONObject("response")
                        settingServiceModelList!!.clear()
                        rootObjSettingService = response?.optJSONObject("response")
                        val docSettingsArr: JSONArray
                        if (rootObjSettingService != null) {
                            docSettingsArr = rootObjSettingService!!.getJSONArray("docSettings")
                            if (docSettingsArr.length() == 0) {
                                noServiceTextMsg!!.visibility = View.VISIBLE
                            } else {
                                noServiceTextMsg!!.visibility = View.GONE
                                for (i in 0 until docSettingsArr.length()) {
                                    val serviceObj = docSettingsArr.getJSONObject(i)
                                    val serviceName = serviceObj.getString("name")
                                    if (serviceName.equals("Clinic", ignoreCase = true)) {
                                        try {
                                            val clinicArray = serviceObj.getJSONArray("Clinic")
                                            val len = clinicArray.length()
                                            var noOfClinicServ = 1
                                            for (j in 0 until len) {
                                                val clinicObj = clinicArray.getJSONObject(j)
                                                if (clinicObj.has("id")) {
                                                    val temp = SettingServiceModel()
                                                    temp.serviceName = "Clinic $noOfClinicServ"
                                                    temp.service_product_ID =
                                                        clinicObj.getInt("id")
                                                    temp.dr_service_ID =
                                                        clinicObj.getInt("dr_service_id")
                                                    temp.clinic_Internal_Supersaas_ID =
                                                        clinicObj.getJSONObject("prod_service")
                                                            .getInt("internal_supersaas_id")
                                                    settingServiceModelList!!.add(temp)
                                                    noOfClinicServ++
                                                }
                                            }
                                        } catch (e: Exception) {
                                            e.printStackTrace()
                                        }
                                    } else if (serviceName.equals("Video", ignoreCase = true)) {
                                        try {
                                            val videoObj = serviceObj.getJSONObject("Video")
                                            if (videoObj.has("id")) {
                                                val temp = SettingServiceModel()
                                                temp.serviceName = serviceName
                                                temp.service_product_ID = videoObj.getInt("id")
                                                temp.dr_service_ID =
                                                    videoObj.getInt("dr_service_id")
                                                temp.clinic_Internal_Supersaas_ID =
                                                    videoObj.getJSONObject("prod_service")
                                                        .getInt("internal_supersaas_id")
                                                settingServiceModelList!!.add(temp)
                                                videoToolSettingSetupText!!.visibility =
                                                    View.VISIBLE
                                            }
                                        } catch (e: Exception) {
                                            e.printStackTrace()
                                        }
                                    } else if (serviceName.equals("Chat", ignoreCase = true)) {
                                        try {
                                            val chatObj = serviceObj.getJSONObject("Chat")
                                            if (chatObj.has("id")) {
                                                val temp = SettingServiceModel()
                                                temp.serviceName = serviceName
                                                temp.service_product_ID = chatObj.getInt("id")
                                                temp.dr_service_ID =
                                                    chatObj.getInt("dr_service_id")
                                                temp.clinic_Internal_Supersaas_ID = 0
                                                settingServiceModelList!!.add(temp)
                                            }
                                        } catch (e: Exception) {
                                            e.printStackTrace()
                                        }
                                    } else if (serviceName.equals(
                                            "Instant Video",
                                            ignoreCase = true
                                        )
                                    ) {
                                        try {
                                            val instantVideo =
                                                serviceObj.getJSONObject("Instant Video")
                                            if (instantVideo.has("id")) {
                                                val temp = SettingServiceModel()
                                                temp.serviceName = serviceName
                                                temp.service_product_ID =
                                                    instantVideo.getInt("id")
                                                temp.dr_service_ID =
                                                    instantVideo.getInt("dr_service_id")
                                                temp.clinic_Internal_Supersaas_ID = 0
                                                settingServiceModelList!!.add(temp)
                                            }
                                        } catch (e: Exception) {
                                            e.printStackTrace()
                                        }
                                    }
                                }
                            }
                        }
                        settingServiceListAdapter!!.notifyDataSetChanged()

                    } else {
                        dialog.dismiss()
                        errorHandler(this@SettingsActivity, result)
                    }
                } catch (e: Exception) {
                    dialog.dismiss()
                    e.printStackTrace()
                }
            }

        }

    // do what you want
    val videoToolSettingDetails: Unit
        get() {
            val url = ApiUrls.getVideoToolSettingDetails

            commonViewModel.commonViewModelCall(url, JSONObject(), Request.Method.GET).observe(
                this@SettingsActivity
            ) { result ->
                try {
                    //Process os success response
                    val responseObj = JSONObject(result)
                    if (responseObj.getInt("status_code") == 200) {
                        val response = responseObj.optJSONObject("response")
                        if (videoSetupButtonClicked) {
                            videoSetupButtonClicked = false
                            val intent = Intent(
                                this@SettingsActivity,
                                VideoToolSettingActivity::class.java
                            )
                            startActivity(intent)
                        } else {
                            if (response != null) {
                                videoToolSettingDetailsObject = response.optJSONObject("response")
                            }
                            val aObjIsShowSection =
                                videoToolSettingDetailsObject!!.getInt("is_show_section")
                            if (aObjIsShowSection == 0) {
                                videoToolSetupLayout!!.visibility = View.GONE
                            } else {
                                videoToolSetupLayout!!.visibility = View.VISIBLE
                            }
                            val aObj = videoToolSettingDetailsObject!!.get("preference")
                            if (aObj is Int) {
                                // do what you want
                                if (videoToolSettingDetailsObject!!.getInt("preference") == 0) {
                                    noExternalVideoSetupText!!.visibility = View.VISIBLE
                                    videoToolOptionLayout!!.visibility = View.GONE
                                }
                            } else {
                                noExternalVideoSetupText!!.visibility = View.GONE
                                videoToolOptionLayout!!.visibility = View.VISIBLE
                                val toolPreferenceObject =
                                    videoToolSettingDetailsObject!!.getJSONObject("preference")
                                if (toolPreferenceObject.getInt("tool_preference") == 1) {
                                    whiteCoatsBuildInLayout!!.visibility = View.VISIBLE
                                    WhatAppLayout!!.visibility = View.GONE
                                    zoomLayout!!.visibility = View.GONE
                                    googleMeetLayout!!.visibility = View.GONE
                                    otherLayout!!.visibility = View.GONE
                                } else if (toolPreferenceObject.getInt("tool_preference") == 2) {
                                    whiteCoatsBuildInLayout!!.visibility = View.GONE
                                    WhatAppLayout!!.visibility = View.VISIBLE
                                    zoomLayout!!.visibility = View.GONE
                                    googleMeetLayout!!.visibility = View.GONE
                                    otherLayout!!.visibility = View.GONE
                                } else if (toolPreferenceObject.getInt("tool_preference") == 3) {
                                    whiteCoatsBuildInLayout!!.visibility = View.GONE
                                    WhatAppLayout!!.visibility = View.GONE
                                    zoomLayout!!.visibility = View.VISIBLE
                                    googleMeetLayout!!.visibility = View.GONE
                                    otherLayout!!.visibility = View.GONE
                                } else if (toolPreferenceObject.getInt("tool_preference") == 4) {
                                    whiteCoatsBuildInLayout!!.visibility = View.GONE
                                    WhatAppLayout!!.visibility = View.GONE
                                    zoomLayout!!.visibility = View.GONE
                                    googleMeetLayout!!.visibility = View.VISIBLE
                                    otherLayout!!.visibility = View.GONE
                                } else if (toolPreferenceObject.getInt("tool_preference") == 5) {
                                    whiteCoatsBuildInLayout!!.visibility = View.GONE
                                    WhatAppLayout!!.visibility = View.GONE
                                    zoomLayout!!.visibility = View.GONE
                                    googleMeetLayout!!.visibility = View.GONE
                                    otherLayout!!.visibility = View.VISIBLE
                                }
                            }
                        }

                    } else {
                        errorHandler(this@SettingsActivity, result)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

        }

    val settingClinicDetails: Unit
        get() {

            showCustomProgressAlertDialog(
                resources.getString(R.string.fetching),
                resources.getString(R.string.wait_while_we_fetching)
            )
            val url = ApiUrls.getDoctorSettings


            commonViewModel.commonViewModelCall(url, JSONObject(), Request.Method.GET).observe(
                this@SettingsActivity
            ) { result ->
                try {
                    //Process os success response
                    val responseObj = JSONObject(result)
                    if (responseObj.getInt("status_code") == 200) {
                        val response = responseObj.optJSONObject("response")
                        settingServiceClinicSize = 0
                        val rootObj = response?.optJSONObject("response")
                        val clinicServices: JSONArray?
                        if (rootObj != null) {
                            clinicServices = rootObj.getJSONArray("enabledServices")
                            for (i in 0 until clinicServices!!.length()) {
                                val services =
                                    clinicServices.getJSONObject(i).getInt("service_id")
                                if (services == 3) {
                                    settingServiceClinicSize++
                                }
                            }
                        }
                        settingServiceSetupDetails

                    } else {
                        errorHandler(this@SettingsActivity, result)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

        }
    val doctorService: Unit
        get() {
            val url = ApiUrls.getDashBoardSummaryDetails + "?duration=" + "Today"

            commonViewModel.commonViewModelCall(url, JSONObject(), Request.Method.GET).observe(
                this@SettingsActivity
            ) { result ->
                try {
                    //Process os success response
                    val responseObj = JSONObject(result)
                    if (responseObj.getInt("status_code") == 200) {
                        val response = responseObj.optJSONObject("response")
                        val summaryArr = response?.getJSONArray("response")
                        for (i in 0 until summaryArr!!.length()) {
                            val tempobj = summaryArr.getJSONObject(i)
                            val summaryServiceObj = tempobj.getJSONObject("prod_service")
                            if (summaryServiceObj.getInt("service_id") == 3) {
                                serviceIdClinicCount++
                            }
                        }
                        settingServiceSetupDetails

                    } else {
                        errorHandler(this@SettingsActivity, result)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

        }

    override fun onResume() {
        super.onResume()
        doctorDetails
        settingClinicDetails
        videoToolSettingDetails
        timeFormatPreferences
    }

    private fun showGuide(section: Int) {
        when (section) {
            1 -> if (!appPreference!!.getBoolean("Settings", false)) {

                MaterialIntroView.Builder(this)
                    .enableDotAnimation(true)
                    .enableIcon(false)
                    .dismissOnTouch(true)
                    .setFocusGravity(FocusGravity.CENTER)
                    .setFocusType(Focus.NORMAL)
                    .setDelayMillis(50)
                    .enableFadeAnimation(true)
                    .setInfoText("Update and add your personal, professional and financial details")
                    .setShape(ShapeType.RECTANGLE)
                    .setTarget(profileGuidePt)
                    .setUsageId("intro_settingUpdate") //THIS SHOULD BE UNIQUE ID
                    .setListener {
                        showGuide(2)
                        val editor = appPreference!!.edit()
                        editor.putBoolean("Settings", true)
                        editor.apply()
                    }
                    .show()
            }
            2 ->
                MaterialIntroView.Builder(this)
                    .enableDotAnimation(true)
                    .enableIcon(false)
                    .dismissOnTouch(true)
                    .setFocusGravity(FocusGravity.CENTER)
                    .setFocusType(Focus.NORMAL)
                    .setDelayMillis(50)
                    .enableFadeAnimation(true)
                    .setInfoText("Click here to setup a new practice (service) via clinic, video or chat")
                    .setShape(ShapeType.RECTANGLE)
                    .setTarget(settingSetupText)
                    .setUsageId("intro_settingSetupText") //THIS SHOULD BE UNIQUE ID
                    .setListener { showGuide(3) }
                    .show()
            3 ->
                MaterialIntroView.Builder(this)
                    .enableDotAnimation(true)
                    .enableIcon(false)
                    .dismissOnTouch(true)
                    .setFocusGravity(FocusGravity.CENTER)
                    .setFocusType(Focus.NORMAL)
                    .setDelayMillis(50)
                    .enableFadeAnimation(true)
                    .setInfoText("Manage your services once they are setup")
                    .setShape(ShapeType.RECTANGLE)
                    .setTarget(serviceRecycleView)
                    .setUsageId("intro_serviceRecycleView") //THIS SHOULD BE UNIQUE ID
                    .setListener {
                    }
                    .show()
        }
    }

    fun trimMessage(json: String?, key: String?): String? {
        val trimmedString: String? = try {
            val obj = json?.let { JSONObject(it) }
            obj?.getString(key)
        } catch (e: JSONException) {
            e.printStackTrace()
            return null
        }
        return trimmedString
    }

    fun saveTimeFormat(timeFormatValue: Int) {

        showCustomProgressAlertDialog(
            resources.getString(R.string.please_wait),
            resources.getString(R.string.changing_time_format)
        )

        var jsonValue = JSONObject()
        try {
            jsonValue = JSONObject()
            jsonValue.put("time_pref", timeFormatValue)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        settingViewModel!!.saveTimeFormat(this@SettingsActivity, jsonValue)
            .observe(this@SettingsActivity) { value ->
                dialog.dismiss()
                try {
                    val jsonObject = JSONObject(value)
                    if (jsonObject.getInt("status_code") == 200) {
                        if (jsonObject.getJSONObject("response").getInt("response") == 1) {
                            Toast.makeText(
                                this@SettingsActivity,
                                "Time format has been changed successfully",
                                Toast.LENGTH_SHORT
                            ).show()
                            setTimeFormat(timeFormatValue)
                        }
                    } else {
                        dialog.dismiss()
                        if (value != null) {
                            errorHandler(this@SettingsActivity, value)
                        }
                    }
                } catch (e: JSONException) {
                    dialog.dismiss()
                    e.printStackTrace()
                }
            }
    }

    //get time format preferences value
    val timeFormatPreferences: Unit
        get() {
            mainActivityViewModel!!.getTimePref(this).observe(this, object : Observer<String?> {
                override fun onChanged(value: String?) {
                    val response: JSONObject?
                    try {
                        response = JSONObject(value)
                        if (response.getInt("status_code") == 200) {
                            val timeFormatPreferencesValue =
                                response.getJSONObject("response").getJSONObject("response")
                                    .getInt("time_pref")
                            val isHaveUpdateAccess =
                                response.getJSONObject("response").getJSONObject("response")
                                    .getInt("is_have_update_access")
                            if (isHaveUpdateAccess == 1) {
                                noAccessMessageLayout!!.visibility = View.GONE
                                setTimeFormat(timeFormatPreferencesValue)
                            } else {
                                noAccessMessageTextView!!.text =
                                    response.getJSONObject("response").getJSONObject("response")
                                        .getString("update_message")
                                noAccessMessageLayout!!.visibility = View.VISIBLE
                            }
                        } else {
                            if (value != null) {
                                errorHandler(this@SettingsActivity, value)
                            }
                        }
                    } catch (e: JSONException) {
                        e.printStackTrace()
                        Log.d("timePrefIntError", "timePrefIntError$e")
                    }
                }
            })
        }

    companion object {
        @SuppressLint("StaticFieldLeak")
        private var mContext: Context? = null

        @SuppressLint("StaticFieldLeak")
        @JvmField
        var settingDocName: TextView? = null

        @SuppressLint("StaticFieldLeak")
        @JvmField
        var settingDocEmail: TextView? = null

        @SuppressLint("StaticFieldLeak")
        @JvmField
        var settingDocPhone: TextView? = null

        @SuppressLint("StaticFieldLeak")
        var settingDocExp: TextView? = null

        @SuppressLint("StaticFieldLeak")
        var settingDocQua: TextView? = null
        var settingServiceClinicSize = 0
        var videoToolSettingDetailsObject: JSONObject? = null
    }

    fun showCustomProgressAlertDialog(
        title: String?,
        progressVal: String?

    ) {
        val builder = AlertDialog.Builder(this@SettingsActivity)
        builder.setTitle(title)
        builder.setCancelable(false)
        val customLayout =
            LayoutInflater.from(this@SettingsActivity).inflate(R.layout.custom_progress_bar, null)
        builder.setView(customLayout)
        dialog = builder.create()
        val tvBody = customLayout.findViewById<TextView>(R.id.tv_value)
        tvBody.text = progressVal
        dialog.show()
    }

}