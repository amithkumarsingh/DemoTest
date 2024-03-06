package com.whitecoats.clinicplus.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.whitecoats.clinicplus.R;
import com.whitecoats.clinicplus.interfaces.VoiceEmrRecordClickListener;
import com.whitecoats.clinicplus.models.EMRAddRecordCategoryModel;
import com.whitecoats.clinicplus.models.VoiceEMRModel;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class VoiceEMRVitalPopupListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private  VoiceEMRModel voiceEMRModel;
    private List<EMRAddRecordCategoryModel> voiceEMRModelsVitalsPopupCategoryList;
    private VoiceEmrRecordClickListener voiceEmrRecordClickListener;
    private Context context;

    public VoiceEMRVitalPopupListAdapter(Context context, List<EMRAddRecordCategoryModel> voiceEMRModelsVitalsPopupCategoryList, VoiceEMRModel voiceEMRModel, VoiceEmrRecordClickListener voiceEmrRecordClickListener) {

        this.context = context;
        this.voiceEMRModelsVitalsPopupCategoryList = voiceEMRModelsVitalsPopupCategoryList;
        this.voiceEmrRecordClickListener = voiceEmrRecordClickListener;
        this.voiceEMRModel=voiceEMRModel;
    }

    @NonNull
    @NotNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_row_voice_emr_vital_popup, parent, false);
        context = parent.getContext();
        return new MyViewHolder(view, context);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull RecyclerView.ViewHolder holder, int position) {
        MyViewHolder itemViewHolder = (MyViewHolder) holder;

            itemViewHolder.vitalCategoryName.setText(voiceEMRModelsVitalsPopupCategoryList.get(position).getCategoryName());

//        itemViewHolder.categoryNameLinearLayoutCompact.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                voiceEmrRecordClickListener.onItemClick(view, position, voiceEMRModelsVitalsPopupCategoryList.get(position).getCategoryId(),voiceEMRModelsVitalsPopupCategoryList.get(position).getCategoryName());
//            }
//        });

        itemViewHolder.radioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                voiceEmrRecordClickListener.onItemClick(view, position, voiceEMRModelsVitalsPopupCategoryList.get(position).getCategoryId(),voiceEMRModelsVitalsPopupCategoryList.get(position).getCategoryName(), voiceEMRModel,"");
            }
        });

    }

    @Override
    public int getItemCount() {
        return voiceEMRModelsVitalsPopupCategoryList.size();
//        return 5;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView vitalCategoryName;
        public LinearLayoutCompat categoryNameLinearLayoutCompact;
        public ImageButton allRecordDelete, singleRecordDelete,symptomsInfoButton,addRecord,editRecord;
        public RadioButton radioButton;
        public MyViewHolder(@NonNull @NotNull View itemView, Context context) {
            super(itemView);
            vitalCategoryName = itemView.findViewById(R.id.vitalCategoryName);
            radioButton= itemView.findViewById(R.id.radioButton);
            categoryNameLinearLayoutCompact=itemView.findViewById(R.id.categoryNameLinearLayoutCompact);

        }
    }
}
