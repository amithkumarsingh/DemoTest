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
public class EMRSharedRecordRepository {

    private static EMRSharedRecordRepository emrSharedRecordRepository;
    private ApiMethodCalls apiMethodCalls = new ApiMethodCalls();
    public static EMRSharedRecordRepository getInstance() {
        if (emrSharedRecordRepository == null) {
            emrSharedRecordRepository = new EMRSharedRecordRepository();
        }

        return emrSharedRecordRepository;
    }
    public MutableLiveData<String> getSharedRecords(Activity activity, String duration, String groupby, int parientId){
        MutableLiveData<String> sharedRecordsResponse = new MutableLiveData<>();
        String url = ApiUrls.getEMRSharedRecords + "?duration=" + duration + "&group_by=" + groupby + "&patient_id=" + parientId;
        try {
            apiMethodCalls.getApiData(url, null, activity, new ApiCallbackInterface() {
                @Override
                public void onSuccessResponse(String response) {
                    sharedRecordsResponse.postValue(response);
                }

                @Override
                public void onErrorResponse(String error) {
                    sharedRecordsResponse.postValue(error);
                }
            });
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return sharedRecordsResponse;
    }
    public MutableLiveData<String> getFileFromUrl(Activity activity, String url){
        MutableLiveData<String> fileResponse = new MutableLiveData<>();
        JSONObject params = new JSONObject();
        try {
            params.put("url", url.trim());
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            apiMethodCalls.postApiData(ApiUrls.getFileFromUrl, params, activity, new ApiCallbackInterface() {
                @Override
                public void onSuccessResponse(String response) {
                    fileResponse.postValue(response);
                }

                @Override
                public void onErrorResponse(String error) {
                    fileResponse.postValue(error);
                }
            });
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return fileResponse;
    }
}
