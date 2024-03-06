package com.whitecoats.fragments

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.ConnectivityManager
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.android.volley.Request
import com.android.volley.toolbox.ImageRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import com.google.android.material.tabs.TabLayout.TabLayoutOnPageChangeListener
import com.whitecoats.adapter.AssistantTabAdapter
import com.whitecoats.broadcast.HomeBroadCastReceiver
import com.whitecoats.clinicplus.*
import com.whitecoats.clinicplus.apis.ApiGetPostMethodCalls
import com.whitecoats.clinicplus.apis.ApiUrls
import com.whitecoats.clinicplus.utils.ErrorHandlerClass.errorHandler
import com.zoho.salesiqembed.ZohoSalesIQ
import org.json.JSONObject
import java.util.*

class HomeFragment : Fragment() {
    private lateinit var homeDocName: TextView
    private lateinit var homeDocImg: ImageView
    var appDatabaseManager: AppDatabaseManager? = null
    val CUSTOM_BROADCAST_ACTION = "HomeBroadCastReceiver"
    private var homeBroadCastReceiver: HomeBroadCastReceiver? = null
    var globalClass: MyClinicGlobalClass? = null
    private lateinit var tabLayout: TabLayout
    var adapter: MainTabPagerAdapter? = null
    private var globalApiCall: ApiGetPostMethodCalls? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // setHasOptionsMenu(true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setToolBarMenu()

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val homeView = inflater.inflate(R.layout.fragment_home, container, false)
        val settings = homeView.findViewById<ImageView>(R.id.homeSettings)
        homeDocName = homeView.findViewById(R.id.homeDocName)
        homeDocImg = homeView.findViewById(R.id.homeDocImg)
        settings.setColorFilter(ContextCompat.getColor(requireContext(), R.color.colorWhite))
        globalClass = requireActivity().applicationContext as MyClinicGlobalClass
        appDatabaseManager = AppDatabaseManager(activity)
        homeBroadCastReceiver = HomeBroadCastReceiver()
        globalApiCall = ApiGetPostMethodCalls()
        tabLayout = homeView.findViewById(R.id.homeTabLayout)
        tabLayout.addTab(tabLayout.newTab().setText("Dashboard"))
        tabLayout.addTab(tabLayout.newTab().setText("Assistant"))
        //        tabLayout.addTab(tabLayout.newTab().setText("Case Channels"));
        tabLayout.addTab(tabLayout.newTab().setText("Support"))
        tabLayout.tabGravity = TabLayout.GRAVITY_FILL
        //        tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        doctorDetails // commented on 6th jan and put method in onResume()
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
        viewPager = homeView.findViewById(R.id.homeTabPager)
        //        adapter = new Regg
//                (getActivity().getSupportFragmentManager(), tabLayout.getTabCount(), caseChannelPresent);
//        viewPager.setAdapter(adapter);
//        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
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
            if (globalClass!!.isOnline) {
                val intent = Intent(activity, SettingsActivity::class.java)
                startActivity(intent)
            } else {
                globalClass!!.noInternetConnection.showDialog(activity)
            }
        }
        return homeView
    }

    /* @Deprecated("Deprecated in Java")
     override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
         // Do something that differs the Activity's menu here
         inflater.inflate(R.menu.home_menu, menu)
         super.onCreateOptionsMenu(menu, inflater)
     }*/

    private fun setToolBarMenu() {
        requireActivity().addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.home_menu, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                TODO("Not yet implemented")
            }
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            requireActivity().unregisterReceiver(homeBroadCastReceiver)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onResume() {
        super.onResume()
        doctorDetails
    }//AppConfigClass.doctorId = appDoctorManagers.get(0).getDocId();
    //Log.d("Doctor ID", AppConfigClass.doctorId + "");
    //Process os success response
    // doctorDetailsJsonResponse = response.toString();
//                            otpLoading.dismiss();
    //                            settingDocPhone.setText(rootObj.getString("phone"));
//                            settingDocEmail.setText(rootObj.getString("email"));
//                            settingDocExpsettingDocName.setText(rootObj.getString("exp") + " Exp.");
//                            settingDocQua.setText(qualificationsStr);

    // docSpecialisation.setText(specialStr);

    //getting language
    private val doctorDetails: Unit
        get() {
            val URL = ApiUrls.getDoctorDetailsTwo + "?id=" + ApiUrls.doctorId
            globalApiCall!!.volleyApiRequestData(
                URL,
                Request.Method.GET,
                null,
                requireContext(),
                object : VolleyCallback {
                    override fun onSuccess(result: String) {
                        try {
                            val response = JSONObject(result)
                            //Process os success response
                            Log.d("DoctorDetailsResponse", response.toString())
                            // doctorDetailsJsonResponse = response.toString();
//                            otpLoading.dismiss();
                            var rootObj = response.optJSONObject("response")
                            var userArr = rootObj!!.getJSONArray("user")
                            rootObj = userArr.getJSONObject(0)
                            val specializationsArr = rootObj!!.getJSONArray("specializations")
                            var specialStr = ""
                            for (i in 0 until specializationsArr.length()) {
                                val tempobj = specializationsArr.getJSONObject(i)
                                specialStr += tempobj.getString("specialization")
                                if (i != specializationsArr.length() - 1) {
                                    specialStr += " | "
                                }
                            }
                            val qualificationsArr = rootObj.getJSONArray("qualifications")
                            var qualificationsStr = ""
                            for (i in 0 until qualificationsArr.length()) {
                                val tempobj = qualificationsArr.getJSONObject(i)
                                qualificationsStr += tempobj.getString("qualification")
                                if (i != qualificationsArr.length() - 1) {
                                    qualificationsStr += " | "
                                }
                            }
                            homeDocName.text = rootObj.getString("fname")
                            //                            settingDocPhone.setText(rootObj.getString("phone"));
//                            settingDocEmail.setText(rootObj.getString("email"));
//                            settingDocExpsettingDocName.setText(rootObj.getString("exp") + " Exp.");
//                            settingDocQua.setText(qualificationsStr);

                            // docSpecialisation.setText(specialStr);
                            getImageData(rootObj.getString("image_v2"))

                            //getting language
                            userArr = rootObj.getJSONArray("languages")
                            var temp = ""
                            for (i in 0 until userArr.length()) {
                                val obj = userArr.getJSONObject(i)
                                temp = (temp + obj.getString("language").substring(0, 1).uppercase(
                                    Locale.getDefault()
                                )
                                        + obj.getString("language").substring(1) + ":")
                            }
                            val appDoctorManagers = appDatabaseManager!!.doctorData
                            Log.d("Lenght", appDoctorManagers.size.toString() + "")
                            if (appDoctorManagers.size == 0) {
                                val appDoctorManager = AppDoctorManager(
                                    rootObj.getInt("id"),
                                    rootObj.getString("fname"),
                                    rootObj.getString("phone"),
                                    rootObj.getInt("gender"),
                                    rootObj.getString("email"),
                                    rootObj.getString("exp"),
                                    temp
                                )
                                appDatabaseManager!!.addDoctor(appDoctorManager)
                            } else {
                                //AppConfigClass.doctorId = appDoctorManagers.get(0).getDocId();
                                //Log.d("Doctor ID", AppConfigClass.doctorId + "");
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }

                    override fun onError(err: String) {
                        errorHandler(requireContext(), err)
                    }
                })
        }
    val dashBoardQuickLink: Unit
        get() {
            val URL = ApiUrls.getDashBoardDetails + "?mode=" + 0
            globalApiCall!!.volleyApiRequestData(
                URL,
                Request.Method.GET,
                null,
                requireContext(),
                object : VolleyCallback {
                    override fun onSuccess(result: String) {
                        try {
                            val response = JSONObject(result)
                            val menuArr = response.getJSONObject("response").getJSONArray("menu")
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
                            adapter = MainTabPagerAdapter(
                                activity!!.supportFragmentManager,
                                tabLayout.tabCount,
                                caseChannelPresent,
                                ""
                            )
                            viewPager!!.adapter = adapter
                            viewPager!!.addOnPageChangeListener(
                                TabLayoutOnPageChangeListener(
                                    tabLayout
                                )
                            )
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }

                    override fun onError(err: String) {
                        errorHandler(requireContext(), err)
                    }
                })
        }

    fun getImageData(imgURL: String) {
        val queue = Volley.newRequestQueue(activity)

        // Retrieves an image specified by the URL, displays it clinicplus the UI.
        val request = ImageRequest(
            imgURL,
            { bitmap -> homeDocImg.setImageBitmap(bitmap) }, 0, 0, null
        ) {
            homeDocImg.setColorFilter(ContextCompat.getColor(requireContext(), R.color.colorWhite))
            homeDocImg.setImageResource(R.drawable.ic_profile)
        }
        // Access the RequestQueue through your singleton class.
        queue.add(request)
    }

    private val isOnline: Boolean
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
            /*Changed the Frame layout to Floating Action Button to avoid the crash*/
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

    companion object {
        var viewPager: ViewPager? = null

        @JvmField
        var caseChannelPresent = false

        @JvmStatic
        fun newInstance(): HomeFragment {
            return HomeFragment()
        }
    }
}