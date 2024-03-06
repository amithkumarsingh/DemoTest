package com.whitecoats.clinicplus.activities

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.graphics.PorterDuff
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnFocusChangeListener
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.whitecoats.clinicplus.AppUtilities
import com.whitecoats.clinicplus.MyClinicGlobalClass
import com.whitecoats.clinicplus.R
import com.whitecoats.clinicplus.apis.ApiUrls
import com.whitecoats.clinicplus.utils.ErrorHandlerClass.errorHandler
import com.whitecoats.clinicplus.viewmodels.AddPatientViewModel
import com.whitecoats.model.AddPatientModel
import com.zoho.salesiqembed.ZohoSalesIQ
import org.json.JSONException
import org.json.JSONObject
import java.util.*


class AddPatientActivity : AppCompatActivity() {
    private lateinit var patientGender: Spinner
    private lateinit var patientType: Spinner
    private lateinit var patientInterface: Spinner
    private lateinit var patient_age_spin: Spinner
    private var patientGenderAdapter: ArrayAdapter<*>? = null
    private var patientTypeAdapter: ArrayAdapter<*>? = null
    private var patientInterfaceAdapter: ArrayAdapter<*>? = null
    private var patientAgeTypeAdapter: ArrayAdapter<*>? = null
    private var toolbar: Toolbar? = null
    private var addPatientViewModel: AddPatientViewModel? = null
    private var addInterfaceModelList: MutableList<AddPatientModel>? = null
    private var interFaceId = 0
    private lateinit var savePatient: Button
    private lateinit var intentObj: Intent
    private lateinit var patientName: EditText
    private lateinit var patientPhoneNumber: EditText
    private lateinit var patientEmailAddress: EditText
    private lateinit var patientAge: EditText
    private lateinit var patientId: EditText
    private lateinit var patientCategory: EditText
    private var globalClass: MyClinicGlobalClass? = null
    private val gender = arrayOf<String?>("Select Gender", "Male", "Female")
    private val role = arrayOf<String?>("Select Patient Type", "Registered", "Internal")
    //private val phoneregex = "^[0-9]{10}$"
    private val age_type = arrayOf<String?>("Years", "Months", "Days")
    private var appUtilities: AppUtilities? = null
    private lateinit var patientTypeText: TextView
    private lateinit var enterIdText: TextView
    private lateinit var patientTypeCard: CardView
    private lateinit var enterIdCard: CardView
    private  var selectedInterfacePos = 0
    private lateinit var dialog: Dialog
    private lateinit var selectInterfaceDownArrow: ImageView
    private lateinit var enterInterfaceText: TextView
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_patient)
        addPatientViewModel = ViewModelProvider(this@AddPatientActivity)[AddPatientViewModel::class.java]
        intentObj = intent
        ApiUrls.bottomNaviType = 0
        addPatientViewModel!!.init()
        patientGender = findViewById(R.id.patient_gender)
        patient_age_spin = findViewById(R.id.patient_age_spin)
        patientType = findViewById(R.id.patient_type)
        patientInterface = findViewById(R.id.patient_interface)
        toolbar = findViewById(R.id.addPatientToolbar)
        savePatient = findViewById(R.id.save_patient)
        patientName = findViewById(R.id.patient_name)
        patientPhoneNumber = findViewById(R.id.patient_contact)
        patientEmailAddress = findViewById(R.id.patient_email)
        patientAge = findViewById(R.id.patient_age)
        patientId = findViewById(R.id.patient_id)
        patientCategory = findViewById(R.id.patient_category)
        patientTypeText = findViewById(R.id.patient_type_text)
        enterIdText = findViewById(R.id.enter_id_text)
        patientTypeCard = findViewById(R.id.patient_type_card)
        enterIdCard = findViewById(R.id.enter_id_card)

        selectInterfaceDownArrow = findViewById(R.id.selectInterfaceDownArrow)
        enterInterfaceText = findViewById(R.id.enter_interface_text)

        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        val drawable = ContextCompat.getDrawable(this, R.drawable.ic_arrow_back)
        if (drawable != null) {
            drawable.setColorFilter(
                ContextCompat.getColor(this,R.color.colorWhite),
                PorterDuff.Mode.SRC_ATOP
            )
            supportActionBar!!.setHomeAsUpIndicator(drawable)
        }
        globalClass = applicationContext as MyClinicGlobalClass
        appUtilities = AppUtilities()
        if (!TextUtils.isEmpty(intentObj.getStringExtra("name"))) {
            val value = intentObj.getStringExtra("name")
            if (isNumeric(value)) {
                patientPhoneNumber.setText(value)
            } else {
                patientName.setText(value)
            }
        }
        addInterfaceModelList = ArrayList()
        patientGenderAdapter =
            ArrayAdapter<Any?>(this, android.R.layout.simple_spinner_dropdown_item, gender)
        patientGenderAdapter!!.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        patientGender.adapter = patientGenderAdapter
        patientAgeTypeAdapter =
            ArrayAdapter<Any?>(this, android.R.layout.simple_spinner_dropdown_item, age_type)
        patientAgeTypeAdapter!!.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        patient_age_spin.adapter = patientAgeTypeAdapter
        patient_age_spin.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?, view: View, position: Int, id: Long
            ) {
                Log.i("itemClicked","itemClecked")
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }
        patientTypeAdapter =
            ArrayAdapter<Any?>(this, android.R.layout.simple_spinner_dropdown_item, role)
        patientTypeAdapter!!.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        patientType.adapter = patientTypeAdapter
        patientInterfaceAdapter = ArrayAdapter(
            applicationContext,
            android.R.layout.simple_spinner_dropdown_item,
            addInterfaceModelList!!
        )
        patientInterfaceAdapter!!.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        patientInterface.adapter = patientInterfaceAdapter
        patientInterface.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View,
                position: Int,
                id: Long
            ) {
                selectedInterfacePos = position
                if (addInterfaceModelList!![position].isAutoGenrateGeneralId.equals(
                        "0",
                        ignoreCase = true
                    )
                ) {
                    enterIdText.visibility = View.VISIBLE
                    enterIdCard.visibility = View.VISIBLE
                } else {
                    enterIdText.visibility = View.GONE
                    enterIdCard.visibility = View.GONE
                }
                if (addInterfaceModelList!![position].isAutoRegistered.equals(
                        "0",
                        ignoreCase = true
                    )
                ) {
                    patientTypeText.visibility = View.VISIBLE
                    patientTypeCard.visibility = View.VISIBLE
                } else {
                    patientTypeText.visibility = View.GONE
                    patientTypeCard.visibility = View.GONE
                }
                interFaceId = addInterfaceModelList!![position].interfaceId
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
        savePatient.setOnClickListener {
            if (patientName.text.toString().isEmpty()) {
                patientName.error = "Name is required"
            } else if (patientPhoneNumber.text.toString().isEmpty()) {
                patientPhoneNumber.error = "Phone number is required"
            } else if (patientPhoneNumber.text.toString().length > 10) {
                patientPhoneNumber.error = "Please enter valid contact number"
            } else if (patientPhoneNumber.text.toString().length < 10) {
                patientPhoneNumber.error = "Please enter valid contact number"
            } else if ((patientAge.text != null) and patientAge.text.toString().isNotEmpty()
            ) {
                val parseVale = patientAge.text.toString().toDouble()
                if (patient_age_spin.selectedItem.toString()
                        .equals("Years", ignoreCase = true)
                ) {
                    if (parseVale > 100.0) {
                        Toast.makeText(
                            applicationContext,
                            "Maximum value that can be entered is 100 years",
                            Toast.LENGTH_LONG
                        ).show()
                    } else {
                        if (globalClass!!.isOnline) {
                            ZohoSalesIQ.Tracking.setCustomAction("Dashboard - Add Patient")
                            savePatientDetails()
                        } else {
                            globalClass!!.noInternetConnection.showDialog(this@AddPatientActivity)
                        }
                    }
                } else if (patient_age_spin.selectedItem.toString()
                        .equals("Months", ignoreCase = true)
                ) {
                    if (parseVale > 1200.0) {
                        Toast.makeText(
                            applicationContext,
                            "Maximum value that can be entered is 1200 months",
                            Toast.LENGTH_LONG
                        ).show()
                    } else {
                        if (globalClass!!.isOnline) {
                            ZohoSalesIQ.Tracking.setCustomAction("Dashboard - Add Patient")
                            savePatientDetails()
                        } else {
                            globalClass!!.noInternetConnection.showDialog(this@AddPatientActivity)
                        }
                    }
                } else if (patient_age_spin.selectedItem.toString()
                        .equals("Days", ignoreCase = true)
                ) {
                    if (parseVale > 36500.0) {
                        Toast.makeText(
                            applicationContext,
                            "Maximum value that can be entered is 36500 days",
                            Toast.LENGTH_LONG
                        ).show()
                    } else {
                        if (globalClass!!.isOnline) {
                            ZohoSalesIQ.Tracking.setCustomAction("Dashboard - Add Patient")
                            savePatientDetails()
                        } else {
                            globalClass!!.noInternetConnection.showDialog(this@AddPatientActivity)
                        }
                    }
                }
            } else {
                if (globalClass!!.isOnline) {
                    ZohoSalesIQ.Tracking.setCustomAction("Dashboard - Add Patient")
                    savePatientDetails()
                } else {
                    globalClass!!.noInternetConnection.showDialog(this@AddPatientActivity)
                }
            }
        }
        addPatientViewModel!!.getInterfaceDetails(this@AddPatientActivity)
            .observe(this) { s ->
                try {
                    val response = JSONObject(s).getJSONObject("response")
                    val rootObj = response.getJSONArray("response")
                    for (j in 0 until rootObj.length()) {
                        val appointmentJsonObject = rootObj.getJSONObject(j)
                        if (!appointmentJsonObject.isNull("parentinterf")) {
                            val parentInterfaceObject =
                                appointmentJsonObject.getJSONObject("parentinterf")
                            val model = AddPatientModel()
                            model.interfaceId = parentInterfaceObject.getInt("id")
                            model.interfaceName =
                                parentInterfaceObject.getString("interface_name")
                            model.isAutoGenrateGeneralId =
                                    parentInterfaceObject.getString("is_auto_genrate_general_id")
                                model.isAutoRegistered =
                                    parentInterfaceObject.getString("is_auto_registered")
                            addInterfaceModelList!!.add(model)
                        }
                    }
                    /*Android : Add patient screen changes*/
                    if (addInterfaceModelList!!.size > 1) {
                        enterInterfaceText.text = getString(R.string.select_patient_interface)
                        selectInterfaceDownArrow.visibility = View.VISIBLE
                        patientInterface.isClickable = true
                        patientInterface.isEnabled = true
                    } else {
                        enterInterfaceText.text = "Patient Interface"
                        selectInterfaceDownArrow.visibility = View.GONE
                        patientInterface.isClickable = false
                        patientInterface.isEnabled = false
                    }
                    patientInterfaceAdapter!!.notifyDataSetChanged()
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }
        patientName.onFocusChangeListener = OnFocusChangeListener { _, _ ->
            MyClinicGlobalClass.logUserActionEvent(
                ApiUrls.doctorId, getString(R.string.AddNewPatientPatientName),
                null
            )
        }
        patientPhoneNumber.onFocusChangeListener = OnFocusChangeListener { _, _ ->
            MyClinicGlobalClass.logUserActionEvent(
                ApiUrls.doctorId, getString(R.string.AddNewPatientPatientContact),
                null
            )
        }
        patientEmailAddress.onFocusChangeListener = OnFocusChangeListener { _, _ ->
            MyClinicGlobalClass.logUserActionEvent(
                ApiUrls.doctorId, getString(R.string.AddNewPatientPatientEmail),
                null
            )
        }
        patientAge.onFocusChangeListener = OnFocusChangeListener { _, _ ->
            MyClinicGlobalClass.logUserActionEvent(
                ApiUrls.doctorId, getString(R.string.AddNewPatientPatientAge),
                null
            )
        }
        patientId.onFocusChangeListener = OnFocusChangeListener { _, _ ->
            MyClinicGlobalClass.logUserActionEvent(
                ApiUrls.doctorId, getString(R.string.AddNewPatientPatientID),
                null
            )
        }
        patientGender.onFocusChangeListener = OnFocusChangeListener { _, _ ->
            MyClinicGlobalClass.logUserActionEvent(
                ApiUrls.doctorId, getString(R.string.AddNewPatientPatientID),
                null
            )
        }
        patientInterface.onFocusChangeListener = OnFocusChangeListener { _, _ ->
            MyClinicGlobalClass.logUserActionEvent(
                ApiUrls.doctorId, getString(R.string.AddNewPatientPatientInterface),
                null
            )
        }
        patientType.onFocusChangeListener = OnFocusChangeListener { _, _ ->
            MyClinicGlobalClass.logUserActionEvent(
                ApiUrls.doctorId, getString(R.string.AddNewPatientPatientType),
                null
            )
        }
        toolbar!!.setNavigationOnClickListener { finish() }
    }

    private fun isNumeric(value: String?): Boolean {
        return try {
            value!!.toLong()
            true
        } catch (e: Exception) {
            false
        }
    }

    private fun savePatientDetails() {
        showCustomProgressAlertDialog(resources.getString(R.string.updating),resources.getString(R.string.wait_while_we_updating))
        var jsonValue = JSONObject()
        try {
            jsonValue = JSONObject()
            jsonValue.put("name", patientName.text.toString().trim { it <= ' ' })
            jsonValue.put("phone", patientPhoneNumber.text.toString().trim { it <= ' ' })
            jsonValue.put("age", patientAge.text.toString().trim { it <= ' ' })
            jsonValue.put("age_type", patient_age_spin.selectedItem.toString())
            jsonValue.put("email", patientEmailAddress.text.toString().trim { it <= ' ' })
            jsonValue.put("gender", patientGender.selectedItemPosition)
            jsonValue.put("interface", interFaceId)
            jsonValue.put("category", patientCategory.text.toString())
            if (addInterfaceModelList!![selectedInterfacePos].isAutoGenrateGeneralId.equals(
                    "0",
                    ignoreCase = true
                )
            ) {
                jsonValue.put("generalid", patientId.text.toString())
            }
            if (addInterfaceModelList!![selectedInterfacePos].isAutoRegistered.equals(
                    "0",
                    ignoreCase = true
                )
            ) {
                jsonValue.put("type", patientType.selectedItem.toString())
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        addPatientViewModel!!.savePatient(this@AddPatientActivity, jsonValue)
            .observe(this@AddPatientActivity) { s ->
                dialog.dismiss()
                try {
                    val jsonObject = JSONObject(s)
                    if (jsonObject.getInt("status_code") == 200) {
                        MyClinicGlobalClass.logUserActionEvent(
                            ApiUrls.doctorId,
                            getString(R.string.AddNewPatientSavePatient),
                            null
                        )
                        Toast.makeText(
                            this@AddPatientActivity,
                            "Patient add successfully",
                            Toast.LENGTH_SHORT
                        ).show()
                        patientName.setText("")
                        patientPhoneNumber.setText("")
                        patientAge.setText("")
                        patientId.setText("")
                        patientEmailAddress.setText("")
                        patientCategory.setText("")
                        finish()
                        val intent = Intent()
                        // Here you can also put data on intent
                        intent.action = "PATIENT_LIST_REFRESH"
                        sendBroadcast(intent)
                    } else {
                        errorHandler(this@AddPatientActivity, s)
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }
    }
    fun showCustomProgressAlertDialog(
        title: String?,
        progressVal: String?

    ) {
        val builder = AlertDialog.Builder(this@AddPatientActivity)
        builder.setTitle(title)
        builder.setCancelable(false)
        val customLayout =
            LayoutInflater.from(this@AddPatientActivity)
                .inflate(R.layout.custom_progress_bar, null)
        builder.setView(customLayout)
        dialog = builder.create()
        val tvBody = customLayout.findViewById<TextView>(R.id.tv_value)
        tvBody.text = progressVal
        dialog.show()
    }

}