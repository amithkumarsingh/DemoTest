package com.whitecoats.clinicplus.autofollowup

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.PorterDuff
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import co.mobiwise.materialintro.shape.Focus
import co.mobiwise.materialintro.shape.FocusGravity
import co.mobiwise.materialintro.shape.ShapeType
import co.mobiwise.materialintro.view.MaterialIntroView
import com.android.volley.Request
import com.android.volley.toolbox.Volley
import com.whitecoats.clinicplus.*
import com.whitecoats.clinicplus.apis.ApiGetPostMethodCalls
import com.whitecoats.clinicplus.apis.ApiUrls
import com.whitecoats.clinicplus.utils.ErrorHandlerClass.errorHandler
import com.whitecoats.model.AppointmentSlotListModel
import com.whitecoats.model.AutoFollowUpModel
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class AutoFollowUpActivity : AppCompatActivity() {
    private var toolbar: Toolbar? = null
    private var followUpRv: RecyclerView? = null
    private var setPref: TextView? = null

    //    private Switch followUpSwt;
    private var apiRequests: AppointmentApiRequests? = null
    private var infoIcon: ImageView? = null
    private var loadingDialog: ProgressDialog? = null
    private var appPreference: SharedPreferences? = null


    private var groupData: ArrayList<Int>? = null
    var c = 0
    private var apiCall: PatientRecordsApi? = null
    private var loader: ProgressBar? = null
    private var searchView: SearchView? = null
    private var searchQuery = ""
    private var menu: Menu? = null
    private var autoFollowUpFilterBottomSheetDialog: AutoFollowUpFilterBottomSheet? = null

    @JvmField
    var durationFilter = "All"

    @JvmField
    var conditionFilter = "All"

    @JvmField
    var instructionFilter = "All"

    @JvmField
    var followUpAppointmentBookedFilter = "All"
    var searchFilter = ""

    @JvmField
    var isFilterApplied = false
    private var globalApiCall: ApiGetPostMethodCalls? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auto_follow_up)
        val backArrow = ContextCompat.getDrawable(this, R.drawable.ic_arrow_back)
        backArrow?.setColorFilter(
            ContextCompat.getColor(applicationContext, R.color.colorWhite),
            PorterDuff.Mode.SRC_ATOP
        )
        toolbar = findViewById(R.id.followUpToolbar)
        toolbar!!.navigationIcon = backArrow // your drawable
        setSupportActionBar(toolbar)
        toolbar!!.setNavigationOnClickListener(View.OnClickListener {
            onBackPressedDispatcher.onBackPressed() // Implemented by activity
        })
        globalApiCall = ApiGetPostMethodCalls()


        followUpRv = findViewById(R.id.followUpRecycleView)
        apiRequests = AppointmentApiRequests()
        setPref = findViewById(R.id.followUpSetPref)
        infoIcon = findViewById(R.id.followUpInfoIcon)
        apiCall = PatientRecordsApi()
        appPreference = getSharedPreferences(ApiUrls.appSharedPref, MODE_PRIVATE)


        loader = findViewById(R.id.appointLoader)
        loader!!.visibility = View.VISIBLE
        emptyText = findViewById(R.id.appointEmptyText)
        //auto follow-up
        followUpRv = findViewById(R.id.followUpRecycleView)
        autoFollowUpListModelList = ArrayList()
        groupData = ArrayList()
        c = 0
        doctorServiceArrayList = ArrayList()
        doctorsDetails
        val patientListClickListner =
            PatientListClickListner { v, type, parameter, videoServiceId, clinicServiceId, instantVideoServiceId, chatServiceId, patientId, patientName, instantVideoObject, instantVideoInfoObject ->
                val separated =
                    parameter.split("_".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                if (type.equals("FILE_URL", ignoreCase = true)) {
                    getFileFromUrl(parameter)
                } else if (type.equals("PHONE", ignoreCase = true)) {

                    // phoneNumber = parameter;
                    callPhoneNumber = parameter
                    val builder = AlertDialog.Builder(this@AutoFollowUpActivity)
                    builder.setTitle("Call Confirm")
                    builder.setMessage("Are you sure?")
                    builder.setPositiveButton("YES") { dialog, which ->
                        onCall(callPhoneNumber)
                        dialog.dismiss()
                    }
                    builder.setNegativeButton("NO") { dialog, which -> // Do nothing
                        dialog.dismiss()
                    }
                    val alert = builder.create()
                    alert.show()
                } else if (type.equals("BOOK_APPT", ignoreCase = true)) {
                    autoFollowUpActivityFlag = 1

                    if (clinicServiceId == 1) {
                        BookAppointmentActivity.quickAppointmentFlag = 1
                        val intent = Intent(
                            this@AutoFollowUpActivity,
                            BookAppointmentTimeSlotActivity::class.java
                        )
                        intent.putExtra(
                            "doctorDetailsRootObjects",
                            doctorsDetailsRootObj.toString()
                        )
                        intent.putExtra("serviceId", videoServiceId)
                        intent.putExtra("patientId", patientId)
                        intent.putExtra("patientName", patientName)
                        intent.putExtra(
                            "quickAppointmentFlag",
                            BookAppointmentActivity.quickAppointmentFlag
                        )
                        intent.putExtra("clinicName", separated[0]) // added by dileep
                        intent.putExtra("followUpApptId", separated[1]) // added by dileep
                        startActivity(intent)
                    } else if (clinicServiceId == 3) {
                        BookAppointmentActivity.quickAppointmentFlag = 1
                        val intent = Intent(
                            this@AutoFollowUpActivity,
                            BookAppointmentTimeSlotActivity::class.java
                        )
                        intent.putExtra(
                            "doctorDetailsRootObjects",
                            doctorsDetailsRootObj.toString()
                        )
                        intent.putExtra("serviceId", clinicServiceId)
                        intent.putExtra("patientId", patientId)
                        intent.putExtra("patientName", patientName)
                        intent.putExtra(
                            "quickAppointmentFlag",
                            BookAppointmentActivity.quickAppointmentFlag
                        )
                        intent.putExtra("clinicName", separated[0]) // added by dileep
                        intent.putExtra("followUpApptId", separated[1]) // added by dileep


                        startActivity(intent)
                    }
                }
            }
        autoFollowUpAdapter = AutoFollowUpAdapter(
            this@AutoFollowUpActivity, this,
            autoFollowUpListModelList as ArrayList<AutoFollowUpModel>,
            groupData!!
        ) { v, type, parameter, videoServiceId, clinicServiceId, instantVideoServiceId, chatServiceId, patientId, patientName, instantVideoObject, instantVideoInfoObject ->
            val separated =
                parameter.split("_".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            if (type.equals("BOOK_APPT", ignoreCase = true)) {
                autoFollowUpActivityFlag = 1

                if (clinicServiceId == 1) {
                    BookAppointmentActivity.quickAppointmentFlag = 1
                    val intent = Intent(
                        this@AutoFollowUpActivity,
                        BookAppointmentTimeSlotActivity::class.java
                    )
                    intent.putExtra("doctorDetailsRootObjects", doctorsDetailsRootObj.toString())
                    intent.putExtra("serviceId", videoServiceId)
                    intent.putExtra("patientId", patientId)
                    intent.putExtra("patientName", patientName)
                    intent.putExtra(
                        "quickAppointmentFlag",
                        BookAppointmentActivity.quickAppointmentFlag
                    )
                    intent.putExtra("clinicName", separated[0]) // added by dileep
                    intent.putExtra("followUpApptId", separated[1]) // added by dileep
                    startActivity(intent)
                } else if (clinicServiceId == 3) {
                    BookAppointmentActivity.quickAppointmentFlag = 1
                    val intent = Intent(
                        this@AutoFollowUpActivity,
                        BookAppointmentTimeSlotActivity::class.java
                    )
                    intent.putExtra("doctorDetailsRootObjects", doctorsDetailsRootObj.toString())
                    intent.putExtra("serviceId", clinicServiceId)
                    intent.putExtra("patientId", patientId)
                    intent.putExtra("patientName", patientName)
                    intent.putExtra(
                        "quickAppointmentFlag",
                        BookAppointmentActivity.quickAppointmentFlag
                    )
                    intent.putExtra("clinicName", separated[0]) // added by dileep
                    intent.putExtra("followUpApptId", separated[1]) // added by dileep


                    startActivity(intent)
                }
            } else if (type.equals("FILE_URL", ignoreCase = true)) {
                getFileFromUrl(parameter)
            } else if (type.equals("PHONE", ignoreCase = true)) {

                // phoneNumber = parameter;
                callPhoneNumber = parameter
                val builder = AlertDialog.Builder(this@AutoFollowUpActivity)
                builder.setTitle("Call Confirm")
                builder.setMessage("Are you sure?")
                builder.setPositiveButton("YES") { dialog, which ->
                    onCall(callPhoneNumber)
                    dialog.dismiss()
                }
                builder.setNegativeButton("NO") { dialog, which -> // Do nothing
                    dialog.dismiss()
                }
                val alert = builder.create()
                alert.show()
            } else if (type.equals("LOADMORE", ignoreCase = true)) {
                pageNumberF = pageNumberF + 1
                loader!!.visibility = View.VISIBLE
                getFollowUpList(
                    "",
                    "fname",
                    "desc",
                    "",
                    "",
                    "",
                    "10",
                    pageNumberF.toString() + "",
                    ""
                )

                //  getPatientList("All", "fname", searchQuery, "asc", pageNumber + "", "10");
            } else if (type.equals("DETAILS", ignoreCase = true)) {
                val bottomSheetDialogFragment = AutoFollowUpDetailsBottomSheet()
                bottomSheetDialogFragment.setupConfig(
                    this@AutoFollowUpActivity,
                    (autoFollowUpListModelList as ArrayList<AutoFollowUpModel>).get(videoServiceId),
                    patientListClickListner
                )
                bottomSheetDialogFragment.show(
                    supportFragmentManager,
                    "Bottom Sheet Dialog Fragment"
                )
            }

            // getFileFromUrl(parameter);
        }
        val mLayoutManager: RecyclerView.LayoutManager = LinearLayoutManager(applicationContext)
        followUpRv!!.layoutManager = mLayoutManager
        followUpRv!!.itemAnimator = DefaultItemAnimator()
        followUpRv!!.adapter = autoFollowUpAdapter

        //String search, String sort_column, String sort_order, String is_follow_up_submitted, String is_feel_better,String is_appointment_booked, final String perPage,final String pageNum
        getFollowUpList("", "fname", "desc", "", "", "", "10", 1.toString() + "", "")
        setPref!!.setOnClickListener(View.OnClickListener {
            val intent = Intent(this@AutoFollowUpActivity, AutoFollowUpPreferenceSetup::class.java)
            startActivity(intent)
        })
        infoIcon!!.setOnClickListener(View.OnClickListener {
            androidx.appcompat.app.AlertDialog.Builder(this@AutoFollowUpActivity)
                .setTitle(resources.getString(R.string.follow_up))
                .setMessage(resources.getString(R.string.follow_up_info))
                .setPositiveButton("OK") { dialog, which -> dialog.dismiss() }
                .show()
        })
    }

    override fun onBackPressed() {
        super.getOnBackPressedDispatcher().onBackPressed()
        finish()
    }

    fun getFollowUpList(
        search: String,
        sort_column: String,
        sort_order: String,
        is_following_instructions: String,
        how_are_you_feeling: String,
        is_appointment_booked: String,
        perPage: String,
        page: String,
        created_at: String
    ) {
        loader!!.visibility = View.VISIBLE
        val url =
            ApiUrls.getFollowUpList + "?search=" + search + "&sort_column=" + sort_column + "&sort_order=" + sort_order + "&is_following_instructions=" + is_following_instructions + "&how_are_you_feeling=" + how_are_you_feeling + "&is_appointment_booked=" + is_appointment_booked + "&per_page=" + perPage + "&page=" + page + "&created_at=" + created_at
        apiRequests!!.getApptApiData(url, "", this, object : VolleyCallback {
            override fun onSuccess(result: String) {
                loader!!.visibility = View.GONE

                try {
                    val resObj = JSONObject(result)
                    val rootObj = resObj.getJSONObject("response")
                    val rootArray = rootObj.getJSONArray("data1")


                    if (rootArray.length() > 0) {
                        for (j in 0 until rootArray.length()) {
                            val appointmentJsonObject = rootArray.getJSONObject(j)
                            //                                Log.d("Date *****", appointmentJsonObject.getString("date"));
                            val appointmentObjectTwo =
                                appointmentJsonObject.getJSONArray("followUps")
                            for (i in 0 until appointmentObjectTwo.length()) {
                                groupData!!.add(c, i)
                                //                                    Log.d("Group %%%%%%", groupData.get(c) + " " + c);
                                c++
                                val tempobj = appointmentObjectTwo.getJSONObject(i)
                                val model = AutoFollowUpModel()
                                model.groupNo = j
                                model.apptDate = appointmentJsonObject.getString("date")
                                model.patientMessage = tempobj.getString("description")
                                model.followUpId = tempobj.getInt("id")
                                if (tempobj.isNull("how_are_you_feeling")) {
                                    model.conditionStatus = 0
                                } else {
                                    model.conditionStatus = tempobj.getInt("how_are_you_feeling")
                                }
                                if (tempobj.isNull("is_following_instructions")) {
                                    model.followInstructionStatus = 0
                                } else {
                                    model.followInstructionStatus =
                                        tempobj.getInt("is_following_instructions")
                                }
                                model.followUpDate = tempobj.getString("appt_datetime")
                                //                                model.setSubmissionDate(tempobj.getString("follow_up_submitted_on"));
                                model.submissionDate = tempobj.getString("created_at")
                                model.appointmentDate = tempobj.getJSONObject("appointment")
                                    .getString("scheduled_start_time")
                                model.clinicName =
                                    tempobj.getJSONObject("appointment").getJSONObject("order")
                                        .getJSONObject("products").getJSONObject("prod_service")
                                        .getString("alias")
                                model.clinicAddress =
                                    tempobj.getJSONObject("appointment").getJSONObject("order")
                                        .getJSONObject("products").getJSONObject("prod_service")
                                        .getString("address")
                                model.patientName =
                                    tempobj.getJSONObject("appointment").getJSONObject("order")
                                        .getJSONObject("order_user").getString("fname")

                                /*New Registration(Autogenerated ID) changes for Gastro interface*/model.generalID =
                                    tempobj.getJSONObject("appointment").getJSONObject("order")
                                        .getJSONObject("order_user").getString("general_id")

                                model.patientPhone =
                                    tempobj.getJSONObject("appointment").getJSONObject("order")
                                        .getJSONObject("order_user").getString("phone")
                                model.patientMessage = tempobj.getString("description")
                                model.isApptBooked = tempobj.getInt("is_appointment_booked")
                                model.fileUrl = tempobj.getString("file_url")
                                model.patientId =
                                    tempobj.getJSONObject("appointment").getJSONObject("order")
                                        .getJSONObject("order_user").getInt("id")
                                model.productServiceId =
                                    tempobj.getJSONObject("appointment").getJSONObject("order")
                                        .getJSONObject("products").getJSONObject("prod_service")
                                        .getInt("service_id")
                                autoFollowUpListModelList!!.add(model)
                                autoFollowUpAdapter!!.notifyDataSetChanged()
                            }
                            isMoreData = autoFollowUpListModelList!!.size < rootObj.getInt("total")


                        }
                    } else {
                        emptyText!!.visibility = View.VISIBLE

                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                    loader!!.visibility = View.GONE
                    emptyText!!.visibility = View.VISIBLE

                }
            }

            override fun onError(err: String) {
                errorHandler(this@AutoFollowUpActivity, err)
            }
        })
    }

    fun getFileFromUrl(filrUrl: String) {
        loadingDialog = ProgressDialog(this@AutoFollowUpActivity)
        loadingDialog!!.setMessage(resources.getString(R.string.loading_data))
        loadingDialog!!.setTitle(resources.getString(R.string.fetching_file))
        loadingDialog!!.setProgressStyle(ProgressDialog.STYLE_SPINNER)
        loadingDialog!!.setCancelable(false)
        loadingDialog!!.show()
        val url = JSONObject()
        try {
            url.put("url", filrUrl.trim { it <= ' ' })
        } catch (e: Exception) {
            e.printStackTrace()
        }

//        Log.d("File Url", url.toString());
        apiCall!!.postRecords(
            ApiUrls.getFileFromUrl,
            url,
            this@AutoFollowUpActivity,
            object : VolleyCallback {
                override fun onSuccess(result: String) {
                    loadingDialog!!.dismiss()
                    try {
                        val resObj = JSONObject(result)
                        val browserIntent =
                            Intent(Intent.ACTION_VIEW, Uri.parse(resObj.getString("response")))
                        startActivity(browserIntent)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                override fun onError(err: String) {
                    loadingDialog!!.dismiss()
                    errorHandler(this@AutoFollowUpActivity, err)
                }
            })
    }

    override fun onResume() {
        super.onResume()
        if (autoFollowUpActivityFlag == 1) {
            autoFollowUpActivityFlag = 0
            finish()
        }
        pageNumberF = 1
    }

    fun onCall(phoneNumber: String?) {

        val intent = Intent(Intent.ACTION_DIAL)
        intent.data = Uri.parse("tel:$phoneNumber")
        startActivity(intent)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            123 -> if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                onCall(callPhoneNumber)
            } else {
//                    Log.d("TAG", "Call Permission Not Granted");
            }
            else -> {}
        }
    }

    val doctorsDetails: Unit
        get() {
            val url = ApiUrls.getDoctorsDetails + "?id=" + ApiUrls.doctorId
            val queue = Volley.newRequestQueue(this@AutoFollowUpActivity)
            globalApiCall!!.volleyApiRequestData(
                url,
                Request.Method.GET,
                null,
                this,
                object : VolleyCallback {
                    override fun onSuccess(result: String) {
                        try {
                            val response = JSONObject(result)
                            doctorsDetailsRootObj = response.getJSONObject("response")
                            val userObject = doctorsDetailsRootObj!!.getJSONObject("user")
                            val serviceArray = userObject.getJSONArray("services")
                            for (j in 0 until serviceArray.length()) {
                                val serviceObject = serviceArray.getJSONObject(j)
                                val bookAppointmentModel = AppointmentSlotListModel()
                                if (serviceObject.getInt("id") == 2) {
                                    val intervention = doctorsDetailsRootObj!!.get("chat_product")
                                    if (intervention is JSONArray) {
                                        // It's an array
                                        val chatServiceObject =
                                            doctorsDetailsRootObj!!.getJSONArray("chat_product")
                                    } else if (intervention is JSONObject) {
                                        // It's an object
                                        val chatServiceObject =
                                            doctorsDetailsRootObj!!.getJSONObject("chat_product")
                                        val chatServiceProdObject =
                                            chatServiceObject.getJSONObject("prod_service")
                                        val prodId = chatServiceObject.getInt("id")
                                        val ServId = chatServiceObject.getInt("dr_service_id")
                                        bookAppointmentModel.prodId = prodId
                                        bookAppointmentModel.servId = ServId
                                        bookAppointmentModel.price =
                                            chatServiceObject.getInt("price")
                                        bookAppointmentModel.prodAliasName =
                                            chatServiceProdObject.getString("alias")
                                        bookAppointmentModel.appointmentServiceID =
                                            serviceObject.getInt("id")
                                        bookAppointmentModel.appointmentServiceName =
                                            serviceObject.getString("service")
                                        bookAppointmentModel.appointmentServiceAlias =
                                            serviceObject.getString("alias")
                                    } else {
                                        // It's something else, like a string or number
                                    }
                                } else {
                                    bookAppointmentModel.appointmentServiceID =
                                        serviceObject.getInt("id")
                                    bookAppointmentModel.appointmentServiceName =
                                        serviceObject.getString("service")
                                    bookAppointmentModel.appointmentServiceAlias =
                                        serviceObject.getString("alias")
                                }
                                doctorServiceArrayList!!.add(bookAppointmentModel)
                            }
                            val intervention = doctorsDetailsRootObj!!.get("inst_video")
                            if (intervention is JSONArray) {
                                // It's an array
                                val instantVideoObject =
                                    doctorsDetailsRootObj!!.getJSONArray("inst_video")
                            } else if (intervention is JSONObject) {
                                // It's an object
                                val instantVideoObject =
                                    doctorsDetailsRootObj!!.getJSONObject("inst_video")
                                val bookAppointmentModel = AppointmentSlotListModel()
                                bookAppointmentModel.instantVideoJsonObject = instantVideoObject
                                doctorServiceArrayList!!.add(bookAppointmentModel)

                            } else {
                                // It's something else, like a string or number
                            }
                            val interventionOne = doctorsDetailsRootObj!!.get("inst_video_info")
                            if (interventionOne is JSONArray) {
                                // It's an array
                                val instantVideoObject =
                                    doctorsDetailsRootObj!!.getJSONArray("inst_video_info")
                            } else if (interventionOne is JSONObject) {
                                // It's an object
                                val instantVideoInfoObject =
                                    doctorsDetailsRootObj!!.getJSONObject("inst_video_info")
                                val bookAppointmentModel = AppointmentSlotListModel()
                                //                                bookAppointmentModel.setInstantVideoJsonObject(instantVideoObject);
                                bookAppointmentModel.instantVideoInfoJsonObject =
                                    instantVideoInfoObject
                                doctorServiceArrayList!!.add(bookAppointmentModel)
                            } else {
                                // It's something else, like a string or number
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }

                    override fun onError(err: String) {
                        errorHandler(this@AutoFollowUpActivity, err)
                    }
                })
        }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.shared_patient_menu, menu)
        searchQuery(menu)
        this.menu = menu
        return super.onCreateOptionsMenu(menu)
    }

    private fun searchQuery(menu: Menu): Boolean {
        val myActionMenuItemFilter = menu.findItem(R.id.patientMenuFilter)

        val yourdrawable = menu.getItem(1).icon // change 0 with 1,2 ...
        yourdrawable!!.mutate()
        yourdrawable.setColorFilter(resources.getColor(R.color.colorWhite), PorterDuff.Mode.SRC_IN)
        val myActionMenuItem = menu.findItem(R.id.patientMenuSearch)
        searchView = myActionMenuItem.actionView as SearchView?
        searchView!!.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {

                val view = window.currentFocus
                isFilterApplied = true
                searchQuery = query
                if (view != null) {
                    val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(view.windowToken, 0)
                }
                loader!!.visibility = View.VISIBLE
                autoFollowUpListModelList!!.clear()
                if (autoFollowUpAdapter != null) {
                    autoFollowUpAdapter!!.notifyDataSetChanged()
                }
                getFollowUpList(query, "fname", "desc", "", "", "", "10", 1.toString() + "", "")

                return true
            }

            override fun onQueryTextChange(newText: String): Boolean {
//                Log.d("Search Query Filter", newText);
                if (TextUtils.isEmpty(newText)) {
                    isFilterApplied = false
                    autoFollowUpListModelList!!.clear()
                    if (emptyText!!.visibility == View.VISIBLE) {
                        emptyText!!.visibility = View.GONE
                    }
                    getFollowUpList("", "fname", "desc", "", "", "", "10", 1.toString() + "", "")

                }
                return true
            }
        })
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == R.id.patientMenuFilter) {
            //   Toast.makeText(getApplicationContext(),"click on filter",Toast.LENGTH_LONG).show();
            autoFollowUpFilterBottomSheetDialog = AutoFollowUpFilterBottomSheet()
            autoFollowUpFilterBottomSheetDialog!!.setupConfig(
                this@AutoFollowUpActivity,
                durationFilter,
                conditionFilter,
                instructionFilter,
                followUpAppointmentBookedFilter
            )
            autoFollowUpFilterBottomSheetDialog!!.show(
                supportFragmentManager,
                "Bottom Sheet Dialog Fragment"
            )
        }

        return super.onOptionsItemSelected(item)
    }

    fun showGuide(section: Int) {
        when (section) {
            1 ->
                MaterialIntroView.Builder(this)
                    .enableDotAnimation(true)
                    .enableIcon(false)
                    .dismissOnTouch(true)
                    .setFocusGravity(FocusGravity.CENTER)
                    .setFocusType(Focus.NORMAL)
                    .setDelayMillis(50)
                    .enableFadeAnimation(true)
                    .setInfoText("Change settings for auto follow up ad per your needs") //.setInfoText("Use our assistant to get instant help like fetching patients data, cancel appointments with one command etc")
                    .setShape(ShapeType.CIRCLE)
                    .setTarget(setPref)
                    .setUsageId("intro_setPref") //THIS SHOULD BE UNIQUE ID
                    .setListener { showGuide(2) }
                    .show()
            2 ->
                MaterialIntroView.Builder(this)
                    .enableDotAnimation(true)
                    .enableIcon(false)
                    .dismissOnTouch(true)
                    .setFocusGravity(FocusGravity.CENTER)
                    .setFocusType(Focus.NORMAL)
                    .setDelayMillis(50)
                    .enableFadeAnimation(true)
                    .setInfoText("Use filter to find a particular data") //.setInfoText("Use our assistant to get instant help like fetching patients data, cancel appointments with one command etc")
                    .setShape(ShapeType.CIRCLE)
                    .setTarget(findViewById(R.id.patientMenuFilter))
                    .setUsageId("intro_patientMenuFilter") //THIS SHOULD BE UNIQUE ID
                    .setListener { showGuide(3) }
                    .show()
            3 ->
                MaterialIntroView.Builder(this)
                    .enableDotAnimation(true)
                    .enableIcon(false)
                    .dismissOnTouch(true)
                    .setFocusGravity(FocusGravity.CENTER)
                    .setFocusType(Focus.NORMAL)
                    .setDelayMillis(50)
                    .enableFadeAnimation(true)
                    .setInfoText("Search follow up by patient name") //.setInfoText("Use our assistant to get instant help like fetching patients data, cancel appointments with one command etc")
                    .setShape(ShapeType.CIRCLE)
                    .setTarget(findViewById(R.id.patientMenuSearch))
                    .setUsageId("intro_patientMenuSearch") //THIS SHOULD BE UNIQUE ID
                    .setListener { }
                    .show()
        }
    }

    companion object {
        @JvmField
        var autoFollowUpAdapter: AutoFollowUpAdapter? = null

        @JvmField
        var autoFollowUpListModelList: MutableList<AutoFollowUpModel>? = null

        @JvmField
        var autoFollowUpActivityFlag = 0
        var callPhoneNumber: String? = null

        @JvmField
        var isMoreData = false

        @JvmField
        var pageNumberF = 1

        @JvmField
        var emptyText: TextView? = null
        var doctorsDetailsRootObj: JSONObject? = null
        var doctorServiceArrayList: MutableList<AppointmentSlotListModel>? = null
    }
}