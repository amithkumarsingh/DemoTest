package com.whitecoats.clinicplus

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.PorterDuff
import android.graphics.Typeface
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.text.*
import android.text.style.*
import android.util.Patterns
import android.view.*
import android.webkit.URLUtil
import android.widget.*
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModelProvider
import com.android.volley.*
import com.android.volley.Request.Method
import com.android.volley.toolbox.Volley
import com.google.android.gms.common.util.IOUtils
import com.whitecoats.clinicplus.apis.ApiGetPostMethodCalls
import com.whitecoats.clinicplus.apis.ApiUrls
import com.whitecoats.clinicplus.utils.ErrorHandlerClass.errorHandler
import com.whitecoats.clinicplus.viewmodels.CommonViewModel
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.*
import java.text.SimpleDateFormat
import java.util.*

class CreatePdfArticlesActivity : AppCompatActivity() {
    lateinit var imageViewPdfArticle: ImageView
    lateinit var addPdfArticleImage: Button
    private val permissionsRequired = arrayOf(Manifest.permission.CAMERA)
    private val permissionsRequiredStorage = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
    private val name: String? = null
    private var fileUri: Uri? = null
    private var pdfFile1: File? = null
    private var uploadImageResponse = ""
    private var catID: String? = null
    private lateinit var jsonValue: JSONObject
    private var category: JSONArray? = null
    private lateinit var pdfArticleTitleText: EditText
    private lateinit var pdfArticleDescriptionText: EditText
    private lateinit var pdfArticleCreateButton: Button

    /*ENGG-3691 -- Refactoring the code by removing the deprecate functions(StartActivityForResults)*/
    private var launchPDFResults: ActivityResultLauncher<Intent>? = null
    private var launcherCameraResults: ActivityResultLauncher<Intent>? = null
    private var apiGetPostMethodCalls: ApiGetPostMethodCalls? = null
    private var actionCallBackDescription: ActionMode.Callback? = null
    private var textPdfDescriptionEditTExt: String? = null
    private lateinit var dialog: Dialog
    private lateinit var commonViewModel: CommonViewModel
    private var isPDFUploaded: Boolean = false

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    private val permissionsRequiredSDK33Higher =
        arrayOf(Manifest.permission.READ_MEDIA_IMAGES, Manifest.permission.READ_MEDIA_VIDEO)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_pdf_articles)
        apiGetPostMethodCalls = ApiGetPostMethodCalls()
        commonViewModel =
            ViewModelProvider(this@CreatePdfArticlesActivity)[CommonViewModel::class.java]
        val backArrow = ContextCompat.getDrawable(this, R.drawable.ic_arrow_back)
        backArrow!!.setColorFilter(
            ContextCompat.getColor(
                this@CreatePdfArticlesActivity,
                R.color.colorWhite
            ), PorterDuff.Mode.SRC_ATOP
        )
        val toolbar = findViewById<Toolbar>(R.id.pdfArticleCommToolbar)
        toolbar.navigationIcon = backArrow // your drawable
        toolbar.title = "PDFs"
        setSupportActionBar(toolbar)
        toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed() // Implemented by activity
        }
        imageViewPdfArticle = findViewById(R.id.imageViewPdfArticle)
        addPdfArticleImage = findViewById(R.id.addPdfArticleImage)
        pdfArticleTitleText = findViewById(R.id.pdfArticleTitleText)
        pdfArticleDescriptionText = findViewById(R.id.pdfArticleDescriptionText)
        pdfArticleCreateButton = findViewById(R.id.pdfArticleCreateButton)

        // ZohoSalesIQ.Tracking.setCustomAction("Communication - Create PDFs ");

        /*ENGG-3691 -- Refactoring the code by removing the deprecate functions(StartActivityForResults)*/
        //Start
        launchPDFResults = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result: ActivityResult ->
            //Request code 1212
            val data = result.data
            val resultCode = result.resultCode
            if (resultCode == RESULT_OK) {
                // Get the Uri of the selected file
                val uri = data!!.data
                val fileName = getFileName(uri)
                try {
//                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                    // Log.d(TAG, String.valueOf(bitmap));
                    if (fileName!!.contains("pdf")) {
                        // isPDFFile = true;
                        uploadPDFFile(fileName, uri)
                    } else {
                        val bitmapImage = MediaStore.Images.Media.getBitmap(contentResolver, uri)
                        val nh = (bitmapImage.height * (720.0 / bitmapImage.width)).toInt()
                        Bitmap.createScaledBitmap(bitmapImage, 720, nh, true)
                        //noteFileImage.setImageBitmap(scaled);

//                            notesHnFormImage.setVisibility(View.VISIBLE);
//                        notesHnFormImage.setImageBitmap(bitmap);
                        uploadImage()
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
        launcherCameraResults = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) {
            //Request code 1
        }
        //End
        textArticlesDetails
        pdfArticleCreateButton.setOnClickListener {
            if (pdfArticleTitleText.text.toString().isEmpty()) {
                this.pdfArticleTitleText.error = "Title is required"
            } else if (pdfArticleDescriptionText.text.toString().isEmpty()) {
                pdfArticleDescriptionText.error = "Description is required"
            } else if (!isPDFUploaded) {
                Toast.makeText(
                    this@CreatePdfArticlesActivity,
                    "Please upload PDF file",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                isPDFUploaded = false
                createTextArticles()
            }
        }
        addPdfArticleImage.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                if (ActivityCompat.checkSelfPermission(
                        this@CreatePdfArticlesActivity,
                        permissionsRequiredSDK33Higher[0]
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    val intent = Intent()
                    intent.action = Intent.ACTION_GET_CONTENT
                    intent.type = "application/pdf"
                    launchPDFResults!!.launch(intent)
                } else {
                    ActivityCompat.requestPermissions(
                        this@CreatePdfArticlesActivity,
                        permissionsRequiredSDK33Higher,
                        PERMISSION_CALLBACK_CONSTANT
                    )
                }
            } else {
                if (ActivityCompat.checkSelfPermission(
                        this@CreatePdfArticlesActivity,
                        permissionsRequiredStorage[0]
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    val intent = Intent()
                    intent.action = Intent.ACTION_GET_CONTENT
                    intent.type = "application/pdf"
                    launchPDFResults!!.launch(intent)
                } else {
                    ActivityCompat.requestPermissions(
                        this@CreatePdfArticlesActivity,
                        permissionsRequiredStorage,
                        PERMISSION_CALLBACK_CONSTANT
                    )
                }
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
                val source = pdfArticleDescriptionText.text.toString()
                val selectionStart = pdfArticleDescriptionText.selectionStart
                val selectionEnd = pdfArticleDescriptionText.selectionEnd
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
                        pdfArticleDescriptionText.text
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
                        pdfArticleDescriptionText.text
                            .replace(selectionStart, selectionEnd, sb1)
                        actionMode.finish()
                        true
                    }
                    R.id.action_Normal -> {
                        val str: Spannable = pdfArticleDescriptionText.text
                        val ss = str.getSpans(selectionStart, selectionEnd, StyleSpan::class.java)
                        val underline =
                            str.getSpans(selectionStart, selectionEnd, UnderlineSpan::class.java)
                        val strikethroughSpans = str.getSpans(
                            selectionStart,
                            selectionEnd,
                            StrikethroughSpan::class.java
                        )
                        if (ss.isNotEmpty()) {
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
                        } else if (strikethroughSpans.isNotEmpty()) {
                            var i = 0
                            while (i < strikethroughSpans.size) {
                                str.removeSpan(strikethroughSpans[i])
                                i++
                            }
                        } else if (underline.isNotEmpty()) {
                            var i = 0
                            while (i < underline.size) {
                                str.removeSpan(underline[i])
                                i++
                            }
                        }
                        pdfArticleDescriptionText.setText("")
                        pdfArticleDescriptionText.setText(str)
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
                        pdfArticleDescriptionText.text
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
                        pdfArticleDescriptionText.text
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
                        pdfArticleDescriptionText.text
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
                        pdfArticleDescriptionText.text
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
                        pdfArticleDescriptionText.text
                            .replace(selectionStart, selectionEnd, stringCenter)
                        actionMode.finish()
                        true
                    }
                    else -> false
                }
            }

            override fun onDestroyActionMode(actionMode: ActionMode) {}
        }
        pdfArticleDescriptionText.customSelectionActionModeCallback = actionCallBackDescription
    }

    fun openCamera() {
        if (ActivityCompat.checkSelfPermission(
                this@CreatePdfArticlesActivity,
                permissionsRequired[0]
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            val photo = File(Environment.getExternalStorageDirectory(), "Pic1.png")
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
                fileUri = Uri.fromFile(photo)
                intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri)
            } else {
                if (intent.resolveActivity(packageManager) != null) {
                    //Create a file to store the image
                    var photoFile: File? = null
                    try {
                        photoFile = createImageFile()
                    } catch (ex: IOException) {
                        ex.printStackTrace()
                    }
                    if (photoFile != null) {
                        fileUri = FileProvider.getUriForFile(
                            this,
                            applicationContext.packageName + ".provider",
                            photoFile
                        )
                        intent.putExtra(
                            MediaStore.EXTRA_OUTPUT,
                            fileUri
                        )
                    }
                }
            }
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            if (intent.resolveActivity(applicationContext.packageManager) != null) {
                launcherCameraResults!!.launch(intent)
            }
        } else {
            ActivityCompat.requestPermissions(
                this@CreatePdfArticlesActivity,
                permissionsRequired,
                PERMISSION_CALLBACK_CONSTANT
            )
        }
    }

    @Throws(IOException::class)
    private fun createImageFile(): File {
        val timeStamp = SimpleDateFormat(
            "yyyyMMdd_HHmmss",
            Locale.getDefault()
        ).format(Date())
        val imageFileName = "IMG_" + timeStamp + "_"
        val storageDir =
            getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            imageFileName,  /* prefix */
            ".png",  /* suffix */
            storageDir /* directory */
        )
    }

    private fun createTextArticles() {

        showCustomProgressAlertDialog("Creating", resources.getString(R.string.wait_while_creating))

        val url = ApiUrls.savePdfArticles
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            val s = Html.toHtml(
                pdfArticleDescriptionText.text,
                Html.FROM_HTML_SEPARATOR_LINE_BREAK_DIV
            )
            val newString = s.replace("&lt;", "<")
            val newString2 = newString.replace("&gt;", ">")
            textPdfDescriptionEditTExt = """<!DOCTYPE html>
<html>
  <body>${appendAnchorTags(newString2)} </body>
""" + "</html>"
        } else {
            textPdfDescriptionEditTExt = """<!DOCTYPE html>
<html>
  <body>${pdfArticleDescriptionText.text} </body>
""" + "</html>"
        }
        try {
            jsonValue = JSONObject()
            jsonValue.put("pdf_url", uploadImageResponse)
            jsonValue.put("catchoice", category)
            jsonValue.put("title", pdfArticleTitleText.text.toString())
            jsonValue.put("desc", textPdfDescriptionEditTExt)
            jsonValue.put("sharewith", 2)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        commonViewModel.commonViewModelCall(url, jsonValue, Method.POST).observe(
            this@CreatePdfArticlesActivity
        ) { result ->
            try {
                //Process os success response
                dialog.dismiss()
                val responseObj = JSONObject(result)
                if (responseObj.getInt("status_code") == 200) {
                    Toast.makeText(
                        applicationContext,
                        "PDF create successfully",
                        Toast.LENGTH_LONG
                    ).show()
                    finish()
                    val intent = Intent()
                    // Here you can also put data on intent
                    intent.action = "ARTICLES_CREATED"
                    sendBroadcast(intent)
                } else {
                    dialog.dismiss()
                    errorHandler(this@CreatePdfArticlesActivity, result)
                }

            } catch (e: JSONException) {
                e.printStackTrace()
                dialog.dismiss()
            }
        }
    }

    private val textArticlesDetails: Unit
        get() {
            val url = ApiUrls.getTextArticlesDetails

            commonViewModel.commonViewModelCall(url, JSONObject(), Method.GET).observe(
                this@CreatePdfArticlesActivity
            ) { result ->
                try {
                    val responseObj = JSONObject(result)
                    if (responseObj.getInt("status_code") == 200) {
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
                    } else {
                        errorHandler(this@CreatePdfArticlesActivity, result)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_CALLBACK_CONSTANT) {
            //check if all permissions are granted
            var allgranted = false
            for (i in grantResults.indices) {
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    allgranted = true
                } else {
                    allgranted = false
                    break
                }
            }
            if (allgranted) {
                val intent = Intent()
                intent.action = Intent.ACTION_GET_CONTENT
                intent.type = "application/pdf"
                launchPDFResults!!.launch(intent)
            } else {
                Toast.makeText(
                    this@CreatePdfArticlesActivity,
                    resources.getString(R.string.common_storage_permission_message),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    fun getFileDataFromDrawable(bitmap: Bitmap): ByteArray {
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 20, byteArrayOutputStream)
        return byteArrayOutputStream.toByteArray()
    }

    private fun uploadImage() {
        val url = ApiUrls.uploadRecordImage
        showCustomProgressAlertDialog(resources.getString(R.string.uploading_pdf), "Please wait..")

        //our custom volley request
        val volleyMultipartRequest: AppImageUploader = object : AppImageUploader(
            Method.POST, url,
            Response.Listener { response ->
                try {
                    var rootObj = JSONObject(String(response.data))
                    //                            Log.d("Image Upload", rootObj.toString());
                    rootObj = rootObj.getJSONObject("response")
                    val url = rootObj.getString("url")
                    if (url != "") {
                        uploadImageResponse = url
                        Toast.makeText(
                            applicationContext,
                            resources.getString(R.string.upload_pdf_success),
                            Toast.LENGTH_SHORT
                        ).show()
                        isPDFUploaded = true
                    }
                    dialog.dismiss()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            },
            Response.ErrorListener { error ->
                when (error) {
                    is TimeoutError, is NoConnectionError -> {
                        Toast.makeText(
                            applicationContext,
                            resources.getString(R.string.slow_internet_connection),
                            Toast.LENGTH_LONG
                        ).show()
                    }
                    is AuthFailureError -> {
                        Toast.makeText(
                            applicationContext,
                            resources.getString(R.string.slow_internet_connection),
                            Toast.LENGTH_LONG
                        ).show()
                    }
                    is ServerError -> {
                        Toast.makeText(
                            applicationContext,
                            resources.getString(R.string.slow_internet_connection),
                            Toast.LENGTH_LONG
                        ).show()

                        //TODO
                    }
                    is NetworkError -> {
                        Toast.makeText(
                            applicationContext,
                            resources.getString(R.string.slow_internet_connection),
                            Toast.LENGTH_LONG
                        ).show()

                        //TODO
                    }
                    is ParseError -> {
                        Toast.makeText(
                            applicationContext,
                            resources.getString(R.string.slow_internet_connection),
                            Toast.LENGTH_LONG
                        ).show()

                        //TODO
                    }
                    else -> {
                        Toast.makeText(
                            applicationContext,
                            resources.getString(R.string.upload_img_error),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
                dialog.dismiss()
            }) {
            /*
             * If you want to add more parameters with the image
             * you can do it here
             * here we have only one parameter with the image
             * which is tags
             * */
            @Throws(AuthFailureError::class)
            override fun getParams(): Map<String, String> {
                val params: MutableMap<String, String> = HashMap()
                params["path"] = "contentlibrary/" + 2529 + "/"
                return params
            }

            /*
             * Here we are passing image by renaming it with a unique name
             * */
            override fun getByteData(): Map<String, DataPart> {
                val params: MutableMap<String, DataPart> = HashMap()
                //                long imagename = System.currentTimeMillis();
                try {
                    val bytesArray = ByteArray(pdfFile1!!.length().toInt())
                    val fis = FileInputStream(pdfFile1)
                    val a = fis.read(bytesArray) //read file into bytes[]
                    fis.close()
                    params["file"] = DataPart("Communication" + ".pdf", bytesArray)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                return params
            }

            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                val headers = HashMap<String, String>()
                //                headers.put("Content-Type", "application/json");
                headers["App-Origin"] = ApiUrls.appOrigin
                headers["Authorization"] =
                    "Bearer " + ApiUrls.loginToken //eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJodHRwOi8vNGRkYjMxNjkubmdyb2suaW8vYXBpL3YxL2F1dGgvdG9rZW4iLCJpYXQiOjE1MTgwODcyMzAsImV4cCI6MTUxOTI5NjgzMCwibmJmIjoxNTE4MDg3MjMwLCJqdGkiOiJ2UEJnY09hVGdGOU9YaFh4Iiwic3ViIjozMzk1LCJwcnYiOiIyM2JkNWM4OTQ5ZjYwMGFkYjM5ZTcwMWM0MDA4NzJkYjdhNTk3NmY3In0.Y3wU_NBXXhw--b75x4iwo6fmHQQzMLkla2gHrheWqeU");
                return headers
            }
        }

        //adding the request to volley
        Volley.newRequestQueue(this).add(volleyMultipartRequest)
        volleyMultipartRequest.retryPolicy = DefaultRetryPolicy(
            60000,
            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        )
    }

    fun getFileName(uri: Uri?): String? {
        val projection = arrayOf(MediaStore.MediaColumns.DISPLAY_NAME)
        val cursor = managedQuery(uri, projection, null, null, null)
        return if (cursor != null) {
            //HERE YOU WILL GET A NULLPOINTER IF CURSOR IS NULL
            //THIS CAN BE, IF YOU USED OI FILE MANAGER FOR PICKING THE MEDIA
            val column_index = cursor
                .getColumnIndexOrThrow(MediaStore.MediaColumns.DISPLAY_NAME)
            cursor.moveToFirst()
            cursor.getString(column_index)
        } else null
    }

    @SuppressLint("Recycle")
    private fun uploadPDFFile(selectedFilePath: String?, uri: Uri?) {
        val resolver = applicationContext
            .contentResolver
        try {
            val pfd = resolver.openFileDescriptor(uri!!, "r")
            val stream: InputStream = FileInputStream(pfd!!.fileDescriptor)
            val localfile = File(this.cacheDir, selectedFilePath!!)
            val localStream: OutputStream = FileOutputStream(localfile)
            IOUtils.copyStream(stream, localStream)
            pdfFile1 = localfile
            uploadImage()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    companion object {
        private const val PERMISSION_CALLBACK_CONSTANT = 100
        fun appendAnchorTags(text: String?): String {
            val m = Patterns.WEB_URL.matcher(text)
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
        val builder = AlertDialog.Builder(this@CreatePdfArticlesActivity)
        builder.setTitle(title)
        builder.setCancelable(false)
        val customLayout = LayoutInflater.from(this@CreatePdfArticlesActivity)
            .inflate(R.layout.custom_progress_bar, null)
        builder.setView(customLayout)
        dialog = builder.create()
        val tvBody = customLayout.findViewById<TextView>(R.id.tv_value)
        tvBody.text = progressVal
        dialog.show()
    }
}