package com.whitecoats.clinicplus.models;

public class SwitchClinicModel {
    public String getClinicName() {
        return clinicName;
    }

    public void setClinicName(String clinicName) {
        this.clinicName = clinicName;
    }
    public String clinicName;


    public int getApptCount() {
        return apptCount;
    }

    public void setApptCount(int apptCount) {
        this.apptCount = apptCount;
    }

    public int apptCount;
}
