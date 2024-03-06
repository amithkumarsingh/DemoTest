package com.whitecoats.clinicplus

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.PorterDuff
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.*
import android.webkit.*
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.android.volley.Request
import com.android.volley.toolbox.ImageLoader
import com.android.volley.toolbox.NetworkImageView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.whitecoats.clinicplus.apis.ApiGetPostMethodCalls
import com.whitecoats.clinicplus.apis.ApiUrls
import com.whitecoats.clinicplus.utils.ErrorHandlerClass.errorHandler
import com.zoho.salesiqembed.ZohoSalesIQ
import org.json.JSONObject
import java.io.UnsupportedEncodingException
import java.net.URLEncoder


class CommunicationDetailsActivity : AppCompatActivity() {
    private lateinit var deleteButton: FloatingActionButton

    var mContext: Activity = this // If you are in activity

    private var articlesValues = 0
    private var title: String? = null
    private var description: String? = null
    private var category: String? = null
    private var date: String? = null
    private var content_path: String? = null
    private var articlesId = 0
    private lateinit var communicationDetailsVideoAndTextHeader: TextView
    private lateinit var createdDate: TextView
    private lateinit var communicationDetailsDescriptionText: WebView

    //    private lateinit var youTubeView: YouTubePlayerView
    private lateinit var youTubeView: WebView
    private lateinit var youtubeLayout: LinearLayout
    private var youtubeId: String? = null
    private lateinit var communicationDetailsLayout: RelativeLayout
    private lateinit var communicationTextDetailsLayout: RelativeLayout
    private lateinit var communicationPDFDetailsLayout: RelativeLayout
    private lateinit var libViewTextLoader: ProgressBar
    private lateinit var mainProgressBar: ProgressBar
    lateinit var imageLoader: ImageLoader
    private lateinit var articleImg: NetworkImageView
    private lateinit var communicationDetailsTextHeader: TextView
    private lateinit var dateTextArticles: TextView
    private lateinit var communicationTextDetailsDescriptionText: WebView
    private lateinit var communicationDetailsPDFHeader: TextView
    private lateinit var datePDFArticles: TextView
    private lateinit var communicationPDFDetailsDescriptionText: WebView
    private lateinit var imageNotAvailableText: TextView
    private lateinit var zoomImgLayout: RelativeLayout
    private lateinit var communicationDetailsFabDelete: FloatingActionButton
    private var itemPosition = 0
    private var imageUrl: String? = null
    private var globalApiCall: ApiGetPostMethodCalls? = null
    private var content: String? = null
    private lateinit var communicationContentText: WebView
    private lateinit var communicationContentTextHeader: TextView
    private lateinit var libViewTextLayout: RelativeLayout
    private lateinit var webViewPDF: WebView
    private var apiGetPostMethodCalls: ApiGetPostMethodCalls? = null
    private lateinit var pdfProgressBar: ProgressBar
    private lateinit var viewPDF: RelativeLayout
    private lateinit var dialog: Dialog

    @SuppressLint("SetJavaScriptEnabled", "CutPasteId", "MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_communication_details)
        val intent = intent
        apiGetPostMethodCalls = ApiGetPostMethodCalls()
        communicationContentText = findViewById(R.id.communicationContentText)
        communicationContentTextHeader = findViewById(R.id.communicationContentTextHeader)
        articlesValues = intent.getIntExtra("articlesValues", 0)
        articlesId = intent.getIntExtra("articlesId", 0)
        title = intent.getStringExtra("title")
        val descriptionValue = intent.getStringExtra("description")
        val newString = descriptionValue!!.replace("&lt;", "<")
        val newString2 = newString.replace("&gt;", ">")
        description = newString2
        //        description = intent.getStringExtra("description");
        if (intent.getStringExtra("content") != null && articlesValues == 2) {
            communicationContentText.visibility = View.VISIBLE
            communicationContentTextHeader.visibility = View.VISIBLE
            content = intent.getStringExtra("content")
            communicationContentText.webViewClient = MyWebViewClient()
            communicationContentText.settings.javaScriptEnabled = true
            communicationContentText.settings.javaScriptCanOpenWindowsAutomatically = true
            communicationContentText.settings.mediaPlaybackRequiresUserGesture = false
            communicationContentText.webChromeClient = WebChromeClient()
            communicationContentText.loadDataWithBaseURL(
                null,
                content!!,
                "text/html",
                "UTF-8",
                null
            )
        } else {
            communicationContentText.visibility = View.GONE
            communicationContentTextHeader.visibility = View.GONE
        }
        category = intent.getStringExtra("category")
        date = intent.getStringExtra("date")
        content_path = intent.getStringExtra("content_path")
        itemPosition = intent.getIntExtra("itemPosition", -1)
        globalApiCall = ApiGetPostMethodCalls()
        articleImg = findViewById(R.id.libViewArticleImage)
        libViewTextLayout = findViewById(R.id.libViewTextLayout)
        imageNotAvailableText = findViewById(R.id.imageNotAvailableText)
        communicationDetailsFabDelete = findViewById(R.id.communicationDetailsFabDelete)
        communicationDetailsVideoAndTextHeader =
            findViewById(R.id.communicationDetailsVideoAndTextHeader)
        createdDate = findViewById(R.id.date)
        communicationDetailsDescriptionText = findViewById(R.id.communicationDetailsDescriptionText)
        communicationDetailsTextHeader = findViewById(R.id.communicationDetailsTextHeader)
        dateTextArticles = findViewById(R.id.dateTextArticles)
        communicationTextDetailsDescriptionText =
            findViewById(R.id.communicationTextDetailsDescriptionText)
        zoomImgLayout = findViewById(R.id.libViewZoomImgLayout)
        communicationDetailsPDFHeader = findViewById(R.id.communicationDetailsPDFHeader)
        datePDFArticles = findViewById(R.id.datePDFArticles)
        communicationPDFDetailsDescriptionText =
            findViewById(R.id.communicationPDFDetailsDescriptionText)
        webViewPDF = findViewById(R.id.webViewPDF)
        pdfProgressBar = findViewById(R.id.pdfProgressBar)
        viewPDF = findViewById(R.id.viewPDF)
        communicationDetailsVideoAndTextHeader.text = title
        createdDate.text = date
        communicationDetailsDescriptionText.webViewClient = MyWebViewClient()
        communicationDetailsDescriptionText.settings.javaScriptEnabled = true
        communicationDetailsDescriptionText.settings.javaScriptCanOpenWindowsAutomatically =
            true
        communicationDetailsDescriptionText.settings.mediaPlaybackRequiresUserGesture = false
        communicationDetailsDescriptionText.webChromeClient = WebChromeClient()
        communicationDetailsDescriptionText.loadDataWithBaseURL(
            null,
            description!!,
            "text/html",
            "UTF-8",
            null
        )
        youtubeLayout = findViewById(R.id.libViewYoutubeLayout)
        youTubeView = findViewById(R.id.libViewYoutubeView)
        communicationDetailsLayout = findViewById(R.id.communicationDetailsLayout)
        communicationTextDetailsLayout = findViewById(R.id.communicationTextDetailsLayout)
        communicationPDFDetailsLayout = findViewById(R.id.communicationPDFDetailsLayout)
        libViewTextLoader = findViewById(R.id.libViewTextLoader)
        if (articlesValues == 1) {
            communicationDetailsLayout.visibility = View.VISIBLE
            communicationTextDetailsLayout.visibility = View.GONE
            communicationPDFDetailsLayout.visibility = View.GONE
//            youTubeInfo
//            playYoutube()

            youtubeId = content_path
            youtubeId = if (youtubeId!!.contains("=")) {
                youtubeId!!.split("=".toRegex()).dropLastWhile { it.isEmpty() }
                    .toTypedArray()[1]
            } else {
                val splitArr =
                    youtubeId!!.split("/".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                splitArr[splitArr.size - 1]
            }


            val videoTwo = """<iframe  id="ytplayer" type="text/html" width="100%" height="100%"
             src="https://www.youtube.com/embed/$youtubeId?autoplay=1&origin=http://example.com"
               frameborder="0" allowfullscreen></iframe>"""

            youTubeView.loadData(videoTwo, "text/html", "utf-8")
            youTubeView.getSettings().setJavaScriptEnabled(true)
            youTubeView.setWebViewClient(object : WebViewClient() {
                override fun shouldOverrideUrlLoading(
                    view: WebView,
                    request: WebResourceRequest
                ): Boolean {
                    return false
                }
            })

            youTubeView.setWebViewClient(WebViewClient())
//            youTubeView.setWebChromeClient(MyChrome())
            youTubeView.webChromeClient = CustomChromeClient(this)

            ZohoSalesIQ.Tracking.setPageTitle("CommunicationDetails - Video")
        } else if (articlesValues == 2) {
            communicationDetailsLayout.visibility = View.GONE
            communicationPDFDetailsLayout.visibility = View.GONE
            communicationTextDetailsLayout.visibility = View.VISIBLE
            ZohoSalesIQ.Tracking.setPageTitle("CommunicationDetails - Text")
            communicationDetailsTextHeader.text = title
            dateTextArticles.text = date
            communicationTextDetailsDescriptionText.webViewClient = MyWebViewClient()
            communicationTextDetailsDescriptionText.settings.javaScriptEnabled = true
            communicationTextDetailsDescriptionText.settings.javaScriptCanOpenWindowsAutomatically =
                true
            communicationTextDetailsDescriptionText.settings.mediaPlaybackRequiresUserGesture =
                false
            communicationTextDetailsDescriptionText.webChromeClient = WebChromeClient()
            communicationTextDetailsDescriptionText.loadDataWithBaseURL(
                null,
                description!!,
                "text/html",
                "UTF-8",
                null
            )
            if (content_path!!.isEmpty()) {
                imageNotAvailableText.visibility = View.VISIBLE
                libViewTextLoader.visibility = View.GONE
                libViewTextLayout.visibility = View.GONE
            } else {
                imageNotAvailableText.visibility = View.GONE
                libViewTextLayout.visibility = View.VISIBLE
                getArticleImage(content_path)
            }
        } else {
            communicationDetailsLayout.visibility = View.GONE
            communicationTextDetailsLayout.visibility = View.GONE
            communicationPDFDetailsLayout.visibility = View.VISIBLE
            content = intent.getStringExtra("content")
            getPdfImage(content, 0)
            communicationDetailsPDFHeader.text = title
            datePDFArticles.text = date
            communicationPDFDetailsDescriptionText.webViewClient = MyWebViewClient()
            communicationPDFDetailsDescriptionText.settings.javaScriptEnabled = true
            communicationPDFDetailsDescriptionText.settings.javaScriptCanOpenWindowsAutomatically =
                true
            communicationPDFDetailsDescriptionText.settings.mediaPlaybackRequiresUserGesture =
                false
            communicationPDFDetailsDescriptionText.webChromeClient = WebChromeClient()
            communicationPDFDetailsDescriptionText.loadDataWithBaseURL(
                null,
                description!!,
                "text/html",
                "UTF-8",
                null
            )
        }
        deleteButton = findViewById(R.id.communicationDetailsFabDelete)
        deleteButton.setColorFilter(
            ContextCompat.getColor(this, R.color.colorWhite),
            PorterDuff.Mode.SRC_IN
        )
        articleImg.setOnClickListener {
            ZohoSalesIQ.Tracking.setCustomAction("CommunicationDetails - Zooming Image")
            val url = imageUrl
            if (url != null && !url.equals("", ignoreCase = true)) {
                val intentNew = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                // Note the Chooser below. If no applications match,
                // Android displays a system message.So here there is no need for try-catch.
                startActivity(Intent.createChooser(intentNew, "Browse with"))
            }
        }
        zoomImgLayout.setOnClickListener {
            zoomImgLayout.visibility = View.GONE
            articleImg.layoutParams = RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                resources.getDimension(R.dimen.libTextImg).toInt()
            )
            articleImg.adjustViewBounds = true
        }
        communicationDetailsFabDelete.setOnClickListener {
            val builder = AlertDialog.Builder(this@CommunicationDetailsActivity)
            builder.setTitle("Delete Confirmation !")
            builder.setMessage("Do you really want to Delete ?")
            builder.setCancelable(false)
            builder.setPositiveButton("Yes") { _, _ ->
                deleteContentArticles()
                ZohoSalesIQ.Tracking.setCustomAction("CommunicationDetails - Deleting Article")
            }
            builder.setNegativeButton("No") { _, _ ->
            }
            builder.show()
        }
        viewPDF.setOnClickListener { getPdfImage(content, 1) }
    }

//    private fun playYoutube() {
//        // Initializing video player with developer key
//        //youTubeView.initialize("AIzaSyD1MssHFt3B2-2GZRytYPfSXAoJPPZW5n8", this);//orininal
//        youTubeView.initialize(BuildConfig.YOUTUBE_VIDEO_API_KEY, this)
//    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.communication_detail_menu, menu)
        val icon = menu.findItem(R.id.commDetailsDelete).icon
        icon!!.setColorFilter(
            ContextCompat.getColor(this, R.color.colorWhite),
            PorterDuff.Mode.SRC_IN
        )
        menu.findItem(R.id.commDetailsDelete).icon = icon
        val icon2 = menu.findItem(R.id.commDetailsEdit).icon
        icon2!!.setColorFilter(
            ContextCompat.getColor(this, R.color.colorWhite),
            PorterDuff.Mode.SRC_IN
        )
        menu.findItem(R.id.commDetailsEdit).icon = icon2
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity clinicplus AndroidManifest.xml.
        item.itemId
        return super.onOptionsItemSelected(item)
    }

//    override fun onInitializationFailure(
//        provider: YouTubePlayer.Provider,
//        errorReason: YouTubeInitializationResult
//    ) {
//        if (errorReason.isUserRecoverableError) {
//            errorReason.getErrorDialog(this, RECOVERY_DIALOG_REQUEST).show()
//        } else {
//            val errorMessage = "Error"
//            Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show()
//        }
//    }

//    override fun onInitializationSuccess(
//        provider: YouTubePlayer.Provider,
//        player: YouTubePlayer, wasRestored: Boolean
//    ) {
//        if (!wasRestored) {
//
//            // loadVideo() will auto play video
//            // Use cueVideo() method, if you don't want to play it automatically
//            youtubeId = content_path
//            youtubeId = if (youtubeId!!.contains("=")) {
//                youtubeId!!.split("=".toRegex()).dropLastWhile { it.isEmpty() }
//                    .toTypedArray()[1]
//            } else {
//                val splitArr =
//                    youtubeId!!.split("/".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
//                splitArr[splitArr.size - 1]
//            }
//
//            //getYouTubeInfo();
//            //playYoutube();
//            player.loadVideo(youtubeId)
//
//            // Hiding player controls
//            player.setPlayerStyle(YouTubePlayer.PlayerStyle.DEFAULT)
//        }
//    }
//
//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
//        if (requestCode == RECOVERY_DIALOG_REQUEST) {
//            // Retry initialization if user performed a recovery action
//            //getYouTubePlayerProvider().initialize("AIzaSyD1MssHFt3B2-2GZRytYPfSXAoJPPZW5n8", this);//original
//            youTubePlayerProvider.initialize(BuildConfig.YOUTUBE_VIDEO_API_KEY, this)
//        }
//    }

//    private val youTubePlayerProvider: YouTubePlayer.Provider
//        get() = findViewById<View>(R.id.libViewYoutubeView) as YouTubePlayerView
//    private val youTubeInfo: Unit
//        get() {
//            val url = ApiUrls.getYouTubeInfo + "?key=" + BuildConfig.YOUTUBE_VIDEO_API_KEY + "" +
//                    "&part=snippet&id=" + youtubeId
//
//
//            globalApiCall!!.volleyApiRequestData(
//                url,
//                Request.Method.GET,
//                null,
//                this,
//                object : VolleyCallback {
//                    override fun onSuccess(result: String) {
//                        try {
//                            val response = JSONObject(result)
//                            val itemArr = response.getJSONArray("items")
//                            var snippet = itemArr.getJSONObject(0)
//                            snippet = snippet.getJSONObject("snippet")
//                        } catch (e: Exception) {
//                            e.printStackTrace()
//                        }
//                    }
//
//                    override fun onError(err: String) {
//                        errorHandler(this@CommunicationDetailsActivity, err)
//                    }
//                })
//        }

    private fun getArticleImage(path: String?) {
        val url = ApiUrls.getArticleImage
        val map = JSONObject()
        try {
            map.put("url", path)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        globalApiCall!!.volleyApiRequestData(
            url,
            Request.Method.POST,
            map,
            this,
            object : VolleyCallback {
                override fun onSuccess(result: String) {
                    try {
                        val response = JSONObject(result)
                        libViewTextLoader.visibility = View.GONE
                        imageLoader = AppImageRequest.getInstance(this@CommunicationDetailsActivity)
                            .imageLoader
                        //Image URL - This can point to any image file supported by Android
                        imageLoader.get(
                            response.getString("response"), ImageLoader.getImageListener(
                                articleImg,
                                0, 0
                            )
                        )
                        articleImg.setImageUrl(response.getString("response"), imageLoader)
                        imageUrl = response.getString("response")
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                override fun onError(err: String) {
                    errorHandler(this@CommunicationDetailsActivity, err)
                }
            })
    }

    private fun deleteContentArticles() {
        showCustomProgressAlertDialog(
            resources.getString(R.string.delete),
            resources.getString(R.string.wait_while_we_updating)
        )
        val url = ApiUrls.deleteContentArticles + articlesId
        globalApiCall!!.volleyApiRequestData(
            url,
            Request.Method.DELETE,
            null,
            this,
            object : VolleyCallback {
                override fun onSuccess(result: String) {
                    try {
                        val response = JSONObject(result)
                        dialog.dismiss()
                        val deleteResponse = response.getInt("response")
                        if (deleteResponse == 1) {
                            Toast.makeText(
                                applicationContext,
                                "You've deleted successfully",
                                Toast.LENGTH_SHORT
                            ).show()
                            CommunicationListActivity.commDetailsModelList!!.removeAt(itemPosition)
                            CommunicationListActivity.commDetailsListAdapter!!.notifyDataSetChanged()
                            finish()
                            val intent = Intent()
                            // Here you can also put data on intent
                            intent.action = "ARTICLES_CREATED"
                            sendBroadcast(intent)
                        }
                    } catch (e: Exception) {
                        dialog.dismiss()
                        e.printStackTrace()
                    }
                }

                override fun onError(err: String) {
                    dialog.dismiss()
                    errorHandler(this@CommunicationDetailsActivity, err)
                }
            })
    }

    inner class MyWebViewClient : WebViewClient() {
        override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean {
            val i = Intent(Intent.ACTION_VIEW, request.url)
            startActivity(i)
            return true
        }
    }

    @SuppressLint("SetJavaScriptEnabled", "ClickableViewAccessibility")
    @Throws(UnsupportedEncodingException::class)
    fun generateImageFromPdf(pdfUrl: String?) {
        webViewPDF.settings.setSupportZoom(true)
        webViewPDF.settings.javaScriptEnabled = true
        webViewPDF.settings.allowFileAccessFromFileURLs = true
        webViewPDF.settings.allowUniversalAccessFromFileURLs = true
        webViewPDF.settings.pluginState = WebSettings.PluginState.ON
        webViewPDF.settings.loadWithOverviewMode = true

        // disable scroll on touch
        webViewPDF.setOnTouchListener { _, event -> event.action == MotionEvent.ACTION_MOVE }
        webViewPDF.isVerticalScrollBarEnabled = false
        webViewPDF.isHorizontalScrollBarEnabled = false
        val url = "https://docs.google.com/gview?embedded=true&url=" + URLEncoder.encode(
            pdfUrl,
            "ISO-8859-1"
        )
        webViewPDF.loadUrl(url)
        webViewPDF.webViewClient = object : WebViewClient() {
            @Deprecated("Deprecated in Java")
            override fun shouldOverrideUrlLoading(view: WebView?, url: String): Boolean {
                view!!.loadUrl(url)
                return true
            }

            override fun onPageStarted(view: WebView?, url: String, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
                if (pdfProgressBar.visibility == View.GONE) {
                    pdfProgressBar.visibility = View.VISIBLE
                }
            }

            override fun onPageFinished(view: WebView?, url: String) {
                if (pdfProgressBar.visibility == View.VISIBLE) {
                    pdfProgressBar.visibility = View.GONE
                }
                if (view!!.title == "") {
                    view.loadUrl(url)
                }
            }

            @Deprecated("Deprecated in Java")
            override fun onReceivedError(
                view: WebView,
                errorCode: Int,
                description: String,
                failingUrl: String
            ) {
                if (pdfProgressBar.visibility == View.VISIBLE) {
                    pdfProgressBar.visibility = View.GONE
                }
            }
        }
    }

    private fun getPdfImage(path: String?, buttonClick: Int) {
        val url = ApiUrls.getArticleImage
        val map = JSONObject()
        try {
            map.put("url", path)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        // prepare the Request
        apiGetPostMethodCalls!!.volleyApiRequestData(
            url,
            Request.Method.POST,
            map,
            this@CommunicationDetailsActivity,
            object : VolleyCallback {
                override fun onSuccess(result: String) {
                    // display response
                    try {
                        val response = JSONObject(result)
                        Log.d("Article Detail Response", response.getString("response"))
                        if (buttonClick == 0) {
                            generateImageFromPdf(response.getString("response"))
                        } else {
                            val browserIntent = Intent(
                                Intent.ACTION_VIEW,
                                Uri.parse(response.getString("response"))
                            )
                            startActivity(browserIntent)
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                override fun onError(err: String) {
                    errorHandler(this@CommunicationDetailsActivity, err)
                }
            })
    }

    companion object {
        private const val RECOVERY_DIALOG_REQUEST = 1
    }

    fun showCustomProgressAlertDialog(
        title: String?,
        progressVal: String?

    ) {
        val builder = AlertDialog.Builder(this@CommunicationDetailsActivity)
        builder.setTitle(title)
        builder.setCancelable(false)
        val customLayout =
            LayoutInflater.from(this@CommunicationDetailsActivity)
                .inflate(R.layout.custom_progress_bar, null)
        builder.setView(customLayout)
        dialog = builder.create()
        val tvBody = customLayout.findViewById<TextView>(R.id.tv_value)
        tvBody.text = progressVal
        dialog.show()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        youTubeView.saveState(outState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        youTubeView.restoreState(savedInstanceState)
    }


    class CustomChromeClient internal constructor(
        private val activity: Activity
    ) :
        WebChromeClient() {
        private var mCustomView: View? = null
        private var mCustomViewCallback: CustomViewCallback? = null
        private var mOriginalOrientation = 0
        private var mOriginalSystemUiVisibility = 0
        override fun getDefaultVideoPoster(): Bitmap? {
            return if (mCustomView == null) {
                null
            } else BitmapFactory.decodeResource(activity.resources, 2130837573)
        }

        override fun onHideCustomView() {
            mCustomViewCallback!!.onCustomViewHidden()
            mCustomViewCallback = null
            (activity.window.decorView as FrameLayout).removeView(mCustomView)
            mCustomView = null
            activity.window.decorView.systemUiVisibility = mOriginalSystemUiVisibility
            activity.requestedOrientation = mOriginalOrientation
            activity.window.decorView.invalidate()
        }

        override fun onShowCustomView(
            paramView: View?,
            paramCustomViewCallback: CustomViewCallback?
        ) {
            if (mCustomView != null) {
                onHideCustomView()
                return
            }
            mCustomView = paramView
            mOriginalSystemUiVisibility = activity.window.decorView.systemUiVisibility
            mOriginalOrientation = activity.requestedOrientation
            mCustomViewCallback = paramCustomViewCallback
            (activity.window.decorView as FrameLayout).addView(
                mCustomView,
                FrameLayout.LayoutParams(-1, -1)
            )
            activity.window.decorView.systemUiVisibility = 3846 or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        }
    }



}