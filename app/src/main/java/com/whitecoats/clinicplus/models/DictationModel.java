package com.whitecoats.clinicplus.models;

import java.io.Serializable;

public class DictationModel implements Serializable {


    public VoiceEMRModel getVoiceEMRModel() {
        return voiceEMRModel;
    }

    public void setVoiceEMRModel(VoiceEMRModel voiceEMRModel) {
        this.voiceEMRModel = voiceEMRModel;
    }

    VoiceEMRModel voiceEMRModel;
    public String getSection_name() {
        return section_name;
    }

    public void setSection_name(String section_name) {
        this.section_name = section_name;
    }

    private  String section_name;

    public DictationModel(String sectionName, VoiceEMRModel voiceEMRModel) {
        this.section_name = sectionName;
        this.voiceEMRModel = voiceEMRModel;
    }
}
