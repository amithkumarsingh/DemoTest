package com.whitecoats.adapter;

import android.app.Activity;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import com.whitecoats.model.PatientRecordsModel;
import com.whitecoats.clinicplus.R;
import com.zoho.salesiqembed.ZohoSalesIQ;

public class PatientRecordsAboutAdapter extends RecyclerView.Adapter<PatientRecordsAboutAdapter.MyViewHolder> {

    private List<PatientRecordsModel> patientRecordsModels;
    private Activity activity;

    public PatientRecordsAboutAdapter(List<PatientRecordsModel> patientRecordsModels, Activity activity) {
        this.activity = activity;
        this.patientRecordsModels = patientRecordsModels;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.list_row_about_records, viewGroup, false);

        return new PatientRecordsAboutAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder myViewHolder, int position) {

        final PatientRecordsModel patientRecordsModel = patientRecordsModels.get(position);

        if(patientRecordsModel.getType() == 1) {
            myViewHolder.recordCard.setVisibility(View.VISIBLE);
            myViewHolder.familyCard.setVisibility(View.GONE);
            myViewHolder.recordName.setText(patientRecordsModel.getCatName());
            myViewHolder.recordCount.setText(Integer.toString(patientRecordsModel.getRecordCount()));

        } else {
            myViewHolder.recordCard.setVisibility(View.GONE);
            myViewHolder.familyCard.setVisibility(View.VISIBLE);

            myViewHolder.famName.setText(patientRecordsModel.getFamName());
            myViewHolder.famAge.setText(patientRecordsModel.getFamAge());
            myViewHolder.famRelation.setText(patientRecordsModel.getFamRelation());
            myViewHolder.famProblems.setText(patientRecordsModel.getFamProblems());
        }

        myViewHolder.recordCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(myViewHolder.recordMoreData.getVisibility() == View.GONE) {
                    ZohoSalesIQ.Tracking.setCustomAction("AppointmentNotes - About Patient - Records View");
                    myViewHolder.recordMoreData.setVisibility(View.VISIBLE);
                    myViewHolder.arrowIcon.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_arrow_down));

                    myViewHolder.patientRecordsModelList = new ArrayList<>();
                    myViewHolder.patientRecordsCategoryAdapter = new PatientRecordsCategoryAdapter(myViewHolder.patientRecordsModelList, activity);
                    LinearLayoutManager horizontalLayoutManager = new LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false);
                    myViewHolder.moreDataRecycleView.setLayoutManager(horizontalLayoutManager);
                    myViewHolder.moreDataRecycleView.setAdapter(myViewHolder.patientRecordsCategoryAdapter);

                    try {
                        JSONArray valueArr = new JSONArray(patientRecordsModel.getCatRecordData());
                        if(valueArr.length() > 0) {
                            myViewHolder.emptyText.setVisibility(View.GONE);
                            for (int k = 0; k < valueArr.length(); k++) {
                                JSONObject valObj = valueArr.getJSONObject(k);
//                            Log.d("Valllll", valObj.getInt("category_id") + "");
//                            Log.d("File2222222", fieldObj.toString());
                                PatientRecordsModel model = new PatientRecordsModel();
                                model.setFieldDictionary(patientRecordsModel.getFieldDictionary());
                                model.setCatRecordData(valObj.toString());
                                myViewHolder.patientRecordsModelList.add(model);
                            }
                        } else {
                            myViewHolder.emptyText.setVisibility(View.VISIBLE);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    myViewHolder.patientRecordsCategoryAdapter.notifyDataSetChanged();

                } else {
                    myViewHolder.arrowIcon.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_arrow_right));
                    myViewHolder.recordMoreData.setVisibility(View.GONE);
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return patientRecordsModels.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        private TextView recordName, recordCount;
        private CardView recordCard, familyCard;
        private RelativeLayout recordMoreData;
        private RecyclerView moreDataRecycleView;
        private List<PatientRecordsModel> patientRecordsModelList;
        private PatientRecordsCategoryAdapter patientRecordsCategoryAdapter;
        private ImageView arrowIcon;
        private TextView emptyText;

        //family section
        private TextView famName, famAge, famRelation, famProblems;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            recordCard = itemView.findViewById(R.id.apptRecCaseRecordCard);
            recordName = itemView.findViewById(R.id.apptRecCaseRecordName);
            familyCard = itemView.findViewById(R.id.apptRecAboutFamilyCard);
            recordMoreData = itemView.findViewById(R.id.apptRecordMoreData);
            moreDataRecycleView = itemView.findViewById(R.id.apptRecordMoreRecycleView);
            recordCount = itemView.findViewById(R.id.apptRecCaseCount);
            arrowIcon = itemView.findViewById(R.id.apptAboutRecordArrow);
            emptyText = itemView.findViewById(R.id.apptRecordMoreEmpteyText);

            //family section
            famName = itemView.findViewById(R.id.famName);
            famAge = itemView.findViewById(R.id.famAge);
            famRelation = itemView.findViewById(R.id.famRelation);
            famProblems = itemView.findViewById(R.id.famProblems);
        }
    }
}
