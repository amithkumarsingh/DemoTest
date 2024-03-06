package com.whitecoats.clinicplus.repositories

import android.app.Activity
import androidx.lifecycle.MutableLiveData
import com.whitecoats.clinicplus.apis.ApiMethodCalls
import com.whitecoats.clinicplus.apis.ApiUrls
import com.whitecoats.clinicplus.interfaces.ApiCallbackInterface
import org.json.JSONObject

/**
 * Created by Mohammad suhail ahmed on 24-07-2020.
 */
class VideoToolSettingRepository {
    private val apiMethodCalls = ApiMethodCalls()
    fun getVideoToolSetup(activity: Activity?): MutableLiveData<String> {
        val fileResponse = MutableLiveData<String>()
        try {
            apiMethodCalls.getApiData(
                ApiUrls.getVideoToolSetupHelp,
                null,
                activity,
                object : ApiCallbackInterface {
                    override fun onSuccessResponse(response: String) {
                        fileResponse.postValue(response)
                    }

                    override fun onErrorResponse(error: String) {
                        fileResponse.postValue(error)
                    }
                })
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return fileResponse
    }

    fun saveVideoToolPreference(
        activity: Activity?,
        videoToolPreferenceDetails: JSONObject?
    ): MutableLiveData<String> {
        val saveVideoToolPreferenceResponse = MutableLiveData<String>()
        try {
            apiMethodCalls.postApiData(
                ApiUrls.saveVideoToolPreference,
                videoToolPreferenceDetails,
                activity,
                object : ApiCallbackInterface {
                    override fun onSuccessResponse(response: String) {
                        saveVideoToolPreferenceResponse.postValue(response)
                    }

                    override fun onErrorResponse(error: String) {
                        saveVideoToolPreferenceResponse.postValue(error)
                    }
                })
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return saveVideoToolPreferenceResponse
    }

    companion object {
        private var videoToolSettingRepository: VideoToolSettingRepository? = null
        @JvmStatic
        val instance: VideoToolSettingRepository?
            get() {
                if (videoToolSettingRepository == null) {
                    videoToolSettingRepository = VideoToolSettingRepository()
                }
                return videoToolSettingRepository
            }
    }
}