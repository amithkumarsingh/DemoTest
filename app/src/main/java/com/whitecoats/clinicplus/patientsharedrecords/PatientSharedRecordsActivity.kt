package com.whitecoats.clinicplus.patientsharedrecords

import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.PorterDuff
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout.OnRefreshListener
import com.android.volley.Request
import com.whitecoats.clinicplus.*
import com.whitecoats.clinicplus.apis.ApiGetPostMethodCalls
import com.whitecoats.clinicplus.apis.ApiUrls
import com.whitecoats.clinicplus.utils.ErrorHandlerClass.errorHandler
import com.whitecoats.model.PatientPListModel
import com.whitecoats.model.PatientRecordsModel
import org.json.JSONException
import org.json.JSONObject

class PatientSharedRecordsActivity : AppCompatActivity() {
    //shared record patient list
    private var patientListRv: RecyclerView? = null
    private var sharedRecPListLayout: RelativeLayout? = null

    //share record category
    private var sharedRecCatListLayout: RelativeLayout? = null
    private var categoryListRv: RecyclerView? = null
    private var patientRecordsModels: MutableList<PatientRecordsModel>? = null
    private var sharedRecordsCategoryAdapter: SharedRecordsCategoryAdapter? = null
    private var sharedEmptyText: TextView? = null
    private var sharedLoder: ProgressBar? = null
    private var patientId = 0
    private var swipeContainer: SwipeRefreshLayout? = null
    private var menu: Menu? = null
    private var apiCall: PatientRecordsApi? = null
    private var loader: ProgressBar? = null
    private var patientEmptyText: TextView? = null
    private var searchView: SearchView? = null
    private var toolbar: Toolbar? = null
    private var patientShareFilterBottomSheetDialogFragment: PatientSharedRecordFilterBottomSheet? =
        null
    @JvmField
    var durationFilter = "All"
    @JvmField
    var consultFilter = "All"
    @JvmField
    var apptTypeFilter = "All"
    var activePastFilter = "active"
    var searchFilter = ""
    @JvmField
    var isFilterApplied = false
    private var patientName: String? = ""
    private var groupData: ArrayList<Int>? = null
    var c = 0
    private var sharedCategory: ArrayList<String>? = null
    private var searchQuery = ""
    private var jsonValue: JSONObject? = null
    private var appliedDurationFilter = false
    private var apiGetPostMethodCalls: ApiGetPostMethodCalls? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_patient_shared_records)
        apiCall = PatientRecordsApi()
        apiGetPostMethodCalls = ApiGetPostMethodCalls()
        loader = findViewById(R.id.sharedRecordPatientLoader)
        patientEmptyText = findViewById(R.id.sharedRecPatientEmptyText)
        swipeContainer = findViewById(R.id.appointmentSwipeContainer)


        val backArrow = ContextCompat.getDrawable(this, R.drawable.ic_arrow_back)
        backArrow?.setColorFilter(ContextCompat.getColor(applicationContext, R.color.colorWhite), PorterDuff.Mode.SRC_ATOP)
        toolbar = findViewById(R.id.sharedRecToolbar)
        toolbar!!.navigationIcon = backArrow // your drawable

        setSupportActionBar(toolbar)
        toolbar!!.setNavigationOnClickListener(View.OnClickListener {
            onBackPressedDispatcher.onBackPressed() // Implemented by activity
        })


        //shared record patient list
        patientListRv = findViewById(R.id.sharedRecordPatientRv)
        patientPListModels = ArrayList()
        groupData = ArrayList()
        sharedCategory = ArrayList()
        sharedRecPListLayout = findViewById(R.id.sharedRecordPatientListLayout)
        sharedRecordsPatientAdater = SharedRecordsPatientAdater(
            patientPListModels!!,
            this,
            groupData!!
        ) { v, loadMore, phoneNumber, videoServiceId, clinicServiceId, instantVideoServiceId, chatServiceId, patientId, patientName, instantVideoObject, instantVideoInfoObject ->
            callPhoneNumber = phoneNumber
            if (loadMore.equals("GET_PATIENT_RECORDS", ignoreCase = true)) {
                sharedRecPListLayout!!.visibility = View.GONE
                sharedRecCatListLayout!!.visibility = View.VISIBLE
                if (menu != null) {
                    menu!!.findItem(R.id.patientMenuSearch).isVisible = false
                    menu!!.findItem(R.id.patientMenuFilter).isVisible = false
                }
                shareRecordPatientListFlag = 1
                patientRecordsModels!!.clear()
                Log.d("Patinet name", patientName)
                toolbar!!.title = patientName
                getSharedRecordCategory(patientId, phoneNumber)
            } else if (loadMore.equals("LOADMORE", ignoreCase = true)) {
                pageNumber += 1
                loader!!.visibility = View.VISIBLE
                getPatientList("All", "fname", searchQuery, "asc", pageNumber.toString() + "", "10")
            } else {
                val builder = AlertDialog.Builder(this@PatientSharedRecordsActivity)
                builder.setTitle("Call Confirm")
                builder.setMessage("Are you sure?")
                builder.setPositiveButton("YES") { dialog, which ->
                    onCall(phoneNumber)
                    dialog.dismiss()
                }
                builder.setNegativeButton("NO") { dialog, which -> // Do nothing
                    dialog.dismiss()
                }
                val alert = builder.create()
                alert.show()
            }
        }
        var mLayoutManager: RecyclerView.LayoutManager = LinearLayoutManager(applicationContext)
        patientListRv!!.layoutManager = mLayoutManager
        patientListRv!!.itemAnimator = DefaultItemAnimator()
        patientListRv!!.adapter = sharedRecordsPatientAdater

        //share record category
        sharedEmptyText = findViewById(R.id.sharedRecCatEmptyText)
        sharedLoder = findViewById(R.id.sharedRecordCatLoader)
        categoryListRv = findViewById(R.id.sharedRecordCatRv)
        sharedRecCatListLayout = findViewById(R.id.sharedRecordCatLayout)
        patientRecordsModels = ArrayList()
        sharedRecordsCategoryAdapter = SharedRecordsCategoryAdapter(
            patientRecordsModels as ArrayList<PatientRecordsModel>,
            this
        ) { v, parameter, type, recordIdArray ->
            if (type.equals("TO_DETAILS", ignoreCase = true)) {
                patientSharedRecordFlag = 1
                //catId_catName
                val paramSplit =
                    parameter.split("_".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                val intent =
                    Intent(this@PatientSharedRecordsActivity, PatientRecordViewActivity::class.java)
                intent.putExtra("CategoryId", paramSplit[0].toInt())
                intent.putExtra("CategoryName", paramSplit[1])
                intent.putExtra("PatientId", patientId)
                intent.putExtra("recordIdArray", recordIdArray)
                startActivity(intent)
            }
        }
        mLayoutManager = LinearLayoutManager(applicationContext)
        categoryListRv!!.layoutManager = mLayoutManager
        categoryListRv!!.itemAnimator = DefaultItemAnimator()
        categoryListRv!!.adapter = sharedRecordsCategoryAdapter

        if (intent.getBooleanExtra("FromNotification", false)) {
            sharedRecPListLayout!!.visibility = View.GONE
            sharedRecCatListLayout!!.visibility = View.VISIBLE
            if (menu != null) {
                menu!!.findItem(R.id.patientMenuSearch).isVisible = false
                menu!!.findItem(R.id.patientMenuFilter).isVisible = false
            }
            shareRecordPatientListFlag = 1
            (patientRecordsModels as ArrayList<PatientRecordsModel>).clear()
            patientId = intent.getIntExtra("PatientId", 0)
            patientName = intent.getStringExtra("PatientName")
            toolbar!!.title = patientName
            getSharedRecordCategory(patientId, intent.getStringExtra("RecordsDate"))
        } else {
            getPatientList("All", "fname", "", "asc", 1.toString() + "", "10")
        }
        swipeContainer!!.setOnRefreshListener(OnRefreshListener {
            (patientPListModels as ArrayList<PatientPListModel>).clear()
            if (isFilterApplied) {
                getPatientList("All", "fname", searchQuery, "asc", 1.toString() + "", "10")
            } else {
                getPatientList("All", "fname", "", "asc", 1.toString() + "", "10")
            }
        })
    }

    fun getPatientList(
        type: String, sortBy: String?, search: String, orderBy: String?, pageNum: String,
        perPage: String
    ) {
        val url =
            ApiUrls.getSharedRecordPatientList + "?duration=" + type + "&search=" + search + "&page=" + pageNum + "&per_page=" + perPage
        loader!!.visibility = View.VISIBLE
        patientEmptyText!!.visibility = View.GONE
        if (swipeContainer!!.isRefreshing) {
            loader!!.visibility = View.GONE
        } else {
            loader!!.visibility = View.VISIBLE
        }
        // prepare the Request
        apiGetPostMethodCalls!!.volleyApiRequestData(url,
            Request.Method.GET,
            null,
            this@PatientSharedRecordsActivity,
            object : VolleyCallback {
                override fun onSuccess(result: String) {
                    loader!!.visibility = View.GONE
                    if (swipeContainer!!.isRefreshing) {
                        swipeContainer!!.isRefreshing = false
                    }
                    try {
                        val response = JSONObject(result)
                        val rootArray = response.getJSONArray("response")
                        isMoreData = rootArray.length() >= 10
                        if (rootArray.length() > 0) {
                            for (j in 0 until rootArray.length()) {
                                val appointmentJsonObject = rootArray.getJSONObject(j)
                                val appointmentObjectTwo =
                                    appointmentJsonObject.getJSONArray("users")
                                for (i in 0 until appointmentObjectTwo.length()) {
                                    groupData!!.add(c, i)
                                    c++
                                    sharedCategory = ArrayList()
                                    val tempobj = appointmentObjectTwo.getJSONObject(i)
                                    val userInfoObject = tempobj.getJSONObject("userinfo")
                                    val categoryObject = tempobj.getJSONArray("categories")
                                    for (k in 0 until categoryObject.length()) {
                                        val categoryObj = categoryObject.getJSONObject(k)
                                        val categoryObjectString =
                                            categoryObj.getString("category") + " (" + categoryObj.getString(
                                                "record_count"
                                            ) + ")"
                                        sharedCategory!!.add(categoryObjectString)
                                    }
                                    val model = PatientPListModel()
                                    model.groupNo = j
                                    model.apptDate = appointmentJsonObject.getString("date")
                                    model.patientId = userInfoObject.getInt("id")
                                    model.roleId = userInfoObject.getInt("role")
                                    model.setPatientName(
                                        userInfoObject.getString("fname").trim { it <= ' ' })
                                    model.sharedCategoryData = sharedCategory
                                    patientPListModels!!.add(model)
                                    sharedRecordsPatientAdater!!.notifyDataSetChanged()
                                }
                            }
                        } else {
                            patientEmptyText!!.visibility = View.VISIBLE
                            patientEmptyText!!.text = "No records has been shared yet"
                        }
                    } catch (e: JSONException) {
                        e.printStackTrace()
                        patientEmptyText!!.visibility = View.VISIBLE
                        patientEmptyText!!.text = resources.getString(R.string.no_patient_found)
                    }
                }

                override fun onError(err: String) {
                    loader!!.visibility = View.GONE
                    patientEmptyText!!.visibility = View.VISIBLE
                    patientEmptyText!!.text = resources.getString(R.string.no_patient_found)
                    if (swipeContainer!!.isRefreshing) {
                        swipeContainer!!.isRefreshing = false
                    }
                    errorHandler(this@PatientSharedRecordsActivity, err)
                }
            })
    }

    private fun getSharedRecordCategory(patientId: Int, sharedDate: String?) {
        this.patientId = patientId
        val url = ApiUrls.getPatientSharedRecordsCount
        try {
            jsonValue = JSONObject()
            jsonValue!!.put("patient_id", patientId)
            jsonValue!!.put("date", sharedDate)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        Log.d("REcords URl", sharedDate!!)
        sharedEmptyText!!.visibility = View.VISIBLE
        sharedEmptyText!!.text = resources.getString(R.string.loading_data)
        sharedLoder!!.visibility = View.VISIBLE
        apiCall!!.postRecords(url, jsonValue, this, object : VolleyCallback {
            override fun onSuccess(result: String) {
                try {
                    Log.d("Success", result)
                    val resObj = JSONObject(result)
                    val catArr = resObj.getJSONObject("response").getJSONArray("categories")
                    val catRecordIdArr = resObj.getJSONObject("response").getJSONArray("record_ids")
                    if (catArr.length() > 0) {
                        sharedEmptyText!!.visibility = View.GONE
                        sharedLoder!!.visibility = View.GONE
                        for (i in 0 until catArr.length()) {
                            val catObj = catArr.getJSONObject(i)
                            val model = PatientRecordsModel()
                            model.recordName = catObj.getString("category")
                            model.recordCount = catObj.getInt("record_count")
                            model.recordId = catObj.getInt("id")
                            model.type = 2
                            model.recordIdArray = catRecordIdArr
                            patientRecordsModels!!.add(model)
                        }
                        sharedRecordsCategoryAdapter!!.notifyDataSetChanged()
                    } else {
                        sharedEmptyText!!.visibility = View.VISIBLE
                        sharedEmptyText!!.text = resources.getString(R.string.no_data_found)
                        sharedLoder!!.visibility = View.GONE
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    sharedEmptyText!!.visibility = View.VISIBLE
                    sharedEmptyText!!.text = resources.getString(R.string.data_load_failed)
                    sharedLoder!!.visibility = View.GONE
                }
            }

            override fun onError(err: String) {
                sharedEmptyText!!.visibility = View.VISIBLE
                sharedEmptyText!!.text = resources.getString(R.string.data_load_failed)
                sharedLoder!!.visibility = View.GONE
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.shared_patient_menu, menu)
        searchQuery(menu)
        this.menu = menu
        if (intent.getBooleanExtra("FromNotification", false)) {
            if (menu != null) {
                menu.findItem(R.id.patientMenuSearch).isVisible = false
                menu.findItem(R.id.patientMenuFilter).isVisible = false
            }
        }
        return super.onCreateOptionsMenu(menu)
    }

    private fun searchQuery(menu: Menu): Boolean {
        val myActionMenuItemFilter = menu.findItem(R.id.patientMenuFilter)
        val yourdrawable = menu.getItem(1).icon // change 0 with 1,2 ...
        yourdrawable!!.mutate()
        yourdrawable.setColorFilter(ContextCompat.getColor(applicationContext, R.color.colorWhite), PorterDuff.Mode.SRC_IN)
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
                patientPListModels!!.clear()
                if (sharedRecordsPatientAdater != null) {
                    sharedRecordsPatientAdater!!.notifyDataSetChanged()
                }
                if (appliedDurationFilter) {
                    getPatientList(durationFilter, "fname", query, "asc", 1.toString() + "", "10")
                } else {
                    getPatientList("All", "fname", query, "asc", 1.toString() + "", "10")
                }
                return true
            }

            override fun onQueryTextChange(newText: String): Boolean {
                if (TextUtils.isEmpty(newText)) {
                    isFilterApplied = false
                    patientPListModels!!.clear()
                    getPatientList("All", "fname", "", "asc", 1.toString() + "", "10")
                }
                return true
            }
        })
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == R.id.patientMenuFilter) {
            appliedDurationFilter = true
            patientShareFilterBottomSheetDialogFragment = PatientSharedRecordFilterBottomSheet()
            patientShareFilterBottomSheetDialogFragment!!.setupConfig(
                this@PatientSharedRecordsActivity,
                durationFilter,
                consultFilter,
                apptTypeFilter,
                activePastFilter
            )
            patientShareFilterBottomSheetDialogFragment!!.show(
                supportFragmentManager,
                "Bottom Sheet Dialog Fragment"
            )
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        super.getOnBackPressedDispatcher().onBackPressed()

        if (shareRecordPatientListFlag == 1) {
            sharedRecPListLayout!!.visibility = View.VISIBLE
            sharedRecCatListLayout!!.visibility = View.GONE
            if (menu != null) {
                menu!!.findItem(R.id.patientMenuSearch).isVisible = true
                menu!!.findItem(R.id.patientMenuFilter).isVisible = true
            }
            shareRecordPatientListFlag = 0
            toolbar!!.title = "Shared Records"
            if (intent.getBooleanExtra("FromNotification", false)) {
                patientPListModels!!.clear()
                getPatientList("All", "fname", "", "asc", 1.toString() + "", "10")
            }
        } else {
            finish()
        }
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
            }
            else -> {}
        }
    }

    companion object {
        @JvmField
        var patientPListModels: MutableList<PatientPListModel>? = null
        @JvmField
        var sharedRecordsPatientAdater: SharedRecordsPatientAdater? = null
        @JvmField
        var isMoreData = false
        @JvmField
        var pageNumber = 1
        @JvmField
        var shareRecordPatientListFlag = 0
        var callPhoneNumber: String? = null
        var patientSharedRecordFlag = 0
    }
}