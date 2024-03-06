package com.whitecoats.clinicplus;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import android.os.Build;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;

import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.whitecoats.adapter.CategoryGridViewCustomAdapter;
import com.whitecoats.adapter.PatientRecordsAboutAdapter;
import com.whitecoats.clinicplus.apis.ApiGetPostMethodCalls;
import com.whitecoats.clinicplus.apis.ApiUrls;
import com.whitecoats.clinicplus.utils.ErrorHandlerClass;
import com.whitecoats.clinicplus.utils.ShowAlertDialog;
import com.whitecoats.model.AppointmentApptListModel;
import com.whitecoats.model.PatientRecordsModel;

import co.mobiwise.materialintro.animation.MaterialIntroListener;
import co.mobiwise.materialintro.shape.Focus;
import co.mobiwise.materialintro.shape.FocusGravity;
import co.mobiwise.materialintro.shape.ShapeType;
import co.mobiwise.materialintro.view.MaterialIntroView;

public class PatientProfileActivity extends AppCompatActivity {

    private LinearLayout mainLayout;
    public static TextView patientProCat, patientProCatEdit;
    private TextView patientProDiag, patientProDiagView;
    //    private TextView patientProBgName, patientProBgAge, patientProBgGender;
    private String assignCategoryData;
    private List<AppointmentApptListModel> doctorSaveCategoryList;
    private List<AppointmentApptListModel> doctorCategoryList;
    private CategoryGridViewCustomAdapter mListAdapter;
    private GridView gridview;
    private String GetItem;
    private AutoCompleteTextView actv;
    private int patientId;
    private String patientName;
    private JSONObject jsonValue;
    private String patientPhone;
    private SharedPreferences appPreference;

    private RecyclerView familyRecycleView;
    private List<PatientRecordsModel> familyModels;
    private PatientRecordsAboutAdapter familyAdapter;
    private TextView familyEmptyText;

    //patient background
    private CardView patientInfoLayout;
    private TextView patientInfoMore;
    private EditText pName, pPhNo, pEmail, pAge, pHeight, pId;
    private Spinner pBloodG;
    private TextView pDOB, pEmptyText;
    private RadioGroup pGender;
    private String[] bloodType = {"A+", "A-", "B+", "B-", "O+", "O-", "AB+", "AB-"};
    private int mYear, mMonth, mDay, mHour, mMinute;
    private Button savePatientData;
    private int genderId = 0;
    private int updatePatientId = 0;

    private int genderFlag = 0, bloodGroupFlag = 0;

    private PatientRecordsApi patientRecordsApi;
    private AppUtilities appUtilities;
    private ProgressDialog loadingDialog;

    //add family info
    private LinearLayout problemCheckBoxes;
    private RelativeLayout familyForm;
    private EditText familyName, familyAge;
    private Spinner familyRelation;
    private ImageView familyCloseForm;
    private TextView addFamily;
    private Button saveFamilyData;
    private String[] relation = {"Choose Relation", "Father", "Mother", "Brother", "Sister", "Wife", "Cousin"};
    private List<Integer> problemSelected;

    //records
    private TextView recordsViewAll, recordView;
    private RelativeLayout recordsLayout;
    private TextView recordCaseName, recordNoOfInteraction, recordDiagnosis, recordsEmptyText;
    private int episodeId = 0;
    private CardView recordsCard;

    //appointment
    private RelativeLayout apptLayout;
    private TextView apptName, apptDateTime, apptPayment, apptType, apptViewAll, apptCompletedText, apptEmptyText, apptBook;
    private LinearLayout apptCancel, apptNote, apptCompleted;
    private int apptId = 0, apptMode = 0, apptOrderId = 0, apptOrderAmt = 0;
    private String apptDate, apptTime;
    private ImageView apptCompleteIcon;
    private CardView apptCard;

    private ProgressDialog loader;
    private RelativeLayout backgroundLoader;

    private int categoryRemoveFlag = 0;
    private int callCurrentState = 0;
    MyClinicGlobalClass globalClass;
    private ApiGetPostMethodCalls globalApiCall;
    private TelephonyManager telephonyManager;
    private PhoneStateListener callStateListener;
    private ActivityResultLauncher<String> requestPermissionLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_profile);

        Intent intent = getIntent();
        patientName = intent.getStringExtra("patientName");
        String patientAge = intent.getStringExtra("age");
        int patientGender = intent.getIntExtra("gender", 0);
        patientPhone = intent.getStringExtra("patientPhoneNumber");
        patientId = intent.getIntExtra("patientId", 0);


        Bundle b = getIntent().getExtras();
        String categoryArray = b.getString("categoryArray");
        Toolbar toolbar = findViewById(R.id.patientProToolBar);
        toolbar.setTitle(patientName);
        setSupportActionBar(toolbar);
        doctorSaveCategoryList = new ArrayList<AppointmentApptListModel>();
        doctorCategoryList = new ArrayList<AppointmentApptListModel>();
        appUtilities = new AppUtilities();
        globalApiCall = new ApiGetPostMethodCalls();
        appPreference = getSharedPreferences(ApiUrls.appSharedPref, 0);
        globalClass = (MyClinicGlobalClass) getApplicationContext();
        mainLayout = findViewById(R.id.patientProMainLayout);
        patientProCat = findViewById(R.id.patientProCat);
        patientProCatEdit = findViewById(R.id.patientProCatEdit);
        patientProDiag = findViewById(R.id.patientProDiag);
        patientProDiagView = findViewById(R.id.patientProDiagView);
        familyRecycleView = findViewById(R.id.patientProFamRv);
        familyEmptyText = findViewById(R.id.patientProFamInfoEmptyText);
        familyModels = new ArrayList<>();
        familyAdapter = new PatientRecordsAboutAdapter(familyModels, this);
        patientRecordsApi = new PatientRecordsApi();
        savePatientData = findViewById(R.id.patientProPatientInfoSave);

        //patient info
        patientInfoLayout = findViewById(R.id.patientProPatientInfoFormCard);
        patientInfoMore = findViewById(R.id.patientProBgMore);
        pName = findViewById(R.id.patientProPatientInfoPName);
        pPhNo = findViewById(R.id.patientProPatientInfoPPhNo);
        pEmail = findViewById(R.id.patientProPatientInfoPEmail);
        pDOB = findViewById(R.id.patientProPatientInfoPDOB);
        pId = findViewById(R.id.patientProPatientInfoPID);
        pHeight = findViewById(R.id.patientProPatientInfoPHeight);
        pAge = findViewById(R.id.patientProPatientInfoPAge);
        pBloodG = findViewById(R.id.patientProPatientInfoPBloodG);
        pGender = findViewById(R.id.patientProPatientInfoPGender);
        pEmptyText = findViewById(R.id.patientProBgEmptyText);
        requestPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                result -> {
                    if (result) {
                        // PERMISSION GRANTED
                        telephonyManager.listen(callStateListener, PhoneStateListener.LISTEN_CALL_STATE);
                    } else {
                        new ShowAlertDialog().showPopupToMovePermissionPage(PatientProfileActivity.this);
                    }
                }
        );

        pName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View arg0, boolean hasfocus) {
                if (hasfocus) {
                    savePatientData.setEnabled(true);
                    savePatientData.setBackgroundTintList(getResources().getColorStateList(R.color.colorAccent));
                }
            }
        });
        pPhNo.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View arg0, boolean hasfocus) {
                if (hasfocus) {
                    savePatientData.setEnabled(true);
                    savePatientData.setBackgroundTintList(getResources().getColorStateList(R.color.colorAccent));
                }
            }
        });
        pEmail.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View arg0, boolean hasfocus) {
                if (hasfocus) {
                    savePatientData.setEnabled(true);
                    savePatientData.setBackgroundTintList(getResources().getColorStateList(R.color.colorAccent));
                }
            }
        });
        pDOB.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View arg0, boolean hasfocus) {
                if (hasfocus) {
                    savePatientData.setEnabled(true);
                    savePatientData.setBackgroundTintList(getResources().getColorStateList(R.color.colorAccent));
                }
            }
        });
        pId.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View arg0, boolean hasfocus) {
                if (hasfocus) {
                    savePatientData.setEnabled(true);
                    savePatientData.setBackgroundTintList(getResources().getColorStateList(R.color.colorAccent));
                }
            }
        });
        pHeight.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View arg0, boolean hasfocus) {
                if (hasfocus) {
                    savePatientData.setEnabled(true);
                    savePatientData.setBackgroundTintList(getResources().getColorStateList(R.color.colorAccent));
                }
            }
        });

        pAge.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View arg0, boolean hasfocus) {
                if (hasfocus) {
                    savePatientData.setEnabled(true);
                    savePatientData.setBackgroundTintList(getResources().getColorStateList(R.color.colorAccent));
                }
            }
        });


        pBloodG.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if (bloodGroupFlag == 1) {
                    savePatientData.setEnabled(true);
                    savePatientData.setBackgroundTintList(getResources().getColorStateList(R.color.colorAccent));
                } else {
                    bloodGroupFlag = 1;
                }

            } // to close the onItemSelected

            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        savePatientData.setEnabled(false);
        savePatientData.setBackgroundTintList(getResources().getColorStateList(R.color.colorGrey1));


        //add family
        familyForm = findViewById(R.id.patientProAddFamForm);
        familyName = findViewById(R.id.patientProFamName);
        familyAge = findViewById(R.id.patientProFamAge);
        familyRelation = findViewById(R.id.patientProFamRelation);
        problemCheckBoxes = findViewById(R.id.patientProProblemCheckBox);
        familyCloseForm = findViewById(R.id.patientProFamClose);
        addFamily = findViewById(R.id.patientProAddFamily);
        saveFamilyData = findViewById(R.id.patientProSaveFamily);
        problemSelected = new ArrayList<>();

        //records
        recordsViewAll = findViewById(R.id.patientProRecordViewAll);
        recordCaseName = findViewById(R.id.patientProRecordCaseName);
        recordsLayout = findViewById(R.id.patientProRecordsLayout);
        recordNoOfInteraction = findViewById(R.id.patientProRecordInteraction);
        recordDiagnosis = findViewById(R.id.patientProRecordDiag);
        recordView = findViewById(R.id.patientProRecordView);
        recordsEmptyText = findViewById(R.id.patientProRecordsEmptyText);
        backgroundLoader = findViewById(R.id.backgroundLoader);
        recordsCard = findViewById(R.id.patientProRecordsCard);

        //appt data
        apptLayout = findViewById(R.id.patientProApptLayout);
        apptName = findViewById(R.id.patientProApptName);
        apptDateTime = findViewById(R.id.patientProApptDate);
        apptPayment = findViewById(R.id.patientProApptPay);
        apptType = findViewById(R.id.patientProApptType);
        apptCancel = findViewById(R.id.patientProApptCancel);
        apptCompleted = findViewById(R.id.patientProApptComplete);
        apptNote = findViewById(R.id.patientProApptNotes);
        apptViewAll = findViewById(R.id.patientProApptViewAll);
        apptCompletedText = findViewById(R.id.patientProApptCompleteText);
        apptCompleteIcon = findViewById(R.id.patientProApptCompleteIcon);
        apptEmptyText = findViewById(R.id.patientProApptEmptyText);
        apptCard = findViewById(R.id.patientProApptCard);
        apptBook = findViewById(R.id.patientProApptBook);

        LinearLayoutManager horizontalLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        familyRecycleView.setLayoutManager(horizontalLayoutManager);
        familyRecycleView.setAdapter(familyAdapter);

        getFamilyDetails();
        getPatientDetails();
        getQuickProblemList();
        showGuide(1);

        ArrayAdapter aa = new ArrayAdapter(this, android.R.layout.simple_spinner_item, bloodType);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Setting the ArrayAdapter data on the Spinner
        pBloodG.setAdapter(aa);

        aa = new ArrayAdapter(this, android.R.layout.simple_spinner_item, relation);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Setting the ArrayAdapter data on the Spinner
        familyRelation.setAdapter(aa);

        pGender.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {

                if (genderFlag == 1) {
                    savePatientData.setEnabled(true);
                    savePatientData.setBackgroundTintList(getResources().getColorStateList(R.color.colorAccent));
                } else {
                    genderFlag = 1;
                }


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

                savePatientData.setEnabled(true);
                savePatientData.setBackgroundTintList(getResources().getColorStateList(R.color.colorAccent));

                if (pDOB.getText().equals("")) {
                    final Calendar c = Calendar.getInstance();
                    mYear = c.get(Calendar.YEAR);
                    mMonth = c.get(Calendar.MONTH);
                    mDay = c.get(Calendar.DAY_OF_MONTH);
                } else {
                    String date = appUtilities.changeDateFormat("dd MMM, yy", "dd MM yyyy", pDOB.getText().toString());
                    String[] dSplit = date.split(" ");
                    mDay = Integer.parseInt(dSplit[0]);
                    mMonth = Integer.parseInt(dSplit[1]) - 1;
                    mYear = Integer.parseInt(dSplit[2]);
                }

                DatePickerDialog datePickerDialog = new DatePickerDialog(PatientProfileActivity.this,
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

        savePatientData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                savePatientDetails();
            }
        });

        addFamily.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                familyForm.setVisibility(View.VISIBLE);
            }
        });

        familyCloseForm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                familyForm.setVisibility(View.GONE);

                familyRelation.setSelection(0);
                familyName.setText("");
                familyAge.setText("");

                for (int i = 0; i < problemCheckBoxes.getChildCount(); i++) {
                    CheckBox cb = (CheckBox) problemCheckBoxes.getChildAt(i);
                    cb.setChecked(false);
                }

                problemSelected.clear();
            }
        });

        saveFamilyData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveFamilyData();
            }
        });

        recordsViewAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent1 = new Intent(PatientProfileActivity.this, PatientRecordActivity.class);
                intent1.putExtra("PatientId", patientId);
                intent1.putExtra("PatientName", patientName);
                startActivity(intent1);
            }
        });

        mListAdapter = new CategoryGridViewCustomAdapter(PatientProfileActivity.this, doctorSaveCategoryList, new CategoryGridViewClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                if (doctorSaveCategoryList.size() == 0) {
                    patientProCat.setText("Category: NA");
                } else {
//                    savePatientCategory(patientId);
                    removeItemCategory(position);//doctorQualificationList


                }
            }
        });


        try {
            StringBuilder sb = new StringBuilder();
            JSONArray appointmentAssignCategory = new JSONArray(categoryArray);
            //JSONObject jsonObj = new JSONObject("{\"phonetype\":\"N95\",\"cat\":\"WP\"}");
            if (appointmentAssignCategory.length() > 0) {
                for (int k = 0; k < appointmentAssignCategory.length(); k++) {
                    JSONObject categoryObject = appointmentAssignCategory.getJSONObject(k);
                    String str = categoryObject.getString("category_name");
                    sb.append(str);
                    sb.append(",");
                }
                sb.deleteCharAt(sb.length() - 1);
                assignCategoryData = "Category: \n" + sb.toString();
            } else {
                assignCategoryData = "Category: \nNA";
            }
        } catch (Exception e) {

        }

        patientProCat.setText(assignCategoryData);
//        if(patientGender ==1)
//        {
//            patientProBgGender.setText("Gender: Male");
//        }
//        else
//        {
//            patientProBgGender.setText("Gender: Female");
//
//        }
//
//        if(patientAge.equalsIgnoreCase(""))
//        {
//            patientProBgAge.setText("Age: ------");
//
//        }
//        else
//        {
//            patientProBgAge.setText("Age: "+patientAge);
//
//        }
//
//        patientProBgName.setText("Name: "+patientName);

        patientProCatEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //saveCategoryList = new ArrayList<>();
                getSavedCategory(patientId);
                getDoctorCategory();
                final ArrayAdapter<String> arrayadapter;
                AlertDialog.Builder builder = new AlertDialog.Builder(PatientProfileActivity.this);
                final LayoutInflater inflater = getLayoutInflater();
                final View inflator = inflater.inflate(R.layout.dailog_patien_category, null);
                final Spinner spinner = (Spinner) inflator.findViewById(R.id.paymentModeSpinner);
                final EditText userNameText = (EditText) inflator.findViewById(R.id.amountPaid);
                final ImageView dailogArticleCancel = (ImageView) inflator.findViewById(R.id.dailogArticleCancel);
                actv = (AutoCompleteTextView) inflator.findViewById(R.id.autoCompleteTextView);
                final Button categorySave = (Button) inflator.findViewById(R.id.categorySaveButton);

                gridview = (GridView) inflator.findViewById(R.id.gridView1);
                builder.setView(inflator)
                        .setPositiveButton("Save & close", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {

                                GetItem = actv.getText().toString();
                                int selectedCategoryId = 0;
                                for (int i = 0; i < doctorCategoryList.size(); i++) {
                                    if (GetItem.equalsIgnoreCase(doctorCategoryList.get(i).getDoctorCategoryName())) {
                                        selectedCategoryId = doctorCategoryList.get(i).getDoctorCategoryId();
                                    } else {

                                    }
                                }
                                if (GetItem.isEmpty()) {
                                    //Toast.makeText(activity, "Item Added SuccessFully", Toast.LENGTH_LONG).show();
                                } else {
                                    AppointmentApptListModel selected = new AppointmentApptListModel();
                                    selected.setCategoryName(GetItem);
                                    selected.setCategoryId(selectedCategoryId);
                                    doctorSaveCategoryList.add(doctorSaveCategoryList.size(), selected);
                                    mListAdapter.notifyDataSetChanged();
                                    //Toast.makeText(activity, "Item Added SuccessFully", Toast.LENGTH_LONG).show();
                                }

                                StringBuilder sb = new StringBuilder();
                                sb.append("Category: ");
                                for (int i = 0; i < CategoryGridViewCustomAdapter.result.size(); i++) {
                                    // JSONObject categoryObject = appointmentAssignCategory.getJSONObject(k);
                                    String str = CategoryGridViewCustomAdapter.result.get(i).getCategoryName();
                                    sb.append(str);
                                    sb.append(",");

                                }
                                sb.deleteCharAt(sb.length() - 1);
                                patientProCat.setText(sb.toString());


                                if (actv.getText().toString().equalsIgnoreCase("")) {
                                    Toast.makeText(PatientProfileActivity.this, "Please enter category name", Toast.LENGTH_LONG).show();

                                } else {
                                    savePatientCategory(patientId);
                                    actv.setText("");
                                    dialog.dismiss();
                                }
                                // otpLoading.dismiss();
                                // dialog.dismiss();
                            }
                        })
                        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                                actv.setText("");
                                //otpLoading.dismiss();
                            }
                        })
                ;
                final AlertDialog alertDialog = builder.create();
                dailogArticleCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        alertDialog.dismiss();
                        //otpLoading.dismiss();
                    }
                });

                categorySave.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        GetItem = actv.getText().toString();
                        int selectedCategoryId = 0;
                        for (int i = 0; i < doctorCategoryList.size(); i++) {
                            if (GetItem.equalsIgnoreCase(doctorCategoryList.get(i).getDoctorCategoryName())) {
                                selectedCategoryId = doctorCategoryList.get(i).getDoctorCategoryId();
                            } else {

                            }
                        }
                        if (GetItem.isEmpty()) {
                            //Toast.makeText(activity, "Item Added SuccessFully", Toast.LENGTH_LONG).show();
                        } else {
                            AppointmentApptListModel selected = new AppointmentApptListModel();
                            selected.setCategoryName(GetItem);
                            selected.setCategoryId(selectedCategoryId);
                            doctorSaveCategoryList.add(doctorSaveCategoryList.size(), selected);
                            mListAdapter.notifyDataSetChanged();
                            //Toast.makeText(activity, "Item Added SuccessFully", Toast.LENGTH_LONG).show();
                        }


                        StringBuilder sb = new StringBuilder();
                        sb.append("Category: ");
                        for (int i = 0; i < CategoryGridViewCustomAdapter.result.size(); i++) {
                            // JSONObject categoryObject = appointmentAssignCategory.getJSONObject(k);
                            String str = CategoryGridViewCustomAdapter.result.get(i).getCategoryName();
                            sb.append(str);
                            sb.append(",");

                        }
                        sb.deleteCharAt(sb.length() - 1);
                        patientProCat.setText(sb.toString());


//                        } else {
//                            assignCategoryData = "Category:NA";
//                        }


                        if (actv.getText().toString().equalsIgnoreCase("")) {
                            Toast.makeText(PatientProfileActivity.this, "Please enter category name", Toast.LENGTH_LONG).show();

                        } else {
                            savePatientCategory(patientId);
                            actv.setText("");
                        }

                    }
                });

                //Creating the instance of ArrayAdapter containing list of fruit names
                ArrayAdapter<AppointmentApptListModel> adapter = new ArrayAdapter<AppointmentApptListModel>
                        (PatientProfileActivity.this, android.R.layout.select_dialog_item, doctorCategoryList);
                //Getting the instance of AutoCompleteTextView
                actv.setThreshold(1);//will start working from first character
                actv.setAdapter(adapter);//setting the adapter data into the AutoCompleteTextView
                actv.setTextColor(Color.RED);
                alertDialog.show();

            }
        });

        patientInfoMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (patientInfoMore.getText().equals(getResources().getString(R.string.more))) {
                    RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    params.addRule(RelativeLayout.BELOW, R.id.patientProIcon1);
                    params.setMargins(20, 20, 20, 20);
                    patientInfoLayout.setLayoutParams(params);
                    patientInfoMore.setText(getResources().getString(R.string.less));
                } else {
                    RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 500);
                    params.addRule(RelativeLayout.BELOW, R.id.patientProIcon1);
                    params.setMargins(20, 20, 20, 20);
                    patientInfoLayout.setLayoutParams(params);
                    patientInfoMore.setText(getResources().getString(R.string.more));
                }
            }
        });

        //appt
        apptCompleted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (apptMode == 1) {
                    telephonyManager =
                            (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
                    callStateListener = new PhoneStateListener() {
                        public void onCallStateChanged(int state, String incomingNumber) {
                            if (state == TelephonyManager.CALL_STATE_RINGING) {
                                callCurrentState = 1;
                                Toast.makeText(getApplicationContext(), "You can't place a video call if you're already on a phone call.",
                                        Toast.LENGTH_LONG).show();
                            } else if (state == TelephonyManager.CALL_STATE_OFFHOOK) {
                                callCurrentState = 2;
                                Toast.makeText(getApplicationContext(), "You can't place a video call if you're already on a phone call.",
                                        Toast.LENGTH_LONG).show();
                            } else {
                                if (callCurrentState == 1 || callCurrentState == 2) {
                                    callCurrentState = 0;
                                } else {
                                    if (globalClass.isOnline()) {
                                        Intent intent = new Intent(PatientProfileActivity.this, JoinVideoActivity.class);
                                        intent.putExtra("AppointmentId", apptId);
                                        startActivity(intent);
                                    } else {
                                        globalClass.noInternetConnection.showDialog(PatientProfileActivity.this);
                                    }

                                }
                            }
                        }
                    };
                    if (Build.VERSION.SDK_INT >= 31) {
                        if (checkSelfPermission(android.Manifest.permission.READ_PHONE_STATE)
                                != PackageManager.PERMISSION_GRANTED) {
                            requestPermissionLauncher.launch(Manifest.permission.READ_PHONE_STATE);
                        } else {
                            telephonyManager.listen(callStateListener, PhoneStateListener.LISTEN_CALL_STATE);
                        }
                    } else {
                        telephonyManager.listen(callStateListener, PhoneStateListener.LISTEN_CALL_STATE);
                    }


                } else {
                    completedAppt(apptId, 3);
                }
            }
        });

        apptCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(PatientProfileActivity.this);
                builder.setTitle("Cancel Confirm");
                builder.setMessage("Do you want cancel appointment?");
                builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        cancelAppt(apptId, 8);
                        dialog.dismiss();
                    }
                });
                builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Do nothing
                        dialog.dismiss();
                    }
                });
                android.app.AlertDialog alert = builder.create();
                alert.show();


                // cancelAppt(apptId, 8);
            }
        });

        apptNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PatientProfileActivity.this, PatientRecordsCaseActivity.class);
                intent.putExtra("ApptId", apptId);
                intent.putExtra("PatientId", patientId);
                intent.putExtra("ApptMode", apptMode);
                intent.putExtra("ApptDate", apptDate);
                intent.putExtra("ApptTime", apptTime);
                startActivity(intent);
            }
        });

        apptPayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!apptPayment.getText().toString().equalsIgnoreCase("received")) {
                    paymentDialog();
                }
            }
        });

        apptType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog2(PatientProfileActivity.this, apptId);
            }
        });

        apptViewAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

//                AppointmentFragment newGamefragment = new AppointmentFragment();
//                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
//                fragmentTransaction.replace(R.id.fragmentContainer, newGamefragment);
//                fragmentTransaction.addToBackStack(null);
//                fragmentTransaction.commit();
//                MainActivity.bottomNavigationView.getMenu().getItem(2).setChecked(true);

//                AppointmentFragment fragment = new AppointmentFragment();
//                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
//                transaction.replace(R.id.fragmentContainer, fragment);
//                transaction.commit();


            }
        });

        apptBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        //records
        recordView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PatientProfileActivity.this, PatientEpisodeActivity.class);
                intent.putExtra("EpisodeId", episodeId);
                intent.putExtra("PatientId", patientId);
                startActivity(intent);
            }
        });

    }

    @Override
    public void onBackPressed() {
        if (familyForm.getVisibility() == View.VISIBLE) {
            familyForm.setVisibility(View.GONE);
        } else {
            finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Do something that differs the Activity's menu here
        getMenuInflater().inflate(R.menu.patient_profile_menu, menu);

        Drawable icon = menu.findItem(R.id.patientProMenuCall).getIcon();
        icon.setColorFilter(getResources().getColor(R.color.colorWhite), PorterDuff.Mode.SRC_IN);
        menu.findItem(R.id.patientProMenuCall).setIcon(icon);


        menu.findItem(R.id.patientProMenuCall).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {

                AlertDialog.Builder builder = new AlertDialog.Builder(PatientProfileActivity.this);
                builder.setTitle("Call Confirm");
                builder.setMessage("Are you sure?");
                builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        onCall(patientPhone);
                        dialog.dismiss();
                    }
                });
                builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Do nothing
                        dialog.dismiss();
                    }
                });
                AlertDialog alert = builder.create();
                alert.show();
                return false;
            }
        });


        return true;
    }


    public void getSavedCategory(int orderUserId) {

//        RequestQueue requestQueue = Volley.newRequestQueue(this);
        final String URL = ApiUrls.getSavedCategory + "?p_id=" + orderUserId;

        globalApiCall.volleyApiRequestData(URL, Request.Method.GET, null, this, new VolleyCallback() {
            @Override
            public void onSuccess(String result) {
                try {
                    JSONObject response = new JSONObject(result);
                    //Process os success response
//                        Log.d("savedCategoryType", response.toString());
                    //otpLoading.dismiss();
                    doctorSaveCategoryList.clear();
                    //JSONObject rootObj = response.getJSONObject("response");
                    JSONArray categoryArray = response.getJSONArray("response");
                    for (int j = 0; j < categoryArray.length(); j++) {
                        JSONObject categoryObject = categoryArray.getJSONObject(j);
                        AppointmentApptListModel model = new AppointmentApptListModel();
                        model.setCategoryId(categoryObject.getInt("id"));
                        model.setCategoryName(categoryObject.getString("category_name"));
//                                Log.d("Cate name", categoryObject.getString("category_name"));
                        doctorSaveCategoryList.add(model);

                    }


                    gridview.setAdapter(mListAdapter);
                    mListAdapter.notifyDataSetChanged();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(String err) {
                ErrorHandlerClass.INSTANCE.errorHandler(PatientProfileActivity.this, err);
            }
        });

    }

    public void getDoctorCategory() {
        RequestQueue requestQueue = Volley.newRequestQueue(PatientProfileActivity.this);
        final String URL = ApiUrls.getDoctorCategory;

        globalApiCall.volleyApiRequestData(URL, Request.Method.GET, null, this, new VolleyCallback() {
            @Override
            public void onSuccess(String result) {
                try {
                    JSONObject response = new JSONObject(result);
                    //Process os success response
//                        Log.d("doctorCategoryType", response.toString());
                    // otpLoading.dismiss();
                    doctorCategoryList.clear();
                    //JSONObject rootObj = response.getJSONObject("response");
                    JSONArray categoryArray = response.getJSONArray("response");
                    for (int j = 0; j < categoryArray.length(); j++) {
                        JSONObject categoryObject = categoryArray.getJSONObject(j);
                        AppointmentApptListModel model = new AppointmentApptListModel();
                        model.setDoctorCategoryId(categoryObject.getInt("id"));
                        model.setDoctorCategoryName(categoryObject.getString("category_name"));
                        doctorCategoryList.add(model);

                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(String err) {
                ErrorHandlerClass.INSTANCE.errorHandler(PatientProfileActivity.this, err);
            }
        });

    }


    public void savePatientCategory(int patientId) {

        final String URL = ApiUrls.savePatientCategory;
        try {
            JSONArray categoryExistArray = new JSONArray();
            JSONArray categoryInsertArray = new JSONArray();


            for (int i = 0; i < doctorCategoryList.size(); i++) {

                if (GetItem != null) {
                    categoryInsertArray = new JSONArray();
                    categoryInsertArray.put(GetItem.toString());

                } else {
                    categoryInsertArray = new JSONArray();
                }

            }


            for (int j = 0; j < doctorSaveCategoryList.size(); j++) {
                JSONObject saveCategoryObject = new JSONObject();
                saveCategoryObject.put("id", doctorSaveCategoryList.get(j).getCategoryId());
                saveCategoryObject.put("doctor_id", ApiUrls.doctorId);
                saveCategoryObject.put("category_name", doctorSaveCategoryList.get(j).getCategoryName());
                categoryExistArray.put(saveCategoryObject);
            }


            jsonValue = new JSONObject();
            jsonValue.put("doctor_id", ApiUrls.doctorId);
            jsonValue.put("exists", categoryExistArray);
            jsonValue.put("insert", categoryInsertArray);
            jsonValue.put("p_id", patientId);

        } catch (Exception e) {
            e.getMessage();
        }


        globalApiCall.volleyApiRequestData(URL, Request.Method.GET, null, this, new VolleyCallback() {
            @Override
            public void onSuccess(String result) {
                try {
                    JSONObject resObj = new JSONObject(result);


                    if (categoryRemoveFlag == 1) {
                        Toast.makeText(PatientProfileActivity.this, "Removed successfully", Toast.LENGTH_LONG).show();

                    } else {
                        Toast.makeText(PatientProfileActivity.this, "Saved successfully", Toast.LENGTH_LONG).show();
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(String err) {
                ErrorHandlerClass.INSTANCE.errorHandler(PatientProfileActivity.this, err);
            }
        });

    }


    public void onCall(String phoneNumber) {
//        int permissionCheck = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CALL_PHONE);
//        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(
//                    getActivity(),
//                    new String[]{Manifest.permission.CALL_PHONE},
//                    123);
//            onCall(phoneNumber);
//
//        } else {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + phoneNumber));
        startActivity(intent);
//        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {

            case 123:
                if ((grantResults.length > 0) && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    onCall(patientPhone);
                } else {
//                    Log.d("TAG", "Call Permission Not Granted");
                }
                break;

            default:
                break;
        }
    }


    private void getFamilyDetails() {

        String url = ApiUrls.getFamilyList + "?doctor_id=" + ApiUrls.doctorId + "&patient_id=" + patientId + "&page=1&per_page=10&mode=all";

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

                            JSONArray probArr = familyObj.getJSONArray("problems");
                            String probStr = "";
                            StringBuilder sb = new StringBuilder();
                            for (int j = 0; j < probArr.length(); j++) {
                                //probStr = probArr.getJSONObject(j).getJSONObject("problem").getString("condition") + ",";
                                probStr = probArr.getJSONObject(j).getJSONObject("problem").getString("condition");
                                sb.append(probStr);
                                sb.append(",");
                            }
                            sb.deleteCharAt(sb.length() - 1);

                            model.setFamProblems(sb.toString());
                            //model.setFamProblems(probStr);

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
                ErrorHandlerClass.INSTANCE.errorHandler(PatientProfileActivity.this, err);
            }
        });
    }

    private void getPatientDetails() {

        recordsEmptyText.setVisibility(View.GONE);
        apptEmptyText.setVisibility(View.GONE);
        pEmptyText.setVisibility(View.GONE);

        recordsCard.setVisibility(View.VISIBLE);
        apptCard.setVisibility(View.VISIBLE);
        patientInfoLayout.setVisibility(View.VISIBLE);
        backgroundLoader.setVisibility(View.VISIBLE);

        String url = ApiUrls.getPatientBackground + "?patient_id=" + patientId;
//        Log.d("Url", url);
        patientRecordsApi.getRecordPref(url, "", this, new VolleyCallback() {
            @Override
            public void onSuccess(String result) {

                backgroundLoader.setVisibility(View.GONE);
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

                    if (!patientObj.getString("dob").equalsIgnoreCase("")) {
                        String date = appUtilities.changeDateFormat("yyyy-MM-dd", "dd MMM, yy", patientObj.getString("dob"));
                        pDOB.setText(date);
                    }


                    JSONObject dataObj = patientObj.getJSONObject("data");

                    Object intervention = dataObj.get("records");
                    if (intervention instanceof JSONArray) {
                        // It's an array
                        //records binding
                        JSONArray recordArr = dataObj.getJSONArray("records");
                        if (recordArr.length() > 0) {
                            JSONObject recObj = recordArr.getJSONObject(0);
                            recordCaseName.setText(recObj.getString("episode_name"));
                            recordNoOfInteraction.setText(recObj.getInt("encounterCount") + " Interaction(s)");
                            recordDiagnosis.setText("Diagnosis: " + recObj.getString("diagnosis_string"));
                            episodeId = recObj.getInt("id");
                        } else {
                            recordsLayout.setVisibility(View.GONE);
                        }


                    } else if (intervention instanceof JSONObject) {
                        // It's an object
                        JSONObject recordObject = dataObj.getJSONObject("records");


                    } else {
                        // It's something else, like a string or number
                        recordsLayout.setVisibility(View.GONE);

                    }


//                    //records binding
//                    JSONArray recordArr = dataObj.getJSONArray("records");
//                    if(recordArr.length() > 0) {
//                        JSONObject recObj = recordArr.getJSONObject(0);
//                        recordCaseName.setText(recObj.getString("episode_name"));
//                        recordNoOfInteraction.setText(recObj.getInt("encounterCount") + " Interaction(s)");
//                        recordDiagnosis.setText("Diagnosis: " + recObj.getString("diagnosis_string"));
//                        episodeId = recObj.getInt("id");
//                    } else {
//                        recordsLayout.setVisibility(View.GONE);
//                    }


                    //appt binding
//                    Log.d("Data Obj", dataObj.get("appointment").toString());
                    if (!dataObj.get("appointment").toString().equalsIgnoreCase("null")) {
                        JSONObject apptObj = dataObj.getJSONObject("appointment");
//                        Log.d("Appt Obj", apptObj.toString());
                        if (apptObj.getInt("mode") != 2) {
                            apptName.setText(apptObj.getJSONObject("order").getJSONObject("products").getJSONObject("prod_service").getString("alias"));
                            String temp = appUtilities.changeDateFormat("yyyy-MM-dd HH:mm:ss", "dd MMM, yy HH:mm", apptObj.getString("scheduled_start_time"));
                            apptDateTime.setText(temp);

                            if (apptObj.getJSONObject("order").getString("payment_status").equalsIgnoreCase("pending")) {
                                apptPayment.setTextColor(getResources().getColor(R.color.colorInfo));
                                apptPayment.setText(apptObj.getJSONObject("order").getString("payment_status"));
                            } else {
                                apptPayment.setTextColor(getResources().getColor(R.color.colorSuccess));
                                apptPayment.setText("Received");
                            }

                            apptId = apptObj.getInt("id");
                            apptMode = apptObj.getInt("mode");
                            temp = apptObj.getString("scheduled_start_time");
                            String[] split = temp.split(" ");
                            apptDate = split[0];
                            apptTime = split[1];
                            apptOrderId = apptObj.getJSONObject("order").getInt("id");
                            apptOrderAmt = apptObj.getJSONObject("order").getInt("net_amount");

                            if (apptObj.getInt("mode") == 1) {
                                apptCompletedText.setText("Join Video");
                                apptCompletedText.setTextColor(getResources().getColor(R.color.colorAccent));
                                apptCompleteIcon.setImageDrawable(getResources().getDrawable(R.drawable.ic_videocam));
                                apptCompleteIcon.setColorFilter(getResources().getColor(R.color.colorAccent), PorterDuff.Mode.SRC_IN);
                            }

                            if (apptObj.getInt("appt_type") == 1) {
                                apptType.setText("Routine");
                            } else if (apptObj.getInt("appt_type") == 2) {
                                apptType.setText("Follow-up");
                            } else if (apptObj.getInt("appt_type") == 3) {
                                apptType.setText("Procedure/Vaccination");
                            } else if (apptObj.getInt("appt_type") == 4) {
                                apptType.setText("Dressing/Plaster");
                            } else if (apptObj.getInt("appt_type") == 5) {
                                apptType.setText("Other");
                            } else if (apptObj.getInt("appt_type") == 6) {
                                apptType.setText("First Visit");
                            }
                        } else {
                            apptLayout.setVisibility(View.GONE);
                        }
                    } else {
                        apptLayout.setVisibility(View.GONE);
                    }

                } catch (Exception e) {
                    backgroundLoader.setVisibility(View.GONE);
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(String err) {
//                Log.d("Errorrrr", err);
                backgroundLoader.setVisibility(View.GONE);
                recordsEmptyText.setVisibility(View.VISIBLE);
                apptEmptyText.setVisibility(View.VISIBLE);
                pEmptyText.setVisibility(View.VISIBLE);

                recordsCard.setVisibility(View.GONE);
                apptCard.setVisibility(View.GONE);
                patientInfoLayout.setVisibility(View.INVISIBLE);

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
                Toast.makeText(PatientProfileActivity.this, getResources().getString(R.string.update_patient), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(String err) {
                ErrorHandlerClass.INSTANCE.errorHandler(PatientProfileActivity.this, err);
            }
        });
    }

    private void getQuickProblemList() {
        patientRecordsApi.getRecordPref(ApiUrls.consultQuickOption, "", this, new VolleyCallback() {
            @Override
            public void onSuccess(String result) {
                try {

//                    Log.d("Quick Problems", result);
                    JSONObject resObj = new JSONObject(result);
                    JSONArray dataArr = resObj.getJSONArray("response");
                    if (dataArr.length() > 0) {
                        for (int i = 0; i < dataArr.length(); i++) {
                            JSONObject problems = dataArr.getJSONObject(i);

                            final CheckBox cb = new CheckBox(PatientProfileActivity.this);
                            cb.setText(problems.getString("condition"));
                            cb.setId(problems.getInt("id"));

                            cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                @Override
                                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                                    if (b) {
                                        problemSelected.add(cb.getId());
                                    } else {
//                                        problemSelected.remove(cb.getId());
                                    }
                                }
                            });

                            problemCheckBoxes.addView(cb);
                        }
                    } else {

                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(String err) {
                ErrorHandlerClass.INSTANCE.errorHandler(PatientProfileActivity.this, err);

            }
        });
    }

    private void saveFamilyData() {

        if (!familyName.getText().toString().equalsIgnoreCase("") &&
                !familyAge.getText().toString().equalsIgnoreCase("") &&
                familyRelation.getSelectedItemPosition() != 0 &&
                problemSelected.size() > 0) {
            loadingDialog = new ProgressDialog(this);
            loadingDialog.setMessage(getResources().getString(R.string.sending_message));
            loadingDialog.setTitle(getResources().getString(R.string.sending_message));
            loadingDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            loadingDialog.setCancelable(false);
            loadingDialog.show();

            int[] probId = new int[problemSelected.size()];
            for (int i = 0; i < problemSelected.size(); i++) {
                probId[i] = problemSelected.get(i);
            }

            JSONObject famObj = new JSONObject();
            try {
                famObj.put("problemids", new JSONArray(Arrays.toString(probId)));
                famObj.put("doctor_id", ApiUrls.doctorId);
                famObj.put("patient_id", patientId);
                famObj.put("relationid", familyRelation.getSelectedItemPosition());
                famObj.put("relativename", familyName.getText().toString());
                famObj.put("age", familyAge.getText().toString());
            } catch (Exception e) {
                e.printStackTrace();
            }

//            Log.d("Data Obj", famObj.toString());

            patientRecordsApi.postRecords(ApiUrls.saveFamilyData, famObj, this, new VolleyCallback() {
                @Override
                public void onSuccess(String result) {
//                    Log.d("Fam success", result);
                    loadingDialog.dismiss();
                    Toast.makeText(PatientProfileActivity.this, getResources().getString(R.string.add_family_success), Toast.LENGTH_SHORT).show();
                    familyForm.setVisibility(View.GONE);

                    familyRelation.setSelection(0);
                    familyName.setText("");
                    familyAge.setText("");

                    for (int i = 0; i < problemCheckBoxes.getChildCount(); i++) {
                        CheckBox cb = (CheckBox) problemCheckBoxes.getChildAt(i);
                        cb.setChecked(false);
                    }

                    problemSelected.clear();

                    familyModels.clear();
                    getFamilyDetails();
                }

                @Override
                public void onError(String err) {
//                    Log.d("Fam sErr", err);
                    loadingDialog.dismiss();
                    ErrorHandlerClass.INSTANCE.errorHandler(PatientProfileActivity.this, err);
                }
            });
        } else {
            if (familyName.getText().toString().equalsIgnoreCase("")) {
                Toast.makeText(PatientProfileActivity.this, "Enter person's name", Toast.LENGTH_SHORT).show();
            } else if (familyAge.getText().toString().equalsIgnoreCase("")) {
                Toast.makeText(PatientProfileActivity.this, "Enter person's age", Toast.LENGTH_SHORT).show();
            } else if (familyRelation.getSelectedItemPosition() == 0) {
                Toast.makeText(PatientProfileActivity.this, "Please select a relation", Toast.LENGTH_SHORT).show();
            } else if (problemSelected.size() == 0) {
                Toast.makeText(PatientProfileActivity.this, "Please select a problem", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void cancelAppt(final int apptID, int status) {

        loader = new ProgressDialog(this);
        loader.setMessage(getResources().getString(R.string.process_request));
        loader.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        loader.setCancelable(false);
        loader.show();

        JSONObject reqObj = new JSONObject();
        try {
            reqObj.put("appId", apptID);
            reqObj.put("cancel", status);
        } catch (Exception e) {
            e.printStackTrace();
        }

        patientRecordsApi.postRecords(ApiUrls.cancelAppt, reqObj, this, new VolleyCallback() {
            @Override
            public void onSuccess(String result) {
                loader.dismiss();
                try {
                    JSONObject resObj = new JSONObject(result);
                    if (resObj.getString("response").equalsIgnoreCase("success")) {
                        Toast.makeText(PatientProfileActivity.this, getResources().getString(R.string.appt_cancel), Toast.LENGTH_SHORT).show();
                        apptLayout.setVisibility(View.GONE);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(String err) {
                loader.dismiss();
                ErrorHandlerClass.INSTANCE.errorHandler(PatientProfileActivity.this, err);
            }
        });
    }

    private void completedAppt(final int apptId, int status) {

        loader = new ProgressDialog(this);
        loader.setMessage(getResources().getString(R.string.process_request));
        loader.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        loader.setCancelable(false);
        loader.show();


        JSONObject reqObj = new JSONObject();
        try {
            reqObj.put("apptId", apptId);
            reqObj.put("status", status);
        } catch (Exception e) {
            e.printStackTrace();
        }

        patientRecordsApi.postRecords(ApiUrls.updateClinicAppt, reqObj, this, new VolleyCallback() {
            @Override
            public void onSuccess(String result) {
//                Log.d("Success", result);
                apptLayout.setVisibility(View.GONE);
                loader.dismiss();
            }

            @Override
            public void onError(String err) {
                loader.dismiss();
                ErrorHandlerClass.INSTANCE.errorHandler(PatientProfileActivity.this, err);

            }
        });

    }

    public void showDialog2(Activity activity, final int appointmentId) {

        final Dialog dialog = new Dialog(activity);
        // dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_summary_listview);

        ImageView cancelButtonDilog = (ImageView) dialog.findViewById(R.id.dailogArticleCancel);
        cancelButtonDilog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();
            }
        });

        String[] myImageNameList = new String[]{"First Visit", "Routine",
                "Follow-up", "Procedure/Vaccination"
                , "Dressing/Plaster"};
        ListView listView = (ListView) dialog.findViewById(R.id.listview);
        ArrayAdapter arrayAdapter = new ArrayAdapter(activity, R.layout.list_item_dashboard_summary, R.id.tv, myImageNameList);
        listView.setAdapter(arrayAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


                String selectedApptType = "";
                if (position == 0) {
                    selectedApptType = "First Visit";
                    apptId = 6;

                } else if (position == 1) {
                    selectedApptType = "Routine";
                    apptId = 1;

                } else if (position == 2) {
                    selectedApptType = "Follow-up";
                    apptId = 2;
                }
                if (position == 3) {
                    selectedApptType = "Procedure/Vaccination";
                    apptId = 3;
                }
                if (position == 4) {
                    selectedApptType = "Dressing/Plaster";
                    apptId = 4;
                }
                if (position == 5) {
                    selectedApptType = "Other";
                    apptId = 5;
                }

                apptType.setText(selectedApptType);
                updateAppointmentType(apptId, appointmentId);


                dialog.dismiss();
            }
        });

        dialog.show();
    }

    public void updateAppointmentType(int apptTypeId, int apppointmentId) {
        loader = new ProgressDialog(this);
        loader.setMessage("Processing your request...");
        loader.setTitle("Updating Appointment");
        loader.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        loader.setCancelable(false);
        loader.show();
//        RequestQueue requestQueue = Volley.newRequestQueue(this);
        final String URL = ApiUrls.updateAppointmentType;

        try {

            jsonValue = new JSONObject();
            jsonValue.put("id", apppointmentId);
            jsonValue.put("appt_type", apptTypeId);

        } catch (Exception e) {

        }

        globalApiCall.volleyApiRequestData(URL, Request.Method.POST, jsonValue, this, new VolleyCallback() {
            @Override
            public void onSuccess(String result) {
                try {
                    JSONObject resObj = new JSONObject(result);
                    //Process os success response
                    loader.dismiss();


                } catch (Exception e) {
                    loader.dismiss();
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(String err) {
                loader.dismiss();
                ErrorHandlerClass.INSTANCE.errorHandler(PatientProfileActivity.this, err);
            }
        });


    }

    private void paymentDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final LayoutInflater inflater = getLayoutInflater();
        final View inflator = inflater.inflate(R.layout.dailog_apointment_payment_status, null);
        final Spinner spinner = (Spinner) inflator.findViewById(R.id.paymentModeSpinner);
        final EditText orderAmount = (EditText) inflator.findViewById(R.id.amountPaid);
        final ImageView dailogArticleCancel = (ImageView) inflator.findViewById(R.id.dailogArticleCancel);
        orderAmount.setText("" + apptOrderAmt);


        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, getResources()
                .getStringArray(R.array.paymentTypelistArray));
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        builder.setView(inflator)
                .setPositiveButton(R.string.received, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        String paymentModespin = spinner.getSelectedItem().toString();
                        String paymentMode = "";

                        if (paymentModespin.equalsIgnoreCase("Cash")) {
                            paymentMode = "Cash";
                        } else if (paymentModespin.equalsIgnoreCase("Credit Card")) {
                            paymentMode = "CC";
                        }
                        if (paymentModespin.equalsIgnoreCase("Debit Card")) {
                            paymentMode = "DC";
                        }
                        if (paymentModespin.equalsIgnoreCase("Net Banking")) {
                            paymentMode = "Net Banking";
                        }

                        String amountPaid = orderAmount.getText().toString();
                        updatePaymentStatus(amountPaid, apptOrderId, paymentMode);

                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                })
        ;
        final AlertDialog alertDialog = builder.create();
        dailogArticleCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                alertDialog.dismiss();
            }
        });
        alertDialog.show();
    }

    public void updatePaymentStatus(String paidAmount, int orderId, String paymentMode) {
        loader = new ProgressDialog(this);
        loader.setMessage(getResources().getString(R.string.wait_while_we_updating));
        loader.setTitle(getResources().getString(R.string.updating));
        loader.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        loader.setCancelable(false);
        loader.show();
//        RequestQueue requestQueue = Volley.newRequestQueue(this);
        final String URL = ApiUrls.updatePaymentStatus;

        try {

            jsonValue = new JSONObject();
            jsonValue.put("order_net_amount", paidAmount);
            jsonValue.put("order_id", orderId);
            jsonValue.put("order_payment_mode", paymentMode);
            jsonValue.put("isGenerateReport", false);
        } catch (Exception e) {

        }

        globalApiCall.volleyApiRequestData(URL, Request.Method.POST, jsonValue, this, new VolleyCallback() {
            @Override
            public void onSuccess(String result) {
                try {
                    JSONObject resObj = new JSONObject(result);
                    //Process os success response
                    loader.dismiss();
                    Toast.makeText(PatientProfileActivity.this, "Payment Status Updated", Toast.LENGTH_SHORT).show();

                    // itemViewHolder.paymentText.setText("Recived");

                    apptPayment.setText("Received");
                    apptPayment.setTextColor(getResources().getColor(R.color.colorSuccess));


                } catch (Exception e) {
                    loader.dismiss();
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(String err) {
                loader.dismiss();
                ErrorHandlerClass.INSTANCE.errorHandler(PatientProfileActivity.this, err);
            }
        });

    }

    private void showGuide(int section) {
        switch (section) {
            case 1:
                if (!appPreference.getBoolean("PatientProfile", false)) {
//                    new GuideView.Builder(this)
//                            .setTitle("Patient Details")
//                            .setContentText("Take a look into your patient's profile and update it as per needed")
//                            .setTargetView(patientInfoLayout)
//                            .setDismissType(DismissType.outside)
//                            .setGuideListener(new GuideListener() {
//                                @Override
//                                public void onDismiss(View view) {
//                                    showGuide(2);
//                                    SharedPreferences.Editor editor = appPreference.edit();
//                                    editor.putBoolean("PatientProfile", true);
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
                            .setInfoText("Take a look into your patient's profile and update it as per needed")
                            .setShape(ShapeType.RECTANGLE)
                            .setTarget(patientInfoLayout)
                            .setUsageId("intro_patientInfoLayout") //THIS SHOULD BE UNIQUE ID
                            .setListener(new MaterialIntroListener() {
                                @Override
                                public void onUserClicked(String materialIntroViewId) {
                                    showGuide(2);
                                    SharedPreferences.Editor editor = appPreference.edit();
                                    editor.putBoolean("PatientProfile", true);
                                    editor.commit();

                                }
                            })
                            .show();


                }
                break;

            case 2:
//                new GuideView.Builder(this)
//                        .setTitle("Family Details")
//                        .setContentText("Take a look into patient's family details")
//                        .setTargetView(familyRecycleView)
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
                        .setInfoText("Take a look into patient's family details")
                        .setShape(ShapeType.RECTANGLE)
                        .setTarget(familyRecycleView)
                        .setUsageId("intro_familyRecycleView") //THIS SHOULD BE UNIQUE ID
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
//                        .setTitle("Add Family Data")
//                        .setContentText("Add a family details for your patients to keep more records")
//                        .setTargetView(addFamily)
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
                        .setInfoText("Add a family details for your patients to keep more records")
                        .setShape(ShapeType.CIRCLE)
                        .setTarget(addFamily)
                        .setUsageId("intro_addFamily") //THIS SHOULD BE UNIQUE ID
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
//                        .setTitle("Assign Category")
//                        .setContentText("Assign come categories to the patients like Diabetes etc")
//                        .setTargetView(patientProCatEdit)
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
                        .setInfoText("Assign categories to the patients like Diabetes etc")
                        .setShape(ShapeType.CIRCLE)
                        .setTarget(patientProCatEdit)
                        .setUsageId("intro_patientProCatEdit") //THIS SHOULD BE UNIQUE ID
                        .setListener(new MaterialIntroListener() {
                            @Override
                            public void onUserClicked(String materialIntroViewId) {
//                                showGuide(4);

                            }
                        })
                        .show();


                break;
        }
    }


    public void removeItemCategory(int position) {
        doctorSaveCategoryList.remove(position);
        //Imageid.remove(position);
        mListAdapter.notifyDataSetChanged();

        categoryRemoveFlag = 1;

//        String languageStr = "";
//        for (int i = 0; i < doctorSaveCategoryList.size(); i++) {
//            // JSONObject tempobj = specializationsArr.getJSONObject(i);
//            languageStr += doctorSaveCategoryList.get(i).getCategoryName();
//            if (i != doctorSaveCategoryList.size() - 1) {
//                languageStr += " | ";
//            }
//        }
//        profesionalLanguageText.setText(languageStr);
//
//        upDateProfesionalDetails();

        StringBuilder sb = new StringBuilder();
        sb.append("Category: ");
        for (int i = 0; i < doctorSaveCategoryList.size(); i++) {
            // JSONObject categoryObject = appointmentAssignCategory.getJSONObject(k);
            String str = doctorSaveCategoryList.get(i).getCategoryName();
            sb.append(str);
            sb.append(",");

        }
        sb.deleteCharAt(sb.length() - 1);
        patientProCat.setText(sb.toString());

        savePatientCategory(patientId);

    }


}
