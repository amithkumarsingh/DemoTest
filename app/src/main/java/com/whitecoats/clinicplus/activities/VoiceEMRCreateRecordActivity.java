package com.whitecoats.clinicplus.activities;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.text.InputType;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.DialogFragment;

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
import com.google.android.gms.common.util.IOUtils;
import com.whitecoats.clinicplus.AppUtilities;
import com.whitecoats.clinicplus.R;
import com.whitecoats.clinicplus.SplashActivity;
import com.whitecoats.clinicplus.VolleyCallback;
import com.whitecoats.clinicplus.apis.ApiGetPostMethodCalls;
import com.whitecoats.clinicplus.apis.ApiUrls;
import com.whitecoats.clinicplus.models.VoiceEMRModel;
import com.whitecoats.clinicplus.utils.ErrorHandlerClass;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class VoiceEMRCreateRecordActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    private String categoryName, categoryId;
    private LinearLayout formLayout;
    private Button submitRecord;
    private TextView dateTv;
    private HashMap<Integer, String> mandatoryField;
    private Uri fileUri;
    private ImageView formImage;
    ProgressDialog loadingDialog;
    private int formImageID = 0;
    private String uploadImageResponse;
    private ArrayList<TextView> taskHiddenTv;
    private JSONObject taskRepeatObj;
    private String callingActivity;
    private ProgressDialog otpLoading;

    //task hidden field
    private ArrayList<EditText> taskHiddenEt;
    private ArrayList<Spinner> taskHidderSpinner;

    //investigation hidden field
    private ArrayList<TextView> investDateList;
    private ArrayList<EditText> investEditText;

    //immunization hidden field
    private ArrayList<TextView> immuDateList;
    private ArrayList<EditText> immuEditText;

    //problems hidden field
    private ArrayList<TextView> probEditText;

    //prcedure hidden field
    private ArrayList<TextView> proceedTextView;

    private JSONArray catFieldObjArr;

    private static final int PERMISSION_CALLBACK_CONSTANT = 100;
    private static final int READ_EXTERNAL_STORAGE_CONSTANT = 102;
    private final String[] permissionsRequired = new String[]{Manifest.permission.CAMERA};
    private final String[] storagePermission = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE};

    private String imageFilePath;
    private boolean isPDFFile = false;
    private File pdfFile;

    private int patientID, episodeID, encounterID;
    private String type;
    RelativeLayout emrRecordFormLoadProgressbar;

    String ObservationEditRecord = "", TreatmentEditRecord = "";
    private int recordPosition;
    String filedData = "";
    private VoiceEMRModel emrObject;
    /* ENGG-3754 -- Continuation of Refactoring the deprecated function in Clinic+ App(Android) */
    private ActivityResultLauncher<Intent> launchOpenFileResults, launcherCameraResults;
    private AppUtilities appUtils;
    private String timeFormat = "HH:mm";
    private boolean is24Hrformat = true;
    private SimpleDateFormat simpleDateFormat;
    private ApiGetPostMethodCalls apiGetPostMethodCalls;

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    private final String[] permissionsRequiredSDK33Higher = new String[]{Manifest.permission.READ_MEDIA_IMAGES, Manifest.permission.READ_MEDIA_VIDEO,
            Manifest.permission.READ_MEDIA_AUDIO};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_create_record2);

        formLayout = findViewById(R.id.recordCreateFormLayout);
        emrRecordFormLoadProgressbar = findViewById(R.id.emrRecordFormLoadProgressbar);
        submitRecord = findViewById(R.id.recordCreateSubmit);
        categoryId = getIntent().getStringExtra("CategoryId");
        categoryName = getIntent().getStringExtra("CategoryName");
        type = getIntent().getStringExtra("Type");
        patientID = getIntent().getIntExtra("PatientId", 0);
        episodeID = getIntent().getIntExtra("EpisodeId", 0);
        encounterID = getIntent().getIntExtra("EncounterID", 0);
        if (getIntent().getStringExtra("ObservationEditRecord") != null) {
            ObservationEditRecord = getIntent().getStringExtra("ObservationEditRecord");
        }
        if (getIntent().getStringExtra("TreatmentEditRecord") != null) {
            TreatmentEditRecord = getIntent().getStringExtra("TreatmentEditRecord");
        }
        recordPosition = getIntent().getIntExtra("recordPosition", 0);
        emrObject = (VoiceEMRModel) getIntent().getSerializableExtra("VoiceEMRModel");
        apiGetPostMethodCalls = new ApiGetPostMethodCalls();
        mandatoryField = new HashMap<>();
        loadingDialog = new ProgressDialog(this);
        formImage = new ImageView(this);
        taskHiddenTv = new ArrayList<>();
        taskHiddenEt = new ArrayList<>();
        taskHidderSpinner = new ArrayList<>();
        taskRepeatObj = new JSONObject();
        investDateList = new ArrayList<>();
        investEditText = new ArrayList<>();
        immuDateList = new ArrayList<>();
        immuEditText = new ArrayList<>();
        catFieldObjArr = new JSONArray();
        probEditText = new ArrayList<>();
        proceedTextView = new ArrayList<>();
        callingActivity = getIntent().getStringExtra("ActivityName");
//        Log.d("Category ID", categoryId);
//        Log.d("Category ID", categoryName);

        Toolbar toolbar = findViewById(R.id.recordCreate2Toolbar);
        toolbar.setTitle(categoryName);
        final Drawable backArrow = getResources().getDrawable(R.drawable.ic_arrow_back);
        backArrow.setColorFilter(getResources().getColor(R.color.colorWhite), PorterDuff.Mode.SRC_ATOP);
        toolbar.setNavigationIcon(backArrow); // your drawable
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed(); // Implemented by activity
            }
        });

        appUtils = new AppUtilities();
        if (appUtils.timeFormatPreferences(getApplicationContext()) == 12) {
            timeFormat = "hh:mm aa";
            is24Hrformat = false;
        }
        simpleDateFormat = new SimpleDateFormat(timeFormat);
        DateFormatSymbols symbols = new DateFormatSymbols(Locale.US);
        simpleDateFormat.setDateFormatSymbols(symbols);

        /* ENGG-3754 -- Continuation of Refactoring the deprecated function in Clinic+ App(Android) */
        //Start
        launcherCameraResults = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    //Request code 1
                    Intent data = result.getData();
                    int resultCode = result.getResultCode();
                    if (resultCode == Activity.RESULT_OK) {
//                    Uri selectedImage = fileUri;
//                    getContentResolver().notifyChange(selectedImage, null);
////                    ImageView imageView = (ImageView) findViewById(R.id.ImageView);
//                    ContentResolver cr = getContentResolver();
////                    Bitmap bitmap;
//                    try {
//                        bitmap = android.provider.MediaStore.Images.Media
//                                .getBitmap(cr, selectedImage);
//                        bitmap = decodeFile(new File(fileUri.getPath()));
//
//                        formImage.setVisibility(View.VISIBLE);
//                        formImage.setImageBitmap(bitmap);
//                        uploadImage();
//                    } catch (Exception e) {
//                        Toast.makeText(this, "Failed To Load Image", Toast.LENGTH_SHORT)
//                                .show();
//                        Log.e("Camera", e.toString());
//                    }

                        //new
                        try {
                            ContentResolver contentResolver = getContentResolver();

                            // Use the content resolver to open camera taken image input stream through image uri.
                            InputStream inputStream = contentResolver.openInputStream(fileUri);

                            // Decode the image input stream to a bitmap use BitmapFactory.
                            Bitmap pictureBitmap = BitmapFactory.decodeStream(inputStream);
                            int nh = (int) (pictureBitmap.getHeight() * (720.0 / pictureBitmap.getWidth()));
                            Bitmap scaled = Bitmap.createScaledBitmap(pictureBitmap, 720, nh, true);
                            formImage.setImageBitmap(scaled);

                            // Set the camera taken image bitmap clinicplus the image view component to display.
                            formImage.setVisibility(View.VISIBLE);
                            uploadImage();
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
                        Log.i("filenme", fileName);

                        if (fileName.endsWith(".jpg") || fileName.endsWith(".jpeg") || fileName.endsWith(".png") || fileName.endsWith(".pdf")) {
                            try {
                                if (fileName.contains("pdf")) {
                                    isPDFFile = true;
                                    uploadPDFFile(fileName, uri);
                                } else {
                                    Bitmap bitmapImage = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                                    int nh = (int) (bitmapImage.getHeight() * (720.0 / bitmapImage.getWidth()));
                                    Bitmap scaled = Bitmap.createScaledBitmap(bitmapImage, 720, nh, true);
                                    formImage.setImageBitmap(scaled);

                                    formImage.setVisibility(View.VISIBLE);
                                    uploadImage();
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        } else {
                            Toast.makeText(VoiceEMRCreateRecordActivity.this, "Please Upload image or pdf files only", Toast.LENGTH_LONG).show();
                        }

                    }
                });
        //End

        getRecordForm(categoryId);

        submitRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (categoryName.equalsIgnoreCase(getResources().getString(R.string.common_task))) {
                    getTaskFormData();
                } else {
                    getFormData();
                }

            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void getRecordForm(String id) {
        String url = ApiUrls.getRecordStructure + "?record_category_id=" + id;

        // prepare the Request
        apiGetPostMethodCalls.volleyApiRequestData(url, Request.Method.GET, null, VoiceEMRCreateRecordActivity.this,
                new VolleyCallback() {
                    @Override
                    public void onSuccess(String result) {
                        emrRecordFormLoadProgressbar.setVisibility(View.GONE);
                        try {
                            JSONObject response = new JSONObject(result);
                            if (categoryName.equalsIgnoreCase(getResources().getString(R.string.common_task))) {
                                JSONObject taskObj = response.getJSONObject("response");
                                for (int i = 0; i < taskObj.length(); i++) {
                                    if (taskObj.has(i + "")) {
                                        JSONObject fieldObj = taskObj.getJSONObject(i + "");
                                        filedData = "";
                                        if (fieldObj.get("id") instanceof Integer) {
                                            int fieldId = fieldObj.getInt("id");
                                            String fieldName = fieldObj.getString("field");
                                            JSONArray fieldType = fieldObj.getJSONArray("field_type");
                                            fieldObj = fieldType.getJSONObject(0);
                                            int type = fieldObj.getInt("type");
                                            int isMand = fieldObj.getInt("is_mandatory");

                                            fieldObj = taskObj.getJSONObject(i + "");
                                            String fieldValues = fieldObj.getJSONArray("field_values").getString(0);
                                            String defaults = fieldObj.getString("default_value");

                                            if (ObservationEditRecord.equalsIgnoreCase("ObservationEditRecord")) {
                                                try {
                                                    String observationRecords = emrObject.getObservationCategoryRecords();
                                                    JSONObject observationRecordsJsonObject = new JSONObject(observationRecords);
                                                    filedData = observationRecordsJsonObject.getString(String.valueOf(fieldId));
                                                } catch (Exception e) {

                                                }

                                                createTaskForm(fieldId, fieldName, type, isMand, fieldValues, filedData);

                                            } else if (TreatmentEditRecord.equalsIgnoreCase("TreatmentEditRecord")) {
                                                try {
                                                    String treatmentRecords = emrObject.getTreatmentCategoryRecords();
                                                    JSONObject treatmentRecordsJsonObject = new JSONObject(treatmentRecords);
                                                    filedData = treatmentRecordsJsonObject.getString(String.valueOf(fieldId));
                                                } catch (Exception e) {

                                                }

                                                createTaskForm(fieldId, fieldName, type, isMand, fieldValues, filedData);

                                            } else {
                                                createTaskForm(fieldId, fieldName, type, isMand, fieldValues, filedData);
                                            }
                                        } else {
                                            taskRepeatObj = taskObj.getJSONObject(i + "");
                                        }
                                    }
                                }
                            } else {

                                JSONArray formArr = response.getJSONArray("response");
                                for (int i = 0; i < formArr.length(); i++) {
                                    JSONObject fieldObj = formArr.getJSONObject(i);
                                    filedData = "";
                                    int fieldId = fieldObj.getInt("id");
                                    String fieldName = fieldObj.getString("field");
                                    JSONArray fieldType = fieldObj.getJSONArray("field_type");
                                    fieldObj = fieldType.getJSONObject(0);
                                    int type = fieldObj.getInt("type");
                                    int isMand = fieldObj.getInt("is_mandatory");

                                    fieldObj = formArr.getJSONObject(i);
                                    String fieldValues = fieldObj.getJSONArray("field_values").getString(0);
                                    String defaults = fieldObj.getString("default_value");


                                    if (ObservationEditRecord.equalsIgnoreCase("ObservationEditRecord")) {
                                        try {
                                            String observationRecord = emrObject.getObservationCategoryRecords();
                                            JSONObject observationRecordJsonObject = new JSONObject(observationRecord);
                                            filedData = observationRecordJsonObject.getString(String.valueOf(fieldId));
                                        } catch (Exception e) {

                                        }

                                        createformElement(fieldId, fieldName, type, isMand, fieldValues, defaults, filedData);

                                    } else if (TreatmentEditRecord.equalsIgnoreCase("TreatmentEditRecord")) {
                                        try {
                                            String treatmentRecord = emrObject.getTreatmentCategoryRecords();
                                            JSONObject treatmentRecordJsonObject = new JSONObject(treatmentRecord);
                                            filedData = treatmentRecordJsonObject.getString(String.valueOf(fieldId));
                                        } catch (Exception e) {

                                        }

                                        createformElement(fieldId, fieldName, type, isMand, fieldValues, defaults, filedData);

                                    } else {
                                        createformElement(fieldId, fieldName, type, isMand, fieldValues, defaults, filedData);
                                    }
                                    JSONObject catFieldObj = new JSONObject();
                                    catFieldObj.put("id", fieldId);
                                    catFieldObj.put("name", fieldName);
                                    catFieldObj.put("type", type);
                                    catFieldObj.put("isMand", isMand);
                                    catFieldObj.put("values", fieldValues);
                                    catFieldObj.put("defaults", defaults);
                                    catFieldObjArr.put(catFieldObj);
                                }
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(String err) {
                        ErrorHandlerClass.INSTANCE.errorHandler(VoiceEMRCreateRecordActivity.this, err);
                    }
                });
    }

    private void createformElement(int fieldId, String fieldName, int fieldType, int isMandatory, String fieldValue, String defaults, String fieldData) {
        String[] valueArr = fieldValue.split(",");
        if (valueArr.length > 1) {
            fieldType = 4;
        }

        switch (fieldType) {
            case 1:
                EditText editText = new EditText(this);
                // Create a LayoutParams for TextView
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT, // Width of TextView
                        150);
                lp.setMargins(0, 30, 0, 0);
                editText.setLayoutParams(lp);
                editText.setBackground(getResources().getDrawable(R.drawable.drawable_rectangle_shape));
                editText.setPadding(40, 10, 10, 10);
                editText.setHint(fieldName);
                editText.setId(fieldId);
                if (fieldData.length() > 0) {
                    editText.setText(fieldData);
                }
                if (isMandatory == 1) {
                    editText.setHint(fieldName + getResources().getString(R.string.mandatory_hint));
                    mandatoryField.put(fieldId, fieldName);
                    if (fieldData.length() > 0) {
                        editText.setText(fieldData);
                    }
                    editText.setBackground(getResources().getDrawable(R.drawable.drawable_rectangle_shape_red));
                }

                if (categoryId.equalsIgnoreCase("5")) {
                    investEditText.add(editText);
                } else if (categoryId.equalsIgnoreCase("2")) {
                    if (fieldId == 18) {
                        immuEditText.add(editText);
                    }

                }

                formLayout.addView(editText);
                break;

            case 2:
                editText = new EditText(this);
                // Create a LayoutParams for TextView
                lp = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT, // Width of TextView
                        ViewGroup.LayoutParams.WRAP_CONTENT);
                lp.setMargins(0, 30, 0, 0);
                editText.setLayoutParams(lp);
                editText.setBackground(getResources().getDrawable(R.drawable.drawable_rectangle_shape));
                editText.setPadding(40, 10, 10, 10);
                editText.setHint(fieldName);
                editText.setId(fieldId);
                if (fieldData.length() > 0) {
                    editText.setText(fieldData);
                }
                if (isMandatory == 1) {
                    editText.setHint(fieldName + getResources().getString(R.string.mandatory_hint));
                    mandatoryField.put(fieldId, fieldName);
                    if (fieldData.length() > 0) {
                        editText.setText(fieldData);
                    }
                    editText.setBackground(getResources().getDrawable(R.drawable.drawable_rectangle_shape_red));
                }

                editText.setSingleLine(false);
                editText.setImeOptions(EditorInfo.IME_FLAG_NO_ENTER_ACTION);
                editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
                editText.setLines(5);
                editText.setMaxLines(10);
                editText.setVerticalScrollBarEnabled(true);
                editText.setMovementMethod(ScrollingMovementMethod.getInstance());
                editText.setScrollBarStyle(View.SCROLLBARS_INSIDE_INSET);
                formLayout.addView(editText);
                break;

            case 3:
                editText = new EditText(this);
                // Create a LayoutParams for TextView
                lp = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT, // Width of TextView
                        150);
                lp.setMargins(0, 30, 0, 0);
                editText.setLayoutParams(lp);
                editText.setBackground(getResources().getDrawable(R.drawable.drawable_rectangle_shape));
                editText.setPadding(40, 10, 10, 10);
                editText.setHint(fieldName);
                editText.setId(fieldId);
                if (fieldData.length() > 0) {
                    editText.setText(fieldData);
                }
                editText.setInputType(InputType.TYPE_CLASS_NUMBER);
                if (isMandatory == 1) {
                    editText.setHint(fieldName + getResources().getString(R.string.mandatory_hint));
                    mandatoryField.put(fieldId, fieldName);
                    if (fieldData.length() > 0) {
                        editText.setText(fieldData);
                    }
                    editText.setBackground(getResources().getDrawable(R.drawable.drawable_rectangle_shape_red));
                }

                formLayout.addView(editText);
                break;

            case 4:

                TextView textView = new TextView(this);
                lp = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT, // Width of TextView
                        ViewGroup.LayoutParams.WRAP_CONTENT);
                lp.setMargins(0, 30, 0, 0);
                textView.setLayoutParams(lp);
                textView.setText(fieldName);
                textView.setTypeface(null, Typeface.BOLD);
                formLayout.addView(textView);

                valueArr = fieldValue.split(",");
                ArrayList<String> spinnerArray = new ArrayList<String>();
                for (int i = 0; i < valueArr.length; i++) {
                    spinnerArray.add(valueArr[i]);
                }
                spinnerArray.add(0, getResources().getString(R.string.select_option));
                Spinner spinner = new Spinner(this);
                ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, spinnerArray);
                spinner.setAdapter(spinnerArrayAdapter);
                spinner.setId(fieldId);

                spinner.setBackground(getResources().getDrawable(R.drawable.drawable_rectangle_shape));
                if (isMandatory == 1) {
                    mandatoryField.put(fieldId, fieldName);
                    spinner.setBackground(getResources().getDrawable(R.drawable.drawable_rectangle_shape_red));
                }
                spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                        if (categoryId.equalsIgnoreCase("5")) {
                            showHiddenInvestField(i);
                        } else if (categoryId.equalsIgnoreCase("2")) {
                            showHiddenImmuField(i);
                        } else if (categoryId.equalsIgnoreCase("14")) {
                            showHiddenProblemField(i);
                        } else if (categoryId.equalsIgnoreCase("4")) {
                            showHiddenProcedureField(i);
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {

                    }
                });

                if (fieldData.length() > 0) {
                    int ii = 0;
                    for (ii = 0; ii < spinnerArray.size(); ii++) ;
                    {
                        int position = -1;
                        position = spinnerArray.indexOf(fieldData);
                        if (position == -1) {
                        } else {
                            spinner.setSelection(position);
                        }
                    }


                } else {
                    spinner.setSelection(getIndex(spinner, defaults));
                }

                lp = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT, // Width of TextView
                        150);
                lp.setMargins(0, 30, 0, 0);
//                spinner.setLayoutParams(lp);
//                spinner.setBackground(getResources().getDrawable(R.drawable.rectangle_view));
                formLayout.addView(spinner);
                break;

            case 5:
                LinearLayout linearLayout = new LinearLayout(this);
                lp = new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, // Width of TextView
                        150);
                linearLayout.setLayoutParams(lp);
                linearLayout.setWeightSum(1);
                linearLayout.setOrientation(LinearLayout.HORIZONTAL);

                ImageView imageView = new ImageView(this);
                lp = new LinearLayout.LayoutParams(
                        0, // Width of TextView
                        150);
                lp.setMargins(0, 30, 0, 0);
                lp.weight = 0.4f;
                imageView.setLayoutParams(lp);
                imageView.setImageResource(R.drawable.ic_attachment);
                linearLayout.addView(imageView);
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            if (ActivityCompat.checkSelfPermission(VoiceEMRCreateRecordActivity.this, permissionsRequiredSDK33Higher[0]) == PackageManager.PERMISSION_GRANTED) {
                                openFileDialog();
                            } else {
                                ActivityCompat.requestPermissions(VoiceEMRCreateRecordActivity.this, permissionsRequiredSDK33Higher, READ_EXTERNAL_STORAGE_CONSTANT);
                            }
                        } else {
                            if (ActivityCompat.checkSelfPermission(VoiceEMRCreateRecordActivity.this, storagePermission[0]) == PackageManager.PERMISSION_GRANTED) {
                                openFileDialog();
                            } else {
                                ActivityCompat.requestPermissions(VoiceEMRCreateRecordActivity.this, storagePermission, READ_EXTERNAL_STORAGE_CONSTANT);
                            }
                        }

                    }
                });

                textView = new TextView(this);
                textView.setText("OR");
                textView.setTypeface(null, Typeface.BOLD);
                linearLayout.addView(textView);

                imageView = new ImageView(this);
                lp = new LinearLayout.LayoutParams(
                        0, // Width of TextView
                        150);
                lp.setMargins(0, 30, 0, 0);
                lp.weight = 0.4f;
                imageView.setLayoutParams(lp);
                imageView.setImageResource(R.drawable.ic_camera);
                linearLayout.addView(imageView);
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (ActivityCompat.checkSelfPermission(VoiceEMRCreateRecordActivity.this, permissionsRequired[0]) == PackageManager.PERMISSION_GRANTED) {
                            openCamera();
                        } else {
                            ActivityCompat.requestPermissions(VoiceEMRCreateRecordActivity.this, permissionsRequired, PERMISSION_CALLBACK_CONSTANT);
                        }
                    }
                });

                if (isMandatory == 1) {
                    mandatoryField.put(fieldId, fieldName);
                }
                linearLayout.setGravity(Gravity.CENTER);
                linearLayout.setId(fieldId);
                formImageID = fieldId;
                formLayout.addView(linearLayout);

                lp = new LinearLayout.LayoutParams(
                        500, // Width of TextView
                        500);
                lp.gravity = Gravity.CENTER;
                formImage.setLayoutParams(lp);
                formImage.setLayoutParams(lp);
                formImage.setVisibility(View.GONE);
                formLayout.addView(formImage);
                break;

            case 7:
                CheckBox checkBox = new CheckBox(this);

                linearLayout = new LinearLayout(this);
                checkBox.setBackground(getResources().getDrawable(R.drawable.drawable_rectangle_shape));
                lp = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT, // Width of TextView
                        150);
                lp.setMargins(0, 30, 0, 0);
                checkBox.setText(fieldName);
                checkBox.setId(fieldId);
                if (isMandatory == 1) {
                    mandatoryField.put(fieldId, fieldName);
                    checkBox.setBackground(getResources().getDrawable(R.drawable.drawable_rectangle_shape_red));
                }
                linearLayout.setLayoutParams(lp);
                linearLayout.addView(checkBox);
                checkBox.setLayoutParams(lp);
                if (checkBox.getParent() != null) {
                    ((ViewGroup) checkBox.getParent()).removeView(checkBox); // <- fix
                }

                if (fieldData.length() > 0) {
                    if (fieldData.equalsIgnoreCase("1")) {
                        checkBox.setChecked(true);
                    } else {
                        checkBox.setChecked(false);
                    }
                }

                formLayout.addView(checkBox);
                break;

            case 8:
                dateTv = new TextView(this);
                lp = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT, // Width of TextView
                        150);
                lp.setMargins(0, 30, 0, 0);
                dateTv.setLayoutParams(lp);
                dateTv.setHint(fieldName);
                dateTv.setId(fieldId);
                dateTv.setBackground(getResources().getDrawable(R.drawable.drawable_rectangle_shape));
                dateTv.setPadding(40, 40, 10, 10);
                dateTv.setTextSize(17);
                DateFormat df = new SimpleDateFormat("dd-MM-yyyy");
                String date = df.format(Calendar.getInstance().getTime());
                dateTv.setText(date);
                if (fieldData.length() > 0) {
                    dateTv.setText(fieldData);
                }
                formLayout.addView(dateTv);
                if (categoryId.equalsIgnoreCase("5")) {
//                    Log.d("Investigation", "***********");
                    investDateList.add(dateTv);
                    if (fieldId == 43) {
//                        Log.d("Investigation", "###############");
                        dateTv.setVisibility(View.GONE);
                    }
                } else if (categoryId.equalsIgnoreCase("2")) {
                    immuDateList.add(dateTv);
                    if (fieldId == 242) {
                        dateTv.setVisibility(View.GONE);
                    }
                } else if (categoryId.equalsIgnoreCase("14")) {
                    if (fieldId == 114) {
                        probEditText.add(dateTv);
                    }
                } else if (categoryId.equalsIgnoreCase("4")) {
                    if (fieldId == 33) {
                        proceedTextView.add(dateTv);
                    }
                }

                dateTv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        datePicker((TextView) view);
                    }
                });
                break;

            case 12:
                TextView textview = new TextView(this);
                lp = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT, // Width of TextView
                        150);
                lp.setMargins(0, 30, 0, 0);
                textview.setLayoutParams(lp);
                textview.setHint(fieldName);
                textview.setId(fieldId);
                textview.setBackground(getResources().getDrawable(R.drawable.drawable_rectangle_shape));
                textview.setPadding(40, 40, 10, 10);
                textview.setTextSize(17);
                //df = new SimpleDateFormat("HH:mm");
                String time = simpleDateFormat.format(Calendar.getInstance().getTime());
                textview.setText(time);
                if (fieldData.length() > 0) {
                    if (fieldName.equalsIgnoreCase("Time")) {
                        textview.setText(appUtils.formatTimeBasedOnPreference(getApplicationContext(), "HH:mm", fieldData));
                    } else {
                        textview.setText(fieldData);
                    }
                }
                formLayout.addView(textview);

                textview.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        timePicker((TextView) view);
                    }
                });
                break;


        }

    }

    private void createTaskForm(int fieldId, String fieldName, int fieldType, int isMandatory, String fieldValue, String fieldData) {

        switch (fieldType) {
            case 1:
                EditText editText = new EditText(this);
                // Create a LayoutParams for TextView
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT, // Width of TextView
                        150);
                lp.setMargins(0, 30, 0, 0);
                editText.setLayoutParams(lp);
                editText.setBackground(getResources().getDrawable(R.drawable.drawable_rectangle_shape));
                editText.setPadding(40, 10, 10, 10);
                editText.setHint(fieldName);
                editText.setId(fieldId);
                if (fieldData.length() > 0) {
                    editText.setText(fieldData);
                }
                if (isMandatory == 1) { //2
                    editText.setHint(fieldName + getResources().getString(R.string.mandatory_hint));
                    mandatoryField.put(fieldId, fieldName);
                    if (fieldData.length() > 0) {
                        editText.setText(fieldData);
                    }
                    editText.setBackground(getResources().getDrawable(R.drawable.drawable_rectangle_shape_red));
                }

                formLayout.addView(editText);
                break;

            case 2:
                editText = new EditText(this);
                // Create a LayoutParams for TextView
                lp = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT, // Width of TextView
                        ViewGroup.LayoutParams.WRAP_CONTENT);
                lp.setMargins(0, 30, 0, 0);
                editText.setLayoutParams(lp);
                editText.setBackground(getResources().getDrawable(R.drawable.drawable_rectangle_shape));
                editText.setPadding(40, 10, 10, 10);
                editText.setHint(fieldName);
                editText.setId(fieldId);
                if (fieldData.length() > 0) {
                    editText.setText(fieldData);
                }

                if (isMandatory == 2) {
                    editText.setHint(fieldName + getResources().getString(R.string.mandatory_hint));
                    mandatoryField.put(fieldId, fieldName);
                    if (fieldData.length() > 0) {
                        editText.setText(fieldData);
                    }
                    editText.setBackground(getResources().getDrawable(R.drawable.drawable_rectangle_shape_red));
                }

                editText.setSingleLine(false);
                editText.setImeOptions(EditorInfo.IME_FLAG_NO_ENTER_ACTION);
                editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
                editText.setLines(5);
                editText.setMaxLines(10);
                editText.setVerticalScrollBarEnabled(true);
                editText.setMovementMethod(ScrollingMovementMethod.getInstance());
                editText.setScrollBarStyle(View.SCROLLBARS_INSIDE_INSET);


                formLayout.addView(editText);
                break;

            case 3:
                editText = new EditText(this);
                // Create a LayoutParams for TextView
                lp = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT, // Width of TextView
                        150);
                lp.setMargins(0, 30, 0, 0);
                editText.setLayoutParams(lp);
                editText.setBackground(getResources().getDrawable(R.drawable.drawable_rectangle_shape));
                editText.setPadding(40, 10, 10, 10);
                editText.setHint(fieldName);
                editText.setId(fieldId);
                if (fieldData.length() > 0) {
                    editText.setText(fieldData);
                }
                editText.setInputType(InputType.TYPE_CLASS_NUMBER);
                if (isMandatory == 2) {
                    editText.setHint(fieldName + getResources().getString(R.string.mandatory_hint));
                    mandatoryField.put(fieldId, fieldName);
                    if (fieldData.length() > 0) {
                        editText.setText(fieldData);
                    }
                    editText.setBackground(getResources().getDrawable(R.drawable.drawable_rectangle_shape_red));
                }

                taskHiddenEt.add(editText);

                formLayout.addView(editText);
                break;

            case 4:

                TextView textView = new TextView(this);
                lp = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT, // Width of TextView
                        ViewGroup.LayoutParams.WRAP_CONTENT);
                lp.setMargins(0, 30, 0, 0);
                textView.setLayoutParams(lp);
                textView.setText(fieldName);
                textView.setTypeface(null, Typeface.BOLD);
                formLayout.addView(textView);

                final String[] valueArr = fieldValue.split(",");
                final ArrayList<String> spinnerArray = new ArrayList<String>();
                for (int i = 0; i < valueArr.length; i++) {
                    spinnerArray.add(valueArr[i]);
                }
                spinnerArray.add(0, getResources().getString(R.string.select_option));
                final Spinner spinner = new Spinner(this);
                ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, spinnerArray);
                spinner.setAdapter(spinnerArrayAdapter);
                spinner.setId(fieldId);
                if (fieldData.length() > 0) {
                    int ii = 0;
                    for (ii = 0; ii < spinnerArray.size(); ii++) ;
                    {
                        if (spinnerArray.get(ii).equalsIgnoreCase(fieldData)) {
                            spinner.setSelection(ii);
                        }
                    }
                }
                spinner.setBackground(getResources().getDrawable(R.drawable.drawable_rectangle_shape));
                if (isMandatory == 2 || isMandatory == 1) {
                    mandatoryField.put(fieldId, fieldName);
                    spinner.setBackground(getResources().getDrawable(R.drawable.drawable_rectangle_shape_red));
                }
//                spinner.setSelection(getIndex(spinner, defaults));

                lp = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT, // Width of TextView
                        150);
                lp.setMargins(0, 30, 0, 0);
//                spinner.setLayoutParams(lp);
//                spinner.setBackground(getResources().getDrawable(R.drawable.rectangle_view));
                spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
//                        Log.d("Selected ", spinnerArray.get(i));
                        if (spinnerArray.get(i).equalsIgnoreCase(getResources().getString(R.string.repeat))) {
                            showTaskHiddenField(View.VISIBLE);
                        } else if (spinnerArray.get(i).equalsIgnoreCase(getResources().getString(R.string.one_time))) {
                            showTaskHiddenField(View.GONE);
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {

                    }
                });
//                Log.d("Value @@@@@@@@@@2", valueArr[0]);
                if (valueArr[0].equalsIgnoreCase("Minutes")) {
                    taskHidderSpinner.add(spinner);
                    taskHiddenTv.add(textView);
                }
                formLayout.addView(spinner);
                break;

            case 5:
                LinearLayout linearLayout = new LinearLayout(this);
                lp = new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, // Width of TextView
                        150);
                linearLayout.setLayoutParams(lp);
                linearLayout.setWeightSum(1);
                linearLayout.setOrientation(LinearLayout.HORIZONTAL);

                ImageView imageView = new ImageView(this);
                lp = new LinearLayout.LayoutParams(
                        0, // Width of TextView
                        150);
                lp.setMargins(0, 30, 0, 0);
                lp.weight = 0.4f;
                imageView.setLayoutParams(lp);
                imageView.setImageResource(R.drawable.ic_attachment);
                linearLayout.addView(imageView);
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        openFileDialog();
                    }
                });

                textView = new TextView(this);
                textView.setText("OR");
                textView.setTypeface(null, Typeface.BOLD);
                linearLayout.addView(textView);

                imageView = new ImageView(this);
                lp = new LinearLayout.LayoutParams(
                        0, // Width of TextView
                        150);
                lp.setMargins(0, 30, 0, 0);
                lp.weight = 0.4f;
                imageView.setLayoutParams(lp);
                imageView.setImageResource(R.drawable.ic_camera);
                linearLayout.addView(imageView);
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        openCamera();
                    }
                });

                if (isMandatory == 1) {
                    mandatoryField.put(fieldId, fieldName);
                }
                linearLayout.setGravity(Gravity.CENTER);
                linearLayout.setId(fieldId);
                formImageID = fieldId;
                formLayout.addView(linearLayout);

                lp = new LinearLayout.LayoutParams(
                        500, // Width of TextView
                        500);
                lp.gravity = Gravity.CENTER;
                formImage.setLayoutParams(lp);
                formImage.setLayoutParams(lp);
                formImage.setVisibility(View.GONE);
                formLayout.addView(formImage);
                break;

            case 7:
                CheckBox checkBox = new CheckBox(this);
                linearLayout = new LinearLayout(this);
                linearLayout.setBackground(getResources().getDrawable(R.drawable.drawable_rectangle_shape));
                lp = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT, // Width of TextView
                        150);
                lp.setMargins(0, 30, 0, 0);
                checkBox.setText(fieldName);
                checkBox.setId(fieldId);
                if (isMandatory == 1) {
                    mandatoryField.put(fieldId, fieldName);
                    linearLayout.setBackground(getResources().getDrawable(R.drawable.drawable_rectangle_shape_red));
                }
                linearLayout.setLayoutParams(lp);
                linearLayout.addView(checkBox);
                formLayout.addView(linearLayout);
                break;

            case 8:
                TextView dateTv = new TextView(this);
                lp = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT, // Width of TextView
                        150);
                lp.setMargins(0, 30, 0, 0);
                dateTv.setLayoutParams(lp);
                dateTv.setHint(fieldName);
                dateTv.setId(fieldId);
                dateTv.setBackground(getResources().getDrawable(R.drawable.drawable_rectangle_shape));
                dateTv.setPadding(40, 40, 10, 10);
                dateTv.setTextSize(17);
                DateFormat df = new SimpleDateFormat("dd-MM-yyyy");
                String date = df.format(Calendar.getInstance().getTime());
                dateTv.setText(date);
                if (fieldData.length() > 0) {
                    dateTv.setText(fieldData);
                }
                if (fieldId == 195) {
                    dateTv.setText(fieldName);
                    if (isMandatory == 2) {
                        mandatoryField.put(fieldId, fieldName);
                    }
                    dateTv.setVisibility(View.GONE);
                    taskHiddenTv.add(dateTv);
                }
                formLayout.addView(dateTv);

                dateTv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        datePicker((TextView) view);
                    }
                });
                break;

            case 12:
                TextView textview = new TextView(this);
                lp = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT, // Width of TextView
                        150);
                lp.setMargins(0, 30, 0, 0);
                textview.setLayoutParams(lp);
                textview.setHint(fieldName);
                textview.setId(fieldId);
                textview.setBackground(getResources().getDrawable(R.drawable.drawable_rectangle_shape));
                textview.setPadding(40, 40, 10, 10);
                textview.setTextSize(17);
                //df = new SimpleDateFormat("HH:mm");
                String time = simpleDateFormat.format(Calendar.getInstance().getTime());
                textview.setText(time);
                if (fieldData.length() > 0) {
                    if (fieldName.equalsIgnoreCase("Time")) {
                        textview.setText(appUtils.formatTimeBasedOnPreference(getApplicationContext(), "HH:mm", fieldData));
                    } else {
                        textview.setText(fieldData);
                    }
                }
                if (fieldId == 196) {
                    textview.setText(fieldName);
                    if (isMandatory == 2) {
                        mandatoryField.put(fieldId, fieldName);
                    }
                    textview.setVisibility(View.GONE);
                    taskHiddenTv.add(textview);
                }
                formLayout.addView(textview);

                textview.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        timePicker((TextView) view);
                    }
                });
                break;
        }

    }

    private void getFormData() {
        JSONObject recordJson = new JSONObject();
        JSONObject recordData = new JSONObject();
        JSONObject metaData = new JSONObject();
        JSONArray dietData = new JSONArray();

        boolean sendRecord = true;

        try {
            recordData.put("user_id", ApiUrls.doctorId);
            recordData.put("doctor_id", ApiUrls.doctorId);
            recordData.put("patient_id", patientID);
            recordData.put("catid", categoryId);

            recordJson.put("record_data", recordData);
            recordJson.put("dietdata", dietData);

            int childCount = formLayout.getChildCount();
//            Log.d("Total Child Present", childCount + "");
            for (int i = 0; i < childCount; i++) {
                View view = formLayout.getChildAt(i);

                if (view instanceof EditText) {
                    int id = view.getId();
                    String value = ((EditText) view).getText().toString();
                    if (id != -1 && !value.equals("")) {
                        metaData.put(id + "", value);
                    } else if (mandatoryField.containsKey(id)) {
                        sendRecord = false;
                        Toast.makeText(VoiceEMRCreateRecordActivity.this, mandatoryField.get(id) + getResources().getString(R.string.is_mandatory), Toast.LENGTH_SHORT).show();
                        break;
                    }
                } else if (view instanceof Spinner) {
                    int id = view.getId();
                    String value = ((Spinner) view).getSelectedItem().toString();
                    if (id != -1 && !value.equals("") && !value.equals(getResources().getString(R.string.select_option))) {
                        metaData.put(id + "", value);
                    } else if (mandatoryField.containsKey(id)) {
                        sendRecord = false;
                        Toast.makeText(VoiceEMRCreateRecordActivity.this, getResources().getString(R.string.select_option), Toast.LENGTH_SHORT).show();
                        break;
                    }
                } else if (view instanceof CheckBox) {
                    int id = view.getId();
                    boolean value = ((CheckBox) view).isChecked();
                    if (id != -1 && value) {
                        metaData.put(id + "", 1);
                    } else if (mandatoryField.containsKey(id)) {
                        sendRecord = false;
                        Toast.makeText(VoiceEMRCreateRecordActivity.this, getResources().getString(R.string.select_check_box_option), Toast.LENGTH_SHORT).show();
                        break;
                    }
                } else if (view instanceof TextView) {
                    int id = view.getId();
                    String value;
                    if (((TextView) view).getHint() != null && ((TextView) view).getHint().toString().equalsIgnoreCase("Time")) {
                        value = appUtils.changeDateFormat(timeFormat, "HH:mm", ((TextView) view).getText().toString());
                    } else {
                        value = ((TextView) view).getText().toString();
                    }
                    if (id != -1 && !value.equals("")) {
                        metaData.put(id + "", value);
                    } else if (mandatoryField.containsKey(id)) {
                        sendRecord = false;
                        Toast.makeText(VoiceEMRCreateRecordActivity.this, "Set " + mandatoryField.get(id), Toast.LENGTH_SHORT).show();
                        break;
                    }
                } else if (view instanceof LinearLayout) {
                    int id = view.getId();
                    if (id != -1 && uploadImageResponse != null) {
                        metaData.put(id + "", uploadImageResponse);
                    } else if (mandatoryField.containsKey(id)) {
                        sendRecord = false;
                        Toast.makeText(VoiceEMRCreateRecordActivity.this, getResources().getString(R.string.please_attached_file), Toast.LENGTH_SHORT).show();
                        break;
                    }
                }
            }
            recordJson.put("metadata", metaData);
            Log.i("records json data", recordJson.toString());
            if (sendRecord) {
                recordJson.put("episode_id", episodeID);
                recordJson.put("encounter_id", encounterID);
                recordJson.put("category_id", categoryId);
                recordJson.put("patient_id", patientID);
                recordJson.put("type", type);
//                Log.d("Record JSON", recordJson.toString());
//                addRecord(recordJson);//commented by dileep


                if (ObservationEditRecord.equalsIgnoreCase("ObservationEditRecord")) {
                    String metaDataString = metaData.toString();
                    emrObject.setObservationCategoryRecords(metaDataString);
                    Intent intent = new Intent();
                    intent.putExtra("VoiceEMRModel", emrObject);
                    intent.putExtra("Type", "edit");
                    setResult(100, intent);
                    finish();

                } else if (TreatmentEditRecord.equalsIgnoreCase("TreatmentEditRecord")) {
                    String metaDataString = metaData.toString();
                    emrObject.setTreatmentCategoryRecords(metaDataString);
                    Intent intent = new Intent();
                    intent.putExtra("VoiceEMRModel", emrObject);
                    intent.putExtra("Type", "edit");
                    setResult(100, intent);
                    finish();

                } else {
                    if (type.equalsIgnoreCase("observations")) {

                        VoiceEMRModel ve = new VoiceEMRModel();
                        ve.setCategoryName("Observation");
                        ve.setObservationCategoryName(categoryName);
                        ve.setObservationCategoryId(Integer.parseInt(categoryId));
                        ve.setObservationCategoryRecords(metaData.toString());
                        ve.setObservationFieldDic(VoiceEMRActivity.fieldDictionary.toString());
                        Long tsLong = System.currentTimeMillis() / 1000;
                        String timeStamp = tsLong.toString();
                        ve.setObservationData_id(timeStamp);

                       /* if (VoiceEMRActivity.voiceEMRModelsObservationCategoryList.size() > 0) {
                            ve.setCategoryNameObservationExistType(2);
                        } else {
                            ve.setCategoryNameObservationExistType(1);
                        }*/

                       /* int subCatogorySerNumber = VoiceEMRActivity.voiceEMRModelsObservationCategoryList.size() + 1;
                        ve.setSubCategorySerNumber(subCatogorySerNumber + "");*/
                        VoiceEMRActivity.voiceEMRModelsObservationCategoryList.add(ve);
                        Intent intent = new Intent();
                        intent.putExtra("VoiceEMRModel", ve);
                        intent.putExtra("Type", "add");
                        setResult(100, intent);
                        finish();
                    } else if (type.equalsIgnoreCase("treatmentplan")) {

                        VoiceEMRModel ve = new VoiceEMRModel();
                        ve.setCategoryName("Treatment Plan");
                        ve.setTreatmentCategoryName(categoryName);
                        ve.setTreatmentCategoryId(Integer.parseInt(categoryId));
                        ve.setTreatmentCategoryRecords(metaData.toString());
                        ve.setTreatmentFieldDic(VoiceEMRActivity.fieldDictionary.toString());
                        Long tsLong = System.currentTimeMillis() / 1000;
                        String timeStamp = tsLong.toString();
                        ve.setTreatmentPlanData_id(timeStamp);

                      /*  if (VoiceEMRActivity.voiceEMRModelsTreatmentPlanCategoryList.size() > 0) {
                            ve.setCategoryNameTreatmentPlanExistType(2);
                        } else {
                            ve.setCategoryNameTreatmentPlanExistType(1);
                        }*/

                      /*  int subCatogorySerNumber = VoiceEMRActivity.voiceEMRModelsTreatmentPlanCategoryList.size() + 1;
                        ve.setSubCategorySerNumber(subCatogorySerNumber + "");
                        VoiceEMRActivity.voiceEMRModelsTreatmentPlanCategoryList.add(ve);*/
                        Intent intent = new Intent();
                        intent.putExtra("VoiceEMRModel", ve);
                        intent.putExtra("Type", "add");
                        setResult(100, intent);
                        finish();
                    }


                }
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void addRecord(JSONObject recordObj) {
        String url = ApiUrls.saveRecords;
        otpLoading = new ProgressDialog(VoiceEMRCreateRecordActivity.this);
        otpLoading.setMessage(getResources().getString(R.string.common_please_wait_loading_message));
        otpLoading.setTitle(getResources().getString(R.string.creating_your_record));
        otpLoading.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        otpLoading.setCancelable(false);
        otpLoading.show();

        // prepare the Request
        apiGetPostMethodCalls.volleyApiRequestData(url, Request.Method.POST, recordObj, VoiceEMRCreateRecordActivity.this,
                new VolleyCallback() {
                    @Override
                    public void onSuccess(String result) {
                        otpLoading.dismiss();
                        try {
                            Toast.makeText(VoiceEMRCreateRecordActivity.this, getResources().getString(R.string.record_added_successfully), Toast.LENGTH_SHORT).show();
                            Intent returnIntent = new Intent();
                            returnIntent.putExtra("result", "OK");
                            setResult(Activity.RESULT_OK, returnIntent);
                            finish();

                        } catch (Exception e) {
                            e.printStackTrace();
                            otpLoading.dismiss();
                        }
                    }

                    @Override
                    public void onError(String err) {
                        otpLoading.dismiss();
                        ErrorHandlerClass.INSTANCE.errorHandler(VoiceEMRCreateRecordActivity.this, err);
                    }
                });

    }

    /**
     * This callback method, call DatePickerFragment class,
     * DatePickerFragment class returns calendar view.
     *
     * @param
     */
    public void datePicker(TextView view) {

        dateTv = view;
        DatePickerFragment fragment = new DatePickerFragment();
        fragment.show(getSupportFragmentManager(), "date");
    }

    /**
     * To set date on TextView
     *
     * @param calendar
     */
    private void setDate(final Calendar calendar) {
//        final DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.SHORT);
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");

        dateTv.setText(sdf.format(calendar.getTime()));
    }

    /**
     * To receive a callback when the user sets the date.
     *
     * @param view
     * @param year
     * @param month
     * @param day
     */
    @Override
    public void onDateSet(DatePicker view, int year, int month, int day) {
        Calendar cal = new GregorianCalendar(year, month, day);
        setDate(cal);
    }

    /**
     * Create a DatePickerFragment class that extends DialogFragment.
     * Define the onCreateDialog() method to return an instance of DatePickerDialog
     */
    public static class DatePickerFragment extends DialogFragment {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {

            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(),
                    (DatePickerDialog.OnDateSetListener)
                            getActivity(), year, month, day);

//            datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);

            return datePickerDialog;
        }

    }

    private void timePicker(final TextView textView) {
        Calendar mcurrentTime = Calendar.getInstance();
        int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
        int minute = mcurrentTime.get(Calendar.MINUTE);
        TimePickerDialog mTimePicker;
        mTimePicker = new TimePickerDialog(VoiceEMRCreateRecordActivity.this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                String format;
                if (timePicker.is24HourView()) {
                    format = "HH:mm";
                } else {
                    format = "hh:mm aa";
                }

                //textView.setHint(appUtils.getFormattedTime(selectedHour,minute,"HH:mm"));
                textView.setText(appUtils.getFormattedTime(selectedHour, minute, format));
                //textView.setText(selectedHour + ":" + selectedMinute);
            }
        }, hour, minute, is24Hrformat);//Yes 24 hour time
        mTimePicker.setTitle("Select Time");
        mTimePicker.show();
    }

    public void openCamera() {
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

    private void uploadImage() {
        String url = ApiUrls.uploadRecordImage;

        loadingDialog.setMessage(getResources().getString(R.string.common_uploading_image));
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
                                Toast.makeText(getApplicationContext(), getResources().getString(R.string.image_upload_sucessfully), Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(getApplicationContext(), getResources().getString(R.string.image_upload_failed), Toast.LENGTH_SHORT).show();

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
//                params.put("path", "records/" + formImageID + "/"); //old
                params.put("path", "records/" + formImageID + "/" + "android" + "/"); // new
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
                    Bitmap bitmap = ((BitmapDrawable) formImage.getDrawable()).getBitmap();
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

    /*
     * The method is taking Bitmap as an argument
     * then it will return the byte[] array for the given bitmap
     * and we will send this array to the server
     * here we are using PNG Compression with 80% quality
     * you can give quality between 0 to 100
     * 0 means worse quality
     * 100 means best quality
     * */
    public byte[] getFileDataFromDrawable(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 20, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }

    private void showTaskHiddenField(int state) {

        if (state == View.VISIBLE) {
            try {
                JSONArray fieldType = taskRepeatObj.getJSONArray("field_type");
//                Log.d("Field", fieldType.length() + "");
                for (int i = 0; i < fieldType.length(); i++) {
                    JSONObject obj = fieldType.getJSONObject(i);
                    int id = obj.getInt("id");
                    int type = obj.getInt("type");
                    String name = taskRepeatObj.getString("field");//"E.g. Once every 2 days";
                    int mandatory = obj.getInt("is_mandatory");
                    String value = "";
                    String fieldData = "";
                    if (type == 4) {
                        fieldType = taskRepeatObj.getJSONArray("field_values");
                        value = fieldType.getString(1);
                    }
                    createTaskForm(id, name, type, mandatory, value, fieldData);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            for (int i = 0; i < taskHiddenTv.size(); i++) {
                TextView textView = taskHiddenTv.get(i);
                textView.setVisibility(state);
            }

            for (int i = 0; i < taskHidderSpinner.size(); i++) {
                Spinner spinner = taskHidderSpinner.get(i);
                spinner.setVisibility(state);
            }

            for (int i = 0; i < taskHiddenEt.size(); i++) {
                EditText editText = taskHiddenEt.get(i);
                editText.setVisibility(state);
            }
        } else {
            for (int i = 0; i < taskHiddenTv.size(); i++) {
                TextView textView = taskHiddenTv.get(i);
                textView.setVisibility(state);
                if (textView.getText().toString().equalsIgnoreCase("E.g. Once every 2 days")) {
                    formLayout.removeView(textView);
                }
            }

            for (int i = 0; i < taskHidderSpinner.size(); i++) {
                Spinner spinner = taskHidderSpinner.get(i);
                formLayout.removeView(spinner);
            }

            for (int i = 0; i < taskHiddenEt.size(); i++) {
                EditText editText = taskHiddenEt.get(i);
                formLayout.removeView(editText);
            }
        }
    }

    private void getTaskFormData() {
        JSONObject recordJson = new JSONObject();
        JSONObject recordData = new JSONObject();
        JSONObject metaData = new JSONObject();
        JSONArray dietData = new JSONArray();

        JSONObject dataJsonObject = new JSONObject();
        JSONArray data = new JSONArray();

        boolean sendRecord = true;

        try {
//            recordData.put("user_id", AppConfigClass.patientId);
//            recordData.put("doctor_id", "0");
//            recordData.put("patient_id", AppConfigClass.patientId);
//            recordData.put("catid", categoryId);

            recordJson.put("episode_id", episodeID);
            recordJson.put("encounter_id", encounterID);
            recordJson.put("category_id", categoryId);
            recordJson.put("patient_id", patientID);
            recordJson.put("type", type);

            recordData.put("user_id", ApiUrls.doctorId);
            recordData.put("doctor_id", ApiUrls.doctorId);
            recordData.put("patient_id", patientID);
            recordData.put("catid", categoryId);


            recordJson.put("record_data", recordData);
            recordJson.put("dietdata", dietData);

            int childCount = formLayout.getChildCount();
//            Log.d("Total Child Present", childCount + "");
            for (int i = 0; i < childCount; i++) {
                View view = formLayout.getChildAt(i);

                if (view instanceof EditText) {
                    int id = view.getId();
                    String value = ((EditText) view).getText().toString();
                    if (id != -1 && !value.equals("")) {
                        metaData.put(id + "", value);
                    } else if (mandatoryField.containsKey(id)) {
                        sendRecord = false;
                        Toast.makeText(VoiceEMRCreateRecordActivity.this, mandatoryField.get(id) + " is Mandatory", Toast.LENGTH_SHORT).show();
                        break;
                    }
                } else if (view instanceof Spinner) {
                    int id = view.getId();
                    String value = ((Spinner) view).getSelectedItem().toString();
                    if (id != -1 && !value.equals("") && !value.equals(getResources().getString(R.string.select_option))) {
                        metaData.put(id + "", value);
                    } else if (mandatoryField.containsKey(id)) {
                        sendRecord = false;
                        Toast.makeText(VoiceEMRCreateRecordActivity.this, "Select One Option", Toast.LENGTH_SHORT).show();
                        break;
                    }
                } else if (view instanceof TextView) {
                    int id = view.getId();
                    String value = ((TextView) view).getText().toString();
                    if (id != -1 && !value.equals("") && !value.equalsIgnoreCase("End Time") && !value.equalsIgnoreCase("End Date")) {
                        metaData.put(id + "", value);
                    } else if (mandatoryField.containsKey(id) && view.getVisibility() == View.VISIBLE) {
                        sendRecord = false;
                        Toast.makeText(VoiceEMRCreateRecordActivity.this, "Set " + mandatoryField.get(id), Toast.LENGTH_SHORT).show();
                        break;
                    }
                }
            }
            metaData.put("192", "Days");
            metaData.put("197", "Patient");
            recordJson.put("metadata", metaData);

            //  JSONObject main = new JSONObject();

            // main.put()

            //   data.put("data", recordJson);
            if (sendRecord) {
//                Log.d("Record JSON", recordJson.toString());
                addRecord(recordJson);
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showHiddenInvestField(int i) {
        if (i == 2) {
            TextView dateTv = investDateList.get(1);
            dateTv.setVisibility(View.VISIBLE);
            dateTv.setText("To Be Conducted");
            dateTv = investDateList.get(0);
            dateTv.setVisibility(View.GONE);
            EditText editText = investEditText.get(1);
            editText.setVisibility(View.GONE);
        } else if (i == 1) {
            TextView dateTv = investDateList.get(1);
            dateTv.setVisibility(View.GONE);
            dateTv = investDateList.get(0);
            dateTv.setVisibility(View.VISIBLE);
            EditText editText = investEditText.get(1);
            editText.setVisibility(View.VISIBLE);
        }
    }

    private void showHiddenImmuField(int i) {
        if (i == 1) {
            TextView dateTv = immuDateList.get(0);
            dateTv.setVisibility(View.VISIBLE);
            dateTv = immuDateList.get(1);
            dateTv.setVisibility(View.GONE);
            EditText editText = immuEditText.get(0);
            editText.setVisibility(View.VISIBLE);
        } else if (i == 2) {
            TextView dateTv = immuDateList.get(0);
            dateTv.setVisibility(View.GONE);
            dateTv = immuDateList.get(1);
            dateTv.setVisibility(View.VISIBLE);
            dateTv.setText("Schedule On");
            EditText editText = immuEditText.get(0);
            editText.setVisibility(View.GONE);
        }
    }

    private int getIndex(Spinner spinner, String myString) {

        int index = 0;

        for (int i = 0; i < spinner.getCount(); i++) {
            if (spinner.getItemAtPosition(i).equals(myString)) {
                index = i;
            }
        }
        return index;
    }

    private void showHiddenProblemField(int i) {

        if (i == 1 || i == 2) {
            TextView textView = probEditText.get(0);
            textView.setVisibility(View.GONE);
        } else if (i == 3) {
            TextView textView = probEditText.get(0);
            textView.setVisibility(View.VISIBLE);
        }
    }

    private void showHiddenProcedureField(int i) {
        if (i == 1) {
//            Log.d("Procedure Gone", "******");
            TextView textView = proceedTextView.get(0);
            textView.setVisibility(View.GONE);
        } else if (i == 2) {
//            Log.d("Procedure Visible", "******");
            TextView textView = proceedTextView.get(0);
            textView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_CALLBACK_CONSTANT) {
            //check if all permissions are granted
            boolean allgranted = false;
            for (int i = 0; i < grantResults.length; i++) {
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    allgranted = true;
                } else {
                    allgranted = false;
                    break;
                }
            }

            if (allgranted) {
//                Toast.makeText(EMRCreateRecordActivity.this, "You Have all the permission", Toast.LENGTH_SHORT).show();
                openCamera();
            } else {
                Toast.makeText(VoiceEMRCreateRecordActivity.this, "You Need To Give Camera Permission", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == READ_EXTERNAL_STORAGE_CONSTANT) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openFileDialog();
            } else {
                Toast.makeText(VoiceEMRCreateRecordActivity.this, "You Need To Give File access Permission", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private Bitmap decodeFile(File f) {
        Bitmap b = null;

        //Decode image size
        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inJustDecodeBounds = true;

        FileInputStream fis = null;
        try {
            fis = new FileInputStream(f);
            BitmapFactory.decodeStream(fis, null, o);
            fis.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        int IMAGE_MAX_SIZE = 1024;
        int scale = 1;
        if (o.outHeight > IMAGE_MAX_SIZE || o.outWidth > IMAGE_MAX_SIZE) {
            scale = (int) Math.pow(2, (int) Math.ceil(Math.log(IMAGE_MAX_SIZE /
                    (double) Math.max(o.outHeight, o.outWidth)) / Math.log(0.5)));
        }

        //Decode with inSampleSize
        BitmapFactory.Options o2 = new BitmapFactory.Options();
        o2.inSampleSize = scale;
        try {
            fis = new FileInputStream(f);
            b = BitmapFactory.decodeStream(fis, null, o2);
            fis.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

//        Log.d("Image Upload", "Width :" + b.getWidth() + " Height :" + b.getHeight());

//        destFile = new File(file, "img_"
//                + dateFormatter.format(new Date()).toString() + ".png");
//        try {
//            FileOutputStream out = new FileOutputStream(destFile);
//            b.compress(Bitmap.CompressFormat.PNG, 100, out);
//            out.flush();
//            out.close();
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        return b;
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
            uploadImage();
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
//                    //Log.d("Cursor ",  allPdfFiles.getString(0) + "");
//        int i = 0;
//        while (allPdfFiles.moveToNext()) {
////            Log.d("Cursorrrrrrr", allPdfFiles.getString(1));
////            Log.d("###########", selectedFilePath);
//            if (allPdfFiles.getString(1).contains(selectedFilePath)) {
//                pdfFile = new File(allPdfFiles.getString(1));
////                Intent inty = new Intent(Intent.ACTION_VIEW, FileProvider.getUriForFile(EMRCreateRecordActivity.this,
////                        getApplicationContext().getPackageName() + ".provider",
////                        pdfFile));
////                inty.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
////                startActivity(inty);
//
//                uploadImage();
//            }
//        }
    }
}
