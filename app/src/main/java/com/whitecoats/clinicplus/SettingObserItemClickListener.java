package com.whitecoats.clinicplus;

import android.view.View;

public interface SettingObserItemClickListener {
    public void onItemClick(View v, int position,boolean categoryStatus,String category,String name);
}