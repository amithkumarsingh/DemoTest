package com.whitecoats.clinicplus.viewmodels

import android.app.Activity
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.whitecoats.clinicplus.repositories.SettingRepository
import org.json.JSONObject

class SettingViewModel(application: Application) : AndroidViewModel(application) {
    private var settingRepository: SettingRepository? = null
    fun init() {
        settingRepository = SettingRepository.instance
    }

    fun saveTimeFormat(activity: Activity?, timeFormatDetails: JSONObject?): LiveData<String> {
        return settingRepository!!.saveTimeFormat(activity, timeFormatDetails)
    }

    fun settingRemainderDetails(): LiveData<String> {
        return settingRepository!!.getAppRemainderDetails(getApplication<Application>().applicationContext)
    }

    fun settingReminderRescheduleNotificationPre(): LiveData<String> {
        return settingRepository!!.getAppReminderRescheduleNotificationPre(getApplication<Application>().applicationContext)
    }

    fun updateRescheduleNotificationPreference(req: JSONObject?): LiveData<String> {
        return settingRepository!!.updateRescheduleNotificationPreference(
            getApplication<Application>().applicationContext,
            req
        )
    }

    fun upDateAppReminder(req: JSONObject?): LiveData<String> {
        return settingRepository!!.upDateAppReminder(
            getApplication<Application>().applicationContext,
            req
        )
    }

    val doctorBlockTime: LiveData<String>
        get() = settingRepository!!.getDoctorsBlockTime(getApplication<Application>().applicationContext)

    fun deleteDoctorTimeBlock(apiURL: String?, req: JSONObject?): LiveData<String> {
        return settingRepository!!.deleteDoctorTimeBlock(
            getApplication<Application>().applicationContext,
            apiURL,
            req
        )
    }

    val doctorServiceForTimeBlock: LiveData<String>
        get() = settingRepository!!.getDoctorServiceForTimeBlock(getApplication<Application>().applicationContext)

    fun saveTimeBlock(req: JSONObject?): LiveData<String> {
        return settingRepository!!.saveTimeBlock(
            getApplication<Application>().applicationContext,
            req
        )
    }
}