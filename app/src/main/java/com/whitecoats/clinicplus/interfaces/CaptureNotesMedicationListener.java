package com.whitecoats.clinicplus.interfaces;

import com.whitecoats.clinicplus.models.CaptureNotesMedicationModel;

public interface CaptureNotesMedicationListener {
    void OnCaptureNoteMedicationClick(int capturedNotesId, CaptureNotesMedicationModel captureNotesMedicationModel);

}
