package com.whitecoats.clinicplus.viewmodels;

import android.app.Activity;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.whitecoats.clinicplus.repositories.EMRSharedRecordRepository;
import com.whitecoats.clinicplus.repositories.VoiceEMRRepository;

import org.json.JSONObject;

/**
 * Created by Mohammad suhail ahmed on 24-07-2020.
 */
public class VoiceEMRViewModel extends ViewModel {
    private VoiceEMRRepository voiceEMRRepository;

    public void init(){
        voiceEMRRepository = VoiceEMRRepository.getInstance();
    }
    public MutableLiveData<String> getParsingSimboData(Activity activity, JSONObject simboPardingDataObject){
        return voiceEMRRepository.getParsingSimboDataObject(activity,simboPardingDataObject);
    }
    public LiveData<String> saveSymptom(Activity activity, JSONObject SymptomDetails){
        return voiceEMRRepository.saveSymptom(activity,SymptomDetails);
    }
    public LiveData<String> saveDiagnosis(Activity activity, JSONObject diagnosisDetails){
        return voiceEMRRepository.saveDiagnosis(activity,diagnosisDetails);
    }
    public LiveData<String> saveInvestigation(Activity activity, JSONObject investigationDetails){
        return voiceEMRRepository.saveInvestigation(activity,investigationDetails);
    }
    public LiveData<String> saveObservationAndTreatmentPlan(Activity activity, JSONObject recordDetails){
        return voiceEMRRepository.saveObservationAndTreatmentPlanRecords(activity,recordDetails);
    }
    public LiveData<String> getSimboAuthKey(Activity activity){
        return voiceEMRRepository.getSimboAuthKey(activity);
    }
}
