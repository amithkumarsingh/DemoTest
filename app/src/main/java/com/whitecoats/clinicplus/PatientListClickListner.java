package com.whitecoats.clinicplus;

import android.view.View;

import org.json.JSONObject;

public interface PatientListClickListner {
    public void onItemClick(View v, String loadMore, String phoneNumber, int videoServiceId, int clinicServiceId, int instantVideoServiceId, int chatServiceId, int patientId, String patientName, JSONObject instantVideoObject,JSONObject instantVideoInfoObject);
}