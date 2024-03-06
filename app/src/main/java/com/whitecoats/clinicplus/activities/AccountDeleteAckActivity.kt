package com.whitecoats.clinicplus.activities

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.whitecoats.clinicplus.*
import com.whitecoats.clinicplus.apis.ApiUrls
import com.whitecoats.clinicplus.constants.AppConstants
import com.whitecoats.clinicplus.databinding.ActivityAccountDeleteAckBinding
import com.whitecoats.clinicplus.fragments.DashboardFullMode
import com.whitecoats.clinicplus.onboarding.OnBoardingActivity
import com.whitecoats.fragments.AppointmentFragment
import com.whitecoats.fragments.ChatFragment
import com.whitecoats.fragments.PatientFragment
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class AccountDeleteAckActivity:AppCompatActivity() {

    private lateinit var appPreference: SharedPreferences
    private lateinit var appDataBaseManager: AppDatabaseManager
    private lateinit var binding: ActivityAccountDeleteAckBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityAccountDeleteAckBinding.inflate(layoutInflater)
        setContentView(binding.root)
        appDataBaseManager=AppDatabaseManager(this@AccountDeleteAckActivity)
        appPreference = getSharedPreferences(ApiUrls.appSharedPref, MODE_PRIVATE)
        lifecycleScope.launch {
            delay(4000)
            clearUserData()
            navigateUserToStartScreen()
        }
        binding.accountDelAckCloseBtn.setOnClickListener{
            clearUserData()
            navigateUserToStartScreen()
        }
    }

    private fun navigateUserToStartScreen() {
        var intent=Intent(this@AccountDeleteAckActivity,OnBoardingActivity::class.java)
        startActivity(intent)
        finishAffinity()
    }

    private fun clearUserData() {
        val appUserManagers: List<AppUserManager> = appDataBaseManager.userData
        appDataBaseManager.deletePatient(appUserManagers[0])
        val editor = appPreference.edit()
        editor.putBoolean("OnBoardingDone", false)
        editor.commit()
        ApiUrls.loginToken = ""
        ApiUrls.doctorId = 0
        PatientFragment.patientTabFlag = 0
        AppointmentFragment.appointmentTabFlag = 0
        ChatFragment.chatTabFlag = 0
        ConfirmOrderActivity.confirmOrderFlagChat = 0
        ConfirmOrderActivity.confirmOrderFlag = 0
        ChatRoomActivity.chatClickFlag = 0
        ApiUrls.bottomNaviType = 0
        AppConstants.durationSelectedValue = 0
        AppConstants.selectedClinicClickOnDashBoard = 0
        AppConstants.selectedClinicIdOnDashBoard = 0
        AppConstants.clinicCount = 0
        DashboardFullMode.switchClickSelectedPosition = 0
        DashboardFullMode.switchClinicSelectedString = ""
        AppointmentFragment.lastHeaderDate = ""
        ApiUrls.activePastFilterFlag = ""
    }

    override fun onBackPressed() {
        //super.onBackPressed()
    }
}