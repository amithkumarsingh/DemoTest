package com.whitecoats.clinicplus.activities

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.PorterDuff
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.*
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import androidx.appcompat.widget.Toolbar
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.widget.NestedScrollView
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.*
import com.android.volley.Request.Method
import com.android.volley.toolbox.Volley
import com.google.gson.GsonBuilder
import com.whitecoats.clinicplus.R
import com.whitecoats.clinicplus.adapters.AddUPIIdAndQRAdapter
import com.whitecoats.clinicplus.adapters.UPIHowItWorksAdapter
import com.whitecoats.clinicplus.apis.ApiUrls
import com.whitecoats.clinicplus.interfaces.PaymentTypeClick
import com.whitecoats.clinicplus.models.APIError
import com.whitecoats.clinicplus.models.AccountDeleteRequest
import com.whitecoats.clinicplus.models.UpiIdDetails
import com.whitecoats.clinicplus.utils.ErrorHandlerClass
import com.whitecoats.clinicplus.viewmodels.CommonViewModel
import com.whitecoats.clinicplus.viewmodels.UserAccountDeleteViewModel
import org.json.JSONArray
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


class UPIPaymentSetUpScreen : AppCompatActivity() {
    private lateinit var toolbar: Toolbar
    private lateinit var callingFrom: String
    private lateinit var tvHowItWorks: TextView
    private lateinit var cardViewParent: CardView
    private lateinit var constUPI: ConstraintLayout
    private lateinit var tvUPIHeader: TextView
    private lateinit var tvUPIBody: TextView
    private lateinit var constUPIAddParent: ConstraintLayout
    private lateinit var rvAddUPIIdAndQR: RecyclerView
    private lateinit var addUPIIdAndQRAdapter: AddUPIIdAndQRAdapter
    private lateinit var nestedScroll: NestedScrollView
    private lateinit var tvStaffHeader: TextView
    private lateinit var tvStaffBody: TextView
    private lateinit var llStaffName: LinearLayout
    private lateinit var etStaffName: EditText
    private lateinit var llStaffNumber: LinearLayout
    private lateinit var etStaffNumber: EditText
    private lateinit var swPaymentModeUPI: SwitchCompat
    private lateinit var btnSaveUPIIDs: Button
    private lateinit var dialog: Dialog
    private lateinit var upiHowItWorksAdapter: UPIHowItWorksAdapter
    private lateinit var cardViewOfflinePayment: CardView
    private lateinit var tvOfflinePaymentContent: TextView
    private lateinit var tvSwitcText: TextView
    private lateinit var tvPaymentMode: TextView
    private lateinit var switchPaymentModeOffline: SwitchCompat
    private val REQUEST_ID_MULTIPLE_PERMISSIONS = 50

    private lateinit var launchGalleryResults: ActivityResultLauncher<Intent>
    private lateinit var launcherCameraResults: ActivityResultLauncher<Intent>
    private lateinit var fileUri: Uri
    private var uploadImageResponse = ""
    private lateinit var uploadedQRCodesNamesList: List<String>
    private var selectedQRFieldPos: Int = 0
    private var upiIdDetailsList: MutableList<UpiIdDetails> = ArrayList()
    private var upiIdDetailsListDup: MutableList<UpiIdDetails> = ArrayList()
    private lateinit var commonViewModel: CommonViewModel
    private lateinit var staffName: String
    private lateinit var staffContact: String
    private var defaultPaymentMode = ""
    private lateinit var tvPaymentModeTextOffline: TextView
    private var upiHowItWorksList: MutableList<String> = ArrayList()


    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_upipayment_set_up_screen)
        toolbar = findViewById(R.id.upiPaymentToolbar)
        val intent = intent
        callingFrom = intent.getStringExtra("callingFrom")!!
        toolbar.title = callingFrom
        commonViewModel = ViewModelProvider(this@UPIPaymentSetUpScreen)[CommonViewModel::class.java]
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
        rvAddUPIIdAndQR = findViewById(R.id.rv_Add_UPI_IDS_QR)
        tvStaffHeader = findViewById(R.id.tv_Staff_header)
        tvStaffBody = findViewById(R.id.tv_staff_body)
        llStaffName = findViewById(R.id.ll_staff_name)
        nestedScroll = findViewById(R.id.nestedScroll)
        etStaffName = findViewById(R.id.et_staffName)
        llStaffNumber = findViewById(R.id.ll_staff_number)
        etStaffNumber = findViewById(R.id.et_staff_number)
        swPaymentModeUPI = findViewById(R.id.sw_payment_mode_upi)
        btnSaveUPIIDs = findViewById(R.id.btn_save_UPIIDs)
        tvHowItWorks = findViewById(R.id.tv_how_it_works)
        cardViewOfflinePayment = findViewById(R.id.cardview_offline_payment)
        tvOfflinePaymentContent = findViewById(R.id.tv_offline_payment_content)
        switchPaymentModeOffline = findViewById(R.id.switch_payment_mode_offline)
        tvPaymentMode = findViewById(R.id.tv_payment_mode)
        tvSwitcText = findViewById(R.id.tv_switch_text)
        tvPaymentModeTextOffline = findViewById(R.id.tv_payment_mode)

        btnSaveUPIIDs.isEnabled = false
        btnSaveUPIIDs.setBackgroundColor(
            ContextCompat.getColor(
                this@UPIPaymentSetUpScreen,
                R.color.light_blue
            )
        )
        btnSaveUPIIDs.setTextColor(
            ContextCompat.getColor(
                this@UPIPaymentSetUpScreen,
                R.color.white
            )
        )
        swPaymentModeUPI.isEnabled = false
        tvSwitcText.alpha = 0.5f
        tvSwitcText.isClickable = false
        getPaymentOptionDetails()
        if (callingFrom.equals("Offline Payment", ignoreCase = true)) {
            cardViewOfflinePayment.visibility = View.VISIBLE
            nestedScroll.visibility = View.GONE
            toolbar.title = "All Payment Offline"
        } else {
            toolbar.title = "Add UPI Id"
            cardViewOfflinePayment.visibility = View.GONE
            nestedScroll.visibility = View.VISIBLE
            addUPIIdAndQRAdapter =
                AddUPIIdAndQRAdapter(btnSaveUPIIDs, swPaymentModeUPI, tvSwitcText,
                    upiIdDetailsListDup,
                    object : PaymentTypeClick {
                        override fun onItemClick(pos: Int, itemName: String,uploadQR:LinearLayout?,llParentAddedQR: LinearLayout?) {
                            when (itemName) {
                                "UploadQACode" -> {
                                    selectedQRFieldPos = pos
                                    if (checkAndRequestPermissions(this@UPIPaymentSetUpScreen)) {
                                        // selectedQRFieldPos = pos
                                        showSelectionOptionDialog()
                                    }
                                }
                                "DeleteQACode" -> {
                                    val builder = AlertDialog.Builder(
                                        this@UPIPaymentSetUpScreen,
                                        R.style.CustomAlertDialog
                                    )
                                        .create()
                                    builder.setCancelable(false)
                                    val view =
                                        layoutInflater.inflate(R.layout.delete_qr_code_popup, null)
                                    val deleteBtn = view.findViewById<Button>(R.id.delete_btn)
                                    val cancelBtn = view.findViewById<Button>(R.id.cancel_btn)
                                    builder.setView(view)
                                    cancelBtn.setOnClickListener {
                                        builder.dismiss()
                                    }
                                    deleteBtn.setOnClickListener {
                                        uploadQR!!.visibility = View.VISIBLE
                                        llParentAddedQR!!.visibility = View.GONE
                                        val removeUploadedQRCode = UpiIdDetails(
                                            upiIdDetailsListDup[pos].id,
                                            upiIdDetailsListDup[pos].upi_id,
                                            ""
                                        )
                                        upiIdDetailsListDup[pos] = removeUploadedQRCode
                                        for (i in 0 until upiIdDetailsListDup.size) {
                                            val view: View = rvAddUPIIdAndQR.getChildAt(i)
                                            val editText: EditText =
                                                view.findViewById(R.id.et_enter_UPI)
                                            val upiID = editText.text.toString()
                                            if (upiID != "" || upiIdDetailsListDup[i].upi_scan_image_url != "") {
                                                btnSaveUPIIDs.isEnabled = true
                                                btnSaveUPIIDs.setBackgroundColor(
                                                    ContextCompat.getColor(
                                                        this@UPIPaymentSetUpScreen,
                                                        R.color.colorAccent
                                                    )
                                                )
                                                btnSaveUPIIDs.setTextColor(
                                                    ContextCompat.getColor(
                                                        this@UPIPaymentSetUpScreen,
                                                        R.color.white
                                                    )
                                                )
                                                swPaymentModeUPI.isEnabled = true
                                                tvSwitcText.alpha = 1f
                                                tvSwitcText.isClickable = true
                                                break
                                            } else {
                                                btnSaveUPIIDs.isEnabled = false
                                                btnSaveUPIIDs.setBackgroundColor(
                                                    ContextCompat.getColor(
                                                        this@UPIPaymentSetUpScreen,
                                                        R.color.light_blue
                                                    )
                                                )
                                                btnSaveUPIIDs.setTextColor(
                                                    ContextCompat.getColor(
                                                        this@UPIPaymentSetUpScreen,
                                                        R.color.white
                                                    )
                                                )
                                                swPaymentModeUPI.isEnabled = false
                                                tvSwitcText.alpha = 0.5f
                                                tvSwitcText.isClickable = false
                                            }
                                        }
                                        builder.dismiss()
                                    }
                                    builder.setCanceledOnTouchOutside(false)
                                    builder.show()
                                }
                            }
                        }
                    })

            val mLinerLayout = LinearLayoutManager(this)
            rvAddUPIIdAndQR.layoutManager = mLinerLayout
            rvAddUPIIdAndQR.itemAnimator = DefaultItemAnimator()
            rvAddUPIIdAndQR.adapter = addUPIIdAndQRAdapter

            launchGalleryResults = registerForActivityResult(
                ActivityResultContracts.StartActivityForResult()
            ) { result: ActivityResult ->
                //Request code 2
                val data = result.data
                val resultCode = result.resultCode
                if (resultCode == RESULT_OK) {
                    val uri = data!!.data
                    try {
                        val bitmapImage =
                            MediaStore.Images.Media.getBitmap(contentResolver, uri)
                        val nh =
                            (bitmapImage.height * (720.0 / bitmapImage.width)).toInt()
                        val scaled = Bitmap.createScaledBitmap(bitmapImage, 720, nh, true)
                        uploadImage(scaled)
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }
            launcherCameraResults = registerForActivityResult(
                ActivityResultContracts.StartActivityForResult()
            ) { result: ActivityResult ->
                //request code 1
                val data = result.data
                val resultCode = result.resultCode
                if (resultCode == RESULT_OK) {
                    try {
                        val contentResolver = contentResolver

                        // Use the content resolver to open camera taken image input stream through image uri.
                        val inputStream =
                            contentResolver.openInputStream(fileUri)
                        val bitmapImage = BitmapFactory.decodeStream(inputStream)
                        val nh =
                            (bitmapImage.height * (720.0 / bitmapImage.width)).toInt()
                        val scaled = Bitmap.createScaledBitmap(bitmapImage, 720, nh, true)
                        uploadImage(scaled) //later uncomment
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        }
        swPaymentModeUPI.setOnClickListener { view ->
            if (view is SwitchCompat) {
                setDefaultPaymentMode(view.isChecked, "upi")
                swPaymentModeUPI.isChecked = view.isChecked

            }
        }
        tvSwitcText.setOnClickListener {
            if (swPaymentModeUPI.isChecked) {
                swPaymentModeUPI.isChecked = false
                setDefaultPaymentMode(false, "upi")
            } else {
                swPaymentModeUPI.isChecked = true
                setDefaultPaymentMode(true, "upi")
            }
        }

        switchPaymentModeOffline.setOnClickListener { view ->
            if (view is SwitchCompat) {
                setDefaultPaymentMode(view.isChecked, "offline")
                switchPaymentModeOffline.isChecked = view.isChecked

            }
        }
        tvPaymentModeTextOffline.setOnClickListener {
            if (switchPaymentModeOffline.isChecked) {
                switchPaymentModeOffline.isChecked = false
                setDefaultPaymentMode(false, "offline")
            } else {
                switchPaymentModeOffline.isChecked = true
                setDefaultPaymentMode(true, "offline")
            }
        }

        tvHowItWorks.setOnClickListener {
            if (upiHowItWorksList.size > 0) {
                showHowItWorksDialog()
            }
        }

        btnSaveUPIIDs.setOnClickListener {
            saveUpiOrOfflinePaymentModeDetails()
        }

    }

    private fun setDefaultPaymentMode(isChecked: Boolean, callingFrom: String) {
        val url = ApiUrls.setDefaultPaymentMode
        val jsonReq = JSONObject()
        //  jsonReq.put("interface_id", ApiUrls.doctorId)
        if (isChecked) {
            jsonReq.put("default_payment_mode", callingFrom)
        } else {
            jsonReq.put("default_payment_mode", "offline")
        }
        commonViewModel.commonViewModelCall(url, jsonReq, Method.POST).observe(
            this@UPIPaymentSetUpScreen
        ) { result ->
            try {
                val resultObj = JSONObject(result)
                if (resultObj.getInt("status_code") == 200) {
                    val resObj = resultObj.optJSONObject("response")
                    val res = resObj!!.getString("response")
                    if (res.equals("success", ignoreCase = true)) {
                        Toast.makeText(
                            this@UPIPaymentSetUpScreen,
                            "Payment mode set successfully",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    if (callingFrom.equals("offline", ignoreCase = true)) {
                        switchPaymentModeOffline.isChecked = !isChecked
                    } else {
                        swPaymentModeUPI.isChecked = !isChecked
                    }
                    switchPaymentModeOffline.isChecked = !isChecked
                    ErrorHandlerClass.errorHandler(this@UPIPaymentSetUpScreen, result)
                }
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
                if (callingFrom.equals("offline", ignoreCase = true)) {
                    switchPaymentModeOffline.isChecked = !isChecked
                } else {
                    swPaymentModeUPI.isChecked = !isChecked
                }
            }
        }

    }


    private fun saveUpiOrOfflinePaymentModeDetails() {
        showCustomProgressAlertDialog("Please Wait", "Please wait saving your UPI ID's")
        val jsonRes = JSONObject()

        val upiIdDetailsList = JSONArray()
        for (i in 0 until upiIdDetailsListDup.size) {
            val view: View = rvAddUPIIdAndQR.getChildAt(i)
            val editText: EditText = view.findViewById(R.id.et_enter_UPI)
            val upiID = editText.text.toString()
            val addedUPIListObj = JSONObject()
            if (upiID != "" || upiIdDetailsListDup[i].upi_scan_image_url != "") {
                if (upiIdDetailsListDup[i].id != 0) {
                    addedUPIListObj.put("id", upiIdDetailsListDup[i].id)
                }
                // addedUPIListObj.put("interface_id", ApiUrls.doctorId)
                addedUPIListObj.put("upi_id", upiID)
                addedUPIListObj.put(
                    "upi_scan_image_url",
                    upiIdDetailsListDup[i].upi_scan_image_url
                )
                upiIdDetailsList.put(addedUPIListObj)
            }

        }
        // jsonRes.put("interface_id", ApiUrls.doctorId)
        jsonRes.put("staff_name", etStaffName.text.toString())
        jsonRes.put("staff_contact", etStaffNumber.text.toString())
        jsonRes.put("paymentMode", "upi")
        jsonRes.put("upi_details", upiIdDetailsList)

        val url = ApiUrls.savePaymentOptions
        commonViewModel.commonViewModelCall(url, jsonRes, Method.POST).observe(
            this@UPIPaymentSetUpScreen
        ) { result ->
            try {
                val resultObj = JSONObject(result)
                if (resultObj.getInt("status_code") == 200) {
                    val resObj = resultObj.optJSONObject("response")
                    val res = resObj!!.getString("response")
                    if (res.equals("success", ignoreCase = true)) {
                        Toast.makeText(
                            this@UPIPaymentSetUpScreen,
                            "UPI details saved successfully",
                            Toast.LENGTH_SHORT
                        ).show()
                        finish()
                    }
                    dialog.dismiss()
                } else {
                    ErrorHandlerClass.errorHandler(this@UPIPaymentSetUpScreen, result)
                    dialog.dismiss()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                dialog.dismiss()
            }
        }
    }

    @SuppressLint("SuspiciousIndentation")
    private fun getPaymentOptionDetails() {
        showCustomProgressAlertDialog("Please Wait", "Fetching details")
        val url = ApiUrls.getPaymentOptionsDetails
        commonViewModel.commonViewModelCall(url, JSONObject(), Method.GET).observe(
            this@UPIPaymentSetUpScreen
        ) { result ->
            try {
                val resultObj = JSONObject(result)
                if (resultObj.getInt("status_code") == 200) {
                    val resObj = resultObj.optJSONObject("response")
                    val res = resObj!!.optJSONObject("response")
                    if (res!!.has("default_payment_mode")) {
                        defaultPaymentMode =
                            res.getString("default_payment_mode")
                        if (defaultPaymentMode.equals("offline", ignoreCase = true)) {
                            switchPaymentModeOffline.isChecked = true
                        } else if (defaultPaymentMode.equals("upi", ignoreCase = true)) {
                            swPaymentModeUPI.isChecked = true
                        }
                    }
                    if (res.has("upi_how_it_works")) {
                        val upiHowItWorks = res.getJSONArray("upi_how_it_works")
                        val size = upiHowItWorks.length()
                        if (upiHowItWorks.length() > 0) {
                            for (i in 0 until upiHowItWorks.length()) {
                                upiHowItWorksList.add(
                                    upiHowItWorks.getJSONObject(i).getString("text")
                                )
                            }
                        }
                    }

                    if (callingFrom != "Offline Payment") {
                        val upiCount = res.getInt("upi_count")
                        if (res.has("staff_name")) {
                            staffName = res.getString("staff_name")
                            if (staffName != "" && staffName != "null") {
                                etStaffName.setText(staffName)
                            }
                        }
                        if (res.has("staff_contact")) {
                            staffContact = res.getString("staff_contact")
                            if (staffContact != "" && staffContact != "null") {
                                etStaffNumber.setText(staffContact)
                            }
                        }

                        if (res.has("upi_details")) {
                            val upiIdList = res.optJSONArray("upi_details")
                            if (upiIdList!!.length() > 0) {
                                for (i in 0 until upiIdList.length()) {
                                    val upiIdDetails = UpiIdDetails(
                                        upiIdList.getJSONObject(i).getInt("id"),
                                        upiIdList.getJSONObject(i).getString("upi_id"),
                                        upiIdList.getJSONObject(i).getString("upi_scan_image_url")
                                    )
                                    upiIdDetailsList.add(upiIdDetails)
                                }
                            }
                        }
                        addUpiDetailsToListToShownOnScreen(upiCount)
                    }
                    dialog.dismiss()

                } else {
                    dialog.dismiss()
                    ErrorHandlerClass.errorHandler(this@UPIPaymentSetUpScreen, result)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                dialog.dismiss()
            }

        }

    }


    @SuppressLint("NotifyDataSetChanged")
    private fun addUpiDetailsToListToShownOnScreen(upiCount: Int) {
        uploadedQRCodesNamesList = ArrayList()
        if (upiIdDetailsList.size > 0) {
            for (i in 0 until upiIdDetailsList.size) {
                if (upiIdDetailsList[i].id != 0) {
                    upiIdDetailsListDup.add(
                        UpiIdDetails(
                            upiIdDetailsList[i].id,
                            upiIdDetailsList[i].upi_id,
                            upiIdDetailsList[i].upi_scan_image_url
                        )
                    )
                } else {
                    upiIdDetailsListDup.add(UpiIdDetails(0, "", ""))
                }
            }
            if (upiIdDetailsListDup.size != upiCount) {
                val enterUPISize = upiCount - upiIdDetailsList.size
                for (i in 0 until enterUPISize) {
                    upiIdDetailsListDup.add(UpiIdDetails(0, "", ""))
                }
            }
        } else {
            for (i in 0 until upiCount) {
                upiIdDetailsListDup.add(UpiIdDetails(0, "", ""))
            }
        }
        for (i in 0 until upiIdDetailsListDup.size) {
            if (upiIdDetailsListDup[i].id != 0) {
                btnSaveUPIIDs.isEnabled = true
                btnSaveUPIIDs.setBackgroundColor(
                    ContextCompat.getColor(
                        this@UPIPaymentSetUpScreen,
                        R.color.colorAccent
                    )
                )
                btnSaveUPIIDs.setTextColor(
                    ContextCompat.getColor(
                        this@UPIPaymentSetUpScreen,
                        R.color.white
                    )
                )
                swPaymentModeUPI.isEnabled = true
                tvSwitcText.alpha = 1f
                tvSwitcText.isClickable = true
                break
            } else {
                btnSaveUPIIDs.isEnabled = false
                btnSaveUPIIDs.setBackgroundColor(
                    ContextCompat.getColor(
                        this@UPIPaymentSetUpScreen,
                        R.color.light_blue
                    )
                )
                btnSaveUPIIDs.setTextColor(
                    ContextCompat.getColor(
                        this@UPIPaymentSetUpScreen,
                        R.color.white
                    )
                )
                swPaymentModeUPI.isEnabled = false
                tvSwitcText.alpha = 0.5f
                tvSwitcText.isClickable = false
            }
        }
        addUPIIdAndQRAdapter.notifyDataSetChanged()
    }

    private fun uploadImage(currentImage: Bitmap) {
        val url = ApiUrls.textArticleUploadImage
        showCustomProgressAlertDialog(
            "Please Wait",
            resources.getString(R.string.common_uploading_image)
        )

        //our custom volley request
        val volleyMultipartRequest: AppImageUploader = @SuppressLint("NotifyDataSetChanged")
        object : AppImageUploader(
            Method.POST, url,
            Response.Listener { response ->
                try {
                    dialog.dismiss()
                    var rootObj = JSONObject(String(response.data))
                    rootObj = rootObj.getJSONObject("response")
                    val urlName = rootObj.getString("url")
                    if (urlName != "") {
                        uploadImageResponse = urlName
                        val size = upiIdDetailsListDup.size
                        Log.i("size", size.toString() + selectedQRFieldPos.toString())
                        for (i in 0 until upiIdDetailsListDup.size) {
                            val view: View = rvAddUPIIdAndQR.getChildAt(i)
                            val editText: EditText = view.findViewById(R.id.et_enter_UPI)
                            val upiID = editText.text.toString()
                            val addObj: UpiIdDetails = if (selectedQRFieldPos == i) {
                                UpiIdDetails(
                                    upiIdDetailsListDup[i].id,
                                    upiID,
                                    uploadImageResponse
                                )

                            } else {
                                UpiIdDetails(
                                    upiIdDetailsListDup[i].id,
                                    upiID,
                                    upiIdDetailsListDup[i].upi_scan_image_url
                                )
                            }

                            upiIdDetailsListDup[i] = addObj
                        }
                        btnSaveUPIIDs.isEnabled = true
                        btnSaveUPIIDs.setBackgroundColor(
                            ContextCompat.getColor(
                                this@UPIPaymentSetUpScreen,
                                R.color.colorAccent
                            )
                        )
                        btnSaveUPIIDs.setTextColor(
                            ContextCompat.getColor(
                                this@UPIPaymentSetUpScreen,
                                R.color.white
                            )
                        )
                        swPaymentModeUPI.isEnabled = true
                        tvSwitcText.alpha = 1f
                        tvSwitcText.isClickable = true
                        val sizeOb = upiIdDetailsListDup.size
                        Log.i("size", sizeOb.toString())
                        addUPIIdAndQRAdapter.notifyDataSetChanged()
                        Toast.makeText(
                            applicationContext,
                            resources.getString(R.string.image_upload_sucessfully),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } catch (e: java.lang.Exception) {
                    e.printStackTrace()
                }
            },
            Response.ErrorListener { error ->
                when (error) {
                    is TimeoutError, is NoConnectionError -> {
                        Toast.makeText(
                            applicationContext,
                            resources.getString(R.string.slow_internet_connection),
                            Toast.LENGTH_LONG
                        ).show()

                    }
                    is AuthFailureError -> {
                        //TODO
                        Toast.makeText(
                            applicationContext,
                            resources.getString(R.string.slow_internet_connection),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    is ServerError -> {
                        //TODO
                        Toast.makeText(
                            applicationContext,
                            resources.getString(R.string.slow_internet_connection),
                            Toast.LENGTH_SHORT
                        ).show()

                    }
                    is NetworkError -> {
                        //TODO
                        Toast.makeText(
                            applicationContext,
                            resources.getString(R.string.slow_internet_connection),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    is ParseError -> {
                        //TODO
                        Toast.makeText(
                            applicationContext,
                            resources.getString(R.string.slow_internet_connection),
                            Toast.LENGTH_SHORT
                        ).show()

                    }
                    else -> {
                        Toast.makeText(
                            applicationContext,
                            resources.getString(R.string.image_upload_failed),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
                dialog.dismiss()
            }) {
            @Throws(AuthFailureError::class)
            override fun getParams(): Map<String, String>? {
                val params: MutableMap<String, String> = HashMap()
             //   params["path"] = "upi/" + ApiUrls.doctorId
                params["path"] = "client/upi/" + ApiUrls.doctorId + "/"
                params["public"] = "true"
                params["completeUrl"] = "true"
                return params
            }

            /*
             * Here we are passing image by renaming it with a unique name
             * */
            override fun getByteData(): Map<String, AppImageUploader.DataPart> {
                val params: MutableMap<String, DataPart> = HashMap()
                params["file"] =
                    DataPart("UPIQRCode" + ".png", getFileDataFromDrawable(currentImage))
                return params
            }


            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                val headers = HashMap<String, String>()
                headers["App-Origin"] = ApiUrls.appOrigin
                headers["Authorization"] =
                    "Bearer " + ApiUrls.loginToken //eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJodHRwOi8vNGRkYjMxNjkubmdyb2suaW8vYXBpL3YxL2F1dGgvdG9rZW4iLCJpYXQiOjE1MTgwODcyMzAsImV4cCI6MTUxOTI5NjgzMCwibmJmIjoxNTE4MDg3MjMwLCJqdGkiOiJ2UEJnY09hVGdGOU9YaFh4Iiwic3ViIjozMzk1LCJwcnYiOiIyM2JkNWM4OTQ5ZjYwMGFkYjM5ZTcwMWM0MDA4NzJkYjdhNTk3NmY3In0.Y3wU_NBXXhw--b75x4iwo6fmHQQzMLkla2gHrheWqeU");
                return headers
            }
        }

        //adding the request to volley
        Volley.newRequestQueue(this).add(volleyMultipartRequest)
    }


    fun getFileDataFromDrawable(bitmap: Bitmap): ByteArray? {
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 20, byteArrayOutputStream)
        return byteArrayOutputStream.toByteArray()
    }

    fun checkAndRequestPermissions(context: Activity?): Boolean {
        val writeExtStorePermission: Int =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                ContextCompat.checkSelfPermission(
                    context!!,
                    Manifest.permission.READ_MEDIA_IMAGES
                )
            } else {
                ContextCompat.checkSelfPermission(
                    context!!,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
            }

        val cameraPermission = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.CAMERA
        )
        val listPermissionsNeeded: MutableList<String> = ArrayList()
        if (cameraPermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.CAMERA)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (writeExtStorePermission != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded
                    .add(Manifest.permission.READ_MEDIA_IMAGES)
            }
        } else {
            if (writeExtStorePermission != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded
                    .add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            }
        }

        if (listPermissionsNeeded.isNotEmpty()) {
            ActivityCompat.requestPermissions(
                context, listPermissionsNeeded
                    .toTypedArray(),
                REQUEST_ID_MULTIPLE_PERMISSIONS
            )
            return false
        }
        return true
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            REQUEST_ID_MULTIPLE_PERMISSIONS -> {
                //check if all permissions are granted
                var allgranted = false
                for (i in grantResults.indices) {
                    if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                        allgranted = true
                    } else {
                        allgranted = false
                        /* ActivityCompat.requestPermissions(
                             this,
                             arrayOf(permissions[i]),
                             REQUEST_ID_MULTIPLE_PERMISSIONS
                         )*/

                        break
                    }
                }
                if (allgranted) {
                    showSelectionOptionDialog()
                } else {
                    Toast.makeText(
                        this@UPIPaymentSetUpScreen,
                        "Required both Camera and Gallery Access Permission to upload the QR code",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                return
            }
        }
        if (requestCode == 101) {
            Toast.makeText(
                this@UPIPaymentSetUpScreen,
                "Requi 101",
                Toast.LENGTH_SHORT
            ).show()
        }
    }


    private fun showSelectionOptionDialog() {
        val options = arrayOf<CharSequence>("Take Photo", "Choose From Gallery", "Cancel")
        val builder = AlertDialog.Builder(this@UPIPaymentSetUpScreen)
        builder.setTitle("Select Option")
        builder.setItems(
            options
        ) { dialog, item ->
            if (options[item].toString().equals("Take Photo", ignoreCase = true)) {
                dialog.dismiss()
                openCamera()

            } else if (options[item].toString().equals("Choose From Gallery", ignoreCase = true)) {
                dialog.dismiss()
                val intent = Intent()
                intent.type = "image/*"
                intent.action = Intent.ACTION_GET_CONTENT
                launchGalleryResults.launch(
                    Intent.createChooser(
                        intent,
                        resources.getString(R.string.common_select_picture)
                    )
                )

            } else if (options[item].toString().equals("Cancel", ignoreCase = true)) {
                dialog.dismiss()
            }
        }
        builder.show()
    }

    fun openCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        val photo = File(Environment.getExternalStorageDirectory(), "QRCode.png")
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            fileUri = Uri.fromFile(photo)
            intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri)
        } else {
            if (intent.resolveActivity(packageManager) != null) {
                //Create a file to store the image
                var photoFile: File? = null
                try {
                    photoFile = createImageFile()
                } catch (ex: IOException) {
                    ex.printStackTrace()
                }
                if (photoFile != null) {
                    fileUri = FileProvider.getUriForFile(
                        this,
                        applicationContext.packageName + ".provider",
                        photoFile
                    )
                    intent.putExtra(
                        MediaStore.EXTRA_OUTPUT,
                        fileUri
                    )
                }
            }
        }
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        if (intent.resolveActivity(applicationContext.packageManager) != null) {
            launcherCameraResults.launch(intent)
        }
    }

    @Throws(IOException::class)
    private fun createImageFile(): File {
        val timeStamp = SimpleDateFormat(
            "yyyyMMdd_HHmmss",
            Locale.getDefault()
        ).format(Date())
        val imageFileName = "IMG_" + timeStamp + "_"
        val storageDir =
            getExternalFilesDir(Environment.DIRECTORY_PICTURES)

        return File.createTempFile(
            imageFileName,  /* prefix */
            ".png",  /* suffix */
            storageDir /* directory */
        )
    }


    private fun showHowItWorksDialog() {
        val dialog = Dialog(this, R.style.Theme_Dialog)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(android.graphics.Color.TRANSPARENT))
        dialog.window!!
            .setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT)
        dialog.setContentView(R.layout.dialog_how_it_works)
        dialog.setCancelable(false)
        val tvHeader = dialog.findViewById<TextView>(R.id.tv_header)
        val imgClose = dialog.findViewById<ImageView>(R.id.img_close)
        val rvHowItWorks = dialog.findViewById<RecyclerView>(R.id.rv_how_it_works)
        upiHowItWorksAdapter = UPIHowItWorksAdapter(upiHowItWorksList)
        val mLinerLayout = LinearLayoutManager(this)
        rvHowItWorks.layoutManager = mLinerLayout
        rvHowItWorks.itemAnimator = DefaultItemAnimator()
        rvHowItWorks.adapter = upiHowItWorksAdapter
        imgClose.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
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
        val builder = AlertDialog.Builder(this@UPIPaymentSetUpScreen)
        builder.setTitle(title)
        builder.setCancelable(false)
        val customLayout = LayoutInflater.from(this@UPIPaymentSetUpScreen)
            .inflate(R.layout.custom_progrss_bar, null)
        builder.setView(customLayout)
        dialog = builder.create()
        val tvBody = customLayout.findViewById<TextView>(R.id.tv_value)
        tvBody.text = progressVal
        dialog.show()
    }

}