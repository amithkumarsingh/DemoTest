package com.whitecoats.clinicplus.models;

public class ActionPermissionsModel {

    private String permissionName;
    private boolean isEnabled;

    public String getPermissionName() {
        return permissionName;
    }

    public void setPermissionName(String permissionName) {
        this.permissionName = permissionName;
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    public void setEnabled(boolean enabled) {
        isEnabled = enabled;
    }
}
