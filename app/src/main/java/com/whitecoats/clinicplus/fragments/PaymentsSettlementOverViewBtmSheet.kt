package com.whitecoats.clinicplus.fragments

import android.annotation.SuppressLint
import android.app.Dialog
import android.graphics.PorterDuff
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.BottomSheetCallback
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.whitecoats.clinicplus.R
import com.whitecoats.model.AddPatientModel

class PaymentsSettlementOverViewBtmSheet : BottomSheetDialogFragment() {
    private var paymentOverviewFragment: PaymentOverviewFragment? = null
    var addInterfaceModelList: List<AddPatientModel>? = null
    private val interFaceId = 0
    private var infoTypeText: String? = null
    private lateinit var cancelIcon: ImageView
    private lateinit var close_layout: RelativeLayout
    fun setupConfig(paymentOverviewFragment: PaymentOverviewFragment?, infoType: String?) {
        this.paymentOverviewFragment = paymentOverviewFragment
        addInterfaceModelList = ArrayList()
        infoTypeText = infoType
    }

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
            View.inflate(context, R.layout.fragment_bottom_sheet_overview_settlement, null)
        dialog.setContentView(contentView)
        val params = (contentView.parent as View).layoutParams as CoordinatorLayout.LayoutParams
        val behavior = params.behavior
        cancelIcon = contentView.findViewById(R.id.bottomSheetSettlementCancelIcon)
        close_layout = contentView.findViewById(R.id.layout_settlement_close)
        if (behavior != null && behavior is BottomSheetBehavior<*>) {
            behavior.setBottomSheetCallback(mBottomSheetBehaviorCallback)
        }
        dialog.setCancelable(false)
        dialog.setCanceledOnTouchOutside(true)
        cancelIcon.setColorFilter(resources.getColor(R.color.colorWhite), PorterDuff.Mode.SRC_IN)
        cancelIcon.setOnClickListener(View.OnClickListener { dialog.dismiss() })
        close_layout.setOnClickListener(View.OnClickListener { dialog.dismiss() })
    }
}