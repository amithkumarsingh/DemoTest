package com.whitecoats.clinicplus.repositories

import com.whitecoats.clinicplus.models.*
import com.whitecoats.clinicplus.utils.Resource
import com.whitecoats.clinicplus.utils.RetrofitClient
import com.whitecoats.clinicplus.utils.RetrofitServiceClient

class SettingScreenRepository {

    suspend fun getAllAppointDatesList(url: String): Resource<AppointDates> {
        return try {
            val retrofitAPI = RetrofitClient.getInstance().create(RetrofitServiceClient::class.java)
            val response = retrofitAPI.getAppointsDates(url)
            if (response.isSuccessful) {
                Resource.Success(response.body())
            } else {
                if (response.errorBody() != null) {
                    Resource.Error(response.errorBody()!!.string())
                } else {
                    Resource.Error(response.message())
                }
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "An error occurred")
        }
    }

    suspend fun getSelectedServiceDatRes(url: String): Resource<ServiceDetailsModelClass> {
        return try {
            val retrofitAPI = RetrofitClient.getInstance().create(RetrofitServiceClient::class.java)
            val response = retrofitAPI.getSelectedServiceData(url)
            if (response.isSuccessful) {
                Resource.Success(response.body())
            } else {
                if (response.errorBody() != null) {
                    Resource.Error(response.errorBody()!!.string())
                } else {
                    Resource.Error(response.message())
                }
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "An error occurred")
        }
    }

    suspend fun isServiceEnableOrDisableRes(body: EnableOrDisableBody): Resource<EnableDisableModel> {
        return try {
            val retrofitAPI = RetrofitClient.getInstance().create(RetrofitServiceClient::class.java)
            val response = retrofitAPI.isServiceEnableOrDisable(body)
            if (response.isSuccessful) {
                Resource.Success(response.body())
            } else {
                if (response.errorBody() != null) {
                    Resource.Error(response.errorBody()!!.string())
                } else {
                    Resource.Error(response.message())
                }
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "An error occurred")
        }
    }
}

