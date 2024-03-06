package com.whitecoats.clinicplus.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.whitecoats.clinicplus.repositories.CommonRepository
import org.json.JSONObject

class CommonViewModel (application: Application) : AndroidViewModel(application) {
    private var commonRepository:CommonRepository= CommonRepository()
    fun commonViewModelCall(
        url: String,
        reqBody: JSONObject, method: Int
    ): LiveData<String> {
        return commonRepository.commonCaseChannelRepositoryCall(
            getApplication<Application>().applicationContext,
            url, method, reqBody
        )
    }
}