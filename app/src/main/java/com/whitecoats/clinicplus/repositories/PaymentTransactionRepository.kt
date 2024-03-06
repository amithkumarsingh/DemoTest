package com.whitecoats.clinicplus.repositories

import android.app.Activity
import androidx.lifecycle.MutableLiveData
import com.whitecoats.clinicplus.apis.ApiMethodCalls
import com.whitecoats.clinicplus.interfaces.ApiCallbackInterface

class PaymentTransactionRepository {
    private val apiMethodCalls = ApiMethodCalls()
    var data = MutableLiveData<String>()
    //get transaction Data
    fun getTransactionResponse(activity: Activity?, url: String?): MutableLiveData<String> {
        val getListResponse = MutableLiveData<String>()
        try {
            apiMethodCalls.getApiData(url, null, activity, object : ApiCallbackInterface {
                override fun onSuccessResponse(response: String) {
                    getListResponse.postValue(response)
                }

                override fun onErrorResponse(error: String) {
                    getListResponse.postValue(error)
                }
            })
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return getListResponse
    }

    //get transaction Data
    fun gePaymentOverview(activity: Activity?, url: String?): MutableLiveData<String> {
        val gePaymentOverviewResponse = MutableLiveData<String>()
        try {
            apiMethodCalls.getApiData(url, null, activity, object : ApiCallbackInterface {
                override fun onSuccessResponse(response: String) {
                    gePaymentOverviewResponse.postValue(response)
                }

                override fun onErrorResponse(error: String) {
                    gePaymentOverviewResponse.postValue(error)
                }
            })
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return gePaymentOverviewResponse
    }

    companion object {
        var instance: PaymentTransactionRepository? = null
            get() {
                if (field == null) {
                    field = PaymentTransactionRepository()
                }
                return field
            }
            private set
    }
}