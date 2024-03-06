package com.whitecoats.clinicplus.models

data class APIError(
    val message: String,
    val status_code: Int
)