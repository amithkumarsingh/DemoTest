package com.whitecoats.clinicplus;

import android.app.Activity;
//<<<<<<< HEAD:app/src/main/java/com/whitecoats/clinicplus/AppointmentApiRequests.java
//=======
import android.app.ProgressDialog;
import android.util.Log;
import android.widget.Toast;
//>>>>>>> 57ff4187a9a21415d752ed46caf16e350bc14a3b:app/src/main/java/com/caregivers/in/AppointmentApiRequests.java

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.whitecoats.clinicplus.apis.ApiUrls;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

//import static org.webrtc.ContextUtils.getApplicationContext;

public class AppointmentApiRequests {

    public void getApptApiData(String url, String parameters, Activity activity, final VolleyCallback callback) {
        JSONObject requestObj = null;

        try {
            if (!parameters.equals("")) {
                requestObj = new JSONObject(parameters);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        RequestQueue requestQueue = Volley.newRequestQueue(activity);
        JsonObjectRequest request_json = new JsonObjectRequest(Request.Method.GET, url, requestObj,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            //Process os success response
                            callback.onSuccess(response.toString());
                        } catch (Exception e) {
                            e.printStackTrace();
//                            Log.d("exception", e.toString());
                            callback.onError(e.toString());
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                VolleyLog.e("Error: ", error.getMessage());
                JSONObject jsonObject = new JSONObject();
                String json = null;
                NetworkResponse response = error.networkResponse;
                if (response != null && response.data != null) {
                    json = new String(response.data);
                    json = trimMessage(json, "message");
                    if (json != null) {
                        try {
                            jsonObject.put("status_code", response.statusCode);
                            jsonObject.put("message", json);
                            callback.onError(jsonObject.toString());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }

            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                headers.put("App-Origin", "8");
                headers.put("Authorization", "Bearer " + ApiUrls.loginToken);
                return headers;
            }
        };

        requestQueue.add(request_json);
        request_json.setRetryPolicy(new DefaultRetryPolicy(
                60000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }

    public void postApptApiData(String url, JSONObject parameters, Activity activity, final VolleyCallback callback) {
        ProgressDialog cancelAppointmentLoadingDialog;
        cancelAppointmentLoadingDialog = new ProgressDialog(activity);
        cancelAppointmentLoadingDialog.setMessage(activity.getString(R.string.process_request));
        cancelAppointmentLoadingDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        cancelAppointmentLoadingDialog.setCancelable(false);
        cancelAppointmentLoadingDialog.show();
        RequestQueue requestQueue = Volley.newRequestQueue(activity);
        JsonObjectRequest request_json = new JsonObjectRequest(Request.Method.POST, url, parameters,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            cancelAppointmentLoadingDialog.dismiss();
                            //Process os success response
//                            Log.d("DashBoardCountRespo", response.toString());
                            callback.onSuccess(response.toString());
                        } catch (Exception e) {
                            e.printStackTrace();
//                            Log.d("exception", e.toString());
                            callback.onError(e.toString());
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                cancelAppointmentLoadingDialog.dismiss();
                VolleyLog.e("Error: ", error.getMessage());
                JSONObject jsonObject = new JSONObject();
                String json = null;
                NetworkResponse response = error.networkResponse;
                if (response != null && response.data != null) {
                    json = new String(response.data);
                    json = trimMessage(json, "message");
                    if (json != null) {
                        try {
                            jsonObject.put("status_code", response.statusCode);
                            jsonObject.put("message", json);
                            callback.onError(jsonObject.toString());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                headers.put("App-Origin", "8");
                headers.put("Authorization", "Bearer " + ApiUrls.loginToken);
                return headers;
            }
        };

        requestQueue.add(request_json);
        request_json.setRetryPolicy(new DefaultRetryPolicy(
                60000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }

    public String trimMessage(String json, String key) {
        String trimmedString = null;

        try {
            JSONObject obj = new JSONObject(json);
            trimmedString = obj.getString(key);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }

        return trimmedString;
    }

    //Somewhere that has access to a context
    public void displayMessage(String toastString, Activity activity) {
        Toast.makeText(activity, toastString, Toast.LENGTH_LONG).show();
    }

}
