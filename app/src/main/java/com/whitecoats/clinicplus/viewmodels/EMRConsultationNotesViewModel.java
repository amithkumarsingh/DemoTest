package com.whitecoats.clinicplus.viewmodels;

import android.app.Activity;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.whitecoats.clinicplus.activities.EMRCreateRecordsFormActivity;
import com.whitecoats.clinicplus.repositories.DashboardRepository;
import com.whitecoats.clinicplus.repositories.EMRConsultationNotesRepository;
import com.whitecoats.clinicplus.utils.RecordSavingType;

import org.json.JSONArray;
import org.json.JSONObject;

public class EMRConsultationNotesViewModel extends ViewModel {
    private LiveData<String> statusString = new MutableLiveData<>();
    private EMRConsultationNotesRepository emrConsultationNotesRepository;

    public void init() {
        emrConsultationNotesRepository = EMRConsultationNotesRepository.getInstance();
        statusString = emrConsultationNotesRepository.getStatus();
    }

    public LiveData<String> checkEncounterForAppointmentDetails(Activity activity,int appointmentId){
        return emrConsultationNotesRepository.checkEncounterForAppointment(activity,appointmentId);
    }

    public LiveData<String> checkEpisodeForAppointmentDetails(Activity activity,int appointmentId){
        return emrConsultationNotesRepository.checkEpisodesForAppointment(activity,appointmentId);
    }

    public LiveData<String> getEpisodicFieldPreferenceDetails(Activity activity,int appointmentId){
        return emrConsultationNotesRepository.getEpisodicFieldPreference(activity,appointmentId);
    }

    public LiveData<String> getDoctorEpisodeDetails(Activity activity,int patientId){
        return emrConsultationNotesRepository.getDoctorEpisode(activity,patientId);
    }
    public LiveData<String> saveNewEpisode(Activity activity, int createdBy,String episodeName,int patientId){
        return emrConsultationNotesRepository.saveNewEpisode(activity,createdBy,episodeName,patientId);
    }
    public LiveData<String> getSimboAuthKey(Activity activity){
        return emrConsultationNotesRepository.getSimboAuthKey(activity);
    }
    public LiveData<String> saveNewEncounter(Activity activity,int apptId,int chatId,String dateTime,String mode,int episodeId,String patientId){
        return emrConsultationNotesRepository.saveNewEncounter(activity,apptId,chatId,dateTime,mode,episodeId,patientId);
    }

    public LiveData<String> getEncounterDropDownList(Activity activity,int patientId,int episodeId){
        return emrConsultationNotesRepository.getEncounterDropDown(activity,patientId,episodeId);
    }

    public LiveData<String> getAllComponentForCaseHistoryDetails(Activity activity,int patientId,int episodeId,int allInteraction){
        return emrConsultationNotesRepository.getAllComponentForCaseHistory(activity,patientId,episodeId,allInteraction);
    }
    public LiveData<String> getAddedNotes(Activity activity,int patientId,int encounterId){
        return emrConsultationNotesRepository.getAddedRecords(activity,patientId,encounterId);
    }


    public LiveData<String> getEvaluationDoctorCategory(Activity activity){
        return emrConsultationNotesRepository.evaluationDoctorCategory(activity);
    }

    public LiveData<String> getTreatmentPlanDoctorCategory(Activity activity){
        return emrConsultationNotesRepository.treatmentPlanDoctorCategory(activity);
    }

    public LiveData<String> getEpisodeFieldPreferences(Activity activity){
        return emrConsultationNotesRepository.episodeFieldPreferences(activity);
    }

    public LiveData<String> getDictationControl(Activity activity){
        return emrConsultationNotesRepository.getDictationControl(activity);
    }

    public LiveData<String> getEvaluationFieldPreferences(Activity activity){
        return emrConsultationNotesRepository.evaluationFieldPreferences(activity);
    }

    public LiveData<String> saveSymptomRecord(Activity activity, JSONObject patientDetails,RecordSavingType saveType){
        return emrConsultationNotesRepository.saveSymptom(activity,patientDetails,saveType);
    }

    public LiveData<String> getDocDiagnosisArrayDetails(Activity activity){
        return emrConsultationNotesRepository.docDiagnosisArrayDetails(activity);
    }

    public LiveData<String> saveDiagnosisRecord(Activity activity, JSONObject diagnosisObject, RecordSavingType saveType){
        return emrConsultationNotesRepository.saveDiagnosis(activity,diagnosisObject,saveType);
    }

    public LiveData<String> saveInvestigationRecord(Activity activity, JSONObject investigationObject,RecordSavingType saveType){
        return emrConsultationNotesRepository.saveInvestigationResult(activity,investigationObject,saveType);
    }

    public LiveData<String> createPrescriptionRecord(Activity activity, JSONObject createPrescriptionObject){
        return emrConsultationNotesRepository.createPrescription(activity,createPrescriptionObject);
    }

    public LiveData<String> getPresignedUrl(Activity activity, JSONObject urlObject){
        return emrConsultationNotesRepository.presignedUrl(activity,urlObject);
    }

    public LiveData<String> sharedPrescriptionRecord(Activity activity, JSONObject createPrescriptionObject){
        return emrConsultationNotesRepository.sharePrescription(activity,createPrescriptionObject);
    }
    public MutableLiveData<String> getPrescriptionDetails(Activity activity,int encounterId,int patientId){
        return emrConsultationNotesRepository.getPrescriptionDetails(activity,encounterId,patientId);
    }

    public MutableLiveData<String> getPrescriptionNoteCountDetails(Activity activity,int encounterId,int patientId){
        return emrConsultationNotesRepository.getPrescriptionNoteCount(activity,encounterId,patientId);
    }

}
