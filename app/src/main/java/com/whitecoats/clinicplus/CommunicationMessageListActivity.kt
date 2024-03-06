package com.whitecoats.clinicplus

import android.annotation.SuppressLint
import android.content.Intent
import android.content.IntentFilter
import android.graphics.PorterDuff
import android.os.Bundle
import android.text.Html
import android.text.TextUtils
import android.view.Menu
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.widget.NestedScrollView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.whitecoats.adapter.CommunicationMessageListAdapter
import com.whitecoats.broadcast.CommunicationBroadcastReceiver
import com.whitecoats.clinicplus.apis.ApiGetPostMethodCalls
import com.whitecoats.clinicplus.apis.ApiUrls
import com.whitecoats.clinicplus.utils.ErrorHandlerClass.errorHandler
import com.whitecoats.model.CommunicationMessageListModel
import com.zoho.salesiqembed.ZohoSalesIQ
import org.json.JSONObject
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class CommunicationMessageListActivity : AppCompatActivity() {
    var consultFilter = "All"
    var activePastFilter = "active"
    private lateinit var loader: ProgressBar
    private lateinit var emptyViewLayout: LinearLayout
    private var searchQuery = ""
    private var emptyText: TextView? = null
    val CUSTOM_BROADCAST_ACTION = "CommunicationBroadcastReceiver"
    private var communicationBroadcastReceiver: CommunicationBroadcastReceiver? = null
    private var globalApiCall: ApiGetPostMethodCalls? = null
    private lateinit var toolbar:Toolbar

    @SuppressLint("CutPasteId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_communication_message_list)
        //val intent = intent
       // val intValue = intent.getIntExtra("articlesValues", 0)
        val mContext = applicationContext
        commMessageModelList = ArrayList()
        globalApiCall = ApiGetPostMethodCalls()
        val addIcon = findViewById<FloatingActionButton>(R.id.commMessageListFabCreate)
        loader = findViewById(R.id.appointLoader)
        loader.visibility = View.VISIBLE
        emptyViewLayout = findViewById(R.id.appointEmptyViewLayout)
        emptyText = findViewById(R.id.commMessageEmptyText)
        communicationBroadcastReceiver = CommunicationBroadcastReceiver()
        ZohoSalesIQ.Tracking.setPageTitle("On Communication Message List Page")

        //register broadcast
        val filter = IntentFilter()
        filter.addAction(CUSTOM_BROADCAST_ACTION)
        filter.addCategory(Intent.CATEGORY_DEFAULT)
        registerReceiver(communicationBroadcastReceiver, filter)

        //dummyData();
        //page=1&per_page=10&search=&sortby=DateTime&sortorder=desc
        getPastMessage(
            "DateTime", searchQuery, "desc", 1,
            10
        )
        commMessageListAdapter = CommunicationMessageListAdapter(
            commMessageModelList!!,
            mContext
        ) { v: View?, loadMore: String, _: String?,
            _: Int, _: Int, _: Int, _: Int, _: Int,
            _: String?, _: JSONObject?, _: JSONObject? ->
            if (loadMore.equals("LOADMORE", ignoreCase = true)) {
                pageNumber += 1
                loader.visibility = View.VISIBLE
                getPastMessage(
                    "DateTime", searchQuery, "desc", pageNumber,
                    10
                )
            }
        }
        recList = findViewById(R.id.commMessageListCardList)
        val bottomSheet = findViewById<NestedScrollView>(R.id.bottom_sheet)
        val bottomSheetBehavior: BottomSheetBehavior<*> = BottomSheetBehavior.from(bottomSheet)
        val fab = findViewById<FloatingActionButton>(R.id.commMessageListFabCreate)
        val llm = LinearLayoutManager(this)
         toolbar = findViewById<Toolbar>(R.id.commMessageListToolbar)
        //getting the toolbar
        val backArrow = ContextCompat.getDrawable(this, R.drawable.ic_arrow_back)
        backArrow!!.setColorFilter(ContextCompat.getColor(this,R.color.colorWhite), PorterDuff.Mode.SRC_ATOP)
        toolbar.navigationIcon = backArrow // your drawable
        toolbar.title = resources.getString(R.string.message)
        setSupportActionBar(toolbar)
        toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed() // Implemented by activity
        }
        addIcon.setColorFilter(ContextCompat.getColor(this,R.color.colorWhite), PorterDuff.Mode.SRC_IN)
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        bottomSheetBehavior.peekHeight = 0
        recList!!.setHasFixedSize(true)
        llm.orientation = LinearLayoutManager.VERTICAL
        recList!!.layoutManager = llm
        recList!!.adapter = commMessageListAdapter
        fab.setOnClickListener { // Click action
            val bottomSheetDialogFragment = CommunicationCreateMessBottomSheet()
            bottomSheetDialogFragment.setupConfig(this@CommunicationMessageListActivity)
            bottomSheetDialogFragment.show(supportFragmentManager, "Bottom Sheet Dialog Fragment")
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.communication_list_menu, menu)
        val icon = menu.findItem(R.id.commListMenuSearch).icon
        icon!!.setColorFilter(ContextCompat.getColor(this,R.color.colorWhite), PorterDuff.Mode.SRC_IN)
        menu.findItem(R.id.commListMenuSearch).icon = icon
        val myActionMenuItem = menu.findItem(R.id.commListMenuSearch)
        val searchView = myActionMenuItem.actionView as SearchView?
        searchView!!.queryHint="Search " + toolbar.title
        searchView.queryHint = Html.fromHtml("<font color = #ffffff>" +"Search " + toolbar.title + "</font>")
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
//                Log.d("Search Submit Query", query);
//                    appointmentListAdapter.filter(query);
                val view = window.currentFocus
                //isFilterApplied = true;
                if (view != null) {
                    val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(view.windowToken, 0)
                }
                loader.visibility = View.VISIBLE
                commMessageModelList!!.clear()
                if (commMessageListAdapter != null) {
                    commMessageListAdapter!!.notifyDataSetChanged()
                }

//                if (patientRegisteredTab.getVisibility() == View.VISIBLE) {
//                    patientType = "All";
//                } else {
//                    patientType = "Internal";
//                }

                //getPatientList(patientType, "fname", query, "asc", pageNumber + "", "50");
                searchQuery = query
                getPastMessage(
                    "DateTime", searchQuery, "desc", pageNumber,
                    10
                )
                return true
            }

            override fun onQueryTextChange(newText: String): Boolean {
//                Log.d("Search Query Filter", newText);
                if (TextUtils.isEmpty(newText)) {
                    // isFilterApplied = false;
                    commMessageModelList!!.clear()

//                    if (patientRegisteredTab.getVisibility() == View.VISIBLE) {
//                        patientType = "All";
//                    } else {
//                        patientType = "Internal";
//                    }
                    //getPatientList(patientType, "fname", "", "asc", pageNumber + "", "50");
                    searchQuery = ""
                    getPastMessage(
                        "DateTime", searchQuery, "desc", pageNumber,
                        10
                    )
                }
                return true
            }
        })
        return true
    }

    fun getPastMessage(
        sortBy: String, search: String, sortorderBy: String, pageNum: Int,
        perPage: Int
    ) {
        val url =
            ApiUrls.getPastMessage + "?sortby=" + sortBy + "&search=" + search + "&sortorder=" + sortorderBy +
                    "&page=" + pageNum + "&per_page=" + perPage
        loader.visibility = View.VISIBLE
        emptyViewLayout.visibility = View.GONE
        globalApiCall!!.volleyApiRequestData(
            url,
            Request.Method.GET,
            null,
            this,
            object : VolleyCallback {
                @SuppressLint("SetTextI18n")
                override fun onSuccess(result: String) {
                    try {
                        val response = JSONObject(result)
                        // display response
//                        Log.d("Patient Response", response.toString());
                        loader.visibility = View.GONE
                        val rootObj = response.getJSONObject("response")
                        val patientArray = rootObj.getJSONArray("data")
                        //                            Log.d("PatientListSize", String.valueOf(patientArray.length()));
                        isMoreData = patientArray.length() >= 10
                        for (j in 0 until patientArray.length()) {
                            val patientObject = patientArray.getJSONObject(j)
                            val temp = CommunicationMessageListModel()
                            temp.title = patientObject.getString("message")
                            temp.date = parseDateToddMMyyyy(patientObject.getString("created_at"))
                            temp.attempted = patientObject.getInt("total_messages_attempted")
                            temp.failed = patientObject.getInt("messages_failed")
                            temp.send = patientObject.getInt("messages_sent")
                            commMessageModelList!!.add(temp)
                        }
                        commMessageListAdapter!!.notifyDataSetChanged()

                        //new logic
                        if (commMessageModelList!!.size == 0) {
                            emptyViewLayout.visibility = View.VISIBLE
                            if (searchQuery.equals("", ignoreCase = true)) {
                                emptyText!!.text = """
                                You have not send any message to any patient 
                                
                                To send a message tap the button below
                                """.trimIndent()
                            } else {
                                emptyText!!.text =
                                    "There is no message with the search \"$searchQuery\""
                            }

                            //sending broadcast
                            val intent = Intent(CUSTOM_BROADCAST_ACTION)
                            intent.putExtra("Activity", "CommunicationMessage")
                            intent.putExtra("Param", "MsgList")
                            /*set the package name for broadcast and changes the custom_broadcast_action string value to normal string*/intent.setPackage(
                                packageName
                            )
                            sendBroadcast(intent)
                        } else {
                            emptyViewLayout.visibility = View.GONE
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                override fun onError(err: String) {
                    errorHandler(this@CommunicationMessageListActivity, err)
                }
            })
    }

    fun parseDateToddMMyyyy(time: String?): String? {
        val inputPattern = "yyyy-MM-dd HH:mm:ss"
        //        String outputPattern = "dd-MMM-yyyy h:mm a";
        val outputPattern = "dd MMM, yyyy"
        val inputFormat = SimpleDateFormat(inputPattern,Locale.ENGLISH)
        val outputFormat = SimpleDateFormat(outputPattern, Locale.ENGLISH)
        val date: Date?
        var str: String? = null
        try {
            date = inputFormat.parse(time!!)
            str = outputFormat.format(date!!)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return str
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(communicationBroadcastReceiver)
    }

    companion object {
        @JvmField
        var commMessageModelList: MutableList<CommunicationMessageListModel>? = null

        @SuppressLint("StaticFieldLeak")
        @JvmField
        var commMessageListAdapter: CommunicationMessageListAdapter? = null
        var isMoreData = false
        var pageNumber = 1

        @JvmField
        var recList: RecyclerView? = null
    }
}