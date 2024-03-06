package com.whitecoats.clinicplus.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.whitecoats.clinicplus.R;
import com.whitecoats.clinicplus.interfaces.VoiceEmrRecordClickListener;
import com.whitecoats.clinicplus.models.VoiceEMRCategoryRecordModel;
import com.whitecoats.clinicplus.models.VoiceEMRModel;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class VoiceEMRListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<VoiceEMRModel> voiceEMRModelsCategoryList;
    //    private List<VoiceEMRModel> voiceEMRModelsSubCategoryList;
    private VoiceEmrRecordClickListener voiceEmrRecordClickListener;
    private Context context;

    public VoiceEMRListAdapter(Context context, List<VoiceEMRModel> voiceEMRModelsCategoryList, VoiceEmrRecordClickListener voiceEmrRecordClickListener) {

        this.context = context;
        this.voiceEMRModelsCategoryList = voiceEMRModelsCategoryList;
        this.voiceEmrRecordClickListener = voiceEmrRecordClickListener;
//        this.voiceEMRModelsSubCategoryList = voiceEMRModelsSubCategoryList;

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
        if (voiceEMRModelsCategoryList.get(position).getCategoryNameSymptomExistType() == 1) {
            itemViewHolder.categoryNameLinearLayoutCompact.setVisibility(View.VISIBLE);
            itemViewHolder.categoryName.setText(voiceEMRModelsCategoryList.get(position).getCategoryName());
            itemViewHolder.subCategoryName.setText(position + 1 + ". " + voiceEMRModelsCategoryList.get(position).getSymptomName());
        } else {
            itemViewHolder.categoryNameLinearLayoutCompact.setVisibility(View.GONE);
            itemViewHolder.subCategoryName.setText(position + 1 + ". " + voiceEMRModelsCategoryList.get(position).getSymptomName());
        }

        itemViewHolder.allRecordDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });
        itemViewHolder.singleRecordDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });
        itemViewHolder.symptomsInfoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });
        itemViewHolder.addRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });

        itemViewHolder.editRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });
    }

    @Override
    public int getItemCount() {
        return voiceEMRModelsCategoryList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public List<VoiceEMRCategoryRecordModel> voiceEMRCategoryRecordModels;
        public TextView categoryName, subCategoryName;
        public LinearLayout categoryNameLinearLayoutCompact;
        public ImageButton allRecordDelete, singleRecordDelete, symptomsInfoButton, addRecord, editRecord;
//        VoiceEMRCategoryRecordsListAdapter voiceEMRCategoryRecordsListAdapter;

        public MyViewHolder(@NonNull @NotNull View itemView, Context context) {
            super(itemView);
            categoryName = itemView.findViewById(R.id.categoryName);
            subCategoryName = itemView.findViewById(R.id.subCategoryName);
            categoryNameLinearLayoutCompact = itemView.findViewById(R.id.categoryNameLinearLayoutCompact);
            allRecordDelete = itemView.findViewById(R.id.allRecordDelete);
            singleRecordDelete = itemView.findViewById(R.id.singleRecordDelete);
            symptomsInfoButton = itemView.findViewById(R.id.infoButton);
            addRecord = itemView.findViewById(R.id.addRecord);
            editRecord = itemView.findViewById(R.id.editRecord);
            // RecyclerView categoryRecordsRV = itemView.findViewById(R.id.categoryRecordsRV);
//            voiceEMRCategoryRecordsListAdapter = new VoiceEMRCategoryRecordsListAdapter(context, voiceEMRModelsSubCategoryList);
//            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(context);
//            categoryRecordsRV.setLayoutManager(mLayoutManager);
//            categoryRecordsRV.setItemAnimator(new DefaultItemAnimator());
//            categoryRecordsRV.setAdapter(voiceEMRCategoryRecordsListAdapter);
//            voiceEMRCategoryRecordModels = new ArrayList<>();
        }
    }
}
