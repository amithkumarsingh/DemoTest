package com.whitecoats.clinicplus

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.content.IntentFilter
import android.content.SharedPreferences
import android.graphics.PorterDuff
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import co.mobiwise.materialintro.shape.Focus
import co.mobiwise.materialintro.shape.FocusGravity
import co.mobiwise.materialintro.shape.ShapeType
import co.mobiwise.materialintro.view.MaterialIntroView
import com.whitecoats.adapter.BlockTimeListAdapter
import com.whitecoats.adapter.BlockTimeServiceAdapter
import com.whitecoats.broadcast.SettingsBroadcastReceiver
import com.whitecoats.clinicplus.apis.ApiGetPostMethodCalls
import com.whitecoats.clinicplus.apis.ApiUrls
import com.whitecoats.clinicplus.utils.ErrorHandlerClass.errorHandler
import com.whitecoats.clinicplus.viewmodels.SettingViewModel
import com.whitecoats.model.BlockTimeListModel
import com.zoho.salesiqembed.ZohoSalesIQ
import org.json.JSONException
import org.json.JSONObject
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class BlockTimeActivity : AppCompatActivity() {
    private lateinit var blockTimeList: RecyclerView
    private var blockTimeListModelList: MutableList<BlockTimeListModel>? = null
    private var blockTimeListAdapter: BlockTimeListAdapter? = null
    private lateinit var addNew: Button
    private lateinit var blockTimeAddTime: Button
    private lateinit var addNew2: Button
    private lateinit var timeListLayout: RelativeLayout
    private lateinit var addNewLayout: RelativeLayout
    private lateinit var url: String
    private lateinit var startDate: TextView
    private lateinit var endDate: TextView
    private lateinit var startTime: TextView
    private lateinit var endTime: TextView
    private lateinit var emptyTv: TextView
    private var mYear = 0
    private var mMonth: Int = 0
    private var mDay: Int = 0
    private var mHour: Int = 0
    private var mMinute: Int = 0
    private var mSecond: Int = 0
    private var serviceName: MutableList<BlockTimeListModel>? = null
    private lateinit var reasonText: EditText
    private lateinit var jsonValue: JSONObject
    var selectedService = 0
    var selectedServiceID: Int = 0
    var selectedProductID: Int = 0
    var selectedDoctorId: Int = 0
    private lateinit var serviceRecycleView: RecyclerView
    private var blockTimeServiceAdapter: BlockTimeServiceAdapter? = null
    private lateinit var appPreference: SharedPreferences
    private lateinit var appUtilities: AppUtilities
    private lateinit var dateLayout: LinearLayout
    val CUSTOM_BROADCAST_ACTION = "SettingsBroadcastReceiver"
    private var settingsBroadcastReceiver: SettingsBroadcastReceiver? = null
    private lateinit var apiGetPostMethodCalls: ApiGetPostMethodCalls
    private lateinit var dialog: AlertDialog
    private lateinit var viewModel: SettingViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_block_time)

        val toolbar = findViewById<Toolbar>(R.id.blockTimeToolbar)
        toolbar.title = "Your Time Blocks"
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        val upArrow = ContextCompat.getDrawable(this, R.drawable.ic_arrow_back)
        upArrow?.setColorFilter(
            ContextCompat.getColor(this, R.color.colorWhite),
            PorterDuff.Mode.SRC_ATOP
        )
        supportActionBar!!.setHomeAsUpIndicator(upArrow)
        // Get Current Date
        // Get Current Date
        val c = Calendar.getInstance()
        mYear = c[Calendar.YEAR]
        mMonth = c[Calendar.MONTH]
        mDay = c[Calendar.DAY_OF_MONTH]
        mHour = c[Calendar.HOUR_OF_DAY]
        mMinute = c[Calendar.MINUTE]
        mSecond = c[Calendar.SECOND]

        blockTimeList = findViewById(R.id.blockTimeRecycleView)
        addNew = findViewById(R.id.blockTimeAddNew)
        addNewLayout = findViewById(R.id.blockTimeAddForm)
        timeListLayout = findViewById(R.id.blockTimeListLayout)
        blockTimeAddTime = findViewById(R.id.blockTimeAddTime)

        startDate = findViewById(R.id.startDateCalender)
        endDate = findViewById(R.id.endDateCalender)
        startTime = findViewById(R.id.startTimeCalender)
        endTime = findViewById(R.id.endTimeCalender)
        reasonText = findViewById(R.id.reasonText)
        emptyTv = findViewById(R.id.blockTimeEmptyText)
        addNew2 = findViewById(R.id.blockTimeAddNew2)
        serviceRecycleView = findViewById(R.id.blockTimeServiceRecycleView)
        appPreference = getSharedPreferences(ApiUrls.appSharedPref, MODE_PRIVATE)
        selectedDoctorId = appPreference.getInt(ApiUrls.docId, 0)
        appUtilities = AppUtilities()
        viewModel = ViewModelProvider(this)[SettingViewModel::class.java]
        viewModel.init()
        dateLayout = findViewById(R.id.blockTimeDateLayout)

        startDate.text = dateFormat(
            mDay.toString() + "/" + (mMonth + 1) + "/" + mYear,
            "dd/MM/yyyy",
            "dd MMM,yy"
        )
        endDate.text = dateFormat(
            (mDay + 1).toString() + "/" + (mMonth + 1) + "/" + mYear,
            "dd/MM/yyyy",
            "dd MMM,yy"
        )
        startTime.text = String.format("%02d:%02d", mHour, mMinute)
        endTime.text = String.format("%02d:%02d", mHour, mMinute)
        apiGetPostMethodCalls = ApiGetPostMethodCalls()
        blockTimeListModelList = ArrayList()
        serviceName = ArrayList()
        blockTimeServiceAdapter = BlockTimeServiceAdapter(this, serviceName)
        val horizontalLayoutManager =
            LinearLayoutManager(this@BlockTimeActivity, LinearLayoutManager.HORIZONTAL, false)
        serviceRecycleView.layoutManager = horizontalLayoutManager
        serviceRecycleView.adapter = blockTimeServiceAdapter
        settingsBroadcastReceiver = SettingsBroadcastReceiver()
        //register broadcast

        //register broadcast
        val filter = IntentFilter()
        filter.addAction(CUSTOM_BROADCAST_ACTION)
        filter.addCategory(Intent.CATEGORY_DEFAULT)
        registerReceiver(settingsBroadcastReceiver, filter)

        startDate.setOnClickListener {
            val date =
                appUtilities.changeDateFormat("dd MMM,yy", "dd MM yyyy", startDate.text.toString())
            val split = date.split(" ".toRegex()).dropLastWhile { it.isEmpty() }
                .toTypedArray()
            mDay = split[0].toInt()
            mMonth = split[1].toInt() - 1
            mYear = split[2].toInt()
            val datePickerDialog = DatePickerDialog(
                this@BlockTimeActivity,
                { _, year, monthOfYear, dayOfMonth ->
                    startDate.text = dateFormat(
                        dayOfMonth.toString() + "/" + (monthOfYear + 1) + "/" + year,
                        "dd/MM/yyyy",
                        "dd MMM,yy"
                    )
                }, mYear, mMonth, mDay
            )
            datePickerDialog.show()
        }

        endDate.setOnClickListener {
            val date =
                appUtilities.changeDateFormat("dd MMM,yy", "dd MM yyyy", endDate.text.toString())
            val split = date.split(" ".toRegex()).dropLastWhile { it.isEmpty() }
                .toTypedArray()
            mDay = split[0].toInt()
            mMonth = split[1].toInt() - 1
            mYear = split[2].toInt()
            val datePickerDialog = DatePickerDialog(
                this@BlockTimeActivity,
                { _, year, monthOfYear, dayOfMonth ->
                    endDate.text = dateFormat(
                        dayOfMonth.toString() + "/" + (monthOfYear + 1) + "/" + year,
                        "dd/MM/yyyy",
                        "dd MMM,yy"
                    )
                }, mYear, mMonth, mDay
            )
            datePickerDialog.show()
        }

        startTime.setOnClickListener {
            val time = startTime.hint.toString().split(":".toRegex()).dropLastWhile { it.isEmpty() }
                .toTypedArray()
            mHour = time[0].toInt()
            mMinute = time[1].toInt()
            var is24HourView = true
            if (appUtilities.timeFormatPreferences(applicationContext) == 12) {
                is24HourView = false
            }

            // Launch Time Picker Dialog
            val timePickerDialog = TimePickerDialog(
                this@BlockTimeActivity,
                { view, hourOfDay, minute ->
                    val format: String = if (view.is24HourView) {
                        "HH:mm"
                    } else {
                        "hh:mm aa"
                    }
                    startTime.hint = appUtilities.getFormattedTime(hourOfDay, minute, "HH:mm")
                    startTime.text = appUtilities.getFormattedTime(hourOfDay, minute, format)
                }, mHour, mMinute, is24HourView
            )
            timePickerDialog.show()
        }

        endTime.setOnClickListener {
            val time = endTime.hint.toString().split(":".toRegex()).dropLastWhile { it.isEmpty() }
                .toTypedArray()
            mHour = time[0].toInt()
            mMinute = time[1].toInt()
            var is24HourView = true
            if (appUtilities.timeFormatPreferences(applicationContext) == 12) {
                is24HourView = false
            }

            // Launch Time Picker Dialog
            val timePickerDialog = TimePickerDialog(
                this@BlockTimeActivity,
                { view, hourOfDay, minute ->
                    val format: String = if (view.is24HourView) {
                        "HH:mm"
                    } else {
                        "hh:mm aa"
                    }
                    endTime.hint = appUtilities.getFormattedTime(hourOfDay, minute, "HH:mm")
                    endTime.text = appUtilities.getFormattedTime(hourOfDay, minute, format)
                }, mHour, mMinute, is24HourView
            )
            timePickerDialog.show()
        }


        getDoctorBlockTime()

        blockTimeListAdapter= BlockTimeListAdapter(this,blockTimeListModelList,
            object : SettingBlockTimeClickListener{
                override fun onItemClick(
                    v: View?,
                    position: Int,
                    timeBlockStatus: JSONObject?,
                    clickType: String
                ) {
                    if (clickType.equals("DELETE", ignoreCase = true)) {
                        deleteDoctorTimeBlock(position, timeBlockStatus, clickType)
                    } else if (clickType.equals("SWITCH", ignoreCase = true)) {
                        deleteDoctorTimeBlock(position, timeBlockStatus, clickType)
                    }
                }
            })

        val mLayoutManager: RecyclerView.LayoutManager = LinearLayoutManager(applicationContext)
        blockTimeList.layoutManager = mLayoutManager
        blockTimeList.itemAnimator = DefaultItemAnimator()
        blockTimeList.adapter = blockTimeListAdapter

        addNew.setOnClickListener {
            showGuide(1)
            ZohoSalesIQ.Tracking.setPageTitle("Settings - Time Block - Time Block Form")
            getDoctorServiceForTimeBlock()
            addNewLayout.visibility = View.VISIBLE
            timeListLayout.visibility = View.GONE
            val cal = Calendar.getInstance()
            mYear = cal[Calendar.YEAR]
            mMonth = cal[Calendar.MONTH]
            mDay = cal[Calendar.DAY_OF_MONTH]
            mHour = cal[Calendar.HOUR_OF_DAY]
            mMinute = cal[Calendar.MINUTE]
            mSecond = cal[Calendar.SECOND]
            startDate.text = dateFormat(
                mDay.toString() + "/" + (mMonth + 1) + "/" + mYear,
                "dd/MM/yyyy",
                "dd MMM,yy"
            )
            endDate.text = dateFormat(
                (mDay + 1).toString() + "/" + (mMonth + 1) + "/" + mYear,
                "dd/MM/yyyy",
                "dd MMM,yy"
            )
            startTime.hint = String.format("%02d:%02d", mHour, mMinute)
            endTime.hint = String.format("%02d:%02d", mHour, mMinute)
            val currentTime = appUtilities.formatTimeBasedOnPreference(
                applicationContext, "HH:mm",
                "$mHour:$mMinute"
            )
            startTime.text = currentTime
            endTime.text = currentTime
            reasonText.setText("")
            selectedServiceID = 0
            selectedService=0
            selectedProductID=0
            emptyTv.visibility = View.GONE
            addNew2.visibility = View.GONE
        }

        addNew2.setOnClickListener {
            getDoctorServiceForTimeBlock()
            addNewLayout.visibility = View.VISIBLE
            timeListLayout.visibility = View.GONE
            reasonText.setText("")
            selectedServiceID = 0
            selectedService=0
            selectedProductID=0
            emptyTv.visibility = View.GONE
            addNew2.visibility = View.GONE
            ZohoSalesIQ.Tracking.setPageTitle("Settings - Time Block - Time Block Form")
            val cal = Calendar.getInstance()
            mYear = cal[Calendar.YEAR]
            mMonth = cal[Calendar.MONTH]
            mDay = cal[Calendar.DAY_OF_MONTH]
            mHour = cal[Calendar.HOUR_OF_DAY]
            mMinute = cal[Calendar.MINUTE]
            mSecond = cal[Calendar.SECOND]
            startDate.text = dateFormat(
                mDay.toString() + "/" + (mMonth + 1) + "/" + mYear,
                "dd/MM/yyyy",
                "dd MMM,yy"
            )
            endDate.text = dateFormat(
                (mDay + 1).toString() + "/" + (mMonth + 1) + "/" + mYear,
                "dd/MM/yyyy",
                "dd MMM,yy"
            )
            startTime.hint = String.format("%02d:%02d", mHour, mMinute)
            endTime.hint = String.format("%02d:%02d", mHour, mMinute)
            val currentTime = appUtilities.formatTimeBasedOnPreference(
                applicationContext, "HH:mm",
                "$mHour:$mMinute"
            )
            startTime.text = currentTime
            endTime.text = currentTime
        }

        blockTimeAddTime.setOnClickListener {
            val sd: String = dateFormat(startDate.text.toString(), "dd MMM,yy", "yyyy-MM-dd")
            val ed: String = dateFormat(endDate.text.toString(), "dd MMM,yy", "yyyy-MM-dd")
            try {
                val timeComparison: Boolean =
                    checkTimings(startTime.hint.toString(), endTime.hint.toString())
                if (ed.compareTo(sd) > 0) {
                    showCustomDialog()
                } else {
                    if (ed.compareTo(sd) >= 0) {
                        if (timeComparison) {
                            showCustomDialog()
                        } else {
                            Toast.makeText(
                                this@BlockTimeActivity,
                                resources.getString(R.string.end_time_warning),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } else {
                        Toast.makeText(
                            this@BlockTimeActivity,
                            resources.getString(R.string.end_date_warning),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // handle arrow click here
        if (item.itemId == android.R.id.home) {
            if (addNewLayout.visibility == View.VISIBLE) {
                addNewLayout.visibility = View.GONE
                timeListLayout.visibility = View.VISIBLE
                emptyTv.visibility = View.GONE
                addNew2.visibility = View.GONE
                addNew.visibility = View.VISIBLE
                if (blockTimeListModelList!!.size == 0) {
                    emptyTv.visibility = View.VISIBLE
                    addNew2.visibility = View.VISIBLE
                    addNew.visibility = View.GONE
                }
            } else {
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            unregisterReceiver(settingsBroadcastReceiver)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    private fun getDoctorBlockTime() {
        showCustomProgressAlertDialog(getString(R.string.fetching))
        emptyTv.visibility = View.GONE
        addNew2.visibility = View.GONE

        viewModel.doctorBlockTime.observe(this@BlockTimeActivity) { result ->
            try {
                //Process os success response
                val response = JSONObject(result)
                if (response.getInt("status_code") == 200) {
                    dialog.dismiss()
                    addNew.visibility = View.VISIBLE
                    blockTimeListModelList!!.clear()
                    val jsonRes = response.optJSONObject("response")
                    val userArr = jsonRes!!.getJSONArray("response")
                    if (userArr.length() > 0) {
                        for (i in 0 until userArr.length()) {
                            val tempobj = userArr.getJSONObject(i)
                            val startDate = dateFormat(
                                tempobj.getString("start_date"),
                                "yyyy-MM-dd",
                                "dd MMM,yy"
                            )
                            val endDate = dateFormat(
                                tempobj.getString("end_date"),
                                "yyyy-MM-dd",
                                "dd MMM,yy"
                            )
                            val startTime = appUtilities.formatTimeBasedOnPreference(
                                applicationContext, "HH:mm:ss", tempobj.getString("start_time")
                            )
                            val endTime = appUtilities.formatTimeBasedOnPreference(
                                applicationContext, "HH:mm:ss", tempobj.getString("end_time")
                            )
                            val reason = tempobj.getString("reason")
                            val service = tempobj.getInt("service")
                            val status = tempobj.getInt("status")
                            val id = tempobj.getInt("id")
                            val doctorId = tempobj.getInt("doctor_id")
                            val serviceId = tempobj.getInt("service_id")
                            val productId = tempobj.getInt("product_id")
                            val active = tempobj.getBoolean("active")
                            val createdAt = tempobj.getString("created_at")
                            val updatedAt = tempobj.getString("updated_at")
                            val settingTimeBlockModel = BlockTimeListModel()
                            settingTimeBlockModel.startDate = startDate
                            settingTimeBlockModel.endDate = endDate
                            settingTimeBlockModel.startTime = startTime
                            settingTimeBlockModel.endTime = endTime
                            settingTimeBlockModel.serviceType = service
                            settingTimeBlockModel.status = status
                            settingTimeBlockModel.reason = reason
                            settingTimeBlockModel.id = id
                            settingTimeBlockModel.doctorId = doctorId
                            settingTimeBlockModel.serviceId = serviceId
                            settingTimeBlockModel.productId = productId
                            settingTimeBlockModel.active = active
                            settingTimeBlockModel.createdAt = createdAt
                            settingTimeBlockModel.updatedAt = updatedAt
                            blockTimeListModelList!!.add(settingTimeBlockModel)
                        }
                        blockTimeListAdapter!!.notifyDataSetChanged()
                    } else {
                        if (blockTimeListModelList!!.size == 0) {
                            emptyTv.visibility = View.VISIBLE
                            addNew2.visibility = View.VISIBLE
                            addNew.visibility = View.GONE

                            //sending broadcast
                            val intent = Intent(CUSTOM_BROADCAST_ACTION)
                            intent.putExtra("Activity", "BlockTime")
                            /*set the package name for broadcast and changes the custom_broadcast_action string value to normal string*/intent.setPackage(
                                packageName
                            )
                            sendBroadcast(intent)
                        }
                    }
                } else {
                    dialog.dismiss()
                    errorHandler(this@BlockTimeActivity, result)
                }
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
                dialog.dismiss()
            }
        }
    }


    private fun deleteDoctorTimeBlock(
        position: Int,
        timeBlockJsonObject: JSONObject?,
        clickType: String
    ) {
        showCustomProgressAlertDialog(resources.getString(R.string.updating))
        if (clickType.equals("DELETE", ignoreCase = true)) {
            url = ApiUrls.deleteDoctorTimeBlock
        } else if (clickType.equals("SWITCH", ignoreCase = true)) {
            url = ApiUrls.changeDoctorTimeBlockStatus
        }

        viewModel.deleteDoctorTimeBlock(url, timeBlockJsonObject)
            .observe(this@BlockTimeActivity) { result ->
                try {
                    //Process os success response
                    val response = JSONObject(result)
                    if (response.getInt("status_code") == 200) {
                        dialog.dismiss()
                        getDoctorBlockTime()
                        /*val res = response.optJSONObject("response")
                        val responseValue = res!!.optInt("response")*/
                    } else {
                        dialog.dismiss()
                        errorHandler(this@BlockTimeActivity, result)
                    }
                } catch (e: JSONException) {
                    dialog.dismiss()
                    e.printStackTrace()
                }
            }
    }


    private fun getDoctorServiceForTimeBlock() {

        viewModel.doctorServiceForTimeBlock.observe(this@BlockTimeActivity) { result ->
            try {
                //Process os success response
                val response = JSONObject(result)
                serviceName!!.clear()
                if (response.getInt("status_code") == 200) {
                    val jsonRes = response.optJSONObject("response")
                    val userArr = jsonRes!!.getJSONArray("response")
                    if (userArr.length() > 0) {
                        var settingTimeBlockModel = BlockTimeListModel()
                        settingTimeBlockModel.serviceType = 0
                        settingTimeBlockModel.serviceNameString = "All"
                        settingTimeBlockModel.status = 0
                        settingTimeBlockModel.id = 0
                        settingTimeBlockModel.doctorId = ApiUrls.doctorId
                        settingTimeBlockModel.serviceId = 0
                        settingTimeBlockModel.productId = 0
                        settingTimeBlockModel.userId = 0
                        serviceName!!.add(settingTimeBlockModel)
                        for (i in 0 until userArr.length()) {
                            val tempobj = userArr.getJSONObject(i)

                            if (!tempobj.opt("productinfo")!!.toString()
                                    .equals("null", ignoreCase = true)
                            ) {
                                val productinfoArray = tempobj.getJSONObject("productinfo")
                                val service = tempobj.getInt("service_id")
                                val status = productinfoArray.getInt("status")
                                val id = tempobj.getInt("id")
                                val doctorId = tempobj.getInt("user_id")
                                val serviceId = productinfoArray.getInt("dr_service_id")
                                val productId = productinfoArray.getInt("id")
                                val userId = tempobj.getInt("user_id")
                                val alias = tempobj.getString("alias")
                                settingTimeBlockModel = BlockTimeListModel()
                                settingTimeBlockModel.serviceType = service
                                settingTimeBlockModel.serviceNameString = alias
                                settingTimeBlockModel.status = status
                                settingTimeBlockModel.id = id
                                settingTimeBlockModel.doctorId = doctorId
                                settingTimeBlockModel.serviceId = serviceId
                                settingTimeBlockModel.productId = productId
                                settingTimeBlockModel.userId = userId
                                serviceName!!.add(settingTimeBlockModel)
                            }
                        }
                        blockTimeServiceAdapter!!.notifyDataSetChanged()
                    } else {
                        androidx.appcompat.app.AlertDialog.Builder(this@BlockTimeActivity)
                            .setTitle("No Service Found")
                            .setMessage("It seems you have not yet created any service so you won't be able to block any time.")
                            .setPositiveButton(
                                "Set-up Service"
                            ) { dialog, _ ->
                                dialog.dismiss()
                                addNewLayout.visibility = View.GONE
                                timeListLayout.visibility = View.VISIBLE
                                emptyTv.visibility = View.GONE
                                addNew2.visibility = View.GONE
                                addNew.visibility = View.VISIBLE
                                if (blockTimeListModelList!!.size == 0) {
                                    emptyTv.visibility = View.VISIBLE
                                    addNew2.visibility = View.VISIBLE
                                    addNew.visibility = View.GONE
                                }
                                val intent = Intent(
                                    this@BlockTimeActivity,
                                    SettingsFormActivity::class.java
                                )
                                intent.putExtra("FormType", 7)
                                intent.putExtra(
                                    "Title",
                                    resources.getString(R.string.service_setup)
                                )
                                startActivity(intent)
                            }
                            .setNegativeButton(
                                "Ok"
                            ) { dialogInterface, i ->
                                dialogInterface.dismiss()
                                addNewLayout.visibility = View.GONE
                                timeListLayout.visibility = View.VISIBLE
                                emptyTv.visibility = View.GONE
                                addNew2.visibility = View.GONE
                                addNew.visibility = View.VISIBLE
                                if (blockTimeListModelList!!.size == 0) {
                                    emptyTv.visibility = View.VISIBLE
                                    addNew2.visibility = View.VISIBLE
                                    addNew.visibility = View.GONE
                                }
                            }
                            .show()
                    }
                } else {
                    errorHandler(this@BlockTimeActivity, result)
                }


            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
        }
    }


    private fun saveTimeBlock() {
        showCustomProgressAlertDialog(resources.getString(R.string.updating))
       // val url = ApiUrls.saveDoctorServiceTimeBlock
        try {
            jsonValue = JSONObject()
            jsonValue.put("doctor_id", selectedDoctorId)
            jsonValue.put(
                "start_date",
                dateFormat(startDate.text.toString(), "dd MMM,yy", "yyyy-MM-dd")
            )
            jsonValue.put(
                "end_date",
                dateFormat(endDate.text.toString(), "dd MMM,yy", "yyyy-MM-dd")
            )
            jsonValue.put("reason", reasonText.text.toString())
            jsonValue.put("service", selectedService)
            jsonValue.put("service_id", selectedServiceID)
            jsonValue.put("product_id", selectedProductID)
            jsonValue.put("start_time", startTime.hint.toString())
            jsonValue.put("end_time", endTime.hint.toString())
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        viewModel.saveTimeBlock(jsonValue).observe(this@BlockTimeActivity) { result ->
            try {
                //Process os success response
                val response = JSONObject(result)
                if (response.getInt("status_code") == 200) {
                    dialog.dismiss()
                    getDoctorBlockTime()
                    if (addNewLayout.visibility == View.VISIBLE) {
                        addNewLayout.visibility = View.GONE
                        timeListLayout.visibility = View.VISIBLE
                    }
                    /*
                    val resObj=response.optJSONObject("response")
                    val myJson = JSONObject(resObj!!.toString())
                    val responseValue = myJson.optInt("response")*/
                } else {
                    dialog.dismiss()
                    errorHandler(this@BlockTimeActivity, result)
                }
            } catch (e: JSONException) {
                e.printStackTrace()
                dialog.dismiss()
            }
        }
    }


    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        super.getOnBackPressedDispatcher().onBackPressed()
        if (addNewLayout.visibility == View.VISIBLE) {
            addNewLayout.visibility = View.GONE
            timeListLayout.visibility = View.VISIBLE
            emptyTv.visibility = View.GONE
            addNew2.visibility = View.GONE
            addNew.visibility = View.VISIBLE
            if (blockTimeListModelList!!.size == 0) {
                emptyTv.visibility = View.VISIBLE
                addNew2.visibility = View.VISIBLE
                addNew.visibility = View.GONE
            }
        } else {
            finish()
        }
    }

    private fun dateFormat(dateStr: String, inputFormat: String, outputFormat: String): String {
        var dataStrDup = dateStr
        return try {
            // String dateStr = "21/20/2011";
            val srcDf: DateFormat = SimpleDateFormat(inputFormat, Locale.ENGLISH)
            // parse the date string into Date object
            val date = srcDf.parse(dataStrDup)
            val destDf: DateFormat = SimpleDateFormat(outputFormat, Locale.ENGLISH)
            // format the date into another format
            dataStrDup = destDf.format(date!!)
            println("Converted date is : $dataStrDup")
            dataStrDup
        } catch (e: ParseException) {
            e.printStackTrace()
            ""
        }
    }

    private fun showCustomDialog() {
        //before inflating the custom alert dialog layout, we will get the current activity viewgroup
        val viewGroup = findViewById<ViewGroup>(android.R.id.content)

        //then we will inflate the custom alert dialog xml that we created
        val dialogView =
            LayoutInflater.from(this).inflate(R.layout.dialog_block_time_confirm, viewGroup, false)
        val yes = dialogView.findViewById<TextView>(R.id.yes)
        val no = dialogView.findViewById<TextView>(R.id.no)

        //Now we need an AlertDialog.Builder object
        val builder = AlertDialog.Builder(this)

        //setting the view of the builder to our custom view that we already inflated
        builder.setView(dialogView)

        //finally creating the alert dialog and displaying it
        val alertDialog = builder.create()
        alertDialog.show()
        yes.setOnClickListener {
            alertDialog.dismiss()
            ZohoSalesIQ.Tracking.setPageTitle("Settings - Time Block - Adding New")
            saveTimeBlock()
        }
        no.setOnClickListener {
            ZohoSalesIQ.Tracking.setPageTitle("Settings - Time Block - Cancel")
            alertDialog.dismiss()
        }
    }


    private fun checkTimings(time: String, endTime: String): Boolean {
        val pattern = "HH:mm"
        val sdf = SimpleDateFormat(pattern, Locale.ENGLISH)
        try {
            val date1 = sdf.parse(time)
            val date2 = sdf.parse(endTime)
            return date1!!.before(date2)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return false
    }


    private fun showGuide(section: Int) {
        when (section) {
            1 -> if (!appPreference.getBoolean("TimeBlockSetup", false)) {
                MaterialIntroView.Builder(this)
                    .enableDotAnimation(true)
                    .enableIcon(false)
                    .dismissOnTouch(true)
                    .setFocusGravity(FocusGravity.CENTER)
                    .setFocusType(Focus.NORMAL)
                    .setDelayMillis(50)
                    .enableFadeAnimation(true)
                    .setInfoText("Select on of your service to block your time")
                    .setShape(ShapeType.RECTANGLE)
                    .setTarget(serviceRecycleView)
                    .setUsageId("intro_serviceRecycleViewTwo") //THIS SHOULD BE UNIQUE ID
                    .setListener {
                        showGuide(2)
                        val editor = appPreference.edit()
                        editor.putBoolean("TimeBlockSetup", true)
                        editor.apply()
                    }
                    .show()
            }
            2 -> MaterialIntroView.Builder(this)
                .enableDotAnimation(true)
                .enableIcon(false)
                .dismissOnTouch(true)
                .setFocusGravity(FocusGravity.CENTER)
                .setFocusType(Focus.NORMAL)
                .setDelayMillis(50)
                .enableFadeAnimation(true)
                .setInfoText("Select from which date till which date you want to block your time for the service")
                .setShape(ShapeType.RECTANGLE)
                .setTarget(dateLayout)
                .setUsageId("intro_dateLayoutTwo") //THIS SHOULD BE UNIQUE ID
                .setListener { showGuide(3) }
                .show()
            3 -> MaterialIntroView.Builder(this)
                .enableDotAnimation(true)
                .enableIcon(false)
                .dismissOnTouch(true)
                .setFocusGravity(FocusGravity.CENTER)
                .setFocusType(Focus.NORMAL)
                .setDelayMillis(50)
                .enableFadeAnimation(true)
                .setInfoText("Enter a particular reason for your not being available. This is optional")
                .setShape(ShapeType.RECTANGLE)
                .setTarget(reasonText)
                .setUsageId("intro_reasonTextTwo") //THIS SHOULD BE UNIQUE ID
                .setListener { }
                .show()
        }
    }

    private fun showCustomProgressAlertDialog(title: String) {
        val builder = AlertDialog.Builder(this@BlockTimeActivity)
        builder.setTitle(title)
        builder.setCancelable(false)
        val customLayout: View = layoutInflater.inflate(R.layout.custom_progress_bar, null)
        builder.setView(customLayout)
        dialog = builder.create()
        dialog.show()
    }
}