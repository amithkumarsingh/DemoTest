package com.whitecoats.clinicplus.models;

public class ShowEditAndDeleteButtons {
    boolean canDeleteEditRecords;
    boolean canDeleteEditWrittenNotes;
    private int ID;
    private int encounter_Id;
    private int episode_id;
    private int patient_id;
    private int doctor_id;

    public int getEncounter_Id() {
        return encounter_Id;
    }

    public void setEncounter_Id(int encounter_Id) {
        this.encounter_Id = encounter_Id;
    }

    public int getEpisode_id() {
        return episode_id;
    }

    public void setEpisode_id(int episode_id) {
        this.episode_id = episode_id;
    }

    public int getPatient_id() {
        return patient_id;
    }

    public void setPatient_id(int patient_id) {
        this.patient_id = patient_id;
    }

    public int getDoctor_id() {
        return doctor_id;
    }

    public void setDoctor_id(int doctor_id) {
        this.doctor_id = doctor_id;
    }


    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public boolean isCanDeleteEditRecords() {
        return canDeleteEditRecords;
    }

    public void setCanDeleteEditRecords(boolean canDeleteEditRecords) {
        this.canDeleteEditRecords = canDeleteEditRecords;
    }

    public boolean isCanDeleteEditWrittenNotes() {
        return canDeleteEditWrittenNotes;
    }

    public void setCanDeleteEditWrittenNotes(boolean canDeleteEditWrittenNotes) {
        this.canDeleteEditWrittenNotes = canDeleteEditWrittenNotes;
    }
}
