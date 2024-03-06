package com.whitecoats.clinicplus;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import com.google.android.gms.common.api.Api;
import com.whitecoats.adapter.PatientRecordsAdapter;
import com.whitecoats.clinicplus.apis.ApiUrls;
import com.whitecoats.clinicplus.utils.ErrorHandlerClass;
import com.whitecoats.model.PatientRecordsModel;
import com.zoho.salesiqembed.ZohoSalesIQ;

import co.mobiwise.materialintro.animation.MaterialIntroListener;
import co.mobiwise.materialintro.shape.Focus;
import co.mobiwise.materialintro.shape.FocusGravity;
import co.mobiwise.materialintro.shape.ShapeType;
import co.mobiwise.materialintro.view.MaterialIntroView;

public class PatientRecordActivity extends AppCompatActivity {

    private int patientId;
    private RelativeLayout recordCaseTab, sharedRecordTab;
    private View recordCaseTabLine, sharedRecordTabLine;
    private LinearLayout tabLayout;

    //shared data
    private RelativeLayout sharedRecordLayout;
    private RecyclerView sharedRecordsCatRv;
    private List<PatientRecordsModel> sharedPatientRecordsModels;
    private PatientRecordsAdapter sharedPatientRecordsAdapter;
    private TextView sharedEmptyText;
    private ProgressBar sharedLaoder;

    //record case data
    private RelativeLayout recordCaseLayout;
    private RecyclerView recordCaseRv;
    private List<PatientRecordsModel> recordCaseModels;
    private TextView recordCaseEmptyText;
    private ProgressBar recordCaseLaoder;
    private PatientRecordsAdapter recordsCaseAdapter;

    //create case
    private Button createNewCase;
    private View createCaseForm;
    private EditText caseName;
    private Spinner caseInteractionMode;
    private RelativeLayout caseDateLayout, caseTimeLayout, createCase;
    private TextView caseDateText, caseTimeText;
    String[] interactionMode = { "Video", "Chat", "Clinic", "Phone", "Home", "Other"};

    private PatientRecordsApi apiCall;
    private AppUtilities appUtilities;
    private ProgressDialog loadingDialog;
    private SharedPreferences appPreference;
    private LinearLayout caseInteractionGuidePt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_record);

        Toolbar toolbar = findViewById(R.id.patientRecordToolBar);
        patientId = getIntent().getIntExtra("PatientId", 0);
        toolbar.setTitle(getIntent().getStringExtra("PatientName"));
        setSupportActionBar(toolbar);
        Drawable mDrawable = getResources().getDrawable(R.drawable.ic_arrow_back);
        mDrawable.setColorFilter(new PorterDuffColorFilter(getResources().getColor(R.color.colorWhite),PorterDuff.Mode.SRC_IN));
        toolbar.setNavigationIcon(mDrawable);

        //shared data
        sharedRecordsCatRv = findViewById(R.id.patientRecordSharedRv);
        sharedEmptyText = findViewById(R.id.patientRecordSharedEmptyText);
        sharedLaoder = findViewById(R.id.patientRecordSharedLoader);
        sharedRecordTab = findViewById(R.id.patientRecordSharedTab);
        sharedRecordLayout = findViewById(R.id.patientRecordSharedLayout);
        sharedPatientRecordsModels = new ArrayList<>();
        sharedRecordTabLine = findViewById(R.id.patientRecordSharedView);

        //record case
        recordCaseTab = findViewById(R.id.patientRecordCaseTab);
        recordCaseLayout = findViewById(R.id.patientRecordCaseLayout);
        recordCaseRv = findViewById(R.id.patientRecordCaseRv);
        recordCaseLaoder = findViewById(R.id.patientRecordCaseLoader);
        recordCaseEmptyText = findViewById(R.id.patientRecordCaseEmptyText);
        recordCaseModels = new ArrayList<>();
        recordCaseTabLine = findViewById(R.id.patientRecordCaseView);

        //create case
        createNewCase = findViewById(R.id.patientRecordCreateCase);
        createCaseForm = findViewById(R.id.recordsCaseCreate);
        caseName = findViewById(R.id.recordsCaseName);
        caseDateLayout = findViewById(R.id.recordsCaseDateLayout);
        caseTimeLayout = findViewById(R.id.recordsCaseTimeLayout);
        caseDateText = findViewById(R.id.recordsCaseDate);
        caseTimeText = findViewById(R.id.recordsCaseTime);
        caseInteractionMode = findViewById(R.id.recordsCaseInteractionType);
        createCase = findViewById(R.id.recordsCaseProceed);
        caseInteractionGuidePt = findViewById(R.id.recordsCaseCreateInteractionGuide);

        tabLayout = findViewById(R.id.patientRecordTabLayout);
        apiCall = new PatientRecordsApi();
        appUtilities = new AppUtilities();
        appPreference = getSharedPreferences(ApiUrls.appSharedPref, MODE_PRIVATE);

        ArrayAdapter aa = new ArrayAdapter(this,android.R.layout.simple_spinner_item, interactionMode);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Setting the ArrayAdapter data on the Spinner
        caseInteractionMode.setAdapter(aa);

        sharedPatientRecordsAdapter = new PatientRecordsAdapter(sharedPatientRecordsModels, this, new PatientRecordListener() {
            @Override
            public void onItemClick(View v, String parameter, String type,String recordIdArray) {
                if(type.equalsIgnoreCase("TO_DETAILS")) {

                    //catId_catName
                    String[] paramSplit = parameter.split("_");

                    Intent intent = new Intent(PatientRecordActivity.this, PatientRecordViewActivity.class);
                    intent.putExtra("CategoryId", Integer.parseInt(paramSplit[0]));
                    intent.putExtra("CategoryName", paramSplit[1]);
                    intent.putExtra("PatientId", patientId);
                    intent.putExtra("PatientSharedFlag", 1);

                    //intent.putExtra("recordIdArray", recordIdArray);

                    startActivity(intent);
                }
            }
        });

        recordsCaseAdapter = new PatientRecordsAdapter(recordCaseModels, this, new PatientRecordListener() {
            @Override
            public void onItemClick(View v, String parameter, String type,String recordIdArray) {
                if(type.equalsIgnoreCase("TO_EPISODE_DETAILS")) {
//                    Log.d("Episode ID", parameter);
                    Intent intent = new Intent(PatientRecordActivity.this, PatientEpisodeActivity.class);
                    intent.putExtra("EpisodeId", Integer.parseInt(parameter));
                    intent.putExtra("PatientId", patientId);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(PatientRecordActivity.this, PatientEpisodeActivity.class);
                    intent.putExtra("EpisodeId", Integer.parseInt(parameter));
                    intent.putExtra("PatientId", patientId);
                    intent.putExtra("SharingRecords", true);
                    startActivity(intent);
                }
            }
        });

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        sharedRecordsCatRv.setLayoutManager(mLayoutManager);
        sharedRecordsCatRv.setItemAnimator(new DefaultItemAnimator());
        sharedRecordsCatRv.setAdapter(sharedPatientRecordsAdapter);

        mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recordCaseRv.setLayoutManager(mLayoutManager);
        recordCaseRv.setItemAnimator(new DefaultItemAnimator());
        recordCaseRv.setAdapter(recordsCaseAdapter);

        getEpisodes();

        recordCaseTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recordCaseLayout.setVisibility(View.VISIBLE);
                sharedRecordLayout.setVisibility(View.GONE);
//                getEpisodes();
                recordCaseTabLine.setVisibility(View.VISIBLE);
                sharedRecordTabLine.setVisibility(View.GONE);
                ZohoSalesIQ.Tracking.setPageTitle("PatientRecords - Record Case Tab");
            }
        });

        sharedRecordTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recordCaseLayout.setVisibility(View.GONE);
                sharedRecordLayout.setVisibility(View.VISIBLE);
                sharedPatientRecordsModels.clear();
                sharedPatientRecordsAdapter.notifyDataSetChanged();
                getSharedRecordCategory();
                recordCaseTabLine.setVisibility(View.GONE);
                sharedRecordTabLine.setVisibility(View.VISIBLE);

                ZohoSalesIQ.Tracking.setPageTitle("PatientRecords - Shared Record Tab");
            }
        });

        createNewCase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createCaseForm.setVisibility(View.VISIBLE);
                createNewCase.setVisibility(View.GONE);
                recordCaseLayout.setVisibility(View.GONE);
                tabLayout.setVisibility(View.GONE);

                showGuide(1);

                ZohoSalesIQ.Tracking.setCustomAction("PatientRecords - Create New Case Form");
            }
        });

        Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH);
        int mDay = c.get(Calendar.DAY_OF_MONTH);
        String temp = mDay + " " + (mMonth+1) + " " + mYear;
        temp = appUtilities.changeDateFormat("dd MM yyyy", "dd MMM, yy", temp);
        caseDateText.setText(temp);
        temp = appUtilities.changeDateFormat("dd MMM, yy", "dd-MM-yyyy", temp);
        temp = "Case-" +  temp;
        caseName.setText(temp);
        caseDateLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDateDialog(caseDateText);
            }
        });


        int mHour = c.get(Calendar.HOUR_OF_DAY);
        int mMinute = c.get(Calendar.MINUTE);
        caseTimeText.setText( mHour + ":" + mMinute);
        caseTimeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openTimeDialog(caseTimeText);
            }
        });

        createCase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!caseName.getText().toString().equalsIgnoreCase("")) {
                    ZohoSalesIQ.Tracking.setCustomAction("PatientRecords - Saving New Case");
                    saveNewCase();
                } else {
                    Toast.makeText(PatientRecordActivity.this, "Please enter a case name", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if(createCaseForm.getVisibility() == View.VISIBLE) {
                    createCaseForm.setVisibility(View.GONE);
                    createNewCase.setVisibility(View.VISIBLE);
                    recordCaseLayout.setVisibility(View.VISIBLE);
                    tabLayout.setVisibility(View.VISIBLE);
                } else {
                    finish();
                }
        }

        return true;
    }

    @Override
    public void onBackPressed() {
        if(createCaseForm.getVisibility() == View.VISIBLE) {
            createCaseForm.setVisibility(View.GONE);
            createNewCase.setVisibility(View.VISIBLE);
            recordCaseLayout.setVisibility(View.VISIBLE);
            tabLayout.setVisibility(View.VISIBLE);
        } else {
            finish();
        }
    }

    private void getSharedRecordCategory() {
        String url = ApiUrls.getSharedRecordsCount + "?doctorId="+ ApiUrls.doctorId +"&patientId="+ patientId +"&type=shared";
//        Log.d("REcords URl", url);
        sharedEmptyText.setVisibility(View.VISIBLE);
        sharedEmptyText.setText(getResources().getString(R.string.loading_data));
        sharedLaoder.setVisibility(View.VISIBLE);

        apiCall.getRecordPref(url, "", this, new VolleyCallback() {
            @Override
            public void onSuccess(String result) {
                try {
//                    Log.d("Success", result);
                    JSONObject resObj = new JSONObject(result);
                    JSONArray catArr = resObj.getJSONObject("response").getJSONArray("categories");
                   // JSONArray catRecordIdArr = resObj.getJSONObject("response").getJSONArray("record_ids");

                    if(catArr.length() > 0) {
                        sharedEmptyText.setVisibility(View.GONE);
                        sharedLaoder.setVisibility(View.GONE);
                        for(int i=0; i<catArr.length(); i++) {
                            JSONObject catObj = catArr.getJSONObject(i);
                            PatientRecordsModel model = new PatientRecordsModel();
                            model.setRecordName(catObj.getString("name"));
                            model.setRecordCount(catObj.getInt("count"));
                            model.setRecordId(catObj.getInt("category"));
                            model.setType(2);
                          //  model.setRecordIdArray(catRecordIdArr);
                            sharedPatientRecordsModels.add(model);
                        }

                        sharedPatientRecordsAdapter.notifyDataSetChanged();
                    } else {
                        sharedEmptyText.setVisibility(View.VISIBLE);
                        sharedEmptyText.setText(getResources().getString(R.string.no_data_found));
                        sharedLaoder.setVisibility(View.GONE);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    sharedEmptyText.setVisibility(View.VISIBLE);
                    sharedEmptyText.setText(getResources().getString(R.string.data_load_failed));
                    sharedLaoder.setVisibility(View.GONE);
                }
            }

            @Override
            public void onError(String err) {
                sharedEmptyText.setVisibility(View.VISIBLE);
                sharedEmptyText.setText(getResources().getString(R.string.data_load_failed));
                sharedLaoder.setVisibility(View.GONE);
            }
        });
    }

    private void getEpisodes() {

        String url = ApiUrls.getEpisodes + "?patient_id=" + patientId;
        apiCall.getRecordPref(url, "", this, new VolleyCallback() {
            @Override
            public void onSuccess(String result) {
                try {
                    JSONObject resObj = new JSONObject(result);
                    JSONArray resArr = resObj.getJSONArray("response");
                    if(resArr.length() > 0) {
                        recordCaseLaoder.setVisibility(View.GONE);
                        recordCaseEmptyText.setVisibility(View.GONE);
                        for(int i=0; i<resArr.length(); i++) {
                            JSONObject episObj = resArr.getJSONObject(i);
                            PatientRecordsModel model = new PatientRecordsModel();
                            model.setEpisodeName(episObj.getString("episode_name"));
                            model.setEpisodeId(episObj.getInt("id"));
                            recordCaseModels.add(model);
                        }
                        recordsCaseAdapter.notifyDataSetChanged();
                    } else {
                        recordCaseEmptyText.setVisibility(View.VISIBLE);
                        recordCaseEmptyText.setText(getResources().getString(R.string.no_data_found));
                        recordCaseLaoder.setVisibility(View.GONE);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    recordCaseEmptyText.setVisibility(View.VISIBLE);
                    recordCaseEmptyText.setText(getResources().getString(R.string.data_load_failed));
                    recordCaseLaoder.setVisibility(View.GONE);
                }
            }

            @Override
            public void onError(String err) {
                recordCaseEmptyText.setVisibility(View.VISIBLE);
                recordCaseEmptyText.setText(getResources().getString(R.string.data_load_failed));
                recordCaseLaoder.setVisibility(View.GONE);
            }
        });
    }

    private void openDateDialog(final TextView tv) {
        int mYear, mDay, mMonth;
        if(tv.getText().equals("")) {
            final Calendar c = Calendar.getInstance();
            mYear = c.get(Calendar.YEAR);
            mMonth = c.get(Calendar.MONTH);
            mDay = c.get(Calendar.DAY_OF_MONTH);
        } else {
            String date = appUtilities.changeDateFormat("dd MMM, yy", "dd MM yyyy", tv.getText().toString());
            String[] dSplit = date.split(" ");
            mDay = Integer.parseInt(dSplit[0]);
            mMonth = Integer.parseInt(dSplit[1]) - 1;
            mYear = Integer.parseInt(dSplit[2]);
        }
        DatePickerDialog datePickerDialog = new DatePickerDialog(PatientRecordActivity.this,
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {

                        String temp = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;
                        temp = appUtilities.changeDateFormat("dd/MM/yyyy", "dd MMM, yy", temp);
                        tv.setText(temp);

                    }
                }, mYear, mMonth, mDay);
        datePickerDialog.show();
    }

    private void openTimeDialog(final TextView tv) {

        caseTimeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String[] timeSplit = tv.getText().toString().split(":");
                int mHour = Integer.parseInt(timeSplit[0]);
                int mMinute = Integer.parseInt(timeSplit[1]);
                TimePickerDialog mTimePicker = new TimePickerDialog(PatientRecordActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        tv.setText( selectedHour + ":" + selectedMinute);
                    }
                }, mHour, mMinute, true);//Yes 24 hour time
                mTimePicker.show();
            }
        });
    }

    private void saveNewCase() {
        loadingDialog = new ProgressDialog(this);
        loadingDialog.setMessage(getResources().getString(R.string.process_request));
        loadingDialog.setCancelable(false);
        loadingDialog.setInverseBackgroundForced(false);
        loadingDialog.show();

        JSONObject caseObj = new JSONObject();
        try {
            caseObj.put("created_by", ApiUrls.doctorId);
            caseObj.put("patient_id", patientId);
            caseObj.put("episode_name", caseName.getText().toString());
        } catch (Exception e) {
            e.printStackTrace();
        }

        apiCall.postRecords(ApiUrls.saveNewEpisode, caseObj, this, new VolleyCallback() {
            @Override
            public void onSuccess(String result) {
                MyClinicGlobalClass.logUserActionEvent(ApiUrls.doctorId, getString(R.string.AppointmentCompletedFollowUp
                ), null);
                loadingDialog.dismiss();
                try {
                    JSONObject resObj = new JSONObject(result);
                    if(resObj.has("response")) {
//                        caseLayout.setVisibility(View.GONE);
//                        caseSelectLayout.setVisibility(View.VISIBLE);
//                        sectionProceed.setVisibility(View.VISIBLE);

                        JSONObject reqObj = new JSONObject();
                        try {
                            reqObj.put("patient_id", patientId);
                            reqObj.put("episode_id", resObj.getString("response"));
                            reqObj.put("encounter_mode", caseInteractionMode.getSelectedItem().toString());

                            String date = appUtilities.changeDateFormat("dd MMM, yy", "dd-MM-yyyy", caseDateText.getText().toString());
                            String dateTime = date + " " + caseTimeText.getText().toString();
                            reqObj.put("encounter_date_time", dateTime);
                            reqObj.put("appointment_id", 0);
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
                loadingDialog.dismiss();
                ErrorHandlerClass.INSTANCE.errorHandler(PatientRecordActivity.this, err);
            }
        });
    }

    private void saveEncounter(final JSONObject reqObj) {

        loadingDialog = new ProgressDialog(this);
        loadingDialog.setMessage(getResources().getString(R.string.process_request));
        loadingDialog.setCancelable(false);
        loadingDialog.setInverseBackgroundForced(false);
        loadingDialog.show();

        apiCall.postRecords(ApiUrls.saveNewInteraction, reqObj, this, new VolleyCallback() {
            @Override
            public void onSuccess(String result) {
                loadingDialog.dismiss();
//                Log.d("Interaction Success", result);
                createCaseForm.setVisibility(View.GONE);
                createNewCase.setVisibility(View.VISIBLE);
                recordCaseLayout.setVisibility(View.VISIBLE);
                tabLayout.setVisibility(View.VISIBLE);

                recordCaseModels.clear();
                recordsCaseAdapter.notifyDataSetChanged();
                getEpisodes();
            }

            @Override
            public void onError(String err) {
                loadingDialog.dismiss();
                ErrorHandlerClass.INSTANCE.errorHandler(PatientRecordActivity.this, err);
            }
        });
    }

    private void showGuide(int section) {
        switch (section) {
            case 1:
                if (!appPreference.getBoolean("PatientCreateCase", false)) {
//                    new GuideView.Builder(this)
//                            .setTitle("Create New Case")
//                            .setContentText("Give a name to the new case or keep it as the default")
//                            .setTargetView(caseName)
//                            .setDismissType(DismissType.outside)
//                            .setGuideListener(new GuideListener() {
//                                @Override
//                                public void onDismiss(View view) {
//                                    showGuide(2);
//                                    SharedPreferences.Editor editor = appPreference.edit();
//                                    editor.putBoolean("PatientCreateCase", true);
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
                            .setInfoText("Give a name to the new case or keep it as the default")
                            .setShape(ShapeType.CIRCLE)
                            .setTarget(caseName)
                            .setUsageId("intro_caseNameThree") //THIS SHOULD BE UNIQUE ID
                            .setListener(new MaterialIntroListener() {
                                @Override
                                public void onUserClicked(String materialIntroViewId) {
                                    showGuide(2);
                                    SharedPreferences.Editor editor = appPreference.edit();
                                    editor.putBoolean("PatientCreateCase", true);
                                    editor.commit();
                                }
                            })
                            .show();




                }
                break;

            case 2:
//                new GuideView.Builder(this)
//                        .setTitle("Case Interactions")
//                        .setContentText("Fill clinicplus the details about the interaction you are going to do with the patient")
//                        .setTargetView(caseInteractionGuidePt)
//                        .setDismissType(DismissType.outside)
//                        .setGuideListener(new GuideListener() {
//                            @Override
//                            public void onDismiss(View view) {
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
                        .setInfoText("Fill clinicplus the details about the interaction you are going to do with the patient")
                        .setShape(ShapeType.CIRCLE)
                        .setTarget(caseInteractionGuidePt)
                        .setUsageId("intro_caseInteractionGuidePtTwo") //THIS SHOULD BE UNIQUE ID
                        .setListener(new MaterialIntroListener() {
                            @Override
                            public void onUserClicked(String materialIntroViewId) {

                            }
                        })
                        .show();


                break;

            case 3:
                break;
        }
    }
}
