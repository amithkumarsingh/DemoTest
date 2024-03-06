package com.whitecoats.clinicplus.models;

public class AddProcedureListModel {

    int procedureId;
    String procedureName;
    int charges;
    int tax;
    int discount;
    int totalAmount;
    Double serviceTax;
    Double serviceDiscount;
    int serviceAmount;
    Double serviceCharges;
    String taxText;
    int applyCoupon;
    boolean isDiscountApplied;
    int appt_service_id;


    public String getProcedureName() {
        return procedureName;
    }

    public void setProcedureName(String procedureName) {
        this.procedureName = procedureName;
    }

    public int getProcedureId() {
        return procedureId;
    }

    public void setProcedureId(int procedureId) {
        this.procedureId = procedureId;
    }


    public int getCharges() {
        return charges;
    }

    public void setCharges(int charges) {
        this.charges = charges;
    }

    public int getTax() {
        return tax;
    }

    public void setTax(int tax) {
        this.tax = tax;
    }

    public int getDiscount() {
        return discount;
    }

    public void setDiscount(int discount) {
        this.discount = discount;
    }

    public int getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(int totalAmount) {
        this.totalAmount = totalAmount;
    }
    public int getServiceAmount() {
        return serviceAmount;
    }

    public void setServiceAmount(int serviceAmount) {
        this.serviceAmount = serviceAmount;
    }


    public Double getServiceCharges() {
        return serviceCharges;
    }

    public void setServiceCharges(Double serviceCharges) {
        this.serviceCharges = serviceCharges;
    }


    public Double getServiceTax() {
        return serviceTax;
    }

    public void setServiceTax(Double serviceTax) {
        this.serviceTax = serviceTax;
    }

    public Double getServiceDiscount() {
        return serviceDiscount;
    }

    public void setServiceDiscount(Double serviceDiscount) {
        this.serviceDiscount = serviceDiscount;
    }



    public String getTaxText() {
        return taxText;
    }

    public void setTaxText(String taxText) {
        this.taxText = taxText;
    }


    public int getApplyCoupon() {
        return applyCoupon;
    }

    public void setApplyCoupon(int applyCoupon) {
        this.applyCoupon = applyCoupon;
    }

    public boolean isDiscountApplied() {
        return isDiscountApplied;
    }

    public void setDiscountApplied(boolean discountApplied) {
        isDiscountApplied = discountApplied;
    }

    public int getAppt_service_id() {
        return appt_service_id;
    }

    public void setAppt_service_id(int appt_service_id) {
        this.appt_service_id = appt_service_id;
    }
}