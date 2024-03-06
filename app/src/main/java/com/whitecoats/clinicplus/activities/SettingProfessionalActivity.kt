package com.whitecoats.clinicplus.activities
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.android.volley.Request
import com.whitecoats.adapter.LanguageGridViewCustomAdapter
import com.whitecoats.adapter.QualificationGridViewCustomAdapter
import com.whitecoats.clinicplus.MyClinicGlobalClass
import com.whitecoats.clinicplus.R
import com.whitecoats.clinicplus.VolleyCallback
import com.whitecoats.clinicplus.apis.ApiGetPostMethodCalls
import com.whitecoats.clinicplus.apis.ApiUrls
import com.whitecoats.clinicplus.utils.ErrorHandlerClass.errorHandler
import com.whitecoats.model.SettingProfesionalModel
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.util.*


class SettingProfessionalActivity : AppCompatActivity() {

    private var jsonValue: JSONObject? = null
    private  var jsonValue1:JSONObject? = null

    private var languageAdd: TextView? = null
    private  var qualificationAdd:TextView? = null
    private var gridview: GridView? = null
    private var GetItem: String? = null
    private var actv: AutoCompleteTextView? = null
    private var updateProfesionalDetails: Button? = null
    private var languageClicked = 0
    private var actv1: EditText? = null
    private var doctorLanguageList: MutableList<SettingProfesionalModel>? = null
    private var languageListAdapter: LanguageGridViewCustomAdapter? = null
    private var doctorQualificationList: MutableList<SettingProfesionalModel>? = null
    private var languageList: MutableList<SettingProfesionalModel>? = null
    private var qualificationListAdapter: QualificationGridViewCustomAdapter? = null
    var globalClass: MyClinicGlobalClass? = null
    var profesionalLanguageText: TextView? = null
    var profesionalQualificationText:TextView? = null
    private var globalApiCall: ApiGetPostMethodCalls? = null
    private lateinit var dialog: AlertDialog


    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.setting_professional_activity)

        val toolbar = findViewById<Toolbar>(R.id.settingFormToolbar)
        toolbar.title = intent.getStringExtra("Title")
        setSupportActionBar(toolbar)
        Objects.requireNonNull(supportActionBar)?.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        val upArrow = ResourcesCompat.getDrawable(resources, R.drawable.ic_arrow_back,null)
        upArrow?.setColorFilter(ContextCompat.getColor(applicationContext, R.color.colorWhite), PorterDuff.Mode.SRC_ATOP)
        supportActionBar!!.setHomeAsUpIndicator(upArrow)
        globalApiCall = ApiGetPostMethodCalls()

        globalClass = applicationContext as MyClinicGlobalClass
        doctorLanguageList = ArrayList()
        doctorQualificationList = ArrayList()
        languageList = ArrayList()
        languageAdd = findViewById(R.id.languageAdd)
        qualificationAdd = findViewById(R.id.qualificationAdd)
        profesionalLanguageText =
            findViewById<TextView>(R.id.profesionalLanguageText)
        profesionalQualificationText =
            findViewById<TextView>(R.id.profesionalQualificationText)
        updateProfesionalDetails = findViewById(R.id.updateProfesionalDetails)
        profesionalLanguageText =
            findViewById<TextView>(R.id.profesionalLanguageText)


        toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed() // Implemented by activity
        }

        updateProfesionalDetails?.setOnClickListener(View.OnClickListener {
            Toast.makeText(
                this@SettingProfessionalActivity,
                "Details updated successfully",
                Toast.LENGTH_LONG
            ).show()
        })


        languageAdd!!.setOnClickListener(View.OnClickListener {
            languageClicked = 1
            (doctorLanguageList as ArrayList<SettingProfesionalModel>).clear()
            (doctorQualificationList as ArrayList<SettingProfesionalModel>).clear()
            getPrefesionalLanguage()
            getPrefesionalQualification()
            val builder = AlertDialog.Builder(this@SettingProfessionalActivity)
            val inflater = layoutInflater
            val inflator = inflater.inflate(R.layout.dailog_profesional_language, null)
            val dailogArticleCancel =
                inflator.findViewById<View>(R.id.dailogArticleCancel) as ImageView
            actv = inflator.findViewById<View>(R.id.autoCompleteTextView) as AutoCompleteTextView
            val categorySave = inflator.findViewById<View>(R.id.categorySaveButton) as Button
            gridview = inflator.findViewById<View>(R.id.gridView1) as GridView
            builder.setView(inflator)
                .setNegativeButton(
                    R.string.cancel
                ) { dialog, id ->
                    dialog.dismiss()
                }
            val alertDialog = builder.create()
            dailogArticleCancel.setOnClickListener {
                alertDialog.dismiss()
            }
            categorySave.setOnClickListener {
                if (globalClass!!.isOnline) {
                    try {
                        GetItem = actv!!.getText().toString()
                        var selectedCategoryId = 0
                        for (i in (languageList as ArrayList<SettingProfesionalModel>).indices) {
                            if (GetItem.equals(
                                    (languageList as ArrayList<SettingProfesionalModel>).get(i).languageName,
                                    ignoreCase = true
                                )
                            ) {
                                selectedCategoryId = (languageList as ArrayList<SettingProfesionalModel>).get(i).languageId
                            } else {
                            }
                        }
                        if (GetItem!!.isEmpty()) {
                        } else {
                            val selected = SettingProfesionalModel()
                            selected.languageName = GetItem
                            selected.languageId = selectedCategoryId
                            (doctorLanguageList as ArrayList<SettingProfesionalModel>).add(doctorLanguageList!!.size, selected)
                            languageListAdapter!!.notifyDataSetChanged()
                        }
                        var languageStr: String? = ""
                        for (i in doctorLanguageList!!.indices) {
                            languageStr += doctorLanguageList!!.get(i).languageName
                            if (i != doctorLanguageList!!.size - 1) {
                                languageStr += " | "
                            }
                        }
                        profesionalLanguageText!!.setText(
                            languageStr
                        )
                        if (actv!!.getText().toString().equals("", ignoreCase = true)) {
                            Toast.makeText(
                                this@SettingProfessionalActivity,
                                "Please enter a language",
                                Toast.LENGTH_LONG
                            ).show()
                        } else {
                            if (globalClass!!.isOnline) {
                                upDateProfesionalDetails();
                                actv!!.setText("")
                            } else {
                                globalClass!!.noInternetConnection.showDialog(this@SettingProfessionalActivity)
                            }
                        }
                    } catch (e: Exception) {
                    }
                } else {
                    globalClass!!.noInternetConnection.showDialog(this@SettingProfessionalActivity)
                }
            }

            //Creating the instance of ArrayAdapter containing list of fruit names
            val adapter = ArrayAdapter(
                this@SettingProfessionalActivity,
                android.R.layout.select_dialog_item,
                languageList as ArrayList<SettingProfesionalModel>
            )
            //Getting the instance of AutoCompleteTextView
            actv!!.setThreshold(1) //will start working from first character
            actv!!.setAdapter(adapter) //setting the adapter data into the AutoCompleteTextView
            actv!!.setTextColor(Color.BLACK)
            alertDialog.show()
        })


        qualificationAdd!!.setOnClickListener(View.OnClickListener {
            languageClicked = 2
            (doctorLanguageList as ArrayList<SettingProfesionalModel>).clear()
            getPrefesionalLanguage()
            (doctorQualificationList as ArrayList<SettingProfesionalModel>).clear()
            getPrefesionalQualification()
            val builder = AlertDialog.Builder(this@SettingProfessionalActivity)
            val inflater = layoutInflater
            val inflator = inflater.inflate(R.layout.dailog_profesional_qualification, null)
            val dailogArticleCancel =
                inflator.findViewById<View>(R.id.dailogArticleCancel) as ImageView
            actv1 = inflator.findViewById<View>(R.id.autoCompleteTextView) as EditText
            val categorySave = inflator.findViewById<View>(R.id.categorySaveButton) as Button
            gridview = inflator.findViewById<View>(R.id.gridView1) as GridView
            builder.setView(inflator)
                .setNegativeButton(
                    R.string.cancel
                ) { dialog, id ->
                    dialog.dismiss()
                }
            val alertDialog = builder.create()
            dailogArticleCancel.setOnClickListener {
                alertDialog.dismiss()
            }
            categorySave.setOnClickListener {
                if (globalClass!!.isOnline) {
                    GetItem = actv1!!.getText().toString()
                    val selectedCategoryId = 0
                    if (GetItem!!.isEmpty()) {
                    } else {
                        val selected = SettingProfesionalModel()
                        selected.qualificationName = GetItem
                        selected.qualificationId = selectedCategoryId
                        (doctorQualificationList as ArrayList<SettingProfesionalModel>).add((doctorQualificationList as ArrayList<SettingProfesionalModel>).size, selected)
                        qualificationListAdapter!!.notifyDataSetChanged()
                    }

                    //JSONArray qualificationsArr = userArr.getJSONArray("qualifications");
                    var qualificationsStr: String? = ""
                    for (i in (doctorQualificationList as ArrayList<SettingProfesionalModel>).indices) {
                        qualificationsStr += (doctorQualificationList as ArrayList<SettingProfesionalModel>).get(i).qualificationName
                        if (i != (doctorQualificationList as ArrayList<SettingProfesionalModel>).size - 1) {
                            qualificationsStr += " | "
                        }
                    }
                    profesionalQualificationText!!.setText(
                        qualificationsStr
                    )
                    if (actv1!!.getText().toString().equals("", ignoreCase = true)) {
                        Toast.makeText(
                            this@SettingProfessionalActivity,
                            "Please enter a qualification",
                            Toast.LENGTH_LONG
                        ).show()
                    } else {
                        if (globalClass!!.isOnline) {
                            upDateProfesionalDetails()
                        } else {
                            globalClass!!.noInternetConnection.showDialog(this@SettingProfessionalActivity)
                        }
                    }
                } else {
                    globalClass!!.noInternetConnection.showDialog(this@SettingProfessionalActivity)
                }
            }
            alertDialog.show()
        })

        updateProfesionalDetails!!.setOnClickListener(View.OnClickListener {
            Toast.makeText(
                this@SettingProfessionalActivity,
                "Details updated successfully",
                Toast.LENGTH_LONG
            ).show()
        })


        getDoctorDetailsProfesional()
        getProfesionalLanguageList()


    }

    private fun updateSettingValues() {
        val doctorDetailsIntent = Intent()
        doctorDetailsIntent.action = "Update_Doctor_Details_Settings"
        sendBroadcast(doctorDetailsIntent)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        updateSettingValues()
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


    fun getPrefesionalLanguage() {
        val URL = ApiUrls.getDoctorDetail + "?id=" + ApiUrls.doctorId
        globalApiCall!!.volleyApiRequestData(
            URL,
            Request.Method.GET,
            null,
            this,
            object : VolleyCallback {
                override fun onSuccess(result: String) {
                    try {
                        val response = JSONObject(result)
                        val rootObj = response.getJSONObject("response")
                        val userArr = rootObj.getJSONObject("user")
                        //rootObj = userArr.getJSONObject(0);
                        val specializationsArr = userArr.getJSONArray("languages")
                        for (j in 0 until specializationsArr.length()) {
                            val categoryObject = specializationsArr.getJSONObject(j)
                            val model = SettingProfesionalModel()
                            model.languageId = categoryObject.getInt("id")
                            model.languageName = categoryObject.getString("language")
                            model.languagePivot = categoryObject.getJSONObject("pivot")
                            doctorLanguageList?.add(model)
                        }
                        if (languageClicked == 1) {
                            languageListAdapter = doctorLanguageList?.let {
                                LanguageGridViewCustomAdapter(
                                    this@SettingProfessionalActivity, it
                                ) { v, parameter, type, recordIdArray ->
                                    if (type.equals("LANG_REMOVE", ignoreCase = true)) {
                                        removeItemLanguage(parameter.toInt()) //doctorQualificationList
                                    }
                                }
                            }
                            gridview!!.adapter = languageListAdapter
                            languageListAdapter!!.notifyDataSetChanged()
                        }
                    } catch (e: java.lang.Exception) {
                        e.printStackTrace()
                    }
                }

                override fun onError(err: String) {
                    errorHandler(this@SettingProfessionalActivity, err)
                }
            })
    }


    fun getPrefesionalQualification() {
        val URL = ApiUrls.getDoctorDetail + "?id=" + ApiUrls.doctorId
        globalApiCall!!.volleyApiRequestData(
            URL,
            Request.Method.GET,
            null,
            this,
            object : VolleyCallback {
                override fun onSuccess(result: String) {
                    try {
                        val response = JSONObject(result)
                        val rootObj = response.getJSONObject("response")
                        val userArr = rootObj.getJSONObject("user")
                        val qualificationsArr = userArr.getJSONArray("qualifications")
                        for (j in 0 until qualificationsArr.length()) {
                            val categoryObject = qualificationsArr.getJSONObject(j)
                            val model = SettingProfesionalModel()
                            model.qualificationId = categoryObject.getInt("id")
                            model.qualificationName = categoryObject.getString("qualification")
                            model.quaalificationUserId = categoryObject.getInt("user_id")
                            doctorQualificationList?.add(model)
                        }
                        if (languageClicked == 2) {
                            qualificationListAdapter = doctorQualificationList?.let {
                                QualificationGridViewCustomAdapter(
                                    this@SettingProfessionalActivity, it
                                ) { v, parameter, type, recordIdArray ->
                                    if (type.equals("QUAL_REMOVE", ignoreCase = true)) {
                                        removeItemQualification(parameter.toInt()) //doctorQualificationList
                                    }
                                }
                            }
                            gridview!!.adapter = qualificationListAdapter
                            qualificationListAdapter!!.notifyDataSetChanged()
                        }
                    } catch (e: java.lang.Exception) {
                        e.printStackTrace()
                    }
                }

                override fun onError(err: String) {
                    errorHandler(this@SettingProfessionalActivity, err)
                }
            })
    }


    fun upDateProfesionalDetails() {
        showCustomProgressAlertDialog();

        val URL = ApiUrls.updateProfesional
        try {
            jsonValue = JSONObject()
            var jsonlanguageObject = JSONObject()
            var jsonQualificationObjects = JSONObject()
            val languageArray = JSONArray()
            val qualificationArray = JSONArray()
            for (i in doctorLanguageList!!.indices) {
                jsonlanguageObject = JSONObject()
                jsonlanguageObject.put("english", doctorLanguageList!![i].languageName)
                jsonlanguageObject.put("id", doctorLanguageList!![i].languageId)
                jsonlanguageObject.put("language", doctorLanguageList!![i].languageName)
                languageArray.put(jsonlanguageObject)
            }
            for (i in doctorQualificationList!!.indices) {
                jsonQualificationObjects = JSONObject()
                jsonQualificationObjects.put(
                    "qualification",
                    doctorQualificationList!![i].qualificationName
                )
                jsonQualificationObjects.put("id", doctorQualificationList!![i].qualificationId)
                jsonQualificationObjects.put("user_id", ApiUrls.doctorId)
                qualificationArray.put(jsonQualificationObjects)
            }
            jsonValue!!.put("languages", languageArray)
            jsonValue!!.put("qualifications", qualificationArray)
            jsonValue!!.put("mode", 0)
        } catch (e: java.lang.Exception) {
        }
        globalApiCall!!.volleyApiRequestData(
            URL,
            Request.Method.POST,
            jsonValue,
            this,
            object : VolleyCallback {
                override fun onSuccess(result: String) {
                    try {
                        val response = JSONObject(result)
                        dialog.dismiss()
                        val myJson = JSONObject(response.toString())
                        val responseValue = myJson.optBoolean("response")
                        if (responseValue) {
                            Toast.makeText(
                                applicationContext,
                                "Updated successfully",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    } catch (e: java.lang.Exception) {
                        dialog.dismiss()
                        e.printStackTrace()
                    }
                }

                override fun onError(err: String) {
                    dialog.dismiss()
                    errorHandler(this@SettingProfessionalActivity, err)
                }
            })
    }


    fun getProfesionalLanguageList() {
        val URL = ApiUrls.getProfesionalLanguage
        globalApiCall!!.volleyApiRequestData(
            URL,
            Request.Method.GET,
            null,
            this,
            object : VolleyCallback {
                override fun onSuccess(result: String) {
                    try {
                        val response = JSONObject(result)
                        val languageArray = response.getJSONArray("response")
                        for (j in 0 until languageArray.length()) {
                            val categoryObject = languageArray.getJSONObject(j)
                            val model = SettingProfesionalModel()
                            model.languageId = categoryObject.getInt("id")
                            model.languageName = categoryObject.getString("language")
                            languageList?.add(model)
                        }
                    } catch (e: java.lang.Exception) {
                        e.printStackTrace()
                    }
                }

                override fun onError(err: String) {
                    errorHandler(this@SettingProfessionalActivity, err)
                }
            })
    }


    fun removeItemLanguage(position: Int) {
        doctorLanguageList?.removeAt(position)
        //Imageid.remove(position);
        languageListAdapter!!.notifyDataSetChanged()
        var languageStr: String? = ""
        for (i in doctorLanguageList!!.indices) {
            // JSONObject tempobj = specializationsArr.getJSONObject(i);
            languageStr += doctorLanguageList!![i].languageName
            if (i != doctorLanguageList!!.size - 1) {
                languageStr += " | "
            }
        }
        profesionalLanguageText!!.setText(languageStr)
        upDateProfesionalDetails()
    }

    fun removeItemQualification(position: Int) {
        doctorQualificationList?.removeAt(position)
        //Imageid.remove(position);
        qualificationListAdapter!!.notifyDataSetChanged()
        var qualificationsStr: String? = ""
        for (i in doctorQualificationList!!.indices) {
            //JSONObject tempobj = qualificationsArr.getJSONObject(i);
            qualificationsStr += doctorQualificationList!![i].qualificationName
            if (i != doctorQualificationList!!.size - 1) {
                qualificationsStr += " | "
            }
        }
        profesionalQualificationText!!.setText(qualificationsStr)
        upDateProfesionalDetails()
    }

    fun getDoctorDetailsProfesional() {
        val URL = ApiUrls.getDoctorDetail + "?id=" + ApiUrls.doctorId
        globalApiCall!!.volleyApiRequestData(
            URL,
            Request.Method.GET,
            null,
            this,
            object : VolleyCallback {
                override fun onSuccess(result: String) {
                    try {
                        val response = JSONObject(result)
                        val rootObj = response.getJSONObject("response")
                        val userArr = rootObj.getJSONObject("user")
                        val specializationsArr = userArr.getJSONArray("languages")
                        var languageStr = ""
                        for (i in 0 until specializationsArr.length()) {
                            val tempobj = specializationsArr.getJSONObject(i)
                            languageStr += tempobj.getString("language")
                            if (i != specializationsArr.length() - 1) {
                                languageStr += " | "
                            }
                        }
                        val qualificationsArr = userArr.getJSONArray("qualifications")
                        var qualificationsStr = ""
                        for (i in 0 until qualificationsArr.length()) {
                            val tempobj = qualificationsArr.getJSONObject(i)
                            qualificationsStr += tempobj.getString("qualification")
                            if (i != qualificationsArr.length() - 1) {
                                qualificationsStr += " | "
                            }
                        }
                       profesionalLanguageText!!.setText(languageStr)
                        profesionalQualificationText!!.setText(
                            qualificationsStr
                        )
                    } catch (e: java.lang.Exception) {
                        e.printStackTrace()
                    }
                }

                override fun onError(err: String) {
                    errorHandler(this@SettingProfessionalActivity, err)
                }
            })
    }

    fun showCustomProgressAlertDialog() {
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