package com.whitecoats.clinicplus.casechannels

import android.content.Intent
import android.graphics.PorterDuff
import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.*
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.whitecoats.clinicplus.PatientRecordsApi
import com.whitecoats.clinicplus.R
import com.whitecoats.clinicplus.VolleyCallback
import com.whitecoats.clinicplus.apis.ApiUrls
import com.whitecoats.clinicplus.utils.ErrorHandlerClass.errorHandler
import com.whitecoats.model.PatientRecordsModel
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class CaseChannelRecordEvaluationDetailsActivity : AppCompatActivity() {
    lateinit var caseChannelRecordEvaluationDetailsRecyclerView: RecyclerView
    private var symptomAdapter: CaseChannelEvaluationAdapter? = null
    private var investAdapter: CaseChannelEvaluationAdapter? = null
    private var diagAdapter: CaseChannelEvaluationAdapter? = null
    private var observationAdapter: CaseChannelEvaluationAdapter? = null
    private var symptomsModel: MutableList<PatientRecordsModel>? = null
    private var investModel: MutableList<PatientRecordsModel>? = null
    private var diagModel: MutableList<PatientRecordsModel>? = null
    private var obsCatModel: MutableList<PatientRecordsModel>? = null
    private var episodeID = 0
    private var patientID = 0
    private var doctorID = 0
    private var duration = ""
    private var apiCall: PatientRecordsApi? = null
    private var typeID = 0
    private var doctorArrayHW: JSONArray? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_case_channel_record_evaluation_symptoms)
        typeID = intent.getIntExtra("type", 0)
        val backArrow = ContextCompat.getDrawable(this,R.drawable.ic_arrow_back)
        backArrow!!.setColorFilter(ContextCompat.getColor(this,R.color.colorWhite), PorterDuff.Mode.SRC_ATOP)
        val toolbar = findViewById<Toolbar>(R.id.caseChannelRecordEvaluationDetailsToolbar)
        toolbar.navigationIcon = backArrow // your drawable
        toolbar.setNavigationOnClickListener { finish() }
        apiCall = PatientRecordsApi()
        symptomsModel = ArrayList()
        investModel = ArrayList()
        diagModel = ArrayList()
        obsCatModel = ArrayList()

        //Intent  getIntent = new Intent();
        episodeID = intent.getIntExtra("caseChannelEpisodeId", 0)
        patientID = intent.getIntExtra("caseChannelPatientId", 0)
        doctorID = intent.getIntExtra("caseChannelDoctorId", 0)
        duration = "All"
        doctorArrayHW = CaseChannelDashboardActivity.doctorArray!!.put(doctorID)


//        episodeID = 378;
//        patientID = 32386;
//        doctorID = 2529;
//        duration = "All";
        caseChannelRecordEvaluationDetailsRecyclerView =
            findViewById(R.id.caseChannelRecordEvaluationDetailsRecyclerView)
        when (typeID) {
            1 -> {
                toolbar.title = "Symptoms"
                symptomAdapter = CaseChannelEvaluationAdapter(
                    this,
                    symptomsModel,
                    object : CaseDoctorOrganisationClickListener {
                        override fun onItemClick(
                            v: View,
                            position: Int,
                            selectState: String,
                            sortByString: String
                        ) {
                        }

                        override fun getFilters(
                            v: View,
                            position: Int,
                            selectState: String,
                            sortByString: String,
                            statusPos: Int,
                            sortPos: Int
                        ) {
                        }
                    })
                val mLayoutManager: RecyclerView.LayoutManager = LinearLayoutManager(
                    applicationContext
                )
                caseChannelRecordEvaluationDetailsRecyclerView.layoutManager = mLayoutManager
                val horizontalLayoutManager =
                    LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
                caseChannelRecordEvaluationDetailsRecyclerView.layoutManager =
                    horizontalLayoutManager
                caseChannelRecordEvaluationDetailsRecyclerView.itemAnimator = DefaultItemAnimator()
                caseChannelRecordEvaluationDetailsRecyclerView.adapter = symptomAdapter
            }
            2 -> {
                toolbar.title = "Observation"
                observationAdapter = CaseChannelEvaluationAdapter(
                    this,
                    obsCatModel,
                    object : CaseDoctorOrganisationClickListener {
                        override fun onItemClick(
                            v: View,
                            position: Int,
                            selectState: String,
                            sortByString: String
                        ) {
                            if (sortByString.equals("FILE_URL", ignoreCase = true)) {
                                getFileFromUrl(selectState)
                            }
                        }

                        override fun getFilters(
                            v: View,
                            position: Int,
                            selectState: String,
                            sortByString: String,
                            statusPos: Int,
                            sortPos: Int
                        ) {
                        }
                    })
                val mLayoutManager3: RecyclerView.LayoutManager = LinearLayoutManager(
                    applicationContext
                )
                caseChannelRecordEvaluationDetailsRecyclerView.layoutManager = mLayoutManager3
                val horizontalLayoutManager3 =
                    LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
                caseChannelRecordEvaluationDetailsRecyclerView.layoutManager =
                    horizontalLayoutManager3
                caseChannelRecordEvaluationDetailsRecyclerView.itemAnimator = DefaultItemAnimator()
                caseChannelRecordEvaluationDetailsRecyclerView.adapter = observationAdapter
                caseDiscussionObservations
            }
            3 -> {
                toolbar.title = "Investigation"
                investAdapter = CaseChannelEvaluationAdapter(
                    this,
                    investModel,
                    object : CaseDoctorOrganisationClickListener {
                        override fun onItemClick(
                            v: View,
                            position: Int,
                            selectState: String,
                            sortByString: String
                        ) {
                            if (sortByString !== "") {
                                getFileFromUrl(sortByString)
                            }
                        }

                        override fun getFilters(
                            v: View,
                            position: Int,
                            selectState: String,
                            sortByString: String,
                            statusPos: Int,
                            sortPos: Int
                        ) {
                        }
                    })
                val mLayoutManager1: RecyclerView.LayoutManager = LinearLayoutManager(
                    applicationContext
                )
                caseChannelRecordEvaluationDetailsRecyclerView.layoutManager = mLayoutManager1
                val horizontalLayoutManager1 =
                    LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
                caseChannelRecordEvaluationDetailsRecyclerView.layoutManager =
                    horizontalLayoutManager1
                caseChannelRecordEvaluationDetailsRecyclerView.itemAnimator = DefaultItemAnimator()
                caseChannelRecordEvaluationDetailsRecyclerView.adapter = investAdapter
            }
            4 -> {
                toolbar.title = "Diagnosis"
                diagAdapter = CaseChannelEvaluationAdapter(
                    this,
                    diagModel,
                    object : CaseDoctorOrganisationClickListener {
                        override fun onItemClick(
                            v: View,
                            position: Int,
                            selectState: String,
                            sortByString: String
                        ) {

                        }

                        override fun getFilters(
                            v: View,
                            position: Int,
                            selectState: String,
                            sortByString: String,
                            statusPos: Int,
                            sortPos: Int
                        ) {
                        }
                    })
                val mLayoutManager2: RecyclerView.LayoutManager = LinearLayoutManager(
                    applicationContext
                )
                caseChannelRecordEvaluationDetailsRecyclerView.layoutManager = mLayoutManager2
                val horizontalLayoutManager2 =
                    LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
                caseChannelRecordEvaluationDetailsRecyclerView.layoutManager =
                    horizontalLayoutManager2
                caseChannelRecordEvaluationDetailsRecyclerView.itemAnimator = DefaultItemAnimator()
                caseChannelRecordEvaluationDetailsRecyclerView.adapter = diagAdapter
            }
            else -> {}
        }
        setSupportActionBar(toolbar)
        caseDiscussionEvaluation
    }// JSONObject resObj = new JSONObject(result);//getting eval diagnosis//JSONObject resObj = new JSONObject(response);//getting investigation data// JSONObject resObj = new JSONObject(response);//getting symptoms data//            JSONArray doctorArray =new JSONArray();

    //            doctorArray.put(doctorID);
    // Post params to be sent to the server
    val caseDiscussionEvaluation: Unit
        get() {
            val requestQueue = Volley.newRequestQueue(this)
            val URL = ApiUrls.getCaseDiscussionEvaluation
            // Post params to be sent to the server
            val jsonObject = JSONObject()
            try {
                jsonObject.put("patient_id", patientID)
                jsonObject.put("episode_id", episodeID)
                jsonObject.put("duration", duration)
                jsonObject.put("doctor_id", doctorArrayHW)
            } catch (e: JSONException) {
                e.printStackTrace()
            }
            val request_json: JsonObjectRequest = object : JsonObjectRequest(URL, jsonObject,
                Response.Listener<JSONObject> { response ->
                    //getting symptoms data
                    if (typeID == 1) {
                        try {
                            // JSONObject resObj = new JSONObject(response);
                            symptomsModel!!.clear()
                            val symptomObj =
                                response.getJSONObject("response").getJSONObject("symptoms")
                            val recordsArr = symptomObj.getJSONArray("records")
                            if (recordsArr.length() > 0) {
                                for (i in 0 until recordsArr.length()) {
                                    val recObj = recordsArr.getJSONObject(i)
                                    val model = PatientRecordsModel()
                                    model.symptomName = recObj.getString("symptom_name")
                                    model.symptomFirstSeen =
                                        recObj.getString("first_reported_on")
                                    model.symptomStatus = recObj.getString("symptom_status")
                                    model.symptom_description =
                                        recObj.getString("symptom_description")
                                    model.createdUser = recObj.getJSONObject("created_by_user")
                                        .getString("fname")
                                    model.created_At = recObj.getString("created_at")
                                    model.type = 1
                                    symptomsModel!!.add(model)
                                }
                                symptomAdapter!!.notifyDataSetChanged()
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    } else if (typeID == 3) {
                        //getting investigation data
                        try {
                            investModel!!.clear()
                            //JSONObject resObj = new JSONObject(response);
                            val symptomObj = response.getJSONObject("response")
                                .getJSONObject("investigation_results")
                            val recordsArr = symptomObj.getJSONArray("records")
                            if (recordsArr.length() > 0) {
                                for (i in 0 until recordsArr.length()) {
                                    val recObj = recordsArr.getJSONObject(i)
                                    val model = PatientRecordsModel()
                                    model.investName = recObj.getString("investigation_name")
                                    model.investParam = recObj.getString("parameter")
                                    model.investValue = recObj.getString("value")
                                    model.investNote = recObj.getString("notes")
                                    model.fileUrl = recObj.getString("file_url")
                                    model.createdUser = recObj.getJSONObject("created_by_user")
                                        .getString("fname")
                                    model.created_At = recObj.getString("created_at")
                                    model.type = 3
                                    investModel!!.add(model)
                                }
                                investAdapter!!.notifyDataSetChanged()
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    } else if (typeID == 4) {

                        //getting eval diagnosis
                        try {
                            diagModel!!.clear()
                            // JSONObject resObj = new JSONObject(result);
                            val symptomObj =
                                response.getJSONObject("response").getJSONObject("diagnosis")
                            val recordsArr = symptomObj.getJSONArray("records")
                            if (recordsArr.length() > 0) {
                                for (i in 0 until recordsArr.length()) {
                                    val recObj = recordsArr.getJSONObject(i)
                                    val model = PatientRecordsModel()
                                    model.diagName = recObj.getString("diagnosis")
                                    model.diagPoisted = recObj.getString("posited_on")
                                    model.diagStatus = recObj.getString("status")
                                    model.diagConfirmed =
                                        recObj.getString("confirmed_ruledout_on")
                                    model.createdUser = recObj.getJSONObject("created_by_user")
                                        .getString("fname")
                                    model.created_At = recObj.getString("created_at")
                                    model.type = 4
                                    diagModel!!.add(model)
                                }
                                diagAdapter!!.notifyDataSetChanged()
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }, object : Response.ErrorListener {
                    override fun onErrorResponse(error: VolleyError) {
                        VolleyLog.e("Error: ", error.message)
                    }
                }) {
                @Throws(AuthFailureError::class)
                override fun getHeaders(): Map<String, String> {
                    val headers = HashMap<String, String>()
                    headers["Content-Type"] = "application/json"
                    headers["App-Origin"] = ApiUrls.appOrigin
                    headers["Authorization"] = "Bearer " + ApiUrls.loginToken
                    return headers
                }
            }
            requestQueue.add(request_json)
        }// finish();

    // Post params to be sent to the server
    val caseDiscussionObservations: Unit
        get() {
            val requestQueue = Volley.newRequestQueue(this)
            val URL = ApiUrls.getCaseDiscussionObservations
            // Post params to be sent to the server
            val jsonObject = JSONObject()
            try {
                jsonObject.put("patient_id", patientID)
                jsonObject.put("episode_id", episodeID)
                jsonObject.put("duration", duration)
                jsonObject.put("doctor_id", doctorArrayHW)
            } catch (e: JSONException) {
                e.printStackTrace()
            }
            val request_json: JsonObjectRequest = object : JsonObjectRequest(URL, jsonObject,
                object : Response.Listener<JSONObject> {
                    override fun onResponse(response: JSONObject) {
                        try {
                            val observationObj = response.getJSONObject("response")
                            val recordsArr = observationObj.getJSONArray("records")
                            if (recordsArr.length() > 0) {
                                for (i in 0 until recordsArr.length()) {
                                    val catObj = recordsArr.getJSONObject(i)
                                    val model = PatientRecordsModel()
                                    model.obsCatName =
                                        catObj.getJSONObject("category").getString("category")
                                    model.recordId = catObj.getJSONObject("category").getInt("id")
                                    model.createdUser = catObj.getString("doctor")
                                    model.created_At = catObj.getString("created_at")
                                    model.type = 2
                                    val fieldDic = observationObj.getJSONObject("field-dictionary")
                                    if (fieldDic[catObj.getJSONObject("category").getInt("id")
                                            .toString() + ""] is JSONArray
                                    ) {
                                        val fieldArr = fieldDic.getJSONArray(
                                            catObj.getJSONObject("category").getInt("id")
                                                .toString() + ""
                                        )
                                        model.primaryKey =
                                            fieldArr.getJSONObject(0).getString("name")
                                        if (fieldArr.length() > 1) {
                                            model.secKey =
                                                fieldArr.getJSONObject(1).getString("name")
                                            if (catObj.has(
                                                    fieldArr.getJSONObject(1).getString("key")
                                                )
                                            ) {
                                                model.secData = catObj.getString(
                                                    fieldArr.getJSONObject(1).getString("key")
                                                )
                                            } else {
                                                model.secData = ""
                                            }
                                        }
                                        if (catObj.getJSONObject("category").getInt("id") == 33) {
                                            model.ternaryKey = "Date"
                                        } else if (fieldArr.length() > 2) {
                                            model.ternaryKey =
                                                fieldArr.getJSONObject(2).getString("name")
                                            if (catObj.has(
                                                    fieldArr.getJSONObject(2).getString("key")
                                                )
                                            ) {
                                                model.ternaryData = catObj.getString(
                                                    fieldArr.getJSONObject(2).getString("key")
                                                )
                                            } else {
                                                model.ternaryData = ""
                                            }
                                        }
                                        if (catObj.has(
                                                fieldArr.getJSONObject(0).getString("key")
                                            )
                                        ) model.primaryData = catObj.getString(
                                            fieldArr.getJSONObject(0).getString("key")
                                        )
                                        if (catObj.has("url")) {
                                            model.fileUrl = catObj.getString("url")
                                        }
                                    }
                                    model.fieldDictionary = fieldDic.toString()
                                    model.catRecordData = recordsArr.toString()
                                    model.catId = catObj.getJSONObject("category").getInt("id")
                                        .toString() + ""
                                    obsCatModel!!.add(model)
                                }
                                observationAdapter!!.notifyDataSetChanged()
                            }
                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }

                        // finish();
                    }
                }, object : Response.ErrorListener {
                    override fun onErrorResponse(error: VolleyError) {
                        VolleyLog.e("Error: ", error.message)
                    }
                }) {
                @Throws(AuthFailureError::class)
                override fun getHeaders(): Map<String, String> {
                    val headers = HashMap<String, String>()
                    headers["Content-Type"] = "application/json"
                    headers["App-Origin"] = ApiUrls.appOrigin
                    headers["Authorization"] = "Bearer " + ApiUrls.loginToken
                    return headers
                }
            }
            requestQueue.add(request_json)
        }

    fun getFileFromUrl(filrUrl: String?) {
        val url = JSONObject()
        try {
            url.put("url", filrUrl)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        apiCall!!.postRecords(ApiUrls.getFileFromUrl, url, this, object : VolleyCallback {
            override fun onSuccess(result: String) {
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
                errorHandler(this@CaseChannelRecordEvaluationDetailsActivity, err)
            }
        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // handle arrow click here
        finish()
        return super.onOptionsItemSelected(item)
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        super.getOnBackPressedDispatcher().onBackPressed()
        finish()
    }
}