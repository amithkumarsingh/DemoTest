package com.whitecoats.clinicplus.activities

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.PorterDuff
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import androidx.appcompat.widget.Toolbar
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.android.volley.Request
import com.android.volley.Request.Method
import com.whitecoats.clinicplus.R
import com.whitecoats.clinicplus.apis.ApiUrls
import com.whitecoats.clinicplus.utils.ErrorHandlerClass
import com.whitecoats.clinicplus.viewmodels.CommonViewModel
import org.json.JSONObject
import java.util.*


class PayUPaymentSetupScreen : AppCompatActivity() {
    private lateinit var toolbar: Toolbar
    private lateinit var tvHowItWorks: TextView
    private lateinit var cardViewParent: CardView
    private lateinit var constPayu: ConstraintLayout
    private lateinit var llSelectGateway: LinearLayout
    private lateinit var gatewaySpinner: Spinner
    private lateinit var imgSelect: ImageView
    private lateinit var etMerchantID: EditText
    private lateinit var etKey: EditText
    private lateinit var etSalt: EditText
    private lateinit var tvAuthHeader: TextView
    private lateinit var etAuthHeader: EditText
    private lateinit var swPayU: SwitchCompat
    private lateinit var btnSaveMerchant: Button
    private lateinit var tvStatusText: TextView
    private lateinit var tvStatusValue: TextView
    private lateinit var tvAccountStatusMessage: TextView
    private lateinit var imgRefreshStatus: ImageView
    private var merchantID: String = ""
    private var merchantKey: String = ""
    private var merchantSalt: String = ""
    private var merchantAuthHeader: String = ""
    private lateinit var dialog: Dialog
    private lateinit var commonViewModel: CommonViewModel
    private var merchantDetailsObj: JSONObject = JSONObject()
    private lateinit var tvSwitchText: TextView
    private var defaultPaymentMode = ""
    private var payUHowItWorksLink: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pay_upayment_setup_screen)
        commonViewModel =
            ViewModelProvider(this@PayUPaymentSetupScreen)[CommonViewModel::class.java]
        toolbar = findViewById(R.id.payUPaymentToolbar)
        tvHowItWorks = findViewById(R.id.tv_how_it_works)
        cardViewParent = findViewById(R.id.cardView_parent)
        constPayu = findViewById(R.id.const_payu)
        llSelectGateway = findViewById(R.id.ll_selectGateway)
        gatewaySpinner = findViewById(R.id.gateway_spinner)
        imgSelect = findViewById(R.id.img_select)
        etMerchantID = findViewById(R.id.et_merchantID)
        etKey = findViewById(R.id.et_Key)
        etSalt = findViewById(R.id.et_salt)
        tvAuthHeader = findViewById(R.id.tv_Auth_Header)
        etAuthHeader = findViewById(R.id.et_Auth_Header)
        swPayU = findViewById(R.id.sw_payU)
        btnSaveMerchant = findViewById(R.id.btn_save_Merchant)
        tvSwitchText = findViewById(R.id.tv_switch_text)
        tvStatusText = findViewById(R.id.tv_Status_text)
        tvStatusValue = findViewById(R.id.tv_Status_value)
        tvAccountStatusMessage = findViewById(R.id.tv_account_status_message)
        imgRefreshStatus = findViewById(R.id.img_refresh_status)
        btnSaveMerchant.isEnabled = false
        btnSaveMerchant.setBackgroundColor(ContextCompat.getColor(this, R.color.light_blue))
        btnSaveMerchant.setTextColor(ContextCompat.getColor(this, R.color.white))
        swPayU.isEnabled = false
        tvSwitchText.alpha = 0.5f
        tvSwitchText.isClickable = false

        etMerchantID.addTextChangedListener(textWatcher)
        etKey.addTextChangedListener(textWatcher)
        etSalt.addTextChangedListener(textWatcher)
        etAuthHeader.addTextChangedListener(textWatcher)



        toolbar.title = "I Have PayU Account"
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        val backArrow = ContextCompat.getDrawable(this, R.drawable.ic_arrow_back)
        backArrow!!.setColorFilter(
            ContextCompat.getColor(this, R.color.colorWhite),
            PorterDuff.Mode.SRC_ATOP
        )
        supportActionBar!!.setHomeAsUpIndicator(backArrow)
        toolbar.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }

        getPaymentOptionDetails()
        checkMerchantStatus("firstTime")
        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item, resources
                .getStringArray(R.array.PayUModeOptions)
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        gatewaySpinner.adapter = adapter
        gatewaySpinner.setSelection(0)
        gatewaySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>?, view: View, i: Int, l: Long) {
                gatewaySpinner.setSelection(i)
                if (gatewaySpinner.selectedItem.toString().equals("PayU Biz", ignoreCase = true)) {
                    tvAuthHeader.visibility = View.GONE
                    etAuthHeader.visibility = View.GONE
                    checkFieldsForEmptyValues()
                } else {
                    tvAuthHeader.visibility = View.VISIBLE
                    etAuthHeader.visibility = View.VISIBLE
                    checkFieldsForEmptyValues()
                }
            }

            override fun onNothingSelected(adapterView: AdapterView<*>?) {
                return
            }
        }

        tvHowItWorks.setOnClickListener {
            if (payUHowItWorksLink != "") {
                val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(payUHowItWorksLink))
                startActivity(browserIntent)
            }
        }

        swPayU.setOnClickListener { view ->
            if (view is SwitchCompat) {
                swPayU.isChecked = view.isChecked
                setDefaultPaymentMode(swPayU.isChecked)
            }
        }
        tvSwitchText.setOnClickListener {
            if (swPayU.isChecked) {
                swPayU.isChecked = false
                setDefaultPaymentMode(false)
            } else {
                swPayU.isChecked = true
                setDefaultPaymentMode(true)
            }
        }
        imgRefreshStatus.setOnClickListener {
            checkMerchantStatus("checkStatus")
        }

        btnSaveMerchant.setOnClickListener {
            savePayUPaymentDetails()
        }

    }

    private fun setDefaultPaymentMode(isChecked: Boolean) {
        val url = ApiUrls.setDefaultPaymentMode
        val jsonReq = JSONObject()
        // jsonReq.put("interface_id", ApiUrls.doctorId)
        if (isChecked) {
            jsonReq.put("default_payment_mode", "payu")
        } else {
            jsonReq.put("default_payment_mode", "offline")
        }
        commonViewModel.commonViewModelCall(url, jsonReq, Method.POST).observe(
            this@PayUPaymentSetupScreen
        ) { result ->
            try {
                val resultObj = JSONObject(result)
                if (resultObj.getInt("status_code") == 200) {
                    val resObj = resultObj.optJSONObject("response")
                    val res = resObj!!.getString("response")
                    if (res.equals("success", ignoreCase = true)) {
                        Toast.makeText(
                            this@PayUPaymentSetupScreen,
                            "Payment mode set successfully",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    swPayU.isChecked = !isChecked
                    ErrorHandlerClass.errorHandler(this@PayUPaymentSetupScreen, result)
                }
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
                swPayU.isChecked = !isChecked
            }
        }

    }

    private fun checkMerchantStatus(call: String) {
        if (call.equals("checkStatus", ignoreCase = true)) {
            showCustomProgressAlertDialog("", "Checking the status")
        }
        val url = ApiUrls.checkMerchantStatus
        commonViewModel.commonViewModelCall(url, JSONObject(), Method.GET).observe(
            this@PayUPaymentSetupScreen
        ) { result ->
            try {
                val resultObj = JSONObject(result)
                if (resultObj.getInt("status_code") == 200) {
                    val resObj = resultObj.optJSONObject("response")
                    val res = resObj!!.optJSONObject("response")
                    if (res!!.has("status")) {
                        if (res.getString("status") != "" && res.getString("status") != "null")
                            tvStatusValue.text = res.getString("status")
                        tvStatusValue.setTextColor(Color.parseColor(res.getString("status_color")))
                    }
                    if (res.has("message")) {
                        if (res.getString("message") != "" && res.getString("message") != "null")
                            tvAccountStatusMessage.text = res.getString("message")
                    }

                    if (res.getInt("is_disable_payu_update") == 1) {
                        etMerchantID.isEnabled = false
                        etKey.isEnabled = false
                        etSalt.isEnabled = false
                        etAuthHeader.isEnabled = false
                        btnSaveMerchant.isEnabled = false
                        gatewaySpinner.isEnabled = false
                        imgRefreshStatus.isEnabled = false
                    } else {
                        etMerchantID.isEnabled = true
                        etKey.isEnabled = true
                        etSalt.isEnabled = true
                        etAuthHeader.isEnabled = true
                        btnSaveMerchant.isEnabled = true
                        gatewaySpinner.isEnabled = true
                        imgRefreshStatus.isEnabled = true

                    }


                } else {
                    ErrorHandlerClass.errorHandler(this@PayUPaymentSetupScreen, result)
                }
                dialog.dismiss()
            } catch (e: Exception) {
                e.printStackTrace()
                dialog.dismiss()
            }

        }

    }

    private fun getPaymentOptionDetails() {
        showCustomProgressAlertDialog("Please Wait", "Fetching details")
        val url = ApiUrls.getPaymentOptionsDetails
        commonViewModel.commonViewModelCall(url, JSONObject(), Method.GET).observe(
            this@PayUPaymentSetupScreen
        ) { result ->
            try {
                val resultObj = JSONObject(result)
                if (resultObj.getInt("status_code") == 200) {
                    val resObj = resultObj.optJSONObject("response")
                    val res = resObj!!.optJSONObject("response")
                    defaultPaymentMode = res!!.getString("default_payment_mode")
                    if (defaultPaymentMode.equals("payu", ignoreCase = true)) {
                        swPayU.isChecked = true
                    }
                    if (res.has("payu_external_link1")) {
                        payUHowItWorksLink = res.getString("payu_external_link1")
                    }
                    if (res.has("merchant_details")) {
                        val item: Any = res.get("merchant_details")
                        Log.d("itemBank", item.toString())
                        if (item is JSONObject) {
                            merchantDetailsObj = res.getJSONObject("merchant_details")
                            if (merchantDetailsObj.getString("gateway_id")
                                    .equals("payu", ignoreCase = true)
                            ) {
                                gatewaySpinner.setSelection(0)
                                tvAuthHeader.visibility = View.GONE
                                etAuthHeader.visibility = View.GONE

                            } else {
                                gatewaySpinner.setSelection(1)
                                tvAuthHeader.visibility = View.VISIBLE
                                etAuthHeader.visibility = View.VISIBLE
                            }
                            if (merchantDetailsObj.getString("merchant_id") != "") {
                                merchantID = merchantDetailsObj.getString("merchant_id")
                                etMerchantID.setText(merchantID)
                            }
                            if (merchantDetailsObj.getString("key") != "") {
                                merchantKey = merchantDetailsObj.getString("key")
                                etKey.setText(merchantKey)
                            }
                            if (merchantDetailsObj.getString("salt") != "") {
                                merchantSalt = merchantDetailsObj.getString("salt")
                                etSalt.setText(merchantSalt)
                            }
                            if (merchantDetailsObj.getString("auth_header") != "") {
                                merchantAuthHeader = merchantDetailsObj.getString("auth_header")
                                etAuthHeader.setText(merchantAuthHeader)
                            }
                        }
                    }
                    dialog.dismiss()

                } else {
                    dialog.dismiss()
                    ErrorHandlerClass.errorHandler(this@PayUPaymentSetupScreen, result)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                dialog.dismiss()
            }

        }

    }


    private fun savePayUPaymentDetails() {
        showCustomProgressAlertDialog("Please Wait", "Please wait saving your UPI ID's")
        val addedPayUObj = JSONObject()
        if (gatewaySpinner.selectedItem.toString().equals("PayU Biz", ignoreCase = true)) {
            addedPayUObj.put("gateway_id", "payu")
            addedPayUObj.put("auth_header", "")
        } else {
            addedPayUObj.put("gateway_id", "payumoney")
            addedPayUObj.put("auth_header", etAuthHeader.text.toString())
        }
        addedPayUObj.put("merchant_id", etMerchantID.text.toString())
        addedPayUObj.put("key", etKey.text.toString())
        addedPayUObj.put("salt", etSalt.text.toString())
        val jsonRes = JSONObject()
        // jsonRes.put("interface_id", ApiUrls.doctorId)
        jsonRes.put("paymentMode", "payu")
        jsonRes.put("merchant_details", addedPayUObj)
        val url = ApiUrls.savePaymentOptions
        commonViewModel.commonViewModelCall(url, jsonRes, Method.POST).observe(
            this@PayUPaymentSetupScreen
        ) { result ->
            try {
                val resultObj = JSONObject(result)
                if (resultObj.getInt("status_code") == 200) {
                    val resObj = resultObj.optJSONObject("response")
                    val res = resObj!!.getString("response")
                    if (res.equals("success", ignoreCase = true)) {
                        Toast.makeText(
                            this@PayUPaymentSetupScreen,
                            "PayU details saved successfully",
                            Toast.LENGTH_SHORT
                        ).show()
                        finish()
                    }
                    dialog.dismiss()
                } else {
                    dialog.dismiss()
                    ErrorHandlerClass.errorHandler(this@PayUPaymentSetupScreen, result)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                dialog.dismiss()
            }
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        super.getOnBackPressedDispatcher().onBackPressed()
        finish()
    }

    private fun showCustomProgressAlertDialog(
        title: String,
        progressVal: String

    ) {
        val builder = AlertDialog.Builder(this@PayUPaymentSetupScreen)
        builder.setTitle(title)
        builder.setCancelable(false)
        val customLayout = LayoutInflater.from(this@PayUPaymentSetupScreen)
            .inflate(R.layout.custom_progrss_bar, null)
        builder.setView(customLayout)
        dialog = builder.create()
        val tvBody = customLayout.findViewById<TextView>(R.id.tv_value)
        tvBody.text = progressVal
        dialog.show()
    }

    private val textWatcher: TextWatcher = object : TextWatcher {
        override fun beforeTextChanged(charSequence: CharSequence, i: Int, i2: Int, i3: Int) {}
        override fun onTextChanged(charSequence: CharSequence, i: Int, i2: Int, i3: Int) {
            checkFieldsForEmptyValues()
        }

        override fun afterTextChanged(editable: Editable) {}
    }

    private fun checkFieldsForEmptyValues() {
        val merchantID: String = etMerchantID.text.toString()
        val merchantKey: String = etKey.text.toString()
        val merchantSalt: String = etSalt.text.toString()
        val merchantHeader: String = etAuthHeader.text.toString()
        if (gatewaySpinner.selectedItem.toString().equals("PayU Biz", ignoreCase = true)) {
            if (merchantID.isNotEmpty() && merchantKey.isNotEmpty() && merchantSalt.isNotEmpty()) {
                btnSaveMerchant.isEnabled = true
                btnSaveMerchant.setBackgroundColor(
                    ContextCompat.getColor(
                        this,
                        R.color.colorAccent
                    )
                )
                btnSaveMerchant.setTextColor(ContextCompat.getColor(this, R.color.white))
                swPayU.isEnabled = true
                tvSwitchText.alpha = 1f
                tvSwitchText.isClickable = true


            } else {
                btnSaveMerchant.isEnabled = false
                btnSaveMerchant.setBackgroundColor(ContextCompat.getColor(this, R.color.light_blue))
                btnSaveMerchant.setTextColor(ContextCompat.getColor(this, R.color.white))
                swPayU.isEnabled = false
                tvSwitchText.alpha = 0.5f
                tvSwitchText.isClickable = false

            }
        } else {
            if (merchantID.isNotEmpty() && merchantKey.isNotEmpty() && merchantSalt.isNotEmpty() && merchantHeader.isNotEmpty()) {
                btnSaveMerchant.isEnabled = true
                btnSaveMerchant.setBackgroundColor(
                    ContextCompat.getColor(
                        this,
                        R.color.colorAccent
                    )
                )
                btnSaveMerchant.setTextColor(ContextCompat.getColor(this, R.color.white))
                swPayU.isEnabled = true
                tvSwitchText.alpha = 1f
                tvSwitchText.isClickable = true


            } else {
                btnSaveMerchant.isEnabled = false
                btnSaveMerchant.setBackgroundColor(ContextCompat.getColor(this, R.color.light_blue))
                btnSaveMerchant.setTextColor(ContextCompat.getColor(this, R.color.white))
                swPayU.isEnabled = false
                tvSwitchText.isClickable = false
                tvSwitchText.alpha = 0.5f
            }
        }

    }
}