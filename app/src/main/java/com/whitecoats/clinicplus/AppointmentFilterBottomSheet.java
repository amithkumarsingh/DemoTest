package com.whitecoats.clinicplus;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import androidx.annotation.NonNull;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;

import com.whitecoats.adapter.DashBoardMoreListAdapter;
import com.whitecoats.clinicplus.apis.ApiUrls;
import com.whitecoats.fragments.AppointmentFragment;
import com.whitecoats.model.DashBoardMoreListModel;
import com.zoho.salesiqembed.ZohoSalesIQ;

/**
 * Created by vaibhav on 07-02-2018.
 */

public class AppointmentFilterBottomSheet extends BottomSheetDialogFragment {
    private AppointmentFragment appointmentActivity;
    private RecyclerView patientListRView;
    private List<DashBoardMoreListModel> dashBoardMoreModelList;
    private DashBoardMoreListAdapter dashBoardMoreListAdapter;
    private RelativeLayout videoLayout, clinicLayout;
    private ImageView cancelIcon, videoMode, clinicMode;
    private Spinner durationSpinner;
    private Context mContext;
    private FloatingActionButton fab;
    private FloatingActionButton addIcon;
    private int intValue;
    private AppDatabaseManager appDatabaseManager;
    private SharedPreferences appPreference;
    private ProgressDialog otpLoading;
    public static int deleteResponse;

    public boolean isBottomsheetFilterApplied = false;

    private RadioGroup durationGroup, durationGroup2, consultGroup, apptTypeGroup, apptTypeGroup2, apptTypeGroup3;
    private ImageView filterClear;
    private AppCompatButton applyFilter;
    private RadioButton all, today, week, month, video, clinic, allType, apptAll, apptRoutine, apptFollow, apptProcedure, apptDressing, apptOther, apptFirstVisit;
    private String durationFilter = "All", consultFilter = "All", apptTypeFilter = "All", activePastFilter = "active";

    public String sort = "asc", sortBy = "scheduled_start_time";


    public AppointmentFilterBottomSheet() {
    }

    public void setupConfig(AppointmentFragment appointmentActivity, String durationFilter, String consultFilter,
                            String apptTypeFilter, String activePastFilter) {
        this.appointmentActivity = appointmentActivity;
        this.mContext = getContext();
        this.dashBoardMoreModelList = new ArrayList<>();
        this.durationFilter = durationFilter;
        this.consultFilter = consultFilter;
        this.apptTypeFilter = apptTypeFilter;
        this.activePastFilter = activePastFilter;


        // dashBoardMoreData();

        this.appDatabaseManager = new AppDatabaseManager(mContext);
        //this.appPreference = mContext.getSharedPreferences(appConfigClass.appSharedPref, MODE_PRIVATE);

    }

    //Bottom Sheet Callback
    private BottomSheetBehavior.BottomSheetCallback mBottomSheetBehaviorCallback = new BottomSheetBehavior.BottomSheetCallback() {

        @Override
        public void onStateChanged(@NonNull View bottomSheet, int newState) {
            if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                dismiss();
            }
        }

        @Override
        public void onSlide(@NonNull View bottomSheet, float slideOffset) {
        }
    };

    @Override
    public void setupDialog(final Dialog dialog, int style) {
//        super.setupDialog(dialog, style);
        //Get the content View
        final View contentView = View.inflate(getContext(), R.layout.fragment_bottom_sheet_appointment_filter, null);
        dialog.setContentView(contentView);
        //Set the coordinator layout behavior
        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) ((View) contentView.getParent()).getLayoutParams();
        CoordinatorLayout.Behavior behavior = params.getBehavior();
        cancelIcon = contentView.findViewById(R.id.bottomSheetCancelIcon);
//        durationSpinner = contentView.findViewById(R.id.bottomSheetCancelIcon);
//        videoLayout  = contentView.findViewById(R.id.videoLayout);
//        clinicLayout  = contentView.findViewById(R.id.clinicLayout);
//        videoMode  = contentView.findViewById(R.id.videoMode);
//        clinicMode  = contentView.findViewById(R.id.clinicMode);
        //LinearLayoutManager llm = new LinearLayoutManager(mContext);
        //RecyclerView recList = contentView.findViewById(R.id.bottomSheetCardList);

//        ArrayAdapter<String> adapter = new ArrayAdapter<String>(mContext,
//                android.R.layout.simple_spinner_item, mContext.getResources()
//                .getStringArray(R.array.DurationListArray));
//        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        durationSpinner.setAdapter(adapter);

//        videoLayout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                videoLayout.setBackgroundColor(getResources().getColor(R.color.colorAccent));
//                Drawable videoDrawable = getResources().getDrawable(R.drawable.ic_video);
//                videoDrawable.setColorFilter(getResources().getColor(R.color.colorWhite), PorterDuff.Mode.SRC_ATOP);
//                videoMode.setImageDrawable(videoDrawable);
//
//            }
//        });
//
//        clinicLayout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                clinicLayout.setBackgroundColor(getResources().getColor(R.color.colorAccent));
//                Drawable clinicDrawable = getResources().getDrawable(R.drawable.ic_hospital);
//                clinicDrawable.setColorFilter(getResources().getColor(R.color.colorWhite), PorterDuff.Mode.SRC_ATOP);
//                clinicMode.setImageDrawable(clinicDrawable);
//
//            }
//        });
//
//
//        String durationSelection = durationSpinner.getSelectedItem().toString();


        //Set callback
        if (behavior != null && behavior instanceof BottomSheetBehavior) {
            ((BottomSheetBehavior) behavior).setBottomSheetCallback(mBottomSheetBehaviorCallback);
        }
        //recList.setHasFixedSize(true);
        //llm.setOrientation(LinearLayoutManager.VERTICAL);
        // recList.setLayoutManager(llm);
        //recList.setAdapter(dashBoardMoreListAdapter);

//        cancelIcon.setColorFilter(getResources().getColor(R.color.colorWhite), PorterDuff.Mode.SRC_IN);
//        cancelIcon.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                dialog.dismiss();
//
//            }
//        });


        durationGroup = contentView.findViewById(R.id.filterDurationGroup);
        consultGroup = contentView.findViewById(R.id.filterConsultGroup);
        apptTypeGroup = contentView.findViewById(R.id.filterApptTypeGroup);
        apptTypeGroup2 = contentView.findViewById(R.id.filterApptTypeGroup2);
        apptTypeGroup3 = contentView.findViewById(R.id.filterApptTypeGroup3);
        filterClear = contentView.findViewById(R.id.filterClear);
        all = contentView.findViewById(R.id.filterDurationAll);
        today = contentView.findViewById(R.id.filterDurationToday);
        week = contentView.findViewById(R.id.filterDurationWeek);
        month = contentView.findViewById(R.id.filterDurationMonth);
        video = contentView.findViewById(R.id.filterVideoType);
        clinic = contentView.findViewById(R.id.filterClinicType);
        allType = contentView.findViewById(R.id.filterAllType);

        //old
//        apptAll = contentView.findViewById(R.id.filterApptTypeAll);
//        apptRoutine = contentView.findViewById(R.id.filterApptTypeRoutine);
//        apptFollow = contentView.findViewById(R.id.filterApptTypeFollow);
//
//        apptProcedure = contentView.findViewById(R.id.filterApptTypeProcedure);
//        apptOther = contentView.findViewById(R.id.filterApptTypeOther);
//        apptFirstVisit = contentView.findViewById(R.id.filterApptTypeFirstVisit);

        //New
        apptAll = contentView.findViewById(R.id.filterApptTypeAll);
        apptFirstVisit = contentView.findViewById(R.id.filterApptTypeFirstVisit);
        apptRoutine = contentView.findViewById(R.id.filterApptTypeRoutine);

        apptFollow = contentView.findViewById(R.id.filterApptTypeFollow);
        apptProcedure = contentView.findViewById(R.id.filterApptTypeProcedure);

        apptDressing = contentView.findViewById(R.id.filterApptTypeDressingPlaster);
        apptOther = contentView.findViewById(R.id.filterApptTypeOther);


        applyFilter = contentView.findViewById(R.id.filterApptApply);

        setFilter(durationFilter, consultFilter, apptTypeFilter);

        durationGroup.clearCheck();
        durationGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {

                ZohoSalesIQ.Tracking.setCustomAction("ApptList - BottomSheet - Duration Filter");
                if (all.isChecked()) {
                    durationFilter = all.getText().toString();
                    appointmentActivity.durationFilter = durationFilter;

                    all.setBackground(getResources().getDrawable(R.drawable.filter_selected_outline));
                    today.setBackground(getResources().getDrawable(R.drawable.filter_outline));
                    week.setBackground(getResources().getDrawable(R.drawable.filter_outline));
                    month.setBackground(getResources().getDrawable(R.drawable.filter_outline));

                    all.setTextColor(Color.WHITE);
                    today.setTextColor(getResources().getColor(R.color.colorCardBack));
                    week.setTextColor(getResources().getColor(R.color.colorCardBack));
                    month.setTextColor(getResources().getColor(R.color.colorCardBack));
                } else if (today.isChecked()) {
                    durationFilter = today.getText().toString();
                    appointmentActivity.durationFilter = durationFilter;

                    today.setBackground(getResources().getDrawable(R.drawable.filter_selected_outline));
                    all.setBackground(getResources().getDrawable(R.drawable.filter_outline));
                    week.setBackground(getResources().getDrawable(R.drawable.filter_outline));
                    month.setBackground(getResources().getDrawable(R.drawable.filter_outline));

                    today.setTextColor(Color.WHITE);
                    all.setTextColor(getResources().getColor(R.color.colorCardBack));
                    week.setTextColor(getResources().getColor(R.color.colorCardBack));
                    month.setTextColor(getResources().getColor(R.color.colorCardBack));
                } else if (week.isChecked()) {
                    durationFilter = "This " + week.getText().toString();
                    appointmentActivity.durationFilter = durationFilter;

                    week.setBackground(getResources().getDrawable(R.drawable.filter_selected_outline));
                    all.setBackground(getResources().getDrawable(R.drawable.filter_outline));
                    today.setBackground(getResources().getDrawable(R.drawable.filter_outline));
                    month.setBackground(getResources().getDrawable(R.drawable.filter_outline));

                    week.setTextColor(Color.WHITE);
                    all.setTextColor(getResources().getColor(R.color.colorCardBack));
                    today.setTextColor(getResources().getColor(R.color.colorCardBack));
                    month.setTextColor(getResources().getColor(R.color.colorCardBack));
                } else if (month.isChecked()) {
                    durationFilter = "This " + month.getText().toString();
                    appointmentActivity.durationFilter = durationFilter;

                    month.setBackground(getResources().getDrawable(R.drawable.filter_selected_outline));
                    all.setBackground(getResources().getDrawable(R.drawable.filter_outline));
                    today.setBackground(getResources().getDrawable(R.drawable.filter_outline));
                    week.setBackground(getResources().getDrawable(R.drawable.filter_outline));

                    month.setTextColor(Color.WHITE);
                    all.setTextColor(getResources().getColor(R.color.colorCardBack));
                    week.setTextColor(getResources().getColor(R.color.colorCardBack));
                    today.setTextColor(getResources().getColor(R.color.colorCardBack));
                }
            }
        });


        apptTypeGroup.clearCheck();
        apptTypeGroup2.clearCheck();
        apptTypeGroup3.clearCheck();
        apptTypeGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                ZohoSalesIQ.Tracking.setCustomAction("ApptList - BottomSheet - Appt Type Filter");
                if (apptAll.isChecked()) {
                    apptTypeFilter = apptAll.getText().toString();

                    apptAll.setBackground(getResources().getDrawable(R.drawable.filter_selected_outline));
                    apptFirstVisit.setBackground(getResources().getDrawable(R.drawable.filter_outline));
                    apptRoutine.setBackground(getResources().getDrawable(R.drawable.filter_outline));
                    apptFollow.setBackground(getResources().getDrawable(R.drawable.filter_outline));
                    apptProcedure.setBackground(getResources().getDrawable(R.drawable.filter_outline));
                    apptDressing.setBackground(getResources().getDrawable(R.drawable.filter_outline));
                    apptOther.setBackground(getResources().getDrawable(R.drawable.filter_outline));

                    apptAll.setTextColor(Color.WHITE);
                    apptFirstVisit.setTextColor(getResources().getColor(R.color.colorCardBack));
                    apptRoutine.setTextColor(getResources().getColor(R.color.colorCardBack));
                    apptFollow.setTextColor(getResources().getColor(R.color.colorCardBack));
                    apptProcedure.setTextColor(getResources().getColor(R.color.colorCardBack));
                    apptDressing.setTextColor(getResources().getColor(R.color.colorCardBack));
                    apptOther.setTextColor(getResources().getColor(R.color.colorCardBack));
                } else if (apptFirstVisit.isChecked()) {
                    apptTypeFilter = apptFirstVisit.getText().toString();

                    apptFirstVisit.setBackground(getResources().getDrawable(R.drawable.filter_selected_outline));
                    apptRoutine.setBackground(getResources().getDrawable(R.drawable.filter_outline));
                    apptAll.setBackground(getResources().getDrawable(R.drawable.filter_outline));
                    apptProcedure.setBackground(getResources().getDrawable(R.drawable.filter_outline));
                    apptDressing.setBackground(getResources().getDrawable(R.drawable.filter_outline));
                    apptFollow.setBackground(getResources().getDrawable(R.drawable.filter_outline));
                    apptOther.setBackground(getResources().getDrawable(R.drawable.filter_outline));

                    apptFirstVisit.setTextColor(Color.WHITE);
                    apptRoutine.setTextColor(getResources().getColor(R.color.colorCardBack));
                    apptFollow.setTextColor(getResources().getColor(R.color.colorCardBack));
                    apptProcedure.setTextColor(getResources().getColor(R.color.colorCardBack));
                    apptDressing.setTextColor(getResources().getColor(R.color.colorCardBack));
                    apptAll.setTextColor(getResources().getColor(R.color.colorCardBack));
                    apptOther.setTextColor(getResources().getColor(R.color.colorCardBack));
                } else if (apptRoutine.isChecked()) {
                    apptTypeFilter = apptRoutine.getText().toString();

                    apptRoutine.setBackground(getResources().getDrawable(R.drawable.filter_selected_outline));
                    apptAll.setBackground(getResources().getDrawable(R.drawable.filter_outline));
                    apptFollow.setBackground(getResources().getDrawable(R.drawable.filter_outline));
                    apptProcedure.setBackground(getResources().getDrawable(R.drawable.filter_outline));
                    apptDressing.setBackground(getResources().getDrawable(R.drawable.filter_outline));
                    apptOther.setBackground(getResources().getDrawable(R.drawable.filter_outline));
                    apptFirstVisit.setBackground(getResources().getDrawable(R.drawable.filter_outline));

                    apptRoutine.setTextColor(Color.WHITE);
                    apptAll.setTextColor(getResources().getColor(R.color.colorCardBack));
                    apptFollow.setTextColor(getResources().getColor(R.color.colorCardBack));
                    apptProcedure.setTextColor(getResources().getColor(R.color.colorCardBack));
                    apptDressing.setTextColor(getResources().getColor(R.color.colorCardBack));
                    apptOther.setTextColor(getResources().getColor(R.color.colorCardBack));
                    apptFirstVisit.setTextColor(getResources().getColor(R.color.colorCardBack));
                }
            }
        });

        apptTypeGroup2.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {

                ZohoSalesIQ.Tracking.setCustomAction("ApptList - BottomSheet - Appt Type Filter");
                // This will get the radiobutton that has changed clinicplus its check state
                if (apptFollow.isChecked()) {
                    apptTypeFilter = apptFollow.getText().toString();

                    apptFollow.setBackground(getResources().getDrawable(R.drawable.filter_selected_outline));
                    apptRoutine.setBackground(getResources().getDrawable(R.drawable.filter_outline));
                    apptAll.setBackground(getResources().getDrawable(R.drawable.filter_outline));
                    apptProcedure.setBackground(getResources().getDrawable(R.drawable.filter_outline));
                    apptDressing.setBackground(getResources().getDrawable(R.drawable.filter_outline));
                    apptOther.setBackground(getResources().getDrawable(R.drawable.filter_outline));
                    apptFirstVisit.setBackground(getResources().getDrawable(R.drawable.filter_outline));

                    apptFollow.setTextColor(Color.WHITE);
                    apptRoutine.setTextColor(getResources().getColor(R.color.colorCardBack));
                    apptAll.setTextColor(getResources().getColor(R.color.colorCardBack));
                    apptProcedure.setTextColor(getResources().getColor(R.color.colorCardBack));
                    apptDressing.setTextColor(getResources().getColor(R.color.colorCardBack));
                    apptOther.setTextColor(getResources().getColor(R.color.colorCardBack));
                    apptFirstVisit.setTextColor(getResources().getColor(R.color.colorCardBack));
                } else if (apptProcedure.isChecked()) {
                    apptTypeFilter = apptProcedure.getText().toString();

                    apptProcedure.setBackground(getResources().getDrawable(R.drawable.filter_selected_outline));
                    apptRoutine.setBackground(getResources().getDrawable(R.drawable.filter_outline));
                    apptAll.setBackground(getResources().getDrawable(R.drawable.filter_outline));
                    apptFollow.setBackground(getResources().getDrawable(R.drawable.filter_outline));
                    apptDressing.setBackground(getResources().getDrawable(R.drawable.filter_outline));
                    apptOther.setBackground(getResources().getDrawable(R.drawable.filter_outline));
                    apptFirstVisit.setBackground(getResources().getDrawable(R.drawable.filter_outline));

                    apptProcedure.setTextColor(Color.WHITE);
                    apptRoutine.setTextColor(getResources().getColor(R.color.colorCardBack));
                    apptFollow.setTextColor(getResources().getColor(R.color.colorCardBack));
                    apptDressing.setTextColor(getResources().getColor(R.color.colorCardBack));
                    apptAll.setTextColor(getResources().getColor(R.color.colorCardBack));
                    apptOther.setTextColor(getResources().getColor(R.color.colorCardBack));
                    apptFirstVisit.setTextColor(getResources().getColor(R.color.colorCardBack));
                }
            }
        });

        apptTypeGroup3.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {

                // This will get the radiobutton that has changed clinicplus its check state
                ZohoSalesIQ.Tracking.setCustomAction("ApptList - BottomSheet - Appt Type Filter");

                if (apptDressing.isChecked()) {

                    apptTypeFilter = apptDressing.getText().toString();

                    apptDressing.setBackground(getResources().getDrawable(R.drawable.filter_selected_outline));
                    apptProcedure.setBackground(getResources().getDrawable(R.drawable.filter_outline));
                    apptRoutine.setBackground(getResources().getDrawable(R.drawable.filter_outline));
                    apptAll.setBackground(getResources().getDrawable(R.drawable.filter_outline));
                    apptFollow.setBackground(getResources().getDrawable(R.drawable.filter_outline));
                    apptOther.setBackground(getResources().getDrawable(R.drawable.filter_outline));
                    apptFirstVisit.setBackground(getResources().getDrawable(R.drawable.filter_outline));

                    apptDressing.setTextColor(Color.WHITE);
                    apptProcedure.setTextColor(getResources().getColor(R.color.colorCardBack));
                    apptRoutine.setTextColor(getResources().getColor(R.color.colorCardBack));
                    apptFollow.setTextColor(getResources().getColor(R.color.colorCardBack));
                    apptAll.setTextColor(getResources().getColor(R.color.colorCardBack));
                    apptOther.setTextColor(getResources().getColor(R.color.colorCardBack));
                    apptFirstVisit.setTextColor(getResources().getColor(R.color.colorCardBack));
                } else if (apptOther.isChecked()) {
                    apptTypeFilter = apptOther.getText().toString();

                    apptOther.setBackground(getResources().getDrawable(R.drawable.filter_selected_outline));
                    apptRoutine.setBackground(getResources().getDrawable(R.drawable.filter_outline));
                    apptAll.setBackground(getResources().getDrawable(R.drawable.filter_outline));
                    apptProcedure.setBackground(getResources().getDrawable(R.drawable.filter_outline));
                    apptDressing.setBackground(getResources().getDrawable(R.drawable.filter_outline));
                    apptFollow.setBackground(getResources().getDrawable(R.drawable.filter_outline));
                    apptFirstVisit.setBackground(getResources().getDrawable(R.drawable.filter_outline));

                    apptOther.setTextColor(Color.WHITE);
                    apptRoutine.setTextColor(getResources().getColor(R.color.colorCardBack));
                    apptFollow.setTextColor(getResources().getColor(R.color.colorCardBack));
                    apptProcedure.setTextColor(getResources().getColor(R.color.colorCardBack));
                    apptDressing.setTextColor(getResources().getColor(R.color.colorCardBack));
                    apptAll.setTextColor(getResources().getColor(R.color.colorCardBack));
                    apptFirstVisit.setTextColor(getResources().getColor(R.color.colorCardBack));
                }
            }
        });

//        apptTypeGroup.clearCheck();
//        apptTypeGroup2.clearCheck();
//        apptTypeGroup3.clearCheck();
//        apptTypeGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(RadioGroup radioGroup, int i) {
//                if (apptAll.isChecked()) {
//                    apptTypeFilter = apptAll.getText().toString();
//
//                    apptAll.setBackground(getResources().getDrawable(R.drawable.filter_selected_outline));
//                    apptRoutine.setBackground(getResources().getDrawable(R.drawable.filter_outline));
//                    apptFollow.setBackground(getResources().getDrawable(R.drawable.filter_outline));
//                    apptProcedure.setBackground(getResources().getDrawable(R.drawable.filter_outline));
//                    apptOther.setBackground(getResources().getDrawable(R.drawable.filter_outline));
//                    apptFirstVisit.setBackground(getResources().getDrawable(R.drawable.filter_outline));
//
//                    apptAll.setTextColor(Color.WHITE);
//                    apptRoutine.setTextColor(getResources().getColor(R.color.colorCardBack));
//                    apptFollow.setTextColor(getResources().getColor(R.color.colorCardBack));
//                    apptProcedure.setTextColor(getResources().getColor(R.color.colorCardBack));
//                    apptOther.setTextColor(getResources().getColor(R.color.colorCardBack));
//                    apptFirstVisit.setTextColor(getResources().getColor(R.color.colorCardBack));
//                } else if (apptRoutine.isChecked()) {
//                    apptTypeFilter = apptRoutine.getText().toString();
//
//                    apptRoutine.setBackground(getResources().getDrawable(R.drawable.filter_selected_outline));
//                    apptAll.setBackground(getResources().getDrawable(R.drawable.filter_outline));
//                    apptFollow.setBackground(getResources().getDrawable(R.drawable.filter_outline));
//                    apptProcedure.setBackground(getResources().getDrawable(R.drawable.filter_outline));
//                    apptOther.setBackground(getResources().getDrawable(R.drawable.filter_outline));
//                    apptFirstVisit.setBackground(getResources().getDrawable(R.drawable.filter_outline));
//
//                    apptRoutine.setTextColor(Color.WHITE);
//                    apptAll.setTextColor(getResources().getColor(R.color.colorCardBack));
//                    apptFollow.setTextColor(getResources().getColor(R.color.colorCardBack));
//                    apptProcedure.setTextColor(getResources().getColor(R.color.colorCardBack));
//                    apptOther.setTextColor(getResources().getColor(R.color.colorCardBack));
//                    apptFirstVisit.setTextColor(getResources().getColor(R.color.colorCardBack));
//                } else if (apptFollow.isChecked()) {
//                    apptTypeFilter = apptFollow.getText().toString();
//
//                    apptFollow.setBackground(getResources().getDrawable(R.drawable.filter_selected_outline));
//                    apptRoutine.setBackground(getResources().getDrawable(R.drawable.filter_outline));
//                    apptAll.setBackground(getResources().getDrawable(R.drawable.filter_outline));
//                    apptProcedure.setBackground(getResources().getDrawable(R.drawable.filter_outline));
//                    apptOther.setBackground(getResources().getDrawable(R.drawable.filter_outline));
//                    apptFirstVisit.setBackground(getResources().getDrawable(R.drawable.filter_outline));
//
//                    apptFollow.setTextColor(Color.WHITE);
//                    apptRoutine.setTextColor(getResources().getColor(R.color.colorCardBack));
//                    apptAll.setTextColor(getResources().getColor(R.color.colorCardBack));
//                    apptProcedure.setTextColor(getResources().getColor(R.color.colorCardBack));
//                    apptOther.setTextColor(getResources().getColor(R.color.colorCardBack));
//                    apptFirstVisit.setTextColor(getResources().getColor(R.color.colorCardBack));
//                }
//            }
//        });
//
//        apptTypeGroup2.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(RadioGroup radioGroup, int i) {
//
//                // This will get the radiobutton that has changed clinicplus its check state
//                if (apptProcedure.isChecked()) {
//                    apptTypeFilter = apptProcedure.getText().toString();
//
//                    apptProcedure.setBackground(getResources().getDrawable(R.drawable.filter_selected_outline));
//                    apptRoutine.setBackground(getResources().getDrawable(R.drawable.filter_outline));
//                    apptAll.setBackground(getResources().getDrawable(R.drawable.filter_outline));
//                    apptFollow.setBackground(getResources().getDrawable(R.drawable.filter_outline));
//                    apptOther.setBackground(getResources().getDrawable(R.drawable.filter_outline));
//                    apptFirstVisit.setBackground(getResources().getDrawable(R.drawable.filter_outline));
//
//                    apptProcedure.setTextColor(Color.WHITE);
//                    apptRoutine.setTextColor(getResources().getColor(R.color.colorCardBack));
//                    apptFollow.setTextColor(getResources().getColor(R.color.colorCardBack));
//                    apptAll.setTextColor(getResources().getColor(R.color.colorCardBack));
//                    apptOther.setTextColor(getResources().getColor(R.color.colorCardBack));
//                    apptFirstVisit.setTextColor(getResources().getColor(R.color.colorCardBack));
//                } else if (apptOther.isChecked()) {
//                    apptTypeFilter = apptOther.getText().toString();
//
//                    apptOther.setBackground(getResources().getDrawable(R.drawable.filter_selected_outline));
//                    apptRoutine.setBackground(getResources().getDrawable(R.drawable.filter_outline));
//                    apptAll.setBackground(getResources().getDrawable(R.drawable.filter_outline));
//                    apptProcedure.setBackground(getResources().getDrawable(R.drawable.filter_outline));
//                    apptFollow.setBackground(getResources().getDrawable(R.drawable.filter_outline));
//                    apptFirstVisit.setBackground(getResources().getDrawable(R.drawable.filter_outline));
//
//                    apptOther.setTextColor(Color.WHITE);
//                    apptRoutine.setTextColor(getResources().getColor(R.color.colorCardBack));
//                    apptFollow.setTextColor(getResources().getColor(R.color.colorCardBack));
//                    apptProcedure.setTextColor(getResources().getColor(R.color.colorCardBack));
//                    apptAll.setTextColor(getResources().getColor(R.color.colorCardBack));
//                    apptFirstVisit.setTextColor(getResources().getColor(R.color.colorCardBack));
//                } else if (apptFirstVisit.isChecked()) {
//                    apptTypeFilter = apptFirstVisit.getText().toString();
//
//                    apptFirstVisit.setBackground(getResources().getDrawable(R.drawable.filter_selected_outline));
//                    apptRoutine.setBackground(getResources().getDrawable(R.drawable.filter_outline));
//                    apptAll.setBackground(getResources().getDrawable(R.drawable.filter_outline));
//                    apptProcedure.setBackground(getResources().getDrawable(R.drawable.filter_outline));
//                    apptFollow.setBackground(getResources().getDrawable(R.drawable.filter_outline));
//                    apptOther.setBackground(getResources().getDrawable(R.drawable.filter_outline));
//
//                    apptFirstVisit.setTextColor(Color.WHITE);
//                    apptRoutine.setTextColor(getResources().getColor(R.color.colorCardBack));
//                    apptFollow.setTextColor(getResources().getColor(R.color.colorCardBack));
//                    apptProcedure.setTextColor(getResources().getColor(R.color.colorCardBack));
//                    apptAll.setTextColor(getResources().getColor(R.color.colorCardBack));
//                    apptOther.setTextColor(getResources().getColor(R.color.colorCardBack));
//                }
//            }
//        });
//
//        apptTypeGroup3.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(RadioGroup radioGroup, int i) {
//
//                // This will get the radiobutton that has changed clinicplus its check state
//                if (apptProcedure.isChecked()) {
//                    apptTypeFilter = apptProcedure.getText().toString();
//
//                    apptProcedure.setBackground(getResources().getDrawable(R.drawable.filter_selected_outline));
//                    apptRoutine.setBackground(getResources().getDrawable(R.drawable.filter_outline));
//                    apptAll.setBackground(getResources().getDrawable(R.drawable.filter_outline));
//                    apptFollow.setBackground(getResources().getDrawable(R.drawable.filter_outline));
//                    apptOther.setBackground(getResources().getDrawable(R.drawable.filter_outline));
//                    apptFirstVisit.setBackground(getResources().getDrawable(R.drawable.filter_outline));
//
//                    apptProcedure.setTextColor(Color.WHITE);
//                    apptRoutine.setTextColor(getResources().getColor(R.color.colorCardBack));
//                    apptFollow.setTextColor(getResources().getColor(R.color.colorCardBack));
//                    apptAll.setTextColor(getResources().getColor(R.color.colorCardBack));
//                    apptOther.setTextColor(getResources().getColor(R.color.colorCardBack));
//                    apptFirstVisit.setTextColor(getResources().getColor(R.color.colorCardBack));
//                } else if (apptOther.isChecked()) {
//                    apptTypeFilter = apptOther.getText().toString();
//
//                    apptOther.setBackground(getResources().getDrawable(R.drawable.filter_selected_outline));
//                    apptRoutine.setBackground(getResources().getDrawable(R.drawable.filter_outline));
//                    apptAll.setBackground(getResources().getDrawable(R.drawable.filter_outline));
//                    apptProcedure.setBackground(getResources().getDrawable(R.drawable.filter_outline));
//                    apptFollow.setBackground(getResources().getDrawable(R.drawable.filter_outline));
//                    apptFirstVisit.setBackground(getResources().getDrawable(R.drawable.filter_outline));
//
//                    apptOther.setTextColor(Color.WHITE);
//                    apptRoutine.setTextColor(getResources().getColor(R.color.colorCardBack));
//                    apptFollow.setTextColor(getResources().getColor(R.color.colorCardBack));
//                    apptProcedure.setTextColor(getResources().getColor(R.color.colorCardBack));
//                    apptAll.setTextColor(getResources().getColor(R.color.colorCardBack));
//                    apptFirstVisit.setTextColor(getResources().getColor(R.color.colorCardBack));
//                } else if (apptFirstVisit.isChecked()) {
//                    apptTypeFilter = apptFirstVisit.getText().toString();
//
//                    apptFirstVisit.setBackground(getResources().getDrawable(R.drawable.filter_selected_outline));
//                    apptRoutine.setBackground(getResources().getDrawable(R.drawable.filter_outline));
//                    apptAll.setBackground(getResources().getDrawable(R.drawable.filter_outline));
//                    apptProcedure.setBackground(getResources().getDrawable(R.drawable.filter_outline));
//                    apptFollow.setBackground(getResources().getDrawable(R.drawable.filter_outline));
//                    apptOther.setBackground(getResources().getDrawable(R.drawable.filter_outline));
//
//                    apptFirstVisit.setTextColor(Color.WHITE);
//                    apptRoutine.setTextColor(getResources().getColor(R.color.colorCardBack));
//                    apptFollow.setTextColor(getResources().getColor(R.color.colorCardBack));
//                    apptProcedure.setTextColor(getResources().getColor(R.color.colorCardBack));
//                    apptAll.setTextColor(getResources().getColor(R.color.colorCardBack));
//                    apptOther.setTextColor(getResources().getColor(R.color.colorCardBack));
//                }
//            }
//        });


        consultGroup.clearCheck();
        consultGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                ZohoSalesIQ.Tracking.setCustomAction("ApptList - BottomSheet - Consult Type Filter");
                if (video.isChecked()) {
                    consultFilter = video.getText().toString();
                    appointmentActivity.consultFilter = consultFilter;

                    video.setBackground(getResources().getDrawable(R.drawable.filter_selected_outline));
                    clinic.setBackground(getResources().getDrawable(R.drawable.filter_outline));
                    allType.setBackground(getResources().getDrawable(R.drawable.filter_outline));

                    video.setTextColor(Color.WHITE);
                    clinic.setTextColor(getResources().getColor(R.color.colorCardBack));
                    allType.setTextColor(getResources().getColor(R.color.colorCardBack));
                } else if (clinic.isChecked()) {
                    consultFilter = clinic.getText().toString();
                    appointmentActivity.consultFilter = consultFilter;

                    clinic.setBackground(getResources().getDrawable(R.drawable.filter_selected_outline));
                    video.setBackground(getResources().getDrawable(R.drawable.filter_outline));
                    allType.setBackground(getResources().getDrawable(R.drawable.filter_outline));

                    clinic.setTextColor(Color.WHITE);
                    video.setTextColor(getResources().getColor(R.color.colorCardBack));
                    allType.setTextColor(getResources().getColor(R.color.colorCardBack));
                } else if (allType.isChecked()) {
                    consultFilter = allType.getText().toString();
                    appointmentActivity.consultFilter = consultFilter;

                    allType.setBackground(getResources().getDrawable(R.drawable.filter_selected_outline));
                    clinic.setBackground(getResources().getDrawable(R.drawable.filter_outline));
                    video.setBackground(getResources().getDrawable(R.drawable.filter_outline));

                    allType.setTextColor(Color.WHITE);
                    clinic.setTextColor(getResources().getColor(R.color.colorCardBack));
                    video.setTextColor(getResources().getColor(R.color.colorCardBack));
                }
            }
        });

        filterClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ZohoSalesIQ.Tracking.setCustomAction("ApptList - BottomSheet - Clear Filter");
                durationFilter = "All";
                appointmentActivity.durationFilter = durationFilter;
                consultFilter = "All";
                appointmentActivity.consultFilter = consultFilter;
                apptTypeFilter = "All";
                appointmentActivity.apptTypeFilter = "0";

                resetFilterRadioLayout();
                appointmentActivity.isFilterApplied = false;

            }
        });

        applyFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ZohoSalesIQ.Tracking.setCustomAction("ApptList - BottomSheet - Apply Filter");
//                Log.d("Duration Filter", durationFilter);
//                Log.d("Condult Filter", consultFilter);
//                Log.d("Type Filter", apptTypeFilter);
                appointmentActivity.isFilterApplied = true;
                isBottomsheetFilterApplied = true;

                if (apptTypeFilter.equalsIgnoreCase("All")) {
                    apptTypeFilter = "0";
                    appointmentActivity.apptTypeFilter = apptTypeFilter;
                } else if (apptTypeFilter.equalsIgnoreCase("Routine")) {
                    apptTypeFilter = "1";
                    appointmentActivity.apptTypeFilter = apptTypeFilter;
                } else if (apptTypeFilter.equalsIgnoreCase("Follow Up")) {
                    apptTypeFilter = "2";
                    appointmentActivity.apptTypeFilter = apptTypeFilter;
                } else if (apptTypeFilter.equalsIgnoreCase("Procedure/Vaccination")) {
                    apptTypeFilter = "3";
                    appointmentActivity.apptTypeFilter = apptTypeFilter;
                } else if (apptTypeFilter.equalsIgnoreCase("Dressing/Plaster")) {
                    apptTypeFilter = "4";
                    appointmentActivity.apptTypeFilter = apptTypeFilter;
                } else if (apptTypeFilter.equalsIgnoreCase("Other")) {
                    apptTypeFilter = "5";
                    appointmentActivity.apptTypeFilter = apptTypeFilter;
                } else if (apptTypeFilter.equalsIgnoreCase("First Visit")) {
                    apptTypeFilter = "6";
                    appointmentActivity.apptTypeFilter = apptTypeFilter;
                }

                AppointmentFragment.all.setBackground(getResources().getDrawable(R.drawable.filter_outline));
                AppointmentFragment.today.setBackground(getResources().getDrawable(R.drawable.filter_outline));
                AppointmentFragment.week.setBackground(getResources().getDrawable(R.drawable.filter_outline));
                AppointmentFragment.month.setBackground(getResources().getDrawable(R.drawable.filter_outline));

                AppointmentFragment.all.setTextColor(getResources().getColor(R.color.colorCardBack));
                AppointmentFragment.today.setTextColor(getResources().getColor(R.color.colorCardBack));
                AppointmentFragment.week.setTextColor(getResources().getColor(R.color.colorCardBack));
                AppointmentFragment.month.setTextColor(getResources().getColor(R.color.colorCardBack));

                AppointmentFragment.all.setChecked(false);
                AppointmentFragment.today.setChecked(false);
                AppointmentFragment.week.setChecked(false);
                AppointmentFragment.month.setChecked(false);

                if (durationFilter.equalsIgnoreCase("All")) {
                    AppointmentFragment.all.setBackground(getResources().getDrawable(R.drawable.filter_selected_outline));
                    AppointmentFragment.all.setTextColor(Color.WHITE);
                } else if (durationFilter.equalsIgnoreCase("Today")) {
                    AppointmentFragment.today.setBackground(getResources().getDrawable(R.drawable.filter_selected_outline));
                    AppointmentFragment.today.setTextColor(Color.WHITE);
                } else if (durationFilter.equalsIgnoreCase("This Week")) {
                    AppointmentFragment.week.setBackground(getResources().getDrawable(R.drawable.filter_selected_outline));
                    AppointmentFragment.week.setTextColor(Color.WHITE);
                } else if (durationFilter.equalsIgnoreCase("This Month")) {
                    AppointmentFragment.month.setBackground(getResources().getDrawable(R.drawable.filter_selected_outline));
                    AppointmentFragment.month.setTextColor(Color.WHITE);
                }

                appointmentActivity.appointmentApptListModelList.clear();
//                appointmentActivity.setFilter(durationFilter);
                appointmentActivity.Companion.setPageNumber(1);
//                Log.d("Appointment Filter", "**********************");

                if (durationFilter.equalsIgnoreCase("All") && consultFilter.equalsIgnoreCase("All")  && apptTypeFilter.equalsIgnoreCase("0")){
                   // appointmentActivity.getAppointmentList(durationFilter, consultFilter,sort, sortBy, "", "1", "10", activePastFilter, apptTypeFilter, AppConfigClass.doctorId + "","no appointments found when given filter apply");
                    appointmentActivity.getAppointmentList(durationFilter, consultFilter, "", sort, sortBy, "1", "50", activePastFilter, apptTypeFilter, ApiUrls.doctorId + "", "No Records Found.");
                }else {
                    appointmentActivity.getAppointmentList(durationFilter, consultFilter, "", sort, sortBy, "1", "50", activePastFilter, apptTypeFilter, ApiUrls.doctorId + "", "No appointments found when given filter apply");
                }

                dialog.dismiss();
            }
        });

    }


    void resetFilterRadioLayout() {
        all.setBackground(getResources().getDrawable(R.drawable.filter_outline));
        today.setBackground(getResources().getDrawable(R.drawable.filter_outline));
        week.setBackground(getResources().getDrawable(R.drawable.filter_outline));
        month.setBackground(getResources().getDrawable(R.drawable.filter_outline));
        apptAll.setBackground(getResources().getDrawable(R.drawable.filter_outline));
        apptRoutine.setBackground(getResources().getDrawable(R.drawable.filter_outline));
        apptFollow.setBackground(getResources().getDrawable(R.drawable.filter_outline));
        apptProcedure.setBackground(getResources().getDrawable(R.drawable.filter_outline));
        apptDressing.setBackground(getResources().getDrawable(R.drawable.filter_outline));// added by dileep
        apptOther.setBackground(getResources().getDrawable(R.drawable.filter_outline));
        apptFirstVisit.setBackground(getResources().getDrawable(R.drawable.filter_outline));
        video.setBackground(getResources().getDrawable(R.drawable.filter_outline));
        clinic.setBackground(getResources().getDrawable(R.drawable.filter_outline));
        allType.setBackground(getResources().getDrawable(R.drawable.filter_outline));

        all.setTextColor(getResources().getColor(R.color.colorCardBack));
        today.setTextColor(getResources().getColor(R.color.colorCardBack));
        week.setTextColor(getResources().getColor(R.color.colorCardBack));
        month.setTextColor(getResources().getColor(R.color.colorCardBack));
        clinic.setTextColor(getResources().getColor(R.color.colorCardBack));
        video.setTextColor(getResources().getColor(R.color.colorCardBack));
        allType.setTextColor(getResources().getColor(R.color.colorCardBack));
        apptProcedure.setTextColor(getResources().getColor(R.color.colorCardBack));
        apptDressing.setTextColor(getResources().getColor(R.color.colorCardBack));// added by dileep
        apptRoutine.setTextColor(getResources().getColor(R.color.colorCardBack));
        apptFollow.setTextColor(getResources().getColor(R.color.colorCardBack));
        apptAll.setTextColor(getResources().getColor(R.color.colorCardBack));
        apptOther.setTextColor(getResources().getColor(R.color.colorCardBack));
        apptFirstVisit.setTextColor(getResources().getColor(R.color.colorCardBack));

        all.setChecked(false);
        today.setChecked(false);
        week.setChecked(false);
        month.setChecked(false);
        clinic.setChecked(false);
        video.setChecked(false);
        allType.setChecked(false);
        apptProcedure.setChecked(false);
        apptDressing.setChecked(false);
        apptRoutine.setChecked(false);
        apptFollow.setChecked(false);
        apptAll.setChecked(false);
        apptOther.setChecked(false);
        apptFirstVisit.setChecked(false);
    }

    void setFilter(String durationCase, String consultCase, String apptCase) {

        switch (durationCase) {
            case "All":
                all.setBackground(getResources().getDrawable(R.drawable.filter_selected_outline));
                all.setTextColor(Color.WHITE);
                all.setChecked(true);
                break;

            case "Today":
                today.setBackground(getResources().getDrawable(R.drawable.filter_selected_outline));
                today.setTextColor(Color.WHITE);
                today.setChecked(true);
                break;

            case "This Week":
                week.setBackground(getResources().getDrawable(R.drawable.filter_selected_outline));
                week.setTextColor(Color.WHITE);
                week.setChecked(true);
                break;

            case "This Month":
                month.setBackground(getResources().getDrawable(R.drawable.filter_selected_outline));
                month.setTextColor(Color.WHITE);
                month.setChecked(true);
                break;

        }

        switch (consultCase) {
            case "Video":
                video.setBackground(getResources().getDrawable(R.drawable.filter_selected_outline));
                video.setTextColor(Color.WHITE);
                video.setChecked(true);
                break;

            case "Clinic":
                clinic.setBackground(getResources().getDrawable(R.drawable.filter_selected_outline));
                clinic.setTextColor(Color.WHITE);
                clinic.setChecked(true);
                break;

            case "All":
                allType.setBackground(getResources().getDrawable(R.drawable.filter_selected_outline));
                allType.setTextColor(Color.WHITE);
                allType.setChecked(true);
        }

        switch (apptCase) {
            case "1": //Routine
                apptRoutine.setBackground(getResources().getDrawable(R.drawable.filter_selected_outline));
                apptRoutine.setTextColor(Color.WHITE);
                apptRoutine.setChecked(true);
                break;

            case "2": //Follow UP
                apptFollow.setBackground(getResources().getDrawable(R.drawable.filter_selected_outline));
                apptFollow.setTextColor(Color.WHITE);
                apptFollow.setChecked(true);
                break;

            case "3": //Procedure
                apptProcedure.setBackground(getResources().getDrawable(R.drawable.filter_selected_outline));
                apptProcedure.setTextColor(Color.WHITE);
                apptProcedure.setChecked(true);
                break;

            case "4": //Dressing/Plaster
                apptDressing.setBackground(getResources().getDrawable(R.drawable.filter_selected_outline));
                apptDressing.setTextColor(Color.WHITE);
                apptDressing.setChecked(true);
                break;

            case "5": //Other
                apptOther.setBackground(getResources().getDrawable(R.drawable.filter_selected_outline));
                apptOther.setTextColor(Color.WHITE);
                apptOther.setChecked(true);
                break;

            case "6": //First Visit
                apptFirstVisit.setBackground(getResources().getDrawable(R.drawable.filter_selected_outline));
                apptFirstVisit.setTextColor(Color.WHITE);
                apptFirstVisit.setChecked(true);
                break;

            case "0":
                apptAll.setBackground(getResources().getDrawable(R.drawable.filter_selected_outline));
                apptAll.setTextColor(Color.WHITE);
                apptAll.setChecked(true);


        }
    }


}