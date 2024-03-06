package com.whitecoats.clinicplus.fragments

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Point
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.whitecoats.adapter.PatientPListAdapter.Companion.isPatientProfileClicked
import com.whitecoats.clinicplus.*
import com.whitecoats.clinicplus.activities.AddPatientActivity
import com.whitecoats.clinicplus.activities.EMRActivity
import com.whitecoats.clinicplus.adapters.PatientSearchAdapter
import com.whitecoats.clinicplus.apis.ApiUrls
import com.whitecoats.clinicplus.utils.ErrorHandlerClass.errorHandler
import com.whitecoats.clinicplus.utils.SharedPref
import com.whitecoats.clinicplus.viewmodels.SearchPatientViewModel
import com.whitecoats.model.PatientPListModel
import com.zoho.salesiqembed.ZohoSalesIQ
import org.json.JSONException
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

/**
 * A simple [Fragment] subclass.
 * Use the [SearchFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SearchFragment : Fragment() {
    private lateinit var search: ImageView
    private lateinit var cancelSearch: ImageView
    private lateinit var cancelSearchDialog: ImageView
    lateinit var searchDialog: AlertDialog
    private var builder: AlertDialog.Builder? = null
    private lateinit var viewAllPatients: TextView
    private lateinit var searchError: TextView
    private lateinit var searchPatientName: TextView
    private lateinit var searchPatientPhone: TextView
    private lateinit var searchPatientEmail: TextView
    private lateinit var noPatientText: TextView
    private lateinit var viewProfile: TextView
    private lateinit var searchingPatient: TextView
    private lateinit var searchingPatientDialog: TextView
    private lateinit var searchPatient: AutoCompleteTextView
    private lateinit var searchPatientDialog: AutoCompleteTextView
    private var searchPatientViewModel: SearchPatientViewModel? = null
    private lateinit var patientSearchAdapter: PatientSearchAdapter
    private lateinit var dialogView: View
    private lateinit var noPatientLayout: LinearLayout
    private lateinit var patientDetailsLayout: LinearLayout
    private lateinit var patientPListModelList: MutableList<PatientPListModel>
    private lateinit var addPatientDialogButton: RelativeLayout
    private lateinit var tv_add_patient: TextView
    private lateinit var createNote: TextView
    private lateinit var bookAppt: TextView
    private var patientPList: PatientPListModel? = null
    var globalClass: MyClinicGlobalClass? = null
    private lateinit var addPatient: RelativeLayout
    private lateinit var sharedPref: SharedPref

    private lateinit var tvPatientGenId: TextView


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_search, container, false)
        searchPatientViewModel = ViewModelProvider(this)[SearchPatientViewModel::class.java]
        searchPatientViewModel!!.init()
        sharedPref = SharedPref(activity)

        search = view.findViewById(R.id.search_patient)
        viewAllPatients = view.findViewById(R.id.view_all_patients)
        searchError = view.findViewById(R.id.search_error)
        searchPatient = view.findViewById(R.id.patient_search_view)
        cancelSearch = view.findViewById(R.id.cancel_patient_search)
        addPatient = view.findViewById(R.id.add_new_patient_button)
        builder = AlertDialog.Builder(context)
        dialogView = layoutInflater.inflate(R.layout.search_dialog, null)
        noPatientLayout = dialogView.findViewById(R.id.no_patient_layout)
        searchPatientName = dialogView.findViewById(R.id.search_patient_name)
        searchPatientPhone = dialogView.findViewById(R.id.search_patient_phone)
        tvPatientGenId = dialogView.findViewById(R.id.tv_patient_general_id)
        searchPatientEmail = dialogView.findViewById(R.id.search_patient_email)
        searchPatientDialog = dialogView.findViewById(R.id.dialog_patient_search)
        patientDetailsLayout = dialogView.findViewById(R.id.patient_details_layout)
        cancelSearchDialog = dialogView.findViewById(R.id.cancel_search)
        emptyPatientText = dialogView.findViewById(R.id.empty_patient_text)
        createNote = dialogView.findViewById(R.id.create_note)
        bookAppt = dialogView.findViewById(R.id.book_new_appt)
        viewProfile = dialogView.findViewById(R.id.view_profile)
        noPatientText = dialogView.findViewById(R.id.no_patient_text)
        addPatientDialogButton = dialogView.findViewById(R.id.add_patient_dialog_button)
        tv_add_patient = dialogView.findViewById(R.id.tv_add_patient)
        searchingPatientDialog = dialogView.findViewById(R.id.searching_patient_dialog)
        searchingPatient = view.findViewById(R.id.searching_patient)
        builder!!.setView(dialogView)
        globalClass = requireActivity().applicationContext as MyClinicGlobalClass
        patientPListModelList = ArrayList()
        patientSearchAdapter =
            PatientSearchAdapter(
                requireContext(),
                R.layout.patient_search_item,
                patientPListModelList
            )
        searchPatient.setAdapter(patientSearchAdapter)
        searchPatientDialog.setAdapter(patientSearchAdapter)
        viewAllPatients.setOnClickListener {
            MyClinicGlobalClass.logUserActionEvent(
                ApiUrls.doctorId,
                getString(R.string.DashboardViewAllPatients),
                null
            )
            (activity as MainActivity?)!!.changeFragment()
        }
        val pointSize = Point()
        requireActivity().windowManager.defaultDisplay.getSize(pointSize)
        searchPatient.dropDownWidth = Math.round(pointSize.x * 0.80).toInt()
        createNote.setOnClickListener {
            ZohoSalesIQ.Tracking.setCustomAction("Patient Search - Create Note")
            val intent = Intent(activity, EMRActivity::class.java)
            intent.putExtra("ApptId", 0)
            intent.putExtra("PatientId", patientPList!!.patientId)
            intent.putExtra("ApptMode", 0)
            intent.putExtra("ApptDate", "")
            intent.putExtra("ApptTime", "00:00:00")
            intent.putExtra("PatientName", patientPList!!.getPatientName())
            searchDialog.dismiss()
            clearSearchData()
            startActivity(intent)
        }
        bookAppt.setOnClickListener {
            MyClinicGlobalClass.logUserActionEvent(
                ApiUrls.doctorId,
                getString(R.string.DashboardBookNewAppt),
                null
            )
            if (DashboardTabFragment.practiceAdded == 1) {
                when (DashboardFullMode.isServiceId) {
                    2 -> {
                        var serviceAlias: String? = ""
                        var prodId = 0
                        var servId = 0
                        var price = 0
                        var serviceAliasName: String? = ""
                        for (i in DashboardFullMode.doctorServiceArrayList.indices) {
                            if (DashboardFullMode.doctorServiceArrayList[i].appointmentServiceID == 2) {
                                serviceAlias =
                                    DashboardFullMode.doctorServiceArrayList[i].appointmentServiceAlias
                                prodId = DashboardFullMode.doctorServiceArrayList[i].prodId
                                servId = DashboardFullMode.doctorServiceArrayList[i].servId
                                price = DashboardFullMode.doctorServiceArrayList[i].price
                                serviceAliasName =
                                    DashboardFullMode.doctorServiceArrayList[i].prodAliasName
                            }
                        }
                        val c = Calendar.getInstance().time
                        println("Current time => $c")
                        val df = SimpleDateFormat("MMMM d, yyyy", Locale.ENGLISH)
                        val formattedDate = df.format(c)
                        val intent = Intent(activity, ConfirmOrderActivity::class.java)
                        intent.putExtra(
                            "appointment_service_id",
                            DashboardFullMode.isAppointmentServiceId
                        )
                        intent.putExtra("date", formattedDate)
                        intent.putExtra("service_name", serviceAlias)
                        intent.putExtra("service_alias_name", serviceAliasName)
                        intent.putExtra("service_price", price)
                        intent.putExtra("service_id", servId)
                        intent.putExtra("prod_id", prodId)
                        intent.putExtra("patientId", patientPList!!.patientId)
                        intent.putExtra("patientName", patientPList!!.getPatientName())
                        intent.putExtra("appointment_service_id", 2)
                        searchDialog.dismiss()
                        startActivity(intent)
                    }
                    1 -> {
                        val intent = Intent(activity, BookAppointmentTimeSlotActivity::class.java)
                        intent.putExtra(
                            "doctorDetailsRootObjects",
                            DashboardFullMode.doctorsDetailsRootObj.toString()
                        )
                        intent.putExtra("serviceId", 1)
                        intent.putExtra("patientId", patientPList!!.patientId)
                        intent.putExtra("patientName", patientPList!!.getPatientName())
                        searchDialog.dismiss()
                        startActivity(intent)
                    }
                    else -> {
                        val intent = Intent(activity, BookAppointmentTimeSlotActivity::class.java)
                        intent.putExtra(
                            "doctorDetailsRootObjects",
                            DashboardFullMode.doctorsDetailsRootObj.toString()
                        )
                        intent.putExtra("serviceId", 3)
                        intent.putExtra("patientId", patientPList!!.patientId)
                        intent.putExtra("patientName", patientPList!!.getPatientName())
                        searchDialog.dismiss()
                        startActivity(intent)
                    }
                }
                clearSearchData()
            } else {
                Toast.makeText(
                    activity,
                    "Please add the service to book and Appt.",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
        viewProfile.setOnClickListener {
            ZohoSalesIQ.Tracking.setCustomAction("Search Dialog - Patient Profile")
            isPatientProfileClicked = true
            val intent = Intent(activity, EMRActivity::class.java)
            intent.putExtra("ApptId", 0)
            intent.putExtra("PatientId", patientPList!!.patientId)
            intent.putExtra("ApptMode", 0)
            intent.putExtra("ApptDate", "")
            intent.putExtra("ApptTime", "00:00:00")
            intent.putExtra("PatientName", patientPList!!.getPatientName())
            searchDialog.dismiss()
            clearSearchData()
            requireActivity().startActivity(intent)
        }
        cancelSearch.setOnClickListener {
            searchPatient.setText("")
            val imm =
                requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0)
        }
        searchDialog = builder!!.create()
        searchDialog.setCancelable(false)
        try {
            searchDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        } catch (e: NullPointerException) {
            e.printStackTrace()
        }
        addPatient.setOnClickListener {
            MyClinicGlobalClass.logUserActionEvent(
                ApiUrls.doctorId,
                getString(R.string.DashboardAddPatient),
                null
            )
            val addPatientIntent = Intent(context, AddPatientActivity::class.java)
            startActivity(addPatientIntent)
        }
        addPatientDialogButton.setOnClickListener {
            val addPatientIntent = Intent(context, AddPatientActivity::class.java)
            addPatientIntent.putExtra(
                "name",
                searchPatientDialog.text.toString().trim { it <= ' ' })
            searchDialog.dismiss()
            startActivity(addPatientIntent)
        }
        cancelSearchDialog.setOnClickListener {
            clearSearchData()
            searchDialog.dismiss()
        }
        searchPatient.onItemClickListener =
            AdapterView.OnItemClickListener { parent, _, position, _ ->
                DashboardFullMode.isServiceId = 3
                DashboardFullMode.isAppointBookingOnDashBoard = 0
                patientPList = parent.getItemAtPosition(position) as PatientPListModel
                searchPatientDialog.setText(patientPList!!.getPatientName())
                searchPatientName.text = patientPList!!.getPatientName()
                searchPatientPhone.text = patientPList!!.getPhNo()
                searchPatientEmail.text = patientPList!!.getEmailid()
            //  if (sharedPref.getPref("is_show_general_id", "").equalsIgnoreCase("1")) {
            if (patientPList!!.getGeneralID() != null && !patientPList!!.getGeneralID()
                    .equals("", ignoreCase = true)
            ) {
                tvPatientGenId.setVisibility(View.VISIBLE)
                tvPatientGenId.setText(patientPList!!.getGeneralID())
            } else {
                tvPatientGenId.setVisibility(View.GONE)
            }
            /*} else {
                        tvPatientGenId.setVisibility(View.GONE);
                    }*/

                patientDetailsLayout.visibility = View.VISIBLE
                if (sharedPref.isPrefExists("EMR")) {
                    createNote.visibility = View.VISIBLE
                } else {
                    createNote.visibility = View.GONE
                }
                emptyPatientText.visibility = View.GONE
                searchDialog.show()
                MyClinicGlobalClass.logUserActionEvent(
                    48, getString(R.string.DashboardSearchPatient),
                    null
                )
            }
        searchPatientDialog.onItemClickListener =
            AdapterView.OnItemClickListener { parent, _, position, _ ->
                patientPList = parent.getItemAtPosition(position) as PatientPListModel
                searchPatientDialog.setText(patientPList!!.getPatientName())
                searchPatientName.text = patientPList!!.getPatientName()
                searchPatientPhone.text = patientPList!!.getPhNo()
                searchPatientEmail.text = patientPList!!.getEmailid()


                /*New Registration(Autogenerated ID) changes for Gastro interface*/
            //  if (sharedPref.getPref("is_show_general_id", "").equalsIgnoreCase("1")) {
            if (patientPList!!.getGeneralID() != null && !patientPList!!.getGeneralID()
                    .equals("", ignoreCase = true)
            ) {
                tvPatientGenId.setVisibility(View.VISIBLE)
                tvPatientGenId.setText(patientPList!!.getGeneralID())
            } else {
                tvPatientGenId.setVisibility(View.GONE)
            }
            /* } else {
                        tvPatientGenId.setVisibility(View.GONE);
                    }*/

            patientDetailsLayout.setVisibility(View.VISIBLE)
                patientDetailsLayout.visibility = View.VISIBLE
                if (sharedPref.isPrefExists("EMR")) {
                    createNote.visibility = View.VISIBLE
                } else {
                    createNote.visibility = View.GONE
                }
                emptyPatientText.visibility = View.GONE
            }
        searchPatientDialog.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            @SuppressLint("SetTextI18n")
            override fun onTextChanged(
                patientName: CharSequence,
                start: Int,
                before: Int,
                count: Int
            ) {
                if (patientName.isEmpty()) {
                    noPatientLayout.visibility = View.VISIBLE
                    patientDetailsLayout.visibility = View.GONE
                    noPatientText.text = "Do you want to add new patient?"
                    tv_add_patient.text = "Add New Patient"
                }
                if (patientName.length > 2) {
                    if (globalClass!!.isOnline) {
                        searchingPatientDialog.visibility = View.VISIBLE
                        searchPatientViewModel!!.searchPatient(activity, patientName.toString())
                            .observe(
                                viewLifecycleOwner
                            ) { s ->
                                searchingPatientDialog.visibility = View.GONE
                                try {
                                    val jsonObject = JSONObject(s)
                                    if (jsonObject.getInt("status_code") == 200) {
                                        val patientDetails =
                                            jsonObject.getJSONObject("response")
                                                .getJSONArray("response")
                                        if (patientDetails.length() == 0) {
                                            if (searchPatientDialog.text.toString()
                                                    .trim { it <= ' ' }.isEmpty()
                                            ) {
                                                noPatientLayout.visibility = View.GONE
                                                patientDetailsLayout.visibility = View.GONE
                                                emptyPatientText.visibility = View.VISIBLE
                                            } else {
                                                noPatientLayout.visibility = View.VISIBLE
                                                patientDetailsLayout.visibility = View.GONE
                                                emptyPatientText.visibility = View.GONE
                                                noPatientText.text =
                                                    resources.getText(R.string.no_patient_text)
                                                        .toString() + " " + patientName.toString()
                                                tv_add_patient.text = "Add $patientName"
                                            }
                                        } else {
                                            noPatientLayout.visibility = View.GONE
                                            patientPListModelList.clear()
                                            for (i in 0 until patientDetails.length()) {
                                                val patientDetail =
                                                    patientDetails.getJSONObject(i)
                                                val assignCategory =
                                                    patientDetail.getJSONArray("assignedCategories")
                                                val patientPListModel = PatientPListModel()
                                                patientPListModel.setPatientName(
                                                    patientDetail.getString("fullname")
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
                                                        /*New Registration(Autogenerated ID) changes for Gastro interface*/patientPListModel.setGeneralID(
                                                            patientDetail.getString("general_id")
                                                        )

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
                    } else {
                        globalClass!!.noInternetConnection.showDialog(activity)
                    }
                }
            }

            override fun afterTextChanged(s: Editable) {
                if (s.toString().isEmpty()) {
                    noPatientLayout.visibility = View.GONE
                    patientDetailsLayout.visibility = View.GONE
                    emptyPatientText.visibility = View.VISIBLE
                }
            }
        })
        searchPatient.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (s.isNotEmpty()) {
                    cancelSearch.visibility = View.VISIBLE
                    search.visibility = View.GONE
                } else {
                    cancelSearch.visibility = View.GONE
                    search.visibility = View.VISIBLE
                    searchError.visibility = View.GONE
                }
                if (s.length > 2) {
                    if (globalClass!!.isOnline) {
                        searchingPatient.visibility = View.VISIBLE
                        searchError.visibility = View.GONE
                        searchPatientViewModel!!.searchPatient(activity, s.toString()).observe(
                            viewLifecycleOwner
                        ) { s ->
                            Log.i("search res", s)
                            searchingPatient.visibility = View.GONE
                            try {
                                val jsonObject = JSONObject(s)
                                if (jsonObject.getInt("status_code") == 200) {
                                    val patientDetails =
                                        jsonObject.getJSONObject("response")
                                            .getJSONArray("response")
                                    if (patientDetails.length() == 0) {
                                        if (searchPatient.text
                                                .toString().isEmpty()
                                        ) {
                                            searchError.visibility = View.GONE
                                        } else {
                                            searchError.visibility = View.VISIBLE
                                        }
                                    } else {
                                        searchError.visibility = View.GONE
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

                                                    /*New Registration(Autogenerated ID) changes for Gastro interface*/patientPListModel.setGeneralID(
                                                        patientDetail.getString("general_id")
                                                    )

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
                    } else {
                        globalClass!!.noInternetConnection.showDialog(activity)
                    }
                }
            }

            override fun afterTextChanged(s: Editable) {
                if (s.toString().isEmpty()) {
                    Log.i("after pati", "called")
                    searchError.visibility = View.GONE
                }
            }
        })
        return view
    }

    private fun clearSearchData() {
        searchPatient.setText("")
        searchPatientDialog.setText("")
        searchPatientName.text = ""
        searchPatientPhone.text = ""
        searchPatientEmail.text = ""
        noPatientLayout.visibility = View.GONE
        patientDetailsLayout.visibility = View.GONE
        emptyPatientText.visibility = View.VISIBLE
    }

    override fun onStop() {
        super.onStop()
        if (searchDialog.isShowing) {
            searchDialog.dismiss()
        }
    }

    override fun onResume() {
        super.onResume()
        clearSearchData()
    }

    companion object {
        @SuppressLint("StaticFieldLeak")
        lateinit var emptyPatientText: TextView
        fun newInstance(): SearchFragment {
            return SearchFragment()
        }
    }
}