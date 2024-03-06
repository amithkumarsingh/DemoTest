package com.whitecoats.clinicplus.repositories

import android.content.Context
import com.whitecoats.clinicplus.apis.ApiMethodCalls
import com.whitecoats.clinicplus.interfaces.ApiCallbackInterface
import com.whitecoats.clinicplus.utils.SingleLiveEvent
import org.json.JSONObject

class CommonRepository {
    var apiMethodCalls: ApiMethodCalls = ApiMethodCalls()
    fun commonCaseChannelRepositoryCall(
        activity: Context,
        url: String,
        method: Int,
        resBody: JSONObject
    ): SingleLiveEvent<String> {
        val caseDashboardSummery = SingleLiveEvent<String>()
        try {
            apiMethodCalls.postAndGetApiDataWithContext(
                url,
                method,
                resBody,
                activity,
                object : ApiCallbackInterface {
                    override fun onSuccessResponse(response: String) {
                        caseDashboardSummery.postValue(response)
                    }

                    override fun onErrorResponse(error: String) {
                        caseDashboardSummery.postValue(error)
                    }

                })
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        return caseDashboardSummery

    }
}