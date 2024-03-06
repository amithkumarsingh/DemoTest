package com.whitecoats.clinicplus.activities

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.whitecoats.adapter.AssistantTabAdapter
import com.whitecoats.adapter.PatientPListAdapter.Companion.isPatientProfileClicked
import com.whitecoats.clinicplus.R
import com.whitecoats.clinicplus.apis.ApiUrls
import com.whitecoats.clinicplus.fragments.EMRConsultationNotesFragment
import com.whitecoats.clinicplus.fragments.EMRPatientProfileFragment
import com.whitecoats.clinicplus.fragments.EMRSharedRecordsFragment
import com.whitecoats.clinicplus.utils.SharedPref

class EMRActivity : AppCompatActivity(), BottomNavigationView.OnNavigationItemSelectedListener {
    private var patientID = 0
    private var appointmentID = 0

    @JvmField
    var patientId = 0

    @JvmField
    var patientName: String? = null
    private var apptDetailsBack: ImageButton? = null
    private var patient_name: TextView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_e_m_r)
        initView()
    }

    private fun initView() {
        val sharedPref = SharedPref(this)
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.emrBottomNav)
        bottomNavigationView.setOnNavigationItemSelectedListener(this)
        apptDetailsBack = findViewById(R.id.appt_details_back)
        patient_name = findViewById(R.id.patient_name)
        bottomNavigationView.menu.findItem(R.id.emrNotes).isVisible = sharedPref.isPrefExists("EMR")
        bottomNavigationView.menu.findItem(R.id.emrSharedRecords).isVisible =
            sharedPref.isPrefExists("EMR")

        patientID = intent.getIntExtra("PatientId", 0)
        appointmentID = intent.getIntExtra("ApptId", 0)
        val intent = intent
        patientId = intent.getIntExtra("PatientId", 0)
        patientName = intent.getStringExtra("PatientName")
        patient_name?.text = patientName
        apptDetailsBack?.setOnClickListener(View.OnClickListener { finish() })
        if (isPatientProfileClicked || AssistantTabAdapter.isAssistantPatientProfileClicked ||
            !sharedPref.isPrefExists("EMR")
        ) {
            //setting profile fragment first
            bottomNavigationView.menu.findItem(R.id.emrProfile).isChecked = true
            val transaction = supportFragmentManager.beginTransaction()
            transaction.replace(
                R.id.emrFragContainer,
                EMRPatientProfileFragment.newInstance(patientID)
            )
            transaction.commit()
            isPatientProfileClicked = false
            AssistantTabAdapter.isAssistantPatientProfileClicked = false
        } else {
            //setting consult note fragment first
            bottomNavigationView.menu.findItem(R.id.emrNotes).isChecked = true
            val transaction = supportFragmentManager.beginTransaction()
            transaction.replace(
                R.id.emrFragContainer,
                EMRConsultationNotesFragment.newInstance(
                    patientID,
                    appointmentID,
                    getIntent().getStringExtra("ApptDate"),
                    getIntent().getStringExtra("ApptTime"),
                    getIntent().getIntExtra("ApptMode", 0),
                    patientName
                )
            )
            transaction.commit()
        }
    }

    @SuppressLint("NonConstantResourceId")
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        var selectedFragment: Fragment? = null
        when (item.itemId) {
            R.id.emrProfile -> selectedFragment = EMRPatientProfileFragment.newInstance(patientID)
            R.id.emrSharedRecords -> selectedFragment = EMRSharedRecordsFragment.newInstance("", "")
            R.id.emrNotes -> selectedFragment = EMRConsultationNotesFragment.newInstance(
                patientID,
                appointmentID,
                intent.getStringExtra("ApptDate"),
                intent.getStringExtra("ApptTime"),
                intent.getIntExtra("ApptMode", 0),
                patientName
            )
        }


        //committing fragment on view
        if (selectedFragment != null) {
            val transaction = supportFragmentManager.beginTransaction()
            transaction.replace(R.id.emrFragContainer, selectedFragment)
            transaction.commit()
        }
        return true
    }

    override fun onBackPressed() {
        super.getOnBackPressedDispatcher().onBackPressed()
        ApiUrls.spinnerSelection = 2
        EMRConsultationNotesFragment.caseSelectedPosition = 0
        interactionSelectedPosition = 0
        finish()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    companion object {
        @JvmField
        var interactionSelectedPosition = 0
    }
}