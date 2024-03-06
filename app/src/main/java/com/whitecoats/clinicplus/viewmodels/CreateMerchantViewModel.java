package com.whitecoats.clinicplus.viewmodels;

import android.app.Activity;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.whitecoats.clinicplus.repositories.CreateMerchantRepository;

import org.json.JSONObject;

public class CreateMerchantViewModel extends ViewModel {
    private CreateMerchantRepository createMerchantRepository;

    public void init(){
        createMerchantRepository = CreateMerchantRepository.getInstance();
    }
    public LiveData<String> createMerchantAccount(Activity activity, JSONObject jsonObject) {
        return  createMerchantRepository.createMerchantAccResponse(activity, jsonObject);
    }

    public LiveData<String> getCategoriesDetails(Activity activity){
        return createMerchantRepository.getCategoriesNames(activity);
    }

    public LiveData<String> connectExistingAcc(Activity activity, JSONObject jsonObject){
        return createMerchantRepository.createExistingAccount(activity,jsonObject);
    }



}
