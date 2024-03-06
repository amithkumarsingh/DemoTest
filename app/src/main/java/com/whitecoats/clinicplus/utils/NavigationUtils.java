package com.whitecoats.clinicplus.utils;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import androidx.appcompat.app.AppCompatActivity;

import com.whitecoats.clinicplus.BookAppointmentActivity;
import com.whitecoats.clinicplus.HelpActivity;
import com.whitecoats.clinicplus.MyClinicGlobalClass;
import com.whitecoats.clinicplus.SettingsFormActivity;
import com.whitecoats.clinicplus.activities.AddPatientActivity;
import com.whitecoats.clinicplus.activities.PaymentSetupScreen;
import com.whitecoats.clinicplus.apis.ApiUrls;
import com.whitecoats.clinicplus.trainingschedule.TrainingScheduleActivity;

import org.json.JSONObject;

import java.util.Map;

public class NavigationUtils extends AppCompatActivity {
    private int docID;
    private SharedPreferences appPreference;
    public static int navigation_path;

    public NavigationUtils(Context mContext, Map<String, Object> map) {
        appPreference = mContext.getSharedPreferences(ApiUrls.appSharedPref, Context.MODE_PRIVATE);
        docID = appPreference.getInt(ApiUrls.docId, 0);
        String deepLinkValue = (String) map.get("deepLink");
        if (deepLinkValue != null && !deepLinkValue.isEmpty()) {
            try {
                boolean needToNavigate = true;
                JSONObject navigationObj = new JSONObject(deepLinkValue);
                MyClinicGlobalClass.logUserActionEvent(docID, "deepLinkNavigation", MyClinicGlobalClass.convertJsonToHashMap(navigationObj));
                navigation_path = navigationObj.optInt("navigation_path");
                Intent intent = new Intent();
                if (navigation_path == NavigationType.ServiceSetup.getNavigationValue()) {
                    intent.setClass(mContext, SettingsFormActivity.class);
                    intent.putExtra("FormType", 7);
                    intent.putExtra("Title", "Service setup");
                } else if (navigation_path == NavigationType.PaymentSetup.getNavigationValue()) {
                    intent.setClass(mContext, PaymentSetupScreen.class);
                } else if (navigation_path == NavigationType.AddPatient.getNavigationValue()) {
                    intent.setClass(mContext, AddPatientActivity.class);
                } else if (navigation_path == NavigationType.Appointments.getNavigationValue()) {
                    intent.setClass(mContext, BookAppointmentActivity.class);
                    intent.putExtra("bookAppointment", 1);
                    intent.putExtra("patient name", "");
                } else if (navigation_path == NavigationType.BookedTrainings.getNavigationValue()) {
                    intent.setClass(mContext, TrainingScheduleActivity.class);
                } else if (navigation_path == NavigationType.UpcomingTrainings.getNavigationValue()) {
                    intent.setClass(mContext, TrainingScheduleActivity.class);
                }
                else if(navigation_path == NavigationType.SupportScreen.getNavigationValue()){
                    intent.setClass(mContext, HelpActivity.class);
                    //intent.setClass(mContext, GetHelpTabFragment.class);
                }

                if(needToNavigate) {
                    mContext.startActivity(intent);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


    }
}
