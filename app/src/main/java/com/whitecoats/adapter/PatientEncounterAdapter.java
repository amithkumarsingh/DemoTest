package com.whitecoats.adapter;

import android.app.Activity;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import com.whitecoats.model.PatientRecordsModel;
import com.whitecoats.clinicplus.AppUtilities;
import com.whitecoats.clinicplus.PatientEpisodeActivity;
import com.whitecoats.clinicplus.R;

public class PatientEncounterAdapter extends RecyclerView.Adapter<PatientEncounterAdapter.MyViewHolder>  {

    private List<PatientRecordsModel> patientRecordsModels;
    private Activity activity;
    private AppUtilities appUtilities;

    public PatientEncounterAdapter(List<PatientRecordsModel> patientRecordsModels, Activity activity) {
        this.activity = activity;
        this.patientRecordsModels = patientRecordsModels;
        appUtilities = new AppUtilities();
    }

    @NonNull
    @Override
    public PatientEncounterAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_row_record_encounters, viewGroup, false);
        return new PatientEncounterAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final PatientEncounterAdapter.MyViewHolder myViewHolder, int i) {

        final PatientRecordsModel recordsModel = patientRecordsModels.get(i);
        //1 is for pdf data
        if (recordsModel.getType() == 1) {
            myViewHolder.pdfLayout.setVisibility(View.VISIBLE);
            myViewHolder.notesLayout.setVisibility(View.GONE);
            myViewHolder.symptomLayout.setVisibility(View.GONE);
            myViewHolder.diagLayout.setVisibility(View.GONE);
            myViewHolder.investLayout.setVisibility(View.GONE);
            myViewHolder.treatPlanLayout.setVisibility(View.GONE);
            myViewHolder.obsLayout.setVisibility(View.GONE);

            String temp = appUtilities.changeDateFormat("yyyy-MM-dd mm:HH:ss", "dd MMM, yy mm:HH", recordsModel.getPdfCreatedDate());
            myViewHolder.pdfCreatedOn.setText("Created on " + temp);

            myViewHolder.viewPdf.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    PatientEpisodeActivity episodeActivity = (PatientEpisodeActivity) activity;
                    episodeActivity.getFileFromUrl(recordsModel.getPdfUrl());
                }
            });
        } else if(recordsModel.getType() == 2) { //2 for written notes
            myViewHolder.notesLayout.setVisibility(View.VISIBLE);
            myViewHolder.pdfLayout.setVisibility(View.GONE);
            myViewHolder.symptomLayout.setVisibility(View.GONE);
            myViewHolder.diagLayout.setVisibility(View.GONE);
            myViewHolder.investLayout.setVisibility(View.GONE);
            myViewHolder.treatPlanLayout.setVisibility(View.GONE);
            myViewHolder.obsLayout.setVisibility(View.GONE);

            String temp = appUtilities.changeDateFormat("yyyy-MM-dd mm:HH:ss", "dd MMM, yy mm:HH", recordsModel.getHnValidTill());

            if(temp.equalsIgnoreCase(""))
            {
                myViewHolder.presValidDate.setText("-");

            }
            else {
                myViewHolder.presValidDate.setText(temp);
            }

            if(recordsModel.getHnDesp().equalsIgnoreCase(""))
            {
                myViewHolder.desp.setText("-");
            }
            else {
                myViewHolder.desp.setText(recordsModel.getHnDesp());
            }

            myViewHolder.medPresState.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_close));
            myViewHolder.medPresState.setColorFilter(new PorterDuffColorFilter(activity.getResources().getColor(R.color.colorDanger), PorterDuff.Mode.SRC_IN));
            if(recordsModel.getHnMedPres().equalsIgnoreCase("1")) {
                myViewHolder.medPresState.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_tick));
                myViewHolder.medPresState.setColorFilter(new PorterDuffColorFilter(activity.getResources().getColor(R.color.colorSuccess), PorterDuff.Mode.SRC_IN));
            }

            myViewHolder.testPresState.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_close));
            myViewHolder.testPresState.setColorFilter(new PorterDuffColorFilter(activity.getResources().getColor(R.color.colorDanger), PorterDuff.Mode.SRC_IN));
            if(recordsModel.getHnMedPres().equalsIgnoreCase("1")) {
                myViewHolder.testPresState.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_tick));
                myViewHolder.testPresState.setColorFilter(new PorterDuffColorFilter(activity.getResources().getColor(R.color.colorSuccess), PorterDuff.Mode.SRC_IN));
            }

            myViewHolder.viewNotes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    PatientEpisodeActivity episodeActivity = (PatientEpisodeActivity) activity;
                    episodeActivity.getFileFromUrl(recordsModel.getHnAttachURL());
                }
            });

        } else if(recordsModel.getType() == 3) { // 3 for symptoms
            myViewHolder.investLayout.setVisibility(View.GONE);
            myViewHolder.symptomLayout.setVisibility(View.VISIBLE);
            myViewHolder.diagLayout.setVisibility(View.GONE);
            myViewHolder.notesLayout.setVisibility(View.GONE);
            myViewHolder.pdfLayout.setVisibility(View.GONE);
            myViewHolder.treatPlanLayout.setVisibility(View.GONE);
            myViewHolder.obsLayout.setVisibility(View.GONE);

            myViewHolder.symptomName.setText(recordsModel.getSymptomName());
            myViewHolder.status.setText(recordsModel.getSymptomStatus());
            String temp = appUtilities.changeDateFormat("yyyy-MM-dd mm:HH:ss", "dd MMM, yy", recordsModel.getSymptomFirstSeen());
            String firstRecordDate = "First Recorded On: " + "<b>" + temp + "</b>";
            myViewHolder.firstSeen.setText(Html.fromHtml(firstRecordDate));
//            myViewHolder.firstSeen.setText("First Recorded On: " + temp);
        } else if(recordsModel.getType() == 4) { //4 for investigation
            myViewHolder.diagLayout.setVisibility(View.GONE);
            myViewHolder.investLayout.setVisibility(View.VISIBLE);
            myViewHolder.symptomLayout.setVisibility(View.GONE);
            myViewHolder.notesLayout.setVisibility(View.GONE);
            myViewHolder.pdfLayout.setVisibility(View.GONE);
            myViewHolder.treatPlanLayout.setVisibility(View.GONE);
            myViewHolder.obsLayout.setVisibility(View.GONE);

            myViewHolder.investName.setText(recordsModel.getInvestName());
            myViewHolder.investParam.setText(recordsModel.getInvestParam());
            myViewHolder.investValue.setText(recordsModel.getInvestValue());
            myViewHolder.investNote.setText(recordsModel.getInvestNote());

            if (recordsModel.getFileUrl().equalsIgnoreCase("") || recordsModel.getFileUrl().equalsIgnoreCase("null")) {
                myViewHolder.investViewFile.setText("Not available");
            }
            else
            {
                myViewHolder.investViewFile.setText("View");

            }


            myViewHolder.investViewFile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (myViewHolder.investViewFile.getText().toString().equalsIgnoreCase("Not available")) {
                        Toast.makeText(activity, "No attachment available", Toast.LENGTH_LONG).show();
                    } else {

                        PatientEpisodeActivity episodeActivity = (PatientEpisodeActivity) activity;
                        episodeActivity.getFileFromUrl(recordsModel.getFileUrl());
                    }
                }
            });
        } else if(recordsModel.getType() == 5) { //5 for diagnosis
            myViewHolder.diagLayout.setVisibility(View.VISIBLE);
            myViewHolder.investLayout.setVisibility(View.GONE);
            myViewHolder.symptomLayout.setVisibility(View.GONE);
            myViewHolder.notesLayout.setVisibility(View.GONE);
            myViewHolder.pdfLayout.setVisibility(View.GONE);
            myViewHolder.treatPlanLayout.setVisibility(View.GONE);
            myViewHolder.obsLayout.setVisibility(View.GONE);

            myViewHolder.diagName.setText(recordsModel.getDiagName());
            String temp = appUtilities.changeDateFormat("yyyy-MM-dd mm:HH:ss", "dd MMM, yy", recordsModel.getDiagPoisted());
            String postedDate = "Posited On: " + "<b>" + temp + "</b>";
            myViewHolder.diagPoistedOn.setText(Html.fromHtml(postedDate));
//            myViewHolder.diagPoistedOn.setText("Posited On: " + temp);
            myViewHolder.diagStatus.setText(recordsModel.getDiagStatus());
            temp = appUtilities.changeDateFormat("yyyy-MM-dd mm:HH:ss", "dd MMM, yy", recordsModel.getDiagConfirmed());
            String confirmDate = "<b>" + temp + "</b>";
            myViewHolder.diagConfirmedDate.setText(Html.fromHtml(confirmDate));
//            myViewHolder.diagConfirmedDate.setText(temp);
        } else if(recordsModel.getType() == 6) { //6 for treatment plan and observation
            myViewHolder.treatPlanLayout.setVisibility(View.VISIBLE);
            myViewHolder.diagLayout.setVisibility(View.GONE);
            myViewHolder.investLayout.setVisibility(View.GONE);
            myViewHolder.symptomLayout.setVisibility(View.GONE);
            myViewHolder.notesLayout.setVisibility(View.GONE);
            myViewHolder.pdfLayout.setVisibility(View.GONE);
            myViewHolder.obsLayout.setVisibility(View.GONE);

            myViewHolder.treatPlanCat.setText(recordsModel.getTreatPlanName());
            myViewHolder.treatPlanCount.setText(recordsModel.getTreatPlanCount() + "");

            PatientEpisodeActivity patientEpisodeActivity = (PatientEpisodeActivity) activity;
            if(patientEpisodeActivity.isAddNew) {
                myViewHolder.treatPlanCount.setVisibility(View.GONE);
            } else {
                myViewHolder.treatPlanCount.setVisibility(View.VISIBLE);
            }

            myViewHolder.treatPlanAddNew.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    PatientEpisodeActivity patientEpisodeActivity = (PatientEpisodeActivity) activity;
                    patientEpisodeActivity.goToCreateRecords(recordsModel.getRecordId(), recordsModel.getTreatPlanName(), "treatmentplan");
                }
            });

            myViewHolder.treatPlanLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(recordsModel.getTreatPlanCount() != 0) {
                        PatientEpisodeActivity patientEpisodeActivity = (PatientEpisodeActivity) activity;
                        patientEpisodeActivity.goToRecordsView(recordsModel.getRecordId(), recordsModel.getTreatPlanName(), "treatmentplan");
                    } else {
                        Toast.makeText(activity, activity.getResources().getString(R.string.no_data_found), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else if(recordsModel.getType() == 7) { //7 for eval obs
            myViewHolder.obsLayout.setVisibility(View.VISIBLE);
            myViewHolder.treatPlanLayout.setVisibility(View.GONE);
            myViewHolder.diagLayout.setVisibility(View.GONE);
            myViewHolder.investLayout.setVisibility(View.GONE);
            myViewHolder.symptomLayout.setVisibility(View.GONE);
            myViewHolder.notesLayout.setVisibility(View.GONE);
            myViewHolder.pdfLayout.setVisibility(View.GONE);

            myViewHolder.obsCat.setText(recordsModel.getObsCatName());
            myViewHolder.obsCount.setText(recordsModel.getObsCount() + "");

            PatientEpisodeActivity patientEpisodeActivity = (PatientEpisodeActivity) activity;
            if(patientEpisodeActivity.isAddNew) {
                myViewHolder.obsCount.setVisibility(View.GONE);
            } else {
                myViewHolder.obsCount.setVisibility(View.VISIBLE);
            }

            myViewHolder.obsAddNew.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    PatientEpisodeActivity patientEpisodeActivity = (PatientEpisodeActivity) activity;
                    patientEpisodeActivity.goToCreateRecords(recordsModel.getRecordId(), recordsModel.getObsCatName(), "observations");
                }
            });

            myViewHolder.obsLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(recordsModel.getObsCount() != 0) {
                        PatientEpisodeActivity patientEpisodeActivity = (PatientEpisodeActivity) activity;
                        patientEpisodeActivity.goToRecordsView(recordsModel.getRecordId(), recordsModel.getObsCatName(), "observations");
                    } else {
                        Toast.makeText(activity, activity.getResources().getString(R.string.no_data_found), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

    }

    @Override
    public int getItemCount() {
        return patientRecordsModels.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        //pdf
        private RelativeLayout pdfLayout;
        private TextView viewPdf, pdfCreatedOn;

        //written notes
        private RelativeLayout notesLayout;
        private TextView viewNotes, presValidDate, desp;
        private ImageView medPresState, testPresState;

        //eval symptom
        private RelativeLayout symptomLayout;
        private TextView symptomName, status, firstSeen;

        //eval invest
        private RelativeLayout investLayout;
        private TextView investName, investParam, investValue, investViewFile, investNote;

        //eval diagnosis
        private RelativeLayout diagLayout;
        private TextView diagName, diagPoistedOn, diagStatus, diagConfirmedDate;

        //eval obs
        private RelativeLayout obsLayout;
        private TextView obsCat, obsCount, obsAddNew;

        //treatment plan
        private RelativeLayout treatPlanLayout;
        private TextView treatPlanCat, treatPlanCount, treatPlanAddNew;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            //pdf
            pdfLayout = itemView.findViewById(R.id.recordEcntPdfLayout);
            viewPdf = itemView.findViewById(R.id.recordPdfView);
            pdfCreatedOn = itemView.findViewById(R.id.recordPdfCreatedOn);

            //notes
            notesLayout = itemView.findViewById(R.id.recordEcntNotesLayout);
            viewNotes = itemView.findViewById(R.id.recordNotesView);
            presValidDate = itemView.findViewById(R.id.recordNotesPresValid);
            desp = itemView.findViewById(R.id.recordNotesDescription);
            medPresState = itemView.findViewById(R.id.recordNotesMedPresIcon);
            testPresState = itemView.findViewById(R.id.recordNotesTestPresIcon);

            //eval symptom
            symptomLayout = itemView.findViewById(R.id.recordEcntEvalSymptomLayout);
            symptomName = itemView.findViewById(R.id.recordEvalSymptomName);
            status = itemView.findViewById(R.id.recordEvalSymptomStatus);
            firstSeen = itemView.findViewById(R.id.recordEvalSymptomFirstRecord);

            //eval invest
            investLayout = itemView.findViewById(R.id.recordEcntEvalInvestLayout);
            investName = itemView.findViewById(R.id.recordEvalInvestName);
            investParam = itemView.findViewById(R.id.recordEvalInvestParamName);
            investValue = itemView.findViewById(R.id.recordEvalInvestValue);
            investNote = itemView.findViewById(R.id.recordEvalInvestNote);
            investViewFile = itemView.findViewById(R.id.recordEvalInvestView);

            //eval diag
            diagLayout = itemView.findViewById(R.id.recordEcntEvalDiagnosLayout);
            diagName = itemView.findViewById(R.id.recordEvalDiagnosName);
            diagPoistedOn = itemView.findViewById(R.id.recordEvalDiagnosPosited);
            diagStatus = itemView.findViewById(R.id.recordEvalDiagnosStatus);
            diagConfirmedDate = itemView.findViewById(R.id.recordEvalDiagnosConfirm);

//            //eval obs
            obsLayout = itemView.findViewById(R.id.recordEvalObsLayout);
            obsCat = itemView.findViewById(R.id.recordEvalObsCatName);
            obsCount = itemView.findViewById(R.id.recordEvalObsCatCount);
            obsAddNew = itemView.findViewById(R.id.recordEvalObsAddNew);

            //treatment plan
            treatPlanLayout = itemView.findViewById(R.id.recordTreatPlanLayout);
            treatPlanCat = itemView.findViewById(R.id.recordTreatPlanCatName);
            treatPlanCount = itemView.findViewById(R.id.recordTreatPlanCatCount);
            treatPlanAddNew = itemView.findViewById(R.id.recordEvalTreatPlanAddNew);

        }
    }
}
