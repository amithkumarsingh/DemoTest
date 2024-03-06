package com.whitecoats.model;

import org.json.JSONArray;

public class AssistantTabListModel {

    public final static String MSG_TYPE_SENT = "MSG_TYPE_SENT";

    public final static String MSG_TYPE_RECEIVED = "MSG_TYPE_RECEIVED";

    // Message content.
    private String msgContent;

    // Message type.
    private int msgType;

    //appointment data
    private String patientName, apptDate, apptTime;
    private int apptType, apptId, patientId;

    //records patient data
    private String patientPhNo;

    //cancel appt data
    private JSONArray apptIds;

    public AssistantTabListModel(int msgType, String msgContent) {
        this.msgType = msgType;
        this.msgContent = msgContent;
    }

    public String getMsgContent() {
        return msgContent;
    }

    public void setMsgContent(String msgContent) {
        this.msgContent = msgContent;
    }

    public int getMsgType() {
        return msgType;
    }

    public void setMsgType(int msgType) {
        this.msgType = msgType;
    }

    public int getPatientId() {
        return patientId;
    }

    public String getPatientName() {
        return patientName;
    }

    public String getApptDate() {
        return apptDate;
    }

    public String getApptTime() {
        return apptTime;
    }

    public int getApptId() {
        return apptId;
    }

    public int getApptType() {
        return apptType;
    }

    public String getPatientPhNo() {
        return patientPhNo;
    }

    public JSONArray getApptIds() {
        return apptIds;
    }

    public void setPatientId(int patientId) {
        this.patientId = patientId;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    public void setApptDate(String apptDate) {
        this.apptDate = apptDate;
    }

    public void setApptTime(String apptTime) {
        this.apptTime = apptTime;
    }

    public void setApptId(int apptId) {
        this.apptId = apptId;
    }

    public void setApptType(int apptType) {
        this.apptType = apptType;
    }

    public void setPatientPhNo(String patientPhNo) {
        this.patientPhNo = patientPhNo;
    }

    public void setApptIds(JSONArray apptIds) {
        this.apptIds = apptIds;
    }
}