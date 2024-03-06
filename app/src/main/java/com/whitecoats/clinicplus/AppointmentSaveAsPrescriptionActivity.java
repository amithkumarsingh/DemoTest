package com.whitecoats.clinicplus;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.Api;
import com.whitecoats.adapter.PatientEncounterAdapter;
import com.whitecoats.clinicplus.apis.ApiUrls;
import com.whitecoats.clinicplus.utils.ErrorHandlerClass;
import com.whitecoats.model.PatientRecordsModel;
import com.zoho.salesiqembed.ZohoSalesIQ;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class AppointmentSaveAsPrescriptionActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener {

    private int episodeID, patientID, encounterID = 0, encounterIdTwo = 0;
    private List<Integer> ecntIdArrTwo;
    private Spinner ecntDropdown;
    private List<String> ecntListArr;
    private List<Integer> ecntIdArr;
    private ArrayAdapter<String> ecntAdapter;
    private RelativeLayout mainLayout;
    private ScrollView recordsEcntLayout;
    private ProgressDialog loadingDialog;
    public static int allInteractionSelectedPosition;
    private int appointmentMode;
    private String appointmentDate, appointmentTime;
    private String receiptUrl;

    private int flagGenerateAndShared = 0;

    //pdf
    private RelativeLayout pdfLayout;
    private LinearLayout pdfListLayout;
    private RecyclerView pdfRv;
    private List<PatientRecordsModel> pdfModel;
    private PatientEncounterAdapter pdfAdapter;
    private TextView pdfCount;

    //written notes
    private RelativeLayout writtenNoteLayout;
    private LinearLayout notesListLayout;
    private RecyclerView notesRv;
    private List<PatientRecordsModel> notesModel;
    private PatientEncounterAdapter notesAdapter;
    private TextView notesCount;
    private TextView notesAddNew;

    //evaluation
    private RelativeLayout evaluationLayout, evalDataLayout, symptomLayout, obsLayout, investLayout, diagLayout, obsMainLayout;
    private LinearLayout symptomListLayout, obsListLayout, investListLayout, diagListLayout;
    private RecyclerView symptomRv, obsRv, investRv, diagListRv;
    private TextView symptomCount, obsCount, investCount, diagCount, totalEvalCount;
    private List<PatientRecordsModel> symptomsModel, investModel, diagModel, obsCatModel;
    private PatientEncounterAdapter symptomAdapter, investAdapter, diagAdapter, obsAdapter;
    private TextView symptomAddNew, investAddNew, diagAddNew, evalAddNew, obsAddNew;

    //treat plan
    private RelativeLayout treatPlanLayout, treatPlanParentLayout;
    private RecyclerView treatPlanCatRv;
    private List<PatientRecordsModel> treatPlanModel;
    private PatientEncounterAdapter treatPlanAdapter;
    private TextView treatPlanCount, treatPlanAddNew;

    //share enct data;
    private LinearLayout shareEcntListLayout, shareTreatPlanDataList;
    private ScrollView shareEcntLayout;
    private Button shareDataBtn;
    private CheckBox shareGenPdf, shareEval, shareTreatPlan, shareSymptom, shareObs, shareInvest, shareDiag, shareAllEcnt;
    private ArrayList<Integer> selectedEcntArr, selectedEcntArrTwo, selectedTreatPlanArr;
    private ArrayList<CheckBox> ecntCbList, treatPlanCbList;
    private boolean anyOneEvaluation = false, anyOneTreatmentPlan = false;
    private int allSpecificEvaluation = 0, allSpecificTreatmentPlan = 0;
    private TextView shareNote;

    private PatientRecordsApi apiCall;
    public boolean isAddNew = false;
    private AppUtilities appUtilities;
    private ImageView closeButton;
    private RelativeLayout generateNowButtonLayout, generateNowAndShareLayout;
    private Button generateNowButton, generateNowAndShareButton;

    private TextView shareEcntNotes, headerField, shareEcntNotesGoToSetting;
    private View evaluationView, treatmentPlanView;
    private CheckBox shareEcntEval, shareEcntTreatPlan;
    private RelativeLayout generateLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointment_save_as_prescription);

        episodeID = getIntent().getIntExtra("EpisodeId", 0);
        patientID = getIntent().getIntExtra("PatientId", 0);
        boolean sharingRec = getIntent().getBooleanExtra("SharingRecords", false);
        appointmentMode = getIntent().getIntExtra("apptMode", 0);
        appointmentDate = getIntent().getStringExtra("apptDate");
        appointmentTime = getIntent().getStringExtra("apptTime");
        encounterIdTwo = getIntent().getIntExtra("encounterId", 0);
        ZohoSalesIQ.Tracking.setCustomAction("AppointmentNotes - Save Prescription");


        ecntDropdown = findViewById(R.id.recordEpisSelectEpis);
        mainLayout = findViewById(R.id.recordEpisMainCatLayout);
        apiCall = new PatientRecordsApi();
        ecntListArr = new ArrayList<>();
        ecntIdArr = new ArrayList<>();
        ecntIdArrTwo = new ArrayList<>();
        appUtilities = new AppUtilities();
        closeButton = findViewById(R.id.closeButton);

//        private TextView shareEcntNotes;
//        private View evaluationView,treatmentPlanView;
//        private CheckBox shareEcntEval,shareEcntTreatPlan;

        shareEcntNotes = findViewById(R.id.shareEcntNotes);
        evaluationView = findViewById(R.id.evaluationView);
        treatmentPlanView = findViewById(R.id.treatmentPlanView);
        shareEcntEval = findViewById(R.id.shareEcntEval);
        shareEcntTreatPlan = findViewById(R.id.shareEcntTreatPlan);
        generateLayout = findViewById(R.id.generateLayout);
        headerField = findViewById(R.id.headerField);
        shareEcntNotesGoToSetting = findViewById(R.id.shareEcntNotesGoToSetting);

        //pdf
        pdfLayout = findViewById(R.id.recordEpisPdfLayout);
        pdfListLayout = findViewById(R.id.recordPdfListLayout);
        pdfRv = findViewById(R.id.recordPdfRecyclerView);
        pdfCount = findViewById(R.id.recordEpisPdfCount);
        pdfModel = new ArrayList<>();
        pdfAdapter = new PatientEncounterAdapter(pdfModel, this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        pdfRv.setLayoutManager(mLayoutManager);
        pdfRv.setItemAnimator(new DefaultItemAnimator());
        pdfRv.setAdapter(pdfAdapter);

        //written note
        writtenNoteLayout = findViewById(R.id.recordEpisNotesLayout);
        notesListLayout = findViewById(R.id.recordNotesListLayout);
        notesRv = findViewById(R.id.recordNotesRecyclerView);
        notesCount = findViewById(R.id.recordEpisNoteCount);
        notesAddNew = findViewById(R.id.recordEpisNoteAddNew);
        notesModel = new ArrayList<>();
        notesAdapter = new PatientEncounterAdapter(notesModel, this);
        mLayoutManager = new LinearLayoutManager(getApplicationContext());
        notesRv.setLayoutManager(mLayoutManager);
        notesRv.setItemAnimator(new DefaultItemAnimator());
        notesRv.setAdapter(notesAdapter);
        generateNowButtonLayout = findViewById(R.id.generateNowButtonLayout);
        generateNowAndShareLayout = findViewById(R.id.generateNowAndShareLayout);
        generateNowButton = findViewById(R.id.generateNowButton);
        generateNowAndShareButton = findViewById(R.id.generateNowAndShareButton);


        //eval
        evaluationLayout = findViewById(R.id.recordEpisEvalLayout);
        evalDataLayout = findViewById(R.id.recordEpisEvaluationLayout);
        symptomLayout = findViewById(R.id.recordEvalSymptomLayout);
        obsLayout = findViewById(R.id.recordEvalObsLayout);
        investLayout = findViewById(R.id.recordEvalInvestLayout);
        diagLayout = findViewById(R.id.recordEvalDiagnosLayout);
        symptomListLayout = findViewById(R.id.recordSymptomListLayout);
        investListLayout = findViewById(R.id.recordInvestListLayout);
        diagListLayout = findViewById(R.id.recordDiagnosListLayout);
        symptomRv = findViewById(R.id.recordSymptomRecyclerView);
        investRv = findViewById(R.id.recordInvestRecyclerView);
        diagListRv = findViewById(R.id.recordDiagnosRecyclerView);
        symptomCount = findViewById(R.id.recordEvalSymptomCount);
        obsCount = findViewById(R.id.recordEvalObsCount);
        investCount = findViewById(R.id.recordEvalInvestCount);
        diagCount = findViewById(R.id.recordEvalDiagnosCount);
        totalEvalCount = findViewById(R.id.recordEpisEvalCount);
        obsMainLayout = findViewById(R.id.recordEpisObservationMainLayout);
        obsLayout = findViewById(R.id.recordEvalObsLayout);
        obsRv = findViewById(R.id.recordEpisObservationRecyclerView);
        symptomAddNew = findViewById(R.id.recordEvalSymptomAddNew);
        investAddNew = findViewById(R.id.recordEvalInvestAddNew);
        diagAddNew = findViewById(R.id.recordEvalDiagnosAddNew);
        evalAddNew = findViewById(R.id.recordEpisEvalAddNew);
        obsAddNew = findViewById(R.id.recordEvalObsAddNew);
        symptomsModel = new ArrayList<>();
        investModel = new ArrayList<>();
        diagModel = new ArrayList<>();
        obsCatModel = new ArrayList<>();
        symptomAdapter = new PatientEncounterAdapter(symptomsModel, this);
        investAdapter = new PatientEncounterAdapter(investModel, this);
        diagAdapter = new PatientEncounterAdapter(diagModel, this);
        obsAdapter = new PatientEncounterAdapter(obsCatModel, this);

        mLayoutManager = new LinearLayoutManager(getApplicationContext());
        symptomRv.setLayoutManager(mLayoutManager);
        symptomRv.setItemAnimator(new DefaultItemAnimator());
        symptomRv.setAdapter(symptomAdapter);
        mLayoutManager = new LinearLayoutManager(getApplicationContext());
        investRv.setLayoutManager(mLayoutManager);
        investRv.setItemAnimator(new DefaultItemAnimator());
        investRv.setAdapter(investAdapter);
        mLayoutManager = new LinearLayoutManager(getApplicationContext());
        diagListRv.setLayoutManager(mLayoutManager);
        diagListRv.setItemAnimator(new DefaultItemAnimator());
        diagListRv.setAdapter(diagAdapter);
        mLayoutManager = new LinearLayoutManager(getApplicationContext());
        obsRv.setLayoutManager(mLayoutManager);
        obsRv.setItemAnimator(new DefaultItemAnimator());
        obsRv.setAdapter(obsAdapter);

        //treat plan
        treatPlanLayout = findViewById(R.id.recordEpisTreatmentLayout);
        treatPlanParentLayout = findViewById(R.id.recordEpisTreatmentMainLayout);
        treatPlanCatRv = findViewById(R.id.recordEpisTreatmentRecyclerView);
        treatPlanModel = new ArrayList<>();
        treatPlanAdapter = new PatientEncounterAdapter(treatPlanModel, this);
        treatPlanCount = findViewById(R.id.recordEpisTreatmentCount);
        mLayoutManager = new LinearLayoutManager(getApplicationContext());
        treatPlanCatRv.setLayoutManager(mLayoutManager);
        treatPlanCatRv.setItemAnimator(new DefaultItemAnimator());
        treatPlanCatRv.setAdapter(treatPlanAdapter);
        treatPlanAddNew = findViewById(R.id.recordEpisTreatmentAddNew);

        ecntAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, ecntListArr);
        ecntAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ecntDropdown.setAdapter(ecntAdapter);

        //share enct
        shareEcntListLayout = findViewById(R.id.shareEpisodeEcntListLayout);
//        shareTreatPlanDataList = findViewById(R.id.shareEcntTreatPlanListLayout);
        shareEcntLayout = findViewById(R.id.shareEcntLayout);
        recordsEcntLayout = findViewById(R.id.recordsEcntLayout);
//        shareDataBtn = findViewById(R.id.shareEcntDataBtn);
        shareGenPdf = findViewById(R.id.shareEcntGenPdf);
        shareNote = findViewById(R.id.shareEcntNotes);
        shareEval = findViewById(R.id.shareEcntEval);
        shareSymptom = findViewById(R.id.shareEcntSymptom);
        shareObs = findViewById(R.id.shareEcntObs);
        shareInvest = findViewById(R.id.shareEcntInvest);
        shareTreatPlan = findViewById(R.id.shareEcntTreatPlan);
        shareDiag = findViewById(R.id.shareEcntDiag);
        shareAllEcnt = findViewById(R.id.shareEcntAll);
        selectedEcntArr = new ArrayList<>();
        selectedEcntArrTwo = new ArrayList<>();
        selectedTreatPlanArr = new ArrayList<>();
        ecntCbList = new ArrayList<>();
        treatPlanCbList = new ArrayList<>();

        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        getEpisEncounter();
        getEpisodicPref();
        getEpisodicEvaluationPref();
//        getEpisPDF();
//        getEpisEvalutaion();
//        getEpisWrittenNote();
//        getEpisEvalutaion();


        generateNowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ZohoSalesIQ.Tracking.setCustomAction("AppointmentNotes - PDF - Generate");

                try {
                    // if (selectedEcntArrTwo.size() > 0) {
                    JSONObject shareObj = new JSONObject();
                    shareObj.put("patient_id", patientID);
                    shareObj.put("episode_id", episodeID);
                    shareObj.put("encounter_id", encounterIdTwo);

                    //shareObj.put("doctor_id", AppConfigClass.doctorId);
                    // shareObj.put("encounters", new JSONArray(selectedEcntArrTwo));
                    // shareObj.put("generated_pdfs", shareGenPdf.isChecked());
                    //shareObj.put("written_notes", shareNote.isChecked());
                    shareObj.put("evaluation", shareEval.isChecked());
                    // shareObj.put("selectedTreatmentPlans", new JSONArray(selectedTreatPlanArr));
                    shareObj.put("symptoms", shareSymptom.isChecked());
                    shareObj.put("observations", shareObs.isChecked());
                    shareObj.put("investigations", shareInvest.isChecked());
                    shareObj.put("diagnosis", shareDiag.isChecked());
//                    shareObj.put("diagnosis", )
                    shareObj.put("treatmentplan", shareTreatPlan.isChecked());
                    // shareObj.put("allSpecificEvaluation", allSpecificEvaluation);
                    //shareObj.put("allSpecificTreatmentPlan", allSpecificTreatmentPlan);

//                        shareObj.put("anyOneEvaluation", false);
//                        if (shareSymptom.isChecked() || shareObs.isChecked() || shareInvest.isChecked()) {
//                            shareObj.put("anyOneEvaluation", true);
//                        }
//
//                        shareObj.put("anyOneTreatmentPlan", false);
//                        if (selectedTreatPlanArr.size() > 0) {
//                            shareObj.put("anyOneTreatmentPlan", true);
//                        }

//                    Log.d("Share Data", shareObj.toString());
                    shareEpisData(shareObj);
//                    } else {
//                        Toast.makeText(AppointmentSaveAsPrescriptionActivity.this, "Please select one interaction", Toast.LENGTH_SHORT).show();
//                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

                //     Toast.makeText(getApplicationContext(),"generate now button",Toast.LENGTH_LONG).show();

            }
        });

        generateNowAndShareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ZohoSalesIQ.Tracking.setCustomAction("AppointmentNotes - PDF - Generate and Share");

                flagGenerateAndShared = 1;

                try {
                    // if (selectedEcntArrTwo.size() > 0) {
                    JSONObject shareObj = new JSONObject();
                    shareObj.put("patient_id", patientID);
                    shareObj.put("episode_id", episodeID);
                    shareObj.put("encounter_id", encounterIdTwo);

                    //shareObj.put("doctor_id", AppConfigClass.doctorId);
                    // shareObj.put("encounters", new JSONArray(selectedEcntArrTwo));
                    // shareObj.put("generated_pdfs", shareGenPdf.isChecked());
                    //shareObj.put("written_notes", shareNote.isChecked());
                    shareObj.put("evaluation", shareEval.isChecked());
                    // shareObj.put("selectedTreatmentPlans", new JSONArray(selectedTreatPlanArr));
                    shareObj.put("symptoms", shareSymptom.isChecked());
                    shareObj.put("observations", shareObs.isChecked());
                    shareObj.put("investigations", shareInvest.isChecked());
                    shareObj.put("diagnosis", shareDiag.isChecked());
//                    shareObj.put("diagnosis", )
                    shareObj.put("treatmentplan", shareTreatPlan.isChecked());
                    // shareObj.put("allSpecificEvaluation", allSpecificEvaluation);
                    //shareObj.put("allSpecificTreatmentPlan", allSpecificTreatmentPlan);

//                        shareObj.put("anyOneEvaluation", false);
//                        if (shareSymptom.isChecked() || shareObs.isChecked() || shareInvest.isChecked()) {
//                            shareObj.put("anyOneEvaluation", true);
//                        }
//
//                        shareObj.put("anyOneTreatmentPlan", false);
//                        if (selectedTreatPlanArr.size() > 0) {
//                            shareObj.put("anyOneTreatmentPlan", true);
//                        }

//                    Log.d("Share Data", shareObj.toString());
                    shareEpisData(shareObj);
//                    } else {
//                        Toast.makeText(AppointmentSaveAsPrescriptionActivity.this, "Please select one interaction", Toast.LENGTH_SHORT).show();
//                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }


//                generateAndShareNow();

                //  Toast.makeText(getApplicationContext(), "generate now and share", Toast.LENGTH_LONG).show();
            }
        });

        recordsEcntLayout.setVisibility(View.VISIBLE);
        shareEcntLayout.setVisibility(View.GONE);
        if (sharingRec) {
            getEpisTreatPlan();
            recordsEcntLayout.setVisibility(View.GONE);
            shareEcntLayout.setVisibility(View.VISIBLE);
        }

        ecntDropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
//                pdfList.setVisibility(View.GONE);
//                noteList.setVisibility(View.GONE);

                allInteractionSelectedPosition = i;

                pdfModel.clear();
                notesModel.clear();
                investModel.clear();
                symptomsModel.clear();
                diagModel.clear();
                treatPlanModel.clear();
                obsCatModel.clear();

                pdfAdapter.notifyDataSetChanged();
                notesAdapter.notifyDataSetChanged();
                investAdapter.notifyDataSetChanged();
                symptomAdapter.notifyDataSetChanged();
                diagAdapter.notifyDataSetChanged();
                treatPlanAdapter.notifyDataSetChanged();
                obsAdapter.notifyDataSetChanged();

                if (ecntIdArr.get(i) == 0) {
                    encounterID = 0;

                    getEpisPDF();
                    getEpisEvalutaion();
                    getEpisWrittenNote();
                    getEpisTreatPlan();
//                    if(mainCat.equalsIgnoreCase("Main")) {
//                        getAllEncounterHistory(0);
//                    } else if(mainCat.equalsIgnoreCase("Observation")) {
//                        obsListArr.clear();
//                        getObservationData(0);
//                    } else if(mainCat.equalsIgnoreCase("Treatment")) {
//                        treatListArr.clear();
//                        getTreatmentPlanData(0);
//                    } else {
//                        getEvaluationData(0);
//                    }
                } else {
                    encounterID = ecntIdArr.get(i);
//                    Log.d("Encounter ID", encounterID + "");
                    getEpisPDF();
                    getEpisEvalutaion();
                    getEpisWrittenNote();
                    getEpisTreatPlan();
//                    getEncounterData(ecntIdArr.get(i));
//                    if(mainCat.equalsIgnoreCase("Main")) {
//                        getAllEncounterHistory(ecntIdArr.get(i));
//                    } else if(mainCat.equalsIgnoreCase("Observation")) {
//                        obsListArr.clear();
//                        getObservationData(ecntIdArr.get(i));
//                    } else if(mainCat.equalsIgnoreCase("Treatment")) {
//                        treatListArr.clear();
//                        getTreatmentPlanData(ecntIdArr.get(i));
//                    } else {
//                        getEvaluationData(ecntIdArr.get(i));
//                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        pdfLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!pdfCount.getText().toString().equalsIgnoreCase("0")) {
                    if (pdfListLayout.getVisibility() == View.GONE) {
                        pdfListLayout.setVisibility(View.VISIBLE);

//                    pdfModel.clear();
//                    getEpisPDF();
                    } else {
                        pdfListLayout.setVisibility(View.GONE);
                    }
                } else {
                    Toast.makeText(AppointmentSaveAsPrescriptionActivity.this, getResources().getString(R.string.no_data_found), Toast.LENGTH_SHORT).show();
                }
            }
        });

        writtenNoteLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!notesCount.getText().toString().equalsIgnoreCase("0")) {
                    if (notesListLayout.getVisibility() == View.GONE) {
                        notesListLayout.setVisibility(View.VISIBLE);

//                    notesModel.clear();
//                    getEpisWrittenNote();
                    } else {
                        notesListLayout.setVisibility(View.GONE);
                    }
                } else {
                    Toast.makeText(AppointmentSaveAsPrescriptionActivity.this, getResources().getString(R.string.no_data_found), Toast.LENGTH_SHORT).show();
                }
            }
        });

        evaluationLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!totalEvalCount.getText().toString().equalsIgnoreCase("0")) {
                    evalDataLayout.setVisibility(View.VISIBLE);
                    mainLayout.setVisibility(View.GONE);
                } else {
                    Toast.makeText(AppointmentSaveAsPrescriptionActivity.this, getResources().getString(R.string.no_data_found), Toast.LENGTH_SHORT).show();
                }

//                getEpisEvalutaion();
            }
        });

        symptomLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!symptomCount.getText().toString().equalsIgnoreCase("0")) {
                    if (symptomListLayout.getVisibility() == View.GONE) {
                        symptomListLayout.setVisibility(View.VISIBLE);
                    } else {
                        symptomListLayout.setVisibility(View.GONE);
                    }
                } else {
                    Toast.makeText(AppointmentSaveAsPrescriptionActivity.this, getResources().getString(R.string.no_data_found), Toast.LENGTH_SHORT).show();
                }
            }
        });

        investLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!investCount.getText().toString().equalsIgnoreCase("0")) {
                    if (investListLayout.getVisibility() == View.GONE) {
                        investListLayout.setVisibility(View.VISIBLE);
                    } else {
                        investListLayout.setVisibility(View.GONE);
                    }
                } else {
                    Toast.makeText(AppointmentSaveAsPrescriptionActivity.this, getResources().getString(R.string.no_data_found), Toast.LENGTH_SHORT).show();
                }
            }
        });

        diagLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!diagCount.getText().toString().equalsIgnoreCase("0")) {
                    if (diagListLayout.getVisibility() == View.GONE) {
                        diagListLayout.setVisibility(View.VISIBLE);
                    } else {
                        diagListLayout.setVisibility(View.GONE);
                    }
                } else {
                    Toast.makeText(AppointmentSaveAsPrescriptionActivity.this, getResources().getString(R.string.no_data_found), Toast.LENGTH_SHORT).show();
                }
            }
        });

        treatPlanLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!treatPlanCount.getText().toString().equalsIgnoreCase("0")) {
                    mainLayout.setVisibility(View.GONE);
                    treatPlanParentLayout.setVisibility(View.VISIBLE);
                    evalDataLayout.setVisibility(View.GONE);
                    obsMainLayout.setVisibility(View.GONE);
                } else {
                    Toast.makeText(AppointmentSaveAsPrescriptionActivity.this, getResources().getString(R.string.no_data_found), Toast.LENGTH_SHORT).show();
                }
            }
        });

        obsLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!obsCount.getText().toString().equalsIgnoreCase("0")) {
                    mainLayout.setVisibility(View.GONE);
                    obsMainLayout.setVisibility(View.VISIBLE);
                    treatPlanParentLayout.setVisibility(View.GONE);
                    evalDataLayout.setVisibility(View.GONE);
                } else {
                    Toast.makeText(AppointmentSaveAsPrescriptionActivity.this, getResources().getString(R.string.no_data_found), Toast.LENGTH_SHORT).show();
                }
            }
        });

        notesAddNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AppointmentSaveAsPrescriptionActivity.this, PatientCreateRecordActivity.class);
                intent.putExtra("RecordName", "WrittenNotes");
                intent.putExtra("PatientId", patientID);
                intent.putExtra("EpisodeId", episodeID);
//                startActivity(intent);
                startActivityForResult(intent, 1);
            }
        });

        symptomAddNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AppointmentSaveAsPrescriptionActivity.this, PatientCreateRecordActivity.class);
                intent.putExtra("RecordName", "Symptoms");
                intent.putExtra("PatientId", patientID);
                intent.putExtra("EpisodeId", episodeID);
//                startActivity(intent);
                startActivityForResult(intent, 1);
            }
        });

        investAddNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AppointmentSaveAsPrescriptionActivity.this, PatientCreateRecordActivity.class);
                intent.putExtra("RecordName", "Investigation");
                intent.putExtra("PatientId", patientID);
                intent.putExtra("EpisodeId", episodeID);
//                startActivity(intent);
                startActivityForResult(intent, 1);
            }
        });

        diagAddNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AppointmentSaveAsPrescriptionActivity.this, PatientCreateRecordActivity.class);
                intent.putExtra("RecordName", "Diagnosis");
                intent.putExtra("PatientId", patientID);
                intent.putExtra("EpisodeId", episodeID);
//                startActivity(intent);
                startActivityForResult(intent, 1);
            }
        });

        treatPlanAddNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isAddNew = true;
                treatPlanAdapter.notifyDataSetChanged();
                mainLayout.setVisibility(View.GONE);
                treatPlanParentLayout.setVisibility(View.VISIBLE);
                evalDataLayout.setVisibility(View.GONE);
                obsMainLayout.setVisibility(View.GONE);
            }
        });

        evalAddNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                evalDataLayout.setVisibility(View.VISIBLE);
                mainLayout.setVisibility(View.GONE);
            }
        });

        obsAddNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isAddNew = true;
                obsAdapter.notifyDataSetChanged();
                mainLayout.setVisibility(View.GONE);
                obsMainLayout.setVisibility(View.VISIBLE);
                treatPlanParentLayout.setVisibility(View.GONE);
                evalDataLayout.setVisibility(View.GONE);
            }
        });

//        shareDataBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                try {
//                    if (selectedEcntArrTwo.size() > 0) {
//                        JSONObject shareObj = new JSONObject();
//                        shareObj.put("patient_id", patientID);
//                        shareObj.put("doctor_id", AppConfigClass.doctorId);
//                        shareObj.put("encounters", new JSONArray(selectedEcntArrTwo));
//                        shareObj.put("generated_pdfs", shareGenPdf.isChecked());
//                        shareObj.put("written_notes", shareNote.isChecked());
//                        shareObj.put("evaluations", shareEval.isChecked());
//                        shareObj.put("selectedTreatmentPlans", new JSONArray(selectedTreatPlanArr));
//                        shareObj.put("symptoms", shareSymptom.isChecked());
//                        shareObj.put("observations", shareObs.isChecked());
//                        shareObj.put("investigation_results", shareInvest.isChecked());
//                        shareObj.put("diagnosis", shareDiag.isChecked());
////                    shareObj.put("diagnosis", )
//                        shareObj.put("treatment_plan", shareTreatPlan.isChecked());
//                        shareObj.put("allSpecificEvaluation", allSpecificEvaluation);
//                        shareObj.put("allSpecificTreatmentPlan", allSpecificTreatmentPlan);
//
//                        shareObj.put("anyOneEvaluation", false);
//                        if (shareSymptom.isChecked() || shareObs.isChecked() || shareInvest.isChecked()) {
//                            shareObj.put("anyOneEvaluation", true);
//                        }
//
//                        shareObj.put("anyOneTreatmentPlan", false);
//                        if (selectedTreatPlanArr.size() > 0) {
//                            shareObj.put("anyOneTreatmentPlan", true);
//                        }
//
//                        Log.d("Share Data", shareObj.toString());
//                        shareEpisData(shareObj);
//                    } else {
//                        Toast.makeText(AppointmentSaveAsPrescriptionActivity.this, "Please select one interaction", Toast.LENGTH_SHORT).show();
//                    }
//
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        });

        //shareGenPdf.setOnCheckedChangeListener(this);
        shareTreatPlan.setOnCheckedChangeListener(this);
        //shareNote.setOnCheckedChangeListener(this);
        shareEval.setOnCheckedChangeListener(this);
        shareSymptom.setOnCheckedChangeListener(this);
        shareObs.setOnCheckedChangeListener(this);
        shareInvest.setOnCheckedChangeListener(this);
        shareTreatPlan.setOnCheckedChangeListener(this);
        shareDiag.setOnCheckedChangeListener(this);
//        shareAllEcnt.setOnCheckedChangeListener(this);
    }

    @Override
    public void onBackPressed() {
        if (obsMainLayout.getVisibility() == View.VISIBLE) {
            obsMainLayout.setVisibility(View.GONE);
            evalDataLayout.setVisibility(View.VISIBLE);
            isAddNew = false;
            obsAdapter.notifyDataSetChanged();
        } else if (treatPlanParentLayout.getVisibility() == View.VISIBLE) {
            evalDataLayout.setVisibility(View.GONE);
            mainLayout.setVisibility(View.VISIBLE);
            treatPlanParentLayout.setVisibility(View.GONE);
            isAddNew = false;
            treatPlanAdapter.notifyDataSetChanged();
        } else if (evalDataLayout.getVisibility() == View.VISIBLE) {
            evalDataLayout.setVisibility(View.GONE);
            mainLayout.setVisibility(View.VISIBLE);
        } else {
            finish();
        }
    }

    private void getEpisEncounter() {
        String url = ApiUrls.getEpisEncounter + "?episode_id=" + episodeID + "&patient_id=" + patientID;

//        CheckBox cb = new CheckBox(PatientEpisodeActivity.this);
//        cb.setText("All Encounters");
//        cb.setId(0);
//        cb.setOnCheckedChangeListener(this);
//        cb.setTag("Ecnt");
//        shareEcntListLayout.addView(cb);

        //  String[] interactionMode = {"Interaction Mode", "Video", "Chat", "Clinic", "Phone", "Home", "Other"};

        String tempDate = appUtilities.changeDateFormat("yyyy-MM-dd", "dd MMM, yy", appointmentDate);
        String tempTime = appUtilities.changeDateFormat("HH:mm:ss", "HH:mm", appointmentTime);


        String appointmentModeString = "";

        if (appointmentMode == 3) {
            appointmentModeString = "Clinic";
        } else if (appointmentMode == 1) {
            appointmentModeString = "Video";

        } else if (appointmentMode == 2) {
            appointmentModeString = "Chat";

        } else if (appointmentMode == 4) {
            appointmentModeString = "Phone";

        } else if (appointmentMode == 5) {
            appointmentModeString = "Home";

        } else if (appointmentMode == 6) {
            appointmentModeString = "Other";

        }

//        //share ecnt list
//        CheckBox cb = new CheckBox(AppointmentSaveAsPrescriptionActivity.this);
//        cb.setText(appointmentModeString + " on " + tempDate + " at " + tempTime);
//        //cb.setId(enctObj.getInt("id"));
//        cb.setId(encounterIdTwo);
//        cb.setTag("Ecnt");
////        cb.setOnCheckedChangeListener(AppointmentEpisodeActivity.this);
//
//        cb.setChecked(true);
//        cb.setEnabled(false);
//        ecntCbList.add(cb);
//        shareEcntListLayout.addView(cb);
//
        selectedEcntArrTwo.add(encounterIdTwo);


//        apiCall.getRecordPref(url, "", this, new VolleyCallback() {
//            @Override
//            public void onSuccess(String result) {
//                try {
//
//                    JSONObject resObj = new JSONObject(result);
//                    JSONArray enctArr = resObj.getJSONArray("response");
//                    if (enctArr.length() > 0) {
//                        ecntListArr.add(getResources().getString(R.string.all_encounter));
//                        ecntIdArr.add(0);
////                        for(int i=0; i<enctArr.length(); i++) {
//                        for (int i = 0; i < 1; i++) {
//                            JSONObject enctObj = enctArr.getJSONObject(i);
//                            String dateStr = enctObj.getString("encounter_date_time");
//                            DateFormat srcDf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//                            Date date = srcDf.parse(dateStr);
//                            DateFormat destDf = new SimpleDateFormat("dd, MMM yy HH:mm");
//                            dateStr = destDf.format(date);
//
//
//                            String ecntStr = enctObj.getString("encounter_mode") + " on " + dateStr;
//                            ecntListArr.add(ecntStr);
//                            ecntIdArr.add(enctObj.getInt("id"));
//
//                            ecntAdapter.notifyDataSetChanged();
//
//                            //share ecnt list
//                            CheckBox cb = new CheckBox(AppointmentEpisodeActivity.this);
//                            cb.setText(ecntStr);
//                            cb.setId(enctObj.getInt("id"));
//                            cb.setTag("Ecnt");
//                            cb.setOnCheckedChangeListener(AppointmentEpisodeActivity.this);
//                            ecntCbList.add(cb);
//                            shareEcntListLayout.addView(cb);
//
//                        }
//                    } else {
//                        ecntListArr.add(getResources().getString(R.string.no_encounter_shared_with_you));
//                        ecntIdArr.add(-1);
//                        ecntAdapter.notifyDataSetChanged();
//                    }
//
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//
//            @Override
//            public void onError(String err) {
//
//            }
//        });
    }

    private void getEpisPDF() {
        String url = ApiUrls.getEpisPdf + "?doctor_id=" + ApiUrls.doctorId + "&episode_id=" + episodeID + "&patient_id=" + patientID + "&sharedOrCreated=1&encounter_id=" + encounterID;
        if (encounterID != 0) {
            url = ApiUrls.getEcntPdf + "?doctor_id=" + ApiUrls.doctorId + "&episode_id=" + episodeID + "&patient_id=" + patientID + "&sharedOrCreated=1&encounter_id=" + encounterID;
        }
//        Log.d("PDF URL", url);
        apiCall.getRecordPref(url, "", this, new VolleyCallback() {
            @Override
            public void onSuccess(String result) {
                try {

                    JSONObject resObj = new JSONObject(result);
                    JSONArray pdfArr = resObj.getJSONArray("response");
                    if (pdfArr.length() > 0) {
                        for (int i = 0; i < pdfArr.length(); i++) {
//                            Log.d("Data", pdfArr.toString());
                            JSONObject pdfObj = pdfArr.getJSONObject(i);
                            PatientRecordsModel model = new PatientRecordsModel();
                            model.setPdfUrl(pdfObj.getString("url"));
                            model.setPdfCreatedDate(pdfObj.getString("created_at"));
                            model.setType(1);

                            pdfModel.add(model);
                        }

                        pdfCount.setText(pdfArr.length() + "");
                        pdfAdapter.notifyDataSetChanged();
                    } else {
                        pdfListLayout.setVisibility(View.GONE);
                        pdfCount.setText(pdfArr.length() + "");
//                        Toast.makeText(PatientEpisodeActivity.this, getResources().getString(R.string.no_data_found), Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(String err) {
                ErrorHandlerClass.INSTANCE.errorHandler(AppointmentSaveAsPrescriptionActivity.this, err);
            }
        });
    }

    private void getEpisWrittenNote() {
        String url = ApiUrls.getWrittenNotes + "?doctor_id=" + ApiUrls.doctorId + "&episode_id=" + episodeID + "&patient_id=" + patientID + "&sharedOrCreated=1&encounter_id=" + encounterID;
        if (encounterID != 0) {
            url = ApiUrls.getEcntWrittenNotes + "?doctor_id=" + ApiUrls.doctorId + "&episode_id=" + episodeID + "&patient_id=" + patientID + "&sharedOrCreated=1&encounter_id=" + encounterID;
        }
        apiCall.getRecordPref(url, "", this, new VolleyCallback() {
            @Override
            public void onSuccess(String result) {
                try {

                    JSONObject resObj = new JSONObject(result);
                    JSONArray notesArr = resObj.getJSONArray("response");
                    if (notesArr.length() > 0) {
                        for (int i = 0; i < notesArr.length(); i++) {
                            JSONObject notesObj = notesArr.getJSONObject(i);
                            PatientRecordsModel model = new PatientRecordsModel();
                            model.setHnAttachURL(notesObj.getString("file_url"));
                            model.setHnDesp(notesObj.getString("description"));
                            model.setHnValidTill(notesObj.getString("med_prescription_valid_till"));
                            model.setHnMedPres("" + notesObj.getInt("has_med_prescription"));
                            model.setHnTestPres("" + notesObj.getInt("has_test_prescription"));
                            model.setType(2);

                            notesModel.add(model);
                        }

                        notesCount.setText(notesArr.length() + "");
                        notesAdapter.notifyDataSetChanged();
                    } else {
                        notesListLayout.setVisibility(View.GONE);
                        notesCount.setText(notesArr.length() + "");
//                        Toast.makeText(PatientEpisodeActivity.this, getResources().getString(R.string.no_data_found), Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(String err) {
                ErrorHandlerClass.INSTANCE.errorHandler(AppointmentSaveAsPrescriptionActivity.this, err);
            }
        });
    }

    private void getEpisEvalutaion() {
        String url = ApiUrls.getEpisEvaluation + "?doctor_id=" + ApiUrls.doctorId + "&episode_id=" + episodeID + "&patient_id=" + patientID + "&sharedOrCreated=1&encounter_id=" + encounterID;
        if (encounterID != 0) {
            url = ApiUrls.getEcntEvaluation + "?doctor_id=" + ApiUrls.doctorId + "&episode_id=" + episodeID + "&patient_id=" + patientID + "&sharedOrCreated=1&encounter_id=" + encounterID;
        }
        apiCall.getRecordPref(url, "", this, new VolleyCallback() {
            @Override
            public void onSuccess(String result) {

                int totalCount = 0;
                //getting symptoms data
                try {
                    JSONObject resObj = new JSONObject(result);

                    JSONObject symptomObj = resObj.getJSONObject("response").getJSONObject("symptoms");
                    JSONArray recordsArr = symptomObj.getJSONArray("records");
                    if (recordsArr.length() > 0) {
                        for (int i = 0; i < recordsArr.length(); i++) {
                            JSONObject recObj = recordsArr.getJSONObject(i);
                            PatientRecordsModel model = new PatientRecordsModel();
                            model.setSymptomName(recObj.getString("symptom_name"));
                            model.setSymptomFirstSeen(recObj.getString("first_reported_on"));
                            model.setSymptomStatus(recObj.getString("symptom_status"));
                            model.setType(3);

                            symptomsModel.add(model);
                        }

                        totalCount += recordsArr.length();
                        symptomCount.setText(symptomObj.getString("symptoms_count"));
                        symptomAdapter.notifyDataSetChanged();
                    } else {
                        symptomCount.setText(symptomObj.getString("symptoms_count"));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                //getting investigation data
                try {
                    JSONObject resObj = new JSONObject(result);
                    JSONObject symptomObj = resObj.getJSONObject("response").getJSONObject("investigation_results");
                    JSONArray recordsArr = symptomObj.getJSONArray("records");
                    if (recordsArr.length() > 0) {
                        for (int i = 0; i < recordsArr.length(); i++) {
                            JSONObject recObj = recordsArr.getJSONObject(i);
                            PatientRecordsModel model = new PatientRecordsModel();
                            model.setInvestName(recObj.getString("investigation_name"));
                            model.setInvestParam(recObj.getString("parameter"));
                            model.setInvestValue(recObj.getString("value"));
                            model.setInvestNote(recObj.getString("notes"));
                            model.setFileUrl(recObj.getString("file_url"));
                            model.setType(4);

                            investModel.add(model);
                        }

                        totalCount += recordsArr.length();
                        investCount.setText(symptomObj.getString("investigation_results_count"));
                        investAdapter.notifyDataSetChanged();
                    } else {
                        investCount.setText(symptomObj.getString("investigation_results_count"));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                //getting eval diagnosis
                try {
                    JSONObject resObj = new JSONObject(result);
                    JSONObject symptomObj = resObj.getJSONObject("response").getJSONObject("diagnosis");
                    JSONArray recordsArr = symptomObj.getJSONArray("records");
                    if (recordsArr.length() > 0) {
                        for (int i = 0; i < recordsArr.length(); i++) {
                            JSONObject recObj = recordsArr.getJSONObject(i);
                            PatientRecordsModel model = new PatientRecordsModel();
                            model.setDiagName(recObj.getString("diagnosis"));
                            model.setDiagPoisted(recObj.getString("posited_on"));
                            model.setDiagStatus(recObj.getString("status"));
                            model.setDiagConfirmed(recObj.getString("confirmed_ruledout_on"));
                            model.setType(5);

                            diagModel.add(model);
                        }

                        totalCount += recordsArr.length();
                        diagCount.setText(symptomObj.getString("diagnosis_count"));
                        diagAdapter.notifyDataSetChanged();
                    } else {
                        diagCount.setText(symptomObj.getString("diagnosis_count"));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                //getting observation
                try {
                    JSONObject resObj = new JSONObject(result);
                    JSONObject symptomObj = resObj.getJSONObject("response").getJSONObject("observations");
                    JSONArray recordsArr = symptomObj.getJSONArray("categories");
                    if (recordsArr.length() > 0) {
                        for (int i = 0; i < recordsArr.length(); i++) {
                            JSONObject catObj = recordsArr.getJSONObject(i);
                            PatientRecordsModel model = new PatientRecordsModel();
                            model.setObsCatName(catObj.getJSONObject("record_categories").getString("category"));
                            model.setObsCount(catObj.getJSONArray("records").length());
                            model.setRecordId(catObj.getJSONObject("record_categories").getInt("id"));
                            model.setType(7);

                            obsCatModel.add(model);
                        }

                        totalCount += Integer.parseInt(symptomObj.getString("record_count"));
//                        Log.d("Obsssss ******", totalCount + "");
                        obsCount.setText(symptomObj.getString("record_count"));
                        obsAdapter.notifyDataSetChanged();
                    } else {
                        obsCount.setText(symptomObj.getString("record_count"));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                totalEvalCount.setText(totalCount + "");

            }

            @Override
            public void onError(String err) {
                ErrorHandlerClass.INSTANCE.errorHandler(AppointmentSaveAsPrescriptionActivity.this, err);
            }
        });
    }

    private void getEpisTreatPlan() {
        String url = ApiUrls.getEpisTreatmentPlan + "?doctor_id=" + ApiUrls.doctorId + "&episode_id=" + episodeID + "&patient_id=" + patientID + "&sharedOrCreated=1&encounter_id=" + encounterID;
        if (encounterID != 0) {
            url = ApiUrls.getEcntTreatmentPlan + "?doctor_id=" + ApiUrls.doctorId + "&episode_id=" + episodeID + "&patient_id=" + patientID + "&sharedOrCreated=1&encounter_id=" + encounterID;
        }
        apiCall.getRecordPref(url, "", this, new VolleyCallback() {
            @Override
            public void onSuccess(String result) {

                try {
                    JSONObject resObj = new JSONObject(result);
                    JSONArray categoryArr = resObj.getJSONObject("response").getJSONArray("categories");
                    if (categoryArr.length() > 0) {
                        for (int i = 0; i < categoryArr.length(); i++) {
                            JSONObject catObj = categoryArr.getJSONObject(i);
                            PatientRecordsModel model = new PatientRecordsModel();
                            model.setTreatPlanName(catObj.getJSONObject("record_categories").getString("category"));
                            model.setTreatPlanCount(catObj.getJSONArray("records").length());
                            model.setRecordId(catObj.getJSONObject("record_categories").getInt("id"));
                            model.setType(6);

                            treatPlanModel.add(model);

                            //share ecnt
                            CheckBox cb = new CheckBox(AppointmentSaveAsPrescriptionActivity.this);
                            cb.setText(catObj.getJSONObject("record_categories").getString("category"));
                            cb.setId(catObj.getJSONObject("record_categories").getInt("id"));
                            cb.setTag("TreatPlan");
                            cb.setOnCheckedChangeListener(AppointmentSaveAsPrescriptionActivity.this);
                            treatPlanCbList.add(cb);
                            shareTreatPlanDataList.addView(cb);
                        }

                        treatPlanCount.setText(resObj.getJSONObject("response").getString("record_count"));
                        treatPlanAdapter.notifyDataSetChanged();
                    } else {
                        treatPlanCount.setText(resObj.getJSONObject("response").getString("record_count"));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(String err) {
                ErrorHandlerClass.INSTANCE.errorHandler(AppointmentSaveAsPrescriptionActivity.this, err);
            }
        });
    }

    public void getFileFromUrl(String filrUrl) {

        JSONObject url = new JSONObject();
        try {
            url.put("url", filrUrl);
        } catch (Exception e) {
            e.printStackTrace();
        }

        apiCall.postRecords(ApiUrls.getFileFromUrl, url, this, new VolleyCallback() {
            @Override
            public void onSuccess(String result) {

                try {
                    JSONObject resObj = new JSONObject(result);
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(resObj.getString("response")));
                    startActivity(browserIntent);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(String err) {
                ErrorHandlerClass.INSTANCE.errorHandler(AppointmentSaveAsPrescriptionActivity.this, err);
            }
        });
    }

    public void goToRecordsView(int catId, String catName, String type) {
        Intent intent = new Intent(this, PatientRecordViewActivity.class);
        intent.putExtra("CategoryId", catId);
        intent.putExtra("PatientId", patientID);
        intent.putExtra("CategoryName", catName);
        intent.putExtra("SharedCreated", 1);
        intent.putExtra("EpisodeId", episodeID);
        intent.putExtra("EncounterID", encounterID);
        intent.putExtra("Type", type);
        startActivity(intent);
    }

    public void goToCreateRecords(int catId, String catName, String type) {
        if (encounterID != 0) {
            Intent intent = new Intent(this, PatientCreateRecord2Activity.class);
            intent.putExtra("CategoryId", catId + "");
            intent.putExtra("CategoryName", catName);
            intent.putExtra("PatientId", patientID);
            intent.putExtra("EpisodeId", episodeID);
            intent.putExtra("EncounterID", encounterID);
            intent.putExtra("Type", type);
//            startActivity(intent);
            startActivityForResult(intent, 1);
        } else {
            Toast.makeText(AppointmentSaveAsPrescriptionActivity.this, "Please select one interaction before adding a record", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

//        Log.d("Activity result", requestCode + "");
        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                String result = data.getStringExtra("result");
                if (result.equalsIgnoreCase("OK")) {
                    pdfModel.clear();
                    notesModel.clear();
                    investModel.clear();
                    symptomsModel.clear();
                    diagModel.clear();
                    treatPlanModel.clear();
                    obsCatModel.clear();
                    isAddNew = false;

                    pdfAdapter.notifyDataSetChanged();
                    notesAdapter.notifyDataSetChanged();
                    investAdapter.notifyDataSetChanged();
                    symptomAdapter.notifyDataSetChanged();
                    diagAdapter.notifyDataSetChanged();
                    treatPlanAdapter.notifyDataSetChanged();
                    obsAdapter.notifyDataSetChanged();

                    getEpisPDF();
                    getEpisEvalutaion();
                    getEpisWrittenNote();
                    getEpisTreatPlan();
                }
            }
            if (resultCode == Activity.RESULT_CANCELED) {
            }
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        CheckBox cb = (CheckBox) compoundButton;
//        Log.d("Checkbox", cb.getText().toString());

        if (cb.getTag().equals("Ecnt")) {
            if (!cb.getText().toString().contains("All")) {
                if (b) {
                    selectedEcntArr.add(cb.getId());
                } else {
                    selectedEcntArr.remove((Integer) cb.getId());
                }
            } else {
                if (b) {
                    selectedEcntArr.clear();
                    selectedEcntArr.addAll(ecntIdArr);
                    for (int i = 0; i < ecntCbList.size(); i++) {
                        CheckBox cb1 = ecntCbList.get(i);
                        cb1.setChecked(true);
                    }
                } else {
                    selectedEcntArr.clear();
                    for (int i = 0; i < ecntCbList.size(); i++) {
                        CheckBox cb1 = ecntCbList.get(i);
                        cb1.setChecked(false);
                    }
                }
            }
        }

        if (cb.getText().toString().equalsIgnoreCase("Evaluations")) {
            if (b) {
                shareSymptom.setChecked(true);
                shareObs.setChecked(true);
                shareInvest.setChecked(true);
                shareDiag.setChecked(true);
                allSpecificEvaluation = 1;
            } else {
                shareSymptom.setChecked(false);
                shareObs.setChecked(false);
                shareInvest.setChecked(false);
                shareDiag.setChecked(false);
                allSpecificEvaluation = 0;
            }
        }

        if (cb.getTag().equals("TreatPlan")) {
            if (cb.getText().toString().equalsIgnoreCase("Treatment Plan")) {
                if (b) {
                    allSpecificTreatmentPlan = 1;
                    selectedTreatPlanArr.clear();
                    for (int i = 0; i < treatPlanModel.size(); i++) {
//                        PatientRecordsModel model = treatPlanModel.get(i);
//                        selectedTreatPlanArr.add(model.getRecordId());

                        CheckBox cb1 = treatPlanCbList.get(i);
                        cb1.setChecked(true);
                    }
                } else {
                    allSpecificTreatmentPlan = 0;
                    selectedTreatPlanArr.clear();
                    for (int i = 0; i < treatPlanModel.size(); i++) {
                        CheckBox cb1 = treatPlanCbList.get(i);
                        cb1.setChecked(false);
                    }
                }
            } else {
                if (b) {
                    selectedTreatPlanArr.add(cb.getId());
                } else {
                    selectedTreatPlanArr.remove((Integer) cb.getId());
                }
            }
        }
    }

    private void shareEpisData(JSONObject shareObj) {
        loadingDialog = new ProgressDialog(this);
        loadingDialog.setMessage(getResources().getString(R.string.process_request));
        loadingDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        loadingDialog.setCancelable(false);
        loadingDialog.show();

        apiCall.postRecords(ApiUrls.generateRecordPdf, shareObj, this, new VolleyCallback() {
            @Override
            public void onSuccess(String result) {
                loadingDialog.dismiss();
                try {
                    JSONObject respObj = new JSONObject(result);
                    JSONObject rootObj = respObj.getJSONObject("response");
//                            String url = "";
//                            //if (type == 1) {
                    receiptUrl = rootObj.getString("url");

                    JSONObject urlObject = new JSONObject();
                    urlObject.put("url", receiptUrl);
                    //  getReceiptUrl("https://arthtech.s3.ap-south-1.amazonaws.com/"+receiptUrl);
                    getPresignedUrlData(urlObject);

                    if(flagGenerateAndShared==1)
                    {
                        flagGenerateAndShared=0;
                        generateAndShareNow();
                    }


                    //  Toast.makeText(AppointmentSaveAsPrescriptionActivity.this, respObj.getString("response"), Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(AppointmentSaveAsPrescriptionActivity.this, "Error Occurred. Try again later", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(String err) {
                loadingDialog.dismiss();
                ErrorHandlerClass.INSTANCE.errorHandler(AppointmentSaveAsPrescriptionActivity.this, err);
            }
        });
    }


    private void getPresignedUrlData(JSONObject shareObj) {
//        loadingDialog = new ProgressDialog(this);
//        loadingDialog.setMessage(getResources().getString(R.string.process_request));
//        loadingDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
//        loadingDialog.setCancelable(false);
//        loadingDialog.show();

        apiCall.postRecords(ApiUrls.getPreSignedUrl, shareObj, this, new VolleyCallback() {
            @Override
            public void onSuccess(String result) {
//                loadingDialog.dismiss();
                try {
                    JSONObject respObj = new JSONObject(result);
                    // JSONObject rootObj = respObj.getJSONObject("response");
//                            String url = "";
//                            //if (type == 1) {
                    String receiptUrlOne = respObj.getString("response");
                    getReceiptUrl(receiptUrlOne);


                    //  Toast.makeText(AppointmentSaveAsPrescriptionActivity.this, respObj.getString("response"), Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(AppointmentSaveAsPrescriptionActivity.this, "Error Occurred. Try again later", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(String err) {
//                loadingDialog.dismiss();
                ErrorHandlerClass.INSTANCE.errorHandler(AppointmentSaveAsPrescriptionActivity.this, err);
            }
        });
    }


    private void getEpisodicPref() {
        //writtenNoteLayout.setVisibility(View.GONE);
        evaluationLayout.setVisibility(View.GONE);
        treatPlanLayout.setVisibility(View.GONE);
        apiCall.getRecordPref(ApiUrls.getEpisodicFieldPref, "", this, new VolleyCallback() {
            @Override
            public void onSuccess(String result) {
                try {
                    JSONObject resObj = new JSONObject(result);
                    JSONArray prefArr = resObj.getJSONArray("response");
//                    if (prefArr.getJSONObject(0).getInt("status") == 1) {
//                        writtenNoteLayout.setVisibility(View.VISIBLE);
//                    }

//                    shareEcntNotes = findViewById(R.id.shareEcntNotes);
//                    evaluationView = findViewById(R.id.evaluationView);
//                    treatmentPlanView = findViewById(R.id.treatmentPlanView);
//                    shareEcntEval = findViewById(R.id.shareEcntEval);
//                    shareEcntTreatPlan = findViewById(R.id.shareEcntTreatPlan);

                    if (prefArr.getJSONObject(1).getInt("status") == 1 || (prefArr.getJSONObject(2).getInt("status") == 1)) {
                        shareEcntNotes.setVisibility(View.VISIBLE);
                        generateLayout.setVisibility(View.VISIBLE);
                        headerField.setVisibility(View.VISIBLE);
                        shareEcntNotesGoToSetting.setVisibility(View.GONE);
                    } else {
                        shareEcntNotes.setVisibility(View.GONE);
                        generateLayout.setVisibility(View.GONE);
                        headerField.setVisibility(View.GONE);
                        shareEcntNotesGoToSetting.setVisibility(View.VISIBLE);
                    }


                    if (prefArr.getJSONObject(1).getInt("status") == 1) {
                        //evaluationLayout.setVisibility(View.VISIBLE);
                        shareEcntEval.setVisibility(View.VISIBLE);
                        evaluationView.setVisibility(View.VISIBLE);
                    } else {
                        shareEcntEval.setVisibility(View.GONE);
                        evaluationView.setVisibility(View.GONE);


                    }

                    if (prefArr.getJSONObject(2).getInt("status") == 1) {
                        //treatPlanLayout.setVisibility(View.VISIBLE);
                        shareEcntTreatPlan.setVisibility(View.VISIBLE);
                        treatmentPlanView.setVisibility(View.VISIBLE);
                        shareEcntTreatPlan.setChecked(true);
                    } else {
                        shareEcntTreatPlan.setVisibility(View.GONE);
                        treatmentPlanView.setVisibility(View.GONE);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(String err) {
                ErrorHandlerClass.INSTANCE.errorHandler(AppointmentSaveAsPrescriptionActivity.this, err);
            }
        });
    }


    private void getEpisodicEvaluationPref() {
//        writtenNoteLayout.setVisibility(View.GONE);
//        evaluationLayout.setVisibility(View.GONE);
//        treatPlanLayout.setVisibility(View.GONE);
        String url = ApiUrls.getEvaluationFieldPreferences + "?doctor_id=" + ApiUrls.doctorId + "&episode_id=" + episodeID + "&patient_id=" + patientID + "&sharedOrCreated=1&encounter_id=" + encounterID;
        if (encounterID != 0) {
            url = ApiUrls.getEvaluationFieldPreferences + "?doctor_id=" + ApiUrls.doctorId + "&episode_id=" + episodeID + "&patient_id=" + patientID + "&sharedOrCreated=1&encounter_id=" + encounterID;
        }

        apiCall.getRecordPref(url, "", this, new VolleyCallback() {
            @Override
            public void onSuccess(String result) {
                try {
                    JSONObject resObj = new JSONObject(result);
                    JSONArray prefArr = resObj.getJSONArray("response");


                    if (shareEcntEval.getVisibility() == View.VISIBLE) {

                        shareEcntEval.setChecked(true);
                        shareSymptom.setVisibility(View.VISIBLE);
                        shareObs.setVisibility(View.VISIBLE);
                        shareInvest.setVisibility(View.VISIBLE);
                        shareDiag.setVisibility(View.VISIBLE);


//                        if (prefArr.getJSONObject(0).getInt("status") == 1) {
//                            shareSymptom.setVisibility(View.VISIBLE);
//                        } else {
//                            shareSymptom.setVisibility(View.GONE);
//                        }
//
//                        if (prefArr.getJSONObject(1).getInt("status") == 1) {
//                            shareObs.setVisibility(View.VISIBLE);
//                        } else {
//                            shareObs.setVisibility(View.GONE);
//
//                        }
//
//                        if (prefArr.getJSONObject(2).getInt("status") == 1) {
//                            shareInvest.setVisibility(View.VISIBLE);
//                        } else {
//                            shareInvest.setVisibility(View.GONE);
//
//                        }
//
//                        if (prefArr.getJSONObject(3).getInt("status") == 1) {
//                            shareDiag.setVisibility(View.VISIBLE);
//                        } else {
//                            shareDiag.setVisibility(View.GONE);
//
//                        }
                    } else {
                        shareSymptom.setVisibility(View.GONE);
                        shareObs.setVisibility(View.GONE);
                        shareInvest.setVisibility(View.GONE);
                        shareDiag.setVisibility(View.GONE);

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(String err) {
                ErrorHandlerClass.INSTANCE.errorHandler(AppointmentSaveAsPrescriptionActivity.this, err);
            }
        });
    }


    private void getReceiptUrl(String url) {


        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        getApplicationContext().startActivity(i);
    }


    private void generateAndShareNow() {
        try {
            if (selectedEcntArrTwo.size() > 0) {
                JSONObject shareObj = new JSONObject();
                shareObj.put("patient_id", patientID);
                shareObj.put("doctor_id", ApiUrls.doctorId);
                shareObj.put("encounters", new JSONArray(selectedEcntArrTwo));
                shareObj.put("generated_pdfs", true);
                shareObj.put("written_notes", false);
                shareObj.put("evaluations", shareEval.isChecked());
                shareObj.put("selectedTreatmentPlans", new JSONArray(selectedTreatPlanArr));
                shareObj.put("symptoms", shareSymptom.isChecked());
                shareObj.put("observations", shareObs.isChecked());
                shareObj.put("investigation_results", shareInvest.isChecked());
                shareObj.put("diagnosis", shareDiag.isChecked());
//                    shareObj.put("diagnosis", )
                shareObj.put("treatment_plan", shareTreatPlan.isChecked());
//                shareObj.put("allSpecificEvaluation", allSpecificEvaluation);
//                shareObj.put("allSpecificTreatmentPlan", allSpecificTreatmentPlan);
                shareObj.put("allSpecificEvaluation", 0);
                shareObj.put("allSpecificTreatmentPlan", 0);

                shareObj.put("anyOneEvaluation", false);
                if (shareSymptom.isChecked() || shareObs.isChecked() || shareInvest.isChecked()) {
                    shareObj.put("anyOneEvaluation", true);
                }

                shareObj.put("anyOneTreatmentPlan", false);
                if (selectedTreatPlanArr.size() > 0) {
                    shareObj.put("anyOneTreatmentPlan", true);
                }

//                Log.d("Share Data", shareObj.toString());
                shareAndGenerate(shareObj);
            } else {
                Toast.makeText(AppointmentSaveAsPrescriptionActivity.this, "Please select one interaction", Toast.LENGTH_SHORT).show();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void shareAndGenerate(JSONObject shareObj) {
//        loadingDialog = new ProgressDialog(this);
//        loadingDialog.setMessage(getResources().getString(R.string.process_request));
//        loadingDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
//        loadingDialog.setCancelable(false);
//        loadingDialog.show();

        apiCall.postRecords(ApiUrls.shareEpisodeData, shareObj, this, new VolleyCallback() {
            @Override
            public void onSuccess(String result) {
               // loadingDialog.dismiss();
                try {
                    JSONObject respObj = new JSONObject(result);
                    Toast.makeText(AppointmentSaveAsPrescriptionActivity.this, respObj.getString("response"), Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(AppointmentSaveAsPrescriptionActivity.this, "Error Occurred. Try again later", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(String err) {
               // loadingDialog.dismiss();
                ErrorHandlerClass.INSTANCE.errorHandler(AppointmentSaveAsPrescriptionActivity.this, err);
            }
        });
    }


}
