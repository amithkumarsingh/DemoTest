package com.whitecoats.adapter;

import android.app.Activity;
import android.graphics.Typeface;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

import com.whitecoats.model.PatientRecordsModel;
import com.whitecoats.clinicplus.PatientRecordsCaseActivity;
import com.whitecoats.clinicplus.R;

public class PatientRecordsCategoryAdapter extends RecyclerView.Adapter<PatientRecordsCategoryAdapter.MyViewHolder> {

    private List<PatientRecordsModel> patientRecordsModels;
    private Activity activity;

    public PatientRecordsCategoryAdapter(List<PatientRecordsModel> patientRecordsModels, Activity activity) {
        this.activity = activity;
        this.patientRecordsModels = patientRecordsModels;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.list_row_about_record_more, viewGroup, false);

        return new PatientRecordsCategoryAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {

        final PatientRecordsModel patientRecordsModel = patientRecordsModels.get(i);

        try {

//            Log.d("@@@@@@@@@@", "Hist Handdddd" + patientRecordsModel.getType());
            if(patientRecordsModel.getType() == 1) {
                myViewHolder.nonEpisodicLayout.setVisibility(View.GONE);
                myViewHolder.handNoteCard.setVisibility(View.VISIBLE);

                myViewHolder.handNoteDesp.setText(patientRecordsModel.getHnDesp());
                myViewHolder.fileUrl = patientRecordsModel.getHnAttachURL();
                myViewHolder.handNoteMedPres.setText(patientRecordsModel.getHnMedPres());
                myViewHolder.handNoteTestPres.setText(patientRecordsModel.getHnTestPres());
                myViewHolder.handNoteValid.setText(patientRecordsModel.getHnValidTill());

                myViewHolder.handNoteviewFile.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        PatientRecordsCaseActivity patientRecordsCaseActivity = (PatientRecordsCaseActivity) activity;
                        patientRecordsCaseActivity.getFileFromUrl(patientRecordsModel.getHnAttachURL());
                    }
                });

            } else {
                Log.d("Field Dic", patientRecordsModel.getFieldDictionary());
                Log.d("Field Values", patientRecordsModel.getCatRecordData());

                myViewHolder.nonEpisodicLayout.setVisibility(View.VISIBLE);
                myViewHolder.handNoteCard.setVisibility(View.GONE);
                JSONArray fieldArr = new JSONArray(patientRecordsModel.getFieldDictionary());
                JSONObject fieldValue = new JSONObject(patientRecordsModel.getCatRecordData());
//            Log.d("Valueeeeeee", fieldValue.toString());
//            Log.d("Arrrrrrr", fieldArr.toString());

                for (int k = 0; k < fieldArr.length(); k++) {
                    if (fieldValue.has(fieldArr.getJSONObject(k).getString("key"))) {
                        TextView tv1 = new TextView(activity);
                        tv1.setText(fieldValue.getString(fieldArr.getJSONObject(k).getString("key")));
                        tv1.setTypeface(tv1.getTypeface(), Typeface.BOLD);

                        TextView tv2 = new TextView(activity);
                        tv2.setText(fieldArr.getJSONObject(k).getString("name") + ": ");
                        tv2.setId(fieldValue.getInt("record_id"));

                        RelativeLayout relativeLayout = new RelativeLayout(activity);
                        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        params.addRule(RelativeLayout.END_OF, tv2.getId());
                        params.leftMargin = 20;
                        tv1.setLayoutParams(params);
                        relativeLayout.addView(tv2);
                        relativeLayout.addView(tv1, params);

                        myViewHolder.nonEpisodicLayout.addView(relativeLayout);
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public int getItemCount() {
        return patientRecordsModels.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        private CardView handNoteCard, symptomCard;
        private LinearLayout nonEpisodicLayout;
        private int type = 0;

        //hand notes section
        private TextView handNoteDesp, handNoteMedPres, handNoteTestPres, handNoteValid, handNoteviewFile;
        private String fileUrl;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            handNoteCard = itemView.findViewById(R.id.apptRecHistHandNotesCard);
            symptomCard = itemView.findViewById(R.id.apptRecHistSymptomCard);
            nonEpisodicLayout = itemView.findViewById(R.id.nonEpisodicDataLayout);

            handNoteviewFile = itemView.findViewById(R.id.apptRecHistAttachView);
            handNoteDesp = itemView.findViewById(R.id.apptRecHistDesp);
            handNoteMedPres = itemView.findViewById(R.id.apptRecHistMed);
            handNoteTestPres = itemView.findViewById(R.id.apptRecHistTest);
            handNoteValid = itemView.findViewById(R.id.apptRecHistValidDate);
        }
    }
}
