package com.whitecoats.clinicplus.casechannels

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.PorterDuff
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.KeyEvent
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.TextView.OnEditorActionListener
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.core.widget.NestedScrollView
import androidx.lifecycle.ViewModelProvider
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.BottomSheetCallback
import com.whitecoats.broadcast.CaseChannelBroadcastReceiver
import com.whitecoats.clinicplus.AppUtilities
import com.whitecoats.clinicplus.PatientRecordsApi
import com.whitecoats.clinicplus.R
import com.whitecoats.clinicplus.apis.ApiUrls
import com.whitecoats.model.CaseChannelModel
import org.json.JSONObject

class CaseChannelListActivity : AppCompatActivity() {
    private lateinit var caseChannelRv: RecyclerView
    private lateinit var caseChannelModels: MutableList<CaseChannelModel>
    private var caseChannelListAdapter: CaseChannelListAdapter? = null
    private var appUtilities: AppUtilities? = null
    private lateinit var episodeLayout: RelativeLayout
    private lateinit var filterBtn: Button
    private lateinit var emptyTv: TextView
    private lateinit var filterCard: CardView
    private val fromAppt = false
    private var patientId: String? = ""
    private var apiCalls: PatientRecordsApi? = null
    private lateinit var searchEt: EditText
    private var searchStr = ""
    private var sortByStr: String? = "name"
    private var sortOrderStr: String? = "asc"
    private lateinit var filterSheetBehavior: BottomSheetBehavior<*>
    private lateinit var filterBottomSheet: NestedScrollView
    private var caseChannelBroadcastReceiver: CaseChannelBroadcastReceiver? = null
    private var pageNumber = 1

    @JvmField
    var total = 0
    private lateinit var dialog: AlertDialog
    private lateinit var caseChannelsViewModel: CaseChannelsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_case_channel_list)
        val toolbar = findViewById<Toolbar>(R.id.caseChannelToolbar)
        toolbar.title = "Case Channel"
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        val upArrow = ContextCompat.getDrawable(this, R.drawable.ic_arrow_back)
        upArrow!!.setColorFilter(
            ContextCompat.getColor(
                this@CaseChannelListActivity,
                R.color.colorWhite
            ), PorterDuff.Mode.SRC_ATOP
        )
        supportActionBar!!.setHomeAsUpIndicator(upArrow)
        caseChannelRv = findViewById(R.id.caseChannelListRecycleView)
        episodeLayout = findViewById(R.id.caseChannelEpisodeLayout)
        emptyTv = findViewById(R.id.caseChannelEmptyText)
        filterCard = findViewById(R.id.caseChannelActionCard)
        searchEt = findViewById(R.id.caseChannelSearchEt)
        filterBtn = findViewById(R.id.caseChannelFilterBtn)
        filterBottomSheet = findViewById(R.id.caseChannelFilterBottomsheet)
        filterSheetBehavior = BottomSheetBehavior.from(filterBottomSheet)
        caseChannelModels = ArrayList()
        appUtilities = AppUtilities()
        apiCalls = PatientRecordsApi()
        caseChannelsViewModel = ViewModelProvider(this)[CaseChannelsViewModel::class.java]
        filterSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        filterSheetBehavior.peekHeight = 0
        caseChannelListAdapter = CaseChannelListAdapter(
            this,
            caseChannelModels,
            object : CaseDoctorOrganisationClickListener {
                override fun onItemClick(
                    v: View,
                    position: Int,
                    selectState: String,
                    sortByString: String
                ) {
                    if (selectState.equals("LOAD_MORE", ignoreCase = true)) {
                        pageNumber++
                        summary
                    }
                }

                override fun getFilters(
                    v: View,
                    position: Int,
                    selectState: String,
                    sortByString: String,
                    statusPos: Int,
                    sortPos: Int
                ) {
                }
            })
        val horizontalLayoutManagaer =
            LinearLayoutManager(this@CaseChannelListActivity, LinearLayoutManager.VERTICAL, false)
        caseChannelRv.layoutManager = horizontalLayoutManagaer
        caseChannelRv.adapter = caseChannelListAdapter
        episodeLayout.visibility = View.GONE
        if (fromAppt) {
            episodeLayout.visibility = View.VISIBLE
        }
        searchEt.setOnEditorActionListener(OnEditorActionListener { _: TextView?, actionId: Int, _: KeyEvent? ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                Log.d("Search text", searchEt.text.toString())
                searchStr = searchEt.text.toString()
                caseChannelModels.clear()
                pageNumber = 1
                summary
                return@OnEditorActionListener true
            }
            return@OnEditorActionListener false
        })
        searchEt.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                if (searchEt.text.toString().equals("", ignoreCase = true)) {
                    searchStr = ""
                    caseChannelModels.clear()
                    pageNumber = 1
                    summary
                }
            }

            override fun afterTextChanged(editable: Editable) {}
        })
        filterBtn.setOnClickListener {
            val bottomSheetDialogFragment = CaseChannelListFilterBottomsheet()
            bottomSheetDialogFragment.show(supportFragmentManager, "Bottom Sheet Dialog Fragment")
        }
        filterSheetBehavior.setBottomSheetCallback(object : BottomSheetCallback() {
            override fun onStateChanged(view: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_HIDDEN -> {}
                    BottomSheetBehavior.STATE_EXPANDED -> {}
                    BottomSheetBehavior.STATE_COLLAPSED -> {}
                    BottomSheetBehavior.STATE_DRAGGING -> {}
                    BottomSheetBehavior.STATE_SETTLING -> {}
                    BottomSheetBehavior.STATE_HALF_EXPANDED -> {
                        TODO()
                    }
                }
            }

            override fun onSlide(view: View, v: Float) {}
        })
        caseChannelBroadcastReceiver = object : CaseChannelBroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                //TODO extract extras from intent
                Log.d("From Filter", intent.getStringExtra("SortBy")!!)
                Log.d("From Filter", intent.getStringExtra("SortOrder")!!)
                sortByStr = intent.getStringExtra("SortBy")
                sortByStr = if (sortByStr.equals("created on", ignoreCase = true)) {
                    "created_at"
                } else {
                    "name"
                }
                sortOrderStr = intent.getStringExtra("SortOrder")
                sortOrderStr = if (sortOrderStr.equals("Ascending", ignoreCase = true)) {
                    "asc"
                } else {
                    "desc"
                }
                pageNumber = 1
                caseChannelModels.clear()
                summary
            }
        }
        patientId = intent.getStringExtra("PatientId")
        summary
    }

    public override fun onResume() {
        super.onResume()
        LocalBroadcastManager.getInstance(applicationContext).registerReceiver(
            caseChannelBroadcastReceiver!!,
            IntentFilter("Filter")
        )
    }

    private fun showCustomProgressAlertDialog(title: String, progressVal: String) {
        val builder = AlertDialog.Builder(this@CaseChannelListActivity)
        builder.setTitle(title)
        builder.setCancelable(false)
        val customLayout: View = layoutInflater.inflate(R.layout.custom_progress_bar, null)
        builder.setView(customLayout)
        dialog = builder.create()
        val tvBody = customLayout.findViewById<TextView>(R.id.tv_value)
        tvBody.text = progressVal
        dialog.show()
    }

    public override fun onPause() {
        LocalBroadcastManager.getInstance(applicationContext).unregisterReceiver(
            caseChannelBroadcastReceiver!!
        )
        super.onPause()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
        }
        return super.onOptionsItemSelected(item)
    }

    //discussion
    val summary: Unit
        @SuppressLint("SetTextI18n")
        get() {
            showCustomProgressAlertDialog(
                resources.getString(R.string.case_channel_dialog_text),
                resources.getString(R.string.please_wait)
            )
            emptyTv.visibility = View.GONE
            val url =
                ApiUrls.caseDiscussionSummary + "?disc_patient_id&disc_status&disc_search=" + searchStr + "&disc_sortby=" + sortByStr + "&disc_sortorder=" + sortOrderStr + "&disc_per_page=10&disc_page=" + pageNumber + "&task_discussion_id&task_status&task_assigned_to&task_sortby&task_sortorder&task_per_page=5&task_page=1&chat_sender_id"

            caseChannelsViewModel.getCaseDiscussionSummery(url)
                .observe(
                    this@CaseChannelListActivity
                ) { result ->
                    try {
                        val resObjRes = JSONObject(result)
                        dialog.dismiss()
                        if (resObjRes.getInt("status_code") == 200) {
                            val resObj = resObjRes.getJSONObject("response")
                            //discussion
                            val discussionObj =
                                resObj.getJSONObject("response").getJSONObject("discussions")
                            val discusArr = discussionObj.getJSONArray("data")
                            total = resObj.getJSONObject("response").getJSONObject("discussions")
                                .getInt("total")
                            if (discusArr.length() > 0) {
                                for (i in 0 until discusArr.length()) {
                                    val tempobj = discusArr.getJSONObject(i)
                                    val temp = CaseChannelModel()
                                    temp.caseChannelName = tempobj.getString("name")
                                    temp.caseId = tempobj.getInt("id")
                                    temp.ownerName = tempobj.getString("owner_name")
                                    temp.createdAt = appUtilities!!.changeDateFormat(
                                        "yyyy-MM-dd HH:mm:ss",
                                        "dd MMM yy",
                                        tempobj.getString("created_at")
                                    )
                                    temp.patientId = tempobj.getJSONObject("patients").getInt("id")
                                    temp.patientName =
                                        tempobj.getJSONObject("patients").getString("fname")
                                    temp.status = tempobj.getInt("status")
                                    temp.taskCount = tempobj.getInt("total_task_count")
                                    temp.recordsCount = tempobj.getInt("total_records_count")
                                    temp.messageCounts = tempobj.getInt("total_chat_count")
                                    caseChannelModels.add(temp)
                                }
                                caseChannelListAdapter!!.notifyDataSetChanged()
                            } else {
                                if (caseChannelModels.size == 0) {
                                    emptyTv.visibility = View.VISIBLE
                                    filterCard.visibility = View.GONE
                                    if (!searchStr.equals("", ignoreCase = true)) {
                                        filterCard.visibility = View.VISIBLE
                                        emptyTv.text = "No channel found with the name '$searchStr'"
                                    }
                                }
                            }
                        } else {
                            emptyTv.text = "Error occurred. Please try again later."
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        dialog.dismiss()
                    }
                }
        }
}