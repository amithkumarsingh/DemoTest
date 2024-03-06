package com.whitecoats.clinicplus.activities

import android.app.Dialog
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.Window
import android.widget.*
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.whitecoats.adapter.HomeSharedRecordsListAdapter
import com.whitecoats.clinicplus.*
import com.whitecoats.clinicplus.adapters.DashBoardSharedRecordsListAdapter
import com.whitecoats.clinicplus.adapters.EMRMoreInfoListAdapter
import com.whitecoats.clinicplus.apis.ApiUrls
import com.whitecoats.clinicplus.interfaces.EMRMoreInfoClickListener
import com.whitecoats.clinicplus.models.MoreInfoListModel
import com.whitecoats.clinicplus.utils.ErrorHandlerClass.errorHandler
import com.whitecoats.clinicplus.viewmodels.EMRCreateRecordsFormViewModel
import com.zoho.salesiqembed.ZohoSalesIQ
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class EMRCaseHistoryMoreInfoActivity() : AppCompatActivity() {
    private var recordDetailObjStr: String? = null
    private var recordId: String? = null
    private var moreInfoCard: LinearLayout? = null
    private var catId: String? = null
    private var toolbar: Toolbar? = null
    private var apiCall: PatientRecordsApi? = null
    var rl: RelativeLayout? = null
    private var moreInfoRecycleView: RecyclerView? = null
    var moreInfoModelList: MutableList<MoreInfoListModel>? = null
    private var emrMoreInfoListAdapter: EMRMoreInfoListAdapter? = null
    private var mLayoutManager: RecyclerView.LayoutManager? = null
    private var interactionMode: String? = null
    private var interactionDateTime: String? = null
    private var recordAddedOn: String? = null
    private var recordDate: TextView? = null
    private var interactionDate: TextView? = null
    private var appUtilities: AppUtilities? = null
    private var multiRecordPosition = 0
    private var appUtils: AppUtilities? = null
    private var rlButtonsParent: RelativeLayout? = null
    private var btnDelete: Button? = null
    private var btnEdit: Button? = null
    private var catName: String? = ""
    private var cardButtons: CardView? = null
    private var canDeleteEditRecords = false
    private var canDeleteEditWrittenNotes = false
    private var encounterId = 0
    private var episodeId = 0
    private var patientId = 0
    private var ID = 0
    private var PatientName: String? = ""
    private var launchEditRecordResults: ActivityResultLauncher<Intent>? = null
    private var myClinicGlobalClass: MyClinicGlobalClass? = null
    private var emrCreateRecordsFormViewModel: EMRCreateRecordsFormViewModel? = null
    private val LAUNCH_SECOND_ACTIVITY = 1001
    private var recordsOBj: JSONObject? = null
    private var categoryType: String? = ""
    private var dynamicEncounterDataIds: String? = null
    private var mainRecordsId = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_emr_case_history_more_info)
        apiCall = PatientRecordsApi()
        moreInfoCard = findViewById(R.id.recordMoreList)
        rlButtonsParent = findViewById(R.id.rl_buttons_parent)
        btnDelete = findViewById(R.id.btn_Delete)
        btnEdit = findViewById(R.id.btn_Edit)
        cardButtons = findViewById(R.id.card_buttons)
        myClinicGlobalClass = applicationContext as MyClinicGlobalClass
        emrCreateRecordsFormViewModel = ViewModelProvider(this).get(
            EMRCreateRecordsFormViewModel::class.java
        )
        emrCreateRecordsFormViewModel!!.init()
        catId = intent.getStringExtra("CatId")
        recordDetailObjStr = intent.getStringExtra("RecordDetail")
        recordId = intent.getStringExtra("RecordId")
        multiRecordPosition = intent.getIntExtra("multiRecordPosition", 0)
        if (intent.hasExtra("categoryType")) {
            categoryType = intent.getStringExtra("categoryType")
        }
        interactionMode = intent.getStringExtra("mode")
        interactionDateTime = intent.getStringExtra("interactionDate")
        recordAddedOn = intent.getStringExtra("addedOnDate")
        if (intent.hasExtra("canDeleteEditRecords")) {
            canDeleteEditRecords = intent.getBooleanExtra("canDeleteEditRecords", false)
        }
        if (intent.hasExtra("canDeleteEditWrittenNotes")) {
            canDeleteEditWrittenNotes = intent.getBooleanExtra("canDeleteEditWrittenNotes", false)
        }
        if (intent.hasExtra("PatientName")) {
            PatientName = intent.getStringExtra("PatientName")
        }
        if (intent.hasExtra("dynamicEncounterDataIds")) {
            dynamicEncounterDataIds = intent.getStringExtra("dynamicEncounterDataIds")
        }
        //getting the toolbar
        toolbar = findViewById(R.id.recordMoreToolbar)
        catName = intent.getStringExtra("CatName")
        toolbar!!.title = catName
        toolbar!!.setTitleTextColor(resources.getColor(R.color.colorWhite))
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        val upArrow = resources.getDrawable(R.drawable.ic_arrow_back)
        upArrow.setColorFilter(resources.getColor(R.color.colorWhite), PorterDuff.Mode.SRC_ATOP)
        supportActionBar!!.setHomeAsUpIndicator(upArrow)
        recordDate = findViewById(R.id.recordDate)
        interactionDate = findViewById(R.id.interactionDate)
        mLayoutManager = LinearLayoutManager(applicationContext)
        moreInfoRecycleView = findViewById(R.id.moreInfoRecycleView)
        moreInfoModelList = ArrayList()
        appUtilities = AppUtilities()
        appUtils = AppUtilities()
        if (recordAddedOn != null) {
            val currentStringStart: String = recordAddedOn as String
            val separatedDate =
                currentStringStart.split(" ".toRegex()).dropLastWhile { it.isEmpty() }
                    .toTypedArray()
            val createDate = separatedDate[0]
            val date = appUtilities!!.changeDateFormat("yyyy-MM-dd", "dd MMM, yyyy", createDate)
            val createTime = separatedDate[1].substring(0, separatedDate[1].length - 3)
            recordDate?.text = "Added on $date"
        }
        if (interactionDateTime != null) {
            val interactionCreatedOn: String = interactionDateTime as String
            val separatedInteractionDate =
                interactionCreatedOn.split(" ".toRegex()).dropLastWhile { it.isEmpty() }
                    .toTypedArray()
            val createInteractionDate = separatedInteractionDate[0]
            val interactionDateTimeString =
                appUtilities!!.changeDateFormat("yyyy-MM-dd", "dd MMM, yy", createInteractionDate)
            val createdInteractionTime =
                separatedInteractionDate[1].substring(0, separatedInteractionDate[1].length - 3)
            interactionDate?.setText(
                interactionMode + " on " + interactionDateTimeString + " @ " + appUtils!!.formatTimeBasedOnPreference(
                    this@EMRCaseHistoryMoreInfoActivity,
                    "HH:mm",
                    createdInteractionTime
                )
            )
        }
        launchEditRecordResults =
            registerForActivityResult<Intent, ActivityResult>(
                ActivityResultContracts.StartActivityForResult(),
                ActivityResultCallback<ActivityResult> { result -> //Request code 1
                    val data = result.data
                    val resultCode = result.resultCode
                    if (resultCode == RESULT_OK) {
                        if (data != null) {
                            val resultNew = data.getStringExtra("editRecord")
                            if (resultNew.equals("editRecord", ignoreCase = true)) {
                                finish()
                            }
                        }
                    }
                })
        emrMoreInfoListAdapter = EMRMoreInfoListAdapter(
            applicationContext,
            moreInfoModelList as ArrayList<MoreInfoListModel>,
            this@EMRCaseHistoryMoreInfoActivity,
            object : EMRMoreInfoClickListener {
                override fun onItemClick(
                    v: View,
                    dataPosition: Int,
                    fileUrl: String,
                    clickAction: String
                ) {
                    if (clickAction.equals("FILE_URL", ignoreCase = true)) {
                        getFileFromUrl(fileUrl)
                    }
                }
            })
        if (catName.equals("HandWritten Note", ignoreCase = true)) {
            if (canDeleteEditWrittenNotes) {
                cardButtons?.visibility = View.VISIBLE
            } else {
                cardButtons?.visibility = View.GONE
            }
        } else {
            if (canDeleteEditRecords) {
                cardButtons?.visibility = View.VISIBLE
            } else {
                cardButtons?.visibility = View.GONE
            }
        }
        moreInfoRecycleView?.layoutManager = mLayoutManager
        moreInfoRecycleView?.itemAnimator = DefaultItemAnimator()
        moreInfoRecycleView?.adapter = emrMoreInfoListAdapter
        ZohoSalesIQ.Tracking.setPageTitle("EMR Shared Records More Info Page")
        if (intent.getStringExtra("CatName").equals("HandWritten Note", ignoreCase = true)) {
            try {
                val handWrittenRecordArr = JSONArray(recordDetailObjStr)
                for (i in 0..4) {
                    val fieldObj = handWrittenRecordArr.getJSONObject(0)
                    if (i == 0) {
                        val moreInfoModel = MoreInfoListModel()
                        if (fieldObj.getString("file_url") != null) {
                            moreInfoModel.fieldName = "Attachment"
                            moreInfoModel.fieldValue = resources.getString(R.string.view_attachment)
                            moreInfoModel.fileUrl = fieldObj.getString("file_url")
                        } else {
                            moreInfoModel.fieldName = "Attachment"
                            moreInfoModel.fieldValue = resources.getString(R.string.no_attachment)
                            moreInfoModel.fileUrl = fieldObj.getString("file_url")
                        }
                        (moreInfoModelList as ArrayList<MoreInfoListModel>).add(moreInfoModel)
                    }
                    if (i == 1) {
                        val moreInfoModel = MoreInfoListModel()
                        moreInfoModel.fieldName = "Medicine Prescription"
                        if (fieldObj.getInt("has_med_prescription") == 0) {
                            moreInfoModel.fieldValue = "No"
                        } else {
                            moreInfoModel.fieldValue = "Yes"
                        }
                        (moreInfoModelList as ArrayList<MoreInfoListModel>).add(moreInfoModel)
                    }
                    if (i == 2) {
                        val moreInfoModel = MoreInfoListModel()
                        moreInfoModel.fieldName = "Prescription Valid Till"
                        if (!fieldObj.isNull("med_prescription_valid_till")) {
                            moreInfoModel.fieldValue = AppUtilities().changeDateFormat(
                                "yyyy-MM-dd HH:mm:ss",
                                "dd/MM/yyyy",
                                fieldObj.getString("med_prescription_valid_till")
                            )
                        } else {
                            moreInfoModel.fieldValue = "-"
                        }
                        (moreInfoModelList as ArrayList<MoreInfoListModel>).add(moreInfoModel)
                    }
                    if (i == 3) {
                        val moreInfoModel = MoreInfoListModel()
                        moreInfoModel.fieldName = "Test Prescription"
                        if (fieldObj.getInt("has_test_prescription") == 0) {
                            moreInfoModel.fieldValue = "No"
                        } else {
                            moreInfoModel.fieldValue = "Yes"
                        }
                        (moreInfoModelList as ArrayList<MoreInfoListModel>).add(moreInfoModel)
                    }
                    if (i == 4) {
                        val moreInfoModel = MoreInfoListModel()
                        moreInfoModel.fieldName = "Description"
                        moreInfoModel.fieldValue = fieldObj.getString("description")
                        (moreInfoModelList as ArrayList<MoreInfoListModel>).add(moreInfoModel)
                    }
                }
            } catch (e: JSONException) {
                e.stackTrace
            }
        } else if (intent.getStringExtra("CatName").equals("Symptoms", ignoreCase = true)) {
            try {
                val handWrittenRecordArr = JSONArray(recordDetailObjStr)
                for (i in 0..3) {
                    val fieldObj = handWrittenRecordArr.getJSONObject(0)
                    if (i == 0) {
                        val moreInfoModel = MoreInfoListModel()
                        moreInfoModel.fieldName = "Name"
                        moreInfoModel.fieldValue = fieldObj.getString("symptom_name")
                        (moreInfoModelList as ArrayList<MoreInfoListModel>).add(moreInfoModel)
                    }
                    if (i == 1) {
                        val moreInfoModel = MoreInfoListModel()
                        moreInfoModel.fieldName = "First Reported On"
                        val date = appUtilities!!.changeDateFormat(
                            "yyyy-MM-dd hh:mm:ss",
                            "dd MMM, yy",
                            fieldObj.getString("first_reported_on")
                        )
                        moreInfoModel.fieldValue = date
                        (moreInfoModelList as ArrayList<MoreInfoListModel>).add(moreInfoModel)
                    }
                    if (i == 2) {
                        val moreInfoModel = MoreInfoListModel()
                        moreInfoModel.fieldName = "Status"
                        moreInfoModel.fieldValue = fieldObj.getString("symptom_status")
                        (moreInfoModelList as ArrayList<MoreInfoListModel>).add(moreInfoModel)
                    }
                    if (i == 3) {
                        val moreInfoModel = MoreInfoListModel()
                        moreInfoModel.fieldName = "Description"
                        moreInfoModel.fieldValue = fieldObj.getString("symptom_description")
                        (moreInfoModelList as ArrayList<MoreInfoListModel>).add(moreInfoModel)
                    }
                }
            } catch (e: JSONException) {
                e.stackTrace
            }
        } else if (intent.getStringExtra("CatName").equals("Diagnosis", ignoreCase = true)) {
            try {
                val handWrittenRecordArr = JSONArray(recordDetailObjStr)
                for (i in 0..3) {
                    val fieldObj = handWrittenRecordArr.getJSONObject(0)
                    if (i == 0) {
                        val moreInfoModel = MoreInfoListModel()
                        moreInfoModel.fieldName = "Name"
                        moreInfoModel.fieldValue = fieldObj.getString("diagnosis")
                        (moreInfoModelList as ArrayList<MoreInfoListModel>).add(moreInfoModel)
                    }
                    if (i == 1) {
                        val moreInfoModel = MoreInfoListModel()
                        moreInfoModel.fieldName = "Posited On"
                        val date = appUtilities!!.changeDateFormat(
                            "yyyy-MM-dd hh:mm:ss",
                            "dd MMM, yy",
                            fieldObj.getString("posited_on")
                        )
                        moreInfoModel.fieldValue = date
                        (moreInfoModelList as ArrayList<MoreInfoListModel>).add(moreInfoModel)
                    }
                    if (i == 2) {
                        val moreInfoModel = MoreInfoListModel()
                        moreInfoModel.fieldName = "Status"
                        moreInfoModel.fieldValue = fieldObj.getString("status")
                        (moreInfoModelList as ArrayList<MoreInfoListModel>).add(moreInfoModel)
                    }
                    if (i == 3) {
                        if (fieldObj.getString("status").equals("Tentative", ignoreCase = true)) {
                        } else {
                            val moreInfoModel = MoreInfoListModel()
                            moreInfoModel.fieldName = "Confirmed/Ruled Out On"
                            if (!fieldObj.getString("confirmed_ruledout_on")
                                    .equals("null", ignoreCase = true)
                            ) {
                                val date = appUtilities!!.changeDateFormat(
                                    "yyyy-MM-dd hh:mm:ss",
                                    "dd MMM, yy",
                                    fieldObj.getString("confirmed_ruledout_on")
                                )
                                moreInfoModel.fieldValue = date
                            } else {
                                moreInfoModel.fieldValue = "-"
                            }
                            (moreInfoModelList as ArrayList<MoreInfoListModel>).add(moreInfoModel)
                        }
                    }
                }
            } catch (e: JSONException) {
                e.stackTrace
            }
        } else if (intent.getStringExtra("CatName")
                .equals("Investigation Results", ignoreCase = true)
        ) {
            try {
                val handWrittenRecordArr = JSONArray(recordDetailObjStr)
                for (i in 0..4) {
                    val fieldObj = handWrittenRecordArr.getJSONObject(0)
                    if (i == 0) {
                        val moreInfoModel = MoreInfoListModel()
                        if (fieldObj.getString("file_url") != null) {
                            moreInfoModel.fieldName = "Attachment"
                            moreInfoModel.fieldValue = resources.getString(R.string.view_attachment)
                            moreInfoModel.fileUrl = fieldObj.getString("file_url")
                        } else {
                            moreInfoModel.fieldName = "Attachment"
                            moreInfoModel.fieldValue = resources.getString(R.string.no_attachment)
                            moreInfoModel.fileUrl = fieldObj.getString("file_url")
                        }
                        (moreInfoModelList as ArrayList<MoreInfoListModel>).add(moreInfoModel)
                    }
                    if (i == 1) {
                        val moreInfoModel = MoreInfoListModel()
                        moreInfoModel.fieldName = "Name"
                        moreInfoModel.fieldValue = fieldObj.getString("investigation_name")
                        (moreInfoModelList as ArrayList<MoreInfoListModel>).add(moreInfoModel)
                    }
                    if (i == 2) {
                        val moreInfoModel = MoreInfoListModel()
                        moreInfoModel.fieldName = "Parameter"
                        moreInfoModel.fieldValue = fieldObj.getString("parameter")
                        (moreInfoModelList as ArrayList<MoreInfoListModel>).add(moreInfoModel)
                    }
                    if (i == 3) {
                        val moreInfoModel = MoreInfoListModel()
                        moreInfoModel.fieldName = "Value"
                        moreInfoModel.fieldValue = fieldObj.getString("value")
                        (moreInfoModelList as ArrayList<MoreInfoListModel>).add(moreInfoModel)
                    }
                    if (i == 4) {
                        val moreInfoModel = MoreInfoListModel()
                        moreInfoModel.fieldName = "Notes"
                        moreInfoModel.fieldValue = fieldObj.getString("notes")
                        (moreInfoModelList as ArrayList<MoreInfoListModel>).add(moreInfoModel)
                    }
                }
            } catch (e: JSONException) {
                e.stackTrace
            }
        } else if (catId.equals("21", ignoreCase = true)) {
            val fieldDic = intent.getStringExtra("RecordFieldDic")
            try {
                val RecordArrData = JSONArray(recordDetailObjStr)
                val recordArrayObject = RecordArrData.getJSONObject(0)
                val RecordsArray = recordArrayObject.getJSONArray("records")
                if (RecordsArray.length() > 0) {
                    //for (int Rec = 0; Rec < RecordsArray.length(); Rec++) {
                    recordsOBj = RecordsArray.getJSONObject(multiRecordPosition)
                    for (i in 0..3) {
                        val fieldObj = RecordsArray.getJSONObject(multiRecordPosition)
                        if (i == 0) {
                            val moreInfoModel = MoreInfoListModel()
                            if (fieldObj.getString("url") != null) {
                                moreInfoModel.fieldName = "Attachment"
                                moreInfoModel.fieldValue =
                                    resources.getString(R.string.view_attachment)
                                moreInfoModel.fileUrl = fieldObj.getString("url")
                            } else {
                                moreInfoModel.fieldName = "Attachment"
                                moreInfoModel.fieldValue =
                                    resources.getString(R.string.no_attachment)
                                moreInfoModel.fileUrl = fieldObj.getString("url")
                            }
                            (moreInfoModelList as ArrayList<MoreInfoListModel>).add(moreInfoModel)
                        }
                        if (i == 1) {
                            val moreInfoModel = MoreInfoListModel()
                            moreInfoModel.fieldName = "No of days"
                            moreInfoModel.fieldValue = fieldObj.getString("187")
                            (moreInfoModelList as ArrayList<MoreInfoListModel>).add(moreInfoModel)
                        }
                        if (i == 2) {
                            val moreInfoModel = MoreInfoListModel()
                            moreInfoModel.fieldName = "Start Date"
                            moreInfoModel.fieldValue = fieldObj.getString("150")
                            (moreInfoModelList as ArrayList<MoreInfoListModel>).add(moreInfoModel)
                        }
                        if (i == 3) {
                            val moreInfoModel = MoreInfoListModel()
                            moreInfoModel.fieldName = "End Date"
                            moreInfoModel.fieldValue = fieldObj.getString("151")
                            (moreInfoModelList as ArrayList<MoreInfoListModel>).add(moreInfoModel)
                        }
                    }
                    //}
                }
            } catch (e: JSONException) {
                e.stackTrace
            }
        } else {
            try {
                val recordArr = JSONArray(recordDetailObjStr)
                for (k in 0 until recordArr.length()) {
                    val recordDetailObj =
                        recordArr.getJSONObject(k) //new JSONObject(recordDetailObjStr);
                    val fieldDic =
                        JSONObject(intent.getStringExtra("RecordField")).getJSONArray(catId)
                    var recordIdDetails: String
                    if (HomeSharedRecordsListAdapter.shareRecordClickFlag == 1 || DashBoardSharedRecordsListAdapter.shareRecordClickFlag == 1) { // added by dileep 8th july 2020 in if  || DashBoardSharedRecordsListAdapter.shareRecordClickFlag == 1
                        recordIdDetails = recordDetailObj.getString("record_id")
                    } else {
                        recordIdDetails = recordDetailObj.getString("id")
                    }
                    if ((recordIdDetails == recordId)) {
                        for (i in 0 until fieldDic.length()) {
                            //if (i != 3) {
                            val fieldObj = fieldDic.getJSONObject(i)
                            val moreInfoModel = MoreInfoListModel()
                            moreInfoModel.fieldName = fieldObj.getString("name")
                            val recordInfoArray =
                                recordDetailObj.getJSONArray("records") //added by dileep
                            recordsOBj =
                                recordInfoArray.getJSONObject(multiRecordPosition) //added by dileep
                            if (fieldObj["key"] is String) {
                                if (recordsOBj!!.has(fieldObj.getString("key"))) {
                                    if (catId.equals(
                                            "38",
                                            ignoreCase = true
                                        ) && ((fieldObj.getString("key") == "234") || (fieldObj.getString(
                                            "key"
                                        ) == "235") || (fieldObj.getString("key") == "236") || (fieldObj.getString(
                                            "key"
                                        ) == "237"))
                                    ) {
                                        if (recordsOBj!!.getString(fieldObj.getString("key"))
                                                .equals("1", ignoreCase = true)
                                        ) {
                                            moreInfoModel.fieldValue = "Yes"
                                        } else {
                                            moreInfoModel.fieldValue = "-"
                                        }
                                    } else {
                                        moreInfoModel.fieldValue =
                                            recordsOBj!!.getString(fieldObj.getString("key"))
                                    }
                                    if (recordsOBj!!.getString("url") != null) {
                                        moreInfoModel.fileUrl = recordsOBj!!.getString("url")
                                    }
                                    if (fieldObj.getString("name")
                                            .equals("Attachments", ignoreCase = true)
                                    ) {
                                        if (recordsOBj!!.getString(fieldObj.getString("key"))
                                                .equals("yes", ignoreCase = true)
                                        ) {
                                            moreInfoModel.fieldValue =
                                                resources.getString(R.string.view_attachment)
                                            if (recordsOBj!!.getString("url") != null) {
                                                moreInfoModel.fileUrl = recordsOBj!!.getString("url")
                                            }
                                            //
                                        } else {
                                            moreInfoModel.fieldValue =
                                                resources.getString(R.string.no_attachment)
                                        }
                                    }
                                }
                            } else if (recordsOBj!!.has(Integer.toString(fieldObj.getInt("key")))) {
                                if (recordsOBj!!.has(Integer.toString(fieldObj.getInt("key")))) {
                                    moreInfoModel.fieldValue =
                                        recordsOBj!!.getString(fieldObj.getString("key"))
                                    if (fieldObj.getString("name").equals(
                                            "Attachments",
                                            ignoreCase = true
                                        ) || fieldObj.getString("name").equals(
                                            "Attachment",
                                            ignoreCase = true
                                        ) || fieldObj.getString("name")
                                            .equals("File", ignoreCase = true)
                                    ) {
                                        if (recordsOBj!!.getString(fieldObj.getString("key"))
                                                .equals("yes", ignoreCase = true)
                                        ) {
                                            moreInfoModel.fieldValue =
                                                resources.getString(R.string.view_attachment)
                                            if (recordsOBj!!.getString("url") != null) {
                                                moreInfoModel.fileUrl = recordsOBj!!.getString("url")
                                            }
                                        } else {
                                            moreInfoModel.fieldValue =
                                                resources.getString(R.string.no_attachment)
                                        }
                                    }
                                }
                            }
                            (moreInfoModelList as ArrayList<MoreInfoListModel>).add(moreInfoModel)
                            //}
                        }
                        break
                    }
                }
                //}
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        btnDelete?.setOnClickListener(View.OnClickListener { view: View? -> deleteNotesPopup() })
        try {
            val recordObj = JSONArray(recordDetailObjStr)
            val recordData = recordObj.getJSONObject(0)
            ID = recordData.getInt("id")
            patientId = recordData.getInt("patient_id")
            encounterId = recordData.getInt("encounter_id")
            episodeId = recordData.getInt("episode_id")
        } catch (e: Exception) {
            e.printStackTrace()
        }
        btnEdit?.setOnClickListener(View.OnClickListener { view: View? ->
            if ((catName.equals("Symptoms", ignoreCase = true)
                        || catName.equals("Investigation Results", ignoreCase = true)
                        || catName.equals("Diagnosis", ignoreCase = true) ||
                        catName.equals("HandWritten Note", ignoreCase = true))
            ) {
                if (encounterId != 0) {
                    val myIntent: Intent = Intent(
                        this@EMRCaseHistoryMoreInfoActivity,
                        EMRCreateRecordsFormActivity::class.java
                    )
                    myIntent.putExtra("patientId", patientId)
                    myIntent.putExtra("episodeId", episodeId)
                    myIntent.putExtra("encounterId", encounterId)
                    myIntent.putExtra("categoryName", catName)
                    myIntent.putExtra("patientName", PatientName)
                    myIntent.putExtra("RecordDetail", recordDetailObjStr)
                    myIntent.putExtra("Record_ID", ID)
                    myIntent.putExtra("callingFrom", "editRecord")
                    //                    startActivity(myIntent);
                    startActivityForResult(myIntent, LAUNCH_SECOND_ACTIVITY)
                } else {
                    Toast.makeText(
                        this@EMRCaseHistoryMoreInfoActivity,
                        "Please select one interaction before adding a record",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } else if (ID != 0) {
                val intent: Intent =
                    Intent(this@EMRCaseHistoryMoreInfoActivity, EMRCreateRecordActivity::class.java)
                intent.putExtra("CategoryId", catId + "")
                intent.putExtra("CategoryName", catName)
                intent.putExtra("PatientId", patientId)
                intent.putExtra("EpisodeId", episodeId)
                intent.putExtra("EncounterID", encounterId)
                intent.putExtra("Record_ID", ID)
                intent.putExtra("callingFrom", "editRecord")
                intent.putExtra("RecordDetail", recordsOBj.toString())
                intent.putExtra("Type", categoryType)
                intent.putExtra("dynamicEncounterDataIds", dynamicEncounterDataIds)
                launchEditRecordResults!!.launch(intent)
            } else {
                Toast.makeText(
                    this@EMRCaseHistoryMoreInfoActivity,
                    "Please select one interaction before adding a record",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }

    fun getFileFromUrl(filrUrl: String) {
        val loadingDialog = ProgressDialog(this)
        loadingDialog.setMessage(resources.getString(R.string.loading_data))
        loadingDialog.setTitle(resources.getString(R.string.fetching_file))
        loadingDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER)
        loadingDialog.setCancelable(false)
        loadingDialog.show()
        val url = JSONObject()
        try {
            url.put("url", filrUrl.trim { it <= ' ' })
        } catch (e: Exception) {
            e.printStackTrace()
        }
        apiCall!!.postRecords(ApiUrls.getFileFromUrl, url, this, object : VolleyCallback {
            override fun onSuccess(result: String) {
                loadingDialog.dismiss()
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
                loadingDialog.dismiss()
                errorHandler(this@EMRCaseHistoryMoreInfoActivity, err)
            }
        })
    }

    fun dummyData() {
        var m = MoreInfoListModel()
        m.fieldName = "Test"
        m.fieldValue = "Datakkk"
        moreInfoModelList!!.add(m)
        m = MoreInfoListModel()
        m.fieldName = "Test"
        m.fieldValue = "Datakkk"
        moreInfoModelList!!.add(m)
    }

    private fun deleteNotesPopup() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setContentView(R.layout.custom_popup_gbp)
        dialog.setCancelable(false)
        val rl_parent = dialog.findViewById<RelativeLayout>(R.id.rl_parent)
        val tv_title = dialog.findViewById<TextView>(R.id.tv_title)
        val tv_body = dialog.findViewById<TextView>(R.id.tv_body)
        val btn_close = dialog.findViewById<Button>(R.id.btn_close)
        val btn_Delete = dialog.findViewById<Button>(R.id.btn_remove)
        val img = ContextCompat.getDrawable(this, R.drawable.cross_lines)
        img!!.setBounds(0, 0, 60, 60)
        btn_close.setCompoundDrawables(img, null, null, null)
        btn_Delete.setText(R.string.delete)
        tv_body.visibility = View.GONE
        tv_title.text = "Are you sure you want to delete $catName"
        btn_close.setOnClickListener({ v: View? -> dialog.dismiss() })
        btn_Delete.setOnClickListener({ v: View? ->
            dialog.dismiss()
            if (categoryType != null && !categoryType.equals("", ignoreCase = true)) {
                try {
                    mainRecordsId = recordsOBj!!.getInt("id")
                    deletePrescriptionRecords(categoryType, mainRecordsId)
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            } else {
                deletePrescriptionRecords(catName, ID)
            }
        })
        dialog.show()
    }

    fun deletePrescriptionRecords(categoryName: String?, recordID: Int) {
        if (myClinicGlobalClass!!.isOnline) {
            val loadingDialog = ProgressDialog(this)
            loadingDialog.setMessage(resources.getString(R.string.removing_record))
            loadingDialog.setCancelable(false)
            loadingDialog.setInverseBackgroundForced(false)
            loadingDialog.show()
            emrCreateRecordsFormViewModel!!.deletePrescriptionRecords(
                this@EMRCaseHistoryMoreInfoActivity,
                recordID,
                categoryName
            ).observe(this@EMRCaseHistoryMoreInfoActivity, object : Observer<String?> {
                override fun onChanged(value: String?) {
                    try {
                        loadingDialog.dismiss()
                        val response = JSONObject(value)
                        if (response.getInt("status_code") == 200) {
                            val responseObj = response.getJSONObject("response")
                            val MSG = responseObj.getString("response")
                            Toast.makeText(
                                this@EMRCaseHistoryMoreInfoActivity,
                                MSG,
                                Toast.LENGTH_LONG
                            ).show()
                            finish()
                        } else {
                            loadingDialog.dismiss()
                            if (value != null) {
                                errorHandler(this@EMRCaseHistoryMoreInfoActivity, value)
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        loadingDialog.dismiss()
                    }
                }
            })
        } else {
            myClinicGlobalClass!!.noInternetConnection.showDialog(this@EMRCaseHistoryMoreInfoActivity)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == LAUNCH_SECOND_ACTIVITY) {
            if (resultCode == RESULT_OK) {
                val result = data!!.getStringExtra("editRecord")
                if (result.equals("editRecord", ignoreCase = true)) {
                    finish()
                }
            }
        }
    }
}