package com.whitecoats.clinicplus.repositories;

import android.app.Activity;
import android.app.ProgressDialog;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.whitecoats.clinicplus.R;
import com.whitecoats.clinicplus.apis.ApiMethodCalls;
import com.whitecoats.clinicplus.apis.ApiUrls;
import com.whitecoats.clinicplus.interfaces.ApiCallbackInterface;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by Mohammad suhail ahmed on 24-07-2020.
 */
public class AppointmentDetailsRepository {

    private static AppointmentDetailsRepository appointmentDetailsRepository;
    private ProgressDialog loadingDialog;
    private ApiMethodCalls apiMethodCalls = new ApiMethodCalls();

    public static AppointmentDetailsRepository getInstance() {
        if (appointmentDetailsRepository == null) {
            appointmentDetailsRepository = new AppointmentDetailsRepository();
        }

        return appointmentDetailsRepository;
    }

    public MutableLiveData<String> getCheckInStatus(Activity activity, String startTime) {
        MutableLiveData<String> checkInstatusResponse = new MutableLiveData<>();
        String url = ApiUrls.checkInStatus + "?start_time=" + startTime;
        apiMethodCalls.getApiData(url, null, activity, new ApiCallbackInterface() {
            @Override
            public void onSuccessResponse(String response) {
                checkInstatusResponse.postValue(response);
            }

            @Override
            public void onErrorResponse(String error) {
                checkInstatusResponse.postValue(error);

            }
        });
        return checkInstatusResponse;
    }

    public MutableLiveData<String> getProductTax(Activity activity, int product_id) {
        MutableLiveData<String> getProductTaxResponse = new MutableLiveData<>();
        String url = ApiUrls.getProductTaxes + "?product_id=" + product_id;
        apiMethodCalls.getApiData(url, null, activity, new ApiCallbackInterface() {
            @Override
            public void onSuccessResponse(String response) {
                getProductTaxResponse.postValue(response);
            }

            @Override
            public void onErrorResponse(String error) {
                getProductTaxResponse.postValue(error);

            }
        });
        return getProductTaxResponse;
    }

    public MutableLiveData<String> saveWrittenNotes(Activity activity, int createdBy, int encounterId, int episodeId, String fileUrl, int patientId) {
        MutableLiveData<String> saveWrittenNotesResponse = new MutableLiveData<>();
        try {
            JSONObject postData = new JSONObject();
            postData.put("created_by", createdBy);
            postData.put("description", "");
            postData.put("encounter_id", encounterId);
            postData.put("episode_id", episodeId);
            postData.put("file_url", fileUrl);
            postData.put("has_med_prescription", "");
            postData.put("has_test_prescription", "");
            postData.put("med_prescription_valid_till", "");
            postData.put("patient_id", patientId);
            apiMethodCalls.postApiData(ApiUrls.saveWrittenNotesNewAppt, postData, activity, new ApiCallbackInterface() {
                @Override
                public void onSuccessResponse(String response) {
                    saveWrittenNotesResponse.postValue(response);
                }

                @Override
                public void onErrorResponse(String error) {
                    saveWrittenNotesResponse.postValue(error);

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return saveWrittenNotesResponse;
    }

    public MutableLiveData<String> updateMedicine(Activity activity, JSONObject params) {
        MutableLiveData<String> medicineResponse = new MutableLiveData<>();
        apiMethodCalls.postApiData(ApiUrls.updateMedicineStatus, params, activity, new ApiCallbackInterface() {
            @Override
            public void onSuccessResponse(String response) {
                medicineResponse.postValue(response);
            }

            @Override
            public void onErrorResponse(String error) {
                medicineResponse.postValue(error);
            }
        });
        return medicineResponse;
    }

    public MutableLiveData<String> addProcedure(Activity activity, JSONObject params) {
        MutableLiveData<String> addProcedureResponse = new MutableLiveData<>();
        apiMethodCalls.postApiData(ApiUrls.addProcedure, params, activity, new ApiCallbackInterface() {
            @Override
            public void onSuccessResponse(String response) {
                addProcedureResponse.postValue(response);
            }

            @Override
            public void onErrorResponse(String error) {
                addProcedureResponse.postValue(error);
            }
        });
        return addProcedureResponse;
    }

    public MutableLiveData<String> createInvoice(Activity activity, JSONObject params) {
        loadingDialog = new ProgressDialog(activity);
        loadingDialog.setMessage("Generating invoice");
        loadingDialog.setTitle(activity.getResources().getString(R.string.please_wait));
        loadingDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        loadingDialog.setCancelable(false);
        loadingDialog.show();
        MutableLiveData<String> createInvoiceResponse = new MutableLiveData<>();
        apiMethodCalls.postApiData(ApiUrls.createInvoice, params, activity, new ApiCallbackInterface() {
            @Override
            public void onSuccessResponse(String response) {
                loadingDialog.dismiss();
                createInvoiceResponse.postValue(response);
            }

            @Override
            public void onErrorResponse(String error) {
                loadingDialog.dismiss();
                createInvoiceResponse.postValue(error);
            }
        });
        return createInvoiceResponse;
    }

    public MutableLiveData<String> sendFollowUp(Activity activity, JSONObject params) {
        MutableLiveData<String> sendFollowUpResponse = new MutableLiveData<>();
        apiMethodCalls.postApiData(ApiUrls.sendFollowUp, params, activity, new ApiCallbackInterface() {
            @Override
            public void onSuccessResponse(String response) {
                sendFollowUpResponse.postValue(response);
            }

            @Override
            public void onErrorResponse(String error) {
                sendFollowUpResponse.postValue(error);
            }
        });
        return sendFollowUpResponse;
    }

    public MutableLiveData<String> getProcedures(Activity activity, String search, String productId, String sortOrder, String sortBy, int perPage, int page) {
        MutableLiveData<String> procedureResponse = new MutableLiveData<>();
        String url = ApiUrls.getProcedure + "?page=" + page + "&per_page=" + perPage + "&product_id=" + productId + "&search=" + search + "&sortby=" + sortBy + "&sortorder=" + sortOrder;
        Log.i("procedure url", url);
        apiMethodCalls.getApiData(url, null, activity, new ApiCallbackInterface() {
            @Override
            public void onSuccessResponse(String response) {
                procedureResponse.postValue(response);
            }

            @Override
            public void onErrorResponse(String error) {
                procedureResponse.postValue(error);
            }
        });
        return procedureResponse;
    }

    public MutableLiveData<String> getCapturedNotesListRecords(Activity activity, int appointmentId) {
        MutableLiveData<String> captureNotesRecordsResponse = new MutableLiveData<>();
        String url = ApiUrls.getCaptureNotesRecordsList + "?appointment_id=" + appointmentId;
        try {
            apiMethodCalls.getApiData(url, null, activity, new ApiCallbackInterface() {
                @Override
                public void onSuccessResponse(String response) {
                    captureNotesRecordsResponse.postValue(response);
                }

                @Override
                public void onErrorResponse(String error) {
                    captureNotesRecordsResponse.postValue(error);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return captureNotesRecordsResponse;
    }

    public MutableLiveData<String> getAppointmentServices(Activity activity, int orderID) {
        MutableLiveData<String> getAppointmentServicesResponse = new MutableLiveData<>();
        String url = ApiUrls.getAppointmentServicesData + "?appointment_id=" + orderID;
        try {
            apiMethodCalls.getApiData(url, null, activity, new ApiCallbackInterface() {
                @Override
                public void onSuccessResponse(String response) {
                    getAppointmentServicesResponse.postValue(response);
                }

                @Override
                public void onErrorResponse(String error) {
                    getAppointmentServicesResponse.postValue(error);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return getAppointmentServicesResponse;
    }

    public MutableLiveData<String> getOrderDetails(Activity activity, int orderID) {
        MutableLiveData<String> getOrderDetailsResponse = new MutableLiveData<>();
        String url = ApiUrls.getOrderDetailsData + "?order_id=" + orderID;
        JSONObject jsonObject=new JSONObject();
        try {
        jsonObject.put("order_id",orderID);
        apiMethodCalls.getApiData(url, jsonObject, activity, new ApiCallbackInterface() {
                @Override
                public void onSuccessResponse(String response) {
                    getOrderDetailsResponse.postValue(response);
                }

                @Override
                public void onErrorResponse(String error) {
                    getOrderDetailsResponse.postValue(error);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return getOrderDetailsResponse;
    }

    public MutableLiveData<String> getInvoiceDetails(Activity activity, int orderID, JSONArray changeArray) {
        MutableLiveData<String> getInvoiceDetailsResponse = new MutableLiveData<>();

        try {

            JSONObject jsonData = new JSONObject();
            jsonData.put("order_id", orderID);
            jsonData.put("change_array", changeArray);
            apiMethodCalls.postApiData(ApiUrls.getInvoiceDetailsData, jsonData, activity, new ApiCallbackInterface() {
                @Override
                public void onSuccessResponse(String response) {
                    getInvoiceDetailsResponse.postValue(response);
                }

                @Override
                public void onErrorResponse(String error) {
                    getInvoiceDetailsResponse.postValue(error);

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return getInvoiceDetailsResponse;
    }


    public MutableLiveData<String> getPaymentTimeline(Activity activity, int appointmentId) {
        loadingDialog = new ProgressDialog(activity);
        loadingDialog.setMessage(activity.getResources().getString(R.string.loading_data));
        loadingDialog.setTitle(activity.getResources().getString(R.string.please_wait));
        loadingDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        loadingDialog.setCancelable(false);
        loadingDialog.show();
        MutableLiveData<String> getPaymentTimelineResponse = new MutableLiveData<>();
        String url = ApiUrls.getPaymentTimeLineData + "?appointment_id=" + appointmentId;
        try {
            apiMethodCalls.getApiData(url, null, activity, new ApiCallbackInterface() {
                @Override
                public void onSuccessResponse(String response) {
                    loadingDialog.dismiss();
                    getPaymentTimelineResponse.postValue(response);
                }

                @Override
                public void onErrorResponse(String error) {
                    loadingDialog.dismiss();
                    getPaymentTimelineResponse.postValue(error);
                }
            });
        } catch (Exception e) {
            loadingDialog.dismiss();
            e.printStackTrace();
        }
        return getPaymentTimelineResponse;
    }


    public MutableLiveData<String> saveNewCaptureAppointmentNote(Activity activity, int encounterId, int episodeId, int patientId, String appointmentNotes) {
        loadingDialog = new ProgressDialog(activity);
        loadingDialog.setMessage(activity.getResources().getString(R.string.loading_data));
        loadingDialog.setTitle(activity.getResources().getString(R.string.please_wait));
        loadingDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        loadingDialog.setCancelable(false);
        loadingDialog.show();
        MutableLiveData<String> saveCaptureAppointmentNotesResponse = new MutableLiveData<>();
        JSONObject params = new JSONObject();
        try {
            params.put("encounter_id", encounterId);
            params.put("episode_id", episodeId);
            params.put("patient_id", patientId);
            params.put("appointment_notes", appointmentNotes);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            apiMethodCalls.postApiData(ApiUrls.saveNewCaptureAppointmentNotes, params, activity, new ApiCallbackInterface() {
                @Override
                public void onSuccessResponse(String response) {
                    loadingDialog.dismiss();
                    saveCaptureAppointmentNotesResponse.postValue(response);
                }

                @Override
                public void onErrorResponse(String error) {
                    saveCaptureAppointmentNotesResponse.postValue(error);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return saveCaptureAppointmentNotesResponse;
    }

    public MutableLiveData<String> updateCaptureAppointmentNote(Activity activity, int appointmentNoteId, String appointmentNotes) {
        loadingDialog = new ProgressDialog(activity);
        loadingDialog.setMessage(activity.getResources().getString(R.string.loading_data));
        loadingDialog.setTitle(activity.getResources().getString(R.string.please_wait));
        loadingDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        loadingDialog.setCancelable(false);
        loadingDialog.show();
        MutableLiveData<String> updateCaptureAppointmentNotesResponse = new MutableLiveData<>();
        JSONObject params = new JSONObject();
        try {
            params.put("appointment_notes_id", appointmentNoteId);
            params.put("appointment_notes", appointmentNotes);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            apiMethodCalls.postApiData(ApiUrls.updateCaptureAppointmentNotes, params, activity, new ApiCallbackInterface() {
                @Override
                public void onSuccessResponse(String response) {
                    loadingDialog.dismiss();
                    updateCaptureAppointmentNotesResponse.postValue(response);
                }

                @Override
                public void onErrorResponse(String error) {
                    updateCaptureAppointmentNotesResponse.postValue(error);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return updateCaptureAppointmentNotesResponse;
    }


    public MutableLiveData<String> deleteCaptureAppointmentNote(Activity activity, JSONArray appointmentNoteArrayId) {
        loadingDialog = new ProgressDialog(activity);
        loadingDialog.setMessage(activity.getResources().getString(R.string.loading_data));
        loadingDialog.setTitle(activity.getResources().getString(R.string.please_wait));
        loadingDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        loadingDialog.setCancelable(false);
        loadingDialog.show();
        MutableLiveData<String> deleteCaptureAppointmentNotesResponse = new MutableLiveData<>();
        JSONObject params = new JSONObject();
        try {
            params.put("appointment_notes_id", appointmentNoteArrayId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            apiMethodCalls.postApiData(ApiUrls.deleteCaptureAppointmentNotes, params, activity, new ApiCallbackInterface() {
                @Override
                public void onSuccessResponse(String response) {
                    loadingDialog.dismiss();
                    deleteCaptureAppointmentNotesResponse.postValue(response);
                }

                @Override
                public void onErrorResponse(String error) {
                    deleteCaptureAppointmentNotesResponse.postValue(error);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return deleteCaptureAppointmentNotesResponse;
    }

    public MutableLiveData<String> saveCheckInStatus(Activity activity, int appointmentId, int statusId) {
        loadingDialog = new ProgressDialog(activity);
        loadingDialog.setMessage(activity.getResources().getString(R.string.loading_data));
        loadingDialog.setTitle(activity.getResources().getString(R.string.please_wait));
        loadingDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        loadingDialog.setCancelable(false);
        loadingDialog.show();
        MutableLiveData<String> saveCheckedIntNotesResponse = new MutableLiveData<>();
        JSONObject params = new JSONObject();
        try {
            params.put("appt_id", appointmentId);
            params.put("status_id", statusId);

        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            apiMethodCalls.postApiData(ApiUrls.saveCheckedInStatus, params, activity, new ApiCallbackInterface() {
                @Override
                public void onSuccessResponse(String response) {
                    loadingDialog.dismiss();
                    saveCheckedIntNotesResponse.postValue(response);
                }

                @Override
                public void onErrorResponse(String error) {
                    saveCheckedIntNotesResponse.postValue(error);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return saveCheckedIntNotesResponse;
    }


}
