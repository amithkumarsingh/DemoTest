package com.whitecoats.clinicplus

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.PorterDuff
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import co.mobiwise.materialintro.shape.Focus
import co.mobiwise.materialintro.shape.FocusGravity
import co.mobiwise.materialintro.shape.ShapeType
import co.mobiwise.materialintro.view.MaterialIntroView
import com.android.volley.Request.Method
import com.whitecoats.clinicplus.apis.ApiGetPostMethodCalls
import com.whitecoats.clinicplus.apis.ApiUrls
import com.whitecoats.clinicplus.utils.ErrorHandlerClass.errorHandler
import com.whitecoats.clinicplus.viewmodels.CommonViewModel
import com.zoho.salesiqembed.ZohoSalesIQ
import org.json.JSONObject

class CommunicationActivity : AppCompatActivity() {
    private lateinit var commVideoArtLayout: RelativeLayout
    private lateinit var commTextArtLayout: RelativeLayout
    private lateinit var commPdfArtLayout: RelativeLayout
    private lateinit var commMessageLayout: RelativeLayout
    private lateinit var videoArticlesCount: TextView
    private lateinit var textArticlesCount: TextView
    private lateinit var pdfArticlesCount: TextView
    private var appPreference: SharedPreferences? = null
    private lateinit var commVideoPublish: TextView
    private lateinit var commTextPublish: TextView
    private lateinit var commPdfPublish: TextView
    private lateinit var commMessagePublish: TextView
    private var apiGetPostMethodCalls: ApiGetPostMethodCalls? = null
    private lateinit var dialog: Dialog
    private lateinit var commonViewModel: CommonViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_communication)
        apiGetPostMethodCalls = ApiGetPostMethodCalls()
        commonViewModel = ViewModelProvider(this@CommunicationActivity)[CommonViewModel::class.java]
        appPreference = getSharedPreferences(ApiUrls.appSharedPref, MODE_PRIVATE)
        if (ZohoSalesIQ.getApplicationManager() != null) {
            ZohoSalesIQ.Tracking.setPageTitle("Communication Overview Page")
        }
        val backArrow = ContextCompat.getDrawable(this, R.drawable.ic_arrow_back)
        backArrow!!.setColorFilter(
            ContextCompat.getColor(this, R.color.colorWhite),
            PorterDuff.Mode.SRC_ATOP
        )
        val toolbar = findViewById<Toolbar>(R.id.commToolbar)
        toolbar.navigationIcon = backArrow // your drawable
        toolbar.title = "Communication"
        setSupportActionBar(toolbar)
        toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed() // Implemented by activity
        }
        videoArticlesCount = findViewById(R.id.commVideoCount)
        textArticlesCount = findViewById(R.id.commTextCount)
        messageCount = findViewById(R.id.commMessageCount)
        pdfArticlesCount = findViewById(R.id.commPdfCount)
        commVideoPublish = findViewById(R.id.commVideoPublish)
        commTextPublish = findViewById(R.id.commTextPublish)
        commMessagePublish = findViewById(R.id.commMessagePublish)
        commPdfPublish = findViewById(R.id.commPdfPublish)
        showGuide(1)
        commVideoPublish.setOnClickListener {
            val intent = Intent(this@CommunicationActivity, CreateVideoArticleActivity::class.java)
            startActivity(intent)
        }
        commTextPublish.setOnClickListener {
            val intent = Intent(this@CommunicationActivity, CreateTextArticleActivity::class.java)
            startActivity(intent)
        }
        commPdfPublish.setOnClickListener {
            val intent = Intent(this@CommunicationActivity, CreatePdfArticlesActivity::class.java)
            startActivity(intent)
        }
        commMessagePublish.setOnClickListener {
            ZohoSalesIQ.Tracking.setCustomAction("Communication - Send New Message")
            val bottomSheetDialogFragment = CommDashBoardMessBottomSheet()
            bottomSheetDialogFragment.setupConfig(this@CommunicationActivity)
            bottomSheetDialogFragment.show(supportFragmentManager, "Bottom Sheet Dialog Fragment")
        }
        commVideoArtLayout = findViewById(R.id.commVideoArtLayout)
        commVideoArtLayout.setOnClickListener {
            val intent = Intent(this@CommunicationActivity, CommunicationListActivity::class.java)
            intent.putExtra("articlesValues", 1)
            startActivity(intent)
        }
        commTextArtLayout = findViewById(R.id.commTextArtLayout)
        commTextArtLayout.setOnClickListener {
            val intent = Intent(this@CommunicationActivity, CommunicationListActivity::class.java)
            intent.putExtra("articlesValues", 2)
            startActivity(intent)
        }
        commPdfArtLayout = findViewById(R.id.commPdfLayout)
        commPdfArtLayout.setOnClickListener {
            val intent = Intent(this@CommunicationActivity, CommunicationListActivity::class.java)
            intent.putExtra("articlesValues", 3)
            startActivity(intent)
        }
        commMessageLayout = findViewById(R.id.commMessageLayout)
        commMessageLayout.setOnClickListener {
            val intent =
                Intent(this@CommunicationActivity, CommunicationMessageListActivity::class.java)
            startActivity(intent)
        }
    }

    //Process os success response
    private val communicationCount: Unit
        get() {
            showCustomProgressAlertDialog(
                resources.getString(R.string.fetching),
                resources.getString(R.string.wait_while_we_fetching)
            )


            val url = ApiUrls.getCommunicationCount

            commonViewModel.commonViewModelCall(url, JSONObject(), Method.GET).observe(
                this@CommunicationActivity
            ) { result ->
                try {
                    //Process os success response
                    dialog.dismiss()
                    val responseObj = JSONObject(result)
                    if (responseObj.getInt("status_code") == 200) {
                        val response = responseObj.optJSONObject("response")
                        val communicationCountObj = response!!.optJSONObject("response")
                        videoArticlesCount.text =
                            communicationCountObj!!.getString("article_video_count").toString()
                        textArticlesCount.text =
                            communicationCountObj.getString("article_text_count").toString()
                        pdfArticlesCount.text =
                            communicationCountObj.getString("article_pdf_count").toString()
                        messageCount!!.text =
                            communicationCountObj.getString("messages_count").toString()
                    } else {
                        dialog.dismiss()
                        errorHandler(this@CommunicationActivity, result)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    dialog.dismiss()
                }
            }
        }

    override fun onResume() {
        super.onResume()
        communicationCount
    }

    private fun showGuide(section: Int) {
        when (section) {
            1 -> if (!appPreference!!.getBoolean("CommMain", false)) {
                MaterialIntroView.Builder(this)
                    .enableDotAnimation(true)
                    .enableIcon(false)
                    .dismissOnTouch(true)
                    .setFocusGravity(FocusGravity.CENTER)
                    .setFocusType(Focus.NORMAL)
                    .setDelayMillis(50)
                    .enableFadeAnimation(true)
                    .setInfoText("Number of Articles Published")
                    .setShape(ShapeType.CIRCLE)
                    .setTarget(videoArticlesCount)
                    .setUsageId("intro_videoArticlesCount") //THIS SHOULD BE UNIQUE ID
                    .setListener {
                        showGuide(2)
                        val editor = appPreference!!.edit()
                        editor.putBoolean("CommMain", true)
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
                .setInfoText("Click here to view published articles")
                .setShape(ShapeType.CIRCLE)
                .setTarget(findViewById(R.id.commVideoArrow))
                .setUsageId("intro_commVideoArrow") //THIS SHOULD BE UNIQUE ID
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
                .setInfoText("Click here to share a new article for your patients to view")
                .setShape(ShapeType.CIRCLE)
                .setTarget(commVideoPublish)
                .setUsageId("intro_commVideoPublish") //THIS SHOULD BE UNIQUE ID
                .setListener { }
                .show()
        }
    }

    companion object {
        @SuppressLint("StaticFieldLeak")
        @JvmField
        var messageCount: TextView? = null
    }
    fun showCustomProgressAlertDialog(
        title: String?,
        progressVal: String?

    ) {
        val builder = AlertDialog.Builder(this@CommunicationActivity)
        builder.setTitle(title)
        builder.setCancelable(false)
        val customLayout = LayoutInflater.from(this@CommunicationActivity).inflate(R.layout.custom_progress_bar, null)
        builder.setView(customLayout)
        dialog = builder.create()
        val tvBody = customLayout.findViewById<TextView>(R.id.tv_value)
        tvBody.text = progressVal
        dialog.show()
    }
}