package com.whitecoats.clinicplus.activities;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.IntDef;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.whitecoats.clinicplus.MainActivityViewModel;
import com.whitecoats.clinicplus.R;
import com.whitecoats.clinicplus.VoiceEmrHelpBottomSheet;
import com.whitecoats.clinicplus.adapters.DicationAdapter;
import com.whitecoats.clinicplus.adapters.VoiceEMRDiagnosisListAdapter;
import com.whitecoats.clinicplus.adapters.VoiceEMRHelpBottomSheet;
import com.whitecoats.clinicplus.adapters.VoiceEMRInvestigationResultListAdapter;
import com.whitecoats.clinicplus.adapters.VoiceEMRListAdapter;
import com.whitecoats.clinicplus.adapters.VoiceEMRObservationListAdapter;
import com.whitecoats.clinicplus.adapters.VoiceEMRTreatmentPlanListAdapter;
import com.whitecoats.clinicplus.adapters.VoiceEMRTreatmentPopupListAdapter;
import com.whitecoats.clinicplus.adapters.VoiceEMRVitalPopupListAdapter;
import com.whitecoats.clinicplus.apis.ApiUrls;
import com.whitecoats.clinicplus.databinding.ActivityVoiceEmractivityBinding;

import com.whitecoats.clinicplus.fragments.VoiceEMRFullDictationBottomSheet;
import com.whitecoats.clinicplus.interfaces.VoiceEmrRecordClickListener;
import com.whitecoats.clinicplus.models.DictationModel;
import com.whitecoats.clinicplus.models.EMRAddRecordCategoryModel;
import com.whitecoats.clinicplus.models.RecordsItems;
import com.whitecoats.clinicplus.models.SectionNameModel;
import com.whitecoats.clinicplus.models.VoiceEMRFullDictationModel;
import com.whitecoats.clinicplus.models.VoiceEMRModel;
import com.whitecoats.clinicplus.utils.AppUtilities;
import com.whitecoats.clinicplus.utils.DictationItemType;
import com.whitecoats.clinicplus.utils.ErrorHandlerClass;
import com.whitecoats.clinicplus.utils.TextImageClass;
import com.whitecoats.clinicplus.viewmodels.EMRConsultationNotesViewModel;
import com.whitecoats.clinicplus.viewmodels.SimboDataResponse;
import com.whitecoats.clinicplus.viewmodels.VoiceEMRViewModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

public class VoiceEMRActivity extends AppCompatActivity {

    private RecyclerView voiceEMRRV, voiceRecordDiagnosisRV, voiceRecordInvestigationRV, voiceRecordObservationRV, vitalListRV, voiceRecordTreatmentPlanRv, treatmentPlanListRV;
    public static List<VoiceEMRModel> voiceEMRModelsCategoryList, voiceEMRModelsDiagnosisCategoryList, voiceEMRModelsInvestigationCategoryList, voiceEMRModelsObservationCategoryList, voiceEMRModelsTreatmentPlanCategoryList;
    public List<EMRAddRecordCategoryModel> voiceEMRModelsVitalsPopupCategoryList, voiceEMRModelsTreatmentPlanPopupCategoryList;
    private VoiceEMRListAdapter voiceEMRListAdapter;
    private VoiceEMRDiagnosisListAdapter voiceEMRDiagnosisListAdapter;
    private VoiceEMRInvestigationResultListAdapter voiceEMRInvestigationResultListAdapter;
    private VoiceEMRObservationListAdapter voiceEMRObservationListAdapter;
    private VoiceEMRTreatmentPlanListAdapter voiceEMRTreatmentPlanListAdapter;
    private VoiceEMRVitalPopupListAdapter voiceEMRVitalPopupListAdapter;
    private VoiceEMRTreatmentPopupListAdapter voiceEMRTreatmentPopupListAdapter;
    private RecyclerView.LayoutManager mLayoutManager, mLayoutManagerDignosis, mLayoutManagerInvestigation, mLayoutManagerObservation, mLayoutManagerTreatmentPlan, mLayoutManagerVitalPopup, mLayoutManagerTreatmentPlanPopup;

    private TextView viewFullDictation;
    private VoiceEMRHelpBottomSheet voiceEMRHelpBottomSheet;
    public List<VoiceEMRFullDictationModel> fullDictationArrayList;
    private RequestQueue mRequestQueue;
    private String simbo_authKey = "", simboWss1_url = "", simboDevId = "";
    private AlertDialog saveRecordDialog;
    private boolean isDictationPaused;
    private RecyclerView records_recycler_view;
    private DicationAdapter dicationAdapter;
    private HashMap<String, Integer> section_existList;
    JSONArray symptomArrayObject = new JSONArray();
    private JSONArray diagnosisArrayObject = new JSONArray();
    private JSONArray investigationArrayObject = new JSONArray();
    private JSONArray observationArrayObject = new JSONArray();
    private JSONArray treatmentPlanArrayObject = new JSONArray();
    private boolean testingVolume;

    public int serialNumber = 0;

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({GRANTED, DENIED, BLOCKED_OR_NEVER_ASKED})
    public @interface PermissionStatus {
    }

    public static final int GRANTED = 0;
    public static final int DENIED = 1;
    public static final int BLOCKED_OR_NEVER_ASKED = 2;

    private RelativeLayout micPermissionNotGiven;
    private LinearLayout howToUse;
    private Button GivePermissionButton, micTestingButton, startDictationButton, pauseDictationButton, stopDictationButton;
    private Toolbar toolbar;
    private ProgressBar volumeProgressBar, volumeProgressBarWhite;
    private ImageView dictateIcon;
    private TextView dictateText;
    private Button saveRecordButton;
    private RelativeLayout clearAllDictationButton;
    private TextView voiceText;

    public static MainActivityViewModel viewModel;
    private ActivityVoiceEmractivityBinding binding;

    private VoiceEMRViewModel voiceEMRViewModel;

    private int patientId, episodeId, encounterId;
    String encounterName, encounterCreeatedOn, caseName, patientName;
    private boolean symptomAddRecordClick, diagnosisAddRecordClick, investigationAddRecordClick, observationAddRecordClick, treatmentPlanAddRecordClick;
    JSONObject jsonValue;
    TextView notRecordText;
    private boolean isCloseDictationClick;
    StringBuilder sbVoiceText;
    JSONArray interventionJsonArray;
    JSONObject interventionObject;
    JSONArray interventionTreatmentJsonArray;
    JSONObject interventionTreatmentJsonObject;

    private EMRConsultationNotesViewModel emrConsultationNotesViewModel;
    private JSONObject responseEpisodeFieldPreferences;
    private boolean isEvaluationPreferences = false;
    private boolean isTreatmentPreferences = false;
    public static JSONObject fieldDictionary;
    public JSONObject recordObject;

    private static final String LOG_TAG = "CheckNetworkStatus";
    private NetworkChangeReceiver receiver;
    private boolean isConnected = false;
    public static String pauseAndResumeStatus = "";
    private RelativeLayout connectingLoaderLayout;
    String lastStringHold = "";
    public static int volumeLevel;
    private boolean stopTestingClick = false;
    private CountDownTimer timer;
    private ProgressDialog loadingDialog;

    private LinkedHashMap<String, DictationModel> dictitationRecords = new LinkedHashMap();
    List<DictationItemType> records_adapter_List = new ArrayList<>();
    ArrayList<String> itemSerailNum = new ArrayList<>();
    /* ENGG-3754 -- Continuation of Refactoring the deprecated function in Clinic+ App(Android) */
    private ActivityResultLauncher<Intent> launchVoiceEMRRecordResults;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voice_emractivity);
        viewModel = new ViewModelProvider(this).get(MainActivityViewModel.class);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_voice_emractivity);
        binding.setLifecycleOwner(this);
        binding.setViewModel(viewModel);
//        viewModel.startAudioRecording("pediatrics");
        voiceEMRViewModel = new ViewModelProvider(this).get(VoiceEMRViewModel.class);
        voiceEMRViewModel.init();
        emrConsultationNotesViewModel = new ViewModelProvider(this).get(EMRConsultationNotesViewModel.class);
        emrConsultationNotesViewModel.init();
        mRequestQueue = Volley.newRequestQueue(VoiceEMRActivity.this);

        toolbar = findViewById(R.id.voiceEMRToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        Drawable upArrow = AppUtilities.changeIconColor(
                getResources().getDrawable(R.drawable.ic_arrow_back, getTheme()), this, R.color.colorWhite);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);

        Intent intent = getIntent();
        patientId = intent.getIntExtra("patientId", 0);
        episodeId = intent.getIntExtra("episodeId", 0);
        encounterId = intent.getIntExtra("encounterId", 0);
        encounterName = intent.getStringExtra("encounterName");
        encounterCreeatedOn = intent.getStringExtra("encounterCreatedOn");
        caseName = intent.getStringExtra("caseName");
        patientName = intent.getStringExtra("patientName");
        sbVoiceText = new StringBuilder();
        viewFullDictation = findViewById(R.id.viewFullDictation);
        clearAllDictationButton = findViewById(R.id.clearAllDictationButton);
        saveRecordButton = findViewById(R.id.saveRecordButton);
        mLayoutManager = new LinearLayoutManager(getApplicationContext(), RecyclerView.VERTICAL, false);
        mLayoutManagerDignosis = new LinearLayoutManager(getApplicationContext());
        mLayoutManagerInvestigation = new LinearLayoutManager(getApplicationContext());
        mLayoutManagerObservation = new LinearLayoutManager(getApplicationContext());
        mLayoutManagerTreatmentPlan = new LinearLayoutManager(getApplicationContext());
//        mLayoutManagerVitalPopup = new LinearLayoutManager(getApplicationContext());
//        mLayoutManagerTreatmentPlanPopup = new LinearLayoutManager(getApplicationContext());
        voiceEMRRV = findViewById(R.id.voiceRecordsRV);
        voiceRecordDiagnosisRV = findViewById(R.id.voiceRecordDiagnosisRV);
        voiceRecordInvestigationRV = findViewById(R.id.voiceRecordInvestigationRV);
        voiceRecordObservationRV = findViewById(R.id.voiceRecordObservationRV);
        voiceRecordTreatmentPlanRv = findViewById(R.id.voiceRecordTreatmentPlanRv);
        micPermissionNotGiven = findViewById(R.id.micPermissionNotGiven);
        howToUse = findViewById(R.id.howToUse);
        GivePermissionButton = findViewById(R.id.GivePermissionButton);
        volumeProgressBar = findViewById(R.id.volumeProgressBar);
        volumeProgressBarWhite = findViewById(R.id.volumeProgressBarWhite);
        micTestingButton = findViewById(R.id.micTestingButton);
        startDictationButton = findViewById(R.id.startDictationButton);
        pauseDictationButton = findViewById(R.id.pauseDictationButton);
        stopDictationButton = findViewById(R.id.stopDictationButton);
        dictateIcon = findViewById(R.id.dictateIcon);
        dictateText = findViewById(R.id.dictateText);
        voiceText = findViewById(R.id.voiceText);
        notRecordText = findViewById(R.id.voiceText);
        connectingLoaderLayout = findViewById(R.id.connectingLoaderLayout);
        records_recycler_view = findViewById(R.id.record_list);
        voiceEMRModelsCategoryList = new ArrayList<>();
        voiceEMRModelsDiagnosisCategoryList = new ArrayList<>();
        voiceEMRModelsInvestigationCategoryList = new ArrayList<>();
        voiceEMRModelsObservationCategoryList = new ArrayList<>();
        voiceEMRModelsTreatmentPlanCategoryList = new ArrayList<>();
        voiceEMRModelsVitalsPopupCategoryList = new ArrayList<>();
        voiceEMRModelsTreatmentPlanPopupCategoryList = new ArrayList<>();
        fullDictationArrayList = new ArrayList<>();
//        voiceEMRFullDictationBottomSheet = new VoiceEMRFullDictationBottomSheet(this,fullDictationArrayList);
        voiceEMRHelpBottomSheet = new VoiceEMRHelpBottomSheet(this);

        saveRecordDialog = new AlertDialog.Builder(this).create();
        saveRecordDialog.setCancelable(false);

        getSimboApiKey();

        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        receiver = new NetworkChangeReceiver();
        registerReceiver(receiver, filter);
        /* ENGG-3754 -- Continuation of Refactoring the deprecated function in Clinic+ App(Android) */
        //Start
        launchVoiceEMRRecordResults = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        //Request code 1 and 100
                        Intent data = result.getData();
                        int resultCode = result.getResultCode();
                        if (resultCode == 100) {
                            Bundle bundle = data.getExtras();
                            if (bundle != null) {
                                VoiceEMRModel emrobject = (VoiceEMRModel) bundle.getSerializable("VoiceEMRModel");
                                if (emrobject != null) {
                                    String action_type = bundle.getString("ActionType");
                                    if (emrobject.getCategoryName().equalsIgnoreCase("Symptoms")) {
                                        dictitationRecords.put(emrobject.getSymptomData_id(), new DictationModel(emrobject.getCategoryName(), emrobject));
                                    } else if (emrobject.getCategoryName().equalsIgnoreCase("Diagnosis")) {
                                        dictitationRecords.put(emrobject.getDiagnosisData_id(), new DictationModel(emrobject.getCategoryName(), emrobject));
                                    } else if (emrobject.getCategoryName().equalsIgnoreCase("Investigation Results")) {
                                        dictitationRecords.put(emrobject.getInvestigationData_id(), new DictationModel(emrobject.getCategoryName(), emrobject));
                                    } else if (emrobject.getCategoryName().equalsIgnoreCase("Observation")) {
                                        dictitationRecords.put(emrobject.getObservationData_id(), new DictationModel(emrobject.getCategoryName(), emrobject));
                                    } else if (emrobject.getCategoryName().equalsIgnoreCase("Treatment Plan")) {
                                        dictitationRecords.put(emrobject.getTreatmentPlanData_id(), new DictationModel(emrobject.getCategoryName(), emrobject));
                                    }
                                    HashMap<String, List<DictationModel>> groupedHashMap = groupDataIntoHashMap(dictitationRecords);


                                    records_adapter_List.clear();
                                    itemSerailNum.clear();
                                    dicationAdapter.notifyDataSetChanged();
                                    ArrayList<String> hashmapKeys = new ArrayList<>(groupedHashMap.keySet());
                                    Collections.reverse(hashmapKeys);
                                    for (String section_name : hashmapKeys) {
                                        int serialNum = 0;
                                        //if(!header_section_names_list.contains(section_name)) {
                                        SectionNameModel sectionNameModel = new SectionNameModel();
                                        sectionNameModel.setSectionName(section_name);
                                        itemSerailNum.add(serialNum + "");
                                        //sectionNameModel.setSectionType(section_name);
                                        records_adapter_List.add(sectionNameModel);

                                        //header_section_names_list.add(sectionName);
                                        //}


                                        for (DictationModel dictationModel : groupedHashMap.get(section_name)) {
                                            RecordsItems recordsItems = new RecordsItems();
                                            recordsItems.setVoiceEMRModel(dictationModel);
                                            recordsItems.setRecordType(dictationModel.getSection_name());
                                            itemSerailNum.add(++serialNum + "");
                                            //setBookingDataTabs(bookingDataTabs);
                                            records_adapter_List.add(recordsItems);
                                        }
                                    }
                                    dicationAdapter.notifyDataSetChanged();
                                }
                            }
                        }
                    }
                });
        //End
        timer = new CountDownTimer(30000, 1000) {

            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                try {
                    timer.cancel();
                    if (pauseAndResumeStatus != null && pauseAndResumeStatus.equalsIgnoreCase("pause")) {

                    } else {
                        dictationPaused(VoiceEMRActivity.this);
                    }
                } catch (Exception e) {
                    Log.e("Error", "Error: " + e.toString());
                }
            }
        };


        dicationAdapter = new DicationAdapter(this, records_adapter_List, itemSerailNum, new VoiceEmrRecordClickListener() {
            @Override
            public void onItemClick(View v, int position, String clickActionType, String categoryName, VoiceEMRModel voiceEMRModel, String recordType) {
                if (clickActionType.equalsIgnoreCase("ItemAllDelete")) {
                  /*  voiceEMRModelsCategoryList.clear();
                    voiceEMRListAdapter.notifyDataSetChanged();*/
                    deleteRecord(categoryName, voiceEMRModel);
                } else if (clickActionType.equalsIgnoreCase("ItemSingleDelete")) {
                    records_adapter_List.remove(position);
                    String dataId = "";
                    if (categoryName.equalsIgnoreCase("Symptoms")) {
                        dataId = voiceEMRModel.getSymptomData_id();
                    } else if (categoryName.equalsIgnoreCase("Diagnosis")) {
                        dataId = voiceEMRModel.getDiagnosisData_id();
                    } else if (categoryName.equalsIgnoreCase("Investigation Results")) {
                        dataId = voiceEMRModel.getInvestigationData_id();
                    } else if (categoryName.equalsIgnoreCase("Observation")) {
                        dataId = voiceEMRModel.getObservationData_id();
                    } else if (categoryName.equalsIgnoreCase("Treatment Plan")) {
                        dataId = voiceEMRModel.getTreatmentPlanData_id();
                    }

                    dictitationRecords.remove(dataId);
                    dicationAdapter.notifyItemRemoved(position);
                    itemSerailNum.clear();
                    int serialNum = 0;
                    for (int i = 0; i < records_adapter_List.size(); i++) {
                        if (records_adapter_List.get(i).getType() == 0) {
                            serialNum = 0;
                            itemSerailNum.add(serialNum + "");
                        } else {
                            itemSerailNum.add(++serialNum + "");
                        }
                    }
                    dicationAdapter.notifyDataSetChanged();

                    int j = 0;
                    int singleDeleteRecordPosition = -1;
                    for (int i = 0; i < records_adapter_List.size(); i++) {
                        if (records_adapter_List.get(i).getRecordType().equalsIgnoreCase(categoryName)) {
                            j++;
                            singleDeleteRecordPosition = i;
                        }
                    }

                    if (j == 1) {
                        records_adapter_List.remove(singleDeleteRecordPosition);
                        dicationAdapter.notifyDataSetChanged();

                    }
                    serialNumber = 0;
                } else if (clickActionType.equalsIgnoreCase("Info")) {
                    if (categoryName.equalsIgnoreCase("Symptoms")) {
                        symptomsHelpDialog(VoiceEMRActivity.this);
                    } else if (categoryName.equalsIgnoreCase("Diagnosis")) {
                        diagnosisHelpDialog(VoiceEMRActivity.this);
                    } else if (categoryName.equalsIgnoreCase("Investigation Results")) {
                        investigationHelpDialog(VoiceEMRActivity.this);
                    } else if (categoryName.equalsIgnoreCase("Observation")) {
                        observationHelpDialog(VoiceEMRActivity.this);
                    } else if (categoryName.equalsIgnoreCase("Treatment Plan")) {
                        treatmentplanHelpDialog(VoiceEMRActivity.this);
                    }
                } else if (clickActionType.equalsIgnoreCase("addRecords")) {
                    symptomAddRecordClick = true;

                    if (categoryName.equalsIgnoreCase("Symptoms")) {
                        if (encounterId != 0) {
                            Intent myIntent = new Intent(VoiceEMRActivity.this, VoiceEMRCreateRecordsFormActivity.class);
                            myIntent.putExtra("patientId", patientId);
                            myIntent.putExtra("episodeId", episodeId);
                            myIntent.putExtra("encounterId", encounterId);
                            myIntent.putExtra("categoryName", categoryName);
                            myIntent.putExtra("patientName", patientName);
                            myIntent.putExtra("recordPosition", position);
                            myIntent.putExtra("SymptomsEditRecord", "");
                            myIntent.putExtra("VoiceEMRModel", voiceEMRModel);
                            launchVoiceEMRRecordResults.launch(myIntent);
                        } else {
                            Toast.makeText(VoiceEMRActivity.this, "Please select one interaction before adding a record", Toast.LENGTH_SHORT).show();
                        }
                    } else if (categoryName.equalsIgnoreCase("Diagnosis")) {
                        diagnosisAddRecordClick = true;
                        if (encounterId != 0) {
                            Intent myIntent = new Intent(VoiceEMRActivity.this, VoiceEMRCreateRecordsFormActivity.class);
                            myIntent.putExtra("patientId", patientId);
                            myIntent.putExtra("episodeId", episodeId);
                            myIntent.putExtra("encounterId", encounterId);
                            myIntent.putExtra("categoryName", categoryName);
                            myIntent.putExtra("patientName", patientName);
                            myIntent.putExtra("recordPosition", position);
                            myIntent.putExtra("diagnosisEditRecord", "");
                            myIntent.putExtra("VoiceEMRModel", voiceEMRModel);
                            launchVoiceEMRRecordResults.launch(myIntent);
                        } else {
                            Toast.makeText(VoiceEMRActivity.this, "Please select one interaction before adding a record", Toast.LENGTH_SHORT).show();
                        }
                    } else if (categoryName.equalsIgnoreCase("Investigation Results")) {
                        investigationAddRecordClick = true;
                        if (encounterId != 0) {
                            Intent myIntent = new Intent(VoiceEMRActivity.this, VoiceEMRCreateRecordsFormActivity.class);
                            myIntent.putExtra("patientId", patientId);
                            myIntent.putExtra("episodeId", episodeId);
                            myIntent.putExtra("encounterId", encounterId);
                            myIntent.putExtra("categoryName", categoryName);
                            myIntent.putExtra("patientName", patientName);
                            myIntent.putExtra("VoiceEMRModel", voiceEMRModel);
                            myIntent.putExtra("recordPosition", position);
                            myIntent.putExtra("InvestigationEditRecord", "");
                            launchVoiceEMRRecordResults.launch(myIntent);
                        } else {
                            Toast.makeText(VoiceEMRActivity.this, "Please select one interaction before adding a record", Toast.LENGTH_SHORT).show();
                        }
                    } else if (categoryName.equalsIgnoreCase("Observation")) {
                        vitalPopupDialog(VoiceEMRActivity.this, voiceEMRModel);
                    } else if (categoryName.equalsIgnoreCase("Treatment Plan")) {
                        treatmentPlanPopupDialog(VoiceEMRActivity.this, voiceEMRModel);
                    }
                } else if (clickActionType.equalsIgnoreCase("editRecord")) {
                    if (categoryName.equalsIgnoreCase("Symptoms")) {
                        symptomAddRecordClick = true;
                        if (encounterId != 0) {
                            Intent myIntent = new Intent(VoiceEMRActivity.this, VoiceEMRCreateRecordsFormActivity.class);
                            myIntent.putExtra("patientId", patientId);
                            myIntent.putExtra("episodeId", episodeId);
                            myIntent.putExtra("encounterId", encounterId);
                            myIntent.putExtra("categoryName", categoryName);
                            myIntent.putExtra("patientName", patientName);
                            myIntent.putExtra("recordPosition", position);
                            myIntent.putExtra("VoiceEMRModel", voiceEMRModel);
                            myIntent.putExtra("SymptomsEditRecord", "SymptomsEditRecord");
                            launchVoiceEMRRecordResults.launch(myIntent);
                        } else {
                            Toast.makeText(VoiceEMRActivity.this, "Please select one interaction before adding a record", Toast.LENGTH_SHORT).show();
                        }
                    } else if (categoryName.equalsIgnoreCase("Diagnosis")) {
                        diagnosisAddRecordClick = true;
                        if (encounterId != 0) {
                            Intent myIntent = new Intent(VoiceEMRActivity.this, VoiceEMRCreateRecordsFormActivity.class);
                            myIntent.putExtra("patientId", patientId);
                            myIntent.putExtra("episodeId", episodeId);
                            myIntent.putExtra("encounterId", encounterId);
                            myIntent.putExtra("categoryName", categoryName);
                            myIntent.putExtra("patientName", patientName);
                            myIntent.putExtra("VoiceEMRModel", voiceEMRModel);
                            myIntent.putExtra("recordPosition", position);
                            myIntent.putExtra("diagnosisEditRecord", "diagnosisEditRecord");
                            launchVoiceEMRRecordResults.launch(myIntent);
                        } else {
                            Toast.makeText(VoiceEMRActivity.this, "Please select one interaction before adding a record", Toast.LENGTH_SHORT).show();
                        }
                    } else if (categoryName.equalsIgnoreCase("Investigation Results")) {
                        investigationAddRecordClick = true;
                        if (encounterId != 0) {
                            Intent myIntent = new Intent(VoiceEMRActivity.this, VoiceEMRCreateRecordsFormActivity.class);
                            myIntent.putExtra("patientId", patientId);
                            myIntent.putExtra("episodeId", episodeId);
                            myIntent.putExtra("encounterId", encounterId);
                            myIntent.putExtra("categoryName", categoryName);
                            myIntent.putExtra("patientName", patientName);
                            myIntent.putExtra("VoiceEMRModel", voiceEMRModel);
                            myIntent.putExtra("recordPosition", position);
                            myIntent.putExtra("InvestigationEditRecord", "InvestigationEditRecord");
                            launchVoiceEMRRecordResults.launch(myIntent);
                        } else {
                            Toast.makeText(VoiceEMRActivity.this, "Please select one interaction before adding a record", Toast.LENGTH_SHORT).show();
                        }
                    } else if (categoryName.equalsIgnoreCase("Observation")) {
                        observationAddRecordClick = true;
                        if (encounterId != 0) {
                            Intent intent = new Intent(VoiceEMRActivity.this, VoiceEMRCreateRecordActivity.class);
                            intent.putExtra("CategoryId", voiceEMRModel.getObservationCategoryId() + "");
                            intent.putExtra("CategoryName", voiceEMRModel.getObservationCategoryName());
                            intent.putExtra("PatientId", patientId);
                            intent.putExtra("EpisodeId", episodeId);
                            intent.putExtra("EncounterID", encounterId);
                            intent.putExtra("Type", "observations");

                            intent.putExtra("recordPosition", position);
                            intent.putExtra("ObservationEditRecord", "ObservationEditRecord");
                            intent.putExtra("VoiceEMRModel", voiceEMRModel);
                            launchVoiceEMRRecordResults.launch(intent);
                        } else {
                            Toast.makeText(VoiceEMRActivity.this, "Please select one interaction before adding a record", Toast.LENGTH_SHORT).show();
                        }
                    } else if (categoryName.equalsIgnoreCase("Treatment Plan")) {
                        treatmentPlanAddRecordClick = true;
                        if (encounterId != 0) {
                            Intent intent = new Intent(VoiceEMRActivity.this, VoiceEMRCreateRecordActivity.class);
                            intent.putExtra("CategoryId", voiceEMRModel.getTreatmentCategoryId() + "");
                            intent.putExtra("CategoryName", voiceEMRModel.getTreatmentCategoryName());
                            intent.putExtra("PatientId", patientId);
                            intent.putExtra("EpisodeId", episodeId);
                            intent.putExtra("EncounterID", encounterId);
                            intent.putExtra("Type", "treatmentplan");
                            intent.putExtra("recordPosition", position);
                            intent.putExtra("TreatmentEditRecord", "TreatmentEditRecord");
                            intent.putExtra("VoiceEMRModel", voiceEMRModel);

                            launchVoiceEMRRecordResults.launch(intent);
                        } else {
                            Toast.makeText(VoiceEMRActivity.this, "Please select one interaction before adding a record", Toast.LENGTH_SHORT).show();
                        }
                    }
                }

            }
        });

        records_recycler_view.setLayoutManager(mLayoutManager);
        records_recycler_view.setItemAnimator(new DefaultItemAnimator());
        records_recycler_view.setAdapter(dicationAdapter);


        //for symptoms records
        voiceEMRListAdapter = new VoiceEMRListAdapter(this, voiceEMRModelsCategoryList, new VoiceEmrRecordClickListener() {
            @Override
            public void onItemClick(View v, int position, String clickActionType, String categoryName, VoiceEMRModel voiceEMRModel, String recordType) {
                if (clickActionType.equalsIgnoreCase("SymptomsAll")) {
                  /*  voiceEMRModelsCategoryList.clear();
                    voiceEMRListAdapter.notifyDataSetChanged();*/
//                    deleteRecord("Symptoms Records");
                } else if (clickActionType.equalsIgnoreCase("SymptomSingle")) {
                    if (position == 0) {
                        voiceEMRModelsCategoryList.remove(position);
                        if (voiceEMRModelsCategoryList.size() > 0) {
                            voiceEMRModelsCategoryList.get(position).setCategoryNameSymptomExistType(1);
                        }

                        voiceEMRListAdapter.notifyDataSetChanged();
                        if (voiceEMRModelsCategoryList.size() == 0 && voiceEMRModelsDiagnosisCategoryList.size() == 0 && voiceEMRModelsInvestigationCategoryList.size() == 0) {
//                            notRecordText.setVisibility(View.VISIBLE);
                        }
                    } else {
                        voiceEMRModelsCategoryList.remove(position);
                        voiceEMRListAdapter.notifyDataSetChanged();
                        if (voiceEMRModelsCategoryList.size() == 0 && voiceEMRModelsDiagnosisCategoryList.size() == 0 && voiceEMRModelsInvestigationCategoryList.size() == 0) {
//                            notRecordText.setVisibility(View.VISIBLE);
                        }
                    }
                } else if (clickActionType.equalsIgnoreCase("SymptomsInfo")) {
                    symptomsHelpDialog(VoiceEMRActivity.this);

                } else if (clickActionType.equalsIgnoreCase("SymptomsAddRecord")) {

                    symptomAddRecordClick = true;

                    if (categoryName.equalsIgnoreCase("Symptoms") || categoryName.equalsIgnoreCase("Investigation Results") || categoryName.equalsIgnoreCase("Diagnosis")) {
                        if (encounterId != 0) {
                            Intent myIntent = new Intent(VoiceEMRActivity.this, VoiceEMRCreateRecordsFormActivity.class);
                            myIntent.putExtra("patientId", patientId);
                            myIntent.putExtra("episodeId", episodeId);
                            myIntent.putExtra("encounterId", encounterId);
                            myIntent.putExtra("categoryName", categoryName);
                            myIntent.putExtra("patientName", patientName);

                            myIntent.putExtra("recordPosition", position);
                            myIntent.putExtra("SymptomsEditRecord", "");

                            startActivity(myIntent);
                        } else {
                            Toast.makeText(VoiceEMRActivity.this, "Please select one interaction before adding a record", Toast.LENGTH_SHORT).show();
                        }
                    } else if (encounterId != 0) {
                    } else {
                        Toast.makeText(VoiceEMRActivity.this, "Please select one interaction before adding a record", Toast.LENGTH_SHORT).show();
                    }


                } else if (clickActionType.equalsIgnoreCase("SymptomsEditRecord")) {

                    symptomAddRecordClick = true;

                    if (categoryName.equalsIgnoreCase("Symptoms") || categoryName.equalsIgnoreCase("Investigation Results") || categoryName.equalsIgnoreCase("Diagnosis")) {
                        if (encounterId != 0) {
                            Intent myIntent = new Intent(VoiceEMRActivity.this, VoiceEMRCreateRecordsFormActivity.class);
                            myIntent.putExtra("patientId", patientId);
                            myIntent.putExtra("episodeId", episodeId);
                            myIntent.putExtra("encounterId", encounterId);
                            myIntent.putExtra("categoryName", categoryName);
                            myIntent.putExtra("patientName", patientName);

                            myIntent.putExtra("recordPosition", position);
                            myIntent.putExtra("SymptomsEditRecord", "SymptomsEditRecord");

                            startActivity(myIntent);
                        } else {
                            Toast.makeText(VoiceEMRActivity.this, "Please select one interaction before adding a record", Toast.LENGTH_SHORT).show();
                        }
                    } else if (encounterId != 0) {
                    } else {
                        Toast.makeText(VoiceEMRActivity.this, "Please select one interaction before adding a record", Toast.LENGTH_SHORT).show();
                    }


                }

            }
        });
       /* voiceEMRRV.setLayoutManager(mLayoutManager);
        voiceEMRRV.setItemAnimator(new DefaultItemAnimator());
        voiceEMRRV.setAdapter(voiceEMRListAdapter);*/

        //for diagnosis records
        voiceEMRDiagnosisListAdapter = new VoiceEMRDiagnosisListAdapter(this, voiceEMRModelsDiagnosisCategoryList, new VoiceEmrRecordClickListener() {
            @Override
            public void onItemClick(View v, int position, String clickActionType, String categoryName, VoiceEMRModel voiceEMRModel, String recordType) {
                if (clickActionType.equalsIgnoreCase("diagnosisAll")) {
                  /*  voiceEMRModelsDiagnosisCategoryList.clear();
                    voiceEMRDiagnosisListAdapter.notifyDataSetChanged();*/
//                    deleteRecord("Diagnosis Records");
                } else if (clickActionType.equalsIgnoreCase("diagnosisSingle")) {

                    if (position == 0) {
                        voiceEMRModelsDiagnosisCategoryList.remove(position);
                        if (voiceEMRModelsDiagnosisCategoryList.size() > 0) {
                            voiceEMRModelsDiagnosisCategoryList.get(position).setCategoryNameDiagnosisExistType(1);
                        }
                        voiceEMRDiagnosisListAdapter.notifyDataSetChanged();
                        if (voiceEMRModelsCategoryList.size() == 0 && voiceEMRModelsDiagnosisCategoryList.size() == 0 && voiceEMRModelsInvestigationCategoryList.size() == 0) {
//                            notRecordText.setVisibility(View.VISIBLE);
                        }
                    } else {
                        voiceEMRModelsDiagnosisCategoryList.remove(position);
                        voiceEMRDiagnosisListAdapter.notifyDataSetChanged();
                        if (voiceEMRModelsCategoryList.size() == 0 && voiceEMRModelsDiagnosisCategoryList.size() == 0 && voiceEMRModelsInvestigationCategoryList.size() == 0) {
//                            notRecordText.setVisibility(View.VISIBLE);
                        }
                    }
                } else if (clickActionType.equalsIgnoreCase("diagnosisInfo")) {
                    diagnosisHelpDialog(VoiceEMRActivity.this);
                } else if (clickActionType.equalsIgnoreCase("diagnosisAddRecord")) {

                    diagnosisAddRecordClick = true;

                    if (categoryName.equalsIgnoreCase("Symptoms") || categoryName.equalsIgnoreCase("Investigation Results") || categoryName.equalsIgnoreCase("Diagnosis")) {
                        if (encounterId != 0) {
                            Intent myIntent = new Intent(VoiceEMRActivity.this, VoiceEMRCreateRecordsFormActivity.class);
                            myIntent.putExtra("patientId", patientId);
                            myIntent.putExtra("episodeId", episodeId);
                            myIntent.putExtra("encounterId", encounterId);
                            myIntent.putExtra("categoryName", categoryName);
                            myIntent.putExtra("patientName", patientName);

                            myIntent.putExtra("recordPosition", position);
                            myIntent.putExtra("diagnosisEditRecord", "");

                            startActivity(myIntent);
                        } else {
                            Toast.makeText(VoiceEMRActivity.this, "Please select one interaction before adding a record", Toast.LENGTH_SHORT).show();
                        }
                    } else if (encounterId != 0) {
                    } else {
                        Toast.makeText(VoiceEMRActivity.this, "Please select one interaction before adding a record", Toast.LENGTH_SHORT).show();
                    }


                } else if (clickActionType.equalsIgnoreCase("diagnosisEditRecord")) {

                    diagnosisAddRecordClick = true;

                    if (categoryName.equalsIgnoreCase("Symptoms") || categoryName.equalsIgnoreCase("Investigation Results") || categoryName.equalsIgnoreCase("Diagnosis")) {
                        if (encounterId != 0) {
                            Intent myIntent = new Intent(VoiceEMRActivity.this, VoiceEMRCreateRecordsFormActivity.class);
                            myIntent.putExtra("patientId", patientId);
                            myIntent.putExtra("episodeId", episodeId);
                            myIntent.putExtra("encounterId", encounterId);
                            myIntent.putExtra("categoryName", categoryName);
                            myIntent.putExtra("patientName", patientName);

                            myIntent.putExtra("recordPosition", position);
                            myIntent.putExtra("diagnosisEditRecord", "diagnosisEditRecord");

                            startActivity(myIntent);
                        } else {
                            Toast.makeText(VoiceEMRActivity.this, "Please select one interaction before adding a record", Toast.LENGTH_SHORT).show();
                        }
                    } else if (encounterId != 0) {
                    } else {
                        Toast.makeText(VoiceEMRActivity.this, "Please select one interaction before adding a record", Toast.LENGTH_SHORT).show();
                    }


                }


            }
        });
        voiceRecordDiagnosisRV.setLayoutManager(mLayoutManagerDignosis);
        voiceRecordDiagnosisRV.setItemAnimator(new DefaultItemAnimator());
        voiceRecordDiagnosisRV.setAdapter(voiceEMRDiagnosisListAdapter);

        //for invetigationResult
        voiceEMRInvestigationResultListAdapter = new VoiceEMRInvestigationResultListAdapter(this, voiceEMRModelsInvestigationCategoryList, new VoiceEmrRecordClickListener() {
            @Override
            public void onItemClick(View v, int position, String clickActionType, String categoryName, VoiceEMRModel voiceEMRModel, String recordType) {
                if (clickActionType.equalsIgnoreCase("InvestigationAll")) {
                   /* voiceEMRModelsInvestigationCategoryList.clear();
                    voiceEMRInvestigationResultListAdapter.notifyDataSetChanged();*/
//                    deleteRecord("Investigation Records");
                } else if (clickActionType.equalsIgnoreCase("InvestigationSingle")) {

                    if (position == 0) {
                        voiceEMRModelsInvestigationCategoryList.remove(position);
                        if (voiceEMRModelsInvestigationCategoryList.size() > 0) {
                            voiceEMRModelsInvestigationCategoryList.get(position).setCategoryNameInvestigationResultsExistType(1);
                        }
                        voiceEMRInvestigationResultListAdapter.notifyDataSetChanged();
                        if (voiceEMRModelsCategoryList.size() == 0 && voiceEMRModelsDiagnosisCategoryList.size() == 0 && voiceEMRModelsInvestigationCategoryList.size() == 0) {
//                            notRecordText.setVisibility(View.VISIBLE);
                        }
                    } else {
                        voiceEMRModelsInvestigationCategoryList.remove(position);
                        voiceEMRInvestigationResultListAdapter.notifyDataSetChanged();
                        if (voiceEMRModelsCategoryList.size() == 0 && voiceEMRModelsDiagnosisCategoryList.size() == 0 && voiceEMRModelsInvestigationCategoryList.size() == 0) {
//                            notRecordText.setVisibility(View.VISIBLE);
                        }
                    }

                } else if (clickActionType.equalsIgnoreCase("InvestigationInfo")) {
                    investigationHelpDialog(VoiceEMRActivity.this);
                } else if (clickActionType.equalsIgnoreCase("InvestigationAddRecord")) {

                    investigationAddRecordClick = true;

                    if (categoryName.equalsIgnoreCase("Symptoms") || categoryName.equalsIgnoreCase("Investigation Results") || categoryName.equalsIgnoreCase("Diagnosis")) {
                        if (encounterId != 0) {
                            Intent myIntent = new Intent(VoiceEMRActivity.this, VoiceEMRCreateRecordsFormActivity.class);
                            myIntent.putExtra("patientId", patientId);
                            myIntent.putExtra("episodeId", episodeId);
                            myIntent.putExtra("encounterId", encounterId);
                            myIntent.putExtra("categoryName", categoryName);
                            myIntent.putExtra("patientName", patientName);

                            myIntent.putExtra("recordPosition", position);
                            myIntent.putExtra("InvestigationEditRecord", "");

                            startActivity(myIntent);
                        } else {
                            Toast.makeText(VoiceEMRActivity.this, "Please select one interaction before adding a record", Toast.LENGTH_SHORT).show();
                        }
                    } else if (encounterId != 0) {
                    } else {
                        Toast.makeText(VoiceEMRActivity.this, "Please select one interaction before adding a record", Toast.LENGTH_SHORT).show();
                    }


                } else if (clickActionType.equalsIgnoreCase("InvestigationEditRecord")) {

                    investigationAddRecordClick = true;

                    if (categoryName.equalsIgnoreCase("Symptoms") || categoryName.equalsIgnoreCase("Investigation Results") || categoryName.equalsIgnoreCase("Diagnosis")) {
                        if (encounterId != 0) {
                            Intent myIntent = new Intent(VoiceEMRActivity.this, VoiceEMRCreateRecordsFormActivity.class);
                            myIntent.putExtra("patientId", patientId);
                            myIntent.putExtra("episodeId", episodeId);
                            myIntent.putExtra("encounterId", encounterId);
                            myIntent.putExtra("categoryName", categoryName);
                            myIntent.putExtra("patientName", patientName);

                            myIntent.putExtra("recordPosition", position);
                            myIntent.putExtra("InvestigationEditRecord", "InvestigationEditRecord");

                            startActivity(myIntent);
                        } else {
                            Toast.makeText(VoiceEMRActivity.this, "Please select one interaction before adding a record", Toast.LENGTH_SHORT).show();
                        }
                    } else if (encounterId != 0) {
                    } else {
                        Toast.makeText(VoiceEMRActivity.this, "Please select one interaction before adding a record", Toast.LENGTH_SHORT).show();
                    }


                }


            }
        });
        voiceRecordInvestigationRV.setLayoutManager(mLayoutManagerInvestigation);
        voiceRecordInvestigationRV.setItemAnimator(new DefaultItemAnimator());
        voiceRecordInvestigationRV.setAdapter(voiceEMRInvestigationResultListAdapter);


        //for Observation
        voiceEMRObservationListAdapter = new VoiceEMRObservationListAdapter(this, voiceEMRModelsObservationCategoryList, new VoiceEmrRecordClickListener() {
            @Override
            public void onItemClick(View v, int position, String clickActionType, String categoryName, VoiceEMRModel voiceEMRModel, String recordType) {
                if (clickActionType.equalsIgnoreCase("ObservationAll")) {
                  /*  voiceEMRModelsObservationCategoryList.clear();
                    voiceEMRObservationListAdapter.notifyDataSetChanged();*/
//                    deleteRecord("Observation Records");
                } else if (clickActionType.equalsIgnoreCase("ObservationSingle")) {

                    if (position == 0) {
                        voiceEMRModelsObservationCategoryList.remove(position);
                        if (voiceEMRModelsObservationCategoryList.size() > 0) {
                            voiceEMRModelsObservationCategoryList.get(position).setCategoryNameObservationExistType(1);
                        }
                        voiceEMRObservationListAdapter.notifyDataSetChanged();
                        if (voiceEMRModelsCategoryList.size() == 0 && voiceEMRModelsDiagnosisCategoryList.size() == 0 && voiceEMRModelsInvestigationCategoryList.size() == 0) {
//                            notRecordText.setVisibility(View.VISIBLE);
                        }
                    } else {
                        voiceEMRModelsObservationCategoryList.remove(position);
                        voiceEMRObservationListAdapter.notifyDataSetChanged();
                        if (voiceEMRModelsCategoryList.size() == 0 && voiceEMRModelsDiagnosisCategoryList.size() == 0 && voiceEMRModelsInvestigationCategoryList.size() == 0) {
//                            notRecordText.setVisibility(View.VISIBLE);
                        }
                    }

                } else if (clickActionType.equalsIgnoreCase("ObservationInfo")) {
                    observationHelpDialog(VoiceEMRActivity.this);
                } else if (clickActionType.equalsIgnoreCase("ObservationAddRecord")) {

                    vitalPopupDialog(VoiceEMRActivity.this, voiceEMRModel);

                } else if (clickActionType.equalsIgnoreCase("ObservationEditRecord")) {

                    observationAddRecordClick = true;
                    if (encounterId != 0) {
                        Intent intent = new Intent(VoiceEMRActivity.this, VoiceEMRCreateRecordActivity.class);
                        intent.putExtra("CategoryId", voiceEMRModelsObservationCategoryList.get(position).getObservationCategoryId() + "");
                        intent.putExtra("CategoryName", voiceEMRModelsObservationCategoryList.get(position).getObservationCategoryName());
                        intent.putExtra("PatientId", patientId);
                        intent.putExtra("EpisodeId", episodeId);
                        intent.putExtra("EncounterID", encounterId);
                        intent.putExtra("Type", "observations");

                        intent.putExtra("recordPosition", position);
                        intent.putExtra("ObservationEditRecord", "ObservationEditRecord");

                        launchVoiceEMRRecordResults.launch(intent);
                    } else {
                        Toast.makeText(VoiceEMRActivity.this, "Please select one interaction before adding a record", Toast.LENGTH_SHORT).show();
                    }
                }


            }
        });
        voiceRecordObservationRV.setLayoutManager(mLayoutManagerObservation);
        voiceRecordObservationRV.setItemAnimator(new DefaultItemAnimator());
        voiceRecordObservationRV.setAdapter(voiceEMRObservationListAdapter);


        //for TreatmentPlan
        voiceEMRTreatmentPlanListAdapter = new VoiceEMRTreatmentPlanListAdapter(this, voiceEMRModelsTreatmentPlanCategoryList, new VoiceEmrRecordClickListener() {
            @Override
            public void onItemClick(View v, int position, String clickActionType, String categoryName, VoiceEMRModel voiceEMRModel, String recordType) {
                if (clickActionType.equalsIgnoreCase("TreatmentAll")) {
                   /* voiceEMRModelsTreatmentPlanCategoryList.clear();
                    voiceEMRTreatmentPlanListAdapter.notifyDataSetChanged();*/
//                    deleteRecord("TreatmentPlan Records");
                } else if (clickActionType.equalsIgnoreCase("TreatmentSingle")) {

                    if (position == 0) {
                        voiceEMRModelsTreatmentPlanCategoryList.remove(position);
                        if (voiceEMRModelsTreatmentPlanCategoryList.size() > 0) {
                            voiceEMRModelsTreatmentPlanCategoryList.get(position).setCategoryNameObservationExistType(1);
                        }
                        voiceEMRTreatmentPlanListAdapter.notifyDataSetChanged();
                        if (voiceEMRModelsCategoryList.size() == 0 && voiceEMRModelsDiagnosisCategoryList.size() == 0 && voiceEMRModelsInvestigationCategoryList.size() == 0) {
//                            notRecordText.setVisibility(View.VISIBLE);
                        }
                    } else {
                        voiceEMRModelsTreatmentPlanCategoryList.remove(position);
                        voiceEMRTreatmentPlanListAdapter.notifyDataSetChanged();
                        if (voiceEMRModelsCategoryList.size() == 0 && voiceEMRModelsDiagnosisCategoryList.size() == 0 && voiceEMRModelsInvestigationCategoryList.size() == 0) {
//                            notRecordText.setVisibility(View.VISIBLE);
                        }
                    }

                } else if (clickActionType.equalsIgnoreCase("TreatmentInfo")) {
                    treatmentplanHelpDialog(VoiceEMRActivity.this);
                } else if (clickActionType.equalsIgnoreCase("TreatmentAddRecord")) {

                    treatmentPlanPopupDialog(VoiceEMRActivity.this, voiceEMRModel);

                } else if (clickActionType.equalsIgnoreCase("TreatmentEditRecord")) {

                    treatmentPlanAddRecordClick = true;
                    if (encounterId != 0) {
                        Intent intent = new Intent(VoiceEMRActivity.this, VoiceEMRCreateRecordActivity.class);
                        intent.putExtra("CategoryId", voiceEMRModelsTreatmentPlanCategoryList.get(position).getTreatmentCategoryId() + "");
                        intent.putExtra("CategoryName", voiceEMRModelsTreatmentPlanCategoryList.get(position).getTreatmentCategoryName());
                        intent.putExtra("PatientId", patientId);
                        intent.putExtra("EpisodeId", episodeId);
                        intent.putExtra("EncounterID", encounterId);
                        intent.putExtra("Type", "treatmentplan");
                        intent.putExtra("recordPosition", position);
                        intent.putExtra("TreatmentEditRecord", "TreatmentEditRecord");

                        launchVoiceEMRRecordResults.launch(intent);
                    } else {
                        Toast.makeText(VoiceEMRActivity.this, "Please select one interaction before adding a record", Toast.LENGTH_SHORT).show();
                    }
                }


            }
        });
        voiceRecordTreatmentPlanRv.setLayoutManager(mLayoutManagerTreatmentPlan);
        voiceRecordTreatmentPlanRv.setItemAnimator(new DefaultItemAnimator());
        voiceRecordTreatmentPlanRv.setAdapter(voiceEMRTreatmentPlanListAdapter);

        voiceText.setMovementMethod(new ScrollingMovementMethod());

//        final int scrollAmount = voiceText.getLayout().getLineTop(voiceText.getLineCount()) - voiceText.getHeight();
//        // if there is no need to scroll, scrollAmount will be <=0
//        if (scrollAmount > 0)
//            voiceText.scrollTo(0, scrollAmount);
//        else
//            voiceText.scrollTo(0, 0);
        String simboResponse = viewModel.getLogs().toString();
        Log.d("simboResponse", "simboResponse" + simboResponse);

        viewModel.getLogs().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
//                if(otpLoadingSimbo!=null) {
//                    otpLoadingSimbo.dismiss();
//                }
//                Log.d("simbo Response++:", s);
                try {
                    if (testingVolume) {
                        Log.e("Testing volume Status", String.valueOf(testingVolume));
                    } else {
                        connectingLoaderLayout.setVisibility(View.GONE);
                        JSONObject resObj = new JSONObject(s);
                        if (resObj.has("prs")) {
                            //get Value of video
//                        String video = resObj.optString("video");
                            Log.d("simbo Response++:", s);
                            parsingSimboData(VoiceEMRActivity.this, resObj);
                        } else if (resObj.has("cmd")) {
                            //get pause and resume status
                            pauseAndResumeStatus = resObj.optString("cmd");
                            Log.d("pauseAndResumeStatus", pauseAndResumeStatus);
                            if (pauseAndResumeStatus != null && pauseAndResumeStatus.equalsIgnoreCase("pause")) {
                                if (isCloseDictationClick) {
                                    isCloseDictationClick = false;
                                    dictateIcon.setVisibility(View.GONE);
                                    dictateText.setText("Your dictation will appear here");
                                    voiceText.setVisibility(View.GONE);
                                    dictateText.setTextColor(Color.parseColor("#000000"));
                                    stopDictationButton.setEnabled(false);
                                    startDictationButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorAccent)));
                                    startDictationButton.setText("Start Dictating");
                                } else {
//                                viewModel.stopAudioRecording();
                                    dictateIcon.setVisibility(View.VISIBLE);
                                    dictateIcon.setImageResource(R.drawable.exo_icon_pause);
                                    dictateIcon.setColorFilter(dictateIcon.getContext().getResources().getColor(R.color.onboaedingImageGreenColor), PorterDuff.Mode.SRC_ATOP);
                                    dictateText.setText("Dictation Pause");
                                    dictateText.setTextColor(Color.parseColor("#5000A65A"));
                                    stopDictationButton.setEnabled(true);
                                    viewFullDictation.setEnabled(true);
                                    clearAllDictationButton.setEnabled(true);
                                    saveRecordButton.setEnabled(true);
                                    startDictationButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorAccent)));
                                    startDictationButton.setText("Resume Dictation");
                                }
                            } else if (pauseAndResumeStatus != null && pauseAndResumeStatus.equalsIgnoreCase("resume")) {
//                            checkValueLevel();
//                            voiceText.setVisibility(View.VISIBLE);
//                            sbVoiceText.append("Connected");
//                            sbVoiceText.append("\n\n");
//                            voiceText.setText(sbVoiceText);
                                dictateIcon.setVisibility(View.VISIBLE);
                                dictateIcon.setImageResource(R.drawable.ic_mic);
                                dictateIcon.setColorFilter(dictateIcon.getContext().getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.SRC_ATOP);
                                dictateText.setText("Listening...");
                                voiceText.setVisibility(View.VISIBLE);
                                dictateText.setTextColor(Color.parseColor("#00A65A"));
                                stopDictationButton.setEnabled(true);
                                viewFullDictation.setEnabled(true);
                                clearAllDictationButton.setEnabled(true);
                                saveRecordButton.setEnabled(true);
                                startDictationButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorInfo)));
                                startDictationButton.setText("Pause Dictating");
                                voiceEMRRV.setVisibility(View.VISIBLE);
//                            howToUse.setVisibility(View.GONE);
                            }

                        } else {

                            Log.d("simbo not req Res:", "");

                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });


        viewModel.getCurrentText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                Log.d("current Text", s);
                try {
                    if (s.equalsIgnoreCase("")) {


                    } else {
                        if (testingVolume) {
                            Log.e("Testing volume Status", String.valueOf(testingVolume));
                        } else {
                            timer.start();
                            if (s.equalsIgnoreCase(lastStringHold)) {
                                lastStringHold = s;
                            } else {
                                lastStringHold = s;
                                VoiceEMRFullDictationModel vefd = new VoiceEMRFullDictationModel();
                                vefd.setFullDictationText(s);
                                fullDictationArrayList.add(vefd);

                                sbVoiceText.append(s);
                                sbVoiceText.append("\n");
                                voiceText.setText(sbVoiceText);
                                voiceText.setVisibility(View.VISIBLE);

                            }
                        }
                    }
                } catch (
                        Exception e) {
                    e.printStackTrace();
                }
            }
        });


        if (startDictationButton.getText().toString().equalsIgnoreCase("Start Dictating")) {
            stopDictationButton.setEnabled(false);
            viewFullDictation.setEnabled(false);
            clearAllDictationButton.setEnabled(false);
            saveRecordButton.setEnabled(false);
        } else {
            stopDictationButton.setEnabled(true);
            viewFullDictation.setEnabled(true);
            clearAllDictationButton.setEnabled(true);
            saveRecordButton.setEnabled(true);
        }

        startDictationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isConnected) {

                    if (startDictationButton.getText().toString().equalsIgnoreCase("Start Dictating")) {
                        if (testingVolume) {
                            viewModel.stopAudioRecording();
                            testingVolume = false;
                            stopTestingClick = true;
                        }
                        connectingLoaderLayout.setVisibility(View.VISIBLE);
                        viewModel.startAudioRecording(simboDevId, simbo_authKey, simboWss1_url);//for starting simbo
                        dictateIcon.setVisibility(View.VISIBLE);
                        dictateIcon.setImageResource(R.drawable.ic_mic);
                        dictateIcon.setColorFilter(dictateIcon.getContext().getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.SRC_ATOP);
                        dictateText.setText("Listening...");
                        voiceText.setVisibility(View.GONE);
                        dictateText.setTextColor(Color.parseColor("#00A65A"));
                        stopDictationButton.setEnabled(true);
                        viewFullDictation.setEnabled(true);
                        clearAllDictationButton.setEnabled(true);
                        saveRecordButton.setEnabled(true);
                        startDictationButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorInfo)));
                        startDictationButton.setText("Pause Dictating");
                        voiceEMRRV.setVisibility(View.VISIBLE);
                        howToUse.setVisibility(View.GONE);
//                    dummyCategoryAndSubCategoryRecord();

                    } else if (startDictationButton.getText().toString().equalsIgnoreCase("Pause Dictating")) {
                        dictateIcon.setVisibility(View.VISIBLE);
                        timer.cancel();
                        isDictationPaused = true;
                        viewModel.stopAudioRecording();
                        dictateIcon.setImageResource(R.drawable.exo_icon_pause);
                        dictateIcon.setColorFilter(dictateIcon.getContext().getResources().getColor(R.color.onboaedingImageGreenColor), PorterDuff.Mode.SRC_ATOP);
                        dictateText.setText("Dictation Pause");
                        dictateText.setTextColor(Color.parseColor("#5000A65A"));
                        stopDictationButton.setEnabled(true);
                        viewFullDictation.setEnabled(true);
                        clearAllDictationButton.setEnabled(true);
                        saveRecordButton.setEnabled(true);
                        startDictationButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorAccent)));
                        startDictationButton.setText("Resume Dictation");

                    } else if (startDictationButton.getText().toString().equalsIgnoreCase("Resume Dictation")) {
                        connectingLoaderLayout.setVisibility(View.VISIBLE);
                        viewModel.startAudioRecording(simboDevId, simbo_authKey, simboWss1_url);//for starting simbo
                        if (isDictationPaused) {
                            timer.start();
                            isDictationPaused = false;
                        }
//                    dictaionPausePopupOpen = false;
                        dictateIcon.setVisibility(View.VISIBLE);
                        dictateIcon.setImageResource(R.drawable.ic_mic);
                        dictateIcon.setColorFilter(dictateIcon.getContext().getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.SRC_ATOP);
                        dictateText.setText("Listening...");
                        dictateText.setTextColor(Color.parseColor("#00A65A"));
                        stopDictationButton.setEnabled(true);
                        viewFullDictation.setEnabled(true);
                        clearAllDictationButton.setEnabled(true);
                        saveRecordButton.setEnabled(true);
                        startDictationButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorInfo)));
                        startDictationButton.setText("Pause Dictating");

                    }
                } else {
                    connectionLostPopup(VoiceEMRActivity.this);
                }

            }
        });
        stopDictationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(VoiceEMRActivity.this);
                boolean dnaCheckBox = preferences.getBoolean("dnaCheckBoxStopDictation", false);
                timer.cancel();
                if (dnaCheckBox) {
                    viewModel.stopAudioRecording();//for stopring simbo
                    isCloseDictationClick = true;
//                    optionDialog.dismiss();
                    dictateIcon.setVisibility(View.GONE);
                    dictateText.setText("Your dictation will appear here");
                    voiceText.setVisibility(View.GONE);
                    dictateText.setTextColor(Color.parseColor("#000000"));
                    stopDictationButton.setEnabled(false);
                    startDictationButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorAccent)));
                    startDictationButton.setText("Start Dictating");
                } else {
                    stopDictationDialog(VoiceEMRActivity.this);
                }

            }
        });

        clearAllDictationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clearDictation(VoiceEMRActivity.this);
            }
        });

        viewFullDictation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                VoiceEMRFullDictationBottomSheet voiceEMRFullDictationBottomSheet = new VoiceEMRFullDictationBottomSheet(VoiceEMRActivity.this, fullDictationArrayList);
                voiceEMRFullDictationBottomSheet.setupConfig(VoiceEMRActivity.this, fullDictationArrayList);
                voiceEMRFullDictationBottomSheet.show(getSupportFragmentManager(), "Bottom Sheet Dialog Fragment");

//                voiceEMRFullDictationBottomSheet.show(VoiceEMRActivity.this.getSupportFragmentManager(), "filter Sheet Dialog Fragment");
//                micPermissionDialog(VoiceEMRActivity.this);

//                dictationStoppedDialog(VoiceEMRActivity.this);

//                clearDictation(VoiceEMRActivity.this);

//                voiceEMRHelpBottomSheet.show(VoiceEMRActivity.this.getSupportFragmentManager(), "");

//                dictationPaused(VoiceEMRActivity.this);
            }
        });

//        //for dialog permission popup
//
//        micPermissionDialog(VoiceEMRActivity.this);

        requestAudioPermissions();

        GivePermissionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Give user option to still opt-in the permissions
                ActivityCompat.requestPermissions(VoiceEMRActivity.this,
                        new String[]{Manifest.permission.RECORD_AUDIO},
                        MY_PERMISSIONS_RECORD_AUDIO);
            }
        });

        micTestingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (micTestingButton.getText().toString().equalsIgnoreCase("START TESTING MIC")) {
                    stopTestingClick = false;
                    testingVolume = true;
                    checkValueLevel();
//                    micTestingButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorDanger)));
//                    micTestingButton.setText("STOP TESTING");
//
//                    AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
//                    int volumeLevel = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
//                    int maxVolumeLevel = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
//                    int volumePercent = (int) (((float) volumeLevel / maxVolumeLevel) * 100);
//                    volumeProgressBar.setMax(maxVolumeLevel);
//                    volumeProgressBar.setProgress(volumeLevel);
                } else {
                    viewModel.stopAudioRecording();
                    micTestingButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorAccent)));
                    micTestingButton.setText("START TESTING MIC");
                    testingVolume = false;
                    stopTestingClick = true;

//                    AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
//                    int volumeLevel = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
//                    int maxVolumeLevel = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
//                    int volumePercent = (int) (((float) volumeLevel / maxVolumeLevel) * 100);
//                    volumeProgressBar.setMax(maxVolumeLevel);
//                    volumeProgressBar.setProgress(0);
                    volumeProgressBar.setVisibility(View.GONE);
                    volumeProgressBarWhite.setVisibility(View.VISIBLE);
                }
            }
        });

        saveRecordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (dictitationRecords.size() == 0) {
                    Toast.makeText(getApplicationContext(), "No record to save", Toast.LENGTH_LONG).show();
                } else {
                    viewModel.stopAudioRecording();
                    try {
                        Set<String> keys = dictitationRecords.keySet();
                        for (String key : keys) {
                            DictationModel dictationObject = dictitationRecords.get(key);
                            VoiceEMRModel emrObject = dictationObject.getVoiceEMRModel();

                            if (dictationObject.getSection_name().equalsIgnoreCase("Symptoms")) {
                                JSONObject symptomObject = new JSONObject();
                                symptomObject.put("patient_id", patientId);
                                symptomObject.put("episode_id", episodeId);
                                symptomObject.put("encounter_id", encounterId);
                                symptomObject.put("first_reported_on", emrObject.getSymptomFirstReportedOn());
                                symptomObject.put("symptom_name", emrObject.getSymptomName());
                                symptomObject.put("symptom_description", emrObject.getSymptomDescription());
                                symptomObject.put("symptom_status", emrObject.getSymptomStatus());
                                symptomArrayObject.put(symptomObject);
                            } else if (dictationObject.getSection_name().equalsIgnoreCase("Diagnosis")) {
                                JSONObject diagnosisObject = new JSONObject();

                                diagnosisObject.put("patient_id", patientId);
                                diagnosisObject.put("episode_id", episodeId);
                                diagnosisObject.put("encounter_id", encounterId);
                                diagnosisObject.put("selectedFromAutocomplete", emrObject.isDiagnosisSelectedFromAutocomplete());
                                diagnosisObject.put("diagnosis", emrObject.getDiagnosisDiagnosis());
                                diagnosisObject.put("status", emrObject.getDiagnosisStatus());
                                diagnosisObject.put("posited_on", emrObject.getDiagnosisPosited_on());
                                diagnosisObject.put("confirmed_ruledout_on", emrObject.getDiagnosisConfirmed_ruledout_on());
                                diagnosisArrayObject.put(diagnosisObject);

                            } else if (dictationObject.getSection_name().equalsIgnoreCase("Investigation Results")) {
                                JSONObject investigationObject = new JSONObject();

                                investigationObject.put("patient_id", patientId);
                                investigationObject.put("episode_id", episodeId);
                                investigationObject.put("encounter_id", encounterId);
                                if (emrObject.getInvestigationFile_url() != null) {
                                    investigationObject.put("file_url", emrObject.getInvestigationFile_url());
                                } else {
                                    investigationObject.put("file_url", "");
                                }
                                investigationObject.put("file_type", emrObject.getInvestigatioFfile_type());
                                investigationObject.put("investigation_name", emrObject.getInvestigationInvestigation_name());
                                investigationObject.put("parameter", emrObject.getInvestigationParameter());
                                investigationObject.put("value", emrObject.getInvestigationValue());
                                investigationObject.put("notes", emrObject.getInvestigationNotes());

                                investigationArrayObject.put(investigationObject);
                            } else if (dictationObject.getSection_name().equalsIgnoreCase("Observation")) {
                                JSONObject observationObject = new JSONObject();

                                observationObject.put("patient_id", patientId);
                                observationObject.put("episode_id", episodeId);
                                observationObject.put("encounter_id", encounterId);
                                observationObject.put("category_id", emrObject.getObservationCategoryId());
                                observationObject.put("type", "observations");


                                JSONObject record_Data = new JSONObject();
                                record_Data.put("user_id", ApiUrls.doctorId);
                                record_Data.put("doctor_id", ApiUrls.doctorId);
                                record_Data.put("patient_id", patientId);
                                record_Data.put("catid", emrObject.getObservationCategoryId());
                                observationObject.put("record_data", record_Data);

                                String observationRecords = emrObject.getObservationCategoryRecords();
                                JSONObject observationRecordsJsonObject = new JSONObject(observationRecords);
                                observationObject.put("metadata", observationRecordsJsonObject);
                                JSONArray dietdata = new JSONArray();
                                observationObject.put("dietdata", dietdata);

                                observationArrayObject.put(observationObject);

                            } else if (dictationObject.getSection_name().equalsIgnoreCase("Treatment Plan")) {
                                JSONObject treatmentplanObject = new JSONObject();

                                treatmentplanObject.put("patient_id", patientId);
                                treatmentplanObject.put("episode_id", episodeId);
                                treatmentplanObject.put("encounter_id", encounterId);
                                treatmentplanObject.put("category_id", emrObject.getTreatmentCategoryId());
                                treatmentplanObject.put("type", "treatmentplan");


                                JSONObject record_Data = new JSONObject();
                                record_Data.put("user_id", ApiUrls.doctorId);
                                record_Data.put("doctor_id", ApiUrls.doctorId);
                                record_Data.put("patient_id", patientId);
                                record_Data.put("catid", emrObject.getTreatmentCategoryId());
                                treatmentplanObject.put("record_data", record_Data);
                                String treatmentRecords = emrObject.getTreatmentCategoryRecords();
                                JSONObject treatmentRecordsJsonObject = new JSONObject(treatmentRecords);
                                treatmentplanObject.put("metadata", treatmentRecordsJsonObject);

                                JSONArray dietdata = new JSONArray();
                                treatmentplanObject.put("dietdata", dietdata);

                                treatmentPlanArrayObject.put(treatmentplanObject);
                            }
                        }

                        if (symptomArrayObject.length() > 0) {
                            saveSymptomRecords();
                        }
                        if (diagnosisArrayObject.length() > 0) {
                            saveDiagnosisRecords();
                        }
                        if (investigationArrayObject.length() > 0) {
                            saveInvestigationRecords();
                        }
                        if (observationArrayObject.length() > 0) {
                            saveObservationRecords();
                        }
                        if (treatmentPlanArrayObject.length() > 0) {
                            saveTreatmentPlanRecords();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                }

            }
        });

        getEpisodeFilePreferences();


    }


    public void getSimboApiKey() {
        loadingDialog = new ProgressDialog(VoiceEMRActivity.this);
//        loadingDialog.setMessage("Generating invoice");
        loadingDialog.setTitle(getResources().getString(R.string.please_wait));
        loadingDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        loadingDialog.setCancelable(false);
        loadingDialog.show();
        voiceEMRViewModel.getSimboAuthKey(VoiceEMRActivity.this).observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                //emrCaseHistoryProgressbar.setVisibility(View.GONE);
                loadingDialog.dismiss();
                try {
                    JSONObject response = new JSONObject(s);
                    if (response.getInt("status_code") == 200) {
                        String simboAuthKey = response.getJSONObject("response").getJSONObject("response").getString("authKey");
                        String wss1_url = response.getJSONObject("response").getJSONObject("response").getString("wss1_url");
                        String devId = response.getJSONObject("response").getJSONObject("response").getString("devId");
                        simbo_authKey = simboAuthKey;
                        simboWss1_url = wss1_url;
                        simboDevId = devId;


                    } else {
                        loadingDialog.dismiss();
                        ErrorHandlerClass.INSTANCE.errorHandler(VoiceEMRActivity.this, s);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    loadingDialog.dismiss();
                }
            }
        });

    }


    public void micPermissionDialog(Activity activity) {

        ViewGroup viewGroup = activity.findViewById(android.R.id.content);
        View dialogView = LayoutInflater.from(activity).inflate(R.layout.dialog_permission, viewGroup, false);
        RelativeLayout yes = dialogView.findViewById(R.id.yes);
        RelativeLayout no = dialogView.findViewById(R.id.no);
        ImageButton closeButton = dialogView.findViewById(R.id.closeButton);
        CheckBox dnaCheckBox = dialogView.findViewById(R.id.dnaCheckBox);

        dnaCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                                                   @Override
                                                   public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                                                       if (isChecked) {
                                                           SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(VoiceEMRActivity.this);
                                                           SharedPreferences.Editor editor = preferences.edit();
                                                           editor.putBoolean("dnaCheckBox", isChecked);
                                                           editor.apply();

                                                       } else {
                                                           SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(VoiceEMRActivity.this);
                                                           SharedPreferences.Editor editor = preferences.edit();
                                                           editor.putBoolean("dnaCheckBox", isChecked);
                                                           editor.apply();
                                                       }

                                                   }
                                               }
        );

        //Now we need an AlertDialog.Builder object
//        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        AlertDialog optionDialog = new AlertDialog.Builder(this).create();
        optionDialog.setCancelable(false);

        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Give user option to still opt-in the permissions
                ActivityCompat.requestPermissions(VoiceEMRActivity.this,
                        new String[]{Manifest.permission.RECORD_AUDIO},
                        MY_PERMISSIONS_RECORD_AUDIO);
                optionDialog.dismiss();


                stopDictationButton.setEnabled(true);
                viewFullDictation.setEnabled(true);
                clearAllDictationButton.setEnabled(true);
                saveRecordButton.setEnabled(true);
                startDictationButton.setEnabled(true);
            }
        });

        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                optionDialog.dismiss();
                micPermissionNotGiven.setVisibility(View.VISIBLE);
                howToUse.setVisibility(View.GONE);

                stopDictationButton.setEnabled(false);
                viewFullDictation.setEnabled(false);
                clearAllDictationButton.setEnabled(false);
                saveRecordButton.setEnabled(false);
                startDictationButton.setEnabled(false);


            }
        });

        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(VoiceEMRActivity.this);
                boolean dnaCheckBox = preferences.getBoolean("dnaCheckBox", false);
                if (dnaCheckBox) {
                    micPermissionNotGiven.setVisibility(View.VISIBLE);
                    howToUse.setVisibility(View.GONE);
                    optionDialog.dismiss();

                    stopDictationButton.setEnabled(false);
                    viewFullDictation.setEnabled(false);
                    clearAllDictationButton.setEnabled(false);
                    saveRecordButton.setEnabled(false);
                    startDictationButton.setEnabled(false);
                } else {
                    optionDialog.dismiss();
                    micPermissionNotGiven.setVisibility(View.VISIBLE);
                    howToUse.setVisibility(View.GONE);

                    stopDictationButton.setEnabled(false);
                    viewFullDictation.setEnabled(false);
                    clearAllDictationButton.setEnabled(false);
                    saveRecordButton.setEnabled(false);
                    startDictationButton.setEnabled(false);
                }

            }
        });

//        //When permission is not granted by user, show them message why this permission is needed.
//        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
//                Manifest.permission.RECORD_AUDIO)) {
//            Toast.makeText(this, "Please grant permissions to record audio", Toast.LENGTH_LONG).show();
//
//            //Give user option to still opt-in the permissions
//            ActivityCompat.requestPermissions(this,
//                    new String[]{Manifest.permission.RECORD_AUDIO},
//                    MY_PERMISSIONS_RECORD_AUDIO);
//
//        } else {
//            // Show user dialog to grant permission to record audio
//            ActivityCompat.requestPermissions(this,
//                    new String[]{Manifest.permission.RECORD_AUDIO},
//                    MY_PERMISSIONS_RECORD_AUDIO);
//        }


//        //Now we need an AlertDialog.Builder object
//        AlertDialog.Builder builder = new AlertDialog.Builder(activity);


        //setting the view of the builder to our custom view that we already inflated
        optionDialog.setView(dialogView);

        //finally creating the alert dialog and displaying it
//        final AlertDialog alertDialog = builder.create();
        if (!isFinishing()) {
            optionDialog.show();
        }

    }

    public void stopDictationDialog(Activity activity) {

        ViewGroup viewGroup = activity.findViewById(android.R.id.content);
        View dialogView = LayoutInflater.from(activity).inflate(R.layout.dialog_stop_dictation, viewGroup, false);
//        Button yes = dialogView.findViewById(R.id.yes);
//        Button no = dialogView.findViewById(R.id.no);
        ImageButton closeButton = dialogView.findViewById(R.id.closeButton);
        RelativeLayout closeButtonForm = dialogView.findViewById(R.id.closeButtonForm);
        CheckBox dnaCheckBox = dialogView.findViewById(R.id.dnaCheckBox);
        TextImageClass dialog_text2 = dialogView.findViewById(R.id.dialog_text2);
        TextImageClass dialog_text3 = dialogView.findViewById(R.id.dialog_text3);

        dialog_text2.setText("You can Edit ( [edit] )");
        dialog_text2.addImage("[edit]", R.drawable.ic_edit, 30, 30);

        dialog_text3.setText("and Delete ([delete]) any entries from your dictation BEFORE saving them.");
        dialog_text3.addImage("[delete]", R.drawable.ic_delete, 30, 30);


        dnaCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                                                   @Override
                                                   public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                                                       if (isChecked) {
                                                           SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(VoiceEMRActivity.this);
                                                           SharedPreferences.Editor editor = preferences.edit();
                                                           editor.putBoolean("dnaCheckBoxStopDictation", isChecked);
                                                           editor.apply();

                                                       } else {
                                                           SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(VoiceEMRActivity.this);
                                                           SharedPreferences.Editor editor = preferences.edit();
                                                           editor.putBoolean("dnaCheckBoxStopDictation", isChecked);
                                                           editor.apply();
                                                       }

                                                   }
                                               }
        );

        //Now we need an AlertDialog.Builder object
//        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        AlertDialog optionDialog = new AlertDialog.Builder(this).create();
        optionDialog.setCancelable(false);


        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewModel.stopAudioRecording();//for stopring simbo
                isCloseDictationClick = true;
                optionDialog.dismiss();
                dictateIcon.setVisibility(View.GONE);
                dictateText.setText("Your dictation will appear here");
                voiceText.setVisibility(View.GONE);
                dictateText.setTextColor(Color.parseColor("#000000"));
                stopDictationButton.setEnabled(false);
                startDictationButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorAccent)));
                startDictationButton.setText("Start Dictating");

            }
        });

        closeButtonForm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewModel.stopAudioRecording();//for stopring simbo
                isCloseDictationClick = true;
                optionDialog.dismiss();
                dictateIcon.setVisibility(View.GONE);
                dictateText.setText("Your dictation will appear here");
                dictateText.setTextColor(Color.parseColor("#000000"));
                stopDictationButton.setEnabled(false);
                startDictationButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorAccent)));
                startDictationButton.setText("Start Dictating");
            }
        });

        //setting the view of the builder to our custom view that we already inflated
        optionDialog.setView(dialogView);

        //finally creating the alert dialog and displaying it
//        final AlertDialog alertDialog = builder.create();
        if (!isFinishing()) {
            optionDialog.show();
        }

    }


    public void saveRecordDialog(Activity activity) {

        ViewGroup viewGroup = activity.findViewById(android.R.id.content);
        View dialogView = LayoutInflater.from(activity).inflate(R.layout.dialog_saved_records, viewGroup, false);
        ImageButton closeButton = dialogView.findViewById(R.id.closeButton);
        RelativeLayout closeButtonForm = dialogView.findViewById(R.id.closeButtonForm);
        TextImageClass dialog_text2 = dialogView.findViewById(R.id.dialog_text2);
        TextImageClass dialog_text3 = dialogView.findViewById(R.id.dialog_text3);

        dialog_text2.setText("You can now generate a Prescription ([rx])");
        dialog_text2.addImage("[rx]", R.drawable.ic_prescription, 30, 30);

        dialog_text3.setText("and Share ([share]) it with the patient.");
        dialog_text3.addImage("[share]", R.drawable.ic_share2, 30, 30);

//        //Now we need an AlertDialog.Builder object
//        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
//
//        //setting the view of the builder to our custom view that we already inflated
//        builder.setView(dialogView);
//
//        //finally creating the alert dialog and displaying it
//        final AlertDialog alertDialog = builder.create();
//        alertDialog.show();

        //Now we need an AlertDialog.Builder object
//        AlertDialog.Builder builder = new AlertDialog.Builder(activity);


        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveRecordDialog.dismiss();
                viewModel.startAudioRecording(simboDevId, simbo_authKey, simboWss1_url);

                records_adapter_List.clear();
                dictitationRecords.clear();
                itemSerailNum.clear();
                dicationAdapter.notifyDataSetChanged();
                viewModel.startAudioRecording(simboDevId, simbo_authKey, simboWss1_url);

                /*voiceEMRModelsCategoryList.clear();
                voiceEMRModelsDiagnosisCategoryList.clear();
                voiceEMRModelsInvestigationCategoryList.clear();
                voiceEMRModelsObservationCategoryList.clear();
                voiceEMRModelsTreatmentPlanCategoryList.clear();
                voiceEMRListAdapter.notifyDataSetChanged();
                voiceEMRDiagnosisListAdapter.notifyDataSetChanged();
                voiceEMRInvestigationResultListAdapter.notifyDataSetChanged();
                voiceEMRObservationListAdapter.notifyDataSetChanged();
                voiceEMRTreatmentPlanListAdapter.notifyDataSetChanged();*/


//                dictateIcon.setVisibility(View.GONE);
//                dictateText.setText("Your dictation will appear here");
//                voiceText.setVisibility(View.GONE);
//                dictateText.setTextColor(Color.parseColor("#000000"));
//                stopDictationButton.setEnabled(false);
//                startDictationButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorAccent)));
//                startDictationButton.setText("Start Dictating");

            }
        });

        closeButtonForm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveRecordDialog.dismiss();

                records_adapter_List.clear();
                dictitationRecords.clear();
                itemSerailNum.clear();
                dicationAdapter.notifyDataSetChanged();
                viewModel.startAudioRecording(simboDevId, simbo_authKey, simboWss1_url);

                /*viewModel.startAudioRecording(simboDevId, simbo_authKey, simboWss1_url);
                voiceEMRModelsCategoryList.clear();
                voiceEMRModelsDiagnosisCategoryList.clear();
                voiceEMRModelsInvestigationCategoryList.clear();
                voiceEMRModelsObservationCategoryList.clear();
                voiceEMRModelsTreatmentPlanCategoryList.clear();
                voiceEMRListAdapter.notifyDataSetChanged();
                voiceEMRDiagnosisListAdapter.notifyDataSetChanged();
                voiceEMRInvestigationResultListAdapter.notifyDataSetChanged();
                voiceEMRObservationListAdapter.notifyDataSetChanged();
                voiceEMRTreatmentPlanListAdapter.notifyDataSetChanged();*/
//                dictateIcon.setVisibility(View.GONE);
//                dictateText.setText("Your dictation will appear here");
//                dictateText.setTextColor(Color.parseColor("#000000"));
//                stopDictationButton.setEnabled(false);
//                startDictationButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorAccent)));
//                startDictationButton.setText("Start Dictating");
            }
        });

        //setting the view of the builder to our custom view that we already inflated
        saveRecordDialog.setView(dialogView);

        //finally creating the alert dialog and displaying it
//        final AlertDialog alertDialog = builder.create();

        if (!isFinishing()) {
            saveRecordDialog.show();
        }
    }

    public void dictationStoppedDialog(Activity activity) {

        ViewGroup viewGroup = activity.findViewById(android.R.id.content);
        View dialogView = LayoutInflater.from(activity).inflate(R.layout.dialog_stop_dictation, viewGroup, false);
        TextImageClass dialog_text2 = dialogView.findViewById(R.id.dialog_text2);
        TextImageClass dialog_text3 = dialogView.findViewById(R.id.dialog_text3);

        dialog_text2.setText("You can Edit ( [edit] )");
        dialog_text2.addImage("[edit]", R.drawable.ic_edit, 30, 30);

        dialog_text3.setText("and Delete ([delete]) any entries from your dictation BEFORE saving them.");
        dialog_text3.addImage("[delete]", R.drawable.ic_delete, 30, 30);

        //Now we need an AlertDialog.Builder object
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);

        //setting the view of the builder to our custom view that we already inflated
        builder.setView(dialogView);

        //finally creating the alert dialog and displaying it
        final AlertDialog alertDialog = builder.create();
        alertDialog.show();

    }

    public void clearDictation(Activity activity) {
        ViewGroup viewGroup = activity.findViewById(android.R.id.content);
        View dialogView = LayoutInflater.from(activity).inflate(R.layout.dialog_clear_dictation, viewGroup, false);
        RelativeLayout yes = dialogView.findViewById(R.id.yes);
        RelativeLayout no = dialogView.findViewById(R.id.no);
        ImageButton closeButton = dialogView.findViewById(R.id.closeButton);


        AlertDialog optionDialog = new AlertDialog.Builder(this).create();
        optionDialog.setCancelable(false);

        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                optionDialog.dismiss();

//                voiceEMRModelsCategoryList.clear();
//                voiceEMRModelsDiagnosisCategoryList.clear();
//                voiceEMRModelsInvestigationCategoryList.clear();
//                voiceEMRModelsObservationCategoryList.clear();
//                voiceEMRModelsTreatmentPlanCategoryList.clear();
//                voiceEMRListAdapter.notifyDataSetChanged();
//                voiceEMRDiagnosisListAdapter.notifyDataSetChanged();
//                voiceEMRInvestigationResultListAdapter.notifyDataSetChanged();
//                voiceEMRObservationListAdapter.notifyDataSetChanged();
//                voiceEMRTreatmentPlanListAdapter.notifyDataSetChanged();
                records_adapter_List.clear();
                dictitationRecords.clear();
                itemSerailNum.clear();
                dicationAdapter.notifyDataSetChanged();
                fullDictationArrayList.clear();
                voiceText.setText("");


                viewFullDictation.setEnabled(false);
                clearAllDictationButton.setEnabled(false);
                saveRecordButton.setEnabled(false);
                dictateIcon.setVisibility(View.GONE);
                dictateText.setText("Your dictation will appear here");
                dictateText.setTextColor(Color.parseColor("#000000"));
                voiceText.setVisibility(View.GONE);
                stopDictationButton.setEnabled(false);
                startDictationButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorAccent)));
                startDictationButton.setText("Start Dictating");
                voiceEMRRV.setVisibility(View.GONE);
                howToUse.setVisibility(View.VISIBLE);
                viewModel.stopAudioRecording();
//                notRecordText.setVisibility(View.GONE);
            }
        });

        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                optionDialog.dismiss();
            }
        });

        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                optionDialog.dismiss();
            }
        });

        //setting the view of the builder to our custom view that we already inflated
        optionDialog.setView(dialogView);
        if (!isFinishing()) {
            optionDialog.show();
        }

    }

    public void deleteRecord(String recordType, VoiceEMRModel voiceEMRModel) {
        ViewGroup viewGroup = this.findViewById(android.R.id.content);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_clear_dictation, viewGroup, false);
        RelativeLayout yes = dialogView.findViewById(R.id.yes);
        RelativeLayout no = dialogView.findViewById(R.id.no);
        ImageButton closeButton = dialogView.findViewById(R.id.closeButton);
        TextView record_type = dialogView.findViewById(R.id.record_type);

        record_type.setText("Do you want to clear all " + recordType);


        AlertDialog optionDialog = new AlertDialog.Builder(this).create();
        optionDialog.setCancelable(false);

        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                optionDialog.dismiss();
                if (recordType.equalsIgnoreCase("Symptoms")) {
                    List<Integer> deletedArrayValue = new ArrayList<Integer>();
                    for (int i = 0; i < records_adapter_List.size(); i++) {
                        if (records_adapter_List.get(i).getRecordType().equalsIgnoreCase("Symptoms")) {
                            deletedArrayValue.add(i);
//                            records_adapter_List.remove(i);
                        }
                    }

                    for (int j = 0; j < deletedArrayValue.size(); j++) {
                        records_adapter_List.remove(deletedArrayValue.get(j) - j);
                    }

                    List<DictationModel> values = new ArrayList<DictationModel>(dictitationRecords.values());

                    for (int j = 0; j < values.size(); j++) {
                        if (values.get(j).getSection_name().equalsIgnoreCase("Symptoms")) {
                            dictitationRecords.remove(values.get(j).getVoiceEMRModel().getSymptomData_id());
                        }

                    }
                    itemSerailNum.clear();
                    int serialNum = 0;
                    for (int i = 0; i < records_adapter_List.size(); i++) {
                        if (records_adapter_List.get(i).getType() == 0) {
                            serialNum = 0;
                            itemSerailNum.add(serialNum + "");
                        } else {
                            itemSerailNum.add(++serialNum + "");
                        }
                    }
                    dicationAdapter.notifyDataSetChanged();

                } else if (recordType.equalsIgnoreCase("Diagnosis")) {

                    List<Integer> deletedArrayValue = new ArrayList<Integer>();
                    for (int i = 0; i < records_adapter_List.size(); i++) {
                        if (records_adapter_List.get(i).getRecordType().equalsIgnoreCase("Diagnosis")) {
//                            records_adapter_List.remove(i);
                            deletedArrayValue.add(i);
                        }
                    }

                    for (int j = 0; j < deletedArrayValue.size(); j++) {
                        records_adapter_List.remove(deletedArrayValue.get(j) - j);
                    }

                    List<DictationModel> values = new ArrayList<DictationModel>(dictitationRecords.values());

                    for (int j = 0; j < values.size(); j++) {
                        if (values.get(j).getSection_name().equalsIgnoreCase("Diagnosis")) {
                            dictitationRecords.remove(values.get(j).getVoiceEMRModel().getDiagnosisData_id());
                        }

                    }
                    itemSerailNum.clear();
                    int serialNum = 0;
                    for (int i = 0; i < records_adapter_List.size(); i++) {
                        if (records_adapter_List.get(i).getType() == 0) {
                            serialNum = 0;
                            itemSerailNum.add(serialNum + "");
                        } else {
                            itemSerailNum.add(++serialNum + "");
                        }
                    }
                    dicationAdapter.notifyDataSetChanged();

                } else if (recordType.equalsIgnoreCase("Investigation Results")) {
                    List<Integer> deletedArrayValue = new ArrayList<Integer>();
                    for (int i = 0; i < records_adapter_List.size(); i++) {
                        if (records_adapter_List.get(i).getRecordType().equalsIgnoreCase("Investigation Results")) {
//                            records_adapter_List.remove(i);
                            deletedArrayValue.add(i);
                        }
                    }

                    for (int j = 0; j < deletedArrayValue.size(); j++) {
                        records_adapter_List.remove(deletedArrayValue.get(j) - j);
                    }

                    List<DictationModel> values = new ArrayList<DictationModel>(dictitationRecords.values());

                    for (int j = 0; j < values.size(); j++) {
                        if (values.get(j).getSection_name().equalsIgnoreCase("Investigation Results")) {
                            dictitationRecords.remove(values.get(j).getVoiceEMRModel().getInvestigationData_id());
                        }

                    }
                    itemSerailNum.clear();
                    int serialNum = 0;
                    for (int i = 0; i < records_adapter_List.size(); i++) {
                        if (records_adapter_List.get(i).getType() == 0) {
                            serialNum = 0;
                            itemSerailNum.add(serialNum + "");
                        } else {
                            itemSerailNum.add(++serialNum + "");
                        }
                    }
                    dicationAdapter.notifyDataSetChanged();

                } else if (recordType.equalsIgnoreCase("Observation")) {
                    List<Integer> deletedArrayValue = new ArrayList<Integer>();
                    for (int i = 0; i < records_adapter_List.size(); i++) {
                        if (records_adapter_List.get(i).getRecordType().equalsIgnoreCase("Observation")) {
                            deletedArrayValue.add(i);
//                            records_adapter_List.remove(i);
                        }
                    }

                    for (int j = 0; j < deletedArrayValue.size(); j++) {
                        records_adapter_List.remove(deletedArrayValue.get(j) - j);
                    }

                    List<DictationModel> values = new ArrayList<DictationModel>(dictitationRecords.values());

                    for (int j = 0; j < values.size(); j++) {
                        if (values.get(j).getSection_name().equalsIgnoreCase("Observation")) {
                            dictitationRecords.remove(values.get(j).getVoiceEMRModel().getObservationData_id());
                        }

                    }
                    itemSerailNum.clear();
                    int serialNum = 0;
                    for (int i = 0; i < records_adapter_List.size(); i++) {
                        if (records_adapter_List.get(i).getType() == 0) {
                            serialNum = 0;
                            itemSerailNum.add(serialNum + "");
                        } else {
                            itemSerailNum.add(++serialNum + "");
                        }
                    }
                    dicationAdapter.notifyDataSetChanged();

                } else if (recordType.equalsIgnoreCase("Treatment Plan")) {
                    List<Integer> deletedArrayValue = new ArrayList<Integer>();
                    for (int i = 0; i < records_adapter_List.size(); i++) {
                        if (records_adapter_List.get(i).getRecordType().equalsIgnoreCase("Treatment Plan")) {
                            deletedArrayValue.add(i);
//                            records_adapter_List.remove(i);
                        }
                    }

                    for (int j = 0; j < deletedArrayValue.size(); j++) {
                        records_adapter_List.remove(deletedArrayValue.get(j) - j);
                    }

                    List<DictationModel> values = new ArrayList<DictationModel>(dictitationRecords.values());

                    for (int j = 0; j < values.size(); j++) {
                        if (values.get(j).getSection_name().equalsIgnoreCase("Treatment Plan")) {
                            dictitationRecords.remove(values.get(j).getVoiceEMRModel().getTreatmentPlanData_id());
                        }

                    }
                    itemSerailNum.clear();
                    int serialNum = 0;
                    for (int i = 0; i < records_adapter_List.size(); i++) {
                        if (records_adapter_List.get(i).getType() == 0) {
                            serialNum = 0;
                            itemSerailNum.add(serialNum + "");
                        } else {
                            itemSerailNum.add(++serialNum + "");
                        }
                    }
                    dicationAdapter.notifyDataSetChanged();

                }
//                dicationAdapter.notifyDataSetChanged();

            }
        });

        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                optionDialog.dismiss();
            }
        });

        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                optionDialog.dismiss();
            }
        });

        //setting the view of the builder to our custom view that we already inflated
        optionDialog.setView(dialogView);
        if (!isFinishing()) {
            optionDialog.show();
        }

    }


    public void backToHomeAlertMessage(Activity activity) {
        ViewGroup viewGroup = activity.findViewById(android.R.id.content);
        View dialogView = LayoutInflater.from(activity).inflate(R.layout.dialog_back_home_dictation, viewGroup, false);
        RelativeLayout yes = dialogView.findViewById(R.id.yes);
        RelativeLayout no = dialogView.findViewById(R.id.no);
        ImageButton closeButton = dialogView.findViewById(R.id.closeButton);


        AlertDialog optionDialog = new AlertDialog.Builder(this).create();
        optionDialog.setCancelable(false);

        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                optionDialog.dismiss();

//                voiceEMRModelsCategoryList.clear();
//                voiceEMRModelsDiagnosisCategoryList.clear();
//                voiceEMRModelsInvestigationCategoryList.clear();
//                voiceEMRModelsObservationCategoryList.clear();
//                voiceEMRModelsTreatmentPlanCategoryList.clear();
//                voiceEMRListAdapter.notifyDataSetChanged();
//                voiceEMRDiagnosisListAdapter.notifyDataSetChanged();
//                voiceEMRInvestigationResultListAdapter.notifyDataSetChanged();
//                voiceEMRObservationListAdapter.notifyDataSetChanged();
//                voiceEMRTreatmentPlanListAdapter.notifyDataSetChanged();

                records_adapter_List.clear();
                dictitationRecords.clear();
                itemSerailNum.clear();
                dicationAdapter.notifyDataSetChanged();

                fullDictationArrayList.clear();
                cancelVolleyRequest();// added by dileep
                finish();


//                viewFullDictation.setEnabled(false);
//                clearAllDictationButton.setEnabled(false);
//                saveRecordButton.setEnabled(false);
//                dictateIcon.setVisibility(View.GONE);
//                dictateText.setText("Your dictation will appear here");
//                dictateText.setTextColor(Color.parseColor("#000000"));
//                voiceText.setVisibility(View.GONE);
//                stopDictationButton.setEnabled(false);
//                startDictationButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorAccent)));
//                startDictationButton.setText("Start Dictating");
//                voiceEMRRV.setVisibility(View.GONE);
//                howToUse.setVisibility(View.VISIBLE);
            }
        });

        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                optionDialog.dismiss();
            }
        });

        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                optionDialog.dismiss();
            }
        });

        //setting the view of the builder to our custom view that we already inflated
        optionDialog.setView(dialogView);
        if (!isFinishing()) {
            optionDialog.show();
        }

    }


    public void dictationPaused(Activity activity) {
        ViewGroup viewGroup = activity.findViewById(android.R.id.content);
        View dialogView = LayoutInflater.from(activity).inflate(R.layout.dialog_voice_pause, viewGroup, false);
        RelativeLayout pauseButton = dialogView.findViewById(R.id.pauseButton);
        RelativeLayout resumeButton = dialogView.findViewById(R.id.resumeButton);
        ImageButton closeButton = dialogView.findViewById(R.id.closeButton);


        viewModel.stopAudioRecording();
        dictateIcon.setVisibility(View.VISIBLE);
        dictateIcon.setImageResource(R.drawable.exo_icon_pause);
        dictateIcon.setColorFilter(dictateIcon.getContext().getResources().getColor(R.color.onboaedingImageGreenColor), PorterDuff.Mode.SRC_ATOP);
        dictateText.setText("Dictation Pause");
        dictateText.setTextColor(Color.parseColor("#5000A65A"));
        stopDictationButton.setEnabled(true);
        viewFullDictation.setEnabled(true);
        clearAllDictationButton.setEnabled(true);
        saveRecordButton.setEnabled(true);
        startDictationButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorAccent)));
        startDictationButton.setText("Resume Dictation");

        AlertDialog optionDialog = new AlertDialog.Builder(this).create();
        optionDialog.setCancelable(false);

        pauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                optionDialog.dismiss();
//                dictaionPausePopupOpen = true;
            }
        });

        resumeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                optionDialog.dismiss();
//                dictaionPausePopupOpen = true;
                connectingLoaderLayout.setVisibility(View.VISIBLE);
                viewModel.startAudioRecording(simboDevId, simbo_authKey, simboWss1_url);//for starting simbo
                dictateIcon.setVisibility(View.VISIBLE);
                dictateIcon.setImageResource(R.drawable.ic_mic);
                dictateIcon.setColorFilter(dictateIcon.getContext().getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.SRC_ATOP);
                dictateText.setText("Listening...");
                dictateText.setTextColor(Color.parseColor("#00A65A"));
                stopDictationButton.setEnabled(true);
                viewFullDictation.setEnabled(true);
                clearAllDictationButton.setEnabled(true);
                saveRecordButton.setEnabled(true);
                startDictationButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorInfo)));
                startDictationButton.setText("Pause Dictating");

            }
        });
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                optionDialog.dismiss();
//                dictaionPausePopupOpen = true;
            }
        });

        //setting the view of the builder to our custom view that we already inflated
        optionDialog.setView(dialogView);
        //to check if its being shown
        if (!optionDialog.isShowing()) {
            //do something
//            dictaionPausePopupOpen = false;
            if (!isFinishing()) {
                optionDialog.show();
            }
        }
//        optionDialog.show();


//        //Now we need an AlertDialog.Builder object
//        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
//
//        //setting the view of the builder to our custom view that we already inflated
//        builder.setView(dialogView);
//
//        //finally creating the alert dialog and displaying it
//        final AlertDialog alertDialog = builder.create();
//        alertDialog.show();
    }

    //Requesting run-time permissions

    //Create placeholder for user's consent to record_audio permission.
    //This will be used in handling callback
    private final int MY_PERMISSIONS_RECORD_AUDIO = 1;

    private void requestAudioPermissions() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {

            //for dialog permission popup

            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
            boolean dnaCheckBox = preferences.getBoolean("dnaCheckBox", false);
            if (dnaCheckBox) {
                micPermissionNotGiven.setVisibility(View.VISIBLE);
                howToUse.setVisibility(View.GONE);
            } else {
                micPermissionDialog(VoiceEMRActivity.this);
            }


//            //When permission is not granted by user, show them message why this permission is needed.
//            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
//                    Manifest.permission.RECORD_AUDIO)) {
//                Toast.makeText(this, "Please grant permissions to record audio", Toast.LENGTH_LONG).show();
//
//                //Give user option to still opt-in the permissions
//                ActivityCompat.requestPermissions(this,
//                        new String[]{Manifest.permission.RECORD_AUDIO},
//                        MY_PERMISSIONS_RECORD_AUDIO);
//
//            } else {
//                // Show user dialog to grant permission to record audio
//                ActivityCompat.requestPermissions(this,
//                        new String[]{Manifest.permission.RECORD_AUDIO},
//                        MY_PERMISSIONS_RECORD_AUDIO);
//            }
        }
        //If permission is granted, then go ahead recording audio
        else if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.RECORD_AUDIO)
                == PackageManager.PERMISSION_GRANTED) {
//            viewModel.startAudioRecording("pediatrics");

            //Go ahead with recording audio now
//            recordAudio();
        }

    }

    //Handling callback
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case MY_PERMISSIONS_RECORD_AUDIO: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay!
//                    recordAudio();
                    micPermissionNotGiven.setVisibility(View.GONE);
                    howToUse.setVisibility(View.VISIBLE);
                    startDictationButton.setEnabled(true);
//                    viewModel.startAudioRecording("pediatrics");
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    micPermissionNotGiven.setVisibility(View.VISIBLE);
                    howToUse.setVisibility(View.GONE);
                    startDictationButton.setEnabled(false);
//                    Toast.makeText(this, "Permissions Denied to record audio", Toast.LENGTH_LONG).show();
                }
                return;
            }
        }
    }

//    @PermissionStatus
//    public static int getPermissionStatus(Activity activity, String androidPermissionName) {
//        if(ContextCompat.checkSelfPermission(activity, androidPermissionName) != PackageManager.PERMISSION_GRANTED) {
//            if(!ActivityCompat.shouldShowRequestPermissionRationale(activity, androidPermissionName)){
//                Log.d("userPermision","userPermision"+BLOCKED_OR_NEVER_ASKED);
//                return BLOCKED_OR_NEVER_ASKED;
//            }
//            Log.d("userPermision","userPermision"+DENIED);
//            return DENIED;
//        }
//        Log.d("userPermision","userPermision"+GRANTED);
//        return GRANTED;
//    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.voice_emr_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.toString().equalsIgnoreCase("Exit")) {
            if (voiceEMRModelsCategoryList.size() == 0 && voiceEMRModelsDiagnosisCategoryList.size() == 0 && voiceEMRModelsInvestigationCategoryList.size() == 0) {
                cancelVolleyRequest();
                finish();
            } else {
                backToHomeAlertMessage(VoiceEMRActivity.this);
            }
//            finish();

        } else if (item.toString().equalsIgnoreCase("Need Help?")) {
            VoiceEmrHelpBottomSheet voiceEmrHelpBottomSheet = new VoiceEmrHelpBottomSheet();
            voiceEmrHelpBottomSheet.setupConfig(VoiceEMRActivity.this);
            voiceEmrHelpBottomSheet.show(getSupportFragmentManager(), "Bottom Sheet Dialog Fragment");

        }

        switch (item.getItemId()) {
            case android.R.id.home:
                if (voiceEMRModelsCategoryList.size() == 0 && voiceEMRModelsDiagnosisCategoryList.size() == 0 && voiceEMRModelsInvestigationCategoryList.size() == 0 && voiceEMRModelsObservationCategoryList.size() == 0 && voiceEMRModelsTreatmentPlanCategoryList.size() == 0) {
                    cancelVolleyRequest();
                    finish();
                } else {
                    backToHomeAlertMessage(VoiceEMRActivity.this);
                }
//                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }

    }

    private void cancelVolleyRequest() {
        mRequestQueue.cancelAll(new RequestQueue.RequestFilter() {
            @Override
            public boolean apply(Request<?> request) {
                return true;
            }
        });
    }

    public void dummyCategoryAndSubCategoryRecord() {
        voiceRecordObservationRV.setVisibility(View.VISIBLE);
        VoiceEMRModel ve = new VoiceEMRModel();
        ve.setCategoryName("Observation");
        ve.setObservationCategoryName("Bloodd Presure");


        if (voiceEMRModelsObservationCategoryList.size() > 0) {
            ve.setCategoryNameObservationExistType(2);
        } else {
            ve.setCategoryNameObservationExistType(1);
        }

        int subCatogorySerNumber = voiceEMRModelsObservationCategoryList.size() + 1;
        ve.setSubCategorySerNumber(subCatogorySerNumber + "");
        voiceEMRModelsObservationCategoryList.add(ve);
        voiceEMRObservationListAdapter.notifyDataSetChanged();

    }


    public void parsingSimboData(Activity activity, JSONObject simboParsingDataObject) {
//        voiceEMRModelsCategoryList.clear();
//        voiceEMRModelsSubCategoryList.clear();

        ProgressDialog otpLoading = new ProgressDialog(VoiceEMRActivity.this);
        otpLoading.setMessage(getResources().getString(R.string.wait_while_we_fetching));
        otpLoading.setTitle(getResources().getString(R.string.fetching));
        otpLoading.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        otpLoading.setCancelable(false);
        otpLoading.show();
        voiceEMRViewModel.getParsingSimboData(activity, simboParsingDataObject).observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                Log.i("simbo to backend res", s);
//                progressBar.setVisibility(View.GONE);
                otpLoading.dismiss();
                try {
                    JSONObject response = new JSONObject(s);
                    if (response.getInt("status_code") == 200) {
                        JSONObject resObject = response.getJSONObject("response").getJSONObject("response");


                        String sectionName = "";
                        Gson gson = new GsonBuilder().setPrettyPrinting().create();
                        SimboDataResponse simboDataResponse = gson.fromJson(simboParsingDataObject.toString(), SimboDataResponse.class);
                        if (resObject.getJSONArray("symptomsRecords").length() > 0) {
                            sectionName = "Symptoms";
                            JSONArray symptomsRecordsArray = resObject.getJSONArray("symptomsRecords");
                            for (int i = 0; i < symptomsRecordsArray.length(); i++) {
                                VoiceEMRModel ve = new VoiceEMRModel();
                                JSONObject symptomsRecordsObject = symptomsRecordsArray.getJSONObject(i);
                                ve.setCategoryName("Symptoms");
                                ve.setSymptomName(symptomsRecordsObject.getString("symptom_name"));
                                String symptomDesc;
                                if (symptomsRecordsObject.getString("symptom_description").equalsIgnoreCase("null")) {
                                    symptomDesc = "";
                                } else {
                                    symptomDesc = symptomsRecordsObject.getString("symptom_description");
                                }
                                ve.setSymptomDescription(symptomDesc);
                                String firstReportedOn = symptomsRecordsObject.optString("first_reported_on");
                                if (firstReportedOn.contains(" ")) {
                                    String[] splitResult = firstReportedOn.split(" ");
                                    firstReportedOn = splitResult[0];
                                }
                                ve.setSymptomFirstReportedOn(firstReportedOn);
                                ve.setSymptomStatus(symptomsRecordsObject.getString("symptom_status"));
                                ve.setSymptomData_id(symptomsRecordsObject.getString("data_id"));
                                boolean isSectionExist = false;
                                if (isSectionExist) {
                                    ve.setCategoryNameSymptomExistType(2);
                                } else {
                                    isSectionExist = true;
                                    ve.setCategoryNameSymptomExistType(1);
                                }
                                // int subCatogorySerNumber = records_adapter_List.size() + 1;
                                //ve.setSubCategorySerNumber(symptomSerialNum++ + "");
                                //voiceEMRModelsCategoryList.add(ve);


                                if (simboDataResponse.getPrs().size() > 0) {
                                    List<String> invArray = simboDataResponse.getPrs().get(0).getInvDataId();
                                    if (invArray != null) {
                                        for (int x = 0; x < invArray.size(); x++) {
                                            String inv_data_id = invArray.get(x);
                                            if (dictitationRecords.containsKey(inv_data_id)) {
                                                dictitationRecords.remove(inv_data_id);
                                            }

                                        }
                                    }
                                }
                                dictitationRecords.put(ve.getSymptomData_id(), new DictationModel(sectionName, ve));
                            }
                        } else if (resObject.getJSONArray("diagnosisRecords").length() > 0) {
                            sectionName = "Diagnosis";
                            JSONArray diagnosisRecordsArray = resObject.getJSONArray("diagnosisRecords");
                            for (int i = 0; i < diagnosisRecordsArray.length(); i++) {
                                JSONObject diagnosisRecordsObject = diagnosisRecordsArray.getJSONObject(i);
                                VoiceEMRModel ve = new VoiceEMRModel();
                                ve.setCategoryName("Diagnosis");
                                ve.setDiagnosisDiagnosis(diagnosisRecordsObject.getString("diagnosis"));
                                ve.setDiagnosisStatus(diagnosisRecordsObject.getString("status"));
                                ve.setDiagnosisPosited_on(diagnosisRecordsObject.getString("posited_on"));
                                ve.setDiagnosisData_id(diagnosisRecordsObject.getString("data_id"));
                                String diagnosisConfirmed_ruledout_on;
                                if (diagnosisRecordsObject.getString("confirmed_ruledout_on").equalsIgnoreCase("null")) {
                                    diagnosisConfirmed_ruledout_on = "";
                                } else {
                                    diagnosisConfirmed_ruledout_on = diagnosisRecordsObject.getString("confirmed_ruledout_on");
                                }
                                ve.setDiagnosisConfirmed_ruledout_on(diagnosisConfirmed_ruledout_on);
                                boolean isSectionExist = false;
                                section_existList = new HashMap<String, Integer>();
                                if (isSectionExist) {
                                    ve.setCategoryNameDiagnosisExistType(2);
                                } else {
                                    isSectionExist = true;
                                    ve.setCategoryNameDiagnosisExistType(1);
                                }

                                // int subCatogorySerNumber = records_adapter_List.size() + 1;
                                // ve.setSubCategorySerNumber(diagnosisSerialNum ++ + "");
                                // voiceEMRModelsDiagnosisCategoryList.add(ve);
                                if (simboDataResponse.getPrs().size() > 0) {
                                    List<String> invArray = simboDataResponse.getPrs().get(0).getInvDataId();
                                    if (invArray != null) {
                                        for (int x = 0; x < invArray.size(); x++) {
                                            String inv_data_id = invArray.get(x);
                                            if (dictitationRecords.containsKey(inv_data_id)) {
                                                dictitationRecords.remove(inv_data_id);
                                            }

                                        }
                                    }
                                }
                                dictitationRecords.put(ve.getDiagnosisData_id(), new DictationModel(sectionName, ve));
                            }
                            //dictitationRecords.add(new DictiatationModel(sectionName,resObject.getJSONArray("diagnosisRecords").toString()));
                        } else if (resObject.getJSONArray("investigationResultsRecords").length() > 0) {
                            sectionName = "Investigation Results";
                            JSONArray investigationResultsRecordsArray = resObject.getJSONArray("investigationResultsRecords");
                            for (int i = 0; i < investigationResultsRecordsArray.length(); i++) {
                                JSONObject investigationRecordsObject = investigationResultsRecordsArray.getJSONObject(i);


                                VoiceEMRModel ve = new VoiceEMRModel();
                                ve.setCategoryName("Investigation Results");
                                ve.setInvestigationInvestigation_name(investigationRecordsObject.getString("investigation_name"));
                                ve.setInvestigationValue(investigationRecordsObject.getString("value"));
                                ve.setInvestigationNotes(investigationRecordsObject.getString("notes"));
                                ve.setInvestigationParameter(investigationRecordsObject.getString("parameter"));
                                ve.setInvestigationFile_url(investigationRecordsObject.getString("file_url"));
                                ve.setInvestigatioFfile_type(investigationRecordsObject.getString("file_type"));
                                ve.setInvestigationData_id(investigationRecordsObject.getString("data_id"));

                                boolean isSectionExist = false;
                                if (isSectionExist) {
                                    ve.setCategoryNameInvestigationResultsExistType(2);
                                } else {
                                    isSectionExist = true;
                                    ve.setCategoryNameInvestigationResultsExistType(2);
                                }

                                //int subCatogorySerNumber = records_adapter_List.size() + 1;
                                // ve.setSubCategorySerNumber(investigationSerialNum++ + "");
                                //voiceEMRModelsInvestigationCategoryList.add(ve);
                                if (simboDataResponse.getPrs().size() > 0) {
                                    List<String> invArray = simboDataResponse.getPrs().get(0).getInvDataId();
                                    if (invArray != null) {
                                        for (int x = 0; x < invArray.size(); x++) {
                                            String inv_data_id = invArray.get(x);
                                            if (dictitationRecords.containsKey(inv_data_id)) {
                                                dictitationRecords.remove(inv_data_id);
                                            }

                                        }
                                    }
                                }
                                dictitationRecords.put(ve.getInvestigationData_id(), new DictationModel(sectionName, ve));
                            }
                            //dictitationRecords.add(new DictiatationModel(sectionName,resObject.getJSONArray("investigationResultsRecords").toString()));
                        } else if (resObject.getJSONArray("observationRecords").length() > 0) {
                            sectionName = "Observation";
                            JSONArray ObservationRecordsArray = resObject.getJSONArray("observationRecords");
                            if (resObject.has("field-dictionary")) {
                                //get Value of video
                                fieldDictionary = resObject.getJSONObject("field-dictionary");
                            }
                            for (int i = 0; i < ObservationRecordsArray.length(); i++) {
                                JSONObject ObservationRecordsObject = ObservationRecordsArray.getJSONObject(i);


                                VoiceEMRModel ve = new VoiceEMRModel();
                                ve.setCategoryName("Observation");
                                ve.setObservationCategoryName(ObservationRecordsObject.getJSONObject("records").getString("category_name"));
                                ve.setObservationCategoryId(ObservationRecordsObject.getJSONObject("records").getInt("category_id"));
                                ve.setObservationData_id(ObservationRecordsObject.getJSONObject("records").getString("data_id"));

                                recordObject = new JSONObject();
                                Object intervention = fieldDictionary.get(String.valueOf(ObservationRecordsObject.getJSONObject("records").getInt("category_id")));
                                if (intervention instanceof JSONArray) {
                                    // It's an array
                                    interventionJsonArray = (JSONArray) intervention;
                                    for (int j = 0; j < interventionJsonArray.length(); j++) {
                                        JSONObject categoryObject = interventionJsonArray.getJSONObject(j);
                                        String categoryKey = String.valueOf(categoryObject.getInt("key"));
                                        String categoryValue = ObservationRecordsObject.getJSONObject("records").getString(categoryKey);
                                        if (ObservationRecordsObject.getJSONObject("records").optString(categoryKey).equalsIgnoreCase("null")) {
                                            categoryValue = "";
                                        }
                                        recordObject.put(categoryKey, categoryValue);

                                    }
                                    Log.d("formatedRecordObject", "formatedRecordObject" + recordObject);


                                } else if (intervention instanceof JSONObject) {
                                    // It's an object
                                    interventionObject = (JSONObject) intervention;
                                } else {
                                    // It's something else, like a string or number
                                }

                                ve.setObservationCategoryRecords(recordObject.toString());
                                ve.setObservationFieldDic(fieldDictionary.toString());

                                /*if (consolidatedList.size() > 0) {
                                    ve.setCategoryNameObservationExistType(2);
                                } else {
                                    ve.setCategoryNameObservationExistType(1);
                                }*/

                                boolean isSectionExist = false;
                                if (isSectionExist) {
                                    ve.setCategoryNameObservationExistType(2);

                                } else {
                                    isSectionExist = true;
                                    ve.setCategoryNameObservationExistType(2);

                                }

                                //int subCatogorySerNumber = records_adapter_List.size() + 1;
                                //  ve.setSubCategorySerNumber(observationSerialNum++ + "");
                                //voiceEMRModelsObservationCategoryList.add(ve);
                                if (simboDataResponse.getPrs().size() > 0) {
                                    List<String> invArray = simboDataResponse.getPrs().get(0).getInvDataId();
                                    if (invArray != null) {
                                        for (int x = 0; x < invArray.size(); x++) {
                                            String inv_data_id = invArray.get(x);
                                            if (dictitationRecords.containsKey(inv_data_id)) {
                                                dictitationRecords.remove(inv_data_id);
                                            }

                                        }
                                    }
                                }
                                dictitationRecords.put(ve.getObservationData_id(), new DictationModel(sectionName, ve));
                            }
                            //dictitationRecords.add(new DictiatationModel(sectionName,resObject.getJSONArray("observationRecords").toString()));
                        } else if (resObject.getJSONArray("treatmentPlanRecords").length() > 0) {
                            sectionName = "Treatment Plan";
                            JSONArray TreatmentPlanRecordsArray = resObject.getJSONArray("treatmentPlanRecords");
                            if (resObject.has("field-dictionary")) {
                                //get Value of video
                                fieldDictionary = resObject.getJSONObject("field-dictionary");
                            }
                            for (int i = 0; i < TreatmentPlanRecordsArray.length(); i++) {
                                JSONObject treatmentPlanRecordsObject = TreatmentPlanRecordsArray.getJSONObject(i);
                                VoiceEMRModel ve = new VoiceEMRModel();
                                ve.setCategoryName("Treatment Plan");
                                ve.setTreatmentCategoryName(treatmentPlanRecordsObject.getJSONObject("records").getString("category_name"));
                                ve.setTreatmentCategoryId(treatmentPlanRecordsObject.getJSONObject("records").getInt("category_id"));
                                ve.setTreatmentPlanData_id(treatmentPlanRecordsObject.getJSONObject("records").getString("data_id"));

                                recordObject = new JSONObject();
                                Object intervention = fieldDictionary.get(String.valueOf(treatmentPlanRecordsObject.getJSONObject("records").getInt("category_id")));
                                if (intervention instanceof JSONArray) {
                                    // It's an array
                                    interventionTreatmentJsonArray = (JSONArray) intervention;
                                    for (int j = 0; j < interventionTreatmentJsonArray.length(); j++) {
                                        JSONObject categoryObject = interventionTreatmentJsonArray.getJSONObject(j);
                                        String categoryKey = String.valueOf(categoryObject.getInt("key"));
                                        if (treatmentPlanRecordsObject.getJSONObject("records").has(categoryKey)) {
                                            String categoryValue = treatmentPlanRecordsObject.getJSONObject("records").optString(categoryKey);
                                            if (treatmentPlanRecordsObject.getJSONObject("records").optString(categoryKey).equalsIgnoreCase("null")) {
                                                categoryValue = "";
                                            }
                                            recordObject.put(categoryKey, categoryValue);
                                        }

                                    }
                                    Log.d("formatedRecordObject", "formatedRecordObject" + recordObject);


                                } else if (intervention instanceof JSONObject) {
                                    // It's an object
                                    interventionTreatmentJsonObject = (JSONObject) intervention;
                                } else {
                                    // It's something else, like a string or number
                                }

                                ve.setTreatmentCategoryRecords(recordObject.toString());
                                ve.setTreatmentFieldDic(fieldDictionary.toString());
                                boolean isSectionExist = false;
                                if (isSectionExist) {
                                    ve.setCategoryNameTreatmentPlanExistType(2);
                                } else {
                                    isSectionExist = true;
                                    ve.setCategoryNameTreatmentPlanExistType(1);
                                }

                                // int subCatogorySerNumber = records_adapter_List.size() + 1;
                                // ve.setSubCategorySerNumber(treatmentPlanSerialNum++ + "");
                                //voiceEMRModelsTreatmentPlanCategoryList.add(ve);
                                if (simboDataResponse.getPrs().size() > 0) {
                                    List<String> invArray = simboDataResponse.getPrs().get(0).getInvDataId();
                                    if (invArray != null) {
                                        for (int x = 0; x < invArray.size(); x++) {
                                            String inv_data_id = invArray.get(x);
                                            if (dictitationRecords.containsKey(inv_data_id)) {
                                                dictitationRecords.remove(inv_data_id);
                                            }

                                        }
                                    }
                                }
                                dictitationRecords.put(ve.getTreatmentPlanData_id(), new DictationModel(sectionName, ve));
                            }
                            //dictitationRecords.add(new DictiatationModel(sectionName,resObject.getJSONArray("treatmentPlanRecords").toString()));
                        }

                        HashMap<String, List<DictationModel>> groupedHashMap = groupDataIntoHashMap(dictitationRecords);

                        records_adapter_List.clear();
                        itemSerailNum.clear();
                        dicationAdapter.notifyDataSetChanged();
                        ArrayList<String> hashmapKeys = new ArrayList<>(groupedHashMap.keySet());
                        Collections.reverse(hashmapKeys);
                        for (String section_name : hashmapKeys) {
                            int serailNum = 0;
                            //if(!header_section_names_list.contains(section_name)) {
                            SectionNameModel sectionNameModel = new SectionNameModel();
                            sectionNameModel.setSectionName(section_name);
                            //sectionNameModel.setSectionType(section_name);
                            records_adapter_List.add(sectionNameModel);
                            itemSerailNum.add(serailNum + "");
                            //header_section_names_list.add(sectionName);
                            //}


                            for (DictationModel dictationModel : groupedHashMap.get(section_name)) {
                                RecordsItems recordsItems = new RecordsItems();
                                recordsItems.setVoiceEMRModel(dictationModel);
                                recordsItems.setRecordType(dictationModel.getSection_name());
                                itemSerailNum.add(++serailNum + "");
                                //setBookingDataTabs(bookingDataTabs);
                                records_adapter_List.add(recordsItems);
                            }
                        }


                        dicationAdapter.notifyDataSetChanged();
















                        /*if (symptomsRecordsArray.length() > 0) {
                            voiceEMRRV.setVisibility(View.VISIBLE);
                            howToUse.setVisibility(View.GONE);
//                            notRecordText.setVisibility(View.GONE);
                            Gson gson = new GsonBuilder().setPrettyPrinting().create();
                            SimboDataResponse simboDataResponse = gson.fromJson(simboParsingDataObject.toString(), SimboDataResponse.class);
                            if(simboDataResponse.getPrs().size()>0) {
                                List<String> invArray = simboDataResponse.getPrs().get(0).getInvDataId();
                                if (invArray != null) {
                                    for (int x = 0; x < invArray.size(); x++) {
                                        String inv_data_id = invArray.get(x).toString();
                                        if (voiceEMRModelsCategoryList.size() > 0) {
                                            for (int ii = 0; ii < voiceEMRModelsCategoryList.size(); ii++) {
                                                if (voiceEMRModelsCategoryList.get(ii).getSymptomData_id().equalsIgnoreCase(inv_data_id)) {
                                                    voiceEMRModelsCategoryList.remove(ii);
                                                }
                                                if (voiceEMRModelsCategoryList.size() > 0) {
                                                    voiceEMRModelsCategoryList.get(0).setCategoryNameSymptomExistType(1);
                                                }

                                            }
                                        }
                                    }
                                }
                            }
                            for (int i = 0; i < symptomsRecordsArray.length(); i++) {
                                JSONObject symptomsRecordsObject = symptomsRecordsArray.getJSONObject(i);
                                VoiceEMRModel ve = new VoiceEMRModel();
                                ve.setCategoryName("Symptoms");
                                ve.setSymptomName(symptomsRecordsObject.getString("symptom_name"));
                                String symptomDesc;
                                if (symptomsRecordsObject.getString("symptom_description").equalsIgnoreCase("null")) {
                                    symptomDesc = "";
                                } else {
                                    symptomDesc = symptomsRecordsObject.getString("symptom_description");
                                }
                                ve.setSymptomDescription(symptomDesc);
                                ve.setSymptomFirstReportedOn(symptomsRecordsObject.getString("first_reported_on"));
                                ve.setSymptomStatus(symptomsRecordsObject.getString("symptom_status"));
                                ve.setSymptomData_id(symptomsRecordsObject.getString("data_id"));
                                if (voiceEMRModelsCategoryList.size() > 0) {
                                    ve.setCategoryNameSymptomExistType(2);
                                } else {
                                    ve.setCategoryNameSymptomExistType(1);
                                }
                                int subCatogorySerNumber = voiceEMRModelsCategoryList.size() + 1;
                                ve.setSubCategorySerNumber(subCatogorySerNumber + "");
                                voiceEMRModelsCategoryList.add(ve);
                            }
                            voiceEMRListAdapter.notifyDataSetChanged();
                        }

                        // Diagnosis Array Data
                        JSONArray diagnosisRecordsArray = resObject.getJSONArray("diagnosisRecords");
                        if (diagnosisRecordsArray.length() > 0) {
                            voiceRecordDiagnosisRV.setVisibility(View.VISIBLE);
                            howToUse.setVisibility(View.GONE);
//                            notRecordText.setVisibility(View.GONE);
                            Gson gson = new GsonBuilder().setPrettyPrinting().create();
                            SimboDataResponse simboDataResponse = gson.fromJson(simboParsingDataObject.toString(), SimboDataResponse.class);
                            if(simboDataResponse.getPrs().size()>0) {
                                List<String> invArray = simboDataResponse.getPrs().get(0).getInvDataId();
                                if (invArray != null) {
                                    for (int x = 0; x < invArray.size(); x++) {
                                        String inv_data_id = invArray.get(x).toString();
                                        if (voiceEMRModelsDiagnosisCategoryList.size() > 0) {
                                            for (int ii = 0; ii < voiceEMRModelsDiagnosisCategoryList.size(); ii++) {
                                                if (voiceEMRModelsDiagnosisCategoryList.get(ii).getDiagnosisData_id().equalsIgnoreCase(inv_data_id)) {
                                                    voiceEMRModelsDiagnosisCategoryList.remove(ii);
                                                    if (voiceEMRModelsDiagnosisCategoryList.size() > 0) {
                                                        voiceEMRModelsDiagnosisCategoryList.get(0).setCategoryNameDiagnosisExistType(1);
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                            for (int i = 0; i < diagnosisRecordsArray.length(); i++) {
                                JSONObject diagnosisRecordsObject = diagnosisRecordsArray.getJSONObject(i);
                                VoiceEMRModel ve = new VoiceEMRModel();
                                ve.setCategoryName("Diagnosis");
                                ve.setDiagnosisDiagnosis(diagnosisRecordsObject.getString("diagnosis"));
                                ve.setDiagnosisStatus(diagnosisRecordsObject.getString("status"));
                                ve.setDiagnosisPosited_on(diagnosisRecordsObject.getString("posited_on"));
                                ve.setDiagnosisData_id(diagnosisRecordsObject.getString("data_id"));
                                String diagnosisConfirmed_ruledout_on;
                                if (diagnosisRecordsObject.getString("confirmed_ruledout_on").equalsIgnoreCase("null")) {
                                    diagnosisConfirmed_ruledout_on = "";
                                } else {
                                    diagnosisConfirmed_ruledout_on = diagnosisRecordsObject.getString("confirmed_ruledout_on");
                                }
                                ve.setDiagnosisConfirmed_ruledout_on(diagnosisConfirmed_ruledout_on);
                                if (voiceEMRModelsDiagnosisCategoryList.size() > 0) {
                                    ve.setCategoryNameDiagnosisExistType(2);
                                } else {
                                    ve.setCategoryNameDiagnosisExistType(1);
                                }

                                int subCatogorySerNumber = voiceEMRModelsDiagnosisCategoryList.size() + 1;
                                ve.setSubCategorySerNumber(subCatogorySerNumber + "");
                                voiceEMRModelsDiagnosisCategoryList.add(ve);
                            }
                            voiceEMRDiagnosisListAdapter.notifyDataSetChanged();
                        }

                        //investigationResult Array Data
                        JSONArray investigationResultsRecordsArray = resObject.getJSONArray("investigationResultsRecords");
                        if (investigationResultsRecordsArray.length() > 0) {
                            voiceRecordInvestigationRV.setVisibility(View.VISIBLE);
                            howToUse.setVisibility(View.GONE);
//                            notRecordText.setVisibility(View.GONE);
                            Gson gson = new GsonBuilder().setPrettyPrinting().create();
                            SimboDataResponse simboDataResponse = gson.fromJson(simboParsingDataObject.toString(), SimboDataResponse.class);
                            if(simboDataResponse.getPrs().size()>0) {
                                List<String> invArray = simboDataResponse.getPrs().get(0).getInvDataId();
                                if (invArray != null) {
                                    for (int x = 0; x < invArray.size(); x++) {
                                        String inv_data_id = invArray.get(x).toString();
                                        if (voiceEMRModelsInvestigationCategoryList.size() > 0) {
                                            for (int ii = 0; ii < voiceEMRModelsInvestigationCategoryList.size(); ii++) {
                                                if (voiceEMRModelsInvestigationCategoryList.get(ii).getInvestigationData_id().equalsIgnoreCase(inv_data_id)) {
                                                    voiceEMRModelsInvestigationCategoryList.remove(ii);
                                                    if (voiceEMRModelsInvestigationCategoryList.size() > 0) {
                                                        voiceEMRModelsInvestigationCategoryList.get(0).setCategoryNameInvestigationResultsExistType(1);
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                            for (int i = 0; i < investigationResultsRecordsArray.length(); i++) {
                                JSONObject investigationRecordsObject = investigationResultsRecordsArray.getJSONObject(i);


                                VoiceEMRModel ve = new VoiceEMRModel();
                                ve.setCategoryName("Investigation Results");
                                ve.setInvestigationInvestigation_name(investigationRecordsObject.getString("investigation_name"));
                                ve.setInvestigationValue(investigationRecordsObject.getString("value"));
                                ve.setInvestigationNotes(investigationRecordsObject.getString("notes"));
                                ve.setInvestigationParameter(investigationRecordsObject.getString("parameter"));
                                ve.setInvestigationFile_url(investigationRecordsObject.getString("file_url"));
                                ve.setInvestigatioFfile_type(investigationRecordsObject.getString("file_type"));
                                ve.setInvestigationData_id(investigationRecordsObject.getString("data_id"));

                                if (voiceEMRModelsInvestigationCategoryList.size() > 0) {
                                    ve.setCategoryNameInvestigationResultsExistType(2);
                                } else {
                                    ve.setCategoryNameInvestigationResultsExistType(1);
                                }

                                int subCatogorySerNumber = voiceEMRModelsInvestigationCategoryList.size() + 1;
                                ve.setSubCategorySerNumber(subCatogorySerNumber + "");
                                voiceEMRModelsInvestigationCategoryList.add(ve);
                            }
                            voiceEMRInvestigationResultListAdapter.notifyDataSetChanged();
                        }

                        //Observation Array Data
                        JSONArray ObservationRecordsArray = resObject.getJSONArray("observationRecords");
                        //has method
                        if (resObject.has("field-dictionary")) {
                            //get Value of video
                            fieldDictionary = resObject.getJSONObject("field-dictionary");
                        }

                        if (ObservationRecordsArray.length() > 0) {
                            voiceRecordObservationRV.setVisibility(View.VISIBLE);
                            howToUse.setVisibility(View.GONE);
//                            notRecordText.setVisibility(View.GONE);
                            Gson gson = new GsonBuilder().setPrettyPrinting().create();
                            SimboDataResponse simboDataResponse = gson.fromJson(simboParsingDataObject.toString(), SimboDataResponse.class);
                            if(simboDataResponse.getPrs().size()>0) {
                                List<String> invArray = simboDataResponse.getPrs().get(0).getInvDataId();
                                if (invArray != null) {
                                    for (int x = 0; x < invArray.size(); x++) {
                                        String inv_data_id = invArray.get(x).toString();
                                        if (voiceEMRModelsObservationCategoryList.size() > 0) {
                                            for (int ii = 0; ii < voiceEMRModelsObservationCategoryList.size(); ii++) {
                                                if (voiceEMRModelsObservationCategoryList.get(ii).getObservationData_id().equalsIgnoreCase(inv_data_id)) {
                                                    voiceEMRModelsObservationCategoryList.remove(ii);
                                                    if (voiceEMRModelsObservationCategoryList.size() > 0) {
                                                        voiceEMRModelsObservationCategoryList.get(0).setCategoryNameObservationExistType(1);
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                            for (int i = 0; i < ObservationRecordsArray.length(); i++) {
                                JSONObject ObservationRecordsObject = ObservationRecordsArray.getJSONObject(i);



                                VoiceEMRModel ve = new VoiceEMRModel();
                                ve.setCategoryName("Observation");
                                ve.setObservationCategoryName(ObservationRecordsObject.getJSONObject("records").getString("category_name"));
                                ve.setObservationCategoryId(ObservationRecordsObject.getJSONObject("records").getInt("category_id"));
                                ve.setObservationData_id(ObservationRecordsObject.getJSONObject("records").getString("data_id"));

                                recordObject = new JSONObject();
                                Object intervention = fieldDictionary.get(String.valueOf(ObservationRecordsObject.getJSONObject("records").getInt("category_id")));
                                if (intervention instanceof JSONArray) {
                                    // It's an array
                                    interventionJsonArray = (JSONArray) intervention;
                                    for (int j = 0; j < interventionJsonArray.length(); j++) {
                                        JSONObject categoryObject = interventionJsonArray.getJSONObject(j);
                                        String categoryKey = String.valueOf(categoryObject.getInt("key"));
                                        String categoryValue = ObservationRecordsObject.getJSONObject("records").getString(categoryKey);
                                        if(ObservationRecordsObject.getJSONObject("records").optString(categoryKey).equalsIgnoreCase("null")){
                                            categoryValue="";
                                        }
                                        recordObject.put(categoryKey, categoryValue);

                                    }
                                    Log.d("formatedRecordObject", "formatedRecordObject" + recordObject);


                                } else if (intervention instanceof JSONObject) {
                                    // It's an object
                                    interventionObject = (JSONObject) intervention;
                                } else {
                                    // It's something else, like a string or number
                                }

                                ve.setObservationCategoryRecords(recordObject);
                                ve.setObservationFieldDic(fieldDictionary);

                                if (voiceEMRModelsObservationCategoryList.size() > 0) {
                                    ve.setCategoryNameObservationExistType(2);
                                } else {
                                    ve.setCategoryNameObservationExistType(1);
                                }

                                int subCatogorySerNumber = voiceEMRModelsObservationCategoryList.size() + 1;
                                ve.setSubCategorySerNumber(subCatogorySerNumber + "");
                                voiceEMRModelsObservationCategoryList.add(ve);
                            }
                            voiceEMRObservationListAdapter.notifyDataSetChanged();
                        }


                        //TreatmentPlan Array Data
                        JSONArray TreatmentPlanRecordsArray = resObject.getJSONArray("treatmentPlanRecords");
                        if (resObject.has("field-dictionary")) {
                            //get Value of video
                            fieldDictionary = resObject.getJSONObject("field-dictionary");
                        }
                        if (TreatmentPlanRecordsArray.length() > 0) {
                            Gson gson = new GsonBuilder().setPrettyPrinting().create();
                            SimboDataResponse simboDataResponse = gson.fromJson(simboParsingDataObject.toString(), SimboDataResponse.class);
                            if(simboDataResponse.getPrs().size()>0) {
                                List<String> invArray = simboDataResponse.getPrs().get(0).getInvDataId();
                                if (invArray != null) {
                                    for (int x = 0; x < invArray.size(); x++) {
                                        String inv_data_id = invArray.get(x).toString();
                                        if (voiceEMRModelsTreatmentPlanCategoryList.size() > 0) {
                                            for (int ii = 0; ii < voiceEMRModelsTreatmentPlanCategoryList.size(); ii++) {
                                                if (voiceEMRModelsTreatmentPlanCategoryList.get(ii).getTreatmentPlanData_id().equalsIgnoreCase(inv_data_id)) {
                                                    voiceEMRModelsTreatmentPlanCategoryList.remove(ii);
                                                    if (voiceEMRModelsTreatmentPlanCategoryList.size() > 0) {
                                                        voiceEMRModelsTreatmentPlanCategoryList.get(0).setCategoryNameTreatmentPlanExistType(1);
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                            voiceRecordTreatmentPlanRv.setVisibility(View.VISIBLE);
                            howToUse.setVisibility(View.GONE);
//                            notRecordText.setVisibility(View.GONE);
                            for (int i = 0; i < TreatmentPlanRecordsArray.length(); i++) {
                                JSONObject treatmentPlanRecordsObject = TreatmentPlanRecordsArray.getJSONObject(i);
                                VoiceEMRModel ve = new VoiceEMRModel();
                                ve.setCategoryName("Treatment Plan");
                                ve.setTreatmentCategoryName(treatmentPlanRecordsObject.getJSONObject("records").getString("category_name"));
                                ve.setTreatmentCategoryId(treatmentPlanRecordsObject.getJSONObject("records").getInt("category_id"));
                                ve.setTreatmentPlanData_id(treatmentPlanRecordsObject.getJSONObject("records").getString("data_id"));

                                recordObject = new JSONObject();
                                Object intervention = fieldDictionary.get(String.valueOf(treatmentPlanRecordsObject.getJSONObject("records").getInt("category_id")));
                                if (intervention instanceof JSONArray) {
                                    // It's an array
                                    interventionTreatmentJsonArray = (JSONArray) intervention;
                                    for (int j = 0; j < interventionTreatmentJsonArray.length(); j++) {
                                        JSONObject categoryObject = interventionTreatmentJsonArray.getJSONObject(j);
                                        String categoryKey = String.valueOf(categoryObject.getInt("key"));
                                        if (treatmentPlanRecordsObject.getJSONObject("records").has(categoryKey)) {
                                            String categoryValue = treatmentPlanRecordsObject.getJSONObject("records").optString(categoryKey);
                                            if(treatmentPlanRecordsObject.getJSONObject("records").optString(categoryKey).equalsIgnoreCase("null")){
                                                categoryValue="";
                                            }
                                            recordObject.put(categoryKey, categoryValue);
                                        }

                                    }
                                    Log.d("formatedRecordObject", "formatedRecordObject" + recordObject);


                                } else if (intervention instanceof JSONObject) {
                                    // It's an object
                                    interventionTreatmentJsonObject = (JSONObject) intervention;
                                } else {
                                    // It's something else, like a string or number
                                }

                                ve.setTreatmentCategoryRecords(recordObject);
                                ve.setTreatmentFieldDic(fieldDictionary);

                                if (voiceEMRModelsTreatmentPlanCategoryList.size() > 0) {
                                    ve.setCategoryNameTreatmentPlanExistType(2);
                                } else {
                                    ve.setCategoryNameTreatmentPlanExistType(1);
                                }

                                int subCatogorySerNumber = voiceEMRModelsTreatmentPlanCategoryList.size() + 1;
                                ve.setSubCategorySerNumber(subCatogorySerNumber + "");
                                voiceEMRModelsTreatmentPlanCategoryList.add(ve);
                            }
                            voiceEMRTreatmentPlanListAdapter.notifyDataSetChanged();

                        }


                        //error message
                        JSONArray errorArray = resObject.getJSONArray("errors");
                        if (errorArray.length() > 0) {
                            String errorObjectString = errorArray.getJSONObject(0).getString("error");
                            Toast.makeText(getApplicationContext(), errorObjectString + "", Toast.LENGTH_LONG).show();
                        }*/

                        JSONArray errorArray = null;
                        try {
                            errorArray = resObject.getJSONArray("errors");
                            if (errorArray.length() > 0) {
                                String errorObjectString = errorArray.getJSONObject(0).getString("error");
                                Toast.makeText(getApplicationContext(), errorObjectString + "", Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    } else {
                        ErrorHandlerClass.INSTANCE.errorHandler(VoiceEMRActivity.this, s);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    otpLoading.dismiss();
                    Log.d("parserError:", "parserError:" + e);
                }

            }
        });

    }


    @Override
    protected void onResume() {
        super.onResume();


        if (symptomAddRecordClick) {
            symptomAddRecordClick = false;
            voiceEMRListAdapter.notifyDataSetChanged();
        }
        if (diagnosisAddRecordClick) {
            diagnosisAddRecordClick = false;
            voiceEMRDiagnosisListAdapter.notifyDataSetChanged();
        }
        if (investigationAddRecordClick) {
            investigationAddRecordClick = false;
            voiceEMRInvestigationResultListAdapter.notifyDataSetChanged();
        }
        if (observationAddRecordClick) {
            observationAddRecordClick = false;
            voiceEMRObservationListAdapter.notifyDataSetChanged();
        }
        if (treatmentPlanAddRecordClick) {
            treatmentPlanAddRecordClick = false;
            voiceEMRTreatmentPlanListAdapter.notifyDataSetChanged();
        }


    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    protected void onDestroy() {
        Log.v(LOG_TAG, "onDestory");
        super.onDestroy();
        unregisterReceiver(receiver);

    }


    public void saveSymptomRecords() {
        ProgressDialog otpLoading = new ProgressDialog(VoiceEMRActivity.this);
        otpLoading.setMessage(getResources().getString(R.string.wait_while_we_updating));
        otpLoading.setTitle(getResources().getString(R.string.updating));
        otpLoading.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        otpLoading.setCancelable(false);
        otpLoading.show();

        try {

            /*JSONArray symptomArrayObject = new JSONArray();

            for (int i = 0; i < voiceEMRModelsCategoryList.size(); i++) {
                JSONObject symptomObject = new JSONObject();

                symptomObject.put("patient_id", patientId);
                symptomObject.put("episode_id", episodeId);
                symptomObject.put("encounter_id", encounterId);
                symptomObject.put("first_reported_on", voiceEMRModelsCategoryList.get(i).getSymptomFirstReportedOn());
                symptomObject.put("symptom_name", voiceEMRModelsCategoryList.get(i).getSymptomName());
                symptomObject.put("symptom_description", voiceEMRModelsCategoryList.get(i).getSymptomDescription());
                symptomObject.put("symptom_status", voiceEMRModelsCategoryList.get(i).getSymptomStatus());

                symptomArrayObject.put(symptomObject);
            }*/


            jsonValue = new JSONObject();
            jsonValue.put("data", symptomArrayObject);
            Log.d("symptomJsonData", "" + jsonValue);

        } catch (Exception e) {

        }
        voiceEMRViewModel.saveSymptom(VoiceEMRActivity.this, jsonValue).observe(VoiceEMRActivity.this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                otpLoading.dismiss();
                symptomArrayObject = new JSONArray();

                try {
                    JSONObject jsonObject = new JSONObject(s);
                    if (jsonObject.getInt("status_code") == 200) {
                        if (diagnosisArrayObject.length() > 0 || investigationArrayObject.length() > 0 || observationArrayObject.length() > 0 || treatmentPlanArrayObject.length() > 0) {

                        } else {
                            if (!saveRecordDialog.isShowing()) {
                                saveRecordDialog(VoiceEMRActivity.this);
                            }
                        }
                        /*if (diagnosisArrayObject.length() > 0) {
                            saveDiagnosisRecords();
                        } else if (investigationArrayObject.length() > 0) {
                            saveInvestigationRecords();
                        } else {
                            if (!saveRecordDialog.isShowing()) {
                                saveRecordDialog(VoiceEMRActivity.this);
                            }
                        }*/


                    } else {
                        ErrorHandlerClass.INSTANCE.errorHandler(VoiceEMRActivity.this, s);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });
    }

    public void saveDiagnosisRecords() {
        ProgressDialog otpLoading = new ProgressDialog(VoiceEMRActivity.this);
        otpLoading.setMessage(getResources().getString(R.string.wait_while_we_updating));
        otpLoading.setTitle(getResources().getString(R.string.updating));
        otpLoading.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        otpLoading.setCancelable(false);
        otpLoading.show();

        try {

          /*  JSONArray diagnosisArrayObject = new JSONArray();

            for (int i = 0; i < voiceEMRModelsDiagnosisCategoryList.size(); i++) {
                JSONObject diagnosisObject = new JSONObject();

                diagnosisObject.put("patient_id", patientId);
                diagnosisObject.put("episode_id", episodeId);
                diagnosisObject.put("encounter_id", encounterId);
                diagnosisObject.put("selectedFromAutocomplete", voiceEMRModelsDiagnosisCategoryList.get(i).isDiagnosisSelectedFromAutocomplete());
                diagnosisObject.put("diagnosis", voiceEMRModelsDiagnosisCategoryList.get(i).getDiagnosisDiagnosis());
                diagnosisObject.put("status", voiceEMRModelsDiagnosisCategoryList.get(i).getDiagnosisStatus());
                diagnosisObject.put("posited_on", voiceEMRModelsDiagnosisCategoryList.get(i).getDiagnosisPosited_on());
                diagnosisObject.put("confirmed_ruledout_on", voiceEMRModelsDiagnosisCategoryList.get(i).getDiagnosisConfirmed_ruledout_on());


                diagnosisArrayObject.put(diagnosisObject);
            }*/


            jsonValue = new JSONObject();
            jsonValue.put("data", diagnosisArrayObject);
            Log.d("diagnosisData", "" + jsonValue);

        } catch (Exception e) {

        }
        voiceEMRViewModel.saveDiagnosis(VoiceEMRActivity.this, jsonValue).observe(VoiceEMRActivity.this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                otpLoading.dismiss();
                diagnosisArrayObject = new JSONArray();
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    if (jsonObject.getInt("status_code") == 200) {

                        if (symptomArrayObject.length() > 0 || investigationArrayObject.length() > 0 || observationArrayObject.length() > 0 || treatmentPlanArrayObject.length() > 0) {

                        } else {
                            if (!saveRecordDialog.isShowing()) {
                                saveRecordDialog(VoiceEMRActivity.this);
                            }
                        }

                        /*if (investigationArrayObject.length() > 0) {
                            saveInvestigationRecords();
                        } else {
                            if (!saveRecordDialog.isShowing()) {
                                saveRecordDialog(VoiceEMRActivity.this);
                            }
                        }*/


                    } else {
                        ErrorHandlerClass.INSTANCE.errorHandler(VoiceEMRActivity.this, s);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });
    }

    public void saveInvestigationRecords() {
        ProgressDialog otpLoading = new ProgressDialog(VoiceEMRActivity.this);
        otpLoading.setMessage(getResources().getString(R.string.wait_while_we_updating));
        otpLoading.setTitle(getResources().getString(R.string.updating));
        otpLoading.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        otpLoading.setCancelable(false);
        otpLoading.show();

        try {

          /*  JSONArray investigationArrayObject = new JSONArray();

            for (int i = 0; i < voiceEMRModelsInvestigationCategoryList.size(); i++) {
                JSONObject investigationObject = new JSONObject();

                investigationObject.put("patient_id", patientId);
                investigationObject.put("episode_id", episodeId);
                investigationObject.put("encounter_id", encounterId);
                investigationObject.put("file_url", voiceEMRModelsInvestigationCategoryList.get(i).getInvestigationFile_url());
                investigationObject.put("file_type", voiceEMRModelsInvestigationCategoryList.get(i).getInvestigatioFfile_type());
                investigationObject.put("investigation_name", voiceEMRModelsInvestigationCategoryList.get(i).getInvestigationInvestigation_name());
                investigationObject.put("parameter", voiceEMRModelsInvestigationCategoryList.get(i).getInvestigationParameter());
                investigationObject.put("value", voiceEMRModelsInvestigationCategoryList.get(i).getInvestigationValue());
                investigationObject.put("notes", voiceEMRModelsInvestigationCategoryList.get(i).getInvestigationNotes());

                investigationArrayObject.put(investigationObject);
            }*/


            jsonValue = new JSONObject();
            jsonValue.put("data", investigationArrayObject);
            Log.d("investigationJsonData", "" + jsonValue);

        } catch (Exception e) {

        }
        voiceEMRViewModel.saveInvestigation(VoiceEMRActivity.this, jsonValue).observe(VoiceEMRActivity.this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                otpLoading.dismiss();
                investigationArrayObject = new JSONArray();
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    if (jsonObject.getInt("status_code") == 200) {
                        if (diagnosisArrayObject.length() > 0 || symptomArrayObject.length() > 0 || observationArrayObject.length() > 0 || treatmentPlanArrayObject.length() > 0) {

                        } else {
                            if (!saveRecordDialog.isShowing()) {
                                saveRecordDialog(VoiceEMRActivity.this);
                            }
                        }
                        /*if (observationArrayObject.length() > 0) {
                            saveObservationRecords();
                        } else {
                            if (saveRecordDialog.isShowing()) {
                                saveRecordDialog(VoiceEMRActivity.this);
                            }
                        }*/
//                        saveRecordDialog(VoiceEMRActivity.this);
                    } else {
                        ErrorHandlerClass.INSTANCE.errorHandler(VoiceEMRActivity.this, s);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });
    }

    //save observation record
    public void saveObservationRecords() {
        ProgressDialog otpLoading = new ProgressDialog(VoiceEMRActivity.this);
        otpLoading.setMessage(getResources().getString(R.string.wait_while_we_updating));
        otpLoading.setTitle(getResources().getString(R.string.updating));
        otpLoading.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        otpLoading.setCancelable(false);
        otpLoading.show();

        try {

           /* JSONArray observationArrayObject = new JSONArray();

            for (int i = 0; i < voiceEMRModelsObservationCategoryList.size(); i++) {
                JSONObject observationObject = new JSONObject();

                observationObject.put("patient_id", patientId);
                observationObject.put("episode_id", episodeId);
                observationObject.put("encounter_id", encounterId);
                observationObject.put("category_id", voiceEMRModelsObservationCategoryList.get(i).getObservationCategoryId());
                observationObject.put("type", "observations");


                JSONObject record_Data = new JSONObject();
                record_Data.put("user_id", AppConfigClass.doctorId);
                record_Data.put("doctor_id", AppConfigClass.doctorId);
                record_Data.put("patient_id", patientId);
                record_Data.put("catid", voiceEMRModelsObservationCategoryList.get(i).getObservationCategoryId());
                observationObject.put("record_data", record_Data);

                observationObject.put("metadata", voiceEMRModelsObservationCategoryList.get(i).getObservationCategoryRecords());
                JSONArray dietdata = new JSONArray();
                observationObject.put("dietdata", dietdata);

                observationArrayObject.put(observationObject);
            }*/


            jsonValue = new JSONObject();
            jsonValue.put("data", observationArrayObject);
            Log.d("observationJsonData", "" + jsonValue);

        } catch (Exception e) {

        }
        voiceEMRViewModel.saveObservationAndTreatmentPlan(VoiceEMRActivity.this, jsonValue).observe(VoiceEMRActivity.this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                otpLoading.dismiss();
                observationArrayObject = new JSONArray();
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    if (jsonObject.getInt("status_code") == 200) {
                        if (diagnosisArrayObject.length() > 0 || investigationArrayObject.length() > 0 || symptomArrayObject.length() > 0 || treatmentPlanArrayObject.length() > 0) {

                        } else {
                            if (!saveRecordDialog.isShowing()) {
                                saveRecordDialog(VoiceEMRActivity.this);
                            }
                        }
                        /*if (treatmentPlanArrayObject.length() > 0) {
                            Log.e("this method called","true");
                            saveTreatmentPlanRecords();
                        } else {
                            if (!saveRecordDialog.isShowing()) {
                                saveRecordDialog(VoiceEMRActivity.this);
                            }
                        }*/
                    } else {
                        ErrorHandlerClass.INSTANCE.errorHandler(VoiceEMRActivity.this, s);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });
    }

    //save treatment plan
    public void saveTreatmentPlanRecords() {
        ProgressDialog otpLoading = new ProgressDialog(VoiceEMRActivity.this);
        otpLoading.setMessage(getResources().getString(R.string.wait_while_we_updating));
        otpLoading.setTitle(getResources().getString(R.string.updating));
        otpLoading.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        otpLoading.setCancelable(false);
        otpLoading.show();

        try {

            /*JSONArray treatmentPlanArrayObject = new JSONArray();

            for (int i = 0; i < voiceEMRModelsTreatmentPlanCategoryList.size(); i++) {
                JSONObject treatmentplanObject = new JSONObject();

                treatmentplanObject.put("patient_id", patientId);
                treatmentplanObject.put("episode_id", episodeId);
                treatmentplanObject.put("encounter_id", encounterId);
                treatmentplanObject.put("category_id", voiceEMRModelsTreatmentPlanCategoryList.get(i).getTreatmentCategoryId());
                treatmentplanObject.put("type", "treatmentplan");


                JSONObject record_Data = new JSONObject();
                record_Data.put("user_id", AppConfigClass.doctorId);
                record_Data.put("doctor_id", AppConfigClass.doctorId);
                record_Data.put("patient_id", patientId);
                record_Data.put("catid", voiceEMRModelsTreatmentPlanCategoryList.get(i).getTreatmentCategoryId());
                treatmentplanObject.put("record_data", record_Data);

                treatmentplanObject.put("metadata", voiceEMRModelsTreatmentPlanCategoryList.get(i).getTreatmentCategoryRecords());

                JSONArray dietdata = new JSONArray();
                treatmentplanObject.put("dietdata", dietdata);

                treatmentPlanArrayObject.put(treatmentplanObject);
            }*/


            jsonValue = new JSONObject();
            jsonValue.put("data", treatmentPlanArrayObject);
            Log.d("treatmentJsonData", "" + jsonValue);

        } catch (Exception e) {
            e.printStackTrace();
        }
        voiceEMRViewModel.saveObservationAndTreatmentPlan(VoiceEMRActivity.this, jsonValue).observe(VoiceEMRActivity.this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                otpLoading.dismiss();
                treatmentPlanArrayObject = new JSONArray();
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    if (jsonObject.getInt("status_code") == 200) {
                        if (diagnosisArrayObject.length() > 0 || investigationArrayObject.length() > 0 || symptomArrayObject.length() > 0 || observationArrayObject.length() > 0) {

                        } else {
                            if (!saveRecordDialog.isShowing()) {
                                saveRecordDialog(VoiceEMRActivity.this);
                            }
                        }
                       /* if (!saveRecordDialog.isShowing()) {
                            saveRecordDialog(VoiceEMRActivity.this);
                        }*/
                    } else {
                        ErrorHandlerClass.INSTANCE.errorHandler(VoiceEMRActivity.this, s);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });
    }


    //symptom help dialog
    public void symptomsHelpDialog(Activity activity) {
        ViewGroup viewGroup = activity.findViewById(android.R.id.content);
        View dialogView = LayoutInflater.from(activity).inflate(R.layout.dialog_symptom_help, viewGroup, false);
        RelativeLayout yes = dialogView.findViewById(R.id.yes);
        ImageButton closeButton = dialogView.findViewById(R.id.closeButton);


        AlertDialog optionDialog = new AlertDialog.Builder(this).create();
        optionDialog.setCancelable(false);

        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                optionDialog.dismiss();

            }
        });

        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                optionDialog.dismiss();
            }
        });

        //setting the view of the builder to our custom view that we already inflated
        optionDialog.setView(dialogView);
        if (!isFinishing()) {
            optionDialog.show();
        }

    }

    //observation help dialog
    public void observationHelpDialog(Activity activity) {
        ViewGroup viewGroup = activity.findViewById(android.R.id.content);
        View dialogView = LayoutInflater.from(activity).inflate(R.layout.dialog_observation_help, viewGroup, false);
        RelativeLayout yes = dialogView.findViewById(R.id.yes);
        ImageButton closeButton = dialogView.findViewById(R.id.closeButton);


        AlertDialog optionDialog = new AlertDialog.Builder(this).create();
        optionDialog.setCancelable(false);

        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                optionDialog.dismiss();

            }
        });

        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                optionDialog.dismiss();
            }
        });

        //setting the view of the builder to our custom view that we already inflated
        optionDialog.setView(dialogView);
        if (!isFinishing()) {
            optionDialog.show();
        }

    }

    //diagnosis help dialog
    public void diagnosisHelpDialog(Activity activity) {
        ViewGroup viewGroup = activity.findViewById(android.R.id.content);
        View dialogView = LayoutInflater.from(activity).inflate(R.layout.dialog_diagnosis_help, viewGroup, false);
        RelativeLayout yes = dialogView.findViewById(R.id.yes);
        ImageButton closeButton = dialogView.findViewById(R.id.closeButton);


        AlertDialog optionDialog = new AlertDialog.Builder(this).create();
        optionDialog.setCancelable(false);

        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                optionDialog.dismiss();

            }
        });

        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                optionDialog.dismiss();
            }
        });

        //setting the view of the builder to our custom view that we already inflated
        optionDialog.setView(dialogView);
        if (!isFinishing()) {
            optionDialog.show();
        }

    }

    //treatmentplan help dialog
    public void treatmentplanHelpDialog(Activity activity) {
        ViewGroup viewGroup = activity.findViewById(android.R.id.content);
        View dialogView = LayoutInflater.from(activity).inflate(R.layout.dialog_treatmentplan_help, viewGroup, false);
        RelativeLayout yes = dialogView.findViewById(R.id.yes);
        ImageButton closeButton = dialogView.findViewById(R.id.closeButton);


        AlertDialog optionDialog = new AlertDialog.Builder(this).create();
        optionDialog.setCancelable(false);

        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                optionDialog.dismiss();

            }
        });

        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                optionDialog.dismiss();
            }
        });

        //setting the view of the builder to our custom view that we already inflated
        optionDialog.setView(dialogView);
        if (!isFinishing()) {
            optionDialog.show();
        }

    }

    //investigation help dialog
    public void investigationHelpDialog(Activity activity) {
        ViewGroup viewGroup = activity.findViewById(android.R.id.content);
        View dialogView = LayoutInflater.from(activity).inflate(R.layout.dialog_investigation_help, viewGroup, false);
        RelativeLayout yes = dialogView.findViewById(R.id.yes);
        ImageButton closeButton = dialogView.findViewById(R.id.closeButton);


        AlertDialog optionDialog = new AlertDialog.Builder(this).create();
        optionDialog.setCancelable(false);

        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                optionDialog.dismiss();

            }
        });

        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                optionDialog.dismiss();
            }
        });

        //setting the view of the builder to our custom view that we already inflated
        optionDialog.setView(dialogView);
        if (!isFinishing()) {
            optionDialog.show();
        }

    }


    //Vital popup dialog
    public void vitalPopupDialog(Activity activity, VoiceEMRModel voiceEMRModel) {
        ViewGroup viewGroup = activity.findViewById(android.R.id.content);
        View dialogView = LayoutInflater.from(activity).inflate(R.layout.dialog_vital_popup, viewGroup, false);
        vitalListRV = dialogView.findViewById(R.id.vitalListRV);
        mLayoutManagerVitalPopup = new LinearLayoutManager(getApplicationContext());

        Button yes = dialogView.findViewById(R.id.yes);
        ImageButton closeButton = dialogView.findViewById(R.id.closeButton);


        AlertDialog optionDialog = new AlertDialog.Builder(this).create();
        optionDialog.setCancelable(false);


        //for Observation records
        voiceEMRVitalPopupListAdapter = new VoiceEMRVitalPopupListAdapter(this, voiceEMRModelsVitalsPopupCategoryList, voiceEMRModel, new VoiceEmrRecordClickListener() {
            @Override
            public void onItemClick(View v, int position, String clickActionType, String categoryName, VoiceEMRModel voiceEMRModel, String recordType) {
                observationAddRecordClick = true;
                optionDialog.dismiss();
                if (encounterId != 0) {
                    Intent intent = new Intent(VoiceEMRActivity.this, VoiceEMRCreateRecordActivity.class);
                    intent.putExtra("CategoryId", clickActionType + "");
                    intent.putExtra("CategoryName", categoryName);
                    intent.putExtra("PatientId", patientId);
                    intent.putExtra("EpisodeId", episodeId);
                    intent.putExtra("EncounterID", encounterId);
                    intent.putExtra("Type", "observations");
                    intent.putExtra("VoiceEMRModel", voiceEMRModel);
                    intent.putExtra("recordPosition", 0);
                    intent.putExtra("ObservationEditRecord", "");
                    launchVoiceEMRRecordResults.launch(intent);
                } else {
                    Toast.makeText(VoiceEMRActivity.this, "Please select one interaction before adding a record", Toast.LENGTH_SHORT).show();
                }

            }
        });
        vitalListRV.setLayoutManager(mLayoutManagerVitalPopup);
        vitalListRV.setItemAnimator(new DefaultItemAnimator());
        vitalListRV.setAdapter(voiceEMRVitalPopupListAdapter);

        voiceEMRVitalPopupListAdapter.notifyDataSetChanged();


        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                optionDialog.dismiss();

            }
        });

        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                optionDialog.dismiss();
            }
        });

        //setting the view of the builder to our custom view that we already inflated
        optionDialog.setView(dialogView);
        if (!isFinishing()) {
            optionDialog.show();
        }

    }

    //TreqtmentPlan popup dialog
    public void treatmentPlanPopupDialog(Activity activity, VoiceEMRModel voiceEMRModel) {
        ViewGroup viewGroup = activity.findViewById(android.R.id.content);
        View dialogView = LayoutInflater.from(activity).inflate(R.layout.dialog_vital_popup, viewGroup, false);
        vitalListRV = dialogView.findViewById(R.id.vitalListRV);
        mLayoutManagerTreatmentPlanPopup = new LinearLayoutManager(getApplicationContext());

        TextView headingTitle = dialogView.findViewById(R.id.headingTitle);
        TextView textInfo = dialogView.findViewById(R.id.textInfo);
        Button yes = dialogView.findViewById(R.id.yes);
        ImageButton closeButton = dialogView.findViewById(R.id.closeButton);
        headingTitle.setText("Add Treatment Notes");
        textInfo.setText("");


        AlertDialog optionDialog = new AlertDialog.Builder(this).create();
        optionDialog.setCancelable(false);


        //for TreatmentPlan records
        voiceEMRTreatmentPopupListAdapter = new VoiceEMRTreatmentPopupListAdapter(this, voiceEMRModelsTreatmentPlanPopupCategoryList, voiceEMRModel, new VoiceEmrRecordClickListener() {
            @Override
            public void onItemClick(View v, int position, String clickActionType, String categoryName, VoiceEMRModel voiceEMRModel, String recordType) {
                treatmentPlanAddRecordClick = true;
                optionDialog.dismiss();
                if (encounterId != 0) {
                    Intent intent = new Intent(VoiceEMRActivity.this, VoiceEMRCreateRecordActivity.class);
                    intent.putExtra("CategoryId", clickActionType + "");
                    intent.putExtra("CategoryName", categoryName);
                    intent.putExtra("PatientId", patientId);
                    intent.putExtra("EpisodeId", episodeId);
                    intent.putExtra("EncounterID", encounterId);
                    intent.putExtra("Type", "treatmentplan");
                    intent.putExtra("recordPosition", 0);
                    intent.putExtra("TreatmentEditRecord", "");
                    intent.putExtra("VoiceEMRModel", voiceEMRModel);
                    launchVoiceEMRRecordResults.launch(intent);
                } else {
                    Toast.makeText(VoiceEMRActivity.this, "Please select one interaction before adding a record", Toast.LENGTH_SHORT).show();
                }

            }
        });

        vitalListRV.setLayoutManager(mLayoutManagerTreatmentPlanPopup);
        vitalListRV.setItemAnimator(new DefaultItemAnimator());
        vitalListRV.setAdapter(voiceEMRTreatmentPopupListAdapter);

        voiceEMRTreatmentPopupListAdapter.notifyDataSetChanged();


        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                optionDialog.dismiss();

            }
        });

        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                optionDialog.dismiss();
            }
        });

        //setting the view of the builder to our custom view that we already inflated
        optionDialog.setView(dialogView);
        if (!isFinishing()) {
            optionDialog.show();
        }

    }


    public void getEpisodeFilePreferences() {
        emrConsultationNotesViewModel.getEpisodeFieldPreferences(this).observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                try {

                    JSONObject jsonObject = new JSONObject(s);
                    if (jsonObject.getInt("status_code") == 200) {
                        responseEpisodeFieldPreferences = new JSONObject(s).getJSONObject("response");
                        JSONArray prefArr = responseEpisodeFieldPreferences.getJSONArray("response");
//                        if (prefArr.getJSONObject(0).getInt("status") == 1) {
//                            uploadHandWrittenNotes.setVisibility(View.VISIBLE);
//                        }

                        if (prefArr.getJSONObject(1).getInt("status") == 1) {
//                            logEvalutaionReport.setVisibility(View.VISIBLE);
                            isEvaluationPreferences = true;
                            loadRecyclerViewEvaluationCategoryItem();
                        }

                        if (prefArr.getJSONObject(2).getInt("status") == 1) {
//                            logTreatmentPlan.setVisibility(View.VISIBLE);
                            isTreatmentPreferences = true;
                            loadRecyclerViewTreatmentCategoryItem();
                        }

//                        if (prefArr.getJSONObject(0).getInt("status") == 0 && prefArr.getJSONObject(1).getInt("status") == 0 && prefArr.getJSONObject(2).getInt("status") == 0) {
//                            noRecordPreferencesSetText.setVisibility((View.VISIBLE));
//                        } else {
//                            noRecordPreferencesSetText.setVisibility((View.GONE));
//                        }

                    } else {
                        ErrorHandlerClass.INSTANCE.errorHandler(VoiceEMRActivity.this, s);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });

    }

    private void loadRecyclerViewEvaluationCategoryItem() {

        if (isEvaluationPreferences) {

            emrConsultationNotesViewModel.getEvaluationFieldPreferences(this).observe(this, new Observer<String>() {
                @Override
                public void onChanged(String s) {
                    try {
                        Log.d("EvaluationFieldPrefere", "" + s);
                        JSONObject jsonObject = new JSONObject(s);
                        if (jsonObject.getInt("status_code") == 200) {
                            JSONObject response = new JSONObject(s).getJSONObject("response");
                            JSONArray prefArr = response.getJSONArray("response");

                            if (prefArr.getJSONObject(1).getInt("status") == 1) {

                                //get observation records
                                emrConsultationNotesViewModel.getEvaluationDoctorCategory(VoiceEMRActivity.this).observe(VoiceEMRActivity.this, new Observer<String>() {
                                    @Override
                                    public void onChanged(String s) {
                                        try {
//                                            emrCategoryLoadProgressbar.setVisibility(View.GONE);
                                            JSONObject jsonObject = new JSONObject(s);
                                            if (jsonObject.getInt("status_code") == 200) {
                                                JSONObject response = new JSONObject(s).getJSONObject("response");
                                                JSONArray rootArray = response.getJSONArray("response");

                                                for (int i = 0; i < rootArray.length(); i++) {
                                                    JSONObject categoryArrayObject = rootArray.getJSONObject(i);
                                                    EMRAddRecordCategoryModel myList = new EMRAddRecordCategoryModel(
                                                            categoryArrayObject.getString("category"), categoryArrayObject.getString("name")
                                                    );
                                                    voiceEMRModelsVitalsPopupCategoryList.add(myList);
                                                }


                                            } else {
                                                ErrorHandlerClass.INSTANCE.errorHandler(VoiceEMRActivity.this, s);
                                            }
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }

                                    }
                                });

                            }

//                            if (prefArr.getJSONObject(2).getInt("status") == 1) {
//                                //get symptom records
//                                EMRAddRecordCategoryModel myList = new EMRAddRecordCategoryModel(
//                                        "0", "Investigation Results"
//                                );
//                                emrEvaluationCategoryList.add(myList);
//                            }
//
//                            if (prefArr.getJSONObject(3).getInt("status") == 1) {
//
//                                //get symptom records
//                                EMRAddRecordCategoryModel myList = new EMRAddRecordCategoryModel(
//                                        "0", "Diagnosis"
//                                );
//                                emrEvaluationCategoryList.add(myList);
//
//                            }


                        } else {
                            ErrorHandlerClass.INSTANCE.errorHandler(VoiceEMRActivity.this, s);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            });
        } else {
            emrConsultationNotesViewModel.getEvaluationDoctorCategory(this).observe(this, new Observer<String>() {
                @Override
                public void onChanged(String s) {
                    try {
//                        emrCategoryLoadProgressbar.setVisibility(View.GONE);
                        JSONObject jsonObject = new JSONObject(s);
                        if (jsonObject.getInt("status_code") == 200) {
                            JSONObject response = new JSONObject(s).getJSONObject("response");
                            JSONArray rootArray = response.getJSONArray("response");

                            for (int i = 0; i < rootArray.length(); i++) {
                                JSONObject categoryArrayObject = rootArray.getJSONObject(i);
                                EMRAddRecordCategoryModel myList = new EMRAddRecordCategoryModel(
                                        categoryArrayObject.getString("category"), categoryArrayObject.getString("name")
                                );
                                voiceEMRModelsVitalsPopupCategoryList.add(myList);
                            }
                        } else {
                            ErrorHandlerClass.INSTANCE.errorHandler(VoiceEMRActivity.this, s);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            });

        }


    }

    //treatment plan category item

    private void loadRecyclerViewTreatmentCategoryItem() {

        if (isTreatmentPreferences) {


            //get treatmentPlan records
            emrConsultationNotesViewModel.getTreatmentPlanDoctorCategory(VoiceEMRActivity.this).observe(VoiceEMRActivity.this, new Observer<String>() {
                @Override
                public void onChanged(String s) {
                    try {
//                                            emrCategoryLoadProgressbar.setVisibility(View.GONE);
                        JSONObject jsonObject = new JSONObject(s);
                        Log.d("treatmentCategory", "treatmentCategory" + s);
                        if (jsonObject.getInt("status_code") == 200) {
                            JSONObject response = new JSONObject(s).getJSONObject("response");
                            JSONArray rootArray = response.getJSONArray("response");

                            for (int i = 0; i < rootArray.length(); i++) {
                                JSONObject categoryArrayObject = rootArray.getJSONObject(i);
                                EMRAddRecordCategoryModel myList = new EMRAddRecordCategoryModel(
                                        categoryArrayObject.getString("category"), categoryArrayObject.getString("name")
                                );
                                voiceEMRModelsTreatmentPlanPopupCategoryList.add(myList);
                            }


                        } else {
                            ErrorHandlerClass.INSTANCE.errorHandler(VoiceEMRActivity.this, s);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            });


        }


    }

    public class NetworkChangeReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(final Context context, final Intent intent) {

            Log.v(LOG_TAG, "Receieved notification about network status");
            isNetworkAvailable(context);

        }

    }

    private boolean isNetworkAvailable(Context context) {
        try {

            ConnectivityManager connectivity = (ConnectivityManager)
                    context.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (connectivity != null) {
                NetworkInfo[] info = connectivity.getAllNetworkInfo();
                if (info != null) {
                    for (int i = 0; i < info.length; i++) {
                        if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                            if (!isConnected) {
                                Log.v(LOG_TAG, "Now you are connected to Internet!");
//                            networkStatus.setText("Now you are connected to Internet!");
                                isConnected = true;
//                            connectingLoaderLayout.setVisibility(View.VISIBLE);
//                            viewModel.startAudioRecording("pediatrics");//for starting simbo
                                //do your processing here ---
                                //if you need to post any data to the server or get status
                                //update from the server
                            }
                            return true;
                        }
                    }
                }
            }
            Log.v(LOG_TAG, "You are not connected to Internet!");
//        networkStatus.setText("You are not connected to Internet!");
            isConnected = false;
            viewModel.stopAudioRecording();
            connectionLostPopup(VoiceEMRActivity.this);
            return false;
        } catch (Exception e) {
            return false;
        }

    }


    public void connectionLostPopup(Activity activity) {
        ViewGroup viewGroup = activity.findViewById(android.R.id.content);
        View dialogView = LayoutInflater.from(activity).inflate(R.layout.dialog_connection_lost_dictation, viewGroup, false);
        RelativeLayout connectionClose = dialogView.findViewById(R.id.connectionClose);
        ImageButton closeButton = dialogView.findViewById(R.id.closeButton);

        AlertDialog optionDialog = new AlertDialog.Builder(this).create();
        optionDialog.setCancelable(false);

        connectionClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                optionDialog.dismiss();

            }
        });

        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                optionDialog.dismiss();
            }
        });

        //setting the view of the builder to our custom view that we already inflated
        optionDialog.setView(dialogView);
        if (!isFinishing()) {
            optionDialog.show();
        }

    }


    // check valuem level
    public void checkValueLevel() {
        viewModel.startAudioRecording(simboDevId, simbo_authKey, simboWss1_url);
        viewModel.getAudioLevel().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer s) {
                Log.d("current valume level", String.valueOf(s));
                try {

                    if (stopTestingClick) {

                    } else {
                        micTestingButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorDanger)));
                        micTestingButton.setText("STOP TESTING");
                        volumeProgressBar.setVisibility(View.VISIBLE);
                        volumeProgressBarWhite.setVisibility(View.GONE);

                        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
//                    int volumeLevel = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
//                    int maxVolumeLevel = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
                        volumeLevel = s;
                        int maxVolumeLevel = 50;
                        int volumePercent = (int) (((float) volumeLevel / maxVolumeLevel) * 100);
                        volumeProgressBar.setMax(maxVolumeLevel);
                        volumeProgressBar.setProgress(volumeLevel);
                    }
                } catch (
                        Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }

    @Override
    public void onBackPressed() {
        if (records_adapter_List.size() == 0) {
            cancelVolleyRequest();
            finish();
        } else {
            backToHomeAlertMessage(VoiceEMRActivity.this);
        }
//                finish();
        super.onBackPressed();
    }

    private HashMap<String, List<DictationModel>> groupDataIntoHashMap(LinkedHashMap<String, DictationModel> recordsList) {

        LinkedHashMap<String, List<DictationModel>> groupedHashMap = new LinkedHashMap<>();
        Set<String> keys = recordsList.keySet();

        // printing the elements of LinkedHashMap
        for (String key : keys) {
           /* System.out.println(key + " -- "
                    + linkedHashMap.get(key));*/
            DictationModel dictationObject = recordsList.get(key);


            String hashMapKey = dictationObject.getSection_name();

            if (groupedHashMap.containsKey(hashMapKey)) {
                // The key is already in the HashMap; add the pojo object
                // against the existing key.
                groupedHashMap.get(hashMapKey).add(dictationObject);
            } else {
                // The key is not there in the HashMap; create a new key-value pair
                List<DictationModel> list = new ArrayList<>();
                list.add(dictationObject);
                groupedHashMap.put(hashMapKey, list);
            }
        }
       /* for (DictiatationModel pojoOfJsonArray : recordsList) {

            String hashMapKey = pojoOfJsonArray.getSection_name();

            if (groupedHashMap.containsKey(hashMapKey)) {
                // The key is already in the HashMap; add the pojo object
                // against the existing key.
                groupedHashMap.get(hashMapKey).add(pojoOfJsonArray);
            } else {
                // The key is not there in the HashMap; create a new key-value pair
                List<DictiatationModel> list = new ArrayList<>();
                list.add(pojoOfJsonArray);
                groupedHashMap.put(hashMapKey, list);
            }
        }*/


        return groupedHashMap;
    }
}