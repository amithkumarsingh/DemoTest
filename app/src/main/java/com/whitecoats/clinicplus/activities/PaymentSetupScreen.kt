package com.whitecoats.clinicplus.activities

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.graphics.PorterDuff
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request.Method
import com.whitecoats.clinicplus.R
import com.whitecoats.clinicplus.adapters.PaymentOptionAdapter
import com.whitecoats.clinicplus.apis.ApiUrls
import com.whitecoats.clinicplus.interfaces.PaymentTypeClick
import com.whitecoats.clinicplus.utils.ErrorHandlerClass
import com.whitecoats.clinicplus.viewmodels.CommonViewModel
import com.zoho.salesiqembed.ZohoSalesIQ
import org.json.JSONObject

class PaymentSetupScreen : AppCompatActivity() {
    private lateinit var toolbar: Toolbar
    private lateinit var cardViewParent: CardView
    private lateinit var rvPaymentsOptions: RecyclerView
    private lateinit var paymentOptionAdapter: PaymentOptionAdapter
    private lateinit var listObj: List<String>
    private lateinit var commonViewModel: CommonViewModel
    private var paymentMode: MutableList<HashMap<String, String>>? = null
    private lateinit var dialog: Dialog
    private lateinit var tvErrorMessage: TextView
    private var firstTime: Boolean = true

    companion object {
        var defaultPaymentMode: String = ""
    }

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment_setup_screen)
        toolbar = findViewById(R.id.paymentOptionsToolbar)
        toolbar.title = "Financial"
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        val backArrow = ContextCompat.getDrawable(this, R.drawable.ic_arrow_back)
        backArrow!!.setColorFilter(
            ContextCompat.getColor(this, R.color.colorWhite),
            PorterDuff.Mode.SRC_ATOP
        )
        supportActionBar!!.setHomeAsUpIndicator(backArrow)
        commonViewModel = ViewModelProvider(this@PaymentSetupScreen)[CommonViewModel::class.java]
        toolbar.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }
        cardViewParent = findViewById(R.id.cardView_parent)
        rvPaymentsOptions = findViewById(R.id.rv_PaymentsOptions)
        tvErrorMessage = findViewById(R.id.tv_error_message)

        listObj = ArrayList()
        (listObj as ArrayList<String>).add("I have PayU Account")
        (listObj as ArrayList<String>).add("Add My UPI Id")
        (listObj as ArrayList<String>).add("All Payments Offline")
        paymentMode = java.util.ArrayList<HashMap<String, String>>()
        paymentOptionAdapter =
            PaymentOptionAdapter(paymentMode!!, object : PaymentTypeClick {
                override fun onItemClick(pos: Int, itemName: String, view1: LinearLayout?, view2: LinearLayout?) {
                    when (itemName) {
                        "I have PayU Account" -> {
                            ZohoSalesIQ.Tracking.setCustomAction("Settings - Financial Details")
                            val intent = Intent(
                                applicationContext,
                                PayUPaymentSetupScreen::class.java
                            )
                            startActivity(intent)
                        }
                        "Add My UPI Id" -> {
                            ZohoSalesIQ.Tracking.setCustomAction("Settings - Financial Details")
                            val intent = Intent(
                                applicationContext,
                                UPIPaymentSetUpScreen::class.java
                            )
                            intent.putExtra("callingFrom", "UPI Payment")
                            startActivity(intent)
                        }
                        else -> {
                            ZohoSalesIQ.Tracking.setCustomAction("Settings - Financial Details")
                            val intent = Intent(
                                applicationContext,
                                UPIPaymentSetUpScreen::class.java
                            )
                            intent.putExtra("callingFrom", "Offline Payment")
                            startActivity(intent)
                        }
                    }
                }
            })

        val mLinerLayout = LinearLayoutManager(this)
        rvPaymentsOptions.layoutManager = mLinerLayout
        rvPaymentsOptions.itemAnimator = DefaultItemAnimator()
        rvPaymentsOptions.adapter = paymentOptionAdapter

    }

    @SuppressLint("NotifyDataSetChanged")
    private fun getPaymentOptionDetails(callingFrom: String) {
        showCustomProgressAlertDialog("Please Wait", "Fetching details")
        val url = ApiUrls.getPaymentOptionsDetails
        commonViewModel.commonViewModelCall(url, JSONObject(), Method.GET).observe(
            this@PaymentSetupScreen
        ) { result ->
            try {
                if (paymentMode != null && paymentMode!!.size > 0) {
                    paymentMode!!.clear()
                }
                val resultObj = JSONObject(result)
                if (resultObj.getInt("status_code") == 200) {
                    val resObj = resultObj.optJSONObject("response")
                    val res = resObj!!.optJSONObject("response")
                    defaultPaymentMode = res!!.getString("default_payment_mode")
                    if (res.has("payment_modes")) {
                        val modeList = res.optJSONArray("payment_modes")
                        for (j in 0 until modeList!!.length()) {
                            val modeMap = HashMap<String, String>()
                            modeMap["title"] = modeList.getJSONObject(j).getString("title")
                            modeMap["payment_mode"] =
                                modeList.getJSONObject(j).getString("payment_mode")
                            paymentMode!!.add(modeMap)
                        }
                    }

                    paymentOptionAdapter.notifyDataSetChanged()
                    dialog.dismiss()
                } else if (resultObj.getInt("status_code") == 422) {
                    rvPaymentsOptions.visibility = View.GONE
                    tvErrorMessage.visibility = View.VISIBLE
                    tvErrorMessage.text = resultObj.getString("message")
                    dialog.dismiss()
                } else {
                    dialog.dismiss()
                    ErrorHandlerClass.errorHandler(this@PaymentSetupScreen, result)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                dialog.dismiss()
            }

        }

    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        super.getOnBackPressedDispatcher().onBackPressed()
        finish()
    }

    override fun onResume() {
        super.onResume()
        getPaymentOptionDetails("GetDetails")

    }

    private fun showCustomProgressAlertDialog(
        title: String,
        progressVal: String

    ) {
        val builder = AlertDialog.Builder(this@PaymentSetupScreen)
        builder.setTitle(title)
        builder.setCancelable(false)
        val customLayout = LayoutInflater.from(this@PaymentSetupScreen)
            .inflate(R.layout.custom_progrss_bar, null)
        builder.setView(customLayout)
        dialog = builder.create()
        val tvBody = customLayout.findViewById<TextView>(R.id.tv_value)
        tvBody.text = progressVal
        if (firstTime) {
            dialog.show()
            firstTime = false
        }
    }

/*
    private fun registerReceiverForUpdatingDefaultPaymentMode() {
        broadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(p0: Context, intent: Intent) {
                if (intent != null) {
                    if (intent.action == "updateSetDefaultPaymentMode") {
                      //  getPaymentOptionDetails("UpdateDetails")
                    }
                }
            }


        }
        val intentFilter = IntentFilter()
        intentFilter.addAction("updateSetDefaultPaymentMode")
        registerReceiver(broadcastReceiver, intentFilter)
    }
*/

    /* override fun onDestroy() {
         super.onDestroy()
         //unregisterReceiver(broadcastReceiver)
     }*/
}