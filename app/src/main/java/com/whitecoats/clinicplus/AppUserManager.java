package com.whitecoats.clinicplus;

import org.json.JSONObject;

/**
 * Created by vaibhav on 19-02-2018.
 */

public class AppUserManager {

    private String userName, userPHno, userEmail , userBday, bloodType, token;
    private int userId;
    private int userGender;
    private int isDoctorOnly;

    public AppUserManager(int userId, String userName, String userPHno,
                          int userGender, String userEmail, String blootType, String userBday, String token, int isDoctorOnly) {

        this.userId = userId;
        this.userName = userName;
        this.userPHno = userPHno;
        this.userEmail = userEmail;
        this.userGender = userGender;
        this.userBday = userBday;
        this.bloodType = blootType;
        this.token = token;
        this.isDoctorOnly = isDoctorOnly;
    }

    public int getUserId() {
        return userId;
    }

    public String getUserName() {
        return userName;
    }

    public String getUserPHno() {
        return userPHno;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public int getUserGender() {
        return userGender;
    }

    public String getUserBday() {
        return userBday;
    }

    public String getBloodType() {
        return bloodType;
    }

    public String getToken() {
        return token;
    }

    public int getIsDoctorOnly() {
        return isDoctorOnly;
    }

    public String toString() {
        JSONObject userData = new JSONObject();
        try {
            userData.put("id", userId);
            userData.put("name", userName);
            userData.put("phone", userPHno);
            userData.put("gender", userGender);
            userData.put("email", userEmail);
            userData.put("blood_group", bloodType);
            userData.put("dob", userBday);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return userData.toString();
    }
}
