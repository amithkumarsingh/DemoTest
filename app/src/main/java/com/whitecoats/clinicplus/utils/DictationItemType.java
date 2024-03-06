package com.whitecoats.clinicplus.utils;

import android.app.LauncherActivity;

public abstract class DictationItemType {

    public static final int TYPE_SECTION_NAME = 0;
    public static final int TYPE_GENERAL = 1;

    abstract public int getType();

    public String getRecordType() {
        return recordType;
    }

    public void setRecordType(String recordType) {
        this.recordType = recordType;
    }

    public String recordType;

}
