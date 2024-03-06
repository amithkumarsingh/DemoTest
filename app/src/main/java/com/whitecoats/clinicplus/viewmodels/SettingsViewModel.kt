package com.whitecoats.clinicplus.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.whitecoats.clinicplus.models.*
import com.whitecoats.clinicplus.repositories.SettingScreenRepository
import com.whitecoats.clinicplus.utils.Resource
import com.whitecoats.clinicplus.utils.SingleLiveEvent
import com.whitecoats.clinicplus.utils.UtilsResource
import kotlinx.coroutines.*


class SettingsViewModel : ViewModel() {

    val appointDatesList = SingleLiveEvent<UtilsResource<AppointDates>>()

    val serviceDetails = SingleLiveEvent<UtilsResource<ServiceDetailsModelClass>>()

    val status = SingleLiveEvent<UtilsResource<EnableDisableModel>>()


    private val settingScreenRepository: SettingScreenRepository by lazy {
        SettingScreenRepository()
    }

    fun getAppointmentsDatesList(url: String) {
        viewModelScope.launch(Dispatchers.IO) {
            when (val response = settingScreenRepository.getAllAppointDatesList(url)) {
                is Resource.Success -> {
                    appointDatesList.postValue(UtilsResource.success(response.data!!))
                }
                is Resource.Error -> {
                    appointDatesList.postValue(UtilsResource.error(response.message))
                }
            }
        }

    }


    fun isServiceEnableOrDisable(body: EnableOrDisableBody) {
        viewModelScope.launch(Dispatchers.IO) {
            when (val res = settingScreenRepository.isServiceEnableOrDisableRes(body)) {
                is Resource.Success -> {
                    status.postValue(UtilsResource.success(res.data!!))
                }
                is Resource.Error -> {
                    /*    val gson = GsonBuilder().create()
                        val test = res.message;
                        Log.i("test", test.toString());

                        val apiError = gson.fromJson(
                            res.message,
                            APIError::class.java
                        )*/
                    status.postValue(UtilsResource.error(res.message))
                }
            }
        }
    }

    fun getSelectedServicesDetails(url: String) {
        viewModelScope.launch(Dispatchers.IO) {
            when (val res = settingScreenRepository.getSelectedServiceDatRes(url)) {
                is Resource.Success -> {
                    serviceDetails.postValue(UtilsResource.success(res.data!!))
                }
                is Resource.Error -> {
                    serviceDetails.postValue(UtilsResource.error(res.message))
                }
            }
        }
    }
}

