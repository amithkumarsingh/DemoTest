package com.whitecoats.clinicplus.repositories

import android.app.Activity
import androidx.lifecycle.MutableLiveData
import com.whitecoats.clinicplus.apis.ApiMethodCalls
import com.whitecoats.clinicplus.apis.ApiUrls
import com.whitecoats.clinicplus.interfaces.ApiCallbackInterface
import org.json.JSONObject

/**
 * Created by Mohammad Suhail ahmed on 24-07-2020.
 */
class DashboardSettingRepository {
    private val apiMethodCalls = ApiMethodCalls()
    fun getDoctorNotificationPreferences(activity: Activity?): MutableLiveData<String> {
        val doctorPreferencesResponse = MutableLiveData<String>()
        val url = ApiUrls.authMe
        try {
            apiMethodCalls.getApiData(url, null, activity, object : ApiCallbackInterface {
                override fun onSuccessResponse(response: String) {
                    doctorPreferencesResponse.postValue(response)
                }

                override fun onErrorResponse(error: String) {
                    doctorPreferencesResponse.postValue(error)
                }
            })
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return doctorPreferencesResponse
    }

    fun saveNotificationSetting(
        activity: Activity?,
        notificationDetails: JSONObject?
    ): MutableLiveData<String> {
        val saveNotificationDetailsResponse = MutableLiveData<String>()
        try {
            apiMethodCalls.postApiData(
                ApiUrls.saveNotificationSetting,
                notificationDetails,
                activity,
                object : ApiCallbackInterface {
                    override fun onSuccessResponse(response: String) {
                        saveNotificationDetailsResponse.postValue(response)
                    }

                    override fun onErrorResponse(error: String) {
                        saveNotificationDetailsResponse.postValue(error)
                    }
                })
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return saveNotificationDetailsResponse
    }

    companion object {
        private var dashboardSettingRepository: DashboardSettingRepository? = null
        @JvmStatic
        val instance: DashboardSettingRepository?
            get() {
                if (dashboardSettingRepository == null) {
                    dashboardSettingRepository = DashboardSettingRepository()
                }
                return dashboardSettingRepository
            }
    }
}