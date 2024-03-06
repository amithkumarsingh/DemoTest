package com.whitecoats.clinicplus.activities

import android.Manifest
import android.app.*
import android.app.DatePickerDialog.OnDateSetListener
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.PorterDuff
import android.graphics.Typeface
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.*
import android.provider.MediaStore
import android.text.Editable
import android.text.InputType
import android.text.TextUtils
import android.text.TextWatcher
import android.text.method.ScrollingMovementMethod
import android.util.Log
import android.view.*
import android.view.View.OnFocusChangeListener
import android.view.inputmethod.EditorInfo
import android.widget.*
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.android.volley.*
import com.android.volley.toolbox.Volley
import com.google.android.gms.common.util.IOUtils
import com.whitecoats.clinicplus.*
import com.whitecoats.clinicplus.R
import com.whitecoats.clinicplus.adapters.AutoCompleteAdapter
import com.whitecoats.clinicplus.apis.ApiGetPostMethodCalls
import com.whitecoats.clinicplus.apis.ApiUrls
import com.whitecoats.clinicplus.models.AddMedicineReq
import com.whitecoats.clinicplus.models.AddMedicineRes
import com.whitecoats.clinicplus.utils.APIResponse
import com.whitecoats.clinicplus.utils.ErrorHandlerClass.errorHandler
import com.whitecoats.clinicplus.viewmodels.CreateEMRRecordViewModel
import com.whitecoats.clinicplus.viewmodels.EMRCreateRecordsFormViewModel
import com.whitecoats.model.MedicationAutoSuggestion
import com.whitecoats.model.MedicineNameResponse
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.*
import java.text.*
import java.util.*

class EMRCreateRecordActivity : AppCompatActivity(), OnDateSetListener {
    private var categoryName: String? = null
    private var categoryId: String? = null
    private var formLayout: LinearLayout? = null
    private var submitRecord: Button? = null
    private var dateTv: TextView? = null
    private var mandatoryField: HashMap<Int, String>? = null
    private var fileUri: Uri? = null
    private var formImage: ImageView? = null
    var loadingDialog: ProgressDialog? = null
    private var formImageID = 0
    private var uploadImageResponse: String? = null
    private var taskHiddenTv: ArrayList<TextView>? = null
    private var taskRepeatObj: JSONObject? = null
    private var callingActivity: String? = null
    private var otpLoading: ProgressDialog? = null

    //task hidden field
    private var taskHiddenEt: ArrayList<EditText>? = null
    private var taskHidderSpinner: ArrayList<Spinner>? = null

    //investigation hidden field
    private var investDateList: ArrayList<TextView>? = null
    private var investEditText: ArrayList<EditText>? = null

    //immunization hidden field
    private var immuDateList: ArrayList<TextView>? = null
    private var immuEditText: ArrayList<EditText>? = null

    //problems hidden field
    private var probEditText: ArrayList<TextView>? = null

    //prcedure hidden field
    private var proceedTextView: ArrayList<TextView>? = null
    private var catFieldObjArr: JSONArray? = null
    private val permissionsRequired = arrayOf(Manifest.permission.CAMERA)
    private val storagePermission = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
    private var imageFilePath: String? = null
    private var isPDFFile = false
    private var pdfFile: File? = null
    private var patientID = 0
    private var episodeID = 0
    private var encounterID = 0
    private var type: String? = null
    var emrRecordFormLoadProgressbar: RelativeLayout? = null

    /*ENGG-3691 -- Refactoring the code by removing the deprecate functions(StartActivityForResults)*/
    private var launchOpenFileResults: ActivityResultLauncher<Intent>? = null
    private var launcherCameraResults: ActivityResultLauncher<Intent>? = null
    private var appUtils: AppUtilities? = null
    private var timeFormat = "HH:mm"
    private var simpleDateFormat: SimpleDateFormat? = null
    private var is24Hrformat = true
    private var apiGetPostMethodCalls: ApiGetPostMethodCalls? = null
    private var recordDetailObj: String? = null
    private var callingFrom: String? = ""
    private var selectedRecordDetailsEdit: JSONObject? = null
    private var editValue = ""
    private var progressDialog: ProgressDialog? = null
    private var emrCreateRecordsFormViewModel: EMRCreateRecordsFormViewModel? = null
    private var selectedRecordID = ""
    private var dynamicEncounterDataIds: String? = null
    private var spinnerArrayDocList: ArrayList<String?>? = null
    private var defaultsTaskCat = ""
    private var attachmentType = ""

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    private val permissionsRequiredSDK33Higher =
        arrayOf(Manifest.permission.READ_MEDIA_IMAGES, Manifest.permission.READ_MEDIA_VIDEO)
    private var autoCompleteListAdapter: AutoCompleteAdapter? = null
    private var handler: Handler? = null
    var suggestionsList: MutableList<MedicineNameResponse>? = null
    private var createEMRRecordViewModel: CreateEMRRecordViewModel? = null
    private var searchText = ""
    private var dialog: Dialog? = null
    private var isMedicineAddedToDatabase = true
    private var isMedicineNameSelFromListOrAdded = true
    private var etMedName: EditText? = null
    private var etMedCompany: EditText? = null
    private var medName = ""
    private var medCompany = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_patient_create_record2)
        progressDialog = ProgressDialog(this@EMRCreateRecordActivity)
        progressDialog!!.setMessage("Please wait while file is opening...")
        emrCreateRecordsFormViewModel = ViewModelProvider(this).get(
            EMRCreateRecordsFormViewModel::class.java
        )
        emrCreateRecordsFormViewModel!!.init()
        formLayout = findViewById(R.id.recordCreateFormLayout)
        emrRecordFormLoadProgressbar = findViewById(R.id.emrRecordFormLoadProgressbar)
        submitRecord = findViewById(R.id.recordCreateSubmit)
        categoryId = intent.getStringExtra("CategoryId")
        categoryName = intent.getStringExtra("CategoryName")
        type = intent.getStringExtra("Type")
        patientID = intent.getIntExtra("PatientId", 0)
        episodeID = intent.getIntExtra("EpisodeId", 0)
        encounterID = intent.getIntExtra("EncounterID", 0)
        if (intent.hasExtra("dynamicEncounterDataIds")) {
            dynamicEncounterDataIds = intent.getStringExtra("dynamicEncounterDataIds")
        }
        if (intent.hasExtra("callingFrom")) {
            callingFrom = intent.getStringExtra("callingFrom")
        }
        if (callingFrom.equals("editRecord", ignoreCase = true)) {
            submitRecord!!.text = "Update Record"
            try {
                val jsonObject = JSONObject(dynamicEncounterDataIds)
                patientID = jsonObject.getInt("patient_id")
                episodeID = jsonObject.getInt("episode_id")
                encounterID = jsonObject.getInt("encounter_id")
            } catch (err: JSONException) {
                Log.d("Error", err.toString())
            }
        } else {
            submitRecord!!.text = getString(R.string.add_record)
        }
        recordDetailObj = intent.getStringExtra("RecordDetail")
        try {
            selectedRecordDetailsEdit = JSONObject(recordDetailObj)
            selectedRecordID = selectedRecordDetailsEdit!!.getString("record_id")
        } catch (e: Exception) {
            e.printStackTrace()
        }
        mandatoryField = HashMap()
        loadingDialog = ProgressDialog(this)
        formImage = ImageView(this)
        taskHiddenTv = ArrayList()
        taskHiddenEt = ArrayList()
        taskHidderSpinner = ArrayList()
        taskRepeatObj = JSONObject()
        investDateList = ArrayList()
        investEditText = ArrayList()
        immuDateList = ArrayList()
        immuEditText = ArrayList()
        catFieldObjArr = JSONArray()
        probEditText = ArrayList()
        proceedTextView = ArrayList()
        callingActivity = intent.getStringExtra("ActivityName")
        appUtils = AppUtilities()
        apiGetPostMethodCalls = ApiGetPostMethodCalls()
        if (appUtils!!.timeFormatPreferences(applicationContext) == 12) {
            timeFormat = "hh:mm aa"
            is24Hrformat = false
        }
        simpleDateFormat = SimpleDateFormat(timeFormat)
        val symbols = DateFormatSymbols(Locale.US)
        simpleDateFormat!!.dateFormatSymbols = symbols
        //        Log.d("Category ID", categoryId);
//        Log.d("Category ID", categoryName);
        val toolbar = findViewById<Toolbar>(R.id.recordCreate2Toolbar)
        toolbar.title = categoryName
        val backArrow = resources.getDrawable(R.drawable.ic_arrow_back)
        backArrow.setColorFilter(resources.getColor(R.color.colorWhite), PorterDuff.Mode.SRC_ATOP)
        toolbar.navigationIcon = backArrow // your drawable
        setSupportActionBar(toolbar)

        /*ENGG-3691 -- Refactoring the code by removing the deprecate functions(StartActivityForResults)*/
        //Start
        launchOpenFileResults = registerForActivityResult<Intent, ActivityResult>(
            ActivityResultContracts.StartActivityForResult()
        ) { result: ActivityResult ->
            //Request code 2
            val data = result.data
            val resultCode = result.resultCode
            if (resultCode == RESULT_OK) {
                val uri = data!!.data
                val fileName = getFileName(uri)
                Log.i("filenme", (fileName)!!)
                if (fileName!!.endsWith(".jpg") || fileName.endsWith(".jpeg") || fileName.endsWith(".png") || fileName.endsWith(
                        ".pdf"
                    )
                ) {
                    try {
                        if (fileName.contains("pdf")) {
                            isPDFFile = true
                            formImage!!.visibility = View.GONE
                            attachmentType = "Pdf"
                            uploadPDFFile(fileName, uri)
                        } else {
                            val bitmapImage =
                                MediaStore.Images.Media.getBitmap(contentResolver, uri)
                            val nh = (bitmapImage.height * (720.0 / bitmapImage.width)).toInt()
                            val scaled = Bitmap.createScaledBitmap(bitmapImage, 720, nh, true)
                            formImage!!.setImageBitmap(scaled)
                            formImage!!.visibility = View.VISIBLE
                            attachmentType = "Image"
                            uploadImage()
                        }
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                } else {
                    Toast.makeText(
                        this@EMRCreateRecordActivity,
                        "Please Upload image or pdf files only",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
        launcherCameraResults = registerForActivityResult<Intent, ActivityResult>(
            ActivityResultContracts.StartActivityForResult()
        ) { result: ActivityResult ->
            //Request code 1
            val data = result.data
            val resultCode = result.resultCode
            if (resultCode == RESULT_OK) {
                try {
                    val contentResolver = contentResolver

                    // Use the content resolver to open camera taken image input stream through image uri.
                    val inputStream = contentResolver.openInputStream((fileUri)!!)

                    // Decode the image input stream to a bitmap use BitmapFactory.
                    val pictureBitmap = BitmapFactory.decodeStream(inputStream)
                    val nh = (pictureBitmap.height * (720.0 / pictureBitmap.width)).toInt()
                    val scaled = Bitmap.createScaledBitmap(pictureBitmap, 720, nh, true)
                    formImage!!.setImageBitmap(scaled)
                    // Set the camera taken image bitmap clinicplus the image view component to display.
                    formImage!!.visibility = View.VISIBLE
                    attachmentType = "Image"
                    uploadImage()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
        //End
        toolbar.setNavigationOnClickListener {
            onBackPressed() // Implemented by activity
        }
        organisationDoctorsList
        submitRecord!!.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View) {
                if (categoryName.equals(
                        resources.getString(R.string.common_task),
                        ignoreCase = true
                    )
                ) {
                    taskFormData
                } else {
                    formData
                }
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

    private val organisationDoctorsList: Unit
        private get() {
            val URL = ApiUrls.getOrganisaitionDocList
            apiGetPostMethodCalls!!.volleyApiRequestData(URL,
                Request.Method.GET,
                null,
                this@EMRCreateRecordActivity,
                object : VolleyCallback {
                    override fun onSuccess(result: String) {
                        spinnerArrayDocList = ArrayList()
                        try {
                            val res = JSONObject(result)
                            val docList = res.getJSONArray("response")
                            if (docList.length() > 0) {
                                for (i in 0 until docList.length()) {
                                    val docDetails = docList.getJSONObject(i)
                                    docDetails.getString("fname")
                                    if (!docDetails.getString("fname")
                                            .equals("", ignoreCase = true)
                                    ) {
                                        spinnerArrayDocList!!.add(docDetails.getString("fname"))
                                    }
                                }
                            } else {
                                spinnerArrayDocList!!.add(MainActivity.docName)
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                            spinnerArrayDocList!!.add(MainActivity.docName)
                        }
                        getRecordForm(categoryId)
                    }

                    override fun onError(err: String) {
                        getRecordForm(categoryId)
                    }
                })
        }

    private fun getRecordForm(id: String?) {
        val url = ApiUrls.getRecordStructure + "?record_category_id=" + id

        // prepare the Request
        apiGetPostMethodCalls!!.volleyApiRequestData(url,
            Request.Method.GET,
            null,
            this@EMRCreateRecordActivity,
            object : VolleyCallback {
                override fun onSuccess(result: String) {
                    emrRecordFormLoadProgressbar!!.visibility = View.GONE
                    try {
                        val response = JSONObject(result)
                        if (categoryName.equals(
                                resources.getString(R.string.common_task),
                                ignoreCase = true
                            )
                        ) {
                            val taskObj = response.optJSONArray("response")
                            for (i in 0 until taskObj.length()) {
                                var fieldObj = taskObj.getJSONObject(i)
                                if (fieldObj["id"] is Int) {
                                    val fieldId = fieldObj.getInt("id")
                                    val fieldName = fieldObj.getString("field")
                                    val fieldType = fieldObj.getJSONArray("field_type")
                                    fieldObj = fieldType.getJSONObject(0)
                                    val type = fieldObj.getInt("type")
                                    val isMand = fieldObj.getInt("is_mandatory")
                                    fieldObj = taskObj.getJSONObject(i)
                                    val fieldValues =
                                        fieldObj.getJSONArray("field_values").getString(0)
                                    defaultsTaskCat = fieldObj.getString("default_value")
                                    editValue = ""
                                    if (callingFrom.equals("editRecord", ignoreCase = true)) {
                                        if (checkIfKeyExists(selectedRecordDetailsEdit, fieldId)) {
                                            val keyValueData =
                                                selectedRecordDetailsEdit!![fieldId.toString()]
                                            if (keyValueData is String) {
                                                editValue =
                                                    selectedRecordDetailsEdit!!.getString(fieldId.toString())
                                            } else if (keyValueData is Int) {
                                                editValue =
                                                    selectedRecordDetailsEdit!!.getInt(fieldId.toString())
                                                        .toString()
                                            }
                                        }
                                        if (type == 5) {
                                            if (editValue.equals(
                                                    "yes",
                                                    ignoreCase = true
                                                ) || editValue.equals(
                                                    "Attachments",
                                                    ignoreCase = true
                                                ) || editValue.equals("File", ignoreCase = true)
                                            ) {
                                                editValue =
                                                    selectedRecordDetailsEdit!!.getString("url")
                                            }
                                        }
                                        createTaskForm(
                                            fieldId,
                                            fieldName,
                                            type,
                                            isMand,
                                            fieldValues,
                                            editValue,
                                            defaultsTaskCat
                                        )
                                    } else {
                                        createTaskForm(
                                            fieldId,
                                            fieldName,
                                            type,
                                            isMand,
                                            fieldValues,
                                            "",
                                            defaultsTaskCat
                                        )
                                    }
                                } else {
                                    taskRepeatObj = taskObj.getJSONObject(i)
                                }
                            }
                        } else {
                            val formArr = response.getJSONArray("response")
                            for (i in 0 until formArr.length()) {
                                val fieldObj = formArr.getJSONObject(i)
                                val fieldType = fieldObj.getJSONArray("field_type")
                                for (j in 0 until fieldType.length()) {
                                    val fieldName = fieldObj.getString("field")
                                    val fieldTypeObj = fieldType.getJSONObject(j)
                                    val type = fieldTypeObj.getInt("type")
                                    val isMand = fieldTypeObj.getInt("is_mandatory")
                                    val fieldId = fieldTypeObj.optInt("id")
                                    val fieldValues =
                                        fieldObj.getJSONArray("field_values").getString(0)
                                    val defaultValueObj = fieldObj["default_value"]
                                    var defaults = ""
                                    if (defaultValueObj is JSONArray) {
                                        val defaultValueJsonArray = defaultValueObj
                                        for (k in 0 until defaultValueJsonArray.length()) {
                                            val innerObj = defaultValueJsonArray.getJSONObject(k)
                                            val id = innerObj.optInt("id")
                                            if (id == fieldId) {
                                                defaults = innerObj.optString("default_value")
                                                break
                                            }
                                        }
                                    } else if (defaultValueObj is String) {
                                        defaults = fieldObj.getString("default_value")
                                    }
                                    editValue = ""
                                    if (callingFrom.equals("editRecord", ignoreCase = true)) {
                                        if (checkIfKeyExists(selectedRecordDetailsEdit, fieldId)) {
                                            val keyValueData =
                                                selectedRecordDetailsEdit!![fieldId.toString()]
                                            if (keyValueData is String) {
                                                editValue =
                                                    selectedRecordDetailsEdit!!.getString(fieldId.toString())
                                            } else if (keyValueData is Int) {
                                                editValue =
                                                    selectedRecordDetailsEdit!!.getInt(fieldId.toString())
                                                        .toString()
                                            }
                                        }
                                        if (type == 5) {
                                            if (editValue.equals(
                                                    "yes",
                                                    ignoreCase = true
                                                ) || editValue.equals(
                                                    "Attachments",
                                                    ignoreCase = true
                                                ) || editValue.equals("File", ignoreCase = true)
                                            ) {
                                                editValue =
                                                    selectedRecordDetailsEdit!!.getString("url")
                                            }
                                        }
                                        createformElement(
                                            fieldId,
                                            fieldName,
                                            type,
                                            isMand,
                                            fieldValues,
                                            defaults,
                                            editValue
                                        )
                                    } else {
                                        createformElement(
                                            fieldId,
                                            fieldName,
                                            type,
                                            isMand,
                                            fieldValues,
                                            defaults,
                                            ""
                                        )
                                    }
                                    val catFieldObj = JSONObject()
                                    catFieldObj.put("id", fieldId)
                                    catFieldObj.put("name", fieldName)
                                    catFieldObj.put("type", type)
                                    catFieldObj.put("isMand", isMand)
                                    catFieldObj.put("values", fieldValues)
                                    catFieldObj.put("defaults", defaults)
                                    catFieldObjArr!!.put(catFieldObj)
                                }
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                override fun onError(err: String) {
                    errorHandler(this@EMRCreateRecordActivity, err)
                }
            })
    }

    fun viewUploadedImageOrPDF() {
        progressDialog!!.show()
        emrCreateRecordsFormViewModel!!.getImagePath(
            this@EMRCreateRecordActivity,
            uploadImageResponse!!.trim { it <= ' ' })
            .observe(this@EMRCreateRecordActivity, object : Observer<String?> {
                override fun onChanged(value: String?) {
                    progressDialog!!.dismiss()
                    try {
                        val response = JSONObject(value)
                        if (response.getInt("status_code") == 200) {
                            val completeUrl =
                                response.getJSONObject("response").getString("response")
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(completeUrl))
                            startActivity(Intent.createChooser(intent, "Browse with"))
                        } else {
                            if (value != null) {
                                errorHandler(this@EMRCreateRecordActivity, value)
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            })
    }

    private fun createformElement(
        fieldId: Int, fieldName: String, fieldType: Int, isMandatory: Int,
        fieldValue: String, defaults: String, editValue: String?
    ) {
        when (fieldType) {
            1 -> {
                val editText = EditText(this)
                // Create a LayoutParams for TextView
                val lp = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,  // Width of TextView
                    150
                )
                lp.setMargins(0, 30, 0, 0)
                editText.layoutParams = lp
                editText.background = resources.getDrawable(R.drawable.drawable_rectangle_shape)
                editText.setPadding(40, 10, 10, 10)
                editText.hint = fieldName
                editText.id = fieldId
                if (isMandatory == 1) {
                    editText.hint = fieldName + resources.getString(R.string.mandatory_hint)
                    mandatoryField!![fieldId] = fieldName
                    editText.background =
                        resources.getDrawable(R.drawable.drawable_rectangle_shape_red)
                }
                if (categoryId.equals("5", ignoreCase = true)) {
                    investEditText!!.add(editText)
                } else if (categoryId.equals("2", ignoreCase = true)) {
                    if (fieldId == 18) {
                        immuEditText!!.add(editText)
                    }
                }
                if (callingFrom.equals("editRecord", ignoreCase = true)) {
                    if (editValue != null && !editValue.equals(
                            "",
                            ignoreCase = true
                        )
                    ) editText.setText(editValue)
                }
                formLayout!!.addView(editText)
            }
            2 -> {
                var editText = EditText(this)
                // Create a LayoutParams for TextView
                var lp = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,  // Width of TextView
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
                lp.setMargins(0, 30, 0, 0)
                editText.setLayoutParams(lp)
                editText.setBackground(resources.getDrawable(R.drawable.drawable_rectangle_shape))
                editText.setPadding(40, 10, 10, 10)
                editText.setHint(fieldName)
                editText.setId(fieldId)
                if (isMandatory == 1) {
                    editText.setHint(fieldName + resources.getString(R.string.mandatory_hint))
                    mandatoryField!![fieldId] = fieldName
                    editText.setBackground(resources.getDrawable(R.drawable.drawable_rectangle_shape_red))
                }
                editText.setSingleLine(false)
                editText.setImeOptions(EditorInfo.IME_FLAG_NO_ENTER_ACTION)
                editText.setInputType(InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_MULTI_LINE)
                editText.setLines(5)
                editText.setMaxLines(10)
                editText.setVerticalScrollBarEnabled(true)
                editText.setMovementMethod(ScrollingMovementMethod.getInstance())
                editText.setScrollBarStyle(View.SCROLLBARS_INSIDE_INSET)
                if (callingFrom.equals("editRecord", ignoreCase = true)) {
                    if (editValue != null && !editValue.equals(
                            "",
                            ignoreCase = true
                        )
                    ) editText.setText(editValue)
                }
                formLayout!!.addView(editText)
            }
            3 -> {
                var editText = EditText(this)
                // Create a LayoutParams for TextView
                var lp = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,  // Width of TextView
                    150
                )
                lp.setMargins(0, 30, 0, 0)
                editText.setLayoutParams(lp)
                editText.setBackground(resources.getDrawable(R.drawable.drawable_rectangle_shape))
                editText.setPadding(40, 10, 10, 10)
                editText.setHint(fieldName)
                editText.setId(fieldId)
                editText.setInputType(InputType.TYPE_CLASS_NUMBER)
                if (isMandatory == 1) {
                    editText.setHint(fieldName + resources.getString(R.string.mandatory_hint))
                    mandatoryField!![fieldId] = fieldName
                    editText.setBackground(resources.getDrawable(R.drawable.drawable_rectangle_shape_red))
                }
                if (callingFrom.equals("editRecord", ignoreCase = true)) {
                    if (editValue != null && !editValue.equals(
                            "",
                            ignoreCase = true
                        )
                    ) editText.setText(editValue)
                }
                formLayout!!.addView(editText)
            }
            4, 6 -> {
                val textView = TextView(this)
                var lp = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,  // Width of TextView
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
                lp.setMargins(0, 30, 0, 0)
                textView.layoutParams = lp
                textView.text = fieldName
                textView.setTypeface(null, Typeface.BOLD)
                formLayout!!.addView(textView)
                val spinner = Spinner(this)
                val spinnerArray = ArrayList<String?>()
                if (defaults.equals("OrgDoctors", ignoreCase = true)) {
                    spinnerArray.addAll((spinnerArrayDocList)!!)
                } else {
                    val valueArr = fieldValue.split(",".toRegex()).dropLastWhile { it.isEmpty() }
                        .toTypedArray()
                    var i = 0
                    while (i < valueArr.size) {
                        spinnerArray.add(valueArr[i])
                        i++
                    }
                }
                spinnerArray.add(0, resources.getString(R.string.select_option))
                val spinnerArrayAdapter =
                    ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, spinnerArray)
                spinner.adapter = spinnerArrayAdapter
                spinner.id = fieldId
                spinner.background = resources.getDrawable(R.drawable.drawable_rectangle_shape)
                if (isMandatory == 1) {
                    mandatoryField!![fieldId] = fieldName
                    spinner.background =
                        resources.getDrawable(R.drawable.drawable_rectangle_shape_red)
                }
                spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                        adapterView: AdapterView<*>?,
                        view: View,
                        i: Int,
                        l: Long
                    ) {
                        if (categoryId.equals("5", ignoreCase = true)) {
                            showHiddenInvestField(i)
                        } else if (categoryId.equals("2", ignoreCase = true)) {
                            showHiddenImmuField(i)
                        } else if (categoryId.equals("14", ignoreCase = true)) {
                            showHiddenProblemField(i)
                        } else if (categoryId.equals("4", ignoreCase = true)) {
                            showHiddenProcedureField(i)
                        }
                    }

                    override fun onNothingSelected(adapterView: AdapterView<*>?) {}
                }
                spinner.setSelection(getIndex(spinner, defaults))
                lp = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,  // Width of TextView
                    150
                )
                lp.setMargins(0, 30, 0, 0)
                //                spinner.setLayoutParams(lp);
//                spinner.setBackground(getResources().getDrawable(R.drawable.rectangle_view));
                if (callingFrom.equals("editRecord", ignoreCase = true)) {
                    if (editValue != null && !editValue.equals(
                            "",
                            ignoreCase = true
                        )
                    ) spinner.setSelection(getIndex(spinner, editValue))
                }
                formLayout!!.addView(spinner)
            }
            5 -> {
                val linearLayoutParent = LinearLayout(this)
                var lp = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,  // Width of TextView
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
                linearLayoutParent.layoutParams = lp
                linearLayoutParent.orientation = LinearLayout.VERTICAL
                if ((callingFrom.equals("editRecord", ignoreCase = true) && (editValue != null) &&
                            !editValue.equals("", ignoreCase = true) && !editValue.equals(
                        "No",
                        ignoreCase = true
                    ))
                ) {
                    val linearLayoutView = LinearLayout(this)
                    lp = LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,  // Width of TextView
                        80
                    )
                    lp.setMargins(0, 12, 0, 0)
                    lp.gravity = Gravity.CENTER
                    linearLayoutView.layoutParams = lp
                    linearLayoutView.orientation = LinearLayout.HORIZONTAL
                    linearLayoutParent.addView(linearLayoutView)
                    val imageView_attached = ImageView(this)
                    lp = LinearLayout.LayoutParams(
                        50,  // Width of TextView
                        50
                    )
                    lp.setMargins(0, 10, 10, 0)
                    //lp.weight = 0.4f;
                    imageView_attached.layoutParams = lp
                    imageView_attached.setColorFilter(
                        ContextCompat.getColor(
                            this,
                            R.color.colorAccent
                        ), PorterDuff.Mode.SRC_IN
                    )
                    imageView_attached.setImageResource(R.drawable.ic_attachment)
                    linearLayoutView.addView(imageView_attached)
                    var textView = TextView(this)
                    textView.setText("View Attachment")
                    textView.setTextColor(resources.getColor(R.color.colorAccent))
                    //  textView.setTypeface(null, Typeface.BOLD);
                    textView.setTextSize(18f)
                    linearLayoutView.addView(textView)
                    uploadImageResponse = editValue
                    textView.setOnClickListener(View.OnClickListener { viewUploadedImageOrPDF() })
                }
                val linearLayout = LinearLayout(this)
                lp = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,  // Width of TextView
                    100
                )
                lp.gravity = Gravity.CENTER
                lp.setMargins(0, 20, 0, 0)
                linearLayout.layoutParams = lp
                linearLayout.weightSum = 1f
                linearLayout.orientation = LinearLayout.HORIZONTAL
                linearLayoutParent.addView(linearLayout)
                var imageView = ImageView(this)
                lp = LinearLayout.LayoutParams(
                    0,  // Width of TextView
                    80
                )
                lp.weight = 0.4f
                imageView.layoutParams = lp
                imageView.setImageResource(R.drawable.ic_attachment)
                linearLayout.addView(imageView)
                imageView.setOnClickListener(View.OnClickListener {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        if (ActivityCompat.checkSelfPermission(
                                this@EMRCreateRecordActivity,
                                permissionsRequiredSDK33Higher[0]
                            ) == PackageManager.PERMISSION_GRANTED
                        ) {
                            openFileDialog()
                        } else {
                            ActivityCompat.requestPermissions(
                                this@EMRCreateRecordActivity,
                                permissionsRequiredSDK33Higher,
                                READ_EXTERNAL_STORAGE_CONSTANT
                            )
                        }
                    } else {
                        if (ActivityCompat.checkSelfPermission(
                                this@EMRCreateRecordActivity,
                                storagePermission[0]
                            ) == PackageManager.PERMISSION_GRANTED
                        ) {
                            openFileDialog()
                        } else {
                            ActivityCompat.requestPermissions(
                                this@EMRCreateRecordActivity,
                                storagePermission,
                                READ_EXTERNAL_STORAGE_CONSTANT
                            )
                        }
                    }
                })
                var textView = TextView(this)
                textView.setText("OR")
                textView.setTypeface(null, Typeface.BOLD)
                linearLayout.addView(textView)
                imageView = ImageView(this)
                lp = LinearLayout.LayoutParams(
                    0,  // Width of TextView
                    80
                )
                lp.weight = 0.4f
                imageView.layoutParams = lp
                imageView.setImageResource(R.drawable.ic_camera)
                linearLayout.addView(imageView)
                imageView.setOnClickListener(object : View.OnClickListener {
                    override fun onClick(view: View) {
                        if (ActivityCompat.checkSelfPermission(
                                this@EMRCreateRecordActivity,
                                permissionsRequired[0]
                            ) == PackageManager.PERMISSION_GRANTED
                        ) {
                            openCamera()
                        } else {
                            ActivityCompat.requestPermissions(
                                this@EMRCreateRecordActivity,
                                permissionsRequired,
                                PERMISSION_CALLBACK_CONSTANT
                            )
                        }
                    }
                })
                if (isMandatory == 1) {
                    mandatoryField!![fieldId] = fieldName
                }
                linearLayoutParent.gravity = Gravity.CENTER
                linearLayoutParent.id = fieldId
                formImageID = fieldId
                formLayout!!.addView(linearLayoutParent)
                lp = LinearLayout.LayoutParams(
                    500,  // Width of TextView
                    500
                )
                lp.gravity = Gravity.CENTER
                lp.setMargins(0, 12, 0, 12)
                formImage!!.layoutParams = lp
                formImage!!.layoutParams = lp
                formImage!!.visibility = View.GONE
                formLayout!!.addView(formImage)
            }
            7 -> {
                val checkBox = CheckBox(this)
                var linearLayout = LinearLayout(this)
                checkBox.background = resources.getDrawable(R.drawable.drawable_rectangle_shape)
                var lp = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,  // Width of TextView
                    150
                )
                lp.setMargins(0, 30, 0, 0)
                checkBox.text = fieldName
                checkBox.id = fieldId
                if (isMandatory == 1) {
                    mandatoryField!![fieldId] = fieldName
                    checkBox.background =
                        resources.getDrawable(R.drawable.drawable_rectangle_shape_red)
                }
                linearLayout.setLayoutParams(lp)
                linearLayout.addView(checkBox)
                checkBox.layoutParams = lp
                if (checkBox.parent != null) {
                    (checkBox.parent as ViewGroup).removeView(checkBox) // <- fix
                }
                if (callingFrom.equals("editRecord", ignoreCase = true)) {
                    if (editValue != null && !editValue.equals(
                            "",
                            ignoreCase = true
                        )
                    ) checkBox.isChecked =
                        editValue.equals("1", ignoreCase = true) || editValue.equals(
                            "yes",
                            ignoreCase = true
                        )
                }
                formLayout!!.addView(checkBox)
            }
            8 -> {
                dateTv = TextView(this)
                var lp = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,  // Width of TextView
                    150
                )
                lp.setMargins(0, 30, 0, 0)
                dateTv!!.layoutParams = lp
                dateTv!!.hint = fieldName
                dateTv!!.id = fieldId
                dateTv!!.background = resources.getDrawable(R.drawable.drawable_rectangle_shape)
                dateTv!!.setPadding(40, 40, 10, 10)
                dateTv!!.textSize = 17f
                if (defaults.equals("Current", ignoreCase = true)) {
                    val df: DateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH)
                    val date = df.format(Calendar.getInstance().time)
                    dateTv!!.text = date
                }
                //                dateTv.setText(date);
                if (callingFrom.equals("editRecord", ignoreCase = true)) {
                    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                    try {
                        val dateNew = dateFormat.parse(editValue)
                        val formatter: Format = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
                        val selectedDate = formatter.format(dateNew)
                        if (!selectedDate.equals("", ignoreCase = true)) dateTv!!.text =
                            selectedDate
                    } catch (e: ParseException) {
                        e.printStackTrace()
                        dateTv!!.text = editValue
                    }
                }
                formLayout!!.addView(dateTv)
                if (categoryId.equals("5", ignoreCase = true)) {
//                    Log.d("Investigation", "***********");
                    investDateList!!.add(dateTv!!)
                    if (fieldId == 43) {
//                        Log.d("Investigation", "###############");
                        dateTv!!.visibility = View.GONE
                    }
                } else if (categoryId.equals("2", ignoreCase = true)) {
                    immuDateList!!.add(dateTv!!)
                    if (fieldId == 242) {
                        dateTv!!.visibility = View.GONE
                    }
                } else if (categoryId.equals("14", ignoreCase = true)) {
                    if (fieldId == 114) {
                        probEditText!!.add(dateTv!!)
                    }
                } else if (categoryId.equals("4", ignoreCase = true)) {
                    if (fieldId == 33) {
                        proceedTextView!!.add(dateTv!!)
                    }
                }
                dateTv!!.setOnClickListener(object : View.OnClickListener {
                    override fun onClick(view: View) {
                        datePicker(view as TextView)
                    }
                })
            }
            12 -> {
                val textview = TextView(this)
                var lp = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,  // Width of TextView
                    150
                )
                lp.setMargins(0, 30, 0, 0)
                textview.layoutParams = lp
                textview.hint = fieldName
                textview.id = fieldId
                textview.background = resources.getDrawable(R.drawable.drawable_rectangle_shape)
                textview.setPadding(40, 40, 10, 10)
                textview.textSize = 17f
                val time = simpleDateFormat!!.format(Calendar.getInstance().time)
                textview.text = time
                if (callingFrom.equals("editRecord", ignoreCase = true)) {
                    if (editValue != null && !editValue.equals(
                            "",
                            ignoreCase = true
                        )
                    ) textview.text = editValue
                }
                formLayout!!.addView(textview)
                textview.setOnClickListener(object : View.OnClickListener {
                    override fun onClick(view: View) {
                        timePicker(view as TextView)
                    }
                })
            }
            16 -> {
                val completeTextView = AutoCompleteTextView(this)
                // Create a LayoutParams for AutoCompleteTextView
                var lp = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,  // Width of TextView
                    150
                )
                lp.setMargins(0, 30, 0, 0)
                completeTextView.layoutParams = lp
                completeTextView.background =
                    ContextCompat.getDrawable(this, R.drawable.drawable_rectangle_shape)
                completeTextView.setPadding(40, 10, 10, 10)
                completeTextView.textSize = 16f
                completeTextView.inputType = InputType.TYPE_TEXT_VARIATION_PERSON_NAME
                completeTextView.hint = fieldName
                completeTextView.id = fieldId
                if (isMandatory == 1) {
                    completeTextView.hint = fieldName + resources.getString(R.string.mandatory_hint)
                    mandatoryField!![fieldId] = fieldName
                    completeTextView.background =
                        ContextCompat.getDrawable(this, R.drawable.drawable_rectangle_shape_red)
                }
                val valText = TextView(this)
                lp = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,  // Width of TextView
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                valText.layoutParams = lp
                valText.id = 0
                valText.setPadding(40, 10, 10, 10)
                valText.textSize = 14f
                valText.text = getString(R.string.addMedicineNameValid)
                valText.setTextColor(
                    ContextCompat.getColor(
                        this@EMRCreateRecordActivity,
                        R.color.red
                    )
                )
                valText.visibility = View.GONE
                suggestionsList = ArrayList()
                completeTextView.onFocusChangeListener =
                    OnFocusChangeListener { view: View?, b: Boolean ->
                        if (!b) {
                            if (!isMedicineNameSelFromListOrAdded) {
                                valText.visibility = View.VISIBLE
                            }
                        }
                    }
                autoCompleteListAdapter =
                    AutoCompleteAdapter(this, object : ItemClick {
                        override fun onItemClickListener(pos: Int, clickFrom: String) {
                            if (clickFrom.equals("showAddToDatabasePopup", ignoreCase = true)) {
                                dialog = Dialog(this@EMRCreateRecordActivity, R.style.Theme_Dialog)
                                dialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
                                dialog!!.window!!.setLayout(
                                    ViewGroup.LayoutParams.MATCH_PARENT,
                                    ViewGroup.LayoutParams.MATCH_PARENT
                                )
                                dialog!!.setCancelable(false)
                                dialog!!.setContentView(R.layout.add_medicine_data)
                                etMedName = dialog!!.findViewById(R.id.et_med_name)
                                etMedCompany = dialog!!.findViewById(R.id.et_med_company)
                                val btnAdd = dialog!!.findViewById<Button>(R.id.btn_Add)
                                val btnCancel = dialog!!.findViewById<Button>(R.id.btn_cancel)
                                val enterMedName = completeTextView.text.toString()
                                if (!enterMedName.equals("", ignoreCase = true)) {
                                    etMedName!!.setText(enterMedName)
                                }
                                btnAdd.setOnClickListener { v: View? ->
                                    isMedicineAddedToDatabase = true
                                    completeTextView.setText("")
                                    medName = etMedName!!.getText().toString()
                                    medCompany = etMedCompany!!.getText().toString()
                                    if (!medName.equals(
                                            "",
                                            ignoreCase = true
                                        ) && !medCompany.equals(
                                            "",
                                            ignoreCase = true
                                        )
                                    ) {
                                        val addMedicineReq: AddMedicineReq =
                                            AddMedicineReq(medName, medCompany)
                                        createEMRRecordViewModel!!.addMedicineToDatabase(
                                            addMedicineReq
                                        )
                                        createEMRRecordViewModel!!.addMedicineLiveData.observe(
                                            this@EMRCreateRecordActivity,
                                            Observer<APIResponse<Any>?> { objectAPIResponse: APIResponse<Any>? ->
                                                if (objectAPIResponse?.getError() == null && objectAPIResponse?.getResponseClassObj() != null) {
                                                    val addMedicineRes: AddMedicineRes? =
                                                        objectAPIResponse.getResponseClassObj() as AddMedicineRes?
                                                    if (addMedicineRes!!.response) {
                                                        etMedName?.setText("")
                                                        etMedCompany?.setText("")
                                                        isMedicineAddedToDatabase = true
                                                        dialog!!.dismiss()
                                                        completeTextView.setText(
                                                            String.format(
                                                                "%s (By-%s)",
                                                                medName,
                                                                medCompany
                                                            )
                                                        )
                                                        isMedicineNameSelFromListOrAdded = true
                                                        valText.visibility = View.GONE
                                                        completeTextView.clearFocus()
                                                        /* int pos1 = completeTextView.getText().length();
                                                completeTextView.setSelection(pos1);
                                                valText.setVisibility(View.GONE);
                                                isMedicineNameSelFromListOrAdded = true;*/
                                                    }
                                                } else {
                                                    if (objectAPIResponse != null) {
                                                        errorHandler(
                                                            this@EMRCreateRecordActivity,
                                                            objectAPIResponse.getError()
                                                        )
                                                    }
                                                }
                                            })
                                    } else if (medName.equals(
                                            "",
                                            ignoreCase = true
                                        ) && medCompany.equals("", ignoreCase = true)
                                    ) {
                                        Toast.makeText(
                                            this@EMRCreateRecordActivity,
                                            "Please enter medicine name and company/brand name",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    } else if (medName.equals("", ignoreCase = true)) {
                                        Toast.makeText(
                                            this@EMRCreateRecordActivity,
                                            "Please enter medicine name",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    } else if (medCompany.equals("", ignoreCase = true)) {
                                        Toast.makeText(
                                            this@EMRCreateRecordActivity,
                                            "Please enter medicine company or brand name",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }
                                btnCancel.setOnClickListener { v: View? ->
                                    isMedicineAddedToDatabase = false
                                    completeTextView.setText(etMedName!!.text.toString())
                                    val pos1: Int = completeTextView.text.length
                                    completeTextView.setSelection(pos1)
                                    dialog!!.dismiss()
                                }
                                dialog!!.show()
                            }
                        }
                    })
                completeTextView.setAdapter(autoCompleteListAdapter)
                createEMRRecordViewModel = ViewModelProvider(this).get(
                    CreateEMRRecordViewModel::class.java
                )
                completeTextView.onItemClickListener =
                    AdapterView.OnItemClickListener { adapterView: AdapterView<*>?, view: View?, i: Int, l: Long ->
                        isMedicineNameSelFromListOrAdded = true
                        isMedicineAddedToDatabase = true
                        valText.visibility = View.GONE
                    }
                completeTextView.addTextChangedListener(object : TextWatcher {
                    override fun beforeTextChanged(
                        charSequence: CharSequence,
                        i: Int,
                        i1: Int,
                        i2: Int
                    ) {
                    }

                    override fun onTextChanged(
                        charSequence: CharSequence,
                        i: Int,
                        i1: Int,
                        i2: Int
                    ) {
                        if (!isMedicineAddedToDatabase) {
                            if (charSequence.length > 2) {
                                //Make API call to get auto suggestions
                                searchText = charSequence.toString()
                                handler!!.removeMessages(TRIGGER_AUTO_COMPLETE)
                                handler!!.sendEmptyMessageDelayed(
                                    TRIGGER_AUTO_COMPLETE,
                                    AUTO_COMPLETE_DELAY
                                )
                            } else {
                                isMedicineNameSelFromListOrAdded = false
                            }
                        }
                        isMedicineAddedToDatabase = false
                    }

                    override fun afterTextChanged(editable: Editable) {}
                })
                handler = Handler { msg: Message ->
                    if (msg.what == TRIGGER_AUTO_COMPLETE) {
                        if (!TextUtils.isEmpty(searchText)) {
                            val url =
                                ApiUrls.getAutoSuggestionsForMedicationMedicienName + "?search=" + searchText
                            createEMRRecordViewModel!!.getMedicationAutoSuggestions(url)
                            createEMRRecordViewModel!!.medicationAutoSuggestionsLiveData.observe(
                                this@EMRCreateRecordActivity,
                                object : Observer<APIResponse<Any>?> {
                                    override fun onChanged(value: APIResponse<Any>?) {
                                        suggestionsList = ArrayList()
                                        if (value?.getError() == null && value?.getResponseClassObj() != null) {
                                            val autoSuggestionList =
                                                value?.getResponseClassObj() as MedicationAutoSuggestion?
                                            (suggestionsList as ArrayList<MedicineNameResponse>).addAll(autoSuggestionList!!.response)
                                            if ((suggestionsList as ArrayList<MedicineNameResponse>).size == 0) {
                                                if (!isMedicineAddedToDatabase) {
                                                    isMedicineNameSelFromListOrAdded = false
                                                    (suggestionsList as ArrayList<MedicineNameResponse>).add(
                                                        MedicineNameResponse(
                                                            0,
                                                            "No Records",
                                                            "",
                                                            0,
                                                            "",
                                                            ""
                                                        )
                                                    )
                                                }
                                            }
                                        } else {
                                            if (!isMedicineAddedToDatabase) {
                                                isMedicineNameSelFromListOrAdded = false
                                                (suggestionsList as ArrayList<MedicineNameResponse>).add(
                                                    MedicineNameResponse(
                                                        0,
                                                        "No Records",
                                                        "",
                                                        0,
                                                        "",
                                                        ""
                                                    )
                                                )
                                            }
                                        }
                                        isMedicineAddedToDatabase = false
                                        autoCompleteListAdapter!!.setData(suggestionsList as ArrayList<MedicineNameResponse>)
                                        autoCompleteListAdapter!!.notifyDataSetChanged()
                                    }
                                })
                        }
                    }
                    false
                }
                if (callingFrom.equals("editRecord", ignoreCase = true)) {
                    if (editValue != null && !editValue.equals(
                            "",
                            ignoreCase = true
                        )
                    ) completeTextView.setText(editValue)
                }
                formLayout!!.addView(completeTextView)
                formLayout!!.addView(valText)
            }
        }
    }

    private fun createTaskForm(
        fieldId: Int, fieldName: String, fieldType: Int,
        isMandatory: Int, fieldValue: String, editValue: String?, defaults: String
    ) {
        when (fieldType) {
            1 -> {
                val editText = EditText(this)
                // Create a LayoutParams for TextView
                val lp = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,  // Width of TextView
                    150
                )
                lp.setMargins(0, 30, 0, 0)
                editText.layoutParams = lp
                editText.background = resources.getDrawable(R.drawable.drawable_rectangle_shape)
                editText.setPadding(40, 10, 10, 10)
                editText.hint = fieldName
                editText.id = fieldId
                if (isMandatory == 1) { //2
                    editText.hint = fieldName + resources.getString(R.string.mandatory_hint)
                    mandatoryField!![fieldId] = fieldName
                    editText.background =
                        resources.getDrawable(R.drawable.drawable_rectangle_shape_red)
                }
                if (callingFrom.equals("editRecord", ignoreCase = true)) {
                    if (editValue != null && !editValue.equals(
                            "",
                            ignoreCase = true
                        )
                    ) editText.setText(editValue)
                }
                formLayout!!.addView(editText)
            }
            2 -> {
                var editText = EditText(this)
                // Create a LayoutParams for TextView
                var lp = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,  // Width of TextView
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
                lp.setMargins(0, 30, 0, 0)
                editText.setLayoutParams(lp)
                editText.setBackground(resources.getDrawable(R.drawable.drawable_rectangle_shape))
                editText.setPadding(40, 10, 10, 10)
                editText.setHint(fieldName)
                editText.setId(fieldId)
                if (isMandatory == 2) {
                    editText.setHint(fieldName + resources.getString(R.string.mandatory_hint))
                    mandatoryField!![fieldId] = fieldName
                    editText.setBackground(resources.getDrawable(R.drawable.drawable_rectangle_shape_red))
                }
                editText.setSingleLine(false)
                editText.setImeOptions(EditorInfo.IME_FLAG_NO_ENTER_ACTION)
                editText.setInputType(InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_MULTI_LINE)
                editText.setLines(5)
                editText.setMaxLines(10)
                editText.setVerticalScrollBarEnabled(true)
                editText.setMovementMethod(ScrollingMovementMethod.getInstance())
                editText.setScrollBarStyle(View.SCROLLBARS_INSIDE_INSET)
                if (callingFrom.equals("editRecord", ignoreCase = true)) {
                    if (editValue != null && !editValue.equals(
                            "",
                            ignoreCase = true
                        )
                    ) editText.setText(editValue)
                }
                formLayout!!.addView(editText)
            }
            3 -> {
                var editText = EditText(this)
                // Create a LayoutParams for TextView
                var lp = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,  // Width of TextView
                    150
                )
                lp.setMargins(0, 30, 0, 0)
                editText.setLayoutParams(lp)
                editText.setBackground(resources.getDrawable(R.drawable.drawable_rectangle_shape))
                editText.setPadding(40, 10, 10, 10)
                editText.setHint(fieldName)
                editText.setId(fieldId)
                editText.setInputType(InputType.TYPE_CLASS_NUMBER)
                if (isMandatory == 2) {
                    editText.setHint(fieldName + resources.getString(R.string.mandatory_hint))
                    mandatoryField!![fieldId] = fieldName
                    editText.setBackground(resources.getDrawable(R.drawable.drawable_rectangle_shape_red))
                }
                taskHiddenEt!!.add(editText)
                if (callingFrom.equals("editRecord", ignoreCase = true)) {
                    if (editValue != null && !editValue.equals(
                            "",
                            ignoreCase = true
                        )
                    ) editText.setText(editValue)
                }
                formLayout!!.addView(editText)
            }
            4, 6 -> {
                val textView = TextView(this)
                var lp = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,  // Width of TextView
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
                lp.setMargins(0, 30, 0, 0)
                textView.layoutParams = lp
                textView.text = fieldName
                textView.setTypeface(null, Typeface.BOLD)
                formLayout!!.addView(textView)
                var valueArr: Array<String>? = null
                val spinner = Spinner(this)
                val spinnerArray = ArrayList<String?>()
                if (defaults.equals("OrgDoctors", ignoreCase = true)) {
                    spinnerArray.addAll(spinnerArrayDocList!!)
                } else {
                    valueArr = fieldValue.split(",".toRegex()).dropLastWhile { it.isEmpty() }
                        .toTypedArray()
                    var i = 0
                    while (i < valueArr.size) {
                        spinnerArray.add(valueArr.get(i))
                        i++
                    }
                }
                spinnerArray.add(0, resources.getString(R.string.select_option))
                val spinnerArrayAdapter =
                    ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, spinnerArray)
                spinner.adapter = spinnerArrayAdapter
                spinner.id = fieldId
                spinner.background = resources.getDrawable(R.drawable.drawable_rectangle_shape)
                if (isMandatory == 2 || isMandatory == 1) {
                    mandatoryField!![fieldId] = fieldName
                    spinner.background =
                        resources.getDrawable(R.drawable.drawable_rectangle_shape_red)
                }
                lp = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,  // Width of TextView
                    150
                )
                lp.setMargins(0, 30, 0, 0)
                //                spinner.setLayoutParams(lp);
//                spinner.setBackground(getResources().getDrawable(R.drawable.rectangle_view));
                spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                        adapterView: AdapterView<*>?,
                        view: View,
                        i: Int,
                        l: Long
                    ) {
//                        Log.d("Selected ", spinnerArray.get(i));
                        if (spinnerArray[i].equals(
                                resources.getString(R.string.repeat),
                                ignoreCase = true
                            )
                        ) {
                            showTaskHiddenField(View.VISIBLE)
                        } else if (spinnerArray[i].equals(
                                resources.getString(R.string.one_time),
                                ignoreCase = true
                            )
                        ) {
                            showTaskHiddenField(View.GONE)
                        }
                    }

                    override fun onNothingSelected(adapterView: AdapterView<*>?) {}
                }
                if (valueArr != null) {
                    if (valueArr[0].equals("Minutes", ignoreCase = true)) {
                        taskHidderSpinner!!.add(spinner)
                        taskHiddenTv!!.add(textView)
                    }
                }
                if (callingFrom.equals("editRecord", ignoreCase = true)) {
                    if (editValue != null && !editValue.equals(
                            "",
                            ignoreCase = true
                        )
                    ) spinner.setSelection(getIndex(spinner, editValue))
                }
                formLayout!!.addView(spinner)
            }
            5 -> {
                val linearLayoutParent = LinearLayout(this)
                var lp = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,  // Width of TextView
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
                linearLayoutParent.layoutParams = lp
                linearLayoutParent.orientation = LinearLayout.VERTICAL
                if (callingFrom.equals("editRecord", ignoreCase = true) && editValue != null &&
                    !editValue.equals("", ignoreCase = true) && !editValue.equals(
                        "No",
                        ignoreCase = true
                    )
                ) {
                    val linearLayoutView = LinearLayout(this)
                    lp = LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,  // Width of TextView
                        80
                    )
                    lp.setMargins(0, 12, 0, 0)
                    lp.gravity = Gravity.CENTER
                    linearLayoutView.layoutParams = lp
                    linearLayoutView.orientation = LinearLayout.HORIZONTAL
                    linearLayoutParent.addView(linearLayoutView)
                    val imageView_attached = ImageView(this)
                    lp = LinearLayout.LayoutParams(
                        50,  // Width of TextView
                        50
                    )
                    lp.setMargins(0, 10, 10, 0)
                    //lp.weight = 0.4f;
                    imageView_attached.layoutParams = lp
                    imageView_attached.setColorFilter(
                        ContextCompat.getColor(
                            this,
                            R.color.colorAccent
                        ), PorterDuff.Mode.SRC_IN
                    )
                    imageView_attached.setImageResource(R.drawable.ic_attachment)
                    linearLayoutView.addView(imageView_attached)
                    var textView = TextView(this)
                    textView.setText("View Attachment")
                    textView.setTextColor(resources.getColor(R.color.colorAccent))
                    //  textView.setTypeface(null, Typeface.BOLD);
                    textView.setTextSize(18f)
                    linearLayoutView.addView(textView)
                    uploadImageResponse = editValue
                    textView.setOnClickListener(View.OnClickListener { viewUploadedImageOrPDF() })
                }
                val linearLayout = LinearLayout(this)
                lp = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,  // Width of TextView
                    100
                )
                lp.gravity = Gravity.CENTER
                lp.setMargins(0, 20, 0, 0)
                linearLayout.layoutParams = lp
                linearLayout.weightSum = 1f
                linearLayout.orientation = LinearLayout.HORIZONTAL
                linearLayoutParent.addView(linearLayout)
                var imageView = ImageView(this)
                lp = LinearLayout.LayoutParams(
                    0,  // Width of TextView
                    80
                )
                lp.weight = 0.4f
                imageView.layoutParams = lp
                imageView.setImageResource(R.drawable.ic_attachment)
                linearLayout.addView(imageView)
                imageView.setOnClickListener { openFileDialog() }
                var textView = TextView(this)
                textView.setText("OR")
                textView.setTypeface(null, Typeface.BOLD)
                linearLayout.addView(textView)
                imageView = ImageView(this)
                lp = LinearLayout.LayoutParams(
                    0,  // Width of TextView
                    80
                )
                lp.weight = 0.4f
                imageView.layoutParams = lp
                imageView.setImageResource(R.drawable.ic_camera)
                linearLayout.addView(imageView)
                imageView.setOnClickListener { openCamera() }
                if (isMandatory == 1) {
                    mandatoryField!![fieldId] = fieldName
                }
                linearLayoutParent.gravity = Gravity.CENTER
                linearLayoutParent.id = fieldId
                formImageID = fieldId
                formLayout!!.addView(linearLayoutParent)
                lp = LinearLayout.LayoutParams(
                    500,  // Width of TextView
                    500
                )
                lp.gravity = Gravity.CENTER
                lp.setMargins(0, 12, 0, 12)
                formImage!!.layoutParams = lp
                formImage!!.layoutParams = lp
                formImage!!.visibility = View.GONE
                formLayout!!.addView(formImage)
            }
            7 -> {
                val checkBox = CheckBox(this)
                var linearLayout = LinearLayout(this)
                linearLayout.setBackground(resources.getDrawable(R.drawable.drawable_rectangle_shape))
                var lp = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,  // Width of TextView
                    150
                )
                lp.setMargins(0, 30, 0, 0)
                checkBox.text = fieldName
                checkBox.id = fieldId
                if (isMandatory == 1) {
                    mandatoryField!![fieldId] = fieldName
                    linearLayout.setBackground(resources.getDrawable(R.drawable.drawable_rectangle_shape_red))
                }
                linearLayout.setLayoutParams(lp)
                linearLayout.addView(checkBox)
                if (callingFrom.equals("editRecord", ignoreCase = true)) {
                    if (editValue != null && !editValue.equals(
                            "",
                            ignoreCase = true
                        )
                    ) checkBox.isChecked =
                        editValue.equals("1", ignoreCase = true) || editValue.equals(
                            "yes",
                            ignoreCase = true
                        )
                }
                formLayout!!.addView(linearLayout)
            }
            8 -> {
                val dateTv = TextView(this)
                var lp = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,  // Width of TextView
                    150
                )
                lp.setMargins(0, 30, 0, 0)
                dateTv.layoutParams = lp
                dateTv.hint = fieldName
                dateTv.id = fieldId
                dateTv.background = resources.getDrawable(R.drawable.drawable_rectangle_shape)
                dateTv.setPadding(40, 40, 10, 10)
                dateTv.textSize = 17f
                val df: DateFormat = SimpleDateFormat("dd-MM-yyyy")
                val date = df.format(Calendar.getInstance().time)
                //                dateTv.setText(date);
                if (fieldId == 195) {
                    dateTv.text = fieldName
                    if (isMandatory == 2) {
                        mandatoryField!![fieldId] = fieldName
                    }
                    dateTv.visibility = View.GONE
                    taskHiddenTv!!.add(dateTv)
                }
                if (callingFrom.equals("editRecord", ignoreCase = true)) {
                    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                    try {
                        val dateNew = dateFormat.parse(editValue)
                        val formatter: Format = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
                        val selectedDate = formatter.format(dateNew)
                        if (!selectedDate.equals("", ignoreCase = true)) dateTv.text = selectedDate
                    } catch (e: ParseException) {
                        e.printStackTrace()
                        dateTv.text = editValue
                    }
                }
                formLayout!!.addView(dateTv)
                dateTv.setOnClickListener { view -> datePicker(view as TextView) }
            }
            12 -> {
                val textview = TextView(this)
                var lp = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,  // Width of TextView
                    150
                )
                lp.setMargins(0, 30, 0, 0)
                textview.layoutParams = lp
                textview.hint = fieldName
                textview.id = fieldId
                textview.background = resources.getDrawable(R.drawable.drawable_rectangle_shape)
                textview.setPadding(40, 40, 10, 10)
                textview.textSize = 17f
                val time = simpleDateFormat!!.format(Calendar.getInstance().time)
                textview.text = time
                /*if(fieldId==102){
                    textview.setHint(new SimpleDateFormat("HH:mm").format(Calendar.getInstance().getTime()));
                }
                else*/if (fieldId == 196) {
                    textview.text = fieldName
                    if (isMandatory == 2) {
                        mandatoryField!![fieldId] = fieldName
                    }
                    textview.visibility = View.GONE
                    taskHiddenTv!!.add(textview)
                }
                if (callingFrom.equals("editRecord", ignoreCase = true)) {
                    if (editValue != null && !editValue.equals(
                            "",
                            ignoreCase = true
                        )
                    ) textview.text = editValue
                }
                formLayout!!.addView(textview)
                textview.setOnClickListener { view -> timePicker(view as TextView) }
            }
        }
    }

    private fun disableCompleteView(isEnable: Boolean) {
        val childCount = formLayout!!.childCount
        for (i in 0 until childCount) {
            val view = formLayout!!.getChildAt(i)
            enableDisableView(view, isEnable)
        }
    }

    fun enableDisableView(view: View, enabled: Boolean) {
        view.isEnabled = enabled
        if (view is ViewGroup) {
            val group = view
            for (idx in 0 until group.childCount) {
                enableDisableView(group.getChildAt(idx), enabled)
            }
        }
    }

    //            Log.d("Total Child Present", childCount + "");
    private val formData: Unit
        private get() {
            if (isMedicineNameSelFromListOrAdded) {
                val recordJson = JSONObject()
                val recordData = JSONObject()
                val metaData = JSONObject()
                val dietData = JSONArray()
                var sendRecord = true
                try {
                    recordData.put("user_id", ApiUrls.doctorId)
                    recordData.put("doctor_id", ApiUrls.doctorId)
                    recordData.put("patient_id", patientID)
                    recordData.put("catid", categoryId)
                    recordJson.put("record_data", recordData)
                    recordJson.put("dietdata", dietData)
                    val childCount = formLayout!!.childCount
                    //            Log.d("Total Child Present", childCount + "");
                    for (i in 0 until childCount) {
                        val view = formLayout!!.getChildAt(i)
                        if (view is EditText) {
                            val id = view.getId()
                            val value = view.text.toString()
                            if (id != -1 && value != "") {
                                metaData.put(id.toString() + "", value)
                            } else if (mandatoryField!!.containsKey(id)) {
                                sendRecord = false
                                Toast.makeText(
                                    this@EMRCreateRecordActivity,
                                    mandatoryField!![id] + resources.getString(R.string.is_mandatory),
                                    Toast.LENGTH_SHORT
                                ).show()
                                break
                            }
                        } else if (view is Spinner) {
                            val id = view.getId()
                            val value = view.selectedItem.toString()
                            if (id != -1 && value != "" && value != resources.getString(R.string.select_option)) {
                                metaData.put(id.toString() + "", value)
                            } else if (mandatoryField!!.containsKey(id)) {
                                sendRecord = false
                                Toast.makeText(
                                    this@EMRCreateRecordActivity,
                                    resources.getString(R.string.select_option),
                                    Toast.LENGTH_SHORT
                                ).show()
                                break
                            }
                        } else if (view is CheckBox) {
                            val id = view.getId()
                            val value = view.isChecked
                            if (id != -1 && value) {
                                metaData.put(id.toString() + "", 1)
                            } else if (mandatoryField!!.containsKey(id)) {
                                sendRecord = false
                                Toast.makeText(
                                    this@EMRCreateRecordActivity,
                                    resources.getString(R.string.select_check_box_option),
                                    Toast.LENGTH_SHORT
                                ).show()
                                break
                            }
                        } else if (view is TextView) {
                            val id = view.getId()
                            if (id != 0) {
                                var value: String
                                value = if (view.hint != null && view.hint.toString()
                                        .equals("Time", ignoreCase = true)
                                ) {
                                    appUtils!!.changeDateFormat(
                                        timeFormat,
                                        "HH:mm",
                                        view.text.toString()
                                    )
                                } else {
                                    view.text.toString()
                                }
                                if (id != -1 && value != "") {
                                    metaData.put(id.toString() + "", value)
                                } else if (mandatoryField!!.containsKey(id)) {
                                    sendRecord = false
                                    Toast.makeText(
                                        this@EMRCreateRecordActivity,
                                        "Set " + mandatoryField!![id],
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    break
                                }
                            }
                        } else if (view is LinearLayout) {
                            val id = view.getId()
                            if (id != -1 && uploadImageResponse != null) {
                                metaData.put(id.toString() + "", uploadImageResponse)
                            } else if (mandatoryField!!.containsKey(id)) {
                                sendRecord = false
                                Toast.makeText(
                                    this@EMRCreateRecordActivity,
                                    resources.getString(R.string.please_attached_file),
                                    Toast.LENGTH_SHORT
                                ).show()
                                break
                            }
                        }
                    }
                    recordJson.put("metadata", metaData)
                    Log.i("records json data", recordJson.toString())
                    if (sendRecord) {
                        recordJson.put("episode_id", episodeID)
                        recordJson.put("encounter_id", encounterID)
                        recordJson.put("category_id", categoryId)
                        recordJson.put("patient_id", patientID)
                        recordJson.put("type", type)
                        addRecord(recordJson)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            } else {
                Toast.makeText(
                    this@EMRCreateRecordActivity,
                    getString(R.string.addMedicineNameValid),
                    Toast.LENGTH_LONG
                ).show()
            }
        }

    private fun addRecord(recordObj: JSONObject) {
        var url: String? = ""
        if (callingFrom.equals("editRecord", ignoreCase = true)) {
            try {
                recordObj.put("record_id", selectedRecordID)
                url = ApiUrls.updateDynamicRecords
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        } else {
            url = ApiUrls.saveRecords
        }
        otpLoading = ProgressDialog(this@EMRCreateRecordActivity)
        otpLoading!!.setMessage(resources.getString(R.string.common_please_wait_loading_message))
        otpLoading!!.setTitle(resources.getString(R.string.creating_your_record))
        otpLoading!!.setProgressStyle(ProgressDialog.STYLE_SPINNER)
        otpLoading!!.setCancelable(false)
        otpLoading!!.show()

        // prepare the Request
        apiGetPostMethodCalls!!.volleyApiRequestData(url,
            Request.Method.POST,
            recordObj,
            this@EMRCreateRecordActivity,
            object : VolleyCallback {
                override fun onSuccess(result: String) {
                    otpLoading!!.dismiss()
                    try {
                        val res = JSONObject(result)
                        val successMSG = res.getString("response")
                        if (callingFrom.equals("editRecord", ignoreCase = true)) {
                            Toast.makeText(
                                this@EMRCreateRecordActivity,
                                successMSG,
                                Toast.LENGTH_SHORT
                            ).show()
                            val intent = Intent()
                            intent.putExtra("editRecord", "editRecord")
                            setResult(RESULT_OK, intent)
                            finish()
                        } else {
                            Toast.makeText(
                                this@EMRCreateRecordActivity,
                                R.string.record_added_successfully,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        val returnIntent = Intent()
                        returnIntent.putExtra("result", "OK")
                        setResult(RESULT_OK, returnIntent)
                        finish()
                    } catch (e: Exception) {
                        e.printStackTrace()
                        otpLoading!!.dismiss()
                    }
                }

                override fun onError(err: String) {
                    otpLoading!!.dismiss()
                    errorHandler(this@EMRCreateRecordActivity, err)
                }
            })
    }

    /**
     * This callback method, call DatePickerFragment class,
     * DatePickerFragment class returns calendar view.
     *
     * @param
     */
    fun datePicker(view: TextView?) {
        dateTv = view
        val fragment = DatePickerFragment()
        fragment.show(supportFragmentManager, "date")
    }

    /**
     * To set date on TextView
     *
     * @param calendar
     */
    private fun setDate(calendar: Calendar) {
//        final DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.SHORT);
        val sdf = SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH)
        dateTv!!.text = sdf.format(calendar.time)
    }

    /**
     * To receive a callback when the user sets the date.
     *
     * @param view
     * @param year
     * @param month
     * @param day
     */
    override fun onDateSet(view: DatePicker, year: Int, month: Int, day: Int) {
        val cal: Calendar = GregorianCalendar(year, month, day)
        setDate(cal)
    }

    /**
     * Create a DatePickerFragment class that extends DialogFragment.
     * Define the onCreateDialog() method to return an instance of DatePickerDialog
     */
    class DatePickerFragment : DialogFragment() {
        override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
            val c = Calendar.getInstance()
            val year = c[Calendar.YEAR]
            val month = c[Calendar.MONTH]
            val day = c[Calendar.DAY_OF_MONTH]

//            datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
            return DatePickerDialog(
                requireActivity(),
                activity as OnDateSetListener?, year, month, day
            )
        }
    }

    private fun timePicker(textView: TextView) {
        val mcurrentTime = Calendar.getInstance()
        val hour = mcurrentTime[Calendar.HOUR_OF_DAY]
        val minute = mcurrentTime[Calendar.MINUTE]
        val mTimePicker: TimePickerDialog
        mTimePicker = TimePickerDialog(
            this@EMRCreateRecordActivity,
            { timePicker, selectedHour, selectedMinute ->
                val format: String
                format = if (timePicker.is24HourView) {
                    "HH:mm"
                } else {
                    "hh:mm aa"
                }

                //textView.setHint(appUtils.getFormattedTime(selectedHour,minute,"HH:mm"));
                textView.text = appUtils!!.getFormattedTime(selectedHour, selectedMinute, format)
                //textView.setText(selectedHour + ":" + selectedMinute);
            },
            hour,
            minute,
            is24Hrformat
        ) //Yes 24 hour time
        mTimePicker.setTitle("Select Time")
        mTimePicker.show()
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

    private fun uploadImage() {
        val url = ApiUrls.uploadRecordImage
        loadingDialog!!.setMessage(resources.getString(R.string.common_uploading_image))
        loadingDialog!!.setCancelable(false)
        loadingDialog!!.setInverseBackgroundForced(false)
        loadingDialog!!.show()

        //our custom volley request
        val volleyMultipartRequest: AppImageUploader = object : AppImageUploader(
            Method.POST, url,
            Response.Listener { response ->
                try {
                    var rootObj = JSONObject(String(response.data))
                    rootObj = rootObj.getJSONObject("response")
                    val url = rootObj.getString("url")
                    if (url != null && url != "") {
                        uploadImageResponse = url
                        Toast.makeText(
                            applicationContext,
                            "$attachmentType Uploaded Successfully",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    loadingDialog!!.dismiss()
                } catch (e: Exception) {
                    e.printStackTrace()
                    Toast.makeText(applicationContext, e.toString(), Toast.LENGTH_SHORT).show()
                }
            },
            Response.ErrorListener { error ->
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
                        resources.getString(R.string.image_upload_failed),
                        Toast.LENGTH_SHORT
                    ).show()
                }
                loadingDialog!!.dismiss()
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
                //                params.put("path", "records/" + formImageID + "/"); //old
                params["path"] = "records/$formImageID/android/" // new
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
                    val bitmap = (formImage!!.drawable as BitmapDrawable).bitmap
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
        Volley.newRequestQueue(this).add(volleyMultipartRequest)
    }

    /*
     * The method is taking Bitmap as an argument
     * then it will return the byte[] array for the given bitmap
     * and we will send this array to the server
     * here we are using PNG Compression with 80% quality
     * you can give quality between 0 to 100
     * 0 means worse quality
     * 100 means best quality
     * */
    fun getFileDataFromDrawable(bitmap: Bitmap): ByteArray {
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 20, byteArrayOutputStream)
        return byteArrayOutputStream.toByteArray()
    }

    private fun showTaskHiddenField(state: Int) {
        if (state == View.VISIBLE) {
            try {
                var fieldType = taskRepeatObj!!.getJSONArray("field_type")
                //                Log.d("Field", fieldType.length() + "");
                for (i in 0 until fieldType.length()) {
                    val obj = fieldType.getJSONObject(i)
                    val id = obj.getInt("id")
                    val type = obj.getInt("type")
                    val name = taskRepeatObj!!.getString("field") //"E.g. Once every 2 days";
                    val mandatory = obj.getInt("is_mandatory")
                    var value = ""
                    if (type == 4) {
                        fieldType = taskRepeatObj!!.getJSONArray("field_values")
                        value = fieldType.getString(1)
                    }
                    editValue = ""
                    if (callingFrom.equals("editRecord", ignoreCase = true)) {
                        if (checkIfKeyExists(selectedRecordDetailsEdit, id)) {
                            val keyValueData = selectedRecordDetailsEdit!![id.toString()]
                            if (keyValueData is String) {
                                editValue = selectedRecordDetailsEdit!!.getString(id.toString())
                            } else if (keyValueData is Int) {
                                editValue =
                                    selectedRecordDetailsEdit!!.getInt(id.toString()).toString()
                            }
                        }
                        if (type == 5) {
                            if (editValue.equals(
                                    "yes",
                                    ignoreCase = true
                                ) || editValue.equals(
                                    "Attachments",
                                    ignoreCase = true
                                ) || editValue.equals("File", ignoreCase = true)
                            ) {
                                editValue = selectedRecordDetailsEdit!!.getString("url")
                            }
                        }
                        createTaskForm(id, name, type, mandatory, value, editValue, defaultsTaskCat)
                    } else {
                        createTaskForm(id, name, type, mandatory, value, "", defaultsTaskCat)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            for (i in taskHiddenTv!!.indices) {
                val textView = taskHiddenTv!![i]
                textView.visibility = state
            }
            for (i in taskHidderSpinner!!.indices) {
                val spinner = taskHidderSpinner!![i]
                spinner.visibility = state
            }
            for (i in taskHiddenEt!!.indices) {
                val editText = taskHiddenEt!![i]
                editText.visibility = state
            }
        } else {
            for (i in taskHiddenTv!!.indices) {
                val textView = taskHiddenTv!![i]
                textView.visibility = state
                if (textView.text.toString().equals("E.g. Once every 2 days", ignoreCase = true)) {
                    formLayout!!.removeView(textView)
                }
            }
            for (i in taskHidderSpinner!!.indices) {
                val spinner = taskHidderSpinner!![i]
                formLayout!!.removeView(spinner)
            }
            for (i in taskHiddenEt!!.indices) {
                val editText = taskHiddenEt!![i]
                formLayout!!.removeView(editText)
            }
        }
    }

    //            Log.d("Total Child Present", childCount + "");
    private val taskFormData: Unit
        private get() {
            val recordJson = JSONObject()
            val recordData = JSONObject()
            val metaData = JSONObject()
            val dietData = JSONArray()
            val dataJsonObject = JSONObject()
            val data = JSONArray()
            var sendRecord = true
            try {
                recordJson.put("episode_id", episodeID)
                recordJson.put("encounter_id", encounterID)
                recordJson.put("category_id", categoryId)
                recordJson.put("patient_id", patientID)
                recordJson.put("type", type)
                recordData.put("user_id", ApiUrls.doctorId)
                recordData.put("doctor_id", ApiUrls.doctorId)
                recordData.put("patient_id", patientID)
                recordData.put("catid", categoryId)
                recordJson.put("record_data", recordData)
                recordJson.put("dietdata", dietData)
                val childCount = formLayout!!.childCount
                //            Log.d("Total Child Present", childCount + "");
                for (i in 0 until childCount) {
                    val view = formLayout!!.getChildAt(i)
                    if (view is EditText) {
                        val id = view.getId()
                        val value = view.text.toString()
                        if (id != -1 && value != "") {
                            metaData.put(id.toString() + "", value)
                        } else if (mandatoryField!!.containsKey(id)) {
                            sendRecord = false
                            Toast.makeText(
                                this@EMRCreateRecordActivity,
                                mandatoryField!![id] + " is Mandatory",
                                Toast.LENGTH_SHORT
                            ).show()
                            break
                        }
                    } else if (view is Spinner) {
                        val id = view.getId()
                        val value = view.selectedItem.toString()
                        if (id != -1 && value != "" && value != resources.getString(R.string.select_option)) {
                            metaData.put(id.toString() + "", value)
                        } else if (mandatoryField!!.containsKey(id)) {
                            sendRecord = false
                            Toast.makeText(
                                this@EMRCreateRecordActivity,
                                "Select One Option",
                                Toast.LENGTH_SHORT
                            ).show()
                            break
                        }
                    } else if (view is TextView) {
                        val id = view.getId()
                        val value = view.text.toString()
                        if (id != -1 && value != "" && !value.equals(
                                "End Time",
                                ignoreCase = true
                            ) && !value.equals("End Date", ignoreCase = true)
                        ) {
                            metaData.put(id.toString() + "", value)
                        } else if (mandatoryField!!.containsKey(id) && view.getVisibility() == View.VISIBLE) {
                            sendRecord = false
                            Toast.makeText(
                                this@EMRCreateRecordActivity,
                                "Set " + mandatoryField!![id],
                                Toast.LENGTH_SHORT
                            ).show()
                            break
                        }
                    }
                }
                metaData.put("192", "Days")
                metaData.put("197", "Patient")
                recordJson.put("metadata", metaData)
                if (sendRecord) {
                    addRecord(recordJson)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

    private fun showHiddenInvestField(i: Int) {
        if (i == 2) {
            var dateTv = investDateList!![1]
            dateTv.visibility = View.VISIBLE
            //dateTv.setText("To Be Conducted");
            dateTv = investDateList!![0]
            dateTv.visibility = View.GONE
            val editText = investEditText!![1]
            editText.visibility = View.GONE
        } else if (i == 1) {
            var dateTv = investDateList!![1]
            dateTv.visibility = View.GONE
            dateTv = investDateList!![0]
            dateTv.visibility = View.VISIBLE
            val editText = investEditText!![1]
            editText.visibility = View.VISIBLE
        }
    }

    private fun showHiddenImmuField(i: Int) {
        if (i == 1) {
            var dateTv = immuDateList!![0]
            dateTv.visibility = View.VISIBLE
            dateTv = immuDateList!![1]
            dateTv.visibility = View.GONE
            val editText = immuEditText!![0]
            editText.visibility = View.VISIBLE
        } else if (i == 2) {
            var dateTv = immuDateList!![0]
            dateTv.visibility = View.GONE
            dateTv = immuDateList!![1]
            dateTv.visibility = View.VISIBLE
            dateTv.text = "Schedule On"
            val editText = immuEditText!![0]
            editText.visibility = View.GONE
        }
    }

    private fun getIndex(spinner: Spinner, myString: String): Int {
        var index = 0
        for (i in 0 until spinner.count) {
            if (spinner.getItemAtPosition(i) == myString) {
                index = i
            }
        }
        return index
    }

    private fun showHiddenProblemField(i: Int) {
        if (i == 1 || i == 2) {
            val textView = probEditText!![0]
            textView.visibility = View.GONE
        } else if (i == 3) {
            val textView = probEditText!![0]
            textView.visibility = View.VISIBLE
        }
    }

    private fun showHiddenProcedureField(i: Int) {
        if (i == 1) {
//            Log.d("Procedure Gone", "******");
            val textView = proceedTextView!![0]
            textView.visibility = View.GONE
        } else if (i == 2) {
//            Log.d("Procedure Visible", "******");
            val textView = proceedTextView!![0]
            textView.visibility = View.VISIBLE
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_CALLBACK_CONSTANT) {
            //check if all permissions are granted
            var allgranted = false
            for (i in grantResults.indices) {
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    allgranted = true
                } else {
                    allgranted = false
                    break
                }
            }
            if (allgranted) {
//                Toast.makeText(EMRCreateRecordActivity.this, "You Have all the permission", Toast.LENGTH_SHORT).show();
                openCamera()
            } else {
                Toast.makeText(
                    this@EMRCreateRecordActivity,
                    "You Need To Give Camera Permission",
                    Toast.LENGTH_SHORT
                ).show()
            }
        } else if (requestCode == READ_EXTERNAL_STORAGE_CONSTANT) {
            if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openFileDialog()
            } else {
                Toast.makeText(
                    this@EMRCreateRecordActivity,
                    "You Need To Give File access Permission",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun decodeFile(f: File): Bitmap? {
        var b: Bitmap? = null

        //Decode image size
        val o = BitmapFactory.Options()
        o.inJustDecodeBounds = true
        var fis: FileInputStream? = null
        try {
            fis = FileInputStream(f)
            BitmapFactory.decodeStream(fis, null, o)
            fis.close()
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        val IMAGE_MAX_SIZE = 1024
        var scale = 1
        if (o.outHeight > IMAGE_MAX_SIZE || o.outWidth > IMAGE_MAX_SIZE) {
            scale = Math.pow(
                2.0,
                Math.ceil(
                    Math.log(
                        IMAGE_MAX_SIZE / Math.max(o.outHeight, o.outWidth).toDouble()
                    ) / Math.log(0.5)
                ).toInt().toDouble()
            ).toInt()
        }

        //Decode with inSampleSize
        val o2 = BitmapFactory.Options()
        o2.inSampleSize = scale
        try {
            fis = FileInputStream(f)
            b = BitmapFactory.decodeStream(fis, null, o2)
            fis.close()
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return b
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

    fun getFileName(uri: Uri?): String? {
        val projection = arrayOf(MediaStore.MediaColumns.DISPLAY_NAME)
        val cursor = managedQuery(uri, projection, null, null, null)
        return if (cursor != null) {
            //HERE YOU WILL GET A NULLPOINTER IF CURSOR IS NULL
            //THIS CAN BE, IF YOU USED OI FILE MANAGER FOR PICKING THE MEDIA
            val column_index = cursor
                .getColumnIndexOrThrow(MediaStore.MediaColumns.DISPLAY_NAME)
            cursor.moveToFirst()
            cursor.getString(column_index)
        } else null
    }

    private fun uploadPDFFile(selectedFilePath: String?, uri: Uri?) {
        val resolver = applicationContext
            .contentResolver
        try {
            val pfd = resolver.openFileDescriptor(uri!!, "r")
            val stream: InputStream = FileInputStream(pfd!!.fileDescriptor)
            val localfile = File(this.cacheDir, selectedFilePath)
            val localStream: OutputStream = FileOutputStream(localfile)
            IOUtils.copyStream(stream, localStream)
            pdfFile = localfile
            uploadImage()
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun checkIfKeyExists(response: JSONObject?, key: Int): Boolean {
        return response!!.has(key.toString())
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    companion object {
        private const val PERMISSION_CALLBACK_CONSTANT = 100
        private const val READ_EXTERNAL_STORAGE_CONSTANT = 102
        private const val TRIGGER_AUTO_COMPLETE = 100
        private const val AUTO_COMPLETE_DELAY: Long = 300
    }
}