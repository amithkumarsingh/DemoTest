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
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
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
import org.json.JSONException
import org.json.JSONObject
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class AppointmentListAdapter(
    private val appointmentModelList: List<AppointmentModel>,
    var activity: Activity,
    var groupData: ArrayList<Int>,
    var apptSize: ArrayList<Int>,
    var context: Context,
    var tabName: String,
    var appointmentClickListener: AppointmentClickListener,
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val appointmentDetailsViewModel: AppointmentDetailsViewModel =
        ViewModelProvider((activity as ViewModelStoreOwner)).get(
            AppointmentDetailsViewModel::class.java
        )
    private val globalClass: MyClinicGlobalClass
    private val patientRecordsApi: PatientRecordsApi = PatientRecordsApi()
    private var orderUserIdData = 0
    private val appUtilities: AppUtilities = AppUtilities()
    private var otpLoading: ProgressDialog? = null
    private val apiMethodCalls = ApiMethodCalls()
    private val globalApiCall: ApiGetPostMethodCalls
    lateinit var footerHolder: FooterViewHolder
    private var sharedPref: SharedPref? = null

    init {
        appointmentDetailsViewModel.init()
        globalClass = context.applicationContext as MyClinicGlobalClass
        globalApiCall = ApiGetPostMethodCalls()
        sharedPref = SharedPref(activity)
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.appointment_item, viewGroup, false)
        context = viewGroup.context
        return AppointmentListViewHolder(view)
    }

    @SuppressLint("SetTextI18n", "SuspiciousIndentation")
    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        @SuppressLint("RecyclerView") position: Int,
    ) {
        holder.itemView.tag = appointmentModelList[position]
        if (holder is AppointmentListViewHolder) {
            try {
                val appointmentApptListModel = appointmentModelList[position]
                val rescheduleJsonObject = JSONObject()
                rescheduleJsonObject.put("JSONARRAY", appointmentApptListModel.rescheduleJsonArray)
                //Log.i("name", appointmentApptListModel.getPatientName()+" "+appointmentApptListModel.getApptTime());
                orderUserIdData = appointmentApptListModel.orderUserId
                if (groupData[position] == 0) {
                        holder.apptHeader.visibility = View.VISIBLE
                        val date = appUtilities.changeDateFormat(
                            "yyyy-MM-dd",
                            "MMM dd, yyyy",
                            appointmentApptListModel.apptDate
                        )
                    val c = Calendar.getInstance().time
                    val df = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
                    val formattedDate = df.format(c)
                    if(date==formattedDate){
                        holder.apptItemTitle.text = "Today's" + " - " + apptSize[position] + " Appts"
                    }else{
                        holder.apptItemTitle.text = date + " - " + apptSize[position] + " Appts"
                    }
                } else {
                    holder.apptHeader.visibility = View.GONE
                }
                holder.patientName.text = appointmentApptListModel.patientName
                /*New Registration(Autogenerated ID) changes for Gastro interface*/
               // if (sharedPref!!.getPref("is_show_general_id", "").equals("1", ignoreCase = true)) {

                    if (appointmentApptListModel.generalID != null && !appointmentApptListModel.generalID.equals(
                            "",
                            ignoreCase = true
                        )
                    ) {
                        holder.tvPatientGeneralId.visibility = View.VISIBLE
                        holder.tvPatientGeneralId.text = appointmentApptListModel.generalID
                    } else {
                        holder.tvPatientGeneralId.visibility = View.GONE
                    }
               /* } else {
                    holder.tvPatientGeneralId.visibility = View.GONE
                }*/
                holder.patient_number.text = appointmentApptListModel.patientPhoneNumber
                if (appointmentApptListModel.is_reschedule_active == 0) {
                    holder.appt_reschedule_layout.alpha = .4f
                } else {
                    holder.appt_reschedule_layout.alpha = 1f
                }
                holder.appt_reschedule_layout.setOnClickListener(View.OnClickListener { view ->
                    if (appointmentApptListModel.is_reschedule_active == 0) {
                        return@OnClickListener
                    } else {
                        appointmentClickListener.onItemClick(
                            view,
                            "Reschedule Appt",
                            appointmentApptListModel,
                            position
                        )
                    }
                })
                holder.single_reschedule_button.setOnClickListener { view: View? ->
                    if (appointmentApptListModel.is_reschedule_active == 0) {
                        return@setOnClickListener
                    } else {
                        appointmentClickListener.onItemClick(
                            view,
                            "Reschedule Appt",
                            appointmentApptListModel,
                            position
                        )
                    }
                }
                if (appointmentApptListModel.rescheduleJsonArray.length() > 0) {
                    holder.rescheduleAppointmentTime.visibility = View.VISIBLE
                    holder.reschedule_appt_time.visibility = View.VISIBLE
                    holder.app_time_date_lay.visibility = View.GONE
                    holder.appointment_time_date.visibility = View.GONE
                    holder.reschedule_appt_time.text =
                        convertDatetoMonthFormate(appointmentApptListModel.scheduleTime)
                } else {
                    holder.rescheduleAppointmentTime.visibility = View.GONE
                    holder.reschedule_appt_time.visibility = View.GONE
                    holder.app_time_date_lay.visibility = View.VISIBLE
                    holder.appointment_time_date.visibility = View.VISIBLE
                    holder.appointment_time_date.text =
                        convertDatetoMonthFormate(appointmentApptListModel.scheduleTime)
                }
                if (!appointmentApptListModel.paymentStatus.equals("success", ignoreCase = true)) {
                    holder.apptPayment.visibility = View.VISIBLE
                    holder.open_appt_actions.visibility = View.VISIBLE
                } else {
                    holder.apptPayment.visibility = View.GONE
                    holder.open_appt_actions.visibility = View.GONE
                }
                holder.appointmentAt.text = appointmentApptListModel.clinicName
                holder.appointmentTime.text =
                    "@ " + appUtilities.formatTimeBasedOnPreference(
                        context,
                        "HH:mm:ss",
                        appointmentApptListModel.apptTime
                    )
                when (appointmentApptListModel.apptType) {
                    1 -> {
                        holder.apptType.text = "Routine"
                    }
                    2 -> {
                        holder.apptType.text = "Follow-up"
                    }
                    3 -> {
                        holder.apptType.text = "Procedure/Vaccination"
                    }
                    4 -> {
                        holder.apptType.text = "Dressing/Plaster"
                    }
                    5 -> {
                        holder.apptType.text = "Other"
                    }
                    6 -> {
                        holder.apptType.text = "First Visit"
                    }
                    else -> {
                        holder.apptType.text = "Not Set"
                    }
                }
                if (appointmentApptListModel.refundStatus == 0 && appointmentApptListModel.isDoAutoRefund == 1) {
                    holder.settlementPendingLayout.visibility = View.VISIBLE
                    holder.statusSettlement.text = "Cancel Refund"
                    holder.statusSettlement.setTextColor(
                        ContextCompat.getColor(
                            activity,
                            R.color.colorDanger
                        )
                    )
                    holder.settlementPendingLayoutR.backgroundTintList =
                        ContextCompat.getColorStateList(activity, R.color.colorDanger)
                } else if (!appointmentApptListModel.payment_title.isEmpty()) {
                    holder.statusSettlement.text = appointmentApptListModel.payment_title
                    if (appointmentApptListModel.payment_title_color.equals(
                            "yellow",
                            ignoreCase = true
                        )
                    ) {
                        holder.statusSettlement.setTextColor(
                            ContextCompat.getColor(
                                activity,
                                R.color.colorDarkInfo
                            )
                        )
                        holder.settlementPendingLayoutR.backgroundTintList =
                            ContextCompat.getColorStateList(activity, R.color.colorDarkInfo)
                    } else if (appointmentApptListModel.payment_title_color.equals(
                            "green",
                            ignoreCase = true
                        )
                    ) {
                        holder.statusSettlement.setTextColor(
                            ContextCompat.getColor(
                                activity,
                                R.color.colorSuccess
                            )
                        )
                        holder.settlementPendingLayoutR.backgroundTintList =
                            ContextCompat.getColorStateList(activity, R.color.colorSuccess)
                    } else if (appointmentApptListModel.payment_title_color.equals(
                            "blue",
                            ignoreCase = true
                        )
                    ) {
                        holder.statusSettlement.setTextColor(
                            ContextCompat.getColor(
                                activity,
                                R.color.colorAccent
                            )
                        )
                        holder.settlementPendingLayoutR.backgroundTintList =
                            ContextCompat.getColorStateList(activity, R.color.colorAccent)
                    } else if (appointmentApptListModel.payment_title_color.equals(
                            "red",
                            ignoreCase = true
                        )
                    ) {
                        holder.statusSettlement.setTextColor(
                            ContextCompat.getColor(
                                activity,
                                R.color.colorDanger
                            )
                        )
                        holder.settlementPendingLayoutR.backgroundTintList =
                            ContextCompat.getColorStateList(activity, R.color.colorDanger)
                    }
                    holder.settlementPendingLayout.visibility = View.VISIBLE
                } else {
                    holder.settlementPendingLayout.visibility = View.GONE
                }
                holder.apptPayment.setOnClickListener { view ->
                    appointmentClickListener.onItemClick(
                        view,
                        "payment",
                        appointmentApptListModel,
                        position
                    )
                    MyClinicGlobalClass.logUserActionEvent(
                        ApiUrls.doctorId,
                        context.getString(R.string.AppointmentsListIncompletePaymentDetails),
                        null
                    )
                }
                holder.settlementPendingLayout.setOnClickListener { view ->
                    if (holder.statusSettlement.text.toString()
                            .equals("Cancel Refund", ignoreCase = true)
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
                }
                if (appointmentApptListModel.activePast == 2) {
                    holder.apptCancelLayout.visibility = View.GONE
                    holder.apptRunningLateLayout.visibility = View.GONE
                    holder.cancel_appt.visibility = View.GONE
                    holder.video_cancel_appt.visibility = View.GONE
                    holder.closed_take_note.visibility = View.GONE
                    holder.closed_take_note.text = Html.fromHtml("<u>Take Notes</u>")
                    holder.apptAction.visibility = View.VISIBLE
                    holder.closed_take_note.setTextColor(
                        ContextCompat.getColor(
                            activity,
                            R.color.colorAccent
                        )
                    )
                    holder.appt_join_video.visibility = View.GONE
                    holder.appt_video_disable.visibility = View.GONE
                    holder.external_video_actions_lay.visibility = View.GONE
                    holder.apptPayment.visibility =
                        View.GONE //set gone while doing defered payment module
                    holder.open_appt_actions.visibility = View.GONE
                    holder.appt_close_payment_status_text.visibility = View.VISIBLE
                    holder.closePaymentStatus.visibility = View.VISIBLE
                    if (appointmentApptListModel.refundAmt > 0) {
                        if (holder.statusSettlement.text.toString()
                                .equals("Cancel Refund", ignoreCase = true)
                        ) {
                            holder.refundAmountLable.visibility = View.GONE
                            holder.refundAmountText.visibility = View.GONE
                            holder.refundAmountLable.text = "Refund Scheduled on"
                            holder.refundAmountText.text =
                                "" + appointmentApptListModel.refundScheduledOn
                            holder.refundAmountText.setTextColor(
                                ContextCompat.getColor(
                                    activity,
                                    R.color.colorInfo
                                )
                            )
                        } else {
                            holder.refundAmountLable.visibility = View.GONE
                            holder.refundAmountText.visibility = View.GONE
                            holder.refundAmountLable.text = "Refund Amount"
                            holder.refundAmountText.text =
                                "Rs." + appointmentApptListModel.refundAmt
                            holder.refundAmountText.setTextColor(
                                ContextCompat.getColor(
                                    activity,
                                    R.color.colorBlack
                                )
                            )
                        }
                    } else {
                        holder.refundAmountLable.visibility = View.GONE
                        holder.refundAmountText.visibility = View.GONE
                    }
                    if (appointmentApptListModel.paymentStatus.equals(
                            "Pending",
                            ignoreCase = true
                        )
                    ) {
                        holder.apptPayment.background.setColorFilter(
                            Color.parseColor("#EB0000"),
                            PorterDuff.Mode.SRC_ATOP
                        )
                    } else {
                        holder.apptPayment.background.setColorFilter(
                            Color.parseColor("#008000"),
                            PorterDuff.Mode.SRC_ATOP
                        )
                    }
                    when (appointmentApptListModel.apptStatus) {
                        3 -> {
                            holder.appt_close_payment_status_text.text = "Consulted"
                            holder.appt_close_payment_status_text.setTextColor(
                                ContextCompat.getColor(
                                    activity,
                                    R.color.colorPrimary
                                )
                            )
                        }
                        4 -> {
                            holder.appt_close_payment_status_text.text = "Cancelled"
                            holder.appt_close_payment_status_text.setTextColor(
                                ContextCompat.getColor(
                                    activity,
                                    R.color.colorDanger
                                )
                            )
                        }
                        5 -> {
                            holder.appt_close_payment_status_text.text =
                                "Cancelled by patient"
                            holder.appt_close_payment_status_text.setTextColor(
                                ContextCompat.getColor(
                                    activity,
                                    R.color.colorDanger
                                )
                            )
                        }
                        6 -> {
                            holder.appt_close_payment_status_text.text = "Patient no show"
                            holder.appt_close_payment_status_text.setTextColor(
                                ContextCompat.getColor(
                                    activity,
                                    R.color.colorDanger
                                )
                            )
                        }
                        7 -> {
                            holder.appt_close_payment_status_text.text = "Doctor no-show"
                            holder.appt_close_payment_status_text.setTextColor(
                                ContextCompat.getColor(
                                    activity,
                                    R.color.colorDanger
                                )
                            )
                        }
                        8 -> {
                            holder.appt_close_payment_status_text.text =
                                "Cancelled by doctor"
                            holder.appt_close_payment_status_text.setTextColor(
                                ContextCompat.getColor(
                                    activity,
                                    R.color.colorDanger
                                )
                            )
                        }
                    }
                } else {
                    holder.apptCancelLayout.visibility = View.VISIBLE
                    holder.apptRunningLateLayout.visibility = View.VISIBLE
                    holder.apptAction.visibility = View.VISIBLE
                    if (appointmentApptListModel.apptMode == 3) {
                        holder.apptAction.text = Html.fromHtml("<u>Take Notes</u>")
                        holder.apptAction.setTextColor(
                            ContextCompat.getColor(
                                activity,
                                R.color.colorAccent
                            )
                        )
                        holder.video_cancel_appt.visibility = View.GONE
                        holder.cancel_appt.visibility = View.VISIBLE
                        holder.cancel_appt.text = Html.fromHtml("<u>Cancel Appt</u>")
                        holder.appt_join_video.visibility = View.GONE
                        holder.appt_video_disable.visibility = View.GONE
                        holder.external_video_actions_lay.visibility = View.GONE
                        holder.appt_video.visibility = View.GONE
                        holder.appt_mark_as_complete.visibility = View.VISIBLE
                        holder.apptPayment.visibility =
                            View.VISIBLE //in defered payment change it as visible
                        holder.open_appt_actions.visibility = View.VISIBLE
                        if (appointmentApptListModel.paymentStatus.equals(
                                "Pending",
                                ignoreCase = true
                            )
                        ) {
                            holder.apptPayment.background.setColorFilter(
                                Color.parseColor("#EB0000"),
                                PorterDuff.Mode.SRC_ATOP
                            )
                        } else {
                            holder.apptPayment.background.setColorFilter(
                                Color.parseColor("#008000"),
                                PorterDuff.Mode.SRC_ATOP
                            )
                        }
                        holder.appt_close_payment_status_text.visibility = View.GONE
                        holder.closePaymentStatus.visibility = View.GONE
                        holder.settlementPendingLayout.visibility = View.GONE
                        holder.refundAmountLable.visibility = View.GONE
                        holder.refundAmountText.visibility = View.GONE
                    } else if (appointmentApptListModel.apptMode == 1) {
                        holder.apptAction.text = Html.fromHtml("<u>Take Notes</u>")
                        holder.apptAction.setTextColor(
                            ContextCompat.getColor(
                                activity,
                                R.color.colorAccent
                            )
                        )
                        holder.video_cancel_appt.visibility = View.VISIBLE
                        holder.video_cancel_appt.text = Html.fromHtml("<u>Cancel Appt</u>")

                        //itemViewHolder.video_take_note_appt.setVisibility(View.VISIBLE);
                        holder.cancel_appt.visibility = View.GONE
                        holder.appt_join_video.visibility = View.VISIBLE
                        holder.appt_video.visibility = View.VISIBLE
                        if (appointmentApptListModel.video_tool > 0) {
                            if (appointmentApptListModel.video_tool == 1) {
                                holder.appt_join_video.visibility = View.VISIBLE
                                holder.appt_video_disable.visibility = View.GONE
                                holder.appt_video.visibility = View.VISIBLE
                                holder.external_video_actions_lay.visibility = View.VISIBLE
                                holder.doctor_join_text.visibility = View.GONE
                                holder.patient_join_text.visibility = View.GONE
                                holder.send_link_patient_text.visibility = View.VISIBLE
                            } else {
                                if (!appointmentApptListModel.doctor_join_external_url.equals(
                                        "null",
                                        ignoreCase = true
                                    )
                                ) {
                                    holder.appt_join_video.visibility = View.VISIBLE
                                    holder.appt_video.visibility = View.VISIBLE
                                    holder.appt_video_disable.visibility = View.GONE
                                    holder.external_video_actions_lay.visibility =
                                        View.VISIBLE
                                    holder.doctor_join_text.visibility = View.VISIBLE
                                    holder.patient_join_text.visibility = View.VISIBLE
                                    holder.send_link_patient_text.visibility = View.VISIBLE
                                } else {
                                    holder.appt_join_video.visibility = View.GONE
                                    holder.appt_video_disable.visibility = View.VISIBLE
                                    holder.external_video_actions_lay.visibility =
                                        View.VISIBLE
                                    holder.doctor_join_text.visibility = View.VISIBLE
                                    holder.patient_join_text.visibility = View.VISIBLE
                                    holder.send_link_patient_text.visibility = View.VISIBLE
                                }
                            }
                        } else {
                            holder.appt_join_video.visibility = View.GONE
                            holder.appt_video_disable.visibility = View.VISIBLE
                            holder.external_video_actions_lay.visibility = View.GONE
                        }
                        holder.appt_mark_as_complete.visibility = View.VISIBLE
                        holder.apptPayment.visibility = View.VISIBLE
                        holder.open_appt_actions.visibility = View.VISIBLE
                        if (appointmentApptListModel.paymentStatus.equals(
                                "Pending",
                                ignoreCase = true
                            )
                        ) {
                            holder.apptPayment.background.setColorFilter(
                                Color.parseColor("#EB0000"),
                                PorterDuff.Mode.SRC_ATOP
                            )
                        } else {
                            holder.apptPayment.background.setColorFilter(
                                Color.parseColor("#008000"),
                                PorterDuff.Mode.SRC_ATOP
                            )
                        }
                        holder.appt_close_payment_status_text.visibility = View.GONE
                        holder.closePaymentStatus.visibility = View.GONE
                        holder.settlementPendingLayout.visibility = View.GONE
                        holder.refundAmountLable.visibility = View.GONE
                        holder.refundAmountText.visibility = View.GONE
                    }
                }
                holder.create_view_receipt.setTextColor(
                    ContextCompat.getColor(
                        activity,
                        R.color.colorPrimary
                    )
                )
                holder.create_view_receipt.visibility = View.VISIBLE
                if (appointmentApptListModel.receiptUrl === "") {
                    val content = SpannableString("Create Receipt")
                    content.setSpan(UnderlineSpan(), 0, content.length, 0)
                    holder.create_view_receipt.text = content
                } else {
                    val content = SpannableString("View Receipt")
                    content.setSpan(UnderlineSpan(), 0, content.length, 0)
                    holder.create_view_receipt.text = content
                }
                if (sharedPref!!.isPrefExists("EMR")) {
                    holder.apptAction.visibility = View.VISIBLE
                } else {
                    holder.apptAction.visibility = View.GONE
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
                        holder.create_view_invoice_tv.visibility = View.VISIBLE
                        if (appointmentApptListModel.invoiceUrl == null) {
                            val content = SpannableString("Create Invoice")
                            content.setSpan(UnderlineSpan(), 0, content.length, 0)
                            holder.create_view_invoice_tv.text = content
                        } else {
                            val content = SpannableString("View Invoice")
                            content.setSpan(UnderlineSpan(), 0, content.length, 0)
                            holder.create_view_invoice_tv.text = content
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
                        holder.create_view_invoice_tv.visibility = View.VISIBLE
                        if (appointmentApptListModel.invoiceUrl == null) {
                            val content = SpannableString("Create Invoice")
                            content.setSpan(UnderlineSpan(), 0, content.length, 0)
                            holder.create_view_invoice_tv.text = content
                        } else {
                            val content = SpannableString("View Invoice")
                            content.setSpan(UnderlineSpan(), 0, content.length, 0)
                            holder.create_view_invoice_tv.text = content
                        }
                    }
                } else {
                    holder.create_view_invoice_tv.visibility = View.GONE
                }
                holder.create_view_invoice_tv.setOnClickListener { view: View? ->
                    if (holder.create_view_invoice_tv.text.toString()
                            .equals("View Invoice", ignoreCase = true)
                    ) {
                        getFileFromUrl(appointmentApptListModel.invoiceUrl)
                    } else {
                        appointmentClickListener.onItemClick(
                            view,
                            "apptDetails",
                            appointmentApptListModel,
                            position
                        )
                    }
                }
                holder.apptRunningLate.setOnClickListener { view ->
                    appointmentClickListener.onItemClick(
                        view,
                        "IamLate",
                        appointmentApptListModel,
                        position
                    )
                    MyClinicGlobalClass.logUserActionEvent(
                        ApiUrls.doctorId,
                        context.getString(R.string.AppointmentsListDelayIntimation),
                        null
                    )
                }

                if (holder.create_view_invoice_tv.visibility == View.VISIBLE || holder.create_view_receipt.visibility == View.VISIBLE
                    || holder.apptAction.visibility == View.VISIBLE
                ) {
                    holder.ll_parent_notes!!.visibility = View.VISIBLE
                } else {
                    holder.ll_parent_notes!!.visibility = View.GONE
                }


                holder.create_view_receipt.setOnClickListener { view: View? ->
                    if (holder.create_view_receipt.text.toString()
                            .equals("Create Receipt", ignoreCase = true)
                    ) {
                        if (globalClass.isOnline) {
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
                                dailogArticleCancel.setOnClickListener { view12: View? -> dialog.dismiss() }
                                close_textView.setOnClickListener { view13: View? -> dialog.dismiss() }
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
                                           /* Toast.makeText(context, error, Toast.LENGTH_SHORT)
                                                .show()*/
                                            errorHandler(context,error)
                                        }
                                    })
                                val createReceiptText =
                                    dialog.findViewById<View>(R.id.createReceiptText) as TextView
                                createReceiptText.setOnClickListener { view14: View? ->
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
                                                holder,
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
                                        context,
                                        android.R.layout.simple_spinner_item,
                                        paymentMode
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
                                    holder
                                )
                            }
                        } else {
                            globalClass.noInternetConnection.showDialog(activity)
                        }
                    } else if (holder.create_view_receipt.text.toString()
                            .equals("View Receipt", ignoreCase = true)
                    ) {
                        getReceiptUrl(appointmentApptListModel.receiptUrl)
                    }
                }
                holder.apptCancel.setOnClickListener { view ->
                    appointmentClickListener.onItemClick(
                        view,
                        "cancel",
                        appointmentApptListModel,
                        position
                    )
                    MyClinicGlobalClass.logUserActionEvent(
                        ApiUrls.doctorId,
                        context.getString(R.string.AppointmentsListCancelAppointment),
                        null
                    )
                }
                holder.appt_cancel_single_layout.setOnClickListener { view ->
                    appointmentClickListener.onItemClick(
                        view,
                        "canAppt",
                        appointmentApptListModel,
                        position
                    )
                }
                holder.closed_take_note.setOnClickListener { view ->
                    if (holder.closed_take_note.text.toString()
                            .equals("Take Notes", ignoreCase = true)
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
                }
                holder.appt_mark_as_complete.setOnClickListener { view: View? ->
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
                holder.appt_complete.setOnClickListener { view: View? ->
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
                holder.appt_video.setOnClickListener { view: View? ->
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
                holder.appt_video_disable.setOnClickListener {
                    Toast.makeText(
                        context,
                        "Video Link Not Updated",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                holder.doctor_join_text.setOnClickListener { view: View? ->
                    appointmentClickListener.onItemClick(
                        view,
                        "setUp doctor video link",
                        appointmentApptListModel,
                        position
                    )
                }
                holder.patient_join_text.setOnClickListener { view: View? ->
                    appointmentClickListener.onItemClick(
                        view,
                        "setUp patient video link",
                        appointmentApptListModel,
                        position
                    )
                }
                holder.send_link_patient_text.setOnClickListener { view: View? ->
                    appointmentClickListener.onItemClick(
                        view,
                        "send video link",
                        appointmentApptListModel,
                        position
                    )
                }
                holder.apptLayout.setOnClickListener { view ->
                    appointmentClickListener.onItemClick(
                        view,
                        "apptDetails",
                        appointmentApptListModel,
                        position
                    )
                    MyClinicGlobalClass.logUserActionEvent(
                        ApiUrls.doctorId,
                        context.getString(R.string.Appointments_listView),
                        null
                    )
                }
                holder.apptAction.setOnClickListener { view ->
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
                holder.cancel_appt.setOnClickListener { view ->
                    appointmentClickListener.onItemClick(
                        view,
                        "canAppt",
                        appointmentApptListModel,
                        position
                    )
                }
                holder.rescheduleAppointmentTime.setOnClickListener { view: View? ->
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
                    dailogArticleCancel.setOnClickListener { view1: View? -> dialog.dismiss() }
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
                holder.footerText.visibility =
                    View.GONE
            } else {
                holder.footerText.visibility =
                    View.GONE
            }
            footerHolder.footerText.setOnClickListener { view ->
                appointmentClickListener.onItemClick(
                    view,
                    "loadMore",
                    null,
                    position
                )
            }
        }
    }

    fun updatePaymentStatus(
        paidAmount: String,
        orderId: Int,
        paymentMode: String?,
        isGeneratedReceipt: Boolean,
        itemViewHolder: AppointmentListViewHolder,
        appointmentApptListModel: AppointmentModel,
        dialog: Dialog,
    ) {
        otpLoading = ProgressDialog(context)
        otpLoading!!.setMessage(context.resources.getString(R.string.wait_while_we_updating))
        otpLoading!!.setTitle(context.resources.getString(R.string.updating))
        otpLoading!!.setProgressStyle(ProgressDialog.STYLE_SPINNER)
        otpLoading!!.setCancelable(false)
        otpLoading!!.show()
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
                        otpLoading!!.dismiss()
                        dialog.dismiss()
                        val rootObj = response.getJSONObject("response")
                        if (isGeneratedReceipt) {
                            val intervention = rootObj["receipt"]
                            if (intervention is JSONObject) {
                                // It's an object
                                val receiptObject = rootObj.getJSONObject("receipt")
                                val receiptUrl = receiptObject.getString("public_url")
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
                        otpLoading!!.dismiss()
                        e.printStackTrace()
                    }
                }

                override fun onError(err: String) {
                    otpLoading!!.dismiss()
                    errorHandler(activity, err)
                }
            })
    }

    fun getFileFromUrl(fileUrl: String?) {
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

    private fun getReceiptUrl(receiptUrl: String) {
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

    private fun createReceipt(
        orderId: Int,
        appointmentApptListModel: AppointmentModel,
        itemViewHolder: AppointmentListViewHolder,
    ) {
        val url: String = ApiUrls.createReceipt + "?order_id=" + orderId
        otpLoading = ProgressDialog(activity)
        otpLoading!!.setMessage(activity.resources.getString(R.string.please_wait))
        otpLoading!!.setProgressStyle(ProgressDialog.STYLE_SPINNER)
        otpLoading!!.setCancelable(false)
        otpLoading!!.show()
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
                        val content = SpannableString(showReceipt)
                        content.setSpan(UnderlineSpan(), 0, content.length, 0)
                        itemViewHolder.create_view_receipt.text = content
                        otpLoading!!.dismiss()
                    } catch (e: Exception) {
                        otpLoading!!.dismiss()
                        e.printStackTrace()
                    }
                }

                override fun onError(err: String) {
                    otpLoading!!.dismiss()
                    errorHandler(activity, err)
                }
            })
    }

    fun trimMessage(json: String, key: String): String? {
        var trimmedString: String? = try {
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
            val sdf = SimpleDateFormat("yyyy-MM-dd")
            val splitDateTime = scheduleTime.split(" ").toTypedArray()
            val dateString = splitDateTime[0]
            val dateSplitedString = dateString.split("-").toTypedArray()
            val time = splitDateTime[1]
            val date = sdf.parse(dateString)
            val month_name = month_date.format(date)
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

    override fun getItemCount(): Int {
        return appointmentModelList.size
    }

    inner class FooterViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var footerText: TextView

        init {
            footerText = view.findViewById(R.id.appt_load_more)
        }
    }

    inner class AppointmentListViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        var appt_reschedule_layout: LinearLayout
        var reschedule_appt_time: TextView
        var appointment_time_date: TextView
        var open_appt_actions: LinearLayout
        var appt_cancel_single_layout: LinearLayout
        var single_reschedule_button: ImageView
        var appt_video: ImageButton
        var appt_video_disable: ImageButton
        var apptAction: TextView
        var appt_mark_as_complete: LinearLayout
        var doctor_join_text: LinearLayout
        var patient_join_text: LinearLayout
        var appt_complete: ImageView
        var send_link_patient_text: TextView
        var patient_number: TextView
        var create_view_receipt: TextView
        var create_view_invoice_tv: TextView
        var app_time_date_lay: LinearLayout
        var apptItemTitle: TextView
        var patientName: TextView
        var appointmentAt: TextView
        var appointmentTime: TextView
        var apptType: TextView
        var apptPayment: LinearLayout
        var apptLayout: LinearLayout
        var apptRunningLateLayout: LinearLayout
        var apptCancelLayout: LinearLayout
        var apptRunningLate: ImageButton
        var apptCancel: ImageButton
        var apptHeader: RelativeLayout
        var rescheduleAppointmentTime: RelativeLayout
        var external_video_actions_lay: RelativeLayout
        var cancel_appt: TextView
        var statusSettlement: TextView
        var video_cancel_appt: TextView
        var video_take_note_appt: TextView? = null
        var closed_take_note: TextView
        var tvPatientGeneralId: TextView
        var appt_join_video: LinearLayout
        var appt_close_payment_status_text: TextView
        var refundAmountLable: TextView
        var refundAmountText: TextView
        var settlementPendingLayout: LinearLayout
        var settlementPendingLayoutR: RelativeLayout
        var closePaymentStatus: LinearLayout
        var sepratorLayout: View? = null
        var ll_parent_notes: LinearLayout? = null

        init {
            tvPatientGeneralId = itemView.findViewById(R.id.tv_patient_general_id)
            apptItemTitle = itemView.findViewById(R.id.appointment_title)
            patientName = itemView.findViewById(R.id.patient_name)
            patient_number = itemView.findViewById(R.id.patient_number)
            appointmentAt = itemView.findViewById(R.id.appointment_at)
            appointmentTime = itemView.findViewById(R.id.appointment_time)
            apptType = itemView.findViewById(R.id.appt_type)
            cancel_appt = itemView.findViewById(R.id.cancel_appt)
            apptAction = itemView.findViewById(R.id.appt_action)
            apptCancel = itemView.findViewById(R.id.appt_cancel)
            apptRunningLate = itemView.findViewById(R.id.appt_running_late)
            apptPayment = itemView.findViewById(R.id.appt_payment)
            open_appt_actions = itemView.findViewById(R.id.open_appt_actions)
            apptLayout = itemView.findViewById(R.id.appt_layout)
            apptHeader = itemView.findViewById(R.id.appt_item_header)
            apptRunningLateLayout = itemView.findViewById(R.id.appt_running_late_layout)
            apptCancelLayout = itemView.findViewById(R.id.appt_cancel_layout)
            appt_cancel_single_layout = itemView.findViewById(R.id.appt_cancel_single_layout)
            refundAmountLable = itemView.findViewById(R.id.refundAmountLable)
            refundAmountText = itemView.findViewById(R.id.refundAmountText)
            appt_video = itemView.findViewById(R.id.appt_video)
            appt_join_video = itemView.findViewById(R.id.appt_join_video)
            appt_video_disable = itemView.findViewById(R.id.appt_video_disable)
            doctor_join_text = itemView.findViewById(R.id.doctor_join_url_button)
            patient_join_text = itemView.findViewById(R.id.patient_join_url)
            send_link_patient_text = itemView.findViewById(R.id.send_join_link_patient)
            external_video_actions_lay = itemView.findViewById(R.id.external_video_actions_lay)
            appt_complete = itemView.findViewById(R.id.appt_complete)
            video_cancel_appt = itemView.findViewById(R.id.video_cancel_appt)
            closed_take_note = itemView.findViewById(R.id.appt_closed_take_notes)
            appt_close_payment_status_text =
                itemView.findViewById(R.id.appt_close_payment_status_text)
            settlementPendingLayout = itemView.findViewById(R.id.settlementPendingLayout)
            settlementPendingLayoutR = itemView.findViewById(R.id.settlementPendingLayoutR)
            closePaymentStatus = itemView.findViewById(R.id.closePaymentStatus)
            statusSettlement = itemView.findViewById(R.id.settlementStatus)
            appt_reschedule_layout = itemView.findViewById(R.id.appt_reschedule_layout)
            single_reschedule_button = itemView.findViewById(R.id.single_reschedule_button)
            rescheduleAppointmentTime = itemView.findViewById(R.id.rescheduleAppointmentTime)
            reschedule_appt_time = itemView.findViewById(R.id.reschedule_appt_time)
            app_time_date_lay = itemView.findViewById(R.id.app_time_date)
            appointment_time_date = itemView.findViewById(R.id.appointment_time_date)
            create_view_receipt = itemView.findViewById(R.id.create_view_receipt_tv)
            create_view_invoice_tv = itemView.findViewById(R.id.create_view_invoice_tv)
            appt_mark_as_complete = itemView.findViewById(R.id.mark_as_complete)
            sepratorLayout = itemView.findViewById(R.id.sepratorLayout)
            ll_parent_notes = itemView.findViewById(R.id.ll_parent_notes)
            itemView.setOnClickListener { view -> val cpu = view.tag as AppointmentModel }
        }
    }
}