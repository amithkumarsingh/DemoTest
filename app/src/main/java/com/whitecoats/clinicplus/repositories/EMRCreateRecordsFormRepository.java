package com.whitecoats.clinicplus.repositories;

import android.app.Activity;

import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.common.api.Api;
import com.whitecoats.clinicplus.activities.EMRCreateRecordsFormActivity;
import com.whitecoats.clinicplus.apis.ApiMethodCalls;
import com.whitecoats.clinicplus.apis.ApiUrls;
import com.whitecoats.clinicplus.interfaces.ApiCallbackInterface;

import org.json.JSONObject;

/**
 * Created by Mohammad suhail ahmed on 01-09-2020.
 */
public class EMRCreateRecordsFormRepository {

    private static EMRCreateRecordsFormRepository emrCreateRecordsFormRepository;
    private ApiMethodCalls apiMethodCalls = new ApiMethodCalls();

    public static EMRCreateRecordsFormRepository getInstance() {
        if (emrCreateRecordsFormRepository == null) {
            emrCreateRecordsFormRepository = new EMRCreateRecordsFormRepository();
        }

        return emrCreateRecordsFormRepository;
    }

    public MutableLiveData<String> saveHandwrittenNotes(Activity activity, int createdBy, int encounterId, int episodeId, String description, String fileUrl, int patientId, int medPrescription, int testPrescription, String validTill, String editRecord, int Record_ID) {
        MutableLiveData<String> saveHandWrittenNotesResponse = new MutableLiveData<>();
        try {
            JSONObject postData = new JSONObject();
            postData.put("created_by", createdBy);
            postData.put("description", description);
            postData.put("encounter_id", encounterId);
            postData.put("episode_id", episodeId);
            postData.put("file_url", fileUrl);
            postData.put("has_med_prescription", medPrescription);
            postData.put("has_test_prescription", testPrescription);
            postData.put("med_prescription_valid_till", validTill);
            postData.put("patient_id", patientId);

            String URL = "";
            if (editRecord.equalsIgnoreCase("editRecord")) {
                postData.put("type", "handWrittenNotesRecords");
                postData.put("id", Record_ID);
                URL = ApiUrls.updatePrescriptionRecord;
            } else {
                URL = ApiUrls.saveNewUploadHandWrittenNotes;
            }

            apiMethodCalls.postApiData(URL, postData, activity, new ApiCallbackInterface() {
                @Override
                public void onSuccessResponse(String response) {
                    saveHandWrittenNotesResponse.postValue(response);
                }

                @Override
                public void onErrorResponse(String error) {
                    saveHandWrittenNotesResponse.postValue(error);

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return saveHandWrittenNotesResponse;
    }

    public MutableLiveData<String> deletePrescriptionRecord(Activity activity, int Record_ID, String catName) {
        MutableLiveData<String> deletePrescriptionRecordResponse = new MutableLiveData<>();
        try {
            JSONObject postData = new JSONObject();
            if (catName.equalsIgnoreCase("HandWritten Note")) {
                postData.put("type", "handWrittenNotesRecords");
            } else if (catName.equalsIgnoreCase("Diagnosis")) {
                postData.put("type", "diagnosisRecords");
            } else if (catName.equalsIgnoreCase("Symptoms")) {
                postData.put("type", "symptomsRecords");
            } else if (catName.equalsIgnoreCase("Investigation Results")) {
                postData.put("type", "investigationResultsRecords");
            } else if (catName.equalsIgnoreCase("observations")) {
                postData.put("type", "observationsRecords");
            } else if (catName.equalsIgnoreCase("treatmentplan")) {
                postData.put("type", "treatmentPlanRecords");
            }
            postData.put("id", Record_ID);
            String URL = ApiUrls.deletePrescriptionRecord;


            apiMethodCalls.postApiData(URL, postData, activity, new ApiCallbackInterface() {
                @Override
                public void onSuccessResponse(String response) {
                    deletePrescriptionRecordResponse.postValue(response);
                }

                @Override
                public void onErrorResponse(String error) {
                    deletePrescriptionRecordResponse.postValue(error);

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return deletePrescriptionRecordResponse;
    }


    public MutableLiveData<String> getImagePath(Activity activity, String url) {
        MutableLiveData<String> getImageDataResponse = new MutableLiveData<>();
        try {
            JSONObject postData = new JSONObject();
            postData.put("url", url);
            apiMethodCalls.postApiData(ApiUrls.getArticleImage, postData, activity, new ApiCallbackInterface() {
                @Override
                public void onSuccessResponse(String response) {
                    getImageDataResponse.postValue(response);
                }

                @Override
                public void onErrorResponse(String error) {
                    getImageDataResponse.postValue(error);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return getImageDataResponse;
    }


}
