package com.whitecoats.clinicplus.activities

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.ViewModelProvider
import com.whitecoats.clinicplus.R
import com.whitecoats.clinicplus.apis.ApiUrls
import com.whitecoats.clinicplus.constants.AppConstants
import com.whitecoats.clinicplus.utils.AppUtilities
import com.whitecoats.clinicplus.utils.ErrorHandlerClass.errorHandler
import com.whitecoats.clinicplus.viewmodels.EMRPatientProfileViewModel
import org.json.JSONArray
import org.json.JSONObject
import java.util.*

class EMRAddFamilyActivity : AppCompatActivity() {
    private var personName: EditText? = null
    private var personAge: EditText? = null
    private var personRelation: Spinner? = null
    private var addMember: Button? = null
    private var checkBoxes: ArrayList<CheckBox>? = null
    private var selectedRelation = ""
    private val param = JSONObject()
    private var progressDialog: ProgressDialog? = null
    private var nameError: TextView? = null
    private var ageError: TextView? = null
    private var relationError: TextView? = null
    private var patientProfileViewModel: EMRPatientProfileViewModel? = null
    private var patientID = 0
    private var patient_age_spin: Spinner? = null
    private val age_type = arrayOf("Years", "Months", "Days")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_e_m_r_add_family)
        patientID = intent.getIntExtra("PatientID", 0)
        initView()
        addMember!!.setOnClickListener { view: View? ->
            if (validateInput()) {
                progressDialog!!.setMessage("Adding Family Member")
                progressDialog!!.show()
                patientProfileViewModel!!.addFamilyData(this, param).observe(this) { s: String? ->
                    progressDialog!!.dismiss()
                    try {
                        val resObj = JSONObject(s)
                        if (resObj.getInt("status_code") != 200) {
                            errorHandler(this@EMRAddFamilyActivity, s!!)
                        } else {
                            val intent = Intent()
                            intent.action = "Get_Family_Updated_Data"
                            sendBroadcast(intent)
                            Toast.makeText(
                                this@EMRAddFamilyActivity,
                                "Data Updated Successfully",
                                Toast.LENGTH_SHORT
                            ).show()
                            finish()
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        }
        personRelation!!.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>, view: View, i: Int, l: Long) {
                selectedRelation = adapterView.getItemAtPosition(i) as String
            }

            override fun onNothingSelected(adapterView: AdapterView<*>?) {}
        }
    }

    private fun initView() {
        personName = findViewById(R.id.personName)
        personAge = findViewById(R.id.personAge)
        personRelation = findViewById(R.id.personRelation)
        addMember = findViewById(R.id.addFamilyBtn)
        nameError = findViewById(R.id.personNameErrorText)
        ageError = findViewById(R.id.personAgeErrorText)
        relationError = findViewById(R.id.personRelationErrorText)
        val toolbar = findViewById<Toolbar>(R.id.addFamilyToolbar)
        progressDialog = ProgressDialog(this)
        patient_age_spin = findViewById(R.id.patient_age_spin)

        //setting up back button on toolbar
        setSupportActionBar(toolbar)
        Objects.requireNonNull(supportActionBar)?.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        val upArrow = AppUtilities.changeIconColor(
            Objects.requireNonNull(
                ResourcesCompat.getDrawable(
                    resources, R.drawable.ic_arrow_back, theme
                )
            ),
            this, R.color.colorWhite
        )
        supportActionBar!!.setHomeAsUpIndicator(upArrow)
        checkBoxes = ArrayList()
        checkBoxes!!.add(findViewById(R.id.cancer))
        checkBoxes!!.add(findViewById(R.id.cholesterol))
        checkBoxes!!.add(findViewById(R.id.diabetes1))
        checkBoxes!!.add(findViewById(R.id.diabetes2))
        checkBoxes!!.add(findViewById(R.id.hairloss))
        checkBoxes!!.add(findViewById(R.id.heartCondition))
        checkBoxes!!.add(findViewById(R.id.hypertension))
        checkBoxes!!.add(findViewById(R.id.infertility))
        checkBoxes!!.add(findViewById(R.id.neurological))
        patientProfileViewModel = ViewModelProvider(this).get(
            EMRPatientProfileViewModel::class.java
        )
        patientProfileViewModel!!.init()
        AppConstants.familyRelation.clear()
        AppConstants.familyRelation.add("Select A Relation")
        for (i in 0 until AppConstants.relationMaster.length()) {
            try {
                AppConstants.familyRelation.add(
                    AppConstants.relationMaster.getJSONObject(i).getString("relation_name")
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        val dataAdapter =
            ArrayAdapter(this, android.R.layout.simple_spinner_item, AppConstants.familyRelation)
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        personRelation?.adapter = dataAdapter
        val patientAgeTypeAdapter =
            ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, age_type)
        patientAgeTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        patient_age_spin?.adapter = patientAgeTypeAdapter
        patient_age_spin?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?, view: View, position: Int, id: Long
            ) {
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun validateInput(): Boolean {
        var noError = true
        nameError!!.visibility = View.GONE
        ageError!!.visibility = View.GONE
        relationError!!.visibility = View.GONE
        if (personName!!.text.toString() == "") {
            noError = false
            nameError!!.visibility = View.VISIBLE
        }
        if (personAge!!.text.toString() == "") {
            noError = false
            ageError!!.visibility = View.VISIBLE
        }
        if (personAge!!.text != null && !personAge!!.text.toString().isEmpty()) {
            val parseVale = personAge!!.text.toString().toDouble()
            if (patient_age_spin!!.selectedItem.toString().equals("Years", ignoreCase = true)) {
                if (parseVale > 100.0) {
                    noError = false
                    Toast.makeText(
                        applicationContext,
                        "Maximum value that can be entered is 100 years",
                        Toast.LENGTH_LONG
                    ).show()
                }
            } else if (patient_age_spin!!.selectedItem.toString()
                    .equals("Months", ignoreCase = true)
            ) {
                if (parseVale > 1200.0) {
                    noError = false
                    Toast.makeText(
                        applicationContext,
                        "Maximum value that can be entered is 1200 months",
                        Toast.LENGTH_LONG
                    ).show()
                }
            } else if (patient_age_spin!!.selectedItem.toString()
                    .equals("Days", ignoreCase = true)
            ) {
                if (parseVale > 36500.0) {
                    noError = false
                    Toast.makeText(
                        applicationContext,
                        "Maximum value that can be entered is 36500 days",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
        if (personRelation!!.selectedItemPosition == 0) {
            noError = false
            relationError!!.visibility = View.VISIBLE
        }
        if (noError) {
            try {
                param.put("doctor_id", ApiUrls.doctorId)
                param.put("patient_id", patientID)
                param.put("relativename", personName!!.text)
                param.put("age", personAge!!.text)
                param.put("age_type", patient_age_spin!!.selectedItem.toString())
                for (i in 0 until AppConstants.relationMaster.length()) {
                    val temp = AppConstants.relationMaster.getJSONObject(i)
                    if (temp.getString("relation_name") == selectedRelation) {
                        param.put("relationid", temp.getInt("id"))
                        break
                    }
                }
                val problem = ArrayList<Int?>()
                for (i in checkBoxes!!.indices) {
                    val temp = checkBoxes!![i]
                    if (temp.isChecked) {
                        problem.add(i + 1)
                    }
                }
                param.put("problemids", JSONArray(problem))
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return noError
    }

    override fun onBackPressed() {
        super.getOnBackPressedDispatcher().onBackPressed()
        finish()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}