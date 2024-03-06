package com.whitecoats.clinicplus.interfaces;

import android.view.View;

public interface EMRMoreInfoClickListener {
    public void onItemClick(View v, int position,String fileUrl,String clickAction);
}