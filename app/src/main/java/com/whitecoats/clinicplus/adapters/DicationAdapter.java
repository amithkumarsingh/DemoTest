package com.whitecoats.clinicplus.adapters;

import android.content.Context;
import android.util.Log;
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
import com.whitecoats.clinicplus.models.DictationModel;
import com.whitecoats.clinicplus.models.RecordsItems;
import com.whitecoats.clinicplus.models.SectionNameModel;
import com.whitecoats.clinicplus.models.VoiceEMRCategoryRecordModel;
import com.whitecoats.clinicplus.models.VoiceEMRModel;
import com.whitecoats.clinicplus.utils.DictationItemType;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class DicationAdapter extends RecyclerView.Adapter {
    private final ArrayList<String> serialNumlist;
    private List<DictationItemType> consolidatedList;
    private VoiceEmrRecordClickListener voiceEmrRecordClickListener;
    private Context context;
    /*private RecordsItems generalItem;
    private DictationModel dictationModel;*/
    private int observationRecords = 0;
    private int positionNumber;

    public DicationAdapter(Context context, List<DictationItemType> consolidatedList, ArrayList<String> itemSerailNum, VoiceEmrRecordClickListener voiceEmrRecordClickListener) {
        this.context = context;
        this.consolidatedList = consolidatedList;
        this.voiceEmrRecordClickListener = voiceEmrRecordClickListener;
        this.serialNumlist=itemSerailNum;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        switch (viewType) {

            case DictationItemType.TYPE_GENERAL:
                View v1 = inflater.inflate(R.layout.dictation_records_lay, parent,
                        false);
                viewHolder = new MyViewHolder(v1);
                break;

            case DictationItemType.TYPE_SECTION_NAME:
                View v2 = inflater.inflate(R.layout.dictitation_section_name, parent, false);
                viewHolder = new SectionDictitationViewHolder(v2);
                break;
        }

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        switch (holder.getItemViewType()) {

            case DictationItemType.TYPE_GENERAL:
                RecordsItems generalItem = (RecordsItems) consolidatedList.get(position);
                DictationModel dictationModel = generalItem.getVoiceEMRModel();

                MyViewHolder generalViewHolder = (MyViewHolder) holder;
                Log.e("Records Name", position + 1 + ". " + dictationModel.getVoiceEMRModel().getSymptomName());
                if (dictationModel.getSection_name().equalsIgnoreCase("Symptoms")) {
                    generalViewHolder.subCategoryName.setText(serialNumlist.get(position)+ ". " + dictationModel.getVoiceEMRModel().getSymptomName());
                } else if (dictationModel.getSection_name().equalsIgnoreCase("Diagnosis")) {
                    generalViewHolder.subCategoryName.setText(serialNumlist.get(position)  + ". " +dictationModel.getVoiceEMRModel().getDiagnosisDiagnosis());
                } else if (dictationModel.getSection_name().equalsIgnoreCase("Investigation Results")) {
                    generalViewHolder.subCategoryName.setText(serialNumlist.get(position) + ". " +dictationModel.getVoiceEMRModel().getInvestigationInvestigation_name());
                } else if (dictationModel.getSection_name().equalsIgnoreCase("Observation")) {
                    generalViewHolder.subCategoryName.setText(serialNumlist.get(position) + ". " +dictationModel.getVoiceEMRModel().getObservationCategoryName());
                } else if (dictationModel.getSection_name().equalsIgnoreCase("Treatment Plan")) {
                    generalViewHolder.subCategoryName.setText(serialNumlist.get(position)  + ". " +dictationModel.getVoiceEMRModel().getTreatmentCategoryName());
                }

                generalViewHolder.singleRecordDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        voiceEmrRecordClickListener.onItemClick(view, position, "ItemSingleDelete",dictationModel.getSection_name(), dictationModel.getVoiceEMRModel(), consolidatedList.get(position).getRecordType());
                    }
                });
                generalViewHolder.editRecord.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        voiceEmrRecordClickListener.onItemClick(view, position, "editRecord", dictationModel.getSection_name(), dictationModel.getVoiceEMRModel(), consolidatedList.get(position).getRecordType());
                    }
                });
                break;

            case DictationItemType.TYPE_SECTION_NAME:
                SectionNameModel sectionNameModel = (SectionNameModel) consolidatedList.get(position);
                SectionDictitationViewHolder sectionViewHolder = (SectionDictitationViewHolder) holder;
                VoiceEMRModel voiceEMRModel=new VoiceEMRModel();
                Log.e("Records SectitionName", position + 1 + ". " + sectionNameModel.getSectionName());
                positionNumber=0;
                sectionViewHolder.categoryName.setText(sectionNameModel.getSectionName());
                sectionViewHolder.allRecordDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        voiceEmrRecordClickListener.onItemClick(view, position, "ItemAllDelete",sectionNameModel.getSectionName(), voiceEMRModel,consolidatedList.get(position).getRecordType());

                    }
                });
                sectionViewHolder.symptomsInfoButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        voiceEmrRecordClickListener.onItemClick(view, position, "Info", sectionNameModel.getSectionName(), voiceEMRModel, consolidatedList.get(position).getRecordType());
                    }
                });

                sectionViewHolder.addRecord.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        voiceEmrRecordClickListener.onItemClick(view, position, "addRecords", sectionNameModel.getSectionName(), voiceEMRModel, consolidatedList.get(position).getRecordType());
                    }
                });

                break;
        }
    }

    @Override
    public int getItemCount() {
        return consolidatedList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public List<VoiceEMRCategoryRecordModel> voiceEMRCategoryRecordModels;
        public TextView categoryName, subCategoryName;
        public LinearLayout categoryNameLinearLayoutCompact;
        public ImageButton allRecordDelete, singleRecordDelete, symptomsInfoButton, addRecord, editRecord;

        public MyViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            categoryName = itemView.findViewById(R.id.categoryName);
            subCategoryName = itemView.findViewById(R.id.subCategoryName);
            categoryNameLinearLayoutCompact = itemView.findViewById(R.id.categoryNameLinearLayoutCompact);
            allRecordDelete = itemView.findViewById(R.id.allRecordDelete);
            singleRecordDelete = itemView.findViewById(R.id.singleRecordDelete);
            symptomsInfoButton = itemView.findViewById(R.id.infoButton);
            addRecord = itemView.findViewById(R.id.addRecord);
            editRecord = itemView.findViewById(R.id.editRecord);
        }
    }


    public class SectionDictitationViewHolder extends RecyclerView.ViewHolder {
        public List<VoiceEMRCategoryRecordModel> voiceEMRCategoryRecordModels;
        public TextView categoryName, subCategoryName;
        public LinearLayout categoryNameLinearLayoutCompact;
        public ImageButton allRecordDelete, singleRecordDelete, symptomsInfoButton, addRecord, editRecord;
//        VoiceEMRCategoryRecordsListAdapter voiceEMRCategoryRecordsListAdapter;

        public SectionDictitationViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            categoryName = itemView.findViewById(R.id.categoryName);
            categoryNameLinearLayoutCompact = itemView.findViewById(R.id.categoryNameLinearLayoutCompact);
            allRecordDelete = itemView.findViewById(R.id.allRecordDelete);
            symptomsInfoButton = itemView.findViewById(R.id.infoButton);
            addRecord = itemView.findViewById(R.id.addRecord);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return consolidatedList.get(position).getType();
    }

}
