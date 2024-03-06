package com.whitecoats.clinicplus.viewmodels;

import android.app.Activity;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.whitecoats.clinicplus.repositories.EMRSharedRecordRepository;

/**
 * Created by Mohammad suhail ahmed on 24-07-2020.
 */
public class EMRSharedRecordsViewModel extends ViewModel {
    private EMRSharedRecordRepository emrSharedRecordRepository;

    public void init(){
        emrSharedRecordRepository = EMRSharedRecordRepository.getInstance();
    }
    public MutableLiveData<String> getEMRSharedRecords(Activity activity, String duration, String groupby, int patientId){
        return emrSharedRecordRepository.getSharedRecords(activity,duration,groupby,patientId);
    }
    public MutableLiveData<String> getFileFromUrl(Activity activity, String url){
       return emrSharedRecordRepository.getFileFromUrl(activity,url);
    }
}
