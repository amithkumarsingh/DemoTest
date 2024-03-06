package com.whitecoats.clinicplus.repositories;

import android.app.Activity;
import android.app.ProgressDialog;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.common.api.Api;
import com.google.gson.Gson;
import com.whitecoats.clinicplus.activities.EMRCreateRecordsFormActivity;
import com.whitecoats.clinicplus.apis.ApiMethodCalls;
import com.whitecoats.clinicplus.apis.ApiUrls;
import com.whitecoats.clinicplus.constants.AppConstants;
import com.whitecoats.clinicplus.interfaces.ApiCallbackInterface;
import com.whitecoats.clinicplus.models.EMRFamilyModel;
import com.whitecoats.clinicplus.models.PatientModel;
import com.whitecoats.clinicplus.utils.RecordSavingType;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class EMRConsultationNotesRepository {

    private static EMRConsultationNotesRepository instance;
    private ApiMethodCalls apiMethodCalls = new ApiMethodCalls();
    MutableLiveData<String> data = new MutableLiveData<>();
    private ProgressDialog loadingDialog;
    private JSONObject jsonValue;

    public static EMRConsultationNotesRepository getInstance() {
        if (instance == null) {
            instance = new EMRConsultationNotesRepository();
        }

        return instance;
    }

    public MutableLiveData<String> checkEncounterForAppointment(Activity activity, int appointmentId) {
        MutableLiveData<String> encounterForAppointmentResponses = new MutableLiveData<>();
        try {
            apiMethodCalls.getApiData(ApiUrls.checkEncounterForAppointment + "?appt_id=" + appointmentId, null, activity, new ApiCallbackInterface() {
                @Override
                public void onSuccessResponse(String response) {
                    encounterForAppointmentResponses.postValue(response);
                }

                @Override
                public void onErrorResponse(String error) {
                    encounterForAppointmentResponses.postValue(error);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return encounterForAppointmentResponses;
    }

    public MutableLiveData<String> checkEpisodesForAppointment(Activity activity, int appointmentId) {
        MutableLiveData<String> episodesForAppointmentResponses = new MutableLiveData<>();
        try {
            apiMethodCalls.getApiData(ApiUrls.checkEpisodesForAppointment + "?appt_id=" + appointmentId, null, activity, new ApiCallbackInterface() {
                @Override
                public void onSuccessResponse(String response) {
                    episodesForAppointmentResponses.postValue(response);
                }

                @Override
                public void onErrorResponse(String error) {
                    episodesForAppointmentResponses.postValue(error);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return episodesForAppointmentResponses;
    }

    public MutableLiveData<String> getEpisodicFieldPreference(Activity activity, int appointmentId) {
        MutableLiveData<String> getEpisodicFieldPreferenceResponses = new MutableLiveData<>();
        try {
            apiMethodCalls.getApiData(ApiUrls.getEpisodicFieldPreference, null, activity, new ApiCallbackInterface() {
                @Override
                public void onSuccessResponse(String response) {
                    getEpisodicFieldPreferenceResponses.postValue(response);
                }

                @Override
                public void onErrorResponse(String error) {
                    getEpisodicFieldPreferenceResponses.postValue(error);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return getEpisodicFieldPreferenceResponses;
    }

    public MutableLiveData<String> getDoctorEpisode(Activity activity, int patientId) {
        MutableLiveData<String> getDoctorEpisodeResponses = new MutableLiveData<>();
        try {
            apiMethodCalls.getApiData(ApiUrls.getDoctorEpisode + "?patient_id=" + patientId, null, activity, new ApiCallbackInterface() {
                @Override
                public void onSuccessResponse(String response) {
                    getDoctorEpisodeResponses.postValue(response);
                }

                @Override
                public void onErrorResponse(String error) {
                    getDoctorEpisodeResponses.postValue(error);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return getDoctorEpisodeResponses;
    }

    public MutableLiveData<String> getEncounterDropDown(Activity activity, int patientId, int episodeId) {
        MutableLiveData<String> geEncounterDropDownResponses = new MutableLiveData<>();
        try {
            apiMethodCalls.getApiData(ApiUrls.getEncounterDropDown + "?patient_id=" + patientId + "&episode_id=" + episodeId, null, activity, new ApiCallbackInterface() {
                @Override
                public void onSuccessResponse(String response) {
                    geEncounterDropDownResponses.postValue(response);
                }

                @Override
                public void onErrorResponse(String error) {
                    geEncounterDropDownResponses.postValue(error);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return geEncounterDropDownResponses;
    }

    public MutableLiveData<String> getAddedRecords(Activity activity, int patientId, int encounterId) {
        MutableLiveData<String> addedRecordsresponse = new MutableLiveData<>();
        String url = ApiUrls.getAddedNotes + "?encounter_id=" + encounterId + "&patient_id=" + patientId;
        Log.i("added records url", url);
        try {
            apiMethodCalls.getApiData(url, null, activity, new ApiCallbackInterface() {
                @Override
                public void onSuccessResponse(String response) {
                    addedRecordsresponse.postValue(response);
                }

                @Override
                public void onErrorResponse(String error) {
                    addedRecordsresponse.postValue(error);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return addedRecordsresponse;

    }

    public MutableLiveData<String> getAllComponentForCaseHistory(Activity activity, int patientId, int episodeId, int allInteraction) {
        MutableLiveData<String> getAllComponentForCaseHistoryResponses = new MutableLiveData<>();
        String episodeAndInteractionId;
        String allComponentCaseHistoryAndEncounter;
        if (allInteraction == 0) {
            episodeAndInteractionId = "&episode_id=" + episodeId;
            allComponentCaseHistoryAndEncounter = ApiUrls.getAllComponentForCaseHistory;
        } else {
            episodeAndInteractionId = "&encounter_id=" + episodeId;
            allComponentCaseHistoryAndEncounter = ApiUrls.getAllComponentForCaseHistoryEncounter;
        }

        try {
            apiMethodCalls.getApiData(allComponentCaseHistoryAndEncounter + "?patient_id=" + patientId + episodeAndInteractionId, null, activity, new ApiCallbackInterface() {
                @Override
                public void onSuccessResponse(String response) {
                    getAllComponentForCaseHistoryResponses.postValue(response);
                }

                @Override
                public void onErrorResponse(String error) {
                    getAllComponentForCaseHistoryResponses.postValue(error);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return getAllComponentForCaseHistoryResponses;
    }


    public MutableLiveData<String> evaluationDoctorCategory(Activity activity) {
        MutableLiveData<String> evaluationDoctorCategoryResponses = new MutableLiveData<>();
        try {

            apiMethodCalls.getApiData(ApiUrls.getEvaluationDoctorCategory, null, activity, new ApiCallbackInterface() {
                @Override
                public void onSuccessResponse(String response) {
                    evaluationDoctorCategoryResponses.postValue(response);
                }

                @Override
                public void onErrorResponse(String error) {
                    evaluationDoctorCategoryResponses.postValue(error);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return evaluationDoctorCategoryResponses;
    }

    public MutableLiveData<String> getPrescriptionDetails(Activity activity, int encounterId, int patientId) {
        MutableLiveData<String> getPrescriptionResponse = new MutableLiveData<>();
        String url = ApiUrls.getPrescriptionDetails + "?encounter_id=" + encounterId + "&patient_id=" + patientId;
        try {
            apiMethodCalls.getApiData(url, null, activity, new ApiCallbackInterface() {
                @Override
                public void onSuccessResponse(String response) {
                    getPrescriptionResponse.postValue(response);
                }

                @Override
                public void onErrorResponse(String error) {
                    getPrescriptionResponse.postValue(error);

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return getPrescriptionResponse;
    }

    public MutableLiveData<String> getPrescriptionNoteCount(Activity activity, int encounterId, int patientId) {
        MutableLiveData<String> getPrescriptionResponse = new MutableLiveData<>();
        String url = ApiUrls.getPrescriptionNoteCount + "?encounter_id=" + encounterId + "&patient_id=" + patientId;
        try {
            apiMethodCalls.getApiData(url, null, activity, new ApiCallbackInterface() {
                @Override
                public void onSuccessResponse(String response) {
                    getPrescriptionResponse.postValue(response);
                }

                @Override
                public void onErrorResponse(String error) {
                    getPrescriptionResponse.postValue(error);

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return getPrescriptionResponse;
    }

    public MutableLiveData<String> getSimboAuthKey(Activity activity) {
        MutableLiveData<String> getSimboAuthKeyResponse = new MutableLiveData<>();
        String url = ApiUrls.getSimboAuthKey;
        try {
            apiMethodCalls.getApiData(url, null, activity, new ApiCallbackInterface() {
                @Override
                public void onSuccessResponse(String response) {
                    getSimboAuthKeyResponse.postValue(response);
                }

                @Override
                public void onErrorResponse(String error) {
                    getSimboAuthKeyResponse.postValue(error);

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return getSimboAuthKeyResponse;
    }

    public MutableLiveData<String> saveNewEncounter(Activity activity, int apptId, int chatId, String dateTime, String mode, int episodeId, String patientId) {
        MutableLiveData<String> saveNewEncounterResponse = new MutableLiveData<>();
        try {
            JSONObject postData = new JSONObject();
            postData.put("appointment_id", apptId);
            postData.put("chat_id", chatId);
            postData.put("encounter_date_time", dateTime);
            postData.put("encounter_mode", mode);
            postData.put("episode_id", episodeId);
            postData.put("patient_id", patientId);
            apiMethodCalls.postApiData(ApiUrls.saveNewEncounter, postData, activity, new ApiCallbackInterface() {
                @Override
                public void onSuccessResponse(String response) {
                    saveNewEncounterResponse.postValue(response);
                }

                @Override
                public void onErrorResponse(String error) {
                    saveNewEncounterResponse.postValue(error);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return saveNewEncounterResponse;
    }

    public MutableLiveData<String> saveNewEpisode(Activity activity, int createdBy, String episodeName, int patientId) {
        MutableLiveData<String> saveNewEpisodeResponse = new MutableLiveData<>();
        try {
            JSONObject postData = new JSONObject();
            postData.put("created_by", createdBy);
            postData.put("episode_name", episodeName);
            postData.put("patient_id", patientId);
            apiMethodCalls.postApiData(ApiUrls.saveNewEpisode, postData, activity, new ApiCallbackInterface() {
                @Override
                public void onSuccessResponse(String response) {
                    saveNewEpisodeResponse.postValue(response);
                }

                @Override
                public void onErrorResponse(String error) {
                    saveNewEpisodeResponse.postValue(error);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return saveNewEpisodeResponse;
    }

    public MutableLiveData<String> treatmentPlanDoctorCategory(Activity activity) {
        MutableLiveData<String> treatmentPlanDoctorCategoryResponses = new MutableLiveData<>();
        try {
            apiMethodCalls.getApiData(ApiUrls.getTreatmentPlanDoctorCategory, null, activity, new ApiCallbackInterface() {
                @Override
                public void onSuccessResponse(String response) {
                    treatmentPlanDoctorCategoryResponses.postValue(response);
                }

                @Override
                public void onErrorResponse(String error) {
                    treatmentPlanDoctorCategoryResponses.postValue(error);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return treatmentPlanDoctorCategoryResponses;
    }

    public MutableLiveData<String> episodeFieldPreferences(Activity activity) {
        MutableLiveData<String> episodeFieldPreferencesResponses = new MutableLiveData<>();
        try {
            apiMethodCalls.getApiData(ApiUrls.getEpisodicFieldPreference, null, activity, new ApiCallbackInterface() {
                @Override
                public void onSuccessResponse(String response) {
                    episodeFieldPreferencesResponses.postValue(response);
                }

                @Override
                public void onErrorResponse(String error) {
                    episodeFieldPreferencesResponses.postValue(error);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return episodeFieldPreferencesResponses;
    }

    public MutableLiveData<String> getDictationControl(Activity activity) {
        MutableLiveData<String> getDictationControlResponses = new MutableLiveData<>();
        try {
            apiMethodCalls.getApiData(ApiUrls.getVoiceEMRPermission, null, activity, new ApiCallbackInterface() {
                @Override
                public void onSuccessResponse(String response) {
                    getDictationControlResponses.postValue(response);
                }

                @Override
                public void onErrorResponse(String error) {
                    getDictationControlResponses.postValue(error);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return getDictationControlResponses;
    }


    public MutableLiveData<String> evaluationFieldPreferences(Activity activity) {
        MutableLiveData<String> evaluationFieldPreferencesResponses = new MutableLiveData<>();
        try {
            apiMethodCalls.getApiData(ApiUrls.getEvaluationFieldPreferences, null, activity, new ApiCallbackInterface() {
                @Override
                public void onSuccessResponse(String response) {
                    evaluationFieldPreferencesResponses.postValue(response);
                }

                @Override
                public void onErrorResponse(String error) {
                    evaluationFieldPreferencesResponses.postValue(error);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return evaluationFieldPreferencesResponses;
    }


    public MutableLiveData<String> saveSymptom(Activity activity, JSONObject patientDetails, RecordSavingType saveType) {
        String URL;
        if (saveType.equals(RecordSavingType.UPDATE_SYMPTOMS)) {
            URL = ApiUrls.updatePrescriptionRecord;
        } else {
            URL = ApiUrls.saveSymptomRecords;
        }
        MutableLiveData<String> saveSymptomResponse = new MutableLiveData<>();
        try {
            apiMethodCalls.postApiData(URL, patientDetails, activity, new ApiCallbackInterface() {
                @Override
                public void onSuccessResponse(String response) {
                    saveSymptomResponse.postValue(response);
                }

                @Override
                public void onErrorResponse(String error) {
                    saveSymptomResponse.postValue(error);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return saveSymptomResponse;
    }

    public MutableLiveData<String> saveDiagnosis(Activity activity, JSONObject diagnosisObject, RecordSavingType saveType) {
        String URL;
        if (saveType.equals(RecordSavingType.UPDATE_DIAGNOSIS)) {
            URL = ApiUrls.updatePrescriptionRecord;
        } else {
            URL = ApiUrls.saveDiagnosisRecords;
        }
        MutableLiveData<String> saveDiagnosisResponse = new MutableLiveData<>();
        try {
            apiMethodCalls.postApiData(URL, diagnosisObject, activity, new ApiCallbackInterface() {
                @Override
                public void onSuccessResponse(String response) {
                    saveDiagnosisResponse.postValue(response);
                }

                @Override
                public void onErrorResponse(String error) {
                    saveDiagnosisResponse.postValue(error);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return saveDiagnosisResponse;
    }

    public MutableLiveData<String> saveInvestigationResult(Activity activity, JSONObject investigationObject, RecordSavingType saveType) {
        MutableLiveData<String> saveInvestigationResultResponse = new MutableLiveData<>();
        String URL;
        if (saveType.equals(RecordSavingType.UPDATE_INVESTIGATION)) {
            URL = ApiUrls.updatePrescriptionRecord;
        } else {
            URL = ApiUrls.saveInvestigationsRecords;
        }
        try {
            apiMethodCalls.postApiData(URL, investigationObject, activity, new ApiCallbackInterface() {
                @Override
                public void onSuccessResponse(String response) {
                    saveInvestigationResultResponse.postValue(response);
                }

                @Override
                public void onErrorResponse(String error) {
                    saveInvestigationResultResponse.postValue(error);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return saveInvestigationResultResponse;
    }

    public MutableLiveData<String> createPrescription(Activity activity, JSONObject createPrescriptionObject) {
        MutableLiveData<String> createPrescriptionResponse = new MutableLiveData<>();
        try {
            apiMethodCalls.postApiData(ApiUrls.createPrescriptionRecords, createPrescriptionObject, activity, new ApiCallbackInterface() {
                @Override
                public void onSuccessResponse(String response) {
                    createPrescriptionResponse.postValue(response);
                }

                @Override
                public void onErrorResponse(String error) {
                    createPrescriptionResponse.postValue(error);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return createPrescriptionResponse;
    }

    public MutableLiveData<String> sharePrescription(Activity activity, JSONObject sharePrescriptionObject) {
        MutableLiveData<String> sharePrescriptionResponse = new MutableLiveData<>();
        try {
            apiMethodCalls.postApiData(ApiUrls.sharePrescriptionRecords, sharePrescriptionObject, activity, new ApiCallbackInterface() {
                @Override
                public void onSuccessResponse(String response) {
                    sharePrescriptionResponse.postValue(response);
                }

                @Override
                public void onErrorResponse(String error) {
                    sharePrescriptionResponse.postValue(error);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sharePrescriptionResponse;
    }


    public MutableLiveData<String> presignedUrl(Activity activity, JSONObject urlObject) {
        MutableLiveData<String> presignedUrlResponse = new MutableLiveData<>();
        try {
            apiMethodCalls.postApiData(ApiUrls.getPresignedUrl, urlObject, activity, new ApiCallbackInterface() {
                @Override
                public void onSuccessResponse(String response) {
                    presignedUrlResponse.postValue(response);
                }

                @Override
                public void onErrorResponse(String error) {
                    presignedUrlResponse.postValue(error);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return presignedUrlResponse;
    }


    public MutableLiveData<String> docDiagnosisArrayDetails(Activity activity) {
        MutableLiveData<String> docDiagnosisArrayDetailsResponses = new MutableLiveData<>();
        try {
            apiMethodCalls.getApiData(ApiUrls.getDocDiagnosisDetails, null, activity, new ApiCallbackInterface() {
                @Override
                public void onSuccessResponse(String response) {
                    docDiagnosisArrayDetailsResponses.postValue(response);
                }

                @Override
                public void onErrorResponse(String error) {
                    docDiagnosisArrayDetailsResponses.postValue(error);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return docDiagnosisArrayDetailsResponses;
    }


    public LiveData<String> getStatus() {
        return data;
    }

}
