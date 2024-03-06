package com.whitecoats.adapter;

import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.whitecoats.clinicplus.R;

public class AssistantTabViewHolder extends RecyclerView.ViewHolder {

    //self msg
    LinearLayout selfMsgLayout;
    TextView selfMsgTextView;

    //ai replies
    public LinearLayout aiMsgLayout;
    public TextView aiMsgTextView;

    //appointment data
    public LinearLayout appointmentLayout, profileLayout, recordsLayout;
    public TextView apptPName, apptDate, apptTime;
    public int apptType, apptId, apptPatientId;
    public ImageView apptIcon;

    //records Patient
    public LinearLayout recordsPLayout, recordPProfile, recordPRecord;
    public TextView recordPName, recordPPhNo;

    //cancel appt
    public LinearLayout cancelApptLayout, yesCancelAppt, noCancelAppt;
    public TextView cancelApptText;

    //late inform
    public LinearLayout lateInformLayout, yesLateInform, noLateInform;
    public TextView lateInformText;

    public AssistantTabViewHolder(View itemView) {
        super(itemView);

        if(itemView!=null) {
            selfMsgLayout = itemView.findViewById(R.id.assistantTabChatSelfMsgLayout);
            aiMsgLayout = itemView.findViewById(R.id.assistantTabAiMsgLayout);
            selfMsgTextView = itemView.findViewById(R.id.assistantTabChatSelfMsgTextView);
            aiMsgTextView = itemView.findViewById(R.id.assistantTabAiMsg);

            //appt data
            appointmentLayout = itemView.findViewById(R.id.assistTabAppointmentRow);
            apptPName = itemView.findViewById(R.id.assistTabAppointmentPName);
            apptDate = itemView.findViewById(R.id.assistTabAppointmentDate);
            apptTime = itemView.findViewById(R.id.assistTabAppointmentTime);
            apptIcon = itemView.findViewById(R.id.assistTabAppointmentType);
            profileLayout = itemView.findViewById(R.id.assistTabAppointmentProfile);
            recordsLayout = itemView.findViewById(R.id.assistTabAppointmentRecord);

            //record Patient layout
            recordsPLayout = itemView.findViewById(R.id.assistTabRecordPatient);
            recordPName = itemView.findViewById(R.id.assistTabPatientName);
            recordPPhNo = itemView.findViewById(R.id.assistTabPatientPhNo);
            recordPProfile = itemView.findViewById(R.id.assistTabPatientProfile);
            recordPRecord = itemView.findViewById(R.id.assistTabPatientRecord);

            //cancel Appt
            cancelApptLayout = itemView.findViewById(R.id.assistTabCancelApptLayout);
            cancelApptText = itemView.findViewById(R.id.assistTabCancelApptText);
            yesCancelAppt = itemView.findViewById(R.id.assistTabCancelApptYes);
            noCancelAppt = itemView.findViewById(R.id.assistTabCancelApptNo);

            //late inform
            lateInformLayout = itemView.findViewById(R.id.assistTabLateInformLayout);
            yesLateInform = itemView.findViewById(R.id.assistTabLateInformYes);
            noLateInform = itemView.findViewById(R.id.assistTabLateInformNo);
            lateInformText = itemView.findViewById(R.id.assistTabLateInformText);
        }
    }
}