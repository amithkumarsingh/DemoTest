package com.whitecoats.clinicplus.viewmodels

import android.app.Activity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.whitecoats.clinicplus.repositories.VideoToolSettingRepository
import com.whitecoats.clinicplus.repositories.VideoToolSettingRepository.Companion.instance
import org.json.JSONObject

/**
 * Created by Mohammad suhail ahmed on 24-07-2020.
 */
class VideoToolSettingViewModel : ViewModel() {
    private var videoToolSettingRepository: VideoToolSettingRepository? = null
    fun init() {
        videoToolSettingRepository = instance
    }

    fun getVideoToolSetupHelp(activity: Activity?): MutableLiveData<String> {
        return videoToolSettingRepository!!.getVideoToolSetup(activity)
    }

    fun saveVideoToolPre(activity: Activity?, videoToolPreference: JSONObject?): LiveData<String> {
        return videoToolSettingRepository!!.saveVideoToolPreference(activity, videoToolPreference)
    }
}