package com.whitecoats.clinicplus.adapters;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.whitecoats.clinicplus.AppUtilities;
import com.whitecoats.clinicplus.PatientRecordListener;
import com.whitecoats.clinicplus.R;
import com.whitecoats.clinicplus.activities.EMRSharedRecordDetails;
import com.whitecoats.clinicplus.models.DashBoardPatientRecordsModel;

import java.util.List;

public class AppointmentRecordDetailsSharedRecordsListAdapter extends RecyclerView.Adapter<AppointmentRecordDetailsSharedRecordsListAdapter.MyViewHolder> {

    private Activity activity;
    private List<DashBoardPatientRecordsModel> dashBoardPatientRecordsModels;
    private PatientRecordListener patientRecordListener;
    public static int shareRecordClickFlag = 0;
    private AppUtilities appUtilities;

    public AppointmentRecordDetailsSharedRecordsListAdapter(Activity activity, List<DashBoardPatientRecordsModel> dashBoardPatientRecordsModels, PatientRecordListener patientRecordListener) {
        this.activity = activity;
        this.dashBoardPatientRecordsModels = dashBoardPatientRecordsModels;
        this.patientRecordListener = patientRecordListener;
        this.appUtilities = new AppUtilities();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.list_row_appointment_shared_records, viewGroup, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder myViewHolder, int i) {

        final DashBoardPatientRecordsModel recordsModel = dashBoardPatientRecordsModels.get(i);

        //getting patient name from getRecordsName Method
        String date = appUtilities.changeDateFormat("yyyy-MM-dd hh:mm:ss", "dd MMM, yy hh:mm", recordsModel.getCreated_At());
        myViewHolder.createdDateText.setText(date);
        myViewHolder.patientName.setText(recordsModel.getRecordName());
        myViewHolder.recordSharedCategoryName.setText(recordsModel.getCatName());
        myViewHolder.priValue.setText(recordsModel.getPrimaryData());
        myViewHolder.priLabel.setText(recordsModel.getPrimaryKey());
        myViewHolder.secValue.setText(recordsModel.getSecData());
        myViewHolder.secLabel.setText(recordsModel.getSecKey());
        myViewHolder.terValue.setText(recordsModel.getTernaryData());
        myViewHolder.terLabel.setText(recordsModel.getTernaryKey());

        myViewHolder.priValue.setTextColor(activity.getResources().getColor(R.color.colorBlack));
        if (recordsModel.getPrimaryKey().contains("Attachment")) {
            myViewHolder.priValue.setText(activity.getResources().getString(R.string.view));
            myViewHolder.priValue.setTextColor(activity.getResources().getColor(R.color.colorAccent));

            if (recordsModel.getFileUrl().equalsIgnoreCase("")) {
                myViewHolder.priValue.setText(activity.getResources().getString(R.string.na));
                myViewHolder.priValue.setTextColor(activity.getResources().getColor(R.color.colorBlack));
            }
        }
        myViewHolder.secValue.setTextColor(activity.getResources().getColor(R.color.colorBlack));
        if (recordsModel.getSecKey() != null && recordsModel.getSecKey().contains("Attachment")) {
            myViewHolder.secValue.setText(activity.getResources().getString(R.string.view));
            myViewHolder.secValue.setTextColor(activity.getResources().getColor(R.color.colorAccent));

            if (recordsModel.getFileUrl().equalsIgnoreCase("")) {
                myViewHolder.secValue.setText(activity.getResources().getString(R.string.na));
                myViewHolder.secValue.setTextColor(activity.getResources().getColor(R.color.colorBlack));
            }
        }
        myViewHolder.terValue.setTextColor(activity.getResources().getColor(R.color.colorBlack));
        if (recordsModel.getTernaryKey() != null && recordsModel.getTernaryKey().contains("Attachment")) {
            myViewHolder.terValue.setText(activity.getResources().getString(R.string.view));
            myViewHolder.terValue.setTextColor(activity.getResources().getColor(R.color.colorAccent));

            if (recordsModel.getFileUrl().equalsIgnoreCase("")) {
                myViewHolder.terValue.setText(activity.getResources().getString(R.string.na));
                myViewHolder.terValue.setTextColor(activity.getResources().getColor(R.color.colorBlack));
            }
        }

        myViewHolder.priValue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (myViewHolder.priValue.getText().toString().equalsIgnoreCase(activity.getResources().getString(R.string.view))) {
                    patientRecordListener.onItemClick(view, recordsModel.getFileUrl(), "FILE_URL", "");
                }
            }
        });

        myViewHolder.secValue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (myViewHolder.secValue.getText().toString().equalsIgnoreCase(activity.getResources().getString(R.string.view))) {
                    patientRecordListener.onItemClick(view, recordsModel.getFileUrl(), "FILE_URL", "");
                }
            }
        });

        myViewHolder.terValue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (myViewHolder.terValue.getText().toString().equalsIgnoreCase(activity.getResources().getString(R.string.view))) {
                    patientRecordListener.onItemClick(view, recordsModel.getFileUrl(), "FILE_URL", "");
                }
            }
        });

        myViewHolder.viewRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                shareRecordClickFlag = 1;

//                String shareOnDate = appUtilities.changeDateFormat("yyyy-MM-dd hh:mm:ss", "dd MMM, yy", recordsModel.getSharedOnDateTime());

                Intent intent = new Intent(activity, EMRSharedRecordDetails.class);
                intent.putExtra("categoryID", recordsModel.getCategoryId());
                intent.putExtra("RecordId", recordsModel.getRecordId());
                intent.putExtra("RecordDetail", recordsModel.getRecordDetailsObject().toString());
                intent.putExtra("fieldDictionary", recordsModel.getFieldDictionaryObject().toString());
                intent.putExtra("sharedOn",recordsModel.getCreated_At());
                intent.putExtra("CatName", recordsModel.getCatName());
                activity.startActivity(intent);

//                Intent intent = new Intent(activity, PatientRecordsSharedMoreInfoActivity.class);
//                intent.putExtra("CatId", recordsModel.getCatId());
//                intent.putExtra("RecordId", recordsModel.getRecordId() + "");
//                intent.putExtra("RecordDetail", recordsModel.getCatRecordData());
//                intent.putExtra("RecordField", recordsModel.getFieldDictionary());
//                intent.putExtra("CatName", recordsModel.getCatName());
//                activity.startActivity(intent);

            }
        });

        myViewHolder.sharedRecordCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                shareRecordClickFlag = 1;

                Intent intent = new Intent(activity, EMRSharedRecordDetails.class);
                intent.putExtra("categoryID", recordsModel.getCategoryId());
                intent.putExtra("RecordId", recordsModel.getRecordId());
                intent.putExtra("RecordDetail", recordsModel.getRecordDetailsObject().toString());
                intent.putExtra("fieldDictionary", recordsModel.getFieldDictionaryObject().toString());
                intent.putExtra("sharedOn",recordsModel.getCreated_At());
                intent.putExtra("CatName", recordsModel.getCatName());
                activity.startActivity(intent);

//                Intent intent = new Intent(activity, PatientRecordsSharedMoreInfoActivity.class);
//                intent.putExtra("CatId", recordsModel.getCatId());
//                intent.putExtra("RecordId", recordsModel.getRecordId() + "");
//                intent.putExtra("RecordDetail", recordsModel.getCatRecordData());
//                intent.putExtra("RecordField", recordsModel.getFieldDictionary());
//                intent.putExtra("CatName", recordsModel.getCatName());
//                activity.startActivity(intent);

            }
        });

    }

    @Override
    public int getItemCount() {
        return dashBoardPatientRecordsModels.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView patientName, recordSharedCategoryName, viewRecord, priLabel, secLabel, terLabel,
                priValue, secValue, terValue, createdDateText;

        private CardView sharedRecordCardView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            patientName = itemView.findViewById(R.id.homeSharedRecordsPatientName);
            recordSharedCategoryName = itemView.findViewById(R.id.recordSharedCategoryName);

            viewRecord = itemView.findViewById(R.id.homeSharedRecordsView);
            priLabel = itemView.findViewById(R.id.homeSharedRecordsPriLabel);
            secLabel = itemView.findViewById(R.id.homeSharedRecordsSecLabel);
            terLabel = itemView.findViewById(R.id.homeSharedRecordsTerLabel);
            priValue = itemView.findViewById(R.id.homeSharedRecordsPriValue);
            secValue = itemView.findViewById(R.id.homeSharedRecordsSecValue);
            terValue = itemView.findViewById(R.id.homeSharedRecordsTerValue);
            sharedRecordCardView = itemView.findViewById(R.id.sharedRecordCardView);
            createdDateText = itemView.findViewById(R.id.createdDateText);
        }
    }
}
