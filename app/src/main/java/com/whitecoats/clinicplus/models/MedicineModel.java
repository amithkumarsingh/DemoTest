package com.whitecoats.clinicplus.models;

public class MedicineModel {
    private int medicineId;
    private String medicineName,medicineCompany;
    public MedicineModel(int medicineId, String medicineName, String medicineCompany){
        this.medicineId = medicineId;
        this.medicineName = medicineName;
        this.medicineCompany = medicineCompany;
    }

    public int getMedicineId() {
        return medicineId;
    }

    public String getMedicineCompany() {
        return medicineCompany;
    }

    public String getMedicineName() {
        return medicineName;
    }
}
