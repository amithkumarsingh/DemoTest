package com.whitecoats.adapter;

import android.app.Activity;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import com.whitecoats.clinicplus.PatientRecordsSharedMoreInfoActivity;
import com.whitecoats.model.PatientRecordsModel;
import com.whitecoats.clinicplus.PatientRecordListener;
import com.whitecoats.clinicplus.R;

public class HomeSharedRecordsListAdapter extends RecyclerView.Adapter<HomeSharedRecordsListAdapter.MyViewHolder> {

    private Activity activity;
    private List<PatientRecordsModel> patientRecordsModels;
    private PatientRecordListener patientRecordListener;
    public static int shareRecordClickFlag = 0;

    public HomeSharedRecordsListAdapter(Activity activity, List<PatientRecordsModel> patientRecordsModels, PatientRecordListener patientRecordListener) {
        this.activity = activity;
        this.patientRecordsModels = patientRecordsModels;
        this.patientRecordListener = patientRecordListener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.list_row_home_shared_records, viewGroup, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder myViewHolder, int i) {

        final PatientRecordsModel recordsModel = patientRecordsModels.get(i);

        //getting patient name from getRecordsName Method
        myViewHolder.patientName.setText(recordsModel.getRecordName());
        myViewHolder.recordSharedCategoryName.setText(recordsModel.getCatName());

        myViewHolder.priValue.setText(recordsModel.getPrimaryData());
//        Log.d("Primary Data", recordsModel.getPrimaryData());
        myViewHolder.priLabel.setText(recordsModel.getPrimaryKey());
        myViewHolder.secValue.setText(recordsModel.getSecData());
        myViewHolder.secLabel.setText(recordsModel.getSecKey());
        myViewHolder.terValue.setText(recordsModel.getTernaryData());
        myViewHolder.terLabel.setText(recordsModel.getTernaryKey());

        myViewHolder.priValue.setTextColor(activity.getResources().getColor(R.color.colorGreyText));
        if (recordsModel.getPrimaryKey().contains("Attachment")) {
            myViewHolder.priValue.setText(activity.getResources().getString(R.string.view));
            myViewHolder.priValue.setTextColor(activity.getResources().getColor(R.color.colorAccent));

            if (recordsModel.getFileUrl().equalsIgnoreCase("")) {
                myViewHolder.priValue.setText(activity.getResources().getString(R.string.na));
                myViewHolder.priValue.setTextColor(activity.getResources().getColor(R.color.colorBlack));
            }
        }
        myViewHolder.secValue.setTextColor(activity.getResources().getColor(R.color.colorGreyText));
        if (recordsModel.getSecKey() != null && recordsModel.getSecKey().contains("Attachment")) {
            myViewHolder.secValue.setText(activity.getResources().getString(R.string.view));
            myViewHolder.secValue.setTextColor(activity.getResources().getColor(R.color.colorAccent));

            if (recordsModel.getFileUrl().equalsIgnoreCase("")) {
                myViewHolder.secValue.setText(activity.getResources().getString(R.string.na));
                myViewHolder.secValue.setTextColor(activity.getResources().getColor(R.color.colorBlack));
            }
        }
        myViewHolder.terValue.setTextColor(activity.getResources().getColor(R.color.colorGreyText));
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
                    patientRecordListener.onItemClick(view, recordsModel.getFileUrl(), "FILE_URL","");
                }
            }
        });

        myViewHolder.secValue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (myViewHolder.secValue.getText().toString().equalsIgnoreCase(activity.getResources().getString(R.string.view))) {
                    patientRecordListener.onItemClick(view, recordsModel.getFileUrl(), "FILE_URL","");
                }
            }
        });

        myViewHolder.terValue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (myViewHolder.terValue.getText().toString().equalsIgnoreCase(activity.getResources().getString(R.string.view))) {
                    patientRecordListener.onItemClick(view, recordsModel.getFileUrl(), "FILE_URL","");
                }
            }
        });

        myViewHolder.viewRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


            }
        });

        myViewHolder.sharedRecordCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                shareRecordClickFlag = 1;

                Intent intent = new Intent(activity, PatientRecordsSharedMoreInfoActivity.class);
                intent.putExtra("CatId", recordsModel.getCatId());
                intent.putExtra("RecordId", recordsModel.getRecordId() + "");
                intent.putExtra("RecordDetail", recordsModel.getCatRecordData());
                intent.putExtra("RecordField", recordsModel.getFieldDictionary());
                intent.putExtra("CatName", recordsModel.getCatName());
                activity.startActivity(intent);


                //   Toast.makeText(activity,"click",Toast.LENGTH_LONG).show();

            }
        });

    }

    @Override
    public int getItemCount() {
        return patientRecordsModels.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView patientName,recordSharedCategoryName, viewRecord, priLabel, secLabel, terLabel,
                priValue, secValue, terValue;

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
        }
    }
}
