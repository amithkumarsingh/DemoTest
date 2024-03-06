package com.whitecoats.clinicplus.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.whitecoats.clinicplus.R;
import com.whitecoats.clinicplus.interfaces.VoiceEmrRecordClickListener;
import com.whitecoats.clinicplus.models.VoiceEMRCategoryRecordModel;
import com.whitecoats.clinicplus.models.VoiceEMRModel;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class VoiceEMRDiagnosisListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<VoiceEMRModel> voiceEMRModelsDiagnosisCategoryList;
    private VoiceEmrRecordClickListener voiceEmrRecordClickListener;
    private Context context;

    public VoiceEMRDiagnosisListAdapter(Context context, List<VoiceEMRModel> voiceEMRModelsDiagnosisCategoryList,VoiceEmrRecordClickListener voiceEmrRecordClickListener) {

        this.context = context;
        this.voiceEMRModelsDiagnosisCategoryList = voiceEMRModelsDiagnosisCategoryList;
        this.voiceEmrRecordClickListener = voiceEmrRecordClickListener;
    }

    @NonNull
    @NotNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_row_voice_emr_category, parent, false);
        context = parent.getContext();
        return new MyViewHolder(view, context);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull RecyclerView.ViewHolder holder, int position) {
        MyViewHolder itemViewHolder = (MyViewHolder) holder;
        if (voiceEMRModelsDiagnosisCategoryList.get(position).getCategoryNameDiagnosisExistType() == 1) {
            itemViewHolder.categoryNameLinearLayoutCompact.setVisibility(View.VISIBLE);
            itemViewHolder.categoryName.setText(voiceEMRModelsDiagnosisCategoryList.get(position).getCategoryName());
            itemViewHolder.subCategoryName.setText(position + 1 + ". "+voiceEMRModelsDiagnosisCategoryList.get(position).getDiagnosisDiagnosis());
        } else {
            itemViewHolder.categoryNameLinearLayoutCompact.setVisibility(View.GONE);
            itemViewHolder.subCategoryName.setText(position + 1 + ". "+voiceEMRModelsDiagnosisCategoryList.get(position).getDiagnosisDiagnosis());
        }
        itemViewHolder.allRecordDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               // voiceEmrRecordClickListener.onItemClick(view, position, "diagnosisAll","Diagnosis", dictiatationModel.getVoiceEMRModel());
            }
        });
        itemViewHolder.singleRecordDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //voiceEmrRecordClickListener.onItemClick(view, position, "diagnosisSingle","Diagnosis", dictiatationModel.getVoiceEMRModel());
            }
        });
        itemViewHolder.infoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               // voiceEmrRecordClickListener.onItemClick(view, position, "diagnosisInfo","Diagnosis", dictiatationModel.getVoiceEMRModel());
            }
        });
        itemViewHolder.addRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               // voiceEmrRecordClickListener.onItemClick(view, position, "diagnosisAddRecord","Diagnosis", dictiatationModel.getVoiceEMRModel());
            }
        });

        itemViewHolder.editRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              //  voiceEmrRecordClickListener.onItemClick(view, position, "diagnosisEditRecord", "Diagnosis", dictiatationModel.getVoiceEMRModel());
            }
        });


    }

    @Override
    public int getItemCount() {
        return voiceEMRModelsDiagnosisCategoryList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public List<VoiceEMRCategoryRecordModel> voiceEMRCategoryRecordModels;
        public TextView categoryName, subCategoryName;
        public LinearLayout categoryNameLinearLayoutCompact;
        public ImageButton allRecordDelete, singleRecordDelete,infoButton,addRecord,editRecord;

        public MyViewHolder(@NonNull @NotNull View itemView, Context context) {
            super(itemView);
            categoryName = itemView.findViewById(R.id.categoryName);
            subCategoryName = itemView.findViewById(R.id.subCategoryName);
            categoryNameLinearLayoutCompact = itemView.findViewById(R.id.categoryNameLinearLayoutCompact);
            allRecordDelete = itemView.findViewById(R.id.allRecordDelete);
            singleRecordDelete = itemView.findViewById(R.id.singleRecordDelete);
            infoButton = itemView.findViewById(R.id.infoButton);
            addRecord=itemView.findViewById(R.id.addRecord);
            editRecord = itemView.findViewById(R.id.editRecord);
        }
    }
}
