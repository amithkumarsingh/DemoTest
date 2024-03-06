package com.whitecoats.clinicplus.autofollowup

import android.app.AlertDialog
import android.app.Dialog
import android.graphics.PorterDuff
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.android.volley.Request.Method
import com.whitecoats.clinicplus.AppointmentApiRequests
import com.whitecoats.clinicplus.R
import com.whitecoats.clinicplus.VolleyCallback
import com.whitecoats.clinicplus.apis.ApiUrls
import com.whitecoats.clinicplus.utils.ErrorHandlerClass.errorHandler
import com.whitecoats.clinicplus.viewmodels.CommonViewModel
import org.json.JSONObject

class AutoFollowUpPreferenceSetup : AppCompatActivity() {
    private lateinit var questionTab: RelativeLayout
    private lateinit var submissionTab: RelativeLayout
    private lateinit var questionBottom: View
    private lateinit var submissionBottom: View
    private lateinit var questionLayout: LinearLayout
    private lateinit var submissionLayout: LinearLayout
    private lateinit var notifyMeLayout: LinearLayout
    private lateinit var notifyStaffLayout: LinearLayout
    private lateinit var docQuestLayout: LinearLayout
    private lateinit var staffQuestLayout: LinearLayout
    private lateinit var prefLayout: LinearLayout
    private var apiRequests: AppointmentApiRequests? = null
    private lateinit var feelingQuest: SwitchCompat
    private lateinit var instQuest: SwitchCompat
    private lateinit var reportQuest: SwitchCompat
    private lateinit var describeQuest: SwitchCompat
    private lateinit var optionBookQuest: SwitchCompat
    private lateinit var notifyMe: SwitchCompat
    private lateinit var notifyStaff: SwitchCompat
    private lateinit var docSubmission: SwitchCompat
    private lateinit var docPatientFeeling: SwitchCompat
    private lateinit var docInst: SwitchCompat
    private lateinit var docDescribe: SwitchCompat
    private lateinit var docUploadFile: SwitchCompat
    private lateinit var staffSubmission: SwitchCompat
    private lateinit var staffPatientFeeling: SwitchCompat
    private lateinit var staffInst: SwitchCompat
    private lateinit var staffDescribe: SwitchCompat
    private lateinit var staffUploadFile: SwitchCompat
    private lateinit var enableFollowUp: SwitchCompat
    private lateinit var staffName: EditText
    private lateinit var staffNumber: EditText
    private lateinit var noOfDays: EditText
    private lateinit var docApply: Button
    private lateinit var staffApply: Button
    private lateinit var saveNoDays: Button
    private lateinit var dialog: Dialog
    private lateinit var commonViewModel: CommonViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auto_follow_up_preference_setup)
        val toolbar = findViewById<Toolbar>(R.id.followUpPrefToolbar)
        questionTab = findViewById(R.id.followUpQuestionTab)
        submissionTab = findViewById(R.id.followUpSubmissionTab)
        questionBottom = findViewById(R.id.followUpQuestionBottom)
        submissionBottom = findViewById(R.id.followUpSubmissionBottom)
        submissionLayout = findViewById(R.id.followUpSubmissionLayout)
        questionLayout = findViewById(R.id.followUpQuestionLayout)
        feelingQuest = findViewById(R.id.followUpHowFeelingSwt)
        instQuest = findViewById(R.id.followUpFollowInstSwt)
        reportQuest = findViewById(R.id.followUpReportSwt)
        describeQuest = findViewById(R.id.followUpDescribeSwt)
        optionBookQuest = findViewById(R.id.followUpBookOptinSwt)
        notifyMe = findViewById(R.id.followUpNotifyMeSwt)
        docSubmission = findViewById(R.id.followUpDocSubmissionSwt)
        docPatientFeeling = findViewById(R.id.followUpPatientFeelingSwt)
        docInst = findViewById(R.id.followUpInstSwt)
        docDescribe = findViewById(R.id.followUpDocDescribeSwt)
        docUploadFile = findViewById(R.id.followUpUploadFile)
        staffName = findViewById(R.id.followUpStaffEmail)
        staffNumber = findViewById(R.id.followUpStaffNumber)
        notifyStaff = findViewById(R.id.followUpNotifyStaffSwt)
        staffPatientFeeling = findViewById(R.id.followUpStaffPatientFeelingSwt)
        staffInst = findViewById(R.id.followUpStaffInstSwt)
        staffDescribe = findViewById(R.id.followUpStaffDescribeSwt)
        staffUploadFile = findViewById(R.id.followUpStaffUplaodFileSwt)
        staffSubmission = findViewById(R.id.followUpStaffSubmissionSwt)
        docApply = findViewById(R.id.followUpDocApply)
        staffApply = findViewById(R.id.followUpStaffApply)
        noOfDays = findViewById(R.id.followUpNoOdDays)
        saveNoDays = findViewById(R.id.followUpPrefApplyDays)
        notifyMeLayout = findViewById(R.id.followUpNotifyMeLayout)
        notifyStaffLayout = findViewById(R.id.followUpNotifyStaffLayout)
        docQuestLayout = findViewById(R.id.followUpDocQuestionLayout)
        staffQuestLayout = findViewById(R.id.followUpStaffQuestionLayout)
        enableFollowUp = findViewById(R.id.followUpSwitch)
        prefLayout = findViewById(R.id.followUpPrefLayout)
        toolbar.title = "Follow-up Preference"
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        val upArrow = ContextCompat.getDrawable(this, R.drawable.ic_arrow_back)
        upArrow!!.setColorFilter(
            ContextCompat.getColor(this, R.color.colorWhite),
            PorterDuff.Mode.SRC_ATOP
        )
        supportActionBar!!.setHomeAsUpIndicator(upArrow)
        apiRequests = AppointmentApiRequests()
        commonViewModel =
            ViewModelProvider(this@AutoFollowUpPreferenceSetup)[CommonViewModel::class.java]
        questionTab.setOnClickListener {
            questionLayout.visibility = View.VISIBLE
            submissionLayout.visibility = View.GONE
            questionBottom.visibility = View.VISIBLE
            submissionBottom.visibility = View.GONE
        }
        submissionTab.setOnClickListener {
            questionLayout.visibility = View.GONE
            submissionLayout.visibility = View.VISIBLE
            questionBottom.visibility = View.GONE
            submissionBottom.visibility = View.VISIBLE
        }
        docApply.setOnClickListener { updatePref() }
        staffApply.setOnClickListener {
            if (staffName.text.toString().isEmpty()) {
                staffName.error = "Please provide staff email id"
            } else if (staffNumber.text.toString().isEmpty()) {
                staffNumber.error = "Please provide staff contact details"
            } else {
                updatePref()
            }
        }
        saveNoDays.setOnClickListener { updatePref() }
        feelingQuest.setOnClickListener { updatePref() }
        instQuest.setOnClickListener { updatePref() }
        reportQuest.setOnClickListener { updatePref() }
        describeQuest.setOnClickListener { updatePref() }
        optionBookQuest.setOnClickListener { updatePref() }
        notifyMe.setOnClickListener {
            updatePref()
            if (notifyMe.isChecked) {
                notifyMeLayout.visibility = View.VISIBLE
            } else {
                notifyMeLayout.visibility = View.GONE
            }
        }
        notifyStaff.setOnClickListener {
            updatePref()
            if (notifyStaff.isChecked) {
                notifyStaffLayout.visibility = View.VISIBLE
            } else {
                notifyStaffLayout.visibility = View.GONE
            }
        }
        docSubmission.setOnClickListener {
            updatePref()
            if (docSubmission.isChecked) {
                docQuestLayout.visibility = View.VISIBLE
            } else {
                docQuestLayout.visibility = View.GONE
            }
        }
        staffSubmission.setOnClickListener {
            updatePref()
            if (staffSubmission.isChecked) {
                staffQuestLayout.visibility = View.VISIBLE
            } else {
                staffQuestLayout.visibility = View.GONE
            }
        }
        enableFollowUp.setOnClickListener {
            updatePref()
            if (enableFollowUp.isChecked) {
                prefLayout.visibility = View.VISIBLE
            } else {
                prefLayout.visibility = View.GONE
            }
        }
        docPref
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        super.getOnBackPressedDispatcher().onBackPressed()
        finish()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle item selection
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private val docPref: Unit
        get() {
            showCustomProgressAlertDialog(
                resources.getString(R.string.follow_up),
                resources.getString(R.string.fetching)
            )

            commonViewModel.commonViewModelCall(ApiUrls.getFollowUpPref, JSONObject(), Method.GET)
                .observe(
                    this@AutoFollowUpPreferenceSetup
                ) { result ->
                    try {
                        dialog.dismiss()
                        var resObj = JSONObject(result)
                        if (resObj.getInt("status_code") == 200) {
                            val resArrObj = resObj.getJSONObject("response")
                            val resArr = resArrObj.getJSONArray("response")
                            resObj = resArr.getJSONObject(0)
                            enableFollowUp.isChecked = resObj.getInt("is_follow_up") == 1
                            feelingQuest.isChecked = resObj.getInt("is_enable_how_feeling") == 1
                            instQuest.isChecked =
                                resObj.getInt("is_enable_following_instructions") == 1
                            reportQuest.isChecked = resObj.getInt("is_enable_file_booking") == 1
                            describeQuest.isChecked = resObj.getInt("is_enable_description") == 1
                            optionBookQuest.isChecked = resObj.getInt("is_enable_booking") == 1
                            notifyMe.isChecked = resObj.getInt("notify_is_doctor") == 1
                            docSubmission.isChecked = resObj.getInt("notify_is_for_all") == 2
                            docInst.isChecked =
                                resObj.getInt("notify_on_not_following_instrunction") == 1
                            docPatientFeeling.isChecked =
                                resObj.getInt("notify_on_not_feeling_better") == 1
                            docDescribe.isChecked =
                                resObj.getInt("notify_on_described_condition") == 1
                            docUploadFile.isChecked =
                                resObj.getInt("notify_on_file_uploaded") == 1
                            notifyStaff.isChecked = resObj.getInt("notify_is_staff") == 1
                            staffSubmission.isChecked =
                                resObj.getInt("notify_staff_is_for_all") == 2
                            staffPatientFeeling.isChecked =
                                resObj.getInt("notify_staff_on_not_feeling_better") == 1
                            staffInst.isChecked =
                                resObj.getInt("notify_staff_on_not_following_instrunction") == 1
                            staffDescribe.isChecked =
                                resObj.getInt("notify_staff_on_described_condition") == 1
                            staffUploadFile.isChecked =
                                resObj.getInt("notify_staff_on_file_uploaded") == 1
                            if (resObj.getString("notify_staff_email").toString()
                                    .equals("null", ignoreCase = true)
                            ) {
                                Log.i("isEmpty", "isEmpty")
                            } else {
                                staffName.setText(resObj.getString("notify_staff_email"))
                            }
                            if (resObj.getString("notify_staff_phone").toString()
                                    .equals("null", ignoreCase = true)
                            ) {
                                Log.i("isEmpty", "isEmpty")
                            } else {
                                staffNumber.setText(resObj.getString("notify_staff_phone"))
                            }
                            if (enableFollowUp.isChecked) {
                                prefLayout.visibility = View.VISIBLE
                            } else {
                                prefLayout.visibility = View.GONE
                            }

                            noOfDays.setText(resObj.getString("days_after_consult"))
                            if (staffSubmission.isChecked) {
                                staffQuestLayout.visibility = View.VISIBLE
                            } else {
                                staffQuestLayout.visibility = View.GONE
                            }
                            if (docSubmission.isChecked) {
                                docQuestLayout.visibility = View.VISIBLE
                            } else {
                                docQuestLayout.visibility = View.GONE
                            }
                            if (notifyStaff.isChecked) {
                                notifyStaffLayout.visibility = View.VISIBLE
                            } else {
                                notifyStaffLayout.visibility = View.GONE
                            }
                            if (notifyMe.isChecked) {
                                notifyMeLayout.visibility = View.VISIBLE
                            } else {
                                notifyMeLayout.visibility = View.GONE
                            }
                        } else {
                            dialog.dismiss()
                            errorHandler(this@AutoFollowUpPreferenceSetup, result)
                        }

                    } catch (e: Exception) {
                        dialog.dismiss()
                    }
                }
        }

    private fun updatePref() {
        val reqObj = JSONObject()
        try {
            reqObj.put("is_follow_up", if (enableFollowUp.isChecked) 1 else 0)
            reqObj.put("is_enable_how_feeling", if (feelingQuest.isChecked) 1 else 0)
            reqObj.put("is_enable_following_instructions", if (instQuest.isChecked) 1 else 0)
            reqObj.put("is_enable_description", if (describeQuest.isChecked) 1 else 0)
            reqObj.put("is_enable_file_booking", if (reportQuest.isChecked) 1 else 0)
            reqObj.put("is_enable_booking", if (optionBookQuest.isChecked) 1 else 0)
            reqObj.put("notify_is_doctor", if (notifyMe.isChecked) 1 else 0)
            reqObj.put("notify_is_for_all", if (docSubmission.isChecked) 2 else 1)
            reqObj.put("notify_on_not_following_instrunction", if (docInst.isChecked) 1 else 0)
            reqObj.put("notify_on_not_feeling_better", if (docPatientFeeling.isChecked) 1 else 0)
            reqObj.put("notify_on_described_condition", if (docDescribe.isChecked) 1 else 0)
            reqObj.put("notify_on_file_uploaded", if (docUploadFile.isChecked) 1 else 0)
            reqObj.put("notify_is_staff", if (notifyStaff.isChecked) 1 else 0)
            reqObj.put("notify_staff_is_for_all", if (staffSubmission.isChecked) 2 else 1)
            reqObj.put(
                "notify_staff_on_not_feeling_better",
                if (staffPatientFeeling.isChecked) 1 else 0
            )
            reqObj.put(
                "notify_staff_on_not_following_instrunction",
                if (staffInst.isChecked) 1 else 0
            )
            reqObj.put(
                "notify_staff_on_described_condition",
                if (staffDescribe.isChecked) 1 else 0
            )
            reqObj.put("notify_staff_on_file_uploaded", if (staffUploadFile.isChecked) 1 else 0)
            reqObj.put("notify_staff_email", staffName.text.toString())
            reqObj.put("notify_staff_phone", staffNumber.text.toString())
            reqObj.put("days_after_consult", noOfDays.text.toString())
        } catch (e: Exception) {
            e.printStackTrace()
        }
        showCustomProgressAlertDialog(
            resources.getString(R.string.follow_up),
            resources.getString(R.string.updating)
        )

        commonViewModel.commonViewModelCall(ApiUrls.updateFollowUpPref, reqObj, Method.POST)
            .observe(
                this@AutoFollowUpPreferenceSetup
            ) { result ->
                val res = JSONObject(result)
                if (res.getInt("status_code") == 200) {
                    dialog.dismiss()
                } else {
                    errorHandler(this@AutoFollowUpPreferenceSetup, result)
                    dialog.dismiss()
                }
            }
    }

    fun showCustomProgressAlertDialog(
        title: String?,
        progressVal: String?

    ) {
        val builder = AlertDialog.Builder(this@AutoFollowUpPreferenceSetup)
        builder.setTitle(title)
        builder.setCancelable(false)
        val customLayout = LayoutInflater.from(this@AutoFollowUpPreferenceSetup)
            .inflate(R.layout.custom_progress_bar, null)
        builder.setView(customLayout)
        dialog = builder.create()
        val tvBody = customLayout.findViewById<TextView>(R.id.tv_value)
        tvBody.text = progressVal
        dialog.show()
    }
}