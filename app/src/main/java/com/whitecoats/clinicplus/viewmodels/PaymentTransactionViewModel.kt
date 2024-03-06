package com.whitecoats.clinicplus.viewmodels

import android.app.Activity
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.whitecoats.clinicplus.apis.ApiUrls
import com.whitecoats.clinicplus.repositories.PaymentTransactionRepository

class PaymentTransactionViewModel : ViewModel() {
    private var paymentTransactionRepository: PaymentTransactionRepository? = null
    fun init() {
        paymentTransactionRepository = PaymentTransactionRepository.instance
    }

    fun getPaymentOverviewDetailsDetails(activity: Activity?, url: String?): LiveData<String> {
        return paymentTransactionRepository!!.gePaymentOverview(activity, url)
    }

    fun getTransactionsDetails(
        activity: Activity?,
        page: Int,
        pageNumber: Int,
        search: String,
        modeOfConsultFilter: String,
        pendingPatientAllFilter: Int,
        pendingPatientFilter: Int,
        receivedPatientFilter: Int,
        pendingSettlementFilter: Int,
        partialSettlePendingFilter: Int,
        settlementDoneFilter: Int,
        partialSettlementDoneFilter: Int,
        scheduledRefundFilter: Int,
        pendingRefundFilter: Int,
        refundCompletedFilter: Int,
        modePaymentAllFilter: String,
        apptStatusAllFilter: String,
        dateFilterSelection: String,
        selectedFromDate: String,
        selectedToDate: String,
        sortby: String,
        sortorder: String
    ): MutableLiveData<String> {
        val url: String = if (dateFilterSelection.equals("Specific", ignoreCase = true)) {
            ApiUrls.payment_transaction_getList + "?per_page=" + page + "&page=" + pageNumber + "&search=" + search + "&mode=" + modeOfConsultFilter +
                    "&payment_is_all=" + pendingPatientAllFilter + "&payment_is_pending=" + pendingPatientFilter + "&payment_is_received=" + receivedPatientFilter + "&payment_pendingsettlement=" + pendingSettlementFilter + "&payment_partialrefundpending=" + partialSettlePendingFilter + "&payment_completesettlement=" + settlementDoneFilter + "&payment_partialrefunddone=" + partialSettlementDoneFilter +
                    "&payment_schedulerefund=" + scheduledRefundFilter + "&payment_pendingrefund=" + pendingRefundFilter + "&payment_completerefund=" + refundCompletedFilter + "&payment=" + modePaymentAllFilter +
                    "&status=" + apptStatusAllFilter + "&date_filter=" + dateFilterSelection + "&fromDate=" + selectedFromDate + "&toDate=" + selectedToDate + "&sortby=" + sortby + "&sortorder=" + sortorder
        } else {
            ApiUrls.payment_transaction_getList + "?per_page=" + page + "&page=" + pageNumber + "&search=" + search + "&mode=" + modeOfConsultFilter +
                    "&payment_is_all=" + pendingPatientAllFilter + "&payment_is_pending=" + pendingPatientFilter + "&payment_is_received=" + receivedPatientFilter + "&payment_pendingsettlement=" + pendingSettlementFilter + "&payment_partialrefundpending=" + partialSettlePendingFilter + "&payment_completesettlement=" + settlementDoneFilter + "&payment_partialrefunddone=" + partialSettlementDoneFilter +
                    "&payment_schedulerefund=" + scheduledRefundFilter + "&payment_pendingrefund=" + pendingRefundFilter + "&payment_completerefund=" + refundCompletedFilter + "&payment=" + modePaymentAllFilter +
                    "&status=" + apptStatusAllFilter + "&date_filter=" + dateFilterSelection + "&sortby=" + sortby + "&sortorder=" + sortorder
        }
        Log.i("url", url)
        return paymentTransactionRepository!!.getTransactionResponse(activity, url)
    }
}