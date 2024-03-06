package com.whitecoats.clinicplus.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.whitecoats.clinicplus.R;
import com.whitecoats.clinicplus.models.VoiceEMRModel;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class VoiceEMRCategoryRecordsListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<VoiceEMRModel> voiceEMRModelsSubCategoryList;
    private Context context;

    public VoiceEMRCategoryRecordsListAdapter(Context context, List<VoiceEMRModel> voiceEMRModelsSubCategoryList) {
        this.context = context;
        this.voiceEMRModelsSubCategoryList = voiceEMRModelsSubCategoryList;
    }

    @NonNull
    @NotNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_row_voice_emr_category_record, parent, false);
        context = parent.getContext();
        return new MyViewHolder(view, context);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull RecyclerView.ViewHolder holder, int position) {
        MyViewHolder itemViewHolder = (MyViewHolder) holder;
        if (voiceEMRModelsSubCategoryList.get(position).getSubCategoryName().equalsIgnoreCase("Symptoms")) {
            itemViewHolder.subCategoryName.setText(voiceEMRModelsSubCategoryList.get(position).getSymptomName());
        } else if (voiceEMRModelsSubCategoryList.get(position).getSubCategoryName().equalsIgnoreCase("Diagnosis")) {
            itemViewHolder.subCategoryName.setText(voiceEMRModelsSubCategoryList.get(position).getDiagnosisDiagnosis());
        }

    }

    @Override
    public int getItemCount() {
        return voiceEMRModelsSubCategoryList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView subCategoryName;

        public MyViewHolder(@NonNull @NotNull View itemView, Context context) {
            super(itemView);
            subCategoryName = itemView.findViewById(R.id.subCategoryName);


        }
    }
}
