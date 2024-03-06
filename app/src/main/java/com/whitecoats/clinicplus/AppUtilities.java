package com.whitecoats.clinicplus;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Time;
import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;

public class AppUtilities {

    public String changeDateFormat(String inputFormat, String outputFormat, String dateStr) {

        String output;
        try {
            SimpleDateFormat srcDf = new SimpleDateFormat(inputFormat);
            Date date = srcDf.parse(dateStr);
            SimpleDateFormat destDf = new SimpleDateFormat(outputFormat);
            DateFormatSymbols symbols = new DateFormatSymbols(Locale.US);
            destDf.setDateFormatSymbols(symbols);
            output = destDf.format(date);
        } catch (Exception e) {
            e.printStackTrace();
            output = "";
        }

        return output;
    }

    public String changeTimeFormat(String time) {

        String output;
        try {
            String result = LocalTime.parse(time, DateTimeFormatter.ofPattern("HH:mm")).format(DateTimeFormatter.ofPattern("hh:mm aa"));
            output = result;
        } catch (Exception e) {
            e.printStackTrace();
            output = "";
        }

        return output;

    }

    public String changeTimeFormatWithSecond(String time) {

        String output;
        try {
            String result = LocalTime.parse(time, DateTimeFormatter.ofPattern("HH:mm:ss")).format(DateTimeFormatter.ofPattern("hh:mm aa"));
            output = result;
        } catch (Exception e) {
            e.printStackTrace();
            output = "";
        }

        return output;

    }

    public String changeTimeFormat12To24Hours(String time) {

        String output;
        try {
            String result = LocalTime.parse(time, DateTimeFormatter.ofPattern("hh:mm aa")).format(DateTimeFormatter.ofPattern("HH:mm"));
            output = result;
        } catch (Exception e) {
            e.printStackTrace();
            output = "";
        }

        return output;

    }


    public int timeFormatPreferences(Context mContext) {

        SharedPreferences sh = mContext.getSharedPreferences("TimeFormatPreference", Context.MODE_PRIVATE);

        int timeFormatOutPut;
        try {
            int timeFormatPreferenceValue = sh.getInt("timeFormat", 12);
            timeFormatOutPut = timeFormatPreferenceValue;
        } catch (Exception e) {
            e.printStackTrace();
            timeFormatOutPut = 12;
        }

        return timeFormatOutPut;

    }

    // Fully customize Toast view content.
    public void showFullyCustomToast(Activity activity, String toastMessage) {
        View toastView = activity.getLayoutInflater().inflate(R.layout.activity_toast_custom_view, null);
        TextView customToastText = toastView.findViewById(R.id.customToastText);
        // Initiate the Toast instance.
        Toast toast = new Toast(activity);
        // Set custom view clinicplus toast.
        toast.setView(toastView);
        customToastText.setText(toastMessage);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.BOTTOM, 0, 0);
        toast.show();
    }

    public String formatTimeBasedOnPreference(Context context, String inputFormat, String time) {
        String timeFormat;
        String formattedTime = "";
        if (timeFormatPreferences(context) == 12) {
            timeFormat = "hh:mm aa";
        } else {
            timeFormat = "HH:mm";
        }
        formattedTime = changeDateFormat(inputFormat, timeFormat, time);
        return formattedTime;

    }

    public String getFormattedTime(int hr, int min, String format) {
        Time tme = new Time(hr, min, 0);//seconds by default set to zero
        SimpleDateFormat formatter;
        formatter = new SimpleDateFormat(format);
        DateFormatSymbols symbols = new DateFormatSymbols(Locale.US);
        formatter.setDateFormatSymbols(symbols);
        return formatter.format(tme);
    }

    public void showCustomProgressAlertDialog(String title, String progressVal, Activity activity, Dialog dialog) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(title);
        builder.setCancelable(false);
        View customLayout = LayoutInflater.from(activity).inflate(R.layout.custom_progress_bar, null);
        builder.setView(customLayout);
        dialog = builder.create();
        TextView tvBody = customLayout.findViewById(R.id.tv_value);
        tvBody.setText(progressVal);
        dialog.show();
    }

}
