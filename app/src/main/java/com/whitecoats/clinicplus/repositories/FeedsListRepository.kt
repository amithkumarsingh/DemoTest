package com.whitecoats.clinicplus.repositories

import android.app.Activity
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.whitecoats.clinicplus.activities.FeedActivity
import com.whitecoats.clinicplus.apis.ApiMethodCalls
import com.whitecoats.clinicplus.apis.ApiUrls
import com.whitecoats.clinicplus.interfaces.ApiCallbackInterface
import com.whitecoats.clinicplus.models.FeedsModel
import org.json.JSONException
import org.json.JSONObject

class FeedsListRepository {
    private val apiMethodCalls = ApiMethodCalls()
    private val feedsModelMutableLiveData = MutableLiveData<FeedsModel?>()
    val feedMediaURL = MutableLiveData<String?>()
    fun feedsList(activity: Activity?, pageNo: Int) {
        try {
            apiMethodCalls.getApiData(
                ApiUrls.getFeedsList + "?page=" + pageNo + "&per_page=10",
                null,
                activity,
                object : ApiCallbackInterface {
                    override fun onSuccessResponse(response: String) {
                        try {
                            Log.d("Data Feeds", response)
                            var resObj = JSONObject(response)
                            resObj = resObj.getJSONObject("response").getJSONObject("response")
                            val gson = Gson()
                            val feedsModel =
                                gson.fromJson(resObj.toString(), FeedsModel::class.java)
                            feedsModel.status = 200
                            feedsModelMutableLiveData.postValue(feedsModel)
                            FeedActivity.pullToRefresh!!.isRefreshing = false
                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }
                    }

                    override fun onErrorResponse(error: String) {
                        Log.d("Network Error Video", error)
                        try {
                            val resObj = JSONObject(error)
                            val gson = Gson()
                            val feedsModel =
                                gson.fromJson(resObj.toString(), FeedsModel::class.java)
                            feedsModelMutableLiveData.postValue(feedsModel)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                })
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun feedMediaURL(activity: Activity?, contentPath: String?) {
        try {
            val param = JSONObject()
            param.put("url", contentPath)
            apiMethodCalls.postApiData(
                ApiUrls.getPresignedURL,
                param,
                activity,
                object : ApiCallbackInterface {
                    override fun onSuccessResponse(response: String) {
                        try {
                            Log.d("Feeds URL", response)
                            val resObj = JSONObject(response)
                            feedMediaURL.postValue(
                                resObj.getJSONObject("response").getString("response")
                            )
                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }
                    }

                    override fun onErrorResponse(error: String) {
                        Log.d("Network Error Video", error)
                    }
                })
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    val feedsList: LiveData<FeedsModel?>
        get() = feedsModelMutableLiveData

   /* fun getFeedMediaURL(): LiveData<String?> {
        return feedMediaURL
    }*/

    fun clearFeedsList() {
        feedsModelMutableLiveData.postValue(null)
    }

   /* fun clearMediaURL() {
        feedMediaURL.postValue(null)
    }*/

    companion object {
        var instance: FeedsListRepository? = null
            get() {
                if (field == null) {
                    field = FeedsListRepository()
                }
                return field
            }
            private set
    }
}