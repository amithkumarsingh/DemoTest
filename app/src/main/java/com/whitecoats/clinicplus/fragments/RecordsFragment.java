package com.whitecoats.clinicplus.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.whitecoats.clinicplus.MyClinicGlobalClass;
import com.whitecoats.clinicplus.PatientRecordListener;
import com.whitecoats.clinicplus.R;
import com.whitecoats.clinicplus.activities.AppointmentDetailsActivity;
import com.whitecoats.clinicplus.activities.EMRActivity;
import com.whitecoats.clinicplus.adapters.AppointmentRecordAdapter;
import com.whitecoats.clinicplus.adapters.AppointmentRecordDetailsSharedRecordsListAdapter;
import com.whitecoats.clinicplus.apis.ApiUrls;
import com.whitecoats.clinicplus.models.DashBoardPatientRecordsModel;
import com.whitecoats.clinicplus.models.EMRConsultCaseHistoryModel;
import com.whitecoats.clinicplus.patientsharedrecords.PatientSharedRecordsActivity;
import com.whitecoats.clinicplus.utils.AppUtilities;
import com.whitecoats.clinicplus.utils.ErrorHandlerClass;
import com.whitecoats.clinicplus.viewmodels.DashboardViewModel;
import com.whitecoats.clinicplus.viewmodels.EMRConsultationNotesViewModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class RecordsFragment extends Fragment {
//<<<<<<< HEAD

    private DashboardViewModel dashboardViewModel;
    private EMRConsultationNotesViewModel emrConsultationNotesViewModel;

    //DashBoard shared records section
    private TextView sharedRecodsViewAll, noRecordSharedWithYouText;
    private RecyclerView appt_shared_records_recyclerview;
    private List<DashBoardPatientRecordsModel> shareRecordsModel;
    private AppointmentRecordDetailsSharedRecordsListAdapter sharedRecordsListAdapter;
    private LinearLayout appt_no_record_layout;
    private RelativeLayout appointmentSharedRecordProgressbar;
    private TextView appt_sharedRecords_view_history, appt_last_iteraction;
    String encounterName, encounterCreeatedOn, caseName;
    private AppUtilities appUtilities;
    private List<EMRConsultCaseHistoryModel> caseDetailsList;
    private AppointmentRecordAdapter emrAddingNotesAdapter;
    private RecyclerView.LayoutManager caseDetailsManager;
    private RecyclerView appt_diagnosis_records_recycler;
    private RelativeLayout appointmentRecordProgressbar;
    private LinearLayout appt_no_diagnosis_layout, appt_create_note_button;
    private AppointmentDetailsActivity appointmentDetailsActivity;
    private TextView appt_create_note;


//    private LinearLayout sharedRecordsSection;


    MyClinicGlobalClass globalClass;


    //=======
//>>>>>>> 587e7bbcfa0129e1282fe7979760905b7543ce13
    public RecordsFragment() {
        // Required empty public constructor
    }

    public static RecordsFragment newInstance(String param1, String param2) {
        RecordsFragment fragment = new RecordsFragment();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View appointmentDetailsRecordTab = inflater.inflate(R.layout.fragment_records, container, false);
        dashboardViewModel = new ViewModelProvider(this).get(DashboardViewModel.class);
        dashboardViewModel.init();
        emrConsultationNotesViewModel = new ViewModelProvider(this).get(EMRConsultationNotesViewModel.class);
        emrConsultationNotesViewModel.init();
        globalClass = (MyClinicGlobalClass) getActivity().getApplicationContext();
        appointmentDetailsActivity = (AppointmentDetailsActivity) getActivity();
        shareRecordsModel = new ArrayList<>();
        appUtilities = new AppUtilities();
        caseDetailsList = new ArrayList<>();
        caseDetailsManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        encounterName = CaptureNotesFragment.encounterName;
        encounterCreeatedOn = CaptureNotesFragment.encounterCreatedOn;


        appointmentSharedRecordProgressbar = appointmentDetailsRecordTab.findViewById(R.id.appointmentSharedRecordProgressbar);
        noRecordSharedWithYouText = appointmentDetailsRecordTab.findViewById(R.id.noRecordSharedWithYouText);
        appt_no_record_layout = appointmentDetailsRecordTab.findViewById(R.id.appt_no_record_layout);
        appt_sharedRecords_view_history = appointmentDetailsRecordTab.findViewById(R.id.appt_sharedRecords_view_history);
        appt_last_iteraction = appointmentDetailsRecordTab.findViewById(R.id.appt_last_iteraction);
        appt_diagnosis_records_recycler = appointmentDetailsRecordTab.findViewById(R.id.appt_diagnosis_records_recycler);
        appointmentRecordProgressbar = appointmentDetailsRecordTab.findViewById(R.id.appointmentRecordProgressbar);
        appt_no_diagnosis_layout = appointmentDetailsRecordTab.findViewById(R.id.appt_no_diagnosis_layout);
        appt_create_note_button = appointmentDetailsRecordTab.findViewById(R.id.appt_create_note_button);
        appt_create_note = appointmentDetailsRecordTab.findViewById(R.id.appt_create_note);
        if (encounterCreeatedOn != null) {
            String interactionCreatedOn = encounterCreeatedOn;
            String[] separatedInteractionDate = interactionCreatedOn.split(" ");
            String createInteractionDate = separatedInteractionDate[0];
            String interactionDate = appUtilities.changeDateFormat("yyyy-MM-dd", "dd MMM, yy", createInteractionDate);
            String createdInteractionTime = separatedInteractionDate[1].substring(0, separatedInteractionDate[1].length() - 3);
            appt_last_iteraction.setText(encounterName + " on " + interactionDate + " @ " + createdInteractionTime);
        }


        emrAddingNotesAdapter = new AppointmentRecordAdapter(getActivity(), caseDetailsList);
        appt_diagnosis_records_recycler.setLayoutManager(caseDetailsManager);
        appt_diagnosis_records_recycler.setItemAnimator(new DefaultItemAnimator());
        appt_diagnosis_records_recycler.setAdapter(emrAddingNotesAdapter);


        appt_shared_records_recyclerview = appointmentDetailsRecordTab.findViewById(R.id.appt_shared_records_recyclerview);
        sharedRecordsListAdapter = new AppointmentRecordDetailsSharedRecordsListAdapter(getActivity(), shareRecordsModel, new PatientRecordListener() {
            @Override
            public void onItemClick(View v, String parameter, String type, String recordIdArray) {
                if (globalClass.isOnline()) {
                    dashboardViewModel.getFileFromUrl(getActivity(), parameter).observe(getActivity(), new Observer<String>() {
                        @Override
                        public void onChanged(String s) {
                            try {

                                JSONObject jsonObject = new JSONObject(s);
                                if (jsonObject.getInt("status_code") == 200) {

                                    JSONObject response = new JSONObject(s).getJSONObject("response");
                                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(response.getString("response")));
                                    startActivity(browserIntent);
                                } else {
                                    ErrorHandlerClass.INSTANCE.errorHandler(requireContext(), s);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();

                            }

                        }
                    });

                } else {
                    globalClass.noInternetConnection.showDialog(getActivity());
                }
            }
        });

        appt_shared_records_recyclerview.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext(), LinearLayoutManager.HORIZONTAL, false));
        appt_shared_records_recyclerview.setItemAnimator(new DefaultItemAnimator());
        appt_shared_records_recyclerview.setAdapter(sharedRecordsListAdapter);

        appt_sharedRecords_view_history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (globalClass.isOnline()) {
                    Intent intent = new Intent(getActivity(), PatientSharedRecordsActivity.class);
                    startActivity(intent);
                } else {
                    globalClass.noInternetConnection.showDialog(getActivity());
                }

            }
        });

        appt_create_note.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyClinicGlobalClass.logUserActionEvent(ApiUrls.doctorId, getString(R.string.RecordsScreenCreateNote), null);
                Intent intent = new Intent(getActivity(), EMRActivity.class);
                intent.putExtra("ApptId", appointmentDetailsActivity.appointmentID);
                intent.putExtra("PatientId", appointmentDetailsActivity.patientId);
                intent.putExtra("ApptMode", appointmentDetailsActivity.appointmentMode);
                intent.putExtra("ApptDate", appointmentDetailsActivity.appointmentDate);
                intent.putExtra("ApptTime", appointmentDetailsActivity.appointmentTime);
                intent.putExtra("PatientName", appointmentDetailsActivity.patientName);
                getActivity().startActivity(intent);
            }
        });

        appt_create_note_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyClinicGlobalClass.logUserActionEvent(ApiUrls.doctorId, getString(R.string.RecordsScreenCreateNote), null);
                Intent intent = new Intent(getActivity(), EMRActivity.class);
                intent.putExtra("ApptId", appointmentDetailsActivity.appointmentID);
                intent.putExtra("PatientId", appointmentDetailsActivity.patientId);
                intent.putExtra("ApptMode", appointmentDetailsActivity.appointmentMode);
                intent.putExtra("ApptDate", appointmentDetailsActivity.appointmentDate);
                intent.putExtra("ApptTime", appointmentDetailsActivity.appointmentTime);
                intent.putExtra("PatientName", appointmentDetailsActivity.patientName);
                getActivity().startActivity(intent);
            }
        });


        return appointmentDetailsRecordTab;
    }

    @Override
    public void onResume() {
        super.onResume();

        caseDetailsList.clear();
        getInteractionDetails(appointmentDetailsActivity.patientId, CaptureNotesFragment.encounterId);

        dashboardViewModel.getSharedAndFollowUpDetails(getActivity()).observe(getActivity(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                try {

                    if (shareRecordsModel != null) {
                        shareRecordsModel.clear();
                    }

                    JSONObject jsonObject = new JSONObject(s);
                    if (jsonObject.getInt("status_code") == 200) {
                        JSONObject response = new JSONObject(s).getJSONObject("response");

//                        share records
                        JSONArray sharedRecordsArr = response.getJSONObject("response").getJSONArray("shared_records");
                        Log.d("Shared Records", sharedRecordsArr.length() + "");
                        if (sharedRecordsArr.length() > 0) {
                            appt_no_record_layout.setVisibility(View.GONE);
                            appointmentSharedRecordProgressbar.setVisibility(View.GONE);

                        } else {

                        }

                        JSONObject fieldDicArr = response.getJSONObject("response").getJSONObject("field-dictionary");
                        if (sharedRecordsArr.length() > 0) {
                            for (int i = 0; i < sharedRecordsArr.length(); i++) {

                                JSONObject sharedObj = sharedRecordsArr.getJSONObject(i);
                                DashBoardPatientRecordsModel model = new DashBoardPatientRecordsModel();

                                String catId = sharedObj.getJSONObject("recordinfo")
                                        .getJSONObject("records").getJSONObject("category").getInt("id") + "";


                                //------------by dileep-------------
                                String catName = sharedObj.getJSONObject("recordinfo")
                                        .getJSONObject("records").getJSONObject("category").getString("category") + "";
                                JSONObject recordDetailsObjects = sharedObj.getJSONObject("recordinfo")
                                        .getJSONObject("records");
                                String sharedOnDateTime = recordDetailsObjects.getJSONObject("share_details").getString("created_at");
                                JSONArray recordDetailsArray = new JSONArray();
                                recordDetailsArray.put(sharedObj);
                                int recordId = sharedObj.getInt("record_id");// added by dileep
                                model.setCatId(catId);//added by dileep
                                model.setCategoryId(Integer.parseInt(catId));
                                model.setFieldDictionary(fieldDicArr.toString());// added by dileep
                                model.setFieldDictionaryObject(fieldDicArr);
                                model.setRecordId(recordId);// added by dileep
                                model.setCatRecordData(recordDetailsArray.toString());
                                model.setRecordDetailsObject(recordDetailsObjects);
                                model.setSharedOnDateTime(sharedOnDateTime);
                                model.setCatName(catName);
                                model.setCreated_At(sharedObj.getString("created_at"));
                                //-----------------end------------------


                                if (fieldDicArr.get(catId) instanceof JSONArray) {
                                    JSONArray fieldArr = fieldDicArr.getJSONArray(sharedObj.getJSONObject("recordinfo")
                                            .getJSONObject("records").getJSONObject("category").getInt("id") + "");


                                    model.setPrimaryKey(fieldArr.getJSONObject(0).getString("name"));
                                    if (fieldArr.length() > 1) {
                                        model.setSecKey(fieldArr.getJSONObject(1).getString("name"));
                                        if (sharedObj.getJSONObject("recordinfo").getJSONObject("records").has(fieldArr.getJSONObject(1).getString("key"))) {
                                            model.setSecData(sharedObj.getJSONObject("recordinfo").getJSONObject("records").getString(fieldArr.getJSONObject(1).getString("key")));
                                        } else {
                                            model.setSecData("-");
                                        }
//                                            model.setSecData(sharedObj.getJSONObject("recordinfo").getJSONObject("records").getString(fieldArr.getJSONObject(1).getString("key")));
                                    }

                                    if (fieldArr.length() > 2) {
                                        model.setTernaryKey(fieldArr.getJSONObject(2).getString("name"));
                                        if (!sharedObj.getJSONObject("recordinfo").getJSONObject("records").has(fieldArr.getJSONObject(2).getString("key"))) {
                                            // if (sharedObj.getJSONObject("recordinfo").getJSONObject("records").getString(fieldArr.getJSONObject(2).getString("key")) == null) {

                                        } else {
                                            model.setTernaryData(sharedObj.getJSONObject("recordinfo").getJSONObject("records").getString(fieldArr.getJSONObject(2).getString("key")));
                                        }

                                    }

                                    if (sharedObj.getJSONObject("recordinfo").getJSONObject("records").has(fieldArr.getJSONObject(0).getString("key"))) {
                                        model.setPrimaryData(sharedObj.getJSONObject("recordinfo").getJSONObject("records").getString(fieldArr.getJSONObject(0).getString("key")));
                                    }

                                    model.setFileUrl("");
                                    if (sharedObj.getJSONObject("recordinfo").getJSONObject("records").has("url")) {
                                        model.setFileUrl(sharedObj.getJSONObject("recordinfo").getJSONObject("records").getString("url"));
                                    }

                                    //use to set patient name
                                    model.setRecordName(sharedObj.getJSONObject("recordinfo").getJSONObject("records").getString("patient"));

                                    shareRecordsModel.add(model);
                                } else {
                                    JSONObject fieldArr = fieldDicArr.getJSONObject(sharedObj.getJSONObject("recordinfo")
                                            .getJSONObject("records").getJSONObject("category").getInt("id") + "");

                                    model.setPrimaryKey(fieldArr.getJSONObject("0").getString("name"));
                                    if (fieldArr.length() > 1) {
                                        model.setSecKey(fieldArr.getJSONObject("1").getString("name"));
                                        if (sharedObj.getJSONObject("recordinfo").getJSONObject("records").has(fieldArr.getJSONObject("1").getString("key"))) {
                                            model.setSecData(sharedObj.getJSONObject("recordinfo").getJSONObject("records").getString(fieldArr.getJSONObject("1").getString("key")));
                                        } else {
                                            model.setSecData("-");
                                        }
//                                            model.setSecData(sharedObj.getJSONObject("recordinfo").getJSONObject("records").getString(fieldArr.getJSONObject("1").getString("key")));
                                    }

                                    if (fieldArr.length() > 2) {
                                        model.setTernaryKey(fieldArr.getJSONObject("2").getString("name"));

                                        // String jsonValue = fieldArr.getJSONObject("2").getString("key");
                                        if (sharedObj.getJSONObject("recordinfo").getJSONObject("records").getString(fieldArr.getJSONObject("2").getString("key")) == null) {

                                        } else {
                                            model.setTernaryData(sharedObj.getJSONObject("recordinfo").getJSONObject("records").getString(fieldArr.getJSONObject("2").getString("key")));
                                        }
                                    }

                                    if (sharedObj.getJSONObject("recordinfo").getJSONObject("records").has(fieldArr.getJSONObject("0").getString("key")))
                                        model.setPrimaryData(sharedObj.getJSONObject("recordinfo").getJSONObject("records").getString(fieldArr.getJSONObject("0").getString("key")));

                                    model.setFileUrl("");
                                    if (sharedObj.getJSONObject("recordinfo").getJSONObject("records").has("url")) {
                                        model.setFileUrl(sharedObj.getJSONObject("recordinfo").getJSONObject("records").getString("url"));
                                    }

                                    shareRecordsModel.add(model);
                                }

                            }

                            sharedRecordsListAdapter.notifyDataSetChanged();
                        } else {
                            appt_no_record_layout.setVisibility(View.VISIBLE);
                            appointmentSharedRecordProgressbar.setVisibility(View.GONE);
                            noRecordSharedWithYouText.setText("has not shared any records with you yet");
                        }


                    } else {
                        ErrorHandlerClass.INSTANCE.errorHandler(requireContext(), s);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });

    }


    public void getInteractionDetails(int patId, int encounterId) {

        emrConsultationNotesViewModel.getAddedNotes(getActivity(), patId, encounterId).observe(getActivity(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                appointmentRecordProgressbar.setVisibility(View.GONE);
                try {
//                    caseDetailsList.clear();
                    JSONObject response = new JSONObject(s);
                    if (response.getInt("status_code") == 200) {
//                        noNoteLayout.setVisibility(View.GONE);
//                        caseDetailsRecycler.setVisibility(View.VISIBLE);
//                        floatingActionButton.setVisibility(View.VISIBLE);
//                        caseLayout.setVisibility(View.VISIBLE);
//                        howItworkd.setVisibility(View.GONE);

                        JSONObject responseObj = response.getJSONObject("response").getJSONObject("response");
                        JSONObject fieldDicArr = responseObj.getJSONObject("field-dictionary");

                        if (responseObj.getInt("allRecordsCount") > 0) {

                            appt_no_diagnosis_layout.setVisibility(View.GONE);
                            appt_create_note.setVisibility(View.VISIBLE);

//                            appt_no_diagnosis_layout.setVisibility(View.GONE);

//                            uploadHandWrittenNotes.setVisibility(View.GONE);
//                            logEvalutaionReport.setVisibility(View.GONE);
//                            logTreatmentPlan.setVisibility(View.GONE);
//                            noRecordPreferencesSetText.setVisibility((View.GONE));
//                            caseFooter.setVisibility(View.VISIBLE);

                            //handwritten record Data
                            JSONArray HandWrittenNoteArray = responseObj.optJSONArray("handWrittenNotesRecords");
                            if (HandWrittenNoteArray != null) {
                                if (HandWrittenNoteArray.length() > 0) {
                                    for (int i = 0; i < HandWrittenNoteArray.length(); i++) {
                                        JSONObject handWrittenNoteObject = HandWrittenNoteArray.getJSONObject(i);
                                        EMRConsultCaseHistoryModel emrConsultCaseHistoryModel = new EMRConsultCaseHistoryModel();

                                        emrConsultCaseHistoryModel.setEncounterID(encounterId);
                                        emrConsultCaseHistoryModel.setEncounterName(encounterName);
                                        emrConsultCaseHistoryModel.setEncounterDateTime(encounterCreeatedOn);
                                        emrConsultCaseHistoryModel.setCategoryName("HandWritten Note");
                                        emrConsultCaseHistoryModel.setCreatedAt(handWrittenNoteObject.getString("created_at"));
                                        emrConsultCaseHistoryModel.setIsRecordData(1);
                                        //------------by dileep-------------
                                        JSONArray recordDetailsArray = new JSONArray();
                                        recordDetailsArray.put(handWrittenNoteObject);
//                                        int recordId = sharedObj.getInt("record_id");// added by dileep
                                        emrConsultCaseHistoryModel.setCategoryId(handWrittenNoteObject.getInt("id"));//added by dileep
                                        emrConsultCaseHistoryModel.setFieldDictionary(fieldDicArr.toString());// added by dileep
//                                        model.setRecordId(recordId);// added by dileep
                                        emrConsultCaseHistoryModel.setCategoryRecordData(recordDetailsArray.toString());


                                        caseDetailsList.add(emrConsultCaseHistoryModel);
                                    }

                                }
                            }

                            //symptoms record Data
                            JSONArray symptomsArray = responseObj.optJSONArray("symptomsRecords");
                            if (symptomsArray != null) {
                                if (symptomsArray.length() > 0) {
                                    for (int i = 0; i < symptomsArray.length(); i++) {
                                        JSONObject symptomsObject = symptomsArray.getJSONObject(i);
                                        EMRConsultCaseHistoryModel emrConsultCaseHistoryModel = new EMRConsultCaseHistoryModel();
                                        emrConsultCaseHistoryModel.setEncounterID(encounterId);
                                        emrConsultCaseHistoryModel.setEncounterName(encounterName);
                                        emrConsultCaseHistoryModel.setEncounterDateTime(encounterCreeatedOn);
                                        emrConsultCaseHistoryModel.setCategoryName("Symptoms");
                                        emrConsultCaseHistoryModel.setCreatedAt(symptomsObject.getString("created_at"));
                                        emrConsultCaseHistoryModel.setIsRecordData(1);

                                        //------------by dileep-------------
                                        JSONArray recordDetailsArray = new JSONArray();
                                        recordDetailsArray.put(symptomsObject);
//                                        int recordId = sharedObj.getInt("record_id");// added by dileep
                                        emrConsultCaseHistoryModel.setCategoryId(symptomsObject.getInt("id"));//added by dileep
                                        emrConsultCaseHistoryModel.setFieldDictionary(fieldDicArr.toString());// added by dileep
//                                        model.setRecordId(recordId);// added by dileep
                                        emrConsultCaseHistoryModel.setCategoryRecordData(recordDetailsArray.toString());


                                        caseDetailsList.add(emrConsultCaseHistoryModel);
                                    }
                                }
                            }


                            //observation record data
                            JSONArray observationArray = responseObj.optJSONArray("observationsRecords");
                            if (observationArray != null) {
                                if (observationArray.length() > 0) {
                                    for (int i = 0; i < observationArray.length(); i++) {
                                        JSONObject observationObject = observationArray.getJSONObject(i);
//                                            EMRConsultCaseHistoryModel emrConsultCaseHistoryModel = new EMRConsultCaseHistoryModel();
                                        JSONArray observationRecordsArray = observationObject.getJSONArray("records");
                                        JSONObject observationRecordCategoryObject = observationObject.getJSONObject("record_categories");
                                        if (observationRecordsArray.length() > 0) {
                                            for (int obs = 0; obs < observationRecordsArray.length(); obs++) {
                                                EMRConsultCaseHistoryModel emrConsultCaseHistoryModel = new EMRConsultCaseHistoryModel();

                                                emrConsultCaseHistoryModel.setEncounterID(encounterId);
                                                emrConsultCaseHistoryModel.setEncounterName(encounterName);
                                                emrConsultCaseHistoryModel.setEncounterDateTime(encounterCreeatedOn);
                                                emrConsultCaseHistoryModel.setCategoryName(observationRecordCategoryObject.getString("category"));
                                                emrConsultCaseHistoryModel.setCreatedAt(observationRecordsArray.getJSONObject(obs).getString("created_at"));
                                                emrConsultCaseHistoryModel.setMultiRecordPosition(obs);
                                                emrConsultCaseHistoryModel.setIsRecordData(1);

                                                //------------by dileep-------------
                                                JSONArray recordDetailsArray = new JSONArray();
                                                recordDetailsArray.put(observationObject);
                                                int recordId = observationObject.getInt("id");// added by dileep
                                                emrConsultCaseHistoryModel.setCategoryId(observationRecordCategoryObject.getInt("id"));//added by dileep
                                                emrConsultCaseHistoryModel.setFieldDictionary(fieldDicArr.toString());// added by dileep
                                                emrConsultCaseHistoryModel.setRecordId(recordId);// added by dileep
                                                emrConsultCaseHistoryModel.setCategoryRecordData(recordDetailsArray.toString());


                                                caseDetailsList.add(emrConsultCaseHistoryModel);
                                            }
                                        }

                                    }
                                }
                            }


                            //investigation results record Data
                            JSONArray investigationResultRecordsArray = responseObj.optJSONArray("investigationResultsRecords");
                            if (investigationResultRecordsArray != null) {
                                if (investigationResultRecordsArray.length() > 0) {
                                    for (int i = 0; i < investigationResultRecordsArray.length(); i++) {
                                        JSONObject investigationResultRecordsObject = investigationResultRecordsArray.getJSONObject(i);
                                        EMRConsultCaseHistoryModel emrConsultCaseHistoryModel = new EMRConsultCaseHistoryModel();

                                        emrConsultCaseHistoryModel.setEncounterID(encounterId);
                                        emrConsultCaseHistoryModel.setEncounterName(encounterName);
                                        emrConsultCaseHistoryModel.setEncounterDateTime(encounterCreeatedOn);
                                        emrConsultCaseHistoryModel.setCategoryName("Investigation Results");
                                        emrConsultCaseHistoryModel.setCreatedAt(investigationResultRecordsObject.getString("created_at"));
                                        emrConsultCaseHistoryModel.setIsRecordData(1);

                                        //------------by dileep-------------
                                        JSONArray recordDetailsArray = new JSONArray();
                                        recordDetailsArray.put(investigationResultRecordsObject);
//                                        int recordId = sharedObj.getInt("record_id");// added by dileep
                                        emrConsultCaseHistoryModel.setCategoryId(investigationResultRecordsObject.getInt("id"));//added by dileep
                                        emrConsultCaseHistoryModel.setFieldDictionary(fieldDicArr.toString());// added by dileep
//                                        model.setRecordId(recordId);// added by dileep
                                        emrConsultCaseHistoryModel.setCategoryRecordData(recordDetailsArray.toString());

                                        caseDetailsList.add(emrConsultCaseHistoryModel);
                                    }
                                }

                            }

                            //diagnosis record Data
                            JSONArray diagnosisRecordsArray = responseObj.optJSONArray("diagnosisRecords");
                            if (diagnosisRecordsArray != null) {
                                if (diagnosisRecordsArray.length() > 0) {
//                                    appt_no_diagnosis_layout.setVisibility(View.GONE);
//                                    appt_create_note.setVisibility(View.VISIBLE);

                                    for (int i = 0; i < diagnosisRecordsArray.length(); i++) {
                                        JSONObject diagnosisRecordsArrayRecordsObject = diagnosisRecordsArray.getJSONObject(i);
                                        EMRConsultCaseHistoryModel emrConsultCaseHistoryModel = new EMRConsultCaseHistoryModel();

                                        emrConsultCaseHistoryModel.setEncounterID(encounterId);
                                        emrConsultCaseHistoryModel.setEncounterName(encounterName);
                                        emrConsultCaseHistoryModel.setEncounterDateTime(encounterCreeatedOn);
                                        emrConsultCaseHistoryModel.setCategoryName("Diagnosis");
                                        emrConsultCaseHistoryModel.setName(diagnosisRecordsArrayRecordsObject.getString("diagnosis"));
                                        emrConsultCaseHistoryModel.setPostedOn(diagnosisRecordsArrayRecordsObject.getString("posited_on"));
                                        emrConsultCaseHistoryModel.setStatus(diagnosisRecordsArrayRecordsObject.getString("status"));
                                        emrConsultCaseHistoryModel.setCreatedAt(diagnosisRecordsArrayRecordsObject.getString("created_at"));
                                        emrConsultCaseHistoryModel.setIsRecordData(1);

                                        //------------by dileep-------------
                                        JSONArray recordDetailsArray = new JSONArray();
                                        recordDetailsArray.put(diagnosisRecordsArrayRecordsObject);
//                                        int recordId = sharedObj.getInt("record_id");// added by dileep
                                        emrConsultCaseHistoryModel.setCategoryId(diagnosisRecordsArrayRecordsObject.getInt("id"));//added by dileep
                                        emrConsultCaseHistoryModel.setFieldDictionary(fieldDicArr.toString());// added by dileep
//                                        model.setRecordId(recordId);// added by dileep
                                        emrConsultCaseHistoryModel.setCategoryRecordData(recordDetailsArray.toString());

                                        caseDetailsList.add(emrConsultCaseHistoryModel);
                                    }
                                } else {
//                                    appt_no_diagnosis_layout.setVisibility(View.VISIBLE);
//                                    appt_create_note.setVisibility(View.GONE);
                                }


                            }


                            //treatment Plan record data
                            JSONArray treatmentPlanArray = responseObj.optJSONArray("treatmentPlanRecords");
                            if (treatmentPlanArray != null) {
                                if (treatmentPlanArray.length() > 0) {
                                    for (int i = 0; i < treatmentPlanArray.length(); i++) {
                                        JSONObject treatmentPlanObject = treatmentPlanArray.getJSONObject(i);
//                                            EMRConsultCaseHistoryModel emrConsultCaseHistoryModel = new EMRConsultCaseHistoryModel();
                                        JSONArray treatmentPlanRecordsArray = treatmentPlanObject.getJSONArray("records");
                                        JSONObject treatmentPlanRecordCategoryObject = treatmentPlanObject.getJSONObject("record_categories");
                                        if (treatmentPlanRecordsArray.length() > 0) {
                                            for (int treat = 0; treat < treatmentPlanRecordsArray.length(); treat++) {
                                                EMRConsultCaseHistoryModel emrConsultCaseHistoryModel = new EMRConsultCaseHistoryModel();

                                                emrConsultCaseHistoryModel.setEncounterID(encounterId);
                                                emrConsultCaseHistoryModel.setEncounterName(encounterName);
                                                emrConsultCaseHistoryModel.setEncounterDateTime(encounterCreeatedOn);
                                                emrConsultCaseHistoryModel.setCategoryName(treatmentPlanRecordCategoryObject.getString("category"));
                                                emrConsultCaseHistoryModel.setCreatedAt(treatmentPlanRecordsArray.getJSONObject(treat).getString("created_at"));
                                                emrConsultCaseHistoryModel.setMultiRecordPosition(treat);
                                                emrConsultCaseHistoryModel.setIsRecordData(1);

                                                //------------by dileep-------------
                                                JSONArray recordDetailsArray = new JSONArray();
                                                recordDetailsArray.put(treatmentPlanObject);
                                                int recordId = treatmentPlanObject.getInt("id");// added by dileep
                                                emrConsultCaseHistoryModel.setCategoryId(treatmentPlanRecordCategoryObject.getInt("id"));//added by dileep
                                                emrConsultCaseHistoryModel.setFieldDictionary(fieldDicArr.toString());// added by dileep
                                                emrConsultCaseHistoryModel.setRecordId(recordId);// added by dileep
                                                emrConsultCaseHistoryModel.setCategoryRecordData(recordDetailsArray.toString());


                                                caseDetailsList.add(emrConsultCaseHistoryModel);
                                            }
                                        }

                                    }
                                }
                            }
                            emrAddingNotesAdapter.notifyDataSetChanged();


                        } else {

                            appt_no_diagnosis_layout.setVisibility(View.VISIBLE);
                            appt_create_note.setVisibility(View.GONE);
//                            caseDetailsRecycler.setVisibility(View.GONE);
//                            floatingActionButton.setVisibility(View.GONE);
//                            if (sharedPreferences.getBoolean("dontShowCreateRecords", false)) {
//                                howItworkd.setVisibility(View.GONE);
//                                noNoteLayout.setVisibility(View.VISIBLE);
//                                caseLayout.setVisibility(View.VISIBLE);
//                                recordPreferencesLoadProgressbar.setVisibility(View.VISIBLE);
//                                caseFooter.setVisibility(View.GONE);
//                                getEpisodeFilePreferences();
//                            } else {
//                                howItworkd.setVisibility(View.VISIBLE);
//                                noNoteLayout.setVisibility(View.GONE);
//                                caseLayout.setVisibility(View.GONE);
//                            }
                        }
                    } else {
                        ErrorHandlerClass.INSTANCE.errorHandler(requireContext(), s);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }


}