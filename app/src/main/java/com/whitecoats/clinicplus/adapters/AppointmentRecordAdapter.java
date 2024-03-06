package com.whitecoats.clinicplus.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.whitecoats.clinicplus.AppUtilities;
import com.whitecoats.clinicplus.R;
import com.whitecoats.clinicplus.activities.EMRAddNotesActivity;
import com.whitecoats.clinicplus.activities.EMRCaseHistoryMoreInfoActivity;
import com.whitecoats.clinicplus.fragments.RecordsFragment;
import com.whitecoats.clinicplus.models.EMRConsultCaseHistoryModel;

import java.util.List;

/**
 * Created by Mohammad suhail ahmed on 28-08-2020.
 */
public class AppointmentRecordAdapter extends RecyclerView.Adapter<AppointmentRecordAdapter.EMRAddingNotesViewHolder> {
    private List<EMRConsultCaseHistoryModel> caseDetailsList;
    private Context context;
    private RecordsFragment emrAddNotesActivity;

    public AppointmentRecordAdapter(Context context, List<EMRConsultCaseHistoryModel> caseDetailsList) {
        this.caseDetailsList = caseDetailsList;
        this.context = context;
         this.emrAddNotesActivity = emrAddNotesActivity;

    }

    @NonNull
    @Override
    public EMRAddingNotesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.appointment_record_item, parent, false);
        return new EMRAddingNotesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EMRAddingNotesViewHolder holder, int position) {
        EMRConsultCaseHistoryModel emrConsultCaseHistoryModel = caseDetailsList.get(position);
        holder.caseTitle.setText(emrConsultCaseHistoryModel.getCategoryName());
        holder.caseDate.setText(new AppUtilities().changeDateFormat("yyyy-MM-dd HH:mm:ss", "dd MMM, yyyy HH:mm", emrConsultCaseHistoryModel.getCreatedAt()));
//        holder.name_Value.setText(emrConsultCaseHistoryModel.getName());
//        holder.posted_on_Value.setText(new AppUtilities().changeDateFormat("yyyy-MM-dd HH:mm:ss", "dd MMM, yyyy",emrConsultCaseHistoryModel.getPostedOn()));
//        holder.status_Value.setText(emrConsultCaseHistoryModel.getStatus());
        holder.caseItemLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, EMRCaseHistoryMoreInfoActivity.class);
                intent.putExtra("CatId", String.valueOf(emrConsultCaseHistoryModel.getCategoryId()));
                intent.putExtra("RecordId", emrConsultCaseHistoryModel.getRecordId() + "");
                intent.putExtra("RecordDetail", emrConsultCaseHistoryModel.getCategoryRecordData());
                intent.putExtra("RecordField", emrConsultCaseHistoryModel.getFieldDictionary());
                intent.putExtra("CatName", emrConsultCaseHistoryModel.getCategoryName());
                intent.putExtra("mode", emrConsultCaseHistoryModel.getEncounterName());
                intent.putExtra("interactionDate", emrConsultCaseHistoryModel.getEncounterDateTime());
                intent.putExtra("addedOnDate", emrConsultCaseHistoryModel.getCreatedAt());
                intent.putExtra("multiRecordPosition", emrConsultCaseHistoryModel.getMultiRecordPosition());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return caseDetailsList.size();
    }

    public class EMRAddingNotesViewHolder extends RecyclerView.ViewHolder {
        private TextView caseDate, caseTitle, name_Value, posted_on_Value, status_Value;
        private LinearLayout caseItemLayout;

        public EMRAddingNotesViewHolder(@NonNull View itemView) {
            super(itemView);
            caseDate = itemView.findViewById(R.id.case_item_date);
            caseTitle = itemView.findViewById(R.id.case_record_title);
            name_Value = itemView.findViewById(R.id.name_Value);
            posted_on_Value = itemView.findViewById(R.id.posted_on_Value);
            status_Value = itemView.findViewById(R.id.status_Value);
            caseItemLayout = itemView.findViewById(R.id.case_record_item);
        }
    }
}
