package com.whitecoats.clinicplus.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import com.whitecoats.clinicplus.MainActivity;
import com.whitecoats.clinicplus.R;

import java.lang.reflect.Field;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AppUtilities {

    public String changeDateFormat(String inputFormat, String outputFormat, String dateStr) {

        String output;
        try {
            DateFormat srcDf = new SimpleDateFormat(inputFormat);
            Date date = srcDf.parse(dateStr);
            DateFormat destDf = new SimpleDateFormat(outputFormat);
            output = destDf.format(date);
        } catch (Exception e) {
            e.printStackTrace();
            output = "";
        }

        return output;
    }

    public static int getId(String resourceName, Class<?> c) {
        try {
            Field idField = c.getDeclaredField(resourceName);
            return idField.getInt(idField);
        } catch (Exception e) {
            throw new RuntimeException("No resource ID found for: "
                    + resourceName + " / " + c, e);
        }
    }

    public static Drawable changeIconColor(Drawable icon, Activity activity, int color) {
        icon.setColorFilter(activity.getResources().getColor(color), PorterDuff.Mode.SRC_IN);
        return icon;
    }

    public static boolean isOnline(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        } else {
            return false;
        }
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

    //Hide Keyboard on the screen
    public void hideSoftKeyboard(Activity activity) {
        View view = activity.getWindow().getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public void selectBottomNavigationScreen(String screenName){
        int navCount = MainActivity.bottomNavigationView.getMenu().size();
        for (int i = 0; i < navCount; i++) {
            CharSequence tile = MainActivity.bottomNavigationView.getMenu().getItem(i).getTitle();
            if (tile.toString().equalsIgnoreCase(screenName)) {
                MainActivity.bottomNavigationView.getMenu().getItem(i).setChecked(true);
                MainActivity.bottomNavigationView.setSelectedItemId(MainActivity.bottomNavigationView.getSelectedItemId());
                break;
            }

        }
    }
    public void showCustomProgressAlertDialog(Context context, Dialog dialog, String msg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(msg);
        builder.setCancelable(false);
        View customLayout = LayoutInflater.from(context).inflate(R.layout.custom_progress_bar, null);
        builder.setView(customLayout);
        dialog = builder.create();
        dialog.show();
    }
}
