package com.whitecoats.adapter;

import android.app.Activity;
import android.content.Intent;

import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import com.whitecoats.clinicplus.activities.EMRActivity;
import com.whitecoats.model.AssistantTabListModel;
import com.whitecoats.clinicplus.AppUtilities;
import com.whitecoats.clinicplus.AssistantClickListener;
import com.whitecoats.clinicplus.R;


public class AssistantTabAdapter extends RecyclerView.Adapter<AssistantTabViewHolder> {

    private List<AssistantTabListModel> msgList = null;
    private AppUtilities appUtilities;
    private Activity activity;
    private AssistantClickListener assistantClickListener;
    public static int assistantPatientProfileRecordFlag = 0;
    public static boolean isAssistantPatientProfileClicked = false;

    public AssistantTabAdapter(List<AssistantTabListModel> msgList, Activity activity, AssistantClickListener assistantClickListener) {
        this.msgList = msgList;
        appUtilities = new AppUtilities();
        this.activity = activity;
        this.assistantClickListener = assistantClickListener;
    }

    @Override
    public void onBindViewHolder(final AssistantTabViewHolder holder, int position) {
        final AssistantTabListModel msgDto = this.msgList.get(position);
//        // If the message is a received message.
//        if(msgDto.MSG_TYPE_RECEIVED.equals(msgDto.getMsgType()))
//        {
//            holder.leftMsgLayout.setVisibility(LinearLayout.VISIBLE);
//            holder.leftMsgTextView.setText(msgDto.getMsgContent());
//            holder.rightMsgLayout.setVisibility(LinearLayout.GONE);
//        }
//        else if(msgDto.MSG_TYPE_SENT.equals(msgDto.getMsgType()))
//        {
//            holder.rightMsgLayout.setVisibility(LinearLayout.VISIBLE);
//            holder.rightMsgTextView.setText(msgDto.getMsgContent());
//            holder.leftMsgLayout.setVisibility(LinearLayout.GONE);
//        }

        //if type is 1 then self msg
        if (msgDto.getMsgType() == 1) {
            holder.selfMsgLayout.setVisibility(View.VISIBLE);
            holder.appointmentLayout.setVisibility(View.GONE);
            holder.aiMsgLayout.setVisibility(View.GONE);
            holder.recordsPLayout.setVisibility(View.GONE);
            holder.cancelApptLayout.setVisibility(View.GONE);
            holder.lateInformLayout.setVisibility(View.GONE);
            holder.selfMsgTextView.setText(msgDto.getMsgContent());

        } else if (msgDto.getMsgType() == 2) {
            holder.aiMsgLayout.setVisibility(View.VISIBLE);
            holder.selfMsgLayout.setVisibility(View.GONE);
            holder.appointmentLayout.setVisibility(View.GONE);
            holder.recordsPLayout.setVisibility(View.GONE);
            holder.cancelApptLayout.setVisibility(View.GONE);
            holder.lateInformLayout.setVisibility(View.GONE);

            holder.aiMsgTextView.setText(msgDto.getMsgContent());
        } else if (msgDto.getMsgType() == 3) { //type 3 for appointment
            holder.selfMsgLayout.setVisibility(View.GONE);
            holder.appointmentLayout.setVisibility(View.VISIBLE);
            holder.aiMsgLayout.setVisibility(View.GONE);
            holder.recordsPLayout.setVisibility(View.GONE);
            holder.cancelApptLayout.setVisibility(View.GONE);
            holder.lateInformLayout.setVisibility(View.GONE);

            holder.apptPName.setText(msgDto.getPatientName());
            holder.apptId = msgDto.getApptId();
            holder.apptPatientId = msgDto.getPatientId();
            holder.apptType = msgDto.getApptType();
            if (holder.apptType == 1) { //1 = video, 2 = chat, 3 = clinic
                holder.apptIcon.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_video));
            } else if (holder.apptType == 3) {
                holder.apptIcon.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_hospital));
            }

            String date = appUtilities.changeDateFormat("yyyy-MM-dd", "dd MMM, yy", msgDto.getApptDate());
            holder.apptDate.setText(date);
            String time = appUtilities.changeDateFormat("hh:mm:ss", "HH:mm", msgDto.getApptTime());
            holder.apptTime.setText(time);
            holder.profileLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    isAssistantPatientProfileClicked = true;
                    Intent intent = new Intent(activity, EMRActivity.class);
                    intent.putExtra("ApptId", 0);
                    intent.putExtra("PatientId", msgDto.getPatientId());
                    intent.putExtra("ApptMode", 0);
                    intent.putExtra("ApptDate", "");
                    intent.putExtra("ApptTime", "00:00:00");
                    intent.putExtra("PatientName", msgDto.getPatientName());
                    activity.startActivity(intent);

//                    assistantPatientProfileRecordFlag = 1;
//                    Intent intent = new Intent(activity, PatientProfileActivity.class);
//                    intent.putExtra("patientName", msgDto.getPatientName());
//                    intent.putExtra("patientId", msgDto.getPatientId());
//                    intent.putExtra("patientPhoneNumber", msgDto.getPatientPhNo());
//                    activity.startActivity(intent);
                }
            });
            holder.recordsLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent intent = new Intent(activity, EMRActivity.class);
                    intent.putExtra("ApptId", 0);
                    intent.putExtra("PatientId", msgDto.getPatientId());
                    intent.putExtra("ApptMode", 0);
                    intent.putExtra("ApptDate", "");
                    intent.putExtra("ApptTime", "00:00:00");
                    intent.putExtra("PatientName", msgDto.getPatientName());
                    activity.startActivity(intent);

//                    assistantPatientProfileRecordFlag = 1;
//                    Intent intent = new Intent(activity, PatientRecordActivity.class);
//                    intent.putExtra("PatientName", msgDto.getPatientName());
//                    intent.putExtra("PatientId", msgDto.getPatientId());
//                    activity.startActivity(intent);
                }
            });
        } else if (msgDto.getMsgType() == 4) { //type 4 for records patient
            holder.recordsPLayout.setVisibility(View.VISIBLE);
            holder.selfMsgLayout.setVisibility(View.GONE);
            holder.appointmentLayout.setVisibility(View.GONE);
            holder.aiMsgLayout.setVisibility(View.GONE);
            holder.cancelApptLayout.setVisibility(View.GONE);
            holder.lateInformLayout.setVisibility(View.GONE);

            holder.recordPName.setText(msgDto.getPatientName());
            holder.recordPPhNo.setText(msgDto.getPatientPhNo());
            holder.apptPatientId = msgDto.getPatientId();

            holder.recordPProfile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

//                    assistantPatientProfileRecordFlag = 1;
                    isAssistantPatientProfileClicked = true;
                    Intent intent = new Intent(activity, EMRActivity.class);
                    intent.putExtra("ApptId", 0);
                    intent.putExtra("PatientId", msgDto.getPatientId());
                    intent.putExtra("ApptMode", 0);
                    intent.putExtra("ApptDate", "");
                    intent.putExtra("ApptTime", "00:00:00");
                    intent.putExtra("PatientName", msgDto.getPatientName());
                    activity.startActivity(intent);

//                    Intent intent = new Intent(activity, PatientProfileActivity.class);
//                    intent.putExtra("patientName", msgDto.getPatientName());
//                    intent.putExtra("patientId", msgDto.getPatientId());
//                    intent.putExtra("patientPhoneNumber", msgDto.getPatientPhNo());
//                    activity.startActivity(intent);
                }
            });

            holder.recordPRecord.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent intent = new Intent(activity, EMRActivity.class);
                    intent.putExtra("ApptId", 0);
                    intent.putExtra("PatientId", msgDto.getPatientId());
                    intent.putExtra("ApptMode", 0);
                    intent.putExtra("ApptDate", "");
                    intent.putExtra("ApptTime", "00:00:00");
                    intent.putExtra("PatientName", msgDto.getPatientName());
                    activity.startActivity(intent);

//                    assistantPatientProfileRecordFlag = 1;
//                    Intent intent = new Intent(activity, PatientRecordActivity.class);
//                    intent.putExtra("PatientName", msgDto.getPatientName());
//                    intent.putExtra("PatientId", msgDto.getPatientId());
//                    activity.startActivity(intent);

                }
            });

            holder.recordPPhNo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    Log.d("Phone Number", "******");
                    assistantClickListener.onItemClick(v, holder.recordPPhNo.getText().toString(), "CALL_PATIENT");
                }
            });

        } else if (msgDto.getMsgType() == 5) { //type 4 for cancel appt
            holder.cancelApptLayout.setVisibility(View.VISIBLE);
            holder.selfMsgLayout.setVisibility(View.GONE);
            holder.appointmentLayout.setVisibility(View.GONE);
            holder.aiMsgLayout.setVisibility(View.GONE);
            holder.lateInformLayout.setVisibility(View.GONE);

            holder.cancelApptText.setText(msgDto.getMsgContent());

            holder.yesCancelAppt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    assistantClickListener.onItemClick(view, msgDto.getApptIds().toString(), "CANCEL_APPT_YES");
                }
            });

            holder.noCancelAppt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    assistantClickListener.onItemClick(view, msgDto.getApptIds().toString(), "CANCEL_APPT_NO");
                }
            });
        } else if (msgDto.getMsgType() == 6) { //type 6 for late inform
            holder.lateInformLayout.setVisibility(View.VISIBLE);
            holder.cancelApptLayout.setVisibility(View.GONE);
            holder.selfMsgLayout.setVisibility(View.GONE);
            holder.appointmentLayout.setVisibility(View.GONE);
            holder.aiMsgLayout.setVisibility(View.GONE);

            holder.lateInformText.setText(msgDto.getMsgContent());

            holder.yesLateInform.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    assistantClickListener.onItemClick(view, "yes", "LATE_INFORM");
                }
            });

            holder.noLateInform.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    assistantClickListener.onItemClick(view, "no", "LATE_INFORM");
                }
            });
        }
    }

    @Override
    public AssistantTabViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.list_row_assistant_tab_item_view, parent, false);
        return new AssistantTabViewHolder(view);
    }

    @Override
    public int getItemCount() {
        if (msgList == null) {
            msgList = new ArrayList<AssistantTabListModel>();
        }
        return msgList.size();
    }

}