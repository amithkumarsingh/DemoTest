package com.whitecoats.clinicplus;

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
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
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
import com.whitecoats.clinicplus.apis.ApiUrls;
import com.whitecoats.clinicplus.utils.ErrorHandlerClass;
import com.zoho.salesiqembed.ZohoSalesIQ;

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

public class PatientCreateRecordActivity extends AppCompatActivity {

    private PatientRecordsApi apiCall;
    private int patientID, episodeID, encounterID;
    private List<String> ecntListArr;
    private List<Integer> ecntIdArr;
    private ArrayAdapter<String> ecntAdapter;
    private Spinner ecntDropdown;
    private Button saveData, createRecordBack;
    private String fileUrl = "";
    private int mYear, mMonth, mDay, mHour, mMinute;
    private AppUtilities appUtilities;
    private Uri fileUri;
    private String imageFilePath;

    //note form
    private ScrollView noteFormLayout;
    private RelativeLayout noteUploadFile;
    private EditText noteDesp;
    private TextView noteValidTill, noteValidTillLabel;
    private Spinner noteMeds, noteTest;
    private ImageView noteFileImage;
    String[] yesNo = {"No", "Yes"};

    //symptom
    private ScrollView symptomForm;
    private EditText sympName, sympDesp;
    private TextView sympFirstReport;
    private Spinner sympStatus;
    String[] sympStatuArr = {"Ongoing", "Partially Subsided", "Completely Subsided"};

    //invest
    private ScrollView investForm;
    private EditText investName, investParam, investValue, investNote;
    private RelativeLayout investUpload;

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
    private static final int REQUEST_PERMISSION_SETTING = 101;
    private String[] permissionsRequired = new String[]{android.Manifest.permission.CAMERA};
    private static final int PERMISSION_CALLBACK_CONSTANT = 100;
    private String uploadImageResponse;
    private File pdfFile;
    private RelativeLayout recordEpisDropdown;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_create_record);

        recordEpisDropdown = findViewById(R.id.recordEpisDropdown);
        ecntDropdown = findViewById(R.id.recordEpisSelectEpis);
        saveData = findViewById(R.id.createRecordSaveData);
        createRecordBack = findViewById(R.id.createRecordBack);
        final String recordName = getIntent().getStringExtra("RecordName");
        patientID = getIntent().getIntExtra("PatientId", 0);
        episodeID = getIntent().getIntExtra("EpisodeId", 0);
        apiCall = new PatientRecordsApi();
        ecntListArr = new ArrayList<>();
        ecntIdArr = new ArrayList<>();
        appUtilities = new AppUtilities();

        ZohoSalesIQ.Tracking.setPageTitle("PatientCreateRecord Page");

        //note form
        noteFormLayout = findViewById(R.id.recordCreateNotesForm);
        noteUploadFile = findViewById(R.id.createNotesHnUploadFile);
        noteDesp = findViewById(R.id.createNotesHnDesp);
        noteValidTill = findViewById(R.id.createNotesHnValidTill);
        noteMeds = findViewById(R.id.createNotesHnMedPres);
        noteTest = findViewById(R.id.createNotesHnTestPres);
        noteFileImage = findViewById(R.id.createNotesHnImage);
        noteValidTillLabel = findViewById(R.id.createNotesHnValidTillLabel);

        //symptom
        symptomForm = findViewById(R.id.recordsCreateSymptomForm);
        sympName = findViewById(R.id.createSymptomName);
        sympDesp = findViewById(R.id.createSymptomDesp);
        sympFirstReport = findViewById(R.id.createSympFirstReporton);
        sympStatus = findViewById(R.id.createSympStatus);

        //invest
        investForm = findViewById(R.id.recordCreateInvestForm);
        investName = findViewById(R.id.recordCreateInvestName);
        investParam = findViewById(R.id.recordCreateInvestParam);
        investValue = findViewById(R.id.recordCreateInvestParamValue);
        investNote = findViewById(R.id.recordCreateInvestNotes);
        investUpload = findViewById(R.id.recordCreateInvestUpload);

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

        ecntAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, ecntListArr);
        ecntAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ecntDropdown.setAdapter(ecntAdapter);

        if (PatientRecordsCaseActivity.dropDownShowFlag == 1) {
            recordEpisDropdown.setVisibility(View.GONE);
            encounterID = PatientRecordsCaseActivity.encounterId;
        } else {
            recordEpisDropdown.setVisibility(View.VISIBLE);

        }

//        Log.d("Record Cat", recordName);
        if (recordName.equalsIgnoreCase("WrittenNotes")) {
            noteFormLayout.setVisibility(View.VISIBLE);
            ZohoSalesIQ.Tracking.setPageTitle("PatientCreateRecord - Notes Form");
        } else if (recordName.equalsIgnoreCase("Symptoms")) {
            symptomForm.setVisibility(View.VISIBLE);
            ZohoSalesIQ.Tracking.setPageTitle("PatientCreateRecord - Symptoms Form");
        } else if (recordName.equalsIgnoreCase("Investigation")) {
            investForm.setVisibility(View.VISIBLE);
            ZohoSalesIQ.Tracking.setPageTitle("PatientCreateRecord - Investigation Form");
        } else if (recordName.equalsIgnoreCase("Diagnosis")) {
            diagForm.setVisibility(View.VISIBLE);
            diagValueLayout.setVisibility(View.GONE);
            ZohoSalesIQ.Tracking.setPageTitle("PatientCreateRecord - Diagnosis Form");
            getDocDiagnoses();
        }

        getEpisEncounter();


        ecntDropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                ZohoSalesIQ.Tracking.setPageTitle("PatientCreateRecord - Selected Encounter");
                if (PatientEpisodeActivity.allInteractionSelectedPosition != 0) {
                    ecntDropdown.setSelection(PatientEpisodeActivity.allInteractionSelectedPosition);
                    encounterID = ecntIdArr.get(PatientEpisodeActivity.allInteractionSelectedPosition);
                } else {
                    if (ecntIdArr.get(i) != 0) {
                        encounterID = ecntIdArr.get(i);
                    } else {

                        if (PatientRecordsCaseActivity.encounterId != 0) {
                            for (int j = 0; j < ecntIdArr.size(); j++) {
                                if (PatientRecordsCaseActivity.encounterId == ecntIdArr.get(j)) {
                                    ecntDropdown.setSelection(j);
                                    encounterID = ecntIdArr.get(j);
                                }
                            }
                        } else {
                            // Toast.makeText(PatientCreateRecordActivity.this, getResources().getString(R.string.please_select_encounter), Toast.LENGTH_SHORT).show();

                        }

                    }
                }


//                if(ecntIdArr.get(PatientEpisodeActivity.allInteractionSelectedPosition) != 0) {
//                    encounterID = ecntIdArr.get(i);
//                } else {
//                    Toast.makeText(PatientCreateRecordActivity.this, getResources().getString(R.string.please_select_encounter), Toast.LENGTH_SHORT).show();
//                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        saveData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ZohoSalesIQ.Tracking.setPageTitle("PatientCreateRecord - Saving Patient Records");
                try {
                    if (recordName.equalsIgnoreCase("WrittenNotes")) {

                        if (encounterID != 0 && !uploadImageResponse.equalsIgnoreCase("")) {
                            JSONObject noteObj = new JSONObject();
                            noteObj.put("patient_id", patientID);
                            noteObj.put("episode_id", episodeID);
                            noteObj.put("encounter_id", encounterID);
                            noteObj.put("created_by", ApiUrls.doctorId);
                            noteObj.put("description", noteDesp.getText().toString());
                            noteObj.put("has_med_prescription", 0);
                            noteObj.put("med_prescription_valid_till", "");
                            if (noteMeds.getSelectedItem().equals("yes")) {
                                noteObj.put("has_med_prescription", 1);
                                String temp = appUtilities.changeDateFormat("dd MMM, yy", "dd-MM-yyyy", noteValidTill.getText().toString());
                                noteObj.put("med_prescription_valid_till", temp);
                            }
                            noteObj.put("has_test_prescription", 0);
                            if (noteTest.getSelectedItem().equals("no")) {
                                noteObj.put("has_test_prescription", 1);
                            }
                            noteObj.put("file_url", uploadImageResponse);

                            saveWrittenNotes(noteObj);
                        } else {
                            if (encounterID == 0) {
                                Toast.makeText(PatientCreateRecordActivity.this, "Please select interaction", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(PatientCreateRecordActivity.this, "Please upload a file", Toast.LENGTH_SHORT).show();
                            }
                        }
                    } else if (recordName.equalsIgnoreCase("Symptoms")) {
                        if (encounterID != 0 && !sympName.getText().toString().equalsIgnoreCase("")) {
                            JSONObject symObj = new JSONObject();
                            symObj.put("symptom_name", sympName.getText());
                            String temp = appUtilities.changeDateFormat("dd MMM, yy", "dd-MM-yyyy", sympFirstReport.getText().toString());
                            symObj.put("first_reported_on", temp);
                            symObj.put("symptom_description", sympDesp.getText().toString());
                            symObj.put("symptom_status", sympStatus.getSelectedItem());
                            symObj.put("patient_id", patientID);
                            symObj.put("episode_id", episodeID);
                            symObj.put("encounter_id", encounterID);

                            saveSymptom(symObj);
                        } else {
                            if (encounterID == 0) {
                                Toast.makeText(PatientCreateRecordActivity.this, "Please select interaction", Toast.LENGTH_SHORT).show();
                            } else if (sympName.getText().toString().equalsIgnoreCase("")) {
                                Toast.makeText(PatientCreateRecordActivity.this, "Please enter symptom name", Toast.LENGTH_SHORT).show();
                            }

//                            else {
//                                Toast.makeText(PatientCreateRecordActivity.this, "Please upload a file", Toast.LENGTH_SHORT).show();
//                            }
                        }
                    } else if (recordName.equalsIgnoreCase("Investigation")) {
                        if (encounterID != 0 && !investName.getText().toString().equalsIgnoreCase("") &&
                                !investParam.getText().toString().equalsIgnoreCase("") &&
                                !investValue.getText().toString().equalsIgnoreCase("")) {

                            JSONObject investObj = new JSONObject();
                            investObj.put("investigation_name", investName.getText().toString());
                            investObj.put("parameter", investParam.getText().toString());
                            investObj.put("value", investValue.getText().toString());
                            investObj.put("notes", investNote.getText().toString());
                            investObj.put("file_url", uploadImageResponse);
                            investObj.put("patient_id", patientID);
                            investObj.put("episode_id", episodeID);
                            investObj.put("encounter_id", encounterID);
                            investObj.put("file_type", "image");

                            saveInvestData(investObj);
                        } else {
                            if (encounterID == 0) {
                                Toast.makeText(PatientCreateRecordActivity.this, "Please select interaction", Toast.LENGTH_SHORT).show();
                            } else if (investName.getText().toString().equalsIgnoreCase("")) {
                                Toast.makeText(PatientCreateRecordActivity.this, "Please enter name for investigation", Toast.LENGTH_SHORT).show();
                            } else if (investParam.getText().toString().equalsIgnoreCase("")) {
                                Toast.makeText(PatientCreateRecordActivity.this, "Please enter parameter for investigation", Toast.LENGTH_SHORT).show();
                            } else if (investValue.getText().toString().equalsIgnoreCase("")) {
                                Toast.makeText(PatientCreateRecordActivity.this, "Please enter parameter value for investigation", Toast.LENGTH_SHORT).show();
                            }
                        }
                    } else if (recordName.equalsIgnoreCase("Diagnosis")) {
                        if (encounterID != 0 && !diagName.getText().toString().equalsIgnoreCase("")) {
                            JSONObject diagObj = new JSONObject();
                            diagObj.put("patient_id", patientID);
                            diagObj.put("episode_id", episodeID);
                            diagObj.put("encounter_id", encounterID);
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

                            saveDiagnoses(diagObj);


                        } else {
                            if (encounterID == 0) {
                                Toast.makeText(PatientCreateRecordActivity.this, "Please select interaction", Toast.LENGTH_SHORT).show();
                            } else if (diagName.getText().toString().equalsIgnoreCase("")) {
                                Toast.makeText(PatientCreateRecordActivity.this, "Please enter diagnoses value", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        createRecordBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        noteUploadFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                openFileDialog();
                selectMethodDialog();
            }
        });

        ArrayAdapter aa = new ArrayAdapter(this, android.R.layout.simple_spinner_item, yesNo);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Setting the ArrayAdapter data on the Spinner
        noteMeds.setAdapter(aa);
        noteMeds.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (yesNo[i].equals("Yes")) {
                    noteValidTill.setVisibility(View.VISIBLE);
                    noteValidTillLabel.setVisibility(View.VISIBLE);
                } else {
                    noteValidTill.setVisibility(View.GONE);
                    noteValidTillLabel.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        aa = new ArrayAdapter(this, android.R.layout.simple_spinner_item, yesNo);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Setting the ArrayAdapter data on the Spinner
        noteTest.setAdapter(aa);

        Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);
        String temp = mDay + " " + (mMonth + 1) + " " + mYear;
        temp = appUtilities.changeDateFormat("dd MM yyyy", "dd MMM, yy", temp);
        noteValidTill.setText(temp);
        noteValidTill.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDateDialog(noteValidTill);
            }
        });

        aa = new ArrayAdapter(this, android.R.layout.simple_spinner_item, sympStatuArr);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Setting the ArrayAdapter data on the Spinner
        sympStatus.setAdapter(aa);

        aa = new ArrayAdapter(this, android.R.layout.simple_spinner_item, diagStatusArr);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Setting the ArrayAdapter data on the Spinner
        diagStatus.setAdapter(aa);

        c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);
        temp = mDay + " " + (mMonth + 1) + " " + mYear;
        temp = appUtilities.changeDateFormat("dd MM yyyy", "dd MMM, yy", temp);
        sympFirstReport.setText(temp);
        sympFirstReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDateDialog(sympFirstReport);
            }
        });

        investUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                openFileDialog();
                selectMethodDialog();
            }
        });

        c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);
        temp = mDay + " " + (mMonth + 1) + " " + mYear;
        temp = appUtilities.changeDateFormat("dd MM yyyy", "dd MMM, yy", temp);
        diagPoisted.setText(temp);
        diagPoisted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDateDialog(diagPoisted);
            }
        });

        c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);
        temp = mDay + " " + (mMonth + 1) + " " + mYear;
        temp = appUtilities.changeDateFormat("dd MM yyyy", "dd MMM, yy", temp);
        diagConfirm.setText(temp);
        diagConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDateDialog(diagConfirm);
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
    }

    private void getEpisEncounter() {
        String url = ApiUrls.getEpisEncounter + "?episode_id=" + episodeID + "&patient_id=" + patientID;

        apiCall.getRecordPref(url, "", this, new VolleyCallback() {
            @Override
            public void onSuccess(String result) {
                try {

                    JSONObject resObj = new JSONObject(result);
                    JSONArray enctArr = resObj.getJSONArray("response");
                    if (enctArr.length() > 0) {
                        ecntListArr.add("Select Interaction");
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
                ErrorHandlerClass.INSTANCE.errorHandler(PatientCreateRecordActivity.this, err);
            }
        });
    }

    private void saveWrittenNotes(JSONObject notesObj) {
        final ProgressDialog loadingDialog = new ProgressDialog(this);
        loadingDialog.setMessage(getResources().getString(R.string.process_request));
        loadingDialog.setCancelable(false);
        loadingDialog.setInverseBackgroundForced(false);
        loadingDialog.show();

        apiCall.postRecords(ApiUrls.saveWrittenNotes, notesObj, this, new VolleyCallback() {
            @Override
            public void onSuccess(String result) {
//                Log.d("Save Note", result);
                Intent returnIntent = new Intent();
                returnIntent.putExtra("result", "OK");
                setResult(Activity.RESULT_OK, returnIntent);
                finish();
                loadingDialog.dismiss();
            }

            @Override
            public void onError(String err) {
                Log.e("Save Note", err);
                ErrorHandlerClass.INSTANCE.errorHandler(PatientCreateRecordActivity.this, err);
                loadingDialog.dismiss();
            }
        });
    }

    private void saveSymptom(JSONObject symObj) {
        final ProgressDialog loadingDialog = new ProgressDialog(this);
        loadingDialog.setMessage(getResources().getString(R.string.process_request));
        loadingDialog.setCancelable(false);
        loadingDialog.setInverseBackgroundForced(false);
        loadingDialog.show();

        apiCall.postRecords(ApiUrls.saveSymptom, symObj, this, new VolleyCallback() {
            @Override
            public void onSuccess(String result) {
                Log.d("symptom","symptom");
                Intent returnIntent = new Intent();
                returnIntent.putExtra("result", "OK");
                setResult(Activity.RESULT_OK, returnIntent);
                finish();
                loadingDialog.dismiss();
            }

            @Override
            public void onError(String err) {
                ErrorHandlerClass.INSTANCE.errorHandler(PatientCreateRecordActivity.this, err);
                loadingDialog.dismiss();
            }
        });
    }

    private void saveInvestData(JSONObject investObj) {
        final ProgressDialog loadingDialog = new ProgressDialog(this);
        loadingDialog.setMessage(getResources().getString(R.string.process_request));
        loadingDialog.setCancelable(false);
        loadingDialog.setInverseBackgroundForced(false);
        loadingDialog.show();

        apiCall.postRecords(ApiUrls.saveInvestData, investObj, this, new VolleyCallback() {
            @Override
            public void onSuccess(String result) {
                Intent returnIntent = new Intent();
                returnIntent.putExtra("result", "OK");
                setResult(Activity.RESULT_OK, returnIntent);
                finish();
                loadingDialog.dismiss();
            }

            @Override
            public void onError(String err) {
                ErrorHandlerClass.INSTANCE.errorHandler(PatientCreateRecordActivity.this, err);
                loadingDialog.dismiss();
            }
        });
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
                        noteFileImage.setImageBitmap(scaled);

                        // Set the camera taken image bitmap clinicplus the image view component to display.
//                        notesHnFormImage.setVisibility(View.VISIBLE);
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
                            noteFileImage.setImageBitmap(scaled);

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
                if (ActivityCompat.checkSelfPermission(PatientCreateRecordActivity.this, permissionsRequired[0]) == PackageManager.PERMISSION_GRANTED) {
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

    private void uploadImage() {
        String url = ApiUrls.uploadRecordImage;

        final ProgressDialog loadingDialog = new ProgressDialog(this);
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
                    Bitmap bitmap = ((BitmapDrawable) noteFileImage.getDrawable()).getBitmap();
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

    private void openDateDialog(final TextView tv) {
        if (tv.getText().equals("")) {
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
        DatePickerDialog datePickerDialog = new DatePickerDialog(PatientCreateRecordActivity.this,
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

        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();

                if (ActivityCompat.checkSelfPermission(PatientCreateRecordActivity.this, permissionsRequired[0]) == PackageManager.PERMISSION_GRANTED) {
                    openCamera();
                } else {
                    ActivityCompat.requestPermissions(PatientCreateRecordActivity.this, permissionsRequired, PERMISSION_CALLBACK_CONSTANT);
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

                if (ActivityCompat.checkSelfPermission(PatientCreateRecordActivity.this, permissionsRequired[0]) == PackageManager.PERMISSION_GRANTED) {
                    openCamera();
                } else {
                    ActivityCompat.requestPermissions(PatientCreateRecordActivity.this, permissionsRequired, PERMISSION_CALLBACK_CONSTANT);
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

    private void getDocDiagnoses() {

        apiCall.getRecordPref(ApiUrls.getDocDiagnoses, "", this, new VolleyCallback() {
            @Override
            public void onSuccess(String result) {
                try {
                    JSONObject resObj = new JSONObject(result);

                    JSONArray diagArr = resObj.getJSONArray("response");
                    if (diagArr.length() > 0) {
                        for (int i = 0; i < diagArr.length(); i++) {
                            JSONObject diagObj = diagArr.getJSONObject(i);
                            diagNameArr.add(diagObj.getString("diagnosis_name"));
                            diagIdArr.add(diagObj.getInt("id"));

                            TextView tv = new TextView(PatientCreateRecordActivity.this);
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
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(String err) {
                ErrorHandlerClass.INSTANCE.errorHandler(PatientCreateRecordActivity.this, err);
            }
        });
    }

    private void saveDiagnoses(JSONObject diagObj) {
//        Log.d("Diag Obj", diagObj.toString());
        final ProgressDialog loadingDialog = new ProgressDialog(this);
        loadingDialog.setMessage(getResources().getString(R.string.process_request));
        loadingDialog.setCancelable(false);
        loadingDialog.setInverseBackgroundForced(false);
        loadingDialog.show();

        apiCall.postRecords(ApiUrls.saveDiagnoses, diagObj, this, new VolleyCallback() {
            @Override
            public void onSuccess(String result) {
//                Log.d("Save Note", result);
                Intent returnIntent = new Intent();
                returnIntent.putExtra("result", "OK");
                setResult(Activity.RESULT_OK, returnIntent);
                finish();
                loadingDialog.dismiss();
            }

            @Override
            public void onError(String err) {
                Log.e("Save Note", err);
                ErrorHandlerClass.INSTANCE.errorHandler(PatientCreateRecordActivity.this, err);
                loadingDialog.dismiss();
            }
        });
    }
}
