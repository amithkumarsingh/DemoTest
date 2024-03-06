package com.whitecoats.clinicplus.models;

public class AppointmentServicesModel {
    private String productName;
    private int userId,productId,numAppts;
    public AppointmentServicesModel(int userId,String productName,int productId,int numAppts){
        this.userId = userId;
        this.productId = productId;
        this.productName = productName;
        this.numAppts = numAppts;
    }

    public int getNumAppts() {
        return numAppts;
    }

    public int getProductId() {
        return productId;
    }

    public String getProductName() {
        return productName;
    }

    public int getUserId() {
        return userId;
    }

}
