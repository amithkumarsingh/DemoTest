package com.whitecoats.model;

public class HistoryModel {

    String date, detailText, type;
    boolean isDate;

    public String getDate() {
        return date;
    }

    public String getDetailText() {
        return detailText;
    }

    public boolean isDate() {
        return isDate;
    }

    public String getType() {
        return type;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setDetailText(String detailText) {
        this.detailText = detailText;
    }

    public void isDate(boolean date) {
        isDate = date;
    }

    public void setType(String type) {
        this.type = type;
    }
}
