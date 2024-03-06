package com.whitecoats.clinicplus.viewmodels

import android.app.Activity
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.whitecoats.clinicplus.repositories.AddPatientRepository
import com.whitecoats.clinicplus.repositories.AddPatientRepository.Companion.instance
import org.json.JSONObject


class AddPatientViewModel : ViewModel() {
    private var addPatientRepository: AddPatientRepository? = null
    fun init() {
        addPatientRepository = instance
    }

    fun getInterfaceDetails(activity: Activity?): LiveData<String> {
        return addPatientRepository!!.getInterfaceName(activity)
    }

    fun savePatient(activity: Activity?, patientDetails: JSONObject?): LiveData<String> {
        return addPatientRepository!!.savePatient(activity, patientDetails)
    }
}