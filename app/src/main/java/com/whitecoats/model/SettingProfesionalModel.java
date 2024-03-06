package com.whitecoats.model;

import org.json.JSONObject;

public class SettingProfesionalModel {

    String languageName;
    String qualificationName, userId;
    int languageId, qualificationId, quaalificationUserId;

    public JSONObject getLanguagePivot() {
        return languagePivot;
    }

    public void setLanguagePivot(JSONObject languagePivot) {
        this.languagePivot = languagePivot;
    }

    JSONObject languagePivot;

    public String getLanguageName() {
        return languageName;
    }

    public void setLanguageName(String languageName) {
        this.languageName = languageName;
    }



    public String getQualificationName() {
        return qualificationName;
    }

    public void setQualificationName(String qualificationName) {
        this.qualificationName = qualificationName;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int getLanguageId() {
        return languageId;
    }

    public void setLanguageId(int languageId) {
        this.languageId = languageId;
    }

    public int getQualificationId() {
        return qualificationId;
    }

    public void setQualificationId(int qualificationId) {
        this.qualificationId = qualificationId;
    }

    public int getQuaalificationUserId() {
        return quaalificationUserId;
    }

    public void setQuaalificationUserId(int quaalificationUserId) {
        this.quaalificationUserId = quaalificationUserId;
    }


    @Override
    public String toString() {
        return languageName;
    }
}
