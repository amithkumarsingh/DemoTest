package com.whitecoats.clinicplus.repositories;

import android.app.Activity;
import android.app.ProgressDialog;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.whitecoats.clinicplus.apis.ApiMethodCalls;
import com.whitecoats.clinicplus.apis.ApiUrls;
import com.whitecoats.clinicplus.interfaces.ApiCallbackInterface;

import org.json.JSONObject;

public class CreateMerchantRepository {
    private static CreateMerchantRepository instance;
    private ApiMethodCalls apiMethodCalls = new ApiMethodCalls();
    MutableLiveData<String> data = new MutableLiveData<>();
    private ProgressDialog loadingDialog;
    private JSONObject jsonObject;
    MutableLiveData<String> createMerchantResponse = new MutableLiveData<>();

    public static CreateMerchantRepository getInstance() {
        if (instance == null) {
            instance = new CreateMerchantRepository();
        }
        return instance;
    }

    public MutableLiveData<String> createMerchantAccResponse(Activity activity, JSONObject jsonObject) {
        try {
            apiMethodCalls.getApiData(ApiUrls.getPaymentMerchant, jsonObject, activity,
                    new ApiCallbackInterface() {
                        @Override
                        public void onSuccessResponse(String response) {
                            Log.d("CreateMer_Response", response);
                            createMerchantResponse.postValue(response);
                        }

                        @Override
                        public void onErrorResponse(String error) {
                            Log.d("CreateMer_Error", error);
                            createMerchantResponse.postValue(error);
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return createMerchantResponse;
    }

    public MutableLiveData<String> createExistingAccount(Activity activity, JSONObject jsonObject) {
        MutableLiveData<String> existingAccResponse = new MutableLiveData<>();
        try {
            apiMethodCalls.getApiData(ApiUrls.connectExistingAccount, jsonObject, activity, new ApiCallbackInterface() {
                @Override
                public void onSuccessResponse(String response) {
                    Log.d("ExistingMer_Response", response);
                    existingAccResponse.postValue(response);
                }

                @Override
                public void onErrorResponse(String error) {
                    existingAccResponse.postValue(error);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return createMerchantResponse;
    }


    public MutableLiveData<String> getCategoriesNames(Activity activity) {
        MutableLiveData<String> CategoriesResponse = new MutableLiveData<>();
        try {
            apiMethodCalls.getApiData(ApiUrls.getCatFieldDetails + "?type=3&category_name=Ecommerce", null, activity, new ApiCallbackInterface() {
                @Override
                public void onSuccessResponse(String response) {
                    CategoriesResponse.postValue(response);
                }

                @Override
                public void onErrorResponse(String error) {
                    CategoriesResponse.postValue(error);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return CategoriesResponse;
    }

}
