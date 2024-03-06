package com.whitecoats.clinicplus.trainingschedule

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.PorterDuff
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.android.volley.Request
import com.whitecoats.clinicplus.R
import com.whitecoats.clinicplus.VolleyCallback
import com.whitecoats.clinicplus.apis.ApiGetPostMethodCalls
import com.whitecoats.clinicplus.apis.ApiUrls
import com.whitecoats.clinicplus.utils.ErrorHandlerClass.errorHandler
import org.json.JSONException
import org.json.JSONObject


class TrainingScheduleActivity : AppCompatActivity() {
    private var otpLoading: ProgressDialog? = null
    private var otpLoading1: ProgressDialog? = null
    private var jsonValue: JSONObject? = null
    private val jsonValue1: JSONObject? = null
    var detailsHeaderText: TextView? = null
    private var upcomingTrainingRecycleView: RecyclerView? = null
    var upcomingTrainingListModelList: MutableList<upcomingTrainingScheduleListModel>? = null
    private var upcomingTrainingListAdapter: UpcomingTrainingScheduleListAdapter? = null
    private var mLayoutManager: RecyclerView.LayoutManager? = null
    private val trainingScheduleListModel: upcomingTrainingScheduleListModel? = null
    private var bookedTrainingRecycleView: RecyclerView? = null
    var bookedTrainingListModelList: MutableList<upcomingTrainingScheduleListModel>? = null
    private var bookedTrainingListAdapter: BookedTrainingScheduleListAdapter? = null
    private var mLayoutManagerBOoked: RecyclerView.LayoutManager? = null
    private var groupData: ArrayList<Int>? = null
    private val bookedTrainingScheduleListModel: upcomingTrainingScheduleListModel? = null
    var c = 0
    private var activeTab: RelativeLayout? = null
    private var pastTab: RelativeLayout? = null
    private var bookedTraining: RelativeLayout? = null
    private var upComingTraining: RelativeLayout? = null
    private var activeTabBottom: View? = null
    private var pastTabBottom: View? = null
    private var emptyText: TextView? = null
    private var swipeContainer: SwipeRefreshLayout? = null
    private var globalApiCall: ApiGetPostMethodCalls? = null
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_training_schedule)
        val toolbar = findViewById<Toolbar>(R.id.trainingScheduleToolbar)
        toolbar.title = "Training Schedule"
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        val upArrow = ContextCompat.getDrawable(this, R.drawable.ic_arrow_back)
        upArrow?.setColorFilter(ContextCompat.getColor(applicationContext, R.color.colorWhite), PorterDuff.Mode.SRC_ATOP)
        supportActionBar!!.setHomeAsUpIndicator(upArrow)
        activeTab = findViewById(R.id.appointActiveTab)
        pastTab = findViewById(R.id.appointPastTab)
        activeTabBottom = findViewById(R.id.appointTabActiveBottom)
        pastTabBottom = findViewById(R.id.appointTabPastBottom)
        bookedTraining = findViewById(R.id.bookedTraining)
        upComingTraining = findViewById(R.id.upComingTraining)
        emptyText = findViewById(R.id.appointEmptyText)
        swipeContainer = findViewById(R.id.appointmentSwipeContainer)
        detailsHeaderText = findViewById(R.id.detailsHeaderText)
        mLayoutManager = LinearLayoutManager(applicationContext)
        mLayoutManagerBOoked = LinearLayoutManager(applicationContext)
        upcomingTrainingRecycleView = findViewById(R.id.trainingScheduleRecycleView)
        bookedTrainingRecycleView = findViewById(R.id.bookedTrainingRecycleView)
        groupData = ArrayList()
        globalApiCall = ApiGetPostMethodCalls()
        upcomingTrainingListModelList = ArrayList()
        bookedTrainingListModelList = ArrayList()
        bookedTraining!!.visibility = View.INVISIBLE
        upComingTraining!!.visibility = View.VISIBLE
        upcomingTrainingListAdapter = UpcomingTrainingScheduleListAdapter(
            applicationContext,
            upcomingTrainingListModelList as ArrayList<upcomingTrainingScheduleListModel>,
            this@TrainingScheduleActivity,
            groupData!!
        ) { v, loadMore, dataPosition, appointmentApptListModel ->
            if (loadMore.equals("LOADMORE", ignoreCase = true)) {
                pageNumber = pageNumber + 1
                if (pastTabBottom!!.visibility == View.VISIBLE) {
                    getUpcomingTraining("50", "", "", pageNumber.toString() + "", "", "")
                } else if (activeTabBottom!!.visibility == View.VISIBLE) {
                    getBookedTraining("50", "", "", pageNumber.toString() + "", "", "")
                }
            } else if (loadMore.equals("Description", ignoreCase = true)) {
                val builder = AlertDialog.Builder(this@TrainingScheduleActivity)
                builder.setTitle("Training Description")
                builder.setMessage(appointmentApptListModel.trainingDescription)
                builder.setPositiveButton("OK") { dialog, which -> //                            ZohoSalesIQ.Tracking.setCustomAction("ApptList - Cancel All Appt - Yes");
                    dialog.dismiss()
                }
                val alert = builder.create()
                alert.show()
            } else if (loadMore.equals("TaringBook", ignoreCase = true)) {
                val builder = AlertDialog.Builder(this@TrainingScheduleActivity)
                builder.setTitle("Training Book")
                builder.setMessage("Do you want to book the training?")
                builder.setPositiveButton("YES") { dialog, which -> //                            ZohoSalesIQ.Tracking.setCustomAction("ApptList - Cancel All Appt - Yes");
                    if (rescheduleClick) {
                        rescheduleClick = false
                        BookedTrainingReschedule(
                            rescheduleId,
                            appointmentApptListModel.id,
                            dataPosition
                        )
                    } else {
                        BookTraining(appointmentApptListModel.id, dataPosition)
                    }
                    dialog.dismiss()
                }
                builder.setNegativeButton("NO") { dialog, which -> // Do nothing
                    dialog.dismiss()
                }
                val alert = builder.create()
                alert.show()
            }
        }
        upcomingTrainingRecycleView!!.layoutManager = mLayoutManager
        upcomingTrainingRecycleView!!.itemAnimator = DefaultItemAnimator()
        upcomingTrainingRecycleView!!.adapter = upcomingTrainingListAdapter
        bookedTrainingListAdapter = BookedTrainingScheduleListAdapter(
            applicationContext,
            bookedTrainingListModelList as ArrayList<upcomingTrainingScheduleListModel>,
            this@TrainingScheduleActivity,
            groupData!!
        ) { v, loadMore, dataPosition, appointmentApptListModel ->
            if (loadMore.equals("LOADMORE", ignoreCase = true)) {
                pageNumber = pageNumber + 1
                if (pastTabBottom!!.visibility == View.VISIBLE) {
                    getUpcomingTraining("50", "", "", pageNumber.toString() + "", "", "")
                } else if (activeTabBottom!!.visibility == View.VISIBLE) {
                    getBookedTraining("50", "", "", pageNumber.toString() + "", "", "")
                }
            } else if (loadMore.equals("Description", ignoreCase = true)) {
                val builder = AlertDialog.Builder(this@TrainingScheduleActivity)
                builder.setTitle("Training Description")
                builder.setMessage(appointmentApptListModel.trainingDescription)
                builder.setPositiveButton("OK") { dialog, which ->
                    dialog.dismiss()
                }
                val alert = builder.create()
                alert.show()
            } else if (loadMore.equals("TaringBook", ignoreCase = true)) {
                val builder = AlertDialog.Builder(this@TrainingScheduleActivity)
                builder.setTitle("Book Training")
                builder.setMessage("Do you want to book the training?")
                builder.setPositiveButton("OK") { dialog, which ->
                    dialog.dismiss()
                }
                builder.setNegativeButton("NO") { dialog, which -> // Do nothing
                    dialog.dismiss()
                }
                val alert = builder.create()
                alert.show()
            } else if (loadMore.equals("CANCEL", ignoreCase = true)) {
                val builder = AlertDialog.Builder(this@TrainingScheduleActivity)
                builder.setTitle("Cancel")
                builder.setMessage("Do you want to cancel the training?")
                builder.setPositiveButton("YES") { dialog, which ->
                    cancelBookedTraining(
                        appointmentApptListModel.id,
                        appointmentApptListModel.trainingScheduleId,
                        dataPosition
                    )
                    dialog.dismiss()
                }
                builder.setNegativeButton("NO") { dialog, which -> // Do nothing
                    dialog.dismiss()
                }
                val alert = builder.create()
                alert.show()
            } else if (loadMore.equals("RESCHEDULE", ignoreCase = true)) {
                val builder = AlertDialog.Builder(this@TrainingScheduleActivity)
                builder.setTitle("Reschedule")
                builder.setMessage("Do you want to reschedule the training?")
                builder.setPositiveButton("YES") { dialog, which ->

                    activeTabBottom!!.setVisibility(View.INVISIBLE)
                    pastTabBottom!!.setVisibility(View.VISIBLE)
                    bookedTraining!!.setVisibility(View.INVISIBLE)
                    upComingTraining!!.setVisibility(View.VISIBLE)
                    rescheduleClick = true
                    rescheduleId = appointmentApptListModel.id
                    (upcomingTrainingListModelList as ArrayList<upcomingTrainingScheduleListModel>).clear()
                    getUpcomingTraining("50", "", "", pageNumber.toString() + "", "", "")
                    dialog.dismiss()
                }
                builder.setNegativeButton("NO") { dialog, which -> // Do nothing
                    dialog.dismiss()
                }
                val alert = builder.create()
                alert.show()
            }
        }
        bookedTrainingRecycleView!!.layoutManager = mLayoutManagerBOoked
        bookedTrainingRecycleView!!.itemAnimator = DefaultItemAnimator()
        bookedTrainingRecycleView!!.adapter = bookedTrainingListAdapter


        //on active tab click
        activeTab?.setOnClickListener(View.OnClickListener {
            activeTabBottom!!.visibility = View.VISIBLE
            pastTabBottom!!.visibility = View.INVISIBLE
            bookedTraining!!.visibility = View.VISIBLE
            upComingTraining!!.visibility = View.INVISIBLE
            (bookedTrainingListModelList as ArrayList<upcomingTrainingScheduleListModel>).clear()
            getBookedTraining("50", "", "", pageNumber.toString() + "", "", "")
        })
        pastTab?.setOnClickListener(View.OnClickListener {
            activeTabBottom!!.visibility = View.INVISIBLE
            pastTabBottom!!.visibility = View.VISIBLE
            bookedTraining!!.visibility = View.INVISIBLE
            upComingTraining!!.visibility = View.VISIBLE
            (upcomingTrainingListModelList as ArrayList<upcomingTrainingScheduleListModel>).clear()
            getUpcomingTraining("50", "", "", pageNumber.toString() + "", "", "")

        })
    }

    private val data: Unit
        private get() {
            var trainingScheduleListModel = upcomingTrainingScheduleListModel()
            trainingScheduleListModel.trainingTitle = "first training"
            trainingScheduleListModel.trainingDescription = "testdescription"
            trainingScheduleListModel.startTrainingSlot = "20:10-19:10"
            upcomingTrainingListModelList!!.add(trainingScheduleListModel)
            trainingScheduleListModel = upcomingTrainingScheduleListModel()
            trainingScheduleListModel.trainingTitle = "second training"
            trainingScheduleListModel.trainingDescription = "secondtestdescription"
            trainingScheduleListModel.startTrainingSlot = "20:10-19:10"
            upcomingTrainingListModelList!!.add(trainingScheduleListModel)
            trainingScheduleListModel = upcomingTrainingScheduleListModel()
            trainingScheduleListModel.trainingTitle = "thrid training"
            trainingScheduleListModel.trainingDescription = "thridtestdescription"
            trainingScheduleListModel.startTrainingSlot = "20:10-19:10"
            upcomingTrainingListModelList!!.add(trainingScheduleListModel)
            //        appointmentApptListAdapter.notifyDataSetChanged();
        }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // handle arrow click here
        if (item.itemId == android.R.id.home) {
            finish()
        }
        return super.onOptionsItemSelected(item)
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

    fun getUpcomingTraining(
        perPage: String, sortBy: String?, search: String, pageNum: String,
        status: String, shortOrder: String?
    ) {
        otpLoading = ProgressDialog(this@TrainingScheduleActivity)
        otpLoading!!.setMessage(resources.getString(R.string.wait_while_we_fetching))
        otpLoading!!.setProgressStyle(ProgressDialog.STYLE_SPINNER)
        otpLoading!!.setCancelable(false)
        otpLoading!!.show()
        val URL =
            ApiUrls.getUpcomingTraining + "?per_page=" + perPage + "&sort_by=" + "created_at" + "&search=" + search +
                    "&page=" + pageNum + "&status=" + status + "&sortorder=" + "asc"
        globalApiCall!!.volleyApiRequestData(
            URL,
            Request.Method.GET,
            null,
            this,
            object : VolleyCallback {
                override fun onSuccess(result: String) {
                    try {
                        val response = JSONObject(result)
                        //Process os success response
                        otpLoading!!.dismiss()
                        emptyText!!.visibility = View.GONE
                        val rootObj = response.getJSONObject("response")
                        val upcomingTrainingArray = rootObj.getJSONArray("data")
                        if (upcomingTrainingArray.length() > 0) {
                            for (j in 0 until upcomingTrainingArray.length()) {
                                val upcomingTrainingObject = upcomingTrainingArray.getJSONObject(j)
                                val model = upcomingTrainingScheduleListModel()
                                model.id = upcomingTrainingObject.getInt("id")
                                model.status = upcomingTrainingObject.getInt("status")
                                model.trainingTitle = upcomingTrainingObject.getString("title")
                                model.trainingDescription =
                                    upcomingTrainingObject.getString("description")
                                model.startTrainingSlot =
                                    upcomingTrainingObject.getString("start_date_time")
                                model.endTrainingSlot =
                                    upcomingTrainingObject.getString("end_date_time")
                                upcomingTrainingListModelList!!.add(model)
                            }
                            upcomingTrainingListAdapter!!.notifyDataSetChanged()
                            isMoreData = upcomingTrainingListModelList!!.size < rootObj.getInt("total")
                        } else {
                            emptyText!!.visibility = View.VISIBLE
                            emptyText!!.text =
                                "No trainings are currently available. Please reach us via the Support section if you need any assistance."
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        otpLoading!!.dismiss()
                    }
                }

                override fun onError(err: String) {
                    otpLoading!!.dismiss()
                    errorHandler(this@TrainingScheduleActivity, err)
                }
            })
    }

    fun getBookedTraining(
        perPage: String, sortBy: String?, search: String, pageNum: String,
        status: String, shortOrder: String?
    ) {
        otpLoading1 = ProgressDialog(this@TrainingScheduleActivity)
        otpLoading1!!.setMessage(resources.getString(R.string.wait_while_we_fetching))
        otpLoading1!!.setProgressStyle(ProgressDialog.STYLE_SPINNER)
        otpLoading1!!.setCancelable(false)
        otpLoading1!!.show()

        val URL =
            ApiUrls.getBookedTraining + "?per_page=" + perPage + "&sort_by=" + "booked_at" + "&search=" + search +
                    "&page=" + pageNum + "&status=" + status + "&sort_order=" + "desc"
        globalApiCall!!.volleyApiRequestData(
            URL,
            Request.Method.GET,
            null,
            this,
            object : VolleyCallback {
                override fun onSuccess(result: String) {
                    try {
                        val response = JSONObject(result)
                        //Process os success response
                        Log.d("bookedTraingResponse", response.toString())
                        otpLoading1!!.dismiss()
                        emptyText!!.visibility = View.GONE
                        val rootObj = response.getJSONObject("response")
                        val upcomingTrainingArray = rootObj.getJSONArray("data")
                        if (upcomingTrainingArray.length() > 0) {
                            for (j in 0 until upcomingTrainingArray.length()) {
                                val upcomingTrainingObject = upcomingTrainingArray.getJSONObject(j)
                                //                                Log.d("Date *****", appointmentJsonObject.getString("date"));
                                val appointmentObjectTwo =
                                    upcomingTrainingObject.getJSONArray("training_schedules")
                                for (i in 0 until appointmentObjectTwo.length()) {
                                    groupData!!.add(c, i)
                                    //                                    Log.d("Group %%%%%%", groupData.get(c) + " " + c);
                                    c++
                                    val tempobj = appointmentObjectTwo.getJSONObject(i)
                                    val model = upcomingTrainingScheduleListModel()
                                    model.id = upcomingTrainingObject.getInt("id")
                                    model.trainingScheduleId =
                                        upcomingTrainingObject.getInt("training_schedule_id")
                                    model.status = upcomingTrainingObject.getInt("status")
                                    model.trainingTitle = tempobj.getString("title")
                                    model.trainingDescription = tempobj.getString("description")
                                    model.startTrainingSlot = tempobj.getString("start_date_time")
                                    model.endTrainingSlot = tempobj.getString("end_date_time")
                                    model.groupNo = j
                                    model.trainingDate =
                                        upcomingTrainingObject.getString("booked_at")
                                    bookedTrainingListModelList!!.add(model)
                                }
                            }
                            bookedTrainingListAdapter!!.notifyDataSetChanged()
                            isMoreData = bookedTrainingListModelList!!.size < rootObj.getInt("total")
                        } else {
                            emptyText!!.visibility = View.VISIBLE
                            emptyText!!.text = "No data found."
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        otpLoading!!.dismiss()
                    }
                }

                override fun onError(err: String) {
                    errorHandler(this@TrainingScheduleActivity, err)
                    otpLoading!!.dismiss()
                }
            })
    }

    fun cancelBookedTraining(id: Int, trainingScheduleId: Int, dataPosition: Int) {
        otpLoading = ProgressDialog(this@TrainingScheduleActivity)
        otpLoading!!.setMessage(resources.getString(R.string.wait_while_we_updating))
        otpLoading!!.setTitle(resources.getString(R.string.common_please_wait_loading_message))
        otpLoading!!.setProgressStyle(ProgressDialog.STYLE_SPINNER)
        otpLoading!!.setCancelable(false)
        otpLoading!!.show()
        val URL = ApiUrls.cancelTraining
        try {
            jsonValue = JSONObject()
            jsonValue!!.put("id", id)
            jsonValue!!.put("training_schedule_id", trainingScheduleId)
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
                        Log.d("trainingcancelResponse", response.toString())
                        otpLoading!!.dismiss()
                        val responseValue = response.getBoolean("response")
                        if (responseValue) {
                            Toast.makeText(
                                this@TrainingScheduleActivity,
                                "Your training has been cancelled successfully",
                                Toast.LENGTH_SHORT
                            ).show()
                            bookedTrainingListModelList!!.clear()
                            getBookedTraining("50", "", "", pageNumber.toString() + "", "", "")
                            val intent = Intent()
                            // Here you can also put data on intent
                            intent.action = "GET_ONLINE_TRAINING"
                            sendBroadcast(intent)
                        } else {
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        otpLoading!!.dismiss()
                    }
                }

                override fun onError(err: String) {
                    otpLoading!!.dismiss()
                    errorHandler(this@TrainingScheduleActivity, err)
                }
            })
    }

    fun BookTraining(id: Int, dataPosition: Int) {
        otpLoading = ProgressDialog(this@TrainingScheduleActivity)
        otpLoading!!.setMessage(resources.getString(R.string.wait_while_we_updating))
        otpLoading!!.setTitle(resources.getString(R.string.common_please_wait_loading_message))
        otpLoading!!.setProgressStyle(ProgressDialog.STYLE_SPINNER)
        otpLoading!!.setCancelable(false)
        otpLoading!!.show()

        val URL = ApiUrls.bookTraining
        try {
            jsonValue = JSONObject()
            jsonValue!!.put("training_schedule_id", id)
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
                        Log.d("trainingbookResponse", response.toString())
                        otpLoading!!.dismiss()
                        val responseValue = response.getBoolean("response")
                        if (responseValue) {
                            Toast.makeText(
                                this@TrainingScheduleActivity,
                                "Your training has been booked successfully",
                                Toast.LENGTH_SHORT
                            ).show()
                            activeTabBottom!!.visibility = View.VISIBLE
                            pastTabBottom!!.visibility = View.INVISIBLE
                            bookedTraining!!.visibility = View.VISIBLE
                            upComingTraining!!.visibility = View.INVISIBLE
                            bookedTrainingListModelList!!.clear()
                            getBookedTraining("50", "", "", pageNumber.toString() + "", "", "")
                            val intent = Intent()
                            // Here you can also put data on intent
                            intent.action = "GET_ONLINE_TRAINING"
                            sendBroadcast(intent)
                        } else {
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        otpLoading!!.dismiss()
                    }
                }

                override fun onError(err: String) {
                    otpLoading!!.dismiss()
                    errorHandler(this@TrainingScheduleActivity, err)
                }
            })
    }

    fun BookedTrainingReschedule(id: Int, trainingScheduleId: Int, dataPosition: Int) {
        otpLoading = ProgressDialog(this@TrainingScheduleActivity)
        otpLoading!!.setMessage(resources.getString(R.string.wait_while_we_updating))
        otpLoading!!.setTitle(resources.getString(R.string.common_please_wait_loading_message))
        otpLoading!!.setProgressStyle(ProgressDialog.STYLE_SPINNER)
        otpLoading!!.setCancelable(false)
        otpLoading!!.show()
        val URL = ApiUrls.bookedTrainingReschedule
        try {
            jsonValue = JSONObject()
            jsonValue!!.put("id", id)
            jsonValue!!.put("training_schedule_id", trainingScheduleId)
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
                        Log.d("trainingbookResponse", response.toString())
                        otpLoading!!.dismiss()
                        val responseValue = response.getBoolean("response")
                        if (responseValue) {
                            Toast.makeText(
                                this@TrainingScheduleActivity,
                                "Your training has been rescheduled successfully",
                                Toast.LENGTH_SHORT
                            ).show()
                            activeTabBottom!!.visibility = View.VISIBLE
                            pastTabBottom!!.visibility = View.INVISIBLE
                            bookedTraining!!.visibility = View.VISIBLE
                            upComingTraining!!.visibility = View.INVISIBLE
                            bookedTrainingListModelList!!.clear()
                            rescheduleId = 0
                            getBookedTraining("50", "", "", pageNumber.toString() + "", "", "")
                            val intent = Intent()
                            // Here you can also put data on intent
                            intent.action = "GET_ONLINE_TRAINING"
                            sendBroadcast(intent)
                        } else {
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        otpLoading!!.dismiss()
                    }
                }

                override fun onError(err: String) {
                    otpLoading!!.dismiss()
                    errorHandler(this@TrainingScheduleActivity, err)
                }
            })
    }

    override fun onResume() {
        super.onResume()
        if (pastTabBottom!!.visibility == View.VISIBLE) {
            upcomingTrainingListModelList!!.clear()
            getUpcomingTraining("50", "", "", pageNumber.toString() + "", "", "")
        } else if (activeTabBottom!!.visibility == View.VISIBLE) {
            bookedTrainingListModelList!!.clear()
            getBookedTraining("50", "", "", pageNumber.toString() + "", "", "")
        }
    }

    companion object {
        @JvmField
        var isMoreData = false
        var pageNumber = 1
        var rescheduleClick = false
        var rescheduleId = 0
    }
}