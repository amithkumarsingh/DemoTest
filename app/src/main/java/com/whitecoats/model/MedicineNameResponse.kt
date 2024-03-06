package com.whitecoats.model

data class MedicineNameResponse(
    val id: Int, val name: String,
    val company: String, val added_by: Int,
    val created_at: String,
    val updated_at: String
)
