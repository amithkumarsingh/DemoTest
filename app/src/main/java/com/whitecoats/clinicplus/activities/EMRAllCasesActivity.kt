package com.whitecoats.clinicplus.activities

import android.content.Intent
import android.graphics.PorterDuff
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.whitecoats.clinicplus.R
import com.whitecoats.clinicplus.adapters.EMRAllCasesListAdapter
import com.whitecoats.clinicplus.fragments.EMRConsultationNotesFragment
import com.whitecoats.clinicplus.models.EMRAllEpisodeModel
import com.whitecoats.clinicplus.utils.ErrorHandlerClass.errorHandler
import com.whitecoats.clinicplus.viewmodels.EMRConsultationNotesViewModel
import org.json.JSONException
import org.json.JSONObject

class EMRAllCasesActivity : AppCompatActivity() {
    private var toolbar: Toolbar? = null
    private var emrAllCasesRecycleView: RecyclerView? = null
    var emrAllCasesModelList: MutableList<EMRAllEpisodeModel>? = null
    private var emrAllCasesListAdapter: EMRAllCasesListAdapter? = null
    private var mLayoutManager: RecyclerView.LayoutManager? = null
    private var groupData: ArrayList<Int>? = null
    private var patientId = 0
    private var emrConsultationNotesViewModel: EMRConsultationNotesViewModel? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.emr_all_cases_activity)
        toolbar = findViewById(R.id.allCasesToolbar)
        toolbar!!.title = "All Cases"
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        val upArrow = resources.getDrawable(R.drawable.ic_arrow_back)
        upArrow.setColorFilter(resources.getColor(R.color.colorWhite), PorterDuff.Mode.SRC_ATOP)
        supportActionBar!!.setHomeAsUpIndicator(upArrow)
        emrConsultationNotesViewModel = ViewModelProvider(this).get(
            EMRConsultationNotesViewModel::class.java
        )
        emrConsultationNotesViewModel!!.init()
        val intent = intent
        patientId = intent.getIntExtra("patientId", 0)
        mLayoutManager = LinearLayoutManager(applicationContext)
        emrAllCasesRecycleView = findViewById(R.id.emrAllCasesRecycleView)
        groupData = ArrayList()
        emrAllCasesModelList = ArrayList()
        //        getallCase();
    }

    override fun finish() {
        // Prepare data intent
        val data = Intent()
        data.putExtra("caseSelectedPosition", EMRConsultationNotesFragment.caseSelectedPosition)
        //        data.putExtra("returnKey2", "You could be better then you are. ");
        // Activity finished ok, return the data
        setResult(RESULT_OK, data)
        super.finish()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // handle arrow click here
        if (item.itemId == android.R.id.home) {
            finish()
        }
        return super.onOptionsItemSelected(item)
    }

    fun getallCase() {
        var emrModel = EMRAllEpisodeModel()
        emrModel.episodeName = "1-8-2020"
        emrAllCasesModelList!!.add(emrModel)
        emrModel = EMRAllEpisodeModel()
        emrModel.episodeName = "1-9-2020"
        emrAllCasesModelList!!.add(emrModel)
    }

    override fun onResume() {
        super.onResume()

        //get doctor episodes
        emrConsultationNotesViewModel!!.getDoctorEpisodeDetails(this, patientId)
            .observe(this, object : Observer<String?> {
                override fun onChanged(value: String?) {
                    try {
                        emrAllCasesModelList!!.clear()
                        val jsonObject = JSONObject(value)
                        if (jsonObject.getInt("status_code") == 200) {
                            val response = JSONObject(value).getJSONObject("response")
                            Log.i("checkfordoctorepisode", response.toString())
                            //                        JSONObject rootObj = response.optJSONObject("response");
                            val doctorEpisodeArray = response.getJSONArray("response")
                            val doctorEpisodeLength = doctorEpisodeArray.length()
                            if (doctorEpisodeLength > 0) {
                                for (i in 0 until doctorEpisodeLength) {
                                    val episodeObject = doctorEpisodeArray.getJSONObject(i)
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
                                    emrAllCasesModelList!!.add(allEpisodeModel)
                                }
                            } else {
                            }
                        } else {
                            if (value != null) {
                                errorHandler(this@EMRAllCasesActivity, value)
                            }
                        }
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                }
            })
        emrAllCasesModelList = EMRConsultationNotesFragment.emrAllEpisodeList
        emrAllCasesListAdapter = emrAllCasesModelList?.let {
            groupData?.let { it1 ->
                EMRAllCasesListAdapter(
                    applicationContext,
                    it,
                    this@EMRAllCasesActivity,
                    it1
                ) { v, selectedPosition ->
                    EMRConsultationNotesFragment.switchCaseClicked = 2
                    EMRConsultationNotesFragment.caseSelectedPosition = selectedPosition
                    EMRActivity.interactionSelectedPosition = 0
                    finish()
                }
            }
        }
        emrAllCasesRecycleView!!.layoutManager = mLayoutManager
        emrAllCasesRecycleView!!.itemAnimator = DefaultItemAnimator()
        emrAllCasesRecycleView!!.adapter = emrAllCasesListAdapter
    }
}