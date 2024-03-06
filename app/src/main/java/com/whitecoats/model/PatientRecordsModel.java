package com.whitecoats.model;

import org.json.JSONArray;

public class PatientRecordsModel {

    String recordName;
    int recordId;
    int type;
    int recordCount;

    public JSONArray getRecordIdArray() {
        return recordIdArray;
    }

    public void setRecordIdArray(JSONArray recordIdArray) {
        this.recordIdArray = recordIdArray;
    }

    JSONArray recordIdArray;

    //hand note
    String hnAttachURL;
    String hnDesp;
    String hnMedPres;
    String hnTestPres;
    String hnValidTill;
    String createdUser;
    String created_At;

    //family section
    String famName, famAge, famRelation, famProblems;

    //non episodic
    String fieldDictionary, catName, catId, catRecordData;

    //shared records
    String primaryData, secData, ternaryData, primaryKey, secKey, ternaryKey;
    String fileUrl;

    //episodic data
    String episodeName;
    int episodeId;

    //pdf data
    String pdfUrl, pdfCreatedDate;

    //eval symptom
    String symptomName;
    String symptomStatus;
    String symptomFirstSeen;
    String symptom_description;

    //eval invest
    String investName, investParam, investValue, investNote;

    //eval diag
    String diagName, diagPoisted, diagStatus, diagConfirmed;

    //treatment plan
    String treatPlanName;
    int treatPlanCount;

    //eval obs
    String obsCatName;
    int obsCount;

    public int getRecordId() {
        return recordId;
    }

    public String getRecordName() {
        return recordName;
    }

    public int getType() {
        return type;
    }

    public void setRecordId(int recordId) {
        this.recordId = recordId;
    }

    public void setRecordName(String recordName) {
        this.recordName = recordName;
    }

    public void setType(int type) {
        this.type = type;
    }

    //hand written

    public String getHnAttachURL() {
        return hnAttachURL;
    }

    public String getHnDesp() {
        return hnDesp;
    }

    public String getHnMedPres() {
        return hnMedPres;
    }

    public String getHnTestPres() {
        return hnTestPres;
    }

    public String getHnValidTill() {
        return hnValidTill;
    }

    public void setHnAttachURL(String hnAttachURL) {
        this.hnAttachURL = hnAttachURL;
    }

    public void setHnDesp(String hnDesp) {
        this.hnDesp = hnDesp;
    }

    public void setHnMedPres(String hnMedPres) {
        this.hnMedPres = hnMedPres;
    }

    public void setHnTestPres(String hnTestPres) {
        this.hnTestPres = hnTestPres;
    }

    public void setHnValidTill(String hnValidTill) {
        this.hnValidTill = hnValidTill;
    }
    public String getCreatedUser() { return createdUser;}

    public void setCreatedUser(String createdUser) { this.createdUser = createdUser; }

    public String getCreated_At() { return created_At;}

    public void setCreated_At(String created_At) { this.created_At = created_At;}

    //family section
    public String getFamName() {
        return famName;
    }

    public String getFamAge() {
        return famAge;
    }

    public String getFamRelation() {
        return famRelation;
    }

    public String getFamProblems() {
        return famProblems;
    }

    public void setFamAge(String famAge) {
        this.famAge = famAge;
    }

    public void setFamName(String famName) {
        this.famName = famName;
    }

    public void setFamRelation(String famRelation) {
        this.famRelation = famRelation;
    }

    public void setFamProblems(String famProblems) {
        this.famProblems = famProblems;
    }

    //non episodic
    public String getCatName() {
        return catName;
    }

    public String getCatId() {
        return catId;
    }

    public String getFieldDictionary() {
        return fieldDictionary;
    }

    public String getCatRecordData() {
        return catRecordData;
    }

    public void setFieldDictionary(String fieldDictionary) {
        this.fieldDictionary = fieldDictionary;
    }

    public void setCatName(String catName) {
        this.catName = catName;
    }

    public void setCatId(String catId) {
        this.catId = catId;
    }

    public void setCatRecordData(String catRecordData) {
        this.catRecordData = catRecordData;
    }

    public int getRecordCount() {
        return recordCount;
    }

    public void setRecordCount(int recordCount) {
        this.recordCount = recordCount;
    }

    //shared record

    public String getPrimaryData() {
        return primaryData;
    }

    public String getSecData() {
        return secData;
    }

    public String getTernaryData() {
        return ternaryData;
    }

    public String getPrimaryKey() {
        return primaryKey;
    }

    public String getSecKey() {
        return secKey;
    }

    public String getTernaryKey() {
        return ternaryKey;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public void setPrimaryData(String primaryData) {
        this.primaryData = primaryData;
    }

    public void setSecData(String secData) {
        this.secData = secData;
    }

    public void setTernaryData(String ternaryData) {
        this.ternaryData = ternaryData;
    }

    public void setPrimaryKey(String primaryKey) {
        this.primaryKey = primaryKey;
    }

    public void setSecKey(String secKey) {
        this.secKey = secKey;
    }

    public void setTernaryKey(String ternaryKey) {
        this.ternaryKey = ternaryKey;
    }

    //episode get set

    public int getEpisodeId() {
        return episodeId;
    }

    public String getEpisodeName() {
        return episodeName;
    }

    public void setEpisodeId(int episodeId) {
        this.episodeId = episodeId;
    }

    public void setEpisodeName(String episodeName) {
        this.episodeName = episodeName;
    }

    //pdf

    public String getPdfCreatedDate() {
        return pdfCreatedDate;
    }

    public String getPdfUrl() {
        return pdfUrl;
    }

    public void setPdfCreatedDate(String pdfCreatedDate) {
        this.pdfCreatedDate = pdfCreatedDate;
    }

    public void setPdfUrl(String pdfUrl) {
        this.pdfUrl = pdfUrl;
    }

    //symptom


    public String getSymptomFirstSeen() {
        return symptomFirstSeen;
    }

    public String getSymptomName() {
        return symptomName;
    }

    public String getSymptomStatus() {
        return symptomStatus;
    }

    public void setSymptomFirstSeen(String symptomFirstSeen) {
        this.symptomFirstSeen = symptomFirstSeen;
    }

    public void setSymptomName(String symptomName) {
        this.symptomName = symptomName;
    }

    public void setSymptomStatus(String symptomStatus) {
        this.symptomStatus = symptomStatus;
    }
    public String getSymptom_description() {
        return symptom_description;
    }

    public void setSymptom_description(String symptom_description) {
        this.symptom_description = symptom_description;
    }

    //eval invest

    public String getInvestName() {
        return investName;
    }

    public String getInvestNote() {
        return investNote;
    }

    public String getInvestParam() {
        return investParam;
    }

    public String getInvestValue() {
        return investValue;
    }

    public void setInvestName(String investName) {
        this.investName = investName;
    }

    public void setInvestNote(String investNote) {
        this.investNote = investNote;
    }

    public void setInvestParam(String investParam) {
        this.investParam = investParam;
    }

    public void setInvestValue(String investValue) {
        this.investValue = investValue;
    }

    //eval diag

    public String getDiagConfirmed() {
        return diagConfirmed;
    }

    public String getDiagName() {
        return diagName;
    }

    public String getDiagPoisted() {
        return diagPoisted;
    }

    public String getDiagStatus() {
        return diagStatus;
    }

    public void setDiagConfirmed(String diagConfirmed) {
        this.diagConfirmed = diagConfirmed;
    }

    public void setDiagName(String diagName) {
        this.diagName = diagName;
    }

    public void setDiagPoisted(String diagPoisted) {
        this.diagPoisted = diagPoisted;
    }

    public void setDiagStatus(String diagStatus) {
        this.diagStatus = diagStatus;
    }

    //eval treatment plan

    public int getTreatPlanCount() {
        return treatPlanCount;
    }

    public String getTreatPlanName() {
        return treatPlanName;
    }

    public void setTreatPlanCount(int treatPlanCount) {
        this.treatPlanCount = treatPlanCount;
    }

    public void setTreatPlanName(String treatPlanName) {
        this.treatPlanName = treatPlanName;
    }

    //eval obs

    public int getObsCount() {
        return obsCount;
    }

    public String getObsCatName() {
        return obsCatName;
    }

    public void setObsCatName(String obsCatName) {
        this.obsCatName = obsCatName;
    }

    public void setObsCount(int obsCount) {
        this.obsCount = obsCount;
    }
}

