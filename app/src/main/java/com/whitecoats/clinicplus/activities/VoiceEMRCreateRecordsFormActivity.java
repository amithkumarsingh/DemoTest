package com.whitecoats.clinicplus.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
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
import com.google.android.gms.common.util.IOUtils;
import com.whitecoats.clinicplus.MyClinicGlobalClass;
import com.whitecoats.clinicplus.R;
import com.whitecoats.clinicplus.adapters.EMRAllPrescriptionListAdapter;
import com.whitecoats.clinicplus.apis.ApiUrls;
import com.whitecoats.clinicplus.constants.AppConstants;
import com.whitecoats.clinicplus.models.EMRPrescriptionModel;
import com.whitecoats.clinicplus.models.VoiceEMRModel;
import com.whitecoats.clinicplus.utils.AppUtilities;
import com.whitecoats.clinicplus.utils.ErrorHandlerClass;
import com.whitecoats.clinicplus.utils.RecordSavingType;
import com.whitecoats.clinicplus.viewmodels.EMRConsultationNotesViewModel;
import com.whitecoats.clinicplus.viewmodels.EMRCreateRecordsFormViewModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class VoiceEMRCreateRecordsFormActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private RelativeLayout saveAndAddMoreLayout;

    private RecyclerView allPrescriptionRecyclerView;
    public List<EMRPrescriptionModel> allPrescriptionList;
    private EMRAllPrescriptionListAdapter allPrescriptionListAdapter;
    private RecyclerView.LayoutManager allPrescriptionLayoutManager;

    private AppUtilities appUtilities;
    private int patientId, episodeId, encounterId;
    private EMRConsultationNotesViewModel emrConsultationNotesViewModel;

    //symptom
    private ScrollView symptomForm;
    private EditText sympName, sympDesp;
    private TextView sympFirstReport;
    private Spinner sympStatus;
    String[] sympStatuArr = {"Ongoing", "Partially Subsided", "Completely Subsided"};
    private Button createRecordSymptomBack, createRecordSaveSymptomData, createRecordDaigBack, createRecordSaveDaigData, createRecordInvesBack, createRecordSaveinvesData;
    private int mYear, mMonth, mDay, mHour, mMinute;
    String temp;

    //invest
    private ScrollView investForm;
    private EditText investName, investParam, investValue, investNote;
    private RelativeLayout investUpload;
    private ImageView investigationImageView;

    //diag
    private ScrollView diagForm;
    private EditText diagName;
    private TextView diagPoisted, diagConfirm, diagConfirmLabel;
    private Spinner diagStatus;
    private LinearLayout diagValueLayout;
    private String[] diagStatusArr = {"Tentative", "Confirmed", "Ruled Out"};
    private List<String> diagNameArr;
    private List<Integer> diagIdArr;
    private boolean selectedFromAutocomplete = false;
    private ImageView diagValueLayoutClose;

    private boolean isPDFFile = false;
    private String[] permissionsRequired = new String[]{android.Manifest.permission.CAMERA};
    private static final int PERMISSION_CALLBACK_CONSTANT = 100;
    private String uploadImageResponse;
    private File pdfFile;
    private Uri fileUri;
    private ImageView noteFileImage;
    private String imageFilePath;
    private RelativeLayout handWrittenMainLayout, symptomMainLayout, investigationMainLayout, daignosisMainLayout;
    private String categoryName;
    private RelativeLayout uploadHandWrittenNotes, attachedImageLayout, saveAndAddMore;
    private TextView attachedImage, viewFile, saveAndClose, removeFile;
    private EditText handWrittenDescription;
    private String attachedFileName;
    private EMRCreateRecordsFormViewModel emrCreateRecordsFormViewModel;
    private ProgressDialog progressDialog, saveDialog;
    private MyClinicGlobalClass myClinicGlobalClass;
    private RelativeLayout removeAndViewLayout;
    private Boolean isInvestigationImageUpload = false;
    private TextView prescriptionText, prescriptionValidTillText, textPrescriptionText, prescriptionValidTillCard;
    private Spinner medicinePrescriptionSpinner, testPrescriptionSpinner;
    private ArrayAdapter prescriptionAdapter, testPrescriptionAdapter;
    private String validTillText;
    private int selectedPrescriptionText, selectedTestPresciption;
    private Calendar calendar;
    private String patientName;
    private String type;
    private int recordPosition;
    private String SymptomsEditRecord, diagnosisEditRecord, InvestigationEditRecord;
    private VoiceEMRModel emrObject;
    /* ENGG-3754 -- Continuation of Refactoring the deprecated function in Clinic+ App(Android) */
    private ActivityResultLauncher<Intent> launchOpenFileResults, launcherCameraResults;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        setContentView(R.layout.activity_e_m_r_create_records_form);
        toolbar = findViewById(R.id.emrAddingNotesToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        Drawable upArrow = AppUtilities.changeIconColor(
                getResources().getDrawable(R.drawable.ic_arrow_back, getTheme()), this, R.color.colorWhite);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);
        emrConsultationNotesViewModel = new ViewModelProvider(this).get(EMRConsultationNotesViewModel.class);
        emrConsultationNotesViewModel.init();
        Intent intent = getIntent();
        patientId = intent.getIntExtra("patientId", 0);
        episodeId = intent.getIntExtra("episodeId", 0);
        encounterId = intent.getIntExtra("encounterId", 0);
        categoryName = intent.getStringExtra("categoryName");
        patientName = intent.getStringExtra("patientName");
        recordPosition = intent.getIntExtra("recordPosition", 0);
        SymptomsEditRecord = intent.getStringExtra("SymptomsEditRecord");
        diagnosisEditRecord = intent.getStringExtra("diagnosisEditRecord");
        InvestigationEditRecord = intent.getStringExtra("InvestigationEditRecord");

        emrObject = (VoiceEMRModel) getIntent().getSerializableExtra("VoiceEMRModel");
        toolbar.setTitle(patientName);
        allPrescriptionList = new ArrayList<>();
        appUtilities = new AppUtilities();
        allPrescriptionLayoutManager = new LinearLayoutManager(getApplicationContext());

        emrCreateRecordsFormViewModel = new ViewModelProvider(this).get(EMRCreateRecordsFormViewModel.class);
        emrCreateRecordsFormViewModel.init();

        handWrittenMainLayout = findViewById(R.id.handWrittenMainLayout);
        symptomMainLayout = findViewById(R.id.symptomMainLayout);
        investigationMainLayout = findViewById(R.id.investigationMainLayout);
        daignosisMainLayout = findViewById(R.id.daignosisMainLayout);


        //symptom
        symptomForm = findViewById(R.id.recordsCreateSymptomForm);
        sympName = findViewById(R.id.createSymptomName);
        sympDesp = findViewById(R.id.createSymptomDesp);
        sympFirstReport = findViewById(R.id.createSympFirstReporton);
        sympStatus = findViewById(R.id.createSympStatus);
        createRecordSymptomBack = findViewById(R.id.createRecordSymptomBack);
        createRecordSaveSymptomData = findViewById(R.id.createRecordSaveSymptomData);


        //invest
        investForm = findViewById(R.id.recordCreateInvestForm);
        investName = findViewById(R.id.recordCreateInvestName);
        investParam = findViewById(R.id.recordCreateInvestParam);
        investValue = findViewById(R.id.recordCreateInvestParamValue);
        investNote = findViewById(R.id.recordCreateInvestNotes);
        investUpload = findViewById(R.id.recordCreateInvestUpload);
        createRecordInvesBack = findViewById(R.id.createRecordInvesBack);
        createRecordSaveinvesData = findViewById(R.id.createRecordSaveinvesData);
        investigationImageView = findViewById(R.id.investigationImageView);

        //diag
        diagForm = findViewById(R.id.recordCreateDiagForm);
        diagName = findViewById(R.id.recordCreateDiagName);
        diagConfirm = findViewById(R.id.recordCreateDiagConfirmed);
        diagPoisted = findViewById(R.id.recordCreateDiagPosited);
        diagStatus = findViewById(R.id.recordCreateDiagStatus);
        diagValueLayout = findViewById(R.id.recordCreateDiagValues);
        diagNameArr = new ArrayList<>();
        diagIdArr = new ArrayList<>();
        diagConfirmLabel = findViewById(R.id.recordCreateDiagConfirmLabel);
        diagValueLayoutClose = findViewById(R.id.recordCreateDiagValueClose);
        createRecordDaigBack = findViewById(R.id.createRecordDaigBack);
        createRecordSaveDaigData = findViewById(R.id.createRecordSaveDaigData);

        myClinicGlobalClass = (MyClinicGlobalClass) getApplicationContext();

        progressDialog = new ProgressDialog(VoiceEMRCreateRecordsFormActivity.this);
        progressDialog.setMessage("Please wait while file is opening...");

        saveDialog = new ProgressDialog(VoiceEMRCreateRecordsFormActivity.this);
        saveDialog.setMessage("Saving HandWritten Notes, Please wait...");

        // HandWritten Notes
        uploadHandWrittenNotes = findViewById(R.id.handwrittenNoteButtonLayout);
        attachedImageLayout = findViewById(R.id.attachedImageNameLayout);
        attachedImage = findViewById(R.id.attached_image_text);
        viewFile = findViewById(R.id.view_file);
        handWrittenDescription = findViewById(R.id.handwritten_description);
        saveAndClose = findViewById(R.id.save_and_close);
        removeFile = findViewById(R.id.remove_file);
        removeAndViewLayout = findViewById(R.id.attachedViewAndRemoveLayout);
        removeAndViewLayout.setVisibility(View.GONE);
        prescriptionText = findViewById(R.id.prescription_text);
        prescriptionValidTillText = findViewById(R.id.prescription_valid_till_text);
        textPrescriptionText = findViewById(R.id.test_prescription_text);
        medicinePrescriptionSpinner = findViewById(R.id.medicine_prescription_spinner);
        prescriptionValidTillCard = findViewById(R.id.prescription_valid_till_card);
        testPrescriptionSpinner = findViewById(R.id.test_prescription_spinner);
        prescriptionValidTillText.setVisibility(View.GONE);
        prescriptionValidTillCard.setVisibility(View.GONE);
        /* ENGG-3754 -- Continuation of Refactoring the deprecated function in Clinic+ App(Android) */
        //Start
        launcherCameraResults = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    //Request code 1
                    Intent data = result.getData();
                    int resultCode = result.getResultCode();
                    if (resultCode == Activity.RESULT_OK) {

                        //new
                        try {
                            ContentResolver contentResolver = getContentResolver();

                            // Use the content resolver to open camera taken image input stream through image uri.
                            attachedFileName = getFileName(fileUri);
                            InputStream inputStream = contentResolver.openInputStream(fileUri);

                            // Decode the image input stream to a bitmap use BitmapFactory.
                            Bitmap pictureBitmap = BitmapFactory.decodeStream(inputStream);
                            int nh = (int) (pictureBitmap.getHeight() * (720.0 / pictureBitmap.getWidth()));
                            Bitmap scaled = Bitmap.createScaledBitmap(pictureBitmap, 720, nh, true);
                            investigationImageView.setImageBitmap(scaled);
                            type = "image";
                            uploadImage("image");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
        launchOpenFileResults = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    //Request code 2
                    Intent data = result.getData();
                    int resultCode = result.getResultCode();
                    if (resultCode == Activity.RESULT_OK) {
                        Uri uri = data.getData();
                        String fileName = getFileName(uri);
                        attachedFileName = fileName;
                        if (fileName.endsWith(".jpg") || fileName.endsWith(".jpeg") || fileName.endsWith(".png") || fileName.endsWith(".pdf")) {
                            try {
                                if (fileName.contains("pdf")) {
                                    isPDFFile = true;
                                    uploadPDFFile(fileName, uri);
                                } else {
                                    Bitmap bitmapImage = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                                    int nh = (int) (bitmapImage.getHeight() * (720.0 / bitmapImage.getWidth()));
                                    Bitmap scaled = Bitmap.createScaledBitmap(bitmapImage, 720, nh, true);
                                    investigationImageView.setImageBitmap(scaled);
                                    type = "image";
                                    uploadImage("image");
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        } else {
                            Toast.makeText(VoiceEMRCreateRecordsFormActivity.this, "Please Upload .jpg or .png files only", Toast.LENGTH_LONG).show();
                        }
                    }
                });
        //End

        calendar = Calendar.getInstance();

        prescriptionAdapter =
                new ArrayAdapter<String>(VoiceEMRCreateRecordsFormActivity.this, android.R.layout.simple_spinner_dropdown_item, AppConstants.prescriptionDetails);
        prescriptionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        medicinePrescriptionSpinner.setAdapter(prescriptionAdapter);
        medicinePrescriptionSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (AppConstants.prescriptionDetails[i].equals("Yes")) {
                    selectedPrescriptionText = 1;
                    prescriptionValidTillText.setVisibility(View.VISIBLE);
                    prescriptionValidTillCard.setVisibility(View.VISIBLE);
                    calendar = Calendar.getInstance();
                    validTillText = appUtilities.changeDateFormat("yyyy-MM-dd", "dd MMM, yyyy", calendar.get(Calendar.YEAR) + "-" + (calendar.get(Calendar.MONTH) + 1) + "-" + calendar.get(Calendar.DAY_OF_MONTH));
                    prescriptionValidTillCard.setText(appUtilities.changeDateFormat("yyyy-MM-dd", "dd MMM, yyyy", calendar.get(Calendar.YEAR) + "-" + (calendar.get(Calendar.MONTH) + 1) + "-" + calendar.get(Calendar.DAY_OF_MONTH)));

                } else if (AppConstants.prescriptionDetails[i].equals("No")) {
                    selectedPrescriptionText = 0;
                    prescriptionValidTillText.setVisibility(View.GONE);
                    prescriptionValidTillCard.setVisibility(View.GONE);
                    validTillText = "";
                } else {
                    selectedPrescriptionText = 2;
                    prescriptionValidTillText.setVisibility(View.GONE);
                    prescriptionValidTillCard.setVisibility(View.GONE);
                    validTillText = "";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        testPrescriptionAdapter =
                new ArrayAdapter<String>(VoiceEMRCreateRecordsFormActivity.this, android.R.layout.simple_spinner_dropdown_item, AppConstants.testPrescriptionDetails);
        testPrescriptionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        testPrescriptionSpinner.setAdapter(testPrescriptionAdapter);
        testPrescriptionSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (AppConstants.testPrescriptionDetails[i].equals("Yes")) {
                    selectedTestPresciption = 1;
                } else if (AppConstants.testPrescriptionDetails[i].equals("No")) {
                    selectedTestPresciption = 0;
                } else {
                    selectedTestPresciption = 2;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        prescriptionValidTillCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(VoiceEMRCreateRecordsFormActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        String d, m;
                        if (dayOfMonth < 10) {
                            d = "0" + (dayOfMonth);
                        } else {
                            d = String.valueOf(dayOfMonth);
                        }
                        if (month < 10) {
                            m = "0" + (month + 1);
                        } else {
                            m = String.valueOf(month + 1);
                        }
                        validTillText = appUtilities.changeDateFormat("dd-MM-yyyy", "dd MMM, yyyy", d + "-" + m + "-" + year);

                        prescriptionValidTillCard.setText(appUtilities.changeDateFormat("dd-MM-yyyy", "dd MMM, yyyy", d + "-" + m + "-" + year));
                    }
                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.show();
            }
        });

        attachedImageLayout.setVisibility(View.GONE);

        uploadHandWrittenNotes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectMethodDialog();
            }
        });
        removeFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadImageResponse = "";
                removeAndViewLayout.setVisibility(View.GONE);
                attachedImageLayout.setVisibility(View.GONE);
                if (type.equalsIgnoreCase("image"))
                    Toast.makeText(VoiceEMRCreateRecordsFormActivity.this, "Image successfully removed", Toast.LENGTH_LONG).show();
                else
                    Toast.makeText(VoiceEMRCreateRecordsFormActivity.this, "Pdf successfully removed", Toast.LENGTH_LONG).show();

            }
        });
        viewFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressDialog.show();
                emrCreateRecordsFormViewModel.getImagePath(VoiceEMRCreateRecordsFormActivity.this, uploadImageResponse).observe(VoiceEMRCreateRecordsFormActivity.this, new Observer<String>() {
                    @Override
                    public void onChanged(String s) {
                        progressDialog.dismiss();
                        try {
                            JSONObject response = new JSONObject(s);
                            if (response.getInt("status_code") == 200) {
                                String completeUrl = response.getJSONObject("response").getString("response");
                                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(completeUrl));
// Note the Chooser below. If no applications match,
// Android displays a system message.So here there is no need for try-catch.
                                startActivity(Intent.createChooser(intent, "Browse with"));
                            } else {
                                ErrorHandlerClass.INSTANCE.errorHandler(VoiceEMRCreateRecordsFormActivity.this, s);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });

            }
        });

        saveAndClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveHandWrittenNotes(VoiceEMRCreateRecordsFormActivity.this, ApiUrls.doctorId, encounterId, episodeId, handWrittenDescription.getText().toString().trim(), uploadImageResponse, patientId, "saveandclose", selectedPrescriptionText, selectedTestPresciption, validTillText);
            }
        });


        if (categoryName.equalsIgnoreCase("Symptoms")) {
            handWrittenMainLayout.setVisibility(View.GONE);
            symptomMainLayout.setVisibility(View.VISIBLE);
            //added by dileep
            if (SymptomsEditRecord.equalsIgnoreCase("SymptomsEditRecord")) {
                sympName.setText(emrObject.getSymptomName());
                sympDesp.setText(emrObject.getSymptomDescription());
                if (emrObject.getSymptomStatus().equalsIgnoreCase("Ongoing")) {
                    sympStatus.setSelection(0);
                } else if (emrObject.getSymptomStatus().equalsIgnoreCase("Partially Subsided")) {
                    sympStatus.setSelection(1);

                } else if (emrObject.getSymptomStatus().equalsIgnoreCase("Completely Subsided")) {
                    sympStatus.setSelection(2);
                }
                sympFirstReport.setText(emrObject.getSymptomDescription());
            }

            investigationMainLayout.setVisibility(View.GONE);
            daignosisMainLayout.setVisibility(View.GONE);
        } else if (categoryName.equalsIgnoreCase("Investigation Results")) {
            handWrittenMainLayout.setVisibility(View.GONE);
            symptomMainLayout.setVisibility(View.GONE);
            investigationMainLayout.setVisibility(View.VISIBLE);

//            VoiceEMRModel ve = new VoiceEMRModel();
//            ve.setCategoryName("Investigation Results");
//            ve.setInvestigationInvestigation_name(investName.getText().toString());
//            ve.setInvestigationValue(investValue.getText().toString());
//            ve.setInvestigationNotes(investNote.getText().toString());
//            ve.setInvestigationParameter(investParam.getText().toString());
//            ve.setInvestigationFile_url(uploadImageResponse);
//            ve.setInvestigatioFfile_type("image");
//
//            if (VoiceEMRActivity.voiceEMRModelsInvestigationCategoryList.size() > 0) {
//                ve.setCategoryNameInvestigationResultsExistType(2);
//            } else {
//                ve.setCategoryNameInvestigationResultsExistType(1);
//            }
//
//            int subCatogorySerNumber = VoiceEMRActivity.voiceEMRModelsInvestigationCategoryList.size() + 1;
//            ve.setSubCategorySerNumber(subCatogorySerNumber + "");
//            VoiceEMRActivity.voiceEMRModelsInvestigationCategoryList.add(ve);


            //added by dileep
            if (InvestigationEditRecord.equalsIgnoreCase("InvestigationEditRecord")) {
                investName.setText(emrObject.getInvestigationInvestigation_name());
                investValue.setText(emrObject.getInvestigationValue());
                investNote.setText(emrObject.getInvestigationNotes());
                investParam.setText(emrObject.getInvestigationParameter());

//                diagPoisted.setText(VoiceEMRActivity.voiceEMRModelsInvestigationCategoryList.get(recordPosition).getDiagnosisPosited_on());
            }


            daignosisMainLayout.setVisibility(View.GONE);
        } else if (categoryName.equalsIgnoreCase("Diagnosis")) {
            handWrittenMainLayout.setVisibility(View.GONE);
            symptomMainLayout.setVisibility(View.GONE);
            investigationMainLayout.setVisibility(View.GONE);
            daignosisMainLayout.setVisibility(View.VISIBLE);

            //added by dileep
            if (diagnosisEditRecord.equalsIgnoreCase("diagnosisEditRecord")) {
                diagName.setText(emrObject.getDiagnosisDiagnosis());
                if (emrObject.getDiagnosisStatus().equalsIgnoreCase("Tentative")) {
                    diagStatus.setSelection(0);
                } else if (emrObject.getDiagnosisStatus().equalsIgnoreCase("Confirmed")) {
                    diagStatus.setSelection(1);
                } else if (emrObject.getDiagnosisStatus().equalsIgnoreCase("Ruled Out")) {
                    diagStatus.setSelection(2);
                }
                diagPoisted.setText(emrObject.getDiagnosisPosited_on());
            }
            diagValueLayout.setVisibility(View.GONE);
            getDocDiagnoses();
        } else {
            handWrittenMainLayout.setVisibility(View.VISIBLE);
            symptomMainLayout.setVisibility(View.GONE);
            investigationMainLayout.setVisibility(View.GONE);
            daignosisMainLayout.setVisibility(View.GONE);
        }


        Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);
        temp = mDay + " " + (mMonth + 1) + " " + mYear;
        temp = appUtilities.changeDateFormat("dd MM yyyy", "dd MMM, yy", temp);
        if (emrObject.getSymptomFirstReportedOn() != null) {
            sympFirstReport.setText(emrObject.getSymptomFirstReportedOn());
        } else {
            sympFirstReport.setText(temp);
        }
        sympFirstReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDateDialog(temp, sympFirstReport);
            }
        });

        ArrayAdapter aa = new ArrayAdapter(this, android.R.layout.simple_spinner_item, sympStatuArr);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Setting the ArrayAdapter data on the Spinner
        sympStatus.setAdapter(aa);
        if (emrObject.getSymptomStatus() != null) {
            if (emrObject.getSymptomStatus().equalsIgnoreCase("Ongoing")) {
                sympStatus.setSelection(0);
            } else if (emrObject.getSymptomStatus().equalsIgnoreCase("Partially Subsided")) {
                sympStatus.setSelection(1);

            } else if (emrObject.getSymptomStatus().equalsIgnoreCase("Completely Subsided")) {
                sympStatus.setSelection(2);
            }
        }


        aa = new ArrayAdapter(this, android.R.layout.simple_spinner_item, diagStatusArr);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Setting the ArrayAdapter data on the Spinner
        diagStatus.setAdapter(aa);
        if (emrObject.getDiagnosisStatus() != null) {
            if (emrObject.getDiagnosisStatus().equalsIgnoreCase("Tentative")) {
                diagStatus.setSelection(0);
            } else if (emrObject.getDiagnosisStatus().equalsIgnoreCase("Confirmed")) {
                diagStatus.setSelection(1);
            } else if (emrObject.getDiagnosisStatus().equalsIgnoreCase("Ruled Out")) {
                diagStatus.setSelection(2);
            }
        }

        c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);
        temp = mDay + " " + (mMonth + 1) + " " + mYear;
        temp = appUtilities.changeDateFormat("dd MM yyyy", "dd MMM, yy", temp);
        if (emrObject.getDiagnosisPosited_on() != null) {
            diagPoisted.setText(emrObject.getDiagnosisPosited_on());
        } else {
            diagPoisted.setText(temp);
        }
        diagPoisted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDateDialog(temp, diagPoisted);
            }
        });

        c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);
        temp = mDay + " " + (mMonth + 1) + " " + mYear;
        temp = appUtilities.changeDateFormat("dd MM yyyy", "dd MMM, yy", temp);
        if (emrObject.getDiagnosisConfirmed_ruledout_on() != null) {
            diagConfirm.setText(emrObject.getDiagnosisConfirmed_ruledout_on());
        } else {
            diagConfirm.setText(temp);
        }
        diagConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDateDialog(temp, diagConfirm);
            }
        });

        diagStatus.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    diagConfirm.setVisibility(View.GONE);
                    diagConfirmLabel.setVisibility(View.GONE);
                } else {
                    diagConfirm.setVisibility(View.VISIBLE);
                    diagConfirmLabel.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        diagName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                Log.d("Value ADDed", s + "");
                if (s.length() == 0) {
                    diagValueLayout.setVisibility(View.GONE);
                } else {
                    diagValueLayout.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        diagValueLayoutClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                diagValueLayout.setVisibility(View.GONE);
            }
        });

        createRecordSaveSymptomData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    if (encounterId != 0 && !sympName.getText().toString().equalsIgnoreCase("")) {
                        JSONObject symObj = new JSONObject();
                        symObj.put("symptom_name", sympName.getText());
                        String temp = appUtilities.changeDateFormat("dd MMM, yy", "dd-MM-yyyy", sympFirstReport.getText().toString());
                        symObj.put("first_reported_on", temp);
                        symObj.put("symptom_description", sympDesp.getText().toString());
                        symObj.put("symptom_status", sympStatus.getSelectedItem());
                        symObj.put("patient_id", patientId);
                        symObj.put("episode_id", episodeId);
                        symObj.put("encounter_id", encounterId);


                        //added by dileep
                        if (SymptomsEditRecord.equalsIgnoreCase("SymptomsEditRecord")) {
                            emrObject.setCategoryName("Symptoms");
                            emrObject.setSymptomName(sympName.getText().toString());
                            emrObject.setSymptomDescription(sympDesp.getText().toString());
                            String tempSymptoms = appUtilities.changeDateFormat("dd MMM, yy", "dd-MM-yyyy", sympFirstReport.getText().toString());
                            emrObject.setSymptomFirstReportedOn(tempSymptoms);
                           /* if (sympStatus.getSelectedItem().toString().equalsIgnoreCase("Ongoing")) {
                                sympStatus.setSelection(0);
                            } else if (sympStatus.getSelectedItem().toString().equalsIgnoreCase("Partially Subsided")) {
                                sympStatus.setSelection(1);
                            } else if (sympStatus.getSelectedItem().toString().equalsIgnoreCase("Completely Subsided")) {
                                sympStatus.setSelection(2);
                            }*/
                            emrObject.setSymptomStatus(sympStatus.getSelectedItem().toString());
                            Intent intent = new Intent();
                            intent.putExtra("VoiceEMRModel", emrObject);
                            intent.putExtra("ActionType", "edit");
                            setResult(100, intent);
                            finish();
                        } else {
                            VoiceEMRModel ve = new VoiceEMRModel();
                            ve.setCategoryName("Symptoms");
                            ve.setSymptomName(sympName.getText().toString());
                            ve.setSymptomDescription(sympDesp.getText().toString());
                            ve.setSymptomFirstReportedOn(temp);
                            ve.setSymptomStatus(sympStatus.getSelectedItem().toString());
                            Long tsLong = System.currentTimeMillis() / 1000;
                            String timeStamp = tsLong.toString();
                            ve.setSymptomData_id(timeStamp);
                           /* if (VoiceEMRActivity.voiceEMRModelsCategoryList.size() > 0) {
                                ve.setCategoryNameSymptomExistType(2);
                            } else {
                                ve.setCategoryNameSymptomExistType(1);
                            }
                            int subCatogorySerNumber = VoiceEMRActivity.voiceEMRModelsCategoryList.size() + 1;
                            ve.setSubCategorySerNumber(subCatogorySerNumber + "");
                            VoiceEMRActivity.voiceEMRModelsCategoryList.add(ve);*/
                            Intent intent = new Intent();
                            intent.putExtra("VoiceEMRModel", ve);
                            intent.putExtra("ActionType", "add");
                            setResult(100, intent);
                            finish();
                            finish();
                        }

//                        saveSymptom(symObj);
                    } else {
                        if (encounterId == 0) {
                            Toast.makeText(VoiceEMRCreateRecordsFormActivity.this, "Please select interaction", Toast.LENGTH_SHORT).show();
                        } else if (sympName.getText().toString().equalsIgnoreCase("")) {
                            Toast.makeText(VoiceEMRCreateRecordsFormActivity.this, "Please enter symptom name", Toast.LENGTH_SHORT).show();
                        }


                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        createRecordSaveDaigData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try {
                    if (encounterId != 0 && !diagName.getText().toString().equalsIgnoreCase("")) {
                        JSONObject diagObj = new JSONObject();
                        diagObj.put("patient_id", patientId);
                        diagObj.put("episode_id", episodeId);
                        diagObj.put("encounter_id", encounterId);
                        diagObj.put("diagnosis", diagName.getText().toString());
                        diagObj.put("status", diagStatus.getSelectedItem().toString());
                        String temp = appUtilities.changeDateFormat("dd MMM, yy", "dd-MM-yyyy", diagPoisted.getText().toString());
                        diagObj.put("posited_on", temp);

                        diagObj.put("confirmed_ruledout_on", "");
                        if (diagConfirm.getVisibility() == View.VISIBLE) {
                            temp = appUtilities.changeDateFormat("dd MMM, yy", "dd-MM-yyyy", diagConfirm.getText().toString());
                            diagObj.put("confirmed_ruledout_on", temp);
                        }
                        diagObj.put("selectedFromAutocomplete", selectedFromAutocomplete);


                        //added by dileep

                        if (diagnosisEditRecord.equalsIgnoreCase("diagnosisEditRecord")) {
                            emrObject.setCategoryName("Diagnosis");
                            emrObject.setDiagnosisDiagnosis(diagName.getText().toString());
                            String tempDignosis = appUtilities.changeDateFormat("dd MMM, yy", "dd-MM-yyyy", diagPoisted.getText().toString());
                            String tempDignosisconformed = appUtilities.changeDateFormat("dd MMM, yy", "dd-MM-yyyy", diagConfirm.getText().toString());
                            emrObject.setDiagnosisPosited_on(tempDignosis);
                            emrObject.setDiagnosisConfirmed_ruledout_on(tempDignosisconformed);
                            /*if (emrObject.getDiagnosisStatus().equalsIgnoreCase("Tentative")) {
                                diagStatus.setSelection(0);
                            } else if (emrObject.getDiagnosisStatus().equalsIgnoreCase("Confirmed")) {
                                diagStatus.setSelection(1);
                            } else if (emrObject.getDiagnosisStatus().equalsIgnoreCase("Ruled Out")) {
                                diagStatus.setSelection(2);
                            }*/
                            emrObject.setDiagnosisStatus(diagStatus.getSelectedItem().toString());

                            Intent intent = new Intent();
                            intent.putExtra("VoiceEMRModel", emrObject);
                            intent.putExtra("ActionType", "edit");
                            setResult(100, intent);
                            finish();
                        } else {

                            VoiceEMRModel ve = new VoiceEMRModel();
                            ve.setCategoryName("Diagnosis");
                            ve.setDiagnosisDiagnosis(diagName.getText().toString());
                            ve.setDiagnosisStatus(diagStatus.getSelectedItem().toString());
                            String temp1 = appUtilities.changeDateFormat("dd MMM, yy", "dd-MM-yyyy", diagPoisted.getText().toString());
                            ve.setDiagnosisPosited_on(temp1);
                            if (diagConfirm.getVisibility() == View.VISIBLE) {
                                temp = appUtilities.changeDateFormat("dd MMM, yy", "dd-MM-yyyy", diagConfirm.getText().toString());
                                diagObj.put("confirmed_ruledout_on", temp);
                                ve.setDiagnosisConfirmed_ruledout_on(temp);
                            } else {
                                ve.setDiagnosisConfirmed_ruledout_on("");

                            }
                            Long tsLong = System.currentTimeMillis() / 1000;
                            String timeStamp = tsLong.toString();
                            ve.setDiagnosisData_id(timeStamp);
                            ve.setDiagnosisSelectedFromAutocomplete(selectedFromAutocomplete);

                           /* if (VoiceEMRActivity.voiceEMRModelsDiagnosisCategoryList.size() > 0) {
                                ve.setCategoryNameDiagnosisExistType(2);
                            } else {
                                ve.setCategoryNameDiagnosisExistType(1);
                            }

                            int subCatogorySerNumber = VoiceEMRActivity.voiceEMRModelsDiagnosisCategoryList.size() + 1;
                            ve.setSubCategorySerNumber(subCatogorySerNumber + "");
                            VoiceEMRActivity.voiceEMRModelsDiagnosisCategoryList.add(ve);*/
                            Intent intent = new Intent();
                            intent.putExtra("VoiceEMRModel", ve);
                            intent.putExtra("ActionType", "add");
                            setResult(100, intent);
                            finish();
                        }
//                        saveDiagnoses(diagObj);

                    } else {
                        if (encounterId == 0) {
                            Toast.makeText(VoiceEMRCreateRecordsFormActivity.this, "Please select interaction", Toast.LENGTH_SHORT).show();
                        } else if (diagName.getText().toString().equalsIgnoreCase("")) {
                            Toast.makeText(VoiceEMRCreateRecordsFormActivity.this, "Please enter diagnoses value", Toast.LENGTH_SHORT).show();
                        }


                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });

        createRecordSaveinvesData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try {
                    if (encounterId != 0 && !investName.getText().toString().equalsIgnoreCase("") &&
                            !investParam.getText().toString().equalsIgnoreCase("") &&
                            !investValue.getText().toString().equalsIgnoreCase("")) {

                        JSONObject investObj = new JSONObject();
                        investObj.put("investigation_name", investName.getText().toString());
                        investObj.put("parameter", investParam.getText().toString());
                        investObj.put("value", investValue.getText().toString());
                        investObj.put("notes", investNote.getText().toString());
                        investObj.put("file_url", uploadImageResponse);
                        investObj.put("patient_id", patientId);
                        investObj.put("episode_id", episodeId);
                        investObj.put("encounter_id", encounterId);
                        investObj.put("file_type", "image");


                        if (InvestigationEditRecord.equalsIgnoreCase("InvestigationEditRecord")) {
                            emrObject.setCategoryName("Investigation Results");
                            emrObject.setInvestigationInvestigation_name(investName.getText().toString());
                            emrObject.setInvestigationValue(investValue.getText().toString());
                            emrObject.setInvestigationNotes(investNote.getText().toString());
                            emrObject.setInvestigationParameter(investParam.getText().toString());
                            if (uploadImageResponse != null) {
                                emrObject.setInvestigationFile_url(uploadImageResponse);
                            } else {
                                emrObject.setInvestigationFile_url("");
                            }

                            emrObject.setInvestigatioFfile_type("image");
                            Intent intent = new Intent();
                            intent.putExtra("VoiceEMRModel", emrObject);
                            intent.putExtra("ActionType", "Edit");
                            setResult(100, intent);
                            finish();
                        } else {
                            VoiceEMRModel ve = new VoiceEMRModel();
                            ve.setCategoryName("Investigation Results");
                            ve.setInvestigationInvestigation_name(investName.getText().toString());
                            ve.setInvestigationValue(investValue.getText().toString());
                            ve.setInvestigationNotes(investNote.getText().toString());
                            ve.setInvestigationParameter(investParam.getText().toString());
                            if (uploadImageResponse != null) {
                                emrObject.setInvestigationFile_url(uploadImageResponse);
                            } else {
                                emrObject.setInvestigationFile_url("");
                            }
                            ve.setInvestigatioFfile_type("image");
                            Long tsLong = System.currentTimeMillis() / 1000;
                            String timeStamp = tsLong.toString();
                            ve.setInvestigationData_id(timeStamp);
                           /* if (VoiceEMRActivity.voiceEMRModelsInvestigationCategoryList.size() > 0) {
                                ve.setCategoryNameInvestigationResultsExistType(2);
                            } else {
                                ve.setCategoryNameInvestigationResultsExistType(1);
                            }*/

                            /*int subCatogorySerNumber = VoiceEMRActivity.voiceEMRModelsInvestigationCategoryList.size() + 1;
                            ve.setSubCategorySerNumber(subCatogorySerNumber + "");
                            VoiceEMRActivity.voiceEMRModelsInvestigationCategoryList.add(ve);*/
                            Intent intent = new Intent();
                            intent.putExtra("VoiceEMRModel", ve);
                            intent.putExtra("ActionType", "Edit");
                            setResult(100, intent);
                            finish();
                        }
//                        saveInvestData(investObj);
                    } else {
                        if (encounterId == 0) {
                            Toast.makeText(VoiceEMRCreateRecordsFormActivity.this, "Please select interaction", Toast.LENGTH_SHORT).show();
                        } else if (investName.getText().toString().equalsIgnoreCase("")) {
                            Toast.makeText(VoiceEMRCreateRecordsFormActivity.this, "Please enter name for investigation", Toast.LENGTH_SHORT).show();
                        } else if (investParam.getText().toString().equalsIgnoreCase("")) {
                            Toast.makeText(VoiceEMRCreateRecordsFormActivity.this, "Please enter parameter for investigation", Toast.LENGTH_SHORT).show();
                        } else if (investValue.getText().toString().equalsIgnoreCase("")) {
                            Toast.makeText(VoiceEMRCreateRecordsFormActivity.this, "Please enter parameter value for investigation", Toast.LENGTH_SHORT).show();
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });


        createRecordSymptomBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        createRecordDaigBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        createRecordInvesBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        investUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                openFileDialog();
                isInvestigationImageUpload = true;
                selectMethodDialog();
            }
        });

        c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);
        temp = mDay + " " + (mMonth + 1) + " " + mYear;
        temp = appUtilities.changeDateFormat("dd MM yyyy", "dd MMM, yy", temp);
        if (emrObject.getDiagnosisPosited_on() != null) {
            diagPoisted.setText(emrObject.getDiagnosisPosited_on());
        } else {
            diagPoisted.setText(temp);
        }
        diagPoisted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDateDialog(temp, diagPoisted);
            }
        });

        c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);
        temp = mDay + " " + (mMonth + 1) + " " + mYear;
        temp = appUtilities.changeDateFormat("dd MM yyyy", "dd MMM, yy", temp);
        if (emrObject.getDiagnosisConfirmed_ruledout_on() != null) {
            diagConfirm.setText(emrObject.getDiagnosisConfirmed_ruledout_on());
        } else {
            diagConfirm.setText(temp);
        }
        diagConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDateDialog(temp, diagConfirm);
            }
        });


        saveAndAddMoreLayout = findViewById(R.id.saveAndAddMoreLayout);
        saveAndAddMoreLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveHandWrittenNotes(VoiceEMRCreateRecordsFormActivity.this, ApiUrls.doctorId, encounterId, episodeId, handWrittenDescription.getText().toString().trim(), uploadImageResponse, patientId, "saveandmore", selectedPrescriptionText, selectedTestPresciption, validTillText);
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void saveHandWrittenNotes(Activity activity, int createdBy, int encounterId, int episodeId, String description, String fileUrl, int patientId, String from, int medPrescription, int testPrescription, String validTill) {

        if (TextUtils.isEmpty(uploadImageResponse)) {
            Toast.makeText(activity, "Please Upload Handwritten notes", Toast.LENGTH_LONG).show();
        } else if (TextUtils.isEmpty(handWrittenDescription.getText().toString().trim())) {
            Toast.makeText(activity, "Enter a valid description", Toast.LENGTH_LONG).show();
        } else if (medPrescription == 2) {
            Toast.makeText(activity, "Select a valid Medicine Prescription", Toast.LENGTH_LONG).show();
        } else if (testPrescription == 2) {
            Toast.makeText(activity, "Select a valid Test Prescription", Toast.LENGTH_LONG).show();
        } else {
            if (myClinicGlobalClass.isOnline()) {
                saveDialog.show();
                emrCreateRecordsFormViewModel.saveHandwrittenNotes(activity, createdBy, encounterId, episodeId, description, fileUrl, patientId, medPrescription, testPrescription, validTill,"",0).observe(VoiceEMRCreateRecordsFormActivity.this, new Observer<String>() {
                    @Override
                    public void onChanged(String s) {
                        saveDialog.dismiss();
                        try {
                            JSONObject response = new JSONObject(s);
                            if (response.getInt("status_code") == 200) {
                                JSONObject responseObj = response.getJSONObject("response").getJSONObject("response");
                                if (responseObj.has("id")) {
                                    if (from.equals("saveandclose")) {
                                        finish();
                                    } else {
                                        attachedImageLayout.setVisibility(View.GONE);
                                        removeAndViewLayout.setVisibility(View.GONE);
                                        handWrittenDescription.setText("");
                                        uploadImageResponse = "";
                                        medicinePrescriptionSpinner.setSelection(0);
                                        testPrescriptionSpinner.setSelection(0);
                                    }
                                }

                            } else {
                                ErrorHandlerClass.INSTANCE.errorHandler(VoiceEMRCreateRecordsFormActivity.this, s);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });

            } else {
                myClinicGlobalClass.noInternetConnection.showDialog(activity);

            }
        }

    }

//    public void allPrescriptionPopup() {
//        final Dialog dialog = new Dialog(EMRCreateRecordsFormActivity.this);
//        dialog.setCancelable(false);
//        dialog.setContentView(R.layout.dailog_emr_all_prescription_popup);
//        final ImageView dialogEvaluationCancel = (ImageView) dialog.findViewById(R.id.dialogAllPrescriptionCancel);
//        allPrescriptionRecyclerView = (RecyclerView) dialog.findViewById(R.id.allPrescriptionRecyclerView);
//        ProgressBar prescriptionProgress = dialog.findViewById(R.id.prescription_progress);
//        RelativeLayout prescriptionLayout = dialog.findViewById(R.id.prescriptionAllListLayout);
//        RelativeLayout noCaseLayout = dialog.findViewById(R.id.dialogNoPrescriptionLayout);
//        prescriptionProgress.setVisibility(View.GONE);
//        allPrescriptionRecyclerView.setLayoutManager(allPrescriptionLayoutManager);
//        allPrescriptionRecyclerView.setItemAnimator(new DefaultItemAnimator());
//        ProgressDialog progressDialog = new ProgressDialog(EMRCreateRecordsFormActivity.this);
//        progressDialog.setMessage("Please wait...");
//        allPrescriptionListAdapter = new EMRAllPrescriptionListAdapter(allPrescriptionList, this, new EMRPrescriptionClickListener() {
//            @Override
//            public void onItemClick(View v, EMRPrescriptionModel emrPrescriptionModel) {
//                progressDialog.show();
//                emrCreateRecordsFormViewModel.getImagePath(EMRCreateRecordsFormActivity.this,emrPrescriptionModel.getUrl()).observe(EMRCreateRecordsFormActivity.this, new Observer<String>() {
//                    @Override
//                    public void onChanged(String s) {
//                        progressDialog.dismiss();
//                        try{
//
//                            JSONObject response = new JSONObject(s);
//                            if(response.getInt("status") == 200){
//                                String completeUrl = response.getJSONObject("response").getString("response");
//                                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(completeUrl));
//// Note the Chooser below. If no applications match,
//// Android displays a system message.So here there is no need for try-catch.
//                                startActivity(Intent.createChooser(intent, "Browse with"));
//                            }
//                            else{
//                                Toast.makeText(EMRCreateRecordsFormActivity.this,response.getString("response"),Toast.LENGTH_LONG).show();
//                            }
//                        }
//                        catch (Exception e){
//                            e.printStackTrace();
//                        }
//                    }
//                });
//            }
//        });
//        allPrescriptionRecyclerView.setAdapter(allPrescriptionListAdapter);
//
//        if(myClinicGlobalClass.isOnline()){
//            prescriptionProgress.setVisibility(View.VISIBLE);
//            emrCreateRecordsFormViewModel.getPrescriptionDetails(EMRCreateRecordsFormActivity.this,encounterId,patientId).observe(EMRCreateRecordsFormActivity.this, new Observer<String>() {
//                @Override
//                public void onChanged(String s) {
//                    prescriptionProgress.setVisibility(View.GONE);
//                    try {
//                        JSONObject response = new JSONObject(s);
//                        if(response.getInt("status") == 200){
//                            JSONArray prescriptionArray = response.getJSONObject("response").getJSONArray("response");
//                            if(prescriptionArray.length() > 0){
//                                noCaseLayout.setVisibility(View.GONE);
//                                prescriptionLayout.setVisibility(View.VISIBLE);
//                                for(int i=0;i<prescriptionArray.length();i++){
//                                    JSONObject prescriptionObject = prescriptionArray.getJSONObject(i);
//                                    allPrescriptionList.add(new EMRPrescriptionModel(String.valueOf(prescriptionObject.getInt("id")),prescriptionObject.getString("url"),prescriptionObject.getString("created_at")));
//
//                                }
//                                allPrescriptionListAdapter.notifyDataSetChanged();
//                            }
//                            else{
//                                prescriptionLayout.setVisibility(View.GONE);
//                                noCaseLayout.setVisibility(View.VISIBLE);
//                            }
//
//                        }
//                        else{
//                            Toast.makeText(EMRCreateRecordsFormActivity.this,response.getString("response"),Toast.LENGTH_LONG).show();
//                        }
//                    }
//                    catch (Exception e)
//                    {
//                        e.printStackTrace();
//                    }
//
//                }
//            });
//        }
//        else
//        {
//            myClinicGlobalClass.noInternetConnection.showDialog(EMRCreateRecordsFormActivity.this);
//        }
//
//        prescriptionListItem();
//        dialogEvaluationCancel.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                dialog.dismiss();
//            }
//        });
//        dialog.show();
//
//
//    }

    private void prescriptionListItem() {
//        EMRAddRecordCategoryModel myList = new EMRAddRecordCategoryModel(
//                "Symptoms"
//        );
//        allPrescriptionList.add(myList);
//
//        myList = new EMRAddRecordCategoryModel(
//                "Comments"
//        );
//        allPrescriptionList.add(myList);
//        myList = new EMRAddRecordCategoryModel(
//                "Glucose"
//        );
//        allPrescriptionList.add(myList);
//        myList = new EMRAddRecordCategoryModel(
//                "Height"
//        );
//        allPrescriptionList.add(myList);


    }

    private void openDateDialog(String tv, TextView textview) {
        if (tv.equals("")) {
            final Calendar c = Calendar.getInstance();
            mYear = c.get(Calendar.YEAR);
            mMonth = c.get(Calendar.MONTH);
            mDay = c.get(Calendar.DAY_OF_MONTH);
        } else {
            String date = appUtilities.changeDateFormat("dd MMM, yy", "dd MM yyyy", tv);
            String[] dSplit = date.split(" ");
            mDay = Integer.parseInt(dSplit[0]);
            mMonth = Integer.parseInt(dSplit[1]) - 1;
            mYear = Integer.parseInt(dSplit[2]);
        }
        DatePickerDialog datePickerDialog = new DatePickerDialog(VoiceEMRCreateRecordsFormActivity.this,
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {

                        String temp = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;
                        temp = appUtilities.changeDateFormat("dd/MM/yyyy", "dd MMM, yy", temp);
                        textview.setText(temp);

                    }
                }, mYear, mMonth, mDay);
        datePickerDialog.show();
    }


    private void saveSymptom(JSONObject symObj) {

        final ProgressDialog loadingDialog = new ProgressDialog(this);
        loadingDialog.setMessage(getResources().getString(R.string.process_request));
        loadingDialog.setCancelable(false);
        loadingDialog.setInverseBackgroundForced(false);
        loadingDialog.show();
        emrConsultationNotesViewModel.saveSymptomRecord(this, symObj,RecordSavingType.UPDATE_SYMPTOMS).observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    if (jsonObject.getInt("status_code") == 200) {
                        JSONObject response = new JSONObject(s).getJSONObject("response");
//                        JSONArray prefArr = response.getJSONArray("response");
                        Intent returnIntent = new Intent();
                        returnIntent.putExtra("result", "OK");
                        setResult(Activity.RESULT_OK, returnIntent);
                        finish();
                        loadingDialog.dismiss();

                    } else {
                        loadingDialog.dismiss();
                        ErrorHandlerClass.INSTANCE.errorHandler(VoiceEMRCreateRecordsFormActivity.this, s);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    loadingDialog.dismiss();
                }

            }
        });

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

        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();

                if (ActivityCompat.checkSelfPermission(VoiceEMRCreateRecordsFormActivity.this, permissionsRequired[0]) == PackageManager.PERMISSION_GRANTED) {
                    openCamera();
                } else {
                    ActivityCompat.requestPermissions(VoiceEMRCreateRecordsFormActivity.this, permissionsRequired, PERMISSION_CALLBACK_CONSTANT);
                }
            }
        });

        attach.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
                openFileDialog();
            }
        });


        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();

                if (ActivityCompat.checkSelfPermission(VoiceEMRCreateRecordsFormActivity.this, permissionsRequired[0]) == PackageManager.PERMISSION_GRANTED) {
                    openCamera();
                } else {
                    ActivityCompat.requestPermissions(VoiceEMRCreateRecordsFormActivity.this, permissionsRequired, PERMISSION_CALLBACK_CONSTANT);
                }
            }
        });

        attachImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();

                openFileDialog();
            }
        });


    }


    public void openCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File photo = new File(Environment.getExternalStorageDirectory(), "Pic1.png");
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            fileUri = Uri.fromFile(photo);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
        } else {
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
                }
            }
        }
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        if (intent.resolveActivity(getApplicationContext().getPackageManager()) != null) {
            launcherCameraResults.launch(intent);
        }
    }

    private void openFileDialog() {

        Intent intent = new Intent();
// Show only images, no videos or anything else
        intent.setType("*/*");
        String[] mimeTypes = {"image/*", "application/pdf"};
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
        intent.setAction(Intent.ACTION_GET_CONTENT);
// Always show the chooser (if there are multiple options available)
        launchOpenFileResults.launch(Intent.createChooser(intent, "Select File"));
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

    private void uploadPDFFile(String selectedFilePath, Uri uri) {
        ContentResolver resolver = getApplicationContext()
                .getContentResolver();
        try {
            ParcelFileDescriptor pfd =
                    resolver.openFileDescriptor(uri, "r");
            InputStream stream = new FileInputStream(pfd.getFileDescriptor());
            File localfile = new File(this.getCacheDir(), selectedFilePath);
            OutputStream localStream = new FileOutputStream(localfile);
            IOUtils.copyStream(stream, localStream);
            pdfFile = localfile;
            type = "pdf";
            uploadImage("pdf");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
//        ContentResolver cr = getContentResolver();
//        Uri uri = MediaStore.Files.getContentUri("external");
//
//// every column, although that is huge waste, you probably need
//// BaseColumns.DATA (the path) only.
//        String[] projection = null;
//
//// exclude media files, they would be here also.
////                    String selection = MediaStore.Files.FileColumns.MEDIA_TYPE + "="
////                            + MediaStore.Files.FileColumns.MEDIA_TYPE_NONE;
////                    String[] selectionArgs = null; // there is no ? clinicplus selection so null here
////
//        String sortOrder = null; // unordered
////                    Cursor allNonMediaFiles = cr.query(uri, projection, selection, selectionArgs, sortOrder);
//
//        String selectionMimeType = MediaStore.Files.FileColumns.MIME_TYPE + "=?";
//        String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension("pdf");
//        String[] selectionArgsPdf = new String[]{mimeType};
//        Cursor allPdfFiles = cr.query(uri, projection, selectionMimeType, selectionArgsPdf, sortOrder);
////                    Log.d("Cursor ",  allPdfFiles.getString(1) + "");
//        int i = 0;
//        while (allPdfFiles.moveToNext()) {
////            Log.d("Cursorrrrrrr", allPdfFiles.getString(1));
////            Log.d("###########", selectedFilePath);
//            if (allPdfFiles.getString(1).contains(selectedFilePath)) {
//                pdfFile = new File(allPdfFiles.getString(1));
////                Intent inty = new Intent(Intent.ACTION_VIEW, FileProvider.getUriForFile(RecordCreateActivity.this,
////                        getApplicationContext().getPackageName() + ".provider",
////                        pdfFile));
////                inty.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
////                startActivity(inty);
//
//                uploadImage();
//            }
//        }
    }

    private void uploadImage(String type) {
        String url = ApiUrls.uploadRecordImage;

        final ProgressDialog loadingDialog = new ProgressDialog(this);
        if (type.equalsIgnoreCase("image")) {
            loadingDialog.setMessage(getResources().getString(R.string.uploading_image));
        } else {
            loadingDialog.setMessage(getResources().getString(R.string.uploading_pdf));
        }

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
                                //Log.i("upload response",uploadImageResponse);
                                if (TextUtils.isEmpty(categoryName)) {
                                    attachedImageLayout.setVisibility(View.VISIBLE);
                                    removeAndViewLayout.setVisibility(View.VISIBLE);
                                    attachedImage.setText(attachedFileName + " file has been attached");
                                }
                                if (type.equalsIgnoreCase("image")) {
                                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.upload_img_success), Toast.LENGTH_SHORT).show();
                                } else if (type.equalsIgnoreCase("pdf")) {
                                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.upload_pdf_success), Toast.LENGTH_SHORT).show();

                                }
                            }
                            isPDFFile = false;
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
//                params.put("path", "records/" + episodeId + "/");//old
                if (isInvestigationImageUpload) {
                    params.put("path", "records_v2/investigation_files/" + ApiUrls.doctorId + "/" + "android" + "/");//new
                } else {
                    params.put("path", "records_v2/images/" + ApiUrls.doctorId + "/" + "android" + "/");//new
                }
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
                    Bitmap bitmap = ((BitmapDrawable) investigationImageView.getDrawable()).getBitmap();
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
        volleyMultipartRequest.setRetryPolicy(new DefaultRetryPolicy(
                120000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        Volley.newRequestQueue(this).add(volleyMultipartRequest);
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

    private void getDocDiagnoses() {

        emrConsultationNotesViewModel.getDocDiagnosisArrayDetails(this).observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    if (jsonObject.getInt("status_code") == 200) {
                        JSONObject response = new JSONObject(s).getJSONObject("response");
//                        JSONArray rootArray = response.getJSONArray("response");
                        JSONArray diagArr = response.getJSONArray("response");
                        if (diagArr.length() > 0) {
                            for (int i = 0; i < diagArr.length(); i++) {
                                JSONObject diagObj = diagArr.getJSONObject(i);
                                diagNameArr.add(diagObj.getString("diagnosis_name"));
                                diagIdArr.add(diagObj.getInt("id"));

                                TextView tv = new TextView(VoiceEMRCreateRecordsFormActivity.this);
                                tv.setText(diagNameArr.get(i));
                                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                                        LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                                layoutParams.setMargins(30, 20, 30, 30);

                                tv.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        TextView tv1 = (TextView) v;
                                        diagName.setText(tv1.getText().toString());
                                        diagValueLayout.setVisibility(View.GONE);
                                        selectedFromAutocomplete = true;
                                    }
                                });

                                diagValueLayout.addView(tv, layoutParams);
                            }
                        }

                    } else {
                        ErrorHandlerClass.INSTANCE.errorHandler(VoiceEMRCreateRecordsFormActivity.this, s);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });

    }

    private void saveDiagnoses(JSONObject diagObj) {

        final ProgressDialog loadingDialog = new ProgressDialog(this);
        loadingDialog.setMessage(getResources().getString(R.string.process_request));
        loadingDialog.setCancelable(false);
        loadingDialog.setInverseBackgroundForced(false);
        loadingDialog.show();
        emrConsultationNotesViewModel.saveDiagnosisRecord(this, diagObj, RecordSavingType.SAVE_AND_CLOSE).observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    if (jsonObject.getInt("status_code") == 200) {
                        JSONObject response = new JSONObject(s).getJSONObject("response");
//                        JSONArray prefArr = response.getJSONArray("response");
                        Intent returnIntent = new Intent();
                        returnIntent.putExtra("result", "OK");
                        setResult(Activity.RESULT_OK, returnIntent);
                        finish();
                        loadingDialog.dismiss();

                    } else {
                        loadingDialog.dismiss();
                        ErrorHandlerClass.INSTANCE.errorHandler(VoiceEMRCreateRecordsFormActivity.this, s);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    loadingDialog.dismiss();
                }

            }
        });

    }

    private void saveInvestData(JSONObject investigationObj) {

        final ProgressDialog loadingDialog = new ProgressDialog(this);
        loadingDialog.setMessage(getResources().getString(R.string.process_request));
        loadingDialog.setCancelable(false);
        loadingDialog.setInverseBackgroundForced(false);
        loadingDialog.show();
        emrConsultationNotesViewModel.saveInvestigationRecord(this, investigationObj,RecordSavingType.SAVE_AND_CLOSE).observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    if (jsonObject.getInt("status_code") == 200) {
                        JSONObject response = new JSONObject(s).getJSONObject("response");
//                        JSONArray prefArr = response.getJSONArray("response");
                        Intent returnIntent = new Intent();
                        returnIntent.putExtra("result", "OK");
                        setResult(Activity.RESULT_OK, returnIntent);
                        finish();
                        loadingDialog.dismiss();

                    } else {
                        loadingDialog.dismiss();
                        ErrorHandlerClass.INSTANCE.errorHandler(VoiceEMRCreateRecordsFormActivity.this, s);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    loadingDialog.dismiss();
                }

            }
        });

    }


}