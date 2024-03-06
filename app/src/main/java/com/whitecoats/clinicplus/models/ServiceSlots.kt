package com.whitecoats.clinicplus.models

data class ServiceSlots(
    val day: String,
    val end_time: String,
    val id: Int,
    val schedule_id: Int,
    val slot_flag: Int,
    val slot_id: Int,
    val start_time: String
)