package com.whitecoats.clinicplus.models;

import com.whitecoats.clinicplus.utils.DictationItemType;

import java.io.Serializable;

public class RecordsItems extends DictationItemType implements Serializable {
    private DictationModel dictationModel;


    @Override
    public int getType() {
        return TYPE_GENERAL;
    }
   public void setRecordType(String recordType){
        super.setRecordType(recordType);
   }
    public DictationModel getVoiceEMRModel() {
        return dictationModel;
    }

    public void setVoiceEMRModel(DictationModel dictationModel) {
        this.dictationModel = dictationModel;
    }
}
