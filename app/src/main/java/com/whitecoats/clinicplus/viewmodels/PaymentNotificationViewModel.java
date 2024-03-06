package com.whitecoats.clinicplus.viewmodels;

import android.app.Activity;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.whitecoats.clinicplus.repositories.PaymentNotificationRepository;

import org.json.JSONObject;

public class PaymentNotificationViewModel extends ViewModel {
    private PaymentNotificationRepository paymentNotificationRepository;

    public void init(){
        paymentNotificationRepository = PaymentNotificationRepository.getInstance();
    }

    //get notification settings
    public LiveData<String> getNotificationSettings(Activity activity){
        return paymentNotificationRepository.getPaymentNotification(activity);
    }

    //post notification settings
    public LiveData<String> postNotificationSettings(Activity activity, JSONObject jsonObject){
        return paymentNotificationRepository.PaymentNotificationResponse(activity,jsonObject);
    }


}
