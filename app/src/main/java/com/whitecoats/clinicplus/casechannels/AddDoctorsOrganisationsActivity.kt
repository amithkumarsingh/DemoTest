package com.whitecoats.clinicplus.casechannels

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.graphics.PorterDuff
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request.Method
import com.whitecoats.clinicplus.AppUtilities
import com.whitecoats.clinicplus.PatientRecordsApi
import com.whitecoats.clinicplus.R
import com.whitecoats.clinicplus.VolleyCallback
import com.whitecoats.clinicplus.apis.ApiGetPostMethodCalls
import com.whitecoats.clinicplus.apis.ApiUrls
import com.whitecoats.clinicplus.utils.ErrorHandlerClass.errorHandler
import org.json.JSONArray
import org.json.JSONObject

class AddDoctorsOrganisationsActivity : AppCompatActivity() {
    private lateinit var caseChannelDoctorTabLayout: RelativeLayout
    private lateinit var caseChannelOrganisationTabLayout: RelativeLayout
    private lateinit var doctorOrganisationEmptLayout: RelativeLayout
    private lateinit var caseChannelOrganisatinText: TextView
    private lateinit var caseChannelDoctorText: TextView
    private lateinit var doctorOrganisationEmptText: TextView
    private lateinit var doctorOrgiRecycleView: RecyclerView
    private var doctorListAdapter: DoctorOrganisationListAdapter? = null
    private var organisationListAdapter: DoctorOrganisationListAdapter? = null
    private lateinit var caseChannelDoctorModelList: MutableList<CaseChannelDoctorModel>
    private lateinit var caseChannelOrganisationModelList: MutableList<CaseChannelDoctorModel>
    private var apiCall: PatientRecordsApi? = null
    private var discussion_id = 0
    private var globalApiCall: ApiGetPostMethodCalls? = null
    private lateinit var appUtilities: AppUtilities
    private lateinit var dialog: Dialog
    private lateinit var caseChannelsViewModel: CaseChannelsViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_doctors_organisations)
        discussion_id = intent.getIntExtra("discussion_id", 0)
        caseChannelDoctorModelList = ArrayList()
        caseChannelOrganisationModelList = ArrayList()
        appUtilities = AppUtilities()
        apiCall = PatientRecordsApi()
        caseChannelsViewModel = ViewModelProvider(this)[CaseChannelsViewModel::class.java]
        globalApiCall = ApiGetPostMethodCalls()
        val backArrow = ContextCompat.getDrawable(this, R.drawable.ic_arrow_back)
        backArrow!!.setColorFilter(
            ContextCompat.getColor(this, R.color.colorWhite),
            PorterDuff.Mode.SRC_ATOP
        )
        val toolbar = findViewById<Toolbar>(R.id.caseToolbar)
        toolbar.navigationIcon = backArrow // your drawable
        toolbar.title = "Add Doctor / Organigation "
        setSupportActionBar(toolbar)
        toolbar.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }
        doctorOrgiRecycleView = findViewById(R.id.doctorOrgiRecycleView)
        caseChannelDoctorTabLayout = findViewById(R.id.caseChannelDoctorTab)
        caseChannelOrganisationTabLayout = findViewById(R.id.caseChannelOrganisationTab)
        caseChannelDoctorText = findViewById(R.id.caseChannelDoctorText)
        caseChannelOrganisatinText = findViewById(R.id.caseChannelOrganisatinText)
        doctorOrganisationEmptText = findViewById(R.id.doctorOrganisationEmptText)
        doctorOrganisationEmptLayout = findViewById(R.id.doctorOrganisationEmptLayout)
        val proceedText = findViewById<TextView>(R.id.proceedText)
        doctorListAdapter = DoctorOrganisationListAdapter(
            this@AddDoctorsOrganisationsActivity,
            caseChannelDoctorModelList,
            object : CaseDoctorOrganisationClickListener {
                override fun onItemClick(
                    v: View,
                    position: Int,
                    selectState: String,
                    sortByString: String
                ) {
                    val doctorModel = caseChannelDoctorModelList.get(position)
                    doctorModel.isSelected = !caseChannelDoctorModelList.get(position).isSelected
                    caseChannelDoctorModelList.removeAt(position)
                    caseChannelDoctorModelList.add(position, doctorModel)
                    doctorListAdapter!!.notifyDataSetChanged()
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
        organisationListAdapter = DoctorOrganisationListAdapter(
            this,
            caseChannelOrganisationModelList,
            object : CaseDoctorOrganisationClickListener {
                override fun onItemClick(
                    v: View,
                    position: Int,
                    selectState: String,
                    sortByString: String
                ) {
                    val doctorModel = caseChannelOrganisationModelList.get(position)
                    doctorModel.isSelected =
                        !caseChannelOrganisationModelList.get(position).isSelected
                    caseChannelOrganisationModelList.removeAt(position)
                    caseChannelOrganisationModelList.add(position, doctorModel)
                    organisationListAdapter!!.notifyDataSetChanged()
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
        doctorOrgiRecycleView.layoutManager = LinearLayoutManager(
            applicationContext.applicationContext,
            LinearLayoutManager.VERTICAL,
            false
        )
        doctorOrgiRecycleView.itemAnimator = DefaultItemAnimator()
        doctorOrgiRecycleView.adapter = doctorListAdapter
        caseChannelDoctorTabLayout.setOnClickListener {
            caseChannelDoctorTabLayout.setBackgroundResource(R.drawable.case_select_tab_background)
            caseChannelOrganisationTabLayout.setBackgroundResource(R.drawable.case_tab_background)
            caseChannelOrganisatinText.setTextColor(
                ContextCompat.getColor(
                    this@AddDoctorsOrganisationsActivity,
                    R.color.colorBlack
                )
            )
            caseChannelDoctorText.setTextColor(
                ContextCompat.getColor(
                    this@AddDoctorsOrganisationsActivity,
                    R.color.colorWhite
                )
            )
            doctorOrgiRecycleView.layoutManager = LinearLayoutManager(
                applicationContext.applicationContext,
                LinearLayoutManager.VERTICAL,
                false
            )
            doctorOrgiRecycleView.itemAnimator = DefaultItemAnimator()
            doctorOrgiRecycleView.adapter = doctorListAdapter
        }
        caseChannelOrganisationTabLayout.setOnClickListener {
            caseChannelOrganisationTabLayout.setBackgroundResource(R.drawable.case_select_tab_background)
            caseChannelDoctorTabLayout.setBackgroundResource(R.drawable.case_tab_background)
            caseChannelDoctorText.setTextColor(
                ContextCompat.getColor(
                    this@AddDoctorsOrganisationsActivity,
                    R.color.colorBlack
                )
            )
            caseChannelOrganisatinText.setTextColor(
                ContextCompat.getColor(
                    this@AddDoctorsOrganisationsActivity,
                    R.color.colorWhite
                )
            )
            doctorOrgiRecycleView.layoutManager = LinearLayoutManager(
                applicationContext.applicationContext,
                LinearLayoutManager.VERTICAL,
                false
            )
            doctorOrgiRecycleView.itemAnimator = DefaultItemAnimator()
            doctorOrgiRecycleView.adapter = organisationListAdapter
            organisationListAdapter!!.notifyDataSetChanged()
        }
        proceedText.setOnClickListener { v: View? ->
            val docIds = JSONArray()
            val orgIds = JSONArray()
            for (i in caseChannelDoctorModelList.indices) {
                if (caseChannelDoctorModelList.get(i).isSelected) {
                    try {
                        val tempObj = JSONObject()
                        tempObj.put("id", caseChannelDoctorModelList.get(i).id)
                        docIds.put(tempObj)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
            for (i in caseChannelOrganisationModelList.indices) {
                if (caseChannelOrganisationModelList.get(i).isSelected) {
                    try {
                        val tempObj = JSONObject()
                        tempObj.put("id", caseChannelOrganisationModelList.get(i).id)
                        orgIds.put(tempObj)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
            val reqObj = JSONObject()
            try {
                reqObj.put("doctors", docIds)
                reqObj.put("orgs", orgIds)
                reqObj.put("discussion_id", discussion_id)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            Log.d("Json obj", reqObj.toString())
            if (docIds.length() != 0 || orgIds.length() != 0) {
                var msg = ""
                if (docIds.length() > 0) {
                    msg += "Adding " + docIds.length() + " Specialist. "
                }
                if (orgIds.length() > 0) {
                    msg += "Adding " + orgIds.length() + " Organisation. "
                }
                msg += "\nAre you sure?"
                AlertDialog.Builder(this@AddDoctorsOrganisationsActivity)
                    .setTitle("Mapping Specialist")
                    .setMessage(msg)
                    .setPositiveButton(android.R.string.ok) { dialog: DialogInterface?, whichButton: Int ->
                        updateDoctorOrganisationDiscussion(
                            reqObj
                        )
                    }
                    .setNegativeButton(android.R.string.cancel, null).show()
            } else {
                Toast.makeText(
                    this@AddDoctorsOrganisationsActivity,
                    "Please select one Specialist or Organisation",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
        caseChannelDoctorOrganisation
    }
    //Process os success response

    //new code
    val caseChannelDoctorOrganisation:

    //maping organisation
            Unit
        @SuppressLint("SetTextI18n")
        get() {
            showCustomProgressAlertDialog(
                resources.getString(R.string.fetching),
                resources.getString(R.string.wait_while_we_fetching)
            )

            doctorOrganisationEmptLayout.visibility = View.GONE
            val url = ApiUrls.getDoctorOrganisation

            caseChannelsViewModel.commonViewModelCall(
                url,
                JSONObject(), Method.GET
            ).observe(
                this
            ) { result ->
                try {
                    val responseVal = JSONObject(result)
                    //Process os success response
                    dialog.dismiss()
                    if (responseVal.getInt("status_code") == 200) {
                        val response = responseVal.getJSONObject("response")
                        //new code
                        val resObj = response.getJSONObject("response")
                        val docArr = resObj.getJSONArray("doctors")
                        if (docArr.length() > 0) {
                            for (i in 0 until docArr.length()) {
                                val docObj = docArr.getJSONObject(i)
                                val doctorModel = CaseChannelDoctorModel()
                                doctorModel.docName = docObj.getString("fname")
                                doctorModel.id = docObj.getInt("id")
                                doctorModel.isSelected = false
                                caseChannelDoctorModelList.add(doctorModel)
                            }
                            doctorListAdapter!!.notifyDataSetChanged()
                        } else {
                            doctorOrganisationEmptLayout.visibility = View.VISIBLE
                            doctorOrganisationEmptText.text = "No Specialist found."
                        }

                        //maping organisation
                        val orgArr = resObj.getJSONArray("orgs")
                        if (orgArr.length() > 0) {
                            for (i in 0 until orgArr.length()) {
                                val orgObj = orgArr.getJSONObject(i)
                                val doctorModel = CaseChannelDoctorModel()
                                doctorModel.docName = orgObj.getString("name")
                                doctorModel.id = orgObj.getInt("id")
                                doctorModel.isSelected = false
                                caseChannelOrganisationModelList.add(doctorModel)
                            }
                            organisationListAdapter!!.notifyDataSetChanged()
                        } else {
                            doctorOrganisationEmptLayout.visibility = View.VISIBLE
                            doctorOrganisationEmptText.text = "No Specialist found."
                        }
                    } else {
                        dialog.dismiss()
                        errorHandler(this@AddDoctorsOrganisationsActivity, result)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    dialog.dismiss()
                }
            }
        }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        super.getOnBackPressedDispatcher().onBackPressed()
        finish()
    }

    fun updateDoctorOrganisationDiscussion(reqObj: JSONObject?) {
        showCustomProgressAlertDialog(
            "Adding Specialist", "Updating data please wait..."
        )
        apiCall!!.postRecords(
            ApiUrls.updateDoctorOrganisationDiscussion,
            reqObj,
            this,
            object : VolleyCallback {
                override fun onSuccess(result: String) {
                    dialog.dismiss()
                    try {
                        Log.d("Success", result)
                        val response = JSONObject(result)
                        if (response.getInt("response") == 1) {
                            val intent = Intent(
                                this@AddDoctorsOrganisationsActivity,
                                CaseChannelDashboardActivity::class.java
                            )
                            intent.putExtra("caseChannelId", discussion_id)
                            startActivity(intent)
                        } else {
                            Toast.makeText(
                                this@AddDoctorsOrganisationsActivity,
                                "Could not map any specialist in the discussion. Try again later.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        Toast.makeText(
                            this@AddDoctorsOrganisationsActivity,
                            "Could not map any specialist in the discussion. Try again later.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onError(err: String) {
                    dialog.dismiss()
                    Log.d("Error", err)
                    Toast.makeText(
                        this@AddDoctorsOrganisationsActivity,
                        "Could not map any specialist in the discussion. Try again later.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }
    private fun showCustomProgressAlertDialog(title: String, progressVal: String) {
        val builder = AlertDialog.Builder(this@AddDoctorsOrganisationsActivity)
        builder.setTitle(title)
        builder.setCancelable(false)
        val customLayout: View = layoutInflater.inflate(R.layout.custom_progress_bar, null)
        builder.setView(customLayout)
        dialog = builder.create()
        val tvBody = customLayout.findViewById<TextView>(R.id.tv_value)
        tvBody.text = progressVal
        dialog.show()
    }

}