package com.whitecoats.clinicplus.apis;

import android.content.Context;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.whitecoats.clinicplus.VolleyCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ApiGetPostMethodCalls {

    public void volleyApiRequestData(String url, int reqMethod, JSONObject body, Context activity, final VolleyCallback callback) {
        RequestQueue requestQueue = Volley.newRequestQueue(activity);
        JsonObjectRequest request_json = new JsonObjectRequest(reqMethod, url, body,
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
                Log.i("",url);
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
