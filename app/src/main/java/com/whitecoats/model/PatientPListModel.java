package com.whitecoats.model;

import org.json.JSONArray;

import java.util.ArrayList;

public class PatientPListModel {

    public String patientName, phNo,emailid;
    private int patientId;
    private int roleId;
    private int isMoreOptionClicked;
    public String generalID;

    public ArrayList<String> getSharedCategoryData() {
        return sharedCategoryData;
    }

    public void setSharedCategoryData(ArrayList<String> sharedCategoryData) {
        this.sharedCategoryData = sharedCategoryData;
    }

    private ArrayList<String> sharedCategoryData;

    public int getGroupNo() {
        return groupNo;
    }

    public void setEmailid(String emailid) {
        this.emailid = emailid;
    }

    public String getEmailid() {
        return emailid;
    }

    public void setGroupNo(int groupNo) {
        this.groupNo = groupNo;
    }

    public String getApptDate() {
        return apptDate;
    }

    public void setApptDate(String apptDate) {
        this.apptDate = apptDate;
    }

    private int groupNo;
    private String apptDate;

    public String getRecordSharedCount() {
        return recordSharedCount;
    }

    public void setRecordSharedCount(String recordSharedCount) {
        this.recordSharedCount = recordSharedCount;
    }

    private String recordSharedCount;


    public int getPatientGender() {
        return patientGender;
    }

    public void setPatientGender(int patientGender) {
        this.patientGender = patientGender;
    }

    public JSONArray getAssignCategory() {
        return assignCategory;
    }

    public void setAssignCategory(JSONArray assignCategory) {
        this.assignCategory = assignCategory;
    }

    private int patientGender;
    private JSONArray assignCategory;

    public void setPatientAge(String patientAge) {
        this.patientAge = patientAge;
    }

    public String getPatientAge() {
        return patientAge;
    }

    private  String patientAge;

    public int getRoleId() {
        return roleId;
    }

    public void setRoleId(int roleId) {
        this.roleId = roleId;
    }

    private ArrayList<PatientPListModel> patientCategory;

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    public String getPatientName() {
        return patientName;
    }

    public int getPatientId() {
        return patientId;
    }

    public String getPhNo() {
        return phNo;
    }


    public void setPatientId(int patientId) {
        this.patientId = patientId;
    }

    public void setPhNo(String phNo) {
        this.phNo = phNo;
    }

    public int getIsMoreOptionClicked() {
        return isMoreOptionClicked;
    }

    public void setIsMoreOptionClicked(int isMoreOptionClicked) {
        this.isMoreOptionClicked = isMoreOptionClicked;
    }

    public String getGeneralID() {
        return generalID;
    }

    public void setGeneralID(String generalID) {
        this.generalID = generalID;
    }
}
