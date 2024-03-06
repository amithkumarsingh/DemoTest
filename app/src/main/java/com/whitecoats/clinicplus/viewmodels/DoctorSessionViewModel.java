package com.whitecoats.clinicplus.viewmodels;

import android.app.Activity;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.whitecoats.clinicplus.repositories.DoctorSessionRepository;

import org.json.JSONObject;

public class DoctorSessionViewModel extends ViewModel {
    private DoctorSessionRepository doctorSessionRepository;

    public void init() {
        doctorSessionRepository = DoctorSessionRepository.getInstance();
    }

    public LiveData<String> createDoctorSessionId(Activity activity, JSONObject jsonObject) {
        return doctorSessionRepository.getDoctorSessionId(activity, jsonObject);
    }


}
