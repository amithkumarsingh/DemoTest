package com.whitecoats.clinicplus.models;

import org.json.JSONArray;

public class EMRConsultCaseHistoryModel {


    int categoryId;
    int recordId;
    String categoryRecordData;
    String fieldDictionary;
    String categoryName;

    public String getCategoryType() {
        return categoryType;
    }

    public void setCategoryType(String categoryType) {
        this.categoryType = categoryType;
    }

    String categoryType;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPostedOn() {
        return PostedOn;
    }

    public void setPostedOn(String postedOn) {
        PostedOn = postedOn;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }

    String name;
    String PostedOn;
    String Status;

    public int getEncounterID() {
        return encounterID;
    }

    public void setEncounterID(int encounterID) {
        this.encounterID = encounterID;
    }

    int encounterID;

    public int getMultiRecordPosition() {
        return multiRecordPosition;
    }

    public void setMultiRecordPosition(int multiRecordPosition) {
        this.multiRecordPosition = multiRecordPosition;
    }

    int multiRecordPosition;


    public String getEncounterName() {
        return encounterName;
    }

    public void setEncounterName(String encounterName) {
        this.encounterName = encounterName;
    }

    public String getEncounterDateTime() {
        return encounterDateTime;
    }

    public void setEncounterDateTime(String encounterDateTime) {
        this.encounterDateTime = encounterDateTime;
    }

    String encounterName;
    String encounterDateTime;

    public int getIsRecordData() {
        return isRecordData;
    }

    public void setIsRecordData(int isRecordData) {
        this.isRecordData = isRecordData;
    }

    int isRecordData;

    public int getEnableSeparatorLine() {
        return enableSeparatorLine;
    }

    public void setEnableSeparatorLine(int enableSeparatorLine) {
        this.enableSeparatorLine = enableSeparatorLine;
    }

    int enableSeparatorLine;

    public int getGroupNo() {
        return groupNo;
    }

    public void setGroupNo(int groupNo) {
        this.groupNo = groupNo;
    }

    private int groupNo;


    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    String createdAt;

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public int getRecordId() {
        return recordId;
    }

    public void setRecordId(int recordId) {
        this.recordId = recordId;
    }

    public String getCategoryRecordData() {
        return categoryRecordData;
    }

    public void setCategoryRecordData(String categoryRecordData) {
        this.categoryRecordData = categoryRecordData;
    }

    public String getFieldDictionary() {
        return fieldDictionary;
    }

    public void setFieldDictionary(String fieldDictionary) {
        this.fieldDictionary = fieldDictionary;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }


}

