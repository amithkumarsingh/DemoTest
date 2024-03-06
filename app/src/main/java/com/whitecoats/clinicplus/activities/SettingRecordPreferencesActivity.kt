package com.whitecoats.clinicplus.activities

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.PorterDuff
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.google.android.material.switchmaterial.SwitchMaterial
import com.whitecoats.adapter.SettingObservationListAdapter
import com.whitecoats.adapter.SettingTreatmentPlanListAdapter
import com.whitecoats.clinicplus.MyClinicGlobalClass
import com.whitecoats.clinicplus.R
import com.whitecoats.clinicplus.VolleyCallback
import com.whitecoats.clinicplus.apis.ApiGetPostMethodCalls
import com.whitecoats.clinicplus.apis.ApiUrls
import com.whitecoats.clinicplus.utils.ErrorHandlerClass.errorHandler
import com.whitecoats.model.SettingObservationModel
import org.json.JSONException
import org.json.JSONObject

class SettingRecordPreferencesActivity : AppCompatActivity() {
    private lateinit var jsonValue: JSONObject
    var globalClass: MyClinicGlobalClass? = null
    private lateinit var settingRecordWriteNoteSwitch: SwitchMaterial
    private lateinit var settingRecordEvalSwitch: SwitchMaterial
    private lateinit var settingRecordTPlanSwitch: SwitchMaterial
    private lateinit var symptomsSwitch: SwitchMaterial
    private lateinit var investigationResultSwitch: SwitchMaterial
    private lateinit var diagnosisSwitch: SwitchMaterial
    private lateinit var observationSwitch: SwitchMaterial
    private lateinit var settingObsRecycleView: RecyclerView
    private var settingObserModelList: MutableList<SettingObservationModel>? = null
    private var settingObservListAdapter: SettingObservationListAdapter? = null

    private var isEvalSymptoms = false
    private var isEvalObservation: Boolean = false
    private var isEvalInvestigation: Boolean = false
    private var isEvalDiagnosis: Boolean = false
    private var isFromGetEvaluationResponse = false
    private var isFromGetHandNotesResponse: Boolean = false
    private var isFromGetTPResponse: Boolean = false
    private lateinit var evaluationChildLayout: LinearLayout
    private lateinit var settingTreatmentRecycleView: RecyclerView
    private var settingTreatmentModelList: MutableList<SettingObservationModel>? = null
    private var settingTreatmentListAdapter: SettingTreatmentPlanListAdapter? = null
    private lateinit var mContext: Context
    private var rootObjGetRecordPref: JSONObject? = null
    private var globalApiCall: ApiGetPostMethodCalls? = null
    private lateinit var dialog: AlertDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.setting_record_preferences_activity)
        val toolbar = findViewById<Toolbar>(R.id.settingFormToolbar)
        toolbar.title = intent.getStringExtra("Title")
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        val upArrow = ContextCompat.getDrawable(this, R.drawable.ic_arrow_back)
        upArrow?.setColorFilter(ContextCompat.getColor(this, R.color.colorWhite), PorterDuff.Mode.SRC_ATOP)
        supportActionBar!!.setHomeAsUpIndicator(upArrow)
        globalClass = applicationContext as MyClinicGlobalClass
        mContext = applicationContext
        settingObserModelList = ArrayList()
        globalApiCall = ApiGetPostMethodCalls()
        settingObsRecycleView = findViewById(R.id.settingObsRecycleView)
        settingObsRecycleView.isNestedScrollingEnabled = false

        toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        settingTreatmentModelList = ArrayList()
        settingTreatmentRecycleView = findViewById(R.id.settingTPlanRecycleView)

        evaluationChildLayout = findViewById(R.id.evaluationChildLayout)

        settingRecordWriteNoteSwitch = findViewById(R.id.settingRecordWriteNoteSwitch)
        settingRecordEvalSwitch = findViewById(R.id.settingRecordEvalSwitch)
        settingRecordTPlanSwitch = findViewById(R.id.settingRecordTPlanSwitch)

        symptomsSwitch = findViewById(R.id.symptomsSwitch)
        investigationResultSwitch = findViewById(R.id.investigationResultSwitch)
        diagnosisSwitch = findViewById(R.id.diagnosisSwitch)
        observationSwitch = findViewById(R.id.observationSwitch)

        settingRecordWriteNoteSwitch.setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener { _, _ ->
            if (isFromGetHandNotesResponse) {
                isFromGetHandNotesResponse = false
                return@OnCheckedChangeListener
            }
            upDateRecordPrefrences()
        })

        settingRecordEvalSwitch.setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                evaluationChildLayout.visibility = View.VISIBLE
            } else {
                evaluationChildLayout.visibility = View.GONE
            }
            if (isFromGetEvaluationResponse) {
                isFromGetEvaluationResponse = false
                return@OnCheckedChangeListener
            }
            upDateRecordPrefrences()
        })
        settingRecordTPlanSwitch.setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                (settingTreatmentModelList as ArrayList<SettingObservationModel>).clear()
                settingTreatmentRecycleView.visibility = View.VISIBLE
                getDoctorTreatmentCategory()
            } else {
                settingTreatmentRecycleView.visibility = View.GONE
            }
            if (isFromGetTPResponse) {
                isFromGetTPResponse = false
                return@OnCheckedChangeListener
            }
            upDateRecordPrefrences()
        })

        symptomsSwitch.setOnCheckedChangeListener { _, _ ->
            if (isEvalSymptoms) {
                isEvalSymptoms = false
            } else {
                upDateRecordPrefrencesEvaluation()
            }
        }

        investigationResultSwitch.setOnCheckedChangeListener { _, _ ->
            if (isEvalInvestigation) {
                isEvalInvestigation = false
            } else {
                upDateRecordPrefrencesEvaluation()
            }
        }

        diagnosisSwitch.setOnCheckedChangeListener { _, _ ->
            if (isEvalDiagnosis) {
                isEvalDiagnosis = false
            } else {
                upDateRecordPrefrencesEvaluation()
            }
        }

        observationSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (isEvalObservation) {
                isEvalObservation = false
            } else {
                if (isChecked) {
                    (settingObserModelList as ArrayList<SettingObservationModel>).clear()
                    settingObsRecycleView.visibility = View.VISIBLE
                    upDateRecordPrefrencesEvaluation()
                    getDoctorObservationCategory()
                } else {
                    upDateRecordPrefrencesEvaluation()
                    settingObsRecycleView.visibility = View.GONE
                }
            }
        }
        getSettingRecordPref()
    }

    private fun updateSettingValues() {
        val doctorDetailsIntent = Intent()
        doctorDetailsIntent.action = "Update_Doctor_Details_Settings"
        sendBroadcast(doctorDetailsIntent)
    }


    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        super.getOnBackPressedDispatcher().onBackPressed()
        updateSettingValues()
    }

    fun trimMessage(json: String, key: String): String? {
        val trimmedString: String = try {
            val obj = JSONObject(json)
            obj.getString(key)
        } catch (e: JSONException) {
            e.printStackTrace()
            return null
        }
        return trimmedString
    }

    //Somewhere that has access to a context
    fun displayMessage(toastString: String?) {
        Toast.makeText(applicationContext, toastString, Toast.LENGTH_LONG).show()
    }


    private fun upDateRecordPrefrences() {
        val url = ApiUrls.saveEpisodicFieldPref
        try {
            jsonValue = JSONObject()
            if (settingRecordWriteNoteSwitch.isChecked) {
                jsonValue.put("written_notes", true)
            } else {
                jsonValue.put("written_notes", false)
            }
            if (settingRecordEvalSwitch.isChecked) {
                jsonValue.put("evaluations", true)
            } else {
                jsonValue.put("evaluations", false)
            }
            if (settingRecordTPlanSwitch.isChecked) {
                jsonValue.put("treatment_plan", true)
            } else {
                jsonValue.put("treatment_plan", false)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        globalApiCall!!.volleyApiRequestData(url, Request.Method.POST, jsonValue, this, object : VolleyCallback {
            override fun onSuccess(result: String) {
                try {
                    val response = JSONObject(result)
                    val myJson = JSONObject(response.toString())
                    val responseValue = myJson.optInt("response")
                    Log.d("response", "" + responseValue)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            override fun onError(err: String) {
                errorHandler(this@SettingRecordPreferencesActivity, err)
            }
        })
    }

    private fun getSettingRecordPrefEvaluation() {
        val url = ApiUrls.getEvaluationControlPref
        globalApiCall!!.volleyApiRequestData(url, Request.Method.GET, null, this, object : VolleyCallback {
            override fun onSuccess(result: String) {
                try {
                    val response = JSONObject(result)
                    val userArr = response.getJSONArray("response")
                    for (i in 0 until userArr.length()) {
                        val tempobj = userArr.getJSONObject(i)
                        if (tempobj.getInt("component_id") == 1) {
                            if (tempobj.getInt("status") == 1) {
                                isEvalSymptoms = true
                                symptomsSwitch.isChecked = true
                            } else {
                                isEvalSymptoms = false
                                symptomsSwitch.isChecked = false
                            }
                        } else if (tempobj.getInt("component_id") == 2) {
                            if (tempobj.getInt("status") == 1) {
                                isEvalObservation = true
                                observationSwitch.isChecked = true
                                settingObserModelList?.clear()
                                settingObsRecycleView.visibility = View.VISIBLE
                                getDoctorObservationCategory()
                            } else {
                                isEvalObservation = false
                                observationSwitch.isChecked = false
                                settingObsRecycleView.visibility = View.GONE
                            }
                        } else if (tempobj.getInt("component_id") == 3) {
                            if (tempobj.getInt("status") == 1) {
                                isEvalInvestigation = true
                                investigationResultSwitch.isChecked = true
                            } else {
                                isEvalInvestigation = false
                                investigationResultSwitch.isChecked = false
                            }
                        } else if (tempobj.getInt("component_id") == 4) {
                            if (tempobj.getInt("status") == 1) {
                                isEvalDiagnosis = true
                                diagnosisSwitch.isChecked = true
                            } else {
                                isEvalDiagnosis = false
                                diagnosisSwitch.isChecked = false
                            }
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            override fun onError(err: String) {
                errorHandler(this@SettingRecordPreferencesActivity, err)
            }
        })
    }

    private fun upDateRecordPrefrencesEvaluation() {
        val url = ApiUrls.saveEvaluationControlPref
        try {
            jsonValue = JSONObject()
            if (symptomsSwitch.isChecked) {
                jsonValue.put("symptoms", true)
            } else {
                jsonValue.put("symptoms", false)
            }
            if (investigationResultSwitch.isChecked) {
                jsonValue.put("investigation_results", true)
            } else {
                jsonValue.put("investigation_results", false)
            }
            if (diagnosisSwitch.isChecked) {
                jsonValue.put("diagnosis", true)
            } else {
                jsonValue.put("diagnosis", false)
            }
            if (observationSwitch.isChecked) {
                jsonValue.put("observations", true)
            } else {
                jsonValue.put("observations", false)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        globalApiCall!!.volleyApiRequestData(url, Request.Method.POST, jsonValue, this, object : VolleyCallback {
            override fun onSuccess(result: String) {
                try {
                    val response = JSONObject(result)
                    val myJson = JSONObject(response.toString())
                    val responseValue = myJson.optInt("response")
                    Log.d("response", "" + responseValue)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            override fun onError(err: String) {
                errorHandler(this@SettingRecordPreferencesActivity, err)
            }
        })
    }

    fun getDoctorObservationCategory() {
        val url = ApiUrls.getDoctorObservationCategory
        globalApiCall!!.volleyApiRequestData(url, Request.Method.GET, null, this, object : VolleyCallback {
            override fun onSuccess(result: String) {
                try {
                    val response = JSONObject(result)
                    settingObserModelList!!.clear()
                    val userArr = response.getJSONArray("response")
                    for (i in 0 until userArr.length()) {
                        val tempobj = userArr.getJSONObject(i)
                        val cateogory = tempobj.getString("category")
                        val name = tempobj.getString("name")
                        val settingObserModel = SettingObservationModel()
                        settingObserModel.category = cateogory
                        settingObserModel.name = name
                        settingObserModel.categoryStatus = false
                        settingObserModelList!!.add(settingObserModel)
                    }
                    getAllObservationCategory()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            override fun onError(err: String) {
                errorHandler(this@SettingRecordPreferencesActivity, err)
            }
        })
    }

    fun getAllObservationCategory() {
        val url = ApiUrls.getAllObservationCategory
        globalApiCall!!.volleyApiRequestData(url, Request.Method.GET, null, this, object : VolleyCallback {
            override fun onSuccess(result: String) {
                try {
                    val response = JSONObject(result)
                    val userArr = response.getJSONArray("response")
                    for (i in 0 until userArr.length()) {
                        val tempobj = userArr.getJSONObject(i)
                        val cateogory = tempobj.getString("category")
                        val name = tempobj.getString("name")
                        val settingObserModel = SettingObservationModel()
                        settingObserModel.category = cateogory
                        settingObserModel.name = name
                        settingObserModel.categoryStatus = true
                        settingObserModelList!!.add(settingObserModel)
                    }
                    settingObservListAdapter = SettingObservationListAdapter(settingObserModelList, mContext) { _, _, categoryStatus, categoryId, name ->
                        upDateRecordPrefrences(categoryId, categoryStatus, name)
                    }
                    settingObsRecycleView.layoutManager = LinearLayoutManager(applicationContext, LinearLayoutManager.VERTICAL, false)
                    settingObsRecycleView.itemAnimator = DefaultItemAnimator()
                    settingObsRecycleView.adapter = settingObservListAdapter
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            override fun onError(err: String) {
                errorHandler(this@SettingRecordPreferencesActivity, err)
            }
        })
    }

    fun getDoctorTreatmentCategory() {
        val url = ApiUrls.getDoctorTreatmentCategory
        globalApiCall!!.volleyApiRequestData(url, Request.Method.GET, null, this, object : VolleyCallback {
            override fun onSuccess(result: String) {
                try {
                    val response = JSONObject(result)
                    settingTreatmentModelList!!.clear()
                    val userArr = response.getJSONArray("response")
                    for (i in 0 until userArr.length()) {
                        val tempobj = userArr.getJSONObject(i)
                        val cateogory = tempobj.getString("category")
                        val name = tempobj.getString("name")
                        val settingObserModel = SettingObservationModel()
                        settingObserModel.category = cateogory
                        settingObserModel.name = name
                        settingObserModel.categoryStatus = false
                        settingTreatmentModelList!!.add(settingObserModel)
                    }
                    getAllTreatmentCategory()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            override fun onError(err: String) {
                errorHandler(this@SettingRecordPreferencesActivity, err)
            }
        })
    }

    fun getAllTreatmentCategory() {
        val url = ApiUrls.getAllTreatmentCategory
        globalApiCall!!.volleyApiRequestData(url, Request.Method.GET, null, this, object : VolleyCallback {
            override fun onSuccess(result: String) {
                try {
                    val response = JSONObject(result)
                    val userArr = response.getJSONArray("response")
                    for (i in 0 until userArr.length()) {
                        val tempobj = userArr.getJSONObject(i)
                        val cateogory = tempobj.getString("category")
                        val name = tempobj.getString("name")
                        val settingObserModel = SettingObservationModel()
                        settingObserModel.category = cateogory
                        settingObserModel.name = name
                        settingObserModel.categoryStatus = true
                        settingTreatmentModelList!!.add(settingObserModel)
                    }
                    settingTreatmentListAdapter = SettingTreatmentPlanListAdapter(settingTreatmentModelList, mContext) { _, _, categoryStatus, categoryId, name -> upDateTreatmentPlanPrefrences(categoryId, categoryStatus, name) }
                    settingTreatmentRecycleView.layoutManager = LinearLayoutManager(applicationContext, LinearLayoutManager.VERTICAL, false)
                    settingTreatmentRecycleView.itemAnimator = DefaultItemAnimator()
                    settingTreatmentRecycleView.adapter = settingTreatmentListAdapter
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            override fun onError(err: String) {
                errorHandler(this@SettingRecordPreferencesActivity, err)
            }
        })
    }

    fun upDateRecordPrefrences(categoryId: String?, categoryStatus: Boolean, categoryName: String?) {
        val url = ApiUrls.saveObservationCategory
        try {
            jsonValue = JSONObject()
            jsonValue.put("category_id", categoryId)
            if (categoryStatus) {
                jsonValue.put("action", "add")
            } else {
                jsonValue.put("action", "remove")
            }
            jsonValue.put("type", categoryName)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        globalApiCall!!.volleyApiRequestData(url, Request.Method.POST, jsonValue, this, object : VolleyCallback {
            override fun onSuccess(result: String) {
                try {
                    getDoctorObservationCategory()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            override fun onError(err: String) {
                errorHandler(this@SettingRecordPreferencesActivity, err)
            }
        })
    }

    fun upDateTreatmentPlanPrefrences(categoryId: String?, categoryStatus: Boolean, categoryName: String?) {
        val url = ApiUrls.saveObservationCategory
        try {
            jsonValue = JSONObject()
            jsonValue.put("category_id", categoryId)
            if (categoryStatus) {
                jsonValue.put("action", "add")
            } else {
                jsonValue.put("action", "remove")
            }
            jsonValue.put("type", categoryName)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        globalApiCall!!.volleyApiRequestData(url, Request.Method.POST, jsonValue, this, object : VolleyCallback {
            override fun onSuccess(result: String) {
                try {
                    val resObj = JSONObject(result)
                    Log.d("response", "" + resObj)
                    getDoctorTreatmentCategory()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            override fun onError(err: String) {
                errorHandler(this@SettingRecordPreferencesActivity, err)
            }
        })
    }

    //record prefrence
    private fun getSettingRecordPref() {
        showCustomProgressAlertDialog()
        val url = ApiUrls.getEpisodicFieldPref
        globalApiCall!!.volleyApiRequestData(url, Request.Method.GET, null, this, object : VolleyCallback {
            override fun onSuccess(result: String) {
                try {
                    val response = JSONObject(result)
                    dialog.dismiss()
                    rootObjGetRecordPref = response.optJSONObject("response")
                    val userArr = response.getJSONArray("response")
                    for (i in 0 until userArr.length()) {
                        val tempobj = userArr.getJSONObject(i)
                        if (tempobj.getInt("component_id") == 1) {
                            if (tempobj.getInt("status") == 1) {
                                isFromGetHandNotesResponse = true
                                settingRecordWriteNoteSwitch.isChecked = true
                            } else {
                                settingRecordWriteNoteSwitch.isChecked = false
                            }
                        } else if (tempobj.getInt("component_id") == 2) {
                            if (tempobj.getInt("status") == 1) {
                                isFromGetEvaluationResponse = true
                                settingRecordEvalSwitch.isChecked = true
                                evaluationChildLayout.visibility = View.VISIBLE
                                getSettingRecordPrefEvaluation()
                            } else {
                                settingRecordEvalSwitch.isChecked = false
                                evaluationChildLayout.visibility = View.GONE
                            }
                        } else if (tempobj.getInt("component_id") == 3) {
                            if (tempobj.getInt("status") == 1) {
                                isFromGetTPResponse = true
                                settingRecordTPlanSwitch.isChecked = true
                                settingTreatmentRecycleView.visibility = View.VISIBLE
                            } else {
                                settingRecordTPlanSwitch.isChecked = false
                                settingTreatmentRecycleView.visibility = View.GONE
                            }
                        }
                    }
                } catch (e: Exception) {
                    dialog.dismiss()
                    e.printStackTrace()
                }
            }

            override fun onError(err: String) {
                dialog.dismiss()
                errorHandler(this@SettingRecordPreferencesActivity, err)
            }
        })
    }

    private fun showCustomProgressAlertDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Fetching")
        builder.setCancelable(false)
        val customLayout: View = layoutInflater.inflate(R.layout.custom_progress_bar, null)
        builder.setView(customLayout)
        dialog = builder.create()
        dialog.show()
    }


}