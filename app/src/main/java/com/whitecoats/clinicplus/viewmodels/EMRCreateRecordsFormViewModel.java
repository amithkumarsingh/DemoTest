package com.whitecoats.clinicplus.viewmodels;

import android.app.Activity;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.whitecoats.clinicplus.repositories.EMRCreateRecordsFormRepository;

/**
 * Created by Mohammad suhail ahmed on 01-09-2020.
 */
public class EMRCreateRecordsFormViewModel extends ViewModel {
    private EMRCreateRecordsFormRepository emrCreateRecordsFormRepository;

    public void init() {
        emrCreateRecordsFormRepository = EMRCreateRecordsFormRepository.getInstance();
    }

    public MutableLiveData<String> getImagePath(Activity activity, String url) {
        return emrCreateRecordsFormRepository.getImagePath(activity, url);
    }

    public MutableLiveData<String> saveHandwrittenNotes(Activity activity, int createdBy, int encounterId, int episodeId, String description, String fileUrl, int patientId, int medPrescription, int testPrescription, String validTill, String editRecord, int Record_ID) {
        return emrCreateRecordsFormRepository.saveHandwrittenNotes(activity, createdBy, encounterId, episodeId, description, fileUrl, patientId, medPrescription, testPrescription, validTill, editRecord, Record_ID);
    }

    public MutableLiveData<String> deletePrescriptionRecords(Activity activity, int Record_ID, String catName) {
        return emrCreateRecordsFormRepository.deletePrescriptionRecord(activity, Record_ID, catName);
    }


}
