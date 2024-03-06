package com.whitecoats.clinicplus.interfaces

interface PaymentTransactionFilterInterface {
    fun applyFilter(
        modeOfConsultFilter: String?,
        paymentModeFilter: String?,
        paymentStatusFilter: String?,
        ApptStatusFilter: String?,
        modePaymentAll: Int,
        modePaymentOnline: Int,
        modePaymentOffline: Int,
        pendingPatientAll: Int,
        pendingPatient: Int,
        receivedPatient: Int,
        pendingSettlement: Int,
        partialSettlementPending: Int,
        settlementDone: Int,
        partialSettlementDone: Int,
        scheduledRefund: Int,
        pendingRefund: Int,
        refundCompleted: Int,
        apptStatusAll: Int,
        statusActive: Int,
        statusCompleted: Int,
        count: Int,
        clearFilterClicked: Boolean
    )
}