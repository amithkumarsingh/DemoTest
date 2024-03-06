package com.whitecoats.clinicplus.fragments

import android.annotation.SuppressLint
import android.app.Dialog
import android.graphics.PorterDuff
import android.view.View
import android.widget.Button
import android.widget.ImageView
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContextCompat
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.BottomSheetCallback
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.whitecoats.clinicplus.R
import com.whitecoats.clinicplus.activities.OrderDetailsActivity
import com.whitecoats.model.AddPatientModel

class PaymentTransactionBottomSheet : BottomSheetDialogFragment() {
    private var orderDetailsActivity: OrderDetailsActivity? = null
    var addInterfaceModelList: List<AddPatientModel>? = null
    private lateinit var cancelIcon: ImageView
    private lateinit var btnClose: Button
    private lateinit var btnCreateInvoice: Button
    fun setupConfig(paymentTransactionOrderDetails: OrderDetailsActivity?) {
        orderDetailsActivity = paymentTransactionOrderDetails
        addInterfaceModelList = ArrayList()
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
        val contentView =
            View.inflate(context, R.layout.fragment_bottom_sheet_payment_transaction_info, null)
        dialog.setContentView(contentView)
        val params = (contentView.parent as View).layoutParams as CoordinatorLayout.LayoutParams
        val behavior = params.behavior
        cancelIcon = contentView.findViewById(R.id.bottomSheetCancelIcon)
        btnClose = contentView.findViewById(R.id.button_close)
        btnCreateInvoice = contentView.findViewById(R.id.button_createInvoice)
        //Set callback
        if (behavior != null && behavior is BottomSheetBehavior<*>) {
            behavior.setBottomSheetCallback(mBottomSheetBehaviorCallback)
        }
        dialog.setCancelable(false)
        dialog.setCanceledOnTouchOutside(true)
        cancelIcon.setColorFilter(ContextCompat.getColor(requireContext(),R.color.colorWhite), PorterDuff.Mode.SRC_IN)
        cancelIcon.setOnClickListener { dialog.dismiss() }
        btnClose.setOnClickListener { dialog.dismiss() }
    }
}