package com.whitecoats.clinicplus

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.*
import android.content.IntentSender.SendIntentException
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.net.ConnectivityManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager.widget.ViewPager
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import com.android.volley.Request
import com.android.volley.Request.Method
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.InstallState
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability
import com.onesignal.OSNotificationReceivedEvent
import com.onesignal.OneSignal
import com.whitecoats.adapter.AssistantTabAdapter
import com.whitecoats.clinicplus.R.drawable
import com.whitecoats.clinicplus.activities.FeedActivity
import com.whitecoats.clinicplus.apis.ApiGetPostMethodCalls
import com.whitecoats.clinicplus.apis.ApiUrls
import com.whitecoats.clinicplus.constants.AppConstants
import com.whitecoats.clinicplus.fragments.ApptDetailsFragment
import com.whitecoats.clinicplus.fragments.DashboardFragment
import com.whitecoats.clinicplus.fragments.NewAppointmentFragment
import com.whitecoats.clinicplus.utils.AppUtilities
import com.whitecoats.clinicplus.utils.ErrorHandlerClass.errorHandler
import com.whitecoats.clinicplus.utils.SharedPref
import com.whitecoats.clinicplus.viewmodels.CommonViewModel
import com.whitecoats.clinicplus.viewmodels.SharedViewModel
import com.whitecoats.clinicplus.views.SwipeDisabledViewPager
import com.whitecoats.fragments.AppointmentFragment.Companion.appointmentTabFlag
import com.whitecoats.fragments.ChatFragment
import com.whitecoats.fragments.PatientFragment
import com.whitecoats.fragments.PatientFragment.Companion.patientTabFlag
import com.zoho.salesiqembed.ZohoSalesIQ
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class MainActivity : AppCompatActivity() {
    private var appPreference: SharedPreferences? = null
    var mContext: Context? = null
    private var menuArr: JSONArray? = null
    private lateinit var navigationMenuNoDataText: RelativeLayout
    var globalClass: MyClinicGlobalClass? = null
    private var pInfo: PackageInfo? = null
    private var AppverCode = 0
    private var app_name: String? = null
    var jsonValue: JSONObject? = null
    private var session_id: String? = null
    private var sharedPref: SharedPref? = null
    private var newAppointmentFragment: NewAppointmentFragment? = null
    var prevMenuItem: MenuItem? = null
    private var patientFragment: PatientFragment? = null
    private var dashboardFragment: DashboardFragment? = null
    private var mAppUpdateManager: AppUpdateManager? = null
    private var selectedMenuPos = 0
    private var isPermissionPageOpen = false
    private var apiGetPostMethodCalls: ApiGetPostMethodCalls? = null
    private var appUtilities: AppUtilities? = null
    private var notiPlayerId: String? = null
    private var notiPushNoti: String? = null
    private lateinit var dialog: Dialog
    private lateinit var commonViewModel: CommonViewModel

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    var permissionSDK33HigherNotifi = arrayOf(Manifest.permission.POST_NOTIFICATIONS)
    private val PERMISSION_CALLBACK_CONSTANT = 100
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        showNotifPermissionPrompt()
        OneSignal.setNotificationWillShowInForegroundHandler { osNotificationReceivedEvent: OSNotificationReceivedEvent ->
            val data = osNotificationReceivedEvent.notification.additionalData
            osNotificationReceivedEvent.complete(osNotificationReceivedEvent.notification)
        }
        OneSignal.unsubscribeWhenNotificationsAreDisabled(true)
        OneSignal.setNotificationOpenedHandler(OSNotificationOpenedHandler(this@MainActivity))

        //Initializing viewPager
        viewPager = findViewById(R.id.viewpager)
        viewPager.offscreenPageLimit = 3
        bottomNavigationView = findViewById(R.id.bottomNavigation)
        navigationMenuNoDataText = findViewById(R.id.navigationMenuNoDataText)
        globalClass = applicationContext as MyClinicGlobalClass
        apiGetPostMethodCalls = ApiGetPostMethodCalls()
        commonViewModel = ViewModelProvider(this)[CommonViewModel::class.java]
        mContext = applicationContext
        appUtilities = AppUtilities()
        if (sharedPref == null) {
            sharedPref = SharedPref(applicationContext)
        }
        SharedPref.getPrefsHelper().delete("FilterPaymentOverView")
        appPreference = applicationContext.getSharedPreferences(ApiUrls.appSharedPref, MODE_PRIVATE)
        val sharedViewModel = ViewModelProvider(this)[SharedViewModel::class.java]
        dashBoardDetails
        dashboardFragment = DashboardFragment.newInstance()
        appDatabaseManager = AppDatabaseManager(this)
        bottomNavigationView!!.setOnNavigationItemSelectedListener { item: MenuItem ->
            if (globalClass!!.isOnline) {
                Log.d("Selected Item", item.itemId.toString() + "")
                if (item.itemId == 0) {
                    viewPager.currentItem = 0
                    ApiUrls.homeGuideTab = 0
                }
                if (item.itemId == 1) {
                    try {
                        for (i in 0 until menuArr!!.length()) {
                            val tempobj = menuArr!!.getJSONObject(i)
                            if (tempobj.getInt("id") == 1 && tempobj.getInt("is_hidden_for_doctor_only") == 1 && ApiUrls.isDoctorOnly == 1) {
                                Toast.makeText(
                                    applicationContext,
                                    "Service not available",
                                    Toast.LENGTH_LONG
                                ).show()
                                homeFragmentOpen()
                                break
                            } else {
                                if (tempobj.getInt("id") == 1) {
                                    newAppointmentFragment =
                                        NewAppointmentFragment.newInstance()
                                    viewPager.currentItem = 2
                                }
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
                if (item.itemId == 18) {
                    try {
                        for (i in 0 until menuArr!!.length()) {
                            val tempobj = menuArr!!.getJSONObject(i)
                            if (tempobj.getInt("id") == 18 && tempobj.getInt("is_hidden_for_doctor_only") == 1 && ApiUrls.isDoctorOnly == 1) {
                                Toast.makeText(
                                    applicationContext,
                                    "Service not available",
                                    Toast.LENGTH_LONG
                                ).show()
                                homeFragmentOpen()
                                break
                            } else {
                                if (tempobj.getInt("id") == 18) {
                                    viewPager.currentItem = 1
                                }
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
                if (item.itemId == 6) {
                    try {
                        for (i in 0 until menuArr!!.length()) {
                            val tempobj = menuArr!!.getJSONObject(i)
                            if (tempobj.getInt("id") == 6 && tempobj.getInt("is_hidden_for_doctor_only") == 1 && ApiUrls.isDoctorOnly == 1) {
                                Toast.makeText(
                                    applicationContext,
                                    "Service not available",
                                    Toast.LENGTH_LONG
                                ).show()
                                break
                            } else {
                                if (tempobj.getInt("id") == 6) {
                                    viewPager.currentItem = 3
                                }
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
                if (item.itemId == 7) { //communication
                    try {
                        for (i in 0 until menuArr!!.length()) {
                            val tempobj = menuArr!!.getJSONObject(i)
                            if (tempobj.getInt("id") == 7 && tempobj.getInt("is_hidden_for_doctor_only") == 1 && ApiUrls.isDoctorOnly == 1) {
                                Toast.makeText(
                                    applicationContext,
                                    "Service not available",
                                    Toast.LENGTH_LONG
                                ).show()
                                homeFragmentOpen()
                                break
                            } else {
                                if (tempobj.getInt("id") == 7) {
                                    val intent = Intent(
                                        this@MainActivity,
                                        CommunicationActivity::class.java
                                    )
                                    startActivity(intent)
                                }
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
                if (item.itemId == 10) { //feed
                    val intent = Intent(mContext, FeedActivity::class.java)
                    startActivity(intent)
                }
                if (item.itemId == 22) { //setting
                    val intent = Intent(this@MainActivity, SettingsActivity::class.java)
                    startActivity(intent)
                }
                if (item.itemId == 11) { //get help
                    DashboardFragment.viewPager!!.currentItem = 2
                }
                if (item.itemId == 5) {
                    val bottomSheetDialogFragment = DashBoardMoreBottomSheet()
                    bottomSheetDialogFragment.setupConfig(this@MainActivity)
                    bottomSheetDialogFragment.show(
                        supportFragmentManager,
                        "Bottom Sheet Dialog Fragment"
                    )
                }
                if (item.itemId == -1) {
                    logout()
                }
            } else {
                globalClass!!.noInternetConnection.showDialog(this@MainActivity)
            }
            true
        }
        checkConnection()
        mAppUpdateManager = AppUpdateManagerFactory.create(this)
        mAppUpdateManager!!.registerListener(installStateUpdatedListener)
        mAppUpdateManager!!.appUpdateInfo.addOnSuccessListener { appUpdateInfo: AppUpdateInfo ->
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE /*AppUpdateType.IMMEDIATE*/)
            ) {
                try {
                    mAppUpdateManager!!.startUpdateFlowForResult(
                        appUpdateInfo,
                        AppUpdateType.FLEXIBLE /*AppUpdateType.IMMEDIATE*/,
                        this@MainActivity,
                        RC_APP_UPDATE
                    )
                } catch (e: SendIntentException) {
                    e.printStackTrace()
                }
            } else if (appUpdateInfo.installStatus() == InstallStatus.DOWNLOADED) {
                //CHECK THIS if AppUpdateType.FLEXIBLE, otherwise you can skip
                popupSnackbarForCompleteUpdate()
            } else {
                Log.e("MainActivity", "checkForAppUpdateAvailability: something else")
            }
        }
        val packageManager = this.packageManager
        var packageInfo: PackageInfo? = null
        try {
            packageInfo = packageManager.getPackageInfo(packageName, 0)
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        currentVersion = packageInfo!!.versionName
        app_name = packageInfo.applicationInfo.loadLabel(getPackageManager()).toString()
        try {
            pInfo = getPackageManager().getPackageInfo(packageName, 0)
            AppverCode = pInfo!!.versionCode
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        sharedViewModel.message.observe(this) {
            bottomNavigationView!!.visibility = View.GONE
            navigationMenuNoDataText.visibility = View.VISIBLE
            val alertReLogin = ViewDialogReLogin()
            alertReLogin.showDialog(this@MainActivity)
        }
        viewPager.addOnPageChangeListener(object : OnPageChangeListener {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
            }

            override fun onPageSelected(position: Int) {
                try {
                    selectedMenuPos = position
                    if (prevMenuItem != null) {
                        prevMenuItem!!.isChecked = false
                    } else {
                        bottomNavigationView!!.menu.getItem(0).isChecked = false
                    }
                    Log.d("page", "" + position)
                    bottomNavigationView!!.menu.getItem(position).isChecked = true
                    prevMenuItem = bottomNavigationView!!.menu.getItem(position)
                } catch (e: Exception) {
                    e.printStackTrace()
                    isPermissionPageOpen = true
                }
            }

            override fun onPageScrollStateChanged(state: Int) {}
        })
        setupViewPager(viewPager)
        OneSignal.clearOneSignalNotifications()
        try {
            if (FeedActivity.isFeedDetailsClick == 1) {
                Log.i("FeedClickPos", "0")
            } else if (AssistantTabAdapter.assistantPatientProfileRecordFlag == 1) {
                AssistantTabAdapter.assistantPatientProfileRecordFlag = 0
            } else {
                if (ApiUrls.bottomNaviType == 1) {
                    appUtilities!!.selectBottomNavigationScreen("Patients")
                    bottomNavigationView!!.selectedItemId = bottomNavigationView!!.selectedItemId
                } else if (ApiUrls.bottomNaviType == 2) {
                    if (ApptDetailsFragment.backToDashBoardClicked) {
                        ApptDetailsFragment.backToDashBoardClicked = false
                        appUtilities!!.selectBottomNavigationScreen("Home")
                        bottomNavigationView!!.selectedItemId =
                            bottomNavigationView!!.selectedItemId
                    } else {
                        appUtilities!!.selectBottomNavigationScreen("Appointments")
                        bottomNavigationView!!.selectedItemId =
                            bottomNavigationView!!.selectedItemId
                        newAppointmentFragment!!.getAppointmentDetails(
                            "All",
                            "All",
                            "",
                            "asc",
                            "scheduled_start_time",
                            1.toString() + "",
                            "50",
                            "active",
                            "0",
                            "",
                            "",
                            "All",
                            "main",
                            ""
                        )
                    }
                } else if (ApiUrls.bottomNaviType == 3) {
                    appUtilities!!.selectBottomNavigationScreen("Chats")
                    ChatFragment.newInstance()
                    bottomNavigationView!!.selectedItemId = bottomNavigationView!!.selectedItemId
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private var installStateUpdatedListener: InstallStateUpdatedListener =
        object : InstallStateUpdatedListener {
            override fun onStateUpdate(state: InstallState) {
                if (state.installStatus() == InstallStatus.DOWNLOADED) {
                    //CHECK THIS if AppUpdateType.FLEXIBLE, otherwise you can skip
                    popupSnackbarForCompleteUpdate()
                } else if (state.installStatus() == InstallStatus.INSTALLED) {
                    if (mAppUpdateManager != null) {
                        mAppUpdateManager!!.unregisterListener(this)
                    }
                } else {
                    Log.i(
                        "MainActivity",
                        "InstallStateUpdatedListener: state: " + state.installStatus()
                    )
                }
            }
        }

    private fun showNotifPermissionPrompt() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(
                    this@MainActivity,
                    permissionSDK33HigherNotifi[0]
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this@MainActivity,
                    permissionSDK33HigherNotifi,
                    PERMISSION_CALLBACK_CONSTANT
                )
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
            for (grantResult in grantResults) {
                if (grantResult == PackageManager.PERMISSION_GRANTED) {
                    allgranted = true
                } else {
                    allgranted = false
                    break
                }
            }
            if (allgranted) {
                OneSignal.unsubscribeWhenNotificationsAreDisabled(true)
                OneSignal.setNotificationOpenedHandler(OSNotificationOpenedHandler(this@MainActivity))
                val device = OneSignal.getDeviceState()
                if (device != null) {
                    notiPlayerId = device.userId
                    notiPushNoti = device.pushToken
                    val editor = appPreference!!.edit()
                    editor.putString(ApiUrls.playerId, notiPlayerId)
                    editor.apply()
                    if (ApiUrls.doctorId != 0) {
                        sendPlayerId(ApiUrls.doctorId)
                    } else {
                        sendPlayerId(0)
                    }
                }
            }
        }
    }

    private fun popupSnackbarForCompleteUpdate() {
        val snackbar = Snackbar.make(
            findViewById(android.R.id.content),
            "New update is ready!",
            Snackbar.LENGTH_INDEFINITE
        )
        snackbar.setAction("Install") {
            if (mAppUpdateManager != null) {
                mAppUpdateManager!!.completeUpdate()
            }
        }
        snackbar.setActionTextColor(ContextCompat.getColor(this@MainActivity, R.color.colorPrimary))
        snackbar.show()
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_APP_UPDATE) {
            if (resultCode != RESULT_OK) {
                Log.e("MainActivity", "onActivityResult: app download failed")
            }
        }
    }

    /*
    private class GetVersionCode extends AsyncTask<Void, String, String> {

        @Override

        protected String doInBackground(Void... voids) {

            String newVersion = null;
            try {
                newVersion = Jsoup.connect("https://play.google.com/store/apps/details?id=" + MainActivity.this.getPackageName() + "&hl=it")
                        .timeout(30000)
                        .userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6")
                        .referrer("http://www.google.com")
                        .get()
                        .select(".hAyfc .htlgb")
                        .get(7)
                        .ownText();
                return newVersion;
            } catch (Exception e) {
                return newVersion;
            }

        }


        @Override

        protected void onPostExecute(String onlineVersion) {

            super.onPostExecute(onlineVersion);
            Log.d("update", "playstore version " + onlineVersion);
            payStoreVersion = onlineVersion;

            //Date date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
            Date c = Calendar.getInstance().getTime();
            SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault());
            currentAppDate = System.currentTimeMillis();
            CurrentDate = UpdateManagerConstant.convertLongtoDates(currentAppDate);
            //appUpdateManager logic
//          mUpdateManager = UpdateManager.Builder(MainActivity.this);
            Long sharedignoredate = appPreference.getLong("IgnoreCancelDate", 0L);
            Date fromDate = new Date(sharedignoredate);
            Date toDate = new Date(currentAppDate);
            int difference =
                    ((int) ((toDate.getTime() / (24 * 60 * 60 * 1000))
                            - (int) (fromDate.getTime() / (24 * 60 * 60 * 1000))));
            if (!appPreference.getString("PlaystoreStoreVersionName", "").equalsIgnoreCase(payStoreVersion)) {
                try {
                    if (currentVersion.equalsIgnoreCase(payStoreVersion)) {

                    } else {
                        showUpdateDialog();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (difference >= 10) {
                try {
                    showUpdateDialog();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }
    }
*/
/*
    private fun showUpdateDialog() {
        try {

            // inflate alert dialog xml
            val li = LayoutInflater.from(this@MainActivity)
            val dialogView = li.inflate(R.layout.app_update_dialog, null)
            val alertDialogBuilder = AlertDialog.Builder(this@MainActivity)
            alertDialogBuilder.setView(dialogView)
            // set dialog message
            alertDialogBuilder
                .setCancelable(false)
            // create alert dialog
            val updateDialog = alertDialogBuilder.create()
            val updateLater = dialogView.findViewById<LinearLayout>(R.id.updateLater)
            val updateNow = dialogView.findViewById<LinearLayout>(R.id.updateNow)
            val laterIcon = dialogView.findViewById<ImageView>(R.id.updateLaterIcon)
            val nowIcon = dialogView.findViewById<ImageView>(R.id.updateNowIcon)
            val laterText = dialogView.findViewById<TextView>(R.id.updateLaterText)
            val headerText = dialogView.findViewById<TextView>(R.id.updateHeaderText)
            laterIcon.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN)
            nowIcon.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN)
            laterText.text = "Ignore"
            laterIcon.setImageResource(drawable.ic_close)
            laterIcon.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN)
            headerText.text =
                "New version $app_name $payStoreVersion is available on Play store."
            updateLater.setOnClickListener {
                updateDialog.dismiss()
                if (laterText.text.toString().equals("Ignore", ignoreCase = true)) {
                    millis = System.currentTimeMillis()
                    updateDialog.dismiss()
                    val editor = appPreference!!.edit()
                    editor.putString("PlaystoreStoreVersionName", payStoreVersion)
                    editor.putLong("IgnoreCancelDate", millis!!)
                    editor.apply()
                }
            }
            updateNow.setOnClickListener {
                try {
                    val i = Intent("android.intent.action.MAIN")
                    i.component =
                        ComponentName.unflattenFromString("com.android.chrome/com.android.chrome.Main")
                    i.addCategory("android.intent.category.LAUNCHER")
                    i.data =
                        Uri.parse("http://play.google.com/store/apps/details?id=" + this@MainActivity.packageName)
                    startActivity(i)
                } catch (e: ActivityNotFoundException) {
                    // Chrome is not installed
                    val i = Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("http://play.google.com/store/apps/details?id=" + this@MainActivity.packageName)
                    )
                    startActivity(i)
                }
            }
            // show it
            updateDialog.show()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
*/


    /*Below condition is for handle to open permission page from other tab's *///Process os success response

    //                    menuArr = rootObj.getJSONArray("menu");
    //        final String URL = ApiUrls.getDashBoardDetails + "?mode=" + 0;
    private val dashBoardDetails: Unit
        get() {

            showCustomProgressAlertDialog(
                resources.getString(R.string.fetching),
                resources.getString(R.string.wait_while_we_fetching)
            )
            val url = ApiUrls.getDashBoardDetailsSubscriptionPlan

            commonViewModel.commonViewModelCall(url, JSONObject(), Method.GET).observe(
                this@MainActivity
            ) { result ->
                try {
                    //Process os success response
                    val responseObj = JSONObject(result)
                    if (responseObj.getInt("status_code") == 200) {
                        val response = responseObj.optJSONObject("response")
                        Log.d("DashBoardResponse", response!!.toString())
                        getDashBoardJsonResponse = response.toString()
                        getMenuJsonObject = response
                        dialog.dismiss()
                        val rootObj = response.optJSONObject("response")
                        menuArr = rootObj!!.getJSONArray("menus")
                        val actionArray = rootObj.getJSONArray("actions")
                        val actionArrayLength = actionArray.length()
                        val menuArrLength = menuArr!!.length()
                        if (menuArrLength >= 4) {
                            bottomNavigationView!!.menu.add(Menu.NONE, 0, Menu.NONE, "Home")
                                .setIcon(
                                    drawable.ic_home
                                )
                            for (i in 0..2) {
                                val tempobj = menuArr!!.getJSONObject(i)
                                val icon: Int = getId(
                                    tempobj.getString("android_icon"),
                                    drawable::class.java
                                )
                                var mDrawable: Drawable?
                                if (tempobj.getInt("is_hidden_for_doctor_only") == 1 && ApiUrls.isDoctorOnly == 1) {
                                    mDrawable = ContextCompat.getDrawable(
                                        mContext!!, icon
                                    )
                                    mDrawable!!.colorFilter = PorterDuffColorFilter(
                                        ContextCompat.getColor(
                                            this@MainActivity,
                                            R.color.colorGrey1
                                        ),
                                        PorterDuff.Mode.SRC_IN
                                    )
                                } else {
                                    mDrawable = ContextCompat.getDrawable(
                                        mContext!!, icon
                                    )
                                    mDrawable!!.colorFilter = PorterDuffColorFilter(
                                        ContextCompat.getColor(
                                            this@MainActivity,
                                            R.color.colorBlack
                                        ),
                                        PorterDuff.Mode.SRC_IN
                                    )
                                }
                                bottomNavigationView!!.menu.add(
                                    Menu.NONE,
                                    tempobj.getInt("id"),
                                    Menu.NONE,
                                    tempobj.getString("page_name")
                                ).icon = mDrawable
                            }
                            bottomNavigationView!!.menu.add(Menu.NONE, 5, Menu.NONE, "More")
                                .setIcon(
                                    drawable.ic_grid
                                )
                        } else if (menuArrLength <= 3) {
                            bottomNavigationView!!.menu.add(Menu.NONE, 0, Menu.NONE, "Home")
                                .setIcon(
                                    drawable.ic_home
                                )
                            for (i in 0 until menuArrLength) {
                                val tempobj = menuArr!!.getJSONObject(i)
                                val icon: Int = getId(
                                    tempobj.getString("android_icon"),
                                    drawable::class.java
                                )
                                bottomNavigationView!!.menu.add(
                                    Menu.NONE,
                                    tempobj.getInt("id"),
                                    Menu.NONE,
                                    tempobj.getString("page_name")
                                ).setIcon(icon)
                            }
                            bottomNavigationView!!.menu.add(Menu.NONE, -1, Menu.NONE, "Logout")
                                .setIcon(
                                    drawable.ic_logout
                                )

                            /*Below condition is for handle to open permission page from other tab's */if (Build.VERSION.SDK_INT >= 31) {
                                if (isPermissionPageOpen) {
                                    isPermissionPageOpen = false
                                    if (prevMenuItem != null) {
                                        prevMenuItem!!.isChecked = false
                                    } else {
                                        bottomNavigationView!!.menu.getItem(selectedMenuPos).isChecked =
                                            false
                                    }
                                    bottomNavigationView!!.menu.getItem(selectedMenuPos).isChecked =
                                        true
                                    prevMenuItem =
                                        bottomNavigationView!!.menu.getItem(selectedMenuPos)
                                }
                            }
                        }
                        if (sharedPref!!.isPrefExists("EMR")) {
                            sharedPref!!.delete("EMR")
                        }
                        if (sharedPref!!.isPrefExists("Video")) {
                            sharedPref!!.delete("Video")
                        }
                        if (sharedPref!!.isPrefExists("Chat")) {
                            sharedPref!!.delete("Chat")
                        }
                        if (sharedPref!!.isPrefExists("Instant Video")) {
                            sharedPref!!.delete("Instant Video")
                        }
                        if (actionArrayLength > 0) {
                            for (i in 0 until actionArrayLength) {
                                val tempObj = actionArray.getJSONObject(i)
                                val actionId = tempObj.getInt("id")
                                val actionName = tempObj.getString("page_name")
                                sharedPref!!.savePref(actionName, actionId)
                            }
                        }
                    } else {
                        dialog.dismiss()
                        errorHandler(this@MainActivity, result)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }

    public override fun onResume() {
        Log.d("CheckingLifeCycle", "Main Activity On Resume")
        super.onResume()
        if (appPreference!!.getBoolean("isLoginStatus", false)) {
            OneSignal.unsubscribeWhenNotificationsAreDisabled(true)
            OneSignal.setNotificationOpenedHandler(OSNotificationOpenedHandler(this@MainActivity))
            val device = OneSignal.getDeviceState()
            val subscribed = device!!.isSubscribed
            if (subscribed) {
                // get player ID
                notiPlayerId = device.userId
                notiPushNoti = device.pushToken
                val editor = appPreference!!.edit()
                editor.putString(ApiUrls.playerId, notiPlayerId)
                editor.apply()
                if (ApiUrls.doctorId != 0) {
                    sendPlayerId(ApiUrls.doctorId)
                } else {
                    sendPlayerId(0)
                }
            }
            val editor = appPreference!!.edit()
            editor.putBoolean(ApiUrls.isLoginStatus, false)
            editor.apply()
        }
        OneSignal.clearOneSignalNotifications()
    }

    override fun onStart() {
        super.onStart()
        Log.d("CheckingLifeCycle", "Main Activity On OnStart")
    }

    override fun onRestart() {
        super.onRestart()
        Log.d("CheckingLifeCycle", "Main Activity On onRestart")
    }

    override fun onPause() {
        super.onPause()
        appointmentTabFlag = 0
        patientTabFlag = 0
        ChatFragment.chatTabFlag = 0
        Log.d("CheckingLifeCycle", "Main Activity On onPuase")
    }

    override fun onStop() {
        if (mAppUpdateManager != null) {
            mAppUpdateManager!!.unregisterListener(installStateUpdatedListener)
        }
        super.onStop()
        Log.d("SampleLifeCycleggg", "ON_STOP")
        appointmentTabFlag = 0
        patientTabFlag = 0
        ChatFragment.chatTabFlag = 0
        Log.d("CheckingLifeCycle", "Main Activity On onStop")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("SampleLifeCycleggg", "ON_DESTROY")
        Log.d("CheckingLifeCycle", "Main Activity On onDestroy")
    }

    private fun logout() {
        val appUserManagers = appDatabaseManager!!.userData
        var userid = 0
        if (appUserManagers.size > 0) {
            userid = appUserManagers[0].userId
        }
        showCustomProgressAlertDialog(
            mContext!!.resources.getString(R.string.logging_out),
            mContext!!.resources.getString(R.string.please_wait_loading_message)
        )
        val playerId = appPreference!!.getString(ApiUrls.playerId, "")
        val url =
            ApiUrls.logout + "?user_id=" + userid + "&type=doctor&action=logout&player_id=" + playerId

        commonViewModel.commonViewModelCall(url, JSONObject(), Method.GET).observe(
            this@MainActivity
        ) { result ->
            try {
                dialog.dismiss()
                val responseObj = JSONObject(result)
                if (responseObj.getInt("status_code") == 200) {
                    val response = responseObj.optJSONObject("response")
                    if (response!!.getInt("response") == 1) {
                        ApiUrls.loginToken = ""
                        ZohoSalesIQ.unregisterVisitor(this@MainActivity)
                        deleteResponse = appDatabaseManager!!.deletePatient(
                            appUserManagers[0]
                        )
                        if (deleteResponse == 1) {
                            ApiUrls.loginToken = ""
                            ApiUrls.doctorId = 0
                            Toast.makeText(
                                this@MainActivity,
                                "You Have Been Successfully Logged Out",
                                Toast.LENGTH_SHORT
                            ).show()
                            val i = Intent(this@MainActivity, LoginActivity::class.java)
                            startActivity(i)
                            finish()
                        } else {
                            Toast.makeText(
                                this@MainActivity,
                                "Error While Logging Out",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } else {
                        Toast.makeText(
                            this@MainActivity,
                            "Error Logging out. Try again later",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    dialog.dismiss()
                    errorHandler(this@MainActivity, result)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                dialog.dismiss()
            }
        }
    }

    @Deprecated("Deprecated in Java")
    @SuppressLint("ResourceType")
    override fun onBackPressed() {
        if (bottomNavigationView!!.selectedItemId == 0) {
            val alert = ViewDialog()
            alert.showDialog(this@MainActivity)
        } else {
            bottomNavigationView!!.selectedItemId = 0
            bottomNavigationView!!.visibility = View.VISIBLE
            ConfirmOrderActivity.confirmOrderFlag = 0
            ConfirmOrderActivity.confirmOrderFlagChat = 0
            patientTabFlag = 0
            appointmentTabFlag = 0
            ChatFragment.chatTabFlag = 0
            ChatRoomActivity.chatClickFlag = 0
        }
    }

    inner class ViewDialog {
        fun showDialog(activity: Activity?) {
            val dialog = Dialog(activity!!)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setCancelable(false)
            dialog.setContentView(R.layout.dailog_exit_from_app)
            dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            val mDialogNo = dialog.findViewById<RelativeLayout>(R.id.frmNo)
            mDialogNo.setOnClickListener { // Toast.makeText(getApplicationContext(),"Cancel" ,Toast.LENGTH_SHORT).show();
                dialog.dismiss()
            }
            val mDialogOk = dialog.findViewById<RelativeLayout>(R.id.frmOk)
            mDialogOk.setOnClickListener {
                doctorSessionSave()
                finish()
                dialog.cancel()
            }
            dialog.show()
        }
    }

    //end session

    //save session activity
    fun doctorSessionSave() {
        val url = ApiUrls.doctorSaveSessionActivity
        try {
            jsonValue = JSONObject()
            val sharedpreferences =
                getSharedPreferences(AppConstants.appSharedPref, MODE_PRIVATE)
            if (sharedpreferences.getString("DOCTOR_SESSION_ID", "")!!.isEmpty()) {
                session_id = LoginActivity.Pref_msg_global
                Log.d("sessId_prefGlobal", session_id!!)
            } else {
                session_id = sharedpreferences.getString("DOCTOR_SESSION_ID", "")
                Log.d("sessId_shared_Pref", session_id!!)
            }
            jsonValue!!.put("session_id", session_id)
            jsonValue!!.put("platform", "android")
        } catch (e: Exception) {
            e.printStackTrace()
        }

        commonViewModel.commonViewModelCall(url,jsonValue!!,Method.POST).observe(this@MainActivity
        ) { result->
            try {
                val responseObj = JSONObject(result)
                if(responseObj.getInt("status_code")==200) {
                    val response = responseObj.optJSONObject("response")
                    val msg = response!!.getString("response")
                    val sharedPreferences =
                        getSharedPreferences(AppConstants.appSharedPref, MODE_PRIVATE)
                    val editor = sharedPreferences.edit()
                    editor.putString("DOCTOR_SESSION_ID", msg)
                    editor.apply()
                }else{
                    errorHandler(this@MainActivity, result)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    inner class ViewDialogReLogin {
        fun showDialog(activity: Activity?) {
            val dialog = Dialog(activity!!)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setCancelable(false)
            dialog.setContentView(R.layout.dailog_relogin_from_app)
            dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            val mDialogNo = dialog.findViewById<RelativeLayout>(R.id.frmNo)
            mDialogNo.setOnClickListener {
                finish()
                dialog.cancel()
            }
            val mDialogOk = dialog.findViewById<RelativeLayout>(R.id.frmOk)
            mDialogOk.setOnClickListener {
                dialog.dismiss()
                clearApplicationData()
            }
            dialog.show()
        }
    }

    //no internet connection poupop
    inner class ViewDialogNoInternet {
        fun showDialog(activity: Activity?) {
            val dialog = Dialog(activity!!)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setCancelable(false)
            dialog.setContentView(R.layout.dailog_no_internet)
            dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            val mDialogNo = dialog.findViewById<RelativeLayout>(R.id.frmNo)
            mDialogNo.setOnClickListener {
                finish()
                dialog.cancel()
            }
            val mDialogOk = dialog.findViewById<FrameLayout>(R.id.frmOk)
            mDialogOk.setOnClickListener {
                dialog.dismiss()
                logout()
            }
            dialog.show()
        }
    }

    fun trimMessage(json: String?, key: String?): String? {
        val trimmedString: String? = try {
            val obj = JSONObject(json!!)
            obj.getString(key!!)
        } catch (e: JSONException) {
            e.printStackTrace()
            return null
        }
        return trimmedString
    }

    //Somewhere that has access to a context
    fun displayMessage(toastString: String?) {
        Toast.makeText(applicationContext, toastString, Toast.LENGTH_LONG).show()
    }

    @SuppressLint("ResourceType")
    fun homeFragmentOpen() {
        val selectedFragment: Fragment?
        selectedFragment = DashboardFragment.newInstance()
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragmentContainer, selectedFragment)
        transaction.commit()
        bottomNavigationView!!.selectedItemId = 0
    }

    private val isOnline: Boolean
        get() {
            val cm = getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
            val netInfo = cm.activeNetworkInfo
            return netInfo != null && netInfo.isConnectedOrConnecting
        }

    private fun checkConnection() {
        if (isOnline) {
            bottomNavigationView!!.visibility = View.VISIBLE
            navigationMenuNoDataText.visibility = View.GONE
        } else {
            bottomNavigationView!!.visibility = View.GONE
            navigationMenuNoDataText.visibility = View.VISIBLE
            val alertRelogin = ViewDialogNoInternet()
            alertRelogin.showDialog(this@MainActivity)
        }
    }

    fun clearApplicationData() {
        val appUserManagers = appDatabaseManager!!.userData
        ApiUrls.loginToken = ""
        ZohoSalesIQ.unregisterVisitor(this)
        deleteResponse = appDatabaseManager!!.deletePatient(
            appUserManagers[0]
        )
        if (deleteResponse == 1) {
            ApiUrls.loginToken = ""
            ApiUrls.doctorId = 0
            val i = Intent(mContext, LoginActivity::class.java)
            i.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            mContext!!.startActivity(i)
            finish()
        } else {
            Toast.makeText(mContext, "Error While Logging Out", Toast.LENGTH_SHORT).show()
        }
    }

    fun changeFragment() {
        appUtilities!!.selectBottomNavigationScreen("Patients")
        bottomNavigationView!!.selectedItemId = bottomNavigationView!!.selectedItemId
    }

    private fun setupViewPager(viewPager: ViewPager?) {
        val adapter = ViewPagerAdapter(supportFragmentManager)
        dashboardFragment = DashboardFragment()
        patientFragment = PatientFragment.newInstance()
        newAppointmentFragment = NewAppointmentFragment.newInstance()
        val chatFragment = ChatFragment.newInstance()
        adapter.addFragment(dashboardFragment!!)
        adapter.addFragment(patientFragment!!)
        adapter.addFragment(newAppointmentFragment!!)
        adapter.addFragment(chatFragment)
        viewPager!!.adapter = adapter
    }

    private inner class ViewPagerAdapter(manager: FragmentManager?) : FragmentStatePagerAdapter(
        manager!!
    ) {
        private val mFragmentList: MutableList<Fragment> = ArrayList()
        override fun getItem(position: Int): Fragment {
            return mFragmentList[position]
        }

        override fun getCount(): Int {
            return mFragmentList.size
        }

        fun addFragment(fragment: Fragment) {
            mFragmentList.add(fragment)
        }
    }

    private fun sendPlayerId(patientID: Int) {
        val url = ApiUrls.insertNotiPlayerId
        val notifyObj = JSONObject()
        try {
            notifyObj.put("userId", patientID)
            notifyObj.put("playerId", notiPlayerId)
            notifyObj.put("pushToken", notiPushNoti)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        apiGetPostMethodCalls!!.volleyApiRequestData(url,
            Request.Method.POST,
            notifyObj,
            this@MainActivity,
            object : VolleyCallback {
                override fun onSuccess(result: String) {}
                override fun onError(err: String) {
//                        ErrorHandlerClass.INSTANCE.errorHandler(LoginActivity.this, err);
                }
            })
    }

    companion object {
        @JvmField
        var bottomNavigationView: BottomNavigationView? = null
        var appDatabaseManager: AppDatabaseManager? = null
        var getDashBoardJsonResponse: String? = null

        @JvmField
        var getMenuJsonObject: JSONObject? = null
        var deleteResponse = 0
        var payStoreVersion: String? = null
        var currentVersion: String? = null

        @JvmField
        var docName: String? = null

        @JvmField
        var docEmail: String? = null

        @JvmField
        var docPhone: String? = null
        lateinit var viewPager: SwipeDisabledViewPager
        private const val RC_APP_UPDATE = 11
        fun getId(resourceName: String, c: Class<*>): Int {
            return try {
                val idField = c.getDeclaredField(resourceName)
                idField.getInt(idField)
            } catch (e: Exception) {
                throw RuntimeException(
                    "No resource ID found for: "
                            + resourceName + " / " + c, e
                )
            }
        }
    }

    fun showCustomProgressAlertDialog(
        title: String?,
        progressVal: String?

    ) {
        val builder = android.app.AlertDialog.Builder(this@MainActivity)
        builder.setTitle(title)
        builder.setCancelable(false)
        val customLayout =
            LayoutInflater.from(this@MainActivity)
                .inflate(R.layout.custom_progress_bar, null)
        builder.setView(customLayout)
        dialog = builder.create()
        val tvBody = customLayout.findViewById<TextView>(R.id.tv_value)
        tvBody.text = progressVal
        dialog.show()
    }
}