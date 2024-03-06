package com.whitecoats.clinicplus.models;

import org.json.JSONArray;

public class EMRAllEpisodeModel {

    String createdOn;
    int numberOfInteraction;
    String lastInteractionOn;
    JSONArray diagnosis;
    int status;
    String episodeName;
    String encounterMode;
    int episodeId;
    int patientId;
    int doctorId;
    int appointmentId;

    public String getEncounterDropDownString() {
        return encounterDropDownString;
    }

    public void setEncounterDropDownString(String encounterDropDownString) {
        this.encounterDropDownString = encounterDropDownString;
    }

    String encounterDropDownString;

    public int getLastInteractionId() {
        return lastInteractionId;
    }

    public void setLastInteractionId(int lastInteractionId) {
        this.lastInteractionId = lastInteractionId;
    }

    int lastInteractionId;

    public String getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(String createdOn) {
        this.createdOn = createdOn;
    }

    public int getNumberOfInteraction() {
        return numberOfInteraction;
    }

    public void setNumberOfInteraction(int numberOfInteraction) {
        this.numberOfInteraction = numberOfInteraction;
    }

    public String getLastInteractionOn() {
        return lastInteractionOn;
    }

    public void setLastInteractionOn(String lastInteractionOn) {
        this.lastInteractionOn = lastInteractionOn;
    }

    public JSONArray getDiagnosis() {
        return diagnosis;
    }

    public void setDiagnosis(JSONArray diagnosis) {
        this.diagnosis = diagnosis;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getEpisodeName() {
        return episodeName;
    }

    public void setEpisodeName(String episodeName) {
        this.episodeName = episodeName;
    }

    public String getEncounterMode() {
        return encounterMode;
    }

    public void setEncounterMode(String encounterMode) {
        this.encounterMode = encounterMode;
    }

    public int getEpisodeId() {
        return episodeId;
    }

    public void setEpisodeId(int episodeId) {
        this.episodeId = episodeId;
    }

    public int getPatientId() {
        return patientId;
    }

    public void setPatientId(int patientId) {
        this.patientId = patientId;
    }

    public int getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(int doctorId) {
        this.doctorId = doctorId;
    }

    public int getAppointmentId() {
        return appointmentId;
    }

    public void setAppointmentId(int appointmentId) {
        this.appointmentId = appointmentId;
    }

    @Override
    public String toString() {
        return encounterDropDownString;
    }

}

