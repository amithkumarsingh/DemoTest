package com.whitecoats.clinicplus.repositories;

import android.app.Activity;
import android.app.ProgressDialog;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.whitecoats.clinicplus.apis.ApiMethodCalls;
import com.whitecoats.clinicplus.apis.ApiUrls;
import com.whitecoats.clinicplus.interfaces.ApiCallbackInterface;

import org.json.JSONObject;

public class CreateNewAccountRepository {
    private static CreateNewAccountRepository instance;
    private ApiMethodCalls apiMethodCalls = new ApiMethodCalls();
    MutableLiveData<String> data = new MutableLiveData<>();
    private ProgressDialog loadingDialog;
    private JSONObject jsonObject;
    MutableLiveData<String> createNewAccountResponse = new MutableLiveData<>();

    public static CreateNewAccountRepository getInstance() {
        if (instance == null) {
            instance = new CreateNewAccountRepository();
        }
        return instance;
    }

    public MutableLiveData<String> createNewAccountResponse(Activity activity, JSONObject jsonObject) {
        try {
            apiMethodCalls.postApiData(ApiUrls.createMerchantURL, jsonObject, activity,
                    new ApiCallbackInterface() {
                        @Override
                        public void onSuccessResponse(String response) {
                            Log.d("CreateMer_Response", response);
                            createNewAccountResponse.postValue(response);
                        }

                        @Override
                        public void onErrorResponse(String error) {
                            Log.d("CreateMer_Error", error);
                            createNewAccountResponse.postValue(error);
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return createNewAccountResponse;
    }


    public MutableLiveData<String> getCategoriesNames(Activity activity, String type, String category_name) {
        MutableLiveData<String> CategoriesResponse = new MutableLiveData<>();
        try {
            apiMethodCalls.getApiData(ApiUrls.getCatFieldDetails + "?type=" + type + "&category_name=" + category_name + "", null, activity, new ApiCallbackInterface() {
                @Override
                public void onSuccessResponse(String response) {
                    CategoriesResponse.postValue(response);
                }

                @Override
                public void onErrorResponse(String error) {
                    CategoriesResponse.postValue(error);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return CategoriesResponse;
    }

    //get otp penny
    public MutableLiveData<String> getOTP(Activity activity, JSONObject jsonObject) {
        MutableLiveData<String> getOTPResponse = new MutableLiveData<>();
        try {
            apiMethodCalls.getApiData(ApiUrls.sendOTP, jsonObject, activity, new ApiCallbackInterface() {
                @Override
                public void onSuccessResponse(String response) {
                    getOTPResponse.postValue(response);
                }

                @Override
                public void onErrorResponse(String error) {
                    getOTPResponse.postValue(error);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return getOTPResponse;
    }

    //get otp bank
    public MutableLiveData<String> getBankOTP(Activity activity, JSONObject jsonObject) {
        MutableLiveData<String> getBankOTPResponse = new MutableLiveData<>();
        try {
            apiMethodCalls.getApiData(ApiUrls.sendOTPBankUpdate, jsonObject, activity, new ApiCallbackInterface() {
                @Override
                public void onSuccessResponse(String response) {
                    getBankOTPResponse.postValue(response);
                }

                @Override
                public void onErrorResponse(String error) {
                    getBankOTPResponse.postValue(error);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return getBankOTPResponse;
    }

    //get pan otp
    public MutableLiveData<String> getPanOTP(Activity activity) {
        MutableLiveData<String> getPanOTPResponse = new MutableLiveData<>();
        try {
            apiMethodCalls.getApiData(ApiUrls.getPanOTP,null, activity, new ApiCallbackInterface() {
                @Override
                public void onSuccessResponse(String response) {
                    getPanOTPResponse.postValue(response);
                }

                @Override
                public void onErrorResponse(String error) {
                    getPanOTPResponse.postValue(error);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return getPanOTPResponse;
    }


    //post pennyVerification
    public MutableLiveData<String> verifyPenny(Activity activity, JSONObject jsonObject) {
        MutableLiveData<String> verifyPennyResponse = new MutableLiveData<>();
        try {
            apiMethodCalls.postApiData(ApiUrls.pennyVerification, jsonObject, activity, new ApiCallbackInterface() {
                @Override
                public void onSuccessResponse(String response) {
                    verifyPennyResponse.postValue(response);
                }

                @Override
                public void onErrorResponse(String error) {
                    verifyPennyResponse.postValue(error);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return verifyPennyResponse;
    }

    //post otp bank
    public MutableLiveData<String> getUpdateBank(Activity activity, JSONObject jsonObject) {
        MutableLiveData<String> getUpdateBankResponse = new MutableLiveData<>();
        try {
            apiMethodCalls.postApiData(ApiUrls.postBankDetails, jsonObject, activity, new ApiCallbackInterface() {
                @Override
                public void onSuccessResponse(String response) {
                    getUpdateBankResponse.postValue(response);
                }

                @Override
                public void onErrorResponse(String error) {
                    getUpdateBankResponse.postValue(error);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return getUpdateBankResponse;
    }

    //post otp PAN
    public MutableLiveData<String> postUpdatePan(Activity activity, JSONObject jsonObject) {
        MutableLiveData<String> postUpdatePanResponse = new MutableLiveData<>();
        try {
            apiMethodCalls.postApiData(ApiUrls.postPanDetails, jsonObject, activity, new ApiCallbackInterface() {
                @Override
                public void onSuccessResponse(String response) {
                    postUpdatePanResponse.postValue(response);
                }

                @Override
                public void onErrorResponse(String error) {
                    postUpdatePanResponse.postValue(error);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return postUpdatePanResponse;
    }

    //get SetUp status
    public MutableLiveData<String> getSetUpStatus(Activity activity) {
        MutableLiveData<String> getSetUpStatusResponse = new MutableLiveData<>();
        try {
            apiMethodCalls.getApiData(ApiUrls.setUpStatus,null, activity, new ApiCallbackInterface() {
                @Override
                public void onSuccessResponse(String response) {
                    getSetUpStatusResponse.postValue(response);
                }

                @Override
                public void onErrorResponse(String error) {
                    getSetUpStatusResponse.postValue(error);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return getSetUpStatusResponse;
    }

    //get verify attempts
    public MutableLiveData<String> getVerifyAttempts(Activity activity) {
        MutableLiveData<String> getVerifyAttemptsResponse = new MutableLiveData<>();
        try {
            apiMethodCalls.getApiData(ApiUrls.getVerifyAttempts,null, activity, new ApiCallbackInterface() {
                @Override
                public void onSuccessResponse(String response) {
                    getVerifyAttemptsResponse.postValue(response);
                }

                @Override
                public void onErrorResponse(String error) {
                    getVerifyAttemptsResponse.postValue(error);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return getVerifyAttemptsResponse;
    }
}
