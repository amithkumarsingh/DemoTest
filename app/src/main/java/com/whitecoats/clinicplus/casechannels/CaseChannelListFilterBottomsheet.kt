package com.whitecoats.clinicplus.casechannels

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.BottomSheetCallback
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.whitecoats.clinicplus.R

class CaseChannelListFilterBottomsheet : BottomSheetDialogFragment() {
    private var sortByStr = "Name"
    private var sortOrderStr = "Ascending"

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
        val contentView = View.inflate(context, R.layout.bottomsheet_case_channel_list, null)
        dialog.setContentView(contentView)

        //Set the coordinator layout behavior
        val params = (contentView.parent as View).layoutParams as CoordinatorLayout.LayoutParams
        val behavior = params.behavior
        val apply = contentView.findViewById<Button>(R.id.caseChannelApplyFilterBtn)
        val sortBy = contentView.findViewById<Spinner>(R.id.caseChannelSortBySpinner)
        val sortOrder = contentView.findViewById<Spinner>(R.id.caseChannelSortOrderSpinner)
        val aa1: ArrayAdapter<*> = ArrayAdapter.createFromResource(
            requireActivity().baseContext,
            R.array.case_channel_sort_by,
            android.R.layout.simple_spinner_item
        )
        aa1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        //Setting the ArrayAdapter data on the Spinner
        sortBy.adapter = aa1
        val aa2: ArrayAdapter<*> = ArrayAdapter.createFromResource(
            requireActivity().baseContext,
            R.array.case_channel_sort_order,
            android.R.layout.simple_spinner_item
        )
        aa2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        //Setting the ArrayAdapter data on the Spinner
        sortOrder.adapter = aa2
        apply.setOnClickListener { view: View? ->
            val intent = Intent("Filter")
            intent.putExtra("SortBy", sortByStr)
            intent.putExtra("SortOrder", sortOrderStr)
            LocalBroadcastManager.getInstance(requireContext()).sendBroadcast(intent)
            dialog.dismiss()
        }
        sortBy.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>?, view: View, i: Int, l: Long) {
                sortByStr = resources.getStringArray(R.array.case_channel_sort_by)[i]
            }

            override fun onNothingSelected(adapterView: AdapterView<*>?) {}
        }
        sortOrder.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>?, view: View, i: Int, l: Long) {
                sortOrderStr = resources.getStringArray(R.array.case_channel_sort_order)[i]
            }

            override fun onNothingSelected(adapterView: AdapterView<*>?) {}
        }

        //Set callback
        if (behavior != null && behavior is BottomSheetBehavior<*>) {
            behavior.setBottomSheetCallback(mBottomSheetBehaviorCallback)
        }
    }
}