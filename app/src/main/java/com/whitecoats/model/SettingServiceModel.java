package com.whitecoats.model;

public class SettingServiceModel {

    String serviceName, serviceAmount;

    String tagTitle;
    int service_product_ID;
    int dr_service_ID;
    int clinic_Internal_Supersaas_ID;

    public int getClinic_Internal_Supersaas_ID() {
        return clinic_Internal_Supersaas_ID;
    }

    public void setClinic_Internal_Supersaas_ID(int clinic_Internal_Supersaas_ID) {
        this.clinic_Internal_Supersaas_ID = clinic_Internal_Supersaas_ID;
    }

    public int getDr_service_ID() {
        return dr_service_ID;
    }

    public void setDr_service_ID(int dr_service_ID) {
        this.dr_service_ID = dr_service_ID;
    }

    public int getService_product_ID() {
        return service_product_ID;
    }

    public void setService_product_ID(int service_product_ID) {
        this.service_product_ID = service_product_ID;
    }

    public String getIconName() {
        return iconName;
    }

    public void setIconName(String iconName) {
        this.iconName = iconName;
    }

    String iconName;

    public String getServiceName() {
        return serviceName;
    }

    public String getServiceAmount() {
        return serviceAmount;
    }

    public String getTagTitle() {
        return tagTitle;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public void setServiceAmount(String serviceAmount) {
        this.serviceAmount = serviceAmount;
    }

    public void setTagTitle(String tagTitle) {
        this.tagTitle = tagTitle;
    }
}
