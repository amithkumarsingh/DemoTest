package com.whitecoats.clinicplus.adapters

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.PorterDuff
import android.net.Uri
import android.text.Html
import android.text.SpannableString
import android.text.style.UnderlineSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.whitecoats.clinicplus.*
import com.whitecoats.clinicplus.apis.ApiGetPostMethodCalls
import com.whitecoats.clinicplus.apis.ApiMethodCalls
import com.whitecoats.clinicplus.apis.ApiUrls
import com.whitecoats.clinicplus.fragments.NewAppointmentFragment.Companion.isMoreData
import com.whitecoats.clinicplus.interfaces.ApiCallbackInterface
import com.whitecoats.clinicplus.interfaces.AppointmentClickListener
import com.whitecoats.clinicplus.models.AppointmentModel
import com.whitecoats.clinicplus.models.RescheduleApptTimelineModel
import com.whitecoats.clinicplus.utils.ErrorHandlerClass
import com.whitecoats.clinicplus.utils.ErrorHandlerClass.errorHandler
import com.whitecoats.clinicplus.utils.SharedPref
import com.whitecoats.clinicplus.viewmodels.AppointmentDetailsViewModel
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*


class AppointmentCalendarListAdapter(
    var appointmentModelList: List<AppointmentModel>,
    var activity: Activity,
    var groupData: ArrayList<Int>,
    var context: Context,
    var appointmentClickListener: AppointmentClickListener,
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var globalClass: MyClinicGlobalClass
    val TYPE_HEADER = 0
    val TYPE_ITEM = 1
    val TYPE_FOOTER = 2
    var appUtilities: AppUtilities = AppUtilities()
    lateinit var otpLoading: ProgressDialog
    lateinit var invoiceServiceArrayAppended: JSONArray
    lateinit var changeArray: JSONArray
    private lateinit var originalServiceArrayData: JSONArray
    lateinit var invoiceServiceArray: JSONArray
    lateinit var footerHolder: FooterViewHolder
    var appointmentDetailsViewModel: AppointmentDetailsViewModel =
        ViewModelProvider((activity as ViewModelStoreOwner)).get(
            AppointmentDetailsViewModel::class.java
        )
    var appointmentOrderAmount = 0
    var appointmentOrderAmountDiscount = 0
    var appointmentNetAmount = 0
    var patientRecordsApi: PatientRecordsApi = PatientRecordsApi()
    val apiMethodCalls = ApiMethodCalls()
    var globalApiCall: ApiGetPostMethodCalls
    var sharedPref: SharedPref? = null


    init {
        appointmentDetailsViewModel.init()
        globalClass = context.applicationContext as MyClinicGlobalClass
        globalApiCall = ApiGetPostMethodCalls()
        sharedPref = SharedPref(activity)
    }


    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): RecyclerView.ViewHolder {
        when (i) {
            TYPE_ITEM -> {
                val view: View = LayoutInflater.from(viewGroup.context)
                    .inflate(R.layout.appointment_calendar_item, viewGroup, false)
                context = viewGroup.context
                return AppointmentCalendarListViewHolder(view)
            }
            TYPE_HEADER -> {
                val view: View = LayoutInflater.from(viewGroup.context)
                    .inflate(R.layout.appointment_calendar_item, viewGroup, false)
                context = viewGroup.context
                return AppointmentCalendarListViewHolder(view)
            }
            TYPE_FOOTER -> {
                val view: View =
                    LayoutInflater.from(viewGroup.context).inflate(
                        R.layout.footer_layout,
                        viewGroup, false
                    )
                return FooterViewHolder(view)
            }
            else -> throw RuntimeException("there is no type that matches the type $i + make sure your using types correctly")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        holder.itemView.tag = appointmentModelList[position]
        if (holder is AppointmentCalendarListViewHolder) {
            try {
                val itemViewHolder: AppointmentCalendarListViewHolder =
                    holder
                val appointmentApptListModel = appointmentModelList[position]
                val rescheduleJsonObject = JSONObject()
                rescheduleJsonObject.put("JSONARRAY", appointmentApptListModel.rescheduleJsonArray)
                if (groupData[position] == 0) {
                    itemViewHolder.apptCalendarHeader.visibility = View.VISIBLE
                    val date = appUtilities.changeDateFormat(
                        "yyyy-MM-dd",
                        "dd MMM, yyyy",
                        appointmentApptListModel.apptDate
                    )
                    itemViewHolder.apptCalendarHeader.text = date
                } else {
                    itemViewHolder.apptCalendarHeader.visibility = View.GONE
                }
                itemViewHolder.calendarPatientName.text = appointmentApptListModel.patientName
                /*New Registration(Autogenerated ID) changes for Gastro interface*/
                    if (appointmentApptListModel.generalID != null && !appointmentApptListModel.generalID.equals(
                            "",
                            ignoreCase = true
                        )
                    ) {
                        itemViewHolder.tvPatientGeneralId.visibility = View.VISIBLE
                        itemViewHolder.tvPatientGeneralId.text = appointmentApptListModel.generalID
                    } else {
                        itemViewHolder.tvPatientGeneralId.visibility = View.GONE
                    }
                itemViewHolder.calendar_patient_number.text =
                    appointmentApptListModel.patientPhoneNumber
                if (appointmentApptListModel.is_reschedule_active == 0) {
                    itemViewHolder.appt_reschedule_layout.alpha = .4f
                } else {
                    itemViewHolder.appt_reschedule_layout.alpha = 1f
                }
                itemViewHolder.single_reschedule_button.setOnClickListener(View.OnClickListener { view ->
                    if (appointmentApptListModel.is_reschedule_active != 0) {
                        appointmentClickListener.onItemClick(
                            view,
                            "Reschedule Appt",
                            appointmentApptListModel,
                            position
                        )
                    }
                })
                if (appointmentApptListModel.rescheduleJsonArray.length() > 0) {
                    itemViewHolder.rescheduleAppointmentTime.visibility = View.VISIBLE
                    itemViewHolder.reschedule_appt_time.visibility = View.VISIBLE
                    itemViewHolder.app_time_date_lay.visibility = View.GONE
                    itemViewHolder.appointment_time_date.visibility = View.GONE
                    itemViewHolder.reschedule_appt_time.text = convertDatetoMonthFormate(
                        appointmentApptListModel.scheduleTime
                    )
                } else {
                    itemViewHolder.rescheduleAppointmentTime.visibility = View.GONE
                    itemViewHolder.reschedule_appt_time.visibility = View.GONE
                    itemViewHolder.app_time_date_lay.visibility = View.VISIBLE
                    itemViewHolder.appointment_time_date.visibility = View.VISIBLE
                    itemViewHolder.appointment_time_date.text = convertDatetoMonthFormate(
                        appointmentApptListModel.scheduleTime
                    )
                }
                if (!appointmentApptListModel.paymentStatus.equals("success", ignoreCase = true)) {
                    itemViewHolder.apptCalendarPaymentLayout.visibility = View.VISIBLE
                } else {
                    itemViewHolder.apptCalendarPaymentLayout.visibility = View.GONE
                }
                itemViewHolder.calendarApptMode.text = appointmentApptListModel.clinicName
                itemViewHolder.appointmentTime.text = "@ " + appUtilities.changeDateFormat(
                    "HH:MM",
                    "H:MM", appointmentApptListModel.apptTime
                )
                when (appointmentApptListModel.apptType) {
                    1 -> {
                        itemViewHolder.caledarApptType.text = "Routine"
                    }
                    2 -> {
                        itemViewHolder.caledarApptType.text = "Follow-up"
                    }
                    3 -> {
                        itemViewHolder.caledarApptType.text = "Procedure/Vaccination"
                    }
                    4 -> {
                        itemViewHolder.caledarApptType.text = "Dressing/Plaster"
                    }
                    5 -> {
                        itemViewHolder.caledarApptType.text = "Other"
                    }
                    6 -> {
                        itemViewHolder.caledarApptType.text = "First Visit"
                    }
                    else -> {
                        itemViewHolder.caledarApptType.text = "Not Set"
                    }
                }
                if (appointmentApptListModel.refundStatus == 0 && appointmentApptListModel.isDoAutoRefund == 1) {
                    itemViewHolder.settlementPendingLayout.visibility = View.VISIBLE
                    itemViewHolder.statusSettlement.text = "Cancel Refund"
                    itemViewHolder.statusSettlement.setTextColor(
                        ContextCompat.getColor(
                            activity,
                            R.color.colorDanger
                        )
                    )
                    itemViewHolder.settlementPendingLayoutR.backgroundTintList =
                        ContextCompat.getColorStateList(activity, R.color.colorDanger)
                } else if (appointmentApptListModel.payment_title.isNotEmpty()) {
                    itemViewHolder.statusSettlement.text = appointmentApptListModel.payment_title
                    if (appointmentApptListModel.payment_title_color.equals(
                            "yellow",
                            ignoreCase = true
                        )
                    ) {
                        itemViewHolder.statusSettlement.setTextColor(
                            ContextCompat.getColor(
                                activity,
                                R.color.colorDarkInfo
                            )
                        )
                        itemViewHolder.settlementPendingLayoutR.backgroundTintList =
                            ContextCompat.getColorStateList(activity, R.color.colorDarkInfo)
                    } else if (appointmentApptListModel.payment_title_color.equals(
                            "green",
                            ignoreCase = true
                        )
                    ) {
                        itemViewHolder.statusSettlement.setTextColor(
                            ContextCompat.getColor(
                                activity,
                                R.color.colorSuccess
                            )
                        )
                        itemViewHolder.settlementPendingLayoutR.backgroundTintList =
                            ContextCompat.getColorStateList(activity, R.color.colorSuccess)
                    } else if (appointmentApptListModel.payment_title_color.equals(
                            "blue",
                            ignoreCase = true
                        )
                    ) {
                        itemViewHolder.statusSettlement.setTextColor(
                            ContextCompat.getColor(
                                activity,
                                R.color.colorAccent
                            )
                        )
                        itemViewHolder.settlementPendingLayoutR.backgroundTintList =
                            ContextCompat.getColorStateList(activity, R.color.colorAccent)
                    } else if (appointmentApptListModel.payment_title_color.equals(
                            "red",
                            ignoreCase = true
                        )
                    ) {
                        itemViewHolder.statusSettlement.setTextColor(
                            ContextCompat.getColor(
                                activity,
                                R.color.colorDanger
                            )
                        )
                        itemViewHolder.settlementPendingLayoutR.backgroundTintList =
                            ContextCompat.getColorStateList(activity, R.color.colorDanger)
                    }
                    itemViewHolder.settlementPendingLayout.visibility = View.VISIBLE
                } else {
                    itemViewHolder.settlementPendingLayout.visibility = View.GONE
                }

                itemViewHolder.appt_cancel_single_layout.setOnClickListener(View.OnClickListener { view ->

                    //>>>>>>> appointment-reschedule
                    MyClinicGlobalClass.logUserActionEvent(
                        ApiUrls.doctorId,
                        context.getString(
                            R.string.AppointmentsCalenderCancelAppointment
                        ),
                        null
                    )
                    appointmentClickListener.onItemClick(
                        view,
                        "canAppt",
                        appointmentApptListModel,
                        position
                    )
                })
                itemViewHolder.appt_calendar_payment.setOnClickListener(View.OnClickListener { view ->
                    appointmentClickListener.onItemClick(
                        view,
                        "payment",
                        appointmentApptListModel,
                        position
                    )
                    MyClinicGlobalClass.logUserActionEvent(
                        ApiUrls.doctorId,
                        context.getString(R.string.AppointmentsCalenderIncompletePaymentDetails),
                        null
                    )
                })
                itemViewHolder.video_cancel_appt.setOnClickListener(View.OnClickListener { view ->
                    appointmentClickListener.onItemClick(
                        view,
                        "canAppt",
                        appointmentApptListModel,
                        position
                    )
                })
                itemViewHolder.video_take_note_appt.setOnClickListener(View.OnClickListener { view ->
                    if (itemViewHolder.video_take_note_appt.text.toString() == ("Take Notes")
                    ) {
                        appointmentClickListener.onItemClick(
                            view,
                            "Take Notes",
                            appointmentApptListModel,
                            position
                        )
                        MyClinicGlobalClass.logUserActionEvent(
                            ApiUrls.doctorId,
                            context.getString(R.string.AppointmentsListStartConsult),
                            null
                        )
                    }
                })
                itemViewHolder.appt_mark_as_complete_calendar.setOnClickListener { view ->
                    appointmentClickListener.onItemClick(
                        view,
                        "CompleteAppt",
                        appointmentApptListModel,
                        position
                    )
                    MyClinicGlobalClass.logUserActionEvent(
                        ApiUrls.doctorId,
                        context.getString(R.string.AppointmentsListStartConsult),
                        null
                    )
                }
                itemViewHolder.appt_complete.setOnClickListener { view ->
                    appointmentClickListener.onItemClick(
                        view,
                        "CompleteAppt",
                        appointmentApptListModel,
                        position
                    )
                    MyClinicGlobalClass.logUserActionEvent(
                        ApiUrls.doctorId,
                        context.getString(R.string.AppointmentsListStartConsult),
                        null
                    )
                }
                itemViewHolder.appt_video.setOnClickListener { view ->
                    appointmentClickListener.onItemClick(
                        view,
                        "calendar join video",
                        appointmentApptListModel,
                        position
                    )
                    MyClinicGlobalClass.logUserActionEvent(
                        ApiUrls.doctorId,
                        context.getString(R.string.AppointmentsListJoinConsult),
                        null
                    )
                }
                itemViewHolder.appt_video_disable.setOnClickListener(View.OnClickListener {
                    Toast.makeText(
                        context,
                        "Video Link Not Updated",
                        Toast.LENGTH_SHORT
                    ).show()
                })
                itemViewHolder.doctor_join_text.setOnClickListener { view ->
                    appointmentClickListener.onItemClick(
                        view,
                        "setUp doctor video link",
                        appointmentApptListModel,
                        position
                    )
                }
                itemViewHolder.patient_join_text.setOnClickListener { view ->
                    appointmentClickListener.onItemClick(
                        view,
                        "setUp patient video link",
                        appointmentApptListModel,
                        position
                    )
                }
                itemViewHolder.send_link_patient_text.setOnClickListener { view ->
                    appointmentClickListener.onItemClick(
                        view,
                        "send video link",
                        appointmentApptListModel,
                        position
                    )
                }
                itemViewHolder.settlementPendingLayout.setOnClickListener(View.OnClickListener { view ->
                    if (itemViewHolder.statusSettlement.text.toString() == "Cancel Refund"
                    ) {
                        appointmentClickListener.onItemClick(
                            view,
                            "canRefund",
                            appointmentApptListModel,
                            position
                        )
                    } else {
                        appointmentClickListener.onItemClick(
                            view,
                            "settlementPending",
                            appointmentApptListModel,
                            position
                        )
                    }
                })
                if (appointmentApptListModel.activePast == 2) {
//                    itemViewHolder.cancel_appt.setVisibility(View.GONE);
                    itemViewHolder.open_appt_actions.visibility = View.GONE
                    itemViewHolder.appt_cancel_single_layout.visibility = View.GONE

                    /*ENGG-3785--Clinic + App issues hiding the view based on the condition*/itemViewHolder.external_video_actions_lay.visibility =
                        View.GONE
                    itemViewHolder.video_cancel_appt.visibility = View.GONE
                    itemViewHolder.video_take_note_appt.visibility = View.GONE
                    itemViewHolder.appt_complete_payment.visibility = View.GONE
                    itemViewHolder.appt_calendar_payment.visibility =
                        View.GONE //set gone while doing defered payment module
                    itemViewHolder.apptAction.visibility = View.VISIBLE
                    itemViewHolder.appt_close_payment_status_text.visibility = View.VISIBLE
                    itemViewHolder.closePaymentStatus.visibility = View.VISIBLE
                    itemViewHolder.settlementPendingLayout.visibility = View.VISIBLE
                    if (appointmentApptListModel.refundAmt > 0) {
                        if (itemViewHolder.statusSettlement.text.toString()
                            == ("Cancel Refund")
                        ) {
                            itemViewHolder.refundAmountLable.visibility = View.VISIBLE
                            itemViewHolder.refundAmountText.visibility = View.VISIBLE
                            itemViewHolder.refundAmountLable.text = "Refund Scheduled on"
                            itemViewHolder.refundAmountText.text =
                                "" + appointmentApptListModel.refundScheduledOn
                            itemViewHolder.refundAmountText.setTextColor(
                                ContextCompat.getColor(
                                    activity,
                                    R.color.colorInfo
                                )
                            )
                        } else {
                            itemViewHolder.refundAmountLable.visibility = View.VISIBLE
                            itemViewHolder.refundAmountText.visibility = View.VISIBLE
                            itemViewHolder.refundAmountLable.text = "Refund Amount"
                            itemViewHolder.refundAmountText.text =
                                "Rs." + appointmentApptListModel.refundAmt
                            itemViewHolder.refundAmountText.setTextColor(
                                ContextCompat.getColor(
                                    activity,
                                    R.color.colorBlack
                                )
                            )
                        }
                    } else {
                        itemViewHolder.refundAmountLable.visibility = View.GONE
                        itemViewHolder.refundAmountText.visibility = View.GONE
                    }
                    if (appointmentApptListModel.paymentStatus.equals(
                            "Pending",
                            ignoreCase = true
                        )
                    ) {
                        itemViewHolder.appt_calendar_payment.background
                            .setColorFilter(Color.parseColor("#EB0000"), PorterDuff.Mode.SRC_ATOP)
                    } else {
                        itemViewHolder.appt_calendar_payment.background
                            .setColorFilter(Color.parseColor("#008000"), PorterDuff.Mode.SRC_ATOP)
                    }
                    when (appointmentApptListModel.apptStatus) {
                        3 -> {
                            itemViewHolder.appt_close_payment_status_text.text = "Consulted"
                            itemViewHolder.appt_close_payment_status_text.setTextColor(
                                ContextCompat.getColor(
                                    activity,
                                    R.color.colorPrimary
                                )
                            )
                        }
                        4 -> {
                            itemViewHolder.appt_close_payment_status_text.text = "Cancelled"
                            itemViewHolder.appt_close_payment_status_text.setTextColor(
                                ContextCompat.getColor(
                                    activity,
                                    R.color.colorDanger
                                )
                            )
                        }
                        5 -> {
                            itemViewHolder.appt_close_payment_status_text.text =
                                "Cancelled by patient"
                            itemViewHolder.appt_close_payment_status_text.setTextColor(
                                ContextCompat.getColor(
                                    activity,
                                    R.color.colorDanger
                                )
                            )
                        }
                        6 -> {
                            itemViewHolder.appt_close_payment_status_text.text = "Patient no show"
                            itemViewHolder.appt_close_payment_status_text.setTextColor(
                                ContextCompat.getColor(
                                    activity,
                                    R.color.colorDanger
                                )
                            )
                        }
                        7 -> {
                            itemViewHolder.appt_close_payment_status_text.text = "Doctor no-show"
                            itemViewHolder.appt_close_payment_status_text.setTextColor(
                                ContextCompat.getColor(
                                    activity,
                                    R.color.colorDanger
                                )
                            )
                        }
                        8 -> {
                            itemViewHolder.appt_close_payment_status_text.text =
                                "Cancelled by doctor"
                            itemViewHolder.appt_close_payment_status_text.setTextColor(
                                ContextCompat.getColor(
                                    activity,
                                    R.color.colorDanger
                                )
                            )
                        }
                    }
                } else {
                    itemViewHolder.open_appt_actions.visibility = View.VISIBLE
                    if (appointmentApptListModel.apptMode == 3) {
                        itemViewHolder.apptAction.text = Html.fromHtml("<u>Take Notes</u>")
                        itemViewHolder.apptAction.setTextColor(
                            ContextCompat.getColor(
                                activity,
                                R.color.colorAccent
                            )
                        )
                        itemViewHolder.video_cancel_appt.visibility = View.GONE
                        itemViewHolder.video_take_note_appt.visibility = View.GONE
                        if (appointmentApptListModel.paymentStatus.equals(
                                "Pending",
                                ignoreCase = true
                            )
                        ) {
                            itemViewHolder.appt_calendar_payment.background.setColorFilter(
                                Color.parseColor("#EB0000"),
                                PorterDuff.Mode.SRC_ATOP
                            )
                        } else {
                            itemViewHolder.appt_calendar_payment.background.setColorFilter(
                                Color.parseColor("#008000"),
                                PorterDuff.Mode.SRC_ATOP
                            )
                        }
                        itemViewHolder.appt_cancel_single_layout.visibility = View.VISIBLE
                        itemViewHolder.appt_complete_payment.visibility = View.GONE
                        itemViewHolder.appt_calendar_payment.visibility = View.VISIBLE
                        itemViewHolder.appt_close_payment_status_text.visibility = View.GONE
                        itemViewHolder.closePaymentStatus.visibility = View.GONE
                        itemViewHolder.settlementPendingLayout.visibility = View.GONE
                        itemViewHolder.refundAmountLable.visibility = View.GONE
                        itemViewHolder.refundAmountText.visibility = View.GONE
                        itemViewHolder.appt_complete_payment.visibility = View.GONE
                        itemViewHolder.appt_video.visibility = View.GONE
                        itemViewHolder.appt_mark_as_complete_calendar.visibility = View.VISIBLE
                        itemViewHolder.appt_mark_as_complete_calendar.visibility = View.VISIBLE
                        itemViewHolder.appt_video.visibility = View.GONE
                        itemViewHolder.appt_video_disable.visibility = View.GONE
                        itemViewHolder.external_video_actions_lay.visibility = View.GONE
                    } else if (appointmentApptListModel.apptMode == 1) {
                        itemViewHolder.appt_complete_payment.visibility = View.VISIBLE
                        itemViewHolder.video_cancel_appt.visibility = View.VISIBLE
                        itemViewHolder.video_cancel_appt.text = Html.fromHtml("<u>Cancel Appt</u>")
                        if (appointmentApptListModel.paymentStatus.equals(
                                "Pending",
                                ignoreCase = true
                            )
                        ) {
                            itemViewHolder.appt_calendar_payment.background.setColorFilter(
                                Color.parseColor("#EB0000"),
                                PorterDuff.Mode.SRC_ATOP
                            )
                        } else {
                            itemViewHolder.appt_calendar_payment.background.setColorFilter(
                                Color.parseColor("#008000"),
                                PorterDuff.Mode.SRC_ATOP
                            )
                        }
                        itemViewHolder.appt_video.visibility = View.VISIBLE
                        itemViewHolder.appt_mark_as_complete_calendar.visibility = View.VISIBLE
                        itemViewHolder.video_take_note_appt.visibility = View.VISIBLE
                        //                        itemViewHolder.cancel_appt.setVisibility(View.GONE);
                        itemViewHolder.appt_cancel_single_layout.visibility = View.VISIBLE
                        itemViewHolder.video_take_note_appt.text =
                            Html.fromHtml("<u>Take Notes</u>")
                        itemViewHolder.video_take_note_appt.setTextColor(
                            ContextCompat.getColor(
                                activity,
                                R.color.colorAccent
                            )
                        )
                        itemViewHolder.appt_calendar_payment.visibility = View.VISIBLE
                        if (appointmentApptListModel.video_tool > 0) {
                            if (appointmentApptListModel.video_tool == 1) {
                                itemViewHolder.appt_complete_payment.visibility = View.VISIBLE
                                itemViewHolder.appt_video_disable.visibility = View.GONE
                                itemViewHolder.external_video_actions_lay.visibility = View.VISIBLE
                                itemViewHolder.appt_video.visibility = View.VISIBLE
                                itemViewHolder.send_link_patient_text.visibility = View.VISIBLE
                                itemViewHolder.doctor_join_text.visibility = View.GONE
                                itemViewHolder.patient_join_text.visibility = View.GONE
                            } else {
                                if (!appointmentApptListModel.doctor_join_external_url.equals(
                                        "null",
                                        ignoreCase = true
                                    )
                                ) {
                                    itemViewHolder.appt_complete_payment.visibility = View.VISIBLE
                                    itemViewHolder.appt_video.visibility = View.VISIBLE
                                    itemViewHolder.appt_video_disable.visibility = View.GONE
                                    itemViewHolder.external_video_actions_lay.visibility =
                                        View.VISIBLE
                                    itemViewHolder.send_link_patient_text.visibility = View.VISIBLE
                                    itemViewHolder.doctor_join_text.visibility = View.VISIBLE
                                    itemViewHolder.patient_join_text.visibility = View.VISIBLE
                                } else {
                                    itemViewHolder.appt_complete_payment.visibility = View.GONE
                                    itemViewHolder.appt_video_disable.visibility = View.VISIBLE
                                    itemViewHolder.external_video_actions_lay.visibility =
                                        View.VISIBLE
                                    itemViewHolder.send_link_patient_text.visibility = View.VISIBLE
                                    itemViewHolder.doctor_join_text.visibility = View.VISIBLE
                                    itemViewHolder.patient_join_text.visibility = View.VISIBLE
                                }
                            }
                        } else {
                            itemViewHolder.appt_complete_payment.visibility = View.GONE
                            itemViewHolder.appt_video_disable.visibility = View.VISIBLE
                            /*ENGG-3785 -- Clinic + App issues hiding the view based on the condition*/itemViewHolder.external_video_actions_lay.visibility =
                                View.GONE
                        }
                        itemViewHolder.appt_close_payment_status_text.visibility = View.GONE
                        itemViewHolder.closePaymentStatus.visibility = View.GONE
                        itemViewHolder.settlementPendingLayout.visibility = View.GONE
                        itemViewHolder.refundAmountLable.visibility = View.GONE
                        itemViewHolder.refundAmountText.visibility = View.GONE
                    }
                }

                if (sharedPref!!.isPrefExists("EMR")) {
                    holder.apptAction.visibility = View.VISIBLE
                } else {
                    holder.apptAction.visibility = View.GONE
                }

                itemViewHolder.apptAction.setOnClickListener(View.OnClickListener { view ->
                    if (itemViewHolder.apptAction.text.toString() == ("Take Notes")
                    ) {
                        appointmentClickListener.onItemClick(
                            view,
                            "Take Notes",
                            appointmentApptListModel,
                            position
                        )
                        MyClinicGlobalClass.logUserActionEvent(
                            ApiUrls.doctorId,
                            context.getString(R.string.AppointmentsListStartConsult),
                            null
                        )
                    } else if (itemViewHolder.apptAction.text.toString() == ("Join Video")
                    ) {
                        appointmentClickListener.onItemClick(
                            view,
                            "join video",
                            appointmentApptListModel,
                            position
                        )
                        MyClinicGlobalClass.logUserActionEvent(
                            ApiUrls.doctorId,
                            context.getString(R.string.AppointmentsListJoinConsult),
                            null
                        )
                    }
                })
                itemViewHolder.create_view_receipt.setTextColor(
                    ContextCompat.getColor(
                        activity,
                        R.color.colorPrimary
                    )
                )
                itemViewHolder.create_view_receipt.visibility = View.VISIBLE
                if (appointmentApptListModel.receiptUrl === "") {
                    val content = SpannableString("Create Receipt")
                    content.setSpan(UnderlineSpan(), 0, content.length, 0)
                    itemViewHolder.create_view_receipt.text = content
                } else {
                    val content = SpannableString("View Receipt")
                    content.setSpan(UnderlineSpan(), 0, content.length, 0)
                    itemViewHolder.create_view_receipt.text = content
                }
                if (appointmentApptListModel.apptStatus == 3) {
                    if (appointmentApptListModel.paymentStatus.equals(
                            "pending",
                            ignoreCase = true
                        ) || appointmentApptListModel.paymentStatus.equals(
                            "success",
                            ignoreCase = true
                        )
                    ) {
                        itemViewHolder.create_view_invoice_tv.visibility = View.VISIBLE
                        if (appointmentApptListModel.invoiceUrl == null) {
                            val content = SpannableString("Create Invoice")
                            content.setSpan(UnderlineSpan(), 0, content.length, 0)
                            itemViewHolder.create_view_invoice_tv.text = content
                        } else {
                            val content = SpannableString("View Invoice")
                            content.setSpan(UnderlineSpan(), 0, content.length, 0)
                            itemViewHolder.create_view_invoice_tv.text = content
                        }
                    }
                } else if (appointmentApptListModel.apptStatus == 4 && appointmentApptListModel.paymentStatus.equals(
                        "success",
                        ignoreCase = true
                    )
                ) {
                    if (appointmentApptListModel.paymentStatus.equals(
                            "success",
                            ignoreCase = true
                        )
                    ) {
                        itemViewHolder.create_view_invoice_tv.visibility = View.VISIBLE
                        if (appointmentApptListModel.invoiceUrl === "") {
                            val content = SpannableString("Create Invoice")
                            content.setSpan(UnderlineSpan(), 0, content.length, 0)
                            itemViewHolder.create_view_invoice_tv.text = content
                        } else {
                            val content = SpannableString("View Invoice")
                            content.setSpan(UnderlineSpan(), 0, content.length, 0)
                            itemViewHolder.create_view_invoice_tv.text = content
                        }
                    }
                } else {
                    itemViewHolder.create_view_invoice_tv.visibility = View.GONE
                }
                itemViewHolder.create_view_invoice_tv.setOnClickListener { view ->
                    if (itemViewHolder.create_view_invoice_tv.text
                            .toString() == ("View Invoice")
                    ) {
                        getFileFromUrl(appointmentApptListModel.invoiceUrl)
                    } else {
                        appointmentClickListener.onItemClick(
                            view,
                            "apptCalednarDetails",
                            appointmentApptListModel,
                            position
                        )

                        // getServicesForAppointmentData(activity, appointmentApptListModel.getAppointmentId(), appointmentApptListModel.getOrderId(), appointmentApptListModel, itemViewHolder);
                    }
                }
                itemViewHolder.apptLayout.setOnClickListener(View.OnClickListener { view ->
                    MyClinicGlobalClass.logUserActionEvent(
                        ApiUrls.doctorId,
                        context.getString(R.string.AppointmentsSearchPatientBookAppointment),
                        null
                    )
                    appointmentClickListener.onItemClick(
                        view,
                        "apptCalednarDetails",
                        appointmentApptListModel,
                        position
                    )
                })

                itemViewHolder.create_view_receipt.setOnClickListener { view ->
                    if (itemViewHolder.create_view_receipt.text
                            .toString() == ("Create Receipt")
                    ) {
                        if (appointmentApptListModel.paymentStatus
                                .equals("pending", ignoreCase = true)
                        ) {
                            val dialog = Dialog(context)
                            dialog.setCancelable(false)
                            dialog.setContentView(R.layout.create_receipt_dialog)
                            val spinner_paymentMode =
                                dialog.findViewById<View>(R.id.spinner_paymentMode) as Spinner
                            val amount_editText =
                                dialog.findViewById<View>(R.id.amount_editText) as EditText
                            val dailogArticleCancel =
                                dialog.findViewById<View>(R.id.dailogArticleCancel) as ImageView
                            val close_textView =
                                dialog.findViewById<View>(R.id.close_textView) as TextView
                            dailogArticleCancel.setOnClickListener { view12: View -> dialog.dismiss() }
                            close_textView.setOnClickListener { view13: View -> dialog.dismiss() }

                            //amount_editText.setText(String.valueOf(appointmentApptListModel.getNetTotalPaid()));
                            val url =
                                ApiUrls.orderPaymentAmount + "?order_id=" + appointmentApptListModel.orderId
                            apiMethodCalls.getApiData(url,
                                null,
                                activity,
                                object : ApiCallbackInterface {
                                    override fun onSuccessResponse(response: String) {
                                        try {
                                            val jsonObject = JSONObject(response)
                                            val response1 =
                                                jsonObject.optJSONObject("response")!!
                                                    .optJSONObject("response")
                                            amount_editText.setText(
                                                response1!!.optInt("amount")
                                                    .toString()
                                            )
                                            if (response1.optBoolean("is_have_incs")) {
                                                amount_editText.isFocusable = false
                                                amount_editText.isFocusableInTouchMode = false
                                                amount_editText.isClickable = false
                                            } else {
                                                amount_editText.isFocusable = true
                                                amount_editText.isFocusableInTouchMode = true
                                                amount_editText.isClickable = true
                                                amount_editText.setSelection(amount_editText.text.length)
                                            }
                                        } catch (e: JSONException) {
                                            e.printStackTrace()
                                        }
                                    }

                                    override fun onErrorResponse(error: String) {
                                      //  Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
                                        errorHandler(context,error)
                                    }
                                })
                            val createReceiptText =
                                dialog.findViewById<View>(R.id.createReceiptText) as TextView
                            createReceiptText.setOnClickListener { view14: View ->
                                if (globalClass.isOnline) {
                                    if (spinner_paymentMode.selectedItemPosition == 0) {
                                        Toast.makeText(
                                            context,
                                            "Please select payment mode",
                                            Toast.LENGTH_LONG
                                        ).show()
                                    } else {
                                        val paymentModespin =
                                            spinner_paymentMode.selectedItem.toString()
                                        var paymentMode = ""
                                        if (paymentModespin.equals("Cash", ignoreCase = true)) {
                                            paymentMode = "Cash"
                                        } else if (paymentModespin.equals(
                                                "Credit Card",
                                                ignoreCase = true
                                            )
                                        ) {
                                            paymentMode = "CC"
                                        }
                                        if (paymentModespin.equals(
                                                "Debit Card",
                                                ignoreCase = true
                                            )
                                        ) {
                                            paymentMode = "DC"
                                        }
                                        if (paymentModespin.equals(
                                                "Net Banking",
                                                ignoreCase = true
                                            )
                                        ) {
                                            paymentMode = "Net Banking"
                                        } else if (paymentModespin.equals(
                                                "Offline Collection",
                                                ignoreCase = true
                                            )
                                        ) {
                                            paymentMode = "Offline Collection"
                                        }
                                        val amountPaid =
                                            amount_editText.text.toString()
                                        updatePaymentStatus(
                                            amountPaid,
                                            appointmentApptListModel.orderId,
                                            paymentMode,
                                            true,
                                            itemViewHolder,
                                            appointmentApptListModel,
                                            dialog
                                        )
                                    }
                                } else {
                                    globalClass.noInternetConnection.showDialog(activity)
                                }
                            }
                            val paymentMode: MutableList<String> =
                                ArrayList()
                            paymentMode.add("Select Payment Mode")
                            paymentMode.add("Cash")
                            paymentMode.add("Credit Card")
                            paymentMode.add("Debit Card")
                            paymentMode.add("Net Banking")


                            // Creating adapter for spinner
                            val dataAdapterPaymentMode =
                                ArrayAdapter(
                                    context, android.R.layout.simple_spinner_item, paymentMode
                                )

                            // Drop down layout style - list view with radio button
                            dataAdapterPaymentMode.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

                            // attaching data adapter to spinner
                            spinner_paymentMode.adapter = dataAdapterPaymentMode
                            dialog.show()
                            val window = dialog.window
                            window!!.setLayout(
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.WRAP_CONTENT
                            )
                        } else {
                            createReceipt(
                                appointmentApptListModel.orderId,
                                appointmentApptListModel,
                                itemViewHolder
                            )
                        }
                        ConfirmOrderActivity.confirmOrderFlag = 0
                    } else if (itemViewHolder.create_view_receipt.text.toString() == ("View Receipt")
                    ) {
                        getReceiptUrl(appointmentApptListModel.receiptUrl)
                    }
                }

                if (holder.create_view_invoice_tv.visibility == View.VISIBLE || holder.create_view_receipt.visibility == View.VISIBLE
                    || holder.apptAction.visibility == View.VISIBLE
                ) {
                    holder.ll_parent_notes!!.visibility = View.VISIBLE
                } else {
                    holder.ll_parent_notes!!.visibility = View.GONE
                }


                itemViewHolder.rescheduleAppointmentTime.setOnClickListener { view ->
                    val dialog = Dialog(context)
                    dialog.setCancelable(false)
                    dialog.setContentView(R.layout.reschedule_appointment_timeline)
                    val rescheduleTimeLineList =
                        ArrayList<RescheduleApptTimelineModel>()
                    val recycler_view =
                        dialog.findViewById<View>(R.id.recycler_view) as RecyclerView
                    val rescheduleApptTimeLineAdapter =
                        RescheduleApptTimeLineAdapter(context, rescheduleTimeLineList)
                    val linearLayoutManager =
                        LinearLayoutManager(context, RecyclerView.VERTICAL, false)
                    recycler_view.layoutManager = linearLayoutManager
                    recycler_view.adapter = rescheduleApptTimeLineAdapter
                    val dailogArticleCancel =
                        dialog.findViewById<View>(R.id.dailogArticleCancel) as ImageView
                    dailogArticleCancel.setOnClickListener { view1: View -> dialog.dismiss() }
                    val it = rescheduleJsonObject.keys()
                    while (it.hasNext()) {
                        val keys = it.next()
                        var innerJson: JSONObject? = null
                        try {
                            innerJson = JSONObject(rescheduleJsonObject.toString())
                            val innerArray = innerJson.getJSONArray(keys)
                            for (i in 0 until innerArray.length()) {
                                val rescheduleApptTimelineModel =
                                    RescheduleApptTimelineModel()
                                val innInnerObj = innerArray.getJSONObject(i)
                                rescheduleApptTimelineModel.appt_time =
                                    innInnerObj.optString("scheduled_start_time")
                                rescheduleApptTimelineModel.booked_by =
                                    innInnerObj.optString("booked_by_name")
                                rescheduleTimeLineList.add(rescheduleApptTimelineModel)
                            }
                            val rescheduleApptTimelineModel =
                                RescheduleApptTimelineModel()
                            rescheduleApptTimelineModel.appt_time =
                                appointmentApptListModel.scheduleTime
                            rescheduleApptTimelineModel.booked_by =
                                appointmentApptListModel.booked_by
                            rescheduleTimeLineList.add(rescheduleApptTimelineModel)
                            rescheduleApptTimeLineAdapter.notifyDataSetChanged()
                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }
                    }
                    dialog.show()
                    val window = dialog.window
                    window!!.setLayout(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                    )
                }
            } catch (e: Exception) {
                e.message
            }
        } else if (holder is FooterViewHolder) {
            footerHolder = holder
            if (isMoreData) {
                holder.footerText.visibility = View.VISIBLE
            } else {
                holder.footerText.visibility = View.GONE
            }
            footerHolder.footerText.setOnClickListener(View.OnClickListener { view ->
                appointmentClickListener.onItemClick(
                    view,
                    "calendarloadMore",
                    null,
                    position
                )
            })
        }
    }

    fun updatePaymentStatus(
        paidAmount: String,
        orderId: Int,
        paymentMode: String,
        isGeneratedReceipt: Boolean,
        itemViewHolder: AppointmentCalendarListViewHolder,
        appointmentApptListModel: AppointmentModel,
        dialog: Dialog,
    ) {
        otpLoading = ProgressDialog(context)
        otpLoading.setMessage(context.resources.getString(R.string.wait_while_we_updating))
        otpLoading.setTitle(context.resources.getString(R.string.updating))
        otpLoading.setProgressStyle(ProgressDialog.STYLE_SPINNER)
        otpLoading.setCancelable(false)
        otpLoading.show()
        //        RequestQueue requestQueue = Volley.newRequestQueue(context);
        val URL = ApiUrls.updatePaymentStatus
        val jsonValue = JSONObject()
        try {
            jsonValue.put("order_net_amount", paidAmount.toInt())
            jsonValue.put("order_id", orderId)
            jsonValue.put("order_payment_mode", paymentMode)
            jsonValue.put("isGenerateReport", isGeneratedReceipt)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        globalApiCall.volleyApiRequestData(URL,
            Request.Method.POST,
            jsonValue,
            activity,
            object : VolleyCallback {
                override fun onSuccess(result: String) {
                    try {
                        val response = JSONObject(result)
                        //Process os success response
                        otpLoading.dismiss()
                        dialog.dismiss()
                        val rootObj = response.getJSONObject("response")
                        if (isGeneratedReceipt) {
                            val intervention = rootObj["receipt"]
                            if (intervention is JSONArray) {
                                // It's an array
                                // model.setPhNo(patientObject.getString("phone"));
                            } else if (intervention is JSONObject) {
                                // It's an object
                                val receiptObject = rootObj.getJSONObject("receipt")
                                //                                    appointmentApptListModel.setReceiptUrl(receiptObject.getString("public_url"));
                                val receiptUrl = receiptObject.getString("public_url")
                                appointmentApptListModel.receiptUrl = receiptUrl
                                appointmentApptListModel.receiptUrl = receiptUrl
                                appointmentApptListModel.paymentStatus = "success"
                                appointmentApptListModel.payment_title = "Payment Completed"
                                appointmentApptListModel.payment_title_color = "green"
                                notifyDataSetChanged()
                            }
                            val showReceipt = "View Receipt"
                            val content = SpannableString(showReceipt)
                            content.setSpan(UnderlineSpan(), 0, showReceipt.length, 0)
                            itemViewHolder.create_view_receipt.text = content
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        otpLoading.dismiss()
                    }
                }

                override fun onError(err: String) {
                    otpLoading.dismiss()
                    errorHandler(activity, err)
                }
            })
    }


    fun getFileFromUrl(fileUrl: String) {
        val url = JSONObject()
        try {
            url.put("url", fileUrl)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        patientRecordsApi.postRecords(ApiUrls.getFileFromUrl,
            url,
            activity,
            object : VolleyCallback {
                override fun onSuccess(result: String) {
                    try {
                        val resObj = JSONObject(result)
                        val browserIntent =
                            Intent(Intent.ACTION_VIEW, Uri.parse(resObj.getString("response")))
                        context.startActivity(browserIntent)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                override fun onError(err: String) {
                    errorHandler(context, err)
                }
            })
    }


    fun getReceiptUrl(receiptUrl: String) {
        var receiptUrl = receiptUrl
        if (!receiptUrl.isEmpty()) {
            try {
                if (receiptUrl.startsWith("http://", 0) || receiptUrl.startsWith("https://", 0)) {
                } else {
                    receiptUrl = "http://$receiptUrl"
                }
                val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(receiptUrl))
                context.startActivity(browserIntent)
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(context, "Invalid URL", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(context, "Invalid URL", Toast.LENGTH_SHORT).show()
        }
    }

    fun createReceipt(
        orderId: Int,
        appointmentApptListModel: AppointmentModel,
        itemViewHolder: AppointmentCalendarListViewHolder,
    ) {
        val url: String = ApiUrls.createReceipt + "?order_id=" + orderId
        otpLoading = ProgressDialog(activity)
        otpLoading.setMessage(activity.resources.getString(R.string.please_wait))
        otpLoading.setProgressStyle(ProgressDialog.STYLE_SPINNER)
        otpLoading.setCancelable(false)
        otpLoading.show()
        globalApiCall.volleyApiRequestData(url,
            Request.Method.GET,
            null,
            activity,
            object : VolleyCallback {
                override fun onSuccess(result: String) {
                    try {
                        val response = JSONObject(result)
                        val rootObj = response.getJSONObject("response")
                        val receiptUrl = rootObj.getString("public_url")
                        appointmentApptListModel.receiptUrl = receiptUrl
                        val showReceipt = "View Receipt"
                        itemViewHolder.create_view_receipt.text = showReceipt
                        otpLoading.dismiss()
                    } catch (e: Exception) {
                        e.printStackTrace()
                        otpLoading.dismiss()
                    }
                }

                override fun onError(err: String) {
                    otpLoading.dismiss()
                    errorHandler(activity, err)
                }
            })
    }

    fun trimMessage(json: String, key: String): String? {
        val trimmedString: String = try {
            val obj = JSONObject(json)
            obj.getString(key)
        } catch (e: JSONException) {
            e.printStackTrace()
            return null
        }
        return trimmedString
    }


    private fun convertDatetoMonthFormate(scheduleTime: String): String {
        var finalDateTimeString = ""
        try {
            val month_date = SimpleDateFormat("MMM yyyy", Locale.ENGLISH)
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
            val splitDateTime = scheduleTime.split(" ").toTypedArray()
            val dateString = splitDateTime[0]
            val dateSplitedString = dateString.split("-").toTypedArray()
            val time = splitDateTime[1]
            val date = sdf.parse(dateString)
            val month_name = month_date.format(date!!)
            finalDateTimeString =
                dateSplitedString[2] + month_name + " @" + appUtilities.formatTimeBasedOnPreference(
                    context,
                    "HH:mm:ss",
                    time
                )
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return finalDateTimeString
    }


    fun getServicesForAppointmentData(
        activity: Activity,
        appointmentId: Int,
        orderId: Int,
        appointmentApptListModel: AppointmentModel,
        itemViewHolder: AppointmentCalendarListViewHolder,
    ) {
        appointmentDetailsViewModel.getServicesForAppointmentData(activity, appointmentId)
            .observe(
                (activity as LifecycleOwner)
            ) { s ->
                Log.i("capture notes res", s)
                try {
                    val response = JSONObject(s)
                    if (response.getInt("status_code") == 200) {
                        val resObject = response.getJSONObject("response").getJSONObject("response")
                        invoiceServiceArrayAppended = JSONArray()
                        changeArray = JSONArray()
                        originalServiceArrayData = JSONArray()
                        invoiceServiceArray = resObject.getJSONArray("invoice_services")
                        for (i in 0 until invoiceServiceArray.length()) {
                            val invoiceArrayObject = invoiceServiceArray.getJSONObject(i)
                            val eachValue = JSONObject()
                            eachValue.put("id", invoiceArrayObject.getInt("id"))
                            eachValue.put(
                                "appointment_id",
                                invoiceArrayObject.getInt("appointment_id")
                            )
                            eachValue.put("order_id", invoiceArrayObject.getInt("order_id"))
                            eachValue.put(
                                "inclusion_type",
                                invoiceArrayObject.getInt("inclusion_type")
                            )
                            eachValue.put(
                                "appt_service_id",
                                invoiceArrayObject.getInt("appt_service_id")
                            )
                            eachValue.put(
                                "pre_tax_amount",
                                invoiceArrayObject.getInt("pre_tax_amount")
                            )
                            eachValue.put("discount", invoiceArrayObject.getInt("discount"))
                            eachValue.put(
                                "final_pre_tax_amount",
                                invoiceArrayObject.getInt("final_pre_tax_amount")
                            )
                            eachValue.put("total", invoiceArrayObject.getInt("total"))
                            eachValue.put("tax_amt", invoiceArrayObject.getInt("tax_amt"))
                            eachValue.put("created_at", invoiceArrayObject.getString("created_at"))
                            eachValue.put("updated_at", invoiceArrayObject.getString("updated_at"))
                            eachValue.put(
                                "service_details",
                                invoiceArrayObject.getJSONObject("service_details")
                            )
                            eachValue.put("taxes", invoiceArrayObject.getJSONArray("taxes"))
                            eachValue.put("status", invoiceArrayObject.getBoolean("status"))
                            eachValue.put(
                                "percentageApply",
                                invoiceArrayObject.getInt("percentageApply")
                            )
                            eachValue.put(
                                "applyDiscount1Flag",
                                invoiceArrayObject.getInt("applyDiscount1Flag")
                            )
                            var totalPercent = 0
                            for (j in 0 until invoiceArrayObject.getJSONArray("taxes").length()) {
                                val percentObject =
                                    invoiceArrayObject.getJSONArray("taxes").getJSONObject(j)
                                val taxPercentage = percentObject.getInt("percentage")
                                totalPercent = totalPercent + taxPercentage
                            }
                            val totalPercentTax = java.lang.Float.valueOf(totalPercent.toFloat())
                            val taxesAmountAfterCalculateText =
                                invoiceArrayObject.getInt("pre_tax_amount")
                                    .toFloat() / 100 * totalPercentTax
                            val finalPostTaxAmount =
                                invoiceArrayObject.getInt("pre_tax_amount") + taxesAmountAfterCalculateText
                            eachValue.put("final_post_tax_amount", finalPostTaxAmount.toDouble())
                            eachValue.put("tax_amount", taxesAmountAfterCalculateText.toDouble())
                            invoiceServiceArrayAppended.put(eachValue)
                        }
                        val appointmentDataObject = resObject.getJSONObject("appt_data")
                        val appointmentOrderObject = appointmentDataObject.getJSONObject("order")
                        appointmentOrderAmount = appointmentOrderObject.getInt("order_amt")
                        appointmentOrderAmountDiscount = appointmentOrderObject.getInt("discount")
                        appointmentNetAmount = appointmentOrderObject.getInt("net_amount")
                        changeArray.put(appointmentOrderObject.getInt("order_amt"))
                        val invoiceServiceArray = resObject.getJSONArray("invoice_services")
                        val totalProcedureAmount = 0
                        if (invoiceServiceArray.length() > 0) {
                            for (i in 0 until invoiceServiceArray.length()) {
                                val appointmentInvoiceServiceArrayObject =
                                    invoiceServiceArray.getJSONObject(i)
                                changeArray.put(appointmentInvoiceServiceArrayObject.getInt("pre_tax_amount"))
                                originalServiceArrayData.put(
                                    appointmentInvoiceServiceArrayObject.getInt(
                                        "pre_tax_amount"
                                    )
                                )
                            }
                        }
                        Log.d("changeArrayData:", "changeArrayData:$changeArray")
                        getInvoiceDetailsData(
                            activity,
                            orderId,
                            changeArray,
                            appointmentApptListModel,
                            itemViewHolder
                        )
                    } else {
                        errorHandler(activity, s)
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                    Log.d("parserError:", "parserError:$e")
                }
            }
    }

    private fun getInvoiceDetailsData(
        activity: Activity,
        orderID: Int,
        changeArray: JSONArray,
        appointmentModel: AppointmentModel,
        itemViewHolder: AppointmentCalendarListViewHolder,
    ) {
        appointmentDetailsViewModel.getInvoiceData(activity, orderID, changeArray)
            .observe(
                (activity as LifecycleOwner)
            ) { s ->
                Log.i("invoice details res", s)
                try {
                    val response = JSONObject(s)
                    if (response.getInt("status_code") == 200) {
                        val resObject = response.getJSONObject("response").getJSONObject("response")
                        val invoicePreTaxAmount = resObject.getDouble("invoicePreTaxAmount")
                        val invoicePreTaxDiscount = resObject.getDouble("invoicePreTaxDiscount")
                        val invoicePreTaxTotal = resObject.getDouble("invoicePreTaxTotal")
                        val invoiceTotalTax = resObject.getDouble("invoiceTotalTax")
                        val invoiceNetAmount = resObject.getDouble("invoiceNetAmount")
                        val invoicePostTaxDiscount = resObject.getDouble("invoicePostTaxDiscount")
                        val invoiceGrandAmount = resObject.getDouble("invoiceGrandAmount")
                        val invoiceGrandDiscount = resObject.getDouble("invoiceGrandDiscount")
                        val invoiceGrandPreTax = resObject.getDouble("invoiceGrandPreTax")
                        val invoiceGrandTax = resObject.getDouble("invoiceGrandTax")
                        val invoiceGrandTotal = resObject.getDouble("invoiceGrandTotal")

                        //                       invoice services data
                        val invoiceServiceArrayData = resObject.getJSONArray("invoice_services")
                        val params = JSONObject()
                        try {
                            params.put("order_id", orderID)
                            params.put("pre_tax_amount", invoicePreTaxAmount)
                            params.put("pre_tax_discount", invoicePreTaxDiscount)
                            params.put("pre_tax_total", invoicePreTaxTotal)
                            params.put("total_tax", invoiceTotalTax)
                            params.put("invoice_total_amount", invoiceNetAmount)
                            params.put("post_tax_discount", invoicePostTaxDiscount)
                            params.put("isConsultaionForInvoice", true)
                            params.put("invoice_services", invoiceServiceArrayData)
                            params.put("invoice_grand_amount", invoiceGrandAmount)
                            params.put("invoice_grand_discount", invoiceGrandDiscount)
                            params.put("invoice_grand_pre_tax", invoiceGrandPreTax)
                            params.put("invoice_grand_tax", invoiceGrandTax)
                            params.put("invoice_grand_total", invoiceGrandTotal)
                            //                       params.put("platform", "app");
                            createInvoice(activity, params, appointmentModel, itemViewHolder)
                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }
                    } else {
                        errorHandler(context, s)
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                    Log.d("parserError:", "parserError:$e")
                }
            }
    }

    fun createInvoice(
        activity: Activity,
        params: JSONObject,
        appointmentModel: AppointmentModel,
        itemViewHolder: AppointmentCalendarListViewHolder,
    ) {
        appointmentDetailsViewModel.creteInvoice(activity, params)
            .observe(
                (activity as LifecycleOwner)
            ) { s ->
                try {
                    val response = JSONObject(s)
                    if (response.getInt("status_code") == 200) {
                        val responseObject = response.getJSONObject("response")

                        val invoiceResponseObject = responseObject.optJSONObject("invoice")
                        if (invoiceResponseObject != null) {
                            Toast.makeText(
                                activity,
                                "Invoice has been generated",
                                Toast.LENGTH_LONG
                            ).show()
                            appointmentModel.invoiceUrl =
                                invoiceResponseObject.optString("public_url")
                            val content = SpannableString("View Invoice")
                            content.setSpan(UnderlineSpan(), 0, content.length, 0)
                            itemViewHolder.create_view_invoice_tv.text = content
                        }
                    } else {
                        errorHandler(context, s)
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }
    }


    override fun getItemCount(): Int {
        return appointmentModelList.size
    }

    override fun getItemViewType(position: Int): Int {
        if (isPositionHeader(position)) {
            return TYPE_HEADER
        } else if (isPositionFooter(position)) {
            return TYPE_FOOTER
        }
        return TYPE_ITEM
    }

    fun isPositionHeader(position: Int): Boolean {
        return position == 0
    }

    fun isPositionFooter(position: Int): Boolean {
        return position >= appointmentModelList.size - 1 && appointmentModelList.size >= 20
    }

    class FooterViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var footerText: TextView

        init {
            footerText = view.findViewById(R.id.appt_load_more)
        }
    }

    class AppointmentCalendarListViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        var calendar_patient_number: TextView
        var apptLayout: LinearLayout
        var appt_video: ImageButton
        var appt_video_disable: ImageButton
        var appt_mark_as_complete_calendar: LinearLayout
        var appt_complete: ImageView
        var doctor_join_text: LinearLayout
        var patient_join_text: LinearLayout
        var send_link_patient_text: TextView

        var apptCalendarHeader: TextView
        lateinit var calendarApptTime: TextView
        var calendarPatientName: TextView
        var calendarApptMode: TextView
        var caledarApptType: TextView
        lateinit var caledarApptStatus: TextView
        var apptCalendarPaymentLayout: LinearLayout
        lateinit var apptCalendarItem: LinearLayout
        var appt_calendar_payment: LinearLayout
        var cancel_appt: TextView
        var statusSettlement: TextView
        var apptAction: TextView
        var video_cancel_appt: TextView
        var video_take_note_appt: TextView
        var appointmentTime: TextView
        var appt_complete_payment: LinearLayout
        var appt_close_payment_status_text: TextView
        var refundAmountLable: TextView
        var refundAmountText: TextView
        var settlementPendingLayout: LinearLayout
        var settlementPendingLayoutR: RelativeLayout
        var closePaymentStatus: LinearLayout
        var appt_cancel_single_layout: LinearLayout
        var open_appt_actions: LinearLayout
        var rescheduleAppointmentTime: RelativeLayout
        var external_video_actions_lay: RelativeLayout

        var appointment_time_date: TextView
        var reschedule_appt_time: TextView
        var tvPatientGeneralId: TextView
        var app_time_date_lay: LinearLayout
        var appt_reschedule_layout: LinearLayout
        var create_view_receipt: TextView
        var create_view_invoice_tv: TextView
        var single_reschedule_button: ImageButton
        var ll_parent_notes: LinearLayout? = null

        init {
            tvPatientGeneralId = itemView.findViewById(R.id.tv_patient_general_id)
            apptCalendarHeader = itemView.findViewById(R.id.appt_calendar_item_header)
            calendarPatientName = itemView.findViewById(R.id.calendar_patient_name)
            calendar_patient_number = itemView.findViewById(R.id.patient_number)
            calendarApptMode = itemView.findViewById(R.id.calendar_appt_mode)
            caledarApptType = itemView.findViewById(R.id.calendar_appt_type)
            cancel_appt = itemView.findViewById(R.id.cancel_appt)
            apptCalendarPaymentLayout = itemView.findViewById(R.id.appt_payment)
            appt_calendar_payment = itemView.findViewById(R.id.appt_payment)
            appt_video = itemView.findViewById(R.id.appt_video)
            appt_video_disable = itemView.findViewById(R.id.appt_video_disable)
            appt_cancel_single_layout = itemView.findViewById(R.id.appt_cancel_single_layout)
            refundAmountLable = itemView.findViewById(R.id.refundAmountLable)
            refundAmountText = itemView.findViewById(R.id.refundAmountText)
            appt_complete_payment = itemView.findViewById(R.id.appt_complete_payment)
            video_cancel_appt = itemView.findViewById(R.id.video_cancel_appt)
            video_take_note_appt = itemView.findViewById(R.id.video_take_note_appt)
            appt_close_payment_status_text =
                itemView.findViewById(R.id.appt_close_payment_status_text)
            settlementPendingLayout = itemView.findViewById(R.id.settlementPendingLayout)
            settlementPendingLayoutR = itemView.findViewById(R.id.settlementPendingLayoutR)
            closePaymentStatus = itemView.findViewById(R.id.closePaymentStatus)
            statusSettlement = itemView.findViewById(R.id.settlementStatus)
            open_appt_actions = itemView.findViewById(R.id.open_appt_actions)
            appointmentTime = itemView.findViewById(R.id.appointment_time)
            rescheduleAppointmentTime = itemView.findViewById(R.id.rescheduleAppointmentTime)
            appointment_time_date = itemView.findViewById(R.id.appointment_time_date)
            reschedule_appt_time = itemView.findViewById(R.id.reschedule_appt_time)
            app_time_date_lay = itemView.findViewById(R.id.app_time_date)
            appt_reschedule_layout = itemView.findViewById(R.id.appt_reschedule_layout)
            single_reschedule_button = itemView.findViewById(R.id.single_reschedule_button)
            apptAction = itemView.findViewById(R.id.appt_action)
            create_view_receipt = itemView.findViewById(R.id.create_view_receipt_tv)
            create_view_invoice_tv = itemView.findViewById(R.id.create_view_invoice_tv)
            apptLayout = itemView.findViewById(R.id.appt_layout)
            appt_mark_as_complete_calendar =
                itemView.findViewById(R.id.appt_mark_as_complete_calendar)
            appt_complete = itemView.findViewById(R.id.appt_complete)
            doctor_join_text = itemView.findViewById(R.id.doctor_join_url_button)
            patient_join_text = itemView.findViewById(R.id.patient_join_url)
            send_link_patient_text = itemView.findViewById(R.id.send_join_link_patient)
            external_video_actions_lay = itemView.findViewById(R.id.external_video_actions_lay)
            ll_parent_notes = itemView.findViewById(R.id.ll_parent_notes)
            itemView.setOnClickListener { view -> val cpu = view.tag as AppointmentModel }
        }
    }
}
