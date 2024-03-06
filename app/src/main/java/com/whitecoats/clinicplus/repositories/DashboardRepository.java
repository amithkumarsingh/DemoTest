package com.whitecoats.clinicplus.repositories;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.Volley;
import com.whitecoats.clinicplus.R;
import com.whitecoats.clinicplus.apis.ApiMethodCalls;
import com.whitecoats.clinicplus.apis.ApiUrls;
import com.whitecoats.clinicplus.interfaces.ApiCallbackInterface;
import com.whitecoats.clinicplus.utils.SingleLiveEvent;

import androidx.lifecycle.LiveData;

//<<<<<<< HEAD
import org.json.JSONArray;
//=======
import org.json.JSONException;
//>>>>>>> 752c5d61537c68b7023a2e90faa758e5486ae6bf
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;

public class DashboardRepository {

    private static DashboardRepository instance;
    private ApiMethodCalls apiMethodCalls = new ApiMethodCalls();
    SingleLiveEvent<String> data = new SingleLiveEvent<>();
    private ProgressDialog loadingDialog;
    private JSONObject jsonValue;

    public static DashboardRepository getInstance() {
        if (instance == null) {
            instance = new DashboardRepository();
        }

        return instance;
    }

    public SingleLiveEvent<String> getDashboardStatus(Activity activity) {
        try {
            apiMethodCalls.getApiData(ApiUrls.getDashboardStatus, null, activity, new ApiCallbackInterface() {
                @Override
                public void onSuccessResponse(String response) {
                    data.postValue(response);
                }

                @Override
                public void onErrorResponse(String error) {
                    data.postValue(error);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }

    public SingleLiveEvent<String> getDashboardQuickLink(Activity activity) {
        SingleLiveEvent<String> dashboardQuickLinkResponse = new SingleLiveEvent<>();
        try {
            apiMethodCalls.getApiData(ApiUrls.getDashboardQuickLinks + "?mode=" + 0, null, activity, new ApiCallbackInterface() {
                @Override
                public void onSuccessResponse(String response) {
                    dashboardQuickLinkResponse.postValue(response);
                }

                @Override
                public void onErrorResponse(String error) {
                    dashboardQuickLinkResponse.postValue(error);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dashboardQuickLinkResponse;
    }

    public SingleLiveEvent<String> getDashboardDoctorDetails(Activity activity) {
        SingleLiveEvent<String> doctorDetailsResponse = new SingleLiveEvent<>();
        try {
            apiMethodCalls.getApiData(ApiUrls.getDocDetails_V2 + "?id=" + ApiUrls.doctorId, null, activity, new ApiCallbackInterface() {
                @Override
                public void onSuccessResponse(String response) {
                    doctorDetailsResponse.postValue(response);
                }

                @Override
                public void onErrorResponse(String error) {
                    doctorDetailsResponse.postValue(error);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return doctorDetailsResponse;
    }

    public SingleLiveEvent<String> getImageData(Activity activity, String url) {
        SingleLiveEvent<String> imageResponse = new SingleLiveEvent<>();
        RequestQueue requestQueue = Volley.newRequestQueue(activity);
        ImageRequest request = new ImageRequest(url,
                new Response.Listener<Bitmap>() {
                    @Override
                    public void onResponse(Bitmap bitmap) {
                        // homeDocImg.setImageBitmap(bitmap);
                        JSONObject jsonObject = new JSONObject();
                        try {
                            //Process os success response
                            jsonObject.put("status", 200);
                            jsonObject.put("response", BitMapToString(bitmap));
                            imageResponse.postValue(jsonObject.toString());
                        } catch (Exception e) {
                            e.printStackTrace();
                            //callback.onErrorResponse(e.toString());
                        }
                    }
                }, 0, 0, null,
                new Response.ErrorListener() {
                    public void onErrorResponse(VolleyError error) {
                        JSONObject jsonObject = new JSONObject();
                        try {
                            jsonObject.put("status", 400);
                            jsonObject.put("response", BitMapToString(BitmapFactory.decodeResource(activity.getResources(), R.drawable.ic_profile)));
                            imageResponse.postValue(jsonObject.toString());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

//                        homeDocImg.setColorFilter(getContext().getResources().getColor(R.color.colorWhite));
//                        homeDocImg.setImageResource(R.drawable.ic_profile);
                    }
                });
        // Access the RequestQueue through your singleton class.
        requestQueue.add(request);
        return imageResponse;
    }

    public LiveData<String> getStatus() {
        return data;
    }

    public SingleLiveEvent<String> getDashBoardAppointment(Activity activity, String duration) {
        SingleLiveEvent<String> dashboardAppointmentResponses = new SingleLiveEvent<>();
        try {
            apiMethodCalls.getApiData(ApiUrls.geDashBoardAppointmentDetails + "?duration=" + duration, null, activity, new ApiCallbackInterface() {
                @Override
                public void onSuccessResponse(String response) {
                    dashboardAppointmentResponses.postValue(response);
                }

                @Override
                public void onErrorResponse(String error) {
                    dashboardAppointmentResponses.postValue(error);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dashboardAppointmentResponses;
    }

    public SingleLiveEvent<String> getQuickAction(Activity activity) {
        SingleLiveEvent<String> quickActionResponse = new SingleLiveEvent<>();
        try {
//            apiMethodCalls.getApiData(ApiUrls.getDashboardQuickAction + "?mode=" + 0, null, activity, new ApiCallbackInterface() {
            apiMethodCalls.getApiData(ApiUrls.getDashBoardDetailsSubscriptionPlan, null, activity, new ApiCallbackInterface() {
                @Override
                public void onSuccessResponse(String response) {
                    quickActionResponse.postValue(response);
                }

                @Override
                public void onErrorResponse(String error) {
                    quickActionResponse.postValue(error);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return quickActionResponse;
    }

    public SingleLiveEvent<String> getSharedAndFollowUp(Activity activity) {
        SingleLiveEvent<String> sharedRecordAndFollowUpResponses = new SingleLiveEvent<>();
        try {
            apiMethodCalls.getApiData(ApiUrls.getDashboardSharedRecordAndFollowUp + "?duration=" + "All"+"&limit=5", null, activity, new ApiCallbackInterface() {
                @Override
                public void onSuccessResponse(String response) {
                    sharedRecordAndFollowUpResponses.postValue(response);
                }

                @Override
                public void onErrorResponse(String error) {
                    sharedRecordAndFollowUpResponses.postValue(error);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sharedRecordAndFollowUpResponses;
    }

    public SingleLiveEvent<String> getDoctorDetail(Activity activity, int doctorId) {
        SingleLiveEvent<String> doctorDetailsResponses = new SingleLiveEvent<>();
        try {
            apiMethodCalls.getApiData(ApiUrls.getDoctorDetails + "?id=" + doctorId, null, activity, new ApiCallbackInterface() {
                @Override
                public void onSuccessResponse(String response) {
                    doctorDetailsResponses.postValue(response);
                }

                @Override
                public void onErrorResponse(String error) {
                    doctorDetailsResponses.postValue(error);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return doctorDetailsResponses;
    }

    public SingleLiveEvent<String> getFileUrl(Activity activity, String fileUrl) {
        loadingDialog = new ProgressDialog(activity);
        loadingDialog.setMessage(activity.getResources().getString(R.string.loading_data));
        loadingDialog.setTitle(activity.getResources().getString(R.string.fetching_file));
        loadingDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        loadingDialog.setCancelable(false);
        loadingDialog.show();
        SingleLiveEvent<String> fileUrlResponses = new SingleLiveEvent<>();
        JSONObject url = new JSONObject();
        try {
            url.put("url", fileUrl.trim());
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            apiMethodCalls.postApiData(ApiUrls.getFileFromUrl, url, activity, new ApiCallbackInterface() {
                @Override
                public void onSuccessResponse(String response) {
                    loadingDialog.dismiss();
                    fileUrlResponses.postValue(response);
                }

                @Override
                public void onErrorResponse(String error) {
                    loadingDialog.dismiss();
                    fileUrlResponses.postValue(error);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return fileUrlResponses;
    }

    public SingleLiveEvent<String> sendMessage(Activity activity, String message, int interfaceId, boolean hasInterFace) {
        loadingDialog = new ProgressDialog(activity);
        loadingDialog.setMessage(activity.getResources().getString(R.string.sending_message));
        loadingDialog.setTitle(activity.getResources().getString(R.string.sending_message));
        loadingDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        loadingDialog.setCancelable(false);
        loadingDialog.show();
        SingleLiveEvent<String> sendMessageResponses = new SingleLiveEvent<>();
        JSONObject url = new JSONObject();
        try {
            jsonValue = new JSONObject();
            jsonValue.put("message", message);
            if (interfaceId == 0) {
                jsonValue.put("interfaceId", "");

            } else {
                jsonValue.put("interfaceId", interfaceId);
            }

            jsonValue.put("hasInterface", hasInterFace);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            apiMethodCalls.postApiData(ApiUrls.createMessageDashboard, jsonValue, activity, new ApiCallbackInterface() {
                @Override
                public void onSuccessResponse(String response) {
                    loadingDialog.dismiss();
                    sendMessageResponses.postValue(response);
                }

                @Override
                public void onErrorResponse(String error) {
                    loadingDialog.dismiss();
                    sendMessageResponses.postValue(error);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            loadingDialog.dismiss();
        }
        return sendMessageResponses;
    }

    public String BitMapToString(Bitmap bitmap) {
        String temp="";
        if(bitmap!=null) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
            byte[] b = baos.toByteArray();
             temp = Base64.encodeToString(b, Base64.DEFAULT);
        }
        return temp;

    }

    public SingleLiveEvent<String> sendDelay(Activity activity, JSONArray pendingIdArray, String delayIn, String delayTime) {
        loadingDialog = new ProgressDialog(activity);
        loadingDialog.setMessage(activity.getResources().getString(R.string.wait_while_we_updating));
        loadingDialog.setTitle(activity.getResources().getString(R.string.updating));
        loadingDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        loadingDialog.setCancelable(false);
        loadingDialog.show();
        SingleLiveEvent<String> sendDelayResponses = new SingleLiveEvent<>();
        JSONObject url = new JSONObject();
        try {
            jsonValue = new JSONObject();
            jsonValue.put("appointment_ids", pendingIdArray);
            jsonValue.put("delay_amount", delayTime);
            jsonValue.put("delay_unit", delayIn);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            apiMethodCalls.postApiData(ApiUrls.sendDelayAppointment, jsonValue, activity, new ApiCallbackInterface() {
                @Override
                public void onSuccessResponse(String response) {
                    loadingDialog.dismiss();
                    sendDelayResponses.postValue(response);
                }

                @Override
                public void onErrorResponse(String error) {
                    loadingDialog.dismiss();
                    sendDelayResponses.postValue(error);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            loadingDialog.dismiss();
        }
        return sendDelayResponses;
    }

    public SingleLiveEvent<String> bulkCancel(Activity activity, String duration, String toDate, String fromDate, JSONArray pendingIdArray, int reason, int product_id, String clinicName) {
        loadingDialog = new ProgressDialog(activity);
        loadingDialog.setMessage(activity.getResources().getString(R.string.wait_while_we_updating));
        loadingDialog.setTitle(activity.getResources().getString(R.string.updating));
        loadingDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        loadingDialog.setCancelable(false);
        loadingDialog.show();
        SingleLiveEvent<String> bulkCancelResponses = new SingleLiveEvent<>();
        JSONArray jsonArrayProductId = new JSONArray();
        jsonArrayProductId.put(product_id);

        try {
            jsonValue = new JSONObject();
            jsonValue.put("duration", duration);
            jsonValue.put("toDate", toDate);
            jsonValue.put("fromDate", fromDate);
            jsonValue.put("service_name", clinicName);
            jsonValue.put("product_ids", jsonArrayProductId);
            jsonValue.put("reason", reason);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            apiMethodCalls.postApiData(ApiUrls.bulkAppointmentCancel, jsonValue, activity, new ApiCallbackInterface() {
                @Override
                public void onSuccessResponse(String response) {
                    loadingDialog.dismiss();
                    bulkCancelResponses.postValue(response);
                }

                @Override
                public void onErrorResponse(String error) {
                    loadingDialog.dismiss();
                    bulkCancelResponses.postValue(error);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            loadingDialog.dismiss();
        }
        return bulkCancelResponses;
    }

    public SingleLiveEvent<String> bulkComplete(Activity activity, String duration, String toDate, String fromDate, JSONArray pendingIdArray, boolean isFollowUp, boolean isInVoice, int product_id) {
        loadingDialog = new ProgressDialog(activity);
        loadingDialog.setMessage(activity.getResources().getString(R.string.wait_while_we_updating));
        loadingDialog.setTitle(activity.getResources().getString(R.string.updating));
        loadingDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        loadingDialog.setCancelable(false);
        loadingDialog.show();
        SingleLiveEvent<String> bulkCompleteResponses = new SingleLiveEvent<>();

        JSONArray jsonArrayProductId = new JSONArray();
        jsonArrayProductId.put(product_id);
        try {
            jsonValue = new JSONObject();
            jsonValue.put("duration", duration);
            jsonValue.put("toDate", toDate);
            jsonValue.put("fromDate", fromDate);
            jsonValue.put("product_ids", jsonArrayProductId);
            jsonValue.put("isFollowUp", isFollowUp);
            jsonValue.put("isInvoice", isInVoice);

//            jsonValue.put("reason", reason);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            apiMethodCalls.postApiData(ApiUrls.bulkAppointmentComplete, jsonValue, activity, new ApiCallbackInterface() {
                @Override
                public void onSuccessResponse(String response) {
                    loadingDialog.dismiss();
                    bulkCompleteResponses.postValue(response);
                }

                @Override
                public void onErrorResponse(String error) {
                    loadingDialog.dismiss();
                    bulkCompleteResponses.postValue(error);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            loadingDialog.dismiss();
        }
        return bulkCompleteResponses;
    }

    public SingleLiveEvent<String> getDefaultFollowUpSetting(Activity activity) {
        SingleLiveEvent<String> followUpDefaultSettingResponses = new SingleLiveEvent<>();
        try {
            apiMethodCalls.getApiData(ApiUrls.getFollowUpDefaultSetting, null, activity, new ApiCallbackInterface() {
                @Override
                public void onSuccessResponse(String response) {
                    followUpDefaultSettingResponses.postValue(response);
                }

                @Override
                public void onErrorResponse(String error) {
                    followUpDefaultSettingResponses.postValue(error);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return followUpDefaultSettingResponses;
    }

    public SingleLiveEvent<String> getBookedTraining(Activity activity, final String perPage, String sortBy, String search, final String pageNum,
                                                     final String status, String shortOrder) {
        SingleLiveEvent<String> bookedTrainingResponses = new SingleLiveEvent<>();
        try {
            apiMethodCalls.getApiData(ApiUrls.getDashboardBookedTraining + "?per_page=" + perPage + "&sort_by=" + "booked_at" + "&search=" + search +
                    "&page=" + pageNum + "&status=" + status + "&sort_order=" + "desc", null, activity, new ApiCallbackInterface() {
                @Override
                public void onSuccessResponse(String response) {
                    bookedTrainingResponses.postValue(response);
                }

                @Override
                public void onErrorResponse(String error) {
                    bookedTrainingResponses.postValue(error);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bookedTrainingResponses;
    }

    public SingleLiveEvent<String> geUpcomingTraining(Activity activity, final String perPage, String sortBy, String search, final String pageNum,
                                                      final String status, String shortOrder) {
        SingleLiveEvent<String> upcomingTrainingResponses = new SingleLiveEvent<>();
        try {
            apiMethodCalls.getApiData(ApiUrls.getDashboardUpcomingTraining + "?per_page=" + perPage + "&sort_by=" + "created_at" + "&search=" + search +
                    "&page=" + pageNum + "&status=" + status + "&sort_order=" + "desc", null, activity, new ApiCallbackInterface() {
                @Override
                public void onSuccessResponse(String response) {
                    upcomingTrainingResponses.postValue(response);
                }

                @Override
                public void onErrorResponse(String error) {
                    upcomingTrainingResponses.postValue(error);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return upcomingTrainingResponses;
    }


    public SingleLiveEvent<String> cancelBookedTraining(Activity activity, int id, int trainingScheduleId) {
        loadingDialog = new ProgressDialog(activity);
        loadingDialog.setMessage(activity.getResources().getString(R.string.wait_while_we_updating));
        loadingDialog.setTitle(activity.getResources().getString(R.string.common_please_wait_loading_message));
        loadingDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        loadingDialog.setCancelable(false);
        loadingDialog.show();

        SingleLiveEvent<String> cancelBookedTrainingResponses = new SingleLiveEvent<>();

        try {

            jsonValue = new JSONObject();
            jsonValue.put("id", id);
            jsonValue.put("training_schedule_id", trainingScheduleId);
        } catch (Exception e) {

        }
        try {
            apiMethodCalls.postApiData(ApiUrls.cancelTraining, jsonValue, activity, new ApiCallbackInterface() {
                @Override
                public void onSuccessResponse(String response) {
                    loadingDialog.dismiss();
                    cancelBookedTrainingResponses.postValue(response);
                }

                @Override
                public void onErrorResponse(String error) {
                    loadingDialog.dismiss();
                    cancelBookedTrainingResponses.postValue(error);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            loadingDialog.dismiss();
        }
        return cancelBookedTrainingResponses;
    }

    public SingleLiveEvent<String> sendPaymentReminder(Activity activity, int appointment_id) {
        loadingDialog = new ProgressDialog(activity);
        loadingDialog.setMessage(activity.getResources().getString(R.string.wait_while_we_updating));
        loadingDialog.setTitle(activity.getResources().getString(R.string.common_please_wait_loading_message));
        loadingDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        loadingDialog.setCancelable(false);
        loadingDialog.show();

        SingleLiveEvent<String> sendPaymentReminderResponses = new SingleLiveEvent<>();

        try {

            jsonValue = new JSONObject();
            jsonValue.put("appointment_id", appointment_id);
        } catch (Exception e) {

        }
        try {
            apiMethodCalls.postApiData(ApiUrls.sendPaymentReminder, jsonValue, activity, new ApiCallbackInterface() {
                @Override
                public void onSuccessResponse(String response) {
                    loadingDialog.dismiss();
                    sendPaymentReminderResponses.postValue(response);
                }

                @Override
                public void onErrorResponse(String error) {
                    loadingDialog.dismiss();
                    sendPaymentReminderResponses.postValue(error);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            loadingDialog.dismiss();
        }
        return sendPaymentReminderResponses;
    }

    public SingleLiveEvent<String> getDashboardData(Activity activity) {
        SingleLiveEvent<String> dashbaordRibbonResponses = new SingleLiveEvent<>();
        try {
            apiMethodCalls.getApiData(ApiUrls.getDashboardData, null, activity, new ApiCallbackInterface() {
                @Override
                public void onSuccessResponse(String response) {
                    dashbaordRibbonResponses.postValue(response);
                }

                @Override
                public void onErrorResponse(String error) {
                    dashbaordRibbonResponses.postValue(error);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dashbaordRibbonResponses;
    }
}
