package com.whitecoats.clinicplus.repositories;

import android.app.Activity;

import androidx.lifecycle.MutableLiveData;

import com.whitecoats.clinicplus.apis.ApiMethodCalls;
import com.whitecoats.clinicplus.apis.ApiUrls;
import com.whitecoats.clinicplus.interfaces.ApiCallbackInterface;

import org.json.JSONObject;

public class DoctorSessionRepository {
    private static DoctorSessionRepository instance;
    private ApiMethodCalls apiMethodCalls = new ApiMethodCalls();
    MutableLiveData<String> doctorSessionRepositoryResponse = new MutableLiveData<>();

    public static DoctorSessionRepository getInstance() {
        if (instance == null) {
            instance = new DoctorSessionRepository();
        }
        return instance;
    }

    public MutableLiveData<String> getDoctorSessionId(Activity activity, JSONObject jsonObject) {
        MutableLiveData<String> sessionIdResponse = new MutableLiveData<>();
        try {
            apiMethodCalls.postApiData(ApiUrls.doctorSessionSaveLogin, jsonObject, activity, new ApiCallbackInterface() {

                @Override
                public void onSuccessResponse(String response) {
                    sessionIdResponse.postValue(response);
                }

                @Override
                public void onErrorResponse(String error) {
                    sessionIdResponse.postValue(error);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sessionIdResponse;
    }
}
