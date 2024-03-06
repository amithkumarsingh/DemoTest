package com.whitecoats.clinicplus

import android.Manifest
import android.annotation.SuppressLint
import android.content.ContextWrapper
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.net.ConnectivityManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import com.android.volley.Request.Method
import com.android.volley.toolbox.ImageRequest
import com.android.volley.toolbox.Volley
import com.whitecoats.clinicplus.apis.ApiUrls
import com.whitecoats.clinicplus.onboarding.OnBoardingActivity
import com.whitecoats.clinicplus.viewmodels.CommonViewModel
import org.json.JSONObject
import java.io.*


@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {
    private var appPreference: SharedPreferences? = null
    private var appDatabaseManager: AppDatabaseManager? = null
    private var apiCall: PatientRecordsApi? = null
    private var splashOrgLogo: ImageView? = null
    private val permissionsRequired = arrayOf(
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.CAMERA
    )

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    private val permissionsRequiredSDK33Higher = arrayOf(
        Manifest.permission.READ_MEDIA_IMAGES,
        Manifest.permission.READ_MEDIA_VIDEO,
        Manifest.permission.CAMERA
    )
    private val logoUrl: String? = null
    private val PERMISSION_CALLBACK_CONSTANT = 100
    private val REQUEST_PERMISSION_SETTING = 101
    private lateinit var commonViewModel: CommonViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        hideSystemUI()
        splashOrgLogo = findViewById(R.id.splashScreenOrgLogo)
        appPreference = applicationContext.getSharedPreferences(ApiUrls.appSharedPref, MODE_PRIVATE)
        appDatabaseManager = AppDatabaseManager(this)
        commonViewModel = ViewModelProvider(this@SplashActivity)[CommonViewModel::class.java]
        apiCall = PatientRecordsApi()
        val appUserManagers = appDatabaseManager!!.userData
        if (appUserManagers.size > 0) {
            ApiUrls.loginToken = appUserManagers[0].token
            ApiUrls.doctorId = appUserManagers[0].userId
            ApiUrls.isDoctorOnly = appUserManagers[0].isDoctorOnly
        }
        if (isOnline) {
            orgLogo
        } else {
            getImageData("", "app-logo.png")
        }
        val data = HashMap<String, Any>()
        data["SplashScreenImpressions"] = "EnterSplashScreen"
    }

    private val orgLogo: Unit
        @SuppressLint("UseCompatLoadingForDrawables")
        get() {

            commonViewModel.commonViewModelCall(
                ApiUrls.getOrganisationLogo,
                JSONObject(),
                Method.GET
            ).observe(
                this@SplashActivity
            ) { result ->
                try {
                    val responseObj = JSONObject(result)
                    if (responseObj.getInt("status_code") == 200) {
                        var resObj = responseObj.optJSONObject("response")
                        if (resObj!!["response"] is JSONObject) {
                            resObj = resObj.getJSONObject("response")
                            val editor = appPreference!!.edit()
                            val logoUrlCache = appPreference!!.getString("LogoPath", "")
                            if (!logoUrlCache.equals(
                                    resObj!!.getString("logo_url"),
                                    ignoreCase = true
                                )
                            ) {
                                editor.putString("LogoPath", resObj.getString("logo_url"))
                                //                                loadDocDetail("app-logo.png", resObj.getString("logo_url"));
                                getImageData(resObj.getString("logo_url"), "app-logo.png")
                            } else if (logoUrlCache !== "") {
                                if (appPreference!!.getBoolean("ImageSave", false)) {
                                    getCache("app-logo.png", logoUrlCache)
                                } else {
                                    getImageData(logoUrlCache, "app-logo.png")
                                }
                            } else if (!resObj.getString("logo_url")
                                    .equals("default", ignoreCase = true)
                            ) {
                                getImageData("", "app-logo.png")
                            } else {
                                editor.putString("LogoPath", "")
                            }
                            editor.apply()
                        } else {
                            splashOrgLogo!!.setImageDrawable(getDrawable(R.mipmap.ic_launcher_round))
                            goToHomePage()
                        }
                    } else {
                        splashOrgLogo!!.setImageDrawable(getDrawable(R.mipmap.ic_launcher_round))
                        goToHomePage()
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }

    private fun getImageData(imgURL: String?, imgName: String?) {
        val queue = Volley.newRequestQueue(this)

        // Retrieves an image specified by the URL, displays it clinicplus the UI.
        val request = ImageRequest(
            imgURL,
            { bitmap ->
                splashOrgLogo!!.setImageBitmap(bitmap)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    if (ActivityCompat.checkSelfPermission(
                            this@SplashActivity,
                            permissionsRequiredSDK33Higher[0]
                        ) == PackageManager.PERMISSION_GRANTED
                    ) {
                        saveToInternalStorage(bitmap)
                        goToHomePage()
                    } else {
                        ActivityCompat.requestPermissions(
                            this@SplashActivity,
                            permissionsRequiredSDK33Higher,
                            PERMISSION_CALLBACK_CONSTANT
                        )
                    }
                } else {
                    if (ActivityCompat.checkSelfPermission(
                            this@SplashActivity,
                            permissionsRequired[0]
                        ) == PackageManager.PERMISSION_GRANTED
                    ) {
                        saveToInternalStorage(bitmap)
                        goToHomePage()
                    } else {
                        ActivityCompat.requestPermissions(
                            this@SplashActivity,
                            permissionsRequired,
                            PERMISSION_CALLBACK_CONSTANT
                        )
                    }
                }
            }, 0, 0, null
        ) {
            try {
                val cw = ContextWrapper(applicationContext)
                // path to /data/data/yourapp/app_data/imageDir
                val directory = cw.getDir("images", MODE_PRIVATE)
                val f = File(directory, imgName!!)
                val b = BitmapFactory.decodeStream(FileInputStream(f))
                splashOrgLogo!!.setImageBitmap(b)
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
                logoUrl?.let { getImageData(imgName, it) }
            }
            goToHomePage()
        }
        // Access the RequestQueue through your singleton class.
        queue.add(request)
    }

    private fun goToHomePage() {
        Handler().postDelayed(
            {
                val loginSession = ApiUrls.loginToken
                if (loginSession == null || (loginSession == "")) {
                    if (!appPreference!!.getBoolean("OnBoardingDone", false)) {
                        val i = Intent(this@SplashActivity, OnBoardingActivity::class.java)
                        startActivity(i)
                        finish()
                    } else {
                        val i = Intent(this@SplashActivity, LoginActivity::class.java)
                        startActivity(i)
                        finish()
                    }
                } else {
                    if (!appPreference!!.getBoolean("OnBoardingDone", false)) {
                        val i = Intent(this@SplashActivity, OnBoardingActivity::class.java)
                        startActivity(i)
                        finish()
                    } else {
                        val i = Intent(this@SplashActivity, MainActivity::class.java)
                        startActivity(i)
                        finish()
                    }
                }
            }, 2000
        )
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
                saveToInternalStorage((splashOrgLogo!!.drawable as BitmapDrawable).bitmap)
                goToHomePage()
            } else {
                goToHomePage()
            }
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_PERMISSION_SETTING) {
            if (ActivityCompat.checkSelfPermission(
                    this@SplashActivity,
                    permissionsRequired[0]
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                //Got Permission
                Toast.makeText(
                    this@SplashActivity,
                    "You Have All The Permission",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun getCache(imgName: String, logoUrl: String?) {
        try {
            val cw = ContextWrapper(applicationContext)
            // path to /data/data/yourapp/app_data/imageDir
            val directory = cw.getDir("images", MODE_PRIVATE)
            val f = File(directory, imgName)
            //            Log.d("Image File NAme", imgName);
            val b = BitmapFactory.decodeStream(FileInputStream(f))
            splashOrgLogo!!.setImageBitmap(b)
            goToHomePage()
        } catch (e: Exception) {
            e.printStackTrace()
            getImageData(logoUrl, imgName)
        }
    }

    private val isOnline: Boolean
        get() {
            val cm = getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
            val netInfo = cm.activeNetworkInfo
            return netInfo != null && netInfo.isConnectedOrConnecting
        }

    private fun saveToInternalStorage(bitmapImage: Bitmap): String {
        val cw = ContextWrapper(applicationContext)
        // path to /data/data/yourapp/app_data/imageDir
        val directory = cw.getDir("images", MODE_PRIVATE)
        // Create imageDir
        //File mypath = new File(directory, splashText.getText().toString() + ".png");
        val mypath = File(directory, "app-logo" + ".png")
        val editor = appPreference!!.edit()
        var fos: FileOutputStream? = null
        try {
            fos = FileOutputStream(mypath)
            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 85, fos)
            editor.putBoolean("ImageSave", true)
            editor.apply()
        } catch (e: Exception) {
            e.printStackTrace()
            editor.putBoolean("ImageSave", false)
            editor.apply()
        } finally {
            try {
                fos!!.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        return directory.absolutePath
    }

    private fun hideSystemUI() {
        // Enables regular immersive mode.
        // For "lean back" mode, remove SYSTEM_UI_FLAG_IMMERSIVE.
        // Or for "sticky immersive," replace it with SYSTEM_UI_FLAG_IMMERSIVE_STICKY
       /* val decorView = window.decorView
        decorView.systemUiVisibility =
            (View.SYSTEM_UI_FLAG_IMMERSIVE // Set the content to appear under the system bars so that the
                    // content doesn't resize when the system bars hide and show.
                    or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN // Hide the nav bar and status bar
                    or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_FULLSCREEN)*/
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.setDecorFitsSystemWindows(false)
            if (window.insetsController != null) {
                window.insetsController!!.hide(WindowInsets.Type.statusBars() or WindowInsets.Type.navigationBars())
                window.insetsController!!.systemBarsBehavior =
                    WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            }
        } else {
            window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    or View.SYSTEM_UI_FLAG_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)
        }
    }
}