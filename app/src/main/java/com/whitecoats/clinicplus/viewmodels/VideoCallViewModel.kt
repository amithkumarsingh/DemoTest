package com.whitecoats.clinicplus.viewmodels

import android.app.Activity
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.whitecoats.clinicplus.models.PatientModel
import com.whitecoats.clinicplus.models.VideoCallModel
import com.whitecoats.clinicplus.repositories.VideoCallRepository

class VideoCallViewModel : ViewModel() {
    private var videoCallRepository: VideoCallRepository? = null
    fun init() {
        videoCallRepository = VideoCallRepository.instance
    }

    fun getPatientData(activity: Activity?, patientID: Int) {
        videoCallRepository!!.getPatientData(activity, patientID)
    }

    fun getVideoSession(activity: Activity?, apptId: Int) {
        videoCallRepository!!.getVideoData(activity, apptId)
    }

    fun exitVideo(activity: Activity?, apptId: Int) {
        videoCallRepository!!.exitVideo(activity, apptId)
    }

    val patientData: LiveData<PatientModel?>
        get() = videoCallRepository!!.patientDetails
    val videoCallModelLiveData: LiveData<VideoCallModel?>
        get() = videoCallRepository!!.videoData
    val exitVideoStatus: LiveData<Boolean>
        get() = videoCallRepository!!.exitVideoStatus

    fun clearData() {
        videoCallRepository!!.clearVideoCallModelMutableLiveData()
    }

    fun clearPatientData() {
        videoCallRepository!!.clearPatientModelMutableLiveData()
    }
}