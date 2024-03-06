package com.whitecoats.clinicplus;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.google.android.gms.common.api.Api;
import com.whitecoats.adapter.PatientEncounterHistoryAdapter;
import com.whitecoats.adapter.PatientEncounterNoteAdapter;
import com.whitecoats.adapter.PatientRecordsAboutAdapter;
import com.whitecoats.adapter.PatientRecordsCategoryAdapter;
import com.whitecoats.clinicplus.apis.ApiUrls;
import com.whitecoats.clinicplus.utils.ErrorHandlerClass;
import com.whitecoats.model.PatientRecordsModel;
import com.zoho.salesiqembed.ZohoSalesIQ;

import co.mobiwise.materialintro.animation.MaterialIntroListener;
import co.mobiwise.materialintro.shape.Focus;
import co.mobiwise.materialintro.shape.FocusGravity;
import co.mobiwise.materialintro.shape.ShapeType;
import co.mobiwise.materialintro.view.MaterialIntroView;

public class PatientRecordsCaseActivity extends AppCompatActivity {

    private RecyclerView aboutRecycleView, familyRecycleView, treatRv, symptomRv, symptomRvH, investRv, investRvH, diagRv;
    private List<PatientRecordsModel> patientRecordsModels, familyModels, handNoteMoodels,
            symptomsModel, investModel, diagModel, symptomsModelH, investModelH, diagModelH;
    private PatientRecordsAboutAdapter aboutAdapter, familyAdapter;
    //    private PatientRecordsCategoryAdapter handNoteAdapter, symptomsAdapter, investAdapter, diagAdapter;
    private PatientRecordsCategoryAdapter handNoteAdapter, symptomsAdapter;
    private CardView familyCard, patientCard;
    private RelativeLayout familyMoreData, evalMoreData, sectionProceed;
    private int sectionNum = 1;
    private View aboutLayout, caseLayout, caseSelectLayout, caseHistoryLayout, notesLayout;
    private Uri fileUri;
    private String imageFilePath;
    private int newInteractionFlagId = 0;
    private int selectCaseSpinner = 0;
    private int interactionSelectedPosition;
    private int flagProceedClick = 0;
    private int caseTypeClick = 0;
    private SharedPreferences sharedPreferences;

    public static int dropDownShowFlag = 0;

    //patient info section
    private LinearLayout patientInfoLayout;
    private EditText pName, pPhNo, pEmail, pAge, pHeight, pId;
    private Spinner pBloodG;
    private TextView pDOB;
    private RadioGroup pGender;
    String[] bloodType = {"A+", "A-", "B+", "B-", "O+", "O-", "AB+", "AB-"};
    private int mYear, mMonth, mDay, mHour, mMinute;
    private TextView familyEmptyText;
    private ImageView patientArrow, familyArrow;

    //notes section
    private CardView notesHandNoteCard, notesEvalCard, notesEvalCardHistory, notesTreatCard;
    private RecyclerView notesHandNoteRv, notesTreatRv, notesSymptomRv, notesInvestRv, notesDiagRv;
    private RelativeLayout notesEvalMoreData, notesEvalMoreDataHistory, notesHnUploadFile, handWrittenNotes, notesPrefrenceEmpty;
    private TextView uploadNotes, notesHnValidTillLabel;
    private LinearLayout notesMainLayout, notesFormLayout;
    private Spinner notesHnMedPres, notesHnTestPres;
    private TextView notesHnValid, notesHnCount;
    private Button notesHnSave, notesHnBack;
    private ImageView notesHnFormImage, notesHnArrow;
    private File pdfFile;
    private EditText notesHnDesp;
    String[] yesNo = {"No", "Yes"};
    public static int encounterId = 0;
    private List<PatientRecordsModel> notesHnNoteModels;
    private PatientRecordsCategoryAdapter notesHnNotesAdapter;
    private RelativeLayout noteLayout;


    //evaluation
    private RelativeLayout evaluationLayout, evalDataLayout, symptomLayout, obsLayout, investLayout, diagLayout, obsMainLayout;
    private LinearLayout symptomListLayout, obsListLayout, investListLayout, diagListLayout;
    private RecyclerView obsRv, diagListRv;
    //    private RecyclerView symptomRv, obsRv, investRv, diagListRv;
    private TextView symptomCount, obsCount, investCount, diagCount, totalEvalCount;
    //    private List<PatientRecordsModel> symptomsModel, investModel, diagModel, obsCatModel;
//    private PatientEncounterAdapter symptomAdapter, investAdapter, diagAdapter, obsAdapter;
    private List<PatientRecordsModel> obsCatModel;
    private PatientEncounterNoteAdapter symptomAdapter, obsAdapter, investAdapter, diagAdapter;

    private TextView symptomAddNew, investAddNew, diagAddNew, evalAddNew, obsAddNew;
    private ImageView recordEpisEvalArrow, recordEvalObsArrow,recordEpisEvalArrowHistory, recordEvalObsArrowHistory;
    private ProgressBar progressbarEvaluation;


    //evaluation History
    private RelativeLayout evaluationLayoutH, evalDataLayoutH, symptomLayoutH, obsLayoutH, investLayoutH, diagLayoutH, obsMainLayoutH;
    private LinearLayout symptomListLayoutH, obsListLayoutH, investListLayoutH, diagListLayoutH;
    private RecyclerView obsRvH, diagListRvH;
    //    private RecyclerView symptomRv, obsRv, investRv, diagListRv;
    private TextView symptomCountH, obsCountH, investCountH, diagCountH, totalEvalCountH;
    //    private List<PatientRecordsModel> symptomsModel, investModel, diagModel, obsCatModel;
//    private PatientEncounterAdapter symptomAdapter, investAdapter, diagAdapter, obsAdapter;
    private List<PatientRecordsModel> obsCatModelH;
    private PatientEncounterHistoryAdapter symptomAdapterH, obsAdapterH, investAdapterH, diagAdapterH;

    private TextView symptomAddNewH, investAddNewH, diagAddNewH, evalAddNewH, obsAddNewH;
    private ImageView recordEpisEvalArrowH, recordEvalObsArrowH;
    private ProgressBar progressbarEvaluationH;


    //treat plan
    private CardView treatPlanLayout;
    private RelativeLayout treatPlanParentLayout;
    private RecyclerView treatPlanCatRv;
    private List<PatientRecordsModel> treatPlanModel;
    private PatientEncounterNoteAdapter treatPlanAdapter;
    private TextView treatPlanCount, treatPlanAddNew;
    private ImageView recordEpisTreatmentArrow,recordEpisTreatmentArrowHistory;
    private ProgressBar progressbarTreatment;

    private ArrayList<CheckBox> ecntCbList, treatPlanCbList;


    //treat plan hostory
    private CardView treatPlanLayoutH;
    private RelativeLayout treatPlanParentLayoutH;
    private RecyclerView treatPlanCatRvH;
    private List<PatientRecordsModel> treatPlanModelH;
    private PatientEncounterHistoryAdapter treatPlanAdapterH;
    private TextView treatPlanCountH, treatPlanAddNewH;
    //    private ImageView recordEpisTreatmentArrow;
    private ProgressBar progressbarTreatmentH;

    private ArrayList<CheckBox> ecntCbListH, treatPlanCbListH;


    //create case section
    private RelativeLayout caseDateLayout, caseTimeLayout, caseProceed;
    private EditText caseName;
    private TextView caseDate, caseTime;
    private Spinner caseType;
    String[] interactionMode = {"Interaction Mode", "Video", "Chat", "Clinic", "Phone", "Home", "Other"};

    //case select section
    private TextView createNewCase, noOfInteraction, selectCaseDiag, caseLastInteraction, selectCaseDate, selectCaseTime;
    private Spinner selectCase, caseInteraction;
    private RelativeLayout selectCaseDateLayout, selectCaseTimeLayout;
    private JSONArray episodesArr;
    private int episodeID;
    private boolean interactionChanged = false;
    private boolean interactionChangeTwo = false;
    private PatientRecordsApi apiCall;
    private LinearLayout caseInfoGuide, interactionGuide, createCaseInteractGuide;


    //header section
    private RelativeLayout headerAboutLayout, headerCaseLayout, headerHistLayout, headerNotesLayout;
    private TextView headAbout, headCase, headHist, headNotes;

    //history section
    private Spinner histSelectCase;
    private CardView histHandNoteCard, histEvalCard, histTreatCard;
    private RecyclerView histHandNoteRv;
    private List<PatientRecordsModel> histHandNoteModels;
    private PatientRecordsCategoryAdapter histHandNoteAdapter;
    private ImageView histHandNoteArrow;
    private TextView histHandNoteEmpty, histHnCount;
    private List<String> ecntListArr;
    private List<Integer> ecntIdArr;
    private ArrayAdapter<String> ecntAdapter;

    private PatientRecordsApi patientRecordsApi;
    private int docId, patientId, apptId, apptMode = 0;
    private AppUtilities appUtilities;
    private ProgressDialog loadingDialog;
    private boolean isPDFFile = false;
    private static final int REQUEST_PERMISSION_SETTING = 101;
    private String[] permissionsRequired = new String[]{android.Manifest.permission.CAMERA};
    private static final int PERMISSION_CALLBACK_CONSTANT = 100;
    private String uploadImageResponse;
    private int encounterID;
    private int caseInteractionChangeClick = 0;
    private RelativeLayout appointmentSaveAsPrescription, appointmentSharedOption, recodShareCancelOption;
    private Button savePatientData;
    private int genderId = 0;
    private int updatePatientId = 0;
    private RecyclerView.LayoutManager mLayoutManager;


    private TextView recordEpisEvalCountHistory;
    private ProgressBar progressbarEvaluationHistory;

    public static int historyTabClick = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pateint_records_case);

        ZohoSalesIQ.Tracking.setCustomAction("AppointmentNotes Page");

        //RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        aboutRecycleView = findViewById(R.id.apptRecCaseAboutRecycleView);
        familyRecycleView = findViewById(R.id.apptRecCaseFamilyRecycleView);
        familyCard = findViewById(R.id.apptRecCaseFamilyInfo);
        patientArrow = findViewById(R.id.apptAboutPatientArrow);
        familyArrow = findViewById(R.id.apptAboutFamilyArrow);
        familyEmptyText = findViewById(R.id.apptRecordFamilyEmptyText);
        familyMoreData = findViewById(R.id.familyMoreInfo);
        histHandNoteCard = findViewById(R.id.caseHistoryHandNotesCard);
        histEvalCard = findViewById(R.id.caseHistoryEvalCard);
        histTreatCard = findViewById(R.id.caseHistoryTreatmentCard);
        histHandNoteRv = findViewById(R.id.caseHistoryHandNoteRv);
        histHandNoteArrow = findViewById(R.id.apptRecHistNotesArrow2);
        histHandNoteEmpty = findViewById(R.id.caseHistHandNoteEmptyText);
        appointmentSaveAsPrescription = findViewById(R.id.apptSaveAsPrescriptionOption);
        appointmentSharedOption = findViewById(R.id.appointmentSharedOption);
        recodShareCancelOption = findViewById(R.id.recodShareCancelOption);
        savePatientData = findViewById(R.id.patientProPatientInfoSave);
        //noteLayout = findViewById(R.id.noteLayout);
        histHnCount = findViewById(R.id.apptHistHnCount);
        symptomRv = findViewById(R.id.recordEvalSymptomRv);
        investRv = findViewById(R.id.recordEvalInvestRv);
        diagRv = findViewById(R.id.recordEvalDiagRv);
        treatRv = findViewById(R.id.caseHistoryTreatRv);
        evalMoreData = findViewById(R.id.caseHistoryEvalMore);
        notesHandNoteCard = findViewById(R.id.recordsNotesHandNotesCard);
        notesEvalCard = findViewById(R.id.recordsNotesEvalCard);
        notesEvalCardHistory = findViewById(R.id.recordsNotesEvalCardHistory);
        notesTreatCard = findViewById(R.id.recordsNotesTreatmentCard);
        notesHandNoteRv = findViewById(R.id.recordsNotesHandNoteRv);
        //notesTreatRv = findViewById(R.id.recordsNotesTreatRv);
        notesSymptomRv = findViewById(R.id.recordEvalSymptomRv);
        notesInvestRv = findViewById(R.id.recordEvalInvestRv);
        notesDiagRv = findViewById(R.id.recordEvalDiagRv);
        notesHnCount = findViewById(R.id.apptNotesHnCount);
        notesEvalMoreData = findViewById(R.id.recordsNotesEvalMore);
        notesEvalMoreDataHistory = findViewById(R.id.recordsNotesEvalMoreHistory);
        patientInfoLayout = findViewById(R.id.apptRecCasePatientInfoForm);
        patientCard = findViewById(R.id.apptRecCasePatientInfo);
        pName = findViewById(R.id.recordsPName);
        pPhNo = findViewById(R.id.recordsPPhNo);
        pEmail = findViewById(R.id.recordsPEmail);
        pDOB = findViewById(R.id.recordsPDOB);
        pId = findViewById(R.id.recordsPID);
        pHeight = findViewById(R.id.recordsPHeight);
        pAge = findViewById(R.id.recordsPAge);
        pBloodG = findViewById(R.id.recordsPBloodG);
        pGender = findViewById(R.id.recordsPGender);
        caseDateLayout = findViewById(R.id.recordsCaseDateLayout);
        caseTimeLayout = findViewById(R.id.recordsCaseTimeLayout);
        caseDate = findViewById(R.id.recordsCaseDate);
        caseName = findViewById(R.id.recordsCaseName);
        caseTime = findViewById(R.id.recordsCaseTime);
        caseType = findViewById(R.id.recordsCaseInteractionType);
        caseProceed = findViewById(R.id.recordsCaseProceed);
        sectionProceed = findViewById(R.id.recordsSectionProceed);
        aboutLayout = findViewById(R.id.recordsAboutPatients);
        caseLayout = findViewById(R.id.recordsCaseCreate);
        caseSelectLayout = findViewById(R.id.recordsCaseSelect);
        selectCase = findViewById(R.id.recordsSelectCaseDrop);
        createNewCase = findViewById(R.id.recordsSelectCaseNew);
        noOfInteraction = findViewById(R.id.recordsSelectCaseNOI);
        selectCaseDiag = findViewById(R.id.recordsSelectCaseDiag);
        caseLastInteraction = findViewById(R.id.recordsSelectCaseLastInt);
        caseInteraction = findViewById(R.id.recordsSelectCaseInteractionMode);
        selectCaseDateLayout = findViewById(R.id.recordsSelectCaseDateLayout);
        selectCaseTimeLayout = findViewById(R.id.recordsSelectCaseTimeLayout);
        selectCaseTime = findViewById(R.id.recordsSelectCaseTime);
        selectCaseDate = findViewById(R.id.recordsSelectCaseDate);
        caseHistoryLayout = findViewById(R.id.recordsCaseHistory);
        headerAboutLayout = findViewById(R.id.recordsProgressAboutLayout);
        headerCaseLayout = findViewById(R.id.recordsProgressCaseLayout);
        headerHistLayout = findViewById(R.id.recordsProgressHistoryLayout);
        headerNotesLayout = findViewById(R.id.recordsProgressNotesLayout);
        headAbout = findViewById(R.id.recordsProgAboutText);
        headCase = findViewById(R.id.recordsProgressCaseText);
        headHist = findViewById(R.id.recordsProgressHistoryText);
        headNotes = findViewById(R.id.recordsProgressNotesText);
        notesLayout = findViewById(R.id.recordsNotes);
        //handWrittenNotes = findViewById(R.id.handWrittenNotes);
        notesPrefrenceEmpty = findViewById(R.id.prefNotesEmpty);
        uploadNotes = findViewById(R.id.recordsUploadNotes);
        notesMainLayout = findViewById(R.id.recordsNotesMainLayout);
        notesFormLayout = findViewById(R.id.recordsNotesFormLayout);
        notesHnSave = findViewById(R.id.notesHnSaveData);
        notesHnFormImage = findViewById(R.id.notesHnImage);
        notesHnUploadFile = findViewById(R.id.notesHnUploadFile);
        notesHnDesp = findViewById(R.id.notesHnDesp);
        notesHnValid = findViewById(R.id.notesHnValidTill);
        notesHnMedPres = findViewById(R.id.notesHnMedPres);
        notesHnTestPres = findViewById(R.id.notesHnTestPres);
        notesHnValidTillLabel = findViewById(R.id.notesHnValidTillLabel);
        notesHnArrow = findViewById(R.id.apptConsultNotesArrow2);
        notesHnBack = findViewById(R.id.notesHnBack);
        patientRecordsModels = new ArrayList<>();
        familyModels = new ArrayList<>();
        handNoteMoodels = new ArrayList<>();
        symptomsModel = new ArrayList<>();
        diagModel = new ArrayList<>();
        investModel = new ArrayList<>();

        symptomsModelH = new ArrayList<>();
        diagModelH = new ArrayList<>();
        investModelH = new ArrayList<>();

        histHandNoteModels = new ArrayList<>();
        notesHnNoteModels = new ArrayList<>();
        treatPlanCbList = new ArrayList<>();
        histSelectCase = findViewById(R.id.recordsHistCaseDrop);
        aboutAdapter = new PatientRecordsAboutAdapter(patientRecordsModels, this);
        familyAdapter = new PatientRecordsAboutAdapter(familyModels, this);
        handNoteAdapter = new PatientRecordsCategoryAdapter(handNoteMoodels, this);
        symptomsAdapter = new PatientRecordsCategoryAdapter(symptomsModel, this);
        // investAdapter = new PatientRecordsCategoryAdapter(investModel, this);
        //diagAdapter = new PatientRecordsCategoryAdapter(diagModel, this);
        diagAdapter = new PatientEncounterNoteAdapter(diagModel, this);
        histHandNoteAdapter = new PatientRecordsCategoryAdapter(histHandNoteModels, this);
        notesHnNotesAdapter = new PatientRecordsCategoryAdapter(notesHnNoteModels, this);
        patientRecordsApi = new PatientRecordsApi();
        appUtilities = new AppUtilities();
        ecntIdArr = new ArrayList<>();
        ecntListArr = new ArrayList<>();
        apiCall = new PatientRecordsApi();
        caseInfoGuide = findViewById(R.id.recordsSelectCaseInfoGuide);
        interactionGuide = findViewById(R.id.recordsSelectCaseInteractionGuide);
        createCaseInteractGuide = findViewById(R.id.recordsCaseCreateInteractionGuide);
        sharedPreferences = getApplicationContext().getSharedPreferences(ApiUrls.appSharedPref, 0);


        recordEpisEvalCountHistory = findViewById(R.id.recordEpisEvalCountHistory);
        progressbarEvaluationHistory = findViewById(R.id.progressbarEvaluationHistory);


        //eval
        // RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
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
        progressbarEvaluation = findViewById(R.id.progressbarEvaluation);
        obsMainLayout = findViewById(R.id.recordEpisObservationMainLayout);
        obsLayout = findViewById(R.id.recordEvalObsLayout);
        obsRv = findViewById(R.id.recordEpisObservationRecyclerView);


        symptomAddNew = findViewById(R.id.recordEvalSymptomAddNew);
        investAddNew = findViewById(R.id.recordEvalInvestAddNew);
        diagAddNew = findViewById(R.id.recordEvalDiagnosAddNew);
        evalAddNew = findViewById(R.id.recordEpisEvalAddNew);
        obsAddNew = findViewById(R.id.recordEvalObsAddNew);
        recordEpisEvalArrow = findViewById(R.id.recordEpisEvalArrow);
        recordEvalObsArrow = findViewById(R.id.recordEvalObsArrow);
        recordEpisEvalArrowHistory = findViewById(R.id.recordEpisEvalArrowHistory);
        recordEvalObsArrowHistory = findViewById(R.id.recordEvalObsArrowHistory);
        symptomsModel = new ArrayList<>();
        investModel = new ArrayList<>();
        diagModel = new ArrayList<>();
        obsCatModel = new ArrayList<>();
        obsCatModelH = new ArrayList<>();
        symptomAdapter = new PatientEncounterNoteAdapter(symptomsModel, this);
        //       investAdapter = new PatientRecordsCategoryAdapter(investModel, this);
        investAdapter = new PatientEncounterNoteAdapter(investModel, this);
        diagAdapter = new PatientEncounterNoteAdapter(diagModel, this);
        obsAdapter = new PatientEncounterNoteAdapter(obsCatModel, this);


        // RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
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


        //eval historyTab
        // RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        evaluationLayoutH = findViewById(R.id.recordEpisEvalLayout);
        evalDataLayoutH = findViewById(R.id.recordEpisEvaluationLayout);
        symptomLayoutH = findViewById(R.id.recordEvalSymptomLayoutHistory);
        obsLayoutH = findViewById(R.id.recordEvalObsLayoutHistory);
        investLayoutH = findViewById(R.id.recordEvalInvestLayoutHistory);
        diagLayoutH = findViewById(R.id.recordEvalDiagnosLayoutHistory);
        symptomListLayoutH = findViewById(R.id.recordSymptomListLayoutHistory);
        investListLayoutH = findViewById(R.id.recordInvestListLayoutHistory);
        diagListLayoutH = findViewById(R.id.recordDiagnosListLayoutHistory);
        symptomRvH = findViewById(R.id.recordSymptomRecyclerViewHistory);
        investRvH = findViewById(R.id.recordInvestRecyclerViewHistory);
        diagListRvH = findViewById(R.id.recordDiagnosRecyclerViewHistory);
        symptomCountH = findViewById(R.id.recordEvalSymptomCountHistory);
        obsCountH = findViewById(R.id.recordEvalObsCountHistory);
        investCountH = findViewById(R.id.recordEvalInvestCountHistory);
        diagCountH = findViewById(R.id.recordEvalDiagnosCountHistory);
        totalEvalCountH = findViewById(R.id.recordEpisEvalCountHistory);
        progressbarEvaluationH = findViewById(R.id.progressbarEvaluationHistory);
        obsMainLayoutH = findViewById(R.id.recordEpisObservationMainLayoutHistory);
        obsLayoutH = findViewById(R.id.recordEvalObsLayoutHistory);
        obsRvH = findViewById(R.id.recordEpisObservationRecyclerViewHistory);
        symptomAddNewH = findViewById(R.id.recordEvalSymptomAddNewHistory);
        investAddNewH = findViewById(R.id.recordEvalInvestAddNewHistory);
        diagAddNewH = findViewById(R.id.recordEvalDiagnosAddNewHistory);
        evalAddNewH = findViewById(R.id.recordEpisEvalAddNew);
        obsAddNewH = findViewById(R.id.recordEvalObsAddNewHistory);
        recordEpisEvalArrow = findViewById(R.id.recordEpisEvalArrow);
        recordEvalObsArrow = findViewById(R.id.recordEvalObsArrow);
        symptomsModelH = new ArrayList<>();
        investModelH = new ArrayList<>();
        diagModelH = new ArrayList<>();
        obsCatModelH = new ArrayList<>();
        symptomAdapterH = new PatientEncounterHistoryAdapter(symptomsModelH, this);
        //       investAdapter = new PatientRecordsCategoryAdapter(investModel, this);
        investAdapterH = new PatientEncounterHistoryAdapter(investModelH, this);
        diagAdapterH = new PatientEncounterHistoryAdapter(diagModelH, this);
        obsAdapterH = new PatientEncounterHistoryAdapter(obsCatModelH, this);

        mLayoutManager = new LinearLayoutManager(getApplicationContext());
        symptomRvH.setLayoutManager(mLayoutManager);
        symptomRvH.setItemAnimator(new DefaultItemAnimator());
        symptomRvH.setAdapter(symptomAdapterH);
        mLayoutManager = new LinearLayoutManager(getApplicationContext());
        investRvH.setLayoutManager(mLayoutManager);
        investRvH.setItemAnimator(new DefaultItemAnimator());
        investRvH.setAdapter(investAdapterH);
        mLayoutManager = new LinearLayoutManager(getApplicationContext());
        diagListRvH.setLayoutManager(mLayoutManager);
        diagListRvH.setItemAnimator(new DefaultItemAnimator());
        diagListRvH.setAdapter(diagAdapterH);
        mLayoutManager = new LinearLayoutManager(getApplicationContext());
        obsRvH.setLayoutManager(mLayoutManager);
        obsRvH.setItemAnimator(new DefaultItemAnimator());
        obsRvH.setAdapter(obsAdapterH);


        //--------------------end---------------------


        // RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
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
        treatPlanLayout = findViewById(R.id.recordsNotesTreatmentCard);
        treatPlanParentLayout = findViewById(R.id.recordEpisTreatmentMainLayout);
        treatPlanCatRv = findViewById(R.id.recordEpisTreatmentRecyclerView);
        treatPlanModel = new ArrayList<>();
        treatPlanAdapter = new PatientEncounterNoteAdapter(treatPlanModel, this);
        treatPlanCount = findViewById(R.id.recordEpisTreatmentCount);
        progressbarTreatment = findViewById(R.id.progressbarTreatment);
        mLayoutManager = new LinearLayoutManager(getApplicationContext());
        treatPlanCatRv.setLayoutManager(mLayoutManager);
        treatPlanCatRv.setItemAnimator(new DefaultItemAnimator());
        treatPlanCatRv.setAdapter(treatPlanAdapter);
        treatPlanAddNew = findViewById(R.id.recordEpisTreatmentAddNew);
        recordEpisTreatmentArrow = findViewById(R.id.recordEpisTreatmentArrow);
        recordEpisTreatmentArrowHistory = findViewById(R.id.recordEpisTreatmentArrowHistory);


        //treat plan history
        treatPlanLayoutH = findViewById(R.id.recordsNotesTreatmentCardHistory);
        treatPlanParentLayoutH = findViewById(R.id.recordEpisTreatmentMainLayoutHistory);
        treatPlanCatRvH = findViewById(R.id.recordEpisTreatmentRecyclerViewHistory);
        treatPlanModelH = new ArrayList<>();
        treatPlanAdapterH = new PatientEncounterHistoryAdapter(treatPlanModelH, this);
        treatPlanCountH = findViewById(R.id.recordEpisTreatmentCountHistory);
        progressbarTreatmentH = findViewById(R.id.progressbarTreatmentHistory);
        mLayoutManager = new LinearLayoutManager(getApplicationContext());
        treatPlanCatRvH.setLayoutManager(mLayoutManager);
        treatPlanCatRvH.setItemAnimator(new DefaultItemAnimator());
        treatPlanCatRvH.setAdapter(treatPlanAdapterH);
        treatPlanAddNewH = findViewById(R.id.recordEpisTreatmentAddNewHistory);
        //recordEpisTreatmentArrow = findViewById(R.id.recordEpisTreatmentArrow);


        apptId = getIntent().getIntExtra("ApptId", 0);
        patientId = getIntent().getIntExtra("PatientId", 0);
        docId = ApiUrls.doctorId;
        apptMode = getIntent().getIntExtra("ApptMode", 0);
//        Log.d("AppId", apptId + "");
//        Log.d("Patient", patientId + "");
//        Log.d("Docotor", docId + "");
//        Log.d("Mode", getIntent().getIntExtra("ApptMode", 0) + "");
//        Log.d("Date", getIntent().getStringExtra("ApptDate"));
//        Log.d("Time", getIntent().getStringExtra("ApptTime"));

        mLayoutManager = new LinearLayoutManager(getApplicationContext());
        aboutRecycleView.setLayoutManager(mLayoutManager);
        aboutRecycleView.setItemAnimator(new DefaultItemAnimator());
        aboutRecycleView.setAdapter(aboutAdapter);

        LinearLayoutManager horizontalLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        familyRecycleView.setLayoutManager(horizontalLayoutManager);
        familyRecycleView.setAdapter(familyAdapter);

        horizontalLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        histHandNoteRv.setLayoutManager(horizontalLayoutManager);
        histHandNoteRv.setAdapter(histHandNoteAdapter);

//        horizontalLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
//        symptomRv.setLayoutManager(horizontalLayoutManager);
//        symptomRv.setAdapter(symptomsAdapter);
//
//        horizontalLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
//        investRv.setLayoutManager(horizontalLayoutManager);
//        investRv.setAdapter(investAdapter);
//
//        horizontalLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
//        diagRv.setLayoutManager(horizontalLayoutManager);
//        diagRv.setAdapter(diagAdapter);

        horizontalLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        notesHandNoteRv.setLayoutManager(horizontalLayoutManager);
        notesHandNoteRv.setAdapter(notesHnNotesAdapter);

        ecntAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, ecntListArr);
        ecntAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        histSelectCase.setAdapter(ecntAdapter);

//        dummyData();
        checkApptInteraction();
        getEncouterDetailsForAppt();
        getRecordPref();
        getFamilyDetails();
        getNonepisodicData();
//        showGuide(1);


        appointmentSaveAsPrescription.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PatientRecordsCaseActivity.this, AppointmentSaveAsPrescriptionActivity.class);
                intent.putExtra("EpisodeId", episodeID);
                intent.putExtra("PatientId", patientId);
                intent.putExtra("apptMode", getIntent().getIntExtra("ApptMode", 0));
                intent.putExtra("apptDate", getIntent().getStringExtra("ApptDate"));
                intent.putExtra("apptTime", getIntent().getStringExtra("ApptTime"));
                intent.putExtra("encounterId", encounterId);
                intent.putExtra("SharingRecords", true);
                startActivity(intent);

                // Toast.makeText(getApplicationContext(),"Save as prescription click",Toast.LENGTH_LONG).show();
            }
        });

        appointmentSharedOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ZohoSalesIQ.Tracking.setCustomAction("AppointmentNotes - Share Data");
                Intent intent = new Intent(PatientRecordsCaseActivity.this, AppointmentEpisodeActivity.class);
                intent.putExtra("EpisodeId", episodeID);
                intent.putExtra("PatientId", patientId);
                intent.putExtra("apptMode", getIntent().getIntExtra("ApptMode", 0));
                intent.putExtra("apptDate", getIntent().getStringExtra("ApptDate"));
                intent.putExtra("apptTime", getIntent().getStringExtra("ApptTime"));
                intent.putExtra("encounterId", encounterId);
                intent.putExtra("SharingRecords", true);
                startActivity(intent);
                //Toast.makeText(getApplicationContext(),"click on share",Toast.LENGTH_LONG).show();
            }
        });
        recodShareCancelOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();

            }
        });

        savePatientData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ZohoSalesIQ.Tracking.setCustomAction("AppointmentNotes - Saving Patient Details");
                savePatientDetails();
            }
        });


        patientCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (patientInfoLayout.getVisibility() == View.VISIBLE) {
                    patientInfoLayout.setVisibility(View.GONE);
                    patientArrow.setImageDrawable(getResources().getDrawable(R.drawable.ic_arrow_right));
                } else {
                    ZohoSalesIQ.Tracking.setCustomAction("AppointmentNotes - View Patient Details");
                    patientInfoLayout.setVisibility(View.VISIBLE);
                    getPatientDetails();
                    patientArrow.setImageDrawable(getResources().getDrawable(R.drawable.ic_arrow_down));
                }
            }
        });

        familyCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (familyMoreData.getVisibility() == View.VISIBLE) {
                    familyMoreData.setVisibility(View.GONE);
                    familyArrow.setImageDrawable(getResources().getDrawable(R.drawable.ic_arrow_right));
                } else {
                    ZohoSalesIQ.Tracking.setCustomAction("AppointmentNotes - View Family Details");
                    familyMoreData.setVisibility(View.VISIBLE);
                    familyArrow.setImageDrawable(getResources().getDrawable(R.drawable.ic_arrow_down));
                }
            }
        });

        histHandNoteCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (histHandNoteRv.getVisibility() == View.VISIBLE) {
//                    Log.d("Gone", "*************************");
                    histHandNoteRv.setVisibility(View.GONE);
                    histHandNoteEmpty.setVisibility(View.GONE);
                    histHandNoteArrow.setImageDrawable(getResources().getDrawable(R.drawable.ic_arrow_right));
                } else {
                    ZohoSalesIQ.Tracking.setCustomAction("AppointmentNotes - History - Written Notes");
                    histHandNoteRv.setVisibility(View.VISIBLE);
                    histHandNoteModels.clear();
                    histHandNoteArrow.setImageDrawable(getResources().getDrawable(R.drawable.ic_arrow_down));
                    getWrittenNotes();
                }
            }
        });

        histEvalCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (evalMoreData.getVisibility() == View.VISIBLE) {
                    evalMoreData.setVisibility(View.GONE);
                } else {
                    ZohoSalesIQ.Tracking.setCustomAction("AppointmentNotes - History - Evaluation");
                    evalMoreData.setVisibility(View.VISIBLE);
                }
            }
        });

        histTreatCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (treatRv.getVisibility() == View.VISIBLE) {
                    treatRv.setVisibility(View.GONE);
                } else {
                    ZohoSalesIQ.Tracking.setCustomAction("AppointmentNotes - History - Treatment Card");
                    treatRv.setVisibility(View.VISIBLE);
                }
            }
        });

        //notes section
        notesHandNoteCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (notesHandNoteRv.getVisibility() == View.VISIBLE) {
                    notesHandNoteRv.setVisibility(View.GONE);
                    notesHnArrow.setImageDrawable(getResources().getDrawable(R.drawable.ic_arrow_right));
                } else {
                    ZohoSalesIQ.Tracking.setCustomAction("AppointmentNotes - Notes - Written Notes");
                    notesHandNoteRv.setVisibility(View.VISIBLE);
                    notesHnArrow.setImageDrawable(getResources().getDrawable(R.drawable.ic_arrow_down));
                    getWrittenNotesForEncounter();
                }
            }
        });

        notesEvalCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (notesEvalMoreData.getVisibility() == View.VISIBLE) {
                    notesEvalMoreData.setVisibility(View.GONE);
                    recordEpisEvalArrow.setImageDrawable(getResources().getDrawable(R.drawable.ic_arrow_right));
                } else {
                    ZohoSalesIQ.Tracking.setCustomAction("AppointmentNotes - Notes - Evaluation");
                    notesEvalMoreData.setVisibility(View.VISIBLE);
                    recordEpisEvalArrow.setImageDrawable(getResources().getDrawable(R.drawable.ic_arrow_down));
                    symptomsModel.clear();
                    investModel.clear();
                    diagModel.clear();
                    obsCatModel.clear();
                    symptomAdapter.notifyDataSetChanged();

//                    if (historyTabClick == 1) {
//                        getEpisEvalutaionForHistoryTab();
//                    } else {
                    getEpisEvalutaion();
//                    }
                }
            }
        });

        //for historyTab

        notesEvalCardHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!recordEpisEvalCountHistory.getText().toString().equalsIgnoreCase("0")) {


                    if (notesEvalMoreDataHistory.getVisibility() == View.VISIBLE) {
                        notesEvalMoreDataHistory.setVisibility(View.GONE);
                        recordEpisEvalArrowHistory.setImageDrawable(getResources().getDrawable(R.drawable.ic_arrow_right));
                    } else {
                        ZohoSalesIQ.Tracking.setCustomAction("AppointmentNotes - History - Eval Card View");
                        notesEvalMoreDataHistory.setVisibility(View.VISIBLE);
                        recordEpisEvalArrowHistory.setImageDrawable(getResources().getDrawable(R.drawable.ic_arrow_down));
                        symptomsModelH.clear();
                        investModelH.clear();
                        diagModelH.clear();
                        obsCatModelH.clear();
                        symptomAdapterH.notifyDataSetChanged();

                        // if (historyTabClick == 1) {
                        getEpisEvalutaionForHistoryTab();
//                    } else {
//                        getEpisEvalutaion();
//                    }
                    }
                } else {
                    Toast.makeText(PatientRecordsCaseActivity.this, getResources().getString(R.string.no_data_found), Toast.LENGTH_SHORT).show();

                }
            }
        });


        //-----------------------------for historyTab----------

        symptomLayoutH.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!symptomCountH.getText().toString().equalsIgnoreCase("0")) {
                    if (symptomListLayoutH.getVisibility() == View.GONE) {
                        symptomListLayoutH.setVisibility(View.VISIBLE);
                        ZohoSalesIQ.Tracking.setCustomAction("AppointmentNotes - History - Symptoms");
                    } else {
                        symptomListLayoutH.setVisibility(View.GONE);
                    }
                } else {
                    Toast.makeText(PatientRecordsCaseActivity.this, getResources().getString(R.string.no_data_found), Toast.LENGTH_SHORT).show();
                }
            }
        });


        symptomAddNewH.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PatientRecordsCaseActivity.this, PatientCreateRecordActivity.class);
                intent.putExtra("RecordName", "Symptoms");
                intent.putExtra("PatientId", patientId);
                intent.putExtra("EpisodeId", episodeID);
//                startActivity(intent);
                startActivityForResult(intent, 1);
            }
        });


        investAddNewH.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PatientRecordsCaseActivity.this, PatientCreateRecordActivity.class);
                intent.putExtra("RecordName", "Investigation");
                intent.putExtra("PatientId", patientId);
                intent.putExtra("EpisodeId", episodeID);
//                startActivity(intent);
                startActivityForResult(intent, 1);
            }
        });

        diagAddNewH.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PatientRecordsCaseActivity.this, PatientCreateRecordActivity.class);
                intent.putExtra("RecordName", "Diagnosis");
                intent.putExtra("PatientId", patientId);
                intent.putExtra("EpisodeId", episodeID);
//                startActivity(intent);
                startActivityForResult(intent, 1);
            }
        });


        obsLayoutH.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if (obsMainLayoutH.getVisibility() == View.VISIBLE) {
                    obsMainLayoutH.setVisibility(View.GONE);
                    recordEvalObsArrowHistory.setImageDrawable(getResources().getDrawable(R.drawable.ic_arrow_right));
                } else {
//                    obsMainLayoutH.setVisibility(View.VISIBLE);
//                    recordEvalObsArrowHistory.setImageDrawable(getResources().getDrawable(R.drawable.ic_arrow_down));

                    if (!obsCountH.getText().toString().equalsIgnoreCase("0")) {
                        // mainLayout.setVisibility(View.GONE);
                        ZohoSalesIQ.Tracking.setCustomAction("AppointmentNotes - History - Observation");
                        obsMainLayoutH.setVisibility(View.VISIBLE);
                        recordEvalObsArrowHistory.setImageDrawable(getResources().getDrawable(R.drawable.ic_arrow_down));

                        // treatPlanParentLayout.setVisibility(View.GONE);
                        //   evalDataLayout.setVisibility(View.GONE);
                    } else {
                        Toast.makeText(PatientRecordsCaseActivity.this, getResources().getString(R.string.no_data_found), Toast.LENGTH_SHORT).show();
                    }

                }
            }
        });


        investLayoutH.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!investCountH.getText().toString().equalsIgnoreCase("0")) {
                    if (investListLayoutH.getVisibility() == View.GONE) {
                        investListLayoutH.setVisibility(View.VISIBLE);
                        ZohoSalesIQ.Tracking.setCustomAction("AppointmentNotes - History - Investigation");
                    } else {
                        investListLayoutH.setVisibility(View.GONE);
                    }
                } else {
                    Toast.makeText(PatientRecordsCaseActivity.this, getResources().getString(R.string.no_data_found), Toast.LENGTH_SHORT).show();
                }
            }
        });

        diagLayoutH.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!diagCountH.getText().toString().equalsIgnoreCase("0")) {
                    if (diagListLayoutH.getVisibility() == View.GONE) {
                        diagListLayoutH.setVisibility(View.VISIBLE);
                        ZohoSalesIQ.Tracking.setCustomAction("AppointmentNotes - History - Diagnosis");
                    } else {
                        diagListLayoutH.setVisibility(View.GONE);
                    }
                } else {
                    Toast.makeText(PatientRecordsCaseActivity.this, getResources().getString(R.string.no_data_found), Toast.LENGTH_SHORT).show();
                }
            }
        });


        //----------------------------- end -------------------------


        symptomLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!symptomCount.getText().toString().equalsIgnoreCase("0")) {
                    if (symptomListLayout.getVisibility() == View.GONE) {
                        symptomListLayout.setVisibility(View.VISIBLE);
                        ZohoSalesIQ.Tracking.setCustomAction("AppointmentNotes - Notes - Symptoms");
                    } else {
                        symptomListLayout.setVisibility(View.GONE);
                    }
                } else {
                    Toast.makeText(PatientRecordsCaseActivity.this, getResources().getString(R.string.no_data_found), Toast.LENGTH_SHORT).show();
                }
            }
        });


        symptomAddNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dropDownShowFlag = 1;
                Intent intent = new Intent(PatientRecordsCaseActivity.this, PatientCreateRecordActivity.class);
                intent.putExtra("RecordName", "Symptoms");
                intent.putExtra("PatientId", patientId);
                intent.putExtra("EpisodeId", episodeID);
//                startActivity(intent);
                startActivityForResult(intent, 1);
            }
        });


        investAddNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dropDownShowFlag = 1;
                Intent intent = new Intent(PatientRecordsCaseActivity.this, PatientCreateRecordActivity.class);
                intent.putExtra("RecordName", "Investigation");
                intent.putExtra("PatientId", patientId);
                intent.putExtra("EpisodeId", episodeID);
//                startActivity(intent);
                startActivityForResult(intent, 1);
            }
        });

        diagAddNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dropDownShowFlag = 1;
                Intent intent = new Intent(PatientRecordsCaseActivity.this, PatientCreateRecordActivity.class);
                intent.putExtra("RecordName", "Diagnosis");
                intent.putExtra("PatientId", patientId);
                intent.putExtra("EpisodeId", episodeID);
//                startActivity(intent);
                startActivityForResult(intent, 1);
            }
        });


        obsLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if (obsMainLayout.getVisibility() == View.VISIBLE) {
                    obsMainLayout.setVisibility(View.GONE);
                    recordEvalObsArrow.setImageDrawable(getResources().getDrawable(R.drawable.ic_arrow_right));
                } else {
                    ZohoSalesIQ.Tracking.setCustomAction("AppointmentNotes - Notes - Observation");
                    obsMainLayout.setVisibility(View.VISIBLE);
                    recordEvalObsArrow.setImageDrawable(getResources().getDrawable(R.drawable.ic_arrow_down));

                   // if (!obsCount.getText().toString().equalsIgnoreCase("0")) {
                        // mainLayout.setVisibility(View.GONE);
                        obsMainLayout.setVisibility(View.VISIBLE);
                        // treatPlanParentLayout.setVisibility(View.GONE);
                        //   evalDataLayout.setVisibility(View.GONE);
//                    } else {
//                        Toast.makeText(PatientRecordsCaseActivity.this, getResources().getString(R.string.no_data_found), Toast.LENGTH_SHORT).show();
//                    }

                }
            }
        });


        investLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!investCount.getText().toString().equalsIgnoreCase("0")) {
                    if (investListLayout.getVisibility() == View.GONE) {
                        investListLayout.setVisibility(View.VISIBLE);
                        ZohoSalesIQ.Tracking.setCustomAction("AppointmentNotes - Notes - Investigation");
                    } else {
                        investListLayout.setVisibility(View.GONE);
                    }
                } else {
                    Toast.makeText(PatientRecordsCaseActivity.this, getResources().getString(R.string.no_data_found), Toast.LENGTH_SHORT).show();
                }
            }
        });

        diagLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!diagCount.getText().toString().equalsIgnoreCase("0")) {
                    if (diagListLayout.getVisibility() == View.GONE) {
                        diagListLayout.setVisibility(View.VISIBLE);
                        ZohoSalesIQ.Tracking.setCustomAction("AppointmentNotes - Notes - Diagnosis");
                    } else {
                        diagListLayout.setVisibility(View.GONE);
                    }
                } else {
                    Toast.makeText(PatientRecordsCaseActivity.this, getResources().getString(R.string.no_data_found), Toast.LENGTH_SHORT).show();
                }
            }
        });


        treatPlanLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

//                if (!treatPlanCount.getText().toString().equalsIgnoreCase("0")) {


                    if (treatPlanParentLayout.getVisibility() == View.VISIBLE) {
                        treatPlanParentLayout.setVisibility(View.GONE);
                        recordEpisTreatmentArrow.setImageDrawable(getResources().getDrawable(R.drawable.ic_arrow_right));

                    } else {
                        ZohoSalesIQ.Tracking.setCustomAction("AppointmentNotes - Notes - Treatment Plan");
                        treatPlanParentLayout.setVisibility(View.VISIBLE);
                        recordEpisTreatmentArrow.setImageDrawable(getResources().getDrawable(R.drawable.ic_arrow_down));
                        treatPlanModel.clear();
                        treatPlanAdapter.notifyDataSetChanged();
                        getEpisTreatPlan();
                    }
//                } else {
//                    Toast.makeText(PatientRecordsCaseActivity.this, getResources().getString(R.string.no_data_found), Toast.LENGTH_SHORT).show();
//
//                }
            }
        });

//treatment paln for history
        treatPlanLayoutH.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!treatPlanCountH.getText().toString().equalsIgnoreCase("0")) {

                    if (treatPlanParentLayoutH.getVisibility() == View.VISIBLE) {
                        treatPlanParentLayoutH.setVisibility(View.GONE);
                        recordEpisTreatmentArrowHistory.setImageDrawable(getResources().getDrawable(R.drawable.ic_arrow_right));

                    } else {
                        ZohoSalesIQ.Tracking.setCustomAction("AppointmentNotes - History - Treatment Plan");
                        treatPlanParentLayoutH.setVisibility(View.VISIBLE);
                        recordEpisTreatmentArrowHistory.setImageDrawable(getResources().getDrawable(R.drawable.ic_arrow_down));
                        treatPlanModelH.clear();
                        treatPlanAdapterH.notifyDataSetChanged();
                        getEpisTreatPlanForHistoryTab();
                    }
                } else {
                    Toast.makeText(PatientRecordsCaseActivity.this, getResources().getString(R.string.no_data_found), Toast.LENGTH_SHORT).show();

                }
            }
        });


        ArrayAdapter aa = new ArrayAdapter(this, android.R.layout.simple_spinner_item, bloodType);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Setting the ArrayAdapter data on the Spinner
        pBloodG.setAdapter(aa);
        pBloodG.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        final Calendar c1 = Calendar.getInstance();
        mYear = c1.get(Calendar.YEAR);
        mMonth = c1.get(Calendar.MONTH);
        mDay = c1.get(Calendar.DAY_OF_MONTH);
        String temp1 = mDay + "-" + (mMonth + 1) + "-" + mYear;
        // temp = appUtilities.changeDateFormat("yyyy-MM-dd", "dd MMM, yy", temp);
        caseName.setText("Case-" + temp1);


        aa = new ArrayAdapter(this, android.R.layout.simple_spinner_item, interactionMode);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Setting the ArrayAdapter data on the Spinner
        caseType.setAdapter(aa);

        if (getIntent().getIntExtra("ApptMode", 0) == 3) {
            caseType.setSelection(3);

        } else if (getIntent().getIntExtra("ApptMode", 0) == 2) {
            caseType.setSelection(2);

        } else if (getIntent().getIntExtra("ApptMode", 0) == 1) {
            caseType.setSelection(1);

        }
        // caseType.setSelection(getIntent().getIntExtra("ApptMode", 0));
        caseType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                caseTypeClick++;

                if (caseTypeClick == 1) {

                } else {
                    final Calendar c1 = Calendar.getInstance();
                    mYear = c1.get(Calendar.YEAR);
                    mMonth = c1.get(Calendar.MONTH);
                    mDay = c1.get(Calendar.DAY_OF_MONTH);
                    // String temp1 = mDay + "-" + (mMonth + 1) + "-" + mYear;
                    String temp1 = mYear + "-" + (mMonth + 1) + "-" + mDay;
                    String tempDate = appUtilities.changeDateFormat("yyyy-MM-dd", "dd MMM, yy", temp1);
                    caseDate.setText(tempDate);


                    Calendar mcurrentTime1 = Calendar.getInstance();
                    mHour = mcurrentTime1.get(Calendar.HOUR_OF_DAY);
                    mMinute = mcurrentTime1.get(Calendar.MINUTE);
                    //tempTime = getIntent().getStringExtra("ApptTime");
                    //tempTime = tempTime.substring(0, 5);
                    caseTime.setText(mHour + ":" + mMinute);
                }


//                if(!interactionChanged)
//                {
//
////                    recordsCaseDate
//
////                            recordsCaseTime
////
////                    caseDate = findViewById(R.id.recordsCaseDate);
////                    caseTime = findViewById(R.id.recordsCaseTime);
//
//                    final Calendar c1 = Calendar.getInstance();
//                    mYear = c1.get(Calendar.YEAR);
//                    mMonth = c1.get(Calendar.MONTH);
//                    mDay = c1.get(Calendar.DAY_OF_MONTH);
//                    // String temp1 = mDay + "-" + (mMonth + 1) + "-" + mYear;
//                    String temp1 = mYear + "-" + (mMonth + 1) + "-" + mDay;
//                    String tempDate = appUtilities.changeDateFormat("yyyy-MM-dd", "dd MMM, yy", temp1);
//                    caseDate.setText(tempDate);
//
//
//                    Calendar mcurrentTime1 = Calendar.getInstance();
//                    mHour = mcurrentTime1.get(Calendar.HOUR_OF_DAY);
//                    mMinute = mcurrentTime1.get(Calendar.MINUTE);
//                    //tempTime = getIntent().getStringExtra("ApptTime");
//                    //tempTime = tempTime.substring(0, 5);
//                    caseTime.setText(mHour + ":" + mMinute);
//                }


            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        pGender.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if (i == R.id.recordPGenderMale) {
                    RadioButton male = findViewById(R.id.recordPGenderMale);
                    male.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                    male.setTextColor(getResources().getColor(R.color.colorWhite));

                    RadioButton female = findViewById(R.id.recordPGenderFemale);
                    female.setBackgroundColor(getResources().getColor(R.color.colorWhite));
                    female.setTextColor(getResources().getColor(R.color.colorBlack));
                    genderId = 1;
                } else {
                    RadioButton male = findViewById(R.id.recordPGenderFemale);
                    male.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                    male.setTextColor(getResources().getColor(R.color.colorWhite));

                    RadioButton female = findViewById(R.id.recordPGenderMale);
                    female.setBackgroundColor(getResources().getColor(R.color.colorWhite));
                    female.setTextColor(getResources().getColor(R.color.colorBlack));
                    genderId = 2;

                }
            }
        });

        pDOB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (pDOB.getText().equals(getResources().getString(R.string.enter_dob))) {
                    final Calendar c = Calendar.getInstance();
                    mYear = c.get(Calendar.YEAR);
                    mMonth = c.get(Calendar.MONTH);
                    mDay = c.get(Calendar.DAY_OF_MONTH);
                } else {
                    String date = appUtilities.changeDateFormat("dd MMM, yy", "dd MM yyyy", pDOB.getText().toString());
                    if(!date.equalsIgnoreCase("")) {
                        String[] dSplit = date.split(" ");
                        mDay = Integer.parseInt(dSplit[0]);
                        mMonth = Integer.parseInt(dSplit[1]) - 1;
                        mYear = Integer.parseInt(dSplit[2]);
                    }
                }

                DatePickerDialog datePickerDialog = new DatePickerDialog(PatientRecordsCaseActivity.this,
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {

                                String temp = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;
                                temp = appUtilities.changeDateFormat("dd/MM/yyyy", "dd MMM, yy", temp);
                                pDOB.setText(temp);

                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.show();
            }
        });

        sectionProceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (caseInteraction.getSelectedItemPosition() == 0) {

                    Toast.makeText(PatientRecordsCaseActivity.this, getResources().getString(R.string.interaction_changed), Toast.LENGTH_SHORT).show();

                } else {

                    flagProceedClick = 1;

                    if (sectionNum == 1) {
//                        Log.d("To Section 2", "***************");
//                    sectionProceed.setVisibility(View.GONE);
                        aboutLayout.setVisibility(View.GONE);
                        caseLayout.setVisibility(View.GONE);
                        caseSelectLayout.setVisibility(View.VISIBLE);
                        sectionNum = 2;

                        showGuide(7);

                        ZohoSalesIQ.Tracking.setCustomAction("AppointmentNotes - Case Select Section");

                        headCase.setTextColor(getResources().getColor(R.color.colorAccent));
                        headHist.setTextColor(getResources().getColor(R.color.colorWhite));
                        headAbout.setTextColor(getResources().getColor(R.color.colorWhite));
                        headNotes.setTextColor(getResources().getColor(R.color.colorWhite));

                        getEpisode();
                    } else if (sectionNum == 2) {
//                        Log.d("To Section 3", "***************");
//                        Log.d("Encounter Type", caseInteraction.getSelectedItem().toString());

                        JSONObject reqObj = new JSONObject();
                        try {
//                            reqObj.put("patient_id", patientId);
//                            reqObj.put("episode_id", episodeID);
//                            // reqObj.put("encounter_mode", caseType.getSelectedItem().toString());//commented by dileep
//                            reqObj.put("encounter_mode", caseInteraction.getSelectedItem().toString());
//
//                            String date = appUtilities.changeDateFormat("dd MMM, yy", "dd-MM-yyyy", caseDate.getText().toString());
//                            String dateTime = date + " " + caseTime.getText().toString();
//                            reqObj.put("encounter_date_time", dateTime);
//                            reqObj.put("appointment_id", apptId);
//                            reqObj.put("chat_id", 0);


                            reqObj.put("patient_id", patientId);
                            reqObj.put("episode_id", episodeID);
                            // reqObj.put("encounter_mode", caseType.getSelectedItem().toString());//commented by dileep
                            reqObj.put("encounter_mode", caseInteraction.getSelectedItem().toString());

                            String date = appUtilities.changeDateFormat("dd MMM, yy", "dd-MM-yyyy", selectCaseDate.getText().toString());
                            String dateTime = date + " " + selectCaseTime.getText().toString();
                            reqObj.put("encounter_date_time", dateTime);
                            reqObj.put("appointment_id", apptId);
                            reqObj.put("chat_id", 0);


                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        //if (interactionChanged) {
                        if (interactionChangeTwo) {
                            saveEncounter(reqObj);
                        } else {
                            //  saveEncounter(reqObj);//added by dileep
                            caseLayout.setVisibility(View.GONE);
//                        caseHistoryLayout.setVisibility(View.VISIBLE);// commented by dileep
//                        sectionProceed.setVisibility(View.VISIBLE); // commented by dileep
                            caseHistoryLayout.setVisibility(View.GONE);// commented by dileep
                            sectionProceed.setVisibility(View.GONE); // commented by dileep

                            notesLayout.setVisibility(View.VISIBLE);// added by dileep

                            showGuide(14);

                            ZohoSalesIQ.Tracking.setCustomAction("AppointmentNotes - Notes Section");

                            getEpisode();
//                        getHistEpisode();

//                        if (ecntListArr != null) {
//                            ecntListArr.clear();
//                        }
//                        if (ecntIdArr != null) {
//                            ecntIdArr.clear();
//                        }

                            getEpisEncounter();
                        }

                        // headHist.setTextColor(getResources().getColor(R.color.colorAccent));
                        headHist.setTextColor(getResources().getColor(R.color.colorWhite));
                        headCase.setTextColor(getResources().getColor(R.color.colorWhite));
                        headAbout.setTextColor(getResources().getColor(R.color.colorWhite));
                        headNotes.setTextColor(getResources().getColor(R.color.colorAccent));
                        sectionNum = 3;

                    } else if (sectionNum == 3) {
//                        Log.d("To Section 4", "***************");
                        showGuide(14);
                        notesLayout.setVisibility(View.VISIBLE);
                        caseHistoryLayout.setVisibility(View.GONE);
                        sectionProceed.setVisibility(View.GONE);
                        getEpisEncounter();

                        headNotes.setTextColor(getResources().getColor(R.color.colorAccent));
                        headCase.setTextColor(getResources().getColor(R.color.colorWhite));
                        headAbout.setTextColor(getResources().getColor(R.color.colorWhite));
                        headHist.setTextColor(getResources().getColor(R.color.colorWhite));
//                    JSONObject reqObj = new JSONObject();
//                    try {
//                        reqObj.put("patient_id", patientId);
//                        reqObj.put("episode_id", episodeID);
//                        reqObj.put("encounter_mode", selectCase.getSelectedItem().toString());
//
//                        String date = appUtilities.changeDateFormat("dd MMM, yy", "dd-MM-yyyy", selectCaseDate.getText().toString());
//                        String dateTime = date + " " + selectCaseTime.getText().toString();
//                        reqObj.put("encounter_date_time", dateTime);
//                        reqObj.put("appointment_id", apptId);
//                        reqObj.put("chat_id", 0);
//
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                    saveEncounter(reqObj);
                    }
                }
            }
        });

        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);
        String temp = getIntent().getStringExtra("ApptDate");//mDay + " " + (mMonth+1) + " " + mYear;
        temp = appUtilities.changeDateFormat("yyyy-MM-dd", "dd MMM, yy", temp);
        caseDate.setText(temp);
        caseDateLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (caseDate.getText().equals("")) {
                    final Calendar c = Calendar.getInstance();
                    mYear = c.get(Calendar.YEAR);
                    mMonth = c.get(Calendar.MONTH);
                    mDay = c.get(Calendar.DAY_OF_MONTH);
                } else {
                    String date = appUtilities.changeDateFormat("dd MMM, yy", "dd MM yyyy", caseDate.getText().toString());
                    String[] dSplit = date.split(" ");
                    mDay = Integer.parseInt(dSplit[0]);
                    mMonth = Integer.parseInt(dSplit[1]) - 1;
                    mYear = Integer.parseInt(dSplit[2]);
                }
                DatePickerDialog datePickerDialog = new DatePickerDialog(PatientRecordsCaseActivity.this,
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {

                                String temp = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;
                                temp = appUtilities.changeDateFormat("dd/MM/yyyy", "dd MMM, yy", temp);
                                caseDate.setText(temp);

                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.show();
            }
        });

        Calendar mcurrentTime = Calendar.getInstance();
        mHour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
        mMinute = mcurrentTime.get(Calendar.MINUTE);
        String tempTime = getIntent().getStringExtra("ApptTime");
        tempTime = tempTime.substring(0, 5);
        caseTime.setText(tempTime);//.setText( mHour + ":" + mMinute);
        caseTimeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Calendar mcurrentTime = Calendar.getInstance();
//                mHour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
//                mMinute = mcurrentTime.get(Calendar.MINUTE);
                String[] timeSplit = caseTime.getText().toString().split(":");
                mHour = Integer.parseInt(timeSplit[0]);
                mMinute = Integer.parseInt(timeSplit[1]);
                TimePickerDialog mTimePicker = new TimePickerDialog(PatientRecordsCaseActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        caseTime.setText(selectedHour + ":" + selectedMinute);
                    }
                }, mHour, mMinute, true);//Yes 24 hour time
//                mTimePicker.setTitle("Select Time");
                mTimePicker.show();
            }
        });

        caseProceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!caseName.getText().toString().equals("")) {
                    ZohoSalesIQ.Tracking.setCustomAction("AppointmentNotes - Saving New Episode");
                    saveNewEpisode();
                } else {
                    Toast.makeText(PatientRecordsCaseActivity.this, getResources().getString(R.string.create_case_proceed_warn), Toast.LENGTH_SHORT).show();
                }
            }
        });

        caseInteraction.setAdapter(aa);
//        caseInteraction.setSelection(getIntent().getIntExtra("ApptMode", 0) - 1);
        caseInteraction.setSelection(getIntent().getIntExtra("ApptMode", 0));//added by dileep 12th june 2019
        caseInteraction.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
//                interactionChanged = true;
                if (apptMode != caseInteraction.getSelectedItemPosition()) {
                    interactionChanged = true;
                    ZohoSalesIQ.Tracking.setCustomAction("AppointmentNotes - Interaction Changed");

                    caseInteractionChangeClick++;

                    if (caseInteractionChangeClick == 1) {
                        try {
                            caseInteractionChangeClick--;
//                        if (episodeID != episodesArr.getJSONObject(selectCase.getSelectedItemPosition()).getInt("id")) {
                            if (interactionChanged) {
                                interactionChanged = false;
                                interactionChangeTwo = true;
                                //caseInteraction.setSelection(0);
                                final Calendar c1 = Calendar.getInstance();
                                mYear = c1.get(Calendar.YEAR);
                                mMonth = c1.get(Calendar.MONTH);
                                mDay = c1.get(Calendar.DAY_OF_MONTH);
                                // String temp1 = mDay + "-" + (mMonth + 1) + "-" + mYear;
                                String temp1 = mYear + "-" + (mMonth + 1) + "-" + mDay;
                                String tempDate = appUtilities.changeDateFormat("yyyy-MM-dd", "dd MMM, yy", temp1);
                                selectCaseDate.setText(tempDate);


                                Calendar mcurrentTime1 = Calendar.getInstance();
                                mHour = mcurrentTime1.get(Calendar.HOUR_OF_DAY);
                                mMinute = mcurrentTime1.get(Calendar.MINUTE);
                                //tempTime = getIntent().getStringExtra("ApptTime");
                                //tempTime = tempTime.substring(0, 5);
                                selectCaseTime.setText(mHour + ":" + mMinute);


                            } else {

                            }

                            if (caseInteraction.getSelectedItemPosition() == 0) {

//                            sectionProceed.setEnabled(false); // commented by dileep
//                            headerHistLayout.setEnabled(false);
//                            headerNotesLayout.setEnabled(false);

                                // Toast.makeText(PatientRecordsCaseActivity.this, getResources().getString(R.string.interaction_changed), Toast.LENGTH_SHORT).show();

                            } else {
                                // interactionChanged=true;
//                            sectionProceed.setEnabled(true); // commented by dileep
//                            headerHistLayout.setEnabled(true);
//                            headerNotesLayout.setEnabled(true);
                            }


                        } catch (Exception e) {

                        }
                    } else {

                        // interactionChanged = false;
                        interactionChangeTwo = true;
                        caseInteraction.setSelection(0);
                        final Calendar c1 = Calendar.getInstance();
                        mYear = c1.get(Calendar.YEAR);
                        mMonth = c1.get(Calendar.MONTH);
                        mDay = c1.get(Calendar.DAY_OF_MONTH);
                        // String temp1 = mDay + "-" + (mMonth + 1) + "-" + mYear;
                        String temp1 = mYear + "-" + (mMonth + 1) + "-" + mDay;
                        String tempDate = appUtilities.changeDateFormat("yyyy-MM-dd", "dd MMM, yy", temp1);
                        selectCaseDate.setText(tempDate);


                        Calendar mcurrentTime1 = Calendar.getInstance();
                        mHour = mcurrentTime1.get(Calendar.HOUR_OF_DAY);
                        mMinute = mcurrentTime1.get(Calendar.MINUTE);
                        //tempTime = getIntent().getStringExtra("ApptTime");
                        //tempTime = tempTime.substring(0, 5);
                        selectCaseTime.setText(mHour + ":" + mMinute);
                    }


//                    try {
//                        for (int k = 0; k < episodesArr.length(); k++) {
//                            JSONObject epic = episodesArr.getJSONObject(k);
//                            Log.d("Selected", "&###############");
//
//
//                            if (epic.getInt("id") == episodeID) {
//                                Log.d("Selected", "&&&&&&&&&&&&&&" + episodeID);
//
//
//                                // selectCase.setSelection(i);
//                                Log.d("Selected case", selectCase.getSelectedItem().toString());
//                            } else {
//                                caseInteraction.setSelection(0);
//                                final Calendar c1 = Calendar.getInstance();
//                                mYear = c1.get(Calendar.YEAR);
//                                mMonth = c1.get(Calendar.MONTH);
//                                mDay = c1.get(Calendar.DAY_OF_MONTH);
//                                // String temp1 = mDay + "-" + (mMonth + 1) + "-" + mYear;
//                                String temp1 = mYear + "-" + (mMonth + 1) + "-" + mDay;
//                                String tempDate = appUtilities.changeDateFormat("yyyy-MM-dd", "dd MMM, yy", temp1);
//                                selectCaseDate.setText(tempDate);
//
//
//                                Calendar mcurrentTime1 = Calendar.getInstance();
//                                mHour = mcurrentTime1.get(Calendar.HOUR_OF_DAY);
//                                mMinute = mcurrentTime1.get(Calendar.MINUTE);
//                                //tempTime = getIntent().getStringExtra("ApptTime");
//                                //tempTime = tempTime.substring(0, 5);
//                                selectCaseTime.setText(mHour + ":" + mMinute);
//
//
//                                sectionProceed.setEnabled(false); // commented by dileep
//                                headerHistLayout.setEnabled(false);
//                                headerNotesLayout.setEnabled(false);
//                            }
//
//
//                        }
//
//                    } catch (Exception e) {
//
//                    }


//                    try {
//                        if (episodeID != episodesArr.getJSONObject(i).getInt("id")) {

//                    caseInteraction.setSelection(0);

//                    if (interactionSelectedPosition == caseInteraction.getSelectedItemPosition()) {
//
//                    } else {
                    //added by dileep
//                    caseInteraction.setSelection(0);
//                    final Calendar c1 = Calendar.getInstance();
//                    mYear = c1.get(Calendar.YEAR);
//                    mMonth = c1.get(Calendar.MONTH);
//                    mDay = c1.get(Calendar.DAY_OF_MONTH);
//                    // String temp1 = mDay + "-" + (mMonth + 1) + "-" + mYear;
//                    String temp1 = mYear + "-" + (mMonth + 1) + "-" + mDay;
//                    String tempDate = appUtilities.changeDateFormat("yyyy-MM-dd", "dd MMM, yy", temp1);
//                    selectCaseDate.setText(tempDate);
//
//
//                    Calendar mcurrentTime1 = Calendar.getInstance();
//                    mHour = mcurrentTime1.get(Calendar.HOUR_OF_DAY);
//                    mMinute = mcurrentTime1.get(Calendar.MINUTE);
//                    //tempTime = getIntent().getStringExtra("ApptTime");
//                    //tempTime = tempTime.substring(0, 5);
//                    selectCaseTime.setText(mHour + ":" + mMinute);
//
//
//                    sectionProceed.setEnabled(false); // commented by dileep
//                    headerHistLayout.setEnabled(false);
//                    headerNotesLayout.setEnabled(false);


//                    }


//                        }
//                    } catch (Exception e) {
//
//                    }


//                    caseInteraction.setSelection(0);
//
////                    if(interactionSelectedPosition==caseInteraction.getSelectedItemPosition())
////                    {
////
////                    }
////                    else {
//                    //added by dileep
//                    final Calendar c1 = Calendar.getInstance();
//                    mYear = c1.get(Calendar.YEAR);
//                    mMonth = c1.get(Calendar.MONTH);
//                    mDay = c1.get(Calendar.DAY_OF_MONTH);
//                   // String temp1 = mDay + "-" + (mMonth + 1) + "-" + mYear;
//                    String temp1 = mYear + "-" + (mMonth + 1) + "-" + mDay;
//                    String tempDate = appUtilities.changeDateFormat("yyyy-MM-dd", "dd MMM, yy", temp1);
//                    selectCaseDate.setText(tempDate);
//
//
//                    Calendar mcurrentTime1 = Calendar.getInstance();
//                    mHour = mcurrentTime1.get(Calendar.HOUR_OF_DAY);
//                    mMinute = mcurrentTime1.get(Calendar.MINUTE);
//                    //tempTime = getIntent().getStringExtra("ApptTime");
//                    //tempTime = tempTime.substring(0, 5);
//                    selectCaseTime.setText(mHour + ":" + mMinute);
////                    }


                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        selectCase.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                try {

                    selectCaseSpinner = 1;

                    if (episodeID == 0) {

                        JSONObject selectedCase = episodesArr.getJSONObject(i);
                        noOfInteraction.setText(selectedCase.getString("encounterCount"));

                        DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        DateFormat outputFormat = new SimpleDateFormat("dd MMM, yyyy");
                        String inputDateStr = selectedCase.getJSONObject("last_encountered_on").getString("encounter_date_time");
                        Date date = inputFormat.parse(inputDateStr);
                        String outputDateStr = outputFormat.format(date);

                        // String date = appUtilities.changeDateFormat("yyyy-MM-dd mm:HH:ss", "dd MMM, yy", selectedCase.getJSONObject("last_encountered_on").getString("encounter_date_time"));
                        caseLastInteraction.setText(outputDateStr);

//                        String encounterMode = episodesArr.getJSONObject(i).getJSONObject("last_encountered_on").getString("encounter_mode");
//                        for (int j = 0; j < interactionMode.length; j++) {
//                            if (interactionMode[j].toString().equalsIgnoreCase(encounterMode)) {
//                                caseInteraction.setSelection(j);
//                                // interactionSelectedPosition = j;
//                                break;
//                            }
//
//                        }
//
//                        selectCaseDate.setText(outputDateStr);
//                        String caseTime = selectedCase.getJSONObject("last_encountered_on").getString("encounter_date_time");
//
//                        String currentString = caseTime;
//                        String[] separated = currentString.split(" ");
//                        String tempTime = separated[1].substring(0, separated[1].length() - 3);
//                        selectCaseTime.setText(tempTime);
                        JSONArray diagArr = selectedCase.getJSONArray("diagnosis");
                        String temp = "";
                        if (diagArr.length() > 0) {
                            for (int j = 0; j < diagArr.length(); j++) {
                                JSONObject diagObj = diagArr.getJSONObject(j);
                                temp += diagObj.getString("diagnosis") + ",";
                            }
                        } else {
                            temp = "--";
                        }
                        selectCaseDiag.setText(temp);

                        episodeID = episodesArr.getJSONObject(i).getInt("id");
                        interactionChanged = false;

                        getEpisEncounter();//added by dileep
                        getWrittenNotesForEncounter();//added by dileep


                    } else {

                        if (episodeID != episodesArr.getJSONObject(i).getInt("id")) {
                            ZohoSalesIQ.Tracking.setCustomAction("AppointmentNotes - Case Select Section - Selected New Case");
                            JSONObject selectedCase = episodesArr.getJSONObject(i);
                            noOfInteraction.setText(selectedCase.getString("encounterCount"));
//                    selectCaseDiag.setText(selectedCase.get("diagnosis").toString());

                            DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            DateFormat outputFormat = new SimpleDateFormat("dd MMM, yyyy");
                            String inputDateStr = selectedCase.getJSONObject("last_encountered_on").getString("encounter_date_time");
                            Date date = inputFormat.parse(inputDateStr);
                            String outputDateStr = outputFormat.format(date);

                            // String date = appUtilities.changeDateFormat("yyyy-MM-dd mm:HH:ss", "dd MMM, yy", selectedCase.getJSONObject("last_encountered_on").getString("encounter_date_time"));
                            caseLastInteraction.setText(outputDateStr);

                            String encounterMode = episodesArr.getJSONObject(i).getJSONObject("last_encountered_on").getString("encounter_mode");
                            for (int j = 0; j < interactionMode.length; j++) {
                                if (interactionMode[j].toString().equalsIgnoreCase(encounterMode)) {
                                    caseInteraction.setSelection(j);
                                    //  interactionSelectedPosition = j;
                                    break;
                                }

                            }


                            //added by dileep


                            // String tempDate = appUtilities.changeDateFormat("yyyy-MM-dd ", "dd MMM, yy", temp1);
                            selectCaseDate.setText(outputDateStr);


//                    Calendar mcurrentTime1 = Calendar.getInstance();
//                    mHour = mcurrentTime1.get(Calendar.HOUR_OF_DAY);
//                    mMinute = mcurrentTime1.get(Calendar.MINUTE);
//                    //tempTime = getIntent().getStringExtra("ApptTime");
//                    //tempTime = tempTime.substring(0, 5);
                            //selectCaseTime.setText(mHour + ":" + mMinute);

                            String caseTime = selectedCase.getJSONObject("last_encountered_on").getString("encounter_date_time");

                            String currentString = caseTime;
                            String[] separated = currentString.split(" ");
//                        separated[0]; // this will contain "Fruit"
//                        separated[1]; // this will contain " they taste good"

                            String tempTime = appUtilities.changeDateFormat("mm:HH:ss", "mm:HH", separated[1]);

                            selectCaseTime.setText(tempTime);


//                        String time = selectedCase.getJSONObject("last_encountered_on").getString("encounter_date_time");
//
//                        String currentString = time;
//                        String[] separated = currentString.split("");
//
//                        String timeAt = appUtilities.changeDateFormat("mm:HH:ss", "mm:HH",  separated[1]);
//                        selectCaseDate.setText(date);
//                        selectCaseTime.setText(timeAt);


                            JSONArray diagArr = selectedCase.getJSONArray("diagnosis");
                            String temp = "";
                            if (diagArr.length() > 0) {
                                for (int j = 0; j < diagArr.length(); j++) {
                                    JSONObject diagObj = diagArr.getJSONObject(j);
                                    temp += diagObj.getString("diagnosis") + ",";
                                }
                            } else {
                                temp = "--";
                            }
                            selectCaseDiag.setText(temp);

                            episodeID = episodesArr.getJSONObject(i).getInt("id");
                            interactionChanged = true;

                            getEpisEncounter();//added by dileep
                            getWrittenNotesForEncounter();//added by dileep

                        } else {
                            JSONObject selectedCase = episodesArr.getJSONObject(i);
                            noOfInteraction.setText(selectedCase.getString("encounterCount"));

                            DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            DateFormat outputFormat = new SimpleDateFormat("dd MMM, yyyy");
                            String inputDateStr = selectedCase.getJSONObject("last_encountered_on").getString("encounter_date_time");
                            Date date = inputFormat.parse(inputDateStr);
                            String outputDateStr = outputFormat.format(date);

                            // String date = appUtilities.changeDateFormat("yyyy-MM-dd mm:HH:ss", "dd MMM, yy", selectedCase.getJSONObject("last_encountered_on").getString("encounter_date_time"));
                            caseLastInteraction.setText(outputDateStr);

//                        String encounterMode = episodesArr.getJSONObject(i).getJSONObject("last_encountered_on").getString("encounter_mode");
//                        for (int j = 0; j < interactionMode.length; j++) {
//                            if (interactionMode[j].toString().equalsIgnoreCase(encounterMode)) {
//                                caseInteraction.setSelection(j);
//                                // interactionSelectedPosition = j;
//                                break;
//                            }
//
//                        }
//
//                        selectCaseDate.setText(outputDateStr);
//                        String caseTime = selectedCase.getJSONObject("last_encountered_on").getString("encounter_date_time");
//
//                        String currentString = caseTime;
//                        String[] separated = currentString.split(" ");
//                        String tempTime = separated[1].substring(0, separated[1].length() - 3);
//                        selectCaseTime.setText(tempTime);
                            JSONArray diagArr = selectedCase.getJSONArray("diagnosis");
                            String temp = "";
                            if (diagArr.length() > 0) {
                                for (int j = 0; j < diagArr.length(); j++) {
                                    JSONObject diagObj = diagArr.getJSONObject(j);
                                    temp += diagObj.getString("diagnosis") + ",";
                                }
                            } else {
                                temp = "--";
                            }
                            selectCaseDiag.setText(temp);

                            episodeID = episodesArr.getJSONObject(i).getInt("id");
                            interactionChanged = false;

                            getEpisEncounter();//added by dileep
                            getWrittenNotesForEncounter();//added by dileep

                        }
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

//        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);
        temp = getIntent().getStringExtra("ApptDate");//mDay + " " + (mMonth+1) + " " + mYear;
        temp = appUtilities.changeDateFormat("yyyy-MM-dd", "dd MMM, yy", temp);
        selectCaseDate.setText(temp);
        selectCaseDateLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (caseDate.getText().equals("")) {
                    final Calendar c = Calendar.getInstance();
                    mYear = c.get(Calendar.YEAR);
                    mMonth = c.get(Calendar.MONTH);
                    mDay = c.get(Calendar.DAY_OF_MONTH);
                } else {
                    String date = appUtilities.changeDateFormat("dd MMM, yy", "dd MM yyyy", selectCaseDate.getText().toString());
                    String[] dSplit = date.split(" ");
                    mDay = Integer.parseInt(dSplit[0]);
                    mMonth = Integer.parseInt(dSplit[1]) - 1;
                    mYear = Integer.parseInt(dSplit[2]);
                }
                DatePickerDialog datePickerDialog = new DatePickerDialog(PatientRecordsCaseActivity.this,
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {

                                String temp = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;
                                temp = appUtilities.changeDateFormat("dd/MM/yyyy", "dd MMM, yy", temp);
                                selectCaseDate.setText(temp);

                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.show();
            }
        });

        mcurrentTime = Calendar.getInstance();
        mHour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
        mMinute = mcurrentTime.get(Calendar.MINUTE);
        tempTime = getIntent().getStringExtra("ApptTime");
        tempTime = tempTime.substring(0, 5);
        selectCaseTime.setText(tempTime);//.setText( mHour + ":" + mMinute);
        selectCaseTimeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Calendar mcurrentTime = Calendar.getInstance();
//                mHour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
//                mMinute = mcurrentTime.get(Calendar.MINUTE);
                String[] timeSplit = selectCaseTime.getText().toString().split(":");
                mHour = Integer.parseInt(timeSplit[0]);
                mMinute = Integer.parseInt(timeSplit[1]);
                TimePickerDialog mTimePicker = new TimePickerDialog(PatientRecordsCaseActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        selectCaseTime.setText(selectedHour + ":" + selectedMinute);
                    }
                }, mHour, mMinute, true);//Yes 24 hour time
//                mTimePicker.setTitle("Select Time");
                mTimePicker.show();
            }
        });

        createNewCase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                caseLayout.setVisibility(View.VISIBLE);
                caseSelectLayout.setVisibility(View.GONE);
                sectionProceed.setVisibility(View.GONE);

                showGuide(11);

                ZohoSalesIQ.Tracking.setCustomAction("AppointmentNotes - Case Select - New Case Form");
            }
        });

        headAbout.setTextColor(getResources().getColor(R.color.colorAccent));
        headCase.setTextColor(getResources().getColor(R.color.colorWhite));
        headHist.setTextColor(getResources().getColor(R.color.colorWhite));
        headNotes.setTextColor(getResources().getColor(R.color.colorWhite));
        headerAboutLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                headAbout.setTextColor(getResources().getColor(R.color.colorAccent));
                headCase.setTextColor(getResources().getColor(R.color.colorWhite));
                headHist.setTextColor(getResources().getColor(R.color.colorWhite));
                headNotes.setTextColor(getResources().getColor(R.color.colorWhite));
                sectionNum = 1;

                showGuide(5);

                aboutLayout.setVisibility(View.VISIBLE);
                caseLayout.setVisibility(View.GONE);
                caseSelectLayout.setVisibility(View.GONE);
                caseHistoryLayout.setVisibility(View.GONE);
                notesLayout.setVisibility(View.GONE);
                notesPrefrenceEmpty.setVisibility(View.GONE);

                ZohoSalesIQ.Tracking.setCustomAction("AppointmentNotes - About Section");
            }
        });

        headerCaseLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                headCase.setTextColor(getResources().getColor(R.color.colorAccent));
                headAbout.setTextColor(getResources().getColor(R.color.colorWhite));
                headHist.setTextColor(getResources().getColor(R.color.colorWhite));
                headNotes.setTextColor(getResources().getColor(R.color.colorWhite));
                sectionNum = 2;

                caseLayout.setVisibility(View.GONE);
                aboutLayout.setVisibility(View.GONE);
                caseSelectLayout.setVisibility(View.VISIBLE);
                caseHistoryLayout.setVisibility(View.GONE);
                notesLayout.setVisibility(View.GONE);
                sectionProceed.setVisibility(View.VISIBLE);
                notesPrefrenceEmpty.setVisibility(View.GONE);

                showGuide(7);

                ZohoSalesIQ.Tracking.setCustomAction("AppointmentNotes - Case Select Section");

                getEpisode();
            }
        });

        headerHistLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                if (!interactionChanged) {//old commented by dileep
//                if (interactionChanged) {

                ZohoSalesIQ.Tracking.setCustomAction("AppointmentNotes - History Section");

                if (!interactionChangeTwo) {
                    headHist.setTextColor(getResources().getColor(R.color.colorAccent));
                    headCase.setTextColor(getResources().getColor(R.color.colorWhite));
                    headAbout.setTextColor(getResources().getColor(R.color.colorWhite));
                    headNotes.setTextColor(getResources().getColor(R.color.colorWhite));
                    sectionNum = 3;

                    showGuide(13);

                    caseHistoryLayout.setVisibility(View.VISIBLE);
                    caseLayout.setVisibility(View.GONE);
                    caseSelectLayout.setVisibility(View.GONE);
                    aboutLayout.setVisibility(View.GONE);
                    notesLayout.setVisibility(View.GONE);
                    sectionProceed.setVisibility(View.VISIBLE);
                    notesPrefrenceEmpty.setVisibility(View.GONE);

                    historyTabClick = 1;

//                    if (ecntListArr != null) {
//                        ecntListArr.clear();
//                    }
//                    if (ecntIdArr != null) {
//                        ecntIdArr.clear();
//                    }

//                    getHistEpisode();


                    // getEpisEncounter(); // commented by dileep


                    if(treatPlanParentLayoutH.getVisibility()==View.VISIBLE)
                    {
                        treatPlanParentLayoutH.setVisibility(View.GONE);
                        recordEpisTreatmentArrowHistory.setImageDrawable(getResources().getDrawable(R.drawable.ic_arrow_right));

                    }


                    histSelectCase.setSelection(0);
                    histHandNoteModels.clear();
                    histHandNoteAdapter.notifyDataSetChanged();

                    notesHnNoteModels.clear();
                    notesHnNotesAdapter.notifyDataSetChanged();

                    getWrittenNotesForEncounter();
                    getWrittenNotes();

                    // getEpisTreatPlan();// added by dileep


                    symptomsModelH.clear();
                    investModelH.clear();
                    diagModelH.clear();
                    obsCatModelH.clear();

                    treatPlanModelH.clear();

                    getEpisEvalutaionForHistoryTab();
                    getEpisTreatPlanForHistoryTab();


                } else {

                    if (caseInteraction.getSelectedItemPosition() == 0 || flagProceedClick == 0) {
                        Toast.makeText(PatientRecordsCaseActivity.this, getResources().getString(R.string.interaction_changed), Toast.LENGTH_SHORT).show();
                    } else {


                        headHist.setTextColor(getResources().getColor(R.color.colorAccent));
                        headCase.setTextColor(getResources().getColor(R.color.colorWhite));
                        headAbout.setTextColor(getResources().getColor(R.color.colorWhite));
                        headNotes.setTextColor(getResources().getColor(R.color.colorWhite));
                        sectionNum = 3;

                        showGuide(13);

                        caseHistoryLayout.setVisibility(View.VISIBLE);
                        caseLayout.setVisibility(View.GONE);
                        caseSelectLayout.setVisibility(View.GONE);
                        aboutLayout.setVisibility(View.GONE);
                        notesLayout.setVisibility(View.GONE);
                        sectionProceed.setVisibility(View.VISIBLE);
                        notesPrefrenceEmpty.setVisibility(View.GONE);

//                    if (ecntListArr != null) {
//                        ecntListArr.clear();
//                    }
//                    if (ecntIdArr != null) {
//                        ecntIdArr.clear();
//                    }

//                    getHistEpisode();


                        // getEpisEncounter(); // commented by dileep

                        histSelectCase.setSelection(0);
                        histHandNoteModels.clear();
                        histHandNoteAdapter.notifyDataSetChanged();

                        notesHnNoteModels.clear();
                        notesHnNotesAdapter.notifyDataSetChanged();

                        getWrittenNotesForEncounter();
                        getWrittenNotes();

                        getEpisTreatPlan();// added by dileep


                    }
                }

//                } else {
//                    Toast.makeText(PatientRecordsCaseActivity.this, getResources().getString(R.string.interaction_changed), Toast.LENGTH_SHORT).show();
//                }
            }
        });

        headerNotesLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                if (!interactionChanged) {// commented by dileep
//                if (interactionChanged) {

                ZohoSalesIQ.Tracking.setCustomAction("AppointmentNotes - Notes Section");

                if (!interactionChangeTwo) {
                    headNotes.setTextColor(getResources().getColor(R.color.colorAccent));
                    headCase.setTextColor(getResources().getColor(R.color.colorWhite));
                    headHist.setTextColor(getResources().getColor(R.color.colorWhite));
                    headAbout.setTextColor(getResources().getColor(R.color.colorWhite));
                    sectionNum = 4;

                    historyTabClick = 0;// added by dileep 25th june 2019

                    showGuide(14);

                    notesLayout.setVisibility(View.VISIBLE);
                    aboutLayout.setVisibility(View.GONE);
                    caseLayout.setVisibility(View.GONE);
                    caseSelectLayout.setVisibility(View.GONE);
                    caseHistoryLayout.setVisibility(View.GONE);
                    sectionProceed.setVisibility(View.GONE);

                    getWrittenNotesForEncounter();
                    getEpisodicPref();
                    getEpisEncounter();

                } else {
                    if (caseInteraction.getSelectedItemPosition() == 0 || flagProceedClick == 0) {
                        Toast.makeText(PatientRecordsCaseActivity.this, getResources().getString(R.string.interaction_changed), Toast.LENGTH_SHORT).show();
                    } else {


                        headNotes.setTextColor(getResources().getColor(R.color.colorAccent));
                        headCase.setTextColor(getResources().getColor(R.color.colorWhite));
                        headHist.setTextColor(getResources().getColor(R.color.colorWhite));
                        headAbout.setTextColor(getResources().getColor(R.color.colorWhite));
                        sectionNum = 4;

                        showGuide(14);
                        notesLayout.setVisibility(View.VISIBLE);
                        aboutLayout.setVisibility(View.GONE);
                        caseLayout.setVisibility(View.GONE);
                        caseSelectLayout.setVisibility(View.GONE);
                        caseHistoryLayout.setVisibility(View.GONE);
                        sectionProceed.setVisibility(View.GONE);

                        getWrittenNotesForEncounter();
                        getEpisodicPref();
                        getEpisEncounter();
                    }
                }

//                } else {
//                    Toast.makeText(PatientRecordsCaseActivity.this, getResources().getString(R.string.interaction_changed), Toast.LENGTH_SHORT).show();
//                }
            }
        });

        histSelectCase.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                try {
//                    episodeID = episodesArr.getJSONObject(i).getInt("id");
//                    histHandNoteModels.clear();
//                    getWrittenNotes();


                    ZohoSalesIQ.Tracking.setCustomAction("AppointmentNotes - History - Case Changed");
                    if (ecntIdArr.get(i) == 0) {
                        encounterID = 0;
                    } else {
                        encounterID = ecntIdArr.get(i);
                    }

                    histHandNoteModels.clear();

                    symptomsModelH.clear();
                    investModelH.clear();
                    diagModelH.clear();
                    obsCatModelH.clear();

                    treatPlanModelH.clear();

                    getWrittenNotes();
                    getEpisEvalutaionForHistoryTab();
                    getEpisTreatPlanForHistoryTab();


//                    getEpisWrittenNote();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        uploadNotes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                notesHnMedPres.setSelection(0);
                notesHnTestPres.setSelection(0);
                notesHnDesp.setText("");
                uploadImageResponse = "";

                ZohoSalesIQ.Tracking.setCustomAction("AppointmentNotes - Notes - Upload Notes Form");

                notesMainLayout.setVisibility(View.GONE);
                notesFormLayout.setVisibility(View.VISIBLE);
                sectionProceed.setVisibility(View.GONE);
            }
        });

        notesHnUploadFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                openFileDialog();
                ZohoSalesIQ.Tracking.setCustomAction("AppointmentNotes - Notes - File Upload - Select File Dialog");
                selectMethodDialog();
            }
        });

//        c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);
        temp = mDay + " " + (mMonth + 1) + " " + mYear;
        temp = appUtilities.changeDateFormat("dd MM yyyy", "dd MMM, yy", temp);
        notesHnValid.setText(temp);
        notesHnValid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (notesHnValid.getText().equals("")) {
                    final Calendar c = Calendar.getInstance();
                    mYear = c.get(Calendar.YEAR);
                    mMonth = c.get(Calendar.MONTH);
                    mDay = c.get(Calendar.DAY_OF_MONTH);
                } else {
                    String date = appUtilities.changeDateFormat("dd MMM, yy", "dd MM yyyy", notesHnValid.getText().toString());
                    String[] dSplit = date.split(" ");
                    mDay = Integer.parseInt(dSplit[0]);
                    mMonth = Integer.parseInt(dSplit[1]) - 1;
                    mYear = Integer.parseInt(dSplit[2]);
                }
                DatePickerDialog datePickerDialog = new DatePickerDialog(PatientRecordsCaseActivity.this,
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {

                                String temp = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;
                                temp = appUtilities.changeDateFormat("dd/MM/yyyy", "dd MMM, yy", temp);
                                notesHnValid.setText(temp);

                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.show();
            }
        });

        notesHnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ZohoSalesIQ.Tracking.setCustomAction("AppointmentNotes - Notes - File Upload - Saving Notes");

                try {
                    if (!uploadImageResponse.equals("")) {
                        JSONObject noteObj = new JSONObject();
                        noteObj.put("created_by", docId);
                        noteObj.put("description", notesHnDesp.getText().toString());
                        noteObj.put("encounter_id", encounterId);
                        noteObj.put("episode_id", episodeID);
                        noteObj.put("file_url", uploadImageResponse);

                        int temp = 0;
                        if (notesHnTestPres.getSelectedItem().equals("Yes")) {
                            temp = 1;
                        }
                        noteObj.put("has_test_prescription", temp);

                        temp = 0;
                        if (notesHnMedPres.getSelectedItem().equals("Yes")) {
                            temp = 1;
                        }
                        noteObj.put("has_med_prescription", temp);

                        noteObj.put("med_prescription_valid_till", "");
                        if (notesHnValid.getVisibility() == View.VISIBLE) {
                            String date = appUtilities.changeDateFormat("dd MMM, yy", "yyyy-MM-dd mm:HH", notesHnValid.getText().toString());
                            noteObj.put("med_prescription_valid_till", date);
                        }
                        noteObj.put("patient_id", patientId);

                        saveNotesHnData(noteObj);
                    } else {
                        Toast.makeText(PatientRecordsCaseActivity.this, getResources().getString(R.string.please_attach_url), Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });

        aa = new ArrayAdapter(this, android.R.layout.simple_spinner_item, yesNo);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Setting the ArrayAdapter data on the Spinner
        notesHnMedPres.setAdapter(aa);
        notesHnMedPres.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (yesNo[i].equals("Yes")) {
                    notesHnValid.setVisibility(View.VISIBLE);
                    notesHnValidTillLabel.setVisibility(View.VISIBLE);
                } else {
                    notesHnValid.setVisibility(View.GONE);
                    notesHnValidTillLabel.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        aa = new ArrayAdapter(this, android.R.layout.simple_spinner_item, yesNo);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Setting the ArrayAdapter data on the Spinner
        notesHnTestPres.setAdapter(aa);
        notesHnTestPres.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        notesHnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                notesMainLayout.setVisibility(View.VISIBLE);
                notesFormLayout.setVisibility(View.GONE);

                ZohoSalesIQ.Tracking.setCustomAction("AppointmentNotes - Notes - File Upload - Back");
            }
        });
    }

    private void getRecordPref() {
        patientRecordsApi.getRecordPref(ApiUrls.getEpisodicNonEpisodicPref, "", this, new VolleyCallback() {
            @Override
            public void onSuccess(String result) {
//                Log.d("Success Response", result);
            }

            @Override
            public void onError(String err) {
                ErrorHandlerClass.INSTANCE.errorHandler(PatientRecordsCaseActivity.this, err);
            }
        });
    }

    private void getFamilyDetails() {

        String url = ApiUrls.getFamilyList + "?doctor_id=" + docId + "&patient_id=" + patientId + "&page=1&per_page=10";

        patientRecordsApi.getRecordPref(url, "", this, new VolleyCallback() {
            @Override
            public void onSuccess(String result) {
//                Log.d("Success Response", result);

                try {
                    JSONObject resultObj = new JSONObject(result);
                    JSONArray familyArr = resultObj.getJSONObject("response").getJSONObject("existing_relations").getJSONArray("data");
                    if (familyArr.length() > 0) {
                        familyEmptyText.setVisibility(View.GONE);
                        for (int i = 0; i < familyArr.length(); i++) {
                            PatientRecordsModel model = new PatientRecordsModel();
                            JSONObject familyObj = familyArr.getJSONObject(i);
                            model.setFamName(familyObj.getString("relative_name"));
                            model.setFamAge(familyObj.getString("relative_age"));
                            model.setFamRelation(familyObj.getJSONObject("relation").getString("relation_name"));
                            model.setType(2);
                            familyModels.add(model);
                        }

                        familyAdapter.notifyDataSetChanged();
                    } else {
                        familyEmptyText.setVisibility(View.VISIBLE);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(String err) {
                ErrorHandlerClass.INSTANCE.errorHandler(PatientRecordsCaseActivity.this, err);

            }
        });
    }

    private void getNonepisodicData() {
        String url = ApiUrls.getEpisodicCatWithRecord + "?patient_id=" + patientId + "&sharedOrCreated=1";
        patientRecordsApi.getRecordPref(url, "", this, new VolleyCallback() {
            @Override
            public void onSuccess(String result) {

                try {
                    JSONObject resultObj = new JSONObject(result);
                    JSONArray catArr = resultObj.getJSONObject("response").getJSONArray("categories");
                    for (int i = 0; i < catArr.length(); i++) {
                        JSONObject catObj = catArr.getJSONObject(i);
                        if (catObj.getInt("active") == 1) {
                            PatientRecordsModel model = new PatientRecordsModel();
                            model.setCatName(catObj.getJSONObject("record_categories").getString("category"));
                            model.setRecordCount(catObj.getJSONArray("records").length());
//                            Log.d("@@@@@@", resultObj.getJSONObject("response").getJSONObject("field-dictionary").
//                                    getJSONArray("1").toString());
                            model.setFieldDictionary(resultObj.getJSONObject("response").getJSONObject("field-dictionary").
                                    getJSONArray(catObj.getJSONObject("record_categories").getString("id")).toString());
                            model.setType(1);
                            model.setCatRecordData(catObj.getJSONArray("records").toString());
                            patientRecordsModels.add(model);
                        }

                        aboutAdapter.notifyDataSetChanged();

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onError(String err) {
                ErrorHandlerClass.INSTANCE.errorHandler(PatientRecordsCaseActivity.this, err);

            }
        });
    }

    private void getPatientDetails() {

        String url = ApiUrls.getPatientBackground + "?patient_id=" + patientId;
        patientRecordsApi.getRecordPref(url, "", this, new VolleyCallback() {
            @Override
            public void onSuccess(String result) {
                try {
                    JSONObject resObj = new JSONObject(result);
                    JSONObject patientObj = resObj.getJSONObject("response");
                    updatePatientId = patientObj.getInt("id");
                    pName.setText(patientObj.getString("name"));
                    pPhNo.setText(patientObj.getString("phone"));
                    pEmail.setText(patientObj.getString("email"));
                    pAge.setText(patientObj.getString("age"));
                    pHeight.setText(patientObj.getString("height"));
                    pId.setText(patientObj.getString("generalid"));

                    int gender = patientObj.getInt("gender");
                    if (gender == 1) {
                        RadioButton button = findViewById(R.id.recordPGenderMale);
                        button.setChecked(true);
                    } else if (gender == 2) {
                        RadioButton button = findViewById(R.id.recordPGenderFemale);
                        button.setChecked(true);
                    } else {

                    }

                    ArrayAdapter myAdap = (ArrayAdapter) pBloodG.getAdapter(); //cast to an ArrayAdapter
                    int spinnerPosition = myAdap.getPosition(patientObj.getString("blood_group"));
                    pBloodG.setSelection(spinnerPosition);

                    String date = appUtilities.changeDateFormat("yyyy-MM-dd", "dd MMM, yy", patientObj.getString("dob"));
                    pDOB.setText(date);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(String err) {
                ErrorHandlerClass.INSTANCE.errorHandler(PatientRecordsCaseActivity.this, err);

            }
        });
    }

    private void saveNewEpisode() {
        JSONObject reqObj = new JSONObject();
        try {
            reqObj.put("created_by", docId);
            reqObj.put("patient_id", patientId);
            reqObj.put("episode_name", caseName.getText().toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        patientRecordsApi.postRecords(ApiUrls.saveNewEpisode, reqObj, this, new VolleyCallback() {
            @Override
            public void onSuccess(String result) {
                try {
                    JSONObject resObj = new JSONObject(result);
                    if (resObj.has("response")) {
//                        caseLayout.setVisibility(View.GONE);
//                        caseSelectLayout.setVisibility(View.VISIBLE);
//                        sectionProceed.setVisibility(View.VISIBLE);
                        newInteractionFlagId = resObj.getInt("response");

                        JSONObject reqObj = new JSONObject();
                        try {
                            reqObj.put("patient_id", patientId);
                            reqObj.put("episode_id", resObj.getString("response"));
                            reqObj.put("encounter_mode", caseType.getSelectedItem().toString());

                            String date = appUtilities.changeDateFormat("dd MMM, yy", "dd-MM-yyyy", caseDate.getText().toString());
                            String dateTime = date + " " + caseTime.getText().toString();
                            reqObj.put("encounter_date_time", dateTime);
                            reqObj.put("appointment_id", apptId);
                            reqObj.put("chat_id", 0);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        saveEncounter(reqObj);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(String err) {
                ErrorHandlerClass.INSTANCE.errorHandler(PatientRecordsCaseActivity.this, err);

            }
        });
    }

    private void getEpisode() {

        String url = ApiUrls.getEpisodes + "?patient_id=" + patientId;
        patientRecordsApi.getRecordPref(url, "", this, new VolleyCallback() {
            @Override
            public void onSuccess(String result) {
                try {
                    JSONObject resObj = new JSONObject(result);
                    episodesArr = resObj.getJSONArray("response");//new JSONArray(resObj.getJSONArray("response"));
                    String[] episodes = new String[episodesArr.length()];

                    if (episodes.length > 0) {
                        for (int i = 0; i < episodesArr.length(); i++) {
                            JSONObject epic = episodesArr.getJSONObject(i);
                            episodes[i] = epic.getString("episode_name");
                        }

                        ArrayAdapter aa = new ArrayAdapter(PatientRecordsCaseActivity.this, android.R.layout.simple_spinner_item, episodes);
                        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        //Setting the ArrayAdapter data on the Spinner
                        selectCase.setAdapter(aa);

                        for (int m = 0; m < episodesArr.length(); m++) {
                            JSONObject epi1 = episodesArr.getJSONObject(m);
                            if (episodeID == epi1.getInt("id")) {
                                selectCase.setSelection(m);
                            }
                        }


//                        DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//                        DateFormat outputFormat = new SimpleDateFormat("dd MMM, yyyy");
//                        String inputDateStr=episodesArr.getJSONObject(0).getJSONObject("last_encountered_on").getString("encounter_date_time");
//                        Date date = inputFormat.parse(inputDateStr);
//                        String outputDateStr = outputFormat.format(date);


//                        noOfInteraction.setText(episodesArr.getJSONObject(0).getString("encounterCount"));
//                        String date = appUtilities.changeDateFormat("yyyy-MM-dd mm:HH:ss", "dd MMM, yy", episodesArr.getJSONObject(0).getJSONObject("last_encountered_on").getString("encounter_date_time"));
//                        caseLastInteraction.setText(outputDateStr);

//                        String encounterMode = episodesArr.getJSONObject(0).getJSONObject("last_encountered_on").getString("encounter_mode");
//
//                        for (int i = 0; i < interactionMode.length; i++) {
//                            if(interactionMode[i].toString().equalsIgnoreCase(encounterMode))
//                            {
//                                caseInteraction.setSelection(i);
//                            }
//
//                        }
//
//                        String time = episodesArr.getJSONObject(0).getJSONObject("last_encountered_on").getString("encounter_date_time");
//
//                        String currentString = time;
//                        String[] separated = currentString.split("");
//
//                        String timeAt = appUtilities.changeDateFormat("mm:HH:ss", "mm:HH",  separated[1]);
//
//                        selectCaseDate.setText(date);
//                                selectCaseTime.setText(timeAt);


                        JSONArray diagArr = episodesArr.getJSONObject(0).getJSONArray("diagnosis");
                        String temp = "";
                        if (diagArr.length() > 0) {
                            for (int j = 0; j < diagArr.length(); j++) {
                                JSONObject diagObj = diagArr.getJSONObject(j);
                                temp += diagObj.getString("diagnosis") + ",";
                            }
                        } else {
                            temp = "--";
                        }
                        selectCaseDiag.setText(temp);

                        //episodeID = episodesArr.getJSONObject(0).getInt("id");

                        for (int i = 0; i < episodesArr.length(); i++) {
                            JSONObject epic = episodesArr.getJSONObject(i);
//                            Log.d("Selected", "&###############");
                            if (newInteractionFlagId != 0) {
                                if (epic.getInt("id") == newInteractionFlagId) {
//                                    Log.d("Selected", "&&&&&&&&&&&&&&" + episodeID);
                                    selectCase.setSelection(i);
//                                    Log.d("Selected case", selectCase.getSelectedItem().toString());
                                }
                            } else {

//                                if (epic.getInt("id") == episodeID) {
//                                    Log.d("Selected", "&&&&&&&&&&&&&&" + episodeID);
//                                    selectCase.setSelection(i);
//                                    // interactionSelectedPosition = i;
//
//
//                                    Log.d("Selected case", selectCase.getSelectedItem().toString());
//                                }
                            }
                        }
                    } else {
                        caseLayout.setVisibility(View.VISIBLE);
                        caseSelectLayout.setVisibility(View.GONE);
                        sectionProceed.setVisibility(View.GONE);
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(String err) {
                ErrorHandlerClass.INSTANCE.errorHandler(PatientRecordsCaseActivity.this, err);

            }
        });
    }

    private void saveEncounter(final JSONObject reqObj) {

        patientRecordsApi.postRecords(ApiUrls.saveNewInteraction, reqObj, this, new VolleyCallback() {
            @Override
            public void onSuccess(String result) {
//                Log.d("Interaction Success", result);
                interactionChanged = false;

                try {
                    JSONObject resObj = new JSONObject(result);
                    encounterId = resObj.getJSONObject("response").getInt("id");
                    // episodeID = resObj.getJSONObject("response").getInt("episode_id");//added by dileep
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (sectionNum == 2) {
                    caseLayout.setVisibility(View.GONE);
//                    caseHistoryLayout.setVisibility(View.VISIBLE);
//                    sectionProceed.setVisibility(View.VISIBLE);
                    notesLayout.setVisibility(View.VISIBLE);
                    headerHistLayout.setEnabled(true);
                    headerNotesLayout.setEnabled(true);
                    getEpisode();
                    showGuide(14);
//                    getHistEpisode();

//                    if (ecntListArr != null) {
//                        ecntListArr.clear();
//                    }
//                    if (ecntIdArr != null) {
//                        ecntIdArr.clear();
//                    }
                    getWrittenNotesForEncounter();
                    getEpisEncounter();
                    // sectionNum = 3;//commented by dileep
                } else if (sectionNum == 3) {
                    //caseSelectLayout.setVisibility(View.GONE);
                    //caseHistoryLayout.setVisibility(View.VISIBLE);
                    // sectionProceed.setVisibility(View.GONE);
//                    getHistEpisode();
                    getEpisEncounter();//commented by dileep
                    showGuide(14);

                    caseLayout.setVisibility(View.GONE);
//                    caseHistoryLayout.setVisibility(View.VISIBLE);
                    sectionProceed.setVisibility(View.GONE);
                    notesLayout.setVisibility(View.VISIBLE);
                    headerHistLayout.setEnabled(true);
                    headerNotesLayout.setEnabled(true);


                    sectionNum = 3;
                }

//                headHist.setTextColor(getResources().getColor(R.color.colorAccent));
//                headCase.setTextColor(getResources().getColor(R.color.colorGreyText));
//                headAbout.setTextColor(getResources().getColor(R.color.colorGreyText));
//                headNotes.setTextColor(getResources().getColor(R.color.colorGreyText));

                headHist.setTextColor(getResources().getColor(R.color.colorWhite));
                headCase.setTextColor(getResources().getColor(R.color.colorWhite));
                headAbout.setTextColor(getResources().getColor(R.color.colorWhite));
                headNotes.setTextColor(getResources().getColor(R.color.colorAccent));

                appointmentSharedOption.setVisibility(View.VISIBLE);// added by dileep
                appointmentSaveAsPrescription.setVisibility(View.VISIBLE);

            }

            @Override
            public void onError(String err) {
                ErrorHandlerClass.INSTANCE.errorHandler(PatientRecordsCaseActivity.this, err);

            }
        });
    }

    private void checkApptInteraction() {
        String url = ApiUrls.checkApptInteraction + "?appt_id=" + apptId;
        patientRecordsApi.getRecordPref(url, "", this, new VolleyCallback() {
            @Override
            public void onSuccess(String result) {
                try {
//                    Log.d("Interaction Check", result);
                    JSONObject resObj = new JSONObject(result);
                    if (resObj.getBoolean("response")) {

                        headHist.setTextColor(getResources().getColor(R.color.colorWhite));
                        headCase.setTextColor(getResources().getColor(R.color.colorWhite));
                        headNotes.setTextColor(getResources().getColor(R.color.colorAccent));
                        headAbout.setTextColor(getResources().getColor(R.color.colorWhite));
                        appointmentSharedOption.setVisibility(View.VISIBLE);// added by dileep
                        appointmentSaveAsPrescription.setVisibility(View.VISIBLE);


//                        caseHistoryLayout.setVisibility(View.VISIBLE);
//                        aboutLayout.setVisibility(View.GONE);
//                        caseLayout.setVisibility(View.GONE);
//                        caseSelectLayout.setVisibility(View.GONE);
//                        notesLayout.setVisibility(View.GONE);


                        caseHistoryLayout.setVisibility(View.GONE);
                        aboutLayout.setVisibility(View.GONE);
                        caseLayout.setVisibility(View.GONE);
                        caseSelectLayout.setVisibility(View.GONE);
                        notesLayout.setVisibility(View.VISIBLE);

                        showGuide(14);


                        sectionProceed.setVisibility(View.GONE);
                        headerHistLayout.setEnabled(true);
                        headerNotesLayout.setEnabled(true);

//                        getHistEpisode();
//                        getEpisEncounter();

                        //    getWrittenNotesForEncounter();//by dileep
                        getEpisodicPref();
                        //sectionNum = 3;
                        sectionNum = 4;
                    } else {
                        appointmentSharedOption.setVisibility(View.GONE);// added by dileep
                        appointmentSaveAsPrescription.setVisibility(View.GONE);
                        checkPatientEpisode();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(String err) {
                ErrorHandlerClass.INSTANCE.errorHandler(PatientRecordsCaseActivity.this, err);

            }
        });
    }

    private void checkPatientEpisode() {
        String url = ApiUrls.checkPatientEpisode + "?patient_id=" + patientId;
        patientRecordsApi.getRecordPref(url, "", this, new VolleyCallback() {
            @Override
            public void onSuccess(String result) {
                try {
                    JSONObject resObj = new JSONObject(result);
                    if (resObj.getBoolean("response")) {
                        headCase.setTextColor(getResources().getColor(R.color.colorAccent));
                        headAbout.setTextColor(getResources().getColor(R.color.colorWhite));
                        headHist.setTextColor(getResources().getColor(R.color.colorWhite));
                        headNotes.setTextColor(getResources().getColor(R.color.colorWhite));
                        sectionNum = 2;

                        caseSelectLayout.setVisibility(View.VISIBLE);
                        aboutLayout.setVisibility(View.GONE);
                        notesLayout.setVisibility(View.GONE);
                        caseLayout.setVisibility(View.GONE);
                        caseHistoryLayout.setVisibility(View.GONE);

                        showGuide(7);

                        headerHistLayout.setEnabled(true);
                        headerNotesLayout.setEnabled(true);

                        getEpisode();
                    } else {
                        headAbout.setTextColor(getResources().getColor(R.color.colorAccent));
                        headCase.setTextColor(getResources().getColor(R.color.colorWhite));
                        headHist.setTextColor(getResources().getColor(R.color.colorWhite));
                        headNotes.setTextColor(getResources().getColor(R.color.colorWhite));

                        aboutLayout.setVisibility(View.VISIBLE);
                        caseLayout.setVisibility(View.GONE);
                        caseSelectLayout.setVisibility(View.GONE);
                        caseHistoryLayout.setVisibility(View.GONE);
                        notesLayout.setVisibility(View.GONE);
                        sectionNum = 1;

                        showGuide(5);


                        headerHistLayout.setEnabled(false);
                        headerNotesLayout.setEnabled(false);


                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(String err) {
                ErrorHandlerClass.INSTANCE.errorHandler(PatientRecordsCaseActivity.this, err);

            }
        });
    }

    private void getWrittenNotes() {
        histHandNoteEmpty.setVisibility(View.GONE);
//        histHandNoteRv.setVisibility(View.VISIBLE);

//        String url = appConfigClass.getWrittenNotes + "?patient_id=" + patientId + "&episode_id=" + episodeID + "&sharedOrCreated=1";
        String url = ApiUrls.getWrittenNotes + "?doctor_id=" + ApiUrls.doctorId + "&episode_id=" + episodeID + "&patient_id=" + patientId + "&sharedOrCreated=1&encounter_id=" + encounterID;
        if (encounterID != 0) {
            url = ApiUrls.getEcntWrittenNotes + "?doctor_id=" + ApiUrls.doctorId + "&episode_id=" + episodeID + "&patient_id=" + patientId + "&sharedOrCreated=1&encounter_id=" + encounterID;
        }

        patientRecordsApi.getRecordPref(url, "", this, new VolleyCallback() {
            @Override
            public void onSuccess(String result) {
                try {
                    JSONObject resObj = new JSONObject(result);
                    JSONArray resArr = resObj.getJSONArray("response");
                    if (resArr.length() > 0) {
                        histHnCount.setText(resArr.length() + "");
//                        Log.d("Hand Note", "PRESSSSSSSSSSSSSSSSSSSSSSSSSS");
                        for (int i = 0; i < resArr.length(); i++) {
                            JSONObject notesObj = resArr.getJSONObject(i);
                            PatientRecordsModel model = new PatientRecordsModel();
                            model.setHnAttachURL(notesObj.getString("file_url"));
                            model.setHnDesp(notesObj.getString("description"));
                            model.setHnMedPres(getResources().getString(R.string.no));
                            if (notesObj.getInt("has_med_prescription") == 1) {
                                model.setHnMedPres(getResources().getString(R.string.yes));
                            }
                            model.setHnTestPres(getResources().getString(R.string.no));
                            if (notesObj.getInt("has_test_prescription") == 1) {
                                model.setHnTestPres(getResources().getString(R.string.yes));
                            }
                            model.setHnValidTill("");
                            if (notesObj.getString("med_prescription_valid_till") != null &&
                                    !notesObj.getString("med_prescription_valid_till").equalsIgnoreCase("null") &&
                                    !notesObj.getString("med_prescription_valid_till").equalsIgnoreCase("")) {
                                String date = appUtilities.changeDateFormat("yyyy-MM-dd mm:HH:ss", "dd MMM, yy", notesObj.getString("med_prescription_valid_till"));
                                model.setHnValidTill(date);
                            }
                            model.setType(1);

                            histHandNoteModels.add(model);
                        }

                        histHandNoteAdapter.notifyDataSetChanged();
                    } else {

                        histHnCount.setText("0");// added by dileep
                        histHandNoteAdapter.notifyDataSetChanged();
                        histHandNoteEmpty.setVisibility(View.VISIBLE);
//                        histHandNoteRv.setVisibility(View.GONE);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    histHandNoteEmpty.setVisibility(View.VISIBLE);
//                    histHandNoteRv.setVisibility(View.GONE);
                }
            }

            @Override
            public void onError(String err) {
                histHandNoteEmpty.setVisibility(View.VISIBLE);
                histHandNoteRv.setVisibility(View.GONE);
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

        patientRecordsApi.postRecords(ApiUrls.getFileFromUrl, url, this, new VolleyCallback() {
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
                ErrorHandlerClass.INSTANCE.errorHandler(PatientRecordsCaseActivity.this, err);

            }
        });
    }

    private void getHistEpisode() {

        String url = ApiUrls.getEpisodes + "?patient_id=" + patientId;
        patientRecordsApi.getRecordPref(url, "", this, new VolleyCallback() {
            @Override
            public void onSuccess(String result) {
                try {
                    JSONObject resObj = new JSONObject(result);
                    episodesArr = resObj.getJSONArray("response");//new JSONArray(resObj.getJSONArray("response"));
                    String[] episodes = new String[episodesArr.length()];
                    for (int i = 0; i < episodesArr.length(); i++) {
                        JSONObject epic = episodesArr.getJSONObject(i);
                        episodes[i] = epic.getString("episode_name");
                    }

                    ArrayAdapter aa = new ArrayAdapter(PatientRecordsCaseActivity.this, android.R.layout.simple_spinner_item, episodes);
                    aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    //Setting the ArrayAdapter data on the Spinner
                    histSelectCase.setAdapter(aa);

//                    noOfInteraction.setText(episodesArr.getJSONObject(0).getString("encounterCount"));
//                    String date = appUtilities.changeDateFormat("yyyy-MM-dd mm:HH:ss", "dd MMM, yy", episodesArr.getJSONObject(0).getJSONObject("last_encountered_on").getString("encounter_date_time"));
//                    caseLastInteraction.setText(date);
//                    JSONArray diagArr = episodesArr.getJSONObject(0).getJSONArray("diagnosis");
//                    String temp = "";
//                    if(diagArr.length() > 0 ) {
//                        for (int j = 0; j < diagArr.length(); j++) {
//                            JSONObject diagObj = diagArr.getJSONObject(j);
//                            temp += diagObj.getString("diagnosis") + ",";
//                        }
//                    } else {
//                        temp = "--";
//                    }
//                    selectCaseDiag.setText(temp);

//                    episodeID = episodesArr.getJSONObject(0).getInt("id");

                    for (int i = 0; i < episodesArr.length(); i++) {
                        JSONObject epic = episodesArr.getJSONObject(i);
                        if (epic.getInt("id") == episodeID) {
                            histSelectCase.setSelection(i);
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(String err) {
                ErrorHandlerClass.INSTANCE.errorHandler(PatientRecordsCaseActivity.this, err);

            }
        });
    }

    private void uploadImage() {
        String url = ApiUrls.uploadRecordImage;

        loadingDialog = new ProgressDialog(this);
        loadingDialog.setMessage(getResources().getString(R.string.uploading_image));
        loadingDialog.setCancelable(false);
        loadingDialog.setInverseBackgroundForced(false);
        loadingDialog.show();

        //our custom volley request
        AppImageUploader volleyMultipartRequest = new AppImageUploader(Request.Method.POST, url,
                new Response.Listener<NetworkResponse>() {
                    @Override
                    public void onResponse(NetworkResponse response) {
                        try {

                            JSONObject rootObj = new JSONObject(new String(response.data));
//                            Log.d("Image Upload", rootObj.toString());
                            rootObj = rootObj.getJSONObject("response");
                            String url = rootObj.getString("url");
                            if (url != null && !url.equals("")) {
                                uploadImageResponse = url;
                                Toast.makeText(getApplicationContext(), getResources().getString(R.string.upload_img_success), Toast.LENGTH_SHORT).show();
                            }

                            loadingDialog.dismiss();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                            Toast.makeText(getApplicationContext(),
                                    getResources().getString(R.string.slow_internet_connection),
                                    Toast.LENGTH_LONG).show();
                        } else if (error instanceof AuthFailureError) {
                            Toast.makeText(getApplicationContext(),
                                    getResources().getString(R.string.slow_internet_connection),
                                    Toast.LENGTH_LONG).show();


                        } else if (error instanceof ServerError) {
                            Toast.makeText(getApplicationContext(),
                                    getResources().getString(R.string.slow_internet_connection),
                                    Toast.LENGTH_LONG).show();

                            //TODO
                        } else if (error instanceof NetworkError) {
                            Toast.makeText(getApplicationContext(),
                                    getResources().getString(R.string.slow_internet_connection),
                                    Toast.LENGTH_LONG).show();

                            //TODO
                        } else if (error instanceof ParseError) {
                            Toast.makeText(getApplicationContext(),
                                    getResources().getString(R.string.slow_internet_connection),
                                    Toast.LENGTH_LONG).show();

                            //TODO
                        } else {
                            Toast.makeText(getApplicationContext(), getResources().getString(R.string.upload_img_error), Toast.LENGTH_SHORT).show();

                        }


                        loadingDialog.dismiss();
                    }
                }) {

            /*
             * If you want to add more parameters with the image
             * you can do it here
             * here we have only one parameter with the image
             * which is tags
             * */
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("path", "records/" + episodeID + "/");
                return params;
            }

            /*
             * Here we are passing image by renaming it with a unique name
             * */
            @Override
            protected Map<String, DataPart> getByteData() {
                Map<String, DataPart> params = new HashMap<>();
//                long imagename = System.currentTimeMillis();
                if (isPDFFile) {
                    try {
                        byte[] bytesArray = new byte[(int) pdfFile.length()];
                        FileInputStream fis = new FileInputStream(pdfFile);
                        int a = fis.read(bytesArray); //read file into bytes[]
                        fis.close();
                        params.put("file", new DataPart("Record" + ".pdf", bytesArray));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    Bitmap bitmap = ((BitmapDrawable) notesHnFormImage.getDrawable()).getBitmap();
                    params.put("file", new DataPart("Record" + ".png", getFileDataFromDrawable(bitmap)));
                }
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<>();
//                headers.put("Content-Type", "application/json");
                headers.put("App-Origin", ApiUrls.appOrigin);
                headers.put("Authorization", "Bearer " + ApiUrls.loginToken);//eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJodHRwOi8vNGRkYjMxNjkubmdyb2suaW8vYXBpL3YxL2F1dGgvdG9rZW4iLCJpYXQiOjE1MTgwODcyMzAsImV4cCI6MTUxOTI5NjgzMCwibmJmIjoxNTE4MDg3MjMwLCJqdGkiOiJ2UEJnY09hVGdGOU9YaFh4Iiwic3ViIjozMzk1LCJwcnYiOiIyM2JkNWM4OTQ5ZjYwMGFkYjM5ZTcwMWM0MDA4NzJkYjdhNTk3NmY3In0.Y3wU_NBXXhw--b75x4iwo6fmHQQzMLkla2gHrheWqeU");
                return headers;
            }
        };

        //adding the request to volley
        Volley.newRequestQueue(this).add(volleyMultipartRequest);
    }

    private void openFileDialog() {

        Intent intent = new Intent();
// Show only images, no videos or anything else
        intent.setType("*/*");
        String[] mimeTypes = {"image/*", "application/pdf"};
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
        intent.setAction(Intent.ACTION_GET_CONTENT);
// Always show the chooser (if there are multiple options available)
        startActivityForResult(Intent.createChooser(intent, "Select File"), 2);
    }

    public void openCamera() {
//        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        File photo = new File(Environment.getExternalStorageDirectory(),  "Pic.jpg");
//        intent.putExtra(MediaStore.EXTRA_OUTPUT,
//                Uri.fromFile(photo));
//        fileUri = Uri.fromFile(photo);
//        startActivityForResult(intent, 1);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File photo = new File(Environment.getExternalStorageDirectory(), "Pic1.png");
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            fileUri = Uri.fromFile(photo);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
        } else {
//            File file = new File(Uri.fromFile(photo).getPath());
//            fileUri = FileProvider.getUriForFile(getApplicationContext(), getApplicationContext().getPackageName() + ".provider", file);
//            Log.d("File Url", fileUri.getPath());
//            intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);

//            Intent pictureIntent = new Intent(
//                    MediaStore.ACTION_IMAGE_CAPTURE);
            if (intent.resolveActivity(getPackageManager()) != null) {
                //Create a file to store the image
                File photoFile = null;
                try {
                    photoFile = createImageFile();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                if (photoFile != null) {
                    fileUri = FileProvider.getUriForFile(this, getApplicationContext().getPackageName() + ".provider", photoFile);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT,
                            fileUri);
//                    startActivityForResult(pictureIntent,
//                            100);
                }
            }
        }
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        if (intent.resolveActivity(getApplicationContext().getPackageManager()) != null) {
            startActivityForResult(intent, 1);
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 1:
                if (resultCode == Activity.RESULT_OK) {

                    //new
                    try {
                        ContentResolver contentResolver = getContentResolver();

                        // Use the content resolver to open camera taken image input stream through image uri.
                        InputStream inputStream = contentResolver.openInputStream(fileUri);

                        // Decode the image input stream to a bitmap use BitmapFactory.
                        Bitmap pictureBitmap = BitmapFactory.decodeStream(inputStream);
                        int nh = (int) (pictureBitmap.getHeight() * (720.0 / pictureBitmap.getWidth()));
                        Bitmap scaled = Bitmap.createScaledBitmap(pictureBitmap, 720, nh, true);
                        notesHnFormImage.setImageBitmap(scaled);

                        // Set the camera taken image bitmap clinicplus the image view component to display.
                        notesHnFormImage.setVisibility(View.VISIBLE);
//                        notesHnFormImage.setImageBitmap(pictureBitmap);
                        uploadImage();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;
            case 2:
                if (resultCode == Activity.RESULT_OK) {
                    Uri uri = data.getData();
                    String fileName = getFileName(uri);

                    try {
//                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                        // Log.d(TAG, String.valueOf(bitmap));

                        if (fileName.contains("pdf")) {
                            isPDFFile = true;
                            uploadPDFFile(fileName);
                        } else {
                            Bitmap bitmapImage = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                            int nh = (int) (bitmapImage.getHeight() * (720.0 / bitmapImage.getWidth()));
                            Bitmap scaled = Bitmap.createScaledBitmap(bitmapImage, 720, nh, true);
                            notesHnFormImage.setImageBitmap(scaled);

//                            notesHnFormImage.setVisibility(View.VISIBLE);
//                        notesHnFormImage.setImageBitmap(bitmap);
                            uploadImage();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case REQUEST_PERMISSION_SETTING:
                if (ActivityCompat.checkSelfPermission(PatientRecordsCaseActivity.this, permissionsRequired[0]) == PackageManager.PERMISSION_GRANTED) {
                    //Got Permission
//                    Toast.makeText(RecordCreateActivity.this, "You Have All The Permission", Toast.LENGTH_SHORT).show();
                }
        }
    }

    public byte[] getFileDataFromDrawable(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 20, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }

    public String getFileName(Uri uri) {
        String[] projection = {MediaStore.MediaColumns.DISPLAY_NAME};
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        if (cursor != null) {
            //HERE YOU WILL GET A NULLPOINTER IF CURSOR IS NULL
            //THIS CAN BE, IF YOU USED OI FILE MANAGER FOR PICKING THE MEDIA
            int column_index = cursor
                    .getColumnIndexOrThrow(MediaStore.MediaColumns.DISPLAY_NAME);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } else return null;
    }

    private void uploadPDFFile(String selectedFilePath) {
        ContentResolver cr = getContentResolver();
        Uri uri = MediaStore.Files.getContentUri("external");

// every column, although that is huge waste, you probably need
// BaseColumns.DATA (the path) only.
        String[] projection = null;

// exclude media files, they would be here also.
//                    String selection = MediaStore.Files.FileColumns.MEDIA_TYPE + "="
//                            + MediaStore.Files.FileColumns.MEDIA_TYPE_NONE;
//                    String[] selectionArgs = null; // there is no ? clinicplus selection so null here
//
        String sortOrder = null; // unordered
//                    Cursor allNonMediaFiles = cr.query(uri, projection, selection, selectionArgs, sortOrder);

        String selectionMimeType = MediaStore.Files.FileColumns.MIME_TYPE + "=?";
        String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension("pdf");
        String[] selectionArgsPdf = new String[]{mimeType};
        Cursor allPdfFiles = cr.query(uri, projection, selectionMimeType, selectionArgsPdf, sortOrder);
//                    Log.d("Cursor ",  allPdfFiles.getString(1) + "");
        int i = 0;
        while (allPdfFiles.moveToNext()) {
//            Log.d("Cursorrrrrrr", allPdfFiles.getString(1));
//            Log.d("###########", selectedFilePath);
            if (allPdfFiles.getString(1).contains(selectedFilePath)) {
                pdfFile = new File(allPdfFiles.getString(1));
//                Intent inty = new Intent(Intent.ACTION_VIEW, FileProvider.getUriForFile(RecordCreateActivity.this,
//                        getApplicationContext().getPackageName() + ".provider",
//                        pdfFile));
//                inty.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//                startActivity(inty);

                uploadImage();
            }
        }
    }

    private void saveNotesHnData(JSONObject obj) {
        patientRecordsApi.postRecords(ApiUrls.saveWrittenNotes, obj, this, new VolleyCallback() {
            @Override
            public void onSuccess(String result) {
//                Log.d("Uploaded sucess", "****************" + result);

                //actual logic
                notesMainLayout.setVisibility(View.VISIBLE);
                notesFormLayout.setVisibility(View.GONE);

                getWrittenNotesForEncounter();

//                sectionProceed.setVisibility(View.GONE);

                //temp fix
//                notesLayout.setVisibility(View.VISIBLE);
//                caseHistoryLayout.setVisibility(View.GONE);
//                sectionProceed.setVisibility(View.VISIBLE);
//                headHist.setTextColor(getResources().getColor(R.color.colorAccent));
//                headCase.setTextColor(getResources().getColor(R.color.colorGreyText));
//                headAbout.setTextColor(getResources().getColor(R.color.colorGreyText));
//                headNotes.setTextColor(getResources().getColor(R.color.colorGreyText));
//                sectionNum = 3;

                Toast.makeText(PatientRecordsCaseActivity.this, getResources().getString(R.string.saving_data_success), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(String err) {
//                Log.d("Uploaded Fail", "****************" + err);
                ErrorHandlerClass.INSTANCE.errorHandler(PatientRecordsCaseActivity.this, err);
            }
        });
    }

    private void getEncouterDetailsForAppt() {
        String url = ApiUrls.getEncounterDetailsForAppt + "?appt_id=" + apptId;
        patientRecordsApi.getRecordPref(url, "", this, new VolleyCallback() {
            @Override
            public void onSuccess(String result) {
                try {

                    JSONObject json = new JSONObject(result);

                    if (json.isNull("response")) {

                    } else {


                        //show error login here
                        JSONObject resObj = new JSONObject(result);
                        encounterId = resObj.getJSONObject("response").getInt("id");
                        episodeID = resObj.getJSONObject("response").getInt("episode_id");
                        patientId = resObj.getJSONObject("response").getInt("patient_id");

                        getWrittenNotesForEncounter();
                        getEpisEncounter();
                    }

//                    JSONObject resObj = new JSONObject(result);
//                    encounterId = resObj.getJSONObject("response").getInt("id");
//                    episodeID = resObj.getJSONObject("response").getInt("episode_id");
//                    patientId = resObj.getJSONObject("response").getInt("patient_id");
//
//                    getWrittenNotesForEncounter();
//                    getEpisEncounter();

                    //   getEpisEncounter();// commented by dileep
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(String err) {
                ErrorHandlerClass.INSTANCE.errorHandler(PatientRecordsCaseActivity.this, err);
            }
        });
    }

    private void getWrittenNotesForEncounter() {
//        String url = appConfigClass.getWrittenNoteForEncounter + "?patient_id=" + patientId + "&encounter_id=" + encounterID +
//                "&sharedOrCreated=1";
        String url = ApiUrls.getEcntWrittenNotes + "?doctor_id=" + ApiUrls.doctorId + "&episode_id=" + episodeID + "&patient_id=" + patientId + "&sharedOrCreated=1&encounter_id=" + encounterId;
        notesHnNoteModels.clear();

        patientRecordsApi.getRecordPref(url, "", this, new VolleyCallback() {
            @Override
            public void onSuccess(String result) {
                try {
                    JSONObject resObj = new JSONObject(result);
                    JSONArray resArr = resObj.getJSONArray("response");
//                    Log.d("Response *************", result);
                    if (resArr.length() > 0) {
                        notesHnCount.setText(resArr.length() + "");
                        for (int i = 0; i < resArr.length(); i++) {
                            JSONObject notesObj = resArr.getJSONObject(i);
                            PatientRecordsModel model = new PatientRecordsModel();
                            model.setHnAttachURL(notesObj.getString("file_url"));
                            model.setHnDesp(notesObj.getString("description"));
                            model.setHnMedPres(getResources().getString(R.string.no));
                            if (notesObj.getInt("has_med_prescription") == 1) {
                                model.setHnMedPres(getResources().getString(R.string.yes));
                            }
                            model.setHnTestPres(getResources().getString(R.string.no));
                            if (notesObj.getInt("has_test_prescription") == 1) {
                                model.setHnTestPres(getResources().getString(R.string.yes));
                            }
                            model.setHnValidTill("NA");
                            if (notesObj.getString("med_prescription_valid_till") != null &&
                                    !notesObj.getString("med_prescription_valid_till").equalsIgnoreCase("null") &&
                                    !notesObj.getString("med_prescription_valid_till").equalsIgnoreCase("")) {
                                String date = appUtilities.changeDateFormat("yyyy-MM-dd mm:HH:ss", "dd MMM, yy", notesObj.getString("med_prescription_valid_till"));
                                model.setHnValidTill(date);
                            }
                            model.setType(1);

                            notesHnNoteModels.add(model);
                        }

                        notesHnNotesAdapter.notifyDataSetChanged();

                    } else {
                        notesHnCount.setText(resArr.length() + "");
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(String err) {
                ErrorHandlerClass.INSTANCE.errorHandler(PatientRecordsCaseActivity.this, err);

            }
        });
    }


    private void getEpisodicPref() {
        // handWrittenNotes.setVisibility(View.GONE);
        //  notesPrefrenceEmpty.setVisibility(View.GONE);
//        evaluationLayout.setVisibility(View.GONE);
//        treatPlanLayout.setVisibility(View.GONE);
        apiCall.getRecordPref(ApiUrls.getEpisodicFieldPref, "", this, new VolleyCallback() {
            @Override
            public void onSuccess(String result) {
                try {
                    JSONObject resObj = new JSONObject(result);
                    JSONArray prefArr = resObj.getJSONArray("response");

                    if (prefArr.getJSONObject(0).getInt("status") == 0 && prefArr.getJSONObject(1).getInt("status") == 0 && prefArr.getJSONObject(2).getInt("status") == 0) {
                        notesLayout.setVisibility(View.GONE);
                        notesPrefrenceEmpty.setVisibility(View.VISIBLE);
                    } else {
                        notesPrefrenceEmpty.setVisibility(View.GONE);
                        notesLayout.setVisibility(View.VISIBLE);

                        showGuide(14);
                    }

//                    notesHnNoteModels.clear();
//                    notesHnNotesAdapter.notifyDataSetChanged();
                    getWrittenNotesForEncounter();


                    if(treatPlanParentLayout.getVisibility()==View.VISIBLE)
                    {
                        treatPlanParentLayout.setVisibility(View.GONE);
                        recordEpisTreatmentArrow.setImageDrawable(getResources().getDrawable(R.drawable.ic_arrow_right));

                    }



                    symptomsModel.clear();
                    investModel.clear();
                    diagModel.clear();
                    obsCatModel.clear();

                    treatPlanModel.clear();

                    getEpisEvalutaion();//added by dileep 21st june
                    getEpisTreatPlan();// added by dileep 21st june


//                    if (prefArr.getJSONObject(0).getInt("status") == 1) {
//                        handWrittenNotes.setVisibility(View.VISIBLE);
//                    } else {
//                        handWrittenNotes.setVisibility(View.GONE);
//                    }

                    if (prefArr.getJSONObject(1).getInt("status") == 1) {
                        notesEvalCard.setVisibility(View.VISIBLE);
                        notesEvalCardHistory.setVisibility(View.VISIBLE);

                    } else {
                        notesEvalCard.setVisibility(View.GONE);
                        notesEvalCardHistory.setVisibility(View.GONE);

                    }

                    if (prefArr.getJSONObject(2).getInt("status") == 1) {
                        treatPlanLayout.setVisibility(View.VISIBLE);
                        treatPlanLayoutH.setVisibility(View.VISIBLE);
                    } else {
                        treatPlanLayout.setVisibility(View.GONE);
                        treatPlanLayoutH.setVisibility(View.GONE);

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(String err) {
                ErrorHandlerClass.INSTANCE.errorHandler(PatientRecordsCaseActivity.this, err);
            }
        });
    }


    private void getEpisEncounter() {
        String url = ApiUrls.getEpisEncounter + "?episode_id=" + episodeID + "&patient_id=" + patientId;

//        Log.d("Encounter Url", url);


        ecntListArr.clear();
        ecntIdArr.clear();


        patientRecordsApi.getRecordPref(url, "", this, new VolleyCallback() {
            @Override
            public void onSuccess(String result) {
                try {

                    JSONObject resObj = new JSONObject(result);
                    JSONArray enctArr = resObj.getJSONArray("response");
                    if (enctArr.length() > 0) {
                        ecntListArr.add("All Previous Interactions");
                        ecntIdArr.add(0);
                        for (int i = 0; i < enctArr.length(); i++) {
                            JSONObject enctObj = enctArr.getJSONObject(i);

                            String dateStr = enctObj.getString("encounter_date_time");
                            DateFormat srcDf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            Date date = srcDf.parse(dateStr);
                            DateFormat destDf = new SimpleDateFormat("dd, MMM yy HH:mm");
                            dateStr = destDf.format(date);


                            String ecntStr = enctObj.getString("encounter_mode") + " on " + dateStr;
                            ecntListArr.add(ecntStr);
                            ecntIdArr.add(enctObj.getInt("id"));

                            ecntAdapter.notifyDataSetChanged();
                        }
                    } else {
                        ecntListArr.add(getResources().getString(R.string.no_encounter_shared_with_you));
                        ecntIdArr.add(-1);
                        ecntAdapter.notifyDataSetChanged();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(String err) {
                ErrorHandlerClass.INSTANCE.errorHandler(PatientRecordsCaseActivity.this, err);
            }
        });
    }

    private File createImageFile() throws IOException {
        String timeStamp =
                new SimpleDateFormat("yyyyMMdd_HHmmss",
                        Locale.getDefault()).format(new Date());
        String imageFileName = "IMG_" + timeStamp + "_";
        File storageDir =
                getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".png",         /* suffix */
                storageDir      /* directory */
        );

        imageFilePath = image.getAbsolutePath();
        return image;
    }

    private void selectMethodDialog() {
        //before inflating the custom alert dialog layout, we will get the current activity viewgroup
        ViewGroup viewGroup = findViewById(android.R.id.content);

        //then we will inflate the custom alert dialog xml that we created
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_uploadimg_method, viewGroup, false);
        LinearLayout camera = dialogView.findViewById(R.id.uploadMethodCamera);
        LinearLayout attach = dialogView.findViewById(R.id.uploadMethodAttachment);

        ImageButton cameraButton = dialogView.findViewById(R.id.cameraButton);
        ImageButton attachImageButton = dialogView.findViewById(R.id.attachImageButton);

        //Now we need an AlertDialog.Builder object
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        //setting the view of the builder to our custom view that we already inflated
        builder.setView(dialogView);

        //finally creating the alert dialog and displaying it
        final AlertDialog alertDialog = builder.create();
        alertDialog.show();

        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();

                if (ActivityCompat.checkSelfPermission(PatientRecordsCaseActivity.this, permissionsRequired[0]) == PackageManager.PERMISSION_GRANTED) {
                    ZohoSalesIQ.Tracking.setCustomAction("AppointmentNotes - Notes - File Upload - Open Camera");
                    openCamera();
                } else {
                    ActivityCompat.requestPermissions(PatientRecordsCaseActivity.this, permissionsRequired, PERMISSION_CALLBACK_CONSTANT);
                }
            }
        });

        attachImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();

                ZohoSalesIQ.Tracking.setCustomAction("AppointmentNotes - Notes - File Upload - Open File Dialog");
                openFileDialog();
            }
        });


        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();

                if (ActivityCompat.checkSelfPermission(PatientRecordsCaseActivity.this, permissionsRequired[0]) == PackageManager.PERMISSION_GRANTED) {
                    ZohoSalesIQ.Tracking.setCustomAction("AppointmentNotes - Notes - File Upload - Open Camera");
                    openCamera();
                } else {
                    ActivityCompat.requestPermissions(PatientRecordsCaseActivity.this, permissionsRequired, PERMISSION_CALLBACK_CONSTANT);
                }
            }
        });

        attach.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();

                ZohoSalesIQ.Tracking.setCustomAction("AppointmentNotes - Notes - File Upload - Open File Dialog");
                openFileDialog();
            }
        });
    }


    private void savePatientDetails() {

        JSONObject patientObj = new JSONObject();
        try {
            patientObj.put("id", patientId);
            patientObj.put("name", pName.getText().toString());
            String temp = "";
            if (!pDOB.getText().toString().equalsIgnoreCase("")) {
                temp = appUtilities.changeDateFormat("dd MMM, yy", "yyyy-MM-dd", pDOB.getText().toString());
            }
            patientObj.put("dob", temp);
            patientObj.put("gender", genderId);
            patientObj.put("phone", pPhNo.getText().toString());
            patientObj.put("email", pEmail.getText().toString());
            patientObj.put("blood_group", pBloodG.getSelectedItem());
            patientObj.put("height", pHeight.getText().toString());
            patientObj.put("age", pAge.getText().toString());
            patientObj.put("generalid", pId.getText().toString());
        } catch (Exception e) {
            e.printStackTrace();
        }

//        Log.d("Passing Obj", patientObj.toString());

        String url = ApiUrls.updatePatientDetails + "/" + updatePatientId;
        patientRecordsApi.postRecords(url, patientObj, this, new VolleyCallback() {
            @Override
            public void onSuccess(String result) {
                Toast.makeText(PatientRecordsCaseActivity.this, getResources().getString(R.string.update_patient), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(String err) {
                ErrorHandlerClass.INSTANCE.errorHandler(PatientRecordsCaseActivity.this, err);
            }
        });
    }


    //for evaluation data
    private void getEpisEvalutaion() {
//        String url = appConfigClass.getEpisEvaluation + "?doctor_id=" + AppConfigClass.doctorId + "&episode_id=" + episodeID + "&patient_id=" + patientId + "&sharedOrCreated=1&encounter_id=" + encounterID;
//        if (encounterID != 0) {
//            url = appConfigClass.getEcntEvaluation + "?doctor_id=" + AppConfigClass.doctorId + "&episode_id=" + episodeID + "&patient_id=" + patientId + "&sharedOrCreated=1&encounter_id=" + encounterID;
//        }

        // https://arth-seed.clu.pw/api/v1/records/get-records-for-evaluation-encounter?doctor_id=46219&encounter_id=1043&patient_id=46223&sharedOrCreated=1
        if (totalEvalCount.getText().toString().equalsIgnoreCase("0")) {
            totalEvalCount.setVisibility(View.GONE);
            progressbarEvaluation.setVisibility(View.VISIBLE);
        }
        String url = ApiUrls.getEcntEvaluation + "?doctor_id=" + ApiUrls.doctorId + "&patient_id=" + patientId + "&sharedOrCreated=1&encounter_id=" + encounterId;
        if (encounterID != 0) {
            url = ApiUrls.getEcntEvaluation + "?doctor_id=" + ApiUrls.doctorId + "&patient_id=" + patientId + "&sharedOrCreated=1&encounter_id=" + encounterID;
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

                if (totalEvalCount.getText().toString().equalsIgnoreCase("0")) {
                    totalEvalCount.setVisibility(View.VISIBLE);
                    progressbarEvaluation.setVisibility(View.GONE);
                }

                totalEvalCount.setText(totalCount + "");

            }

            @Override
            public void onError(String err) {
                progressbarEvaluation.setVisibility(View.GONE);
                ErrorHandlerClass.INSTANCE.errorHandler(PatientRecordsCaseActivity.this, err);
            }
        });
    }

    private void getEpisEvalutaionForHistoryTab() {

        String url = ApiUrls.getEpisEvaluation + "?doctor_id=" + ApiUrls.doctorId + "&episode_id=" + episodeID + "&patient_id=" + patientId + "&sharedOrCreated=1&encounter_id=" + encounterID;
        if (encounterID != 0) {
            url = ApiUrls.getEcntEvaluation + "?doctor_id=" + ApiUrls.doctorId + "&episode_id=" + episodeID + "&patient_id=" + patientId + "&sharedOrCreated=1&encounter_id=" + encounterID;
        }

//        String url = appConfigClass.getEpisEvaluation + "?doctor_id=" + AppConfigClass.doctorId + "&episode_id=" + episodeID + "&patient_id=" + patientId + "&sharedOrCreated=1&encounter_id=" + encounterID;
//        if (encounterID != 0) {
//            url = appConfigClass.getEcntEvaluation + "?doctor_id=" + AppConfigClass.doctorId + "&episode_id=" + episodeID + "&patient_id=" + patientId + "&sharedOrCreated=1&encounter_id=" + encounterID;
//        }

        // https://arth-seed.clu.pw/api/v1/records/get-records-for-evaluation-encounter?doctor_id=46219&encounter_id=1043&patient_id=46223&sharedOrCreated=1
//        if (totalEvalCount.getText().toString().equalsIgnoreCase("0")) {
//            totalEvalCount.setVisibility(View.GONE);
//            progressbarEvaluation.setVisibility(View.VISIBLE);
//        }
//        String url = appConfigClass.getEcntEvaluation + "?doctor_id=" + AppConfigClass.doctorId + "&patient_id=" + patientId + "&sharedOrCreated=1&encounter_id=" + encounterId;
//        if (encounterID != 0) {
//            url = appConfigClass.getEcntEvaluation + "?doctor_id=" + AppConfigClass.doctorId + "&patient_id=" + patientId + "&sharedOrCreated=1&encounter_id=" + encounterID;
//        }
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

                            symptomsModelH.add(model);
                        }

                        totalCount += recordsArr.length();
                        symptomCountH.setText(symptomObj.getString("symptoms_count"));
                        symptomAdapterH.notifyDataSetChanged();
                    } else {
                        symptomCountH.setText(symptomObj.getString("symptoms_count"));
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

                            investModelH.add(model);
                        }

                        totalCount += recordsArr.length();
                        investCountH.setText(symptomObj.getString("investigation_results_count"));
                        investAdapterH.notifyDataSetChanged();
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

                            diagModelH.add(model);
                        }

                        totalCount += recordsArr.length();
                        diagCountH.setText(symptomObj.getString("diagnosis_count"));
                        diagAdapterH.notifyDataSetChanged();
                    } else {
                        diagCountH.setText(symptomObj.getString("diagnosis_count"));
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

                            obsCatModelH.add(model);
                        }

                        totalCount += Integer.parseInt(symptomObj.getString("record_count"));
//                        Log.d("Obsssss ******", totalCount + "");
                        obsCountH.setText(symptomObj.getString("record_count"));
                        obsAdapterH.notifyDataSetChanged();
                    } else {
                        obsCountH.setText(symptomObj.getString("record_count"));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

//                if (totalEvalCount.getText().toString().equalsIgnoreCase("0")) {
//                    totalEvalCount.setVisibility(View.VISIBLE);
//                    progressbarEvaluation.setVisibility(View.GONE);
//                }

                recordEpisEvalCountHistory.setText(totalCount + "");

            }

            @Override
            public void onError(String err) {
                //  progressbarEvaluation.setVisibility(View.GONE);
                ErrorHandlerClass.INSTANCE.errorHandler(PatientRecordsCaseActivity.this, err);

            }
        });
    }


    public void goToRecordsView(int catId, String catName, String type) {
        Intent intent = new Intent(this, PatientRecordViewActivity.class);
        intent.putExtra("CategoryId", catId);
        intent.putExtra("PatientId", patientId);
        intent.putExtra("CategoryName", catName);
        intent.putExtra("SharedCreated", 1);
        intent.putExtra("EpisodeId", episodeID);
        intent.putExtra("EncounterID", encounterID);
        intent.putExtra("Type", type);
        startActivity(intent);
    }


    public void goToCreateRecords(int catId, String catName, String type) {
        if (encounterId != 0) {
            Intent intent = new Intent(this, PatientCreateRecord2Activity.class);
            intent.putExtra("CategoryId", catId + "");
            intent.putExtra("CategoryName", catName);
            intent.putExtra("PatientId", patientId);
            intent.putExtra("EpisodeId", episodeID);
            intent.putExtra("EncounterID", encounterId);
            intent.putExtra("Type", type);
//            startActivity(intent);
            startActivityForResult(intent, 1);
        } else {
            Toast.makeText(PatientRecordsCaseActivity.this, "Please select one interaction before adding a record", Toast.LENGTH_SHORT).show();
        }
    }


    //for treatment plan

    private void getEpisTreatPlan() {


        String url = ApiUrls.getEpisTreatmentPlan + "?doctor_id=" + ApiUrls.doctorId + "&episode_id=" + episodeID + "&patient_id=" + patientId + "&sharedOrCreated=1&encounter_id=" + encounterID;
        if (encounterID != 0) {
            url = ApiUrls.getEcntTreatmentPlan + "?doctor_id=" + ApiUrls.doctorId + "&episode_id=" + episodeID + "&patient_id=" + patientId + "&sharedOrCreated=1&encounter_id=" + encounterID;
        }

        if (treatPlanCount.getText().toString().equalsIgnoreCase("0")) {

            treatPlanCount.setVisibility(View.GONE);
            progressbarTreatment.setVisibility(View.VISIBLE);
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
                            if (catObj.getJSONObject("record_categories").getInt("id") == 21) {

                            } else {
                                model.setTreatPlanName(catObj.getJSONObject("record_categories").getString("category"));
                                model.setTreatPlanCount(catObj.getJSONArray("records").length());
                                model.setRecordId(catObj.getJSONObject("record_categories").getInt("id"));
                                model.setType(6);

                                treatPlanModel.add(model);
                            }

//                            //share ecnt
//                            CheckBox cb = new CheckBox(PatientRecordsCaseActivity.this);
//                            cb.setText(catObj.getJSONObject("record_categories").getString("category"));
//                            cb.setId(catObj.getJSONObject("record_categories").getInt("id"));
//                            cb.setTag("TreatPlan");
//                            cb.setOnCheckedChangeListener(PatientRecordsCaseActivity.this);
//                            treatPlanCbList.add(cb);
//                            shareTreatPlanDataList.addView(cb);
                        }

                        if (treatPlanCount.getText().toString().equalsIgnoreCase("0")) {


                            treatPlanCount.setVisibility(View.VISIBLE);
                            progressbarTreatment.setVisibility(View.GONE);
                        }


                        treatPlanCount.setText(resObj.getJSONObject("response").getString("record_count"));
                        treatPlanAdapter.notifyDataSetChanged();
                    } else {

                        if (treatPlanCount.getText().toString().equalsIgnoreCase("0")) {
                            treatPlanCount.setVisibility(View.VISIBLE);
                            progressbarTreatment.setVisibility(View.GONE);
                        }


                        treatPlanCount.setText(resObj.getJSONObject("response").getString("record_count"));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(String err) {
                ErrorHandlerClass.INSTANCE.errorHandler(PatientRecordsCaseActivity.this, err);
            }
        });
    }


    private void getEpisTreatPlanForHistoryTab() {

        String url = ApiUrls.getEpisTreatmentPlan + "?doctor_id=" + ApiUrls.doctorId + "&episode_id=" + episodeID + "&patient_id=" + patientId + "&sharedOrCreated=1&encounter_id=" + encounterID;
        if (encounterID != 0) {
            url = ApiUrls.getEcntTreatmentPlan + "?doctor_id=" + ApiUrls.doctorId + "&episode_id=" + episodeID + "&patient_id=" + patientId + "&sharedOrCreated=1&encounter_id=" + encounterID;
        }


//        String url = appConfigClass.getEpisTreatmentPlan + "?doctor_id=" + AppConfigClass.doctorId + "&episode_id=" + episodeID + "&patient_id=" + patientId + "&sharedOrCreated=1&encounter_id=" + encounterID;
//        if (encounterID != 0) {
//            url = appConfigClass.getEcntTreatmentPlan + "?doctor_id=" + AppConfigClass.doctorId + "&episode_id=" + episodeID + "&patient_id=" + patientId + "&sharedOrCreated=1&encounter_id=" + encounterID;
//        }

//        if (treatPlanCount.getText().toString().equalsIgnoreCase("0")) {
//
//            treatPlanCount.setVisibility(View.GONE);
//            progressbarTreatment.setVisibility(View.VISIBLE);
//        }


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

                            if (catObj.getJSONObject("record_categories").getInt("id") == 21) {

                            } else {

                                model.setTreatPlanName(catObj.getJSONObject("record_categories").getString("category"));
                                model.setTreatPlanCount(catObj.getJSONArray("records").length());
                                model.setRecordId(catObj.getJSONObject("record_categories").getInt("id"));
                                model.setType(6);

                                treatPlanModelH.add(model);
                            }

//                            //share ecnt
//                            CheckBox cb = new CheckBox(PatientRecordsCaseActivity.this);
//                            cb.setText(catObj.getJSONObject("record_categories").getString("category"));
//                            cb.setId(catObj.getJSONObject("record_categories").getInt("id"));
//                            cb.setTag("TreatPlan");
//                            cb.setOnCheckedChangeListener(PatientRecordsCaseActivity.this);
//                            treatPlanCbList.add(cb);
//                            shareTreatPlanDataList.addView(cb);
                        }

                        if (treatPlanCountH.getText().toString().equalsIgnoreCase("0")) {


                            treatPlanCountH.setVisibility(View.VISIBLE);
                            progressbarTreatmentH.setVisibility(View.GONE);
                        }


                        treatPlanCountH.setText(resObj.getJSONObject("response").getString("record_count"));
                        treatPlanAdapterH.notifyDataSetChanged();
                    } else {

//                        if (treatPlanCount.getText().toString().equalsIgnoreCase("0")) {
//                            treatPlanCount.setVisibility(View.VISIBLE);
//                            progressbarTreatment.setVisibility(View.GONE);
//                        }


                        treatPlanCountH.setText(resObj.getJSONObject("response").getString("record_count"));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(String err) {
                ErrorHandlerClass.INSTANCE.errorHandler(PatientRecordsCaseActivity.this, err);
            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();

        symptomsModel.clear();
        investModel.clear();
        diagModel.clear();
        obsCatModel.clear();

        treatPlanModel.clear();

        getEpisEvalutaion();
        getEpisTreatPlan();

    }

    private void showGuide(int section) {
        switch (section) {
            case 1:
//                new GuideView.Builder(this)
//                        .setTitle("Patient Notes")
//                        .setContentText("Get more info on the patient details")
//                        .setTargetView(headerAboutLayout)
//                        .setDismissType(DismissType.outside)
//                        .setGuideListener(new GuideListener() {
//                            @Override
//                            public void onDismiss(View view) {
//                                showGuide(2);
//                            }
//                        })
//                        .build()
//                        .show();

                new MaterialIntroView.Builder(this)
                        .enableDotAnimation(true)
                        .enableIcon(false)
                        .dismissOnTouch(true)
                        .setFocusGravity(FocusGravity.CENTER)
                        .setFocusType(Focus.NORMAL)
                        .setDelayMillis(50)
                        .enableFadeAnimation(true)
                        .setInfoText("Get more info on the patient details")
                        .setShape(ShapeType.CIRCLE)
                        .setTarget(headerAboutLayout)
                        .setUsageId("intro_headerAboutLayout") //THIS SHOULD BE UNIQUE ID
                        .setListener(new MaterialIntroListener() {
                            @Override
                            public void onUserClicked(String materialIntroViewId) {
                                showGuide(2);
                            }
                        })
                        .show();


                break;

            case 2:
//                new GuideView.Builder(this)
//                        .setTitle("Case And Interactions")
//                        .setContentText("Create a new case or a new interaction for your appointment")
//                        .setTargetView(headerCaseLayout)
//                        .setDismissType(DismissType.outside)
//                        .setGuideListener(new GuideListener() {
//                            @Override
//                            public void onDismiss(View view) {
//                                showGuide(3);
//                            }
//                        })
//                        .build()
//                        .show();

                new MaterialIntroView.Builder(this)
                        .enableDotAnimation(true)
                        .enableIcon(false)
                        .dismissOnTouch(true)
                        .setFocusGravity(FocusGravity.CENTER)
                        .setFocusType(Focus.NORMAL)
                        .setDelayMillis(50)
                        .enableFadeAnimation(true)
                        .setInfoText("Create a new case or a new interaction for your appointment")
                        .setShape(ShapeType.CIRCLE)
                        .setTarget(headerCaseLayout)
                        .setUsageId("intro_headerCaseLayout") //THIS SHOULD BE UNIQUE ID
                        .setListener(new MaterialIntroListener() {
                            @Override
                            public void onUserClicked(String materialIntroViewId) {
                                showGuide(3);
                            }
                        })
                        .show();


                break;

            case 3:
//                new GuideView.Builder(this)
//                        .setTitle("Case History")
//                        .setContentText("Have a look into past cases and history data for the appointments")
//                        .setTargetView(headerHistLayout)
//                        .setDismissType(DismissType.outside)
//                        .setGuideListener(new GuideListener() {
//                            @Override
//                            public void onDismiss(View view) {
//                                showGuide(4);
//                            }
//                        })
//                        .build()
//                        .show();

                new MaterialIntroView.Builder(this)
                        .enableDotAnimation(true)
                        .enableIcon(false)
                        .dismissOnTouch(true)
                        .setFocusGravity(FocusGravity.CENTER)
                        .setFocusType(Focus.NORMAL)
                        .setDelayMillis(50)
                        .enableFadeAnimation(true)
                        .setInfoText("Have a look into past cases and history data for the appointments")
                        .setShape(ShapeType.CIRCLE)
                        .setTarget(headerHistLayout)
                        .setUsageId("intro_headerHistLayout") //THIS SHOULD BE UNIQUE ID
                        .setListener(new MaterialIntroListener() {
                            @Override
                            public void onUserClicked(String materialIntroViewId) {
                                showGuide(4);
                            }
                        })
                        .show();


                break;

            case 4:
//                new GuideView.Builder(this)
//                        .setTitle("Create Notes")
//                        .setContentText("Add a new note of the patient for the appointment")
//                        .setTargetView(headerNotesLayout)
//                        .setDismissType(DismissType.outside)
//                        .setGuideListener(new GuideListener() {
//                            @Override
//                            public void onDismiss(View view) {
////                                showGuide(4);
//                            }
//                        })
//                        .build()
//                        .show();

                new MaterialIntroView.Builder(this)
                        .enableDotAnimation(true)
                        .enableIcon(false)
                        .dismissOnTouch(true)
                        .setFocusGravity(FocusGravity.CENTER)
                        .setFocusType(Focus.NORMAL)
                        .setDelayMillis(50)
                        .enableFadeAnimation(true)
                        .setInfoText("Add a new note of the patient for the appointment")
                        .setShape(ShapeType.CIRCLE)
                        .setTarget(headerNotesLayout)
                        .setUsageId("intro_headerNotesLayout") //THIS SHOULD BE UNIQUE ID
                        .setListener(new MaterialIntroListener() {
                            @Override
                            public void onUserClicked(String materialIntroViewId) {
//                                showGuide(4);
                            }
                        })
                        .show();



                break;

            case 5:
                if(!sharedPreferences.getBoolean("About", false)) {
//                    new GuideView.Builder(this)
//                            .setTitle("Patient Information")
//                            .setContentText("View and update patient information")
//                            .setTargetView(patientCard)
//                            .setDismissType(DismissType.outside)
//                            .setGuideListener(new GuideListener() {
//                                @Override
//                                public void onDismiss(View view) {
//                                    showGuide(6);
//                                    SharedPreferences.Editor editor = sharedPreferences.edit();
//                                    editor.putBoolean("About", true);
//                                    editor.commit();
//                                }
//                            })
//                            .build()
//                            .show();

                    new MaterialIntroView.Builder(this)
                            .enableDotAnimation(true)
                            .enableIcon(false)
                            .dismissOnTouch(true)
                            .setFocusGravity(FocusGravity.CENTER)
                            .setFocusType(Focus.NORMAL)
                            .setDelayMillis(50)
                            .enableFadeAnimation(true)
                            .setInfoText("View and update patient information")
                            .setShape(ShapeType.RECTANGLE)
                            .setTarget(patientCard)
                            .setUsageId("intro_patientCard") //THIS SHOULD BE UNIQUE ID
                            .setListener(new MaterialIntroListener() {
                                @Override
                                public void onUserClicked(String materialIntroViewId) {
                                    showGuide(6);
                                    SharedPreferences.Editor editor = sharedPreferences.edit();
                                    editor.putBoolean("About", true);
                                    editor.commit();
                                }
                            })
                            .show();


                }
                break;

            case 6:
//                new GuideView.Builder(this)
//                        .setTitle("Patient Records")
//                        .setContentText("View patients current records")
//                        .setTargetView(aboutRecycleView)
//                        .setDismissType(DismissType.outside)
//                        .setGuideListener(new GuideListener() {
//                            @Override
//                            public void onDismiss(View view) {
////                                showGuide(6);
//                                showGuide(1);
//                            }
//                        })
//                        .build()
//                        .show();

                new MaterialIntroView.Builder(this)
                        .enableDotAnimation(true)
                        .enableIcon(false)
                        .dismissOnTouch(true)
                        .setFocusGravity(FocusGravity.CENTER)
                        .setFocusType(Focus.NORMAL)
                        .setDelayMillis(50)
                        .enableFadeAnimation(true)
                        .setInfoText("View patients current records")
                        .setShape(ShapeType.RECTANGLE)
                        .setTarget(aboutRecycleView)
                        .setUsageId("intro_aboutRecycleView3") //THIS SHOULD BE UNIQUE ID
                        .setListener(new MaterialIntroListener() {
                            @Override
                            public void onUserClicked(String materialIntroViewId) {
                                showGuide(1);

                            }
                        })
                        .show();


                break;

            case 7:
                if(!sharedPreferences.getBoolean("Case", false)) {
//                    new GuideView.Builder(this)
//                            .setTitle("Cases And Interaction")
//                            .setContentText("Select a case to get more information and details")
//                            .setTargetView(selectCase)
//                            .setDismissType(DismissType.outside)
//                            .setGuideListener(new GuideListener() {
//                                @Override
//                                public void onDismiss(View view) {
//                                    showGuide(8);
//                                    SharedPreferences.Editor editor = sharedPreferences.edit();
//                                    editor.putBoolean("Case", true);
//                                    editor.commit();
//                                }
//                            })
//                            .build()
//                            .show();

                    new MaterialIntroView.Builder(this)
                            .enableDotAnimation(true)
                            .enableIcon(false)
                            .dismissOnTouch(true)
                            .setFocusGravity(FocusGravity.CENTER)
                            .setFocusType(Focus.NORMAL)
                            .setDelayMillis(50)
                            .enableFadeAnimation(true)
                            .setInfoText("Select a case to get more information and details")
                            .setShape(ShapeType.RECTANGLE)
                            .setTarget(selectCase)
                            .setUsageId("intro_selectCase") //THIS SHOULD BE UNIQUE ID
                            .setListener(new MaterialIntroListener() {
                                @Override
                                public void onUserClicked(String materialIntroViewId) {
                                    showGuide(8);
                                    SharedPreferences.Editor editor = sharedPreferences.edit();
                                    editor.putBoolean("Case", true);
                                    editor.commit();

                                }
                            })
                            .show();


                }
                break;

            case 8:
//                new GuideView.Builder(this)
//                        .setTitle("Create New Case")
//                        .setContentText("Tap to create a new case for the appointment")
//                        .setTargetView(createNewCase)
//                        .setDismissType(DismissType.outside)
//                        .setGuideListener(new GuideListener() {
//                            @Override
//                            public void onDismiss(View view) {
//                                showGuide(9);
//                            }
//                        })
//                        .build()
//                        .show();

                new MaterialIntroView.Builder(this)
                        .enableDotAnimation(true)
                        .enableIcon(false)
                        .dismissOnTouch(true)
                        .setFocusGravity(FocusGravity.CENTER)
                        .setFocusType(Focus.NORMAL)
                        .setDelayMillis(50)
                        .enableFadeAnimation(true)
                        .setInfoText("Tap to create a new case for the appointment")
                        .setShape(ShapeType.CIRCLE)
                        .setTarget(createNewCase)
                        .setUsageId("intro_createNewCase") //THIS SHOULD BE UNIQUE ID
                        .setListener(new MaterialIntroListener() {
                            @Override
                            public void onUserClicked(String materialIntroViewId) {
                                showGuide(9);
                            }
                        })
                        .show();



                break;

            case 9:
//                new GuideView.Builder(this)
//                        .setTitle("Case Details")
//                        .setContentText("Know more about your cases and last interaction done")
//                        .setTargetView(caseInfoGuide)
//                        .setDismissType(DismissType.outside)
//                        .setGuideListener(new GuideListener() {
//                            @Override
//                            public void onDismiss(View view) {
//                                showGuide(10);
//                            }
//                        })
//                        .build()
//                        .show();

                new MaterialIntroView.Builder(this)
                        .enableDotAnimation(true)
                        .enableIcon(false)
                        .dismissOnTouch(true)
                        .setFocusGravity(FocusGravity.CENTER)
                        .setFocusType(Focus.NORMAL)
                        .setDelayMillis(50)
                        .enableFadeAnimation(true)
                        .setInfoText("Know more about your cases and last interaction done")
                        .setShape(ShapeType.RECTANGLE)
                        .setTarget(caseInfoGuide)
                        .setUsageId("intro_caseInfoGuide") //THIS SHOULD BE UNIQUE ID
                        .setListener(new MaterialIntroListener() {
                            @Override
                            public void onUserClicked(String materialIntroViewId) {
                                showGuide(10);
                            }
                        })
                        .show();


                break;

            case 10:
//                new GuideView.Builder(this)
//                        .setTitle("Create New Interactions")
//                        .setContentText("Create a new interaction for the appointment")
//                        .setTargetView(interactionGuide)
//                        .setDismissType(DismissType.outside)
//                        .setGuideListener(new GuideListener() {
//                            @Override
//                            public void onDismiss(View view) {
////                                showGuide(10);
//                                showGuide(1);
//                            }
//                        })
//                        .build()
//                        .show();


                new MaterialIntroView.Builder(this)
                        .enableDotAnimation(true)
                        .enableIcon(false)
                        .dismissOnTouch(true)
                        .setFocusGravity(FocusGravity.CENTER)
                        .setFocusType(Focus.NORMAL)
                        .setDelayMillis(50)
                        .enableFadeAnimation(true)
                        .setInfoText("Create a new interaction for the appointment")
                        .setShape(ShapeType.RECTANGLE)
                        .setTarget(interactionGuide)
                        .setUsageId("intro_interactionGuide") //THIS SHOULD BE UNIQUE ID
                        .setListener(new MaterialIntroListener() {
                            @Override
                            public void onUserClicked(String materialIntroViewId) {
//                                showGuide(10);
                                showGuide(1);
                            }
                        })
                        .show();



                break;

            case 11:
                if(!sharedPreferences.getBoolean("CreateCase", false)) {
//                    new GuideView.Builder(this)
//                            .setTitle("New Case Name")
//                            .setContentText("Enter a new case name or you can keep the default entry")
//                            .setTargetView(caseName)
//                            .setDismissType(DismissType.outside)
//                            .setGuideListener(new GuideListener() {
//                                @Override
//                                public void onDismiss(View view) {
//                                    showGuide(12);
//                                    SharedPreferences.Editor editor = sharedPreferences.edit();
//                                    editor.putBoolean("CreateCase", true);
//                                    editor.commit();
//                                }
//                            })
//                            .build()
//                            .show();

                    new MaterialIntroView.Builder(this)
                            .enableDotAnimation(true)
                            .enableIcon(false)
                            .dismissOnTouch(true)
                            .setFocusGravity(FocusGravity.CENTER)
                            .setFocusType(Focus.NORMAL)
                            .setDelayMillis(50)
                            .enableFadeAnimation(true)
                            .setInfoText("Enter a new case name or you can keep the default entry")
                            .setShape(ShapeType.CIRCLE)
                            .setTarget(caseName)
                            .setUsageId("intro_caseNameTwo") //THIS SHOULD BE UNIQUE ID
                            .setListener(new MaterialIntroListener() {
                                @Override
                                public void onUserClicked(String materialIntroViewId) {
                                    showGuide(12);
                                    SharedPreferences.Editor editor = sharedPreferences.edit();
                                    editor.putBoolean("CreateCase", true);
                                    editor.commit();
                                }
                            })
                            .show();


                }
                break;

            case 12:
//                new GuideView.Builder(this)
//                        .setTitle("Case Interaction")
//                        .setContentText("Fill clinicplus details for the interaction clinicplus your new case data")
//                        .setTargetView(createCaseInteractGuide)
//                        .setDismissType(DismissType.outside)
//                        .setGuideListener(new GuideListener() {
//                            @Override
//                            public void onDismiss(View view) {
////                                showGuide(12);
//                            }
//                        })
//                        .build()
//                        .show();


                new MaterialIntroView.Builder(this)
                        .enableDotAnimation(true)
                        .enableIcon(false)
                        .dismissOnTouch(true)
                        .setFocusGravity(FocusGravity.CENTER)
                        .setFocusType(Focus.NORMAL)
                        .setDelayMillis(50)
                        .enableFadeAnimation(true)
                        .setInfoText("Fill clinicplus details for the interaction clinicplus your new case data")
                        .setShape(ShapeType.CIRCLE)
                        .setTarget(createCaseInteractGuide)
                        .setUsageId("intro_createCaseInteractGuide") //THIS SHOULD BE UNIQUE ID
                        .setListener(new MaterialIntroListener() {
                            @Override
                            public void onUserClicked(String materialIntroViewId) {
//                                showGuide(12);
                            }
                        })
                        .show();



                break;

            case 13:
                if(!sharedPreferences.getBoolean("History", false)) {
//                    new GuideView.Builder(this)
//                            .setTitle("Case History")
//                            .setContentText("Select one interaction to fetch all records for that that interaction")
//                            .setTargetView(histSelectCase)
//                            .setDismissType(DismissType.outside)
//                            .setGuideListener(new GuideListener() {
//                                @Override
//                                public void onDismiss(View view) {
////                                showGuide(12);
//                                    SharedPreferences.Editor editor = sharedPreferences.edit();
//                                    editor.putBoolean("History", true);
//                                    editor.commit();
//                                }
//                            })
//                            .build()
//                            .show();

                    new MaterialIntroView.Builder(this)
                            .enableDotAnimation(true)
                            .enableIcon(false)
                            .dismissOnTouch(true)
                            .setFocusGravity(FocusGravity.CENTER)
                            .setFocusType(Focus.NORMAL)
                            .setDelayMillis(50)
                            .enableFadeAnimation(true)
                            .setInfoText("Select one interaction to fetch all records for that interaction")
                            .setShape(ShapeType.RECTANGLE)
                            .setTarget(histSelectCase)
                            .setUsageId("intro_histSelectCase") //THIS SHOULD BE UNIQUE ID
                            .setListener(new MaterialIntroListener() {
                                @Override
                                public void onUserClicked(String materialIntroViewId) {
//                                showGuide(12);
                                    SharedPreferences.Editor editor = sharedPreferences.edit();
                                    editor.putBoolean("History", true);
                                    editor.commit();
                                }
                            })
                            .show();


                }
                break;

//            case 14:
//                if(!sharedPreferences.getBoolean("Notes", false)) {
////                    new GuideView.Builder(this)
////                            .setTitle("Create New Note")
////                            .setContentText("Upload a file from your folder or click a new image of your notes")
////                            .setTargetView(notesHnUploadFile)
////                            .setDismissType(DismissType.outside)
////                            .setGuideListener(new GuideListener() {
////                                @Override
////                                public void onDismiss(View view) {
//////                                showGuide(12);
////                                    SharedPreferences.Editor editor = sharedPreferences.edit();
////                                    editor.putBoolean("Notes", true);
////                                    editor.commit();
////                                }
////                            })
////                            .build()
////                            .show();
//
//                    new MaterialIntroView.Builder(this)
//                            .enableDotAnimation(true)
//                            .enableIcon(false)
//                            .dismissOnTouch(true)
//                            .setFocusGravity(FocusGravity.CENTER)
//                            .setFocusType(Focus.NORMAL)
//                            .setDelayMillis(50)
//                            .enableFadeAnimation(true)
//                            .setInfoText("Upload a file from your folder or click a new image of your notes")
//                            .setShape(ShapeType.RECTANGLE)
//                            .setTarget(uploadNotes)
//                            .setUsageId("intro_uploadNotes") //THIS SHOULD BE UNIQUE ID
//                            .setListener(new MaterialIntroListener() {
//                                @Override
//                                public void onUserClicked(String materialIntroViewId) {
////                                showGuide(12);
//                                    SharedPreferences.Editor editor = sharedPreferences.edit();
//                                    editor.putBoolean("Notes", true);
//                                    editor.commit();
//                                }
//                            })
//                            .show();
//
//
//
//                }
//                break;
        }
    }
}
