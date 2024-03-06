package com.whitecoats.clinicplus.trainingschedule;

import android.view.View;

public interface UpcomingTrainingClickListener {
    public void onItemClick(View v, String loadMore, int position, upcomingTrainingScheduleListModel upcomingTrainingScheduleListModel);

}