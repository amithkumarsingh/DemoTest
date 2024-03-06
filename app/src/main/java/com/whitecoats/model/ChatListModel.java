package com.whitecoats.model;

public class ChatListModel {

    String patientName, chatDateTime, readCount;
    int chatId;

    public int getSendPaymentReminder() {
        return sendPaymentReminder;
    }

    public void setSendPaymentReminder(int sendPaymentReminder) {
        this.sendPaymentReminder = sendPaymentReminder;
    }

    int sendPaymentReminder;

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

    private int netAmount, orderAmount, discount;

    public String getReceiptUrl() {
        return receiptUrl;
    }

    public void setReceiptUrl(String receiptUrl) {
        this.receiptUrl = receiptUrl;
    }

    String receiptUrl;

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    String paymentStatus;

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public int getAppointmentId() {
        return appointmentId;
    }

    public void setAppointmentId(int appointmentId) {
        this.appointmentId = appointmentId;
    }

    int orderId;
    int appointmentId;

    public int getChatId() {
        return chatId;
    }

    public void setChatId(int chatId) {
        this.chatId = chatId;
    }

    public int getRecipientId() {
        return recipientId;
    }

    public void setRecipientId(int recipientId) {
        this.recipientId = recipientId;
    }

    int recipientId;

    public String getPatientName() {
        return patientName;
    }

    public String getChatDateTime() {
        return chatDateTime;
    }

    public String getReadCount() {
        return readCount;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    public void setChatDateTime(String chatDateTime) {
        this.chatDateTime = chatDateTime;
    }

    public void setReadCount(String readCount) {
        this.readCount = readCount;
    }
}
