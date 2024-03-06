package com.whitecoats.clinicplus.fragments

import android.annotation.SuppressLint
import android.app.Dialog
import android.graphics.PorterDuff
import android.view.View
import android.widget.*
import androidx.core.content.ContextCompat
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.whitecoats.clinicplus.R
import com.whitecoats.clinicplus.interfaces.PaymentTransactionFilterInterface
import com.whitecoats.model.AddPatientModel

class TransactionsFilterBottomSheet : BottomSheetDialogFragment() {
    private var transactionsFragment: TransactionsFragment? = null
    private lateinit var cancelIcon: ImageView
    var addInterfaceModelList: List<AddPatientModel>? = null
    private var paymentTransactionFilterInterface: PaymentTransactionFilterInterface? = null
    private lateinit var applyFilter: TextView
    private lateinit var clearFilter: TextView
    private var modeConsultFilter = "0"
    private var paymentModeFilter = "All"
    private var paymentStatusFilter = "All"
    private var apptStatusFilter = "All"
    private var pendingPatientFilter = 0
    private var receivedPatientFilter = 0
    private var pendingSettlementFilter = 0
    private var partialSettlePendingFilter = 0
    private var settlementDoneFilter = 0
    private var partialSettlementDoneFilter = 0
    private var scheduledRefundFilter = 0
    private var pendingRefundFilter = 0
    private var refundCompletedFilter = 0
    private lateinit var video: CheckBox
    private lateinit var clinic: CheckBox
    private lateinit var chat: CheckBox
    private lateinit var paymentModeGroup: RadioGroup
    private lateinit var apptStatusGroup: RadioGroup
    private lateinit var radioButton_paymentMode_all: RadioButton
    private lateinit var radioButton_online: RadioButton
    private lateinit var radioButton_offline: RadioButton
    private lateinit var radioButton_apptStatus_all: RadioButton
    private lateinit var radioButton_active: RadioButton
    private lateinit var radioButton_completed: RadioButton
    private lateinit var paymentStatusAll: CheckBox
    private lateinit var cbPaymentCompleted: CheckBox
    private lateinit var cbSettlementPending: CheckBox
    private lateinit var cbRefundSchedule: CheckBox
    private lateinit var cbRefundCompleted: CheckBox
    private lateinit var cbPartialRefundPending: CheckBox
    private lateinit var cbPartialRefundCompleted: CheckBox
    private lateinit var cbPaymentPending: CheckBox
    private lateinit var cdSettlementCompleted: CheckBox
    private lateinit var cbRefundPending: CheckBox
    private var modePaymentAll = 0
    private var modePaymentOnline = 0
    private var modePaymentOffline = 0
    private var pendingPatientAll = 0
    private var paymentCompleted = 0
    private var settlementPending = 0
    private var refundSchedule = 0
    private var refundCompleted = 0
    private var partialRefundPending = 0
    private var partialRefundCompleted = 0
    private var paymentPending = 0
    private var settlementCompleted = 0
    private var refundPending = 0
    private var apptStatusAll = 0
    private var statusActive = 0
    private var statusCompleted = 0
    private var checkBoxList = HashMap<String, Boolean>()
    private var isDeselected = false
    private var clearFilterClicked = false
    private var count = 0
    fun setupBottomSheet(
        modeOfConsultFilter: String,
        paymentModeFilter: String,
        paymentStatusFilter: String,
        pendingPatientFilter: Int,
        receivedPatientFilter: Int,
        pendingSettlementFilter: Int,
        partialSettlePendingFilter: Int,
        settlementDoneFilter: Int,
        partialSettlementDoneFilter: Int,
        scheduledRefundFilter: Int,
        pendingRefundFilter: Int,
        refundCompletedFilter: Int,
        ApptStatusFilter: String,
        paymentTransactionFilterInterface: PaymentTransactionFilterInterface?
    ) {
        this.paymentTransactionFilterInterface = paymentTransactionFilterInterface
        modeConsultFilter = modeOfConsultFilter
        this.paymentModeFilter = paymentModeFilter
        this.paymentStatusFilter = paymentStatusFilter
        this.pendingPatientFilter = pendingPatientFilter
        this.receivedPatientFilter = receivedPatientFilter
        this.pendingSettlementFilter = pendingSettlementFilter
        this.partialSettlePendingFilter = partialSettlePendingFilter
        this.settlementDoneFilter = settlementDoneFilter
        this.partialSettlementDoneFilter = partialSettlementDoneFilter
        this.scheduledRefundFilter = scheduledRefundFilter
        this.pendingRefundFilter = pendingRefundFilter
        this.refundCompletedFilter = refundCompletedFilter
        apptStatusFilter = ApptStatusFilter
    }

    fun setupConfig(paymentTransactionsFragment: TransactionsFragment?) {
        transactionsFragment = paymentTransactionsFragment
        addInterfaceModelList = ArrayList()
    }

    @SuppressLint("RestrictedApi")
    override fun setupDialog(dialog: Dialog, style: Int) {
        //Get the content View
        val contentView = View.inflate(context, R.layout.transaction_filter_layout, null)
        dialog.setContentView(contentView)
        clearFilter = contentView.findViewById(R.id.payment_clear_filter)
        applyFilter = contentView.findViewById(R.id.payment_apply_filter)
        paymentModeGroup = contentView.findViewById(R.id.type_of_payment_radio)
        apptStatusGroup = contentView.findViewById(R.id.type_of_payment_apptStatus)
        video = contentView.findViewById(R.id.video_checkbox)
        clinic = contentView.findViewById(R.id.clinic_checkbox)
        chat = contentView.findViewById(R.id.chat_checkbox)
        radioButton_paymentMode_all = contentView.findViewById(R.id.mode_type_all)
        radioButton_online = contentView.findViewById(R.id.consult_type_online)
        radioButton_offline = contentView.findViewById(R.id.consult_type_offline)
        paymentStatusAll = contentView.findViewById(R.id.payment_status_all)
        cbPaymentCompleted = contentView.findViewById(R.id.payment_status_receivedPatient)
        cbSettlementPending = contentView.findViewById(R.id.payment_status_parSettledPending)
        cbRefundSchedule = contentView.findViewById(R.id.payment_status_partial_settlement)
        cbRefundCompleted = contentView.findViewById(R.id.payment_status_pendingRefund)
        cbPartialRefundPending = contentView.findViewById(R.id.payment_status_pendingPatient)
        cbPartialRefundCompleted = contentView.findViewById(R.id.payment_status_pendingSettled)
        cbPaymentPending = contentView.findViewById(R.id.payment_status_settlement_done)
        cdSettlementCompleted = contentView.findViewById(R.id.payment_status_schdRefund)
        cbRefundPending = contentView.findViewById(R.id.payment_status_refundComplete)
        radioButton_apptStatus_all = contentView.findViewById(R.id.payment_appt_status_all)
        radioButton_active = contentView.findViewById(R.id.payment_appt_status_active)
        radioButton_completed = contentView.findViewById(R.id.payment_appt_status_completed)
        cancelIcon = contentView.findViewById(R.id.close_transfilter_sheet)
        dialog.setCancelable(false)
        dialog.setCanceledOnTouchOutside(true)
        cancelIcon.setColorFilter(
            ContextCompat.getColor(requireContext(), R.color.colorWhite),
            PorterDuff.Mode.SRC_IN
        )
        cancelIcon.setOnClickListener { dialog.dismiss() }
        paymentStatusAll.setOnCheckedChangeListener { compoundButton, b ->
            if (b) {
                cbPaymentCompleted.isChecked = true
                cbSettlementPending.isChecked = true
                cbRefundSchedule.isChecked = true
                cbRefundCompleted.isChecked = true
                cbPartialRefundPending.isChecked = true
                cbPartialRefundCompleted.isChecked = true
                cbPaymentPending.isChecked = true
                cdSettlementCompleted.isChecked = true
                cbRefundPending.isChecked = true
            } else {
                cbPaymentCompleted.isChecked = false
                cbSettlementPending.isChecked = false
                cbRefundSchedule.isChecked = false
                cbRefundCompleted.isChecked = false
                cbPartialRefundPending.isChecked = false
                cbPartialRefundCompleted.isChecked = false
                cbPaymentPending.isChecked = false
                cdSettlementCompleted.isChecked = false
                cbRefundPending.isChecked = false
            }
        }
        setFilter(
            modeConsultFilter,
            paymentModeFilter,
            paymentStatusFilter,
            pendingPatientFilter,
            receivedPatientFilter,
            pendingSettlementFilter,
            partialSettlePendingFilter,
            settlementDoneFilter,
            partialSettlementDoneFilter,
            scheduledRefundFilter,
            pendingRefundFilter,
            refundCompletedFilter,
            apptStatusFilter
        )
        paymentModeGroup.setOnCheckedChangeListener { group, checkedId ->
            if (radioButton_paymentMode_all.isChecked) {
                paymentModeFilter = "All"
            } else if (radioButton_online.isChecked) {
                paymentModeFilter = "Online"
            } else if (radioButton_offline.isChecked) {
                paymentModeFilter = "Offline"
            }
        }
        apptStatusGroup.setOnCheckedChangeListener { group, checkedId ->
            if (radioButton_apptStatus_all.isChecked) {
                paymentModeFilter = "All"
            } else if (radioButton_active.isChecked) {
                paymentModeFilter = "Active"
            } else if (radioButton_completed.isChecked) {
                paymentModeFilter = "Completed"
            }
        }
        applyFilter.setOnClickListener {
            count = 0
            clearFilterClicked = false
            checkBoxList.clear()
            pendingPatientAll = 0
            paymentCompleted = 0
            settlementPending = 0
            refundSchedule = 0
            refundCompleted = 0
            partialRefundPending = 0
            partialRefundCompleted = 0
            paymentPending = 0
            settlementCompleted = 0
            refundPending = 0
            if (video.isChecked && clinic.isChecked && chat.isChecked) {
                modeConsultFilter = "video,clinic,chat"
                count += 3
            } else if (video.isChecked && clinic.isChecked) {
                modeConsultFilter = "video,clinic"
                count += 2
            } else if (video.isChecked && chat.isChecked) {
                modeConsultFilter = "video,chat"
                count += 2
            } else if (clinic.isChecked && chat.isChecked) {
                modeConsultFilter = "clinic,chat"
                count += 2
            } else if (video.isChecked) {
                modeConsultFilter = "video"
                count++
            } else if (clinic.isChecked) {
                modeConsultFilter = "clinic"
                count++
            } else if (chat.isChecked) {
                modeConsultFilter = "chat"
                count++
            }
            if (radioButton_paymentMode_all.isChecked) {
                paymentModeFilter = "All"
                count++
            } else if (radioButton_online.isChecked) {
                paymentModeFilter = "Online"
                count++
            } else if (radioButton_offline.isChecked) {
                paymentModeFilter = "Offline"
                count++
            }
            if (radioButton_apptStatus_all.isChecked) {
                apptStatusFilter = "All"
                count++
            } else if (radioButton_active.isChecked) {
                apptStatusFilter = "Active"
                count++
            } else if (radioButton_completed.isChecked) {
                apptStatusFilter = "Completed"
                count++
            }
            if (radioButton_paymentMode_all.isChecked) {
                modePaymentAll = 1
            }
            if (radioButton_online.isChecked) {
                modePaymentOnline = 1
            }
            if (radioButton_offline.isChecked) {
                modePaymentOffline = 1
            }
            if (paymentStatusAll.isChecked) {
                pendingPatientAll = 1
                checkBoxList["All"] = true
                count++
            } else {
                pendingPatientAll = 0
                checkBoxList["All"] = false
            }
            if (cbPaymentCompleted.isChecked) {
                paymentCompleted = 1
                checkBoxList["cbPaymentCompleted"] = true
                count++
            } else {
                paymentCompleted = 0
                checkBoxList["cbPaymentCompleted"] = false
            }
            if (cbSettlementPending.isChecked) {
                settlementPending = 1
                checkBoxList["cbSettlementPending"] = true
                count++
            } else {
                settlementPending = 0
                checkBoxList["cbSettlementPending"] = false
            }
            if (cbRefundSchedule.isChecked) {
                refundSchedule = 1
                checkBoxList["cbRefundSchedule"] = true
                count++
            } else {
                refundSchedule = 0
                checkBoxList["cbRefundSchedule"] = false
            }
            if (cbRefundCompleted.isChecked) {
                refundCompleted = 1
                checkBoxList["cbRefundCompleted"] = true
                count++
            } else {
                refundCompleted = 0
                checkBoxList["cbRefundCompleted"] = false
            }
            if (cbPartialRefundPending.isChecked) {
                partialRefundPending = 1
                checkBoxList["cbPartialRefundPending"] = true
                count++
            } else {
                partialRefundPending = 0
                checkBoxList["cbPartialRefundPending"] = false
            }
            if (cbPartialRefundCompleted.isChecked) {
                partialRefundCompleted = 1
                checkBoxList["cbPartialRefundCompleted"] = true
                count++
            } else {
                partialRefundCompleted = 0
                checkBoxList["cbPartialRefundCompleted"] = false
            }
            if (cbPaymentPending.isChecked) {
                paymentPending = 1
                checkBoxList["cbPaymentPending"] = true
                count++
            } else {
                paymentPending = 0
                checkBoxList["cbPaymentPending"] = false
            }
            if (cdSettlementCompleted.isChecked) {
                settlementCompleted = 1
                checkBoxList["cdSettlementCompleted"] = true
                count++
            } else {
                settlementCompleted = 0
                checkBoxList["cdSettlementCompleted"] = false
            }
            if (cbRefundPending.isChecked) {
                refundPending = 1
                checkBoxList["cbRefundPending"] = true
                count++
            } else {
                refundPending = 0
                checkBoxList["cbRefundPending"] = false
            }
            if (radioButton_apptStatus_all.isChecked) {
                apptStatusAll = 1
            }
            if (radioButton_active.isChecked) {
                statusActive = 1
            }
            if (radioButton_completed.isChecked) {
                statusCompleted = 1
            }
            dialog.dismiss()
            paymentTransactionFilterInterface!!.applyFilter(
                modeConsultFilter,
                paymentModeFilter,
                paymentStatusFilter,
                apptStatusFilter,
                modePaymentAll,
                modePaymentOnline,
                modePaymentOffline,
                pendingPatientAll,
                paymentCompleted,
                settlementPending,
                refundSchedule,
                refundCompleted,
                partialRefundPending,
                partialRefundCompleted,
                paymentPending,
                settlementCompleted,
                refundPending,
                apptStatusAll,
                statusActive,
                statusCompleted,
                count,
                clearFilterClicked
            )
        }
        clearFilter.setOnClickListener {
            resetFilter()
            clearFilterClicked = true
            paymentTransactionFilterInterface!!.applyFilter(
                modeConsultFilter,
                paymentModeFilter,
                paymentStatusFilter,
                apptStatusFilter,
                modePaymentAll,
                modePaymentOnline,
                modePaymentOffline,
                pendingPatientAll,
                paymentCompleted,
                settlementPending,
                refundSchedule,
                refundCompleted,
                partialRefundPending,
                partialRefundCompleted,
                paymentPending,
                settlementCompleted,
                refundPending,
                apptStatusAll,
                statusActive,
                statusCompleted,
                count,
                clearFilterClicked
            )
        }
    }

    private fun setFilter(
        modeConsult: String,
        modePayment: String,
        statusPayment: String,
        pendingPatientFilter: Int,
        receivedPatientFilter: Int,
        pendingSettlementFilter: Int,
        partialSettlePendingFilter: Int,
        settlementDoneFilter: Int,
        partialSettlementDoneFilter: Int,
        scheduledRefundFilter: Int,
        pendingRefundFilter: Int,
        refundCompletedFilter: Int,
        apptStatus: String
    ) {
        when (modeConsult) {
            "Clinic" -> {
                clinic.isChecked = true
                video.isChecked = false
                chat.isChecked = false
            }
            "video" -> {
                video.isChecked = true
                clinic.isChecked = false
                chat.isChecked = false
            }
            "chat" -> {
                chat.isChecked = true
                clinic.isChecked = false
                video.isChecked = false
            }
            "clinic,chat" -> {
                chat.isChecked = true
                clinic.isChecked = true
                video.isChecked = false
            }
            "video,chat" -> {
                chat.isChecked = true
                clinic.isChecked = false
                video.isChecked = true
            }
            "video,clinic" -> {
                chat.isChecked = false
                clinic.isChecked = true
                video.isChecked = true
            }
            "video,clinic,chat" -> {
                chat.isChecked = true
                clinic.isChecked = true
                video.isChecked = true
            }
        }
        when (modePayment) {
            "All" -> radioButton_paymentMode_all.isChecked = true
            "Online" -> radioButton_online.isChecked = true
            "Offline" -> radioButton_offline.isChecked = true
        }
        if (statusPayment.equals("All", ignoreCase = true)) {
            cbPaymentCompleted.isChecked = true
            cbSettlementPending.isChecked = true
            cbRefundSchedule.isChecked = true
            cbRefundCompleted.isChecked = true
            cbPartialRefundPending.isChecked = true
            cbPartialRefundCompleted.isChecked = true
            cbPaymentPending.isChecked = true
            cdSettlementCompleted.isChecked = true
            cbRefundPending.isChecked = true
        }
        if (receivedPatientFilter == 1) {
            cbPaymentCompleted.isChecked = true
        }
        if (pendingSettlementFilter == 1) {
            cbSettlementPending.isChecked = true
        }
        if (scheduledRefundFilter == 1) {
            cbRefundSchedule.isChecked = true
        }
        if (refundCompletedFilter == 1) {
            cbRefundCompleted.isChecked = true
        }
        if (partialSettlePendingFilter == 1) {
            cbPartialRefundPending.isChecked = true
        }
        if (partialSettlementDoneFilter == 1) {
            cbPartialRefundCompleted.isChecked = true
        }
        if (pendingPatientFilter == 1) {
            cbPaymentPending.isChecked = true
        }
        if (settlementDoneFilter == 1) {
            cdSettlementCompleted.isChecked = true
        }
        if (pendingRefundFilter == 1) {
            cbRefundPending.isChecked = true
        }
        when (apptStatus) {
            "All" -> radioButton_apptStatus_all.isChecked = true
            "Active" -> radioButton_active.isChecked = true
            "Completed" -> radioButton_completed.isChecked = true
        }
        for ((selectedCheckBoxName, isSelected) in checkBoxList) {
            if (selectedCheckBoxName.equals("cbPaymentCompleted", ignoreCase = true)) {
                cbPaymentCompleted.isChecked = isSelected
            } else if (selectedCheckBoxName.equals("cbSettlementPending", ignoreCase = true)) {
                cbSettlementPending.isChecked = isSelected
            } else if (selectedCheckBoxName.equals("All", ignoreCase = true)) {
                paymentStatusAll.isChecked = isSelected
            } else if (selectedCheckBoxName.equals("cbRefundSchedule", ignoreCase = true)) {
                cbRefundSchedule.isChecked = isSelected
            } else if (selectedCheckBoxName.equals("cbRefundCompleted", ignoreCase = true)) {
                cbRefundCompleted.isChecked = isSelected
            } else if (selectedCheckBoxName.equals("cbPartialRefundPending", ignoreCase = true)) {
                cbPartialRefundPending.isChecked = isSelected
            } else if (selectedCheckBoxName.equals("cbPartialRefundCompleted", ignoreCase = true)) {
                cbPartialRefundCompleted.isChecked = isSelected
            } else if (selectedCheckBoxName.equals("cbPaymentPending", ignoreCase = true)) {
                cbPaymentPending.isChecked = isSelected
            } else if (selectedCheckBoxName.equals("cdSettlementCompleted", ignoreCase = true)) {
                cdSettlementCompleted.isChecked = isSelected
            } else if (selectedCheckBoxName.equals("cbRefundPending", ignoreCase = true)) {
                cbRefundPending.isChecked = isSelected
            } else {
                paymentStatusAll.isChecked = true
            }
        }
    }

    private fun resetFilter() {
        isDeselected = false
        video.isChecked = true
        clinic.isChecked = true
        chat.isChecked = true
        radioButton_paymentMode_all.isChecked = true
        radioButton_online.isChecked = false
        radioButton_offline.isChecked = false
        paymentStatusAll.isChecked = true
        cbPaymentCompleted.isChecked = true
        cbSettlementPending.isChecked = true
        cbRefundSchedule.isChecked = true
        cbRefundCompleted.isChecked = true
        cbPartialRefundPending.isChecked = true
        cbPartialRefundCompleted.isChecked = true
        cbPaymentPending.isChecked = true
        cdSettlementCompleted.isChecked = true
        cbRefundPending.isChecked = true
        radioButton_apptStatus_all.isChecked = true
        radioButton_active.isChecked = false
        radioButton_completed.isChecked = false
        checkBoxList.clear()
        count = 15
    }
}