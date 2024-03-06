package com.whitecoats.clinicplus.fragments

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.graphics.PorterDuff
import android.view.View
import android.widget.*
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.BottomSheetCallback
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.whitecoats.clinicplus.AppointmentApiRequests
import com.whitecoats.clinicplus.R
import com.whitecoats.clinicplus.VolleyCallback
import com.whitecoats.clinicplus.activities.OrderDetailsActivity
import com.whitecoats.clinicplus.apis.ApiUrls
import com.whitecoats.clinicplus.utils.ErrorHandlerClass.errorHandler
import com.whitecoats.clinicplus.viewmodels.DashboardViewModel
import com.whitecoats.model.AddPatientModel
import org.json.JSONException
import org.json.JSONObject

/**
 * Created by vaibhav on 07-02-2018.
 */
class OrderDetailsRefundNowBottomSheet : BottomSheetDialogFragment() {
    private var orderDetailsActivity: OrderDetailsActivity? = null
    private lateinit var cancelIcon: ImageView
    private var appointmentStatusValue: TextView? = null
    private var paymentStatusValue: TextView? = null
    private var amountPaidValue: TextView? = null
    private var refundAmountEditText: TextView? = null
    private var yes: RadioButton? = null
    private var no: RadioButton? = null
    private var refundNowLayout: RelativeLayout? = null
    private var activeConsultationCloseButtonLayout: RelativeLayout? = null
    private val bottomSheetMessageText: EditText? = null
    private var jsonValue: JSONObject? = null
    var addInterfaceModelList: List<AddPatientModel>? = null
    private val interFaceId = 0
    private val hasInterface = false
    private val dashboardViewModel: DashboardViewModel? = null
    private var infoTypeText: String? = null
    private var appointmentApiRequests: AppointmentApiRequests? = null
    private lateinit var mContext:Context
    fun setupConfig(
        orderDetailsActivity: OrderDetailsActivity?,
        infoType: String?,
        mContext: Context
    ) {
        this.orderDetailsActivity = orderDetailsActivity
        addInterfaceModelList = ArrayList()
        infoTypeText = infoType
        appointmentApiRequests = AppointmentApiRequests()
        this.mContext=mContext

    }

    //Bottom Sheet Callback
    private val mBottomSheetBehaviorCallback: BottomSheetCallback = object : BottomSheetCallback() {
        override fun onStateChanged(bottomSheet: View, newState: Int) {
            if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                dismiss()
            }
        }

        override fun onSlide(bottomSheet: View, slideOffset: Float) {}
    }

    @SuppressLint("RestrictedApi")
    override fun setupDialog(dialog: Dialog, style: Int) {
//        super.setupDialog(dialog, style);
        //Get the content View
        val contentView =
            View.inflate(context, R.layout.fragment_bottom_sheet_order_details_cancel_refund, null)
        dialog.setContentView(contentView)
        val params = (contentView.parent as View).layoutParams as CoordinatorLayout.LayoutParams
        val behavior = params.behavior
        cancelIcon = contentView.findViewById(R.id.refundNowCancelIcon)
        appointmentStatusValue = contentView.findViewById(R.id.appointmentStatusValue)
        paymentStatusValue = contentView.findViewById(R.id.paymentStatusValue)
        amountPaidValue = contentView.findViewById(R.id.amountPaidValue)
        refundAmountEditText = contentView.findViewById(R.id.refundAmountEditText)
        yes = contentView.findViewById(R.id.yes)
        no = contentView.findViewById(R.id.no)
        refundNowLayout = contentView.findViewById(R.id.refundNowLayout)
        activeConsultationCloseButtonLayout =
            contentView.findViewById(R.id.activeConsultationCloseButtonLayout)
        triggerMessageLayout = contentView.findViewById(R.id.triggerMessageLayout)
        refundNowViewLayout = contentView.findViewById(R.id.refundNowViewLayout)
        CloseButtonLayout = contentView.findViewById(R.id.CloseButtonLayout)
        appointmentStatusValue?.text = OrderDetailsActivity.apptStatus
        paymentStatusValue?.text = OrderDetailsActivity.paymentStatus
        refundAmountEditText?.text = "" + OrderDetailsActivity.refundAmount
        amountPaidValue?.text = "Rs. " + OrderDetailsActivity.netPaidAmount
        yes?.isChecked = true
        yes?.setOnClickListener(View.OnClickListener {
            no?.isChecked = false
            refundNowLayout?.visibility = View.VISIBLE
            refundAmountEditText?.isEnabled = true
            activeConsultationCloseButtonLayout?.setVisibility(View.GONE)
        })
        no?.setOnClickListener(View.OnClickListener {
            yes?.isChecked = false
            refundNowLayout?.visibility = View.GONE
            refundAmountEditText?.isEnabled = false
            activeConsultationCloseButtonLayout?.visibility = View.VISIBLE
        })
        if (OrderDetailsActivity.apptStatusColor.equals("red", ignoreCase = true)) {
            appointmentStatusValue?.setTextColor(ContextCompat.getColor(mContext,R.color.colorDanger))
        } else if (OrderDetailsActivity.apptStatusColor.equals("yellow", ignoreCase = true)) {
            appointmentStatusValue?.setTextColor(ContextCompat.getColor(mContext,R.color.colorInfo))
        } else if (OrderDetailsActivity.apptStatusColor.equals("black", ignoreCase = true)) {
            appointmentStatusValue?.setTextColor(ContextCompat.getColor(mContext,R.color.colorBlack))
        } else if (OrderDetailsActivity.apptStatusColor.equals("green", ignoreCase = true)) {
            appointmentStatusValue?.setTextColor(ContextCompat.getColor(mContext,R.color.colorPrimary))
        } else if (OrderDetailsActivity.apptStatusColor.equals("blue", ignoreCase = true)) {
            appointmentStatusValue?.setTextColor(ContextCompat.getColor(mContext,R.color.colorAccent))
        }
        if (OrderDetailsActivity.paymentStatusColor.equals("red", ignoreCase = true)) {
            paymentStatusValue?.setTextColor(ContextCompat.getColor(mContext,R.color.colorDanger))
        } else if (OrderDetailsActivity.paymentStatusColor.equals("yellow", ignoreCase = true)) {
            paymentStatusValue?.setTextColor(ContextCompat.getColor(mContext,R.color.colorInfo))
        } else if (OrderDetailsActivity.paymentStatusColor.equals("black", ignoreCase = true)) {
            paymentStatusValue?.setTextColor(ContextCompat.getColor(mContext,R.color.colorBlack))
        } else if (OrderDetailsActivity.paymentStatusColor.equals("green", ignoreCase = true)) {
            paymentStatusValue?.setTextColor(ContextCompat.getColor(mContext,R.color.colorPrimary))
        } else if (OrderDetailsActivity.paymentStatusColor.equals("blue", ignoreCase = true)) {
            paymentStatusValue?.setTextColor(ContextCompat.getColor(mContext,R.color.colorAccent))
        }
        refundNowLayout?.setOnClickListener(View.OnClickListener {
            val refAmt = refundAmountEditText?.text.toString().toInt()
            refundNow(OrderDetailsActivity.orderID, refAmt)
        })
        activeConsultationCloseButtonLayout?.setOnClickListener(View.OnClickListener { dialog.dismiss() })
        CloseButtonLayout?.setOnClickListener(View.OnClickListener { dialog.dismiss() })


        //Set callback
        if (behavior != null && behavior is BottomSheetBehavior<*>) {
            behavior.setBottomSheetCallback(mBottomSheetBehaviorCallback)
        }
        dialog.setCancelable(false)
        dialog.setCanceledOnTouchOutside(true)
        cancelIcon.setColorFilter(ContextCompat.getColor(mContext,R.color.colorWhite), PorterDuff.Mode.SRC_IN)
        cancelIcon.setOnClickListener(View.OnClickListener { dialog.dismiss() })
    }

    fun refundNow(dialog: Dialog) {
        dashboardViewModel!!.sendMessageDetails(
            activity,
            bottomSheetMessageText!!.text.toString(),
            interFaceId,
            hasInterface
        ).observe(
            requireActivity(), object : Observer<String?> {
                override fun onChanged(value: String?) {
                    try {
                        val jsonObject = JSONObject(value)
                        if (jsonObject.getInt("status_code") == 200) {
                            val response = JSONObject(value).getJSONObject("response")
                            val rootObj = response.optJSONObject("response")
                            val createMessageResponse = response.getBoolean("response")
                            if (createMessageResponse) {
                                Toast.makeText(
                                    orderDetailsActivity,
                                    "Message sent successfully.",
                                    Toast.LENGTH_LONG
                                ).show()
                                dialog.dismiss()
                            }
                        } else {
                            if (value != null) {
                                errorHandler(requireContext(), value)
                            }
                        }
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                }
            })
    }

    private fun cancelRefund(order_id: Int) {
        val url = ApiUrls.cancelRefund + "?order_id=" + order_id
        appointmentApiRequests!!.getApptApiData(url, "", activity, object : VolleyCallback {
            override fun onSuccess(result: String) {
                try {
                    val resObj = JSONObject(result)
                    if (resObj.getInt("response") == 1) {
                        triggerMessageLayout!!.visibility = View.VISIBLE
                        refundNowViewLayout!!.visibility = View.GONE
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            override fun onError(err: String) {
                errorHandler(requireActivity(), err)
            }
        })
    }

    private fun refundNow(orderId: Int, refundAmount: Int) {
        val url = ApiUrls.cancelAppointmentRefund
        try {
            jsonValue = JSONObject()
            jsonValue!!.put("order_id", orderId)
            jsonValue!!.put("refundAmt", refundAmount)
        } catch (e: JSONException) {
            e.message
        }
        appointmentApiRequests!!.postApptApiData(url, jsonValue, activity, object : VolleyCallback {
            override fun onSuccess(result: String) {
                try {
                    val resObj = JSONObject(result)
                    Toast.makeText(
                        orderDetailsActivity,
                        resObj.getString("response"),
                        Toast.LENGTH_LONG
                    ).show()
                    triggerMessageLayout!!.visibility = View.VISIBLE
                    refundNowViewLayout!!.visibility = View.GONE
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            override fun onError(err: String) {
                errorHandler(orderDetailsActivity!!, err)
            }
        })
    }

    companion object {
        var triggerMessageLayout: RelativeLayout? = null
        var refundNowViewLayout: RelativeLayout? = null
        var CloseButtonLayout: RelativeLayout? = null
    }
}