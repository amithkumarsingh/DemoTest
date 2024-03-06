package com.whitecoats.clinicplus.autofollowup

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.view.View
import android.widget.*
import androidx.appcompat.widget.AppCompatButton
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContextCompat
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.BottomSheetCallback
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.whitecoats.clinicplus.AppDatabaseManager
import com.whitecoats.clinicplus.R
import com.whitecoats.model.DashBoardMoreListModel

class AutoFollowUpFilterBottomSheet
    : BottomSheetDialogFragment() {
    private var autoFollowUpActivity: AutoFollowUpActivity? = null
    private var dashBoardMoreModelList: List<DashBoardMoreListModel>? = null
    private var mContext: Context? = null
    private var appDatabaseManager: AppDatabaseManager? = null
    private var isBottomsheetFilterApplied = false
    private lateinit var durationGroup: RadioGroup
    private lateinit var conditionGroup: RadioGroup
    private lateinit var instructionGroup: RadioGroup
    private lateinit var apptBookedGroup: RadioGroup
    private lateinit var filterClear: ImageView
    private lateinit var applyFilter: AppCompatButton
    private lateinit var all: RadioButton
    private lateinit var today: RadioButton
    private lateinit var week: RadioButton
    private lateinit var month: RadioButton
    private lateinit var conditionAll: RadioButton
    private lateinit var conditionSame: RadioButton
    private lateinit var conditionBetter: RadioButton
    private lateinit var conditionWorse: RadioButton
    private lateinit var instructionAll: RadioButton
    private lateinit var instructionCompletely: RadioButton
    private lateinit var instructionPartially: RadioButton
    private lateinit var instructionNo: RadioButton
    private lateinit var apptBookedAll: RadioButton
    private lateinit var apptBookedYes: RadioButton
    private lateinit var apptBookedNo: RadioButton
    var durationFilter = "All"
    private var conditionFilter = "All"
    private var instructionFilter = "All"
    private var followUpAppointmentBookedFilter = "All"
    fun setupConfig(
        appointmentActivity: AutoFollowUpActivity?, durationFilter: String, conditionFilter: String,
        instructionFilter: String, followUpAppointmentBookedFilter: String
    ) {
        autoFollowUpActivity = appointmentActivity
        mContext = context
        dashBoardMoreModelList = ArrayList()
        this.durationFilter = durationFilter
        this.conditionFilter = conditionFilter
        this.instructionFilter = instructionFilter
        this.followUpAppointmentBookedFilter = followUpAppointmentBookedFilter
        appDatabaseManager = AppDatabaseManager(mContext)
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

    @SuppressLint("RestrictedApi", "NotifyDataSetChanged")
    override fun setupDialog(dialog: Dialog, style: Int) {
//        super.setupDialog(dialog, style);
        //Get the content View
        val contentView =
            View.inflate(context, R.layout.activity_bottom_sheet_autofollow_up_filter, null)
        dialog.setContentView(contentView)
        //Set the coordinator layout behavior
        val params = (contentView.parent as View).layoutParams as CoordinatorLayout.LayoutParams
        val behavior = params.behavior
        //Set callback
        if (behavior != null && behavior is BottomSheetBehavior<*>) {
            behavior.setBottomSheetCallback(mBottomSheetBehaviorCallback)
        }

        durationGroup = contentView.findViewById(R.id.filterDurationGroup)
        conditionGroup = contentView.findViewById(R.id.filterConditionGroup)
        instructionGroup = contentView.findViewById(R.id.filterInstructionTypeGroup)
        apptBookedGroup = contentView.findViewById(R.id.filterApptBookedGroup)
        filterClear = contentView.findViewById(R.id.filterClear)
        all = contentView.findViewById(R.id.filterDurationAll)
        today = contentView.findViewById(R.id.filterDurationToday)
        week = contentView.findViewById(R.id.filterDurationWeek)
        month = contentView.findViewById(R.id.filterDurationMonth)
        conditionAll = contentView.findViewById(R.id.filterConditionAllType)
        conditionSame = contentView.findViewById(R.id.filterSameType)
        conditionBetter = contentView.findViewById(R.id.filterBetterType)
        conditionWorse = contentView.findViewById(R.id.filterWorseType)
        instructionAll = contentView.findViewById(R.id.filterInstructionTypeAll)
        instructionCompletely = contentView.findViewById(R.id.filterInstructionCompletely)
        instructionPartially = contentView.findViewById(R.id.filterInstructionPartially)
        instructionNo = contentView.findViewById(R.id.filterInstructionNo)
        apptBookedAll = contentView.findViewById(R.id.filterApptBookedAll)
        apptBookedYes = contentView.findViewById(R.id.filterApptBookedYes)
        apptBookedNo = contentView.findViewById(R.id.filterApptBookedNo)
        applyFilter = contentView.findViewById(R.id.filterApptApply)

        setFilter(
            durationFilter,
            conditionFilter,
            instructionFilter,
            followUpAppointmentBookedFilter
        )
        durationGroup.clearCheck()
        durationGroup.setOnCheckedChangeListener { _, _ ->
            if (all.isChecked) {
                durationFilter = all.text.toString()
                autoFollowUpActivity!!.durationFilter = durationFilter
                all.background = ContextCompat.getDrawable(requireActivity(),R.drawable.filter_selected_outline)
                today.background =ContextCompat.getDrawable(requireActivity(),R.drawable.filter_outline)
                week.background = ContextCompat.getDrawable(requireActivity(),R.drawable.filter_outline)
                month.background = ContextCompat.getDrawable(requireActivity(),R.drawable.filter_outline)
                all.setTextColor(Color.WHITE)
                today.setTextColor(ContextCompat.getColor(requireActivity(),R.color.colorCardBack))
                week.setTextColor(ContextCompat.getColor(requireActivity(),R.color.colorCardBack))
                month.setTextColor(ContextCompat.getColor(requireActivity(),R.color.colorCardBack))
            } else if (today.isChecked) {
                durationFilter = today.text.toString()
                autoFollowUpActivity!!.durationFilter = durationFilter
                today.background = ContextCompat.getDrawable(requireActivity(),R.drawable.filter_selected_outline)
                all.background = ContextCompat.getDrawable(requireActivity(),R.drawable.filter_outline)
                week.background = ContextCompat.getDrawable(requireActivity(),R.drawable.filter_outline)
                month.background = ContextCompat.getDrawable(requireActivity(),R.drawable.filter_outline)
                today.setTextColor(Color.WHITE)
                all.setTextColor(ContextCompat.getColor(requireActivity(),R.color.colorCardBack))
                week.setTextColor(ContextCompat.getColor(requireActivity(),R.color.colorCardBack))
                month.setTextColor(ContextCompat.getColor(requireActivity(),R.color.colorCardBack))
            } else if (week.isChecked) {
                durationFilter = week.text.toString()
                autoFollowUpActivity!!.durationFilter = durationFilter
                week.background =ContextCompat.getDrawable(requireActivity(),R.drawable.filter_selected_outline)
                all.background = ContextCompat.getDrawable(requireActivity(),R.drawable.filter_outline)
                today.background = ContextCompat.getDrawable(requireActivity(),R.drawable.filter_outline)
                month.background =ContextCompat.getDrawable(requireActivity(),R.drawable.filter_outline)
                week.setTextColor(Color.WHITE)
                all.setTextColor(ContextCompat.getColor(requireActivity(),R.color.colorCardBack))
                today.setTextColor(ContextCompat.getColor(requireActivity(),R.color.colorCardBack))
                month.setTextColor(ContextCompat.getColor(requireActivity(),R.color.colorCardBack))
            } else if (month.isChecked) {
                durationFilter = month.text.toString()
                autoFollowUpActivity!!.durationFilter = durationFilter
                month.background = ContextCompat.getDrawable(requireActivity(),R.drawable.filter_selected_outline)
                all.background = ContextCompat.getDrawable(requireActivity(),R.drawable.filter_outline)
                today.background = ContextCompat.getDrawable(requireActivity(),R.drawable.filter_outline)
                week.background = ContextCompat.getDrawable(requireActivity(),R.drawable.filter_outline)
                month.setTextColor(Color.WHITE)
                all.setTextColor(ContextCompat.getColor(requireActivity(),R.color.colorCardBack))
                week.setTextColor(ContextCompat.getColor(requireActivity(),R.color.colorCardBack))
                today.setTextColor(ContextCompat.getColor(requireActivity(),R.color.colorCardBack))
            }
        }
        conditionGroup.clearCheck()
        conditionGroup.setOnCheckedChangeListener { radioGroup, i ->
            if (conditionAll.isChecked) {
                conditionFilter = conditionAll.text.toString()
                autoFollowUpActivity!!.conditionFilter = conditionFilter
                conditionAll.background = ContextCompat.getDrawable(requireActivity(),R.drawable.filter_selected_outline)
                conditionSame.background =ContextCompat.getDrawable(requireActivity(),R.drawable.filter_outline)
                conditionBetter.background = ContextCompat.getDrawable(requireActivity(),R.drawable.filter_outline)
                conditionWorse.background = ContextCompat.getDrawable(requireActivity(),R.drawable.filter_outline)
                conditionAll.setTextColor(Color.WHITE)
                conditionSame.setTextColor(ContextCompat.getColor(requireActivity(),R.color.colorCardBack))
                conditionBetter.setTextColor(ContextCompat.getColor(requireActivity(),R.color.colorCardBack))
                conditionWorse.setTextColor(ContextCompat.getColor(requireActivity(),R.color.colorCardBack))
            } else if (conditionSame.isChecked) {
                conditionFilter = conditionSame.text.toString()
                autoFollowUpActivity!!.conditionFilter = conditionFilter
                conditionSame.background = ContextCompat.getDrawable(requireActivity(),R.drawable.filter_selected_outline)
                conditionAll.background = ContextCompat.getDrawable(requireActivity(),R.drawable.filter_outline)
                conditionBetter.background = ContextCompat.getDrawable(requireActivity(),R.drawable.filter_outline)
                conditionWorse.background = ContextCompat.getDrawable(requireActivity(),R.drawable.filter_outline)
                conditionSame.setTextColor(Color.WHITE)
                conditionAll.setTextColor(ContextCompat.getColor(requireActivity(),R.color.colorCardBack))
                conditionBetter.setTextColor(ContextCompat.getColor(requireActivity(),R.color.colorCardBack))
                conditionWorse.setTextColor(ContextCompat.getColor(requireActivity(),R.color.colorCardBack))
            } else if (conditionBetter.isChecked) {
                conditionFilter = conditionBetter.text.toString()
                autoFollowUpActivity!!.conditionFilter = conditionFilter
                conditionBetter.background =
                    ContextCompat.getDrawable(requireActivity(),R.drawable.filter_selected_outline)
                conditionSame.background = ContextCompat.getDrawable(requireActivity(),R.drawable.filter_outline)
                conditionAll.background =ContextCompat.getDrawable(requireActivity(),R.drawable.filter_outline)
                conditionWorse.background = ContextCompat.getDrawable(requireActivity(),R.drawable.filter_outline)
                conditionBetter.setTextColor(Color.WHITE)
                conditionSame.setTextColor(ContextCompat.getColor(requireActivity(),R.color.colorCardBack))
                conditionAll.setTextColor(ContextCompat.getColor(requireActivity(),R.color.colorCardBack))
                conditionWorse.setTextColor(ContextCompat.getColor(requireActivity(),R.color.colorCardBack))
            } else if (conditionWorse.isChecked) {
                conditionFilter = conditionWorse.text.toString()
                autoFollowUpActivity!!.conditionFilter = conditionFilter
                conditionWorse.background =
                    ContextCompat.getDrawable(requireActivity(),R.drawable.filter_selected_outline)
                conditionSame.background = ContextCompat.getDrawable(requireActivity(),R.drawable.filter_outline)
                conditionBetter.background = ContextCompat.getDrawable(requireActivity(),R.drawable.filter_outline)
                conditionAll.background = ContextCompat.getDrawable(requireActivity(),R.drawable.filter_outline)
                conditionWorse.setTextColor(Color.WHITE)
                conditionSame.setTextColor(ContextCompat.getColor(requireActivity(),R.color.colorCardBack))
                conditionBetter.setTextColor(ContextCompat.getColor(requireActivity(),R.color.colorCardBack))
                conditionAll.setTextColor(ContextCompat.getColor(requireActivity(),R.color.colorCardBack))
            }
        }
        instructionGroup.clearCheck()
        instructionGroup.setOnCheckedChangeListener { radioGroup, i ->
            if (instructionAll.isChecked) {
                instructionFilter = instructionAll.text.toString()
                autoFollowUpActivity!!.instructionFilter = instructionFilter
                instructionAll.background =
                    ContextCompat.getDrawable(requireActivity(),R.drawable.filter_selected_outline)
                instructionCompletely.background = ContextCompat.getDrawable(requireActivity(),R.drawable.filter_outline)
                instructionPartially.background = ContextCompat.getDrawable(requireActivity(),R.drawable.filter_outline)
                instructionNo.background = ContextCompat.getDrawable(requireActivity(),R.drawable.filter_outline)
                instructionAll.setTextColor(Color.WHITE)
                instructionCompletely.setTextColor(ContextCompat.getColor(requireActivity(),R.color.colorCardBack))
                instructionPartially.setTextColor(ContextCompat.getColor(requireActivity(),R.color.colorCardBack))
                instructionNo.setTextColor(ContextCompat.getColor(requireActivity(),R.color.colorCardBack))
            } else if (instructionCompletely.isChecked) {
                instructionFilter = instructionCompletely.text.toString()
                autoFollowUpActivity!!.instructionFilter = instructionFilter
                instructionCompletely.background =
                    ContextCompat.getDrawable(requireActivity(),R.drawable.filter_selected_outline)
                instructionAll.background = ContextCompat.getDrawable(requireActivity(),R.drawable.filter_outline)
                instructionPartially.background = ContextCompat.getDrawable(requireActivity(),R.drawable.filter_outline)
                instructionNo.background = ContextCompat.getDrawable(requireActivity(),R.drawable.filter_outline)
                instructionCompletely.setTextColor(Color.WHITE)
                instructionAll.setTextColor(ContextCompat.getColor(requireActivity(),R.color.colorCardBack))
                instructionPartially.setTextColor(ContextCompat.getColor(requireActivity(),R.color.colorCardBack))
                instructionNo.setTextColor(ContextCompat.getColor(requireActivity(),R.color.colorCardBack))
            } else if (instructionPartially.isChecked) {
                instructionFilter = instructionPartially.text.toString()
                autoFollowUpActivity!!.instructionFilter = instructionFilter
                instructionPartially.background =
                    ContextCompat.getDrawable(requireActivity(),R.drawable.filter_selected_outline)
                instructionCompletely.background = ContextCompat.getDrawable(requireActivity(),R.drawable.filter_outline)
                instructionAll.background = ContextCompat.getDrawable(requireActivity(),R.drawable.filter_outline)
                instructionNo.background = ContextCompat.getDrawable(requireActivity(),R.drawable.filter_outline)
                instructionPartially.setTextColor(Color.WHITE)
                instructionCompletely.setTextColor(ContextCompat.getColor(requireActivity(),R.color.colorCardBack))
                instructionAll.setTextColor(ContextCompat.getColor(requireActivity(),R.color.colorCardBack))
                instructionNo.setTextColor(ContextCompat.getColor(requireActivity(),R.color.colorCardBack))
            } else if (instructionNo.isChecked) {
                instructionFilter = instructionNo.text.toString()
                autoFollowUpActivity!!.instructionFilter = instructionFilter
                instructionNo.background = ContextCompat.getDrawable(requireActivity(),R.drawable.filter_selected_outline)
                instructionCompletely.background = ContextCompat.getDrawable(requireActivity(),R.drawable.filter_outline)
                instructionPartially.background = ContextCompat.getDrawable(requireActivity(),R.drawable.filter_outline)
                instructionAll.background = ContextCompat.getDrawable(requireActivity(),R.drawable.filter_outline)
                instructionNo.setTextColor(Color.WHITE)
                instructionCompletely.setTextColor(ContextCompat.getColor(requireActivity(),R.color.colorCardBack))
                instructionPartially.setTextColor(ContextCompat.getColor(requireActivity(),R.color.colorCardBack))
                instructionAll.setTextColor(ContextCompat.getColor(requireActivity(),R.color.colorCardBack))
            }
        }
        apptBookedGroup.clearCheck()
        apptBookedGroup.setOnCheckedChangeListener { radioGroup, i ->
            if (apptBookedAll.isChecked) {
                followUpAppointmentBookedFilter = apptBookedAll.text.toString()
                autoFollowUpActivity!!.followUpAppointmentBookedFilter =
                    followUpAppointmentBookedFilter
                apptBookedAll.background =
                    ContextCompat.getDrawable(requireActivity(), R.drawable.filter_selected_outline)
                apptBookedYes.background =
                    ContextCompat.getDrawable(requireActivity(), R.drawable.filter_outline)
                apptBookedNo.background =
                    ContextCompat.getDrawable(requireActivity(), R.drawable.filter_outline)
                apptBookedAll.setTextColor(Color.WHITE)
                apptBookedYes.setTextColor(
                    ContextCompat.getColor(
                        requireActivity(),
                        R.color.colorCardBack
                    )
                )
                apptBookedNo.setTextColor(ContextCompat.getColor(requireActivity(), R.color.colorCardBack))
            } else if (apptBookedYes.isChecked) {
                followUpAppointmentBookedFilter = apptBookedYes.text.toString()
                autoFollowUpActivity!!.followUpAppointmentBookedFilter =
                    followUpAppointmentBookedFilter
                apptBookedYes.background =
                    ContextCompat.getDrawable(requireActivity(), R.drawable.filter_selected_outline)
                apptBookedAll.background =
                    ContextCompat.getDrawable(requireActivity(), R.drawable.filter_outline)
                apptBookedNo.background =
                    ContextCompat.getDrawable(requireActivity(), R.drawable.filter_outline)
                apptBookedYes.setTextColor(Color.WHITE)
                apptBookedAll.setTextColor(
                    ContextCompat.getColor(
                        requireActivity(),
                        R.color.colorCardBack
                    )
                )
                apptBookedNo.setTextColor(ContextCompat.getColor(requireActivity(), R.color.colorCardBack))
            } else if (apptBookedNo.isChecked) {
                followUpAppointmentBookedFilter = apptBookedNo.text.toString()
                autoFollowUpActivity!!.followUpAppointmentBookedFilter =
                    followUpAppointmentBookedFilter
                apptBookedNo.background =
                    ContextCompat.getDrawable(requireActivity(), R.drawable.filter_selected_outline)
                apptBookedYes.background =
                    ContextCompat.getDrawable(requireActivity(), R.drawable.filter_outline)
                apptBookedAll.background =
                    ContextCompat.getDrawable(requireActivity(), R.drawable.filter_outline)
                apptBookedNo.setTextColor(Color.WHITE)
                apptBookedYes.setTextColor(
                    ContextCompat.getColor(
                        requireActivity(),
                        R.color.colorCardBack
                    )
                )
                apptBookedAll.setTextColor(
                    ContextCompat.getColor(
                        requireActivity(),
                        R.color.colorCardBack
                    )
                )
            }
        }
        filterClear.setOnClickListener {
            resetFilterRadioLayout()
            autoFollowUpActivity!!.isFilterApplied = false
        }
        applyFilter.setOnClickListener {
            autoFollowUpActivity!!.isFilterApplied = true
            isBottomsheetFilterApplied = true

            if (durationFilter.equals("All", ignoreCase = true)) {
                durationFilter = ""
            } else if (durationFilter.equals("Today", ignoreCase = true)) {
                durationFilter = "1"
            } else if (durationFilter.equals("Week", ignoreCase = true)) {
                durationFilter = "2"
            } else if (durationFilter.equals("Month", ignoreCase = true)) {
                durationFilter = "3"
            }
            if (conditionFilter.equals("All", ignoreCase = true)) {
                conditionFilter = ""
            } else if (conditionFilter.equals("Same", ignoreCase = true)) {
                conditionFilter = "1"
            } else if (conditionFilter.equals("Better", ignoreCase = true)) {
                conditionFilter = "2"
            } else if (conditionFilter.equals("Worse", ignoreCase = true)) {
                conditionFilter = "3"
            }
            if (instructionFilter.equals("All", ignoreCase = true)) {
                instructionFilter = ""
            } else if (instructionFilter.equals("Completely", ignoreCase = true)) {
                instructionFilter = "1"
            } else if (instructionFilter.equals("Partially", ignoreCase = true)) {
                instructionFilter = "2"
            } else if (instructionFilter.equals("No", ignoreCase = true)) {
                instructionFilter = "3"
            }
            if (followUpAppointmentBookedFilter.equals("All", ignoreCase = true)) {
                followUpAppointmentBookedFilter = ""
            } else if (followUpAppointmentBookedFilter.equals("Yes", ignoreCase = true)) {
                followUpAppointmentBookedFilter = "1"
            } else if (followUpAppointmentBookedFilter.equals("No", ignoreCase = true)) {
                followUpAppointmentBookedFilter = "0"
            }
            AutoFollowUpActivity.autoFollowUpListModelList!!.clear()
            AutoFollowUpActivity.autoFollowUpAdapter!!.notifyDataSetChanged()
            AutoFollowUpActivity.pageNumberF = 1
            if (AutoFollowUpActivity.emptyText!!.visibility == View.VISIBLE) {
                AutoFollowUpActivity.emptyText!!.visibility = View.GONE
            }
            autoFollowUpActivity!!.getFollowUpList(
                "",
                "fname",
                "desc",
                instructionFilter,
                conditionFilter,
                followUpAppointmentBookedFilter,
                "10",
                1.toString() + "",
                durationFilter
            )
            dialog.dismiss()
        }
    }

    private fun resetFilterRadioLayout() {
        all.background = ContextCompat.getDrawable(requireActivity(),R.drawable.filter_outline)
        today.background = ContextCompat.getDrawable(requireActivity(),R.drawable.filter_outline)
        week.background = ContextCompat.getDrawable(requireActivity(),R.drawable.filter_outline)
        month.background = ContextCompat.getDrawable(requireActivity(),R.drawable.filter_outline)
        conditionAll.background = ContextCompat.getDrawable(requireActivity(),R.drawable.filter_outline)
        conditionSame.background = ContextCompat.getDrawable(requireActivity(),R.drawable.filter_outline)
        conditionBetter.background = ContextCompat.getDrawable(requireActivity(),R.drawable.filter_outline)
        conditionWorse.background = ContextCompat.getDrawable(requireActivity(),R.drawable.filter_outline)
        instructionAll.background = ContextCompat.getDrawable(requireActivity(),R.drawable.filter_outline)
        instructionCompletely.background = ContextCompat.getDrawable(requireActivity(),R.drawable.filter_outline)
        instructionPartially.background = ContextCompat.getDrawable(requireActivity(),R.drawable.filter_outline)
        instructionNo.background = ContextCompat.getDrawable(requireActivity(),R.drawable.filter_outline)
        apptBookedAll.background = ContextCompat.getDrawable(requireActivity(),R.drawable.filter_outline)
        apptBookedYes.background = ContextCompat.getDrawable(requireActivity(),R.drawable.filter_outline)
        apptBookedNo.background = ContextCompat.getDrawable(requireActivity(),R.drawable.filter_outline)
        all.setTextColor(ContextCompat.getColor(requireActivity(),R.color.colorCardBack))
        today.setTextColor(ContextCompat.getColor(requireActivity(),R.color.colorCardBack))
        week.setTextColor(ContextCompat.getColor(requireActivity(),R.color.colorCardBack))
        month.setTextColor(ContextCompat.getColor(requireActivity(),R.color.colorCardBack))
        conditionAll.setTextColor(ContextCompat.getColor(requireActivity(),R.color.colorCardBack))
        conditionSame.setTextColor(ContextCompat.getColor(requireActivity(),R.color.colorCardBack))
        conditionBetter.setTextColor(ContextCompat.getColor(requireActivity(),R.color.colorCardBack))
        conditionWorse.setTextColor(ContextCompat.getColor(requireActivity(),R.color.colorCardBack))
        instructionAll.setTextColor(ContextCompat.getColor(requireActivity(),R.color.colorCardBack))
        instructionCompletely.setTextColor(ContextCompat.getColor(requireActivity(),R.color.colorCardBack))
        instructionPartially.setTextColor(ContextCompat.getColor(requireActivity(),R.color.colorCardBack))
        instructionNo.setTextColor(ContextCompat.getColor(requireActivity(),R.color.colorCardBack))
        apptBookedAll.setTextColor(ContextCompat.getColor(requireActivity(),R.color.colorCardBack))
        apptBookedYes.setTextColor(ContextCompat.getColor(requireActivity(),R.color.colorCardBack))
        apptBookedNo.setTextColor(ContextCompat.getColor(requireActivity(),R.color.colorCardBack))
        all.isChecked = false
        today.isChecked = false
        week.isChecked = false
        month.isChecked = false
        conditionAll.isChecked = false
        conditionSame.isChecked = false
        conditionBetter.isChecked = false
        conditionWorse.isChecked = false
        instructionAll.isChecked = false
        instructionCompletely.isChecked = false
        instructionPartially.isChecked = false
        instructionNo.isChecked = false
        apptBookedAll.isChecked = false
        apptBookedYes.isChecked = false
        apptBookedNo.isChecked = false
    }
    private fun setFilter(
        durationCase: String?,
        conditionCase: String?,
        instructionCase: String?,
        apptBookedcase: String?
    ) {
        when (durationCase) {
            "All" -> {
                all.background = ContextCompat.getDrawable(requireActivity(),R.drawable.filter_selected_outline)
                all.setTextColor(Color.WHITE)
                all.isChecked = true
            }
            "Today" -> {
                today.background = ContextCompat.getDrawable(requireActivity(),R.drawable.filter_selected_outline)
                today.setTextColor(Color.WHITE)
                today.isChecked = true
            }
            "Week" -> {
                week.background = ContextCompat.getDrawable(requireActivity(),R.drawable.filter_selected_outline)
                week.setTextColor(Color.WHITE)
                week.isChecked = true
            }
            "Month" -> {
                month.background = ContextCompat.getDrawable(requireActivity(),R.drawable.filter_selected_outline)
                month.setTextColor(Color.WHITE)
                month.isChecked = true
            }
        }
        when (conditionCase) {
            "All" -> {
                conditionAll.background =
                    ContextCompat.getDrawable(requireActivity(),R.drawable.filter_selected_outline)
                conditionAll.setTextColor(Color.WHITE)
                conditionAll.isChecked = true
            }
            "Same" -> {
                conditionSame.background =
                    ContextCompat.getDrawable(requireActivity(),R.drawable.filter_selected_outline)
                conditionSame.setTextColor(Color.WHITE)
                conditionSame.isChecked = true
            }
            "Better" -> {
                conditionBetter.background =
                    ContextCompat.getDrawable(requireActivity(),R.drawable.filter_selected_outline)
                conditionBetter.setTextColor(Color.WHITE)
                conditionBetter.isChecked = true
            }
            "Worse" -> {
                conditionWorse.background =
                    ContextCompat.getDrawable(requireActivity(),R.drawable.filter_selected_outline)
                conditionWorse.setTextColor(Color.WHITE)
                conditionWorse.isChecked = true
            }
        }
        when (instructionCase) {
            "All" -> {
                instructionAll.background =
                    ContextCompat.getDrawable(requireActivity(),R.drawable.filter_selected_outline)
                instructionAll.setTextColor(Color.WHITE)
                instructionAll.isChecked = true
            }
            "Completely" -> {
                instructionCompletely.background =
                    ContextCompat.getDrawable(requireActivity(),R.drawable.filter_selected_outline)
                instructionCompletely.setTextColor(Color.WHITE)
                instructionCompletely.isChecked = true
            }
            "Partially" -> {
                instructionPartially.background =
                    ContextCompat.getDrawable(requireActivity(),R.drawable.filter_selected_outline)
                instructionPartially.setTextColor(Color.WHITE)
                instructionPartially.isChecked = true
            }
            "No" -> {
                instructionNo.background =
                    ContextCompat.getDrawable(requireActivity(),R.drawable.filter_selected_outline)
                instructionNo.setTextColor(Color.WHITE)
                instructionNo.isChecked = true
            }
        }
        when (apptBookedcase) {
            "All" -> {
                apptBookedAll.background =
                    ContextCompat.getDrawable(requireActivity(),R.drawable.filter_selected_outline)
                apptBookedAll.setTextColor(Color.WHITE)
                apptBookedAll.isChecked = true
            }
            "Yes" -> {
                apptBookedYes.background =
                    ContextCompat.getDrawable(requireActivity(),R.drawable.filter_selected_outline)
                apptBookedYes.setTextColor(Color.WHITE)
                apptBookedYes.isChecked = true
            }
            "No" -> {
                apptBookedNo.background =
                    ContextCompat.getDrawable(requireActivity(),R.drawable.filter_selected_outline)
                apptBookedNo.setTextColor(Color.WHITE)
                apptBookedNo.isChecked = true
            }
        }

    }
}