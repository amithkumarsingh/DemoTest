package com.whitecoats.adapter;

import android.app.Activity;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import com.whitecoats.clinicplus.PatientRecordsSharedMoreInfoActivity;
import com.whitecoats.clinicplus.patientsharedrecords.PatientSharedRecordsActivity;
import com.whitecoats.model.PatientRecordsModel;
import com.whitecoats.clinicplus.PatientRecordListener;
import com.whitecoats.clinicplus.PatientRecordsMoreInfoActivity;
import com.whitecoats.clinicplus.R;

public class PatientRecordViewAdapter extends RecyclerView.Adapter<PatientRecordViewAdapter.MyViewHolder> {

    private List<PatientRecordsModel> patientRecordsModels;
    private Activity activity;
    private PatientRecordListener patientRecordListener;
    public static int patientRecordClickFlag = 0;


    public PatientRecordViewAdapter(List<PatientRecordsModel> patientRecordsModels, Activity activity, PatientRecordListener patientRecordListener) {
        this.patientRecordsModels = patientRecordsModels;
        this.activity = activity;
        this.patientRecordListener = patientRecordListener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_row_record_view_details, viewGroup, false);
        return new PatientRecordViewAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder myViewHolder, int i) {

        try {
            final PatientRecordsModel patientRecordsModel = patientRecordsModels.get(i);
            myViewHolder.primaryKey.setText(patientRecordsModel.getPrimaryKey() + ": ");
            if (patientRecordsModel.getPrimaryData() != null) {
                myViewHolder.primaryText.setText(patientRecordsModel.getPrimaryData());
            } else {
                myViewHolder.primaryText.setText("-");

            }
            myViewHolder.secKey.setText(patientRecordsModel.getSecKey() + ": ");
            if (patientRecordsModel.getSecData() != null) {
                myViewHolder.secText.setText(patientRecordsModel.getSecData());
            } else {
                myViewHolder.secText.setText("-");

            }

            if (patientRecordsModel.getTernaryKey() != null) {
                myViewHolder.ternaryKey.setText(patientRecordsModel.getTernaryKey() + ": ");
            }
//        if (patientRecordsModel.getTernaryData() != null) {
            myViewHolder.ternaryText.setText(patientRecordsModel.getTernaryData());
//        } else {
//            myViewHolder.ternaryText.setText("-");
//
//        }

            //file url
            myViewHolder.primaryText.setTextColor(activity.getResources().getColor(R.color.colorGreyText));
            if (patientRecordsModel.getPrimaryKey().contains("Attachment") &&
                    patientRecordsModel.getPrimaryData().equalsIgnoreCase("yes")) {
                myViewHolder.primaryText.setText(activity.getResources().getString(R.string.view));
                myViewHolder.primaryText.setTextColor(activity.getResources().getColor(R.color.colorAccent));
            }
            myViewHolder.secText.setTextColor(activity.getResources().getColor(R.color.colorGreyText));
            if (patientRecordsModel.getSecKey() != null && patientRecordsModel.getSecKey().contains("Attachment") &&
                    patientRecordsModel.getSecData().equalsIgnoreCase("yes")) {
                myViewHolder.secText.setText(activity.getResources().getString(R.string.view));
                myViewHolder.secText.setTextColor(activity.getResources().getColor(R.color.colorAccent));
            }
            myViewHolder.ternaryText.setTextColor(activity.getResources().getColor(R.color.colorGreyText));
            if (patientRecordsModel.getTernaryKey() != null && patientRecordsModel.getTernaryKey().contains("Attachment") &&
                    patientRecordsModel.getTernaryData().equalsIgnoreCase("yes")) {
                myViewHolder.ternaryText.setText(activity.getResources().getString(R.string.view));
                myViewHolder.ternaryText.setTextColor(activity.getResources().getColor(R.color.colorAccent));
            }

            myViewHolder.primaryText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (myViewHolder.primaryText.getText().toString().equalsIgnoreCase(activity.getResources().getString(R.string.view))) {
                        patientRecordListener.onItemClick(view, patientRecordsModel.getFileUrl(), "FILE_URL", "");
                    }
                }
            });

            myViewHolder.secText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (myViewHolder.secText.getText().toString().equalsIgnoreCase(activity.getResources().getString(R.string.view))) {
                        patientRecordListener.onItemClick(view, patientRecordsModel.getFileUrl(), "FILE_URL", "");
                    }
                }
            });

            myViewHolder.ternaryText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (myViewHolder.ternaryText.getText().toString().equalsIgnoreCase(activity.getResources().getString(R.string.view))) {
                        patientRecordListener.onItemClick(view, patientRecordsModel.getFileUrl(), "FILE_URL", "");
                    }
                }
            });

            myViewHolder.recordsLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    //  patientRecordClickFlag = 1;


                    if (PatientSharedRecordsActivity.shareRecordPatientListFlag == 1) {
                        Intent intent = new Intent(activity, PatientRecordsSharedMoreInfoActivity.class);
                        intent.putExtra("CatId", patientRecordsModel.getCatId());
                        intent.putExtra("RecordId", patientRecordsModel.getRecordId() + "");
                        intent.putExtra("RecordDetail", patientRecordsModel.getCatRecordData());
                        intent.putExtra("RecordField", patientRecordsModel.getFieldDictionary());
                        intent.putExtra("CatName", patientRecordsModel.getCatName());
                        activity.startActivity(intent);
                    } else {
                        Intent intent = new Intent(activity, PatientRecordsMoreInfoActivity.class);
                        intent.putExtra("CatId", patientRecordsModel.getCatId());
                        intent.putExtra("RecordId", patientRecordsModel.getRecordId() + "");
                        intent.putExtra("RecordDetail", patientRecordsModel.getCatRecordData());
                        intent.putExtra("RecordField", patientRecordsModel.getFieldDictionary());
                        intent.putExtra("CatName", patientRecordsModel.getCatName());
                        activity.startActivity(intent);
                    }
                }
            });
        } catch (Exception e) {

        }

    }

    @Override
    public int getItemCount() {
        return patientRecordsModels.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView primaryText, secText, ternaryText, primaryKey, secKey, ternaryKey;
        private RelativeLayout recordsLayout;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            primaryText = itemView.findViewById(R.id.recordViewPrimaryTv);
            secText = itemView.findViewById(R.id.recordViewSecondaryTv);
            ternaryText = itemView.findViewById(R.id.recordViewTernaryTv);
            primaryKey = itemView.findViewById(R.id.recordViewPrimaryKeyTv);
            secKey = itemView.findViewById(R.id.recordViewSecondaryKeyTv);
            ternaryKey = itemView.findViewById(R.id.recordViewTernaryKeyTv);
            recordsLayout = itemView.findViewById(R.id.recordViewLayout);
        }
    }
}
