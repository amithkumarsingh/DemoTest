package com.whitecoats.clinicplus.interfaces;

public interface ApiCallbackInterface {
    void onSuccessResponse(String response);
    void onErrorResponse(String error);
}
