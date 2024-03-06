package com.whitecoats;

import com.whitecoats.clinicplus.models.GBPListModel;

public interface GBPListClickListener {
     void onListItemClick(String callFrom, GBPListModel gbpModelList,Boolean isSwitchClick,int position);
}
