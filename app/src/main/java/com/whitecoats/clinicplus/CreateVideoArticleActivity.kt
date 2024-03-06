package com.whitecoats.clinicplus

import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.graphics.PorterDuff
import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import android.text.*
import android.text.style.AlignmentSpan
import android.text.style.StrikethroughSpan
import android.text.style.StyleSpan
import android.text.style.UnderlineSpan
import android.util.Patterns
import android.view.*
import android.webkit.URLUtil
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.core.widget.NestedScrollView
import androidx.lifecycle.ViewModelProvider
import com.android.volley.Request.Method
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.whitecoats.clinicplus.apis.ApiGetPostMethodCalls
import com.whitecoats.clinicplus.apis.ApiUrls
import com.whitecoats.clinicplus.utils.ErrorHandlerClass.errorHandler
import com.whitecoats.clinicplus.viewmodels.CommonViewModel
import com.zoho.salesiqembed.ZohoSalesIQ
import org.json.JSONArray
import org.json.JSONObject

class CreateVideoArticleActivity : AppCompatActivity() {
    private lateinit var rightArrowIcon: ImageView
    private lateinit var externalVideoArticlesLayout: RelativeLayout
    private lateinit var videoArticlesLayout: RelativeLayout
    private var intValue = 0
    private lateinit var languageDetails: CardView
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<*>
    private lateinit var bottomSheet: NestedScrollView
    private lateinit var externalVideotitleText: EditText
    private lateinit var externalVideodescriptionText: EditText
    private lateinit var externalVideoUrlText: EditText
    private lateinit var createButton: Button
    private var catID: String? = null
    private lateinit var jsonValue: JSONObject
    private var category: JSONArray? = null
    private var apiGetPostMethodCalls: ApiGetPostMethodCalls? = null
    private var actionCallBackDescription: ActionMode.Callback? = null
    private var textVideoDescriptionEditTExt: String? = null
    private lateinit var dialog: Dialog
    private lateinit var commonViewModel: CommonViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_communication_create_video_article)
        val intent = intent
        commonViewModel =
            ViewModelProvider(this@CreateVideoArticleActivity)[CommonViewModel::class.java]
        intValue = intent.getIntExtra("articlesValues", 0)
        externalVideoArticlesLayout = findViewById(R.id.externalVideoArticlesLayout)
        videoArticlesLayout = findViewById(R.id.videoArticleLayout)
        languageDetails = findViewById(R.id.videoArticleLanguageDetailsCardView)
        rightArrowIcon = findViewById(R.id.arrowRightIcon)
        externalVideotitleText = findViewById(R.id.externalVideotitleText)
        externalVideodescriptionText = findViewById(R.id.externalVideodescriptionText)
        externalVideoUrlText = findViewById(R.id.externalVideoUrlText)
        createButton = findViewById(R.id.createButton)
        apiGetPostMethodCalls = ApiGetPostMethodCalls()
        ZohoSalesIQ.Tracking.setCustomAction("Communication - Create Video Article")
        rightArrowIcon.setColorFilter(
            ContextCompat.getColor(this, R.color.colorAccent),
            PorterDuff.Mode.SRC_IN
        )
        bottomSheet = findViewById(R.id.bottom_sheet)
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        bottomSheetBehavior.peekHeight = 0
        if (intValue == 1) {
            val backArrow = ContextCompat.getDrawable(this, R.drawable.ic_arrow_back)
            backArrow!!.setColorFilter(
                ContextCompat.getColor(this, R.color.colorWhite),
                PorterDuff.Mode.SRC_ATOP
            )
            val toolbar = findViewById<Toolbar>(R.id.videoArticleCommToolbar)
            toolbar.navigationIcon = backArrow // your drawable
            toolbar.title = resources.getString(R.string.articles)
            setSupportActionBar(toolbar)
            toolbar.setNavigationOnClickListener {
                onBackPressedDispatcher.onBackPressed() // Implemented by activity
            }
        } else {
            val backArrow = ContextCompat.getDrawable(this, R.drawable.ic_arrow_back)
            backArrow!!.setColorFilter(
                ContextCompat.getColor(this, R.color.colorWhite),
                PorterDuff.Mode.SRC_ATOP
            )
            val toolbar1 = findViewById<Toolbar>(R.id.commToolbar1)
            toolbar1.title = resources.getString(R.string.video_details)
            toolbar1.navigationIcon = backArrow // your drawable
            setSupportActionBar(toolbar1)
            toolbar1.setNavigationOnClickListener {
               onBackPressedDispatcher.
               onBackPressed() // Implemented by activity
            }
        }
        if (intValue == 1) {
            videoArticlesLayout.visibility = View.VISIBLE
            externalVideoArticlesLayout.visibility = View.INVISIBLE
        } else {
            videoArticlesLayout.visibility = View.INVISIBLE
            externalVideoArticlesLayout.visibility = View.VISIBLE
        }
        languageDetails.setOnClickListener {
            val bottomSheetDialogFragment = CommunicationLanguageBottomSheet()
            bottomSheetDialogFragment.setupConfig(this@CreateVideoArticleActivity)
            bottomSheetDialogFragment.show(supportFragmentManager, "Bottom Sheet Dialog Fragment")
        }
        textArticlesDetails
        createButton.setOnClickListener {
            if (externalVideotitleText.text.toString().isEmpty()) {
                externalVideotitleText.error = "Title is required"
            } else if (externalVideodescriptionText.text.toString().isEmpty()) {
                externalVideodescriptionText.error = "Description is required"
            } else if (externalVideoUrlText.text.toString().isEmpty()) {
                externalVideoUrlText.error = "Valid video url is required"
            } else {
                ZohoSalesIQ.Tracking.setCustomAction("Communication - Creating New Video")
                createTextArticles()
            }
        }
        actionCallBackDescription = object : ActionMode.Callback {
            override fun onCreateActionMode(actionMode: ActionMode, menu: Menu): Boolean {
                val inflater = actionMode.menuInflater
                inflater.inflate(R.menu.menu_stylefont, menu)
                return true
            }

            @RequiresApi(Build.VERSION_CODES.M)
            override fun onPrepareActionMode(actionMode: ActionMode, menu: Menu): Boolean {
                menu.removeItem(android.R.id.shareText)
                return false
            }

            override fun onActionItemClicked(actionMode: ActionMode, menuItem: MenuItem): Boolean {
                val itemId = menuItem.itemId
                val source = externalVideodescriptionText.text.toString()
                val selectionStart = externalVideodescriptionText.selectionStart
                val selectionEnd = externalVideodescriptionText.selectionEnd
                return when (itemId) {
                    R.id.action_italic -> {
                        val substring = source.substring(selectionStart, selectionEnd)
                        val sb = SpannableStringBuilder(substring)
                        sb.setSpan(
                            StyleSpan(Typeface.ITALIC),
                            0,
                            substring.length,
                            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                        )
                        externalVideodescriptionText.text
                            .replace(selectionStart, selectionEnd, sb)
                        actionMode.finish()
                        true
                    }
                    R.id.action_bold -> {
                        val substring1 = source.substring(selectionStart, selectionEnd)
                        val sb1 = SpannableStringBuilder(substring1)
                        sb1.setSpan(
                            StyleSpan(Typeface.BOLD),
                            0,
                            substring1.length,
                            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                        )
                        externalVideodescriptionText.text
                            .replace(selectionStart, selectionEnd, sb1)
                        actionMode.finish()
                        true
                    }
                    R.id.action_Normal -> {
                        val str: Spannable = externalVideodescriptionText.text
                        val ss = str.getSpans(selectionStart, selectionEnd, StyleSpan::class.java)
                        val underline =
                            str.getSpans(selectionStart, selectionEnd, UnderlineSpan::class.java)
                        val strikethroughSpans = str.getSpans(
                            selectionStart,
                            selectionEnd,
                            StrikethroughSpan::class.java
                        )
                        if (ss.size > 0) {
                            var i = 0
                            while (i < ss.size) {
                                if (ss[i].style == Typeface.BOLD) {
                                    str.removeSpan(ss[i])
                                } else if (ss[i].style == Typeface.ITALIC) {
                                    str.removeSpan(ss[i])
                                } else if (ss[i].style == Typeface.BOLD_ITALIC) {
                                    str.removeSpan(ss[i])
                                }
                                i++
                            }
                        } else if (strikethroughSpans.size > 0) {
                            var i = 0
                            while (i < strikethroughSpans.size) {
                                str.removeSpan(strikethroughSpans[i])
                                i++
                            }
                        } else if (underline.size > 0) {
                            var i = 0
                            while (i < underline.size) {
                                str.removeSpan(underline[i])
                                i++
                            }
                        }
                        externalVideodescriptionText.setText("")
                        externalVideodescriptionText.setText(str)
                        actionMode.finish()
                        true
                    }
                    R.id.action_Strikethrough -> {
                        val substring2 = source.substring(selectionStart, selectionEnd)
                        val sb2 = SpannableStringBuilder(substring2)
                        sb2.setSpan(
                            StrikethroughSpan(),
                            0,
                            substring2.length,
                            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                        )
                        externalVideodescriptionText.text
                            .replace(selectionStart, selectionEnd, sb2)
                        actionMode.finish()
                        true
                    }
                    R.id.action_UnderLine -> {
                        val substringUline = source.substring(selectionStart, selectionEnd)
                        val sbUline = SpannableStringBuilder(substringUline)
                        sbUline.setSpan(
                            UnderlineSpan(),
                            0,
                            substringUline.length,
                            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                        )
                        externalVideodescriptionText.text
                            .replace(selectionStart, selectionEnd, sbUline)
                        actionMode.finish()
                        true
                    }
                    R.id.action_Right -> {
                        val substringAlignRight = source.substring(selectionStart, selectionEnd)
                        val stringRight = SpannableStringBuilder(substringAlignRight)
                        stringRight.setSpan(
                            AlignmentSpan.Standard(Layout.Alignment.ALIGN_OPPOSITE), 0,
                            substringAlignRight.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                        )
                        externalVideodescriptionText.text
                            .replace(selectionStart, selectionEnd, stringRight)
                        actionMode.finish()
                        true
                    }
                    R.id.action_Left -> {
                        val substringAlignLeft = source.substring(selectionStart, selectionEnd)
                        val stringLeft = SpannableStringBuilder(substringAlignLeft)
                        stringLeft.setSpan(
                            AlignmentSpan.Standard(Layout.Alignment.ALIGN_NORMAL), 0,
                            substringAlignLeft.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                        )
                        externalVideodescriptionText.text
                            .replace(selectionStart, selectionEnd, stringLeft)
                        actionMode.finish()
                        true
                    }
                    R.id.action_Center -> {
                        val substringAlignCenter = source.substring(selectionStart, selectionEnd)
                        val stringCenter = SpannableStringBuilder(substringAlignCenter)
                        stringCenter.setSpan(
                            AlignmentSpan.Standard(Layout.Alignment.ALIGN_CENTER), 0,
                            substringAlignCenter.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                        )
                        externalVideodescriptionText.text
                            .replace(selectionStart, selectionEnd, stringCenter)
                        actionMode.finish()
                        true
                    }
                    else -> false
                }
            }

            override fun onDestroyActionMode(actionMode: ActionMode) {}
        }
        externalVideodescriptionText.customSelectionActionModeCallback =
            actionCallBackDescription
    }

    private fun createTextArticles() {
        showCustomProgressAlertDialog(
            "Creating",
            resources.getString(R.string.wait_while_creating)
        )
        val url = ApiUrls.saveArticlesDetailsExternal
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            val s = Html.toHtml(
                externalVideodescriptionText.text,
                Html.FROM_HTML_SEPARATOR_LINE_BREAK_DIV
            )
            val newString = s.replace("&lt;", "<")
            val newString2 = newString.replace("&gt;", ">")
            textVideoDescriptionEditTExt = """<!DOCTYPE html>
<html>
  <body>${appendAnchorTags(newString2)} </body>
""" + "</html>"
        } else {
            textVideoDescriptionEditTExt = """<!DOCTYPE html>
<html>
  <body>${externalVideodescriptionText.text} </body>
""" + "</html>"
        }
        try {
            jsonValue = JSONObject()
            jsonValue.put("catchoice", category)
            jsonValue.put("title", externalVideotitleText.text.toString())
            jsonValue.put("desc", textVideoDescriptionEditTExt)
            jsonValue.put("url", externalVideoUrlText.text.toString())
            jsonValue.put("sharewith", 2)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        commonViewModel.commonViewModelCall(url, jsonValue, Method.POST).observe(
            this@CreateVideoArticleActivity
        ) { result ->
            try {
                val responseObj = JSONObject(result)
                if (responseObj.getInt("status_code") == 200) {
                    //Process os success response
                    dialog.dismiss()
                    Toast.makeText(
                        this@CreateVideoArticleActivity,
                        "Video create successfully",
                        Toast.LENGTH_LONG
                    ).show()
                    finish()
                    val intent = Intent()
                    // Here you can also put data on intent
                    intent.action = "ARTICLES_CREATED"
                    sendBroadcast(intent)
                } else {
                    dialog.dismiss()
                    errorHandler(this@CreateVideoArticleActivity, result)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                dialog.dismiss()
            }
        }
    }

    private val textArticlesDetails: Unit
        get() {
            val url = ApiUrls.getTextArticlesDetails

            commonViewModel.commonViewModelCall(url,JSONObject(),Method.GET).observe(this@CreateVideoArticleActivity
            ) { res->
                try {
                    val responseObj = JSONObject(res)
                    if(responseObj.getInt("status_code")==200) {
                        val response = responseObj.optJSONObject("response")
                        val rootObj = response!!.getJSONObject("response")
                        val catUser = rootObj.getJSONObject("user_cat")
                        catID = catUser.getString("catId")
                        val separated =
                            catID!!.split(",".toRegex()).dropLastWhile { it.isEmpty() }
                                .toTypedArray()
                        category = JSONArray()
                        for (i in separated.indices) {
                            val converCatID = separated[i].toInt()
                            category!!.put(converCatID)
                        }
                    }else{
                        errorHandler(this@CreateVideoArticleActivity, res)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }

    companion object {
        fun appendAnchorTags(text: String?): String {
            val m = Patterns.WEB_URL.matcher(text!!)
            val builder = StringBuffer()
            while (m.find()) {
                var url = m.group()
                if (!URLUtil.isValidUrl(url)) {
                    url = "http://$url"
                }
                m.appendReplacement(builder, "<a href=\"" + url + "\">" + m.group() + "</a>")
            }
            m.appendTail(builder)
            return builder.toString()
        }
    }
    fun showCustomProgressAlertDialog(
        title: String?,
        progressVal: String?

    ) {
        val builder = AlertDialog.Builder(this@CreateVideoArticleActivity)
        builder.setTitle(title)
        builder.setCancelable(false)
        val customLayout = LayoutInflater.from(this@CreateVideoArticleActivity).inflate(R.layout.custom_progress_bar, null)
        builder.setView(customLayout)
        dialog = builder.create()
        val tvBody = customLayout.findViewById<TextView>(R.id.tv_value)
        tvBody.text = progressVal
        dialog.show()
    }
}