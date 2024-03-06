package com.whitecoats.clinicplus.models

data class InstantPermission(
    val doctor_id: Int,
    val id: Int,
    val instant_day_end: Int,
    val instant_days: Int,
    val instant_end_time: String,
    val instant_permission: Int,
    val instant_start_time: String
)