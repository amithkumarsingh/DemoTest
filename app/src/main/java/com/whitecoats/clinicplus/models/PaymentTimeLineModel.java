package com.whitecoats.clinicplus.models;

public class PaymentTimeLineModel {

    String text1, text2, text3;
    String color1, color2, color3;
    int isShowCancelRefund, isShowPayULink;
    String payULink;

    public String getApptBookedOn() {
        return apptBookedOn;
    }

    public void setApptBookedOn(String apptBookedOn) {
        this.apptBookedOn = apptBookedOn;
    }

    public String getApptScheduleOn() {
        return apptScheduleOn;
    }

    public void setApptScheduleOn(String apptScheduleOn) {
        this.apptScheduleOn = apptScheduleOn;
    }

    String apptBookedOn,apptScheduleOn;

    public String getText1() {
        return text1;
    }

    public void setText1(String text1) {
        this.text1 = text1;
    }

    public String getText2() {
        return text2;
    }

    public void setText2(String text2) {
        this.text2 = text2;
    }

    public String getText3() {
        return text3;
    }

    public void setText3(String text3) {
        this.text3 = text3;
    }

    public String getColor1() {
        return color1;
    }

    public void setColor1(String color1) {
        this.color1 = color1;
    }

    public String getColor2() {
        return color2;
    }

    public void setColor2(String color2) {
        this.color2 = color2;
    }

    public String getColor3() {
        return color3;
    }

    public void setColor3(String color3) {
        this.color3 = color3;
    }

    public int getIsShowCancelRefund() {
        return isShowCancelRefund;
    }

    public void setIsShowCancelRefund(int isShowCancelRefund) {
        this.isShowCancelRefund = isShowCancelRefund;
    }

    public int getIsShowPayULink() {
        return isShowPayULink;
    }

    public void setIsShowPayULink(int isShowPayULink) {
        this.isShowPayULink = isShowPayULink;
    }

    public String getPayULink() {
        return payULink;
    }

    public void setPayULink(String payULink) {
        this.payULink = payULink;
    }

}