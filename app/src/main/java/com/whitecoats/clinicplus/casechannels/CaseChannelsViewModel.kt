package com.whitecoats.clinicplus.casechannels

import android.app.Activity
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.android.volley.toolbox.Volley
import org.json.JSONObject

class CaseChannelsViewModel(application: Application) : AndroidViewModel(application) {
    private var caseChannelRepository: CaseChannelRepository = CaseChannelRepository()


    fun getCaseDiscussionSummery(url: String): LiveData<String> {
        return caseChannelRepository.getCaseDiscussionSummery(
            getApplication<Application>().applicationContext,
            url
        )
    }

    fun updateTaskStatus(reqBody: JSONObject): LiveData<String> {
        return caseChannelRepository.updateTaskStatus(
            getApplication<Application>().applicationContext,
            reqBody
        )
    }

    fun addNewPost(reqBody: JSONObject): LiveData<String> {
        return caseChannelRepository.addNewPost(
            getApplication<Application>().applicationContext,
            reqBody
        )
    }

    fun getCaseDashboardSummery(url: String): LiveData<String> {
        return caseChannelRepository.getCaseDashboardSummery(
            getApplication<Application>().applicationContext,
            url
        )
    }

    fun commonViewModelCall(
        url: String,
        reqBody: JSONObject, method: Int
    ): LiveData<String> {
        return caseChannelRepository.commonCaseChannelRepositoryCall(
            getApplication<Application>().applicationContext,
            url, method, reqBody
        )
    }

}