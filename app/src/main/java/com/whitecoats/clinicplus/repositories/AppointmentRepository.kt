package com.whitecoats.clinicplus.repositories

import android.app.Activity
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.whitecoats.clinicplus.apis.ApiMethodCalls
import com.whitecoats.clinicplus.apis.ApiUrls
import com.whitecoats.clinicplus.interfaces.ApiCallbackInterface
import org.json.JSONException
import org.json.JSONObject

class AppointmentRepository {
    private val apiMethodCalls = ApiMethodCalls()
    private val apptStatus = MutableLiveData<Int?>()
    fun storeMedication(activity: Activity?, params: JSONObject?): MutableLiveData<String> {
        val medicationsResponse = MutableLiveData<String>()
        apiMethodCalls.postApiData(
            ApiUrls.storeMedication,
            params,
            activity,
            object : ApiCallbackInterface {
                override fun onSuccessResponse(response: String) {
                    medicationsResponse.postValue(response)
                }

                override fun onErrorResponse(error: String) {
                    medicationsResponse.postValue(error)
                }
            })
        return medicationsResponse
    }

    fun getRecordStructure(activity: Activity?, recordCategoryId: String): MutableLiveData<String> {
        val recordStructureResponse = MutableLiveData<String>()
        apiMethodCalls.getApiData(
            ApiUrls.getRecordStructure + "?record_category_id=" + recordCategoryId,
            null,
            activity,
            object : ApiCallbackInterface {
                override fun onSuccessResponse(response: String) {
                    recordStructureResponse.postValue(response)
                }

                override fun onErrorResponse(error: String) {
                    recordStructureResponse.postValue(error)
                }
            })
        return recordStructureResponse
    }

    fun addMedicine(activity: Activity?, params: JSONObject?): MutableLiveData<String> {
        val medicineResponse = MutableLiveData<String>()
        apiMethodCalls.postApiData(
            ApiUrls.addMedication,
            params,
            activity,
            object : ApiCallbackInterface {
                override fun onSuccessResponse(response: String) {
                    medicineResponse.postValue(response)
                }

                override fun onErrorResponse(error: String) {
                    medicineResponse.postValue(error)
                }
            })
        return medicineResponse
    }

    fun searchMedicines(activity: Activity?, url: String?): MutableLiveData<String> {
        val medicineResponse = MutableLiveData<String>()
        apiMethodCalls.getApiData(url, null, activity, object : ApiCallbackInterface {
            override fun onSuccessResponse(response: String) {
                medicineResponse.postValue(response)
            }

            override fun onErrorResponse(error: String) {
                medicineResponse.postValue(error)
            }
        })
        return medicineResponse
    }

    fun getServiceDetails(activity: Activity?): MutableLiveData<String> {
        val serviceResponse = MutableLiveData<String>()
        apiMethodCalls.getApiData(
            ApiUrls.serviceDetails,
            null,
            activity,
            object : ApiCallbackInterface {
                override fun onSuccessResponse(response: String) {
                    serviceResponse.postValue(response)
                }

                override fun onErrorResponse(error: String) {
                    serviceResponse.postValue(error)
                }
            })
        return serviceResponse
    }

    fun cancelAppt(activity: Activity?, params: JSONObject?): MutableLiveData<String> {
        val cancelResponse = MutableLiveData<String>()
        apiMethodCalls.postApiData(
            ApiUrls.cancelAppt,
            params,
            activity,
            object : ApiCallbackInterface {
                override fun onSuccessResponse(response: String) {
                    cancelResponse.postValue(response)
                }

                override fun onErrorResponse(error: String) {
                    cancelResponse.postValue(error)
                }
            })
        return cancelResponse
    }

    fun updateDelayIntemation(activity: Activity?, params: JSONObject?): MutableLiveData<String> {
        val dealyResponse = MutableLiveData<String>()
        apiMethodCalls.postApiData(
            ApiUrls.delayAppointment,
            params,
            activity,
            object : ApiCallbackInterface {
                override fun onSuccessResponse(response: String) {
                    dealyResponse.postValue(response)
                }

                override fun onErrorResponse(error: String) {
                    dealyResponse.postValue(error)
                }
            })
        return dealyResponse
    }

    fun cancelAppointmentRefund(activity: Activity?, params: JSONObject?): MutableLiveData<String> {
        val cancelAppointmentRefundResponse = MutableLiveData<String>()
        apiMethodCalls.postApiData(
            ApiUrls.cancelAppointmentRefund,
            params,
            activity,
            object : ApiCallbackInterface {
                override fun onSuccessResponse(response: String) {
                    cancelAppointmentRefundResponse.postValue(response)
                }

                override fun onErrorResponse(error: String) {
                    cancelAppointmentRefundResponse.postValue(error)
                }
            })
        return cancelAppointmentRefundResponse
    }

    fun sendVideoLinkDetails(activity: Activity?, appointmentID: Int): MutableLiveData<String> {
        val sendVideoLinkDetailsResponse = MutableLiveData<String>()
        apiMethodCalls.getApiData(
            ApiUrls.notifyPatientExternalJoinVideo + "?appt_id=" + appointmentID,
            null,
            activity,
            object : ApiCallbackInterface {
                override fun onSuccessResponse(response: String) {
                    sendVideoLinkDetailsResponse.postValue(response)
                }

                override fun onErrorResponse(error: String) {
                    sendVideoLinkDetailsResponse.postValue(error)
                }
            })
        return sendVideoLinkDetailsResponse
    }

    fun getAppointmentRefundAmount(
        activity: Activity?,
        params: JSONObject?,
        orderId: Int,
        cancelReason: Int
    ): MutableLiveData<String> {
        val getAppointmentRefundAmountResponse = MutableLiveData<String>()
        apiMethodCalls.getApiData(
            ApiUrls.getRefundAmount + "?order_id=" + orderId + "&cancel_reason=" + cancelReason,
            null,
            activity,
            object : ApiCallbackInterface {
                override fun onSuccessResponse(response: String) {
                    getAppointmentRefundAmountResponse.postValue(response)
                }

                override fun onErrorResponse(error: String) {
                    getAppointmentRefundAmountResponse.postValue(error)
                }
            })
        return getAppointmentRefundAmountResponse
    }

    fun getAppointmentDetails(activity: Activity?, url: String?): MutableLiveData<String> {
        val appointmentResponse = MutableLiveData<String>()
        apiMethodCalls.getApiData(url, null, activity, object : ApiCallbackInterface {
            override fun onSuccessResponse(response: String) {
                appointmentResponse.postValue(response)
            }

            override fun onErrorResponse(error: String) {
                appointmentResponse.postValue(error)
            }
        })
        return appointmentResponse
    }

    fun setUpVideoLink(activity: Activity?, jsonObject: JSONObject?): MutableLiveData<String> {
        val appointmentResponse = MutableLiveData<String>()
        apiMethodCalls.postApiData(
            ApiUrls.setUpVideoLink,
            jsonObject,
            activity,
            object : ApiCallbackInterface {
                override fun onSuccessResponse(response: String) {
                    appointmentResponse.postValue(response)
                }

                override fun onErrorResponse(error: String) {
                    appointmentResponse.postValue(error)
                }
            })
        return appointmentResponse
    }

    fun getApptStatus(activity: Activity?, apptID: Int) {
        try {
            apiMethodCalls.getApiData(
                ApiUrls.getApptStatus + "?id=" + apptID,
                null,
                activity,
                object : ApiCallbackInterface {
                    override fun onSuccessResponse(response: String) {
                        try {
                            Log.d("Data Video", response)
                            var resObj = JSONObject(response)
                            resObj = resObj.getJSONObject("response")
                            apptStatus.postValue(resObj.getInt("response"))
                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }
                    }

                    override fun onErrorResponse(error: String) {
                        Log.d("Data Error", error)
                        try {
//                        JSONObject errorObj = new JSONObject(error);
//                        Gson gson = new Gson();
//                        VideoCallModel videoCallModel = gson.fromJson(String.valueOf(errorObj), VideoCallModel.class);
//                        videoCallModelMutableLiveData.postValue(videoCallModel);
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                })
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun apptStatus(): LiveData<Int?> {
        return apptStatus
    }

    fun clearStatus() {
        apptStatus.postValue(null)
    }

    companion object {
        @JvmStatic
        var instance: AppointmentRepository? = null
            get() {
                if (field == null) {
                    field = AppointmentRepository()
                }
                return field
            }
            private set
    }
}