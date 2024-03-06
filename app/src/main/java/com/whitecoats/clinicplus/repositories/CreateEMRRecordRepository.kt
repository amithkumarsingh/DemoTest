package com.whitecoats.clinicplus.repositories

import com.whitecoats.clinicplus.models.AddMedicineReq
import com.whitecoats.clinicplus.models.AddMedicineRes
import com.whitecoats.clinicplus.utils.Resource
import com.whitecoats.clinicplus.utils.RetrofitClient
import com.whitecoats.clinicplus.utils.RetrofitServiceClient
import com.whitecoats.model.MedicationAutoSuggestion
import com.whitecoats.model.SymptomsAutoSuggestion

class CreateEMRRecordRepository {

    suspend fun getSuggestionsForSymptoms(url: String): Resource<SymptomsAutoSuggestion> {
        return try {
            val retrofitAPIs =
                RetrofitClient.getInstance().create(RetrofitServiceClient::class.java)
            val response = retrofitAPIs.getAutoSuggestionsForSymptoms(url)
            val result = response.body()
            if (response.isSuccessful && result != null) {
                Resource.Success(result)
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

    suspend fun getMedicationAutoSuggestionsList(url: String): Resource<MedicationAutoSuggestion> {
        return try {
            val retrofitAPIs =
                RetrofitClient.getInstance().create(RetrofitServiceClient::class.java)
            val response = retrofitAPIs.getAutoSuggestionsForMedication(url)
            val result = response.body()
            if (response.isSuccessful && result != null) {
                Resource.Success(result)
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
    suspend fun addMedicine(addMedicineReq:AddMedicineReq): Resource<AddMedicineRes> {
        return try {
            val retrofitAPIs =
                RetrofitClient.getInstance().create(RetrofitServiceClient::class.java)
            val response = retrofitAPIs.addMedicineToDatabase(addMedicineReq)
            val result = response.body()
            if (response.isSuccessful && result != null) {
                Resource.Success(result)
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