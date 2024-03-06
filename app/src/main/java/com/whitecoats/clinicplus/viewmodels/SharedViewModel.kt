package com.whitecoats.clinicplus.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SharedViewModel : ViewModel() {

    val message = MutableLiveData<String>()

    // function to send error response
    fun sendErrorResponse(text: String) {
        message.value = text
    }
}