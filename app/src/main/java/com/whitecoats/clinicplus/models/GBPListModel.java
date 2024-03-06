package com.whitecoats.clinicplus.models;

import android.widget.CheckBox;

import org.json.JSONArray;

import java.util.ArrayList;

public class GBPListModel {

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    private String serviceName;

    public int getDr_service_id() {
        return dr_service_id;
    }

    public void setDr_service_id(int dr_service_id) {
        this.dr_service_id = dr_service_id;
    }

    public String getGbp_page_link() {
        return gbp_page_link;
    }

    public void setGbp_page_link(String gbp_page_link) {
        this.gbp_page_link = gbp_page_link;
    }

    public String getGbp_review_link() {
        return gbp_review_link;
    }

    public void setGbp_review_link(String gbp_review_link) {
        this.gbp_review_link = gbp_review_link;
    }

    public boolean isAuto_share_post_consultation() {
        return auto_share_post_consultation;
    }

    public void setAuto_share_post_consultation(boolean auto_share_post_consultation) {
        this.auto_share_post_consultation = auto_share_post_consultation;
    }

    private int dr_service_id;
    private String gbp_page_link;
    private String gbp_review_link;
    private boolean auto_share_post_consultation;

    public boolean isInExpandedMode() {
        return isInExpandedMode;
    }

    public void setInExpandedMode(boolean inExpandedMode) {
        isInExpandedMode = inExpandedMode;
    }

    private boolean isInExpandedMode;

    public boolean getEditModeStatus() {
        return is_edit_mode_enable;
    }

    public void setEditModeStatus(boolean is_edit_mode_enable) {
        this.is_edit_mode_enable = is_edit_mode_enable;
    }

    public boolean is_edit_mode_enable = false;

    public int getService_id() {
        return service_id;
    }

    public void setService_id(int service_id) {
        this.service_id = service_id;
    }

    private int service_id;


    public boolean getApplyToAll() {
        return applyToAll;
    }

    public void setApplyToAll(boolean applyToAll) {
        this.applyToAll = applyToAll;
    }

    private boolean applyToAll;

}
