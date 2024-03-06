package com.whitecoats.adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import com.whitecoats.model.HomeApptListModel;
import com.whitecoats.clinicplus.AppointmentClickListener;
import com.whitecoats.clinicplus.R;
import com.zoho.salesiqembed.ZohoSalesIQ;

public class HomeApptListAdapter extends RecyclerView.Adapter<HomeApptListAdapter.MyViewHolder> {

    private List<HomeApptListModel> homeApptListModelList;
    AppointmentClickListener apptClickLitner;
    private Context mContext;

    public HomeApptListAdapter(Context mContext, List<HomeApptListModel> homeApptListModelList, AppointmentClickListener apptClickLitner) {
        this.homeApptListModelList = homeApptListModelList;
        this.apptClickLitner = apptClickLitner;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.list_row_home_appt, viewGroup, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder myViewHolder, final int i) {

        //getting current position value
        final HomeApptListModel homeApptListModel = homeApptListModelList.get(i);

        //setting values to ui
        //myViewHolder.homeApptListClinicName.setText(homeApptListModel.getClinicName());
        // myViewHolder.serviceImage.setText(homeApptListModel.getClinicName());
        myViewHolder.appointmentCount.setText(homeApptListModel.getApptCount() + "");
        myViewHolder.appointmentDone.setText(homeApptListModel.getDoneCount() + " Done");
        myViewHolder.appointmentCancelled.setText(homeApptListModel.getCancelCount() + " Cancelled");
        myViewHolder.appointmentPending.setText(homeApptListModel.getPendingCount() + " Pending");

        if (homeApptListModel.getServiceId() == 1) {
            myViewHolder.homeApptListClinicName.setText(homeApptListModel.getClinicName());
            myViewHolder.serviceImage.setImageResource(R.drawable.ic_video);
            myViewHolder.cancelAppointment.setVisibility(View.VISIBLE);
            myViewHolder.iAmLate.setVisibility(View.GONE);
            myViewHolder.actionView.setVisibility(View.VISIBLE);

        } else if (homeApptListModel.getServiceId() == 2) {
            myViewHolder.homeApptListClinicName.setText(homeApptListModel.getClinicName());
            myViewHolder.serviceImage.setImageResource(R.drawable.ic_chat);
            myViewHolder.cancelAppointment.setVisibility(View.GONE);
            myViewHolder.iAmLate.setVisibility(View.VISIBLE);
            myViewHolder.iamLateText.setText("View all chats");
            myViewHolder.iamLateIcon.setVisibility(View.GONE);
            myViewHolder.actionView.setVisibility(View.GONE);

            myViewHolder.appointmentCount.setText(homeApptListModel.getApptCount() + "");
            myViewHolder.appointmentsText.setText("Active chats");
            myViewHolder.appointmentDone.setText(homeApptListModel.getPendingCount() + " Unread messages");
            myViewHolder.appointmentCancelled.setVisibility(View.GONE);

            myViewHolder.appointmentCancelled.setText("");
            myViewHolder.appointmentPending.setText("");
            myViewHolder.appointmentDone.setTextColor(mContext.getResources().getColor(R.color.colorGreen3));

        } else {
            myViewHolder.homeApptListClinicName.setText(homeApptListModel.getClinicName());
            myViewHolder.serviceImage.setImageResource(R.drawable.ic_hospital);
            myViewHolder.cancelAppointment.setVisibility(View.VISIBLE);
            myViewHolder.iAmLate.setVisibility(View.VISIBLE);
            myViewHolder.actionView.setVisibility(View.VISIBLE);
        }


        myViewHolder.cancelAppointment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ZohoSalesIQ.Tracking.setCustomAction("HomeTab - Appt Summary - Cancel Appt");
                if (homeApptListModel.getPendingCount() == 0) {
                    Toast.makeText(mContext, "You do not have any appointments to cancel", Toast.LENGTH_LONG).show();
                } else {
                    apptClickLitner.onItemClick(v, i, homeApptListModel.getAppointmentPendingId(), "Cancel", "");

                }
            }
        });
        myViewHolder.iAmLate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ZohoSalesIQ.Tracking.setCustomAction("HomeTab - Appt Summary - Delay Intimation");
                if (myViewHolder.iamLateText.getText().toString().equalsIgnoreCase("View all chats")) {
                   // Toast.makeText(mContext, "click", Toast.LENGTH_LONG).show();
                    apptClickLitner.onItemClick(v, i, homeApptListModel.getAppointmentPendingId(), "", "View all chats");


                } else {

                    if (homeApptListModel.getPendingCount() == 0) {
                        Toast.makeText(mContext, "You do not have any appointment to send delay intimation", Toast.LENGTH_LONG).show();
                    } else {
                        apptClickLitner.onItemClick(v, i, homeApptListModel.getAppointmentPendingId(), "", "Late");
                    }
                }

            }
        });


    }

    @Override
    public int getItemCount() {
        return homeApptListModelList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView homeApptListClinicName;
        public ImageView serviceImage, iamLateIcon;
        public TextView appointmentCount, appointmentDone, appointmentCancelled, appointmentPending, appointmentsText;
        public LinearLayout cancelAppointment, iAmLate;
        public View actionView;
        public TextView iamLateText;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            homeApptListClinicName = itemView.findViewById(R.id.homeApptListClinicName);
            serviceImage = itemView.findViewById(R.id.serviceImage);
            appointmentCount = itemView.findViewById(R.id.appointmentCount);
            appointmentDone = itemView.findViewById(R.id.appointmentDone);
            appointmentCancelled = itemView.findViewById(R.id.appointmentCancelled);
            appointmentPending = itemView.findViewById(R.id.appointmentPending);
            cancelAppointment = itemView.findViewById(R.id.cancelAppointment);
            iAmLate = itemView.findViewById(R.id.iAmLate);
            actionView = itemView.findViewById(R.id.actionView);
            appointmentsText = itemView.findViewById(R.id.appointmentsText);
            iamLateIcon = itemView.findViewById(R.id.iamLateIcon);
            iamLateText = itemView.findViewById(R.id.iamLateText);

        }
    }
}
