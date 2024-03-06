package com.whitecoats.clinicplus.casechannels

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.view.View
import android.widget.*
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.BottomSheetCallback
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.whitecoats.clinicplus.R

class TaskListFillterBottomsheet : BottomSheetDialogFragment() {
    private lateinit var activity: Activity
    private lateinit var assignedToArray: MutableList<String>
    private lateinit var selectStatusSpinner: Spinner
    private lateinit var assignedSpinner: Spinner
    private lateinit var sortBySpinner: Spinner
    private var caseDoctorOrganisationClickListener: CaseDoctorOrganisationClickListener? = null
    private var selectStateString = ""
    private var sortByString = ""
    private var indextPosition = -1
    private var statusPos = 0
    private var sortPos = 0

    //Bottom Sheet Callback
    private val mBottomSheetBehaviorCallback: BottomSheetCallback = object : BottomSheetCallback() {
        override fun onStateChanged(bottomSheet: View, newState: Int) {
            if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                dismiss()
            }
        }

        override fun onSlide(bottomSheet: View, slideOffset: Float) {}
    }

    fun setupConfig(
        activity: Activity,
        assignedToArray: MutableList<String>,
        assignedPos: Int,
        statusPos: Int,
        sortPos: Int,
        caseDoctorOrganisationClickListener: CaseDoctorOrganisationClickListener?
    ) {
        this.activity = activity
        this.assignedToArray = assignedToArray
        this.caseDoctorOrganisationClickListener = caseDoctorOrganisationClickListener
        indextPosition = assignedPos
        this.statusPos = statusPos
        this.sortPos = sortPos
    }

    @SuppressLint("RestrictedApi")
    override fun setupDialog(dialog: Dialog, style: Int) {
        super.setupDialog(dialog, style)
        val contentView = View.inflate(context, R.layout.task_list_filter_bottomsheet, null)
        dialog.setContentView(contentView)
        val params = (contentView.parent as View).layoutParams as CoordinatorLayout.LayoutParams
        val behavior = params.behavior
        //Set callback
        if (behavior != null && behavior is BottomSheetBehavior<*>) {
            behavior.setBottomSheetCallback(mBottomSheetBehaviorCallback)
        }
        val clearFilter = contentView.findViewById<ImageView>(R.id.taskListFilterClear)
        val saveButton = contentView.findViewById<Button>(R.id.applyFilterButton)
        selectStatusSpinner = contentView.findViewById(R.id.selectStatusSpinner)
        val typeadapter = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.select_Status_array, android.R.layout.simple_spinner_item
        )
        typeadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        selectStatusSpinner.setAdapter(typeadapter)
        sortBySpinner = contentView.findViewById(R.id.sortBySpinner)
        val sortBySpinnerAdapter = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.sort_By_Array, android.R.layout.simple_spinner_item
        )
        sortBySpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        sortBySpinner.setAdapter(sortBySpinnerAdapter)
        assignedSpinner = contentView.findViewById(R.id.assignedSpinner)
        val dataAdapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, assignedToArray)
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        assignedSpinner.setAdapter(dataAdapter)
        assignedSpinner.setSelection(indextPosition)
        selectStatusSpinner.setSelection(statusPos)
        sortBySpinner.setSelection(sortPos)
        selectStatusSpinner.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View,
                position: Int,
                id: Long
            ) {
                selectStateString = selectStatusSpinner.getItemAtPosition(position).toString()
                statusPos = position
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        })
        sortBySpinner.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View,
                position: Int,
                id: Long
            ) {
                sortByString = sortBySpinner.getItemAtPosition(position).toString()
                sortPos = position
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        })
        assignedSpinner.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View,
                position: Int,
                id: Long
            ) {

                // String paymentString = assignedSpinner.getItemAtPosition(position).toString();
                indextPosition = position
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        })
        // if button is clicked, close the custom dialog
        clearFilter.setOnClickListener { v ->
            dismiss()
            indextPosition = 0
            statusPos = 0
            sortPos = 0
            selectStateString = ""
            sortByString = ""
            caseDoctorOrganisationClickListener!!.getFilters(
                v,
                indextPosition,
                selectStateString,
                sortByString,
                statusPos,
                sortPos
            )
        }
        saveButton.setOnClickListener { v ->
            dismiss()
            caseDoctorOrganisationClickListener!!.getFilters(
                v,
                indextPosition,
                selectStateString,
                sortByString,
                statusPos,
                sortPos
            )
            //  Toast.makeText(mContext,"Dismissed..!!",Toast.LENGTH_SHORT).show();
        }
    }
}