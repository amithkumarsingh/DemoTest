package com.whitecoats.clinicplus.activities

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Intent
import android.graphics.PorterDuff
import android.os.Bundle
import android.text.SpannableString
import android.text.style.UnderlineSpan
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request.Method
import com.whitecoats.clinicplus.MyClinicGlobalClass
import com.whitecoats.clinicplus.R
import com.whitecoats.clinicplus.adapters.AppointmentRescheduleSlotAdapter
import com.whitecoats.clinicplus.apis.ApiUrls
import com.whitecoats.clinicplus.databinding.ActivityAppointmentRescheduleBinding
import com.whitecoats.clinicplus.utils.AppUtilities
import com.whitecoats.clinicplus.utils.ErrorHandlerClass.errorHandler
import com.whitecoats.clinicplus.viewmodels.CommonViewModel
import com.whitecoats.clinicplus.viewmodels.RescheduleApptViewModel
import com.whitecoats.model.AppointmentSlotListModel
import org.json.JSONException
import org.json.JSONObject
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class AppointmentRescheduleActivity : AppCompatActivity() {
    private var progressBar: ProgressBar? = null
    private var emptyText: TextView? = null
    private var slotTimeArr: ArrayList<AppointmentSlotListModel>? = null
    private var slotEndTime: ArrayList<AppointmentSlotListModel>? = null
    private var timeSlotListAdapter: AppointmentRescheduleSlotAdapter? = null
    private var gridview: RecyclerView? = null
    private var date_tv: TextView? = null
    private var slotSaasId = 0
    private var num_slots_avaliable: TextView? = null
    private var mLayoutManager: GridLayoutManager? = null
    private var time_of_reschedule: TextView? = null
    private var custom_date_lay: RelativeLayout? = null
    private var mYear = 0
    private var mMonth = 0
    private var mDay = 0
    private var patientName: String? = null
    private var patientNumber: String? = null
    private var appointmentMode: String? = null
    private var appointmentTime: String? = null
    private var patient_name: TextView? = null
    private var patient_num: TextView? = null
    private var consult_type: TextView? = null
    private var consult_time_date: TextView? = null
    private var rescheduleApptViewModel: RescheduleApptViewModel? = null
    private var submit_button: TextView? = null
    private var appointmentId = 0
    private var selectedStartTime = ""
    private var selectedEndTime = ""
    private var toolbar: Toolbar? = null
    private var appointmentDate: String? = null
    private var selectedDate = ""
    private var appUtilities: AppUtilities? = null
    private var globalClass: MyClinicGlobalClass? = null
    private var next_day_slot_lay: RelativeLayout? = null
    private var nextDayslotavailable_textview: TextView? = null
    private var slots_lay: RelativeLayout? = null
    private var nextDate: String? = null
    private var firstDateString: String? = null
    private var selectedDateString: String? = null
    private var appUtils: com.whitecoats.clinicplus.AppUtilities? = null
    private lateinit var commonViewModel: CommonViewModel
    private lateinit var dialog: Dialog
    private var dateStr = ""
    private var dateStrSplit: Array<String>?=null

    private var tvPatientGeneralId: TextView? = null
    private var patientGeneralID: String? = ""

    @SuppressLint("SetTextI18n", "NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_appointment_reschedule)
        globalClass = applicationContext as MyClinicGlobalClass
        val activityAppointmentRescheduleBinding =
            DataBindingUtil.setContentView<ActivityAppointmentRescheduleBinding>(
                this@AppointmentRescheduleActivity,
                R.layout.activity_appointment_reschedule
            )

        toolbar = findViewById(R.id.reschedule_toolBar)
        toolbar!!.title = "Reschedule Appointment"
        //        toolbar.setTitle("Payment Timeline Test");
        setSupportActionBar(toolbar)
        commonViewModel=ViewModelProvider(this@AppointmentRescheduleActivity)[CommonViewModel::class.java]
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        val backArrow = ContextCompat.getDrawable(this, R.drawable.ic_arrow_back)
        backArrow!!.setColorFilter(
            ContextCompat.getColor(this, R.color.colorWhite),
            PorterDuff.Mode.SRC_ATOP
        )
        supportActionBar!!.setHomeAsUpIndicator(backArrow)
        progressBar = findViewById(R.id.bookLoading)
        emptyText = findViewById(R.id.bookEmptyText)
        gridview = findViewById<View>(R.id.gridView1) as RecyclerView
        date_tv = findViewById<View>(R.id.date_tv) as TextView
        time_of_reschedule = findViewById<View>(R.id.time_of_reschedule) as TextView
        num_slots_avaliable = findViewById<View>(R.id.num_slots_avaliable) as TextView
        custom_date_lay = findViewById<View>(R.id.custom_date_lay) as RelativeLayout
        patient_name = findViewById<View>(R.id.patient_name) as TextView
        patient_num = findViewById<View>(R.id.patient_num) as TextView
        consult_type = findViewById<View>(R.id.consult_type) as TextView
        consult_time_date = findViewById<View>(R.id.consult_time_date) as TextView
        submit_button = findViewById<View>(R.id.submit_button) as TextView
        next_day_slot_lay = findViewById<View>(R.id.next_day_slot_lay) as RelativeLayout
        nextDayslotavailable_textview =
            findViewById<View>(R.id.nextDayslotavailable_textview) as TextView
        slots_lay = findViewById<View>(R.id.slots_lay) as RelativeLayout

        tvPatientGeneralId = findViewById(R.id.tv_patient_general_id)


        slotTimeArr = ArrayList()
        slotEndTime = ArrayList()
        appUtilities = AppUtilities()
        submit_button!!.isClickable = false
        slotSaasId = intent.extras!!.getInt("InternalSaasId")
        patientName = intent.extras!!.getString("PatientName")

        if (intent.hasExtra("PatientGeneralID")) {
            patientGeneralID = intent.extras!!.getString("PatientGeneralID")
        }

        patientNumber = intent.extras!!.getString("PatientNumber")
        appointmentMode = intent.extras!!.getString("AppointmentMode")
        appointmentTime = intent.extras!!.getString("AppointmentTime")
        appointmentId = intent.extras!!.getInt("AppointmentId")
        appointmentDate = intent.extras!!.getString("AppointmentDate")
        patient_name!!.text = patientName
        patient_num!!.text = patientNumber
        consult_type!!.text = appointmentMode

        if (!patientGeneralID.equals("", ignoreCase = true)) {
            tvPatientGeneralId!!.visibility = View.VISIBLE
            tvPatientGeneralId!!.text = patientGeneralID
        } else {
            tvPatientGeneralId!!.visibility = View.GONE
        }

        appUtils = com.whitecoats.clinicplus.AppUtilities()
        rescheduleApptViewModel = ViewModelProvider(this@AppointmentRescheduleActivity)[RescheduleApptViewModel::class.java]
        activityAppointmentRescheduleBinding.viewModel = rescheduleApptViewModel
        activityAppointmentRescheduleBinding.lifecycleOwner = this
        val calendar = Calendar.getInstance()
        val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH)
        dateStr = format.format(Date())
        var finalDateTimeString = ""
        try {
            val month_date = SimpleDateFormat("MMM yyyy", Locale.ENGLISH)
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
            val dateSplitedString =
                appointmentDate!!.split("-".toRegex()).dropLastWhile { it.isEmpty() }
                    .toTypedArray()
            val date = sdf.parse(appointmentDate.toString())
            val month_name = month_date.format(date!!)
            finalDateTimeString =
                dateSplitedString[2] + month_name + " @" + appUtils!!.formatTimeBasedOnPreference(
                    applicationContext, "HH:mm:ss", appointmentTime
                )
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        consult_time_date!!.text = finalDateTimeString
        timeSlotListAdapter = AppointmentRescheduleSlotAdapter(
            this@AppointmentRescheduleActivity,
            slotTimeArr!!,
            slotEndTime!!
        ) { startTime, endTime ->
            if (selectedDate.isEmpty()) {
                time_of_reschedule!!.text =
                    "Today" + " @ " + appUtils!!.formatTimeBasedOnPreference(
                        this@AppointmentRescheduleActivity,
                        "HH:mm",
                        startTime
                    )
            } else {
                time_of_reschedule!!.text =
                    "$selectedDate @ " + appUtils!!.formatTimeBasedOnPreference(
                        this@AppointmentRescheduleActivity,
                        "HH:mm",
                        startTime
                    )
            }
            selectedStartTime = startTime
            selectedEndTime = endTime
            submit_button!!.isClickable = true
            submit_button!!.setBackgroundColor(ContextCompat.getColor(this,R.color.colorPrimary))
        }

//        Log.d("Current Data", dateStr);
        val dateFormat = DateFormat.getDateInstance(DateFormat.MEDIUM)
        try {
            val month_date = SimpleDateFormat("MMM yyyy", Locale.ENGLISH)
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
            val dateTime = dateStr.split(" ".toRegex()).dropLastWhile { it.isEmpty() }
                .toTypedArray()
            val dateSplitedString = dateTime[0].split("-".toRegex()).dropLastWhile { it.isEmpty() }
                .toTypedArray()
            val date = sdf.parse(appointmentDate!!)
            val month_name = month_date.format(date!!)
            val monthYear =
                month_name.split(" ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            selectedDateString = dateSplitedString[2] + monthYear[0] + ", " + monthYear[1]
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        date_tv!!.text = "Today, " + dateFormat.format(calendar.time)
        custom_date_lay!!.setOnClickListener { // Get Current Date
            val c = Calendar.getInstance()
            mYear = c[Calendar.YEAR]
            mMonth = c[Calendar.MONTH]
            mDay = c[Calendar.DAY_OF_MONTH]
            val datePickerDialog = DatePickerDialog(
                this@AppointmentRescheduleActivity,
                { view, year, monthOfYear, dayOfMonth -> //startDate.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
                    selectedDate = dateFormat(
                        dayOfMonth.toString() + "/" + (monthOfYear + 1) + "/" + year,
                        "dd/MM/yyyy",
                        "dd MMM,yy"
                    )
                    date_tv!!.text = selectedDate
                    dateStr = dateFormat(
                        dayOfMonth.toString() + "/" + (monthOfYear + 1) + "/" + year,
                        "dd/MM/yyyy",
                        "yyyy-MM-dd HH:mm:ss"
                    )
                    slotTimeArr!!.clear()
                    slotEndTime!!.clear()
                    selectedStartTime = ""
                    timeSlotListAdapter!!.selectedSlot = ""
                    timeSlotListAdapter!!.notifyDataSetChanged()
                    getSlots(slotSaasId, dateStr)
                }, mYear, mMonth, mDay
            )
            datePickerDialog.show()
            datePickerDialog.datePicker.minDate = System.currentTimeMillis() - 1000
        }
        gridview!!.setHasFixedSize(true)
        gridview!!.isNestedScrollingEnabled = false
        mLayoutManager = GridLayoutManager(this, 4)
        gridview!!.layoutManager = mLayoutManager
        gridview!!.adapter = timeSlotListAdapter
        getSlots(slotSaasId, dateStr)
        submit_button!!.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View) {
                if (selectedStartTime.equals("", ignoreCase = true)) {
                    return
                }
                if (globalClass!!.isOnline) {
                    val dateSplit: Array<String?> =
                        dateStr.split(" ".toRegex()).dropLastWhile { it.isEmpty() }
                            .toTypedArray()
                    val jsonObject = JSONObject()
                    try {
                        jsonObject.put("appId", appointmentId)
                        jsonObject.put("startSlot", dateSplit[0] + " " + selectedStartTime)
                        jsonObject.put("endSlot", dateSplit[0] + " " + selectedEndTime)
                        showCustomProgressAlertDialog("", "Please wait..")
                        commonViewModel.commonViewModelCall(
                            ApiUrls.apptReschedule,
                            jsonObject,
                            Method.POST
                        ).observe(
                            this@AppointmentRescheduleActivity
                        ) { result ->
                            try {
                                val resObjNew = JSONObject(result)
                                dialog.dismiss()
                                if (resObjNew.getInt("status_code") == 200) {
                                    val dialog = Dialog(this@AppointmentRescheduleActivity)
                                    dialog.setCancelable(false)
                                    dialog.setContentView(R.layout.dialog_reschedule_appt_success)
                                    val appointment_reschedule_time =
                                        dialog.findViewById<View>(R.id.appointment_reschedule_time) as TextView
                                    val close_textView =
                                        dialog.findViewById<View>(R.id.close_textView) as TextView
                                    val dailogArticleCancel =
                                        dialog.findViewById<View>(R.id.dailogArticleCancel) as ImageView
                                    if (selectedDate.isEmpty()) {
                                        appointment_reschedule_time.text =
                                            getString(R.string.reschedule_appointment_success) + " " + selectedDateString + " @ " + appUtils!!.formatTimeBasedOnPreference(
                                                this@AppointmentRescheduleActivity,
                                                "HH:mm",
                                                selectedStartTime
                                            )
                                    } else {
                                        appointment_reschedule_time.text =
                                            getString(R.string.reschedule_appointment_success) + " " + selectedDate + " @ " + appUtils!!.formatTimeBasedOnPreference(
                                                this@AppointmentRescheduleActivity,
                                                "HH:mm",
                                                selectedStartTime
                                            )
                                    }
                                    val intent = Intent()
                                    // Here you can also put data on intent
                                    intent.action = "UPDATE_FRAG_LIST"
                                    if (dateSplit[0] != null) {
                                        intent.putExtra("appointDateInCal", dateSplit[0])
                                    }
                                    intent.putExtra("isAppointmentNew", false)
                                    sendBroadcast(intent)
                                    close_textView.setOnClickListener { finish() }
                                    dailogArticleCancel.setOnClickListener { finish() }
                                    dialog.show()
                                    val window = dialog.window
                                    window!!.setLayout(
                                        ViewGroup.LayoutParams.MATCH_PARENT,
                                        ViewGroup.LayoutParams.WRAP_CONTENT
                                    )
                                } else {
                                    errorHandler(this@AppointmentRescheduleActivity, result)
                                }
                            } catch (e: Exception) {
                                dialog.dismiss()
                                e.printStackTrace()
                            }
                        }
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                } else {
                    globalClass!!.noInternetConnection.showDialog(this@AppointmentRescheduleActivity)
                }
            }
        })
        nextDayslotavailable_textview!!.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View) {
                val nextDateString = "$firstDateString 00:00:00"
                dateStr = "$firstDateString 00:00:00"
                selectedStartTime = ""
                timeSlotListAdapter!!.selectedSlot = ""
                getSlots(slotSaasId, nextDateString)
            }
        })

    }

    private fun showCustomProgressAlertDialog(title: String, progressVal: String) {
        val builder = AlertDialog.Builder(this@AppointmentRescheduleActivity)
        builder.setTitle(title)
        builder.setCancelable(false)
        val customLayout: View = layoutInflater.inflate(R.layout.custom_progress_bar, null)
        builder.setView(customLayout)
        dialog = builder.create()
        val tvBody = customLayout.findViewById<TextView>(R.id.tv_value)
        tvBody.text = progressVal
        dialog.show()
    }

    fun trimMessage(json: String?, key: String?): String? {
        val trimmedString: String?
        try {
            val obj = JSONObject(json!!)
            trimmedString = obj.getString(key!!)
        } catch (e: JSONException) {
            e.printStackTrace()
            return null
        }
        return trimmedString
    }

    @SuppressLint("SetTextI18n", "NotifyDataSetChanged")
    fun getSlots(slotSaasId: Int, date: String) {
        if (globalClass!!.isOnline) {
            progressBar!!.visibility = View.VISIBLE
            emptyText!!.visibility = View.GONE
            val url =
                (ApiUrls.getDoctorsSlot + "?doctor_id=" + ApiUrls.doctorId + "&id=" + slotSaasId
                        + "&from=" + date.replace(" ", "%20"))

            commonViewModel.commonViewModelCall(url, JSONObject(), Method.GET).observe(
                this@AppointmentRescheduleActivity
            ) { result ->
                try {
                    val responseObj = JSONObject(result)
                    if (responseObj.getInt("status_code") == 200) {
                        val response = responseObj.optJSONObject("response")
                        progressBar!!.visibility = View.GONE
                        emptyText!!.visibility = View.GONE
                        try {
                            val rootObj = response!!.getJSONObject("response")
                            val firstDate = rootObj.optString("firstDate").split(" ".toRegex())
                                .dropLastWhile { it.isEmpty() }
                                .toTypedArray()
                            val dateString =
                                firstDate[0].split("/".toRegex()).dropLastWhile { it.isEmpty() }
                                    .toTypedArray()
                            firstDateString =
                                dateString[0] + "-" + dateString[1] + "-" + dateString[2]
                            dateStrSplit =
                                dateStr.split(" ".toRegex()).dropLastWhile { it.isEmpty() }
                                    .toTypedArray()
                            if (firstDateString.equals(dateStrSplit!![0], ignoreCase = true)) {
                                next_day_slot_lay!!.visibility = View.GONE
                                slots_lay!!.visibility = View.VISIBLE
                                var slotArr = rootObj.getJSONArray("slots")
                                slotArr = slotArr.getJSONArray(0)
                                num_slots_avaliable!!.text =
                                    slotArr.length().toString() + " slots Available"
                                for (i in 0 until slotArr.length()) {
                                    val slotObj = slotArr.getJSONObject(i)
                                    val slotStart = slotObj.getString("start")
                                    var format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH)
                                    var date = format.parse(slotStart)
                                    format = SimpleDateFormat("HH:mm", Locale.ENGLISH)
                                    var shortTimeStr = format.format(date!!)
                                    val startSlot = AppointmentSlotListModel()
                                    startSlot.slotTime = shortTimeStr
                                    slotTimeArr!!.add(startSlot)
                                    val slotEnd = slotObj.getString("finish")
                                    format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH)
                                    date = format.parse(slotEnd)
                                    format = SimpleDateFormat("HH:mm", Locale.ENGLISH)
                                    shortTimeStr = format.format(date!!)
                                    val endSlot = AppointmentSlotListModel()
                                    endSlot.endSlotTime = shortTimeStr
                                    slotEndTime!!.add(endSlot)
                                }
                                timeSlotListAdapter!!.notifyDataSetChanged()
                                val slotObj = slotArr.getJSONObject(0)
                                var dateTemp = slotObj.getString("sdate")
                                var srcDf: DateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
                                var date = srcDf.parse(dateTemp)
                                var destDf: DateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.ENGLISH)
                                dateTemp = destDf.format(date!!)
                                date_tv!!.text = dateTemp
                                dateTemp = slotObj.getString("sdate")
                                srcDf = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
                                date = srcDf.parse(dateTemp)
                                destDf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH)
                                dateStr = destDf.format(date!!)
                            } else {
                                next_day_slot_lay!!.visibility = View.VISIBLE
                                slots_lay!!.visibility = View.GONE
                                nextDate =
                                    convertDatetoMonthFormate(rootObj.optString("firstDate"))
                                val text = "Next Slots Available on $nextDate"
                                val content = SpannableString(text)
                                content.setSpan(UnderlineSpan(), 0, content.length, 0)
                                nextDayslotavailable_textview!!.text = content
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                            emptyText!!.visibility = View.VISIBLE
                            progressBar!!.visibility = View.GONE
                        }
                    }else{
                        progressBar!!.visibility = View.GONE
                        errorHandler(this@AppointmentRescheduleActivity, result)
                    }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        progressBar!!.visibility = View.GONE
                    }
                }
            } else {
                globalClass!!.noInternetConnection.showDialog(this@AppointmentRescheduleActivity)
            }
        }

        override fun onOptionsItemSelected(item: MenuItem): Boolean {
            when (item.itemId) {
                android.R.id.home -> finish()
            }
            //                finish();
            return true
        }

        private fun dateFormat(dateStr: String, inputFormat: String, outputFormat: String): String {
            var dateStr = dateStr
            return try {
        //            String dateStr = "21/20/2011";
                val srcDf: DateFormat = SimpleDateFormat(inputFormat, Locale.ENGLISH)
                // parse the date string into Date object
                val date = srcDf.parse(dateStr)
                val destDf: DateFormat = SimpleDateFormat(outputFormat, Locale.ENGLISH)
                // format the date into another format
                dateStr = destDf.format(date!!)
                println("Converted date is : $dateStr")
                dateStr
            } catch (e: ParseException) {
                e.printStackTrace()
                ""
            }
        }

        @Deprecated("Deprecated in Java")
        override fun onBackPressed() {
           super.getOnBackPressedDispatcher().onBackPressed()
            finish()
        }

        private fun convertDatetoMonthFormate(scheduleTime: String): String {
            var finalDateTimeString = ""
            try {
                val month_date = SimpleDateFormat("MMM yyyy", Locale.ENGLISH)
                val sdf = SimpleDateFormat("yyyy/MM/dd", Locale.ENGLISH)
                val date_time = scheduleTime
                val splitDateTime =
                    date_time.split(" ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                val dateString = splitDateTime[0]
                val dateSplitedString =
                    dateString.split("/".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                val time = splitDateTime[1]
                val date = sdf.parse(dateString)
                val month_name = month_date.format(date!!)
                finalDateTimeString = dateSplitedString[2] + month_name
            } catch (e: ParseException) {
                e.printStackTrace()
            }
            return finalDateTimeString
        }
    }