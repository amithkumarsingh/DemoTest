package com.whitecoats.clinicplus.viewmodels;

import android.app.Activity;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.whitecoats.clinicplus.repositories.SearchPatientRepository;

/**
 * Created by Mohammad suhail ahmed on 09-07-2020.
 */
public class SearchPatientViewModel extends ViewModel {
    private SearchPatientRepository searchPatientRepository;
    public void init(){
        searchPatientRepository = SearchPatientRepository.getInstance();
    }
    public LiveData<String> searchPatient(Activity activity,String searchText){
        return searchPatientRepository.searchPatient(activity,searchText);
    }
}
