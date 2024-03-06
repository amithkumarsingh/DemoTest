package com.whitecoats.clinicplus.interfaces;

/**
 * Created by tejaswiniy on 25-01-2016.
 */
public interface OnReceiveResponse {

    void onSuccessResponse(String successResponse);
    void onErrorResponse(String errorResponse);

}
