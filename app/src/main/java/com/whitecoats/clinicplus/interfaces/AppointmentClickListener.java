package com.whitecoats.clinicplus.interfaces;

import android.view.View;

import com.whitecoats.clinicplus.models.AppointmentModel;


public interface AppointmentClickListener {
    public void onItemClick(View v, String loadMore, AppointmentModel appointmentModel, int appointmentPosition);
}
