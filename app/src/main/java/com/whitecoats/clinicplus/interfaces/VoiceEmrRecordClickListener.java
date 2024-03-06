package com.whitecoats.clinicplus.interfaces;

import android.view.View;

import com.whitecoats.clinicplus.models.VoiceEMRModel;

public interface VoiceEmrRecordClickListener {
    public void onItemClick(View v, int position, String clickActionType, String categoryName, VoiceEMRModel voiceEMRModel, String recordType);
}