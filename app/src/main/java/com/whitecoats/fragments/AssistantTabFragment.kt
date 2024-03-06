package com.whitecoats.fragments

import ai.api.AIDataService
import ai.api.AIListener
import ai.api.AIServiceException
import ai.api.android.AIConfiguration
import ai.api.android.AIService
import ai.api.model.AIError
import ai.api.model.AIRequest
import ai.api.model.AIResponse
import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.PorterDuff
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import android.widget.EditText
import android.widget.HorizontalScrollView
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.whitecoats.adapter.AssistantTabAdapter
import com.whitecoats.clinicplus.AppointmentApiRequests
import com.whitecoats.clinicplus.MyClinicGlobalClass
import com.whitecoats.clinicplus.R
import com.whitecoats.clinicplus.VolleyCallback
import com.whitecoats.clinicplus.apis.ApiGetPostMethodCalls
import com.whitecoats.clinicplus.apis.ApiUrls
import com.whitecoats.clinicplus.utils.ErrorHandlerClass.errorHandler
import com.whitecoats.model.AssistantTabListModel
import org.json.JSONArray
import org.json.JSONObject
import smartdevelop.ir.eram.showcaseviewlib.GuideView
import smartdevelop.ir.eram.showcaseviewlib.config.DismissType

class AssistantTabFragment : Fragment(), AIListener {
    private val mMessageRecycler: RecyclerView? = null
    private var aiSendRequest: FloatingActionButton? = null

    //private ImageView aiSendRequest;
    private var msgInputText: EditText? = null
    var aiDataService: AIDataService? = null
    private var aiService: AIService? = null
    var permissionsRequired = arrayOf(Manifest.permission.RECORD_AUDIO)
    private var inListeningMode = false
    private var queryType = 0 // 0 = voice, 1 = text
    private val savingRecord = false
    private var assistantTabAdapter: AssistantTabAdapter? = null
    private var msgList: MutableList<AssistantTabListModel>? = null
    private var msgRecyclerView: RecyclerView? = null

    //dialog flow ai api token
    //    public String aiClientId = "eb5059a850be4700b837af39c4d88014";
    private var apiRequests: AppointmentApiRequests? = null
    private var aiQuery: JSONObject? = null
    private var isContextPresent = false
    private var aiShortcutsView: HorizontalScrollView? = null
    private var lateShortcut: RelativeLayout? = null
    private var recordsShortcut: RelativeLayout? = null
    private var apptShortcut: RelativeLayout? = null
    private var helpShortcut: RelativeLayout? = null
    private var cancelApptShortcut: RelativeLayout? = null
    private var appPreference: SharedPreferences? = null
    private var assistView: View? = null
    private var apiGetPostMethodCalls: ApiGetPostMethodCalls? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val assistTabView = inflater.inflate(R.layout.activity_assistant_tab_list, container, false)
        // final View assistTabView = View.inflate(getContext(),R.layout.activity_assistant_tab_list, null);

        // Get RecyclerView object.
        assistView = assistTabView
        apiGetPostMethodCalls = ApiGetPostMethodCalls()
        msgRecyclerView = assistTabView.findViewById(R.id.assistantTabChatRecyclerView)
        aiSendRequest = assistTabView.findViewById(R.id.assistantTabChatSendMsg)
        //        try {
//            Drawable myFabSrc = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_mic_assistance, null);
        val myFabSrc = resources.getDrawable(R.drawable.ic_mic_assistance)
        val willBeWhite = myFabSrc.constantState!!.newDrawable()
        willBeWhite.mutate()
            .setColorFilter(resources.getColor(R.color.colorWhite), PorterDuff.Mode.SRC_IN)
        aiSendRequest?.setImageDrawable(willBeWhite)

//        }
//        catch (Exception e)
//        {
//
//        }
        val config = AIConfiguration(
            ApiUrls.aiClientId,
//            AIConfiguration.SupportedLanguages.English,
            ai.api.AIConfiguration.SupportedLanguages.English,
            AIConfiguration.RecognitionEngine.System
        )
        aiService = AIService.getService(activity, config)
        aiDataService = AIDataService(config)
        aiService?.setListener(this)
        aiService?.stopListening()
        // Set RecyclerView layout manager.
        val linearLayoutManager = LinearLayoutManager(activity)
        msgRecyclerView?.layoutManager = linearLayoutManager
        // Create the initial data list.
        msgList = ArrayList()
        val msgDto = AssistantTabListModel(2, "How can I help you?")
        msgInputText = assistTabView.findViewById<View>(R.id.assistantTabChatInputMsg) as EditText
        //        Button msgSendButton = assistTabView.findViewById(R.id.assistantTabChatSendMsg);
        (msgList as ArrayList<AssistantTabListModel>).add(msgDto)
        apiRequests = AppointmentApiRequests()
        aiQuery = JSONObject()
        aiShortcutsView = assistTabView.findViewById(R.id.assistantTabHorizontalScrollView1)
        lateShortcut = assistTabView.findViewById(R.id.assistantTabLateShortcut)
        recordsShortcut = assistTabView.findViewById(R.id.assistantTabRecordShortcut)
        apptShortcut = assistTabView.findViewById(R.id.assistantTabApptShortcut)
        helpShortcut = assistTabView.findViewById(R.id.assistantTabHelpShortcut)
        cancelApptShortcut = assistTabView.findViewById(R.id.assistantTabCancelApptShortcut)
        appPreference = requireActivity().getSharedPreferences(ApiUrls.appSharedPref, Context.MODE_PRIVATE)
        aiSendRequest?.viewTreeObserver
            ?.addOnGlobalLayoutListener(object : OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    if (ApiUrls.homeGuideTab == 1) {
                        // showGuide(1);
                        aiSendRequest!!.viewTreeObserver.removeOnGlobalLayoutListener(this)
                    }
                }
            })


        // Create the data adapter with above data list.
        assistantTabAdapter = AssistantTabAdapter(msgList, activity) { v, parameter, type ->
            if (type.equals("CANCEL_APPT_YES", ignoreCase = true)) {
                cancelAppointments(parameter)
            } else if (type.equals("CANCEL_APPT_NO", ignoreCase = true)) {
                val model = AssistantTabListModel(2, "")
                model.msgContent = "Ok as you wish"
                (msgList as ArrayList<AssistantTabListModel>).add(model)
                val newMsgPosition = (msgList as ArrayList<AssistantTabListModel>).size - 1
                assistantTabAdapter!!.notifyItemInserted(newMsgPosition)
                msgRecyclerView?.scrollToPosition(newMsgPosition)
            } else if (type.equals("LATE_INFORM", ignoreCase = true)) {
                delayIntimation(parameter)
            } else if (type.equals("CALL_PATIENT", ignoreCase = true)) {
                val builder = AlertDialog.Builder(
                    activity
                )
                builder.setTitle("Call Confirm")
                builder.setMessage("Are you sure?")
                builder.setPositiveButton("YES") { dialog, which ->
                    onCall(parameter)
                    dialog.dismiss()
                }
                builder.setNegativeButton("NO") { dialog, which -> // Do nothing
                    dialog.dismiss()
                }
                val alert = builder.create()
                alert.show()
            }
        }

        // Set data adapter to RecyclerView.
        msgRecyclerView?.adapter = assistantTabAdapter
        msgInputText!!.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
//                aiShortcutsView.setVisibility(View.GONE);
            }

            override fun afterTextChanged(editable: Editable) {
                val myFabSrc2 = resources.getDrawable(R.drawable.ic_send)
                val willBeWhite2 = myFabSrc2.constantState!!.newDrawable()
                willBeWhite2.mutate()
                    .setColorFilter(resources.getColor(R.color.colorWhite), PorterDuff.Mode.SRC_IN)
                aiSendRequest?.setImageDrawable(willBeWhite2)
                // aiSendRequest.setImageDrawable(assistTabView.getResources().getDrawable(R.drawable.ic_send));
                queryType = 1
                if (msgInputText!!.text.toString().isEmpty()) {
                    val myFabSrc = resources.getDrawable(R.drawable.ic_close)
                    val willBeWhite = myFabSrc.constantState!!.newDrawable()
                    willBeWhite.mutate().setColorFilter(
                        resources.getColor(R.color.colorWhite),
                        PorterDuff.Mode.SRC_IN
                    )
                    aiSendRequest?.setImageDrawable(willBeWhite)
                    queryType = 0
                }

//                aiShortcutsView.setVisibility(View.VISIBLE);
            }
        })
        aiSendRequest?.setOnClickListener(View.OnClickListener {
            MyClinicGlobalClass.logUserActionEvent(
                ApiUrls.doctorId,
                getString(R.string.AssistantSend),
                null
            )
            if (queryType == 0) {
                inListeningMode = if (inListeningMode) {
                    aiService?.stopListening()
                    false
                } else {
                    aiService?.startListening()
                    //get the drawable
                    val myFabSrc = resources.getDrawable(R.drawable.ic_close)
                    val willBeWhite = myFabSrc.constantState!!.newDrawable()
                    willBeWhite.mutate().setColorFilter(
                        resources.getColor(R.color.colorWhite),
                        PorterDuff.Mode.SRC_IN
                    )
                    aiSendRequest!!.setImageDrawable(willBeWhite)
                    true
                }
            } else if (queryType == 1) {
                val aiRequest = AIRequest()
                aiRequest.setQuery(msgInputText!!.text.toString())
                if (msgInputText!!.text.toString().equals("help", ignoreCase = true)) {
                    //  getAIHelp();
                } else {
                    if (isContextPresent) {
                        try {
                            aiQuery!!.remove("query")
                            aiQuery!!.put("query", msgInputText!!.text.toString())
                            getAIQuery(aiQuery)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    } else {
                        SendTextAIRequest().execute(aiRequest)
                    }
                    val msgContent = msgInputText!!.text.toString()
                    if (!TextUtils.isEmpty(msgContent)) {
                        // Add a new sent message to the list.
                        val msgDto = AssistantTabListModel(1, msgContent)
                        (msgList as ArrayList<AssistantTabListModel>).add(msgDto)
                        val newMsgPosition = (msgList as ArrayList<AssistantTabListModel>).size - 1
                        // Notify recycler view insert one new data.
                        assistantTabAdapter!!.notifyItemInserted(newMsgPosition)
                        // Scroll RecyclerView to the last message.
                        msgRecyclerView?.scrollToPosition(newMsgPosition)
                        // Empty the input edit text box.
                        msgInputText!!.setText("")
                    }
                }
                msgInputText!!.setText("")
                queryType = 0
                msgInputText!!.hint = "Type a Message"
                val myFabSrc = resources.getDrawable(R.drawable.ic_mic_assistance)
                val willBeWhite = myFabSrc.constantState!!.newDrawable()
                willBeWhite.mutate()
                    .setColorFilter(resources.getColor(R.color.colorWhite), PorterDuff.Mode.SRC_IN)
                aiSendRequest!!.setImageDrawable(willBeWhite)
                // aiSendRequest.setImageDrawable(view.getResources().getDrawable(R.drawable.ic_mic_assistance));
            }
        })
        lateShortcut?.setOnClickListener(View.OnClickListener {
            MyClinicGlobalClass.logUserActionEvent(
                ApiUrls.doctorId,
                getString(R.string.AssistantIamLate),
                null
            )
            sendingShortcutRequest("I am running late")
        })
        recordsShortcut?.setOnClickListener(View.OnClickListener {
            MyClinicGlobalClass.logUserActionEvent(
                ApiUrls.doctorId,
                getString(R.string.AssistantSearchPatients),
                null
            )
            sendingShortcutRequest("Show my records")
        })
        apptShortcut?.setOnClickListener(View.OnClickListener {
            MyClinicGlobalClass.logUserActionEvent(
                ApiUrls.doctorId,
                getString(R.string.AssistantWhatsTodaySchedule),
                null
            )
            sendingShortcutRequest("What's today's schedule")
        })
        helpShortcut?.setOnClickListener(View.OnClickListener {
            MyClinicGlobalClass.logUserActionEvent(
                ApiUrls.doctorId,
                getString(R.string.AssistantKnowMore),
                null
            )
            val msgDto = AssistantTabListModel(1, "i need some help")
            (msgList as ArrayList<AssistantTabListModel>).add(msgDto)
            val newMsgPosition = (msgList as ArrayList<AssistantTabListModel>).size - 1
            // Notify recycler view insert one new data.
            assistantTabAdapter!!.notifyItemInserted(newMsgPosition)
            msgRecyclerView?.scrollToPosition(newMsgPosition)
            welcomeText()
        })
        cancelApptShortcut?.setOnClickListener(View.OnClickListener {
            MyClinicGlobalClass.logUserActionEvent(
                ApiUrls.doctorId,
                getString(R.string.AssistantCancelTodayAppts),
                null
            )
            sendingShortcutRequest("Cancel today's appointments")
        })
        val data = HashMap<String, Any>()
        data["Assistant_Msg"] = msgDto
        MyClinicGlobalClass.logUserActionEvent(
            ApiUrls.doctorId, getString(R.string.HomeAssistantScreen),
            data
        )
        return assistTabView
    }

    override fun onResult(aiResponse: AIResponse) {
        val result = aiResponse.result
        //        Log.d("AI", aiResponse.toString());
        if (isContextPresent) {
            try {
//                Log.d("Maitaining", "Context #######");
                aiQuery!!.remove("query")
                aiQuery!!.put("query", result.resolvedQuery)
                getAIQuery(aiQuery)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        } else {
            val aiRequest = AIRequest()
            aiRequest.setQuery(result.resolvedQuery)
            SendTextAIRequest().execute(aiRequest)
        }

        // Add a new sent message to the list.
        val msgContent = msgInputText!!.text.toString()
        val msgDto = AssistantTabListModel(1, result.resolvedQuery)
        msgList!!.add(msgDto)
        val newMsgPosition = msgList!!.size - 1
        // Notify recycler view insert one new data.
        assistantTabAdapter!!.notifyItemInserted(newMsgPosition)
        msgRecyclerView!!.scrollToPosition(newMsgPosition)
        msgInputText!!.hint = "Type a message "
    }

    override fun onError(error: AIError) {
//        Log.d("AI Error XXXXXXXXXXXXXX", error.toString());
        if (ActivityCompat.checkSelfPermission(
                requireActivity(),
                permissionsRequired[0]
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(permissionsRequired, PERMISSION_CALLBACK_CONSTANT)
        } else {
            msgInputText!!.hint = "Type a message"
            val myFabSrc = resources.getDrawable(R.drawable.ic_mic_assistance)
            val willBeWhite = myFabSrc.constantState!!.newDrawable()
            willBeWhite.mutate()
                .setColorFilter(resources.getColor(R.color.colorWhite), PorterDuff.Mode.SRC_IN)
            aiSendRequest!!.setImageDrawable(willBeWhite)
            //aiSendRequest.setImageDrawable(getResources().getDrawable(R.drawable.ic_mic_assistance));
            inListeningMode = false
        }
    }

    override fun onAudioLevel(level: Float) {}
    override fun onListeningStarted() {
//        Log.d("AI Listening Started", "*********************");
        msgInputText!!.setText("")
        msgInputText!!.hint = "Listening...."
    }

    override fun onListeningCanceled() {
//        Log.d("AI Listening Canceled", "*********************");
        msgInputText!!.hint = "Type a message"
        val myFabSrc = resources.getDrawable(R.drawable.ic_mic_assistance)
        val willBeWhite = myFabSrc.constantState!!.newDrawable()
        willBeWhite.mutate()
            .setColorFilter(resources.getColor(R.color.colorWhite), PorterDuff.Mode.SRC_IN)
        aiSendRequest!!.setImageDrawable(willBeWhite)
        //aiSendRequest.setImageDrawable(getResources().getDrawable(R.drawable.ic_mic_assistance));
    }

    override fun onListeningFinished() {
//        Log.d("AI Listening Finished", "*********************");
        msgInputText!!.hint = "Type a message"
        aiService!!.stopListening()
        val myFabSrc = resources.getDrawable(R.drawable.ic_mic_assistance)
        val willBeWhite = myFabSrc.constantState!!.newDrawable()
        willBeWhite.mutate()
            .setColorFilter(resources.getColor(R.color.colorWhite), PorterDuff.Mode.SRC_IN)
        aiSendRequest!!.setImageDrawable(willBeWhite)
        // aiSendRequest.setImageDrawable(getResources().getDrawable(R.drawable.ic_mic_assistance));
        inListeningMode = false
    }

    private fun getAIQuery(aiquery: JSONObject?) {
        val url = ApiUrls.aiQueryLink

        // prepare the Request
        apiGetPostMethodCalls!!.volleyApiRequestData(url,
            Request.Method.POST,
            aiquery,
            requireContext(),
            object : VolleyCallback {
                override fun onSuccess(result: String) {
                    try {
                        val response = JSONObject(result)
                        if (!response.getJSONObject("result").getJSONObject("fulfillment")
                                .getString("speech").equals("", ignoreCase = true)
                        ) {
                            if (response.getJSONObject("result").getJSONObject("parameters")
                                    .has("type") && response.getJSONObject("result")
                                    .getJSONObject("parameters").getString("type")
                                    .equals("delay_intimation", ignoreCase = true)
                            ) {
                                val model = AssistantTabListModel(6, "")
                                model.msgContent =
                                    response.getJSONObject("result").getJSONObject("fulfillment")
                                        .getString("speech")
                                msgList!!.add(model)
                                val newMsgPosition = msgList!!.size - 1
                                // Notify recycler view insert one new data.
                                assistantTabAdapter!!.notifyItemInserted(newMsgPosition)
                                // Scroll RecyclerView to the last message.
                                msgRecyclerView!!.scrollToPosition(newMsgPosition)
                            } else {
                                val model = AssistantTabListModel(2, "")
                                model.msgContent =
                                    response.getJSONObject("result").getJSONObject("fulfillment")
                                        .getString("speech")
                                msgList!!.add(model)
                                //                                assistantTabAdapter.notifyDataSetChanged();
                                val newMsgPosition = msgList!!.size - 1
                                // Notify recycler view insert one new data.
                                assistantTabAdapter!!.notifyItemInserted(newMsgPosition)
                                // Scroll RecyclerView to the last message.
                                msgRecyclerView!!.scrollToPosition(newMsgPosition)
                            }
                        }
                        if (response.getJSONObject("result").getJSONArray("contexts")
                                .length() == 0
                        ) {
                            sendQueryToServer(
                                response,
                                response.getJSONObject("result").getJSONObject("parameters")
                                    .getString("type")
                            )
                        } else {
                            aiQuery = aiquery
                            isContextPresent = true
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

    inner class SendTextAIRequest : AsyncTask<AIRequest?, Void?, AIResponse?>() {
        protected override fun doInBackground(vararg params: AIRequest?): AIResponse? {
            val request = params[0]
            try {
                return aiDataService!!.request(request)
            } catch (e: AIServiceException) {
                e.printStackTrace()
            }
            return null
        }
        override fun onPostExecute(aiResponse: AIResponse?) {
            if (aiResponse != null) {

//                Log.d("AI Text Query", "!!!!!!!!!!!!!!");
                val result = aiResponse.result
                //                aiQuery = new JSONObject();
                try {
                    aiQuery!!.put("lang", "en")
                    aiQuery!!.put("timezone", "Aisa/Calcutta")
                    aiQuery!!.put("sessionId", aiResponse.sessionId)
                    aiQuery!!.put("query", result.resolvedQuery)
                    getAIQuery(aiQuery)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_CALLBACK_CONSTANT) {
            //check if all permissions are granted
            var allgranted = false
            for (i in grantResults.indices) {
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    allgranted = true
                } else {
                    allgranted = false
                    break
                }
            }
            if (allgranted) {
                aiService!!.startListening()
                val myFabSrc = resources.getDrawable(R.drawable.ic_close)
                val willBeWhite = myFabSrc.constantState!!.newDrawable()
                willBeWhite.mutate()
                    .setColorFilter(resources.getColor(R.color.colorDanger), PorterDuff.Mode.SRC_IN)
                aiSendRequest!!.setImageDrawable(willBeWhite)
                inListeningMode = true
            } else {
                Toast.makeText(context, "You have to give a mic permission", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_PERMISSION_SETTING) {
            if (ActivityCompat.checkSelfPermission(
                    requireContext(),
                    permissionsRequired[0]
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                //Got Permission
//                Toast.makeText(getContext(), "You Have all the permission", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private fun sendQueryToServer(aiQuery: JSONObject, responseType: String) {
        apiRequests!!.postApptApiData(ApiUrls.aiRouter, aiQuery, activity, object : VolleyCallback {
            override fun onSuccess(result: String) {
                try {
                    if (responseType.equals("consult_get", ignoreCase = true)) {
                        val resObj = JSONObject(result)
                        if (resObj["speech"] is JSONObject) {
                            val data = resObj.getJSONObject("speech").getJSONArray("data")
                            if (data.length() > 0) {
                                for (i in 0 until data.length()) {
                                    val appt = data.getJSONObject(i)
                                    val model = AssistantTabListModel(3, "")
                                    if (appt.getJSONArray("usersd").length() != 0) {
                                        model.patientId =
                                            appt.getJSONArray("usersd").getJSONObject(0)
                                                .getInt("id")
                                        model.patientName =
                                            appt.getJSONArray("usersd").getJSONObject(0)
                                                .getString("fname")
                                        model.apptId = appt.getJSONObject("app").getInt("id")
                                        val dateTime = appt.getJSONObject("app")
                                            .getString("scheduled_start_time").split(" ".toRegex())
                                            .dropLastWhile { it.isEmpty() }
                                            .toTypedArray()
                                        model.apptDate = dateTime[0]
                                        model.apptTime = dateTime[1]
                                        model.apptType = appt.getJSONObject("app").getInt("mode")
                                        msgList!!.add(model)
                                    }
                                }

//                            assistantTabAdapter.notifyDataSetChanged();
                                val newMsgPosition = msgList!!.size - 1
                                // Notify recycler view insert one new data.
                                assistantTabAdapter!!.notifyItemInserted(newMsgPosition)
                                // Scroll RecyclerView to the last message.
                                msgRecyclerView!!.scrollToPosition(newMsgPosition)
                            } else {
                            }
                        } else {
                            val model = AssistantTabListModel(2, "")
                            model.msgContent = resObj.getString("speech")
                            msgList!!.add(model)
                            val newMsgPosition = msgList!!.size - 1
                            assistantTabAdapter!!.notifyItemInserted(newMsgPosition)
                            msgRecyclerView!!.scrollToPosition(newMsgPosition)
                        }
                    } else if (responseType.equals("d_records_get", ignoreCase = true)) {
                        val resObj = JSONObject(result)
                        if (resObj["speech"] is JSONArray) {
                            val data = resObj.getJSONArray("speech")
                            if (data.length() > 0) {
                                for (i in 0 until data.length()) {
                                    val patientObj = data.getJSONObject(i)
                                    val model = AssistantTabListModel(4, "")
                                    model.patientName = patientObj.getString("fname")
                                    model.patientId = patientObj.getInt("id")
                                    model.patientPhNo = patientObj.getString("phone")
                                    msgList!!.add(model)
                                }

//                            assistantTabAdapter.notifyDataSetChanged();
                                val newMsgPosition = msgList!!.size - 1
                                // Notify recycler view insert one new data.
                                assistantTabAdapter!!.notifyItemInserted(newMsgPosition)
                                // Scroll RecyclerView to the last message.
                                msgRecyclerView!!.scrollToPosition(newMsgPosition)
                            }
                        } else {
                            val model = AssistantTabListModel(2, "")
                            model.msgContent = resObj.getString("speech")
                            msgList!!.add(model)
                            val newMsgPosition = msgList!!.size - 1
                            assistantTabAdapter!!.notifyItemInserted(newMsgPosition)
                            msgRecyclerView!!.scrollToPosition(newMsgPosition)
                        }
                    } else if (responseType.equals("delay_intimation", ignoreCase = true)) {
                        val resObj = JSONObject(result)
                        val model = AssistantTabListModel(2, "")
                        model.msgContent = resObj.getString("speech")
                        msgList!!.add(model)
                        //                        assistantTabAdapter.notifyDataSetChanged();
                        val newMsgPosition = msgList!!.size - 1
                        // Notify recycler view insert one new data.
                        assistantTabAdapter!!.notifyItemInserted(newMsgPosition)
                        // Scroll RecyclerView to the last message.
                        msgRecyclerView!!.scrollToPosition(newMsgPosition)
                    } else if (responseType.equals("consult_cancel", ignoreCase = true)) {
                        val resObj = JSONObject(result)
                        if (resObj["speech"] is JSONObject) {
                            val data = resObj.getJSONObject("speech").getJSONArray("data")
                            val apptId = JSONArray()
                            for (i in 0 until data.length()) {
                                val dataObj = data.getJSONObject(i)
                                apptId.put(dataObj.getJSONObject("app").getString("id"))
                            }
                            val text = data.length()
                                .toString() + " " + activity!!.resources.getString(R.string.ai_cancel_appt)
                            val model = AssistantTabListModel(5, "")
                            model.msgContent = text
                            model.apptIds = apptId
                            msgList!!.add(model)
                            val newMsgPosition = msgList!!.size - 1
                            assistantTabAdapter!!.notifyItemInserted(newMsgPosition)
                            msgRecyclerView!!.scrollToPosition(newMsgPosition)
                        } else {
                            val model = AssistantTabListModel(2, "")
                            model.msgContent = resObj.getString("speech")
                            msgList!!.add(model)
                            val newMsgPosition = msgList!!.size - 1
                            assistantTabAdapter!!.notifyItemInserted(newMsgPosition)
                            msgRecyclerView!!.scrollToPosition(newMsgPosition)
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            override fun onError(err: String) {
                errorHandler(requireActivity(), err)
            }
        })
    }

    fun cancelAppointments(apptIds: String?) {
        val model = AssistantTabListModel(2, "")
        model.msgContent =
            "Processing your cancellation request. This may take upto 30-40 seconds..."
        msgList!!.add(model)
        val newMsgPosition = msgList!!.size - 1
        assistantTabAdapter!!.notifyItemInserted(newMsgPosition)
        msgRecyclerView!!.scrollToPosition(newMsgPosition)
        val apptObj = JSONObject()
        try {
            apptObj.put("appointment_ids", JSONArray(apptIds))
        } catch (e: Exception) {
            e.printStackTrace()
        }
        apiRequests!!.postApptApiData(
            ApiUrls.cancelAllAppts,
            apptObj,
            activity,
            object : VolleyCallback {
                override fun onSuccess(result: String) {
                    model.msgContent = "Appointments cancelled successfully"
                    msgList!!.add(model)
                    val newMsgPosition = msgList!!.size - 1
                    assistantTabAdapter!!.notifyItemInserted(newMsgPosition)
                    msgRecyclerView!!.scrollToPosition(newMsgPosition)
                }

                override fun onError(err: String) {
//                Log.d("Cancel Appt Error", err);
                    model.msgContent = "Error cancelling your appointments. Please try again later"
                    msgList!!.add(model)
                    val newMsgPosition = msgList!!.size - 1
                    assistantTabAdapter!!.notifyItemInserted(newMsgPosition)
                    msgRecyclerView!!.scrollToPosition(newMsgPosition)
                }
            })
    }

    private fun sendingShortcutRequest(shortCutReq: String) {
        val msgDto = AssistantTabListModel(1, shortCutReq)
        msgList!!.add(msgDto)
        val newMsgPosition = msgList!!.size - 1
        // Notify recycler view insert one new data.
        assistantTabAdapter!!.notifyItemInserted(newMsgPosition)
        msgRecyclerView!!.scrollToPosition(newMsgPosition)
        val aiRequest = AIRequest()
        aiRequest.setQuery(shortCutReq)
        SendTextAIRequest().execute(aiRequest)
    }

    private fun welcomeText() {
        apiRequests!!.getApptApiData(ApiUrls.aiWelcomeText, "", activity, object : VolleyCallback {
            override fun onSuccess(result: String) {
                try {
                    val resObj = JSONObject(result)
                    val msgDto = AssistantTabListModel(2, resObj.getString("response"))
                    msgList!!.add(msgDto)
                    val newMsgPosition = msgList!!.size - 1
                    // Notify recycler view insert one new data.
                    assistantTabAdapter!!.notifyItemInserted(newMsgPosition)
                    msgRecyclerView!!.scrollToPosition(newMsgPosition)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            override fun onError(err: String) {
                errorHandler(requireActivity(), err)
            }
        })
    }

    private fun delayIntimation(yesNo: String) {
        if (yesNo.equals("yes", ignoreCase = true)) {
            val model = AssistantTabListModel(2, "")
            model.msgContent = "Processing your request. This may take upto 30-40 seconds..."
            msgList!!.add(model)
            val newMsgPosition = msgList!!.size - 1
            assistantTabAdapter!!.notifyItemInserted(newMsgPosition)
            msgRecyclerView!!.scrollToPosition(newMsgPosition)
            try {
                aiQuery!!.remove("query")
                aiQuery!!.put("query", yesNo)
                getAIQuery(aiQuery)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        } else {
            val model = AssistantTabListModel(2, "")
            model.msgContent = "As you wish"
            msgList!!.add(model)
            val newMsgPosition = msgList!!.size - 1
            assistantTabAdapter!!.notifyItemInserted(newMsgPosition)
            msgRecyclerView!!.scrollToPosition(newMsgPosition)
        }
    }

    fun onCall(phoneNumber: String) {
        val intent = Intent(Intent.ACTION_DIAL)
        intent.data = Uri.parse("tel:$phoneNumber")
        startActivity(intent)
    }

    fun showGuide(section: Int) {
        when (section) {
            1 -> if (!appPreference!!.getBoolean("Assistant", false)) {
                GuideView.Builder(activity)
                    .setTitle("Say Hello")
                    .setContentText("Tap and speak to our Virtual Assistant for any help or perform any task")
                    .setTargetView(aiSendRequest)
                    .setDismissType(DismissType.outside)
                    .setGuideListener {
                        showGuide(2)
                        val editor = appPreference!!.edit()
                        editor.putBoolean("Assistant", true)
                        editor.commit()
                    }
                    .build()
                    .show()
            }
            2 -> GuideView.Builder(activity)
                .setTitle("Type Command")
                .setContentText("Type your command or for any help you need from our Virtual Assistant")
                .setTargetView(msgInputText)
                .setDismissType(DismissType.outside)
                .setGuideListener { showGuide(3) }
                .build()
                .show()
            3 -> GuideView.Builder(activity)
                .setTitle("Easy Shortcuts")
                .setContentText("On the go shortcuts to help you with important task faster")
                .setTargetView(aiShortcutsView)
                .setDismissType(DismissType.outside)
                .setGuideListener {
                    //                                showGuide(2);
                }
                .build()
                .show()
        }
    }

    companion object {
        private const val PERMISSION_CALLBACK_CONSTANT = 100
        private const val REQUEST_PERMISSION_SETTING = 101
    }
}