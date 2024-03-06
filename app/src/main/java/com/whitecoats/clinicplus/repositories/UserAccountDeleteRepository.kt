package com.whitecoats.clinicplus.repositories

import com.whitecoats.clinicplus.apis.ApiUrls
import com.whitecoats.clinicplus.models.AccountDeleteRequest
import com.whitecoats.clinicplus.models.AccountDeleteResponse
import com.whitecoats.clinicplus.utils.Resource
import com.whitecoats.clinicplus.utils.RetrofitClient
import com.whitecoats.clinicplus.utils.RetrofitServiceClient

class UserAccountDeleteRepository {

    suspend fun requestForAccountDelete(deleteRequest: AccountDeleteRequest):Resource<AccountDeleteResponse>{
        return try {
            var retrofitAPIs= RetrofitClient.getInstance().create(RetrofitServiceClient::class.java)
            val response = retrofitAPIs.deleteUserAccount(deleteRequest)
            val result=response.body()
            if(response.isSuccessful && result!=null){
                Resource.Success(result)
            }else{
                if(response.errorBody()!=null){
                    Resource.Error(response.errorBody()!!.string())
                    /*val gson = GsonBuilder().create()
                    try {
                        var pojo = gson.fromJson(
                                response.errorBody()!!.string(),
                                APIError::class.java)
                        Resource.Error(pojo.message)
                    } catch (e: IOException) {
                        Resource.Error(e.message?:"An error occured")
                    }*/
                }else {
                    Resource.Error(response.message())
                }
            }
        }catch (e: Exception){
            Resource.Error(e.message?:"An error occurred")
        }
    }
}