package com.whitecoats.model

data class AutoSuggestionResponse(
    val created_at: String,
    val id: Int,
    val status: Int,
    val symptom_name: String,
    val updated_at: String
)