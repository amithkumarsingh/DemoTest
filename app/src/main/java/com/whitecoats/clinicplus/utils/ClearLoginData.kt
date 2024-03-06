package com.whitecoats.clinicplus.utils

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import com.whitecoats.clinicplus.*
import com.whitecoats.clinicplus.apis.ApiUrls
import com.whitecoats.clinicplus.constants.AppConstants
import com.whitecoats.clinicplus.fragments.DashboardFullMode
import com.whitecoats.fragments.AppointmentFragment
import com.whitecoats.fragments.ChatFragment
import com.whitecoats.fragments.PatientFragment

class ClearLoginData {
    private lateinit var appPreference: SharedPreferences
    private lateinit var appDataBaseManager: AppDatabaseManager
    fun clearUserData(context: Context, callingFrom: String) {
        appDataBaseManager = AppDatabaseManager(context)
        appPreference =
            context.getSharedPreferences(ApiUrls.appSharedPref, AppCompatActivity.MODE_PRIVATE)
        val appUserManagers: List<AppUserManager> = appDataBaseManager.userData
        appDataBaseManager.deletePatient(appUserManagers[0])
        if (callingFrom.equals("accountDelete", ignoreCase = true)) {
            val editor = appPreference.edit()
            editor.putBoolean("OnBoardingDone", false)
            editor.apply()
        }
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

}