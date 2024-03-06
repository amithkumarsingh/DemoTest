package com.whitecoats.clinicplus.activities;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.whitecoats.clinicplus.MyClinicGlobalClass;
import com.whitecoats.clinicplus.R;
import com.whitecoats.clinicplus.apis.ApiUrls;
import com.whitecoats.clinicplus.utils.ErrorHandlerClass;
import com.whitecoats.clinicplus.utils.RestUtils;
import com.whitecoats.clinicplus.viewmodels.CreateMerchantViewModel;

import org.json.JSONObject;

public class ConnectExistingAccount extends AppCompatActivity {
    public static final String TAG = ConnectExistingAccount.class.getSimpleName();
    private Context mContext;
    Button btn_cntAccount;
    EditText et_mid, et_key, et_salt;
    TextView error_mid, error_key, error_salt;
    CreateMerchantViewModel createMerchantViewModel;
    private MyClinicGlobalClass globalClass;
    ProgressBar progressExistingBar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect_existing_account);
        globalClass = (MyClinicGlobalClass) getApplicationContext();
        Toolbar toolbar = findViewById(R.id.existingAccountToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        Drawable upArrow = getResources().getDrawable(R.drawable.ic_arrow_back);
        upArrow.setColorFilter(getResources().getColor(R.color.colorWhite), PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);
        mContext = getApplicationContext();
        initialize();
    }

    public void initialize() {
        createMerchantViewModel = new ViewModelProvider(this).get(CreateMerchantViewModel.class);
        createMerchantViewModel.init();
        progressExistingBar = findViewById(R.id.progressExiBar_loader);
        et_mid = findViewById(R.id.et_merchantMid);
        et_key = findViewById(R.id.et_merchatKey);
        et_salt = findViewById(R.id.et_merchantSalt);
        error_mid = findViewById(R.id.midErrorText);
        error_key = findViewById(R.id.keyErrorText);
        error_salt = findViewById(R.id.saltErrorText);
        btn_cntAccount = findViewById(R.id.bt_createAccount);

        btn_cntAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (globalClass.isOnline()) {
                    if(validateInput()) {
                        progressExistingBar.setVisibility(View.VISIBLE);
                        postExistingAccResponse();
                    }
                }

            }
        });
    }

    public void postExistingAccResponse() {

        JSONObject jsonValue = new JSONObject();
        try {
            jsonValue.put("mid", et_mid.getText().toString());
            jsonValue.put("key", et_key.getText().toString());
            jsonValue.put("salt", et_salt.getText().toString());
            createMerchantViewModel.connectExistingAcc(this, jsonValue).observe(ConnectExistingAccount.this, new Observer<String>() {
                @Override
                public void onChanged(String s) {
                    Log.d(TAG, "existingResponse" + s);
                    progressExistingBar.setVisibility(View.GONE);
                    try {
                        JSONObject jsonObject = new JSONObject(s);
                        if (jsonObject.getInt("status_code") == 200) {
                            MyClinicGlobalClass.logUserActionEvent(ApiUrls.doctorId, getString(R.string.PaymentConnectAccount
                            ),null);
                            JSONObject response = new JSONObject(s).getJSONObject("response").getJSONObject("response");
                            int createFlag = response.getInt(RestUtils.TAG_CREATE_FLAG);
                            if(createFlag == 1){
                                CreateNewAccount createNewAccount = new CreateNewAccount();
                                createNewAccount.getPaymentMerchantApi();
                            }
                            finish();
                        }else {
                            ErrorHandlerClass.INSTANCE.errorHandler(ConnectExistingAccount.this, s);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public boolean validateInput() {
        boolean noError = true;
        error_mid.setVisibility(View.GONE);
        error_key.setVisibility(View.GONE);
        error_salt.setVisibility(View.GONE);

        if (et_mid.getText().toString().equalsIgnoreCase("")) {
            noError = false;
            error_mid.setVisibility(View.VISIBLE);
        } else if (et_key.getText().toString().equalsIgnoreCase("")) {
            noError = false;
            error_key.setVisibility(View.VISIBLE);
        } else if (et_salt.getText().toString().equalsIgnoreCase("")) {
            noError = false;
            et_salt.setVisibility(View.VISIBLE);
        }
        return noError;
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
