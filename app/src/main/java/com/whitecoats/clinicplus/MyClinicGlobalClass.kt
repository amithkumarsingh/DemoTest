package com.whitecoats.clinicplus

import android.annotation.SuppressLint
import android.app.Activity
import android.app.ActivityManager
import android.app.ActivityManager.RunningAppProcessInfo
import android.app.Application
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.ConnectivityManager
import android.os.Build
import android.util.Log
import android.view.Window
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ProcessLifecycleOwner
import com.android.volley.Request
import com.brandkinesis.BrandKinesis
import com.brandkinesis.utils.BKAppStatusUtil.BKAppStatusListener
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.onesignal.OneSignal
import com.whitecoats.clinicplus.apis.ApiGetPostMethodCalls
import com.whitecoats.clinicplus.apis.ApiUrls
import com.whitecoats.clinicplus.constants.AppConstants
import com.whitecoats.clinicplus.utils.ClinicPlusFlavours
import com.whitecoats.clinicplus.utils.ErrorHandlerClass.errorHandler
import com.whitecoats.clinicplus.utils.UpShotHelperClass
import com.whitecoats.clinicplus.utils.UpshotConstants
import com.zoho.salesiqembed.ZohoSalesIQ
import org.json.JSONException
import org.json.JSONObject

/*import io.sentry.Sentry;
import io.sentry.android.core.SentryAndroid;*/
class MyClinicGlobalClass : Application(), LifecycleObserver, BKAppStatusListener {
    var jsonValue: JSONObject? = null
    private var upshotHelper: UpShotHelperClass? = null

    //private static MyClinicGlobalClass instance;
    var session_id: String? = null
    private var globalApiCall: ApiGetPostMethodCalls? = null
    fun isAppIsInBackground(context: Context): Boolean {
        var isInBackground = true
        val am = context.getSystemService(ACTIVITY_SERVICE) as ActivityManager
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH) {
            val runningProcesses = am.runningAppProcesses
            for (processInfo in runningProcesses) {
                if (processInfo.importance == RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    for (activeProcess in processInfo.pkgList) {
                        if (activeProcess == context.packageName) {
                            isInBackground = false
                        }
                    }
                }
            }
        } else {
            val taskInfo = am.getRunningTasks(1)
            val componentInfo = taskInfo[0].topActivity
            if (componentInfo!!.packageName == context.packageName) {
                isInBackground = false
            }
        }
        return isInBackground
    }

    fun isApplicationInTheBackground(context: Context): Int {
        val activityManager = context.getSystemService(ACTIVITY_SERVICE) as ActivityManager
        val services = activityManager
            .getRunningTasks(Int.MAX_VALUE)
        var isActivityFound = false
        if (services[0].topActivity!!.packageName
                .equals(context.packageName.toString(), ignoreCase = true)
        ) {
            isActivityFound = true
        }
        return if (isActivityFound) {
            1
        } else {
            2
            // write your code to build a notification.
            // return the notification you built here
        }
    }

    val isOnline: Boolean
        get() {
            val cm = getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
            val netInfo = cm.activeNetworkInfo
            return netInfo != null && netInfo.isConnectedOrConnecting
        }

    inner class ViewDialogNoInternet {
        fun showDialog(activity: Activity?) {
            val dialog = Dialog(activity!!)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setCancelable(false)
            dialog.setContentView(R.layout.dailog_no_internet_connection)
            dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            val mDialogNo = dialog.findViewById<FloatingActionButton>(R.id.frmNo)
            mDialogNo.setOnClickListener { dialog.cancel() }
            val mDialogOk = dialog.findViewById<FloatingActionButton>(R.id.frmOk)
            mDialogOk.setOnClickListener {
                dialog.dismiss()
                // logout();
            }
            dialog.show()
        }
    }

    @JvmField
    var noInternetConnection: ViewDialogNoInternet = ViewDialogNoInternet()
    override fun onCreate() {
        super.onCreate()
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
        context = applicationContext
        /*
         * Set Default development server & default Flavor
         * Values - 'qa',qakb,'dev','stage','uat','prod'
         *
         * <p>This is only for development mode, in release mode app will generate flavors as specified
         * in gradle.</p>
         */if (BuildConfig.DEBUG) {
            setDefaultEnvironment("prod")
        } else {
            if (BuildConfig.FLAVOR.equals(ClinicPlusFlavours.DEV.environment, ignoreCase = true)) {
                setDefaultEnvironment("dev")
            } else if (BuildConfig.FLAVOR.equals(
                    ClinicPlusFlavours.QA.environment,
                    ignoreCase = true
                )
            ) {
                setDefaultEnvironment("qa")
            } else if (BuildConfig.FLAVOR.equals(
                    ClinicPlusFlavours.QAKB.environment,
                    ignoreCase = true
                )
            ) {
                setDefaultEnvironment("qakb")
            } else if (BuildConfig.FLAVOR.equals(
                    ClinicPlusFlavours.UAT.environment,
                    ignoreCase = true
                )
            ) {
                setDefaultEnvironment("uat")
            } else if (BuildConfig.FLAVOR.equals(
                    ClinicPlusFlavours.STAGE.environment,
                    ignoreCase = true
                )
            ) {
                setDefaultEnvironment("stage")
            } else if (BuildConfig.FLAVOR.equals(
                    ClinicPlusFlavours.PROD.environment,
                    ignoreCase = true
                )
            ) {
                setDefaultEnvironment("prod")
            }
        }
        globalApiCall = ApiGetPostMethodCalls()
        instance = this
        OneSignal.initWithContext(this)
        OneSignal.setAppId(ONESIGNAL_APP_ID)
        ZohoSalesIQ.init(
            this,
            "Bv4h3qkiMVo%2F3q2scRJ9Qg4DBQpHPo8kThBM6M4lyEGSmpFHytW8azRf3PWdr4WV_in",
            "MrowfqPP5kL5Iy%2FfMAe1hqkk2Uov%2Ff%2BRJLGDXN8XFoSlBsaopUbZwagy7xpZwuduSQsXSanzgygY378duoYInsjxQ5Gv2twXh%2FBrPbHN%2BsxxPhYxVGVdyqLL%2Bl8UAc7U0%2FqM8ZIWSpBZwb4zw1bkFHpNWI3C2YpR2a1vYjRi9vE%3D"
        )
        ZohoSalesIQ.showLauncher(false)
        val appDatabaseManager = AppDatabaseManager(this)
        val appUserManagers = appDatabaseManager.userData
        if (appUserManagers.size > 0) {
            ApiUrls.loginToken = appUserManagers[0].token
            ApiUrls.doctorId = appUserManagers[0].userId
            ApiUrls.isDoctorOnly = appUserManagers[0].isDoctorOnly
        }
        val sharedpreferences = getSharedPreferences(AppConstants.appSharedPref, MODE_PRIVATE)
        val editor = sharedpreferences.edit()
        editor.putBoolean("InVideoSession", false)
        editor.putBoolean("InVideoCalling", false)
        editor.apply()
        //sentry initialization
        /* SentryAndroid.init(this, options -> {
            String dns="https://a447921805b1440195938d2cbb44a3f0@o574145.ingest.sentry.io/5827089";
            options.setDsn(dns);
            options.setTracesSampleRate(1.0);
        });*/initApplication()

//        UPSHOT_APP_ID = UpshotConstants.qa_app_id;
        UPSHOT_APP_ID = UpshotConstants.prod_app_id
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun created() {
        Log.d("SampleLifeCycle", "ON_CREATE")
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun started() {
        Log.d("SampleLifeCycle", "ON_START")
        //doctorSessionLoginAndLogout(1);
        doctorSessionSave()
        //Sentry.captureMessage("ClinicPlus Sentry");
        startSessionFlag = true
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun resumed() {
        Log.d("SampleLifeCycle", "ON_RESUME")
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    fun paused() {
        Log.d("SampleLifeCycle", "ON_PAUSE")
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun stopped() {
        Log.d("SampleLifeCycle", "ON_STOP")
        //doctorSessionLoginAndLogout(2);
        doctorSessionSave()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun destroyed() {
        Log.d("SampleLifeCycle", "ON_DESTROY")
    }

    //save session activity
    private fun doctorSessionSave() {
        val url = ApiUrls.doctorSaveSessionActivity
        try {
            jsonValue = JSONObject()
            val sharedpreferences = getSharedPreferences(AppConstants.appSharedPref, MODE_PRIVATE)
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
        globalApiCall!!.volleyApiRequestData(
            url,
            Request.Method.POST,
            jsonValue,
            this,
            object : VolleyCallback {
                override fun onSuccess(result: String) {
                    try {
                        val response = JSONObject(result)
                        Log.d("Session_Save_Activity", response.toString())
                        val Msg = response.getString("response")
                        val sharedpreferences =
                            getSharedPreferences(AppConstants.appSharedPref, MODE_PRIVATE)
                        val editor = sharedpreferences.edit()
                        editor.putString("DOCTOR_SESSION_ID", Msg)
                        editor.apply()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                override fun onError(err: String) {}
            })
    }

    fun doctorSessionLoginAndLogout(inOut: Int) {
        val url = ApiUrls.doctorSessionInOut
        try {
            jsonValue = JSONObject()
            jsonValue!!.put("user_type", "doctor")
            if (inOut == 1) {
                jsonValue!!.put("action", "session_in")
            } else {
                jsonValue!!.put("action", "session_out")
            }
            jsonValue!!.put("platform", "android")
        } catch (e: Exception) {
            e.printStackTrace()
        }
        globalApiCall!!.volleyApiRequestData(
            url,
            Request.Method.POST,
            jsonValue,
            this,
            object : VolleyCallback {
                override fun onSuccess(result: String) {
                    try {
                        val resObj = JSONObject(result)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                override fun onError(err: String) {
                    errorHandler(this@MyClinicGlobalClass, err)
                }
            })
    }

    override fun onAppComesForeground(activity: Activity) {
        if (upshotHelper != null) {
            Log.d("UPSHOT_INIT", "start init")
            upshotHelper!!.initUpshot(activity)
        }
    }

    override fun onAppGoesBackground() {
        val brandKinesis = BrandKinesis.getBKInstance()
        brandKinesis.terminate(applicationContext)
    }

    override fun onAppRemovedFromRecentsList() {
        Log.d("UPSHOT_INIT", "app removed")
    }

    private fun initApplication() {
        Log.i(TAG, "initApplication()")
        instance = this
        upshotHelper = UpShotHelperClass.getInstance()
        upshotHelper!!.registerAppInUpshot(this, this)
    }

    private fun setDefaultEnvironment(environment: String?) {
        productionURL = when (environment) {
            "qa" -> "https://qa-arth-seed.whitecoats.net"
            "qakb" -> "https://qakb-arth-seed.whitecoats.net"
            "stage" -> "https://stage-arth-seed.whitecoats.net"
            "uat" -> "https://uat-arth-seed.whitecoats.net"
            "prod" -> "https://arth-seed.whitecoats.com"
            else -> "https://dev-arth-seed.whitecoats.net"
        }
    }

    companion object {
        val TAG = MyClinicGlobalClass::class.java.simpleName
        private const val ONESIGNAL_APP_ID = "64c60354-296b-4753-8fbc-7c795a8a627b"
        @SuppressLint("StaticFieldLeak")
        var context: Context? = null
            private set

        @SuppressLint("StaticFieldLeak")
        @get:Synchronized
        var instance: MyClinicGlobalClass? = null
            private set
        @JvmField
        var UPSHOT_APP_ID = ""
        var startSessionFlag = false
        @JvmField
        var productionURL = ""
        //upshot events
        @JvmStatic
        fun logUserActionEvent(
            docId: Int,
            eventName: String?,
            upShotEventMap: HashMap<String, Any>?
        ) {
            val bkInstance = BrandKinesis.getBKInstance()
            bkInstance.createEvent(eventName, upShotEventMap, false)
        }

        //converting jason to hashMap
        @JvmStatic
        @Throws(JSONException::class)
        fun convertJsonToHashMap(jObject: JSONObject): HashMap<String, Any> {
            val map = HashMap<String, Any>()
            val keys: Iterator<*> = jObject.keys()
            while (keys.hasNext()) {
                val key = keys.next() as String
                val value = jObject.getString(key)
                map[key] = value
            }
            return map
        }
    }
}