package com.whitecoats.clinicplus

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.*
import android.content.pm.PackageManager
import android.graphics.PorterDuff
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.view.MenuItem
import android.view.View
import android.view.View.OnFocusChangeListener
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.whitecoats.adapter.BookApptListAdapter
import com.whitecoats.broadcast.BookApptBroadcastReceiver
import com.whitecoats.clinicplus.apis.ApiGetPostMethodCalls
import com.whitecoats.clinicplus.apis.ApiUrls
import com.whitecoats.clinicplus.fragments.DashboardFullMode
import com.whitecoats.clinicplus.utils.ErrorHandlerClass.errorHandler
import com.whitecoats.clinicplus.viewmodels.AddPatientViewModel
import com.whitecoats.clinicplus.viewmodels.CommonViewModel
import com.whitecoats.fragments.AppointmentFragment.Companion.appointmentTabFlag
import com.whitecoats.fragments.ChatFragment
import com.whitecoats.fragments.PatientFragment.Companion.patientTabFlag
import com.whitecoats.model.AddPatientModel
import com.whitecoats.model.AppointmentSlotListModel
import com.whitecoats.model.PatientPListModel
import com.zoho.salesiqembed.ZohoSalesIQ
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

class BookAppointmentActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {
    private lateinit var bookApptModelList: MutableList<PatientPListModel>
    private var bookApptListAdapter: BookApptListAdapter? = null
    private lateinit var searchLayout: RelativeLayout
    private lateinit var addPatientLayout: RelativeLayout
    private lateinit var tab1Line: View
    private lateinit var tab2Line: View
    lateinit var addInterfaceModelList: MutableList<AddPatientModel>
    var consultFilter = "All"
    var activePastFilter = "active"
    private lateinit var loader: ProgressBar
    private lateinit var emptyViewLayout: LinearLayout
    private lateinit var searchView: SearchView
    private lateinit var searchPageEmptyText2: TextView
    private lateinit var searchPageEmptyText: TextView
    private lateinit var bookApptSearchCount: TextView
    private lateinit var searchPageAddPatient: Button
    var pageNumber = 1
    private var callPhoneNumber: String? = null
    private lateinit var doctorsDetailsRootObj: JSONObject
    lateinit var doctorServiceArrayList: MutableList<AppointmentSlotListModel>
    private var searchQuery = ""
    val CUSTOM_BROADCAST_ACTION = "BookApptBroadcastReceiver"
    private var bookApptBroadcastReceiver: BookApptBroadcastReceiver? = null
    var globalClass: MyClinicGlobalClass? = null
    private var receiver: BroadcastReceiver? = null
    private var activity: Activity? = null
    private lateinit var searchBtn: ImageButton
    private val isSearchViewEnabled = true
    private lateinit var patientGender: Spinner
    private lateinit var patientType: Spinner
    private lateinit var patient_age_spin: Spinner
    private var patientInterfaceAdapter: ArrayAdapter<AddPatientModel>? = null
    private var addPatientViewModel: AddPatientViewModel? = null
    private var interFaceId = 0
    private lateinit var patientName: EditText
    private lateinit var patientPhoneNumber: EditText
    private lateinit var patientEmailAddress: EditText
    private lateinit var patientAge: EditText
    private lateinit var patientId: EditText
    private val gender = arrayOf("Select Gender", "Male", "Female")
    private val role = arrayOf("Select Patient Type", "Registered", "Internal")
    private val age_type = arrayOf("Years", "Months", "Days")
    private lateinit var toolbar: Toolbar
    private var apiGetPostMethodCalls: ApiGetPostMethodCalls? = null
    private var selectedInterfacePos = 0
    private lateinit var patientTypeText: TextView
    private lateinit var enterIdText: TextView
    private lateinit var patientTypeCard: CardView
    private lateinit var enterIdCard: CardView
    private lateinit var patientCategory: EditText

    private lateinit var dialog:Dialog
    private lateinit var commonViewModel: CommonViewModel
    private lateinit var selectInterfaceDownArrow: ImageView
    private lateinit var enterInterfaceText: TextView
    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_book_appointment)
        toolbar = findViewById(R.id.bookApptToolbar)
        setScreenTitle("Select Patient")
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        commonViewModel=ViewModelProvider(this)[CommonViewModel::class.java]
        val upArrow = ContextCompat.getDrawable(this, R.drawable.ic_arrow_back)
        if (upArrow != null) {
            upArrow.setColorFilter(ContextCompat.getColor(this,R.color.colorWhite), PorterDuff.Mode.SRC_ATOP)
            supportActionBar!!.setHomeAsUpIndicator(upArrow)
        }
        activity = this
        val patientListRView = findViewById<RecyclerView>(R.id.bookApptRecycleView)
        val searchTab = findViewById<RelativeLayout>(R.id.bookApptSearchTab)
        val addPatientTab = findViewById<RelativeLayout>(R.id.bookApptAddPatientTab)
        searchLayout = findViewById(R.id.bookApptSearchLayout)
        addPatientLayout = findViewById(R.id.bookApptAddPatientLayout)
        searchPageEmptyText2 = findViewById(R.id.searchPageEmptyText2)
        searchPageEmptyText = findViewById(R.id.searchPageEmptyText)
        searchPageAddPatient = findViewById(R.id.searchPageAddPatient)
        bookApptSearchCount = findViewById(R.id.bookApptSearchCount)
        emptyViewLayout = findViewById(R.id.appointEmptyViewLayout)
        searchBtn = findViewById(R.id.searchPatientSearchBtn)
        loader = findViewById(R.id.appointLoader)
        searchView = findViewById(R.id.searchPatientEditText)
        patientTypeText = findViewById(R.id.patient_type_text)
        enterIdText = findViewById(R.id.enter_id_text)
        patientTypeCard = findViewById(R.id.patient_type_card)
        enterIdCard = findViewById(R.id.enter_id_card)
        patientCategory = findViewById(R.id.patient_category)
        addPatientViewModel = ViewModelProvider(this@BookAppointmentActivity)[AddPatientViewModel::class.java]
        val intent1 = intent
        apiGetPostMethodCalls = ApiGetPostMethodCalls()
        ApiUrls.bottomNaviType = 0
        addPatientViewModel!!.init()
        patientGender = findViewById(R.id.patient_gender)
        patientType = findViewById(R.id.patient_type)
        val patientInterface = findViewById<Spinner>(R.id.patient_interface)
        val savePatient = findViewById<Button>(R.id.save_patient)
        patientName = findViewById(R.id.patient_name)
        patientPhoneNumber = findViewById(R.id.patient_contact)
        patientEmailAddress = findViewById(R.id.patient_email)
        patientAge = findViewById(R.id.patient_age)
        patientId = findViewById(R.id.patient_id)

        selectInterfaceDownArrow = findViewById(R.id.selectInterfaceDownArrow)
        enterInterfaceText = findViewById(R.id.enter_interface_text)

        if (!TextUtils.isEmpty(intent1.getStringExtra("name"))) {
            val value = intent1.getStringExtra("name")
            if (isNumeric(value)) {
                patientPhoneNumber.setText(value)
            } else {
                patientName.setText(value)
            }
        }
        patient_age_spin = findViewById(R.id.patient_age_spin)
        globalClass = applicationContext as MyClinicGlobalClass
        doctorServiceArrayList = ArrayList()
        tab1Line = findViewById(R.id.bookApptTab1line)
        tab2Line = findViewById(R.id.bookApptTab2line)
        registerReceiverForDoctorDetails()
        bookApptModelList = ArrayList()
        bookApptBroadcastReceiver = BookApptBroadcastReceiver()
        enableSearchView(searchView, true)
        bookApptListAdapter = BookApptListAdapter(
            bookApptModelList,
            doctorServiceArrayList,
            this
        ) { _: View?, loadMore: String, phoneNumber: String?, videoServiceId: Int, clinicServiceId: Int, _: Int, chatServiceId: Int, patientId: Int, patientName: String?, instantVideoObject: JSONObject?, instantVideoInfoObject: JSONObject? ->
            if (loadMore.equals("LOADMORE", ignoreCase = true)) {
                pageNumber += 1
                loader.visibility = View.VISIBLE
                getPatientList(
                    "All",
                    "fname",
                    searchQuery,
                    "asc",
                    pageNumber.toString() + "",
                    "50"
                )
            } else if (videoServiceId == 1) {
                quickAppointmentFlag = 1
                DashboardFullMode.isAppointBookingOnDashBoard = 0
                val intent = Intent(activity, BookAppointmentTimeSlotActivity::class.java)
                intent.putExtra("doctorDetailsRootObjects", doctorsDetailsRootObj.toString())
                intent.putExtra("serviceId", videoServiceId)
                intent.putExtra("patientId", patientId)
                intent.putExtra("patientName", patientName)
                intent.putExtra("quickAppointmentFlag", quickAppointmentFlag)
                startActivity(intent)
            } else if (clinicServiceId == 3) {
                quickAppointmentFlag = 1
                DashboardFullMode.isAppointBookingOnDashBoard = 0
                val intent =
                    Intent(applicationContext, BookAppointmentTimeSlotActivity::class.java)
                intent.putExtra("doctorDetailsRootObjects", doctorsDetailsRootObj.toString())
                intent.putExtra("serviceId", clinicServiceId)
                intent.putExtra("patientId", patientId)
                intent.putExtra("patientName", patientName)
                intent.putExtra("quickAppointmentFlag", quickAppointmentFlag)
                if (getIntent().getStringExtra("followUpApptId") != null) {
                    intent.putExtra(
                        "followUpApptId",
                        getIntent().getStringExtra("followUpApptId")
                    )
                }
                startActivity(intent)
            } else if (chatServiceId == 2) {
                var serviceAlias: String? = ""
                var prodId = 0
                var servId = 0
                var price = 0
                var serviceAliasName: String? = ""
                quickAppointmentFlag = 1
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
                intent.putExtra("quickAppointmentFlag", quickAppointmentFlag)
                startActivity(intent)
            } else if (instantVideoObject != null) {
                try {
                    getOrderDetails(
                        patientId,
                        instantVideoInfoObject!!.getInt("id"),
                        instantVideoInfoObject,
                        chatServiceId,
                        patientName
                    )
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            } else {
                val builder = AlertDialog.Builder(activity)
                builder.setTitle("Call Confirm")
                builder.setMessage("Are you sure?")
                builder.setPositiveButton("YES") { dialog: DialogInterface, _: Int ->
                    onCall(phoneNumber)
                    dialog.dismiss()
                }
                builder.setNegativeButton("NO") { dialog: DialogInterface, _: Int -> dialog.dismiss() }
                val alert = builder.create()
                alert.show()
            }
        }
        val mLayoutManager: RecyclerView.LayoutManager = LinearLayoutManager(applicationContext)
        patientListRView.layoutManager = mLayoutManager
        patientListRView.itemAnimator = DefaultItemAnimator()
        patientListRView.adapter = bookApptListAdapter
        val filter = IntentFilter()
        filter.addAction(CUSTOM_BROADCAST_ACTION)
        filter.addCategory(Intent.CATEGORY_DEFAULT)
        registerReceiver(bookApptBroadcastReceiver, filter)
        if (intent.getIntExtra("bookAppointment", 0) == 1) {
            setScreenTitle("Select Patient")
            searchLayout.visibility = View.VISIBLE
            addPatientLayout.visibility = View.GONE
            tab1Line.visibility = View.VISIBLE
            tab2Line.visibility = View.GONE
            searchView.setQuery(intent.getStringExtra("patient name"), true)
            ZohoSalesIQ.Tracking.setPageTitle("Book Appt Search Patient Page")
            //sending broadcast
            val intent = Intent(CUSTOM_BROADCAST_ACTION)
            intent.putExtra("Activity", "BookAppt")
            /*set the package name for broadcast and changes the custom_broadcast_action string value to normal string*/intent.setPackage(
                packageName
            )
            sendBroadcast(intent)
        } else {
            setScreenTitle("Add New Patient")
            searchLayout.visibility = View.GONE
            addPatientLayout.visibility = View.VISIBLE
            tab1Line.visibility = View.GONE
            tab2Line.visibility = View.VISIBLE
            ZohoSalesIQ.Tracking.setPageTitle("Add Patient Page")
            //sending broadcast
            val intent = Intent(CUSTOM_BROADCAST_ACTION)
            intent.putExtra("Activity", "AddPatient")
            /*set the package name for broadcast and changes the custom_broadcast_action string value to normal string*/intent.setPackage(
                packageName
            )
            sendBroadcast(intent)
        }
        if (intent.getStringExtra("searchParameter") != null) {
            searchPageEmptyText2.visibility = View.GONE
            searchPageEmptyText.visibility = View.GONE
            searchPageAddPatient.visibility = View.GONE
            bookApptSearchCount.visibility = View.VISIBLE
            val searchText = intent.getStringExtra("searchParameter")
            searchView.setQuery(searchText, true)
            val view = window.currentFocus
            if (view != null) {
                val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(view.windowToken, 0)
            }
            bookApptModelList.clear()
            if (bookApptListAdapter != null) {
                bookApptListAdapter!!.notifyDataSetChanged()
            }
            getPatientList("All", "fname", searchQuery, "asc", 1.toString() + "", "50")
        }
        searchTab.setOnClickListener {
            searchLayout.visibility = View.VISIBLE
            setScreenTitle("Select Patient")
            addPatientLayout.visibility = View.GONE
            tab1Line.visibility = View.VISIBLE
            tab2Line.visibility = View.GONE
            ZohoSalesIQ.Tracking.setPageTitle("Book Appt Search Patient Page")
        }
        addPatientTab.setOnClickListener {
            searchLayout.visibility = View.GONE
            setScreenTitle("Add New Patient")
            addPatientLayout.visibility = View.VISIBLE
            tab1Line.visibility = View.GONE
            tab2Line.visibility = View.VISIBLE
            ZohoSalesIQ.Tracking.setPageTitle("Add Patient Page")
        }
        searchBtn.setOnClickListener {
            ZohoSalesIQ.Tracking.setCustomAction("BookAppt - Search Patient Query")
            if (!searchView.query.toString().equals("", ignoreCase = true)) {
                searchPageEmptyText2.visibility = View.GONE
                searchPageEmptyText.visibility = View.GONE
                searchPageAddPatient.visibility = View.GONE
                bookApptSearchCount.visibility = View.VISIBLE
                val view = window.currentFocus
                if (view != null) {
                    val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(view.windowToken, 0)
                }
                bookApptModelList.clear()
                if (bookApptListAdapter != null) {
                    bookApptListAdapter!!.notifyDataSetChanged()
                }
                pageNumber = 1
                searchQuery = searchView.query.toString()
                getPatientList("All", "fname", searchQuery, "asc", 1.toString() + "", "50")
            } else {
                Toast.makeText(activity, "Please Enter Patient Name/Number", Toast.LENGTH_SHORT)
                    .show()
            }
        }
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                if (!query.equals("", ignoreCase = true)) {
                    searchPageEmptyText2.visibility = View.GONE
                    searchPageEmptyText.visibility = View.GONE
                    searchPageAddPatient.visibility = View.GONE
                    bookApptSearchCount.visibility = View.VISIBLE
                    val view = window.currentFocus
                    if (view != null) {
                        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                        imm.hideSoftInputFromWindow(view.windowToken, 0)
                    }
                    bookApptModelList.clear()
                    if (bookApptListAdapter != null) {
                        bookApptListAdapter!!.notifyDataSetChanged()
                    }
                    searchQuery = query
                    pageNumber = 1
                    getPatientList("All", "fname", searchQuery, "asc", 1.toString() + "", "50")
                } else {
                    Toast.makeText(activity, "Please Enter Patient Name/Number", Toast.LENGTH_SHORT)
                        .show()
                }
                return true
            }

            override fun onQueryTextChange(newText: String): Boolean {
                if (isSearchViewEnabled) {
                    if (TextUtils.isEmpty(newText)) {
                        searchPageEmptyText2.visibility = View.VISIBLE
                        searchPageEmptyText.visibility = View.VISIBLE
                        searchPageAddPatient.visibility = View.VISIBLE
                        bookApptSearchCount.visibility = View.GONE
                        emptyViewLayout.visibility = View.GONE
                        searchPageEmptyText2.text = resources.getString(R.string.to_book_appt)
                        searchPageEmptyText.text = resources.getString(R.string.book_search_empty_text)
                        bookApptModelList.clear()
                        if (bookApptListAdapter != null) {
                            bookApptListAdapter!!.notifyDataSetChanged()
                        }
                    }
                }
                return true
            }
        })
        searchPageAddPatient.setOnClickListener {
            setScreenTitle("Add New Patient")
            searchLayout.visibility = View.GONE
            addPatientLayout.visibility = View.VISIBLE
            tab1Line.visibility = View.GONE
            tab2Line.visibility = View.VISIBLE
            ZohoSalesIQ.Tracking.setPageTitle("Add Patient Page")
        }
        addInterfaceModelList = ArrayList()
        val patientGenderAdapter =
            ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, gender)
        patientGenderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        patientGender.adapter = patientGenderAdapter
        val patientAgeTypeAdapter =
            ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, age_type)
        patientAgeTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        patient_age_spin.adapter = patientAgeTypeAdapter
        patient_age_spin.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?, view: View, position: Int, id: Long
            ) {
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
        val patientTypeAdapter =
            ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, role)
        patientTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        patientType.adapter = patientTypeAdapter
        patientInterfaceAdapter = ArrayAdapter(
            applicationContext,
            android.R.layout.simple_spinner_dropdown_item,
            addInterfaceModelList
        )
        patientInterfaceAdapter!!.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        patientInterface.adapter = patientInterfaceAdapter
        patientInterface.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View,
                position: Int,
                id: Long
            ) {
                selectedInterfacePos = position


                if (addInterfaceModelList[position].isAutoGenrateGeneralId.equals(
                        "0",
                        ignoreCase = true
                    )
                ) {
                    enterIdText.visibility = View.VISIBLE
                    enterIdCard.visibility = View.VISIBLE
                } else {
                    enterIdText.visibility = View.GONE
                    enterIdCard.visibility = View.GONE
                }
                if (addInterfaceModelList[position].isAutoRegistered.equals(
                        "0",
                        ignoreCase = true
                    )
                ) {
                    patientTypeText.visibility = View.VISIBLE
                    patientTypeCard.visibility = View.VISIBLE
                } else {
                    patientTypeText.visibility = View.GONE
                    patientTypeCard.visibility = View.GONE
                }


                interFaceId = addInterfaceModelList[position].interfaceId
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
        savePatient.setOnClickListener {
            if (patientName.text.toString().isEmpty()) {
                patientName.error = "Name is required"
            } else if (patientPhoneNumber.text.toString().isEmpty()) {
                patientPhoneNumber.error = "Phone number is required"
            } else if (patientPhoneNumber.text.toString().length > 10) {
                patientPhoneNumber.error = "Please enter valid contact number"
            } else if (patientPhoneNumber.text.toString().length < 10) {
                patientPhoneNumber.error = "Please enter valid contact number"
            } else if ((patientAge.text != null) and Objects.requireNonNull(patientAge.text)
                    .toString().isNotEmpty()
            ) {
                val parseVale = patientAge.text.toString().toDouble()
                if (patient_age_spin.selectedItem.toString()
                        .equals("Years", ignoreCase = true)
                ) {
                    if (parseVale > 100.0) {
                        Toast.makeText(
                            applicationContext,
                            "Maximum value that can be entered is 100 years",
                            Toast.LENGTH_LONG
                        ).show()
                    } else {
                        if (globalClass!!.isOnline) {
                            ZohoSalesIQ.Tracking.setCustomAction("Dashboard - Add Patient")
                            savePatientDetails()
                        } else {
                            globalClass!!.noInternetConnection.showDialog(this@BookAppointmentActivity)
                        }
                    }
                } else if (patient_age_spin.selectedItem.toString()
                        .equals("Months", ignoreCase = true)
                ) {
                    if (parseVale > 1200.0) {
                        Toast.makeText(
                            applicationContext,
                            "Maximum value that can be entered is 1200 months",
                            Toast.LENGTH_LONG
                        ).show()
                    } else {
                        if (globalClass!!.isOnline) {
                            ZohoSalesIQ.Tracking.setCustomAction("Dashboard - Add Patient")
                            savePatientDetails()
                        } else {
                            globalClass!!.noInternetConnection.showDialog(this@BookAppointmentActivity)
                        }
                    }
                } else if (patient_age_spin.selectedItem.toString()
                        .equals("Days", ignoreCase = true)
                ) {
                    if (parseVale > 36500.0) {
                        Toast.makeText(
                            applicationContext,
                            "Maximum value that can be entered is 36500 days",
                            Toast.LENGTH_LONG
                        ).show()
                    } else {
                        if (globalClass!!.isOnline) {
                            ZohoSalesIQ.Tracking.setCustomAction("Dashboard - Add Patient")
                            savePatientDetails()
                        } else {
                            globalClass!!.noInternetConnection.showDialog(this@BookAppointmentActivity)
                        }
                    }
                }
            } else {
                if (globalClass!!.isOnline) {
                    ZohoSalesIQ.Tracking.setCustomAction("Dashboard - Add Patient")
                    savePatientDetails()
                } else {
                    globalClass!!.noInternetConnection.showDialog(this@BookAppointmentActivity)
                }
            }
        }
        addPatientViewModel!!.getInterfaceDetails(this@BookAppointmentActivity)
            .observe(this) { s: String? ->
                try {
                    val response = JSONObject(s!!).getJSONObject("response")
                    val rootObj = response.getJSONArray("response")
                    for (j in 0 until rootObj.length()) {
                        val appointmentJsonObject = rootObj.getJSONObject(j)
                        if (!appointmentJsonObject.isNull("parentinterf")) {
                            val parentInterfaceObject =
                                appointmentJsonObject.getJSONObject("parentinterf")
                            val model = AddPatientModel()
                            model.interfaceId = parentInterfaceObject.getInt("id")
                            model.interfaceName = parentInterfaceObject.getString("interface_name")

                            model.isAutoGenrateGeneralId =
                                    parentInterfaceObject.getString("is_auto_genrate_general_id")
                                model.isAutoRegistered =
                                    parentInterfaceObject.getString("is_auto_registered")
                            addInterfaceModelList.add(model)
                        }
                    }
                    /*Android : Add patient screen changes*/
                    if (addInterfaceModelList!!.size > 1) {
                        enterInterfaceText.text = getString(R.string.select_patient_interface)
                        selectInterfaceDownArrow.visibility = View.VISIBLE
                        patientInterface.isClickable = true
                        patientInterface.isEnabled = true
                    } else {
                        enterInterfaceText.text = "Patient Interface"
                        selectInterfaceDownArrow.visibility = View.GONE
                        patientInterface.isClickable = false
                        patientInterface.isEnabled = false
                    }
                    patientInterfaceAdapter!!.notifyDataSetChanged()
                    doctorsDetails
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }
        patientName.onFocusChangeListener = OnFocusChangeListener { _: View?, _: Boolean ->
            MyClinicGlobalClass.logUserActionEvent(
                ApiUrls.doctorId, getString(R.string.AddNewPatientPatientName),
                null
            )
        }
        patientPhoneNumber.onFocusChangeListener = OnFocusChangeListener { _: View?, _: Boolean ->
            MyClinicGlobalClass.logUserActionEvent(
                ApiUrls.doctorId, getString(R.string.AddNewPatientPatientContact),
                null
            )
        }
        patientEmailAddress.onFocusChangeListener = OnFocusChangeListener { _: View?, _: Boolean ->
            MyClinicGlobalClass.logUserActionEvent(
                ApiUrls.doctorId, getString(R.string.AddNewPatientPatientEmail),
                null
            )
        }
        patientAge.onFocusChangeListener = OnFocusChangeListener { _: View?, _: Boolean ->
            MyClinicGlobalClass.logUserActionEvent(
                ApiUrls.doctorId, getString(R.string.AddNewPatientPatientAge),
                null
            )
        }
        patientId.onFocusChangeListener = OnFocusChangeListener { _: View?, _: Boolean ->
            MyClinicGlobalClass.logUserActionEvent(
                ApiUrls.doctorId, getString(R.string.AddNewPatientPatientID),
                null
            )
        }
        patientGender.onFocusChangeListener = OnFocusChangeListener { _: View?, _: Boolean ->
            MyClinicGlobalClass.logUserActionEvent(
                ApiUrls.doctorId, getString(R.string.AddNewPatientPatientID),
                null
            )
        }
        patientInterface.onFocusChangeListener =
            OnFocusChangeListener { _: View?, _: Boolean ->
                MyClinicGlobalClass.logUserActionEvent(
                    ApiUrls.doctorId, getString(R.string.AddNewPatientPatientInterface),
                    null
                )
            }
        patientType.onFocusChangeListener = OnFocusChangeListener { _: View?, _: Boolean ->
            MyClinicGlobalClass.logUserActionEvent(
                ApiUrls.doctorId, getString(R.string.AddNewPatientPatientType),
                null
            )
        }
        MyClinicGlobalClass.logUserActionEvent(
            ApiUrls.doctorId,
            getString(R.string.AppointmentsOpenTabBookAppointment),
            null
        )
    }

    //Performing action onItemSelected and onNothing selected
    override fun onItemSelected(arg0: AdapterView<*>?, arg1: View, position: Int, id: Long) {
        // TODO Auto-generated method stub
    }

    override fun onNothingSelected(arg0: AdapterView<*>?) {
        // TODO Auto-generated method stub
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // handle arrow click here
        if (item.itemId == android.R.id.home) {
            finish()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun isNumeric(value: String?): Boolean {
        return try {
            value!!.toLong()
            true
        } catch (e: Exception) {
            false
        }
    }

    fun getPatientList(
        type: String, sortBy: String, search: String, orderBy: String, pageNum: String,
        perPage: String
    ) {
        enableSearchView(searchView, false)
        val url =
            ApiUrls.getPatientList + "?type=" + type + "&sortby=" + sortBy + "&search=" + search + "&sortorder=" + orderBy +
                    "&page=" + pageNum + "&per_page=" + perPage
        loader.visibility = View.VISIBLE
        emptyViewLayout.visibility = View.GONE

        // prepare the Request
        apiGetPostMethodCalls!!.volleyApiRequestData(
            url,
            Request.Method.GET,
            null,
            activity,
            object : VolleyCallback {
                @SuppressLint("NotifyDataSetChanged")
                override fun onSuccess(result: String) {
                    loader.visibility = View.GONE
                    try {
                        val response = JSONObject(result)
                        val rootObj = response.getJSONObject("response")
                        val totalPatientCount = rootObj.getString("total")
                        val res = "$totalPatientCount Result Found"
                        bookApptSearchCount.text = res
                        val patientArray = rootObj.getJSONArray("data")
                        isMoreData = patientArray.length() >= 50
                        for (j in 0 until patientArray.length()) {
                            val patientObject = patientArray.getJSONObject(j)
                            val intervention = patientObject["patientProfile"]
                            val assignCategory = patientObject.getJSONArray("assignedCategories")
                            val model = PatientPListModel()
                            model.patientId = patientObject.getInt("id")
                            model.roleId = patientObject.getInt("role")
                            model.setPatientName(
                                patientObject.getString("fname").trim { it <= ' ' })


                            /*New Registration(Autogenerated ID) changes for Gastro interface*/model.setGeneralID(
                                patientObject.getString("general_id")
                            )


                            model.patientAge = patientObject.getString("age")
                            model.patientGender = patientObject.getInt("gender")
                            model.assignCategory = assignCategory
                            if (intervention is JSONArray) {
                                // It's an array
                                model.setPhNo(patientObject.getString("phone"))
                            } else if (intervention is JSONObject) {
                                // It's an object
                                val patientProfileObject =
                                    patientObject.getJSONObject("patientProfile")
                                model.setPhNo(patientProfileObject.getString("mobile"))
                            }
                            bookApptModelList.add(model)
                        }
                        bookApptListAdapter!!.notifyDataSetChanged()
                        if (bookApptModelList.size == 0) {
                            searchPageEmptyText2.visibility = View.VISIBLE
                            searchPageEmptyText.visibility = View.VISIBLE
                            searchPageAddPatient.visibility = View.VISIBLE
                            searchPageEmptyText2.setText(R.string.No_Patient_Found)
                            val noPatentWithNameFound = """No Patient found with name "$search" 

To add this patient Tap the button below"""
                            searchPageEmptyText.text = noPatentWithNameFound
                        }
                        enableSearchView(searchView, true)
                    } catch (e: JSONException) {
                        e.printStackTrace()
                        enableSearchView(searchView, true)
                        emptyViewLayout.visibility = View.VISIBLE
                        bookApptSearchCount.setText(R.string.Zero_Results)
                    }
                }

                override fun onError(err: String) {
                    loader.visibility = View.GONE
                    emptyViewLayout.visibility = View.VISIBLE
                    bookApptSearchCount.setText(R.string.Zero_Results)
                    errorHandler(activity!!, err)
                }
            })
    }

    private fun onCall(phoneNumber: String?) {
        val intent = Intent(Intent.ACTION_DIAL)
        intent.data = Uri.parse("tel:$phoneNumber")
        startActivity(intent)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 123) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                onCall(callPhoneNumber)
            }
        }
    }// It's an object// It's an object// It's an object

    // prepare the Request
    val doctorsDetails: Unit
        get() {
            val url = ApiUrls.getDoctorsDetails + "?id=" + ApiUrls.doctorId
            // prepare the Request
            apiGetPostMethodCalls!!.volleyApiRequestData(
                url,
                Request.Method.GET,
                null,
                activity,
                object : VolleyCallback {
                    override fun onSuccess(result: String) {
                        try {
                            val response = JSONObject(result)
                            doctorsDetailsRootObj = response.getJSONObject("response")
                            val userObject = doctorsDetailsRootObj.getJSONObject("user")
                            val serviceArray = userObject.getJSONArray("services")
                            if (serviceArray.length() > 0) {
                                for (j in 0 until serviceArray.length()) {
                                    val serviceObject = serviceArray.getJSONObject(j)
                                    val bookAppointmentModel = AppointmentSlotListModel()
                                    if (serviceObject.getInt("id") == 2) {
                                        val intervention = doctorsDetailsRootObj.get("chat_product")
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
                                        }
                                    } else {
                                        bookAppointmentModel.appointmentServiceID =
                                            serviceObject.getInt("id")
                                        bookAppointmentModel.appointmentServiceName =
                                            serviceObject.getString("service")
                                        bookAppointmentModel.appointmentServiceAlias =
                                            serviceObject.getString("alias")
                                    }
                                    doctorServiceArrayList.add(bookAppointmentModel)
                                }
                                val intervention = doctorsDetailsRootObj.get("inst_video")
                                if (intervention is JSONObject) {
                                    // It's an object
                                    val instantVideoObject =
                                        doctorsDetailsRootObj.getJSONObject("inst_video")
                                    val bookAppointmentModel = AppointmentSlotListModel()
                                    bookAppointmentModel.instantVideoJsonObject = instantVideoObject
                                    doctorServiceArrayList.add(bookAppointmentModel)
                                }
                                val interventionOne = doctorsDetailsRootObj.get("inst_video_info")
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
                        errorHandler(activity!!, err)
                    }
                })
        }

    private fun getOrderDetails(
        patientId: Int,
        orderId: Int,
        instantVideoInfoObject: JSONObject,
        chatServiceId: Int,
        patientName: String?
    ) {
        val url = ApiUrls.getOrderDetails + "?patientId=" + patientId + "&prodId=" + orderId
        // prepare the Request
        apiGetPostMethodCalls!!.volleyApiRequestData(
            url,
            Request.Method.GET,
            null,
            activity,
            object : VolleyCallback {
                override fun onSuccess(result: String) {
                    try {
                        val response = JSONObject(result)
                        val orderDetails = response.getJSONObject("response")
                        val iVSlotStart = orderDetails.getString("IVstart")
                        val iVSlotEnd = orderDetails.getString("IVend")
                        val instantStartTime: String = iVSlotStart
                        val instantEndTime: String = iVSlotEnd
                        val prodId: Int = instantVideoInfoObject.getInt("id")
                        val servId: Int = instantVideoInfoObject.getInt("dr_service_id")
                        val price: Int = instantVideoInfoObject.getInt("price")
                        val serviceName: String = instantVideoInfoObject.getString("name")
                        val serviceAliasName: String = instantVideoInfoObject.getString("desc")
                        val c = Calendar.getInstance().time
                        println("Current time => $c")
                        val df = SimpleDateFormat("MMMM d, yyyy", Locale.getDefault())
                        val formattedDate = df.format(c)
                        val intent = Intent(applicationContext, ConfirmOrderActivity::class.java)
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
                        intent.putExtra("quickAppointmentFlag", 1)
                        startActivity(intent)
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                }

                override fun onError(err: String) {
                    errorHandler(activity!!, err)
                }
            })
    }

    override fun onResume() {
        super.onResume()
        enableSearchView(searchView, true)
        try {
            if (quickAppointmentFlag == 2) {
                quickAppointmentFlag = 0
                ConfirmOrderActivity.confirmOrderFlag = 0
                finish()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        searchView.clearFocus()
    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            unregisterReceiver(bookApptBroadcastReceiver)
            unregisterReceiver(receiver)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun registerReceiverForDoctorDetails() {
        receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                if (intent.action == "Call_Doctor_Details_API") {
                    doctorsDetails
                }
            }
        }
        val filter = IntentFilter()
        filter.addAction("Call_Doctor_Details_API")
        registerReceiver(receiver, filter)
    }

    private fun enableSearchView(view: View?, enabled: Boolean) {
        searchBtn.isClickable = enabled
        searchBtn.isEnabled = enabled
        view!!.isEnabled = enabled
        if (view is ViewGroup) {
            for (i in 0 until view.childCount) {
                val child = view.getChildAt(i)
                enableSearchView(child, enabled)
            }
        }
    }

    private fun savePatientDetails() {
        showCustomProgressAlertDialog(resources.getString(R.string.updating),resources.getString(R.string.wait_while_we_updating))
        var jsonValue = JSONObject()
        try {
            jsonValue = JSONObject()
            jsonValue.put("name", patientName.text.toString().trim { it <= ' ' })
            jsonValue.put("phone", patientPhoneNumber.text.toString().trim { it <= ' ' })
            jsonValue.put("age", patientAge.text.toString().trim { it <= ' ' })
            jsonValue.put("age_type", patient_age_spin.selectedItem.toString())
            jsonValue.put("email", patientEmailAddress.text.toString().trim { it <= ' ' })
            jsonValue.put("gender", patientGender.selectedItemPosition)
            jsonValue.put("interface", interFaceId)

            jsonValue.put("category", patientCategory.text.toString())
            if (addInterfaceModelList[selectedInterfacePos].isAutoGenrateGeneralId.equals(
                    "0",
                    ignoreCase = true
                )
            ) {
                jsonValue.put("generalid", patientId.text.toString())
            }
            if (addInterfaceModelList[selectedInterfacePos].isAutoRegistered.equals(
                    "0",
                    ignoreCase = true
                )
            ) {
                jsonValue.put("type", patientType.selectedItem.toString())
            }


        } catch (e: Exception) {
            e.printStackTrace()
        }
        addPatientViewModel!!.savePatient(this@BookAppointmentActivity, jsonValue)
            .observe(this@BookAppointmentActivity) { s: String? ->
                dialog.dismiss()
                try {
                    val jsonObject = JSONObject(s!!)
                    if (jsonObject.getInt("status_code") == 200) {
                        MyClinicGlobalClass.logUserActionEvent(
                            ApiUrls.doctorId,
                            getString(R.string.AddNewPatientSavePatient),
                            null
                        )
                        Toast.makeText(
                            this@BookAppointmentActivity,
                            "Patient add successfully",
                            Toast.LENGTH_SHORT
                        ).show()
                        patientName.setText("")
                        patientPhoneNumber.setText("")
                        patientAge.setText("")
                        patientId.setText("")
                        patientEmailAddress.setText("")
                            patientCategory.setText("")
                        patientTabFlag = 1
                        appointmentTabFlag = 0
                        ChatFragment.chatTabFlag = 0
                        ApiUrls.bottomNaviType = 1
                        finish()
                        val intent = Intent()
                        // Here you can also put data on intent
                        intent.action = "PATIENT_LIST_REFRESH"
                        sendBroadcast(intent)
                    } else {
                        errorHandler(this@BookAppointmentActivity, s)
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }
    }

    private fun setScreenTitle(title: String) {
        toolbar.title = title
    }
    private fun showCustomProgressAlertDialog(title: String, progressVal: String) {
        val builder = AlertDialog.Builder(this@BookAppointmentActivity)
        builder.setTitle(title)
        builder.setCancelable(false)
        val customLayout: View = layoutInflater.inflate(R.layout.custom_progress_bar, null)
        builder.setView(customLayout)
        dialog = builder.create()
        val tvBody = customLayout.findViewById<TextView>(R.id.tv_value)
        tvBody.text = progressVal
        dialog.show()
    }

    companion object {
        var isMoreData = false
        var quickAppointmentFlag = 0
    }
}