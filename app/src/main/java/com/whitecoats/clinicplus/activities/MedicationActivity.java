package com.whitecoats.clinicplus.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.whitecoats.clinicplus.MyClinicGlobalClass;
import com.whitecoats.clinicplus.R;
import com.whitecoats.clinicplus.adapters.MedicineSearchAdapter;
import com.whitecoats.clinicplus.apis.ApiUrls;
import com.whitecoats.clinicplus.models.MedicineModel;
import com.whitecoats.clinicplus.utils.ErrorHandlerClass;
import com.whitecoats.clinicplus.viewmodels.AppointmentViewModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class MedicationActivity extends AppCompatActivity {
    private AutoCompleteTextView medicationSearch;
    private TextView medicationSearchResult, selectedMedicineName;
    private LinearLayout medicationSearchLayout, medicationDetailsLayout;
    private EditText medicationName, medicationCompanyName, medicineNoOfDays, medicineStrength, medicineDose, medicineAddInstructions;
    private TextView medicineStartDate;
    private Button medicationSavePrescribeButton, medicationPrescribe;
    private ImageButton cancelMedicine, medicineBack;
    private Spinner medicineFrequency, medicineIntakeMethod;
    private CheckBox medicineMorning, medicineEvening, medicineAfternoon, medicineNight;
    private MedicineSearchAdapter medicineSearchAdapter;
    private List<MedicineModel> medicineModelList;
    private String medicineString;
    private MyClinicGlobalClass globalClass;
    private AppointmentViewModel appointmentViewModel;
    private String frequency, intakeMethod;
    private ArrayAdapter frequencyAdapter, inTakeAdapter;
    private ArrayList<String> frequencyList, intakeList;
    private ProgressBar progressBar;
    private String patientId;
    private int episodeId, encounterId;
    private Date date;
    private SimpleDateFormat simpleDateFormat;
    private String currentDate;
    private int cyear, cmonth, cday;
    private Calendar calendar;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medication);
        medicationSearch = findViewById(R.id.medication_search);
        medicationSearchResult = findViewById(R.id.medicine_search_result_text);
        medicationSearchLayout = findViewById(R.id.medicine_search_layout);
        medicationDetailsLayout = findViewById(R.id.medicine_details_layout);
        medicationName = findViewById(R.id.medicine_name);
        medicationCompanyName = findViewById(R.id.medicine_company_name);
        medicationSavePrescribeButton = findViewById(R.id.medication_save_prescribe);
        selectedMedicineName = findViewById(R.id.selected_medicine_name);
        cancelMedicine = findViewById(R.id.cancel_medicine);
        medicineNoOfDays = findViewById(R.id.medicine_no_of_days);
        medicineFrequency = findViewById(R.id.medicine_frequency);
        medicineIntakeMethod = findViewById(R.id.medicine_intake_method);
        medicineStrength = findViewById(R.id.medicine_strength);
        medicineDose = findViewById(R.id.medicine_dose);
        medicineAddInstructions = findViewById(R.id.medicine_add_instructions);
        medicineMorning = findViewById(R.id.medication_morning);
        medicineAfternoon = findViewById(R.id.medication_afternoon);
        medicineEvening = findViewById(R.id.medication_evening);
        medicineNight = findViewById(R.id.medication_night);
        medicationPrescribe = findViewById(R.id.medication_prescribe);
        progressBar = findViewById(R.id.medicine_progress);
        medicineStartDate = findViewById(R.id.medicine_start_date);
        medicineBack = findViewById(R.id.medication_back);
        progressBar.setVisibility(View.GONE);

        intent = getIntent();

//        patientId = "47240";
//        episodeId = 1715;
//        encounterId = 2909;

        patientId = String.valueOf(intent.getIntExtra("patientId", 0));
        episodeId = intent.getIntExtra("episodeId", 0);
        encounterId = intent.getIntExtra("encounterId", 0);

        date = new Date();
        simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");

        calendar = Calendar.getInstance();

        cyear = calendar.get(Calendar.YEAR);
        cmonth = calendar.get(Calendar.MONTH);
        cday = calendar.get(Calendar.DAY_OF_MONTH);

        currentDate = simpleDateFormat.format(date);

        medicineStartDate.setText(currentDate);
        MyClinicGlobalClass.logUserActionEvent(ApiUrls.doctorId, getString(R.string.MedicationScreenImpressions), null);
        medicineNoOfDays.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                MyClinicGlobalClass.logUserActionEvent(ApiUrls.doctorId, getString(R.string.MedicationScreenForDays
                ), null);
            }
        });
        medicineStrength.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                MyClinicGlobalClass.logUserActionEvent(ApiUrls.doctorId, getString(R.string.MedicationScreenStrength
                ), null);
            }
        });
        medicineAddInstructions.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                MyClinicGlobalClass.logUserActionEvent(ApiUrls.doctorId, getString(R.string.MedicationScreenInstructions
                ), null);
            }
        });
        medicineDose.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                MyClinicGlobalClass.logUserActionEvent(ApiUrls.doctorId, getString(R.string.MedicationScreenDose
                ), null);
            }
        });
        medicineStartDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(MedicationActivity.this, new DatePickerDialog.OnDateSetListener() {
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
                        currentDate = d + "-" + (m) + "-" + year;
                        medicineStartDate.setText(currentDate);
                    }
                }, cyear, cmonth, cday);
                datePickerDialog.show();
            }
        });


        frequencyList = new ArrayList<>();
        intakeList = new ArrayList<>();

        frequencyList.add("Select frequency");
        intakeList.add("Select InTake method");

        frequencyAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, frequencyList);
        inTakeAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, intakeList);

        medicineFrequency.setAdapter(frequencyAdapter);
        medicineIntakeMethod.setAdapter(inTakeAdapter);

        medicineFrequency.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                frequency = (String) adapterView.getItemAtPosition(i);
                MyClinicGlobalClass.logUserActionEvent(ApiUrls.doctorId, getString(R.string.MedicationScreenFrequency
                ), null);

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                frequency = "select frequency";
            }
        });

        medicineIntakeMethod.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                intakeMethod = (String) adapterView.getItemAtPosition(i);
                MyClinicGlobalClass.logUserActionEvent(ApiUrls.doctorId, getString(R.string.MedicationScreenIntakeMethod
                ), null);

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                frequency = "select intake method";
            }
        });
        medicineBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        globalClass = (MyClinicGlobalClass) getApplicationContext();
        appointmentViewModel = new ViewModelProvider(this).get(AppointmentViewModel.class);
        appointmentViewModel.init();

        medicineModelList = new ArrayList<>();
        medicineSearchAdapter = new MedicineSearchAdapter(MedicationActivity.this, R.layout.patient_search_item, medicineModelList);
        medicationSearch.setAdapter(medicineSearchAdapter);
        medicationSearch.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MyClinicGlobalClass.logUserActionEvent(ApiUrls.doctorId, getString(R.string.MedicationScreenSearch
                ), null);

                MedicineModel medicineModel = (MedicineModel) parent.getItemAtPosition(position);
                medicineString = medicineModel.getMedicineName();
                selectedMedicineName.setText(medicineString);
                progressBar.setVisibility(View.VISIBLE);
                getDropDownDetails();
            }
        });

        medicationPrescribe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyClinicGlobalClass.logUserActionEvent(ApiUrls.doctorId, getString(R.string.MedicationScreenPrescribe
                ), null);

                if (TextUtils.isEmpty(medicineNoOfDays.getText().toString().trim())) {
                    Toast.makeText(MedicationActivity.this, "Enter valid no of days", Toast.LENGTH_LONG).show();
                } else if (frequency.equalsIgnoreCase("select frequency")) {
                    Toast.makeText(MedicationActivity.this, "select a valid frequency", Toast.LENGTH_LONG).show();
                } else if (intakeMethod.equalsIgnoreCase("select intake method")) {
                    Toast.makeText(MedicationActivity.this, "select a valid intake method", Toast.LENGTH_LONG).show();
                } else if (!medicineMorning.isChecked() && !medicineAfternoon.isChecked() && !medicineEvening.isChecked() && !medicineNight.isChecked()) {
                    Toast.makeText(MedicationActivity.this, "select a valid duration", Toast.LENGTH_LONG).show();
                } else {
                    progressBar.setVisibility(View.VISIBLE);
                    if (globalClass.isOnline()) {
                        JSONObject metadata = new JSONObject();
                        JSONObject recordData = new JSONObject();
                        JSONObject data = new JSONObject();
                        JSONObject request = new JSONObject();
                        try {

                            metadata.put("222", selectedMedicineName.getText().toString().trim());
                            metadata.put("241", frequency);
                            metadata.put("225", intakeMethod);
                            metadata.put("233", Integer.parseInt(medicineNoOfDays.getText().toString().trim()));
                            metadata.put("226", currentDate);
                            if (medicineMorning.isChecked()) {
                                metadata.put("234", 1);
                            }
                            if (medicineAfternoon.isChecked()) {
                                metadata.put("235", 1);
                            }
                            if (medicineEvening.isChecked()) {
                                metadata.put("236", 1);
                            }
                            if (medicineNight.isChecked()) {
                                metadata.put("237", 1);
                            }
                            if (!TextUtils.isEmpty(medicineStrength.getText().toString().trim())) {
                                metadata.put("223", medicineStrength.getText().toString().trim());
                            }
                            if (!TextUtils.isEmpty(medicineDose.getText().toString().trim())) {
                                metadata.put("224", medicineDose.getText().toString().trim());
                            }
                            if (!TextUtils.isEmpty(medicineAddInstructions.getText().toString().trim())) {
                                metadata.put("230", medicineAddInstructions.getText().toString().trim());
                            }
                            recordData.put("user_id", ApiUrls.doctorId);
                            recordData.put("doctor_id", ApiUrls.doctorId);
                            recordData.put("patient_id", patientId);
                            recordData.put("catid", "38");
                            data.put("episode_id", episodeId);
                            data.put("encounter_id", encounterId);
                            data.put("category_id", "38");
                            data.put("patient_id", patientId);
                            data.put("type", "treatmentplan");
                            data.put("metadata", metadata);
                            data.put("record_data", recordData);
                            data.put("dietdata", new JSONArray());
                            JSONArray dataArray = new JSONArray();
                            dataArray.put(data);
                            request.put("data", dataArray);
                            appointmentViewModel.storeMedication(MedicationActivity.this, request).observe(MedicationActivity.this, new Observer<String>() {
                                @Override
                                public void onChanged(String s) {
                                    progressBar.setVisibility(View.GONE);
                                    try {
                                        JSONObject response = new JSONObject(s);
                                        if (response.getInt("status_code") == 200) {
                                            JSONObject responseObj = response.getJSONObject("response").getJSONObject("response");
                                            if (responseObj.getString("save_message").contains("1 record(s) saved.")) {
                                                medicineNoOfDays.setText("");
                                                medicineFrequency.setSelection(0);
                                                medicineIntakeMethod.setSelection(0);
                                                medicineMorning.setChecked(false);
                                                medicineAfternoon.setChecked(false);
                                                medicineEvening.setChecked(false);
                                                medicineNight.setChecked(false);
                                                medicineStrength.setText("");
                                                medicineDose.setText("");
                                                medicineAddInstructions.setText("");
                                                medicationSearch.setText("");
                                                medicationDetailsLayout.setVisibility(View.GONE);
                                                medicationSearchLayout.setVisibility(View.VISIBLE);
                                            } else {
                                                Toast.makeText(MedicationActivity.this, "Cannot able to store medicine, please try again", Toast.LENGTH_LONG).show();
                                            }
                                        } else {
                                            ErrorHandlerClass.INSTANCE.errorHandler(MedicationActivity.this, s);
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }


                                }
                            });


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {
                        globalClass.noInternetConnection.showDialog(MedicationActivity.this);
                    }
                }


            }
        });

        cancelMedicine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                medicationDetailsLayout.setVisibility(View.GONE);
                medicationSearchLayout.setVisibility(View.VISIBLE);
            }
        });

        medicationName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                MyClinicGlobalClass.logUserActionEvent(ApiUrls.doctorId, getString(R.string.MedicationMedicineName
                ), null);
            }
        });
        medicationCompanyName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                MyClinicGlobalClass.logUserActionEvent(ApiUrls.doctorId, getString(R.string.MedicationMedicineCompanyName
                ), null);
            }
        });


        medicationSavePrescribeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                MyClinicGlobalClass.logUserActionEvent(ApiUrls.doctorId, getString(R.string.MedicationMedicineSave), null);

                if (TextUtils.isEmpty(medicationName.getText().toString().trim())) {
                    Toast.makeText(MedicationActivity.this, "Enter a valid medicine name", Toast.LENGTH_LONG).show();
                } else if (TextUtils.isEmpty(medicationCompanyName.getText().toString().trim())) {
                    Toast.makeText(MedicationActivity.this, "Enter a valid company name", Toast.LENGTH_LONG).show();
                } else {
                    if (globalClass.isOnline()) {
                        progressBar.setVisibility(View.VISIBLE);
                        JSONObject params = new JSONObject();
                        try {
                            params.put("name", medicationName.getText().toString().trim());
                            params.put("company", medicationCompanyName.getText().toString().trim());
                            appointmentViewModel.addMedicine(MedicationActivity.this, params).observe(MedicationActivity.this, new Observer<String>() {
                                @Override
                                public void onChanged(String s) {
                                    try {
                                        JSONObject response = new JSONObject(s);
                                        if (response.getInt("status_code") == 200) {
                                            if (response.getJSONObject("response").getBoolean("response")) {
                                                selectedMedicineName.setText(medicationName.getText().toString().trim());
                                                medicationName.setText("");
                                                medicationCompanyName.setText("");
                                                Toast.makeText(MedicationActivity.this, "Medicine successfully saved", Toast.LENGTH_LONG).show();
                                                progressBar.setVisibility(View.VISIBLE);
                                                getDropDownDetails();
                                            } else {
                                                Toast.makeText(MedicationActivity.this, "Unexpected error has occured please try again", Toast.LENGTH_LONG).show();
                                            }
                                        } else {
                                            ErrorHandlerClass.INSTANCE.errorHandler(MedicationActivity.this, s);
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    } else {
                        globalClass.noInternetConnection.showDialog(MedicationActivity.this);
                    }
                }
            }
        });

        medicationSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence medicineName, int i, int i1, int i2) {
                if (medicineName.length() == 0) {
                    medicineString = "";
                    medicationSearchResult.setText("Search for medication");
                }
                if (medicineName.length() > 2) {
                    if (globalClass.isOnline()) {
                        appointmentViewModel.searchMedicines(MedicationActivity.this, medicineName.toString()).observe(MedicationActivity.this, new Observer<String>() {
                            @Override
                            public void onChanged(String s) {
                                try {
                                    JSONObject jsonObject = new JSONObject(s);
                                    if (jsonObject.getInt("status_code") == 200) {
                                        JSONArray medicineDetails = jsonObject.getJSONObject("response").getJSONArray("response");
                                        if (medicineDetails.length() == 0) {
                                            medicationSearchResult.setText("Medicine Not Found");

                                        } else {
                                            medicineModelList.clear();
                                            for (int i = 0; i < medicineDetails.length(); i++) {
                                                JSONObject medicine = medicineDetails.getJSONObject(i);
                                                medicineModelList.add(new MedicineModel(medicine.getInt("id"), medicine.getString("name"), medicine.getString("company")));
                                            }
                                            medicineSearchAdapter.notifyDataSetChanged();
                                        }

                                    }
                                    else
                                    {
                                        ErrorHandlerClass.INSTANCE.errorHandler(MedicationActivity.this, s);
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });

                    } else {
                        globalClass.noInternetConnection.showDialog(MedicationActivity.this);
                    }
                }

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().length() == 0) {
                    medicineString = "";
                    medicationSearchResult.setText("Search Medicine");
                }
            }
        });


    }

    public void getDropDownDetails() {
        appointmentViewModel.getRecordStructure(MedicationActivity.this, "38").observe(MedicationActivity.this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                JSONObject response = null;
                try {
                    response = new JSONObject(s);
                    if (response.getInt("status_code") == 200) {
                        frequencyList.clear();
                        intakeList.clear();
                        JSONArray responseArray = response.getJSONObject("response").getJSONArray("response");
                        String frequencyString = responseArray.getJSONObject(4).getJSONArray("field_values").getString(0);
                        String intakeMethodString = responseArray.getJSONObject(10).getJSONArray("field_values").getString(0);
                        String[] frequencyArray = frequencyString.split(",");
                        String[] intakeArray = intakeMethodString.split(",");

                        frequencyList.add("Select frequency");
                        intakeList.add("Select InTake method");
                        Collections.addAll(frequencyList, frequencyArray);
                        Collections.addAll(intakeList, intakeArray);
                        frequencyAdapter.notifyDataSetChanged();
                        inTakeAdapter.notifyDataSetChanged();
                        progressBar.setVisibility(View.GONE);
                        medicationSearchLayout.setVisibility(View.GONE);
                        medicationDetailsLayout.setVisibility(View.VISIBLE);
                    } else {
                        ErrorHandlerClass.INSTANCE.errorHandler(MedicationActivity.this, s);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });
    }
}