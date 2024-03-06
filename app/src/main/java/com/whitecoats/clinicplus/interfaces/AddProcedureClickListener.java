package com.whitecoats.clinicplus.interfaces;

import com.whitecoats.clinicplus.models.AddProcedureListModel;

public interface AddProcedureClickListener {
    void OnProcedureClick(int capturedNotesId, String captureNotesText, AddProcedureListModel addProcedureListModel,int discountAmount);
}
