package com.whitecoats.clinicplus.utils

import com.whitecoats.clinicplus.models.*
import com.whitecoats.model.MedicationAutoSuggestion
import com.whitecoats.model.SymptomsAutoSuggestion
import retrofit2.Response
import retrofit2.http.*

interface RetrofitServiceClient {
    @GET
    suspend fun getAppointsDates(
        @Url url: String
    ): Response<AppointDates>

    @GET
    suspend fun getSelectedServiceData(
        @Url url: String
    ): Response<ServiceDetailsModelClass>

    @POST("/api/v1/doctors/update-service-status")
    suspend fun isServiceEnableOrDisable(
        @Body body: EnableOrDisableBody
    ): Response<EnableDisableModel>

    @POST("/api/v1/delete-account")
    suspend fun deleteUserAccount(
        @Body accountDeleteRequest: AccountDeleteRequest
    ): Response<AccountDeleteResponse>

    @POST("/api/v1/records/add-medicine")
    suspend fun addMedicineToDatabase(
        @Body addMedicine: AddMedicineReq
    ): Response<AddMedicineRes>


    @GET
    suspend fun getAutoSuggestionsForSymptoms(
            @Url url: String
    ): Response<SymptomsAutoSuggestion>

    @GET
    suspend fun getAutoSuggestionsForMedication(
        @Url url: String
    ): Response<MedicationAutoSuggestion>
}