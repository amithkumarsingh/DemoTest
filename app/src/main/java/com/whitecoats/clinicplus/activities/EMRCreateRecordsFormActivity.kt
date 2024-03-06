package com.whitecoats.clinicplus.activities

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.app.ProgressDialog
import android.content.ContentResolver
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.*
import android.provider.MediaStore
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.*
import android.widget.*
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.*
import com.android.volley.toolbox.Volley
import com.google.android.gms.common.util.IOUtils
import com.whitecoats.clinicplus.MyClinicGlobalClass
import com.whitecoats.clinicplus.PatientRecordsApi
import com.whitecoats.clinicplus.R
import com.whitecoats.clinicplus.adapters.AutoSuggestAdapter
import com.whitecoats.clinicplus.adapters.EMRAllPrescriptionListAdapter
import com.whitecoats.clinicplus.apis.ApiUrls
import com.whitecoats.clinicplus.constants.AppConstants
import com.whitecoats.clinicplus.models.EMRPrescriptionModel
import com.whitecoats.clinicplus.models.MoreInfoListModel
import com.whitecoats.clinicplus.utils.APIResponse
import com.whitecoats.clinicplus.utils.AppUtilities
import com.whitecoats.clinicplus.utils.ErrorHandlerClass.errorHandler
import com.whitecoats.clinicplus.utils.RecordSavingType
import com.whitecoats.clinicplus.viewmodels.CreateEMRRecordViewModel
import com.whitecoats.clinicplus.viewmodels.EMRConsultationNotesViewModel
import com.whitecoats.clinicplus.viewmodels.EMRCreateRecordsFormViewModel
import com.whitecoats.model.SymptomsAutoSuggestion
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.*
import java.text.SimpleDateFormat
import java.util.*

class EMRCreateRecordsFormActivity() : AppCompatActivity() {
    private var toolbar: Toolbar? = null
    private var saveAndAddMoreLayout: RelativeLayout? = null
    private val allPrescriptionRecyclerView: RecyclerView? = null
    var allPrescriptionList: List<EMRPrescriptionModel>? = null
    private val allPrescriptionListAdapter: EMRAllPrescriptionListAdapter? = null
    private var allPrescriptionLayoutManager: RecyclerView.LayoutManager? = null
    private var appUtilities: AppUtilities? = null
    private var patientId = 0
    private var episodeId = 0
    private var encounterId = 0
    private var emrConsultationNotesViewModel: EMRConsultationNotesViewModel? = null

    //symptom
    private var symptomForm: ScrollView? = null
    private var sympDesp: EditText? = null
    private var sympFirstReport: TextView? = null
    private var sympStatus: Spinner? = null
    var sympStatuArr = arrayOf<String?>("Ongoing", "Partially Subsided", "Completely Subsided")
    private var createRecordSymptomBack: Button? = null
    private var createRecordSaveSymptomData: Button? = null
    private var createRecordDaigBack: Button? = null
    private var createRecordSaveDaigData: Button? = null
    private var createRecordInvesBack: Button? = null
    private var createRecordSaveinvesData: Button? = null
    private var mYear = 0
    private var mMonth = 0
    private var mDay = 0
    private val mHour = 0
    private val mMinute = 0
    var temp: String? = null

    //invest
    private var investForm: ScrollView? = null
    private var investName: EditText? = null
    private var investParam: EditText? = null
    private var investValue: EditText? = null
    private var investNote: EditText? = null
    private var investUpload: RelativeLayout? = null
    private var investigationImageView: ImageView? = null

    //diag
    private var diagForm: ScrollView? = null
    private var diagName: EditText? = null
    private var diagPoisted: TextView? = null
    private var diagConfirm: TextView? = null
    private var diagConfirmLabel: TextView? = null
    private var diagStatus: Spinner? = null
    private var diagValueLayout: LinearLayout? = null
    private val diagStatusArr = arrayOf<String?>("Tentative", "Confirmed", "Ruled Out")
    private var diagNameArr: MutableList<String>? = null
    private var diagIdArr: MutableList<Int>? = null
    private var selectedFromAutocomplete = false
    private var diagValueLayoutClose: ImageView? = null
    private var isPDFFile = false
    private val permissionsRequired = arrayOf(Manifest.permission.CAMERA)
    private var uploadImageResponse: String? = null
    private var pdfFile: File? = null
    private var fileUri: Uri? = null
    private val noteFileImage: ImageView? = null
    private var imageFilePath: String? = null
    private var handWrittenMainLayout: RelativeLayout? = null
    private var symptomMainLayout: RelativeLayout? = null
    private var investigationMainLayout: RelativeLayout? = null
    private var daignosisMainLayout: RelativeLayout? = null
    private var categoryName: String? = null
    private var uploadHandWrittenNotes: RelativeLayout? = null
    private var attachedImageLayout: RelativeLayout? = null
    private val saveAndAddMore: RelativeLayout? = null
    private var attachedImage: TextView? = null
    private var viewFile: TextView? = null
    private var saveAndClose: TextView? = null
    private var removeFile: TextView? = null
    private var handWrittenDescription: EditText? = null
    private var attachedFileName: String? = null
    private var emrCreateRecordsFormViewModel: EMRCreateRecordsFormViewModel? = null
    private var progressDialog: ProgressDialog? = null
    private var saveDialog: ProgressDialog? = null
    private var myClinicGlobalClass: MyClinicGlobalClass? = null
    private var removeAndViewLayout: RelativeLayout? = null
    private var isInvestigationImageUpload = false
    private var prescriptionText: TextView? = null
    private var prescriptionValidTillText: TextView? = null
    private var textPrescriptionText: TextView? = null
    private var prescriptionValidTillCard: TextView? = null
    private var medicinePrescriptionSpinner: Spinner? = null
    private var testPrescriptionSpinner: Spinner? = null
    private var prescriptionAdapter: ArrayAdapter<*>? = null
    private var testPrescriptionAdapter: ArrayAdapter<*>? = null
    private var validTillText: String? = null
    private var selectedPrescriptionText = 0
    private var selectedTestPresciption = 0
    private var calendar: Calendar? = null
    private var patientName: String? = null
    private var callingFrom: String? = null
    private var type: String? = null

    /*ENGG-3691 -- Refactoring the code by removing the deprecate functions(StartActivityForResults)*/
    private var launchOpenFileResults: ActivityResultLauncher<Intent>? = null
    private var launcherCameraResults: ActivityResultLauncher<Intent>? = null
    private var symptomAutoCompleteTv: AutoCompleteTextView? = null
    private var createEMRRecordViewModel: CreateEMRRecordViewModel? = null
    private var autoSuggestAdapter: AutoSuggestAdapter? = null
    private var handler: Handler? = null
    private var recordDetailObjStr: String? = null
    private var llUpdateRecord: LinearLayout? = null
    private var createRecordBottomLayoutSymptoms: LinearLayout? = null
    private var createInvesRecordBottomLayout: LinearLayout? = null
    private var createDaigRecordBottomLayout: LinearLayout? = null
    private var btnUpdateRecord: Button? = null
    private var rlSaveCloseHandWritten: RelativeLayout? = null
    private val ll_update_record: LinearLayout? = null
    private var Record_ID = 0
    private var rlAttachmentParenInvestigation: RelativeLayout? = null
    private var tvAttachmentNameInvestigation: TextView? = null
    private var tvRemoveInvestigation: TextView? = null
    private var tvViewInvestigation: TextView? = null
    private var apiCall: PatientRecordsApi? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
        setContentView(R.layout.activity_e_m_r_create_records_form)
        toolbar = findViewById(R.id.emrAddingNotesToolbar)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        val upArrow = AppUtilities.changeIconColor(
            resources.getDrawable(R.drawable.ic_arrow_back, theme), this, R.color.colorWhite
        )
        supportActionBar!!.setHomeAsUpIndicator(upArrow)
        emrConsultationNotesViewModel = ViewModelProvider(this).get(
            EMRConsultationNotesViewModel::class.java
        )
        emrConsultationNotesViewModel!!.init()
        apiCall = PatientRecordsApi()
        val intent = intent
        patientId = intent.getIntExtra("patientId", 0)
        episodeId = intent.getIntExtra("episodeId", 0)
        encounterId = intent.getIntExtra("encounterId", 0)
        categoryName = intent.getStringExtra("categoryName")
        patientName = intent.getStringExtra("patientName")
        Record_ID = intent.getIntExtra("Record_ID", 0)
        recordDetailObjStr = getIntent().getStringExtra("RecordDetail")
        if (intent.hasExtra("callingFrom")) {
            callingFrom = intent.getStringExtra("callingFrom")
        }
        toolbar?.title = patientName
        allPrescriptionList = ArrayList()
        appUtilities = AppUtilities()
        allPrescriptionLayoutManager = LinearLayoutManager(applicationContext)
        emrCreateRecordsFormViewModel = ViewModelProvider(this).get(
            EMRCreateRecordsFormViewModel::class.java
        )
        emrCreateRecordsFormViewModel!!.init()
        handWrittenMainLayout = findViewById(R.id.handWrittenMainLayout)
        symptomMainLayout = findViewById(R.id.symptomMainLayout)
        investigationMainLayout = findViewById(R.id.investigationMainLayout)
        daignosisMainLayout = findViewById(R.id.daignosisMainLayout)
        rlAttachmentParenInvestigation = findViewById(R.id.rl_attachment_parent)
        tvAttachmentNameInvestigation = findViewById(R.id.tv_attachment_name_investigation)
        tvRemoveInvestigation = findViewById(R.id.tv_remove_investigation)
        tvViewInvestigation = findViewById(R.id.tv_view_investigation)


        //symptom
        symptomForm = findViewById(R.id.recordsCreateSymptomForm)
        //sympName = findViewById(R.id.createSymptomName);
        sympDesp = findViewById(R.id.createSymptomDesp)
        sympFirstReport = findViewById(R.id.createSympFirstReporton)
        sympStatus = findViewById(R.id.createSympStatus)
        createRecordSymptomBack = findViewById(R.id.createRecordSymptomBack)
        createRecordSaveSymptomData = findViewById(R.id.createRecordSaveSymptomData)
        symptomAutoCompleteTv = findViewById(R.id.symptoms_acTV)


        //invest
        investForm = findViewById(R.id.recordCreateInvestForm)
        investName = findViewById(R.id.recordCreateInvestName)
        investParam = findViewById(R.id.recordCreateInvestParam)
        investValue = findViewById(R.id.recordCreateInvestParamValue)
        investNote = findViewById(R.id.recordCreateInvestNotes)
        investUpload = findViewById(R.id.recordCreateInvestUpload)
        createRecordInvesBack = findViewById(R.id.createRecordInvesBack)
        createRecordSaveinvesData = findViewById(R.id.createRecordSaveinvesData)
        investigationImageView = findViewById(R.id.investigationImageView)

        //diag
        diagForm = findViewById(R.id.recordCreateDiagForm)
        diagName = findViewById(R.id.recordCreateDiagName)
        diagConfirm = findViewById(R.id.recordCreateDiagConfirmed)
        diagPoisted = findViewById(R.id.recordCreateDiagPosited)
        diagStatus = findViewById(R.id.recordCreateDiagStatus)
        diagValueLayout = findViewById(R.id.recordCreateDiagValues)
        diagNameArr = ArrayList()
        diagIdArr = ArrayList()
        diagConfirmLabel = findViewById(R.id.recordCreateDiagConfirmLabel)
        diagValueLayoutClose = findViewById(R.id.recordCreateDiagValueClose)
        createRecordDaigBack = findViewById(R.id.createRecordDaigBack)
        createRecordSaveDaigData = findViewById(R.id.createRecordSaveDaigData)
        myClinicGlobalClass = applicationContext as MyClinicGlobalClass
        progressDialog = ProgressDialog(this@EMRCreateRecordsFormActivity)
        progressDialog!!.setMessage("Please wait while file is opening...")
        saveDialog = ProgressDialog(this@EMRCreateRecordsFormActivity)
        saveDialog!!.setMessage("Saving HandWritten Notes, Please wait...")

        // HandWritten Notes
        uploadHandWrittenNotes = findViewById(R.id.handwrittenNoteButtonLayout)
        attachedImageLayout = findViewById(R.id.attachedImageNameLayout)
        attachedImage = findViewById(R.id.attached_image_text)
        viewFile = findViewById(R.id.view_file)
        handWrittenDescription = findViewById(R.id.handwritten_description)
        saveAndClose = findViewById(R.id.save_and_close)
        removeFile = findViewById(R.id.remove_file)
        removeAndViewLayout = findViewById(R.id.attachedViewAndRemoveLayout)
        removeAndViewLayout!!.visibility = View.GONE
        prescriptionText = findViewById(R.id.prescription_text)
        prescriptionValidTillText = findViewById(R.id.prescription_valid_till_text)
        textPrescriptionText = findViewById(R.id.test_prescription_text)
        medicinePrescriptionSpinner = findViewById(R.id.medicine_prescription_spinner)
        prescriptionValidTillCard = findViewById(R.id.prescription_valid_till_card)
        testPrescriptionSpinner = findViewById(R.id.test_prescription_spinner)
        prescriptionValidTillText!!.visibility = View.GONE
        prescriptionValidTillCard!!.visibility = View.GONE
        llUpdateRecord = findViewById(R.id.ll_update_record)
        btnUpdateRecord = findViewById(R.id.btn_update_record)
        rlSaveCloseHandWritten = findViewById(R.id.rl_save_close_handWritten)
        createRecordBottomLayoutSymptoms = findViewById(R.id.createRecordBottomLayout)
        createInvesRecordBottomLayout = findViewById(R.id.createInvesRecordBottomLayout)
        createDaigRecordBottomLayout = findViewById(R.id.createDaigRecordBottomLayout)
        if (callingFrom != null && callingFrom.equals("editRecord", ignoreCase = true)) {
            llUpdateRecord!!.visibility = View.VISIBLE
            rlSaveCloseHandWritten!!.visibility = View.GONE
            createRecordBottomLayoutSymptoms!!.visibility = View.GONE
            createInvesRecordBottomLayout!!.visibility = View.GONE
            createDaigRecordBottomLayout!!.visibility = View.GONE
        } else {
            llUpdateRecord!!.visibility = View.GONE
        }
        calendar = Calendar.getInstance()
        autoSuggestAdapter = AutoSuggestAdapter(
            this,
            android.R.layout.simple_dropdown_item_1line
        )
        symptomAutoCompleteTv!!.setAdapter(autoSuggestAdapter)
        createEMRRecordViewModel = ViewModelProvider(this).get(
            CreateEMRRecordViewModel::class.java
        )
        symptomAutoCompleteTv!!.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                if (charSequence.length > 2) {
                    //Make API call to get auto suggestions
                    handler!!.removeMessages(TRIGGER_AUTO_COMPLETE)
                    handler!!.sendEmptyMessageDelayed(
                        TRIGGER_AUTO_COMPLETE,
                        AUTO_COMPLETE_DELAY
                    )
                }
            }

            override fun afterTextChanged(editable: Editable) {}
        })
        handler = Handler { msg: Message ->
            if (msg.what == TRIGGER_AUTO_COMPLETE) {
                if (!TextUtils.isEmpty(symptomAutoCompleteTv!!.getText())) {
                    val url: String =
                        ApiUrls.getAutoSuggestionsForSymptoms + "?source=app&search=" + symptomAutoCompleteTv!!.getText()
                            .toString()
                    createEMRRecordViewModel!!.getAutoSuggestionsForSymptoms(url)
                    createEMRRecordViewModel!!.autoSuggestionsLiveData.observe(
                        this@EMRCreateRecordsFormActivity,
                        Observer<APIResponse<Any>?> { objectAPIResponse: APIResponse<Any>? ->
                            if (objectAPIResponse?.getError() == null && objectAPIResponse?.getResponseClassObj() != null) {
                                val suggestionsList: MutableList<String> = ArrayList()
                                val autoSuggestionList: SymptomsAutoSuggestion? =
                                    objectAPIResponse?.getResponseClassObj() as SymptomsAutoSuggestion?
                                for (i in autoSuggestionList!!.response.indices) {
                                    suggestionsList.add(autoSuggestionList.response.get(i).symptom_name)
                                }
                                autoSuggestAdapter!!.setData(suggestionsList)
                                autoSuggestAdapter!!.notifyDataSetChanged()
                            }
                        })
                }
            }
            false
        }

        /*ENGG-3691 -- Refactoring the code by removing the deprecate functions(StartActivityForResults)*/
        //Start
        launchOpenFileResults =
            registerForActivityResult<Intent, ActivityResult>(
                ActivityResultContracts.StartActivityForResult()
            ) { result: ActivityResult ->
                //Request code 2
                val data: Intent? = result.getData()
                val resultCode: Int = result.getResultCode()
                if (resultCode == RESULT_OK) {
                    val uri: Uri? = data!!.getData()
                    val fileName: String? = getFileName(uri)
                    attachedFileName = fileName
                    if (fileName!!.endsWith(".jpg") || fileName.endsWith(".jpeg") || fileName.endsWith(
                            ".png"
                        ) || fileName.endsWith(".pdf")
                    ) {
                        try {
                            if (fileName.contains("pdf")) {
                                isPDFFile = true
                                uploadPDFFile(fileName, uri)
                            } else {
                                val bitmapImage: Bitmap =
                                    MediaStore.Images.Media.getBitmap(getContentResolver(), uri)
                                val nh: Int =
                                    (bitmapImage.height * (720.0 / bitmapImage.getWidth())).toInt()
                                val scaled: Bitmap =
                                    Bitmap.createScaledBitmap(bitmapImage, 720, nh, true)
                                investigationImageView!!.setImageBitmap(scaled)
                                type = "image"
                                uploadImage("image")
                            }
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    } else {
                        Toast.makeText(
                            this@EMRCreateRecordsFormActivity,
                            "Please Upload .jpg or .png files only",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }
        launcherCameraResults =
            registerForActivityResult<Intent, ActivityResult>(
                ActivityResultContracts.StartActivityForResult()
            ) { result: ActivityResult ->
                //Request code 1
                val data: Intent? = result.data
                val resultCode: Int = result.resultCode
                if (resultCode == RESULT_OK) {

                    //new
                    try {
                        val contentResolver: ContentResolver = getContentResolver()

                        // Use the content resolver to open camera taken image input stream through image uri.
                        attachedFileName = getFileName(fileUri)
                        val inputStream: InputStream? =
                            contentResolver.openInputStream((fileUri)!!)

                        // Decode the image input stream to a bitmap use BitmapFactory.
                        val pictureBitmap: Bitmap = BitmapFactory.decodeStream(inputStream)
                        val nh: Int =
                            (pictureBitmap.height * (720.0 / pictureBitmap.getWidth())).toInt()
                        val scaled: Bitmap =
                            Bitmap.createScaledBitmap(pictureBitmap, 720, nh, true)
                        investigationImageView!!.setImageBitmap(scaled)
                        type = "image"
                        uploadImage("image")
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        //End
        prescriptionAdapter = ArrayAdapter(
            this@EMRCreateRecordsFormActivity,
            android.R.layout.simple_spinner_dropdown_item,
            AppConstants.prescriptionDetails
        )
        (prescriptionAdapter as ArrayAdapter<String>).setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        medicinePrescriptionSpinner!!.adapter = prescriptionAdapter
        medicinePrescriptionSpinner!!.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>?, view: View, i: Int, l: Long) {
                if ((AppConstants.prescriptionDetails[i] == "Yes")) {
                    selectedPrescriptionText = 1
                    prescriptionValidTillText!!.visibility = View.VISIBLE
                    prescriptionValidTillCard!!.visibility = View.VISIBLE
                    calendar = Calendar.getInstance()
                    validTillText = appUtilities!!.changeDateFormat(
                        "yyyy-MM-dd", "dd MMM, yyyy", calendar!!.get(
                            Calendar.YEAR
                        )
                            .toString() + "-" + (calendar!!.get(Calendar.MONTH) + 1) + "-" + calendar!!.get(
                            Calendar.DAY_OF_MONTH
                        )
                    )
                    prescriptionValidTillCard!!.text = appUtilities!!.changeDateFormat(
                        "yyyy-MM-dd", "dd MMM, yyyy", calendar!!.get(
                            Calendar.YEAR
                        )
                            .toString() + "-" + (calendar!!.get(Calendar.MONTH) + 1) + "-" + calendar!!.get(
                            Calendar.DAY_OF_MONTH
                        )
                    )
                } else if ((AppConstants.prescriptionDetails[i] == "No")) {
                    selectedPrescriptionText = 0
                    prescriptionValidTillText!!.visibility = View.GONE
                    prescriptionValidTillCard!!.visibility = View.GONE
                    validTillText = ""
                } else {
                    selectedPrescriptionText = 2
                    prescriptionValidTillText!!.visibility = View.GONE
                    prescriptionValidTillCard!!.setVisibility(View.GONE)
                    validTillText = ""
                }
            }

            override fun onNothingSelected(adapterView: AdapterView<*>?) {}
        }
        testPrescriptionAdapter = ArrayAdapter(
            this@EMRCreateRecordsFormActivity,
            android.R.layout.simple_spinner_dropdown_item,
            AppConstants.testPrescriptionDetails
        )
        (testPrescriptionAdapter as ArrayAdapter<String>).setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        testPrescriptionSpinner!!.adapter = testPrescriptionAdapter
        testPrescriptionSpinner!!.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>?, view: View, i: Int, l: Long) {
                if ((AppConstants.testPrescriptionDetails[i] == "Yes")) {
                    selectedTestPresciption = 1
                } else if ((AppConstants.testPrescriptionDetails[i] == "No")) {
                    selectedTestPresciption = 0
                } else {
                    selectedTestPresciption = 2
                }
            }

            override fun onNothingSelected(adapterView: AdapterView<*>?) {}
        }
        prescriptionValidTillCard!!.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View) {
                val datePickerDialog = DatePickerDialog(
                    this@EMRCreateRecordsFormActivity,
                    OnDateSetListener { view, year, month, dayOfMonth ->
                        val d: String
                        val m: String
                        if (dayOfMonth < 10) {
                            d = "0$dayOfMonth"
                        } else {
                            d = dayOfMonth.toString()
                        }
                        if (month < 10) {
                            m = "0" + (month + 1)
                        } else {
                            m = (month + 1).toString()
                        }
                        validTillText = appUtilities!!.changeDateFormat(
                            "dd-MM-yyyy",
                            "dd MMM, yyyy",
                            "$d-$m-$year"
                        )
                        prescriptionValidTillCard!!.setText(
                            appUtilities!!.changeDateFormat(
                                "dd-MM-yyyy",
                                "dd MMM, yyyy",
                                "$d-$m-$year"
                            )
                        )
                    },
                    calendar!!.get(Calendar.YEAR),
                    calendar!!.get(Calendar.MONTH),
                    calendar!!.get(Calendar.DAY_OF_MONTH)
                )
                datePickerDialog.show()
            }
        })
        attachedImageLayout!!.setVisibility(View.GONE)
        uploadHandWrittenNotes!!.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View) {
                selectMethodDialog()
            }
        })
        tvViewInvestigation!!.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View) {
                viewUploadedImageOrPDF()
            }
        })
        tvRemoveInvestigation!!.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View) {
                uploadImageResponse = ""
                rlAttachmentParenInvestigation!!.visibility = View.GONE
                tvAttachmentNameInvestigation!!.text = ""
                if (type.equals(
                        "image",
                        ignoreCase = true
                    )
                ) Toast.makeText(
                    this@EMRCreateRecordsFormActivity,
                    "Image successfully removed",
                    Toast.LENGTH_LONG
                ).show() else Toast.makeText(
                    this@EMRCreateRecordsFormActivity,
                    "Pdf successfully removed",
                    Toast.LENGTH_LONG
                ).show()
            }
        })
        removeFile!!.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View) {
                uploadImageResponse = ""
                removeAndViewLayout!!.visibility = View.GONE
                attachedImageLayout!!.visibility = View.GONE
                if (type.equals(
                        "image",
                        ignoreCase = true
                    )
                ) Toast.makeText(
                    this@EMRCreateRecordsFormActivity,
                    "Image successfully removed",
                    Toast.LENGTH_LONG
                ).show() else Toast.makeText(
                    this@EMRCreateRecordsFormActivity,
                    "Pdf successfully removed",
                    Toast.LENGTH_LONG
                ).show()
            }
        })
        viewFile!!.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View) {
                viewUploadedImageOrPDF()
            }
        })
        saveAndClose!!.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View) {
                saveHandWrittenNotes(
                    this@EMRCreateRecordsFormActivity,
                    ApiUrls.doctorId,
                    encounterId,
                    episodeId,
                    handWrittenDescription!!.text.toString().trim { it <= ' ' },
                    uploadImageResponse,
                    patientId,
                    "saveandclose",
                    selectedPrescriptionText,
                    selectedTestPresciption,
                    validTillText,
                    "",
                    Record_ID
                )
            }
        })
        btnUpdateRecord!!.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View) {
                if (categoryName.equals("HandWritten Note", ignoreCase = true)) {
                    saveHandWrittenNotes(
                        this@EMRCreateRecordsFormActivity,
                        ApiUrls.doctorId,
                        encounterId,
                        episodeId,
                        handWrittenDescription!!.text.toString().trim { it <= ' ' },
                        uploadImageResponse,
                        patientId,
                        "saveandclose",
                        selectedPrescriptionText,
                        selectedTestPresciption,
                        validTillText,
                        callingFrom,
                        Record_ID
                    )
                } else if (categoryName.equals("Diagnosis", ignoreCase = true)) {
                    saveDiagnosesDataByType(RecordSavingType.UPDATE_DIAGNOSIS)
                } else if (categoryName.equals("Symptoms", ignoreCase = true)) {
                    saveSymptomBaseOnType(RecordSavingType.UPDATE_SYMPTOMS)
                } else if (categoryName.equals("Investigation Results", ignoreCase = true)) {
                    saveInvestigationDataByType(RecordSavingType.UPDATE_INVESTIGATION)
                }
            }
        })
        if (categoryName.equals("Symptoms", ignoreCase = true)) {
            handWrittenMainLayout!!.visibility = View.GONE
            symptomMainLayout!!.visibility = View.VISIBLE
            investigationMainLayout!!.visibility = View.GONE
            daignosisMainLayout!!.visibility = View.GONE
        } else if (categoryName.equals("Investigation Results", ignoreCase = true)) {
            handWrittenMainLayout!!.visibility = View.GONE
            symptomMainLayout!!.visibility = View.GONE
            investigationMainLayout!!.visibility = View.VISIBLE
            daignosisMainLayout!!.visibility = View.GONE
        } else if (categoryName.equals("Diagnosis", ignoreCase = true)) {
            handWrittenMainLayout!!.visibility = View.GONE
            symptomMainLayout!!.visibility = View.GONE
            investigationMainLayout!!.visibility = View.GONE
            daignosisMainLayout!!.visibility = View.VISIBLE
            diagValueLayout!!.visibility = View.GONE
            docDiagnoses
        } else {
            handWrittenMainLayout!!.visibility = View.VISIBLE
            symptomMainLayout!!.visibility = View.GONE
            investigationMainLayout!!.visibility = View.GONE
            daignosisMainLayout!!.visibility = View.GONE
        }
        sympFirstReport!!.text = currentDateFormat
        sympFirstReport!!.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View) {
                openDateDialog(sympFirstReport)
            }
        })
        var aa: ArrayAdapter<*> =
            ArrayAdapter<Any?>(this, android.R.layout.simple_spinner_item, sympStatuArr)
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        //Setting the ArrayAdapter data on the Spinner
        sympStatus!!.adapter = aa
        aa = ArrayAdapter<Any?>(this, android.R.layout.simple_spinner_item, diagStatusArr)
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        //Setting the ArrayAdapter data on the Spinner
        diagStatus!!.adapter = aa
        diagPoisted!!.text = currentDateFormat
        diagPoisted!!.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View) {
                openDateDialog(diagPoisted)
            }
        })
        diagConfirm!!.text = currentDateFormat
        diagConfirm!!.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View) {
                openDateDialog(diagConfirm)
            }
        })
        diagStatus!!.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View,
                position: Int,
                id: Long
            ) {
                if (position == 0) {
                    diagConfirm!!.visibility = View.GONE
                    diagConfirmLabel!!.visibility = View.GONE
                } else {
                    diagConfirm!!.visibility = View.VISIBLE
                    diagConfirmLabel!!.visibility = View.VISIBLE
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
        diagName!!.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
//                Log.d("Value ADDed", s + "");
                if (s.isEmpty()) {
                    diagValueLayout!!.visibility = View.GONE
                } else {
                    diagValueLayout!!.visibility = View.VISIBLE
                }
            }

            override fun afterTextChanged(s: Editable) {}
        })
        diagValueLayoutClose!!.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View) {
                diagValueLayout!!.visibility = View.GONE
            }
        })
        createRecordSaveSymptomData!!.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View) {
                saveSymptomBaseOnType(RecordSavingType.SAVE_AND_MORE)
            }
        })
        createRecordSaveDaigData!!.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View) {
                saveDiagnosesDataByType(RecordSavingType.SAVE_AND_MORE)
            }
        })
        createRecordSaveinvesData!!.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View) {
                saveInvestigationDataByType(RecordSavingType.SAVE_AND_MORE)
            }
        })
        createRecordSymptomBack!!.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View) {
                saveSymptomBaseOnType(RecordSavingType.SAVE_AND_CLOSE)
            }
        })
        createRecordDaigBack!!.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View) {
                saveDiagnosesDataByType(RecordSavingType.SAVE_AND_CLOSE)
            }
        })
        createRecordInvesBack!!.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View) {
                saveInvestigationDataByType(RecordSavingType.SAVE_AND_CLOSE)
            }
        })
        investUpload!!.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View) {
//                openFileDialog();
                isInvestigationImageUpload = true
                selectMethodDialog()
            }
        })
        diagPoisted!!.text = currentDateFormat
        diagPoisted!!.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View) {
                openDateDialog(diagPoisted)
            }
        })
        diagConfirm!!.text = currentDateFormat
        diagConfirm!!.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View) {
                openDateDialog(diagConfirm)
            }
        })
        saveAndAddMoreLayout = findViewById(R.id.saveAndAddMoreLayout)
        saveAndAddMoreLayout!!.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View) {
                saveHandWrittenNotes(
                    this@EMRCreateRecordsFormActivity,
                    ApiUrls.doctorId,
                    encounterId,
                    episodeId,
                    handWrittenDescription!!.text.toString().trim { it <= ' ' },
                    uploadImageResponse,
                    patientId,
                    "saveandmore",
                    selectedPrescriptionText,
                    selectedTestPresciption,
                    validTillText,
                    "",
                    Record_ID
                )
            }
        })
        if (callingFrom != null && callingFrom.equals("editRecord", ignoreCase = true)) {
            if (getIntent().getStringExtra("categoryName")
                    .equals("HandWritten Note", ignoreCase = true)
            ) {
                try {
                    val handWrittenRecordArr = JSONArray(recordDetailObjStr)
                    for (i in 0..4) {
                        val fieldObj = handWrittenRecordArr.getJSONObject(0)
                        if (i == 0) {
                            val moreInfoModel = MoreInfoListModel()
                            if (fieldObj.getString("file_url") != null) {
                                uploadImageResponse = fieldObj.getString("file_url")
                                if (uploadImageResponse!!.endsWith(".jpg") || uploadImageResponse!!.endsWith(
                                        ".jpeg"
                                    ) || uploadImageResponse!!.endsWith(".png") || uploadImageResponse!!.endsWith(
                                        ".pdf"
                                    )
                                ) {
                                    if (uploadImageResponse!!.contains("pdf")) {
                                        type = "pdf"
                                    } else {
                                        type = "image"
                                    }
                                }
                                attachedImageLayout!!.visibility = View.VISIBLE
                                removeAndViewLayout!!.visibility = View.VISIBLE
                                attachedImage!!.text = fieldObj.getString("file_url") + " file has been attached"
                            } else {
                                attachedImage!!.text = fieldObj.getString("file_url")
                            }
                        }
                        if (i == 1) {
                            if (fieldObj.getInt("has_med_prescription") == 0) {
                                medicinePrescriptionSpinner!!.setSelection(2)
                            } else {
                                medicinePrescriptionSpinner!!.setSelection(1)
                            }
                        }
                        if (i == 2) {
                            if (!fieldObj.isNull("med_prescription_valid_till")) {
                                prescriptionValidTillCard!!.text =
                                    com.whitecoats.clinicplus.AppUtilities().changeDateFormat(
                                        "yyyy-MM-dd HH:mm:ss",
                                        "dd/MM/yyyy",
                                        fieldObj.getString("med_prescription_valid_till")
                                    )
                            } else {
                                prescriptionValidTillCard!!.text = "-"
                            }
                        }
                        if (i == 3) {
                            if (fieldObj.getInt("has_test_prescription") == 0) {
                                testPrescriptionSpinner!!.setSelection(2)
                            } else {
                                testPrescriptionSpinner!!.setSelection(1)
                            }
                        }
                        if (i == 4) {
                            handWrittenDescription!!.setText(fieldObj.getString("description"))
                        }
                    }
                } catch (e: JSONException) {
                    e.stackTrace
                }
            } else if (getIntent().getStringExtra("categoryName")
                    .equals("Symptoms", ignoreCase = true)
            ) {
                try {
                    val symptomsRecordArr = JSONArray(recordDetailObjStr)
                    val fieldObj = symptomsRecordArr.getJSONObject(0)
                    symptomAutoCompleteTv!!.setText(fieldObj.getString("symptom_name"))
                    sympDesp!!.setText(fieldObj.getString("symptom_description"))
                    fieldObj.getString("first_reported_on")
                    val reportDate = appUtilities!!.changeDateFormat(
                        "yyyy-MM-dd HH:mm:ss",
                        "dd MMM, yy",
                        fieldObj.getString("first_reported_on")
                    )
                    sympFirstReport!!.text = reportDate
                    for (i in sympStatuArr.indices) {
                        fieldObj.getString("symptom_status")
                        if (fieldObj.getString("symptom_status")
                                .equals(sympStatuArr[i], ignoreCase = true)
                        ) {
                            sympStatus!!.setSelection(i)
                        }
                    }
                } catch (e: JSONException) {
                    e.stackTrace
                }
            } else if (categoryName.equals("Diagnosis", ignoreCase = true)) {
                try {
                    val handWrittenRecordArr = JSONArray(recordDetailObjStr)
                    for (i in 0..3) {
                        val fieldObj = handWrittenRecordArr.getJSONObject(0)
                        if (i == 0) {
                            diagName!!.setText(fieldObj.getString("diagnosis"))
                            diagValueLayout!!.visibility = View.GONE
                        }
                        if (i == 1) {
                            val date = appUtilities!!.changeDateFormat(
                                "yyyy-MM-dd hh:mm:ss",
                                "dd MMM, yy",
                                fieldObj.getString("posited_on")
                            )
                            diagPoisted!!.text = date
                        }
                        if (i == 2) {
                            if (fieldObj.getString("status")
                                    .equals("Confirmed", ignoreCase = true)
                            ) {
                                diagStatus!!.setSelection(1)
                                diagConfirm!!.visibility = View.VISIBLE
                                diagConfirmLabel!!.visibility = View.VISIBLE
                            } else if (fieldObj.getString("status")
                                    .equals("Ruled Out", ignoreCase = true)
                            ) {
                                diagStatus!!.setSelection(2)
                                diagConfirm!!.visibility = View.VISIBLE
                                diagConfirmLabel!!.visibility = View.VISIBLE
                            } else {
                                diagStatus!!.setSelection(0)
                                diagConfirm!!.visibility = View.GONE
                                diagConfirmLabel!!.visibility = View.GONE
                            }
                        }
                        if (i == 3) {
                            if (!fieldObj.getString("confirmed_ruledout_on")
                                    .equals("null", ignoreCase = true)
                            ) {
                                val date = appUtilities!!.changeDateFormat(
                                    "yyyy-MM-dd hh:mm:ss",
                                    "dd MMM, yy",
                                    fieldObj.getString("confirmed_ruledout_on")
                                )
                                diagConfirm!!.text = date
                            } else {
                                diagConfirm!!.text = "-"
                            }
                        }
                    }
                } catch (e: JSONException) {
                    e.stackTrace
                }
            } else if (categoryName.equals("Investigation Results", ignoreCase = true)) {
                try {
                    val investigationRecordsArr = JSONArray(recordDetailObjStr)
                    val fieldObj = investigationRecordsArr.getJSONObject(0)
                    investName!!.setText(fieldObj.getString("investigation_name"))
                    investParam!!.setText(fieldObj.getString("parameter"))
                    investValue!!.setText(fieldObj.getString("value"))
                    investNote!!.setText(fieldObj.getString("notes"))
                    val fileURL = fieldObj.getString("file_url")
                    if (((fileURL != null) &&
                                !fileURL.equals("", ignoreCase = true) &&
                                !fileURL.equals("null", ignoreCase = true))
                    ) {
                        uploadImageResponse = fieldObj.getString("file_url")
                        if (uploadImageResponse!!.endsWith(".jpg") || uploadImageResponse!!.endsWith(".jpeg") || uploadImageResponse!!.endsWith(
                                ".png"
                            ) || uploadImageResponse!!.endsWith(".pdf")
                        ) {
                            if (uploadImageResponse!!.contains("pdf")) {
                                type = "pdf"
                            } else {
                                type = "image"
                            }
                        }
                        rlAttachmentParenInvestigation!!.visibility = View.VISIBLE
                        tvAttachmentNameInvestigation!!.text = fieldObj.getString("file_url") + " file has been attached"
                    }
                } catch (e: JSONException) {
                    e.stackTrace
                }
            }
        }
    }

    fun viewUploadedImageOrPDF() {
        progressDialog!!.show()
        emrCreateRecordsFormViewModel!!.getImagePath(
            this@EMRCreateRecordsFormActivity,
            uploadImageResponse
        ).observe(this@EMRCreateRecordsFormActivity, object : Observer<String?> {
            override fun onChanged(value: String?) {
                progressDialog!!.dismiss()
                try {
                    val response = JSONObject(value)
                    if (response.getInt("status_code") == 200) {
                        val completeUrl = response.getJSONObject("response").getString("response")
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(completeUrl))
                        // Note the Chooser below. If no applications match,
                        // Android displays a system message.So here there is no need for try-catch.
                        startActivity(Intent.createChooser(intent, "Browse with"))
                    } else {
                        if (value != null) {
                            errorHandler(this@EMRCreateRecordsFormActivity, value)
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        })
    }

    private val currentDateFormat: String?
        private get() {
            val c = Calendar.getInstance()
            mYear = c[Calendar.YEAR]
            mMonth = c[Calendar.MONTH]
            mDay = c[Calendar.DAY_OF_MONTH]
            temp = mDay.toString() + " " + (mMonth + 1) + " " + mYear
            temp = appUtilities!!.changeDateFormat("dd MM yyyy", "dd MMM, yy", temp)
            return temp
        }

    private fun saveDiagnosesDataByType(saveType: RecordSavingType) {
        try {
            if (encounterId != 0 && !diagName!!.text.toString().equals("", ignoreCase = true)) {
                val diagObj = JSONObject()
                diagObj.put("patient_id", patientId)
                diagObj.put("episode_id", episodeId)
                diagObj.put("encounter_id", encounterId)
                diagObj.put("diagnosis", diagName!!.text.toString())
                diagObj.put("status", diagStatus!!.selectedItem.toString())
                var temp = appUtilities!!.changeDateFormat(
                    "dd MMM, yy",
                    "dd-MM-yyyy",
                    diagPoisted!!.text.toString()
                )
                diagObj.put("posited_on", temp)
                diagObj.put("confirmed_ruledout_on", "")
                if (diagConfirm!!.visibility == View.VISIBLE) {
                    temp = appUtilities!!.changeDateFormat(
                        "dd MMM, yy",
                        "dd-MM-yyyy",
                        diagConfirm!!.text.toString()
                    )
                    diagObj.put("confirmed_ruledout_on", temp)
                }
                diagObj.put("selectedFromAutocomplete", selectedFromAutocomplete)
                if (callingFrom != null && callingFrom.equals("editRecord", ignoreCase = true)) {
                    diagObj.put("type", "diagnosisRecords")
                    diagObj.put("id", Record_ID)
                }
                saveDiagnoses(diagObj, saveType)
            } else {
                if (encounterId == 0) {
                    Toast.makeText(
                        this@EMRCreateRecordsFormActivity,
                        "Please select interaction",
                        Toast.LENGTH_SHORT
                    ).show()
                } else if (diagName!!.text.toString().equals("", ignoreCase = true)) {
                    Toast.makeText(
                        this@EMRCreateRecordsFormActivity,
                        "Please enter diagnoses value",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun saveInvestigationDataByType(saveType: RecordSavingType) {
        try {
            if (((encounterId != 0) && !investName!!.text.toString()
                    .equals("", ignoreCase = true) &&
                        !investParam!!.text.toString().equals("", ignoreCase = true) &&
                        !investValue!!.text.toString().equals("", ignoreCase = true))
            ) {
                val investObj = JSONObject()
                investObj.put("investigation_name", investName!!.text.toString())
                investObj.put("parameter", investParam!!.text.toString())
                investObj.put("value", investValue!!.text.toString())
                investObj.put("notes", investNote!!.text.toString())
                investObj.put("file_url", uploadImageResponse)
                investObj.put("patient_id", patientId)
                investObj.put("episode_id", episodeId)
                investObj.put("encounter_id", encounterId)
                investObj.put("file_type", "image")
                if (callingFrom != null && callingFrom.equals("editRecord", ignoreCase = true)) {
                    investObj.put("type", "investigationResultsRecords")
                    investObj.put("id", Record_ID)
                }
                saveInvestData(investObj, saveType)
            } else {
                if (encounterId == 0) {
                    Toast.makeText(
                        this@EMRCreateRecordsFormActivity,
                        "Please select interaction",
                        Toast.LENGTH_SHORT
                    ).show()
                } else if (investName!!.text.toString().equals("", ignoreCase = true)) {
                    Toast.makeText(
                        this@EMRCreateRecordsFormActivity,
                        "Please enter name for investigation",
                        Toast.LENGTH_SHORT
                    ).show()
                } else if (investParam!!.text.toString().equals("", ignoreCase = true)) {
                    Toast.makeText(
                        this@EMRCreateRecordsFormActivity,
                        "Please enter parameter for investigation",
                        Toast.LENGTH_SHORT
                    ).show()
                } else if (investValue!!.text.toString().equals("", ignoreCase = true)) {
                    Toast.makeText(
                        this@EMRCreateRecordsFormActivity,
                        "Please enter parameter value for investigation",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun saveSymptomBaseOnType(saveType: RecordSavingType) {
        try {
            if (encounterId != 0 && !TextUtils.isEmpty(symptomAutoCompleteTv!!.text.toString())) {
                val symObj = JSONObject()
                symObj.put("symptom_name", symptomAutoCompleteTv!!.text)
                val temp = appUtilities!!.changeDateFormat(
                    "dd MMM, yy",
                    "dd-MM-yyyy",
                    sympFirstReport!!.text.toString()
                )
                symObj.put("first_reported_on", temp)
                symObj.put("symptom_description", sympDesp!!.text.toString())
                symObj.put("symptom_status", sympStatus!!.selectedItem)
                symObj.put("patient_id", patientId)
                symObj.put("episode_id", episodeId)
                symObj.put("encounter_id", encounterId)
                if (callingFrom != null && callingFrom.equals("editRecord", ignoreCase = true)) {
                    symObj.put("type", "symptomsRecords")
                    symObj.put("id", Record_ID)
                }
                saveSymptom(symObj, saveType)
            } else {
                if (encounterId == 0) {
                    Toast.makeText(
                        this@EMRCreateRecordsFormActivity,
                        "Please select interaction",
                        Toast.LENGTH_SHORT
                    ).show()
                } else if (TextUtils.isEmpty(symptomAutoCompleteTv!!.text.toString())) {
                    Toast.makeText(
                        this@EMRCreateRecordsFormActivity,
                        "Please enter symptom name",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    fun saveHandWrittenNotes(
        activity: Activity?,
        createdBy: Int,
        encounterId: Int,
        episodeId: Int,
        description: String?,
        fileUrl: String?,
        patientId: Int,
        from: String,
        medPrescription: Int,
        testPrescription: Int,
        validTill: String?,
        editRecord: String?,
        Record_ID: Int
    ) {
        if (TextUtils.isEmpty(uploadImageResponse)) {
            Toast.makeText(activity, "Please Upload Handwritten notes", Toast.LENGTH_LONG).show()
        } else if (TextUtils.isEmpty(handWrittenDescription!!.text.toString().trim { it <= ' ' })) {
            Toast.makeText(activity, "Enter a valid description", Toast.LENGTH_LONG).show()
        } else if (medPrescription == 2) {
            Toast.makeText(activity, "Select a valid Medicine Prescription", Toast.LENGTH_LONG)
                .show()
        } else if (testPrescription == 2) {
            Toast.makeText(activity, "Select a valid Test Prescription", Toast.LENGTH_LONG).show()
        } else {
            if (myClinicGlobalClass!!.isOnline) {
                saveDialog!!.show()
                emrCreateRecordsFormViewModel!!.saveHandwrittenNotes(
                    activity,
                    createdBy,
                    encounterId,
                    episodeId,
                    description,
                    fileUrl,
                    patientId,
                    medPrescription,
                    testPrescription,
                    validTill,
                    editRecord,
                    Record_ID
                ).observe(this@EMRCreateRecordsFormActivity, object : Observer<String?> {
                    override fun onChanged(value: String?) {
                        saveDialog!!.dismiss()
                        try {
                            val response = JSONObject(value)
                            if (response.getInt("status_code") == 200) {
                                val responseObj =
                                    response.getJSONObject("response").getJSONObject("response")
                                if (responseObj.has("id")) {
                                    if ((from == "saveandclose")) {
                                        if (editRecord.equals("editRecord", ignoreCase = true)) {
                                            val intent = Intent()
                                            intent.putExtra("editRecord", editRecord)
                                            setResult(RESULT_OK, intent)
                                            Toast.makeText(
                                                this@EMRCreateRecordsFormActivity,
                                                R.string.record_updated_successfully,
                                                Toast.LENGTH_LONG
                                            ).show()
                                            finish()
                                        } else {
                                            Toast.makeText(
                                                this@EMRCreateRecordsFormActivity,
                                                R.string.record_added_successfully,
                                                Toast.LENGTH_LONG
                                            ).show()
                                            finish()
                                        }
                                    } else {
                                        attachedImageLayout!!.visibility = View.GONE
                                        removeAndViewLayout!!.visibility = View.GONE
                                        handWrittenDescription!!.setText("")
                                        uploadImageResponse = ""
                                        medicinePrescriptionSpinner!!.setSelection(0)
                                        testPrescriptionSpinner!!.setSelection(0)
                                        Toast.makeText(
                                            this@EMRCreateRecordsFormActivity,
                                            R.string.record_added_successfully, Toast.LENGTH_LONG
                                        ).show()
                                    }
                                }
                            } else {
                                if (value != null) {
                                    errorHandler(this@EMRCreateRecordsFormActivity, value)
                                }
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                })
            } else {
                myClinicGlobalClass!!.noInternetConnection.showDialog(activity)
            }
        }
    }


    private fun prescriptionListItem() {
//        EMRAddRecordCategoryModel myList = new EMRAddRecordCategoryModel(
//                "Symptoms"
//        );
//        allPrescriptionList.add(myList);
//
//        myList = new EMRAddRecordCategoryModel(
//                "Comments"
//        );
//        allPrescriptionList.add(myList);
//        myList = new EMRAddRecordCategoryModel(
//                "Glucose"
//        );
//        allPrescriptionList.add(myList);
//        myList = new EMRAddRecordCategoryModel(
//                "Height"
//        );
//        allPrescriptionList.add(myList);
    }

    private fun openDateDialog(tv: TextView?) {
        if ((tv!!.text == "")) {
            val c = Calendar.getInstance()
            mYear = c[Calendar.YEAR]
            mMonth = c[Calendar.MONTH]
            mDay = c[Calendar.DAY_OF_MONTH]
        } else {
            val date =
                appUtilities!!.changeDateFormat("dd MMM, yy", "dd MM yyyy", tv.text.toString())
            val dSplit = date.split(" ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            mDay = dSplit[0].toInt()
            mMonth = dSplit[1].toInt() - 1
            mYear = dSplit[2].toInt()
        }
        val datePickerDialog = DatePickerDialog(this@EMRCreateRecordsFormActivity,
            object : OnDateSetListener {
                override fun onDateSet(
                    view: DatePicker, year: Int,
                    monthOfYear: Int, dayOfMonth: Int
                ) {
                    var temp: String? = dayOfMonth.toString() + "/" + (monthOfYear + 1) + "/" + year
                    temp = appUtilities!!.changeDateFormat("dd/MM/yyyy", "dd MMM, yy", temp)
                    tv.text = temp
                }
            }, mYear, mMonth, mDay
        )
        datePickerDialog.show()
    }

    private fun saveSymptom(symObj: JSONObject, saveType: RecordSavingType) {
        val loadingDialog = ProgressDialog(this)
        loadingDialog.setMessage(resources.getString(R.string.process_request))
        loadingDialog.setCancelable(false)
        loadingDialog.setInverseBackgroundForced(false)
        loadingDialog.show()
        emrConsultationNotesViewModel!!.saveSymptomRecord(this, symObj, saveType)
            .observe(this, object : Observer<String?> {
                override fun onChanged(value: String?) {
                    try {
                        val jsonObject = JSONObject(value)
                        if (jsonObject.getInt("status_code") == 200) {
                            when (saveType) {
                                RecordSavingType.SAVE_AND_CLOSE -> {
                                    val returnIntent = Intent()
                                    returnIntent.putExtra("result", "OK")
                                    setResult(RESULT_OK, returnIntent)
                                    finish()
                                    Toast.makeText(
                                        this@EMRCreateRecordsFormActivity,
                                        R.string.record_added_successfully, Toast.LENGTH_LONG
                                    ).show()
                                }
                                RecordSavingType.SAVE_AND_MORE -> {
                                    sympDesp!!.setText("")
                                    symptomAutoCompleteTv!!.setText("")
                                    sympFirstReport!!.text = currentDateFormat
                                    sympStatus!!.setSelection(0)
                                    Toast.makeText(
                                        this@EMRCreateRecordsFormActivity,
                                        R.string.record_added_successfully, Toast.LENGTH_LONG
                                    ).show()
                                }
                                RecordSavingType.UPDATE_SYMPTOMS -> {
                                    sympDesp!!.setText("")
                                    symptomAutoCompleteTv!!.setText("")
                                    sympFirstReport!!.text = currentDateFormat
                                    sympStatus!!.setSelection(0)
                                    val intent = Intent()
                                    intent.putExtra("editRecord", "editRecord")
                                    setResult(RESULT_OK, intent)
                                    finish()
                                    Toast.makeText(
                                        this@EMRCreateRecordsFormActivity,
                                        R.string.record_updated_successfully, Toast.LENGTH_LONG
                                    ).show()
                                }
                                else -> {}
                            }
                            loadingDialog.dismiss()
                        } else {
                            loadingDialog.dismiss()
                            if (value != null) {
                                errorHandler(this@EMRCreateRecordsFormActivity, value)
                            }
                        }
                    } catch (e: JSONException) {
                        e.printStackTrace()
                        loadingDialog.dismiss()
                    }
                }
            })
    }

    private fun selectMethodDialog() {
        //before inflating the custom alert dialog layout, we will get the current activity viewgroup
        val viewGroup = findViewById<ViewGroup>(android.R.id.content)

        //then we will inflate the custom alert dialog xml that we created
        val dialogView =
            LayoutInflater.from(this).inflate(R.layout.dialog_uploadimg_method, viewGroup, false)
        val camera = dialogView.findViewById<LinearLayout>(R.id.uploadMethodCamera)
        val attach = dialogView.findViewById<LinearLayout>(R.id.uploadMethodAttachment)
        val cameraButton = dialogView.findViewById<ImageButton>(R.id.cameraButton)
        val attachImageButton = dialogView.findViewById<ImageButton>(R.id.attachImageButton)


        //Now we need an AlertDialog.Builder object
        val builder = AlertDialog.Builder(this)

        //setting the view of the builder to our custom view that we already inflated
        builder.setView(dialogView)

        //finally creating the alert dialog and displaying it
        val alertDialog = builder.create()
        alertDialog.show()
        camera.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View) {
                alertDialog.dismiss()
                if (ActivityCompat.checkSelfPermission(
                        this@EMRCreateRecordsFormActivity,
                        permissionsRequired[0]
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    openCamera()
                } else {
                    ActivityCompat.requestPermissions(
                        this@EMRCreateRecordsFormActivity,
                        permissionsRequired,
                        PERMISSION_CALLBACK_CONSTANT
                    )
                }
            }
        })
        attach.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View) {
                alertDialog.dismiss()
                openFileDialog()
            }
        })
        cameraButton.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View) {
                alertDialog.dismiss()
                if (ActivityCompat.checkSelfPermission(
                        this@EMRCreateRecordsFormActivity,
                        permissionsRequired[0]
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    openCamera()
                } else {
                    ActivityCompat.requestPermissions(
                        this@EMRCreateRecordsFormActivity,
                        permissionsRequired,
                        PERMISSION_CALLBACK_CONSTANT
                    )
                }
            }
        })
        attachImageButton.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View) {
                alertDialog.dismiss()
                openFileDialog()
            }
        })
    }

    fun openCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        val photo = File(Environment.getExternalStorageDirectory(), "Pic1.png")
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            fileUri = Uri.fromFile(photo)
            intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri)
        } else {
            if (intent.resolveActivity(packageManager) != null) {
                //Create a file to store the image
                var photoFile: File? = null
                try {
                    photoFile = createImageFile()
                } catch (ex: IOException) {
                    ex.printStackTrace()
                }
                if (photoFile != null) {
                    fileUri = FileProvider.getUriForFile(
                        this,
                        applicationContext.packageName + ".provider",
                        photoFile
                    )
                    intent.putExtra(
                        MediaStore.EXTRA_OUTPUT,
                        fileUri
                    )
                }
            }
        }
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        if (intent.resolveActivity(applicationContext.packageManager) != null) {
            launcherCameraResults!!.launch(intent)
        }
    }

    private fun openFileDialog() {
        val intent = Intent()
        // Show only images, no videos or anything else
        intent.type = "*/*"
        val mimeTypes = arrayOf("image/*", "application/pdf")
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
        intent.action = Intent.ACTION_GET_CONTENT
        // Always show the chooser (if there are multiple options available)
        launchOpenFileResults!!.launch(Intent.createChooser(intent, "Select File"))
    }

    fun getFileDataFromDrawable(bitmap: Bitmap): ByteArray {
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 20, byteArrayOutputStream)
        return byteArrayOutputStream.toByteArray()
    }

    fun getFileName(uri: Uri?): String? {
        val projection = arrayOf(MediaStore.MediaColumns.DISPLAY_NAME)
        val cursor = managedQuery(uri, projection, null, null, null)
        if (cursor != null) {
            //HERE YOU WILL GET A NULLPOINTER IF CURSOR IS NULL
            //THIS CAN BE, IF YOU USED OI FILE MANAGER FOR PICKING THE MEDIA
            val column_index = cursor
                .getColumnIndexOrThrow(MediaStore.MediaColumns.DISPLAY_NAME)
            cursor.moveToFirst()
            return cursor.getString(column_index)
        } else return null
    }

    private fun uploadPDFFile(selectedFilePath: String?, uri: Uri?) {
        val resolver = applicationContext
            .contentResolver
        try {
            val pfd = resolver.openFileDescriptor((uri)!!, "r")
            val stream: InputStream = FileInputStream(pfd!!.fileDescriptor)
            val localfile = File(this.cacheDir, selectedFilePath)
            val localStream: OutputStream = FileOutputStream(localfile)
            IOUtils.copyStream(stream, localStream)
            pdfFile = localfile
            type = "pdf"
            uploadImage("pdf")
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }

    }

    private fun uploadImage(type: String) {
        val url = ApiUrls.uploadRecordImage
        val loadingDialog = ProgressDialog(this)
        if (type.equals("image", ignoreCase = true)) {
            loadingDialog.setMessage(resources.getString(R.string.uploading_image))
        } else {
            loadingDialog.setMessage(resources.getString(R.string.uploading_pdf))
        }
        loadingDialog.setCancelable(false)
        loadingDialog.setInverseBackgroundForced(false)
        loadingDialog.show()

        //our custom volley request
        val volleyMultipartRequest: AppImageUploader = object : AppImageUploader(
            Method.POST, url,
            object : Response.Listener<NetworkResponse> {
                override fun onResponse(response: NetworkResponse) {
                    try {
                        var rootObj = JSONObject(String(response.data))
                        //                            Log.d("Image Upload", rootObj.toString());
                        rootObj = rootObj.getJSONObject("response")
                        val url = rootObj.getString("url")
                        if (url != null && url != "") {
                            uploadImageResponse = url
                            if (categoryName.equals("Investigation Results", ignoreCase = true)) {
                                rlAttachmentParenInvestigation!!.visibility = View.VISIBLE
                                tvAttachmentNameInvestigation!!.text =
                                    "$attachedFileName file has been attached"
                            } else {
                                attachedImageLayout!!.visibility = View.VISIBLE
                                removeAndViewLayout!!.visibility = View.VISIBLE
                                attachedImage!!.text = "$attachedFileName file has been attached"
                            }
                            if (type.equals("image", ignoreCase = true)) {
                                Toast.makeText(
                                    applicationContext,
                                    resources.getString(R.string.upload_img_success),
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else if (type.equals("pdf", ignoreCase = true)) {
                                Toast.makeText(
                                    applicationContext,
                                    resources.getString(R.string.upload_pdf_success),
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                        isPDFFile = false
                        loadingDialog.dismiss()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            },
            object : Response.ErrorListener {
                override fun onErrorResponse(error: VolleyError) {
                    if (error is TimeoutError || error is NoConnectionError) {
                        Toast.makeText(
                            applicationContext,
                            resources.getString(R.string.slow_internet_connection),
                            Toast.LENGTH_LONG
                        ).show()
                    } else if (error is AuthFailureError) {
                        Toast.makeText(
                            applicationContext,
                            resources.getString(R.string.slow_internet_connection),
                            Toast.LENGTH_LONG
                        ).show()
                    } else if (error is ServerError) {
                        Toast.makeText(
                            applicationContext,
                            resources.getString(R.string.slow_internet_connection),
                            Toast.LENGTH_LONG
                        ).show()

                        //TODO
                    } else if (error is NetworkError) {
                        Toast.makeText(
                            applicationContext,
                            resources.getString(R.string.slow_internet_connection),
                            Toast.LENGTH_LONG
                        ).show()

                        //TODO
                    } else if (error is ParseError) {
                        Toast.makeText(
                            applicationContext,
                            resources.getString(R.string.slow_internet_connection),
                            Toast.LENGTH_LONG
                        ).show()

                        //TODO
                    } else {
                        Toast.makeText(
                            applicationContext,
                            resources.getString(R.string.upload_img_error),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    loadingDialog.dismiss()
                }
            }) {
            /*
             * If you want to add more parameters with the image
             * you can do it here
             * here we have only one parameter with the image
             * which is tags
             * */
            @Throws(AuthFailureError::class)
            override fun getParams(): Map<String, String>? {
                val params: MutableMap<String, String> = HashMap()
                //                params.put("path", "records/" + episodeId + "/");//old
                if (isInvestigationImageUpload) {
                    params["path"] =
                        "records_v2/investigation_files/" + ApiUrls.doctorId + "/" + "android" + "/" //new
                } else {
                    params["path"] =
                        "records_v2/images/" + ApiUrls.doctorId + "/" + "android" + "/" //new
                }
                return params
            }

            /*
             * Here we are passing image by renaming it with a unique name
             * */
            override fun getByteData(): Map<String, DataPart> {
                val params: MutableMap<String, DataPart> = HashMap()
                //                long imagename = System.currentTimeMillis();
                if (isPDFFile) {
                    try {
                        val bytesArray = ByteArray(pdfFile!!.length().toInt())
                        val fis = FileInputStream(pdfFile)
                        val a = fis.read(bytesArray) //read file into bytes[]
                        fis.close()
                        params["file"] = DataPart("Record" + ".pdf", bytesArray)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                } else {
                    val bitmap = (investigationImageView!!.drawable as BitmapDrawable).bitmap
                    params["file"] = DataPart("Record" + ".png", getFileDataFromDrawable(bitmap))
                }
                return params
            }

            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                val headers = HashMap<String, String>()
                //                headers.put("Content-Type", "application/json");
                headers["App-Origin"] = ApiUrls.appOrigin
                headers["Authorization"] =
                    "Bearer " + ApiUrls.loginToken //eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJodHRwOi8vNGRkYjMxNjkubmdyb2suaW8vYXBpL3YxL2F1dGgvdG9rZW4iLCJpYXQiOjE1MTgwODcyMzAsImV4cCI6MTUxOTI5NjgzMCwibmJmIjoxNTE4MDg3MjMwLCJqdGkiOiJ2UEJnY09hVGdGOU9YaFh4Iiwic3ViIjozMzk1LCJwcnYiOiIyM2JkNWM4OTQ5ZjYwMGFkYjM5ZTcwMWM0MDA4NzJkYjdhNTk3NmY3In0.Y3wU_NBXXhw--b75x4iwo6fmHQQzMLkla2gHrheWqeU");
                return headers
            }
        }

        //adding the request to volley
        volleyMultipartRequest.retryPolicy = DefaultRetryPolicy(
            120000,
            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        )
        Volley.newRequestQueue(this).add(volleyMultipartRequest)
    }

    @Throws(IOException::class)
    private fun createImageFile(): File {
        val timeStamp = SimpleDateFormat(
            "yyyyMMdd_HHmmss",
            Locale.getDefault()
        ).format(Date())
        val imageFileName = "IMG_" + timeStamp + "_"
        val storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val image = File.createTempFile(
            imageFileName,  /* prefix */
            ".png",  /* suffix */
            storageDir /* directory */
        )
        imageFilePath = image.absolutePath
        return image
    }

    //                        JSONArray rootArray = response.getJSONArray("response");
    private val docDiagnoses: Unit
        private get() {
            emrConsultationNotesViewModel!!.getDocDiagnosisArrayDetails(this)
                .observe(this, object : Observer<String?> {
                    override fun onChanged(value: String?) {
                        try {
                            val jsonObject = JSONObject(value)
                            if (jsonObject.getInt("status_code") == 200) {
                                val response = JSONObject(value).getJSONObject("response")
                                //                        JSONArray rootArray = response.getJSONArray("response");
                                val diagArr = response.getJSONArray("response")
                                if (diagArr.length() > 0) {
                                    for (i in 0 until diagArr.length()) {
                                        val diagObj = diagArr.getJSONObject(i)
                                        diagNameArr!!.add(diagObj.getString("diagnosis_name"))
                                        diagIdArr!!.add(diagObj.getInt("id"))
                                        val tv = TextView(this@EMRCreateRecordsFormActivity)
                                        tv.text = diagNameArr!!.get(i)
                                        val layoutParams = LinearLayout.LayoutParams(
                                            LinearLayout.LayoutParams.MATCH_PARENT,
                                            LinearLayout.LayoutParams.WRAP_CONTENT
                                        )
                                        layoutParams.setMargins(30, 20, 30, 30)
                                        tv.setOnClickListener(object : View.OnClickListener {
                                            override fun onClick(v: View) {
                                                val tv1 = v as TextView
                                                diagName!!.setText(tv1.text.toString())
                                                diagValueLayout!!.visibility = View.GONE
                                                selectedFromAutocomplete = true
                                            }
                                        })
                                        diagValueLayout!!.addView(tv, layoutParams)
                                    }
                                }
                            } else {
                                if (value != null) {
                                    errorHandler(this@EMRCreateRecordsFormActivity, value)
                                }
                            }
                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }
                    }
                })
        }

    private fun saveDiagnoses(diagObj: JSONObject, saveType: RecordSavingType) {
        val loadingDialog = ProgressDialog(this)
        loadingDialog.setMessage(resources.getString(R.string.process_request))
        loadingDialog.setCancelable(false)
        loadingDialog.setInverseBackgroundForced(false)
        loadingDialog.show()
        emrConsultationNotesViewModel!!.saveDiagnosisRecord(this, diagObj, saveType)
            .observe(this, object : Observer<String?> {
                override fun onChanged(value: String?) {
                    try {
                        val jsonObject = JSONObject(value)
                        if (jsonObject.getInt("status_code") == 200) {
                            loadingDialog.dismiss()
                            when (saveType) {
                                RecordSavingType.SAVE_AND_CLOSE -> {
                                    val returnIntent = Intent()
                                    returnIntent.putExtra("result", "OK")
                                    setResult(RESULT_OK, returnIntent)
                                    finish()
                                    Toast.makeText(
                                        this@EMRCreateRecordsFormActivity,
                                        R.string.record_added_successfully, Toast.LENGTH_LONG
                                    ).show()
                                }
                                RecordSavingType.SAVE_AND_MORE -> {
                                    diagName!!.setText("")
                                    diagStatus!!.setSelection(0)
                                    diagPoisted!!.text = currentDateFormat
                                    diagConfirm!!.text = currentDateFormat
                                    Toast.makeText(
                                        this@EMRCreateRecordsFormActivity,
                                        R.string.record_added_successfully, Toast.LENGTH_LONG
                                    ).show()
                                }
                                RecordSavingType.UPDATE_DIAGNOSIS -> {
                                    val intent = Intent()
                                    intent.putExtra("editRecord", "editRecord")
                                    setResult(RESULT_OK, intent)
                                    finish()
                                    Toast.makeText(
                                        this@EMRCreateRecordsFormActivity,
                                        R.string.record_updated_successfully, Toast.LENGTH_LONG
                                    ).show()
                                }
                                else -> {}
                            }
                        } else {
                            loadingDialog.dismiss()
                            if (value != null) {
                                errorHandler(this@EMRCreateRecordsFormActivity, value)
                            }
                        }
                    } catch (e: JSONException) {
                        e.printStackTrace()
                        loadingDialog.dismiss()
                    }
                }
            })
    }

    private fun saveInvestData(investigationObj: JSONObject, saveType: RecordSavingType) {
        val loadingDialog = ProgressDialog(this)
        loadingDialog.setMessage(resources.getString(R.string.process_request))
        loadingDialog.setCancelable(false)
        loadingDialog.setInverseBackgroundForced(false)
        loadingDialog.show()
        emrConsultationNotesViewModel!!.saveInvestigationRecord(this, investigationObj, saveType)
            .observe(this, object : Observer<String?> {
                override fun onChanged(value: String?) {
                    try {
                        val jsonObject = JSONObject(value)
                        if (jsonObject.getInt("status_code") == 200) {
                            loadingDialog.dismiss()
                            when (saveType) {
                                RecordSavingType.SAVE_AND_MORE -> {
                                    investName!!.setText("")
                                    investParam!!.setText("")
                                    investValue!!.setText("")
                                    investNote!!.setText("")
                                    uploadImageResponse = null
                                    rlAttachmentParenInvestigation!!.visibility = View.GONE
                                    tvAttachmentNameInvestigation!!.text = ""
                                    Toast.makeText(
                                        this@EMRCreateRecordsFormActivity,
                                        R.string.record_added_successfully, Toast.LENGTH_LONG
                                    ).show()
                                }
                                RecordSavingType.SAVE_AND_CLOSE -> {
                                    val returnIntent = Intent()
                                    returnIntent.putExtra("result", "OK")
                                    setResult(RESULT_OK, returnIntent)
                                    finish()
                                    Toast.makeText(
                                        this@EMRCreateRecordsFormActivity,
                                        R.string.record_added_successfully, Toast.LENGTH_LONG
                                    ).show()
                                }
                                RecordSavingType.UPDATE_INVESTIGATION -> {
                                    investName!!.setText("")
                                    investParam!!.setText("")
                                    investValue!!.setText("")
                                    investNote!!.setText("")
                                    uploadImageResponse = null
                                    val intent = Intent()
                                    intent.putExtra("editRecord", "editRecord")
                                    setResult(RESULT_OK, intent)
                                    finish()
                                    Toast.makeText(
                                        this@EMRCreateRecordsFormActivity,
                                        R.string.record_updated_successfully, Toast.LENGTH_LONG
                                    ).show()
                                }
                                else -> {}
                            }
                        } else {
                            loadingDialog.dismiss()
                            if (value != null) {
                                errorHandler(this@EMRCreateRecordsFormActivity, value)
                            }
                        }
                    } catch (e: JSONException) {
                        e.printStackTrace()
                        loadingDialog.dismiss()
                    }
                }
            })
    }

    companion object {
        private val PERMISSION_CALLBACK_CONSTANT = 100
        private val TRIGGER_AUTO_COMPLETE = 100
        private val AUTO_COMPLETE_DELAY: Long = 300
    }
}