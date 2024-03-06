package com.whitecoats.clinicplus.viewmodels;

import android.app.Activity;
import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.whitecoats.clinicplus.repositories.SocialProfileRepository;
import com.whitecoats.clinicplus.utils.SingleLiveEvent;

import org.json.JSONObject;

public class UserSocialProfileViewModel extends ViewModel {
    private SocialProfileRepository socialProfileRepository;
    public void init(){
        socialProfileRepository = SocialProfileRepository.getInstance();
    }

    public LiveData<String> getGBPLinkData(Activity activity){
        return socialProfileRepository.getGBPLink(activity);
    }
    public LiveData<String> updateGBPLink(Activity activity, JSONObject gbpLinkDetails){
        return socialProfileRepository.saveGBPLink(activity,gbpLinkDetails);
    }
    public LiveData<String> updateGBPSharePreference(Activity activity, JSONObject gbpLinkSharePreferencesDetails){
        return socialProfileRepository.updateGBPSharePreferences(activity,gbpLinkSharePreferencesDetails);
    }

    public LiveData<String> applyToAllGbpLink(Activity activity, JSONObject applyToAllDetails){
        return socialProfileRepository.applyToAllGBPLink(activity,applyToAllDetails);
    }
    public LiveData<String> restGBPLinkData(Activity activity, JSONObject gbpResetData){
        return socialProfileRepository.restGBPlinkData(activity,gbpResetData);
    }

    public LiveData<String> getStaffPermissionsData(Activity context,JSONObject requestData){
        return socialProfileRepository.getStaffPermissionData(context,requestData);
    }

    public LiveData<String> setStaffPermission(Activity context,JSONObject requestData){
        return socialProfileRepository.setStaffPermissions(context,requestData);
    }
}
