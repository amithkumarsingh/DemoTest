package com.whitecoats.clinicplus.models;

import org.json.JSONObject;

/**
 * Created by Mohammad suhail ahmed on 24-07-2020.
 */
public class EMRSharedRecordModel {
    private String headerDate;
    private String dateTime;
    private int categoryId;
    private String category;
    private JSONObject record;
    private int recordId;

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    private String createdAt;

    public EMRSharedRecordModel(String headerDate, String dateTime, int categoryId, String category, JSONObject record, int recordId, String createdAt) {
        this.headerDate = headerDate;
        this.dateTime = dateTime;
        this.categoryId = categoryId;
        this.category = category;
        this.record = record;
        this.recordId = recordId;
        this.createdAt = createdAt;
    }

    public int getRecordId() {
        return recordId;
    }

    public String getHeaderDate() {
        return headerDate;
    }

    public String getCategory() {
        return category;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public JSONObject getRecord() {
        return record;
    }

    public String getDateTime() {
        return dateTime;
    }
}
