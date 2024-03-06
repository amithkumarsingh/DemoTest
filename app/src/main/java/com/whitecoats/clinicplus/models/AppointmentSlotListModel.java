package com.whitecoats.clinicplus.models;

import org.json.JSONObject;

public class AppointmentSlotListModel {

    private String slotTime;
    private String endSlotTime;
    private int appointmentServiceID;
    private String appointmentServiceName;
    private String appointmentServiceAlias;

    public JSONObject getInstantVideoJsonObject() {
        return instantVideoJsonObject;
    }

    public void setInstantVideoJsonObject(JSONObject instantVideoJsonObject) {
        this.instantVideoJsonObject = instantVideoJsonObject;
    }

    private JSONObject instantVideoJsonObject;

    public JSONObject getInstantVideoInfoJsonObject() {
        return instantVideoInfoJsonObject;
    }

    public void setInstantVideoInfoJsonObject(JSONObject instantVideoInfoJsonObject) {
        this.instantVideoInfoJsonObject = instantVideoInfoJsonObject;
    }

    private JSONObject instantVideoInfoJsonObject;

    public String getProdAliasName() {
        return prodAliasName;
    }

    public void setProdAliasName(String prodAliasName) {
        this.prodAliasName = prodAliasName;
    }

    private String prodAliasName;

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    private int price;

    public int getProdId() {
        return prodId;
    }

    public void setProdId(int prodId) {
        this.prodId = prodId;
    }

    public int getServId() {
        return servId;
    }

    public void setServId(int servId) {
        this.servId = servId;
    }

    private int prodId;
    private int servId;

    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    public int getPatientId() {
        return patientId;
    }

    public void setPatientId(int patientId) {
        this.patientId = patientId;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    private String patientName;
    private int patientId;
    private int productId;

    public int getAppointmentServiceID() {
        return appointmentServiceID;
    }

    public void setAppointmentServiceID(int appointmentServiceID) {
        this.appointmentServiceID = appointmentServiceID;
    }

    public String getAppointmentServiceName() {
        return appointmentServiceName;
    }

    public void setAppointmentServiceName(String appointmentServiceName) {
        this.appointmentServiceName = appointmentServiceName;
    }

    public String getAppointmentServiceAlias() {
        return appointmentServiceAlias;
    }

    public void setAppointmentServiceAlias(String appointmentServiceAlias) {
        this.appointmentServiceAlias = appointmentServiceAlias;
    }


    public String getEndSlotTime() {
        return endSlotTime;
    }

    public void setEndSlotTime(String endSlotTime) {
        this.endSlotTime = endSlotTime;
    }

    public String getSlotTime() {
        return slotTime;
    }

    public void setSlotTime(String slotTime) {
        this.slotTime = slotTime;
    }


    @Override
    public String toString() {
        return slotTime;
    }


}
