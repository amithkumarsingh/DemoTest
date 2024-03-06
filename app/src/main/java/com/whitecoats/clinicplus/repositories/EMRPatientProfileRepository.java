package com.whitecoats.clinicplus.repositories;

import android.app.Activity;
import android.app.ProgressDialog;
import android.util.Log;

import com.google.gson.Gson;
import com.whitecoats.clinicplus.apis.ApiMethodCalls;
import com.whitecoats.clinicplus.apis.ApiUrls;
import com.whitecoats.clinicplus.constants.AppConstants;
import com.whitecoats.clinicplus.interfaces.ApiCallbackInterface;
import com.whitecoats.clinicplus.models.DashboardMenuModel;
import com.whitecoats.clinicplus.models.EMRFamilyModel;
import com.whitecoats.clinicplus.models.PatientModel;
import com.whitecoats.clinicplus.utils.SingleLiveEvent;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import androidx.lifecycle.MutableLiveData;

public class EMRPatientProfileRepository {

    private static EMRPatientProfileRepository instance;
    private ApiMethodCalls apiMethodCalls = new ApiMethodCalls();
    MutableLiveData<PatientModel> patientDetails = new MutableLiveData<>();
    MutableLiveData<List<EMRFamilyModel>> familyData = new MutableLiveData<>();
    SingleLiveEvent<String> updateResponse = new SingleLiveEvent<>();
    private ProgressDialog loadingDialog;


    public static EMRPatientProfileRepository getInstance() {
        if (instance == null) {
            instance = new EMRPatientProfileRepository();
        }

        return instance;
    }

    public MutableLiveData<PatientModel> getPatientDetails(Activity activity, int patientId) {
        try {

            apiMethodCalls.getApiData(ApiUrls.getPatientDetails + "?patient_id=" + patientId, null, activity,
                    new ApiCallbackInterface() {
                        @Override
                        public void onSuccessResponse(String response) {
                            Log.d("Response", response);

                            try {
                                JSONObject resObj = new JSONObject(response);
                                resObj = resObj.getJSONObject("response").getJSONObject("response");
                                Gson gson = new Gson();
                                PatientModel patientModel = gson.fromJson(String.valueOf(resObj), PatientModel.class);

                                patientDetails.postValue(patientModel);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onErrorResponse(String error) {

                        }
                    });

        } catch (Exception e) {
            e.printStackTrace();
        }

        return patientDetails;
    }

    public SingleLiveEvent<String> updatePatientDetails(Activity activity, int patientId, JSONObject parameter) {
        try {
            apiMethodCalls.postApiData(ApiUrls.updatePatientDetails + "/" + patientId, parameter, activity,
                    new ApiCallbackInterface() {
                        @Override
                        public void onSuccessResponse(String response) {
                            Log.d("Response", response);
                            updateResponse.postValue(response);
                        }

                        @Override
                        public void onErrorResponse(String error) {
                            Log.d("Error", error);
                            updateResponse.postValue(error);
                        }
                    });

        } catch (Exception e) {
            e.printStackTrace();
        }

        return updateResponse;
    }

    public MutableLiveData<List<EMRFamilyModel>> getFamilyData(Activity activity, String param) {

        try {
            apiMethodCalls.getApiData(ApiUrls.getFamilyData + "?" + param, null, activity, new ApiCallbackInterface() {
                @Override
                public void onSuccessResponse(String response) {
                    Log.d("Response", response);

                    try {
                        List<EMRFamilyModel> familyModels = new ArrayList<>();
                        JSONObject resObj = new JSONObject(response);
                        resObj = resObj.getJSONObject("response").getJSONObject("response");

                        AppConstants.relationMaster = resObj.getJSONArray("relation_master");

                        for (int i = 0; i < resObj.getJSONObject("existing_relations").getJSONArray("data").length(); i++) {
                            JSONObject tempObj = resObj.getJSONObject("existing_relations").getJSONArray("data").getJSONObject(i);
                            Gson gson = new Gson();
                            EMRFamilyModel familyModel = gson.fromJson(String.valueOf(tempObj), EMRFamilyModel.class);
                            familyModels.add(familyModel);
                        }
//
                        familyData.postValue(familyModels);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onErrorResponse(String error) {
                    Log.d("Response", error);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }


        return familyData;
    }

    public MutableLiveData<String> addFamilyData(Activity activity, JSONObject parameter) {
        try {
            apiMethodCalls.postApiData(ApiUrls.addFamilyData, parameter, activity,
                    new ApiCallbackInterface() {
                        @Override
                        public void onSuccessResponse(String response) {
                            Log.d("Response", response);
                            updateResponse.postValue(response);
                        }

                        @Override
                        public void onErrorResponse(String error) {
                            Log.d("Error", error);
                            updateResponse.postValue(error);
                        }
                    });

        } catch (Exception e) {
            e.printStackTrace();
        }

        return updateResponse;
    }

}
