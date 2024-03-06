package com.whitecoats.clinicplus.models;

import org.json.JSONObject;

import java.io.Serializable;

public class VoiceEMRModel implements Serializable {
    public String getCategoryName() {
        return CategoryName;
    }

    public void setCategoryName(String categoryName) {
        CategoryName = categoryName;
    }

    String CategoryName;

    public String getSubCategorySerNumber() {
        return subCategorySerNumber;
    }

    public void setSubCategorySerNumber(String subCategorySerNumber) {
        this.subCategorySerNumber = subCategorySerNumber;
    }

    public String getSubCategoryName() {
        return subCategoryName;
    }

    public void setSubCategoryName(String subCategoryName) {
        this.subCategoryName = subCategoryName;
    }

    String subCategorySerNumber;
    String subCategoryName;

    public String getSymptomDescription() {
        return symptomDescription;
    }

    public void setSymptomDescription(String symptomDescription) {
        this.symptomDescription = symptomDescription;
    }

    public String getSymptomFirstReportedOn() {
        return symptomFirstReportedOn;
    }

    public void setSymptomFirstReportedOn(String symptomFirstReportedOn) {
        this.symptomFirstReportedOn = symptomFirstReportedOn;
    }

    public String getSymptomStatus() {
        return symptomStatus;
    }

    public void setSymptomStatus(String symptomStatus) {
        this.symptomStatus = symptomStatus;
    }

    public String getSymptomName() {
        return symptomName;
    }

    public void setSymptomName(String symptomName) {
        this.symptomName = symptomName;
    }

    //for symptoms record
    String symptomName;
    String symptomDescription;
    String symptomFirstReportedOn;
    String symptomStatus;

    public String getSymptomData_id() {
        return symptomData_id;
    }

    public void setSymptomData_id(String symptomData_id) {
        this.symptomData_id = symptomData_id;
    }

    String symptomData_id;



    public String getDiagnosisDiagnosis() {
        return diagnosisDiagnosis;
    }

    public void setDiagnosisDiagnosis(String diagnosisDiagnosis) {
        this.diagnosisDiagnosis = diagnosisDiagnosis;
    }

    public String getDiagnosisStatus() {
        return diagnosisStatus;
    }

    public void setDiagnosisStatus(String diagnosisStatus) {
        this.diagnosisStatus = diagnosisStatus;
    }

    public String getDiagnosisPosited_on() {
        return diagnosisPosited_on;
    }

    public void setDiagnosisPosited_on(String diagnosisPosited_on) {
        this.diagnosisPosited_on = diagnosisPosited_on;
    }

    public String getDiagnosisConfirmed_ruledout_on() {
        return diagnosisConfirmed_ruledout_on;
    }

    public void setDiagnosisConfirmed_ruledout_on(String diagnosisConfirmed_ruledout_on) {
        this.diagnosisConfirmed_ruledout_on = diagnosisConfirmed_ruledout_on;
    }

    public boolean isDiagnosisSelectedFromAutocomplete() {
        return diagnosisSelectedFromAutocomplete;
    }

    public void setDiagnosisSelectedFromAutocomplete(boolean diagnosisSelectedFromAutocomplete) {
        this.diagnosisSelectedFromAutocomplete = diagnosisSelectedFromAutocomplete;
    }

    // for diagnosis record
    boolean diagnosisSelectedFromAutocomplete;
    String diagnosisDiagnosis;
    String diagnosisStatus;
    String diagnosisPosited_on;
    String diagnosisConfirmed_ruledout_on;

    public String getDiagnosisData_id() {
        return diagnosisData_id;
    }

    public void setDiagnosisData_id(String diagnosisData_id) {
        this.diagnosisData_id = diagnosisData_id;
    }

    String diagnosisData_id;


    public String getInvestigationInvestigation_name() {
        return investigationInvestigation_name;
    }

    public void setInvestigationInvestigation_name(String investigationInvestigation_name) {
        this.investigationInvestigation_name = investigationInvestigation_name;
    }

    public String getInvestigationValue() {
        return investigationValue;
    }

    public void setInvestigationValue(String investigationValue) {
        this.investigationValue = investigationValue;
    }

    public String getInvestigationNotes() {
        return investigationNotes;
    }

    public void setInvestigationNotes(String investigationNotes) {
        this.investigationNotes = investigationNotes;
    }

    public String getInvestigationParameter() {
        return investigationParameter;
    }

    public void setInvestigationParameter(String investigationParameter) {
        this.investigationParameter = investigationParameter;
    }

    public String getInvestigationFile_url() {
        return investigationFile_url;
    }

    public void setInvestigationFile_url(String investigationFile_url) {
        this.investigationFile_url = investigationFile_url;
    }

    public String getInvestigatioFfile_type() {
        return investigatioFfile_type;
    }

    public void setInvestigatioFfile_type(String investigatioFfile_type) {
        this.investigatioFfile_type = investigatioFfile_type;
    }

    // for investigation record
    String investigationInvestigation_name;
    String investigationValue;
    String investigationNotes;
    String investigationParameter;
    String investigationFile_url;
    String investigatioFfile_type;

    public String getInvestigationData_id() {
        return investigationData_id;
    }

    public void setInvestigationData_id(String investigationData_id) {
        this.investigationData_id = investigationData_id;
    }

    String investigationData_id;




    public String getObservationCategoryName() {
        return observationCategoryName;
    }

    public void setObservationCategoryName(String observationCategoryName) {
        this.observationCategoryName = observationCategoryName;
    }


    public String getObservationFieldDic() {
        return observationFieldDic;
    }

    public void setObservationFieldDic(String observationFieldDic) {
        this.observationFieldDic = observationFieldDic;
    }

    public int getObservationCategoryId() {
        return observationCategoryId;
    }

    public void setObservationCategoryId(int observationCategoryId) {
        this.observationCategoryId = observationCategoryId;
    }

    //for Observation
    int observationCategoryId;
    String observationCategoryName;

    public String getObservationData_id() {
        return observationData_id;
    }

    public void setObservationData_id(String observationData_id) {
        this.observationData_id = observationData_id;
    }

    String observationData_id;

    public String getObservationCategoryRecords() {
        return observationCategoryRecords;
    }

    public void setObservationCategoryRecords(String observationCategoryRecords) {
        this.observationCategoryRecords = observationCategoryRecords;
    }

    String observationCategoryRecords;
    String observationFieldDic;


    public int getTreatmentCategoryId() {
        return treatmentCategoryId;
    }

    public void setTreatmentCategoryId(int treatmentCategoryId) {
        this.treatmentCategoryId = treatmentCategoryId;
    }

    public String getTreatmentCategoryName() {
        return treatmentCategoryName;
    }

    public void setTreatmentCategoryName(String treatmentCategoryName) {
        this.treatmentCategoryName = treatmentCategoryName;
    }

    public String getTreatmentCategoryRecords() {
        return treatmentCategoryRecords;
    }

    public void setTreatmentCategoryRecords(String treatmentCategoryRecords) {
        this.treatmentCategoryRecords = treatmentCategoryRecords;
    }

    public String getTreatmentFieldDic() {
        return treatmentFieldDic;
    }

    public void setTreatmentFieldDic(String treatmentFieldDic) {
        this.treatmentFieldDic = treatmentFieldDic;
    }

    //for TreatmentPlan
    int treatmentCategoryId;
    String treatmentCategoryName;

    public String getTreatmentPlanData_id() {
        return treatmentPlanData_id;
    }

    public void setTreatmentPlanData_id(String treatmentPlanData_id) {
        this.treatmentPlanData_id = treatmentPlanData_id;
    }

    String treatmentPlanData_id;
    String treatmentCategoryRecords ;
    String treatmentFieldDic;





    public int getCategoryNameSymptomExistType() {
        return categoryNameSymptomExistType;
    }

    public void setCategoryNameSymptomExistType(int categoryNameSymptomExistType) {
        this.categoryNameSymptomExistType = categoryNameSymptomExistType;
    }

    int categoryNameSymptomExistType;

    public int getCategoryNameDiagnosisExistType() {
        return categoryNameDiagnosisExistType;
    }

    public void setCategoryNameDiagnosisExistType(int categoryNameDiagnosisExistType) {
        this.categoryNameDiagnosisExistType = categoryNameDiagnosisExistType;
    }

    public int getCategoryNameInvestigationResultsExistType() {
        return categoryNameInvestigationResultsExistType;
    }

    public void setCategoryNameInvestigationResultsExistType(int categoryNameInvestigationResultsExistType) {
        this.categoryNameInvestigationResultsExistType = categoryNameInvestigationResultsExistType;
    }

    int categoryNameDiagnosisExistType;
    int categoryNameInvestigationResultsExistType;

    public int getCategoryNameObservationExistType() {
        return categoryNameObservationExistType;
    }

    public void setCategoryNameObservationExistType(int categoryNameObservationExistType) {
        this.categoryNameObservationExistType = categoryNameObservationExistType;
    }

    int categoryNameObservationExistType;

    public int getCategoryNameTreatmentPlanExistType() {
        return categoryNameTreatmentPlanExistType;
    }

    public void setCategoryNameTreatmentPlanExistType(int categoryNameTreatmentPlanExistType) {
        this.categoryNameTreatmentPlanExistType = categoryNameTreatmentPlanExistType;
    }

    int categoryNameTreatmentPlanExistType;


    public String getVitalCategoryName() {
        return vitalCategoryName;
    }

    public void setVitalCategoryName(String vitalCategoryName) {
        this.vitalCategoryName = vitalCategoryName;
    }

    public int getVitalCategoryId() {
        return vitalCategoryId;
    }

    public void setVitalCategoryId(int vitalCategoryId) {
        this.vitalCategoryId = vitalCategoryId;
    }

    //vital popup
    String vitalCategoryName;
    int vitalCategoryId;
}
