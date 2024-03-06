package com.whitecoats.clinicplus.fragments

import android.annotation.SuppressLint
import android.app.Dialog
import android.app.ProgressDialog
import android.graphics.PorterDuff
import android.view.View
import android.widget.*
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.lifecycle.Observer
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.BottomSheetCallback
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.whitecoats.clinicplus.R
import com.whitecoats.clinicplus.utils.ErrorHandlerClass.errorHandler
import com.whitecoats.clinicplus.viewmodels.DashboardViewModel
import com.whitecoats.model.AddPatientModel
import org.json.JSONException
import org.json.JSONObject

/**
 * Created by vaibhav on 07-02-2018.
 */
class PaymentsOverViewInfoBottomSheet : BottomSheetDialogFragment() {
    private var paymentOverviewFragment: PaymentOverviewFragment? = null
    private lateinit var cancelIcon: ImageView
    private lateinit var close_layout: RelativeLayout
    private val bottomSheetMessageText: EditText? = null
    var addInterfaceModelList: List<AddPatientModel>? = null
    private val interFaceId = 0
    private val hasInterface = false
    private val dashboardViewModel: DashboardViewModel? = null
    private var infoTypeText: String? = null
    fun setupConfig(paymentOverviewFragment: PaymentOverviewFragment?, infoType: String?) {
        this.paymentOverviewFragment = paymentOverviewFragment
        addInterfaceModelList = ArrayList()
        infoTypeText = infoType
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
            View.inflate(context, R.layout.fragment_bottom_sheet_payment_overview_info, null)
        dialog.setContentView(contentView)
        val params = (contentView.parent as View).layoutParams as CoordinatorLayout.LayoutParams
        val behavior = params.behavior
        cancelIcon = contentView.findViewById(R.id.bottomSheetActiveConsultationCancelIcon)
        close_layout = contentView.findViewById(R.id.layout_close)
        //Set callback
        if (behavior != null && behavior is BottomSheetBehavior<*>) {
            behavior.setBottomSheetCallback(mBottomSheetBehaviorCallback)
        }
        dialog.setCancelable(false)
        dialog.setCanceledOnTouchOutside(true)
        cancelIcon.setColorFilter(resources.getColor(R.color.colorWhite), PorterDuff.Mode.SRC_IN)
        cancelIcon.setOnClickListener(View.OnClickListener { dialog.dismiss() })
        close_layout.setOnClickListener(View.OnClickListener { dialog.dismiss() })
    }

    fun sendMessage(dialog: Dialog) {
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
                            //                        JSONObject rootObj = response.optJSONObject("response");
                            val rootObj = response.optJSONObject("response")
                            val createMessageResponse = response.getBoolean("response")
                            if (createMessageResponse) {
                                Toast.makeText(
                                    paymentOverviewFragment!!.activity,
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
}