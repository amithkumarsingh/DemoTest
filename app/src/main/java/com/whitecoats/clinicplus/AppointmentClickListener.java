package com.whitecoats.clinicplus;

import android.view.View;

import org.json.JSONArray;

public interface AppointmentClickListener {
    public void onItemClick(View v, int position, JSONArray pendingIdArray,String cancel,String late);
}