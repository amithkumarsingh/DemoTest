package com.whitecoats.clinicplus.activities

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.whitecoats.clinicplus.MyClinicGlobalClass
import com.whitecoats.clinicplus.R
import com.whitecoats.clinicplus.adapters.AppointmentDetailsAddProcedureAdapter
import com.whitecoats.clinicplus.adapters.ProcedureSearchAdapter
import com.whitecoats.clinicplus.apis.ApiUrls
import com.whitecoats.clinicplus.models.AddProcedureListModel
import com.whitecoats.clinicplus.utils.ErrorHandlerClass.errorHandler
import com.whitecoats.clinicplus.viewmodels.AppointmentDetailsViewModel
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class AddProcedureActivity : AppCompatActivity() {
    private lateinit var procedureSearch: AutoCompleteTextView
    private lateinit var procedureSearchTextLayout: LinearLayout
    private lateinit var procedureListLayout: LinearLayout
    private lateinit var procedureSearchLayout: LinearLayout
    private lateinit var proceureList: RecyclerView
    private lateinit var procedureCount: TextView
    private lateinit var addProcedureLink: TextView
    private lateinit var addProcedure: Button
    private var addProcedureListModels: ArrayList<AddProcedureListModel>? = null
    private var searchProcedureListModels: ArrayList<AddProcedureListModel>? = null
    private var procedureSearchAdapter: ProcedureSearchAdapter? = null
    private var globalClass: MyClinicGlobalClass? = null
    private var appointmentDetailsViewModel: AppointmentDetailsViewModel? = null
    private var appointmentDetailsAddProcedureAdapter: AppointmentDetailsAddProcedureAdapter? = null
    private var discountArrayList: ArrayList<Int>? = null
    private var apptId = 0
    private var productId = 0
    private lateinit var intentObj: Intent
    private lateinit var addProcedureProgress: ProgressBar
    private var procedureString: String? = null
    private lateinit var procedureSearchText: TextView
    private lateinit var procedureBack: ImageView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_procedure)
        procedureSearch = findViewById(R.id.procedure_search)
        procedureSearchTextLayout = findViewById(R.id.procedure_error_search_layout)
        procedureListLayout = findViewById(R.id.procedure_list_layout)
        proceureList = findViewById(R.id.procedure_list)
        procedureCount = findViewById(R.id.procedure_count_text)
        addProcedure = findViewById(R.id.add_procedure)
        addProcedureLink = findViewById(R.id.add_procedure_link)
        addProcedureProgress = findViewById(R.id.add_proecedure_progress)
        procedureSearchLayout = findViewById(R.id.procedure_search_layout)
        procedureSearchText = findViewById(R.id.procedure_search_text)
        procedureBack = findViewById(R.id.procedure_back)
        addProcedureProgress.visibility = View.GONE
        intentObj = intent
        apptId = intentObj.getIntExtra("apptId", 0)
        productId = intentObj.getIntExtra("productId", 0)
        globalClass = applicationContext as MyClinicGlobalClass
        appointmentDetailsViewModel = ViewModelProvider(this).get(
            AppointmentDetailsViewModel::class.java
        )
        appointmentDetailsViewModel!!.init()
        addProcedureListModels = ArrayList()
        searchProcedureListModels = ArrayList()
        discountArrayList = ArrayList()
        discountArrayList = intentObj.getIntegerArrayListExtra("discountList")
        procedureSearchAdapter = ProcedureSearchAdapter(
            this@AddProcedureActivity,
            R.layout.procedure_search_item,
            searchProcedureListModels!!
        )
        procedureSearch.setAdapter(procedureSearchAdapter)
        procedureSearchLayout.visibility = View.GONE
        procedureSearchTextLayout.visibility = View.GONE
        procedureListLayout.visibility = View.GONE
        addProcedureProgress.visibility = View.VISIBLE
        addProcedure.isEnabled = false
        if (globalClass!!.isOnline) {
            getServicesForAppointmentData(this@AddProcedureActivity, apptId)
        } else {
            globalClass!!.noInternetConnection.showDialog(this@AddProcedureActivity)
        }
        MyClinicGlobalClass.logUserActionEvent(
            ApiUrls.doctorId,
            getString(R.string.AddingProcedureScreenImpressions),
            null
        )
        procedureSearch.setOnItemClickListener { parent, _, position, _ ->
            MyClinicGlobalClass.logUserActionEvent(
                ApiUrls.doctorId, getString(
                    R.string.AddingProcedureScreenSearch
                ), null
            )
            val addProcedureListModel = parent.getItemAtPosition(position) as AddProcedureListModel
            procedureString = addProcedureListModel.procedureName
            addProcedureListModels!!.add(addProcedureListModel)
            discountArrayList!!.add(0)
            appointmentDetailsAddProcedureAdapter!!.notifyData(
                addProcedureListModels!!,
                discountArrayList!!
            )
            updateCount(procedureCount, addProcedureListModels)
        }
        procedureBack.setOnClickListener {
            val intent = Intent()
            intent.putExtra("discountList", discountArrayList)
            setResult(1, intent)
            finish()
        }
        appointmentDetailsAddProcedureAdapter = AppointmentDetailsAddProcedureAdapter(
            addProcedureListModels,
            discountArrayList!!
        ) { capturedNotesId, captureNotesText, _, _ ->
            if (captureNotesText.equals("DiscountRemove", ignoreCase = true)) {
                MyClinicGlobalClass.logUserActionEvent(
                    ApiUrls.doctorId,
                    getString(R.string.AddingProcedureScreenRemove),
                    null
                )
                discountArrayList!!.removeAt(capturedNotesId)
                discountArrayList!!.add(capturedNotesId, 0)
                appointmentDetailsAddProcedureAdapter!!.notifyData(
                    addProcedureListModels!!,
                    discountArrayList!!
                )
            } else if (captureNotesText.equals("applyDiscount", ignoreCase = true)) {
                MyClinicGlobalClass.logUserActionEvent(
                    ApiUrls.doctorId,
                    getString(R.string.AddingProcedureScreenApplyDiscount),
                    null
                )
                val dialog = Dialog(this@AddProcedureActivity)
                dialog.setCancelable(false)
                dialog.setContentView(R.layout.procedure_apply_coupon_popup)
                val applyCouponEditText = dialog.findViewById<EditText>(R.id.applyCouponEditText)
                val applyCouponCardView = dialog.findViewById<CardView>(R.id.applyCouponCardView)
                val dailogArticleCancel = dialog.findViewById<ImageView>(R.id.dailogArticleCancel)
                dailogArticleCancel.setOnClickListener { dialog.dismiss() }
                applyCouponCardView.setOnClickListener {
                    val applycouponValue = applyCouponEditText.text.toString().toInt()
                    discountArrayList!!.removeAt(capturedNotesId)
                    discountArrayList!!.add(capturedNotesId, applycouponValue)
                    Log.i("apply dus", discountArrayList.toString())
                    dialog.dismiss()
                    appointmentDetailsAddProcedureAdapter!!.notifyData(
                        addProcedureListModels!!, discountArrayList!!
                    )
                }
                dialog.show()
            } else if (captureNotesText.equals("removeProcedure", ignoreCase = true)) {
                MyClinicGlobalClass.logUserActionEvent(
                    ApiUrls.doctorId,
                    getString(R.string.AddingProcedureScreenRemove),
                    null
                )
                addProcedureListModels!!.removeAt(capturedNotesId)
                discountArrayList!!.removeAt(capturedNotesId)
                appointmentDetailsAddProcedureAdapter!!.notifyData(
                    addProcedureListModels!!,
                    discountArrayList!!
                )
                updateCount(procedureCount, addProcedureListModels)
            }
        }
        val layoutManager: RecyclerView.LayoutManager =
            LinearLayoutManager(this@AddProcedureActivity)
        proceureList.layoutManager = layoutManager
        proceureList.itemAnimator = DefaultItemAnimator()
        proceureList.adapter = appointmentDetailsAddProcedureAdapter
        procedureSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            @SuppressLint("SetTextI18n")
            override fun onTextChanged(procedureName: CharSequence, i: Int, i1: Int, i2: Int) {
                if (procedureName.isEmpty()) {
                    // show eror text
                    procedureSearchTextLayout.visibility = View.VISIBLE
                    procedureListLayout.visibility = View.GONE
                }
                if (procedureName.length > 2) {
                    if (globalClass!!.isOnline) {
                        appointmentDetailsViewModel!!.getProcedure(
                            this@AddProcedureActivity,
                            procedureName.toString(),
                            "" + productId,
                            "asc",
                            "name",
                            10,
                            1
                        ).observe(this@AddProcedureActivity
                        ) { s ->
                            try {
                                val jsonObject = JSONObject(s)
                                Log.d("procedureName", "procedureName$jsonObject")
                                if (jsonObject.getInt("status_code") == 200) {
                                    val data = jsonObject.getJSONObject("response")
                                        .getJSONObject("response").getJSONArray("data")
                                    if (data.length() == 0) {
                                        procedureSearchTextLayout.visibility = View.VISIBLE
                                        procedureSearchText.text = "No Service Found"
                                    } else {
                                        searchProcedureListModels!!.clear()
                                        for (i in 0 until data.length()) {
                                            val procedure = data.getJSONObject(i)
                                            val addProcedureListModel = AddProcedureListModel()
                                            addProcedureListModel.procedureId =
                                                procedure.getInt("id")
                                            addProcedureListModel.procedureName =
                                                procedure.getString("name")
                                            addProcedureListModel.charges =
                                                procedure.getInt("pre_tax_amount")
                                            addProcedureListModel.discount = 0
                                            addProcedureListModel.tax = 0
                                            addProcedureListModel.totalAmount =
                                                procedure.getInt("pre_tax_amount")
                                            searchProcedureListModels!!.add(
                                                addProcedureListModel
                                            )
                                        }
                                        procedureSearchTextLayout.visibility = View.GONE
                                        procedureListLayout.visibility = View.VISIBLE
                                        procedureSearchAdapter!!.notifyDataSetChanged()
                                    }
                                } else {
                                    errorHandler(this@AddProcedureActivity, s)
                                }
                            } catch (e: JSONException) {
                                e.printStackTrace()
                            }
                        }
                    } else {
                        globalClass!!.noInternetConnection.showDialog(this@AddProcedureActivity)
                    }
                }
            }

            override fun afterTextChanged(s: Editable) {
                if (s.toString().isEmpty()) {
                    // shoe no laout
                    procedureSearchTextLayout.visibility = View.VISIBLE
                    procedureListLayout.visibility = View.GONE
                }
            }
        })
        addProcedure.setOnClickListener {
            if (globalClass!!.isOnline) {
                MyClinicGlobalClass.logUserActionEvent(
                    ApiUrls.doctorId, getString(
                        R.string.AddingProcedureScreenAdd
                    ), null
                )
                val params = JSONObject()
                try {
                    params.put("appointment_id", apptId)
                    val array = JSONArray()
                    for (i in addProcedureListModels!!.indices) {
                        array.put(addProcedureListModels!![i].procedureId)
                    }
                    params.put("services_ids", array)
                    Log.i("params", params.toString())
                    addProcedureProgress.visibility = View.VISIBLE
                    updateProcedure(this@AddProcedureActivity, params)
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            } else {
                globalClass!!.noInternetConnection.showDialog(this@AddProcedureActivity)
            }
        }
    }

    private fun getServicesForAppointmentData(activity: Activity?, appointmentID: Int) {
        appointmentDetailsViewModel!!.getServicesForAppointmentData(activity, appointmentID)
            .observe(this@AddProcedureActivity) { s ->
                try {
                    val response = JSONObject(s)
                    if (response.getInt("status_code") == 200) {
                        procedureSearchLayout.visibility = View.VISIBLE
                        val resObject =
                            response.getJSONObject("response").getJSONObject("response")
                        val invoiceServiceArray = resObject.getJSONArray("invoice_services")
                        if (invoiceServiceArray.length() > 0) {
                            for (i in 0 until invoiceServiceArray.length()) {
                                val appointmentInvoiceServiceArrayObject =
                                    invoiceServiceArray.getJSONObject(i)
                                val procedureListModel = AddProcedureListModel()
                                val serviceDetailsObjects =
                                    appointmentInvoiceServiceArrayObject.getJSONObject("service_details")
                                procedureListModel.procedureId =
                                    serviceDetailsObjects.getInt("id")
                                procedureListModel.procedureName =
                                    serviceDetailsObjects.getString("name")
                                procedureListModel.charges =
                                    serviceDetailsObjects.getInt("pre_tax_amount")
                                procedureListModel.tax =
                                    appointmentInvoiceServiceArrayObject.getInt("tax_amt")
                                procedureListModel.discount = 0
                                procedureListModel.totalAmount = 0
                                addProcedureListModels!!.add(procedureListModel)
                            }
                            procedureListLayout.visibility = View.VISIBLE
                            updateCount(procedureCount, addProcedureListModels)
                            addProcedure.isEnabled = true
                            appointmentDetailsAddProcedureAdapter!!.notifyData(
                                addProcedureListModels!!, discountArrayList!!
                            )
                        } else {
                            procedureSearchTextLayout.visibility = View.VISIBLE
                        }
                    } else {
                        errorHandler(this@AddProcedureActivity, s)
                    }
                    addProcedureProgress.visibility = View.GONE
                } catch (e: JSONException) {
                    e.printStackTrace()
                    Log.d("parserError:", "parserError:$e")
                }
            }
    }

    @SuppressLint("SetTextI18n")
    fun updateCount(
        textView: TextView?,
        addProcedureListModels: ArrayList<AddProcedureListModel>?
    ) {
        textView!!.text = addProcedureListModels!!.size.toString() + " Service added"
        addProcedure.isEnabled = addProcedureListModels.size > 0
    }

    private fun updateProcedure(activity: Activity?, params: JSONObject?) {
        appointmentDetailsViewModel!!.updateProcedure(activity, params)
            .observe(this@AddProcedureActivity) { s ->
                try {
                    val response = JSONObject(s)
                    if (response.getInt("status_code") == 200) {
                        if (response.getJSONObject("response").getInt("response") == 1) {
                            val intent = Intent()
                            intent.putExtra("discountList", discountArrayList)
                            Log.i("from discount", discountArrayList.toString())
                            setResult(2, intent)
                            finish()
                        } else {
                            Toast.makeText(
                                this@AddProcedureActivity,
                                "UnExpected error has occured, please try again",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    } else {
                        errorHandler(this@AddProcedureActivity, s)
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
                addProcedureProgress.visibility = View.GONE
            }
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        super.getOnBackPressedDispatcher().onBackPressed()
        val intent = Intent()
        intent.putExtra("discountList", discountArrayList)
        setResult(1, intent)
        finish()
    }
}