package com.whitecoats.clinicplus

import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.PorterDuff
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import co.mobiwise.materialintro.shape.Focus
import co.mobiwise.materialintro.shape.FocusGravity
import co.mobiwise.materialintro.shape.ShapeType
import co.mobiwise.materialintro.view.MaterialIntroView
import com.android.volley.Request.Method
import com.whitecoats.adapter.ApptTimeSlotGridViewCustomAdapter
import com.whitecoats.clinicplus.apis.ApiGetPostMethodCalls
import com.whitecoats.clinicplus.apis.ApiUrls
import com.whitecoats.clinicplus.autofollowup.AutoFollowUpActivity
import com.whitecoats.clinicplus.fragments.DashboardFullMode
import com.whitecoats.clinicplus.utils.ErrorHandlerClass.errorHandler
import com.whitecoats.clinicplus.viewmodels.CommonViewModel
import com.whitecoats.clinicplus.viewmodels.SettingViewModel
import com.whitecoats.model.AppointmentSlotListModel
import com.whitecoats.model.AppointmentSlotSpinnerModel
import com.zoho.salesiqembed.ZohoSalesIQ
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class BookAppointmentTimeSlotActivity : AppCompatActivity() {
    private var timeSlotListAdapter: ApptTimeSlotGridViewCustomAdapter? = null
    private var doctorServiceArrayList: MutableList<AppointmentSlotSpinnerModel>? = null
    private lateinit var dateSelectionLayout: RelativeLayout
    private lateinit var dateTextView: TextView
    private var mYear = 0
    private var mMonth = 0
    private var mDay = 0
    private lateinit var gridview: GridView
    private lateinit var appointmentServiceSpinner: Spinner
    private var dateStr = ""
    private var slotServiceName = ""
    private var slotSaasID = 0
    private var slotServicePrice = 0
    private var slotServiceId = 0
    private var slotProdID = 0
    private var patientId = 0
    private var quickApptFlag = 0
    private var serviceID = 0
    private var spinnerSelectedPosition = 0
    private var progressBar: ProgressBar? = null
    private var patientName: String? = null
    private var isClinicServiceAvailable = 0
    private var isVideoServiceAvailable = 0
    private var appPreference: SharedPreferences? = null
    private lateinit var serviceSelectGuidePt: RelativeLayout
    private lateinit var llNoSlotsAvailable: LinearLayout
    private var slotTimeArr: ArrayList<AppointmentSlotListModel>? = null
    private var slotEndTime: ArrayList<AppointmentSlotListModel>? = null
    private var findEarlierSlotsAvailableDate = false
    private val timeFormatSinnerArray = arrayOf("12 Hrs Format", "24 Hrs Format")
    private var flagFirst = 0
    private var timeFormat = 0
    private var sharedPreferencesTimeFormat: SharedPreferences? = null
    private var settingViewModel: SettingViewModel? = null
    private var activity: Activity? = null
    private var globalApiCall: ApiGetPostMethodCalls? = null
    private lateinit var commonViewModel: CommonViewModel

    private lateinit var bookEmptyText: TextView
    private lateinit var bookEmptyText1: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_book_appointment_slot_time)
        val toolbar = findViewById<Toolbar>(R.id.blockTimeToolbar)
        toolbar.title = "Location & Time"
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        commonViewModel =
            ViewModelProvider(this@BookAppointmentTimeSlotActivity)[CommonViewModel::class.java]
        @SuppressLint("UseCompatLoadingForDrawables") val upArrow =
            getDrawable(R.drawable.ic_arrow_back)
        upArrow!!.setColorFilter(
            ContextCompat.getColor(this, R.color.colorWhite), PorterDuff.Mode.SRC_ATOP
        )
        supportActionBar!!.setHomeAsUpIndicator(upArrow)
        activity = this
        doctorServiceArrayList = ArrayList()
        globalApiCall = ApiGetPostMethodCalls()
        slotTimeArr = ArrayList()
        slotEndTime = ArrayList()
        progressBar = findViewById(R.id.bookLoading)
        llNoSlotsAvailable = findViewById(R.id.ll_No_Slots_Available)
        bookEmptyText = findViewById(R.id.bookEmptyText)
        bookEmptyText1 = findViewById(R.id.bookEmptyText1)
        val appointmentBookNow = findViewById<Button>(R.id.appointmentBookNow)
        appPreference = getSharedPreferences(ApiUrls.appSharedPref, MODE_PRIVATE)
        serviceSelectGuidePt = findViewById(R.id.appointmentSlotSpinnerLayout)
        val timeFormatSpinner = findViewById<Spinner>(R.id.timeFormatSpinner)
        val appUtilities = AppUtilities()
        sharedPreferencesTimeFormat =
            applicationContext.getSharedPreferences("TimeFormatPreference", MODE_PRIVATE)
        ZohoSalesIQ.Tracking.setPageTitle("On Book Appt Slots Page")
        settingViewModel = ViewModelProvider(this)[SettingViewModel::class.java]
        settingViewModel!!.init()
        showGuide(1)
        val patientAgeTypeAdapter =
            ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, timeFormatSinnerArray)
        patientAgeTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        timeFormatSpinner.adapter = patientAgeTypeAdapter
        if (appUtilities.timeFormatPreferences(applicationContext) == 12) {
            timeFormatSpinner.setSelection(0)
        } else {
            timeFormatSpinner.setSelection(1)
        }
        flagFirst = 0
        timeFormatSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?, view: View, position: Int, id: Long
            ) {
                if (flagFirst == 0) {
                    flagFirst = 1
                } else {
                    timeFormat = if (position == 0) {
                        12
                    } else {
                        24
                    }
                    saveTimeFormat(timeFormat)
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
        try {
            serviceID = intent.getIntExtra("serviceId", 0)
            val doctorDetailsObjects =
                JSONObject(intent.getStringExtra("doctorDetailsRootObjects")!!)
            val userObject = doctorDetailsObjects.getJSONObject("user")
            val serviceArray = userObject.getJSONArray("services")
            for (j in 0 until serviceArray.length()) {
                val serviceObject = serviceArray.getJSONObject(j)
                val serviceObjectPivotId = serviceObject.getJSONObject("pivot")
                if (serviceObject.getInt("id") == 3) {
                    isClinicServiceAvailable = 1
                    val intervention = doctorDetailsObjects["clinic_product"]
                    if (intervention is JSONArray) {
                        // It's an array
                        val clinicProductArray = doctorDetailsObjects.getJSONArray("clinic_product")
                        val clinicSerSize = clinicProductArray.length()
                        var bookAppointmentSpinnerModel: AppointmentSlotSpinnerModel? = null
                        for (i in 0 until clinicSerSize) {
                            val clinicID =
                                clinicProductArray.getJSONObject(i).getJSONObject("prod_service")
                                    .getInt("id")
                            if (clinicID == serviceObjectPivotId.getInt("id")) {
                                val clinicProductObject = clinicProductArray.getJSONObject(i)
                                bookAppointmentSpinnerModel = AppointmentSlotSpinnerModel()
                                bookAppointmentSpinnerModel.appointmentServiceID =
                                    serviceObject.getInt("id")
                                bookAppointmentSpinnerModel.appointmentServiceName =
                                    serviceObject.getString("service")
                                bookAppointmentSpinnerModel.pivotId =
                                    serviceObjectPivotId.getInt("id")
                                bookAppointmentSpinnerModel.orderByAlphaBet = "B"
                                bookAppointmentSpinnerModel.price =
                                    clinicProductObject.getInt("price")
                                bookAppointmentSpinnerModel.productId =
                                    clinicProductObject.getInt("id")
                                val ProductServiceObject =
                                    clinicProductObject.getJSONObject("prod_service")
                                bookAppointmentSpinnerModel.internalSuperSaasId =
                                    ProductServiceObject.getInt("internal_supersaas_id")
                                bookAppointmentSpinnerModel.address =
                                    ProductServiceObject.getString("address")
                                bookAppointmentSpinnerModel.appointmentServiceAlias =
                                    ProductServiceObject.getString("alias")
                            }
                        }
                        doctorServiceArrayList!!.add(bookAppointmentSpinnerModel!!)
                    }
                }
                if (serviceObject.getInt("id") == 1) {
                    isVideoServiceAvailable = 1
                    val intervention = doctorDetailsObjects["video_product"]
                    if (intervention is JSONObject) {
                        // It's an object
                        val videoProductObject = doctorDetailsObjects.getJSONObject("video_product")
                        val bookAppointmentSpinnerModel = AppointmentSlotSpinnerModel()
                        bookAppointmentSpinnerModel.appointmentServiceID =
                            serviceObject.getInt("id")
                        bookAppointmentSpinnerModel.appointmentServiceName =
                            serviceObject.getString("service")
                        //bookAppointmentSpinnerModel.setAppointmentServiceAlias(serviceObject.getString("alias"));
                        bookAppointmentSpinnerModel.pivotId = serviceObjectPivotId.getInt("id")
                        bookAppointmentSpinnerModel.orderByAlphaBet = "A"
                        bookAppointmentSpinnerModel.price = videoProductObject.getInt("price")
                        bookAppointmentSpinnerModel.productId = videoProductObject.getInt("id")
                        val videoProductServiceObject =
                            videoProductObject.getJSONObject("prod_service")
                        if (videoProductServiceObject.getInt("id") == serviceObjectPivotId.getInt("id")) {
                            bookAppointmentSpinnerModel.internalSuperSaasId =
                                videoProductServiceObject.getInt("internal_supersaas_id")
                            bookAppointmentSpinnerModel.address =
                                videoProductServiceObject.getString("address")
                            bookAppointmentSpinnerModel.appointmentServiceAlias =
                                videoProductServiceObject.getString("alias")
                        }
                        doctorServiceArrayList!!.add(bookAppointmentSpinnerModel)
                    }
                }
            }
            doctorServiceArrayList!!.sortWith { s1: AppointmentSlotSpinnerModel?, s2: AppointmentSlotSpinnerModel? ->
                s1!!.orderByAlphaBet!!.compareTo(
                    s2!!.orderByAlphaBet!!, ignoreCase = true
                )
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        appointmentServiceSpinner = findViewById(R.id.appointmentServiceSpinner)
        dateSelectionLayout = findViewById(R.id.dateSelectionLayout)
        dateTextView = findViewById(R.id.dateTextView)
        val earlierDateTextView = findViewById<TextView>(R.id.earlierDateTextView)
        val nextDateTextView = findViewById<TextView>(R.id.nextDateTextView)
        gridview = findViewById(R.id.gridView1)
        appointmentBookNow.setOnClickListener { v: View? ->
            if (timeSlotListAdapter!!.selectedStartTime != null && !timeSlotListAdapter!!.selectedStartTime!!.isEmpty()) {
                val intent = Intent(activity, ConfirmOrderActivity::class.java)
                intent.putExtra("time_slot", timeSlotListAdapter!!.selectedStartTime)
                intent.putExtra("end_time_slot", timeSlotListAdapter!!.selectedEndTime)
                intent.putExtra("date", dateTextView.text.toString())
                intent.putExtra("price", slotServicePrice)
                intent.putExtra("slot_service", slotServiceName)
                intent.putExtra("slot_service_id", slotServiceId)
                intent.putExtra("slot_prod_id", slotProdID)
                intent.putExtra("slot_saas_id", slotSaasID)
                intent.putExtra("patientId", patientId)
                intent.putExtra("patientName", patientName)
                intent.putExtra("appointment_service_id", serviceID)
                intent.putExtra("quickAppointmentFlag", quickApptFlag)
                if (getIntent().getStringExtra("followUpApptId") != null) {
                    intent.putExtra("followUpApptId", getIntent().getStringExtra("followUpApptId"))
                }
                startActivity(intent)
            } else {
                Toast.makeText(
                    activity, resources.getString(R.string.please_select_a_slot), Toast.LENGTH_SHORT
                ).show()
            }
        }
        val calendar = Calendar.getInstance()
        val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        dateStr = format.format(Date())
        dateTextView.text = convertDateFormat(calendar)
        dateSelectionLayout.setOnClickListener {

            // Get Current Date
            val c = Calendar.getInstance()
            mYear = c[Calendar.YEAR]
            mMonth = c[Calendar.MONTH]
            mDay = c[Calendar.DAY_OF_MONTH]
            val datePickerDialog = DatePickerDialog(
                activity!!, { _: DatePicker?, year: Int, monthOfYear: Int, dayOfMonth: Int ->
                    dateTextView.text = dateFormat(
                        dayOfMonth.toString() + "/" + (monthOfYear + 1) + "/" + year, "MMM dd, yyyy"
                    )
                    val string = format.format(Date())
                    val parts =
                        string.split(" ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                    val part1 = parts[0] // 2021-06-29
                    val selectedDate = dateFormat(
                        dayOfMonth.toString() + "/" + (monthOfYear + 1) + "/" + year, "yyyy-MM-dd"
                    )
                    dateStr = if (part1.equals(selectedDate, ignoreCase = true)) {
                        format.format(Date())
                    } else {
                        dateFormat(
                            dayOfMonth.toString() + "/" + (monthOfYear + 1) + "/" + year,
                            "yyyy-MM-dd HH:mm:ss"
                        )
                    }
                    slotTimeArr!!.clear()
                    slotEndTime!!.clear()
                    timeSlotListAdapter!!.selectedSlot = ""
                    timeSlotListAdapter!!.selectedStartTime = ""
                    timeSlotListAdapter!!.notifyDataSetChanged()
                    findEarlierSlotsAvailableDate = false
                    getSlots(doctorServiceArrayList!![spinnerSelectedPosition].internalSuperSaasId)
                }, mYear, mMonth, mDay
            )
            datePickerDialog.show()
            datePickerDialog.datePicker.minDate = System.currentTimeMillis() - 1000
        }
        earlierDateTextView.setOnClickListener { v: View? ->
            val calendar1 = Calendar.getInstance()
            val format1 = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            dateStr = format1.format(calendar1.time)
            dateTextView.text = convertDateFormat(calendar1)
            slotTimeArr!!.clear()
            slotEndTime!!.clear()
            timeSlotListAdapter!!.selectedSlot = ""
            timeSlotListAdapter!!.selectedStartTime = ""
            timeSlotListAdapter!!.notifyDataSetChanged()
            findEarlierSlotsAvailableDate = true
            getSlots(doctorServiceArrayList!![spinnerSelectedPosition].internalSuperSaasId)
        }
        nextDateTextView.setOnClickListener { v: View? ->
            try {
                val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                val c = Calendar.getInstance()
                c.time = Objects.requireNonNull(sdf.parse(dateStr))
                c.add(Calendar.DATE, 1) // number of days to add
                c[Calendar.HOUR_OF_DAY] = 0
                dateStr = sdf.format(c.time) // dt is now the new date
                dateTextView.text = convertDateFormat(c)
                slotTimeArr!!.clear()
                slotEndTime!!.clear()
                timeSlotListAdapter!!.selectedSlot = ""
                timeSlotListAdapter!!.selectedStartTime = ""
                timeSlotListAdapter!!.notifyDataSetChanged()
                findEarlierSlotsAvailableDate = false
                getSlots(doctorServiceArrayList!![spinnerSelectedPosition].internalSuperSaasId)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        val adapter = ArrayAdapter(
            applicationContext,
            android.R.layout.simple_spinner_dropdown_item,
            doctorServiceArrayList!!
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        appointmentServiceSpinner.adapter = adapter
        if (DashboardFullMode.isAppointBookingOnDashBoard == 1) {
            /*Added the null check condition to avoid crash foe arraylist object and string*/
            if (doctorServiceArrayList != null && doctorServiceArrayList!!.size > 0) {
                for (i in doctorServiceArrayList!!.indices) {
                    if (doctorServiceArrayList!![i].appointmentServiceAlias != null) {
                        if (doctorServiceArrayList!![i].appointmentServiceAlias.equals(
                                DashboardFullMode.switchClinicSelectedString, ignoreCase = true
                            )
                        ) {
                            appointmentServiceSpinner.setSelection(i)
                        }
                    }
                }
            }
        } else {
            if (serviceID == 3) {
                if (isVideoServiceAvailable == 0 && isClinicServiceAvailable == 1) {
                    if (intent.getStringExtra("followUpApptId") != null) {
                        val clinicName = intent.getStringExtra("clinicName")
                        /*Added the null check condition to avoid crash foe arraylist object and string*/if (doctorServiceArrayList != null && doctorServiceArrayList!!.size > 0) {
                            for (i in doctorServiceArrayList!!.indices) {
                                if (doctorServiceArrayList!![i].appointmentServiceAlias != null) {
                                    if (doctorServiceArrayList!![i].appointmentServiceAlias.equals(
                                            clinicName, ignoreCase = true
                                        )
                                    ) {
                                        appointmentServiceSpinner.setSelection(i)
                                    }
                                }
                            }
                        }
                    } else {
                        appointmentServiceSpinner.setSelection(0)
                    }
                } else if (isVideoServiceAvailable == 1 && isClinicServiceAvailable == 1) {
                    if (intent.getStringExtra("followUpApptId") != null) {
                        val clinicName = intent.getStringExtra("clinicName")
                        /*Added the null check condition to avoid crash foe arraylist object and string*/if (doctorServiceArrayList != null && doctorServiceArrayList!!.size > 0) {
                            for (i in doctorServiceArrayList!!.indices) {
                                if (doctorServiceArrayList!![i].appointmentServiceAlias != null) {
                                    if (doctorServiceArrayList!![i].appointmentServiceAlias.equals(
                                            clinicName, ignoreCase = true
                                        )
                                    ) {
                                        appointmentServiceSpinner.setSelection(i)
                                    }
                                }
                            }
                        }
                    } else {
                        appointmentServiceSpinner.setSelection(1)
                    }
                }
            } else {
                if (isVideoServiceAvailable == 1 && isClinicServiceAvailable == 0) {
                    if (intent.getStringExtra("followUpApptId") != null) {
                        val clinicName = intent.getStringExtra("clinicName")
                        /*Added the null check condition to avoid crash foe arraylist object and string*/if (doctorServiceArrayList != null && doctorServiceArrayList!!.size > 0) {
                            for (i in doctorServiceArrayList!!.indices) {
                                if (doctorServiceArrayList!![i].appointmentServiceAlias != null) {
                                    if (doctorServiceArrayList!![i].appointmentServiceAlias.equals(
                                            clinicName, ignoreCase = true
                                        )
                                    ) {
                                        appointmentServiceSpinner.setSelection(i)
                                    }
                                }
                            }
                        }
                    } else {
                        appointmentServiceSpinner.setSelection(0)
                    }
                } else if (isVideoServiceAvailable == 1 && isClinicServiceAvailable == 1) {
                    if (intent.getStringExtra("followUpApptId") != null) {
                        val clinicName = intent.getStringExtra("clinicName")
                        /*Added the null check condition to avoid crash foe arraylist object and string*/if (doctorServiceArrayList != null && doctorServiceArrayList!!.size > 0) {
                            for (i in doctorServiceArrayList!!.indices) {
                                if (doctorServiceArrayList!![i].appointmentServiceAlias != null) {
                                    if (doctorServiceArrayList!![i].appointmentServiceAlias.equals(
                                            clinicName, ignoreCase = true
                                        )
                                    ) {
                                        appointmentServiceSpinner.setSelection(i)
                                    }
                                }
                            }
                        }
                    } else {
                        appointmentServiceSpinner.setSelection(0)
                    }
                }
            }
        }

        // Set the ClickListener for Spinner
        appointmentServiceSpinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    adapterView: AdapterView<*>?, view: View, i: Int, l: Long
                ) {
                    spinnerSelectedPosition = appointmentServiceSpinner.selectedItemPosition
                    slotSaasID =
                        doctorServiceArrayList!![spinnerSelectedPosition].internalSuperSaasId
                    slotServicePrice = doctorServiceArrayList!![spinnerSelectedPosition].price
                    slotServiceName =
                        doctorServiceArrayList!![spinnerSelectedPosition].appointmentServiceAlias!!
                    slotServiceId = doctorServiceArrayList!![spinnerSelectedPosition].pivotId
                    slotProdID = doctorServiceArrayList!![spinnerSelectedPosition].productId
                    patientId = intent.getIntExtra("patientId", 0)
                    patientName = intent.getStringExtra("patientName")
                    serviceID =
                        doctorServiceArrayList!![spinnerSelectedPosition].appointmentServiceID
                    quickApptFlag = intent.getIntExtra("quickAppointmentFlag", 0)
                    val calendar = Calendar.getInstance()
                    val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                    dateStr = format.format(Date())
                    dateTextView.text = convertDateFormat(calendar)
                    slotTimeArr!!.clear()
                    slotEndTime!!.clear()
                    timeSlotListAdapter!!.selectedSlot = ""
                    timeSlotListAdapter!!.selectedStartTime = ""
                    timeSlotListAdapter!!.notifyDataSetChanged()
                    findEarlierSlotsAvailableDate = false
                    getSlots(doctorServiceArrayList!![spinnerSelectedPosition].internalSuperSaasId)
                }

                // If no option selected
                override fun onNothingSelected(arg0: AdapterView<*>?) {
                    // TODO Auto-generated method stub
                }
            }
        timeSlotListAdapter =
            ApptTimeSlotGridViewCustomAdapter(activity!!, slotTimeArr!!, slotEndTime!!)
        gridview.adapter = timeSlotListAdapter
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // handle arrow click here
        if (item.itemId == android.R.id.home) {
            finish()
            if (AutoFollowUpActivity.autoFollowUpActivityFlag == 1) {
                AutoFollowUpActivity.autoFollowUpActivityFlag = 0
            }
        }
        return super.onOptionsItemSelected(item)
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        super.getOnBackPressedDispatcher().onBackPressed()
        finish()
    }

    private fun dateFormat(dateStr: String, outputFormat: String): String {
        var dateStr = dateStr
        return try {
            val srcDf: DateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            // parse the date string into Date object
            val date = srcDf.parse(dateStr)
            val destDf: DateFormat = SimpleDateFormat(outputFormat, Locale.getDefault())
            // format the date into another format
            if (date != null) {
                dateStr = destDf.format(date)
            }
            dateStr
        } catch (e: ParseException) {
            e.printStackTrace()
            ""
        }
    }

    fun getSlots(slotSaasId: Int) {
        progressBar!!.visibility = View.VISIBLE
        llNoSlotsAvailable.visibility = View.GONE
        val url =
            (ApiUrls.getDoctorsSlot + "?doctor_id=" + ApiUrls.doctorId + "&id=" + slotSaasId + "&from=" + dateStr.replace(
                " ",
                "%20"
            ))


        commonViewModel.commonViewModelCall(url, JSONObject(), Method.GET).observe(
            this@BookAppointmentTimeSlotActivity
        ) { result ->
            try {
                progressBar!!.visibility = View.GONE
                val responseObj = JSONObject(result)
                if (responseObj.getInt("status_code") == 200) {
                    val response = responseObj.optJSONObject("response")
//                    val rootObj = response!!.getJSONObject("response")
                    // added by dileep 26-09-2023

                    val item: Any = response.get("response")
                    if (item is JSONArray) {
                        // it's an array
                        val urlArray = item

                    } else if (item is JSONObject) {
                        val urlObject = item as JSONObject
                        // do objecty stuff with urlObject
                        val rootObj = response!!.getJSONObject("response")
                        var slotArr = rootObj.getJSONArray("slots")

                        /*Android UI  - Display the message for slots availability*/
                        val firstDateRes =
                            getShortDateFromDateFormat(rootObj.getString("firstDate"))
                        if (dateTextView.text.toString().equals(
                                firstDateRes, ignoreCase = true
                            ) || findEarlierSlotsAvailableDate
                        ) {
                            slotArr = slotArr.getJSONArray(0)
                            for (i in 0 until slotArr.length()) {
                                val slotObj = slotArr.getJSONObject(i)
                                val slotStart = slotObj.getString("start")
                                var format =
                                    SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                                var date = format.parse(slotStart)
                                format = SimpleDateFormat("HH:mm", Locale.getDefault())
                                var shortTimeStr: String? = null
                                if (date != null) {
                                    shortTimeStr = format.format(date)
                                }
                                val startSlot = AppointmentSlotListModel()
                                startSlot.slotTime = shortTimeStr
                                slotTimeArr!!.add(startSlot)
                                val slotEnd = slotObj.getString("finish")
                                format =
                                    SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                                date = format.parse(slotEnd)
                                format = SimpleDateFormat("HH:mm", Locale.getDefault())
                                if (date != null) {
                                    shortTimeStr = format.format(date)
                                }
                                val endSlot = AppointmentSlotListModel()
                                endSlot.endSlotTime = shortTimeStr
                                slotEndTime!!.add(endSlot)
                            }
                            timeSlotListAdapter!!.notifyDataSetChanged()
                            val slotObj = slotArr.getJSONObject(0)
                            var dateTemp = slotObj.getString("sdate")
                            var srcDf: DateFormat =
                                SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                            var date = srcDf.parse(dateTemp)
                            var destDf: DateFormat =
                                SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
                            if (date != null) {
                                dateTemp = destDf.format(date)
                            }
                            dateTextView.text = dateTemp
                            dateTemp = slotObj.getString("sdate")
                            srcDf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                            date = srcDf.parse(dateTemp)
                            destDf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                            if (date != null) {
                                dateStr = destDf.format(date)
                            }
                        } else {
                            llNoSlotsAvailable.visibility = View.VISIBLE
                            progressBar!!.visibility = View.GONE
                            bookEmptyText.text = "Slots are NOT available."
                            bookEmptyText1.text =
                                "Slots are NOT available for this date.\n Please try with other date."
                        }

                    } else {
                        llNoSlotsAvailable.visibility = View.VISIBLE
                        progressBar!!.visibility = View.GONE
                        bookEmptyText.text = "Slots are NOT available."
                        bookEmptyText1.text = "Slots are NOT available for this date.\n" +
                                " Please try with other date."
                    }
                } else {
                    progressBar!!.visibility = View.GONE
                    errorHandler(this@BookAppointmentTimeSlotActivity, result)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                progressBar!!.visibility = View.GONE
            }
        }
    }

    /*Android UI  - Display the message for slots availability*/
    private fun getShortDateFromDateFormat(firstDate: String): String {
        var format = SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.getDefault())
        return try {
            val date = format.parse(firstDate)
            format = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
            assert(date != null)
            format.format(date!!)
        } catch (e: Exception) {
            ""
        }
    }

    /*Android UI  - Display the message for slots availability*/
    private fun convertDateFormat(calendar: Calendar): String {
        val format1 = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
        return format1.format(calendar.time)
    }

    override fun onResume() {
        super.onResume()
        if (ConfirmOrderActivity.confirmOrderFlag == 1) {
            ConfirmOrderActivity.confirmOrderFlag = 0
            finish()
        }
    }

    private fun showGuide(section: Int) {
        when (section) {
            1 -> if (!appPreference!!.getBoolean("Slots", false)) {
                MaterialIntroView.Builder(this).enableDotAnimation(true).enableIcon(false)
                    .dismissOnTouch(true).setFocusGravity(FocusGravity.CENTER)
                    .setFocusType(Focus.NORMAL).setDelayMillis(50).enableFadeAnimation(true)
                    .setInfoText("Select the service for which you want to book the appointment")
                    .setShape(ShapeType.RECTANGLE).setTarget(serviceSelectGuidePt)
                    .setUsageId("intro_serviceSelectGuidePt") //THIS SHOULD BE UNIQUE ID
                    .setListener { materialIntroViewId: String? ->
                        showGuide(2)
                        val editor = appPreference!!.edit()
                        editor.putBoolean("Slots", true)
                        editor.apply()
                    }.show()
            }
            2 -> MaterialIntroView.Builder(this).enableDotAnimation(true).enableIcon(false)
                .dismissOnTouch(true).setFocusGravity(FocusGravity.CENTER)
                .setFocusType(Focus.NORMAL).setDelayMillis(50).enableFadeAnimation(true)
                .setInfoText("Select the date for your appointment").setShape(ShapeType.RECTANGLE)
                .setTarget(dateSelectionLayout)
                .setUsageId("intro_dateSelectionLayoutOne") //THIS SHOULD BE UNIQUE ID
                .setListener { materialIntroViewId: String? -> showGuide(3) }.show()
            3 -> MaterialIntroView.Builder(this).enableDotAnimation(true).enableIcon(false)
                .dismissOnTouch(true).setFocusGravity(FocusGravity.CENTER)
                .setFocusType(Focus.NORMAL).setDelayMillis(50).enableFadeAnimation(true)
                .setInfoText("Find your slots here. Select one slots as per your availability")
                .setShape(ShapeType.RECTANGLE).setTarget(gridview)
                .setUsageId("intro_gridview") //THIS SHOULD BE UNIQUE ID
                .setListener { materialIntroViewId: String? -> }.show()
        }
    }

    fun saveTimeFormat(timeFormatValue: Int) {
        progressBar!!.visibility = View.VISIBLE
        llNoSlotsAvailable.visibility = View.GONE
        var jsonValue = JSONObject()
        try {
            jsonValue = JSONObject()
            jsonValue.put("time_pref", timeFormatValue)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        settingViewModel!!.saveTimeFormat(activity, jsonValue)
            .observe(this@BookAppointmentTimeSlotActivity) { s: String? ->
                progressBar!!.visibility = View.GONE
                llNoSlotsAvailable.visibility = View.GONE
                try {
                    val jsonObject = JSONObject(s!!)
                    if (jsonObject.getInt("status_code") == 200) {
                        if (jsonObject.getJSONObject("response").getInt("response") == 1) {
                            Toast.makeText(
                                activity,
                                "Time format has been changed successfully",
                                Toast.LENGTH_SHORT
                            ).show()
                            setTimeFormat(timeFormatValue)
                            if (slotTimeArr!!.size > 0) {
                                llNoSlotsAvailable.visibility = View.GONE
                                timeSlotListAdapter!!.notifyDataSetChanged()
                            } else {
                                llNoSlotsAvailable.visibility = View.VISIBLE
                            }
                        }
                    } else {
                        llNoSlotsAvailable.visibility = View.VISIBLE
                        progressBar!!.visibility = View.GONE
                        errorHandler(this@BookAppointmentTimeSlotActivity, s)
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }
    }

    fun setTimeFormat(timeFormatValue: Int) {
        // Creating an Editor object to edit(write to the file)
        val myEdit = sharedPreferencesTimeFormat!!.edit()

        // Storing the key and its value as the data fetched from edittext
        myEdit.putInt("timeFormat", timeFormatValue)

        // Once the changes have been made,
        // we need to commit to apply those changes made,
        // otherwise, it will throw an error
        myEdit.apply()
    }
}