package com.whitecoats.clinicplus.repositories

import android.app.Activity
import androidx.lifecycle.MutableLiveData
import com.whitecoats.clinicplus.apis.ApiMethodCalls
import com.whitecoats.clinicplus.apis.ApiUrls
import com.whitecoats.clinicplus.interfaces.ApiCallbackInterface
import org.json.JSONObject


class AddPatientRepository {
    private val apiMethodCalls = ApiMethodCalls()
    fun getInterfaceName(activity: Activity?): MutableLiveData<String> {
        val interfaceResponse = MutableLiveData<String>()
        try {
            apiMethodCalls.getApiData(
                ApiUrls.getDoctorInterface,
                null,
                activity,
                object : ApiCallbackInterface {
                    override fun onSuccessResponse(response: String) {
                        interfaceResponse.postValue(response)
                    }

                    override fun onErrorResponse(error: String) {
                        interfaceResponse.postValue(error)
                    }
                })
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return interfaceResponse
    }

    fun savePatient(activity: Activity?, patientDetails: JSONObject?): MutableLiveData<String> {
        val savePatientResponse = MutableLiveData<String>()
        try {
            apiMethodCalls.postApiData(
                ApiUrls.savePatient,
                patientDetails,
                activity,
                object : ApiCallbackInterface {
                    override fun onSuccessResponse(response: String) {
                        savePatientResponse.postValue(response)
                    }

                    override fun onErrorResponse(error: String) {
                        savePatientResponse.postValue(error)
                    }
                })
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return savePatientResponse
    }

    companion object {
        private var addPatientRepository: AddPatientRepository? = null
        @JvmStatic
        val instance: AddPatientRepository?
            get() {
                if (addPatientRepository == null) {
                    addPatientRepository = AddPatientRepository()
                }
                return addPatientRepository
            }
    }
}