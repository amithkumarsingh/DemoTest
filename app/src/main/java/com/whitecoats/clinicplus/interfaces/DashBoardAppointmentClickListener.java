package com.whitecoats.clinicplus.interfaces;

import android.view.View;

import com.whitecoats.model.AppointmentApptListModel;

import org.json.JSONArray;

public interface DashBoardAppointmentClickListener {
    public void onItemClick(View v, int position, JSONArray pendingIdArray, String ClickAction, String ClinicName, int product_id, int serviceId, int isAppointmentNextBackVideoClick,int isAppointmentNextBackChatClick,int isAppointmentNextBackClinicClick,int appointmentServiceId);
}