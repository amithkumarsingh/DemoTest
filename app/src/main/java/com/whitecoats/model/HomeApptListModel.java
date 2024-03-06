package com.whitecoats.model;

import org.json.JSONArray;

public class HomeApptListModel {

    private String clinicName;
    private int apptCount;
    private int doneCount;
    private int pendingCount;
    private int cancelCount;

    public JSONArray getAppointmentPendingId() {
        return appointmentPendingId;
    }

    public void setAppointmentPendingId(JSONArray appointmentPendingId) {
        this.appointmentPendingId = appointmentPendingId;
    }

    private JSONArray appointmentPendingId;

    public int getServiceId() {
        return serviceId;
    }

    public void setServiceId(int serviceId) {
        this.serviceId = serviceId;
    }

    private int serviceId;

    public int getApptCount() {
        return apptCount;
    }

    public int getCancelCount() {
        return cancelCount;
    }

    public int getDoneCount() {
        return doneCount;
    }

    public int getPendingCount() {
        return pendingCount;
    }

    public String getClinicName() {
        return clinicName;
    }

    public void setApptCount(int apptCount) {
        this.apptCount = apptCount;
    }

    public void setCancelCount(int cancelCount) {
        this.cancelCount = cancelCount;
    }

    public void setClinicName(String clinicName) {
        this.clinicName = clinicName;
    }

    public void setDoneCount(int doneCount) {
        this.doneCount = doneCount;
    }

    public void setPendingCount(int pendingCount) {
        this.pendingCount = pendingCount;
    }
}
