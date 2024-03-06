package com.whitecoats.clinicplus.viewmodels;

import android.app.Activity;

import com.whitecoats.clinicplus.models.DashboardMenuModel;
import com.whitecoats.clinicplus.repositories.MainActivityRepository;

import java.util.ArrayList;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

public class MainActivityViewModel extends ViewModel {

    private MainActivityRepository mainActivityRepository;

    public void init(){
        mainActivityRepository = MainActivityRepository.getInstance();
    }

    public LiveData<ArrayList<DashboardMenuModel>> getDashMenu(Activity activity){
        return mainActivityRepository.getDashboardMenu(activity);
    }

    public LiveData<String> getTimePref(Activity activity){
        return mainActivityRepository.getTimePreferenceData(activity);
    }
}
