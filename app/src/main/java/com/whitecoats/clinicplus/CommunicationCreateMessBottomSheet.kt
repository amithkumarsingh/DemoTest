package com.whitecoats.clinicplus

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.graphics.PorterDuff
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.android.volley.Request.Method
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.BottomSheetCallback
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.switchmaterial.SwitchMaterial
import com.whitecoats.clinicplus.apis.ApiGetPostMethodCalls
import com.whitecoats.clinicplus.apis.ApiUrls
import com.whitecoats.clinicplus.utils.ErrorHandlerClass.errorHandler
import com.whitecoats.clinicplus.viewmodels.CommonViewModel
import com.whitecoats.model.AddPatientModel
import com.whitecoats.model.CommunicationMessageListModel
import org.json.JSONException
import org.json.JSONObject
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by vaibhav on 07-02-2018.
 */
class CommunicationCreateMessBottomSheet : BottomSheetDialogFragment() {
    private  var commMessagActivity: Activity?=null
    private lateinit var cancelIcon: ImageView
    private lateinit var bottomSheetMessageText: EditText
    private lateinit var bottomSheetToggleLayout: RelativeLayout
    private lateinit var switchButton: SwitchMaterial
    private lateinit var bottomSheetDone: RelativeLayout
    private lateinit var bottomSheetSendMessage: Button
    private lateinit var jsonValue: JSONObject
    private lateinit var interFaceSpinner: Spinner
    private lateinit var selectInterface: RelativeLayout
    var addInterfaceModelList: MutableList<AddPatientModel>? = null
    private var interFaceId = 0
    private lateinit var bottomSheetMessageNote: RelativeLayout
    private var hasInterface = false
    private var apiGetPostMethodCalls: ApiGetPostMethodCalls? = null
    private lateinit var dialogPopup: Dialog
    private lateinit var commonViewModel: CommonViewModel

    fun setupConfig(commMessagActivity: Activity) {
        this.commMessagActivity = commMessagActivity
        apiGetPostMethodCalls = ApiGetPostMethodCalls()
        addInterfaceModelList = ArrayList()
        commonViewModel =
            ViewModelProvider(commMessagActivity as CommunicationMessageListActivity)[CommonViewModel::class.java]
    }

    //Bottom Sheet Callback
    private val mBottomSheetBehaviorCallback: BottomSheetCallback = object : BottomSheetCallback() {
        override fun onStateChanged(bottomSheet: View, newState: Int) {
            if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                dismiss()
            }
        }

        override fun onSlide(bottomSheet: View, slideOffset: Float) {}
    }

    @SuppressLint("RestrictedApi")
    override fun setupDialog(dialog: Dialog, style: Int) {
//        super.setupDialog(dialog, style);
        //Get the content View
        val contentView =
            View.inflate(context, R.layout.fragment_bottom_sheet_comm_create_message, null)
        dialog.setContentView(contentView)
        //Set the coordinator layout behavior
        interfaceName
        val params = (contentView.parent as View).layoutParams as CoordinatorLayout.LayoutParams
        val behavior = params.behavior
        cancelIcon = contentView.findViewById(R.id.bottomSheetCancelIcon)
        cancelIcon = contentView.findViewById(R.id.bottomSheetCancelIcon)
        bottomSheetMessageText = contentView.findViewById(R.id.bottomSheetMessageText)
        bottomSheetToggleLayout = contentView.findViewById(R.id.bottomSheetToggleIcon)
        interFaceSpinner = contentView.findViewById(R.id.interFaceSpinner)
        selectInterface = contentView.findViewById(R.id.selectInterface)
        bottomSheetSendMessage = contentView.findViewById(R.id.bottomSheetSendMessage)
        bottomSheetMessageNote = contentView.findViewById(R.id.bottomSheetMessageNote)
        switchButton = contentView.findViewById(R.id.switchButton)
        switchButton.setOnCheckedChangeListener { _, isChecked ->
            hasInterface = if (isChecked) {
                // The toggle is enabled
                bottomSheetMessageNote.visibility = View.GONE
                selectInterface.visibility = View.VISIBLE
                true
            } else {
                // The toggle is disabled
                selectInterface.visibility = View.GONE
                bottomSheetMessageNote.visibility = View.VISIBLE
                false
            }
        }
        bottomSheetDone = contentView.findViewById(R.id.bottomSheetDone)
        bottomSheetSendMessage.setOnClickListener { //dialog.dismiss();
            if (bottomSheetMessageText.text.toString().equals("", ignoreCase = true)) {
                Toast.makeText(commMessagActivity, "Please write message.", Toast.LENGTH_LONG)
                    .show()
            } else {
                sendMessage(dialog)
            }
        }

        //Set callback
        if (behavior != null && behavior is BottomSheetBehavior<*>) {
            behavior.setBottomSheetCallback(mBottomSheetBehaviorCallback)
        }
        dialog.setCancelable(false)
        dialog.setCanceledOnTouchOutside(true)
        cancelIcon.setColorFilter(
            ContextCompat.getColor(requireActivity(), R.color.colorWhite),
            PorterDuff.Mode.SRC_IN
        )
        cancelIcon.setOnClickListener { dialog.dismiss() }
    }

    private fun sendMessage(dialog: Dialog) {
        showCustomProgressAlertDialog(
            resources.getString(R.string.sending_message),
            resources.getString(R.string.sending_message)
        )
        val url = ApiUrls.createMessage
        try {
            jsonValue = JSONObject()
            jsonValue.put("message", bottomSheetMessageText.text.toString())
            if (interFaceId == 0) {
                jsonValue.put("interfaceId", "")
            } else {
                jsonValue.put("interfaceId", interFaceId)
            }
            jsonValue.put("hasInterface", hasInterface)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        commonViewModel.commonViewModelCall(url, jsonValue, Method.POST).observe(
            requireActivity()
        ) { result ->
            try {
                //Process os success response
                dialogPopup.dismiss()
                val responseObj = JSONObject(result)
                if (responseObj.getInt("status_code") == 200) {
                    val response = responseObj.optJSONObject("response")
                    val createMessageResponse = response!!.getBoolean("response")
                    if (createMessageResponse) {
                        Toast.makeText(
                            commMessagActivity,
                            "Message sent successfully.",
                            Toast.LENGTH_SHORT
                        ).show()
                        dialog.dismiss()
                        getPastMessage(
                            "DateTime", "", "desc", 1,
                            10
                        )
                    }
                } else {
                    dialog.dismiss()
                    dialogPopup.dismiss()
                    errorHandler(requireContext(), result)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

    }

    // prepare the Request
    private val interfaceName: Unit
        get() {
            val url = ApiUrls.getDoctorInterFace
            // prepare the Request

            commonViewModel.commonViewModelCall(url, JSONObject(), Method.GET).observe(
                requireActivity()
            ) { result ->
                try {
                    val responseObj = JSONObject(result)
                    if (responseObj.getInt("status_code") == 200) {
                        val response = responseObj.optJSONObject("response")
                        val rootObj = response!!.getJSONArray("response")
                        for (j in 0 until rootObj.length()) {
                            val appointmentJsonObject = rootObj.getJSONObject(j)
                            if (!appointmentJsonObject.isNull("parentinterf")) {
                                val parentInterfaceObject =
                                    appointmentJsonObject.getJSONObject("parentinterf")
                                val model = AddPatientModel()
                                model.interfaceId = parentInterfaceObject.getInt("id")
                                model.interfaceName =
                                    parentInterfaceObject.getString("interface_name")
                                addInterfaceModelList!!.add(model)
                            }
                        }
                        val adapter = ArrayAdapter(
                            requireContext(),
                            android.R.layout.simple_spinner_dropdown_item,
                            addInterfaceModelList!!
                        )
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                        interFaceSpinner.adapter = adapter

                        // Set the ClickListener for Spinner
                        interFaceSpinner.onItemSelectedListener =
                            object : AdapterView.OnItemSelectedListener {
                                override fun onItemSelected(
                                    adapterView: AdapterView<*>?,
                                    view: View, i: Int, l: Long
                                ) {
                                    interFaceId = addInterfaceModelList!![i].interfaceId
                                }

                                // If no option selected
                                override fun onNothingSelected(arg0: AdapterView<*>?) {
                                    // TODO Auto-generated method stub
                                }
                            }
                    } else {
                        errorHandler(requireContext(), result)
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }
        }

    @SuppressLint("NotifyDataSetChanged")
    private fun getPastMessage(
        sortBy: String, search: String, sortorderBy: String, pageNum: Int,
        perPage: Int
    ) {
        val url =
            ApiUrls.getPastMessage + "?sortby=" + sortBy + "&search=" + search + "&sortorder=" + sortorderBy +
                    "&page=" + pageNum + "&per_page=" + perPage
        // prepare the Request

        commonViewModel.commonViewModelCall(url, JSONObject(), Method.GET).observe(
            requireActivity()
        ) { result ->
            CommunicationMessageListActivity.commMessageModelList!!.clear()
            try {
                val responseObj = JSONObject(result)
                if (responseObj.getInt("status_code") == 200) {
                    val response = responseObj.optJSONObject("response")
                    val rootObj = response!!.getJSONObject("response")
                    val patientArray = rootObj.getJSONArray("data")
                    for (j in 0 until patientArray.length()) {
                        val patientObject = patientArray.getJSONObject(j)
                        val temp = CommunicationMessageListModel()
                        temp.title = patientObject.getString("message")
                        temp.date =
                            parseDateToddMMyyyy(patientObject.getString("created_at"))
                        temp.attempted = patientObject.getInt("total_messages_attempted")
                        temp.failed = patientObject.getInt("messages_failed")
                        temp.send = patientObject.getInt("messages_sent")
                        CommunicationMessageListActivity.commMessageModelList!!.add(temp)
                    }
                    CommunicationMessageListActivity.recList!!.adapter =
                        CommunicationMessageListActivity.commMessageListAdapter
                    CommunicationMessageListActivity.commMessageListAdapter!!.notifyDataSetChanged()
                } else {
                    errorHandler(requireContext(), result)
                }
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }

        apiGetPostMethodCalls!!.volleyApiRequestData(
            url,
            Method.GET,
            null,
            requireContext(),
            object : VolleyCallback {
                override fun onSuccess(result: String) {

                }

                override fun onError(err: String) {
                }
            })
    }

    private fun parseDateToddMMyyyy(time: String?): String? {
        val inputPattern = "yyyy-MM-dd HH:mm:ss"
        val outputPattern = "dd MMM, yyyy"
        val inputFormat = SimpleDateFormat(inputPattern, Locale.ENGLISH)
        val outputFormat = SimpleDateFormat(outputPattern, Locale.ENGLISH)
        val date: Date?
        var str: String? = null
        try {
            date = inputFormat.parse(time!!)
            str = outputFormat.format(date!!)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return str
    }

    fun showCustomProgressAlertDialog(
        title: String?,
        progressVal: String?

    ) {
        val builder = AlertDialog.Builder(requireActivity())
        builder.setTitle(title)
        builder.setCancelable(false)
        val customLayout =
            LayoutInflater.from(requireActivity())
                .inflate(R.layout.custom_progress_bar, null)
        builder.setView(customLayout)
        dialogPopup = builder.create()
        val tvBody = customLayout.findViewById<TextView>(R.id.tv_value)
        tvBody.text = progressVal
        dialogPopup.show()
    }
}