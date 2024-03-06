package com.whitecoats.clinicplus;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

//<<<<<<< HEAD:app/src/main/java/com/whitecoats/clinicplus/PatientRecordsSharedMoreInfoActivity.java
import com.whitecoats.adapter.HomeSharedRecordsListAdapter;
import com.whitecoats.adapter.PatientRecordViewAdapter;
//=======
//import com.caregivers.adapter.HomeSharedRecordsListAdapter;
//import com.caregivers.adapter.PatientRecordViewAdapter;
//import com.caregivers.in.casechannels.CaseChannelEvaluationAdapter;
//import com.caregivers.in.patientsharedrecords.PatientSharedRecordsActivity;
//>>>>>>> 9cd2f719774959ef939e2ca0ebf3be149f2ac7b1:app/src/main/java/com/caregivers/in/PatientRecordsSharedMoreInfoActivity.java
import com.whitecoats.clinicplus.adapters.DashBoardSharedRecordsListAdapter;
import com.whitecoats.clinicplus.apis.ApiUrls;
import com.whitecoats.clinicplus.casechannels.CaseChannelEvaluationAdapter;
import com.whitecoats.clinicplus.utils.ErrorHandlerClass;
import com.zoho.salesiqembed.ZohoSalesIQ;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class PatientRecordsSharedMoreInfoActivity extends AppCompatActivity {

    private String recordDetailObjStr;
    private String recordId;
    private LinearLayout moreInfoCard;
    private String catId;

    private ListView dietListView;
    private TextView dietDate, dietPrev, dietNext;
    private int dietPlanNum = 0, totalDietPlan = 0;
    private ArrayList<HashMap<String, String>> dietListData;
//    private DietPlanListadapter dietPlanListadapter;

    private ProgressDialog otpLoading;
    private Toolbar toolbar;
    private PatientRecordsApi apiCall;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_records_more_info);

        apiCall = new PatientRecordsApi();
        moreInfoCard = findViewById(R.id.recordMoreList);
        catId = getIntent().getStringExtra("CatId");
        recordDetailObjStr = getIntent().getStringExtra("RecordDetail");
        recordId = getIntent().getStringExtra("RecordId");
//        Log.d("Category Id", catId);
//        Log.d("Records Details", recordDetailObjStr);

        //getting the toolbar
        toolbar = findViewById(R.id.recordMoreToolbar);
        toolbar.setTitle(getIntent().getStringExtra("CatName"));
        toolbar.setTitleTextColor(getResources().getColor(R.color.colorWhite));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        Drawable upArrow = getResources().getDrawable(R.drawable.ic_arrow_back);
        upArrow.setColorFilter(getResources().getColor(R.color.colorWhite), PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);

        ZohoSalesIQ.Tracking.setPageTitle("Patient Shared Records More Info Page");


//        if (PatientRecordViewAdapter.patientRecordClickFlag == 0) {

        if (catId.equalsIgnoreCase("21")) {
//            Log.d("Record Array", recordDetailObjStr);
            String fieldDic = getIntent().getStringExtra("RecordFieldDic");
//            showDietData(fieldDic);
        } else {

            try {
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT);
                params.weight = 0.5f;
                JSONArray recordArr = new JSONArray(recordDetailObjStr);
                if (!catId.equalsIgnoreCase("30")) {
                    for (int k = 0; k < recordArr.length(); k++) {

                        final JSONObject recordDetailObj = recordArr.getJSONObject(k);//new JSONObject(recordDetailObjStr);
                        JSONArray fieldDic = new JSONObject(getIntent().getStringExtra("RecordField")).getJSONArray(catId);//recordDetailObj.getJSONObject("field-dictionary").getJSONArray(catId);
//                        JSONArray recordData = recordDetailObj.getJSONArray("records");
//                        Log.d("Reocrd Data #######", recordDetailObj.toString());
//                        for (int j = 0; j < recordData.length(); j++) {
//                            JSONObject record = recordData.getJSONObject(j);
//                            Log.d("Recooooooooood", recordDetailObj.getString("id"));
                        String recordIdDetails;
                        if (HomeSharedRecordsListAdapter.shareRecordClickFlag == 1 || PatientRecordViewAdapter.patientRecordClickFlag == 1 || DashBoardSharedRecordsListAdapter.shareRecordClickFlag == 1) { // added by dileep 8th july 2020 in if  || DashBoardSharedRecordsListAdapter.shareRecordClickFlag == 1
                            recordIdDetails = recordDetailObj.getString("record_id");

                        } else if(CaseChannelEvaluationAdapter.caseChannelRecordClickFlag == 1){
                            recordIdDetails = recordDetailObj.getJSONObject("category").getString("id");
                        }else {
                            recordIdDetails = recordDetailObj.getString("id");
                        }

                        if (recordIdDetails.equals(recordId)) {

                            for (int i = 0; i < fieldDic.length(); i++) {
                                LinearLayout linearLayout = new LinearLayout(this);
                                JSONObject fieldObj = fieldDic.getJSONObject(i);
                                TextView fieldName = new TextView(this);
                                fieldName.setText(fieldObj.getString("name"));
                                fieldName.setTextSize(20);
                                fieldName.setLayoutParams(params);
                                linearLayout.addView(fieldName);
//                                Log.d("Field Name", fieldObj.getString("name"));

                                JSONObject recordInfoOBj = recordDetailObj.getJSONObject("recordinfo");//added by dileep
                                final JSONObject recordsOBj = recordInfoOBj.getJSONObject("records");//added by dileep

                                if (recordsOBj.has(Integer.toString(fieldObj.getInt("key")))) {
                                    if (recordsOBj.has(Integer.toString(fieldObj.getInt("key")))) {
                                        TextView fieldValue = new TextView(this);
                                        fieldValue.setText(recordsOBj.getString(fieldObj.getString("key")));
                                        if (fieldObj.getString("name").equalsIgnoreCase("Attachment")) {
                                            if (recordsOBj.getString(fieldObj.getString("key")).equalsIgnoreCase("yes")) {
                                                fieldValue.setText(getResources().getString(R.string.view_attachment));
                                                fieldValue.setTextColor(getResources().getColor(R.color.colorAccent));
                                                fieldValue.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View view) {
                                                        try {
                                                            getFileFromUrl(recordsOBj.getString("url"));
                                                        } catch (Exception e) {
                                                            e.printStackTrace();
                                                        }
                                                    }
                                                });
                                            } else {
                                                fieldValue.setText(getResources().getString(R.string.no_attachment));
                                            }
                                        }
                                        fieldValue.setTextSize(20);
                                        fieldValue.setLayoutParams(params);
                                        linearLayout.addView(fieldValue);
                                    }
                                }

                                linearLayout.setPadding(20, 20, 20, 20);
                                linearLayout.setWeightSum(1);
                                moreInfoCard.addView(linearLayout);

                                View separator = new LinearLayout(this);
                                LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 5);
                                params1.setMargins(10, 10, 10, 10);
                                separator.setLayoutParams(params1);
                                separator.setBackgroundColor(getResources().getColor(R.color.colorBackground));
                                moreInfoCard.addView(separator);
                            }
                            break;
                        }
//                        }
                    }
                } else {
                    for (int k = 0; k < recordArr.length(); k++) {
                        JSONObject recordDetailObj = recordArr.getJSONObject(k);//new JSONObject(recordDetailObjStr);
                        JSONObject fieldDic = new JSONObject(getIntent().getStringExtra("RecordField")).getJSONObject(catId);
                        ;//recordDetailObj.getJSONObject("field-dictionary").getJSONObject(catId);
//                        JSONArray recordData = recordDetailObj.getJSONArray("records");

//                        for (int j = 0; j < recordData.length(); j++) {
//                            JSONObject record = recordData.getJSONObject(j);
//                        Log.d("Recooooooooood", recordDetailObj.getString("id"));

                        String recordIdDetails;
                        if (HomeSharedRecordsListAdapter.shareRecordClickFlag == 1 || DashBoardSharedRecordsListAdapter.shareRecordClickFlag == 1) { // added by dileep 8th july 2020 in if  || DashBoardSharedRecordsListAdapter.shareRecordClickFlag == 1
                            recordIdDetails = recordDetailObj.getString("record_id");
                        } else {
                            recordIdDetails = recordDetailObj.getString("id");

                        }


                        if (recordIdDetails.equals(recordId)) {
                            for (int i = 0; i < fieldDic.length(); i++) {
                                if (i != 3) {
                                    LinearLayout linearLayout = new LinearLayout(this);
                                    JSONObject fieldObj = fieldDic.getJSONObject(i + "");
                                    TextView fieldName = new TextView(this);
                                    fieldName.setText(fieldObj.getString("name"));
                                    fieldName.setTextSize(20);
                                    fieldName.setLayoutParams(params);
                                    linearLayout.addView(fieldName);
//                                    Log.d("Field Name", fieldObj.getString("name"));

                                    JSONObject recordInfoOBj = recordDetailObj.getJSONObject("recordinfo");//added by dileep
                                    final JSONObject recordsOBj = recordInfoOBj.getJSONObject("records");//added by dileep


                                    if (fieldObj.get("key") instanceof String) {
                                        if (recordsOBj.has(fieldObj.getString("key"))) {
                                            TextView fieldValue = new TextView(this);
                                            fieldValue.setText(recordsOBj.getString(fieldObj.getString("key")));
                                            if (fieldObj.getString("name").equalsIgnoreCase("Attachment")) {
                                                if (recordsOBj.getString(fieldObj.getString("key")).equalsIgnoreCase("yes")) {
                                                    fieldValue.setText(getResources().getString(R.string.view_attachment));
                                                    fieldValue.setOnClickListener(new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View view) {

                                                        }
                                                    });
                                                } else {
                                                    fieldValue.setText(getResources().getString(R.string.no_attachment));
                                                }
                                            }
                                            fieldValue.setTextSize(20);
                                            fieldValue.setLayoutParams(params);
                                            linearLayout.addView(fieldValue);
                                        }

                                    } else if (recordsOBj.has(Integer.toString(fieldObj.getInt("key")))) {
                                        if (recordsOBj.has(Integer.toString(fieldObj.getInt("key")))) {
                                            TextView fieldValue = new TextView(this);
                                            fieldValue.setText(recordsOBj.getString(fieldObj.getString("key")));
                                            if (fieldObj.getString("name").equalsIgnoreCase("Attachment")) {
                                                if (recordsOBj.getString(fieldObj.getString("key")).equalsIgnoreCase("yes")) {
                                                    fieldValue.setText(getResources().getString(R.string.view_attachment));
                                                    fieldValue.setOnClickListener(new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View view) {

                                                        }
                                                    });
                                                } else {
                                                    fieldValue.setText(getResources().getString(R.string.no_attachment));
                                                }
                                            }
                                            fieldValue.setTextSize(20);
                                            fieldValue.setLayoutParams(params);
                                            linearLayout.addView(fieldValue);
                                        }
                                    }

                                    linearLayout.setPadding(20, 20, 20, 20);
                                    linearLayout.setWeightSum(1);
                                    moreInfoCard.addView(linearLayout);

                                    View separator = new LinearLayout(this);
                                    LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 5);
                                    params1.setMargins(10, 10, 10, 10);
                                    separator.setLayoutParams(params1);
                                    separator.setBackgroundColor(getResources().getColor(R.color.colorBackground));
                                    moreInfoCard.addView(separator);
                                }
                            }
                            break;
                        }
//                        }
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

//        }//end  shared record if condition
//        else {
//
//            if (catId.equalsIgnoreCase("21")) {
//                Log.d("Record Array", recordDetailObjStr);
//                String fieldDic = getIntent().getStringExtra("RecordFieldDic");
////            showDietData(fieldDic);
//            } else {
//
//                try {
//                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT);
//                    params.weight = 0.5f;
//                    JSONArray recordArr = new JSONArray(recordDetailObjStr);
//                    if (!catId.equalsIgnoreCase("30")) {
//                        for (int k = 0; k < recordArr.length(); k++) {
//
//                            final JSONObject recordDetailObj = recordArr.getJSONObject(k);//new JSONObject(recordDetailObjStr);
//                            JSONArray fieldDic = new JSONObject(getIntent().getStringExtra("RecordField")).getJSONArray(catId);//recordDetailObj.getJSONObject("field-dictionary").getJSONArray(catId);
////                        JSONArray recordData = recordDetailObj.getJSONArray("records");
//                            Log.d("Reocrd Data #######", recordDetailObj.toString());
////                        for (int j = 0; j < recordData.length(); j++) {
////                            JSONObject record = recordData.getJSONObject(j);
//                            Log.d("Recooooooooood", recordDetailObj.getString("record_id"));
//                            if (recordDetailObj.getString("record_id").equals(recordId)) {
//
//                                for (int i = 0; i < fieldDic.length(); i++) {
//                                    LinearLayout linearLayout = new LinearLayout(this);
//                                    JSONObject fieldObj = fieldDic.getJSONObject(i);
//                                    TextView fieldName = new TextView(this);
//                                    fieldName.setText(fieldObj.getString("name"));
//                                    fieldName.setTextSize(20);
//                                    fieldName.setLayoutParams(params);
//                                    linearLayout.addView(fieldName);
//                                    Log.d("Field Name", fieldObj.getString("name"));
//
//                                    if (recordDetailObj.has(Integer.toString(fieldObj.getInt("key")))) {
//                                        if (recordDetailObj.has(Integer.toString(fieldObj.getInt("key")))) {
//                                            TextView fieldValue = new TextView(this);
//                                            fieldValue.setText(recordDetailObj.getString(fieldObj.getString("key")));
//                                            if (fieldObj.getString("name").equalsIgnoreCase("Attachment")) {
//                                                if (recordDetailObj.getString(fieldObj.getString("key")).equalsIgnoreCase("yes")) {
//                                                    fieldValue.setText(getResources().getString(R.string.view_attachment));
//                                                    fieldValue.setTextColor(getResources().getColor(R.color.colorAccent));
//                                                    fieldValue.setOnClickListener(new View.OnClickListener() {
//                                                        @Override
//                                                        public void onClick(View view) {
//                                                            try {
//                                                                getFileFromUrl(recordDetailObj.getString("url"));
//                                                            } catch (Exception e) {
//                                                                e.printStackTrace();
//                                                            }
//                                                        }
//                                                    });
//                                                } else {
//                                                    fieldValue.setText(getResources().getString(R.string.no_attachment));
//                                                }
//                                            }
//                                            fieldValue.setTextSize(20);
//                                            fieldValue.setLayoutParams(params);
//                                            linearLayout.addView(fieldValue);
//                                        }
//                                    }
//
//                                    linearLayout.setPadding(20, 20, 20, 20);
//                                    linearLayout.setWeightSum(1);
//                                    moreInfoCard.addView(linearLayout);
//
//                                    View separator = new LinearLayout(this);
//                                    LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 5);
//                                    params1.setMargins(10, 10, 10, 10);
//                                    separator.setLayoutParams(params1);
//                                    separator.setBackgroundColor(getResources().getColor(R.color.colorBackground));
//                                    moreInfoCard.addView(separator);
//                                }
//                                break;
//                            }
////                        }
//                        }
//                    } else {
//                        for (int k = 0; k < recordArr.length(); k++) {
//                            JSONObject recordDetailObj = recordArr.getJSONObject(k);//new JSONObject(recordDetailObjStr);
//                            JSONObject fieldDic = new JSONObject(getIntent().getStringExtra("RecordField")).getJSONObject(catId);
//                            ;//recordDetailObj.getJSONObject("field-dictionary").getJSONObject(catId);
////                        JSONArray recordData = recordDetailObj.getJSONArray("records");
//
////                        for (int j = 0; j < recordData.length(); j++) {
////                            JSONObject record = recordData.getJSONObject(j);
//                            Log.d("Recooooooooood", recordDetailObj.getString("record_id"));
//                            if (recordDetailObj.getString("record_id").equals(recordId)) {
//                                for (int i = 0; i < fieldDic.length(); i++) {
//                                    if (i != 3) {
//                                        LinearLayout linearLayout = new LinearLayout(this);
//                                        JSONObject fieldObj = fieldDic.getJSONObject(i + "");
//                                        TextView fieldName = new TextView(this);
//                                        fieldName.setText(fieldObj.getString("name"));
//                                        fieldName.setTextSize(20);
//                                        fieldName.setLayoutParams(params);
//                                        linearLayout.addView(fieldName);
//                                        Log.d("Field Name", fieldObj.getString("name"));
//
//                                        if (fieldObj.get("key") instanceof String) {
//                                            if (recordDetailObj.has(fieldObj.getString("key"))) {
//                                                TextView fieldValue = new TextView(this);
//                                                fieldValue.setText(recordDetailObj.getString(fieldObj.getString("key")));
//                                                if (fieldObj.getString("name").equalsIgnoreCase("Attachment")) {
//                                                    if (recordDetailObj.getString(fieldObj.getString("key")).equalsIgnoreCase("yes")) {
//                                                        fieldValue.setText(getResources().getString(R.string.view_attachment));
//                                                        fieldValue.setOnClickListener(new View.OnClickListener() {
//                                                            @Override
//                                                            public void onClick(View view) {
//
//                                                            }
//                                                        });
//                                                    } else {
//                                                        fieldValue.setText(getResources().getString(R.string.no_attachment));
//                                                    }
//                                                }
//                                                fieldValue.setTextSize(20);
//                                                fieldValue.setLayoutParams(params);
//                                                linearLayout.addView(fieldValue);
//                                            }
//
//                                        } else if (recordDetailObj.has(Integer.toString(fieldObj.getInt("key")))) {
//                                            if (recordDetailObj.has(Integer.toString(fieldObj.getInt("key")))) {
//                                                TextView fieldValue = new TextView(this);
//                                                fieldValue.setText(recordDetailObj.getString(fieldObj.getString("key")));
//                                                if (fieldObj.getString("name").equalsIgnoreCase("Attachment")) {
//                                                    if (recordDetailObj.getString(fieldObj.getString("key")).equalsIgnoreCase("yes")) {
//                                                        fieldValue.setText(getResources().getString(R.string.view_attachment));
//                                                        fieldValue.setOnClickListener(new View.OnClickListener() {
//                                                            @Override
//                                                            public void onClick(View view) {
//
//                                                            }
//                                                        });
//                                                    } else {
//                                                        fieldValue.setText(getResources().getString(R.string.no_attachment));
//                                                    }
//                                                }
//                                                fieldValue.setTextSize(20);
//                                                fieldValue.setLayoutParams(params);
//                                                linearLayout.addView(fieldValue);
//                                            }
//                                        }
//
//                                        linearLayout.setPadding(20, 20, 20, 20);
//                                        linearLayout.setWeightSum(1);
//                                        moreInfoCard.addView(linearLayout);
//
//                                        View separator = new LinearLayout(this);
//                                        LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 5);
//                                        params1.setMargins(10, 10, 10, 10);
//                                        separator.setLayoutParams(params1);
//                                        separator.setBackgroundColor(getResources().getColor(R.color.colorBackground));
//                                        moreInfoCard.addView(separator);
//                                    }
//                                }
//                                break;
//                            }
////                        }
//                        }
//                    }
//
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//
//        }//end


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                finish();
                //PatientSharedRecordsActivity.patientSharedRecordFlag = 0;
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

       // PatientSharedRecordsActivity.patientSharedRecordFlag = 0;

    }

    public void getFileFromUrl(String filrUrl) {

        final ProgressDialog loadingDialog = new ProgressDialog(this);
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
                ErrorHandlerClass.INSTANCE.errorHandler(PatientRecordsSharedMoreInfoActivity.this, err);
            }
        });
    }

//    private void getDietDetail(String recordId) {
//        RequestQueue queue = Volley.newRequestQueue(PatientRecordsMoreInfoActivity.this);
//        String url = appConfigClass.getDietDetail + "?dietid=" + recordId;
//
//        dietListData.clear();
//        otpLoading = new ProgressDialog(PatientRecordsMoreInfoActivity.this);
//        otpLoading.setMessage(getResources().getString(R.string.fetching_your_plan));
//        otpLoading.setTitle(getResources().getString(R.string.diet_plan));
//        otpLoading.setProgressStyle(ProgressDialog.STYLE_SPINNER);
//        otpLoading.setCancelable(false);
//        otpLoading.show();
//
//        // prepare the Request
//        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, url, null,
//                new Response.Listener<JSONObject>()
//                {
//                    @Override
//                    public void onResponse(JSONObject response) {
//                        // display response
//                        Log.d("Diet Plan Response", response.toString());
//                        otpLoading.dismiss();
//                        try {
//                            JSONObject rootObj = response.getJSONObject("response");
//                            JSONArray dietPlanArr = rootObj.getJSONArray("dietdetails");
//                            dietDate.setText(dietPlanArr.getJSONObject(dietPlanNum).getString("date"));
//                            totalDietPlan = dietPlanArr.length();
////                            for(int i=0; i<dietPlanArr.length(); i++) {
//                            JSONObject dietPlanObj = dietPlanArr.getJSONObject(dietPlanNum);
//                            JSONArray diet = dietPlanObj.getJSONArray("diet");
//                            for(int j=0; j<diet.length(); j++) {
//                                JSONObject meal = diet.getJSONObject(j);
//                                HashMap<String, String> map = new HashMap<>();
//                                if(!meal.getString("meal").equalsIgnoreCase("")) {
//                                    map.put("MealName", meal.getString("meal"));
//                                    map.put("MealNo", "Meal no. " + (j + 1));
//                                    map.put("MealTime", meal.getString("time"));
//
//                                    dietListData.add(map);
//                                }
//                            }
////                                break;
////                            }
//
//                            dietPlanListadapter.notifyDataSetChanged();
//                        }catch (Exception e) {
//                            e.printStackTrace();
//                            otpLoading.dismiss();
//                            Toast.makeText(RecordsMoreInfoActivity.this, getResources().getString(R.string.error_fetching_your_diet_plan), Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                },
//                new Response.ErrorListener()
//                {
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
//                        Log.d("Diet Plan Error", error.toString());
//                        otpLoading.dismiss();
//                        Toast.makeText(RecordsMoreInfoActivity.this, getResources().getString(R.string.error_fetching_your_diet_plan), Toast.LENGTH_SHORT).show();
//                    }
//                }
//        ) {
//            @Override
//            public Map<String, String> getHeaders() throws AuthFailureError {
//                HashMap<String, String> headers = new HashMap<>();
//                headers.put("Content-Type", "application/json");
//                headers.put("App-Origin", appConfigClass.appOrigin);
//                headers.put("Authorization", "Bearer " + AppConfigClass.loginToken);//eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJodHRwOi8vNGRkYjMxNjkubmdyb2suaW8vYXBpL3YxL2F1dGgvdG9rZW4iLCJpYXQiOjE1MTgwODcyMzAsImV4cCI6MTUxOTI5NjgzMCwibmJmIjoxNTE4MDg3MjMwLCJqdGkiOiJ2UEJnY09hVGdGOU9YaFh4Iiwic3ViIjozMzk1LCJwcnYiOiIyM2JkNWM4OTQ5ZjYwMGFkYjM5ZTcwMWM0MDA4NzJkYjdhNTk3NmY3In0.Y3wU_NBXXhw--b75x4iwo6fmHQQzMLkla2gHrheWqeU");
//                return headers;
//            }
//        };
//
//        // add it to the RequestQueue
//        queue.add(getRequest);
//    }

//    private void showDietData(String fieldDic) {
//        setContentView(R.layout.diet_record_more_info);
//        moreInfoCard = findViewById(R.id.recordMoreList);
//        dietListView = findViewById(R.id.recordDietList);
//        dietDate = findViewById(R.id.recordDietDate);
//        dietPrev = findViewById(R.id.recordDietPrev);
//        dietNext = findViewById(R.id.recordDietNext);
//        dietListData = new ArrayList<>();
//        dietPlanListadapter = new DietPlanListadapter(RecordsMoreInfoActivity.this, dietListData);
//        dietListView.setAdapter(dietPlanListadapter);
//        dietDate.setVisibility(View.GONE);
//        dietNext.setVisibility(View.GONE);
//        dietPrev.setVisibility(View.GONE);
//
//        dietNext.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                dietPlanNum++;
//                Log.d("Diet Number +", "" + dietPlanNum);
//                if(dietPlanNum < totalDietPlan) {
//                    getDietDetail(recordId);
//                } else {
//                    dietPlanNum--;
//                }
//            }
//        });
//
//        dietPrev.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                dietPlanNum--;
//                Log.d("Diet Number", "" + dietPlanNum);
//                if(dietPlanNum >= 0) {
//                    getDietDetail(recordId);
//                } else {
//                    dietPlanNum = 0;
//                }
//            }
//        });
//
////        JSONArray fieldDic = recordDetailObj.getJSONObject("field-dictionary").getJSONArray(catId);
//        try {
//            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT);
//            params.weight = 0.5f;
//            JSONArray fieldDicObj = new JSONObject(fieldDic).getJSONArray(catId);
//            JSONArray recordData = new JSONArray(recordDetailObjStr);
//            Log.d("Reocrd Data #######", recordData.toString());
//            for (int j = 0; j < recordData.length(); j++) {
//                JSONObject record = recordData.getJSONObject(j);
//                Log.d("Recooooooooood", record.getString("record_id"));
//                if (record.getString("record_id").equals(recordId)) {
//
//                    for (int i = 0; i < fieldDicObj.length(); i++) {
//                        LinearLayout linearLayout = new LinearLayout(this);
//                        JSONObject fieldObj = fieldDicObj.getJSONObject(i);
//                        TextView fieldName = new TextView(this);
//                        fieldName.setText(fieldObj.getString("name"));
//                        fieldName.setTextSize(20);
//                        fieldName.setLayoutParams(params);
//                        linearLayout.addView(fieldName);
//                        Log.d("Field Name", fieldObj.getString("name"));
//
//                        if (record.has(Integer.toString(fieldObj.getInt("key")))) {
//                            if (record.has(Integer.toString(fieldObj.getInt("key")))) {
//                                TextView fieldValue = new TextView(this);
//                                fieldValue.setText(record.getString(fieldObj.getString("key")));
//                                if (fieldObj.getString("name").equalsIgnoreCase("Attachment")) {
//                                    if (record.getString(fieldObj.getString("key")).equalsIgnoreCase("yes")) {
//                                        fieldValue.setText(getResources().getString(R.string.view_attachment));
//                                        fieldValue.setOnClickListener(new View.OnClickListener() {
//                                            @Override
//                                            public void onClick(View view) {
//
//                                            }
//                                        });
//                                    } else {
//                                        fieldValue.setText(getResources().getString(R.string.no_attachment));
//                                    }
//                                }
//                                fieldValue.setTextSize(20);
//                                fieldValue.setLayoutParams(params);
//                                linearLayout.addView(fieldValue);
//                            }
//                        }
//
//                        linearLayout.setPadding(20, 20, 20, 20);
//                        linearLayout.setWeightSum(1);
//                        moreInfoCard.addView(linearLayout);
//
//                        View separator = new LinearLayout(this);
//                        LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 5);
//                        params1.setMargins(10, 10, 10, 10);
//                        separator.setLayoutParams(params1);
//                        separator.setBackgroundColor(getResources().getColor(R.color.colorBackground));
//                        moreInfoCard.addView(separator);
//                    }
//
//                    LinearLayout linearLayout = new LinearLayout(this);
//                    TextView fieldName = new TextView(this);
//                    fieldName.setText(getResources().getString(R.string.please_select_record_message));
//                    fieldName.setTextSize(20);
//                    fieldName.setLayoutParams(params);
//                    linearLayout.addView(fieldName);
//
//                    TextView fieldValue = new TextView(this);
//                    fieldValue.setText(getResources().getString(R.string.view_details));
//                    fieldValue.setTextColor(getResources().getColor(R.color.colorPrimary));
//                    fieldValue.setTextSize(18);
//                    fieldValue.setLayoutParams(params);
//                    fieldValue.setTypeface(null, Typeface.BOLD);
//
//                    fieldValue.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View view) {
//                            Log.d("Viewing Diet Plan", "@@@@@@@@@@@@@");
//                            dietDate.setVisibility(View.VISIBLE);
//                            dietNext.setVisibility(View.VISIBLE);
//                            dietPrev.setVisibility(View.VISIBLE);
//                            getDietDetail(recordId);
//                        }
//                    });
//                    linearLayout.addView(fieldValue);
//                    linearLayout.setPadding(20, 20, 20, 20);
//                    linearLayout.setWeightSum(1);
//                    moreInfoCard.addView(linearLayout);
//
//                    break;
//                }
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

//    private void getConsultNoteDetail(String noteID) {
//        RequestQueue queue = Volley.newRequestQueue(PatientRecordsMoreInfoActivity.this);
//        String url = appConfigClass.getConsultNoteDetail + "?noteid=" + noteID;
//
//        // prepare the Request
//        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, url, null,
//                new Response.Listener<JSONObject>()
//                {
//                    @Override
//                    public void onResponse(JSONObject response) {
//                        // display response
//                        Log.d("Consult Note Response", response.toString());
//                        try {
//                            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT);
//                            params.weight = 0.5f;
//                            JSONObject rootObj = response.getJSONObject("response");
//                            JSONArray dataArry = rootObj.getJSONArray("data");
//                            for(int i=0; i<dataArry.length(); i++) {
//                                JSONObject noteData = dataArry.getJSONObject(i);
//
//                                LinearLayout linearLayout = new LinearLayout(PatientRecordsMoreInfoActivity.this);
//                                TextView fieldName = new TextView(PatientRecordsMoreInfoActivity.this);
//                                fieldName.setText("Date");
//                                fieldName.setTextSize(20);
//                                fieldName.setLayoutParams(params);
//                                linearLayout.addView(fieldName);
//
//                                fieldName = new TextView(PatientRecordsMoreInfoActivity.this);
//                                String date = noteData.getString("created_at");
//                                date = date.split(" ")[0];
//                                fieldName.setText(date);
//                                fieldName.setTextSize(20);
//                                fieldName.setLayoutParams(params);
//                                linearLayout.addView(fieldName);
//
//                                linearLayout.setPadding(20, 20, 20, 20);
//                                linearLayout.setWeightSum(1);
//                                moreInfoCard.addView(linearLayout);
//                                View separator = new LinearLayout(PatientRecordsMoreInfoActivity.this);
//                                LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 5);
//                                params1.setMargins(10, 10, 10, 10);
//                                separator.setLayoutParams(params1);
//                                separator.setBackgroundColor(getResources().getColor(R.color.colorBackground));
//                                moreInfoCard.addView(separator);
//
//                                linearLayout = new LinearLayout(PatientRecordsMoreInfoActivity.this);
//
//                                fieldName = new TextView(PatientRecordsMoreInfoActivity.this);
//                                fieldName.setText("Description");
//                                fieldName.setTextSize(20);
//                                fieldName.setLayoutParams(params);
//                                linearLayout.addView(fieldName);
//
//                                fieldName = new TextView(PatientRecordsMoreInfoActivity.this);
//                                fieldName.setText(noteData.getString("description"));
//                                fieldName.setTextSize(20);
//                                fieldName.setLayoutParams(params);
//                                linearLayout.addView(fieldName);
//
//                                linearLayout.setPadding(20, 20, 20, 20);
//                                linearLayout.setWeightSum(1);
//                                moreInfoCard.addView(linearLayout);
//
//                                separator = new LinearLayout(PatientRecordsMoreInfoActivity.this);
//                                params1 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 5);
//                                params1.setMargins(10, 10, 10, 10);
//                                separator.setLayoutParams(params1);
//                                separator.setBackgroundColor(getResources().getColor(R.color.colorBackground));
//                                moreInfoCard.addView(separator);
//                            }
//
//                        }catch (Exception e) {
//                            e.printStackTrace();
//                        }
//                    }
//                },
//                new Response.ErrorListener()
//                {
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
//                        Log.d("Diet Plan Error", error.toString());
//                    }
//                }
//        ) {
//            @Override
//            public Map<String, String> getHeaders() throws AuthFailureError {
//                HashMap<String, String> headers = new HashMap<>();
//                headers.put("Content-Type", "application/json");
//                headers.put("App-Origin", appConfigClass.appOrigin);
//                headers.put("Authorization", "Bearer " + AppConfigClass.loginToken);//eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJodHRwOi8vNGRkYjMxNjkubmdyb2suaW8vYXBpL3YxL2F1dGgvdG9rZW4iLCJpYXQiOjE1MTgwODcyMzAsImV4cCI6MTUxOTI5NjgzMCwibmJmIjoxNTE4MDg3MjMwLCJqdGkiOiJ2UEJnY09hVGdGOU9YaFh4Iiwic3ViIjozMzk1LCJwcnYiOiIyM2JkNWM4OTQ5ZjYwMGFkYjM5ZTcwMWM0MDA4NzJkYjdhNTk3NmY3In0.Y3wU_NBXXhw--b75x4iwo6fmHQQzMLkla2gHrheWqeU");
//                return headers;
//            }
//        };
//
//        // add it to the RequestQueue
//        queue.add(getRequest);
//    }
}
