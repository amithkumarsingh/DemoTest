package com.whitecoats.clinicplus.viewmodels;

import android.app.Activity;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.whitecoats.clinicplus.repositories.CreateMerchantRepository;
import com.whitecoats.clinicplus.repositories.CreateNewAccountRepository;

import org.json.JSONObject;

public class CreateNewAccountViewModel extends ViewModel {
    private CreateNewAccountRepository createNewAccountRepository;

    public void init() {
        createNewAccountRepository = CreateNewAccountRepository.getInstance();
    }

    //create new account
    public LiveData<String> createNewAccount(Activity activity, JSONObject jsonObject) {
        return createNewAccountRepository.createNewAccountResponse(activity, jsonObject);
    }

    //get categories
    public LiveData<String> getCategoriesDetails(Activity activity, String type, String category_name) {
        return createNewAccountRepository.getCategoriesNames(activity, type, category_name);
    }

    //get penny OTP
    public LiveData<String> getOTPDetails(Activity activity, JSONObject jsonObject) {
        return createNewAccountRepository.getOTP(activity, jsonObject);
    }

    //get Bank details OTP
    public LiveData<String> getBankOTPDetails(Activity activity, JSONObject jsonObject) {
        return createNewAccountRepository.getBankOTP(activity, jsonObject);
    }

    //get Pan details OTP
    public LiveData<String> getPanOTPDetails(Activity activity) {
        return createNewAccountRepository.getPanOTP(activity);
    }

    //post updated bank details
    public LiveData<String> updateBankDetails(Activity activity, JSONObject jsonObject) {
        return createNewAccountRepository.getUpdateBank(activity, jsonObject);
    }

    //post updated pan details
    public LiveData<String> updatePanDetails(Activity activity, JSONObject jsonObject) {
        return createNewAccountRepository.postUpdatePan(activity, jsonObject);
    }

    //send penny amount
    public LiveData<String> postPennyVerification(Activity activity, JSONObject jsonObject) {
        return createNewAccountRepository.verifyPenny(activity, jsonObject);
    }

    //get setUp status
    public LiveData<String> setUpStatusModel(Activity activity, JSONObject jsonObject) {
        return createNewAccountRepository.getSetUpStatus(activity);
    }

    //get verify attempts
    public LiveData<String> getVerifyAttemptsModel(Activity activity, JSONObject jsonObject) {
        return createNewAccountRepository.getVerifyAttempts(activity);
    }

}
