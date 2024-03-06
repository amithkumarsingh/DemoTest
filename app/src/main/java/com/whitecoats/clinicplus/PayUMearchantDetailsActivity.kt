package com.whitecoats.clinicplus

import android.app.ProgressDialog
import android.graphics.PorterDuff
import android.os.Build
import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.text.HtmlCompat
import com.android.volley.Request
import com.whitecoats.clinicplus.apis.ApiGetPostMethodCalls
import com.whitecoats.clinicplus.apis.ApiUrls
import com.whitecoats.clinicplus.utils.ErrorHandlerClass.errorHandler
import org.json.JSONException
import org.json.JSONObject

class PayUMearchantDetailsActivity : AppCompatActivity() {
    private var otpLoading: ProgressDialog? = null
    private var jsonValue: JSONObject? = null
    private var detailsHeaderText: TextView?=null
    private lateinit var nameEditText: EditText
    private lateinit var phoneEditText: EditText
    private lateinit var emailEditText: EditText
    private lateinit var sendButton: Button

    var emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"
    private var globalApiCall: ApiGetPostMethodCalls? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payu_merchant_details)
        val toolbar = findViewById<Toolbar>(R.id.payUMerchantDetailsToolbar)
        toolbar.title = "Payu Merchant Details"
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        val upArrow = resources.getDrawable(R.drawable.ic_arrow_back)
        upArrow.setColorFilter(resources.getColor(R.color.colorWhite), PorterDuff.Mode.SRC_ATOP)
        supportActionBar!!.setHomeAsUpIndicator(upArrow)
        globalApiCall = ApiGetPostMethodCalls()
        detailsHeaderText = findViewById(R.id.detailsHeaderText)
        nameEditText = findViewById(R.id.name)
        phoneEditText = findViewById(R.id.phone)
        emailEditText = findViewById(R.id.email)
        sendButton = findViewById(R.id.sendButton)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            val payUTextDetailsHtml =
                "<h5>Create your PayU account by submitting the following details and start collecting payments from patients. Once your account is created, please <a target=\"_blank\" href=\"https://clients.whitecoats.com/onboarding.paymoney.com\">login here</a> with your email id and complete your profile (<a target=\"_blank\" href=\"https://www.youtube.com/watch?v=4dTUTH-mHjw\">Complete bank verification and document upload</a>). For any support, email whitecoats.support@payu.in</h5>"
            val result = HtmlCompat.fromHtml(payUTextDetailsHtml, HtmlCompat.FROM_HTML_MODE_LEGACY)
            detailsHeaderText?.text = result
            detailsHeaderText?.movementMethod = LinkMovementMethod.getInstance()
        } else {
            val payUTextDetailsHtml =
                "<h5>Create your PayU account by submitting the following details and start collecting payments from patients. Once your account is created, please <a target=\"_blank\" href=\"https://clients.whitecoats.com/onboarding.paymoney.com\">login here</a> with your email id and complete your profile (<a target=\"_blank\" href=\"https://www.youtube.com/watch?v=4dTUTH-mHjw\">Complete bank verification and document upload</a>). For any support, email whitecoats.support@payu.in</h5>"
            val result = HtmlCompat.fromHtml(payUTextDetailsHtml, HtmlCompat.FROM_HTML_MODE_LEGACY)
            detailsHeaderText?.text = result
            detailsHeaderText?.movementMethod = LinkMovementMethod.getInstance()
        }
        sendButton.setOnClickListener(View.OnClickListener {
            val nameString = nameEditText.text.toString().trim { it <= ' ' }
            val phoneString = phoneEditText.text.toString().trim { it <= ' ' }
            val emailString = emailEditText.text.toString().trim { it <= ' ' }
            if (nameString.isEmpty() && phoneString.isEmpty() && emailString.isEmpty()) {
                val Name = "Enter name"
                nameEditText.error = Name
                val phone = "Enter 10 digit phone number"
                phoneEditText.error = phone
                val email = "Enter email id"
                emailEditText.error = email
                Toast.makeText(
                    this@PayUMearchantDetailsActivity,
                    "Please enter all required details",
                    Toast.LENGTH_SHORT
                ).show()
            } else if (nameString.isEmpty()) {
                val Name = "Enter name"
                nameEditText.error = Name
            } else if (phoneString.isEmpty() || phoneString.length < 10) {
                val phone = "Enter 10 digit phone number"
                phoneEditText.error = phone
            } else if (emailString.isEmpty()) {
                val email = "Enter email id"
                emailEditText.error = email
            } else {
                createMerchant(nameString, phoneString, emailString)
            }
        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // handle arrow click here
        if (item.itemId == android.R.id.home) {
            finish()
        }
        return super.onOptionsItemSelected(item)
    }

    //added on 23rd 2020
    fun createMerchant(nameValue: String?, phoneValue: String?, EmailValue: String?) {
        otpLoading = ProgressDialog(this@PayUMearchantDetailsActivity)
        otpLoading!!.setMessage(resources.getString(R.string.wait_while_creating))
        otpLoading!!.setTitle(resources.getString(R.string.common_please_wait_loading_message))
        otpLoading!!.setProgressStyle(ProgressDialog.STYLE_SPINNER)
        otpLoading!!.setCancelable(false)
        otpLoading!!.show()

        val URL = ApiUrls.createMerchant
        try {
            jsonValue = JSONObject()
            jsonValue!!.put("name", nameValue)
            jsonValue!!.put("phone", phoneValue)
            jsonValue!!.put("email", EmailValue)
        } catch (e: Exception) {
        }
        globalApiCall!!.volleyApiRequestData(
            URL,
            Request.Method.POST,
            jsonValue,
            this,
            object : VolleyCallback {
                override fun onSuccess(result: String) {
                    try {
                        val response = JSONObject(result)
                        otpLoading!!.dismiss()
                        val responseValue = response.getInt("response")
                        if (responseValue == 1) {
                            Toast.makeText(
                                this@PayUMearchantDetailsActivity,
                                "Your PayU merchant account has been created successfully",
                                Toast.LENGTH_SHORT
                            ).show()
                            SettingsFormActivity.isPayUMerchantCreate = true
                            finish()
                        } else {
                        }
                    } catch (e: Exception) {
                        otpLoading!!.dismiss()
                        e.printStackTrace()
                    }
                }

                override fun onError(err: String) {
                    otpLoading!!.dismiss()
                    errorHandler(this@PayUMearchantDetailsActivity, err)
                }
            })
    }

    fun trimMessage(json: String?, key: String?): String? {
        var trimmedString: String? = null
        trimmedString = try {
            val obj = JSONObject(json)
            obj.getString(key)
        } catch (e: JSONException) {
            e.printStackTrace()
            return null
        }
        return trimmedString
    }

    //Somewhere that has access to a context
    fun displayMessage(toastString: String?) {
        Toast.makeText(applicationContext, toastString, Toast.LENGTH_LONG).show()
    }
}