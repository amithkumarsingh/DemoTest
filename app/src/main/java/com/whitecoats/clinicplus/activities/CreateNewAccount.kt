package com.whitecoats.clinicplus.activities

import android.app.Activity
import android.app.Dialog
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PorterDuff
import android.net.Uri
import android.os.Bundle
import android.os.CountDownTimer
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.tooltip.Tooltip
import com.whitecoats.clinicplus.*
import com.whitecoats.clinicplus.apis.ApiUrls
import com.whitecoats.clinicplus.models.CreateAccMerchantModel
import com.whitecoats.clinicplus.utils.ErrorHandlerClass.errorHandler
import com.whitecoats.clinicplus.utils.RestUtils
import com.whitecoats.clinicplus.viewmodels.CreateMerchantViewModel
import com.whitecoats.clinicplus.viewmodels.CreateNewAccountViewModel
import org.json.JSONException
import org.json.JSONObject
import java.util.*
import java.util.concurrent.TimeUnit

class CreateNewAccount : AppCompatActivity(), AdapterView.OnItemSelectedListener {
    private var mContext: Context? = null
    var timerCountDown = TimerCountDown()
    private var globalClass: MyClinicGlobalClass? = null
    var btn_createMerchant: Button? = null
    var btn_verify: Button? = null
    var btn_retry_penny: Button? = null
    var btn_pennyVerification: Button? = null
    var btn_createNewAccount: Button? = null
    var btn_createExistingAccount: TextView? = null
    var otp_penny_heading: TextView? = null
    var tv_penny_countDown: TextView? = null
    var tv_pennyDialog_heading: TextView? = null
    var et_pennyAmount: EditText? = null
    var et_otp_penny: EditText? = null
    var et_merchantName: EditText? = null
    var et_merchantEmail: EditText? = null
    var et_merchantPhone: EditText? = null
    var et_monthlyAmount: EditText? = null
    var et_bankAccountNumber: EditText? = null
    var et_bankIfsc: EditText? = null
    var et_accountHolderName: EditText? = null
    var et_address: EditText? = null
    var et_pinCode: EditText? = null
    var et_reEnterAccNo: EditText? = null
    var et_busiRegiName: EditText? = null
    var et_panNumber: EditText? = null
    var et_panName: EditText? = null
    var spinner_entityType: Spinner? = null
    var merName: String? = null
    var merEmail: String? = null
    var merPhone: String? = null
    var tv_step1: TextView? = null
    var tv_step2: TextView? = null
    var tv_accountCreatedStatus: TextView? = null
    var tv_panStatus: TextView? = null
    var tv_bankVerify_Status: TextView? = null
    var tv_merchantName_status: TextView? = null
    var tv_merchant_Name: TextView? = null
    var tv_merchant_email: TextView? = null
    var tv_panCardName: TextView? = null
    var tv_panCardNumber: TextView? = null
    var tv_merchant_phone: TextView? = null
    var tv_merchant_AccNo: TextView? = null
    var tv_merchant_ifscCode: TextView? = null
    var tv_merchant_HolderName: TextView? = null
    var tv_merchant_address: TextView? = null
    var tv_merchant_pinCode: TextView? = null
    var tv_merchant_registeredName: TextView? = null
    var tv_monthly_volume: TextView? = null
    var tv_merchant_entityType: TextView? = null
    var createNewAccountViewModel: CreateNewAccountViewModel? = null
    var createMerchantViewModel: CreateMerchantViewModel? = null
    private val param = JSONObject()
    private var progressDialog: ProgressDialog? = null

    //ProgressBar progressBar_loader;
    var merchantNameError: TextView? = null
    var merchantEmailError: TextView? = null
    var merchantPhoneError: TextView? = null
    var bankAccNoError: TextView? = null
    var bankIfscCodeError: TextView? = null
    var bankAccNameError: TextView? = null
    var panNameError: TextView? = null
    var panNoError: TextView? = null
    var addressError: TextView? = null
    var pinCodeError: TextView? = null
    var monthlyAmountError: TextView? = null
    var ReenterAccError: TextView? = null
    var busiRegisNameError: TextView? = null
    private var arrayList_category: ArrayList<String>? = null
    private var arrayList_subCategory: ArrayList<String>? = null
    private var arrayList_entityType: ArrayList<String?>? = null
    var entityType_arrayAdapter: ArrayAdapter<*>? = null
    var entityType_position = 0
    var category_string: String? = null
    var entityType_string: String? = null
    var toolbar: Toolbar? = null
    var createNewAccStep1_layout: RelativeLayout? = null
    var createNewAccStep2_layout: RelativeLayout? = null
    var MerchantSetUp_layout: RelativeLayout? = null
    var updateMerchant_layout: RelativeLayout? = null
    var createAccMerchantModel = CreateAccMerchantModel()

    var docId = 0
    var imageView_help: ImageView? = null
    var payU_status_help_imageView: ImageView? = null
    var imageView_refresh: ImageButton? = null
    var tv_OTPHeadingBank: TextView? = null
    var tv_OTPHeadingPan: TextView? = null
    var appDatabaseManager: AppDatabaseManager? = null
    var btn_save: Button? = null
    var btn_change_bank_details_failed: Button? = null
    private var appPreference: SharedPreferences? = null
    private var activity: Activity? = null
    var appDoctorManagers: List<AppDoctorManager> = ArrayList()
    var temp = ""
    var tv_bank_inProgress1: TextView? = null
    var merchantPersonalDetails: RelativeLayout? = null
    var layout_accountStatus: RelativeLayout? = null
    var layout_merchantDetails: RelativeLayout? = null
    var layout_ribbon: LinearLayout? = null
    var layout_btnDetails: LinearLayout? = null
    var bankDetails_cardView: CardView? = null
    var cardView_verify_heading: CardView? = null
    var cardView_bank_inProgress: CardView? = null
    var cardView_bank_failed: CardView? = null
    var cardView_exhausted_heading: CardView? = null
    var verify_success_card: CardView? = null
    var verify_successOld_card: CardView? = null
    var createMerDetailsStep1Flag = false
    var createMerDetailsStep2Flag = false
    var getMerDetailsFlag = false
    var tv_bankUpdateDetails: TextView? = null
    var tv_reSendBankOTP: TextView? = null
    var timer_text: TextView? = null
    var tv_OTPExpired: TextView? = null
    var tv_expiresOTP: TextView? = null
    var tv_reSendOTP: TextView? = null
    var timer_pan_text: TextView? = null
    var panTimer_txt: TextView? = null
    var tv_panOTPExpired: TextView? = null
    var tv_panExpiresOTP: TextView? = null
    var tv_panReSendOTP: TextView? = null
    var tv_pandetailschange: TextView? = null
    var layout_bankOTP: RelativeLayout? = null
    var layout_panOTP: RelativeLayout? = null
    var layout_verify_heading: RelativeLayout? = null
    var layout_failedDividerEnd: RelativeLayout? = null
    var layout_vSuccess_heading: RelativeLayout? = null
    var layout_oldSuccess_heading: RelativeLayout? = null
    var layout_Success_divider: RelativeLayout? = null
    var layout_heading_bank_inProgress: RelativeLayout? = null
    var layout_bank_failed_heading: RelativeLayout? = null
    var layout_exhausted_heading: RelativeLayout? = null
    var layout_bank_inProgress_dividerEnd: RelativeLayout? = null
    var layout_bankFailed_DividerEnd: RelativeLayout? = null
    var layout_divider_exhausted: RelativeLayout? = null
    var tv_error: TextView? = null
    var btn_updateBankDetails: TextView? = null
    var tv_verifyNow: TextView? = null
    var tv_changeBankDetails: TextView? = null
    var tv_uploadKYC: TextView? = null
    var text_exhausted_clickHere: TextView? = null
    var tv_success_old_clickHere: TextView? = null
    var et_pan_otp: EditText? = null
    var et_bank_otp: EditText? = null
    var et_updateBankAccName: EditText? = null
    var et_updateBankAccNo: EditText? = null
    var et_updateReenterBankAcc: EditText? = null
    var et_updateIfscCode: EditText? = null
    var et_pan_practiceName: EditText? = null
    var et_pan_panName: EditText? = null
    var et_pan_panNo: EditText? = null
    var et_pan_address: EditText? = null
    var et_pan_pinCode: EditText? = null
    var et_pan_monthlyAmount: EditText? = null
    var spinner_pan_entityType: Spinner? = null
    var layout_verifyNowStatus: RelativeLayout? = null
    var step2CreateMerchant = false
    var retry_bank_flag = false
    var retry_pan_flag = false
    var retry_penny_flag = false
    var createNewAccFlag = false
    var updateBankDetailsFlag1 = false
    var merchantDetailsBankUpdateFlag = false
    var isPayUSetupPage = false
    var pennyTimerFlag = false
    private var mTimerRunning = false
    private var mCountDownTimer: CountDownTimer? = null
    var img_Name_Info: ImageButton? = null
    var img_entity_info: ImageButton? = null
    var img_panName_info: ImageButton? = null
    var img_PanNo_info: ImageButton? = null
    var img_address_info: ImageButton? = null
    var img_holderName_info: ImageButton? = null
    var info_bankMerChangeName: ImageButton? = null
    var info_panMerchant_name: ImageButton? = null
    var info_pan_merchantPanName: ImageButton? = null
    var info_pan_merchantPanNo: ImageButton? = null
    var info_pan_merchant_entity: ImageButton? = null
    var info_pan_merchantAddress: ImageButton? = null
    var customDialogOpen = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_new_account)
        globalClass = applicationContext as MyClinicGlobalClass
        toolbar = findViewById(R.id.createAccountToolbar)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        val backArrow = ResourcesCompat.getDrawable(resources, R.drawable.ic_arrow_back, null)
        backArrow?.setColorFilter(
            ContextCompat.getColor(applicationContext, R.color.colorWhite),
            PorterDuff.Mode.SRC_ATOP
        )
        supportActionBar!!.setHomeAsUpIndicator(backArrow)
        mContext = applicationContext
        arrayList_category = ArrayList()
        arrayList_subCategory = ArrayList()
        arrayList_entityType = ArrayList()
        activity = activity
        appPreference = getSharedPreferences(ApiUrls.appSharedPref, MODE_PRIVATE)
        appDatabaseManager = AppDatabaseManager(this)
        appDoctorManagers = appDatabaseManager!!.doctorData
        Log.d("Lenght", appDoctorManagers.size.toString() + "")
        initialize()
    }

    private fun initialize() {
        tv_error = findViewById(R.id.error_txt)
        img_Name_Info = findViewById(R.id.prac_info)
        img_entity_info = findViewById(R.id.img_entity_info)
        img_PanNo_info = findViewById(R.id.img_panNumber_info)
        img_panName_info = findViewById(R.id.img_panCardName_info)
        img_address_info = findViewById(R.id.img_address_info)
        img_holderName_info = findViewById(R.id.img_bankAccName_info)
        info_panMerchant_name = findViewById(R.id.panMerName_info)
        info_pan_merchantPanNo = findViewById(R.id.panNo_info)
        info_pan_merchantPanName = findViewById(R.id.panName_info)
        info_pan_merchant_entity = findViewById(R.id.pan_entity_info)
        info_pan_merchantAddress = findViewById(R.id.pan_address_info)
        info_bankMerChangeName = findViewById(R.id.bankMerChangeName_info)
        btn_createNewAccount = findViewById(R.id.createNewAccount)
        btn_createExistingAccount = findViewById(R.id.text_existing)
        bankDetails_cardView = findViewById(R.id.createMerchant_cardView)
        cardView_verify_heading = findViewById(R.id.verify_heading_cardView)
        cardView_bank_inProgress = findViewById(R.id.cardView_bankInProgress)
        verify_success_card = findViewById(R.id.verify_success_cardView)
        verify_successOld_card = findViewById(R.id.verify_successful_old_cardView)
        layout_failedDividerEnd = findViewById(R.id.layout_faileddividerend)
        layout_Success_divider = findViewById(R.id.layout_dividerend)
        layout_vSuccess_heading = findViewById(R.id.verifySuccess_heading)
        layout_oldSuccess_heading = findViewById(R.id.verify_success_old_heading)
        layout_verify_heading = findViewById(R.id.verify_failed_heading)
        layout_bank_failed_heading = findViewById(R.id.layout_heading_bank_failed)
        layout_heading_bank_inProgress = findViewById(R.id.layout_bankInProgress)
        layout_bank_inProgress_dividerEnd = findViewById(R.id.layout_bank_inProgress_DividerEnd)
        layout_exhausted_heading = findViewById(R.id.verify_exhausted_heading)
        layout_divider_exhausted = findViewById(R.id.layout_failed_Exhausted_DividerEnd)
        cardView_exhausted_heading = findViewById(R.id.verify_exhausted_cardView)
        cardView_bank_failed = findViewById(R.id.cardView_bank_failed)
        layout_bankFailed_DividerEnd = findViewById(R.id.layout_bankFailed_DividerEnd)
        layout_ribbon = findViewById(R.id.ribbon_section_verify)
        tv_bank_inProgress1 = findViewById(R.id.text_verify_inProgress2)
        tv_step1 = findViewById(R.id.heading_create_account)
        tv_step2 = findViewById(R.id.heading_create_step2)
        layout_btnDetails = findViewById(R.id.merchantDetails_btn)
        layout_accountStatus = findViewById(R.id.layout_status)
        layout_merchantDetails = findViewById(R.id.layout_merchantDetails)
        merchantPersonalDetails = findViewById(R.id.layout_bank_details)
        createNewAccStep2_layout = findViewById(R.id.layout_createMerchantSet2)
        createNewAccStep1_layout = findViewById(R.id.layout_createMerchant)
        MerchantSetUp_layout = findViewById(R.id.layout_MerchantSetUp)
        updateMerchant_layout = findViewById(R.id.layout_updateMerchant)
        //progressBar_loader = findViewById(R.id.progressBar_loader);
        tv_bankUpdateDetails = findViewById(R.id.tv_bankUpdateBankDetails)
        tv_bankUpdateDetails?.setPaintFlags(
            tv_bankUpdateDetails?.getPaintFlags() ?: Paint.UNDERLINE_TEXT_FLAG
        )
        et_merchantName = findViewById(R.id.merchant_name)
        et_merchantEmail = findViewById(R.id.et_merchantEmail)
        et_merchantPhone = findViewById(R.id.et_merchant_phone)
        et_bankAccountNumber = findViewById(R.id.et_bank_accNo)
        et_bankIfsc = findViewById(R.id.et_bankIfscCode)
        et_accountHolderName = findViewById(R.id.et_bankHolderName)
        et_address = findViewById(R.id.et_address)
        et_panName = findViewById(R.id.et_panName)
        et_panNumber = findViewById(R.id.et_panNumber)
        et_pinCode = findViewById(R.id.et_pinCode)
        et_busiRegiName = findViewById(R.id.et_busiRegiName)
        et_reEnterAccNo = findViewById(R.id.et_bank_ReAccNo)
        et_monthlyAmount = findViewById(R.id.et_monthly_amount)
        spinner_entityType = findViewById(R.id.business_type_spinner)
        btn_save = findViewById(R.id.btn_saveNext)
        btn_change_bank_details_failed = findViewById(R.id.btn_failed_bank_details)
        tv_reSendBankOTP = findViewById(R.id.tv_reSendOtp)
        tv_OTPHeadingBank = findViewById(R.id.bankOTP_statementText)
        tv_OTPHeadingPan = findViewById(R.id.panOTP_statementText)
        imageView_help = findViewById(R.id.helpToolBarButton)
        imageView_refresh = findViewById(R.id.refreshToolBarButton)
        //update merchant
        tv_accountCreatedStatus = findViewById(R.id.tv_account_created)
        tv_panStatus = findViewById(R.id.tv_panStatus_result)
        tv_pandetailschange = findViewById(R.id.tv_changePanDetails)
        tv_bankVerify_Status = findViewById(R.id.tv_bankVerifyStatus)
        tv_merchantName_status = findViewById(R.id.tv_merchantName)
        tv_merchant_Name = findViewById(R.id.tv_merchantName)
        tv_merchant_email = findViewById(R.id.merchant_email)
        tv_merchant_phone = findViewById(R.id.merchant_phone)
        tv_panCardName = findViewById(R.id.pan_name_text)
        tv_panCardNumber = findViewById(R.id.pan_No_text)
        tv_merchant_address = findViewById(R.id.merchant_address)
        tv_merchant_pinCode = findViewById(R.id.merchant_pinCode)
        tv_merchant_HolderName = findViewById(R.id.merchant_HolderName)
        tv_merchant_AccNo = findViewById(R.id.merchant_bankAccNo)
        tv_merchant_ifscCode = findViewById(R.id.merchant_ifscCode)
        tv_merchant_registeredName = findViewById(R.id.merchant_busiRegisteredName)
        tv_monthly_volume = findViewById(R.id.merchant_monthlyVol)
        tv_merchant_entityType =
            findViewById(R.id.merchant_busiEntityType)
        btn_createMerchant = findViewById(R.id.bt_createMerchant)
        btn_pennyVerification = findViewById(R.id.bt_penny_verification)
        merchantNameError = findViewById(R.id.personNameErrorText)
        merchantEmailError = findViewById(R.id.personEmailErrorText)
        merchantPhoneError = findViewById(R.id.personPhoneErrorText)
        bankAccNoError = findViewById(R.id.bankAccNoErrorText)
        bankIfscCodeError = findViewById(R.id.bankIfscErrorText)
        bankAccNameError = findViewById(R.id.accHolderNameErrorText)
        panNameError = findViewById(R.id.panNameErrorText)
        panNoError = findViewById(R.id.panNumberErrorText)
        addressError = findViewById(R.id.addressErrorText)
        pinCodeError = findViewById(R.id.pinCodeErrorText)
        busiRegisNameError = findViewById(R.id.regiNameErrorText)
        ReenterAccError = findViewById(R.id.bankReAccNoErrorText)
        monthlyAmountError = findViewById(R.id.monthlyErrorText)
        btn_updateBankDetails = findViewById(R.id.tv_merchantUpdateBankDetails)
        text_exhausted_clickHere = findViewById(R.id.text_verifyExhausted_clickHere)
        tv_success_old_clickHere = findViewById(R.id.text_verify_success_Old)
        layout_bankOTP = findViewById(R.id.layout_bankUpdateOTP)
        layout_panOTP = findViewById(R.id.layout_panUpdateOTP)
        tv_verifyNow = findViewById(R.id.tv_verify_now)
        tv_changeBankDetails = findViewById(R.id.tv_changeUpdateDetails)
        layout_verifyNowStatus = findViewById(R.id.layout_bankChange_status)
        tv_uploadKYC = findViewById(R.id.tv_uploadKYC)
        payU_status_help_imageView = findViewById(R.id.payU_status_icon_help)
        spinner_pan_entityType = findViewById(R.id.panBusiness_type_spinner)
        //bank count down textview

        //pan count down textview
        timer_pan_text = findViewById(R.id.text_view_panCountDown)
        img_Name_Info?.setOnClickListener {
            val tooltip = Tooltip.Builder(img_Name_Info!!, R.style.Tooltip2)
                .setText("Note: If your practice is not registered as a separate legal entity, please enter your own name. Else, please enter your legal entity's name. ")
                .setTextColor(ContextCompat.getColor(applicationContext, R.color.white))
                .setGravity(Gravity.BOTTOM)
                .setCornerRadius(6f)
                .setDismissOnClick(true)
                .setCancelable(true)
                .show()
        }
        img_entity_info?.setOnClickListener {
            val tooltip = Tooltip.Builder(img_entity_info!!, R.style.Tooltip2)
                .setText("Note: If your practice is not registered as a separate legal entity, please select ")
                .setTextColor(ContextCompat.getColor(applicationContext, R.color.white))
                .setGravity(Gravity.BOTTOM)
                .setCornerRadius(6f)
                .setDismissOnClick(true)
                .setCancelable(true)
                .show()
        }
        img_PanNo_info?.setOnClickListener {
            val tooltip = Tooltip.Builder(img_PanNo_info!!, R.style.Tooltip2)
                .setText("Note: If your practice is not registered as a separate legal entity, please enter your own PAN number. Else, please enter your legal entity's PAN number and not your own")
                .setTextColor(ContextCompat.getColor(applicationContext, R.color.white))
                .setGravity(Gravity.BOTTOM)
                .setCornerRadius(6f)
                .setDismissOnClick(true)
                .setCancelable(true)
                .show()
        }
        img_panName_info?.setOnClickListener {
            val tooltip = Tooltip.Builder(img_panName_info!!, R.style.Tooltip2)
                .setText("PAN holder name will be auto verified by PayU against the Registered Name you entered above. Please ensure the two match to avoid further verification steps. ")
                .setTextColor(ContextCompat.getColor(applicationContext, R.color.white))
                .setGravity(Gravity.BOTTOM)
                .setCornerRadius(6f)
                .setDismissOnClick(true)
                .setCancelable(true)
                .show()
        }
        img_address_info?.setOnClickListener {
            val tooltip = Tooltip.Builder(img_address_info!!, R.style.Tooltip2)
                .setText("Please provide the registered address for your practice.")
                .setTextColor(ContextCompat.getColor(applicationContext, R.color.white))
                .setGravity(Gravity.BOTTOM)
                .setCornerRadius(6f)
                .setDismissOnClick(true)
                .setCancelable(true)
                .show()
        }
        img_holderName_info?.setOnClickListener {
            val tooltip = Tooltip.Builder(img_holderName_info!!, R.style.Tooltip2)
                .setText("Note: This is where your practice's online payments will be credited. To allow auto-verification, please ensure this matches the name entered in, If the names do not match, you will receive a small amount in your given bank account within 30 minutes. Please re-visit this section and enter that amount to complete your bank account verification.")
                .setTextColor(ContextCompat.getColor(applicationContext, R.color.white))
                .setGravity(Gravity.BOTTOM)
                .setCornerRadius(6f)
                .setDismissOnClick(true)
                .setCancelable(true)
                .show()
        }
        info_panMerchant_name?.setOnClickListener {
            val tooltip = Tooltip.Builder(info_panMerchant_name!!, R.style.Tooltip2)
                .setText("Note: This is where your practice's online payments will be credited. To allow auto-verification, please ensure this matches the name entered in, If the names do not match, you will receive a small amount in your given bank account within 30 minutes. Please re-visit this section and enter that amount to complete your bank account verification.")
                .setTextColor(ContextCompat.getColor(applicationContext, R.color.white))
                .setGravity(Gravity.BOTTOM)
                .setCornerRadius(6f)
                .setDismissOnClick(true)
                .setCancelable(true)
                .show()
        }
        info_pan_merchant_entity?.setOnClickListener {
            val tooltip = Tooltip.Builder(info_pan_merchant_entity!!, R.style.Tooltip2)
                .setText("Note: If your practice is not registered as a separate legal entity, please select ")
                .setTextColor(ContextCompat.getColor(applicationContext, R.color.white))
                .setGravity(Gravity.BOTTOM)
                .setCornerRadius(6f)
                .setDismissOnClick(true)
                .setCancelable(true)
                .show()
        }
        info_pan_merchantPanNo?.setOnClickListener {
            val tooltip = Tooltip.Builder(info_pan_merchantPanNo!!, R.style.Tooltip2)
                .setText("Note: If your practice is not registered as a separate legal entity, please enter your own PAN number. Else, please enter your legal entity's PAN number and not your own")
                .setTextColor(ContextCompat.getColor(applicationContext, R.color.white))
                .setGravity(Gravity.BOTTOM)
                .setCornerRadius(6f)
                .setDismissOnClick(true)
                .setCancelable(true)
                .show()
        }
        info_pan_merchantPanName?.setOnClickListener {
            val tooltip = Tooltip.Builder(info_pan_merchantPanName!!, R.style.Tooltip2)
                .setText("PAN holder name will be auto verified by PayU against the Registered Name you entered above. Please ensure the two match to avoid further verification steps. ")
                .setTextColor(ContextCompat.getColor(applicationContext, R.color.white))
                .setGravity(Gravity.BOTTOM)
                .setCornerRadius(6f)
                .setDismissOnClick(true)
                .setCancelable(true)
                .show()
        }
        info_pan_merchantAddress?.setOnClickListener {
            val tooltip = Tooltip.Builder(info_pan_merchantAddress!!, R.style.Tooltip2)
                .setText("Please provide the registered address for your practice.")
                .setTextColor(ContextCompat.getColor(applicationContext, R.color.white))
                .setGravity(Gravity.BOTTOM)
                .setCornerRadius(6f)
                .setDismissOnClick(true)
                .setCancelable(true)
                .show()
        }
        info_bankMerChangeName?.setOnClickListener {
            val tooltip = Tooltip.Builder(info_bankMerChangeName!!, R.style.Tooltip2)
                .setText("Note: This is where your practice's online payments will be credited. To allow auto-verification, please ensure this matches the name entered in, If the names do not match, you will receive a small amount in your given bank account within 30 minutes. Please re-visit this section and enter that amount to complete your bank account verification.")
                .setTextColor(ContextCompat.getColor(applicationContext, R.color.white))
                .setGravity(Gravity.BOTTOM)
                .setCornerRadius(6f)
                .setDismissOnClick(true)
                .setCancelable(true)
                .show()
        }
        spinner_entityType?.onItemSelectedListener = this
        entityType_arrayAdapter =
            arrayList_entityType?.let { ArrayAdapter<Any?>(this, android.R.layout.simple_spinner_item, it.toList()) }
        entityType_arrayAdapter?.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        progressDialog = ProgressDialog(this)
        createNewAccountViewModel = ViewModelProvider(this)[CreateNewAccountViewModel::class.java]
        createNewAccountViewModel!!.init()
        createMerchantViewModel = ViewModelProvider(this)[CreateMerchantViewModel::class.java]
        createMerchantViewModel!!.init()


        //get merchant details
        paymentMerchantApi
        imageView_refresh?.setOnClickListener { paymentMerchantApi }
        //get help
        imageView_help?.setOnClickListener { customQueriesDialog() }

        //call merchant support help
        payU_status_help_imageView?.setOnClickListener {
            if (createAccMerchantModel.bank_verification_status.equals(
                    "success",
                    ignoreCase = true
                )
            ) {
                customMerchantDialogSupport()
            } else {
                customQueriesDialog()
            }
        }
        //verify Now
        tv_verifyNow?.setOnClickListener { customPennyDialog() }
        btn_change_bank_details_failed?.setOnClickListener {
            if (globalClass!!.isOnline) {
                getMerDetailsFlag = false
                updateBankDetailsFlag1 = true
                createMerDetailsStep1Flag = false
                updateMerchant_layout?.visibility = View.GONE
                UpdateBankDetails()
            } else {
                globalClass!!.noInternetConnection.showDialog(this@CreateNewAccount)
            }
        }
        //change bank details
        tv_changeBankDetails?.setOnClickListener {
            if (globalClass!!.isOnline) {
                getMerDetailsFlag = false
                updateBankDetailsFlag1 = true
                createMerDetailsStep1Flag = false
                updateMerchant_layout?.visibility = View.GONE
                UpdateBankDetails()
            } else {
                globalClass!!.noInternetConnection.showDialog(this@CreateNewAccount)
            }
        }
        //create merchant step2 bank update details
        btn_updateBankDetails?.setOnClickListener {
            if (globalClass!!.isOnline) {
                merchantDetailsBankUpdateFlag = true
                createMerDetailsStep2Flag = false
                layout_merchantDetails?.visibility = View.GONE
                UpdateBankDetails()
            } else {
                globalClass!!.noInternetConnection.showDialog(this@CreateNewAccount)
            }
        }
        //update pan details
        tv_pandetailschange?.setOnClickListener {
            if (globalClass!!.isOnline) {
                updateMerchant_layout?.visibility = View.GONE
                layout_panOTP?.visibility = View.VISIBLE
                UpdatePanDetails()
            } else {
                globalClass!!.noInternetConnection.showDialog(this@CreateNewAccount)
            }
        }

        //update bank details
        tv_bankUpdateDetails?.setOnClickListener {
            if (globalClass!!.isOnline) {
                getMerDetailsFlag = false
                updateBankDetailsFlag1 = true
                createMerDetailsStep1Flag = false
                updateMerchant_layout?.visibility = View.GONE
                UpdateBankDetails()
            } else {
                globalClass!!.noInternetConnection.showDialog(this@CreateNewAccount)
            }
        }
        layout_btnDetails?.setOnClickListener {
            getMerDetailsFlag = true
            createMerDetailsStep2Flag = true
            createMerDetailsStep1Flag = false
            layout_accountStatus?.visibility = View.GONE
            layout_btnDetails?.visibility = View.GONE
            layout_merchantDetails?.visibility = View.VISIBLE
            toolbar!!.title = "Merchant Details"
            imageView_refresh?.visibility = View.GONE
        }
        btn_save?.setOnClickListener {
            if (validateStep1()) {
                if (globalClass!!.isOnline) {
                    et_busiRegiName?.setText(merName)
                    et_panName?.setText(merName)
                    et_accountHolderName?.setText(merName)
                    arrayList_entityType!!.clear()
                    getBusinessEntityFieldDetails("1")
                    step2CreateMerchant = true
                    createMerDetailsStep1Flag = false
                    tv_step1?.visibility = View.GONE
                    tv_step2?.visibility = View.VISIBLE
                    createNewAccStep1_layout?.visibility = View.GONE
                    createNewAccStep2_layout?.visibility = View.VISIBLE
                    bankDetails_cardView?.visibility = View.VISIBLE
                } else {
                    globalClass!!.noInternetConnection.showDialog(this@CreateNewAccount)
                }
            }
        }
        val s = resources.getString(R.string.verification_successful_old_statement)
        val spannable = SpannableString(s)
        val color = ContextCompat.getColor(applicationContext, R.color.colorAccent)
        spannable.setSpan(ForegroundColorSpan(color), 16, 26, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        tv_success_old_clickHere?.text = spannable

        //bank success old
        tv_success_old_clickHere?.setOnClickListener {
            if (globalClass!!.isOnline) {
                //support URL
                var browserIntent: Intent? = null
                try {
                    val login_URL = createAccMerchantModel.payULogin_URL
                    browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(login_URL))
                    startActivity(browserIntent)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            } else {
                globalClass!!.noInternetConnection.showDialog(this@CreateNewAccount)
            }
        }
        //attempts exhausted
        text_exhausted_clickHere?.setOnClickListener {
            if (globalClass!!.isOnline) {
                progressDialog!!.setMessage("Please wait..")
                progressDialog!!.show()
                createNewAccountViewModel!!.getVerifyAttemptsModel(this@CreateNewAccount, param)
                    .observe(this@CreateNewAccount, object : Observer<String?> {
                        override fun onChanged(value: String?) {
                            Log.d("Response", s)
                            progressDialog!!.dismiss()
                            try {
                                val res = JSONObject(s)
                                if (res.getInt("status_code") == 200) {
                                    Log.d(TAG, "onChanged: $s")
                                    verifyAttemptsExhausted
                                } else {
                                    errorHandler(this@CreateNewAccount, s)
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                    })
            } else {
                globalClass!!.noInternetConnection.showDialog(this@CreateNewAccount)
            }
        }
        btn_createMerchant?.setOnClickListener {
            if (globalClass!!.isOnline) {
                if (validateInput()) {
                    try {
                        isMerchantCreated = 1
                        //param.put("doctor_id", AppConfigClass.doctorId);
                        param.put("name", et_merchantName?.getText())
                        param.put("email", et_merchantEmail?.getText())
                        param.put("phone", et_merchantPhone?.getText())
                        param.put("account_no", et_bankAccountNumber?.getText())
                        param.put("ifsc_code", et_bankIfsc?.getText())
                        param.put("account_holder_name", et_accountHolderName?.getText())
                        param.put("addr_line1", et_address?.getText())
                        param.put("pin", et_pinCode?.getText())
                        param.put("business_category", "Healthcare")
                        param.put("business_sub_category", "Others")
                        param.put("registered_name", et_busiRegiName?.getText())
                        param.put("pan", et_panNumber?.getText())
                        param.put("business_entity_type", spinner_entityType?.getSelectedItem())
                        param.put("pancard_name", et_panName?.getText())
                        param.put("monthly_expected_volume", "")
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                    progressDialog!!.setMessage("Please wait..")
                    progressDialog!!.show()
                    createNewAccountViewModel!!.createNewAccount(this@CreateNewAccount, param)
                        .observe(this@CreateNewAccount) {
                            Log.d("Response", s)
                            progressDialog!!.dismiss()
                            try {
                                createNewAccFlag = true
                                val resObj = JSONObject(s)
                                if (resObj.getInt("status_code") != 200) {
                                    errorHandler(this@CreateNewAccount, s)
                                    MyClinicGlobalClass.logUserActionEvent(
                                        ApiUrls.doctorId, getString(
                                            R.string.PaymentCreatePayUAccount
                                        ), null
                                    )
                                    val resObject = resObj.getString("response")
                                    val obj = JSONObject(resObject)
                                    val Msg = obj.getString("message")

                                    if (customDialogOpen == 0) {
                                        customDialogOpen = 1
                                        customDialog(Msg)
                                    }

                                    //Toast.makeText(CreateNewAccount.this, resObj.getString("response"), Toast.LENGTH_SHORT).show();
                                } else {
                                    MyClinicGlobalClass.logUserActionEvent(
                                        ApiUrls.doctorId, getString(
                                            R.string.PaymentCreatePayUAccount
                                        ), null
                                    )
                                    createMerchantSuccessDialog()
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                }
                // showOtpDialog();
            } else {
                globalClass!!.noInternetConnection.showDialog(this@CreateNewAccount)
            }
        }
        btn_pennyVerification?.setOnClickListener { showPennyVerifyDialog() }
    }

    val pennyOtpDetails: Unit
        get() {
            createNewAccountViewModel!!.getOTPDetails(this@CreateNewAccount, param)
                .observe(this@CreateNewAccount) { value ->
                    if (value != null) {
                        Log.d("Response", value)
                    }
                    progressDialog!!.dismiss()
                    try {
                        val res = JSONObject(value)
                        if (res.getInt("status_code") == 200) {
                            Log.d(TAG, "onChanged: $value")
                            startPennyTimer()
                        } else {
                            if (value != null) {
                                errorHandler(this@CreateNewAccount, value)
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
        }

    //update pan Details
    private fun UpdatePanDetails() {
        PanDetails = true
        layout_panOTP!!.visibility = View.VISIBLE
        imageView_refresh!!.visibility = View.GONE
        panTimer_txt = findViewById(R.id.tv_panExpiry_timer)
        tv_panOTPExpired = findViewById(R.id.tv_panOTPExpiry)
        tv_panExpiresOTP = findViewById(R.id.tv_panotp_expires)
        tv_panReSendOTP = findViewById(R.id.tv_panReSendOtp)
        et_pan_otp = findViewById(R.id.merchantPan_otp)
        et_pan_practiceName = findViewById(R.id.merchant_practiceName)
        et_pan_panNo = findViewById(R.id.et_merchantPanNo)
        et_pan_panName = findViewById(R.id.et_panCard_name)
        et_pan_address = findViewById(R.id.et_pan_address_code)
        et_pan_pinCode = findViewById(R.id.et_pan_pinCode)
        et_pan_monthlyAmount = findViewById(R.id.et_pan_monthlyVol)
        val btn_panUpdateDetails = findViewById<Button>(R.id.btn_panUpdate)
        tv_OTPHeadingPan!!.text =
            resources.getString(R.string.panDetailsStatement) + " " + createAccMerchantModel.phone
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        val backArrow = ResourcesCompat.getDrawable(resources, R.drawable.ic_arrow_back, null)
        backArrow?.setColorFilter(
            ContextCompat.getColor(applicationContext, R.color.colorWhite),
            PorterDuff.Mode.SRC_ATOP
        )
        supportActionBar!!.setHomeAsUpIndicator(backArrow)
        toolbar!!.title = "Update Pan Details"
        getPanBusinessEntityFieldDetails("1")
        panDetailsOTP
        tv_panReSendOTP?.setOnClickListener {
            timerCountDown.resetTimer()
            panDetailsOTP
        }
        btn_panUpdateDetails.setOnClickListener {
            val jsonValue = JSONObject()
            try {
                jsonValue.put("otp", et_pan_otp?.text.toString())
                jsonValue.put("business_name", et_pan_practiceName?.text.toString())
                jsonValue.put("pancard_name", et_pan_panName?.text.toString())
                jsonValue.put("pancard_number", et_pan_panNo?.text.toString())
                jsonValue.put("business_entity", spinner_pan_entityType!!.selectedItem.toString())
                jsonValue.put("address_line", et_pan_address?.text.toString())
                jsonValue.put("pincode", et_pan_pinCode?.text.toString())
                jsonValue.put("monthly_expected_volume", et_pan_monthlyAmount?.text.toString())
                progressDialog!!.setMessage("Please wait..")
                progressDialog!!.show()
                createNewAccountViewModel!!.updatePanDetails(this@CreateNewAccount, jsonValue)
                    .observe(
                        this@CreateNewAccount
                    ) { value ->
                        if (globalClass!!.isOnline) {
                            try {
                                progressDialog!!.dismiss()
                                val resObj = JSONObject(value)
                                if (resObj.getInt("status_code") != 200) {
                                    if (value != null) {
                                        errorHandler(this@CreateNewAccount, value)
                                    }
                                    retry_pan_flag = true
                                    val resObject = resObj.getString("response")
                                    val obj = JSONObject(resObject)
                                    val panInterror = obj.getInt("error_type")
                                    if (panInterror == 1001) {
                                        val panMsg = obj.getString("message")
                                        customDialog(panMsg)
                                    } else if (panInterror == 1002) {
                                        val Msg = obj.getString("message")
                                        customDialog(Msg)

                                    }
                                } else {
                                    progressDialog!!.dismiss()
                                    paymentMerchantApi
                                    Toast.makeText(
                                        this@CreateNewAccount,
                                        "Updated Successfully",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            } catch (e: JSONException) {
                                e.printStackTrace()
                            }
                        } else {
                            globalClass!!.noInternetConnection.showDialog(this@CreateNewAccount)
                        }
                    }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    //update bank details
    private fun UpdateBankDetails() {
        imageView_refresh!!.visibility = View.GONE
        layout_bankOTP!!.visibility = View.VISIBLE
        val btn_update = findViewById<Button>(R.id.btn_update)
        tv_expiresOTP = findViewById(R.id.tv_expires_timer)
        tv_OTPExpired = findViewById(R.id.tv_OTPExpired)
        tv_reSendOTP = findViewById(R.id.tv_reSendOtp)
        et_bank_otp = findViewById(R.id.merchant_otp)
        et_updateBankAccName = findViewById(R.id.merchant_AccName)
        et_updateBankAccNo = findViewById(R.id.et_merchantAccNo)
        et_updateReenterBankAcc = findViewById(R.id.et_reEnterAcc_no)
        et_updateIfscCode = findViewById(R.id.et_bank_ifsc_code)
        tv_OTPHeadingBank!!.text =
            resources.getString(R.string.bankDetailsStatement) + " " + createAccMerchantModel.phone
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        val backArrow = ResourcesCompat.getDrawable(resources, R.drawable.ic_arrow_back, null)
        backArrow?.setColorFilter(
            ContextCompat.getColor(applicationContext, R.color.colorWhite),
            PorterDuff.Mode.SRC_ATOP
        )
        supportActionBar!!.setHomeAsUpIndicator(backArrow)
        toolbar!!.title = "Update Bank Details"
        et_bank_otp?.setText("")
        et_updateBankAccName?.setText("")
        et_updateBankAccNo?.setText("")
        et_updateReenterBankAcc?.setText("")
        et_updateIfscCode?.setText("")
        bankDetailsOTP
        tv_reSendOTP?.setOnClickListener {
            resetTimer()
            bankDetailsOTP
        }
        btn_update.setOnClickListener {
            //get bank details
            //progressBar_loader.setVisibility(View.VISIBLE);
            val jsonValue = JSONObject()
            try {
                jsonValue.put("otp", et_bank_otp?.text.toString())
                jsonValue.put("bank_account_number", et_updateBankAccNo?.text.toString())
                jsonValue.put("ifsc_code", et_updateReenterBankAcc?.text.toString())
                jsonValue.put("holder_name", et_updateBankAccName?.text.toString())
            } catch (e: Exception) {
                e.printStackTrace()
            }
            progressDialog!!.setMessage("Please wait..")
            progressDialog!!.show()
            createNewAccountViewModel!!.updateBankDetails(this@CreateNewAccount, jsonValue)
                .observe(this@CreateNewAccount) { value ->
                    if (globalClass!!.isOnline) {
                        try {
                            progressDialog!!.dismiss()
                            val resObj = JSONObject(value)
                            if (resObj.getInt("status_code") != 200) {
                                if (value != null) {
                                    errorHandler(this@CreateNewAccount, value)
                                }
                                retry_bank_flag = true
                                val resObject = resObj.getString("response")
                                customDialog(resObject)

                            } else {
                                progressDialog!!.dismiss()
                                Toast.makeText(
                                    this@CreateNewAccount,
                                    "Updated Successfully",
                                    Toast.LENGTH_SHORT
                                ).show()

                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    } else {
                        globalClass!!.noInternetConnection.showDialog(this@CreateNewAccount)
                    }
                }
        }
    }

    //pan otp api call
    val panDetailsOTP: Unit
        get() {
            createNewAccountViewModel!!.getPanOTPDetails(this@CreateNewAccount)
                .observe(this@CreateNewAccount) { value ->
                    try {
                        val res = JSONObject(value)
                        if (res.getInt("status_code") == 200) {
                            Log.d(TAG, "onChanged: $value")
                            startPanTimer()
                        } else {
                            if (value != null) {
                                errorHandler(this@CreateNewAccount, value)
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
        }

    //bank otp api call
    val bankDetailsOTP: Unit
        get() {
            createNewAccountViewModel!!.getBankOTPDetails(this@CreateNewAccount, param)
                .observe(this@CreateNewAccount) { value ->
                    try {
                        val `object` = JSONObject(value)
                        if (`object`.getInt("status_code") == 200) {
                            Log.d(TAG, "onChanged: $value")
                            startBankTimer()
                        } else {
                            if (value != null) {
                                errorHandler(this@CreateNewAccount, value)
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
        }

    val paymentMerchantApi: Unit
        get() {
            progressDialog!!.setMessage("Please wait..")
            progressDialog!!.show()
            createMerchantViewModel!!.createMerchantAccount(this, null)
                .observe(this) { value ->
                    try {
                        Log.d(TAG, "onChanged: $value")
                        val jsonObject = JSONObject(value)
                        if (jsonObject.getInt("status_code") == 200) {
                            MyClinicGlobalClass.logUserActionEvent(
                                ApiUrls.doctorId, getString(
                                    R.string.PaymentSetUpScreenImpression
                                ), null
                            )
                            progressDialog!!.dismiss()
                            val response = JSONObject(value).getJSONObject("response")
                                .getJSONObject("response")
                            val createFlag = response.getInt(RestUtils.TAG_CREATE_FLAG)
                            if (createFlag == 0) {
                                createMerDetailsStep1Flag = true
                                imageView_help!!.visibility = View.GONE
                                if (createAccMerchantModel.payu_status !== "live") {
                                    imageView_refresh!!.visibility = View.VISIBLE
                                }
                                progressDialog!!.dismiss()
                                toolbar!!.title = "PayU Account Status"
                                layout_ribbon!!.visibility = View.GONE
                                layout_panOTP!!.visibility = View.GONE
                                updateMerchant_layout!!.visibility = View.VISIBLE
                                createNewAccStep1_layout!!.visibility = View.GONE
                                MerchantSetUp_layout!!.visibility = View.GONE
                                layout_accountStatus!!.visibility = View.VISIBLE
                                layout_btnDetails!!.visibility = View.VISIBLE
                                createNewAccStep1_layout!!.visibility = View.GONE
                                createNewAccStep2_layout!!.visibility = View.GONE
                                val payUMerDetailsJsonObject =
                                    response.getJSONObject(RestUtils.TAG_PAYUMERCHANTDETAILS)
                                createAccMerchantModel.id =
                                    payUMerDetailsJsonObject.optInt(RestUtils.TAG_ID)
                                createAccMerchantModel.interface_id =
                                    payUMerDetailsJsonObject.optInt(RestUtils.TAG_ID)
                                createAccMerchantModel.interface_id =
                                    payUMerDetailsJsonObject.optInt(RestUtils.TAG_INTERFACE_ID)
                                createAccMerchantModel.gateway_id =
                                    payUMerDetailsJsonObject.optString(RestUtils.TAG_GATEWAY_ID)
                                createAccMerchantModel.name =
                                    payUMerDetailsJsonObject.optString(RestUtils.TAG_NAME)
                                createAccMerchantModel.email =
                                    payUMerDetailsJsonObject.optString(RestUtils.TAG_EMAIL)
                                createAccMerchantModel.phone =
                                    payUMerDetailsJsonObject.optString(RestUtils.TAG_PHONE)
                                createAccMerchantModel.merchant_mid =
                                    payUMerDetailsJsonObject.optString(RestUtils.TAG_MERCHANT_MID)
                                createAccMerchantModel.merchant_type =
                                    payUMerDetailsJsonObject.optString(RestUtils.TAG_MERCHANT_TYPE)
                                createAccMerchantModel.payu_status =
                                    payUMerDetailsJsonObject.optString(RestUtils.TAG_PAYU_STATUS)
                                createAccMerchantModel.is_merchant_verified =
                                    payUMerDetailsJsonObject.optInt(RestUtils.TAG_IS_MERCHANT_VERIFIED)
                                createAccMerchantModel.added_from =
                                    payUMerDetailsJsonObject.optString(RestUtils.TAG_ADDED_FROM)
                                createAccMerchantModel.created_at =
                                    payUMerDetailsJsonObject.optString(RestUtils.TAG_CREATED_AT)
                                createAccMerchantModel.updated_at =
                                    payUMerDetailsJsonObject.optString(RestUtils.TAG_UPDATED_AT)
                                createAccMerchantModel.business_name =
                                    payUMerDetailsJsonObject.optString(RestUtils.TAG_BUSINESS_NAME)
                                createAccMerchantModel.pancard_name =
                                    payUMerDetailsJsonObject.optString(RestUtils.TAG_PANCARD_NAME)
                                createAccMerchantModel.pancard_number =
                                    payUMerDetailsJsonObject.optString(RestUtils.TAG_PANCARD_NUMBER)
                                createAccMerchantModel.website_url =
                                    payUMerDetailsJsonObject.optString(RestUtils.TAG_WEBSITE_URL)
                                createAccMerchantModel.android_url =
                                    payUMerDetailsJsonObject.optString(RestUtils.TAG_ANDROID_URL)
                                createAccMerchantModel.bank_account_number =
                                    payUMerDetailsJsonObject.optString(RestUtils.TAG_BANK_ACCOUNT_NUMBER)
                                createAccMerchantModel.ifsc_code =
                                    payUMerDetailsJsonObject.optString(RestUtils.TAG_IFSC_CODE)
                                createAccMerchantModel.holder_name =
                                    payUMerDetailsJsonObject.optString(RestUtils.TAG_HOLDER_NAME)
                                createAccMerchantModel.address_line =
                                    payUMerDetailsJsonObject.optString(RestUtils.TAG_ADDRESS_LINE)
                                createAccMerchantModel.pinCode =
                                    payUMerDetailsJsonObject.optInt(RestUtils.TAG_PINCODE)
                                createAccMerchantModel.business_entity =
                                    payUMerDetailsJsonObject.optString(RestUtils.TAG_BUSINESS_ENTITY)
                                createAccMerchantModel.business_category =
                                    payUMerDetailsJsonObject.optString(RestUtils.TAG_BUSINESS_CATEGORY)
                                createAccMerchantModel.business_sub_category =
                                    payUMerDetailsJsonObject.optString(RestUtils.TAG_BUSINESS_SUB_CATEGORY)
                                createAccMerchantModel.pan_verification_status =
                                    payUMerDetailsJsonObject.optString(RestUtils.TAG_PAN_VERIFICATION_STATUS)
                                createAccMerchantModel.website_approval_status =
                                    payUMerDetailsJsonObject.optString(RestUtils.TAG_WEBSITE_APPROVAL_STATUS)
                                createAccMerchantModel.settlement_status =
                                    payUMerDetailsJsonObject.optString(RestUtils.TAG_SETTLEMENT_STATUS)
                                createAccMerchantModel.isIs_service_agreement_accepted =
                                    payUMerDetailsJsonObject.optBoolean(RestUtils.TAG_IS_SERVICE_AGREEMENT_ACCEPTED)
                                createAccMerchantModel.isIs_authorisation_letter_required =
                                    payUMerDetailsJsonObject.optBoolean(RestUtils.TAG_IS_AUTHORISATION_LETTER_REQUIRED)
                                createAccMerchantModel.bank_verification_status =
                                    payUMerDetailsJsonObject.optString(RestUtils.TAG_BANK_VERIFICATION_STATUS)
                                createAccMerchantModel.penny_deposit_status =
                                    payUMerDetailsJsonObject.optString(RestUtils.TAG_PENNY_DEPOSIT_STATUS)
                                createAccMerchantModel.payULogin_URL =
                                    payUMerDetailsJsonObject.optString(RestUtils.TAG_PAYU_LOGIN_URL)
                                createAccMerchantModel.payU_callback_url =
                                    payUMerDetailsJsonObject.optString(RestUtils.TAG_SUPPORT_CALLBACK)
                                createAccMerchantModel.payU_support_help_url =
                                    payUMerDetailsJsonObject.optString(RestUtils.TAG_SUPPORT_HELP)
                                createAccMerchantModel.pan_verification_status1 =
                                    payUMerDetailsJsonObject.optString(RestUtils.TAG_PAN_VERIFICATION_STATUS1)
                                createAccMerchantModel.bank_verification_status1 =
                                    payUMerDetailsJsonObject.optString(RestUtils.TAG_BANK_VERIFICATION_STATUS1)
                                createAccMerchantModel.payu_status1 =
                                    payUMerDetailsJsonObject.optString(RestUtils.TAG_PAYU_STATUS1)
                                createAccMerchantModel.payu_section_id =
                                    payUMerDetailsJsonObject.optString(RestUtils.TAG_PAYU_SECTION_ID)
                                createAccMerchantModel.is_show_bank_update_button =
                                    payUMerDetailsJsonObject.optInt(RestUtils.TAG_PAYU_SHOW_BANK_UPDATE_BUTTON)
                                createAccMerchantModel.check_after_time =
                                    payUMerDetailsJsonObject.optString(RestUtils.TAG_PAYU_CHECK_AFTER_TIME)
                                if (createAccMerchantModel.is_show_bank_update_button == 1) {
                                    tv_changeBankDetails!!.visibility = View.VISIBLE
                                    btn_updateBankDetails!!.visibility = View.VISIBLE
                                }
                                tv_uploadKYC!!.setOnClickListener {
                                    var browserIntent: Intent? = null
                                    try {
                                        val login_URL = createAccMerchantModel.payULogin_URL
                                        browserIntent =
                                            Intent(Intent.ACTION_VIEW, Uri.parse(login_URL))
                                        startActivity(browserIntent)
                                    } catch (e: Exception) {
                                        e.printStackTrace()
                                    }
                                }

                                tv_accountCreatedStatus!!.text =
                                    createAccMerchantModel.payu_status1
                                tv_panStatus!!.text =
                                    createAccMerchantModel.pan_verification_status1
                                tv_bankVerify_Status!!.text =
                                    createAccMerchantModel.bank_verification_status1
                                if (createAccMerchantModel.pan_verification_status.equals(
                                        "Failed",
                                        ignoreCase = true
                                    ) &&
                                    createAccMerchantModel.bank_verification_status.equals(
                                        "Failed",
                                        ignoreCase = true
                                    )
                                ) {
                                    layout_verifyNowStatus!!.visibility = View.VISIBLE
                                    tv_bankUpdateDetails!!.visibility = View.GONE
                                } else {
                                    layout_verifyNowStatus!!.visibility = View.GONE
                                    tv_bankUpdateDetails!!.visibility = View.VISIBLE
                                }
                                if (createAccMerchantModel.pan_verification_status.equals(
                                        "Failed",
                                        ignoreCase = true
                                    )
                                ) {
                                    tv_panStatus!!.setTextColor(Color.RED)
                                }
                                if (createAccMerchantModel.bank_verification_status.equals(
                                        "Failed",
                                        ignoreCase = true
                                    )
                                ) {
                                    tv_bankVerify_Status!!.setTextColor(Color.RED)
                                }
                                if (createAccMerchantModel.pan_verification_status.equals(
                                        "Success",
                                        ignoreCase = true
                                    )
                                ) {
                                    tv_panStatus!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.colorPrimary))
                                    tv_pandetailschange!!.visibility = View.GONE
                                }
                                if (createAccMerchantModel.bank_verification_status.equals(
                                        "Success",
                                        ignoreCase = true
                                    )
                                ) {
                                    tv_bankVerify_Status!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.colorPrimary))
                                }
                                if (createAccMerchantModel.pan_verification_status.equals(
                                        "Verification Attempts Exhausted",
                                        ignoreCase = true
                                    )
                                ) {
                                    tv_panStatus!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.colorOrange))
                                }
                                if (createAccMerchantModel.bank_verification_status.equals(
                                        "Verification Attempts Exhausted",
                                        ignoreCase = true
                                    )
                                ) {
                                    tv_bankVerify_Status!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.colorOrange))
                                }
                                if (createAccMerchantModel.pan_verification_status.equals(
                                        "Pending",
                                        ignoreCase = true
                                    )
                                ) {
                                    tv_panStatus!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.colorYellow))
                                }
                                if (createAccMerchantModel.bank_verification_status.equals(
                                        "Pending",
                                        ignoreCase = true
                                    )
                                ) {
                                    tv_bankVerify_Status!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.colorYellow))
                                    layout_verifyNowStatus!!.visibility = View.VISIBLE
                                    tv_bankUpdateDetails!!.visibility = View.GONE
                                }
                                //bank/penny status
                                if (createAccMerchantModel.payu_section_id.equals(
                                        "verification_attempts_exhausted",
                                        ignoreCase = true
                                    )
                                ) {
                                    layout_exhausted_heading!!.visibility = View.VISIBLE
                                    cardView_exhausted_heading!!.visibility = View.VISIBLE
                                    layout_divider_exhausted!!.visibility = View.VISIBLE
                                    text_exhausted_clickHere!!.text =
                                        "Click here" + " to get a call back for your assistance"
                                } else if (createAccMerchantModel.payu_section_id.equals(
                                        "bank_success",
                                        ignoreCase = true
                                    )
                                ) {
                                    verify_success_card!!.visibility = View.VISIBLE
                                    layout_vSuccess_heading!!.visibility = View.VISIBLE
                                    verify_successOld_card!!.visibility = View.GONE
                                    layout_oldSuccess_heading!!.visibility = View.GONE
                                } else if (createAccMerchantModel.payu_section_id.equals(
                                        "penny_verification",
                                        ignoreCase = true
                                    )
                                ) {
                                    layout_verify_heading!!.visibility = View.VISIBLE
                                    cardView_verify_heading!!.visibility = View.VISIBLE
                                    layout_failedDividerEnd!!.visibility = View.VISIBLE

                                } else if (createAccMerchantModel.payu_section_id.equals(
                                        "bank_failed",
                                        ignoreCase = true
                                    )
                                ) {
                                    layout_bank_failed_heading!!.visibility = View.VISIBLE
                                    cardView_bank_failed!!.visibility = View.VISIBLE
                                    layout_bankFailed_DividerEnd!!.visibility = View.VISIBLE
                                } else if (createAccMerchantModel.payu_section_id.equals(
                                        "bank_success_2",
                                        ignoreCase = true
                                    )
                                ) {
                                    verify_successOld_card!!.visibility = View.VISIBLE
                                    layout_oldSuccess_heading!!.visibility = View.VISIBLE
                                    layout_vSuccess_heading!!.visibility = View.GONE
                                    verify_success_card!!.visibility = View.GONE
                                } else {
                                    if (createAccMerchantModel.payu_section_id.equals(
                                            "bank_verification_in_progress",
                                            ignoreCase = true
                                        )
                                    ) {
                                        layout_heading_bank_inProgress!!.visibility =
                                            View.VISIBLE
                                        cardView_bank_inProgress!!.visibility = View.VISIBLE
                                        layout_bank_inProgress_dividerEnd!!.visibility =
                                            View.VISIBLE
                                        tv_bank_inProgress1!!.text =
                                            resources.getString(R.string.bank_inProgress2) + " " + createAccMerchantModel.check_after_time + " " + resources.getString(
                                                R.string.bank_inProgress3
                                            )
                                    }
                                }
                                tv_merchant_Name!!.text = createAccMerchantModel.name
                                tv_merchant_email!!.text = createAccMerchantModel.email
                                tv_merchant_phone!!.text = createAccMerchantModel.phone
                                tv_panCardName!!.text = createAccMerchantModel.pancard_name
                                tv_panCardNumber!!.text = createAccMerchantModel.pancard_number
                                tv_merchant_address!!.text = createAccMerchantModel.address_line
                                tv_merchant_pinCode!!.text =
                                    Integer.toString(createAccMerchantModel.pinCode)
                                tv_merchant_HolderName!!.text =
                                    createAccMerchantModel.holder_name
                                tv_merchant_AccNo!!.text =
                                    createAccMerchantModel.bank_account_number
                                tv_merchant_ifscCode!!.text = createAccMerchantModel.ifsc_code
                                tv_merchant_registeredName!!.text =
                                    createAccMerchantModel.business_name
                                tv_merchant_entityType!!.text =
                                    createAccMerchantModel.business_entity
                            } else {
                                if (createFlag == 1) {
                                    imageView_refresh!!.visibility = View.GONE
                                    createMerDetailsStep1Flag = true
                                    progressDialog!!.dismiss()
                                    tv_error!!.visibility = View.GONE
                                    MerchantSetUp_layout!!.visibility = View.VISIBLE
                                    imageView_help!!.visibility = View.VISIBLE
                                    toolbar!!.title = "PayU Setup"
                                    btn_createNewAccount!!.setOnClickListener {
                                        createMerDetailsStep1Flag = false
                                        layout_ribbon!!.visibility = View.VISIBLE
                                        tv_step1!!.visibility = View.VISIBLE
                                        tv_step2!!.visibility = View.GONE
                                        createNewAccStep1_layout!!.visibility = View.VISIBLE
                                        updateMerchant_layout!!.visibility = View.GONE
                                        MerchantSetUp_layout!!.visibility = View.GONE
                                        isPayUSetupPage = true
                                        imageView_help!!.visibility = View.GONE
                                        toolbar!!.title = "Create PayU Account"
                                        val iDetails = intent
                                        val bundle = iDetails.extras
                                        merName = bundle!!["Doc_Name"] as String?
                                        merEmail = bundle["Doc_Email"] as String?
                                        merPhone = bundle["Doc_Phone"] as String?
                                        et_merchantName!!.setText(merName)
                                        et_merchantEmail!!.setText(merEmail)
                                        et_merchantPhone!!.setText(merPhone)
                                    }
                                    btn_createExistingAccount!!.setOnClickListener {
                                        val existingIntent = Intent(
                                            this@CreateNewAccount,
                                            ConnectExistingAccount::class.java
                                        )
                                        startActivity(existingIntent)
                                    }
                                }
                            }
                        } else {
                            if (jsonObject.getInt("status_code") == 422) {
                                createMerDetailsStep1Flag = true
                                progressDialog!!.dismiss()
                                toolbar!!.title = "PayU Account Status"
                                tv_error!!.visibility = View.VISIBLE
                                tv_error!!.text = jsonObject.getString("response")
                            } else {
                                if (value != null) {
                                    errorHandler(this@CreateNewAccount, value)
                                }
                            }
                        }
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                }
        }

    //pan update details screen entity details
    fun getPanBusinessEntityFieldDetails(type: String?) {
        createNewAccountViewModel!!.getCategoriesDetails(
            this@CreateNewAccount,
            type,
            category_string
        ).observe(this) { value ->
            try {
                val response = JSONObject(value).getJSONObject("response")
                Log.d("category", "BusEntityDetails$response")
                val rootObj = response.getJSONArray("response")
                arrayList_entityType!!.add("Select Business Entity Type")
                for (j in 0 until rootObj.length()) {
                    arrayList_entityType!!.add(rootObj[j].toString())
                }
                spinner_pan_entityType!!.adapter = entityType_arrayAdapter
                entityType_arrayAdapter!!.notifyDataSetChanged()
                spinner_pan_entityType!!.onItemSelectedListener =
                    object : AdapterView.OnItemSelectedListener {
                        override fun onItemSelected(
                            parent: AdapterView<*>?,
                            view: View,
                            position: Int,
                            id: Long
                        ) {
                            entityType_position = spinner_pan_entityType!!.selectedItemPosition
                            entityType_string = spinner_pan_entityType!!.selectedItem.toString()
                            Log.d(TAG, "entity_position: $entityType_position")
                            Log.d(TAG, "entity_string: $entityType_string")
                        }

                        override fun onNothingSelected(parent: AdapterView<*>?) {}
                    }
                Log.d("entityList", "list$arrayList_entityType")
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }
    }

    fun getBusinessEntityFieldDetails(type: String?) {
        createNewAccountViewModel!!.getCategoriesDetails(
            this@CreateNewAccount,
            type,
            category_string
        ).observe(this) { value ->
            try {
                val response = JSONObject(value).getJSONObject("response")
                Log.d("category", "BusEntityDetails$response")
                val rootObj = response.getJSONArray("response")
                //arrayList_entityType.add("Select Business Entity Type");
                for (j in 0 until rootObj.length()) {
                    arrayList_entityType!!.add(rootObj[j].toString())
                }
                if (PanDetails) {
                    spinner_pan_entityType!!.adapter = entityType_arrayAdapter
                    entityType_arrayAdapter!!.notifyDataSetChanged()
                    spinner_pan_entityType!!.onItemSelectedListener =
                        object : AdapterView.OnItemSelectedListener {
                            override fun onItemSelected(
                                parent: AdapterView<*>?,
                                view: View,
                                position: Int,
                                id: Long
                            ) {
                                entityType_position =
                                    spinner_pan_entityType!!.selectedItemPosition
                                entityType_string =
                                    spinner_pan_entityType!!.selectedItem.toString()
                                Log.d(TAG, "entity_position: $entityType_position")
                                Log.d(TAG, "entity_string: $entityType_string")
                            }

                            override fun onNothingSelected(parent: AdapterView<*>?) {}
                        }
                } else {
                    spinner_entityType!!.adapter = entityType_arrayAdapter
                    entityType_arrayAdapter!!.notifyDataSetChanged()
                    spinner_entityType!!.onItemSelectedListener =
                        object : AdapterView.OnItemSelectedListener {
                            override fun onItemSelected(
                                parent: AdapterView<*>?,
                                view: View,
                                position: Int,
                                id: Long
                            ) {
                                entityType_position = spinner_entityType!!.selectedItemPosition
                                entityType_string = spinner_entityType!!.selectedItem.toString()
                                Log.d(TAG, "entity_position: $entityType_position")
                                Log.d(TAG, "entity_string: $entityType_string")
                            }

                            override fun onNothingSelected(parent: AdapterView<*>?) {}
                        }
                }
                Log.d("entityList", "list$arrayList_entityType")
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }
    }

    private fun validateStep1(): Boolean {
        var noError = true
        merchantNameError!!.visibility = View.GONE
        merchantEmailError!!.visibility = View.GONE
        merchantPhoneError!!.visibility = View.GONE
        if (et_merchantName!!.text.toString().equals("", ignoreCase = true)) {
            noError = false
            merchantNameError!!.visibility = View.VISIBLE
        } else if (et_merchantEmail!!.text.toString().equals("", ignoreCase = true)) {
            noError = false
            merchantEmailError!!.visibility = View.VISIBLE
        } else if (et_merchantPhone!!.text.toString().equals("", ignoreCase = true)) {
            noError = false
            merchantPhoneError!!.visibility = View.VISIBLE
        }
        return noError
    }

    private fun validateInput(): Boolean {
        var noError = true
        bankAccNoError!!.visibility = View.GONE
        bankIfscCodeError!!.visibility = View.GONE
        bankAccNameError!!.visibility = View.GONE
        panNameError!!.visibility = View.GONE
        panNoError!!.visibility = View.GONE
        addressError!!.visibility = View.GONE
        pinCodeError!!.visibility = View.GONE
        busiRegisNameError!!.visibility = View.GONE
        ReenterAccError!!.visibility = View.GONE
        monthlyAmountError!!.visibility = View.GONE

        if (et_bankAccountNumber!!.text.toString().equals("", ignoreCase = true)) {
            noError = false
            bankAccNoError!!.visibility = View.VISIBLE
        } else if (et_bankIfsc!!.text.toString().equals("", ignoreCase = true)) {
            noError = false
            bankIfscCodeError!!.visibility = View.VISIBLE
        } else if (et_accountHolderName!!.text.toString().equals("", ignoreCase = true)) {
            noError = false
            bankAccNameError!!.visibility = View.VISIBLE
        } else if (et_panName!!.text.toString().equals("", ignoreCase = true)) {
            noError = false
            panNameError!!.visibility = View.VISIBLE
        } else if (et_panNumber!!.text.toString().equals("", ignoreCase = true)) {
            noError = false
            panNoError!!.visibility = View.VISIBLE
        } else if (et_address!!.text.toString().equals("", ignoreCase = true)) {
            noError = false
            addressError!!.visibility = View.VISIBLE
        } else if (et_pinCode!!.text.toString().equals("", ignoreCase = true)) {
            noError = false
            pinCodeError!!.visibility = View.VISIBLE
        } else if (et_busiRegiName!!.text.toString().equals("", ignoreCase = true)) {
            noError = false
            busiRegisNameError!!.visibility = View.VISIBLE
        } else if (et_reEnterAccNo!!.text.toString().equals("", ignoreCase = true)) {
            noError = false
            ReenterAccError!!.visibility = View.VISIBLE
        }
        return noError
    }

    //verify attempts exhausted
    val verifyAttemptsExhausted: Unit
        get() {
            val dialog = Dialog(this@CreateNewAccount)
            dialog.setContentView(R.layout.layout_exhausted_dialog)
            val img_close = dialog.findViewById<ImageView>(R.id.dialogExhaustedVerifyCancel)
            val btn_close = dialog.findViewById<Button>(R.id.exhausted_close)
            btn_close.setOnClickListener { dialog.dismiss() }
            img_close.setOnClickListener { dialog.dismiss() }
            dialog.show()
        }

    //Penny Amount Dialog
    fun showPennyVerifyDialog() {
        pennyTimerFlag = true
        val dialog = Dialog(this@CreateNewAccount)
        dialog.setCanceledOnTouchOutside(true)
        dialog.setContentView(R.layout.custom_penny_layout)
        et_otp_penny = dialog.findViewById(R.id.et_pennyOtp)
        otp_penny_heading = dialog.findViewById(R.id.otp_heading)
        otp_penny_heading?.text = resources.getString(R.string.penny_otp_heading) + " " + createAccMerchantModel.phone
        tv_penny_countDown = dialog.findViewById(R.id.text_view_penny_countdown)
        tv_pennyDialog_heading = dialog.findViewById(R.id.penny_account_heading1)
        tv_pennyDialog_heading?.text = resources.getString(R.string.heading_penny_amount) + " " + createAccMerchantModel.bank_account_number
        et_pennyAmount = dialog.findViewById<View>(R.id.et_pennyAmount) as EditText
        btn_verify = dialog.findViewById<View>(R.id.bt_verify) as Button
        val btn_close = dialog.findViewById<ImageView>(R.id.dialogPennyVerifyCancel)
        pennyOtpDetails
        val btn_penny_reSend = dialog.findViewById<TextView>(R.id.tv_penny_reSendOtp)
        btn_penny_reSend.setOnClickListener { showPennyVerifyDialog() }
        btn_close.setOnClickListener { dialog.dismiss() }
        btn_verify!!.setOnClickListener {
            // verify penny button
            val jsonValue = JSONObject()
            try {
                jsonValue.put("otp", et_otp_penny?.getText().toString())
                jsonValue.put("penny_amount", et_pennyAmount!!.text.toString())
            } catch (e: Exception) {
                e.printStackTrace()
            }
            progressDialog!!.setMessage("Please wait..")
            progressDialog!!.show()
            createNewAccountViewModel!!.postPennyVerification(this@CreateNewAccount, jsonValue)
                .observe(this@CreateNewAccount) { value ->
                    if (value != null) {
                        Log.d("Response", value)
                    }
                    try {
                        progressDialog!!.dismiss()
                        val resObj = JSONObject(value)
                        if (resObj.getInt("status_code") != 200) {
                            retry_penny_flag = true
                            dialog.dismiss()
                            customDialog(resObj.getString("response"))
                            if (value != null) {
                                errorHandler(this@CreateNewAccount, value)
                            }
                        } else {
                            paymentMerchantApi
                            dialog.dismiss()
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
        }
        dialog.show()
    }

    //create merchant submit
    fun createMerchantSuccessDialog() {
        val dialog = Dialog(this@CreateNewAccount)
        dialog.setCanceledOnTouchOutside(false)
        dialog.setContentView(R.layout.datasubmitedcustomdialog)
        val toolbar = dialog.findViewById<TextView>(R.id.createSubmitToolbar)
        val tv_close = dialog.findViewById<TextView>(R.id.close_textView)
        val close = dialog.findViewById<ImageView>(R.id.closeToolBarButton)
        close.setOnClickListener {
            dialog.dismiss()
            createNewAccStep2_layout!!.visibility = View.GONE
            tv_step2!!.visibility = View.GONE
            paymentMerchantApi
        }
        tv_close.setOnClickListener {
            dialog.dismiss()
            createNewAccStep2_layout!!.visibility = View.GONE
            tv_step2!!.visibility = View.GONE
            paymentMerchantApi
        }
        dialog.show()
    }

    //merchant support custom dialog
    fun customMerchantDialogSupport() {
        val dialog = Dialog(this@CreateNewAccount)
        dialog.setCanceledOnTouchOutside(true)
        dialog.setContentView(R.layout.layout_merchant_support)
        val btn_close = dialog.findViewById<ImageView>(R.id.dialogMerchantSupportCancel)
        val supportURL = dialog.findViewById<TextView>(R.id.dialogMerchantSupportURL)
        val supportCallBack = dialog.findViewById<TextView>(R.id.dialogCallBack)
        val supportHelp = dialog.findViewById<TextView>(R.id.dialogHelpText)
        supportURL.setOnClickListener { //support URL
            var browserIntent: Intent? = null
            try {
                val login_URL = createAccMerchantModel.payULogin_URL
                browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(login_URL))
                startActivity(browserIntent)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        supportCallBack.setOnClickListener { //call URL
            var browserIntent: Intent? = null
            try {
                val login_URL = createAccMerchantModel.payU_callback_url
                browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(login_URL))
                startActivity(browserIntent)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        supportHelp.setOnClickListener { //help URL
            var browserIntent: Intent? = null
            try {
                val login_URL = createAccMerchantModel.payU_support_help_url
                browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(login_URL))
                startActivity(browserIntent)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        btn_close.setOnClickListener { dialog.dismiss() }
        dialog.show()
    }

    fun customPennyDialog() {
        val dialog = Dialog(this@CreateNewAccount)
        dialog.setCanceledOnTouchOutside(true)
        dialog.setContentView(R.layout.custom_penny_dialog)
        val toolbar = dialog.findViewById<TextView>(R.id.dailogArticleHeaderText)
        val btn_close = dialog.findViewById<ImageView>(R.id.dialogPennyCancel)
        toolbar.text = "Penny Verification"
        val verify_btn = dialog.findViewById<Button>(R.id.bt_penny_verification)
        verify_btn.setOnClickListener {
            dialog.dismiss()
            showPennyVerifyDialog()
        }
        btn_close.setOnClickListener { dialog.dismiss() }
        dialog.show()
    }

    //custom error dialog
    fun customDialog(errorMessage: String?) {
        val dialog = Dialog(this@CreateNewAccount)
        dialog.setCanceledOnTouchOutside(false)
        dialog.setContentView(R.layout.custom_otp_dialog)
        val toolbar = dialog.findViewById<TextView>(R.id.otpDialogToolbar)
        btn_retry_penny = dialog.findViewById<View>(R.id.bt_retry) as Button
        val btn_close = dialog.findViewById<ImageButton>(R.id.closeCustomButton)
        val error = dialog.findViewById<TextView>(R.id.error_msg)
        error.text = errorMessage
        if (retry_bank_flag) {
            toolbar.text = "Invalid Data"
            btn_retry_penny!!.text = "Retry Again"
        } else if (retry_penny_flag) {
            toolbar.text = "OTP"
            btn_retry_penny!!.text = "Retry Penny Verification"
        } else if (createNewAccFlag) {
            toolbar.text = "Invalid Data"
            btn_retry_penny!!.visibility = View.GONE
        }
        btn_retry_penny!!.setOnClickListener {
            if (retry_bank_flag) {
                UpdateBankDetails()
                dialog.dismiss()
            } else if (retry_penny_flag) {
                showPennyVerifyDialog()
                dialog.dismiss()
            } else if (retry_pan_flag) {
                UpdatePanDetails()
                dialog.dismiss()
            }
        }
        btn_close.setOnClickListener {
            dialog.dismiss()
            customDialogOpen = 0
        }
        dialog.show()
    }

    override fun onBackPressed() {
        if (createMerDetailsStep1Flag) {
            finish()
        } else if (createMerDetailsStep2Flag) {
            createMerDetailsStep1Flag = true
            createMerDetailsStep2Flag = false
            layout_accountStatus!!.visibility = View.VISIBLE
            layout_btnDetails!!.visibility = View.VISIBLE
            layout_merchantDetails!!.visibility = View.GONE
            toolbar!!.title = "PayU Account Status"
            imageView_refresh!!.visibility = View.VISIBLE
            cancelTimer()
        } else if (getMerDetailsFlag) {
            getMerDetailsFlag = false
            layout_merchantDetails!!.visibility = View.VISIBLE
            layout_btnDetails!!.visibility = View.GONE
            layout_bankOTP!!.visibility = View.GONE
            layout_accountStatus!!.visibility = View.GONE
            cancelTimer()
        } else if (updateBankDetailsFlag1) {
            updateBankDetailsFlag1 = false
            createMerDetailsStep1Flag = true
            updateMerchant_layout!!.visibility = View.VISIBLE
            layout_bankOTP!!.visibility = View.GONE
            toolbar!!.title = "PayU Account Status"
            if (createAccMerchantModel.payu_status !== "live") {
                imageView_refresh!!.visibility = View.VISIBLE
            }
            cancelTimer()
        } else if (merchantDetailsBankUpdateFlag) {
            merchantDetailsBankUpdateFlag = false
            createMerDetailsStep2Flag = false
            createMerDetailsStep1Flag = true
            layout_accountStatus!!.visibility = View.VISIBLE
            layout_btnDetails!!.visibility = View.VISIBLE
            layout_merchantDetails!!.visibility = View.GONE
            layout_bankOTP!!.visibility = View.GONE
            cancelTimer()
        } else if (isPayUSetupPage) {
            createMerDetailsStep1Flag = true
            layout_ribbon!!.visibility = View.GONE
            tv_step1!!.visibility = View.VISIBLE
            createNewAccStep1_layout!!.visibility = View.VISIBLE
            createNewAccStep2_layout!!.visibility = View.GONE
            tv_step2!!.visibility = View.GONE
            MerchantSetUp_layout!!.visibility = View.GONE
            isPayUSetupPage = false
            cancelTimer()
        } else {
            if (step2CreateMerchant) {
                createMerDetailsStep1Flag = true
                step2CreateMerchant = false
                bankDetails_cardView!!.visibility = View.GONE
                createNewAccStep1_layout!!.visibility = View.VISIBLE
                createNewAccStep2_layout!!.visibility = View.GONE
                tv_step1!!.visibility = View.VISIBLE
                tv_step2!!.visibility = View.GONE
                cancelTimer()
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                if (createMerDetailsStep1Flag) {
                    finish()
                } else if (createMerDetailsStep2Flag) {
                    createMerDetailsStep1Flag = true
                    createMerDetailsStep2Flag = false
                    layout_accountStatus!!.visibility = View.VISIBLE
                    layout_btnDetails!!.visibility = View.VISIBLE
                    layout_merchantDetails!!.visibility = View.GONE
                    toolbar!!.title = "PayU Account Status"
                    imageView_refresh!!.visibility = View.VISIBLE
                    cancelTimer()
                } else if (getMerDetailsFlag) {
                    getMerDetailsFlag = false
                    layout_merchantDetails!!.visibility = View.VISIBLE
                    layout_btnDetails!!.visibility = View.GONE
                    layout_bankOTP!!.visibility = View.GONE
                    layout_accountStatus!!.visibility = View.GONE
                    if (createAccMerchantModel.payu_status !== "live") {
                        imageView_refresh!!.visibility = View.VISIBLE
                    }
                    cancelTimer()
                } else if (updateBankDetailsFlag1) {
                    updateBankDetailsFlag1 = false
                    createMerDetailsStep1Flag = true
                    updateMerchant_layout!!.visibility = View.VISIBLE
                    layout_bankOTP!!.visibility = View.GONE
                } else if (merchantDetailsBankUpdateFlag) {
                    merchantDetailsBankUpdateFlag = false
                    createMerDetailsStep2Flag = false
                    createMerDetailsStep1Flag = true
                    layout_accountStatus!!.visibility = View.VISIBLE
                    layout_btnDetails!!.visibility = View.VISIBLE
                    layout_merchantDetails!!.visibility = View.GONE
                    layout_bankOTP!!.visibility = View.GONE
                }  else if (isPayUSetupPage) {
                    createMerDetailsStep1Flag = true
                    layout_ribbon!!.visibility = View.GONE
                    tv_step1!!.visibility = View.VISIBLE
                    createNewAccStep1_layout!!.visibility = View.VISIBLE
                    createNewAccStep2_layout!!.visibility = View.GONE
                    tv_step2!!.visibility = View.GONE
                    MerchantSetUp_layout!!.visibility = View.GONE
                    isPayUSetupPage = false
                } else {
                    if (step2CreateMerchant) {
                        createMerDetailsStep1Flag = true
                        step2CreateMerchant = false
                        bankDetails_cardView!!.visibility = View.GONE
                        createNewAccStep1_layout!!.visibility = View.VISIBLE
                        createNewAccStep2_layout!!.visibility = View.GONE
                        tv_step1!!.visibility = View.VISIBLE
                        tv_step2!!.visibility = View.GONE
                    }
                }
                super.onOptionsItemSelected(item)
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View, position: Int, id: Long) {}
    override fun onNothingSelected(parent: AdapterView<*>?) {}
    private fun startBankTimer() {
        val duration = TimeUnit.MINUTES.toMillis(15)
        cancelTimer()
        mCountDownTimer = object : CountDownTimer(duration, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val sDuration = String.format(
                    Locale.ENGLISH,
                    "%02d : %02d",
                    TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished),
                    TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) -
                            TimeUnit.MINUTES.toSeconds(
                                TimeUnit.MILLISECONDS.toMinutes(
                                    millisUntilFinished
                                )
                            )
                )

                timer_text = findViewById(R.id.text_view_countdown)
                timer_text?.text = sDuration

            }

            override fun onFinish() {
                mCountDownTimer!!.cancel()
            }
        }.start()
    }

    //cancel timer
    fun cancelTimer() {
        if (mCountDownTimer != null) mCountDownTimer!!.cancel()
    }

    fun startPanTimer() {
        val duration = TimeUnit.MINUTES.toMillis(15)
        cancelTimer()
        mCountDownTimer = object : CountDownTimer(duration, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val sDuration = String.format(
                    Locale.ENGLISH,
                    "%02d : %02d",
                    TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished),
                    TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) -
                            TimeUnit.MINUTES.toSeconds(
                                TimeUnit.MILLISECONDS.toMinutes(
                                    millisUntilFinished
                                )
                            )
                )
                timer_pan_text!!.text = sDuration
            }

            override fun onFinish() {

            }
        }.start()
        mTimerRunning = true
    }

    //penny timer
    fun startPennyTimer() {
        val duration = TimeUnit.MINUTES.toMillis(15)
        cancelTimer()
        mCountDownTimer = object : CountDownTimer(duration, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val sDuration = String.format(
                    Locale.ENGLISH,
                    "%02d : %02d",
                    TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished),
                    TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) -
                            TimeUnit.MINUTES.toSeconds(
                                TimeUnit.MILLISECONDS.toMinutes(
                                    millisUntilFinished
                                )
                            )
                )
                tv_penny_countDown!!.text = sDuration
            }

            override fun onFinish() {

            }
        }.start()
        mTimerRunning = true
    }

    private fun resetTimer() {

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        //MenuInflater menuInflater = getMenuInflater();
        //menuInflater.inflate(R.menu.menu_help, menu);
        return true
    }

    fun customQueriesDialog() {
        val dialog = Dialog(this@CreateNewAccount)
        dialog.setCanceledOnTouchOutside(true)
        dialog.setContentView(R.layout.queriescustomdialog)
        val toolbar = dialog.findViewById<TextView>(R.id.queriesToolbar)
        val btn_askQueries = dialog.findViewById<RelativeLayout>(R.id.layout_queries)
        val btn_close = dialog.findViewById<ImageView>(R.id.dialogQueriesCancel)
        btn_askQueries.setOnClickListener {
            val intent = Intent(mContext, HelpActivity::class.java)
            startActivity(intent)
        }
        btn_close.setOnClickListener { dialog.dismiss() }
        dialog.show()
    }

    companion object {
        val TAG = CreateNewAccount::class.java.simpleName

        @JvmField
        var isMerchantCreated = 0

        @JvmField
        var PanDetails = false
    }
}