package com.whitecoats.clinicplus;

import android.app.Activity;
import android.app.ProgressDialog;

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

public class PatientRecordsApi {

    private ProgressDialog loadingDialog;

    public void getRecordPref(String url, String parameters, Activity activity, final VolleyCallback callback) {
        JSONObject requestObj = null;
        try {
            if(!parameters.equals("")) {
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
                            callback.onSuccess(response.toString());
                        } catch (Exception e) {
                            e.printStackTrace();
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
//                callback.onError(error.toString());
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
    }

    public void postRecords(String url, JSONObject parameters, Activity activity, final VolleyCallback callback) {

        loadingDialog = new ProgressDialog(activity);
        loadingDialog.setMessage(activity.getResources().getString(R.string.process_request));
        loadingDialog.setCancelable(false);
        loadingDialog.setInverseBackgroundForced(false);

        RequestQueue requestQueue = Volley.newRequestQueue(activity);
        JsonObjectRequest request_json = new JsonObjectRequest(Request.Method.POST, url, parameters,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            callback.onSuccess(response.toString());
                            loadingDialog.dismiss();
                        } catch (Exception e) {
                            e.printStackTrace();
                            callback.onError(e.toString());
                            loadingDialog.dismiss();
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
                loadingDialog.dismiss();
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
        request_json.setRetryPolicy(new DefaultRetryPolicy(
                60000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(request_json);

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


}
