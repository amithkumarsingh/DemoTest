package com.whitecoats.clinicplus;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import com.whitecoats.adapter.PatientRecordViewAdapter;
import com.whitecoats.clinicplus.apis.ApiUrls;
import com.whitecoats.clinicplus.utils.ErrorHandlerClass;
import com.whitecoats.model.PatientRecordsModel;
import com.zoho.salesiqembed.ZohoSalesIQ;

public class PatientRecordViewActivity extends AppCompatActivity {

    private RecyclerView recordsViewRv;
    private List<PatientRecordsModel> patientRecordsModels;
    private PatientRecordViewAdapter recordViewAdapter;
    private PatientRecordsApi apiCall;
    private int catId, patientId, sharedCreated = 0, episodeID, encounterID;
    private ProgressDialog loadingDialog;
    private Toolbar toolbar;
    private TextView emptyText;
    private ProgressBar loader;
    private AppUtilities appUtilities;
    private String fileDate = "";
    private JSONArray recordIdArray;
    private JSONObject jsonValue;
    private int patientShareFlag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_record_view);

        recordsViewRv = findViewById(R.id.patientRecordViewRv);
        toolbar = findViewById(R.id.patientRecordViewToolBar);
        emptyText = findViewById(R.id.patientRecordEmptyText);
        loader = findViewById(R.id.patientRecordLoader);
        patientRecordsModels = new ArrayList<>();
        apiCall = new PatientRecordsApi();
        appUtilities = new AppUtilities();

        ZohoSalesIQ.Tracking.setPageTitle("Patient Records View Page");

        recordViewAdapter = new PatientRecordViewAdapter(patientRecordsModels, this, new PatientRecordListener() {
            @Override
            public void onItemClick(View v, String parameter, String type, String recordIdArray) {
                getFileFromUrl(parameter);
            }
        });

        Intent intent = getIntent();
        patientShareFlag = getIntent().getIntExtra("PatientSharedFlag", 0);
        String jsonArray = intent.getStringExtra("recordIdArray");
        try {
            if (jsonArray != null) {
                recordIdArray = new JSONArray(jsonArray);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        catId = getIntent().getIntExtra("CategoryId", 0);
        patientId = getIntent().getIntExtra("PatientId", 0);
        episodeID = getIntent().getIntExtra("EpisodeId", 0);
        encounterID = getIntent().getIntExtra("EncounterID", 0);
        sharedCreated = getIntent().getIntExtra("SharedCreated", 0);
//        Log.d("Cat Name", getIntent().getStringExtra("CategoryName"));
        toolbar.setTitle(getIntent().getStringExtra("CategoryName"));
        setSupportActionBar(toolbar);
        Drawable mDrawable = getResources().getDrawable(R.drawable.ic_arrow_back);
        mDrawable.setColorFilter(new PorterDuffColorFilter(getResources().getColor(R.color.colorWhite), PorterDuff.Mode.SRC_IN));
        toolbar.setNavigationIcon(mDrawable);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recordsViewRv.setLayoutManager(mLayoutManager);
        recordsViewRv.setItemAnimator(new DefaultItemAnimator());
        recordsViewRv.setAdapter(recordViewAdapter);

        if (sharedCreated == 1) {
            if (getIntent().getStringExtra("Type").equalsIgnoreCase("observations")) {
                getEpisEvalutaion();
            } else {
                getEpisTreatPlan();
            }
        } else {
            if (patientShareFlag == 1) {
                getRecordsDetailsShare();
            } else {
                getRecordsDetails();
            }
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
        }

        return true;
    }

    private void getRecordsDetails() {
        // String url = appConfigClass.getSharedRecordDetails + "?catid=" + catId + "&doctorId=" + AppConfigClass.doctorId + "&patientId=" + patientId + "&sharedOrCreated=" + sharedCreated;
        String url = ApiUrls.getPatientSharedRecordDetails;


        try {
            jsonValue = new JSONObject();
            jsonValue.put("record_ids", recordIdArray);
            jsonValue.put("category_id", catId);
        } catch (Exception e) {

        }


//        Log.d("REcord URl ", url);
        loader.setVisibility(View.VISIBLE);
        emptyText.setVisibility(View.VISIBLE);
        emptyText.setText(getResources().getString(R.string.loading_data));

        apiCall.postRecords(url, jsonValue, this, new VolleyCallback() {
            @Override
            public void onSuccess(String result) {
//                Log.d("Details Success", result);

                try {
                    JSONObject resObj = new JSONObject(result);
                    JSONArray recordsArr = resObj.getJSONObject("response").getJSONArray("records");
//                    Log.d("REcord Arry", resObj.getJSONObject("response").toString());
                    if (recordsArr.length() > 0) {
                        emptyText.setVisibility(View.GONE);
                        loader.setVisibility(View.GONE);
                        for (int i = 0; i < recordsArr.length(); i++) {
                            JSONObject recordOBj = recordsArr.getJSONObject(i);

                            JSONObject recordInfoOBj = recordOBj.getJSONObject("recordinfo");//added by dileep
                            JSONObject recordsOBj = recordInfoOBj.getJSONObject("records");//added by dileep
                            if (catId == 33) {
                                String recordSharedDetailsObject = recordsOBj.getJSONObject("share_details").getString("created_at");//added by dileep
                                fileDate = appUtilities.changeDateFormat("yyyy-MM-dd hh:mm:ss", "dd/MM/yyyy", recordSharedDetailsObject);//added by dileep
                            }

                            JSONObject fieldDic = resObj.getJSONObject("response").getJSONObject("field-dictionary");
                            PatientRecordsModel model = new PatientRecordsModel();
                            if (fieldDic.get(catId + "") instanceof JSONArray) {

                                JSONArray fieldArr = fieldDic.getJSONArray(catId + "");

                                model.setPrimaryKey(fieldArr.getJSONObject(0).getString("name"));
                                if (fieldArr.length() > 1) {
                                    model.setSecKey(fieldArr.getJSONObject(1).getString("name"));

                                    if (recordsOBj.has(fieldArr.getJSONObject(1).getString("key"))) {
                                        model.setSecData(recordsOBj.getString(fieldArr.getJSONObject(1).getString("key")));
                                    } else {
                                        model.setSecData("");
                                    }
                                }

                                if (catId == 33) {

                                    model.setTernaryKey("Date");
                                    model.setTernaryData(fileDate);

                                } else if (fieldArr.length() > 2) {
                                    model.setTernaryKey(fieldArr.getJSONObject(2).getString("name"));

                                    if (recordsOBj.has(fieldArr.getJSONObject(2).getString("key"))) {
                                        String ternaryData = recordsOBj.getString(fieldArr.getJSONObject(2).getString("key"));
                                        model.setTernaryData(ternaryData);
                                    } else {
                                        model.setTernaryData("");
                                    }

                                }

                                if (recordsOBj.has(fieldArr.getJSONObject(0).getString("key")))
                                    model.setPrimaryData(recordsOBj.getString(fieldArr.getJSONObject(0).getString("key")));

                                if (recordsOBj.has("url")) {
                                    model.setFileUrl(recordsOBj.getString("url"));
                                }
                            } else {
                                JSONObject fieldObj = fieldDic.getJSONObject(catId + "");
                                model.setPrimaryKey(fieldObj.getJSONObject("0").getString("name"));
                                if (fieldObj.has("1")) {
                                    model.setSecKey(fieldObj.getJSONObject("1").getString("name"));
                                    model.setSecData(recordsOBj.getString(fieldObj.getJSONObject("1").getString("key")));
                                }

                                if (catId == 33) {

                                    model.setTernaryKey("Date");
                                    model.setTernaryData(fileDate);

                                } else if (fieldObj.has("2")) {
                                    model.setTernaryKey(fieldObj.getJSONObject("2").getString("name"));
                                    model.setTernaryData(recordsOBj.getString(fieldObj.getJSONObject("2").getString("key")));
                                }

                                model.setPrimaryData(recordsOBj.getString(fieldObj.getJSONObject("0").getString("key")));

                                if (recordsOBj.has("url")) {
                                    model.setFileUrl(recordsOBj.getString("url"));
                                }

                            }

                            model.setFieldDictionary(fieldDic.toString());
                            model.setCatRecordData(recordsArr.toString());
                            model.setCatId(catId + "");
                            model.setCatName(recordsOBj.getJSONObject("category").getString("category"));
                            model.setRecordId(recordOBj.getInt("id"));

                            toolbar.setTitle(recordsOBj.getJSONObject("category").getString("category"));
                            patientRecordsModels.add(model);
                        }

                        recordViewAdapter.notifyDataSetChanged();
                    } else {
                        loader.setVisibility(View.GONE);
                        emptyText.setVisibility(View.VISIBLE);
                        emptyText.setText(getResources().getString(R.string.no_data_found));
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    loader.setVisibility(View.GONE);
                    emptyText.setVisibility(View.VISIBLE);
                    emptyText.setText(getResources().getString(R.string.data_load_failed));
                }

            }

            @Override
            public void onError(String err) {
//                Log.d("Details Error", err);
                emptyText.setVisibility(View.VISIBLE);
                loader.setVisibility(View.GONE);
                emptyText.setText(getResources().getString(R.string.data_load_failed));
            }
        });
    }


    private void getRecordsDetailsShare() {
        String url = ApiUrls.getSharedRecordDetails + "?catid=" + catId + "&doctorId=" + ApiUrls.doctorId + "&patientId=" + patientId + "&sharedOrCreated=" + sharedCreated;
//        Log.d("REcord URl ", url);
        loader.setVisibility(View.VISIBLE);
        emptyText.setVisibility(View.VISIBLE);
        emptyText.setText(getResources().getString(R.string.loading_data));

        apiCall.getRecordPref(url, "", this, new VolleyCallback() {
            @Override
            public void onSuccess(String result) {
//                Log.d("Details Success", result);

                try {
                    JSONObject resObj = new JSONObject(result);
                    JSONArray recordsArr = resObj.getJSONObject("response").getJSONArray("records");
//                    Log.d("REcord Arry", resObj.getJSONObject("response").toString());
                    if (recordsArr.length() > 0) {
                        emptyText.setVisibility(View.GONE);
                        loader.setVisibility(View.GONE);
                        for (int i = 0; i < recordsArr.length(); i++) {
                            JSONObject recordOBj = recordsArr.getJSONObject(i);

                            if (catId == 33) {
                                String recordSharedDetailsObject = recordOBj.getJSONObject("share_details").getString("created_at");//added by dileep
                                fileDate = appUtilities.changeDateFormat("yyyy-MM-dd hh:mm:ss", "dd/MM/yyyy", recordSharedDetailsObject);//added by dileep
                            }

                            JSONObject fieldDic = resObj.getJSONObject("response").getJSONObject("field-dictionary");
                            PatientRecordsModel model = new PatientRecordsModel();
                            if (fieldDic.get(catId + "") instanceof JSONArray) {

                                JSONArray fieldArr = fieldDic.getJSONArray(catId + "");

                                model.setPrimaryKey(fieldArr.getJSONObject(0).getString("name"));
                                if (fieldArr.length() > 1) {
                                    model.setSecKey(fieldArr.getJSONObject(1).getString("name"));
                                    if (recordOBj.has(fieldArr.getJSONObject(1).getString("key"))) {
                                        model.setSecData(recordOBj.getString(fieldArr.getJSONObject(1).getString("key")));
                                    }else {
                                        model.setSecData("");
                                    }
                                }

                                if (catId == 33) {

                                    model.setTernaryKey("Date");
                                    model.setTernaryData(fileDate);

                                } else if (fieldArr.length() > 2) {
                                    model.setTernaryKey(fieldArr.getJSONObject(2).getString("name"));

                                    if (recordOBj.has(fieldArr.getJSONObject(2).getString("key"))) {
                                        model.setTernaryData(recordOBj.getString(fieldArr.getJSONObject(2).getString("key")));
                                    }else {
                                        model.setTernaryData("");
                                    }

//                                    if (recordOBj.has(recordsArr.getJSONObject(2).getString("key"))) {
//                                        String ternaryData = recordOBj.getString(recordsArr.getJSONObject(2).getString("key"));
//                                        model.setTernaryData(ternaryData);
//                                    } else {
//                                        model.setTernaryData("");
//                                    }

                                }

                                if (recordOBj.has(fieldArr.getJSONObject(0).getString("key")))
                                    model.setPrimaryData(recordOBj.getString(fieldArr.getJSONObject(0).getString("key")));

                                if (recordOBj.has("url")) {
                                    model.setFileUrl(recordOBj.getString("url"));
                                }
                            } else {
                                JSONObject fieldObj = fieldDic.getJSONObject(catId + "");
                                model.setPrimaryKey(fieldObj.getJSONObject("0").getString("name"));
                                if (fieldObj.has("1")) {
                                    model.setSecKey(fieldObj.getJSONObject("1").getString("name"));

                                    if (fieldObj.has(recordsArr.getJSONObject(1).getString("key"))) {
                                        model.setSecData(recordOBj.getString(fieldObj.getJSONObject("1").getString("key")));
                                    }else {
                                        model.setSecData("");
                                    }

                                }

                                if (catId == 33) {

                                    model.setTernaryKey("Date");
                                    model.setTernaryData(fileDate);

                                }
                                else if (fieldObj.has("2")) {
                                    model.setTernaryKey(fieldObj.getJSONObject("2").getString("name"));

                                    if (fieldObj.has(recordsArr.getJSONObject(2).getString("key"))) {
                                        model.setTernaryData(recordOBj.getString(fieldObj.getJSONObject("2").getString("key")));
                                   }else {
                                      model.setTernaryData("");
                                  }

                                }

//                                else if (fieldObj.has(recordsArr.getJSONObject(2).getString("key"))) {
//                                    String ternaryData = fieldObj.getString(recordsArr.getJSONObject(2).getString("key"));
//                                    model.setTernaryData(ternaryData);
//                                } else {
//                                    model.setTernaryData("");
//                                }

                                model.setPrimaryData(recordOBj.getString(fieldObj.getJSONObject("0").getString("key")));

                                if (recordOBj.has("url")) {
                                    model.setFileUrl(recordOBj.getString("url"));
                                }

                            }

                            model.setFieldDictionary(fieldDic.toString());
                            model.setCatRecordData(recordsArr.toString());
                            model.setCatId(catId + "");
                            model.setCatName(resObj.getJSONObject("response").getJSONObject("cat_details").getString("category"));
                            model.setRecordId(recordOBj.getInt("record_id"));

                            toolbar.setTitle(resObj.getJSONObject("response").getJSONObject("cat_details").getString("category"));
                            patientRecordsModels.add(model);
                        }

                        recordViewAdapter.notifyDataSetChanged();
                    } else {
                        loader.setVisibility(View.GONE);
                        emptyText.setVisibility(View.VISIBLE);
                        emptyText.setText(getResources().getString(R.string.no_data_found));
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    loader.setVisibility(View.GONE);
                    emptyText.setVisibility(View.VISIBLE);
                    emptyText.setText(getResources().getString(R.string.data_load_failed));
                }

            }

            @Override
            public void onError(String err) {
//                Log.d("Details Error", err);
                emptyText.setVisibility(View.VISIBLE);
                loader.setVisibility(View.GONE);
                emptyText.setText(getResources().getString(R.string.data_load_failed));
            }
        });
    }


    public void getFileFromUrl(String filrUrl) {

        loadingDialog = new ProgressDialog(this);
        loadingDialog.setMessage(getResources().getString(R.string.loading_data));
        loadingDialog.setTitle(getResources().getString(R.string.fetching_file));
        loadingDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        loadingDialog.setCancelable(false);
        loadingDialog.show();

        JSONObject url = new JSONObject();
        try {
            url.put("url", filrUrl.trim());
        } catch (Exception e) {
            e.printStackTrace();
        }

//        Log.d("File Url", url.toString());

        apiCall.postRecords(ApiUrls.getFileFromUrl, url, this, new VolleyCallback() {
            @Override
            public void onSuccess(String result) {
                loadingDialog.dismiss();

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
                loadingDialog.dismiss();
                ErrorHandlerClass.INSTANCE.errorHandler(PatientRecordViewActivity.this, err);
            }
        });
    }

    private void getEpisEvalutaion() {
        loader.setVisibility(View.VISIBLE);
        emptyText.setVisibility(View.VISIBLE);
        emptyText.setText(getResources().getString(R.string.loading_data));

        String url = ApiUrls.getEpisEvaluation + "?doctor_id=" + ApiUrls.doctorId + "&episode_id=" + episodeID + "&patient_id=" + patientId + "&sharedOrCreated=1&encounter_id=" + encounterID;
        if (encounterID != 0) {
            url = ApiUrls.getEcntEvaluation + "?doctor_id=" + ApiUrls.doctorId + "&episode_id=" + episodeID + "&patient_id=" + patientId + "&sharedOrCreated=1&encounter_id=" + encounterID;
        }
        apiCall.getRecordPref(url, "", this, new VolleyCallback() {
            @Override
            public void onSuccess(String result) {
                loader.setVisibility(View.GONE);
                emptyText.setVisibility(View.GONE);

//                //getting observation
//                try {
//                    JSONObject resObj = new JSONObject(result);
//                    JSONArray catArr = resObj.getJSONObject("response").getJSONObject("observations").getJSONArray("categories");
//                    for (int i = 0; i < catArr.length(); i++) {
//                        JSONObject catObj = catArr.getJSONObject(i);
//                        if (catObj.getJSONObject("record_categories").getInt("id") == catId) {
//                            JSONArray recordArr = catObj.getJSONArray("records");
//                            if (recordArr.length() > 0) {
//                                for (int j = 0; j < recordArr.length(); j++) {
//                                    JSONObject recObj = recordArr.getJSONObject(j);
//
//                                    JSONObject fieldDic = resObj.getJSONObject("response").getJSONObject("observations").getJSONObject("field-dictionary");
//                                    JSONArray fieldArr = fieldDic.getJSONArray(catId + "");
//
//                                    PatientRecordsModel model = new PatientRecordsModel();
//
//                                    model.setPrimaryKey(fieldArr.getJSONObject(0).getString("name"));
//                                    if (fieldArr.length() > 1) {
//                                        model.setSecKey(fieldArr.getJSONObject(1).getString("name"));
//                                        model.setSecData(recObj.getString(fieldArr.getJSONObject(1).getString("key")));
//                                    }
//
//                                    if (fieldArr.length() > 2) {
//                                        model.setTernaryKey(fieldArr.getJSONObject(2).getString("name"));
//                                        model.setTernaryData(recObj.getString(fieldArr.getJSONObject(2).getString("key")));
//                                    }
//
//                                    model.setPrimaryData(recObj.getString(fieldArr.getJSONObject(0).getString("key")));
//
//                                    if (recObj.has("url")) {
//                                        model.setFileUrl(recObj.getString("url"));
//                                    }
//
//                                    patientRecordsModels.add(model);
//                                }
//                                recordViewAdapter.notifyDataSetChanged();
//                            } else {
//                                loader.setVisibility(View.GONE);
//                                emptyText.setVisibility(View.VISIBLE);
//                                emptyText.setText(getResources().getString(R.string.no_data_found));
//                            }
//                        }
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                    emptyText.setVisibility(View.VISIBLE);
//                    loader.setVisibility(View.GONE);
//                    emptyText.setText(getResources().getString(R.string.data_load_failed));
//                }


                //getting observation
                try {
                    JSONObject resObj = new JSONObject(result);
                    JSONArray catArr = resObj.getJSONObject("response").getJSONObject("observations").getJSONArray("categories");
                    for (int i = 0; i < catArr.length(); i++) {
                        JSONObject catObj = catArr.getJSONObject(i);
                        if (catObj.getJSONObject("record_categories").getInt("id") == catId) {
                            JSONArray recordArr = catObj.getJSONArray("records");
                            if (recordArr.length() > 0) {
                                for (int j = 0; j < recordArr.length(); j++) {
                                    JSONObject recObj = recordArr.getJSONObject(j);

                                    JSONObject fieldDic = resObj.getJSONObject("response").getJSONObject("observations").getJSONObject("field-dictionary");
                                    JSONArray fieldArr = fieldDic.getJSONArray(catId + "");

                                    PatientRecordsModel model = new PatientRecordsModel();


                                    String catId = recObj.getInt("category_id") + "";
                                    //------------by dileep-------------
                                    String catName = catObj.getJSONObject("record_categories").getString("category");
//                                        JSONObject recordDetailsObjects = recObj.getJSONObject("recordinfo")
//                                                .getJSONObject("records");
                                    JSONArray recordDetailsArray = new JSONArray();
                                    recordDetailsArray.put(recObj);
                                    int recordId = recObj.getInt("record_id");// added by dileep
                                    model.setCatId(catId);//added by dileep
                                    model.setFieldDictionary(fieldDic.toString());// added by dileep
                                    model.setRecordId(recordId);// added by dileep
                                    model.setCatRecordData(recordDetailsArray.toString());
                                    model.setCatName(catName);
                                    //-----------------end------------------


                                    model.setPrimaryKey(fieldArr.getJSONObject(0).getString("name"));
                                    if (fieldArr.length() > 1) {
                                        model.setSecKey(fieldArr.getJSONObject(1).getString("name"));
                                        model.setSecData(recObj.getString(fieldArr.getJSONObject(1).getString("key")));
                                    }

                                    if (fieldArr.length() > 2) {
                                        model.setTernaryKey(fieldArr.getJSONObject(2).getString("name"));
                                        if (!recObj.has(fieldArr.getJSONObject(2).getString("key"))) {

                                        } else {
                                            model.setTernaryData(recObj.getString(fieldArr.getJSONObject(2).getString("key")));
                                        }
                                        //  model.setTernaryData(recObj.getString(fieldArr.getJSONObject(2).getString("key")));
                                    }

                                    model.setPrimaryData(recObj.getString(fieldArr.getJSONObject(0).getString("key")));

                                    if (recObj.has("url")) {
                                        model.setFileUrl(recObj.getString("url"));
                                    }

                                    patientRecordsModels.add(model);
                                }
                                recordViewAdapter.notifyDataSetChanged();
                            } else {
                                loader.setVisibility(View.GONE);
                                emptyText.setVisibility(View.VISIBLE);
                                emptyText.setText(getResources().getString(R.string.no_data_found));
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    emptyText.setVisibility(View.VISIBLE);
                    loader.setVisibility(View.GONE);
                    emptyText.setText(getResources().getString(R.string.data_load_failed));
                }


            }

            @Override
            public void onError(String err) {
                emptyText.setVisibility(View.VISIBLE);
                loader.setVisibility(View.GONE);
                emptyText.setText(getResources().getString(R.string.data_load_failed));
            }
        });
    }

    private void getEpisTreatPlan() {

        loader.setVisibility(View.VISIBLE);
        emptyText.setVisibility(View.VISIBLE);
        emptyText.setText(getResources().getString(R.string.loading_data));

        String url = ApiUrls.getEpisTreatmentPlan + "?doctor_id=" + ApiUrls.doctorId + "&episode_id=" + episodeID + "&patient_id=" + patientId + "&sharedOrCreated=1&encounter_id=" + encounterID;
        if (encounterID != 0) {
            url = ApiUrls.getEcntTreatmentPlan + "?doctor_id=" + ApiUrls.doctorId + "&episode_id=" + episodeID + "&patient_id=" + patientId + "&sharedOrCreated=1&encounter_id=" + encounterID;
        }
        apiCall.getRecordPref(url, "", this, new VolleyCallback() {
            @Override
            public void onSuccess(String result) {
                loader.setVisibility(View.GONE);
                emptyText.setVisibility(View.GONE);

//                try {
//                    JSONObject resObj = new JSONObject(result);
//                    JSONArray categoryArr = resObj.getJSONObject("response").getJSONArray("categories");
//                    if (categoryArr.length() > 0) {
//                        for (int i = 0; i < categoryArr.length(); i++) {
//                            JSONObject catObj = categoryArr.getJSONObject(i);
//                            if (catObj.getJSONObject("record_categories").getInt("id") == catId) {
//                                JSONArray recordArr = catObj.getJSONArray("records");
//                                if (recordArr.length() > 0) {
//                                    for (int j = 0; j < recordArr.length(); j++) {
//                                        JSONObject recObj = recordArr.getJSONObject(j);
//                                        JSONObject fieldDic = resObj.getJSONObject("response").getJSONObject("field-dictionary");
//                                        JSONArray fieldArr = fieldDic.getJSONArray(catId + "");
//                                        PatientRecordsModel model = new PatientRecordsModel();
//
//                                        model.setPrimaryKey(fieldArr.getJSONObject(0).getString("name"));
//                                        if (fieldArr.length() > 1) {
//                                            model.setSecKey(fieldArr.getJSONObject(1).getString("name"));
//                                            model.setSecData(recObj.getString(fieldArr.getJSONObject(1).getString("key")));
//                                        }
//
//                                        if (fieldArr.length() > 2) {
//                                            model.setTernaryKey(fieldArr.getJSONObject(2).getString("name"));
//                                            model.setTernaryData(recObj.getString(fieldArr.getJSONObject(2).getString("key")));
//                                        }
//
//                                        model.setPrimaryData(recObj.getString(fieldArr.getJSONObject(0).getString("key")));
//
//                                        if (recObj.has("url")) {
//                                            model.setFileUrl(recObj.getString("url"));
//                                        }
//
//                                        patientRecordsModels.add(model);
//                                    }
//                                } else {
//                                    loader.setVisibility(View.GONE);
//                                    emptyText.setVisibility(View.VISIBLE);
//                                    emptyText.setText(getResources().getString(R.string.no_data_found));
//                                }
//                            }
//                        }
//                    } else {
//
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                    emptyText.setVisibility(View.VISIBLE);
//                    loader.setVisibility(View.GONE);
//                    emptyText.setText(getResources().getString(R.string.data_load_failed));
//                }

                try {
                    JSONObject resObj = new JSONObject(result);
                    JSONArray categoryArr = resObj.getJSONObject("response").getJSONArray("categories");
                    if (categoryArr.length() > 0) {
                        for (int i = 0; i < categoryArr.length(); i++) {
                            JSONObject catObj = categoryArr.getJSONObject(i);
                            if (catObj.getJSONObject("record_categories").getInt("id") == catId) {
                                JSONArray recordArr = catObj.getJSONArray("records");
                                if (recordArr.length() > 0) {
                                    for (int j = 0; j < recordArr.length(); j++) {
                                        JSONObject recObj = recordArr.getJSONObject(j);
                                        JSONObject fieldDic = resObj.getJSONObject("response").getJSONObject("field-dictionary");
                                        // JSONArray fieldArr = fieldDic.getJSONArray(catId + "");
                                        PatientRecordsModel model = new PatientRecordsModel();


                                        String catId = recObj.getInt("category_id") + "";

                                        //------------by dileep-------------
                                        String catName = catObj.getJSONObject("record_categories").getString("category");
//                                        JSONObject recordDetailsObjects = recObj.getJSONObject("recordinfo")
//                                                .getJSONObject("records");
                                        JSONArray recordDetailsArray = new JSONArray();
                                        recordDetailsArray.put(recObj);
                                        int recordId = recObj.getInt("record_id");// added by dileep
                                        model.setCatId(catId);//added by dileep
                                        model.setFieldDictionary(fieldDic.toString());// added by dileep
                                        model.setRecordId(recordId);// added by dileep
                                        model.setCatRecordData(recordDetailsArray.toString());
                                        model.setCatName(catName);
                                        //-----------------end------------------


                                        //--------------------------------------

                                        if (fieldDic.get(catId + "") instanceof JSONArray) {
                                            JSONArray fieldArr = fieldDic.getJSONArray(catId + "");

                                            model.setPrimaryKey(fieldArr.getJSONObject(0).getString("name"));
                                            if (fieldArr.length() > 1) {
                                                model.setSecKey(fieldArr.getJSONObject(1).getString("name"));
                                                model.setSecData(recObj.getString(fieldArr.getJSONObject(1).getString("key")));
                                            }

                                            if (fieldArr.length() > 2) {
                                                model.setTernaryKey(fieldArr.getJSONObject(2).getString("name"));

                                                if (recObj.has("merge-2")) {
                                                    // String status = json.getString("status"));
                                                    model.setTernaryData(recObj.getString(fieldArr.getJSONObject(2).getString("key")));

                                                } else {

                                                }


//                                                if (!recObj.has(fieldArr.getJSONObject(2).getString("key"))) {
//
//                                                } else {
//                                                    model.setTernaryData(recObj.getString(fieldArr.getJSONObject(2).getString("key")));
//
//                                                }


                                                //  model.setTernaryData(recObj.getString(fieldArr.getJSONObject(2).getString("key")));
                                            }

                                            if (!recObj.has(fieldArr.getJSONObject(0).getString("key"))) {

                                                model.setPrimaryData("No attachment");


                                            } else {
                                                model.setPrimaryData(recObj.getString(fieldArr.getJSONObject(0).getString("key")));
                                            }

//                                            model.setPrimaryData(recObj.getString(fieldArr.getJSONObject(0).getString("key")));

                                            if (recObj.has("url")) {
                                                model.setFileUrl(recObj.getString("url"));
                                            }

                                            patientRecordsModels.add(model);


//                                            JSONArray fieldArr = fieldDic.getJSONArray(sharedObj.getJSONObject("recordinfo")
//                                                    .getJSONObject("records").getJSONObject("category").getInt("id") + "");


//                                            model.setPrimaryKey(fieldArr.getJSONObject(0).getString("name"));
//                                            if (fieldArr.length() > 1) {
//                                                model.setSecKey(fieldArr.getJSONObject(1).getString("name"));
//                                                model.setSecData(sharedObj.getJSONObject("recordinfo").getJSONObject("records").getString(fieldArr.getJSONObject(1).getString("key")));
//                                            }
//
//                                            if (fieldArr.length() > 2) {
//                                                model.setTernaryKey(fieldArr.getJSONObject(2).getString("name"));
//                                                if (!sharedObj.getJSONObject("recordinfo").getJSONObject("records").has(fieldArr.getJSONObject(2).getString("key"))) {
//                                                    // if (sharedObj.getJSONObject("recordinfo").getJSONObject("records").getString(fieldArr.getJSONObject(2).getString("key")) == null) {
//
//                                                } else {
//                                                    model.setTernaryData(sharedObj.getJSONObject("recordinfo").getJSONObject("records").getString(fieldArr.getJSONObject(2).getString("key")));
//                                                }
//
////                                                model.setTernaryData(sharedObj.getJSONObject("recordinfo").getJSONObject("records").getString(fieldArr.getJSONObject(2).getString("key")));
//                                            }
//
//                                            if (sharedObj.getJSONObject("recordinfo").getJSONObject("records").has(fieldArr.getJSONObject(0).getString("key"))) {
//                                                model.setPrimaryData(sharedObj.getJSONObject("recordinfo").getJSONObject("records").getString(fieldArr.getJSONObject(0).getString("key")));
//                                            }
//
//                                            model.setFileUrl("");
//                                            if (sharedObj.has("url")) {
//                                                model.setFileUrl(sharedObj.getJSONObject("recordinfo").getJSONObject("records").getString("url"));
//                                            }
//
//                                            //use to set patient name
//                                            model.setRecordName(sharedObj.getJSONObject("recordinfo").getJSONObject("records").getString("patient"));
//
//                                            shareRecordsModel.add(model);
                                        } else {
                                            JSONObject fieldArr = fieldDic.getJSONObject(catId + "");

                                            model.setPrimaryKey(fieldArr.getJSONObject("0").getString("name"));
                                            if (fieldArr.length() > 1) {
                                                model.setSecKey(fieldArr.getJSONObject("1").getString("name"));
                                                model.setSecData(recObj.getString(fieldArr.getJSONObject("1").getString("key")));
                                            }

                                            if (fieldArr.length() > 2) {
                                                model.setTernaryKey(fieldArr.getJSONObject("2").getString("name"));

                                                if (recObj.has("merge-2")) {
                                                    // String status = json.getString("status"));
                                                    model.setTernaryData(recObj.getString(fieldArr.getJSONObject("2").getString("key")));

                                                } else {

                                                }

//                                                if (!recObj.has(fieldArr.getJSONObject("2").getString("key"))) {
//
//                                                } else {
//                                                    model.setTernaryData(recObj.getString(fieldArr.getJSONObject("2").getString("key")));
//
//                                                }


//                                                if (recObj.getString(fieldArr.getJSONObject("2").getString("key"))==null) {
//                                                    // if (sharedObj.getJSONObject("recordinfo").getJSONObject("records").getString(fieldArr.getJSONObject(2).getString("key")) == null) {
//
//                                                } else {
//                                                    model.setTernaryData(recObj.getString(fieldArr.getJSONObject("2").getString("key")));
//                                                }

                                                //model.setTernaryData(recObj.getString(fieldArr.getJSONObject("2").getString("key")));
                                            }

                                            if (!recObj.has(fieldArr.getJSONObject("0").getString("key"))) {
                                                model.setPrimaryData("no");
                                            } else {
                                                model.setPrimaryData(recObj.getString(fieldArr.getJSONObject("0").getString("key")));

                                            }

                                            // model.setPrimaryData(recObj.getString(fieldArr.getJSONObject("0").getString("key")));

                                            if (recObj.has("url")) {
                                                model.setFileUrl(recObj.getString("url"));
                                            }

                                            patientRecordsModels.add(model);
                                        }

                                        //---------------------------------------------


//                                        model.setPrimaryKey(fieldArr.getJSONObject(0).getString("name"));
//                                        if (fieldArr.length() > 1) {
//                                            model.setSecKey(fieldArr.getJSONObject(1).getString("name"));
//                                            model.setSecData(recObj.getString(fieldArr.getJSONObject(1).getString("key")));
//                                        }
//
//                                        if (fieldArr.length() > 2) {
//                                            model.setTernaryKey(fieldArr.getJSONObject(2).getString("name"));
//                                            model.setTernaryData(recObj.getString(fieldArr.getJSONObject(2).getString("key")));
//                                        }
//
//                                        model.setPrimaryData(recObj.getString(fieldArr.getJSONObject(0).getString("key")));
//
//                                        if (recObj.has("url")) {
//                                            model.setFileUrl(recObj.getString("url"));
//                                        }
//
//                                        patientRecordsModels.add(model);
                                    }
                                } else {
                                    loader.setVisibility(View.GONE);
                                    emptyText.setVisibility(View.VISIBLE);
                                    emptyText.setText(getResources().getString(R.string.no_data_found));
                                }
                            }
                        }
                    } else {

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    emptyText.setVisibility(View.VISIBLE);
                    loader.setVisibility(View.GONE);
                    emptyText.setText(getResources().getString(R.string.data_load_failed));
                }


            }

            @Override
            public void onError(String err) {
                emptyText.setVisibility(View.VISIBLE);
                loader.setVisibility(View.GONE);
                emptyText.setText(getResources().getString(R.string.data_load_failed));
            }
        });
    }
}
