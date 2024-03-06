package com.whitecoats.model

class AppointmentSlotSpinnerModel {
    var appointmentServiceID = 0
    var appointmentServiceName: String? = null
    var appointmentServiceAlias: String? = null
    var orderByAlphaBet: String? = null
    var productId = 0
    var price = 0
    var pivotId = 0
    var internalSuperSaasId = 0
    var address: String? = null
    override fun toString(): String {
        return appointmentServiceAlias!!
    }
}