package com.whitecoats.model

import org.json.JSONObject

class PaymentTransactionModel {
    var patientName: String? = null
    var ph_number: String? = null
    var order_details_date: String? = null
    var amount_paid: String? = null
    var order_id = 0
    var payment_title: String? = null
    var payment_title_color: String? = null
    var sendPaymentNotification = 0

    //<<<<<<< HEAD
    var mode = 0
    var paymentStatus = 0
    var orderPaymentStatus: String? = null
    var orderReceipt: String? = null
    var orderInvoice: String? = null
    var netAmountPaid = 0
    var productId = 0
    var patientPaymentStatus: String? = null
    var refund_status = 0
    var is_refund_processed = 0
    var net_total_paid: String? = null
    var refund_amt: String? = null
    var is_do_auto_refund = 0
    var is_settlement_processed = 0
    var is_settlement_triggered = 0
    var payment_status: String? = null
    var receiptURL: String? = null
    var status = 0
    var invoiceData: JSONObject? = null
    var unmapped_status: String? = null
    var payment_mode: String? = null
    var invoiceUrl: String? = null
    var appointmentId = 0
    var generalID:String?=null
}