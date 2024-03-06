package com.whitecoats.clinicplus.activities

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.PorterDuff
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.android.volley.Request
import com.whitecoats.clinicplus.R
import com.whitecoats.clinicplus.VolleyCallback
import com.whitecoats.clinicplus.apis.ApiGetPostMethodCalls
import com.whitecoats.clinicplus.apis.ApiUrls
import com.whitecoats.clinicplus.utils.ErrorHandlerClass.errorHandler
import org.json.JSONException
import org.json.JSONObject
import java.util.*


class SettingPersonalActivity : AppCompatActivity() {
    private var personalDobLayout: RelativeLayout? = null
    private var updatePersionalDetails: Button? = null
    private var personalDobText: TextView? = null
    private var personalExp: TextView? = null
    private var personalEmail: EditText? = null
    private var personalPhoneNumber: EditText? = null
    private var genderValue = 0
    private var rbMale: RadioButton? = null
    private var rbFemale: RadioButton? = null
    private var rg: RadioGroup? = null
    private var jsonValue: JSONObject? = null
    private var currentDate = ""
    private var globalApiCall: ApiGetPostMethodCalls? = null
    private lateinit var dialog: AlertDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.setting_personal_activity)
        val toolbar = findViewById<Toolbar>(R.id.settingFormToolbar)
        toolbar.title = intent.getStringExtra("Title")
        setSupportActionBar(toolbar)
        Objects.requireNonNull(supportActionBar)?.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        val upArrow = ResourcesCompat.getDrawable(resources, R.drawable.ic_arrow_back, null)
        upArrow?.setColorFilter(ContextCompat.getColor(applicationContext, R.color.colorWhite), PorterDuff.Mode.SRC_ATOP)
        supportActionBar!!.setHomeAsUpIndicator(upArrow)
        globalApiCall = ApiGetPostMethodCalls()
        //getting current date and setting it
        val c = Calendar.getInstance()
        currentDate =
            c[Calendar.DAY_OF_MONTH].toString() + "-" + (c[Calendar.MONTH] + 1) + "-" + c[Calendar.YEAR]

        personalDobLayout = findViewById(R.id.personalDobL)
        personalDobText = findViewById(R.id.personalDobText)
        personalExp = findViewById(R.id.personalExp)
        personalEmail = findViewById(R.id.personalEmail)
        personalPhoneNumber = findViewById(R.id.personalPhoneNumber)
        rg = findViewById(R.id.radioSex)
        rbMale = findViewById(R.id.personalMale)
        rbFemale = findViewById(R.id.personalFemale)
        updatePersionalDetails = findViewById(R.id.updatePersionalDetails)

        toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed() // Implemented by activity
        }

        updatePersionalDetails?.setOnClickListener { upDatePersionalDetails() }

        personalDobLayout?.setOnClickListener(View.OnClickListener { // Toast.makeText(getApplicationContext(),"ccc",Toast.LENGTH_LONG).show();
            val date = currentDate.split("-".toRegex()).dropLastWhile { it.isEmpty() }
                .toTypedArray()
            openDatePicker(date[0].toInt(), date[1].toInt() - 1, date[2].toInt())
        })

        getDoctorDetailsPersonal()

    }

    private fun updateSettingValues() {
        val doctorDetailsIntent = Intent()
        doctorDetailsIntent.action = "Update_Doctor_Details_Settings"
        sendBroadcast(doctorDetailsIntent)
    }


    override fun onBackPressed() {
        super.getOnBackPressedDispatcher().onBackPressed()
        updateSettingValues()
    }

    fun onRadioButtonClicked(v: View) {

        // Is the current Radio Button checked?
        val checked = (v as RadioButton).isChecked
        when (v.getId()) {
            R.id.personalMale -> {
                if (checked) rbMale!!.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.colorPrimary))
                rbMale!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.colorWhite))
                rbFemale!!.background =
                    ContextCompat.getDrawable(this, R.drawable.drawable_rectangle_shape)
                rbFemale!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.colorBlack))
                genderValue = 1
            }
            R.id.personalFemale -> {
                if (checked) rbFemale!!.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.colorPrimary))
                rbFemale!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.colorWhite))
                rbMale!!.background =
                    ContextCompat.getDrawable(this, R.drawable.drawable_rectangle_shape)
                rbMale!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.colorBlack))
                genderValue = 2
            }
        }
    }


    fun getDoctorDetailsPersonal() {
        val URL = ApiUrls.getDoctorDetailsProfile + "?id=" + ApiUrls.doctorId + "&mode=" + 1
        globalApiCall!!.volleyApiRequestData(
            URL,
            Request.Method.GET,
            null,
            this,
            object : VolleyCallback {
                override fun onSuccess(result: String) {
                    try {
                        val resObj = JSONObject(result)
                        val rootObj = resObj.getJSONArray("response")
                        for (i in 0 until rootObj.length()) {
                            val tempobj = rootObj.getJSONObject(i)
                            personalEmail!!.setText(tempobj.getString("email"))
                            personalPhoneNumber!!.setText(tempobj.getString("phone"))
                            genderValue = tempobj.getInt("gender")
                            if (genderValue == 1) {
                                rbMale!!.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.colorPrimary))
                                rbMale!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.colorWhite))
                                rbFemale!!.background = ContextCompat.getDrawable(
                                    this@SettingPersonalActivity,
                                    R.drawable.drawable_rectangle_shape
                                )
                                rbFemale!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.colorBlack))
                            } else if (genderValue == 2) {
                                rbFemale!!.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.colorPrimary))
                                rbFemale!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.colorWhite))
                                rbMale!!.background = ContextCompat.getDrawable(
                                    this@SettingPersonalActivity,
                                    R.drawable.drawable_rectangle_shape
                                )
                                rbMale!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.colorBlack))
                            }
                            personalDobText!!.text = tempobj.optString("dob")
                            personalExp!!.text = tempobj.getString("exp")
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                override fun onError(err: String) {
                    errorHandler(this@SettingPersonalActivity, err)
                }
            })
    }

    fun upDatePersionalDetails() {
        showCustomProgressAlertDialog();
        val URL = ApiUrls.updatePersionalDetails
        try {
            jsonValue = JSONObject()
            jsonValue!!.put("email", personalEmail!!.text.toString())
            jsonValue!!.put("phone", personalPhoneNumber!!.text.toString())
            jsonValue!!.put("dob", personalDobText!!.text.toString())
            jsonValue!!.put("gender", genderValue.toString())
            jsonValue!!.put("exp", personalExp!!.text.toString())
        } catch (e: java.lang.Exception) {
        }
        globalApiCall!!.volleyApiRequestData(
            URL,
            Request.Method.POST,
            jsonValue,
            this,
            object : VolleyCallback {
                override fun onSuccess(result: String) {
                    dialog.dismiss()
                    try {
                        val response = JSONObject(result)
                        //Process os success response
                        val myJson = JSONObject(response.toString())
                        //String name = myJson.optString("name");
                        val responseValue = myJson.optBoolean("response")
                        if (responseValue) {
                            Toast.makeText(
                                applicationContext,
                                "Updated successfully",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    } catch (e: java.lang.Exception) {
                        e.printStackTrace()
                        dialog.dismiss()

                    }
                }

                override fun onError(err: String) {
                    dialog.dismiss()
                    errorHandler(this@SettingPersonalActivity, err)
                }
            })
    }

    fun trimMessage(json: String?, key: String?): String? {
        var trimmedString: String? = null
        trimmedString = try {
            val obj = JSONObject(json)
            obj.getString(key)
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


    private fun openDatePicker(day: Int, month: Int, year: Int) {
        val datePickerDialog = DatePickerDialog(
            this,
            { view, year, monthOfYear, dayOfMonth ->
                personalDobText!!.text =
                    dayOfMonth.toString() + "-" + (monthOfYear + 1) + "-" + year
            }, year, month, day
        )
        datePickerDialog.datePicker.maxDate = System.currentTimeMillis()
        datePickerDialog.show()
    }

    private fun showCustomProgressAlertDialog() {
        // Create an alert builder
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Updating")

        // set the custom layout
        val customLayout: View = layoutInflater.inflate(R.layout.custom_progress_bar, null)
        builder.setView(customLayout)

        // create and show the alert dialog
        dialog = builder.create()
        dialog.show()
    }

}