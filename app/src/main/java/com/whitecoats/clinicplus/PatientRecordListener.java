package com.whitecoats.clinicplus;

import android.view.View;

public interface PatientRecordListener {
    public void onItemClick(View v, String parameter, String type,String catRecordIdArray);
}
