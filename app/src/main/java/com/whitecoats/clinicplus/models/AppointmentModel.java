package com.whitecoats.clinicplus.models;

import org.json.JSONArray;
import org.json.JSONObject;

public class AppointmentModel {

    private String patientName, apptTime,scheduleTime,generalID;
    private int groupNo;
    private String apptDate;
    private String clinicName;
    private String paymentStatus;
    private int apptType;
    private int orderId, patientId, apptMode,productId;
    private int activePast;
    private int apptStatus;
    private int categoryId;
    private int refundStatus;
    private int isRefundProcessed;
    private int netTotalPaid;
    private int refundAmt;
    private int isSettlementProcessed;
    private int isDoAutoRefund;
    private int isSettlementTriggered;

    private int is_reschedule_active;

    private int sendPaymentNotification;

    public String getGeneralID() {
        return generalID;
    }

    public void setGeneralID(String generalID) {
        this.generalID = generalID;
    }

    public String getPayment_title() {
        return payment_title;
    }

    public void setPayment_title(String payment_title) {
        this.payment_title = payment_title;
    }

    public String getPayment_title_color() {
        return payment_title_color;
    }

    public void setPayment_title_color(String payment_title_color) {
        this.payment_title_color = payment_title_color;
    }

    private String payment_title;
    private String payment_title_color;

    public String getUnmapped_status() {
        return unmapped_status;
    }

    public void setUnmapped_status(String unmapped_status) {
        this.unmapped_status = unmapped_status;
    }

    private String unmapped_status;

    private String invoiceUrl;

    private String patientPhoneNumber;

    private JSONArray rescheduleJsonArray;
    private String booked_by;


    public int getInternalSaas_id() {
        return internalSaas_id;
    }

    public void setInternalSaas_id(int internalSaas_id) {
        this.internalSaas_id = internalSaas_id;
    }

    private int internalSaas_id;

    public String getRefundScheduledOn() {
        return refundScheduledOn;
    }

    public void setRefundScheduledOn(String refundScheduledOn) {
        this.refundScheduledOn = refundScheduledOn;
    }

    private String refundScheduledOn;

    public String getReceiptUrl() {
        return receiptUrl;
    }

    public void setReceiptUrl(String receiptUrl) {
        this.receiptUrl = receiptUrl;
    }

    private String receiptUrl;

    public int getIsSettlementTriggered() {
        return isSettlementTriggered;
    }

    public void setIsSettlementTriggered(int isSettlementTriggered) {
        this.isSettlementTriggered = isSettlementTriggered;
    }


    public JSONObject getInvoiceData() {
        return invoiceData;
    }

    public void setInvoiceData(JSONObject invoiceData) {
        this.invoiceData = invoiceData;
    }

    private JSONObject invoiceData;



    private int doctorCategoryId;
    private String doctorCategoryName;

    private String categoryName;

    private int orderUserId;
    private int appointmentId;

    public void setDoctorCategoryId(int doctorCategoryId) {
        this.doctorCategoryId = doctorCategoryId;
    }

    public void setDoctorCategoryName(String doctorCategoryName) {
        this.doctorCategoryName = doctorCategoryName;
    }
    public int getVideo_tool() {
        return video_tool;
    }

    public void setVideo_tool(int video_tool) {
        this.video_tool = video_tool;
    }

    public String getDoctor_join_external_url() {
        return doctor_join_external_url;
    }

    public void setDoctor_join_external_url(String doctor_join_external_url) {
        this.doctor_join_external_url = doctor_join_external_url;
    }

    private int video_tool;

    private String doctor_join_external_url;

    private String patient_join_external_url;

    public String getScheduleTime() {
        return scheduleTime;
    }

    public void setScheduleTime(String scheduleTime) {
        this.scheduleTime = scheduleTime;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public int getDoctorCategoryId() {
        return doctorCategoryId;
    }


    public String getDoctorCategoryName() {
        return doctorCategoryName;
    }


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



    public int getOrderUserId() {
        return orderUserId;
    }

    public void setOrderUserId(int orderUserId) {
        this.orderUserId = orderUserId;
    }


    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }



    public int getApptType() {
        return apptType;
    }

    public void setApptType(int apptType) {
        this.apptType = apptType;
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





    public String getClinicName() {
        return clinicName;
    }

    public void setClinicName(String clinicName) {
        this.clinicName = clinicName;
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


    public int getRefundStatus() {
        return refundStatus;
    }

    public void setRefundStatus(int refundStatus) {
        this.refundStatus = refundStatus;
    }

    public int getIsRefundProcessed() {
        return isRefundProcessed;
    }

    public void setIsRefundProcessed(int isRefundProcessed) {
        this.isRefundProcessed = isRefundProcessed;
    }

    public int getNetTotalPaid() {
        return netTotalPaid;
    }

    public void setNetTotalPaid(int netTotalPaid) {
        this.netTotalPaid = netTotalPaid;
    }

    public int getRefundAmt() {
        return refundAmt;
    }

    public void setRefundAmt(int refundAmt) {
        this.refundAmt = refundAmt;
    }

    public int getIsSettlementProcessed() {
        return isSettlementProcessed;
    }

    public void setIsSettlementProcessed(int isSettlementProcessed) {
        this.isSettlementProcessed = isSettlementProcessed;
    }

    public int getIsDoAutoRefund() {
        return isDoAutoRefund;
    }

    public void setIsDoAutoRefund(int isDoAutoRefund) {
        this.isDoAutoRefund = isDoAutoRefund;
    }


    public String getPatientPhoneNumber() {
        return patientPhoneNumber;
    }

    public void setPatientPhoneNumber(String patientPhoneNumber) {
        this.patientPhoneNumber = patientPhoneNumber;
    }

    public JSONArray getRescheduleJsonArray() {
        return rescheduleJsonArray;
    }

    public void setRescheduleJsonArray(JSONArray rescheduleJsonArray) {
        this.rescheduleJsonArray = rescheduleJsonArray;
    }

    public String getInvoiceUrl() {
        return invoiceUrl;
    }

    public void setInvoiceUrl(String invoiceUrl) {
        this.invoiceUrl = invoiceUrl;
    }

    public String getBooked_by() {
        return booked_by;
    }

    public void setBooked_by(String booked_by) {
        this.booked_by = booked_by;
    }

    public int getIs_reschedule_active() {
        return is_reschedule_active;
    }

    public void setIs_reschedule_active(int is_reschedule_active) {
        this.is_reschedule_active = is_reschedule_active;
    }

    public int getSendPaymentNotification() {
        return sendPaymentNotification;
    }

    public void setSendPaymentNotification(int sendPaymentNotification) {
        this.sendPaymentNotification = sendPaymentNotification;
    }

    public String getPatient_join_external_url() {
        return patient_join_external_url;
    }

    public void setPatient_join_external_url(String patient_join_external_url) {
        this.patient_join_external_url = patient_join_external_url;
    }
}
