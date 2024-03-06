package com.whitecoats.clinicplus.models;

/**
 * Created by Mohammad suhail ahmed on 01-09-2020.
 */
public class EMRPrescriptionModel {

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    private String categoryId;
    private String url;
    private String createdAt;

    public String getCreatedAt() {
        return createdAt;
    }

    public String getUrl() {
        return url;
    }

    //constructor initializing values
    public EMRPrescriptionModel(String categoryId,String url,String createdAt) {
        this.categoryId = categoryId;
        this.url = url;
        this.createdAt= createdAt;
    }
}
