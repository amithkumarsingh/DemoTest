package com.whitecoats.clinicplus.casechannels

import android.app.Activity
import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.whitecoats.clinicplus.apis.ApiMethodCalls
import com.whitecoats.clinicplus.apis.ApiUrls
import com.whitecoats.clinicplus.interfaces.ApiCallbackInterface
import org.json.JSONObject

class CaseChannelRepository {
    var apiMethodCalls: ApiMethodCalls = ApiMethodCalls()


    fun getCaseDiscussionSummery(activity: Context, url: String): MutableLiveData<String> {
        val caseDiscussionSummery = MutableLiveData<String>()
        try {
            apiMethodCalls.getApiDataWithContext(
                url,
                null,
                activity,
                object : ApiCallbackInterface {
                    override fun onSuccessResponse(response: String) {
                        caseDiscussionSummery.postValue(response)
                    }

                    override fun onErrorResponse(error: String) {
                        caseDiscussionSummery.postValue(error)
                    }

                })
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        return caseDiscussionSummery

    }

    fun updateTaskStatus(activity: Context, reqBody: JSONObject): MutableLiveData<String> {
        val updateTaskStatus = MutableLiveData<String>()
        try {
            apiMethodCalls.postApiDataWithContext(
                ApiUrls.updateTaskStatus,
                reqBody,
                activity,
                object : ApiCallbackInterface {
                    override fun onSuccessResponse(response: String) {
                        updateTaskStatus.postValue(response)
                    }

                    override fun onErrorResponse(error: String) {
                        updateTaskStatus.postValue(error)
                    }

                })
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        return updateTaskStatus

    }

    fun addNewPost(activity: Context, reqBody: JSONObject): MutableLiveData<String> {
        val newPost = MutableLiveData<String>()
        try {
            apiMethodCalls.postApiDataWithContext(
                ApiUrls.postNewDiscussion,
                reqBody,
                activity,
                object : ApiCallbackInterface {
                    override fun onSuccessResponse(response: String) {
                        newPost.postValue(response)
                    }

                    override fun onErrorResponse(error: String) {
                        newPost.postValue(error)
                    }

                })
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        return newPost

    }

    fun getCaseDashboardSummery(activity: Context, url: String): MutableLiveData<String> {
        val caseDashboardSummery = MutableLiveData<String>()
        try {
            apiMethodCalls.getApiDataWithContext(
                url,
                null,
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

    fun commonCaseChannelRepositoryCall(
        activity: Context,
        url: String,
        method: Int,
        resBody: JSONObject
    ): MutableLiveData<String> {
        val caseDashboardSummery = MutableLiveData<String>()
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