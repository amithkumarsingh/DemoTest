package com.whitecoats.clinicplus.repositories

import android.app.Activity
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.whitecoats.clinicplus.apis.ApiMethodCalls
import com.whitecoats.clinicplus.apis.ApiUrls
import com.whitecoats.clinicplus.interfaces.ApiCallbackInterface
import com.whitecoats.clinicplus.models.PatientModel
import com.whitecoats.clinicplus.models.VideoCallModel
import org.json.JSONException
import org.json.JSONObject

class VideoCallRepository {
    private val apiMethodCalls = ApiMethodCalls()
    private val patientModelMutableLiveData = MutableLiveData<PatientModel?>()
    private val videoCallModelMutableLiveData = MutableLiveData<VideoCallModel?>()
    val exitVideoStatus = MutableLiveData<Boolean>()
    fun getPatientData(activity: Activity?, patientID: Int) {
        try {
            apiMethodCalls.getApiData(
                ApiUrls.getPatientDetail + "?patient_id=" + patientID,
                null,
                activity,
                object : ApiCallbackInterface {
                    override fun onSuccessResponse(response: String) {
                        try {
                            Log.d("Data Video", response)
                            var resObj = JSONObject(response)
                            resObj = resObj.getJSONObject("response").getJSONObject("response")
                            val gson = Gson()
                            val patientModel =
                                gson.fromJson(resObj.toString(), PatientModel::class.java)
                            patientModel.status = 200
                            patientModelMutableLiveData.postValue(patientModel)
                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }
                    }

                    override fun onErrorResponse(error: String) {
                        Log.d("Network Error Video", error)
                        try {
                            val resObj = JSONObject(error)
                            val gson = Gson()
                            val patientModel =
                                gson.fromJson(resObj.toString(), PatientModel::class.java)
                            patientModelMutableLiveData.postValue(patientModel)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                })
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun getVideoData(activity: Activity?, apptID: Int) {
        try {
            apiMethodCalls.getApiData(
                ApiUrls.getVideoData + "/" + apptID + "?joined_from=android",
                null,
                activity,
                object : ApiCallbackInterface {
                    override fun onSuccessResponse(response: String) {
                        try {
                            Log.d("Data Video", response)
                            var resObj = JSONObject(response)
                            resObj = resObj.getJSONObject("response").getJSONObject("response")
                            val gson = Gson()
                            val videoCallModel =
                                gson.fromJson(resObj.toString(), VideoCallModel::class.java)
                            videoCallModel.status = JSONObject(response).getInt("status_code")
                            videoCallModelMutableLiveData.postValue(videoCallModel)
                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }
                    }

                    override fun onErrorResponse(error: String) {
                        Log.d("Data Error", error)
                        try {
                            val errorObj = JSONObject(error)
                            val gson = Gson()
                            val videoCallModel =
                                gson.fromJson(errorObj.toString(), VideoCallModel::class.java)
                            videoCallModelMutableLiveData.postValue(videoCallModel)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                })
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun exitVideo(activity: Activity?, apptID: Int) {
        try {
            apiMethodCalls.getApiData(
                ApiUrls.exitVideo + "/" + apptID + "?joined_from=android",
                null,
                activity,
                object : ApiCallbackInterface {
                    override fun onSuccessResponse(response: String) {
                        Log.d("Data Video", response)
                        exitVideoStatus.postValue(true)
                    }

                    override fun onErrorResponse(error: String) {
                        Log.d("Data Error", error)
                        exitVideoStatus.postValue(false)
                    }
                })
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    val patientDetails: LiveData<PatientModel?>
        get() = patientModelMutableLiveData
    val videoData: LiveData<VideoCallModel?>
        get() = videoCallModelMutableLiveData

    fun clearVideoCallModelMutableLiveData() {
        videoCallModelMutableLiveData.postValue(null)
    }

    fun clearPatientModelMutableLiveData() {
        patientModelMutableLiveData.postValue(null)
    }

    fun getExitVideoStatus(): LiveData<Boolean> {
        return exitVideoStatus
    }

    companion object {
        var instance: VideoCallRepository? = null
            get() {
                if (field == null) {
                    field = VideoCallRepository()
                }
                return field
            }
            private set
    }
}