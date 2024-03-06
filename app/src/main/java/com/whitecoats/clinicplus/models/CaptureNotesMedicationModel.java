package com.whitecoats.clinicplus.models;

public class CaptureNotesMedicationModel {
    public String getMedicationName() {
        return medicationName;
    }

    public void setMedicationName(String medicationName) {
        this.medicationName = medicationName;
    }

    public String getMedicationCompanyName() {
        return medicationCompanyName;
    }

    public void setMedicationCompanyName(String medicationCompanyName) {
        this.medicationCompanyName = medicationCompanyName;
    }



    public String getMedicationDuration() {
        return medicationDuration;
    }

    public void setMedicationDuration(String medicationDuration) {
        this.medicationDuration = medicationDuration;
    }

    String medicationName;
    String medicationCompanyName;

    public int getMedicationDays() {
        return medicationDays;
    }

    public void setMedicationDays(int medicationDays) {
        this.medicationDays = medicationDays;
    }

    int medicationDays;
    int recordId;
    String status;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getRecordId() {
        return recordId;
    }

    public void setRecordId(int recordId) {
        this.recordId = recordId;
    }

    String medicationDuration;

}