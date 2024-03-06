package com.whitecoats.clinicplus.fragments

import android.app.Dialog
import android.view.View
import android.widget.*
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.whitecoats.clinicplus.R
import com.whitecoats.clinicplus.interfaces.AppointmentFilterInterface


class AppointmentFilterBottomSheeet : BottomSheetDialogFragment() {
    private lateinit var closeFilterSheet: ImageButton
    private lateinit var appointmentFilterInterface: AppointmentFilterInterface
    private lateinit var applyFilter: TextView
    private lateinit var clearFilter: TextView
    var durationFilter = "All"
    private var modeFilter = "All"
    var consultFilter = "0"
    var activePastFilter = "active"
    var checkInStat = ""
    private var payment = "All"
    var video: CheckBox? = null
     var clinic: CheckBox?=null
    private lateinit var consultGroup: RadioGroup
    private lateinit var attendanceGroup: RadioGroup
    private lateinit var paymentGroup: RadioGroup
    private lateinit var consultAll: RadioButton
    private lateinit var consultRoutine: RadioButton
    private lateinit var consultVaccination: RadioButton
    private lateinit var consultAssesment: RadioButton
    private lateinit var consultFirstVisit: RadioButton
    private lateinit var consultFollowUp: RadioButton
    private lateinit var consultPlaster: RadioButton
    private lateinit var consultOthers: RadioButton
    private lateinit var attendanceNone: RadioButton
    private lateinit var attendanceCheckup: RadioButton
    private lateinit var attendanceCheckout: RadioButton
    private lateinit var attendanceCheckin: RadioButton
    private lateinit var attendanceInConsult: RadioButton
    private lateinit var paymentAll: RadioButton
    private lateinit var paymentPending: RadioButton
    private lateinit var payment_received: RadioButton
    private lateinit var payment_pending_settlement: RadioButton
    private lateinit var payment_complete_settlement: RadioButton
    private lateinit var payment_pending_refund: RadioButton
    private lateinit var payment_complete_refund: RadioButton
    private lateinit var payment_refund_scheduled: RadioButton
    private lateinit var payment_partial_refund_pending: RadioButton
    private lateinit var payment_partial_refund_completed: RadioButton
    fun setupBottomSheet(
        durationFilter: String,
        modeFilter: String,
        consultFilter: String,
        activePastFilter: String,
        checkInStatus: String,
        payment: String,
        appointmentFilterInterface: AppointmentFilterInterface,
    ) {
        this.appointmentFilterInterface = appointmentFilterInterface
        this.durationFilter = durationFilter
        this.consultFilter = consultFilter
        this.modeFilter = modeFilter
        this.activePastFilter = activePastFilter
        checkInStat = checkInStatus
        this.payment = payment
    }

    fun setupBottomSheet(
        durationFilter: String,
        modeFilter: String,
        consultFilter: String,
        activePastFilter: String,
        checkInStatus: String,
        payment: String,
    ) {
        this.appointmentFilterInterface = appointmentFilterInterface
        this.durationFilter = durationFilter
        this.consultFilter = consultFilter
        this.modeFilter = modeFilter
        this.activePastFilter = activePastFilter
        checkInStat = checkInStatus
        this.payment = payment
    }

    override fun setupDialog(dialog: Dialog, style: Int) {
        val contentView = View.inflate(context, R.layout.appointment_filter_layout, null)
        dialog.setContentView(contentView)
        closeFilterSheet = contentView.findViewById(R.id.close_filter_sheet)
        applyFilter = contentView.findViewById(R.id.appointment_apply_filter)
        consultGroup = contentView.findViewById(R.id.type_of_consult_radio)
        attendanceGroup = contentView.findViewById(R.id.attendence_radio)
        paymentGroup = contentView.findViewById(R.id.payment_radio)
        consultAll = contentView.findViewById(R.id.consult_type_all)
        consultRoutine = contentView.findViewById(R.id.consult_type_routine)
        consultVaccination = contentView.findViewById(R.id.consult_type_procedure)
        consultFirstVisit = contentView.findViewById(R.id.consult_type_first)
        consultAssesment = contentView.findViewById(R.id.consult_type_assessment)
        consultFollowUp = contentView.findViewById(R.id.consult_type_followup)
        consultPlaster = contentView.findViewById(R.id.consult_type_dressing)
        consultOthers = contentView.findViewById(R.id.consult_type_Others)
        attendanceNone = contentView.findViewById(R.id.attendence_none)
        attendanceCheckup = contentView.findViewById(R.id.attendence_basic_checkup)
        attendanceCheckout = contentView.findViewById(R.id.attendence_checkedout)
        attendanceCheckin = contentView.findViewById(R.id.attendence_checked_in)
        attendanceInConsult = contentView.findViewById(R.id.attendence_in_consult)
        paymentAll = contentView.findViewById(R.id.payment_all)
        paymentPending = contentView.findViewById(R.id.payment_pending)
        payment_received = contentView.findViewById(R.id.payment_received)
        payment_pending_settlement = contentView.findViewById(R.id.payment_pending_settlement)
        payment_complete_settlement = contentView.findViewById(R.id.payment_complete_settlement)
        payment_pending_refund = contentView.findViewById(R.id.payment_pending_refund)
        payment_complete_refund = contentView.findViewById(R.id.payment_complete_refund)
        payment_refund_scheduled = contentView.findViewById(R.id.payment_refund_scheduled)
        payment_partial_refund_pending =
            contentView.findViewById(R.id.payment_partial_refund_pending)
        payment_partial_refund_completed =
            contentView.findViewById(R.id.payment_partial_refund_completed)
        clearFilter = contentView.findViewById(R.id.appointment_clear_filter)
        video = contentView.findViewById(R.id.video_checkbox)
        clinic = contentView.findViewById(R.id.clinic_checkbox)
        if (NewAppointmentFragment.activePastFilter.equals("Active", ignoreCase = true)) {
            payment_pending_settlement.visibility = View.GONE
            payment_complete_settlement.visibility = View.GONE
            payment_pending_refund.visibility = View.GONE
            payment_complete_refund.visibility = View.GONE
            payment_refund_scheduled.visibility = View.GONE
            payment_partial_refund_pending.visibility = View.GONE
            payment_partial_refund_completed.visibility = View.GONE
        } else {
            payment_pending_settlement.visibility = View.VISIBLE
            payment_complete_settlement.visibility = View.VISIBLE
            payment_pending_refund.visibility = View.VISIBLE
            payment_complete_refund.visibility = View.VISIBLE
            payment_refund_scheduled.visibility = View.VISIBLE
            payment_partial_refund_pending.visibility = View.VISIBLE
            payment_partial_refund_completed.visibility = View.VISIBLE
        }
        setFilter(modeFilter, consultFilter, checkInStat, payment)
        paymentGroup.setOnCheckedChangeListener(RadioGroup.OnCheckedChangeListener { radioGroup, i ->
            if (paymentAll.isChecked) {
                payment = "All"
            } else if (paymentPending.isChecked) {
                payment = "Pending"
            } else if (payment_received.isChecked) {
                payment = "Received"
            } else if (payment_pending_settlement.isChecked) {
                payment = "pendingsettlement"
            } else if (payment_complete_settlement.isChecked) {
                payment = "completesettlement"
            } else if (payment_pending_refund.isChecked) {
                payment = "pendingrefund"
            } else if (payment_complete_refund.isChecked) {
                payment = "completerefund"
            } else if (payment_refund_scheduled.isChecked) {
                payment = "schedulerefund"
            } else if (payment_partial_refund_pending.isChecked) {
                payment = "partialrefundpending"
            } else if (payment_partial_refund_completed.isChecked) {
                payment = "partialrefunddone"
            }
        })
        consultGroup.setOnCheckedChangeListener(RadioGroup.OnCheckedChangeListener { radioGroup, i ->
            if (consultAll.isChecked) {
                consultFilter = "0"
            } else if (consultRoutine.isChecked) {
                consultFilter = "1"
            } else if (consultVaccination.isChecked) {
                consultFilter = "3"
            } else if (consultAssesment.isChecked) {
                consultFilter = "7"
            } else if (consultFirstVisit.isChecked) {
                consultFilter = "6"
            } else if (consultFollowUp.isChecked) {
                consultFilter = "2"
            } else if (consultPlaster.isChecked) {
                consultFilter = "4"
            } else if (consultOthers.isChecked) {
                consultFilter = "5"
            }
        })
        attendanceGroup.setOnCheckedChangeListener(RadioGroup.OnCheckedChangeListener { radioGroup, i ->
            if (attendanceNone.isChecked) {
                checkInStat = "0"
            } else if (attendanceCheckup.isChecked) {
                checkInStat = "2"
            } else if (attendanceCheckin.isChecked) {
                checkInStat = "1"
            } else if (attendanceInConsult.isChecked) {
                checkInStat = "3"
            } else if (attendanceCheckout.isChecked) {
                checkInStat = "4"
            }
        })
        clearFilter.setOnClickListener(View.OnClickListener { resetFilter() })
        applyFilter.setOnClickListener(View.OnClickListener {
            var count = 0
            if (video!!.isChecked && clinic!!.isChecked) {
                modeFilter = "All"
            } else if (video!!.isChecked) {
                modeFilter = "Video"
            } else if (clinic!!.isChecked) {
                modeFilter = "Clinic"
            }
            if (video!!.isChecked && !clinic!!.isChecked || !video!!.isChecked && clinic!!.isChecked) {
                count += 1
            }
            if (consultFilter != "0") {
                count += 1
            }
            if (checkInStat != "0") {
                count += 1
            }
            if (payment != "All") {
                count += 1
            }
            dialog.dismiss()
            appointmentFilterInterface.applyFilter(modeFilter,
                consultFilter,
                checkInStat,
                payment,
                count)
        })
        closeFilterSheet.setOnClickListener(View.OnClickListener { dialog.dismiss() })
    }

    fun resetFilter() {
        if (video != null) {
            video!!.isChecked = true
            clinic!!.isChecked = true
            consultAll.isChecked = true
            consultRoutine.isChecked = false
            consultVaccination.isChecked = false
            consultAssesment.isChecked = false
            consultFirstVisit.isChecked = false
            consultFollowUp.isChecked = false
            consultPlaster.isChecked = false
            consultOthers.isChecked = false
            attendanceNone.isChecked = true
            attendanceCheckout.isChecked = false
            attendanceCheckup.isChecked = false
            attendanceCheckin.isChecked = false
            attendanceInConsult.isChecked = false
            paymentAll.isChecked = true
            paymentPending.isChecked = false
            payment_received.isChecked = false
            payment_pending_settlement.isChecked = false
            payment_complete_settlement.isChecked = false
            payment_pending_refund.isChecked = false
            payment_complete_refund.isChecked = false
            payment_refund_scheduled.isChecked = false
            payment_partial_refund_pending.isChecked = false
            payment_partial_refund_completed.isChecked = false
            modeFilter = "All"
            checkInStat = "0"
            consultFilter = "0"
        }
    }

    private fun setFilter(
        mode: String,
        typeOfConsult: String,
        attendance: String,
        payment: String,
    ) {
        when (payment) {
            "All" -> paymentAll.isChecked = true
            "Pending" -> paymentPending.isChecked = true
            "Received" -> payment_received.isChecked = true
            "pendingsettlement" -> payment_pending_settlement.isChecked = true
            "completesettlement" -> payment_complete_settlement.isChecked = true
            "pendingrefund" -> payment_pending_refund.isChecked = true
            "completerefund" -> payment_complete_refund.isChecked = true
            "schedulerefund" -> payment_refund_scheduled.isChecked = true
            "partialrefundpending" -> payment_partial_refund_pending.isChecked = true
            "partialrefunddone" -> payment_partial_refund_completed.isChecked = true
        }
        when (mode) {
            "Video" -> {
                video!!.isChecked = true
                clinic!!.isChecked = false
            }
            "Clinic" -> {
                clinic!!.isChecked = true
                video!!.isChecked = false
            }
            "All" -> {
                video!!.isChecked = true
                clinic!!.isChecked = true
            }
        }
        when (typeOfConsult) {
            "1" -> consultRoutine.isChecked = true
            "2" -> consultFollowUp.isChecked = true
            "3" -> consultVaccination.isChecked = true
            "4" -> consultPlaster.isChecked = true
            "5" -> consultOthers.isChecked = true
            "6" -> consultFirstVisit.isChecked = true
            "7" -> consultAssesment.isChecked = true
            "0" -> consultAll.isChecked = true
        }
        when (attendance) {
            "0" -> attendanceNone.isChecked = true
            "1" -> attendanceCheckin.isChecked = true
            "2" -> attendanceCheckup.isChecked = true
            "3" -> attendanceInConsult.isChecked = true
            "4" -> attendanceCheckout.isChecked = true
        }
    }
}
