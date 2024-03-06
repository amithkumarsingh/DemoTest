package com.whitecoats.clinicplus.repositories;

import android.app.Activity;

import androidx.lifecycle.MutableLiveData;

import com.whitecoats.clinicplus.apis.ApiMethodCalls;
import com.whitecoats.clinicplus.apis.ApiUrls;
import com.whitecoats.clinicplus.interfaces.ApiCallbackInterface;

import org.json.JSONObject;

/**
 * Created by Mohammad suhail ahmed on 24-07-2020.
 */
public class VoiceEMRRepository {

    private static VoiceEMRRepository voiceEmrRepository;
    private ApiMethodCalls apiMethodCalls = new ApiMethodCalls();
    public static VoiceEMRRepository getInstance() {
        if (voiceEmrRepository == null) {
            voiceEmrRepository = new VoiceEMRRepository();
        }

        return voiceEmrRepository;
    }
    public MutableLiveData<String> getParsingSimboDataObject(Activity activity,JSONObject simboResponseDataObject){
        MutableLiveData<String> getParsingSimbResponse = new MutableLiveData<>();
        try{
//            JSONObject postData = new JSONObject();
//            postData.put("appointment_id",apptId);
//            postData.put("chat_id",chatId);
//            postData.put("encounter_date_time",dateTime);
//            postData.put("encounter_mode",mode);
//            postData.put("episode_id",episodeId);
//            postData.put("patient_id",patientId);
            apiMethodCalls.postApiData(ApiUrls.getParsingSimboData, simboResponseDataObject, activity, new ApiCallbackInterface() {
                @Override
                public void onSuccessResponse(String response) {
                    getParsingSimbResponse.postValue(response);
                }

                @Override
                public void onErrorResponse(String error) {
                    getParsingSimbResponse.postValue(error);
                }
            });
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return getParsingSimbResponse;
    }

    public MutableLiveData<String> saveSymptom(Activity activity, JSONObject SymptomDetails){
        MutableLiveData<String> saveSymptomResponse = new MutableLiveData<>();
        try {
            apiMethodCalls.postApiData(ApiUrls.saveMultipleNewSymptom, SymptomDetails, activity, new ApiCallbackInterface() {
                @Override
                public void onSuccessResponse(String response) {
                    saveSymptomResponse.postValue(response);
                }

                @Override
                public void onErrorResponse(String error) {
                    saveSymptomResponse.postValue(error);
                }
            });
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return saveSymptomResponse;
    }

    public MutableLiveData<String> saveDiagnosis(Activity activity, JSONObject diagnosisDetails){
        MutableLiveData<String> saveDiagnosisResponse = new MutableLiveData<>();
        try {
            apiMethodCalls.postApiData(ApiUrls.saveMultipleNewDiagnosis, diagnosisDetails, activity, new ApiCallbackInterface() {
                @Override
                public void onSuccessResponse(String response) {
                    saveDiagnosisResponse.postValue(response);
                }

                @Override
                public void onErrorResponse(String error) {
                    saveDiagnosisResponse.postValue(error);
                }
            });
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return saveDiagnosisResponse;
    }


    public MutableLiveData<String> saveInvestigation(Activity activity, JSONObject investigationDetails){
        MutableLiveData<String> saveInvestigationResponse = new MutableLiveData<>();
        try {
            apiMethodCalls.postApiData(ApiUrls.saveMultipleNewInvestigationResult, investigationDetails, activity, new ApiCallbackInterface() {
                @Override
                public void onSuccessResponse(String response) {
                    saveInvestigationResponse.postValue(response);
                }

                @Override
                public void onErrorResponse(String error) {
                    saveInvestigationResponse.postValue(error);
                }
            });
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return saveInvestigationResponse;
    }


    public MutableLiveData<String> saveObservationAndTreatmentPlanRecords(Activity activity, JSONObject recordDetails){
        MutableLiveData<String> saveObservationAndTreatmentPlanRecordsResponse = new MutableLiveData<>();
        try {
            apiMethodCalls.postApiData(ApiUrls.saveMultipleNewObservationAndTreatmentPlanResult, recordDetails, activity, new ApiCallbackInterface() {
                @Override
                public void onSuccessResponse(String response) {
                    saveObservationAndTreatmentPlanRecordsResponse.postValue(response);
                }

                @Override
                public void onErrorResponse(String error) {
                    saveObservationAndTreatmentPlanRecordsResponse.postValue(error);
                }
            });
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return saveObservationAndTreatmentPlanRecordsResponse;
    }


    public MutableLiveData<String> getSimboAuthKey(Activity activity){
        MutableLiveData<String> getSimboAuthKeyResponse = new MutableLiveData<>();
        String url = ApiUrls.getSimboAuthKey;
        try {
            apiMethodCalls.getApiData(url, null, activity, new ApiCallbackInterface() {
                @Override
                public void onSuccessResponse(String response) {
                    getSimboAuthKeyResponse.postValue(response);
                }

                @Override
                public void onErrorResponse(String error) {
                    getSimboAuthKeyResponse.postValue(error);

                }
            });
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return  getSimboAuthKeyResponse;
    }




}
