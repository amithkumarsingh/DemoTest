package com.whitecoats.clinicplus

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.content.*
import android.graphics.Color
import android.graphics.PorterDuff
import android.net.Uri
import android.os.Bundle
import android.text.Html
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request.Method
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.whitecoats.adapter.CommunicationListAdapter
import com.whitecoats.broadcast.CommunicationBroadcastReceiver
import com.whitecoats.clinicplus.apis.ApiGetPostMethodCalls
import com.whitecoats.clinicplus.apis.ApiUrls
import com.whitecoats.clinicplus.utils.ErrorHandlerClass.errorHandler
import com.whitecoats.clinicplus.viewmodels.CommonViewModel
import com.whitecoats.model.CommunicationListModel
import com.zoho.salesiqembed.ZohoSalesIQ
import org.json.JSONObject
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class CommunicationListActivity : AppCompatActivity() {
    private var mContext: Context? = null
    private lateinit var addIcon: FloatingActionButton
    private var articlesTypeValue = 0
    private var appPreference: SharedPreferences? = null
    private lateinit var recList: RecyclerView
    private lateinit var llm: LinearLayoutManager
    private lateinit var loader: ProgressBar
    private lateinit var searchView: SearchView
    private lateinit var toolbar: Toolbar
    var myActionMenuItem: MenuItem? = null
    private lateinit var emptyText: TextView
    private var searchQuery = ""
    private var communicationBroadcastReceiver: CommunicationBroadcastReceiver? = null
    private var apiGetPostMethodCalls: ApiGetPostMethodCalls? = null
    var broadcastReceiver: BroadcastReceiver? = null
    private lateinit var dialogPopup: Dialog
    private lateinit var commonViewModel: CommonViewModel

    @SuppressLint("CutPasteId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_communication_list)
        val backArrow = ContextCompat.getDrawable(this, R.drawable.ic_arrow_back)
        backArrow!!.setColorFilter(
            ContextCompat.getColor(this, R.color.colorWhite),
            PorterDuff.Mode.SRC_ATOP
        )
        toolbar = findViewById(R.id.commListToolbar)
        toolbar.navigationIcon = backArrow // your drawable
        val intent = intent
        commDetailsModelList = ArrayList()
        commonViewModel = ViewModelProvider(this)[CommonViewModel::class.java]
        articlesTypeValue = intent.getIntExtra("articlesValues", 0)
        mContext = applicationContext
        apiGetPostMethodCalls = ApiGetPostMethodCalls()
        appPreference = getSharedPreferences(ApiUrls.appSharedPref, MODE_PRIVATE)
        emptyText = findViewById(R.id.commListEmptyText)
        communicationBroadcastReceiver = CommunicationBroadcastReceiver()
        if (ZohoSalesIQ.getApplicationManager() != null) {
            ZohoSalesIQ.Tracking.setPageTitle("Communication List Page")
        }

        //register broadcast
        val filter = IntentFilter()
        filter.addAction(CUSTOM_BROADCAST_ACTION)
        filter.addCategory(Intent.CATEGORY_DEFAULT)
        registerReceiver(communicationBroadcastReceiver, filter)
        loader = findViewById(R.id.appointLoader)
        addIcon = findViewById(R.id.commListFabCreate)
        val fab = findViewById<View>(R.id.commListFabCreate) as FloatingActionButton
        recList = findViewById<View>(R.id.commListCardList) as RecyclerView
        llm = LinearLayoutManager(this)
        //getting the toolbar
        toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed() // Implemented by activity
        }
        addIcon.setColorFilter(
            ContextCompat.getColor(this, R.color.colorWhite),
            PorterDuff.Mode.SRC_IN
        )
        recList.setHasFixedSize(true)
        llm.orientation = LinearLayoutManager.VERTICAL
        fab.setOnClickListener { // Click action
            when (articlesTypeValue) {
                1 -> {
                    val intentNew =
                        Intent(
                            this@CommunicationListActivity,
                            CreateVideoArticleActivity::class.java
                        )
                    intentNew.putExtra("articlesValues", 2)
                    startActivity(intentNew)
                }
                3 -> {
                    val intentNew =
                        Intent(
                            this@CommunicationListActivity,
                            CreatePdfArticlesActivity::class.java
                        )
                    startActivity(intentNew)
                }
                else -> {
                    val intentNew =
                        Intent(
                            this@CommunicationListActivity,
                            CreateTextArticleActivity::class.java
                        )
                    startActivity(intentNew)
                }
            }
        }
        registerReceiverNewArticle()
        when (articlesTypeValue) {
            1 -> {
                toolbar.title = resources.getString(R.string.video_article)
                commDetailsModelList!!.clear()
                getCommunicationArticleList(
                    searchQuery, 1,
                    10
                )
            }
            2 -> {
                toolbar.title = resources.getString(R.string.text_article)
                commDetailsModelList!!.clear()
                getCommunicationArticleList(
                    searchQuery, 1,
                    10
                )
            }
            3 -> {
                toolbar.title = "Pdf Articles"
                commDetailsModelList!!.clear()
                getCommunicationArticleList(
                    searchQuery, 1,
                    10
                )
            }
        }
        commDetailsListAdapter = CommunicationListAdapter(
            commDetailsModelList!!,
            mContext!!,
            articlesTypeValue
        ) { v, position, menuId, loadMOre ->
            if (loadMOre.equals("LOADMORE", ignoreCase = true)) {
                pageNumber += 1
                loader.visibility = View.VISIBLE
                getCommunicationArticleList(
                    searchQuery, pageNumber,
                    10
                )
            } else if (loadMOre.equals("pdf", ignoreCase = true)) {
                val communicationDetailsListModel = commDetailsModelList!![position]
                getPdfImage(communicationDetailsListModel.content_value)
            } else {
                val builder = AlertDialog.Builder(this@CommunicationListActivity)
                builder.setTitle("Delete confirmation !")
                builder.setMessage("Do you really want to delete ?")
                builder.setCancelable(false)
                builder.setPositiveButton("Yes") { dialog, which ->
                    deleteContentArticles(
                        position,
                        menuId
                    )
                }
                builder.setNegativeButton("No") { dialog, which -> }
                builder.show()
            }
        }
        recList.setHasFixedSize(true)
        llm.orientation = LinearLayoutManager.VERTICAL
        recList.layoutManager = llm
        recList.adapter = commDetailsListAdapter
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.communication_list_menu, menu)
        val icon = menu.findItem(R.id.commListMenuSearch).icon
        icon!!.setColorFilter(
            ContextCompat.getColor(
                this@CommunicationListActivity,
                R.color.colorWhite
            ), PorterDuff.Mode.SRC_IN
        )
        menu.findItem(R.id.commListMenuSearch).icon = icon
        myActionMenuItem = menu.findItem(R.id.commListMenuSearch)
        searchView = (myActionMenuItem!!.actionView as SearchView?)!!
        searchView.queryHint="Search " + toolbar.title
        searchView.queryHint = Html.fromHtml("<font color = #ffffff>" +"Search " + toolbar.title + "</font>")
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                val view = window.currentFocus
                if (view != null) {
                    val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(view.windowToken, 0)
                }
                loader.visibility = View.VISIBLE
                commDetailsModelList!!.clear()
                if (commDetailsListAdapter != null) {
                    commDetailsListAdapter!!.notifyDataSetChanged()
                }
                searchQuery = query
                getCommunicationArticleList(
                    searchQuery, 1,
                    10
                )
                return true
            }

            override fun onQueryTextChange(newText: String): Boolean {
                if (TextUtils.isEmpty(newText)) {
                    commDetailsModelList!!.clear()
                    searchQuery = ""
                    getCommunicationArticleList(
                        searchQuery, 1,
                        10
                    )
                }
                return true
            }
        })
        return true
    }

    @SuppressLint("SetTextI18n")
    fun getCommunicationArticleList(
        search: String, pageNum: Int,
        perPage: Int
    ) {

        showCustomProgressAlertDialog(
            resources.getString(R.string.fetching),
            resources.getString(R.string.wait_while_we_fetching)
        )


        emptyText.visibility = View.GONE
        var url = ""
        when (articlesTypeValue) {
            1 -> {
                url = ApiUrls.getCommunicationArticlesDetails + "?&search=" + search +
                        "&page=" + pageNum + "&per_page=" + perPage
            }
            2 -> {
                url = ApiUrls.getCommunicationArticlesText + "?&search=" + search +
                        "&page=" + pageNum + "&per_page=" + perPage
            }
            3 -> {
                url = ApiUrls.getCommunicationPdfArticles + "?&search=" + search +
                        "&page=" + pageNum + "&per_page=" + perPage
            }
        }

        commonViewModel.commonViewModelCall(url, JSONObject(), Method.GET).observe(
            this@CommunicationListActivity
        ) { result ->
            try {
                //Process os success response
                dialogPopup.dismiss()
                loader.visibility = View.GONE
                val responseObj = JSONObject(result)
                if (responseObj.getInt("status_code") == 200) {
                    val response = responseObj.optJSONObject("response")
                    val communicationListRootObj = response!!.optJSONObject("response")
                    val communicationArr = communicationListRootObj!!.getJSONArray("data")
                    isMoreData = communicationArr.length() >= 10
                    for (i in 0 until communicationArr.length()) {
                        val tempobj = communicationArr.getJSONObject(i)
                        val temp = CommunicationListModel()
                        temp.articlesId = tempobj.getInt("id")
                        temp.title = tempobj.getString("content_title")
                        temp.date = parseDateToddMMyyyy(tempobj.getString("created_at"))
                        if (articlesTypeValue == 1) {
                            temp.contentPath = tempobj.getString("content_path")
                        } else if (articlesTypeValue == 2) {
                            temp.contentPath = tempobj.getString("header_img")
                            temp.content_value = tempobj.getString("content_value")
                        } else if (articlesTypeValue == 3) {
                            temp.content_value = tempobj.getString("content_value")
                        }
                        temp.desc = tempobj.getString("content_desc")
                        temp.articlesType = articlesTypeValue
                        val communicationSpecification = tempobj.getJSONArray("spec")
                        val sb = StringBuilder()
                        for (j in 0 until communicationSpecification.length()) {
                            val str = communicationSpecification.getString(j)
                            sb.append(str)
                            sb.append("  |  ")
                        }
                        sb.deleteCharAt(sb.length - 3)
                        val sel_cat = sb.toString()
                        temp.category = sel_cat
                        commDetailsModelList!!.add(temp)
                    }
                    commDetailsListAdapter!!.notifyDataSetChanged()
                    //new logic
                    if (commDetailsModelList!!.size == 0) {
                        emptyText.visibility = View.VISIBLE
                        if (searchQuery.equals("", ignoreCase = true)) {
                            emptyText.text = """You have not created any article yet. 

 Tap the button below to create one"""
                        } else {
                            emptyText.text =
                                "No Article found for the search \"$searchQuery\""
                        }

                        //sending broadcast
                        packageName
                        val intent = Intent(CUSTOM_BROADCAST_ACTION)
                        intent.putExtra("Activity", "CommunicationList")
                        /*set the package name for broadcast and changes the custom_broadcast_action string value to normal string*/intent.setPackage(
                            packageName
                        )
                        sendBroadcast(intent)
                    } else {
                        emptyText.visibility = View.GONE
                    }
                } else {
                    emptyText.visibility = View.VISIBLE
                    try {
                        val `object` = JSONObject(result)
                        val message = `object`.getString("message")
                        emptyText.text = message
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                    dialogPopup.dismiss()
                    errorHandler(this@CommunicationListActivity, result)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                dialogPopup.dismiss()
            }
        }
    }

    private fun getPdfImage(path: String) {
        val url = ApiUrls.getArticleImage
        val map = JSONObject()
        try {
            map.put("url", path)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        // prepare the Request

        commonViewModel.commonViewModelCall(url, map, Method.POST).observe(
            this@CommunicationListActivity
        ) { result ->
            try {
                val responseObj = JSONObject(result)
                if (responseObj.getInt("status_code") == 200) {
                    val response = responseObj.optJSONObject("response")
                    Log.d("Article Detail Response", response!!.getString("response"))
                    val browserIntent =
                        Intent(Intent.ACTION_VIEW, Uri.parse(response.getString("response")))
                    startActivity(browserIntent)
                } else {
                    errorHandler(this@CommunicationListActivity, result)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun parseDateToddMMyyyy(time: String?): String? {
        val inputPattern = "yyyy-MM-dd HH:mm:ss"
        //        String outputPattern = "dd-MMM-yyyy h:mm a";
        val outputPattern = "dd MMM, yyyy"
        val inputFormat = SimpleDateFormat(inputPattern, Locale.ENGLISH)
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

    override fun onResume() {
        super.onResume()
        setSupportActionBar(toolbar)
        toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed() // Implemented by activity
        }
    }

    @SuppressLint("SetTextI18n")
    private fun deleteContentArticles(articlesID: Int, itemPosition: Int) {

        showCustomProgressAlertDialog("Deleting", resources.getString(R.string.wait_while_deleting))
        val url = ApiUrls.deleteContentArticles + articlesID

        // prepare the Request
        commonViewModel.commonViewModelCall(url, JSONObject(), Method.DELETE).observe(
            this@CommunicationListActivity
        ) { result ->
            dialogPopup.dismiss()
            try {
                val responseObj = JSONObject(result)
                if (responseObj.getInt("status_code") == 200) {
                    val response = responseObj.optJSONObject("response")
                    val deleteResponse = response!!.getInt("response")
                    if (deleteResponse == 1) {
                        Toast.makeText(
                            mContext,
                            "You've deleted successfully",
                            Toast.LENGTH_SHORT
                        ).show()
                        if (articlesTypeValue == 1) {
                            commDetailsModelList!!.clear()
                            getCommunicationArticleList(
                                searchQuery, 1,
                                10
                            )
                        } else if (articlesTypeValue == 2) {
                            commDetailsModelList!!.clear()
                            getCommunicationArticleList(
                                searchQuery, 1,
                                10
                            )
                        } else if (articlesTypeValue == 3) {
                            commDetailsModelList!!.clear()
                            getCommunicationArticleList(
                                searchQuery, 1,
                                10
                            )
                        }
                        if (commDetailsModelList!!.size == 0) {
                            emptyText.visibility = View.VISIBLE
                            emptyText.text = """You have not created any article yet. \n\n " +
                                            "Tap the button below to create one"""
                        } else {
                            emptyText.visibility = View.GONE
                        }
                    }
                } else {
                    dialogPopup.dismiss()
                    errorHandler(this@CommunicationListActivity, result)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                dialogPopup.dismiss()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            unregisterReceiver(communicationBroadcastReceiver)
            unregisterReceiver(broadcastReceiver)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun registerReceiverNewArticle() {
        broadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                if (intent.action == "ARTICLES_CREATED") {
                    // here you can fire your action which you want also get data from intent
                    if (articlesTypeValue == 1) {
                        toolbar.title = resources.getString(R.string.video_article)
                        commDetailsModelList!!.clear()
                        getCommunicationArticleList(
                            searchQuery, 1,
                            10
                        )
                    } else if (articlesTypeValue == 2) {
                        toolbar.title = resources.getString(R.string.text_article)
                        commDetailsModelList!!.clear()
                        getCommunicationArticleList(
                            searchQuery, 1,
                            10
                        )
                    } else if (articlesTypeValue == 3) {
                        toolbar.title = "Pdf Articles"
                        commDetailsModelList!!.clear()
                        getCommunicationArticleList(
                            searchQuery, 1,
                            10
                        )
                    }
                }
            }
        }
        val intentFilter = IntentFilter()
        intentFilter.addAction("ARTICLES_CREATED")
        registerReceiver(broadcastReceiver, intentFilter)
    }

    companion object {
        @JvmField
        var commDetailsModelList: MutableList<CommunicationListModel>? = null

        @SuppressLint("StaticFieldLeak")
        @JvmField
        var commDetailsListAdapter: CommunicationListAdapter? = null

        @JvmField
        var isMoreData = false
        var pageNumber = 1
        const val CUSTOM_BROADCAST_ACTION = "CommunicationBroadcastReceiver"
    }

    fun showCustomProgressAlertDialog(
        title: String?,
        progressVal: String?

    ) {
        val builder = AlertDialog.Builder(this@CommunicationListActivity)
        builder.setTitle(title)
        builder.setCancelable(false)
        val customLayout =
            LayoutInflater.from(this@CommunicationListActivity)
                .inflate(R.layout.custom_progress_bar, null)
        builder.setView(customLayout)
        dialogPopup = builder.create()
        val tvBody = customLayout.findViewById<TextView>(R.id.tv_value)
        tvBody.text = progressVal
        dialogPopup.show()
    }
}