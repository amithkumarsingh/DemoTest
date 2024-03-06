package com.whitecoats.model;

public class RecordsModel {

    //written notes
    private String fileUrl, presDate, desp, medPresp, testPresp;

    public String getDesp() {
        return desp;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public String getMedPresp() {
        return medPresp;
    }

    public String getPresDate() {
        return presDate;
    }

    public String getTestPresp() {
        return testPresp;
    }

    public void setDesp(String desp) {
        this.desp = desp;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public void setMedPresp(String medPresp) {
        this.medPresp = medPresp;
    }

    public void setPresDate(String presDate) {
        this.presDate = presDate;
    }

    public void setTestPresp(String testPresp) {
        this.testPresp = testPresp;
    }
}
