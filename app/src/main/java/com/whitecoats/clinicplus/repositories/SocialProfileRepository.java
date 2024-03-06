package com.whitecoats.clinicplus.repositories;

import android.app.Activity;
import android.app.ProgressDialog;

import androidx.lifecycle.MutableLiveData;

import com.whitecoats.clinicplus.R;
import com.whitecoats.clinicplus.apis.ApiMethodCalls;
import com.whitecoats.clinicplus.apis.ApiUrls;
import com.whitecoats.clinicplus.interfaces.ApiCallbackInterface;
import com.whitecoats.clinicplus.utils.SingleLiveEvent;

import org.json.JSONObject;

public class SocialProfileRepository {
    private static SocialProfileRepository socialProfileRepository;
    private ApiMethodCalls apiMethodCalls = new ApiMethodCalls();
    private ProgressDialog loadingDialogGetLink,loadingDialogSaveGBPLink,loadingDialogUpdatePreferences,loadingDialogApplyToAll;
    public static SocialProfileRepository getInstance() {
        if(socialProfileRepository == null) {
            socialProfileRepository = new SocialProfileRepository();
        }
        return socialProfileRepository;
    }

    public MutableLiveData<String> getGBPLink(Activity activity){
        loadingDialogGetLink = new ProgressDialog(activity);
        loadingDialogGetLink.setMessage(activity.getResources().getString(R.string.please_wait));
        loadingDialogGetLink.setTitle(activity.getResources().getString(R.string.fetching));
        loadingDialogGetLink.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        loadingDialogGetLink.setCancelable(false);
        loadingDialogGetLink.show();
        MutableLiveData<String> getGbpLinkResponse = new MutableLiveData<>();
        try {
            apiMethodCalls.getApiData(ApiUrls.getGBPLink, null, activity, new ApiCallbackInterface() {
                @Override
                public void onSuccessResponse(String response) {
                    loadingDialogGetLink.dismiss();
                    getGbpLinkResponse.postValue(response);
                }

                @Override
                public void onErrorResponse(String error) {
                    loadingDialogGetLink.dismiss();
                    getGbpLinkResponse.postValue(error);
                }
            });
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return getGbpLinkResponse;
    }

    public MutableLiveData<String> saveGBPLink(Activity activity, JSONObject gbpLinkDetails){
        loadingDialogSaveGBPLink = new ProgressDialog(activity);
        loadingDialogSaveGBPLink.setMessage(activity.getResources().getString(R.string.please_wait));
        loadingDialogSaveGBPLink.setTitle(activity.getResources().getString(R.string.updating));
        loadingDialogSaveGBPLink.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        loadingDialogSaveGBPLink.setCancelable(false);
        loadingDialogSaveGBPLink.show();
        MutableLiveData<String> saveGBPLinkResponse = new MutableLiveData<>();
        try {
            apiMethodCalls.postApiData(ApiUrls.SaveGBPLink, gbpLinkDetails, activity, new ApiCallbackInterface() {
                @Override
                public void onSuccessResponse(String response) {
                    loadingDialogSaveGBPLink.dismiss();
                    saveGBPLinkResponse.postValue(response);
                }

                @Override
                public void onErrorResponse(String error) {
                    loadingDialogSaveGBPLink.dismiss();
                    saveGBPLinkResponse.postValue(error);
                }
            });
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return saveGBPLinkResponse;
    }
    public MutableLiveData<String> updateGBPSharePreferences(Activity activity, JSONObject gbpLinkSharePreferencesDetails){
        loadingDialogUpdatePreferences = new ProgressDialog(activity);
        loadingDialogUpdatePreferences.setMessage("Saving Preference");
//        loadingDialogUpdatePreferences.setTitle(activity.getResources().getString(R.string.updating));
        loadingDialogUpdatePreferences.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        loadingDialogUpdatePreferences.setCancelable(false);
        loadingDialogUpdatePreferences.show();
        MutableLiveData<String> updateGBPSharePreferencesResponse = new MutableLiveData<>();
        try {
            apiMethodCalls.postApiData(ApiUrls.updateGBPSharePreferences, gbpLinkSharePreferencesDetails, activity, new ApiCallbackInterface() {
                @Override
                public void onSuccessResponse(String response) {
                    loadingDialogUpdatePreferences.dismiss();
                    updateGBPSharePreferencesResponse.postValue(response);
                }

                @Override
                public void onErrorResponse(String error) {
                    loadingDialogUpdatePreferences.dismiss();
                    updateGBPSharePreferencesResponse.postValue(error);
                }
            });
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return updateGBPSharePreferencesResponse;
    }

    public MutableLiveData<String> applyToAllGBPLink(Activity activity, JSONObject applyToAllDetails){
//        loadingDialogApplyToAll = new ProgressDialog(activity);
//        loadingDialogApplyToAll.setMessage(activity.getResources().getString(R.string.please_wait));
//        loadingDialogApplyToAll.setTitle(activity.getResources().getString(R.string.updating));
//        loadingDialogApplyToAll.setProgressStyle(ProgressDialog.STYLE_SPINNER);
//        loadingDialogApplyToAll.setCancelable(false);
//        loadingDialogApplyToAll.show();
        MutableLiveData<String> applyToAllGBPLinkResponse = new MutableLiveData<>();
        try {
            apiMethodCalls.postApiData(ApiUrls.applyToAllGBPLink, applyToAllDetails, activity, new ApiCallbackInterface() {
                @Override
                public void onSuccessResponse(String response) {
//                    loadingDialogApplyToAll.dismiss();
                    applyToAllGBPLinkResponse.postValue(response);
                }

                @Override
                public void onErrorResponse(String error) {
//                    loadingDialogApplyToAll.dismiss();
                    applyToAllGBPLinkResponse.postValue(error);
                }
            });
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return applyToAllGBPLinkResponse;
    }

    public MutableLiveData<String> restGBPlinkData(Activity activity, JSONObject gbpResetData){
//        loadingDialogApplyToAll = new ProgressDialog(activity);
//        loadingDialogApplyToAll.setMessage(activity.getResources().getString(R.string.please_wait));
//        loadingDialogApplyToAll.setTitle(activity.getResources().getString(R.string.updating));
//        loadingDialogApplyToAll.setProgressStyle(ProgressDialog.STYLE_SPINNER);
//        loadingDialogApplyToAll.setCancelable(false);
//        loadingDialogApplyToAll.show();
        MutableLiveData<String> resetGBPLinkResponse = new MutableLiveData<>();
        try {
            apiMethodCalls.postApiData(ApiUrls.resetGBPLink, gbpResetData, activity, new ApiCallbackInterface() {
                @Override
                public void onSuccessResponse(String response) {
//                    loadingDialogApplyToAll.dismiss();
                    resetGBPLinkResponse.postValue(response);
                }

                @Override
                public void onErrorResponse(String error) {
//                    loadingDialogApplyToAll.dismiss();
                    resetGBPLinkResponse.postValue(error);
                }
            });
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return resetGBPLinkResponse;
    }

    public MutableLiveData<String> getStaffPermissionData(Activity activity, JSONObject requestData){
        SingleLiveEvent<String> staffPermissionResponse = new SingleLiveEvent<>();
        try {
            apiMethodCalls.getApiData(ApiUrls.getStaffPermissionAPI, requestData, activity, new ApiCallbackInterface() {
                @Override
                public void onSuccessResponse(String response) {
                    staffPermissionResponse.postValue(response);
                }

                @Override
                public void onErrorResponse(String error) {
                    staffPermissionResponse.postValue(error);
                }
            });
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return staffPermissionResponse;
    }

    public MutableLiveData<String> setStaffPermissions(Activity activity, JSONObject requestData){
        loadingDialogSaveGBPLink = new ProgressDialog(activity);
        loadingDialogSaveGBPLink.setMessage(activity.getResources().getString(R.string.please_wait));
        loadingDialogSaveGBPLink.setTitle(activity.getResources().getString(R.string.updating));
        loadingDialogSaveGBPLink.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        loadingDialogSaveGBPLink.setCancelable(false);
        loadingDialogSaveGBPLink.show();
        SingleLiveEvent<String> setPermissionsResponse = new SingleLiveEvent<>();
        try {
            apiMethodCalls.postApiData(ApiUrls.setStaffPermissionAPI, requestData, activity, new ApiCallbackInterface() {
                @Override
                public void onSuccessResponse(String response) {
                    loadingDialogSaveGBPLink.dismiss();
                    setPermissionsResponse.postValue(response);
                }

                @Override
                public void onErrorResponse(String error) {
                    loadingDialogSaveGBPLink.dismiss();
                    setPermissionsResponse.postValue(error);
                }
            });
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return setPermissionsResponse;
    }
}
