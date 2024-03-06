package com.whitecoats.clinicplus.viewmodels;

import android.app.Activity;
import android.util.Log;

import com.whitecoats.clinicplus.models.EMRFamilyModel;
import com.whitecoats.clinicplus.models.PatientModel;
import com.whitecoats.clinicplus.repositories.DashboardRepository;
import com.whitecoats.clinicplus.repositories.EMRPatientProfileRepository;

import org.json.JSONObject;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class EMRPatientProfileViewModel extends ViewModel {

    private EMRPatientProfileRepository patientProfileRepository;
    public static MutableLiveData<String> familyDataResponse = new MutableLiveData<>();

    public void init() {
        patientProfileRepository = EMRPatientProfileRepository.getInstance();
    }

    public LiveData<PatientModel> getPatientDetails(Activity activity, int patientId) {
        return patientProfileRepository.getPatientDetails(activity, patientId);
    }

    public LiveData<String> updatePatientDetails(Activity activity, int patientId, JSONObject parameter) {
        return  patientProfileRepository.updatePatientDetails(activity, patientId, parameter);
    }

    public LiveData<List<EMRFamilyModel>> getFamilyData(Activity activity, String param) {
        return patientProfileRepository.getFamilyData(activity, param);
    }

    public LiveData<String> addFamilyData(Activity activity, JSONObject parameter) {
        return  patientProfileRepository.addFamilyData(activity, parameter);
    }

}
