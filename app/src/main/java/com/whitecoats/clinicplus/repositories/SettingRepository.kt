package com.whitecoats.clinicplus.repositories

import android.app.Activity
import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.whitecoats.clinicplus.apis.ApiMethodCalls
import com.whitecoats.clinicplus.apis.ApiUrls
import com.whitecoats.clinicplus.interfaces.ApiCallbackInterface
import com.whitecoats.clinicplus.utils.SingleLiveEvent
import org.json.JSONObject

class SettingRepository {
    private val apiMethodCalls = ApiMethodCalls()
    fun saveTimeFormat(
        activity: Activity?,
        timeFormatDetails: JSONObject?
    ): MutableLiveData<String> {
        val updateTimePreferenceResponse = MutableLiveData<String>()
        try {
            apiMethodCalls.postApiData(
                ApiUrls.updateTimeFormatPreference,
                timeFormatDetails,
                activity,
                object : ApiCallbackInterface {
                    override fun onSuccessResponse(response: String) {
                        updateTimePreferenceResponse.postValue(response)
                    }

                    override fun onErrorResponse(error: String) {
                        updateTimePreferenceResponse.postValue(error)
                    }
                })
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return updateTimePreferenceResponse
    }

    fun getAppRemainderDetails(activity: Context?): MutableLiveData<String> {
        val appReminderDetails = MutableLiveData<String>()
        try {
            apiMethodCalls.getApiDataWithContext(
                ApiUrls.getAppReminder,
                null,
                activity,
                object : ApiCallbackInterface {
                    override fun onSuccessResponse(response: String) {
                        appReminderDetails.postValue(response)
                    }

                    override fun onErrorResponse(error: String) {
                        appReminderDetails.postValue(error)
                    }
                })
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return appReminderDetails
    }

    fun getAppReminderRescheduleNotificationPre(activity: Context?): MutableLiveData<String> {
        val appReminderRescheduleNotiPre = MutableLiveData<String>()
        try {
            apiMethodCalls.getApiDataWithContext(
                ApiUrls.getAppReschedulePreferences,
                null,
                activity,
                object : ApiCallbackInterface {
                    override fun onSuccessResponse(response: String) {
                        appReminderRescheduleNotiPre.postValue(response)
                    }

                    override fun onErrorResponse(error: String) {
                        appReminderRescheduleNotiPre.postValue(error)
                    }
                })
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return appReminderRescheduleNotiPre
    }

    fun updateRescheduleNotificationPreference(
        activity: Context?,
        req: JSONObject?
    ): MutableLiveData<String> {
        val rescheduleNotificationPreference = MutableLiveData<String>()
        try {
            apiMethodCalls.postApiDataWithContext(
                ApiUrls.updateRescheduleNotificationPreferences,
                req,
                activity,
                object : ApiCallbackInterface {
                    override fun onSuccessResponse(response: String) {
                        rescheduleNotificationPreference.postValue(response)
                    }

                    override fun onErrorResponse(error: String) {
                        rescheduleNotificationPreference.postValue(error)
                    }
                })
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return rescheduleNotificationPreference
    }

    fun upDateAppReminder(activity: Context?, params: JSONObject?): SingleLiveEvent<String> {
        val upDateAppReminder = SingleLiveEvent<String>()
        try {
            apiMethodCalls.postApiDataWithContext(
                ApiUrls.saveAppReminder,
                params,
                activity,
                object : ApiCallbackInterface {
                    override fun onSuccessResponse(response: String) {
                        upDateAppReminder.postValue(response)
                    }

                    override fun onErrorResponse(error: String) {
                        upDateAppReminder.postValue(error)
                    }
                })
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return upDateAppReminder
    }

    fun getDoctorsBlockTime(activity: Context?): SingleLiveEvent<String> {
        val blockTime = SingleLiveEvent<String>()
        try {
            apiMethodCalls.getApiDataWithContext(
                ApiUrls.getDoctorTimeBlock,
                null,
                activity,
                object : ApiCallbackInterface {
                    override fun onSuccessResponse(response: String) {
                        blockTime.postValue(response)
                    }

                    override fun onErrorResponse(error: String) {
                        blockTime.postValue(error)
                    }
                })
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return blockTime
    }

    fun deleteDoctorTimeBlock(
        activity: Context?,
        apiURL: String?,
        req: JSONObject?
    ): SingleLiveEvent<String> {
        val deleteBlockTime = SingleLiveEvent<String>()
        try {
            apiMethodCalls.postApiDataWithContext(
                apiURL,
                req,
                activity,
                object : ApiCallbackInterface {
                    override fun onSuccessResponse(response: String) {
                        deleteBlockTime.postValue(response)
                    }

                    override fun onErrorResponse(error: String) {
                        deleteBlockTime.postValue(error)
                    }
                })
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return deleteBlockTime
    }

    fun getDoctorServiceForTimeBlock(activity: Context?): SingleLiveEvent<String> {
        val getServicesTime = SingleLiveEvent<String>()
        try {
            apiMethodCalls.getApiDataWithContext(
                ApiUrls.doctorServiceTimeBlock,
                null,
                activity,
                object : ApiCallbackInterface {
                    override fun onSuccessResponse(response: String) {
                        getServicesTime.postValue(response)
                    }

                    override fun onErrorResponse(error: String) {
                        getServicesTime.postValue(error)
                    }
                })
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return getServicesTime
    }

    fun saveTimeBlock(activity: Context?, req: JSONObject?): SingleLiveEvent<String> {
        val saveTimeBlock = SingleLiveEvent<String>()
        try {
            apiMethodCalls.postApiDataWithContext(
                ApiUrls.saveDoctorServiceTimeBlock,
                req,
                activity,
                object : ApiCallbackInterface {
                    override fun onSuccessResponse(response: String) {
                        saveTimeBlock.postValue(response)
                    }

                    override fun onErrorResponse(error: String) {
                        saveTimeBlock.postValue(error)
                    }
                })
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return saveTimeBlock
    }

    companion object {
        private var settingRepository: SettingRepository? = null
        val instance: SettingRepository?
            get() {
                if (settingRepository == null) {
                    settingRepository = SettingRepository()
                }
                return settingRepository
            }
    }
}