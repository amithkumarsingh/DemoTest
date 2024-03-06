package com.whitecoats.clinicplus.models;

import org.json.JSONArray;

public class DashBoardApptListModel {

    private String clinicName;
    private int apptCount;
    private int doneCount;
    private int pendingCount;
    private int cancelCount;

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public int getDoctorServiceId() {
        return doctorServiceId;
    }

    public void setDoctorServiceId(int doctorServiceId) {
        this.doctorServiceId = doctorServiceId;
    }

    private int productId;
    private int doctorServiceId;

    public JSONArray getAppointmentPendingId() {
        return appointmentPendingId;
    }

    public void setAppointmentPendingId(JSONArray appointmentPendingId) {
        this.appointmentPendingId = appointmentPendingId;
    }

    private JSONArray appointmentPendingId;

    public JSONArray getPatientApptArray() {
        return patientApptArray;
    }

    public void setPatientApptArray(JSONArray patientApptArray) {
        this.patientApptArray = patientApptArray;
    }

    private JSONArray patientApptArray;

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
