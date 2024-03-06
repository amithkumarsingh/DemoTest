package com.whitecoats.clinicplus

import android.annotation.SuppressLint
import android.app.Dialog
import android.app.ProgressDialog
import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.view.View
import android.widget.*
import androidx.appcompat.widget.AppCompatButton
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.BottomSheetCallback
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.whitecoats.adapter.DashBoardMoreListAdapter
import com.whitecoats.clinicplus.MyClinicGlobalClass.Companion.logUserActionEvent
import com.whitecoats.clinicplus.apis.ApiUrls
import com.whitecoats.clinicplus.patientsharedrecords.PatientSharedRecordsActivity
import com.whitecoats.model.DashBoardMoreListModel

/**
 * Created by vaibhav on 07-02-2018.
 */
class PatientSharedRecordFilterBottomSheet : BottomSheetDialogFragment() {
    private var appointmentActivity: PatientSharedRecordsActivity? = null
    private var dashBoardMoreModelList: List<DashBoardMoreListModel>? = null
    private var cancelIcon: ImageView? = null
    private var mContext: Context? = null
    private var appDatabaseManager: AppDatabaseManager? = null
    var isBottomsheetFilterApplied = false
    private lateinit var durationGroup: RadioGroup
    private lateinit var durationGroup2: RadioGroup
    private lateinit var consultGroup: RadioGroup
    private lateinit var apptTypeGroup: RadioGroup
    private lateinit var apptTypeGroup2: RadioGroup
    private lateinit var filterClear: ImageView
    private lateinit var applyFilter: AppCompatButton
    private lateinit var all: RadioButton
    private lateinit var today: RadioButton
    private lateinit var week: RadioButton
    private lateinit var month: RadioButton
    private lateinit var video: RadioButton
    private lateinit var clinic: RadioButton
    private lateinit var allType: RadioButton
    private lateinit var apptAll: RadioButton
    private lateinit var apptRoutine: RadioButton
    private lateinit var apptFollow: RadioButton
    private lateinit var apptProcedure: RadioButton
    private lateinit var apptOther: RadioButton
    private lateinit var apptFirstVisit: RadioButton
    private  var durationFilter = "All"
    private var consultFilter = "All"
    private var apptTypeFilter = "All"
    private var activePastFilter = "active"
    fun setupConfig(
        appointmentActivity: PatientSharedRecordsActivity?,
        durationFilter: String,
        consultFilter: String,
        apptTypeFilter: String,
        activePastFilter: String
    ) {
        this.appointmentActivity = appointmentActivity
        mContext = context
        dashBoardMoreModelList = ArrayList()
        this.durationFilter = durationFilter
        this.consultFilter = consultFilter
        this.apptTypeFilter = apptTypeFilter
        this.activePastFilter = activePastFilter
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

    @SuppressLint("RestrictedApi")
    override fun setupDialog(dialog: Dialog, style: Int) {
//        super.setupDialog(dialog, style);
        //Get the content View
        val contentView =
            View.inflate(context, R.layout.fragment_bottom_sheet_patient_shared_filter, null)
        dialog.setContentView(contentView)
        //Set the coordinator layout behavior
        val params = (contentView.parent as View).layoutParams as CoordinatorLayout.LayoutParams
        val behavior = params.behavior
        cancelIcon = contentView.findViewById(R.id.bottomSheetCancelIcon)


        //Set callback
        if (behavior != null && behavior is BottomSheetBehavior<*>) {
            behavior.setBottomSheetCallback(mBottomSheetBehaviorCallback)
        }
        durationGroup = contentView.findViewById(R.id.filterDurationGroup)
        consultGroup = contentView.findViewById(R.id.filterConsultGroup)
        apptTypeGroup = contentView.findViewById(R.id.filterApptTypeGroup)
        apptTypeGroup2 = contentView.findViewById(R.id.filterApptTypeGroup2)
        filterClear = contentView.findViewById(R.id.filterClear)
        all = contentView.findViewById(R.id.filterDurationAll)
        today = contentView.findViewById(R.id.filterDurationToday)
        week = contentView.findViewById(R.id.filterDurationWeek)
        month = contentView.findViewById(R.id.filterDurationMonth)
        video = contentView.findViewById(R.id.filterVideoType)
        clinic = contentView.findViewById(R.id.filterClinicType)
        allType = contentView.findViewById(R.id.filterAllType)
        apptAll = contentView.findViewById(R.id.filterApptTypeAll)
        apptRoutine = contentView.findViewById(R.id.filterApptTypeRoutine)
        apptFollow = contentView.findViewById(R.id.filterApptTypeFollow)
        apptProcedure = contentView.findViewById(R.id.filterApptTypeProcedure)
        apptOther = contentView.findViewById(R.id.filterApptTypeOther)
        apptFirstVisit = contentView.findViewById(R.id.filterApptTypeFirstVisit)
        applyFilter = contentView.findViewById(R.id.filterApptApply)
        setFilter(durationFilter)
        durationGroup.clearCheck()
        durationGroup.setOnCheckedChangeListener(RadioGroup.OnCheckedChangeListener { radioGroup, i ->
            if (all.isChecked()) {
                durationFilter = all.getText().toString()
                appointmentActivity!!.durationFilter = durationFilter
                all.setBackground(resources.getDrawable(R.drawable.filter_selected_outline))
                today.setBackground(resources.getDrawable(R.drawable.filter_outline))
                week.setBackground(resources.getDrawable(R.drawable.filter_outline))
                month.setBackground(resources.getDrawable(R.drawable.filter_outline))
                all.setTextColor(Color.WHITE)
                today.setTextColor(resources.getColor(R.color.colorCardBack))
                week.setTextColor(resources.getColor(R.color.colorCardBack))
                month.setTextColor(resources.getColor(R.color.colorCardBack))
            } else if (today.isChecked()) {
                durationFilter = today.getText().toString()
                appointmentActivity!!.durationFilter = durationFilter
                today.setBackground(resources.getDrawable(R.drawable.filter_selected_outline))
                all.setBackground(resources.getDrawable(R.drawable.filter_outline))
                week.setBackground(resources.getDrawable(R.drawable.filter_outline))
                month.setBackground(resources.getDrawable(R.drawable.filter_outline))
                today.setTextColor(Color.WHITE)
                all.setTextColor(resources.getColor(R.color.colorCardBack))
                week.setTextColor(resources.getColor(R.color.colorCardBack))
                month.setTextColor(resources.getColor(R.color.colorCardBack))
            } else if (week.isChecked()) {
                durationFilter = "This " + week.getText().toString()
                appointmentActivity!!.durationFilter = durationFilter
                week.setBackground(resources.getDrawable(R.drawable.filter_selected_outline))
                all.setBackground(resources.getDrawable(R.drawable.filter_outline))
                today.setBackground(resources.getDrawable(R.drawable.filter_outline))
                month.setBackground(resources.getDrawable(R.drawable.filter_outline))
                week.setTextColor(Color.WHITE)
                all.setTextColor(resources.getColor(R.color.colorCardBack))
                today.setTextColor(resources.getColor(R.color.colorCardBack))
                month.setTextColor(resources.getColor(R.color.colorCardBack))
            } else if (month.isChecked()) {
                durationFilter = "This " + month.getText().toString()
                appointmentActivity!!.durationFilter = durationFilter
                month.setBackground(resources.getDrawable(R.drawable.filter_selected_outline))
                all.setBackground(resources.getDrawable(R.drawable.filter_outline))
                today.setBackground(resources.getDrawable(R.drawable.filter_outline))
                week.setBackground(resources.getDrawable(R.drawable.filter_outline))
                month.setTextColor(Color.WHITE)
                all.setTextColor(resources.getColor(R.color.colorCardBack))
                week.setTextColor(resources.getColor(R.color.colorCardBack))
                today.setTextColor(resources.getColor(R.color.colorCardBack))
            }
        })
        apptTypeGroup.clearCheck()
        apptTypeGroup2.clearCheck()
        apptTypeGroup.setOnCheckedChangeListener(RadioGroup.OnCheckedChangeListener { radioGroup, i ->
            if (apptAll.isChecked()) {
                apptTypeFilter = apptAll.getText().toString()
                apptAll.setBackground(resources.getDrawable(R.drawable.filter_selected_outline))
                apptRoutine.setBackground(resources.getDrawable(R.drawable.filter_outline))
                apptFollow.setBackground(resources.getDrawable(R.drawable.filter_outline))
                apptProcedure.setBackground(resources.getDrawable(R.drawable.filter_outline))
                apptOther.setBackground(resources.getDrawable(R.drawable.filter_outline))
                apptFirstVisit.setBackground(resources.getDrawable(R.drawable.filter_outline))
                apptAll.setTextColor(Color.WHITE)
                apptRoutine.setTextColor(resources.getColor(R.color.colorCardBack))
                apptFollow.setTextColor(resources.getColor(R.color.colorCardBack))
                apptProcedure.setTextColor(resources.getColor(R.color.colorCardBack))
                apptOther.setTextColor(resources.getColor(R.color.colorCardBack))
                apptFirstVisit.setTextColor(resources.getColor(R.color.colorCardBack))
            } else if (apptRoutine.isChecked()) {
                apptTypeFilter = apptRoutine.getText().toString()
                apptRoutine.setBackground(resources.getDrawable(R.drawable.filter_selected_outline))
                apptAll.setBackground(resources.getDrawable(R.drawable.filter_outline))
                apptFollow.setBackground(resources.getDrawable(R.drawable.filter_outline))
                apptProcedure.setBackground(resources.getDrawable(R.drawable.filter_outline))
                apptOther.setBackground(resources.getDrawable(R.drawable.filter_outline))
                apptFirstVisit.setBackground(resources.getDrawable(R.drawable.filter_outline))
                apptRoutine.setTextColor(Color.WHITE)
                apptAll.setTextColor(resources.getColor(R.color.colorCardBack))
                apptFollow.setTextColor(resources.getColor(R.color.colorCardBack))
                apptProcedure.setTextColor(resources.getColor(R.color.colorCardBack))
                apptOther.setTextColor(resources.getColor(R.color.colorCardBack))
                apptFirstVisit.setTextColor(resources.getColor(R.color.colorCardBack))
            } else if (apptFollow.isChecked()) {
                apptTypeFilter = apptFollow.getText().toString()
                apptFollow.setBackground(resources.getDrawable(R.drawable.filter_selected_outline))
                apptRoutine.setBackground(resources.getDrawable(R.drawable.filter_outline))
                apptAll.setBackground(resources.getDrawable(R.drawable.filter_outline))
                apptProcedure.setBackground(resources.getDrawable(R.drawable.filter_outline))
                apptOther.setBackground(resources.getDrawable(R.drawable.filter_outline))
                apptFirstVisit.setBackground(resources.getDrawable(R.drawable.filter_outline))
                apptFollow.setTextColor(Color.WHITE)
                apptRoutine.setTextColor(resources.getColor(R.color.colorCardBack))
                apptAll.setTextColor(resources.getColor(R.color.colorCardBack))
                apptProcedure.setTextColor(resources.getColor(R.color.colorCardBack))
                apptOther.setTextColor(resources.getColor(R.color.colorCardBack))
                apptFirstVisit.setTextColor(resources.getColor(R.color.colorCardBack))
            }
        })
        apptTypeGroup2.setOnCheckedChangeListener(RadioGroup.OnCheckedChangeListener { radioGroup, i -> // This will get the radiobutton that has changed clinicplus its check state
            if (apptProcedure.isChecked()) {
                apptTypeFilter = apptProcedure.getText().toString()
                apptProcedure.setBackground(resources.getDrawable(R.drawable.filter_selected_outline))
                apptRoutine.setBackground(resources.getDrawable(R.drawable.filter_outline))
                apptAll.setBackground(resources.getDrawable(R.drawable.filter_outline))
                apptFollow.setBackground(resources.getDrawable(R.drawable.filter_outline))
                apptOther.setBackground(resources.getDrawable(R.drawable.filter_outline))
                apptFirstVisit.setBackground(resources.getDrawable(R.drawable.filter_outline))
                apptProcedure.setTextColor(Color.WHITE)
                apptRoutine.setTextColor(resources.getColor(R.color.colorCardBack))
                apptFollow.setTextColor(resources.getColor(R.color.colorCardBack))
                apptAll.setTextColor(resources.getColor(R.color.colorCardBack))
                apptOther.setTextColor(resources.getColor(R.color.colorCardBack))
                apptFirstVisit.setTextColor(resources.getColor(R.color.colorCardBack))
            } else if (apptOther.isChecked()) {
                apptTypeFilter = apptOther.getText().toString()
                apptOther.setBackground(resources.getDrawable(R.drawable.filter_selected_outline))
                apptRoutine.setBackground(resources.getDrawable(R.drawable.filter_outline))
                apptAll.setBackground(resources.getDrawable(R.drawable.filter_outline))
                apptProcedure.setBackground(resources.getDrawable(R.drawable.filter_outline))
                apptFollow.setBackground(resources.getDrawable(R.drawable.filter_outline))
                apptFirstVisit.setBackground(resources.getDrawable(R.drawable.filter_outline))
                apptOther.setTextColor(Color.WHITE)
                apptRoutine.setTextColor(resources.getColor(R.color.colorCardBack))
                apptFollow.setTextColor(resources.getColor(R.color.colorCardBack))
                apptProcedure.setTextColor(resources.getColor(R.color.colorCardBack))
                apptAll.setTextColor(resources.getColor(R.color.colorCardBack))
                apptFirstVisit.setTextColor(resources.getColor(R.color.colorCardBack))
            } else if (apptFirstVisit.isChecked()) {
                apptTypeFilter = apptFirstVisit.getText().toString()
                apptFirstVisit.setBackground(resources.getDrawable(R.drawable.filter_selected_outline))
                apptRoutine.setBackground(resources.getDrawable(R.drawable.filter_outline))
                apptAll.setBackground(resources.getDrawable(R.drawable.filter_outline))
                apptProcedure.setBackground(resources.getDrawable(R.drawable.filter_outline))
                apptFollow.setBackground(resources.getDrawable(R.drawable.filter_outline))
                apptOther.setBackground(resources.getDrawable(R.drawable.filter_outline))
                apptFirstVisit.setTextColor(Color.WHITE)
                apptRoutine.setTextColor(resources.getColor(R.color.colorCardBack))
                apptFollow.setTextColor(resources.getColor(R.color.colorCardBack))
                apptProcedure.setTextColor(resources.getColor(R.color.colorCardBack))
                apptAll.setTextColor(resources.getColor(R.color.colorCardBack))
                apptOther.setTextColor(resources.getColor(R.color.colorCardBack))
            }
        })
        consultGroup.clearCheck()
        consultGroup.setOnCheckedChangeListener(RadioGroup.OnCheckedChangeListener { radioGroup, i ->
            if (video.isChecked()) {
                consultFilter = video.getText().toString()
                appointmentActivity!!.consultFilter = consultFilter
                video.setBackground(resources.getDrawable(R.drawable.filter_selected_outline))
                clinic.setBackground(resources.getDrawable(R.drawable.filter_outline))
                allType.setBackground(resources.getDrawable(R.drawable.filter_outline))
                video.setTextColor(Color.WHITE)
                clinic.setTextColor(resources.getColor(R.color.colorCardBack))
                allType.setTextColor(resources.getColor(R.color.colorCardBack))
            } else if (clinic.isChecked()) {
                consultFilter = clinic.getText().toString()
                appointmentActivity!!.consultFilter = consultFilter
                clinic.setBackground(resources.getDrawable(R.drawable.filter_selected_outline))
                video.setBackground(resources.getDrawable(R.drawable.filter_outline))
                allType.setBackground(resources.getDrawable(R.drawable.filter_outline))
                clinic.setTextColor(Color.WHITE)
                video.setTextColor(resources.getColor(R.color.colorCardBack))
                allType.setTextColor(resources.getColor(R.color.colorCardBack))
            } else if (allType.isChecked()) {
                consultFilter = allType.getText().toString()
                appointmentActivity!!.consultFilter = consultFilter
                allType.setBackground(resources.getDrawable(R.drawable.filter_selected_outline))
                clinic.setBackground(resources.getDrawable(R.drawable.filter_outline))
                video.setBackground(resources.getDrawable(R.drawable.filter_outline))
                allType.setTextColor(Color.WHITE)
                clinic.setTextColor(resources.getColor(R.color.colorCardBack))
                video.setTextColor(resources.getColor(R.color.colorCardBack))
            }
        })
        filterClear.setOnClickListener(View.OnClickListener {
            durationFilter = "All"
            appointmentActivity!!.durationFilter = durationFilter
            consultFilter = "All"
            appointmentActivity!!.consultFilter = consultFilter
            apptTypeFilter = "All"
            appointmentActivity!!.apptTypeFilter = "0"
            resetFilterRadioLayout()
            appointmentActivity!!.isFilterApplied = false
        })
        applyFilter.setOnClickListener(View.OnClickListener {
            appointmentActivity!!.isFilterApplied = true
            isBottomsheetFilterApplied = true
            if (apptTypeFilter.equals("All", ignoreCase = true)) {
                apptTypeFilter = "0"
                appointmentActivity!!.apptTypeFilter = apptTypeFilter
            } else if (apptTypeFilter.equals("Routine", ignoreCase = true)) {
                apptTypeFilter = "1"
                appointmentActivity!!.apptTypeFilter = apptTypeFilter
            } else if (apptTypeFilter.equals("Follow Up", ignoreCase = true)) {
                apptTypeFilter = "2"
                appointmentActivity!!.apptTypeFilter = apptTypeFilter
            } else if (apptTypeFilter.equals("Procedure", ignoreCase = true)) {
                apptTypeFilter = "3"
                appointmentActivity!!.apptTypeFilter = apptTypeFilter
            } else if (apptTypeFilter.equals("Other", ignoreCase = true)) {
                apptTypeFilter = "4"
                appointmentActivity!!.apptTypeFilter = apptTypeFilter
            } else if (apptTypeFilter.equals("First Visit", ignoreCase = true)) {
                apptTypeFilter = "6"
                appointmentActivity!!.apptTypeFilter = apptTypeFilter
            }



            PatientSharedRecordsActivity.patientPListModels!!.clear()
            PatientSharedRecordsActivity.sharedRecordsPatientAdater!!.notifyDataSetChanged()
            PatientSharedRecordsActivity.pageNumber = 1
            appointmentActivity!!.getPatientList(
                durationFilter,
                "fname",
                "",
                "asc",
                1.toString() + "",
                "10"
            )
            dialog.dismiss()
        })
    }

    fun resetFilterRadioLayout() {
        all!!.background = resources.getDrawable(R.drawable.filter_outline)
        today!!.background = resources.getDrawable(R.drawable.filter_outline)
        week!!.background = resources.getDrawable(R.drawable.filter_outline)
        month!!.background = resources.getDrawable(R.drawable.filter_outline)
        all!!.setTextColor(resources.getColor(R.color.colorCardBack))
        today!!.setTextColor(resources.getColor(R.color.colorCardBack))
        week!!.setTextColor(resources.getColor(R.color.colorCardBack))
        month!!.setTextColor(resources.getColor(R.color.colorCardBack))
        all!!.isChecked = false
        today!!.isChecked = false
        week!!.isChecked = false
        month!!.isChecked = false

    }

    fun setFilter(durationCase: String?) {
        logUserActionEvent(ApiUrls.doctorId, getString(R.string.DashboardPracticesFilter), null)
        when (durationCase) {
            "All" -> {
                all!!.background = resources.getDrawable(R.drawable.filter_selected_outline)
                all!!.setTextColor(Color.WHITE)
                all!!.isChecked = true
            }
            "Today" -> {
                today!!.background = resources.getDrawable(R.drawable.filter_selected_outline)
                today!!.setTextColor(Color.WHITE)
                today!!.isChecked = true
            }
            "This Week" -> {
                week!!.background = resources.getDrawable(R.drawable.filter_selected_outline)
                week!!.setTextColor(Color.WHITE)
                week!!.isChecked = true
            }
            "This Month" -> {
                month!!.background = resources.getDrawable(R.drawable.filter_selected_outline)
                month!!.setTextColor(Color.WHITE)
                month!!.isChecked = true
            }
        }

    }

    companion object {
        var deleteResponse = 0
    }
}