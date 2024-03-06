package com.whitecoats.clinicplus.activities

import android.annotation.SuppressLint
import android.app.Dialog
import android.app.ProgressDialog
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.Window
import android.widget.*
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.clans.fab.FloatingActionButton
import com.github.clans.fab.FloatingActionMenu
import com.whitecoats.clinicplus.MyClinicGlobalClass
import com.whitecoats.clinicplus.R
import com.whitecoats.clinicplus.adapters.EMRAddingNotesAdapter
import com.whitecoats.clinicplus.adapters.EMRAllPrescriptionListAdapter
import com.whitecoats.clinicplus.adapters.EMREvaluationCategoryListAdapter
import com.whitecoats.clinicplus.adapters.EMRTreatmentPlanCategoryListAdapter
import com.whitecoats.clinicplus.apis.ApiUrls
import com.whitecoats.clinicplus.constants.AppConstants
import com.whitecoats.clinicplus.models.EMRAddRecordCategoryModel
import com.whitecoats.clinicplus.models.EMRConsultCaseHistoryModel
import com.whitecoats.clinicplus.models.EMRPrescriptionModel
import com.whitecoats.clinicplus.utils.AppUtilities
import com.whitecoats.clinicplus.utils.ErrorHandlerClass.errorHandler
import com.whitecoats.clinicplus.viewmodels.EMRConsultationNotesViewModel
import com.whitecoats.clinicplus.viewmodels.EMRCreateRecordsFormViewModel
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class EMRAddNotesActivity : AppCompatActivity() {
    private val toolbar: Toolbar? = null
    private var proceedToAddRecords: LinearLayout? = null
    private var evaluationCategoryRecyclerView: RecyclerView? = null
    private var caseDetailsRecycler: RecyclerView? = null
    var emrEvaluationCategoryList: MutableList<EMRAddRecordCategoryModel>? = null
    private var emrEvaluationCategoryListAdapter: EMREvaluationCategoryListAdapter? = null
    private var evaluationLayoutManager: RecyclerView.LayoutManager? = null
    private var caseDetailsManager: RecyclerView.LayoutManager? = null
    private var treatmentPlanCategoryRecyclerView: RecyclerView? = null
    var emrTreatmentPlanCategoryList: MutableList<EMRAddRecordCategoryModel>? = null
    private var emrTreatmentPlanCategoryListAdapter: EMRTreatmentPlanCategoryListAdapter? = null
    private var treatmentPlanLayoutManager: RecyclerView.LayoutManager? = null
    private var uploadHandWrittenNotes: Button? = null
    private var logEvalutaionReport: Button? = null
    private var logTreatmentPlan: Button? = null
    private var dictate_prescription_button: Button? = null
    private var emrAddingNotesAdapter: EMRAddingNotesAdapter? = null
    private var caseDetailsList: MutableList<EMRConsultCaseHistoryModel>? = null
    private var noNoteLayout: LinearLayout? = null
    private var caseFooter: LinearLayout? = null
    private var floatingActionButton: FloatingActionMenu? = null
    private var emrConsultationNotesViewModel: EMRConsultationNotesViewModel? = null
    private var patientId = 0
    private var episodeId = 0
    private var encounterId = 0
    private var noRecordPreferencesSetText: RelativeLayout? = null
    private var recordPreferencesLoadProgressbar: RelativeLayout? = null
    private var isEvaluationPreferences = false
    private var savePrescriptionButton: Button? = null
    private var share_prescription: TextView? = null
    private var responseEpisodeFieldPreferences: JSONObject? = null
    private var evaluation: Boolean? = null
    private var symptoms: Boolean? = null
    private var observations: Boolean? = null
    private var investigations: Boolean? = null
    private var diagnosis: Boolean? = null
    private var treatmentplan: Boolean? = null
    private var written_notes: Boolean? = null
    private var generated_pdfs: Boolean? = null
    private var handwrittenNotesfb: FloatingActionButton? = null
    private var logEvaluationRecordfb: FloatingActionButton? = null
    private var logTreatmentPlanfb: FloatingActionButton? = null
    private var viewPrescriptionFb: FloatingActionButton? = null
    private var exit_button: Button? = null
    private var transparentView: RelativeLayout? = null
    private var caseNameText: TextView? = null
    private var interactionDetails: TextView? = null
    private var prescriptionDialog: Dialog? = null
    private var dialogEvaluationCancel: ImageView? = null
    private var allPrescriptionRecyclerView: RecyclerView? = null
    private var prescriptionProgress: ProgressBar? = null
    private var prescriptionLayout: RelativeLayout? = null
    private var noCaseLayout: RelativeLayout? = null
    private var allPrescriptionLayoutManager: RecyclerView.LayoutManager? = null
    private var allPrescriptionListAdapter: EMRAllPrescriptionListAdapter? = null
    var allPrescriptionList: MutableList<EMRPrescriptionModel>? = null
    var progressDialog: ProgressDialog? = null
    private var myClinicGlobalClass: MyClinicGlobalClass? = null
    private var emrCreateRecordsFormViewModel: EMRCreateRecordsFormViewModel? = null
    private var sharePerscriptionPopup: RelativeLayout? = null
    private var caseLayout: RelativeLayout? = null
    private var dontShowAgain: CheckBox? = null
    private var howItworkd: ScrollView? = null
    private var sharedPreferences: SharedPreferences? = null
    private var editor: SharedPreferences.Editor? = null
    var encounterName: String? = null
    var encounterCreeatedOn: String? = null
    var caseName: String? = null
    private var appUtilities: AppUtilities? = null
    private var isFbViewAllPrescriptionClicked = false
    private var prescriptionCreateNewText: TextView? = null
    private var addingNotesProgress: ProgressBar? = null
    private var patientName: String? = null
    private var handWrittenNoteDes: TextView? = null
    private var evaluationTextDes: TextView? = null
    private var treatmentTextDesc: TextView? = null
    private var savePrescriptionTextDes: TextView? = null
    private var sharePrescriptionTextDes: TextView? = null
    private var tipTextDes: TextView? = null
    private var dictationFab: FloatingActionButton? = null

    /* ENGG-3754 -- Continuation of Refactoring the deprecated function in Clinic+ App(Android) */
    private var launchCreateRecordResults: ActivityResultLauncher<Intent>? = null
    private var appUtils: com.whitecoats.clinicplus.AppUtilities? = null
    private var canDeleteEditRecords = false
    private var canDeleteEditWrittenNotes = false
    private var apptDetailsBack: ImageButton? = null
    private var patient_name: TextView? = null
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_e_m_r_add_notes)

        emrConsultationNotesViewModel = ViewModelProvider(this).get(
            EMRConsultationNotesViewModel::class.java
        )
        emrConsultationNotesViewModel!!.init()
        appUtilities = AppUtilities()
        caseNameText = findViewById(R.id.case_name)
        interactionDetails = findViewById(R.id.interaction_details)
        handWrittenNoteDes = findViewById(R.id.handWrittenNoteDes)
        evaluationTextDes = findViewById(R.id.evaluationTextDes)
        treatmentTextDesc = findViewById(R.id.treatmentTextDesc)
        savePrescriptionTextDes = findViewById(R.id.savePrescriptionTextDes)
        sharePrescriptionTextDes = findViewById(R.id.sharePrescriptionTextDes)
        tipTextDes = findViewById(R.id.tipTextDes)
        apptDetailsBack = findViewById(R.id.emr_add_back)
        patient_name = findViewById(R.id.emr_patient_name)
        appUtils = com.whitecoats.clinicplus.AppUtilities()
        val strHR =
            "<a><font color='#000000'><b> Handwritten notes: </b></font>Use this to upload prescriptions you have written by hand.</a>"
        handWrittenNoteDes?.text = Html.fromHtml(strHR)
        val strEvaluation =
            "<a><font color='#000000'><b> Evaluation: </b></font>Use this to enter symptoms, comments, diagnosis etc.</a>"
        evaluationTextDes?.text = Html.fromHtml(strEvaluation)
        val strTreatmentPlan =
            "<a><font color='#000000'><b> Treatment Plan: </b></font>Use this to enter medications, proposed investigations etc.</a>"
        treatmentTextDesc?.text = Html.fromHtml(strTreatmentPlan)
        val strSavePrescription =
            "<a>If you enter notes in Evaluation or Treatment Plan, use <font color='#000000'><b><a>Save Prescription</b></font> to generate a PDF prescription that you can print and share.</a>"
        savePrescriptionTextDes?.text = Html.fromHtml(strSavePrescription)
        val strSharePrescription =
            "<a>Use <font color='#000000'><b><a>Share Prescription</b></font> to share the generated prescriptions OR your handwritten notes with your patients via email/SMS.</a>"
        sharePrescriptionTextDes?.text = Html.fromHtml(strSharePrescription)
        val intent = intent
        patientId = intent.getIntExtra("patientId", 0)
        episodeId = intent.getIntExtra("episodeId", 0)
        encounterId = intent.getIntExtra("encounterId", 0)
        encounterName = intent.getStringExtra("encounterName")
        encounterCreeatedOn = intent.getStringExtra("encounterCreatedOn")
        caseName = intent.getStringExtra("caseName")
        patientName = intent.getStringExtra("patientName")
        patient_name?.text = patientName
        apptDetailsBack?.setOnClickListener(View.OnClickListener { finish() })
        caseNameText?.setText(caseName)

        launchCreateRecordResults = registerForActivityResult<Intent, ActivityResult>(
            ActivityResultContracts.StartActivityForResult()
        ) { result -> //Request code 1
            val data = result.data
            val resultCode = result.resultCode
        }
        if (encounterCreeatedOn != null) {
            val interactionCreatedOn: String = encounterCreeatedOn as String
            val separatedInteractionDate =
                interactionCreatedOn.split(" ".toRegex()).dropLastWhile { it.isEmpty() }
                    .toTypedArray()
            val createInteractionDate = separatedInteractionDate[0]
            val interactionDate =
                appUtilities!!.changeDateFormat("yyyy-MM-dd", "dd MMM, yy", createInteractionDate)
            val createdInteractionTime =
                separatedInteractionDate[1].substring(0, separatedInteractionDate[1].length - 3)
            interactionDetails?.text = encounterName + " on " + interactionDate + " @ " + appUtils!!.formatTimeBasedOnPreference(
                this@EMRAddNotesActivity,
                "HH:mm",
                createdInteractionTime
            )
        }
        sharedPreferences = getSharedPreferences(AppConstants.appSharedPref, MODE_PRIVATE)
        proceedToAddRecords = findViewById(R.id.procreed_records)
        uploadHandWrittenNotes = findViewById(R.id.upload_handwritten_notes)
        logEvalutaionReport = findViewById(R.id.log_evaluation_records)
        logTreatmentPlan = findViewById(R.id.log_treatment)
        dictate_prescription_button = findViewById(R.id.dictate_prescription_button)
        noRecordPreferencesSetText = findViewById(R.id.noRecordPreferencesSetText)
        recordPreferencesLoadProgressbar = findViewById(R.id.recordPreferencesLoadProgressbar)
        savePrescriptionButton = findViewById(R.id.savePrescriptionButton)
        share_prescription = findViewById(R.id.share_prescription)
        caseDetailsRecycler = findViewById(R.id.case_details_recycler)
        noNoteLayout = findViewById(R.id.no_notes_layout)
        caseFooter = findViewById(R.id.case_footer)
        floatingActionButton = findViewById(R.id.case_fab_menu)
        dictationFab = findViewById<View>(R.id.dictationFab) as FloatingActionButton
        handwrittenNotesfb = findViewById<View>(R.id.upload_handwritten_fab) as FloatingActionButton
        logEvaluationRecordfb = findViewById<View>(R.id.log_evaluation_fab) as FloatingActionButton
        logTreatmentPlanfb = findViewById<View>(R.id.log_treatment_fab) as FloatingActionButton
        viewPrescriptionFb = findViewById<View>(R.id.view_prescription_fab) as FloatingActionButton
        exit_button = findViewById(R.id.exit_button)
        transparentView = findViewById(R.id.transparentView)
        howItworkd = findViewById(R.id.add_notes_how_it_works)
        caseLayout = findViewById(R.id.case_layout)
        dontShowAgain = findViewById(R.id.interaction_dont_show_again)
        dontShowAgain?.setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener { compoundButton, b ->
            if (b) {
                editor = sharedPreferences?.edit()
                editor?.putBoolean("dontShowCreateRecords", true)
                editor?.apply()
            }
        })
        dictationFab!!.setOnClickListener {
            val myIntent = Intent(this@EMRAddNotesActivity, VoiceEMRActivity::class.java)
            myIntent.putExtra("patientId", patientId)
            myIntent.putExtra("episodeId", episodeId)
            myIntent.putExtra("encounterId", encounterId)
            myIntent.putExtra("categoryName", "")
            myIntent.putExtra("patientName", patientName)
            startActivity(myIntent)
        }

        noNoteLayout?.visibility = View.GONE
        caseDetailsManager = LinearLayoutManager(this)
        caseDetailsList = ArrayList()
        emrAddingNotesAdapter = patientName?.let {
            EMRAddingNotesAdapter(
                this,
                caseDetailsList as ArrayList<EMRConsultCaseHistoryModel>,
                canDeleteEditRecords,
                canDeleteEditWrittenNotes,
                it,
                ""
            )
        }
        caseDetailsRecycler?.layoutManager = caseDetailsManager
        caseDetailsRecycler?.itemAnimator = DefaultItemAnimator()
        caseDetailsRecycler?.adapter = emrAddingNotesAdapter
        emrCreateRecordsFormViewModel = ViewModelProvider(this).get(
            EMRCreateRecordsFormViewModel::class.java
        )
        emrCreateRecordsFormViewModel!!.init()
        myClinicGlobalClass = applicationContext as MyClinicGlobalClass
        prescriptionDialog = Dialog(this@EMRAddNotesActivity)
        prescriptionDialog!!.setCancelable(false)
        prescriptionDialog!!.setContentView(R.layout.dailog_emr_all_prescription_popup)
        dialogEvaluationCancel =
            prescriptionDialog!!.findViewById<View>(R.id.dialogAllPrescriptionCancel) as ImageView
        allPrescriptionRecyclerView =
            prescriptionDialog!!.findViewById(R.id.allPrescriptionRecyclerView)
        prescriptionProgress = prescriptionDialog!!.findViewById(R.id.prescription_progress)
        prescriptionLayout = prescriptionDialog!!.findViewById(R.id.prescriptionAllListLayout)
        noCaseLayout = prescriptionDialog!!.findViewById(R.id.dialogNoPrescriptionLayout)
        sharePerscriptionPopup = prescriptionDialog!!.findViewById(R.id.share_prescription_popup)
        prescriptionCreateNewText =
            prescriptionDialog!!.findViewById(R.id.prescriptionCreateNewText)
        addingNotesProgress = findViewById(R.id.addingg_notes_progress)
        addingNotesProgress?.visibility = View.GONE
        floatingActionButton?.visibility = View.GONE
        dictationFab!!.visibility = View.GONE
        caseFooter?.visibility = View.INVISIBLE
        prescriptionProgress?.visibility = View.GONE
        allPrescriptionLayoutManager = LinearLayoutManager(applicationContext)
        allPrescriptionRecyclerView?.layoutManager = allPrescriptionLayoutManager
        allPrescriptionRecyclerView?.itemAnimator = DefaultItemAnimator()
        allPrescriptionList = ArrayList()
        progressDialog = ProgressDialog(this@EMRAddNotesActivity)
        progressDialog!!.setMessage("Please wait...")
        allPrescriptionListAdapter =
            EMRAllPrescriptionListAdapter(allPrescriptionList as ArrayList<EMRPrescriptionModel>, this) { v, emrPrescriptionModel ->
                progressDialog!!.show()
                emrCreateRecordsFormViewModel!!.getImagePath(
                    this@EMRAddNotesActivity,
                    emrPrescriptionModel.url
                ).observe(this@EMRAddNotesActivity, object : Observer<String?> {
                    override fun onChanged(value: String?) {
                        progressDialog!!.dismiss()
                        try {
                            val response = JSONObject(value)
                            if (response.getInt("status_code") == 200) {
                                val completeUrl =
                                    response.getJSONObject("response").getString("response")
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(completeUrl))
                                //                              Note the Chooser below. If no applications match,
//                              Android displays a system message.So here there is no need for try-catch.
                                startActivity(Intent.createChooser(intent, "Browse with"))
                            } else {
                                if (value != null) {
                                    errorHandler(this@EMRAddNotesActivity, value)
                                }
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                })
            }
        allPrescriptionRecyclerView?.adapter = allPrescriptionListAdapter
        sharePerscriptionPopup?.setOnClickListener(View.OnClickListener {
            prescriptionDialog!!.dismiss()
            isFbViewAllPrescriptionClicked = true
            getPrescriptionNoteCountData()
        })
        prescriptionCreateNewText?.setOnClickListener(View.OnClickListener {
            prescriptionDialog!!.dismiss()
            savePrescriptionPopup()
        })
        savePrescriptionButton?.setOnClickListener(View.OnClickListener { //                savePrescriptionPopup();
            savePrescriptionNotePopup()
        })
        share_prescription?.setOnClickListener(View.OnClickListener {
            getPrescriptionNoteCountData()
        })
        uploadHandWrittenNotes?.setOnClickListener(View.OnClickListener {
            val myIntent =
                Intent(this@EMRAddNotesActivity, EMRCreateRecordsFormActivity::class.java)
            myIntent.putExtra("patientId", patientId)
            myIntent.putExtra("episodeId", episodeId)
            myIntent.putExtra("encounterId", encounterId)
            myIntent.putExtra("categoryName", "")
            myIntent.putExtra("patientName", patientName)
            startActivity(myIntent)
        })
        logEvalutaionReport?.setOnClickListener(View.OnClickListener { logEvaluationPopup() })
        logTreatmentPlan?.setOnClickListener(View.OnClickListener { logTreatmentPlanPopup() })
        dictate_prescription_button?.setOnClickListener(View.OnClickListener {
            val myIntent = Intent(this@EMRAddNotesActivity, VoiceEMRActivity::class.java)
            myIntent.putExtra("patientId", patientId)
            myIntent.putExtra("episodeId", episodeId)
            myIntent.putExtra("encounterId", encounterId)
            myIntent.putExtra("categoryName", "")
            myIntent.putExtra("patientName", patientName)
            startActivity(myIntent)
        })
        proceedToAddRecords?.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View) {
                howItworkd?.visibility = View.GONE
                caseLayout?.visibility = View.VISIBLE
                noNoteLayout?.visibility = View.VISIBLE
                recordPreferencesLoadProgressbar?.visibility = View.VISIBLE
                episodeFilePreferences
            }
        })
        exit_button?.setOnClickListener(View.OnClickListener { finish() })


        //handling each floating action button clicked
        handwrittenNotesfb!!.setOnClickListener(onButtonClick())
        logEvaluationRecordfb!!.setOnClickListener(onButtonClick())
        logTreatmentPlanfb!!.setOnClickListener(onButtonClick())
        viewPrescriptionFb!!.setOnClickListener(onButtonClick())
    }

    private fun onButtonClick(): View.OnClickListener {
        return View.OnClickListener { view ->
            if (view === handwrittenNotesfb) {
                val myIntent =
                    Intent(this@EMRAddNotesActivity, EMRCreateRecordsFormActivity::class.java)
                myIntent.putExtra("patientId", patientId)
                myIntent.putExtra("episodeId", episodeId)
                myIntent.putExtra("encounterId", encounterId)
                myIntent.putExtra("categoryName", "")
                myIntent.putExtra("patientName", patientName)
                startActivity(myIntent)
            } else if (view === logEvaluationRecordfb) {
                logEvaluationPopup()
            } else if (view === logTreatmentPlanfb) {
                logTreatmentPlanPopup()
            } else {
                allPrescriptionPopup()
            }
            floatingActionButton!!.close(true)
        }
    }

    private fun showToast(msg: String) {
        Toast.makeText(this@EMRAddNotesActivity, msg, Toast.LENGTH_SHORT).show()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    fun logEvaluationPopup() {
        emrEvaluationCategoryList = ArrayList()
        evaluationLayoutManager = LinearLayoutManager(applicationContext)
        val dialog = Dialog(this@EMRAddNotesActivity)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.dailog_emr_log_evaluation_popup)
        val dialogEvaluationCancel =
            dialog.findViewById<View>(R.id.dialogEvaluationCancel) as ImageView
        val emrCategoryLoadProgressbar =
            dialog.findViewById<View>(R.id.emrCategoryLoadProgressbar) as RelativeLayout
        evaluationCategoryRecyclerView =
            dialog.findViewById<View>(R.id.evaluationCategoryRecyclerView) as RecyclerView
        evaluationCategoryRecyclerView!!.layoutManager = evaluationLayoutManager
        evaluationCategoryRecyclerView!!.itemAnimator = DefaultItemAnimator()
        //evaluationCategoryRecyclerView.setAdapter(emrEvaluationCategoryListAdapter);
        emrEvaluationCategoryListAdapter = EMREvaluationCategoryListAdapter(
            emrEvaluationCategoryList as ArrayList<EMRAddRecordCategoryModel>,
            this@EMRAddNotesActivity
        ) { v, position, catId, catName ->
            if (catName.equals(
                    "Symptoms",
                    ignoreCase = true
                ) || catName.equals(
                    "Investigation Results",
                    ignoreCase = true
                ) || catName.equals("Diagnosis", ignoreCase = true)
            ) {
                if (encounterId != 0) {
                    val myIntent =
                        Intent(this@EMRAddNotesActivity, EMRCreateRecordsFormActivity::class.java)
                    myIntent.putExtra("patientId", patientId)
                    myIntent.putExtra("episodeId", episodeId)
                    myIntent.putExtra("encounterId", encounterId)
                    myIntent.putExtra("categoryName", catName)
                    myIntent.putExtra("patientName", patientName)
                    startActivity(myIntent)
                    dialog.dismiss()
                } else {
                    Toast.makeText(
                        this@EMRAddNotesActivity,
                        "Please select one interaction before adding a record",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } else if (encounterId != 0) {
                val intent = Intent(this@EMRAddNotesActivity, EMRCreateRecordActivity::class.java)
                intent.putExtra("CategoryId", catId + "")
                intent.putExtra("CategoryName", catName)
                intent.putExtra("PatientId", patientId)
                intent.putExtra("EpisodeId", episodeId)
                intent.putExtra("EncounterID", encounterId)
                intent.putExtra("Type", "observations")
                launchCreateRecordResults!!.launch(intent)
                dialog.dismiss()
            } else {
                Toast.makeText(
                    this@EMRAddNotesActivity,
                    "Please select one interaction before adding a record",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
        evaluationCategoryRecyclerView!!.adapter = emrEvaluationCategoryListAdapter
        loadRecyclerViewEvaluationCategoryItem(emrCategoryLoadProgressbar)
        dialogEvaluationCancel.setOnClickListener { dialog.dismiss() }
        dialog.show()
    }

    private fun loadRecyclerViewEvaluationCategoryItem(emrCategoryLoadProgressbar: RelativeLayout) {
        if (isEvaluationPreferences) {
            emrConsultationNotesViewModel!!.getEvaluationFieldPreferences(this)
                .observe(this, object : Observer<String?> {
                    override fun onChanged(value: String?) {
                        try {
                            Log.d("EvaluationFieldPrefere", "" + value)
                            val jsonObject = JSONObject(value)
                            if (jsonObject.getInt("status_code") == 200) {
                                val response = JSONObject(value).getJSONObject("response")
                                val prefArr = response.getJSONArray("response")
                                val typeArray =
                                    arrayOf("Symptoms", "", "Investigation Results", "Diagnosis")
                                emrCategoryLoadProgressbar.visibility = View.GONE
                                for (i in 0 until prefArr.length()) {
                                    if (i != 1) {
                                        if (prefArr.getJSONObject(i).optInt("status") == 1) {
                                            val myList = EMRAddRecordCategoryModel(
                                                "0", typeArray[i]
                                            )
                                            emrEvaluationCategoryList!!.add(myList)
                                        }
                                    } else {
                                        if (prefArr.getJSONObject(i).optInt("status") == 1) {
                                            emrConsultationNotesViewModel!!.getEvaluationDoctorCategory(
                                                this@EMRAddNotesActivity
                                            ).observe(
                                                this@EMRAddNotesActivity,
                                                object : Observer<String?> {
                                                    override fun onChanged(value: String?) {
                                                        try {
                                                            //emrCategoryLoadProgressbar.setVisibility(View.GONE);
                                                            val jsonObject = JSONObject(value)
                                                            if (jsonObject.getInt("status_code") == 200) {
                                                                val response =
                                                                    JSONObject(value).getJSONObject("response")
                                                                val rootArray =
                                                                    response.getJSONArray("response")
                                                                for (i in 0 until rootArray.length()) {
                                                                    val categoryArrayObject =
                                                                        rootArray.getJSONObject(i)
                                                                    val myList =
                                                                        EMRAddRecordCategoryModel(
                                                                            categoryArrayObject.getString(
                                                                                "category"
                                                                            ),
                                                                            categoryArrayObject.getString(
                                                                                "name"
                                                                            )
                                                                        )
                                                                    emrEvaluationCategoryList!!.add(
                                                                        myList
                                                                    )
                                                                }
                                                                emrEvaluationCategoryListAdapter!!.notifyDataSetChanged()
                                                            } else {
                                                                if (value != null) {
                                                                    errorHandler(
                                                                        this@EMRAddNotesActivity,
                                                                        value
                                                                    )
                                                                }
                                                            }
                                                        } catch (e: JSONException) {
                                                            e.printStackTrace()
                                                        }
                                                    }
                                                })
                                        }
                                    }
                                }
                                emrEvaluationCategoryListAdapter!!.notifyDataSetChanged()
                            } else {
                                if (value != null) {
                                    errorHandler(this@EMRAddNotesActivity, value)
                                }
                            }
                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }
                    }
                })
        } else {
            emrConsultationNotesViewModel!!.getEvaluationDoctorCategory(this)
                .observe(this, object : Observer<String?> {
                    override fun onChanged(value: String?) {
                        try {
                            emrCategoryLoadProgressbar.visibility = View.GONE
                            val jsonObject = JSONObject(value)
                            if (jsonObject.getInt("status_code") == 200) {
                                val response = JSONObject(value).getJSONObject("response")
                                val rootArray = response.getJSONArray("response")
                                for (i in 0 until rootArray.length()) {
                                    val categoryArrayObject = rootArray.getJSONObject(i)
                                    val myList = EMRAddRecordCategoryModel(
                                        categoryArrayObject.getString("category"),
                                        categoryArrayObject.getString("name")
                                    )
                                    emrEvaluationCategoryList!!.add(myList)
                                }
                                emrEvaluationCategoryListAdapter!!.notifyDataSetChanged()
                            } else {
                                if (value != null) {
                                    errorHandler(this@EMRAddNotesActivity, value)
                                }
                            }
                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }
                    }
                })
        }
    }

    fun logTreatmentPlanPopup() {
        emrTreatmentPlanCategoryList = ArrayList()
        treatmentPlanLayoutManager = LinearLayoutManager(applicationContext)
        val dialog = Dialog(this@EMRAddNotesActivity)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.dailog_emr_log_treatment_plan_popup)
        val dialogEvaluationCancel =
            dialog.findViewById<View>(R.id.dialogTreatmentPlanCancel) as ImageView
        val emrCategoryLoadProgressbar =
            dialog.findViewById<View>(R.id.emrCategoryLoadProgressbar) as RelativeLayout
        treatmentPlanCategoryRecyclerView =
            dialog.findViewById<View>(R.id.treatmentPlanCategoryRecyclerView) as RecyclerView
        treatmentPlanCategoryRecyclerView!!.layoutManager = treatmentPlanLayoutManager
        treatmentPlanCategoryRecyclerView!!.itemAnimator = DefaultItemAnimator()
        treatmentPlanCategoryRecyclerView!!.adapter = emrTreatmentPlanCategoryListAdapter
        loadRecyclerViewTreatmentCategoryItem(emrCategoryLoadProgressbar, dialog)
        dialogEvaluationCancel.setOnClickListener { dialog.dismiss() }
        dialog.show()
    }

    private fun loadRecyclerViewTreatmentCategoryItem(
        emrCategoryLoadProgressbar: RelativeLayout,
        dialog: Dialog
    ) {
        emrConsultationNotesViewModel!!.getTreatmentPlanDoctorCategory(this)
            .observe(this, object : Observer<String?> {
                override fun onChanged(value: String?) {
                    try {
                        emrCategoryLoadProgressbar.visibility = View.GONE
                        val jsonObject = JSONObject(value)
                        if (jsonObject.getInt("status_code") == 200) {
                            val response = JSONObject(value).getJSONObject("response")
                            val rootArray = response.getJSONArray("response")
                            for (i in 0 until rootArray.length()) {
                                val categoryArrayObject = rootArray.getJSONObject(i)
                                val myList = EMRAddRecordCategoryModel(
                                    categoryArrayObject.getString("category"),
                                    categoryArrayObject.getString("name")
                                )
                                emrTreatmentPlanCategoryList!!.add(myList)
                            }
                            emrTreatmentPlanCategoryListAdapter =
                                emrTreatmentPlanCategoryList?.let {
                                    EMRTreatmentPlanCategoryListAdapter(
                                        it,
                                        this@EMRAddNotesActivity
                                    ) { v, position, catId, catName ->
                                        if (encounterId != 0) {
                                            val intent = Intent(
                                                this@EMRAddNotesActivity,
                                                EMRCreateRecordActivity::class.java
                                            )
                                            intent.putExtra("CategoryId", catId + "")
                                            intent.putExtra("CategoryName", catName)
                                            intent.putExtra("PatientId", patientId)
                                            intent.putExtra("EpisodeId", episodeId)
                                            intent.putExtra("EncounterID", encounterId)
                                            intent.putExtra("Type", "treatmentplan")
                                            launchCreateRecordResults!!.launch(intent)
                                            dialog.dismiss()
                                        } else {
                                            Toast.makeText(
                                                this@EMRAddNotesActivity,
                                                "Please select one interaction before adding a record",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    }
                                }
                            treatmentPlanCategoryRecyclerView!!.adapter =
                                emrTreatmentPlanCategoryListAdapter
                        } else {
                            if (value != null) {
                                errorHandler(this@EMRAddNotesActivity, value)
                            }
                        }
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                }
            })
    }

    fun getInteractionDetails(patId: Int, encounterId: Int) {
        caseDetailsList!!.clear()
        emrAddingNotesAdapter!!.notifyDataSetChanged()
        addingNotesProgress!!.visibility = View.VISIBLE
        caseFooter!!.visibility = View.INVISIBLE
        floatingActionButton!!.visibility = View.GONE
        dictationFab!!.visibility = View.GONE
        emrConsultationNotesViewModel!!.getAddedNotes(this@EMRAddNotesActivity, patId, encounterId)
            .observe(this@EMRAddNotesActivity, object : Observer<String?> {
                override fun onChanged(value: String?) {
                    addingNotesProgress!!.visibility = View.GONE
                    try {
                        caseDetailsList!!.clear()
                        val response = JSONObject(value)
                        if (response.getInt("status_code") == 200) {
                            noNoteLayout!!.visibility = View.GONE
                            caseDetailsRecycler!!.visibility = View.VISIBLE
                            floatingActionButton!!.visibility = View.VISIBLE
                            dictationFab!!.visibility = View.VISIBLE
                            caseLayout!!.visibility = View.VISIBLE
                            howItworkd!!.visibility = View.GONE
                            val responseObj =
                                response.getJSONObject("response").getJSONObject("response")
                            val fieldDicArr = responseObj.getJSONObject("field-dictionary")
                            if (responseObj.getInt("allRecordsCount") > 0) {
                                uploadHandWrittenNotes!!.visibility = View.GONE
                                logEvalutaionReport!!.visibility = View.GONE
                                logTreatmentPlan!!.visibility = View.GONE
                                dictate_prescription_button!!.visibility = View.GONE
                                noRecordPreferencesSetText!!.visibility = View.GONE
                                caseFooter!!.visibility = View.VISIBLE

                                //handwritten record Data
                                val HandWrittenNoteArray =
                                    responseObj.optJSONArray("handWrittenNotesRecords")
                                if (HandWrittenNoteArray != null) {
                                    if (HandWrittenNoteArray.length() > 0) {
                                        for (i in 0 until HandWrittenNoteArray.length()) {
                                            val handWrittenNoteObject =
                                                HandWrittenNoteArray.getJSONObject(i)
                                            val emrConsultCaseHistoryModel =
                                                EMRConsultCaseHistoryModel()
                                            emrConsultCaseHistoryModel.encounterID = encounterId
                                            emrConsultCaseHistoryModel.encounterName = encounterName
                                            emrConsultCaseHistoryModel.encounterDateTime =
                                                encounterCreeatedOn
                                            emrConsultCaseHistoryModel.categoryName =
                                                "HandWritten Note"
                                            emrConsultCaseHistoryModel.createdAt =
                                                handWrittenNoteObject.getString("created_at")
                                            emrConsultCaseHistoryModel.isRecordData = 1
                                            //------------by dileep-------------
                                            val recordDetailsArray = JSONArray()
                                            recordDetailsArray.put(handWrittenNoteObject)
                                            //                                        int recordId = sharedObj.getInt("record_id");// added by dileep
                                            emrConsultCaseHistoryModel.categoryId =
                                                handWrittenNoteObject.getInt("id") //added by dileep
                                            emrConsultCaseHistoryModel.fieldDictionary =
                                                fieldDicArr.toString() // added by dileep
                                            //                                        model.setRecordId(recordId);// added by dileep
                                            emrConsultCaseHistoryModel.categoryRecordData =
                                                recordDetailsArray.toString()
                                            caseDetailsList!!.add(emrConsultCaseHistoryModel)
                                        }
                                    }
                                }

                                //symptoms record Data
                                val symptomsArray = responseObj.optJSONArray("symptomsRecords")
                                if (symptomsArray != null) {
                                    if (symptomsArray.length() > 0) {
                                        for (i in 0 until symptomsArray.length()) {
                                            val symptomsObject = symptomsArray.getJSONObject(i)
                                            val emrConsultCaseHistoryModel =
                                                EMRConsultCaseHistoryModel()
                                            emrConsultCaseHistoryModel.encounterID = encounterId
                                            emrConsultCaseHistoryModel.encounterName = encounterName
                                            emrConsultCaseHistoryModel.encounterDateTime =
                                                encounterCreeatedOn
                                            emrConsultCaseHistoryModel.categoryName = "Symptoms"
                                            emrConsultCaseHistoryModel.createdAt =
                                                symptomsObject.getString("created_at")
                                            emrConsultCaseHistoryModel.isRecordData = 1

                                            //------------by dileep-------------
                                            val recordDetailsArray = JSONArray()
                                            recordDetailsArray.put(symptomsObject)
                                            //                                        int recordId = sharedObj.getInt("record_id");// added by dileep
                                            emrConsultCaseHistoryModel.categoryId =
                                                symptomsObject.getInt("id") //added by dileep
                                            emrConsultCaseHistoryModel.fieldDictionary =
                                                fieldDicArr.toString() // added by dileep
                                            //                                        model.setRecordId(recordId);// added by dileep
                                            emrConsultCaseHistoryModel.categoryRecordData =
                                                recordDetailsArray.toString()
                                            caseDetailsList!!.add(emrConsultCaseHistoryModel)
                                        }
                                    }
                                }


                                //observation record data
                                val observationArray =
                                    responseObj.optJSONArray("observationsRecords")
                                if (observationArray != null) {
                                    if (observationArray.length() > 0) {
                                        for (i in 0 until observationArray.length()) {
                                            val observationObject =
                                                observationArray.getJSONObject(i)
                                            //                                            EMRConsultCaseHistoryModel emrConsultCaseHistoryModel = new EMRConsultCaseHistoryModel();
                                            val observationRecordsArray =
                                                observationObject.getJSONArray("records")
                                            val observationRecordCategoryObject =
                                                observationObject.getJSONObject("record_categories")
                                            if (observationRecordsArray.length() > 0) {
                                                for (obs in 0 until observationRecordsArray.length()) {
                                                    val emrConsultCaseHistoryModel =
                                                        EMRConsultCaseHistoryModel()
                                                    emrConsultCaseHistoryModel.encounterID =
                                                        encounterId
                                                    emrConsultCaseHistoryModel.encounterName =
                                                        encounterName
                                                    emrConsultCaseHistoryModel.encounterDateTime =
                                                        encounterCreeatedOn
                                                    emrConsultCaseHistoryModel.categoryName =
                                                        observationRecordCategoryObject.getString("category")
                                                    emrConsultCaseHistoryModel.createdAt =
                                                        observationRecordsArray.getJSONObject(obs)
                                                            .getString("created_at")
                                                    emrConsultCaseHistoryModel.multiRecordPosition =
                                                        obs
                                                    emrConsultCaseHistoryModel.isRecordData = 1
                                                    emrConsultCaseHistoryModel.categoryType =
                                                        "observations"

                                                    //------------by dileep-------------
                                                    val recordDetailsArray = JSONArray()
                                                    recordDetailsArray.put(observationObject)
                                                    val recordId =
                                                        observationObject.getInt("id") // added by dileep
                                                    emrConsultCaseHistoryModel.categoryId =
                                                        observationRecordCategoryObject.getInt("id") //added by dileep
                                                    emrConsultCaseHistoryModel.fieldDictionary =
                                                        fieldDicArr.toString() // added by dileep
                                                    emrConsultCaseHistoryModel.recordId =
                                                        recordId // added by dileep
                                                    emrConsultCaseHistoryModel.categoryRecordData =
                                                        recordDetailsArray.toString()
                                                    caseDetailsList!!.add(emrConsultCaseHistoryModel)
                                                }
                                            }
                                        }
                                    }
                                }


                                //investigation results record Data
                                val investigationResultRecordsArray =
                                    responseObj.optJSONArray("investigationResultsRecords")
                                if (investigationResultRecordsArray != null) {
                                    if (investigationResultRecordsArray.length() > 0) {
                                        for (i in 0 until investigationResultRecordsArray.length()) {
                                            val investigationResultRecordsObject =
                                                investigationResultRecordsArray.getJSONObject(i)
                                            val emrConsultCaseHistoryModel =
                                                EMRConsultCaseHistoryModel()
                                            emrConsultCaseHistoryModel.encounterID = encounterId
                                            emrConsultCaseHistoryModel.encounterName = encounterName
                                            emrConsultCaseHistoryModel.encounterDateTime =
                                                encounterCreeatedOn
                                            emrConsultCaseHistoryModel.categoryName =
                                                "Investigation Results"
                                            emrConsultCaseHistoryModel.createdAt =
                                                investigationResultRecordsObject.getString("created_at")
                                            emrConsultCaseHistoryModel.isRecordData = 1

                                            //------------by dileep-------------
                                            val recordDetailsArray = JSONArray()
                                            recordDetailsArray.put(investigationResultRecordsObject)
                                            //                                        int recordId = sharedObj.getInt("record_id");// added by dileep
                                            emrConsultCaseHistoryModel.categoryId =
                                                investigationResultRecordsObject.getInt("id") //added by dileep
                                            emrConsultCaseHistoryModel.fieldDictionary =
                                                fieldDicArr.toString() // added by dileep
                                            //                                        model.setRecordId(recordId);// added by dileep
                                            emrConsultCaseHistoryModel.categoryRecordData =
                                                recordDetailsArray.toString()
                                            caseDetailsList!!.add(emrConsultCaseHistoryModel)
                                        }
                                    }
                                }

                                //diagnosis record Data
                                val diagnosisRecordsArray =
                                    responseObj.optJSONArray("diagnosisRecords")
                                if (diagnosisRecordsArray != null) {
                                    if (diagnosisRecordsArray.length() > 0) {
                                        for (i in 0 until diagnosisRecordsArray.length()) {
                                            val diagnosisRecordsArrayRecordsObject =
                                                diagnosisRecordsArray.getJSONObject(i)
                                            val emrConsultCaseHistoryModel =
                                                EMRConsultCaseHistoryModel()
                                            emrConsultCaseHistoryModel.encounterID = encounterId
                                            emrConsultCaseHistoryModel.encounterName = encounterName
                                            emrConsultCaseHistoryModel.encounterDateTime =
                                                encounterCreeatedOn
                                            emrConsultCaseHistoryModel.categoryName = "Diagnosis"
                                            emrConsultCaseHistoryModel.createdAt =
                                                diagnosisRecordsArrayRecordsObject.getString("created_at")
                                            emrConsultCaseHistoryModel.isRecordData = 1

                                            //------------by dileep-------------
                                            val recordDetailsArray = JSONArray()
                                            recordDetailsArray.put(
                                                diagnosisRecordsArrayRecordsObject
                                            )
                                            //                                        int recordId = sharedObj.getInt("record_id");// added by dileep
                                            emrConsultCaseHistoryModel.categoryId =
                                                diagnosisRecordsArrayRecordsObject.getInt("id") //added by dileep
                                            emrConsultCaseHistoryModel.fieldDictionary =
                                                fieldDicArr.toString() // added by dileep
                                            //                                        model.setRecordId(recordId);// added by dileep
                                            emrConsultCaseHistoryModel.categoryRecordData =
                                                recordDetailsArray.toString()
                                            caseDetailsList!!.add(emrConsultCaseHistoryModel)
                                        }
                                    }
                                }


                                //treatment Plan record data
                                val treatmentPlanArray =
                                    responseObj.optJSONArray("treatmentPlanRecords")
                                if (treatmentPlanArray != null) {
                                    if (treatmentPlanArray.length() > 0) {
                                        for (i in 0 until treatmentPlanArray.length()) {
                                            val treatmentPlanObject =
                                                treatmentPlanArray.getJSONObject(i)
                                            //                                            EMRConsultCaseHistoryModel emrConsultCaseHistoryModel = new EMRConsultCaseHistoryModel();
                                            val treatmentPlanRecordsArray =
                                                treatmentPlanObject.getJSONArray("records")
                                            val treatmentPlanRecordCategoryObject =
                                                treatmentPlanObject.getJSONObject("record_categories")
                                            if (treatmentPlanRecordsArray.length() > 0) {
                                                for (treat in 0 until treatmentPlanRecordsArray.length()) {
                                                    val emrConsultCaseHistoryModel =
                                                        EMRConsultCaseHistoryModel()
                                                    emrConsultCaseHistoryModel.encounterID =
                                                        encounterId
                                                    emrConsultCaseHistoryModel.encounterName =
                                                        encounterName
                                                    emrConsultCaseHistoryModel.encounterDateTime =
                                                        encounterCreeatedOn
                                                    emrConsultCaseHistoryModel.categoryName =
                                                        treatmentPlanRecordCategoryObject.getString(
                                                            "category"
                                                        )
                                                    emrConsultCaseHistoryModel.createdAt =
                                                        treatmentPlanRecordsArray.getJSONObject(
                                                            treat
                                                        ).getString("created_at")
                                                    emrConsultCaseHistoryModel.multiRecordPosition =
                                                        treat
                                                    emrConsultCaseHistoryModel.isRecordData = 1
                                                    emrConsultCaseHistoryModel.categoryType =
                                                        "treatmentplan"
                                                    //------------by dileep-------------
                                                    val recordDetailsArray = JSONArray()
                                                    recordDetailsArray.put(treatmentPlanObject)
                                                    val recordId =
                                                        treatmentPlanObject.getInt("id") // added by dileep
                                                    emrConsultCaseHistoryModel.categoryId =
                                                        treatmentPlanRecordCategoryObject.getInt("id") //added by dileep
                                                    emrConsultCaseHistoryModel.fieldDictionary =
                                                        fieldDicArr.toString() // added by dileep
                                                    emrConsultCaseHistoryModel.recordId =
                                                        recordId // added by dileep
                                                    emrConsultCaseHistoryModel.categoryRecordData =
                                                        recordDetailsArray.toString()
                                                    caseDetailsList!!.add(emrConsultCaseHistoryModel)
                                                }
                                            }
                                        }
                                    }
                                }
                                canDeleteEditRecords =
                                    responseObj.getBoolean("canDeleteEditRecords")
                                canDeleteEditWrittenNotes =
                                    responseObj.getBoolean("canDeleteEditWrittenNotes")
                                val encounterData = responseObj.getJSONObject("encounterDetails")
                                val dynamicEncounterIds = JSONObject()
                                dynamicEncounterIds.put("encounter_id", encounterData.getInt("id"))
                                dynamicEncounterIds.put(
                                    "episode_id",
                                    encounterData.getInt("episode_id")
                                )
                                dynamicEncounterIds.put(
                                    "patient_id",
                                    encounterData.getInt("patient_id")
                                )
                                dynamicEncounterIds.put(
                                    "doctor_id",
                                    encounterData.getInt("doctor_id")
                                )

                                //  emrAddingNotesAdapter.notifyDataSetChanged();
                                emrAddingNotesAdapter = patientName?.let {
                                    EMRAddingNotesAdapter(
                                        this@EMRAddNotesActivity,
                                        caseDetailsList!!,
                                        canDeleteEditRecords,
                                        canDeleteEditWrittenNotes,
                                        it,
                                        dynamicEncounterIds.toString()
                                    )
                                }
                                caseDetailsRecycler!!.layoutManager = caseDetailsManager
                                caseDetailsRecycler!!.itemAnimator = DefaultItemAnimator()
                                caseDetailsRecycler!!.adapter = emrAddingNotesAdapter
                            } else {
                                caseDetailsRecycler!!.visibility = View.GONE
                                floatingActionButton!!.visibility = View.GONE
                                dictationFab!!.visibility = View.GONE
                                if (sharedPreferences!!.getBoolean(
                                        "dontShowCreateRecords",
                                        false
                                    )
                                ) {
                                    howItworkd!!.visibility = View.GONE
                                    noNoteLayout!!.visibility = View.VISIBLE
                                    caseLayout!!.visibility = View.VISIBLE
                                    recordPreferencesLoadProgressbar!!.visibility = View.VISIBLE
                                    caseFooter!!.visibility = View.GONE
                                    episodeFilePreferences
                                } else {
                                    howItworkd!!.visibility = View.VISIBLE
                                    noNoteLayout!!.visibility = View.GONE
                                    caseLayout!!.visibility = View.GONE
                                }
                            }
                        } else {
                            if (value != null) {
                                errorHandler(this@EMRAddNotesActivity, value)
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            })
    }

    //noNoteLayout.setVisibility(View.VISIBLE);
    val episodeFilePreferences: Unit
        get() {
            emrConsultationNotesViewModel!!.getEpisodeFieldPreferences(this)
                .observe(this, object : Observer<String?> {
                    override fun onChanged(value: String?) {
                        try {
                            recordPreferencesLoadProgressbar!!.visibility = View.GONE
                            //noNoteLayout.setVisibility(View.VISIBLE);
                            val jsonObject = JSONObject(value)
                            if (jsonObject.getInt("status_code") == 200) {
                                responseEpisodeFieldPreferences =
                                    JSONObject(value).getJSONObject("response")
                                val prefArr =
                                    responseEpisodeFieldPreferences?.getJSONArray("response")
                                if (prefArr != null) {
                                    if (prefArr.getJSONObject(0).getInt("status") == 1) {
                                        uploadHandWrittenNotes!!.visibility = View.VISIBLE
                                        handwrittenNotesfb!!.visibility = View.VISIBLE
                                    } else {
                                        handwrittenNotesfb!!.visibility = View.GONE
                                    }
                                }
                                if (prefArr != null) {
                                    if (prefArr.getJSONObject(1).getInt("status") == 1) {
                                        logEvalutaionReport!!.visibility = View.VISIBLE
                                        isEvaluationPreferences = true
                                        logEvaluationRecordfb!!.visibility = View.VISIBLE
                                    } else {
                                        logEvaluationRecordfb!!.visibility = View.GONE
                                    }
                                }
                                if (prefArr != null) {
                                    if (prefArr.getJSONObject(2).getInt("status") == 1) {
                                        logTreatmentPlan!!.visibility = View.VISIBLE
                                        logTreatmentPlanfb!!.visibility = View.VISIBLE
                                    } else {
                                        logTreatmentPlanfb!!.visibility = View.GONE
                                    }
                                }
                                if (prefArr != null) {
                                    if (prefArr.getJSONObject(0)
                                            .getInt("status") == 0 && prefArr.getJSONObject(1)
                                            .getInt("status") == 0 && prefArr.getJSONObject(2)
                                            .getInt("status") == 0
                                    ) {
                                        noRecordPreferencesSetText!!.visibility = View.VISIBLE
                                    } else {
                                        noRecordPreferencesSetText!!.visibility = View.GONE
                                    }
                                }
                            } else {
                                if (value != null) {
                                    errorHandler(this@EMRAddNotesActivity, value)
                                }
                            }
                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }
                    }
                })
        }

    val dictaionControl: Unit
        get() {
            emrConsultationNotesViewModel!!.getDictationControl(this)
                .observe(this, object : Observer<String?> {
                    override fun onChanged(value: String?) {
                        try {
                            Log.d("dictaionPermission", "dictaionPermission$value")
                            //                    recordPreferencesLoadProgressbar.setVisibility(View.GONE);
                            //noNoteLayout.setVisibility(View.VISIBLE);
                            val jsonObject = JSONObject(value)
                            if (jsonObject.getInt("status_code") == 200) {
                                val dictaionPermission =
                                    JSONObject(value).getJSONObject("response").getInt("response")
                                if (dictaionPermission == 1) {
                                    dictate_prescription_button!!.visibility = View.VISIBLE
                                    dictationFab!!.visibility = View.GONE
                                } else {
                                    dictate_prescription_button!!.visibility = View.GONE
                                    dictationFab!!.visibility = View.GONE
                                }

                            } else {
                                if (value != null) {
                                    errorHandler(this@EMRAddNotesActivity, value)
                                }
                            }
                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }
                    }
                })
        }

    override fun onResume() {
        super.onResume()
        uploadHandWrittenNotes!!.visibility = View.GONE
        logEvalutaionReport!!.visibility = View.GONE
        logTreatmentPlan!!.visibility = View.GONE
        dictate_prescription_button!!.visibility = View.GONE
        noRecordPreferencesSetText!!.visibility = View.GONE
        recordPreferencesLoadProgressbar!!.visibility = View.VISIBLE
        episodeFilePreferences
        getInteractionDetails(patientId, encounterId)

        //get dictation control
        dictaionControl
    }

    fun savePrescriptionPopup() {
        val dialog = Dialog(this@EMRAddNotesActivity)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.dailog_emr_save_prescription_popup)
        val shareEcntEval = dialog.findViewById<View>(R.id.shareEcntEval) as CheckBox
        val shareEcntSymptom = dialog.findViewById<View>(R.id.shareEcntSymptom) as CheckBox
        val shareEcntObs = dialog.findViewById<View>(R.id.shareEcntObs) as CheckBox
        val shareEcntInvest = dialog.findViewById<View>(R.id.shareEcntInvest) as CheckBox
        val shareEcntDiag = dialog.findViewById<View>(R.id.shareEcntDiag) as CheckBox
        val shareEcntTreatPlan = dialog.findViewById<View>(R.id.shareEcntTreatPlan) as CheckBox
        val dialogEvaluationCancel =
            dialog.findViewById<View>(R.id.dialogEvaluationCancel) as? ImageView
        val createPrescriptionLayout =
            dialog.findViewById<View>(R.id.createPrescriptionLayout) as RelativeLayout
        val prescriptionCloseTextView =
            dialog.findViewById<View>(R.id.prescriptionCloseTextView) as TextView
        val preferencesNotAvailable =
            dialog.findViewById<View>(R.id.preferencesNotAvailable) as RelativeLayout
        val preferencesAvailable =
            dialog.findViewById<View>(R.id.preferencesAvailable) as RelativeLayout
        val shareEcntEvalListLayout =
            dialog.findViewById<View>(R.id.shareEcntEvalListLayout) as LinearLayout
        prescriptionCloseTextView.setOnClickListener { dialog.dismiss() }
        try {
            val prefArr = responseEpisodeFieldPreferences!!.getJSONArray("response")
            if (prefArr.getJSONObject(1).getInt("status") == 1) {
                shareEcntEval.visibility = View.VISIBLE
                shareEcntEvalListLayout.visibility = View.VISIBLE
                shareEcntEval.isChecked = true
                evaluation = true
                emrConsultationNotesViewModel!!.getEvaluationFieldPreferences(this)
                    .observe(this, object : Observer<String?> {
                        override fun onChanged(value: String?) {
                            try {
                                val jsonObject = JSONObject(value)
                                if (jsonObject.getInt("status_code") == 200) {
                                    val response = JSONObject(value).getJSONObject("response")
                                    val prefArr = response.getJSONArray("response")
                                    if (prefArr.getJSONObject(0).getInt("status") == 1) {
                                        //get symptom records
                                        shareEcntSymptom.visibility = View.VISIBLE
                                        shareEcntSymptom.isChecked = true
                                        symptoms = true
                                    } else {
                                        shareEcntSymptom.visibility = View.GONE
                                        symptoms = false
                                    }
                                    if (prefArr.getJSONObject(1).getInt("status") == 1) {
                                        //get observation records
                                        shareEcntObs.visibility = View.VISIBLE
                                        shareEcntObs.isChecked = true
                                        observations = true
                                    } else {
                                        shareEcntObs.visibility = View.GONE
                                        observations = false
                                    }
                                    if (prefArr.getJSONObject(2).getInt("status") == 1) {
                                        //get invetigation records
                                        shareEcntInvest.visibility = View.VISIBLE
                                        shareEcntInvest.isChecked = true
                                        investigations = true
                                    } else {
                                        shareEcntInvest.visibility = View.GONE
                                        investigations = false
                                    }
                                    if (prefArr.getJSONObject(3).getInt("status") == 1) {
                                        //get dignosis records
                                        shareEcntDiag.visibility = View.VISIBLE
                                        shareEcntDiag.isChecked = true
                                        diagnosis = true
                                    } else {
                                        shareEcntDiag.visibility = View.GONE
                                        diagnosis = false
                                    }
                                } else {
                                    if (value != null) {
                                        errorHandler(this@EMRAddNotesActivity, value)
                                    }
                                }
                            } catch (e: JSONException) {
                                e.printStackTrace()
                            }
                        }
                    })
            } else {
                shareEcntEval.visibility = View.GONE
                shareEcntEvalListLayout.visibility = View.GONE
                evaluation = false
            }
            if (prefArr.getJSONObject(2).getInt("status") == 1) {
                shareEcntTreatPlan.visibility = View.VISIBLE
                shareEcntTreatPlan.isChecked = true
                treatmentplan = true
            } else {
                shareEcntTreatPlan.visibility = View.GONE
                treatmentplan = false
            }
            if (prefArr.getJSONObject(1).getInt("status") == 0 && prefArr.getJSONObject(2)
                    .getInt("status") == 0
            ) {
                preferencesNotAvailable.visibility = View.VISIBLE
                preferencesAvailable.visibility = View.GONE
                createPrescriptionLayout.isEnabled = false
            } else {
                preferencesNotAvailable.visibility = View.GONE
                preferencesAvailable.visibility = View.VISIBLE
                createPrescriptionLayout.isEnabled = true
            }
        } catch (e: Exception) {
        }
        shareEcntEval.setOnCheckedChangeListener { buttonView, isChecked ->
            evaluation = isChecked
        }
        shareEcntSymptom.setOnCheckedChangeListener { buttonView, isChecked ->
            symptoms = isChecked
        }
        shareEcntObs.setOnCheckedChangeListener { buttonView, isChecked ->
            observations = isChecked
        }
        shareEcntInvest.setOnCheckedChangeListener { buttonView, isChecked ->
            investigations = isChecked
        }
        shareEcntDiag.setOnCheckedChangeListener { buttonView, isChecked ->
            diagnosis = isChecked
        }
        shareEcntTreatPlan.setOnCheckedChangeListener { buttonView, isChecked ->
            treatmentplan = isChecked
        }
        createPrescriptionLayout.setOnClickListener {
            try {
                val prescriptionObj = JSONObject()
                prescriptionObj.put("evaluation", evaluation)
                prescriptionObj.put("symptoms", symptoms)
                prescriptionObj.put("observations", observations)
                prescriptionObj.put("investigations", investigations)
                prescriptionObj.put("diagnosis", diagnosis)
                prescriptionObj.put("treatmentplan", treatmentplan)
                prescriptionObj.put("patient_id", patientId)
                prescriptionObj.put("episode_id", episodeId)
                prescriptionObj.put("encounter_id", encounterId)
                createPrescriptionData(prescriptionObj, dialog)
            } catch (e: Exception) {
            }
        }
        dialog.show()
    }

    fun sharedPrescriptionPopup(
        generatedPrescriptionCount: Int,
        handwrittenNoteCount: Int,
        totalPrescriptionCount: Int
    ) {
        val dialog = Dialog(this@EMRAddNotesActivity)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.dailog_emr_shared_prescription_popup)
        val generatedPrescriptionCb =
            dialog.findViewById<View>(R.id.generatedPrescriptionCb) as CheckBox
        val handWrittenNoteCb = dialog.findViewById<View>(R.id.handWrittenNoteCb) as CheckBox
        val sharedPrescriptionLayout =
            dialog.findViewById<View>(R.id.sharedPrescriptionLayout) as RelativeLayout
        val dialogEvaluationCancel =
            dialog.findViewById<View>(R.id.dialogSharePrescriptionCancel) as ImageView
        val createNewButton = dialog.findViewById<View>(R.id.createNewButton) as RelativeLayout
        generated_pdfs = false
        if (isFbViewAllPrescriptionClicked) {
            generatedPrescriptionCb.isChecked = true
            generated_pdfs = true
            isFbViewAllPrescriptionClicked = false
        }
        try {
            val prefArr = responseEpisodeFieldPreferences!!.getJSONArray("response")
            if (prefArr.getJSONObject(0).getInt("status") == 1) {
                handWrittenNoteCb.visibility = View.VISIBLE
                written_notes = false
            } else {
                handWrittenNoteCb.visibility = View.GONE
                written_notes = false
            }
        } catch (e: Exception) {
        }
        if (generatedPrescriptionCount == 0) {
            generatedPrescriptionCb.isEnabled = false
            generatedPrescriptionCb.setTextColor(resources.getColor(R.color.colorGrey1))
            generatedPrescriptionCb.text = "Generated Prescription(No prescriptions generated)"
        } else {
            generatedPrescriptionCb.isEnabled = true
            generatedPrescriptionCb.setTextColor(resources.getColor(R.color.colorBlack))
            generatedPrescriptionCb.text = "Generated Prescription"
        }
        if (handwrittenNoteCount == 0) {
            handWrittenNoteCb.isEnabled = false
            handWrittenNoteCb.setTextColor(resources.getColor(R.color.colorGrey1))
            handWrittenNoteCb.text = "HandWritten Notes (No handwritten notes uploaded)"
        } else {
            handWrittenNoteCb.isEnabled = true
            handWrittenNoteCb.setTextColor(resources.getColor(R.color.colorBlack))
            handWrittenNoteCb.text = "HandWritten Notes"
        }
        if (totalPrescriptionCount == 0) {
            sharedPrescriptionLayout.isEnabled = false
            sharedPrescriptionLayout.setBackgroundColor(resources.getColor(R.color.colorGrey1))
        } else {
            sharedPrescriptionLayout.isEnabled = true
            sharedPrescriptionLayout.setBackgroundColor(resources.getColor(R.color.colorAccent))
        }
        generatedPrescriptionCb.setOnCheckedChangeListener { buttonView, isChecked ->
            generated_pdfs = isChecked
        }
        handWrittenNoteCb.setOnCheckedChangeListener { buttonView, isChecked ->
            written_notes = isChecked
        }
        sharedPrescriptionLayout.setOnClickListener {
            if (generatedPrescriptionCount > 0 && handwrittenNoteCount > 0) {
                if (generatedPrescriptionCb.isChecked || handWrittenNoteCb.isChecked) {
                    sharePrescriptionNote(dialog)
                } else {
                    Toast.makeText(
                        applicationContext,
                        "Please Select Data To Share",
                        Toast.LENGTH_LONG
                    ).show()
                }
            } else if (generatedPrescriptionCount == 0 && handwrittenNoteCount > 0) {
                if (handWrittenNoteCb.isChecked) {
                    sharePrescriptionNote(dialog)
                } else {
                    Toast.makeText(
                        applicationContext,
                        "Please Select Data To Share",
                        Toast.LENGTH_LONG
                    ).show()
                }
            } else if (generatedPrescriptionCount > 0 && handwrittenNoteCount == 0) {
                if (generatedPrescriptionCb.isChecked) {
                    sharePrescriptionNote(dialog)
                } else {
                    Toast.makeText(
                        applicationContext,
                        "Please Select Data To Share",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
        createNewButton.setOnClickListener {
            dialog.dismiss()
            savePrescriptionPopup()
        }
        dialogEvaluationCancel.setOnClickListener { dialog.dismiss() }
        dialog.show()
    }

    private fun createPrescriptionData(prescriptionObj: JSONObject, dialog: Dialog) {
        val loadingDialog = ProgressDialog(this)
        loadingDialog.setMessage(resources.getString(R.string.process_request))
        loadingDialog.setCancelable(false)
        loadingDialog.setInverseBackgroundForced(false)
        loadingDialog.show()
        emrConsultationNotesViewModel!!.createPrescriptionRecord(this, prescriptionObj)
            .observe(this, object : Observer<String?> {
                override fun onChanged(value: String?) {
                    Log.d("pdfUrl", "pdfUrl$value")
                    try {
                        val jsonObject = JSONObject(value)
                        if (jsonObject.getInt("status_code") == 200) {
                            val response = JSONObject(value).getJSONObject("response")
                            dialog.dismiss()
                            val createdPdfUrl = response.getJSONObject("response").getString("url")
                            getCreatedPrescriptionImage(createdPdfUrl)
                            loadingDialog.dismiss()
                            Toast.makeText(
                                this@EMRAddNotesActivity,
                                "Prescription created successfully", Toast.LENGTH_LONG
                            ).show()
                        } else {
                            loadingDialog.dismiss()
                            if (value != null) {
                                errorHandler(this@EMRAddNotesActivity, value)
                            }
                        }
                    } catch (e: JSONException) {
                        e.printStackTrace()
                        loadingDialog.dismiss()
                    }
                }
            })
    }

    private fun getCreatedPrescriptionImage(filrUrl: String) {
        val url = JSONObject()
        try {
            url.put("url", filrUrl)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        emrConsultationNotesViewModel!!.getPresignedUrl(this, url)
            .observe(this, object : Observer<String?> {
                override fun onChanged(value: String?) {
                    Log.d("pdfUrl", "pdfUrl$value")
                    try {
                        val jsonObject = JSONObject(value)
                        if (jsonObject.getInt("status_code") == 200) {
                            val response = JSONObject(value).getJSONObject("response")
                            val browserIntent = Intent(
                                Intent.ACTION_VIEW,
                                Uri.parse(response.getString("response"))
                            )
                            startActivity(browserIntent)
                        } else {
                            if (value != null) {
                                errorHandler(this@EMRAddNotesActivity, value)
                            }
                        }
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                }
            })
    }

    private fun sharePrescriptionData(prescriptionObj: JSONObject, dialog: Dialog) {
        val loadingDialog = ProgressDialog(this)
        loadingDialog.setMessage(resources.getString(R.string.process_request))
        loadingDialog.setCancelable(false)
        loadingDialog.setInverseBackgroundForced(false)
        loadingDialog.show()
        emrConsultationNotesViewModel!!.sharedPrescriptionRecord(this, prescriptionObj)
            .observe(this, object : Observer<String?> {
                override fun onChanged(value: String?) {
                    Log.d("shareRecordData", "shareRecordData$value")
                    try {
                        val jsonObject = JSONObject(value)
                        if (jsonObject.getInt("status_code") == 200) {
                            val response = JSONObject(value).getJSONObject("response")
                            Toast.makeText(
                                this@EMRAddNotesActivity,
                                response.getString("response"),
                                Toast.LENGTH_LONG
                            ).show()
                            dialog.dismiss()

                            loadingDialog.dismiss()
                            getInteractionDetails(patientId, encounterId)
                        } else {
                            loadingDialog.dismiss()
                            if (value != null) {
                                errorHandler(this@EMRAddNotesActivity, value)
                            }
                        }
                    } catch (e: JSONException) {
                        e.printStackTrace()
                        loadingDialog.dismiss()
                    }
                }
            })
    }

    private fun getPrescriptionNoteCountData() {
        val loadingDialog = ProgressDialog(this)
        loadingDialog.setMessage(resources.getString(R.string.process_request))
        loadingDialog.setCancelable(false)
        loadingDialog.setInverseBackgroundForced(false)
        loadingDialog.show()
        emrConsultationNotesViewModel!!.getPrescriptionNoteCountDetails(
            this,
            encounterId,
            patientId
        ).observe(this, object : Observer<String?> {
            override fun onChanged(value: String?) {
                Log.d("noteCount", "noteCount$value")
                try {
                    val jsonObject = JSONObject(value)
                    if (jsonObject.getInt("status_code") == 200) {
                        val response =
                            JSONObject(value).getJSONObject("response").getJSONObject("response")
                        val generatedPrescriptionCount = response.getInt("generated_prescriptions")
                        val handWrittenCount = response.getInt("handwritten_notes")
                        val totalPrescriptionCount = response.getInt("total_count")
                        loadingDialog.dismiss()
                        sharedPrescriptionPopup(
                            generatedPrescriptionCount,
                            handWrittenCount,
                            totalPrescriptionCount
                        )
                    } else {
                        loadingDialog.dismiss()
                        if (value != null) {
                            errorHandler(this@EMRAddNotesActivity, value)
                        }
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                    loadingDialog.dismiss()
                }
            }
        })
    }

    fun allPrescriptionPopup() {
        if (myClinicGlobalClass!!.isOnline) {
            allPrescriptionList!!.clear()
            prescriptionProgress!!.visibility = View.VISIBLE
            emrConsultationNotesViewModel!!.getPrescriptionDetails(
                this@EMRAddNotesActivity,
                encounterId,
                patientId
            ).observe(this@EMRAddNotesActivity, object : Observer<String?> {
                override fun onChanged(value: String?) {
                    prescriptionProgress!!.visibility = View.GONE
                    try {
                        val response = JSONObject(value)
                        if (response.getInt("status_code") == 200) {
                            val prescriptionArray =
                                response.getJSONObject("response").getJSONArray("response")
                            if (prescriptionArray.length() > 0) {
                                noCaseLayout!!.visibility = View.GONE
                                prescriptionLayout!!.visibility = View.VISIBLE
                                for (i in 0 until prescriptionArray.length()) {
                                    val prescriptionObject = prescriptionArray.getJSONObject(i)
                                    allPrescriptionList!!.add(
                                        EMRPrescriptionModel(
                                            prescriptionObject.getInt("id").toString(),
                                            prescriptionObject.getString("url"),
                                            prescriptionObject.getString("created_at")
                                        )
                                    )
                                }
                                allPrescriptionListAdapter!!.notifyDataSetChanged()
                            } else {
                                prescriptionLayout!!.visibility = View.GONE
                                noCaseLayout!!.visibility = View.VISIBLE
                            }
                        } else {
                            if (value != null) {
                                errorHandler(this@EMRAddNotesActivity, value)
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            })
        } else {
            myClinicGlobalClass!!.noInternetConnection.showDialog(this@EMRAddNotesActivity)
        }
        dialogEvaluationCancel!!.setOnClickListener { prescriptionDialog!!.dismiss() }
        prescriptionDialog!!.show()
    }

    fun sharePrescriptionNote(dialog: Dialog) {
        try {
            val selectedTreatmentPlanArray = JSONArray()
            val encounterArray = JSONArray()
            encounterArray.put(encounterId)
            val prescriptionObj = JSONObject()
            prescriptionObj.put("patient_id", patientId)
            prescriptionObj.put("doctor_id", ApiUrls.doctorId)
            prescriptionObj.put("encounters", encounterArray)
            prescriptionObj.put("generated_pdfs", generated_pdfs)
            prescriptionObj.put("written_notes", written_notes)
            prescriptionObj.put("evaluations", false)
            prescriptionObj.put("anyOneEvaluation", false)
            prescriptionObj.put("allSpecificEvaluation", 0)
            prescriptionObj.put("allSpecificTreatmentPlan", 0)
            prescriptionObj.put("selectedTreatmentPlans", selectedTreatmentPlanArray)
            prescriptionObj.put("symptoms", false)
            prescriptionObj.put("observations", false)
            prescriptionObj.put("investigation_results", false)
            prescriptionObj.put("diagnosis", false)
            prescriptionObj.put("treatment_plan", false)
            prescriptionObj.put("anyOneTreatmentPlan", false)
            sharePrescriptionData(prescriptionObj, dialog)
        } catch (e: Exception) {
        }
    }

    private fun savePrescriptionNotePopup() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setContentView(R.layout.custom_popup_save_prescription)
        dialog.setCancelable(false)
        val rl_parent = dialog.findViewById<RelativeLayout>(R.id.rl_parent)
        val tv_title = dialog.findViewById<TextView>(R.id.tv_title)
        val tv_body = dialog.findViewById<TextView>(R.id.tv_body)
        val btn_close = dialog.findViewById<Button>(R.id.btn_close)
        val btn_Delete = dialog.findViewById<Button>(R.id.btn_remove)
        val img = ContextCompat.getDrawable(this, R.drawable.cross_lines)
        img!!.setBounds(0, 0, 60, 60)
        btn_close.setCompoundDrawables(img, null, null, null)
        btn_Delete.text = "Save"
        tv_body.visibility = View.VISIBLE
        tv_body.text =
            "Note: Once saved, you will not able to edit and delete the records in the prescriptions."
        tv_title.text = "Save Prescription"
        btn_close.setOnClickListener { v: View? -> dialog.dismiss() }
        btn_Delete.setOnClickListener { v: View? ->
            dialog.dismiss()
            savePrescriptionPopup()
        }
        dialog.show()
    }
}