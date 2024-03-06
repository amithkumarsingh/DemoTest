package com.whitecoats.clinicplus;

/**
 * Created by vaibhav on 18-03-2018.
 */

public class AppDoctorManager {


    private String docName, docPHno, docEmail , docExp, docLang;
    private int docId, docGender;

    public AppDoctorManager(int docId, String docName, String docPHno,
                            int docGender, String docEmail, String docExp, String docLang) {

        this.docId = docId;
        this.docName = docName;
        this.docPHno = docPHno;
        this.docEmail = docEmail;
        this.docGender = docGender;
        this.docExp = docExp;
        this.docLang = docLang;

    }

    public void setDocName(String docName) {
        this.docName = docName;
    }
    public int getDocId() {
        return docId;
    }

    public String getDocName() {
        return docName;
    }

    public String getDocPHno() {
        return docPHno;
    }

    public String getDocEmail() {
        return docEmail;
    }

    public int getDocGender() {
        return docGender;
    }

    public String getDocExp() {
        return docExp;
    }

    public String getDocLang() {
        return docLang;
    }
}
