package com.whitecoats.clinicplus.adapters;

import android.content.Context;
import android.graphics.PorterDuff;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.whitecoats.clinicplus.AppUtilities;
import com.whitecoats.clinicplus.R;
import com.whitecoats.clinicplus.models.RescheduleApptTimelineModel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class RescheduleApptTimeLineAdapter extends RecyclerView.Adapter {
    private final Context context;
    private final AppUtilities appUtils;
    private  ArrayList<RescheduleApptTimelineModel> rescheduleTimeLineList=new ArrayList<>();

    public RescheduleApptTimeLineAdapter(Context context, ArrayList<RescheduleApptTimelineModel> mrescheduleTimeLineList) {
        this.rescheduleTimeLineList=mrescheduleTimeLineList;
        this.context=context;
        appUtils=new AppUtilities();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.reschedule_appt_timeline_child, parent, false);
        RecyclerItemViewHolder holder = new RecyclerItemViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        final RecyclerItemViewHolder itemViewHolder = (RecyclerItemViewHolder) holder;
        itemViewHolder.appt_time.setText(convertDateToMonthFormate(rescheduleTimeLineList.get(position).getAppt_time()));
        int lastitem = rescheduleTimeLineList.size();
        if(position==0){
            itemViewHolder.sepratorLayout.setVisibility(View.GONE);
            itemViewHolder.appointmentStatus.setText("Previous Appointment");
            itemViewHolder.book_appointment_by.setText("Booked By: "+rescheduleTimeLineList.get(position).getBooked_by());
        }else{
            itemViewHolder.sepratorLayout.setVisibility(View.VISIBLE);
            itemViewHolder.appointmentStatus.setText("Reschedule Appointment");
            itemViewHolder.book_appointment_by.setText("Rescheduled By: "+rescheduleTimeLineList.get(position).getBooked_by());
        }
        if(position==lastitem-1){
            itemViewHolder.leftCircleImageSecond.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_circle));
            itemViewHolder.leftCircleImageSecond.setColorFilter(context.getResources().getColor(R.color.colorGreen3), PorterDuff.Mode.SRC_IN);
        }else {
            itemViewHolder.leftCircleImageSecond.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_circle));
            itemViewHolder.leftCircleImageSecond.setColorFilter(context.getResources().getColor(R.color.colorInfo), PorterDuff.Mode.SRC_IN);
        }

    }

    @Override
    public int getItemCount() {
        return rescheduleTimeLineList.size();
    }


    public class RecyclerItemViewHolder extends RecyclerView.ViewHolder {


        private final View sepratorLayout;
        private  ImageView leftCircleImageSecond;
        private  TextView appointmentStatus;
        private  TextView appt_time;
        private  TextView book_appointment_by;

        public RecyclerItemViewHolder(final View parent) {
            super(parent);
            appt_time=(TextView)parent.findViewById(R.id.appt_time);
            book_appointment_by=(TextView)parent.findViewById(R.id.book_appointment_by);
            appointmentStatus=(TextView)parent.findViewById(R.id.appointmentStatus);
            leftCircleImageSecond=(ImageView)parent.findViewById(R.id.leftCircleImageSecond);
            sepratorLayout=(View)parent.findViewById(R.id.sepratorLayout);

        }
    }

    private String convertDateToMonthFormate(String scheduleTime) {
        String finalDateTimeString = "";
        try {
            SimpleDateFormat month_date = new SimpleDateFormat("MMM yyyy", Locale.ENGLISH);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String date_time = scheduleTime;
            String[] splitDateTime = date_time.split(" ");
            String dateString = splitDateTime[0];
            String[] dateSplitedString = dateString.split("-");
            String time = splitDateTime[1];
            //String[] timeSplitedString = time.split(":");
            Date date = sdf.parse(dateString);
            String month_name = month_date.format(date);


            /*SimpleDateFormat _24HourSDF = new SimpleDateFormat("HH:mm");
            SimpleDateFormat _12HourSDF = new SimpleDateFormat("hh:mm a");
            Date _24HourDt = _24HourSDF.parse(time);
            String finalTime = _12HourSDF.format(_24HourDt);
            String[] finalTimeSpilted = finalTime.split(" ");*/

            finalDateTimeString = dateSplitedString[2] + month_name + " @" + appUtils.formatTimeBasedOnPreference(context,"HH:mm:ss",time);

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return finalDateTimeString;
    }


}
