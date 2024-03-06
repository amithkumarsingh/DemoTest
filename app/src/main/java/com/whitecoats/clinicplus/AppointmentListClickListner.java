package com.whitecoats.clinicplus;

import android.view.View;

import com.whitecoats.model.AppointmentApptListModel;

public interface AppointmentListClickListner {
    public void onItemClick(View v, String loadMore, AppointmentApptListModel appointmentApptListModel);
}