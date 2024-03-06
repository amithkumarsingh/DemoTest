package com.whitecoats.clinicplus.apis;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

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
import com.whitecoats.clinicplus.activities.CreateNewAccount;
import com.whitecoats.clinicplus.constants.AppConstants;
import com.whitecoats.clinicplus.interfaces.ApiCallbackInterface;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ApiMethodCalls {

    public void getApiData(String url, JSONObject parameters, Activity activity, final ApiCallbackInterface callback) {

        RequestQueue requestQueue = Volley.newRequestQueue(activity);
        JsonObjectRequest request_json = new JsonObjectRequest(Request.Method.GET, url, parameters,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        JSONObject jsonObject = new JSONObject();
                        try {
                            //Process os success response
                            jsonObject.put("status_code", 200);
                            jsonObject.put("response", response);
                            callback.onSuccessResponse(jsonObject.toString());
                        } catch (Exception e) {
                            e.printStackTrace();
                            callback.onErrorResponse(e.toString());
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
                            callback.onErrorResponse(jsonObject.toString());
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
                headers.put("App-Origin", AppConstants.APP_ORIGIN);
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

    /*Passing ViewModel context instead of Activity's context to avoid the tightly coupling*/
    public void getApiDataWithContext(String url, JSONObject parameters, Context activity, final ApiCallbackInterface callback) {

        RequestQueue requestQueue = Volley.newRequestQueue(activity);
        JsonObjectRequest request_json = new JsonObjectRequest(Request.Method.GET, url, parameters,
                response -> {
                    JSONObject jsonObject = new JSONObject();
                    try {
                        //Process os success response
                        jsonObject.put("status_code", 200);
                        jsonObject.put("response", response);
                        callback.onSuccessResponse(jsonObject.toString());
                    } catch (Exception e) {
                        e.printStackTrace();
                        callback.onErrorResponse(e.toString());
                    }
                }, error -> {

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
                                callback.onErrorResponse(jsonObject.toString());
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                headers.put("App-Origin", AppConstants.APP_ORIGIN);
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

    public void postApiData(String url, JSONObject parameters, Activity activity, final ApiCallbackInterface callback) {

        RequestQueue requestQueue = Volley.newRequestQueue(activity);
        JsonObjectRequest request_json = new JsonObjectRequest(Request.Method.POST, url, parameters,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        JSONObject jsonObject = new JSONObject();
                        try {
                            //Process os success response
                            jsonObject.put("status_code", 200);
                            jsonObject.put("response", response);
                            callback.onSuccessResponse(jsonObject.toString());
                        } catch (Exception e) {
                            e.printStackTrace();
                            callback.onErrorResponse(e.toString());
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("volley error res", error.toString());
                JSONObject jsonObject = new JSONObject();
                String json = null;
                NetworkResponse response = error.networkResponse;
                if (CreateNewAccount.isMerchantCreated == 1) {

                    if (response != null && response.data != null) {
                        json = new String(response.data);
                        json = trimMessage(json, "message");
                        if (json != null) {
                            try {
                                CreateNewAccount.isMerchantCreated = 0;
                                jsonObject.put("status_code", response.statusCode);
                                jsonObject.put("message", json);
                                callback.onErrorResponse(jsonObject.toString());
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                } else if (CreateNewAccount.PanDetails == true) {

                    if (response != null && response.data != null) {
                        json = new String(response.data);
                        json = trimMessage(json, "message");
                        if (json != null) {
                            try {
                                CreateNewAccount.isMerchantCreated = 0;
                                jsonObject.put("status_code", response.statusCode);
                                jsonObject.put("message", json);
                                callback.onErrorResponse(jsonObject.toString());
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                    }
                } else {
                    if (response != null && response.data != null) {

                        json = new String(response.data);
                        json = trimMessage(json, "message");
                        if (json != null) {
                            try {
                                jsonObject.put("status_code", response.statusCode);
                                jsonObject.put("message", json);
                                callback.onErrorResponse(jsonObject.toString());
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                    }
                }
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                headers.put("App-Origin", AppConstants.APP_ORIGIN);
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
    public void postApiDataWithContext(String url, JSONObject parameters, Context activity, final ApiCallbackInterface callback) {

        RequestQueue requestQueue = Volley.newRequestQueue(activity);
        JsonObjectRequest request_json = new JsonObjectRequest(Request.Method.POST, url, parameters,
                response -> {

                    JSONObject jsonObject = new JSONObject();
                    try {
                        //Process os success response
                        jsonObject.put("status_code", 200);
                        jsonObject.put("response", response);
                        callback.onSuccessResponse(jsonObject.toString());
                    } catch (Exception e) {
                        e.printStackTrace();
                        callback.onErrorResponse(e.toString());
                    }
                }, error -> {
                    Log.i("volley error res", error.toString());
                    JSONObject jsonObject = new JSONObject();
                    String json = null;
                    NetworkResponse response = error.networkResponse;
                    if (CreateNewAccount.isMerchantCreated == 1) {

                        if (response != null && response.data != null) {
                            json = new String(response.data);
                            json = trimMessage(json, "message");
                            if (json != null) {
                                try {
                                    CreateNewAccount.isMerchantCreated = 0;
                                    jsonObject.put("status_code", response.statusCode);
                                    jsonObject.put("message", json);
                                    callback.onErrorResponse(jsonObject.toString());
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    } else if (CreateNewAccount.PanDetails == true) {

                        if (response != null && response.data != null) {
                            json = new String(response.data);
                            json = trimMessage(json, "message");
                            if (json != null) {
                                try {
                                    CreateNewAccount.isMerchantCreated = 0;
                                    jsonObject.put("status_code", response.statusCode);
                                    jsonObject.put("message", json);
                                    callback.onErrorResponse(jsonObject.toString());
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }

                        }
                    } else {
                        if (response != null && response.data != null) {

                            json = new String(response.data);
                            json = trimMessage(json, "message");
                            if (json != null) {
                                try {
                                    jsonObject.put("status_code", response.statusCode);
                                    jsonObject.put("message", json);
                                    callback.onErrorResponse(jsonObject.toString());
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
                headers.put("App-Origin", AppConstants.APP_ORIGIN);
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
    public void postAndGetApiDataWithContext(String url,int method, JSONObject parameters, Context activity, final ApiCallbackInterface callback) {

        RequestQueue requestQueue = Volley.newRequestQueue(activity);
        JsonObjectRequest request_json = new JsonObjectRequest(method, url, parameters,
                response -> {

                    JSONObject jsonObject = new JSONObject();
                    try {
                        //Process os success response
                        jsonObject.put("status_code", 200);
                        jsonObject.put("response", response);
                        callback.onSuccessResponse(jsonObject.toString());
                    } catch (Exception e) {
                        e.printStackTrace();
                        callback.onErrorResponse(e.toString());
                    }
                }, error -> {
                    Log.i("volley error res", error.toString());
                    JSONObject jsonObject = new JSONObject();
                    String json = null;
                    NetworkResponse response = error.networkResponse;
                    if (CreateNewAccount.isMerchantCreated == 1) {

                        if (response != null && response.data != null) {
                            json = new String(response.data);
                            json = trimMessage(json, "message");
                            if (json != null) {
                                try {
                                    CreateNewAccount.isMerchantCreated = 0;
                                    jsonObject.put("status_code", response.statusCode);
                                    jsonObject.put("message", json);
                                    callback.onErrorResponse(jsonObject.toString());
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    } else if (CreateNewAccount.PanDetails == true) {

                        if (response != null && response.data != null) {
                            json = new String(response.data);
                            json = trimMessage(json, "message");
                            if (json != null) {
                                try {
                                    CreateNewAccount.isMerchantCreated = 0;
                                    jsonObject.put("status_code", response.statusCode);
                                    jsonObject.put("message", json);
                                    callback.onErrorResponse(jsonObject.toString());
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }

                        }
                    } else {
                        if (response != null && response.data != null) {

                            json = new String(response.data);
                            json = trimMessage(json, "message");
                            if (json != null) {
                                try {
                                    jsonObject.put("status_code", response.statusCode);
                                    jsonObject.put("message", json);
                                    callback.onErrorResponse(jsonObject.toString());
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
                headers.put("App-Origin", AppConstants.APP_ORIGIN);
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
}
