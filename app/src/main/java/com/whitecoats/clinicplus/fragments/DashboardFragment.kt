package com.whitecoats.clinicplus.fragments

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.ConnectivityManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.provider.Settings
import android.util.Base64
import android.util.Log
import android.view.*
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.os.postDelayed
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager.widget.ViewPager
import com.android.volley.Request
import com.android.volley.Request.Method
import com.android.volley.toolbox.Volley
import com.brandkinesis.BKUserInfo
import com.brandkinesis.BrandKinesis
import com.brandkinesis.activitymanager.BKActivityTypes
import com.bumptech.glide.Glide
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import com.google.android.material.tabs.TabLayout.TabLayoutOnPageChangeListener
import com.whitecoats.adapter.AssistantTabAdapter
import com.whitecoats.broadcast.HomeBroadCastReceiver
import com.whitecoats.clinicplus.*
import com.whitecoats.clinicplus.apis.ApiGetPostMethodCalls
import com.whitecoats.clinicplus.apis.ApiUrls
import com.whitecoats.clinicplus.constants.AppConstants
import com.whitecoats.clinicplus.utils.ErrorHandlerClass.errorHandler
import com.whitecoats.clinicplus.viewmodels.CommonViewModel
import com.whitecoats.clinicplus.viewmodels.DashboardViewModel
import com.whitecoats.clinicplus.viewmodels.SharedViewModel
import com.zoho.livechat.android.exception.InvalidVisitorIDException
import com.zoho.salesiqembed.ZohoSalesIQ
import org.json.JSONException
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*
import kotlin.concurrent.timerTask

open class DashboardFragment : Fragment() {
    private lateinit var homeDocName: TextView
    private lateinit var homeDocImg: ImageView
    var appDatabaseManager: AppDatabaseManager? = null
    private var globalClass: MyClinicGlobalClass? = null
    private lateinit var homeView: View
    private var homeBroadCastReceiver: HomeBroadCastReceiver? = null
    val CUSTOM_BROADCAST_ACTION = "HomeBroadCastReceiver"
    private var dashboardViewModel: DashboardViewModel? = null
    var adapter: MainTabPagerAdapter? = null
    var android_id: String? = null
    var specialStr = ""
    var appVersion = ""
    private var appPreference: SharedPreferences? = null
    var jsonValue: JSONObject? = null
    var session_id: String? = null
    private lateinit var bkInstance: BrandKinesis
    private val dashBoardTag = "Dashboard tag"
    private var sharedViewModel: SharedViewModel? = null
    private var globalApiCall: ApiGetPostMethodCalls? = null
    private lateinit var commonViewModel: CommonViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setHasOptionsMenu(true)
        bkInstance = BrandKinesis.getBKInstance()
        bkInstance.getActivity(activity, BKActivityTypes.ACTIVITY_ANY, dashBoardTag)
        sharedViewModel = ViewModelProvider(requireActivity())[SharedViewModel::class.java]
        globalApiCall = ApiGetPostMethodCalls()
        commonViewModel = ViewModelProvider(requireActivity())[CommonViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        homeView = inflater.inflate(R.layout.fragment_dashboard, container, false)
        val settings = homeView.findViewById<ImageView>(R.id.homeSettings)
        dashboardViewModel = ViewModelProvider(this).get(DashboardViewModel::class.java)
        dashboardViewModel!!.init()
        homeDocName = homeView.findViewById(R.id.homeDocName)
        homeDocImg = homeView.findViewById(R.id.homeDocImg)
        settings.setColorFilter(ContextCompat.getColor(requireContext(), R.color.colorWhite))
        globalClass = requireActivity().applicationContext as MyClinicGlobalClass
        appDatabaseManager = AppDatabaseManager(activity)
        homeBroadCastReceiver = HomeBroadCastReceiver()
        progressBarDasBoardFragment = homeView.findViewById(R.id.dashboard_tab_progress)
        tabLayout = homeView.findViewById(R.id.tabLayout)
        tabLayout.addTab(tabLayout.newTab().setText("Dashboard"))
        tabLayout.addTab(tabLayout.newTab().setText("Assistant"))
        tabLayout.addTab(tabLayout.newTab().setText("Support"))
        tabLayout.tabGravity = TabLayout.GRAVITY_FILL
        tabLayout.tabMode = TabLayout.MODE_FIXED
        //initView();
        //getDoctorDetails(); // commented on 6th jan and put method in onResume()
        ApiUrls.bottomNaviType = 0
        val filter = IntentFilter()
        filter.addAction(CUSTOM_BROADCAST_ACTION)
        filter.addCategory(Intent.CATEGORY_DEFAULT)
        requireActivity().registerReceiver(homeBroadCastReceiver, filter)
        dashBoardQuickLink
        MainActivity.bottomNavigationView!!.visibility = View.VISIBLE

        //sending broadcast
        /*set the package name for broadcast and changes the custom_broadcast_action string value to normal string*/
        val intent = Intent(CUSTOM_BROADCAST_ACTION)
        intent.putExtra("Activity", "HomeTab")
        intent.setPackage(requireActivity().packageName)
        requireActivity().sendBroadcast(intent)
        appPreference =
            requireActivity().getSharedPreferences(ApiUrls.appSharedPref, Context.MODE_PRIVATE)
        viewPager = homeView.findViewById(R.id.viewPager)
        tabLayout.setOnTabSelectedListener(object : OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                if (isOnline) {
                    viewPager!!.currentItem = tab.position
                    Log.d("Current tab", tab.position.toString() + "")
                    if (tab.position == 1) {
                        MainActivity.bottomNavigationView!!.visibility = View.GONE
                        ZohoSalesIQ.Tracking.setPageTitle("On AssistantTab")
                        ApiUrls.homeGuideTab = 1

                        //sending broadcast
                        /*set the package name for broadcast and changes the custom_broadcast_action string value to normal string*/
                        val intent = Intent(CUSTOM_BROADCAST_ACTION)
                        intent.putExtra("Activity", "Assistant")
                        if (activity != null) {
                            intent.setPackage(activity!!.packageName)
                            activity!!.sendBroadcast(intent)
                        }
                    }
                    if (tab.position == 2) {
                        if (caseChannelPresent) {
                            ZohoSalesIQ.Tracking.setPageTitle("On Case Channels Tab")
                            ApiUrls.homeGuideTab = 2
                            AssistantTabAdapter.assistantPatientProfileRecordFlag = 0
                            MainActivity.bottomNavigationView!!.visibility = View.GONE
                        } else {
                            ZohoSalesIQ.Tracking.setPageTitle("On SupportTab")
                            ApiUrls.homeGuideTab = 2
                            AssistantTabAdapter.assistantPatientProfileRecordFlag = 0
                        }
                    }
                    if (tab.position == 3) {
                        ZohoSalesIQ.Tracking.setPageTitle("On SupportTab")
                        ApiUrls.homeGuideTab = 3
                        AssistantTabAdapter.assistantPatientProfileRecordFlag = 0
                    }
                    if (tab.position == 0) {
                        MainActivity.bottomNavigationView!!.visibility = View.VISIBLE
                        ZohoSalesIQ.Tracking.setPageTitle("On HomeTab")
                        ApiUrls.homeGuideTab = 0
                        AssistantTabAdapter.assistantPatientProfileRecordFlag = 0
                    }
                    if (tab.position != 1 && tab.position != 2 && caseChannelPresent) {
                        MainActivity.bottomNavigationView!!.visibility = View.VISIBLE
                    }
                } else {
                    val noInternetConnection = ViewDialogNoInternet()
                    noInternetConnection.showDialog(activity)
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })
        settings.setOnClickListener {
            val data = HashMap<String, Any>()
            data["onProfileClicked"] = "onProfileClicked"
            MyClinicGlobalClass.logUserActionEvent(
                ApiUrls.doctorId, getString(R.string.onProfileClicked),
                data
            )
            if (globalClass!!.isOnline) {
                val intent = Intent(activity, SettingsActivity::class.java)
                startActivity(intent)
            } else {
                globalClass!!.noInternetConnection.showDialog(activity)
            }
        }
        return homeView
    }

    /*@Deprecated("Deprecated in Java")
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        // Do something that differs the Activity's menu here
        inflater.inflate(R.menu.home_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }*/

    private fun setToolbarMenu() {
        requireActivity().addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.home_menu, menu)

            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return false
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }


    override fun onDestroy() {
        super.onDestroy()
        try {
            requireActivity().unregisterReceiver(homeBroadCastReceiver)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setToolbarMenu()
        progressBarDasBoardFragment!!.visibility = View.VISIBLE
        doctorDetails
        if (MyClinicGlobalClass.startSessionFlag) {
            MyClinicGlobalClass.startSessionFlag = false
        } else {
            doctorSessionSave()
        }
    }

    //save session activity
    private fun doctorSessionSave() {
        Volley.newRequestQueue(activity)
        val url = ApiUrls.doctorSaveSessionActivity
        try {
            jsonValue = JSONObject()
            val sharedpreferences =
                requireActivity().getSharedPreferences(
                    AppConstants.appSharedPref,
                    Context.MODE_PRIVATE
                )
            if (sharedpreferences.getString("DOCTOR_SESSION_ID", "")!!.isEmpty()) {
                session_id = LoginActivity.Pref_msg_global
                Log.d("sessId_prefGlobal", session_id!!)
            } else {
                //SharedPreferences sharedpreferences = getSharedPreferences(AppConstants.appSharedPref, Context.MODE_PRIVATE);
                session_id = sharedpreferences.getString("DOCTOR_SESSION_ID", "")
                Log.d("sessId_shared_Pref", session_id!!)
            }
            jsonValue!!.put("session_id", session_id)
            jsonValue!!.put("platform", "android")
        } catch (e: Exception) {
            e.printStackTrace()
        }

        commonViewModel.commonViewModelCall(url, jsonValue!!, Method.POST).observe(
            requireActivity()
        ) { result ->
            try {
                val responseObj = JSONObject(result)
                if (responseObj.getInt("status_code") == 200) {
                    val response = responseObj.optJSONObject("response")
                    Log.d("Session_Save_Activity", response!!.toString())
                    val msg = response.getString("response")
                    val sharedPreferences = requireActivity().getSharedPreferences(
                        AppConstants.appSharedPref,
                        Context.MODE_PRIVATE
                    )
                    val editor = sharedPreferences.edit()
                    editor.putString("DOCTOR_SESSION_ID", msg)
                    editor.apply()
                } else {
                    errorHandler(requireActivity(), result)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    //setup viewpager

    //getting language
    //public void getDoctorDetails () {
    private val doctorDetails: Unit
        @SuppressLint("HardwareIds")
        get() {

            //public void getDoctorDetails () {
            dashboardViewModel!!.getDashboardDoctorDetails(activity)
                .observe(viewLifecycleOwner) { s ->
                    try {
                        val jsonObject = JSONObject(s)
                        if (jsonObject.getInt("status_code") == 200) {
                            val response = jsonObject.getJSONObject("response")
                            Log.d("DoctorDetailsResponse", response.toString())
                            val rootObj = response.optJSONObject("response")
                            val userObj = rootObj!!.optJSONObject("user")
                            val sharedPreferences = requireContext().getSharedPreferences(
                                ApiUrls.appSharedPref,
                                Context.MODE_PRIVATE
                            )
                            val editor = sharedPreferences.edit()
                            editor.putInt(ApiUrls.docId, userObj!!.getInt("id"))
                            editor.apply()
                            MainActivity.docName = userObj.getString("fname")
                            MainActivity.docPhone = userObj.getString("phone")
                            MainActivity.docEmail = userObj.getString("email")
                            ZohoSalesIQ.registerVisitor(userObj.getInt("id").toString())
                            ZohoSalesIQ.Visitor.setName(userObj.getString("fname"))
                            ZohoSalesIQ.Visitor.setContactNumber(userObj.getString("phone"))
                            ZohoSalesIQ.Visitor.setEmail(userObj.getString("email"))


                            //setup viewpager
                            adapter = MainTabPagerAdapter(
                                childFragmentManager,
                                tabLayout.tabCount,
                                caseChannelPresent,
                                s
                            )
                            viewPager!!.adapter = adapter
                            viewPager!!.addOnPageChangeListener(
                                TabLayoutOnPageChangeListener(
                                    tabLayout
                                )
                            )
                            viewPager!!.currentItem = ApiUrls.homeGuideTab
                            val specializationsArr = userObj.getJSONArray("specializations")
                            for (i in 0 until specializationsArr.length()) {
                                val tempobj = specializationsArr.getJSONObject(i)
                                specialStr += tempobj.getString("specialization")
                                if (i != specializationsArr.length() - 1) {
                                    specialStr += " | "
                                }
                            }
                            val qualificationsArr = userObj.getJSONArray("qualifications")
                            var qualificationsStr = ""
                            for (i in 0 until qualificationsArr.length()) {
                                val tempobj = qualificationsArr.getJSONObject(i)
                                qualificationsStr += tempobj.getString("qualification")
                                if (i != qualificationsArr.length() - 1) {
                                    qualificationsStr += " | "
                                }
                            }
                            homeDocName.text = userObj.getString("fname")
                            getImageData(userObj.getString("image_v2"))

                            //getting language
                            val languagesArray = userObj.getJSONArray("languages")
                            var temp = ""
                            for (i in 0 until languagesArray.length()) {
                                val obj = languagesArray.getJSONObject(i)
                                temp =
                                    (temp + obj.getString("language").substring(0, 1).uppercase(
                                        Locale.getDefault()
                                    )
                                            + obj.getString("language").substring(1) + ":")
                            }
                            val appDoctorManagers = appDatabaseManager!!.doctorData
                            val appDoctorManager = AppDoctorManager(
                                userObj.getInt("id"),
                                userObj.getString("fname"),
                                userObj.getString("phone"),
                                userObj.getInt("gender"),
                                userObj.getString("email"),
                                userObj.getString("exp"),
                                temp
                            )
                            Log.d("Lenght", appDoctorManagers.size.toString() + "")
                            if (appDoctorManagers.size == 0) {
                                appDatabaseManager!!.addDoctor(appDoctorManager)
                            } else {
                                //AppConfigClass.doctorId = appDoctorManagers.get(0).getDocId();
                                //Log.d("Doctor ID", AppConfigClass.doctorId + "");
                            }
                            val docId = appDoctorManager.docId
                            MyClinicGlobalClass.logUserActionEvent(
                                docId, getString(R.string.HomeDashboardScreen),
                                null
                            )
                            android_id = Settings.Secure.getString(
                                requireActivity().contentResolver,
                                Settings.Secure.ANDROID_ID
                            )
                            val jsonObject1 = JSONObject()
                            try {
                                jsonObject1.put("X-DEVICE-ID", android_id)
                            } catch (e: JSONException) {
                                e.printStackTrace()
                            }
                            val userInfo = Bundle()
                            try {
                                val pInfo = requireActivity().packageManager.getPackageInfo(
                                    requireActivity().packageName, 0
                                )
                                appVersion = pInfo.versionName
                            } catch (e: PackageManager.NameNotFoundException) {
                                e.printStackTrace()
                            }
                            val sdf = SimpleDateFormat("yyyy.MM.dd HH:mm:ss", Locale.ENGLISH)
                            val currentDateandTime = sdf.format(Date())
                            val androidRelease = Build.VERSION.RELEASE
                            userInfo.putString(
                                BKUserInfo.BKExternalIds.APPUID,
                                appDoctorManager.docId.toString()
                            )
                            userInfo.putString(
                                BKUserInfo.BKUserData.USER_NAME,
                                appDoctorManager.docName
                            )
                            val others = HashMap<String, Any?>()
                            others["DocSpeciality"] = specialStr
                            others["AppVersion"] = appVersion
                            others["OsVersion"] = androidRelease
                            others["DocId"] = appDoctorManager.docId
                            others["last_app_usage_date"] = currentDateandTime
                            val loginDate = appPreference!!.getString(ApiUrls.lastLoginDate, "")
                            others["Last_Login_Date"] = loginDate
                            userInfo.putSerializable(BKUserInfo.BKUserData.OTHERS, others)
                            val bkInstance = BrandKinesis.getBKInstance()
                            Log.d("BUNDLE_DATA", userInfo.toString())
                            bkInstance?.setUserInfoBundle(userInfo) { uploadStatus ->
                                Log.d(
                                    "UPSHOT_USER_LOG",
                                    "result :$uploadStatus"
                                )
                            }
                            Handler().postDelayed({
                                progressBarDasBoardFragment!!.visibility = View.GONE
                            }, 300)
                        } else {
                           // sharedViewModel!!.sendErrorResponse(s)
                            //  progressBarDasBoardFragment!!.visibility = View.GONE
                            errorHandler(requireContext(), s)
                        }
                    } catch (e: Exception) {
                        //  progressBarDasBoardFragment!!.visibility = View.GONE
                        e.printStackTrace()
                    }
                }
        }

    private fun getImageData(url: String) {
        Log.d("imageURL", "imageURL$url")
        Glide.with(requireActivity())
            .load(url)
            .into(homeDocImg)
    }

    private val dashBoardQuickLink: Unit
        get() {
            dashboardViewModel!!.getDashboardQuickLink(activity)
                .observe(viewLifecycleOwner) { s ->
                    try {
                        val jsonObject = JSONObject(s)
                        if (jsonObject.getInt("status_code") == 200) {
                            val response = jsonObject.getJSONObject("response")
                            val menuArr =
                                response.getJSONObject("response").getJSONArray("menu")
                            if (menuArr.length() > 0) {
                                for (i in 0 until menuArr.length()) {
                                    val temp = menuArr.getJSONObject(i)
                                    if (temp.getInt("id") == 49) {
                                        caseChannelPresent = true
                                        tabLayout.addTab(
                                            tabLayout.newTab().setText("Case Channels"), 2
                                        )
                                        tabLayout.tabMode = TabLayout.MODE_SCROLLABLE
                                        break
                                    }
                                }
                            }
                        }
                        if (tabLayout.tabCount == 3) {
                            tabLayout.tabGravity = TabLayout.GRAVITY_FILL
                            tabLayout.tabMode = TabLayout.MODE_FIXED
                        } else {
                            errorHandler(requireContext(), s)
                        }
                    } catch (e: JSONException) {
                        Log.d("quickError", "quickError$e")
                        e.printStackTrace()
                    }
                }
        }

    protected val isOnline: Boolean
        get() {
            val cm =
                requireActivity().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
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
            /*Changed the Relative layout to Floating Action Button to avoid the crash*/
            val mDialogNo = dialog.findViewById<FloatingActionButton>(R.id.frmNo)
            mDialogNo.setOnClickListener { dialog.cancel() }
            /*Changed the Frame layout to Floating Action Button to avoid the crash*/
            val mDialogOk = dialog.findViewById<FloatingActionButton>(R.id.frmOk)
            mDialogOk.setOnClickListener {
                dialog.dismiss()
                // logout();
            }
            dialog.show()
        }
    }

    fun StringToBitMap(encodedString: String?): Bitmap? {
        return try {
            val encodeByte =
                Base64.decode(encodedString, Base64.DEFAULT)
            BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.size)
        } catch (e: Exception) {
            e.message
            null
        }
    }

    companion object {
        lateinit var tabLayout: TabLayout

        @JvmField
        var viewPager: ViewPager? = null

        @JvmField
        var caseChannelPresent = false

        @SuppressLint("StaticFieldLeak")
        @JvmField
        var progressBarDasBoardFragment: ProgressBar? = null

        @JvmStatic
        fun newInstance(): DashboardFragment {
            return DashboardFragment()
        }
    }
}