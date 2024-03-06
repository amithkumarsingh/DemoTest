package com.whitecoats.clinicplus.activities

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.app.ProgressDialog
import android.os.Bundle
import android.text.Editable
import android.text.Html
import android.text.TextWatcher
import android.util.Patterns
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModelProvider
import com.whitecoats.clinicplus.R
import com.whitecoats.clinicplus.SettingsActivity
import com.whitecoats.clinicplus.utils.AppUtilities
import com.whitecoats.clinicplus.utils.ErrorHandlerClass.errorHandler
import com.whitecoats.clinicplus.viewmodels.VideoToolSettingViewModel
import org.json.JSONException
import org.json.JSONObject
import java.util.*

class VideoToolSettingActivity : AppCompatActivity() {
    private var toolbar: Toolbar? = null
    private var videoToolPreferenceSpinner: Spinner? = null
    private var BuidInToolPreferenceLayout: RelativeLayout? = null
    private var whatAppToolPreferenceLayout: RelativeLayout? = null
    private var zoomToolPreferenceLayout: RelativeLayout? = null
    private var googlemetToolPreferenceLayout: RelativeLayout? = null
    private var otherToolPreferenceLayout: RelativeLayout? = null
    private var helpMeSetupLayout: RelativeLayout? = null
    private var videoToolSettingViewModel: VideoToolSettingViewModel? = null
    private var videoToolSettingDetails: JSONObject? = null
    private var spinnerSelection = 0
    private var howtoWork: String? = null
    private var whatsAppInfoButton: ImageView? = null
    private var zoomInfoButton: ImageView? = null
    private var googlemetInfoButton: ImageView? = null
    private var otherInfoButton: ImageView? = null
    private var tool_preference = 0
    private var tool_name: String? = null
    private var phone: String? = null
    private var webhook_url: String? = null
    private var default_join_video_url: String? = null
    private var contact_phone: String? = null

    //build in layout
    private var buildInSubmitPreferencesButtonLayout: RelativeLayout? = null

    //whatsApp
    private var whatAppNumberEditText: EditText? = null
    private var whatsAppVideoLinkEditText: EditText? = null
    private var submitPreferencesButtonLayout: RelativeLayout? = null

    //zoom
    private var zoomWebHookNumberEditText: EditText? = null
    private var zoomContactNumberEditText: EditText? = null
    private var zoomDefaultJoiningVideoUrlEditText: EditText? = null
    private var zoomSubmitPreferencesButtonLayout: RelativeLayout? = null

    //googleMeet
    private var googlemetWebHookNumberEditText: EditText? = null
    private var googlemetContactNumberEditText: EditText? = null
    private var googlemetDefaultJoiningVideoUrlEditText: EditText? = null
    private var googlemetSubmitPreferencesButtonLayout: RelativeLayout? = null

    //other
    private var otherWebHookNumberEditText: EditText? = null
    private var otherToolNameEditText: EditText? = null
    private var otherContactNumberEditText: EditText? = null
    private var otherDefaultJoiningVideoUrlEditText: EditText? = null
    private var otherSubmitPreferencesButtonLayout: RelativeLayout? = null
    private var otpLoading: ProgressDialog? = null
    var videoToolPreferenceArray: MutableList<String>? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video_tool_setting)
        videoToolSettingViewModel = ViewModelProvider(this)[VideoToolSettingViewModel::class.java]
        videoToolSettingViewModel!!.init()

        // Spinner Drop down elements
        videoToolPreferenceArray = ArrayList()
        videoToolSettingDetails = SettingsActivity.videoToolSettingDetailsObject
        buildInSubmitPreferencesButtonLayout =
            findViewById(R.id.buildInSubmitPreferencesButtonLayout)

        //whatsApp
        whatAppNumberEditText = findViewById(R.id.whatAppNumberEditText)
        whatsAppVideoLinkEditText = findViewById(R.id.whatsAppVideoLinkEditText)
        submitPreferencesButtonLayout = findViewById(R.id.submitPreferencesButtonLayout)
        whatAppNumberEditText?.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {}
            override fun beforeTextChanged(
                s: CharSequence, start: Int,
                count: Int, after: Int
            ) {
            }

            override fun onTextChanged(
                s: CharSequence, start: Int,
                before: Int, count: Int
            ) {
                if (s.length == 10) whatsAppVideoLinkEditText?.setText("https://wa.me/91$s")
            }
        })


        //zoom
        zoomWebHookNumberEditText = findViewById(R.id.zoomWebHookNumberEditText)
        zoomContactNumberEditText = findViewById(R.id.zoomContactNumberEditText)
        zoomDefaultJoiningVideoUrlEditText = findViewById(R.id.zoomDefaultJoiningVideoUrlEditText)
        zoomSubmitPreferencesButtonLayout = findViewById(R.id.zoomSubmitPreferencesButtonLayout)

        //googleMeet
        googlemetWebHookNumberEditText = findViewById(R.id.googlemetWebHookNumberEditText)
        googlemetContactNumberEditText = findViewById(R.id.googlemetContactNumberEditText)
        googlemetDefaultJoiningVideoUrlEditText =
            findViewById(R.id.googlemetDefaultJoiningVideoUrlEditText)
        googlemetSubmitPreferencesButtonLayout =
            findViewById(R.id.googlemetSubmitPreferencesButtonLayout)

        //other
        otherWebHookNumberEditText = findViewById(R.id.otherWebHookNumberEditText)
        otherToolNameEditText = findViewById(R.id.otherToolNameEditText)
        otherContactNumberEditText = findViewById(R.id.otherContactNumberEditText)
        otherDefaultJoiningVideoUrlEditText = findViewById(R.id.otherDefaultJoiningVideoUrlEditText)
        otherSubmitPreferencesButtonLayout = findViewById(R.id.otherSubmitPreferencesButtonLayout)
        try {
            if (videoToolSettingDetails!!.isNull("preference")) {
                spinnerSelection = 0
            } else {
                val toolPreferenceObject = videoToolSettingDetails!!.getJSONObject("preference")
                if (toolPreferenceObject.getInt("tool_preference") == 1) {
                    spinnerSelection = 0
                } else if (toolPreferenceObject.getInt("tool_preference") == 2) {
                    spinnerSelection = 1
                } else if (toolPreferenceObject.getInt("tool_preference") == 3) {
                    spinnerSelection = 2
                } else if (toolPreferenceObject.getInt("tool_preference") == 4) {
                    spinnerSelection = 3
                } else if (toolPreferenceObject.getInt("tool_preference") == 5) {
                    spinnerSelection = 4
                }
                val allVideoToolDetails =
                    videoToolSettingDetails!!.getJSONArray("all_video_tools_details")

                for (k in 0 until allVideoToolDetails.length()) {
                    val supportedVideoTool = allVideoToolDetails.getJSONObject(k)
                    (videoToolPreferenceArray as ArrayList<String>).add(
                        supportedVideoTool.getString(
                            "name"
                        )
                    )
                }
                if (allVideoToolDetails.length() > 0) {
                    val allVideoToolDetailsObject: JSONObject
                    if (spinnerSelection == 0) {
                        allVideoToolDetailsObject = allVideoToolDetails.getJSONObject(0)
                        howtoWork = allVideoToolDetailsObject.getString("how_it_works")
                    } else if (spinnerSelection == 1) {
                        allVideoToolDetailsObject = allVideoToolDetails.getJSONObject(1)
                        howtoWork = allVideoToolDetailsObject.getString("how_it_works")
                    } else if (spinnerSelection == 2) {
                        allVideoToolDetailsObject = allVideoToolDetails.getJSONObject(2)
                        howtoWork = allVideoToolDetailsObject.getString("how_it_works")
                    } else if (spinnerSelection == 3) {
                        allVideoToolDetailsObject = allVideoToolDetails.getJSONObject(3)
                        howtoWork = allVideoToolDetailsObject.getString("how_it_works")
                    } else if (spinnerSelection == 4) {
                        allVideoToolDetailsObject = allVideoToolDetails.getJSONObject(4)
                        howtoWork = allVideoToolDetailsObject.getString("how_it_works")
                    }
                }
                tool_preference = toolPreferenceObject.getInt("tool_preference")
                tool_name = toolPreferenceObject.getString("tool_name")
                phone = toolPreferenceObject.getString("phone")
                webhook_url = toolPreferenceObject.getString("webhook_url")
                default_join_video_url = toolPreferenceObject.getString("default_join_video_url")
                contact_phone = toolPreferenceObject.getString("contact_phone")
                if (tool_preference == 1) {
                } else if (tool_preference == 2) {
                    whatAppNumberEditText?.setText(phone)
                    if (whatAppNumberEditText?.text?.length == 0) {
                        whatsAppVideoLinkEditText?.setText("https://wa.me/91")
                    } else {
                        whatsAppVideoLinkEditText?.setText("https://wa.me/91$phone")
                    }
                } else if (tool_preference == 3) {
                    zoomWebHookNumberEditText?.setText(webhook_url)
                    if (contact_phone.equals("null", ignoreCase = true)) {
                        zoomContactNumberEditText?.setText("")
                    } else {
                        zoomContactNumberEditText?.setText(contact_phone)
                    }
                    zoomDefaultJoiningVideoUrlEditText?.setText(default_join_video_url)
                } else if (tool_preference == 4) {
                    googlemetWebHookNumberEditText?.setText(webhook_url)
                    if (contact_phone.equals("null", ignoreCase = true)) {
                        googlemetContactNumberEditText?.setText("")
                    } else {
                        googlemetContactNumberEditText?.setText(contact_phone)
                    }
                    googlemetDefaultJoiningVideoUrlEditText?.setText(default_join_video_url)
                } else if (tool_preference == 5) {
                    otherWebHookNumberEditText?.setText(webhook_url)
                    otherToolNameEditText?.setText(tool_name)
                    if (contact_phone.equals("null", ignoreCase = true)) {
                        otherContactNumberEditText?.setText("")
                    } else {
                        otherContactNumberEditText?.setText(contact_phone)
                    }
                    otherDefaultJoiningVideoUrlEditText?.setText(default_join_video_url)
                }
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        buildInSubmitPreferencesButtonLayout?.setOnClickListener { saveVideoPreferenceDetails() }
        submitPreferencesButtonLayout?.setOnClickListener { saveVideoPreferenceDetails() }
        zoomSubmitPreferencesButtonLayout?.setOnClickListener { saveVideoPreferenceDetails() }
        googlemetSubmitPreferencesButtonLayout?.setOnClickListener { saveVideoPreferenceDetails() }
        otherSubmitPreferencesButtonLayout?.setOnClickListener { saveVideoPreferenceDetails() }
        initView()
    }

    private fun initView() {
        toolbar = findViewById(R.id.videoToolSettingToolbar)

        //setting up back button on toolbar
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        val upArrow = AppUtilities.changeIconColor(
            resources.getDrawable(R.drawable.ic_arrow_back, theme), this, R.color.colorWhite
        )
        supportActionBar!!.setHomeAsUpIndicator(upArrow)
        videoToolPreferenceSpinner = findViewById(R.id.videoToolPreferenceSpinner)
        BuidInToolPreferenceLayout = findViewById(R.id.BuidInToolPreferenceLayout)
        whatAppToolPreferenceLayout = findViewById(R.id.whatAppToolPreferenceLayout)
        zoomToolPreferenceLayout = findViewById(R.id.zoomToolPreferenceLayout)
        googlemetToolPreferenceLayout = findViewById(R.id.googlemetToolPreferenceLayout)
        otherToolPreferenceLayout = findViewById(R.id.otherToolPreferenceLayout)
        helpMeSetupLayout = findViewById(R.id.helpMeSetupLayout)
        whatsAppInfoButton = findViewById(R.id.whatsAppInfoButton)
        zoomInfoButton = findViewById(R.id.zoomInfoButton)
        googlemetInfoButton = findViewById(R.id.googlemetInfoButton)
        otherInfoButton = findViewById(R.id.otherInfoButton)

        // Creating adapter for spinner
        val dataAdapter =
            videoToolPreferenceArray?.let {
                ArrayAdapter(
                    this,
                    android.R.layout.simple_spinner_item,
                    it.toList()
                )
            }

        // Drop down layout style - list view with radio button
        dataAdapter?.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        // attaching data adapter to spinner
        videoToolPreferenceSpinner?.adapter = dataAdapter
        videoToolPreferenceSpinner?.setSelection(spinnerSelection)
        videoToolPreferenceSpinner?.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>?, view: View, i: Int, l: Long) {
                val spinnerSelectedValue = videoToolPreferenceSpinner!!.selectedItem.toString()
                try {
                    val allVideoToolDetails =
                        videoToolSettingDetails!!.getJSONArray("all_video_tools_details")
                    if (allVideoToolDetails.length() > 0) {
                        val allVideoToolDetailsObject: JSONObject
                        if (videoToolPreferenceSpinner!!.selectedItemPosition == 0) {
                            allVideoToolDetailsObject = allVideoToolDetails.getJSONObject(0)
                            howtoWork = allVideoToolDetailsObject.getString("how_it_works")
                        } else if (videoToolPreferenceSpinner!!.selectedItemPosition == 1) {
                            allVideoToolDetailsObject = allVideoToolDetails.getJSONObject(1)
                            howtoWork = allVideoToolDetailsObject.getString("how_it_works")
                        } else if (videoToolPreferenceSpinner!!.selectedItemPosition == 2) {
                            allVideoToolDetailsObject = allVideoToolDetails.getJSONObject(2)
                            howtoWork = allVideoToolDetailsObject.getString("how_it_works")
                        } else if (videoToolPreferenceSpinner!!.selectedItemPosition == 3) {
                            allVideoToolDetailsObject = allVideoToolDetails.getJSONObject(3)
                            howtoWork = allVideoToolDetailsObject.getString("how_it_works")
                        } else if (videoToolPreferenceSpinner!!.selectedItemPosition == 4) {
                            allVideoToolDetailsObject = allVideoToolDetails.getJSONObject(4)
                            howtoWork = allVideoToolDetailsObject.getString("how_it_works")
                        }
                    }
                } catch (e: JSONException) {
                }
                if (spinnerSelectedValue.equals("WhiteCoats Built-In", ignoreCase = true)) {
                    BuidInToolPreferenceLayout?.visibility = View.VISIBLE
                    whatAppToolPreferenceLayout?.visibility = View.GONE
                    zoomToolPreferenceLayout?.visibility = View.GONE
                    googlemetToolPreferenceLayout?.visibility = View.GONE
                    otherToolPreferenceLayout?.visibility = View.GONE
                } else if (spinnerSelectedValue.equals("WhatsApp", ignoreCase = true)) {
                    BuidInToolPreferenceLayout?.visibility = View.GONE
                    whatAppToolPreferenceLayout?.visibility = View.VISIBLE
                    zoomToolPreferenceLayout?.visibility = View.GONE
                    googlemetToolPreferenceLayout?.visibility = View.GONE
                    otherToolPreferenceLayout?.visibility = View.GONE
                } else if (spinnerSelectedValue.equals("Zoom", ignoreCase = true)) {
                    BuidInToolPreferenceLayout?.visibility = View.GONE
                    whatAppToolPreferenceLayout?.visibility = View.GONE
                    zoomToolPreferenceLayout?.visibility = View.VISIBLE
                    googlemetToolPreferenceLayout?.visibility = View.GONE
                    otherToolPreferenceLayout?.visibility = View.GONE
                } else if (spinnerSelectedValue.equals("Google Meet", ignoreCase = true)) {
                    BuidInToolPreferenceLayout?.visibility = View.GONE
                    googlemetToolPreferenceLayout?.visibility = View.VISIBLE
                    whatAppToolPreferenceLayout?.visibility = View.GONE
                    zoomToolPreferenceLayout?.visibility = View.GONE
                    otherToolPreferenceLayout?.visibility = View.GONE
                } else if (spinnerSelectedValue.equals("Other", ignoreCase = true)) {
                    BuidInToolPreferenceLayout?.visibility = View.GONE
                    whatAppToolPreferenceLayout?.visibility = View.GONE
                    zoomToolPreferenceLayout?.visibility = View.GONE
                    googlemetToolPreferenceLayout?.visibility = View.GONE
                    otherToolPreferenceLayout?.visibility = View.VISIBLE
                } else {
                    BuidInToolPreferenceLayout?.visibility = View.GONE
                    whatAppToolPreferenceLayout?.visibility = View.GONE
                    zoomToolPreferenceLayout?.visibility = View.GONE
                    googlemetToolPreferenceLayout?.visibility = View.GONE
                    otherToolPreferenceLayout?.visibility = View.GONE
                }
                if (tool_preference == 1) {
                } else if (tool_preference == 2) {
                    whatAppNumberEditText!!.setText(phone)
                    if (whatAppNumberEditText!!.text.isEmpty()) {
                        whatsAppVideoLinkEditText!!.setText("https://wa.me/91")
                    } else {
                        whatsAppVideoLinkEditText!!.setText("https://wa.me/91$phone")
                    }
                } else if (tool_preference == 3) {
                    zoomWebHookNumberEditText!!.setText(webhook_url)
                    zoomContactNumberEditText!!.setText(contact_phone)
                    zoomDefaultJoiningVideoUrlEditText!!.setText(default_join_video_url)
                } else if (tool_preference == 4) {
                    googlemetWebHookNumberEditText!!.setText(webhook_url)
                    googlemetContactNumberEditText!!.setText(contact_phone)
                    googlemetDefaultJoiningVideoUrlEditText!!.setText(default_join_video_url)
                } else if (tool_preference == 5) {
                    otherWebHookNumberEditText!!.setText(webhook_url)
                    otherToolNameEditText!!.setText(tool_name)
                    otherContactNumberEditText!!.setText(contact_phone)
                    otherDefaultJoiningVideoUrlEditText!!.setText(default_join_video_url)
                }
            }

            override fun onNothingSelected(adapterView: AdapterView<*>?) {}
        }
        helpMeSetupLayout?.setOnClickListener { videoToolSetupHelp }
        whatsAppInfoButton?.setOnClickListener {
            howToWorkInfo(
                this@VideoToolSettingActivity,
                howtoWork
            )
        }
        zoomInfoButton?.setOnClickListener {
            howToWorkInfo(
                this@VideoToolSettingActivity,
                howtoWork
            )
        }
        googlemetInfoButton?.setOnClickListener {
            howToWorkInfo(
                this@VideoToolSettingActivity,
                howtoWork
            )
        }
        otherInfoButton?.setOnClickListener {
            howToWorkInfo(
                this@VideoToolSettingActivity,
                howtoWork
            )
        }
    }

    override fun onBackPressed() {
        super.getOnBackPressedDispatcher().onBackPressed()
        finish()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }


    val videoToolSetupHelp: Unit
        get() {
            val loadingDialog = ProgressDialog(this)
            loadingDialog.setMessage(resources.getString(R.string.loading_data))
            loadingDialog.setTitle(resources.getString(R.string.please_wait))
            loadingDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER)
            loadingDialog.setCancelable(false)
            loadingDialog.show()
            videoToolSettingViewModel!!.getVideoToolSetupHelp(this)
                .observe(this) { value ->
                    loadingDialog.dismiss()
                    try {
                        val response = JSONObject(value)
                        if (response.getInt("status_code") == 200) {
                            helpMeSetupDialog(this@VideoToolSettingActivity)
                        } else {
                            if (value != null) {
                                errorHandler(this@VideoToolSettingActivity, value)
                            }
                        }
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                }
        }

    //diagnosis help dialog
    fun howToWorkInfo(activity: Activity, howItWork: String?) {
        val viewGroup = activity.findViewById<ViewGroup>(android.R.id.content)
        val dialogView = LayoutInflater.from(activity)
            .inflate(R.layout.video_tool_how_to_work_dialog, viewGroup, false)
        val howItWorkText = dialogView.findViewById<TextView>(R.id.howItWorkText)
        val yes = dialogView.findViewById<RelativeLayout>(R.id.yes)
        val closeButton = dialogView.findViewById<ImageButton>(R.id.closeButton)
        howItWorkText.text = Html.fromHtml(howItWork)
        val optionDialog = AlertDialog.Builder(this).create()
        optionDialog.setCancelable(false)
        yes.setOnClickListener { optionDialog.dismiss() }
        closeButton.setOnClickListener { optionDialog.dismiss() }

        optionDialog.setView(dialogView)
        if (!isFinishing) {
            optionDialog.show()
        }
    }

    //help me setup dilog
    fun helpMeSetupDialog(activity: Activity?) {
        val dialog = Dialog(this@VideoToolSettingActivity)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.video_tool_helpme_setup_dialog)
        val dailogArticleCancel = dialog.findViewById<View>(R.id.dailogArticleCancel) as ImageView
        val closeButton = dialog.findViewById<View>(R.id.closeButton) as RelativeLayout
        closeButton.setOnClickListener { dialog.dismiss() }
        dailogArticleCancel.setOnClickListener { dialog.dismiss() }
        dialog.show()
    }

    //save video prefrences
    fun saveVideoPreferenceDetails() {
        var jsonValue: JSONObject
        try {
            jsonValue = JSONObject()
            if (videoToolPreferenceSpinner!!.selectedItem.toString()
                    .equals("WhiteCoats Built-In", ignoreCase = true)
            ) {
                jsonValue.put("tool_preference", 1)
                jsonValue.put("tool_name", "")
                jsonValue.put("phone", "")
                jsonValue.put("webhook_url", "")
                jsonValue.put("default_join_video_url", "")
                jsonValue.put("contact_phone", "")
                setPreferenceApi(jsonValue)
            } else if (videoToolPreferenceSpinner!!.selectedItem.toString()
                    .equals("WhatsApp", ignoreCase = true)
            ) {
                if (whatAppNumberEditText!!.text.length == 10) {
                    jsonValue.put("tool_preference", 2)
                    jsonValue.put("tool_name", "")
                    jsonValue.put(
                        "phone",
                        whatAppNumberEditText!!.text.toString().trim { it <= ' ' })
                    jsonValue.put("webhook_url", "")
                    jsonValue.put(
                        "default_join_video_url",
                        whatsAppVideoLinkEditText!!.text.toString()
                    )
                    jsonValue.put("contact_phone", "")
                    setPreferenceApi(jsonValue)
                } else {
                    Toast.makeText(
                        this@VideoToolSettingActivity,
                        "Please enter valid whatsApp number",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } else if (videoToolPreferenceSpinner!!.selectedItem.toString()
                    .equals("Zoom", ignoreCase = true)
            ) {
                if (zoomWebHookNumberEditText!!.text.length > 0) {
                    if (zoomContactNumberEditText!!.text.length > 0) {
                        if (zoomContactNumberEditText!!.text.length == 10) {
                            jsonValue.put("tool_preference", 3)
                            jsonValue.put("tool_name", "")
                            jsonValue.put("phone", "")
                            jsonValue.put(
                                "webhook_url",
                                zoomWebHookNumberEditText!!.text.toString().trim { it <= ' ' })
                            jsonValue.put(
                                "default_join_video_url",
                                zoomDefaultJoiningVideoUrlEditText!!.text.toString()
                                    .trim { it <= ' ' })
                            jsonValue.put(
                                "contact_phone",
                                zoomContactNumberEditText!!.text.toString().trim { it <= ' ' })
                            setPreferenceApi(jsonValue)
                        } else {
                            Toast.makeText(
                                this@VideoToolSettingActivity,
                                "Please enter 10 digit contact number",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } else {
                        jsonValue.put("tool_preference", 3)
                        jsonValue.put("tool_name", "")
                        jsonValue.put("phone", "")
                        jsonValue.put(
                            "webhook_url",
                            zoomWebHookNumberEditText!!.text.toString().trim { it <= ' ' })
                        jsonValue.put(
                            "default_join_video_url",
                            zoomDefaultJoiningVideoUrlEditText!!.text.toString().trim { it <= ' ' })
                        jsonValue.put(
                            "contact_phone",
                            zoomContactNumberEditText!!.text.toString().trim { it <= ' ' })
                        setPreferenceApi(jsonValue)
                    }
                } else {
                    Toast.makeText(
                        this@VideoToolSettingActivity,
                        "Please enter valid Webhook URL",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } else if (videoToolPreferenceSpinner!!.selectedItem.toString()
                    .equals("Google Meet", ignoreCase = true)
            ) {
                if (googlemetWebHookNumberEditText!!.text.isNotEmpty()) {
                    if (googlemetContactNumberEditText!!.text.isNotEmpty()) {
                        if (googlemetContactNumberEditText!!.text.length == 10) {
                            jsonValue.put("tool_preference", 4)
                            jsonValue.put("tool_name", "")
                            jsonValue.put("phone", "")
                            jsonValue.put(
                                "webhook_url",
                                googlemetWebHookNumberEditText!!.text.toString().trim { it <= ' ' })
                            jsonValue.put(
                                "default_join_video_url",
                                googlemetDefaultJoiningVideoUrlEditText!!.text.toString()
                                    .trim { it <= ' ' })
                            jsonValue.put(
                                "contact_phone",
                                googlemetContactNumberEditText!!.text.toString().trim { it <= ' ' })
                            setPreferenceApi(jsonValue)
                        } else {
                            Toast.makeText(
                                this@VideoToolSettingActivity,
                                "Please enter 10 digit contact number",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } else {
                        jsonValue.put("tool_preference", 4)
                        jsonValue.put("tool_name", "")
                        jsonValue.put("phone", "")
                        jsonValue.put(
                            "webhook_url",
                            googlemetWebHookNumberEditText!!.text.toString().trim { it <= ' ' })
                        jsonValue.put(
                            "default_join_video_url",
                            googlemetDefaultJoiningVideoUrlEditText!!.text.toString()
                                .trim { it <= ' ' })
                        jsonValue.put(
                            "contact_phone",
                            googlemetContactNumberEditText!!.text.toString().trim { it <= ' ' })
                        setPreferenceApi(jsonValue)
                    }
                } else {
                    Toast.makeText(
                        this@VideoToolSettingActivity,
                        "Please enter valid Webhook URL",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } else if (videoToolPreferenceSpinner!!.selectedItem.toString()
                    .equals("Other", ignoreCase = true)
            ) {
                if (otherWebHookNumberEditText!!.text.isNotEmpty()) {
                    if (otherToolNameEditText!!.text.isNotEmpty()) {
                        if (otherContactNumberEditText!!.text.isNotEmpty()) {
                            if (otherContactNumberEditText!!.text.length == 10) {
                                jsonValue.put("tool_preference", 5)
                                jsonValue.put(
                                    "tool_name",
                                    otherToolNameEditText!!.text.toString().trim { it <= ' ' })
                                jsonValue.put("phone", "")
                                jsonValue.put(
                                    "webhook_url",
                                    otherWebHookNumberEditText!!.text.toString().trim { it <= ' ' })
                                jsonValue.put(
                                    "default_join_video_url",
                                    otherDefaultJoiningVideoUrlEditText!!.text.toString()
                                        .trim { it <= ' ' })
                                jsonValue.put(
                                    "contact_phone",
                                    otherContactNumberEditText!!.text.toString().trim { it <= ' ' })
                                setPreferenceApi(jsonValue)
                            } else {
                                Toast.makeText(
                                    this@VideoToolSettingActivity,
                                    "Please enter 10 digit contact number",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        } else {
                            jsonValue.put("tool_preference", 5)
                            jsonValue.put(
                                "tool_name",
                                otherToolNameEditText!!.text.toString().trim { it <= ' ' })
                            jsonValue.put("phone", "")
                            jsonValue.put(
                                "webhook_url",
                                otherWebHookNumberEditText!!.text.toString().trim { it <= ' ' })
                            jsonValue.put(
                                "default_join_video_url",
                                otherDefaultJoiningVideoUrlEditText!!.text.toString()
                                    .trim { it <= ' ' })
                            jsonValue.put(
                                "contact_phone",
                                otherContactNumberEditText!!.text.toString().trim { it <= ' ' })
                            setPreferenceApi(jsonValue)
                        }
                    } else {
                        Toast.makeText(
                            this@VideoToolSettingActivity,
                            "Please enter tool name",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    Toast.makeText(
                        this@VideoToolSettingActivity,
                        "Please enter valid Webhook URL",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        } catch (e: Exception) {
            e.message
        }
    }

    fun setPreferenceApi(jsonValue: JSONObject?) {
        otpLoading = ProgressDialog(this@VideoToolSettingActivity)
        otpLoading!!.setMessage(resources.getString(R.string.wait_while_we_updating))
        otpLoading!!.setTitle(resources.getString(R.string.updating))
        otpLoading!!.setProgressStyle(ProgressDialog.STYLE_SPINNER)
        otpLoading!!.setCancelable(false)
        otpLoading!!.show()
        videoToolSettingViewModel!!.saveVideoToolPre(this@VideoToolSettingActivity, jsonValue)
            .observe(this@VideoToolSettingActivity
            ) { value ->
                otpLoading!!.dismiss()
                try {
                    val jsonObject = JSONObject(value)
                    if (jsonObject.getInt("status_code") == 200) {
                        Toast.makeText(
                            this@VideoToolSettingActivity,
                            "External video tool configured successfully",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        if (value != null) {
                            errorHandler(this@VideoToolSettingActivity, value)
                        }
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }
    }

    /**
     * This is used to check the given URL is valid or not.
     *
     * @param url
     * @return true if url is valid, false otherwise.
     */
    private fun isValidUrl(url: String): Boolean {
        val p = Patterns.WEB_URL
        val m = p.matcher(url.lowercase(Locale.getDefault()))
        return m.matches()
    }
}