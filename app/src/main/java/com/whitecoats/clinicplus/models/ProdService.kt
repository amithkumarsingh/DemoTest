package com.whitecoats.clinicplus.models

data class ProdService(
    val active: Int,
    val address: String,
    val advance_notice_in_mins: Int,
    val alias: String,
    val buffer_time: Int,
    val city: String,
    val consultation_time: Int,
    val created_at: String,
    val id: Int,
    val internal_supersaas_id: Int,
    val is_new_supersaas: Int,
    val need_approval: Int,
    val pincode: String,
    val service_id: Int,
    val state: Int,
    val supersaas_id: Int,
    val updated_at: String,
    val user_id: Int
)