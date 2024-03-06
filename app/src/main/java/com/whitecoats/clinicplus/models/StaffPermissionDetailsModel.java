package com.whitecoats.clinicplus.models;

import java.util.ArrayList;

public class StaffPermissionDetailsModel {

    private int staffId;
    private String staffName;
    private String staffPhNumber;
    private ArrayList<ActionPermissionsModel> permissionsModelArrayList;

    public int getStaffId() {
        return staffId;
    }

    public void setStaffId(int staffId) {
        this.staffId = staffId;
    }

    public String getStaffName() {
        return staffName;
    }

    public void setStaffName(String staffName) {
        this.staffName = staffName;
    }



    public String getStaffPhNumber() {
        return staffPhNumber;
    }

    public void setStaffPhNumber(String staffPhNumber) {
        this.staffPhNumber = staffPhNumber;
    }


    public ArrayList<ActionPermissionsModel> getPermissionsModelArrayList() {
        return permissionsModelArrayList;
    }

    public void setPermissionsModelArrayList(ArrayList<ActionPermissionsModel> permissionsModelArrayList) {
        this.permissionsModelArrayList = permissionsModelArrayList;
    }
}
