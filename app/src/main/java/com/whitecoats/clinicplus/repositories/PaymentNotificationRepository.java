package com.whitecoats.clinicplus.repositories;

import android.app.Activity;
import android.app.ProgressDialog;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.whitecoats.clinicplus.apis.ApiMethodCalls;
import com.whitecoats.clinicplus.apis.ApiUrls;
import com.whitecoats.clinicplus.interfaces.ApiCallbackInterface;

import org.json.JSONObject;

public class PaymentNotificationRepository {
    private static PaymentNotificationRepository instance;
    private ApiMethodCalls apiMethodCalls = new ApiMethodCalls();
    MutableLiveData<String> data = new MutableLiveData<>();
    private ProgressDialog loadingDialog;
    private JSONObject jsonObject;
    MutableLiveData<String> paymentNotificationResponse = new MutableLiveData<>();

    public static PaymentNotificationRepository getInstance() {
        if (instance == null) {
            instance = new PaymentNotificationRepository();
        }
        return instance;
    }

    //get Api
    public MutableLiveData<String> getPaymentNotification(Activity activity){
        MutableLiveData<String> getPaymentResponse = new MutableLiveData<>();
        apiMethodCalls.getApiData(ApiUrls.get_payment_notification_preferences, null, activity, new ApiCallbackInterface() {
            @Override
            public void onSuccessResponse(String response) {
                getPaymentResponse.postValue(response);
            }

            @Override
            public void onErrorResponse(String error) {
                getPaymentResponse.postValue(error);
            }
        });

        return getPaymentResponse;
    }
    //post api
    public MutableLiveData<String> PaymentNotificationResponse(Activity activity, JSONObject jsonObject) {
        try {
            apiMethodCalls.postApiData(ApiUrls.payment_notification_preferences, jsonObject, activity,
                    new ApiCallbackInterface() {
                        @Override
                        public void onSuccessResponse(String response) {
                            Log.d("NotificSetting_Response", response);
                            paymentNotificationResponse.postValue(response);
                        }

                        @Override
                        public void onErrorResponse(String error) {
                            Log.d("NotificSetting_Error", error);
                            paymentNotificationResponse.postValue(error);

                        }
                    });

        } catch (Exception e) {
            e.printStackTrace();
        }

        return paymentNotificationResponse;
    }

}
