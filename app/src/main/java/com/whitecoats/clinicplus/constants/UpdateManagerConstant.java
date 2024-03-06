package com.whitecoats.clinicplus.constants;

import com.google.android.play.core.install.model.AppUpdateType;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

public class UpdateManagerConstant {

    public static final int REQUEST_CODE = 781;
    public static final int FLEXIBLE = AppUpdateType.FLEXIBLE;
    public static final int IMMEDIATE = AppUpdateType.IMMEDIATE;

    public static String convertLongtoDates(Long millisec) {
        try {
            DateFormat Dformatter = new SimpleDateFormat("dd/MM/yyyy");
            String timeZone = Calendar.getInstance().getTimeZone().getID();
            Dformatter.setTimeZone(TimeZone.getTimeZone(timeZone));
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(millisec);
            return Dformatter.format(calendar.getTime());
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return "";
    }

}
