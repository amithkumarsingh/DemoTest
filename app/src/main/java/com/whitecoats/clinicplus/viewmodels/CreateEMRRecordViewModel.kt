package com.whitecoats.clinicplus.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.whitecoats.clinicplus.models.AddMedicineReq
import com.whitecoats.clinicplus.repositories.CreateEMRRecordRepository
import com.whitecoats.clinicplus.utils.APIResponse
import com.whitecoats.clinicplus.utils.Resource
import com.whitecoats.clinicplus.utils.SingleLiveEvent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CreateEMRRecordViewModel : ViewModel() {

    private val _autoSuggestionsResponseLiveData = SingleLiveEvent<APIResponse<Any>>()
    val autoSuggestionsLiveData: LiveData<APIResponse<Any>> = _autoSuggestionsResponseLiveData

    private val medicationAutoSuggestions = SingleLiveEvent<APIResponse<Any>>()
    val medicationAutoSuggestionsLiveData: LiveData<APIResponse<Any>> = medicationAutoSuggestions

    private val addMedicineToDatabase = SingleLiveEvent<APIResponse<Any>>()
    val addMedicineLiveData: LiveData<APIResponse<Any>> = addMedicineToDatabase


    private val repository: CreateEMRRecordRepository by lazy {
        CreateEMRRecordRepository()
    }

    fun getAutoSuggestionsForSymptoms(url: String) {
        viewModelScope.launch(Dispatchers.IO) {
            when (val response = repository.getSuggestionsForSymptoms(url)) {
                is Resource.Success -> {
                    _autoSuggestionsResponseLiveData.postValue(APIResponse(response.data!!))
                }
                is Resource.Error -> {
                    _autoSuggestionsResponseLiveData.postValue(APIResponse(response.message!!))
                }
            }
        }
    }

    fun getMedicationAutoSuggestions(url: String) {
        viewModelScope.launch(Dispatchers.IO) {
            when (val response = repository.getMedicationAutoSuggestionsList(url)) {
                is Resource.Success -> {
                    medicationAutoSuggestions.postValue(APIResponse(response.data!!))
                }
                is Resource.Error -> {
                    medicationAutoSuggestions.postValue(APIResponse(response.message!!))
                }
            }
        }
    }

    fun addMedicineToDatabase(addMedicineReq: AddMedicineReq) {
        viewModelScope.launch(Dispatchers.IO) {
            when (val response = repository.addMedicine(addMedicineReq)) {
                is Resource.Success -> {
                    addMedicineToDatabase.postValue(APIResponse(response.data!!))
                }
                is Resource.Error -> {
                    addMedicineToDatabase.postValue(APIResponse(response.message!!))
                }
            }
        }
    }
}