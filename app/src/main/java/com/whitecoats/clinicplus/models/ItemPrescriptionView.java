package com.whitecoats.clinicplus.models;

import android.graphics.Bitmap;

public class ItemPrescriptionView {


    public ItemPrescriptionView(String image, int clickId, String fileUrl) {

//        super();
        this.image = image;
        this.clickId = clickId;
        this.fileUrl = fileUrl;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    String image;

    public int getClickId() {
        return clickId;
    }

    public void setClickId(int clickId) {
        this.clickId = clickId;
    }

    int clickId;

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    private String fileUrl;
}
