package com.whitecoats.model;

public class QuickActionViewItem {

    public String getQuickActionName() {
        return quickActionName;
    }

    public void setQuickActionName(String quickActionName) {
        this.quickActionName = quickActionName;
    }

    public String getQuickActionIcon() {
        return quickActionIcon;
    }

    public void setQuickActionIcon(String quickActionIcon) {
        this.quickActionIcon = quickActionIcon;
    }

    public int getQuickActionId() {
        return quickActionId;
    }

    public void setQuickActionId(int quickActionId) {
        this.quickActionId = quickActionId;
    }

    public int getQuickActionHiddenForDoctorOnly() {
        return quickActionHiddenForDoctorOnly;
    }

    public void setQuickActionHiddenForDoctorOnly(int quickActionHiddenForDoctorOnly) {
        this.quickActionHiddenForDoctorOnly = quickActionHiddenForDoctorOnly;
    }

    public int getCarImageId() {
        return carImageId;
    }

    public void setCarImageId(int carImageId) {
        this.carImageId = carImageId;
    }

    // Save car name.
    private String quickActionName;
    private String quickActionIcon;
    private int quickActionId;
    private int quickActionHiddenForDoctorOnly;

    // Save car image resource id.
    private int carImageId;

    public QuickActionViewItem(String quickActionName, String quickActionIcon, int quickActionId, int quickActionHiddenForDoctorOnly) {
        this.quickActionName = quickActionName;
        this.quickActionIcon = quickActionIcon;
        this.quickActionId = quickActionId;
        this.quickActionHiddenForDoctorOnly = quickActionHiddenForDoctorOnly;
    }


}
