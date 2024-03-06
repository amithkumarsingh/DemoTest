package com.whitecoats.model;

public class AppointmentApptListModel {

    private String patientName, apptTime;
    private int groupNo;
    private String apptDate;
    private String clinicName;
    private String clinicAddress;
    private String apptCatAssign;
    private String paymentStatus;
    private int paymentType;
    private int netAmount, orderAmount, discount;
    private int orderId, patientId, apptMode;
    private int apptAttendanceStatus;
    private int activePast;
    private int apptStatus;

    public int getSendPaymentNotification() {
        return sendPaymentNotification;
    }

    public void setSendPaymentNotification(int sendPaymentNotification) {
        this.sendPaymentNotification = sendPaymentNotification;
    }

    private int sendPaymentNotification;

    public String getReceiptUrl() {
        return receiptUrl;
    }

    public void setReceiptUrl(String receiptUrl) {
        this.receiptUrl = receiptUrl;
    }

    private String receiptUrl;

    private int categoryId;

    public int getDoctorCategoryId() {
        return doctorCategoryId;
    }

    public void setDoctorCategoryId(int doctorCategoryId) {
        this.doctorCategoryId = doctorCategoryId;
    }

    public String getDoctorCategoryName() {
        return doctorCategoryName;
    }

    public void setDoctorCategoryName(String doctorCategoryName) {
        this.doctorCategoryName = doctorCategoryName;
    }

    private int doctorCategoryId;
    private String doctorCategoryName;

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    private String categoryName;

    public int getOrderUserId() {
        return orderUserId;
    }

    public void setOrderUserId(int orderUserId) {
        this.orderUserId = orderUserId;
    }

    private int orderUserId;

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public int getPaymentType() {
        return paymentType;
    }

    public int getNetAmount() {
        return netAmount;
    }

    public void setNetAmount(int netAmount) {
        this.netAmount = netAmount;
    }

    public int getOrderAmount() {
        return orderAmount;
    }

    public void setOrderAmount(int orderAmount) {
        this.orderAmount = orderAmount;
    }

    public int getDiscount() {
        return discount;
    }

    public void setDiscount(int discount) {
        this.discount = discount;
    }

    public void setPaymentType(int paymentType) {
        this.paymentType = paymentType;
    }


    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }


    public int getAppointmentId() {
        return appointmentId;
    }

    public void setAppointmentId(int appointmentId) {
        this.appointmentId = appointmentId;
    }

    private int appointmentId;

    public String getApptCatAssign() {
        return apptCatAssign;
    }

    public void setApptCatAssign(String apptCatAssign) {
        this.apptCatAssign = apptCatAssign;
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

    public String getApptTime() {
        return apptTime;
    }

    public String getPatientName() {
        return patientName;
    }

    public int getGroupNo() {
        return groupNo;
    }

    public String getApptDate() {
        return apptDate;
    }

    public void setApptTime(String apptTime) {
        this.apptTime = apptTime;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    public void setGroupNo(int groupNo) {
        this.groupNo = groupNo;
    }

    public void setApptDate(String apptDate) {
        this.apptDate = apptDate;
    }

    public int getPatientId() {
        return patientId;
    }

    public void setPatientId(int patientId) {
        this.patientId = patientId;
    }

    public int getApptMode() {
        return apptMode;
    }

    public void setApptMode(int apptMode) {
        this.apptMode = apptMode;
    }

    public int getApptAttendanceStatus() {
        return apptAttendanceStatus;
    }

    public void setApptAttendanceStatus(int apptAttendanceStatus) {
        this.apptAttendanceStatus = apptAttendanceStatus;
    }

    public int getActivePast() {
        return activePast;
    }

    public void setActivePast(int activePast) {
        this.activePast = activePast;
    }

    public int getApptStatus() {
        return apptStatus;
    }

    public void setApptStatus(int apptStatus) {
        this.apptStatus = apptStatus;
    }

    @Override
    public String toString() {
        return doctorCategoryName;
    }


}
