package com.whitecoats.clinicplus.utils;

public enum Enum {

    fiveHundred(500), forHundred(400), forNotFour(404), forNotOne(401);
    int statusCodeVale;

    private Enum(int mValue) {
        this.statusCodeVale = mValue;
    }

    public int getEnum() {
        return statusCodeVale;
    }
}
