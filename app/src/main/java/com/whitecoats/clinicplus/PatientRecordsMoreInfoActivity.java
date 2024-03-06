package com.whitecoats.clinicplus;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.whitecoats.clinicplus.apis.ApiUrls;
import com.whitecoats.clinicplus.utils.ErrorHandlerClass;
import com.zoho.salesiqembed.ZohoSalesIQ;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class PatientRecordsMoreInfoActivity extends AppCompatActivity {

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
       // Log.d("Category Id", catId);
      //  Log.d("Records Details", recordDetailObjStr);

        ZohoSalesIQ.Tracking.setPageTitle("Patient Records More Info Page");

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


            if (catId.equalsIgnoreCase("21")) {
//                Log.d("Record Array", recordDetailObjStr);
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
                            if (recordDetailObj.getString("record_id").equals(recordId)) {

                                for (int i = 0; i < fieldDic.length(); i++) {
                                    LinearLayout linearLayout = new LinearLayout(this);
                                    JSONObject fieldObj = fieldDic.getJSONObject(i);
                                    TextView fieldName = new TextView(this);
                                    fieldName.setText(fieldObj.getString("name"));
                                    fieldName.setTextSize(20);
                                    fieldName.setLayoutParams(params);
                                    linearLayout.addView(fieldName);
//                                    Log.d("Field Name", fieldObj.getString("name"));

                                    if (recordDetailObj.has(Integer.toString(fieldObj.getInt("key")))) {
                                        if (recordDetailObj.has(Integer.toString(fieldObj.getInt("key")))) {
                                            TextView fieldValue = new TextView(this);
                                            fieldValue.setText(recordDetailObj.getString(fieldObj.getString("key")));
                                            if (fieldObj.getString("name").equalsIgnoreCase("Attachment")) {
                                                if (recordDetailObj.getString(fieldObj.getString("key")).equalsIgnoreCase("yes")) {
                                                    fieldValue.setText(getResources().getString(R.string.view_attachment));
                                                    fieldValue.setTextColor(getResources().getColor(R.color.colorAccent));
                                                    fieldValue.setOnClickListener(new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View view) {
                                                            try {
                                                                getFileFromUrl(recordDetailObj.getString("url"));
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
                            if (recordDetailObj.getString("record_id").equals(recordId)) {
                                for (int i = 0; i < fieldDic.length(); i++) {
                                    if (i != 3) {
                                        LinearLayout linearLayout = new LinearLayout(this);
                                        JSONObject fieldObj = fieldDic.getJSONObject(i + "");
                                        TextView fieldName = new TextView(this);
                                        fieldName.setText(fieldObj.getString("name"));
                                        fieldName.setTextSize(20);
                                        fieldName.setLayoutParams(params);
                                        linearLayout.addView(fieldName);
//                                        Log.d("Field Name", fieldObj.getString("name"));

                                        if (fieldObj.get("key") instanceof String) {
                                            if (recordDetailObj.has(fieldObj.getString("key"))) {
                                                TextView fieldValue = new TextView(this);
                                                fieldValue.setText(recordDetailObj.getString(fieldObj.getString("key")));
                                                if (fieldObj.getString("name").equalsIgnoreCase("Attachment")) {
                                                    if (recordDetailObj.getString(fieldObj.getString("key")).equalsIgnoreCase("yes")) {
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

                                        } else if (recordDetailObj.has(Integer.toString(fieldObj.getInt("key")))) {
                                            if (recordDetailObj.has(Integer.toString(fieldObj.getInt("key")))) {
                                                TextView fieldValue = new TextView(this);
                                                fieldValue.setText(recordDetailObj.getString(fieldObj.getString("key")));
                                                if (fieldObj.getString("name").equalsIgnoreCase("Attachment")) {
                                                    if (recordDetailObj.getString(fieldObj.getString("key")).equalsIgnoreCase("yes")) {
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

//        }//end


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
                ErrorHandlerClass.INSTANCE.errorHandler(PatientRecordsMoreInfoActivity.this, err);            }
        });
    }
}
