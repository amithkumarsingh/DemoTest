package com.whitecoats.clinicplus

import android.Manifest
import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
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
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModelProvider
import com.android.volley.*
import com.android.volley.Request.Method
import com.android.volley.toolbox.Volley
import com.whitecoats.clinicplus.apis.ApiGetPostMethodCalls
import com.whitecoats.clinicplus.apis.ApiUrls
import com.whitecoats.clinicplus.utils.ErrorHandlerClass.errorHandler
import com.whitecoats.clinicplus.viewmodels.CommonViewModel
import com.zoho.salesiqembed.ZohoSalesIQ
import org.json.JSONArray
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class CreateTextArticleActivity : AppCompatActivity() {
    private var actionCallBackDescription: ActionMode.Callback? = null
    private var actionCallBackContent: ActionMode.Callback? = null
    lateinit var imageViewTextArticle: ImageView
    lateinit var addTextArticleImage: Button
    private val permissionsRequired = arrayOf(Manifest.permission.CAMERA)
    private var fileUri: Uri? = null
    private var uploadImageResponse = ""
    private val formImageID = 44555
    private var catID: String? = null
    private lateinit var jsonValue: JSONObject
    private var category: JSONArray? = null
    private lateinit var textArticleTitleText: EditText
    private lateinit var textArticleDescriptionText: EditText
    private lateinit var textArticleContentText: EditText
    private lateinit var textArticleCreateButton: Button

    /*ENGG-3691 -- Refactoring the code by removing the deprecate functions(StartActivityForResults)*/
    private var launchGalleryResults: ActivityResultLauncher<Intent>? = null
    private var launcherCameraResults: ActivityResultLauncher<Intent>? = null
    private var globalApiCall: ApiGetPostMethodCalls? = null
    private var textArticleDescriptionEditTExt: String? = null
    private var textArticleContentEditTExt: String? = null
    private lateinit var dialog: Dialog
    private lateinit var commonViewModel: CommonViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_communication_create_text_article)
        val backArrow = ContextCompat.getDrawable(this, R.drawable.ic_arrow_back)
        backArrow!!.setColorFilter(
            ContextCompat.getColor(this, R.color.colorWhite),
            PorterDuff.Mode.SRC_ATOP
        )
        val toolbar = findViewById<Toolbar>(R.id.textArticleCommToolbar)
        toolbar.navigationIcon = backArrow // your drawable
        toolbar.title = resources.getString(R.string.add_article)
        commonViewModel =
            ViewModelProvider(this@CreateTextArticleActivity)[CommonViewModel::class.java]
        setSupportActionBar(toolbar)
        toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed() // Implemented by activity
        }
        imageViewTextArticle = findViewById(R.id.imageViewTextArticle)
        addTextArticleImage = findViewById(R.id.addTextArticleImage)
        textArticleTitleText = findViewById(R.id.textArticleTitleText)
        textArticleDescriptionText = findViewById(R.id.textArticleDescriptionText)
        textArticleContentText = findViewById(R.id.textArticleContentText)
        textArticleCreateButton = findViewById(R.id.textArticleCreateButton)
        globalApiCall = ApiGetPostMethodCalls()
        /*ENGG-3691 -- Refactoring the code by removing the deprecate functions(StartActivityForResults)*/
        //Start
        launchGalleryResults = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result: ActivityResult ->
            //Request code 2
            val data = result.data
            val resultCode = result.resultCode
            if (resultCode == RESULT_OK) {
                val uri = data!!.data
                try {
                    val bitmapImage = MediaStore.Images.Media.getBitmap(contentResolver, uri)
                    val nh = (bitmapImage.height * (720.0 / bitmapImage.width)).toInt()
                    val scaled = Bitmap.createScaledBitmap(bitmapImage, 720, nh, true)
                    imageViewTextArticle.setImageBitmap(scaled)
                    uploadImage(scaled)
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
        launcherCameraResults = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result: ActivityResult ->
            //request code 1
            val data = result.data
            val resultCode = result.resultCode
            if (resultCode == RESULT_OK) {
                try {
                    val contentResolver = contentResolver

                    // Use the content resolver to open camera taken image input stream through image uri.
                    val inputStream = contentResolver.openInputStream(fileUri!!)
                    val bitmapImage = BitmapFactory.decodeStream(inputStream)
                    val nh = (bitmapImage.height * (720.0 / bitmapImage.width)).toInt()
                    val scaled = Bitmap.createScaledBitmap(bitmapImage, 720, nh, true)
                    imageViewTextArticle.setImageBitmap(scaled)
                    uploadImage(scaled) //later uncomment
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
        //End
        ZohoSalesIQ.Tracking.setCustomAction("Communication - Create Text Article")
        textArticlesDetails
        textArticleCreateButton.setOnClickListener {
            if (textArticleTitleText.getText().toString().isEmpty()) {
                textArticleTitleText.setError("Title is required")
            } else if (textArticleDescriptionText.getText().toString().isEmpty()) {
                textArticleDescriptionText.setError("Description is required")
            } else if (textArticleContentText.getText().toString().isEmpty()) {
                textArticleContentText.setError("Content is required")
            } else {
                ZohoSalesIQ.Tracking.setCustomAction("Communication - Creating New Text")
                createTextArticles()
            }
        }
        addTextArticleImage.setOnClickListener {
            if (ActivityCompat.checkSelfPermission(
                    this@CreateTextArticleActivity,
                    permissionsRequired[0]
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                val options = arrayOf<CharSequence>("Take Photo", "Choose From Gallery", "Cancel")
                val builder = AlertDialog.Builder(this@CreateTextArticleActivity)
                builder.setTitle("Select Option")
                builder.setItems(options) { dialog: DialogInterface, item: Int ->
                    if (options[item] == "Take Photo") {
                        dialog.dismiss()
                        openCamera()
                    } else if (options[item] == "Choose From Gallery") {
                        dialog.dismiss()
                        val intent = Intent()
                        intent.type = "image/*"
                        intent.action = Intent.ACTION_GET_CONTENT
                        launchGalleryResults!!.launch(
                            Intent.createChooser(
                                intent,
                                resources.getString(R.string.common_select_picture)
                            )
                        )
                    } else if (options[item] == "Cancel") {
                        dialog.dismiss()
                    }
                }
                builder.show()
            } else {
                ActivityCompat.requestPermissions(
                    this@CreateTextArticleActivity,
                    permissionsRequired,
                    PERMISSION_CALLBACK_CONSTANT
                )
            }
        }
        actionCallBackDescription = object : ActionMode.Callback {
            override fun onCreateActionMode(actionMode: ActionMode, menu: Menu): Boolean {
                val inflater = actionMode.menuInflater
                inflater.inflate(R.menu.menu_stylefont, menu)
                return true
            }

            @RequiresApi(api = Build.VERSION_CODES.M)
            override fun onPrepareActionMode(actionMode: ActionMode, menu: Menu): Boolean {
                menu.removeItem(android.R.id.shareText)
                return false
            }

            override fun onActionItemClicked(actionMode: ActionMode, menuItem: MenuItem): Boolean {
                val itemId = menuItem.itemId
                val source = textArticleDescriptionText.getText().toString()
                val selectionStart = textArticleDescriptionText.getSelectionStart()
                val selectionEnd = textArticleDescriptionText.getSelectionEnd()
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
                        textArticleDescriptionText.getText()
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
                        textArticleDescriptionText.getText()
                            .replace(selectionStart, selectionEnd, sb1)
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
                        textArticleDescriptionText.getText()
                            .replace(selectionStart, selectionEnd, sb2)
                        actionMode.finish()
                        true
                    }
                    R.id.action_Normal -> {
                        val str: Spannable = textArticleDescriptionText.getText()
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
                        textArticleDescriptionText.setText("")
                        textArticleDescriptionText.setText(str)
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
                        textArticleDescriptionText.getText()
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
                        textArticleDescriptionText.getText()
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
                        textArticleDescriptionText.getText()
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
                        textArticleDescriptionText.getText()
                            .replace(selectionStart, selectionEnd, stringCenter)
                        actionMode.finish()
                        true
                    }
                    else -> false
                }
            }

            override fun onDestroyActionMode(actionMode: ActionMode) {}
        }
        textArticleDescriptionText.setCustomSelectionActionModeCallback(actionCallBackDescription)
        actionCallBackContent = object : ActionMode.Callback {
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
                val source = textArticleContentText.getText().toString()
                val selectionStart = textArticleContentText.getSelectionStart()
                val selectionEnd = textArticleContentText.getSelectionEnd()
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
                        textArticleContentText.getText().replace(selectionStart, selectionEnd, sb)
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
                        textArticleContentText.getText().replace(selectionStart, selectionEnd, sb1)
                        actionMode.finish()
                        true
                    }
                    R.id.action_Normal -> {
                        val str: Spannable = textArticleContentText.getText()
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
                        textArticleContentText.setText("")
                        textArticleContentText.setText(str)
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
                        textArticleContentText.getText().replace(selectionStart, selectionEnd, sb2)
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
                        textArticleContentText.getText()
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
                        textArticleContentText.getText()
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
                        textArticleContentText.getText()
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
                        textArticleContentText.getText()
                            .replace(selectionStart, selectionEnd, stringCenter)
                        actionMode.finish()
                        true
                    }
                    else -> false
                }
            }

            override fun onDestroyActionMode(actionMode: ActionMode) {}
        }
        textArticleContentText.setCustomSelectionActionModeCallback(actionCallBackContent)
    }

    fun openCamera() {
        if (ActivityCompat.checkSelfPermission(
                this@CreateTextArticleActivity,
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
                this@CreateTextArticleActivity,
                permissionsRequired,
                PERMISSION_CALLBACK_CONSTANT
            )
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
                openCamera()
            } else {
                Toast.makeText(
                    this@CreateTextArticleActivity,
                    resources.getString(R.string.common_camera_permission_message),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun uploadImage(currentImage: Bitmap) {
        val url = ApiUrls.textArticleUploadImage
        showCustomProgressAlertDialog(
            resources.getString(R.string.common_uploading_image),
            "Please wait.."
        )

        //our custom volley request
        val volleyMultipartRequest: AppImageUploader = object : AppImageUploader(
            Method.POST, url,
            Response.Listener { response: NetworkResponse ->
                try {
                    dialog.dismiss()
                    var rootObj = JSONObject(String(response.data))
                    rootObj = rootObj.getJSONObject("response")
                    val url1 = rootObj.getString("url")
                    if (url1 != "") {
                        uploadImageResponse = url1
                        Toast.makeText(
                            applicationContext,
                            resources.getString(R.string.image_upload_sucessfully),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            },
            Response.ErrorListener { error: VolleyError? ->
                when (error) {
                    is TimeoutError, is NoConnectionError -> {
                        Toast.makeText(
                            applicationContext,
                            resources.getString(R.string.slow_internet_connection),
                            Toast.LENGTH_LONG
                        ).show()
                    }
                    is AuthFailureError -> {
                        //TODO
                        Toast.makeText(
                            applicationContext,
                            resources.getString(R.string.slow_internet_connection),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    is ServerError -> {
                        //TODO
                        Toast.makeText(
                            applicationContext,
                            resources.getString(R.string.slow_internet_connection),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    is NetworkError -> {
                        //TODO
                        Toast.makeText(
                            applicationContext,
                            resources.getString(R.string.slow_internet_connection),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    is ParseError -> {
                        //TODO
                        Toast.makeText(
                            applicationContext,
                            resources.getString(R.string.slow_internet_connection),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    else -> {
                        Toast.makeText(
                            applicationContext,
                            resources.getString(R.string.image_upload_failed),
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
                //params.put("path", "financial-details/" + formImageID + "/");
                params["path"] = "contentlibrary/$formImageID/"
                return params
            }

            /*
             * Here we are passing image by renaming it with a unique name
             * */
            override fun getByteData(): Map<String, DataPart> {
                val params: MutableMap<String, DataPart> = HashMap()
                params["file"] = DataPart("Record" + ".png", getFileDataFromDrawable(currentImage))
                return params
            }

            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                val headers = HashMap<String, String>()
                headers["App-Origin"] = ApiUrls.appOrigin
                headers["Authorization"] =
                    "Bearer " + ApiUrls.loginToken //eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJodHRwOi8vNGRkYjMxNjkubmdyb2suaW8vYXBpL3YxL2F1dGgvdG9rZW4iLCJpYXQiOjE1MTgwODcyMzAsImV4cCI6MTUxOTI5NjgzMCwibmJmIjoxNTE4MDg3MjMwLCJqdGkiOiJ2UEJnY09hVGdGOU9YaFh4Iiwic3ViIjozMzk1LCJwcnYiOiIyM2JkNWM4OTQ5ZjYwMGFkYjM5ZTcwMWM0MDA4NzJkYjdhNTk3NmY3In0.Y3wU_NBXXhw--b75x4iwo6fmHQQzMLkla2gHrheWqeU");
                return headers
            }
        }

        //adding the request to volley
        Volley.newRequestQueue(this).add(volleyMultipartRequest)
    }

    fun getFileDataFromDrawable(bitmap: Bitmap): ByteArray {
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 20, byteArrayOutputStream)
        return byteArrayOutputStream.toByteArray()
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

    //String currentString = "Fruit: they taste good";
    private val textArticlesDetails: Unit
        get() {
            val url = ApiUrls.getTextArticlesDetails

            commonViewModel.commonViewModelCall(url, JSONObject(), Method.GET).observe(
                this@CreateTextArticleActivity
            ) { result ->
                try {
                    val responseNew = JSONObject(result)
                    if (responseNew.getInt("status_code") == 200) {
                        val response = responseNew.optJSONObject("response")

                        val rootObj = response!!.getJSONObject("response")
                        val catUser = rootObj.getJSONObject("user_cat")
                        catID = catUser.getString("catId")
                        //String currentString = "Fruit: they taste good";
                        val separated =
                            catID!!.split(",".toRegex()).dropLastWhile { it.isEmpty() }
                                .toTypedArray()
                        category = JSONArray()
                        for (i in separated.indices) {
                            val converCatID = separated[i].toInt()
                            category!!.put(converCatID)
                        }
                    } else {
                        errorHandler(this@CreateTextArticleActivity, result)

                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }

    private fun createTextArticles() {

        showCustomProgressAlertDialog(
            "Creating",
            resources.getString(R.string.wait_while_creating)
        )
        val url = ApiUrls.saveTextArticlesDetails
        textArticleDescriptionEditTExt = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            val s = Html.toHtml(
                textArticleDescriptionText.text,
                Html.FROM_HTML_SEPARATOR_LINE_BREAK_DIV
            )
            val newString = s.replace("&lt;", "<")
            val newString2 = newString.replace("&gt;", ">")
            """<!DOCTYPE html>
    <html>
      <body>${appendAnchorTags(newString2)} </body>
    """ + "</html>"
        } else {
            textArticleDescriptionText.text.toString()
        }
        textArticleContentEditTExt = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            val s =
                Html.toHtml(textArticleContentText.text, Html.FROM_HTML_SEPARATOR_LINE_BREAK_DIV)
            val newString = s.replace("&lt;", "<")
            val newString2 = newString.replace("&gt;", ">")
            """<!DOCTYPE html>
    <html>
      <body>${appendAnchorTags(newString2)} </body>
    """ + "</html>"
        } else {
            """<!DOCTYPE html>
    <html>
      <body>${textArticleContentText.text} </body>
    """ + "</html>"
        }
        try {
            jsonValue = JSONObject()
            jsonValue.put("header_img", uploadImageResponse)
            jsonValue.put("catchoice", category)
            jsonValue.put("title", textArticleTitleText.text.toString())
            jsonValue.put("desc", textArticleDescriptionEditTExt)
            jsonValue.put("content", textArticleContentEditTExt)
            jsonValue.put("sharewith", 2)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        commonViewModel.commonViewModelCall(url, jsonValue, Method.POST).observe(
            this@CreateTextArticleActivity
        ) { result ->
            try {
                val response = JSONObject(result)
                if (response.getInt("status_code") == 200) {
                    dialog.dismiss()
                    Toast.makeText(
                        applicationContext,
                        "Articles create successfully",
                        Toast.LENGTH_LONG
                    ).show()
                    finish()
                    val intent = Intent()
                    // Here you can also put data on intent
                    intent.action = "ARTICLES_CREATED"
                    sendBroadcast(intent)
                } else {
                    dialog.dismiss()
                    errorHandler(this@CreateTextArticleActivity, result)
                }
            } catch (e: Exception) {
                dialog.dismiss()
                e.printStackTrace()
            }
        }
    }

    companion object {
        private const val PERMISSION_CALLBACK_CONSTANT = 100
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
        val builder = android.app.AlertDialog.Builder(this@CreateTextArticleActivity)
        builder.setTitle(title)
        builder.setCancelable(false)
        val customLayout = LayoutInflater.from(this@CreateTextArticleActivity)
            .inflate(R.layout.custom_progress_bar, null)
        builder.setView(customLayout)
        dialog = builder.create()
        val tvBody = customLayout.findViewById<TextView>(R.id.tv_value)
        tvBody.text = progressVal
        dialog.show()
    }
}