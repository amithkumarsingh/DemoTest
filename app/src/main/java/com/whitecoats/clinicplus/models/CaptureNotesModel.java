package com.whitecoats.clinicplus.models;

import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;

public class CaptureNotesModel {

    public String getNotesText() {
        return notesText;
    }

    public void setNotesText(String notesText) {
        this.notesText = notesText;
    }

    String notesText;

    public int getCaptureNoteId() {
        return captureNoteId;
    }

    public void setCaptureNoteId(int captureNoteId) {
        this.captureNoteId = captureNoteId;
    }

    int captureNoteId;

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    private boolean isSelected;


    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    private String fileUrl;
}