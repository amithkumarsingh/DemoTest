package com.whitecoats.clinicplus

import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Paint
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View.OnFocusChangeListener
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.android.volley.Request.Method
import com.onesignal.OSNotificationReceivedEvent
import com.onesignal.OneSignal
import com.whitecoats.clinicplus.apis.ApiGetPostMethodCalls
import com.whitecoats.clinicplus.apis.ApiUrls
import com.whitecoats.clinicplus.constants.AppConstants
import com.whitecoats.clinicplus.utils.ErrorHandlerClass.errorHandler
import com.whitecoats.clinicplus.viewmodels.CommonViewModel
import com.whitecoats.clinicplus.viewmodels.DoctorSessionViewModel
import org.json.JSONException
import org.json.JSONObject
import android.view.View as View1


class LoginActivity : AppCompatActivity() {
    private lateinit var signUpLayout: RelativeLayout
    private lateinit var signInLayout: RelativeLayout
    private lateinit var forgotPasswordLayout: RelativeLayout
    private lateinit var otpUpdateLayout: RelativeLayout
    private lateinit var signInPasswordText: EditText
    private lateinit var signInUserText: EditText
    private lateinit var forgotPasswordPhoneText: EditText
    private lateinit var otpText: EditText
    private lateinit var otpPasswordText: EditText
    private lateinit var otpConfirmPasswordText: EditText
    private lateinit var appPreference: SharedPreferences
    private var appDatabaseManager: AppDatabaseManager? = null
    private lateinit var jsonValue: JSONObject
    private lateinit var signUpUserText: EditText
    private lateinit var signUpMobileText: EditText
    private lateinit var signUpEmailText: EditText
    private lateinit var signUpQualificationText: EditText
    private lateinit var signUpRegistrationNoText: EditText
    private lateinit var signUpSpecializationText: EditText
    private lateinit var signUpFocusAreasText: EditText
    private lateinit var signUpExperienceText: EditText
    private lateinit var signUpReferralCodeText: EditText
    private var appUtilities: AppUtilities? = null
    private var notiPlayerId: String? = null
    private var notiPushNoti: String? = null
    private var doctorSessionViewModel: DoctorSessionViewModel? = null
    private val param = JSONObject()
    private var apiGetPostMethodCalls: ApiGetPostMethodCalls? = null
    private lateinit var dialog: Dialog
    private lateinit var commonViewModel: CommonViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        signUpLayout = findViewById(R.id.signUpLayout)
        signInLayout = findViewById(R.id.signInLayout)
        val signInCreateNow = findViewById<TextView>(R.id.signInCreateNow)
        val signUpLoginHere = findViewById<TextView>(R.id.signUpLoginHere)
        forgotPasswordLayout = findViewById(R.id.forgotPasswordLayout)
        val singInForgotPassword = findViewById<TextView>(R.id.singInForgotPassword)
        otpUpdateLayout = findViewById(R.id.otpUpdateLayout)
        val forgotPasswordButton = findViewById<Button>(R.id.forgotPasswordButton)
        signInUserText = findViewById(R.id.signInUserText)
        signInPasswordText = findViewById(R.id.signInPasswordText)
        val signInButton = findViewById<Button>(R.id.signInButton)
        forgotPasswordPhoneText = findViewById(R.id.forgotPasswordPhoneText)
        val otpUpdateButton = findViewById<Button>(R.id.otpUpdateButton)
        otpText = findViewById(R.id.otpText)
        otpPasswordText = findViewById(R.id.otpPasswordText)
        otpConfirmPasswordText = findViewById(R.id.otpConfirmPasswordText)
        val signUpButton = findViewById<Button>(R.id.signUpButton)
        signUpUserText = findViewById(R.id.signUpUserText)
        signUpMobileText = findViewById(R.id.signUpMobileText)
        signUpEmailText = findViewById(R.id.signUpEmailText)
        signUpQualificationText = findViewById(R.id.signUpQualificationText)
        signUpRegistrationNoText = findViewById(R.id.signUpRegistrationNoText)
        signUpSpecializationText = findViewById(R.id.signUpSpecializationText)
        signUpFocusAreasText = findViewById(R.id.signUpFocusAreasText)
        signUpExperienceText = findViewById(R.id.signUpExperienceText)
        signUpReferralCodeText = findViewById(R.id.signUpReferralCodeText)
        appUtilities = AppUtilities()
        commonViewModel = ViewModelProvider(this@LoginActivity)[CommonViewModel::class.java]
        apiGetPostMethodCalls = ApiGetPostMethodCalls()
        appPreference = applicationContext.getSharedPreferences(ApiUrls.appSharedPref, MODE_PRIVATE)
        appDatabaseManager = AppDatabaseManager(this)
        signInLayout.visibility = View1.VISIBLE
        singInForgotPassword.paintFlags =
            singInForgotPassword.paintFlags or Paint.UNDERLINE_TEXT_FLAG
        OneSignal.setNotificationWillShowInForegroundHandler { osNotificationReceivedEvent: OSNotificationReceivedEvent ->
            val data = osNotificationReceivedEvent.notification.additionalData
            osNotificationReceivedEvent.complete(osNotificationReceivedEvent.notification)
        }
        OneSignal.unsubscribeWhenNotificationsAreDisabled(true)
        OneSignal.setNotificationOpenedHandler(OSNotificationOpenedHandler(this@LoginActivity))
        val device = OneSignal.getDeviceState()
        val subscribed = device!!.isSubscribed
        if (subscribed) {
            // get player ID
            notiPlayerId = device.userId
            notiPushNoti = device.pushToken
            val editor = appPreference.edit()
            editor.putString(ApiUrls.playerId, notiPlayerId)
            editor.apply()
            sendPlayerId(0)
        }
        signUpButton.setOnClickListener {
            MyClinicGlobalClass.logUserActionEvent(
                ApiUrls.doctorId,
                getString(R.string.RegisterNowSignUp),
                null
            )
            registerUser()
        }
        signInButton.setOnClickListener {
            checkDataEntered()
            MyClinicGlobalClass.logUserActionEvent(
                ApiUrls.doctorId,
                getString(R.string.LoginSignInButton),
                null
            )
        }
        signInCreateNow.setOnClickListener {
            signUpLayout.visibility = View1.VISIBLE
            signInLayout.visibility = View1.GONE
            forgotPasswordLayout.visibility = View1.GONE
            otpUpdateLayout.visibility = View1.GONE
            MyClinicGlobalClass.logUserActionEvent(
                ApiUrls.doctorId,
                getString(R.string.SignupLoginRegisterNow),
                null
            )
        }
        signUpUserText.setOnFocusChangeListener { _: View1?, _: Boolean ->
            MyClinicGlobalClass.logUserActionEvent(
                ApiUrls.doctorId,
                getString(R.string.RegisterNowName),
                null
            )
        }
        signUpMobileText.setOnFocusChangeListener { _: View1?, _: Boolean ->
            MyClinicGlobalClass.logUserActionEvent(
                ApiUrls.doctorId,
                getString(R.string.RegisterNowMobileNo),
                null
            )
        }
        signUpEmailText.setOnFocusChangeListener { _: View1?, _: Boolean ->
            MyClinicGlobalClass.logUserActionEvent(
                ApiUrls.doctorId,
                getString(R.string.RegisterNowEmail),
                null
            )
        }
        signUpQualificationText.setOnFocusChangeListener { _: View1?, _: Boolean ->
            MyClinicGlobalClass.logUserActionEvent(
                ApiUrls.doctorId,
                getString(R.string.RegisterNowQualifications),
                null
            )
        }
        signUpSpecializationText.setOnFocusChangeListener { _: View1?, _: Boolean ->
            MyClinicGlobalClass.logUserActionEvent(
                ApiUrls.doctorId,
                getString(R.string.RegisterNowSpecialization),
                null
            )
        }
        signUpFocusAreasText.setOnFocusChangeListener { _: View1?, _: Boolean ->
            MyClinicGlobalClass.logUserActionEvent(
                ApiUrls.doctorId,
                getString(R.string.RegisterNowFocusAreas),
                null
            )
        }
        signUpLoginHere.setOnClickListener {
            MyClinicGlobalClass.logUserActionEvent(
                ApiUrls.doctorId,
                getString(R.string.RegisterNowLoginHere),
                null
            )
            signUpUserText.setText("")
            signUpEmailText.setText("")
            signUpMobileText.setText("")
            signUpQualificationText.setText("")
            signUpRegistrationNoText.setText("")
            signUpSpecializationText.setText("")
            signUpFocusAreasText.setText("")
            signUpExperienceText.setText("")
            signUpReferralCodeText.setText("")
            signInLayout.visibility = View1.VISIBLE
            signUpLayout.visibility = View1.GONE
            forgotPasswordLayout.visibility = View1.GONE
            otpUpdateLayout.visibility = View1.GONE
        }
        singInForgotPassword.setOnClickListener {
            signInLayout.visibility = View1.GONE
            signUpLayout.visibility = View1.GONE
            forgotPasswordLayout.visibility = View1.VISIBLE
            otpUpdateLayout.visibility = View1.GONE
            MyClinicGlobalClass.logUserActionEvent(
                ApiUrls.doctorId,
                getString(R.string.SignupLoginForgotPassword), null
            )
        }
        forgotPasswordButton.setOnClickListener { checkDataEnteredForgotPassword() }
        otpUpdateButton.setOnClickListener { checkDataEnteredUpdatePassword() }
        signInUserText.onFocusChangeListener = OnFocusChangeListener { _: View1?, _: Boolean ->
            MyClinicGlobalClass.logUserActionEvent(
                ApiUrls.doctorId, getString(R.string.LoginEmailIDPhone),
                null
            )
        }
        signInPasswordText.onFocusChangeListener = OnFocusChangeListener { _: View1?, _: Boolean ->
            MyClinicGlobalClass.logUserActionEvent(
                ApiUrls.doctorId, getString(R.string.LoginPassword),
                null
            )
        }
        signInPasswordText.setOnClickListener {
            MyClinicGlobalClass.logUserActionEvent(
                ApiUrls.doctorId, getString(R.string.LoginPassword),
                null
            )
        }
        doctorSessionViewModel = ViewModelProvider(this)[DoctorSessionViewModel::class.java]
        doctorSessionViewModel!!.init()
    }

    private fun checkDataEntered() {
        if (isEmpty(signInUserText)) {
            val signInUserText = resources.getString(R.string.enter_email_mobile)
            appUtilities!!.showFullyCustomToast(this, signInUserText)
        } else if (isEmpty(signInPasswordText)) {
            val signInUserText = resources.getString(R.string.enter_password)
            appUtilities!!.showFullyCustomToast(this, signInUserText)
        } else {
            showCustomProgressAlertDialog(
                resources.getString(R.string.common_sign_in),
                resources.getString(R.string.wait_while_sign_in)
            )
            val playerId = appPreference.getString(ApiUrls.playerId, "")
            val url = ApiUrls.loginUrl
            val jsonValue = JSONObject()
            jsonValue.put("user", signInUserText.text.toString())
            jsonValue.put("password", signInPasswordText.text.toString())
            jsonValue.put("player_id", playerId!!)
            jsonValue.put("platform", "android")

            commonViewModel.commonViewModelCall(url, jsonValue, Method.POST).observe(
                this@LoginActivity
            ) { result ->

                try {
                    //Process os success response
                    dialog.dismiss()
                    val responseObj = JSONObject(result)
                    if (responseObj.getInt("status_code") == 200) {
                        val response = responseObj.optJSONObject("response")
                        val rootObjUser = response!!.getJSONObject("userData")
                        ApiUrls.loginToken = response.getString("token")
                        ApiUrls.isDoctorOnly = rootObjUser.getInt("is_doctor_only")
                        ApiUrls.doctorId = rootObjUser.getInt("id")
                        ApiUrls.login_counter = rootObjUser.getInt("login_counter")

                        //get session id
                        sessionId
                        if (ApiUrls.login_counter == 1) {
                            MyClinicGlobalClass.logUserActionEvent(
                                ApiUrls.doctorId, getString(R.string.FirstTimeLogin),
                                null
                            )
                        } else {
                            MyClinicGlobalClass.logUserActionEvent(
                                ApiUrls.doctorId,
                                "NoFirstTimeLogin",
                                null
                            )
                        }
                        ApiUrls.lastLoginDate = rootObjUser.getString("last_loggedin")
                        val userManager = AppUserManager(
                            rootObjUser.getInt("id"),
                            "",
                            "",
                            0,
                            "",
                            "",
                            "",
                            response.getString("token"),
                            rootObjUser.getInt("is_doctor_only")
                        )
                        appDatabaseManager!!.addPatient(userManager)
                        Toast.makeText(
                            this@LoginActivity,
                            resources.getString(R.string.you_have_been_successfully_login),
                            Toast.LENGTH_SHORT
                        ).show()
                        getUserDetails(response)
                        sendPlayerId(ApiUrls.doctorId)
                        val editor = appPreference.edit()
                        editor.putBoolean(ApiUrls.isLoginStatus, true)
                        editor.apply()
                        val intent = Intent(this@LoginActivity, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        dialog.dismiss()
                        errorHandler(this@LoginActivity, result)
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }
        }
    }

    //get doctor session id
    private val sessionId: Unit
        get() {
            try {
                param.put("platform", "android")
            } catch (e: Exception) {
                e.printStackTrace()
            }
            doctorSessionViewModel!!.createDoctorSessionId(this@LoginActivity, param)
                .observe(this@LoginActivity) { s: String? ->
                    Log.d("Response", s!!)
                    try {
                        val resObj = JSONObject(s)
                        if (resObj.getInt("status_code") == 200) {
                            val resObject = resObj.getJSONObject("response")
                            val msg = resObject.getString("response")
                            Pref_msg_global = resObject.getString("response")
                            val sharedpreferences =
                                getSharedPreferences(AppConstants.appSharedPref, MODE_PRIVATE)
                            val editor = sharedpreferences.edit()
                            editor.putString("DOCTOR_SESSION_ID", msg)
                            editor.apply()
                            Log.d("login_editor", msg)
                        } else {
                            errorHandler(this@LoginActivity, s)
                        }
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                }
        }

    private fun checkDataEnteredForgotPassword() {
        if (isEmpty(forgotPasswordPhoneText)) {
            val signInUserText = resources.getString(R.string.enter_mobile_number)
            appUtilities!!.showFullyCustomToast(this, signInUserText)
        } else {
            showCustomProgressAlertDialog(
                resources.getString(R.string.otp_request),
                resources.getString(R.string.requesting_server)
            )

            val url = ApiUrls.forgotPassOTP
            val jsonValue = JSONObject()
            jsonValue.put("phone", forgotPasswordPhoneText.text.toString())

            commonViewModel.commonViewModelCall(url, jsonValue, Method.POST).observe(
                this@LoginActivity
            ) { result ->
                try {
                    //Process os success response
                    dialog.dismiss()
                    val response = JSONObject(result)
                    if (response.getInt("status_code") == 200) {
                        signInLayout.visibility = View1.GONE
                        signUpLayout.visibility = View1.GONE
                        forgotPasswordLayout.visibility = View1.GONE
                        otpUpdateLayout.visibility = View1.VISIBLE
                    } else {
                        dialog.dismiss()
                        errorHandler(this@LoginActivity, result)
                    }
                } catch (e: Exception) {
                    dialog.dismiss()
                    e.printStackTrace()
                }
            }
        }
    }

    private fun checkDataEnteredUpdatePassword() {
        if (isEmpty(otpText)) {
            val signInUserText = resources.getString(R.string.enter_OTP_number)
            appUtilities!!.showFullyCustomToast(this, signInUserText)
        } else if (isEmpty(otpPasswordText)) {
            val signInUserText = resources.getString(R.string.enter_password)
            appUtilities!!.showFullyCustomToast(this, signInUserText)
        } else if (isEmpty(otpConfirmPasswordText)) {
            val signInUserText = resources.getString(R.string.re_enter_password)
            appUtilities!!.showFullyCustomToast(this, signInUserText)
        } else {
            if (otpPasswordText.text.toString()
                    .equals(otpConfirmPasswordText.text.toString(), ignoreCase = true)
            ) {
                showCustomProgressAlertDialog(
                    resources.getString(R.string.updating_password),
                    resources.getString(R.string.sending_update_request)
                )
                val url = ApiUrls.forgotPassUpdate
                // Post params to be sent to the server

                val jsonRes = JSONObject()
                jsonRes.put("otp", otpText.text.toString())
                jsonRes.put("phone", forgotPasswordPhoneText.text.toString())
                jsonRes.put("password", otpPasswordText.text.toString())
                jsonRes.put("cpassword", otpConfirmPasswordText.text.toString())


                commonViewModel.commonViewModelCall(url, jsonRes, Method.POST).observe(
                    this@LoginActivity
                ) { result ->
                    try {
                        //Process os success response
                        val json: String
                        val responseObj = JSONObject(result)
                        if (responseObj.getInt("status_code") == 200) {
                            val response = responseObj.optJSONObject("response")
                            val obj = JSONObject(response!!.toString())
                            json = obj.getString("response")
                            displayMessage(json)
                            otpText.setText("")
                            forgotPasswordPhoneText.setText("")
                            otpPasswordText.setText("")
                            otpConfirmPasswordText.setText("")
                            signInLayout.visibility = View1.VISIBLE
                            signUpLayout.visibility = View1.GONE
                            forgotPasswordLayout.visibility = View1.GONE
                            otpUpdateLayout.visibility = View1.GONE
                            dialog.dismiss()
                        } else {
                            dialog.dismiss()
                            errorHandler(this@LoginActivity, result)
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        dialog.dismiss()
                    }
                }
            } else {
                Toast.makeText(application, "Password are not match", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun isEmail(text: EditText): Boolean {
        val email: CharSequence = text.text.toString()
        return !TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    fun isEmpty(text: EditText?): Boolean {
        val str: CharSequence = text!!.text.toString()
        return TextUtils.isEmpty(str)
    }

    private fun getUserDetails(userDetailsRes: JSONObject) {
        try {
            val rootObj = userDetailsRes.getJSONObject("userData")
            ApiUrls.doctorId = rootObj.getInt("id")
            // int doctorId = rootObj.getInt("id");
            val name = rootObj.getString("fname")
            val email = rootObj.getString("email")
            val phno = rootObj.getString("phone")
            val lastLoggedIn = rootObj.getString("last_loggedin")
            ApiUrls.isDoctorOnly = rootObj.getInt("is_doctor_only")
            val editor = appPreference.edit()
            editor.putInt(ApiUrls.docId, rootObj.getInt("id"))
            editor.putString(ApiUrls.lastLoginDate, lastLoggedIn)
            editor.apply()
            var dob = rootObj.getString("dob")
            dob =
                if (!dob.equals("null", ignoreCase = true) && !dob.equals(
                        "",
                        ignoreCase = true
                    )
                ) {
                    rootObj.getString("dob")
                } else {
                    ""
                }
            val gender = rootObj.getInt("gender")
            val bloodType = rootObj.getString("blood_group")
            val userManager = AppUserManager(
                ApiUrls.doctorId,
                name,
                phno,
                gender,
                email,
                bloodType,
                dob,
                ApiUrls.loginToken,
                ApiUrls.isDoctorOnly
            )
            appDatabaseManager!!.updatePatient(userManager)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun trimMessage(json: String?, key: String?): String? {
        val trimmedString: String = try {
            val obj = JSONObject(json!!)
            obj.getString(key!!)
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

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        super.getOnBackPressedDispatcher().onBackPressed()
        if (signUpLayout.visibility == View1.VISIBLE) {
            signUpLayout.visibility = View1.GONE
            signInLayout.visibility = View1.VISIBLE
            forgotPasswordLayout.visibility = View1.GONE
            otpUpdateLayout.visibility = View1.GONE
        } else if (forgotPasswordLayout.visibility == View1.VISIBLE) {
            signUpLayout.visibility = View1.GONE
            signInLayout.visibility = View1.VISIBLE
            forgotPasswordLayout.visibility = View1.GONE
            otpUpdateLayout.visibility = View1.GONE
        } else if (otpUpdateLayout.visibility == View1.VISIBLE) {
            signUpLayout.visibility = View1.GONE
            signInLayout.visibility = View1.VISIBLE
            forgotPasswordLayout.visibility = View1.GONE
            otpUpdateLayout.visibility = View1.GONE
        } else if (signInLayout.visibility == View1.VISIBLE) {
            finish()
        }
    }

    private fun registerUser() {
        if (isEmpty(signUpUserText) && isEmpty(signUpEmailText) && (isEmpty(signUpMobileText) || signUpMobileText.text.length < 10) && isEmpty(
                signUpQualificationText
            ) && isEmpty(signUpSpecializationText) && isEmpty(signUpFocusAreasText)
        ) {
            val signInUserTextFullName = resources.getString(R.string.enter_full_Name)
            signUpUserText.error = signInUserTextFullName
            val signInUserTextEmail = resources.getString(R.string.enter_email_address)
            signUpEmailText.error = signInUserTextEmail
            val signInUserTextMobile =
                resources.getString(R.string.enter_ten_digit_phone_number)
            signUpMobileText.error = signInUserTextMobile
            val signInUserTextQualification = resources.getString(R.string.enter_qualification)
            signUpQualificationText.error = signInUserTextQualification
            val signInUserTextSpecialization =
                resources.getString(R.string.enter_specialization)
            signUpSpecializationText.error = signInUserTextSpecialization
            val signInUserTextFocusArea = resources.getString(R.string.enter_focus_area)
            signUpFocusAreasText.error = signInUserTextFocusArea
            Toast.makeText(
                this@LoginActivity,
                "Please enter all required details",
                Toast.LENGTH_SHORT
            ).show()
        } else if (isEmpty(signUpUserText)) {
            val signInUserTextFullName = resources.getString(R.string.enter_full_Name)
            signUpUserText.error = signInUserTextFullName
        } else if (isEmpty(signUpEmailText)) {
            val signInUserTextEmail = resources.getString(R.string.enter_email_address)
            signUpEmailText.error = signInUserTextEmail
        } else if (isEmpty(signUpMobileText) || signUpMobileText.text.length < 10) {
            val signInUserTextMobile =
                resources.getString(R.string.enter_ten_digit_phone_number)
            signUpMobileText.error = signInUserTextMobile
        } else if (isEmpty(signUpQualificationText)) {
            val signInUserTextQualification = resources.getString(R.string.enter_qualification)
            signUpQualificationText.error = signInUserTextQualification
        } else if (isEmpty(signUpSpecializationText)) {
            val signInUserTextSpecialization =
                resources.getString(R.string.enter_specialization)
            signUpSpecializationText.error = signInUserTextSpecialization
        } else if (isEmpty(signUpFocusAreasText)) {
            val signInUserTextFocusArea = resources.getString(R.string.enter_focus_area)
            signUpFocusAreasText.error = signInUserTextFocusArea
        } else {
            showCustomProgressAlertDialog(resources.getString(R.string.please_wait),resources.getString(R.string.please_wait_loading_message))
            val url = ApiUrls.signUpUser
            try {
                jsonValue = JSONObject()
                jsonValue.put("full_name", signUpUserText.text.toString())
                jsonValue.put("email", signUpEmailText.text.toString())
                jsonValue.put("phone", signUpMobileText.text.toString())
                jsonValue.put("qualifications", signUpQualificationText.text.toString())
                jsonValue.put("specialization", signUpSpecializationText.text.toString())
                jsonValue.put("focus_area", signUpFocusAreasText.text.toString())
            } catch (e: Exception) {
                e.printStackTrace()
            }

            commonViewModel.commonViewModelCall(url,jsonValue,Method.POST).observe(this@LoginActivity
            ) { result->
                try {
                    //Process os success response
                   val response=JSONObject(result)
                    if(response.getInt("status_code")==200) {
                        displayMessage("User Registered Successfully")
                        dialog.dismiss()
                        signUpUserText.setText("")
                        signUpEmailText.setText("")
                        signUpMobileText.setText("")
                        signUpQualificationText.setText("")
                        signUpSpecializationText.setText("")
                        signUpFocusAreasText.setText("")
                        signInLayout.visibility = View1.VISIBLE
                        signUpLayout.visibility = View1.GONE
                        forgotPasswordLayout.visibility = View1.GONE
                        otpUpdateLayout.visibility = View1.GONE
                    }else{
                        dialog.dismiss()
                        errorHandler(this@LoginActivity, result)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    dialog.dismiss()
                }
            }
        }
    }

    private fun sendPlayerId(patientID: Int) {
        val url = ApiUrls.insertNotiPlayerId
        val notifyObj = JSONObject()
        try {
            notifyObj.put("userId", patientID)
            notifyObj.put("playerId", notiPlayerId)
            notifyObj.put("pushToken", notiPushNoti)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        commonViewModel.commonViewModelCall(url,notifyObj,Method.POST).observe(this@LoginActivity
        ) { result->
            val response=JSONObject(result)
        }
    }

    companion object {
        @JvmField
        var Pref_msg_global: String? = null
    }

    fun showCustomProgressAlertDialog(
        title: String?,
        progressVal: String?

    ) {
        val builder = AlertDialog.Builder(this@LoginActivity)
        builder.setTitle(title)
        builder.setCancelable(false)
        val customLayout =
            LayoutInflater.from(this@LoginActivity)
                .inflate(R.layout.custom_progress_bar, null)
        builder.setView(customLayout)
        dialog = builder.create()
        val tvBody = customLayout.findViewById<TextView>(R.id.tv_value)
        tvBody.text = progressVal
        dialog.show()
    }

}