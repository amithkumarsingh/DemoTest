package com.whitecoats.clinicplus.fragments

import android.app.ProgressDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.gson.GsonBuilder
import com.whitecoats.clinicplus.MyClinicGlobalClass
import com.whitecoats.clinicplus.R
import com.whitecoats.clinicplus.apis.ApiUrls
import com.whitecoats.clinicplus.constants.AppConstants
import com.whitecoats.clinicplus.models.PatientModel
import com.whitecoats.clinicplus.utils.ErrorHandlerClass.errorHandler
import com.whitecoats.clinicplus.viewmodels.EMRPatientProfileViewModel
import org.json.JSONObject

class EMRProfileTabFragment : Fragment() {
    private var mainView: View? = null
    private var detailsLayout: ScrollView? = null
    private var progressDialog: ProgressDialog? = null
    private var patientName: TextView? = null
    private var patientEmail: TextView? = null
    private var patientPhone: TextView? = null
    private var patientAge: TextView? = null
    private var patientGender: TextView? = null
    private var patientBloodGroup: TextView? = null
    private var patientHeight: TextView? = null
    private var saveEditBtn: Button? = null
    private var nameEdit: EditText? = null
    private var emailEdit: EditText? = null
    private var phoneEdit: EditText? = null
    private var ageEdit: EditText? = null
    private var heightEdit: EditText? = null
    private var nameEditLayout: LinearLayout? = null
    private var emailEditLayout: LinearLayout? = null
    private var phoneEditLayout: LinearLayout? = null
    private var ageEditLayout: LinearLayout? = null
    private var heightEditLayout: LinearLayout? = null
    private var bloodGroupEditLayout: LinearLayout? = null
    private var genderEditLayout: LinearLayout? = null
    private var bloodGroupEdit: Spinner? = null
    private var genderEdit: Spinner? = null
    private var nameError: TextView? = null
    private var phoneError: TextView? = null
    private var emailError: TextView? = null
    private var patientProfileViewModel: EMRPatientProfileViewModel? = null
    private var patientID = 0
    private var mode = 0 // 0 = not editable, 1 = editable
    private var patientModel: PatientModel? = null
    private val isDataUpdateToastDisplay = 0
    private val age_type = arrayOf("Years", "Months", "Days")
    private var patient_age_spin: Spinner? = null

    private lateinit var ll_parent_gen_id: LinearLayout
    private lateinit var tvGeneralIdHeader: TextView
    private lateinit var tvGeneralId: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mainView = inflater.inflate(R.layout.fragment_emr_profile_tab, container, false)
        if (arguments != null) {
            patientID = requireArguments().getInt("PatientID")
        }
        patient_age_spin = mainView?.findViewById(R.id.patient_age_spin)
        val patientAgeTypeAdapter =
            ArrayAdapter(requireActivity(), android.R.layout.simple_spinner_dropdown_item, age_type)
        patientAgeTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        patient_age_spin?.adapter = patientAgeTypeAdapter
        patient_age_spin?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?, view: View, position: Int, id: Long
            ) {
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
        initView()
        return mainView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    private fun initView() {

        tvGeneralId = mainView!!.findViewById(R.id.tvGeneralId)
        tvGeneralIdHeader = mainView!!.findViewById(R.id.tvGeneralIdHeader)
        ll_parent_gen_id = mainView!!.findViewById(R.id.ll_parent_gen_id)


        patientName = mainView!!.findViewById(R.id.patientNameText)
        patientEmail = mainView!!.findViewById(R.id.patientEmailText)
        patientPhone = mainView!!.findViewById(R.id.patientPhoneText)
        patientGender = mainView!!.findViewById(R.id.patientGenderText)
        patientBloodGroup = mainView!!.findViewById(R.id.patientBloodText)
        patientHeight = mainView!!.findViewById(R.id.patientHeightText)
        patientAge = mainView!!.findViewById(R.id.patientAgeText)
        saveEditBtn = mainView!!.findViewById(R.id.saveEditButton)
        nameEdit = mainView!!.findViewById(R.id.patientNameEdit)
        emailEdit = mainView!!.findViewById(R.id.patientEmailEdit)
        phoneEdit = mainView!!.findViewById(R.id.patientPhoneEdit)
        ageEdit = mainView!!.findViewById(R.id.patientAgeEdit)
        heightEdit = mainView!!.findViewById(R.id.patientHeightEdit)
        bloodGroupEdit = mainView!!.findViewById(R.id.patientBloodSelect)
        genderEdit = mainView!!.findViewById(R.id.patientGenderSelect)
        nameEditLayout = mainView!!.findViewById(R.id.patientNameInputLayout)
        emailEditLayout = mainView!!.findViewById(R.id.patientEmailInputLayout)
        phoneEditLayout = mainView!!.findViewById(R.id.patientPhoneInputLayout)
        ageEditLayout = mainView!!.findViewById(R.id.patientAgeInputLayout)
        heightEditLayout = mainView!!.findViewById(R.id.patientHeightInputLayout)
        bloodGroupEditLayout = mainView!!.findViewById(R.id.patientBloodInputLayout)
        genderEditLayout = mainView!!.findViewById(R.id.patientGenderInputLayout)
        detailsLayout = mainView!!.findViewById(R.id.detailsLayout)
        nameError = mainView!!.findViewById(R.id.patientNameErrorText)
        emailError = mainView!!.findViewById(R.id.patientEmailErrorText)
        phoneError = mainView!!.findViewById(R.id.patientPhoneErrorText)
        detailsLayout?.visibility = View.GONE
        progressDialog = ProgressDialog(context)
        patientProfileViewModel = ViewModelProvider(this).get(
            EMRPatientProfileViewModel::class.java
        )
        patientProfileViewModel!!.init()
        patientDetails
        saveEditBtn?.setOnClickListener(View.OnClickListener { view: View? ->
            // if mode is not editable then we switch to editable
            if (mode == 0) {
                enableEditMode()
                mode = 1
                saveEditBtn!!.text = requireActivity().resources.getString(R.string.save_proceed)
            } else {
                // saving data call
                try {
                    if (makeRequestObj()) {
                        val gson = GsonBuilder().create()
                        val jsonObject = JSONObject(gson.toJson(patientModel))
                        jsonObject.put("user_id", patientID)
                        Log.d("Json Obj", jsonObject.toString())
                        progressDialog!!.setMessage("Updating Data...")
                        progressDialog!!.show()
                        patientProfileViewModel!!.updatePatientDetails(
                            activity,
                            patientModel!!.id,
                            jsonObject
                        ).observe(
                            viewLifecycleOwner
                        ) { s: String? ->
                            Log.d("Update Response", s!!)
                            progressDialog!!.dismiss()
                            try {
                                val resObj = JSONObject(s)
                                if (resObj.getInt("status_code") != 200) {
                                    errorHandler(requireContext(), s)
                                } else {
                                    Toast.makeText(
                                        context,
                                        "Data Updated Successfully",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    patientDetails
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        })
        var dataAdapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, AppConstants.genderList)
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        genderEdit!!.adapter = dataAdapter
        dataAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            AppConstants.bloodGroupList
        )
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        bloodGroupEdit!!.adapter = dataAdapter
    }

    private val patientDetails: Unit
        private get() {
            progressDialog!!.setMessage("Fetching Data...")
            progressDialog!!.show()
            patientProfileViewModel!!.getPatientDetails(activity, patientID)
                .observe(viewLifecycleOwner) { patientModel: PatientModel ->
                    detailsLayout!!.visibility = View.VISIBLE
                    progressDialog!!.dismiss()
                    bindingData(patientModel)
                }
        }

    private fun bindingData(patientModel: PatientModel) {
        this.patientModel = patientModel


        // if (sharedPref.getPref("is_show_general_id", "").equalsIgnoreCase("1")) {
        if (patientModel.generalID != null && !patientModel.generalID.equals(
                "",
                ignoreCase = true
            )
        ) {
            ll_parent_gen_id!!.visibility = View.VISIBLE
            tvGeneralId!!.text = patientModel.generalID
        } else {
            ll_parent_gen_id!!.visibility = View.GONE
        }
        /* } else {
            ll_parent_gen_id.setVisibility(View.GONE);
        }*/


        patientName!!.text = getString(R.string.na)
        patientName!!.visibility = View.VISIBLE
        nameEditLayout!!.visibility = View.GONE
        if (patientModel.name != "") {
            patientName!!.text = patientModel.name
        }
        patientEmail!!.text = getString(R.string.na)
        emailEditLayout!!.visibility = View.GONE
        patientEmail!!.visibility = View.VISIBLE
        if (patientModel.email != "") {
            patientEmail!!.text = patientModel.email
        }
        patientPhone!!.text = getString(R.string.na)
        phoneEditLayout!!.visibility = View.GONE
        patientPhone!!.visibility = View.VISIBLE
        if (patientModel.phone != "") {
            patientPhone!!.text = patientModel.phone
        }
        patientAge!!.text = getString(R.string.na)
        ageEditLayout!!.visibility = View.GONE
        patientAge!!.visibility = View.VISIBLE
        if (patientModel.age != "") {
            if (patientModel.age_type != null) {
                patientAge!!.text = String.format("%s %s", patientModel.age, patientModel.age_type)
            } else {
                patientAge!!.text = String.format("%s years", patientModel.age)
            }
        }
        patientGender!!.text = getString(R.string.na)
        genderEditLayout!!.visibility = View.GONE
        patientGender!!.visibility = View.VISIBLE
        if (patientModel.gender != 0) {
            patientGender!!.text = AppConstants.genderList[patientModel.gender]
        }
        patientBloodGroup!!.text = getString(R.string.na)
        bloodGroupEditLayout!!.visibility = View.GONE
        patientBloodGroup!!.visibility = View.VISIBLE
        if (patientModel.blood_group != "") {
            patientBloodGroup!!.text = patientModel.blood_group
        }
        patientHeight!!.text = getString(R.string.na)
        heightEditLayout!!.visibility = View.GONE
        patientHeight!!.visibility = View.VISIBLE
        if (patientModel.height != "") {
            patientHeight!!.text = String.format("%s inches", patientModel.height)
        }
        saveEditBtn!!.text = getString(R.string.edit_profile)
        mode = 0
    }

    private fun enableEditMode() {
        MyClinicGlobalClass.logUserActionEvent(
            ApiUrls.doctorId,
            getString(R.string.PatientInfoEditProfile),
            null
        )


        ll_parent_gen_id!!.visibility = View.GONE


        patientName!!.visibility = View.GONE
        nameEditLayout!!.visibility = View.VISIBLE
        nameEdit!!.setText(patientModel!!.name)
        patientEmail!!.visibility = View.GONE
        emailEditLayout!!.visibility = View.VISIBLE
        emailEdit!!.setText(patientModel!!.email)
        patientPhone!!.visibility = View.GONE
        phoneEditLayout!!.visibility = View.VISIBLE
        phoneEdit!!.setText(patientModel!!.phone)
        patientAge!!.visibility = View.GONE
        ageEditLayout!!.visibility = View.VISIBLE
        ageEdit!!.setText(patientModel!!.age)
        patient_age_spin!!.setSelection(AppConstants.ageTypeList.indexOf(patientModel!!.age_type))
        patientHeight!!.visibility = View.GONE
        heightEditLayout!!.visibility = View.VISIBLE
        heightEdit!!.setText(patientModel!!.height)
        patientGender!!.visibility = View.GONE
        genderEditLayout!!.visibility = View.VISIBLE
        genderEdit!!.setSelection(patientModel!!.gender)
        patientBloodGroup!!.visibility = View.GONE
        bloodGroupEditLayout!!.visibility = View.VISIBLE
        bloodGroupEdit!!.setSelection(AppConstants.bloodGroupList.indexOf(patientModel!!.blood_group))
    }

    /**
     * Validating user input and making request obj
     */
    private fun makeRequestObj(): Boolean {
        var noError = true
        nameError!!.visibility = View.GONE
        emailError!!.visibility = View.GONE
        phoneError!!.visibility = View.GONE
        if (nameEdit!!.text.toString() == "") {
            nameError!!.visibility = View.VISIBLE
            noError = false
        }
        if (phoneEdit!!.text.toString() == "" || phoneEdit!!.text.length < 10) {
            phoneError!!.visibility = View.VISIBLE
            noError = false
        }
        if (!ageEdit!!.text.toString().isEmpty()) {
            val parseVale = ageEdit!!.text.toString().toDouble()
            when (patient_age_spin!!.selectedItem.toString()) {
                "Years" -> if (parseVale > 100.0) {
                    Toast.makeText(
                        activity,
                        "Maximum value that can be entered is 100 years",
                        Toast.LENGTH_LONG
                    ).show()
                    noError = false
                }
                "Months" -> if (parseVale > 1200.0) {
                    Toast.makeText(
                        activity,
                        "Maximum value that can be entered is 1200 months",
                        Toast.LENGTH_LONG
                    ).show()
                    noError = false
                }
                "Days" -> if (parseVale > 36500.0) {
                    Toast.makeText(
                        activity,
                        "Maximum value that can be entered is 36500 days",
                        Toast.LENGTH_LONG
                    ).show()
                    noError = false
                }
                else -> {}
            }
        }
        if (noError) {
            nameError!!.visibility = View.GONE
            emailError!!.visibility = View.GONE
            phoneError!!.visibility = View.GONE
            patientModel!!.name = nameEdit!!.text.toString()
            patientModel!!.email = emailEdit!!.text.toString()
            patientModel!!.phone = phoneEdit!!.text.toString()
            patientModel!!.age = ageEdit!!.text.toString()
            patientModel!!.age_type =
                AppConstants.ageTypeList[patient_age_spin!!.selectedItemPosition]
            patientModel!!.gender = genderEdit!!.selectedItemPosition
            patientModel!!.blood_group =
                AppConstants.bloodGroupList[bloodGroupEdit!!.selectedItemPosition]
            patientModel!!.height = heightEdit!!.text.toString()
            return true
        }
        return false
    }

    companion object {
        @JvmStatic
        fun newInstance(patientId: Int): EMRProfileTabFragment {
            val fragment = EMRProfileTabFragment()
            val args = Bundle()
            args.putInt("PatientID", patientId)
            fragment.arguments = args
            return fragment
        }
    }
}