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

public class VoiceEMRTreatmentPlanListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<VoiceEMRModel> voiceEMRModelsTreatmentPlanCategoryList;
    private VoiceEmrRecordClickListener voiceEmrRecordClickListener;
    private Context context;

    public VoiceEMRTreatmentPlanListAdapter(Context context, List<VoiceEMRModel> voiceEMRModelsTreatmentPlanCategoryList, VoiceEmrRecordClickListener voiceEmrRecordClickListener) {

        this.context = context;
        this.voiceEMRModelsTreatmentPlanCategoryList = voiceEMRModelsTreatmentPlanCategoryList;
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
        if (voiceEMRModelsTreatmentPlanCategoryList.get(position).getCategoryNameTreatmentPlanExistType() == 1) {
            itemViewHolder.categoryNameLinearLayoutCompact.setVisibility(View.VISIBLE);
            itemViewHolder.categoryName.setText(voiceEMRModelsTreatmentPlanCategoryList.get(position).getCategoryName());
            itemViewHolder.subCategoryName.setText(position + 1 + ". "+ voiceEMRModelsTreatmentPlanCategoryList.get(position).getTreatmentCategoryName());
        } else {
            itemViewHolder.categoryNameLinearLayoutCompact.setVisibility(View.GONE);
            itemViewHolder.subCategoryName.setText(position + 1 + ". "+ voiceEMRModelsTreatmentPlanCategoryList.get(position).getTreatmentCategoryName());
        }

        itemViewHolder.allRecordDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //voiceEmrRecordClickListener.onItemClick(view, position, "TreatmentAll","Treatment Plan", dictiatationModel.getVoiceEMRModel());
            }
        });
        itemViewHolder.singleRecordDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               // voiceEmrRecordClickListener.onItemClick(view, position, "TreatmentSingle","Treatment Plan", dictiatationModel.getVoiceEMRModel());
            }
        });
        itemViewHolder.symptomsInfoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //voiceEmrRecordClickListener.onItemClick(view, position, "TreatmentInfo","Treatment Plan", dictiatationModel.getVoiceEMRModel());
            }
        });

        itemViewHolder.addRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //voiceEmrRecordClickListener.onItemClick(view, position, "TreatmentAddRecord","Treatment Plan", dictiatationModel.getVoiceEMRModel());
            }
        });


        itemViewHolder.editRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
             //   voiceEmrRecordClickListener.onItemClick(view, position, "TreatmentEditRecord", "Treatment Plan", dictiatationModel.getVoiceEMRModel());
            }
        });



    }

    @Override
    public int getItemCount() {
        return voiceEMRModelsTreatmentPlanCategoryList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public List<VoiceEMRCategoryRecordModel> voiceEMRCategoryRecordModels;
        public TextView categoryName, subCategoryName;
        public LinearLayout categoryNameLinearLayoutCompact;
        public ImageButton allRecordDelete, singleRecordDelete,symptomsInfoButton,addRecord,editRecord;

        public MyViewHolder(@NonNull @NotNull View itemView, Context context) {
            super(itemView);
            categoryName = itemView.findViewById(R.id.categoryName);
            subCategoryName = itemView.findViewById(R.id.subCategoryName);
            categoryNameLinearLayoutCompact = itemView.findViewById(R.id.categoryNameLinearLayoutCompact);
            allRecordDelete = itemView.findViewById(R.id.allRecordDelete);
            singleRecordDelete = itemView.findViewById(R.id.singleRecordDelete);
            symptomsInfoButton = itemView.findViewById(R.id.infoButton);
            addRecord=itemView.findViewById(R.id.addRecord);
            editRecord = itemView.findViewById(R.id.editRecord);
        }
    }
}
