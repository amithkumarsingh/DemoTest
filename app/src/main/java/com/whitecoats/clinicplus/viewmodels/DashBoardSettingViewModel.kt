package com.whitecoats.clinicplus.viewmodels

import android.app.Activity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.whitecoats.clinicplus.repositories.DashboardSettingRepository
import com.whitecoats.clinicplus.repositories.DashboardSettingRepository.Companion.instance
import org.json.JSONObject

/**
 * Created by Mohammad suhail ahmed on 24-07-2020.
 */
class DashBoardSettingViewModel : ViewModel() {
    private var dashBoardSettingRepository: DashboardSettingRepository? = null
    fun init() {
        dashBoardSettingRepository = instance
    }

    fun getDoctorSettingPreferences(activity: Activity?): MutableLiveData<String> {
        return dashBoardSettingRepository!!.getDoctorNotificationPreferences(activity)
    }

    fun saveNotificationStatus(
        activity: Activity?,
        notificationDetails: JSONObject?
    ): LiveData<String> {
        return dashBoardSettingRepository!!.saveNotificationSetting(activity, notificationDetails)
    }
}