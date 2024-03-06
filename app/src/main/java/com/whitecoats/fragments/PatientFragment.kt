package com.whitecoats.fragments

import android.app.AlertDialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.graphics.PorterDuff
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.*
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.android.volley.Request
import com.whitecoats.adapter.PatientPListAdapter
import com.whitecoats.broadcast.PatientListBroadcastReceiver
import com.whitecoats.clinicplus.*
import com.whitecoats.clinicplus.apis.ApiGetPostMethodCalls
import com.whitecoats.clinicplus.apis.ApiUrls
import com.whitecoats.clinicplus.fragments.DashboardFullMode
import com.whitecoats.clinicplus.utils.ErrorHandlerClass.errorHandler
import com.whitecoats.clinicplus.utils.SharedPref
import com.whitecoats.model.AppointmentSlotListModel
import com.whitecoats.model.PatientPListModel
import com.zoho.salesiqembed.ZohoSalesIQ
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

class PatientFragment : Fragment() {
    private lateinit var patientPListModelList: MutableList<PatientPListModel>
    lateinit var doctorServiceArrayList: MutableList<AppointmentSlotListModel>
    private lateinit var patientPListAdapter: PatientPListAdapter
    var patientType = "All"
    var consultFilter: String? = "All"
    var apptTypeFilter: String? = "All"
    var activePastFilter: String? = "active"
    private lateinit var emptyText: TextView
    private lateinit var loader: ProgressBar
    private lateinit var emptyViewLayout: LinearLayout
    var pageNumber = 1
    private lateinit var swipeContainer: SwipeRefreshLayout
    private lateinit var patientRegisteredTab: RelativeLayout
    private lateinit var patientRegisteredView: View
    private lateinit var patientInternalView: View
    private lateinit var toolbar: Toolbar
    lateinit var callPhoneNumber: String
    lateinit var doctorsDetailsRootObj: JSONObject
    private var searchQuery = ""
    val CUSTOM_BROADCAST_ACTION = "PatientListBroadcastReceiver"
    private lateinit var patientListBroadcastReceiver: PatientListBroadcastReceiver
    private var mRequestStartTime: Long = 0
    lateinit var broadcastReceiver: BroadcastReceiver
    private lateinit var rl_Search_Text: RelativeLayout
    private lateinit var rl_header: RelativeLayout
    private lateinit var tv_total: TextView
    private lateinit var et_search: EditText
    private lateinit var doctorDetailsReceiver: BroadcastReceiver
    private lateinit var apiGetPostMethodCalls: ApiGetPostMethodCalls
    private var sharedPref: SharedPref? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    companion object {
        var patientTabFlag = 0
        var isMoreData = false
        fun newInstance(): PatientFragment {
            return PatientFragment()
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        // Inflate the layout for this fragment
        val patientView: View = inflater.inflate(R.layout.fragment_patient, container, false)
        //getting the toolbar
        toolbar = patientView.findViewById(R.id.patientToolbar)
        toolbar.title = "My Patient"
        toolbar.subtitle = "Total: 0"
        ConfirmOrderActivity.confirmOrderFlag = 0
        patientTabFlag = 1
        AppointmentFragment.appointmentTabFlag = 0
        ChatFragment.chatTabFlag = 0
        AppointmentFragment.lastHeaderDate = ""
        (requireActivity() as AppCompatActivity).setSupportActionBar(toolbar)
        apiGetPostMethodCalls = ApiGetPostMethodCalls()
        sharedPref = SharedPref(requireContext())
        emptyViewLayout = patientView.findViewById(R.id.appointEmptyViewLayout)
        loader = patientView.findViewById(R.id.appointLoader)
        loader.visibility = View.VISIBLE
        emptyText = patientView.findViewById(R.id.patientListEmptyText)
        patientRegisteredTab = patientView.findViewById(R.id.patientRegisteredTab)
        val patientInternalTab = patientView.findViewById<RelativeLayout>(R.id.patientInternalTab)
        patientRegisteredView = patientView.findViewById(R.id.patientRegisteredView)
        patientInternalView = patientView.findViewById(R.id.patientInternalView)
        swipeContainer = patientView.findViewById(R.id.appointmentSwipeContainer)
        val addPatient = patientView.findViewById<Button>(R.id.patientListAddPatient)
        rl_Search_Text = patientView.findViewById(R.id.rl_Search_Text)
        rl_header = patientView.findViewById(R.id.rl_header)
        tv_total = patientView.findViewById(R.id.tv_total)
        val patientSearch = patientView.findViewById<ImageView>(R.id.patientSearch)
        et_search = patientView.findViewById(R.id.et_search)
        val patientSearch_Clear = patientView.findViewById<ImageView>(R.id.patientSearch_Clear)

        patientSearch.setColorFilter(ContextCompat.getColor(requireContext(), R.color.colorWhite))
        patientSearch_Clear.setColorFilter(
            ContextCompat.getColor(
                requireContext(),
                R.color.colorWhite
            )
        )
        rl_Search_Text.visibility = View.GONE
        rl_header.visibility = View.VISIBLE
        et_search.setText("")

        patientListBroadcastReceiver = PatientListBroadcastReceiver()

        //bottom setting type

        //bottom setting type
        ApiUrls.bottomNaviType = 1
        ZohoSalesIQ.Tracking.setPageTitle("On Patient List Page")

        //registering broadcast

        //registering broadcast
        val filter = IntentFilter()
        filter.addAction(CUSTOM_BROADCAST_ACTION)
        filter.addCategory(Intent.CATEGORY_DEFAULT)
        requireActivity().registerReceiver(patientListBroadcastReceiver, filter)

        val patientRecycleView = patientView.findViewById<RecyclerView>(R.id.patientRecycleView)
        patientPListModelList = ArrayList()
        doctorServiceArrayList = ArrayList()
        enableSearchView(rl_Search_Text, true)

        patientPListAdapter =
            PatientPListAdapter(
                patientPListModelList, doctorServiceArrayList, sharedPref, requireActivity()
            ) {
                    v, loadMore, phoneNumber, videoServiceId,
                    clinicServiceId, instantVideoServiceId,
                    chatServiceId, patientId, patientName,
                    instantVideoObject, instantVideoInfoObject,
                ->
                callPhoneNumber = phoneNumber
                DashboardFullMode.isAppointBookingOnDashBoard = 0
                if (loadMore.equals("LOADMORE")) {
                    pageNumber += 1
                    loader.visibility = View.VISIBLE
                    patientType = if (patientRegisteredTab.getVisibility() == View.VISIBLE) {
                        "All"
                    } else {
                        "Internal"
                    }
                    getPatientList(
                        patientType,
                        "fname",
                        searchQuery,
                        "asc",
                        pageNumber.toString(),
                        "50"
                    )
                } else if (videoServiceId == 1) {
                    val intent = Intent(activity, BookAppointmentTimeSlotActivity::class.java)
                    intent.putExtra("doctorDetailsRootObjects", doctorsDetailsRootObj.toString())
                    intent.putExtra("serviceId", videoServiceId)
                    intent.putExtra("patientId", patientId)
                    intent.putExtra("patientName", patientName)
                    intent.putExtra("appointment_service_id", chatServiceId)
                    requireActivity().startActivity(intent)
                } else if (clinicServiceId == 3) {
                    val intent = Intent(activity, BookAppointmentTimeSlotActivity::class.java)
                    intent.putExtra("doctorDetailsRootObjects", doctorsDetailsRootObj.toString())
                    intent.putExtra("serviceId", clinicServiceId)
                    intent.putExtra("patientId", patientId)
                    intent.putExtra("patientName", patientName)
                    intent.putExtra("appointment_service_id", chatServiceId)
                    requireActivity().startActivity(intent)
                } else if (chatServiceId == 2) {
                    var serviceAlias = ""
                    var prodId = 0
                    var servId = 0
                    var price = 0
                    var serviceAliasName = ""
                    for (i in doctorServiceArrayList.indices) {
                        if (doctorServiceArrayList[i].appointmentServiceID == 2) {
                            serviceAlias = doctorServiceArrayList[i].appointmentServiceAlias
                            prodId = doctorServiceArrayList[i].prodId
                            servId = doctorServiceArrayList[i].servId
                            price = doctorServiceArrayList[i].price
                            serviceAliasName = doctorServiceArrayList[i].prodAliasName
                        }
                    }
                    val c = Calendar.getInstance().time
                    println("Current time => $c")

                    val df = SimpleDateFormat("MMMM d, yyyy", Locale.getDefault())
                    val formattedDate = df.format(c)
                    val intent = Intent(activity, ConfirmOrderActivity::class.java)
                    intent.putExtra("appointment_service_id", chatServiceId)
                    intent.putExtra("date", formattedDate)
                    intent.putExtra("service_name", serviceAlias)
                    intent.putExtra("service_alias_name", serviceAliasName)
                    intent.putExtra("service_price", price)
                    intent.putExtra("service_id", servId)
                    intent.putExtra("prod_id", prodId)
                    intent.putExtra("patientId", patientId)
                    intent.putExtra("patientName", patientName)
                    startActivity(intent)

                } else if (instantVideoObject != null) {
                    try {
                        getOrderDetails(
                            patientId,
                            instantVideoInfoObject.getInt("id"),
                            instantVideoInfoObject,
                            chatServiceId,
                            patientName
                        )
                    } catch (e: java.lang.Exception) {
                        e.printStackTrace()
                    }
                } else {
                    val builder = AlertDialog.Builder(activity)
                    builder.setTitle("Call Confirm")
                    builder.setMessage("Are you sure?")
                    builder.setPositiveButton(
                        "Yes"
                    ) { dialog, _ ->
                        onCall(phoneNumber)
                        dialog.dismiss()
                    }
                    builder.setNegativeButton(
                        "NO"
                    ) { dialog, _ ->
                        dialog.dismiss()
                    }
                    val alert = builder.create()
                    alert.show()
                }

            }
        val mLayoutManager = LinearLayoutManager(activity)
        patientRecycleView.layoutManager = mLayoutManager
        patientRecycleView.itemAnimator = DefaultItemAnimator()
        patientRecycleView.adapter = patientPListAdapter

        swipeContainer.setOnRefreshListener {

            // Your code to refresh the list here.
            // Make sure you call swipeContainer.setRefreshing(false)
            // once the network request has completed successfully.
            patientPListModelList.clear()
            patientType = if (patientRegisteredTab.visibility == View.VISIBLE) {
                "All"
            } else {
                "Internal"
            }
            pageNumber = 1
            enableSearchView(rl_Search_Text, true)
            getDoctorService()
            getPatientList(
                patientType, "fname", searchQuery, "asc",
                "1", "50"
            )

        }
        getDoctorService()
        //        getDoctorsDetails();
        registerReceiverForDoctorDetails()

        getPatientList(
            "All", "fname", searchQuery,
            "asc", "1", "50"
        )


        //on active tab click
        patientRegisteredTab.setOnClickListener {
            patientRegisteredView.visibility = View.VISIBLE
            patientInternalView.visibility = View.INVISIBLE
            patientPListModelList.clear()
            loader.visibility = View.VISIBLE
            pageNumber = 1
            patientType = "All"
            getPatientList(
                patientType, "fname", searchQuery, "asc",
                "1", "50"
            )
        }

        patientInternalTab.setOnClickListener {
            patientRegisteredView.visibility = View.INVISIBLE
            patientInternalView.visibility = View.VISIBLE
            patientPListModelList.clear()
            loader.visibility = View.VISIBLE
            patientType = "Internal"
            pageNumber = 1
            getPatientList(
                patientType, "fname", searchQuery, "asc",
                "1", "50"
            )
        }

        addPatient.setOnClickListener {
            val intent = Intent(activity, BookAppointmentActivity::class.java)
            intent.putExtra("bookAppointment", 0)
            requireActivity().startActivity(intent)
        }
        /*search icon is not displaying in patient tab at top right conner. */
        //Start
        /*search icon is not displaying in patient tab at top right conner. */
        //Start
        patientSearch.setOnClickListener {
            rl_Search_Text.visibility = View.VISIBLE
           /* if (sharedPref!!.getPref("is_show_general_id", "").equals("1", ignoreCase = true)) {
                et_search.hint = getString(R.string.patient_search_text_withID)
            }*/
            rl_header.visibility = View.GONE
        }

        et_search.setOnEditorActionListener { textView: TextView, i: Int, _: KeyEvent? ->
            if (i == EditorInfo.IME_ACTION_SEARCH) {
                val query = textView.text.toString()
                Log.d("Search Submit Query", query)
                MyClinicGlobalClass.logUserActionEvent(
                    ApiUrls.doctorId,
                    getString(R.string.PatientSearch),
                    null
                )
                if (activity != null) {
                    val view = requireActivity().window.currentFocus
                    if (view != null) {
                        val imm =
                            requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                        imm.hideSoftInputFromWindow(view.windowToken, 0)
                    }
                }
                loader.visibility = View.VISIBLE
                patientPListModelList.clear()
                if (patientPListAdapter != null) {
                    patientPListAdapter.notifyDataSetChanged()
                }
                patientType = if (patientRegisteredTab.visibility == View.VISIBLE) {
                    "All"
                } else {
                    "Internal"
                }
                searchQuery = query
                pageNumber = 1
                getPatientList(
                    patientType, "fname", searchQuery, "asc",
                    "1", "50"
                )
            }
            false
        }

        patientSearch_Clear.setOnClickListener { view: View ->
            if (activity != null) {
                val imm =
                    requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(view.windowToken, 0)
            }
            rl_Search_Text.visibility = View.GONE
            rl_header.visibility = View.VISIBLE
            MyClinicGlobalClass.logUserActionEvent(
                ApiUrls.doctorId,
                getString(R.string.PatientSearch),
                null
            )
            if (!et_search.text.toString().equals("", ignoreCase = true)) {
                et_search.setText("")
                patientPListModelList.clear()
                searchQuery = ""
                patientType = if (patientRegisteredTab.visibility == View.VISIBLE) {
                    "All"
                } else {
                    "Internal"
                }
                pageNumber = 1
                getPatientList(
                    patientType,
                    "fname",
                    searchQuery,
                    "asc",
                    "1",
                    "50"
                )
            }
        }

        //End
        /*  broadcastReceiver = object : BroadcastReceiver() {
              override fun onReceive(context: Context, intent: Intent) {
                  if (intent != null && intent.action == "UPDATE_FRAG_LIST") {
                      // here you can fire your action which you want also get data from intent
                      Log.d("onReceviedCallPatient", "onReceviedCallPatient")
                  }
              }
          }
  */

        return patientView
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        // Do something that differs the Activity's menu here
        inflater.inflate(R.menu.patient_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)

        val icon = menu.findItem(R.id.patientMenuSearch).icon
        icon?.setColorFilter(
            ContextCompat.getColor(requireActivity(), R.color.colorWhite),
            PorterDuff.Mode.SRC_IN
        )
        menu.findItem(R.id.patientMenuSearch).icon = icon

        val myActionMenuItem = menu.findItem(R.id.patientMenuSearch)
        val searchView = myActionMenuItem.actionView as SearchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                Log.d("Search Submit Query", query)
                MyClinicGlobalClass.logUserActionEvent(
                    ApiUrls.doctorId,
                    getString(R.string.PatientSearch),
                    null
                )
                if (activity != null) {
                    val view = activity!!.window.currentFocus
                    if (view != null) {
                        val imm =
                            activity!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                        imm.hideSoftInputFromWindow(view.windowToken, 0)
                    }
                }
                loader.visibility = View.VISIBLE
                patientPListModelList.clear()
                if (patientPListAdapter != null) {
                    patientPListAdapter.notifyDataSetChanged()
                }
                patientType = if (patientRegisteredTab.visibility == View.VISIBLE) {
                    "All"
                } else {
                    "Internal"
                }
                searchQuery = query
                pageNumber = 1
                getPatientList(
                    patientType, "fname", searchQuery, "asc",
                    "1", "50"
                )
                return true
            }

            override fun onQueryTextChange(newText: String): Boolean {
                Log.d("Search Query Filter", newText)
                MyClinicGlobalClass.logUserActionEvent(
                    ApiUrls.doctorId,
                    getString(R.string.PatientSearch),
                    null
                )
                if (TextUtils.isEmpty(newText)) {
                    patientPListModelList.clear()
                    searchQuery = ""
                    patientType = if (patientRegisteredTab.visibility == View.VISIBLE) {
                        "All"
                    } else {
                        "Internal"
                    }
                    pageNumber = 1
                    getPatientList(
                        patientType,
                        "fname",
                        searchQuery,
                        "asc",
                        "1",
                        "50"
                    )
                }
                return true
            }
        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        // Respond to the toorbar's NavigationIcon as up/home button
        if (item.itemId == android.R.id.home) { //NavigationIcon
            com.whitecoats.clinicplus.utils.AppUtilities().selectBottomNavigationScreen("Patients")
            // MainActivity.bottomNavigationView.menu.getItem(1).isChecked = true

            //Manually displaying the first fragment - one time only
            if (activity != null) {
                val transaction = requireActivity().supportFragmentManager.beginTransaction()
                transaction.replace(R.id.fragmentContainer, HomeFragment.newInstance())
                transaction.commit()
            }
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray,
    ) {
        if (requestCode == 123) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                onCall(callPhoneNumber)
            } else {
                Log.d("TAG", "Call Permission Not Granted")
            }
        }
    }

    override fun onResume() {
        super.onResume()
        enableSearchView(rl_Search_Text, true)
    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            if (activity != null) {
                requireActivity().unregisterReceiver(patientListBroadcastReceiver)
                requireActivity().unregisterReceiver(doctorDetailsReceiver)
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    override fun setMenuVisibility(isVisibleToUser: Boolean) {
        super.setMenuVisibility(isVisibleToUser)
        if (isVisibleToUser) {
            // do something when visible.
            Log.d("TAG", "setUserVisibleHint:")
            if (DashboardFullMode.isPatientListRefreshReq) {
                patientPListModelList.clear()
                pageNumber = 1
                getPatientList(
                    "All", "fname", searchQuery, "asc",
                    "1", "50"
                )
                DashboardFullMode.isPatientListRefreshReq = false
            }
        }
    }

    private fun onCall(phoneNumber: String) {
        val intent = Intent(Intent.ACTION_DIAL)
        intent.data = Uri.parse("tel:$phoneNumber")
        startActivity(intent)
    }

    fun getPatientList(
        type: String?, sortBy: String?,
        search: String?, orderBy: String?,
        pageNum: String?,
        perPage: String?,
    ) {
        enableSearchView(rl_Search_Text, false)
        val url =
            ApiUrls.getPatientList + "?type=" + type + "&sortby=" + sortBy + "&search=" + search + "&sortorder=" + orderBy +
                    "&page=" + pageNum + "&per_page=" + perPage
        if (swipeContainer.isRefreshing) {
            loader.visibility = View.GONE
        } else {
            loader.visibility = View.VISIBLE
        }
        emptyViewLayout.visibility = View.GONE
        mRequestStartTime = System.currentTimeMillis()
        // prepare the Request
        apiGetPostMethodCalls.volleyApiRequestData(url,
            Request.Method.GET,
            null,
            requireContext(),
            object : VolleyCallback {
                override fun onSuccess(result: String) {
                    // calculate the duration in milliseconds
                    val totalRequestTime = System.currentTimeMillis() - mRequestStartTime
                    Log.d("response time", totalRequestTime.toString() + "")
                    loader.visibility = View.GONE
                    if (swipeContainer.isRefreshing) {
                        swipeContainer.isRefreshing = false
                    }
                    try {
                        val response = JSONObject(result)
                        val rootObj = response.getJSONObject("response")
                        val totalPatientCount = rootObj.getString("total")
                        toolbar.subtitle = "Total: $totalPatientCount"

                        /*search icon is not displaying in patient tab at top right conner. */
                        val totalCount =
                            "Total: $totalPatientCount"
                        tv_total.text = totalCount
                        val patientArray = rootObj.getJSONArray("data")
                        Log.d("PatientListSize", patientArray.length().toString())

                        isMoreData = patientArray.length() >= 50
                        for (j in 0 until patientArray.length()) {
                            val patientObject = patientArray.getJSONObject(j)
                            val intervention = patientObject["patientProfile"]
                            val assignCategory =
                                patientObject.getJSONArray("assignedCategories")
                            val model = PatientPListModel()
                            model.patientId = patientObject.getInt("id")
                            model.roleId = patientObject.getInt("role")
                            model.setPatientName(patientObject.getString("fname").trim())
                            model.patientAge = patientObject.getString("age")
                            model.patientGender = patientObject.getInt("gender")
                            /*New Registration(Autogenerated ID) changes for Gastro interface*/
                            model.generalID = patientObject.getString("general_id")
                            model.assignCategory = assignCategory

                            if (intervention is JSONArray) {
                                // It's an array
                                model.setPhNo(patientObject.getString("phone"))
                            } else if (intervention is JSONObject) {
                                // It's an object
                                val patientProfileObject =
                                    patientObject.getJSONObject("patientProfile")
                                model.setPhNo(patientProfileObject.getString("mobile"))
                                model.setPatientName(patientObject.getString("fullname").trim())
                            }
                            model.isMoreOptionClicked = 0
                            patientPListModelList.add(model)
                        }
                        if (patientPListModelList.size == 0) {
                            emptyViewLayout.visibility = View.VISIBLE
                            if (searchQuery.equals("", ignoreCase = true)) {
                                val addPat = "You have not added any patient till now.\n\n" +
                                        "Tap on Add Patient button to add a new patient."
                                emptyText.text = addPat
                            } else {
                                val noPat =
                                    "No Patient found with name $searchQuery" + "\" \n\n" +
                                            "To add this patient Tap the button below";
                                emptyText.text = noPat
                            }
                            //sending broadcast
                            /*set the package name for broadcast and changes the custom_broadcast_action string value to normal string*/if (activity != null) {
                                val intent = Intent(CUSTOM_BROADCAST_ACTION)
                                intent.putExtra("Activity", "PatientList")
                                intent.putExtra("Param", "NoPatients")
                                intent.setPackage(activity!!.packageName)
                                activity!!.sendBroadcast(intent)
                            }
                        } else {
                            emptyViewLayout.visibility = View.GONE
                        }
                        patientPListAdapter.notifyDataSetChanged()
                        enableSearchView(rl_Search_Text, true)
                    } catch (e: JSONException) {
                        e.printStackTrace()
                        emptyViewLayout.visibility = View.VISIBLE
                    }
                }

                override fun onError(err: String) {
                    try {
                        loader.visibility = View.GONE
                        enableSearchView(rl_Search_Text, true)
                        emptyViewLayout.visibility = View.VISIBLE
                        emptyText.text = resources.getString(R.string.no_patient_found)
                        if (swipeContainer.isRefreshing) {
                            swipeContainer.isRefreshing = false
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                    errorHandler(requireContext(), err)
                }
            })


    }

    fun getOrderDetails(
        patientId: Int,
        orderId: Int,
        instantVideoInfoObject: JSONObject,
        chatServiceId: Int,
        patientName: String?,
    ) {
        val url = ApiUrls.getOrderDetails + "?patientId=" + patientId + "&prodId=" + orderId
        // prepare the Request
        apiGetPostMethodCalls.volleyApiRequestData(url,
            Request.Method.GET,
            null,
            requireContext(),
            object : VolleyCallback {
                override fun onSuccess(result: String) {
                    try {
                        val response = JSONObject(result)
                        val orderDetails = response.getJSONObject("response")
                        val iVSlotStart = orderDetails.getString("IVstart")
                        val iVSlotEnd = orderDetails.getString("IVend")
                        var prodId = 0
                        var servId = 0
                        var price = 0
                        var instantStartTime: String? = ""
                        var instantEndTime: String? = ""
                        var serviceAliasName: String? = ""
                        var serviceName: String? = ""
                        try {
                            instantStartTime = iVSlotStart
                            instantEndTime = iVSlotEnd
                            prodId = instantVideoInfoObject.getInt("id")
                            servId = instantVideoInfoObject.getInt("dr_service_id")
                            price = instantVideoInfoObject.getInt("price")
                            serviceName = instantVideoInfoObject.getString("name")
                            serviceAliasName = instantVideoInfoObject.getString("desc")
                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }
                        val c = Calendar.getInstance().time
                        val df = SimpleDateFormat("MMMM d, yyyy", Locale.getDefault())
                        val formattedDate = df.format(c)
                        val intent = Intent(activity, ConfirmOrderActivity::class.java)
                        intent.putExtra("time_slot", instantStartTime)
                        intent.putExtra("end_time_slot", instantEndTime)
                        intent.putExtra("appointment_service_id", chatServiceId)
                        intent.putExtra("date", formattedDate)
                        intent.putExtra("service_name", serviceName)
                        intent.putExtra("service_alias_name", serviceAliasName)
                        intent.putExtra("service_price", price)
                        intent.putExtra("service_id", servId)
                        intent.putExtra("prod_id", prodId)
                        intent.putExtra("patientId", patientId)
                        intent.putExtra("patientName", patientName)
                        startActivity(intent)
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                }

                override fun onError(err: String) {
                    errorHandler(requireContext(), err)
                }
            })
    }

    fun enableSearchView(view: View, enabled: Boolean) {
        view.isEnabled = enabled
        if (view is ViewGroup) {
            for (i in 0 until view.childCount) {
                val child = view.getChildAt(i)
                enableSearchView(child, enabled)
            }
        }
    }

    fun getDoctorService() {
        val URL = ApiUrls.getDoctorSettings
        apiGetPostMethodCalls.volleyApiRequestData(URL,
            Request.Method.GET,
            null,
            requireActivity(),
            object : VolleyCallback {
                override fun onSuccess(result: String) {
                    try {
                        val response = JSONObject(result)
                        Log.d("serviceDataStatus", "serviceDataStatus $response")
                        val rootObj = response.optJSONObject("response")
                        var clinicServices: JSONArray? = null
                        val serviceStatusList = ArrayList<Int>()
                        if (rootObj != null) {
                            clinicServices = rootObj.getJSONArray("enabledServices")
                            for (i in 0 until clinicServices.length()) {
                                val services =
                                    clinicServices.getJSONObject(i).getInt("service_id")
                                val serviceActive =
                                    clinicServices.getJSONObject(i).getInt("active")
                                if (services == 1) {
                                    ApiUrls.videoServiceStatus = serviceActive
                                } else if (services == 2) {
                                    ApiUrls.chatServiceStatus = serviceActive
                                } else if (services == 3) {
                                    serviceStatusList.add(serviceActive)
                                }
                            }
                            if (serviceStatusList.size > 0) {
                                for (servList in serviceStatusList.indices) {
                                    if (serviceStatusList[servList] == 1) {
                                        ApiUrls.clinicServiceStatus = 1
                                        break
                                    } else {
                                        ApiUrls.clinicServiceStatus = 0
                                    }
                                }
                            }
                        }
                        doctorServiceArrayList.clear()
                        getDoctorsDetails()
                    } catch (e: java.lang.Exception) {
                        e.printStackTrace()
                    }
                }

                override fun onError(err: String) {
                    errorHandler(requireActivity(), err)
                }
            })
    }

    fun getDoctorsDetails() {
        val url = ApiUrls.getDoctorsDetails + "?id=" + ApiUrls.doctorId

        // prepare the Request
        apiGetPostMethodCalls.volleyApiRequestData(url,
            Request.Method.GET,
            null,
            requireContext(),
            object : VolleyCallback {
                override fun onSuccess(result: String) {
                    try {
                        Log.d("doctorService", "doctorService$result")
                        val response = JSONObject(result)
                        doctorsDetailsRootObj = response.getJSONObject("response")
                        val userObject = doctorsDetailsRootObj.getJSONObject("user")
                        val serviceArray = userObject.getJSONArray("services")
                        if (serviceArray.length() != 0) {
                            for (j in 0 until serviceArray.length()) {
                                val serviceObject = serviceArray.getJSONObject(j)
                                val bookAppointmentModel = AppointmentSlotListModel()
                                if (serviceObject.getInt("id") == 2) {
                                    val intervention = doctorsDetailsRootObj["chat_product"]

                                    if (intervention is JSONObject) {
                                        // It's an object
                                        val chatServiceObject =
                                            doctorsDetailsRootObj.getJSONObject("chat_product")
                                        val chatServiceProdObject =
                                            chatServiceObject.getJSONObject("prod_service")
                                        val prodId = chatServiceObject.getInt("id")
                                        val ServId = chatServiceObject.getInt("dr_service_id")
                                        bookAppointmentModel.prodId = prodId
                                        bookAppointmentModel.servId = ServId
                                        bookAppointmentModel.price =
                                            chatServiceObject.getInt("price")
                                        bookAppointmentModel.prodAliasName =
                                            chatServiceProdObject.getString("alias")
                                        bookAppointmentModel.appointmentServiceID =
                                            serviceObject.getInt("id")
                                        bookAppointmentModel.appointmentServiceName =
                                            serviceObject.getString("service")
                                        bookAppointmentModel.appointmentServiceAlias =
                                            serviceObject.getString("alias")
                                        bookAppointmentModel.serviceStatus =
                                            ApiUrls.chatServiceStatus
                                    }
                                } else {
                                    bookAppointmentModel.appointmentServiceID =
                                        serviceObject.getInt("id")
                                    bookAppointmentModel.appointmentServiceName =
                                        serviceObject.getString("service")
                                    bookAppointmentModel.appointmentServiceAlias =
                                        serviceObject.getString("alias")
                                    if (serviceObject.getInt("id") == 1) {
                                        bookAppointmentModel.serviceStatus =
                                            ApiUrls.videoServiceStatus
                                    } else if (serviceObject.getInt("id") == 3) {
                                        bookAppointmentModel.serviceStatus =
                                            ApiUrls.clinicServiceStatus
                                    }
                                }
                                doctorServiceArrayList.add(bookAppointmentModel)
                            }
                            val intervention = doctorsDetailsRootObj["inst_video"]
                            if (intervention is JSONObject) {
                                // It's an object
                                val instantVideoObject =
                                    doctorsDetailsRootObj.getJSONObject("inst_video")
                                val bookAppointmentModel = AppointmentSlotListModel()
                                bookAppointmentModel.instantVideoJsonObject = instantVideoObject
                                doctorServiceArrayList.add(bookAppointmentModel)
                            }
                            val interventionOne = doctorsDetailsRootObj["inst_video_info"]
                            if (interventionOne is JSONObject) {
                                // It's an object
                                val instantVideoInfoObject =
                                    doctorsDetailsRootObj.getJSONObject("inst_video_info")
                                val bookAppointmentModel = AppointmentSlotListModel()
                                bookAppointmentModel.instantVideoInfoJsonObject =
                                    instantVideoInfoObject
                                doctorServiceArrayList.add(bookAppointmentModel)
                            }
                        }
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                }

                override fun onError(err: String) {
                    errorHandler(requireContext(), err)
                }
            })
    }

    private fun registerReceiverForDoctorDetails() {
        doctorDetailsReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                if (intent != null && intent.action == "Update_Doctor_Details_Settings") {
                    doctorServiceArrayList.clear()
                    getDoctorsDetails()
                }
            }
        }
        val filter = IntentFilter()
        filter.addAction("Update_Doctor_Details_Settings")
        requireActivity().registerReceiver(doctorDetailsReceiver, filter)
    }

}