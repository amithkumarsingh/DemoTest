package com.whitecoats.clinicplus.models;

public class DashBoardAutoFollowUpModel {

    String patientName, appointmentDate, clinicName, clinicAddress, followUpDate, submissionDate, patientMessage, reportUrl, apptNotes;
    int conditionStatus;
    int followInstructionStatus;
    int mode;
    String phoneNumber;
    String fileUrl;
    int patientId;

    public int getProductServiceId() {
        return productServiceId;
    }

    public void setProductServiceId(int productServiceId) {
        this.productServiceId = productServiceId;
    }

    int productServiceId;

    public int getPatientId() {
        return patientId;
    }

    public void setPatientId(int patientId) {
        this.patientId = patientId;
    }

    public int getGroupNo() {
        return groupNo;
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


    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }


    public int getFollowUpId() {
        return followUpId;
    }

    public void setFollowUpId(int followUpId) {
        this.followUpId = followUpId;
    }

    int followUpId;
    String patientPhone;

    public int getIsApptBooked() {
        return isApptBooked;
    }

    public void setIsApptBooked(int isApptBooked) {
        this.isApptBooked = isApptBooked;
    }

    int isApptBooked;

    public int getConditionStatus() {
        return conditionStatus;
    }

    public void setConditionStatus(int conditionStatus) {
        this.conditionStatus = conditionStatus;
    }

    public int getFollowInstructionStatus() {
        return followInstructionStatus;
    }

    public void setFollowInstructionStatus(int followInstructionStatus) {
        this.followInstructionStatus = followInstructionStatus;
    }

    public int getMode() {
        return mode;
    }

    public String getPatientPhone() {
        return patientPhone;
    }

    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    public String getAppointmentDate() {
        return appointmentDate;
    }

    public void setAppointmentDate(String appointmentDate) {
        this.appointmentDate = appointmentDate;
    }

    public String getClinicName() {
        return clinicName;
    }

    public void setClinicName(String clinicName) {
        this.clinicName = clinicName;
    }

    public String getClinicAddress() {
        return clinicAddress;
    }

    public void setClinicAddress(String clinicAddress) {
        this.clinicAddress = clinicAddress;
    }

    public String getFollowUpDate() {
        return followUpDate;
    }

    public void setFollowUpDate(String followUpDate) {
        this.followUpDate = followUpDate;
    }

    public String getSubmissionDate() {
        return submissionDate;
    }

    public void setSubmissionDate(String submissionDate) {
        this.submissionDate = submissionDate;
    }


    public String getPatientMessage() {
        return patientMessage;
    }

    public void setPatientMessage(String patientMessage) {
        this.patientMessage = patientMessage;
    }

    public String getReportUrl() {
        return reportUrl;
    }

    public void setReportUrl(String reportUrl) {
        this.reportUrl = reportUrl;
    }

    public String getApptNotes() {
        return apptNotes;
    }

    public void setApptNotes(String apptNotes) {
        this.apptNotes = apptNotes;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }

    public void setPatientPhone(String patientPhone) {
        this.patientPhone = patientPhone;
    }
}
