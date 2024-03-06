package com.whitecoats.clinicplus.fragments

import android.app.*
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Paint
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.clans.fab.FloatingActionButton
import com.github.clans.fab.FloatingActionMenu
import com.whitecoats.clinicplus.AppUtilities
import com.whitecoats.clinicplus.MyClinicGlobalClass
import com.whitecoats.clinicplus.R
import com.whitecoats.clinicplus.activities.EMRActivity
import com.whitecoats.clinicplus.activities.EMRAddNotesActivity
import com.whitecoats.clinicplus.activities.EMRAllCasesActivity
import com.whitecoats.clinicplus.adapters.EMRConsultCaseHistoryListAdapter
import com.whitecoats.clinicplus.apis.ApiUrls
import com.whitecoats.clinicplus.constants.AppConstants
import com.whitecoats.clinicplus.models.EMRAllEpisodeModel
import com.whitecoats.clinicplus.models.EMRConsultCaseHistoryModel
import com.whitecoats.clinicplus.models.ShowEditAndDeleteButtons
import com.whitecoats.clinicplus.utils.ErrorHandlerClass.errorHandler
import com.whitecoats.clinicplus.viewmodels.EMRConsultationNotesViewModel
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.util.*

/**
 * A simple [Fragment] subclass.
 * Use the [EMRConsultationNotesFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class EMRConsultationNotesFragment : Fragment() {
    private var historyDetailsArrowUpDown: ImageView? = null
    private var viewAllCasesText: TextView? = null
    private var fullHistoryDetailsLayout: RelativeLayout? = null
    private var halfHistoryDetailsLayout: RelativeLayout? = null
    private var emrConsultationNotesViewModel: EMRConsultationNotesViewModel? = null
    private var consultCaseHistoryRecycleView: RecyclerView? = null
    var EMRConsultCaseHistoryModelList: MutableList<EMRConsultCaseHistoryModel>? = null
    private var editAndDeleteButtons: MutableList<ShowEditAndDeleteButtons>? = null
    private var emrConsultCaseHistoryListAdapter: EMRConsultCaseHistoryListAdapter? = null
    private var mLayoutManager: RecyclerView.LayoutManager? = null
    private var groupData: ArrayList<Int>? = null
    private var c = 0
    private var totalCasesText: TextView? = null
    private var caseNameText: TextView? = null
    private var encounterDropDownAdapter: ArrayAdapter<*>? = null
    private var allInteractionSpinner: Spinner? = null
    private var lastInteractionID = 0
    private var appUtilities: AppUtilities? = null
    private var caseSwitchIcon: ImageView? = null
    private var ii = 0
    private var recordCount = 0
    private var allInteraction = 0
    private var emrCaseHistoryProgressbar: RelativeLayout? = null
    private var consultNoteFragmentLayout: RelativeLayout? = null
    private var noCaseCreated: RelativeLayout? = null
    private var noInteractionTextLayout: RelativeLayout? = null
    private var recordLayout: NestedScrollView? = null
    private var fam: FloatingActionMenu? = null
    private var newCasefb: FloatingActionButton? = null
    private var newInteractionfb: FloatingActionButton? = null
    private var howItWorksLayout: ScrollView? = null
    private var proceedNotes: LinearLayout? = null
    private var createNewCaseLayout: ScrollView? = null
    private var dontShowAgain: CheckBox? = null
    private var caseName: EditText? = null
    private var interactionMode: Spinner? = null
    private var caseDate: TextView? = null
    private var caseTime: TextView? = null
    private var sharedPreferences: SharedPreferences? = null
    private var editor: SharedPreferences.Editor? = null
    private var apptDate: String? = null
    private var apptTime: String? = null
    private var apptMode = 0
    private var interactionModeAdapter: ArrayAdapter<*>? = null
    private var interactionModeData: String? = null
    private var calendar: Calendar? = null
    private var cyear = 0
    private var cmonth = 0
    private var cday = 0
    private var hours = 0
    private var mins = 0
    private var newCaseButton: Button? = null
    private var myClinicGlobalClass: MyClinicGlobalClass? = null
    private var progressDialog: ProgressDialog? = null
    private var newCaseHeading: TextView? = null
    private var createDialog: Dialog? = null
    private var createHeading: TextView? = null
    private var caseDatePopup: TextView? = null
    private var caseTImePopup: TextView? = null
    private var closePopup: ImageView? = null
    private var caseNamePopup: EditText? = null
    private var interactionModePopup: Spinner? = null
    private var createCasePopup: Button? = null
    private var createInteractionPopup: Button? = null
    private var patientName: String? = null

    // TODO: Rename and change types of parameters
    private var patientId = 0
    private var appointmentId = 0

    /*ENGG-3691 -- Refactoring the code by removing the deprecate functions(StartActivityForResults)*/
    private var launcherEMRAllCaeseResults: ActivityResultLauncher<Intent>? = null
    private var selectedTime: String? = null
    private var selected_time: String? = null
    private var patientNameTop: TextView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            patientId = requireArguments().getInt(ARG_PARAM1)
            appointmentId = requireArguments().getInt(ARG_PARAM2)
            apptDate = requireArguments().getString(ARG_PARAM3)
            apptTime = requireArguments().getString(ARG_PARAM4)
            apptMode = requireArguments().getInt(ARG_PARAM5)
            patientName = requireArguments().getString(ARG_PARAM6)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val emrConsultNoteFragmentTabView =
            inflater.inflate(R.layout.fragment_e_m_r_consultation_notes, container, false)
        emrConsultationNotesViewModel = ViewModelProvider(this).get(
            EMRConsultationNotesViewModel::class.java
        )
        emrConsultationNotesViewModel!!.init()
        caseSelectedPosition = 0
        historyDetailsArrowUpDown =
            emrConsultNoteFragmentTabView.findViewById(R.id.historyDetailsArrowUpDown)
        viewAllCasesText = emrConsultNoteFragmentTabView.findViewById(R.id.viewAllCasesText)
        viewAllCasesText!!.paintFlags = viewAllCasesText!!.paintFlags or Paint.UNDERLINE_TEXT_FLAG
        fullHistoryDetailsLayout =
            emrConsultNoteFragmentTabView.findViewById(R.id.fullHistoryDetailsLayout)
        halfHistoryDetailsLayout =
            emrConsultNoteFragmentTabView.findViewById(R.id.halfHistoryDetailsLayout)
        mLayoutManager = LinearLayoutManager(requireActivity().applicationContext)
        consultCaseHistoryRecycleView =
            emrConsultNoteFragmentTabView.findViewById(R.id.consultCaseHistoryRecycleView)
        totalCasesText = emrConsultNoteFragmentTabView.findViewById(R.id.totalCasesText)
        caseNameText = emrConsultNoteFragmentTabView.findViewById(R.id.caseNameText)
        allInteractionSpinner =
            emrConsultNoteFragmentTabView.findViewById(R.id.allInteractionSpinner)
        caseSwitchIcon = emrConsultNoteFragmentTabView.findViewById(R.id.caseSwitchIcon)
        emrCaseHistoryProgressbar =
            emrConsultNoteFragmentTabView.findViewById(R.id.emrCaseHistoryProgressbar)
        consultNoteFragmentLayout =
            emrConsultNoteFragmentTabView.findViewById(R.id.consultNoteFragmentLayout)
        noCaseCreated = emrConsultNoteFragmentTabView.findViewById(R.id.noCaseCreated)
        noInteractionTextLayout =
            emrConsultNoteFragmentTabView.findViewById(R.id.noInteractionTextLayout)
        recordLayout = emrConsultNoteFragmentTabView.findViewById(R.id.recordLayout)
        patientNameTop = emrConsultNoteFragmentTabView.findViewById(R.id.patientNameTop)
        patientNameTop!!.text = patientName
        newCasefb =
            emrConsultNoteFragmentTabView.findViewById<View>(R.id.newCase) as FloatingActionButton
        newInteractionfb =
            emrConsultNoteFragmentTabView.findViewById<View>(R.id.newInteraction) as FloatingActionButton
        fam = emrConsultNoteFragmentTabView.findViewById<View>(R.id.fab_menu) as FloatingActionMenu
        howItWorksLayout = emrConsultNoteFragmentTabView.findViewById(R.id.how_it_works_notes)
        proceedNotes = emrConsultNoteFragmentTabView.findViewById(R.id.proceed_notes)
        createNewCaseLayout =
            emrConsultNoteFragmentTabView.findViewById(R.id.create_new_case_layout)
        dontShowAgain = emrConsultNoteFragmentTabView.findViewById(R.id.dont_show_again)
        caseName = emrConsultNoteFragmentTabView.findViewById(R.id.case_name)
        caseDate = emrConsultNoteFragmentTabView.findViewById(R.id.case_date)
        caseTime = emrConsultNoteFragmentTabView.findViewById(R.id.case_time)
        interactionMode = emrConsultNoteFragmentTabView.findViewById(R.id.interaction_mode)
        newCaseButton = emrConsultNoteFragmentTabView.findViewById(R.id.create_new_case_button)
        newCaseHeading = emrConsultNoteFragmentTabView.findViewById(R.id.new_case_heading)
        myClinicGlobalClass = requireActivity().applicationContext as MyClinicGlobalClass
        progressDialog = ProgressDialog(context)
        progressDialog!!.setMessage("Creating new case please wait...")
        EMRConsultCaseHistoryModelList = ArrayList()
        editAndDeleteButtons = ArrayList()
        groupData = ArrayList()
        emrAllEpisodeList = ArrayList()
        encounterDropDownList = ArrayList()
        appUtilities = AppUtilities()
        sharedPreferences =
            requireContext().getSharedPreferences(AppConstants.appSharedPref, Context.MODE_PRIVATE)
        /*ENGG-3691 -- Refactoring the code by removing the deprecate functions(StartActivityForResults)*/
//Start
        launcherEMRAllCaeseResults = registerForActivityResult<Intent, ActivityResult>(
            ActivityResultContracts.StartActivityForResult()
        ) { result: ActivityResult ->
            //Request code 111
            val data = result.data
            val resultCode = result.resultCode
            if (resultCode == Activity.RESULT_OK) {
                try {
                    caseNameText!!.text = emrAllEpisodeList!!.get(caseSelectedPosition).episodeName
                    if (switchCaseClicked == 1) {
                        if (switchCaseClicked == 2) {
                            getDropDownEncounterList(
                                (emrAllEpisodeList as ArrayList<EMRAllEpisodeModel>).get(caseSelectedPosition).patientId,
                                (emrAllEpisodeList as ArrayList<EMRAllEpisodeModel>).get(
                                    caseSelectedPosition
                                ).episodeId
                            )
                        }
                    } else {
                        getDropDownEncounterList(
                            (emrAllEpisodeList as ArrayList<EMRAllEpisodeModel>).get(caseSelectedPosition).patientId,
                            (emrAllEpisodeList as ArrayList<EMRAllEpisodeModel>).get(
                                caseSelectedPosition
                            ).episodeId
                        )
                    }
                } catch (E: Exception) {
                    E.printStackTrace()
                }
            }
        }

//End
        interactionModeAdapter = ArrayAdapter<Any?>(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            AppConstants.interactionDetails
        )
        (interactionModeAdapter as ArrayAdapter<Any?>).setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        interactionMode!!.adapter = interactionModeAdapter
        if (apptMode == 1) {
            interactionMode!!.setSelection(2)
        } else if (apptMode == 3) {
            interactionMode!!.setSelection(1)
        } else {
            interactionMode!!.setSelection(0)
        }
        createDialog = Dialog(requireActivity())
        createDialog!!.setCancelable(false)
        createDialog!!.setContentView(R.layout.create_new_case_layout)
        createHeading = createDialog!!.findViewById(R.id.new_case_heading_popup)
        closePopup = createDialog!!.findViewById(R.id.close_popup)
        caseNamePopup = createDialog!!.findViewById(R.id.case_name_popup)
        interactionModePopup = createDialog!!.findViewById(R.id.interaction_mode_popup)
        caseDatePopup = createDialog!!.findViewById(R.id.case_date_popup)
        caseTImePopup = createDialog!!.findViewById(R.id.case_time_popup)
        createCasePopup = createDialog!!.findViewById(R.id.create_new_case_button_popup)
        createInteractionPopup = createDialog!!.findViewById(R.id.create_new_case_interaction_popup)
        closePopup!!.setOnClickListener(View.OnClickListener { createDialog!!.dismiss() })
        interactionModePopup!!.adapter = interactionModeAdapter
        interactionModePopup!!.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>?, view: View, i: Int, l: Long) {
                interactionModeData = AppConstants.interactionDetails[i]
            }

            override fun onNothingSelected(adapterView: AdapterView<*>?) {}
        }
        createCasePopup!!.setOnClickListener(View.OnClickListener {
            val casename = caseNamePopup!!.text.toString().trim { it <= ' ' }
            val dateTime = appUtilities!!.changeDateFormat(
                "dd MMM, yyyy",
                "dd-MM-yyyy",
                caseDatePopup!!.text.toString()
                    .trim { it <= ' ' }) + " " + appUtilities!!.changeDateFormat(
                "HH:mm",
                "HH:mm:ss",
                selectedTime
            )
            if (TextUtils.isEmpty(casename)) {
                Toast.makeText(context, "Enter a valid case name", Toast.LENGTH_LONG).show()
            } else if (interactionModeData == "Interaction Mode") {
                Toast.makeText(context, "Select a valid interaction", Toast.LENGTH_LONG).show()
            } else {
                if (myClinicGlobalClass!!.isOnline) {
                    //emrCaseHistoryProgressbar.setVisibility(View.VISIBLE);
                    //Toast.makeText(getContext(), AppConfigClass.doctorId+" ",Toast.LENGTH_LONG).show();
                    progressDialog!!.show()
                    saveCase(
                        casename,
                        ApiUrls.doctorId,
                        patientId,
                        appointmentId,
                        interactionModeData,
                        selectedTime
                    )
                } else {
                    myClinicGlobalClass!!.noInternetConnection.showDialog(activity)
                }
            }
        })
        //Log.i("appointmnet mode","appintmnet mode is "+apptMode);
        interactionMode!!.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>?, view: View, i: Int, l: Long) {
                interactionModeData = AppConstants.interactionDetails[i]
            }

            override fun onNothingSelected(adapterView: AdapterView<*>?) {}
        }
        dontShowAgain!!.setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener { compoundButton, b ->
            if (b) {
                editor = sharedPreferences!!.edit()
                editor!!.putBoolean("dontShowCreateNotesWorks", true)
                editor!!.apply()
            }
        })
        proceedNotes!!.setOnClickListener(View.OnClickListener {
            howItWorksLayout!!.visibility = View.GONE
            createNewCaseLayout!!.visibility = View.VISIBLE
        })
        calendar = Calendar.getInstance()
        cyear = calendar!!.get(Calendar.YEAR)
        cmonth = calendar!!.get(Calendar.MONTH)
        cday = calendar!!.get(Calendar.DAY_OF_MONTH)
        hours = calendar!!.get(Calendar.HOUR_OF_DAY)
        mins = calendar!!.get(Calendar.MINUTE)
        caseTImePopup!!.setOnClickListener(View.OnClickListener {
            val timePickerDialog =
                TimePickerDialog(context, { timePicker, selectedHour, selectedMinute ->
                    val h: String
                    val m: String
                    h = if (selectedHour < 10) {
                        "0$selectedHour"
                    } else {
                        selectedHour.toString()
                    }
                    m = if (selectedMinute < 10) {
                        "0$selectedMinute"
                    } else {
                        selectedMinute.toString()
                    }
                    caseTImePopup!!.setText(
                        appUtils!!.formatTimeBasedOnPreference(
                            context, "HH:mm", "$h:$m"
                        )
                    )
                    selectedTime = "$h:$m"
                }, hours, mins, true)
            timePickerDialog.show()
        })
        caseTime!!.setOnClickListener(View.OnClickListener {
            val mTimePicker =
                TimePickerDialog(context, { timePicker, selectedHour, selectedMinute ->
                    val h: String
                    val m: String
                    h = if (selectedHour < 10) {
                        "0$selectedHour"
                    } else {
                        selectedHour.toString()
                    }
                    m = if (selectedMinute < 10) {
                        "0$selectedMinute"
                    } else {
                        selectedMinute.toString()
                    }
                    //caseTime.setText(h + ":" + m);
                    caseTime!!.setText(
                        appUtils!!.formatTimeBasedOnPreference(
                            context, "HH:mm", "$h:$m"
                        )
                    )
                    selected_time = "$h:$m"
                }, hours, mins, true) //Yes 24 hour time
            mTimePicker.show()
        })
        caseDatePopup!!.setOnClickListener(View.OnClickListener {
            val datePickerDialog = DatePickerDialog(requireContext(), { view, year, month, dayOfMonth ->
                val d: String
                val m: String
                d = if (dayOfMonth < 10) {
                    "0$dayOfMonth"
                } else {
                    dayOfMonth.toString()
                }
                m = if (month < 10) {
                    "0" + (month + 1)
                } else {
                    (month + 1).toString()
                }
                caseDatePopup!!.setText(
                    appUtilities!!.changeDateFormat(
                        "dd-MM-yyyy",
                        "dd MMM, yyyy",
                        "$d-$m-$year"
                    )
                )
            }, cyear, cmonth, cday)
            datePickerDialog.show()
        })
        caseDate!!.setOnClickListener(View.OnClickListener {
            val datePickerDialog = DatePickerDialog(requireContext(), { view, year, month, dayOfMonth ->
                val d: String
                val m: String
                d = if (dayOfMonth < 10) {
                    "0$dayOfMonth"
                } else {
                    dayOfMonth.toString()
                }
                m = if (month < 10) {
                    "0" + (month + 1)
                } else {
                    (month + 1).toString()
                }
                apptDate = "$d-$m-$year"
                caseDate!!.setText(
                    appUtilities!!.changeDateFormat(
                        "dd-MM-yyyy",
                        "dd MMM, yyyy",
                        apptDate
                    )
                )
            }, cyear, cmonth, cday)
            datePickerDialog.show()
        })
        newCaseButton!!.setOnClickListener(View.OnClickListener {
            val casename = caseName!!.getText().toString().trim { it <= ' ' }
            val dateTime = appUtilities!!.changeDateFormat(
                "dd MMM, yyyy",
                "dd-MM-yyyy",
                caseDate!!.getText().toString()
                    .trim { it <= ' ' }) + " " + appUtilities!!.changeDateFormat(
                "HH:mm",
                "HH:mm:ss",
                selected_time
            )
            if (TextUtils.isEmpty(casename)) {
                Toast.makeText(context, "Enter a valid case name", Toast.LENGTH_LONG).show()
            } else if (interactionModeData == "Interaction Mode") {
                Toast.makeText(context, "Select a valid interaction", Toast.LENGTH_LONG).show()
            } else {
                if (myClinicGlobalClass!!.isOnline) {
                    //emrCaseHistoryProgressbar.setVisibility(View.VISIBLE);
                    //Toast.makeText(getContext(), AppConfigClass.doctorId+" ",Toast.LENGTH_LONG).show();
                    progressDialog!!.show()
                    saveCase(
                        casename,
                        ApiUrls.doctorId,
                        patientId,
                        appointmentId,
                        interactionModeData,
                        dateTime
                    )
                } else {
                    myClinicGlobalClass!!.noInternetConnection.showDialog(activity)
                }
            }
        })
        createInteractionPopup!!.setOnClickListener(View.OnClickListener {
            val caseNamedata = caseNamePopup!!.getText().toString().trim { it <= ' ' }
            val dateTime = appUtilities!!.changeDateFormat(
                "dd MMM, yyyy",
                "dd-MM-yyyy",
                caseDatePopup!!.getText().toString()
                    .trim { it <= ' ' }) + " " + appUtilities!!.changeDateFormat(
                "HH:mm",
                "HH:mm:ss",
                selectedTime
            )
            if (interactionModeData == "Interaction Mode") {
                Toast.makeText(context, "Select a valid interaction", Toast.LENGTH_LONG).show()
            } else {
                if (myClinicGlobalClass!!.isOnline) {
                    progressDialog!!.setMessage("Creating new interaction Please wait...")
                    progressDialog!!.show()
                    emrConsultationNotesViewModel!!.saveNewEncounter(
                        activity,
                        appointmentId,
                        0,
                        dateTime,
                        interactionModeData,
                        (emrAllEpisodeList as ArrayList<EMRAllEpisodeModel>).get(
                            caseSelectedPosition
                        ).episodeId,
                        patientId.toString()
                    ).observe(
                        viewLifecycleOwner, object : Observer<String?> {
                            override fun onChanged(value: String?) {
                                progressDialog!!.dismiss()
                                try {
                                    val response = JSONObject(value)
                                    if (response.getInt("status_code") == 200) {
                                        val responseObj = response.getJSONObject("response")
                                            .getJSONObject("response")
                                        if (responseObj.has("id")) {
                                            caseNamePopup!!.setText("")
                                            interactionModePopup!!.setSelection(0)
                                            val myIntent =
                                                Intent(activity, EMRAddNotesActivity::class.java)
                                            myIntent.putExtra("patientId", patientId)
                                            myIntent.putExtra(
                                                "episodeId", (emrAllEpisodeList as ArrayList<EMRAllEpisodeModel>).get(
                                                    caseSelectedPosition
                                                ).episodeId
                                            )
                                            myIntent.putExtra(
                                                "encounterId",
                                                responseObj.getInt("id")
                                            )
                                            myIntent.putExtra("encounterName", interactionModeData)
                                            myIntent.putExtra(
                                                "encounterCreatedOn",
                                                responseObj.getString("created_at")
                                            )
                                            myIntent.putExtra("caseName", caseNamedata)
                                            myIntent.putExtra("patientName", patientName)
                                            createDialog!!.dismiss()
                                            startActivity(myIntent)
                                        }
                                    } else {
                                        if (value != null) {
                                            errorHandler(requireContext(), value)
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
        })


//        dummyCaseHistoryData();

        //handling menu status (open or close)
        fam!!.setOnMenuToggleListener { opened ->
            if (opened) {
//                    showToast("Menu is opened");
            } else {
//                    showToast("Menu is closed");
            }
        }

        //handling each floating action button clicked
        newCasefb!!.setOnClickListener(onButtonClick())
        newInteractionfb!!.setOnClickListener(onButtonClick())
        //        fam.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (fam.isOpened()) {
//                    fam.close(true);
//                }
//            }
//        });
        emrConsultCaseHistoryListAdapter = activity?.let {
            patientName?.let { it1 ->
                EMRConsultCaseHistoryListAdapter(
                    it,
                    EMRConsultCaseHistoryModelList as ArrayList<EMRConsultCaseHistoryModel>,
                    requireActivity(), groupData!!,
                    editAndDeleteButtons as ArrayList<ShowEditAndDeleteButtons>, it1
                ) { v, itemPosition, encounterID, itemClick, encounterName, encounterCreatedOn ->
                    if (itemClick.equals("ADD_RECORDS", ignoreCase = true)) {
                        val myIntent = Intent(activity, EMRAddNotesActivity::class.java)
                        myIntent.putExtra("patientId", patientId)
                        myIntent.putExtra(
                            "episodeId",
                            (emrAllEpisodeList as ArrayList<EMRAllEpisodeModel>).get(caseSelectedPosition).episodeId
                        )
                        myIntent.putExtra("encounterId", encounterID)
                        myIntent.putExtra("encounterName", encounterName)
                        myIntent.putExtra("encounterCreatedOn", encounterCreatedOn)
                        myIntent.putExtra(
                            "caseName",
                            (emrAllEpisodeList as ArrayList<EMRAllEpisodeModel>).get(caseSelectedPosition).episodeName
                        )
                        myIntent.putExtra("patientName", patientName)
                        startActivity(myIntent)
                    }
                }
            }
        }
        consultCaseHistoryRecycleView?.layoutManager = mLayoutManager
        consultCaseHistoryRecycleView?.itemAnimator = DefaultItemAnimator()
        consultCaseHistoryRecycleView?.adapter = emrConsultCaseHistoryListAdapter
        fullHistoryDetailsLayout?.visibility = View.VISIBLE
        historyDetailsArrowUpDown?.setImageDrawable(requireActivity().getDrawable(R.drawable.ic_arrow_up))
        recordLayout?.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
            if (scrollY > oldScrollY) {
                historyDetailsArrowUpDown?.setImageDrawable(requireActivity().getDrawable(R.drawable.ic_arrow_down))
                fullHistoryDetailsLayout?.visibility = View.GONE
            }
            if (scrollY == 0) {
                fullHistoryDetailsLayout?.visibility = View.VISIBLE
                historyDetailsArrowUpDown?.setImageDrawable(requireActivity().getDrawable(R.drawable.ic_arrow_up))
            }
        })
        historyDetailsArrowUpDown?.setOnClickListener(View.OnClickListener {
            if (fullHistoryDetailsLayout?.visibility == View.VISIBLE) {
                historyDetailsArrowUpDown?.setImageDrawable(requireActivity().getDrawable(R.drawable.ic_arrow_down))
                fullHistoryDetailsLayout?.visibility = View.GONE
            } else {
                fullHistoryDetailsLayout?.visibility = View.VISIBLE
                historyDetailsArrowUpDown?.setImageDrawable(requireActivity().getDrawable(R.drawable.ic_arrow_up))
            }
        })
        viewAllCasesText?.setOnClickListener(View.OnClickListener { view: View? ->
            switchCaseClicked = 1
            val i = Intent(activity, EMRAllCasesActivity::class.java)
            i.putExtra("patientId", patientId)
            launcherEMRAllCaeseResults!!.launch(i)
        })
        caseSwitchIcon?.setOnClickListener(View.OnClickListener { view: View? ->
            switchCaseClicked = 1
            val i = Intent(activity, EMRAllCasesActivity::class.java)
            i.putExtra("patientId", patientId)
            launcherEMRAllCaeseResults!!.launch(i)
        })
        encounterDropDownAdapter = ArrayAdapter(
            requireActivity(),
            android.R.layout.simple_spinner_dropdown_item,
            encounterDropDownList as ArrayList<EMRAllEpisodeModel>
        )
        (encounterDropDownAdapter as ArrayAdapter<EMRAllEpisodeModel>).setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        allInteractionSpinner?.adapter = encounterDropDownAdapter
        allInteractionSpinner?.setSelection(EMRActivity.interactionSelectedPosition)
        allInteractionSpinner?.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View,
                position: Int,
                id: Long
            ) {
                EMRActivity.interactionSelectedPosition = position
                if (position == 0) {
                    allInteraction = 0
                    emrCaseHistoryProgressbar?.visibility = View.VISIBLE
                    emrAllEpisodeList?.get(
                        caseSelectedPosition
                    )?.let {
                        getAllComponentForCaseHistory(
                            emrAllEpisodeList?.get(caseSelectedPosition)!!.patientId,
                            it.episodeId,
                            allInteraction
                        )
                    }
                } else {
                    allInteraction = 1
                    emrCaseHistoryProgressbar?.visibility = View.VISIBLE
                    lastInteractionID = (encounterDropDownList as ArrayList<EMRAllEpisodeModel>).get(position).lastInteractionId
                    getAllComponentForCaseHistory(
                        (emrAllEpisodeList as ArrayList<EMRAllEpisodeModel>).get(caseSelectedPosition).patientId,
                        lastInteractionID,
                        allInteraction
                    )
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        //  getSimboAuthKey();
        return emrConsultNoteFragmentTabView
    }

    private fun onButtonClick(): View.OnClickListener {
        return View.OnClickListener { view ->
            calendar = Calendar.getInstance()
            //Toast.makeText(getActivity(),calendar.get(Calendar.YEAR)+"-"+(calendar.get(Calendar.MONTH)+1)+"-"+calendar.get(Calendar.DAY_OF_MONTH),Toast.LENGTH_LONG).show();
            if (view === newCasefb) {
                createHeading!!.setText(R.string.create_new_case)
                createInteractionPopup!!.visibility = View.GONE
                createCasePopup!!.visibility = View.VISIBLE
                //caseNamePopup.setEnabled(true);
                caseNamePopup!!.requestFocus()
                caseNamePopup!!.isFocusable = true
                //caseNamePopup.setEnabled(true);
                caseNamePopup!!.isFocusableInTouchMode = true
                caseNamePopup!!.setText("")
                caseDatePopup!!.text = appUtilities!!.changeDateFormat(
                    "yyyy-MM-dd", "dd MMM, yyyy", calendar?.get(
                        Calendar.YEAR
                    ).toString() + "-" + (calendar?.get(Calendar.MONTH)?.plus(1)) + "-" + calendar?.get(
                        Calendar.DAY_OF_MONTH
                    )
                )
                caseTImePopup!!.text = appUtils!!.formatTimeBasedOnPreference(
                    context,
                    "HH:mm",
                    calendar?.get(Calendar.HOUR_OF_DAY).toString() + ":" + calendar?.get(
                        Calendar.MINUTE
                    )
                )
                selectedTime = calendar?.get(Calendar.HOUR_OF_DAY).toString() + ":" + calendar?.get(
                    Calendar.MINUTE
                )
                createDialog!!.show()
            } else {
                createHeading!!.setText(R.string.create_new_interaction)
                createInteractionPopup!!.visibility = View.VISIBLE
                createCasePopup!!.visibility = View.GONE
                caseNamePopup!!.setText(caseNameText!!.text.toString().trim { it <= ' ' })
                //caseNamePopup.setEnabled(false);
                caseNamePopup!!.clearFocus()
                caseNamePopup!!.isFocusable = false
                caseDatePopup!!.text = appUtilities!!.changeDateFormat(
                    "yyyy-MM-dd", "dd MMM, yyyy", calendar?.get(
                        Calendar.YEAR
                    ).toString() + "-" + (calendar?.get(Calendar.MONTH)?.plus(1)) + "-" + calendar?.get(
                        Calendar.DAY_OF_MONTH
                    )
                )
                caseTImePopup!!.text = appUtils!!.formatTimeBasedOnPreference(
                    context,
                    "HH:mm",
                    calendar?.get(Calendar.HOUR_OF_DAY).toString() + ":" + calendar?.get(
                        Calendar.MINUTE
                    )
                )
                selectedTime = calendar?.get(Calendar.HOUR_OF_DAY).toString() + ":" + calendar?.get(
                    Calendar.MINUTE
                )
                createDialog!!.show()
            }
            fam!!.close(true)
        }
    }

    fun newCasePopup() {
        val dialog = Dialog(requireContext())
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.create_new_case_layout)
    }

    fun dummyCaseHistoryData() {
        val iLength = 2
        for (j in 0..1) {
            if (j == 0) {
                for (i in 0 until iLength) {
                    groupData!!.add(c, i)
                    c++
                    val model = EMRConsultCaseHistoryModel()
                    model.categoryName = "Temprature"
                    //                    model.setGroupNo(0);
                    if (i == iLength - 1) {
                        model.enableSeparatorLine = 1
                    } else {
                        model.enableSeparatorLine = 0
                    }
                    EMRConsultCaseHistoryModelList!!.add(model)
                }
            } else {
                for (i in 0..0) {
                    groupData!!.add(c, i)
                    c++
                    val model = EMRConsultCaseHistoryModel()
                    model.categoryName = "Temprature"
                    //                    model.setGroupNo(0);
                    if (i == 1 - 1) {
                        model.enableSeparatorLine = 1
                    } else {
                        model.enableSeparatorLine = 0
                    }
                    EMRConsultCaseHistoryModelList!!.add(model)
                }
            }
        }
        //        model.setPatientName(orderUserArray.getString("fname"));
//        model.setPatientId(orderUserArray.getInt("id"));
//        model.setApptTime(tempobj.getString("appt_start_time"));
//        model.setGroupNo(j);
//        model.setApptDate(appointmentJsonObject.getString("date"));

//        model = new EMRConsultCaseHistoryModel();
//        model.setCategoryName("Blood Pres5");
//        model.setGroupNo(0);

//        EMRConsultCaseHistoryModelList.add(model);
    }

    fun saveCase(
        newCaseName: String?,
        doctorId: Int,
        patId: Int,
        apptId: Int,
        interactionModeString: String?,
        caseDateTime: String?
    ) {
        emrConsultationNotesViewModel!!.saveNewEpisode(activity, doctorId, newCaseName, patId)
            .observe(
                viewLifecycleOwner, object : Observer<String?> {
                    override fun onChanged(value: String?) {
                        //emrCaseHistoryProgressbar.setVisibility(View.GONE);
                        progressDialog!!.dismiss()
                        try {
                            val response = JSONObject(value)
                            if (response.getInt("status_code") == 200) {
                                val episodeId =
                                    response.getJSONObject("response").getInt("response")
                                emrConsultationNotesViewModel!!.saveNewEncounter(
                                    activity,
                                    apptId,
                                    0,
                                    caseDateTime,
                                    interactionModeString,
                                    episodeId,
                                    patId.toString()
                                ).observe(
                                    viewLifecycleOwner, object : Observer<String?> {
                                        override fun onChanged(value: String?) {
                                            try {
                                                val response = JSONObject(value)
                                                if (response.getInt("status_code") == 200) {
                                                    val resObject =
                                                        response.getJSONObject("response")
                                                            .getJSONObject("response")
                                                    getDoctorEpisodes(patId)
                                                } else {
                                                    if (value != null) {
                                                        errorHandler(requireContext(), value)
                                                    }
                                                }
                                            } catch (e: Exception) {
                                                e.printStackTrace()
                                            }
                                        }
                                    })
                            } else {
                                if (value != null) {
                                    errorHandler(requireContext(), value)
                                }
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                })
    }

    override fun onResume() {
        super.onResume()
        if (fullHistoryDetailsLayout!!.visibility == View.GONE) {
            fullHistoryDetailsLayout!!.visibility = View.VISIBLE
            historyDetailsArrowUpDown!!.setImageDrawable(requireActivity().getDrawable(R.drawable.ic_arrow_up))
        }
        if (switchCaseClicked == 1) {
            if (switchCaseClicked == 2) {
                switchCaseClicked = 0
                getDoctorEpisodes(patientId)
            } else {
                switchCaseClicked = 0
            }
        } else {
            getDoctorEpisodes(patientId)
        }
    }

    fun getDoctorEpisodes(patientId: Int) {
        //get doctor episodes
        emrConsultationNotesViewModel!!.getDoctorEpisodeDetails(activity, patientId)
            .observe(requireActivity(), object : Observer<String?> {
                override fun onChanged(value: String?) {
                    try {
                        if (createDialog!!.isShowing) {
                            interactionModePopup!!.setSelection(0)
                            createDialog!!.dismiss()
                        }
                        emrAllEpisodeList!!.clear()
                        val jsonObject = JSONObject(value)
                        if (jsonObject.getInt("status_code") == 200) {
                            val response = JSONObject(value).getJSONObject("response")
                            Log.i("checkfordoctorepisode", response.toString())
                            //                        JSONObject rootObj = response.optJSONObject("response");
                            val doctorEpisodeArray = response.getJSONArray("response")
                            val doctorEpisodeLength = doctorEpisodeArray.length()
                            if (doctorEpisodeLength > 0) {
                                consultNoteFragmentLayout!!.visibility = View.VISIBLE
                                noCaseCreated!!.visibility = View.GONE
                                noInteractionTextLayout!!.visibility = View.GONE
                                howItWorksLayout!!.visibility = View.GONE
                                createNewCaseLayout!!.visibility = View.GONE
                                for (i in 0 until doctorEpisodeLength) {
                                    val episodeObject = doctorEpisodeArray.getJSONObject(i)

//                                JSONObject lastInteractionOnObject = episodeObject.getJSONObject("last_encountered_on");
                                    val diagnosisArray = episodeObject.getJSONArray("diagnosis")
                                    val allEpisodeModel = EMRAllEpisodeModel()
                                    allEpisodeModel.episodeName =
                                        episodeObject.getString("episode_name")
                                    allEpisodeModel.status = episodeObject.getInt("status")
                                    allEpisodeModel.createdOn =
                                        episodeObject.getString("created_at")
                                    allEpisodeModel.numberOfInteraction =
                                        episodeObject.getInt("encounterCount")
                                    if (!episodeObject.isNull("last_encountered_on")) {
                                        val lastInteractionOnObject =
                                            episodeObject.getJSONObject("last_encountered_on")
                                        allEpisodeModel.lastInteractionId =
                                            lastInteractionOnObject.getInt("id")
                                        allEpisodeModel.doctorId =
                                            lastInteractionOnObject.getInt("doctor_id")
                                        allEpisodeModel.encounterMode =
                                            lastInteractionOnObject.getString("encounter_mode")
                                        allEpisodeModel.lastInteractionOn =
                                            lastInteractionOnObject.getString("encounter_date_time")
                                    }
                                    allEpisodeModel.episodeId = episodeObject.getInt("id")
                                    allEpisodeModel.patientId = episodeObject.getInt("patient_id")
                                    allEpisodeModel.diagnosis = diagnosisArray
                                    emrAllEpisodeList!!.add(allEpisodeModel)
                                }
                                totalCasesText!!.text = doctorEpisodeLength.toString() + ""
                                /*Added try catch block to avoid the ArrayIndexOutOfBound exception*/try {
                                    caseNameText!!.text =
                                        emrAllEpisodeList!![caseSelectedPosition].episodeName
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                                if (activity != null) {
//                                encounterDropDownList.clear();
                                    getDropDownEncounterList(
                                        emrAllEpisodeList!![caseSelectedPosition].patientId,
                                        emrAllEpisodeList!![caseSelectedPosition].episodeId
                                    )
                                }
                            } else {
                                emrCaseHistoryProgressbar!!.visibility = View.GONE
                                consultNoteFragmentLayout!!.visibility = View.GONE
                                noCaseCreated!!.visibility = View.GONE
                                noInteractionTextLayout!!.visibility = View.GONE
                                if (sharedPreferences!!.getBoolean(
                                        "dontShowCreateNotesWorks",
                                        false
                                    )
                                ) {
                                    createNewCaseLayout!!.visibility = View.VISIBLE
                                    howItWorksLayout!!.visibility = View.GONE
                                } else {
                                    howItWorksLayout!!.visibility = View.VISIBLE
                                    createNewCaseLayout!!.visibility = View.GONE
                                }
                                if (TextUtils.isEmpty(apptDate)) {
                                    caseDate!!.text = appUtilities!!.changeDateFormat(
                                        "yyyy-MM-dd",
                                        "dd MMM, yyyy",
                                        "$cyear-$cmonth-$cday"
                                    )
                                } else {
                                    caseDate!!.text = appUtilities!!.changeDateFormat(
                                        "yyyy-MM-dd",
                                        "dd MMM, yyyy",
                                        apptDate
                                    )
                                }
                                if (TextUtils.isEmpty(apptTime) || apptTime == "00:00:00") {
                                    // caseTime.setText(appUtilities.changeDateFormat("HH:mm", "HH:mm", hours + ":" + mins));
                                    caseTime!!.text = appUtils!!.formatTimeBasedOnPreference(
                                        context, "HH:mm", "$hours:$mins"
                                    )
                                    selected_time = "$hours:$mins"
                                } else {
                                    // caseTime.setText(appUtilities.changeDateFormat("HH:mm:ss", "HH:mm", apptTime));
                                    caseTime!!.text = appUtils!!.formatTimeBasedOnPreference(
                                        context, "HH:mm", "$hours:$mins"
                                    )
                                    selected_time = "$hours:$mins"
                                }
                            }
                        } else {
                            if (value != null) {
                                errorHandler(requireContext(), value)
                            }
                        }
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                }
            })
    }

    fun getDropDownEncounterList(patientId: Int, episodeId: Int) {
        //get encounter drop
        emrConsultationNotesViewModel!!.getEncounterDropDownList(activity, patientId, episodeId)
            .observe(
                requireActivity(), object : Observer<String?> {
                    override fun onChanged(value: String?) {
                        try {
                            encounterDropDownList!!.clear()
                            val jsonObject = JSONObject(value)
                            if (jsonObject.getInt("status_code") == 200) {
                                val response = JSONObject(value).getJSONObject("response")
                                val encounterDropDownArray = response.getJSONArray("response")
                                val encounterDropDownArrayLength = encounterDropDownArray.length()
                                var allEpisodeModel = EMRAllEpisodeModel()
                                allEpisodeModel.encounterDropDownString = "All Interactions"
                                encounterDropDownList!!.add(allEpisodeModel)
                                for (i in 0 until encounterDropDownArrayLength) {
                                    val episodeObject = encounterDropDownArray.getJSONObject(i)
                                    allEpisodeModel = EMRAllEpisodeModel()
                                    allEpisodeModel.lastInteractionId = episodeObject.getInt("id")
                                    allEpisodeModel.episodeId = episodeObject.getInt("episode_id")
                                    allEpisodeModel.patientId = episodeObject.getInt("patient_id")
                                    allEpisodeModel.doctorId = episodeObject.getInt("doctor_id")
                                    allEpisodeModel.encounterMode =
                                        episodeObject.getString("encounter_mode")
                                    allEpisodeModel.lastInteractionOn =
                                        episodeObject.getString("encounter_date_time")
                                    val currentStringStart =
                                        episodeObject.getString("encounter_date_time")
                                    val separatedStart = currentStringStart.split(" ".toRegex())
                                        .dropLastWhile { it.isEmpty() }
                                        .toTypedArray()
                                    val date = separatedStart[0]
                                    val encounterDate = appUtilities!!.changeDateFormat(
                                        "yyyy-MM-dd",
                                        "dd MMM, yy",
                                        date
                                    )
                                    val encounterTime =
                                        separatedStart[1].substring(0, separatedStart[1].length - 3)
                                    allEpisodeModel.encounterDropDownString =
                                        episodeObject.getString("encounter_mode") + " on " + encounterDate + " @ " + appUtils!!.formatTimeBasedOnPreference(
                                            context, "HH:mm", encounterTime
                                        )
                                    encounterDropDownList!!.add(allEpisodeModel)
                                }

                                encounterDropDownAdapter!!.notifyDataSetChanged()
                                if (encounterDropDownList!!.size > 0) {
                                    if (allInteractionSpinner!!.selectedItemPosition == 0) {
                                        allInteraction = 0
                                        emrCaseHistoryProgressbar!!.visibility = View.VISIBLE
                                        getAllComponentForCaseHistory(
                                            emrAllEpisodeList!![caseSelectedPosition].patientId,
                                            emrAllEpisodeList!![caseSelectedPosition].episodeId,
                                            allInteraction
                                        )
                                    } else {
                                        allInteractionSpinner!!.setSelection(0)
                                    }
                                }
                            } else {
                                if (value != null) {
                                    errorHandler(requireContext(), value)
                                }
                            }
                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }
                    }
                })
    }

    fun getAllComponentForCaseHistory(patientId: Int, episodeId: Int, allInteraction: Int) {
        //get all component for case history
        EMRConsultCaseHistoryModelList!!.clear()
        editAndDeleteButtons!!.clear()
        emrConsultCaseHistoryListAdapter!!.notifyDataSetChanged()
        groupData!!.clear()
        c = 0
        emrConsultationNotesViewModel!!.getAllComponentForCaseHistoryDetails(
            activity,
            patientId,
            episodeId,
            allInteraction
        ).observe(
            requireActivity(), object : Observer<String?> {
                override fun onChanged(value: String?) {
                    try {
                        emrCaseHistoryProgressbar!!.visibility = View.GONE
                        EMRConsultCaseHistoryModelList!!.clear()
                        groupData!!.clear()
                        c = 0
                        val jsonObject = JSONObject(value)
                        if (jsonObject.getInt("status_code") == 200) {
                            val response = JSONObject(value).getJSONObject("response")
                            Log.d("allcomponent", response.toString())
                            val rootObj = response.optJSONObject("response")
                            val encounterDataArray = rootObj.getJSONArray("encounterData")
                            val fieldDicArr =
                                response.getJSONObject("response").getJSONObject("field-dictionary")
                            val encounterDataLength = encounterDataArray.length()
                            if (encounterDataLength > 0) {
                                for (j in 0 until encounterDataLength) {
                                    val encounterDataObject = encounterDataArray.getJSONObject(j)
                                    val recordDataObject =
                                        encounterDataObject.getJSONObject("recordsData")
                                    if (recordDataObject.getInt("allRecordsCount") > 0) {
                                        recordCount = 0
                                        ii = 0

                                        //handwritten record Data
                                        val HandWrittenNoteArray =
                                            recordDataObject.getJSONArray("handWrittenNotesRecords")
                                        if (HandWrittenNoteArray.length() > 0) {
                                            for (i in 0 until HandWrittenNoteArray.length()) {
                                                val handWrittenNoteObject =
                                                    HandWrittenNoteArray.getJSONObject(i)
                                                val emrConsultCaseHistoryModel =
                                                    EMRConsultCaseHistoryModel()
                                                groupData!!.add(c, ii)
                                                c++
                                                ii++
                                                recordCount++
                                                emrConsultCaseHistoryModel.encounterID =
                                                    encounterDataObject.getInt("id")
                                                emrConsultCaseHistoryModel.encounterName =
                                                    encounterDataObject.getString("encounter_mode")
                                                emrConsultCaseHistoryModel.encounterDateTime =
                                                    encounterDataObject.getString("encounter_date_time")
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
                                                if (recordCount == recordDataObject.getInt("allRecordsCount")) {
                                                    emrConsultCaseHistoryModel.enableSeparatorLine =
                                                        1
                                                } else {
                                                    emrConsultCaseHistoryModel.enableSeparatorLine =
                                                        0
                                                }
                                                EMRConsultCaseHistoryModelList!!.add(
                                                    emrConsultCaseHistoryModel
                                                )
                                            }
                                        }

                                        //symptoms record Data
                                        val symptomsArray =
                                            recordDataObject.getJSONArray("symptomsRecords")
                                        if (symptomsArray.length() > 0) {
                                            for (i in 0 until symptomsArray.length()) {
                                                val symptomsObject = symptomsArray.getJSONObject(i)
                                                val emrConsultCaseHistoryModel =
                                                    EMRConsultCaseHistoryModel()
                                                groupData!!.add(c, ii)
                                                c++
                                                ii++
                                                recordCount++
                                                emrConsultCaseHistoryModel.encounterID =
                                                    encounterDataObject.getInt("id")
                                                emrConsultCaseHistoryModel.encounterName =
                                                    encounterDataObject.getString("encounter_mode")
                                                emrConsultCaseHistoryModel.encounterDateTime =
                                                    encounterDataObject.getString("encounter_date_time")
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
                                                if (recordCount == recordDataObject.getInt("allRecordsCount")) {
                                                    emrConsultCaseHistoryModel.enableSeparatorLine =
                                                        1
                                                } else {
                                                    emrConsultCaseHistoryModel.enableSeparatorLine =
                                                        0
                                                }
                                                EMRConsultCaseHistoryModelList!!.add(
                                                    emrConsultCaseHistoryModel
                                                )
                                            }
                                        }


                                        //observation record data
                                        val observationArray =
                                            recordDataObject.getJSONArray("observationsRecords")
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
                                                        groupData!!.add(c, ii)
                                                        c++
                                                        ii++
                                                        recordCount++
                                                        emrConsultCaseHistoryModel.encounterID =
                                                            encounterDataObject.getInt("id")
                                                        emrConsultCaseHistoryModel.encounterName =
                                                            encounterDataObject.getString("encounter_mode")
                                                        emrConsultCaseHistoryModel.encounterDateTime =
                                                            encounterDataObject.getString("encounter_date_time")
                                                        emrConsultCaseHistoryModel.categoryName =
                                                            observationRecordCategoryObject.getString(
                                                                "category"
                                                            )
                                                        emrConsultCaseHistoryModel.createdAt =
                                                            observationRecordsArray.getJSONObject(
                                                                obs
                                                            ).getString("created_at")
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
                                                        if (recordCount == recordDataObject.getInt("allRecordsCount")) {
                                                            emrConsultCaseHistoryModel.enableSeparatorLine =
                                                                1
                                                        } else {
                                                            emrConsultCaseHistoryModel.enableSeparatorLine =
                                                                0
                                                        }
                                                        EMRConsultCaseHistoryModelList!!.add(
                                                            emrConsultCaseHistoryModel
                                                        )
                                                    }
                                                }
                                            }
                                        }

                                        //investigation results record Data
                                        val investigationResultRecordsArray =
                                            recordDataObject.getJSONArray("investigationResultsRecords")
                                        if (investigationResultRecordsArray.length() > 0) {
                                            for (i in 0 until investigationResultRecordsArray.length()) {
                                                val investigationResultRecordsObject =
                                                    investigationResultRecordsArray.getJSONObject(i)
                                                val emrConsultCaseHistoryModel =
                                                    EMRConsultCaseHistoryModel()
                                                groupData!!.add(c, ii)
                                                c++
                                                ii++
                                                recordCount++
                                                emrConsultCaseHistoryModel.encounterID =
                                                    encounterDataObject.getInt("id")
                                                emrConsultCaseHistoryModel.encounterName =
                                                    encounterDataObject.getString("encounter_mode")
                                                emrConsultCaseHistoryModel.encounterDateTime =
                                                    encounterDataObject.getString("encounter_date_time")
                                                emrConsultCaseHistoryModel.categoryName =
                                                    "Investigation Results"
                                                emrConsultCaseHistoryModel.createdAt =
                                                    investigationResultRecordsObject.getString("created_at")
                                                emrConsultCaseHistoryModel.isRecordData = 1

                                                //------------by dileep-------------
                                                val recordDetailsArray = JSONArray()
                                                recordDetailsArray.put(
                                                    investigationResultRecordsObject
                                                )
                                                //                                        int recordId = sharedObj.getInt("record_id");// added by dileep
                                                emrConsultCaseHistoryModel.categoryId =
                                                    investigationResultRecordsObject.getInt("id") //added by dileep
                                                emrConsultCaseHistoryModel.fieldDictionary =
                                                    fieldDicArr.toString() // added by dileep
                                                //                                        model.setRecordId(recordId);// added by dileep
                                                emrConsultCaseHistoryModel.categoryRecordData =
                                                    recordDetailsArray.toString()
                                                if (recordCount == recordDataObject.getInt("allRecordsCount")) {
                                                    emrConsultCaseHistoryModel.enableSeparatorLine =
                                                        1
                                                } else {
                                                    emrConsultCaseHistoryModel.enableSeparatorLine =
                                                        0
                                                }
                                                EMRConsultCaseHistoryModelList!!.add(
                                                    emrConsultCaseHistoryModel
                                                )
                                            }
                                        }

                                        //diagnosis record Data
                                        val diagnosisRecordsArray =
                                            recordDataObject.getJSONArray("diagnosisRecords")
                                        if (diagnosisRecordsArray.length() > 0) {
                                            for (i in 0 until diagnosisRecordsArray.length()) {
                                                val diagnosisRecordsArrayRecordsObject =
                                                    diagnosisRecordsArray.getJSONObject(i)
                                                val emrConsultCaseHistoryModel =
                                                    EMRConsultCaseHistoryModel()
                                                groupData!!.add(c, ii)
                                                c++
                                                ii++
                                                recordCount++
                                                emrConsultCaseHistoryModel.encounterID =
                                                    encounterDataObject.getInt("id")
                                                emrConsultCaseHistoryModel.encounterName =
                                                    encounterDataObject.getString("encounter_mode")
                                                emrConsultCaseHistoryModel.encounterDateTime =
                                                    encounterDataObject.getString("encounter_date_time")
                                                emrConsultCaseHistoryModel.categoryName =
                                                    "Diagnosis"
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
                                                if (recordCount == recordDataObject.getInt("allRecordsCount")) {
                                                    emrConsultCaseHistoryModel.enableSeparatorLine =
                                                        1
                                                } else {
                                                    emrConsultCaseHistoryModel.enableSeparatorLine =
                                                        0
                                                }
                                                EMRConsultCaseHistoryModelList!!.add(
                                                    emrConsultCaseHistoryModel
                                                )
                                            }
                                        }

                                        //treatment Plan record data
                                        val treatmentPlanArray =
                                            recordDataObject.getJSONArray("treatmentPlanRecords")
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
                                                        groupData!!.add(c, ii)
                                                        c++
                                                        ii++
                                                        recordCount++
                                                        emrConsultCaseHistoryModel.encounterID =
                                                            encounterDataObject.getInt("id")
                                                        emrConsultCaseHistoryModel.encounterName =
                                                            encounterDataObject.getString("encounter_mode")
                                                        emrConsultCaseHistoryModel.encounterDateTime =
                                                            encounterDataObject.getString("encounter_date_time")
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
                                                            treatmentPlanRecordCategoryObject.getInt(
                                                                "id"
                                                            ) //added by dileep
                                                        emrConsultCaseHistoryModel.fieldDictionary =
                                                            fieldDicArr.toString() // added by dileep
                                                        emrConsultCaseHistoryModel.recordId =
                                                            recordId // added by dileep
                                                        emrConsultCaseHistoryModel.categoryRecordData =
                                                            recordDetailsArray.toString()
                                                        if (recordCount == recordDataObject.getInt("allRecordsCount")) {
                                                            emrConsultCaseHistoryModel.enableSeparatorLine =
                                                                1
                                                        } else {
                                                            emrConsultCaseHistoryModel.enableSeparatorLine =
                                                                0
                                                        }
                                                        EMRConsultCaseHistoryModelList!!.add(
                                                            emrConsultCaseHistoryModel
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                    } else {
                                        val emrConsultCaseHistoryModel =
                                            EMRConsultCaseHistoryModel()
                                        groupData!!.add(c, 0)
                                        c++
                                        emrConsultCaseHistoryModel.encounterID =
                                            encounterDataObject.getInt("id")
                                        emrConsultCaseHistoryModel.encounterName =
                                            encounterDataObject.getString("encounter_mode")
                                        emrConsultCaseHistoryModel.encounterDateTime =
                                            encounterDataObject.getString("encounter_date_time")
                                        emrConsultCaseHistoryModel.isRecordData = 0
                                        emrConsultCaseHistoryModel.enableSeparatorLine = 1
                                        EMRConsultCaseHistoryModelList!!.add(
                                            emrConsultCaseHistoryModel
                                        )
                                    }
                                    val buttons = ShowEditAndDeleteButtons()
                                    buttons.isCanDeleteEditRecords =
                                        recordDataObject.getBoolean("canDeleteEditRecords")
                                    buttons.isCanDeleteEditWrittenNotes =
                                        recordDataObject.getBoolean("canDeleteEditWrittenNotes")
                                    buttons.id = encounterDataObject.getInt("id")
                                    buttons.encounter_Id = encounterDataObject.getInt("id")
                                    buttons.episode_id = encounterDataObject.getInt("episode_id")
                                    buttons.patient_id = encounterDataObject.getInt("patient_id")
                                    buttons.doctor_id = encounterDataObject.getInt("doctor_id")
                                    editAndDeleteButtons!!.add(buttons)
                                    /*  canDeleteEditRecords = recordDataObject.getBoolean("canDeleteEditRecords");
                                canDeleteEditWrittenNotes = recordDataObject.getBoolean("canDeleteEditWrittenNotes");*/
                                }
                                emrConsultCaseHistoryListAdapter!!.notifyDataSetChanged()
                            } else {
                                noInteractionTextLayout!!.visibility = View.VISIBLE
                                //                            Toast.makeText(getActivity(), "You have not logged any interaction with this case", Toast.LENGTH_LONG).show();
                            }
                        } else {
                            if (value != null) {
                                errorHandler(requireContext(), value)
                            }
                        }
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                }
            })
    } //get simboAuthKey

    companion object {
        // TODO: Rename parameter arguments, choose names that match
        // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
        private const val ARG_PARAM1 = "patientId"
        private const val ARG_PARAM2 = "appointmentId"
        private const val ARG_PARAM3 = "appointmentDate"
        private const val ARG_PARAM4 = "appointmentTime"
        private const val ARG_PARAM5 = "appointmentMode"
        private const val ARG_PARAM6 = "patientName"
        private var appUtils: AppUtilities? = null
        var emrAllEpisodeList: MutableList<EMRAllEpisodeModel>? = null
        var encounterDropDownList: MutableList<EMRAllEpisodeModel>? = null
        @JvmField
        var caseSelectedPosition = 0
        var switchCaseClicked = 0
        var canDeleteEditRecords = false
        var canDeleteEditWrittenNotes = false

        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param patientId     Parameter 1.
         * @param appointmentId Parameter 2.
         * @return A new instance of fragment EMRConsultationNotesFragment.
         */
        // TODO: Rename and change types and number of parameters
        fun newInstance(
            patientId: Int,
            appointmentId: Int,
            apptDate: String?,
            apptTime: String?,
            apptMode: Int,
            patientName: String?
        ): EMRConsultationNotesFragment {
            val fragment = EMRConsultationNotesFragment()
            appUtils = AppUtilities()
            val args = Bundle()
            args.putInt(ARG_PARAM1, patientId)
            args.putInt(ARG_PARAM2, appointmentId)
            args.putString(ARG_PARAM3, apptDate)
            args.putString(ARG_PARAM4, apptTime)
            args.putInt(ARG_PARAM5, apptMode)
            args.putString(ARG_PARAM6, patientName)
            fragment.arguments = args
            return fragment
        }
    }
}