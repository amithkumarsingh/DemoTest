package com.whitecoats.clinicplus.models;

import com.whitecoats.clinicplus.utils.DictationItemType;

public class SectionNameModel extends DictationItemType {
    private String sectionName;

    @Override
    public int getType() {
        return TYPE_SECTION_NAME;
    }

    public String getSectionName() {
        return sectionName;
    }

    public void setSectionName(String sectionName) {
        this.sectionName = sectionName;
        super.setRecordType(sectionName);
    }
}
