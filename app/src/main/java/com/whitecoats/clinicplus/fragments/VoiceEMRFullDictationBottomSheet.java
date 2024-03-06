package com.whitecoats.clinicplus.fragments;

import android.app.Dialog;
import android.content.Context;
import android.graphics.PorterDuff;
import android.view.View;
import android.widget.ImageButton;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.whitecoats.clinicplus.R;
import com.whitecoats.clinicplus.activities.VoiceEMRActivity;
import com.whitecoats.clinicplus.adapters.VoiceEMRFullDictationListAdapter;
import com.whitecoats.clinicplus.models.VoiceEMRFullDictationModel;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class VoiceEMRFullDictationBottomSheet extends BottomSheetDialogFragment {

    private RecyclerView fullDictationRV;
    public List<VoiceEMRFullDictationModel> voiceEMRFullDictationModels;
    private VoiceEMRFullDictationListAdapter voiceEMRFullDictationListAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private Context context;
    private VoiceEMRActivity voiceEMRActivity;

    public VoiceEMRFullDictationBottomSheet(Context context,List<VoiceEMRFullDictationModel> voiceEMRFullDictationModels) {
        this.context = context;
        this.voiceEMRFullDictationModels= voiceEMRFullDictationModels;
    }

    public void setupConfig(VoiceEMRActivity voiceEMRActivity,List<VoiceEMRFullDictationModel> voiceEMRFullDictationModels) {
        this.voiceEMRActivity = voiceEMRActivity;
        this.voiceEMRFullDictationModels= voiceEMRFullDictationModels;
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
        final View contentView = View.inflate(getContext(), R.layout.voice_emr_full_dictation, null);
        dialog.setContentView(contentView);

        ImageButton cancelIcon = contentView.findViewById(R.id.bottomSheetCancelIcon);
        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) ((View) contentView.getParent()).getLayoutParams();
        CoordinatorLayout.Behavior behavior = params.getBehavior();

        mLayoutManager = new LinearLayoutManager(context);
        fullDictationRV = contentView.findViewById(R.id.fullDictationRV);
//        voiceEMRFullDictationModels = new ArrayList<>();

        voiceEMRFullDictationListAdapter = new VoiceEMRFullDictationListAdapter(context,voiceEMRFullDictationModels);
        fullDictationRV.setLayoutManager(mLayoutManager);
        fullDictationRV.setItemAnimator(new DefaultItemAnimator());
        fullDictationRV.setAdapter(voiceEMRFullDictationListAdapter);

        //Set callback
        if (behavior != null && behavior instanceof BottomSheetBehavior) {
            ((BottomSheetBehavior) behavior).setBottomSheetCallback(mBottomSheetBehaviorCallback);
        }
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(true);
        cancelIcon.setColorFilter(getResources().getColor(R.color.colorWhite), PorterDuff.Mode.SRC_IN);
        cancelIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
    }
}
