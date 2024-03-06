package com.whitecoats.clinicplus.interfaces;

import android.view.View;

import com.whitecoats.clinicplus.models.TrainingScheduleModel;

public interface DashBoardTrainingListClickListener {
    public void onItemClick(View v, int position, String bookTraining, TrainingScheduleModel trainingModel);
}