package com.whitecoats.clinicplus.casechannels

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.graphics.PorterDuff
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager.widget.ViewPager
import com.android.volley.Request
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import com.google.android.material.tabs.TabLayout.TabLayoutOnPageChangeListener
import com.whitecoats.clinicplus.AppUtilities
import com.whitecoats.clinicplus.R
import com.whitecoats.clinicplus.apis.ApiGetPostMethodCalls
import com.whitecoats.clinicplus.apis.ApiUrls
import com.whitecoats.clinicplus.utils.ErrorHandlerClass.errorHandler
import org.json.JSONArray
import org.json.JSONObject

class CaseChannelDashboardActivity : AppCompatActivity(), OnTabSelectedListener {
    private lateinit var tabLayout: TabLayout
    private lateinit var viewPager: ViewPager
    private var patientNameStr: String? = null
    private var patientPhNoStr: String? = null
    private var caseChannelName: String? = null
    private var caseDate: String? = null
    private var participantCount = "0"
    private var appUtilities: AppUtilities? = null
    private lateinit var toolbar: Toolbar
    private var globalApiCall: ApiGetPostMethodCalls? = null
    private lateinit var caseChannelsViewModel: CaseChannelsViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_case_channel_dashboard)
        toolbar = findViewById(R.id.caseChannelDashToolbar)
        toolbar.setTitleTextColor(
            ContextCompat.getColor(
                this@CaseChannelDashboardActivity,
                R.color.colorWhite
            )
        )
        val backArrow = ContextCompat.getDrawable(this, R.drawable.ic_arrow_back)
        backArrow!!.setColorFilter(
            ContextCompat.getColor(
                this@CaseChannelDashboardActivity,
                R.color.colorWhite
            ), PorterDuff.Mode.SRC_ATOP
        )
        toolbar.navigationIcon = backArrow
        setSupportActionBar(toolbar)
        toolbar.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }
        tabLayout = findViewById(R.id.caseChannelDashTabLayout)
        viewPager = findViewById(R.id.caseChannelDashTabPager)
        appUtilities = AppUtilities()
        caseChannelsViewModel =
            ViewModelProvider(this@CaseChannelDashboardActivity)[CaseChannelsViewModel::class.java]
        doctorArray = JSONArray()
        globalApiCall = ApiGetPostMethodCalls()
        tabLayout.setTabTextColors(Color.parseColor("#ffffff"), Color.parseColor("#FFC300"))
        tabLayout.addTab(tabLayout.newTab().setText("Tasks"))
        tabLayout.addTab(tabLayout.newTab().setText("Discussions"))
        tabLayout.tabGravity = TabLayout.GRAVITY_FILL

        //getting case channel id
        caseChannelId = intent.getIntExtra("caseChannelId", 0)
        caseChannelPatientId = intent.getIntExtra("caseChannelPatientId", 0)
        caseChannelEpisodeId = intent.getIntExtra("caseChannelEpisodeId", 0)
        caseChannelDoctorId = intent.getIntExtra("caseChannelDoctorId", 0)
        if (intent.getStringExtra("SwitchTab") != null && intent.getStringExtra("SwitchTab")
                .equals("Discussion", ignoreCase = true)
        ) {
            tabLayout.getTabAt(1)!!.select()
        }
        getCaseChannelDashBoardSummary(caseChannelId)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.case_info -> {
                showCaseInfo()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu, this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.case_channel_dash_nav_menu, menu)
        val yourdrawable = menu.getItem(0).icon // change 0 with 1,2 ...
        yourdrawable!!.mutate()
        yourdrawable.setColorFilter(
            ContextCompat.getColor(
                this@CaseChannelDashboardActivity,
                R.color.colorWhite
            ), PorterDuff.Mode.SRC_IN
        )
        return true
    }

    override fun onTabSelected(tab: TabLayout.Tab) {
        viewPager.currentItem = tab.position
    }

    override fun onTabUnselected(tab: TabLayout.Tab) {}
    override fun onTabReselected(tab: TabLayout.Tab) {}

    @SuppressLint("SetTextI18n")
    private fun showCaseInfo() {
        val viewGroup = findViewById<ViewGroup>(android.R.id.content)
        val dialogView =
            LayoutInflater.from(this).inflate(R.layout.dialog_case_channel_info, viewGroup, false)
        val builder = AlertDialog.Builder(this)
        builder.setView(dialogView)
        val alertDialog = builder.create()
        val channelName = dialogView.findViewById<TextView>(R.id.caseChannelInfoName)
        val startDate = dialogView.findViewById<TextView>(R.id.caseChannelDate)
        val patientName = dialogView.findViewById<TextView>(R.id.caseChannelInfoPatientName)
        val patientPhNo = dialogView.findViewById<TextView>(R.id.caseChannelInfoPatientContact)
        val participants = dialogView.findViewById<TextView>(R.id.caseChannelInfoParticipants)
        val closeBtn = dialogView.findViewById<ImageButton>(R.id.closeDialog)
        channelName.text = caseChannelName
        startDate.text = "Created On: $caseDate"
        patientName.text = patientNameStr
        patientPhNo.text = patientPhNoStr
        participants.text = "Participants: $participantCount"
        closeBtn.setOnClickListener { alertDialog.dismiss() }
        patientPhNo.setOnClickListener {
            val intent = Intent(Intent.ACTION_DIAL)
            intent.data = Uri.parse("tel:$patientPhNoStr")
            startActivity(intent)
        }
        alertDialog.show()
    }

    private fun getCaseChannelDashBoardSummary(group_id: Int) {
        val url = ApiUrls.getCaseChannelDashBoardSummaryDetails + "?group_id=" + group_id

        caseChannelsViewModel.getCaseDashboardSummery(url)
            .observe(
                this
            ) { result ->
                try {
                    val responseObj = JSONObject(result)
                    Log.d("DashBoardSummaryRespo", responseObj.toString())
                    if (responseObj.getInt("status_code") == 200) {
                        val response = responseObj.getJSONObject("response")
                        caseChannelName = response.getJSONObject("response").getString("name")
                        toolbar.title = caseChannelName
                        participantCount =
                            response.getJSONObject("response").getString("is_user_admin")
                        patientNameStr =
                            response.getJSONObject("response").getJSONObject("patients")
                                .getString("fname")
                        patientPhNoStr =
                            response.getJSONObject("response").getJSONObject("patients")
                                .getString("phone")
                        val startedDate = response.getJSONObject("response").getString("created_at")
                        caseDate = appUtilities!!.changeDateFormat(
                            "yyyy-MM-dd HH:mm:ss",
                            "dd MMM, yy HH:mm a",
                            startedDate
                        )
                        toolbar.subtitle = "Created On: $caseDate"
                        toolbar.setSubtitleTextColor(
                            ContextCompat.getColor(
                                this@CaseChannelDashboardActivity,
                                R.color.colorWhite
                            )
                        )

                        //to get the participant doctor id array
                        val doctorParticipantObject = response.getJSONObject("response")
                        val doctorArr = doctorParticipantObject.getJSONArray("doctorParticipants")
                        if (doctorArr.length() > 0) {
                            for (i in 0 until doctorArr.length()) {
                                val tempobj = doctorArr.getJSONObject(i)
                                doctorArray!!.put(tempobj.getInt("participants_id"))
                            }
                        }
                        val adapter = CaseChannelDashTabAdapter(
                            supportFragmentManager,
                            tabLayout.tabCount,
                            caseChannelId,
                            caseChannelPatientId,
                            caseChannelEpisodeId,
                            caseChannelDoctorId,
                            doctorArray!!,
                            1
                        )
                        viewPager.adapter = adapter
                        viewPager.addOnPageChangeListener(TabLayoutOnPageChangeListener(tabLayout))
                        tabLayout.setOnTabSelectedListener(this@CaseChannelDashboardActivity)
                    } else {
                        errorHandler(this@CaseChannelDashboardActivity, result)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }

            }
    }

    companion object {
        var caseChannelId = 0
        var caseChannelPatientId = 0
        var caseChannelEpisodeId = 0
        var caseChannelDoctorId = 0

        @JvmField
        var doctorArray: JSONArray? = null
    }
}