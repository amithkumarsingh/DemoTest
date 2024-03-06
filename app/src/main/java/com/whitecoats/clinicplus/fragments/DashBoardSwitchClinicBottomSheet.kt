package com.whitecoats.clinicplus.fragments

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.graphics.PorterDuff
import android.view.View
import android.widget.ImageView
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.BottomSheetCallback
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.whitecoats.clinicplus.R
import com.whitecoats.clinicplus.adapters.DashBoardApptListAdapter
import com.whitecoats.clinicplus.adapters.DashBoardSwitchClinicListAdapter
import com.whitecoats.clinicplus.fragments.DashboardFullMode.Companion.switchClinic
import com.whitecoats.clinicplus.models.DashBoardApptListModel
import com.whitecoats.clinicplus.models.SwitchClinicModel

/**
 * Created by vaibhav on 07-02-2018.
 */
class DashBoardSwitchClinicBottomSheet : BottomSheetDialogFragment() {
    private var commMessagActivity: DashboardFullMode? = null
    private var adapter: DashBoardSwitchClinicListAdapter? = null
    private var appointmentsAllList: List<DashBoardApptListModel>? = null
    private lateinit var dialogContext:Activity
    fun setupConfig(activity:Activity,
        commMessagActivity: DashboardFullMode?,
        appointmetsAll: List<DashBoardApptListModel>?
    ) {
        this.commMessagActivity = commMessagActivity
        appointmentsAllList = appointmetsAll
        dialogContext=activity
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
        //Get the content View
        val contentView =
            View.inflate(context, R.layout.fragment_bottom_sheet_switch_clinic_list, null)
        dialog.setContentView(contentView)
        //Set the coordinator layout behavior
        populateSwitchClinicListView(contentView, dialog)
        val params = (contentView.parent as View).layoutParams as CoordinatorLayout.LayoutParams
        val behavior = params.behavior
        cancelIcon = contentView.findViewById(R.id.bottomSheetCancelIcon)

        //Set callback
        if (behavior != null && behavior is BottomSheetBehavior<*>) {
            behavior.addBottomSheetCallback(mBottomSheetBehaviorCallback)
        }
        dialog.setCancelable(false)
        dialog.setCanceledOnTouchOutside(true)
        cancelIcon.setColorFilter(ContextCompat.getColor(dialogContext,R.color.colorWhite), PorterDuff.Mode.SRC_IN)
        cancelIcon.setOnClickListener { dialog.dismiss() }
    }

    private fun populateSwitchClinicListView(view: View, dialog: Dialog) {
        val recyclerView = view.findViewById<View>(R.id.bottomSheetSwitchClinicList) as RecyclerView
        recyclerView.setHasFixedSize(true)
        val linearLayoutManager = LinearLayoutManager(context)
        recyclerView.layoutManager = linearLayoutManager
        arrayList = ArrayList()
        for (i in appointmentsAllList!!.indices) try {
            val dashboardAppointmentList = appointmentsAllList!![i]
            if (dashboardAppointmentList.serviceId == 3) {
                val switchClinicModel = SwitchClinicModel()
                switchClinicModel.setClinicName(dashboardAppointmentList.clinicName)
                switchClinicModel.setApptCount(dashboardAppointmentList.apptCount)
                arrayList.add(switchClinicModel) //Adding items to recycler view
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        adapter = DashBoardSwitchClinicListAdapter(requireContext(), arrayList) { _, position ->
            dialog.dismiss()
            DashBoardApptListAdapter.isAppointmentNextBackVideoClick = 0
            DashBoardApptListAdapter.isAppointmentNextBackChatClick = 0
            DashBoardApptListAdapter.isAppointmentNextBackClinicClick = 0
            switchClinic(position as Int)
        }
        recyclerView.adapter = adapter
    }

    companion object {
        @SuppressLint("StaticFieldLeak")
        lateinit var cancelIcon: ImageView
        lateinit var arrayList: MutableList<SwitchClinicModel>
    }
}