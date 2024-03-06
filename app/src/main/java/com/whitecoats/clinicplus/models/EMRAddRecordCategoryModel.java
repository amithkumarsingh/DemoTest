package com.whitecoats.clinicplus.models;

public class EMRAddRecordCategoryModel {
    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    private String categoryName;

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    private String categoryId;
    private String url;

    public String getUrl() {
        return url;
    }

    //constructor initializing values
    public EMRAddRecordCategoryModel(String categoryId,String categoryName) {
        this.categoryName = categoryName;
        this.categoryId = categoryId;
    }
}
