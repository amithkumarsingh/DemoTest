package com.whitecoats.clinicplus.casechannels

import android.content.Intent
import android.graphics.PorterDuff
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.whitecoats.clinicplus.R
import com.whitecoats.clinicplus.apis.ApiUrls
import org.json.JSONArray
import org.json.JSONObject

class CaseChannelRecordsMoreInfoActivity : AppCompatActivity() {
    private var recordDetailObjStr: String? = null
    private var recordId: String? = null
    private lateinit var moreInfoCard: LinearLayout
    private var catId: String? = null
    private var imageUrl: String? = null
    private lateinit var toolbar: Toolbar
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_case_records_more_info)
        toolbar = findViewById(R.id.recordMoreToolbar)
        toolbar.title = intent.getStringExtra("CatName")
        toolbar.setTitleTextColor(ContextCompat.getColor(this,R.color.colorWhite))
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        val upArrow = ContextCompat.getDrawable(this, R.drawable.ic_arrow_back)
        upArrow!!.setColorFilter(ContextCompat.getColor(this,R.color.colorWhite), PorterDuff.Mode.SRC_ATOP)
        supportActionBar!!.setHomeAsUpIndicator(upArrow)
        moreInfoCard = findViewById(R.id.recordMoreList)
        catId = intent.getStringExtra("CatId")
        if (intent.getStringExtra("RecordImageUrl") != null) { // added by dileep 27th auguest
            imageUrl = intent.getStringExtra("RecordImageUrl")
        }
        recordDetailObjStr = intent.getStringExtra("RecordDetail")
        recordId = intent.getStringExtra("RecordId")
        Log.d("Category Id", catId!!)
        if (catId.equals("cn", ignoreCase = true)) {
        } else {
            try {
                val params = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT)
                params.weight = 0.5f
                val recordArr = JSONArray(recordDetailObjStr)
                if (!catId.equals("30", ignoreCase = true)) {
                    for (k in 0 until recordArr.length()) {
                        val recordDetailObj = recordArr.getJSONObject(k)
                        val fieldDic =
                            JSONObject(intent.getStringExtra("RecordField")!!).getJSONArray(catId!!)
                        val recordIdDetails: String = recordDetailObj.getJSONObject("category").getString("id")
                        if (recordIdDetails == recordId) {
                            try {
                                for (i in 0 until fieldDic.length()) {
                                    val linearLayout = LinearLayout(this)
                                    val fieldObj = fieldDic.getJSONObject(i)
                                    val fieldName = TextView(this)
                                    fieldName.text = fieldObj.getString("name")
                                    fieldName.textSize = 20f
                                    fieldName.layoutParams = params
                                    linearLayout.addView(fieldName)
                                    Log.d("Field Name", fieldObj.getString("name"))
                                    if (recordDetailObj.has(Integer.toString(fieldObj.getInt("key")))) {
                                        if (recordDetailObj.has(Integer.toString(fieldObj.getInt("key")))) {
                                            val fieldValue = TextView(this)
                                            fieldValue.text =
                                                recordDetailObj.getString(fieldObj.getString("key"))
                                            if (fieldObj.getString("name")
                                                    .equals("Attachment", ignoreCase = true)
                                            ) {
                                                if (recordDetailObj.getString(fieldObj.getString("key"))
                                                        .equals("yes", ignoreCase = true)
                                                ) {
                                                    fieldValue.text =
                                                        resources.getString(R.string.view_attachment)
                                                    fieldValue.setTextColor(ContextCompat.getColor(this,R.color.colorAccent))
                                                    fieldValue.setOnClickListener {
                                                        getAttachmentImage(
                                                            imageUrl
                                                        )
                                                    }
                                                } else {
                                                    fieldValue.text =
                                                        resources.getString(R.string.no_attachment)
                                                }
                                            } else if (fieldObj.getString("name")
                                                    .equals("File", ignoreCase = true)
                                            ) {
                                                if (recordDetailObj.getString(fieldObj.getString("key"))
                                                        .equals("yes", ignoreCase = true)
                                                ) {
                                                    fieldValue.text =
                                                        resources.getString(R.string.view_attachment)
                                                    fieldValue.setTextColor(ContextCompat.getColor(this,R.color.colorAccent))
                                                    fieldValue.setOnClickListener {
                                                        getAttachmentImage(
                                                            imageUrl
                                                        )
                                                    }
                                                } else {
                                                    fieldValue.text =
                                                        resources.getString(R.string.no_attachment)
                                                }
                                            }
                                            fieldValue.textSize = 20f
                                            fieldValue.layoutParams = params
                                            linearLayout.addView(fieldValue)
                                        }
                                    }
                                    linearLayout.setPadding(20, 20, 20, 20)
                                    linearLayout.weightSum = 1f
                                    moreInfoCard.addView(linearLayout)
                                    val separator: View = LinearLayout(this)
                                    val params1 = LinearLayout.LayoutParams(
                                        ViewGroup.LayoutParams.MATCH_PARENT,
                                        5
                                    )
                                    params1.setMargins(10, 10, 10, 10)
                                    separator.layoutParams = params1
                                    separator.setBackgroundColor(ContextCompat.getColor(this,R.color.colorBackground))
                                    moreInfoCard.addView(separator)
                                }
                            } catch (e: Exception) {
                                e.message
                            }
                            break
                        }
                    }
                } else {
                    for (k in 0 until recordArr.length()) {
                        val recordDetailObj =
                            recordArr.getJSONObject(k) //new JSONObject(recordDetailObjStr);
                        val fieldDic =
                            JSONObject(intent.getStringExtra("RecordField")!!).getJSONObject(catId!!)
                        val recordData = recordDetailObj.getJSONArray("records")
                        for (j in 0 until recordData.length()) {
                            val record = recordData.getJSONObject(j)
                            Log.d("Recooooooooood", record.getString("record_id"))
                            if (record.getString("record_id") == recordId) {
                                for (i in 0 until fieldDic.length()) {
                                    if (i != 3) {
                                        val linearLayout = LinearLayout(this)
                                        val fieldObj = fieldDic.getJSONObject(i.toString() + "")
                                        val fieldName = TextView(this)
                                        fieldName.text = fieldObj.getString("name")
                                        fieldName.textSize = 20f
                                        fieldName.layoutParams = params
                                        linearLayout.addView(fieldName)
                                        Log.d("Field Name", fieldObj.getString("name"))
                                        if (fieldObj["key"] is String) {
                                            if (record.has(fieldObj.getString("key"))) {
                                                val fieldValue = TextView(this)
                                                fieldValue.text =
                                                    record.getString(fieldObj.getString("key"))
                                                if (fieldObj.getString("name")
                                                        .equals("Attachment", ignoreCase = true)
                                                ) {
                                                    if (record.getString(fieldObj.getString("key"))
                                                            .equals("yes", ignoreCase = true)
                                                    ) {
                                                        fieldValue.text =
                                                            resources.getString(R.string.view_attachment)
                                                        fieldValue.setOnClickListener { }
                                                    } else {
                                                        fieldValue.text =
                                                            resources.getString(R.string.no_attachment)
                                                    }
                                                }
                                                fieldValue.textSize = 20f
                                                fieldValue.layoutParams = params
                                                linearLayout.addView(fieldValue)
                                            }
                                        } else if (record.has(Integer.toString(fieldObj.getInt("key")))) {
                                            if (record.has(Integer.toString(fieldObj.getInt("key")))) {
                                                val fieldValue = TextView(this)
                                                fieldValue.text =
                                                    record.getString(fieldObj.getString("key"))
                                                if (fieldObj.getString("name")
                                                        .equals("Attachment", ignoreCase = true)
                                                ) {
                                                    if (record.getString(fieldObj.getString("key"))
                                                            .equals("yes", ignoreCase = true)
                                                    ) {
                                                        fieldValue.text =
                                                            resources.getString(R.string.view_attachment)
                                                        fieldValue.setOnClickListener { }
                                                    } else {
                                                        fieldValue.text =
                                                            resources.getString(R.string.no_attachment)
                                                    }
                                                }
                                                fieldValue.textSize = 20f
                                                fieldValue.layoutParams = params
                                                linearLayout.addView(fieldValue)
                                            }
                                        }
                                        linearLayout.setPadding(20, 20, 20, 20)
                                        linearLayout.weightSum = 1f
                                        moreInfoCard.addView(linearLayout)
                                        val separator: View = LinearLayout(this)
                                        val params1 = LinearLayout.LayoutParams(
                                            ViewGroup.LayoutParams.MATCH_PARENT,
                                            5
                                        )
                                        params1.setMargins(10, 10, 10, 10)
                                        separator.layoutParams = params1
                                        separator.setBackgroundColor(ContextCompat.getColor(this,R.color.colorBackground))
                                        moreInfoCard.addView(separator)
                                    }
                                }
                                break
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    fun getAttachmentImage(imageUrl: String?) {
        val queue = Volley.newRequestQueue(this)
        Log.d("URL", imageUrl!!)
        val url = ApiUrls.getFileFromUrl
        val imgObj = JSONObject()
        try {
            imgObj.put("url", imageUrl)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        // prepare the Request
        val getRequest: JsonObjectRequest = object : JsonObjectRequest(
            Method.POST, url, imgObj,
            Response.Listener { response -> // display response
                try {
                    val i = Intent(Intent.ACTION_VIEW)
                    i.data = Uri.parse(response.getString("response"))
                    startActivity(i)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            },
            Response.ErrorListener { error -> Log.d("Consult Error.Response", error.toString()) }
        ) {
            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                val headers = HashMap<String, String>()
                headers["Content-Type"] = "application/json"
                headers["App-Origin"] = ApiUrls.appOrigin
                headers["Authorization"] = "Bearer " + ApiUrls.loginToken
                return headers
            }
        }

        // add it to the RequestQueue
        queue.add(getRequest)
    }
}