package com.whitecoats.clinicplus.activities

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager.widget.ViewPager
import com.android.volley.*
import com.android.volley.toolbox.Volley
import com.google.android.gms.common.util.IOUtils
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import com.google.android.material.tabs.TabLayout.TabLayoutOnPageChangeListener
import com.whitecoats.clinicplus.MyClinicGlobalClass
import com.whitecoats.clinicplus.R
import com.whitecoats.clinicplus.adapters.AppointmentDetailsAdapter
import com.whitecoats.clinicplus.apis.ApiUrls
import com.whitecoats.clinicplus.fragments.CaptureNotesFragment
import com.whitecoats.clinicplus.models.CaptureNotesModel
import com.whitecoats.clinicplus.models.ItemPrescriptionView
import com.whitecoats.clinicplus.utils.ErrorHandlerClass.errorHandler
import com.whitecoats.clinicplus.viewmodels.AppointmentDetailsViewModel
import org.json.JSONObject
import java.io.*
import java.text.SimpleDateFormat
import java.util.*

class AppointmentDetailsActivity : AppCompatActivity() {
    private var adapter: AppointmentDetailsAdapter? = null
    private lateinit var title: TextView
    @JvmField
    var appointmentID = 0
    @JvmField
    var appointmentOrderId = 0
    @JvmField
    var appointmentMode = 0
    @JvmField
    var productId = 0
    @JvmField
    var patientId = 0
    @JvmField
    var appointmentPosition = 0
    @JvmField
    var appointmentDate: String? = null
    @JvmField
    var appointmentTime: String? = null
    @JvmField
    var patientName: String? = null
    @JvmField
    var scheduleTime: String? = null
    @JvmField
    var invoiceUrlCompletedId: String? = null
    @JvmField
    var refundAmount: String? = null
    @JvmField
    var receiptUrl: String? = null
    private lateinit var intentObj: Intent
    private lateinit var apptDetailsBack: ImageButton
    private var isPDFFile = false
    private var uploadImageResponse: String? = null
    private var pdfFile: File? = null
    private var fileUri: Uri? = null
    private var imageFilePath: String? = null
    private var attachedFileName: String? = null
    private var type: String? = null
    private val isInvestigationImageUpload = false
    private var scaled: Bitmap? = null
    private var appointmentDetailsViewModel: AppointmentDetailsViewModel? = null
    @JvmField
    var encounterId = 0
    @JvmField
    var episodeId = 0
    @JvmField
    var invoiceData: String? = null
    @JvmField
    var flagCreateInvoiceInComplete = false
    @JvmField
    var video_tool = 0
    @JvmField
    var doctor_join_url: String? = null
    @JvmField
    var appointActivePast = 0
    private lateinit var apptDetailstabLayout: TabLayout
    private lateinit var apptDetailsviewPager: ViewPager

    /*ENGG-3691 -- Refactoring the code by removing the deprecate functions(StartActivityForResults)*/
    private var launchOpenFileResults: ActivityResultLauncher<Intent>? = null
    private var launcherCameraResults: ActivityResultLauncher<Intent>? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_appointment_details)
        apptDetailstabLayout = findViewById(R.id.appt_details_tabLayout)
        apptDetailsviewPager = findViewById(R.id.appt_details_viewPager)
        apptDetailsBack = findViewById(R.id.appt_details_back)
        title = findViewById(R.id.appt_patient_name)
        //apptDetailstabLayout.addTab(apptDetailstabLayout.newTab().setText("Capture Notes"));
        apptDetailstabLayout.addTab(apptDetailstabLayout.newTab().setText("Appt. Details"))
        //apptDetailstabLayout.addTab(apptDetailstabLayout.newTab().setText("Records"));
        apptDetailstabLayout.tabGravity = TabLayout.GRAVITY_FILL
        apptDetailstabLayout.tabMode = TabLayout.MODE_FIXED
        adapter =
            AppointmentDetailsAdapter(supportFragmentManager, apptDetailstabLayout.tabCount)
        apptDetailsviewPager.adapter = adapter
        apptDetailsviewPager.addOnPageChangeListener(
            TabLayoutOnPageChangeListener(
                apptDetailstabLayout
            )
        )
        MyClinicGlobalClass.logUserActionEvent(
            ApiUrls.doctorId, getString(
                R.string.CaptureNotesScreenNotesCaptured
            ), null
        )
        appointmentDetailsViewModel = ViewModelProvider(this).get(
            AppointmentDetailsViewModel::class.java
        )
        appointmentDetailsViewModel!!.init()
        intentObj = intent
        appointmentID = intentObj.getIntExtra("apptID", 0)
        appointmentOrderId = intentObj.getIntExtra("orderId", 0)
        appointmentMode = intentObj.getIntExtra("apptMode", 0)
        appointmentDate = intentObj.getStringExtra("appointmentDate")
        appointmentTime = intentObj.getStringExtra("appointmentTime")
        patientId = intentObj.getIntExtra("patientId", 0)
        patientName = intentObj.getStringExtra("patientName")
        productId = intentObj.getIntExtra("procedureId", 0)
        scheduleTime = intentObj.getStringExtra("scheduletime")
        appointmentPosition = intentObj.getIntExtra("appointmentPosition", 0)
        invoiceUrlCompletedId = intentObj.getStringExtra("invoiceURLComplete")
        title.text = intentObj.getStringExtra("patientName")
        invoiceData = intentObj.getStringExtra("invoiceData")
        refundAmount = intentObj.getStringExtra("refundAmt")
        receiptUrl = intentObj.getStringExtra("receiptUrl")
        video_tool = intentObj.getIntExtra("Video_tool", 0)
        doctor_join_url = intentObj.getStringExtra("Doctor_Join_External_Url")
        if (intentObj.hasExtra("AppointActivePast")) {
            appointActivePast = intentObj.getIntExtra("AppointActivePast", 0)
        }
        flagCreateInvoiceInComplete = intentObj.getBooleanExtra("flagCreateInvoiceInComplete", false)

        /*ENGG-3691 -- Refactoring the code by removing the deprecate functions(StartActivityForResults)*/
        //Start
        launchOpenFileResults = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result: ActivityResult ->
            //Request code 2
            val data = result.data
            val resultCode = result.resultCode
            if (resultCode == RESULT_OK) {
                val uri = data!!.data
                val fileName = getFileName(uri)
                attachedFileName = fileName
                if (fileName!!.endsWith(".jpg") || fileName.endsWith(".jpeg") || fileName.endsWith(".png") || fileName.endsWith(
                        ".pdf"
                    )
                ) {
                    try {
                        if (fileName.contains("pdf")) {
                            isPDFFile = true
                            uploadPDFFile(fileName, uri)
                        } else {
                            val bitmapImage =
                                MediaStore.Images.Media.getBitmap(contentResolver, uri)
                            val nh = (bitmapImage.height * (720.0 / bitmapImage.width)).toInt()
                            scaled = Bitmap.createScaledBitmap(bitmapImage, 720, nh, true)
                            type = "image"
                            uploadImage("image")
                        }
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                } else {
                    Toast.makeText(
                        this@AppointmentDetailsActivity,
                        "Please Upload .jpg or .png files only",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
        launcherCameraResults = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result: ActivityResult ->
            //Request code 1
           // val data = result.data
            val resultCode = result.resultCode
            if (resultCode == RESULT_OK) {
                try {
                    val contentResolver = contentResolver
                    // Use the content resolver to open camera taken image input stream through image uri.
                    attachedFileName = getFileName(fileUri)
                    val inputStream = contentResolver.openInputStream(fileUri!!)

                    // Decode the image input stream to a bitmap use BitmapFactory.
                    val pictureBitmap = BitmapFactory.decodeStream(inputStream)
                    val nh = (pictureBitmap.height * (720.0 / pictureBitmap.width)).toInt()
                    scaled = Bitmap.createScaledBitmap(pictureBitmap, 720, nh, true)
                    type = "image"
                    uploadImage("image")
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
        //End
        apptDetailsBack.setOnClickListener { finish() }
        if (intentObj.getStringExtra("type").equals("payment", ignoreCase = true)) {
            apptDetailsviewPager.currentItem = 1
        } else {
            apptDetailsviewPager.currentItem = 0
        }
        apptDetailstabLayout.setOnTabSelectedListener(object : OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                //apptDetailsviewPager.setCurrentItem(tab.getPosition());
                apptDetailsviewPager.currentItem = 1
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })
    }

    fun openCamera() {
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
    }

    fun openFileDialog() {
        val intent = Intent()
        // Show only images, no videos or anything else
        intent.type = "*/*"
        val mimeTypes = arrayOf("image/*", "application/pdf")
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
        intent.action = Intent.ACTION_GET_CONTENT
        // Always show the chooser (if there are multiple options available)
        launchOpenFileResults!!.launch(Intent.createChooser(intent, "Select File"))
    }

    fun getFileDataFromDrawable(bitmap: Bitmap?): ByteArray {
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap!!.compress(Bitmap.CompressFormat.PNG, 20, byteArrayOutputStream)
        return byteArrayOutputStream.toByteArray()
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

    private fun uploadPDFFile(selectedFilePath: String?, uri: Uri?) {
        val resolver = applicationContext
            .contentResolver
        try {
            val pfd = resolver.openFileDescriptor(uri!!, "r")
            val stream: InputStream = FileInputStream(pfd!!.fileDescriptor)
            val localfile = File(this.cacheDir, selectedFilePath!!)
            val localStream: OutputStream = FileOutputStream(localfile)
            IOUtils.copyStream(stream, localStream)
            pdfFile = localfile
            type = "pdf"
            uploadImage("pdf")
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun uploadImage(type: String) {
        val url = ApiUrls.uploadRecordImage
        val loadingDialog = ProgressDialog(this)
        if (type.equals("image", ignoreCase = true)) {
            loadingDialog.setMessage(resources.getString(R.string.uploading_image))
        } else {
            loadingDialog.setMessage(resources.getString(R.string.uploading_pdf))
        }
        loadingDialog.setCancelable(false)
        loadingDialog.setInverseBackgroundForced(false)
        loadingDialog.show()

        //our custom volley request
        val volleyMultipartRequest: AppImageUploader = @SuppressLint("NotifyDataSetChanged")
        object : AppImageUploader(
            Method.POST, url,
            Response.Listener { response ->
                try {
                    var rootObj = JSONObject(String(response.data))
                    Log.d("Image Upload", rootObj.toString())
                    rootObj = rootObj.getJSONObject("response")
                    val url = rootObj.getString("url")
                    if (url != "") {
                        uploadImageResponse = url
                        Log.i(
                            "params details",
                            ApiUrls.doctorId.toString() + " " + encounterId + " " + episodeId + " " + patientId
                        )
                        appointmentDetailsViewModel!!.saveWrittenNotes(
                            this@AppointmentDetailsActivity,
                            ApiUrls.doctorId,
                            encounterId,
                            episodeId,
                            uploadImageResponse,
                            patientId
                        ).observe(this@AppointmentDetailsActivity
                        ) { s ->
                            try {
                                val responseVal = JSONObject(s)
                                if (responseVal.getInt("status_code") == 200) {
                                    val responseObj = responseVal.getJSONObject("response")
                                        .getJSONObject("response")
                                    if (responseObj.has("id")) {
                                        // update the handwritten notes in the capture notes.
                                        val iconNamePdf = "ic_pdf_file2"
                                        val iconNameImage = "ic_insert_photo"
                                        val iconNameAdd = "ic_add3"
                                        CaptureNotesFragment.prescriptionViewItemList.removeAt(
                                            CaptureNotesFragment.prescriptionViewItemList.size - 1
                                        )
                                        val cnm1 = CaptureNotesModel()
                                        cnm1.fileUrl = uploadImageResponse
                                        CaptureNotesFragment.handWrittenNoteFileArray.add(cnm1)
                                        if (uploadImageResponse!!.contains(".pdf")) {
                                            val cnm = ItemPrescriptionView(
                                                iconNamePdf,
                                                1,
                                                uploadImageResponse
                                            )
                                            CaptureNotesFragment.prescriptionViewItemList.add(
                                                cnm
                                            )
                                        } else {
                                            val cnm = ItemPrescriptionView(
                                                iconNameImage,
                                                1,
                                                uploadImageResponse
                                            )
                                            CaptureNotesFragment.prescriptionViewItemList.add(
                                                cnm
                                            )
                                        }
                                        val cnm = ItemPrescriptionView(iconNameAdd, 0, "")
                                        CaptureNotesFragment.prescriptionViewItemList.add(cnm)
                                        CaptureNotesFragment.prescriptionViewGridDataAdapter.notifyDataSetChanged()
                                    }
                                } else {
                                    errorHandler(this@AppointmentDetailsActivity, s)
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                        if (type.equals("image", ignoreCase = true)) {
                            Toast.makeText(
                                applicationContext,
                                resources.getString(R.string.upload_img_success),
                                Toast.LENGTH_SHORT
                            ).show()
                        } else if (type.equals("pdf", ignoreCase = true)) {
                            Toast.makeText(
                                applicationContext,
                                resources.getString(R.string.upload_pdf_success),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                    isPDFFile = false
                    loadingDialog.dismiss()
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
                loadingDialog.dismiss()
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
                //                params.put("path", "records/" + episodeId + "/");//old
                if (isInvestigationImageUpload) {
                    params["path"] =
                        "records_v2/investigation_files/" + ApiUrls.doctorId + "/" + "android" + "/" //new
                } else {
                    params["path"] =
                        "records_v2/images/" + ApiUrls.doctorId + "/" + "android" + "/" //new
                }
                return params
            }

            /*
             * Here we are passing image by renaming it with a unique name
             * */
            override fun getByteData(): Map<String, DataPart> {
                val params: MutableMap<String, DataPart> = HashMap()
                //                long imagename = System.currentTimeMillis();
                if (isPDFFile) {
                    try {
                        val bytesArray = ByteArray(pdfFile!!.length().toInt())
                        val fis = FileInputStream(pdfFile)
                        val a = fis.read(bytesArray) //read file into bytes[]
                        fis.close()
                        params["file"] = DataPart("Record" + ".pdf", bytesArray)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                } else {
                    val bitmap = scaled
                    params["file"] = DataPart("Record" + ".png", getFileDataFromDrawable(bitmap))
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
        volleyMultipartRequest.retryPolicy = DefaultRetryPolicy(
            120000,
            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        )
        Volley.newRequestQueue(this).add(volleyMultipartRequest)
    }

    @Throws(IOException::class)
    private fun createImageFile(): File {
        val timeStamp = SimpleDateFormat(
            "yyyyMMdd_HHmmss",
            Locale.getDefault()
        ).format(Date())
        val imageFileName = "IMG_" + timeStamp + "_"
        val storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val image = File.createTempFile(
            imageFileName,  /* prefix */
            ".png",  /* suffix */
            storageDir /* directory */
        )
        imageFilePath = image.absolutePath
        return image
    }
}