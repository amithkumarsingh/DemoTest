package com.whitecoats.clinicplus.activities

import android.app.ProgressDialog
import android.content.Intent
import android.graphics.PorterDuff
import android.graphics.Typeface
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.whitecoats.clinicplus.R
import com.whitecoats.clinicplus.utils.ErrorHandlerClass.errorHandler
import com.whitecoats.clinicplus.viewmodels.EMRSharedRecordsViewModel
import org.json.JSONException
import org.json.JSONObject
import java.text.ParseException
import java.text.SimpleDateFormat

class EMRSharedRecordDetails : AppCompatActivity() {
    private var toolbar: Toolbar? = null
    private var intent: Intent? = null
    private var categoryId = 0
    private var recordId = 0
    private var recordDetails: JSONObject? = null
    private var fieldDictionary: JSONObject? = null
    private var mainLayout: LinearLayout? = null
    private var emrSharedOn: TextView? = null
    private var inputFormat: SimpleDateFormat? = null
    private var outputFormat: SimpleDateFormat? = null
    private var emrSharedRecordsViewModel: EMRSharedRecordsViewModel? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_e_m_r_shared_record_details)
        toolbar = findViewById(R.id.emr_shared_record_details_toolbar)
        mainLayout = findViewById(R.id.emr_shared_record_layout)
        emrSharedOn = findViewById(R.id.emr_shared_record_shared_on)
        intent = getIntent()
        categoryId = intent!!.getIntExtra("categoryID", 0)
        recordId = intent!!.getIntExtra("RecordId", 0)
        inputFormat = SimpleDateFormat("yyyy-MM-dd hh:mm:ss")
        outputFormat = SimpleDateFormat("dd MMM, yyyy")
        //        inputFormat = new SimpleDateFormat("dd MMM, yyyy mm:HH");
//        outputFormat = new SimpleDateFormat("dd MMM, yyyy");
        toolbar!!.title = intent!!.getStringExtra("CatName")
        toolbar!!.setTitleTextColor(resources.getColor(R.color.colorWhite))
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        val upArrow = resources.getDrawable(R.drawable.ic_arrow_back)
        upArrow.setColorFilter(resources.getColor(R.color.colorWhite), PorterDuff.Mode.SRC_ATOP)
        supportActionBar!!.setHomeAsUpIndicator(upArrow)
        emrSharedRecordsViewModel = ViewModelProvider(this).get(
            EMRSharedRecordsViewModel::class.java
        )
        emrSharedRecordsViewModel!!.init()
        try {
            recordDetails = JSONObject(intent!!.getStringExtra("RecordDetail"))
            fieldDictionary = JSONObject(intent!!.getStringExtra("fieldDictionary"))
            emrSharedOn!!.text = "Shared on " + outputFormat!!.format(
                inputFormat!!.parse(
                    intent!!.getStringExtra(
                        "sharedOn"
                    )
                )!!
            )
            val recordFields = fieldDictionary!!.getJSONArray(categoryId.toString())
            val layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            for (i in 0 until recordFields.length()) {
                val recordFieldItem = recordFields.getJSONObject(i)
                val linearLayout = LinearLayout(this)
                linearLayout.orientation = LinearLayout.VERTICAL
                val title = TextView(this)
                title.setPadding(16, 0, 0, 0)
                title.text = recordFieldItem.getString("name")
                title.textSize = 16f
                title.setTypeface(Typeface.DEFAULT_BOLD)
                title.layoutParams = layoutParams
                linearLayout.addView(title)
                if (recordDetails!!.has(recordFieldItem.getInt("key").toString())) {
                    val value = TextView(this)
                    value.setPadding(16, 0, 0, 0)

                    if (recordDetails!!.getString(recordFieldItem.getString("key")).isEmpty()) {
                        value.text = "-"
                    } else {
                        value.text = recordDetails!!.getString(recordFieldItem.getString("key"))
                    }
                    value.textSize = 24f
                    value.setTextColor(resources.getColor(R.color.colorBlack))
                    value.layoutParams = layoutParams
                    linearLayout.addView(value)
                } else {
                    Log.i("text present", "no")
                    val value = TextView(this)
                    value.setPadding(16, 0, 0, 0)
                    value.text = "-"
                    value.textSize = 24f
                    value.setTextColor(resources.getColor(R.color.colorBlack))
                    value.layoutParams = layoutParams
                    linearLayout.addView(value)
                }
                linearLayout.setPadding(32, 24, 16, 24)
                mainLayout!!.addView(linearLayout)
            }
            if (recordDetails!!.getString("url") != " " && recordDetails!!.getString("url") != "" && recordDetails!!.getString(
                    "url"
                ) != null
            ) {
                val textparams = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
                textparams.weight = 8f
                val textView = TextView(this)
                textView.text = resources.getString(R.string.view_attachment)
                textView.setTextColor(resources.getColor(R.color.colorAccent))
                textView.textSize = 24f
                textView.setTypeface(Typeface.DEFAULT_BOLD)
                textView.setPadding(32, 32, 32, 32)
                textView.layoutParams = textparams
                mainLayout!!.addView(textView)
                textView.setOnClickListener {
                    try {
                        getFileFromUrl(recordDetails!!.getString("url"))
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        } catch (e: ParseException) {
            e.printStackTrace()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
                //PatientSharedRecordsActivity.patientSharedRecordFlag = 0;
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

    fun getFileFromUrl(url: String?) {
        val loadingDialog = ProgressDialog(this)
        loadingDialog.setMessage(resources.getString(R.string.loading_data))
        loadingDialog.setTitle(resources.getString(R.string.fetching_file))
        loadingDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER)
        loadingDialog.setCancelable(false)
        loadingDialog.show()
        emrSharedRecordsViewModel!!.getFileFromUrl(this, url)
            .observe(this, object : Observer<String?> {
                override fun onChanged(value: String?) {
                    loadingDialog.dismiss()
                    try {
                        val response = JSONObject(value)
                        if (response.getInt("status_code") == 200) {
                            val responseObject = response.getJSONObject("response")
                            val browserIntent = Intent(
                                Intent.ACTION_VIEW,
                                Uri.parse(responseObject.getString("response"))
                            )
                            startActivity(browserIntent)
                        } else {
                            if (value != null) {
                                errorHandler(this@EMRSharedRecordDetails, value)
                            }
                        }
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                }
            })
    }
}