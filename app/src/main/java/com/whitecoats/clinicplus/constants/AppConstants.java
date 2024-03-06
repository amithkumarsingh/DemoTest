package com.whitecoats.clinicplus.constants;

import com.whitecoats.clinicplus.apis.ApiUrls;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

public class AppConstants {

    public static String APP_ORIGIN = "8";
    public static String LOGIN_TOKEN = ApiUrls.loginToken;
    public static int durationSelectedValue = 0;
    public static int selectedClinicIdOnDashBoard = 0;
    public static int selectedClinicClickOnDashBoard = 0;
    public static int clinicCount = 0;
    public static JSONArray relationMaster;

    public static String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    public static String phonePattern = "^\\s*(?:\\+?(\\d{1,3}))?[-. (]*(\\d{3})[-. )]*(\\d{3})[-. ]*(\\d{4})(?: *x(\\d+))?\\s*$";

    public static List<String> genderList = new ArrayList() {{
        add("Select Gender");
        add("Male");
        add("Female");
        add("Transgender");
    }};

    public static List<String> bloodGroupList = new ArrayList() {{
        add("Select Blood Group");
        add("O+");
        add("O-");
        add("A+");
        add("A-");
        add("B+");
        add("B-");
        add("AB+");
        add("AB-");
    }};

    public static List<String> ageTypeList = new ArrayList() {{
        add("Years");
        add("Months");
        add("Days");
    }};

    public static List<String> familyRelation = new ArrayList();

    public static String[] familyCondition = {
            "Cancer",
            "Cholesterol",
            "Diabetes Type 1",
            "Diabetes Type 2",
            "Hairloss",
            "Heart Condition",
            "Hypertension",
            "Infertility",
            "Neurological Condition"
    };

    public static String[] interactionDetails ={
            "Interaction Mode",
            "Clinic",
            "Video",
            "Chat",
            "Phone",
            "Home",
            "Other"
    };
    public static String[] prescriptionDetails = {
            "Select",
            "Yes",
            "No"
    };
    public static String[] testPrescriptionDetails = {
            "Select",
            "Yes",
            "No"
    };

    public static String appSharedPref = "WCA-CP";

}
