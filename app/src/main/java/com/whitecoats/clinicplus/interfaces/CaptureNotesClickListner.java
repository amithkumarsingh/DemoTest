package com.whitecoats.clinicplus.interfaces;

import com.whitecoats.clinicplus.models.AddProcedureListModel;

public interface CaptureNotesClickListner {
    void OnCaptureNoteClick(int capturedNotesId, String captureNotesText);
}
