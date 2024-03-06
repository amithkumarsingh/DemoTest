package com.whitecoats.clinicplus.interfaces;

import android.view.View;

public interface EMRAddRecordsClickListener {
    public void onItemClick(View v, int position, int encounterId, String clickItem, String caseName, String InteractionDateOn);
}